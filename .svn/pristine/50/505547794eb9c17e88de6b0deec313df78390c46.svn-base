package com.isansys.patientgateway.algorithms;

import android.text.format.DateUtils;

import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.TimestampConversion;

import java.util.ArrayList;

class EarlyWarningScoreMinute
{
    private class SimpleEws
    {
        public int value;
        public int maximum;

        public SimpleEws(int val, int max)
        {
            value = val;
            maximum = max;
        }
    }


    private final static int NOT_YET_SET = -1;
    private final SimpleEws not_set_ews;

    final long start_timestamp;
    final long end_timestamp;
    
    // Scores for each individual vital sign
    private final ArrayList<SimpleEws> measurement_ews = new ArrayList<>();

    private ArrayList<VitalSignType> expected_measurements;

    public int total_ews;
    public int max_possible_ews;
    
    public int special_alert_level;

    public EarlyWarningScoreMinute(long timestamp)
    {
        /* EarlyWarningScoreMinute is defined as the period of time where t > start_timestamp and <= end_timestamp.
         * So to set the start timestamp, subtract one from the value and round it down to a whole minute.
         * That way if the passed-in timestamp is a whole multiple of a minute, the EarlyWarningScoreMinute will cover the
         * real minute up to and including the timestamp.
         */
        start_timestamp = DateUtils.MINUTE_IN_MILLIS * ((timestamp - 1) / DateUtils.MINUTE_IN_MILLIS);    // round down to 1 minute intervals
        end_timestamp = start_timestamp + DateUtils.MINUTE_IN_MILLIS;

        not_set_ews = new SimpleEws(NOT_YET_SET, NOT_YET_SET);

        for (VitalSignType vital_sign_type : VitalSignType.values())
        {
            measurement_ews.add(not_set_ews);
        }

        expected_measurements = null;
        total_ews = 0;
        max_possible_ews = 0;

        special_alert_level = 0;
    }


    public void addEarlyWarningScore(VitalSignType vital_sign_type, int early_warning_score, int max_possible)
    {
        if(isEarlyWarningScoreInvalidForVitalSign(vital_sign_type))
        {
            measurement_ews.set(vital_sign_type.ordinal(), new SimpleEws(early_warning_score, max_possible));
        }
    }


    public void setExpectedMeasurements(ArrayList<VitalSignType> measurements)
    {
        expected_measurements = measurements;
    }


    private boolean isEarlyWarningScoreInvalidForVitalSign(VitalSignType vital_sign_type)
    {
        return (measurement_ews.get(vital_sign_type.ordinal()).value == NOT_YET_SET);
    }


    private boolean isEarlyWarningScoreValidForVitalSign(VitalSignType vital_sign_type)
    {
        return (measurement_ews.get(vital_sign_type.ordinal()).value != NOT_YET_SET);
    }
    

    @Override
    public String toString()
    {
        return "EarlyWarningScoreMinute{" + "start_timestamp=" + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(start_timestamp) + ", end_timestamp=" + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(end_timestamp) + ", measurement_ews=" + measurement_ews + '}';
    }


    private boolean allVitalsReceived()
    {
        if(expected_measurements != null)
        {
            for (VitalSignType v : expected_measurements)
            {
                if(isEarlyWarningScoreValidForVitalSign(v) == false)
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }

        return true;
    }


    public boolean calculateMinute()
    {
        if(allVitalsReceived())
        {
            for (VitalSignType type : expected_measurements)
            {
                SimpleEws ews = measurement_ews.get(type.ordinal());

                total_ews += ews.value;
                max_possible_ews += ews.maximum;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
