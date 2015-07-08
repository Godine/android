package com.owncloud.android.datamodel.contact;

public class Im {
    private String mIm;
    private int mType;
    private int mProtocol;

    public Im(String im, int type, final int protocol) {
        mIm = im;
        mType = type;
        mProtocol = protocol;
    }

    public String getIm() {
        return mIm;
    }

    public int getType() {
        return mType;
    }

    public int getProtocol() {
        return mProtocol;
    }

    @Override
    public String toString() {
        return "Im{" +
                "mIm='" + mIm + '\'' +
                ", mType=" + mType +
                ", mProtocol=" + mProtocol +
                '}';
    }
}
