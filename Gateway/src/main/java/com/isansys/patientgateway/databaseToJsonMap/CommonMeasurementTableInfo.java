package com.isansys.patientgateway.databaseToJsonMap;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class CommonMeasurementTableInfo
{
    @JsonProperty("timestamp")
    public String sql_timestamp;

    @JsonProperty("WrittenToAndroidDatabaseTimestamp")
    public String sql_written_to_android_database_timestamp;

    @JsonProperty("MeasurementExpiryTimestamp")
    public String measure_expiry_sql_timestamp;
}
