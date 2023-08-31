package com.stockticker.model;

public class Reddit {

    private RedditData data;

    public Reddit() {
    }

    public RedditData getData() {
        return data;
    }

    public void setData(RedditData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Reddit{" +
                "data=" + data +
                '}';
    }
}
