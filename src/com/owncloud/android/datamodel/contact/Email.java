package com.owncloud.android.datamodel.contact;

public class Email {
    private String mEmail;
    private int mType;

    public Email(String mail, int type) {
        mEmail = mail;
        mType = type;
    }

    public String getEmail() {
        return mEmail;
    }

    public int getType() {
        return mType;
    }

    @Override
    public String toString() {
        return "Email{" +
                "mEmail='" + mEmail + '\'' +
                ", mType='" + mType + '\'' +
                '}';
    }
}
