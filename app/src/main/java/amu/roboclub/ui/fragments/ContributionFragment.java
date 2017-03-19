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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import amu.roboclub.R;
import amu.roboclub.models.Contribution;
import amu.roboclub.ui.viewholder.ContributionHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContributionFragment extends Fragment {

    public static ContributionFragment newInstance() {
        return new ContributionFragment();
    }

    int count;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private DatabaseReference contributionReference;
    private ChildEventListener childEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contribution, container, false);

        count = 0;

        ButterKnife.bind(this, root);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        gridLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        final Snackbar snackbar = Snackbar.make(recyclerView, R.string.loading_contributors, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        contributionReference = FirebaseDatabase.getInstance().getReference("contribution");
        FirebaseRecyclerAdapter contributionAdapter = new FirebaseRecyclerAdapter<Contribution, ContributionHolder>
                (Contribution.class, R.layout.item_contribution, ContributionHolder.class, contributionReference) {

            @Override
            protected void populateViewHolder(ContributionHolder holder, Contribution contribution, int position) {
                if (snackbar.isShown())
                    snackbar.dismiss();
                holder.contributor.setText(contribution.contributor);
                holder.purpose.setText(contribution.purpose);
                holder.remark.setText(contribution.remark);
                holder.amount.setText(contribution.amount);
            }
        };

        recyclerView.setAdapter(contributionAdapter);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.setSpanCount(2);
        }

        childEventListener = contributionReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                gridLayoutManager.smoothScrollToPosition(recyclerView, null, count++);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                count--;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        contributionReference.removeEventListener(childEventListener);
    }
}
