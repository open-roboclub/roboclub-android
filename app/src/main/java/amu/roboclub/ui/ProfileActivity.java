package amu.roboclub.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import amu.roboclub.R;
import amu.roboclub.models.Profile;
import amu.roboclub.models.ProfileInfo;
import amu.roboclub.ui.fragments.ProfileEditorFragment;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    public static final String REFERENCE_KEY = "profile_reference";
    private String reference;

    private DatabaseReference profileReference;
    private ValueEventListener valueEventListener;
    private FirebaseUser user;

    private ProfileEditorFragment profileEditorFragment = new ProfileEditorFragment();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.root) CoordinatorLayout rootLayout;
    @BindView(R.id.profileContainer) NestedScrollView profileContainer;
    @BindView(R.id.fab) FloatingActionButton fab;

    @BindView(R.id.avatar) ImageView avatar;
    @BindView(R.id.position) TextView position;
    @BindView(R.id.batch) TextView batch;
    @BindView(R.id.about) TextView about;
    @BindView(R.id.interestsCard) CardView interestsCard;
    @BindView(R.id.interests) TextView interests;
    @BindView(R.id.projectsCard) CardView projectsCard;
    @BindView(R.id.projects) TextView projects;
    @BindView(R.id.cvCard) CardView cvCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fab.hide();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(REFERENCE_KEY)) {
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
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        showProfile(profile);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Snackbar.make(rootLayout, R.string.load_profile_error, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProfile(Profile profile) {
        profileContainer.setVisibility(View.VISIBLE);
        if(getSupportActionBar()!=null) getSupportActionBar().setTitle(profile.name);

        profileEditorFragment.setProfile(profile);

        position.setText(profile.position);

        if(profile.thumbnail != null) {
            Picasso.with(this)
                    .load(profile.thumbnail)
                    .placeholder(VectorDrawableCompat.create(getResources(), R.drawable.ic_avatar, null))
                    .transform(new CircleTransform())
                    .into(avatar);
        }

        ProfileInfo profileInfo = profile.profile_info;
        batch.setText(profileInfo.batch);
        about.setText(profileInfo.about);

        if(profileInfo.cv != null) {
            cvCard.setVisibility(View.VISIBLE);
            cvCard.setOnClickListener(view -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(profileInfo.cv)));
                } catch (ActivityNotFoundException ane) {
                    Snackbar.make(rootLayout, R.string.browser_not_found, Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        if(profileInfo.interests != null && !profileInfo.interests.isEmpty()) {
            interestsCard.setVisibility(View.VISIBLE);
            StringBuilder stringBuilder = new StringBuilder();
            for (String interest: profileInfo.interests) {
                stringBuilder.append("\u25CF");
                stringBuilder.append(interest);
                stringBuilder.append("\n");
            }

            interests.setText(stringBuilder.toString());
        }

        if(profileInfo.projects != null && !profileInfo.projects.isEmpty()) {
            projectsCard.setVisibility(View.VISIBLE);
            StringBuilder stringBuilder = new StringBuilder();
            for(HashMap project : profileInfo.projects) {
                stringBuilder.append("\u25CF");
                stringBuilder.append(project.get("name"));
                stringBuilder.append("\n");
            }

            projects.setText(stringBuilder.toString());
        }

        configureUser(profile, true);

        if(user ==  null) return;

        FirebaseDatabase.getInstance()
                .getReference("admins/" + user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null) {
                            // User is Admin. Show Profile Editor
                            configureUser(profile, true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // No action
                    }
                });
    }

    private void configureUser(Profile profile, boolean override) {
        if(!override && (user == null || profile.uid == null || !user.getUid().equals(profile.uid)))
            return;

        fab.show();

        if(/* TODO: Remove user != null */ user != null && user.getDisplayName() != null && !TextUtils.isEmpty(user.getDisplayName())){
            String welcome = String.format(getString(R.string.welcome_message), user.getDisplayName());
            Snackbar.make(rootLayout, welcome, Snackbar.LENGTH_SHORT).show();
        }

        fab.setOnClickListener(view -> {
            if(profileEditorFragment.isDetached())
                profileEditorFragment = new ProfileEditorFragment();
            profileEditorFragment.setProfile(profile);
            profileEditorFragment.show(getSupportFragmentManager(), profileEditorFragment.getTag());
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

        if(profileReference != null) {
            profileReference.removeEventListener(valueEventListener);
        }
    }
}
