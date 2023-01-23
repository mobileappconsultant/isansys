package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableManuallyEnteredAnnotation extends Table
{
    // Database table
    public static final String TABLE_NAME = "manually_entered_annotation";
    public static final String COLUMN_ANNOTATION_TEXT = "annotation_text";

    // Database creation SQL statement
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PATIENT_SESSION_NUMBER + " int not null,"
            + COLUMN_BY_USER_ID + " int not null, "
            + COLUMN_ANNOTATION_TEXT + " string not null,"
            + COLUMN_TIMESTAMP + " int not null,"
            + COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS + " int null,"
            + COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP + " int default 0,"
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
        Log.w(TableManuallyEnteredAnnotation.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
