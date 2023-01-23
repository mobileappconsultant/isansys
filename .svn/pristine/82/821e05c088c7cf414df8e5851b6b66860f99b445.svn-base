package com.isansys.pse_isansysportal;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateModeStatus implements Parcelable
{
    private enum AppName
    {
        NONE,
        PATIENT_GATEWAY,
        USER_INTERFACE
    }

    private AppName app_being_updated = AppName.NONE;

    public enum ApkInstallStatus
    {
        NONE,
        INSTALLED_RESTART_GATEWAY,
        ABORTED,
        FAILED
    }

    public ApkInstallStatus apk_install_status;

    public int available_gateway_version;
    public int available_ui_version;

    private String gateway_apk_name = "";
    private String ui_apk_name = "";

    public UpdateModeStatus()
    {
        apk_install_status = ApkInstallStatus.NONE;
    }

    public void setGatewayInstallationInProgress()
    {
        app_being_updated = AppName.PATIENT_GATEWAY;
    }

    public boolean isGatewayInstallationInProgress()
    {
        return app_being_updated == AppName.PATIENT_GATEWAY;
    }

    public void setUserInterfaceInstallationInProgress()
    {
        app_being_updated = AppName.USER_INTERFACE;
    }

    public boolean isUserInterfaceInstallationInProgress()
    {
        return app_being_updated == AppName.USER_INTERFACE;
    }

    public void setInstallationNotInProgress()
    {
        app_being_updated = AppName.NONE;
    }

    public String getGatewayApkName()
    {
        return gateway_apk_name;
    }

    public String getUserInterfaceApkName()
    {
        return ui_apk_name;
    }

    public void setGatewayVersionDetails(int version, String filename)
    {
        available_gateway_version = version;
        gateway_apk_name = filename;
    }

    public void setUserInterfaceVersionDetails(int version, String filename)
    {
        available_ui_version = version;
        ui_apk_name = filename;
    }

    public String getGatewayInfoForComment()
    {
        return "Gateway version is " + available_gateway_version + " : APK name = " + getGatewayApkName();
    }

    public String getUserInterfaceInfoForComment()
    {
        return "User Interface version is " + available_ui_version + " : APK name = " + getUserInterfaceApkName();
    }

    public void setApkInstallStatus(ApkInstallStatus status)
    {
        apk_install_status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.app_being_updated == null ? -1 : this.app_being_updated.ordinal());
        dest.writeInt(this.apk_install_status == null ? -1 : this.apk_install_status.ordinal());
        dest.writeInt(this.available_gateway_version);
        dest.writeInt(this.available_ui_version);
        dest.writeString(this.gateway_apk_name);
        dest.writeString(this.ui_apk_name);
    }

    public void readFromParcel(Parcel source) {
        int tmpUpdate_in_progress = source.readInt();
        this.app_being_updated = tmpUpdate_in_progress == -1 ? null : AppName.values()[tmpUpdate_in_progress];
        int tmpApk_install_status = source.readInt();
        this.apk_install_status = tmpApk_install_status == -1 ? null : ApkInstallStatus.values()[tmpApk_install_status];
        this.available_gateway_version = source.readInt();
        this.available_ui_version = source.readInt();
        this.gateway_apk_name = source.readString();
        this.ui_apk_name = source.readString();
    }

    protected UpdateModeStatus(Parcel in) {
        int tmpUpdate_in_progress = in.readInt();
        this.app_being_updated = tmpUpdate_in_progress == -1 ? null : AppName.values()[tmpUpdate_in_progress];
        int tmpApk_install_status = in.readInt();
        this.apk_install_status = tmpApk_install_status == -1 ? null : ApkInstallStatus.values()[tmpApk_install_status];
        this.available_gateway_version = in.readInt();
        this.available_ui_version = in.readInt();
        this.gateway_apk_name = in.readString();
        this.ui_apk_name = in.readString();
    }

    public static final Creator<UpdateModeStatus> CREATOR = new Creator<UpdateModeStatus>() {
        @Override
        public UpdateModeStatus createFromParcel(Parcel source) {
            return new UpdateModeStatus(source);
        }

        @Override
        public UpdateModeStatus[] newArray(int size) {
            return new UpdateModeStatus[size];
        }
    };
}
