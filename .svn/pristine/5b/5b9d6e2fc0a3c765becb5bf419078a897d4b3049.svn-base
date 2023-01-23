package com.isansys.patientgateway.bluetoothLowEnergyDevices.Instapatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;

import com.idevicesinc.sweetblue.BleDevice;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.patientgateway.AES;
import com.isansys.common.ErrorCodes;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.VitalSignSet;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public class BluetoothLeInstapatch extends BluetoothLeDevice
{
    private final static String TAG = BluetoothLeInstapatch.class.getSimpleName();

    private final UUID SERVICE_ISANSYS                          = UUID.fromString("00001000-EE81-4555-B724-3DC951D9203E");
    private final UUID CHARACTERISTIC_VITAL_SIGNS               = UUID.fromString("00001001-EE81-4555-B724-3DC951D9203E");  // Indication
    private final UUID CHARACTERISTIC_HEART_BEATS               = UUID.fromString("00001003-EE81-4555-B724-3DC951D9203E");  // Indication
    private final UUID CHARACTERISTIC_SETUP_MODE                = UUID.fromString("00001004-EE81-4555-B724-3DC951D9203E");  // Indication, Write
    //private final UUID CHARACTERISTIC_RAW_ACCELEROMETER_MODE    = UUID.fromString("00001005-EE81-4555-B724-3DC951D9203E");
    private final UUID CHARACTERISTIC_LEADS_OFF                 = UUID.fromString("00001006-EE81-4555-B724-3DC951D9203E");  // Indication
    private final UUID CHARACTERISTIC_TIMESTAMP                 = UUID.fromString("00001008-EE81-4555-B724-3DC951D9203E");  // Read
    private final UUID CHARACTERISTIC_TURN_OFF                  = UUID.fromString("00001009-EE81-4555-B724-3DC951D9203E");  // Write
    private final UUID CHARACTERISTIC_ENABLE_NIGHT_MODE         = UUID.fromString("0000100A-EE81-4555-B724-3DC951D9203E");  // Write
    private final UUID CHARACTERISTIC_AUTHENTICATION            = UUID.fromString("0000100C-EE81-4555-B724-3DC951D9203E");  // Read/Write

    public final static String DATATYPE_VITAL_SIGN_SET = "DATATYPE_VITAL_SIGN_SET";
    public final static String DATATYPE_BEAT_INFO = "BEAT_INFO";
    public final static String DATATYPE_SETUP_MODE_RAW_SAMPLES = "SETUP_MODE_RAW_SAMPLES";
    public final static String DATATYPE_HEART_BEAT_LEADS_OFF = "DATATYPE_HEART_BEAT_LEADS_OFF";
    //public final static String DATATYPE_RAW_ACCELEROMETER_RAW_SAMPLES = "RAW_ACCELEROMETER_RAW_SAMPLES";

    public final static String ACTION_CONNECTED = "com.isansys.patientgateway.instapatch.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED = "com.isansys.patientgateway.instapatch.ACTION_DISCONNECTED";
    public final static String ACTION_DATA_AVAILABLE = "com.isansys.patientgateway.instapatch.ACTION_DATA_AVAILABLE";
    public final static String ACTION_TURNED_OFF = "com.isansys.patientgateway.instapatch.ACTION_TURNED_OFF";
    public final static String ACTION_RESCAN_REQUIRED = "com.isansys.patientgateway.instapatch.ACTION_RESCAN_REQUIRED";
    public final static String ACTION_AUTHENTICATION_PASSED = "com.isansys.patientgateway.instapatch.ACTION_AUTHENTICATION_PASSED";
    public final static String ACTION_AUTHENTICATION_FAILED = "com.isansys.patientgateway.instapatch.ACTION_AUTHENTICATION_FAILED";

    public final static String BATTERY_PERCENTAGE = "com.isansys.patientgateway.instapatch.BATTERY_PERCENTAGE";
    public final static String BATTERY_VOLTAGE = "com.isansys.patientgateway.instapatch.BATTERY_VOLTAGE";
    public final static String BATTERY_PERCENTAGE_TIMESTAMP = "com.isansys.patientgateway.instapatch.BATTERY_PERCENTAGE_TIMESTAMP";

    public final static String SETUP_MODE_DATA = "com.isansys.patientgateway.instapatch.SETUP_MODE_DATA";

    //public final static String RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES = "com.isansys.patientgateway.instapatch.RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES";
    //public final static String RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES = "com.isansys.patientgateway.instapatch.RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES";
    //public final static String RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES = "com.isansys.patientgateway.instapatch.RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES";
    //public final static String RAW_ACCELEROMETER_MODE__TIMESTAMPS = "com.isansys.patientgateway.instapatch.RAW_ACCELEROMETER_MODE__TIMESTAMPS";

    public final static String HEART_BEAT_INFO = "com.isansys.patientgateway.instapatch.HEART_BEAT_INFO";
    public final static String HEART_BEAT_LEADS_OFF = "com.isansys.patientgateway.instapatch.HEART_BEAT_LEADS_OFF";
    public final static String HEART_BEAT_LEADS_OFF_TIMESTAMP = "com.isansys.patientgateway.instapatch.HEART_BEAT_LEADS_OFF_TIMESTAMP";
    public final static String HEART_BEATS_PENDING = "com.isansys.patientgateway.instapatch.HEART_BEATS_PENDING";

    public final static String TYPE_VITAL_SIGN_SET = "com.isansys.patientgateway.instapatch.TYPE_VITAL_SIGN_SET";
    public final static String TYPE_VITAL_SIGN_SETS_PENDING = "com.isansys.patientgateway.instapatch.INTENT_VITAL_SIGN_SETS_PENDING";

    public final static int GAP_IN_DATA = -1;

    private final static int NO_DATA = 0xC0;
    private final static int WHOLE_DATA_POINT = 0x80;
    private final static int POSITIVE_DELTA = 0x40;
    private final static int NEGATIVE_DELTA = 0x00;

    // TEMPORARY
    public static final byte[] instapatch_key_aes_128_ecb = {(byte)0x78, (byte)0x36, (byte)0x34, (byte)0x65, (byte)0x12, (byte)0x83, (byte)0x0D, (byte)0x7A, (byte)0xDF, (byte)0x1A, (byte)0x50, (byte)0xBC, (byte)0xFF, (byte)0xE0, (byte)0xB1, (byte)0xD0};

    public BluetoothLeInstapatch(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time)
    {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__INSTAPATCH);
    }


    public String getChildTag()
    {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
    }


    private ArrayList<Short> setupModeDeltasSampleExtractor(byte[] raw_message)
    {
        try
        {
            // Decrypt the cipher text to get the plaintext
            final int timeStampSize = 4;
            byte[] encryptedMessage = Arrays.copyOfRange(raw_message, timeStampSize, raw_message.length);
            byte[] decryptedMessage = new byte[16];// = AES.decrypt(encryptedMessage, instapatch_key_aes_128_ecb);
// THIS IS BODGE CODE AS PAYLOAD NOT ENCRYPTED FOR NOW
            System.arraycopy(encryptedMessage, 0, decryptedMessage, 0, encryptedMessage.length);
            //Log.e(TAG, "Sample DEC is " + Utils.byteArrayToHexString(decryptedMessage) + " - " + decryptedMessage.length);

            int last_sample = 0;

            final int DELTA_MASK = 0x3F;

            ArrayList<Short> samples = new ArrayList<>();

            // Start from byte 4, after the timestamp
            for(int i = 0; i < decryptedMessage.length ; i++)
            {
                // Get whole value/delta flag from the top two bits
                int prefix = (decryptedMessage[i] & 0xC0) & 0xFF;

                switch(prefix)
                {
                    case WHOLE_DATA_POINT:
                    {
                        // Then we have the whole value in two bytes
                        int sample = (decryptedMessage[i++] & 0xFF);
                        sample = sample << 8;
                        sample += (decryptedMessage[i] & 0xFF);

                        last_sample = sample & 0x3FFF;
                        samples.add((short)last_sample);
                        //Log.d(TAG, "Sample WHOLE = " + last_sample);
                    }
                    break;

                    case POSITIVE_DELTA:
                    {
                        int delta = decryptedMessage[i] & DELTA_MASK;

                        last_sample = last_sample + delta;
                        samples.add((short)last_sample);
                        //Log.d(TAG, "Sample +VE   = " + last_sample);
                    }
                    break;

                    case NEGATIVE_DELTA:
                    {
                        int delta = decryptedMessage[i] & DELTA_MASK;

                        last_sample = last_sample - delta;
                        samples.add((short)last_sample);
                        //Log.d(TAG, "Sample -VE   = " + last_sample);
                    }
                    break;

                    case NO_DATA:
                    {
                        Log.d(TAG, "Sample NO_DATA");
                        // Nothing to do. Lifetouch has detected there is not enough room to write a sample.
                        // Can only happen when trying to write a full 2 byte sample but only 1 byte left
                    }
                    break;

                    default:
                    {
                        // Can never get here
                    }
                    break;
                }
            }

            return samples;
        }
        catch (Exception e)
        {
            Log.d(TAG, "Error lifetouchSetupModeDeltasSampleExtractor" + e.toString());

            return null;
        }
    }

    
    private long[] instapatchRawDataSampleTimestampExtractor(int timeDelayBetweenSamplesInMilliseconds, byte[] data, int number_of_samples)
    {
        long[] return_value= new long[number_of_samples];

        long first_sample_timestamp = (data[3] & 0xFF);
        first_sample_timestamp<<=8;
        first_sample_timestamp += (data[2] & 0xFF);
        first_sample_timestamp<<=8;
        first_sample_timestamp += (data[1] & 0xFF);
        first_sample_timestamp<<=8;
        first_sample_timestamp += (data[0] & 0xFF);
        
        for(int counter=0; counter<number_of_samples; counter++)
        {
            return_value[counter] = first_sample_timestamp + (counter * timeDelayBetweenSamplesInMilliseconds);
        }
        
        return return_value;
    }
    

    public void sendTurnOffCommand()
    {
        Log.d(TAG, "Sending Turnoff Command");

        if (ble_device != null)
        {
            final byte[] turn_off_code = {0x42, 0x41, 0x44, 0x47, 0x45, 0x52};

            ble_device.write(SERVICE_ISANSYS, CHARACTERISTIC_TURN_OFF, turn_off_code, event -> {
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


    public void sendTestModeCommand(boolean enable)
    {
        if (enable)
        {
            //sendEnterTestModeCommand();
        }
        else
        {
            //sendExitTestModeCommand();
        }
    }

/*
    private void sendEnterTestModeCommand()
    {
        Log.d(TAG, "Sending sendEnterTestModeCommand Command to the lifetouch");

        if (ble_device != null)
        {
            final byte[] enter_test_mode_code = {0x4D, 0x55, 0x53, 0x48, 0x52, 0x4D};

            ble_device.write(SERVICE_ISANSYS, UUID_ENTER_TEST_MODE, enter_test_mode_code, new BleDevice.ReadWriteListener()
            {
                @Override
                public void onEvent(ReadWriteEvent event)
                {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.e(TAG, "UUID_ENTER_TEST_MODE Write successful");
                    }
                    else
                    {
                        Log.e(TAG, "sendEnterTestModeCommand FAILED : " + event.status().toString());
                    }
                }
            });
        }
    }

    
    private void sendExitTestModeCommand()
    {
        Log.d(TAG, "Sending sendExitTestModeCommand Command to the lifetouch");

        if (ble_device != null)
        {
            final byte[] exit_test_mode_code = {0x53, 0x4E, 0x41, 0x4B, 0x45, 0x21};

            ble_device.write(SERVICE_ISANSYS, UUID_ENTER_TEST_MODE, exit_test_mode_code, new BleDevice.ReadWriteListener()
            {
                @Override
                public void onEvent(ReadWriteEvent event)
                {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.e(TAG, "UUID_ENTER_TEST_MODE Write successful");
                    }
                    else
                    {
                        Log.e(TAG, "sendExitTestModeCommand FAILED : " + event.status().toString());
                    }
                }
            });
        }
    }
*/

    public void sendTimestamp(long timestamp_now_in_milliseconds, final byte[] value)
    {
        // Nothing to do for Instapatch
    }


    public void enableDisableSetupMode(final boolean enter_setup_mode)
    {
        if (ble_device != null)
        {
            Log.d(TAG, "Sending enableDisableSetupMode (" + enter_setup_mode + ")");

            byte[] value = new byte[1];

            if (enter_setup_mode)
            {
                value[0] = 0x01;
            }
            else
            {
                value[0] = 0x00;
            }

            ble_device.write(SERVICE_ISANSYS, CHARACTERISTIC_SETUP_MODE, value, event -> {
                logEventStatus(event);

                if (event.wasSuccess())
                {
                    if (enter_setup_mode)
                    {
                        Log.e(TAG, "UUID_SETUP_MODE_ENABLE enable write successful");
                    }
                    else
                    {
                        Log.e(TAG, "UUID_SETUP_MODE_ENABLE disable write successful");
                    }
                }
                else
                {
                    Log.e(TAG, "enableDisableSetupMode FAILED : " + event.status().toString());
                }
            });
        }
    }


    public void enableDisableRawAccelerometerMode(final boolean enter_raw_accelerometer_mode)
    {
/*
        if (ble_device != null)
        {
            Log.d(TAG, "Sending enableDisableRawAccelerometerMode Command to the Lifetouch");

            byte[] value = new byte[1];

            if (enter_raw_accelerometer_mode)
            {
                value[0] = 0x01;
            }
            else
            {
                value[0] = 0x00;
            }

            ble_device.write(UUID_SERVICE_RAW_ACCELEROMETER_MODE, UUID_RAW_ACCELEROMETER_MODE_ENABLE, value, new BleDevice.ReadWriteListener()
            {
                @Override
                public void onEvent(ReadWriteEvent event)
                {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        if (enter_raw_accelerometer_mode)
                        {
                            Log.e(TAG, "UUID_RAW_ACCELEROMETER_MODE_ENABLE enable write successful");
                        }
                        else
                        {
                            Log.e(TAG, "UUID_RAW_ACCELEROMETER_MODE_ENABLE disable write successful");
                        }
                    }
                    else
                    {
                        Log.e(TAG, "enableDisableRawAccelerometerMode FAILED : " + event.status().toString());
                    }
                }
            });
        }
*/
    }


    /**
     * Function to send the night mode command to the sensor
     * @param enable_night_mode if true, sensor is set in the night mode
     */
    public void sendNightModeCommand(final boolean enable_night_mode)
    {
        Log.d(TAG, "Sending sendNightModeCommand " + enable_night_mode);

        if (ble_device != null)
        {
            if (enable_night_mode)
            {
                final byte[] night_mode_enable_code = {0x01, 0x01, 0x01, 0x01, 0x01};

                ble_device.write(SERVICE_ISANSYS, CHARACTERISTIC_ENABLE_NIGHT_MODE, night_mode_enable_code, event -> {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.e(TAG, "sendNightModeCommand ENABLE Write successful");
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

                ble_device.write(SERVICE_ISANSYS, CHARACTERISTIC_ENABLE_NIGHT_MODE, night_mode_enable_code, event -> {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.e(TAG, "sendNightModeCommand DISABLE Write successful");
                    }
                    else
                    {
                        Log.e(TAG, "sendNightModeCommand FAILED : " + event.status().toString());
                    }
                });
            }
        }
    }


    public void setRadioOff(byte timeTillRadioOff, byte timeWithRadioOff)
    {
/*
        Log.d(TAG, "setRadioOff");
        
        if (ble_device != null)
        {
            final byte[] value = {timeTillRadioOff, timeWithRadioOff};

            ble_device.write(SERVICE_ISANSYS, UUID_RADIO_ON_OFF, value, new BleDevice.ReadWriteListener()
            {
                @Override
                public void onEvent(ReadWriteEvent event)
                {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.e(TAG, "UUID_RADIO_ON_OFF write successful");
                    }
                    else
                    {
                        Log.e(TAG, "setRadioOff FAILED : " + event.status().toString());
                    }
                }
            });
        }
*/
    }


    private void connectToVitalSignsIndication()
    {
        Log.d(TAG, "connectToVitalSignsIndication called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToVitalSignsIndication executing enableNotify");

            ble_device.enableNotify(SERVICE_ISANSYS, CHARACTERISTIC_VITAL_SIGNS, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToVitalSignsIndication", event);
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        // Received data from the device
                        if(event.wasSuccess())
                        {
                            Log.e(TAG, "CHARACTERISTIC_VITAL_SIGNS is " + Utils.byteArrayToHexString(event.data()));

                            last_data_received_ntp_time = ntp_time.currentTimeMillis();

                            byte[] data = event.data();

                            int data_length = data.length;

                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);

                            if (data_length > 0)
                            {
                                intent.putExtra(DATA_TYPE, DATATYPE_VITAL_SIGN_SET);

                                byte[] decodedData = new byte[data_length];

                                try
                                {
                                    byte[] ciphertext_data = new byte[data_length];
                                    System.arraycopy(data, 0, ciphertext_data, 0, data_length);

                                    decodedData = AES.decrypt(ciphertext_data, instapatch_key_aes_128_ecb);

                                    Log.e(TAG, "DECODED = " + Utils.byteArrayToHexString(decodedData));
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG, "CHARACTERISTIC_VITAL_SIGNS Try/Catch : " + e.toString());
                                }

                                VitalSignSet vitalSignSet = new VitalSignSet();
                                vitalSignSet.setTag(decodedData[1] & 0xFF | (decodedData[0] & 0x1F) << 8);
                                vitalSignSet.setHeartRate(decodedData[3] & 0xFF | (decodedData[2] & 0x1F) << 8);
                                vitalSignSet.setRespirationRate(decodedData[5] & 0xFF | (decodedData[4] & 0x1F) << 8);

                                int temperature_in_tenths_of_degrees_c = decodedData[7] & 0xFF | (decodedData[6] & 0x1F) << 8;
                                vitalSignSet.setTemperature((double)temperature_in_tenths_of_degrees_c / 10);

                                long timestamp_in_milliseconds_since_device_turned_on = decodedData[11] & 0xFF | (decodedData[10] & 0xFF) << 8 | (decodedData[9] & 0xFF) << 16 | (decodedData[8] & 0xFF) << 24;
                                vitalSignSet.setTimestampInMs(timestamp_in_milliseconds_since_device_turned_on);

                                int number_of_vital_sign_sets_pending_after_these = decodedData[13] & 0xFF | (decodedData[12] & 0xFF) << 8;

                                intent.putExtra(TYPE_VITAL_SIGN_SET, vitalSignSet);
                                intent.putExtra(TYPE_VITAL_SIGN_SETS_PENDING, number_of_vital_sign_sets_pending_after_these);
                            }
                            else
                            {
                                Log.e(TAG, "broadcastUpdate (CHARACTERISTIC_VITAL_SIGNS): Data size of the CHARACTERISTIC_VITAL_SIGNS is NULL !!!!!!!!!!!!!!!!!");
                            }

                            sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                        }
                        else
                        {
                            Log.e(TAG, "CHARACTERISTIC_VITAL_SIGNS read FAILED");
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


    private void connectToHeartBeatIndication()
    {
        Log.d(TAG, "connectToHeartBeatIndication called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToHeartBeatIndication executing enableNotify");

            ble_device.enableNotify(SERVICE_ISANSYS, CHARACTERISTIC_HEART_BEATS, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToHeartBeatIndication", event);
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        // Received data from the device
                        if(event.wasSuccess())
                        {
                            //Log.e(TAG, "UUID_HEART_BEAT_BEAT_INFO is " + Utils.byteArrayToHexString(event.data()));

                            last_data_received_ntp_time = ntp_time.currentTimeMillis();

                            byte[] data = event.data();

                            final int SIZE_OF_NUMBER_OF_HEART_BEATS_PENDING = 4;

                            int data_length;
                            boolean lifetouch_reporting_number_of_heart_beats_pending;

                            // Ensure the Lifetouch supports the Number of Heart Beats pending
                            if ((data.length == (16 + SIZE_OF_NUMBER_OF_HEART_BEATS_PENDING)) || (data.length == (8 + SIZE_OF_NUMBER_OF_HEART_BEATS_PENDING)))
                            {
                                data_length = data.length - SIZE_OF_NUMBER_OF_HEART_BEATS_PENDING;

                                lifetouch_reporting_number_of_heart_beats_pending = true;
                            }
                            else
                            {
                                data_length = data.length;

                                lifetouch_reporting_number_of_heart_beats_pending = false;
                            }

                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);

                            if (data_length > 0)
                            {
                                intent.putExtra(DATA_TYPE, DATATYPE_BEAT_INFO);

                                byte[] decodedData = new byte[data_length];

                                try
                                {
                                    // The last 4 bytes are not used. Make another array just for the data otherwise the AES will complain about an invalid block size
                                    byte[] ciphertext_data = new byte[data_length];
                                    System.arraycopy(data, 0, ciphertext_data, 0, data_length);

                                    decodedData = AES.decryptCBF(ciphertext_data, AES.default_key_256, AES.default_init_vector);
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG, "UUID_HEART_BEAT_BEAT_INFO Try/Catch : " + e.toString());
                                }

                                // Number of Heart Beats is the length of the received data / 8 as Hearts Beats are 8 bytes in size
                                int number_of_heart_beats = data_length / 8;                        // Will either be 1 or 2 heart beats
                                int j = 0;

                                ArrayList<HeartBeatInfo> heart_beat_list = new ArrayList<>();

                                try
                                {
                                    for (int i = 0; i < number_of_heart_beats; i++)
                                    {
                                        // Process the Heart Beats
                                        HeartBeatInfo this_heart_beat = new HeartBeatInfo();

                                        this_heart_beat.setTag(decodedData[j + 1] & 0xFF | (decodedData[j] & 0x1F) << 8);
                                        this_heart_beat.setActivity(HeartBeatInfo.ActivityLevel.fromInt((decodedData[j] & 0xE0) >> 5));
                                        this_heart_beat.setAmplitude(decodedData[j + 3] & 0xFF | (decodedData[j + 2] & 0xFF) << 8);
                                        this_heart_beat.setTimestampInMs(decodedData[j + 7] & 0xFF | (decodedData[j + 6] & 0xFF) << 8 | (decodedData[j + 5] & 0xFF) << 16 | (decodedData[j + 4] & 0xFF) << 24);

                                        if (this_heart_beat.getActivity() == null)
                                        {
                                            this_heart_beat.setActivity(HeartBeatInfo.ActivityLevel.NO_DATA);
                                        }

                                        // Nordic bug to do with queue up packets on the LT
                                        if (this_heart_beat.getAmplitude() != ErrorCodes.ERROR_CODE__LIFETOUCH_NO_DATA)
                                        {
                                            heart_beat_list.add(this_heart_beat);

                                            // This timestamp delta will not be correct as it has not had device_info_manager.device_info__lifetouch.start_date_at_midnight_in_milliseconds added to it
                                            Log.d(TAG, "Rx " + Utils.explainHeartBeat(this_heart_beat));
                                        }

                                        j = j + 8;
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, "Error reading heart beat");
                                    Log.e(TAG, e.toString());
                                }

                                intent.putParcelableArrayListExtra(HEART_BEAT_INFO, heart_beat_list);

                                if (lifetouch_reporting_number_of_heart_beats_pending)
                                {
                                    // Get the number of Heart Beats Pending after these ones
                                    int number_of_heart_beats_pending_after_these = data[j + 3] & 0xFF | (data[j + 2] & 0xFF) << 8 | (data[j + 1] & 0xFF) << 16 | (data[j] & 0xFF) << 24;
                                    intent.putExtra(HEART_BEATS_PENDING, number_of_heart_beats_pending_after_these);
                                }
                            }
                            else
                            {
                                Log.e(TAG, "broadcastUpdate (UUID_HEART_BEAT_BEAT_INFO): Data size of the UUID_HEART_BEAT_BEAT_INFO is NULL !!!!!!!!!!!!!!!!!");
                            }

                            sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                        }
                        else
                        {
                            Log.e(TAG, "UUID_HEART_BEAT_BEAT_INFO read FAILED");
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


    private void connectToSetupModeIndication()
    {
        Log.d(TAG, "connectToSetupModeIndication called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToSetupModeIndication executing enableNotify");

            ble_device.enableNotify(SERVICE_ISANSYS, CHARACTERISTIC_SETUP_MODE, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToSetupModeIndication", event);
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if(event.wasSuccess())
                        {
                            Log.e(TAG, "UUID_SETUP_MODE_RAW_SAMPLES is " + Utils.byteArrayToHexString(event.data()));

                            last_data_received_ntp_time = ntp_time.currentTimeMillis();

                            byte[] data = event.data();

                            if (data.length > 0)
                            {
                                ArrayList<Short> setup_mode_data = setupModeDeltasSampleExtractor(data);

                                if (setup_mode_data != null)
                                {
                                    final int time_delay_between_samples_in_milliseconds = 10;  // 100 Hz Setup Mode data - change this to handle high-hz sampling
                                    long[] setup_mode_timestamps = instapatchRawDataSampleTimestampExtractor(time_delay_between_samples_in_milliseconds, data, setup_mode_data.size());

                                    ArrayList<MeasurementSetupModeDataPoint> measurements = new ArrayList<>();

                                    for (int i=0; i<setup_mode_data.size(); i++)
                                    {
                                        measurements.add(new MeasurementSetupModeDataPoint(setup_mode_data.get(i), setup_mode_timestamps[i]));
                                    }

                                    // Now checking for gaps. If so then insert "fake" samples that get translated to NaN on the charts
                                    ArrayList<MeasurementSetupModeDataPoint> measurements_with_gaps_filled_in = checkForSetupModeGapsAndInsertNulls(measurements);

                                    Bundle bundle = new Bundle();
                                    bundle.putParcelableArrayList(SETUP_MODE_DATA, measurements_with_gaps_filled_in);

                                    Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                                    intent.putExtra(DATA_TYPE, DATATYPE_SETUP_MODE_RAW_SAMPLES);
                                    intent.putExtras(bundle);
                                    sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                                }
                            }
                            else
                            {
                                Log.e(TAG, "broadcastUpdate (UUID_SETUP_MODE_RAW_SAMPLES): Data size of the UUID_SETUP_MODE_RAW_SAMPLES is NULL !!!!!!!!!!!!!!!!!");
                            }
                        }
                        else
                        {
                            Log.e(TAG, "UUID_SETUP_MODE_RAW_SAMPLES read FAILED");
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


    MeasurementSetupModeDataPoint previous_setup_mode_measurement = new MeasurementSetupModeDataPoint(0, 0);


    private ArrayList<MeasurementSetupModeDataPoint> checkForSetupModeGapsAndInsertNulls(ArrayList<MeasurementSetupModeDataPoint> measurements)
    {
        if (previous_setup_mode_measurement.timestamp_in_ms == 0)
        {
            // Init this variable once per session
            previous_setup_mode_measurement.timestamp_in_ms = measurements.get(0).timestamp_in_ms;
        }

        ArrayList<MeasurementSetupModeDataPoint> measurements_with_gaps_filled_in = new ArrayList<>();

        for (MeasurementSetupModeDataPoint measurement : measurements)
        {
            long gap_between_samples = measurement.timestamp_in_ms - previous_setup_mode_measurement.timestamp_in_ms;

            // Lifetouch is 100 Hz so 10 mS.
            if (gap_between_samples > 15)
            {
                // Then there is a gap
                Log.e(TAG, "Gap at " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms) + " of " + gap_between_samples);

                // Add fake point at start of gap (just after previous measurement). Will get replaced with Double.NaN in bulkLoadGraphFromMeasurementArray
                measurements_with_gaps_filled_in.add(new MeasurementSetupModeDataPoint(GAP_IN_DATA, previous_setup_mode_measurement.timestamp_in_ms + 1));

                // Add fake point at end of gap (just before current measurement). Will get replaced with Double.NaN in bulkLoadGraphFromMeasurementArray
                measurements_with_gaps_filled_in.add(new MeasurementSetupModeDataPoint(GAP_IN_DATA, measurement.timestamp_in_ms - 1));
            }

            previous_setup_mode_measurement = measurement;

            measurements_with_gaps_filled_in.add(measurement);
        }

        return measurements_with_gaps_filled_in;
    }


    private void connectToLeadsOffNotification()
    {
        Log.d(TAG, "connectToLeadsOffNotification called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToLeadsOffNotification executing enableNotify");

            ble_device.enableNotify(SERVICE_ISANSYS, CHARACTERISTIC_LEADS_OFF, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToLeadsOffNotification", event);
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if(event.wasSuccess())
                        {
                            last_data_received_ntp_time = ntp_time.currentTimeMillis();

                            int leads_off = event.data()[0];

                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                            intent.putExtra(DATA_TYPE, DATATYPE_HEART_BEAT_LEADS_OFF);
                            intent.putExtra(HEART_BEAT_LEADS_OFF_TIMESTAMP, PatientGatewayService.getNtpTimeNowInMillisecondsStatic());

                            if (leads_off == 0)
                            {
                                Log.d(TAG, "----==< LEADS ON RECEIVED >==----");
                                intent.putExtra(HEART_BEAT_LEADS_OFF, false);
                            }
                            else
                            {
                                Log.d(TAG, "----==< LEADS OFF RECEIVED >==----");
                                intent.putExtra(HEART_BEAT_LEADS_OFF, true);
                            }

                            sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                        }
                        else
                        {
                            Log.e(TAG, "UUID_HEART_BEAT_LEADS_OFF read FAILED");
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

/*
    private void connectToRawAccelerometerNotification()
    {
        Log.d(TAG, "connectToRawAccelerometerNotification called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToRawAccelerometerNotification executing enableNotify");

            ble_device.enableNotify(UUID_SERVICE_RAW_ACCELEROMETER_MODE, UUID_RAW_ACCELEROMETER_MODE_RAW_SAMPLES, new BleDevice.ReadWriteListener()
            {
                @Override
                public void onEvent(ReadWriteEvent event)
                {
                    logEventStatus(event);

                    switch (event.type())
                    {
                        // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                        case ENABLING_NOTIFICATION:
                        {
                            logEnablingNotification("connectToRawAccelerometerNotification", event);
                        }
                        break;

                        // Data from the Indication/Notification
                        case INDICATION:
                        case NOTIFICATION:
                        {
                            if(event.wasSuccess())
                            {
                                Log.e(TAG, "UUID_RAW_ACCELEROMETER_MODE_RAW_SAMPLES is " + Utils.byteArrayToHexString(event.data()));

                                last_data_received_ntp_time = ntp_time.currentTimeMillis();

                                byte[] data = event.data();

                                if (data.length > 0)
                                {
                                    // Number of Samples is the length of the received data - 4 (as first four bytes are Timestamp) / 3 (as three byte pairs for X/Y/Z axis)
                                    int number_of_samples = (data.length - 4) / 3;

                                    short[] x_axis_samples = new short[number_of_samples];
                                    short[] y_axis_samples = new short[number_of_samples];
                                    short[] z_axis_samples = new short[number_of_samples];

                                    final int time_delay_between_samples_in_milliseconds = 100;  // 10 Hz Raw Accelerometer Mode data
                                    long[] timestamps = lifetouchRawDataSampleTimestampExtractor(time_delay_between_samples_in_milliseconds, data, number_of_samples);

                                    int i, j;

                                    // Convert the remaining bytes to into Samples (shorts)
                                    j = 4;
                                    for (i = 0; i < number_of_samples; i++)
                                    {
                                        // The +128 takes it from signed to unsigned "measurement". The &0xFF takes its to a Java unsigned variable
                                        int x_axis_sample = (data[j++] + 128) & 0xFF;
                                        int y_axis_sample = (data[j++] + 128) & 0xFF;
                                        int z_axis_sample = (data[j++] + 128) & 0xFF;

                                        x_axis_samples[i] = (short) x_axis_sample;
                                        y_axis_samples[i] = (short) y_axis_sample;
                                        z_axis_samples[i] = (short) z_axis_sample;
                                    }

                                    final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                                    intent.putExtra(DATA_TYPE, DATATYPE_RAW_ACCELEROMETER_RAW_SAMPLES);
                                    intent.putExtra(RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES, x_axis_samples);
                                    intent.putExtra(RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES, y_axis_samples);
                                    intent.putExtra(RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES, z_axis_samples);
                                    intent.putExtra(RAW_ACCELEROMETER_MODE__TIMESTAMPS, timestamps);
                                    sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                                }
                                else
                                {
                                    Log.e(TAG, "broadcastUpdate (UUID_RAW_ACCELEROMETER_MODE_RAW_SAMPLES): Data size of the UUID_RAW_ACCELEROMETER_MODE_RAW_SAMPLES is NULL !!!!!!!!!!!!!!!!!");
                                }
                            }
                            else
                            {
                                Log.e(TAG, "UUID_RAW_ACCELEROMETER_MODE_RAW_SAMPLES read FAILED");
                            }
                        }
                        break;

                        default:
                        {
                            Log.e(TAG, "Default is " + Utils.byteArrayToHexString(event.data()));
                        }
                    }
                }
            });
        }
    }
*/
/*
    private void connectToBatteryIndication()
    {
        Log.d(TAG, "connectToBatteryIndication called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToBatteryIndication executing enableNotify");

            ble_device.enableNotify(SERVICE_ISANSYS_BATTERY, UUID_ISANSYS_BATTERY_LEVEL, new BleDevice.ReadWriteListener()
            {
                @Override
                public void onEvent(ReadWriteEvent event)
                {
                    logEventStatus(event);

                    switch (event.type())
                    {
                        // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                        case ENABLING_NOTIFICATION:
                        {
                            logEnablingNotification("connectToBatteryIndication", event);
                        }
                        break;

                        // Data from the Indication/Notification
                        case INDICATION:
                        case NOTIFICATION:
                        {
                            if(event.wasSuccess())
                            {
                                Log.e(TAG, "UUID_ISANSYS_BATTERY_LEVEL is " + Utils.byteArrayToHexString(event.data()));

                                last_data_received_ntp_time = ntp_time.currentTimeMillis();

                                byte[] data = event.data();

                                int battery_percentage = data[0] & 0xFF;

                                int battery_voltage = (data[1] & 0xFF);
                                battery_voltage = battery_voltage * 256;
                                battery_voltage = battery_voltage + (data[2] & 0xFF);

                                int battery_instant_voltage = -1;

                                if(data.length>=9)
                                {
                                    battery_instant_voltage = (data[7] & 0xFF);
                                    battery_instant_voltage = battery_instant_voltage * 256;
                                    battery_instant_voltage = battery_instant_voltage + (data[8] & 0xFF);
                                }

                                long timestamp_in_ms = 0;

                                if (data.length >= 7)
                                {
                                    Log.d(TAG, "UUID_ISANSYS_BATTERY_LEVEL : Length of data received is "+ data.length +" bytes with timestamp");
                                    timestamp_in_ms = ((data[3] & 0xFF) << 24) | ((data[4] & 0xFF) << 16) | ((data[5] & 0xFF) << 8) | ((data[6] & 0xFF));

                                    if(data.length>=9)
                                    {
                                        Log.d(TAG, "UUID_ISANSYS_BATTERY_LEVEL : Instant Voltage = " + battery_instant_voltage + " mV");
                                    }
                                }
                                else
                                {
                                    Log.w(TAG, "UUID_ISANSYS_BATTERY_LEVEL : Length of data received is less than 7 bytes i.e. timestamp is not send");
                                }

                                Log.i(TAG, "UUID_ISANSYS_BATTERY_LEVEL : Received Lifetouch Battery Level % : " + battery_percentage + " and measured Battery Voltage of " + battery_voltage + " . Timestamp received = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(timestamp_in_ms));

                                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                                intent.putExtra(DATA_TYPE, DATATYPE_BATTERY_LEVEL);
                                intent.putExtra(DATA_AS_STRING, String.valueOf(battery_percentage));
                                intent.putExtra(BATTERY_VOLTAGE, battery_voltage);
                                intent.putExtra(BATTERY_PERCENTAGE, battery_percentage);
                                intent.putExtra(BATTERY_PERCENTAGE_TIMESTAMP, timestamp_in_ms);
                                sendIntentWithDeviceAddress(intent, event.device().getMacAddress());
                            }
                            else
                            {
                                Log.e(TAG, "UUID_ISANSYS_BATTERY_LEVEL read FAILED");
                            }
                        }
                        break;

                        default:
                        {
                            Log.e(TAG, "Default is " + Utils.byteArrayToHexString(event.data()));
                        }
                    }
                }
            });
        }
    }
*/

    public void getTimestamp()
    {
        if(isDeviceSessionInProgress())
        {
            Log.d(TAG, "getTimestamp - session started so getting timestamp");

            if (ble_device != null)
            {
                Log.d(TAG, "getTimestamp read");

                ble_device.read(SERVICE_ISANSYS, CHARACTERISTIC_TIMESTAMP, event -> {
                    logEventStatus(event);

                    if( event.wasSuccess() )
                    {
                        Log.d(TAG, "CHARACTERISTIC_GET_TIMESTAMP : " + Utils.byteArrayToHexString(event.data()));

//TODO Not sure what this is for on Instapatch
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
        Log.d(TAG, "connectToCharacteristics - starting authentication");

        authenticate();
    }


    private void authenticate()
    {
        Log.d(TAG, "authenticate called");

        if (ble_device != null)
        {
            Log.d(TAG, "authenticate read one time key");

            ble_device.read(SERVICE_ISANSYS, CHARACTERISTIC_AUTHENTICATION, event -> {
                logEventStatus(event);

                if(event.wasSuccess())
                {
                    handleOneTimeKey(event.data());
                }
                else
                {
                    Log.e(TAG, "UUID_READ_AUTHENTICATION_ONE_TIME_KEY FAILED");
                }
            });
        }
    }


    private void handleOneTimeKey(byte[] data)
    {
        Log.d(TAG, "handleOneTimeKey : Received " + Utils.byteArrayToHexString(data));

        if (ble_device != null)
        {
            byte[] encrypted_data = null;

            try
            {
                // Encrypted received data from the Lifetouch Green.
                //encrypted_data = AES.encryptCBF(data, AES.default_key_256, AES.default_init_vector);
                encrypted_data = AES.encrypt(data, AES.default_key_128);

                Log.d(TAG, "handleOneTimeKey : Encrypted to become " + Utils.byteArrayToHexString(encrypted_data));
            }
            catch(Exception e)
            {
                Log.e(TAG, "handleOneTimeKey token generation failed");
                Log.e(TAG, e.toString());
            }

            // write token
            if(encrypted_data != null)
            {
                byte[] auth_token = new byte[16];

                // Only copies the first 16 bytes
                System.arraycopy(encrypted_data, 0, auth_token, 0, 16);

                Log.d(TAG, "handleOneTimeKey : Copy as " + Utils.byteArrayToHexString(auth_token));

                ble_device.write(SERVICE_ISANSYS, CHARACTERISTIC_AUTHENTICATION, auth_token, event -> {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.d(TAG, "handleOneTimeKey UUID_WRITE_AUTHENTICATION_TOKEN write successful");
                        authenticationSucceeded();
                    }
                    else
                    {
                        Log.e(TAG, "handleOneTimeKey UUID_WRITE_AUTHENTICATION_TOKEN FAILED : " + event.status().toString());
                        authenticationFailed();
                    }
                });
            }
        }
    }


    private void authenticationSucceeded()
    {
        Log.d(TAG, "authenticationSucceeded - connecting to remaining characteristics");

        connectToSetupModeIndication();
        connectToLeadsOffNotification();
        //connectToRawAccelerometerNotification();
        //connectToBatteryIndication();
        //connectToHeartBeatIndication();
        connectToVitalSignsIndication();

        sendIntent(new Intent(ACTION_AUTHENTICATION_PASSED));

        authenticated = true;
    }


    private void authenticationFailed()
    {
        sendIntent(new Intent(ACTION_AUTHENTICATION_FAILED));

        authenticated = false;
    }


    @Override
    public void keepAlive()
    {
        /*
        if((ble_device != null) && (authenticated))
        {
            // generate 16 bytes of random data
            byte[] keep_alive_data = new byte[16];
            rand.nextBytes(keep_alive_data);

            // send it to the authentication characteristic to keep the connection alive.
            ble_device.write(SERVICE_ISANSYS, CHARACTERISTIC_AUTHENTICATION, keep_alive_data, new BleDevice.ReadWriteListener()
            {
                @Override
                public void onEvent(ReadWriteEvent event)
                {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.e(TAG, "UUID_WRITE_AUTHENTICATION_TOKEN keep alive write successful");
                    }
                    else
                    {
                        Log.e(TAG, "UUID_WRITE_AUTHENTICATION_TOKEN keep alive  write FAILED : " + event.status().toString());
                    }
                }
            });
        }
        */
    }


    @Override
    public void sendConnectedIntentAndContinueConnection()
    {
        sendConnectedIntent();

        // MUST get the timestamp first BEFORE even authenticating. Once timestamp received, authenticate will be called
        getTimestamp();
    }


    @Override
    public void sendDisconnectedIntent()
    {
        sendIntent(ACTION_DISCONNECTED);
    }


    @Override
    public void sendRescanRequiredIntent()
    {
        sendIntent(ACTION_RESCAN_REQUIRED);
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


    public void firmwareOtaUpdate(byte[] firmware_binary_array)
    {
    }


    public void setMeasurementInterval(int interval_in_seconds)
    {
        // Not implemented on this device but function needs to exist
    }


    @Override
    public void resetStateVariables()
    {
        super.resetStateVariables();

        previous_setup_mode_measurement = new MeasurementSetupModeDataPoint(0, 0);
    }
}
