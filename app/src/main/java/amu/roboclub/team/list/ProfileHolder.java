package amu.roboclub.team.list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Map;

import amu.roboclub.R;
import amu.roboclub.team.Profile;
import amu.roboclub.team.detail.ProfileActivity;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;

class ProfileHolder extends RecyclerView.ViewHolder {

    private static FirebaseUser user;
    private static boolean adminOverride = false;

    private final Context context;

    @BindView(R.id.rootView)
    protected CardView root;
    @BindView(R.id.title)
    protected TextView name;
    @BindView(R.id.position)
    protected TextView position;
    @BindView(R.id.avatar)
    protected ImageView avatar;
    @BindView(R.id.contactPanel)
    protected LinearLayout contactPanel;
    @BindView(R.id.showProfile)
    protected TextView showProfile;
    @BindView(R.id.editable)
    protected TextView editable;

    ProfileHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
        this.context = view.getContext();
    }

    static void setUser(FirebaseUser user) {
        ProfileHolder.user = user;
    }

    static void setAdminOverride(boolean adminOverride) {
        ProfileHolder.adminOverride = adminOverride;
    }

    void setProfile(Profile profile, String databaseReference) {
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
        if (sameUser || adminOverride) {
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
            if (profile.profile_info == null) {
                root.setOnClickListener(null);
            }
        }

        contactPanel.removeAllViews();

        Map<String, String> links = profile.links;
        if (links == null)
            return;

        ImageUtils.addImageLink(context, R.drawable.ic_email, links.get("email"), contactPanel, ImageUtils.MODE_EMAIL);
        ImageUtils.addImageLink(context, R.drawable.ic_phone, links.get("mobile"), contactPanel, ImageUtils.MODE_TELEPHONE);
        ImageUtils.addImageLink(context, R.drawable.ic_facebook, links.get("facebook"), contactPanel);
        ImageUtils.addImageLink(context, R.drawable.ic_gplus, links.get("g-plus"), contactPanel);
        ImageUtils.addImageLink(context, R.drawable.ic_linkedin, links.get("linkedin"), contactPanel);
    }
}