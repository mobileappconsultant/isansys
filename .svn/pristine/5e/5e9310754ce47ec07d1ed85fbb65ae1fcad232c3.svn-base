package com.isansys.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.isansys.common.enums.DeviceType;

public class AppVersions
{
    private final Context context;

    public AppVersions(Context parent_context)
    {
        context = parent_context;
    }

    private final String INVALID_APP_VERSION_NUMBER = "-1";

    public boolean doAllVersionNumbersMatch()
    {
        if (getGatewayVersionNumberAsInt() == getUserInterfaceVersionNumberAsInt())
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public String getGatewayVersionNumber()
    {
        return getVersionNumberByType(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY);
    }


    public int getGatewayVersionNumberAsInt()
    {
        return Integer.parseInt(getGatewayVersionNumber());
    }


    public String getUserInterfaceVersionNumber()
    {
        return getVersionNumberByType(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE);
    }


    public int getUserInterfaceVersionNumberAsInt()
    {
        return Integer.parseInt(getUserInterfaceVersionNumber());
    }


    private String getVersionNumberOfApp(String package_name)
    {
        String version_name = INVALID_APP_VERSION_NUMBER;

        PackageInfo package_info;
        try
        {
            package_info = context.getPackageManager().getPackageInfo(package_name, 0);

            //int verCode = package_info.versionCode;
            version_name = package_info.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return version_name;
    }

    public String getVersionNumberByType(DeviceType type)
    {
        switch (type)
        {
            case DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY:
            {
                return getVersionNumberOfApp("com.isansys.patientgateway");
            }

            case DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE:
            {
                return getVersionNumberOfApp("com.isansys.pse_isansysportal");
            }

            default:
            {
                return INVALID_APP_VERSION_NUMBER;
            }
        }
    }
}
