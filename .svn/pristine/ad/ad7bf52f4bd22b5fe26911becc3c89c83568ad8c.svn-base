package com.isansys.patientgateway.serverlink.realtimeclient;

import android.content.Context;
import android.util.Pair;

import com.google.gson.Gson;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.serverlink.SslUtil;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

public class MqttClient implements RealTimeClient
{
    private MqttAndroidClient mqttAndroidClient;

    private final String TAG = "MqttClient";
    private final RemoteLoggingWithEnable Log;

    private final String server_uri;

    private final int QoS2 = 2;

    private final boolean use_ssl;

    private final String client_id;

    private final int qos;

    private ResultHandler connection_handler;
    private final HashMap<String, Pair<Class<?>,SubscriptionHandler>> subscriptions = new HashMap<>();

    private final String ca_certificate_path;
//    private final String client_certificate_path;
//    private final String client_key_path;

    private final String username = "Gateway";
    private final String secret = "zeKsIY0dPu23w1QKOZ06hYvxJ3NTI83o";
    private final Context context;

    public MqttClient(String address, String port, RemoteLoggingWithEnable logger, String client_id, Context context, boolean use_ssl)
    {
        this.context = context;
        Log = logger;

        this.client_id = client_id;

        qos = QoS2;

        if (use_ssl)
        {
            server_uri = "ssl://" + address + ":" + port;
        }
        else
        {
            server_uri = "tcp://" + address + ":" + port;
        }

        this.use_ssl = use_ssl;

        ca_certificate_path = context.getFilesDir() + File.separator + "certificates" + File.separator + "ca.crt";
   //     client_certificate_path = context.getFilesDir() + File.separator + "certificates" + File.separator + "client.crt";
   //     client_key_path = context.getFilesDir() + File.separator + "certificates" + File.separator + "client.key";
    }


    @Override
    public boolean isConnected()
    {
        if (mqttAndroidClient != null)
        {
            try
            {
                boolean server_connected = mqttAndroidClient.isConnected();
                Log.d(TAG, "isMqttConnected : server_connected = " + server_connected);
                return server_connected;
            }
            catch (IllegalArgumentException e)
            {
                Log.d(TAG, "isMqttConnected : IllegalArgumentException. Returning FALSE");
                return false;
            }
        }

        return false;
    }

    @Override
    public void connect(ResultHandler handler)
    {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(false);
        mqttConnectOptions.setCleanSession(false);  // Apparently, true keeps a persistent session but doesn't seem to be required here?  https://www.hivemq.com/blog/mqtt-essentials-part-3-client-broker-connection-establishment  http://www.steves-internet-guide.com/mqtt-clean-sessions-example/

        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(secret.toCharArray());

        mqttConnectOptions.setConnectionTimeout(5);


        connection_handler = handler;

        try
        {
            if (use_ssl)
            {
                mqttConnectOptions.setSocketFactory(SslUtil.getSocketFactory(ca_certificate_path)); //, client_certificate_path, client_key_path, "password"));
            }

            mqttAndroidClient = new MqttAndroidClient(context, server_uri, client_id, Ack.AUTO_ACK);

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener()
            {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    Log.d(TAG, "MQTT Connect onSuccess");
/*
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);

                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
*/

                    connection_handler.onEvent(true);

                    if (mqttAndroidClient != null) // Could be null if we call disconnect before result received
                    {
                        mqttAndroidClient.setCallback(callback);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    Log.w(TAG, "Failed to connect to: " + server_uri + "   Exception = " + exception.toString());

                    if (connection_handler != null)
                    {
                        connection_handler.onEvent(false);
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, "connect Exception " + e.getMessage());
            e.printStackTrace();

            if (connection_handler != null)
            {
                connection_handler.onEvent(false);
            }
        }
    }

    @Override
    public void disconnect()
    {
        if(mqttAndroidClient != null)
        {
            try
            {
                Log.d(TAG, "disconnect : unsubscribe, then disconnect client");

                List<String> topics = new ArrayList<>(subscriptions.keySet());
                for(String topic : topics)
                {
                    unsubscribe(topic);
                }

                mqttAndroidClient.disconnect(5000); // allow up to 5000 ms to deal with any pending messages

                Log.d(TAG, "disconnect : disposing of old client resources");

                mqttAndroidClient.unregisterResources();

                mqttAndroidClient = null;

                Log.d(TAG, "disconnect : Completed");
            }
//            catch (Exception e)
//            {
//                Log.e(TAG, "disconnect Mqtt exception : " + e.toString());
//            }
            catch (IllegalArgumentException e)
            {
                Log.e(TAG, "disconnect Illegal Argument exception : " + e.toString());
            }
            catch (NullPointerException e)
            {
                // Can be thrown by the MqttAndroidClient library.
                Log.e(TAG, "disconnect Null Pointer exception : " + e.toString());
            }
            catch (Exception e)
            {
                Log.e(TAG, "disconnect UNEXPECTED exception : " + e.toString());
            }
        }
        else
        {
            Log.d(TAG, "disconnect : client is null");
        }
    }

    @Override
    public void subscribe(final String topicUri, Class<?> eventType, final SubscriptionHandler eventHandler)
    {
        try
        {
            mqttAndroidClient.subscribe(topicUri, qos, null, new IMqttActionListener()
            {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    subscriptions.put(topicUri, new Pair<>(eventType, eventHandler));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    Log.e(TAG, "Subscribe failure to topic_from_server!");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void unsubscribe(String topicUri)
    {
        try
        {
            mqttAndroidClient.unsubscribe(topicUri);

            subscriptions.remove(topicUri);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(String topic, String json_data)
    {
        try
        {
            MqttMessage message = new MqttMessage(json_data.getBytes());

            mqttAndroidClient.publish(topic, message);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void pingConnection(String pingUri, ResultHandler pong_handler)
    {
        String ping_topic = "gateway_ping__" + client_id;

        if(subscriptions.get(ping_topic) == null)
        {
            subscribe(ping_topic, String.class, (topicUri, event) -> {
                Log.d(TAG, "pingConnection onEvent : " + event.toString());

                String received_msg = event.toString();

                pong_handler.onEvent(received_msg.equals(pingUri));
            });
        }

        publish(ping_topic, pingUri);
    }


    final MqttCallbackExtended callback = new MqttCallbackExtended()
    {
        @Override
        public void connectComplete(boolean b, String s)
        {
            Log.d(TAG, "connectComplete : " + s + " connection was reconnect : " + b);

            connection_handler.onEvent(true);
        }

        @Override
        public void connectionLost(Throwable cause)
        {
            if(cause == null)
            {
                Log.w(TAG, "connectionLost : deliberate disconnect");
            }
            else
            {
                Log.w(TAG, "connectionLost : " + cause.toString());
            }

            connection_handler.onEvent(false);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception
        {
            String message_as_a_string = message.toString();

            Log.d(TAG, "messageArrived : " + message_as_a_string);

            Pair<Class<?>, SubscriptionHandler> class_and_handler = subscriptions.get(topic);

            if(class_and_handler != null)
            {
                SubscriptionHandler handler = class_and_handler.second;
                Class<?> type = class_and_handler.first;

                if(type != String.class)
                {
                    Object from_json = new Gson().fromJson(message_as_a_string, type);

                    handler.onEvent(topic, from_json);
                }
                else
                {
                    handler.onEvent(topic, message_as_a_string);
                }
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token)
        {
            Log.d(TAG, "deliveryComplete : " + token.toString());
        }
    };
}
