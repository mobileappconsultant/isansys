package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableBloodPressureBattery extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "blood_pressure_battery";
    public static final String COLUMN_BATTERY = "battery";
    
    private final String MEASUREMENT_SPECIFIC_SQL = COLUMN_BATTERY + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
