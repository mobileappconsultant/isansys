package com.isansys.patientgateway.deviceInfo;

import android.content.Intent;

import com.google.gson.JsonObject;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService.PatientSessionInfo;
import com.isansys.patientgateway.bluetooth.SpO2.NoninWristOx;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_TM2441.BluetoothLe_AnD_TM2441;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UA651.BluetoothLe_AnD_UA651;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UA1200BLE.BluetoothLe_AnD_UA1200BLE;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UA656.BluetoothLe_AnD_UA656;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UC352BLE.BluetoothLe_AnD_UC352BLE;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Lifetemp.BluetoothLeLifetempV2;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Instapatch.BluetoothLeInstapatch;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Lifetouch.BluetoothLeLifetouch;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree.BluetoothLeLifetouchThree;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Nonin3230.BluetoothLeNonin3230;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.NoninWristOxBtle.BluetoothLeNoninWristOx;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.MedLinket.BluetoothLeMedLinket;
import com.isansys.patientgateway.database.contentprovider.IsansysPatientGatewayContentProvider;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.ArrayList;
import java.util.HashMap;


public class DeviceInfoManager
{
    private final RemoteLogging Log;
    private final String TAG = "DeviceInfoManager";

    private final HashMap<DeviceType, DeviceInfo> device_info_map;

    private final BluetoothLeDeviceController le_device_controller;

    private final ContextInterface context_interface;

    public DeviceInfoManager(ContextInterface context_interface, RemoteLogging logger, TimeSource ntp_time, PatientSessionInfo patient_session_info)
    {
        this.context_interface = context_interface;
        this.Log = logger;

        device_info_map = new HashMap<>();

        le_device_controller = new BluetoothLeDeviceController(context_interface, Log, this, ntp_time, patient_session_info);

        /* Add a null device so that we can use it if a device type isn't in use                                                                    */
        addNonRadioDevice(DeviceType.DEVICE_TYPE__INVALID, SensorType.SENSOR_TYPE__INVALID);

        addNonRadioDevice(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY, SensorType.SENSOR_TYPE__GATEWAY_INFO);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE, SensorType.SENSOR_TYPE__GATEWAY_INFO);

        addBtleDevice(DeviceType.DEVICE_TYPE__LIFETOUCH, SensorType.SENSOR_TYPE__LIFETOUCH,
                new SetupModeInfo(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES, 4096),
                new BluetoothLeLifetouch(context_interface, Log, le_device_controller, ntp_time, DeviceType.DEVICE_TYPE__LIFETOUCH));

        addBtleDevice(DeviceType.DEVICE_TYPE__INSTAPATCH, SensorType.SENSOR_TYPE__LIFETOUCH,
                new SetupModeInfo(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES, 4096),
                new BluetoothLeInstapatch(context_interface, Log, le_device_controller, ntp_time));

        addBtleDevice(DeviceType.DEVICE_TYPE__LIFETOUCH_THREE, SensorType.SENSOR_TYPE__LIFETOUCH,
                new SetupModeInfo(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES, 4096),
                new BluetoothLeLifetouchThree(context_interface, Log, le_device_controller, ntp_time));

        addBtleDevice(DeviceType.DEVICE_TYPE__LIFETEMP_V2, SensorType.SENSOR_TYPE__TEMPERATURE,
                new BluetoothLeLifetempV2(context_interface, Log, le_device_controller, ntp_time));

        addBtClassicDevice(DeviceType.DEVICE_TYPE__FORA_IR20, SensorType.SENSOR_TYPE__TEMPERATURE);

        addBtClassicDevice(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX, SensorType.SENSOR_TYPE__SPO2,
                new SetupModeInfo(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES, 256));

        addBtleDevice(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE, SensorType.SENSOR_TYPE__SPO2,
                new SetupModeInfo(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES, 65536),
                new BluetoothLeNoninWristOx(context_interface, Log, le_device_controller, ntp_time));

        addBtleDevice(DeviceType.DEVICE_TYPE__NONIN_3230, SensorType.SENSOR_TYPE__SPO2,
                new BluetoothLeNonin3230(context_interface, Log, le_device_controller, ntp_time));

        addBtleDevice(DeviceType.DEVICE_TYPE__MEDLINKET, SensorType.SENSOR_TYPE__SPO2,
                new SetupModeInfo(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES, 128),
                new BluetoothLeMedLinket(context_interface, Log, le_device_controller, ntp_time));

        addBtleDevice(DeviceType.DEVICE_TYPE__AND_ABPM_TM2441, SensorType.SENSOR_TYPE__BLOOD_PRESSURE,
                new BluetoothLe_AnD_TM2441(context_interface, Log, le_device_controller, ntp_time));

        addBtleDevice(DeviceType.DEVICE_TYPE__AND_UA651, SensorType.SENSOR_TYPE__BLOOD_PRESSURE,
                new BluetoothLe_AnD_UA651(context_interface, Log, le_device_controller, ntp_time));

        addBtleDevice(DeviceType.DEVICE_TYPE__AND_UA656BLE, SensorType.SENSOR_TYPE__BLOOD_PRESSURE,
                new BluetoothLe_AnD_UA656(context_interface, Log, le_device_controller, ntp_time));

        addBtClassicDevice(DeviceType.DEVICE_TYPE__AND_UA767, SensorType.SENSOR_TYPE__BLOOD_PRESSURE);

        addBtleDevice(DeviceType.DEVICE_TYPE__AND_UC352BLE, SensorType.SENSOR_TYPE__WEIGHT_SCALE,
                new BluetoothLe_AnD_UC352BLE(context_interface, Log, le_device_controller, ntp_time));

        addBtleDevice(DeviceType.DEVICE_TYPE__AND_UA1200BLE, SensorType.SENSOR_TYPE__BLOOD_PRESSURE,
                new BluetoothLe_AnD_UA1200BLE(context_interface, Log, le_device_controller, ntp_time));

        addNonRadioDevice(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE, SensorType.SENSOR_TYPE__ALGORITHM);

        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SPO2, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_DISTRESS, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, SensorType.SENSOR_TYPE__MANUAL_VITAL);
        addNonRadioDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_URINE_OUTPUT, SensorType.SENSOR_TYPE__MANUAL_VITAL);
    }


    private void addNonRadioDevice(DeviceType device_type, SensorType sensor_type)
    {
        // non-radio device
        device_info_map.put(device_type, new DeviceInfo(Log, device_type, sensor_type, new SetupModeInfo()));
    }


    private void addBtClassicDevice(DeviceType device_type, SensorType sensor_type, SetupModeInfo setup_mode_info)
    {
        // Bluetooth Classic
        device_info_map.put(device_type, new BtClassicSensorDevice(Log, device_type, sensor_type, setup_mode_info));
    }

    private void addBtClassicDevice(DeviceType device_type, SensorType sensor_type)
    {
        // Bluetooth Classic no setup mode
        device_info_map.put(device_type, new BtClassicSensorDevice(Log, device_type, sensor_type, new SetupModeInfo()));
    }


    private void addBtleDevice(DeviceType device_type, SensorType sensor_type, SetupModeInfo setup_mode_info, BluetoothLeDevice bluetooth_le_device)
    {
        // BLE
        device_info_map.put(device_type, new BtleSensorDevice(Log, device_type, sensor_type, setup_mode_info, bluetooth_le_device));
    }


    private void addBtleDevice(DeviceType device_type, SensorType sensor_type, BluetoothLeDevice bluetooth_le_device)
    {
        // BLE with no Setup Mode
        device_info_map.put(device_type, new BtleSensorDevice(Log, device_type, sensor_type, new SetupModeInfo(), bluetooth_le_device));
    }


    public void resetAll()
    {
        for (DeviceInfo device_info : device_info_map.values())
        {
            device_info.removeFromPatientSessionAndResetAsNew();
        }
    }


    public DeviceInfo getDeviceInfoFromBluetoothAddress(String bluetooth_address)
    {
        for (DeviceInfo device_info : device_info_map.values())
        {
            if (bluetooth_address.equals(device_info.bluetooth_address))
            {
                return device_info;
            }
        }

        return null;
    }


    public DeviceInfo getDeviceInfoByDeviceType(DeviceType device_type)
    {
        DeviceInfo device_info = device_info_map.get(device_type);

        if(device_info != null)
        {
            return device_info;
        }
        else
        {
            return device_info_map.get(DeviceType.DEVICE_TYPE__INVALID);
        }
    }


    // Returns the first Device Info with a valid Human Readable Device ID for a specific Sensor Type. There should only ever be one Device Type per Sensor Type each session
    // If there are not any then return DEVICE_TYPE__INVALID device info
    public DeviceInfo getDeviceInfoBySensorType(SensorType sensor_type)
    {
        // If there's a valid, active device, return that
        for (DeviceInfo device_info : device_info_map.values())
        {
            if (device_info.sensor_type == sensor_type)
            {
                if (device_info.isDeviceHumanReadableDeviceIdValid())
                {
                    return device_info;
                }
            }
        }

        // If there's a removed device, return that instead
        for (DeviceInfo device_info : device_info_map.values())
        {
            if (device_info.sensor_type == sensor_type)
            {
                if (device_info.isDeviceTypePartOfPatientSession())
                {
                    return device_info;
                }
            }
        }

        // otherwise return the invalid device info
        return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__INVALID);
    }


    private DeviceInfo getDeviceInfoByVitalSignType(VitalSignType vital_sign_type)
    {
        switch(vital_sign_type)
        {
            case HEART_RATE:
            case RESPIRATION_RATE:
                return getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);

            case TEMPERATURE:
                return getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE);

            case SPO2:
                return getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);

            case BLOOD_PRESSURE:
                return getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);

            case WEIGHT:
                return getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE);

            case EARLY_WARNING_SCORE:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE);

            case MANUALLY_ENTERED_HEART_RATE:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE);

            case MANUALLY_ENTERED_RESPIRATION_RATE:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE);

            case MANUALLY_ENTERED_TEMPERATURE:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE);

            case MANUALLY_ENTERED_SPO2:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SPO2);

            case MANUALLY_ENTERED_BLOOD_PRESSURE:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE);

            case MANUALLY_ENTERED_WEIGHT:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT);

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_DISTRESS);

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL);

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN);

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN);

            case MANUALLY_ENTERED_URINE_OUTPUT:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_URINE_OUTPUT);

            // unused, uninitialised or ambiguous vital sign types...
            default:
                return getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__INVALID);
        }
    }


    public boolean isDeviceSessionInProgress(VitalSignType vital_sign_type)
    {
        DeviceInfo device_info = getDeviceInfoByVitalSignType(vital_sign_type);

        return device_info.isDeviceSessionInProgress();
    }


    public ArrayList<Integer> getListOfActiveDeviceSessions()
    {
        ArrayList<Integer> list = new ArrayList<>();

        for (DeviceType device_type : DeviceType.values())
        {
            DeviceInfo device_info = getDeviceInfoByDeviceType(device_type);

            if (device_info.isDeviceSessionInProgress())
            {
                list.add(device_info.getAndroidDeviceSessionId());
            }
        }

        return list;
    }


    public void destroy()
    {
        for (DeviceInfo device_info : device_info_map.values())
        {
            device_info.getSetupModeTimeoutHandler().removeCallbacksAndMessages(null);
            device_info.cancelBluetoothConnectionTimer();
            if(device_info instanceof BtleSensorDevice)
            {
                ((BtleSensorDevice)device_info).cancelBleCommandHandler();
            }
        }

        le_device_controller.onDestroy();
    }


    public ArrayList<DeviceInfo> getListOfSensorDeviceInfoObjects()
    {
        ArrayList<DeviceInfo> list = new ArrayList<>();

        for (DeviceInfo device_info : device_info_map.values())
        {
            if (device_info.isDeviceTypeASensorDevice())
            {
                list.add(device_info);
            }
        }

        return list;
    }


    public ArrayList<DeviceInfo> getListOfNonSensorDeviceInfoObjects()
    {
        ArrayList<DeviceInfo> list = new ArrayList<>();

        for (DeviceInfo device_info : device_info_map.values())
        {
            if (!device_info.isDeviceTypeASensorDevice())
            {
                list.add(device_info);
            }
        }

        return list;
    }


    public ArrayList<BtleSensorDevice> getListOfBtleSensorDeviceInfoObjects()
    {
        ArrayList<BtleSensorDevice> list = new ArrayList<>();

        for (DeviceInfo device_info : device_info_map.values())
        {
            if (device_info.isDeviceTypeABtleSensorDevice())
            {
                list.add((BtleSensorDevice)device_info);
            }
        }

        return list;
    }


    public ArrayList<DeviceInfo> getListOfSensorDeviceInfoObjectsThatSupportSetupMode()
    {
        ArrayList<DeviceInfo> list = new ArrayList<>();

        for (DeviceInfo device_info : device_info_map.values())
        {
            if (device_info.isDeviceTypeASensorDevice())
            {
                if (device_info.deviceSupportsSetupMode())
                {
                    list.add(device_info);
                }
            }
        }

        return list;
    }


    public BluetoothLeDeviceController getBluetoothLeDeviceController()
    {
        return le_device_controller;
    }

    public void enableSetupMode(DeviceType device_type, final boolean enable)
    {
        //TODO long term replace this with a bluetooth_device object
        if (device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX)
        {
            Intent outgoing_intent = new Intent(NoninWristOx.NONIN_WRIST_OX__ENABLE_SETUP_MODE);
            outgoing_intent.putExtra(NoninWristOx.SETUP_MODE_ENABLED, enable);
            context_interface.getAppContext().sendBroadcast(outgoing_intent);
        }
        else
        {
            DeviceInfo device_info = getDeviceInfoByDeviceType(device_type);
            if(device_info.deviceSupportsSetupMode())
            {
                ((BtleSensorDevice)device_info).enableDisableSetupMode(enable);
            }
        }
    }

    // Overloaded version for devices supporting KHz setup mode
    public void enableSetupMode(DeviceType device_type, final boolean enable, final boolean enable_khz_mode)
    {
        DeviceInfo device_info = getDeviceInfoByDeviceType(device_type);
        ((BtleSensorDevice)device_info).enableDisableSetupMode(enable, enable_khz_mode);
    }

    public void enableNightMode(boolean enable)
    {
        ArrayList<BtleSensorDevice> sensor_device_info_objects = getListOfBtleSensorDeviceInfoObjects();
        for (BtleSensorDevice device_info : sensor_device_info_objects)
        {
            device_info.night_mode_enabled = enable;

            if (device_info.isDeviceHumanReadableDeviceIdValid())
            {
                device_info.sendNightModeCommand(enable);
            }
        }
    }


    public JsonObject getAllDeviceInfo()
    {
        JsonObject all_device_infos = new JsonObject();

        for (HashMap.Entry<DeviceType, DeviceInfo> entry : device_info_map.entrySet())
        {
            DeviceInfo device_info = entry.getValue();

            JsonObject this_device = new JsonObject();

            this_device.addProperty("bluetooth_address", device_info.bluetooth_address);
            this_device.addProperty("radio_type", device_info.getRadioType().toString());
            this_device.addProperty("device_name", device_info.device_name);
            this_device.addProperty("desired_device_connection_status", device_info.desired_device_connection_status.toString());
            this_device.addProperty("actual_device_connection_status", device_info.getActualDeviceConnectionStatus().toString());
            this_device.addProperty("measurement_interval_in_seconds", device_info.measurement_interval_in_seconds);
            this_device.addProperty("show_on_ui", device_info.isDeviceTypePartOfPatientSession());
            this_device.addProperty("pairing_pin_number", device_info.pairing_pin_number);
            this_device.addProperty("dummy_data_mode", device_info.dummy_data_mode);
            this_device.addProperty("device_firmware_version", device_info.getDeviceFirmwareVersion());
            this_device.addProperty("device_firmware_string", device_info.getDeviceFirmwareVersionString());
            this_device.addProperty("hardware_version", device_info.hardware_version);
            this_device.addProperty("model", device_info.model);
            this_device.addProperty("last_battery_reading_percentage", device_info.last_battery_reading_percentage);
            this_device.addProperty("last_battery_reading_in_millivolts", device_info.last_battery_reading_in_millivolts);
            this_device.addProperty("start_date_at_midnight_in_milliseconds", device_info.start_date_at_midnight_in_milliseconds);
            this_device.addProperty("last_battery_reading_written_to_database_timestamp", device_info.last_battery_reading_written_to_database_timestamp);
            this_device.addProperty("device_session_start_time", device_info.device_session_start_time);
            this_device.addProperty("device_session_end_time", device_info.device_session_end_time);
            this_device.addProperty("android_database_device_info_id", device_info.android_database_device_info_id);
            this_device.addProperty("android_database_device_session_id", device_info.getAndroidDeviceSessionId());
            this_device.addProperty("setup_mode_time_left_in_seconds", device_info.setup_mode_time_left_in_seconds);
            this_device.addProperty("night_mode_enabled", device_info.night_mode_enabled);
            this_device.addProperty("device_disconnected_from_body", device_info.device_disconnected_from_body);
            this_device.addProperty("last_device_disconnected_timestamp", device_info.last_device_disconnected_timestamp);
            this_device.addProperty("operating_mode", device_info.operating_mode.toString());
            this_device.addProperty("operating_mode_when_disconnected", device_info.getOperatingModeWhenDisconnected().toString());
            this_device.addProperty("no_measurements_detected", device_info.no_measurements_detected);
            this_device.addProperty("bluetooth_connection_counter", device_info.bluetooth_connection_counter);
            this_device.addProperty("desired_bluetooth_search_period_in_milliseconds", device_info.desired_bluetooth_search_period_in_milliseconds);
            this_device.addProperty("counter_leads_off_after_last_valid_data", device_info.counter_leads_off_after_last_valid_data);
            this_device.addProperty("timestamp_leads_off_disconnection", device_info.timestamp_leads_off_disconnection);
            this_device.addProperty("counter_total_leads_off", device_info.counter_total_leads_off);
            this_device.addProperty("manufacture_date", device_info.manufacture_date.toString());
            this_device.addProperty("expiration_date", device_info.expiration_date.toString());
            this_device.addProperty("lot_number", device_info.lot_number);
            this_device.addProperty("current_setup_mode_log_database_id", device_info.current_setup_mode_log_database_id);
            this_device.addProperty("patient_orientation_mode_enabled", device_info.getPatientOrientationModeEnabled());
            this_device.addProperty("patient_orientation_mode_interval_time", device_info.getPatientOrientationModeIntervalTime());
            this_device.addProperty("setup_mode_database_write_in_progress", device_info.setup_mode_database_write_in_progress);

            all_device_infos.add(entry.getKey().toString(), this_device);
        }

        return all_device_infos;
    }


    public boolean isDummyDataModeEnabled()
    {
        for (DeviceInfo device_info : device_info_map.values())
        {
            if (device_info.dummy_data_mode)
            {
                return true;
            }
        }

        return false;
    }

    // separate method in case we want to use this elsewhere
    private boolean isDeviceTypePartOfPatientSession(DeviceType type)
    {
        return getDeviceInfoByDeviceType(type).isDeviceTypePartOfPatientSession();
    }


    /* Normally we get this from a device info object, but in some places it's more convenient to have a single
     * call to the device info manager specifically for EWS.
     */
    public boolean isEarlyWarningScoreDevicePartOfSession()
    {
        return isDeviceTypePartOfPatientSession(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE);
    }
}
