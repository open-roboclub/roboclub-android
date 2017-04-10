package amu.roboclub.ui.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import amu.roboclub.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.rootView)
    public CardView root;
    @BindView(R.id.title)
    public TextView name;
    @BindView(R.id.position)
    public TextView position;
    @BindView(R.id.avatar)
    public ImageView avatar;
    @BindView(R.id.contactPanel)
    public LinearLayout contactPanel;
    @BindView(R.id.showProfile)
    public TextView showProfile;
    @BindView(R.id.editable)
    public TextView editable;

    public ProfileHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }
}