package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableManuallyEnteredWeight extends TableManuallyEnteredMeasurement
{
    public static final String TABLE_NAME = "manually_entered_weight";
    public static final String COLUMN_WEIGHT = "weight";
    
    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_WEIGHT + " double not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
