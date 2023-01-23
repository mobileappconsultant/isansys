package com.isansys.patientgateway;

import android.os.Parcel;
import android.os.Parcelable;

public class RowsPending implements Parcelable {

    // Can't sync because parent data is yet to be synced.
    public int rows_pending_non_syncable;

    // Parent data is synced but server has refused the data to be synced
    public int rows_pending_but_failed;

    // Data can be synced to the server
    public int rows_pending_syncable;

    public int getTotalRowsPending()
    {
        int count = 0;

        if (rows_pending_syncable >= 0)
        {
            count += rows_pending_syncable;
        }

        if (rows_pending_non_syncable >= 0)
        {
            count += rows_pending_non_syncable;
        }

        if (rows_pending_but_failed >= 0)
        {
            count += rows_pending_but_failed;
        }

        return count;
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
        reset();
    }


    public void reset()
    {
        int UNKNOWN = -1;

        rows_pending_non_syncable = UNKNOWN;
        rows_pending_but_failed = UNKNOWN;
        rows_pending_syncable = UNKNOWN;
    }


    public RowsPending(int non_syncable, int failed, int syncable)
    {
        this.rows_pending_non_syncable = non_syncable;
        this.rows_pending_but_failed = failed;
        this.rows_pending_syncable = syncable;
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


    public @Override String toString()
    {
        return "Non Sync = " + rows_pending_non_syncable + ". Failed = " + rows_pending_but_failed + ". Syncable = " + rows_pending_syncable + ". Total = " + getTotalRowsPending();
    }
}
