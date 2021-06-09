
package com.example.news;

import java.io.Serializable;

public class News implements Serializable {
    private String mUrl;
    private String mContent;
    private String mUrlImage;
    private String mTitle;
    private String mPublished;

    public News(String url, String title, String urlImage, String content, String publish) {
        this.mUrl = url;
        this.mContent = content;
        this.mUrlImage = urlImage;
        this.mTitle = title;
        this.mPublished = publish;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPublished() {
        return mPublished;
    }

    public String getContent() {
        return mContent;
    }

    public String getUrlImage() {
        return mUrlImage;
    }
}
