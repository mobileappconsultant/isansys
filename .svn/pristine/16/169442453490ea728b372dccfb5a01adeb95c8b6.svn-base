package com.isansys.patientgateway.bluetoothLowEnergyDevices;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.format.DateUtils;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceState;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.BleManagerState;
import com.idevicesinc.sweetblue.utils.Uuids;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.GenericStartStopTimer;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.idevicesinc.sweetblue.BleDeviceState.BONDED;
import static com.idevicesinc.sweetblue.BleDeviceState.BONDING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTED;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING_OVERALL;
import static com.idevicesinc.sweetblue.BleDeviceState.RECONNECTING_LONG_TERM;
import static com.idevicesinc.sweetblue.BleDeviceState.UNBONDED;
import static com.idevicesinc.sweetblue.BleDeviceState.UNDISCOVERED;
import static com.isansys.common.DeviceInfoConstants.INVALID_FIRMWARE_VERSION;

/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public abstract class BluetoothLeDevice
{
    protected final RemoteLogging Log;
    protected final ContextInterface gateway_context_interface;

    public BleDevice ble_device;

    public final DeviceType device_type;

    protected final BluetoothLeDeviceController device_controller;

    protected UUID device_service_uuid;

    private boolean firmware_read_required = true;

    protected boolean just_discovered = false;

    protected final TimeSource ntp_time;

    // Go off to the child class to get the TAG
    public abstract String getChildTag();

    protected long last_data_received_ntp_time;

    private boolean dfu_rescan_required = false;

    protected final Random rand;

    protected boolean authenticated;
    private boolean unbond_requested;
    protected boolean unbonded_during_session;

    public final static String DATATYPE_FIRMWARE_REVISION = "DATATYPE_FIRMWARE_REVISION";
    public final static String DATATYPE_GET_TIMESTAMP = "DATATYPE_GET_TIMESTAMP";
    public final static String DATATYPE_BATTERY_LEVEL = "DATATYPE_BATTERY_LEVEL";

    public final static String DATA_AS_STRING = "DATA_AS_STRING";
    public final static String FIRMWARE_VERSION_NUMBER = "FIRMWARE_VERSION_NUMBER";
    public final static String DATA_TYPE = "DATA_TYPE";
    public final static String DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public final static String PACKET_ID = "PACKET_ID";

    protected final DateTimeZone utc_time_zone;
    protected long timezone_offset_to_utc;

    public BluetoothLeDevice(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time, DeviceType device_type)
    {
        Log = logger;

        Log.d(getChildTag(), "BluetoothLeDevice initialize");

        gateway_context_interface = context_interface;

        device_controller = controller;

        last_data_received_ntp_time = 0;

        ntp_time = gateway_time;

        this.device_type = device_type;

        rand = new Random();

        authenticated = false;
        unbond_requested = false;
        unbonded_during_session = false;

        utc_time_zone = DateTimeZone.forID("UTC");
        timezone_offset_to_utc = 0;
    }


    protected String getDescriptiveDeviceName()
    {
        if (ble_device != null)
        {
            return ble_device.getName_override() + " " + ble_device.getMacAddress();
        }
        else
        {
            return "getDescriptiveDeviceName ble_device NULL";
        }
    }


    // Direct copy of DefaultConnectionFailListener with logging added
    class CustomConnectionFailListener implements BleDevice.ConnectionFailListener
    {
        /**
         * The default retry count provided to {@link BleDevice.DefaultConnectionFailListener}.
         * So if you were to call {@link BleDevice#connect()} and all connections failed, in total the
         * library would try to connect {DEFAULT_CONNECTION_FAIL_RETRY_COUNT}+1 times.
         *
         * @see BleDevice.DefaultConnectionFailListener
         */
        static final int DEFAULT_CONNECTION_FAIL_RETRY_COUNT = 1;

        /**
         * The default connection fail limit past which BleDevice.DefaultConnectionFailListener will start returning BleNode.ConnectionFailListener.Please#retryWithAutoConnectTrue().
         */
        static final int DEFAULT_FAIL_COUNT_BEFORE_USING_AUTO_CONNECT = 1;

        private final int m_retryCount;
        private final int m_failCountBeforeUsingAutoConnect;

        CustomConnectionFailListener()
        {
            m_retryCount = DEFAULT_CONNECTION_FAIL_RETRY_COUNT;
            m_failCountBeforeUsingAutoConnect = DEFAULT_FAIL_COUNT_BEFORE_USING_AUTO_CONNECT;
        }

        @Override
        public Please onEvent(ConnectionFailEvent e)
        {
            Log.e(getChildTag(), "ConnectionFailEvent " + getDescriptiveDeviceName() + " : " + e.toString());
            Log.e(getChildTag(), "e.status().allowsRetry() = " + e.status().allowsRetry());

            if(!isDeviceSessionInProgress())
            {
                // We're on the scan page adding the device to a session, and the connection has failed
                // In this case, we want to leave the user in charge, so they can press re-scan

                Log.w(getChildTag(), "Connection Fail while device session not started - defer re-try to the user interface.");

                return Please.doNotRetry();
            }

            if((unbonded_during_session) &&
                    ((device_type == DeviceType.DEVICE_TYPE__AND_UA651)
                            || (device_type == DeviceType.DEVICE_TYPE__AND_UA656BLE)
                            || (device_type == DeviceType.DEVICE_TYPE__AND_ABPM_TM2441)))
            {
// Unsure if needed for UA1200BLE
                // If true, user will re-scan. Do not auto reconnect
                Log.w(getChildTag(), "Connection Fail while AnD device unexpectedly unbonded - defer re-try to the user interface.");

                return Please.doNotRetry();
            }

            if (!e.status().allowsRetry() || e.device().is(RECONNECTING_LONG_TERM))
            {
                if(e.timing() == Timing.IMMEDIATELY)
                {
                    Log.e(getChildTag(), "Likely a weird state with BluetoothGatt returned null, so reconnecting via scan");

                    device_controller.postRemoveDeviceAndRescanForIt(BluetoothLeDevice.this);
                }
                else
                {
                    // If the device is BleDeviceState.RECONNECTING_LONG_TERM then authority is deferred to BleNodeConfig.ReconnectFilter.....which is currently setup to retry every 3 seconds for infinity
                    Log.e(getChildTag(), "Authority is deferred to BleNodeConfig.ReconnectFilter");
                }
                return Please.doNotRetry();
            }

            if (e.failureCountSoFar() <= m_retryCount)
            {
                if (e.failureCountSoFar() >= m_failCountBeforeUsingAutoConnect)
                {
                    Log.e(getChildTag(), "m_failCountBeforeUsingAutoConnect");
                    return Please.retryWithAutoConnectTrue();
                }
                else
                {
                    if (e.status() == Status.NATIVE_CONNECTION_FAILED && e.timing() == Timing.TIMED_OUT)
                    {
                        Log.e(getChildTag(), "Native connect failed due to time out");

                        switch (e.autoConnectUsage())
                        {
                            case USED:
                                Log.e(getChildTag(), "AutoConnectUsage.USED");
                                return Please.retryWithAutoConnectFalse();

                            case NOT_USED:
                                Log.e(getChildTag(), "AutoConnectUsage.NOT_USED");
                                return Please.retryWithAutoConnectTrue();

                            default:
                                Log.e(getChildTag(), "AutoConnectUsage OTHER : Timing.TIMED_OUT");
                                return Please.retry();
                        }
                    }
                    else
                    {
                        Log.e(getChildTag(), "Fail was not a timeout, so retry");
                        return Please.retry();
                    }
                }
            }
            else
            {
                Log.e(getChildTag(), "CustomConnectionFailListener : Exceeded retry count, so not retrying");
                Log.e(getChildTag(), "CustomConnectionFailListener : Rescan for the device via removeDeviceAndRescanForIt");

                device_controller.postRemoveDeviceAndRescanForIt(BluetoothLeDevice.this);

                return Please.doNotRetry();
            }
        }
    }


    public void onDiscovered(BleManager.DiscoveryListener.DiscoveryEvent event)
    {
        ble_device = event.device();

        Log.d(getChildTag(), "BluetoothLeDevice OnDiscovered : " + getDescriptiveDeviceName() + " " + event.lifeCycle());

        if (event.was(BleManager.DiscoveryListener.LifeCycle.UNDISCOVERED))
        {
            Log.d(getChildTag(), getDescriptiveDeviceName() + " UNDISCOVERED");
        }
        else
        {
            Log.d(getChildTag(), getDescriptiveDeviceName() + " discovered");

            executeConnect();
        }
    }


    protected Timer retry_timeout_timer;

    protected final Handler retry_handler = new Handler();

    private void readFirmwareVersion()
    {
        Log.d(getChildTag(), getDescriptiveDeviceName() + " readFirmwareVersion");

        // Create Timer here in case the devices Pending Queue is flushed during a RECONNECTING_SHORT_TERM. See Redmine 1228
        // If the timer fires, then it means we haven't received the FW version within the desired timeout, so call disconnect and start the reconnection again
        startRetryTimeoutHandler();

        ble_device.read(Uuids.DEVICE_INFORMATION_SERVICE_UUID, Uuids.FIRMWARE_REVISION, event -> {
            logEventStatus(event);

            if( event.wasSuccess() )
            {
                firmware_read_required = false;

                GenericStartStopTimer.cancelTimer(retry_timeout_timer, Log);

                String firmware_string = event.data_string();

                Log.i(getChildTag(), getDescriptiveDeviceName() + " FIRMWARE_REVISION is " + firmware_string);

                firmware_string = firmware_string.replace(" ", "");

                int firmware_int = INVALID_FIRMWARE_VERSION;

                try
                {
                    firmware_int = Integer.parseInt(firmware_string);
                }
                catch(Exception e)
                {
                    Log.e(getChildTag(), "Can't parse firmware string: " + e.getMessage());
                }

                sendFirmwareIntent(firmware_string, firmware_int);

                if(event.device().is(BleDeviceState.INITIALIZED))
                {
                    sendConnectedIntentAndContinueConnection();
                }
                else
                {
                    Log.w(getChildTag(), "readFirmwareVersion - FW received but device disconnected");
                }
            }
            else {
                Log.e(getChildTag(), "postDelayedReadFirmwareVersion : Nothing pending so posting runnable for 2 seconds");

                // TEMPORARY FIX FOR MEDLINKET, WHICH DOES NOT SUPPORT FIRMWARE VERSION, IIT-2695
                if (device_type == DeviceType.DEVICE_TYPE__MEDLINKET)
                {
                    Log.d(getChildTag(), "*** using fake firmware version for Medlinket ***");

                    sendFirmwareIntent("????", 2);

                    if (event.device().is(BleDeviceState.INITIALIZED))
                    {
                        sendConnectedIntentAndContinueConnection();
                    }

                    firmware_read_required = false;
                }
                else
                {
                // END OF TEMPORARY FIX FOR MEDLINKET
                    retry_handler.removeCallbacksAndMessages(null);

                    if (ble_device.is(BleDeviceState.INITIALIZED)) {
                        retry_handler.postDelayed(() -> {
                            Log.d(getChildTag(), "reRead_firmware_version_runnable : reading the firmware version again");

                            readFirmwareVersion();
                        }, 2 * DateUtils.SECOND_IN_MILLIS);
                    }
                }
            }
        });
    }


    protected void startRetryTimeoutHandler()
    {
        GenericStartStopTimer.cancelTimer(retry_timeout_timer, Log);

        retry_timeout_timer = new Timer("retry_timeout_timer");
        retry_timeout_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.e(getChildTag(), "Device timed out waiting for operation to succeed. Doing a forceReconnection");
                device_controller.postRemoveDeviceAndRescanForIt(BluetoothLeDevice.this);
            }
        }, 20 * (int)DateUtils.SECOND_IN_MILLIS);
    }


    public abstract void sendTimestamp(long timestamp_now_in_milliseconds, final byte[] value);


    public abstract void getTimestamp();


    // Parse Date Time received on standard Characteristic 0x2A08
    // https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.date_time.xml
    protected long parseDateTime(byte[] payload)
    {
        int index = 0;

        int timestamp_year_low = payload[index++] & 0xFF;
        int timestamp_year_high = payload[index++] & 0xFF;

        int timestamp_year = (timestamp_year_high * 256) | timestamp_year_low;

        int timestamp_month = payload[index++] & 0xFF;
        int timestamp_day = payload[index++] & 0xFF;
        int timestamp_hour = payload[index++] & 0xFF;
        int timestamp_minute = payload[index++] & 0xFF;
        int timestamp_second = payload[index] & 0xFF;

        DateTime timestamp = new DateTime(timestamp_year, timestamp_month, timestamp_day, timestamp_hour, timestamp_minute, timestamp_second, utc_time_zone);

        long time_in_ms = timestamp.getMillis();
        time_in_ms -= timezone_offset_to_utc;

        Log.e(getChildTag(), "parseDateTime : timestamp_year = " + timestamp_year);
        Log.e(getChildTag(), "parseDateTime : timestamp_month = " + timestamp_month);
        Log.e(getChildTag(), "parseDateTime : timestamp_day = " + timestamp_day);
        Log.e(getChildTag(), "parseDateTime : timestamp_hour = " + timestamp_hour);
        Log.e(getChildTag(), "parseDateTime : timestamp_minute = " + timestamp_minute);
        Log.e(getChildTag(), "parseDateTime : timestamp_second = " + timestamp_second);
        Log.e(getChildTag(), "parseDateTime : timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(time_in_ms));

        return time_in_ms;
    }


    // Timestamp sent to standard Date Time characteristic
    // https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.date_time.xml
    protected void writeStandardDateTimeCharacteristic(final long timestamp)
    {
        Log.d(getChildTag(), "writeStandardDateTimeCharacteristic");

        DateTime date_time = new DateTime(timestamp);

        int year = date_time.getYear();
        int month = date_time.getMonthOfYear();
        int day = date_time.getDayOfMonth();
        int hour = date_time.getHourOfDay();
        int minute = date_time.getMinuteOfHour();
        int second = date_time.getSecondOfMinute();

        timezone_offset_to_utc = date_time.getZone().getOffset(timestamp);

        byte[] command = {
                (byte) (year & 0xFF),
                (byte) ((year >> 8) & 0xFF),
                (byte) month,
                (byte) day,
                (byte) hour,
                (byte) minute,
                (byte) second,
        };

        if (ble_device != null)
        {
            ble_device.write(device_service_uuid, Uuids.DATE_TIME, command, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.e(getChildTag(),"writeStandardDateTimeCharacteristic SUCCESS");
                }
                else
                {
                    Log.e(getChildTag(),"writeStandardDateTimeCharacteristic FAILED : " + event.status().toString());

                    if(event.device().is(CONNECTED))
                    {
                        postSetTimestampRetry(timestamp);
                    }
                }
            });
        }
    }


    private void postSetTimestampRetry(final long timestamp)
    {
        retry_handler.postDelayed(() -> writeStandardDateTimeCharacteristic(timestamp), DateUtils.SECOND_IN_MILLIS/2);
    }


    public abstract void sendConnectedIntentAndContinueConnection();


    public abstract void sendConnectedIntent();


    protected abstract String getActionDataAvailableString();


    public abstract void sendDisconnectedIntent();


    protected abstract void sendRescanRequiredIntent();


    protected abstract void sendUnexpectedlyUnbondedIntent();


    public abstract void connectToCharacteristics();


    public abstract void sendTurnOffCommand();


    public abstract void sendNightModeCommand(final boolean enable_night_mode);


    public abstract void enableDisableSetupMode(final boolean enter_setup_mode);

    public void enableDisableSetupMode(final boolean enter_setup_mode, final boolean khz_mode_enabled) {
        enableDisableKHzSetupMode(khz_mode_enabled);
        enableDisableSetupMode(enter_setup_mode);
    }

    public void enableDisableKHzSetupMode(final boolean enable) {
        // Overrideable
    }

    public abstract void sendTestModeCommand(boolean in_test_mode);


    public abstract void setMeasurementInterval(int interval_in_seconds);


    public abstract void firmwareOtaUpdate(byte[] firmware_binary_array);


    public abstract void setRadioOff(byte timeTillRadioOff, byte timeWithRadioOff);


    public abstract void enableDisableRawAccelerometerMode(final boolean enter_raw_accelerometer_mode);


    public abstract void keepAlive();


    public void removeDeviceKeepingBondState()
    {
        undiscover();
        forget();
    }


    public void removeDevice()
    {
        undiscover();
        unbond();
        forget();
    }


    private void disconnect()
    {
        try
        {
            GenericStartStopTimer.cancelTimer(retry_timeout_timer, Log);

            ble_device.disconnect();
        }
        catch(Exception e)
        {
            Log.e(getChildTag(), "disconnect failed with error:");
            Log.e(getChildTag(), "disconnect: " + e);
        }
    }


    private void undiscover()
    {
        // Forcefully undiscover a device, disconnecting it first if needed and removing it from this manager's internal list.
        // BleManager.DiscoveryListener.onEvent(DiscoveryEvent) with BleManager.DiscoveryListener.LifeCycle.UNDISCOVERED will be called.
        try
        {
            GenericStartStopTimer.cancelTimer(retry_timeout_timer, Log);

            if (ble_device != null)
            {
                ble_device.undiscover();
            }
            else
            {
                Log.e(getChildTag(), "undiscover : ble_device is NULL");
            }
        }
        catch(Exception e)
        {
            Log.e(getChildTag(), "undiscover failed with error:");
            Log.e(getChildTag(), "undiscover: " + e);
        }
    }


    private void unbond()
    {
        Log.e(getChildTag(), "unbond called");
        try
        {
            if ((ble_device != null) && (ble_device.is(BONDED, BONDING)))
            {
                unbond_requested = true;
                ble_device.unbond();
            }
            else
            {
                Log.e(getChildTag(), "unbond : ble_device is NULL");
            }
        }
        catch(Exception e)
        {
            Log.e(getChildTag(), "unbond failed with error:");
            Log.e(getChildTag(), "unbond: " + e);
        }
    }


    private void forget()
    {
        try
        {
            if (ble_device != null)
            {
                ble_device.clearAllData();
            }
            else
            {
                Log.e(getChildTag(), "clearAllData : ble_device is NULL");
            }
        }
        catch(Exception e)
        {
            Log.e(getChildTag(), "clearAllData failed with error:");
            Log.e(getChildTag(), "clearAllData: " + e);
        }
    }


    public boolean isFound()
    {
        if(ble_device != null)
        {
            if(just_discovered)
            {
                Log.d(getChildTag(), "isFound: just_discovered = true");
            }

            return (ble_device.isAny(CONNECTING, CONNECTING_OVERALL, CONNECTED) || just_discovered);
        }
        else
        {
            return false;
        }
    }


    public void logEnablingNotification(String function_name, BleDevice.ReadWriteListener.ReadWriteEvent event)
    {
        Log.e(getChildTag(), function_name + " : ENABLING_NOTIFICATION success = " + event.wasSuccess() + " : Data = " + Utils.byteArrayToHexString(event.data()));
    }


    public void logEventStatus(BleDevice.ReadWriteListener.ReadWriteEvent event)
    {
        if (event.wasSuccess())
        {
            Log.d(getChildTag(), "ReadWriteListener : " + event.type().toString() + " : " + event.charUuid().toString() + " : " + event.status().toString() + " : Data = " + Utils.byteArrayToHexString(event.data()));
        }
        else
        {
            Log.e(getChildTag(), "ReadWriteListener event = " + event);
        }
    }


    public boolean checkLastDataIsRestartRequired(long timeout)
    {
        if((ble_device != null) && (ble_device.is(CONNECTED)))
        {
            if((ntp_time.currentTimeMillis() - last_data_received_ntp_time) > timeout)
            {
                Log.d(getChildTag(), "checkLastData - reconnecting");

                return true;
            }
            else
            {
                Log.d(getChildTag(), "checkLastData - data received");

                return false;
            }
        }

        return false;
    }


    public void dfuRescanRequired()
    {
        dfu_rescan_required = true;

        // Also set firmware read required to true, to ensure we get the new FW version
        firmware_read_required = true;
    }


    public void sendFirmwareIntent(String firmware_as_string, int firmware_as_int)
    {
        Intent intent = new Intent(getActionDataAvailableString());
        intent.putExtra(DATA_AS_STRING, firmware_as_string);
        intent.putExtra(FIRMWARE_VERSION_NUMBER, firmware_as_int);
        intent.putExtra(DATA_TYPE, DATATYPE_FIRMWARE_REVISION);
        intent.putExtra(DEVICE_ADDRESS, ble_device.getMacAddress());
        sendIntent(intent);
    }


    public void sendIntentWithDeviceAddressAndDataAsString(Intent intent, String device_address, byte[] data)
    {
        intent.putExtra(DATA_AS_STRING, Utils.byteArrayToHexString(data));
        sendIntentWithDeviceAddress(intent, device_address);
    }


    public void sendIntentWithDeviceAddress(Intent intent, String device_address)
    {
        intent.putExtra(DEVICE_ADDRESS, device_address);
        sendIntent(intent);
    }


    public void sendIntent(Intent intent)
    {
        intent.putExtra("device_type", device_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void sendIntent(String string)
    {
        sendIntent(new Intent(string));
    }


    private boolean device_session_in_progress;

    public void setAndroidDeviceSessionInProgress(boolean device_session_in_progress)
    {
        this.device_session_in_progress = device_session_in_progress;
    }

    public boolean isDeviceSessionInProgress()
    {
        return device_session_in_progress;
    }


    private int firmware_version;

    public int getFirmwareVersion()
    {
        return firmware_version;
    }

    public void setFirmwareVersion(int firmware_version)
    {
        this.firmware_version = firmware_version;
    }


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


    /**
     * Reset flags to do with each individual scan
     */
    public void resetScanVariables()
    {
        just_discovered = false;
        dfu_rescan_required = false;
        authenticated = false;
        unbond_requested = false;
    }


    private void handleUnexpectedUnbond()
    {
        resetScanVariables();

        unbonded_during_session = true;

        sendUnexpectedlyUnbondedIntent();
    }


    public void resetUnbondedDuringSession()
    {
        unbonded_during_session = false;
    }

    /**
     * Reset flags to do with each device session
     */
    public void resetStateVariables()
    {
        firmware_read_required = true;
    }


    protected boolean isAndroidNineOrMore()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }


    protected void executeConnect()
    {
        Log.d(getChildTag(), "executeConnect");

        ble_device.setListener_ConnectionFail(new BluetoothLeDevice.CustomConnectionFailListener());

        just_discovered = true;

        ble_device.connect(event -> {
            Log.e(getChildTag(), "executeConnect : Sweetblue event for " + getDescriptiveDeviceName() + "   " + event.toString());

            just_discovered = false;

            // If the device has reset (non intentionally) then it will show up as a SHORT TERM reconnection...which would not do a Timestamp read normally
            if (event.didExit(BleDeviceState.RECONNECTING_SHORT_TERM))
            {
                Log.d(getChildTag(), "executeConnect : Exited RECONNECTING_SHORT_TERM");

                if ((BleDeviceState.INITIALIZED.bit() & ble_device.getStateMask()) > 0)
                {
                    Log.d(getChildTag(), "executeConnect : sendConnectedIntentAndContinueConnection");

                    // update the last data received time - device has just re-connected so should only
                    // time out if no data received from this point onwards.
                    last_data_received_ntp_time = ntp_time.currentTimeMillis();

                    sendConnectedIntentAndContinueConnection();
                }
            }


            // Has the device fully connected?
            if (event.didEnter(BleDeviceState.INITIALIZED))
            {
                Log.d(getChildTag(), "executeConnect : Entered INITIALIZED");

                // Moved this from here to AFTER reading the Firmware version. This way we ensure that any Firmware Version specific code can be executed straight away
                //context.sendBroadcast(new Intent(ACTION_CONNECTED));

                // update the last data received time - device has just connected so should only
                // time out if no data received from this point onwards.
                last_data_received_ntp_time = ntp_time.currentTimeMillis();

                if (firmware_read_required)
                {
                    Log.i(getChildTag(), getDescriptiveDeviceName() + " just initialized! Queuing up FW read");

                    readFirmwareVersion();
                }
                else
                {
                    Log.i(getChildTag(), getDescriptiveDeviceName() + " just initialized! No need to queue up FW version");

                    sendConnectedIntentAndContinueConnection();
                }
            }

            if (event.didExit(BleDeviceState.INITIALIZED))
            {
                Log.d(getChildTag(), "executeConnect : Left INITIALIZED");

                sendDisconnectedIntent();

                retry_handler.removeCallbacksAndMessages(null);
                GenericStartStopTimer.cancelTimer(retry_timeout_timer, Log);

                authenticated = false;
            }

            if((event.didEnter(UNDISCOVERED)) && (dfu_rescan_required))
            {
                Log.d(getChildTag(), "executeConnect : Entered UNDISCOVERED && dfu_rescan_required");

                sendRescanRequiredIntent();

                dfu_rescan_required = false;
            }

            // Bonding happens before connecting (and unbonding after disconnecting) so if we unbond when we're running a session something has gone wrong
            if (event.didExit(BONDED) && (!unbond_requested))
            {
                Log.d(getChildTag(), "executeConnect : Exit BONDED && !unbond_requested");

                Log.i(getChildTag(), getDescriptiveDeviceName() + " unexpected bonding loss - checking bond state after delay...");

                checkBondStateAfterDelay();
            }
            else if(event.didEnter(BONDED))
            {
                Log.d(getChildTag(), "executeConnect : Entered BONDED");

                resetUnbondedDuringSession();
            }
        });
    }

    protected void checkBondStateAfterDelay()
    {
        retry_handler.postDelayed(() -> {
            if(ble_device.getManager().is(BleManagerState.ON))
            {
                if (ble_device.is(UNBONDED))
                {
                    if(ble_device.getNative().getBondState() == BluetoothDevice.BOND_BONDED)
                    {
                        Log.i(getChildTag(), "checkBondStateAfterDelay : " + getDescriptiveDeviceName() + " device tracked state UNBONDED but natively bonded");
                    }
                    else
                    {
                        Log.i(getChildTag(), "checkBondStateAfterDelay : " + getDescriptiveDeviceName() + " device actually UNBONDED - informing the UI");

                        handleUnexpectedUnbond();
                    }
                }
                else
                {
                    Log.i(getChildTag(), "checkBondStateAfterDelay : " + getDescriptiveDeviceName() + " device re-bonded");
                }
            }
            else
            {
                Log.i(getChildTag(), "checkBondStateAfterDelay : " + getDescriptiveDeviceName() + " checking for unbonding but Bluetooth IS NOT ON - try again after delay");

                checkBondStateAfterDelay();
            }
        }, 5 * DateUtils.SECOND_IN_MILLIS);
    }
}
