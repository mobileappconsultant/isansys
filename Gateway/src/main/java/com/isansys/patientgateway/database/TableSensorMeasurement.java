package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableSensorMeasurement extends Table
{
    void onCreateTable(SQLiteDatabase database, String table_name, String measurement_specific_sql)
    {
        String create_new_database_table = "create table " + table_name + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_DEVICE_SESSION_NUMBER + " int not null,"
                + COLUMN_HUMAN_READABLE_DEVICE_ID + " int not null, "
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


    void createIndices(SQLiteDatabase database, String table_name)
    {
        database.execSQL("DROP INDEX IF EXISTS " + table_name + "_device_session_number;");
        database.execSQL("CREATE INDEX IF NOT EXISTS " + table_name + "_session_and_syncing on " + table_name + "(" + COLUMN_DEVICE_SESSION_NUMBER + ", " + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + ", " + COLUMN_SENT_TO_SERVER_BUT_FAILED + ");");
        database.execSQL("DROP INDEX IF EXISTS " + table_name + "_pending_or_failed;");

    }
}
