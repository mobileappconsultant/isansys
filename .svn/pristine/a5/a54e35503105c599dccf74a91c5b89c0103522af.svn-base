package com.isansys.patientgateway;

import android.os.Parcel;
import android.os.Parcelable;

import com.isansys.common.measurements.MeasurementVitalSign;

public class VitalSignSet implements Parcelable
{
    private int tag;
    private int heart_rate;
    private int respiration_rate;
    private double temperature;
    private long timestamp_in_ms;

    /**
     * TAG_MAX_SIZE is the rollover value of the tag, defined in the Lifetouch firmware as 8192.
     * This means that once the tag reaches 8191, incrementing again it will roll over to zero
     */
    static public final int TAG_MAX_SIZE = 8192;
    static public final int RR_NOT_YET_CALCULATED = -2;
    static public final int RR_NOT_VALID = -1;

    public void setTag(int tag)
    {
        this.tag = tag;
    }

    public void setHeartRate(int hr)
    {
        this.heart_rate = hr;
    }

    public void setRespirationRate(int rr)
    {
        this.respiration_rate = rr;
    }

    public void setTemperature(double temp)
    {
        this.temperature = temp;
    }

    public void setTimestampInMs(long timestamp_in_ms)
    {
        this.timestamp_in_ms = timestamp_in_ms;
    }

    public int getTag()
    {
        return tag;
    }

    public int getHeartRate()
    {
        return heart_rate;
    }

    public int getRespirationRate()
    {
        return respiration_rate;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public long getTimestampInMs()
    {
        return timestamp_in_ms;
    }

    public VitalSignSet()
    {
        tag = MeasurementVitalSign.INVALID_MEASUREMENT;
        heart_rate = MeasurementVitalSign.INVALID_MEASUREMENT;
        respiration_rate = MeasurementVitalSign.INVALID_MEASUREMENT;
        temperature = MeasurementVitalSign.INVALID_MEASUREMENT;
        timestamp_in_ms = MeasurementVitalSign.INVALID_TIMESTAMP;
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
        dest.writeInt(tag);
        dest.writeInt(heart_rate);
        dest.writeInt(respiration_rate);
        dest.writeDouble(temperature);
        dest.writeLong(timestamp_in_ms);
    }
    
    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of the object CREATOR
     **/
     private VitalSignSet(Parcel in)
     {
         this.tag = in.readInt();
         this.heart_rate = in.readInt();
         this.respiration_rate = in.readInt();
         this.temperature = in.readDouble();
         this.timestamp_in_ms = in.readLong();
     }
  
     public static final Creator<VitalSignSet> CREATOR = new Creator<VitalSignSet>()
     {
         @Override
         public VitalSignSet createFromParcel(Parcel source)
         {
             return new VitalSignSet(source);
         }
  
         @Override
         public VitalSignSet[] newArray(int size)
         {
             return new VitalSignSet[size];
         }
     };
}
