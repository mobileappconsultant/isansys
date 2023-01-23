package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;
import static com.isansys.common.measurements.VitalSignType.HEART_RATE;

public class MeasurementHeartRate extends MeasurementVitalSign
{
    public final int heart_rate;

    public MeasurementHeartRate(int heart_rate, long timestamp_in_ms, long time_now_in_ms)
    {
        super(HEART_RATE, timestamp_in_ms, time_now_in_ms);
        this.heart_rate = heart_rate;
    }

    public MeasurementHeartRate(int heart_rate, long timestamp_in_ms)
    {
        super(HEART_RATE, timestamp_in_ms);
        this.heart_rate = heart_rate;
    }

    public MeasurementHeartRate(Parcel in)
    {
        super(in);
        this.heart_rate = in.readInt();
    }


    public MeasurementHeartRate()
    {
        super(HEART_RATE);
        this.heart_rate = INVALID_MEASUREMENT;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.heart_rate;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.heart_rate);
    }

    public static final Creator<MeasurementHeartRate> CREATOR = new Parcelable.Creator<MeasurementHeartRate>()
    {
        public MeasurementHeartRate createFromParcel(Parcel in)
        {
            return new MeasurementHeartRate(in);
        }


        public MeasurementHeartRate[] newArray(int size)
        {
            return new MeasurementHeartRate[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
