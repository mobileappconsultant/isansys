package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.SPO2;

public class MeasurementSpO2 extends MeasurementVitalSign
{
    public int SpO2;
    public int pulse;

    public MeasurementSpO2(int SpO2, int pulse, long timestamp_in_ms)
    {
        super(SPO2, timestamp_in_ms);
        this.SpO2 = SpO2;
        this.pulse = pulse;
    }


    public MeasurementSpO2(int SpO2, long timestamp_in_ms)
    {
        super(SPO2, timestamp_in_ms);
        this.SpO2 = SpO2;
        this.pulse = INVALID_MEASUREMENT;
    }


    public MeasurementSpO2(int SpO2, int Pulse, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(SPO2, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.SpO2 = SpO2;
        this.pulse = Pulse;
    }


    public MeasurementSpO2(Parcel in)
    {
        super(in);
        this.SpO2 = in.readInt();
        this.pulse = in.readInt();
    }


    public MeasurementSpO2()
    {
        super(SPO2);
        this.SpO2 = INVALID_MEASUREMENT;
        this.pulse = INVALID_MEASUREMENT;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.SpO2;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.SpO2);
        dest.writeInt(this.pulse);
    }

    public static final Creator<MeasurementSpO2> CREATOR = new Parcelable.Creator<MeasurementSpO2>()
    {
        public MeasurementSpO2 createFromParcel(Parcel in)
        {
            return new MeasurementSpO2(in);
        }


        public MeasurementSpO2[] newArray(int size)
        {
            return new MeasurementSpO2[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
