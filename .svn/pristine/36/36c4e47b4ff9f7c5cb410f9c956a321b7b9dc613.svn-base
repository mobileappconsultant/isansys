package com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UA651;

import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BloodPressureBluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

/*
Code based on AnD docs available at

https://www.aandd.co.jp/adhome/support/software/sdk_eng/ble/

User ID : wcsdk
Password : cnipnf2430
*/


/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public class BluetoothLe_AnD_UA651 extends BloodPressureBluetoothLeDevice
{
    private final static String TAG = BluetoothLe_AnD_UA651.class.getSimpleName();

    public final static String ACTION_PAIRING = "BluetoothLe_AnD_UA651.ACTION_PAIRING";
    public final static String ACTION_PAIRING_SUCCESS = "BluetoothLe_AnD_UA651.ACTION_PAIRING_SUCCESS";
    public final static String ACTION_PAIRING_FAILURE = "BluetoothLe_AnD_UA651.ACTION_PAIRING_FAILURE";
    public final static String ACTION_CONNECTED = "BluetoothLe_AnD_UA651.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED = "BluetoothLe_AnD_UA651.ACTION_DISCONNECTED";
    public final static String ACTION_UNEXPECTED_UNBOND  = "BluetoothLe_AnD_UA651.ACTION_UNEXPECTED_UNBOND";
    public final static String ACTION_DATA_AVAILABLE = "BluetoothLe_AnD_UA651.ACTION_DATA_AVAILABLE";


    public BluetoothLe_AnD_UA651(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time)
    {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__AND_UA651);
    }


    public String getChildTag()
    {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
    }


    protected String getActionPairingString()
    {
        return ACTION_PAIRING;
    }

    protected String getActionPairingSuccessString()
    {
        return ACTION_PAIRING_SUCCESS;
    }

    protected String getActionPairingFailureString()
    {
        return ACTION_PAIRING_FAILURE;
    }

    protected String getActionConnectedString()
    {
        return ACTION_CONNECTED;
    }

    protected String getActionDisconnectedString()
    {
        return ACTION_DISCONNECTED;
    }

    protected String getActionUnexpectedlyUnbondedString()
    {
        return ACTION_UNEXPECTED_UNBOND;
    }

    protected String getActionDataAvailableString()
    {
        return ACTION_DATA_AVAILABLE;
    }

    private void deleteAllDataInBufferAndSetBufferSize()
    {
        Log.d(TAG, "deleteAllDataInBufferAndSetBufferSize");

        // See AnD UA651 Application Development Specification

        //Format here is (size)  (type)       (command)      (data)
        // values are     (3)    (write)  (set buffer size)   (1 = 30 measurements)
        byte[] command = { (byte) 0x03, (byte) 0x01, (byte) 0xA6, (byte) 0x01 };

        ble_device.write(BloodPressureBluetoothLeDevice.AnD_CUSTOM_SERVICE, BloodPressureBluetoothLeDevice.AnD_CHARACTERISTIC_CUSTOM_ONE, command, event -> {
            logEventStatus(event);

            if (event.wasSuccess())
            {
                Log.e(TAG, "deleteAllDataInBufferAndSetBufferSize Write successful : Command Response = " + Utils.byteArrayToHexString(event.data()));

                send_configuration_commands = false;
            }
            else
            {
                Log.e(TAG, "deleteAllDataInBufferAndSetBufferSize Write FAILED : " + event.status().toString());
            }
        });
    }
}
