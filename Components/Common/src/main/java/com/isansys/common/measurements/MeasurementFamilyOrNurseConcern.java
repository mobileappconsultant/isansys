package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN;

public class MeasurementFamilyOrNurseConcern extends MeasurementVitalSign
{
    public final int concern;


    public MeasurementFamilyOrNurseConcern(int concern, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.concern = concern;
    }


    public MeasurementFamilyOrNurseConcern(Parcel in)
    {
        super(in);
        this.concern = in.readInt();
    }


    public MeasurementFamilyOrNurseConcern()
    {
        super(MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN);
        this.concern = INVALID_MEASUREMENT;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.concern;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.concern);
    }

    public static final Creator<MeasurementFamilyOrNurseConcern> CREATOR = new Parcelable.Creator<MeasurementFamilyOrNurseConcern>()
    {
        public MeasurementFamilyOrNurseConcern createFromParcel(Parcel in)
        {
            return new MeasurementFamilyOrNurseConcern(in);
        }


        public MeasurementFamilyOrNurseConcern[] newArray(int size)
        {
            return new MeasurementFamilyOrNurseConcern[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
