package com.isansys.common.measurements;

import android.os.Parcel;

public class MeasurementRespirationDistress extends MeasurementVitalSign
{
    public final int value;

    public MeasurementRespirationDistress(int value, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.value = value;
    }


    private MeasurementRespirationDistress(Parcel in)
    {
        super(in);
        this.value = in.readInt();
    }


    public MeasurementRespirationDistress()
    {
        super(VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS);
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

    public static final Creator<MeasurementRespirationDistress> CREATOR = new Creator<MeasurementRespirationDistress>()
    {
        public MeasurementRespirationDistress createFromParcel(Parcel in)
        {
            return new MeasurementRespirationDistress(in);
        }


        public MeasurementRespirationDistress[] newArray(int size)
        {
            return new MeasurementRespirationDistress[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
