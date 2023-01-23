package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableManuallyEnteredBloodPressure extends TableManuallyEnteredMeasurement
{
    public static final String TABLE_NAME = "manually_entered_blood_pressure_measurement";
    public static final String COLUMN_SYSTOLIC = "systolic";
    public static final String COLUMN_DIASTOLIC = "diastolic";

    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_SYSTOLIC + " int not null," 
            + COLUMN_DIASTOLIC + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
