package com.isansys.common;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoCallContact implements Parcelable
{
    // lifeguardUserId should work with no Active Directory. Email should work with Active Directory
    public int lifeguardUserId;
    public String name;
    public String email;
    public boolean available;

    public final int INVALID_LIFEGUARD_USER_ID = -1;

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.lifeguardUserId);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeByte(this.available ? (byte) 1 : (byte) 0);
    }

    public VideoCallContact()
    {
        lifeguardUserId = INVALID_LIFEGUARD_USER_ID;
        name = "";
        email = "";
    }

    protected VideoCallContact(Parcel in)
    {
        this.lifeguardUserId = in.readInt();
        this.name = in.readString();
        this.email = in.readString();
        this.available = in.readByte() != 0;
    }

    public static final Creator<VideoCallContact> CREATOR = new Creator<VideoCallContact>()
    {
        @Override
        public VideoCallContact createFromParcel(Parcel source)
        {
            return new VideoCallContact(source);
        }

        @Override
        public VideoCallContact[] newArray(int size)
        {
            return new VideoCallContact[size];
        }
    };

    @Override
    public String toString() {
        return "VideoCallContact{" +
                "lifeguardUserId=" + lifeguardUserId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", available=" + available +
                '}';
    }
}
