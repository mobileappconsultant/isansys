package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableThresholdSetColour extends Table
{
    // Database table
    public static final String TABLE_NAME = "threshold_set_colour";

    public static final String COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID = "threshold_set_age_block_detail"; 
                                                                                        // Which Threshold Set Age Block Detail record this Level record is linked to
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_COLOUR = "colour";
    public static final String COLUMN_TEXT_COLOUR = "text_colour";

    // Database creation SQL statement
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_SERVERS_ID + " int not null,"
            + COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID + " int not null,"
            + COLUMN_SCORE + " int not null,"
            + COLUMN_COLOUR + " int not null,"
            + COLUMN_TEXT_COLOUR + " int not null,"
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
