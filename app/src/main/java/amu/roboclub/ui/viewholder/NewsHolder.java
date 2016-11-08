package amu.roboclub.ui.viewholder;

import amu.roboclub.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewsHolder extends RecyclerView.ViewHolder {

    public TextView news, date;
    public LinearLayout link, divider;

    public NewsHolder(View view) {
        super(view);
        news = (TextView) view.findViewById(R.id.news);
        date = (TextView) view.findViewById(R.id.date);
        link = (LinearLayout) view.findViewById(R.id.link);
        divider = (LinearLayout) view.findViewById(R.id.divider);
    }
}
