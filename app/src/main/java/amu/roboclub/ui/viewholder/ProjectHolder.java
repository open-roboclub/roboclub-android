package amu.roboclub.ui.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import amu.roboclub.R;
import butterknife.BindView;

public class ProjectHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.rootView)
    public CardView root;
    @BindView(R.id.title)
    public TextView title;
    @BindView(R.id.team)
    public TextView team;
    @BindView(R.id.projectImg)
    public ImageView projectImg;

    public ProjectHolder(View view) {
        super(view);
        root = (CardView) view.findViewById(R.id.rootView);
        title = (TextView) view.findViewById(R.id.title);
        team = (TextView) view.findViewById(R.id.team);
        projectImg = (ImageView) view.findViewById(R.id.projectImg);
    }
}