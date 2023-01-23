package com.isansys.patientgateway.deviceInfo;

import android.net.Uri;
import android.os.Handler;
import android.text.format.DateUtils;

import com.isansys.common.enums.DeviceConnectionStatus;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.RadioType;
import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.patientgateway.GenericStartStopTimer;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.isansys.common.DeviceInfoConstants.CONTINUOUS_SETUP_MODE;
import static com.isansys.common.DeviceInfoConstants.INVALID_DEVICE_INFO_ID;
import static com.isansys.common.DeviceInfoConstants.INVALID_DEVICE_SESSION_ID;
import static com.isansys.common.DeviceInfoConstants.INVALID_DEVICE_SESSION_TIME;
import static com.isansys.common.DeviceInfoConstants.INVALID_FIRMWARE_VERSION;
import static com.isansys.common.DeviceInfoConstants.INVALID_HUMAN_READABLE_DEVICE_ID;
import static com.isansys.common.DeviceInfoConstants.INVALID_SETUP_MODE_LOG_DATABASE_ID;
import static com.isansys.common.DeviceInfoConstants.NO_LOT_NUMBER;
import static com.isansys.common.DeviceInfoConstants.SETUP_MODE_LOG_INITIAL_START_TIME;


public class DeviceInfo
{
    protected final String TAG = "DeviceInfo";

    public long human_readable_device_id;
    public String bluetooth_address;
    public final DeviceType device_type;
    public final SensorType sensor_type;
    public String device_name;                                                      // Used ONLY by the Pulse Ox to verify we are talking to the correct device
    public DeviceConnectionStatus desired_device_connection_status;

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

    public boolean isActualDeviceConnectionStatusNotPaired()
    {
        return (actual_device_connection_status == DeviceConnectionStatus.NOT_PAIRED);
    }

    public boolean isActualDeviceConnectionStatusUnexpectedlyUnbonded()
    {
        return (actual_device_connection_status == DeviceConnectionStatus.UNBONDED);
    }


    public int measurement_interval_in_seconds;

    private boolean show_on_ui;

    public String pairing_pin_number = "";

    public boolean dummy_data_mode;

    private int device_firmware_version;                                            // Firmware version reported by the device
    private String device_firmware_string;                                          // String firmware version - not all devices give us an integer value for version so this stores the raw info.

    public int getDeviceFirmwareVersion()
    {
        return device_firmware_version;
    }

    public String getDeviceFirmwareVersionString()
    {
        return device_firmware_string;
    }

    public void setDeviceFirmwareVersion(String device_firmware_version_string, int device_firmware_version)
    {
        Log.d(TAG, "setDeviceFirmwareVersion : DeviceType = " + device_type + " : FW as Int = " + device_firmware_version + " and as String = " + device_firmware_version_string);

        this.device_firmware_version = device_firmware_version;
        this.device_firmware_string = device_firmware_version_string;
    }

    public String hardware_version;
    public String model;

    public int last_battery_reading_percentage;
    public int last_battery_reading_in_millivolts;
    public long last_battery_reading_received_timestamp;
    public long last_battery_reading_written_to_database_timestamp;

    // For the Lifetouch/temp/ox, only the number of milliseconds since midnight get sent to the device. Therefore when saving the incoming measurements timestamp we
    // need to add this time to it to get the real datetime in milliseconds.
    public long start_date_at_midnight_in_milliseconds;

    // For Instapatch/Lifetouch Green, the offset that needs to be applied to the timestamp from the device to get to the real time
    public long timestamp_offset;

    public long device_session_start_time;                                          // When the device was attached to the patient in the session
    // Used by the Lifetouch to detect if Heartbeats are being downloaded from before this session started
    public long device_session_end_time;                                            // Used to allow code to export a Device Session using just this object

    public int android_database_device_info_id;                                     // Android Database unique value to identify this device
    private int android_database_device_session_id;                                  // Android Database unique value to identify this devices current Session

    public int getAndroidDeviceSessionId()
    {
        return android_database_device_session_id;
    }

    public void setAndroidDeviceSessionId(int device_session_id)
    {
        this.android_database_device_session_id = device_session_id;
    }

    private final Handler setup_mode_timeout_handler = new Handler();
    public int setup_mode_time_left_in_seconds;

    public boolean night_mode_enabled;                                      // If night mode enabled for the device. Not all devices use this

    public boolean device_disconnected_from_body;                           // Leads Off state as a common variable rather than lots of individual booleans
    public boolean simulate_device_disconnected_from_body;

    public long last_device_disconnected_timestamp;
    public volatile OperatingMode operating_mode;
    private OperatingMode operating_mode_when_disconnected;

    // Used on Lifetouch to detect is no beats have been detected in 30 seconds
    public boolean no_measurements_detected;

    private Timer bluetooth_connection_timer;
    public int bluetooth_connection_counter;
    public int desired_bluetooth_search_period_in_milliseconds;

    // Count the number of leads-off after last valid data. Used for displaying Nonin disconnection after 10 minutes leads-off detection
    // Value is resetAsNew only after receiving "Valid Data"
    public long counter_leads_off_after_last_valid_data;
    public long timestamp_leads_off_disconnection;

    // Total leads-off for this ble device. Long data type can hold up to (2^63 -1) values.
    public long counter_total_leads_off;

    // These are only valid for devices with DataMatrix codes
    public DateTime manufacture_date = null;
    public DateTime expiration_date = null;
    public String lot_number = "";

    // This is in DeviceInfo because the AND_UC352BLE needs to know the preferred measurement units
    public boolean show_weight_in_lbs = false;

    public enum OperatingMode
    {
        NORMAL_MODE,
        PERIODIC_SETUP_MODE,
        GATEWAY_INITIATED_SETUP_MODE,
        GATEWAY_INITIATED_RAW_ACCELEROMETER_MODE,
        SERVER_INITIATED_SETUP_MODE_STREAMING_DATA,
        SERVER_INITIATED_SETUP_MODE_NOT_STREAMING_DATA,
        SERVER_INITIATED_RAW_ACCELEROMETER_MODE_STREAMING_DATA,
        SERVER_INITIATED_RAW_ACCELEROMETER_MODE_NOT_STREAMING_DATA,
    }

    private final SetupModeInfo setup_mode_info;

    public SetupModeLog set_mode_log_entry;
    public int current_setup_mode_log_database_id;


    // ToDo: these are stored in settings... not sure it makes sense to have orientation mode settings duplicated here and inside the BluetoothLeDevice.
    private boolean patient_orientation_mode_enabled;

    public boolean getPatientOrientationModeEnabled()
    {
        return patient_orientation_mode_enabled;
    }

    public void setPatientOrientationModeEnabled(boolean patient_orientation_mode_enabled)
    {
        this.patient_orientation_mode_enabled = patient_orientation_mode_enabled;
    }

    private int patient_orientation_mode_interval_time;

    public int getPatientOrientationModeIntervalTime()
    {
        return patient_orientation_mode_interval_time;
    }

    public void setPatientOrientationModeIntervalTime(int patient_orientation_mode_interval_time)
    {
        this.patient_orientation_mode_interval_time = patient_orientation_mode_interval_time;
    }

    public void setShowWeightInLbs(boolean in_lbs)
    {
        this.show_weight_in_lbs = in_lbs;
    }

        // Thread safe queue to hold the Setup Mode samples. These are received in the Patient Gateway Service but written to the Database on the 1 second timer (running on another thread)
    public final ConcurrentLinkedQueue<MeasurementSetupModeDataPoint> queue_setup_mode_datapoints;
    public final ConcurrentLinkedQueue<MeasurementSetupModeDataPoint> queue_setup_mode_datapoints_for_server;

    public volatile boolean setup_mode_database_write_in_progress = false;

    protected final RemoteLogging Log;  // I know it's stupid having this in here, but for now it's necessary. ToDo: refactor this to be more sensible.

    @JsonIgnore
    public DeviceInfo(RemoteLogging logger, DeviceType type, SensorType sensor_type, SetupModeInfo setup_mode_info)
    {
        this.device_type = type;
        this.sensor_type = sensor_type;
        this.Log = logger;
        this.setup_mode_info = setup_mode_info;

        this.queue_setup_mode_datapoints = new ConcurrentLinkedQueue<>();
        this.queue_setup_mode_datapoints_for_server = new ConcurrentLinkedQueue<>();

        removeFromPatientSessionAndResetAsNew();
    }


    @JsonIgnore
    public void removeFromPatientSessionAndResetAsNew()
    {
        resetAsNew();

        setDeviceTypePartOfPatientSession(false);
    }


    @JsonIgnore
    public void resetAsNew()
    {
        Log.e(TAG, "resetAsNew for " + this.device_type);

        this.human_readable_device_id = INVALID_HUMAN_READABLE_DEVICE_ID;
        this.bluetooth_address = "";

        // Device type is unchanged

        this.device_name = "";
        this.desired_device_connection_status = DeviceConnectionStatus.NOT_PAIRED;
        this.actual_device_connection_status = DeviceConnectionStatus.NOT_PAIRED;
        this.measurement_interval_in_seconds = 60;
        this.dummy_data_mode = false;

        setDeviceFirmwareVersion("", INVALID_FIRMWARE_VERSION);

        this.hardware_version = "";
        this.model = "";

        this.last_battery_reading_percentage = -1;                                // Used by Lifetouch/temp/ox to keep track of the last received battery reading received
        this.last_battery_reading_in_millivolts = -1;                             // Used by Lifetouch/temp/ox to keep track of the last received battery reading received
        this.last_battery_reading_received_timestamp = -1;
        this.last_battery_reading_written_to_database_timestamp = -1;             // Medlinket sends a battery reading every second

        // For the Lifetouch/temp/ox, only the number of milliseconds since midnight get sent to the device. Therefore when saving the incoming measurements timestamp we
        // need to add this time to it to get the real datetime in milliseconds.
        this.start_date_at_midnight_in_milliseconds = 0;

        // For Instapatch/Lifetouch Green, the offset that needs to be applied to the timestamp from the device to get to the real time
        this.timestamp_offset = 0;

        this.device_session_start_time = INVALID_DEVICE_SESSION_TIME;             // When the device was attached to the patient in the session
        // Used by the Lifetouch to detect if Heartbeats are being downloaded from before this session started
        this.device_session_end_time = INVALID_DEVICE_SESSION_TIME;               // Used to allow code to export a Device Session using just this object

        this.android_database_device_info_id = INVALID_DEVICE_INFO_ID;            // Android Database unique value to identify this device
        setAndroidDeviceSessionId(INVALID_DEVICE_SESSION_ID);                     // Android Database unique value to identify this devices current Session

        resetDeviceInfoSetupModeTimer();

        this.night_mode_enabled = false;                                      // If night mode enabled for the device. Not all devices use this

        this.device_disconnected_from_body = false;                           // Leads Off state as a common variable rather than lots of individual booleans
        this.simulate_device_disconnected_from_body = false;

        this.last_device_disconnected_timestamp = 0;

        this.operating_mode = OperatingMode.NORMAL_MODE;
        this.operating_mode_when_disconnected = this.operating_mode;

        this.counter_leads_off_after_last_valid_data = 0;
        this.timestamp_leads_off_disconnection = 0;
        this.counter_total_leads_off = 0;

        // These are only valid for devices with DataMatrix bar codes. QR codes do not have this info in them
        this.manufacture_date = new DateTime(0);
        this.expiration_date = new DateTime(0);
        this.lot_number = NO_LOT_NUMBER;

        this.set_mode_log_entry = new SetupModeLog();
        this.current_setup_mode_log_database_id = INVALID_SETUP_MODE_LOG_DATABASE_ID;

        desired_bluetooth_search_period_in_milliseconds = 0;
    }


    public boolean isDeviceTypePartOfPatientSession()
    {
        return show_on_ui;
    }


    public void setDeviceTypePartOfPatientSession(boolean part_of_patient_session)
    {
        Log.e(TAG, "setDeviceTypePartOfPatientSession for " + this.device_type + " = " + part_of_patient_session);

        this.show_on_ui = part_of_patient_session;
    }


    public void createSetupModeLogEntry()
    {
        set_mode_log_entry = new SetupModeLog(this.sensor_type, this.device_type, SETUP_MODE_LOG_INITIAL_START_TIME, Long.MIN_VALUE);
    }


    public void setOperatingMode(OperatingMode operating_mode)
    {
        this.operating_mode = operating_mode;
    }


    public boolean isInSetupMode()
    {
        return this.operating_mode == OperatingMode.GATEWAY_INITIATED_SETUP_MODE ||
               this.operating_mode == OperatingMode.SERVER_INITIATED_SETUP_MODE_NOT_STREAMING_DATA ||
               this.operating_mode == OperatingMode.SERVER_INITIATED_SETUP_MODE_STREAMING_DATA ||
               this.operating_mode == OperatingMode.PERIODIC_SETUP_MODE;
    }


    public boolean isInRawAccelerometerMode()
    {
        return this.operating_mode == OperatingMode.GATEWAY_INITIATED_RAW_ACCELEROMETER_MODE ||
               this.operating_mode == OperatingMode.SERVER_INITIATED_RAW_ACCELEROMETER_MODE_NOT_STREAMING_DATA ||
               this.operating_mode == OperatingMode.SERVER_INITIATED_RAW_ACCELEROMETER_MODE_STREAMING_DATA;
    }


    public boolean isInServerInitedRawDataMode()
    {
        return this.operating_mode == OperatingMode.SERVER_INITIATED_SETUP_MODE_STREAMING_DATA ||
               this.operating_mode == OperatingMode.SERVER_INITIATED_RAW_ACCELEROMETER_MODE_STREAMING_DATA;
    }


    public boolean isDeviceHumanReadableDeviceIdValid()
    {
        return human_readable_device_id != INVALID_HUMAN_READABLE_DEVICE_ID;
    }


    public void setLastBatteryReading(int last_battery_reading_percentage, int last_battery_reading_in_millivolts, long last_battery_reading_received_timestamp)
    {
        this.last_battery_reading_percentage = last_battery_reading_percentage;
        this.last_battery_reading_in_millivolts = last_battery_reading_in_millivolts;
        this.last_battery_reading_received_timestamp = last_battery_reading_received_timestamp;
    }


    public boolean isDeviceSessionInProgress()
    {
        return getAndroidDeviceSessionId() != INVALID_DEVICE_SESSION_ID;
    }


    public void saveOperatingModeWhenDisconnected()
    {
        this.operating_mode_when_disconnected = this.operating_mode;
    }


    public OperatingMode getOperatingModeWhenDisconnected()
    {
        return this.operating_mode_when_disconnected;
    }


    public boolean wasDeviceInSetupModeBeforeDisconnection()
    {
        return this.operating_mode_when_disconnected == OperatingMode.GATEWAY_INITIATED_SETUP_MODE ||
               this.operating_mode_when_disconnected == OperatingMode.SERVER_INITIATED_SETUP_MODE_NOT_STREAMING_DATA ||
               this.operating_mode_when_disconnected == OperatingMode.SERVER_INITIATED_SETUP_MODE_STREAMING_DATA ||
               this.operating_mode_when_disconnected == OperatingMode.PERIODIC_SETUP_MODE;
    }


    public boolean wasDeviceInRawAccelerometerModeBeforeDisconnection()
    {
        return this.operating_mode_when_disconnected == OperatingMode.GATEWAY_INITIATED_RAW_ACCELEROMETER_MODE ||
               this.operating_mode_when_disconnected == OperatingMode.SERVER_INITIATED_RAW_ACCELEROMETER_MODE_NOT_STREAMING_DATA ||
               this.operating_mode_when_disconnected == OperatingMode.SERVER_INITIATED_RAW_ACCELEROMETER_MODE_STREAMING_DATA;
    }


    public boolean isDeviceInContinuousSetupMode()
    {
        return setup_mode_time_left_in_seconds == CONTINUOUS_SETUP_MODE;
    }


    public void resetDeviceInfoSetupModeTimer()
    {
        setup_mode_timeout_handler.removeCallbacksAndMessages(null);
        setup_mode_time_left_in_seconds = 0;
    }


    public Handler getSetupModeTimeoutHandler()
    {
        return setup_mode_timeout_handler;
    }


    public boolean deviceSupportsSetupMode()
    {
        return setup_mode_info.supports_setup_mode;
    }


    public Uri getSetupModeUri()
    {
        return setup_mode_info.database_uri;
    }


    public int getMaxSetupModeSampleSize()
    {
        return setup_mode_info.max_sample_size;
    }


    public void cancelBluetoothConnectionTimer()
    {
        GenericStartStopTimer.cancelTimer(bluetooth_connection_timer, Log);

        bluetooth_connection_counter = 0;
    }


    public void makeNewBluetoothConnectionTimerObject()
    {
        bluetooth_connection_timer = new Timer(device_type.toString() + ":bluetooth_connection_timer");
    }


    public Timer getBluetoothConnectionTimer()
    {
        return bluetooth_connection_timer;
    }


    public boolean dummyDataModeRunningAndConnected()
    {
        return dummy_data_mode && isDeviceSessionInProgress() && (actual_device_connection_status == DeviceConnectionStatus.CONNECTED);
    }


    public String getSensorTypeAndDeviceTypeAsString()
    {
        return sensor_type + " : " + device_type;
    }


    public boolean isDeviceTypeASensorDevice()
    {
        return false;
    }


    public boolean isDeviceTypeABtleSensorDevice()
    {
        return false;
    }


    public void getOrSetTimestamp(long timestamp_in_milliseconds)
    {
        Log.d(TAG, "getOrSetTimestamp for device type = " + getSensorTypeAndDeviceTypeAsString());

        handleGeneratedTimestamp(timestamp_in_milliseconds);
    }


    public boolean isStartSessionTimeInvalid()
    {
        return device_session_start_time == INVALID_DEVICE_SESSION_TIME;
    }


    /**
     * Handle a recieved or generated timestamp on device session start.
     * </p>
     * If a device info doesn't yet have a valid device_session_start_time, this handles setting it
     * based on the desired timestamp. Otherwise it's ignored, because the device session was
     * previously started before the new timestamp was generated or received.
     * </p>
     * @param timestamp_in_milliseconds the received or generated timestamp in milliseconds based on the UNIX epoch
     */
    public void handleGeneratedTimestamp(long timestamp_in_milliseconds)
    {
        if(isStartSessionTimeInvalid())
        {
            // Setup calendar to be "timestamp_in_milliseconds"
            Calendar today_at_midnight = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            today_at_midnight.setTimeInMillis(timestamp_in_milliseconds);

            device_session_start_time = today_at_midnight.getTimeInMillis();

            // Set calendar to be midnight today.
            today_at_midnight.set(Calendar.HOUR_OF_DAY, 0);
            today_at_midnight.set(Calendar.MINUTE, 0);
            today_at_midnight.set(Calendar.SECOND, 0);
            today_at_midnight.set(Calendar.MILLISECOND, 0);

            // Save "today at midnight in ms" as will need to add this to each incoming measurement to get a full date/time stamp
            start_date_at_midnight_in_milliseconds = today_at_midnight.getTimeInMillis();
        }
        else
        {
            Log.e(TAG, "handleNewTimestamp : Device ID : " + human_readable_device_id + "timestamp was already valid so not changing it");
        }
    }


    public void parseTimestamp(String data)
    {
        String [] bytes = data.split(" ");

        long millisecond_timestamp = (Long.parseLong(bytes[0], 16) << 24);
        millisecond_timestamp += (Long.parseLong(bytes[1], 16) << 16);
        millisecond_timestamp += (Long.parseLong(bytes[2], 16) << 8);
        millisecond_timestamp += Long.parseLong(bytes[3], 16);

        long unix_timestamp_of_start = (Long.parseLong(bytes[4], 16) << 24);
        unix_timestamp_of_start += (Long.parseLong(bytes[5], 16) << 16);
        unix_timestamp_of_start += (Long.parseLong(bytes[6], 16) << 8);
        unix_timestamp_of_start += Long.parseLong(bytes[7], 16);

        start_date_at_midnight_in_milliseconds = unix_timestamp_of_start * (int)DateUtils.SECOND_IN_MILLIS;

        device_session_start_time = start_date_at_midnight_in_milliseconds + millisecond_timestamp;

        Log.d(TAG, "Parsing midnight at start of device session: " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(start_date_at_midnight_in_milliseconds));
    }


    public RadioType getRadioType()
    {
        return RadioType.RADIO_TYPE__NONE;
    }


    /* Stubs for overriding in specific implementations */
    public void setTestMode(boolean runDevicesInTestMode)
    {
    }

    public void firmwareOtaUpdate(byte[] latestStoredBinary)
    {
    }

    public void dfuProgressReceived(long ntpTimeNowInMilliseconds)
    {
    }

    public void dfuComplete()
    {
    }
}
