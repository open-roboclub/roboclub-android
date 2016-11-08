package amu.roboclub.ui.viewholder;

import amu.roboclub.R;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProjectHolder extends RecyclerView.ViewHolder {

    public CardView root;
    public TextView title, team, about;
    public ImageView projectImg;
    public LinearLayout hiddenView;

    public ProjectHolder(View view) {
        super(view);
        root = (CardView) view.findViewById(R.id.rootView);
        title = (TextView) view.findViewById(R.id.title);
        team = (TextView) view.findViewById(R.id.team);
        about = (TextView) view.findViewById(R.id.aboutProject);
        projectImg = (ImageView) view.findViewById(R.id.projectImg);
        hiddenView = (LinearLayout) view.findViewById(R.id.hidden);
    }
}