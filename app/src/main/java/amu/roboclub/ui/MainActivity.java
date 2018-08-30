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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import amu.roboclub.R;
import amu.roboclub.admin.AdminFragment;
import amu.roboclub.contribution.ContributionFragment;
import amu.roboclub.home.HomeFragment;
import amu.roboclub.project.list.ProjectFragment;
import amu.roboclub.team.list.TeamFragment;
import amu.roboclub.ui.fragments.FeedbackDialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_KEY = "fragment";
    private static final String TITLE_KEY = "title";

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        if (amu.roboclub.BuildConfig.DEBUG)
            FirebaseMessaging.getInstance().subscribeToTopic("debug-news");
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private Class fragmentClass;
    private Fragment instanceFragment;
    private String title;

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
        setupDrawer();

        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            instanceFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_KEY);
            title = savedInstanceState.getString(TITLE_KEY);
            Log.d(TAG, String.format("onCreate: Restoring State : Fragment->%s Title->%s", instanceFragment, title));
        }

        if (instanceFragment == null)
            instanceFragment = HomeFragment.newInstance();

        if (title == null)
            title = getString(R.string.app_name);

        replaceFragment();
    }

    private void setupDrawer() {
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) { /* No Action */ }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) { /* No Action */ }

            @Override
            public void onDrawerStateChanged(int newState) { /* No Action */ }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (fragmentClass != null) {
                    try {
                        instanceFragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    replaceFragment();
                }
            }
        });
    }

    private void replaceFragment() {
        setTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_content, instanceFragment).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        getSupportFragmentManager().putFragment(state, FRAGMENT_KEY, instanceFragment);
        state.putString(TITLE_KEY, title);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);

        fragmentClass = null;

        title = item.getTitle().toString();

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_projects:
                fragmentClass = ProjectFragment.class;
                break;
            case R.id.contribution:
                fragmentClass = ContributionFragment.class;
                break;
            case R.id.nav_contact:
                fragmentClass = TeamFragment.class;
                break;
            case R.id.nav_admin:
                fragmentClass = AdminFragment.class;
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
        return true;
    }

    private void askFeedback() {
        BottomSheetDialogFragment myBottomSheet = FeedbackDialogFragment.newInstance();
        myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
    }
}
