package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableAuditTrail extends Table {

    public static final String TABLE_NAME = "audit_trail";

    public static final String COLUMN_EVENT = "event";   // defined in AuditTrailEvent enum
    public static final String COLUMN_ADDITIONAL = "additional";

    public static final String COLUMN_BLUETOOTH_ADDRESS = "bluetooth_address";
    public static final String COLUMN_GATEWAY_NAME = "gateway_name";
    public static final String COLUMN_BED_ID = "bed_id";

    // Database creation SQL statement
    private final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_BLUETOOTH_ADDRESS + " text not null, "
            + COLUMN_GATEWAY_NAME + " text not null, "
            + COLUMN_BED_ID + " text not null,"
            + COLUMN_TIMESTAMP + " int not null,"
            + COLUMN_EVENT + " int, "
            + COLUMN_BY_USER_ID + " int not null, "
            + COLUMN_ADDITIONAL + " string, "
            + COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP + " int default 0,"
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0,"
            + COLUMN_SENT_TO_SERVER_BUT_FAILED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + " int default 0"

            + ");";


    public void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE);
    }


    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TableAuditTrail.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }

}
