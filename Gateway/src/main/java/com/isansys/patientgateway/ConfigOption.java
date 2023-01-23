package com.isansys.patientgateway;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConfigOption
{
    @SerializedName("OptionName")
    @Expose
    public final String option_name;

    @SerializedName("OptionValue")
    @Expose
    public final String option_value;

    public ConfigOption(String option_name, String option_value)
    {
        this.option_name = option_name;
        this.option_value = option_value;
    }
}
