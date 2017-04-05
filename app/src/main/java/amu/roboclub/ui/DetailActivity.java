package amu.roboclub.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import amu.roboclub.R;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.backdrop)
    ImageView header;
    @BindView(R.id.avatarAreeb)
    ImageView avatarAreeb;
    @BindView(R.id.avatarDP)
    ImageView avatarDP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        fab.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/iamareebjamal/roboclub-amu")));
            } catch (ActivityNotFoundException anfe) {
                Toast.makeText(getApplicationContext(), R.string.browser_not_found, Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnLongClickListener(view -> {
                Toast.makeText(getApplicationContext(), R.string.source_code, Toast.LENGTH_SHORT).show();
                return true;
            }
        );

        loadImages();

    }

    private void loadImages() {
        Picasso.with(this).load(R.drawable.header).into(header);

        Drawable mPlaceholderDrawable = ResourcesCompat.getDrawable(
                getResources(),
                R.drawable.ic_avatar, null);

        Picasso.with(this)
                .load("http://www.amuroboclub.in/img/members/AreebJamal.jpg")
                .placeholder(mPlaceholderDrawable)
                .transform(new CircleTransform())
                .into(avatarAreeb);

        Picasso.with(this)
                .load("https://avatars3.githubusercontent.com/u/9443348?v=3&s=460")
                .placeholder(mPlaceholderDrawable)
                .transform(new CircleTransform())
                .into(avatarDP);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}
