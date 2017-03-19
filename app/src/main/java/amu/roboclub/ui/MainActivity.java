package amu.roboclub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import amu.roboclub.R;
import amu.roboclub.ui.fragments.ContactFragment;
import amu.roboclub.ui.fragments.ContributionFragment;
import amu.roboclub.ui.fragments.FeedbackDialogFragment;
import amu.roboclub.ui.fragments.HomeFragment;
import amu.roboclub.ui.fragments.ProjectFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    private Fragment instanceFragment;
    private int id = R.id.nav_home;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState != null && savedInstanceState.getInt("id") != 0)
            id = savedInstanceState.getInt("id");

        createFragmentFromId(id, getString(R.string.app_name));
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("id", id);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        id = savedInstanceState.getInt("id");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void createFragmentFromId(int id, String title) {
        Class fragmentClass = null;

        switch (id) {
            case R.id.nav_home:
                this.id = id;
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_projects:
                this.id = id;
                fragmentClass = ProjectFragment.class;
                break;
            case R.id.contribution:
                this.id = id;
                fragmentClass = ContributionFragment.class;
                break;
            case R.id.nav_contact:
                this.id = id;
                fragmentClass = ContactFragment.class;
                break;
            case R.id.nav_feedback:
                askFeedback();
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, DetailActivity.class));
                break;
            default:
                // Do nothing
        }

        if (fragmentClass != null) {
            try {
                instanceFragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            setTitle(title);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_content, instanceFragment).commit();
        }

        if(drawer.isDrawerOpen(GravityCompat.START)) drawer.postDelayed(() -> drawer.closeDrawer(GravityCompat.START), 200);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        createFragmentFromId(item.getItemId(), (String) item.getTitle());
        return true;
    }

    private void askFeedback() {
        BottomSheetDialogFragment myBottomSheet = FeedbackDialogFragment.newInstance();
        myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
    }
}
