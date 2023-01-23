package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableWeightScaleWeight extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "weight_scale_weight";
    public static final String COLUMN_WEIGHT = "weight";
    
    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_WEIGHT + " real not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
