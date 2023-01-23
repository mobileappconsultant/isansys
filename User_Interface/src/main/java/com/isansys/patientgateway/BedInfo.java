package com.isansys.patientgateway;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class BedInfo implements Parcelable
{
    private final int NOT_SET_YET = 0;

    public int bed_details_id;
    public final int by_ward_id;
    public String bed_name;

    public BedInfo()
    {
        bed_details_id = NOT_SET_YET;
        by_ward_id = NOT_SET_YET;
        bed_name = "";
    }
    
    @Override
    public int describeContents() 
    {
        return 0;
    }
    
    /**
    * Storing the Student data to Parcel object
    **/
    @Override
    public void writeToParcel(Parcel dest, int flags) 
    {
        dest.writeInt(bed_details_id);
        dest.writeInt(by_ward_id);
        dest.writeString(bed_name);
    }
    
    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of the object CREATOR
     **/
     private BedInfo(Parcel in)
     {
         this.bed_details_id = in.readInt();
         this.by_ward_id = in.readInt();
         this.bed_name = in.readString();
     }
  
     public static final Parcelable.Creator<BedInfo> CREATOR = new Parcelable.Creator<BedInfo>() 
     {
         @Override
         public BedInfo createFromParcel(Parcel source) 
         {
             return new BedInfo(source);
         }
  
         @Override
         public BedInfo[] newArray(int size) 
         {
             return new BedInfo[size];
         }
     };

    public boolean getInitialised()
    {
        return !(this.bed_details_id == NOT_SET_YET);
    }


    @NotNull
    @Override
    public String toString()
    {
        return "BedInfo{" + "bed_details_id=" + bed_details_id + ", by_ward_id=" + by_ward_id + ", bed_name='" + bed_name + '\'' + '}';
    }
}
