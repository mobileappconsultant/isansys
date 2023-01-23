package com.isansys.common;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class ThresholdSet implements Parcelable
{
    public String name;
    public boolean is_default;
    public int servers_database_row_id;
    public int local_database_row_id;
    public ArrayList<ThresholdSetAgeBlockDetail> list_threshold_set_age_block_detail;

    public ThresholdSet()
    {
        list_threshold_set_age_block_detail = new ArrayList<>();
    }
    
    @Override
    public int describeContents() 
    {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) 
    {
        dest.writeString(name);
        dest.writeInt(is_default ? 1 : 0);
        dest.writeInt(servers_database_row_id);
        dest.writeInt(local_database_row_id);
        dest.writeList(list_threshold_set_age_block_detail);
    }

    private ThresholdSet(Parcel in)
    {
        this.name = in.readString();
        this.is_default = in.readInt() != 0;
        this.servers_database_row_id = in.readInt();
        this.local_database_row_id = in.readInt();
        in.readList(this.list_threshold_set_age_block_detail, getClass().getClassLoader());
    }

    public static final Parcelable.Creator<ThresholdSet> CREATOR = new Parcelable.Creator<ThresholdSet>()
    {
        @Override
        public ThresholdSet createFromParcel(Parcel source)
        {
        return new ThresholdSet(source);
        }

        @Override
        public ThresholdSet[] newArray(int size)
        {
        return new ThresholdSet[size];
        }
    };
}
