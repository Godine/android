package com.owncloud.android.datamodel.contact;

public class Phone {
    private String mPhone;
    private int mType;

    public Phone(String phone, int type) {
        mPhone = phone;
        mType = type;
    }

    public String getPhone() {
        return mPhone;
    }

    public int getType() {
        return mType;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "mPhone='" + mPhone + '\'' +
                ", mType='" + mType + '\'' +
                '}';
    }
}
