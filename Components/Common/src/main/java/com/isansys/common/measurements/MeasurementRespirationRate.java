package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.RESPIRATION_RATE;

public class MeasurementRespirationRate extends MeasurementVitalSign
{
    public final int respiration_rate;

    public MeasurementRespirationRate(int respiration_rate, long timestamp_in_ms, long time_now_in_ms)
    {
        super(RESPIRATION_RATE, timestamp_in_ms, time_now_in_ms);
        this.respiration_rate = respiration_rate;
    }

    public MeasurementRespirationRate(int respiration_rate, long timestamp_in_ms)
    {
        super(RESPIRATION_RATE, timestamp_in_ms);
        this.respiration_rate = respiration_rate;
    }

    public MeasurementRespirationRate(Parcel in)
    {
        super(in);
        this.respiration_rate = in.readInt();
    }


    public MeasurementRespirationRate()
    {
        super(RESPIRATION_RATE);
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

    public static final Creator<MeasurementRespirationRate> CREATOR = new Parcelable.Creator<MeasurementRespirationRate>()
    {
        public MeasurementRespirationRate createFromParcel(Parcel in)
        {
            return new MeasurementRespirationRate(in);
        }


        public MeasurementRespirationRate[] newArray(int size)
        {
            return new MeasurementRespirationRate[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
