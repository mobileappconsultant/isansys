package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.BATTERY_READING;

public class MeasurementBatteryReading extends MeasurementVitalSign
{
    public int millivolts;
    public int percentage;


    public MeasurementBatteryReading(int millivolts, int percentage, long timestamp_in_ms, long time_now_in_ms)
    {
        super(BATTERY_READING, timestamp_in_ms, time_now_in_ms);
        this.millivolts = millivolts;
        this.percentage = percentage;
    }


    public MeasurementBatteryReading(Parcel in)
    {
        super(in);
        this.millivolts = in.readInt();
        this.percentage = in.readInt();
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.millivolts;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.millivolts);
        dest.writeInt(this.percentage);
    }

    public static final Creator<MeasurementBatteryReading> CREATOR = new Parcelable.Creator<MeasurementBatteryReading>()
    {
        public MeasurementBatteryReading createFromParcel(Parcel in)
        {
            return new MeasurementBatteryReading(in);
        }


        public MeasurementBatteryReading[] newArray(int size)
        {
            return new MeasurementBatteryReading[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
