package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.MANUALLY_ENTERED_TEMPERATURE;

public class MeasurementManuallyEnteredTemperature extends MeasurementVitalSign
{
    public double temperature;

    public MeasurementManuallyEnteredTemperature(double temperature, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_TEMPERATURE, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.temperature = temperature;
    }

    public MeasurementManuallyEnteredTemperature(double temperature, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_TEMPERATURE, timestamp_in_ms);
        this.temperature = temperature;
    }

    public MeasurementManuallyEnteredTemperature(Parcel in)
    {
        super(in);
        this.temperature = in.readDouble();
    }


    public MeasurementManuallyEnteredTemperature()
    {
        super(MANUALLY_ENTERED_TEMPERATURE);
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

    public static final Creator<MeasurementManuallyEnteredTemperature> CREATOR = new Parcelable.Creator<MeasurementManuallyEnteredTemperature>()
    {
        public MeasurementManuallyEnteredTemperature createFromParcel(Parcel in)
        {
            return new MeasurementManuallyEnteredTemperature(in);
        }


        public MeasurementManuallyEnteredTemperature[] newArray(int size)
        {
            return new MeasurementManuallyEnteredTemperature[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}