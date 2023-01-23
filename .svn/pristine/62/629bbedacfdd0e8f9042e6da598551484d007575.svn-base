package com.isansys.patientgateway.bluetoothLowEnergyDevices;

import android.os.Handler;
import android.text.format.DateUtils;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceConfig;
import com.idevicesinc.sweetblue.BleDeviceState;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.BleManagerConfig;
import com.idevicesinc.sweetblue.BleNodeConfig;
import com.idevicesinc.sweetblue.BleScanApi;
import com.idevicesinc.sweetblue.BleScanPower;
import com.idevicesinc.sweetblue.BleStatuses;
import com.idevicesinc.sweetblue.utils.Interval;
import com.isansys.common.enums.DeviceConnectionStatus;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService.PatientSessionInfo;
import com.isansys.patientgateway.deviceInfo.BtleSensorDevice;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Single entry point to all BTLE code (in theory at least).
 *
 * Created by Rory on 31/01/2017.
 */

public class BluetoothLeDeviceController
{
    private final RemoteLogging Log;
    private final static String TAG = BluetoothLeDeviceController.class.getSimpleName();

    private final BleManager m_bleManager;

    private final DeviceInfoManager device_info_manager;

    private final Handler ble_reset_handler = new Handler();
    private final Handler ble_rescan_handler = new Handler();

    private boolean emergency_ble_rescan_in_progress = false;
    public volatile boolean doing_requested_bluetooth_reset = false;

    private final Timer device_oversight_timer;

    private final long NO_DATA_CHECK_TIME = 2*DateUtils.MINUTE_IN_MILLIS;
    private final long KEEP_ALIVE_CHECK_TIME = 15*DateUtils.SECOND_IN_MILLIS;

    private final TimeSource ntp_time;
    private final PatientSessionInfo patient_session_info;

    public BluetoothLeDeviceController(ContextInterface context_interface, RemoteLogging logger, DeviceInfoManager info_manager, TimeSource time_source, PatientSessionInfo passed_patient_session_info)
    {
        Log = logger;
        device_info_manager = info_manager;

        ntp_time = time_source;
        patient_session_info = passed_patient_session_info;

        BleManagerConfig ble_manager_config = new BleManagerConfig();

        // Disable this line to hide Sweetblue logging
        ble_manager_config.loggingEnabled = true;

        ble_manager_config.runOnMainThread = true;

        ble_manager_config.scanPower = BleScanPower.HIGH_POWER;

        ble_manager_config.scanApi = BleScanApi.PRE_LOLLIPOP;

        ble_manager_config.enableCrashResolverForReset = false;

        ble_manager_config.uhOhCallbackThrottle = Interval.secs(1.5); // 1.5 seconds

        ble_manager_config.revertToClassicDiscoveryIfNeeded = false;

        // When in "Short Term", try and reconnected every second
        final Interval SHORT_TERM_ATTEMPT_RATE = Interval.DISABLED;
        // How long to be in "Short Term" which is where we do not show the device as disconnected on the Patient Gateway UI
        final Interval SHORT_TERM_TIMEOUT = Interval.DISABLED;

        // When in "Long Term", try and reconnected every 3 seconds
        final Interval LONG_TERM_ATTEMPT_RATE = Interval.secs(30.0);  //Interval.secs(3.0);
        // Never stop trying to reconnect the device. This state is when we show the device as Disconnected on the Patient Gateway UI
        final Interval LONG_TERM_TIMEOUT = Interval.INFINITE;

        ble_manager_config.reconnectFilter = new CustomReconnectFilter(SHORT_TERM_ATTEMPT_RATE, LONG_TERM_ATTEMPT_RATE, SHORT_TERM_TIMEOUT, LONG_TERM_TIMEOUT);
        // Setup a custom Timeout Request Filter so we are in control of how long we spend trying to connect to the device
//        ble_manager_config.taskTimeoutRequestFilter = new CustomTaskTimeoutRequestFilter();

        ble_manager_config.autoEnableNotifiesOnReconnect = false;

        // Don't auto reconnect stuff after a BLE reset - we handle reconnecting ourselves anyway, and it has the potential to result in disconnected devices reconnecting
        ble_manager_config.autoReconnectDeviceWhenBleTurnsBackOn = false;
        ble_manager_config.retainDeviceWhenBleTurnsOff = false;
        ble_manager_config.cacheDeviceOnUndiscovery = false;

        ble_manager_config.connectFailRetryConnectingOverall = true;

        ble_manager_config.useGattRefresh = true;

        ble_manager_config.gattRefreshOption = BleDeviceConfig.RefreshOption.AFTER_DISCONNECTING;

        ble_manager_config.taskTimeoutRequestFilter = new CustomTaskTimeoutRequestFilter();

        m_bleManager = BleManager.get(context_interface.getAppContext(), ble_manager_config);

        m_bleManager.setListener_UhOh(new CustomUhOhListener());
        //noinspection deprecation
        m_bleManager.setListener_State(new CustomStateListener());
        m_bleManager.setListener_NativeState(new CustomNativeStateListener());
        m_bleManager.setListener_Assert(new CustomAssertListener());
        m_bleManager.setListener_Discovery(new CustomDiscoveryListener());

        device_oversight_timer = new Timer("device_oversight_timer");
        device_oversight_timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                boolean a_device_needs_restart = false;

                if(patient_session_info.patient_session_running)
                {
                    for (BtleSensorDevice device_info : device_info_manager.getListOfBtleSensorDeviceInfoObjects())
                    {
                        if(device_info.isDeviceSessionInProgress())
                        {
                            a_device_needs_restart |= device_info.checkLastDataIsRestartRequired(NO_DATA_CHECK_TIME);
                        }
                    }
                }

                for (BtleSensorDevice device_info : device_info_manager.getListOfBtleSensorDeviceInfoObjects())
                {
                    if(device_info.getActualDeviceConnectionStatus() == DeviceConnectionStatus.DFU)
                    {
                        a_device_needs_restart |= device_info.checkDfuIsRestartRequired(ntp_time.currentTimeMillis() - NO_DATA_CHECK_TIME);
                    }
                }


                if (a_device_needs_restart)
                {
                    Log.e(TAG, "Calling postCompleteBleReset from device_oversight_timer");

                    postCompleteBleReset(0);
                }
            }
        }, 0, NO_DATA_CHECK_TIME);


        device_oversight_timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                for (BtleSensorDevice device_info : device_info_manager.getListOfBtleSensorDeviceInfoObjects())
                {
                    device_info.keepAlive();
                }
            }
        }, KEEP_ALIVE_CHECK_TIME, KEEP_ALIVE_CHECK_TIME);
    }


    public void onDestroy()
    {
        ble_reset_handler.removeCallbacksAndMessages(null);
        ble_rescan_handler.removeCallbacksAndMessages(null);

        device_oversight_timer.cancel();
    }


    // Direct copy of DefaultReconnectFilter with logging added
    private class CustomReconnectFilter implements BleNodeConfig.ReconnectFilter
    {
        final Please DEFAULT_INITIAL_RECONNECT_DELAY	= Please.retryInstantly();

        private final Please m_please__SHORT_TERM__SHOULD_TRY_AGAIN;
        private final Please m_please__LONG_TERM__SHOULD_TRY_AGAIN;

        private final Interval m_timeout__SHORT_TERM__SHOULD_CONTINUE;
        private final Interval m_timeout__LONG_TERM__SHOULD_CONTINUE;

        CustomReconnectFilter(final Interval reconnectRate__SHORT_TERM, final Interval reconnectRate__LONG_TERM, final Interval timeout__SHORT_TERM, final Interval timeout__LONG_TERM)
        {
            m_please__SHORT_TERM__SHOULD_TRY_AGAIN = Please.retryIn(reconnectRate__SHORT_TERM);
            m_please__LONG_TERM__SHOULD_TRY_AGAIN = Please.retryIn(reconnectRate__LONG_TERM);

            m_timeout__SHORT_TERM__SHOULD_CONTINUE = timeout__SHORT_TERM;
            m_timeout__LONG_TERM__SHOULD_CONTINUE = timeout__LONG_TERM;
        }

        @Override public Please onEvent(final ReconnectEvent e)
        {
            if( e.type().isShouldTryAgain() )
            {
                if( e.failureCount() == 0 )
                {
                    return DEFAULT_INITIAL_RECONNECT_DELAY;
                }
                else
                {
                    Log.w(TAG, "CustomReconnectFilter : failure count = " + e.failureCount() + " so retrying");
                    Log.w(TAG, "CustomReconnectFilter : failure reason = " + e.connectionFailEvent().gattStatus());

                    if(e.connectionFailEvent().gattStatus() == BleStatuses.CONN_TERMINATE_LOCAL_HOST)
                    {
                        Log.e(TAG, "CustomReconnectFilter : got status 22");
                    }

                    if( e.type().isShortTerm() )
                    {
                        return m_please__SHORT_TERM__SHOULD_TRY_AGAIN;
                    }
                    else
                    {
                        return m_please__LONG_TERM__SHOULD_TRY_AGAIN;
                    }
                }
            }
            else if( e.type().isShouldContinue() )
            {
                if( e.node() instanceof BleDevice )
                {
                    final boolean definitelyPersist = BleDeviceState.CONNECTING_OVERALL.overlaps(e.device().getNativeStateMask()) &&
                            BleDeviceState.CONNECTED.overlaps(e.device().getNativeStateMask());

                    //--- DRK > We don't interrupt if we're in the middle of connecting
                    //---		but this will be the last attempt if it fails.
                    if( definitelyPersist )
                    {
                        return Please.persist();
                    }
                    else
                    {
                        return shouldContinue(e);
                    }
                }
                else
                {
                    return shouldContinue(e);
                }
            }
            else
            {
                Log.e(TAG, "CustomReconnectFilter : Not retrying");

                return Please.stopRetrying();
            }
        }

        private Please shouldContinue(final ReconnectEvent e)
        {
            if( e.type().isShortTerm() )
            {
                return Please.persistIf(e.totalTimeReconnecting().lt(m_timeout__SHORT_TERM__SHOULD_CONTINUE));
            }
            else
            {
                return Please.persistIf(e.totalTimeReconnecting().lt(m_timeout__LONG_TERM__SHOULD_CONTINUE));
            }
        }
    }


    private class CustomUhOhListener implements BleManager.UhOhListener
    {
        @Override
        public void onEvent(BleManager.UhOhListener.UhOhEvent e)
        {
            Log.e(TAG, "CustomUhOhListener : " + e.toString());

            // An UhOh is a warning about an exceptional (in the bad sense) and unfixable problem with the underlying stack that the app can warn its user about.
            // It's kind of like an Exception but they can be so common that using Exception would render this library unusable without a rat's nest of try/catches.
            // Instead you implement BleManager.UhOhListener to receive them.
            // Each BleManager.UhOhListener.UhOh has a getRemedy() that suggests what might be done about it.

            Remedy remedy = e.remedy();

            switch (e.uhOh())
            {
                case BOND_TIMED_OUT:
                    // A BleTask.BOND operation timed out.
                    break;

                case CANNOT_DISABLE_BLUETOOTH:
                    // BluetoothAdapter.disable(), through BleManager.turnOff(), is failing to complete.
                    break;

                case CANNOT_ENABLE_BLUETOOTH:
                    // BluetoothAdapter.enable(), through BleManager.turnOn(), is failing to complete.
                    break;

                case CLASSIC_DISCOVERY_FAILED:
                    // BluetoothAdapter.startLeScan(BluetoothAdapter.LeScanCallback) failed and BleManagerConfig.revertToClassicDiscoveryIfNeeded is true so we try BluetoothAdapter.startDiscovery() but that also fails...fun!
                    break;

                case CONNECTED_WITHOUT_EVER_CONNECTING:
                    // BluetoothGatt.getConnectionState(BluetoothDevice) says we're connected but we never tried to connect in the first place.
                    break;

                case DEAD_OBJECT_EXCEPTION:
                    // Similar in concept to RANDOM_EXCEPTION but used when DeadObjectException is thrown.
                    break;

                case DUPLICATE_SERVICE_FOUND:
                    // A BluetoothGatt.discoverServices() operation returned two duplicate services.
                    break;

                case INCONSISTENT_NATIVE_BLE_STATE:
                    // When the underlying stack meets a race condition where BluetoothAdapter.getState() does not match the value provided through BluetoothAdapter.ACTION_STATE_CHANGED with BluetoothAdapter.EXTRA_STATE.
                    break;

                case OLD_DUPLICATE_SERVICE_FOUND:
                    // A BluetoothGatt.discoverServices() operation returned a service instance that we already received before after disconnecting and reconnecting.
                    break;

                case RANDOM_EXCEPTION:
                    // The underlying native BLE stack enjoys surprising you with random exceptions.
                    break;

                case READ_RETURNED_NULL:
                    // A BleDevice.read(java.util.UUID, BleDevice.ReadWriteListener) returned with a null characteristic value.
                    break;

                case READ_TIMED_OUT:
                    // A BleDevice.read(java.util.UUID, BleDevice.ReadWriteListener) took longer than timeout set by BleNodeConfig.taskTimeoutRequestFilter.
                    break;

                case SERVICE_DISCOVERY_IMMEDIATELY_FAILED:
                    // BluetoothGatt.discoverServices() failed right off the bat and returned false.
                    break;

                case START_BLE_SCAN_FAILED:
                    // BluetoothAdapter.startLeScan(BluetoothAdapter.LeScanCallback) failed and BleManagerConfig.revertToClassicDiscoveryIfNeeded is false.
                    break;

                case START_BLE_SCAN_FAILED__USING_CLASSIC:
                    // BluetoothAdapter.startLeScan(BluetoothAdapter.LeScanCallback) failed for an unknown reason.
                    break;

                case INCONSISTENT_NATIVE_DEVICE_STATE:
                {
                    Log.w(TAG, "INCONSISTENT_NATIVE_DEVICE_STATE thrown by connection task - wait and see");

                    // Force WAIT AND SEE - we know this UhOh only occurs in P_Task_Connect when we have a native connected
                    // state but the cached state hasn't caught up. Resetting BLE at this point causes more trouble than it
                    // solves, and the connection task itself will succeed anyway so we're better off doing nothing.
                    remedy = Remedy.WAIT_AND_SEE;
                }
                break;

                case UNKNOWN_BLE_ERROR:
                    // Just a blanket case for when the library has to completely shrug its shoulders.
                    break;

                case WRITE_TIMED_OUT:
                    // Similar to READ_TIMED_OUT but for BleDevice.write(java.util.UUID, byte[]).
                    break;
            }

            switch (remedy)
            {
                case RESET_BLE:
                case RESTART_PHONE:
                {
                    Log.e(TAG, "CustomUhOhListener : doing resetBluetoothAdapter as something gone VERY wrong : " + e.remedy());

                    postBleResetWithoutRemovingDevices(2);    /* post complete reset with 0 second delay */
                }
                break;

                case WAIT_AND_SEE:
                    break;
            }
        }
    }


    @SuppressWarnings("deprecation")
    private class CustomStateListener implements BleManager.StateListener
    {
        @Override
        public void onEvent(@SuppressWarnings("deprecation") BleManager.StateListener.StateEvent e)
        {
            Log.e(TAG, "CustomStateListener : " + e.toString());
        }
    }


    private class CustomNativeStateListener implements BleManager.NativeStateListener
    {
        @Override
        public void onEvent(BleManager.NativeStateListener.NativeStateEvent e)
        {
            Log.e(TAG, "CustomNativeStateListener : " + e.toString());
        }
    }


    private class CustomAssertListener implements BleManager.AssertListener
    {
        @Override
        public void onEvent(BleManager.AssertListener.AssertEvent e)
        {
            Log.e(TAG, "CustomAssertListener : " + e.toString());
        }
    }


    private class CustomDiscoveryListener implements BleManager.DiscoveryListener
    {
        @Override
        public void onEvent(BleManager.DiscoveryListener.DiscoveryEvent e)
        {
            Log.e(TAG, "CustomDiscoveryListener : " + e.toString());
        }
    }


    /**
     * Standard method to start scanning for a BLE device.
     *
     * @param scan_time_in_seconds
     * @param device_info
     */
    public void connectWithScan(int scan_time_in_seconds, BtleSensorDevice device_info)
    {
        Log.e(TAG, "connectWithScan : Connect to " + device_info.getSensorTypeAndDeviceTypeAsString() + " (" + device_info.bluetooth_address + ") for " + scan_time_in_seconds + " seconds");

        Interval scan_time;

        if(scan_time_in_seconds == 0)
        {
            scan_time = Interval.INFINITE;
        }
        else
        {
            scan_time = Interval.secs(scan_time_in_seconds);
        }

        Log.d(TAG, "connectWithScan : Scan_time = " + scan_time);
        
        device_info.resetBleScanVariables();

        boolean started = m_bleManager.startScan(scan_time, discovery_listener);
        
        if(!started)
        {
            Log.e(TAG, "connectWithScan : SCAN NOT STARTED!!!");
        }
    }


    /**
     * DiscoveryListener - handles what to do when a device is found by {@link BluetoothLeDeviceController#connectWithScan(int, BtleSensorDevice)}
     */
    private final CustomDiscoveryListener discovery_listener = new CustomDiscoveryListener()
    {
        @Override public void onEvent(BleManager.DiscoveryListener.DiscoveryEvent event)
        {
            if( event.was(LifeCycle.DISCOVERED) || event.was(LifeCycle.REDISCOVERED))
            {
                ArrayList<BtleSensorDevice> rescan_devices = getDevicesRequiringRescan();

                // Check for re-scanning devices
                for(BtleSensorDevice device : rescan_devices)
                {
                    if (event.device().getMacAddress().equals(device.bluetooth_address))
                    {
                        Log.e(TAG, "BLE Device Scan : " + event);

                        Log.d(TAG, "connectWithScan : Found " + device.getSensorTypeAndDeviceTypeAsString());
                        device.onDiscovered(event);

                        break;
                    }
                }

                // check list again
                rescan_devices = getDevicesRequiringRescan();

                if(rescan_devices.isEmpty())
                {
                    // All devices found
                    m_bleManager.stopScan();

                    emergency_ble_rescan_in_progress = false;

                    Log.d(TAG, "<<<<<<<<<<<< Scan Stopped! >>>>>>>>>>>>>>>");
                }
            }
        }
    };


    private ArrayList<BtleSensorDevice> getDevicesRequiringRescan()
    {
        ArrayList<BtleSensorDevice> devices = new ArrayList<>();

        for (BtleSensorDevice device_info : device_info_manager.getListOfBtleSensorDeviceInfoObjects())
        {
            if((device_info.desired_device_connection_status == DeviceConnectionStatus.CONNECTED)
                    && (!device_info.isFound()))
            {
                devices.add(device_info);
            }
        }

        return devices;
    }


    public void stopRunningScan()
    {
        if(!emergency_ble_rescan_in_progress)
        {
            m_bleManager.stopAllScanning();
        }
        else
        {
            Log.d(TAG, "stopRunningScan : scan not stopped, as a full ble rescan is in progress");
        }
    }


    public void endSessionStopAllScans()
    {
        m_bleManager.stopScan();

        emergency_ble_rescan_in_progress = false;

        ble_rescan_handler.removeCallbacksAndMessages(null);

        Log.d(TAG, "<<<<<<<<<<<< Scan Stopped - END OF SESSION! >>>>>>>>>>>>>>>");
    }


    public boolean checkLocationEnabled()
    {
        if(!m_bleManager.isLocationEnabledForScanning())
        {
            Log.e(TAG, "checkLocationEnabled : not enabled");

            return false;
        }

        Log.e(TAG, "checkLocationEnabled : enabled");

        return true;
    }


    private void removeAllDevices()
    {
        m_bleManager.removeAllDevicesFromCache();
    }


    static class CustomTaskTimeoutRequestFilter implements BleNodeConfig.TaskTimeoutRequestFilter
    {

        /**
         * Default value for all tasks.
         */
        static final double LONG_TASK_TIMEOUT					= 25;


        private final Please DEFAULT_RETURN_VALUE = Please.setTimeoutFor(Interval.secs(BleNodeConfig.DefaultTaskTimeoutRequestFilter.DEFAULT_TASK_TIMEOUT));



        @Override public Please onEvent(TaskTimeoutRequestEvent e)
        {
            switch (e.task())
            {
                case RESOLVE_CRASHES:
                    return Please.setTimeoutFor(Interval.secs(BleNodeConfig.DefaultTaskTimeoutRequestFilter.DEFAULT_CRASH_RESOLVER_TIMEOUT));
                case DISCOVER_SERVICES:
                case CONNECT:
                    return Please.setTimeoutFor(Interval.secs(LONG_TASK_TIMEOUT));
                case BOND:
                default:
                    return DEFAULT_RETURN_VALUE;
            }
        }
    }


    /**
     * Full reset - remove devices, turn BLE off and on, rescan for desired devices.
     * @param seconds
     */
    public void postCompleteBleReset(int seconds)
    {
        if(!doing_requested_bluetooth_reset)
        {
            Log.w(TAG, "postCompleteBleResetWithReScan : completely reset bluetooth - DON'T re scan devices");
            doing_requested_bluetooth_reset = true;

            // Keep track of the number of times this is called a session for logging
            patient_session_info.number_of_times_postCompleteBleReset_called++;

            ble_reset_handler.removeCallbacksAndMessages(null);
            ble_reset_handler.postDelayed(runnable_removeDevicesAndResetBluetoothAdapter, seconds * DateUtils.SECOND_IN_MILLIS); // reset BLE after 10 seconds
        }
        else
        {
            Log.d(TAG, "postCompleteBleResetWithReScan : reset already posted");
        }
    }


    /**
     * Full reset without removing the devices first
     * @param delay
     */
    public void postBleResetWithoutRemovingDevices(int delay)
    {
        Log.e(TAG, "postDelayedBleReset : Checking if runnable already posted....");

        if(!doing_requested_bluetooth_reset)
        {
            doing_requested_bluetooth_reset = true;

            Log.e(TAG, "postDelayedBleReset : Nothing pending so posting runnable for 10 seconds");

            // Keep track of the number of times this is called a session for logging
            patient_session_info.number_of_times_postBleResetWithoutRemovingDevices_called++;

            ble_reset_handler.removeCallbacksAndMessages(null);
            ble_reset_handler.postDelayed(runnable_resetBluetoothAdapter, delay * DateUtils.SECOND_IN_MILLIS); // reset BLE after 10 seconds
        }
        else
        {
            Log.e(TAG, "postDelayedBleReset : Runnable already queued");
        }
    }



    /**
     * Posts {@link BluetoothLeDeviceController#removeDeviceAndRescanForIt(BluetoothLeDevice)} to a runnable.
     *
     *  This is an emergency reset for a single device - if something has gone wrong with a connection, we can remove it and try again.
     *
     * @param device
     */
    public void postRemoveDeviceAndRescanForIt(final BluetoothLeDevice device)
    {
        Runnable runnable = () -> removeDeviceAndRescanForIt(device);

        ble_rescan_handler.post(runnable);
    }


    /**
     * Just trigger a rescan without turning anything off and on or removing devices.
     */
    public void postBleReScan(long delay)
    {
        ble_rescan_handler.postDelayed(callScanNextBleDevice, delay);
    }


    public void postBleReScanDefaultDelay()
    {
        postBleReScan(2 * DateUtils.SECOND_IN_MILLIS);
    }


    /**
     *  BLE reset and rescan runnables
     */

    private final Runnable runnable_removeDevicesAndResetBluetoothAdapter = this::removeDevicesAndResetBluetoothAdapter;


    private final Runnable runnable_resetBluetoothAdapter = this::resetBluetoothAdapter;


    private final Runnable callScanNextBleDevice = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(TAG, "callScanNextBleDevice fired");

            if (!emergency_ble_rescan_in_progress)
            {
                emergency_ble_rescan_in_progress = true;

                scanNextBleDevice();
            }
        }
    };


    private final Runnable scanNextDevice_timedOut = new Runnable()
    {
        @Override
        public void run()
        {
            Log.w(TAG, "scanNextDevice_timedOut");

            scanNextBleDevice();
        }
    };


    /**
     *  Functions to do the work resetting and rescanning ble.
     *
     */
    private void removeDevicesAndResetBluetoothAdapter()
    {
        // Disconnect and cancel all existing connections
        for (BtleSensorDevice device_info : device_info_manager.getListOfBtleSensorDeviceInfoObjects())
        {
            removeDeviceIfConnected(device_info);
        }

        // remove devices from sweetblue cache...
        removeAllDevices();

        resetBluetoothAdapter();
    }


    private void removeDeviceIfConnected(BtleSensorDevice device_info)
    {
        if(device_info.desired_device_connection_status == DeviceConnectionStatus.CONNECTED)
        {
            device_info.disconnectDevice();
        }
    }


    private void resetBluetoothAdapter()
    {
        // Reset BLE manager.
        m_bleManager.clearQueue();

        Log.d(TAG,"resettingBluetoothAdapter : Resetting Bluetooth using Sweetblue. Seems to take about 5-10 seconds");

        m_bleManager.reset(new SweetblueResetListener());
    }


    private class SweetblueResetListener implements BleManager.ResetListener
    {
        @Override
        public void onEvent(BleManager.ResetListener.ResetEvent e)
        {
            Log.e(TAG, "SweetblueResetListener : " + e.toString());

            if (e.progress() == Progress.COMPLETED)
            {
                Log.e(TAG, "SweetblueResetListener finished reset");

                doing_requested_bluetooth_reset = false;
            }
        }
    }


    /**
     * This is an emergency reset for a device - if something has gone wrong with a connection, we can remove it and try again.
     *
     * Triggered by {@link BluetoothLeDeviceController#postRemoveDeviceAndRescanForIt(BluetoothLeDevice)}
     *
     * Only runs if the device has a session already - otherwise it interferes with pairing etc.
     * @param device
     */
    private void removeDeviceAndRescanForIt(BluetoothLeDevice device)
    {
        if(device.isDeviceSessionInProgress())
        {
            device.removeDeviceKeepingBondState();

            if (!emergency_ble_rescan_in_progress)
            {
                emergency_ble_rescan_in_progress = true;
                scanNextBleDevice();
            }
        }
    }



    /**
     * Generic scan code used by the emergency rescan/recovery code
     */
    private void scanNextBleDevice()
    {
        ArrayList<BtleSensorDevice> devices_requiring_rescan = getDevicesRequiringRescan();

        // no devices require scanning
        if (devices_requiring_rescan.isEmpty())
        {
            Log.d(TAG, "scanNextBleDevice: all_desired_devices_connected == true");

            // Stop the timeout handler, otherwise it will set last device to NOT_PAIRED
            ble_rescan_handler.removeCallbacksAndMessages(null);
        }
        else
        {
            // Bluetooth devices not connected
            Log.e(TAG, "scanNextBleDevice DEVICES NOT CONNECTED : " + formatDeviceTypesString(devices_requiring_rescan));

            for(BtleSensorDevice device_info : devices_requiring_rescan)
            {
                if(device_info.isDeviceTypePartOfPatientSession())
                {
                    if (startConnect(device_info))
                    {
                        Log.e(TAG, "scanNextBleDevice - starting a scan for " + device_info.device_type);

                        return;
                    }
                }
                else
                {
                    Log.d(TAG, "scanNextBleDevice : " + device_info.device_type + " not added to session yet so ignoring");
                }
            }

            // If we get here, no scan started
            Log.e(TAG, "scanNextBleDevice no scan was started. Code thinks LT, LTemp and LO connected!!!!");
        }

        // No scan started - if we get here then either no devices needed scanning, or no scan could be started for a device
        // that is part of the session
        emergency_ble_rescan_in_progress = false;
    }


    private String formatDeviceTypesString(ArrayList<BtleSensorDevice> devices_requiring_scan)
    {
        StringBuilder devices = new StringBuilder();

        for(BtleSensorDevice device : devices_requiring_scan)
        {
            devices.append(device.device_type.toString()).append(", ");
        }

        return devices.substring(0, devices.length() - 2);
    }


    /**
     * Calls {@link BluetoothLeDeviceController#connectWithScan(int, BtleSensorDevice)} to trigger the scan.
     * This is the same scanning method as is used for user-initiated scans, but we set up an automatic timeout
     * and rescan, whereas normally we leave the rescanning to the user.
     */
    private boolean startConnect(BtleSensorDevice device_info)
    {
        final int scanning_timeout_in_seconds = 10;
        final long rescan_timeout_in_milliseconds = 30 * DateUtils.SECOND_IN_MILLIS;

        Log.d(TAG, "device_info.desired_device_connection_status " + device_info.desired_device_connection_status + " : device_info.actual_device_connection_status " + device_info.getActualDeviceConnectionStatus());

        if(device_info.desired_device_connection_status == DeviceConnectionStatus.CONNECTED)
        {
            if (!device_info.isFound())
            {
                Log.e(TAG, "Searching for " + device_info.getSensorTypeAndDeviceTypeAsString() + " : " + device_info.bluetooth_address);
                connectWithScan(scanning_timeout_in_seconds, device_info);

                // Post the runnable for the next attempt
                ble_rescan_handler.removeCallbacksAndMessages(null);
                ble_rescan_handler.postDelayed(scanNextDevice_timedOut, rescan_timeout_in_milliseconds);

                return true;
            }
        }

        return false;
    }
}
