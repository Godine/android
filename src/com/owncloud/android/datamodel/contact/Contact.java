package com.owncloud.android.datamodel.contact;

import java.util.List;

public class Contact {
    private long mId;
    private String mName;
    private String mNickName;
    private String mTitle;
    private String mCompany;
    private String mNote;
    private List<Email> mEmails;
    private List<Phone> mPhones;
    private List<Im> mIms;
    private List<Address> mAddresses;
    private List<Website> mWebsites;
    private String mBirthDate;


    public long getId() {
        return mId;
    }

    public void setId(final long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(final String name) {
        mName = name;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(final String nickName) {
        mNickName = nickName;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String title) {
        mTitle = title;
    }

    public String getCompany() {
        return mCompany;
    }

    public void setCompany(final String company) {
        mCompany = company;
    }

    public List<Email> getEmails() {
        return mEmails;
    }

    public void setEmails(final List<Email> emails) {
        mEmails = emails;
    }

    public List<Phone> getPhones() {
        return mPhones;
    }

    public void setPhones(final List<Phone> phones) {
        mPhones = phones;
    }

    public List<Im> getIms() {
        return mIms;
    }

    public void setIms(final List<Im> ims) {
        mIms = ims;
    }

    public List<Address> getAddresses() {
        return mAddresses;
    }

    public void setAddresses(final List<Address> addresses) {
        mAddresses = addresses;
    }

    public List<Website> getWebsites() {
        return mWebsites;
    }

    public void setWebsites(final List<Website> websites) {
        mWebsites = websites;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public void setBirthDate(final String birthDate) {
        mBirthDate = birthDate;
    }

    public String getBirthDate() {
        return mBirthDate;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "mName='" + mName + '\'' +
                ", mNickName='" + mNickName + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mCompany='" + mCompany + '\'' +
                ", mNote='" + mNote + '\'' +
                ", mBirthDate='" + mBirthDate + '\'' +
                ", mEmails=" + mEmails +
                ", mPhones=" + mPhones +
                ", mIms=" + mIms +
                ", mAddresses=" + mAddresses +
                ", mWebsites=" + mWebsites +
                '}';
    }


}
