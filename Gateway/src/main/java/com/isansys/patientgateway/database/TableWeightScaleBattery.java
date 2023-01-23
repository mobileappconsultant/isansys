package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableWeightScaleBattery extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "weight_scale_battery";
    public static final String COLUMN_BATTERY_PERCENTAGE = "battery";

    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_BATTERY_PERCENTAGE + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
