package com.isansys.common;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoCallDetails implements Parcelable
{
    public String server_url;

    public String meeting_id;
    public String meeting_password;

    // Name of this Gateway to show on Zoom call
    public String display_name;

    // Text shown on Incoming Video Call popup
    public String from_text;

    // Text shown on Incoming Video Call popup if the call times out
    public String missed_call_text;

    // How long to show the Incoming call popup before changing to Missed call
    public int ring_length_in_seconds;

    public String connection_id;

    public boolean cancel;

    public VideoCallDetails() {
    }

    @Override
    public String toString() {
        return "VideoCallDetails{" +
                "server_url='" + server_url + '\'' +
                ", meeting_id='" + meeting_id + '\'' +
                ", meeting_password='" + meeting_password + '\'' +
                ", connection_id='" + connection_id + '\'' +
                ", display_name='" + display_name + '\'' +
                ", from_text='" + from_text + '\'' +
                ", missed_call_text='" + missed_call_text + '\'' +
                ", ring_length_in_seconds=" + ring_length_in_seconds +
                ", cancel=" + cancel +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.server_url);
        dest.writeString(this.meeting_id);
        dest.writeString(this.connection_id);
        dest.writeString(this.meeting_password);
        dest.writeString(this.display_name);
        dest.writeString(this.from_text);
        dest.writeString(this.missed_call_text);
        dest.writeInt(this.ring_length_in_seconds);
        dest.writeInt(this.cancel ? 1 : 0);
    }

    protected VideoCallDetails(Parcel in) {
        this.server_url = in.readString();
        this.meeting_id = in.readString();
        this.connection_id = in.readString();
        this.meeting_password = in.readString();
        this.display_name = in.readString();
        this.from_text = in.readString();
        this.missed_call_text = in.readString();
        this.ring_length_in_seconds = in.readInt();
        this.cancel = in.readInt() == 1;
    }

    public static final Creator<VideoCallDetails> CREATOR = new Creator<VideoCallDetails>() {
        @Override
        public VideoCallDetails createFromParcel(Parcel source) {
            return new VideoCallDetails(source);
        }

        @Override
        public VideoCallDetails[] newArray(int size) {
            return new VideoCallDetails[size];
        }
    };
}
