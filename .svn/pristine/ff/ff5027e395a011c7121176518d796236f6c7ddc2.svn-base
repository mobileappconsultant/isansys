package com.isansys.pse_isansysportal.deviceInfo;


import com.isansys.common.enums.RadioType;
import com.isansys.common.enums.SensorType;
import com.isansys.common.enums.DeviceConnectionStatus;
import com.isansys.common.enums.DeviceType;

import org.joda.time.DateTime;

import static com.isansys.common.DeviceInfoConstants.INVALID_DEVICE_SESSION_ID;
import static com.isansys.common.DeviceInfoConstants.INVALID_FIRMWARE_VERSION;
import static com.isansys.common.DeviceInfoConstants.INVALID_HUMAN_READABLE_DEVICE_ID;
import static com.isansys.common.DeviceInfoConstants.NO_LOT_NUMBER;

public class DeviceInfo
{
    public long human_readable_device_id;
    public String bluetooth_address;
    public final DeviceType device_type;
    public RadioType radio_type;
    public SensorType sensor_type;
    public String device_name;                                                             // Only used for Pulse Ox and Blood Pressure
    public int measurement_interval_in_seconds;
    private DeviceConnectionStatus actual_device_connection_status;

    public DeviceConnectionStatus getActualDeviceConnectionStatus()
    {
        return actual_device_connection_status;
    }

    public void setActualDeviceConnectionStatus(DeviceConnectionStatus status)
    {
        this.actual_device_connection_status = status;
    }

    public boolean isActualDeviceConnectionStatusConnected()
    {
        return (actual_device_connection_status == DeviceConnectionStatus.CONNECTED);
    }

    public boolean isActualDeviceConnectionStatusDisconnected()
    {
        return (actual_device_connection_status == DeviceConnectionStatus.DISCONNECTED);
    }

    public boolean scanRequired()
    {
        return ((android_database_device_session_id == INVALID_DEVICE_SESSION_ID)
                || (actual_device_connection_status == DeviceConnectionStatus.UNBONDED));
    }

    public int setup_mode_time_in_seconds;
    private boolean show_on_ui;

    public boolean dummy_data_mode;
    public int firmware_version;
    public String firmware_string;

    public long counter_leads_off_after_last_valid_data;
    public long timestamp_leads_off_disconnection;
    public long counter_total_leads_off;

    // These are only valid for devices with DataMatrix barcodes. QR codes do not have this info in them
    public DateTime manufacture_date;
    public DateTime expiration_date;
    public String lot_number;

    public int last_battery_reading_percentage;
    public int last_battery_reading_in_millivolts;
    public long last_battery_reading_received_timestamp;

    public int android_database_device_session_id;

    private boolean is_device_in_setup_mode;
    private boolean previous_value_of_is_device_in_setup_mode;

    private boolean is_device_in_raw_accelerometer_mode;
    private boolean previous_value_of_is_device_in_raw_accelerometer_mode;

    private boolean is_device_under_server_control;

    private boolean is_device_in_periodic_setup_mode;

    private boolean is_device_off_body;
    private boolean previous_value_of_is_device_off_body;

    private int measurements_pending;

    public int max_setup_mode_sample_size;
    public boolean supports_setup_mode = false;

    public boolean supports_disconnecting_progress_bar = false;

    public boolean nonin_playback_ongoing = false;

    public boolean show_weight_in_lbs = false;

    public boolean supports_battery_info = true;


    public enum OperatingMode
    {
        NORMAL_MODE,
        PERIODIC_SETUP_MODE,
        GATEWAY_INITIATED_SETUP_MODE,
        GATEWAY_INITIATED_RAW_ACCELEROMETER_MODE,
        SERVER_INITIATED_SETUP_MODE,
        SERVER_INITIATED_RAW_ACCELEROMETER_MODE,
    }


    public DeviceInfo(DeviceType type)
    {
        this.device_type = type;

        this.setDeviceTypePartOfPatientSession(false);

        resetAsNew();
    }


    private void resetAsNew()
    {
        this.radio_type = RadioType.RADIO_TYPE__NONE;
        this.sensor_type = SensorType.SENSOR_TYPE__INVALID;

        this.human_readable_device_id = INVALID_HUMAN_READABLE_DEVICE_ID;

        this.actual_device_connection_status = DeviceConnectionStatus.NOT_PAIRED;
        this.measurement_interval_in_seconds = 60;
        this.dummy_data_mode = false;

        this.firmware_version = INVALID_FIRMWARE_VERSION;
        this.firmware_string = "";

        this.android_database_device_session_id = INVALID_DEVICE_SESSION_ID;

        this.setup_mode_time_in_seconds = 3 * 60;

        this.counter_leads_off_after_last_valid_data = 0;
        this.timestamp_leads_off_disconnection = 0;
        this.counter_total_leads_off = 0;

        // These are only valid for devices with DataMatrix barcodes. QR codes do not have this info in them
        this.manufacture_date = new DateTime(0);
        this.expiration_date = new DateTime(0);
        this.lot_number = NO_LOT_NUMBER;

        resetDeviceInSetupModeStates();

        resetDeviceInRawAccelerometerModeStates();

        this.is_device_under_server_control = false;

        this.is_device_in_periodic_setup_mode = false;

        resetDeviceOffBodyStates();

        measurements_pending = 0;

        this.nonin_playback_ongoing = false;

        this.supports_setup_mode = false;
        
        this.supports_disconnecting_progress_bar = false;

        this.supports_battery_info = true;
    }


    public boolean isDeviceTypePartOfPatientSession()
    {
        return show_on_ui;
    }


    public void setDeviceTypePartOfPatientSession(boolean part_of_patient_session)
    {
        this.show_on_ui = part_of_patient_session;
    }


    public boolean isDeviceTypeASensorDevice()
    {
        return radio_type != RadioType.RADIO_TYPE__NONE;
    }


    public boolean isDeviceTypeABtleSensorDevice()
    {
        return radio_type == RadioType.RADIO_TYPE__BTLE;
    }


    public boolean isDeviceHumanReadableDeviceIdValid()
    {
        return human_readable_device_id != INVALID_HUMAN_READABLE_DEVICE_ID;
    }

    public boolean isDeviceSessionInProgress()
    {
        return this.android_database_device_session_id != INVALID_DEVICE_SESSION_ID;
    }


    public void setDeviceInSetupMode(boolean is_device_in_setup_mode)
    {
        this.is_device_in_setup_mode = is_device_in_setup_mode;
    }
    public boolean isDeviceInSetupMode()
    {
        return is_device_in_setup_mode;
    }
    public boolean hasDeviceSetupModeStateChanged()
    {
        return is_device_in_setup_mode != previous_value_of_is_device_in_setup_mode;
    }
    public void updatePreviousValueOfIsDeviceInSetupMode()
    {
        previous_value_of_is_device_in_setup_mode = is_device_in_setup_mode;
    }
    public void resetDeviceInSetupModeStates()
    {
        is_device_in_setup_mode = false;
        previous_value_of_is_device_in_setup_mode = false;
    }


    public void setDeviceInRawAccelerometerMode(boolean is_device_in_raw_accelerometer_mode)
    {
        this.is_device_in_raw_accelerometer_mode = is_device_in_raw_accelerometer_mode;
    }
    public boolean isDeviceInRawAccelerometerMode()
    {
        return is_device_in_raw_accelerometer_mode;
    }
    public boolean hasDeviceRawAccelerometerModeStateChanged()
    {
        return is_device_in_raw_accelerometer_mode != previous_value_of_is_device_in_raw_accelerometer_mode;
    }
    public void updatePreviousValueOfIsDeviceInRawAccelerometerMode()
    {
        previous_value_of_is_device_in_raw_accelerometer_mode = is_device_in_raw_accelerometer_mode;
    }
    public void resetDeviceInRawAccelerometerModeStates()
    {
        is_device_in_raw_accelerometer_mode = false;
        previous_value_of_is_device_in_raw_accelerometer_mode = false;
    }



    public void setDeviceInServerControl(boolean is_device_in_server_initiated_setup_mode)
    {
        this.is_device_under_server_control = is_device_in_server_initiated_setup_mode;
    }
    public boolean isDeviceUnderServerControl()
    {
        return is_device_under_server_control;
    }


    public void setDeviceInPeriodicSetupMode(boolean is_device_in_periodic_setup_mode)
    {
        this.is_device_in_periodic_setup_mode = is_device_in_periodic_setup_mode;
        this.is_device_in_setup_mode = is_device_in_periodic_setup_mode;
    }
    public boolean isDeviceInPeriodicSetupMode()
    {
        return is_device_in_periodic_setup_mode;
    }


    public void setDeviceOffBody(boolean is_device_off_body)
    {
        this.is_device_off_body = is_device_off_body;
    }
    public boolean isDeviceOffBody()
    {
        return is_device_off_body;
    }
    public boolean hasDeviceOffBodyStateChanged()
    {
        return is_device_off_body != previous_value_of_is_device_off_body;
    }
    public void updatePreviousValueOfIsDeviceOffBodyState()
    {
        previous_value_of_is_device_off_body = is_device_off_body;
    }
    public void resetDeviceOffBodyStates()
    {
        is_device_off_body = false;
        previous_value_of_is_device_off_body = false;
    }


    public int getMeasurementsPending()
    {
        return measurements_pending;
    }

    public void setMeasurementsPending(int measurements_pending)
    {
        this.measurements_pending = measurements_pending;
    }

    
    public String getSensorTypeAndDeviceTypeAsString()
    {
        return sensor_type + " : " + device_type;
    }


    public boolean isSensorTypeGatewayInfo()
    {
        return sensor_type == SensorType.SENSOR_TYPE__GATEWAY_INFO;
    }

}
