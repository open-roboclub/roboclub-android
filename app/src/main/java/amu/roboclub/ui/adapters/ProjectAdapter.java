package amu.roboclub.ui.adapters;

import amu.roboclub.R;
import amu.roboclub.models.Project;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private Context context;
    private List<Project> projects;

    public ProjectAdapter(Context context, List<Project> projects) {
        this.context = context;
        this.projects = projects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Project project = projects.get(position);

        holder.title.setText(project.title);

        if (project.team != null)
            holder.team.setText(project.team);
        else
            holder.team.setVisibility(View.GONE);

        if (project.about != null)
            holder.about.setText(project.about);
        else
            holder.about.setVisibility(View.GONE);

        if (project.imgUrl != null)
            Picasso.with(context).load(project.imgUrl).into(holder.projectImg);
        else
            holder.projectImg.setVisibility(View.GONE);

        if (project.opened)
            holder.hiddenView.setVisibility(View.VISIBLE);
        else
            holder.hiddenView.setVisibility(View.GONE);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.hiddenView.getVisibility() == View.VISIBLE) {
                    holder.hiddenView.setVisibility(View.GONE);
                    project.opened = false;
                } else {
                    holder.hiddenView.setVisibility(View.VISIBLE);
                    project.opened = true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView root;
        public TextView title, team, about;
        public ImageView projectImg;
        public LinearLayout hiddenView;

        public ViewHolder(View view) {
            super(view);
            root = (CardView) view.findViewById(R.id.rootView);
            title = (TextView) view.findViewById(R.id.title);
            team = (TextView) view.findViewById(R.id.team);
            about = (TextView) view.findViewById(R.id.aboutProject);
            projectImg = (ImageView) view.findViewById(R.id.projectImg);
            hiddenView = (LinearLayout) view.findViewById(R.id.hidden);
        }
    }

}
