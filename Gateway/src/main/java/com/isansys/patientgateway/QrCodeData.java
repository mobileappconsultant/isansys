package com.isansys.patientgateway;

import com.isansys.common.enums.BarcodeDeviceType;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.SensorType;
import com.isansys.patientgateway.remotelogging.RemoteLogging;


/**
 * QrCodeData corresponds to a QR code, DataMatrix or NFC tag containing device or unlock information
 */
public class QrCodeData
{
    private final boolean valid;

    BarcodeDeviceType barcode_type = null;
    String bluetooth_device_address = null;
    long human_readable_product_id = -1;
    DeviceType device_type = DeviceType.DEVICE_TYPE__INVALID;
    SensorType sensor_type = SensorType.SENSOR_TYPE__INVALID;
    boolean emulated = false;

    private final String TAG = this.getClass().getSimpleName();


    /**
     * For use with WAMP commands to emulate a QR unlock request
     *
     * @param unlock_type
     * @param user_id
     */
    public QrCodeData(BarcodeDeviceType unlock_type, long user_id)
    {
        barcode_type = unlock_type;
        bluetooth_device_address = "";
        human_readable_product_id = user_id;

        this.device_type = DeviceType.DEVICE_TYPE__INVALID;
        this.sensor_type = SensorType.SENSOR_TYPE__INVALID;

        valid = true;

        emulated = true;
    }


    /**
     * For use with decoded data from a QR code or NFC tag
     *
     * @param plaintext The raw bytes decoded from the QR
     * @param Log RemoteLogging instance so we can log the results
     */
    public QrCodeData(byte[] plaintext, RemoteLogging Log)
    {
        if (plaintext != null)
        {
            Log.d("Hex Decryption", Utils.byteArrayToHexString(plaintext));

            if ((plaintext[0] == 'I') && (plaintext[1] == 'S') && (plaintext[2] == 'A'))
            {
                // Then its a valid Isansys product QR code. Start processing from index 3 onwards
                int i;
                int j = 3;

                // Bytes 3, 4 and 5 (and maybe 13, 14 and 15 if not LEA) are the Human Readable Product ID. E.g. Lifetouch 234
                human_readable_product_id = 0;
                for(i = 0; i<3; i++)
                {
                    int next_byte = (int)plaintext[j++] & 0xFF;
                    human_readable_product_id *= 256;
                    human_readable_product_id += next_byte;
                }

                // Bytes 6, 7, 8, 9, 10 and 11 are the Bluetooth Address
                StringBuilder bluetooth_device_address_string_builder = new StringBuilder();
                for (i=0; i<6; i++)
                {
                    String value_as_hex_string = String.format("%02X:", plaintext[j++]);
                    bluetooth_device_address_string_builder.append(value_as_hex_string);
                }
                // Remove the trailing ":"
                bluetooth_device_address_string_builder.deleteCharAt(bluetooth_device_address_string_builder.length() - 1);

                bluetooth_device_address = bluetooth_device_address_string_builder.toString();
                Log.d(TAG, "Bluetooth Device Address = " + bluetooth_device_address);

                // Byte 12 is the Device Type
                int barcode_type_as_int = plaintext[j++];
                barcode_type = BarcodeDeviceType.values()[barcode_type_as_int];

                // If the Human Readable Device ID is larger than 3 bytes, then the remaining 3 bytes are used to hold the rest of the Human Readable Device ID
                // If it can fit in 3 bytes, then the last 3 bytes are spare, and will contain "LEA" as test data.
                if ((plaintext[13] != 'L') || (plaintext[14] != 'E') || (plaintext[15] != 'A'))
                {
                    int high_part_of_human_readable_product_id = 0;

                    for(i = 0; i<3; i++)
                    {
                        int next_byte = (int)plaintext[j++] & 0xFF;
                        high_part_of_human_readable_product_id *= 256;
                        high_part_of_human_readable_product_id += next_byte;
                    }

                    high_part_of_human_readable_product_id = high_part_of_human_readable_product_id  * 0x1000000;

                    human_readable_product_id += high_part_of_human_readable_product_id;
                }
                else
                {
                    // Bytes 13, 14 and 15 are currently spare
                    String spare_bytes_string_builder = String.valueOf((char) plaintext[13]) +
                            (char) plaintext[14] +
                            (char) plaintext[15];

                    Log.d(TAG, "Spare Bytes = " + spare_bytes_string_builder);
                }

                Log.d(TAG, "Human Readable Product ID = " + human_readable_product_id);


                switch (barcode_type)
                {
                    case BARCODE_TYPE__LIFETOUCH:
                    {
                        device_type = DeviceType.DEVICE_TYPE__LIFETOUCH;
                        sensor_type = SensorType.SENSOR_TYPE__LIFETOUCH;
                    }
                    break;

                    case BARCODE_TYPE__LIFETEMP_V2:
                    {
                        device_type = DeviceType.DEVICE_TYPE__LIFETEMP_V2;
                        sensor_type = SensorType.SENSOR_TYPE__TEMPERATURE;
                    }
                    break;

                    case BARCODE_TYPE__NONIN_WRIST_OX:
                    {
                        device_type = DeviceType.DEVICE_TYPE__NONIN_WRIST_OX;
                        sensor_type = SensorType.SENSOR_TYPE__SPO2;
                    }
                    break;

                    case BARCODE_TYPE__AND_UA767:
                    {
                        device_type = DeviceType.DEVICE_TYPE__AND_UA767;
                        sensor_type = SensorType.SENSOR_TYPE__BLOOD_PRESSURE;
                    }
                    break;

                    case BARCODE_TYPE__AND_UC352BLE:
                    {
                        device_type = DeviceType.DEVICE_TYPE__AND_UC352BLE;
                        sensor_type = SensorType.SENSOR_TYPE__WEIGHT_SCALE;
                    }
                    break;

                    case BARCODE_TYPE__NONIN_WRIST_OX_BTLE:
                    {
                        device_type = DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE;
                        sensor_type = SensorType.SENSOR_TYPE__SPO2;
                    }
                    break;

                    case BARCODE_TYPE__NONIN_3230:
                    {
                        device_type = DeviceType.DEVICE_TYPE__NONIN_3230;
                        sensor_type = SensorType.SENSOR_TYPE__SPO2;
                    }
                    break;

                    case BARCODE_TYPE__LIFETOUCH_BLUE_V2:
                    {
                        device_type = DeviceType.DEVICE_TYPE__LIFETOUCH_BLUE_V2;
                        sensor_type = SensorType.SENSOR_TYPE__LIFETOUCH;
                    }
                    break;

                    case BARCODE_TYPE__AND_TM2441:
                    {
                        device_type = DeviceType.DEVICE_TYPE__AND_ABPM_TM2441;
                        sensor_type = SensorType.SENSOR_TYPE__BLOOD_PRESSURE;
                    }
                    break;

                    case BARCODE_TYPE__AND_UA651:
                    {
                        device_type = DeviceType.DEVICE_TYPE__AND_UA651;
                        sensor_type = SensorType.SENSOR_TYPE__BLOOD_PRESSURE;
                    }
                    break;

                    case BARCODE_TYPE__FORA_IR20:
                    {
                        device_type = DeviceType.DEVICE_TYPE__FORA_IR20;
                        sensor_type = SensorType.SENSOR_TYPE__TEMPERATURE;
                    }
                    break;

                    case BARCODE_TYPE__INSTAPATCH:
                    {
                        device_type = DeviceType.DEVICE_TYPE__INSTAPATCH;
                        sensor_type = SensorType.SENSOR_TYPE__LIFETOUCH;
                    }
                    break;

                    case BARCODE_TYPE__LIFETOUCH_THREE:
                    {
                        device_type = DeviceType.DEVICE_TYPE__LIFETOUCH_THREE;
                        sensor_type = SensorType.SENSOR_TYPE__LIFETOUCH;
                    }
                    break;

                    case BARCODE_TYPE__MEDLINKET:
                    {
                        device_type = DeviceType.DEVICE_TYPE__MEDLINKET;
                        sensor_type = SensorType.SENSOR_TYPE__SPO2;
                    }
                    break;

                    case BARCODE_TYPE__AND_UA1200BLE:
                    {
                        device_type = DeviceType.DEVICE_TYPE__AND_UA1200BLE;
                        sensor_type = SensorType.SENSOR_TYPE__BLOOD_PRESSURE;
                    }
                    break;

                    case BARCODE_TYPE__AND_UA656BLE:
                    {
                        device_type = DeviceType.DEVICE_TYPE__AND_UA656BLE;
                        sensor_type = SensorType.SENSOR_TYPE__BLOOD_PRESSURE;
                    }
                    break;
                }

                valid = true;
            }
            else
            {
                Log.d(TAG, "Not an Isansys QR code - or an Installation Domain Name");
                valid = false;
            }
        }
        else
        {
            Log.d(TAG, "Non Isansys QR code");
            valid = false;
        }
    }


    public boolean isValid()
    {
        return valid;
    }


    public BarcodeDeviceType getBarcodeType()
    {
        return barcode_type;
    }


    public String getBluetoothDeviceAddress()
    {
        return bluetooth_device_address;
    }


    public long getHumanReadableId()
    {
        return human_readable_product_id;
    }


    public DeviceType getDeviceType()
    {
        return device_type;
    }


    public SensorType getSensorType()
    {
        return sensor_type;
    }
}
