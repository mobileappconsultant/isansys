package com.isansys.pse_isansysportal;

import static com.google.zxing.BarcodeFormat.QR_CODE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.google.zxing.BarcodeFormat;

public class FragmentInstallationServerAddressScan extends FragmentIsansys implements IScanResultHandler
{
    private BarcodeFragment barcodeFragment;
    private final String TAG = this.getClass().getSimpleName();
    
    @Override
    public void scanResult(ScanResult result) 
    {
        main_activity_interface.beep();

    	String barcode_code_contents = result.getRawResult().getText();

        BarcodeFormat format = result.getRawResult().getBarcodeFormat();

        // Send data to Activity
        if (format == QR_CODE)
        {
            main_activity_interface.onQrCodeDetected("FragmentInstallationServerAddressScan", barcode_code_contents);
        }

        barcodeFragment.restart();
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.qr_code_server_address_entry, container, false);
    }
    

    @Override
    public void onResume()
    {
        barcodeFragment = new BarcodeFragment();
        barcodeFragment.setFrontCamera(main_activity_interface.useFrontCamera());
        barcodeFragment.setScanResultHandler(this);

        if(getActivity() != null)
        {
            getChildFragmentManager().beginTransaction().add(R.id.qr_bar_code, barcodeFragment).commit();
        }
        else
        {
            Log.e(TAG, "onResume : FragmentInstallationServerAddressScan getActivity() is null");
        }

        super.onResume();
    }


    @Override
    public void onPause()
    {
        if(getActivity() != null)
        {
            getChildFragmentManager().beginTransaction().remove(barcodeFragment).commit();
        }
        else
        {
            Log.e(TAG, "onPause : FragmentInstallationServerAddressScan getActivity() is null");
        }

        super.onPause();
    }
}
