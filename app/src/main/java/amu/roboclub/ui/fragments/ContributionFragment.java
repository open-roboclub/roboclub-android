package amu.roboclub.ui.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import amu.roboclub.R;
import amu.roboclub.models.Contribution;
import amu.roboclub.ui.viewholder.ContributionHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContributionFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public static ContributionFragment newInstance() {
        return new ContributionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contribution, container, false);

        ButterKnife.bind(this, root);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        final Snackbar snackbar = Snackbar.make(recyclerView, R.string.loading_contributors, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        DatabaseReference contributionReference = FirebaseDatabase.getInstance().getReference("contribution");
        FirebaseRecyclerAdapter contributionAdapter = new FirebaseRecyclerAdapter<Contribution, ContributionHolder>
                (Contribution.class, R.layout.item_contribution, ContributionHolder.class, contributionReference) {

            @Override
            protected void populateViewHolder(ContributionHolder holder, Contribution contribution, int position) {
                if (snackbar.isShown())
                    snackbar.dismiss();
                holder.setContribution(contribution);
            }

            @Override
            public Contribution getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
            }
        };

        recyclerView.setAdapter(contributionAdapter);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.setSpanCount(2);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
