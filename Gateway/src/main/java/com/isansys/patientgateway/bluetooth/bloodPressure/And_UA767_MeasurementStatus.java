package com.isansys.patientgateway.bluetooth.bloodPressure;

public class And_UA767_MeasurementStatus
{
    public enum Status
    {
        CORRECT_MEASUREMENT,
        MEASUREMENT_ERROR,
        CUFF_ERROR,
        LOW_BATTERY_STATUS,
        LOW_BATTERY_STATUS_AND_IRREGULAR_HEART_BEAT,
        IRREGULAR_HEART_BEAT,

        NON_INITED_ENUM_VALUE
    }
}
