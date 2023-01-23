package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableManuallyEnteredTemperature extends TableManuallyEnteredMeasurement
{
    public static final String TABLE_NAME = "manually_entered_temperature";
    public static final String COLUMN_TEMPERATURE = "temperature";

    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_TEMPERATURE + " double not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
