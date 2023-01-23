package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

public abstract class MeasurementVitalSign implements Parcelable, Cloneable
{
    public static final int INVALID_MEASUREMENT = -1;
    public static final long INVALID_TIMESTAMP = -1;
    public static final int DEFAULT_MEASUREMENT_VALIDITY_IN_SECONDS = 60;

    public long timestamp_in_ms;
    public int measurement_validity_time_in_seconds;
    public int measurement_validity_time_left_in_seconds;

    private VitalSignType type;

    public MeasurementVitalSign(VitalSignType type)
    {
        this.timestamp_in_ms = INVALID_TIMESTAMP;
        this.measurement_validity_time_in_seconds = DEFAULT_MEASUREMENT_VALIDITY_IN_SECONDS;
        this.measurement_validity_time_left_in_seconds = 0;
        this.type = type;
    }


    public MeasurementVitalSign(VitalSignType type, long timestamp_in_ms)
    {
        this.timestamp_in_ms = timestamp_in_ms;
        this.type = type;
    }


    public MeasurementVitalSign(VitalSignType type, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        this.type = type;
        this.timestamp_in_ms = timestamp_in_ms;
        this.measurement_validity_time_in_seconds = measurement_validity_in_seconds;

        calculateRemainingValidityTime(time_now_in_ms);
    }


    public MeasurementVitalSign(VitalSignType type, long timestamp_in_ms, long time_now_in_ms)
    {
        this.type = type;
        this.timestamp_in_ms = timestamp_in_ms;
        this.measurement_validity_time_in_seconds = DEFAULT_MEASUREMENT_VALIDITY_IN_SECONDS;

        calculateRemainingValidityTime(time_now_in_ms);
    }


    protected MeasurementVitalSign(Parcel in)
    {
        this.timestamp_in_ms = in.readLong();
        this.measurement_validity_time_in_seconds = in.readInt();
        this.measurement_validity_time_left_in_seconds = in.readInt();
        this.type = VitalSignType.values()[in.readInt()];
    }


    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(timestamp_in_ms);
        dest.writeInt(measurement_validity_time_in_seconds);
        dest.writeInt(measurement_validity_time_left_in_seconds);
        dest.writeInt(type.ordinal());
    }


    public VitalSignType getType()
    {
        return type;
    }

    public abstract double getPrimaryMeasurement();


    public void calculateRemainingValidityTime(long time_now_in_ms)
    {
        long measurement_validity_expiry_time_in_ms = timestamp_in_ms + (measurement_validity_time_in_seconds * DateUtils.SECOND_IN_MILLIS);

        if (time_now_in_ms > measurement_validity_expiry_time_in_ms)
        {
            this.measurement_validity_time_left_in_seconds = 0;
        }
        else
        {
            this.measurement_validity_time_left_in_seconds = (int)((measurement_validity_expiry_time_in_ms - time_now_in_ms) / DateUtils.SECOND_IN_MILLIS);
        }
    }
}
