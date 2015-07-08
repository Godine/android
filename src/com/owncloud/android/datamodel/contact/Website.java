package com.owncloud.android.datamodel.contact;

public class Website {
    private String mUrl;
    private int mType;

    public Website(String url, int type) {
        this.mUrl = url;
        this.mType = type;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getType() {
        return mType;
    }

    @Override
    public String toString() {
        return "Website{" +
                "url='" + mUrl + '\'' +
                ", type='" + mType + '\'' +
                '}';
    }
}
