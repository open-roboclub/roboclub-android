package amu.roboclub.utils;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class ImageUploader {
    private static WeakReference<ImageUploadListener> imageUploadListenerWeakReference;

    public static void uploadImage(String filePath, ImageUploadListener imageUploadListener) {
        if(imageUploadListenerWeakReference != null) imageUploadListenerWeakReference.clear();

        imageUploadListenerWeakReference = new WeakReference<>(imageUploadListener);

        new ImageUploaderAsyncTask().execute();
    }

    public interface ImageUploadListener {
        void onImageUpload(boolean error, String message);
    }

    private static class ImageUploaderAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return "Image Uploaded";
        }

        @Override
        protected void onPostExecute(String result) {
            ImageUploadListener imageUploadListener = imageUploadListenerWeakReference.get();
            if(imageUploadListener == null)
                return;

            imageUploadListener.onImageUpload(false, result);
        }
    }
}
