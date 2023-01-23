package com.isansys.common.measurements;

import android.os.Parcel;
import android.os.Parcelable;

import static com.isansys.common.measurements.VitalSignType.EARLY_WARNING_SCORE;

public class MeasurementEarlyWarningScore extends MeasurementVitalSign
{
    public int early_warning_score;
    public int max_possible_score;
    public boolean is_special_alert;
    public int trend_direction;

    public MeasurementEarlyWarningScore(int early_warning_score, int max_possible_score, boolean is_special_alert, int trend_direction, long timestamp_in_ms, long time_now_in_ms)
    {
        super(EARLY_WARNING_SCORE, timestamp_in_ms, 60, time_now_in_ms);
        this.early_warning_score = early_warning_score;
        this.max_possible_score = max_possible_score;
        this.is_special_alert = is_special_alert;
        this.trend_direction = trend_direction;
        this.timestamp_in_ms = timestamp_in_ms;
    }


    public MeasurementEarlyWarningScore(Parcel in)
    {
        super(in);
        this.early_warning_score = in.readInt();
        this.max_possible_score = in.readInt();
        this.is_special_alert = (boolean) in.readValue(getClass().getClassLoader());
        this.trend_direction = in.readInt();
        this.timestamp_in_ms = in.readLong();
    }


    public MeasurementEarlyWarningScore()
    {
        super(EARLY_WARNING_SCORE);
        this.early_warning_score = INVALID_MEASUREMENT;
        this.max_possible_score = INVALID_MEASUREMENT;
        this.is_special_alert = false;
        this.trend_direction = INVALID_MEASUREMENT;
        this.timestamp_in_ms = INVALID_TIMESTAMP;
    }


    @Override
    public double getPrimaryMeasurement()
    {
        return (double)this.early_warning_score;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.early_warning_score);
        dest.writeInt(this.max_possible_score);
        dest.writeValue(this.is_special_alert);
        dest.writeInt(this.trend_direction);
        dest.writeLong(this.timestamp_in_ms);
    }

    public static final Creator<MeasurementEarlyWarningScore> CREATOR = new Parcelable.Creator<MeasurementEarlyWarningScore>()
    {
        public MeasurementEarlyWarningScore createFromParcel(Parcel in)
        {
            return new MeasurementEarlyWarningScore(in);
        }


        public MeasurementEarlyWarningScore[] newArray(int size)
        {
            return new MeasurementEarlyWarningScore[size];
        }
    };


    @Override
    public int describeContents()
    {
        return 0;
    }
}
