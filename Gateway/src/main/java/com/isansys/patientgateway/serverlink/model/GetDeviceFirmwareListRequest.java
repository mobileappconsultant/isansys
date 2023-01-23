package com.isansys.patientgateway.serverlink.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDeviceFirmwareListRequest
{
    @SerializedName("Latest")
    @Expose
    private final boolean Latest = true;
}
