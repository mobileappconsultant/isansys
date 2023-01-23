package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableLifetouchRespirationRate extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "lifetouch_respiration_rate";
    public static final String COLUMN_RESPIRATION_RATE = "respiration_rate";

    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_RESPIRATION_RATE + " real not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
