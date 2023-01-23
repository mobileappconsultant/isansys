package com.isansys.pse_isansysportal;

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

public class FragmentCheckDeviceStatus extends FragmentIsansys implements IScanResultHandler
{
    private final String TAG =  FragmentCheckDeviceStatus.class.getName();

	private BarcodeFragment barcodeFragment;
    
	private TextView textViewCheckDeviceStatusDeviceTypeLabel;
    private TextView textViewDeviceType;
    
    private TextView textViewCheckDeviceStatusDeviceHumanReadableSerialNumberLabel;
	private TextView textViewDeviceDeviceHumanReadableSerialNumber;
	
	private TextView textViewDeviceWardNameLabel;
	private TextView textViewDeviceWardName;
	
	private TextView textViewDeviceBedNameLabel;
	private TextView textViewDeviceBedName;

	private TextView textViewDeviceUseStatus;

	private TextView textViewCheckDeviceStatusCheckingServer;
	
	private TextView textViewDeviceMacAddress;
	private TextView textViewDeviceMacAddressLabel;
    

    @Override
    public void scanResult(ScanResult result) 
    {
        main_activity_interface.beep();

        String barcode_code_contents = result.getRawResult().getText();

        BarcodeFormat format = result.getRawResult().getBarcodeFormat();

        hideBedAndWardDetails();

        switch (format)
        {
            case QR_CODE:
            {
                main_activity_interface.onQrCodeDetected("FragmentCheckDeviceStatus", barcode_code_contents);
            }
            break;

            case DATA_MATRIX:
            {
                main_activity_interface.onDataMatrixDetected("FragmentCheckDeviceStatus", barcode_code_contents);
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
        View v = inflater.inflate(R.layout.check_device_status, container, false); // Inflate the layout for this fragment

        textViewCheckDeviceStatusDeviceTypeLabel = v.findViewById(R.id.textViewCheckDeviceStatusDeviceTypeLabel);
        textViewCheckDeviceStatusDeviceTypeLabel.setVisibility(View.INVISIBLE);
        textViewDeviceType = v.findViewById(R.id.textViewChangeDeviceStatusDeviceType);
        textViewDeviceType.setVisibility(View.INVISIBLE);
        
        textViewCheckDeviceStatusDeviceHumanReadableSerialNumberLabel = v.findViewById(R.id.textViewCheckDeviceStatusDeviceHumanReadableSerialNumberLabel);
        textViewCheckDeviceStatusDeviceHumanReadableSerialNumberLabel.setVisibility(View.INVISIBLE);
        textViewDeviceDeviceHumanReadableSerialNumber = v.findViewById(R.id.textViewDeviceHumanReadableSerialNumber);
        textViewDeviceDeviceHumanReadableSerialNumber.setVisibility(View.INVISIBLE);
        
        textViewDeviceWardNameLabel = v.findViewById(R.id.textViewDeviceWardNameLabel);
        textViewDeviceWardNameLabel.setVisibility(View.INVISIBLE);
        textViewDeviceWardName = v.findViewById(R.id.textViewCheckDeviceStatusDeviceWardName);
        textViewDeviceWardName.setVisibility(View.INVISIBLE);
        
        textViewDeviceBedNameLabel = v.findViewById(R.id.textViewDeviceBedNameLabel);
        textViewDeviceBedNameLabel.setVisibility(View.INVISIBLE);
        textViewDeviceBedName = v.findViewById(R.id.textViewCheckDeviceStatusBedName);
        textViewDeviceBedName.setVisibility(View.INVISIBLE);

        textViewDeviceUseStatus = v.findViewById(R.id.textViewDeviceUseStatus);
        textViewDeviceUseStatus.setVisibility(View.INVISIBLE);

        textViewCheckDeviceStatusCheckingServer = v.findViewById(R.id.textViewCheckDeviceStatusCheckingServer);
        textViewCheckDeviceStatusCheckingServer.setVisibility(View.INVISIBLE);
        
        textViewDeviceMacAddress = v.findViewById(R.id.textViewDeviceMacAddressNumber);
        textViewDeviceMacAddressLabel = v.findViewById(R.id.textViewDeviceMacAddressLabel);
        textViewDeviceMacAddress.setVisibility(View.INVISIBLE);
    	textViewDeviceMacAddressLabel.setVisibility(View.INVISIBLE);
                        
        return v;
    }


    @Override
    public void onResume()
    {
        barcodeFragment = new BarcodeFragment();
        barcodeFragment.setFrontCamera(main_activity_interface.useFrontCamera());
        barcodeFragment.setScanResultHandler(this);

        replaceFragmentIfSafe(R.id.check_device_status_qr_bar_code, barcodeFragment);

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
            Log.e(TAG,"onPause : FragmentCheckDeviceStatus getActivity() is null");
        }
        super.onPause();
    }


    public void setHumanReadableNumberDeviceId(long human_readable_device_id)
    {
        textViewCheckDeviceStatusDeviceHumanReadableSerialNumberLabel.setVisibility(View.VISIBLE);
        
        textViewDeviceDeviceHumanReadableSerialNumber.setText(String.valueOf(human_readable_device_id));
        textViewDeviceDeviceHumanReadableSerialNumber.setVisibility(View.VISIBLE);
    }
    
    
    public void setDeviceMacAddress(String device_mac_address)
    {
        if(main_activity_interface.getShowMacAddressOnStatus())
        {
        	textViewDeviceMacAddress.setVisibility(View.VISIBLE);
        	textViewDeviceMacAddressLabel.setVisibility(View.VISIBLE);
        	
        	textViewDeviceMacAddress.setText(device_mac_address);
        }
    }


    public void setDeviceType(DeviceType device_type)
    {
        textViewCheckDeviceStatusDeviceTypeLabel.setVisibility(View.VISIBLE);

        textViewDeviceType.setText(main_activity_interface.getDeviceNameByType(device_type));
        textViewDeviceType.setVisibility(View.VISIBLE);
    }


    public void showDeviceInUse(String wardName, String bedName)
    {
        textViewDeviceUseStatus.setVisibility(View.VISIBLE);
        textViewDeviceUseStatus.setText(getResources().getString(R.string.device_is_in_use));

        setWardName(wardName);

        setBedName(bedName);
    }


    public void showDeviceNotInUse()
    {
        textViewDeviceUseStatus.setVisibility(View.VISIBLE);
        textViewDeviceUseStatus.setText(getResources().getString(R.string.device_not_in_use));

        hideBedAndWardDetails();
    }

    
    private void setWardName(String value)
    {
        textViewDeviceWardNameLabel.setVisibility(View.VISIBLE);
        
        textViewDeviceWardName.setText(value);
        textViewDeviceWardName.setVisibility(View.VISIBLE);
    }

    
    private void setBedName(String value)
    {
        textViewDeviceBedNameLabel.setVisibility(View.VISIBLE);
        
        textViewDeviceBedName.setText(value);
        textViewDeviceBedName.setVisibility(View.VISIBLE);
    }

    
    private void hideBedAndWardDetails()
    {
        textViewDeviceWardNameLabel.setVisibility(View.INVISIBLE);
        textViewDeviceWardName.setVisibility(View.INVISIBLE);
        
        textViewDeviceBedNameLabel.setVisibility(View.INVISIBLE);
        textViewDeviceBedName.setVisibility(View.INVISIBLE);
    }
    
    
    public void serverRequestFailed()
    {
        hideBedAndWardDetails();
        
        textViewCheckDeviceStatusCheckingServer.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
        
        textViewCheckDeviceStatusCheckingServer.setText(getResources().getString(R.string.problem_checking_server__please_try_again_later));
        textViewCheckDeviceStatusCheckingServer.setVisibility(View.VISIBLE);
    }
    
    
    public void showCheckingWithServer(boolean show)
    {
        if (show)
        {
            textViewCheckDeviceStatusCheckingServer.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

            textViewCheckDeviceStatusCheckingServer.setText(getResources().getString(R.string.checking_with_server));
            textViewCheckDeviceStatusCheckingServer.setVisibility(View.VISIBLE);
        }
        else
        {
            textViewCheckDeviceStatusCheckingServer.setVisibility(View.INVISIBLE);
        }
    }
        
}
