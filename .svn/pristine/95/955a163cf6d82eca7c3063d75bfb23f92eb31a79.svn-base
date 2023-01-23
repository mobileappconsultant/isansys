package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableLifetouchRawAccelerometerModeSample extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "lifetouch_accelerometer_mode_sample";
    public static final String COLUMN_X_SAMPLE_VALUE = "x";
    public static final String COLUMN_Y_SAMPLE_VALUE = "y";
    public static final String COLUMN_Z_SAMPLE_VALUE = "z";

    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_X_SAMPLE_VALUE + " int not null,"
            + COLUMN_Y_SAMPLE_VALUE + " int not null,"
            + COLUMN_Z_SAMPLE_VALUE + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);
    }
}
