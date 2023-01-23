package com.isansys.patientgateway.bluetoothLowEnergyDevices.Lifetemp;

import java.util.Locale;
import java.util.UUID;

import android.content.Intent;

import com.idevicesinc.sweetblue.BleDevice;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.AES;
import com.isansys.common.ErrorCodes;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public class BluetoothLeLifetempV2 extends BluetoothLeDevice
{
    private final static String TAG = BluetoothLeLifetempV2.class.getSimpleName();

    private final UUID UUID_SERVICE_TEMPERATURE = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
    private final UUID UUID_TEMPERATURE_MEASUREMENT = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
    private final UUID UUID_GET_SET_TIMESTAMP = UUID.fromString("00001525-1212-efde-1523-785feabcd123");
    private final UUID UUID_TURN_OFF = UUID.fromString("00001527-1212-efde-1523-785feabcd123");
    private final UUID UUID_ENABLE_NIGHT_MODE = UUID.fromString("00001528-1212-efde-1523-785feabcd123");
    private final UUID UUID_SET_MEASUREMENT_INTERVAL = UUID.fromString("0000152a-1212-efde-1523-785feabcd123");
    private final UUID UUID_AUTHENTICATION = UUID.fromString("0000152b-1212-efde-1523-785feabcd123");

    public final static String DATATYPE_TEMPERATURE_MEASUREMENT = "DATATYPE_TEMPERATURE_MEASUREMENT";
    public final static String DATATYPE_LIFETEMP_MEASUREMENT_INTERVAL = "DATATYPE_LIFETEMP_MEASUREMENT_INTERVAL";

    public final static String ACTION_CONNECTED = "com.isansys.patientgateway.ACTION_CONNECTED.Lifetemp_V2";
    public final static String ACTION_DISCONNECTED = "com.isansys.patientgateway.ACTION_DISCONNECTED.Lifetemp_V2";
    public final static String ACTION_DATA_AVAILABLE = "com.isansys.patientgateway.ACTION_DATA_AVAILABLE.Lifetemp_V2";
    public final static String ACTION_TURNED_OFF = "com.isansys.patientgateway.ACTION_TURNED_OFF.Lifetemp_V2";

    private final static int KEEP_ALIVE_IMPLEMENTED_AT_VERSION_NUMBER = 9999; // so far not implemented...

    private boolean measurement_interval_needs_setting;

    public BluetoothLeLifetempV2(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time)
    {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__LIFETEMP_V2);

        measurement_interval_needs_setting = true;
    }


    public String getChildTag()
    {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
    }


    @Override
    public void resetStateVariables()
    {
        super.resetStateVariables();

        measurement_interval_needs_setting = true;
    }


    public void sendTurnOffCommand()
    {
        Log.d(TAG, "Sending Turnoff Command");

        if (ble_device != null)
        {
            final byte[] turn_off_code = {0x11, 0x22, 0x33, 0x44, 0x55};

            ble_device.write(UUID_SERVICE_TEMPERATURE, UUID_TURN_OFF, turn_off_code, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.e(TAG, "UUID_TURN_OFF Write successful");
                }
                else
                {
                    Log.e(TAG, "sendTurnOffCommand FAILED : " + event.status().toString());
                }

                // Either Turned Off properly, or timed out trying to send the Turn Off command as out of range.
                sendIntent(ACTION_TURNED_OFF);
            });
        }
        else
        {
            Log.d(TAG, "sendTurnOffCommand : ble_device == null, cannot send turn off command");
        }
    }


    /**
     * Function to send the night mode command to the Lifetemp
     * @param enable_night_mode if true, Lifetemp is set in the night mode
     */
    public void sendNightModeCommand(final boolean enable_night_mode)
    {
        Log.d(TAG, "Sending sendNightModeCommand " + enable_night_mode + "  to the Lifetemp");

        if (ble_device != null)
        {
            if (enable_night_mode)
            {
                final byte[] night_mode_enable_code = {0x01, 0x01, 0x01, 0x01, 0x01};

                ble_device.write(UUID_SERVICE_TEMPERATURE, UUID_ENABLE_NIGHT_MODE, night_mode_enable_code, event -> {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.e(TAG, "UUID_ENABLE_NIGHT_MODE ENABLE Write successful");
                    }
                    else
                    {
                        Log.e(TAG, "sendNightModeCommand FAILED : " + event.status().toString());
                    }
                });
            }
            else
            {
                final byte[] night_mode_enable_code = {0x00, 0x00, 0x00, 0x00, 0x00};

                ble_device.write(UUID_SERVICE_TEMPERATURE, UUID_ENABLE_NIGHT_MODE, night_mode_enable_code, event -> {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.e(TAG, "UUID_ENABLE_NIGHT_MODE DISABLE Write successful");
                    }
                    else
                    {
                        Log.e(TAG, "sendNightModeCommand FAILED : " + event.status().toString());
                    }
                });
            }
        }
    }


    public void sendTimestamp(long timestamp_now_in_milliseconds, final byte[] value)
    {
        Log.d(TAG, "Sending the time stamp to the Lifetemp : time stamp is " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(timestamp_now_in_milliseconds));

        if (ble_device != null)
        {
            ble_device.write(UUID_SERVICE_TEMPERATURE, UUID_GET_SET_TIMESTAMP, value, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.e(TAG, "UUID_GET_SET_TIMESTAMP Write successful");
                }
                else
                {
                    Log.e(TAG, "sendTimestamp FAILED : " + event.status().toString());
                }
            });
        }
    }


    public void setMeasurementInterval(int interval_in_seconds)
    {
        if(measurement_interval_needs_setting == true)
        {
            Log.d(TAG, "Sending the measurement interval to the lifetemp : interval is " + interval_in_seconds + " seconds");

            byte[] interval = {(byte) interval_in_seconds};

            if (ble_device != null)
            {
                ble_device.write(UUID_SERVICE_TEMPERATURE, UUID_SET_MEASUREMENT_INTERVAL, interval, event -> {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.e(TAG, "UUID_SET_MEASUREMENT_INTERVAL Write successful");

                        measurement_interval_needs_setting = false;
                    }
                    else
                    {
                        Log.e(TAG, "setMeasurementInterval FAILED : " + event.status().toString());

                        measurement_interval_needs_setting = true;
                    }
                });
            }
        }
        else
        {
            Log.d(TAG, "Measurement interval does not need setting");
        }
    }


    private void connectToTemperatureMeasurementIndication()
    {
        Log.d(TAG, "connectToTemperatureMeasurementIndication called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToTemperatureMeasurementIndication executing enableNotify");

            ble_device.enableNotify(UUID_SERVICE_TEMPERATURE, UUID_TEMPERATURE_MEASUREMENT, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToTemperatureMeasurementIndication", event);
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if(event.wasSuccess())
                        {
                            Log.d(TAG, "UUID_TEMPERATURE_MEASUREMENT raw data : " + Utils.byteArrayToHexString(event.data()));

                            byte[] decodedData = null;

                            last_data_received_ntp_time = ntp_time.currentTimeMillis();

                            try
                            {
                                decodedData = AES.decrypt(event.data(), AES.default_key_128);
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG,"UUID_TEMPERATURE_MEASUREMENT "+ e.toString());
                            }

                            if((decodedData != null) && (decodedData.length == AES.aesDataChunkSize))
                            {
                                int error_code = (decodedData[0] & 0xFF);
                                error_code = error_code * 256;
                                error_code += decodedData[1] & 0xFF;

                                int temperature_integer = decodedData[0] & 0xFF;
                                int temperature_fraction = decodedData[1] & 0xFF;

                                long timestamp = (decodedData[5] & 0xFF) |  ((decodedData[4] & 0xFF) << 8) | ((decodedData[3] & 0xFF) << 16) | ((decodedData[2] & 0xFF) << 24) ;

                                int battery_percentage = decodedData[6] & 0xFF;
                                int battery_voltage = (decodedData[7] & 0xFF);
                                battery_voltage = battery_voltage + ((decodedData[8] & 0xFF) * 256);

                                long packet_id =  (decodedData[9] & 0xFF) |  ((decodedData[10] & 0xFF) << 8) | ((decodedData[11] & 0xFF) << 16) | ((decodedData[12] & 0xFF) << 24) ;

                                int measurements_pending = (decodedData[14] & 0xFF);
                                measurements_pending = measurements_pending + ((decodedData[13] & 0xFF) * 256);

                                if (error_code >= ErrorCodes.ERROR_CODES)
                                {
                                    int error_type = (decodedData[5] & 0xFF) |  ((decodedData[4] & 0xFF) << 8) | ((decodedData[3] & 0xFF) << 16) | ((decodedData[2] & 0xFF) << 24) ;
                                    String error_type_as_string = Utils.convertIntToString(error_type);

                                    switch (error_code)
                                    {
                                        case ErrorCodes.ERROR_CODE__LIFETEMP_RESET_CALLEE_IDENTIFIER:
                                        {
                                            Log.e(TAG, "ERROR_CODE__LIFETEMP_RESET_CALLEE_IDENTIFIER : " + error_type_as_string);
                                        }
                                        break;

                                        case ErrorCodes.ERROR_CODE__LIFETEMP_RESET_ERROR_CODE:
                                        {
                                            Log.e(TAG, "ERROR_CODE__LIFETEMP_RESET_ERROR_CODE : " + error_type_as_string);
                                        }
                                        break;

                                        case ErrorCodes.ERROR_CODE__LIFETEMP_RESET_LINE_NUMBER:
                                        {
                                            Log.e(TAG, "ERROR_CODE__LIFETEMP_RESET_LINE_NUMBER : " + error_type_as_string);
                                        }
                                        break;
                                    }
                                }
                                else
                                {
                                    if(temperature_integer == 0)
                                    {
                                        // Leads off (Lifetemp is measuring a temperature that is too low - so assume its not connected to patient anymore)
                                        Log.d(TAG, "Received temperature out of range, treating as leads off. Timestamp is " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(timestamp) );

                                        temperature_integer = ErrorCodes.ERROR_CODE__LIFETEMP_LEADS_OFF;
                                        temperature_fraction = 0x00;
                                    }
                                    else
                                    {
                                        String logLine = "Received temperature: " + temperature_integer + "." + temperature_fraction + ". Timestamp: " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(timestamp) + " packet id: " + packet_id + "  Measurements Pending: " + measurements_pending;
                                        logLine = logLine + ". Battery Voltage = " + battery_voltage + ". Battery % = " + battery_percentage + "%";
                                        Log.d(TAG, logLine);
                                    }

                                    final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                                    intent.putExtra(PACKET_ID, packet_id);
                                    intent.putExtra(DATA_AS_STRING, (temperature_integer + "." + String.format(Locale.UK, "%02d",temperature_fraction) + "__" + timestamp + "__" + battery_percentage + "__" + battery_voltage) + "__" + measurements_pending);
                                    intent.putExtra(DATA_TYPE, BluetoothLeLifetempV2.DATATYPE_TEMPERATURE_MEASUREMENT);
                                    sendIntentWithDeviceAddress(intent, event.device().getMacAddress());
                                }
                            }
                        }
                        else
                        {
                            Log.e(TAG, "UUID_TEMPERATURE_MEASUREMENT read FAILED");
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


    public void getTimestamp()
    {
        if(isDeviceSessionInProgress())
        {
            Log.d(TAG, "getTimestamp - session started so getting timestamp");

            if (ble_device != null)
            {
                Log.d(TAG, "getTimestamp read");

                ble_device.read(UUID_SERVICE_TEMPERATURE, UUID_GET_SET_TIMESTAMP, event -> {
                    logEventStatus(event);

                    if( event.wasSuccess() )
                    {
                        Log.d(TAG, "UUID_GET_SET_TIMESTAMP : " + Utils.byteArrayToHexString(event.data()));

                        last_data_received_ntp_time = ntp_time.currentTimeMillis();

                        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                        intent.putExtra(DATA_TYPE, DATATYPE_GET_TIMESTAMP);
                        sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                    }
                    else
                    {
                        Log.e(TAG, "UUID_GET_SET_TIMESTAMP FAILED");
                    }
                });
            }
        }
        else
        {
            Log.d(TAG, "getTimestamp - no session started so NOT getting timestamp");
        }
    }


    public void connectToCharacteristics()
    {
        Log.d(TAG, "connectToCharacteristics");

        connectToTemperatureMeasurementIndication();
    }


    @Override
    public void sendConnectedIntentAndContinueConnection()
    {
        sendConnectedIntent();

        getTimestamp();
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


    private boolean isKeepAliveImplemented()
    {
        return getFirmwareVersion() >= KEEP_ALIVE_IMPLEMENTED_AT_VERSION_NUMBER;
    }


    @Override
    public void keepAlive()
    {
        if((ble_device != null) && (authenticated) && isKeepAliveImplemented())
        {
            // generate 16 bytes of random data
            byte[] keep_alive_data = new byte[16];
            rand.nextBytes(keep_alive_data);

            // send it to the authentication characteristic to keep the connection alive.
            ble_device.write(UUID_SERVICE_TEMPERATURE, UUID_AUTHENTICATION, keep_alive_data, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    Log.e(TAG, "UUID_AUTHENTICATION keep alive write successful");
                }
                else
                {
                    Log.e(TAG, "UUID_AUTHENTICATION keep alive  write FAILED : " + event.status().toString());
                }
            });
        }
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
}
