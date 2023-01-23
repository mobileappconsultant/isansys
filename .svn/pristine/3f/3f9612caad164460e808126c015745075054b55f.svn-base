package com.isansys.patientgateway;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.isansys.common.measurements.MeasurementVitalSign;

import java.util.ArrayList;

import static com.isansys.common.ErrorCodes.ERROR_CODES;

public class HeartBeatInfo extends IntermediateMeasurement implements Parcelable
{
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

    public void setAmplitude(int amplitude)
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

    public void setTimestampInMs(long timestamp_in_ms)
    {
        this.timestamp_in_ms = timestamp_in_ms;

        long time_delta = PatientGatewayService.getNtpTimeNowInMillisecondsStatic() - timestamp_in_ms;

        if (time_delta > (15 * DateUtils.SECOND_IN_MILLIS))
        {
            setBeatIsRealTime(false);
        }
        else
        {
            setBeatIsRealTime(true);
        }
    }

    public void setActivity(ActivityLevel activity)
    {
        this.activity = activity;
    }

    public void setRrInterval(int rr_interval)
    {
        this.rr_interval = rr_interval;
    }

    public void setBeatIsRealTime(boolean beat_is_real_time)
    {
        this.beat_is_real_time = beat_is_real_time;
    }

    public void setBeatAmplitudeTooSmall(boolean beat_amplitude_too_small)
    {
        this.beat_amplitude_too_small = beat_amplitude_too_small;
    }

    public void setBeatAmplitudeTooLarge(boolean beat_amplitude_too_large)
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

	    public int getValue() {
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

    private int rr_interval;
    private int tag;

    private ActivityLevel activity;
    private boolean beat_is_real_time;
    private boolean beat_amplitude_too_small;
    private boolean beat_amplitude_too_large;

    public HeartBeatInfo()
    {
        tag = MeasurementVitalSign.INVALID_MEASUREMENT;
        amplitude = MeasurementVitalSign.INVALID_MEASUREMENT;
        timestamp_in_ms = MeasurementVitalSign.INVALID_TIMESTAMP;

        rr_interval = RR_NOT_YET_CALCULATED;

        beat_is_real_time = false;
        beat_amplitude_too_small = false;
        beat_amplitude_too_large = false;
    }

    public HeartBeatInfo(int desired_tag, int desired_amplitude, long desired_timestamp)
    {
        tag = desired_tag;
        amplitude = desired_amplitude;
        timestamp_in_ms = desired_timestamp;

        rr_interval = RR_NOT_YET_CALCULATED;

        beat_is_real_time = false;
        beat_amplitude_too_small = false;
        beat_amplitude_too_large = false;
    }


    public HeartBeatInfo(HeartBeatInfo info_to_copy)
    {
        this.tag = info_to_copy.tag;
        this.amplitude = info_to_copy.amplitude;
        this.timestamp_in_ms = info_to_copy.timestamp_in_ms;
        this.activity = info_to_copy.activity;
        this.rr_interval = info_to_copy.rr_interval;
        this.beat_is_real_time = info_to_copy.beat_is_real_time;
        this.beat_amplitude_too_small = info_to_copy.beat_amplitude_too_small;
        this.beat_amplitude_too_large = info_to_copy.beat_amplitude_too_large;
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
  
     public static final Creator<HeartBeatInfo> CREATOR = new Creator<HeartBeatInfo>()
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


    public <T extends IntermediateMeasurement> void calculateAndSetRrIntervalIfPossible(T previous_beat, ArrayList<T> list_to_report)
    {
        int tag_delta = this.getTag() - ((HeartBeatInfo)(previous_beat)).getTag();

        if(tag_delta < 0)
        {
            tag_delta += TAG_MAX_SIZE;
        }

        if((this.getRrInterval() == RR_NOT_YET_CALCULATED) && (tag_delta == 1))
        {
            if((this.getAmplitude() >= ERROR_CODES) || (previous_beat.getAmplitude() >= ERROR_CODES))
            {
                this.setRrInterval(RR_NOT_VALID); // set not valid
            }
            else
            {
                this.setRrInterval((int) (this.getTimestampInMs() - previous_beat.getTimestampInMs()));
            }

            // Copy the heart beat info to avoid duplicate references
            HeartBeatInfo temp = new HeartBeatInfo(this);

            list_to_report.add((T) temp);
        }
    }

    public int getTagMaxSize()
    {
        return TAG_MAX_SIZE;
    }

    @Override
    public boolean hasRrIntervals()
    {
        return true;
    }
}
