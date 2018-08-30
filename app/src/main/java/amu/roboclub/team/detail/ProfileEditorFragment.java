package amu.roboclub.team.detail;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cloudinary.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import amu.roboclub.R;
import amu.roboclub.team.Profile;
import amu.roboclub.team.ProfileInfo;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ProfileEditorFragment extends BottomSheetDialogFragment {

    private static final String TAG = "ProfileEditorFragment";
    private static final int PICK_IMAGE = 2020;
    private static final String STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

    @BindView(R.id.nestedScrollView)
    NestedScrollView scrollView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.save_fab)
    FloatingActionButton saveFab;
    @BindView(R.id.photo_fab)
    FloatingActionButton photoFab;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.photo)
    TextInputEditText photoLink;
    @BindView(R.id.position)
    TextInputEditText position;
    @BindView(R.id.batch)
    TextInputEditText batch;
    @BindView(R.id.about)
    TextInputEditText about;
    @BindView(R.id.cv_link)
    TextInputEditText cvLink;
    @BindView(R.id.interests)
    TextInputEditText interests;
    @BindView(R.id.phone)
    TextInputEditText phone;
    @BindView(R.id.email)
    TextInputEditText email;
    @BindView(R.id.facebook)
    TextInputEditText facebook;
    @BindView(R.id.gplus)
    TextInputEditText gplus;
    @BindView(R.id.linkedin)
    TextInputEditText linkedin;

    private Profile profile;
    private OnProfileChangeListener onProfileChangeListener;
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
        setScrollBehaviour();

        setupEditTexts();
    }

    private void setScrollBehaviour() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int dy = scrollY - oldScrollY;

            if (dy > 0) {
                saveFab.hide();
            } else {
                saveFab.show();
            }
        });
    }

    public void setProfile(Profile profile) {
        this.profile = profile;

        showProfile();
    }

    public void setOnProfileChangeListener(OnProfileChangeListener onProfileChangeListener) {
        this.onProfileChangeListener = onProfileChangeListener;
    }

    private void save() {
        String photoString = photoLink.getText().toString();
        String nameString = name.getText().toString();
        String batchString = batch.getText().toString();
        String aboutString = about.getText().toString();
        String cvString = cvLink.getText().toString();
        String positionString = position.getText().toString();
        String interestString = interests.getText().toString();
        String phoneNo = phone.getText().toString();
        String emailAdd = email.getText().toString();
        String facebookId = facebook.getText().toString();
        String gplusId = gplus.getText().toString();
        String linkedinId = linkedin.getText().toString();

        ProfileInfo profileInfo = profile.profile_info;
        if (profileInfo == null)
            profileInfo = new ProfileInfo(); // Hack to avoid NPE

        Map<String, String> links = profile.links;
        if (links == null)
            links = new HashMap<>(); // Hack to avoid NPE

        UpdateMapBuilder updateMapBuilder = new UpdateMapBuilder()
                .addNonNullNonEqualString("name", nameString, profile.name)
                .addNonNullNonEqualString("profile_info/batch", batchString, profileInfo.batch)
                .addNonNullNonEqualString("profile_info/about", aboutString, profileInfo.about)
                .addNonNullNonEqualString("profile_info/cv", cvString, profileInfo.cv)
                .addNonNullNonEqualString("links/mobile", phoneNo, links.get("mobile"))
                .addNonNullNonEqualString("links/email", emailAdd, links.get("email"))
                .addNonNullNonEqualString("links/facebook", facebookId, links.get("facebook"))
                .addNonNullNonEqualString("links/g-plus", gplusId, links.get("g-plus"))
                .addNonNullNonEqualString("links/linkedin", linkedinId, links.get("linkedin"))
                .addNonNullNonEmptyList("profile_info/interests", interestString, profileInfo.interests)
                .addNonEqualString("thumbnail", photoString, profile.thumbnail);


        if (profile.adminOverride)
            updateMapBuilder.addNonNullNonEqualString("position", positionString, profile.position);

        Map<String, Object> updateMap = updateMapBuilder.build();

        if (onProfileChangeListener != null && !updateMap.isEmpty()) {
            onProfileChangeListener.onProfileChange(updateMap);
        }

        Log.d(TAG, "save: profile" + onProfileChangeListener + " profileChanges " + updateMap);

        dismiss();
    }

    private void setupEditTexts() {
        setEditTextDisabled(about, photoLink, position, batch, about, cvLink, interests, phone,
                email, gplus, facebook, linkedin);
    }

    private void setEditTextDisabled(TextInputEditText... editTexts) {
        for (TextInputEditText editText : editTexts) {
            editText.setMaxLines(1);
            editText.setEllipsize(TextUtils.TruncateAt.END);
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    editText.setMaxLines(1);
                    editText.setEllipsize(TextUtils.TruncateAt.END);
                } else {
                    editText.setMaxLines(200);
                    editText.setEllipsize(null);
                }
            });
        }
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
        if (root == null || profile == null) return;

        name.setText(profile.name);

        position.setText(profile.position);
        if (profile.adminOverride)
            position.setEnabled(true);
        else
            position.setEnabled(false);

        if (profile.thumbnail != null) {
            Uri uri = Uri.parse(profile.thumbnail);

            // Cancel previously loading image if new request is generated
            Picasso.get()
                    .cancelTag(TAG);

            Picasso.get()
                    .load(uri)
                    .placeholder(VectorDrawableCompat.create(root.getResources(), R.drawable.ic_avatar, null))
                    .tag(TAG)
                    .transform(new CircleTransform())
                    .into(avatar);

            photoLink.setText(profile.thumbnail);
        }

        ProfileInfo info = profile.profile_info;
        if (info != null) {
            loadText(info.batch, batch);

            loadText(info.cv, cvLink);

            loadText(info.about, about);

            if (info.interests != null)
                loadText(StringUtils.join(info.interests, "\n"), interests);
        }

        Map<String, String> links = profile.links;
        if (links != null && !links.isEmpty()) {
            loadText(links.get("mobile"), phone);
            loadText(links.get("email"), email);
            loadText(links.get("facebook"), facebook);
            loadText(links.get("g-plus"), gplus);
            loadText(links.get("linkedin"), linkedin);
        }
    }

    private void loadText(String text, TextInputEditText editText) {
        if (text == null)
            return;

        editText.setText(text);
    }

    private void imageLoaded() {
        if (filePath == null)
            return;

        Picasso.get()
                .cancelTag(TAG);

        Picasso.get()
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

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

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

            filePath = FileUtils.getFilePath(getContext(), fileUri);
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
