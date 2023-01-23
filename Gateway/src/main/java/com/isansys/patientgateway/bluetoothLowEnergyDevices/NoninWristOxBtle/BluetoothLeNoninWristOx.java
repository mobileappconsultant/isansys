package com.isansys.patientgateway.bluetoothLowEnergyDevices.NoninWristOxBtle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceConfig;
import com.idevicesinc.sweetblue.BleManager;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.algorithms.NoninWristOxPlaybackDecoder;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.idevicesinc.sweetblue.BleDeviceState.BONDED;
import static com.idevicesinc.sweetblue.BleDeviceState.BONDING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTED;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING_OVERALL;

/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public class BluetoothLeNoninWristOx extends BluetoothLeDevice
{
    private final static String TAG = BluetoothLeNoninWristOx.class.getSimpleName();

    private final UUID UUID_SERVICE_NONIN_OXIMETRY_SERVICE = UUID.fromString("46A970E0-0D5F-11E2-8B5E-0002A5D5C51B");
        private final UUID UUID_CHARACTERISTIC_NONIN_OXIMETRY_MEASUREMENT = UUID.fromString("0AAD7EA0-0D60-11E2-8E3C-0002A5D5C51B");
        private final UUID UUID_CHARACTERISTIC_NONIN_PULSE_INTERVAL_TIME = UUID.fromString("34E27863-76FF-4F8E-96F1-9E3993AA6199");
        private final UUID UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT = UUID.fromString("1447AF80-0D60-11E2-88B6-0002A5D5C51B");
        private final UUID UUID_CHARACTERISTIC_NONIN_PPG = UUID.fromString("EC0A883A-4D24-11E7-B114-B2F933D5FE66");
        private final UUID UUID_CHARACTERISTIC_NONIN_MEMORY_PLAYBACK = UUID.fromString("EC0A8DDA-4D24-11E7-B114-B2F933D5FE66");
        private final UUID UUID_CHARACTERISTIC_NONIN_DEVICE_STATUS = UUID.fromString("EC0A9302-4D24-11E7-B114-B2F933D5FE66");

    public final static String ACTION_PAIRING = "BluetoothLeNoninWristOx.ACTION_PAIRING";
    public final static String ACTION_PAIRING_SUCCESS = "BluetoothLeNoninWristOx.ACTION_PAIRING_SUCCESS";
    public final static String ACTION_PAIRING_FAILURE = "BluetoothLeNoninWristOx.ACTION_PAIRING_FAILURE";
    public final static String ACTION_CONNECTED = "BluetoothLeNoninWristOx.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED = "BluetoothLeNoninWristOx.ACTION_DISCONNECTED";
    public final static String ACTION_DATA_AVAILABLE = "BluetoothLeNoninWristOx.ACTION_DATA_AVAILABLE";
    public final static String ACTION_NONIN_PLAYBACK_EXPECTED = "BluetoothLeNoninWristOx.ACTION_NONIN_PLAYBACK_EXPECTED";
    public final static String ACTION_NONIN_SETUP_MODE_IS_BLOCKED = "BluetoothLeNoninWristOx.ACTION_NONIN_SETUP_MODE_IS_BLOCKED";
    public final static String ACTION_NONIN_PLAYBACK_IS_BEING_RECEIVED = "BluetoothLeNoninWristOx.ACTION_NONIN_PLAYBACK_IS_BEING_RECEIVED";

    public final static String DATATYPE_SPO2_MEASUREMENT = "BluetoothLeNoninWristOx.MEASUREMENT";
        public final static String NONIN_CONTINUOUS_OXIMETRY__VALID_READING = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__VALID_READING";
        public final static String NONIN_CONTINUOUS_OXIMETRY__BATTERY_VOLTAGE = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__BATTERY_VOLTAGE";
        public final static String NONIN_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE";
        public final static String NONIN_CONTINUOUS_OXIMETRY__PULSE_AMPLITUDE_INDEX = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__PULSE_AMPLITUDE_INDEX";
        public final static String NONIN_CONTINUOUS_OXIMETRY__COUNTER = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__COUNTER";
        public final static String NONIN_CONTINUOUS_OXIMETRY__SPO2 = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__SPO2";
        public final static String NONIN_CONTINUOUS_OXIMETRY__PULSE_RATE = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__PULSE_RATE";
        public final static String NONIN_CONTINUOUS_OXIMETRY__TIMESTAMP = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__TIMESTAMP";
        public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__ENCRYPTION = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__ENCRYPTION";
        public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_BATTERY = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_BATTERY";
        public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT";
        public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__SEARCHING = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__SEARCHING";
        public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__SMART_POINT_ALGORITHM = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__SMART_POINT_ALGORITHM";
        public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_WEAK_SIGNAL = "BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_WEAK_SIGNAL";

    public final static String DATATYPE_DEVICE_STATUS = "BluetoothLeNoninWristOx.DATATYPE_DEVICE_STATUS";
        public final static String NONIN_DEVICE_STATUS__SENSOR_TYPE = "BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__SENSOR_TYPE";
        public final static String NONIN_DEVICE_STATUS__ERROR = "BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__ERROR";
        public final static String NONIN_DEVICE_STATUS__BATTERY_VOLTAGE = "BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__BATTERY_VOLTAGE";
        public final static String NONIN_DEVICE_STATUS__BATTERY_PERCENTAGE = "BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__BATTERY_PERCENTAGE";
        public final static String NONIN_DEVICE_STATUS__TX_INDEX = "BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__TX_INDEX";

    public final static String DATATYPE_PPG_DATA = "DATATYPE_PPG_DATA";
    public final static String PPG_DATA_POINTS = "PPG_DATA_POINTS";

    public final static String RECEIVED_TIMESTAMP_AS_LONG = "RECEIVED_TIMESTAMP_AS_LONG";

    private boolean low_battery;
    private boolean device_detached_from_patient;
    private boolean smartpoint_algorithm;                                                           // Indicates that the data successfully passed the SmartPoint Algorithm

    private final byte SECURITY_CHAR_0 = 'N';
    private final byte SECURITY_CHAR_1 = 'M';
    private final byte SECURITY_CHAR_2 = 'I';

    private final byte ACK = 0x06;

    /**
     * Commands and responses for the nonin.
     *
     * Note the responses are ints, because Java bytes are signed, so give the wrong value
     */
    private final byte OPCODE_ASCII_COMMAND = 0x60;
    private final int OPCODE_ASCII_COMMAND_RESPONSE = OPCODE_ASCII_COMMAND + 0x80;

    private final byte OPCODE_DELETE_BOND = 0x63;

    private final byte OPCODE_SET_NONIN_SECURITY_MODE = 0x64;
    private final int OPCODE_SET_NONIN_SECURITY_MODE_RESPONSE = OPCODE_SET_NONIN_SECURITY_MODE + 0x80;

    private final byte OPCODE_GET_NONIN_SECURITY_MODE = 0x65;
    private final int OPCODE_GET_NONIN_SECURITY_MODE_RESPONSE = OPCODE_GET_NONIN_SECURITY_MODE + 0x80;

    private long time_of_last_reading = -1;
    private long previous_time_of_last_reading = -1; // used if a playback failure is detected to try again

    private final NoninWristOxPlaybackDecoder playback_decoder;

    // This is the ACK from the Nonin Classic which is included with Playback. At present, the Decoder expects this, despite it not being
    // part of the playback on Nonin BLE.
    // TODO: rewrite hardcoded String location numbers in NoninWristOxPlaybackDecoder to remove necessity for "06"
    private final static String NONIN_CLASSIC_ACK = "06 ";

    private String playbackAsString = NONIN_CLASSIC_ACK;

    private boolean sample_storage_rate_needs_setting;

    private static final String END_OF_PLAYBACK_DATA_MARKER = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00";

    private Timer memory_clear_timer;
    private Timer get_timestamp_timer;
    private Timer playback_timeout_timer;
    // Cannot reuse the same task https://stackoverflow.com/questions/43636768/illegal-state-exception-task-already-scheduled-or-cancelled

    private boolean setup_mode_is_blocked = false;

    // This is called when a playback failure has been detected (i.e., playback started but end of data marker not received quickly enough)
    private TimerTask createPlaybackTimerTask()
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Playback timer has timed out (playback has probably failed this time) time_of_last_reading:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(time_of_last_reading));

                playbackAsString = NONIN_CLASSIC_ACK;       // see comment in declaration

                playback_decoder.playbackFailed();

                resetConnectionState();

                // Set time_of_last_reading back to previous time to allow a further attempt at playback
                time_of_last_reading = previous_time_of_last_reading;

                Log.d(TAG, "Reset time_of_last_reading:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(time_of_last_reading));

                Log.d(TAG, "Calling unblockSetupMode from createPlaybackTimerTask()");

                unblockSetupMode();

                // Check again if we need to get playback - if not, subscribe to notifications and keep going as normal
                startPlaybackOrSubscribeToOximeteryNotification();
            }
        };
    }


    // This triggers 10 seconds after the memory clear command has been sent to prevent setup mode starting before the memory has been cleared
    private TimerTask memoryClearTimeout()
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Calling unblockSetupMode from memoryClearTimeout() memory_clear_timer");
                unblockSetupMode();

                memory_clear_timer.cancel();
            }
        };
    }


    // This triggers 10 seconds after the get timestamp command has been sent to prevent misleading "not attached" warnings
    // If a playback occurs, this timer will be cancelled and setup mode unblocking will be handled by memory_clear_timer
    private TimerTask getTimestampTimeout()
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Calling unblockSetupMode from getTimestampTimeout() get_timestamp_timer");
                unblockSetupMode();

                get_timestamp_timer .cancel();
            }
        };
    }


    private final long standard_playback_timeout_ms = 5*DateUtils.SECOND_IN_MILLIS;
    private void startPlaybackTimeout(long added_initial_delay_ms)
    {
        if (playback_timeout_timer != null)
        {
            playback_timeout_timer.cancel();
            playback_timeout_timer.purge();
        }

        playback_timeout_timer = new Timer();
        playback_timeout_timer.schedule(createPlaybackTimerTask(), added_initial_delay_ms + standard_playback_timeout_ms); // 5 secs
    }

    private enum ConnectionStates
    {
        UNKNOWN,
        GET_TIMESTAMP,
        SET_TIMESTAMP,
        GET_ACTIVATION_MODE,
        SET_ACTIVATION_MODE,
        GET_SECURITY_MODE,
        SET_SECURITY_MODE,
        SET_STORAGE_RATE,
        CLEAR_MEMORY,
        GETTING_DATA,
        PLAYBACK_REQUIRED
    }


    private ConnectionStates connection_state;


    private final BroadcastReceiver broadcast_receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(NoninWristOxPlaybackDecoder.NONIN_WRIST_OX__START_PLAYBACK_SIMULATION_FROM_FILE))
            {
                Log.d(TAG, "starting playback simulation from file via NONIN_WRIST_OX__START_PLAYBACK_SIMULATION_FROM_FILE");

                startSimulationOfPlaybackFromFile();
            }
        }
    };


    public BluetoothLeNoninWristOx(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time)
    {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE);

        connection_state = ConnectionStates.UNKNOWN;

        playback_decoder = new NoninWristOxPlaybackDecoder(context_interface, Log);

        gateway_context_interface.getAppContext().registerReceiver(broadcast_receiver, new IntentFilter(NoninWristOxPlaybackDecoder.NONIN_WRIST_OX__START_PLAYBACK_SIMULATION_FROM_FILE));

        sample_storage_rate_needs_setting = true;

        playback_timeout_timer = new Timer();
    }


    private void blockSetupMode()
    {
        Log.d(TAG, "blockSetupMode");

        setup_mode_is_blocked = true;

        informUiThatPlaybackHasStarted(true);

        playback_decoder.informGatewayThatNoninSetupModeIsBlocked();
    }


    private void unblockSetupMode()
    {
        Log.d(TAG, "unblockSetupMode");

        setup_mode_is_blocked = false;

        informUiThatPlaybackHasStarted(false);

        playback_decoder.informGatewayThatNoninSetupModeIsNotBlocked();
    }


    @Override
    public void resetStateVariables()
    {
        Log.d(TAG, "resetStateVariables(), including time_of_last_reading");
        super.resetStateVariables();

        sample_storage_rate_needs_setting = true;

        time_of_last_reading = -1;
        previous_time_of_last_reading = -1;

        setup_mode_is_blocked = false;

        playback_decoder.reset();

        if (playback_timeout_timer != null)
        {
            playback_timeout_timer.cancel();
        }

        if (get_timestamp_timer != null)
        {
            get_timestamp_timer.cancel();
        }

        if (memory_clear_timer != null)
        {
            memory_clear_timer.cancel();
        }
    }


    public String getChildTag()
    {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
    }


    private void informUiThatPlaybackHasStarted(boolean ongoing)
    {
        Log.d(TAG, "informUiThatPlaybackHasStarted " + ongoing);

        Intent intent = new Intent();
        intent.setAction(ACTION_NONIN_PLAYBACK_IS_BEING_RECEIVED);
        intent.putExtra("ongoing", ongoing);

        sendIntent(intent);
    }


    private void informGatewayThatNoninPlaybackIsNotExpected()
    {
        Log.d(TAG, "informGatewayThatNoninPlaybackIsNotExpected");

        Intent intent = new Intent();
        intent.setAction(ACTION_NONIN_PLAYBACK_EXPECTED);
        intent.putExtra("expected", false);

        sendIntent(intent);
    }


    private void informGatewayThatNoninPlaybackIsExpected()
    {
        Log.d(TAG, "informGatewayThatNoninPlaybackIsExpected");

        Intent intent = new Intent();
        intent.setAction(ACTION_NONIN_PLAYBACK_EXPECTED);
        intent.putExtra("expected", true);

        sendIntent(intent);
    }


    private void sendPlaybackCommand()
    {
       Log.d(TAG, "sendPlaybackCommand  " + " time_of_last_reading:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(time_of_last_reading) + " previous_time_of_last_reading:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(previous_time_of_last_reading));

       byte[] memory_playback_command_ble = {(byte) 0x71, (byte) 0x4e, (byte) 0x4d, (byte) 0x49};  // 3150 Memory Playback Document p.8

       connection_state = ConnectionStates.PLAYBACK_REQUIRED; // in case this has been reset to GETTING_DATA by failure to send playback command

       if (ble_device != null)
        {
            ble_device.write(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT, memory_playback_command_ble, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.d(TAG, "sendPlaybackCommand successful " + event.status().toString());

                    time_of_last_reading = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();  // reconnected, previously this was set on receiving an actual reading

                    startPlaybackTimeout(0);
                }
                else
                {
                    time_of_last_reading = previous_time_of_last_reading;

                    connection_state = ConnectionStates.GETTING_DATA; // to allow checkIfPlaybackIsRequired() to get called again

                    Log.d(TAG, "sendPlaybackCommand FAILED : " + event.status().toString() + " so reset time_of_last_reading to " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(time_of_last_reading));
                }
            });
         }
    }

    // For testing
    private void getConfigurationSector()
    {
        Log.d(TAG, "getConfigurationSector  LE");

        byte[] config_sector = { (byte)'C', (byte)'F', (byte)'G', (byte)'?' };

        if (ble_device != null)
        {
            ble_device.write(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT, config_sector, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.d(TAG, " CONFIG  " + event.data().toString());
                }
                else
                {
                    Log.d(TAG, "CONFIG FAILED : " + event.status().toString());
               }
            });
        }
    }


    private void connectToNoninOximetryServiceNotification()
    {
        Log.d(TAG, "connectToNoninOximetryServiceNotification called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToNoninOximetryServiceNotification executing enableNotify");

            ble_device.enableNotify(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_MEASUREMENT, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToNoninOximetryServiceNotification", event);
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if(event.wasSuccess())
                        {
                            Log.d(TAG, "UUID_NONIN_CONTINUOUS_OXIMETRY raw data : " + Utils.byteArrayToHexString(event.data()));

                            byte[] decodedData;
                            decodedData = event.data();

                            last_data_received_ntp_time = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();

                            Log.d(TAG, "last_data_received_ntp_time " + last_data_received_ntp_time + " " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(last_data_received_ntp_time) + " connection_state:" + connection_state);

                            String data = Utils.byteArrayToHexString(decodedData);

                            Log.d(TAG, "decodedData: " + data);

                            /*
                            Byte    Field                           Description
                            0       Length                          The number of bytes used including this one.
                            1       Status                          Indicates the current device status (defined below).
                            2       Battery Voltage                 Voltage level of the batteries in use in 0.1 volt increments.
                            3-4     Pulse Amplitude Index (PAI)     PAI = AC ⁄ DC×100%. Units 0.01%. Range 0.00% to 20.00%
                            5-6     Counter                         Value is incremented on each second from turn-on (between 0 – 65535). Counter can be used to confirm no data loss.
                            7       SpO2                            SpO2 percentage, 0 – 100 (4-beat average as displayed).
                            8-9     Pulse Rate                      Pulse rate in beats per minute, 0 – 321 (4-beat average as displayed).
                            >9      Reserved                        Reserved for future use.
                            */

                            // Status
                            // Bit 7 RFU
                            final byte STATUS__ENCRYPTION = (byte) (1 << 6);
                            final byte STATUS__LOW_BATTERY = (byte) ((1 << 5));
                            final byte STATUS__SENSOR_DISPLACED = (byte) ((1 << 4));
                            final byte STATUS__SEARCHING = (byte) ((1 << 3));
                            final byte STATUS__SMARTPOINT = (byte) ((1 << 2));
                            final byte STATUS__LOW_OR_WEAK_SIGNAL = (byte) ((1 << 1));
                            // Bit 0 RFU

                            /* Battery Voltage
                            For all characteristics on the 3150 BLE, the battery voltage and battery percentage are reported as one of three values that correspond
                            to the battery gauge on the display.

                            Battery Voltage     Battery Percentage      Battery Gauge Display
                            3.3 volts           100%                    Full
                            2.4 volts           50%                     Half
                            1.9 volts           10%                     Low
                            Note: When the Battery voltage is approximately 1.9 volts, the Low Battery bit in the Nonin Continuous Oximetry Status byte will be set high,
                            indicating that the batteries should be replaced soon.
                            */

                            byte length = decodedData[0];

                            byte status = decodedData[1];

                            boolean encryption;
                            encryption = (status & STATUS__ENCRYPTION) > 0;
                            //Log.d(TAG, "Connection encrypted = " + encryption);

                            low_battery = (status & STATUS__LOW_BATTERY) > 0;
                            //Log.d(TAG, "low_battery = " + low_battery);

                            device_detached_from_patient = (status & STATUS__SENSOR_DISPLACED) <= 0;
                            //Log.d(TAG, "Device Off = " + device_detached_from_patient);

                            boolean searching;
                            searching = (status & STATUS__SEARCHING) > 0;
                            //Log.d(TAG, "Oximeter is searching for consecutive pulse signals = " + searching);

                            // If Nonin is in searching state (i.e. it can't find a valid pulse signal) we also consider it to be disconnected from the body.
                            device_detached_from_patient |= searching;

                            if(setup_mode_is_blocked)
                            {
                                // Hard-code leads off to false until configuration and playback are done
                                device_detached_from_patient = false;
                            }


                            smartpoint_algorithm = (status & STATUS__SMARTPOINT) > 0;
                            //Log.d(TAG, "Data successfully passed the SmartPoint Algorithm = " + smartpoint_algorithm);

                            boolean low_weak_signal = (status & STATUS__LOW_OR_WEAK_SIGNAL) > 0;
                            /*
                            if (low_weak_signal)
                            {
                                Log.d(TAG, "Pulse signal strength is 0.3% modulation or less");
                            }
                            */

                            int battery_voltage = (decodedData[2] & 0xFF);
                            //Log.d(TAG, "Battery voltage : " + battery_voltage);

                            int battery_percentage;
                            if (battery_voltage > 24)
                            {
                                battery_percentage = 100;
                            }
                            else
                            {
                                battery_percentage = 50;
                            }

                            if (low_battery)
                            {
                                battery_percentage = 10;
                            }

                            int pulse_amplitude_index = ((decodedData[3] & 0xFF) << 8) | (decodedData[4] & 0xFF);
                            //Log.d(TAG, "pulse_amplitude_index : " + pulse_amplitude_index);

                            int counter = ((decodedData[5] & 0xFF) << 8) | (decodedData[6] & 0xFF);
                            //Log.d(TAG, "counter : " + counter);

                            int spo2 = (decodedData[7] & 0xFF);                                                                                 // Four beat average as displayed
                            //Log.d(TAG, "spo2 : " + spo2);

                            int pulse_rate = ((decodedData[8] & 0xFF) << 8) | (decodedData[9] & 0xFF);                                          // Four beat average as displayed
                            //Log.d(TAG, "pulse_rate : " + pulse_rate);

                            boolean valid_reading = (spo2 != 127) && (pulse_rate != 511);

                            time_of_last_reading = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();

                            Intent intent = new Intent();
                            intent.setAction(ACTION_DATA_AVAILABLE);
                            intent.putExtra(DATA_TYPE, DATATYPE_SPO2_MEASUREMENT);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__VALID_READING, valid_reading);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__SPO2, spo2);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__PULSE_RATE, pulse_rate);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__COUNTER, counter);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__TIMESTAMP, last_data_received_ntp_time);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__PULSE_AMPLITUDE_INDEX, pulse_amplitude_index);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__BATTERY_VOLTAGE, battery_voltage);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE, battery_percentage);

                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__ENCRYPTION, encryption);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_BATTERY, low_battery);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT, device_detached_from_patient);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__SEARCHING, searching);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__SMART_POINT_ALGORITHM, smartpoint_algorithm);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_WEAK_SIGNAL, low_weak_signal);

                            sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());

                            Log.d(TAG, "updating time_of_last_reading:" + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(time_of_last_reading));
                        }
                        else
                        {
                            Log.e(TAG, "UUID_NONIN_CONTINUOUS_OXIMETRY read FAILED");
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


    private void connectToNoninDeviceStatusNotification()
    {
        Log.d(TAG, "connectToNoninDeviceStatusNotification called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToNoninDeviceStatusNotification executing enableNotify");

            ble_device.enableNotify(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_DEVICE_STATUS, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToNoninDeviceStatusNotification", event);
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if(event.wasSuccess())
                        {
                            Log.d(TAG, "UUID_CHARACTERISTIC_NONIN_DEVICE_STATUS raw data : " + Utils.byteArrayToHexString(event.data()));

                            byte[] decodedData;
                            decodedData = event.data();

                            last_data_received_ntp_time = ntp_time.currentTimeMillis();

                            /*
                            Byte    Field                           Description
                            0       Packet Length                   The number of bytes used including this one.
                            1       Sensor Type                     Sensor type being currently used.
                                                                        0x01 - Pulse Oximeter Sensor
                            2       Error                           Current device error
                                                                        0x00 - No Error
                                                                        0x01 - No Sensor
                                                                        0x05 - Sensor Fault
                                                                        0x06 - System Error
                            3       Battery Voltage                 Voltage level of the battery
                            4       Battery Percentage              Estimated battery percentage remaining

                                    For all characteristics on the 3150 BLE, the battery voltage and battery percentage are reported as one of three values that correspond
                                    to the battery gauge on the display.

                                    Battery Voltage     Battery Percentage      Battery Gauge Display
                                    3.3 volts           100%                    Full
                                    2.4 volts           50%                     Half
                                    1.9 volts           10%                     Low
                                    Note: When the Battery voltage is approximately 1.9 volts, the Low Battery bit in the Nonin Continuous Oximetry Status byte will be set high,
                                    indicating that the batteries should be replaced soon.

                            5-6     TX Index                        Incremented each packet
                            */

                            int length = decodedData[0] & 0xFF;
                            int sensor_type = decodedData[1] & 0xFF;
                            int error = decodedData[2] & 0xFF;
                            int battery_voltage = decodedData[3] & 0xFF;
                            int battery_percentage = decodedData[4] & 0xFF;
                            int tx_index = ((decodedData[5] & 0xFF) << 8) | (decodedData[6] & 0xFF);

                            Intent intent = new Intent();
                            intent.setAction(ACTION_DATA_AVAILABLE);
                            intent.putExtra(DATA_TYPE, DATATYPE_DEVICE_STATUS);
                            intent.putExtra(NONIN_DEVICE_STATUS__SENSOR_TYPE, sensor_type);
                            intent.putExtra(NONIN_DEVICE_STATUS__ERROR, error);
                            intent.putExtra(NONIN_DEVICE_STATUS__BATTERY_VOLTAGE, battery_voltage);
                            intent.putExtra(NONIN_DEVICE_STATUS__BATTERY_PERCENTAGE, battery_percentage);
                            intent.putExtra(NONIN_DEVICE_STATUS__TX_INDEX, tx_index);
                            sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                        }
                        else
                        {
                            Log.e(TAG, "UUID_NONIN_CONTINUOUS_OXIMETRY read FAILED");
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


    private void connectToNoninPlaybackNotification()
    {
        Log.d(TAG, "connectToNoninPlaybackNotification called");
        if (ble_device != null)
        {
            //Log.d(TAG, "connectToNoninPlaybackNotification executing enableNotify");

            ble_device.enableNotify(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_MEMORY_PLAYBACK, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION: 
                    {
                        logEnablingNotification("connectToNoninPlaybackNotification", event);

                        Log.d(TAG, "Playback ENABLING_NOTIFICATION : success = " + event.wasSuccess());

                        if (event.wasSuccess())
                        {
                            startPlaybackOrSubscribeToOximeteryNotification();
                        }
                    }
                    break;

                    // Bluetooth version on Tab A is 4.2, SM-T580 specs https://www.sammobile.com/devices/galaxy-tab-a-2016-101-wi-fi/specs/SM-T580/

                    // Data from the Indication/Notification
                    case INDICATION:
                    {
                        if (event.wasSuccess())
                        {
                            Log.d(TAG, "PLAYBACK INDICATION success? " + event.wasSuccess() + " cancelled?" + event.wasCancelled() + " indication:" + Utils.byteArrayToHexString(event.data()));

                            last_data_received_ntp_time = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();

                            playbackAsString += Utils.byteArrayToHexString(event.data());

                            // Checking for the end of data marker
                            int endOfPlaybackLocation = playbackAsString.indexOf(END_OF_PLAYBACK_DATA_MARKER);

                            Log.d(TAG, "endOfPlaybackLocation:" + endOfPlaybackLocation + " playBackAsString length:" + playbackAsString.length());

                            startPlaybackTimeout(2000);

                            if (endOfPlaybackLocation > -1)
                            {
                                String playbackOnlyAsString = playbackAsString.substring(0, endOfPlaybackLocation);

                                if (playback_decoder != null)
                                {
                                    Log.d(TAG, "Calling decodePlayback from UUID_CHARACTERISTIC_NONIN_MEMORY_PLAYBACK INDICATION event");

                                    playback_timeout_timer.cancel();

                                    playback_decoder.newPlaybackSession(() -> {
                                        Log.d(TAG, "playback session complete, connecting to oximetry notification");
                                        connectToNoninOximetryServiceNotification();
                                    });

                                    playback_decoder.decodePlayback(playbackOnlyAsString);

                                    clearMemoryProcedure();

                                    playbackAsString = NONIN_CLASSIC_ACK;  // see comment in declaration
                                }
                            }
                        }
                        else
                        {
                            Log.d(TAG, "PLAYBACK INDICATION success? " + event.wasSuccess() + " cancelled?" + event.wasCancelled() + " indication:" + Utils.byteArrayToHexString(event.data()) + "<<interim");
                        }
                    }
                    break;

                    case NOTIFICATION:
                    {
                        Log.d(TAG, "PLAYBACK NOTIFICATION  success? " + event.wasSuccess() + " cancelled?" + event.wasCancelled());

                        if(event.wasSuccess())
                        {
                            Log.d(TAG, "UUID_CHARACTERISTIC_NONIN_MEMORY_PLAYBACK raw data : " + Utils.byteArrayToHexString(event.data()));
                        }
                        else
                        {
                            Log.e(TAG, "UUID_CHARACTERISTIC_NONIN_MEMORY_PLAYBACK read FAILED");
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

    private void connectToNoninControlPointNotification()
    {
        Log.d(TAG, "connectToNoninControlPointNotification called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToNoninControlPointNotification executing enableNotify");

            ble_device.enableNotify(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 01 00 for a Notification and 02 00 for an Indication
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToNoninControlPointNotification", event);

                        if(isDeviceSessionInProgress())
                        {
                            getTimestampNowResponseCanBeReceived();
                        }
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if(event.wasSuccess())
                        {
                            Log.d(TAG, "UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT. connection_state = " + connection_state + " : Raw data : " + Utils.byteArrayToHexString(event.data()));

                            byte[] decoded_data;
                            decoded_data = event.data();

                            int opcode = decoded_data[0] & 0xFF;
                            int result = decoded_data[1] & 0xFF;

                            if(opcode == OPCODE_ASCII_COMMAND_RESPONSE)
                            {
                                /* Need to switch on connection state, as multiple commands go via the same opcode */
                                switch(connection_state)
                                {
                                    case GET_TIMESTAMP:
                                    {
                                        if (result == 0x00)
                                        {
                                            int data_length = decoded_data[2] & 0xFF;

                                            if (data_length == 0x0C)
                                            {
                                                // Data is encoded as ASCII characters for some stupid reason only Nonin knows
                                                String year = (char) decoded_data[3] + Character.toString((char) decoded_data[4]);
                                                String month = (char) decoded_data[5] + Character.toString((char) decoded_data[6]);
                                                String day = (char) decoded_data[7] + Character.toString((char) decoded_data[8]);
                                                String hour = (char) decoded_data[9] + Character.toString((char) decoded_data[10]);
                                                String minute = (char) decoded_data[11] + Character.toString((char) decoded_data[12]);
                                                String second = (char) decoded_data[13] + Character.toString((char) decoded_data[14]);

                                                DateTime dt = new DateTime();
                                                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyMMddHHmmss");

                                                // Sometimes, Nonin can reset its time to month 0, day 0, minute 0 etc. instead of month 1, day 1 etc. This can cause an IllegalFieldValueException.
                                                // as months need to be in the range 1-12. So catch this and return a valid date long enough in the past so that the Gateway can see that the
                                                // time is wrong and set it correctly. IIT-2039
                                                try
                                                {
                                                    dt = formatter.parseDateTime(year.concat(month).concat(day).concat(hour).concat(minute).concat(second));

                                                    Log.d(TAG, "getTimestamp Command successful. DateTime = " + dt.toString());
                                                }
                                                catch (IllegalFieldValueException ex)
                                                {
                                                    dt = formatter.parseDateTime("100101010101");

                                                    Log.d(TAG, "IllegalFieldValueException: " + ex.toString() + " caught, sending fake value to prompt setting of correct time");
                                                }

                                                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                                                intent.putExtra(DATA_TYPE, DATATYPE_GET_TIMESTAMP);
                                                intent.putExtra(RECEIVED_TIMESTAMP_AS_LONG, dt.getMillis());
                                                sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                                            }
                                            else
                                            {
//*****************
                                                Log.e(TAG, "getTimestamp Command FAILED");
                                            }
                                        }
                                        else
                                        {
//*****************
                                            Log.e(TAG, "getTimestamp Command FAILED");
                                        }

                                    }
                                    break;

                                    case SET_TIMESTAMP:
                                    {
                                        if (result == 0x00)
                                        {
                                            int data_length = decoded_data[2] & 0xFF;
                                            int ack = decoded_data[3] & 0xFF;

                                            if ((ack == ACK) && (data_length == 0x01))
                                            {
                                                Log.d(TAG, "setDateAndTime Command successful");

                                                connectToCharacteristics();

                                                break;
                                            }
                                            else
                                            {
//*****************
                                                Log.e(TAG, "setDateAndTime Command FAILED");
                                            }
                                        }
                                        else
                                        {
//*****************
                                            Log.e(TAG, "setDateAndTime Command FAILED");
                                        }

                                        // handle timestamp failure... read the timestamp again.
                                        getTimestamp();
                                    }
                                    break;

                                    case GET_ACTIVATION_MODE:
                                    {
                                        if (result == 0x00)
                                        {
                                            int data_length = decoded_data[2] & 0xFF;

                                            if (data_length == 0x01)
                                            {
                                                int activation_mode = decoded_data[3] & 0xFF;

                                                Log.d(TAG, "getActivationMode Command successful. activation_mode = " + activation_mode);
//moo
                                                setActivationMode((byte) 0x33);  // hard-coding to spot-check mode as we haven't got the logic in place to set this from gateway settings.
                                                                                 // Spot check activation mode is the only mode that allows the Nonin to automatically switch off 10 seconds after finger removal
                                            }
                                            else
                                            {
                                                Log.e(TAG, "getActivationMode Command FAILED");
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "getActivationMode Command FAILED");
                                        }
                                    }
                                    break;

                                    case SET_ACTIVATION_MODE:
                                    {
                                        if (result == 0x00)
                                        {
                                            int data_length = decoded_data[2] & 0xFF;
                                            int ack = decoded_data[3] & 0xFF;

                                            if ((ack == ACK) && (data_length == 0x01))
                                            {
                                                Log.d(TAG, "setActivationMode Command successful");

                                                getNoninSecurityMode();
                                            }
                                            else
                                            {
                                                Log.e(TAG, "setActivationMode Command FAILED");
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "setActivationMode Command FAILED");
                                        }
                                    }
                                    break;

                                    case SET_STORAGE_RATE:
                                    {
                                        if (result == 0x00)
                                        {
                                            int data_length = decoded_data[2] & 0xFF;
                                            int ack = decoded_data[3] & 0xFF;

                                            if ((ack == ACK) && (data_length == 0x01))
                                            {
                                                Log.d(TAG, "setStorageRate Command successful");

                                                clearMemoryProcedure();
                                            }
                                            else
                                            {
                                                Log.d(TAG, "setStorageRate Command FAILED");
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "setStorageRate Command FAILED");
                                        }
                                    }
                                    break;

                                    case CLEAR_MEMORY:
                                    {
                                        if (result == 0x00)
                                        {
                                            int data_length = decoded_data[2] & 0xFF;
                                            int ack = decoded_data[3] & 0xFF;

                                            if ((ack == ACK) && (data_length == 0x01))
                                            {
                                                Log.d(TAG, "clearMemoryProcedure Command successful");

                                                startGettingData();
                                            }
                                            else
                                            {
                                                Log.e(TAG, "clearMemoryProcedure Command FAILED");
                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                                /* opcode specific results */
                                switch(opcode)
                                {
                                    case OPCODE_GET_NONIN_SECURITY_MODE_RESPONSE:
                                    {
                                        Log.d(TAG, "getNoninSecurityMode result received. Mode = " + result);

                                        setNoninSecurityMode();
                                    }
                                    break;

                                    case OPCODE_SET_NONIN_SECURITY_MODE_RESPONSE:
                                    {
                                        if (result == 0x00)
                                        {
                                            Log.d(TAG, "setNoninSecurityMode Command successful");

                                            if (sample_storage_rate_needs_setting)
                                            {
                                                Log.d(TAG, "Sample storage rate needs setting");

                                                setStorageRate();
                                            }
                                            else
                                            {
                                                Log.d(TAG, "sample storage does not need setting");

                                                startGettingData();
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "setNoninSecurityMode Command FAILED");
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        else
                        {
                            Log.e(TAG, "UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT FAILED");
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


    private enum SetupModeTimestampTracking
    {
        NOT_SET,
        SET,
    }

    private SetupModeTimestampTracking setupModeTimestampTracking;

    private double setup_mode_sample_timestamp;
    private int expected_setup_mode_packet_count;


    private void connectToNoninPpgNotification(boolean enable)
    {
        Log.d(TAG, "connectToNoninPpgNotification called");

        if (ble_device != null)
        {
            if (enable)
            {
                Log.d(TAG, "connectToNoninPpgNotification executing enableNotify");

                setupModeTimestampTracking = SetupModeTimestampTracking.NOT_SET;
                expected_setup_mode_packet_count = -1;

                ble_device.enableNotify(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_PPG, event -> {
                    logEventStatus(event);

                    switch (event.type())
                    {
                        // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 01 00 for a Notification and 02 00 for an Indication
                        case ENABLING_NOTIFICATION:
                        {
                            logEnablingNotification("connectToNoninPpgNotification", event);
                        }
                        break;

                        // Data from the Indication/Notification
                        case INDICATION:
                        case NOTIFICATION:
                        {
                            if(event.wasSuccess())
                            {
                                byte[] decodedData;
                                decodedData = event.data();

                                last_data_received_ntp_time = ntp_time.currentTimeMillis();

                                byte length = decodedData[0];

                                int top_byte = (decodedData[51] & 0xFF) << 8;
                                int bottom_byte = decodedData[52] & 0xFF;
                                int packet_counter = top_byte | bottom_byte;

                                final int packet_counter_max = 65535;

                                // Nonin is sampling at 75 Hz
                                final double TIME_BETWEEN_SAMPLES = (double)DateUtils.SECOND_IN_MILLIS / 75;

                                Log.d(TAG, "UUID_CHARACTERISTIC_NONIN_PPG : Packet Counter = " + packet_counter + " : Length = " + length + " : Data = " + Utils.byteArrayToHexString(event.data()));

                                switch (setupModeTimestampTracking)
                                {
                                    case NOT_SET:
                                    {
                                        // Use the timestamp of the very first Setup Mode packet as the initial timestamp.
                                        setup_mode_sample_timestamp = ntp_time.currentTimeMillis();

                                        setupModeTimestampTracking = SetupModeTimestampTracking.SET;
                                        expected_setup_mode_packet_count = (packet_counter + 1) % packet_counter_max;
                                    }
                                    break;

                                    case SET:
                                    {
                                        // Initial timestamp has been received from the first setup mode packet in this setup mode run.

                                        // Need to check that the received packet counter is what we expect it to be. If the Nonin is on the edge of range, we might miss packets but Sweetblue will
                                        // still class it as connected.

                                        if (expected_setup_mode_packet_count != packet_counter)
                                        {
                                            Log.e(TAG, "UUID_CHARACTERISTIC_NONIN_PPG : expected_setup_mode_packet_count = " + expected_setup_mode_packet_count + " but packet_counter = " + packet_counter);

                                            // Need to see how many packets are missing. E.g. we last received packet 65534 and now have received packet 2 (16 bit roll over)
                                            // So 65535, 0 and 1 were missing
                                            // Need to adjust setup_mode_sample_timestamp for this
                                            int number_of_packets_missed = ((packet_counter + packet_counter_max) - expected_setup_mode_packet_count) % packet_counter_max;

                                            // 25 samples per packet
                                            double time_in_milliseconds_missed = number_of_packets_missed * (TIME_BETWEEN_SAMPLES * 25);

                                            Log.e(TAG, "UUID_CHARACTERISTIC_NONIN_PPG : number_of_packets_missed = " + number_of_packets_missed + " : time_in_milliseconds_missed = " + time_in_milliseconds_missed);

                                            setup_mode_sample_timestamp += time_in_milliseconds_missed;
                                        }

                                        expected_setup_mode_packet_count++;
                                    }
                                    break;
                                }

                                ArrayList<MeasurementSetupModeDataPoint> data_points = new ArrayList<>();

                                for (int i=1; i<length - 2;)
                                {
                                    top_byte = (decodedData[i++] & 0xFF) << 8;
                                    bottom_byte = decodedData[i++] & 0xFF;

                                    // 16 bit data
                                    int sample = top_byte | bottom_byte;

                                    data_points.add(new MeasurementSetupModeDataPoint(sample, (long)setup_mode_sample_timestamp));

                                    setup_mode_sample_timestamp = setup_mode_sample_timestamp + TIME_BETWEEN_SAMPLES;
                                }

                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList(PPG_DATA_POINTS, data_points);

                                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                                intent.putExtra(DATA_TYPE, DATATYPE_PPG_DATA);
                                intent.putExtras(bundle);
                                sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                            }
                            else
                            {
                                Log.e(TAG, "UUID_CHARACTERISTIC_NONIN_PPG read FAILED");
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
            else
            {
                Log.d(TAG, "connectToNoninPpgNotification executing disableNotify");

                ble_device.disableNotify(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_PPG, event -> {
                    logEventStatus(event);

                    switch (event.type())
                    {
                        // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 01 00 for a Notification and 02 00 for an Indication
                        case ENABLING_NOTIFICATION:
                        {
                            logEnablingNotification("disconnectToNoninPpgNotification", event);
                        }
                        break;

                        // Data from the Indication/Notification
                        case INDICATION:
                        case NOTIFICATION:
                        {
                            if(event.wasSuccess())
                            {
                                Log.d(TAG, "Disconnected from UUID_CHARACTERISTIC_NONIN_PPG raw data : " + Utils.byteArrayToHexString(event.data()));
                            }
                            else
                            {
                                Log.e(TAG, "Disconnected from UUID_CHARACTERISTIC_NONIN_PPG read FAILED");
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
    }


    public void deleteBond()
    {
        Log.d(TAG, "Sending the Delete ALL Bonds command");

        byte[] delete_bond_command = {OPCODE_DELETE_BOND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2, 0x00};

        if (ble_device != null)
        {
            ble_device.write(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT, delete_bond_command, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.e(TAG, "deleteBond Write successful");

                    byte[] decoded_data;
                    decoded_data = event.data();

                    int opcode = decoded_data[0] & 0xFF;
                    int result = decoded_data[1] & 0xFF;

                    if ((opcode == (OPCODE_DELETE_BOND + 0x80)) && (result == 0x00))
                    {
                        Log.d(TAG, "deleteBond Command successful");
                    }
                    else
                    {
                        Log.e(TAG, "deleteBond Command FAILED");
                    }
                }
                else
                {
                    Log.e(TAG, "deleteBond FAILED : " + event.status().toString());
                }
            });
        }
    }


    private void setNoninSecurityMode()
    {
        Log.d(TAG, "setNoninSecurityMode");

        // 0x01 is Security Mode 1. In Security Mode 1, the 3150 BLE allows the creation of a new bond during each connection without requiring battery removal and insertion
        byte[] command = {OPCODE_SET_NONIN_SECURITY_MODE, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2, 0x01};

        if (ble_device != null)
        {
            connection_state = ConnectionStates.SET_SECURITY_MODE;

            writeCommandToNoninOximetryControlPoint(command, "setNoninSecurityMode");
        }
    }


    private void getNoninSecurityMode()
    {
        Log.d(TAG, "getNoninSecurityMode");

        byte[] command = {OPCODE_GET_NONIN_SECURITY_MODE, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2};

        if (ble_device != null)
        {
            connection_state = ConnectionStates.GET_SECURITY_MODE;

            writeCommandToNoninOximetryControlPoint(command, "getNoninSecurityMode");
        }
    }


    private void writeCommandToNoninOximetryControlPoint(byte[] command, final String callingFunctionName)
    {
        Log.e(TAG, "writeCommandToNoninOximetryControlPoint : Outgoing command = " + Utils.byteArrayToHexString(command) + " " + callingFunctionName);

        ble_device.write(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT, command, event -> {
            logEventStatus(event);

            if (!event.wasSuccess())
            {
                Log.e(TAG, callingFunctionName + " FAILED : " + event.status().toString());

                resetConnectionState();
            }
            else
            {
                Log.e(TAG, callingFunctionName + " DID NOT FAIL : " + event.status().toString());
            }
        });
    }


    private boolean startPlaybackIfRequired()
    {
        Log.d(TAG, "startPlaybackIfRequired() ongoing? " + playback_decoder.isPlaybackOngoing() + " setup_mode_is_blocked:" + setup_mode_is_blocked + " connection_state:" + connection_state);

        if (time_of_last_reading != -1 && PatientGatewayService.getNtpTimeNowInMillisecondsStatic() - time_of_last_reading > 60000 && !playback_decoder.isPlaybackOngoing())
        {
            Log.d(TAG, "startPlaybackIfRequired >> playback required <<  time_of_last_reading:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(time_of_last_reading) + " lastDataRecTime:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(last_data_received_ntp_time));

            // cancel get_timestamp_timer because setup mode should now be unblocked by end of playback
            if (get_timestamp_timer != null)
            {
                Log.d(TAG, "startPlaybackIfRequired : cancelling get_timestamp_timer");
                get_timestamp_timer.cancel();
            }

            Log.d(TAG, "startPlaybackIfRequired : calling blockSetupMode");
            blockSetupMode();

            playback_decoder.setTimeOfLastReading(time_of_last_reading);

            playbackAsString = NONIN_CLASSIC_ACK;   // see comment in declaration

            connection_state = ConnectionStates.PLAYBACK_REQUIRED;

            previous_time_of_last_reading = time_of_last_reading;

            sendPlaybackCommand();

            return true;
        }

        return false;
    }


    private void resetConnectionState()
    {
        Log.d(TAG, "resetConnectionState()");

        connection_state = ConnectionStates.GET_TIMESTAMP;
    }


    public void getTimestamp()
    {
        // Only once the connection to the "response" indication is established will commands be set. This in turn calls the real getTimestamp code
        connectToNoninControlPointNotification();
    }


    private void getTimestampNowResponseCanBeReceived()
    {
        Log.d(TAG, "getTimestampNowResponseCanBeReceived");

        byte[] command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x06, 'D', 'T', 'M', '?',
                0x0D, 0xA
        };

        if (ble_device != null)
        {
            connection_state = ConnectionStates.GET_TIMESTAMP;

            writeCommandToNoninOximetryControlPoint(command, "getTimestamp");

            Log.d(TAG, "creating get_timestamp_timer");
            get_timestamp_timer = new Timer();
            get_timestamp_timer.schedule(getTimestampTimeout(), 10*DateUtils.SECOND_IN_MILLIS ); // 10 secs

            blockSetupMode();
        }
    }

    /*
    0x31 – Sensor Activation
    0x32 – Programmed Activation
    0x33 – Spot-Check Activation
    0x34 – Bluetooth Connection Activation
    */
    private void setActivationMode(byte activation_mode)
    {
        Log.d(TAG, "setActivationMode");

        byte[] command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x07, 'A', 'C', 'T', '=',
                activation_mode,
                0x0D, 0xA
        };

        if (ble_device != null)
        {
            connection_state = ConnectionStates.SET_ACTIVATION_MODE;

            writeCommandToNoninOximetryControlPoint(command, "setActivationMode");
        }
    }


    private void getActivationMode()
    {
        Log.d(TAG, "getActivationMode");

        byte[] command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x06, 'A', 'C', 'T', '?',
                0x0D, 0xA
        };

        if (ble_device != null)
        {
            connection_state = ConnectionStates.GET_ACTIVATION_MODE;

            writeCommandToNoninOximetryControlPoint(command, "getActivationMode");
        }
    }


/*
    0x31 – Full Display
    0x32 – Partial Display
    0x33 – MVI Display
 */
    public void setDisplayMode(byte display_mode)
    {
        Log.d(TAG, "setDisplayMode");

        byte[] command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x07, 'D', 'I', 'S', '=',
                display_mode,
                0x0D, 0xA
        };

        if (ble_device != null)
        {
            ble_device.write(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT, command, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.e(TAG, "setDisplayMode Write successful");

                    byte[] decoded_data;
                    decoded_data = event.data();

                    int opcode = decoded_data[0] & 0xFF;
                    int result = decoded_data[1] & 0xFF;
                    int data_length = decoded_data[2] & 0xFF;
                    int ack = decoded_data[3] & 0xFF;

                    if ((opcode == (OPCODE_ASCII_COMMAND + 0x80)) && (result == 0x00) && (ack == ACK))
                    {
                        Log.d(TAG, "setDisplayMode Command successful");
                    }
                    else
                    {
                        Log.e(TAG, "setDisplayMode Command FAILED");
                    }
                }
                else
                {
                    Log.e(TAG, "setDisplayMode FAILED : " + event.status().toString());
                }
            });
        }
    }


    public void getDisplayMode()
    {
        Log.d(TAG, "getDisplayMode");

        byte[] command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x06, 'D', 'I', 'S', '?',
                0x0D, 0xA
        };

        if (ble_device != null)
        {
            ble_device.write(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT, command, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.e(TAG, "getDisplayMode Write successful");

                    byte[] decoded_data;
                    decoded_data = event.data();

                    int opcode = decoded_data[0] & 0xFF;
                    int result = decoded_data[1] & 0xFF;
                    int data_length = decoded_data[2] & 0xFF;
                    int display_mode = decoded_data[3] & 0xFF;

                    if ((opcode == (OPCODE_ASCII_COMMAND + 0x80)) && (result == 0x00))
                    {
                        Log.d(TAG, "getDisplayMode Command successful. activation_mode = " + display_mode);
                    }
                    else
                    {
                        Log.e(TAG, "getDisplayMode Command FAILED");
                    }
                }
                else
                {
                    Log.e(TAG, "getDisplayMode FAILED : " + event.status().toString());
                }
            });
        }
    }


    private void clearMemoryProcedure()
    {
        Log.d(TAG, "clearMemoryProcedure");

        connection_state = ConnectionStates.CLEAR_MEMORY;

        if (get_timestamp_timer != null)
        {
            Log.d(TAG, "cancelling get_timestamp_timer from clearMemoryProcedure");
            get_timestamp_timer.cancel();
        }

        Log.d(TAG, "starting memory_clear_timer");
        memory_clear_timer = new Timer();
        memory_clear_timer.schedule(memoryClearTimeout(), 10*DateUtils.SECOND_IN_MILLIS); //, 10*DateUtils.SECOND_IN_MILLIS ); // 10 secs

        Log.d(TAG, "calling blockSetupMode from clearMemoryProcedure");
        blockSetupMode();

        byte[] command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x06, 'M', 'C', 'L', '!',
                0x0D, 0xA
        };

        writeCommandToNoninOximetryControlPoint(command, "clearMemoryProcedure");
    }


    private void setStorageRateToBogusRateForTestPurposes()
    {
        Log.d(TAG, "setStorageRate to BOGUS rate! " + connection_state);

        byte[] set_storage_rate_command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x07, 'D', 'S', 'R', '=',
                0x32,   // Storage rate  0x31: 1 second/sample, 0x32: 2 seconds/sample, 0x34: 4 seconds/sample
                0x0D, 0xA
        };

        sample_storage_rate_needs_setting = false;

        if (ble_device != null)
        {
            connection_state = ConnectionStates.SET_STORAGE_RATE;

            writeCommandToNoninOximetryControlPoint(set_storage_rate_command, "setStorageRate(test value)");
        }
    }

    /*
    0x31 – 1 second/sample
    0x32 – 2 seconds/sample
    0x34 – 4 seconds/sample
     */
    private void setStorageRate()
    {
        Log.d(TAG, "setStorageRate");

        byte[] set_storage_rate_command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x07, 'D', 'S', 'R', '=',
                0x31,   // Storage rate  0x31: 1 second/sample, 0x32: 2 seconds/sample, 0x34: 4 seconds/sample
                0x0D, 0xA
        };

        sample_storage_rate_needs_setting = false;

        if (ble_device != null)
        {
            connection_state = ConnectionStates.SET_STORAGE_RATE;

            writeCommandToNoninOximetryControlPoint(set_storage_rate_command, "setStorageRate");
        }

        //Log.d("-pb", " >> " + Utils.byteArrayToHexString(set_storage_rate_command));
        // Why this failed before... int sent as 01 not as 0x31
        // claimed success with  >> 60 4E 4D 49 07 44 53 52 3D 31 0D 0A
        // using int                60 4E 4D 49 07 44 53 52 3D 01 0D 0A

        // NOTIFICATION seems to be NOTIFICATION : 1447af80-0d60-11e2-88b6-0002a5d5c51b : SUCCESS : Data = E0 00 01 06
        //
        // 2019-07-25 14:50:50.726 7062-7062/com.isansys.patientgateway D/BluetoothLeDevice-BluetoothLeNoninWristOx: ReadWriteListener : NOTIFICATION : 1447af80-0d60-11e2-88b6-0002a5d5c51b : SUCCESS : Data = E0 00 01 06
        //2019-07-25 14:50:50.727 7062-7062/com.isansys.patientgateway D/BluetoothLeNoninWristOx: UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT. connection_state = SET_ACTIVATION_MODE : Raw data : E0 00 01 06


    }


    public void getStorageRate()
    {
        Log.d(TAG, "getStorageRate");

        byte[] command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x06, 'D', 'S', 'R', '?',
                0x0D, 0xA
        };

        if (ble_device != null)
        {
            ble_device.write(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT, command, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.e(TAG, "getStorageRate Write successful");

                    byte[] decoded_data;
                    decoded_data = event.data();

                    int opcode = decoded_data[0] & 0xFF;
                    int result = decoded_data[1] & 0xFF;
                    int data_length = decoded_data[2] & 0xFF;
                    int ack = decoded_data[3] & 0xFF;

                    if ((opcode == (OPCODE_ASCII_COMMAND + 0x80)) && (result == 0x00) && (ack == ACK))
                    {
                        Log.d(TAG, "getStorageRate Command successful");
                    }
                    else
                    {
                        Log.e(TAG, "getStorageRate Command FAILED");
                    }
                }
                else
                {
                    Log.e(TAG, "getStorageRate FAILED : " + event.status().toString());
                }
            });
        }
    }


    public void connectToCharacteristics()
    {
        Log.d(TAG, "connectToCharacteristics");

        getActivationMode();
    }


    private void startGettingData()
    {
        Log.d(TAG, "startGettingData");

        connection_state = ConnectionStates.GETTING_DATA;

        connectToNoninDeviceStatusNotification();
        connectToNoninPlaybackNotification();
    }


    private void startPlaybackOrSubscribeToOximeteryNotification()
    {
        boolean playback_started = false;

        if (connection_state != ConnectionStates.PLAYBACK_REQUIRED)
        {
            if (!PatientGatewayService.spO2_spot_measurements_enabled)
            {
                playback_started = startPlaybackIfRequired();
            }
        }

        // if playback was not requested, and isn't ongoing, subscribe to current data
        if((playback_started == false) && (playback_decoder.isPlaybackOngoing() == false))
        {
            connectToNoninOximetryServiceNotification();
        }
    }


    @Override
    public void sendConnectedIntentAndContinueConnection()
    {
        // Several Nonin packets are longer than the default 20 byte MTU.
        // The Gateway HAS to support Bluetooth 4.2. The Tab A does but the Tab S does NOT
        // Setting MTU value to 100 (as previously) caused big problems with playback, usually the playback message would not be fully received
        Log.e(TAG, "sendConnectedIntentAndContinueConnection calling setMtu");
        ble_device.setMtu(247);

        sendConnectedIntent();

        Log.e(TAG, "sendConnectedIntentAndContinueConnection calling connectToNoninControlPointNotification");
        connectToNoninControlPointNotification();
    }


    @Override
    public void sendDisconnectedIntent()
    {
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
        // Not implemented on this device but function needs to exist
    }


    @Override
    public void sendConnectedIntent()
    {
        sendIntent(ACTION_CONNECTED);
    }


    @Override
    protected String getActionDataAvailableString()
    {
        return ACTION_DATA_AVAILABLE;
    }


    public void sendPairingIntent()
    {
        sendIntent(ACTION_PAIRING);
    }


    public void sendPairingSuccessIntent()
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

    }


    public void setMeasurementInterval(int interval_in_seconds)
    {
        // Not implemented on this device but function needs to exist
    }


    public void enableDisableSetupMode(final boolean enter_setup_mode)
    {
        if (connection_state != ConnectionStates.PLAYBACK_REQUIRED)
        {
            connectToNoninPpgNotification(enter_setup_mode);
        }
        else
        {
            Log.d(TAG,"Ignoring command to enter/exit setup mode because playback is required  enter_setup_mode:" + enter_setup_mode);
        }
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
        // If the batteries are removed from the Nonin for more than 45 seconds, the time will default back to Jan 1st 2010
        Log.d(TAG, "sendTimestamp");

        DateTime date_time = new DateTime(timestamp_now_in_milliseconds);

        String year = String.format(Locale.getDefault(),"%02d", date_time.getYearOfCentury());
        String month = String.format(Locale.getDefault(),"%02d", date_time.getMonthOfYear());
        String day = String.format(Locale.getDefault(),"%02d", date_time.getDayOfMonth());
        String hour = String.format(Locale.getDefault(),"%02d", date_time.getHourOfDay());
        String minute = String.format(Locale.getDefault(),"%02d", date_time.getMinuteOfHour());
        String second = String.format(Locale.getDefault(),"%02d", date_time.getSecondOfMinute());

        Log.d(TAG, "sendTimestamp: setting to " + hour + ":" + minute + ":" + second + " Day:"+day + " Month: " + month + " Year:" + year);

        byte[] command = {OPCODE_ASCII_COMMAND, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2,
                0x12, 'D', 'T', 'M', '=',
                (byte)year.charAt(0),   (byte)year.charAt(1),
                (byte)month.charAt(0),  (byte)month.charAt(1),
                (byte)day.charAt(0),    (byte)day.charAt(1),
                (byte)hour.charAt(0),   (byte)hour.charAt(1),
                (byte)minute.charAt(0), (byte)minute.charAt(1),
                (byte)second.charAt(0), (byte)second.charAt(1),
                0x0D, 0xA
        };

        if (ble_device != null)
        {
            connection_state = ConnectionStates.SET_TIMESTAMP;

            writeCommandToNoninOximetryControlPoint(command, "sendTimestamp");
        }
    }


    private void startSimulationOfPlaybackFromFile()
    {
        Log.d("-pb", " ------------------------  big file ---------------------------------");

        //      playback_decoder.setDebugSendAll(true);

        time_of_last_reading = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();
        playback_decoder.setTimeOfLastReading(time_of_last_reading);

        playback_decoder.newPlaybackSession(() -> Log.d(TAG, "playback session complete"));

        playback_decoder.setDebugSendAll(true);  // sends through all readings, including those that happened before the time of the last real reading

        informGatewayThatNoninPlaybackIsExpected();

        String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
        String directory = "/playback/";
        String full_dir = SD_CARD_PATH + directory;

        File folder_files = new File(full_dir);

        File[] file_list = folder_files.listFiles();

        int actualFileIndex = -1;

        if (file_list != null)
        {
            for (int i = 0; i < file_list.length; i++)
            {
                if (file_list[i].getName().equals("playbackFile.txt"))
                {
                    actualFileIndex = i;
                }
            }

            if (actualFileIndex > -1)
            {
                try
                {
                    FileInputStream fileIn = new FileInputStream(file_list[actualFileIndex]);

                    byte[] in = new byte[fileIn.available()];

                    Log.d(TAG, " reading file available:" + fileIn.available() + " " + file_list[actualFileIndex]);

                    fileIn.read(in);

                    String asString = new String(in);

                    // Remove END_OF_PLAYBACK_DATA_MARKER
                    int endOfPlaybackLocation = asString.indexOf(END_OF_PLAYBACK_DATA_MARKER);

                    playback_decoder.decodePlayback(asString.substring(0, endOfPlaybackLocation));

                    fileIn.close();

                } catch (FileNotFoundException fe) {
                    Log.d(TAG, "Playback FILE NOT FOUND" + fe.toString());
                } catch (IOException io) {
                    Log.d(TAG, "Playback IO EXCEPTION " + io.toString());
                }
            }
        }
    }



    @Override
    public void onDiscovered(BleManager.DiscoveryListener.DiscoveryEvent event)
    {
        ble_device = event.device();

        just_discovered = true;

        int manufacturer_id = ble_device.getManufacturerId();

        Log.d(TAG, "onDiscovered WristOx : " + manufacturer_id);

        BleDeviceConfig config = BleDeviceConfig.newNulled(); // get a nulled version, so that it doesn't override any of the ble manager config settings accidentally.

        // set the non-nullable settings to match the ones we have in the BleDeviceController
        config.autoEnableNotifiesOnReconnect = false;

        // set false if android 9, otherwise true
        config.alwaysBondOnConnect = true; //!isAndroidNine();

        ble_device.setConfig(config);

        if (event.was(BleManager.DiscoveryListener.LifeCycle.UNDISCOVERED))
        {
            Log.d(getChildTag(), getDescriptiveDeviceName() + " UNDISCOVERED");
        }
        else if(ble_device.is(BONDED) && !ble_device.is(CONNECTED, CONNECTING, CONNECTING_OVERALL))
        {
            Log.d(getChildTag(), "*************** onDiscovered WristOx : " + ble_device.getMacAddress());

            executeConnect();
        }
        else if(!ble_device.is(BONDING))
        {
            ble_device.setListener_Bond(m_bond_listener);

            sendPairingIntent();

            executeConnect();
        }
        else
        {
            Log.d(getChildTag(), "onDiscovered WristOx : BONDING in progress");
        }
    }


    public final BleDevice.BondListener m_bond_listener = event -> {
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
}
