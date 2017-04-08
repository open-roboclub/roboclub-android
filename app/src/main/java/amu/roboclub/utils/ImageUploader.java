package amu.roboclub.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ImageUploader {
    private static final String TAG = "ImageUploader";

    private static ImageUploadListener imageUploadListener;
    private static Cloudinary cloudinary;
    private static String filePath;

    public static void uploadImage(String filePath, ImageUploadListener imageUploadListener) {

        ImageUploader.imageUploadListener = imageUploadListener;

        if(cloudinary == null) cloudinary = new Cloudinary(Utils.getCloudinaryUrl());

        ImageUploader.filePath = filePath;

        new ImageUploaderAsyncTask().execute();
    }

    public static void removeImageUploadListener() {
        imageUploadListener = null;
    }

    public interface ImageUploadListener {
        void onImageUpload(boolean error, String message);
    }

    private static class ImageUploaderAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try{
                File file = new File(filePath);
                String fileName = file.getName();
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));

                Map cloudinaryResult = cloudinary.uploader().upload(file, ObjectUtils.asMap("public_id", "profile_img/"+fileName));
                Log.d(TAG, cloudinaryResult.toString());

                return (String) cloudinaryResult.get("secure_url");
            } catch (IOException ioe) {
                return "Error Uploading File " + ioe.toString();
            } catch (RuntimeException run) {
                return "Error Uploading File " + run.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(imageUploadListener == null)
                return;

            if(result.contains("Error")) {
                imageUploadListener.onImageUpload(true, result);
                imageUploadListener = null;
            } else {
                imageUploadListener.onImageUpload(false, result);
                imageUploadListener = null;
            }

        }
    }
}
