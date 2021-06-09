package com.example.news;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<News> extractNewsFromJson(String urlJson) {
        URL url = createUrl(urlJson);
        String jsonResponse = " ";
        try {
            jsonResponse = performHttpRequest(url);
        } catch (IOException ie) {
            Log.e(LOG_TAG, "Problem performing Http request", ie);
        }
        List<News> news = extractContentFromJson(jsonResponse);
        return news;
    }

    private static String performHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String jsonResponse = " ";

        if (url == null) {
            return jsonResponse;
        }

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response Code : " + urlConnection.getResponseCode());
            }
        } catch (IOException ie) {
            Log.e(LOG_TAG, "Problem retrieving JSON results", ie);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuilder output = new StringBuilder();

        try {
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        } catch (IOException ie) {
            Log.e(LOG_TAG,"Problem retrieving results",ie);
        }
        return output.toString();
    }

    private static URL createUrl(String urlJson) {
        URL url = null;
        try {
            url = new URL(urlJson);
        } catch (MalformedURLException me) {
            Log.e(LOG_TAG, "Error creating url", me);
        }
        return url;
    }

    private static List<News> extractContentFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<News> news = new ArrayList<News>();
        try {
            JSONObject baseJson = new JSONObject(jsonResponse);
            JSONArray articles = baseJson.getJSONArray("articles");

            for (int i = 0; i < articles.length(); i++) {
                JSONObject elements = articles.getJSONObject(i);
                String url = elements.getString("url");
                String title = elements.getString("title");
                String imageUrl = elements.getString("urlToImage");
                String content = elements.getString("content");
                String publish = elements.getString("publishedAt");
                news.add(new News(url, title, imageUrl, content, publish));
            }
            return news;
        } catch (JSONException je) {
            Log.e(LOG_TAG, "Problem retrieving JSON results", je);
        }
        return null;
    }
}