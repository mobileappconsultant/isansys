package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableBeds extends Table
{
    // Database table
    public static final String TABLE_NAME = "beds";
    public static final String COLUMN_BED_NAME = "bed_name";
    public static final String COLUMN_BY_WARD_ID = "by_ward_id";


    // Database creation SQL statement
    private final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SERVERS_ID + " int, "
            + COLUMN_BED_NAME + " string not null,"
            + COLUMN_BY_WARD_ID + " int, "
            + COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP + " int default 0"
            + ");";

    public void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TableWards.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
