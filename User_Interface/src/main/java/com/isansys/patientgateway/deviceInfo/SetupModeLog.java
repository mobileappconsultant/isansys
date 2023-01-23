package com.isansys.patientgateway.deviceInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.SensorType;
import com.isansys.pse_isansysportal.TimestampConversion;

import org.jetbrains.annotations.NotNull;

public class SetupModeLog implements Parcelable
{
    public final SensorType sensor_type;
    public final DeviceType device_type;
    public final long start_time;
    public final long end_time;

    // This is what is displayed on the buttons on the Setup Mode Viewer popup
    @NotNull
    @Override
    public String toString()
    {
        String start_timestamp = TimestampConversion.convertDateToHumanReadableStringDayHoursMinutesSeconds(start_time);
        String end_timestamp = TimestampConversion.convertDateToHumanReadableStringDayHoursMinutesSeconds(end_time);

        return start_timestamp + "\n" + end_timestamp;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.sensor_type.ordinal());
        dest.writeInt(this.device_type.ordinal());
        dest.writeLong(this.start_time);
        dest.writeLong(this.end_time);
    }

    public SetupModeLog(SensorType sensor_type, DeviceType device_type, long start_time, long end_time)
    {
        this.sensor_type = sensor_type;
        this.device_type = device_type;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    private SetupModeLog(Parcel in)
    {
        this.sensor_type = SensorType.values()[in.readInt()];
        this.device_type = DeviceType.values()[in.readInt()];
        this.start_time = in.readLong();
        this.end_time = in.readLong();
    }

    public static final Parcelable.Creator<SetupModeLog> CREATOR = new Parcelable.Creator<SetupModeLog>()
    {
        @Override
        public SetupModeLog createFromParcel(Parcel source)
        {
            return new SetupModeLog(source);
        }

        @Override
        public SetupModeLog[] newArray(int size)
        {
            return new SetupModeLog[size];
        }
    };
}
