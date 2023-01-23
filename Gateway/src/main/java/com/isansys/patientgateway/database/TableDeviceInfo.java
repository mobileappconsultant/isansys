package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableDeviceInfo extends Table
{
    // Database table
    public static final String TABLE_NAME = "device_info";
    
    public static final String COLUMN_DEVICE_TYPE = "device_type";
    public static final String COLUMN_HUMAN_READABLE_DEVICE_ID = "human_readable_device_id";
    public static final String COLUMN_BLUETOOTH_ADDRESS = "bluetooth_address";
    public static final String COLUMN_FIRMWARE_VERSION = "firmware_version";
    public static final String COLUMN_FIRMWARE_VERSION_AS_STRING = "firmware_version_as_string";
    public static final String COLUMN_HARDWARE = "hardware";
    public static final String COLUMN_MODEL = "model";
    
    public static final String COLUMN_BY_USER_ID = "by_user_id";

    // Database creation SQL statement
    private static final String CREATE_TABLE_DEVICE_INFO = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            
            + COLUMN_SERVERS_ID + " int, "
            
            + COLUMN_DEVICE_TYPE + " int not null, "
            + COLUMN_HUMAN_READABLE_DEVICE_ID + " int not null, "
            + COLUMN_BLUETOOTH_ADDRESS + " text not null, "
            + COLUMN_FIRMWARE_VERSION + " int not null, "
            + COLUMN_FIRMWARE_VERSION_AS_STRING + " string not null,"
            + COLUMN_HARDWARE + " int not null, " 
            + COLUMN_MODEL + " int not null, "
            + COLUMN_BY_USER_ID + " int not null, " 
            + COLUMN_TIMESTAMP + " int not null,"

            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0,"
            + COLUMN_SENT_TO_SERVER_BUT_FAILED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + " int default 0"

            + ");";

    public static void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE_DEVICE_INFO);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TableDeviceInfo.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
