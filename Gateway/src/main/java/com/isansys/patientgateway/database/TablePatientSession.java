package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TablePatientSession extends Table
{
    // Database table
    public static final String TABLE_NAME = "patient_session";
    
    public static final String COLUMN_BY_PATIENT_DETAILS_ID = "by_patient_details_id";
    public static final String COLUMN_BY_SERVERS_PATIENT_DETAILS_ID = "by_servers_patient_details_id";
    public static final String COLUMN_PATIENT_START_SESSION_TIME = "patient_start_session_time";
    public static final String COLUMN_PATIENT_START_SESSION_BY_USER_ID = "patient_start_session_by_user_id";    
    public static final String COLUMN_PATIENT_END_SESSION_TIME = "patient_end_session_time";
    public static final String COLUMN_PATIENT_END_SESSION_BY_USER_ID = "patient_end_session_by_user_id";
    
    public static final String COLUMN_PATIENT_SESSION_PAUSED = "patient_session_paused";
    
    public static final String COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED = "start_session_sent_to_server_and_server_acknowledged";
    public static final String COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP = "start_session_sent_to_server_and_server_acknowledged_timestamp";
    public static final String COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED = "start_session_sent_to_server_but_failed";
    public static final String COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP = "start_session_sent_to_server_but_failed_timestamp";

    public static final String COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED = "end_session_sent_to_server_and_server_acknowledged";
    public static final String COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP = "end_session_sent_to_server_and_server_acknowledged_timestamp";
    public static final String COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED = "end_session_sent_to_server_but_failed";
    public static final String COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP = "end_session_sent_to_server_but_failed_timestamp";

    private static final String CREATE_TABLE_PATIENT_SESSION = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            
            + COLUMN_SERVERS_ID + " int, "
            
            + COLUMN_BY_PATIENT_DETAILS_ID + " int not null, "
            + COLUMN_BY_SERVERS_PATIENT_DETAILS_ID + " int, "
            
            + COLUMN_PATIENT_START_SESSION_TIME + " int not null, "
            + COLUMN_PATIENT_START_SESSION_BY_USER_ID + " int not null, "

            /* Special case for End Session. These WILL be NULL when a new row is created as the Session hasn't ended yet */
            + COLUMN_PATIENT_END_SESSION_TIME + " int default 0, "
            + COLUMN_PATIENT_END_SESSION_BY_USER_ID + " int, "
            
            + COLUMN_PATIENT_SESSION_PAUSED + " boolean not null default 0, "
  
            + COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
            + COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0, "
            + COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED + " boolean not null default 0, "
            + COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + " int default 0, "

            + COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
            + COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0, "
            + COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED + " boolean not null default 0, "
            + COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + " int default 0"

            + ");";

	public static void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE_PATIENT_SESSION);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TablePatientSession.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
