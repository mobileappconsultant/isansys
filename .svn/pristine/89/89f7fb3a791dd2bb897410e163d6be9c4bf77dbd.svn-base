package com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.idevicesinc.sweetblue.BleDevice;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class BluetoothLeLifetouchThree extends BluetoothLeDevice {
    private final static String TAG = BluetoothLeLifetouchThree.class.getSimpleName();

    private static final byte[] lifetouch_three_aes_key_128 = {0x78, 0x36, 0x34, 0x65, 0x12, (byte) 0x83, 0x0D, 0x7A, (byte) 0xDF, 0x1A, 0x50, (byte) 0xBC, (byte) 0xFF, (byte) 0xE0, (byte) 0xB1, (byte) 0xD0};
    public static final int MAX_PACKET_SIZE_BYTES = 257;

    private static final int KHZ_SAMPLE_PERIOD_MS = 1;
    private static final int HUNDRED_HZ_SAMPLE_PERIOD_MS = 10;
    private static final int HUNDRED_HZ_ENABLE_OP_CODE = 0x01;
    private static final int KHZ_ENABLE_OP_CODE = 0x02;
    private static final int SETUP_MODE_DISABLE_OP_CODE = 0x00;

    private final UUID SERVICE_ISANSYS = UUID.fromString("00001000-EE81-4555-B724-3DC951D9203E");

    private final UUID CHARACTERISTIC_HEART_BEATS = UUID.fromString("00001003-EE81-4555-B724-3DC951D9203E");  // Indication
    private final UUID CHARACTERISTIC_SETUP_MODE = UUID.fromString("00001004-EE81-4555-B724-3DC951D9203E");  // Indication, Write
    private final UUID CHARACTERISTIC_RAW_ACCELEROMETER_MODE = UUID.fromString("00001005-EE81-4555-B724-3DC951D9203E");  // Indication, Write
    private final UUID CHARACTERISTIC_LEADS_OFF = UUID.fromString("00001006-EE81-4555-B724-3DC951D9203E");  // Indication
    private final UUID CHARACTERISTIC_TIMESTAMP = UUID.fromString("00001008-EE81-4555-B724-3DC951D9203E");  // Read
    private final UUID CHARACTERISTIC_TURN_OFF = UUID.fromString("00001009-EE81-4555-B724-3DC951D9203E");  // Write
    private final UUID CHARACTERISTIC_ENABLE_NIGHT_MODE = UUID.fromString("0000100A-EE81-4555-B724-3DC951D9203E");  // Write
    private final UUID CHARACTERISTIC_AUTHENTICATION = UUID.fromString("0000100C-EE81-4555-B724-3DC951D9203E");  // Read/Write
    private final UUID CHARACTERISTIC_BATTERY_LEVEL = UUID.fromString("0000100D-EE81-4555-B724-3DC951D9203E");  // Indication
    private final UUID CHARACTERISTIC_BEGIN_DFU = UUID.fromString("0000100E-EE81-4555-B724-3DC951D9203E");  // Write
    private final UUID CHARACTERISTIC_DISCONNECT = UUID.fromString("0000100F-EE81-4555-B724-3DC951D9203E");  // Write

    public final static String DATATYPE_BEAT_INFO = "BEAT_INFO";
    public final static String DATATYPE_SETUP_MODE_RAW_SAMPLES = "SETUP_MODE_RAW_SAMPLES";
    public final static String DATATYPE_HEART_BEAT_LEADS_OFF = "DATATYPE_HEART_BEAT_LEADS_OFF";
    public final static String DATATYPE_RAW_ACCELEROMETER_RAW_SAMPLES = "RAW_ACCELEROMETER_RAW_SAMPLES";

    public final static String ACTION_CONNECTED = "com.isansys.patientgateway.ACTION_CONNECTED.LifetouchThree";
    public final static String ACTION_DISCONNECTED = "com.isansys.patientgateway.ACTION_DISCONNECTED.LifetouchThree";
    public final static String ACTION_DATA_AVAILABLE = "com.isansys.patientgateway.ACTION_DATA_AVAILABLE.LifetouchThree";
    public final static String ACTION_TURNED_OFF = "com.isansys.patientgateway.ACTION_TURNED_OFF.LifetouchThree";
    public final static String ACTION_RESCAN_REQUIRED = "com.isansys.patientgateway.ACTION_RESCAN_REQUIRED.LifetouchThree";
    public final static String ACTION_AUTHENTICATION_PASSED = "com.isansys.patientgateway.ACTION_AUTHENTICATION_PASSED.LifetouchThree";
    public final static String ACTION_AUTHENTICATION_FAILED = "com.isansys.patientgateway.ACTION_AUTHENTICATION_FAILED.LifetouchThree";

    public final static String BATTERY_PERCENTAGE = "com.isansys.patientgateway.BATTERY_PERCENTAGE.LifetouchThree";
    public final static String BATTERY_VOLTAGE = "com.isansys.patientgateway.BATTERY_VOLTAGE.LifetouchThree";
    public final static String BATTERY_PERCENTAGE_TIMESTAMP = "com.isansys.patientgateway.BATTERY_PERCENTAGE_TIMESTAMP.Lifetouch";

    public final static String SETUP_MODE_DATA = "com.isansys.patientgateway.SETUP_MODE_DATA";

    public final static String RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES = "com.isansys.patientgateway.RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES";
    public final static String RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES = "com.isansys.patientgateway.RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES";
    public final static String RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES = "com.isansys.patientgateway.RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES";
    public final static String RAW_ACCELEROMETER_MODE__TIMESTAMPS = "com.isansys.patientgateway.RAW_ACCELEROMETER_MODE__TIMESTAMPS";

    public final static String HEART_BEAT_INFO = "com.isansys.patientgateway.HEART_BEAT_INFO";
    public final static String HEART_BEAT_LEADS_OFF = "com.isansys.patientgateway.HEART_BEAT_LEADS_OFF";
    public final static String HEART_BEAT_LEADS_OFF_TIMESTAMP = "com.isansys.patientgateway.HEART_BEAT_LEADS_OFF_TIMESTAMP";
    public final static String HEART_BEATS_PENDING = "com.isansys.patientgateway.HEART_BEATS_PENDING";

    private boolean khzSetupMode = false;

    private final LifetouchThreeAuthentication auth;
    private final LifetouchThreeHeartbeats heartbeatParser;
    private final LifetouchThreeSetupMode setupModeParser;
    private final LifetouchThreeRawAccel accelModeParser;
    private final LifetouchThreeBattery batteryParser;

    private LifetouchThreeFirmwareUpdater lifetouch_dfu;

    public BluetoothLeLifetouchThree(ContextInterface context_interface,
                                     RemoteLogging logger,
                                     BluetoothLeDeviceController controller,
                                     TimeSource gateway_time) {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__LIFETOUCH_THREE);

        auth = new LifetouchThreeAuthentication(logger, lifetouch_three_aes_key_128);

        heartbeatParser = new LifetouchThreeHeartbeats(logger, auth);
        setupModeParser = new LifetouchThreeSetupMode(logger, auth);
        accelModeParser = new LifetouchThreeRawAccel(logger, auth);
        batteryParser = new LifetouchThreeBattery(logger, auth);
    }

    public String getChildTag() {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
    }

    private interface WriteCallback {
        void call(boolean success);
    }

    private void writeCharacteristic(UUID characteristic, final byte[] data, @Nullable WriteCallback callback) {
        if (ble_device == null)
            Log.e(TAG, "Cannot write: BLE device is null!");
        else {
            ble_device.write(SERVICE_ISANSYS, characteristic, data, event -> {
                logEventStatus(event);

                if (callback != null) {
                    callback.call(event.wasSuccess());
                }
            });
        }
    }

    private void writeCharacteristic(UUID characteristic, final byte[] data) {
        writeCharacteristic(characteristic, data, null);
    }

    private interface ReadCallback {
        void call(final String macAddress, final byte[] data);
    }

    private void readCharacteristic(UUID characteristic, ReadCallback callback) {
        if (ble_device == null) {
            Log.e(TAG, "cannot read: BLE device is null!");
        }
        else {
            ble_device.read(SERVICE_ISANSYS, characteristic, event -> {
                logEventStatus(event);
                if (event.wasSuccess() && event.data().length > 0) {
                    last_data_received_ntp_time = ntp_time.currentTimeMillis();
                    callback.call(event.macAddress(), event.data());
                }
                else
                    Log.e(TAG, "Read characteristic failed!");
            });
        }
    }

    public void sendTurnOffCommand() {
        Log.d(TAG, "Sending Turnoff Command");
        final byte[] turn_off_code = {0x42, 0x41, 0x44, 0x47, 0x45, 0x52};
        writeCharacteristic(CHARACTERISTIC_TURN_OFF, turn_off_code, success -> sendIntent(ACTION_TURNED_OFF));
    }

    public void sendTestModeCommand(boolean enable) {
        // Unimplemented in Lifetouch Three
    }

    public void sendTimestamp(long timestamp_now_in_milliseconds, final byte[] data) {
        Log.d(TAG, "Sending the time stamp to the Lifetouch : time stamp is " +
                TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(timestamp_now_in_milliseconds));
        writeCharacteristic(CHARACTERISTIC_TIMESTAMP, data);
    }

    @Override
    public void enableDisableKHzSetupMode(final boolean enable) {
        khzSetupMode = enable;
    }

    public void enableDisableSetupMode(final boolean enter_setup_mode) {
        Log.d(TAG, enter_setup_mode ? "Enabling setup mode" : "Disabling setup mode");
        byte[] data = setupModeOpCode(enter_setup_mode);
        writeCharacteristic(CHARACTERISTIC_SETUP_MODE, data);
    }

    private byte[] setupModeOpCode(final boolean enter_setup_mode) {
        byte[] data = new byte[] { (byte)SETUP_MODE_DISABLE_OP_CODE };
        if (enter_setup_mode) {
            data[0] = (byte)(khzSetupMode ? KHZ_ENABLE_OP_CODE : HUNDRED_HZ_ENABLE_OP_CODE);
        }
        return data;
    }

    public void enableDisableRawAccelerometerMode(final boolean enter_raw_accelerometer_mode) {
        Log.d(TAG, enter_raw_accelerometer_mode ? "Enabling accel mode" : "Disabling accel mode");
        byte[] data = new byte[] {(byte) (enter_raw_accelerometer_mode ? 0x01 : 0x00)};
        writeCharacteristic(CHARACTERISTIC_RAW_ACCELEROMETER_MODE, data);
    }

    public void sendNightModeCommand(final boolean enable_night_mode) {
        Log.d(TAG, enable_night_mode ? "Enabling night mode" : "Disabling night mode");
        byte[] data = new byte[] {(byte) (enable_night_mode ? 0x01 : 0x00)};
        writeCharacteristic(CHARACTERISTIC_ENABLE_NIGHT_MODE, data);
    }

    public void setRadioOff(byte timeTillRadioOff, byte timeWithRadioOff) {
        // Unimplemented in LT3
    }

    private interface DataHandler {
        void receive(final String macAddress, final byte[] data);
    }

    private void subscribeToCharacteristic(UUID characteristic, String name, DataHandler handler) {
        if (ble_device == null) {
            Log.e(TAG, "Cannot subscribe to " + name + " as ble_device is NULL!");
            return;
        }

        ble_device.enableNotify(SERVICE_ISANSYS, characteristic, event -> {
            switch (event.type()) {
                case ENABLING_NOTIFICATION: {
                    logEnablingNotification(name, event);
                }
                break;

                case INDICATION:
                case NOTIFICATION: {
                    if (event.wasSuccess() && event.data().length > 0) {
                        last_data_received_ntp_time = ntp_time.currentTimeMillis();
                        handler.receive(event.macAddress(), event.data());
                    }
                    else
                        Log.e(TAG, name + " read failed!");
                }
                break;

                default: {
                    Log.i(TAG, name + " : " + Utils.byteArrayToHexString(event.data()));
                }
            }
        });
    }

    private void requestLargeBLEPackets() {
        ble_device.setMtu(MAX_PACKET_SIZE_BYTES, event -> {
            Log.i(TAG, "SET MTU: " + event.data_string());
        });
    }

    public void connectToCharacteristics() {
        Log.d(TAG, "connectToCharacteristics - starting authentication");
        requestLargeBLEPackets();
        authenticate();
    }

    private void authenticate() {
        Log.d(TAG, "authenticate called");
        readCharacteristic(CHARACTERISTIC_AUTHENTICATION, (macAddress, data) -> {
            handleOneTimeKey(data);
        });
    }

    private void handleOneTimeKey(byte[] data) {
        Log.d(TAG, "handleOneTimeKey : Received " + Utils.byteArrayToHexString(data));

        byte[] encrypted_data = null;

        try {
            encrypted_data = auth.encrypt(data);
            Log.d(TAG, "handleOneTimeKey : Encrypted to become " + Utils.byteArrayToHexString(encrypted_data));
        }
        catch (Exception e) {
            Log.e(TAG, "handleOneTimeKey token generation failed");
            Log.e(TAG, e.toString());
        }

        // write token
        if (encrypted_data != null) {
            byte[] auth_token = new byte[16];
            System.arraycopy(encrypted_data, 0, auth_token, 0, 16);

            Log.d(TAG, "handleOneTimeKey : Copy as " + Utils.byteArrayToHexString(auth_token));

            writeCharacteristic(CHARACTERISTIC_AUTHENTICATION, auth_token, success -> {
                if (success)
                    authenticationSucceeded();
                else
                    authenticationFailed();
            });
        }
    }

    private void authenticationSucceeded() {
        Log.d(TAG, "authenticationSucceeded - connecting to remaining characteristics");

        subscribeToCharacteristic(CHARACTERISTIC_SETUP_MODE, "Setup Mode", this::handleSetupModeData);
        subscribeToCharacteristic(CHARACTERISTIC_LEADS_OFF, "Leads Off", this::handleLeadsOffData);
        subscribeToCharacteristic(CHARACTERISTIC_RAW_ACCELEROMETER_MODE, "Raw Accel", this::handleRawAccelData);
        subscribeToCharacteristic(CHARACTERISTIC_BATTERY_LEVEL, "Battery Level", this::handleBatteryData);
        subscribeToCharacteristic(CHARACTERISTIC_HEART_BEATS, "Heartbeats", this::handleHeartbeatData);

        sendIntent(new Intent(ACTION_AUTHENTICATION_PASSED));
        authenticated = true;
    }

    private void authenticationFailed() {
        sendIntent(new Intent(ACTION_AUTHENTICATION_FAILED));
        authenticated = false;
    }

    private void handleHeartbeatData(final String macAddress, final byte[] data)
    {
        int end_of_encrypted_data = data.length-4;
        byte [] encrypted_data = Arrays.copyOfRange(data, 0, end_of_encrypted_data);
        byte [] pending_beats_data = Arrays.copyOfRange(data, end_of_encrypted_data, data.length);
        if((end_of_encrypted_data > 0) && (end_of_encrypted_data % 16 == 0))
        {
            ArrayList<HeartBeatInfo> heartbeats = heartbeatParser.parse(encrypted_data);

            int numBeatsPending = heartbeatParser.parseNumPending(pending_beats_data);

            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
            intent.putExtra(DATA_TYPE, DATATYPE_BEAT_INFO);
            intent.putParcelableArrayListExtra(HEART_BEAT_INFO, heartbeats);
            intent.putExtra(HEART_BEATS_PENDING, numBeatsPending);

            sendIntentWithDeviceAddressAndDataAsString(intent, macAddress, data);
        }
        else
        {
            Log.w(TAG, "handleHeartbeatData - encrypted data length not a multiple of AES block size");
        }
    }

    private void handleSetupModeData(final String macAddress, final byte[] data) {
        long sample_period_ms = khzSetupMode ? KHZ_SAMPLE_PERIOD_MS : HUNDRED_HZ_SAMPLE_PERIOD_MS;
        ArrayList<MeasurementSetupModeDataPoint> dataPoints = setupModeParser.parse(data, sample_period_ms);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SETUP_MODE_DATA, dataPoints);

        Intent intent = new Intent(ACTION_DATA_AVAILABLE);
        intent.putExtra(DATA_TYPE, DATATYPE_SETUP_MODE_RAW_SAMPLES);
        intent.putExtras(bundle);

        sendIntentWithDeviceAddressAndDataAsString(intent, macAddress, data);
    }

    private void handleLeadsOffData(final String macAddress, final byte[] encrypted_data)
    {
        byte[] data = auth.decrypt(encrypted_data);
        int leads_off = data[0];

        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
        intent.putExtra(DATA_TYPE, DATATYPE_HEART_BEAT_LEADS_OFF);
        intent.putExtra(HEART_BEAT_LEADS_OFF_TIMESTAMP, PatientGatewayService.getNtpTimeNowInMillisecondsStatic());

        if (leads_off == 0) {
            Log.d(TAG, "----==< LEADS ON RECEIVED >==----");
            intent.putExtra(HEART_BEAT_LEADS_OFF, false);
        } else {
            Log.d(TAG, "----==< LEADS OFF RECEIVED >==----");
            intent.putExtra(HEART_BEAT_LEADS_OFF, true);
        }

        sendIntentWithDeviceAddressAndDataAsString(intent, macAddress, data);
    }

    private void handleRawAccelData(final String macAddress, final byte[] data)
    {
        Log.d(TAG, "UUID_RAW_ACCELEROMETER_MODE_RAW_SAMPLES is " + Utils.byteArrayToHexString(data));
        AccelData accelData = accelModeParser.parse(data);

        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
        intent.putExtra(DATA_TYPE, DATATYPE_RAW_ACCELEROMETER_RAW_SAMPLES);
        intent.putExtra(RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES, accelData.x);
        intent.putExtra(RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES, accelData.y);
        intent.putExtra(RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES, accelData.z);
        intent.putExtra(RAW_ACCELEROMETER_MODE__TIMESTAMPS, accelData.timestamps);

        sendIntentWithDeviceAddressAndDataAsString(intent, macAddress, data);
    }

    private void handleBatteryData(final String macAddress, final byte[] encrypted_data)
    {
        byte[] data = auth.decrypt(encrypted_data);

        Log.e(TAG, "UUID_ISANSYS_BATTERY_LEVEL is " + Utils.byteArrayToHexString(data));
        BatteryLevel level = batteryParser.parse(data);

        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
        intent.putExtra(DATA_TYPE, DATATYPE_BATTERY_LEVEL);
        intent.putExtra(DATA_AS_STRING, String.valueOf(level.percentage));
        intent.putExtra(BATTERY_VOLTAGE, level.averageVoltage_mV);
        intent.putExtra(BATTERY_PERCENTAGE, level.percentage);
        intent.putExtra(BATTERY_PERCENTAGE_TIMESTAMP, level.timestampMS);
        sendIntentWithDeviceAddress(intent, macAddress);
    }

    @Override
    public void keepAlive() {
        // Unimplemented in LT3
    }

    @Override
    public void sendConnectedIntentAndContinueConnection() {
        sendConnectedIntent();
        getTimestamp();
    }

    public void getTimestamp() {
        if (isDeviceSessionInProgress()) {
            Log.d(TAG, "getTimestamp - session started so getting timestamp");
            readCharacteristic(CHARACTERISTIC_TIMESTAMP, this::handleTimestamp);
        }
        else
            Log.d(TAG, "getTimestamp - no session started so NOT getting timestamp");
    }

    private void handleTimestamp(final String macAddress, final byte[] data)
    {
        Log.d(TAG, "CHARACTERISTIC_GET_TIMESTAMP : " + Utils.byteArrayToHexString(data));
        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
        intent.putExtra(DATA_TYPE, DATATYPE_GET_TIMESTAMP);
        sendIntentWithDeviceAddressAndDataAsString(intent, macAddress, data);
    }

    @Override
    public void sendDisconnectedIntent() {
        sendIntent(ACTION_DISCONNECTED);
        disconnectFirmwareUpdaterIfExists();
    }

    @Override
    public void removeDevice() {
        disconnectFirmwareUpdaterIfExists();
        disconnectThenUnbondAndForget();
    }

    private void disconnectFirmwareUpdaterIfExists()
    {
        if (lifetouch_dfu != null)
            lifetouch_dfu.onDisconnect();
    }

    private void disconnectThenUnbondAndForget() {
        byte[] disconnect_payload = new byte[1];
        writeCharacteristic(CHARACTERISTIC_DISCONNECT, disconnect_payload, success -> {
            ble_device.undiscover();
            ble_device.unbond();
            ble_device.clearAllData();
        });
    }

    @Override
    public void sendRescanRequiredIntent() {
        sendIntent(ACTION_RESCAN_REQUIRED);
    }

    @Override
    protected void sendUnexpectedlyUnbondedIntent() {
        // Not implemented on this device but function needs to exist
    }

    @Override
    public void sendConnectedIntent() {
        sendIntent(ACTION_CONNECTED);
    }

    @Override
    protected String getActionDataAvailableString() {
        return ACTION_DATA_AVAILABLE;
    }

    public void firmwareOtaUpdate(byte[] firmware_binary_array) {
        lifetouch_dfu = new LifetouchThreeFirmwareUpdater(gateway_context_interface, Log, ble_device, device_type);

        byte[] begin_dfu_payload = new byte[1];
        writeCharacteristic(CHARACTERISTIC_BEGIN_DFU, begin_dfu_payload, success -> {
            if (success)
                lifetouch_dfu.update(firmware_binary_array);
        });
    }

    public void setMeasurementInterval(int interval_in_seconds) {
        // Not implemented on this device but function needs to exist
    }

    @Override
    public void resetStateVariables() {
        super.resetStateVariables();

        setupModeParser.reset();
    }
}
