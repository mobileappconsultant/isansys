package com.isansys.patientgateway.serverlink.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isansys.common.enums.ServerConfigurableTextStringTypes;

public class GetServerConfigurableTextResponse
{
    @SerializedName("ServerConfigurableTextId")
    @Expose
    private Integer server_configurable_text_id;

    @SerializedName("ServerConfigurableTextType")
    @Expose
    private Integer server_configurable_text_type;

    @SerializedName("ConfigurableText")
    @Expose
    private String configurable_text;

    @SerializedName("StatusCode")
    @Expose
    private Integer status_code;

    @SerializedName("MessageStatus")
    @Expose
    private String message_status;

    public Integer getServersId()
    {
        return server_configurable_text_id;
    }

    public ServerConfigurableTextStringTypes getStringType()
    {
        return ServerConfigurableTextStringTypes.values()[server_configurable_text_type];
    }

    public String getStringText()
    {
        return configurable_text;
    }
}