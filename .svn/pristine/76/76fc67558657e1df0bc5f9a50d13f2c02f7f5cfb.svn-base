package com.isansys.patientgateway.bluetoothLowEnergyDevices;

import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTED;

import androidx.annotation.Nullable;

import com.idevicesinc.sweetblue.BleDevice;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.UUID;

/**
 * Implements common null/failure checking boilerplate for BleDevices
 */

public class BluetoothLeHelper
{
    private BleDevice ble_device;
    private final RemoteLogging Log;
    private final String TAG;

    public BluetoothLeHelper(BleDevice ble_device, RemoteLogging Log, String TAG)
    {
        this.ble_device = ble_device;
        this.Log = Log;
        this.TAG = TAG;
    }

    public void setBleDevice(BleDevice ble_device) {
        this.ble_device = ble_device;
    }

    public interface BLEWriteCallback
    {
        void call(boolean success);
    }

    public interface BLEReadCallback
    {
        void call(final String macAddress, final byte[] data);
    }

    public interface BLEDataHandler
    {
        void receive(final String macAddress, final byte[] data);
    }

    public void writeCharacteristic(UUID service, UUID characteristic, final byte[] data)
    {
        writeCharacteristic(service, characteristic, data, null);
    }

    public void writeCharacteristic(UUID service, UUID characteristic, final byte[] data, @Nullable BLEWriteCallback callback)
    {
        if (ble_device == null)
        {
            Log.e(TAG, "Cannot write: BLE device is null!");
        }
        else
        {
            ble_device.write(service, characteristic, data, event -> {
                logEventStatus(event);

                Log.d(TAG, "Write response: " + Utils.byteArrayToHexString(event.data()));

                if (callback != null)
                {
                    callback.call(event.wasSuccess());
                }
            });
        }
    }

    public void readCharacteristic(UUID service, UUID characteristic, BLEReadCallback callback)
    {
        if (ble_device == null)
        {
            Log.e(TAG, "cannot read: BLE device is null!");
        }
        else
        {
            ble_device.read(service, characteristic, event -> {
                logEventStatus(event);
                if (event.wasSuccess() && event.data().length > 0)
                {
                    callback.call(event.macAddress(), event.data());
                }
                else
                {
                    Log.e(TAG, "Read characteristic failed!");
                }
            });
        }
    }

    public void subscribeToCharacteristic(UUID service, UUID characteristic, String name, BLEDataHandler handler)
    {
        if (ble_device == null)
        {
            Log.e(TAG, "Cannot subscribe to " + name + " as ble_device is NULL!");
            return;
        }

        ble_device.enableNotify(service, characteristic, event -> {
            switch (event.type())
            {
                case ENABLING_NOTIFICATION:
                {
                    Log.e(TAG, name + " : ENABLING_NOTIFICATION success = " + event.wasSuccess() + " : Data = " + Utils.byteArrayToHexString(event.data()));

                    if (!event.wasSuccess() && event.device().is(CONNECTED))
                    {
                        Log.e(TAG, "Need to retry!");
                    }
                }
                break;

                case INDICATION:
                case NOTIFICATION:
                {
                    if (event.wasSuccess() && event.data().length > 0)
                    {
                        handler.receive(event.macAddress(), event.data());
                    }
                    else
                    {
                        Log.e(TAG, name + " read failed!");
                    }
                }
                break;

                default:
                {
                    Log.i(TAG, name + " : " + Utils.byteArrayToHexString(event.data()));
                }
            }
        });
    }

    private void logEventStatus(BleDevice.ReadWriteListener.ReadWriteEvent event)
    {
        if (event.wasSuccess())
        {
            Log.d(TAG, "ReadWriteListener : " + event.type().toString() + " : " + event.charUuid().toString() + " : " + event.status().toString() + " : Data = " + Utils.byteArrayToHexString(event.data()));
        }
        else
        {
            Log.e(TAG, "ReadWriteListener event = " + event);
        }
    }
}
