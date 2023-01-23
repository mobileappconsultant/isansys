package com.isansys.common.measurements;

import android.os.Parcel;

public class MeasurementConsciousnessLevel extends MeasurementVitalSign
{
    public final int value;

    public MeasurementConsciousnessLevel(int value, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.value = value;
    }


    private MeasurementConsciousnessLevel(Parcel in)
    {
        super(in);
        this.value = in.readInt();
    }


    public MeasurementConsciousnessLevel()
    {
        super(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL);
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

    public static final Creator<MeasurementConsciousnessLevel> CREATOR = new Creator<MeasurementConsciousnessLevel>()
    {
        public MeasurementConsciousnessLevel createFromParcel(Parcel in)
        {
            return new MeasurementConsciousnessLevel(in);
        }


        public MeasurementConsciousnessLevel[] newArray(int size)
        {
            return new MeasurementConsciousnessLevel[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
