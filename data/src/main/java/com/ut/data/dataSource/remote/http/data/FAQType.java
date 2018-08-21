package com.ut.data.dataSource.remote.http.data;

public class FAQType {
    private int id;
    private String type;

    @Override
    public String toString() {
        return "FAQType id:" + id + " type:" + type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
