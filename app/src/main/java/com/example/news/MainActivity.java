package com.example.news;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;

import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private String url = "";
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private int loading = -1;
    private NavigationView mNavigation;
    private ActionBarDrawerToggle drawerToggle;
    private ListView listView;
    private String category;
    private int loaderId;
    NewsAdapter mAdapter;
    private ProgressBar progressBar;
    private ImageView mImageView;
    private ImageView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        // use toolbar as action bar
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress);

        mDrawer = findViewById(R.id.drawer_layout);
        mNavigation = findViewById(R.id.nv_view);

        // setup drawer view
        setUpDrawerContent(mNavigation);

        drawerToggle = setToggle();
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        mEmptyTextView = findViewById(R.id.text_gif);
        mImageView = findViewById(R.id.gif);

        listView = findViewById(R.id.list);

        listView.setEmptyView(mEmptyTextView);
        List<News> news = new ArrayList<News>();

        mAdapter = new NewsAdapter(this, news);
        listView.setAdapter(mAdapter);

        progressBar.setVisibility(View.GONE);

        if (!isNetworkAvailable()) {
            Glide.with(this).load(R.raw.tenor2).into(mImageView);
            Glide.with(this).load(R.raw.internet).into(mEmptyTextView);
        } else {
            Glide.with(this).load(R.raw.left).into(mImageView);
            Glide.with(this).load(R.raw.text).into(mEmptyTextView);
        }

        SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                getLoaderManager().restartLoader(loaderId,null,MainActivity.this);
            }
        });

        refreshLayout.setColorSchemeColors(Color.RED);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                News news = mAdapter.getItem(position);
//                Uri uri = Uri.parse(news.getUrl());
//                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//                if(intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
//            }
//        });
//        LoaderManager loaderManager = getLoaderManager();
//        loaderManager.initLoader(1,null,this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Log.v("MainActivity", "Creating the loader");
        progressBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.GONE);
        return new NewsLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        Log.v("MainActivity", "LoadFinished");
        progressBar.setVisibility(View.GONE);
        mAdapter.clear();
        
        if (!isNetworkAvailable()) {
            mImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.raw.tenor2).into(mImageView);
            Glide.with(this).load(R.raw.internet).into(mEmptyTextView);
        }

        else {
            if (data != null && !data.isEmpty()) {
                mImageView.setVisibility(View.GONE);
                mEmptyTextView.setVisibility(View.GONE);
                mAdapter.addAll(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.v("MainActivity", "Resseting the loader");
        mAdapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.sports:
                category = "sports";
                loaderId = 0;
                break;
            case R.id.technology:
                category = "technology";
                loaderId = 1;
                break;
            case R.id.business:
                category = "business";
                loaderId = 2;
                break;
            case R.id.entertainment:
                category = "entertainment";
                loaderId = 3;
                break;
            case R.id.general:
                category = "general";
                loaderId = 4;
                break;
            case R.id.health:
                category = "health";
                loaderId = 5;
                break;
            case R.id.science:
                category = "science";
                loaderId = 6;
                break;
        }
        if (!(TextUtils.isEmpty(url))) {
            url = " ";
        }
        url = "https://saurav.tech/NewsAPI/top-headlines/category/";
        // Uri.parse creates a new Uri object from a properly formated String
        Uri baseUri = Uri.parse(url);
        // Constructs a new builder, copying the attributes from this Uri.
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendPath(category);
        uriBuilder.appendPath("in.json");

        url = uriBuilder.toString();

        if(loading >= 0) {
            getLoaderManager().destroyLoader(loading);
        }
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(loaderId, null, this);

        loading = loaderId;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = mAdapter.getItem(position);
//                        Uri uri = Uri.parse(news.getUrl());
                Intent intent = new Intent(MainActivity.this,ViewingClass.class);
                intent.putExtra("ClassObj", news);
                startActivity(intent);
//                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//                        if(intent.resolveActivity(getPackageManager()) != null) {
//                            startActivity(intent);
//                        }
            }
        });


        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, R.string.open_drawer, R.string.close_drawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        drawerToggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }
}