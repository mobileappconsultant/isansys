package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableOximeterMeasurement extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "oximeter_measurement";
    public static final String COLUMN_PULSE = "pulse";
    public static final String COLUMN_SPO2 = "spo2";

    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_PULSE + " int not null,"
            + COLUMN_SPO2 + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
