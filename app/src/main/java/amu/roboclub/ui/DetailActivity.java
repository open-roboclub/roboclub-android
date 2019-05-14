package amu.roboclub.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RoboClub-core/roboclub-amu")));
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
        Picasso.get().load(R.drawable.header).into(header);

        Drawable placeholder = ResourcesCompat.getDrawable(
                getResources(),
                R.drawable.ic_avatar, null);

        Picasso.get()
                .load("https://avatars1.githubusercontent.com/u/3874064?v=3&s=460")
                .placeholder(placeholder)
                .transform(new CircleTransform())
                .into(avatarAreeb);

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
