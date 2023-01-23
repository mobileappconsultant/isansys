package com.isansys.patientgateway.deviceInfo;

import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;

import com.idevicesinc.sweetblue.BleManager;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.RadioType;
import com.isansys.common.enums.SensorType;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class BtleSensorDevice extends DeviceInfo
{
    private final BluetoothLeDevice bluetooth_le_device;
    private final Handler ble_command_handler;

    public BtleSensorDevice(RemoteLogging logger, DeviceType type, SensorType sensor_type, SetupModeInfo setup_mode_info, BluetoothLeDevice bluetooth_le_device)
    {
        super(logger, type, sensor_type, setup_mode_info);

        this.bluetooth_le_device = bluetooth_le_device;

        ble_command_handler = new Handler(Looper.myLooper());
    }


    @Override
    public void setPatientOrientationModeIntervalTime(int patient_orientation_mode_interval_time)
    {
        super.setPatientOrientationModeIntervalTime(patient_orientation_mode_interval_time);

        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.setPatientOrientationModeIntervalTime(patient_orientation_mode_interval_time);
        }
    }

    @Override
    public void setPatientOrientationModeEnabled(boolean patient_orientation_mode_enabled)
    {
        super.setPatientOrientationModeEnabled(patient_orientation_mode_enabled);

        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.setPatientOrientationModeEnabled(patient_orientation_mode_enabled);
        }
    }

    @Override
    public void setAndroidDeviceSessionId(int device_session_id)
    {
        super.setAndroidDeviceSessionId(device_session_id);

        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.setAndroidDeviceSessionInProgress(isDeviceSessionInProgress());
        }
    }

    @Override
    public void setDeviceFirmwareVersion(String device_firmware_version_string, int device_firmware_version)
    {
        super.setDeviceFirmwareVersion(device_firmware_version_string, device_firmware_version);

        if (bluetooth_le_device != null)
        {
            bluetooth_le_device.setFirmwareVersion(device_firmware_version);
        }
    }

    @Override
    public void resetAsNew()
    {
        super.resetAsNew();

        cancelBluetoothConnectionTimer();
        makeNewBluetoothConnectionTimerObject();

        desired_bluetooth_search_period_in_milliseconds = 30 * (int) DateUtils.SECOND_IN_MILLIS;

        resetBleScanVariables();

        resetBleStateVariables();
    }

    @Override
    public boolean isDeviceTypeASensorDevice()
    {
        return true;
    }

    @Override
    public boolean isDeviceTypeABtleSensorDevice()
    {
        return true;
    }

    @Override
    public void getOrSetTimestamp(long timestamp_in_milliseconds)
    {
        Log.d(TAG, "getOrSetTimestamp for device type = " + getSensorTypeAndDeviceTypeAsString());

        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.getTimestamp();
        }
    }


    /**
     *
     * BLE SPECIFIC METHODS BELOW
     *
     */


    public void cancelBleCommandHandler()
    {
        ble_command_handler.removeCallbacksAndMessages(null);
    }


    public void enableRawAccelerometerMode(final boolean enable)
    {
        if(bluetooth_le_device != null)
        {
            Runnable runnable = () -> bluetooth_le_device.enableDisableRawAccelerometerMode(enable);

            ble_command_handler.post(runnable);
        }
    }


    public void setDeviceTimestamp(long timestamp_now_in_milliseconds)
    {
        if(bluetooth_le_device != null)
        {
            handleGeneratedTimestamp(timestamp_now_in_milliseconds);

            // handleNewTimestamp has just set start_date_at_midnight_in_milliseconds, so can subtract it from "now".
            long time_since_midnight_in_milliseconds = timestamp_now_in_milliseconds - start_date_at_midnight_in_milliseconds;

            long unix_style_timestamp_in_seconds = start_date_at_midnight_in_milliseconds/(int)DateUtils.SECOND_IN_MILLIS;

            Log.d(TAG, "device_info.start_date_at_midnight_in_milliseconds = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(start_date_at_midnight_in_milliseconds));
            Log.d(TAG, "time_since_midnight_in_milliseconds = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(time_since_midnight_in_milliseconds));
            Log.d(TAG, "unix timestamp of start date = " + unix_style_timestamp_in_seconds);

            // Encode it as an 8 byte array
            byte[] value = new byte[8];
            value[0] = (byte)(time_since_midnight_in_milliseconds >> 24);
            value[1] = (byte)(time_since_midnight_in_milliseconds >> 16);
            value[2] = (byte)(time_since_midnight_in_milliseconds >> 8);
            value[3] = (byte)time_since_midnight_in_milliseconds;

            value[4] = (byte)(unix_style_timestamp_in_seconds >> 24);
            value[5] = (byte)(unix_style_timestamp_in_seconds >> 16);
            value[6] = (byte)(unix_style_timestamp_in_seconds >> 8);
            value[7] = (byte)unix_style_timestamp_in_seconds;

            Log.e(TAG, "setDeviceTimestamp : " + getSensorTypeAndDeviceTypeAsString());

            bluetooth_le_device.sendTimestamp(timestamp_now_in_milliseconds, value);
        }
    }


    @Override
    public void setTestMode(boolean in_test_mode)
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.sendTestModeCommand(in_test_mode);
        }
    }


    public void setMeasurementInterval()
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.setMeasurementInterval(measurement_interval_in_seconds);
        }
    }


    @Override
    public void firmwareOtaUpdate(byte[] firmware_binary_array)
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.firmwareOtaUpdate(firmware_binary_array);
        }
    }


    public void radioOff(byte timeTillOff, byte timeOff)
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.setRadioOff(timeTillOff, timeOff);
        }
    }


    public void sendTurnOffCommand()
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.sendTurnOffCommand();
        }
    }


    public void sendNightModeCommand(boolean enable)
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.sendNightModeCommand(enable);
        }
    }


    public boolean checkLastDataIsRestartRequired(long no_data_check_time)
    {
        if(bluetooth_le_device != null)
        {
            return bluetooth_le_device.checkLastDataIsRestartRequired(no_data_check_time);
        }
        else
        {
            return false;
        }
    }


    public void keepAlive()
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.keepAlive();
        }
    }


    long last_dfu_update_time = 0;


    public boolean checkDfuIsRestartRequired(long last_acceptable_dfu_update_time)
    {
        return last_dfu_update_time < last_acceptable_dfu_update_time;
    }


    @Override
    public void dfuProgressReceived(long time_now_in_milliseconds)
    {
        last_dfu_update_time = time_now_in_milliseconds;
    }

    @Override
    public void dfuComplete()
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.dfuRescanRequired();

            bluetooth_le_device.removeDeviceKeepingBondState();
        }
    }


    public void onDiscovered(BleManager.DiscoveryListener.DiscoveryEvent event)
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.onDiscovered(event);
        }
    }

    public void disconnectDevice()
    {
        Log.i(TAG, "disconnectDevice : " + getSensorTypeAndDeviceTypeAsString() + " : disconnect, un-discover, forget and dispose of old device");

        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.removeDevice();
        }
    }


    public boolean isFound()
    {
        if(bluetooth_le_device != null)
        {
            return bluetooth_le_device.isFound();
        }
        else
        {
            return false;
        }
    }


    public void connectToCharacteristics()
    {
        Log.d(TAG, "connectToCharacteristics for " + getSensorTypeAndDeviceTypeAsString());

        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.setMeasurementInterval(measurement_interval_in_seconds); // Only does anything for Lifetemp

            bluetooth_le_device.connectToCharacteristics();
        }
    }

    public void enableDisableSetupMode(final boolean enable)
    {
        if(bluetooth_le_device != null)
        {
            Runnable runnable = () -> bluetooth_le_device.enableDisableSetupMode(enable);

            ble_command_handler.post(runnable);
        }
    }

    public void enableDisableSetupMode(final boolean enable, final boolean khz_mode_enabled)
    {
        if(bluetooth_le_device != null)
        {
            Runnable runnable = () -> bluetooth_le_device.enableDisableSetupMode(enable, khz_mode_enabled);

            ble_command_handler.post(runnable);
        }
    }


    public void resetBleScanVariables()
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.resetScanVariables();
        }
    }


    public void resetBleStateVariables()
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.resetStateVariables();
        }
    }


    public void resetForUserControlledScan()
    {
        if(bluetooth_le_device != null)
        {
            bluetooth_le_device.resetUnbondedDuringSession();
        }
    }

    public RadioType getRadioType()
    {
        return RadioType.RADIO_TYPE__BTLE;
    }
}
