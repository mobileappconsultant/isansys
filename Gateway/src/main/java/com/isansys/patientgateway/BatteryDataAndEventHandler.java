package com.isansys.patientgateway;

import java.util.ArrayList;
import java.util.List;

import android.text.format.DateUtils;
import android.util.LongSparseArray;

import com.isansys.patientgateway.remotelogging.RemoteLogging;


/**
 *  Class to detect if tablet has changed the charging status. 
 * @author Sushant : Isansys
 * @ NOTE : WAMP is used to send tablet charging status
 */
public class BatteryDataAndEventHandler
{
    private final RemoteLoggingWithEnable Log;
    
    // Log Tag Name
    private final String TAG = "BatteryDataAndEventHandler";
    
    // ArrayList to hold the average current from battery Status Intent
    private LongSparseArray<Integer> array_list__tablet_average_current = new LongSparseArray<>();
    
    // ArrayList to hold the charge_type from battery Status intent
    private LongSparseArray<Integer> array_list__tablet_charge_type = new LongSparseArray<>();
    
    // Number of hours to store the data
    private final int NUMBER_OF_HOURS_TO_RECORDED_SAMPLE = 8;
    
    // Number of hours to analyse data
    private final int NUMBER_OF_HOURS_TO_ANALYSE_SAMPLE = 6;
    
    // This constant is used to delete oldest one hour data from hash map
    private final int SAMPLE_NUMBER_IN_ONE_HOUR = 60 * 6;
    
    // Battery status is read every 10 seconds. So every minute 6 samples.
    private final int SAMPLE_NUMBER_IN_ONE_MINUTE = 6;
    
    // total number of unplugged event before sending ALERT message (current setting 10 minutes)
    private final int NUMBER_OF_CHARGER_UNPLUGGED_EVENTS = 10 * SAMPLE_NUMBER_IN_ONE_MINUTE;
    
    // Limit the size of array list. only put one day worth of data. 
    // After this data is placed on the top array index. Sorting is not necessary because hash map handles it.
    private final int ARRAY_SIZE_LIMIT = NUMBER_OF_HOURS_TO_RECORDED_SAMPLE * SAMPLE_NUMBER_IN_ONE_HOUR;
    
    // Counts the number of unplugged event happened. Send alert if greater than NUMBER_OF_CHARGER_UNPLUGGED_EVENT
    private int tablet_charger_unplugged_event_counter = 0;
        
    // Counts the number of times 50% less battery happened. Send alert if greater than BATTERY_PERCENTAGE_LESS_THAN_50_DEBOUNCE_COUNT
    private int tablet_battery_percentage_50_or_less_counter = 0;
    
    // Counter to send the charging state every minute. This reduces processing.
    private int count_for_sending_long_period_charge_state = 0;


    private final PatientGatewayInterface patient_gateway_interface;
    
    
/* 
        WebPage battery status
        
        Red   :-   no-ping
        
        Green :- ping received AND Timer switch event happened with in 6 hours (Means Charging cycle) and charger is plugged for last 10 mins.
        
        Amber :- Tablet hasn't charged for more than 6 hours or charger is unplugged for more than 10 minutes.
*/    
    
    /**
     * Struct for battery charging status. Variables are set after the analysis of battery reading
     * @author Sushant : Isansys
     * @ plugged : boolean = set to False if charger is unplugged. This variable is updated to "False" every battery reading if tablet is unplugged.
     * @ plugged_and_charging : boolean = set to True if Tablet is putting charge in. This variable is updated on every battery reading.
     * @ battery_below_50_percent : boolean = set to True if tablet battery percentage is less than 50. This variable is updated every battery reading to True is battery level is less than 50
     * @ tablet_charged_within_period : boolean = set to False if tablet isn't charged for specified period of time (6 hours)
     * @ last_charged_time : long = time stamp in ms for last time charged. "0" if not charged with in 6 hours. 
     * @ valid_updated_tablet_charge_status : boolean = True if the check for last charge time is done. This is updated every five minutes.
     * @ NOTE : tablet_charged_within_period and last_charged_time is updated once every 5 minutes. During other time, Invalid timestamp = "1" is send via WAMP.
     * @ NOTE : if valid_updated_tablet_charge_status = "false" then ignore tablet_charged_within_period and last_charged_time variable.
     */
    public static class TabletBatteryChargingStatus
    {
        public boolean charger_unplugged = true;
        public boolean charger_unplugged_long_term = true;
        public boolean plugged_and_charging = false;
        public boolean battery_below_50_percent = false;
        
        public boolean valid_updated_tablet_charge_status = false;
        public boolean tablet_charged_within_period = true;
        public long last_charged_time = 0;
    }
    
    // Set the tablet_charging_status in "analyseTabletBatteryStatus" function. WAMP command is send after this function call
    public final TabletBatteryChargingStatus tablet_charging_status = new TabletBatteryChargingStatus();
    
    
    /**
     * Initialise the BatteryDataAndEventHandler before using BatteryDataAndEventHandler
     * @param patient_gateway_interface
     */
    public BatteryDataAndEventHandler(RemoteLogging logger, PatientGatewayInterface patient_gateway_interface, boolean enable_logs)
    {
        Log = new RemoteLoggingWithEnable(logger, enable_logs);
        this.patient_gateway_interface = patient_gateway_interface;

        // Remove all the added entries for average_current
        emptyArrayBatteryAverageCurrent();
        array_list__tablet_average_current = new LongSparseArray<>();
        
        // Remove all the added entries for charge_type
        emptyArrayBatteryChargeType();
        array_list__tablet_charge_type = new LongSparseArray<>();
        
        // test code to check the array list
        //addBatteryInformation(NUMBER_OF_HOURS_TO_RECORDED_SAMPLE);
    }
    
    
    /**
     *  Function to add the Tablet's battery Average Current in array_list__tablet_average_current arrayList
     * @param average_current : int = average_current  "Discharge is negative and Charging is positive"
     * @param current_time_in_ms : long = current time in Ms  
     */
    private void addTabletBatteryAverageCurrent(long current_time_in_ms, int average_current)
    {
        if(array_list__tablet_average_current != null)
        {
            // check if the size of array_list__tablet_average_current is more than one day
            if(array_list__tablet_average_current.size() >= ARRAY_SIZE_LIMIT )
            {
                // Delete oldest hour of data.
                deleteOldestAverageCurrentData(1);
            }
            
            // Add average current to array list
            Log.d(TAG, "addTabletBatteryAverageCurrent : array_list__tablet_average_current size = " + array_list__tablet_average_current.size() +". Adding the in_average_current = " + average_current + ". Time = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(current_time_in_ms));
            array_list__tablet_average_current.put(current_time_in_ms, average_current);
        }
        else
        {
            Log.e(TAG, "addTabletAverageCurrent : array_list__tablet_average_current == null");
        }
    }
    
    
    /**
     *  Charge_type is the actual indication of current coming in the tablet.
     *  NOTE :=  Even if the charger is plugged in, current consumption is negative. So charger plugged status doesn't indicate actual charging.
     *  NOTE := if Charge_type is '0' for long time than tablet plugged state must be reset. One way of doing it is re-plugging the charging cable.
     *  NOTE := Combining Charge_type and current_avg information, we can correctly predict the actual current in and prevent the unexpected shutdown
     * @param current_time_in_ms
     * @param charge_type
     */
    private void addTabletChargeType(long current_time_in_ms, int charge_type)
    {
        if(array_list__tablet_charge_type != null)
        {
            // Check if the size of array_list__tablet_charge_type is more than one day
            if(array_list__tablet_charge_type.size() >= ARRAY_SIZE_LIMIT )
            {
                // Delete oldest hour of data.
                deleteOldestChargeTypeData(1);
            }
            
            Log.d(TAG, "addTabletChargeType : array_list__tablet_average_current size = " + array_list__tablet_charge_type.size() +". adding the tablet charge_type = " + charge_type + ". Time = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(current_time_in_ms));
            array_list__tablet_charge_type.put(current_time_in_ms, charge_type);
        }
        else
        {
            Log.e(TAG, "addTabletChargeType : array_list__tablet_charge_type == null");
        }
    }
    
    
    /**
     * Function to Empty the array_list__tablet_average_current
     */
    private void emptyArrayBatteryAverageCurrent()
    {
        if(array_list__tablet_average_current != null)
        {
            Log.d(TAG, "emptyArrayBatteryAverageCurrent : removing all the entries in the array list average current");
            array_list__tablet_average_current.clear();
        }
        else
        {
            Log.e(TAG, "emptyArrayBatteryAverageCurrent : array list tablet average current is null");
        }
    }
    
    
    /**
     * Function to Empty the array_list__tablet_average_current
     */
    private void emptyArrayBatteryChargeType()
    {
        if(array_list__tablet_average_current != null)
        {
            Log.d(TAG, "emptyArrayBatteryChargeType : removing all the entries in the array list charge_type");
            array_list__tablet_charge_type.clear();
        }
        else
        {
            Log.e(TAG, "emptyArrayBatteryChargeType : array list tablet charge_type is null");
        }
    }
    
    
    /**
     * Function to analyse the recent battery reading.
     * @param average_current : int = tablet average current in (can be positive or negative)
     * @param charger_charge_type : int = 1 if tablet's average current is positive
     * @param plugged_status : int = 1 if tablet plugged to the charger
     * @param battery_level : int = raw battery level  
     * @param time_in_ms : long = current time in ms
     */
    public void analyseTabletBatteryStatus(int average_current, int charger_charge_type, int plugged_status, int battery_level, long time_in_ms)
    {
        addTabletBatteryAverageCurrent(time_in_ms, average_current);
        addTabletChargeType(time_in_ms, charger_charge_type);
        

        if(average_current > 0)
        {
            // Send WAMP when was the last time tablet was charged
            if (tablet_charging_status.last_charged_time < time_in_ms)
            {
                tablet_charging_status.last_charged_time = time_in_ms;
            }
        }

        // Updated with every battery voltage readings
        tablet_charging_status.charger_unplugged = (plugged_status == 0);
        tablet_charging_status.charger_unplugged_long_term = isTabletLongTermUnplugged(plugged_status);
        tablet_charging_status.battery_below_50_percent = isTabletBatteryBelowFiftyPercent(battery_level);
        
        Log.d(TAG, "analyseTabletBatteryStatus : time = " +  TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(time_in_ms) + ". Charger plug status = " + plugged_status + ". Battery Percentage = " + battery_level);
        
        if(charger_charge_type == 1)
        {
            // Tablet is charging state. Check if enough current is coming through socket
            if(average_current > 0)
            {
                // Charge is being added to the tablet
                // Send charging command
                tablet_charging_status.plugged_and_charging = true;
            }
            else
            {
                // Not enough current is coming through to current
                // Send Alert to message
                tablet_charging_status.plugged_and_charging = false;
            }
        }
        else
        {
            // Tablet is discharging
            if(average_current < 0)
            {
                tablet_charging_status.plugged_and_charging = false;
            }
            else
            {
                // Charger isn't plugged-in but tablet is adding charge(current)
            }
        }
        
        // Only update this variable every 5 minute to avoid multiple message ALERT
        // check last six hours sample and send alert
        // TODO Change the NUMBER_OF_HOURS_TO_RECORDED_SAMPLE to 6 from 24
        if(count_for_sending_long_period_charge_state >= 5 * SAMPLE_NUMBER_IN_ONE_MINUTE)
        {
            tablet_charging_status.valid_updated_tablet_charge_status = false;
            
            // Reset the variables
            tablet_charging_status.tablet_charged_within_period = false;
            
            if((array_list__tablet_average_current.size() >= (NUMBER_OF_HOURS_TO_ANALYSE_SAMPLE * SAMPLE_NUMBER_IN_ONE_HOUR)) && (array_list__tablet_charge_type.size() >= (NUMBER_OF_HOURS_TO_ANALYSE_SAMPLE * SAMPLE_NUMBER_IN_ONE_HOUR)))
            {
                // tablet_charging_status.tablet_charged_within_period and tablet_charging_status.last_charged_status variables are set in this function
                getChargerStatus(NUMBER_OF_HOURS_TO_ANALYSE_SAMPLE);
                // Now we have a valid value, we can set the valid flag to true.
            	tablet_charging_status.valid_updated_tablet_charge_status = true;
            }
            else
            {
                Log.w(TAG, "analyseTabletBatteryStatus : array_list__tablet_average_current && array_list__tablet_charge_type size is not enough for 6 hour battery analysis");
            }
            
            count_for_sending_long_period_charge_state = 0;
        }
        else
        {
            tablet_charging_status.valid_updated_tablet_charge_status = false;
            
            // If 6 samples hasn't passed yet then just send the default value via WAMP
            tablet_charging_status.tablet_charged_within_period = false;
            count_for_sending_long_period_charge_state++;
        }
    }
    
    /**
     * Get charging status of tablet for given number of hours 
     *  NOTE :=  Even if the charger is plugged in, current consumption is negative. So charger plugged status doesn't indicate actual charging.
     *  NOTE := if Charge_type is '0' for long time than tablet plugged state must be reset. One way of doing it is re-plugging the charging cable.
     *  NOTE := Combining Charge_type and current_avg information, we can correctly predict average current_in and prevent the unexpected shutdown 
     * @param numberOfHours : int number of hours
     */
    private void getChargerStatus(int numberOfHours)
    {
        int count_charge_type_1_in_specified_period = 0;
        int count_charge_type_0_in_specified_period = 0;
        
        int count_average_current_added_to_tablet = 0;                                  // Number of samples in which tablet was actually charging
        int count_average_current_subtracted_from_tablet = 0;                           // Number of samples in which tablet was actually discharging
        
        int total_average_current_in = 0;
        
        long current_time_in_ms = patient_gateway_interface.getNtpTimeNowInMilliseconds();
        long requested_number_of_hour_in_ms = numberOfHours * DateUtils.HOUR_IN_MILLIS;
        
        // Start time for analysing tablet battery charge_type. From this time onward till now, charge_type is counted.
        // If charge_type count is less than Specified number than SEND ALERT!!!!
        long start_time_of_battery_monitor = current_time_in_ms - requested_number_of_hour_in_ms;
        
        // Get the size of array list and go through all of them.
        int array_length = array_list__tablet_charge_type.size();
        Log.i(TAG, "getChargerStatus : array_list__tablet_charge_type size = " + array_length);
        for(int i=0; i<array_length; i++)
        {
            long this_time_in_ms = array_list__tablet_charge_type.keyAt(i);
            
            if(this_time_in_ms >= start_time_of_battery_monitor)
            {
                int this_charge_type = array_list__tablet_charge_type.get(this_time_in_ms);
                if(this_charge_type == 1)
                {
                    count_charge_type_1_in_specified_period++;
//                    Log.i(TAG, "getChargerStatus : Charge type = " + this_charge_type + " . Time stamp = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(this_time_in_ms));
                }
                else
                {
                    count_charge_type_0_in_specified_period++;
//                    Log.w(TAG, "getChargerStatus : Charge type = " + this_charge_type + " . Time stamp = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(this_time_in_ms));
                }
                
                
                // If the average current is positive then charge (current) is actually added to the tablet. If this is less than 24 samples (4 minutes test_value) than send alert  
                int average_current_in = array_list__tablet_average_current.get(this_time_in_ms);
                
                if(average_current_in > 0)
                {
                    count_average_current_added_to_tablet++; 
                }
                else
                {
                    count_average_current_subtracted_from_tablet++;
                }
                
                // If charge type is 1, positive current_avg is expected.
                total_average_current_in = total_average_current_in + average_current_in;
            }
        }
        
        // One hour has 360 samples. 
        Log.d(TAG, "getChargerStatus : number of 'charge_type = 1' for given interval of time = " + count_charge_type_1_in_specified_period + ". Charge_type ZERO =  "+ count_charge_type_0_in_specified_period);
        Log.d(TAG, "getChargerStatus : number of count_average_current_added_to_tablet = " + count_average_current_added_to_tablet + ".  Number of count_average_current_subtracted_tablet = " + count_average_current_subtracted_from_tablet);
        
        // Report for 6 hours
        // Check if tablet is charged for at least 5 minutes in 6 hours
        int least_number_of_tablet_charge_sample = 5 * SAMPLE_NUMBER_IN_ONE_MINUTE;
        if(count_average_current_added_to_tablet < least_number_of_tablet_charge_sample)
        {
            Log.e(TAG, "getChargerStatus : ALERT ALERT ALERT !!!!! Tablet Hasn't been charged for last 6 hours");
            
            // Send Alert via WAMP
            tablet_charging_status.tablet_charged_within_period = false;
        }
        else
        {
            Log.d(TAG, "Number of samples where average current to Tablet is positive = " + count_average_current_added_to_tablet + ".  Last Charging Event = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(tablet_charging_status.last_charged_time));
            
            tablet_charging_status.tablet_charged_within_period = true;
        }
    }
    

    /**
     *  Send Gateway WAMP report for tablet plugged in status (if charger_status = 0 send the WAMP report)
     * @param plugged_status : int = 0 if charger is not plugged
     */
    private boolean isTabletLongTermUnplugged(int plugged_status)
    {
        boolean is_charger_unplugged = false;

        Log.d(TAG, "isTabletLongTermUnplugged : tablet_charger_unplugged_event_counter = " + tablet_charger_unplugged_event_counter);
        
        // Reset the counter if plugged status is non-zero
        if(plugged_status != 0)
        {
            tablet_charger_unplugged_event_counter = 0;
            is_charger_unplugged = false;
        }
        else
        {
            // Plugged status is zero so tablet is on battery power
            if(tablet_charger_unplugged_event_counter >= NUMBER_OF_CHARGER_UNPLUGGED_EVENTS)
            {
                Log.e(TAG, "isTabletLongTermUnplugged : Charger unplugged. Sending ALERT Message !!!!!!!!!!!!!!!");

                // Send to UI to trigger unplugged overlay
                is_charger_unplugged = true;
            }
            else
            {
                tablet_charger_unplugged_event_counter++;
            }
        }
        
        return is_charger_unplugged;
    } 
    
    
    /**
     * Send Gateway WAMP report for battery percentage less than 50% every sample
     * @param battery_percentage : int battery level
     */
    private boolean isTabletBatteryBelowFiftyPercent(int battery_percentage)
    {
        boolean is_battery_level_less_than_50_percentage = false;
        
        if(battery_percentage <= 50)
        {
            // Battery percentage is greater than 50 reset the counter
            if(tablet_battery_percentage_50_or_less_counter > NUMBER_OF_CHARGER_UNPLUGGED_EVENTS)
            {
                Log.e(TAG, "isTabletBatteryBelowFiftyPercent : Battery Percentage is less than 50%");
                // Send ALERT via WAMP
                is_battery_level_less_than_50_percentage = true;
            }
            else
            {
                tablet_battery_percentage_50_or_less_counter++;
            }
        }
        else
        {
            // Battery percentage is greater than 50 reset the counter

            is_battery_level_less_than_50_percentage = false;

            tablet_battery_percentage_50_or_less_counter = 0;
        }
        
        return is_battery_level_less_than_50_percentage;
    } 
    
    
    /**
     * Function to Remove the oldest data from array list of tablet average current.
     * @param numberOfHours : int = hours of data to be removed
     */
    private void deleteOldestAverageCurrentData(int numberOfHours)
    {
        int counter_number_of_removed_sample = 0;
        
        // Start and end time to remove data is like window filter(Band Pass). if the sample falls in that period than remove it
        long current_time_in_millisecond = patient_gateway_interface.getNtpTimeNowInMilliseconds();
        long start_time_to_remove_oldest_data = (current_time_in_millisecond - (NUMBER_OF_HOURS_TO_RECORDED_SAMPLE * DateUtils.HOUR_IN_MILLIS));
        
        long end_time_to_remove_oldest_data = start_time_to_remove_oldest_data + (numberOfHours * DateUtils.HOUR_IN_MILLIS);
        
        if(array_list__tablet_average_current != null)
        {
            // Go through the array list and remove the data based on timestamp.
            // Increase counter and log it. For debugging and validation purpose 
            int array_size = array_list__tablet_average_current.size();
            
            List<Long> remove_time_stamp_array_list = new ArrayList<>();
            
            Log.w(TAG, "deleteOldestAverageCurrentData : array_list__tablet_average_current size = " + array_size);
            
            for(int i=0;i<array_size;i++)
            {
                long this_time_in_ms = array_list__tablet_average_current.keyAt(i);
                
                if((this_time_in_ms >= start_time_to_remove_oldest_data) && (this_time_in_ms <= end_time_to_remove_oldest_data))
                {
                    remove_time_stamp_array_list.add(this_time_in_ms);
                }
            }
            
            for(long key_to_remove : remove_time_stamp_array_list)
            {
                array_list__tablet_average_current.remove(key_to_remove);
                counter_number_of_removed_sample++;
            }
           
        }
        else
        {
            Log.e(TAG, "deleteOldestAverageCurrentData : array_list__tablet_average_current is not initialized");
        }
        
        Log.i(TAG, "deleteOldestAverageCurrentData : number of removed data from " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(start_time_to_remove_oldest_data) 
                    + " to "+ TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(end_time_to_remove_oldest_data) + " is =" + counter_number_of_removed_sample);
    }
    
    
    /**
     * Function to remove the oldest charge type data 
     * @param numberOfHours : int = hours of data to be removed 
     */
    private void deleteOldestChargeTypeData(int numberOfHours)
    {
        int counter_number_of_removed_sample = 0;
        
        // Start and end time to remove data is like window filter(Band Pass). if the sample falls in that period than remove it
        long current_time_in_millisecond = patient_gateway_interface.getNtpTimeNowInMilliseconds();
        long start_time_to_remove_oldest_data = (current_time_in_millisecond - (NUMBER_OF_HOURS_TO_RECORDED_SAMPLE * DateUtils.HOUR_IN_MILLIS));
        
        long end_time_to_remove_oldest_data = start_time_to_remove_oldest_data + (numberOfHours * DateUtils.HOUR_IN_MILLIS);
        
        if(array_list__tablet_charge_type != null)
        {
            // Go through the array list and remove the data based on timestamp.
            // Increase counter and log it. For debugging and validation purpose 
            int array_size = array_list__tablet_charge_type.size();
            
            List<Long> remove_time_stamp_array_list = new ArrayList<>();
            
            Log.w(TAG, "deleteOldestChargeTypeData : array_list__tablet_charge_type size = " + array_size);

            for(int i=0; i<array_size;i++)
            {
                long this_time_in_ms = array_list__tablet_charge_type.keyAt(i);
                
                if((this_time_in_ms >= start_time_to_remove_oldest_data) && (this_time_in_ms <= end_time_to_remove_oldest_data))
                {
                    remove_time_stamp_array_list.add(this_time_in_ms);
                }
            }
            
            for(long key_to_remove : remove_time_stamp_array_list)
            {
                array_list__tablet_charge_type.remove(key_to_remove);
                counter_number_of_removed_sample++;
            }
        }
        else
        {
            Log.e(TAG, "deleteOldestChargeTypeData : array_list__tablet_charge_type NOT initialized");
        }
        
        Log.i(TAG, "deleteOldestAverageCurrentData : number of removed data from " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(start_time_to_remove_oldest_data) 
                + " to "+ TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(end_time_to_remove_oldest_data) + " is =" + counter_number_of_removed_sample);
    }

    /**
     * Function to reset the "tablet_charger_unplugged_event_counter" counter
     */
    public void resetUnpluggedEventCounter()
    {
        Log.d(TAG, "resetUnpluggedEventCounter : Current tablet_charger_unplugged_event_counter " + tablet_charger_unplugged_event_counter);

        tablet_charger_unplugged_event_counter = 0;
    }
    
//    /**
//     * Test function to populate the array_list__tablet_average_current and array list_tablet_charger_status
//     * @param numberOfHours : int = number of hours data to be filled
//     */
//    private static void addBatteryInformation(int numberOfHours)
//    {
//        long current_time_in_ms = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();
//        long start_time = current_time_in_ms - (numberOfHours * (int)DateUtils.HOUR_IN_MILLIS);
//        
//        int average_current = -8000;
//        int charger_status = 0;
//        
//        int array_size = numberOfHours * SAMPLE_NUMBER_IN_ONE_HOUR;
//        long array list_start_time = start_time;
//        
//        for(int i=0; i<array_size; i++)
//        {
//            array_list__tablet_average_current.put(array list_start_time, average_current);
//            array_list__tablet_charge_type.put(array list_start_time, charger_status);
//            
//            array list_start_time = array list_start_time + 10 * 1000;
//        }
//        
//    }
}
