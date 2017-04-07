package amu.roboclub.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import amu.roboclub.R;
import amu.roboclub.models.Profile;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    public static final String REFERENCE_KEY = "profile_reference";
    private String reference;

    private DatabaseReference profileReference;
    private ValueEventListener valueEventListener;

    @BindView(R.id.fab) FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        fab.hide();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(REFERENCE_KEY)) {
            setReferenceKey(getIntent().getStringExtra(REFERENCE_KEY));
            loadProfile();
        } else {
            Toast.makeText(this, R.string.no_profile_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void setReferenceKey(String referenceKey) {
        String postFix = "firebaseio.com";
        int index = referenceKey.indexOf(postFix) + postFix.length();

        String sanitized = referenceKey.substring(index);
        Log.d(REFERENCE_KEY, sanitized);
        reference = sanitized;
    }

    private void loadProfile() {
        profileReference = FirebaseDatabase.getInstance().getReference(reference);

        valueEventListener = profileReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        Log.d(REFERENCE_KEY, profile.toString());
                        showProfile(profile);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), R.string.load_profile_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProfile(Profile profile) {
        if(getSupportActionBar()!=null) getSupportActionBar().setTitle(profile.name);


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
