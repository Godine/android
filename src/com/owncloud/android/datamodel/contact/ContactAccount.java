package com.owncloud.android.datamodel.contact;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactAccount implements Parcelable {
    private final String mName;
    private final String mType;
    private int mCount;

    public ContactAccount(String type, String name) {
        mName = name;
        mType = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactAccount)) return false;

        final ContactAccount that = (ContactAccount) o;

        if (!mName.equals(that.mName)) return false;
        return mType.equals(that.mType);

    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mType.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mType);
        dest.writeInt(mCount);
    }

    protected ContactAccount(Parcel in) {
        this.mName = in.readString();
        this.mType = in.readString();
        this.mCount = in.readInt();
    }

    public static final Creator<ContactAccount> CREATOR = new Creator<ContactAccount>() {
        public ContactAccount createFromParcel(Parcel source) {
            return new ContactAccount(source);
        }

        public ContactAccount[] newArray(int size) {
            return new ContactAccount[size];
        }
    };

    public void setCount(final int count) {
        mCount = count;
    }

    public String getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public int getCount() {
        return mCount;
    }
}
