package com.isansys.pse_isansysportal;

import static com.isansys.common.DeviceInfoConstants.INVALID_HUMAN_READABLE_DEVICE_ID;
import static com.isansys.common.DeviceInfoConstants.NO_LOT_NUMBER;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.google.zxing.BarcodeFormat;
import com.isansys.common.enums.SensorType;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class FragmentAddDevices extends FragmentIsansys implements IScanResultHandler, OnClickListener
{
    private final String TAG = this.getClass().getSimpleName();
	
    private BarcodeFragment barcodeFragment;

    private static class DeviceControls
    {
        TextView textViewHumanReadableSerialNumber;
        Button buttonRemove;
        View viewGoodToUse;
        TableRow tableRowLotNumber;
        TextView textViewLotNumber;
        TableRow tableRowManufactureDate;
        TextView textViewManufactureDate;
        TableRow tableRowExpirationDate;
        TextView textViewExpirationDate;
    }


    private DeviceControls device_controls_lifetouch;
    private DeviceControls device_controls_thermometer;
    private DeviceControls device_controls_pulse_ox;
    private DeviceControls device_controls_blood_pressure;
    private DeviceControls device_controls_scales;

    private Button buttonAddEarlyWarningScore;
    private Button buttonGrayedOutAddEarlyWarningScore;
    private Button buttonRemoveEarlyWarningScore;
    private Button buttonGrayedOutRemoveEarlyWarningScore;

    private TextView textViewAddDevicesCheckingServer;
    
    private TextView textViewAddDeviceFinishAdding;
    private ImageView imageViewAddDeviceDownArrow;

    private ProgressBar progressBarAddDevicesCheckDeviceStatusProgressBar;

    private TextView textViewAddDevicesDeviceWardNameLabel;
    private TextView textViewAddDevicesDeviceWardName;
    private TextView textViewAddDevicesDeviceBedNameLabel;
    private TextView textViewAddDevicesDeviceBedName;

    private LinearLayout linearLayoutNoCameraControls;
    private LinearLayout linearLayoutManualVitalsOnly;


    @Override
    public void scanResult(ScanResult result)
    {
        main_activity_interface.beep();

        String barcode_code_contents = result.getRawResult().getText();

        BarcodeFormat format = result.getRawResult().getBarcodeFormat();

        // Send data to Activity
        switch (format)
        {
            case QR_CODE:
            {
                main_activity_interface.onQrCodeDetected("FragmentAddDevices", barcode_code_contents);
            }
            break;

            case DATA_MATRIX:
            {
                main_activity_interface.onDataMatrixDetected("FragmentAddDevices", barcode_code_contents);
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
        View v = inflater.inflate(R.layout.add_devices, container, false); // Inflate the layout for this fragment

        linearLayoutNoCameraControls = v.findViewById(R.id.linearLayoutNoCameraControls);
        linearLayoutNoCameraControls.setVisibility(View.GONE);

        linearLayoutManualVitalsOnly = v.findViewById(R.id.linearLayoutManualVitalsOnly);
        if(main_activity_interface.includeManualVitalSignEntry())
        {
            linearLayoutManualVitalsOnly.setVisibility(View.VISIBLE);
        }
        else
        {
            linearLayoutManualVitalsOnly.setVisibility(View.GONE);
        }

        device_controls_lifetouch = new DeviceControls();
        device_controls_thermometer = new DeviceControls();
        device_controls_pulse_ox = new DeviceControls();
        device_controls_blood_pressure = new DeviceControls();
        device_controls_scales = new DeviceControls();

        Button buttonTestAddLifetouch = v.findViewById(R.id.buttonTestAddLifetouch);
        buttonTestAddLifetouch.setOnClickListener(x ->
        {
            // Barcode for Lifetouch B1728
            String barcode_code_contents = "hc`:t.W1L_=XB#M(_*m.";
            main_activity_interface.onQrCodeDetected("FragmentAddDevices", barcode_code_contents);
        });

        setupRemoveButton(device_controls_lifetouch, v, R.id.buttonRemoveLifetouch);
        setupRemoveButton(device_controls_thermometer, v, R.id.buttonRemoveThermometer);
        setupRemoveButton(device_controls_pulse_ox, v, R.id.buttonRemovePulseOximeter);
        setupRemoveButton(device_controls_blood_pressure, v, R.id.buttonRemoveBloodPressure);
        setupRemoveButton(device_controls_scales, v, R.id.buttonRemoveScales);

        textViewAddDeviceFinishAdding = v.findViewById(R.id.textAddDevicesPageHelp2);
        imageViewAddDeviceDownArrow = v.findViewById(R.id.imageAddDevice_DownArrow);

        device_controls_lifetouch.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewLifetouchHumanReadableSerialNumber);

        device_controls_lifetouch.tableRowLotNumber = v.findViewById(R.id.tableRowLifetouchLotNumber);
        device_controls_lifetouch.textViewLotNumber = v.findViewById(R.id.textViewLifetouchLotNumber);
        device_controls_lifetouch.tableRowManufactureDate = v.findViewById(R.id.tableRowLifetouchManufactureDate);
        device_controls_lifetouch.textViewManufactureDate = v.findViewById(R.id.textViewLifetouchManufactureDate);
        device_controls_lifetouch.tableRowExpirationDate = v.findViewById(R.id.tableRowLifetouchExpirationDate);
        device_controls_lifetouch.textViewExpirationDate = v.findViewById(R.id.textViewLifetouchExpirationDate);

        device_controls_thermometer.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewAddDevicesThermometerHumanReadableSerialNumber);
        device_controls_thermometer.tableRowLotNumber = v.findViewById(R.id.tableRowThermometerLotNumber);
        device_controls_thermometer.textViewLotNumber = v.findViewById(R.id.textViewThermometerLotNumber);
        device_controls_thermometer.tableRowManufactureDate = v.findViewById(R.id.tableRowThermometerManufactureDate);
        device_controls_thermometer.textViewManufactureDate = v.findViewById(R.id.textViewThermometerManufactureDate);
        device_controls_thermometer.tableRowExpirationDate = v.findViewById(R.id.tableRowThermometerExpirationDate);
        device_controls_thermometer.textViewExpirationDate = v.findViewById(R.id.textViewThermometerExpirationDate);

        device_controls_pulse_ox.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewPulseOximeterHumanReadableSerialNumber);

        device_controls_blood_pressure.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewBloodPressureHumanReadableSerialNumber);

        device_controls_scales.textViewHumanReadableSerialNumber = v.findViewById(R.id.textViewScalesHumanReadableSerialNumber);

        device_controls_lifetouch.viewGoodToUse = v.findViewById(R.id.viewLifetouchGoodToUse);
        setViewGoodToUseInvisible(device_controls_lifetouch);

        device_controls_thermometer.viewGoodToUse = v.findViewById(R.id.viewThermometerGoodToUse);
        setViewGoodToUseInvisible(device_controls_thermometer);

        device_controls_pulse_ox.viewGoodToUse = v.findViewById(R.id.viewPulseOximeterGoodToUse);
        setViewGoodToUseInvisible(device_controls_pulse_ox);

        device_controls_blood_pressure.viewGoodToUse = v.findViewById(R.id.viewBloodPressureGoodToUse);
        setViewGoodToUseInvisible(device_controls_blood_pressure);

        device_controls_scales.viewGoodToUse = v.findViewById(R.id.viewScalesGoodToUse);
        setViewGoodToUseInvisible(device_controls_scales);

        textViewAddDevicesCheckingServer = v.findViewById(R.id.textViewCheckDeviceStatusCheckingServer);
        textViewAddDevicesCheckingServer.setVisibility(View.INVISIBLE);
        
        ProgressBar progressBarAddDevicesCheckDeviceStatusProgressBar = v.findViewById(R.id.progressBarAddDevicesCheckDeviceStatusProgressBar);
        progressBarAddDevicesCheckDeviceStatusProgressBar.setVisibility(View.INVISIBLE);
        
        textViewAddDevicesDeviceWardNameLabel = v.findViewById(R.id.textViewAddDevicesDeviceWardNameLabel);
        textViewAddDevicesDeviceWardNameLabel.setVisibility(View.INVISIBLE);
        textViewAddDevicesDeviceWardName = v.findViewById(R.id.textViewAddDevicesDeviceWardName);
        textViewAddDevicesDeviceWardName.setVisibility(View.INVISIBLE);
        textViewAddDevicesDeviceBedNameLabel = v.findViewById(R.id.textViewAddDevicesDeviceBedNameLabel);
        textViewAddDevicesDeviceBedNameLabel.setVisibility(View.INVISIBLE);
        textViewAddDevicesDeviceBedName = v.findViewById(R.id.textViewAddDevicesDeviceBedName);
        textViewAddDevicesDeviceBedName.setVisibility(View.INVISIBLE);

        TableRow tableRowEarlyWarningScores = v.findViewById(R.id.tableRowEarlyWarningScores);

        buttonAddEarlyWarningScore = v.findViewById(R.id.buttonAddEarlyWarningScore);
        buttonGrayedOutAddEarlyWarningScore = v.findViewById(R.id.buttonGrayedOutAddEarlyWarningScore);
        buttonRemoveEarlyWarningScore = v.findViewById(R.id.buttonRemoveEarlyWarningScore);
        buttonGrayedOutRemoveEarlyWarningScore = v.findViewById(R.id.buttonGrayedOutRemoveEarlyWarningScore);

        tableRowEarlyWarningScores.setVisibility(View.VISIBLE);
        updateAddRemoveEarlyWarningScoreButton();

        Button buttonManualVitalsOnly = v.findViewById(R.id.buttonManualVitalsOnly);
        buttonManualVitalsOnly.setOnClickListener(this);

        hideDataMatrixInfo(SensorType.SENSOR_TYPE__LIFETOUCH);
        hideDataMatrixInfo(SensorType.SENSOR_TYPE__TEMPERATURE);

        return v;
    }


    @Override
    public void onResume()
    {
        barcodeFragment = new BarcodeFragment();
        barcodeFragment.setFrontCamera(main_activity_interface.useFrontCamera());
        barcodeFragment.setScanResultHandler(this);

        if (main_activity_interface.haveCamera())
        {
            linearLayoutNoCameraControls.setVisibility(View.GONE);
            replaceFragmentIfSafe(R.id.qr_bar_code, barcodeFragment);
        }
        else
        {
            linearLayoutNoCameraControls.setVisibility(View.VISIBLE);
        }

        main_activity_interface.getAllDeviceInfosFromGateway();

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
            Log.e(TAG, "onPause : FragmentAddDevices getActivity() is null");
        }

        super.onPause();
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.buttonRemoveLifetouch)
        {
            buttonRemoveDeviceClickCommon(SensorType.SENSOR_TYPE__LIFETOUCH);
        }
        else if (id == R.id.buttonRemoveThermometer)
        {
            buttonRemoveDeviceClickCommon(SensorType.SENSOR_TYPE__TEMPERATURE);
        }
        else if (id == R.id.buttonRemovePulseOximeter)
        {
            buttonRemoveDeviceClickCommon(SensorType.SENSOR_TYPE__SPO2);
        }
        else if (id == R.id.buttonRemoveBloodPressure)
        {
            buttonRemoveDeviceClickCommon(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        }
        else if (id == R.id.buttonRemoveScales)
        {
            buttonRemoveDeviceClickCommon(SensorType.SENSOR_TYPE__WEIGHT_SCALE);
        }
        else if (id == R.id.buttonAddEarlyWarningScore)
        {
            buttonAddEarlyWarningScoreClick();
        }
        else if (id == R.id.buttonRemoveEarlyWarningScore)
        {
            buttonRemoveEarlyWarningScoreClick();
        }
        else if (id == R.id.buttonManualVitalsOnly)
        {
            buttonManualVitalsOnlyClick();
        }
    }


    private void setupRemoveButton(DeviceControls device_control, View v, int control_id)
    {
        device_control.buttonRemove = v.findViewById(control_id);
        device_control.buttonRemove.setOnClickListener(this);
        device_control.buttonRemove.setVisibility(View.INVISIBLE);
    }

    
    private void showOnceFinishedAddingDevicesHelperTextAndArrowImageVisibility(boolean visible)
    {
    	if((textViewAddDeviceFinishAdding != null) && (imageViewAddDeviceDownArrow != null))
    	{
    		if(visible)
    		{
    			textViewAddDeviceFinishAdding.setVisibility(View.VISIBLE);
    			imageViewAddDeviceDownArrow.setVisibility(View.VISIBLE);
    		}
    		else
    		{
    			textViewAddDeviceFinishAdding.setVisibility(View.INVISIBLE);
    			imageViewAddDeviceDownArrow.setVisibility(View.INVISIBLE);
    		}
    	}
    }
    
    
    private void hideDeviceStatusFromServer()
    {
        textViewAddDevicesCheckingServer.setVisibility(View.INVISIBLE);
        
        textViewAddDevicesDeviceWardNameLabel.setVisibility(View.INVISIBLE);
        textViewAddDevicesDeviceWardName.setVisibility(View.INVISIBLE);
        textViewAddDevicesDeviceBedNameLabel.setVisibility(View.INVISIBLE);
        textViewAddDevicesDeviceBedName.setVisibility(View.INVISIBLE);
    }


    private void buttonRemoveDeviceClickCommon(SensorType sensor_type)
    {
        DeviceInfo device_info = main_activity_interface.getDeviceByType(sensor_type);

        Log.d(TAG, "buttonRemoveDeviceClickCommon : " + device_info.device_type + " : " + sensor_type + " : " + device_info.getActualDeviceConnectionStatus() + " : Device Session Running : " + device_info.isDeviceSessionInProgress());

        setHumanReadableDeviceIdByType(device_info, INVALID_HUMAN_READABLE_DEVICE_ID);

        setViewGoodToUseInvisible(getDeviceControls(sensor_type));

        hideDataMatrixInfo(sensor_type);

        hideDeviceStatusFromServer();

        main_activity_interface.removeDeviceFromGatewayAndRemoveFromEwsIfRequested(sensor_type);

        showConnectionTextAndArrowIfOneOrMoreDevicesAdded();
    }


    private void buttonAddEarlyWarningScoreClick()
    {
    	main_activity_interface.enableEarlyWarningScoreDevice(true);
    	
    	buttonAddEarlyWarningScore.setVisibility(View.INVISIBLE);
    	buttonRemoveEarlyWarningScore.setVisibility(View.VISIBLE);
    }
    
    
    private void buttonRemoveEarlyWarningScoreClick()
    {
    	main_activity_interface.enableEarlyWarningScoreDevice(false);
    	
    	buttonAddEarlyWarningScore.setVisibility(View.VISIBLE);
    	buttonRemoveEarlyWarningScore.setVisibility(View.INVISIBLE);

        showConnectionTextAndArrowIfOneOrMoreDevicesAdded();
    }


    private void buttonManualVitalsOnlyClick()
    {
        main_activity_interface.manualVitalsOnlySelected();
    }
    
    
    public void showDataMatrixInfo(SensorType sensor_type, String lot_number, DateTime manufacture_date, DateTime expiration_date)
    {
        DeviceControls device_controls = getDeviceControls(sensor_type);

        String manufacture_date_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDay(manufacture_date);
        String expiration_date_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDay(expiration_date);

        // If the Lot Number is not empty then show it
        if (!lot_number.equals(NO_LOT_NUMBER))
        {
            if (device_controls != null)
            {
                if (device_controls.textViewLotNumber != null)
                {
                    device_controls.textViewLotNumber.setText(lot_number);
                }

                if (device_controls.tableRowLotNumber != null)
                {
                    device_controls.tableRowLotNumber.setVisibility(View.VISIBLE);
                }
            }
        }

        // If the Manufacture date is real (not Unix time 0)
        if (manufacture_date.isAfter(new DateTime(0)))
        {
            if (device_controls != null)
            {
                if (device_controls.textViewManufactureDate != null)
                {
                    device_controls.textViewManufactureDate.setText(manufacture_date_string);
                }

                if (device_controls.tableRowManufactureDate != null)
                {
                    device_controls.tableRowManufactureDate.setVisibility(View.VISIBLE);
                }
            }
        }

        // If the Expiration date is real (not Unix time 0)
        if (expiration_date.isAfter(new DateTime(0)))
        {
            if (device_controls != null)
            {
                if (device_controls.textViewExpirationDate != null)
                {
                    device_controls.textViewExpirationDate.setText(expiration_date_string);
                }

                if (device_controls.tableRowExpirationDate != null)
                {
                    device_controls.tableRowExpirationDate.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void hideDataMatrixInfo(SensorType sensor_type)
    {
        DeviceControls device_controls = getDeviceControls(sensor_type);

        if (device_controls != null)
        {
            setTableRowGone(device_controls.tableRowLotNumber);
            setTableRowGone(device_controls.tableRowManufactureDate);
            setTableRowGone(device_controls.tableRowExpirationDate);
        }
    }


    private void setTableRowGone(TableRow tableRow)
    {
        if (tableRow != null)
        {
            tableRow.setVisibility(View.GONE);
        }
    }


    private DeviceControls getDeviceControls(SensorType sensor_type)
    {
        switch (sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                return device_controls_lifetouch;
            }

            case SENSOR_TYPE__TEMPERATURE:
            {
                return device_controls_thermometer;
            }

            case SENSOR_TYPE__SPO2:
            {
                return device_controls_pulse_ox;
            }

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                return device_controls_blood_pressure;
            }

            case SENSOR_TYPE__WEIGHT_SCALE:
            {
                return device_controls_scales;
            }
        }

        return null;
    }


    public void setHumanReadableDeviceIdByType(DeviceInfo device_info, long human_readable_device_id)
    {
        DeviceControls device_controls = getDeviceControls(device_info.sensor_type);

        if (device_controls != null)
        {
            if (human_readable_device_id == INVALID_HUMAN_READABLE_DEVICE_ID)
            {
                device_controls.textViewHumanReadableSerialNumber.setText(getResources().getString(R.string.six_dashes));
                device_controls.buttonRemove.setVisibility(View.INVISIBLE);
            }
            else
            {
                device_controls.textViewHumanReadableSerialNumber.setText(String.valueOf(human_readable_device_id));
                device_controls.buttonRemove.setVisibility(View.VISIBLE);
            }

            showConnectionTextAndArrowIfOneOrMoreDevicesAdded();
        }
    }


    private void showConnectionTextAndArrowIfOneOrMoreDevicesAdded()
    {
        ArrayList<DeviceInfo> bluetooth_devices_in_use = main_activity_interface.getBluetoothDevicesTypesInUse();

        if (bluetooth_devices_in_use.size() > 0)
        {
            boolean show_text_and_arrow = false;

            // Go through each of the Bluetooth Sensor devices and see if any of them are not connected yet
            for (DeviceInfo device_info : bluetooth_devices_in_use)
            {
                // Only show if there is a device without a valid device session yet - means it needs to be scanned for and added.
                if (device_info.scanRequired())
                {
                    show_text_and_arrow = true;
                }
            }

            if (textViewAddDeviceFinishAdding != null)
            {
                textViewAddDeviceFinishAdding.setText(getResources().getString(R.string.once_finished_adding_devices_press_connect));
            }

            showOnceFinishedAddingDevicesHelperTextAndArrowImageVisibility(show_text_and_arrow);

            if(main_activity_interface.includeManualVitalSignEntry())
            {
                linearLayoutManualVitalsOnly.setVisibility(View.GONE);
            }
        }
        else
        {
            showOnceFinishedAddingDevicesHelperTextAndArrowImageVisibility(false);

            if(main_activity_interface.includeManualVitalSignEntry())
            {
                linearLayoutManualVitalsOnly.setVisibility(View.VISIBLE);
            }
        }
    }
    
    
    private void showDeviceAlreadyInUseAccordingToServer(String ward_name, String bed_name)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            textViewAddDevicesCheckingServer.setTextColor(ContextCompat.getColor(activity, R.color.red));
            textViewAddDevicesCheckingServer.setText(getResources().getString(R.string.device_already_in_use_according_to_server));
            textViewAddDevicesCheckingServer.setVisibility(View.VISIBLE);

            textViewAddDevicesDeviceWardNameLabel.setVisibility(View.VISIBLE);
            textViewAddDevicesDeviceWardName.setText(ward_name);
            textViewAddDevicesDeviceWardName.setVisibility(View.VISIBLE);

            textViewAddDevicesDeviceBedNameLabel.setVisibility(View.VISIBLE);
            textViewAddDevicesDeviceBedName.setText(bed_name);
            textViewAddDevicesDeviceBedName.setVisibility(View.VISIBLE);

            if (progressBarAddDevicesCheckDeviceStatusProgressBar != null)
            {
                progressBarAddDevicesCheckDeviceStatusProgressBar.setVisibility(View.INVISIBLE);
            }
            else
            {
                Log.e("showDeviceAlreadyInUseAccordingToServer", "progressBarAddDevicesCheckDeviceStatusProgressBar = NULL");
            }
        }
    }
    
    
    public void showDeviceGoodToUseResult(SensorType sensor_type, boolean good_to_use, String ward_name, String bed_name)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            if (!good_to_use)
            {
                showDeviceAlreadyInUseAccordingToServer(ward_name, bed_name);
            }

            DeviceControls device_controls = getDeviceControls(sensor_type);

            if (device_controls != null)
            {
                if (device_controls.viewGoodToUse != null)
                {
                    if (good_to_use)
                    {
                        device_controls.viewGoodToUse.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_green));
                    }
                    else
                    {
                        device_controls.viewGoodToUse.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_red));
                    }

                    device_controls.viewGoodToUse.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    
    public void serverRequestFailed()
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            if (textViewAddDevicesCheckingServer != null)
            {
                textViewAddDevicesCheckingServer.setTextColor(ContextCompat.getColor(activity, R.color.red));
                textViewAddDevicesCheckingServer.setText(getResources().getString(R.string.problem_checking_server__please_try_again_later));
                textViewAddDevicesCheckingServer.setVisibility(View.VISIBLE);
            }

            if (progressBarAddDevicesCheckDeviceStatusProgressBar != null)
            {
                progressBarAddDevicesCheckDeviceStatusProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void showCheckingWithServer()
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            if (textViewAddDevicesCheckingServer != null)
            {
                textViewAddDevicesCheckingServer.setTextColor(ContextCompat.getColor(activity, R.color.black));
                textViewAddDevicesCheckingServer.setText(getResources().getString(R.string.checking_with_server));
            }

            if (progressBarAddDevicesCheckDeviceStatusProgressBar != null)
            {
                progressBarAddDevicesCheckDeviceStatusProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }


    public void updateAddRemoveEarlyWarningScoreButton()
    {
        if(!main_activity_interface.isSessionInProgress())
        {
            // Enable add/remove buttons
            buttonAddEarlyWarningScore.setOnClickListener(this);
            buttonRemoveEarlyWarningScore.setOnClickListener(this);

            buttonGrayedOutAddEarlyWarningScore.setVisibility(View.GONE);
            buttonGrayedOutRemoveEarlyWarningScore.setVisibility(View.GONE);

            if (main_activity_interface.getEarlyWarningScoresDeviceEnabled())
            {
                buttonAddEarlyWarningScore.setVisibility(View.INVISIBLE);
                buttonRemoveEarlyWarningScore.setVisibility(View.VISIBLE);
            }
            else
            {
                buttonAddEarlyWarningScore.setVisibility(View.VISIBLE);
                buttonRemoveEarlyWarningScore.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            if (main_activity_interface.getEarlyWarningScoresDeviceEnabled())
            {
                // EWS has been added. So "active" buttons must be hidden as its not possible to remove EWS from an active session
                buttonAddEarlyWarningScore.setVisibility(View.GONE);
                buttonRemoveEarlyWarningScore.setVisibility(View.GONE);

                buttonGrayedOutAddEarlyWarningScore.setVisibility(View.INVISIBLE);
                buttonGrayedOutRemoveEarlyWarningScore.setVisibility(View.VISIBLE);
            }
            else
            {
                // No "inactive" buttons
                buttonGrayedOutAddEarlyWarningScore.setVisibility(View.GONE);
                buttonGrayedOutRemoveEarlyWarningScore.setVisibility(View.GONE);

                buttonAddEarlyWarningScore.setOnClickListener(this);
                buttonAddEarlyWarningScore.setVisibility(View.VISIBLE);
                buttonRemoveEarlyWarningScore.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void setViewGoodToUseInvisible(DeviceControls device_controls)
    {
        if (device_controls != null)
        {
            device_controls.viewGoodToUse.setVisibility(View.INVISIBLE);
        }
    }
}
