package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableDiagnosticsUiStartupTimes extends Table
{
    // Database table
    public static final String TABLE_NAME = "diagnostics_ui_startup_times";

    // Database creation SQL statement
    private static final String CREATE_TABLE_DIAGNOSTICS_UI_STARTUP_TIMES = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, " 
            + COLUMN_TIMESTAMP + " int not null"
            + ");";

    public static void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE_DIAGNOSTICS_UI_STARTUP_TIMES);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TableDiagnosticsUiStartupTimes.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
