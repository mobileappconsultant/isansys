package com.isansys.patientgateway.serverlink.realtimeclient;

public interface RealTimeClient
{
    boolean isConnected();

    void connect(ResultHandler handler);

    void disconnect();

    void subscribe(String topicUri, Class<?> eventType, SubscriptionHandler eventHandler);

    void unsubscribe(String topicUri);

    void publish(String topic, String json_data);

    void pingConnection(String pingUri, final ResultHandler pong_handler);
}
