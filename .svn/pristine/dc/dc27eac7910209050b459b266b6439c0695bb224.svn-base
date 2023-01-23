package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableThresholdSet extends Table
{
    // Database table
    public static final String TABLE_NAME = "threshold_set";

    public static final String COLUMN_NAME = "set_name";                                    // Description of this EWS type. E.g. NEWS, PEWS, Mats Hospital Adult, Rorys Hospital Kids
    public static final String COLUMN_IS_DEFAULT = "is_default";                              // Was this download as a Default from the Server
    
    // Database creation SQL statement
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_SERVERS_ID + " int not null,"
            + COLUMN_NAME + " string not null,"
            + COLUMN_IS_DEFAULT + " boolean not null default 0, "
            + COLUMN_TIMESTAMP + " int default 0, "
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
        Log.w(TableThresholdSet.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
