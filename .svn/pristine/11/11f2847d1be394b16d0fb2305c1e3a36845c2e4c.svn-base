package com.isansys.common;

import android.os.Parcel;
import android.os.Parcelable;

public class WebPageDescriptor implements Parcelable
{
    public String description;
    public String url;

    public WebPageDescriptor(String description, String url)
    {
        this.description = description;
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeString(this.url);
    }

    public void readFromParcel(Parcel source) {
        this.description = source.readString();
        this.url = source.readString();
    }

    protected WebPageDescriptor(Parcel in) {
        this.description = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<WebPageDescriptor> CREATOR = new Parcelable.Creator<WebPageDescriptor>() {
        @Override
        public WebPageDescriptor createFromParcel(Parcel source) {
            return new WebPageDescriptor(source);
        }

        @Override
        public WebPageDescriptor[] newArray(int size) {
            return new WebPageDescriptor[size];
        }
    };
}
