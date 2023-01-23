package com.isansys.common.measurements;

import android.os.Parcel;

public class MeasurementSupplementalOxygenLevel extends MeasurementVitalSign
{
    public final int value;

    public MeasurementSupplementalOxygenLevel(int value, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.value = value;
    }


    private MeasurementSupplementalOxygenLevel(Parcel in)
    {
        super(in);
        this.value = in.readInt();
    }


    public MeasurementSupplementalOxygenLevel()
    {
        super(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN);
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

    public static final Creator<MeasurementSupplementalOxygenLevel> CREATOR = new Creator<MeasurementSupplementalOxygenLevel>()
    {
        public MeasurementSupplementalOxygenLevel createFromParcel(Parcel in)
        {
            return new MeasurementSupplementalOxygenLevel(in);
        }


        public MeasurementSupplementalOxygenLevel[] newArray(int size)
        {
            return new MeasurementSupplementalOxygenLevel[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
