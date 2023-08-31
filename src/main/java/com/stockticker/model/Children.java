package com.stockticker.model;

public class Children {

    private ChildrenData data;


    public Children() {
    }

    public ChildrenData getData() {
        return data;
    }

    public void setData(ChildrenData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Children{" +
                "data=" + data +
                '}';
    }
}
