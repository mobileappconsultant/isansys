package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableManuallyEnteredSupplementalOxygenLevel extends TableManuallyEnteredMeasurement
{
    public static final String TABLE_NAME = "manually_entered_supplemental_oxygen_level";
    public static final String COLUMN_SUPPLEMENTAL_OXYGEN_LEVEL = "supplemental_oxygen_level";

    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_SUPPLEMENTAL_OXYGEN_LEVEL + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
