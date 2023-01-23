package com.isansys.patientgateway.deviceInfo;

import android.text.format.DateUtils;

import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.RadioType;
import com.isansys.common.enums.SensorType;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class BtClassicSensorDevice extends DeviceInfo
{
    public BtClassicSensorDevice(RemoteLogging logger, DeviceType type, SensorType sensor_type, SetupModeInfo setup_mode_info)
    {
        super(logger, type, sensor_type, setup_mode_info);
    }


    @Override
    public void resetAsNew()
    {
        super.resetAsNew();

        cancelBluetoothConnectionTimer();
        makeNewBluetoothConnectionTimerObject();

        // Bluetooth scan is for approx 12 seconds. No way to change this that I know of
        desired_bluetooth_search_period_in_milliseconds = 12 * (int) DateUtils.SECOND_IN_MILLIS;
    }


    @Override
    public boolean isDeviceTypeASensorDevice()
    {
        return true;
    }

    public RadioType getRadioType()
    {
        return RadioType.RADIO_TYPE__BT_CLASSIC;
    }
}
