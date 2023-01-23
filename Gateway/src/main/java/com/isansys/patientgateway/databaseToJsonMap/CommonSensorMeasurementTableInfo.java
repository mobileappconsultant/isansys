package com.isansys.patientgateway.databaseToJsonMap;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class CommonSensorMeasurementTableInfo extends CommonMeasurementTableInfo
{
    @JsonProperty("ByDevSessionId")
    public String servers_device_session_id;
}
