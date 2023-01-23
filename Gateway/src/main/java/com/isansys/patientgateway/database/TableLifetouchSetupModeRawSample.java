package com.isansys.patientgateway.database;

import android.database.sqlite.SQLiteDatabase;

public class TableLifetouchSetupModeRawSample extends TableSetupModeData
{
    public static final String TABLE_NAME = "lifetouch_setup_mode_sample";

    public void onCreateTable(SQLiteDatabase database)
    {
        super.onCreateTable(database, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);

        super.createIndices(database, TABLE_NAME);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME, MEASUREMENT_SPECIFIC_SQL);

        super.createIndices(database, TABLE_NAME);
    }
}
