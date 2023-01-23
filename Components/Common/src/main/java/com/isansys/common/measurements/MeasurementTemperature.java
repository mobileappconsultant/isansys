package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.TEMPERATURE;

public class MeasurementTemperature extends MeasurementVitalSign
{
    public double temperature;

    public MeasurementTemperature(double temperature, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(TEMPERATURE, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.temperature = temperature;
    }

    public MeasurementTemperature(double temperature, long timestamp_in_ms, long time_now_in_ms)
    {
        super(TEMPERATURE, timestamp_in_ms, time_now_in_ms);
        this.temperature = temperature;
    }

    public MeasurementTemperature(double temperature, long timestamp_in_ms)
    {
        super(TEMPERATURE, timestamp_in_ms);
        this.temperature = temperature;
    }

    public MeasurementTemperature(Parcel in)
    {
        super(in);
        this.temperature = in.readDouble();
    }


    public MeasurementTemperature()
    {
        super(TEMPERATURE);
        this.temperature = INVALID_MEASUREMENT;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return this.temperature;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeDouble(this.temperature);
    }

    public static final Creator<MeasurementTemperature> CREATOR = new Parcelable.Creator<MeasurementTemperature>()
    {
        public MeasurementTemperature createFromParcel(Parcel in)
        {
            return new MeasurementTemperature(in);
        }


        public MeasurementTemperature[] newArray(int size)
        {
            return new MeasurementTemperature[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
