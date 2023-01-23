package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TablePatientDetails extends Table
{
    public static final String TABLE_NAME = "patient_details";
    
    public static final String COLUMN_BY_BED_ID = "by_bed_id";
    public static final String COLUMN_HOSPITAL_PATIENT_ID = "hospital_patient_id";

    // Deprecated and replaced by COLUMN_SERVERS_THRESHOLD_SET_ID and COLUMN_SERVERS_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID
    public static final String COLUMN_AGE_BLOCK = "age_block";
    public static final String COLUMN_SERVERS_THRESHOLD_SET_ID = "servers_threshold_set_id";
    public static final String COLUMN_SERVERS_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID = "servers_threshold_set_age_block_detail_id";
    public static final String COLUMN_BY_USER_ID = "by_user_id";    

    private static final String CREATE_TABLE_PATIENT_DETAILS = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SERVERS_ID + " int, "
            + COLUMN_BY_BED_ID + " int not null, "
            + COLUMN_HOSPITAL_PATIENT_ID + " text not null, "
            + COLUMN_AGE_BLOCK + " int not null, "
            + COLUMN_SERVERS_THRESHOLD_SET_ID + " int not null, "
            + COLUMN_SERVERS_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID + " int not null, "
            + COLUMN_BY_USER_ID + " int not null, "
            + COLUMN_TIMESTAMP + " int not null,"
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP + " int default 0,"
            + COLUMN_SENT_TO_SERVER_BUT_FAILED + " boolean not null default 0, "
            + COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + " int default 0"
            + ");";

    public static void onCreateTable(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE_PATIENT_DETAILS);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(TablePatientDetails.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(database);
    }
}
