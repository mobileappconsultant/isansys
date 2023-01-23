package com.isansys.common.measurements;

import static com.isansys.common.measurements.VitalSignType.WEIGHT;

import android.os.Parcel;
import android.os.Parcelable;

public class MeasurementWeight extends MeasurementVitalSign
{
    public double weight;

    public MeasurementWeight(double weight, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(WEIGHT, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.weight = weight;
    }

    public MeasurementWeight(double weight, long timestamp_in_ms, long time_now_in_ms)
    {
        super(WEIGHT, timestamp_in_ms, time_now_in_ms);
        this.weight = weight;
    }

    public MeasurementWeight(double weight, long timestamp_in_ms)
    {
        super(WEIGHT, timestamp_in_ms);
        this.weight = weight;
    }

    public MeasurementWeight(Parcel in)
    {
        super(in);
        this.weight = in.readDouble();
    }

    public MeasurementWeight()
    {
        super(WEIGHT);
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

    public static final Creator<MeasurementWeight> CREATOR = new Parcelable.Creator<MeasurementWeight>()
    {
        public MeasurementWeight createFromParcel(Parcel in)
        {
            return new MeasurementWeight(in);
        }


        public MeasurementWeight[] newArray(int size)
        {
            return new MeasurementWeight[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }
}
