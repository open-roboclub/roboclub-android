package amu.roboclub.team.list;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import amu.roboclub.R;
import amu.roboclub.team.Profile;
import butterknife.BindView;
import butterknife.ButterKnife;


public class TeamFragment extends Fragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Snackbar snackbar;
    private FirebaseRecyclerAdapter teamAdapter;

    public static TeamFragment newInstance() {
        return new TeamFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_team, container, false);

        ButterKnife.bind(this, root);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        snackbar = Snackbar.make(recyclerView, R.string.loading_members, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ProfileHolder.setUser(user);

        Query teamReference = FirebaseDatabase.getInstance().getReference("team/current/members").orderByChild("rank");
        FirebaseRecyclerOptions<Profile> options = new FirebaseRecyclerOptions.Builder<Profile>()
                .setQuery(teamReference, Profile.class)
                .build();
        teamAdapter = new FirebaseRecyclerAdapter<Profile, ProfileHolder>(options) {

            @NonNull
            @Override
            public ProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_contact, parent, false);
                return new ProfileHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProfileHolder holder, int position, @NonNull Profile profile) {
                if (snackbar.isShown())
                    snackbar.dismiss();

                holder.setProfile(profile, getRef(position).toString());
            }

        };

        recyclerView.setAdapter(teamAdapter);
        teamAdapter.startListening(); // TODO: Move to LifecycleOwner or AAC

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.setSpanCount(2);
        }

        if (user != null) {
            FirebaseDatabase.getInstance()
                    .getReference("admins/" + user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                // User is Admin. Set override to true
                                Log.d("TeamFragment", "Admin Override Enabled");
                                ProfileHolder.setAdminOverride(true);
                                teamAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // No action
                        }
                    });
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (teamAdapter != null) {
            teamAdapter.stopListening();
        }
    }
}
