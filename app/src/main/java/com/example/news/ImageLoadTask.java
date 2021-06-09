package com.example.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoadTask extends AsyncTask<Void,Void, Bitmap> {

    private String mUrl;
    private ImageView mImageView;

    public ImageLoadTask(String url, ImageView imageView) {
        this.mUrl = url;
        this.mImageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL url = new URL(mUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
            return myBitmap;
        }
        catch (Exception ie) {
            ie.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        mImageView.setImageBitmap(result);
    }
}
