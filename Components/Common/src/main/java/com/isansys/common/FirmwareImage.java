package com.isansys.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.isansys.common.enums.DeviceType;

import static com.isansys.common.DeviceInfoConstants.INVALID_FIRMWARE_VERSION;

public class FirmwareImage implements Parcelable
{
    public int latest_stored_firmware_version;                                      // Latest firmware version stored on the Gateway
    public boolean firmware_update_pending;                                         // Is there a firmware update pending for this device (used by the Lifetouch DFU code in the gateway)
    public DeviceType device_type;
    public String file_name;                                                        // Name of firmware binary image stored on the Gateway

    public FirmwareImage(DeviceType type)
    {
        this.latest_stored_firmware_version = INVALID_FIRMWARE_VERSION;
        this.firmware_update_pending = false;
        this.device_type = type;
        this.file_name = "";
    }

    public String deviceCodeLookup()
    {
        switch(device_type)
        {
            case DEVICE_TYPE__LIFETOUCH:
            {
                return "LT";
            }

            case DEVICE_TYPE__LIFETOUCH_BLUE_V2:
            {
                return "LT2";
            }

            case DEVICE_TYPE__LIFETOUCH_THREE:
            {
                return "LT3";
            }

            case DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY:
            {
                return "GW";
            }

            case DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE:
            {
                return "UI";
            }

            default:
            {
                return "?";
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.latest_stored_firmware_version);
        dest.writeByte(this.firmware_update_pending ? (byte) 1 : (byte) 0);
        dest.writeInt(this.device_type == null ? -1 : this.device_type.ordinal());
        dest.writeString(this.file_name);
    }

    protected FirmwareImage(Parcel in)
    {
        this.latest_stored_firmware_version = in.readInt();
        this.firmware_update_pending = in.readByte() != 0;
        int tmpDevice_type = in.readInt();
        this.device_type = tmpDevice_type == -1 ? null : DeviceType.values()[tmpDevice_type];
        this.file_name = in.readString();
    }

    public static final Parcelable.Creator<FirmwareImage> CREATOR = new Parcelable.Creator<FirmwareImage>()
    {
        @Override
        public FirmwareImage createFromParcel(Parcel source)
        {
            return new FirmwareImage(source);
        }

        @Override
        public FirmwareImage[] newArray(int size)
        {
            return new FirmwareImage[size];
        }
    };
}
