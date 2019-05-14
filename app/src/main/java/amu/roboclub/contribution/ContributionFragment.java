package amu.roboclub.contribution;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import amu.roboclub.R;
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
        FirebaseRecyclerOptions<Contribution> options = new FirebaseRecyclerOptions.Builder<Contribution>()
                .setQuery(contributionReference, Contribution.class)
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter contributionAdapter = new FirebaseRecyclerAdapter<Contribution, ContributionHolder>(options) {

            @NonNull
            @Override
            public ContributionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_contribution, parent, false);

                return new ContributionHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContributionHolder holder, int position, @NonNull Contribution contribution) {
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

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.setSpanCount(2);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
