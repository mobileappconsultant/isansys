package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.MANUALLY_ENTERED_HEART_RATE;

public class MeasurementManuallyEnteredHeartRate extends MeasurementVitalSign
{
    public final int heart_rate;

    public MeasurementManuallyEnteredHeartRate(int heart_rate, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_HEART_RATE, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.heart_rate = heart_rate;
    }

    public MeasurementManuallyEnteredHeartRate(int heart_rate, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_HEART_RATE, timestamp_in_ms);
        this.heart_rate = heart_rate;
    }

    public MeasurementManuallyEnteredHeartRate(Parcel in)
    {
        super(in);
        this.heart_rate = in.readInt();
    }


    public MeasurementManuallyEnteredHeartRate()
    {
        super(MANUALLY_ENTERED_HEART_RATE);
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

    public static final Creator<MeasurementManuallyEnteredHeartRate> CREATOR = new Parcelable.Creator<MeasurementManuallyEnteredHeartRate>()
    {
        public MeasurementManuallyEnteredHeartRate createFromParcel(Parcel in)
        {
            return new MeasurementManuallyEnteredHeartRate(in);
        }


        public MeasurementManuallyEnteredHeartRate[] newArray(int size)
        {
            return new MeasurementManuallyEnteredHeartRate[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
