package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableThresholdSetAgeBlockDetail extends Table
{
    // Database table
    public static final String TABLE_NAME = "threshold_set_age_block_detail";

    public static final String COLUMN_THRESHOLD_SET_ID = "threshold_set_id";            // Which Threshold Set record this Level record is linked to
    public static final String COLUMN_AGE_BOTTOM = "age_bottom";                        // Start Age this threshold set is valid across  
    public static final String COLUMN_AGE_TOP = "age_top";                              // End Age this threshold set is valid across
    public static final String COLUMN_DISPLAY_NAME = "display_name";                    // Name to show on the Gateway/Server
    public static final String COLUMN_IMAGE_BINARY = "image_binary";                    // Image to show on Select Age Range page
    public static final String COLUMN_IS_ADULT = "is_adult";                            // Is this for an Adult

    // Database creation SQL statement
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_SERVERS_ID + " int not null,"
            + COLUMN_THRESHOLD_SET_ID + " int not null,"
            + COLUMN_AGE_BOTTOM + " int not null,"
            + COLUMN_AGE_TOP + " int not null,"
            + COLUMN_DISPLAY_NAME + " int not null,"
            + COLUMN_IMAGE_BINARY + " BLOB not null,"
            + COLUMN_IS_ADULT + " int not null,"
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
        Log.w(TableThresholdSetAgeBlockDetail.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
