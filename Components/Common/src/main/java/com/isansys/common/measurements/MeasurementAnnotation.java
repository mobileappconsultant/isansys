package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.MANUALLY_ENTERED_ANNOTATION;

public class MeasurementAnnotation extends MeasurementVitalSign
{
    public final String annotation;

    public MeasurementAnnotation(String annotation, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_ANNOTATION, timestamp_in_ms);
        this.annotation = annotation;
    }


    public MeasurementAnnotation(String annotation, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_ANNOTATION, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.annotation = annotation;
    }


    public MeasurementAnnotation(Parcel in)
    {
        super(in);
        this.annotation = in.readString();
    }


    public MeasurementAnnotation()
    {
        super(MANUALLY_ENTERED_ANNOTATION);
        this.annotation = "";
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeString(annotation);
    }

    @Override
    public double getPrimaryMeasurement()
    {
        return 0;
    }

    public static final Creator<MeasurementAnnotation> CREATOR = new Parcelable.Creator<MeasurementAnnotation>()
    {
        public MeasurementAnnotation createFromParcel(Parcel in)
        {
            return new MeasurementAnnotation(in);
        }


        public MeasurementAnnotation[] newArray(int size)
        {
            return new MeasurementAnnotation[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
