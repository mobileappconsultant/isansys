package com.isansys.common.measurements;

import com.isansys.common.enums.SensorType;

public enum VitalSignType
{
    NOT_SET_YET,

    HEART_RATE,                                                                                     // 1
    RESPIRATION_RATE,                                                                               // 2
    TEMPERATURE,                                                                                    // 3
    SPO2,                                                                                           // 4
    DO_NOT_USE__WAS_LIFEOX_SPO2,                                                                    // 5
    BLOOD_PRESSURE,                                                                                 // 6

    EARLY_WARNING_SCORE,                                                                            // 7
    REMOVED__PULSE_TRANSIT_TIME,                                                                    // 8
    PATIENT_ORIENTATION,                                                                            // 9

    MANUALLY_ENTERED_HEART_RATE,                                                                    // 10
    MANUALLY_ENTERED_RESPIRATION_RATE,                                                              // 11
    MANUALLY_ENTERED_TEMPERATURE,                                                                   // 12
    MANUALLY_ENTERED_SPO2,                                                                          // 13
    MANUALLY_ENTERED_BLOOD_PRESSURE,                                                                // 14
    MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL,                                                           // 15
    MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN,                                                           // 16
    MANUALLY_ENTERED_ANNOTATION,                                                                    // 17
    MANUALLY_ENTERED_CAPILLARY_REFILL_TIME,                                                         // 18
    MANUALLY_ENTERED_RESPIRATION_DISTRESS,                                                          // 19
    MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN,                                                       // 20

    SETUP_MODE_DATA_POINT,                                                                          // 21

    BATTERY_READING,                                                                                // 22

    WEIGHT,                                                                                         // 23
    MANUALLY_ENTERED_WEIGHT,                                                                        // 24
    MANUALLY_ENTERED_URINE_OUTPUT;                                                                  // 25

    /**
     * If the VitalSignType is a manual vital with a corresponding sensor vital, return the
     * relevant VitalSignType.
     *
     * @return Sensor Vital Sign Type, or NOT_SET_YET
     */
    public VitalSignType getEquivalentSensorVitalSignType()
    {
        switch(this)
        {
            case MANUALLY_ENTERED_HEART_RATE:
                return VitalSignType.HEART_RATE;

            case MANUALLY_ENTERED_RESPIRATION_RATE:
                return VitalSignType.RESPIRATION_RATE;

            case MANUALLY_ENTERED_TEMPERATURE:
                return VitalSignType.TEMPERATURE;

            case MANUALLY_ENTERED_SPO2:
                return VitalSignType.SPO2;

            case MANUALLY_ENTERED_BLOOD_PRESSURE:
                return VitalSignType.BLOOD_PRESSURE;

            case MANUALLY_ENTERED_WEIGHT:
                return VitalSignType.WEIGHT;
        }

        return VitalSignType.NOT_SET_YET;
    }


    /**
     * If the VitalSignType is a sensor vital with a corresponding manual vital, return the
     * relevant VitalSignType.
     *
     * @return Manual Vital Sign Type, or NOT_SET_YET
     */
    public VitalSignType getEquivalentManualVitalSignType()
    {
        switch(this)
        {
            case HEART_RATE:
                return MANUALLY_ENTERED_HEART_RATE;

            case RESPIRATION_RATE:
                return MANUALLY_ENTERED_RESPIRATION_RATE;

            case TEMPERATURE:
                return MANUALLY_ENTERED_TEMPERATURE;

            case SPO2:
                return MANUALLY_ENTERED_SPO2;

            case BLOOD_PRESSURE:
                return MANUALLY_ENTERED_BLOOD_PRESSURE;

            case WEIGHT:
                return MANUALLY_ENTERED_WEIGHT;
        }

        return VitalSignType.NOT_SET_YET;
    }

    public boolean isManualVital()
    {
        switch(this)
        {
            case MANUALLY_ENTERED_HEART_RATE:
            case MANUALLY_ENTERED_RESPIRATION_RATE:
            case MANUALLY_ENTERED_TEMPERATURE:
            case MANUALLY_ENTERED_SPO2:
            case MANUALLY_ENTERED_BLOOD_PRESSURE:
            case MANUALLY_ENTERED_WEIGHT:
            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
            case MANUALLY_ENTERED_ANNOTATION:
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case MANUALLY_ENTERED_URINE_OUTPUT:
                return true;

            default:
                return false;
        }
    }

    public static VitalSignType[] getVitalSignTypesFromSensorType(SensorType sensor_type)
    {
        switch(sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                return new VitalSignType[]{HEART_RATE, RESPIRATION_RATE};
            }

            case SENSOR_TYPE__TEMPERATURE:
            {
                return new VitalSignType[]{TEMPERATURE};
            }

            case SENSOR_TYPE__SPO2:
            {
                return new VitalSignType[]{SPO2};
            }

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                return new VitalSignType[]{BLOOD_PRESSURE};
            }

            case SENSOR_TYPE__WEIGHT_SCALE:
            {
                return new VitalSignType[]{WEIGHT};
            }

            default:
            {
                return new VitalSignType[]{};
            }
        }
    }
}
