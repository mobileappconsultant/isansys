package com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.idevicesinc.sweetblue.BleDevice;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import io.runtime.mcumgr.ble.McuMgrBleTransport;
import io.runtime.mcumgr.dfu.FirmwareUpgradeCallback;
import io.runtime.mcumgr.dfu.FirmwareUpgradeController;
import io.runtime.mcumgr.dfu.FirmwareUpgradeManager;
import io.runtime.mcumgr.exception.McuMgrException;

public class LifetouchThreeFirmwareUpdater implements FirmwareUpgradeCallback
{
    private final static String TAG = LifetouchThreeFirmwareUpdater.class.getSimpleName();

    public static final String EXTRA_DATA = "no.nordicsemi.android.nrftoolbox.dfu.EXTRA_DATA";
    public static final String DEVICE_TYPE = "DEVICE_TYPE";

    public static final String BROADCAST_ERROR = "no.nordicsemi.android.nrftoolbox.dfu.BROADCAST_ERROR";
    public static final String BROADCAST_PROGRESS = "no.nordicsemi.android.nrftoolbox.dfu.BROADCAST_PROGRESS";
    public static final String BROADCAST_DFU_UPLOAD_COMPLETE = "no.nordicsemi.android.nrftoolbox.dfu.BROADCAST_DFU_UPLOAD_COMPLETE";

    private final RemoteLogging Log;
    private final ContextInterface gateway_context_interface;
    private final DeviceType device_type;

    private final McuMgrBleTransport transport;
    private final FirmwareUpgradeManager dfuManager;
    private int last_progress_percent = -1;

    public LifetouchThreeFirmwareUpdater(ContextInterface context_interface, RemoteLogging logger, BleDevice lifetouch_device, DeviceType device_type)
    {
        gateway_context_interface = context_interface;

        // Initialize the BLE transporter with context and a BluetoothDevice
        BluetoothDevice nordic_ble_device = constructNordicBleDevice(lifetouch_device);
        transport = new McuMgrBleTransport(gateway_context_interface.getAppContext(), nordic_ble_device);
        dfuManager = new FirmwareUpgradeManager(transport, this);

        Log = logger;
        this.device_type = device_type;

        last_progress_percent = -1;
    }

    public void update(byte[] desired_firmware_binary_array)
    {
        Log.w(TAG,"BEGINNING LIFETOUCH THREE FIRMWARE UPDATE");

        // Start the firmware upgrade with the image data
        try {
            dfuManager.setMode(FirmwareUpgradeManager.Mode.CONFIRM_ONLY);
            dfuManager.start(desired_firmware_binary_array);
        } catch (McuMgrException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        sendProgressBroadcast(0);
    }

    private BluetoothDevice constructNordicBleDevice(BleDevice ble_device)
    {
        return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ble_device.getMacAddress());
    }

    private void sendProgressBroadcast(final int progress)
    {
        Intent intent = new Intent(BROADCAST_PROGRESS);
        intent.putExtra(EXTRA_DATA, progress);
        sendIntentWithDeviceType(intent);
    }

    private void sendUploadCompleteIntent()
    {
        Intent intent = new Intent(BROADCAST_DFU_UPLOAD_COMPLETE);
        sendIntentWithDeviceType(intent);
    }

    private void sendErrorIntent(final int error)
    {
        Intent intent = new Intent(BROADCAST_ERROR);
        intent.putExtra(EXTRA_DATA, error);
        sendIntentWithDeviceType(intent);
    }

    private void sendIntentWithDeviceType(Intent intent)
    {
        intent.putExtra(DEVICE_TYPE, device_type.ordinal());
        LocalBroadcastManager.getInstance(gateway_context_interface.getAppContext()).sendBroadcast(intent);
    }

    private void disconnectNordicBle()
    {
        transport.release();
        transport.disconnect().enqueue();
    }

    /** Called by BluetoothLeLifetouchThree */
    public void onDisconnect()
    {
        dfuManager.cancel();
        disconnectNordicBle();
    }

    /**
     * Called when the {@link FirmwareUpgradeManager} has started.
     * <p>
     * This callback is used to pass the upgrade controller which can pause/resume/cancel
     * an upgrade to a controller which may not have access to the original object.
     *
     * @param controller the upgrade controller.
     */
    @Override
    public void onUpgradeStarted(FirmwareUpgradeController controller) {
        Log.w(TAG, "Lifetouch Three firmware upgrade started");
    }

    /**
     * Called when the firmware upgrade changes state.
     *
     * @param prevState previous state.
     * @param newState  new state.
     * @see FirmwareUpgradeManager.State
     */
    @Override
    public void onStateChanged(FirmwareUpgradeManager.State prevState, FirmwareUpgradeManager.State newState) {
        Log.w(TAG, "Lifetouch Three firmware state changed: " + newState.toString());
    }

    /**
     * Called when the firmware upgrade has succeeded.
     */
    @Override
    public void onUpgradeCompleted() {
        Log.w(TAG, "LIFETOUCH THREE FIRMWARE UPGRADE COMPLETE!");
        disconnectNordicBle();
        sendUploadCompleteIntent();
    }

    /**
     * Called when the firmware upgrade has failed.
     *
     * @param state the state the upgrade failed in.
     * @param error the error.
     */
    @Override
    public void onUpgradeFailed(FirmwareUpgradeManager.State state, McuMgrException error) {
        Log.e(TAG, "LifetouchThree firmware upgrade failed");
        Log.e(TAG, error.toString());
        disconnectNordicBle();
        sendErrorIntent(1);
    }

    /**
     * Called when the firmware upgrade has been canceled using the
     * {@link FirmwareUpgradeManager#cancel()} method. The upgrade may be cancelled only during
     * uploading the image. When the image is uploaded, the test and/or confirm commands will be
     * sent depending on the {@link FirmwareUpgradeManager#setMode(FirmwareUpgradeManager.Mode)}.
     *
     * @param state the state the upgrade was cancelled in.
     */
    @Override
    public void onUpgradeCanceled(FirmwareUpgradeManager.State state) {
        Log.e(TAG, "LifetouchThree firmware upgrade cancelled");
        Log.e(TAG, state.toString());
        disconnectNordicBle();
        sendErrorIntent(1);
    }

    /**
     * Called when the {@link FirmwareUpgradeManager.State#UPLOAD} state progress has changed.
     *
     * @param bytesSent the number of bytes sent so far.
     * @param imageSize the total number of bytes to send.
     * @param timestamp the time that the successful response packet for the progress was received.
     */
    @Override
    public void onUploadProgressChanged(int bytesSent, int imageSize, long timestamp) {
        int progress = Math.round((bytesSent * 100f) / imageSize);

        if (progress != last_progress_percent) {
            last_progress_percent = progress;
            sendProgressBroadcast(progress);
        }
    }
}
