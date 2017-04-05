package amu.roboclub.ui.fragments;


import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.firebase.ui.auth.ui.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import amu.roboclub.R;
import amu.roboclub.models.News;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class AdminFragment extends Fragment {

    private static final int RC_SIGN_IN = 2020;
    private static final String TAG = "AdminPanel";

    private View root;

    @BindView(R.id.sign_in) FloatingActionButton signIn;
    @BindView(R.id.state) TextView state;
    @BindView(R.id.account_info) NestedScrollView accountInfo;
    @BindView(R.id.account_progress) ProgressBar progressBar;
    @BindView(R.id.avatar) ImageView avatar;
    @BindView(R.id.name_edit) EditText name;
    @BindView(R.id.providers) RadioGroup providersLayout;

    @BindView(R.id.notification_card) CardView notificationCard;
    @BindView(R.id.title_edit) EditText title;
    @BindView(R.id.message_edit) EditText message;
    @BindView(R.id.link_edit) EditText link;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

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
                state.setText(R.string.signed_out_warning);
                notificationCard.setVisibility(View.GONE);
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

        Picasso.with(getContext()).cancelTag(TAG);

        Picasso.with(getContext())
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
                    public void onError() {
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

    @OnClick(R.id.send_btn)
    public void sendNotification() {
        News news = new News();
        news.title = title.getText().toString();
        news.notice = message.getText().toString();
        if(!TextUtils.isEmpty(link.getText().toString()))
            news.link = link.getText().toString();
        news.date = getDateString();
        news.timestamp = -System.currentTimeMillis();

        Log.d(TAG, news.toString());

        FirebaseDatabase.getInstance()
                .getReference("news")
                .push()
                .setValue(news, (databaseError, databaseReference) -> {

                    if(databaseError != null) {
                        showSnackbar(R.string.error_notification);
                        Log.d(TAG, databaseError.toString());
                    } else {
                        showSnackbar(R.string.notification_sent);
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
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
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
            if (resultCode == ResultCodes.OK) {
                showSnackbar(R.string.signed_in);
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);
        }
    }

    private void showSnackbar(@StringRes int id) {
        Snackbar.make(root, id, Snackbar.LENGTH_LONG).show();
    }

}
