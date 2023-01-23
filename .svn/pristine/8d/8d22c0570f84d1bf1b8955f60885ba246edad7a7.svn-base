package com.isansys.patientgateway.serverlink.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckDeviceStatusResponse
{
    @SerializedName("DeviceType")
    @Expose
    private Integer DeviceType;

    @SerializedName("ByHumanReadableDeviceId")
    @Expose
    private Integer ByHumanReadableDeviceId;

    @SerializedName("WardName")
    @Expose
    private String WardName;

    @SerializedName("BedName")
    @Expose
    private String BedName;

    @SerializedName("StatusCode")
    @Expose
    private Integer StatusCode;

    @SerializedName("InUse")
    @Expose
    private boolean InUse;

    @SerializedName("MessageStatus")
    @Expose
    private String MessageStatus;

    public boolean isInUse() {
        return InUse;
    }

    public Integer getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(Integer deviceType) {
        DeviceType = deviceType;
    }

    public Integer getByHumanReadableDeviceId() {
        return ByHumanReadableDeviceId;
    }

    public void setByHumanReadableDeviceId(Integer byHumanReadableDeviceId) {
        ByHumanReadableDeviceId = byHumanReadableDeviceId;
    }

    public String getWardName() {
        return WardName;
    }

    public String getBedName() {
        return BedName;
    }

    @Override
    public String toString() {
        return "CheckDeviceStatusResponse{" +
                "DeviceType=" + DeviceType +
                ", ByHumanReadableDeviceId=" + ByHumanReadableDeviceId +
                ", WardName='" + WardName + '\'' +
                ", BedName='" + BedName + '\'' +
                ", StatusCode=" + StatusCode +
                ", InUse=" + InUse +
                ", MessageStatus='" + MessageStatus + '\'' +
                '}';
    }
}
