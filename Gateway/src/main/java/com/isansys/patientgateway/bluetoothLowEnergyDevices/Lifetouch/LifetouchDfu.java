package com.isansys.patientgateway.bluetoothLowEnergyDevices.Lifetouch;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.idevicesinc.sweetblue.BleDevice;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class LifetouchDfu
{
    private final static String TAG = LifetouchDfu.class.getSimpleName();

    private final UUID DFU_SERVICE_UUID = new UUID(0x000015301212EFDEL, 0x1523785FEABCD123L);
    private final UUID DFU_CONTROL_POINT_UUID = new UUID(0x000015311212EFDEL, 0x1523785FEABCD123L);
    private final UUID DFU_PACKET_UUID = new UUID(0x000015321212EFDEL, 0x1523785FEABCD123L);

    private final BleDevice ble_device;

    private final RemoteLogging Log;
    private final ContextInterface gateway_context_interface;
    private final DeviceType device_type;


    public LifetouchDfu(ContextInterface context_interface, RemoteLogging logger, BleDevice lifetouch_device, DeviceType device_type)
    {
        gateway_context_interface = context_interface;

        ble_device = lifetouch_device;

        Log = logger;

        this.device_type = device_type;
    }


    private void writeOpCodeStartDfu()
    {
        if(ble_device != null)
        {
            ble_device.write(DFU_SERVICE_UUID, DFU_CONTROL_POINT_UUID, OP_CODE_START_DFU, event -> {
                if(event.wasSuccess())
                {
                    Log.e(TAG, "writeOpCodeStartDfu SUCCESS : Tx'ed " + Utils.byteArrayToHexString(event.data()));

                    dfu_state = DfuState.DFU_STATE__WRITE_FIRMWARE_IMAGE_SIZE;
                    dfuStateMachine();
                }
                else
                {
                    Log.e(TAG, "writeOpCodeStartDfu FAILED : " + event.status().toString());
                }
            });
        }
        else
        {
            Log.e(TAG, "writeOpCodeStartDfu : COULD NOT WRITE : ble_device == null");
        }
    }


    private void writeOpCodeReceiveFirmwareImage()
    {
        if(ble_device != null)
        {
            ble_device.write(DFU_SERVICE_UUID, DFU_CONTROL_POINT_UUID, OP_CODE_RECEIVE_FIRMWARE_IMAGE, event -> {
                if(event.wasSuccess())
                {
                    Log.e(TAG, "writeOpCodeReceiveFirmwareImage SUCCESS : Tx'ed " + Utils.byteArrayToHexString(event.data()));

                    dfu_state = DfuState.DFU_STATE__WRITE_FIRMWARE_PACKET;
                    dfuStateMachine();
                }
                else
                {
                    Log.e(TAG, "writeOpCodeReceiveFirmwareImage FAILED : " + event.status().toString());
                }
            });
        }
        else
        {
            Log.e(TAG, "writeOpCodeReceiveFirmwareImage : COULD NOT WRITE : ble_device == null");
        }
    }


    private void writeOpCodeValidateFirmwareImage()
    {
        if(ble_device != null)
        {
            ble_device.write(DFU_SERVICE_UUID, DFU_CONTROL_POINT_UUID, OP_CODE_VALIDATE, event -> {
                if(event.wasSuccess())
                {
                    Log.e(TAG, "writeOpCodeValidateFirmwareImage SUCCESS : Tx'ed " + Utils.byteArrayToHexString(event.data()));

                    // Bootloader will send a Notification with the result
                }
                else
                {
                    Log.e(TAG, "writeOpCodeValidateFirmwareImage FAILED : " + event.status().toString());
                }
            });
        }
        else
        {
            Log.e(TAG, "writeOpCodeValidateFirmwareImage : COULD NOT WRITE : ble_device == null");
        }
    }


    private void writeOpCodeActivateAndReset()
    {
        if(ble_device != null)
        {
            ble_device.write(DFU_SERVICE_UUID, DFU_CONTROL_POINT_UUID, OP_CODE_ACTIVATE_AND_RESET, event -> {
                if(event.wasSuccess())
                {
                    Log.e(TAG, "writeOpCodeActivateAndReset SUCCESS : Tx'ed " + Utils.byteArrayToHexString(event.data()));

                    // Chip will have reset now
                }
                else
                {
                    Log.e(TAG, "writeOpCodeActivateAndReset FAILED : " + event.status().toString());
                }

                sendDfuUploadCompleteBroadcast();
            });
        }
        else
        {
            Log.e(TAG, "writeOpCodeActivateAndReset : COULD NOT WRITE : ble_device == null");
        }
    }

    private void writeOpCodeReset()
    {
        if(ble_device != null)
        {
            ble_device.write(DFU_SERVICE_UUID, DFU_CONTROL_POINT_UUID, OP_CODE_RESET, event -> {
                if(event.wasSuccess())
                {
                    Log.e(TAG, "writeOpCodeReset SUCCESS : Tx'ed " + Utils.byteArrayToHexString(event.data()));

                    dfu_state = DfuState.DFU_STATE__INIT;
                    dfuStateMachine();
                }
                else
                {
                    Log.e(TAG, "writeOpCodeReset FAILED : " + event.status().toString());
                }
            });
        }
        else
        {
            Log.e(TAG, "writeOpCodeReset : COULD NOT WRITE : ble_device == null");
        }
    }

    private byte[] intToLittleEndianByteArray(int value)
    {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(value);
        return b.array();
    }


    private void writeImageSize()
    {
        byte[] image_size = intToLittleEndianByteArray(firmware_binary_array.length);

        Log.d(TAG, "writeImageSize : image_size = " + Utils.byteArrayToHexString(image_size));

        if(ble_device != null)
        {
            ble_device.write(DFU_SERVICE_UUID, DFU_PACKET_UUID, image_size, event -> {
                if(event.wasSuccess())
                {
                    Log.e(TAG, "writeImageSize SUCCESS. Tx'ed " + Utils.byteArrayToHexString(event.data()));

                    // Bootloader will send a Notification with the response
                }
                else
                {
                    Log.e(TAG, "writeImageSize FAILED : " + event.status().toString());
                }
            });
        }
        else
        {
            Log.e(TAG, "writeImageSize : COULD NOT WRITE : ble_device == null");
        }
    }


    private final static int PACKET_SIZE = 16;
    private int localPos;

    private byte[] makeNextPayloadPacket()
    {
        final byte[] buffer = new byte[PACKET_SIZE];

        for (int i=0; i<buffer.length; i++)
        {
            buffer[i] = firmware_binary_array[localPos++];
        }

        return buffer;
    }



    private void writeOpCodeFirmwarePacket()
    {
        //ble_device.performOta(new MatTransaction());

        final byte[] buffer = makeNextPayloadPacket();

        if(ble_device != null)
        {
            ble_device.write(DFU_SERVICE_UUID, DFU_PACKET_UUID, buffer, event -> {
                //logEventStatus(event);

                if(event.wasSuccess())
                {
                    //Log.e(TAG, "writeOpCodeFirmwarePacket SUCCESS. Tx'ed " + Utils.byteArrayToHexString(event.data()));

                    number_of_bytes_sent += buffer.length;
                    number_of_packets_sent_since_notification++;

                    Log.e(TAG, "writeOpCodeFirmwarePacket successful : number_of_bytes_sent = " + number_of_bytes_sent + " : number_of_packets_sent_since_notification = " + number_of_packets_sent_since_notification);

                    // If a packet receipt notification is expected, or the last packet was sent, do nothing. There Notification listener will catch either
                    // a packet confirmation (if there are more bytes to send) or the image received notification (it upload process was completed)
                    final boolean notification_expected = (number_of_packets_to_send_before_notification > 0) && (number_of_packets_sent_since_notification == number_of_packets_to_send_before_notification);
                    final boolean last_packet_transferred = (number_of_bytes_sent == firmware_binary_array.length);

                    if (notification_expected)
                    {
                        Log.e(TAG, "notification_expected");
                    }

                    if (last_packet_transferred)
                    {
                        final long end_time = System.currentTimeMillis();
                        Log.e(TAG, "last_packet_transferred : Transfer of " + number_of_bytes_sent + " bytes has taken " + (end_time - start_time) + " ms");

                        dfu_state = DfuState.DFU_STATE__VALIDATE_FIRMWARE_IMAGE;
                        dfuStateMachine();
                    }

                    if (notification_expected || last_packet_transferred)
                    {
                        return;
                    }

                    writeOpCodeFirmwarePacket();

                    updateProgressNotification();
                }
                else
                {
                    Log.e(TAG, "writeOpCodeFirmwarePacket FAILED : " + event.status().toString());
                }
            });
        }
        else
        {
            Log.e(TAG, "writeOpCodeFirmwarePacket : COULD NOT WRITE : ble_device == null");
        }
    }


    public enum DfuState
    {
        DFU_STATE__INIT,
        DFU_STATE__WRITE_OP_CODE_START_DFU,
        DFU_STATE__WRITE_FIRMWARE_IMAGE_SIZE,
        DFU_STATE__WRITE_OP_CODE_RECEIVE_FIRMWARE_IMAGE,
        DFU_STATE__WRITE_FIRMWARE_PACKET,
        DFU_STATE__VALIDATE_FIRMWARE_IMAGE,
        DFU_STATE__WRITE_ACTIVATE_AND_RESET_OP_CODE,

        DFU_STATE__WRITE_OP_CODE_RESET,
    }

    private void initDfu()
    {
        start_time = System.currentTimeMillis();

        updateProgressNotification();

        localPos = 0;
        number_of_bytes_sent = 0;
        number_of_packets_sent_since_notification = 0;

        enableDfuControlPointNotification();
    }

    private void dfuStateMachine()
    {
        Log.e(TAG, "dfuStateMachine : Executing " + dfu_state);

        switch (dfu_state)
        {
            case DFU_STATE__INIT:
                initDfu();
                break;

            case DFU_STATE__WRITE_OP_CODE_START_DFU:
                writeOpCodeStartDfu();
                break;

            case DFU_STATE__WRITE_FIRMWARE_IMAGE_SIZE:
                writeImageSize();
                break;

            case DFU_STATE__WRITE_OP_CODE_RECEIVE_FIRMWARE_IMAGE:
                writeOpCodeReceiveFirmwareImage();
                break;

            case DFU_STATE__WRITE_FIRMWARE_PACKET:
                writeOpCodeFirmwarePacket();
                break;

            case DFU_STATE__VALIDATE_FIRMWARE_IMAGE:
                writeOpCodeValidateFirmwareImage();
                break;

            case DFU_STATE__WRITE_ACTIVATE_AND_RESET_OP_CODE:
                writeOpCodeActivateAndReset();
                break;

            // Should never be needed but included in case anything goes wrong
            case DFU_STATE__WRITE_OP_CODE_RESET:
                writeOpCodeReset();
                break;

        }
    }

    private static final int DFU_STATUS_SUCCESS = 1;

    private void enableDfuControlPointNotification()
    {
        if(ble_device != null)
        {
            ble_device.enableNotify(DFU_SERVICE_UUID, DFU_CONTROL_POINT_UUID, event -> {
                switch (event.type())
                {
                    // Sweetblue fires this ReadWrite event for when the Indication/Notification is enabled. This should be 02 00
                    case ENABLING_NOTIFICATION:
                    {
                        Log.e(TAG, "enableDfuControlPointNotification : ENABLING_NOTIFICATION success = " + event.wasSuccess());

                        dfu_state = DfuState.DFU_STATE__WRITE_OP_CODE_START_DFU;
                        dfuStateMachine();
                    }
                    break;

                    // Data from the Indication/Notification
                    case INDICATION:
                    case NOTIFICATION:
                    {
                        if(event.wasSuccess())
                        {
                            byte[] response = event.data();
                            Log.e(TAG, dfu_state + " : " + Utils.byteArrayToHexString(response));

                            switch (dfu_state)
                            {
                                case DFU_STATE__WRITE_FIRMWARE_IMAGE_SIZE:
                                {
                                    int status = response[2];

                                    if (status != DFU_STATUS_SUCCESS)
                                    {
                                        Log.e(TAG, "DFU_STATE__WRITE_FIRMWARE_IMAGE_SIZE failed : Status = " + status);
                                        //throw new RemoteDfuException("Starting DFU failed", status);
// Need to tell the Gateway we have reset the bootloader - so it can go searching for it faster
                                        dfu_state = DfuState.DFU_STATE__WRITE_OP_CODE_RESET;
                                    }
                                    else
                                    {
                                        dfu_state = DfuState.DFU_STATE__WRITE_OP_CODE_RECEIVE_FIRMWARE_IMAGE;
                                    }

                                    dfuStateMachine();
                                }
                                break;

                                case DFU_STATE__VALIDATE_FIRMWARE_IMAGE:
                                {
                                    int status = response[2];
                                    //sendLogBroadcast("Response received (Op Code: " + response[1] + " Status: " + status + ")");
                                    if (status != DFU_STATUS_SUCCESS)
                                    {
                                        Log.e(TAG, "DFU_STATE__READ_VALIDATION_RESULT failed");
                                        //throw new RemoteDfuException("Starting DFU failed", status);
// Something gone very wrong
// Need to tell the Gateway we have reset the bootloader - so it can go searching for it faster
                                        dfu_state = DfuState.DFU_STATE__WRITE_OP_CODE_RESET;
                                    }
                                    else
                                    {
                                        dfu_state = DfuState.DFU_STATE__WRITE_ACTIVATE_AND_RESET_OP_CODE;
                                    }

                                    dfuStateMachine();
                                }
                                break;
                            }
                        }
                        else
                        {
                            Log.e(TAG, event.type() + " read FAILED");
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
        else
        {
            Log.e(TAG, "enableDfuControlPointNotification : COULD NOT WRITE : ble_device == null");
        }
    }


    private DfuState dfu_state;

    private byte[] firmware_binary_array;


    private int number_of_bytes_sent;
    private int number_of_packets_sent_since_notification;
    //private int number_of_packets_to_send_before_notification = 10;
    private final int number_of_packets_to_send_before_notification = 0;

    private long start_time;

    public void startFirmwareUpdate(byte[] desired_firmware_binary_array)
    {
        firmware_binary_array = desired_firmware_binary_array;

        dfu_state = DfuState.DFU_STATE__INIT;
        dfuStateMachine();
    }



    private static final int OP_CODE_RECEIVE_START_DFU_KEY = 0x01;
    private static final int OP_CODE_RECEIVE_FIRMWARE_IMAGE_KEY = 0x03;
    private static final int OP_CODE_RECEIVE_VALIDATE_KEY = 0x04;
    private static final int OP_CODE_RECEIVE_ACTIVATE_AND_RESET_KEY = 0x05;
    private static final int OP_CODE_RECEIVE_RESET_KEY = 0x06;
    // private static final int OP_CODE_PACKET_REPORT_RECEIVED_IMAGE_SIZE_KEY = 0x07;
    private static final int OP_CODE_PACKET_RECEIPT_NOTIFY_REQ_KEY = 0x08;
    //private static final int OP_CODE_RESPONSE_CODE_KEY = 0x10; // 16
    private static final int OP_CODE_PACKET_RECEIPT_NOTIFY_KEY = 0x11; // 11

    private static final byte[] OP_CODE_START_DFU = new byte[] { OP_CODE_RECEIVE_START_DFU_KEY };
    private static final byte[] OP_CODE_RECEIVE_FIRMWARE_IMAGE = new byte[] { OP_CODE_RECEIVE_FIRMWARE_IMAGE_KEY };
    private static final byte[] OP_CODE_VALIDATE = new byte[] { OP_CODE_RECEIVE_VALIDATE_KEY };
    private static final byte[] OP_CODE_ACTIVATE_AND_RESET = new byte[] { OP_CODE_RECEIVE_ACTIVATE_AND_RESET_KEY };
    private static final byte[] OP_CODE_RESET = new byte[] { OP_CODE_RECEIVE_RESET_KEY };
    // private static final byte[] OP_CODE_REPORT_RECEIVED_IMAGE_SIZE = new byte[] { OP_CODE_PACKET_REPORT_RECEIVED_IMAGE_SIZE_KEY };
    private static final byte[] OP_CODE_PACKET_RECEIPT_NOTIFY_REQ = new byte[] {OP_CODE_PACKET_RECEIPT_NOTIFY_REQ_KEY, 0x00, 0x00 };

    private int last_progress = -1;


    /**
     * Creates or updates the notification in the Notification Manager. Sends broadcast with current progress to the activity.
     */
    private void updateProgressNotification()
    {
        final int progress = (int) Math.ceil(100.0f * number_of_bytes_sent / firmware_binary_array.length);

        if (last_progress == progress)
        {
            return;
        }

        last_progress = progress;

//        if (progress < ERROR_MASK)
//        {
            // Progress is in %
            Log.e(TAG, "Progress = " + progress + "%");
            sendProgressBroadcast(progress);
//        }
//        else
//        {
//            Log.e(TAG, "Progress = Error Code = " + progress);
//            sendErrorBroadcast(progress & ~ERROR_CONNECTION_MASK);
//        }
    }


    public static final String EXTRA_DATA = "no.nordicsemi.android.nrftoolbox.dfu.EXTRA_DATA";
    public static final String DEVICE_TYPE = "DEVICE_TYPE";


    public static final String BROADCAST_ERROR = "no.nordicsemi.android.nrftoolbox.dfu.BROADCAST_ERROR";
    private static final int ERROR_MASK = 0x0100;
    private static final int ERROR_DEVICE_DISCONNECTED = ERROR_MASK;
    private static final int ERROR_SERVICE_DISCOVERY_NOT_STARTED = ERROR_MASK | 0x05;
    private static final int ERROR_UNSUPPORTED_DEVICE = ERROR_MASK | 0x06;

    /** Look for DFU specification to get error codes */
    private final int ERROR_REMOTE_MASK = 0x0200;
    private final int ERROR_CONNECTION_MASK = 0x0400;


    public static final String BROADCAST_PROGRESS = "no.nordicsemi.android.nrftoolbox.dfu.BROADCAST_PROGRESS";
    public static final String BROADCAST_DFU_UPLOAD_COMPLETE = "no.nordicsemi.android.nrftoolbox.dfu.BROADCAST_DFU_UPLOAD_COMPLETE";


    private void sendProgressBroadcast(final int progress)
    {
        Intent intent = new Intent(BROADCAST_PROGRESS);
        intent.putExtra(EXTRA_DATA, progress);
        sendIntentWithDeviceType(intent);
    }


    private void sendDfuUploadCompleteBroadcast()
    {
        Intent intent = new Intent(BROADCAST_DFU_UPLOAD_COMPLETE);
        sendIntentWithDeviceType(intent);
    }


    private void sendErrorBroadcast(final int error)
    {
        Intent intent = new Intent(BROADCAST_ERROR);
        intent.putExtra(EXTRA_DATA, error & ~ERROR_CONNECTION_MASK);
        sendIntentWithDeviceType(intent);
    }


    private void sendIntentWithDeviceType(Intent intent)
    {
        intent.putExtra(DEVICE_TYPE, device_type.ordinal());
        LocalBroadcastManager.getInstance(gateway_context_interface.getAppContext()).sendBroadcast(intent);
    }
}
