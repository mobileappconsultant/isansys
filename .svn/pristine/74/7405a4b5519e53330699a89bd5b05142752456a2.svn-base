package com.isansys.common.enums;

import com.isansys.common.measurements.VitalSignType;

public enum MeasurementTypes
{
    // These are the measurement types as defined in the webservices (CommonCode.Enums.PatientMeasurement)
    // They are different to VitalSignType, which is used internally on the Gateway

    UNKNOWN(0),
    HEART_RATE(1),
    RESPIRATION_RATE(2),
    TEMPERATURE(3),
    SPO2(4),
    BLOOD_PRESSURE(5),
    BLOOD_PRESSURE_ENUM_NO_LONGER_USED_BUT_REQUIRED_FOR_BACKWARDS_COMAPTIBILITY(6), // Used to exist, used when loading in thresholds
    HEART_BEAT(7),
    EARLY_WARNING_SCORE(8),
    PATIENT_ORIENTATION(9),
    SUPPLEMENTAL_OXYGEN(10),
    CONSCIOUSNESS_LEVEL(11),

    BATTERY_PERCENTAGE(12), // Not used in Gateway but here to align with server CommonCode.Enums\PatientMeasurement.cs
    RAPID_INDEX(13),        // Not used in Gateway but here to align with server CommonCode.Enums\PatientMeasurement.cs
    ANNOTATION(14),         // Not used in Gateway but here to align with server CommonCode.Enums\PatientMeasurement.cs
    SETUP_MODE_LOG(15),     // Not used in Gateway but here to align with server CommonCode.Enums\PatientMeasurement.cs

    CAPILLARY_REFILL_TIME(16),
    FAMILY_OR_NURSE_CONCERN(17),
    RESPIRATION_DISTRESS(18),

    SETUP_MODE(19),         // Not used in Gateway but here to align with server CommonCode.Enums\PatientMeasurement.cs
    RAW_ACCELERATOR(20),    // Not used in Gateway but here to align with server CommonCode.Enums\PatientMeasurement.cs
    SPO2_INTERMEDIATE(21),  // Not used in Gateway but here to align with server CommonCode.Enums\PatientMeasurement.cs
    CONNECTION_EVENT(22),   // Not used in Gateway but here to align with server CommonCode.Enums\PatientMeasurement.cs

    WEIGHT(23),
    URINE_OUTPUT(24);

    private final int value;

    MeasurementTypes(int value)
    {
        this.value = value;
    }

    public static MeasurementTypes getMeasurementTypeFromVitalSignType(VitalSignType vital_sign)
    {
        switch(vital_sign)
        {
            case HEART_RATE:
            case MANUALLY_ENTERED_HEART_RATE:
            {
                return HEART_RATE;
            }

            case RESPIRATION_RATE:
            case MANUALLY_ENTERED_RESPIRATION_RATE:
            {
                return RESPIRATION_RATE;
            }

            case TEMPERATURE:
            case MANUALLY_ENTERED_TEMPERATURE:
            {
                return TEMPERATURE;
            }

            case SPO2:
            case MANUALLY_ENTERED_SPO2:
            {
                return SPO2;
            }

            case BLOOD_PRESSURE:
            case MANUALLY_ENTERED_BLOOD_PRESSURE:
            {
                return BLOOD_PRESSURE;
            }

            case WEIGHT:
            case MANUALLY_ENTERED_WEIGHT:
            {
                return WEIGHT;
            }

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
            {
                // ToDO: PEWS uses Glasgow Coma Scale, need to work out how to incorporate that...
                return CONSCIOUSNESS_LEVEL;
            }

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
            {
                return SUPPLEMENTAL_OXYGEN;
            }

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            {
                return CAPILLARY_REFILL_TIME;
            }

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                return RESPIRATION_DISTRESS;
            }

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                return FAMILY_OR_NURSE_CONCERN;
            }

            case EARLY_WARNING_SCORE:
            case PATIENT_ORIENTATION:
            case NOT_SET_YET:
            default:
            {
                return UNKNOWN;
            }
        }
    }

    public int getValue()
    {
        return value;
    }
}
