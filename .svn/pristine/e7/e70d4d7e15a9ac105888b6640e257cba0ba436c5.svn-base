package com.isansys.patientgateway.bluetoothLowEnergyDevices.Nonin3230;

import static com.idevicesinc.sweetblue.BleDeviceState.BONDED;
import static com.idevicesinc.sweetblue.BleDeviceState.BONDING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTED;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING;
import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTING_OVERALL;
import static com.idevicesinc.sweetblue.utils.Uuids.CURRENT_TIME;
import static com.idevicesinc.sweetblue.utils.Uuids.CURRENT_TIME_SERVICE_UUID;
import static com.idevicesinc.sweetblue.utils.Uuids.PLX_SPOT_CHECK_MEASUREMENT;
import static com.idevicesinc.sweetblue.utils.Uuids.PULSE_OXIMETER_SERVICE_UUID;
import static com.idevicesinc.sweetblue.utils.Uuids.RECORD_ACCESS_CONTROL_POINT;
import static com.isansys.patientgateway.Utils.readSFloat16;

import android.content.Intent;
import android.text.format.DateUtils;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceConfig;
import com.idevicesinc.sweetblue.BleManager;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;


// TODO
//      clear stored records on initial connection
//      need decision on Nonin Spot measurements / Isansys spot measurements conflicts
//          could use Nonin spots ** ONLY ** to fill in blanks when out of range for > 1 min. detected
//              keep all logic in this class to keep it simple
//      firmware revision shown as "2.1" on Change session settings, looks different to the reported number at start up

//
//  Nonin 3150 Nonin Oximetry services offers 6 characteristics:
//     Oximetry Measurement, Pulse Interval Timing (PIT), Control Point, PPG characteristic, Memory Playback, Device Status
//
// Nonin 3230 Nonin Oximetry services offer only 3:
//     Oximetry Measurement,  Pulse Interval Time,  Control Point


public class BluetoothLeNonin3230 extends BluetoothLeDevice
{
    private final static String TAG = BluetoothLeNonin3230.class.getSimpleName();

    private final UUID UUID_SERVICE_NONIN_OXIMETRY_SERVICE = UUID.fromString("46A970E0-0D5F-11E2-8B5E-0002A5D5C51B");
        private final UUID UUID_CHARACTERISTIC_NONIN_OXIMETRY_MEASUREMENT = UUID.fromString("0AAD7EA0-0D60-11E2-8E3C-0002A5D5C51B");
        private final UUID UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT = UUID.fromString("1447AF80-0D60-11E2-88B6-0002A5D5C51B");

    public final static String ACTION_PAIRING = "BluetoothLeNonin3230.ACTION_PAIRING";
    public final static String ACTION_PAIRING_SUCCESS = "BluetoothLeNonin3230.ACTION_PAIRING_SUCCESS";
    public final static String ACTION_PAIRING_FAILURE = "BluetoothLeNonin3230.ACTION_PAIRING_FAILURE";
    public final static String ACTION_CONNECTED = "BluetoothLeNonin3230.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED = "BluetoothLeNonin3230.ACTION_DISCONNECTED";
    public final static String ACTION_DATA_AVAILABLE = "BluetoothLeNonin3230.ACTION_DATA_AVAILABLE";

    public final static String ACTION_NONIN_3230_SPOT_MEASUREMENT_AVAILABLE = "BluetoothLeNonin3230.ACTION_NONIN_3230_SPOT_MEASUREMENT_AVAILABLE";

    public final static String DATATYPE_SPO2_MEASUREMENT = "BluetoothLeNonin3230.MEASUREMENT";
    public final static String NONIN_CONTINUOUS_OXIMETRY__VALID_READING = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__VALID_READING";
    public final static String NONIN_CONTINUOUS_OXIMETRY__PULSE_AMPLITUDE_INDEX = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__PULSE_AMPLITUDE_INDEX";
    public final static String NONIN_CONTINUOUS_OXIMETRY__COUNTER = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__COUNTER";
    public final static String NONIN_CONTINUOUS_OXIMETRY__SPO2 = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__SPO2";
    public final static String NONIN_CONTINUOUS_OXIMETRY__PULSE_RATE = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__PULSE_RATE";
    public final static String NONIN_CONTINUOUS_OXIMETRY__TIMESTAMP = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__TIMESTAMP";
    public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__ENCRYPTION = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__ENCRYPTION";
    public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT";
    public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__SEARCHING = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__SEARCHING";
    public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__SMART_POINT_ALGORITHM = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__SMART_POINT_ALGORITHM";
    public final static String NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_WEAK_SIGNAL = "BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_WEAK_SIGNAL";

    public final static String DATATYPE_DEVICE_STATUS = "BluetoothLeNonin3230.DATATYPE_DEVICE_STATUS";
    public final static String NONIN_DEVICE_STATUS__SENSOR_TYPE = "BluetoothLeNonin3230.NONIN_DEVICE_STATUS__SENSOR_TYPE";
    public final static String NONIN_DEVICE_STATUS__ERROR = "BluetoothLeNonin3230.NONIN_DEVICE_STATUS__ERROR";
    public final static String NONIN_DEVICE_STATUS__BATTERY_PERCENTAGE = "BluetoothLeNonin3230.NONIN_DEVICE_STATUS__BATTERY_PERCENTAGE";
    public final static String NONIN_DEVICE_STATUS__TX_INDEX = "BluetoothLeNonin3230.NONIN_DEVICE_STATUS__TX_INDEX";
    public final static String NONIN_DEVICE_STATUS__TIMESTAMP = "BluetoothLeNonin3230.NONIN_DEVICE_STATUS__TIMESTAMP";

    public final static String DATATYPE_NONIN_3230_SPOT_MEASUREMENT = "DATATYPE_NONIN_3230_SPOT_MEASUREMENT";
    public final static String NONIN_3230_SPOT_DATA_AS_STRING = "NONIN_3230_SPOT_DATA_AS_STRING";

    public final static String RECEIVED_TIMESTAMP_AS_LONG = "RECEIVED_TIMESTAMP_AS_LONG";


    private boolean device_detached_from_patient;
    private boolean smartpoint_algorithm;                                                           // Indicates that the data successfully passed the SmartPoint Algorithm

    private final byte SECURITY_CHAR_0 = 'N';
    private final byte SECURITY_CHAR_1 = 'M';
    private final byte SECURITY_CHAR_2 = 'I';


    /**
     * Commands and responses for the nonin.
     *
     * Note the responses are ints, because Java bytes are signed, so give the wrong value
     */
    private final byte OPCODE_DELETE_BOND = 0x63;

    private final byte OPCODE_SET_NONIN_SECURITY_MODE = 0x64;
    private final int OPCODE_SET_NONIN_SECURITY_MODE_RESPONSE = OPCODE_SET_NONIN_SECURITY_MODE + 0x80;

    private long time_of_last_battery_reading_sent = Long.MAX_VALUE;

    private final NoninSpotRecordsAction nonin_spot_records_action = NoninSpotRecordsAction.DELETE_ALL_NONIN_SPOT_RECORDS;

    private enum NoninSpotRecordsAction
    {
        DELETE_ALL_NONIN_SPOT_RECORDS,
        REPORT_ALL_NONIN_SPOT_RECORDS
    }

    public BluetoothLeNonin3230(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time)
    {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__NONIN_3230);
    }


    @Override
    public void resetStateVariables()
    {
        Log.d(TAG, "resetStateVariables()");
        super.resetStateVariables();

        time_of_last_battery_reading_sent = Long.MAX_VALUE;
    }


    public String getChildTag()
    {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
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

                            Log.d(TAG, "last_data_received_ntp_time " + last_data_received_ntp_time + " " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(last_data_received_ntp_time));

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
                            final byte STATUS__CORRECT_CHECK = (byte) ((1 << 4));
                            final byte STATUS__SEARCHING = (byte) ((1 << 3));
                            final byte STATUS__SMARTPOINT = (byte) ((1 << 2));
                            final byte STATUS__LOW_OR_WEAK_SIGNAL = (byte) ((1 << 1));
                            // Bit 0 RFU

                            byte status = decodedData[1];

                            boolean encryption;
                            encryption = (status & STATUS__ENCRYPTION) > 0;
                            //Log.d(TAG, "Connection encrypted = " + encryption);

                            boolean low_battery = (status & STATUS__LOW_BATTERY) > 0;
                            Log.d(TAG, "Low battery = " + low_battery);
                            
                            if (low_battery)
                            {
                                reportBatteryLevel(true, event);
                            }
                            else
                            {
                                reportBatteryLevel(false, event);
                            }

                            device_detached_from_patient = (status & STATUS__CORRECT_CHECK) <= 0;
                            //Log.d(TAG, "Device Off = " + device_detached_from_patient);

                            boolean searching;
                            searching = (status & STATUS__SEARCHING) > 0;
                            //Log.d(TAG, "Oximeter is searching for consecutive pulse signals = " + searching);

                            // If Nonin is in searching state (i.e. it can't find a valid pulse signal) we also consider it to be disconnected from the body.
                            device_detached_from_patient |= searching;

                            smartpoint_algorithm = (status & STATUS__SMARTPOINT) > 0;
                            //Log.d(TAG, "Data successfully passed the SmartPoint Algorithm = " + smartpoint_algorithm);

                            boolean low_weak_signal = (status & STATUS__LOW_OR_WEAK_SIGNAL) > 0;
                            /*
                            if (low_weak_signal)
                            {
                                Log.d(TAG, "Pulse signal strength is 0.3% modulation or less");
                            }
                            */

                            int pulse_amplitude_index = ((decodedData[3] & 0xFF) << 8) | (decodedData[4] & 0xFF);
                            //Log.d(TAG, "pulse_amplitude_index : " + pulse_amplitude_index);

                            int counter = ((decodedData[5] & 0xFF) << 8) | (decodedData[6] & 0xFF);
                            //Log.d(TAG, "counter : " + counter);

                            int spo2 = (decodedData[7] & 0xFF);                                                                                 // Four beat average as displayed
                            //Log.d(TAG, "spo2 : " + spo2);

                            int pulse_rate = ((decodedData[8] & 0xFF) << 8) | (decodedData[9] & 0xFF);                                          // Four beat average as displayed
                            //Log.d(TAG, "pulse_rate : " + pulse_rate);

                            boolean valid_reading = (spo2 != 127) && (pulse_rate != 511);

                            Intent intent = new Intent();
                            intent.setAction(ACTION_DATA_AVAILABLE);
                            intent.putExtra(DATA_TYPE, DATATYPE_SPO2_MEASUREMENT);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__VALID_READING, valid_reading);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__SPO2, spo2);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__PULSE_RATE, pulse_rate);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__COUNTER, counter);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__TIMESTAMP, last_data_received_ntp_time);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__PULSE_AMPLITUDE_INDEX, pulse_amplitude_index);

                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__ENCRYPTION, encryption);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT, device_detached_from_patient);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__SEARCHING, searching);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__SMART_POINT_ALGORITHM, smartpoint_algorithm);
                            intent.putExtra(NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_WEAK_SIGNAL, low_weak_signal);

                            sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());

                            //updateBatteryLevel();
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


    private void disconnectFromNoninOximetryServiceNotification() {
        //ble_device.disableNotify(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_MEASUREMENT);
        if (ble_device != null)
        {
            ble_device.disableNotify(UUID_SERVICE_NONIN_OXIMETRY_SERVICE, UUID_CHARACTERISTIC_NONIN_OXIMETRY_MEASUREMENT, event ->
            {
                if (event.wasSuccess())
                {
                    Log.d(TAG, "disconnect from Nonin Oximetry Service ok " + event.status().toString());
                }
                else
                {
                    Log.d(TAG, "disconnect from Nonin Oximetry Service failed " + event.status().toString());
                }
            });
        }
    }


    private void handleSpotMeasurement(byte[] data)
    {
        /* Short Floating Point Data Structure, a 16-bit word comprising a signed 4-bit
        integer exponent followed by a signed 12-bit mantissa, used to represent
        floating point numbers with limited range and significantly reduce payload
        size. For example, the SpO2 value 98.0 is represented as 980 x 10-1, giving
        a mantissa of 0x3D4, and exponent of 0xF, which generates the final value
        of SpO2 in SFLOAT format as 0xF3D4.
            See IEC 11073-20601 for more details. */


        int spo2 = (int)readSFloat16(data[1], data[2]);
        int year = data[6] * 256 + (data[5] & 0xFF);
        int month = data[7] - 1;

        if (month == -1)
        {
            month = 11;
        }

        int day = data[8];
        int hour = data[9];
        int minute = data[10];
        int second = data[11];

        final Calendar spotTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        spotTime.set(Calendar.YEAR, year);
        spotTime.set(Calendar.MONTH, month);
        spotTime.set(Calendar.DATE, day);
        spotTime.set(Calendar.HOUR_OF_DAY, hour);
        spotTime.set(Calendar.MINUTE, minute);
        spotTime.set(Calendar.SECOND, second);

        long timestamp = spotTime.getTimeInMillis() - DateUtils.HOUR_IN_MILLIS;

        final Intent intent = new Intent(ACTION_NONIN_3230_SPOT_MEASUREMENT_AVAILABLE);
        intent.putExtra(NONIN_3230_SPOT_DATA_AS_STRING, (spo2 + "__" + timestamp));
        intent.putExtra(DATA_TYPE, DATATYPE_NONIN_3230_SPOT_MEASUREMENT);

        sendIntent(intent);
    }


    private void enableNoninSpotMeasurements()
    {
        if (ble_device != null)
        {
            ble_device.enableNotify(PULSE_OXIMETER_SERVICE_UUID, PLX_SPOT_CHECK_MEASUREMENT, event ->
            {
                switch (event.type())
                {
                    case ENABLING_NOTIFICATION:
                    {
                        Log.d(TAG, "Enabled spot measurements");
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                        {
                        if (event.wasSuccess())
                        {
                            Log.d(TAG, "Got Spot measurement " + Utils.byteArrayToHexString(event.data()));

                            handleSpotMeasurement(event.data());
                        }
                    }
                }
            });
        }
    }


    private void disableNoninSpotMeasurements()
    {
        if (ble_device != null)
        {
            ble_device.disableNotify(PULSE_OXIMETER_SERVICE_UUID, PLX_SPOT_CHECK_MEASUREMENT, event ->
            {
                if (event.wasSuccess())
                {
                    Log.d(TAG, "disable Notify spot notifications ok " + event.status().toString());
                }
                else
                {
                    Log.d(TAG, "disable Notify spot notifications failed " + event.status().toString());
                }
            });
        }
    }


    private void connectToRecordAccessControlPoint()
    {
        Log.d(TAG, "connectToRecordAccessControlPoint() nonin_spot_records_action : " + nonin_spot_records_action);

        if (ble_device != null)
        {
            ble_device.enableNotify(PULSE_OXIMETER_SERVICE_UUID, RECORD_ACCESS_CONTROL_POINT, event ->
            {
                switch (event.type())
                {
                    case ENABLING_NOTIFICATION:
                    {
                        if (event.wasSuccess())
                        {
                            final byte[] command_report_stored_records = { 0x01, 0x01};
                            final byte[] command_delete_stored_records = { 0x02, 0x01};

                            byte[] command_to_send = new byte[2];

                            switch (nonin_spot_records_action)
                            {
                                case DELETE_ALL_NONIN_SPOT_RECORDS:
                                {
                                    System.arraycopy(command_delete_stored_records, 0, command_to_send, 0, 2);
                                }
                                break;

                                case REPORT_ALL_NONIN_SPOT_RECORDS:
                                {
                                    System.arraycopy(command_report_stored_records, 0, command_to_send, 0, 2);
                                }
                                break;
                            }

                            //                 1822                         2A52
                            ble_device.write(PULSE_OXIMETER_SERVICE_UUID, RECORD_ACCESS_CONTROL_POINT, command_to_send, event2 ->
                            {
                                if (event2.wasSuccess())
                                {
                                    Log.d(TAG, "enabling RECORD_ACCESS_CONTROL_POINT ok" + Utils.byteArrayToHexString(event2.data()));
                                }
                                else
                                {
                                    Log.d(TAG, "enabling RECORD_ACCESS_CONTROL_POINT fail");
                                }
                            });
                        }
                        else
                        {
                            Log.d(TAG, "enable notify RECORD_ACCESS_CONTROL_POINT ok failed" + event.status().toString());
                        }
                    }
                    break;

                    case NOTIFICATION:
                    case INDICATION:
                    {
                        if (event.wasSuccess())
                        {
                            Log.d(TAG, "NOTIFICATION/INDICATION " + event.status().toString() + ":" + Utils.byteArrayToHexString(event.data()));
                        }
                        else
                        {
                            Log.d(TAG, "NOTIFICATION/INDICATION FAIL  " + event.status().toString());
                        }
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
                Log.d(TAG, event.type().toString() + " : " + event.charUuid().toString() + " : " + event.status().toString() + " : Data = " + Utils.byteArrayToHexString(event.data()));

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
                            Log.d(TAG, "UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT : Raw data : " + Utils.byteArrayToHexString(event.data()));

                            byte[] decoded_data;
                            decoded_data = event.data();

                            int opcode = decoded_data[0] & 0xFF;
                            int result = decoded_data[1] & 0xFF;

                            if (opcode == OPCODE_SET_NONIN_SECURITY_MODE_RESPONSE)
                            {
                                if (result == 0x00)
                                {
                                    startGettingData();
                                }
                                else
                                {
                                    Log.e(TAG, "setNoninSecurityMode Command FAILED");
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

        // 0x01 is Security Mode 1. In Security Mode 1, the 3230 allows the creation of a new bond during each connection without requiring battery removal and insertion
        byte[] command = {OPCODE_SET_NONIN_SECURITY_MODE, SECURITY_CHAR_0, SECURITY_CHAR_1, SECURITY_CHAR_2, 0x01};

        if (ble_device != null)
        {
            writeCommandToNoninOximetryControlPoint(command, "setNoninSecurityMode");
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
            }
            else
            {
                Log.e(TAG, callingFunctionName + " DID NOT FAIL : " + event.status().toString() + " " + event.data_string());
            }
        });
    }


    public void getTimestamp()
    {
        Log.d(TAG, "getTimestamp()");

        // Only once the connection to the "response" indication is established will commands be set. This in turn calls the real getTimestamp code
        connectToNoninControlPointNotification();
    }


    private void getTimestampNowResponseCanBeReceived()
    {
        if (ble_device != null)
        {
            ble_device.read(CURRENT_TIME_SERVICE_UUID, CURRENT_TIME, event ->
            {
                if (event.wasSuccess())
                {
                    int year = (event.data()[1] * 256) + (event.data()[0] & 0xFF) ;  // 0xFF because Java bytes are signed
                    int month = event.data()[2];
                    int day = event.data()[3];
                    int hours = event.data()[4];
                    int minutes = event.data()[5];
                    int seconds = event.data()[6];

                    final String yearStr = String.valueOf(year - 2000);
                    final String monthStr = Utils.leftPadWithZeroes(String.valueOf(month), 2);
                    final String dayStr = Utils.leftPadWithZeroes(String.valueOf(day), 2);
                    final String hourStr = Utils.leftPadWithZeroes(String.valueOf(hours), 2);
                    final String minutesStr = Utils.leftPadWithZeroes(String.valueOf(minutes), 2);
                    final String secondsStr = Utils.leftPadWithZeroes(String.valueOf(seconds), 2);

                    DateTime dt;
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyMMddHHmmss");

                    try
                    {
                        dt = formatter.parseDateTime(yearStr.concat(monthStr).concat(dayStr).concat(hourStr).concat(minutesStr).concat(secondsStr));

                        Log.d(TAG, "getTimestamp Command successful. DateTime = " + dt);
                    }
                    catch (IllegalFieldValueException ex)
                    {
                        dt = formatter.parseDateTime("100101010101");

                        Log.d(TAG, "IllegalFieldValueException: " + ex + " caught, sending fake value to prompt setting of correct time");
                    }

                    final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                    intent.putExtra(DATA_TYPE, DATATYPE_GET_TIMESTAMP);
                    intent.putExtra(RECEIVED_TIMESTAMP_AS_LONG, dt.getMillis());
                    sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
                }
            });
        }
    }

        //Log.d("-pb", " >> " + Utils.byteArrayToHexString(set_storage_rate_command));
        // Why this failed before... int sent as 01 not as 0x31
        // claimed success with  >> 60 4E 4D 49 07 44 53 52 3D 31 0D 0A
        // using int                60 4E 4D 49 07 44 53 52 3D 01 0D 0A

        // NOTIFICATION seems to be NOTIFICATION : 1447af80-0d60-11e2-88b6-0002a5d5c51b : SUCCESS : Data = E0 00 01 06
        //
        // 2019-07-25 14:50:50.726 7062-7062/com.isansys.patientgateway D/BluetoothLeDevice-BluetoothLeNonin3230: ReadWriteListener : NOTIFICATION : 1447af80-0d60-11e2-88b6-0002a5d5c51b : SUCCESS : Data = E0 00 01 06
        //2019-07-25 14:50:50.727 7062-7062/com.isansys.patientgateway D/BluetoothLeNonin3230: UUID_CHARACTERISTIC_NONIN_OXIMETRY_CONTROL_POINT. connection_state = SET_ACTIVATION_MODE : Raw data : E0 00 01 06


    public void connectToCharacteristics()
    {
        Log.d(TAG, "connectToCharacteristics");

        setNoninSecurityMode();
    }


    private void startGettingData()
    {
        Log.d(TAG, "startGettingData() spO2_spot_measurements_enabled : " + PatientGatewayService.spO2_spot_measurements_enabled);

        connectToNoninOximetryServiceNotification();

        connectToRecordAccessControlPoint();

        //updateBatteryLevel();

        if (PatientGatewayService.spO2_spot_measurements_enabled)
        {
            // --- If we wish to use Nonin Spot measurements as well as Isansys Spot measurements, uncomment 2 lines below...

            // enableNoninSpotMeasurements();
            // nonin_spot_records_action = NoninSpotRecordsAction.REPORT_ALL_NONIN_SPOT_RECORDS;

            // but as we're not using Nonin Spot measurements for now, disable them...
            disableNoninSpotMeasurements();
        }
        else
        {
            // Always disable spot measurements in case they were enabled by previous use of the device...
            disableNoninSpotMeasurements();
        }
    }


    // Workaround for IIT-2744 Nonin 3230 battery level reported as 100 when batteries low
    // Obtaining battery level via ble_device.readBatteryLevel() doesn't seem to work for the Nonin 3230, it always seems to report 100
    // More accurate Battery low / high can be obtained from the Nonin Data format via STATUS__LOW_BATTERY
    private void reportBatteryLevel(boolean lowBattery, BleDevice.ReadWriteListener.ReadWriteEvent event)
    {
        int battery_as_int = 100;

        if (lowBattery == true)
        {
            battery_as_int = 20;
        }

        long timestamp_in_ms = ntp_time.currentTimeMillis();

        if (Math.abs(timestamp_in_ms - time_of_last_battery_reading_sent) > DateUtils.MINUTE_IN_MILLIS)
        {
            Intent intent = new Intent();
            intent.setAction(ACTION_DATA_AVAILABLE);
            intent.putExtra(DATA_TYPE, DATATYPE_DEVICE_STATUS);
            intent.putExtra(BluetoothLeNonin3230.NONIN_DEVICE_STATUS__BATTERY_PERCENTAGE, battery_as_int);

            time_of_last_battery_reading_sent = timestamp_in_ms;
            intent.putExtra(BluetoothLeNonin3230.NONIN_DEVICE_STATUS__TIMESTAMP, timestamp_in_ms);

            sendIntentWithDeviceAddressAndDataAsString(intent, event.device().getMacAddress(), event.data());
        }
    }


    // IIT-2744 Nonin 3230 battery level reported as 100 when batteries low, see comment above reportBatteryLevel() 
    /*private void updateBatteryLevel()
    {
        Log.d(TAG, "updateBatteryLevel()");

        ble_device.readBatteryLevel(e -> {
            if (e.type() == BleDevice.ReadWriteListener.Type.READ)
            {
                if (e.wasSuccess())
                {
                    byte[] decodedData1 = e.data();

                    int battery_as_int = decodedData1[0];

                    Log.d(TAG,"battery_as_int: " + battery_as_int);

                    long timestamp_in_ms = ntp_time.currentTimeMillis();

                    if (Math.abs(timestamp_in_ms - time_of_last_battery_reading_sent) > DateUtils.MINUTE_IN_MILLIS)
                    {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_DATA_AVAILABLE);
                        intent.putExtra(DATA_TYPE, DATATYPE_DEVICE_STATUS);
                        intent.putExtra(BluetoothLeNonin3230.NONIN_DEVICE_STATUS__BATTERY_PERCENTAGE, battery_as_int);

                        time_of_last_battery_reading_sent = timestamp_in_ms;
                        intent.putExtra(BluetoothLeNonin3230.NONIN_DEVICE_STATUS__TIMESTAMP, timestamp_in_ms);
                        sendIntentWithDeviceAddressAndDataAsString(intent, e.device().getMacAddress(), e.data());
                    }
                }
                else
                {
                    Log.d(TAG, "Nonin 3230 battery read failed " + e.data_string() + " " + e.gattStatus());
                }
            }
        });
    }*/


    @Override
    public void sendConnectedIntentAndContinueConnection()
    {
        // Several Nonin packets are longer than the default 20 byte MTU.

        Log.e(TAG, "sendConnectedIntentAndContinueConnection");

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
        // No PPG data on Nonin 3230
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
        Log.d(TAG,"sendTurnOffCommand()");
    }


    public void sendNightModeCommand(final boolean enable_night_mode)
    {
        // Not implemented on this device but function needs to exist
    }


    public void sendTimestamp(long timestamp_now_in_milliseconds, final byte[] value)
    {
        // If the batteries are removed from the Nonin 3230, the time will default back to 1st Jan 2016, 00:00:00
        Log.d(TAG, "sendTimestamp");

        DateTime date_time = new DateTime(timestamp_now_in_milliseconds);

        int year = date_time.getYear();
        int month = date_time.getMonthOfYear();
        int day = date_time.getDayOfMonth();
        int hour = date_time.getHourOfDay();
        int minute = date_time.getMinuteOfHour();
        int second = date_time.getSecondOfMinute();

        // Years are written little endian
        int yearMsb = year / 256;
        int yearLsb = year % 256;

        if (ble_device != null)
        {
            byte[] timestamp = { (byte) yearLsb, (byte) yearMsb, (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) second, 0x00, 0x00, 0x00 };

            ble_device.write(CURRENT_TIME_SERVICE_UUID, CURRENT_TIME, timestamp, event -> {
                if (!event.wasSuccess())
                {
                    Log.d(TAG, "setting time failed " + event.status().toString());

                    //resetConnectionState();
                }
                else
                {
                    Log.d(TAG, "Set time OK " + hour + ":" + minute + ":" + second + " Day:"+day + " Month: " + month + " Year:" + year);

                    startGettingData();
                }
            });
        }
    }


    @Override
    public void onDiscovered(BleManager.DiscoveryListener.DiscoveryEvent event)
    {
        ble_device = event.device();

        just_discovered = true;

        int manufacturer_id = ble_device.getManufacturerId();

        Log.d(TAG, "onDiscovered Nonin 3230 : " + manufacturer_id);

        BleDeviceConfig config = BleDeviceConfig.newNulled(); // get a nulled version, so that it doesn't override any of the ble manager config settings accidentally.

        // set the non-nullable settings to match the ones we have in the BleDeviceController
        config.autoEnableNotifiesOnReconnect = false;

        // Initial pairing & connection still not 100% reliable
        //    config.alwaysBondOnConnect = false - reliable except for the first pairing connection after Gateway startup
        //                  during the initial failure, log shows "checkLastData - data received". called by checkLastDataIsRestartRequired called by checkLastDataIsRestartRequired
        //                  called by device_oversight_timer
        //    config.alwaysBondOnConnect = true  - first connection OK, subsequent connections OK generally, but did get into a state once where pairing would
        //                  continuously fail


        config.alwaysBondOnConnect = true;

        ble_device.setConfig(config);

        Log.d(TAG, "Nonin 3230 config alwaysBondOnConnect:" + config.alwaysBondOnConnect + " autoEnableNotifiesOnReconnect:" + config.autoEnableNotifiesOnReconnect);

        if (event.was(BleManager.DiscoveryListener.LifeCycle.UNDISCOVERED))
        {
            Log.d(getChildTag(), getDescriptiveDeviceName() + " UNDISCOVERED");
        }
        else if(ble_device.is(BONDED) && !ble_device.is(CONNECTED, CONNECTING, CONNECTING_OVERALL))
        {
            Log.d(getChildTag(), "*************** onDiscovered Nonin 3230 : " + ble_device.getMacAddress());

            Log.d(TAG,"3230 calling executeConnect from -1-");

            executeConnect();
        }
        else if(!ble_device.is(BONDING))
        {
            ble_device.setListener_Bond(m_bond_listener);

            sendPairingIntent();

            Log.d(TAG,"3230 calling executeConnect from -2-");

            executeConnect();
        }
        else
        {
            Log.d(getChildTag(), "onDiscovered Nonin 3230 : BONDING in progress");

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
