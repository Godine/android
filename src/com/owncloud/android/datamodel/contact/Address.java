package com.owncloud.android.datamodel.contact;

public class Address {
    private String mStreet;
    private String mCity;
    private String mRegion;
    private String mPostalCode;
    private String mCountry;
    private String mType;

    public Address(final String streetAddress, final String city, final String region, final String postalCode, final String country, final String type) {
        this.mStreet = streetAddress;
        this.mCity = city;
        this.mRegion = region;
        this.mPostalCode = postalCode;
        this.mCountry = country;
        this.mType = type;
    }

    public String getType() {
        return mType;
    }

    public String getStreet() {
        return mStreet;
    }

    public String getCity() {
        return mCity;
    }

    public String getRegion() {
        return mRegion;
    }

    public String getPostalCode() {
        return mPostalCode;
    }

    public String getCountry() {
        return mCountry;
    }

    @Override
    public String toString() {
        return "Address{" +
                "mStreet='" + mStreet + '\'' +
                ", mCity='" + mCity + '\'' +
                ", mRegion='" + mRegion + '\'' +
                ", mPostalCode='" + mPostalCode + '\'' +
                ", mCountry='" + mCountry + '\'' +
                ", mType='" + mType + '\'' +
                '}';
    }
}
