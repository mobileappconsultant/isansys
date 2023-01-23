package com.isansys.patientgateway;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

public class HeartBeatInfo implements Parcelable
{
    private void setTag(int tag)
    {
        this.tag = tag;
    }

    private void setAmplitude(int amplitude)
    {
        this.amplitude = amplitude;

        // If the Amplitude is below 300 uV (112 ADC) then we are out of the Operating Spec of the Lifetouch.
        // The hardware can detect this but if we go about 240 bpm then it starts miscalculating. This puts a hard limit in.
        if (amplitude < 112)
        {
            setBeatAmplitudeTooSmall(true);
        }

        // If the Amplitude is above 3724 ADC counts (10mV) then we are out of the Operating Spec of the Lifetouch.
        // The hardware can detect this but if we go about 240 bpm then it starts miscalculating. This puts a hard limit in.
        // NOTE that this check catches all error codes as well as large real amplitudes.
        if (amplitude > 3724)
        {
            setBeatAmplitudeTooLarge(true);
        }
    }

    private void setTimestampInMs(long timestamp_in_ms)
    {
        this.timestamp_in_ms = timestamp_in_ms;
    }

    private void setActivity(ActivityLevel activity)
    {
        this.activity = activity;
    }

    private void setRrInterval(int rr_interval)
    {
        this.rr_interval = rr_interval;
    }

    private void setBeatIsRealTime(boolean beat_is_real_time)
    {
        this.beat_is_real_time = beat_is_real_time;
    }

    private void setBeatAmplitudeTooSmall(boolean beat_amplitude_too_small)
    {
        this.beat_amplitude_too_small = beat_amplitude_too_small;
    }

    private void setBeatAmplitudeTooLarge(boolean beat_amplitude_too_large)
    {
        this.beat_amplitude_too_large = beat_amplitude_too_large;
    }

    public int getTag()
    {
        return tag;
    }

    public int getAmplitude()
    {
        return amplitude;
    }

    public long getTimestampInMs()
    {
        return timestamp_in_ms;
    }

    public ActivityLevel getActivity()
    {
        return activity;
    }

    public int getRrInterval()
    {
        return rr_interval;
    }

    public boolean isBeatRealTime()
    {
        return beat_is_real_time;
    }

    public boolean isBeatAmplitudeTooSmall()
    {
        return beat_amplitude_too_small;
    }

    public boolean isBeatAmplitudeTooLarge()
    {
        return beat_amplitude_too_large;
    }

    public enum ActivityLevel
	{
		NO_DATA(1),
		NONE(2),
		LOW(3),
		HIGH(4);
		
	    private final int value;
	    
	    ActivityLevel(int value) {
	        this.value = value;
	    }

	    int getValue() {
	        return value;
	    }
	    
	    public static ActivityLevel fromInt(int i) 
	    {
	        for (ActivityLevel b : ActivityLevel .values()) {
	            if (b.getValue() == i) { return b; }
	        }
	        
	        if(i == 0)
	        {
	        	return NO_DATA;
	        }
	        
	        return null;
	    }
	}
	
    private int tag;
    private int amplitude;
    private long timestamp_in_ms;
    private ActivityLevel activity;
    private int rr_interval;
    private boolean beat_is_real_time;
    private boolean beat_amplitude_too_small;
    private boolean beat_amplitude_too_large;


    public HeartBeatInfo(int tag, int amplitude, long timestamp_in_ms, ActivityLevel activity_level, int rr_interval, long time_now)
    {
        setTag(tag);
        setAmplitude(amplitude);
        setTimestampInMs(timestamp_in_ms);
        setActivity(activity_level);
        setRrInterval(rr_interval);

        long time_delta = time_now - timestamp_in_ms;

        setBeatIsRealTime(time_delta <= (15 * DateUtils.SECOND_IN_MILLIS));
    }


    public boolean isBeatAmplitudeTooSmallOrToHigh()
    {
        return beat_amplitude_too_small || beat_amplitude_too_large;
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
        dest.writeInt(amplitude);
        dest.writeLong(timestamp_in_ms);
        dest.writeInt(activity.getValue());
        dest.writeInt(rr_interval);
        dest.writeInt((byte)(beat_is_real_time ? 1 : 0));
        dest.writeInt((byte)(beat_amplitude_too_small ? 1 : 0));
        dest.writeInt((byte)(beat_amplitude_too_large ? 1 : 0));
    }
    
    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of the object CREATOR
     **/
     private HeartBeatInfo(Parcel in)
     {
         this.tag = in.readInt();
         this.amplitude = in.readInt();
         this.timestamp_in_ms = in.readLong();
         this.activity = ActivityLevel.fromInt(in.readInt());
         this.rr_interval = in.readInt();
         this.beat_is_real_time = in.readByte() != 0;
         this.beat_amplitude_too_small = in.readByte() != 0;
         this.beat_amplitude_too_large = in.readByte() != 0;
     }
  
     public static final Parcelable.Creator<HeartBeatInfo> CREATOR = new Parcelable.Creator<HeartBeatInfo>() 
     {
         @Override
         public HeartBeatInfo createFromParcel(Parcel source) 
         {
             return new HeartBeatInfo(source);
         }
  
         @Override
         public HeartBeatInfo[] newArray(int size) 
         {
             return new HeartBeatInfo[size];
         }
     };
}
