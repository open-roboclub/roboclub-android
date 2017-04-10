package amu.roboclub.utils;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import amu.roboclub.BuildConfig;
import amu.roboclub.R;

public class Utils {

    public static final int MODE_LINK = 0, MODE_TELEPHONE = 1, MODE_EMAIL = 2;

    static String  getCloudinaryUrl() {
        String url = BuildConfig.API_KEY;

        try {
            String decoded = new String(Base64.decode(url, Base64.DEFAULT), "UTF-8");
            return decoded.substring(1, decoded.length() - 1);
        } catch (UnsupportedEncodingException uee) {
            return null;
        }
    }

    private static ImageButton getRippleImageButton(Context context) {
        ImageButton im = new ImageButton(context);

        int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        im.setBackgroundResource(typedArray.getResourceId(0, 0));
        typedArray.recycle();
        im.setPadding(20, 20, 20, 20);

        return im;
    }

    public static void addImageLink(Context context, @DrawableRes int drawable, String link, LinearLayout linearLayout, int mode) {
        if(link == null)
            return;

        ImageButton im = getRippleImageButton(context);

        im.setImageResource(drawable);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        if(mode == MODE_TELEPHONE)
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + link));
        else if(mode == MODE_EMAIL) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{link});
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.mail_subject));
        }

        linearLayout.addView(im);

        Intent finalIntent = intent;
        im.setOnClickListener(view -> {
            try {
                context.startActivity(finalIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, R.string.app_not_found, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void addImageLink(Context context, @DrawableRes int drawable, String link, LinearLayout linearLayout) {
        addImageLink(context, drawable, link, linearLayout, MODE_LINK);
    }

    public static String getFilePath(Context context, Uri uri) {
        String selection = null;
        String[] selectionArgs = null;

        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);

                if (cursor == null)
                    return null;

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
