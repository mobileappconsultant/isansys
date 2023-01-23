package com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_TM2441;

import static com.idevicesinc.sweetblue.BleDeviceState.CONNECTED;
import static com.idevicesinc.sweetblue.BleDeviceState.UNBONDED;

import android.text.format.DateUtils;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceConfig;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.utils.Uuids;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.GenericStartStopTimer;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BloodPressureBluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDevice;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.BluetoothLeDeviceController;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public class BluetoothLe_AnD_TM2441 extends BloodPressureBluetoothLeDevice
{
    private final static String TAG = BluetoothLe_AnD_TM2441.class.getSimpleName();

    private final UUID UUID_SERVICE_CUSTOM_SERVICE_ONE = UUID.fromString("11127000-B364-11E4-AB27-0800200C9A66");
        private final UUID UUID_CHARACTERISTIC_CUSTOM_ONE = UUID.fromString("11127001-B364-11E4-AB27-0800200C9A66");

    private final UUID UUID_SERVICE_CUSTOM_SERVICE_TWO = UUID.fromString("1A0934F0-B364-11E4-AB27-0800200C9A66");
    private final UUID UUID_CHARACTERISTIC_CUSTOM_TWO = UUID.fromString("1A0934F1-B364-11E4-AB27-0800200C9A66");

    public final static String ACTION_PAIRING = "BluetoothLe_AnD_TM2441.ACTION_PAIRING";
    public final static String ACTION_PAIRING_SUCCESS = "BluetoothLe_AnD_TM2441.ACTION_PAIRING_SUCCESS";
    public final static String ACTION_PAIRING_FAILURE = "BluetoothLe_AnD_TM2441.ACTION_PAIRING_FAILURE";
    public final static String ACTION_CONNECTED = "BluetoothLe_AnD_TM2441.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED = "BluetoothLe_AnD_TM2441.ACTION_DISCONNECTED";
    public final static String ACTION_UNEXPECTED_UNBOND  = "BluetoothLe_AnD_TM2441.ACTION_UNEXPECTED_UNBOND";
    public final static String ACTION_DATA_AVAILABLE = "BluetoothLe_AnD_TM2441.ACTION_DATA_AVAILABLE";

    private boolean send_hdg_name;

    private final long ON_DISCONNECT_RE_SCAN_DELAY = 60 * DateUtils.SECOND_IN_MILLIS;

    /* Command Definitions     */
    private final int SET_HDG_NAME_REQUEST = 0x11;
    private final int RAW_ACCELEROMETER_DATA_REQUEST = 0x53;
    private final int RAW_BP_WAVEFORM_DATA_REQUEST = 0x55;

    /* Command ID (sub-command) */
    private final int NEW_DATA_REQUEST = 0x00;
    private final int NEXT_DATA_REQUEST = 0x01;
    //private final int RESEND_DATA_REQUEST = 0x01;
    private final int TRANSFER_COMPLETE = 0x03;
    private final int SPECIFIC_DATA_REQUEST = 0x04;
    //private final int SECTOR_INFO_REQUEST = 0x05;

    /* command arrays for the custom characteristic */
    private final byte[] acceleration_new_sector_data_request = new byte[]{0x08, 0x01, RAW_ACCELEROMETER_DATA_REQUEST, NEW_DATA_REQUEST, 0x00, 0x00, 0x00, 0x00, 0x00};
    private final byte[] dc_pressure_new_sector_data_request = new byte[]{0x08, 0x01, RAW_BP_WAVEFORM_DATA_REQUEST, NEW_DATA_REQUEST, 0x00, 0x00, 0x00, 0x00, 0x00};

    private final byte [] accelerometer_next_data_command = new byte [] {0x07, 0x01, RAW_ACCELEROMETER_DATA_REQUEST, NEXT_DATA_REQUEST, 0x00, 0x00, 0x00, 0x01};
    private final byte [] dc_pressure_next_data_command = new byte [] {0x07, 0x01, RAW_BP_WAVEFORM_DATA_REQUEST, NEXT_DATA_REQUEST, 0x00, 0x00, 0x00, 0x01};

    private final byte [] accelerometer_end_of_data_command = new byte [] {0x03, 0x01, RAW_ACCELEROMETER_DATA_REQUEST, TRANSFER_COMPLETE};
    private final byte [] dc_pressure_end_of_data_command = new byte [] {0x03, 0x01, RAW_BP_WAVEFORM_DATA_REQUEST, TRANSFER_COMPLETE};

    private final byte[] sector_data_request_range__generic = new byte[]{0x0B, 0x01, 0x00, SPECIFIC_DATA_REQUEST, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};

    //private final byte[] acceleration_sector_info_request = new byte[]{0x03, 0x01, RAW_ACCELEROMETER_DATA_REQUEST, SECTOR_INFO_REQUEST};
    //private final byte[] dc_pressure_sector_info_request = new byte[]{0x03, 0x01, RAW_BP_WAVEFORM_DATA_REQUEST, SECTOR_INFO_REQUEST};

    // ISAN 0000 0000 - ToDo add in unique ID as hex bytes here?
    final byte[] hdg_command = new byte[]{0x0F, 0x01, SET_HDG_NAME_REQUEST, 0x00, 0x49, 0x53, 0x41, 0x4E, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};


    /* flags for whether data is pending on the device */
    boolean bp_data_available = false;
    boolean raw_accelerometer_data_available = false;
    boolean raw_pressure_data_available = false;

    boolean accelerometer_data_in_progress = false;
    boolean pressure_data_in_progress = false;


    int sector_indication_count = 0;
    final byte [] sector_data = new byte[26*20];
    int sectors_remaining_count = 0;
    int sector_to_sync = 0;

    final boolean download_raw_data = false; // we want to turn off raw data download completely for now

    public BluetoothLe_AnD_TM2441(ContextInterface context_interface, RemoteLogging logger, BluetoothLeDeviceController controller, TimeSource gateway_time)
    {
        super(context_interface, logger, controller, gateway_time, DeviceType.DEVICE_TYPE__AND_ABPM_TM2441);

        device_service_uuid = Uuids.BLOOD_PRESSURE_SERVICE_UUID;

        send_hdg_name = false;
    }


    public String getChildTag()
    {
        return BluetoothLeDevice.class.getSimpleName() + "-" + TAG;
    }


    private void connectToAnDCustomCharacteristicNotification()
    {
        Log.d(TAG, "connectToAnDCustomCharacteristicNotification called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToAnDCustomCharacteristicNotification executing enableNotify");

            ble_device.enableNotify(UUID_SERVICE_CUSTOM_SERVICE_ONE, UUID_CHARACTERISTIC_CUSTOM_ONE, event -> {
                logEventStatus(event);

                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToAnDCustomCharacteristicNotification", event);
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if(event.wasSuccess())
                        {
                            Log.d(TAG, "connectToAnDCustomCharacteristicNotification raw data : " + Utils.byteArrayToHexString(event.data()));

                            /* handle the response to a command sent over characteristic one */
                            handleCommandResponse(event.data());

                            // Update this variable. If nothing received within X sec
                            last_data_received_ntp_time = ntp_time.currentTimeMillis();
                        }
                        else
                        {
                            Log.e(TAG, "connectToAnDCustomCharacteristicNotification notify FAILED");
                        }
                    }
                    break;

                    default:
                    {
                        Log.e(TAG, "Default is " + Utils.byteArrayToHexString(event.data()));
                    }
                }
            });
        }
    }


    public void getTimestamp()
    {
        Log.d(TAG, "getTimestamp");

        if (ble_device != null)
        {
            Log.d(TAG, "getTimestamp read");

            if (ble_device.is(CONNECTED))
            {
                startRetryTimeoutHandler();

                ble_device.read(Uuids.BLOOD_PRESSURE_SERVICE_UUID, Uuids.DATE_TIME, event -> {
                    logEventStatus(event);

                    if (event.wasSuccess())
                    {
                        Log.d(TAG, "Uuids.BLOOD_PRESSURE_SERVICE_UUID, Uuids.DATE_TIME : " + Utils.byteArrayToHexString(event.data()));

                        last_data_received_ntp_time = ntp_time.currentTimeMillis();

                        long received_timestamp_in_ms = parseDateTime(event.data());

                        GenericStartStopTimer.cancelTimer(retry_timeout_timer, Log);

                        sendTimestampIntent(received_timestamp_in_ms, event.device().getMacAddress(), event.data());
                    }
                    else
                    {
                        Log.e(TAG, "getTimestamp FAILED");

                        if(event.device().is(CONNECTED))
                        {
                            postGetTimestampRetry();
                        }
                    }
                });
            }
        }
    }


    private void postGetTimestampRetry()
    {
        retry_handler.postDelayed(this::getTimestamp, DateUtils.SECOND_IN_MILLIS/2);
    }


    public void connectToCharacteristics()
    {
        Log.e(TAG, "connectToCharacteristics");

        connectToBloodPressureServiceNotification(); // BP data

        if(download_raw_data)
        {
            connectToAnDCustomCharacteristicNotification(); // Used for raw data commands and set HDG name
                                                            // Given HDG name is set before we subscribe to this,
                                                            // effectively only used for raw data commands.
            connectToCustomServiceServiceTwoNotification(); // only used for raw data
        }
    }


    @Override
    public void sendConnectedIntentAndContinueConnection()
    {
        Log.e(TAG, "sendConnectedIntentAndContinueConnection");

        sendConnectedIntent();

        getTimestamp();

        if (send_hdg_name)
        {
            Log.d(TAG, "writing HDG name...");

            // The HDG transmits the HDG name to the ABPM through the HDG name setting command. The ABPM sends initial setting end response after receiving command.
            sendCommand(hdg_command);

            // Only valid to send once after initial pairing - so set to false now so we don't keep re-trying
            // every time we connect if the initial attempt fails.
            send_hdg_name = false;
        }

        if(isDeviceSessionInProgress())
        {
            connectToCharacteristics();
        }
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
        ble_device = event.device();

        just_discovered = true;

        int manufacturer_id = ble_device.getManufacturerId();
        byte [] advertising_data = ble_device.getManufacturerData();

        Log.d(TAG, "onDiscovered TM2441 : " + manufacturer_id);


        BleDeviceConfig config = BleDeviceConfig.newNulled(); // get a nulled version, so that it doesn't override any of the ble manager config settings accidentally.
        config.reconnectFilter = new DoNotReconnectFilter();

        // set the non-nullable settings to match the ones we have in the BleDeviceController
        config.autoEnableNotifiesOnReconnect = false;

        // set false if android 9, otherwise true
        config.alwaysBondOnConnect = true;

        config.tryBondingWhileDisconnected = !isAndroidNineOrMore();

        ble_device.setConfig(config);

        if (event.was(BleManager.DiscoveryListener.LifeCycle.UNDISCOVERED))
        {
            Log.d(getChildTag(), getDescriptiveDeviceName() + " UNDISCOVERED");

            return;
        }
        else if(unbonded_during_session)
        {
            // don't re-connect - user will trigger manually
            return;
        }
        else if(ble_device.is(UNBONDED))
        {
            ble_device.setListener_Bond(m_bond_listener);

            sendPairingIntent();

            executeConnect();

            return;
        }
        else if(advertising_data.length > 0)
        {
            String data = Utils.byteArrayToHexString(advertising_data);

            Log.d(TAG, "onDiscovered TM2441 : " + data);

            bp_data_available = ((advertising_data[0] & (byte)(0x04)) != 0);
            raw_pressure_data_available = ((advertising_data[0] & (byte)(0x02)) != 0);
            raw_accelerometer_data_available = ((advertising_data[0] & (byte)(0x01)) != 0);

            boolean raw_data_available = raw_accelerometer_data_available || raw_pressure_data_available;

            if((bp_data_available) || ( download_raw_data && raw_data_available))
            {
                Log.d(TAG, "onDiscovered : data available so starting connection");

                executeConnect();

                return;
            }
        }

        just_discovered = false;

        device_controller.postBleReScan(ON_DISCONNECT_RE_SCAN_DELAY);
    }


    public final BleDevice.BondListener m_bond_listener = event -> {
        Log.e(getChildTag(), "Sweetblue BondEvent for " + getDescriptiveDeviceName() + "   " + event.toString());

        ble_device = event.device();

        if (event.wasSuccess())
        {
            Log.d(getChildTag(), getDescriptiveDeviceName() + " bonded");

            sendPairingSuccessIntent();
        }
        else
        {
            sendPairingFailureIntent();
        }
    };

    @Override
    public void resetStateVariables()
    {
        super.resetStateVariables();

        // Set send hdg name to true, as we only do this at the start/end of a device session.
        send_hdg_name = true;
    }


    /**
     * Sector data syncing code
     *
     */
    private void connectToCustomServiceServiceTwoNotification()
    {
        Log.d(TAG, "connectToCustomServiceServiceTwoNotification called");

        if (ble_device != null)
        {
            Log.d(TAG, "connectToCustomServiceServiceTwoNotification executing enableNotify");

            ble_device.enableNotify(UUID_SERVICE_CUSTOM_SERVICE_TWO, UUID_CHARACTERISTIC_CUSTOM_TWO, event -> {
                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        logEnablingNotification("connectToCustomServiceServiceTwoNotification", event);

                        if(download_raw_data)
                        {
                            if(raw_accelerometer_data_available)
                            {
                                accelerometer_data_in_progress = true;
                                pressure_data_in_progress = false;
                                sector_indication_count = 0;

                                sendCommand(acceleration_new_sector_data_request);
                            }
                            else if(raw_pressure_data_available)
                            {
                                accelerometer_data_in_progress = false;
                                pressure_data_in_progress = true;
                                sector_indication_count = 0;

                                sendCommand(dc_pressure_new_sector_data_request);
                            }
                        }
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if (event.wasSuccess())
                        {
                            // No logging here so we don't spam the log files.
//                                Log.d(TAG, "connectToCustomServiceServiceTwoNotification raw data : " + Utils.byteArrayToHexString(event.data()));
//                                logEventStatus(event);

                            System.arraycopy(sector_data, sector_indication_count*20, event.data(), 0, event.data().length);

                            sector_indication_count ++;

                            if(sector_indication_count == 26)
                            {
                                handleReceivedSector();
                            }

                            // Update this variable. If nothing received within X sec
                            last_data_received_ntp_time = ntp_time.currentTimeMillis();
                        }
                        else
                        {
                            Log.e(TAG, "connectToCustomServiceServiceTwoNotification notify FAILED");
                        }
                    }
                    break;

                    default:
                    {
                        logEventStatus(event);
                    }
                }
            });
        }
    }


    private void handleReceivedSector()
    {
        if(download_raw_data)
        {
            if (accelerometer_data_in_progress)
            {
                handleReceivedAccelerometerSector();
            }
            else if (pressure_data_in_progress)
            {
                handleReceivedPressureSector();
            }
            else
            {
                Log.w(TAG, "handleReceivedSector called but no download in progress");
            }
        }
    }


    private void handleReceivedAccelerometerSector()
    {
        sector_indication_count = 0;
        sectors_remaining_count--;

        Log.d(TAG, "handleReceivedAccelerometerSector : remaining sectors = " + sectors_remaining_count);

        if(sectors_remaining_count <= 0)
        {
            // We've synced all the accelerometer data, so set these both to false
            accelerometer_data_in_progress = false;
            raw_accelerometer_data_available = false;

            // now send the end of data command
            sendCommand(accelerometer_end_of_data_command);
        }
        else
        {
            // keep syncing until there's no data on the device
            sendCommand(accelerometer_next_data_command);
        }
    }


    private void handleReceivedPressureSector()
    {
        sector_indication_count = 0;
        sectors_remaining_count--;

        Log.d(TAG, "handleReceivedPressureSector : remaining sectors = " + sectors_remaining_count);

        if(sectors_remaining_count <= 0)
        {
            // We've synced all the pressure data, so set these both to false
            pressure_data_in_progress = false;
            raw_pressure_data_available = false;

            // now send the end of data command
            sendCommand(dc_pressure_end_of_data_command);
        }
        else
        {
            // keep syncing until there's no data on the device
            sendCommand(dc_pressure_next_data_command);
        }
    }


    private void sendCommand(byte[] value)
    {
        ble_device.write(UUID_SERVICE_CUSTOM_SERVICE_ONE, UUID_CHARACTERISTIC_CUSTOM_ONE, value, event -> {
            logEventStatus(event);

            if (event.wasSuccess())
            {
                Log.e(TAG, "sendCommand enable write successful: " + Utils.byteArrayToHexString(event.data()));
            }
            else
            {
                Log.e(TAG, "sendCommand write FAILED : " + event.status().toString());
            }
        });
    }


    private void handleCommandResponse(byte [] response_data)
    {
        int length = response_data[0];
        int command = -1;
        int command_id = -1;

        if(length >= 3)
        {
            command = response_data[2];
            command_id = response_data[3];
        }

        switch(command)
        {
            case RAW_ACCELEROMETER_DATA_REQUEST:
            case RAW_BP_WAVEFORM_DATA_REQUEST:
            {
                switch(command_id)
                {
                    case NEW_DATA_REQUEST:
                    {
                        if(download_raw_data && (length == 11))
                        {
                            // parse starting sector
                            int start_sector_number = (response_data[4]&0xFF) << 24;
                            start_sector_number += (response_data[5]&0xFF) << 16;
                            start_sector_number += (response_data[6]&0xFF) << 8;
                            start_sector_number += (response_data[7]&0xFF);

                            Log.d(TAG, "SYNCING SECTOR NUMBER: " + start_sector_number);

                            sector_to_sync = start_sector_number;


                            // parse remaining sectors
                            int remaining_sectors = (response_data[8]&0xFF) << 24;
                            remaining_sectors += (response_data[9]&0xFF) << 16;
                            remaining_sectors += (response_data[10]&0xFF) << 8;
                            remaining_sectors += (response_data[11]&0xFF);

                            Log.d(TAG, "GOT NUMBER OF REMAINING SECTORS: " + remaining_sectors);

                            sectors_remaining_count = remaining_sectors;
                        }
                    }
                    break;

                    case NEXT_DATA_REQUEST: // next data request
                    {

                    }
                    break;

                    case TRANSFER_COMPLETE: // transmit complete
                    {
                        // start the raw BP data sync if necessary...
                        if(download_raw_data && (command == RAW_ACCELEROMETER_DATA_REQUEST) && (raw_pressure_data_available))
                        {
                            Log.d(TAG, "handleCommandResponse accelerometer data complete, so starting pressure data download");

                            accelerometer_data_in_progress = false;
                            pressure_data_in_progress = true;
                            sector_indication_count = 0;

                            sendCommand(dc_pressure_new_sector_data_request);
                        }
                    }
                    break;

                    case SPECIFIC_DATA_REQUEST:
                    {
                        if(download_raw_data && (length == 11))
                        {
                            // parse starting sector
                            int start_sector_number = (response_data[4] & 0xFF) << 24;
                            start_sector_number += (response_data[5] & 0xFF) << 16;
                            start_sector_number += (response_data[6] & 0xFF) << 8;
                            start_sector_number += (response_data[7] & 0xFF);

                            Log.d(TAG, "SYNCING SECTOR NUMBER: " + start_sector_number);


                            // parse remaining sectors
                            int number_to_sync = (response_data[8] & 0xFF) << 24;
                            number_to_sync += (response_data[9] & 0xFF) << 16;
                            number_to_sync += (response_data[10] & 0xFF) << 8;
                            number_to_sync += (response_data[11] & 0xFF);

                            Log.d(TAG, "SECTORS TO SYNC: " + number_to_sync);
                        }
                    }

                    case 0x05: // info request
                    {
                        if(length == 15)
                        {
                            // parse most recent sector
                            int latest_sector_number = (response_data[4]&0xFF) << 24;
                            latest_sector_number += (response_data[5]&0xFF) << 16;
                            latest_sector_number += (response_data[6]&0xFF) << 8;
                            latest_sector_number += (response_data[7]&0xFF);

                            Log.d(TAG, "LATEST SECTOR NUMBER: " + latest_sector_number);


                            // parse last sector synced sectors
                            int last_synced_sector = (response_data[8]&0xFF) << 24;
                            last_synced_sector += (response_data[9]&0xFF) << 16;
                            last_synced_sector += (response_data[10]&0xFF) << 8;
                            last_synced_sector += (response_data[11]&0xFF);

                            Log.d(TAG, "LAST SYNCED SECTOR: " + last_synced_sector);

                            sectors_remaining_count = latest_sector_number - last_synced_sector;

                            sector_to_sync = last_synced_sector + 1;

                            if(download_raw_data && (sectors_remaining_count > 0))
                            {
                                byte [] command_to_send = sector_data_request_range__generic.clone();

                                if(command == RAW_ACCELEROMETER_DATA_REQUEST)
                                {
                                    accelerometer_data_in_progress = true;
                                    command_to_send[2] = RAW_ACCELEROMETER_DATA_REQUEST;
                                }
                                else if(command == RAW_BP_WAVEFORM_DATA_REQUEST)
                                {
                                    pressure_data_in_progress = true;
                                    command_to_send[2] = RAW_BP_WAVEFORM_DATA_REQUEST;
                                }
                                else
                                {
                                    Log.w(TAG, "handling unknown command, so returning");
                                    return;
                                }

                                command_to_send[4] = (byte)(sector_to_sync >> 24);
                                command_to_send[5] = (byte)(sector_to_sync >> 16);
                                command_to_send[6] = (byte)(sector_to_sync >> 8);
                                command_to_send[7] = (byte)(sector_to_sync);

                                sector_indication_count = 0; // start the data counter at zero as we're requesting a new sector

                                sendCommand(command_to_send); // request the next sector to sync
                            }
                        }
                    }
                }
            }
            break;

            case SET_HDG_NAME_REQUEST:
            {
                send_hdg_name = false;

                int pairing_number = response_data[4]&0xFF;

                Log.d(TAG, "HDG Name set - pairing number is " + pairing_number);
            }
            break;
        }
    }
}
