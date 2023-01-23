package com.isansys.pse_isansysportal;

import static com.isansys.common.DeviceInfoConstants.INVALID_DEVICE_SESSION_ID;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.google.zxing.BarcodeFormat;
import com.isansys.common.enums.SensorType;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

import roo.clockanimation.ClockDrawable;

public class FragmentQrCodeUnlock extends FragmentIsansys implements IScanResultHandler
{
    private final String TAG = this.getClass().getSimpleName();

    private final int qr_code_page_timeout = 30 * (int)DateUtils.SECOND_IN_MILLIS;

    private BarcodeFragment barcodeFragment;

    private TableRow tableRowStartVideoCall;
    private TableRow tableRowManuallyEnteredOnlySession;
    private TableRow tableRowSoftwareUpdatePending;
    private TableRow tableRowSensorDeviceInfo;


    private static class DeviceControls
    {
        TextView textDeviceType;
        LinearLayout linearLayoutLockScreenDeviceInfo;
        TextView textHumanReadableDeviceId;
        ImageView imageView;
        LinearLayout linearLayoutLockScreenDeviceRedBorderStatus;
        TextView textRedBorderStatus;
        ImageView imageBattery;
        TextView textBatteryPercentage;
        ClockDrawable clockDrawable;
        ImageView imageOutOfRange;
        LinearLayout linearLayoutLockScreenDeviceInfoShowWhenConnected;
        LinearLayout linearLayoutLockScreenDeviceInfoShowWhenNotConnected;
        LinearLayout linearLayoutLockScreenDeviceInfoShowWhenRemoved;

        String string_setup_mode;
        String string_leads_off;
        String string_accelerometer_mode;

        boolean low_battery_blink_action_running;

        DeviceControls()
        {
            string_setup_mode = "";
            string_leads_off = "";
            string_accelerometer_mode = "";

            low_battery_blink_action_running = false;
        }
    }


    private DeviceControls device_controls_lifetouch;
    private DeviceControls device_controls_thermometer;
    private DeviceControls device_controls_pulse_ox;
    private DeviceControls device_controls_blood_pressure;
    private DeviceControls device_controls_weight_scale;

    private TextView textPleaseScanIdQrCode;
    private TextView textQrCodeUnlockPageHelp;
    
    private Button buttonUnlockQrCodeScan;

    private final Handler lifetouch_heartbeat_handler = new Handler();

    private Handler handler = new Handler();


    @Override
    public void scanResult(ScanResult result) 
    {
        main_activity_interface.beep();

        String barcode_code_contents = result.getRawResult().getText();

        BarcodeFormat format = result.getRawResult().getBarcodeFormat();
 
        if (format == BarcodeFormat.QR_CODE)
        {
            main_activity_interface.onQrCodeDetected("FragmentQrCodeUnlock", barcode_code_contents);
        }

        barcodeFragment.restart();
    }
    
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.qr_code_unlock, container, false); // Inflate the layout for this fragment

        tableRowStartVideoCall = v.findViewById(R.id.tableRowStartVideoCall);
        showStartVideoCallButtonIfNeeded();

        LinearLayout linearLayoutStartVideoCall = v.findViewById(R.id.linearLayoutStartVideoCall);
        linearLayoutStartVideoCall.setOnLongClickListener(arg0 -> {

            main_activity_interface.videoCallContactsSelectedFromUnlockPage();

            return true;
        });

        tableRowSoftwareUpdatePending = v.findViewById(R.id.tableRowSoftwareUpdatePending);
        tableRowSoftwareUpdatePending.setVisibility(View.GONE);

        tableRowManuallyEnteredOnlySession = v.findViewById(R.id.tableRowManuallyEnteredOnlySession);
        tableRowManuallyEnteredOnlySession.setVisibility(View.GONE);

        tableRowSensorDeviceInfo = v.findViewById(R.id.tableRowSensorDeviceInfo);

        device_controls_lifetouch = new DeviceControls();
        device_controls_lifetouch.string_setup_mode = getString(R.string.setup_mode);
        device_controls_lifetouch.string_leads_off = getString(R.string.detached);
        device_controls_lifetouch.string_accelerometer_mode = getString(R.string.motion_mode);

        device_controls_thermometer = new DeviceControls();
        device_controls_thermometer.string_leads_off = getString(R.string.detached);

        device_controls_pulse_ox = new DeviceControls();
        device_controls_pulse_ox.string_setup_mode = getString(R.string.setup_mode);
        device_controls_pulse_ox.string_leads_off = getString(R.string.detached);

        device_controls_blood_pressure = new DeviceControls();

        device_controls_weight_scale = new DeviceControls();

        // Lifetouch Connection Status
        device_controls_lifetouch.textDeviceType = v.findViewById(R.id.textLifetouchLabel);
        device_controls_lifetouch.textHumanReadableDeviceId = v.findViewById(R.id.textLifetouchHumanReadableDeviceId);
        device_controls_lifetouch.imageBattery = v.findViewById(R.id.imageBatteryLifetouch);
        device_controls_lifetouch.imageBattery.setImageResource(R.drawable.battery_empty);
        device_controls_lifetouch.textBatteryPercentage = v.findViewById(R.id.textLifetouchBatteryPercentage);
        device_controls_lifetouch.imageView = v.findViewById(R.id.heartbeat);
        device_controls_lifetouch.imageView.setImageResource(R.drawable.heart_current_data);
        device_controls_lifetouch.imageView.setVisibility(View.INVISIBLE);
        device_controls_lifetouch.clockDrawable = new ClockDrawable(getResources());

        device_controls_lifetouch.linearLayoutLockScreenDeviceInfoShowWhenConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoLifetouchShowWhenConnected);
        device_controls_lifetouch.linearLayoutLockScreenDeviceInfoShowWhenNotConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoLifetouchShowWhenNotConnected);
        device_controls_lifetouch.linearLayoutLockScreenDeviceInfoShowWhenRemoved = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoLifetouchShowWhenRemoved);

        device_controls_lifetouch.imageOutOfRange = v.findViewById(R.id.imageLifetouchOutOfRange);
        main_activity_interface.pulseImage(device_controls_lifetouch.imageOutOfRange);

        device_controls_lifetouch.linearLayoutLockScreenDeviceRedBorderStatus = v.findViewById(R.id.linearLayoutLockScreenDeviceRedBorderStatusLifetouch);
        device_controls_lifetouch.linearLayoutLockScreenDeviceRedBorderStatus.setVisibility(View.GONE);
        device_controls_lifetouch.textRedBorderStatus = v.findViewById(R.id.textLifetouchRedBorderStatus);

        device_controls_lifetouch.linearLayoutLockScreenDeviceInfo = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoLifetouch);
        device_controls_lifetouch.linearLayoutLockScreenDeviceInfo.setVisibility(View.INVISIBLE);


        // Thermometer Connection Status
        device_controls_thermometer.textDeviceType = v.findViewById(R.id.textLifetempLabel);
        device_controls_thermometer.textHumanReadableDeviceId = v.findViewById(R.id.textLifetempHumanReadableDeviceId);
        device_controls_thermometer.imageBattery = v.findViewById(R.id.imageBatteryLifetemp);
        device_controls_thermometer.imageBattery.setImageResource(R.drawable.battery_empty);
        device_controls_thermometer.textBatteryPercentage = v.findViewById(R.id.textLifetempBatteryPercentage);
        device_controls_thermometer.linearLayoutLockScreenDeviceRedBorderStatus = v.findViewById(R.id.linearLayoutLockScreenDeviceRedBorderStatusLifetemp);
        device_controls_thermometer.linearLayoutLockScreenDeviceRedBorderStatus.setVisibility(View.GONE);
        device_controls_thermometer.textRedBorderStatus = v.findViewById(R.id.textLifetempRedBorderStatus);
        device_controls_thermometer.imageView = v.findViewById(R.id.imageSpaceFillerLifetemp);

        device_controls_thermometer.linearLayoutLockScreenDeviceInfoShowWhenConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoThermometerShowWhenConnected);
        device_controls_thermometer.linearLayoutLockScreenDeviceInfoShowWhenNotConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoThermometerShowWhenNotConnected);
        device_controls_thermometer.linearLayoutLockScreenDeviceInfoShowWhenRemoved = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoThermometerShowWhenRemoved);

        device_controls_thermometer.imageOutOfRange = v.findViewById(R.id.imageThermometerOutOfRange);
        main_activity_interface.pulseImage(device_controls_thermometer.imageOutOfRange);

        device_controls_thermometer.linearLayoutLockScreenDeviceInfo = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoLifetemp);
        device_controls_thermometer.linearLayoutLockScreenDeviceInfo.setVisibility(View.INVISIBLE);


        // Pulse Ox Connection Status
        device_controls_pulse_ox.textDeviceType = v.findViewById(R.id.textNoninWristOxLabel);
        device_controls_pulse_ox.linearLayoutLockScreenDeviceRedBorderStatus = v.findViewById(R.id.linearLayoutLockScreenDeviceRedBorderStatusNonin);
        device_controls_pulse_ox.linearLayoutLockScreenDeviceRedBorderStatus.setVisibility(View.GONE);
        device_controls_pulse_ox.textRedBorderStatus = v.findViewById(R.id.textNoninRedBorderStatus);
        device_controls_pulse_ox.imageView = v.findViewById(R.id.imageSpaceFillerNonin);
        device_controls_pulse_ox.textHumanReadableDeviceId = v.findViewById(R.id.textNoninWristOxHumanReadableDeviceId);
        device_controls_pulse_ox.imageBattery = v.findViewById(R.id.imageBatteryNoninWristOx);
        device_controls_pulse_ox.imageBattery.setImageResource(R.drawable.battery_empty);

        device_controls_pulse_ox.linearLayoutLockScreenDeviceInfoShowWhenConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoPulseOxShowWhenConnected);
        device_controls_pulse_ox.linearLayoutLockScreenDeviceInfoShowWhenNotConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoPulseOxShowWhenNotConnected);
        device_controls_pulse_ox.linearLayoutLockScreenDeviceInfoShowWhenRemoved = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoPulseOxShowWhenRemoved);

        device_controls_pulse_ox.imageOutOfRange = v.findViewById(R.id.imagePulseOxOutOfRange);
        main_activity_interface.pulseImage(device_controls_pulse_ox.imageOutOfRange);

        device_controls_pulse_ox.linearLayoutLockScreenDeviceInfo = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoNoninWristOx);
        device_controls_pulse_ox.linearLayoutLockScreenDeviceInfo.setVisibility(View.INVISIBLE);


        // Blood Pressure Connection Status
        device_controls_blood_pressure.textDeviceType = v.findViewById(R.id.textBloodPressure);
        device_controls_blood_pressure.linearLayoutLockScreenDeviceRedBorderStatus = v.findViewById(R.id.linearLayoutLockScreenDeviceRedBorderStatusBloodPressure);
        device_controls_blood_pressure.linearLayoutLockScreenDeviceRedBorderStatus.setVisibility(View.GONE);
        device_controls_blood_pressure.textHumanReadableDeviceId = v.findViewById(R.id.textBloodPressureHumanReadableDeviceId);
        device_controls_blood_pressure.imageBattery = v.findViewById(R.id.imageBatteryBloodPressure);
        device_controls_blood_pressure.imageBattery.setImageResource(R.drawable.battery_empty);
        device_controls_blood_pressure.imageBattery.setVisibility(View.INVISIBLE);

        device_controls_blood_pressure.linearLayoutLockScreenDeviceInfoShowWhenConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoBloodPressureShowWhenConnected);
        device_controls_blood_pressure.linearLayoutLockScreenDeviceInfoShowWhenNotConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoBloodPressureShowWhenNotConnected);
        device_controls_blood_pressure.linearLayoutLockScreenDeviceInfoShowWhenRemoved = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoBloodPressureShowWhenRemoved);

        device_controls_blood_pressure.imageOutOfRange = v.findViewById(R.id.imageBloodPressureOutOfRange);
        main_activity_interface.pulseImage(device_controls_blood_pressure.imageOutOfRange);

        device_controls_blood_pressure.linearLayoutLockScreenDeviceInfo = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoBloodPressure);
        device_controls_blood_pressure.linearLayoutLockScreenDeviceInfo.setVisibility(View.INVISIBLE);


        // Weight Scales Connection Status
        device_controls_weight_scale.textDeviceType = v.findViewById(R.id.textWeightScale);
        device_controls_weight_scale.linearLayoutLockScreenDeviceRedBorderStatus = v.findViewById(R.id.linearLayoutLockScreenDeviceRedBorderStatusWeightScale);
        device_controls_weight_scale.linearLayoutLockScreenDeviceRedBorderStatus.setVisibility(View.GONE);
        device_controls_weight_scale.textHumanReadableDeviceId = v.findViewById(R.id.textWeightScaleHumanReadableDeviceId);
        device_controls_weight_scale.imageBattery = v.findViewById(R.id.imageBatteryWeightScale);
        device_controls_weight_scale.imageBattery.setVisibility(View.INVISIBLE);

        device_controls_weight_scale.linearLayoutLockScreenDeviceInfoShowWhenConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoWeightScaleShowWhenConnected);
        device_controls_weight_scale.linearLayoutLockScreenDeviceInfoShowWhenNotConnected = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoWeightScaleShowWhenNotConnected);
        device_controls_weight_scale.linearLayoutLockScreenDeviceInfoShowWhenRemoved = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoWeightScaleShowWhenRemoved);

        device_controls_weight_scale.imageOutOfRange = v.findViewById(R.id.imageWeightScaleOutOfRange);
        main_activity_interface.pulseImage(device_controls_weight_scale.imageOutOfRange);

        device_controls_weight_scale.linearLayoutLockScreenDeviceInfo = v.findViewById(R.id.linearLayoutLockScreenDeviceInfoWeightScale);
        device_controls_weight_scale.linearLayoutLockScreenDeviceInfo.setVisibility(View.INVISIBLE);


        // Button to activate the camera for scanning the QR code
        buttonUnlockQrCodeScan = v.findViewById(R.id.buttonUnlockQrCodeScanScreen);

        // If no camera then these buttons are used
        Button buttonSimulateAdminQrCode = v.findViewById(R.id.buttonSimulateAdminQrCode);
        Button buttonSimulateGeneralQrCode = v.findViewById(R.id.buttonSimulateGeneralQrCode);

        if (main_activity_interface.haveCamera())
        {
            buttonUnlockQrCodeScan.setVisibility(View.INVISIBLE);
            buttonUnlockQrCodeScan.setEnabled(false);

            buttonSimulateGeneralQrCode.setVisibility(View.GONE);
            buttonSimulateAdminQrCode.setVisibility(View.GONE);
        }
        else
        {
            buttonUnlockQrCodeScan.setVisibility(View.GONE);

            buttonSimulateGeneralQrCode.setVisibility(View.VISIBLE);
            buttonSimulateGeneralQrCode.setEnabled(true);
            buttonSimulateGeneralQrCode.setOnClickListener(x -> main_activity_interface.dummyUnlockCode());

            buttonSimulateAdminQrCode.setVisibility(View.VISIBLE);
            buttonSimulateAdminQrCode.setEnabled(true);
            buttonSimulateAdminQrCode.setOnClickListener(x -> main_activity_interface.dummyAdminCode());
        }

        textPleaseScanIdQrCode = v.findViewById(R.id.textPleaseScanIdQrCode);
        textPleaseScanIdQrCode.setText(R.string.please_scan_general_user_qr_code_to_access_patient_gateway);

        textQrCodeUnlockPageHelp = v.findViewById(R.id.textQrCodeUnlockPageHelp);

        if (main_activity_interface.isScreenLandscape())
        {
            textQrCodeUnlockPageHelp.setText(R.string.hold_qr_code_so_it_fits_in_the_smaller_lighter_square_on_the_left);
        }
        else
        {
            textQrCodeUnlockPageHelp.setText(R.string.hold_qr_code_so_it_fits_in_the_smaller_lighter_square_above);
        }

        handler = new Handler();
        
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
            replaceFragmentIfSafe(R.id.qr_bar_code, barcodeFragment);

            setupButtonTimeout();

            if (buttonUnlockQrCodeScan != null)
            {
                buttonUnlockQrCodeScan.setOnClickListener(v -> buttonScanQrCodeClicked());
            }
        }

        main_activity_interface.getAllDeviceInfosFromGateway();

        setupForDevice(main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH));
        setupForDevice(main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE));
        setupForDevice(main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__SPO2));
        setupForDevice(main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE));

        main_activity_interface.refreshDeviceLeadsOffStatus();

        super.onResume();
    }

    public void buttonScanQrCodeClicked()
    {
        textPleaseScanIdQrCode.setText(R.string.please_scan_general_user_qr_code_to_access_patient_gateway);
        textPleaseScanIdQrCode.setVisibility(View.VISIBLE);
        textQrCodeUnlockPageHelp.setVisibility(View.VISIBLE);

        buttonUnlockQrCodeScan.setVisibility(View.INVISIBLE);
        buttonUnlockQrCodeScan.setEnabled(false);

        replaceFragmentIfSafe(R.id.qr_bar_code, barcodeFragment);

        setupButtonTimeout();
    }

    private void setupForDevice(DeviceInfo device_info)
    {
        if (device_info.isDeviceOffBody())
        {
            showDeviceLeadsOffDetected(device_info, true);
        }
        if (device_info.isDeviceInSetupMode())
        {
            showDeviceInSetupMode(device_info, true);
        }
        if(device_info.isDeviceInRawAccelerometerMode())
        {
            showDeviceInRawAccelerometerMode(device_info, true);
        }
    }

    public void setupButtonTimeout()
    {
        Runnable QrCodeTouchButtonActivationRunnable = () -> {
            if(getActivity() != null)
            {
                getChildFragmentManager().beginTransaction().remove(barcodeFragment).commit();
                buttonUnlockQrCodeScan.setVisibility(View.VISIBLE);
                buttonUnlockQrCodeScan.setEnabled(true);

                textPleaseScanIdQrCode.setText(R.string.press_button_to_scan_qr_code);
                textPleaseScanIdQrCode.setVisibility(View.INVISIBLE);
                textQrCodeUnlockPageHelp.setVisibility(View.INVISIBLE);

                if (main_activity_interface.isScreensaverEnabled())
                {
                    if (main_activity_interface.isSessionInProgress() == false)
                    {
                        Log.d(TAG, "setupButtonTimeout : Starting screensaver");

                        main_activity_interface.showScreensaver();
                    }
                }
            }
            else
            {
                Log.e(TAG, "setButtonPressedActivities : getActivity() is null");
            }
        };

        handler.postDelayed(QrCodeTouchButtonActivationRunnable, qr_code_page_timeout);
    }
    
    
    @Override
    public void onPause()
    {
        if(getActivity() != null)
        {
            if (main_activity_interface.haveCamera())
            {
                getChildFragmentManager().beginTransaction().remove(barcodeFragment).commit();
            }
        }
        else
        {
            Log.e(TAG, "onPause : FragmentQrCodeUnlock getActivity() is null");
        }

        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }


    private DeviceControls getDeviceControls(DeviceInfo device_info)
    {
        switch (device_info.sensor_type)
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
                return device_controls_weight_scale;
            }
        }

        return null;
    }


    // Use View.VISIBLE or View.INVISIBLE
    private void showDeviceInfoSubFunction(DeviceInfo device_info, int visible)
    {
        DeviceControls device_controls = getDeviceControls(device_info);
        if (device_controls != null)
        {
            device_controls.linearLayoutLockScreenDeviceInfo.setVisibility(visible);

            device_controls.textDeviceType.setText(main_activity_interface.getDeviceNameFromSensorType(device_info.sensor_type));
            
            if(device_info.isDeviceHumanReadableDeviceIdValid() && device_info.android_database_device_session_id != INVALID_DEVICE_SESSION_ID)
            {
                device_controls.textHumanReadableDeviceId.setVisibility(View.VISIBLE);
                device_controls.textHumanReadableDeviceId.setText(String.valueOf(device_info.human_readable_device_id));
            }
            else
            {
                device_controls.textHumanReadableDeviceId.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    
    public void showHeartBeatIcon(HeartBeatInfo heart_beat)
    {
        if (device_controls_lifetouch.imageView != null)
        {
            int display_time_in_ms;

            device_controls_lifetouch.imageView.setVisibility(View.VISIBLE);

            if (heart_beat.isBeatRealTime())
            {
                if (main_activity_interface.getShowLifetouchActivityLevel())
                {
                    switch(heart_beat.getActivity())
                    {
                        case LOW:
                            device_controls_lifetouch.imageView.setImageResource(R.drawable.heart_low_activity);
                            break;

                        case HIGH:
                            /* Instructions For Use say two bars are displayed for high activity (which is the "medium" icon). Since we haven't defined this anywhere else,
                             * and changing the instructions requires new translations, for convenience changing this to "medium" icon (two bars) instead of "high" (three bars)
                             */
                            device_controls_lifetouch.imageView.setImageResource(R.drawable.heart_medium_activity);
                            break;

                        case NO_DATA:
                        case NONE:
                            device_controls_lifetouch.imageView.setImageResource(R.drawable.heart_current_data);
                    }
                }
                else
                {
                    device_controls_lifetouch.imageView.setImageResource(R.drawable.heart_current_data);
                }

                display_time_in_ms = 250;
            }
            else
            {
                device_controls_lifetouch.imageView.setImageDrawable(device_controls_lifetouch.clockDrawable);
                device_controls_lifetouch.clockDrawable.start(new DateTime(heart_beat.getTimestampInMs()));

                display_time_in_ms = 1500;
            }

            lifetouch_heartbeat_handler.removeCallbacksAndMessages(null);
            lifetouch_heartbeat_handler.postDelayed(runnableHideLifetouchHeartBeat, display_time_in_ms);
        }
    }


    private final Runnable runnableHideLifetouchHeartBeat = new Runnable()
    {
        @Override
        public void run()
        {
            device_controls_lifetouch.imageView.setVisibility(View.INVISIBLE);
        }
    };


    private void showRedBorderStatus(boolean show, String displayString, DeviceControls device_controls)
    {
        if (device_controls != null)
        {
            if (show)
            {
                device_controls.imageView.setVisibility(View.GONE);

                device_controls.linearLayoutLockScreenDeviceRedBorderStatus.setVisibility(View.VISIBLE);
                device_controls.textRedBorderStatus.setText(displayString);
            }
            else
            {
                device_controls.imageView.setVisibility(View.INVISIBLE);
                device_controls.linearLayoutLockScreenDeviceRedBorderStatus.setVisibility(View.GONE);
            }
        }
    }


    public void showDeviceLeadsOffDetected(DeviceInfo device_info, boolean leads_off_detected)
    {
        DeviceControls device_controls = getDeviceControls(device_info);
        if (device_controls != null)
        {
            showRedBorderStatus(leads_off_detected, device_controls.string_leads_off, device_controls);
        }
    }


    public void showDeviceInSetupMode(DeviceInfo device_info, boolean in_setup_mode)
    {
        DeviceControls device_controls = getDeviceControls(device_info);
        if (device_controls != null)
        {
             showRedBorderStatus(in_setup_mode, device_controls.string_setup_mode, device_controls);

             // Refresh device controls, device may have been Leads off before setup mode started and finished and is still Leads Off
             if (in_setup_mode == false)
             {
                 setupForDevice(device_info);
             }
        }
    }


    public void showDeviceInRawAccelerometerMode(DeviceInfo device_info, boolean in_raw_accelerometer_mode)
    {
        DeviceControls device_controls = getDeviceControls(device_info);
        if (device_controls != null)
        {
            showRedBorderStatus(in_raw_accelerometer_mode, device_controls.string_accelerometer_mode, device_controls);
        }
    }


    private void updateBatteryImageFromBatteryPercentage(ImageView desired_image, int battery_percentage)
    {
        if (battery_percentage < 5)                                                     // 0 - 4 = Empty
        {
            desired_image.setImageResource(R.drawable.battery_empty);   
        }
        else if (battery_percentage < 10)                                               // 5 - 9 = Almost Empty
        {
            desired_image.setImageResource(R.drawable.battery_almost_empty);            
        }
        else if (battery_percentage < 25)                                               // 10 - 25 = 25% 
        {
            desired_image.setImageResource(R.drawable.battery_25_percent);
        }
        else if (battery_percentage < 50)                                               // 25 - 49 = 50%
        {
            desired_image.setImageResource(R.drawable.battery_50_percent);
        }
        else if (battery_percentage < 75)                                               // 50 - 74 = 75%
        {
            desired_image.setImageResource(R.drawable.battery_75_percent);
        }
        else if (battery_percentage < 90)                                               // 75 - 89 = Almost Full
        {
            desired_image.setImageResource(R.drawable.battery_almost_full);
        }
        else if (battery_percentage <= 100)                                             // 90 - 100 = Full
        {
            desired_image.setImageResource(R.drawable.battery_full);
        }
    }


    private void updateBatteryMeasurementText(TextView desired_text, int battery_percentage, int battery_voltage_in_millivolts, Date measurement_time)
    {
        String string = battery_percentage + "%\n" + battery_voltage_in_millivolts + "mV\n" + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement_time);
        desired_text.setText(string);
    }


    public void setDeviceBatteryLevel(DeviceInfo device_info)
    {
        Date time_measurement_received = new Date(device_info.last_battery_reading_received_timestamp);

        DeviceControls device_controls = getDeviceControls(device_info);
        if (device_controls != null)
        {
            switch (device_info.sensor_type)
            {
                case SENSOR_TYPE__LIFETOUCH:
                case SENSOR_TYPE__TEMPERATURE:
                {
                    setDeviceBatteryLevel(device_controls, device_info.last_battery_reading_percentage, device_info.last_battery_reading_in_millivolts, time_measurement_received);
                }
                break;

                case SENSOR_TYPE__SPO2:
                case SENSOR_TYPE__BLOOD_PRESSURE:
                case SENSOR_TYPE__WEIGHT_SCALE:
                {
                    setDeviceBatteryLevel(device_controls, device_info.last_battery_reading_percentage);
                }
                break;
            }
        }
    }


    private void setDeviceBatteryLevel(DeviceControls device_controls, int battery_percentage, int battery_voltage_in_millivolts, Date time_measurement_received)
    {
        if (device_controls.imageBattery != null)
        {
            if(battery_voltage_in_millivolts < 0)
            {
                // Invalid battery level
                device_controls.textBatteryPercentage.setVisibility(View.INVISIBLE);
                device_controls.imageBattery.setVisibility(View.INVISIBLE);
            }
            else
            {
                if(main_activity_interface.getShowNumbersOnBatteryIndicator())
                {
                    device_controls.textBatteryPercentage.setVisibility(View.VISIBLE);
                }
                else
                {
                    device_controls.textBatteryPercentage.setVisibility(View.GONE);
                }

                device_controls.imageBattery.setVisibility(View.VISIBLE);

                updateBatteryMeasurementText(device_controls.textBatteryPercentage, battery_percentage, battery_voltage_in_millivolts, time_measurement_received);

                updateBatteryImageFromBatteryPercentage(device_controls.imageBattery, battery_percentage);

                if(battery_percentage < 10)
                {
                    lowBatteryBlinkAction(device_controls);
                }
                else
                {
                    lowBatteryBlinkActionStop(device_controls);
                }
            }
        }
    }


    private void lowBatteryBlinkAction(final DeviceControls device_controls)
    {
        if (device_controls.imageBattery != null)
        {
            if(device_controls.imageBattery.getVisibility() == View.VISIBLE)
            {
                device_controls.imageBattery.setVisibility(View.INVISIBLE);
            }
            else
            {
                device_controls.imageBattery.setVisibility(View.VISIBLE);
            }
        }
        
        if(device_controls.low_battery_blink_action_running)
        {
            Runnable runnable = () -> lowBatteryBlinkAction(device_controls);
            handler.postDelayed(runnable, 500);
        }
    }
    
    
    private void lowBatteryBlinkActionStop(DeviceControls device_controls)
    {
        device_controls.low_battery_blink_action_running = false;
        device_controls.imageBattery.setVisibility(View.VISIBLE);
    }
       

    private void setDeviceBatteryLevel(DeviceControls device_controls, int battery_level)
    {
        if (device_controls != null)
        {
            if (device_controls.imageBattery != null)
            {
                if(battery_level < 0)
                {
                    device_controls.imageBattery.setVisibility(View.INVISIBLE);
                }
                else
                {
                    device_controls.imageBattery.setVisibility(View.VISIBLE);

                    updateBatteryImageFromBatteryPercentage(device_controls.imageBattery, battery_level);
                }
            }
        }
    }


    private void showDeviceConnectionStatus(DeviceInfo device_info, boolean connected, boolean removed)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            // If the device is physically connected (but Start Monitoring has not been pressed yet) then still show as removed
            // "connected" may be true or false at this stage as devices are discovered/undiscovered/pairing failed but not fully connected until pressing Start Monitoring
            // If "connected" being true is used to check for this stage, the grey boxes become red temporarily on this unlock screen.
            if (!device_info.isDeviceSessionInProgress())
            {
                connected = false;
                removed = true;
            }

            // Some devices do not provide battery information, so hide icons on the unlock screen
            boolean hide_battery_info = false;
            if (device_info.supports_battery_info == false)
            {
                hide_battery_info = true;
            }

            DeviceControls device_controls = getDeviceControls(device_info);
            if (device_controls != null)
            {
                if(device_info.dummy_data_mode)
                {
                    device_controls.linearLayoutLockScreenDeviceInfo.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_corner_dummy_data_mode));
                }
                else
                {
                    showDeviceConnectionStatus(device_controls.linearLayoutLockScreenDeviceInfo,
                            device_controls.imageBattery,
                            device_controls.textBatteryPercentage,
                            device_controls.linearLayoutLockScreenDeviceInfoShowWhenConnected,
                            device_controls.linearLayoutLockScreenDeviceInfoShowWhenNotConnected,
                            device_controls.linearLayoutLockScreenDeviceInfoShowWhenRemoved,
                            connected,
                            removed,
                            hide_battery_info);
                }
            }
        }
    }


    private void showDeviceConnectionStatus(LinearLayout linear_layout_background,
                                            ImageView battery_image,
                                            TextView textview_battery_percentage,
                                            LinearLayout deviceInfoWhenConnected,
                                            LinearLayout deviceInfoWhenNotConnected,
                                            LinearLayout deviceInfoWhenRemoved,
                                            boolean connected, boolean removed, boolean hide_battery_info)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            if (connected)
            {
                if (linear_layout_background != null)
                {
                    linear_layout_background.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_corner_green));
                }

                if (deviceInfoWhenConnected != null)
                {
                    deviceInfoWhenConnected.setVisibility(View.VISIBLE);
                }

                if (deviceInfoWhenNotConnected != null)
                {
                    deviceInfoWhenNotConnected.setVisibility(View.GONE);
                }

                if (deviceInfoWhenRemoved != null)
                {
                    deviceInfoWhenRemoved.setVisibility(View.GONE);
                }

                if (!hide_battery_info)
                {
                    if (battery_image != null)
                    {
                        battery_image.setVisibility(View.VISIBLE);
                    }

                    if (textview_battery_percentage != null)
                    {
                        textview_battery_percentage.setVisibility(View.VISIBLE);
                    }
                }
            }
            else if(removed)
            {
                if (linear_layout_background != null)
                {
                    linear_layout_background.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_corner_grey));
                }

                if (deviceInfoWhenConnected != null)
                {
                    deviceInfoWhenConnected.setVisibility(View.GONE);
                }

                if (deviceInfoWhenNotConnected != null)
                {
                    deviceInfoWhenNotConnected.setVisibility(View.GONE);
                }

                if (deviceInfoWhenRemoved != null)
                {
                    deviceInfoWhenRemoved.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                if (linear_layout_background != null)
                {
                    linear_layout_background.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_corner_red));
                }

                if (deviceInfoWhenConnected != null)
                {
                    deviceInfoWhenConnected.setVisibility(View.GONE);
                }

                if (deviceInfoWhenNotConnected != null)
                {
                    deviceInfoWhenNotConnected.setVisibility(View.VISIBLE);
                }

                if (deviceInfoWhenRemoved != null)
                {
                    deviceInfoWhenRemoved.setVisibility(View.GONE);
                }
            }
        }
    }


    private void showManualVitalsOnly(boolean show)
    {
        if (show)
        {
            // Manual vitals only - all other rows gone
            tableRowManuallyEnteredOnlySession.setVisibility(View.VISIBLE);
            tableRowSensorDeviceInfo.setVisibility(View.GONE);
        }
        else
        {
            // Normal device indicators - all other rows gone
            tableRowManuallyEnteredOnlySession.setVisibility(View.GONE);
            tableRowSensorDeviceInfo.setVisibility(View.VISIBLE);
        }

        tableRowSoftwareUpdatePending.setVisibility(View.GONE);
    }


    public void showSoftwareUpdatesPending()
    {
        tableRowSoftwareUpdatePending.setVisibility(View.VISIBLE);
        tableRowManuallyEnteredOnlySession.setVisibility(View.GONE);
        tableRowSensorDeviceInfo.setVisibility(View.GONE);
    }


    public void showDeviceInfo(DeviceInfo device_info)
    {
        if(device_info.isDeviceTypePartOfPatientSession())
        {
            setDeviceBatteryLevel(device_info);

            if(!device_info.dummy_data_mode)
            {
                switch (device_info.getActualDeviceConnectionStatus())
                {
                    case NOT_PAIRED:
                    {
                        if(main_activity_interface.isSessionInProgress())
                        {
                            showDeviceInfoSubFunction(device_info, View.VISIBLE);

                            showDeviceConnectionStatus(device_info, false, true);
                        }
                        else
                        {
                            Log.d(TAG, "showDeviceInfo : " + device_info.getSensorTypeAndDeviceTypeAsString() + " is connected but session has not started");
                        }
                    }
                    break;

                    case CONNECTED:
                    case PAIRED:                                            // Pairing should only be for Blood Pressure
                    {
                        if(main_activity_interface.isSessionInProgress())
                        {
                            showDeviceInfoSubFunction(device_info, View.VISIBLE);

                            showDeviceConnectionStatus(device_info, true, false);
                        }
                        else
                        {
                            Log.d(TAG, "showDeviceInfo : " + device_info.getSensorTypeAndDeviceTypeAsString() + " is connected but session has not started");
                        }
                    }
                    break;

                    case SEARCHING:
                    case DISCONNECTED:
                    case UNBONDED:
                    {
                        if(main_activity_interface.isSessionInProgress())
                        {
                            showDeviceInfoSubFunction(device_info, View.VISIBLE);

                            showDeviceConnectionStatus(device_info, false, false);
                        }
                        else
                        {
                            Log.d(TAG, "showDeviceInfo : " + device_info.getSensorTypeAndDeviceTypeAsString() + " is Disconnected but session hasn't started");
                        }
                    }
                    break;

                    case FOUND:
                    {
                        showDeviceInfoSubFunction(device_info, View.INVISIBLE);
                    }
                    break;
                }
            }
            else
            {
                // Dummy data mode is enabled
                showDeviceInfoSubFunction(device_info, View.VISIBLE);

                showDeviceConnectionStatus(device_info, false, false);
            }
        }
        else
        {
            showDeviceInfoSubFunction(device_info, View.INVISIBLE);
        }

        if (main_activity_interface.isSessionInProgress())
        {
            int sensor_devices_used_in_session = 0;

            ArrayList<DeviceInfo> device_info_list = main_activity_interface.getDeviceInfoList();
            for(DeviceInfo di : device_info_list)
            {
                if (di.isDeviceTypeASensorDevice())
                {
                    if (di.isDeviceTypePartOfPatientSession())
                    {
                        sensor_devices_used_in_session++;
                    }
                }
            }

            showManualVitalsOnly(sensor_devices_used_in_session == 0);
        }
        else
        {
            if(main_activity_interface.isSoftwareUpdateAvailable())
            {
                showSoftwareUpdatesPending();
            }
            else
            {
                showManualVitalsOnly(false);
            }
        }
    }


    public void showStartVideoCallButtonIfNeeded()
    {
        if (main_activity_interface.getVideoCallsEnabled())
        {
            tableRowStartVideoCall.setVisibility(View.VISIBLE);
        }
        else
        {
            tableRowStartVideoCall.setVisibility(View.GONE);
        }
    }
}
