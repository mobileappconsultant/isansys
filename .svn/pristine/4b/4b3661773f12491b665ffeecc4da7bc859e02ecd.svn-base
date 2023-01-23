package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import com.isansys.common.enums.PatientPositionOrientation;

import static com.isansys.common.measurements.VitalSignType.PATIENT_ORIENTATION;

public class MeasurementPatientOrientation extends MeasurementVitalSign
{
    public PatientPositionOrientation orientation;


    public MeasurementPatientOrientation(PatientPositionOrientation orientation, long timestamp_in_ms, long time_now_in_ms)
    {
        super(PATIENT_ORIENTATION, timestamp_in_ms, time_now_in_ms);
        this.orientation = orientation;
    }


    public MeasurementPatientOrientation(Parcel in)
    {
        super(in);
        this.orientation = PatientPositionOrientation.values()[in.readInt()];
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.orientation.ordinal());
    }

    public static final Creator<MeasurementPatientOrientation> CREATOR = new Parcelable.Creator<MeasurementPatientOrientation>()
    {
        public MeasurementPatientOrientation createFromParcel(Parcel in)
        {
            return new MeasurementPatientOrientation(in);
        }


        public MeasurementPatientOrientation[] newArray(int size)
        {
            return new MeasurementPatientOrientation[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
