package com.isansys.common.measurements;

import android.os.Parcel;

public class MeasurementCapillaryRefillTime extends MeasurementVitalSign
{
    public final int value;

    public MeasurementCapillaryRefillTime(int value, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.value = value;
    }


    private MeasurementCapillaryRefillTime(Parcel in)
    {
        super(in);
        this.value = in.readInt();
    }


    public MeasurementCapillaryRefillTime()
    {
        super(VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);
        this.value = INVALID_MEASUREMENT;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.value;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.value);
    }

    public static final Creator<MeasurementCapillaryRefillTime> CREATOR = new Creator<MeasurementCapillaryRefillTime>()
    {
        public MeasurementCapillaryRefillTime createFromParcel(Parcel in)
        {
            return new MeasurementCapillaryRefillTime(in);
        }


        public MeasurementCapillaryRefillTime[] newArray(int size)
        {
            return new MeasurementCapillaryRefillTime[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
