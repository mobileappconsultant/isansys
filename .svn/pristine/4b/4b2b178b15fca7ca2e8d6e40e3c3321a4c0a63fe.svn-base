package com.isansys.patientgateway.serverlink.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckDeviceStatusRequest
{
    @SerializedName("DeviceType")
    @Expose
    private final Integer device_type;

    @SerializedName("ByHumanReadableDeviceId")
    @Expose
    private final Long human_readable_device_id;

    public CheckDeviceStatusRequest(Integer device_type, Long human_readable_device_id)
    {
        this.device_type = device_type;
        this.human_readable_device_id = human_readable_device_id;
    }
}
