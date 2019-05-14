package amu.roboclub.team.list;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import androidx.annotation.DrawableRes;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import amu.roboclub.R;

class ImageUtils {

    static final int MODE_LINK = 0, MODE_TELEPHONE = 1, MODE_EMAIL = 2;

    private static ImageButton getRippleImageButton(Context context) {
        ImageButton im = new ImageButton(context);

        int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        im.setBackgroundResource(typedArray.getResourceId(0, 0));
        typedArray.recycle();
        im.setPadding(20, 20, 20, 20);

        return im;
    }

    static void addImageLink(Context context, @DrawableRes int drawable, String link, LinearLayout linearLayout, int mode) {
        if (link == null)
            return;

        ImageButton im = getRippleImageButton(context);

        im.setImageResource(drawable);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        if (mode == MODE_TELEPHONE)
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + link));
        else if (mode == MODE_EMAIL) {
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

    static void addImageLink(Context context, @DrawableRes int drawable, String link, LinearLayout linearLayout) {
        addImageLink(context, drawable, link, linearLayout, MODE_LINK);
    }

}
