package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableLifetouchPatientOrientation extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "patient_orientation";
    public static final String COLUMN_PATIENT_ORIENTATION = "orientation";

    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_PATIENT_ORIENTATION + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
