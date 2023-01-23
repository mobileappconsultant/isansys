package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableManuallyEnteredCapillaryRefillTime extends TableManuallyEnteredMeasurement
{
    public static final String TABLE_NAME = "manually_entered_capillary_refill_time";
    public static final String COLUMN_CAPILLARY_REFILL_TIME = "capillary_refill_time";

    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_CAPILLARY_REFILL_TIME + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
