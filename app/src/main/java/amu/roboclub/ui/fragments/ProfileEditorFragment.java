package amu.roboclub.ui.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import amu.roboclub.R;
import amu.roboclub.models.Profile;
import amu.roboclub.models.ProfileInfo;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public class ProfileEditorFragment extends BottomSheetDialogFragment {

    private static final String TAG = "ProfileEditorFragment";

    private Profile profile;
    private WeakReference<OnProfileChangeListener> onProfileChangeListenerWeakReference;

    @BindView(R.id.close) ImageView close;
    @BindView(R.id.save_fab) FloatingActionButton saveFab;
    @BindView(R.id.photo_fab) FloatingActionButton photoFab;
    @BindView(R.id.avatar) ImageView avatar;
    @BindView(R.id.name) EditText name;
    @BindView(R.id.photo) EditText photoLink;
    @BindView(R.id.batch) EditText batch;
    @BindView(R.id.about) EditText about;
    @BindView(R.id.cv_link) EditText cvLink;

    private View root;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        root = View.inflate(getContext(), R.layout.bottomsheet_profile_editor, null);
        dialog.setContentView(root);

        ButterKnife.bind(this, root);

        close.setOnClickListener(view -> dismiss());

        showProfile();

        saveFab.setOnClickListener(view -> save());
    }

    public void setProfile(Profile profile) {
        this.profile = profile;

        showProfile();
    }

    public void setOnProfileChangeListener(OnProfileChangeListener onProfileChangeListener) {
        if(onProfileChangeListenerWeakReference != null) onProfileChangeListenerWeakReference.clear();
        onProfileChangeListenerWeakReference = new WeakReference<>(onProfileChangeListener);
    }

    private void save() {
        Map<String, Object> profileChangeMap = new HashMap<>();

        String nameString = name.getText().toString();
        if(!TextUtils.isEmpty(nameString)) {
            profileChangeMap.put("name", nameString);
        }

        // TODO: Add all attributes to map

        OnProfileChangeListener onProfileChangeListener = onProfileChangeListenerWeakReference.get();
        if(onProfileChangeListener != null) {
            onProfileChangeListener.onProfileChange(profileChangeMap);
        }

        dismiss();
    }

    @OnLongClick(R.id.save_fab)
    public boolean saveDescription() {
        Toast.makeText(getContext(), R.string.save, Toast.LENGTH_SHORT).show();

        return false;
    }

    @OnLongClick(R.id.photo_fab)
    public boolean photoDescription() {
        Toast.makeText(getContext(), R.string.upload_photo, Toast.LENGTH_SHORT).show();

        return false;
    }

    private void showProfile() {
        if(root == null || profile == null) return;

        name.setText(profile.name);

        if(profile.thumbnail != null) {
            Uri uri = Uri.parse(profile.thumbnail);

            // Cancel previously loading image if new request is generated
            Picasso.with(getContext())
                    .cancelTag(TAG);

            Picasso.with(getContext())
                    .load(uri)
                    .placeholder(VectorDrawableCompat.create(root.getResources(), R.drawable.ic_avatar, null))
                    .tag(TAG)
                    .transform(new CircleTransform())
                    .into(avatar);

            photoLink.setText(profile.thumbnail);
        }

        ProfileInfo info = profile.profile_info;
        if(info != null) {
            if(info.batch != null)
                batch.setText(info.batch);

            if(info.cv != null)
                cvLink.setText(info.cv);

            if(info.about != null)
                about.setText(info.about);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if(onProfileChangeListenerWeakReference != null)
            onProfileChangeListenerWeakReference.clear();
    }

    public interface OnProfileChangeListener {
        void onProfileChange(Map<String, Object> profileChanges);
    }
}
