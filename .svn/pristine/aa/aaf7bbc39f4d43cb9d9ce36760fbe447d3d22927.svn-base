package com.isansys.patientgateway;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.isansys.common.enums.BluetoothStatus;
import com.isansys.common.enums.DeviceConnectionStatus;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class BluetoothAdapterResetManager
{
    private final RemoteLogging Log;
    private final String TAG = "BluetoothAdapterResetManager";

    private final Context context;
    private final DeviceInfoManager device_info_manager;
    private final SystemCommands commands_to_ui;

    BluetoothAdapterResetManager(Context context, RemoteLogging logger, DeviceInfoManager device_info_manager, SystemCommands commands)
    {
        this.Log = logger;
        this.context = context;
        this.device_info_manager = device_info_manager;
        this.commands_to_ui = commands;

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(broadcastReceivedBluetoothAdapter, filter);
    }


    public void destroy()
    {
        context.unregisterReceiver(broadcastReceivedBluetoothAdapter);
    }


    enum BluetoothResetStage
    {
        NOT_STARTED,
        BLE_OFF,
        BLE_ON,
    }


    private BluetoothResetStage ble_reset_stage = BluetoothResetStage.NOT_STARTED;

    public void handleBluetoothOff()
    {
        ble_reset_stage = BluetoothResetStage.BLE_OFF;

        checkForDeadBluetooth();
    }


    private void handleBluetoothOn()
    {
        ble_reset_stage = BluetoothResetStage.BLE_ON;

        last_bluetooth_enabled_state = true;
    }


    private final BroadcastReceiver broadcastReceivedBluetoothAdapter = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state)
                {
                    case BluetoothAdapter.STATE_OFF:
                    {
                        Log.d(TAG, "broadcastReceivedBluetoothAdapter : BluetoothAdapter.STATE_OFF");

                        turning_off_should_not_have_happened = false; // set to false as handleBluetoothOff is called now.

                        reportBluetoothOff();

                        handleBluetoothOff();
                    }
                    break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                    {
                        Log.d(TAG, "broadcastReceivedBluetoothAdapter : BluetoothAdapter.STATE_TURNING_OFF");

                        if (!device_info_manager.getBluetoothLeDeviceController().doing_requested_bluetooth_reset)
                        {
                            Log.e(TAG, "SHOULD NOT HAVE HAPPENED. Will be turned on via BluetoothAdapter.STATE_OFF");

                            turning_off_should_not_have_happened = true;
                            skip_turning_wifi_off = true; // don't turn wifi off and on if we're just responding to native BLE state change
                        }
                    }
                    break;

                    case BluetoothAdapter.STATE_ON:
                    {
                        Log.d(TAG, "broadcastReceivedBluetoothAdapter : BluetoothAdapter.STATE_ON");

                        handleBluetoothOn();

                        // trigger a sweetblue rescan if needed, to ensure everything is reconnected.
                        if(isBleScanRequired())
                        {
                            Log.d(TAG, "broadcastReceivedBluetoothAdapter : posting Ble rescan");

                            device_info_manager.getBluetoothLeDeviceController().postBleReScanDefaultDelay();
                        }
                    }
                    break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                    {
                        Log.d(TAG, "broadcastReceivedBluetoothAdapter : BluetoothAdapter.STATE_TURNING_ON");

                        if(turning_off_should_not_have_happened)
                        {
                            // strange dead bluetooth turning off -> turning on transition
                            // Tell the UI that BLE fell over...
                            reportBluetoothOff();
                        }
                    }
                    break;
                }
            }
        }
    };


    private boolean last_bluetooth_enabled_state = false;
    public boolean skip_turning_wifi_off = false;
    private boolean turning_off_should_not_have_happened = false;

    public void checkForDeadBluetooth()
    {
        Log.d(TAG, "checkForDeadBluetooth : ble_reset_stage = " + ble_reset_stage);

        switch(ble_reset_stage)
        {
            case NOT_STARTED:
            case BLE_ON:
            {
                try
                {
                    if (!BluetoothAdapter.getDefaultAdapter().isEnabled())
                    {
                        Log.e(TAG, "bluetooth_adapter is disabled");

                        if (!last_bluetooth_enabled_state)
                        {
                            Log.e(TAG, "Enabling bluetooth_adapter as it has been off for more than 10 seconds.");

                            ble_reset_stage = BluetoothResetStage.BLE_OFF;
                        }

                        last_bluetooth_enabled_state = false;
                    }
                    else
                    {
                        last_bluetooth_enabled_state = true;

                        ble_reset_stage = BluetoothResetStage.BLE_ON;
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "checkForDeadBluetooth exception : " + e.toString());
                }
            }
            break;

            case BLE_OFF:
            {
                // If unexpected, then turn it back on again. Sweetblue should detect this and tidy up itself
                if (!device_info_manager.getBluetoothLeDeviceController().doing_requested_bluetooth_reset)
                {
                    Log.e(TAG, "SHOULD NOT HAVE HAPPENED. Turning on Bluetooth Adapter");

                    BluetoothAdapter.getDefaultAdapter().enable();

                    // update this here?
                    ble_reset_stage = BluetoothResetStage.BLE_ON;
                }
            }
            break;
        }
    }


    private boolean isBleScanRequired()
    {
        for (DeviceInfo device_info : device_info_manager.getListOfBtleSensorDeviceInfoObjects())
        {
            if((device_info.getActualDeviceConnectionStatus() != DeviceConnectionStatus.CONNECTED) && (device_info.desired_device_connection_status == DeviceConnectionStatus.CONNECTED))
            {
                return true;
            }
        }

        return false;
    }


    private void reportBluetoothOff()
    {
        for(DeviceInfo device_info : device_info_manager.getListOfSensorDeviceInfoObjects())
        {
            commands_to_ui.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_SCAN_FINISHED__DEVICE_NOT_FOUND);

            commands_to_ui.reportBluetoothDeviceNotConnected(device_info);
        }

        commands_to_ui.sendCommandEndOfDeviceConnection(false, false);

        commands_to_ui.reportBluetoothOff();
    }
}
