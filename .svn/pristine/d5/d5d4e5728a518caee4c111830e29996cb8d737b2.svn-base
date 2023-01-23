package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableLifetouchHeartBeat extends TableSensorMeasurement
{
    public static final String TABLE_NAME = "lifetouch_heart_beat";
    public static final String COLUMN_AMPLITUDE = "amplitude";
    public static final String COLUMN_SEQUENCE_ID = "sequence_id";
    public static final String COLUMN_ACTIVITY_LEVEL = "activity_level";
    public static final String COLUMN_RR_INTERVAL = "rr_interval_in_ms";
    
    private static final String MEASUREMENT_SPECIFIC_SQL = COLUMN_AMPLITUDE + " int not null," 
            + COLUMN_SEQUENCE_ID + " int not null,"
            + COLUMN_ACTIVITY_LEVEL + " int not null,"
            + COLUMN_RR_INTERVAL + " int not null,";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);

        super.createIndices(database, TABLE_NAME);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);

        super.createIndices(database, TABLE_NAME);
    }
}
