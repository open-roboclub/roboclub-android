package amu.roboclub.contribution;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amu.roboclub.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContributionHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.contributor)
    TextView contributor;
    @BindView(R.id.purpose)
    TextView purpose;
    @BindView(R.id.remark)
    TextView remark;
    @BindView(R.id.amount)
    TextView amount;

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