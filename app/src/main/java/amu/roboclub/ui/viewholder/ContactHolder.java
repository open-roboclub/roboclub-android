package amu.roboclub.ui.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import amu.roboclub.R;

public class ContactHolder extends RecyclerView.ViewHolder {

    public CardView root;
    public TextView name, position;
    public ImageView avatar;

    public ContactHolder(View view) {
        super(view);
        root = (CardView) view.findViewById(R.id.rootView);
        name = (TextView) view.findViewById(R.id.title);
        position = (TextView) view.findViewById(R.id.position);
        avatar = (ImageView) view.findViewById(R.id.avatar);
    }
}