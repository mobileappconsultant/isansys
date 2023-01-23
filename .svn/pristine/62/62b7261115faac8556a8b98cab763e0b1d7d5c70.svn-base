package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.SETUP_MODE_DATA_POINT;

public class MeasurementSetupModeDataPoint extends MeasurementVitalSign
{
    public final int sample;

    public MeasurementSetupModeDataPoint(int sample, long timestamp_in_ms)
    {
        super(SETUP_MODE_DATA_POINT, timestamp_in_ms);
        this.sample = sample;
    }

    public MeasurementSetupModeDataPoint(Parcel in)
    {
        super(in);
        this.sample = in.readInt();
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.sample;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.sample);
    }

    public static final Creator<MeasurementSetupModeDataPoint> CREATOR = new Parcelable.Creator<MeasurementSetupModeDataPoint>()
    {
        public MeasurementSetupModeDataPoint createFromParcel(Parcel in)
        {
            return new MeasurementSetupModeDataPoint(in);
        }


        public MeasurementSetupModeDataPoint[] newArray(int size)
        {
            return new MeasurementSetupModeDataPoint[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
