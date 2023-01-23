package com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UA1200BLE;

import android.text.format.DateUtils;

import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.utils.Uuids;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BloodPressureBluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeHelper;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import org.joda.time.DateTime;

/*
Code based on AnD docs available at

https://www.aandd.co.jp/adhome/support/software/sdk_eng/ble/

User ID : wcsdk
Password : cnipnf2430
*/

/**
 * Device Specification (PDF in Dropbox Development/Guides/Devices/etc.):
 * * Uses A&D custom ble service as well as standard blood pressure profile
 * * Has two modes: Link and Solo. Operation mode can be identified from advertising info
 * * Link custom 128bit UUID: 0x233BF0005A341B6D975C000D5690ABE4, Solo Blood Pressure primary UUID: 0x1810
 * * Can store up to 5 user IDs. Oldest is overwritten when full
 * * Device retains up to 100 readings per user
 * * Can read memory on device without starting measurement
 * * Blood Pressure Service: Measurement is 19 bytes, Feature is 2 bytes (detect movement, cuff fit)
 * * A&D Custom Service: Char 1 is Read/Write commands, Char 2 is Notification responses
 * * * Write 0x0001 to client characteristic config
 * * * Receive Battery Status Notification. 0x00: 0 to 32%, 0x21: 33 to 65%, 0x42: 66 to 99%, 0x64: 100%
 * * * Write 0x04 Time "Stting"(Setting?) command
 * * User Registration 0x17: Send ID, wait 100ms, read and expect ID back, send custom bitmap image for OLED
 * * Send 0x15 start measurement.
 * * * Notifications sent throughout
 * * * Indications sent on measurement finish.
 * * * Gap of 10 seconds after receiving indication means all data has been received, and can disconnect
 * * Nickname is either 10 characters or a 16x19 dot image (row is 2 bytes, 19 rows)
 */

public class BluetoothLe_AnD_UA1200BLE extends BloodPressureBluetoothLeDevice
{
    private final static String TAG = BluetoothLe_AnD_UA1200BLE.class.getSimpleName();

    private static final int BLOOD_PRESSURE_MEASUREMENT_LEN_BYTES = 19;
    private static final int BATTERY_LEVEL_OP_CODE = 0x41;
    private static final int OP_CODE_INDEX = 2;

    public final static String ACTION_PAIRING = "BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING";
    public final static String ACTION_PAIRING_SUCCESS = "BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING_SUCCESS";
    public final static String ACTION_PAIRING_FAILURE = "BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING_FAILURE";
    public final static String ACTION_CONNECTED = "BluetoothLe_AnD_UA1200BLE.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED = "BluetoothLe_AnD_UA1200BLE.ACTION_DISCONNECTED";
    public final static String ACTION_UNEXPECTED_UNBOND = "BluetoothLe_AnD_UA1200BLE.ACTION_UNEXPECTED_UNBOND";
    public final static String ACTION_DATA_AVAILABLE = "BluetoothLe_AnD_UA1200BLE.ACTION_DATA_AVAILABLE";

    private final BluetoothLeHelper bleHelper;


    public BluetoothLe_AnD_UA1200BLE(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time)
    {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__AND_UA1200BLE);

        bleHelper = new BluetoothLeHelper(ble_device, logger, TAG);

        device_service_uuid = Uuids.BLOOD_PRESSURE_SERVICE_UUID;
    }


    public String getChildTag()
    {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
    }


    @Override
    public void sendConnectedIntentAndContinueConnection()
    {
        Log.e(TAG, "sendConnectedIntent");

        sendConnectedIntent();

        subscribeToAnDCustomService();

        if (isDeviceSessionInProgress())
        {
            connectToCharacteristics();
        }
    }

    public void connectToCharacteristics()
    {
        Log.d(TAG, "connectToCharacteristics");

        if (isReadyToConnectToBLEService())
        {
            subscribeToBloodPressureService();
        }
        else
        {
            Log.e(TAG, "Not ready to connect to BLE service!");
        }
    }

    private void subscribeToAnDCustomService()
    {
        bleHelper.subscribeToCharacteristic(BloodPressureBluetoothLeDevice.AnD_CUSTOM_SERVICE, BloodPressureBluetoothLeDevice.AnD_CHARACTERISTIC_CUSTOM_TWO,
                "AnD Service", this::handleCustomServiceData);
    }

    private void subscribeToBloodPressureService()
    {
        bleHelper.subscribeToCharacteristic(Uuids.BLOOD_PRESSURE_SERVICE_UUID,
                Uuids.BLOOD_PRESSURE_MEASUREMENT,
                "BloodPressure",
                this::handleBloodPressureData);
    }

    private void retryNotify()
    {
        retry_handler.postDelayed(this::connectToCharacteristics, DateUtils.SECOND_IN_MILLIS / 2);
    }

    private boolean isReadyToConnectToBLEService()
    {
        return isDeviceSessionInProgress() && ble_device != null;
    }

    private void handleCustomServiceData(String macAddress, byte[] data)
    {
        Log.e(TAG, "AnD Service: " + Utils.byteArrayToHexString(data));

        if (data.length < 4)
        {
            Log.e(TAG, "Data too short! Len: " + data.length);
            return;
        }

        if (data[OP_CODE_INDEX] == BATTERY_LEVEL_OP_CODE)
        {
            int battery_level = data[3];

            sendBatteryLevelIntent(battery_level, macAddress, data);

            setDeviceTimestamp();
        }
    }

    private void setDeviceTimestamp()
    {
        long timestamp_ms = ntp_time.currentTimeMillis();
        byte[] timestamp_bytes = getTimestampByteArray(timestamp_ms);
        byte[] command_bytes = new byte[10];
        command_bytes[0] = 0x09;
        command_bytes[1] = 0x01;
        command_bytes[2] = 0x04;
        System.arraycopy(timestamp_bytes, 0, command_bytes, 3, 7);

        Log.d(TAG, "Sending: " + Utils.byteArrayToHexString(command_bytes));

        bleHelper.writeCharacteristic(BloodPressureBluetoothLeDevice.AnD_CUSTOM_SERVICE, BloodPressureBluetoothLeDevice.AnD_CHARACTERISTIC_CUSTOM_ONE,
                command_bytes, write_timestamp_success -> {
                    sendTimestampIntent(timestamp_ms, ble_device.getMacAddress(), command_bytes);
                });
    }

    private void userRegistrationSequence()
    {
        byte[] command_bytes = new byte[19];
        command_bytes[0] = 0x12;
        command_bytes[1] = 0x00;
        command_bytes[2] = 0x17;
        command_bytes[18] = 0x01; // User ID

        bleHelper.writeCharacteristic(BloodPressureBluetoothLeDevice.AnD_CUSTOM_SERVICE, BloodPressureBluetoothLeDevice.AnD_CHARACTERISTIC_CUSTOM_ONE,
                command_bytes, success -> {
                    retry_handler.postDelayed(() -> {
                        bleHelper.readCharacteristic(BloodPressureBluetoothLeDevice.AnD_CUSTOM_SERVICE, BloodPressureBluetoothLeDevice.AnD_CHARACTERISTIC_CUSTOM_ONE, ((macAddress, data) -> {
                            Log.d(TAG, "Get User response: " + Utils.byteArrayToHexString(data));
                        }));
                    }, 100);
                });
    }

    private void startMeasurement()
    {
        bleHelper.writeCharacteristic(BloodPressureBluetoothLeDevice.AnD_CUSTOM_SERVICE, BloodPressureBluetoothLeDevice.AnD_CHARACTERISTIC_CUSTOM_ONE,
                new byte[]{0x03, 0x01, 0x15, 0x01}, take_measurement_success -> {
                    Log.d(TAG, "TAKE MEASUREMENT SUCCESS: " + take_measurement_success);
                });
    }

    private byte[] getTimestampByteArray(long timestamp)
    {
        DateTime date_time = new DateTime(timestamp);

        int year = date_time.getYear();
        int month = date_time.getMonthOfYear();
        int day = date_time.getDayOfMonth();
        int hour = date_time.getHourOfDay();
        int minute = date_time.getMinuteOfHour();
        int second = date_time.getSecondOfMinute();

        timezone_offset_to_utc = date_time.getZone().getOffset(timestamp);

        return new byte[]{
                (byte) (year & 0xFF),
                (byte) ((year >> 8) & 0xFF),
                (byte) month,
                (byte) day,
                (byte) hour,
                (byte) minute,
                (byte) second,
        };
    }

    private void handleBloodPressureData(String macAddress, byte[] data)
    {
        Log.d(TAG, "BLOOD_PRESSURE_MEASUREMENT raw data : " + Utils.byteArrayToHexString(data));

        onReceivedData();

        try
        {
            if (isDataCorrectLengthForBloodPressure(data))
            {
                BloodPressureData blood_pressure = parseBloodPressure(data);
                sendBloodPressureIntent(ACTION_DATA_AVAILABLE, blood_pressure, macAddress, data);
            }
            else
            {
                Log.e(TAG, "Blood pressure data not correct length");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        readBattery();
    }

    private boolean isDataCorrectLengthForBloodPressure(byte[] data)
    {
        return data.length == BLOOD_PRESSURE_MEASUREMENT_LEN_BYTES;
    }

    private void onReceivedData()
    {
        last_data_received_ntp_time = ntp_time.currentTimeMillis();
    }

    public void getTimestamp()
    {
        // sendTimestampIntent(ntp_time.currentTimeMillis(), "00:00:00:00:00:00", new byte[]{});

//        if (ble_device != null) {
//            // First-time connection, so go through full timestamp and configuration code...
//            ble_device.read(Uuids.BLOOD_PRESSURE_SERVICE_UUID, Uuids.BLOOD_PRESSURE_MEASUREMENT, event -> {
//                logEventStatus(event);
//
//                if (event.wasSuccess()) {
//                    Log.d(TAG, "getTimestamp : SUCCESS : " + Utils.byteArrayToHexString(event.data()));
//                    long received_timestamp_in_ms = parseDateTime(event.data());
//                    sendTimestampIntent(received_timestamp_in_ms, event.device().getMacAddress(), event.data());
//                } else {
//                    Log.e(TAG, "getTimestamp FAILED : " + event.status().toString());
//
//                    if (event.device().is(CONNECTED)) {
//                        Log.e(TAG, "getTimestamp RETRYING");
//
//                        postGetTimestampRetry();
//                    }
//                }
//            });
//        }
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

    @Override
    public boolean checkLastDataIsRestartRequired(long timeout)
    {
        Log.e(TAG, "checkLastDataIsRestartRequired returning FALSE");

        return false;
    }


    @Override
    public void onDiscovered(BleManager.DiscoveryListener.DiscoveryEvent event)
    {
        super.onDiscovered(event);

        ble_device = event.device();
        bleHelper.setBleDevice(ble_device);
    }
}
