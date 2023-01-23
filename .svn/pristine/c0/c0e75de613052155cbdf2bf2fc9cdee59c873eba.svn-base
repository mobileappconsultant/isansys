package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE;

public class MeasurementManuallyEnteredRespirationRate extends MeasurementVitalSign
{
    public final int respiration_rate;


    public MeasurementManuallyEnteredRespirationRate(int respiration_rate, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_RESPIRATION_RATE, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.respiration_rate = respiration_rate;
    }


    public MeasurementManuallyEnteredRespirationRate(int respiration_rate, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_RESPIRATION_RATE, timestamp_in_ms);
        this.respiration_rate = respiration_rate;
    }


    public MeasurementManuallyEnteredRespirationRate(Parcel in)
    {
        super(in);
        this.respiration_rate = in.readInt();
    }


    public MeasurementManuallyEnteredRespirationRate()
    {
        super(MANUALLY_ENTERED_RESPIRATION_RATE);
        this.respiration_rate = INVALID_MEASUREMENT;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.respiration_rate;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.respiration_rate);
    }


    public static final Creator<MeasurementManuallyEnteredRespirationRate> CREATOR = new Parcelable.Creator<MeasurementManuallyEnteredRespirationRate>()
    {
        public MeasurementManuallyEnteredRespirationRate createFromParcel(Parcel in)
        {
            return new MeasurementManuallyEnteredRespirationRate(in);
        }


        public MeasurementManuallyEnteredRespirationRate[] newArray(int size)
        {
            return new MeasurementManuallyEnteredRespirationRate[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
