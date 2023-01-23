package com.isansys.patientgateway;

import android.os.Parcel;
import android.os.Parcelable;

import com.isansys.common.measurements.MeasurementVitalSign;

import java.util.ArrayList;

public class IntermediateSpO2 extends IntermediateMeasurement implements Parcelable
{
    private int pulse;

    public IntermediateSpO2()
    {
        amplitude = MeasurementVitalSign.INVALID_MEASUREMENT;
        pulse =  MeasurementVitalSign.INVALID_MEASUREMENT;
        timestamp_in_ms = MeasurementVitalSign.INVALID_TIMESTAMP;
    }


    public IntermediateSpO2(int SpO2, int hr, long timestamp)
    {
        amplitude = SpO2;
        pulse = hr;
        timestamp_in_ms = timestamp;
    }

    public IntermediateSpO2(Parcel source) {
        this.amplitude = source.readInt();
        this.pulse = source.readInt();
        this.timestamp_in_ms = source.readLong();
    }

    @Override
    public int getAmplitude()
    {
        return amplitude;
    }

    @Override
    public long getTimestampInMs()
    {
        return timestamp_in_ms;
    }

    @Override
    public <T extends IntermediateMeasurement> void calculateAndSetRrIntervalIfPossible(T previous_beat, ArrayList<T> list_to_report)
    {

    }

    @Override
    public boolean hasRrIntervals()
    {
        return false;
    }

    public int getSpO2()
    {
        return getAmplitude();
    }

    public int getPulse()
    {
        return pulse;
    }

    public void setSpO2(int sp_o2)
    {
        amplitude = sp_o2;
    }

    public void setPulse(int pulse)
    {
        this.pulse = pulse;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i)
    {
        dest.writeInt(amplitude);
        dest.writeInt(pulse);
        dest.writeLong(timestamp_in_ms);
    }

    public static final Creator<IntermediateSpO2> CREATOR = new Creator<IntermediateSpO2>()
    {
        @Override
        public IntermediateSpO2 createFromParcel(Parcel source)
        {
            return new IntermediateSpO2(source);
        }

        @Override
        public IntermediateSpO2[] newArray(int size)
        {
            return new IntermediateSpO2[size];
        }
    };
}
