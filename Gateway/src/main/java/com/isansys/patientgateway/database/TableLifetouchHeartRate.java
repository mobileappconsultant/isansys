package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableLifetouchHeartRate extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "lifetouch_heart_rate";
    public static final String COLUMN_HEART_RATE = "heart_rate";
    
    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_HEART_RATE + " real not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
