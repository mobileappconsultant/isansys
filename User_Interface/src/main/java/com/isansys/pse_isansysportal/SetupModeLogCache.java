package com.isansys.pse_isansysportal;

import com.isansys.common.enums.SensorType;
import com.isansys.patientgateway.deviceInfo.SetupModeLog;

import java.util.ArrayList;
import java.util.Comparator;


class SetupModeLogCache
{
    private final ArrayList<SetupModeLog> cached_lifetouch_setup_mode_logs = new ArrayList<>();

    private final ArrayList<SetupModeLog> cached_spo2_setup_mode_logs = new ArrayList<>();


    private ArrayList<SetupModeLog> getListFromDeviceType(SensorType sensor_type)
    {
        switch(sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
                return cached_lifetouch_setup_mode_logs;

            case SENSOR_TYPE__SPO2:
                return cached_spo2_setup_mode_logs;
        }

        return null;
    }


    public ArrayList<SetupModeLog> getCachedMeasurements(SensorType sensor_type)
    {
        return getListFromDeviceType(sensor_type);
    }


    public void updateCachedVitalsListAndSortInTimeOrder(SensorType sensor_type, ArrayList<SetupModeLog> new_item_list)
    {
        ArrayList<SetupModeLog> cache_list = getListFromDeviceType(sensor_type);

        if (cache_list != null)
        {
            for (SetupModeLog new_log_entry : new_item_list)
            {
                if (new_log_entry.end_time != -1)
                {
                    cache_list.add(new_log_entry);
                }
            }

            // Sort this into time order
            sortCachedVitalsListInTimeOrder(cache_list);
        }
    }


    private void sortCachedVitalsListInTimeOrder(ArrayList<? extends SetupModeLog> list)
    {
        list.sort((Comparator<SetupModeLog>) (lhs, rhs) -> Long.compare(lhs.start_time, rhs.start_time));
    }


    public void clearAll()
    {
        cached_lifetouch_setup_mode_logs.clear();
        cached_spo2_setup_mode_logs.clear();
    }
}
