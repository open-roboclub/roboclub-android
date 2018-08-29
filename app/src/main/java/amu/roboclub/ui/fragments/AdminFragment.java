package amu.roboclub.ui.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import amu.roboclub.BuildConfig;
import amu.roboclub.R;
import amu.roboclub.models.News;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static android.app.Activity.RESULT_OK;

public class AdminFragment extends Fragment {

    private static final int RC_SIGN_IN = 2020;
    private static final String TAG = "AdminPanel";

    private View root;

    @BindView(R.id.sign_in) FloatingActionButton signIn;
    @BindView(R.id.state) LinearLayout state;
    @BindView(R.id.account_info) NestedScrollView accountInfo;
    @BindView(R.id.account_progress) ProgressBar progressBar;
    @BindView(R.id.avatar) ImageView avatar;
    @BindView(R.id.name_edit) EditText name;
    @BindView(R.id.providers) RadioGroup providersLayout;

    @BindView(R.id.notification_card) CardView notificationCard;
    @BindView(R.id.title_edit) EditText title;
    @BindView(R.id.message_edit) EditText message;
    @BindView(R.id.link_edit) EditText link;

    @BindView(R.id.news_selector) RadioGroup newsSelector;

    @BindView(R.id.news_card) CardView newsCard;
    @BindView(R.id.title_modify) EditText titleModify;
    @BindView(R.id.message_modify) EditText messageModify;
    @BindView(R.id.news_reference) EditText newsReferenceText;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private static final String newsReference = BuildConfig.DEBUG?"news-debug/":"news/";

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_admin, container, false);

        ButterKnife.bind(this, root);

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.INVISIBLE);
        accountInfo.setVisibility(View.GONE);

        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            providersLayout.removeAllViews();

            if (user != null) {
                // User is signed in
                signIn.setVisibility(View.GONE);

                accountInfo.setVisibility(View.VISIBLE);
                state.setVisibility(View.GONE);

                showAccountInfo(user);
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                signIn.setVisibility(View.VISIBLE);

                accountInfo.setVisibility(View.GONE);
                state.setVisibility(View.VISIBLE);
                notificationCard.setVisibility(View.GONE);
                newsCard.setVisibility(View.GONE);
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };

        return root;
    }

    private void showAccountInfo(FirebaseUser user) {
        name.setText(user.getDisplayName());

        showImageAvatar(user.getPhotoUrl());

        showProviders(user);

        showNotificationPanel(user.getUid());
    }

    private void showImageAvatar(Uri uri) {
        if(uri == null) {
            Toast.makeText(getContext(), R.string.no_image, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Picasso.get().cancelTag(TAG);

        Picasso.get()
                .load(uri)
                .placeholder(VectorDrawableCompat.create(getResources(), R.drawable.ic_avatar, null))
                .transform(new CircleTransform())
                .tag(TAG)
                .into(avatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception ex) {
                        progressBar.setVisibility(View.INVISIBLE);
                        avatar.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_avatar, null));
                    }
                });
    }

    private RadioButton createRadio(String text) {
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setText(text);
        return radioButton;
    }

    private void showProviders(FirebaseUser user) {
        List<? extends UserInfo> providers = user.getProviderData();

        for (int i = 0; i < providers.size(); i++) {
            UserInfo userInfo = providers.get(i);

            RadioButton radioButton = createRadio(userInfo.getProviderId());
            radioButton.setId(i);
            if(userInfo.getPhotoUrl()!= null && userInfo.getPhotoUrl().equals(user.getPhotoUrl())) {
                radioButton.setChecked(true);
            }
            providersLayout.addView(radioButton);
        }

        providersLayout.setOnCheckedChangeListener((group, checkedId) -> {
            Uri uri = providers.get(checkedId).getPhotoUrl();
            showImageAvatar(uri);
        });
    }

    @OnClick(R.id.save_btn)
    public void saveUser() {
        FirebaseUser user = auth.getCurrentUser();

        if(user == null)
            return;

        UserProfileChangeRequest.Builder userProfileChangeRequest = new UserProfileChangeRequest.Builder();

        String userName = name.getText().toString();
        if(!userName.equals(auth.getCurrentUser().getDisplayName())) {
            userProfileChangeRequest.setDisplayName(userName);
        }

        int index = providersLayout.getCheckedRadioButtonId();
        List<? extends UserInfo> providerData = user.getProviderData();
        if(index >= 0 && index < providerData.size()) {
            Uri uri = providerData.get(index).getPhotoUrl();
            if(uri != null && !uri.equals(user.getPhotoUrl())) {
                userProfileChangeRequest.setPhotoUri(uri);
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        user.updateProfile(userProfileChangeRequest.build())
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        showSnackbar(R.string.profile_updated);
                    }
                });
    }

    private void showNotificationPanel(String uid) {
        FirebaseDatabase.getInstance()
                .getReference("admins/"+uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null) {
                            // Our user is an admin
                            showSnackbar(R.string.admin_detected);
                            notificationCard.setVisibility(View.VISIBLE);
                        } else {
                            // User is not admin
                            Log.d(TAG, "Not admin");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // User is not admin
                        Log.d(TAG, "Not admin");
                    }
                });
    }

    private String getDateString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());

        Date date = new Date();

        return simpleDateFormat.format(date);
    }

    private String getNotificationStatus() {
        switch (newsSelector.getCheckedRadioButtonId()) {
            case R.id.news_only:
                return "only";
            case R.id.news_yes:
                return "yes";
        }

        return "no";
    }

    @OnClick(R.id.send_btn)
    public void sendNotification() {
        News news = new News();
        news.title = title.getText().toString();
        news.notice = message.getText().toString();
        if(!TextUtils.isEmpty(link.getText().toString()))
            news.link = link.getText().toString();
        news.date = getDateString();
        news.timestamp = -System.currentTimeMillis();
        news.notification = getNotificationStatus();

        Log.d(TAG, "Sending notification : " + news.toString());

        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance()
                .getReference(newsReference)
                .push()
                .setValue(news, (databaseError, databaseReference) -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    if(databaseError != null) {
                        showSnackbar(R.string.error_notification);
                        Log.d(TAG, databaseError.toString());
                    } else {
                        showSnackbar(R.string.notification_sent);
                        showNewsCard(databaseReference.getKey());
                    }

                });
    }

    private void showNewsCard(String key) {
        newsCard.setVisibility(View.VISIBLE);

        newsReferenceText.setText(key);

        FirebaseDatabase.getInstance()
                .getReference(newsReference+key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        News news = dataSnapshot.getValue(News.class);
                        Log.d(TAG, "Received News : " + news.toString());
                        titleModify.setText(news.title);
                        messageModify.setText(news.notice);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showSnackbar(R.string.error_loading_news);
                        newsCard.setVisibility(View.GONE);
                    }
                });
    }

    @OnClick(R.id.update)
    public void updateNews() {
        Map<String, Object> newsUpdate = new HashMap<>();

        String title = titleModify.getText().toString();
        String message = messageModify.getText().toString();

        if(!TextUtils.isEmpty(title))
            newsUpdate.put("title", title);

        if(!TextUtils.isEmpty(message))
            newsUpdate.put("notice", message);

        if(!newsUpdate.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance()
                    .getReference(newsReference+newsReferenceText.getText().toString())
                    .updateChildren(newsUpdate, (databaseError, databaseReference) -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(databaseError != null) {
                            showSnackbar(R.string.error_updating_news);
                            Log.d(TAG, "Update Error : " + databaseError.toString());
                        } else {
                            showSnackbar(R.string.news_update_success);
                        }
                    });
        }
    }

    @OnClick(R.id.delete)
    public void deleteNews() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance()
                .getReference(newsReference+newsReferenceText.getText().toString())
                .removeValue((databaseError, databaseReference) -> {
                    progressBar.setVisibility(View.GONE);
                    if(databaseError != null) {
                        showSnackbar(R.string.news_delete_error);
                        Log.d(TAG, "Delete Error : " + databaseError.toString());
                    } else {
                        showSnackbar(R.string.news_delete_success);
                        newsCard.setVisibility(View.GONE);
                    }

                });
    }

    @Override
    public void onStart() {
        super.onStart();

        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        auth.removeAuthStateListener(authListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_admin, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                AuthUI.getInstance().signOut(getActivity())
                        .addOnCompleteListener(task -> showSnackbar(R.string.signed_out));
                break;
            default:
                // No action
        }

        return false;
    }

    @OnClick(R.id.sign_in)
    public void signIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build()))
                        .setTheme(R.style.PurpleTheme)
                        .setLogo(R.drawable.logo)
                        .build(),
                RC_SIGN_IN);
    }

    @OnLongClick(R.id.sign_in)
    public boolean signinToast() {
        Toast.makeText(getContext(), R.string.sign_in, Toast.LENGTH_SHORT).show();
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                showSnackbar(R.string.signed_in);
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                FirebaseUiException error = response.getError();
                if (error !=  null) {
                    if (error.getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showSnackbar(R.string.no_internet_connection);
                        return;
                    }

                    if (error.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackbar(R.string.unknown_error);
                        return;
                    }
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);
        }
    }

    private void showSnackbar(@StringRes int id) {
        Snackbar.make(root, id, Snackbar.LENGTH_LONG).show();
    }

}
