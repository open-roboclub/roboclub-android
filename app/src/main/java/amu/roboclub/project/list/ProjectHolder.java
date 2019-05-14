package amu.roboclub.project.list;

import android.content.Context;
import android.content.Intent;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import amu.roboclub.R;
import amu.roboclub.project.Project;
import amu.roboclub.project.detail.ProjectDetailActivity;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.rootView)
    CardView root;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.team)
    TextView team;
    @BindView(R.id.projectImg)
    ImageView projectImg;

    public ProjectHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public static void setImage(Context context, ImageView imageView, String imgUrl) {
        if (imgUrl == null || imgUrl.contains("robo.jpg"))
            imageView.setImageDrawable(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_gear, null));
        else
            Picasso.get()
                    .load(imgUrl)
                    .transform(new CircleTransform())
                    .into(imageView);
    }

    public void setProject(Context context, Project project) {
        title.setText(project.name);

        if (project.team != null)
            team.setText(project.team);
        else
            team.setText(R.string.dividers);

        setImage(context, projectImg, project.image);

        root.setOnClickListener(view -> {
            if (project.description == null || project.description.equals(""))
                return;

            Intent intent = new Intent(context, ProjectDetailActivity.class);
            intent.putExtra("project", project);
            context.startActivity(intent);
        });
    }
}