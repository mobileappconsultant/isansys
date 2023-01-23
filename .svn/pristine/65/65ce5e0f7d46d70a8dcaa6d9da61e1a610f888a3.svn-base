package com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UC352BLE;

import static com.idevicesinc.sweetblue.BleDeviceState.BONDED;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTED;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING_OVERALL;
import static com.idevicesinc.sweetblue.BleDeviceState.UNBONDED;

import android.content.Intent;
import android.text.format.DateUtils;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceConfig;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.BleNodeConfig;
import com.idevicesinc.sweetblue.utils.Interval;
import com.idevicesinc.sweetblue.utils.Uuids;
import com.isansys.common.ErrorCodes;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.SensorType;

import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/*
Code based on AnD docs available at

https://www.aandd.co.jp/adhome/support/software/sdk_eng/ble/

User ID : wcsdk
Password : cnipnf2430


https://www.aandd.co.jp/support/software/sdk_eng/ble/pdf/sdk_ble_uc-352ble_V1.4_160209-En.pdf
*/


//      multiple measurements in same minute (someone standing on and off and on several times in short time)
//   		in ZOOM meeting - required to store multiple measurements in the database - so maybe just let them get replaced on the Patient vitals display

// Weight measurement units:
// -------------------------
// Kgs are the preferred unit for db storage, only the UI (Gateway, Lifeguard, Database Extractor) need to know about lbs
//      found it's possible for a user to switch scales kg <--> lb with 2 jabs of the pairing button, this class will switch scales back to preferred units if
//      wrong units are detected via setScalesDisplayUnits()

// if timestamp not present, weight measurement could still be processed as a "live" reading using current time ? Maybe not because we cannot be certain enough that
// the measurement is actually from this session.

// IIT-2711 Connection problem - about 1/20 times, after taking a measurement, scales will switch off before Gateway has a chance to re-discover / reconnect

// IIT-2667 Lifeguard:  EWS 0/0 no plot - not Weight scales specific and solution proposed

// IIT-2674 Possible Gateway issue "Waiting for data" after Weight times out, Weights & 1 other device - solution implemented


// test cases:
// https://isansys.testrail.io/index.php?/suites/view/5&group_by=cases:section_id&group_order=asc&display_deleted_cases=0&group_id=5023
//



/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public class BluetoothLe_AnD_UC352BLE extends BluetoothLeDevice
{
    private final static String TAG = BluetoothLe_AnD_UC352BLE.class.getSimpleName();

    private final UUID UUID_SERVICE_PRIMARY_SERVICE = UUID.fromString("233BF000-5A34-1B6D-975C-000D5690ABE4");
        private final UUID UUID_CUSTOM_CHARACTERISTIC = UUID.fromString("233BF001-5A34-1B6D-975C-000D5690ABE4");

    public final static String ACTION_PAIRING = "BluetoothLe_AnD_UC352BLE.ACTION_PAIRING";
    public final static String ACTION_PAIRING_SUCCESS = "BluetoothLe_AnD_UC352BLE.ACTION_PAIRING_SUCCESS";
    public final static String ACTION_PAIRING_FAILURE = "BluetoothLe_AnD_UC352BLE.ACTION_PAIRING_FAILURE";
    public final static String ACTION_CONNECTED = "BluetoothLe_AnD_UC352BLE.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED = "BluetoothLe_AnD_UC352BLE.ACTION_DISCONNECTED";
    public final static String ACTION_UNEXPECTED_UNBOND  = "BluetoothLe_AnD_UC352BLE.ACTION_UNEXPECTED_UNBOND";
    public final static String ACTION_DATA_AVAILABLE = "BluetoothLe_AnD_UC352BLE.ACTION_DATA_AVAILABLE";

    public final static String DATATYPE_WEIGHT_MEASUREMENT = "BluetoothLe_AnD_UC352BLE.MEASUREMENT";
        public final static String WEIGHT = "BluetoothLe_AnD_UC352BLE.WEIGHT";
        public final static String TIMESTAMP = "BluetoothLe_AnD_UC352BLE.TIMESTAMP";
        public final static String DATA_AS_STRING = "DATA_AS_STRING";

    public final static String DATATYPE_BATTERY_LEVEL = "BluetoothLe_AnD_UC352BLE.DATATYPE_BATTERY_LEVEL";
        public final static String BATTERY_PERCENTAGE = "BATTERY_PERCENTAGE";
        public final static String BATTERY_TIMESTAMP = "BATTERY_TIMESTAMP";
        public final static String RECEIVED_TIMESTAMP_AS_LONG = "RECEIVED_TIMESTAMP_AS_LONG";

    public final static String GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED = "GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED";

    private WeightMeasurementUnits preferred_display_units = WeightMeasurementUnits.KILOGRAMS;

    private boolean send_configuration_commands;

    private final long ON_DISCONNECT_RE_SCAN_DELAY = 60 * DateUtils.SECOND_IN_MILLIS;

    private enum WeightMeasurementUnits
    {
        KILOGRAMS,
        LBS
    }


    public BluetoothLe_AnD_UC352BLE(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time)
    {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__AND_UC352BLE);

        device_service_uuid = Uuids.WEIGHT_SCALE_SERVICE_UUID;

        send_configuration_commands = true;
    }


    public String getChildTag()
    {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
    }


    private void updateBatteryLevel()
    {
        Log.d(TAG, "connectToBatteryServiceNotification called");

        ble_device.readBatteryLevel(e -> {
            if (e.type() == BleDevice.ReadWriteListener.Type.READ)
            {
                if (e.wasSuccess())
                {
                    Log.d(TAG, "WEIGHTS BATTERY raw data : " + Utils.byteArrayToHexString(e.data()));

                    byte[] decodedData1 = e.data();

                    int battery_as_int = decodedData1[0];

                    Intent intent = new Intent();
                    intent.setAction(ACTION_DATA_AVAILABLE);
                    intent.putExtra(DATA_TYPE, DATATYPE_BATTERY_LEVEL);
                    intent.putExtra(BATTERY_PERCENTAGE, battery_as_int);
                    intent.putExtra(BATTERY_TIMESTAMP, ntp_time.currentTimeMillis());
                    sendIntentWithDeviceAddressAndDataAsString(intent, e.device().getMacAddress(), e.data());
                }
                else
                {
                    Log.d(TAG, "Weights battery read failed " + e.data_string() + " " + e.gattStatus());
                }
            }
        });
    }


    private void connectToWeightMeasurementServiceNotification()
    {
        Log.d(TAG, "connectToWeightMeasurementServiceNotification called");

        if (isDeviceSessionInProgress())
        {
            if (ble_device != null)
            {
                Log.d(TAG, "connectToWeightMeasurementServiceNotification executing enableNotify");

                ble_device.enableNotify__HighPriority(Uuids.WEIGHT_SCALE_SERVICE_UUID, Uuids.WEIGHT_MEASUREMENT, event -> {
                    logEventStatus(event);

                    switch (event.type())
                    {
                        // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                        case ENABLING_NOTIFICATION:
                        {
                            Log.d(TAG, "ENABLING_NOTIFICATION " + Utils.byteArrayToHexString(event.data()));

                            logEnablingNotification("connectToWeightMeasurementServiceNotification", event);

                            if (!event.wasSuccess() && event.device().is(CONNECTED))
                            {
                                postEnableNotificationRetry();
                            }
                        }
                        break;

                        // Data from the Indication/Notification
                        case INDICATION:
                        case NOTIFICATION:
                        {
                            if (event.wasSuccess())
                            {
                                Log.d(TAG, "WEIGHT_MEASUREMENT raw data : " + Utils.byteArrayToHexString(event.data()));

                                byte[] decodedData = event.data();

                                if (event.data().length == 10)
                                {
                                    long measurement_timestamp_in_ms = parseDateTime(Arrays.copyOfRange(decodedData, 3, 10));

                                    long start_time = PatientGatewayService.device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_UC352BLE).device_session_start_time;

                                    if (measurement_timestamp_in_ms > start_time)
                                    {
                                        int measurement_units_bit = (byte) 1;
                                        int in_kilos = (measurement_units_bit & decodedData[0]);
                                        WeightMeasurementUnits this_readings_units;

                                        if (in_kilos == 0)
                                        {
                                            this_readings_units = WeightMeasurementUnits.KILOGRAMS;
                                        } else
                                        {
                                            this_readings_units = WeightMeasurementUnits.LBS;
                                        }

                                        int weight_scale_measurement = ((256 * decodedData[2]) + (decodedData[1] & 0xff));
                                        double weight_in_kgs = weight_scale_measurement * 0.005d; // scales Kg resolution = 0.005 (Specification Document section 2.1.1)

                                        if (this_readings_units == WeightMeasurementUnits.LBS)
                                        {
                                            double weight_in_lbs = weight_scale_measurement * 0.01d; // scales lb resolution = 0.01 (Specification Document section 2.1.1)
                                            weight_in_kgs = weight_in_lbs * 0.45359237d;
                                        }

                                        Intent intent = new Intent();
                                        intent.setAction(ACTION_DATA_AVAILABLE);
                                        intent.putExtra(DATA_TYPE, DATATYPE_WEIGHT_MEASUREMENT);
                                        intent.putExtra(WEIGHT, weight_in_kgs);
                                        intent.putExtra(TIMESTAMP, measurement_timestamp_in_ms);

                                        sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());

                                        if (this_readings_units != preferred_display_units)
                                        {
                                            Log.d(TAG, "Reading was in different units, this_readings_units:" + this_readings_units + " preferred_display_units:" + preferred_display_units);

                                            setScalesDisplayUnits(preferred_display_units);
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received a measurement but not sending because timestamp is before device was added ( " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_timestamp_in_ms) + " ) " + " start_time:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(start_time));
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "Measurement notification length not valid");
                                }

                                // definitely connected so....
                                setTimestamp();
                                updateBatteryLevel();
                            }
                            else
                            {
                                Log.e(TAG, "WEIGHT_MEASUREMENT read FAILED");
                            }
                        }
                        break;

                        default:
                        {
                            Log.e(TAG, "Default is " + Utils.byteArrayToHexString(event.data()));
                        }
                    }
                });
            }
        }
        else
        {
            Log.e(TAG, "connectToWeightMeasurementServiceNotification - no device session in progress so not connecting");
        }
    }


    private void postEnableNotificationRetry()
    {
        retry_handler.postDelayed(this::connectToWeightMeasurementServiceNotification, DateUtils.SECOND_IN_MILLIS/2);
    }


    public void getTimestamp()
    {
        Log.d(TAG, "getTimestamp()");

        if (ble_device != null)
        {
            // First-time connection, so go through full timestamp and configuration code...
            ble_device.read(Uuids.WEIGHT_SCALE_SERVICE_UUID, Uuids.DATE_TIME, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    long received_timestamp_in_ms = parseDateTime(event.data());
                    long timestamp_now_in_ms = ntp_time.currentTimeMillis();

                    Log.d(TAG, "getTimestamp : SUCCESS : " + Utils.byteArrayToHexString(event.data()) + " " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(received_timestamp_in_ms));

                    final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                    intent.putExtra(DATA_TYPE, DATATYPE_GET_TIMESTAMP);

                    intent.putExtra(RECEIVED_TIMESTAMP_AS_LONG, received_timestamp_in_ms);
                    intent.putExtra(GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED, timestamp_now_in_ms);

                    sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                }
                else
                {
                    Log.e(TAG, "getTimestamp FAILED : " + event.status().toString());

                    if (event.device().is(CONNECTED))
                    {
                        Log.e(TAG, "getTimestamp retrying");

                        postGetTimestampRetry();
                    }
                }
            });
        }
    }


    private void postGetTimestampRetry()
    {
        retry_handler.postDelayed(this::getTimestamp, DateUtils.SECOND_IN_MILLIS/2);
    }


    public void connectToCharacteristics()
    {
        Log.e(TAG, "connectToCharacteristics send_configuration_commands:" + send_configuration_commands);

        if (send_configuration_commands)
        {
            Log.e(TAG, "connectToCharacteristics : send_configuration_commands");

            deleteAllData();

            boolean show_weight_in_lbs = PatientGatewayService.device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE).show_weight_in_lbs;

            if (show_weight_in_lbs)
            {
                preferred_display_units = WeightMeasurementUnits.LBS;
            }
            else
            {
                preferred_display_units = WeightMeasurementUnits.KILOGRAMS;
            }

            Log.d(TAG, "preferred_display_units = " + preferred_display_units);

            setScalesDisplayUnits(preferred_display_units);

            updateBatteryLevel();  // this fails to get an "initial" battery reading on first connection because Rx'ed BluetoothLe_AnD_UC352BLE Battery Level but IGNORED as no Device Session in progress.
        }
        else
        {
            connectToWeightMeasurementServiceNotification();
        }
    }


    // if this succeeds, call connectToCharacteristics()
    private void setTimestamp()
    {
        Date now = new Date(PatientGatewayService.getNtpTimeNowInMillisecondsStatic());

        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTime(now);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);

        Log.d(TAG, "setTimestamp " + hour + ":" + minute + ":" + seconds + " " + day + "/" + month + "/" + year);

        byte[] date_and_time_as_bytes = new byte[7];
        date_and_time_as_bytes[0] = (byte)(year & 0xFF);
        date_and_time_as_bytes[1] = (byte)((year >> 8) & 0xFF);
        date_and_time_as_bytes[2] = (byte)month;
        date_and_time_as_bytes[3] = (byte)day;
        date_and_time_as_bytes[4] = (byte)hour;
        date_and_time_as_bytes[5] = (byte)minute;
        date_and_time_as_bytes[6] = (byte)seconds;

        Log.d(TAG, "writing time bytes " + Utils.byteArrayToHexString(date_and_time_as_bytes));

        ble_device.write(Uuids.WEIGHT_SCALE_SERVICE_UUID, Uuids.DATE_TIME, date_and_time_as_bytes, event ->
        {
            logEventStatus(event);

            if (event.wasSuccess())
            {
                Log.d(TAG, "time written OK " + Utils.byteArrayToHexString(event.data()));
            }
            else
            {
                Log.d(TAG, "time write failed " + Utils.byteArrayToHexString(event.data()));
            }
        });
    }

    @Override
    public void sendConnectedIntentAndContinueConnection()
    {
        Log.d(TAG, "sendConnectedIntentAndContinueConnection  send_configuration_commands:"+ send_configuration_commands );

        sendConnectedIntent();

        getTimestamp();

        connectToCharacteristics();
    }


    @Override
    public void sendDisconnectedIntent()
    {
        Log.d(TAG, "sendDisconnectedIntent()");

        sendIntent(ACTION_DISCONNECTED);
    }


    @Override
    protected void sendRescanRequiredIntent()
    {
        // Not implemented on this device but function needs to exist
    }


    @Override
    protected void sendUnexpectedlyUnbondedIntent()
    {
        sendIntent(ACTION_UNEXPECTED_UNBOND);
    }


    @Override
    public void sendConnectedIntent()
    {
        Log.d(TAG, "CONNECTED");

        sendIntent(ACTION_CONNECTED);
    }


    @Override
    protected String getActionDataAvailableString()
    {
        return ACTION_DATA_AVAILABLE;
    }


    private void sendPairingIntent()
    {
        sendIntent(ACTION_PAIRING);
    }


    private void sendPairingSuccessIntent()
    {
        sendIntent(ACTION_PAIRING_SUCCESS);
    }


    private void sendPairingFailureIntent()
    {
        sendIntent(ACTION_PAIRING_FAILURE);
    }


    @Override
    public void keepAlive()
    {
        // Not implemented on this device but function needs to exist
    }


    @Override
    public boolean checkLastDataIsRestartRequired(long timeout)
    {
        Log.e(TAG, "checkLastDataIsRestartRequired returning FALSE");

        return false;
    }


    public void setMeasurementInterval(int interval_in_seconds)
    {
        // Not implemented on this device but function needs to exist
    }


    public void enableDisableSetupMode(final boolean enter_setup_mode)
    {
        // Not implemented on this device but function needs to exist
    }


    public void sendTestModeCommand(boolean enable)
    {
        // Not implemented on this device but function needs to exist
    }


    public void firmwareOtaUpdate(byte[] firmware_binary_array)
    {
        // Not implemented on this device but function needs to exist
    }


    public void setRadioOff(byte timeTillRadioOff, byte timeWithRadioOff)
    {
        // Not implemented on this device but function needs to exist
    }


    public void enableDisableRawAccelerometerMode(final boolean enter_raw_accelerometer_mode)
    {
        // Not implemented on this device but function needs to exist
    }


    public void sendTurnOffCommand()
    {
        // Not implemented on this device but function needs to exist
    }


    public void sendNightModeCommand(final boolean enable_night_mode)
    {
        // Not implemented on this device but function needs to exist
    }


    public void sendTimestamp(long timestamp_now_in_milliseconds, final byte[] value)
    {
        Log.d(TAG, "sendTimestamp... but actually calling own setTimestamp (not send)");

        setTimestamp();
    }


    private float checkForErrorOrReadFloat(byte first_byte, byte second_byte)
    {
        if (((first_byte & 0xFF) == 0xFF) && ((second_byte & 0xFF) == 0x07))
        {
            // This is a Continua measurement error code
            return ErrorCodes.ERROR_CODE__AND_MEASUREMENT_ERROR;
        }
        else
        {
            return Utils.readSFloat16(first_byte, second_byte);
        }
    }


    @Override
    public void onDiscovered(BleManager.DiscoveryListener.DiscoveryEvent event)
    {
        ble_device = event.device();

        just_discovered = true;

        Log.d(TAG, "onDiscovered UC352BLE : " + ble_device.getMacAddress());

        BleDeviceConfig config = BleDeviceConfig.newNulled(); // get a nulled version, so that it doesn't override any of the ble manager config settings accidentally.
        config.reconnectFilter = new DoNotReconnectFilter();

        // set the non-nullable settings to match the ones we have in the BleDeviceController
        config.autoEnableNotifiesOnReconnect = false;

        // set false if android 9, otherwise true
        config.alwaysBondOnConnect = true;

        ble_device.setConfig(config);

        if (event.was(BleManager.DiscoveryListener.LifeCycle.UNDISCOVERED))
        {
            Log.d(getChildTag(), getDescriptiveDeviceName() + " UNDISCOVERED");
        }
        else if (ble_device.is(CONNECTED, CONNECTING, CONNECTING_OVERALL))
        {
            // Already connected or connecting - do nothing
            Log.d(TAG, "*************** onDiscovered UC352BLE : already connecting or connected");
        }
        else
        {
            // Need to connect
            if (ble_device.is(BONDED))
            {
                // Already bonded, so just re-connect
                Log.d(getChildTag(), "*************** onDiscovered UC352BLE : already BONDED so reconnect");

                executeConnect();
            }
            else if (ble_device.is(UNBONDED))
            {
                // not yet bonded so need to connect, tell gateway service we're going to bond, and set up bonding listener.
                // This lets us feed back to the UI scan page.
                // Actual bonding is handled by sweetblue/android behind the scenes.
                Log.d(getChildTag(), "*************** onDiscovered UC352BLE : BONDING required");

                ble_device.setListener_Bond(m_bond_listener);

                sendPairingIntent();

                executeConnect();
            }
            else
            {
                // do nothing - wait for bonding to finish.
                // In fact this should never happen with the current settings - we only do bonding
                // when triggered as part of a connection so shouldn't be BONDING unless we're also CONNECTING or CONNECTED
                Log.d(getChildTag(), "*************** onDiscovered UC352BLE : pre-connection BONDING in progress");
            }
        }
    }


    public final BleDevice.BondListener m_bond_listener = event ->
    {
        Log.e(getChildTag(), "Sweetblue BondEvent for " + getDescriptiveDeviceName() + "   " + event.toString());

        ble_device = event.device();

        if (event.wasSuccess())
        {
            Log.d(getChildTag(), getDescriptiveDeviceName() + " bonded");

            sendPairingSuccessIntent();
        }
        else
        {
            sendPairingFailureIntent();
        }
    };


    public class DoNotReconnectFilter implements BleNodeConfig.ReconnectFilter
    {
        @Override
        public Please onEvent(ReconnectEvent e)
        {
            just_discovered = false;

            if (isDeviceSessionInProgress() && (!unbonded_during_session))
            {
                device_controller.postBleReScan(ON_DISCONNECT_RE_SCAN_DELAY);

                return Please.stopRetrying();
            }

            if (e.device().is(BONDED))
            {
                // We're on the scan page after a successful connection - should re-try
                return Please.retryIn(Interval.FIVE_SECS);
            }
            else
            {
                // Bonding has failed - allow the user to re-try
                return Please.stopRetrying();
            }
        }
    }


    @Override
    public void resetStateVariables()
    {
        Log.e(TAG, "*********************** resetStateVariables");
        super.resetStateVariables();

        send_configuration_commands = true;
    }


    private void sendReadBufferSizeCommand()
    {
        Log.d(TAG, "sendReadBufferSizeCommand");

                            // Size     Type         Command
        byte[] command = {(byte) 0x02, (byte) 0x00, (byte) 0xD6};

        ble_device.write(UUID_SERVICE_PRIMARY_SERVICE, UUID_CUSTOM_CHARACTERISTIC, command, event -> {
            logEventStatus(event);

            if (event.wasSuccess())
            {
                Log.e(TAG, "sendReadBufferSizeCommand Write successful : Command Response = " + Utils.byteArrayToHexString(event.data()));
            }
            else
            {
                Log.e(TAG, "sendReadBufferSizeCommand Write FAILED : " + event.status().toString());
            }
        });
    }


    private void setScalesDisplayUnits(WeightMeasurementUnits desired_units)
    {
        Log.d(TAG, "setScalesDisplayUnits desired_units:" + desired_units);

        //                        len          type         command      data 0=kilos, 1=lbs
        byte[] command = { (byte) 0x03, (byte) 0x01, (byte) 0x11, (byte) 0x00 };

        if (desired_units == WeightMeasurementUnits.LBS)
        {
            command[3] = (byte) 0x01;
        }

        ble_device.write(UUID_SERVICE_PRIMARY_SERVICE, UUID_CUSTOM_CHARACTERISTIC, command, event -> {
            logEventStatus(event);

            if (event.wasSuccess())
            {
                Log.e(TAG, "setScalesDisplayUnits Write successful : Command Response = " + Utils.byteArrayToHexString(event.data()));
            }
            else
            {
                Log.e(TAG, "setScalesDisplayUnits Write FAILED : " + Utils.byteArrayToHexString(event.data()));
            }
        });
    }


    private void deleteAllData()
    {
        Log.d(TAG, "deleteAllData");

        //Format here is         (size)  (type)       (command)      (data)
        // values are     (3)    (write)  (set buffer size)   (1 = 30 measurements)
        byte[] command = { (byte) 0x05, (byte) 0x01, (byte) 0x12, (byte) 0x02, (byte) 0x01, (byte) 0x12 };

        ble_device.write(UUID_SERVICE_PRIMARY_SERVICE, UUID_CUSTOM_CHARACTERISTIC, command, event -> {
            logEventStatus(event);

            if (event.wasSuccess())
            {
                Log.e(TAG, "deleteAllData Write successful : Command Response = " + Utils.byteArrayToHexString(event.data()));

                send_configuration_commands = false;
            }
            else
            {
                Log.e(TAG, "deleteAllData Write FAILED : " + Utils.byteArrayToHexString(event.data()));
            }
        });
    }
}
