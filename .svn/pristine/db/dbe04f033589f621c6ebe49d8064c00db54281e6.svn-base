package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableManuallyEnteredMeasurement extends Table
{
    void onCreateTable(SQLiteDatabase database, String table_name, String measurement_specific_sql)
    {
        String create_new_database_table = "create table " + table_name + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_PATIENT_SESSION_NUMBER + " int not null,"
                + COLUMN_BY_USER_ID + " int not null,"
                + measurement_specific_sql
                + COLUMN_TIMESTAMP + " int not null,"
                + COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS + " int null,"
                + COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP + " int default 0,"
                + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
                + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0,"
                + COLUMN_SENT_TO_SERVER_BUT_FAILED + " boolean not null default 0, "
                + COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + " int default 0"
                + ");";

        database.execSQL(create_new_database_table);
    }

    void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion, String table_name, String measurement_specific_sql)
    {
        Log.w(table_name, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreateTable(database, table_name, measurement_specific_sql);
    }
}
