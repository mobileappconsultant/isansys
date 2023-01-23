package com.isansys.patientgateway.serverlink.model;

import com.google.gson.JsonObject;
import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.HttpOperationType;
import com.isansys.patientgateway.database.RowRange;

import java.util.ArrayList;

public class ServerPostParameters
{
    public final HttpOperationType http_operation_type;
    public String json_string;
    public ActiveOrOldSession active_or_old_session;

    // Normally only one of the below two are used.
    public int android_database_row;
    public ArrayList<RowRange> row_ranges;

    public ServerPostParameters(HttpOperationType http_operation_type)
    {
        this.http_operation_type = http_operation_type;
        this.active_or_old_session = ActiveOrOldSession.INVALID;
    }

    public ServerPostParameters(HttpOperationType http_operation_type, JsonObject json_object)
    {
        this.http_operation_type = http_operation_type;
        this.active_or_old_session = ActiveOrOldSession.INVALID;
        this.json_string = json_object.toString();
    }

    public ServerPostParameters(HttpOperationType http_operation_type, JsonObject json_object, int android_database_row)
    {
        this.http_operation_type = http_operation_type;
        this.active_or_old_session = ActiveOrOldSession.INVALID;
        this.json_string = json_object.toString();
        this.android_database_row = android_database_row;
    }
}
