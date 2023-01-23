package com.isansys.patientgateway;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceFirmwareDetails
{
    @SerializedName("DeviceFirmwareId")
    @Expose
    public int servers_row_id;

    @SerializedName("DeviceType")
    @Expose
    public int device_type_as_int;

    @SerializedName("FirmwareVersion")
    @Expose
    public int firmware_version;

    @SerializedName("FirmwareFile")
    @Expose
    public String filename;
}
