package com.isansys.pse_isansysportal;

import static com.isansys.common.DeviceInfoConstants.CONTINUOUS_SETUP_MODE;
import static com.isansys.pse_isansysportal.UtilityFunctions.convertSecondsToStringForSpinnerCheck;
import static com.isansys.pse_isansysportal.UtilityFunctions.getLengthInSecondsFromSpinnerString;
import static com.isansys.pse_isansysportal.UtilityFunctions.getSpinnerIndex;
import static com.isansys.pse_isansysportal.UtilityFunctions.setSpinnerValue;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.isansys.common.enums.SensorType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FragmentAdminModeGatewaySettings extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    private CheckBox checkBoxDevicePeriodicSetupModeEnabled;

    private LinearLayout linearLayoutDevicePeriodicSetupModeEnabled;

    private Spinner spinnerSetupModeLength;

    private Spinner spinnerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid;

    private Spinner spinnerNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid;


    private CheckBox checkBoxEnableUnpluggedOverlay;
    private CheckBox checkBoxEnableLT3KHzSetupMode;
    private CheckBox checkBoxAutoAddEarlyWarningScores;

    private CheckBox checkBoxPatientNameLookup;
    private CheckBox checkBoxEnablePredefinedAnnotations;
    private CheckBox checkDfuBootloaderEnabled;
    private CheckBox checkSpO2SpotMeasurementsEnabled;
    private CheckBox checkBoxGsmModeOnly;

    private Spinner spinnerLongTermMeasurementTimeoutLengthSpO2;
    private Spinner spinnerLongTermMeasurementTimeoutLengthBloodPressure;
    private Spinner spinnerLongTermMeasurementTimeoutLengthWeight;
    private Spinner spinnerLongTermMeasurementTimeoutLengthThirdPartyTemperature;

    private Spinner spinnerDevicePeriodicModePeriodTimeInSeconds;
    private Spinner spinnerDevicePeriodicModeActiveTimeInSeconds;

    private Spinner spinnerDisplayTimeoutLength;
    private CheckBox checkBoxDisplayTimeoutAppliesToPatientVitalsDisplay;

    private int periodic_setup_mode__period_time_length_in_seconds;
    private int periodic_setup_mode__active_time_length_in_seconds;

    private int patient_vitals_display_timeout_length_in_seconds;

    private Drawable original_spinner;

    private LinearLayout linearLayoutApplyDisplayTimeoutToPatientVitals;

    final HashMap<String, Integer> longTermMeasurementTimeoutStringsAndLinkedValues = new LinkedHashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.admin_mode_gateway_settings, container, false); // Inflate the layout for this fragment

        linearLayoutDevicePeriodicSetupModeEnabled = v.findViewById(R.id.linearLayoutDevicePeriodicSetupModeEnabled);
        linearLayoutApplyDisplayTimeoutToPatientVitals = v.findViewById(R.id.linearLayoutApplyDisplayTimeoutToPatientVitalsDisplay);

        checkBoxDevicePeriodicSetupModeEnabled = v.findViewById(R.id.checkBoxDevicePeriodicSetupModeEnabled);
        checkBoxEnableUnpluggedOverlay = v.findViewById(R.id.checkBoxEnableUnpluggedOverlay);
        checkBoxEnableLT3KHzSetupMode = v.findViewById(R.id.checkBoxEnableLT3KHzSetupMode);
        checkBoxAutoAddEarlyWarningScores = v.findViewById(R.id.checkBoxAutoAddEarlyWarningScores);
        checkBoxPatientNameLookup = v.findViewById(R.id.checkBoxPatientNameLookup);
        checkBoxEnablePredefinedAnnotations = v.findViewById(R.id.checkBoxEnablePredefinedAnnotations);
        checkDfuBootloaderEnabled = v.findViewById(R.id.checkDfuBootloaderEnabled);
        checkSpO2SpotMeasurementsEnabled = v.findViewById(R.id.checkSpO2SpotMeasurementsEnabled);
        checkBoxGsmModeOnly = v.findViewById(R.id.checkBoxGsmModeOnly);
        checkBoxDisplayTimeoutAppliesToPatientVitalsDisplay = v.findViewById(R.id.checkBoxApplyDisplayTimeoutToPatientVitalsDisplay);

        if (main_activity_interface.isTabletGsm())
        {
            // GSM tablet
            checkBoxGsmModeOnly.setButtonDrawable(R.drawable.checkbox);
            checkBoxGsmModeOnly.setEnabled(true);
        }
        else
        {
            // WIFI only tablet
            checkBoxGsmModeOnly.setButtonDrawable(R.drawable.checkbox_disabled);
            checkBoxGsmModeOnly.setEnabled(false);
        }

        // Build up the Hash Map of Strings and their corresponding integer values
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.one_minute), 1);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.two_minutes), 2);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.five_minutes), 5);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.ten_minutes), 10);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.fifteen_minutes), 15);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.twenty_minutes), 20);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.thirty_minutes), 30);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.sixty_minutes), 60);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.ninty_minutes), 90);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.text2Hours), 120);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.text3Hours), 180);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.text4Hours), 240);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.text6Hours), 360);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.text12Hours), 720);
        longTermMeasurementTimeoutStringsAndLinkedValues.put(getResources().getString(R.string.text24Hours), 1440);

        spinnerLongTermMeasurementTimeoutLengthSpO2 = v.findViewById(R.id.spinnerLongTermMeasurementTimeoutLengthSpO2);
        SpinnerInteractionListener listenerLongTermMeasurementTimeoutLengthSpO2 = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                String selectedString = parent.getItemAtPosition(pos).toString();
                Integer timeout = longTermMeasurementTimeoutStringsAndLinkedValues.get(selectedString);
                if (timeout != null)
                {
                    main_activity_interface.setSpO2LongTermMeasurementTimeout(timeout);
                }
            }
        };
        spinnerLongTermMeasurementTimeoutLengthSpO2.setOnTouchListener(listenerLongTermMeasurementTimeoutLengthSpO2);
        spinnerLongTermMeasurementTimeoutLengthSpO2.setOnItemSelectedListener(listenerLongTermMeasurementTimeoutLengthSpO2);


        spinnerLongTermMeasurementTimeoutLengthBloodPressure = v.findViewById(R.id.spinnerLongTermMeasurementTimeoutLengthBloodPressure);
        SpinnerInteractionListener listenerLongTermMeasurementTimeoutLengthBloodPressure = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                String selectedString = parent.getItemAtPosition(pos).toString();
                Integer timeout = longTermMeasurementTimeoutStringsAndLinkedValues.get(selectedString);
                if (timeout != null)
                {
                    main_activity_interface.setBloodPressureLongTermMeasurementTimeout(timeout);
                }
            }
        };
        spinnerLongTermMeasurementTimeoutLengthBloodPressure.setOnTouchListener(listenerLongTermMeasurementTimeoutLengthBloodPressure);
        spinnerLongTermMeasurementTimeoutLengthBloodPressure.setOnItemSelectedListener(listenerLongTermMeasurementTimeoutLengthBloodPressure);


        spinnerLongTermMeasurementTimeoutLengthWeight = v.findViewById(R.id.spinnerLongTermMeasurementTimeoutLengthWeight);
        SpinnerInteractionListener listenerLongTermMeasurementTimeoutLengthWeight = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                String selectedString = parent.getItemAtPosition(pos).toString();
                Integer timeout = longTermMeasurementTimeoutStringsAndLinkedValues.get(selectedString);
                if (timeout != null)
                {
                    main_activity_interface.setWeightLongTermMeasurementTimeout(timeout);
                }
            }
        };
        spinnerLongTermMeasurementTimeoutLengthWeight.setOnTouchListener(listenerLongTermMeasurementTimeoutLengthWeight);
        spinnerLongTermMeasurementTimeoutLengthWeight.setOnItemSelectedListener(listenerLongTermMeasurementTimeoutLengthWeight);


        spinnerLongTermMeasurementTimeoutLengthThirdPartyTemperature = v.findViewById(R.id.spinnerLongTermMeasurementTimeoutLengthThirdPartyTemperature);
        SpinnerInteractionListener listenerLongTermMeasurementTimeoutLengthThirdPartyTemperature = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                String selectedString = parent.getItemAtPosition(pos).toString();
                Integer timeout = longTermMeasurementTimeoutStringsAndLinkedValues.get(selectedString);
                if (timeout != null)
                {
                    main_activity_interface.setThirdPartyTemperatureLongTermMeasurementTimeout(timeout);
                }
            }
        };
        spinnerLongTermMeasurementTimeoutLengthThirdPartyTemperature.setOnTouchListener(listenerLongTermMeasurementTimeoutLengthThirdPartyTemperature);
        spinnerLongTermMeasurementTimeoutLengthThirdPartyTemperature.setOnItemSelectedListener(listenerLongTermMeasurementTimeoutLengthThirdPartyTemperature);


        spinnerSetupModeLength = v.findViewById(R.id.spinnerSetupModeLength);
        SpinnerInteractionListener listenerSetupModeLength = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                Context context = getContext();
                if (context != null)
                {
                    int setup_mode_length_in_seconds = getLengthInSecondsFromSpinnerString(parent.getItemAtPosition(pos).toString(), context);

                    main_activity_interface.setSetupModeLengthInSeconds(setup_mode_length_in_seconds);
                }
            }
        };
        spinnerSetupModeLength.setOnTouchListener(listenerSetupModeLength);
        spinnerSetupModeLength.setOnItemSelectedListener(listenerSetupModeLength);

        spinnerDevicePeriodicModePeriodTimeInSeconds = v.findViewById(R.id.spinnerDevicePeriodicModePeriodTimeInSeconds);
        SpinnerInteractionListener listenerDevicePeriodicModePeriodTimeInSeconds = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                Context context = getContext();
                if (context != null)
                {
                    periodic_setup_mode__period_time_length_in_seconds = getLengthInSecondsFromSpinnerString(parent.getItemAtPosition(pos).toString(), context);

                    if (checkPeriodicSetupModeTimingsValid())
                    {
                        main_activity_interface.setDevicePeriodicModePeriodTimeInSeconds(periodic_setup_mode__period_time_length_in_seconds);
                    }
                    else
                    {
                        // Force Periodic Mode off until the user fixes the issue
                        main_activity_interface.setDevicePeriodicSetupModeEnabled(false);
                    }
                }
            }
        };
        spinnerDevicePeriodicModePeriodTimeInSeconds.setOnTouchListener(listenerDevicePeriodicModePeriodTimeInSeconds);
        spinnerDevicePeriodicModePeriodTimeInSeconds.setOnItemSelectedListener(listenerDevicePeriodicModePeriodTimeInSeconds);

        spinnerDevicePeriodicModeActiveTimeInSeconds = v.findViewById(R.id.spinnerDevicePeriodicModeActiveTimeInSeconds);

        original_spinner = spinnerDevicePeriodicModeActiveTimeInSeconds.getBackground();

        SpinnerInteractionListener listenerDevicePeriodicModeActiveTimeInSeconds = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                Context context = getContext();
                if (context != null)
                {
                    periodic_setup_mode__active_time_length_in_seconds = getLengthInSecondsFromSpinnerString(parent.getItemAtPosition(pos).toString(), context);

                    if (checkPeriodicSetupModeTimingsValid())
                    {
                        main_activity_interface.setDevicePeriodicModeActiveTimeInSeconds(periodic_setup_mode__active_time_length_in_seconds);
                    }
                    else
                    {
                        // Force Periodic Mode off until the user fixes the issue
                        main_activity_interface.setDevicePeriodicSetupModeEnabled(false);
                    }
                }
            }
        };
        spinnerDevicePeriodicModeActiveTimeInSeconds.setOnTouchListener(listenerDevicePeriodicModeActiveTimeInSeconds);
        spinnerDevicePeriodicModeActiveTimeInSeconds.setOnItemSelectedListener(listenerDevicePeriodicModeActiveTimeInSeconds);

        spinnerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid = v.findViewById(R.id.spinnerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid);
        SpinnerInteractionListener listenerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                String selection = parent.getItemAtPosition(pos).toString();
                main_activity_interface.setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(Integer.parseInt(selection));
            }
        };
        spinnerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid.setOnTouchListener(listenerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid);
        spinnerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid.setOnItemSelectedListener(listenerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid);

        spinnerNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid = v.findViewById(R.id.spinnerNoninPulseOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid);
        SpinnerInteractionListener listenerNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                String selection = parent.getItemAtPosition(pos).toString();
                main_activity_interface.setMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(Integer.parseInt(selection));
            }
        };
        spinnerNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid.setOnTouchListener(listenerNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid);
        spinnerNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid.setOnItemSelectedListener(listenerNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid);

        spinnerDisplayTimeoutLength = v.findViewById(R.id.spinnerDisplayTimeoutLength);
        SpinnerInteractionListener listenerDisplayTimeoutLength = new SpinnerInteractionListener()
        {
            @Override
            public void onItemSelectedByUser(AdapterView<?> parent, View view, int pos, long id)
            {
                Context context = getContext();
                if (context != null)
                {
                    // First item in the list (at position 0) is "No timeout", which does not contain a number, so cannot use getLengthInSeconds from its string
                    if (pos == 0)
                    {
                        patient_vitals_display_timeout_length_in_seconds = 0;

                        enableOrDisableAllControlsInLayout(false, linearLayoutApplyDisplayTimeoutToPatientVitals);
                    }
                    else
                    {
                        patient_vitals_display_timeout_length_in_seconds = getLengthInSecondsFromSpinnerString(parent.getItemAtPosition(pos).toString(), context);

                        // Enable "Apply to Patient Vitals" checkbox
                        enableOrDisableAllControlsInLayout(true, linearLayoutApplyDisplayTimeoutToPatientVitals);
                    }

                    main_activity_interface.setDisplayTimeoutLengthInSeconds(patient_vitals_display_timeout_length_in_seconds);
                }
            }
        };
        spinnerDisplayTimeoutLength.setOnTouchListener(listenerDisplayTimeoutLength);
        spinnerDisplayTimeoutLength.setOnItemSelectedListener(listenerDisplayTimeoutLength);

        checkBoxDisplayTimeoutAppliesToPatientVitalsDisplay.setOnClickListener(x -> main_activity_interface.setDisplayTimeoutAppliesToPatientVitalsDisplay(checkBoxDisplayTimeoutAppliesToPatientVitalsDisplay.isChecked()));

        checkBoxDevicePeriodicSetupModeEnabled.setOnClickListener(x -> {
            if (checkPeriodicSetupModeTimingsValid())
            {
                main_activity_interface.setDevicePeriodicSetupModeEnabled(checkBoxDevicePeriodicSetupModeEnabled.isChecked());
            }
            else
            {
                // Do not let the user enable Periodic Setup Mode with invalid options
                checkBoxDevicePeriodicSetupModeEnabled.setChecked(false);
            }
        });


        checkBoxEnableUnpluggedOverlay.setOnClickListener(x -> main_activity_interface.setUnpluggedOverlayEnableStatus(checkBoxEnableUnpluggedOverlay.isChecked()));

        checkBoxEnableLT3KHzSetupMode.setOnClickListener(x -> main_activity_interface.setLT3KHzSetupModeEnableStatus(checkBoxEnableLT3KHzSetupMode.isChecked()));

        checkBoxAutoAddEarlyWarningScores.setOnClickListener(x -> main_activity_interface.setAutoAddEarlyWarningScoreEnableStatus(checkBoxAutoAddEarlyWarningScores.isChecked()));

        checkBoxPatientNameLookup.setOnClickListener(x -> main_activity_interface.setServerPatientNameLookupEnabled(checkBoxPatientNameLookup.isChecked()));

        checkBoxEnablePredefinedAnnotations.setOnClickListener(x -> main_activity_interface.setPredefinedAnnotationEnableStatus(checkBoxEnablePredefinedAnnotations.isChecked()));

        checkDfuBootloaderEnabled.setOnClickListener(x -> main_activity_interface.setDfuBootloaderEnableStatus(checkDfuBootloaderEnabled.isChecked()));

        checkSpO2SpotMeasurementsEnabled.setOnClickListener(x -> main_activity_interface.setSpO2SpotMeasurementsEnableStatus(checkSpO2SpotMeasurementsEnabled.isChecked()));

        checkBoxGsmModeOnly.setOnClickListener(x -> main_activity_interface.setGsmModeOnlyEnabled(checkBoxGsmModeOnly.isChecked()));

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        
        // Load in the Strings that have been made up dynamically
        String[] longTermMeasurementStrings = longTermMeasurementTimeoutStringsAndLinkedValues.keySet().toArray(new String[0]);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, longTermMeasurementStrings);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLongTermMeasurementTimeoutLengthSpO2.setAdapter(spinnerArrayAdapter);
        spinnerLongTermMeasurementTimeoutLengthBloodPressure.setAdapter(spinnerArrayAdapter);
        spinnerLongTermMeasurementTimeoutLengthWeight.setAdapter(spinnerArrayAdapter);
        spinnerLongTermMeasurementTimeoutLengthThirdPartyTemperature.setAdapter(spinnerArrayAdapter);

        // Now that all the UI elements have been configured, its safe to ask the Gateway for current settings
        main_activity_interface.getLifetouchPeriodicSamplingStatus();

        main_activity_interface.getBloodPressureLongTermMeasurementTimeout();
        main_activity_interface.getSpO2LongTermMeasurementTimeout();
        main_activity_interface.getWeightLongTermMeasurementTimeout();
        main_activity_interface.getThirdPartyTemperatureLongTermMeasurementTimeout();

        main_activity_interface.getSetupModeLengthInSeconds();
        main_activity_interface.getDevicePeriodicModePeriodTimeInSeconds();
        main_activity_interface.getDevicePeriodicModeActiveTimeInSeconds();
        main_activity_interface.getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid();
        main_activity_interface.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid();
        main_activity_interface.getUnpluggedOverlayEnabledStatusFromPatientGateway();
        main_activity_interface.getLT3KHzSetupModeEnabledStatusFromPatientGateway();
        main_activity_interface.getAutoAddEarlyWarningScoreEnableStatusFromPatientGateway();
        main_activity_interface.getPatientNameLookupEnabled();
        main_activity_interface.getPredefinedAnnotationEnableStatus();
        main_activity_interface.getDfuBootloaderEnableStatus();
        main_activity_interface.getGsmModeOnlyEnabled();
        main_activity_interface.getDisplayTimeoutLengthInSeconds();
        main_activity_interface.getDisplayTimeoutAppliesToPatientVitalsDisplay();
        main_activity_interface.getSpO2SpotMeasurementsEnableStatus();
    }


    private void enableOrDisableAllControlsInLayout(boolean enable, LinearLayout layout)
    {
        for (int i=0; i<layout.getChildCount(); i++)
        {
            View view = layout.getChildAt(i);

            view.setEnabled(enable);
        }
    }

    public void setDevicePeriodicSetupModeEnableStatus(boolean enabled)
    {
    	checkBoxDevicePeriodicSetupModeEnabled.setChecked(enabled);
    }

    public void setUnpluggedOverlayEnabledStatus(boolean enabled)
    {
        checkBoxEnableUnpluggedOverlay.setChecked(enabled);
    }

    public void setLT3KHzSetupModeEnabledStatus(boolean enabled)
    {
        checkBoxEnableLT3KHzSetupMode.setChecked(enabled);
    }

    public void setAutoAddEarlyWarningScoresEnabledStatus(boolean enabled)
    {
        checkBoxAutoAddEarlyWarningScores.setChecked(enabled);
    }

    public void setDfuBootloaderEnabledStatus(boolean enabled)
    {
        checkDfuBootloaderEnabled.setChecked(enabled);
    }

    public void setSpO2SpotMeasurementsEnabledStatus(boolean enabled)
    {
        checkSpO2SpotMeasurementsEnabled.setChecked(enabled);
    }

    public void setPredefinedAnnotationsEnabledStatus(boolean enabled)
    {
        checkBoxEnablePredefinedAnnotations.setChecked(enabled);
    }

    public void setGsmOnlyModeEnabledStatus(boolean enabled)
    {
        checkBoxGsmModeOnly.setChecked(enabled);
    }

    private void setLongTermMeasurementTimeout(Spinner spinner, int timeout)
    {
        if(spinner != null)
        {
            // Number of drop down items
            int index = 0;

            for (Map.Entry<String, Integer> entry : longTermMeasurementTimeoutStringsAndLinkedValues.entrySet())
            {
                if (entry.getValue() == timeout)
                {
                    spinner.setSelection(index, false);
                }

                index++;
            }
        }
    }

    public void setLongTermMeasurementTimeout(SensorType sensorType, int timeout)
    {
        Log.d(TAG, "setLongTermMeasurementTimeout : " + sensorType + " = " + timeout);

        Spinner spinner = null;

        switch (sensorType)
        {
            case SENSOR_TYPE__SPO2:
                spinner = spinnerLongTermMeasurementTimeoutLengthSpO2;
                break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
                spinner = spinnerLongTermMeasurementTimeoutLengthBloodPressure;
                break;

            case SENSOR_TYPE__WEIGHT_SCALE:
                spinner = spinnerLongTermMeasurementTimeoutLengthWeight;
                break;

            case SENSOR_TYPE__TEMPERATURE:
                spinner = spinnerLongTermMeasurementTimeoutLengthThirdPartyTemperature;
                break;
        }

        if (spinner != null)
        {
            setLongTermMeasurementTimeout(spinner, timeout);
        }
    }

    public void setSetupModeLengthInSeconds(int setup_mode_time_in_seconds)
    {
        String length_as_string = convertSecondsToStringForSpinnerCheck(setup_mode_time_in_seconds, getContext());
        
        if(spinnerSetupModeLength != null)
        {
            spinnerSetupModeLength.setSelection(getSpinnerIndex(spinnerSetupModeLength, length_as_string), false);

            if (setup_mode_time_in_seconds == CONTINUOUS_SETUP_MODE)
            {
                enableOrDisableAllControlsInLayout(false, linearLayoutDevicePeriodicSetupModeEnabled);

                // Disable periodic setup mode if running, as we do when disabling Periodic setup mode checkbox in main_activity_interface.setDevicePeriodicSetupModeEnabled(((CheckBox) v).isChecked());
                // This partially fixes IIT-2066 where user initiated setup mode shows the Setup mode checkbox as the not cancellable "Periodic setup mode"
                main_activity_interface.setDevicePeriodicSetupModeEnabled(false);
            }
            else
            {
                enableOrDisableAllControlsInLayout(true, linearLayoutDevicePeriodicSetupModeEnabled);
            }
        }
    }

    public void setDevicePeriodicModePeriodTimeInSeconds(int time_in_seconds)
    {
        periodic_setup_mode__period_time_length_in_seconds = time_in_seconds;

        String length_as_string = convertSecondsToStringForSpinnerCheck(time_in_seconds, getContext());

        setSpinnerValue(spinnerDevicePeriodicModePeriodTimeInSeconds, length_as_string);
    }

    public void setDevicePeriodicModeActiveTimeInSeconds(int time_in_seconds)
    {
        periodic_setup_mode__active_time_length_in_seconds = time_in_seconds;

        String length_as_string = convertSecondsToStringForSpinnerCheck(time_in_seconds, getContext());

        setSpinnerValue(spinnerDevicePeriodicModeActiveTimeInSeconds, length_as_string);
    }

    public void setNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid(int number)
    {
        String value = String.valueOf(number);

        setSpinnerValue(spinnerNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid, value);
    }

    public void setDisplayTimeoutLengthInSeconds(int time_in_seconds, boolean timeout_on_charts)
    {
        String length_as_string = convertSecondsToStringForSpinnerCheck(time_in_seconds, getContext());

        if (spinnerDisplayTimeoutLength != null)
        {
            // Enable or disable "Apply to Patient Vitals" depending on whether timeout time is > 0
            if (time_in_seconds == 0)
            {
                // "No timeout" does not contain a number, so getSpinnerIndex won't work
                spinnerDisplayTimeoutLength.setSelection(0, false);

                enableOrDisableAllControlsInLayout(false, linearLayoutApplyDisplayTimeoutToPatientVitals);

                setDisplayTimeoutAppliesToPatientVitalsDisplay(false);

                checkBoxDisplayTimeoutAppliesToPatientVitalsDisplay.setButtonDrawable(R.drawable.checkbox_disabled);
            }
            else
            {
                spinnerDisplayTimeoutLength.setSelection(getSpinnerIndex(spinnerDisplayTimeoutLength, length_as_string), false);

                enableOrDisableAllControlsInLayout(true, linearLayoutApplyDisplayTimeoutToPatientVitals);

                setDisplayTimeoutAppliesToPatientVitalsDisplay(timeout_on_charts);

                checkBoxDisplayTimeoutAppliesToPatientVitalsDisplay.setButtonDrawable(R.drawable.checkbox);
            }
        }
    }

    public void setDisplayTimeoutAppliesToPatientVitalsDisplay(boolean patient_vitals_should_timeout)
    {
        if (checkBoxDisplayTimeoutAppliesToPatientVitalsDisplay != null)
        {
            checkBoxDisplayTimeoutAppliesToPatientVitalsDisplay.setChecked(patient_vitals_should_timeout);
        }
    }

    public void setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(int number)
    {
        String value = String.valueOf(number);

        setSpinnerValue(spinnerLifetouchPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid, value);
    }

    private boolean checkPeriodicSetupModeTimingsValid()
    {
        if ((periodic_setup_mode__period_time_length_in_seconds == 0) || (periodic_setup_mode__active_time_length_in_seconds == 0))
        {
            // Means the Admin page is loading.
            return false;
        }

        if (periodic_setup_mode__period_time_length_in_seconds > periodic_setup_mode__active_time_length_in_seconds)
        {
            Log.d(TAG, "checkPeriodicSetupModeTimingsValid : valid : " + periodic_setup_mode__active_time_length_in_seconds + " every " + periodic_setup_mode__period_time_length_in_seconds);

            spinnerDevicePeriodicModeActiveTimeInSeconds.setBackground(original_spinner);

            return true;
        }
        else
        {
            Log.e(TAG, "checkPeriodicSetupModeTimingsValid : INVALID : " + periodic_setup_mode__active_time_length_in_seconds + " every " + periodic_setup_mode__period_time_length_in_seconds);

            spinnerDevicePeriodicModeActiveTimeInSeconds.setBackgroundColor(Color.RED);

            return false;
        }
    }

    public void setPatientIdCheckEnableStatus(boolean enabled)
    {
        checkBoxPatientNameLookup.setChecked(enabled);
    }
}
