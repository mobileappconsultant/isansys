package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.isansys.common.enums.RealTimeServer;

import static com.isansys.pse_isansysportal.UtilityFunctions.convertSecondsToStringForSpinnerCheckFixedEnglishStrings;
import static com.isansys.pse_isansysportal.UtilityFunctions.setSpinnerValue;

public class FragmentFeatureEnable extends FragmentIsansys implements OnClickListener
{
    private boolean radioOffButtonActionTaken = false;

    private CheckBox checkBoxEnableManuallyEnteredVitalSigns;
    private CheckBox checkBoxEnableCsvOutput;
    private CheckBox checkBoxEnablePatientOrientation;
    private CheckBox checkBoxShowNumbersOnBatteryIndicator;
    private CheckBox checkBoxRunDevicesInTestMode;
    private CheckBox checkBoxEnableManufacturingMode;
    private CheckBox checkBoxEnableBackCamera;
    private CheckBox checkBoxSimpleHeartRate;
    private CheckBox checkBoxEnableDeveloperPopup;
    private CheckBox checkBoxEnableVideoCalls;
    private CheckBox checkBoxDisplayTemperatureInFahrenheit;
    private CheckBox checkBoxDisplayWeightInLbs;
    private CheckBox checkBoxShowIpAddressOnWifiPopup;
    private CheckBox checkBoxShowMacOnCheckDevStatus;
    private CheckBox checkBoxUsaMode;
    private CheckBox checkBoxShowLifetouchActivityLevel;
    private CheckBox checkBoxEnableAutoResume;
    private CheckBox checkBoxEnableAutoLogFileUploadToServer;
    private CheckBox checkBoxEnableViewWebPages;

    private CheckBox checkBoxEnableWifiLogging;
    private CheckBox checkBoxEnableGsmLogging;
    private CheckBox checkBoxEnableDatabaseLogging;
    private CheckBox checkBoxEnableServerLogging;
    private CheckBox checkBoxEnableBatteryLogging;

    private Spinner spinnerLifetempMeasurementInterval;
    private Spinner spinnerPatientOrientationMeasurementInterval;
    private Spinner spinnerJsonArraySize;
    private Spinner spinnerRealTimeClientType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.feature_enable, container, false); // Inflate the layout for this fragment

        checkBoxEnableManuallyEnteredVitalSigns = v.findViewById(R.id.checkBoxEnableManuallyEnteredVitalSigns);
        checkBoxEnableManuallyEnteredVitalSigns.setOnClickListener(x -> main_activity_interface.storeEnableManuallyEnteredVitalSigns(((CheckBox) x).isChecked()));

        checkBoxEnableCsvOutput = v.findViewById(R.id.checkBoxEnableCsvOutput);
        checkBoxEnableCsvOutput.setOnClickListener(x -> main_activity_interface.setCsvOutputEnableCheckedByAdmin(((CheckBox) x).isChecked()));

        checkBoxEnablePatientOrientation = v.findViewById(R.id.checkBoxDisplayPatientOrientation);
        checkBoxEnablePatientOrientation.setOnClickListener(x -> main_activity_interface.setEnablePatientOrientation(((CheckBox) x).isChecked()));

        checkBoxEnableAutoLogFileUploadToServer = v.findViewById(R.id.checkBoxEnableAutoLogFileUploadToServer);
        checkBoxEnableAutoLogFileUploadToServer.setOnClickListener(x -> main_activity_interface.setEnableAutoLogFileUploadToServer(((CheckBox) x).isChecked()));

        checkBoxShowNumbersOnBatteryIndicator = v.findViewById(R.id.checkBoxShowNumbersOnBatteryIndicator);
        checkBoxShowNumbersOnBatteryIndicator.setOnClickListener(x -> main_activity_interface.setShowNumbersOnBatteryIndicator(((CheckBox) x).isChecked()));

        checkBoxRunDevicesInTestMode = v.findViewById(R.id.checkBoxRunDevicesInTestMode);
        checkBoxRunDevicesInTestMode.setOnClickListener(x -> main_activity_interface.setRunDevicesInTestMode(((CheckBox) x).isChecked()));

        checkBoxEnableManufacturingMode = v.findViewById(R.id.checkBoxEnableManufacturingMode);
        checkBoxEnableManufacturingMode.setOnClickListener(x -> main_activity_interface.setManufacturingModeEnabled(((CheckBox) x).isChecked()));

        checkBoxEnableBackCamera = v.findViewById(R.id.checkBoxEnableBackCamera);
        checkBoxEnableBackCamera.setOnClickListener(x -> main_activity_interface.setUseBackCameraEnabled(((CheckBox) x).isChecked()));

        checkBoxSimpleHeartRate = v.findViewById(R.id.checkBoxSimpleHeartRate);
        checkBoxSimpleHeartRate.setOnClickListener(x -> main_activity_interface.setSimpleHeartRateEnabled(((CheckBox) x).isChecked()));

        checkBoxEnableDeveloperPopup = v.findViewById(R.id.checkBoxEnableDeveloperPopup);
        checkBoxEnableDeveloperPopup.setOnClickListener(x -> main_activity_interface.setDeveloperPopupEnabled(((CheckBox) x).isChecked()));

        checkBoxEnableVideoCalls = v.findViewById(R.id.checkBoxEnableVideoCalls);
        checkBoxEnableVideoCalls.setOnClickListener(x -> main_activity_interface.setVideoCallsEnabled(((CheckBox) x).isChecked()));

        checkBoxEnableViewWebPages = v.findViewById(R.id.checkBoxEnableViewWebPages);
        checkBoxEnableViewWebPages.setOnClickListener(x -> main_activity_interface.setViewWebPagesEnabled(((CheckBox) x).isChecked()));

        checkBoxDisplayTemperatureInFahrenheit = v.findViewById(R.id.checkBoxDisplayTemperatureInFahrenheit);
        checkBoxDisplayTemperatureInFahrenheit.setOnClickListener(x -> main_activity_interface.setDisplayTemperatureInFahrenheitEnableStatus(((CheckBox) x).isChecked()));

        checkBoxDisplayWeightInLbs = v.findViewById(R.id.checkBoxDisplayWeightInLbs);
        checkBoxDisplayWeightInLbs.setOnClickListener(x -> main_activity_interface.setDisplayWeightInLbsEnableStatus(((CheckBox) x).isChecked()));

        checkBoxShowIpAddressOnWifiPopup = v.findViewById(R.id.checkBoxShowIpAddressOnWifiPopup);
        checkBoxShowIpAddressOnWifiPopup.setOnClickListener(x -> main_activity_interface.setShowIpAddressOnWifiPopupEnabled(((CheckBox) x).isChecked()));

        checkBoxShowMacOnCheckDevStatus = v.findViewById(R.id.checkBoxShowMacAddressOnDeviceStatusScreen);
        checkBoxShowMacOnCheckDevStatus.setOnClickListener(x -> main_activity_interface.setShowMacAddressOnStatus(((CheckBox) x).isChecked()));

        checkBoxUsaMode = v.findViewById(R.id.checkBoxUsaMode);
        checkBoxUsaMode.setOnClickListener(x -> main_activity_interface.setUsaMode(((CheckBox) x).isChecked()));

        checkBoxShowLifetouchActivityLevel = v.findViewById(R.id.checkBoxShowLifetouchActivityLevel);
        checkBoxShowLifetouchActivityLevel.setOnClickListener(x -> main_activity_interface.setShowLifetouchActivityLevel(((CheckBox) x).isChecked()));

        checkBoxEnableAutoResume = v.findViewById(R.id.checkBoxEnableAutoResume);
        checkBoxEnableAutoResume.setOnClickListener(x -> main_activity_interface.setAutoResumeEnabled(((CheckBox) x).isChecked()));

        checkBoxEnableWifiLogging = v.findViewById(R.id.checkBoxEnableWifiLogging);
        checkBoxEnableWifiLogging.setOnClickListener(x -> main_activity_interface.setWifiLoggingEnabled(((CheckBox) x).isChecked()));

        checkBoxEnableGsmLogging = v.findViewById(R.id.checkBoxEnableGsmLogging);
        checkBoxEnableGsmLogging.setOnClickListener(x -> main_activity_interface.setGsmLoggingEnabled(((CheckBox) x).isChecked()));

        checkBoxEnableDatabaseLogging = v.findViewById(R.id.checkBoxEnableDatabaseLogging);
        checkBoxEnableDatabaseLogging.setOnClickListener(x -> main_activity_interface.setDatabaseLoggingEnabled(((CheckBox) x).isChecked()));

        checkBoxEnableServerLogging = v.findViewById(R.id.checkBoxEnableServerLogging);
        checkBoxEnableServerLogging.setOnClickListener(x -> main_activity_interface.setServerLoggingEnabled(((CheckBox) x).isChecked()));

        checkBoxEnableBatteryLogging = v.findViewById(R.id.checkBoxEnableBatteryLogging);
        checkBoxEnableBatteryLogging.setOnClickListener(x -> main_activity_interface.setBatteryLoggingEnabled(((CheckBox) x).isChecked()));

        Button buttonSetDummyServer = v.findViewById(R.id.buttonSetDummyServer);
        buttonSetDummyServer.setOnClickListener(this);

        Button buttonImportDatabase = v.findViewById(R.id.buttonImportDatabase);
        buttonImportDatabase.setOnClickListener(this);

        spinnerRealTimeClientType = v.findViewById(R.id.spinnerRealTimeClientType);
        SpinnerInteractionListener listenerRealTimeClientType = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                String selection = parent.getItemAtPosition(pos).toString();

                if(selection.equals(getString(R.string.connection_type_wamp)))
                {
                    main_activity_interface.setRealTimeClientType(RealTimeServer.WAMP);
                }
                else if(selection.equals(getString(R.string.connection_type_mqtt)))
                {
                    main_activity_interface.setRealTimeClientType(RealTimeServer.MQTT);
                }
                else
                {
                    main_activity_interface.setRealTimeClientType(RealTimeServer.INVALID);
                }
            }
        };
        spinnerRealTimeClientType.setOnTouchListener(listenerRealTimeClientType);
        spinnerRealTimeClientType.setOnItemSelectedListener(listenerRealTimeClientType);

        spinnerLifetempMeasurementInterval = v.findViewById(R.id.spinnerLifetempMeasurementInterval);
        SpinnerInteractionListener listenerLifetempMeasurementInterval = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                String selection = parent.getItemAtPosition(pos).toString();
                String number_part_of_selection = selection.substring(0, selection.indexOf(" "));
                int length_in_units = Integer.parseInt(number_part_of_selection);

                if (selection.contains("second"))
                {
                    // Seconds
                    main_activity_interface.setLifetempMeasurementInterval(length_in_units);
                }
                else
                {
                    // Minute(s)
                    main_activity_interface.setLifetempMeasurementInterval(length_in_units * 60);
                }
            }
        };
        spinnerLifetempMeasurementInterval.setOnTouchListener(listenerLifetempMeasurementInterval);
        spinnerLifetempMeasurementInterval.setOnItemSelectedListener(listenerLifetempMeasurementInterval);


        spinnerPatientOrientationMeasurementInterval = v.findViewById(R.id.spinnerPatientOrientationMeasurementInterval);
        SpinnerInteractionListener listenerPatientOrientationMeasurementInterval = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                String selection = parent.getItemAtPosition(pos).toString();
                String number_part_of_selection = selection.substring(0, selection.indexOf(" "));
                int length_in_units = Integer.parseInt(number_part_of_selection);

                if (selection.contains("second"))
                {
                    // Seconds
                    main_activity_interface.setPatientOrientationMeasurementInterval(length_in_units);
                }
                else
                {
                    // Minute(s)
                    main_activity_interface.setPatientOrientationMeasurementInterval(length_in_units * 60);
                }
            }
        };
        spinnerPatientOrientationMeasurementInterval.setOnTouchListener(listenerPatientOrientationMeasurementInterval);
        spinnerPatientOrientationMeasurementInterval.setOnItemSelectedListener(listenerPatientOrientationMeasurementInterval);

        Button buttonUnitRadioOff = v.findViewById(R.id.buttonUnitRadioOff);
        buttonUnitRadioOff.setOnClickListener(x -> {
            if (x.isPressed() && !radioOffButtonActionTaken)
            {
                main_activity_interface.radioOff((byte)0, (byte)30);
                radioOffButtonActionTaken = true;
            }
            else
            {
                radioOffButtonActionTaken = false;
            }
        });

        spinnerJsonArraySize = v.findViewById(R.id.spinnerJsonArraySize);
        SpinnerInteractionListener listenerJsonArraySize = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                main_activity_interface.setJsonArraySize(Integer.parseInt(parent.getItemAtPosition(pos).toString()));
            }
        };
        spinnerJsonArraySize.setOnTouchListener(listenerJsonArraySize);
        spinnerJsonArraySize.setOnItemSelectedListener(listenerJsonArraySize);

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        
        checkBoxEnableManuallyEnteredVitalSigns.setChecked(main_activity_interface.includeManualVitalSignEntry());
        checkBoxEnableCsvOutput.setChecked(main_activity_interface.getCsvOutputEnableStatus());
        checkBoxEnablePatientOrientation.setChecked(main_activity_interface.getEnablePatientOrientation());
        checkBoxShowNumbersOnBatteryIndicator.setChecked(main_activity_interface.getShowNumbersOnBatteryIndicator());
        checkBoxRunDevicesInTestMode.setChecked(main_activity_interface.getRunDevicesInTestModeEnableStatus());
        checkBoxEnableManufacturingMode.setChecked(main_activity_interface.getManufacturingModeEnabled());
        checkBoxEnableBackCamera.setChecked(main_activity_interface.getUseBackCameraEnabled());
        checkBoxEnableDeveloperPopup.setChecked(main_activity_interface.getDeveloperPopupEnabled());
        checkBoxEnableVideoCalls.setChecked(main_activity_interface.getVideoCallsEnabled());
        checkBoxDisplayTemperatureInFahrenheit.setChecked(main_activity_interface.isShowTemperatureInFahrenheitEnabled());
        checkBoxDisplayWeightInLbs.setChecked(main_activity_interface.isShowWeightInLbsEnabled());
        checkBoxShowIpAddressOnWifiPopup.setChecked(main_activity_interface.getShowIpAddressOnPopupEnabled());
        checkBoxShowMacOnCheckDevStatus.setChecked(main_activity_interface.getShowMacAddressOnStatus());
        checkBoxUsaMode.setChecked(main_activity_interface.getUsaMode());
        checkBoxShowLifetouchActivityLevel.setChecked(main_activity_interface.getShowLifetouchActivityLevel());
        checkBoxEnableAutoResume.setChecked(main_activity_interface.getAutoResumeEnabled());
        checkBoxEnableAutoLogFileUploadToServer.setChecked(main_activity_interface.getEnableAutoLogFileUploadToServer());
        checkBoxEnableWifiLogging.setChecked(main_activity_interface.getWifiLoggingEnabled());
        checkBoxEnableGsmLogging.setChecked(main_activity_interface.getGsmLoggingEnabled());
        checkBoxEnableDatabaseLogging.setChecked(main_activity_interface.getDatabaseLoggingEnabled());
        checkBoxEnableServerLogging.setChecked(main_activity_interface.getServerLoggingEnabled());
        checkBoxEnableBatteryLogging.setChecked(main_activity_interface.getBatteryLoggingEnabled());
        checkBoxEnableViewWebPages.setChecked(main_activity_interface.getViewWebPagesEnabled());

        main_activity_interface.getSimpleHeartRateEnabled();

        main_activity_interface.getJsonArraySize();

        setRealTimeClientType(main_activity_interface.getRealTimeClientType());
        setLifetempMeasurementInterval(main_activity_interface.getLifetempMeasurementInterval());
        setPatientOrientationMeasurementInterval(main_activity_interface.getPatientOrientationMeasurementInterval());
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.buttonSetDummyServer)
        {
            buttonSetDummyServerClick();
        }
        else if (id == R.id.buttonImportDatabase)
        {
            buttonImportDatabaseClick();
        }
    }

    public void setCsvOutputEnableStatus(boolean enabled)
    {
        checkBoxEnableCsvOutput.setChecked(enabled);
    }


    private void buttonSetDummyServerClick()
    {
    	main_activity_interface.setDummyServerDetails();
    }


    private void buttonImportDatabaseClick()
    {
        main_activity_interface.importDatabaseFromAndroidRoot();
    }


    public void setManualVitalSignsEnableStatus(boolean enabled)
    {
        checkBoxEnableManuallyEnteredVitalSigns.setChecked(enabled);
    }


    public void setUseBackCameraEnableStatus(boolean enabled)
    {
        checkBoxEnableBackCamera.setChecked(enabled);
    }


    public void setPatientOrientationEnableStatus(boolean enabled)
    {
        checkBoxEnablePatientOrientation.setChecked(enabled);
    }


    public void setAutoUploadLogFileToServerEnableStatus(boolean enabled)
    {
        checkBoxEnableAutoLogFileUploadToServer.setChecked(enabled);
    }


    public void setShowNumbersOnBatteryIndicatorEnableStatus(boolean enabled)
    {
        checkBoxShowNumbersOnBatteryIndicator.setChecked(enabled);
    }


    public void setRunDevicesInTestModeEnableStatus(boolean enabled)
    {
        checkBoxRunDevicesInTestMode.setChecked(enabled);
    }


    public void setManufacturingModeEnabledStatus(boolean enabled)
    {
        checkBoxEnableManufacturingMode.setChecked(enabled);
    }


    public void setSimpleHeartRateEnabledStatus(boolean enabled)
    {
        checkBoxSimpleHeartRate.setChecked(enabled);
    }


    public void setCheckBoxEnableDeveloperPopup(boolean enabled)
    {
        checkBoxEnableDeveloperPopup.setChecked(enabled);
    }


    public void setVideoCallsEnabledStatus(boolean enabled)
    {
        checkBoxEnableVideoCalls.setChecked(enabled);
    }


    public void setViewWebPagesEnabledStatus(boolean enabled)
    {
        checkBoxEnableViewWebPages.setChecked(enabled);
    }


    public void setDisplayTemperatureInFahrenheitEnabledStatus(boolean enabled)
    {
        checkBoxDisplayTemperatureInFahrenheit.setChecked(enabled);
    }


    public void setDisplayWeightInLbsEnabledStatus(boolean enabled)
    {
        checkBoxDisplayWeightInLbs.setChecked(enabled);
    }


    public void setCheckBoxShowIpAddressOnWifiPopup(boolean enabled)
    {
        checkBoxShowIpAddressOnWifiPopup.setChecked(enabled);
    }


    public void setLifetempMeasurementInterval(int measurement_interval_in_seconds)
    {
        String length_as_string = convertSecondsToStringForSpinnerCheckFixedEnglishStrings(measurement_interval_in_seconds);

        setSpinnerValue(spinnerLifetempMeasurementInterval, length_as_string);
    }


    public void setPatientOrientationMeasurementInterval(int measurement_interval_in_seconds)
    {
        String length_as_string = convertSecondsToStringForSpinnerCheckFixedEnglishStrings(measurement_interval_in_seconds);

        setSpinnerValue(spinnerPatientOrientationMeasurementInterval, length_as_string);
    }


    public void setJsonArraySize(int json_array_size)
    {
        String size = String.valueOf(json_array_size);

        setSpinnerValue(spinnerJsonArraySize, size);
    }


    public void setShowMacAddressEnableStatus(boolean enabled)
    {
        checkBoxShowMacOnCheckDevStatus.setChecked(enabled);
    }


    public void setUsaModeEnableStatus(boolean enabled)
    {
        checkBoxUsaMode.setChecked(enabled);
    }


    public void setShowLifetouchActivityLevelEnableStatus(boolean enabled)
    {
        checkBoxShowLifetouchActivityLevel.setChecked(enabled);
    }


    public void setEnableAutoResume(boolean enabled)
    {
        checkBoxEnableAutoResume.setChecked(enabled);
    }


    public void setEnableWifiLogging(boolean enabled)
    {
        checkBoxEnableWifiLogging.setChecked(enabled);
    }


    public void setEnableGsmLogging(boolean enabled)
    {
        checkBoxEnableGsmLogging.setChecked(enabled);
    }


    public void setEnableDatabaseLogging(boolean enabled)
    {
        checkBoxEnableDatabaseLogging.setChecked(enabled);
    }


    public void setEnableServerLogging(boolean enabled)
    {
        checkBoxEnableServerLogging.setChecked(enabled);
    }


    public void setEnableBatteryLogging(boolean enabled)
    {
        checkBoxEnableBatteryLogging.setChecked(enabled);
    }


    public void setRealTimeClientType(RealTimeServer type)
    {
        String spinner_value = "";

        switch(type)
        {
            case MQTT:
                spinner_value = getString(R.string.connection_type_mqtt);
                break;

            case WAMP:
                spinner_value = getString(R.string.connection_type_wamp);
                break;

            case INVALID:
                return;
        }

        setSpinnerValue(spinnerRealTimeClientType, spinner_value);
    }
}
