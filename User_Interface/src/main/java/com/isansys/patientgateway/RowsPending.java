package com.isansys.patientgateway;

import android.os.Parcel;
import android.os.Parcelable;

public class RowsPending implements Parcelable {
    private final int UNKNOWN = -1;

    // Can't sync because parent data is yet to be synced.
    public int rows_pending_non_syncable = UNKNOWN;

    // Parent data is synced but server has refused the data to be synced
    public int rows_pending_but_failed =  UNKNOWN;

    // Data can be synced to the server
    public int rows_pending_syncable = UNKNOWN;

    public int getTotalRowsPending()
    {
        return rows_pending_non_syncable + rows_pending_but_failed + rows_pending_syncable;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.rows_pending_non_syncable);
        dest.writeInt(this.rows_pending_but_failed);
        dest.writeInt(this.rows_pending_syncable);
    }

    public RowsPending()
    {
    }

    private RowsPending(Parcel in)
    {
        this.rows_pending_non_syncable = in.readInt();
        this.rows_pending_but_failed = in.readInt();
        this.rows_pending_syncable = in.readInt();
    }

    public static final Parcelable.Creator<RowsPending> CREATOR = new Parcelable.Creator<RowsPending>()
    {
        @Override
        public RowsPending createFromParcel(Parcel source) {
            return new RowsPending(source);
        }

        @Override
        public RowsPending[] newArray(int size) {
            return new RowsPending[size];
        }
    };
}
