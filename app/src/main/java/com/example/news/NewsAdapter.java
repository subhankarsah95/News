package com.example.news;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {
    private Activity mContext;

    public NewsAdapter(Activity context, List<News> news) {
        super(context,0,news);
        this.mContext = context;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.listitem,parent,false);
        }
        News news = getItem(position);

        ImageView imageView = listItemView.findViewById(R.id.image);
        // Glide works to load and display images in the most optimized manner, as fast and smooth as possible
        Glide.with(mContext).load(news.getUrlImage()).into(imageView);
        // new ImageLoadTask(news.getUrlImage(),imageView).execute();

        TextView textView = listItemView.findViewById(R.id.text);
        textView.setText(news.getTitle());

        return listItemView;
    }

}
