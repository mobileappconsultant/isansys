package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableThresholdSetLevel extends Table
{
    // Database table
    public static final String TABLE_NAME = "threshold_set_level";

    public static final String COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID = "threshold_set_age_block_detail"; 
                                                                                        // Which Threshold Set Age Block Detail record this Level record is linked to
    public static final String COLUMN_RANGE_BOTTOM = "range_bottom";                    // Bottom value this threshold set is valid from  
    public static final String COLUMN_RANGE_TOP = "range_top";                          // Top value this threshold set is valid to
    public static final String COLUMN_EARLY_WARNING_SCORE = "early_warning_score";      // Early Warning Score number for this range
    public static final String COLUMN_MEASUREMENT_TYPE = "measurement_type";            // What kind of measurement is it?
    public static final String COLUMN_MEASUREMENT_TYPE_AS_STRING = "measurement_type_as_string";            // What kind of measurement is it?
    public static final String COLUMN_DISPLAY_TEXT = "display_text";                    // What to show on the screen when entering this measurement
    public static final String COLUMN_INFORMATION_TEXT = "information_text";            // What to show on the RHS of the screen when entering this measurement
    
    // Database creation SQL statement
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_SERVERS_ID + " int not null,"
            + COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID + " int not null,"
            + COLUMN_RANGE_BOTTOM + " int not null,"
            + COLUMN_RANGE_TOP + " int not null,"
            + COLUMN_EARLY_WARNING_SCORE + " int not null,"
            + COLUMN_MEASUREMENT_TYPE + " int not null,"
            + COLUMN_MEASUREMENT_TYPE_AS_STRING + " string not null,"
            + COLUMN_DISPLAY_TEXT + " string not null,"
            + COLUMN_INFORMATION_TEXT + " string not null,"
            + COLUMN_TIMESTAMP + " int default 0,"
            + COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP + " int default 0,"
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0"
            + ");";

    public static void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TableThresholdSetLevel.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
