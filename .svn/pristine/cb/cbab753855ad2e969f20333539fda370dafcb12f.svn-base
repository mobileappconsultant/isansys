package com.isansys.common;

import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.SensorType;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceSession implements Parcelable
{
	public SensorType sensor_type;
	public DeviceType device_type;
	public int local_device_session_id;

	public DeviceSession(SensorType sensor_type, DeviceType device_type, int session_id)
	{
		this.sensor_type = sensor_type;
		this.device_type = device_type;
		this.local_device_session_id = session_id;
	}
	
	public DeviceSession(Parcel in) 
	{
		sensor_type = SensorType.values()[in.readInt()];
		device_type = DeviceType.values()[in.readInt()];
		local_device_session_id = in.readInt();
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
        dest.writeInt(this.local_device_session_id);
	}
	
	public static final Creator<DeviceSession> CREATOR = new Parcelable.Creator<DeviceSession>()
    {
        public DeviceSession createFromParcel(Parcel in)
        {
            return new DeviceSession(in);
        }


        public DeviceSession[] newArray(int size)
        {
            return new DeviceSession[size];
        }
    };
}
