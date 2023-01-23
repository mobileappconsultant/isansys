package com.isansys.patientgateway.bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateUtils;

import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.remotelogging.RemoteLogging;


/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public abstract class BluetoothService extends Service implements ContextInterface
{
    public final RemoteLogging Log = new RemoteLogging();

    private String TAG = this.getClass().getSimpleName();

    public enum DeviceConnectingMode
    {
        CONNECT_TO_DEVICE,
        LISTEN_FOR_DEVICE,
    }

    private final DeviceConnectingMode device_connection_mode;

    // Constants that indicate the current connection state
    public enum State
    {
        STATE_NONE,
        STATE_LISTEN,
        STATE_CONNECTING,
        STATE_CONNECTED,
        STATE_CONNECTION_TIMEOUT,
    }

    private static final String NAME_SECURE = "SPP Profile";
    private static final UUID UUID_RFCOMM = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private AcceptThread mSecureAcceptThread = null;                                                // Used by AnD only
    private ConnectThread mConnectThread = null;                                                    // Used by Nonin only
    private ConnectedThread mConnectedThread = null;
    private State mState;

    private final boolean auto_reconnect;
    private final boolean auto_start_connected_thread;

    private static final int MESSAGE_STATE_CHANGE    = 1;
    private static final int MESSAGE_READ            = 2;
    private static final int MESSAGE_WRITE           = 3;

    public abstract IntentFilter makeIntentFilter_IncomingFromPatientGateway();
    public abstract void handleIncomingFromPatientGateway(Context context, Intent intent);
    public abstract String getDesiredDeviceBluetoothAddress();
    public abstract String getBluetoothDeviceName();
    public abstract void reportDisconnected();
    public abstract void handleMessageRead(byte[] bluetooth_read_buffer);
    public abstract void handleMessageStateChange(State state);


    public BluetoothService(String parent_name, DeviceConnectingMode device_connection_mode, boolean auto_reconnect, boolean auto_start_connected_thread)
    {
        TAG = TAG + "-" + parent_name;

        this.device_connection_mode = device_connection_mode;

        this.auto_reconnect = auto_reconnect;
        this.auto_start_connected_thread = auto_start_connected_thread;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();

        setState(State.STATE_NONE);

        registerReceiver(btStatusResult, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        registerReceiver(btStatusResult, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));

        registerReceiver(broadcastReceiverIncomingFromPatientGateway, makeIntentFilter_IncomingFromPatientGateway());
    }


    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }


    @Override
    public synchronized void onDestroy()
    {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        start_reconnection_handler.removeCallbacksAndMessages(null);

        unregisterReceiver(btStatusResult);

        unregisterReceiver(broadcastReceiverIncomingFromPatientGateway);

        stopThreads();
    }


    private final BroadcastReceiver broadcastReceiverIncomingFromPatientGateway = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            handleIncomingFromPatientGateway(context, intent);
        }
    };


    private final BroadcastReceiver btStatusResult = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (!device_removed_from_session)
            {
                String action = intent.getAction();

                String gateway_device_address = getDesiredDeviceBluetoothAddress();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String incoming_device_address = device.getAddress();

                if (gateway_device_address.equals(incoming_device_address))
                {
                    State state = getState();

                    if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED))
                    {
                        Log.d(TAG, "ACTION_ACL_CONNECTED received : bluetooth_service.getState() = " + state);

                        restarting_device_connection = false;

                        start_reconnection_handler.removeCallbacks(restart_connection_runnable);

                        Log.i(TAG, "ACTION_ACL_CONNECTED: Added device address is " + gateway_device_address);

                        if (device_connection_mode == DeviceConnectingMode.LISTEN_FOR_DEVICE)
                        {
                            if (state == State.STATE_CONNECTION_TIMEOUT)
                            {
                                Log.d(TAG, "Listener timed out so starting listening again.");

                                startListening();
                            }
                        }
                    }
                    else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
                    {
                        Log.d(TAG, "ACTION_ACL_DISCONNECTED received : bluetooth_service.getState() = " + state + " : device_connection_mode = " + device_connection_mode);

                        reportDisconnected();

                        if (device_connection_mode == DeviceConnectingMode.CONNECT_TO_DEVICE)
                        {
                            // Check if the disconnection is because of endSession/ChangeSession, desired_device_connection_status is set to NOT_PAIRED in
                            if (device_removed_from_session)
                            {
                                Log.w(TAG, "Removing Device from session. Ignore this disconnection for device address = " + getDesiredDeviceBluetoothAddress());
                            }
                            else
                            {
                                Log.i(TAG, "Device disconnected unexpectedly. Device address = " + getDesiredDeviceBluetoothAddress());

                                if (auto_reconnect)
                                {
                                    Log.i(TAG, "Device reconnecting. Device address = " + getDesiredDeviceBluetoothAddress());

                                    startConnecting(device);
                                }
                                else
                                {
                                    Log.i(TAG, "auto_reconnect is false. Device address = " + getDesiredDeviceBluetoothAddress());
                                }
                            }
                        }
                    }
                }
                else
                {
                    Log.d(TAG, "received address = " + incoming_device_address + " but desired address = " + gateway_device_address);
                }
            }
            else
            {
                Log.d(TAG, "action received for removed device");
            }
        }
    };


    public static final Handler start_reconnection_handler = new Handler();


    public void connectToDeviceIfPaired(String device_name)
    {
        BluetoothAdapter bluetooth_adaptor = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth_adaptor != null)
        {
            Set<BluetoothDevice> pairedDevices = bluetooth_adaptor.getBondedDevices();
            if (pairedDevices.size() > 0)
            {
                for (BluetoothDevice device : pairedDevices)
                {
                    if (device.getName().contains(device_name))
                    {
                        Log.w(TAG, "Starting scan");

                        restarting_device_connection = false;

                        startConnecting(device);

                        break;
                    }
                }
            }
        }
    }


    public final Runnable restart_connection_runnable = new Runnable()
    {
        @Override
        public void run()
        {
            if(device_removed_from_session)
            {
                Log.w(TAG, "Nonin removed from session - not starting connection.");
                restarting_device_connection = false;
            }
            else
            {
                connectToDeviceIfPaired(getBluetoothDeviceName());

                if(restarting_device_connection)
                {
                    Log.w(TAG, "Couldn't start scan - trying again in (" + reconnection_time_in_milliseconds/(int)DateUtils.SECOND_IN_MILLIS + " seconds) to attempt reconnection");
                    // Need to keep trying - post the runnable again.
                    start_reconnection_handler.postDelayed(restart_connection_runnable, reconnection_time_in_milliseconds);
                }
            }
        }
    };


    public static final int reconnection_time_in_milliseconds = 15 * (int) DateUtils.SECOND_IN_MILLIS;


    private volatile boolean device_removed_from_session = true;
    public volatile boolean restarting_device_connection = false;

    public void setDeviceRemovedFromSession()
    {
        device_removed_from_session = true;
        restarting_device_connection = false;

        start_reconnection_handler.removeCallbacksAndMessages(null);
    }


    public void setDeviceAddedToSession()
    {
        device_removed_from_session = false;
    }


    protected synchronized void setState(State state)
    {
        mState = state;

        try
        {
            Log.d(TAG, "BluetoothService.MESSAGE_STATE_CHANGE : New State = " + state);

            handler.obtainMessage(MESSAGE_STATE_CHANGE, state.ordinal(), -1).sendToTarget();
        }
        catch (Exception e)
        {
            Log.e(TAG, "setState Exception : " + e.toString());
        }
    }

    /**
     * Return the current connection state.
     */
    public synchronized State getState()
    {
        return mState;
    }


    // AnD only
    /**
     * Start AcceptThread to begin a session in listening (server) mode
     */
    public synchronized void startListening()
    {
        Log.d(TAG, "BluetoothService : startListening");

        stopThreads();

        setState(State.STATE_LISTEN);

        mSecureAcceptThread = new AcceptThread();
        mSecureAcceptThread.start();
    }


    private synchronized void cancelConnectThread()
    {
        if (mConnectThread != null)
        {
            if(mConnectThread.isAlive())
            {
                Log.d(TAG, "mConnectThread.cancel()");
                mConnectThread.cancel();
            }
        }
    }


    private synchronized void cancelConnectedThread()
    {
        if (mConnectedThread != null)
        {
            if(mConnectedThread.isAlive())
            {
                Log.d(TAG, "mConnectedThread.cancel()");
                mConnectedThread.cancel();
            }
        }
    }


    private synchronized void cancelSecureAcceptThread()
    {
        if (mSecureAcceptThread != null)
        {
            if(mSecureAcceptThread.isAlive())
            {
                Log.d(TAG, "mSecureAcceptThread.cancel()");
                mSecureAcceptThread.cancel();
            }
        }
    }


    private synchronized void stopThreads()
    {
        Log.d(TAG, "stopThreads");

        cancelConnectThread();

        cancelConnectedThread();

        cancelSecureAcceptThread();
    }


    // Called by AnD ACTION_ACL_DISCONNECTED
    public void stopThreadsAndSetStateToNone()
    {
        Log.e(TAG, "stopThreadsAndSetStateToNone");

        stopThreads();

        setState(State.STATE_NONE);
    }


    // Nonin Only
    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    private synchronized void startConnecting(BluetoothDevice device)
    {
        // Cancel any thread attempting to make a connection
        cancelConnectThread();

        // Cancel any thread currently running a connection
        cancelConnectedThread();

        setState(State.STATE_CONNECTING);

        Log.d(TAG, "startConnecting : Creating ConnectThread");

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }


    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
    {
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        cancelConnectThread();

        // Cancel any thread currently running a connection
        cancelConnectedThread();

        // Cancel the accept thread because we only want to connect to one device
        cancelSecureAcceptThread();

        try
        {
            Log.d(TAG, "connected : Creating ConnectedThread for " + device.getName() + " : auto_start_connected_thread = " + auto_start_connected_thread);

            if (auto_start_connected_thread)
            {
                // Start the thread to manage the connection and perform transmissions
                mConnectedThread = new ConnectedThread(socket);
                mConnectedThread.start();
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "connected : Exception e = " + e.toString());

            setState(State.STATE_NONE);
        }
    }


    private synchronized void logErrorAndGotoStateConnectionTimeout(String string)
    {
        Log.e(TAG, "logErrorAndGotoStateConnectionTimeout : " + string);
        
        //setState(STATE_NONE);
        setState(State.STATE_CONNECTION_TIMEOUT);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out)
    {
        try
        {
            // Create temporary object
            ConnectedThread cThread;

            // Synchronize a copy of the ConnectedThread            
            synchronized (this)
            {
                if (mState != State.STATE_CONNECTED)
                {
                    Log.e(TAG, "write : not in STATE_CONNECTED so returning. State = " + mState);
                    return;
                }
                cThread = mConnectedThread;
            }

            // Perform the write unsynchronized
            cThread.write(out);
        }
        catch (Exception e)
        {
            Log.e(TAG, "write Exception : " + e.toString());
        }
    }


    // This is really a ListenThread
    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread
    {
        // The local server socket
        private BluetoothServerSocket mmServerSocket = null;

        public AcceptThread()
        {
            BluetoothServerSocket tmp;

            // Create a new listening server socket
            try
            {
                Log.d(TAG, "AcceptThread : AcceptThread starting listenUsingRfcommWithServiceRecord");

                tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(NAME_SECURE, UUID_RFCOMM);
            }
            catch (IOException e)
            {
                logErrorAndGotoStateConnectionTimeout("AcceptThread : Creation of RFCOMM socket Failed!\n");
                return;
            }

            mmServerSocket = tmp;
        }

        public void run()
        {
            Log.d(TAG, "AcceptThread : run");

            setName("AcceptThread-" + TAG);

            while (mState != BluetoothService.State.STATE_CONNECTED)
            {
                try
                {
                    if (mmServerSocket != null)
                    {
/*
                        int TIMEOUT = 30;
                        int TIMEOUT_IN_MILLISECONDS = (int)(TIMEOUT * DateUtils.SECOND_IN_MILLIS);

                        Log.d(TAG, "AcceptThread : accept. Waits for device to connect. Timeout = " + TIMEOUT);

                        // This is a blocking call and will only return on a successful connection or an exception
                        BluetoothSocket socket = mmServerSocket.accept(TIMEOUT_IN_MILLISECONDS);
*/
                        Log.d(TAG, "AcceptThread : accept. Waits for device to connect");

                        // This is a blocking call and will only return on a successful connection or an exception
                        BluetoothSocket socket = mmServerSocket.accept();

                        Log.d(TAG, "AcceptThread: got response");

                        // If a connection was accepted
                        synchronized (BluetoothService.this)
                        {
                            Log.d(TAG, "AcceptThread : mState = " + mState.toString());

                            switch (mState)
                            {
                                case STATE_LISTEN:
                                case STATE_CONNECTING:
                                {
                                    Log.d(TAG, "AcceptThread : mState = STATE_LISTEN or STATE_CONNECTING : Situation normal. Start the connected thread.");

                                    connected(socket, socket.getRemoteDevice());
                                }
                                break;

                                case STATE_NONE:
                                case STATE_CONNECTED:
                                {
                                    try
                                    {
                                        Log.e(TAG, "AcceptThread: mState = STATE_NONE or STATE_CONNECTED : Either not ready or already connected. Terminate new socket.");

                                        socket.close();
                                    }
                                    catch (IOException e)
                                    {
                                        Log.e(TAG, "run IOException : " + e.toString());
                                    }
                                }
                                break;

                                default:
                                    break;
                            }
                        }
                    }
                }
                catch (IOException e)
                {
                    logErrorAndGotoStateConnectionTimeout("AcceptThread run : Unable connect to remote device! Aborted or Timed out");
                    return;
                }
            }
        }

        public void cancel()
        {
            Log.d(TAG, "AcceptThread : Cancel the AcceptThread() and closing 'mmServerSocket'");

            try
            {
                if (mmServerSocket != null)
                {
                    mmServerSocket.close();
                }
            }
            catch (IOException e)
            {
                Log.e(TAG, "AcceptThread : Cancel Exception : " + e.toString());
            }
        }
    }


    // Nonin only
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread
    {
        private final BluetoothDevice mmDevice;
        private BluetoothSocket mmSocket = null;

        public ConnectThread(BluetoothDevice device)
        {
            mmDevice = device;
            BluetoothSocket tmp;

            Log.d(TAG, "ConnectThread : ConnectThread");

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try
            {
                Log.d(TAG, "ConnectThread : createRfcommSocketToServiceRecord");
                tmp = device.createRfcommSocketToServiceRecord(UUID_RFCOMM);
            }
            catch (IOException e)
            {
                logErrorAndGotoStateConnectionTimeout("ConnectThread : Creation of RFCOMM socket Failed!\n");
                return;
            }

            mmSocket = tmp;
        }

        public void run()
        {
            Log.d(TAG, "ConnectThread : run");

            setName("ConnectThread-" + TAG);

            if (BluetoothAdapter.getDefaultAdapter().isDiscovering())
            {
                // Always cancel discovery because it will slow down a connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            }

            // Make a connection to the BluetoothSocket
            try
            {
                // This is a blocking call and will only return on a successful connection or an exception
                mmSocket.connect();
            }
            catch (IOException e)
            {
                // Close the socket
                Log.w(TAG, "mmSocket reConnect 'timeOut'/connect() exception.     e = " + e.toString());
                try
                {
                    mmSocket.close();
                }
                catch (IOException e2)
                {
                    Log.e(TAG, "run Exception : " + e2.toString());
                }

                logErrorAndGotoStateConnectionTimeout("ConnectThread run : Unable connect to remote device!\n");
                return;
            }
            catch (NullPointerException npe)
            {
                logErrorAndGotoStateConnectionTimeout("ConnectThread run : socket is null!\n");
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this)
            {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel()
        {
            Log.d(TAG, "ConnectThread : Cancel and closing 'mmSocket'");

            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "cancel Exception : " + e.toString());
            }
        }
    }


    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread
    {
        public BluetoothSocket mmSocket;
        public DataInputStream mmInStream = null;
        public DataOutputStream mmOutStream = null;
        public volatile boolean flag_read_stream_run = true;

        public ConnectedThread(BluetoothSocket socket)
        {
            Log.d(TAG, "ConnectedThread : ConnectedThread");

            mmSocket = socket;
            DataInputStream tmpIn;
            DataOutputStream tmpOut;

            // Get the BluetoothSocket input and output streams
            try
            {
                tmpIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            }
            catch (IOException e)
            {
                logErrorAndGotoStateConnectionTimeout("Unable to create streams in RFCOMM socket for BufferedInputStream!\n");
                return;
            }

            try
            {
                tmpOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            }
            catch (IOException e)
            {
                logErrorAndGotoStateConnectionTimeout("Unable to create streams in RFCOMM socket for BufferedOutputStream !\n");
                return;
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            Log.d(TAG, "ConnectedThread : device connection state = STATE_CONNECTED");
            setState(BluetoothService.State.STATE_CONNECTED);
        }

        public void run()
        {
            Log.d(TAG, "ConnectedThread : run");

            while (flag_read_stream_run)
            {
                if (dataAvailable() > 0)
                {
                    try
                    {
                        byte[] data_to_send = dataReceive();

                        if (flag_read_stream_run)
                        {
                            handler.obtainMessage(MESSAGE_READ, -1, -1, data_to_send).sendToTarget();
                        }
                    }
                    catch (Exception e)
                    {
                        logErrorAndGotoStateConnectionTimeout("Error!\nIncoming data reading fail!\n");
                    }
                }

                try
                {
                    Thread.sleep(DateUtils.SECOND_IN_MILLIS);
                }
                catch (InterruptedException e)
                {
                    cancel();
                    flag_read_stream_run = false;
                    Log.e(TAG, "Stopping the socket thread because of disconnect. Run InterruptedException : " + e.toString());
                    return;
                }
            }
        }


        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public synchronized void write(byte[] buffer)
        {
            if(mmOutStream != null)
            {
                try
                {
                    mmOutStream.write(buffer, 0, buffer.length);
                    mmOutStream.flush();

                    Log.d(TAG, "BluetoothService.MESSAGE_WRITE : " + Utils.byteArrayToHexString(buffer));

                    handler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
                }
                catch (IOException e)
                {
                    logErrorAndGotoStateConnectionTimeout("Error!\nOutgoing data writing fail!\n");
                }
            }
            else
            {
                Log.e(TAG, "write : mmOutStream is NULL");
                logErrorAndGotoStateConnectionTimeout("Error! output Socket. Restarting the connection");
            }
        }

        public void cancel()
        {
            if(mmOutStream != null)
            {
                try
                {
                    Log.i(TAG, "ConnectedThread : cancel : close mmOutStream");

                    mmOutStream.close();
                    mmOutStream = null;
                }
                catch (IOException e)
                {
                    Log.e(TAG, "mmOutStream : cancel IOException : " + e.toString());
                }
            }

            if(mmInStream != null)
            {
                try
                {
                    Log.i(TAG, "ConnectedThread : cancel : close mmInStream");

                    mmInStream.close();
                    mmInStream = null;
                }
                catch (IOException e)
                {
                    Log.e(TAG, "mmInStream : cancel IOException : " + e.toString());
                }
            }

            if(mmSocket != null)
            {
                try
                {
                    Log.i(TAG, "ConnectedThread : cancel : close mmSocket");

                    mmSocket.close();
                    mmSocket = null;
                }
                catch (IOException e)
                {
                    Log.e(TAG, "mmSocket : cancel IOException : " + e.toString());
                }
            }
        }


        public int dataAvailable()
        {
            if (mmInStream != null)
            {
                try
                {
                    return mmInStream.available();
                }
                catch (IOException e)
                {
                    Log.e(TAG, "dataAvailable IOException : " + e.toString());
                }
            }

            flag_read_stream_run = false;

            Log.e(TAG, "ConnectedThread : dataAvailable : mmInStream null");

            return 0;
        }


        public synchronized byte[] dataReceive()
        {
            byte[] return_value = new byte[0];

            try
            {
                int avail = mmInStream.available();
                return_value = new byte[avail];

                //noinspection ResultOfMethodCallIgnored
                mmInStream.read(return_value);
            }
            catch (IOException e)
            {
                flag_read_stream_run = false;
                logErrorAndGotoStateConnectionTimeout("Error!\nIncoming data reading fail!\n");
            }

            return (return_value);
        }
    }


    public Context getAppContext()
    {
        return getApplicationContext();
    }


    public void sendBroadcastIntent(Intent intent)
    {
        try
        {
            sendBroadcast(intent);
        }
        catch(Exception ex)
        {
            android.util.Log.e("sendBroadcastIntent", "Send broadcast failed: ", ex);
        }
    }


    private static class MyHandler extends Handler
    {
        private final WeakReference<BluetoothService> service_weak_reference;

        public MyHandler(BluetoothService service)
        {
            service_weak_reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg)
        {
            BluetoothService service = service_weak_reference.get();

            switch (msg.what)
            {
                case MESSAGE_STATE_CHANGE:
                {
                    service.handleMessageStateChange(State.values()[msg.arg1]);
                }
                break;

                case MESSAGE_WRITE:
                {
                }
                break;

                case MESSAGE_READ:
                {
                    service.handleMessageRead((byte[]) msg.obj);
                }
                break;

                default:
                    service.Log.d(service.TAG, "Unknown handleMessage message : " + msg.what);
                    break;
            }
        }
    }


    private final MyHandler handler = new MyHandler(this);
}
