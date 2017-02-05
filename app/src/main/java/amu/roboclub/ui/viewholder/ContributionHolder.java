package amu.roboclub.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amu.roboclub.R;

public class ContributionHolder extends RecyclerView.ViewHolder {

    public TextView contributor, purpose, remark, amount;

    public ContributionHolder(View view) {
        super(view);
        contributor = (TextView) view.findViewById(R.id.contributor);
        purpose = (TextView) view.findViewById(R.id.purpose);
        remark = (TextView) view.findViewById(R.id.remark);
        amount = (TextView) view.findViewById(R.id.amount);
    }
}