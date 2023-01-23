package com.isansys.patientgateway.bluetooth.temperature;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.text.format.DateUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetooth.BluetoothService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.isansys.common.DeviceInfoConstants.INVALID_DEVICE_SESSION_TIME;

public class Fora_Ir20 extends BluetoothService
{
    private final static String TAG = Fora_Ir20.class.getSimpleName();

    public final static String DATATYPE_FORA_TEMPERATURE_MEASUREMENT = "DATATYPE_FORA_TEMPERATURE_MEASUREMENT";

    public final static String ACTION_PAIRING = "FORA_IR20.ACTION_PAIRING";
    public final static String ACTION_PAIRING_SUCCESS = "FORA_IR20.ACTION_PAIRING_SUCCESS";
    public final static String ACTION_PAIRING_FAILURE = "FORA_IR20.ACTION_PAIRING_FAILURE";
    public final static String ACTION_DATA_AVAILABLE = "FORA_IR20.ACTION_DATA_AVAILABLE";
    public final static String ACTION_TURNED_OFF = "FORA_IR20.ACTION_TURNED_OFF";
    public final static String ACTION_UNEXPECTED_UNBOND  = "FORA_IR20.ACTION_UNEXPECTED_UNBOND";

    // Outgoing Intents
    public final static String FORA_IR20__CONNECTED = "com.isansys.patientgateway.FORA_IR20__CONNECTED";
    public final static String FORA_IR20__DISCONNECTED = "com.isansys.patientgateway.FORA_IR20__DISCONNECTED";

    // Incoming Intents
    public final static String SET_ADDED_TO_SESSION = "com.isansys.patientgateway.FORA_IR20_SET_ADDED_TO_SESSION";
    public final static String SET_REMOVED_FROM_SESSION = "com.isansys.patientgateway.FORA_IR20_SET_REMOVED_FROM_SESSION";

    // Development only
    public final static String DEV_READ_TIME = "com.isansys.patientgateway.DEV_READ_TIME";

    // FROM BluetoothLeDevice
    public final static String FORA_DATA_AS_STRING = "DATA_AS_STRING";
    public final static String FIRMWARE_VERSION_NUMBER = "FIRMWARE_VERSION_NUMBER";
    public final static String DATA_TYPE = "DATA_TYPE";
    public final static String DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public final static String PACKET_ID = "PACKET_ID";

    private final static byte FORA_CMD_READ_DEVICE_CLOCK = 0x23;
    private final static byte FORA_CMD_READ_DEVICE_MODEL = 0x24;
    private final static byte FORA_CMD_READ_STORAGE_TIME_DATA_WITH_INDEX = 0x25;     // Get time of reading
    private final static byte FORA_CMD_READ_STORAGE_RESULT_DATA_WITH_INDEX = 0x26;   // Get temperature readings
    private final static byte FORA_CMD_READ_STORAGE_NUMBER_OF_DATA = 0x2B;

    private final static byte FORA_CMD_WRITE_SYSTEM_CLOCK = 0x33;

    private final static byte FORA_CMD_TURN_OFF_DEVICE = 0x50;
    private final static byte FORA_START_FRAME = 0x51;  // Every sent frame starts with this
    private final static byte FORA_CMD_CLEAR_MEMORY = 0x52;
    private final static byte FORA_READY_FOR_COMMANDS = 0x54;

    private final static byte FORA_STOP = (byte) 0xA3;  // Every sent frame contains this after Data_3

    private boolean setupComplete = false; // Connection now set up and readings can be taken from subsequent connections

    private int numberOfReadingsOnDevice = 0;
    private int deviceReadingIndex = 0;
    private int offsetSeconds = 0;
    private int correctionSeconds = 59;

    // Debug, for release these should both be false (and these bools deleted)
    private final boolean debugLeaveForaOn = false;
    private final boolean debugDontClearMemory = false;

    // Locations of items in bluetooth_read_buffer array for Fora
    private final static int FORA_COMMS_ACK = 1;    // ACK codes are the same as the CMD that requested it, except ACK 0x54 that happens after connection
    private final static int FORA_COMMS_DATA_0 = 2;
    private final static int FORA_COMMS_DATA_1 = 3;
    private final static int FORA_COMMS_DATA_2 = 4;
    private final static int FORA_COMMS_DATA_3 = 5;

    private final ArrayList<ForaReading> fora_readings = new ArrayList<>();
    private final ArrayList<ForaReading> fora_readings_filtered = new ArrayList<>();

    private enum ForaState
    {
        STATE_NONE,
        STATE_INITIAL_ARBITRARY_COMMAND_SENT,
        STATE_READY_TO_SET_TIME_AND_DATE,
        STATE_GETTING_READINGS,
        STATE_GETTING_TIMINGS,
        STATE_CHECKING_TIME_AND_DATE,
        STATE_NEED_TO_SET_TIME_AND_DATE,
        STATE_CLEARING_MEMORY_AFTER_INITIAL_CONNECTION,
        STATE_DEVICE_OFF,
        STATE_DEBUG
    }

    private enum ForaTemperatureFormat
    {
        TEMPERATURE_CELSIUS,
        TEMPERATURE_FAHRENHEIT
    }

    private ForaState foraState = ForaState.STATE_NONE;

    static class ForaReading
    {
        int temperature_integer;
        int temperature_fraction;
        long timestamp;
        ForaTemperatureFormat temperature_format;
    }

    static class MinuteOfForaDataAddedAlready
    {
        int hour;
        int minute;

        // Overriding equals() to compare two MinuteOfForaDataAddedAlready objects values
        @Override
        public boolean equals(Object o) {

            // If the object is compared with itself then return true
            if (o == this) {
                return true;
            }

        /* Check if o is an instance of MinuteOfForaDataAddedAlready or not
          "null instanceof [type]" also returns false */
            if (!(o instanceof MinuteOfForaDataAddedAlready)) {
                return false;
            }

            // typecast o to MinuteOfForaDataAddedAlready so that we can compare data members
            MinuteOfForaDataAddedAlready m = (MinuteOfForaDataAddedAlready) o;

            // Compare the data members and return accordingly
            return hour == m.hour && minute == m.minute;
        }
    }


    private final CountDownTimer firstRealCommandTimer = new CountDownTimer(3000, 3000) {
        @Override
        public void onTick(long l)
        {
            Log.d(TAG, "onTick()");
        }

        @Override
        public void onFinish()
        {
            if (!setupComplete)
            {
                setForaTimeAndDateToNow();  // Need to set time and date after initial connection to get offset and complete connection
            }
            else
            {
                getForaTimeAndDate();       // If setup is complete, check date is actually correct because batteries might have been changed since connection
            }
        }
    };

    public Fora_Ir20()
    {
        super("Fora_Ir20", DeviceConnectingMode.CONNECT_TO_DEVICE, true, true);
    }

    @Override
    public IntentFilter makeIntentFilter_IncomingFromPatientGateway()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Fora_Ir20.SET_ADDED_TO_SESSION);
        intentFilter.addAction(Fora_Ir20.SET_REMOVED_FROM_SESSION);
        intentFilter.addAction(Fora_Ir20.DEV_READ_TIME);

        return intentFilter;
    }


    @Override
    public void handleIncomingFromPatientGateway(Context context, Intent intent)
    {
        String action = intent.getAction();

        Log.d(TAG, "handleIncomingFromPatientGateway " + action);

        switch (action)
        {
            case SET_ADDED_TO_SESSION:
            {
                setDeviceAddedToSession();
            }
            break;

            case SET_REMOVED_FROM_SESSION:
            {
                Log.d(TAG, "Fora SET_REMOVED_FROM_SESSION");

                correctionSeconds = 59;
                setupComplete = false;

                setDeviceRemovedFromSession();
            }
            break;

            case DEV_READ_TIME:
            {
                foraState = ForaState.STATE_DEBUG;

                sendForaMessage(FORA_CMD_READ_DEVICE_CLOCK);
            }
            break;
        }
    }


    private void getReadings()
    {
        fora_readings.clear();

        foraState = ForaState.STATE_GETTING_READINGS;

        sendForaMessage(FORA_CMD_READ_STORAGE_NUMBER_OF_DATA);
    }


    private void sendForaMessage(byte command)
    {
        sendForaMessage(command, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00);
    }


    private void sendForaMessage(byte command, byte data_0, byte data_1, byte data_2, byte data_3)
    {
        Log.d(TAG,"sendForaMessage command:" + Utils.convertByteToString(command) + " data_0:" + Utils.convertByteToString(data_0));

        byte checksum = calculateForaChecksum(command, data_0, data_1, data_2, data_3);

        byte[] fora_message = {FORA_START_FRAME,  command, data_0,  data_1, data_2,   data_3,  FORA_STOP,  checksum};

        write(fora_message);
    }


    private void getForaTimeAndDate()
    {
        Log.d(TAG, "getForaTimeAndDate");

        foraState = ForaState.STATE_CHECKING_TIME_AND_DATE;

        sendForaMessage(FORA_CMD_READ_DEVICE_CLOCK);
    }


    // An "arbitrary" command must be sent after connection to put the device into "PCL" (Communication) mode.
    // If a following "real" command is not sent shortly afterwards, the Fora will switch off after ~ 15 seconds.
    // The arbitrary command will not be acknowledged.
    // From TICD_Thermometer_v1.16_20180430.pdf page 3:
    //  "For thermometer, please send first two adjoining commands in 10 seconds in order to make sure the device is in communication mode"
    private void sendArbitraryCommand()
    {
        Log.d(TAG, "sendArbitraryCommand()");

        foraState = ForaState.STATE_INITIAL_ARBITRARY_COMMAND_SENT;

        sendForaMessage(FORA_CMD_READ_DEVICE_MODEL);

        firstRealCommandTimer.start();
    }


    private void setForaTimeAndDateToNow()
    {
        // Data_0 and Data_1, how years, months and days are handled in Fora from TICD_Thermometer_v1.16_20180430.pdf Table A (page 7)
        // MSB                           LSB
        // Data_1          | Data_0
        // Year (7)      Month (4) Day (5)
        // 6 5 4 3 2 1 0 3 | 2 1 0 4 3 2 1 0

        foraState = ForaState.STATE_READY_TO_SET_TIME_AND_DATE;

        Date now = new Date(PatientGatewayService.getNtpTimeNowInMillisecondsStatic());
        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTime(now);

        offsetSeconds = c.get(Calendar.SECOND);

        Log.d(TAG, "setForaTimeAndDateToNow() Present time:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(now) + " offsetSeconds:" + offsetSeconds);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR) - 2000;

        String day_as_binary = convertIntToBinaryStringOfLength(day, 5);
        String month_as_binary = convertIntToBinaryStringOfLength(month, 4);
        String year_as_binary = convertIntToBinaryStringOfLength(year, 7);
        String year_month_day_as_binary = year_as_binary + month_as_binary + day_as_binary;

        String data_0_as_binary = year_month_day_as_binary.substring(8,16);
        String data_1_as_binary = year_month_day_as_binary.substring(0,8);

        int data_0_int = Integer.parseInt(data_0_as_binary, 2);
        byte data_0 = (byte) data_0_int;
        int data_1_int = Integer.parseInt(data_1_as_binary, 2);
        byte data_1 = (byte) data_1_int;

        byte data_2 = (byte) minute;
        byte data_3 = (byte) hour;

        sendForaMessage(FORA_CMD_WRITE_SYSTEM_CLOCK, data_0, data_1, data_2, data_3);
    }


    private byte calculateForaChecksum(byte command, byte data_0, byte data_1, byte data_2, byte data_3)
    {
        int checksum = (int) command + (int) data_0 + (int) data_1 + (int) data_2 + (int) data_3 + (int) FORA_START_FRAME + (int) FORA_STOP;

        return (byte)checksum;
    }


    private String convertIntToBinaryStringOfLength(int n, int length_required)
    {
        String tmp = Integer.toBinaryString(n);

        if (tmp.length() < length_required)
        {
            tmp = Utils.leftPadWithZeroes(tmp, length_required);
        }
        else
        {
            tmp = tmp.substring(tmp.length() - length_required);
        }

        return tmp;
    }


    @Override
    public String getDesiredDeviceBluetoothAddress()
    {
        return PatientGatewayService.device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__FORA_IR20).bluetooth_address;
    }


    @Override
    public String getBluetoothDeviceName()
    {
        return "Taidoc-Device";
    }

    private long getTimestampFromForaData(byte[] foraData)
    {
        String data0_as_binary = convertIntToBinaryStringOfLength(foraData[FORA_COMMS_DATA_0], 8);
        String data1_as_binary = convertIntToBinaryStringOfLength(foraData[FORA_COMMS_DATA_1], 8);
        String data2_as_binary = convertIntToBinaryStringOfLength(foraData[FORA_COMMS_DATA_2], 8);
        String data3_as_binary = convertIntToBinaryStringOfLength(foraData[FORA_COMMS_DATA_3], 8);

        String hour_as_binary = data3_as_binary.substring(3, 8);
        String minute_as_binary = data2_as_binary.substring(2, 8);

        int hour = Integer.parseInt(hour_as_binary, 2);
        int minute = Integer.parseInt(minute_as_binary, 2);

        String year_as_binary = data1_as_binary.substring(0, 7);
        String month_as_binary = data1_as_binary.substring(7) + data0_as_binary.substring(0, 3);
        String day_as_binary = data0_as_binary.substring(3, 8);

        int year = Integer.parseInt(year_as_binary, 2) + 2000;
        int month = Integer.parseInt(month_as_binary, 2) - 1;
        int day = Integer.parseInt(day_as_binary, 2);

        final Calendar foraTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        foraTime.set(Calendar.YEAR, year);
        foraTime.set(Calendar.MONTH, month);
        foraTime.set(Calendar.DATE, day);
        foraTime.set(Calendar.HOUR_OF_DAY, hour);
        foraTime.set(Calendar.MINUTE, minute);
        foraTime.set(Calendar.SECOND,0);

        long foraTimeCorrectMinute = foraTime.getTimeInMillis();

        // Add offset (Fora time is behind the real time by offsetSeconds)
        foraTimeCorrectMinute += offsetSeconds * DateUtils.SECOND_IN_MILLIS;

        // We used offset seconds to find the correct minute. We cannot know to the second what time the measurement was taken because Fora does not supply seconds.
        // So we are using the seconds from the measurement received time as seconds for this measurement. We cannot just add them because this might push the measurement into the next minute.
        // However this can cause a problem if a measurement is taken soon after connection
        // 14:57:10 - add Fora to session
        // 14:57:40 - take the 1st reading
        // 14:58:05 - the Gateway revceives the reading taken at 14:57:40 and "corrects" the time to 14:57:05
        // This time is before the Fora was added to the Session, so it is excluded
        // Solution? if measurement minute == same minute Fora was added, adjust seconds

        Calendar foraTimeCorrectMinuteZeroSeconds = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        foraTimeCorrectMinuteZeroSeconds.setTimeInMillis(foraTimeCorrectMinute);
        foraTimeCorrectMinuteZeroSeconds.set(Calendar.SECOND,0);
        long foraTimeCorrectMinuteZeroSecondsLong = foraTimeCorrectMinuteZeroSeconds.getTimeInMillis();

        long start_time = PatientGatewayService.device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__FORA_IR20).device_session_start_time;

        long foraMeasurementTime;

        long difference = Math.abs(foraTimeCorrectMinuteZeroSecondsLong - start_time);

        if (timesAreInTheSameMinute(foraTimeCorrectMinuteZeroSecondsLong, start_time) && difference < DateUtils.HOUR_IN_MILLIS)
        {
            // Get seconds value from start time
            final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(start_time);
            int seconds = c.get(Calendar.SECOND);

            foraMeasurementTime = foraTimeCorrectMinuteZeroSecondsLong;

            // Make this measurement appear at hh:mm:59
            //foraMeasurementTime += correctionSeconds * DateUtils.SECOND_IN_MILLIS;

            Log.d(TAG, "Time is in same minute as connection time returning " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(foraMeasurementTime) + " difference:" + difference + " state:" + foraState);
        }
        else
        {
            // Use seconds value from time now as seconds value for this measurement

            // IIT 2377 - trying to NOT use seconds value of received time to avoid graph bunching

            //Date now = new Date(PatientGatewayService.getNtpTimeNowInMillisecondsStatic());
            //final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            //c.setTime(now);

            //int secondsNow = c.get(Calendar.SECOND);
            //foraMeasurementTime = foraTimeCorrectMinuteZeroSecondsLong + (secondsNow * DateUtils.SECOND_IN_MILLIS);

            foraMeasurementTime = foraTimeCorrectMinuteZeroSecondsLong;
        }

        return foraMeasurementTime;
    }


    private ForaTemperatureFormat getTemperatureFormatFromTimeReading(byte hour)
    {
        String data3_as_binary = convertIntToBinaryStringOfLength(hour, 8);

        ForaTemperatureFormat returnValue;

        if (data3_as_binary.startsWith("0"))
        {
            returnValue = ForaTemperatureFormat.TEMPERATURE_CELSIUS;
        }
        else
        {
            Log.d(TAG,"** Fahrenheit measurement detected on Fora **");

            returnValue = ForaTemperatureFormat.TEMPERATURE_FAHRENHEIT;
        }

        return returnValue;
    }


    @Override
    public void handleMessageRead(byte[] bluetooth_read_buffer)
    {
        Log.d(TAG, "handleMessageRead Ack:" + Utils.convertByteToString(bluetooth_read_buffer[FORA_COMMS_ACK]) + " foraState:" + foraState);

        switch (foraState)
        {
            case STATE_READY_TO_SET_TIME_AND_DATE:
            {
                if (bluetooth_read_buffer[FORA_COMMS_ACK] == FORA_CMD_WRITE_SYSTEM_CLOCK)
                {
                    if (!setupComplete)
                    {
                        firstRealCommandTimer.cancel();

                        Log.d(TAG, "Fora setup complete (connected and time set), will now clear memory and switch off...");

                        setupComplete = true;

                        foraState = ForaState.STATE_CLEARING_MEMORY_AFTER_INITIAL_CONNECTION;

                        clearForaMemory();
                    }
                    else
                    {
                        // There may be readings taken before the batteries were replaced

                        Log.d(TAG, "Fora clock reset during a session probably because batteries were replaced so trying to get readings taken before batteries were removed...");

                        getReadings();
                    }
                }
            }
            break;

            case STATE_INITIAL_ARBITRARY_COMMAND_SENT:
            {
                if (bluetooth_read_buffer[FORA_COMMS_ACK] == FORA_CMD_READ_DEVICE_MODEL)
                {
                    foraState = ForaState.STATE_READY_TO_SET_TIME_AND_DATE;
                }
            }
            break;

            case STATE_GETTING_READINGS:
            {
                if (bluetooth_read_buffer[FORA_COMMS_ACK] == FORA_CMD_READ_STORAGE_NUMBER_OF_DATA)
                {
                    numberOfReadingsOnDevice =  bluetooth_read_buffer[FORA_COMMS_DATA_0] + (bluetooth_read_buffer[FORA_COMMS_DATA_1] * 256);

                    Log.d(TAG, "Fora reports that it has " + numberOfReadingsOnDevice + " reading(s)");

                    if (numberOfReadingsOnDevice > 0)
                    {
                        startGettingReadingsFromDevice();
                    }
                }

                if (bluetooth_read_buffer[FORA_COMMS_ACK] == FORA_CMD_READ_STORAGE_RESULT_DATA_WITH_INDEX)
                {
                    int temperature_units = bluetooth_read_buffer[FORA_COMMS_DATA_1] * 256 + bluetooth_read_buffer[FORA_COMMS_DATA_0];
                    float temperature = (float)temperature_units * (float)0.1; // Fora Ir20 records in units, each unit representing 0.1 degrees C

                    String temperature_as_string = String.valueOf(temperature);
                    int index_of_decimal = temperature_as_string.indexOf(".");

                    String temperature_integral = temperature_as_string.substring(0, index_of_decimal);
                    String temperature_fractional = temperature_as_string.substring(index_of_decimal + 1, index_of_decimal + 2);

                    Log.d(TAG, "Fora device reports measurement of "+ temperature_integral + "." + temperature_fractional + " degrees (from " + temperature + " )");

                    ForaReading newReading = new ForaReading();
                    newReading.temperature_integer = Integer.parseInt(temperature_integral);
                    newReading.temperature_fraction = Integer.parseInt(temperature_fractional);
                    fora_readings.add(newReading);

                    if (deviceReadingIndex + 1 == numberOfReadingsOnDevice)
                    {
                        Log.d(TAG, "Fora has finished getting readings, now getting timings...");

                        foraState = ForaState.STATE_GETTING_TIMINGS;

                        startGettingTimingsFromDevice();
                    }
                    else
                    {
                        getNextReadingFromDevice();
                    }
                }
            }
            break;

            case STATE_DEBUG:
            {

                long foraTimeTimestamp = getTimestampFromForaData(bluetooth_read_buffer);
                long nowToCompare = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();
                long difference = Math.abs(foraTimeTimestamp - nowToCompare);

                Date now = new Date(PatientGatewayService.getNtpTimeNowInMillisecondsStatic());
                final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                c.setTime(now);
                int secondsNow = c.get(Calendar.SECOND);
                int minutesNow = c.get(Calendar.MINUTE);
                int secondsToAdd = secondsNow + offsetSeconds;

                long foraTimeTimestampWithSeconds = foraTimeTimestamp + (secondsToAdd * DateUtils.SECOND_IN_MILLIS);

                Log.d(TAG, "Fora device reports Time: " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(foraTimeTimestamp) + " with seconds added: " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(foraTimeTimestampWithSeconds) + " Time now: " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(nowToCompare) + " difference:" + difference);

                String strNow = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(nowToCompare);
                String strRaw = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(foraTimeTimestamp);
                String strAdj = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(foraTimeTimestampWithSeconds);

                Log.d(TAG, "now: " + strNow + " raw: " + strRaw + " adj:" + strAdj + " offset:" + offsetSeconds + " secondsNow:" + secondsNow + " minutesNow: " + minutesNow);
            }
            break;

            case STATE_CHECKING_TIME_AND_DATE:
            {
                if (bluetooth_read_buffer[FORA_COMMS_ACK] == FORA_CMD_READ_DEVICE_CLOCK || bluetooth_read_buffer[FORA_COMMS_ACK] == FORA_CMD_WRITE_SYSTEM_CLOCK)
                {
                    try
                    {
                        long foraTimeTimestamp = getTimestampFromForaData(bluetooth_read_buffer);
                        long nowToCompare = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();

                        // can only really use the offset to check accuracy of time now
                        Date now = new Date(PatientGatewayService.getNtpTimeNowInMillisecondsStatic());
                        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        c.setTime(now);
                        int secondsNow = c.get(Calendar.SECOND);
                        int secondsToAdd = secondsNow + offsetSeconds;

                        long foraTimeTimestampWithSeconds = foraTimeTimestamp + (secondsToAdd * DateUtils.SECOND_IN_MILLIS);
                        long difference = Math.abs(nowToCompare - foraTimeTimestampWithSeconds);

                        Log.d(TAG, "Fora device reports Time: " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(foraTimeTimestamp) + " with seconds added: " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(foraTimeTimestampWithSeconds) + " Time now: " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(nowToCompare) + " difference:" + difference + " setupComplete? " + setupComplete);

                        if (difference > DateUtils.MINUTE_IN_MILLIS)
                        {
                            Log.d(TAG, "Time on Fora is > 1 minute different to actual time now, so need to set time on Fora");

                            foraState = ForaState.STATE_NEED_TO_SET_TIME_AND_DATE;

                            setForaTimeAndDateToNow();
                        }
                        else
                        {
                            if (setupComplete)
                            {
                                Log.d(TAG, "Fora time is correct, getting readings..." + foraState);

                                getReadings();
                            }
                        }
                    }
                    catch(Exception ex)
                    {
                        Log.d(TAG, "** Fora Exception ** " + Utils.convertByteToString(bluetooth_read_buffer[FORA_COMMS_ACK]));
                    }
                }
            }
            break;

            case STATE_GETTING_TIMINGS:
            {
                if (bluetooth_read_buffer[FORA_COMMS_ACK] == FORA_CMD_READ_STORAGE_TIME_DATA_WITH_INDEX)
                {
                    long new_reading_time = getTimestampFromForaData(bluetooth_read_buffer);

                    ForaReading reading = fora_readings.get(deviceReadingIndex);
                    reading.timestamp = new_reading_time;
                    fora_readings.set(deviceReadingIndex, reading);

                    reading.temperature_format = getTemperatureFormatFromTimeReading(bluetooth_read_buffer[FORA_COMMS_DATA_3]);

                    if (deviceReadingIndex + 1 == numberOfReadingsOnDevice)
                    {
                        Log.d(TAG, "Got all timings from Fora");

                        sendReadingsToGateway();

                        clearForaMemory();
                    }
                    else
                    {
                        getNextTimingFromDevice();
                    }
                }
            }
            break;
        }

        // If memory has just been cleared, switch off device
        // Improves usability because the end user would only have to switch off the Fora to take it out of "PCL" mode, and auto switch off will also save battery on Fora
        if (bluetooth_read_buffer[FORA_COMMS_ACK] == FORA_CMD_CLEAR_MEMORY)
        {
            Log.d(TAG, "Fora ACKd memory clear, switching off now to take Fora out of PCL mode...");

            if (foraState == ForaState.STATE_CLEARING_MEMORY_AFTER_INITIAL_CONNECTION)
            {
                setupComplete = true;  // time set, memory cleared so ready to take measurements from now
            }

            switchOffForaDevice();
        }

        if (bluetooth_read_buffer[FORA_COMMS_ACK] == FORA_READY_FOR_COMMANDS)
        {
            Log.d(TAG, "Fora device reports it is ready to receive commands");

            sendArbitraryCommand();

            foraState = ForaState.STATE_INITIAL_ARBITRARY_COMMAND_SENT;
        }
    }


    private void startGettingReadingsFromDevice()
    {
        deviceReadingIndex = 0;

        sendForaMessage(FORA_CMD_READ_STORAGE_RESULT_DATA_WITH_INDEX, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00);
    }


    private void startGettingTimingsFromDevice()
    {
        deviceReadingIndex = 0;

        sendForaMessage(FORA_CMD_READ_STORAGE_TIME_DATA_WITH_INDEX, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00);
    }


    private void getNextReadingFromDevice()
    {
        deviceReadingIndex++;

        sendForaMessage(FORA_CMD_READ_STORAGE_RESULT_DATA_WITH_INDEX, (byte)deviceReadingIndex,(byte)0, (byte)0, (byte)0);
    }


    private void getNextTimingFromDevice()
    {
        deviceReadingIndex++;

        sendForaMessage(FORA_CMD_READ_STORAGE_TIME_DATA_WITH_INDEX, (byte)deviceReadingIndex,(byte)0, (byte)0, (byte)0);
    }


    private void clearForaMemory()
    {
        Log.d(TAG, "clearForaMemory()");

        if (!debugDontClearMemory)
        {
            sendForaMessage(FORA_CMD_CLEAR_MEMORY);
        }
    }


    private void switchOffForaDevice()
    {
        Log.d(TAG, "switchOffForaDevice()");

        if (!debugLeaveForaOn)
        {
            sendForaMessage(FORA_CMD_TURN_OFF_DEVICE);

            foraState = ForaState.STATE_NONE;
        }
    }

    private boolean timesAreInTheSameMinute(long timestamp1, long timestamp2)
    {
        final Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal1.setTimeInMillis(timestamp1);
        int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
        int minute1 = cal1.get(Calendar.MINUTE);

        final Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal2.setTimeInMillis(timestamp2);
        int hour2 = cal2.get(Calendar.HOUR_OF_DAY);
        int minute2 = cal2.get(Calendar.MINUTE);

        // Also need to check that these aren't on different days, in case of comparing identical times on different days
        boolean different_day = false;
        long difference = Math.abs(timestamp1 - timestamp2);
        if (difference > DateUtils.HOUR_IN_MILLIS)
        {
            different_day = true;
        }

        if (hour1 == hour2 && minute1 == minute2 && !different_day)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    // Only add items to filtered list if the time does not appear in the minute_already_added_list
    private void filterOutAllMeasurementsForSameMinuteExceptTheMostRecent()
    {
        fora_readings_filtered.clear();

        ArrayList<MinuteOfForaDataAddedAlready> minute_already_added_list = new ArrayList<>();

        for (int i=0; i < fora_readings.size(); i++)
        {
            ForaReading this_reading = fora_readings.get(i);

            final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTimeInMillis(this_reading.timestamp);

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            MinuteOfForaDataAddedAlready this_minute = new MinuteOfForaDataAddedAlready();
            this_minute.hour = hour;
            this_minute.minute = minute;

            if (minute_already_added_list.contains(this_minute))
            {
                // forget about this measurement, it is not the latest for this minute
                Log.d(TAG, "Discarding a reading for " + hour + ":" + minute + " because it is not the latest reading for this minute " + this_reading.temperature_integer + "." + this_reading.temperature_fraction);
            }
            else
            {
                ForaReading filtered_reading = new ForaReading();
                filtered_reading.timestamp = this_reading.timestamp;
                filtered_reading.temperature_integer = this_reading.temperature_integer;
                filtered_reading.temperature_fraction = this_reading.temperature_fraction;

                fora_readings_filtered.add(filtered_reading);

                minute_already_added_list.add(this_minute);

                Log.d(TAG, "Most recent reading for this minute " + hour + ":" + minute + "  " + this_reading.temperature_integer + "." + this_reading.temperature_fraction);
            }
        }
    }


    private void sendReadingsToGateway()
    {
        Log.d(TAG, "sendReadingsToGateway()");

        long start_time = PatientGatewayService.device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__FORA_IR20).device_session_start_time;
        start_time = start_time - 2 * DateUtils.MINUTE_IN_MILLIS;

        filterOutAllMeasurementsForSameMinuteExceptTheMostRecent();

        for (int i=0; i < fora_readings_filtered.size(); i++)
        {
            ForaReading this_reading = fora_readings_filtered.get(i);

            int temperature_integer = this_reading.temperature_integer;
            int temperature_fraction = this_reading.temperature_fraction;
            long timestamp = this_reading.timestamp;

            if (timestamp > start_time && start_time != INVALID_DEVICE_SESSION_TIME)
            {
                Log.d(TAG, "Send Fora reading " + temperature_integer + "." + temperature_fraction + " " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp) +  " Fora added to session at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(start_time) + " setupcomplete? " + setupComplete);

                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                intent.putExtra(FORA_DATA_AS_STRING, (temperature_integer + "." + temperature_fraction) + "__" + timestamp);

                intent.putExtra(DATA_TYPE, Fora_Ir20.DATATYPE_FORA_TEMPERATURE_MEASUREMENT);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
            }
            else
            {
                Log.d(TAG, "NOT sending Fora reading " + temperature_integer + "." + temperature_fraction + " " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp) +  " Fora added to session at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(start_time) + " setupcomplete? " + setupComplete);
            }
        }

        fora_readings.clear();
        fora_readings_filtered.clear();
    }


    @Override
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


    private void reportConnected()
    {
        Log.d(TAG, "reportConnected()");

        Intent intent = new Intent(Fora_Ir20.FORA_IR20__CONNECTED);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }


    private void reportTemporaryDisconnection()
    {
        Log.d(TAG, "reportTemporaryDisconnection()");
    }


    public void reportDisconnected()
    {
        Log.d(TAG, "reportDisconnected()");

        Intent intent = new Intent(FORA_IR20__DISCONNECTED);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
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

