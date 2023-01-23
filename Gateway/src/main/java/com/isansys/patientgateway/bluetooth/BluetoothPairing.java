
package com.isansys.patientgateway.bluetooth;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;

import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

// This class does two separate things. First off the normal class part of it does the Bluetooth search for the specific device (address passed in)
// If its found, then it starts itself on a Thread which does the "Runnable" part. This does the pairing and PIN number entry (if needed)

public class BluetoothPairing implements Runnable
{
    private final RemoteLogging Log;
    private final ContextInterface gateway_context_interface;
    private final String TAG = "BluetoothPairing";

    public final static String BLUETOOTH_PAIRING__SEARCHING = "com.isansys.patientgateway.BLUETOOTH_PAIRING__SEARCHING";
    public final static String BLUETOOTH_PAIRING__DESIRED_DEVICE_FOUND = "com.isansys.patientgateway.BLUETOOTH_PAIRING__DESIRED_DEVICE_FOUND";
    public final static String BLUETOOTH_PAIRING__DESIRED_DEVICE_NOT_FOUND = "com.isansys.patientgateway.BLUETOOTH_PAIRING__DESIRED_DEVICE_NOT_FOUND";
    public final static String BLUETOOTH_PAIRING__PAIRING_STARTED = "com.isansys.patientgateway.BLUETOOTH_PAIRING__PAIRING_STARTED";
    public final static String BLUETOOTH_PAIRING__PAIRING_SUCCESS = "com.isansys.patientgateway.BLUETOOTH_PAIRING__PAIRING_SUCCESS";
    public final static String BLUETOOTH_PAIRING__PAIRING_FAILED = "com.isansys.patientgateway.BLUETOOTH_PAIRING__PAIRING_FAILED";

    // Below are the variables for the Searching part
    private static BluetoothAdapter bluetoothAdapter;

    private volatile String desired_bluetooth_device_address = "";
    private String pin = "";
    private boolean desired_device_found_on_scan = false;

    private Thread thread_bluetooth_pairing = null;

    // Below are the variables for the Pairing part
    private BluetoothDevice bluetooth_device;

    private static final int PAIRING_FAILED = 1;
    private static final int PAIRING_WAIT = 3;

    private AtomicInteger bluetooth_pairing_counter = new AtomicInteger();



    public BluetoothPairing(ContextInterface context_interface, RemoteLogging logger)
    {
        gateway_context_interface = context_interface;

        Log = logger;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    public void searchAndPair(String bluetooth_device_address, String passed_in_pin)
    {
        desired_bluetooth_device_address = bluetooth_device_address;

        pin = passed_in_pin;

        Log.d(TAG, "searchAndPair: bluetooth_device_address = " + bluetooth_device_address + " : bluetooth_device_address = " + bluetooth_device_address);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled())
        {
            bluetoothRegister();
        }

        bluetoothCancelDiscoveryIfRequired();

        if (unpairSpecificDevice(bluetooth_device_address))
        {
            Log.e(TAG, "searchAndPair : Unpair Success");
        }
        else
        {
            Log.e(TAG, "searchAndPair : Unpair Failed - may not have been paired to start with");
        }

        // http://developer.android.com/reference/android/bluetooth/BluetoothAdapter.html#startDiscovery() states that this is approx 12 seconds
        bluetoothAdapter.startDiscovery();
    }


    private void bluetoothRegister()
    {
        gateway_context_interface.getAppContext().registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        gateway_context_interface.getAppContext().registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        gateway_context_interface.getAppContext().registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
    }


    public void bluetoothCancelAndUnregister()
    {
        try
        {
            gateway_context_interface.getAppContext().unregisterReceiver(bluetoothBroadcastReceiver);
        }
        catch (Exception e)
        {
            Log.e(TAG, "bluetoothCancelAndUnregister Exception : " + e.toString());
        }

        bluetoothCancelDiscoveryIfRequired();
    }


    private final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (action != null)
            {
                Log.d(TAG, "Event fired: " + action);

                if(!desired_bluetooth_device_address.equals(""))
                {
                    switch (action)
                    {
                        case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        {
                            desired_device_found_on_scan = false;

                            reportBluetoothStartedSearching();
                        }
                        break;

                        case BluetoothDevice.ACTION_FOUND:
                        {
                            BluetoothDevice bluetooth_device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                            String bluetooth_friendly_name = bluetooth_device.getName();

                            Log.e(TAG, "ACTION_FOUND : " + bluetooth_friendly_name);

                            if (bluetooth_device.getBondState() != BluetoothDevice.BOND_BONDED)
                            {
                                String found_bluetooth_device_address = bluetooth_device.getAddress();

                                Log.d(TAG, "Found device = " + bluetooth_friendly_name + ". MAC Address = " + found_bluetooth_device_address);

                                if (found_bluetooth_device_address.equals(desired_bluetooth_device_address))
                                {
                                    if (desired_device_found_on_scan == false)
                                    {
                                        bluetoothCancelDiscoveryIfRequired();

                                        reportBluetoothDesiredDeviceFound();

                                        desired_device_found_on_scan = true;

                                        pairDevice(bluetooth_device);
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Found device but NOT pairing as pairing already in progress");
                                    }
                                }
                            }
                        }
                        break;

                        case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        {
                            Log.d(TAG, "ACTION_DISCOVERY_FINISHED");

                            if (desired_device_found_on_scan)
                            {
                                Log.e(TAG, "Bluetooth device : " + desired_bluetooth_device_address + " found during Scan");
                            }
                            else
                            {
                                Log.e(TAG, "Bluetooth device : " + desired_bluetooth_device_address + " NOT found during Scan");
                                reportBluetoothDesiredDeviceNotFound();
                            }

                            // Now unregister the receiver so that it doesn't fire unless discovery is started again.
                            gateway_context_interface.getAppContext().unregisterReceiver(bluetoothBroadcastReceiver);
                        }
                        break;
                    }
                }
            }
        }
    };


    private void reportBluetoothStartedSearching()
    {
        Intent outgoing_intent = new Intent(BLUETOOTH_PAIRING__SEARCHING);
        outgoing_intent.putExtra("bluetooth_address", desired_bluetooth_device_address);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    private void reportBluetoothDesiredDeviceNotFound()
    {
        Intent outgoing_intent = new Intent(BLUETOOTH_PAIRING__DESIRED_DEVICE_NOT_FOUND);
        outgoing_intent.putExtra("bluetooth_address", desired_bluetooth_device_address);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    private void reportBluetoothDesiredDeviceFound()
    {
        Intent outgoing_intent = new Intent(BLUETOOTH_PAIRING__DESIRED_DEVICE_FOUND);
        outgoing_intent.putExtra("bluetooth_address", desired_bluetooth_device_address);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    private void reportBluetoothPairingStarted()
    {
        Intent outgoing_intent = new Intent(BLUETOOTH_PAIRING__PAIRING_STARTED);
        outgoing_intent.putExtra("bluetooth_address", desired_bluetooth_device_address);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    private void reportBluetoothPairingSuccess()
    {
        Intent outgoing_intent = new Intent(BLUETOOTH_PAIRING__PAIRING_SUCCESS);
        outgoing_intent.putExtra("bluetooth_address", desired_bluetooth_device_address);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    private void reportBluetoothPairingFailed()
    {
        Intent outgoing_intent = new Intent(BLUETOOTH_PAIRING__PAIRING_FAILED);
        outgoing_intent.putExtra("bluetooth_address", desired_bluetooth_device_address);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    private boolean unpairSpecificDevice(String bluetooth_device_address_to_unpair)
    {
        boolean cleared = false;

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        try
        {
            Log.d(TAG, "unpairSpecificDevice : Number of bonded devices =  " + bondedDevices.size());
            
            for (BluetoothDevice bluetooth_device : bondedDevices)
            {
                String bluetooth_address = bluetooth_device.getAddress();

                if (bluetooth_address.equals(bluetooth_device_address_to_unpair))
                {
                	Log.d(TAG, "unpairSpecificDevice : Unpairing " + bluetooth_device_address_to_unpair);
                    cleared = removeBond(bluetooth_device);
                    break;
                }
            }
        }
        catch (Throwable th)
        {
            Log.e(TAG, "unpairSpecificDevice : Throwable = " + th.getMessage());
        }

        if (cleared)
        {
            Log.d(TAG, "unpairSpecificDevice was able to unpair device");
            return true;
        }
        else
        {
            Log.d(TAG, "unpairSpecificDevice didn't unpair device (maybe not paired before)");
            return false;
        }
    }
    
    
    public boolean unpairAllPairedDevices()
    {
        try
        {
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

            Log.d(TAG, "unpairAllPairedDevices : Number of bonded devices =  " + bondedDevices.size());

            for (BluetoothDevice bluetooth_device : bondedDevices)
            {
                String deviceDetails = bluetooth_device.getName() + " = " + bluetooth_device.getAddress() + " : Type = " + bluetooth_device.getType();

                if (bluetooth_device.getName().toLowerCase().contains("keyboard"))
                {
                    Log.d(TAG, "unpairAllPairedDevices : NOT unpairing device as its a Keyboard : " + deviceDetails);
                }
                else
                {
                    Log.d(TAG, "unpairAllPairedDevices : Unpairing " + deviceDetails);
                    removeBond(bluetooth_device);
                }
            }
        }
        catch (Throwable th)
        {
            Log.e(TAG, th.getMessage());
            Log.e(TAG, "Error! : unpairAllPairedDevices Failed!");
            return false;
        }

        Log.d(TAG, "unpairAllPairedDevices : All devices unpaired!");

        return true;
    }


    private void pairDevice(BluetoothDevice bluetooth_device)
    {
        Log.d(TAG, "pairDevice : Attempting to pair " + bluetooth_device.getName() + " : " + bluetooth_device.getAddress());

        this.bluetooth_device = bluetooth_device;
        this.bluetooth_pairing_counter = new AtomicInteger();

        this.thread_bluetooth_pairing = new Thread(BluetoothPairing.this);
        this.thread_bluetooth_pairing.start();
    }

    
    // Below is the "Runnable part of the Class. This is what is run on the above thread in pairDevice()
    
    @Override
    public void run()
    {
        Log.d(TAG, "Starting Pairing attempt for " + bluetooth_device.getAddress());

        reportBluetoothPairingStarted();

        try
        {
            bluetooth_device.createBond();

            handler_pairing.sendEmptyMessageDelayed(PAIRING_WAIT, 1500);
        }
        catch (Exception e)
        {
            Log.e(TAG, "run : Exception : Pairing Failed! " + e.getMessage());

            pairingFailed();
        }
    }


    private static class MyHandler extends Handler
    {
        private final WeakReference<BluetoothPairing> runnable_weak_reference;

        MyHandler(BluetoothPairing runnable)
        {
            runnable_weak_reference = new WeakReference<>(runnable);
        }

        public void handleMessage(Message msg)
        {
            BluetoothPairing runnable = runnable_weak_reference.get();

            runnable.handleMessage(msg);
        }
    }


    private boolean removeBond(BluetoothDevice bluetooth_device) throws Exception
    {
        Method method = bluetooth_device.getClass().getMethod("removeBond");
        boolean success = (Boolean) method.invoke(bluetooth_device);

        if (success)
        {
            Log.d(TAG, "removeBond : SUCCESS : " + bluetooth_device.getAddress());
        }
        else
        {
            Log.d(TAG, "removeBond : FAILURE : " + bluetooth_device.getAddress());
        }

        return success;
    }


    private boolean cancelBond(BluetoothDevice bluetooth_device) throws Exception
    {
        Method method = bluetooth_device.getClass().getMethod("cancelBondProcess");
        boolean success = (Boolean) method.invoke(bluetooth_device);

        if (success)
        {
            Log.d(TAG, "cancelBond : SUCCESS : " + bluetooth_device.getAddress());
        }
        else
        {
            Log.d(TAG, "cancelBond : FAILURE : " + bluetooth_device.getAddress());
        }

        return success;
    }


    private void handleMessage(Message msg)
    {
        if (thread_bluetooth_pairing != null)
        {
            thread_bluetooth_pairing = null;
        }

        if (pin.length() > 0)
        {
            bluetoothSendPin(pin);
        }

        switch (msg.what)
        {
            case PAIRING_FAILED:
            {
                Log.e(TAG, "handleMessage : PAIRING_FAILED");

                reportBluetoothPairingFailed();

                cancelBondingAndUnpairIfNeeded(desired_bluetooth_device_address);
            }
            break;

            case PAIRING_WAIT:
            {
                switch (bluetooth_device.getBondState())
                {
                    case BluetoothDevice.BOND_NONE:
                    {
                        Log.e(TAG, "handleMessage : BOND_NONE");

                        pairingFailed();
                    }
                    break;

                    case BluetoothDevice.BOND_BONDING:
                    {
                        int value = bluetooth_pairing_counter.getAndAdd(1);

                        Log.e(TAG, "handleMessage : BOND_BONDING. bluetooth_pairing_counter = " + value);

                        if (value > 40)
                        {
                            pairingFailed();
                        }
                        else
                        {
                            handler_pairing.sendEmptyMessageDelayed(PAIRING_WAIT, DateUtils.SECOND_IN_MILLIS);
                        }
                    }
                    break;

                    case BluetoothDevice.BOND_BONDED:
                    {
                        Log.e(TAG, "handleMessage : BOND_BONDED");
                        reportBluetoothPairingSuccess();
                    }
                    break;
                }
            }
            break;
        }
    }


    /* Note that this function needs to take the device address as an argument, rather than using desired_bluetooth_device_address,
       as desired_bluetooth_device_address may be empty or contain the MAC address of a different device
     */
    public void cancelBondingAndUnpairIfNeeded(String device_address)
    {
        Log.d(TAG, "cancelBondingAndUnpairIfNeeded");

        if (bluetooth_device != null)
        {
            bluetoothCancelDiscoveryIfRequired();

            if (bluetooth_device.getBondState() == BluetoothDevice.BOND_BONDING)
            {
                Log.d(TAG, "cancelBondingAndUnpairIfNeeded : BluetoothDevice.BOND_BONDING");

                try
                {
                    boolean success = cancelBond(bluetooth_device);
                    Log.d(TAG, "cancelBond returned " + success);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "cancelBondingAndUnpairIfNeeded Exception : " + e.getMessage());
                }
            }

            Log.d(TAG, "cancelBondingAndUnpairIfNeeded : Unpair " + device_address);

            if (unpairSpecificDevice(device_address))
            {
                Log.d(TAG, "cancelBondingAndUnpairIfNeeded : Unpair Success");
            }
            else
            {
                Log.e(TAG, "cancelBondingAndUnpairIfNeeded : Unpair Failure");
            }
        }
    }


    private final MyHandler handler_pairing = new MyHandler(this);


    private void pairingFailed()
    {
        handler_pairing.sendEmptyMessageDelayed(PAIRING_FAILED, 10);
    }


    private void bluetoothSendPin(String pin)
    {
        Log.d(TAG, "bluetoothSendPin : " + pin);

        byte[] pin_code = pin.getBytes();

        try
        {
            if (bluetooth_device.setPin(pin_code))
            {
                Log.d(TAG, "Pin set ok");
            }
            else
            {
                Log.e(TAG, "Problem setting pin");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "bluetoothSendPin Exception : Sending PIN Failed : " + e.getMessage());
        }
    }


    public void bluetoothCancelDiscoveryIfRequired()
    {
        if (bluetoothAdapter.isDiscovering())
        {
            Log.d(TAG, "Cancelling Bluetooth Discovery");
            bluetoothAdapter.cancelDiscovery();
        }
    }
}
