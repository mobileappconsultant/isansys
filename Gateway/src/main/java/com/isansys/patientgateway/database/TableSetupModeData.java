package com.isansys.patientgateway.database;


public class TableSetupModeData extends TableSensorMeasurement
{
    public static final String COLUMN_SAMPLE_VALUE = "sample_value";

    protected static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_SAMPLE_VALUE + " int not null,";
}
