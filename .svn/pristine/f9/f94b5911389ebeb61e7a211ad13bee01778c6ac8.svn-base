package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.MANUALLY_ENTERED_SPO2;

public class MeasurementManuallyEnteredSpO2 extends MeasurementVitalSign
{
    public int SpO2;
    public int pulse;

    public MeasurementManuallyEnteredSpO2(int SpO2, int pulse, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_SPO2, timestamp_in_ms);
        this.SpO2 = SpO2;
        this.pulse = pulse;
    }


    public MeasurementManuallyEnteredSpO2(int SpO2, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_SPO2, timestamp_in_ms);
        this.SpO2 = SpO2;
        this.pulse = INVALID_MEASUREMENT;
    }


    public MeasurementManuallyEnteredSpO2(int SpO2, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_SPO2, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.SpO2 = SpO2;
        this.pulse = INVALID_MEASUREMENT;
    }


    public MeasurementManuallyEnteredSpO2(Parcel in)
    {
        super(in);
        this.SpO2 = in.readInt();
        this.pulse = in.readInt();
    }


    public MeasurementManuallyEnteredSpO2()
    {
        super(MANUALLY_ENTERED_SPO2);
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

    public static final Creator<MeasurementManuallyEnteredSpO2> CREATOR = new Parcelable.Creator<MeasurementManuallyEnteredSpO2>()
    {
        public MeasurementManuallyEnteredSpO2 createFromParcel(Parcel in)
        {
            return new MeasurementManuallyEnteredSpO2(in);
        }


        public MeasurementManuallyEnteredSpO2[] newArray(int size)
        {
            return new MeasurementManuallyEnteredSpO2[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
