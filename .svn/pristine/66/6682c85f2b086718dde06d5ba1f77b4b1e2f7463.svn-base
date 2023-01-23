package com.isansys.pse_isansysportal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.google.zxing.BarcodeFormat;
import com.isansys.common.enums.DeviceType;

public class FragmentCheckPackaging extends FragmentIsansys implements IScanResultHandler
{
    private final String TAG =  FragmentCheckPackaging.class.getName();

	private BarcodeFragment barcodeFragment;

	private TextView textViewScanBarcode;

    private TextView textViewSensorBarcodeDeviceType;
	private TextView textViewSensorBarcodeHumanReadableSerialNumber;
	private TextView textViewSensorBarcodeMacAddress;

    private TextView textViewPackagingBarcodeDeviceType;
    private TextView textViewPackagingBarcodeHumanReadableSerialNumber;
    private TextView textViewPackagingBarcodeMacAddress;

    private TextView textViewBarcodesMatch;

    private enum BarcodeTypeBeingScanned
    {
        SENSOR,
        PACKAGING
    }

    private BarcodeTypeBeingScanned barcodeTypeBeingScanned;

    @Override
    public void scanResult(ScanResult result) 
    {
        main_activity_interface.beep();

        String barcode_code_contents = result.getRawResult().getText();

        BarcodeFormat format = result.getRawResult().getBarcodeFormat();

        switch (format)
        {
            case QR_CODE:
            {
                main_activity_interface.onQrCodeDetected("FragmentCheckPackaging", barcode_code_contents);
            }
            break;

            case DATA_MATRIX:
            {
                main_activity_interface.onDataMatrixDetected("FragmentCheckPackaging", barcode_code_contents);
            }
            break;

            default:
                break;
        }

        barcodeFragment.restart();
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.check_packaging, container, false); // Inflate the layout for this fragment

        textViewScanBarcode = v.findViewById(R.id.textViewScanBarcode);

        textViewSensorBarcodeDeviceType = v.findViewById(R.id.textViewSensorBarcodeDeviceType);
        textViewSensorBarcodeHumanReadableSerialNumber = v.findViewById(R.id.textViewSensorBarcodeHumanReadableSerialNumber);
        textViewSensorBarcodeMacAddress = v.findViewById(R.id.textViewSensorBarcodeMacAddress);

        textViewPackagingBarcodeDeviceType = v.findViewById(R.id.textViewPackagingBarcodeDeviceType);
        textViewPackagingBarcodeHumanReadableSerialNumber = v.findViewById(R.id.textViewPackagingBarcodeHumanReadableSerialNumber);
        textViewPackagingBarcodeMacAddress = v.findViewById(R.id.textViewPackagingBarcodeMacAddress);

        textViewBarcodesMatch = v.findViewById(R.id.textViewBarcodesMatch);
        textViewBarcodesMatch.setVisibility(View.INVISIBLE);

        return v;
    }


    @Override
    public void onResume()
    {
        barcodeTypeBeingScanned = BarcodeTypeBeingScanned.SENSOR;

        barcodeFragment = new BarcodeFragment();
        barcodeFragment.setFrontCamera(main_activity_interface.useFrontCamera());
        barcodeFragment.setScanResultHandler(this);

        replaceFragmentIfSafe(R.id.check_packaging_qr_bar_code, barcodeFragment);

        super.onResume();
    }


    @Override
    public void onPause()
    {
        if(getActivity() != null)
        {
            (getChildFragmentManager()).beginTransaction().remove(barcodeFragment).commit();
        }
        else
        {
            Log.e(TAG,"onPause : getActivity() is null");
        }
        super.onPause();
    }


    public void setHumanReadableNumberDeviceId(long human_readable_device_id)
    {
        if (barcodeTypeBeingScanned == BarcodeTypeBeingScanned.SENSOR)
        {
            textViewSensorBarcodeHumanReadableSerialNumber.setText(String.valueOf(human_readable_device_id));
        }
        else
        {
            textViewPackagingBarcodeHumanReadableSerialNumber.setText(String.valueOf(human_readable_device_id));
        }
    }
    
    
    public void setDeviceMacAddress(String device_mac_address)
    {
        if (barcodeTypeBeingScanned == BarcodeTypeBeingScanned.SENSOR)
        {
            textViewSensorBarcodeMacAddress.setText(device_mac_address);
        }
        else
        {
            textViewPackagingBarcodeMacAddress.setText(device_mac_address);
        }
    }


    public void setDeviceType(DeviceType device_type)
    {
        String deviceName;

        switch(device_type)
        {
            case DEVICE_TYPE__LIFETOUCH:
            case DEVICE_TYPE__LIFETOUCH_BLUE_V2:
            {
                deviceName = "Lifetouch";
            }
            break;

            case DEVICE_TYPE__LIFETOUCH_THREE:
            {
                deviceName = "Lifetouch Three";
            }
            break;

            case DEVICE_TYPE__LIFETEMP_V2:
            {
                deviceName = "Lifetemp v2";
            }
            break;

            case DEVICE_TYPE__NONIN_WRIST_OX:
            case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
            {
                deviceName = getResources().getString(R.string.textNoninWristOxSingleLine);
            }
            break;

            case DEVICE_TYPE__MEDLINKET:
            {
                deviceName = "Medlinket";
            }
            break;

            case DEVICE_TYPE__FORA_IR20:
            {
                deviceName = "Fora";
            }
            break;

            case DEVICE_TYPE__AND_UA767:
            {
                deviceName = "A&D UA767";
            }
            break;
            
            case DEVICE_TYPE__AND_UA651:
            {
                deviceName = "A&D UA651";
            }
            break;
            
            case DEVICE_TYPE__AND_UA656BLE:
            {
                deviceName = "A&D UA656";
            }
            break;
            
            case DEVICE_TYPE__AND_ABPM_TM2441:
            {
                deviceName = "A&D TM2441";
            }
            break;
            
            case DEVICE_TYPE__AND_UA1200BLE:
            {
                deviceName = "A&D UA1200BLE";
            }
            break;
            
            case DEVICE_TYPE__AND_UC352BLE:
            {
                deviceName = "A&D UC352";
            }
            break;

            default:
            {
                deviceName = "UNKNOWN";
            }
            break;
        }

        if (barcodeTypeBeingScanned == BarcodeTypeBeingScanned.SENSOR)
        {
            textViewSensorBarcodeDeviceType.setText(deviceName);
        }
        else
        {
            textViewPackagingBarcodeDeviceType.setText(deviceName);
        }
    }


    private void ShowIfBarcodesMatch()
    {
        Context context = getContext();
        if (context != null)
        {
            boolean barcodesMatch = true;

            barcodesMatch &= (textViewSensorBarcodeDeviceType.getText().equals(textViewPackagingBarcodeDeviceType.getText()));
            barcodesMatch &= (textViewSensorBarcodeHumanReadableSerialNumber.getText().equals(textViewPackagingBarcodeHumanReadableSerialNumber.getText()));
            barcodesMatch &= (textViewSensorBarcodeMacAddress.getText().equals(textViewPackagingBarcodeMacAddress.getText()));

            if (barcodesMatch)
            {
                textViewBarcodesMatch.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                textViewBarcodesMatch.setText(R.string.barcodes_match);
            }
            else
            {
                textViewBarcodesMatch.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                textViewBarcodesMatch.setText(R.string.barcodes_do_not_match);
            }

            textViewBarcodesMatch.setVisibility(View.VISIBLE);
        }
    }


    private void ResetForNewComparison()
    {
        // Clear Packaging screen controls
        textViewPackagingBarcodeDeviceType.setText("");
        textViewPackagingBarcodeHumanReadableSerialNumber.setText("");
        textViewPackagingBarcodeMacAddress.setText("");

        textViewBarcodesMatch.setVisibility(View.INVISIBLE);
    }


    public void setupForNextBarcodeScan()
    {
        if (barcodeTypeBeingScanned == BarcodeTypeBeingScanned.SENSOR)
        {
            ResetForNewComparison();

            textViewScanBarcode.setText(R.string.scan_packaging_barcode);
            barcodeTypeBeingScanned = BarcodeTypeBeingScanned.PACKAGING;
        }
        else
        {
            // barcodeTypeBeingScanned is currently BarcodeTypeBeingScanned.PACKAGING.
            // Therefore both Sensor and Packaging barcodes scanned

            ShowIfBarcodesMatch();

            textViewScanBarcode.setText(R.string.scan_sensor_barcode);
            barcodeTypeBeingScanned = BarcodeTypeBeingScanned.SENSOR;
        }
    }

}
