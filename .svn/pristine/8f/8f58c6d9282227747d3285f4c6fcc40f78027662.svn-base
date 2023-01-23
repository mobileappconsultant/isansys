package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE;

public class MeasurementManuallyEnteredBloodPressure extends MeasurementVitalSign
{
    public int systolic;
    public int diastolic;
    public int pulse;

    public MeasurementManuallyEnteredBloodPressure(int systolic, int diastolic, int pulse, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_BLOOD_PRESSURE, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = pulse;
    }

    public MeasurementManuallyEnteredBloodPressure(int systolic, int diastolic, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_BLOOD_PRESSURE, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = INVALID_MEASUREMENT;
    }

    public MeasurementManuallyEnteredBloodPressure(int systolic, int diastolic, int pulse, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_BLOOD_PRESSURE, timestamp_in_ms);
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = pulse;
    }

    public MeasurementManuallyEnteredBloodPressure(int systolic, int diastolic, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_BLOOD_PRESSURE, timestamp_in_ms);
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = INVALID_MEASUREMENT;
    }



    public MeasurementManuallyEnteredBloodPressure(Parcel in)
    {
        super(in);
        this.systolic = in.readInt();
        this.diastolic = in.readInt();
        this.pulse = in.readInt();
    }


    public MeasurementManuallyEnteredBloodPressure()
    {
        super(MANUALLY_ENTERED_BLOOD_PRESSURE);
        this.systolic = INVALID_MEASUREMENT;
        this.diastolic = INVALID_MEASUREMENT;
        this.pulse = INVALID_MEASUREMENT;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.systolic;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.systolic);
        dest.writeInt(this.diastolic);
        dest.writeInt(this.pulse);
    }

    public static final Parcelable.Creator<MeasurementManuallyEnteredBloodPressure> CREATOR = new Parcelable.Creator<MeasurementManuallyEnteredBloodPressure>()
    {
        public MeasurementManuallyEnteredBloodPressure createFromParcel(Parcel in)
        {
            return new MeasurementManuallyEnteredBloodPressure(in);
        }


        public MeasurementManuallyEnteredBloodPressure[] newArray(int size)
        {
            return new MeasurementManuallyEnteredBloodPressure[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
