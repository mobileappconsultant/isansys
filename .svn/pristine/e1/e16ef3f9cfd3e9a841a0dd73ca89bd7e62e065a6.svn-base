package com.isansys.patientgateway.bluetoothLowEnergyDevices.MedLinket;

import static com.idevicesinc.sweetblue.BleDeviceState.BONDED;
import static com.idevicesinc.sweetblue.BleDeviceState.BONDING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTED;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING_OVERALL;
import static com.isansys.common.measurements.MeasurementVitalSign.INVALID_MEASUREMENT;

import android.content.Intent;
import android.os.Bundle;

import androidx.collection.CircularArray;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceConfig;
import com.idevicesinc.sweetblue.BleManager;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public class BluetoothLeMedLinket extends BluetoothLeDevice
{
    private final static String TAG = BluetoothLeMedLinket.class.getSimpleName();

    private final UUID UUID_SERVICE_MEDLINKET_OXIMETRY_SERVICE = UUID.fromString("0000FFB0-0000-1000-8000-00805f9b34fb");
        private final UUID UUID_CHARACTERISTIC_MEDLINKET_OXIMETRY_MEASUREMENT = UUID.fromString("0000FFB2-0000-1000-8000-00805f9b34fb");

    public final static String ACTION_PAIRING = "BluetoothLeMedLinket.ACTION_PAIRING";
    public final static String ACTION_PAIRING_SUCCESS = "BluetoothLeMedLinket.ACTION_PAIRING_SUCCESS";
    public final static String ACTION_PAIRING_FAILURE = "BluetoothLeMedLinket.ACTION_PAIRING_FAILURE";
    public final static String ACTION_CONNECTED = "BluetoothLeMedLinket.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED = "BluetoothLeMedLinket.ACTION_DISCONNECTED";
    public final static String ACTION_DATA_AVAILABLE = "BluetoothLeMedLinket.ACTION_DATA_AVAILABLE";

    public final static String DATATYPE_SPO2_MEASUREMENT = "BluetoothLeMedLinket.MEASUREMENT";
    public final static String DATATYPE_BATTERY_LEVEL = "BluetoothLeMedLinket.DATATYPE_BATTERY_LEVEL";
    public final static String MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE = "BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE";
    public final static String MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_TIMESTAMP = "BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_TIMESTAMP";

    public final static String MEDLINKET_CONTINUOUS_OXIMETRY__SPO2 = "BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__SPO2";
    public final static String MEDLINKET_CONTINUOUS_OXIMETRY__PULSE_RATE = "BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__PULSE_RATE";
    public final static String MEDLINKET_CONTINUOUS_OXIMETRY__TIMESTAMP = "BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__TIMESTAMP";
    public final static String MEDLINKET_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT = "BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT";
    public final static String MEDLINKET_CONTINUOUS_OXIMETRY__STATUS__SEARCHING = "BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__STATUS__SEARCHING";

    public final static String DATATYPE_PPG_DATA = "DATATYPE_PPG_DATA";
    public final static String PPG_DATA_POINTS = "PPG_DATA_POINTS";

    private boolean device_detached_from_patient;


    public BluetoothLeMedLinket(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time)
    {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__MEDLINKET);
    }


    @Override
    public void resetStateVariables()
    {
        Log.d(TAG, "resetStateVariables()");
        
        super.resetStateVariables();
    }


    public String getChildTag()
    {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
    }


    private static class PacketDetector
    {
        private final CircularArray<Byte> messageBuffer = new CircularArray<>(50);

        private byte[] message;

        public void AddByte(byte value)
        {
            messageBuffer.addLast(value);
        }

        public boolean CheckForCompleteMessage()
        {
            // Packet is 0xFF 0xFE Len Checksum ID Cmd Payload(n)

            // Remove any bytes from the start of the buffer until the first byte is 0xFF
            while (messageBuffer.size() > 0)
            {
                byte firstByte = messageBuffer.getFirst();
                if (firstByte != (byte)0xFF)
                {
                    messageBuffer.popFirst();
                }
                else
                {
                    // First byte IS 0xFF
                    break;
                }
            }

            // messageBuffer(0) is either 0xFF or messageBuffer is empty

            // Min bytes for a message is 7. Some messages are longer
            if (messageBuffer.size() < 7)
            {
                return false;    // Not a full message yet
            }

            byte secondByte = messageBuffer.get(1);

            if (secondByte != (byte)0xFE)
            {
                // First two bytes are NOT 0xFF 0xFE.
                // Remove them
                messageBuffer.popFirst();
                messageBuffer.popFirst();

                return false;    // Not a full message yet
            }

            // First byte is 0xFF and second is 0xFE. Maybe the start of a message

            byte length = messageBuffer.get(2);
            byte checksum = messageBuffer.get(3);
            byte id = messageBuffer.get(4);
            byte command = messageBuffer.get(5);
            // Payload follows (maybe)

            // The ID byte must be 0x23 for a valid message
            if (id != (byte)0x23)
            {
                // The 0xFE 0xFF found is NOT the start of a message
                messageBuffer.popFirst();   // 0xFF
                messageBuffer.popFirst();   // 0xFE
                messageBuffer.popFirst();   // Len
                messageBuffer.popFirst();   // Checksum
                messageBuffer.popFirst();   // ID

                return false;    // Not a full message yet
            }

            // If got here then 'id' IS 0x23

            // Check if enough bytes for the full message
            // 'length' does not include the first 2 bytes so include them here
            if (messageBuffer.size() < (length + 2))
            {
                // NOT enough bytes to make a full message....yet

                return false;    // Not a full message yet
            }

            // If got here then there ARE enough bytes for a full message
            // Does not mean its a valid message however

            // Calculate the Checksum byte
            byte calculatedCheckSum = (byte)(length + id + command);
            // and include the "payload"
            // Start at 6 as this is the offset in the circular buffer
            // Go until length+2 as that offsets the first two bytes that are NOT in the checksum
            for (int i = 6; i < length+2; i++)
            {
                calculatedCheckSum = (byte)(calculatedCheckSum + messageBuffer.get(i));
            }

            if (calculatedCheckSum != checksum)
            {
                // Checksum does not match so NOT a valid message

                return false;    // Not a full message yet
            }

            // Checksum matches. Therefore process this message

            int messageSize = length + 2;

            message = new byte[messageSize];

            for (int i = 0; i < messageSize; i++)
            {
                message[i] = messageBuffer.get(i);
            }

            return true;
        }

        public byte[] getMessage()
        {
            messageBuffer.clear();
            return message;
        }
    }

    private final PacketDetector packetDetector = new PacketDetector();

    private int convertBoolToInt(boolean value)
    {
        return value ? 1 : 0;
    }

    private void connectToNotification()
    {
        Log.d(TAG, "connectToNotification called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToNotification executing enableNotify");

            ble_device.enableNotify(UUID_SERVICE_MEDLINKET_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_MEDLINKET_OXIMETRY_MEASUREMENT, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToNotification", event);
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if(event.wasSuccess())
                        {
                            Log.d(TAG, "UUID_MEDLINKET_CONTINUOUS_OXIMETRY raw data : " + Utils.byteArrayToHexString(event.data()));

                            byte [] receivedData = event.data();

                            last_data_received_ntp_time = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();

                            // Feed this data into a class that syncs up and processes entire messages
                            for (byte x : receivedData)
                            {
                                packetDetector.AddByte(x);

                                boolean foundMessage = packetDetector.CheckForCompleteMessage();
                                if (foundMessage)
                                {
                                    byte[] message = packetDetector.getMessage();

                                    Log.d(TAG, "Received Medlinket Message : " + Utils.byteArrayToHexString(message));

                                    // First "payload" byte the message
                                    int index = 6;

                                    switch (message[5])
                                    {
                                        // SPO2 protocol Data Package 1 - SPO2 & PR Result Data
                                        case (byte)0x95:
                                        {
                                            // Payload "byte0"
                                            byte payloadByte0 = message[index++];

                                            final byte MASK__SYNC_BIT = (byte) (1 << 7);
                                            final byte MASK__PR_BIT7 = (byte) (1 << 6);
                                            final byte MASK__NO_PULSE = (byte) (1 << 5);
                                            final byte MASK__SEARCHING_FLAG = (byte) (1 << 4);
                                            final byte MASK__PROBE_STATE = (byte) 0x0F;

                                            String logLine = "* SPO2 and PR *";

                                            boolean syncBit = ((payloadByte0 & 0xFF) & MASK__SYNC_BIT) > 0;
                                            logLine = logLine + " : Byte0 syncBit = " + convertBoolToInt(syncBit);

                                            boolean pulseRateBit7 = (payloadByte0 & MASK__PR_BIT7) > 0;
                                            logLine = logLine + " : pulseRateBit7 = " + convertBoolToInt(pulseRateBit7);

                                            boolean noPulse = (payloadByte0 & MASK__NO_PULSE) > 0;
                                            logLine = logLine + " : noPulse = " + convertBoolToInt(noPulse);

                                            boolean searching = (payloadByte0 & MASK__SEARCHING_FLAG) > 0;
                                            logLine = logLine + " : searching = " + convertBoolToInt(searching);

                                            int probeState = payloadByte0 & MASK__PROBE_STATE;
                                            logLine = logLine + " : probeState = " + probeState;

                                            device_detached_from_patient = false;
                                            switch (probeState)
                                            {
                                                case 0:
                                                    //Log.d(TAG, "Probe State = Normal");
                                                    break;

                                                case 1:
                                                    //Log.d(TAG, "Probe State = Sensor Not Connected");
                                                    device_detached_from_patient = true;
                                                    break;

                                                case 2:
                                                    //Log.d(TAG, "Probe State = Current Overload");
                                                    break;

                                                case 3:
                                                    //Log.d(TAG, "Probe State = Sensor Error");
                                                    device_detached_from_patient = true;
                                                    break;

                                                case 4:
                                                    //Log.d(TAG, "Probe State = No Finger");
                                                    device_detached_from_patient = true;
                                                    break;
                                            }


                                            // Payload "byte1"
                                            byte payloadByte1 = message[index++];
                                            syncBit = ((payloadByte1 & 0xFF) & MASK__SYNC_BIT) > 0;
                                            logLine = logLine + " : Byte1 syncBit = " + convertBoolToInt(syncBit);

                                            int pulseRate = payloadByte1 & 0x7F;
                                            if (pulseRateBit7)
                                            {
                                                pulseRate = pulseRate + 128;
                                            }
                                            logLine = logLine + " : pulseRate = " + pulseRate;


                                            // Payload "byte2"
                                            byte payloadByte2 = message[index++];
                                            syncBit = ((payloadByte2 & 0xFF) & MASK__SYNC_BIT) > 0;
                                            logLine = logLine + " : Byte 2 syncBit = " + convertBoolToInt(syncBit);

                                            int spO2 = payloadByte2 & 0x7F;
                                            logLine = logLine + " : spO2 = " + spO2;


                                            // Payload "byte3"
                                            byte payloadByte3 = message[index++];

                                            // Payload "byte4"
                                            byte payloadByte4 = message[index++];

                                            // Payload "byte5"
                                            byte perfusionIndexInteger = message[index++];
                                            logLine = logLine + " : perfusionIndexInteger = " + perfusionIndexInteger;

                                            // Payload "byte6"
                                            byte perfusionIndexDecimal = message[index];
                                            logLine = logLine + " : perfusionIndexDecimal = " + perfusionIndexDecimal;

                                            logLine = logLine + " : Probe State = ";

                                            switch (probeState)
                                            {
                                                case 0:
                                                    logLine = logLine + " Normal";
                                                    break;

                                                case 1:
                                                    logLine = logLine + " Sensor Not Connected";
                                                    break;

                                                case 2:
                                                    logLine = logLine + " Current Overload";
                                                    break;

                                                case 3:
                                                    logLine = logLine + " Sensor Error";
                                                    break;

                                                case 4:
                                                    logLine = logLine + " No Finger";
                                                    break;

                                                default:
                                                    logLine = logLine + " Unknown state " + probeState;
                                                    break;
                                            }

                                            Log.d(TAG, logLine);

                                            // Protocol spreadsheet states that SpO2 values > 100 should be ignored (IIT-2691)
                                            if (spO2 <= 100)
                                            {
                                                Intent intent = new Intent();
                                                intent.setAction(ACTION_DATA_AVAILABLE);
                                                intent.putExtra(DATA_TYPE, DATATYPE_SPO2_MEASUREMENT);
                                                //intent.putExtra(MEDLINKET_CONTINUOUS_OXIMETRY__VALID_READING, valid_reading);
                                                intent.putExtra(MEDLINKET_CONTINUOUS_OXIMETRY__SPO2, spO2);
                                                intent.putExtra(MEDLINKET_CONTINUOUS_OXIMETRY__PULSE_RATE, pulseRate);
                                                intent.putExtra(MEDLINKET_CONTINUOUS_OXIMETRY__TIMESTAMP, last_data_received_ntp_time);
                                                intent.putExtra(MEDLINKET_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT, device_detached_from_patient);
                                                intent.putExtra(MEDLINKET_CONTINUOUS_OXIMETRY__STATUS__SEARCHING, searching);

                                                sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                                            }
                                            else
                                            {
                                                Log.d(TAG, "Not sending invalid reading of " + spO2);
                                            }
                                        }
                                        break;

                                        // SPO2 protocol Data Package 2 - PPG data
                                        case (byte)0x96:
                                        {
                                            last_data_received_ntp_time = ntp_time.currentTimeMillis();

                                            // Sampling at 50 Hz (10 samples at 5 Hz)
                                            final long TIME_BETWEEN_SAMPLES = 20;

                                            long setup_mode_sample_timestamp = ntp_time.currentTimeMillis();

                                            ArrayList<MeasurementSetupModeDataPoint> data_points = new ArrayList<>();

                                            for (int counter = 0; counter < 10; counter++)
                                            {
                                                // 7 bits
                                                byte sample = (byte)(message[index++] & 0x7F);
                                                byte theRest = (byte)(message[index++] & 0x7F);

                                                data_points.add(new MeasurementSetupModeDataPoint(sample, setup_mode_sample_timestamp));

                                                setup_mode_sample_timestamp = setup_mode_sample_timestamp + TIME_BETWEEN_SAMPLES;
                                            }

                                            Bundle bundle = new Bundle();
                                            bundle.putParcelableArrayList(PPG_DATA_POINTS, data_points);

                                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                                            intent.putExtra(DATA_TYPE, DATATYPE_PPG_DATA);
                                            intent.putExtras(bundle);
                                            sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                                        }
                                        break;

                                        // SPO2 protocol Data Package 3 : HRV data
                                        case (byte)0x97:
                                        {
                                            Log.d(TAG, "HRV data not processed");
                                        }
                                        break;

                                        // SPO2 protocol Data Package 4 : Battery level
                                        case (byte)0x99:
                                        {
                                            // reportDeviceBatteryLevel
                                            int battery_percentage = INVALID_MEASUREMENT;

                                            // Battery information is given as an integer 0-3
                                            // From email with MedLinket, the meaning of each number is:
                                            // 0= 0-5%, 1= 5%-33%,  2= 33%-67%, 3= 67-100%

                                            switch (message[6])
                                            {
                                                case 0: battery_percentage = 5; break;
                                                case 1: battery_percentage = 33; break;
                                                case 2: battery_percentage = 67; break;
                                                case 3: battery_percentage = 100; break;
                                                default: // nothing
                                            }

                                            if (battery_percentage > INVALID_MEASUREMENT)
                                            {
                                                Intent intent1 = new Intent();
                                                intent1.setAction(ACTION_DATA_AVAILABLE);
                                                intent1.putExtra(DATA_TYPE, DATATYPE_BATTERY_LEVEL);
                                                intent1.putExtra(MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE, battery_percentage);
                                                intent1.putExtra(MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_TIMESTAMP, ntp_time.currentTimeMillis());

                                                sendIntentWithDeviceAddressAndDataAsString(intent1, event.device().getMacAddress(), event.data());
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        else
                        {
                            Log.e(TAG, "UUID_MEDLINKET_CONTINUOUS_OXIMETRY read FAILED");
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
        // There is not Timestamp code for this device so connect to the data now

        connectToCharacteristics();
    }


    public void connectToCharacteristics()
    {
        Log.d(TAG, "connectToCharacteristics");

        startGettingData();
    }


    private void startGettingData()
    {
        Log.d(TAG, "startGettingData");

        connectToNotification();
    }


    @Override
    public void sendConnectedIntentAndContinueConnection()
    {
        // Several packets are longer than the default 20 byte MTU.
        // The Gateway HAS to support Bluetooth 4.2. The Tab A does but the Tab S does NOT
        // Setting MTU value to 100 (as previously) caused big problems with playback, usually the playback message would not be fully received
        Log.e(TAG, "sendConnectedIntentAndContinueConnection calling setMtu");
        ble_device.setMtu(247);

        sendConnectedIntent();

        Log.e(TAG, "sendConnectedIntentAndContinueConnection calling connectToCharacteristics");
        connectToCharacteristics();
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
        // Setup Mode Data is ALWAYS sent for this device
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
        // Not implemented on this device but function needs to exist
    }



    @Override
    public void onDiscovered(BleManager.DiscoveryListener.DiscoveryEvent event)
    {
        ble_device = event.device();

        just_discovered = true;

        int manufacturer_id = ble_device.getManufacturerId();

        Log.d(TAG, "onDiscovered FingerOx : " + manufacturer_id);

        BleDeviceConfig config = BleDeviceConfig.newNulled(); // get a nulled version, so that it doesn't override any of the ble manager config settings accidentally.

        // set the non-nullable settings to match the ones we have in the BleDeviceController
        config.autoEnableNotifiesOnReconnect = false;

        // set false if android 9, otherwise true
        config.alwaysBondOnConnect = false; //!isAndroidNine();

        ble_device.setConfig(config);

        if (event.was(BleManager.DiscoveryListener.LifeCycle.UNDISCOVERED))
        {
            Log.d(getChildTag(), getDescriptiveDeviceName() + " UNDISCOVERED");
        }
        else if(ble_device.is(BONDED) && !ble_device.is(CONNECTED, CONNECTING, CONNECTING_OVERALL))
        {
            Log.d(getChildTag(), "*************** onDiscovered FingerOx : " + ble_device.getMacAddress());

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
            Log.d(getChildTag(), "onDiscovered FingerOx : BONDING in progress");
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
