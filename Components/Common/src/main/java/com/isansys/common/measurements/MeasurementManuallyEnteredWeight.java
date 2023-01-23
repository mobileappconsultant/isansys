package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.MANUALLY_ENTERED_WEIGHT;

public class MeasurementManuallyEnteredWeight extends MeasurementVitalSign
{
    public double weight;

    public MeasurementManuallyEnteredWeight(double weight, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_WEIGHT, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.weight = weight;
    }

    public MeasurementManuallyEnteredWeight(double weight, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_WEIGHT, timestamp_in_ms);
        this.weight = weight;
    }

    public MeasurementManuallyEnteredWeight(Parcel in)
    {
        super(in);
        this.weight = in.readDouble();
    }


    public MeasurementManuallyEnteredWeight()
    {
        super(MANUALLY_ENTERED_WEIGHT);
        this.weight = INVALID_MEASUREMENT;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.weight;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeDouble(this.weight);
    }

    public static final Creator<MeasurementManuallyEnteredWeight> CREATOR = new Parcelable.Creator<MeasurementManuallyEnteredWeight>()
    {
        public MeasurementManuallyEnteredWeight createFromParcel(Parcel in)
        {
            return new MeasurementManuallyEnteredWeight(in);
        }


        public MeasurementManuallyEnteredWeight[] newArray(int size)
        {
            return new MeasurementManuallyEnteredWeight[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
