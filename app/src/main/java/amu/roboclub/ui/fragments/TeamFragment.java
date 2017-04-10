package amu.roboclub.ui.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import amu.roboclub.R;
import amu.roboclub.models.Profile;
import amu.roboclub.ui.viewholder.ProfileHolder;
import butterknife.BindView;
import butterknife.ButterKnife;


public class TeamFragment extends Fragment {
    private Snackbar snackbar;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    public static TeamFragment newInstance() {
        return new TeamFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        ProfileHolder.setUser(FirebaseAuth.getInstance().getCurrentUser());

        Query teamReference = FirebaseDatabase.getInstance().getReference("team/16").orderByChild("rank");
        FirebaseRecyclerAdapter teamAdapter =
                new FirebaseRecyclerAdapter<Profile, ProfileHolder>
                        (Profile.class, R.layout.item_contact, ProfileHolder.class, teamReference) {

            @Override
            protected void populateViewHolder(final ProfileHolder holder, Profile profile, int position) {
                if (snackbar.isShown())
                    snackbar.dismiss();

                holder.setProfile(getContext(), profile, getRef(position).toString());
            }
        };

        recyclerView.setAdapter(teamAdapter);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.setSpanCount(2);
        }

        if(user != null) {
            FirebaseDatabase.getInstance()
                    .getReference("admins/" + user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                // User is Admin. Set override to true
                                Log.d("TeamFragment", "Admin Override Enabled");
                                ProfileHolder.setAdminOverride(true);
                                teamAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // No action
                        }
                    });
        }

        return root;
    }


}
