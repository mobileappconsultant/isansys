package com.isansys.patientgateway.bluetooth.bloodPressure;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetooth.BluetoothService;
import com.isansys.common.enums.DeviceType;

public class AnD_UA767 extends BluetoothService
{
    private final String TAG = this.getClass().getSimpleName();

    // Outgoing Intent names
    public final static String CONNECTED = "com.isansys.patientgateway.AND_UA767__CONNECTED";
    public final static String NEW_DATA = "com.isansys.patientgateway.AND_UA767__NEW_DATA";
    public final static String DISCONNECTED = "com.isansys.patientgateway.AND_UA767__DISCONNECTED";

    public final static String DEVICE_NAME = "com.isansys.patientgateway.AnD_UA767.DEVICE_NAME";
    public final static String DEVICE_TYPE = "com.isansys.patientgateway.AnD_UA767.DEVICE_TYPE";
    public final static String SERIAL_NUMBER_STRING = "com.isansys.patientgateway.AnD_UA767.SERIAL_NUMBER_STRING";
    public final static String MEASUREMENT_TIME = "com.isansys.patientgateway.AnD_UA767.MEASUREMENT_TIME";
    public final static String TRANSMISSION_TIME = "com.isansys.patientgateway.AnD_UA767.TRANSMISSION_TIME";
    public final static String DEVICE_FIRMWARE_REVISION = "com.isansys.patientgateway.AnD_UA767.DEVICE_FIRMWARE_REVISION";
    public final static String DEVICE_HARDWARE_REVISION = "com.isansys.patientgateway.AnD_UA767.DEVICE_HARDWARE_REVISION";
    public final static String DEVICE_BATTERY_STATUS = "com.isansys.patientgateway.AnD_UA767.DEVICE_BATTERY_STATUS";
    public final static String MEASUREMENT_STATUS = "com.isansys.patientgateway.AnD_UA767.MEASUREMENT_STATUS";

    public final static String DIASTOLIC = "com.isansys.patientgateway.AnD_UA767.DIASTOLIC";
    public final static String SYSTOLIC = "com.isansys.patientgateway.AnD_UA767.SYSTOLIC";
    public final static String PULSE_RATE = "com.isansys.patientgateway.AnD_UA767.PULSE_RATE";

    public final static String MEAN_ARTERIAL_PRESSURE = "com.isansys.patientgateway.AnD_UA767.MEAN_ARTERIAL_PRESSURE";

    public final static String BATTERY_GOOD = "com.isansys.patientgateway.AnD_UA767.battery.good";
    public final static String BATTERY_BAD = "com.isansys.patientgateway.AnD_UA767.battery.bad";
    private final static String BATTERY_UNKNOWN = "com.isansys.patientgateway.AnD_UA767.battery.unknown";

    // Incoming intent names
    public final static String SET_ADDED_TO_SESSION = "com.isansys.patientgateway.AnD_UA767_SET_ADDED_TO_SESSION";
    public final static String SET_REMOVED_FROM_SESSION = "com.isansys.patientgateway.AnD_UA767_SET_REMOVED_FROM_SESSION";

    private final Handler send_response_handler = new Handler();


    public AnD_UA767()
    {
        super("AnD_UA767", DeviceConnectingMode.LISTEN_FOR_DEVICE, true, true);
    }


    public String getBluetoothDeviceName()
    {
        return "";
    }


    public String getDesiredDeviceBluetoothAddress()
    {
        return PatientGatewayService.device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_UA767).bluetooth_address;
    }


    public void handleIncomingFromPatientGateway(Context context, Intent intent)
    {
        String action = intent.getAction();

        switch (action)
        {
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
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(AnD_UA767.SET_ADDED_TO_SESSION);
        intentFilter.addAction(AnD_UA767.SET_REMOVED_FROM_SESSION);
        return intentFilter;
    }


    public void handleMessageRead(byte[] bluetooth_read_buffer)
    {
        String log_line = "BluetoothService.MESSAGE_READ : ";

        try
        {
            Log.d(TAG, log_line + "bluetooth_read_buffer as hex string = " + Utils.byteArrayToHexString(bluetooth_read_buffer));

            String bluetooth_read_buffer_as_string = new String(bluetooth_read_buffer);

            switch (bluetooth_read_buffer_as_string)
            {
                case "PWRQCF":
                {
                    Log.d(TAG, "Config request message received");
                    sendConfig();
                }
                break;

                case "PWC1":
                {
                    Log.e(TAG, "Config change successful");
                }
                break;

                case "PWC0":
                {
                    Log.e(TAG, "Config change failed");
                }
                break;

                default:
                {
                    Log.w(TAG, "Non config data : treating as measurement");

                    processReceivedData(bluetooth_read_buffer);
                }
                break;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, log_line + "Exception : " + e.toString());
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
                    // The AnD UA-767 attempts to connect to us - so we have to be a Server
                    startListening();
                }
            }
            break;

            case STATE_CONNECTED:
            {
                reportConnected();
            }
            break;

            case STATE_CONNECTING:
            case STATE_LISTEN:
            case STATE_CONNECTION_TIMEOUT:
            default:
            {
            }
            break;
        }
    }

    
    private void sendStringResponseAfter250mS(final String response_string)
    {
        send_response_handler.postDelayed(() -> {
            Log.d(TAG, "sendStringResponseAfter250mS : Writing now : " + response_string);

            write(response_string.getBytes());
        }, 250);
    }
    
    
    private void sendByteArrayResponseAfter250mS(final byte[] response_bytes)
    {
        send_response_handler.postDelayed(() -> {
            Log.d(TAG, "sendByteArrayResponseAfter250mS : Writing now : " + Utils.byteArrayToHexString(response_bytes));

            write(response_bytes);
        }, 250);
    }
    
    
//    private void sendDataAcceptedResponse()
//    {
//        sendStringResponseAfter250mS("PWA1");
//    }
    

    private void sendDataAcceptedAndEnterConfigurationMode()
    {
        sendStringResponseAfter250mS("PWA2");
    }
    

    private void sendDataAcceptedWithoutDisconnectResponse()
    {
        sendStringResponseAfter250mS("PWA4");
    }


    private void sendConfig()
    {
        byte [] config_data = new byte[90];

        // Bytes 0 to 19 are reserved according to datasheet
        config_data[19] = 1;    // Change memory size == true
        config_data[20] = 1;    // New memory size == 1
        config_data[21] = 0;    // Change PIN == false
        // 22 to 31 are pin - which is not being changed
        config_data[32] = 0;    // Change App Bundle identifier == false
        // 33 to 58 are App Bundle Identified - which is not being changed
        config_data[59] = 1;    // Change SMM/DMM == true
        config_data[60] = 1;    // Change SMM/DMM for all users
        config_data[61] = 1;    // Change to SMM (Single Manager Mode)
        
        Date now = new Date(PatientGatewayService.getNtpTimeNowInMillisecondsStatic());
        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTime(now);
        int year = c.get(Calendar.YEAR);
        config_data[62] = 1;    // Set clock == true
        config_data[63] = (byte) (year);
        config_data[64] = (byte) (year >> 8);
        config_data[65] = (byte) (c.get(Calendar.MONTH) + 1);
        config_data[66] = (byte) c.get(Calendar.DAY_OF_MONTH);
        config_data[67] = (byte) c.get(Calendar.HOUR_OF_DAY);
        config_data[68] = (byte) c.get(Calendar.MINUTE);
        config_data[69] = (byte) c.get(Calendar.SECOND);

        sendByteArrayResponseAfter250mS(config_data);
    }


    private void processReceivedData(byte[] data)
    {
        // Decode the Data Header Section.
        //int packet_type = (Utils.convertByteToInt(bluetooth_read_buffer[1]) << 8) + Utils.convertByteToInt(bluetooth_read_buffer[0]);
//Log.d(TAG, "packet_type = " + String.valueOf(packet_type));
        //int packet_length = (Utils.convertByteToInt(bluetooth_read_buffer[5]) << 24) | (Utils.convertByteToInt(bluetooth_read_buffer[4]) << 16) | (Utils.convertByteToInt(bluetooth_read_buffer[3]) << 8) | (Utils.convertByteToInt(bluetooth_read_buffer[2]));
//Log.d(TAG, "packet_length = " + String.valueOf(packet_length));
        int device_type = (Utils.convertByteToInt(data[7]) << 8) + Utils.convertByteToInt(data[6]);
//Log.d(TAG, "device_type = " + String.valueOf(device_type));
        //int flag = bluetooth_read_buffer[8];
//Log.d(TAG, "flag = " + String.valueOf(flag));

        int year_of_measurement = (Utils.convertByteToInt(data[10]) << 8) + Utils.convertByteToInt(data[9]);

        int month_of_measurement = Utils.convertByteToInt(data[11]);

        int day_of_measurement = Utils.convertByteToInt(data[12]);

        int hour_of_measurement = Utils.convertByteToInt(data[13]);

        int minute_of_measurement = Utils.convertByteToInt(data[14]);

        int second_of_measurement = Utils.convertByteToInt(data[15]);

        int year_of_transmission = (Utils.convertByteToInt(data[17]) << 8) + Utils.convertByteToInt(data[16]);

        int month_of_transmission = Utils.convertByteToInt(data[18]);

        int day_of_transmission = Utils.convertByteToInt(data[19]);

        int hour_of_transmission = Utils.convertByteToInt(data[20]);

        int minute_of_transmission = Utils.convertByteToInt(data[21]);

        int second_of_transmission = Utils.convertByteToInt(data[22]);

        byte[] bluetooth_id = new byte[6];
        for (int i=0; i<6; i++)
        {
            bluetooth_id[i] = (byte)Utils.convertByteToInt(data[23 + i]);
        }

        char[] device_name_upper_chars = new char[6];
        for (int i=0; i<6; i++)
        {
            device_name_upper_chars[i] = (char)data[29 + i];
        }
        
        char[] serial_number_chars = new char[12];
        for (int i=0; i<12; i++)
        {
            serial_number_chars[i] = (char)data[35 + i];
        }
        
        char[] device_name_lower_chars = new char[10];
        for (int i=0; i<10; i++)
        {
            device_name_lower_chars[i] = (char)data[47 + i];
        }
        
        int device_battery_status = Utils.convertByteToInt(data[57]);
        // Byte 58 reserved
        int device_firmware_revision_and_hardware_revision = Utils.convertByteToInt(data[59]);

        
        
        // Process the Data Header Section into meaningful values that can be sent out on the intent
        String device_name_upper = new String(device_name_upper_chars);
        String device_name_lower = new String(device_name_lower_chars);
        String device_name = device_name_upper + device_name_lower;    
        device_name = device_name.trim();
        
        String serial_number = new String(serial_number_chars);
        serial_number = serial_number.trim();
        
        Calendar measurement_time = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        measurement_time.set(Calendar.YEAR, year_of_measurement);
        measurement_time.set(Calendar.MONTH, month_of_measurement - 1);
        measurement_time.set(Calendar.DAY_OF_MONTH, day_of_measurement);
        measurement_time.set(Calendar.HOUR_OF_DAY, hour_of_measurement);
        measurement_time.set(Calendar.MINUTE, minute_of_measurement);
        measurement_time.set(Calendar.SECOND, second_of_measurement);
        
        Calendar transmission_time = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        transmission_time.set(Calendar.YEAR, year_of_transmission);
        transmission_time.set(Calendar.MONTH, month_of_transmission - 1);
        transmission_time.set(Calendar.DAY_OF_MONTH, day_of_transmission);
        transmission_time.set(Calendar.HOUR_OF_DAY, hour_of_transmission);
        transmission_time.set(Calendar.MINUTE, minute_of_transmission);
        transmission_time.set(Calendar.SECOND, second_of_transmission);
        
        long delta = PatientGatewayService.getNtpTimeNowInMillisecondsStatic() - transmission_time.getTimeInMillis();
        boolean update_device_settings = false;

        if (!PatientGatewayService.disableCommentsForSpeed())
        {
            Log.d(TAG, "year_of_measurement = " + year_of_measurement);
            Log.d(TAG, "month_of_measurement = " + month_of_measurement);
            Log.d(TAG, "day_of_measurement = " + day_of_measurement);
            Log.d(TAG, "hour_of_measurement = " + hour_of_measurement);
            Log.d(TAG, "minute_of_measurement = " + minute_of_measurement);
            Log.d(TAG, "second_of_measurement = " + second_of_measurement);
            Log.d(TAG, "year_of_transmission = " + year_of_transmission);
            Log.d(TAG, "month_of_transmission = " + month_of_transmission);
            Log.d(TAG, "day_of_transmission = " + day_of_transmission);
            Log.d(TAG, "hour_of_transmission = " + hour_of_transmission);
            Log.d(TAG, "minute_of_transmission = " + minute_of_transmission);
            Log.d(TAG, "second_of_transmission = " + second_of_transmission);
            Log.d(TAG, "bluetooth_id = " + Utils.byteArrayToHexString(bluetooth_id));
            Log.i(TAG, "measurement_time = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time.getTimeInMillis()));
            Log.i(TAG, "transmission_time = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(transmission_time.getTimeInMillis()));
        }

        if(delta > 60000)
        {
            if (!PatientGatewayService.disableCommentsForSpeed())
            {
                Log.e(TAG, "Data sent from past");
            }

            update_device_settings = true;
        }
        else if (delta < -60000)
        {
            if (!PatientGatewayService.disableCommentsForSpeed())
            {
                Log.e(TAG, "Data sent from future");
            }

            update_device_settings = true;
        }
        else
        {
            if (!PatientGatewayService.disableCommentsForSpeed())
            {
                Log.i(TAG, "Data sent from +- now");
            }
        }
        
        if(update_device_settings)
        {
            sendDataAcceptedAndEnterConfigurationMode();
        }
        else
        {
            sendDataAcceptedWithoutDisconnectResponse();
        }
        
        int device_firmware_revision = (device_firmware_revision_and_hardware_revision & 0xF8) >> 5;
        
        int device_hardware_revision = device_firmware_revision_and_hardware_revision & 0x07;

        String battery_status_string;
        switch (device_battery_status)
        {
            case 0xF0:
                battery_status_string = BATTERY_GOOD;
                break;

            case 0x39:
                battery_status_string = BATTERY_BAD;
                break;

            default:
                battery_status_string = BATTERY_UNKNOWN;
                break;
        }


        // Start of Blood Pressure Section
        char[] measurement_status_chars = new char[2];
        for (int i=0; i<2; i++)
        {
            measurement_status_chars[i] = (char)data[60 + i];
        }
        String measurement_status_string = new String(measurement_status_chars);
        
        int measurement_status_int = Integer.parseInt(measurement_status_string, 16);
        
        And_UA767_MeasurementStatus.Status measurement_status = And_UA767_MeasurementStatus.Status.NON_INITED_ENUM_VALUE;
        
        switch(measurement_status_int)
        {
            case 0x80:
            {
                measurement_status = And_UA767_MeasurementStatus.Status.CORRECT_MEASUREMENT;
            }
            break;

            case 0x81:
            case 0x82:
            {
                measurement_status = And_UA767_MeasurementStatus.Status.MEASUREMENT_ERROR;
            }
            break;

            case 0x83:
            {
                measurement_status = And_UA767_MeasurementStatus.Status.CUFF_ERROR;
            }
            break;

            case 0x8A:
            {
                measurement_status = And_UA767_MeasurementStatus.Status.LOW_BATTERY_STATUS;
            }
            break;

            case 0x8B:
            {
                measurement_status = And_UA767_MeasurementStatus.Status.LOW_BATTERY_STATUS_AND_IRREGULAR_HEART_BEAT;
            }
            break;

            case 0x8F:
            {
                measurement_status = And_UA767_MeasurementStatus.Status.IRREGULAR_HEART_BEAT;
            }
            break;
        }

        // Need to get Diastolic first in order to get Systolic
        char[] diastolic_chars = new char[2];
        for (int i=0; i<2; i++)
        {
            diastolic_chars[i] = (char)data[64 + i];
        }
        String diastolic_string = new String(diastolic_chars);

        int diastolic = Integer.parseInt(diastolic_string, 16);
        
        char[] systolic_chars = new char[2];
        for (int i=0; i<2; i++)
        {
            systolic_chars[i] = (char)data[62 + i];
        }
        String systolic_string = new String(systolic_chars);

        int systolic = Integer.parseInt(systolic_string, 16);
        systolic = systolic + diastolic;
        
        char[] pulse_rate_chars = new char[2];
        for (int i=0; i<2; i++)
        {
            pulse_rate_chars[i] = (char)data[66 + i];
        }
        String pulse_rate_string = new String(pulse_rate_chars);

        int pulse_rate = Integer.parseInt(pulse_rate_string, 16);
        
        char[] mean_arterial_pressure_chars = new char[2];
        for (int i=0; i<2; i++)
        {
            mean_arterial_pressure_chars[i] = (char)data[68 + i];
        }
        String mean_arterial_pressure_string = new String(mean_arterial_pressure_chars);

        int mean_arterial_pressure = Integer.parseInt(mean_arterial_pressure_string, 16);

        if (!PatientGatewayService.disableCommentsForSpeed())
        {
            Log.d(TAG, "measurement_status_string = " + measurement_status_string);
            Log.d(TAG, "measurement_status = " + measurement_status.toString());
            Log.d(TAG, "Diastolic = " + diastolic);
            Log.d(TAG, "Systolic = " + systolic);
            Log.d(TAG, "Pulse Rate = " + pulse_rate);
            Log.d(TAG, "Mean Arterial Pressure = " + mean_arterial_pressure);
        }
        // End of Blood Pressure Section

        // Broadcast the new Blood Pressure reading to listening Android apps.
        Intent data_intent = new Intent();
        data_intent.setAction(NEW_DATA);

        data_intent.putExtra(DEVICE_NAME, device_name);
        data_intent.putExtra(DEVICE_TYPE, device_type);
        data_intent.putExtra(SERIAL_NUMBER_STRING, serial_number);
        data_intent.putExtra(MEASUREMENT_TIME, measurement_time.getTimeInMillis());
        data_intent.putExtra(TRANSMISSION_TIME, transmission_time.getTimeInMillis());
        data_intent.putExtra(DEVICE_FIRMWARE_REVISION, device_firmware_revision);
        data_intent.putExtra(DEVICE_HARDWARE_REVISION, device_hardware_revision);
        data_intent.putExtra(DEVICE_BATTERY_STATUS, battery_status_string);
        data_intent.putExtra(MEASUREMENT_STATUS, measurement_status.ordinal());
        data_intent.putExtra(DIASTOLIC, diastolic);
        data_intent.putExtra(SYSTOLIC, systolic);
        data_intent.putExtra(PULSE_RATE, pulse_rate );
        data_intent.putExtra(MEAN_ARTERIAL_PRESSURE, mean_arterial_pressure);
        
        sendBroadcast(data_intent);
    }


    private void reportConnected()
    {
        sendBroadcast(new Intent(CONNECTED));
    }
    
    
    public void reportDisconnected()
    {
        sendBroadcast(new Intent(DISCONNECTED));

        stopThreadsAndSetStateToNone();
    }
}
