package amu.roboclub.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import amu.roboclub.R;
import amu.roboclub.models.Profile;
import amu.roboclub.models.ProfileInfo;
import amu.roboclub.utils.CircleTransform;
import amu.roboclub.utils.ImageUploader;
import amu.roboclub.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ProfileEditorFragment extends BottomSheetDialogFragment {

    private static final String TAG = "ProfileEditorFragment";
    private static final int PICK_IMAGE = 2020;
    private static final String STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

    private Profile profile;
    private OnProfileChangeListener onProfileChangeListener;

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.close) ImageView close;
    @BindView(R.id.save_fab) FloatingActionButton saveFab;
    @BindView(R.id.photo_fab) FloatingActionButton photoFab;
    @BindView(R.id.avatar) ImageView avatar;
    @BindView(R.id.name) EditText name;
    @BindView(R.id.photo) EditText photoLink;
    @BindView(R.id.position) EditText position;
    @BindView(R.id.batch) EditText batch;
    @BindView(R.id.about) EditText about;
    @BindView(R.id.cv_link) EditText cvLink;

    private View root;
    private String filePath;

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
        this.onProfileChangeListener = onProfileChangeListener;
    }

    private void save() {
        Map<String, Object> profileChangeMap = new HashMap<>();

        String nameString = name.getText().toString();
        if(!TextUtils.isEmpty(nameString) && !profile.name.equals(nameString)) {
            profileChangeMap.put("name", nameString);
        }

        String photoString = photoLink.getText().toString();
        if(!TextUtils.isEmpty(photoString) && !profile.thumbnail.equals(photoString)) {
            profileChangeMap.put("thumbnail", photoString);
        }

        String batchString = batch.getText().toString();
        if(!TextUtils.isEmpty(batchString)) {
            profileChangeMap.put("profile_info/batch", batchString);
        }

        String aboutString = about.getText().toString();
        if(!TextUtils.isEmpty(aboutString)) {
            profileChangeMap.put("profile_info/about", aboutString);
        }

        String cvString = cvLink.getText().toString();
        if(!TextUtils.isEmpty(cvString)) {
            profileChangeMap.put("profile_info/cv", cvString);
        }

        if(profile.adminOverride) {
            String positionString = position.getText().toString();
            if(!TextUtils.isEmpty(positionString)) {
                profileChangeMap.put("position", positionString);
            }
        }

        if(onProfileChangeListener != null && !profileChangeMap.isEmpty()) {
            onProfileChangeListener.onProfileChange(profileChangeMap);
        }

        Log.d(TAG, "save: profile" + onProfileChangeListener + " profileChanges " + profileChangeMap);

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

        position.setText(profile.position);
        if(profile.adminOverride)
            position.setEnabled(true);
        else
            position.setEnabled(false);

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

    private void imageLoaded() {
        if(filePath == null)
            return;

        Picasso.with(getContext())
                .cancelTag(TAG);

        Picasso.with(getContext())
                .load(new File(filePath))
                .placeholder(VectorDrawableCompat.create(root.getResources(), R.drawable.ic_avatar, null))
                .tag(TAG)
                .transform(new CircleTransform())
                .into(avatar);

        Toast.makeText(getContext(), R.string.photo_upload_confirm, Toast.LENGTH_LONG).show();

        Snackbar.make(avatar, R.string.upload_photograph, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.yes, v -> {
                    progressBar.setVisibility(View.VISIBLE);
                    ImageUploader.uploadImage(filePath, (error, message) -> {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, String.format("ImageUpload: Error->%s Message->%s", error, message));

                        if (error) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            // In case of no error, message is URL of photo
                            photoLink.setText(message);
                            Log.d(TAG, "Link set to : " + message);
                            Toast.makeText(getContext(), R.string.image_upload_success, Toast.LENGTH_LONG).show();
                        }
                    });
                }).show();
    }


    @OnClick(R.id.photo_fab)
    public void requestPermissionAndLoadImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(getContext(), STORAGE_PERMISSION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{STORAGE_PERMISSION}, PICK_IMAGE);
            return;
        }

        loadImage();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) loadImage();

    }

    private void loadImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Snackbar.make(root, R.string.image_load_failed, Snackbar.LENGTH_LONG).show();
                return;
            }

            Uri fileUri = data.getData();

            filePath = Utils.getFilePath(getContext(), fileUri);
            imageLoaded();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        onProfileChangeListener = null;
        ImageUploader.removeImageUploadListener();
    }

    public interface OnProfileChangeListener {
        void onProfileChange(Map<String, Object> profileChanges);
    }
}
