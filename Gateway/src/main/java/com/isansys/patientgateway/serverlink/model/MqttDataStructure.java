package com.isansys.patientgateway.serverlink.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isansys.patientgateway.serverlink.MessageType;

import java.util.UUID;

public class MqttDataStructure
{
    @SerializedName("MessageType")
    @Expose
    public final MessageType message_type;

    @SerializedName("JSON")
    @Expose
    public final String json;

    @SerializedName("ResponseTopic")
    @Expose
    public String response_topic;

    @SerializedName("MessageUniqueID")
    @Expose
    public String message_unique_id;

    @SerializedName("BedID")
    @Expose
    public String bed_id;

    @SerializedName("GatewayID")
    @Expose
    public String gateway_id;

    public ServerPostParameters server_post_parameters;

    public final static String WEBSERVICE_NAME = "webservice_name";
    public final static String RESULT = "result";
    public final static String PAYLOAD = "payload";

    public long timeout_milliseonds;

    public MqttDataStructure(MessageType message_type, String gateway_id, String bed_id, JsonObject json_object, long timeout_milliseonds)
    {
        this.message_type = message_type;
        this.json = json_object.toString();
        this.message_unique_id = UUID.randomUUID().toString();
        this.timeout_milliseonds = timeout_milliseonds;
        this.bed_id = bed_id;
        this.gateway_id = gateway_id;
    }


    public MqttDataStructure(ServerPostParameters mqtt_post_parameters, long timeout_milliseonds)
    {
        this.message_type = MessageType.WEBSERVICE_CALL;

        this.server_post_parameters = mqtt_post_parameters;

        JsonObject json_object = new JsonObject();

        json_object.addProperty(WEBSERVICE_NAME, mqtt_post_parameters.http_operation_type.name());
        json_object.addProperty(PAYLOAD, mqtt_post_parameters.json_string);

        this.json = json_object.toString();

        this.message_unique_id = UUID.randomUUID().toString();

        this.timeout_milliseonds = timeout_milliseonds;
    }


    @Override
    public String toString()
    {
        return "MqttDataStructure{" +
                "message_type=" + message_type +
                ", json='" + json + '\'' +
                ", response_topic='" + response_topic + '\'' +
                ", message_unique_id='" + message_unique_id + '\'' +
                ", bed_id='" + bed_id + '\'' +
                ", gateway_id='" + gateway_id + '\'' +
                '}';
    }


    public boolean timedOutAfterTick(long tick_length)
    {
        timeout_milliseonds = timeout_milliseonds - tick_length;

        return timeout_milliseonds <= 0;
    }
}
