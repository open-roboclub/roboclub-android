package amu.roboclub.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import amu.roboclub.R;
import amu.roboclub.models.Profile;
import amu.roboclub.ui.ProfileActivity;
import amu.roboclub.ui.viewholder.ProfileHolder;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;


public class TeamFragment extends Fragment {
    private Snackbar snackbar;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private boolean adminOverride =  false;

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

        Query teamReference = FirebaseDatabase.getInstance().getReference("team/16").orderByChild("rank");
        FirebaseRecyclerAdapter teamAdapter = new FirebaseRecyclerAdapter<Profile, ProfileHolder>(Profile.class, R.layout.item_contact, ProfileHolder.class, teamReference) {

            @Override
            protected void populateViewHolder(final ProfileHolder holder, final Profile profile, int position) {
                if (snackbar.isShown())
                    snackbar.dismiss();

                holder.name.setText(profile.name);
                holder.position.setText(profile.position);

                Drawable mPlaceholderDrawable = ResourcesCompat.getDrawable(
                        getContext().getResources(),
                        R.drawable.ic_avatar, null);

                Picasso.with(getContext()).load(profile.thumbnail)
                        .placeholder(mPlaceholderDrawable)
                        .transform(new CircleTransform())
                        .into(holder.avatar);

                if(profile.profile_info != null) {
                    holder.showProfile.setVisibility(View.VISIBLE);
                    holder.root.setOnClickListener(view -> {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.REFERENCE_KEY, getRef(position).toString());

                        startActivity(intent);
                    });
                } else {
                    holder.showProfile.setVisibility(View.GONE);
                    holder.root.setOnClickListener(null);
                }

                // TODO: Make better logic structure

                boolean sameUser = user != null && user.getUid().equals(profile.uid);
                if(sameUser ||  adminOverride) {
                    holder.editable.setVisibility(View.VISIBLE);

                    int resId = sameUser?R.drawable.capsule:R.drawable.capsule_grey;

                    holder.editable.setBackground(ResourcesCompat.getDrawable(getResources(), resId, null));

                    holder.root.setOnClickListener(view -> {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.REFERENCE_KEY, getRef(position).toString());

                        startActivity(intent);
                    });
                } else {
                    holder.editable.setVisibility(View.GONE);
                    if(profile.profile_info == null) {
                        holder.root.setOnClickListener(null);
                    }
                }

                LinearLayout contactPanel = (LinearLayout) holder.root.findViewById(R.id.contactPanel);
                contactPanel.removeAllViews();

                if (profile.links == null)
                    return;

                for (String link : profile.links.values()) {
                    ImageButton im = new ImageButton(getContext());

                    int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
                    TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
                    im.setBackgroundResource(typedArray.getResourceId(0, 0));
                    typedArray.recycle();
                    im.setPadding(20, 20, 20, 20);

                    Intent i = null;

                    if (link.contains("facebook")) {
                        im.setImageResource(R.drawable.ic_facebook);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.fb_blue));

                        i = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(link));
                    } else if (link.contains("linkedin")) {
                        im.setImageResource(R.drawable.ic_linkedin);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.li_blue));

                        i = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(link));
                    } else if (link.contains("plus.google")) {
                        im.setImageResource(R.drawable.ic_gplus);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.gp_red));

                        i = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(link));
                    } else if (link.contains("+91")) {
                        im.setImageResource(R.drawable.ic_phone);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.ph_blue));

                        i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + link));
                    } else if (link.contains("@")) {
                        im.setImageResource(R.drawable.ic_email);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.em_red));

                        i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{link});
                        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
                    }


                    if (i != null) {
                        contactPanel.addView(im);
                        final Intent intent = i;
                        im.setOnClickListener(view -> {
                            try {
                                getActivity().startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Snackbar.make(holder.root, R.string.app_not_found, Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
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
                                adminOverride = true;
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
