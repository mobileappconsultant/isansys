package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableConnectionEvent extends Table
{
    // Database table
    public static final String TABLE_NAME = "connection_event";
    public static final String COLUMN_PATIENT_SESSION_NUMBER = "patient_session_number";
    public static final String COLUMN_DEVICE_SESSION_NUMBER = "device_session_number";
    public static final String COLUMN_CONNECTED = "connected";

    // Database creation SQL statement
    private static final String CREATE_TABLE_CONNECTION_EVENT = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, " 
            + COLUMN_PATIENT_SESSION_NUMBER + " int not null,"
            + COLUMN_DEVICE_SESSION_NUMBER + " int not null," 
            + COLUMN_CONNECTED + " int not null, " 
            + COLUMN_TIMESTAMP + " int not null,"
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0,"
            + COLUMN_SENT_TO_SERVER_BUT_FAILED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + " int default 0"
            + ");";

    public static void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE_CONNECTION_EVENT);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TableConnectionEvent.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
