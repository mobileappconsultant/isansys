package com.isansys.common.measurements;

import static com.isansys.common.measurements.VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT;

import android.os.Parcel;
import android.os.Parcelable;

public class MeasurementUrineOutput extends MeasurementVitalSign
{
    public final int urine_output;

    public MeasurementUrineOutput(int urine_output, long timestamp_in_ms, int measurement_validity_in_seconds, long time_now_in_ms)
    {
        super(MANUALLY_ENTERED_URINE_OUTPUT, timestamp_in_ms, measurement_validity_in_seconds, time_now_in_ms);
        this.urine_output = urine_output;
    }

    public MeasurementUrineOutput(int urine_output, long timestamp_in_ms)
    {
        super(MANUALLY_ENTERED_URINE_OUTPUT, timestamp_in_ms);
        this.urine_output = urine_output;
    }

    public MeasurementUrineOutput(Parcel in)
    {
        super(in);
        this.urine_output = in.readInt();
    }


    public MeasurementUrineOutput()
    {
        super(MANUALLY_ENTERED_URINE_OUTPUT);
        this.urine_output = INVALID_MEASUREMENT;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.urine_output;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.urine_output);
    }

    public static final Creator<MeasurementUrineOutput> CREATOR = new Parcelable.Creator<MeasurementUrineOutput>()
    {
        public MeasurementUrineOutput createFromParcel(Parcel in)
        {
            return new MeasurementUrineOutput(in);
        }


        public MeasurementUrineOutput[] newArray(int size)
        {
            return new MeasurementUrineOutput[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
