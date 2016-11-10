package amu.roboclub.ui.fragments;

import amu.roboclub.R;
import amu.roboclub.models.Contribution;
import amu.roboclub.ui.viewholder.ContributionHolder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContributionFragment extends Fragment {
    public ContributionFragment() {
        // Required empty public constructor
    }

    public static ContributionFragment newInstance() {
        ContributionFragment fragment = new ContributionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contribution, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        DatabaseReference contributionReference = FirebaseDatabase.getInstance().getReference("contribution");
        FirebaseRecyclerAdapter contributionAdapter = new FirebaseRecyclerAdapter<Contribution, ContributionHolder>(Contribution.class, R.layout.item_contribution, ContributionHolder.class, contributionReference){

            @Override
            protected void populateViewHolder(ContributionHolder holder, Contribution contribution, int position) {
                holder.contributor.setText(contribution.contributor);
                holder.purpose.setText(contribution.purpose);
                holder.remark.setText(contribution.remark);
                holder.amount.setText(contribution.amount);
            }
        };

        recyclerView.setAdapter(contributionAdapter);

        return root;
    }
}
