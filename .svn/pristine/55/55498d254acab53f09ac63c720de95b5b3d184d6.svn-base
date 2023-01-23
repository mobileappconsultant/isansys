package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableEarlyWarningScore extends Table
{
    // Database table
    public static final String TABLE_NAME = "early_warning_score";
    public static final String COLUMN_PATIENT_SESSION_NUMBER = "patient_session_number";                // Early Warning Scores are calculated on the Gateway. So tied to a Patient Session
    public static final String COLUMN_DEVICE_SESSION_NUMBER = "device_session_number";					// Early warning scores are associated with a "device" session - not a physical one, but still used as one in the gateway code.
    public static final String COLUMN_EARLY_WARNING_SCORE = "early_warning_score";                      // The EWS measurement
    public static final String COLUMN_MAX_POSSIBLE = "max_possible";                                    // The max number this EWS is measured against. E.g. if there is only a Lifetouch then the Max is different to if there is a Lifetouch, Lifetemp, Pulse Ox and Blood Pressure
    public static final String COLUMN_IS_SPECIAL_ALERT = "is_special_alert";							// EWS level triggered by a special case rather than the aggregate score (e.g. NEWS specifies EWS of 2 (orange) if any one vital sign gives a score of 3)
    public static final String COLUMN_TREND_DIRECTION = "trend_direction";                              // Which way is the trend going. Allows the UI and Server to show this onscreen
    
    // Database creation SQL statement
    private static final String CREATE_TABLE_EARLY_WARNING_SCORE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_PATIENT_SESSION_NUMBER + " int not null,"
            + COLUMN_DEVICE_SESSION_NUMBER + " int not null,"
            + COLUMN_EARLY_WARNING_SCORE + " int not null,"
            + COLUMN_MAX_POSSIBLE + " int not null,"
            + COLUMN_IS_SPECIAL_ALERT + " boolean not null default 0, "
            + COLUMN_TREND_DIRECTION + " int not null,"
            + COLUMN_TIMESTAMP + " int not null,"
            + COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP + " int default 0,"
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0,"
            + COLUMN_SENT_TO_SERVER_BUT_FAILED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + " int default 0"
            + ");";

    public static void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE_EARLY_WARNING_SCORE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TableEarlyWarningScore.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
