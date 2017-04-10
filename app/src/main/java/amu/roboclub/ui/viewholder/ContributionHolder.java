package amu.roboclub.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amu.roboclub.R;
import amu.roboclub.models.Contribution;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContributionHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.contributor) TextView contributor;
    @BindView(R.id.purpose) TextView purpose;
    @BindView(R.id.remark) TextView remark;
    @BindView(R.id.amount) TextView amount;

    public ContributionHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }

    public void setContribution(Contribution contribution) {
        contributor.setText(contribution.contributor);
        purpose.setText(contribution.purpose);
        remark.setText(contribution.remark);
        amount.setText(contribution.amount);
    }
}