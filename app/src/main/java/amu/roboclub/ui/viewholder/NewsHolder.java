package amu.roboclub.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import amu.roboclub.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.news)
    public TextView news;
    @BindView(R.id.date)
    public  TextView date;
    @BindView(R.id.link)
    public LinearLayout link;
    @BindView(R.id.divider)
    public LinearLayout divider;

    public NewsHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }
}
