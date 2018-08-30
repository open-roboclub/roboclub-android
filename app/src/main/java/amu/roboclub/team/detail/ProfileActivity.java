package amu.roboclub.team.detail;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import amu.roboclub.R;
import amu.roboclub.team.Profile;
import amu.roboclub.team.ProfileInfo;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    public static final String REFERENCE_KEY = "profile_reference";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    CoordinatorLayout rootLayout;
    @BindView(R.id.profileContainer)
    NestedScrollView profileContainer;
    @BindView(R.id.profileInfo)
    LinearLayout profileInfoContainer;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.position)
    TextView position;
    @BindView(R.id.batch)
    TextView batch;
    @BindView(R.id.about_card)
    CardView aboutCard;
    @BindView(R.id.about)
    TextView about;
    @BindView(R.id.interestsCard)
    CardView interestsCard;
    @BindView(R.id.interests)
    TextView interests;
    @BindView(R.id.projectsCard)
    CardView projectsCard;
    @BindView(R.id.projects)
    TextView projects;
    @BindView(R.id.cvCard)
    CardView cvCard;

    private String reference;
    private DatabaseReference profileReference;
    private ValueEventListener valueEventListener;
    private FirebaseUser user;
    private boolean greeted = false;
    private ProfileEditorFragment profileEditorFragment = new ProfileEditorFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fab.hide();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra(REFERENCE_KEY)) {
            setReferenceKey(getIntent().getStringExtra(REFERENCE_KEY));
            loadProfile();
        } else {
            Snackbar.make(rootLayout, R.string.no_profile_found, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setReferenceKey(String referenceKey) {
        String postFix = "firebaseio.com";
        int index = referenceKey.indexOf(postFix) + postFix.length();

        reference = referenceKey.substring(index);
    }

    private void loadProfile() {
        profileReference = FirebaseDatabase.getInstance().getReference(reference);

        valueEventListener = profileReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        showProfile(profile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(rootLayout, R.string.load_profile_error, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean showAndSetText(TextView textView, @Nullable String text) {
        if (!TextUtils.isEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
            return true;
        } else {
            textView.setVisibility(View.GONE);
        }

        return false;
    }

    private void showProfile(Profile profile) {
        profileContainer.setVisibility(View.VISIBLE);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(profile.name);

        profileEditorFragment.setProfile(profile);

        position.setText(profile.position);

        if (profile.thumbnail != null) {
            Picasso.get()
                    .load(profile.thumbnail)
                    .placeholder(VectorDrawableCompat.create(getResources(), R.drawable.ic_avatar, null))
                    .transform(new CircleTransform())
                    .into(avatar);
        }

        ProfileInfo profileInfo = profile.profile_info;

        if (profileInfo != null) {
            profileInfoContainer.setVisibility(View.VISIBLE);

            showAndSetText(batch, profileInfo.batch);

            if (showAndSetText(about, profileInfo.about)) {
                aboutCard.setVisibility(View.VISIBLE);
            }

            if (profileInfo.cv != null) {
                cvCard.setVisibility(View.VISIBLE);
                cvCard.setOnClickListener(view -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(profileInfo.cv)));
                    } catch (ActivityNotFoundException ane) {
                        Snackbar.make(rootLayout, R.string.browser_not_found, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            if (profileInfo.interests != null && !profileInfo.interests.isEmpty()) {
                interestsCard.setVisibility(View.VISIBLE);
                StringBuilder stringBuilder = new StringBuilder();
                for (String interest : profileInfo.interests) {
                    stringBuilder.append("\u25CF");
                    stringBuilder.append(interest);
                    stringBuilder.append("\n");
                }

                interests.setText(stringBuilder.toString());
            }

            if (profileInfo.projects != null && !profileInfo.projects.isEmpty()) {
                projectsCard.setVisibility(View.VISIBLE);
                StringBuilder stringBuilder = new StringBuilder();
                for (HashMap project : profileInfo.projects) {
                    stringBuilder.append("\u25CF");
                    stringBuilder.append(project.get("name"));
                    stringBuilder.append("\n");
                }

                projects.setText(stringBuilder.toString());
            }
        } else {
            profileInfoContainer.setVisibility(View.GONE);
        }

        configureUser(profile);

        if (user == null) return;

        FirebaseDatabase.getInstance()
                .getReference("admins/" + user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            // User is Admin. Show Profile Editor
                            profile.adminOverride = true;
                            configureUser(profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // No action
                    }
                });
    }

    private void configureUser(Profile profile) {
        if (!profile.adminOverride && (user == null || profile.uid == null || !user.getUid().equals(profile.uid)))
            return;

        fab.show();

        if (!greeted && user.getDisplayName() != null && !TextUtils.isEmpty(user.getDisplayName())) {
            greeted = true;
            String welcome = String.format(getString(R.string.welcome_message), user.getDisplayName());
            Snackbar.make(rootLayout, welcome, Snackbar.LENGTH_SHORT).show();
        }

        fab.setOnClickListener(view -> {
            if (profileEditorFragment.isDetached())
                profileEditorFragment = new ProfileEditorFragment();
            profileEditorFragment.setProfile(profile);
            profileEditorFragment.show(getSupportFragmentManager(), profileEditorFragment.getTag());

            profileEditorFragment.setOnProfileChangeListener(profileChanges -> {

                // Save old thumbnail. Just in case
                if (profileChanges.containsKey("thumbnail") && profile.thumbnail != null) {
                    final String thumbnail = profile.thumbnail;
                    FirebaseDatabase.getInstance()
                            .getReference(reference + "/old_avatars/")
                            .push()
                            .setValue(thumbnail, (databaseError, databaseReference) -> {
                                if (databaseError != null) {
                                    Log.d("Profile ", "configureUser: " + databaseError.toString());
                                }
                            });
                }

                updateProfile(profileChanges);
            });
        });

    }

    private void updateProfile(Map<String, Object> objectMap) {
        FirebaseDatabase.getInstance()
                .getReference(reference)
                .updateChildren(objectMap, (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        Log.d("UpdateProfile", "updateProfile: " + databaseError);
                        Snackbar.make(rootLayout, R.string.profile_update_failed, BaseTransientBottomBar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(rootLayout, R.string.profile_updated, BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                // No action
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (profileReference != null) {
            profileReference.removeEventListener(valueEventListener);
        }
    }
}
