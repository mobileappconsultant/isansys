package com.isansys.patientgateway.tabletBattery;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.text.format.DateUtils;

import com.isansys.patientgateway.BatteryDataAndEventHandler;
import com.isansys.patientgateway.GenericStartStopTimer;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.Timer;
import java.util.TimerTask;

public class TabletBatteryInterpreter
{
    private final RemoteLoggingWithEnable Log;
    private final ContextInterface gateway_context_interface;
    private final String TAG = "TabletBatteryInterpreter";
    private Timer battery_information_timer;

    private final BatteryVoltageToPercentageTable mBatteryVoltageToPercentageTable;

    private final BatteryDataAndEventHandler battery_data_and_event_handler;

    private final PatientGatewayInterface patient_gateway_interface;


    /**
     * Intent for Battery information is sticky. That means don't need to register for the message. Information can be extracted at anytime.
     * follow the android website :- http://developer.android.com/training/monitoring-device-state/battery-monitoring.html
     * Get battery information every 10 sec
     */
    public TabletBatteryInterpreter(ContextInterface context_interface, RemoteLogging logger, BatteryDataAndEventHandler battery_data_and_event_handler, PatientGatewayInterface patient_gateway_interface, boolean enable_logs)
    {
        gateway_context_interface = context_interface;
        Log = new RemoteLoggingWithEnable(logger, enable_logs);
        this.patient_gateway_interface = patient_gateway_interface;

        mBatteryVoltageToPercentageTable = new BatteryVoltageToPercentageTable();

        this.battery_data_and_event_handler = battery_data_and_event_handler;

        // Intent for Battery information is sticky. That means don't need to register for the message. Information can be extracted at anytime.
        // follow the android website :- http://developer.android.com/training/monitoring-device-state/battery-monitoring.html
        // Get battery information every 10 sec
        GenericStartStopTimer.cancelTimer(battery_information_timer, Log.getLog());
        battery_information_timer = new Timer("battery_information_timer");
        battery_information_timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                getBatteryVoltage();
            }
        }, 0, 10 * DateUtils.SECOND_IN_MILLIS);
    }


    public static class GatewayBatteryInfo
    {
        public boolean isPresent = false;
        public int plugged = -1;
        public int scale = -1;
        public int health = 0;
        public int status = 0;
        public int raw_level = -1;
        public int voltage = 0;
        public float temperature = 0;
        public int current_avg = 0;
        public boolean charging = false;

        // This variable is different than actual battery estimated_percentage.
        public int estimated_percentage = -1;
        public int android_percentage = -1;
    }

    private final GatewayBatteryInfo gateway_battery_info = new GatewayBatteryInfo();


    @TargetApi(21)
    private int getAndroidAverageCurrent()
    {
        BatteryManager mBatteryManager = (BatteryManager)gateway_context_interface.getAppContext().getSystemService(Context.BATTERY_SERVICE);

        return (int)mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
    }


    /**
     * Can be called from two places at the same time 1) timer, 2) onDestroy(). So Synchronized is added
     */
    public synchronized void getBatteryVoltage()
    {
        // Intent for Battery information is sticky. That means don't need to register for the message. Information can be extracted at anytime.
        IntentFilter battery_intent = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent battery_status = gateway_context_interface.getAppContext().registerReceiver(null, battery_intent);

        try
        {
            gateway_battery_info.isPresent = battery_status.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
            gateway_battery_info.plugged = battery_status.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            gateway_battery_info.scale = battery_status.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            gateway_battery_info.health = battery_status.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            gateway_battery_info.status = battery_status.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            gateway_battery_info.raw_level = battery_status.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            gateway_battery_info.voltage = battery_status.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            gateway_battery_info.current_avg = getAndroidAverageCurrent();
            gateway_battery_info.temperature = battery_status.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

            int charge_type = battery_status.getIntExtra("charge_type", 0);

            if (gateway_battery_info.isPresent)
            {
                long time_stamp_battery_status = patient_gateway_interface.getNtpTimeNowInMilliseconds();

                if (gateway_battery_info.raw_level >= 0 && gateway_battery_info.scale > 0)
                {
                    gateway_battery_info.android_percentage = (gateway_battery_info.raw_level * 100) / gateway_battery_info.scale;
                }
                else
                {
                    gateway_battery_info.android_percentage = 0;
                }

                int estimated_battery_level;

                // Test to simulate all the battery level.
    //            if(test_voltage <= 3500)
    //            {
    //                test_voltage = 4300;
    //
    //                estimated_battery_level = tabletVoltageToBatteryLevelInterpreter(test_voltage,level);
    //            }
    //            else
    //            {
    //                test_voltage -= 10;
    //
    //                estimated_battery_level = tabletVoltageToBatteryLevelInterpreter(test_voltage,level);
    //            }

                estimated_battery_level = tabletVoltageToBatteryLevelInterpreter(gateway_battery_info.voltage,gateway_battery_info.android_percentage);

                battery_data_and_event_handler.analyseTabletBatteryStatus(gateway_battery_info.current_avg, charge_type, gateway_battery_info.plugged, gateway_battery_info.raw_level, time_stamp_battery_status);

                // Adding all the battery information together to log it in same line. Easier while debugging
                String info = "Estimated Battery Raw_Level: " + estimated_battery_level + "%,   ";
                info += ("Android Battery Raw_Level: " + gateway_battery_info.android_percentage + ",   ");
                info += ("Health: " + getHealthString(gateway_battery_info.health) + ",   ");
                info += ("online: " + battery_status.getIntExtra("online",-1)+ ",   ");
                info += ("Status: " + getStatusString(gateway_battery_info.status) + ",   ");
                info += ("Plugged: " + getPlugTypeString(gateway_battery_info.plugged) + ",   ");
                info += ("present: " + battery_status.getBooleanExtra("present", false) + ",   ");
                info += ("level: " + gateway_battery_info.raw_level+ ",   ");
                info += ("Scale: " + gateway_battery_info.scale + ",   ");
                info += ("temperature: " + gateway_battery_info.temperature + ",   ");
                info += ("Current: " + gateway_battery_info.current_avg + ",   ");
                info += ("Voltage: " + gateway_battery_info.voltage + ",   ");
                info += ("charge_type: " + battery_status.getIntExtra("charge_type",-1)+ ",   ");
                info += ("power_sharing " + battery_status.getBooleanExtra("power_sharing", false)+",   ");
                info += ("invalid_charger " + battery_status.getIntExtra("invalid_charger", -1)+",   ");
                Log.d(TAG, info);

                boolean charging = false;
                if (gateway_battery_info.status == BatteryManager.BATTERY_STATUS_CHARGING)
                {
                    charging = true;
                }

                // Temperature is something like 342 which translates to 34.2 degrees.
                // Convert it into 34.2
                float temperature_as_float = (gateway_battery_info.temperature / 10);

                gateway_battery_info.charging = charging;
                gateway_battery_info.estimated_percentage = estimated_battery_level;
                gateway_battery_info.temperature = temperature_as_float;

                patient_gateway_interface.reportTabletBatteryInfo(gateway_battery_info, battery_data_and_event_handler.tablet_charging_status);
            }
            else
            {
                Log.d(TAG, "Battery not present!!!");
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "getBatteryVoltage : Exception e = " + e.toString());
        }
    }


    private String getPlugTypeString(int plugged)
    {
        String plugType = "Unknown";

        switch (plugged)
        {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = "USB";
                break;
        }

        return plugType;
    }


    private String getHealthString(int health)
    {
        String healthString = "Unknown";

        switch (health)
        {
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthString = "Cold";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = "Over Heat";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = "Over Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                healthString = "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = "Failure";
                break;
        }

        return healthString;
    }


    private String getStatusString(int status)
    {
        String statusString = "Unknown";

        switch (status)
        {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "Discharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "Full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "Not Charging";
                break;
        }

        return statusString;
    }


    /**
     * Uses Map called voltage_to_percentage_map in BatteryVoltageToPercentageTable.java.
     * Tablet Battery voltage is used to get the closest pre-calculated voltage. Estimated voltage is then used to get the percentage
     * @param battery_voltage : Android battery voltage
     * @param battery_percentage : Android battery percentage. Only used for Log message
     * @return int : estimated android battery percentage based on voltage
     */
    private int tabletVoltageToBatteryLevelInterpreter(int battery_voltage, int battery_percentage)
    {
        int mBattery_Percentage;
        int mVoltage_to_percentage_map = 0;

        // Return the Map less than or equal to battery_voltage
        try
        {
            mVoltage_to_percentage_map =  mBatteryVoltageToPercentageTable.voltage_to_percentage_map.floorKey(battery_voltage);
        }
        catch (Exception e)
        {
            Log.e(TAG, "tabletVoltageToBatteryLevelInterpreter : Map for voltage isn't found(Not in voltage Range). Exception e = " + e.toString());
        }

        // Check if the array isn't null. If null, battery voltage is at lowest.
        if(mVoltage_to_percentage_map != 0)
        {
            // Get the battery percentage associated to mapping voltage
            mBattery_Percentage = mBatteryVoltageToPercentageTable.voltage_to_percentage_map.get(mVoltage_to_percentage_map);

            // Separate log line for easy debugging
            Log.d(TAG, "tabletVoltageToBatteryLevelInterpreter : Estimated battery percentage = " + mBattery_Percentage + ". Mapping voltage = " +mVoltage_to_percentage_map +
                        ". Actual Android Battery Percentage = "+ battery_percentage+" . Actual Battery Voltage = "+ battery_voltage);
        }
        else
        {
            // No Map with given Android voltage found
            mBattery_Percentage = battery_percentage;

            Log.d(TAG, "tabletVoltageToBatteryLevelInterpreter : ALERT ALERT!!! Android Battery voltage is low. Android Battery voltage = " + battery_voltage + " . Android Battery Percentage = " + battery_percentage);
        }

        return mBattery_Percentage;
    }


    public GatewayBatteryInfo getTabletBatteryInfo()
    {
        return gateway_battery_info;
    }

}
