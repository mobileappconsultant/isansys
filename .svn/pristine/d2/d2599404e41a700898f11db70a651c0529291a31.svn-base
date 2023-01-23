package com.isansys.patientgateway.serverlink.realtimeclient;

import com.isansys.patientgateway.RemoteLoggingWithEnable;

import java.util.Dictionary;
import java.util.Hashtable;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampCra;
import de.tavendo.autobahn.WampCraConnection;

public class WampClient implements RealTimeClient
{
    private final WampCra mConnection;
    private final String hostname;
    private final String port;

    private final RemoteLoggingWithEnable Log;
    private final String TAG = "WampClient";

    // flag to show Authentication to WAMP failed so next time we try Un-Authenticated
    private boolean authentication_failed_flag = false;
    private ResultHandler connection_handler;

    private final String gateway_unique_id;

    public WampClient(String address, String port, RemoteLoggingWithEnable logger, String desired_gateway_unique_id)
    {
        mConnection = new WampCraConnection();
        hostname = address;
        this.port = port;

        Log = logger;
        gateway_unique_id = desired_gateway_unique_id;
    }



    @Override
    public boolean isConnected()
    {
        if (mConnection != null)
        {
            return mConnection.isConnected();
        }
        else
        {
            return false;
        }
    }


    @Override
    public void connect(ResultHandler handler)
    {
        connection_handler = handler;

        final String ws_uri = "ws://" + hostname + ":" + port;

        Log.d(TAG, "Connecting to " + ws_uri + " ..");

        //  If we have failed to Authenticate on the previous attempt, the flag will be set so try Un-Authenticated
        //  A connection failure will NOT have set the flag, only an authentication failure post connection
        if (authentication_failed_flag)
        {
            mConnection.connect(ws_uri, getBasicConnectionHandler());
        }
        else
        {
            mConnection.connect(ws_uri, getCraConnectionHandler());
        }
    }


    @Override
    public void disconnect()
    {
        mConnection.disconnect();
    }


    @Override
    public void subscribe(String topicUri, Class<?> eventType, final SubscriptionHandler eventHandler)
    {
        mConnection.subscribe(topicUri, eventType, eventHandler::onEvent);
    }


    @Override
    public void unsubscribe(String topicUri)
    {
        mConnection.unsubscribe(topicUri);
    }


    @Override
    public void publish(String topic, String json_data)
    {
        mConnection.publish(topic, json_data);
    }

    @Override
    public void pingConnection(String pingUri, final ResultHandler pong_handler)
    {
        String blob = "Hello From Gateway";
        mConnection.call(pingUri, String.class,
                new de.tavendo.autobahn.Wamp.CallHandler()
                {
                    @Override
                    public void onResult(Object result)
                    {
                        String res = result.toString();
                        if (res.equals("Pong"))
                        {
                            pong_handler.onEvent(true);
                        }
                        else
                        {
                            pong_handler.onEvent(false);
                        }
                    }

                    @Override
                    public void onError(String error, String info)
                    {
                        Log.e(TAG, "ERROR" + error + info);
                    }
                }, blob);
    }


    private Wamp.ConnectionHandler getCraConnectionHandler()
    {
        Wamp.ConnectionHandler craConnHandler =  new WampCra.ConnectionHandler()
        {
            @Override
            public void onOpen()
            {
                // The connection was successfully established.
                Log.d(TAG, "Connecting to WAMP (Authenticated)" );

                String username = "Gateway";
                String secret = "zeKsIY0dPu23w1QKOZ06hYvxJ3NTI83o";
                Dictionary<String, String> extra = new Hashtable<>();
                //extra.put("salt","somerandomsalttext");  // Salt specification thru autobahn not working.
                extra.put("my_id", gateway_unique_id);

                Log.d(TAG, "Authenticating with WAMP" );

                mConnection.authenticate(new WampCra.AuthHandler()
                {
                    @Override
                    public void onAuthSuccess(Object permissions)
                    {
                        Log.d(TAG,"Authenticated");

                        connection_handler.onEvent(true);
                    }

                    @Override
                    public void onAuthError(String errorUri, String errorDesc)
                    {
                        Log.e(TAG,"Failed to Authenticate: " + errorUri);
                        Log.e(TAG,errorDesc);

                        // Authentication failed, so set the flag so next time we will try without Authentication
                        authentication_failed_flag = true;

                        connection_handler.onEvent(false);
                    }

                }, username, secret, extra);
            }

            @Override
            public void onClose(int code, String reason)
            {
                try
                {
                    connection_handler.onEvent(false);
                }
                catch(Exception e)
                {
                    Log.e(TAG, "Authenticated WAMP onClose failed");
                    Log.e(TAG, e.toString());
                }
            }
        };
        return craConnHandler;
    }

    private Wamp.ConnectionHandler getBasicConnectionHandler()
    {
        Wamp.ConnectionHandler connHandler = new Wamp.ConnectionHandler()
        {
            @Override
            public void onOpen()
            {
                try
                {
                    // The connection was successfully established.
                    Log.d(TAG, "Connected to WAMP (Unauthenticated)");

                    connection_handler.onEvent(true);
                }
                catch(Exception e)
                {
                    Log.e(TAG, "Unauthenticated WAMP onOpen failed");
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onClose(int code, String reason)
            {
                // The connection was closed.
                try
                {
                    connection_handler.onEvent(false);
                }
                catch(Exception e)
                {
                    Log.e(TAG, "Unauthenticated  WAMP onClose failed");
                    Log.e(TAG, e.toString());
                }
            }
        };
        return connHandler;
    }
}
