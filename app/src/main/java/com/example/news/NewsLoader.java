package com.example.news;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.AsyncTaskLoader;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;
    public NewsLoader(Activity context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if(mUrl == null) {
            return null;
        }
        List<News> news = QueryUtils.extractNewsFromJson(mUrl);
        return news;
    }
}
