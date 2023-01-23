package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableSetupModeLog extends Table
{
    // Database table
    public static final String TABLE_NAME = "setup_mode_log";
    
    public static final String COLUMN_START_SETUP_MODE_TIME = "start_setup_mode_time";
    public static final String COLUMN_END_SETUP_MODE_TIME = "end_setup_mode_time";
    public static final String COLUMN_SENSOR_TYPE = "sensor_type";
    public static final String COLUMN_DEVICE_TYPE = "device_type";

    // Database creation SQL statement
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SERVERS_ID + " int, "
            
            + COLUMN_DEVICE_SESSION_NUMBER + " int not null, "

            + COLUMN_SENSOR_TYPE + " int not null, "
            + COLUMN_DEVICE_TYPE + " int not null, "

            + COLUMN_START_SETUP_MODE_TIME + " int not null, "
            + COLUMN_END_SETUP_MODE_TIME + " int default 0, "

            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0,"
            + COLUMN_SENT_TO_SERVER_BUT_FAILED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + " int default 0"

            + ");";

    public static void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TableDeviceSession.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
