package com.isansys.patientgateway.serverlink.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDeviceFirmwareListResponse
{
    @SerializedName("DeviceFirmwareId")
    @Expose
    private Integer DeviceFirmwareId;

    @SerializedName("DeviceType")
    @Expose
    private Integer DeviceType;

    @SerializedName("FirmwareVersion")
    @Expose
    private Integer FirmwareVersion;

    @SerializedName("FirmwareFile")
    @Expose
    private String FirmwareFile;

    public Integer getDeviceFirmwareId() {
        return DeviceFirmwareId;
    }

    public void setDeviceFirmwareId(Integer deviceFirmwareId) {
        DeviceFirmwareId = deviceFirmwareId;
    }

    public Integer getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(Integer deviceType) {
        DeviceType = deviceType;
    }

    public Integer getFirmwareVersion() {
        return FirmwareVersion;
    }

    public void setFirmwareVersion(Integer firmwareVersion) {
        FirmwareVersion = firmwareVersion;
    }

    public String getFirmwareFile() {
        return FirmwareFile;
    }

    public void setFirmwareFile(String firmwareFile) {
        FirmwareFile = firmwareFile;
    }

    @Override
    public String toString() {
        return "GetDeviceFirmwareListResponse{" +
                "DeviceFirmwareId=" + DeviceFirmwareId +
                ", DeviceType=" + DeviceType +
                ", FirmwareVersion=" + FirmwareVersion +
                ", FirmwareFile='" + FirmwareFile + '\'' +
                '}';
    }
}
