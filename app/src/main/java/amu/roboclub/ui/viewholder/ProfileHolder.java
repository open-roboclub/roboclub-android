package amu.roboclub.ui.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Map;

import amu.roboclub.R;
import amu.roboclub.models.Profile;
import amu.roboclub.ui.ProfileActivity;
import amu.roboclub.utils.CircleTransform;
import amu.roboclub.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileHolder extends RecyclerView.ViewHolder {

    private static FirebaseUser user;
    private static boolean adminOverride = false;

    private final Context context;

    @BindView(R.id.rootView) CardView root;
    @BindView(R.id.title) TextView name;
    @BindView(R.id.position) TextView position;
    @BindView(R.id.avatar) ImageView avatar;
    @BindView(R.id.contactPanel) LinearLayout contactPanel;
    @BindView(R.id.showProfile) TextView showProfile;
    @BindView(R.id.editable) TextView editable;

    public ProfileHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
        this.context = view.getContext();
    }

    public static void setUser(FirebaseUser user) {
        ProfileHolder.user = user;
    }

    public static void setAdminOverride(boolean adminOverride) {
        ProfileHolder.adminOverride = adminOverride;
    }

    public void setProfile(Profile profile, String databaseReference) {
        name.setText(profile.name);
        position.setText(profile.position);

        Picasso.get()
                .load(Uri.parse(profile.thumbnail))
                .placeholder(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_avatar, null))
                .transform(new CircleTransform())
                .into(avatar);

        if (profile.profile_info != null) {
            showProfile.setVisibility(View.VISIBLE);
            root.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(ProfileActivity.REFERENCE_KEY, databaseReference);

                context.startActivity(intent);
            });
        } else {
            showProfile.setVisibility(View.GONE);
            root.setOnClickListener(null);
        }

        // TODO: Make better logic structure

        boolean sameUser = user != null && user.getUid().equals(profile.uid);
        if(sameUser ||  adminOverride) {
            editable.setVisibility(View.VISIBLE);

            int resId = sameUser ? R.drawable.capsule : R.drawable.capsule_grey;

            editable.setBackground(ResourcesCompat.getDrawable(context.getResources(), resId, null));

            root.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(ProfileActivity.REFERENCE_KEY, databaseReference);

                context.startActivity(intent);
            });
        } else {
            editable.setVisibility(View.GONE);
            if(profile.profile_info == null) {
                root.setOnClickListener(null);
            }
        }

        contactPanel.removeAllViews();

        Map<String, String> links = profile.links;
        if (links == null)
            return;

        Utils.addImageLink(context, R.drawable.ic_email, links.get("email"), contactPanel, Utils.MODE_EMAIL);
        Utils.addImageLink(context, R.drawable.ic_phone, links.get("mobile"), contactPanel, Utils.MODE_TELEPHONE);
        Utils.addImageLink(context, R.drawable.ic_facebook, links.get("facebook"), contactPanel);
        Utils.addImageLink(context, R.drawable.ic_gplus, links.get("g-plus"), contactPanel);
        Utils.addImageLink(context, R.drawable.ic_linkedin, links.get("linkedin"), contactPanel);
    }
}