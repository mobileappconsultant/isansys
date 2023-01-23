package com.isansys.patientgateway.bluetoothLowEnergyDevices;

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
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.Arrays;
import java.util.UUID;

import static com.idevicesinc.sweetblue.BleDeviceState.BONDED;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTED;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING_OVERALL;
import static com.idevicesinc.sweetblue.BleDeviceState.UNBONDED;

/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public abstract class BloodPressureBluetoothLeDevice extends BluetoothLeDevice
{
    // Go off to the child class to get the TAG
    public String getChildTag()
    {
        return "";
    }

    public final static UUID AnD_CUSTOM_SERVICE = UUID.fromString("233BF000-5A34-1B6D-975C-000D5690ABE4");
    public final static UUID AnD_CHARACTERISTIC_CUSTOM_ONE = UUID.fromString("233BF001-5A34-1B6D-975C-000D5690ABE4");
    public final static UUID AnD_CHARACTERISTIC_CUSTOM_TWO = UUID.fromString("233BF002-5A34-1B6D-975C-000D5690ABE4");

    // Blood Pressure strings
    public final static String DATATYPE_BLOOD_PRESSURE_MEASUREMENT = "BloodPressureBluetoothLeDevice.MEASUREMENT";
    public final static String DIASTOLIC = "BloodPressureBluetoothLeDevice.DIASTOLIC";
    public final static String SYSTOLIC = "BloodPressureBluetoothLeDevice.SYSTOLIC";
    public final static String PULSE_RATE = "BloodPressureBluetoothLeDevice.PULSE_RATE";
    public final static String MEAN_ARTERIAL_PRESSURE = "BloodPressureBluetoothLeDevice.MEAN_ARTERIAL_PRESSURE";
    public final static String TIMESTAMP = "BloodPressureBluetoothLeDevice.TIMESTAMP";

    // Battery Strings
    public final static String BATTERY_PERCENTAGE = "BATTERY_PERCENTAGE";
    public final static String BATTERY_TIMESTAMP = "BATTERY_TIMESTAMP";
    public final static String RECEIVED_TIMESTAMP_AS_LONG = "RECEIVED_TIMESTAMP_AS_LONG";
    public final static String GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED = "GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED";

    public boolean send_configuration_commands;

    private final long ON_DISCONNECT_RE_SCAN_DELAY = 60 * DateUtils.SECOND_IN_MILLIS;


    public BloodPressureBluetoothLeDevice(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time, DeviceType device_type)
    {
        super(context_interface, logger, controller, gateway_time, device_type);

        device_service_uuid = Uuids.BLOOD_PRESSURE_SERVICE_UUID;

        send_configuration_commands = true;
    }


    public void connectToBloodPressureServiceNotification()
    {
        Log.d(getChildTag(), "connectToBloodPressureServiceNotification called");

        if(isDeviceSessionInProgress())
        {
            if (ble_device != null)
            {
                Log.d(getChildTag(), "connectToBloodPressureServiceNotification executing enableNotify");

                ble_device.enableNotify__HighPriority(Uuids.BLOOD_PRESSURE_SERVICE_UUID, Uuids.BLOOD_PRESSURE_MEASUREMENT, event -> {
                    logEventStatus(event);

                    switch (event.type())
                    {
                        // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                        case ENABLING_NOTIFICATION:
                        {
                            logEnablingNotification("connectToBloodPressureServiceNotification", event);

                            if(!event.wasSuccess() && event.device().is(CONNECTED))
                            {
                                postEnableNotificationRetry();
                            }
                        }
                        break;

                        // Data from the Indication/Notification
                        case INDICATION:
                        case NOTIFICATION:
                        {
                            if(event.wasSuccess())
                            {
                                Log.d(getChildTag(), "BLOOD_PRESSURE_MEASUREMENT raw data : " + Utils.byteArrayToHexString(event.data()));

                                byte[] decodedData = event.data();

                                if(decodedData.length == 18)
                                {
                                    // Update this variable. If nothing received within X sec
                                    last_data_received_ntp_time = ntp_time.currentTimeMillis();

                                    BloodPressureData bloodPressureData = parseBloodPressure(decodedData);
                                    sendBloodPressureIntent(getActionDataAvailableString(), bloodPressureData, event.device().getMacAddress(), event.data());

                                    // ToDo: Should this stay inside the indication success?
                                    readBattery();
                                }
                                else
                                {
                                    Log.e(getChildTag(), "BLOOD_PRESSURE_MEASUREMENT incorrect data length: length was " + decodedData.length);
                                }
                            }
                            else
                            {
                                Log.e(getChildTag(), "BLOOD_PRESSURE_MEASUREMENT read FAILED");
                            }
                        }
                        break;

                        default:
                        {
                            Log.e(getChildTag(), "Default is " + Utils.byteArrayToHexString(event.data()));
                        }
                    }
                });
            }
        }
        else
        {
            Log.e(getChildTag(), "connectToBloodPressureServiceNotification - no device session in progress so not connecting");
        }
    }

    public void readBattery()
    {
        ble_device.readBatteryLevel(e -> {
            if (e.type() == BleDevice.ReadWriteListener.Type.READ)
            {
                if (e.wasSuccess())
                {
                    Log.d(getChildTag(), "BLOOD_PRESSURE_MEASUREMENT BATTERY raw data : " + Utils.byteArrayToHexString(e.data()));

                    int battery_as_int = e.data()[0];

                    sendBatteryLevelIntent(battery_as_int, e.device().getMacAddress(), e.data());
                }
            }
        });
    }


    private void postEnableNotificationRetry()
    {
        retry_handler.postDelayed(this::connectToBloodPressureServiceNotification, DateUtils.SECOND_IN_MILLIS/2);
    }


    public void getTimestamp()
    {
        if (ble_device != null)
        {
            // First-time connection, so go through full timestamp and configuration code...
            ble_device.read(Uuids.BLOOD_PRESSURE_SERVICE_UUID, Uuids.DATE_TIME, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.d(getChildTag(), "getTimestamp : SUCCESS : " + Utils.byteArrayToHexString(event.data()));

                    long received_timestamp_in_ms = parseDateTime(event.data());

                    sendTimestampIntent(received_timestamp_in_ms, event.device().getMacAddress(), event.data());
                }
                else
                {
                    Log.e(getChildTag(), "getTimestamp FAILED : " + event.status().toString());

                    if (event.device().is(CONNECTED))
                    {
                        Log.e(getChildTag(), "getTimestamp RETRYING");

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
        Log.e(getChildTag(), "connectToCharacteristics");

        if(send_configuration_commands)
        {
            Log.e(getChildTag(), "connectToCharacteristics : send_configuration_commands");

            deleteAllDataInBufferAndSetBufferSize();
        }

        connectToBloodPressureServiceNotification();
    }


    @Override
    public void sendConnectedIntentAndContinueConnection()
    {
        Log.e(getChildTag(), "sendConnectedIntent");

        sendConnectedIntent();

        getTimestamp();

        if(isDeviceSessionInProgress())
        {
            connectToCharacteristics();
        }
    }

    @Override
    protected void sendRescanRequiredIntent()
    {
        // Not implemented on this device but function needs to exist
    }


    protected abstract String getActionPairingString();
    protected abstract String getActionPairingSuccessString();
    protected abstract String getActionPairingFailureString();
    protected abstract String getActionUnexpectedlyUnbondedString();
    protected abstract String getActionConnectedString();
    protected abstract String getActionDisconnectedString();
    protected abstract String getActionDataAvailableString();

    public void sendPairingIntent()
    {
        sendIntent(getActionPairingString());
    }

    public void sendPairingSuccessIntent()
    {
        sendIntent(getActionPairingSuccessString());
    }

    public void sendPairingFailureIntent()
    {
        sendIntent(getActionPairingFailureString());
    }

    public void sendUnexpectedlyUnbondedIntent()
    {
        sendIntent(getActionUnexpectedlyUnbondedString());
    }

    public void sendConnectedIntent()
    {
        sendIntent(getActionConnectedString());
    }

    public void sendDisconnectedIntent()
    {
        sendIntent(getActionDisconnectedString());
    }

    @Override
    public boolean checkLastDataIsRestartRequired(long timeout)
    {
        Log.e(getChildTag(), "checkLastDataIsRestartRequired returning FALSE");

        return false;
    }


    public void sendTimestamp(long timestamp_now_in_milliseconds, final byte[] value)
    {
        writeStandardDateTimeCharacteristic(timestamp_now_in_milliseconds);
    }


    public void setBleDeviceConfig()
    {
        BleDeviceConfig config = BleDeviceConfig.newNulled(); // get a Nulled version, so that it doesn't override any of the ble manager config settings accidentally.
        config.reconnectFilter = new DoNotReconnectFilter();

        // set the non-nullable settings to match the ones we have in the BleDeviceController
        config.autoEnableNotifiesOnReconnect = false;

        // set false if android 9, otherwise true
        config.alwaysBondOnConnect = true;

        ble_device.setConfig(config);
    }


    @Override
    public void onDiscovered(BleManager.DiscoveryListener.DiscoveryEvent event)
    {
        ble_device = event.device();

        just_discovered = true;

        Log.d(getChildTag(), "onDiscovered : " + ble_device.getMacAddress());

        setBleDeviceConfig();

        pairAndConnect(event);
    }


    public void pairAndConnect(BleManager.DiscoveryListener.DiscoveryEvent event)
    {
        if (event.was(BleManager.DiscoveryListener.LifeCycle.UNDISCOVERED))
        {
            Log.d(getChildTag(), "*************** onDiscovered : " + getDescriptiveDeviceName() + " UNDISCOVERED");
        }
        else if (ble_device.is(CONNECTED, CONNECTING, CONNECTING_OVERALL))
        {
            // Already connected or connecting - do nothing
            Log.d(getChildTag(), "*************** onDiscovered : " + getDescriptiveDeviceName() + " CONNECTED, CONNECTING or CONNECTING_OVERALL");
        }
        else if (ble_device.is(BONDED))
        {
            // Already bonded, so just re-connect
            Log.d(getChildTag(), "*************** onDiscovered : " + getDescriptiveDeviceName() + " BONDED so reconnect");

            executeConnect();
        }
        else if (ble_device.is(UNBONDED))
        {
            // not yet bonded so need to connect, tell gateway service we're going to bond, and set up bonding listener.
            // This lets us feed back to the UI scan page.
            // Actual bonding is handled by sweetblue/android behind the scenes.
            Log.d(getChildTag(), "*************** onDiscovered : " + getDescriptiveDeviceName() + " UNBONDED so bond");
                
            ble_device.setListener_Bond(m_bond_listener);

            sendPairingIntent();

            executeConnect();
        }
        else
        {
            // do nothing - wait for bonding to finish.
            // In fact this should never happen with the current settings - we only do bonding
            // when triggered as part of a connection so shouldn't be BONDING unless we're also CONNECTING or CONNECTED

            Log.d(getChildTag(), "*************** onDiscovered : " + getDescriptiveDeviceName() + " ELSE statement. pre-connection BONDING in progress");
        }
    }


    public final BleDevice.BondListener m_bond_listener = event -> {
        Log.e(getChildTag(), "m_bond_listener : Sweetblue BondEvent for " + getDescriptiveDeviceName() + "   " + event.toString());

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

            if(isDeviceSessionInProgress() && (!unbonded_during_session))
            {
                device_controller.postBleReScan(ON_DISCONNECT_RE_SCAN_DELAY);

                return Please.stopRetrying();
            }

            if(e.device().is(BONDED))
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
        Log.e(getChildTag(), "*********************** resetStateVariables");
        super.resetStateVariables();

        send_configuration_commands = true;
    }

    private void deleteAllDataInBufferAndSetBufferSize()
    {
        Log.d(getChildTag(), "deleteAllDataInBufferAndSetBufferSize");

        // See AnD UA651 Application Development Specification

        //Format here is (size)  (type)       (command)      (data)
        // values are     (3)    (write)  (set buffer size)   (1 = 30 measurements)
        byte[] command = { (byte) 0x03, (byte) 0x01, (byte) 0xA6, (byte) 0x01 };

        ble_device.write(BloodPressureBluetoothLeDevice.AnD_CUSTOM_SERVICE, BloodPressureBluetoothLeDevice.AnD_CHARACTERISTIC_CUSTOM_ONE, command, event -> {
            logEventStatus(event);

            if (event.wasSuccess())
            {
                Log.e(getChildTag(), "deleteAllDataInBufferAndSetBufferSize Write successful : Command Response = " + Utils.byteArrayToHexString(event.data()));

                send_configuration_commands = false;
            }
            else
            {
                Log.e(getChildTag(), "deleteAllDataInBufferAndSetBufferSize Write FAILED : " + event.status().toString());
            }
        });
    }


    public static class BloodPressureData
    {
        public int systolic;
        public int diastolic;
        public int pulse_rate;
        public int mean_arterial_pressure;
        public long timestamp_ms;
    }


    // Standard Blood Pressure profile 0x2A35
    public BloodPressureData parseBloodPressure(byte[] data)
    {
        int index = 0;

        byte flags = data[index++];
        Log.e(getChildTag(), "flags = " + Utils.convertByteToString(flags));

        final byte FLAGS__UNITS_IN_KPA = (byte) (1);
        final byte FLAGS__TIMESTAMP_FLAG_PRESENT = (byte) (1 << 1);
        final byte FLAGS__PULSE_RATE_FLAG_PRESENT = (byte) (1 << 2);
        final byte FLAGS__USER_ID_FLAG_PRESENT = (byte) (1 << 3);
        final byte FLAGS__MEASUREMENT_STATUS_FLAG_PRESENT = (byte) (1 << 4);
        // Bits 7, 6 and 5 are Reserved for future use

        boolean units_in_kpa = (flags & FLAGS__UNITS_IN_KPA) > 0;
        Log.e(getChildTag(), "units_in_kpa = " + units_in_kpa);

        boolean timestamp_present = (flags & FLAGS__TIMESTAMP_FLAG_PRESENT) > 0;
        Log.e(getChildTag(), "timestamp_present = " + timestamp_present);

        boolean pulse_rate_present = (flags & FLAGS__PULSE_RATE_FLAG_PRESENT) > 0;
        Log.e(getChildTag(), "pulse_rate_present = " + pulse_rate_present);

        boolean user_id_present = (flags & FLAGS__USER_ID_FLAG_PRESENT) > 0;
        Log.e(getChildTag(), "user_id_present = " + user_id_present);

        boolean measurement_status_present = (flags & FLAGS__MEASUREMENT_STATUS_FLAG_PRESENT) > 0;
        Log.e(getChildTag(), "measurement_status_present = " + measurement_status_present);

        // Bits 7, 6 and 5 are Reserved for future use
        float systolic = checkForErrorOrReadFloat(data[index++], data[index++]);
        float diastolic = checkForErrorOrReadFloat(data[index++], data[index++]);
        float mean_arterial_pressure = checkForErrorOrReadFloat(data[index++], data[index++]);

        Log.e(getChildTag(), "Systolic = " + systolic);
        Log.e(getChildTag(), "diastolic = " + diastolic);
        Log.e(getChildTag(), "mean_arterial_pressure = " + mean_arterial_pressure);

        long measurement_timestamp_in_ms = 0;
        if (timestamp_present)
        {
            measurement_timestamp_in_ms = parseDateTime(Arrays.copyOfRange(data, index, index+7));

            index += 7;

            Log.e(getChildTag(), "UTC timestamp = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(measurement_timestamp_in_ms));
        }

        float pulse_rate = -1;
        if (pulse_rate_present)
        {
            pulse_rate = checkForErrorOrReadFloat(data[index++], data[index++]);

            Log.e(getChildTag(), "pulse_rate = " + pulse_rate);
        }

        if (user_id_present)
        {
            int user_id = data[index++] & 0xFF;
            Log.e(getChildTag(), "user_id = " + user_id);
        }

        if (measurement_status_present)
        {
            int measurement_status = ((data[index++] & 0xFF) * 256) | (data[index++] & 0xFF);
            Log.e(getChildTag(), "measurement_status = " + measurement_status);

            final byte MEASUREMENT_STATUS__BODY_MOVEMENT_DETECTED_DURING_MEASUREMENT = (byte) (1);
            boolean body_movement_detected_during_measurement = (measurement_status & MEASUREMENT_STATUS__BODY_MOVEMENT_DETECTED_DURING_MEASUREMENT) > 0;
            Log.e(getChildTag(), "body_movement_detected_during_measurement = " + body_movement_detected_during_measurement);

            final byte MEASUREMENT_STATUS__CUFF_FIT_TOO_LOOSE = (byte) (1 << 1);
            boolean cuff_fit_too_loose = (measurement_status & MEASUREMENT_STATUS__CUFF_FIT_TOO_LOOSE) > 0;
            Log.e(getChildTag(), "cuff_fit_too_loose = " + cuff_fit_too_loose);

            final byte MEASUREMENT_STATUS__IRREGULAR_PULSE_DETECTED = (byte) (1 << 2);
            boolean irregular_pulse_detected = (measurement_status & MEASUREMENT_STATUS__IRREGULAR_PULSE_DETECTED) > 0;
            Log.e(getChildTag(), "irregular_pulse_detected = " + irregular_pulse_detected);

            final byte MEASUREMENT_STATUS__PULSE_RATE_RANGE_DETECTION_FLAGS = (byte) (3 << 3);
            int pulse_rate_range_detection_flags = (measurement_status & MEASUREMENT_STATUS__PULSE_RATE_RANGE_DETECTION_FLAGS) >> 3;
            Log.e(getChildTag(), "pulse_rate_range_detection_flags = " + pulse_rate_range_detection_flags);

            final byte MEASUREMENT_STATUS__IMPROPER_MEASUREMENT_POSITION = (byte) (1 << 5);
            boolean improper_measurement_position = (measurement_status & MEASUREMENT_STATUS__IMPROPER_MEASUREMENT_POSITION) > 0;
            Log.e(getChildTag(), "improper_measurement_position = " + improper_measurement_position);
        }

        Log.e(getChildTag(), "Number of bytes processed = " + index);

        BloodPressureData blood_pressure = new BloodPressureData();
        blood_pressure.systolic = (int) systolic;
        blood_pressure.diastolic = (int) diastolic;
        blood_pressure.mean_arterial_pressure = (int) mean_arterial_pressure;
        blood_pressure.pulse_rate = (int) pulse_rate;
        blood_pressure.timestamp_ms = measurement_timestamp_in_ms;

        return blood_pressure;
    }


    public void sendBloodPressureIntent(String actionDataAvailable, BloodPressureData blood_pressure, String macAddress, byte[] data)
    {
        Intent intent = new Intent();
        intent.setAction(actionDataAvailable);
        intent.putExtra(DATA_TYPE, DATATYPE_BLOOD_PRESSURE_MEASUREMENT);
        intent.putExtra(SYSTOLIC, blood_pressure.systolic);
        intent.putExtra(DIASTOLIC, blood_pressure.diastolic);
        intent.putExtra(PULSE_RATE, blood_pressure.pulse_rate);
        intent.putExtra(MEAN_ARTERIAL_PRESSURE, blood_pressure.mean_arterial_pressure);
        intent.putExtra(TIMESTAMP, blood_pressure.timestamp_ms);

        sendIntentWithDeviceAddressAndDataAsString(intent, macAddress, data);
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


    public void sendTimestampIntent(long device_timestamp, String mac_address, byte[] raw_data)
    {
        final Intent intent = new Intent(getActionDataAvailableString());
        intent.putExtra(DATA_TYPE, DATATYPE_GET_TIMESTAMP);

        intent.putExtra(RECEIVED_TIMESTAMP_AS_LONG, device_timestamp);
        intent.putExtra(GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED, ntp_time.currentTimeMillis());

        sendIntentWithDeviceAddressAndDataAsString(intent, mac_address, raw_data);
    }



    public void processEvent(@SuppressWarnings("deprecation") BleDevice.StateListener.StateEvent event)
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }

    public void keepAlive()
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }


    public void setMeasurementInterval(int interval_in_seconds)
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }


    public void enableDisableSetupMode(final boolean enter_setup_mode)
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }


    public void sendTestModeCommand(boolean enable)
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }


    public void firmwareOtaUpdate(byte[] firmware_binary_array)
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }


    public void setRadioOff(byte timeTillRadioOff, byte timeWithRadioOff)
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }


    public void enableDisableRawAccelerometerMode(final boolean enter_raw_accelerometer_mode)
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }


    public void sendTurnOffCommand()
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }


    public void sendNightModeCommand(final boolean enable_night_mode)
    {
        // Not implemented on Blood Pressure devices, but this device but function needs to exist
    }


    public void sendBatteryLevelIntent(int batteryPercentage, String macAddress, byte[] data)
    {
        Intent intent = new Intent();
        intent.setAction(getActionDataAvailableString());
        intent.putExtra(DATA_TYPE, DATATYPE_BATTERY_LEVEL);
        intent.putExtra(BATTERY_PERCENTAGE, batteryPercentage);
        intent.putExtra(BATTERY_TIMESTAMP, ntp_time.currentTimeMillis());

        sendIntentWithDeviceAddressAndDataAsString(intent, macAddress, data);
    }
}
