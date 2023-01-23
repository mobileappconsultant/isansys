package com.isansys.patientgateway;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class WardInfo implements Parcelable
{
    private final int NOT_SET_YET = 0;

    public final int ward_details_id;
    public final String ward_name;

    public WardInfo()
    {
        ward_details_id = NOT_SET_YET;
        ward_name = "";
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
        dest.writeInt(ward_details_id);
        dest.writeString(ward_name);
    }
    
    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of the object CREATOR
     **/
     private WardInfo(Parcel in)
     {
         this.ward_details_id = in.readInt();
         this.ward_name = in.readString();
     }
  
     public static final Parcelable.Creator<WardInfo> CREATOR = new Parcelable.Creator<WardInfo>() 
     {
         @Override
         public WardInfo createFromParcel(Parcel source) 
         {
             return new WardInfo(source);
         }
  
         @Override
         public WardInfo[] newArray(int size) 
         {
             return new WardInfo[size];
         }
     };

    public boolean getInitialised()
    {
        return !(this.ward_details_id == NOT_SET_YET);
    }


    @NotNull
    @Override
    public String toString()
    {
        return "WardInfo{" + "ward_details_id=" + ward_details_id + ", ward_name='" + ward_name + '\'' + '}';
    }
}
