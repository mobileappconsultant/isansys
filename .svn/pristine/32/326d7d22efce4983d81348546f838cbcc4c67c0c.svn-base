package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TablePatientSessionsFullySynced extends Table
{
    // Database table
    public static final String TABLE_NAME = "patient_sessions_fully_synced";
    public static final String COLUMN_ANDROID_PATIENT_SESSION_NUMBER = "android_patient_session_number";
    public static final String COLUMN_SERVER_PATIENT_SESSION_NUMBER = "server_patient_session_number";

    // Database creation SQL statement
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, " 
            + COLUMN_ANDROID_PATIENT_SESSION_NUMBER + " int not null,"
            + COLUMN_SERVER_PATIENT_SESSION_NUMBER + " int not null,"
            + COLUMN_TIMESTAMP + " int not null,"
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
        Log.w(TablePatientSessionsFullySynced.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
