package amu.roboclub.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import amu.roboclub.R;
import amu.roboclub.models.Doc;
import amu.roboclub.models.Project;
import amu.roboclub.ui.fragments.ProjectFragment;

public class ProjectDetailActivity extends AppCompatActivity {

    private Project project;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.gallery_list)
    LinearLayout imageList;

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.team)
    TextView team;
    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.youtube_card)
    CardView youtubeCard;
    @BindView(R.id.youtube_thumb)
    ImageView youtube;

    @BindView(R.id.documents_card)
    CardView documentsCard;
    @BindView(R.id.documents)
    TextView documents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        project = (Project) getIntent().getSerializableExtra("project");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(project.name);
        }

        populateUI();
    }

    private static float toPx(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private static String getSmallImage(String url) {
        return url.replaceFirst("upload/", "upload/c_thumb,w_150,h_150/");
    }

    private void showImages() {
        if (project.images == null || project.images.isEmpty())
            return;

        for (final String imageUrl : project.images) {
            final ImageView im = new ImageView(this);

            final int pad = (int) toPx(5, this);
            im.setPadding(pad, pad, pad, pad);
            im.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            (int) toPx(100, this),
                            (int) toPx(100, this)
                    )
            );

            final Context context = this;

            im.setOnClickListener(view -> {
                Toast.makeText(context, R.string.loading, Toast.LENGTH_SHORT).show();

                ImageView bigImage = new ImageView(getApplicationContext());
                bigImage.setPadding(pad, pad, pad, pad);
                Picasso.with(getApplicationContext())
                        .load(imageUrl)
                        .into(bigImage);

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setView(bigImage);
                alert.show();
            });

            Picasso.with(this)
                    .load(getSmallImage(imageUrl))
                    .into(im, new Callback() {
                        @Override
                        public void onSuccess() {
                            imageList.addView(im);
                            findViewById(R.id.gallery_card).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }

    }

    private void showYoutube() {
        if (project.youtube == null)
            return;


        Picasso.with(this)
                .load("https://img.youtube.com/vi/" + project.youtube + "/hqdefault.jpg")
                .into(youtube, new Callback() {
                    @Override
                    public void onSuccess() {
                        youtubeCard.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        // No Action
                    }
                });

        youtube.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/" + project.youtube))));

    }

    private void showDocuments() {
        if(project.docs == null || project.docs.isEmpty())
            return;

        documents.setMovementMethod(LinkMovementMethod.getInstance());

        documentsCard.setVisibility(View.VISIBLE);

        StringBuilder sb = new StringBuilder();

        for (Doc doc : project.docs) {
            sb.append("<a href=\"");
            sb.append(doc.url);
            sb.append("\" >");
            sb.append(doc.name);
            sb.append("</a><br>");
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            documents.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY));
        else
            documents.setText(Html.fromHtml(sb.toString()));
    }

    private void populateUI() {
        if (project == null)
            return;

        ProjectFragment.setImage(this, image, project.image);

        if (project.team != null)
            team.setText(project.team);
        else
            team.setText("---");

        if (project.description != null)
            description.setText(project.description);

        showYoutube();
        showImages();
        showDocuments();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                // No Action
        }
        return super.onOptionsItemSelected(item);
    }

}
