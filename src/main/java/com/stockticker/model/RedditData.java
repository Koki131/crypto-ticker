package com.stockticker.model;

import java.util.List;

public class RedditData {

    private List<Children> children;


    public RedditData() {
    }


    public List<Children> getChildren() {
        return children;
    }

    public void setChildren(List<Children> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "RedditData{" +
                "children=" + children +
                '}';
    }
}
