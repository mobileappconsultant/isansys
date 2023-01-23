package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.isansys.common.enums.SensorType;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;

import org.jetbrains.annotations.NotNull;

public class FragmentPatientVitalsDisplayLifetouchOptions extends FragmentIsansys
{
    private CheckedTextView checkBoxLifetouchSetupMode = null;
    private CheckedTextView checkBoxLifetouchPoincare = null;
    private CheckedTextView checkBoxLifetouchRawAccelerometerMode = null;
    private CheckedTextView checkBoxLifetouchSetupModeDisabled = null;
    private CheckedTextView checkBoxLifetouchRawAccelerometerModeDisabled = null;

    private DeviceInfo device_info;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.patient_vitals_display__lifetouch_options, container, false);

        checkBoxLifetouchSetupMode = v.findViewById(R.id.checkBoxLifetouchOptionsSetupMode);
        createLifetouchSetupModeCheckBoxOnCheckedChangeListener();
        checkBoxLifetouchSetupModeDisabled = v.findViewById(R.id.checkBoxLifetouchOptionsSetupModeDisabled);

        checkBoxLifetouchPoincare = v.findViewById(R.id.checkBoxLifetouchOptionsHeartRatePoincare);
        createPoincareCheckBoxOnCheckedChangeListener();

        checkBoxLifetouchRawAccelerometerMode = v.findViewById(R.id.checkBoxLifetouchOptionsRawAccelerometerMode);
        createLifetouchRawAccelerometerCheckBoxOnCheckedChangeListener();
        checkBoxLifetouchRawAccelerometerModeDisabled = v.findViewById(R.id.checkBoxLifetouchOptionsRawAccelerometerModeDisabled);

        device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);

        // Disable Setup & Accelerometer Modes option if Leads Off
        if (device_info.isDeviceOffBody())
        {
            disableSetupAndAccelerometerButtons();
        }

        // Additionally, disable Setup & Accelerometer Modes option if under server control
        if (device_info.isDeviceUnderServerControl())
        {
            disableSetupAndAccelerometerButtons();
        }

        // Enable & check Accelerometer Mode option if Leads Off & in Accelerometer mode to allow cancellation of Accelerometer mode from Options menu
        if (device_info.isDeviceOffBody() && device_info.isDeviceInRawAccelerometerMode())
        {
            enableAndCheckAccelerometerButton();
        }

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        device_info = main_activity_interface.getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);

        setSetupModeChecked(device_info.isDeviceInSetupMode());
        setRawAccelerometerModeChecked(device_info.isDeviceInRawAccelerometerMode());
    }


    private void disableSetupAndAccelerometerButtons()
    {
        checkBoxLifetouchSetupMode.setVisibility(View.GONE);
        checkBoxLifetouchSetupModeDisabled.setVisibility(View.VISIBLE);

        checkBoxLifetouchRawAccelerometerMode.setVisibility(View.GONE);
        checkBoxLifetouchRawAccelerometerModeDisabled.setVisibility(View.VISIBLE);
    }


    private void enableAndCheckAccelerometerButton()
    {
        checkBoxLifetouchRawAccelerometerModeDisabled.setVisibility(View.GONE);
        checkBoxLifetouchRawAccelerometerMode.setVisibility(View.VISIBLE);
        checkBoxLifetouchRawAccelerometerMode.setChecked(true);
    }


    private void setSetupModeChecked(boolean checked)
    {
        checkBoxLifetouchSetupMode.setChecked(checked);
    }


    private void setRawAccelerometerModeChecked(boolean checked)
    {
        checkBoxLifetouchRawAccelerometerMode.setChecked(checked);
    }

    private void createLifetouchSetupModeCheckBoxOnCheckedChangeListener()
    {
        checkBoxLifetouchSetupMode.setOnClickListener(v -> {
            ((CheckedTextView) v).toggle();
            main_activity_interface.showLifetouchSetupMode(checkBoxLifetouchSetupMode.isChecked());
        });
    }


    private void createPoincareCheckBoxOnCheckedChangeListener()
    {
        checkBoxLifetouchPoincare.setOnClickListener(v -> {
            main_activity_interface.showLifetouchPoincare(true);  // will no longer sendStopLifetouchSetupModeCommand() or sendStopLifetouchRawAccelerometerModeCommand so returning from Poincare will retain Setup/Motion modes
        });
    }


    private void createLifetouchRawAccelerometerCheckBoxOnCheckedChangeListener()
    {
        checkBoxLifetouchRawAccelerometerMode.setOnClickListener(v -> {
            ((CheckedTextView) v).toggle();
            main_activity_interface.showLifetouchRawAccelerometer(checkBoxLifetouchRawAccelerometerMode.isChecked());
        });
    }

}
