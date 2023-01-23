package com.isansys.patientgateway;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WardInfo implements Parcelable
{
    @SerializedName("WardDetailsId")
    @Expose
    public int ward_details_id;

    @SerializedName("WardDescription")
    @Expose
    public String ward_name;

    public WardInfo()
    {
    	
    }

    public WardInfo(int ward_details_id, String ward_name)
    {
        this.ward_details_id = ward_details_id;
        this.ward_name = ward_name;
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
}
