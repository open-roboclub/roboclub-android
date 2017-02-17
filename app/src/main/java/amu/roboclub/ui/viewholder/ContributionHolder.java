package amu.roboclub.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amu.roboclub.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContributionHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.contributor)
    public TextView contributor;
    @BindView(R.id.purpose)
    public TextView purpose;
    @BindView(R.id.remark)
    public TextView remark;
    @BindView(R.id.amount)
    public TextView amount;

    public ContributionHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }
}