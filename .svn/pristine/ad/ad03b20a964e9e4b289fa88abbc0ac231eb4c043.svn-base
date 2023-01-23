package com.isansys.common;

import com.isansys.common.enums.MeasurementTypes;

import java.util.ArrayList;

public class ThresholdSetAgeBlockDetail
{
    public int age_range_top;
    public int age_range_bottom;
    public String display_name;
    public byte[] image_binary;
    public boolean is_adult;
    public int servers_database_row_id;
    public int local_database_row_id;

    public final ArrayList<ThresholdSetColour> list_of_threshold_set_colours;
    public final ArrayList<ArrayList<ThresholdSetLevel>> list_of_threshold_set_levels_by_measurement_type;

    public ThresholdSetAgeBlockDetail()
    {
        list_of_threshold_set_colours = new ArrayList<>();
        list_of_threshold_set_levels_by_measurement_type = new ArrayList<>();
    }


    public ArrayList<ThresholdSetLevel> getThresholdSetLevelForMeasurementType(MeasurementTypes measurement_type)
    {
        ArrayList<ThresholdSetLevel> thresholdSetLevels = new ArrayList<>();

        for (ArrayList<ThresholdSetLevel> thresholdSetLevelsArrayList : list_of_threshold_set_levels_by_measurement_type)
        {
            for (ThresholdSetLevel thresholdSetLevel : thresholdSetLevelsArrayList)
            {
                if (thresholdSetLevel.measurement_type == measurement_type.ordinal())
                {
                    thresholdSetLevels.add(thresholdSetLevel);
                }
            }
        }

        return thresholdSetLevels;
    }
}
