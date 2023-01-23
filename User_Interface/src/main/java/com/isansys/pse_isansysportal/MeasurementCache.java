package com.isansys.pse_isansysportal;


import com.isansys.common.measurements.MeasurementAnnotation;
import com.isansys.common.measurements.MeasurementBloodPressure;
import com.isansys.common.measurements.MeasurementCapillaryRefillTime;
import com.isansys.common.measurements.MeasurementConsciousnessLevel;
import com.isansys.common.measurements.MeasurementEarlyWarningScore;
import com.isansys.common.measurements.MeasurementHeartRate;
import com.isansys.common.measurements.MeasurementFamilyOrNurseConcern;
import com.isansys.common.measurements.MeasurementManuallyEnteredBloodPressure;
import com.isansys.common.measurements.MeasurementManuallyEnteredHeartRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredRespirationRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredSpO2;
import com.isansys.common.measurements.MeasurementManuallyEnteredTemperature;
import com.isansys.common.measurements.MeasurementUrineOutput;
import com.isansys.common.measurements.MeasurementManuallyEnteredWeight;
import com.isansys.common.measurements.MeasurementRespirationDistress;
import com.isansys.common.measurements.MeasurementRespirationRate;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.common.measurements.MeasurementSupplementalOxygenLevel;
import com.isansys.common.measurements.MeasurementTemperature;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.MeasurementWeight;
import com.isansys.common.measurements.VitalSignType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rory on 06/01/2017.
 *
 * Contains all the cached measurements and associated functions.
 *
 * Separates cached measurement functionality out from MainActivity, and allows independent testing.
 */

class MeasurementCache
{
    public final int max_data_points = 10080;

    private final ArrayList<MeasurementHeartRate> cached_heart_rate_measurements = new ArrayList<>(max_data_points);
    private final ArrayList<MeasurementManuallyEnteredHeartRate> cached_manually_entered_heart_rate_measurements = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementRespirationRate> cached_respiration_rate_measurements = new ArrayList<>(max_data_points);
    private final ArrayList<MeasurementManuallyEnteredRespirationRate> cached_manually_entered_respiration_rate_measurements = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementTemperature> cached_temperature_measurements = new ArrayList<>(max_data_points);
    private final ArrayList<MeasurementManuallyEnteredTemperature> cached_manually_entered_temperature_measurements = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementSpO2> cached_spo2_measurements = new ArrayList<>(max_data_points);
    private final ArrayList<MeasurementManuallyEnteredSpO2> cached_manually_entered_spo2_measurements = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementBloodPressure> cached_blood_pressure_measurements = new ArrayList<>(max_data_points);
    private final ArrayList<MeasurementManuallyEnteredBloodPressure> cached_manually_entered_blood_pressure_measurements = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementWeight> cached_weight_scale_measurements = new ArrayList<>(max_data_points);
    private final ArrayList<MeasurementManuallyEnteredWeight> cached_manually_entered_weight_measurements = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementEarlyWarningScore> cached_early_warning_scores = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementConsciousnessLevel> cached_manually_entered_consciousness_level_measurements = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementSupplementalOxygenLevel> cached_manually_entered_supplemental_oxygen_level_measurements = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementAnnotation> cached_manually_entered_annotations = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementCapillaryRefillTime> cached_manually_entered_capillary_refill_time = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementRespirationDistress> cached_manually_entered_respiration_distress = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementFamilyOrNurseConcern> cached_manually_entered_family_or_nurse_concern = new ArrayList<>(max_data_points);

    private final ArrayList<MeasurementUrineOutput> cached_manually_entered_urine_output = new ArrayList<>(max_data_points);

    public ArrayList<? extends MeasurementVitalSign> getListFromType(VitalSignType type)
    {
        switch(type)
        {
            case HEART_RATE:
                return cached_heart_rate_measurements;

            case RESPIRATION_RATE:
                return cached_respiration_rate_measurements;

            case TEMPERATURE:
                return cached_temperature_measurements;

            case SPO2:
                return cached_spo2_measurements;

            case BLOOD_PRESSURE:
                return cached_blood_pressure_measurements;

            case WEIGHT:
                return cached_weight_scale_measurements;

            case EARLY_WARNING_SCORE:
                return cached_early_warning_scores;

            case MANUALLY_ENTERED_HEART_RATE:
                return cached_manually_entered_heart_rate_measurements;

            case MANUALLY_ENTERED_RESPIRATION_RATE:
                return cached_manually_entered_respiration_rate_measurements;

            case MANUALLY_ENTERED_TEMPERATURE:
                return cached_manually_entered_temperature_measurements;

            case MANUALLY_ENTERED_SPO2:
                return cached_manually_entered_spo2_measurements;

            case MANUALLY_ENTERED_BLOOD_PRESSURE:
                return cached_manually_entered_blood_pressure_measurements;

            case MANUALLY_ENTERED_WEIGHT:
                return cached_manually_entered_weight_measurements;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                return cached_manually_entered_consciousness_level_measurements;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                return cached_manually_entered_supplemental_oxygen_level_measurements;

            case MANUALLY_ENTERED_ANNOTATION:
                return cached_manually_entered_annotations;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                return cached_manually_entered_capillary_refill_time;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                return cached_manually_entered_respiration_distress;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                return cached_manually_entered_family_or_nurse_concern;

            case MANUALLY_ENTERED_URINE_OUTPUT:
                return cached_manually_entered_urine_output;
        }

        return null;
    }


    public MeasurementVitalSign getLatestMeasurementByType(VitalSignType type)
    {
        ArrayList<? extends MeasurementVitalSign> return_list = getListFromType(type);

        if(return_list != null && return_list.size() > 0)
        {
            return return_list.get(return_list.size() - 1);
        }
        else
        {
            return null;
        }
    }


    public ArrayList<? extends MeasurementVitalSign> getCachedMeasurements(VitalSignType vital_sign)
    {
        return getListFromType(vital_sign);
    }


    public <T extends MeasurementVitalSign> void updateCachedVitalsList(VitalSignType type, T vital)
    {
        boolean measurement_already_added = false;

        ArrayList<T> list = (ArrayList<T>) getListFromType(type);

        if (list != null)
        {
            for (T measurement : list)
            {
                if(measurement.timestamp_in_ms == vital.timestamp_in_ms)
                {
                    measurement_already_added = true;
                    break;
                }
            }

            if(!measurement_already_added)
            {
                list.add(vital);

                sortCachedVitalsListInTimeOrder(list);

                if (list.size() > max_data_points)
                {
                    list.remove(0);
                }
            }
        }
    }


    public <T extends MeasurementVitalSign> void updateCachedVitalsListAndSortInTimeOrder(VitalSignType type, ArrayList<T> vitals_list)
    {
        // Remove duplicates (if any)
        // http://stackoverflow.com/questions/203984/how-do-i-remove-repeated-elements-from-arraylist
        ArrayList<T> cache_list = (ArrayList<T>) getListFromType(type);

        if (cache_list != null)
        {
            Set<T> hashSet = new HashSet<>();
            hashSet.addAll(cache_list);
            hashSet.addAll(vitals_list);
            cache_list.clear();
            cache_list.addAll(hashSet);

            // Sort this into time order
            sortCachedVitalsListInTimeOrder(cache_list);
        }
    }


    private void sortCachedVitalsListInTimeOrder(ArrayList<? extends MeasurementVitalSign> list)
    {
        if (list != null)
        {
            list.sort((Comparator<MeasurementVitalSign>) (lhs, rhs) -> Long.compare(lhs.timestamp_in_ms, rhs.timestamp_in_ms));
        }
    }


    public void clearAll()
    {
        cached_heart_rate_measurements.clear();
        cached_respiration_rate_measurements.clear();
        cached_temperature_measurements.clear();
        cached_spo2_measurements.clear();
        cached_blood_pressure_measurements.clear();
        cached_weight_scale_measurements.clear();

        cached_early_warning_scores.clear();

        cached_manually_entered_heart_rate_measurements.clear();
        cached_manually_entered_respiration_rate_measurements.clear();
        cached_manually_entered_temperature_measurements.clear();
        cached_manually_entered_spo2_measurements.clear();
        cached_manually_entered_blood_pressure_measurements.clear();
        cached_manually_entered_weight_measurements.clear();
        cached_manually_entered_consciousness_level_measurements.clear();
        cached_manually_entered_supplemental_oxygen_level_measurements.clear();
        cached_manually_entered_annotations.clear();
        cached_manually_entered_capillary_refill_time.clear();
        cached_manually_entered_respiration_distress.clear();
        cached_manually_entered_family_or_nurse_concern.clear();
        cached_manually_entered_urine_output.clear();
    }


    public int getCacheSize(VitalSignType vital_sign_type)
    {
        ArrayList<? extends MeasurementVitalSign> list = getListFromType(vital_sign_type);

        if (list == null)
        {
            return 0;
        }
        else
        {
            return list.size();
        }
    }


    public long getEarliestTimestampInCache()
    {
        ArrayList<Long> earliest_list = new ArrayList<>();

        if(cached_heart_rate_measurements.size() > 0)
        {
            earliest_list.add(cached_heart_rate_measurements.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_heart_rate_measurements.size() > 0)
        {
            earliest_list.add(cached_manually_entered_heart_rate_measurements.get(0).timestamp_in_ms);
        }

        if(cached_respiration_rate_measurements.size() > 0)
        {
            earliest_list.add(cached_respiration_rate_measurements.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_respiration_rate_measurements.size() > 0)
        {
            earliest_list.add(cached_manually_entered_respiration_rate_measurements.get(0).timestamp_in_ms);
        }

        if(cached_temperature_measurements.size() > 0)
        {
            earliest_list.add(cached_temperature_measurements.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_temperature_measurements.size() > 0)
        {
            earliest_list.add(cached_manually_entered_temperature_measurements.get(0).timestamp_in_ms);
        }

        if(cached_spo2_measurements.size() > 0)
        {
            earliest_list.add(cached_spo2_measurements.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_spo2_measurements.size() > 0)
        {
            earliest_list.add(cached_manually_entered_spo2_measurements.get(0).timestamp_in_ms);
        }

        if(cached_blood_pressure_measurements.size() > 0)
        {
            earliest_list.add(cached_blood_pressure_measurements.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_blood_pressure_measurements.size() > 0)
        {
            earliest_list.add(cached_manually_entered_blood_pressure_measurements.get(0).timestamp_in_ms);
        }

        if(cached_weight_scale_measurements.size() > 0)
        {
            earliest_list.add(cached_weight_scale_measurements.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_weight_measurements.size() > 0)
        {
            earliest_list.add(cached_manually_entered_weight_measurements.get(0).timestamp_in_ms);
        }

        if(cached_early_warning_scores.size() > 0)
        {
            earliest_list.add(cached_early_warning_scores.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_consciousness_level_measurements.size() > 0)
        {
            earliest_list.add(cached_manually_entered_consciousness_level_measurements.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_supplemental_oxygen_level_measurements.size() > 0)
        {
            earliest_list.add(cached_manually_entered_supplemental_oxygen_level_measurements.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_annotations.size() > 0)
        {
            earliest_list.add(cached_manually_entered_annotations.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_capillary_refill_time.size() > 0)
        {
            earliest_list.add(cached_manually_entered_capillary_refill_time.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_respiration_distress.size() > 0)
        {
            earliest_list.add(cached_manually_entered_respiration_distress.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_family_or_nurse_concern.size() > 0)
        {
            earliest_list.add(cached_manually_entered_family_or_nurse_concern.get(0).timestamp_in_ms);
        }

        if(cached_manually_entered_urine_output.size() > 0)
        {
            earliest_list.add(cached_manually_entered_urine_output.get(0).timestamp_in_ms);
        }

        if(earliest_list.size() > 0)
        {
            Collections.sort(earliest_list);

            return earliest_list.get(0);
        }
        else
        {
            return -1;
        }
    }


    public MeasurementManuallyEnteredBloodPressure getMatchingManuallyEnteredBloodPressureMeasurementFromTimestamp(long timestamp_in_ms)
    {
        for(MeasurementManuallyEnteredBloodPressure measurement : cached_manually_entered_blood_pressure_measurements)
        {
            if (measurement.timestamp_in_ms == timestamp_in_ms)
            {
                return measurement;
            }
        }

        return new MeasurementManuallyEnteredBloodPressure();
    }
}
