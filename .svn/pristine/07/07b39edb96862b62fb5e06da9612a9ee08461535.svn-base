package com.isansys.pse_isansysportal;

import android.text.format.DateUtils;

import com.isansys.common.ErrorCodes;
import com.isansys.common.measurements.MeasurementBloodPressure;
import com.isansys.common.measurements.MeasurementCapillaryRefillTime;
import com.isansys.common.measurements.MeasurementConsciousnessLevel;
import com.isansys.common.measurements.MeasurementEarlyWarningScore;
import com.isansys.common.measurements.MeasurementFamilyOrNurseConcern;
import com.isansys.common.measurements.MeasurementHeartRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredBloodPressure;
import com.isansys.common.measurements.MeasurementManuallyEnteredHeartRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredRespirationRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredSpO2;
import com.isansys.common.measurements.MeasurementManuallyEnteredTemperature;
import com.isansys.common.measurements.MeasurementManuallyEnteredWeight;
import com.isansys.common.measurements.MeasurementRespirationDistress;
import com.isansys.common.measurements.MeasurementRespirationRate;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.common.measurements.MeasurementSupplementalOxygenLevel;
import com.isansys.common.measurements.MeasurementTemperature;
import com.isansys.common.measurements.MeasurementUrineOutput;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.MeasurementWeight;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.remotelogging.RemoteLogging;


class SavedMeasurements
{
    private final String TAG = "SavedMeasurements";
    private final RemoteLogging Log;

    private MeasurementHeartRate saved_measurement_heart_rate = new MeasurementHeartRate();
    private MeasurementRespirationRate saved_measurement_respiration_rate = new MeasurementRespirationRate();
    private MeasurementTemperature saved_measurement_temperature = new MeasurementTemperature();
    private MeasurementSpO2 saved_measurement_spo2 = new MeasurementSpO2();
    private MeasurementBloodPressure saved_measurement_blood_pressure = new MeasurementBloodPressure();
    private MeasurementWeight saved_measurement_weight = new MeasurementWeight();
    private MeasurementEarlyWarningScore saved_measurement_early_warning_score = new MeasurementEarlyWarningScore();
    private MeasurementManuallyEnteredHeartRate saved_measurement_manually_entered_heart_rate = new MeasurementManuallyEnteredHeartRate();
    private MeasurementManuallyEnteredRespirationRate saved_measurement_manually_entered_respiration_rate = new MeasurementManuallyEnteredRespirationRate();
    private MeasurementManuallyEnteredTemperature saved_measurement_manually_entered_temperature = new MeasurementManuallyEnteredTemperature();
    private MeasurementManuallyEnteredSpO2 saved_measurement_manually_entered_spo2 = new MeasurementManuallyEnteredSpO2();
    private MeasurementManuallyEnteredBloodPressure saved_measurement_manually_entered_blood_pressure = new MeasurementManuallyEnteredBloodPressure();
    private MeasurementManuallyEnteredWeight saved_measurement_manually_entered_weight = new MeasurementManuallyEnteredWeight();
    private MeasurementSupplementalOxygenLevel saved_measurement_manually_entered_supplemental_oxygen = new MeasurementSupplementalOxygenLevel();
    private MeasurementConsciousnessLevel saved_measurement_manually_entered_consciousness_level = new MeasurementConsciousnessLevel();
    private MeasurementCapillaryRefillTime saved_measurement_manually_entered_capillary_refill_time = new MeasurementCapillaryRefillTime();
    private MeasurementRespirationDistress saved_measurement_manually_entered_respiration_distress = new MeasurementRespirationDistress();
    private MeasurementFamilyOrNurseConcern saved_measurement_manually_entered_nurse_or_family_concern = new MeasurementFamilyOrNurseConcern();
    private MeasurementUrineOutput saved_measurement_manually_entered_urine_output = new MeasurementUrineOutput();

    public SavedMeasurements(RemoteLogging logger)
    {
        this.Log = logger;
    }


    public void reset()
    {
        saved_measurement_heart_rate = new MeasurementHeartRate();
        saved_measurement_respiration_rate = new MeasurementRespirationRate();
        saved_measurement_temperature = new MeasurementTemperature();
        saved_measurement_spo2 = new MeasurementSpO2();
        saved_measurement_blood_pressure = new MeasurementBloodPressure();
        saved_measurement_weight = new MeasurementWeight();
        saved_measurement_early_warning_score = new MeasurementEarlyWarningScore();
        saved_measurement_manually_entered_heart_rate = new MeasurementManuallyEnteredHeartRate();
        saved_measurement_manually_entered_respiration_rate = new MeasurementManuallyEnteredRespirationRate();
        saved_measurement_manually_entered_temperature = new MeasurementManuallyEnteredTemperature();
        saved_measurement_manually_entered_spo2 = new MeasurementManuallyEnteredSpO2();
        saved_measurement_manually_entered_blood_pressure = new MeasurementManuallyEnteredBloodPressure();
        saved_measurement_manually_entered_weight = new MeasurementManuallyEnteredWeight();
        saved_measurement_manually_entered_consciousness_level = new MeasurementConsciousnessLevel();
        saved_measurement_manually_entered_supplemental_oxygen = new MeasurementSupplementalOxygenLevel();
        saved_measurement_manually_entered_capillary_refill_time = new MeasurementCapillaryRefillTime();
        saved_measurement_manually_entered_respiration_distress = new MeasurementRespirationDistress();
        saved_measurement_manually_entered_nurse_or_family_concern = new MeasurementFamilyOrNurseConcern();
    }


    public void reset(VitalSignType vital_sign_type)
    {
        Log.e(TAG, " reset " + vital_sign_type);

        switch (vital_sign_type)
        {
            case HEART_RATE:
            {
                saved_measurement_heart_rate = new MeasurementHeartRate();
            }
            break;

            case RESPIRATION_RATE:
            {
                saved_measurement_respiration_rate = new MeasurementRespirationRate();
            }
            break;

            case TEMPERATURE:
            {
                saved_measurement_temperature = new MeasurementTemperature();
            }
            break;

            case SPO2:
            {
                saved_measurement_spo2 = new MeasurementSpO2();
            }
            break;

            case BLOOD_PRESSURE:
            {
                saved_measurement_blood_pressure = new MeasurementBloodPressure();
            }
            break;

            case WEIGHT:
            {
                saved_measurement_weight = new MeasurementWeight();
            }
            break;

            case EARLY_WARNING_SCORE:
            {
                saved_measurement_early_warning_score = new MeasurementEarlyWarningScore();
            }
            break;

            case MANUALLY_ENTERED_HEART_RATE:
            {
                saved_measurement_manually_entered_heart_rate = new MeasurementManuallyEnteredHeartRate();
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_RATE:
            {
                saved_measurement_manually_entered_respiration_rate = new MeasurementManuallyEnteredRespirationRate();
            }
            break;

            case MANUALLY_ENTERED_TEMPERATURE:
            {
                saved_measurement_manually_entered_temperature = new MeasurementManuallyEnteredTemperature();
            }
            break;

            case MANUALLY_ENTERED_SPO2:
            {
                saved_measurement_manually_entered_spo2 = new MeasurementManuallyEnteredSpO2();
            }
            break;

            case MANUALLY_ENTERED_BLOOD_PRESSURE:
            {
                saved_measurement_manually_entered_blood_pressure = new MeasurementManuallyEnteredBloodPressure();
            }
            break;

            case MANUALLY_ENTERED_WEIGHT:
            {
                saved_measurement_manually_entered_weight = new MeasurementManuallyEnteredWeight();
            }

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            {
                saved_measurement_manually_entered_capillary_refill_time = new MeasurementCapillaryRefillTime();
            }
            break;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
            {
                saved_measurement_manually_entered_consciousness_level = new MeasurementConsciousnessLevel();
            }
            break;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                saved_measurement_manually_entered_nurse_or_family_concern = new MeasurementFamilyOrNurseConcern();
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                saved_measurement_manually_entered_respiration_distress = new MeasurementRespirationDistress();
            }
            break;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
            {
                saved_measurement_manually_entered_supplemental_oxygen = new MeasurementSupplementalOxygenLevel();
            }
            break;

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                saved_measurement_manually_entered_urine_output = new MeasurementUrineOutput();
            }
            break;
        }
    }


    /**
     * Function to get whether the saved measurement is within the measurement validity time
     * @return saved_measurement_heart_rate
     */
    public boolean isSavedMeasurementValid(VitalSignType vital_sign_type)
    {
        MeasurementVitalSign saved_measurement = getSavedMeasurement(vital_sign_type);
        boolean x = isMeasurementValid(vital_sign_type, saved_measurement);

        Log.d(TAG, "isSavedMeasurementValid : " + vital_sign_type + " = " + x);

        if (saved_measurement != null) {
            Log.d(TAG, "isSavedMeasurementValid : " + saved_measurement + " = " + saved_measurement.getPrimaryMeasurement());
            Log.d(TAG, "isSavedMeasurementValid : " + saved_measurement + " = " + saved_measurement.measurement_validity_time_left_in_seconds);
        }

        return x;
    }


    public boolean isMeasurementValid(VitalSignType vital_sign_type, MeasurementVitalSign measurement)
    {
        if (measurement == null)
        {
            return false;
        }

        int measurement_value = (int)measurement.getPrimaryMeasurement();

        long validity_time_left_in_ms = measurement.measurement_validity_time_left_in_seconds * DateUtils.SECOND_IN_MILLIS;

        String log_line = "isSavedMeasurementValid " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement);

        if (validity_time_left_in_ms <= 0)
        {
            log_line = log_line.concat(" : TIMED OUT");
        }

        if(measurement_value >= ErrorCodes.ERROR_CODES)
        {
            log_line = log_line.concat(" : ERROR_CODES");
        }

        if(measurement_value == MeasurementVitalSign.INVALID_MEASUREMENT)
        {
            log_line = log_line.concat(" : INVALID_MEASUREMENT");
        }

        boolean return_value;

        switch(vital_sign_type)
        {
            case HEART_RATE:
            case RESPIRATION_RATE:
            case TEMPERATURE:
            case SPO2:
            case BLOOD_PRESSURE:
            case WEIGHT:
            case EARLY_WARNING_SCORE:
            case MANUALLY_ENTERED_HEART_RATE:
            case MANUALLY_ENTERED_RESPIRATION_RATE:
            case MANUALLY_ENTERED_TEMPERATURE:
            case MANUALLY_ENTERED_SPO2:
            case MANUALLY_ENTERED_BLOOD_PRESSURE:
            case MANUALLY_ENTERED_WEIGHT:
            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                return_value = (validity_time_left_in_ms > 0) && (measurement_value < ErrorCodes.ERROR_CODES) && (measurement_value != MeasurementVitalSign.INVALID_MEASUREMENT);
            }
            break;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                return_value = validity_time_left_in_ms > 0;
            }
            break;

            case MANUALLY_ENTERED_ANNOTATION:
            case PATIENT_ORIENTATION:
            case NOT_SET_YET:
            default:
            {
                return_value = false;
            }
            break;
        }

        if (return_value)
        {
            Log.e(TAG, log_line + " : VALID");
        }
        else
        {
            Log.e(TAG, log_line);
        }

        return return_value;
    }


    public MeasurementVitalSign getSavedMeasurement(VitalSignType vital_sign_type)
    {
        switch (vital_sign_type)
        {
            case HEART_RATE:                                return saved_measurement_heart_rate;
            case RESPIRATION_RATE:                          return saved_measurement_respiration_rate;
            case TEMPERATURE:                               return saved_measurement_temperature;
            case SPO2:                                      return saved_measurement_spo2;
            case BLOOD_PRESSURE:                            return saved_measurement_blood_pressure;
            case WEIGHT:                                    return saved_measurement_weight;
            case EARLY_WARNING_SCORE:                       return saved_measurement_early_warning_score;
            case MANUALLY_ENTERED_HEART_RATE:               return saved_measurement_manually_entered_heart_rate;
            case MANUALLY_ENTERED_RESPIRATION_RATE:         return saved_measurement_manually_entered_respiration_rate;
            case MANUALLY_ENTERED_TEMPERATURE:              return saved_measurement_manually_entered_temperature;
            case MANUALLY_ENTERED_SPO2:                     return saved_measurement_manually_entered_spo2;
            case MANUALLY_ENTERED_BLOOD_PRESSURE:           return saved_measurement_manually_entered_blood_pressure;
            case MANUALLY_ENTERED_WEIGHT:                   return saved_measurement_manually_entered_weight;
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:    return saved_measurement_manually_entered_capillary_refill_time;
            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:      return saved_measurement_manually_entered_consciousness_level;
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:  return saved_measurement_manually_entered_nurse_or_family_concern;
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:     return saved_measurement_manually_entered_respiration_distress;
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:      return saved_measurement_manually_entered_supplemental_oxygen;
            case MANUALLY_ENTERED_URINE_OUTPUT:             return saved_measurement_manually_entered_urine_output;

            default:                                        return null;
        }
    }


    public void setSavedMeasurement(VitalSignType vital_sign_type, MeasurementVitalSign saved_measurement)
    {
        Log.e(TAG, "setSavedMeasurement : " + vital_sign_type);

        switch (vital_sign_type)
        {
            case HEART_RATE:
            {
                saved_measurement_heart_rate = (MeasurementHeartRate)saved_measurement;
            }
            break;

            case RESPIRATION_RATE:
            {
                saved_measurement_respiration_rate = (MeasurementRespirationRate)saved_measurement;
            }
            break;

            case TEMPERATURE:
            {
                saved_measurement_temperature= (MeasurementTemperature)saved_measurement;
            }
            break;

            case SPO2:
            {
                saved_measurement_spo2 = (MeasurementSpO2)saved_measurement;
            }
            break;

            case BLOOD_PRESSURE:
            {
                saved_measurement_blood_pressure = (MeasurementBloodPressure)saved_measurement;
            }
            break;

            case WEIGHT:
            {
                saved_measurement_weight = (MeasurementWeight)saved_measurement;
            }
            break;

            case EARLY_WARNING_SCORE:
            {
                saved_measurement_early_warning_score = (MeasurementEarlyWarningScore)saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_HEART_RATE:
            {
                saved_measurement_manually_entered_heart_rate = (MeasurementManuallyEnteredHeartRate) saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_RATE:
            {
                saved_measurement_manually_entered_respiration_rate = (MeasurementManuallyEnteredRespirationRate) saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_TEMPERATURE:
            {
                saved_measurement_manually_entered_temperature = (MeasurementManuallyEnteredTemperature) saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_SPO2:
            {
                saved_measurement_manually_entered_spo2 = (MeasurementManuallyEnteredSpO2) saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_BLOOD_PRESSURE:
            {
                saved_measurement_manually_entered_blood_pressure = (MeasurementManuallyEnteredBloodPressure) saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_WEIGHT:
            {
                saved_measurement_manually_entered_weight = (MeasurementManuallyEnteredWeight) saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            {
                saved_measurement_manually_entered_capillary_refill_time = (MeasurementCapillaryRefillTime)saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
            {
                saved_measurement_manually_entered_consciousness_level = (MeasurementConsciousnessLevel)saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                saved_measurement_manually_entered_nurse_or_family_concern = (MeasurementFamilyOrNurseConcern)saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                saved_measurement_manually_entered_respiration_distress = (MeasurementRespirationDistress)saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
            {
                saved_measurement_manually_entered_supplemental_oxygen = (MeasurementSupplementalOxygenLevel)saved_measurement;
            }
            break;

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                saved_measurement_manually_entered_urine_output = (MeasurementUrineOutput) saved_measurement;
            }
            break;
        }
    }
}
