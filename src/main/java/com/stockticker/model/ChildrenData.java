package com.stockticker.model;

public class ChildrenData {


    private String selftext;
    private String title;

    private String url;


    public ChildrenData() {
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ChildrenData{" +
                "selftext='" + selftext + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
