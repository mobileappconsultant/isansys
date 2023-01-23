package com.isansys.patientgateway.bluetooth.SpO2;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.format.DateUtils;

import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetooth.BluetoothService;
import com.isansys.common.enums.DeviceType;

public class NoninWristOx extends BluetoothService
{
    private final String TAG = this.getClass().getSimpleName();

    // Outgoing Intent names
    public final static String NONIN_WRIST_OX__CONNECTED = "com.isansys.patientgateway.NONIN_WRIST_OX__CONNECTED";
    public final static String NONIN_WRIST_OX__NEW_MEASUREMENT_DATA = "com.isansys.patientgateway.NONIN_WRIST_OX__NEW_MEASUREMENT_DATA";
    public final static String NONIN_WRIST_OX__NEW_SETUP_DATA_SAMPLE = "com.isansys.patientgateway.NONIN_WRIST_OX__NEW_SETUP_DATA_SAMPLE";
    public final static String NONIN_WRIST_OX__NEW_SETUP_DATA_FRAME_INFO = "com.isansys.patientgateway.NONIN_WRIST_OX__NEW_SETUP_DATA_FRAME_INFO";
    public final static String NONIN_WRIST_OX__DISCONNECTED = "com.isansys.patientgateway.NONIN_WRIST_OX__DISCONNECTED";
    public final static String NONIN_WRIST_OX__TEMPORARY_DISCONNECTION = "com.isansys.patientgateway.NONIN_WRIST_OX__TEMPORARY_DISCONNECTION";
    
    public final static String NONIN_WRIST_OX__DETACHED_FROM_PATIENT = "com.isansys.patientgateway.NONIN_WRIST_OX__DETACHED_FROM_PATIENT";
    public final static String NONIN_WRIST_OX__VALID_READING = "com.isansys.patientgateway.NONIN_WRIST_OX__VALID_READING";
    public final static String NONIN_WRIST_OX__HEART_RATE = "com.isansys.patientgateway.NONIN_WRIST_OX__HEART_RATE";
    public final static String NONIN_WRIST_OX__SP_O2 = "com.isansys.patientgateway.NONIN_WRIST_OX__SP_O2";
    public final static String NONIN_WRIST_OX__LOW_BATTERY = "com.isansys.patientgateway.NONIN_WRIST_OX__LOW_BATTERY";
    public final static String NONIN_WRIST_OX__CRITICALLY_LOW_BATTERY = "com.isansys.patientgateway.NONIN_WRIST_OX__CRITICALLY_LOW_BATTERY";
    public final static String NONIN_WRIST_OX__TIMESTAMP = "com.isansys.patientgateway.NONIN_WRIST_OX__TIMESTAMP";
    public final static String NONIN_WRIST_OX__ARTIFACT = "com.isansys.patientgateway.NONIN_WRIST_OX__ARTIFACT";
    public final static String NONIN_WRIST_OX__OUT_OF_TRACK = "com.isansys.patientgateway.NONIN_WRIST_OX__OUT_OF_TRACK";
    public final static String NONIN_WRIST_OX__LOW_PERFUSION = "com.isansys.patientgateway.NONIN_WRIST_OX__LOW_PERFUSION";
    public final static String NONIN_WRIST_OX__MARGINAL_PERFUSION = "com.isansys.patientgateway.NONIN_WRIST_OX__MARGINAL_PERFUSION";
    public final static String NONIN_WRIST_OX__SMART_POINT_ALGORITHM = "com.isansys.patientgateway.NONIN_WRIST_OX__SMART_POINT_ALGORITHM";
    public final static String NONIN_WRIST_OX__SETUP_MODE_SAMPLE = "com.isansys.patientgateway.NONIN_WRIST_OX__SETUP_MODE_SAMPLE";
    public final static String NONIN_WRIST_OX__SETUP_MODE_TIMESTAMP = "com.isansys.patientgateway.NONIN_WRIST_OX__SETUP_MODE_TIMESTAMP";
    
    public final static String NONIN_WRIST_OX__CHECKSUM_ERROR = "com.isansys.patientgateway.NONIN_WRIST_OX__CHECKSUM_ERROR";

    public final static String NONIN_WRIST_OX__SOFTWARE_REVISION = "com.isansys.patientgateway.NONIN_WRIST_OX__SOFTWARE_REVISION";
    public final static String NONIN_WRIST_OX__SOFTWARE_STRING = "com.isansys.patientgateway.NONIN_WRIST_OX__SOFTWARE_STRING";


    // Incoming Intent names
    public final static String NONIN_WRIST_OX__ENABLE_SETUP_MODE = "com.isansys.patientgateway.NONIN_WRIST_OX__ENABLE_SETUP_MODE";
    public final static String SETUP_MODE_ENABLED = "com.isansys.patientgateway.NONIN_WRIST_OX__ENABLE_SETUP_MODE.SETUP_MODE_ENABLED";

    public final static String SET_ADDED_TO_SESSION = "com.isansys.patientgateway.NONIN_WRIST_OX_SET_ADDED_TO_SESSION";
    public final static String SET_REMOVED_FROM_SESSION = "com.isansys.patientgateway.NONIN_WRIST_OX_SET_REMOVED_FROM_SESSION";


    private final static byte SERIAL_DATA_FORMAT_EIGHT__OOT_BIT = (byte)(0x20);
    private final static byte SERIAL_DATA_FORMAT_EIGHT__OOT_BYTE = 0;

    private final static byte SERIAL_DATA_FORMAT_EIGHT__LPRF_BIT = (byte)(0x10);
    private final static byte SERIAL_DATA_FORMAT_EIGHT__LPRF_BYTE = 0;

    private final static byte SERIAL_DATA_FORMAT_EIGHT__MPRF_BIT = (byte)(0x08);
    private final static byte SERIAL_DATA_FORMAT_EIGHT__MPRF_BYTE = 0;

    private final static byte SERIAL_DATA_FORMAT_EIGHT__ARTF_BIT = (byte)(0x04);
    private final static byte SERIAL_DATA_FORMAT_EIGHT__ARTF_BYTE = 0;

    private final static byte SERIAL_DATA_FORMAT_EIGHT__SPA_BIT = (byte)(0x20);
    private final static byte SERIAL_DATA_FORMAT_EIGHT__SPA_BYTE = 3;

    private final static byte SERIAL_DATA_FORMAT_EIGHT__FINGER_OFF_DETECTION_BIT = (byte)(0x08);
    private final static byte SERIAL_DATA_FORMAT_EIGHT__FINGER_OFF_DETECTION_BYTE = 3;
    
    private final static byte SERIAL_DATA_FORMAT_EIGHT__BATTERY_CONDITION_BIT = (byte)(0x01);
    private final static byte SERIAL_DATA_FORMAT_EIGHT__BATTERY_CONDITION_BYTE = 3;

    private final static byte SERIAL_DATA_FORMAT_EIGHT__STATUS_DETECTION_BIT = (byte)(0x80);

    private final static byte SERIAL_DATA_FORMAT_EIGHT__PACKET_SIZE_IN_BYTES = 4;


    private final static byte SERIAL_DATA_FORMAT_TWO__FRAME_SIZE_IN_BYTES = 5;
    private final static byte SERIAL_DATA_FORMAT_TWO__FRAMES_PER_PACKET = 25;
    private final static byte SERIAL_DATA_FORMAT_TWO__PACKET_SIZE_IN_BYTES = SERIAL_DATA_FORMAT_TWO__FRAME_SIZE_IN_BYTES * SERIAL_DATA_FORMAT_TWO__FRAMES_PER_PACKET;
    private final static byte SERIAL_DATA_FORMAT_TWO__STATUS_DETECTION_BITS = (byte)(0x81);
                                                                                        // Bit 0 is SYNC. Indicates its Frame 1. 0 on frames 2 through 25
                                                                                        // Bit 7 is always set in Byte 2
    private final static byte SERIAL_DATA_FORMAT_TWO__FRAME_START_BYTE = (byte)(0x01);

    private final static byte SERIAL_DATA_FORMAT_TWO__STAT2__LOW_BATTERY_BIT = (byte)(0x01);
    private final static byte SERIAL_DATA_FORMAT_TWO__STAT2__CRITICAL_BATTERY_BIT = (byte)(0x02);
    private final static byte SERIAL_DATA_FORMAT_TWO__STAT2__DEVICE_IN_SPOT_CHECK_BIT = (byte)(0x04);
    private final static byte SERIAL_DATA_FORMAT_TWO__STAT2__HIGH_QUALITY_SMARTPOINT_MEASUREMENT = (byte)(0x20);

    private final static byte SERIAL_DATA_FORMAT_TWO__PERFUSION_BITS = (byte)(0x06);
    private final static byte SERIAL_DATA_FORMAT_TWO__SNSF_BIT = (byte)(0x08);
    private final static byte SERIAL_DATA_FORMAT_TWO__OOT_BIT = (byte)(0x10);
    private final static byte SERIAL_DATA_FORMAT_TWO__ARTF_BIT = (byte)(0x20);


    private final static byte NONIN_COMMAND_RESPONSE__ACK = (byte)0x06;
    private final static byte NONIN_COMMAND_RESPONSE__NACK = (byte)0x15;

    private final static byte NONIN_BLUETOOTH_TIMEOUT_RESPONSE_PACKET_LENGTH = 8;


    public enum NoninTurnOnMode
    {
        INSERT_SENSOR,
        INSERT_FINGER
    }

    private final NoninTurnOnMode nonin_turn_on_mode = NoninTurnOnMode.INSERT_FINGER;


    private final Queue<byte[]> rxFifo = new LinkedList<>();

    private byte byte_one;
    private byte byte_two;
    private byte byte_three;
    private byte byte_four;
    private byte byte_five;
    
    private boolean just_synced;

    private int frame_counter = 0;

    private int hr_upper_msb = 0;
    private int hr_lower_lsb = 0;
    private int heart_rate = 0;

    private int sp_o2 = 0;

    private int tmr_msb = 0;
    private int tmr_lsb = 0;

    private int ehr_upper_msb = 0;
    private int ehr_lower_lsb = 0;
    private int extended_average_heart_rate = 0;

    private int extended_sp_o2 = 0;

    private int spo2_fast = 0;
    private int spo2_beat_to_beat = 0;

    private int software_rev = 0;

    private boolean valid_reading;

    private boolean low_battery_condition;
    private boolean critically_low_battery_condition;

    private boolean spot_mode;

    private boolean device_detached_from_patient;                                               // True when Nonin not connected to Patient

    private boolean artifact;                                                                   // Indicated artifact condition on each pulse
    private boolean out_of_track;                                                               // An absence of consecutive good pulse signals
    private boolean low_perfusion;                                                              // Amplitude representation of low/no signal quality (holds for entire duration).
    private boolean marginal_perfusion;                                                         // Amplitude representation of low/marginal signal quality (holds for entire duration).
    private boolean smartpoint_algorithm;                                                       // High quality SmartPoint measurement

    private double frame_start_timestamp;

    public enum NoninWristOxOperatingMode
    {
        ENTERING_NORMAL_MODE,
        NORMAL_MODE,
        ENTERING_SETUP_MODE,                                                            // Send the Enter Setup Mode command
        SETUP_MODE_SYNC,
        SETUP_MODE_RECEIVING_DATA
    }

    private static volatile NoninWristOxOperatingMode operating_mode = NoninWristOxOperatingMode.ENTERING_NORMAL_MODE;
    private static volatile NoninWristOxOperatingMode desired_operating_mode = NoninWristOxOperatingMode.NORMAL_MODE;
    private static volatile boolean timeout_set_required = true;

    private final byte[] set_bluetooth_timeout_successful_response = {(byte)0x02, (byte)0xF5, (byte)0x04, (byte)0x04, (byte)0x00, (byte)0x01, (byte)0x05, (byte)0x03};

    public enum ConfigurationCommandStateMachine
    {
        SET_OPERATING_MODE,
        SETUP_MODE_SYNC,
        SET_BLUETOOTH_TIMEOUT,
        CONFIGURATION_COMPLETE
    }

    private static final ConfigurationCommandStateMachine initial_command = ConfigurationCommandStateMachine.SET_OPERATING_MODE;

    private static ConfigurationCommandStateMachine next_configuration_command = initial_command;


    public NoninWristOx()
    {
        super("NoninWristOx", DeviceConnectingMode.CONNECT_TO_DEVICE, true, true);
    }


    public String getBluetoothDeviceName()
    {
        return "Nonin_Medical_Inc";
    }


    public String getDesiredDeviceBluetoothAddress()
    {
        return PatientGatewayService.device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX).bluetooth_address;
    }


    private void executeConfigurationCommands()
    {
        Log.d(TAG, "executeConfigurationCommands : " + operating_mode + " : " + next_configuration_command);

        switch (next_configuration_command)
        {
            case SET_BLUETOOTH_TIMEOUT:
            {
                rxFifo.clear();
                Log.d(TAG, "executeConfigurationCommands : rxFifo cleared");

                byte timeout_in_minutes = 0;                                            // 0 means the Bluetooth Timeout is disabled

                Log.i(TAG, "Sending SET_BLUETOOTH_TIMEOUT command. Timeout = " + timeout_in_minutes + " minutes");

                setNoninBluetoothTimeout(timeout_in_minutes);

                timeout_set_required = false;
            }
            break;

            case SETUP_MODE_SYNC:
            {
                boolean buffer_empty = setupModeSync();
                if (buffer_empty)
                {
                    Log.i(TAG, "Buffer empty after setupModeSync");
                }
                else
                {
                    Log.e(TAG, "Buffer not empty after setupModeSync");
                }
            }
            break;

            case SET_OPERATING_MODE:
            {
                rxFifo.clear();
                Log.d(TAG, "executeConfigurationCommands : rxFifo cleared");

                Log.i(TAG, "Sending SEND_OPERATING_MODE command : " + operating_mode);

                switch(operating_mode)
                {
                    case ENTERING_NORMAL_MODE:
                    {
                        setNoninWristOxConfig_DataFormat8();
                    } 
                    break;
                    
                    case NORMAL_MODE:
                    {
                        Log.e(TAG, "executeConfigurationCommands : NORMAL_MODE -  Alert shouldn't be there !!!!!");
                    }
                    break;
                    
                    case ENTERING_SETUP_MODE:
                    {
                        setNoninWristOxConfig_DataFormat2();
                    }
                    break;
                    
                    case SETUP_MODE_SYNC:
                    case SETUP_MODE_RECEIVING_DATA:
                    {
                        Log.e(TAG, "executeConfigurationCommands : SETUP_MODE -  Alert shouldn't be there !!!!!");
                    }
                    break;
                }
            }
            break;

            case CONFIGURATION_COMPLETE:
            {
                rxFifo.clear();
                Log.d(TAG, "executeConfigurationCommands : rxFifo cleared");
            }
            break;
        }
    }


    private void resetConfigurationCommandStateMachine()
    {
        next_configuration_command = initial_command;
        timeout_set_required = true;

        operating_mode = NoninWristOxOperatingMode.ENTERING_NORMAL_MODE;
        desired_operating_mode = NoninWristOxOperatingMode.NORMAL_MODE;
    }


    private void resetConfigurationCommandStateMachineAndExecuteFirstCommand()
    {
        Log.d(TAG, "resetConfigurationCommandStateMachineAndExecuteFirstCommand");

        resetConfigurationCommandStateMachine();

        executeConfigurationCommands();
    }


    public void handleMessageRead(byte[] bluetooth_read_buffer)
    {
        String log_line = "handleMessageRead : ";

        // Put data into the FIFO
        try
        {
            for (byte value : bluetooth_read_buffer)
            {
                byte[] tempByteArray = new byte[1];

                tempByteArray[0] = value;

                rxFifo.add(tempByteArray);
            }

            //log_line = log_line + Utils.byteArrayToHexString(bluetooth_read_buffer);

            switch (next_configuration_command)
            {
                case SET_BLUETOOTH_TIMEOUT:
                {
                    setBluetoothTimeout(log_line, bluetooth_read_buffer);
                }
                break;

                case SET_OPERATING_MODE:
                {
                    setOperatingMode(log_line, bluetooth_read_buffer);
                }
                break;

                case SETUP_MODE_SYNC:
                {
                    executeConfigurationCommands();
                }
                break;

                case CONFIGURATION_COMPLETE:
                {
                    Log.e(TAG, "Raw data from Pulse Ox (" + bluetooth_read_buffer.length + ") : " + log_line);

                    // Process FIFO data
                    boolean buffer_empty = false;
                    while(buffer_empty == false)
                    {
                        switch (operating_mode)
                        {
                            case NORMAL_MODE:
                            {
                                buffer_empty = normalModeRead();
                            }
                            break;

                            case SETUP_MODE_SYNC:
                            {
                                buffer_empty = setupModeSync();
                            }
                            break;

                            case SETUP_MODE_RECEIVING_DATA:
                            {
                                buffer_empty = setupModeReceivingData();
                            }
                            break;

                            case ENTERING_NORMAL_MODE:
                            case ENTERING_SETUP_MODE:
                            {
                                Log.e(TAG, "handleMessageRead : packet received for ENTERING_SETUP_MODE or ENTERING_NORMAL_MODE. This packet should be ignored");

                                return;
                            }
                        }
                    }

                    Log.d(TAG, "handleMessageRead : " + operating_mode + " : buffer is empty (or not enough bytes for a full message)");

                    if(desired_operating_mode != operating_mode)
                    {
                        if(desired_operating_mode == NoninWristOxOperatingMode.SETUP_MODE_RECEIVING_DATA)
                        {
                            enterSetupMode();
                        }
                        else if(desired_operating_mode == NoninWristOxOperatingMode.NORMAL_MODE)
                        {
                            enterNormalMode();
                        }
                        else
                        {
                            Log.d(TAG, "handleMessageRead : desired_operating_mode unexpected value : " + desired_operating_mode);
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "MESSAGE_READ : Exception = " + e.toString());
        }
    }

    public void handleMessageStateChange(State state)
    {
        switch (state)
        {
            case STATE_NONE:
            {
                if (getState() == State.STATE_NONE)
                {
                    connectToDeviceIfPaired(getBluetoothDeviceName());
                }
            }
            break;

            case STATE_CONNECTED:
            {
                reportConnected();
            }
            break;

            case STATE_CONNECTING:
            {
                reportTemporaryDisconnection();
            }
            break;

            case STATE_LISTEN:
            {
            }
            break;

            case STATE_CONNECTION_TIMEOUT:
            {
                if(restarting_device_connection)
                {
                    Log.d(TAG, "BluetoothService.STATE_CONNECTION_TIMEOUT and restarting_device_connection is TRUE");
                }
                else
                {
                    Log.d(TAG, "BluetoothService.STATE_CONNECTION_TIMEOUT : Posting delayed runnable (" + reconnection_time_in_milliseconds/(int) DateUtils.SECOND_IN_MILLIS + " seconds) to attempt reconnection");

                    restarting_device_connection = true;

                    start_reconnection_handler.removeCallbacks(restart_connection_runnable);

                    start_reconnection_handler.postDelayed(restart_connection_runnable, reconnection_time_in_milliseconds);
                }
            }
            break;

            default:
                break;
        }
    }


    private boolean normalModeRead()
    {
        int lengthOfDataInBuffer = rxFifo.size();
        boolean buffer_empty = false;

        // Find the start byte of the packet
        while(lengthOfDataInBuffer > 0)
        {
            if((rxFifo.peek()[0] & SERIAL_DATA_FORMAT_EIGHT__STATUS_DETECTION_BIT)!=0)
            {
                // Wait until Bit 7 is set. This identifies that byte as Byte 1
                lengthOfDataInBuffer = 0;
            }
            else
            {
                lengthOfDataInBuffer--;
                rxFifo.remove();
            }
        }

        // If not enough data, just exit
        if(rxFifo.size() < SERIAL_DATA_FORMAT_EIGHT__PACKET_SIZE_IN_BYTES)
        {
            Log.d(TAG, "Insufficient data in nonin rx buffer, returning");

            buffer_empty = true;

            return buffer_empty;
        }

        // read the next 4-byte chunk of normal mode data
        normalModeDecode();

        // if no full packet left, return true, otherwise false.
        if(rxFifo.size() < SERIAL_DATA_FORMAT_EIGHT__PACKET_SIZE_IN_BYTES)
        {
            buffer_empty = true;
        }

        return buffer_empty;
    }


    // Returns a boolean acting as Buffer Empty
    private boolean setupModeSync()
    {
        boolean buffer_empty;

        // Wait for at least a full packet (25 * 5 bytes) of data. These bytes will probably be the end of Frame 1 and the start of Frame 2
        if(rxFifo.size() < SERIAL_DATA_FORMAT_TWO__PACKET_SIZE_IN_BYTES)
        {
            Log.d(TAG, "Not enough data yet. rxFifo.size = " + rxFifo.size());
            buffer_empty = true;

            return buffer_empty;
        }

        if (checkForSetupModeSync())
        {
            // Setup mode is synced!
            Log.d(TAG, "setupModeSync : Synced to Setup Mode datastream. rxFifo.size = " + rxFifo.size());

            // There may be more data to process in the buffer
            buffer_empty = false;
        }
        else
        {
            Log.d(TAG, "Processed all the pending data but didn't find a checksum - so return true and wait for more data");
            buffer_empty = true;
        }

        return buffer_empty;
    }


    private boolean setupModeReceivingData()
    {
        boolean buffer_empty;

        // While at least 5 bytes pending (are already sync'ed at this point)
        if (rxFifo.size() >= SERIAL_DATA_FORMAT_TWO__FRAME_SIZE_IN_BYTES)
        {
            while (rxFifo.size() >= SERIAL_DATA_FORMAT_TWO__FRAME_SIZE_IN_BYTES)
            {
                // If we have just entered this state, it means we have already read Frame 0 in SETUP_MODE_SYNC
                if (just_synced)
                {
                    just_synced = false;
                }
                else
                {
                    // Process a frames worth of bytes
                    byte_one = rxFifo.poll()[0];
                    byte_two = rxFifo.poll()[0];
                    byte_three = rxFifo.poll()[0];
                    byte_four = rxFifo.poll()[0];
                    byte_five = rxFifo.poll()[0];
                }

                byte checksum = (byte)(byte_one + byte_two + byte_three + byte_four);

                // Should be synced already. Do an additional check every 25 frames to ensure still synced
                if (frame_counter == 0)
                {
                    if (byte_one != SERIAL_DATA_FORMAT_TWO__FRAME_START_BYTE)
                    {
                        // Somehow the syncing has got screwed up.
                        Log.e(TAG, "Syncing/checksum problem. Clearing rxFifo : SERIAL_DATA_FORMAT_TWO__FRAME_START_BYTE");

                        resetConfigurationCommandStateMachineAndExecuteFirstCommand();

                        // Report Gateway about checksum error and reset the graphview in Portal
                        reportNoninCheckSumError();

                        buffer_empty = true;

                        return buffer_empty;
                    }
                }

                if (checksum != byte_five)
                {
                    // Somehow the syncing has got screwed up.
                    Log.e(TAG, "Syncing/checksum problem. Clearing rxFifo : checksum = " + Utils.convertByteToString(checksum) + " and expected " + Utils.convertByteToString(byte_five));

                    resetConfigurationCommandStateMachineAndExecuteFirstCommand();

                    // Report Gateway about checksum error and reset the graphview in Portal
                    reportNoninCheckSumError();

                    buffer_empty = true;

                    return buffer_empty;
                }

                // read a whole setup mode frame. we go round this loop until there are no more full frames to process.
                setupModeDecodeFrame();
            } // While

            buffer_empty = true;
        }
        else
        {
            buffer_empty = true;
        }

        return buffer_empty;
    }


    private void setupModeDecodeFrame()
    {
        short setup_mode_sample;
        double setup_mode_timestamp;
        artifact = Utils.convertToBoolean(byte_two & SERIAL_DATA_FORMAT_TWO__ARTF_BIT);
        out_of_track = Utils.convertToBoolean(byte_two & SERIAL_DATA_FORMAT_TWO__OOT_BIT);
        device_detached_from_patient = Utils.convertToBoolean(byte_two & SERIAL_DATA_FORMAT_TWO__SNSF_BIT);

        int perfusion = byte_two & SERIAL_DATA_FORMAT_TWO__PERFUSION_BITS;
        perfusion = perfusion >> 1;

        switch (perfusion)
        {
            case 1:
            {
                // Green Perfusion : Amplitude representation of high signal quality (occurs only during pulse)
                low_perfusion = false;
                marginal_perfusion = false;
            }
            break;

            case 2:
            {
                // Red Perfusion : Amplitude representation of low/poor signal quality (occurs only during pulse)
                low_perfusion = true;
                marginal_perfusion = false;
            }
            break;

            case 3:
            {
                // Yellow Perfusion : Amplitude representation of low/marginal signal quality (occurs only during pulse)
                low_perfusion = false;
                marginal_perfusion = true;
            }
            break;
        }

        //pleth_data[frame_counter] = (short)(byte_three & 0xFF);
        setup_mode_sample = (short)(byte_three & 0xFF);

// Haven't worked out how this time is set initially, so for the time being, we are faking the timestamps
/*
                    float time_in_milliseconds_since_start_of_frame = frame_counter * time_between_frames;
                    setup_mode_timestamps[frame_counter] = frame_start_timestamp + (long)time_in_milliseconds_since_start_of_frame;
*/
        float TIME_BETWEEN_FRAMES = (float) DateUtils.SECOND_IN_MILLIS / (float) 75;
        frame_start_timestamp = frame_start_timestamp + TIME_BETWEEN_FRAMES;
        setup_mode_timestamp = frame_start_timestamp;

        frame_counter++;

        // Frame counter starts from 1 in the PDF, so using the ++ from above to count from 1 instead of 0.
        switch (frame_counter)
        {
            case 1:     // HR MSB (4-beat Pulse Rate Average)
            {
                hr_upper_msb = byte_four & 0x03;
                                        // Bottom 2 bits of byte 0 are the top two bits of the Heart Rate
            }
            break;

            case 2:     // HR LSB (4-beat Pulse Rate Average)
            {
                hr_lower_lsb = byte_four & 0x7F;
                                        // Bottom 7 bits are Heart Rate

                heart_rate = (hr_upper_msb << 7) + hr_lower_lsb;
            }
            break;

            case 3:     // SpO2 (4-beat SpO2 Average)
            {
                sp_o2 = byte_four & 0x7F;
                                        // Bottom 7 bits are the SpO2 level
            }
            break;

            case 4:     // Software Rev
            {
                software_rev = byte_four & 0x07F;
            }
            break;

            case 5:     // Reserved
            {
                // Nothing to do
            }
            break;

            case 6:     // TMR MSB (7 bits)
            {
                tmr_msb = byte_four & 0x7F;
            }
            break;

            case 7:     // TMR LSB (7 bits)
            {
                tmr_lsb = byte_four & 0x7F;

                int time = tmr_msb << 15;
                time = time + tmr_lsb;
            }
            break;

            case 8:     // STAT2
            {
                low_battery_condition = Utils.convertToBoolean(byte_four & SERIAL_DATA_FORMAT_TWO__STAT2__LOW_BATTERY_BIT);

                critically_low_battery_condition = Utils.convertToBoolean(byte_four & SERIAL_DATA_FORMAT_TWO__STAT2__CRITICAL_BATTERY_BIT);

                spot_mode = Utils.convertToBoolean(byte_four & SERIAL_DATA_FORMAT_TWO__STAT2__DEVICE_IN_SPOT_CHECK_BIT);

                smartpoint_algorithm = Utils.convertToBoolean(byte_four & SERIAL_DATA_FORMAT_TWO__STAT2__HIGH_QUALITY_SMARTPOINT_MEASUREMENT);
            }
            break;

            case 9:     // SpO2-D (formatted for display purposes)
            {
                // Nothing to do
            }
            break;

            case 10:    // SpO2 Fast (4-beat Average optimised for fast responding)
            {
                spo2_fast = byte_four & 0x07F;
            }
            break;

            case 11:    // SpO2 B-B (Beat to Beat value - No Average)
            {
                spo2_beat_to_beat = byte_four & 0x07F;
            }
            break;

            case 12:    // Reserved
            case 13:    // Reserved
            {
                // Nothing to do
            }
            break;

            case 14:    // E-HR MSB (8-beat Pulse Rate Extended Average)
            {
                ehr_upper_msb = byte_four & 0x03;
                                        // Bottom 2 bits of byte 0 are the top two bits of the Heart Rate
            }
            break;

            case 15:    // E-HR LSB (8-beat Pulse Rate Extended Average)
            {
                ehr_lower_lsb = byte_four & 0x7F;
                                        // Bottom 7 bits are Heart Rate

                extended_average_heart_rate = (ehr_upper_msb << 7) + ehr_lower_lsb;
            }
            break;

            case 16:    // E-SPO2 (8-beat SpO2 Extended Average)
            {
                extended_sp_o2 = byte_four & 0x7F;
                                        // Bottom 7 bits are the SpO2 level
            }
            break;

            case 17:    // E-SPO2-D (formatted for display purposes)
            case 18:    // Reserved
            case 19:    // Reserved
            case 20:    // HR-D MSB (formatted for display purposes)
            case 21:    // HR-D LSB (formatted for display purposes)
            case 22:    // E-HR-D MSB (formatted for display purposes)
            case 23:    // E-HR-D LSB (formatted for display purposes)
            case 24:    // Reserved
            case 25:    // Reserved
            default:
            {
                // Nothing to do
            }
            break;
        }

        // Missing data indicators
        if ((extended_sp_o2 == 127) || (extended_average_heart_rate == 511))
        {
            valid_reading = false;
        }
        else
        {
            valid_reading = true;
        }

        // Only report setup mode data if we're supposed to be in setup mode, as sometimes the nonin will keep sending
        // setup mode data after we've told it to exit setup mode
        if(desired_operating_mode == NoninWristOxOperatingMode.SETUP_MODE_RECEIVING_DATA)
        {
            final Intent intent = new Intent(NONIN_WRIST_OX__NEW_SETUP_DATA_SAMPLE);
            intent.putExtra(NONIN_WRIST_OX__SETUP_MODE_SAMPLE, setup_mode_sample);
            intent.putExtra(NONIN_WRIST_OX__SETUP_MODE_TIMESTAMP, (long) setup_mode_timestamp);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

            // Output all the info we only know after rx'ing a full frame
            if (frame_counter == 25)
            {
                final Intent frame_info_intent = new Intent(NONIN_WRIST_OX__NEW_SETUP_DATA_FRAME_INFO);
                frame_info_intent.putExtra(NONIN_WRIST_OX__VALID_READING, valid_reading);
                frame_info_intent.putExtra(NONIN_WRIST_OX__HEART_RATE, extended_average_heart_rate);
                frame_info_intent.putExtra(NONIN_WRIST_OX__SP_O2, extended_sp_o2);
// Don't have a Timestamp yet...as unsure when this should be as we are not using the time from the Nonin as unsure where there is init'ed
//                                                          intent.putExtra(TIMESTAMP, timestamp_in_ms);
                frame_info_intent.putExtra(NONIN_WRIST_OX__LOW_BATTERY, low_battery_condition);
                frame_info_intent.putExtra(NONIN_WRIST_OX__CRITICALLY_LOW_BATTERY, critically_low_battery_condition);
                frame_info_intent.putExtra(NONIN_WRIST_OX__DETACHED_FROM_PATIENT, device_detached_from_patient);
                frame_info_intent.putExtra(NONIN_WRIST_OX__ARTIFACT, artifact);
                frame_info_intent.putExtra(NONIN_WRIST_OX__OUT_OF_TRACK, out_of_track);
                frame_info_intent.putExtra(NONIN_WRIST_OX__LOW_PERFUSION, low_perfusion);
                frame_info_intent.putExtra(NONIN_WRIST_OX__MARGINAL_PERFUSION, marginal_perfusion);
                frame_info_intent.putExtra(NONIN_WRIST_OX__SMART_POINT_ALGORITHM, smartpoint_algorithm);
                frame_info_intent.putExtra(NONIN_WRIST_OX__SOFTWARE_REVISION, software_rev);
                frame_info_intent.putExtra(NONIN_WRIST_OX__SOFTWARE_STRING, Integer.toString(software_rev));
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(frame_info_intent);
            }
        }

        if (frame_counter == 25)
        {
            // Send Spo2 and pulse rate(NEW_MEASUREMENT_DATA) to PatientGatewayService.
            sendNoninMeasurementsFromSetupMode();

            frame_counter = 0;
        }
    }


    private boolean checkForSetupModeSync()
    {
        frame_counter = 0;

        // There might be multiple packets worth of data pending now. Process all of them. Three packets are transmitted every second
        while(rxFifo.size() >= SERIAL_DATA_FORMAT_TWO__PACKET_SIZE_IN_BYTES)
        {
            // Find the start byte of the packet from within SERIAL_DATA_FORMAT_TWO__PACKET_SIZE_IN_BYTES
            byte_one = rxFifo.poll()[0];
            byte_two = rxFifo.poll()[0];
            byte_three = rxFifo.poll()[0];
            byte_four = rxFifo.poll()[0];
            byte_five = rxFifo.poll()[0];

            byte calculated_checksum = (byte)(byte_one + byte_two + byte_three + byte_four);

            // Packet start is 0x01 followed by a byte with bit 0 and 7 set.
            // This can occur in the data stream, so also make sure the checksum matches to ensure we lock at the right part.
            if((byte_one == SERIAL_DATA_FORMAT_TWO__FRAME_START_BYTE)
                    && ((byte_two & SERIAL_DATA_FORMAT_TWO__STATUS_DETECTION_BITS) == SERIAL_DATA_FORMAT_TWO__STATUS_DETECTION_BITS)
                    && (calculated_checksum == byte_five))
            {
                // Found the start of the Packet
                Log.i(TAG, "checkForSetupModeSync : Synced. Bytes pending = " + rxFifo.size());

                next_configuration_command = ConfigurationCommandStateMachine.CONFIGURATION_COMPLETE;
                operating_mode = NoninWristOxOperatingMode.SETUP_MODE_RECEIVING_DATA;

                just_synced = true;

                return true;
            }
            else
            {
                // Looking for the start of the Packet (25 frames of 5 bytes)
            }
        } // While

        return false;
    }


    private void normalModeDecode()
    {
        // Extract 'noninWristOxDataPacketSizeInBytes' bytes
        byte[] bluetooth_data = new byte[SERIAL_DATA_FORMAT_EIGHT__PACKET_SIZE_IN_BYTES];

        for(int tempCounter=0;tempCounter<SERIAL_DATA_FORMAT_EIGHT__PACKET_SIZE_IN_BYTES;tempCounter++)
        {
            bluetooth_data[tempCounter] = rxFifo.poll()[0];         // This is a 1 byte fifo
        }

        // Start work to extract packet data
        device_detached_from_patient = Utils.convertToBoolean(bluetooth_data[SERIAL_DATA_FORMAT_EIGHT__FINGER_OFF_DETECTION_BYTE] & SERIAL_DATA_FORMAT_EIGHT__FINGER_OFF_DETECTION_BIT);

        // The Wrist Ox doesn't give us the time when using Serial Data Format #8. So use the time of when the measurement was received. This will be a maximum of one second
        // out as this is how often the measurement is taken on the Wrist Ox.
        long timestamp_in_ms = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();

        hr_upper_msb = bluetooth_data[0] & 0x03;    // Bottom 2 bits of byte 0 are the top two bits of the Heart Rate
        hr_lower_lsb = bluetooth_data[1] & 0x7F;    // Bottom 7 bits are Heart Rate
        heart_rate = (hr_upper_msb << 7) + hr_lower_lsb;

        sp_o2 = bluetooth_data[2] & 0x7F;           // Bottom 7 bits are SPO2;

        low_battery_condition = Utils.convertToBoolean(bluetooth_data[SERIAL_DATA_FORMAT_EIGHT__BATTERY_CONDITION_BYTE] & SERIAL_DATA_FORMAT_EIGHT__BATTERY_CONDITION_BIT);

        artifact = Utils.convertToBoolean(bluetooth_data[SERIAL_DATA_FORMAT_EIGHT__ARTF_BYTE] & SERIAL_DATA_FORMAT_EIGHT__ARTF_BIT);
        // Indicated artifact condition on each pulse
        out_of_track = Utils.convertToBoolean(bluetooth_data[SERIAL_DATA_FORMAT_EIGHT__OOT_BYTE] & SERIAL_DATA_FORMAT_EIGHT__OOT_BIT);
        // An absence of consecutive good pulse signals
        low_perfusion = Utils.convertToBoolean(bluetooth_data[SERIAL_DATA_FORMAT_EIGHT__LPRF_BYTE] & SERIAL_DATA_FORMAT_EIGHT__LPRF_BIT);
        // Amplitude representation of low/no signal quality (holds for entire duration).
        marginal_perfusion = Utils.convertToBoolean(bluetooth_data[SERIAL_DATA_FORMAT_EIGHT__MPRF_BYTE] & SERIAL_DATA_FORMAT_EIGHT__MPRF_BIT);
        // Amplitude representation of low/marginal signal quality (holds for entire duration).
        smartpoint_algorithm = Utils.convertToBoolean(bluetooth_data[SERIAL_DATA_FORMAT_EIGHT__SPA_BYTE] & SERIAL_DATA_FORMAT_EIGHT__SPA_BIT);
        // High quality SmartPoint measurement

        // Missing data indicators
        if ((sp_o2 == 127) || (heart_rate == 511))
        {
            valid_reading = false;
        }
        else
        {
            valid_reading = true;
        }


//                                            Log.i(TAG, "MESSAGE_READ : "
//                                                    + ". valid_reading = " + Utils.padRight(String.valueOf(valid_reading), 5)
//                                                    + ". device_detached_from_patient = " + Utils.padRight(String.valueOf(device_detached_from_patient), 5)
//                                                    + ". sp_o2 " + Utils.padRight(String.valueOf(sp_o2), 4)
//                                                    + ". heart_rate = " + Utils.padRight(String.valueOf(heart_rate), 4)
//                                                    + ". low_battery_condition = " + Utils.padRight(String.valueOf(low_battery_condition), 5)
//                                                    + ". artifact = " + Utils.padRight(String.valueOf(artifact), 5)
//                                                    + ". out_of_track = " + Utils.padRight(String.valueOf(out_of_track), 5)
//                                                    + ". low_perfusion = " + Utils.padRight(String.valueOf(low_perfusion), 5)
//                                                    + ". marginal_perfusion = " + Utils.padRight(String.valueOf(marginal_perfusion), 5)
//                                                    + ". smartpoint_algorithm = " + Utils.padRight(String.valueOf(smartpoint_algorithm), 5)
//                                                    );

        Intent intent = new Intent();
        intent.setAction(NONIN_WRIST_OX__NEW_MEASUREMENT_DATA);
        intent.putExtra(NONIN_WRIST_OX__VALID_READING, valid_reading);
        intent.putExtra(NONIN_WRIST_OX__HEART_RATE, heart_rate);
        intent.putExtra(NONIN_WRIST_OX__SP_O2, sp_o2);
        intent.putExtra(NONIN_WRIST_OX__TIMESTAMP, timestamp_in_ms);
        intent.putExtra(NONIN_WRIST_OX__LOW_BATTERY, low_battery_condition);
        intent.putExtra(NONIN_WRIST_OX__DETACHED_FROM_PATIENT, device_detached_from_patient);
        intent.putExtra(NONIN_WRIST_OX__ARTIFACT, artifact);
        intent.putExtra(NONIN_WRIST_OX__OUT_OF_TRACK, out_of_track);
        intent.putExtra(NONIN_WRIST_OX__LOW_PERFUSION, low_perfusion);
        intent.putExtra(NONIN_WRIST_OX__MARGINAL_PERFUSION, marginal_perfusion);
        intent.putExtra(NONIN_WRIST_OX__SMART_POINT_ALGORITHM, smartpoint_algorithm);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }


    private void setOperatingMode(String log_line, byte[] bluetooth_read_buffer)
    {
        Log.i(TAG, "SET_OPERATING_MODE : " + operating_mode + " : length = " + bluetooth_read_buffer.length + " : buffer : " + log_line);

        if (bluetooth_read_buffer[0] == NONIN_COMMAND_RESPONSE__ACK)
        {
            Log.i(TAG, "SET_OPERATING_MODE ACK'ed");

            if(timeout_set_required)
            {
                next_configuration_command = ConfigurationCommandStateMachine.SET_BLUETOOTH_TIMEOUT;
            }
            else
            {
                next_configuration_command = ConfigurationCommandStateMachine.CONFIGURATION_COMPLETE;
            }

            switch (operating_mode)
            {
                case ENTERING_NORMAL_MODE:
                    operating_mode = NoninWristOxOperatingMode.NORMAL_MODE;
                    break;

                case ENTERING_SETUP_MODE:
                    operating_mode = NoninWristOxOperatingMode.SETUP_MODE_SYNC;
                    break;

                default:
                    Log.e(TAG, "setOperatingMode : ALERT ALERT operating_mode = " + operating_mode.toString() + ". Expected to be ENTERING_NORMAL_MODE or ENTERING_SETUP_MODE");
                    break;
            }
        }
        else if(bluetooth_read_buffer[0] == NONIN_COMMAND_RESPONSE__NACK)
        {
            if(operating_mode == NoninWristOxOperatingMode.ENTERING_NORMAL_MODE)
            {
                Log.e(TAG, "NONIN_COMMAND_RESPONSE__NACK : Send again the NORMAL_MODE command to NONIN.........");

                // Send again Normal operation command to the Nonin
                next_configuration_command = ConfigurationCommandStateMachine.SET_OPERATING_MODE;
                operating_mode = NoninWristOxOperatingMode.ENTERING_NORMAL_MODE;
            }
            else
            {
                Log.e(TAG, "NONIN_COMMAND_RESPONSE__NACK : Sending again the SETUP_MODE command to Nonin.........");

                // Send again Setup operation command to the Nonin
                next_configuration_command = ConfigurationCommandStateMachine.SET_OPERATING_MODE;
                operating_mode = NoninWristOxOperatingMode.ENTERING_SETUP_MODE;
            }
        }
        // Check if packet received contains data and ACK value
        else if(bluetooth_read_buffer.length == 5)
        {
            // Check if the first bit of received packet is 0x06 or 0x80 and length is less than 5 or equal to 5 bytes. If so, transition to normal mode is successful
            if((bluetooth_read_buffer[4] == NONIN_COMMAND_RESPONSE__ACK )) //|| bluetooth_read_buffer[0] == SERIAL_DATA_FORMAT_EIGHT__STATUS_DETECTION_BIT))
            {
                Log.d(TAG, "SET_OPERATING_MODE : bluetooth_read_buffer data is NORMAL_MODE.........");
                // Check if the transition is for normal mode. Otherwise send the packet again
                if(operating_mode == NoninWristOxOperatingMode.ENTERING_NORMAL_MODE)
                {
                    Log.d(TAG, "SET_OPERATING_MODE : Setting NONIN operation mode to Normal mode");
                    // Nonin in Normal mode operation
                    if(timeout_set_required)
                    {
                        next_configuration_command = ConfigurationCommandStateMachine.SET_BLUETOOTH_TIMEOUT;
                    }
                    else
                    {
                        next_configuration_command = ConfigurationCommandStateMachine.CONFIGURATION_COMPLETE;
                    }

                    operating_mode = NoninWristOxOperatingMode.NORMAL_MODE;
                }
                else
                {
                    Log.e(TAG, "SET_OPERATING_MODE : Resetting the NONIN mode to SETUP_MODE because OperatingMode = ENTERING_SETUP_MODE");
                    // Resend the Normal Mode operation packet
                    next_configuration_command = ConfigurationCommandStateMachine.SET_OPERATING_MODE;
                    operating_mode = NoninWristOxOperatingMode.ENTERING_SETUP_MODE;
                }
            }
            else
            {
                Log.w(TAG, "SET_OPERATING_MODE : bluetooth_read_buffer data is different. Expected to be NORMAL_MODE.........");

                // Packet length is resembles the Normal operation mode of Nonin but data information isn't expected value
                // check the transition state and resend the command to Nonin
                if(operating_mode == NoninWristOxOperatingMode.ENTERING_NORMAL_MODE)
                {
                    Log.d(TAG, "SET_OPERATING_MODE : Send again the NORMAL_MODE command to NONIN.........");

                    // Send again Normal operation command to the Nonin
                    next_configuration_command = ConfigurationCommandStateMachine.SET_OPERATING_MODE;
                    operating_mode = NoninWristOxOperatingMode.ENTERING_NORMAL_MODE;
                }
                else
                {
                    Log.e(TAG, "SET_OPERATING_MODE : Changing the  operating_mode from NORMAL_MODE to SETUP_MODE.........");

                    // Send again Setup operation command to the Nonin
                    next_configuration_command = ConfigurationCommandStateMachine.SET_OPERATING_MODE;
                    operating_mode = NoninWristOxOperatingMode.ENTERING_SETUP_MODE;
                }
            }
        }
        // Received packet size is of setup mode
        else if((bluetooth_read_buffer[0] == SERIAL_DATA_FORMAT_EIGHT__BATTERY_CONDITION_BIT))
        {
            Log.d(TAG, "SET_OPERATING_MODE : bluetooth_read_buffer data is SETUP_MODE.........");

            // Check if the transition is for normal mode. Otherwise send the packet again
            if(operating_mode == NoninWristOxOperatingMode.ENTERING_SETUP_MODE)
            {
                Log.d(TAG, "SET_OPERATING_MODE : Setting NONIN operation mode to setup mode");

                // Nonin in Normal mode operation
                next_configuration_command = ConfigurationCommandStateMachine.CONFIGURATION_COMPLETE;
                operating_mode = NoninWristOxOperatingMode.SETUP_MODE_SYNC;
            }
            else
            {
                Log.e(TAG, "SET_OPERATING_MODE : Resetting the NONIN mode to NORMAL_MODE because OperatingMode = ENTERING_NORMAL_MODE");

                // Resend the Normal Mode operation packet
                next_configuration_command = ConfigurationCommandStateMachine.SETUP_MODE_SYNC;
                operating_mode = NoninWristOxOperatingMode.ENTERING_NORMAL_MODE;
            }
        }
        else
        {
            Log.w(TAG, "SET_OPERATING_MODE : bluetooth_read_buffer data is different. Expected to be SETUP_MODE.........");

            // Packet length is resembles the Setup operation mode of Nonin but data information isn't expected value
            // check the transition state and resend the command to Nonin
            if(operating_mode == NoninWristOxOperatingMode.ENTERING_SETUP_MODE)
            {
                Log.d(TAG, "SET_OPERATING_MODE : Send again the SETUP_MODE command to NONIN.........");

                // Send again Normal operation command to the Nonin
                next_configuration_command = ConfigurationCommandStateMachine.SET_OPERATING_MODE;
                operating_mode = NoninWristOxOperatingMode.ENTERING_SETUP_MODE;
            }
            else
            {
                Log.e(TAG, "SET_OPERATING_MODE : Changing the operating_mode from SETUP_MODE to NORMAL_MODE.........");

                // Send again Setup operation command to the Nonin
                next_configuration_command = ConfigurationCommandStateMachine.SET_OPERATING_MODE;
                operating_mode = NoninWristOxOperatingMode.ENTERING_NORMAL_MODE;
            }
        }

        executeConfigurationCommands();

        frame_start_timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();
    }


    private void setBluetoothTimeout(String log_line, byte[] bluetooth_read_buffer)
    {
        Log.i(TAG, "SET_BLUETOOTH_TIMEOUT length = " + bluetooth_read_buffer.length + " : buffer = " + log_line);

        try
        {
            // Test code
            //bluetooth_read_buffer = new byte[]{0x02, 0x18, 0x06, 0x0A, 0x01, 0x04, 0x04, 0x37, 0x01, 0x03, 0x02, (byte)0xF5, 0x04, 0x04, 0x00, 0x01, 0x05, 0x03};

            // Sometime Nonin Wrist-ox timeout response is 02 18 06 0A 01 04 04 37 01 03 02 F5 04 04 00 01 05 03
            // Need to filter out the correct response
            if(bluetooth_read_buffer.length == 8)
            {
                // Response length is eight "02 F5 04 04 00 01 05 03"
                if (Arrays.equals(bluetooth_read_buffer, set_bluetooth_timeout_successful_response))
                {
                    Log.i(TAG, "SET_BLUETOOTH_TIMEOUT ACK'ed");

                    next_configuration_command = ConfigurationCommandStateMachine.CONFIGURATION_COMPLETE;
                }
                else
                {
                    Log.e(TAG, "SET_BLUETOOTH_TIMEOUT failed. Nonin response = " + log_line);
                }
            }
            else
            {
                int array_length = bluetooth_read_buffer.length;

                //for-each array check is not efficient in this case
                for(int i=0; i<array_length; i++)
                {
                    // Check if Nonin timeout response has "0x02" byte
                    if(bluetooth_read_buffer[i] == 0x02)
                    {
                        // check if there are eight more bytes are after "0x02"
                        if((i+NONIN_BLUETOOTH_TIMEOUT_RESPONSE_PACKET_LENGTH) <= array_length)
                        {
                            byte[] trimmed_response_array = Arrays.copyOfRange(bluetooth_read_buffer, i,i + NONIN_BLUETOOTH_TIMEOUT_RESPONSE_PACKET_LENGTH);

                            Log.d(TAG, "SET_BLUETOOTH_TIMEOUT : Trimmed Nonin timeout response = " + Utils.byteArrayToHexString(trimmed_response_array));

                            if (Arrays.equals(trimmed_response_array, set_bluetooth_timeout_successful_response))
                            {
                                Log.i(TAG, "SET_BLUETOOTH_TIMEOUT : trimmed_response_array  ACK'ed");

                                next_configuration_command = ConfigurationCommandStateMachine.CONFIGURATION_COMPLETE;

                                break;
                            }
                            else
                            {
                                Log.e(TAG, "SET_BLUETOOTH_TIMEOUT failed. Trimmed Nonin response = " + Utils.byteArrayToHexString(trimmed_response_array));
                            }
                        }
                        else
                        {
                            Log.d(TAG,"SET_BLUETOOTH_TIMEOUT : Trimmed Nonin Timeout response is different after 0x02 byte");
                        }
                    }
                    else
                    {
                        // Log.d(TAG, "SET_BLUETOOTH_TIMEOUT : checked byte = " + bluetooth_read_buffer[i]);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "SET_BLUETOOTH_TIMEOUT : exception e = " + e.toString());
        }

        Log.d(TAG, "SET_BLUETOOTH_TIMEOUT : changing the operation after setting Nonin Bluetooth_timeout");

        executeConfigurationCommands();
    }


    public void handleIncomingFromPatientGateway(Context context, Intent intent)
    {
        String action = intent.getAction();

        switch (action)
        {
            case NONIN_WRIST_OX__ENABLE_SETUP_MODE:
            {
                boolean setup_mode_enabled = intent.getBooleanExtra(NoninWristOx.SETUP_MODE_ENABLED, false);

                Log.i(TAG, "setup_mode_enabled = " + setup_mode_enabled);

                if (setup_mode_enabled)
                {
                    enterSetupMode();
                }
                else
                {
                    enterNormalMode();
                }
            }
            break;

            case SET_ADDED_TO_SESSION:
            {
                setDeviceAddedToSession();
            }
            break;

            case SET_REMOVED_FROM_SESSION:
            {
                setDeviceRemovedFromSession();
            }
            break;
        }
    }


    public IntentFilter makeIntentFilter_IncomingFromPatientGateway()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NoninWristOx.NONIN_WRIST_OX__ENABLE_SETUP_MODE);
        intentFilter.addAction(NoninWristOx.SET_ADDED_TO_SESSION);
        intentFilter.addAction(NoninWristOx.SET_REMOVED_FROM_SESSION);
        return intentFilter;
    }


    private void reportConnected()
    {
        Log.d(TAG, "reportConnected");

        Intent intent = new Intent(NONIN_WRIST_OX__CONNECTED);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        // If no configuration command is received within 5 seconds after a Bluetooth connection is established, the 3150 will operate and send data based on the
        // last saved configuration settings
        executeConfigurationCommands();
    }


    private void reportTemporaryDisconnection()
    {
        Log.d(TAG, "reportTemporaryDisconnection");

        // Reset the configuration FSM back to defaults
        resetConfigurationCommandStateMachine();

        Intent intent = new Intent(NONIN_WRIST_OX__TEMPORARY_DISCONNECTION);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }


    public void reportDisconnected()
    {
        Intent intent = new Intent(NONIN_WRIST_OX__DISCONNECTED);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        // Reset Nonin configuration to Normal operation mode
        resetConfigurationCommandStateMachine();
    }
    
    /**
     * Report Gateway about CheckSum Error in pulse-ox packet. This will set graphview to Normal mode. 
     */
    private void reportNoninCheckSumError()
    {
        Intent intent = new Intent(NONIN_WRIST_OX__CHECKSUM_ERROR);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }

    
    private void setNoninWristOxConfig_DataFormat8()
    {
        // http://www.nonin.com/documents/IFUManuals/3150TechSpec_ENG.pdf
        // Page 11 and 12   
        // Set the device to protocol type D8 (4 bytes once a second)
        switch (nonin_turn_on_mode)
        {
            case INSERT_SENSOR:
            {
                //                                            STX    OP Code  Size   DataType  DataFormat  CDF Byte   Checksum               ETX
                byte[] nonin_config_data_data_format_eight = {0x02,  0x70,    0x04,  0x02,     0x08,       0x21,      (byte) (0x9F & 0xFF),  0x03};

                write(nonin_config_data_data_format_eight);
            }
            break;

            case INSERT_FINGER:
            {
                //                                            STX    OP Code  Size   DataType  DataFormat  CDF Byte   Checksum               ETX
                byte[] nonin_config_data_data_format_eight = {0x02,  0x70,    0x04,  0x02,     0x08,       0x61,      (byte) (0xDF & 0xFF),  0x03};

                write(nonin_config_data_data_format_eight);
            }
            break;
        }
    }
    

    private void setNoninWristOxConfig_DataFormat2()
    {
        // http://www.nonin.com/documents/IFUManuals/3150TechSpec_ENG.pdf
        // Page 11 and 12

        Log.e(TAG, "setNoninWristOxConfig_DataFormat2");

        // reset the setup mode packet counter. This counter is used to send the SpO2 level to Gateway.
        count_setupMode_packet = 0;

        // http://www.nonin.com/documents/IFUManuals/3150TechSpec_ENG.pdf
        // Page 11 and 12

        // Set the device to protocol type D2 (5 bytes, 75 per/sec, 8-bit pleth)
        switch (nonin_turn_on_mode)
        {
            case INSERT_SENSOR:
            {
                //                                          STX    OP Code   Size   DataType  DataFormat   CDF Byte  Checksum                ETX
                byte[] nonin_config_data_data_format_two = {0x02,  0x70,     0x04,  0x02,     0x02,        0x21,     (byte) (0x99 & 0xFF),   0x03};

                write(nonin_config_data_data_format_two);
            }
            break;

            case INSERT_FINGER:
            {
                //                                            STX    OP Code  Size   DataType  DataFormat  CDF Byte   Checksum               ETX
                byte[] nonin_config_data_data_format_eight = {0x02,  0x70,    0x04,  0x02,     0x02,       0x61,      (byte) (0xD9 & 0xFF),  0x03};

                write(nonin_config_data_data_format_eight);
            }
            break;
        }
    }


    // timeout_in_minutes can be 00 (disabled), and 02 to FF (2 - 255 minutes)
    private void setNoninBluetoothTimeout(byte timeout_in_minutes)
    {
        // http://www.nonin.com/documents/IFUManuals/3150TechSpec_ENG.pdf
        // Page 14
        /*
         * As a power saving feature the 3150 can be configured to turn off the Bluetooth after a 2 to 255 minutes
        from power on. The Bluetooth power saving feature can be enabled by setting the following command:
        SBT=XXX<CR><LF> where XXX is a three character ASCII number in the range 0, 2 to 255 minutes. A
        zero value = no Bluetooth time-out period. Because the Bluetooth pairing process can take more than
        one minute, a 1 minute Bluetooth time-out period is not an option.        
         */
        
        
        //                                            STX    OP Code  Size   ID Data Code  Module  TimeCDF Byte         Checksum                                  ETX
        byte[] nonin_config_data_bluetooth_timeout = {0x02,  0x75,    0x04,  0x04,         0x00,   timeout_in_minutes,  (byte)((0x04 + timeout_in_minutes)&0xFF), 0x03};
        
        write(nonin_config_data_bluetooth_timeout);
    }


    private void enterSetupMode()
    {
        Log.d(TAG, "enterSetupMode");

        desired_operating_mode = NoninWristOxOperatingMode.SETUP_MODE_RECEIVING_DATA;

        setOperationMode(NoninWristOxOperatingMode.ENTERING_SETUP_MODE);
    }


    private void enterNormalMode()
    {
        Log.d(TAG, "enterNormalMode");

        desired_operating_mode = NoninWristOxOperatingMode.NORMAL_MODE;

        setOperationMode(NoninWristOxOperatingMode.ENTERING_NORMAL_MODE);
    }


    private void setOperationMode(NoninWristOxOperatingMode desired_operating_mode)
    {
        operating_mode = desired_operating_mode;

        next_configuration_command = ConfigurationCommandStateMachine.SET_OPERATING_MODE;
        executeConfigurationCommands();
    }


    private static final int NUMBER_OF_SETUP_MODE_PACKET_IN_ONE_SEC = 3;
    private static int count_setupMode_packet = 0;

    /**
     * Nonin Wrist Ox sends three setup mode packets every second. So after the third packet NONIN_WRIST_OX__NEW_MEASUREMENT_DATA
     * is send to the Gateway.
     */
    private void sendNoninMeasurementsFromSetupMode()
    {
        // Check if three setup mode packets are received
        if(count_setupMode_packet >= NUMBER_OF_SETUP_MODE_PACKET_IN_ONE_SEC)
        {
            // reset the counter and send intermediate SpO2 value to PatientGatewayService
            count_setupMode_packet = 0;

            Intent intent = new Intent();
            intent.setAction(NONIN_WRIST_OX__NEW_MEASUREMENT_DATA);
            intent.putExtra(NONIN_WRIST_OX__VALID_READING, valid_reading);
            intent.putExtra(NONIN_WRIST_OX__HEART_RATE, extended_average_heart_rate);
            intent.putExtra(NONIN_WRIST_OX__SP_O2, extended_sp_o2);
            intent.putExtra(NONIN_WRIST_OX__TIMESTAMP, PatientGatewayService.getNtpTimeNowInMillisecondsStatic());
            intent.putExtra(NONIN_WRIST_OX__LOW_BATTERY, low_battery_condition);
            intent.putExtra(NONIN_WRIST_OX__DETACHED_FROM_PATIENT, device_detached_from_patient);
            intent.putExtra(NONIN_WRIST_OX__ARTIFACT, artifact);
            intent.putExtra(NONIN_WRIST_OX__OUT_OF_TRACK, out_of_track);
            intent.putExtra(NONIN_WRIST_OX__LOW_PERFUSION, low_perfusion);
            intent.putExtra(NONIN_WRIST_OX__MARGINAL_PERFUSION, marginal_perfusion);
            intent.putExtra(NONIN_WRIST_OX__SMART_POINT_ALGORITHM, smartpoint_algorithm);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
        }
        else
        {
            // Increment the counter
            count_setupMode_packet++;
        }
    }
}
