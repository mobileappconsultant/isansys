package com.isansys.patientgateway.serverlink.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isansys.common.enums.ServerConfigurableTextStringTypes;

public class GetWebPagesResponse
{
    @SerializedName("Url")
    @Expose
    private String url;

    @SerializedName("Description")
    @Expose
    private String description;

    @SerializedName("StatusCode")
    @Expose
    private Integer status_code;

    @SerializedName("MessageStatus")
    @Expose
    private String message_status;

    public String getUrl()
    {
        return url;
    }
    public String getDescription()
    {
        return description;
    }
}