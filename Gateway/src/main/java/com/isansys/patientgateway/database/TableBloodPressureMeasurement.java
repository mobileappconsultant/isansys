package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableBloodPressureMeasurement extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "blood_pressure_measurement";
    public static final String COLUMN_DIASTOLIC = "diastolic";
    public static final String COLUMN_SYSTOLIC = "systolic";
    public static final String COLUMN_PULSE = "pulse";
    
    private final String MEASUREMENT_SPECIFIC_SQL =
              COLUMN_DIASTOLIC + " int not null,"
            + COLUMN_SYSTOLIC + " int not null,"
            + COLUMN_PULSE + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
