package com.isansys.pse_isansysportal;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.isansys.common.FirmwareImage;
import com.isansys.common.ThresholdSet;
import com.isansys.common.enums.SensorType;
import com.isansys.patientgateway.BedInfo;
import com.isansys.patientgateway.WardInfo;

import java.util.ArrayList;

public class FragmentAdminMode extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    private TextView textPatientGatewaySoftwareVersion;
    private TextView textUserInterfaceSoftwareVersion;
    private TextView textGatewayName;
    private TextView textAndroidVersion;

    private final int TAB_POSITION__GATEWAY_SETTINGS = 0;
    private final int TAB_POSITION__SERVER_CONNECTION_SETTINGS = 1;

    private Button buttonAdminTabGatewaySettings;
    private View indicatorAdminTabGatewaySettings;

    private Button buttonAdminTabServerConnectionSettings;
    private View indicatorAdminTabServerConnectionSettings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.admin_mode, container, false); // Inflate the layout for this fragment

	    textPatientGatewaySoftwareVersion = v.findViewById(R.id.textPatientGatewaySoftwareVersion);
        textUserInterfaceSoftwareVersion = v.findViewById(R.id.textUserInterfaceSoftwareVersion);

        textGatewayName = v.findViewById(R.id.textGatewayName);

        textAndroidVersion = v.findViewById(R.id.textAndroidVersion);

        buttonAdminTabGatewaySettings = v.findViewById(R.id.buttonAdminTabGatewaySettings);
        buttonAdminTabGatewaySettings.setOnClickListener(view -> selectTab(TAB_POSITION__GATEWAY_SETTINGS));

        buttonAdminTabServerConnectionSettings = v.findViewById(R.id.buttonAdminTabServerConnectionSettings);
        buttonAdminTabServerConnectionSettings.setOnClickListener(view -> selectTab(TAB_POSITION__SERVER_CONNECTION_SETTINGS));

        indicatorAdminTabServerConnectionSettings = v.findViewById(R.id.indicatorAdminTabServerConnectionSettings);
        indicatorAdminTabGatewaySettings = v.findViewById(R.id.indicatorAdminTabGatewaySettings);


        String gtin_string;

        switch (android.os.Build.MODEL)
        {
            case "SM-T500":     // Samsung Galaxy Tab A7 2020 10.4 inch WIFI
                gtin_string = "GTIN : 5060488680304";
                break;
            case "SM-T505":     // Samsung Galaxy Tab A7 2020 10.4 inch GSM
                gtin_string = "GTIN : 5060488680311";
                break;
            case "SM-T510":     // Samsung Galaxy Tab A 2019 10.1 inch WIFI
                gtin_string = "GTIN : 5060488680250";
                break;
            case "SM-T515":     // Samsung Galaxy Tab A 2019 10.1 inch GSM
                gtin_string = "GTIN : 5060488680267";
                break;
            case "SM-T580":     // Samsung Galaxy Tab A 10.1 inch WIFI
                gtin_string = "GTIN : 5060488680182";
                break;
            case "SM-T585":     // Samsung Galaxy Tab A 10.1 inch GSM
                gtin_string = "GTIN : 5060488680205";
                break;
            case "SM-T805":     // Samsung Galaxy Tab S 10.5 inch GSM
                gtin_string = "GTIN : 5060488680199";
                break;
            case "SM-T800":     // Samsung Galaxy Tab S 10.5 inch WIFI
                gtin_string = "GTIN : 5060488680007";
                break;
            default:
                gtin_string = "GTIN : NOT SET YET";
                break;
        }

        TextView labelGatewayGTIN = v.findViewById(R.id.labelGatewayGTIN);
        labelGatewayGTIN.setText(gtin_string);

        TextView labelGatewayNonCeModeStatement = v.findViewById(R.id.labelGatewayNonCeModeStatement);
        labelGatewayNonCeModeStatement.setVisibility(View.GONE);

        if (main_activity_interface.inNonCeMode())
        {
            labelGatewayGTIN.setVisibility(View.GONE);
            labelGatewayNonCeModeStatement.setVisibility(View.VISIBLE);
        }

        Button exitButton = v.findViewById(R.id.adminExitButton);
        exitButton.setOnClickListener(x -> {
            if (getActivity() != null)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.stringExitDialog);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.stringYes,
                        (dialog, id) -> {
                            // Closing application and removing application services
                            main_activity_interface.adminModeExitPressed();
                            dialog.cancel();
                        });

                builder.setNegativeButton(R.string.stringNo,
                        (dialog, id) -> {
                            // do nothing
                            dialog.cancel();
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else
            {
                Log.e(TAG, "onResume : FragmentAdminMode getActivity() is NULL");
            }
        });

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        
        String patient_gateway_software_revision = " " + main_activity_interface.getPatientGatewaySoftwareVersionNumber();
        String user_interface_software_revision = " " + main_activity_interface.getUserInterfaceSoftwareVersionNumber();

        textPatientGatewaySoftwareVersion.setText(patient_gateway_software_revision);
        textUserInterfaceSoftwareVersion.setText(user_interface_software_revision);

        String gatewayName = " " + BluetoothAdapter.getDefaultAdapter().getName();
        textGatewayName.setText(gatewayName);

        String text = " " + Build.VERSION.RELEASE + "   " + Build.MODEL;
        textAndroidVersion.setText(text);

        if (main_activity_interface.isGatewaySetupComplete())
        {
            selectTab(TAB_POSITION__GATEWAY_SETTINGS);
        }
        else
        {
            selectTab(TAB_POSITION__SERVER_CONNECTION_SETTINGS);
        }
    }

    private void selectTab(int tab)
    {
        switch (tab)
        {
            case TAB_POSITION__GATEWAY_SETTINGS:
                showSelectedTab(buttonAdminTabGatewaySettings, indicatorAdminTabGatewaySettings, true);
                showSelectedTab(buttonAdminTabServerConnectionSettings, indicatorAdminTabServerConnectionSettings, false);

                getChildFragmentManager().beginTransaction().replace(R.id.fragment_admin_page, new FragmentAdminModeGatewaySettings()).commitNow();
                break;

            case TAB_POSITION__SERVER_CONNECTION_SETTINGS:
                showSelectedTab(buttonAdminTabServerConnectionSettings, indicatorAdminTabServerConnectionSettings, true);
                showSelectedTab(buttonAdminTabGatewaySettings, indicatorAdminTabGatewaySettings, false);

                getChildFragmentManager().beginTransaction().replace(R.id.fragment_admin_page, new FragmentAdminModeServerConnection()).commitNow();
                break;
        }
    }

    private void showSelectedTab(Button button, View indicator, boolean selected)
    {
        int TAB_SELECTED_TEXT_SIZE_SP = 24;
        int TAB_NOT_SELECTED_TEXT_SIZE_SP = 16;

        if (selected)
        {
            indicator.setBackgroundColor(ContextCompat.getColor(indicator.getContext(), R.color.red));
            setButtonTextSize(button, TAB_SELECTED_TEXT_SIZE_SP, Typeface.BOLD);
        }
        else
        {
            indicator.setBackgroundColor(ContextCompat.getColor(indicator.getContext(), R.color.blue));
            setButtonTextSize(button, TAB_NOT_SELECTED_TEXT_SIZE_SP, Typeface.NORMAL);
        }
    }

    private void setButtonTextSize(Button button, int textSizeSp, int style)
    {
        button.setTextSize(textSizeSp);
        button.setTypeface(null, style);
        button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.white));
    }

    public void showGatewaysAssignedBedId(String gateways_assigned_bed_id)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.showGatewaysAssignedBedId(gateways_assigned_bed_id);
            }
        }
    }

    public void setServerAddress(String server_address)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setServerAddress(server_address);
            }
        }
    }

    public void showServerRealtimeLinkStatus(boolean status)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.showServerRealtimeLinkStatus(status);
            }
        }
    }

    public void showServerDataSyncStatus(boolean status)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.showServerDataSyncStatus(status);
            }
        }
    }

    public void setServerPort(String server_port)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setServerPort(server_port);
            }
        }
    }

    public void setRealTimeServerPort(String real_time_server_port)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setRealTimeServerPort(real_time_server_port);
            }
        }
    }

    public void showServerPingResult(boolean ping_status, boolean authentication_ok)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.showServerPingResult(ping_status, authentication_ok);
            }
        }
    }

    public void showGetGatewayConfigResult(boolean success)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.showGetGatewayConfigResult(success);
            }
        }
    }

    public void showGetServerConfigurableTextResult(boolean success)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.showGetServerConfigurableTextResult(success);
            }
        }
    }

    public void showGetViewableWebPageFromServerResult(boolean success)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.showGetViewableWebPageFromServerResult(success);
            }
        }
    }

    public void updateEarlyWarningScoreTypeList(ArrayList<ThresholdSet> list)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.updateEarlyWarningScoreTypeList(list);
            }
        }
    }

    public void showForceInstallationCompleteButton(boolean show)
    {
        Fragment fragment =  getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.showForceInstallationCompleteButton(show);
            }
        }
    }

    public void hideUpdateBusyIndicator()
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.hideUpdateBusyIndicator();
            }
        }
    }

    public void hideGettingEwsThresholdsBusyIndicator()
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.hideGettingEwsThresholdsBusyIndicator();
            }
        }
    }

    public void showGetUpdatedFirmwareResult(boolean success, ArrayList<FirmwareImage> firmware_image_list)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.showGetUpdatedFirmwareResult(success, firmware_image_list);
            }
        }
    }

    public void updateWardList(ArrayList<WardInfo> new_ward_info_list)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.updateWardList(new_ward_info_list);
            }
        }
    }

    public void updateBedList(ArrayList<BedInfo> new_bed_info_list)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.updateBedList(new_bed_info_list);
            }
        }
    }

    public void setHttpsEnableStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setHttpsEnableStatus(enabled);
            }
        }
    }

    public void setWebServiceAuthenticationStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setWebServiceAuthenticationStatus(enabled);
            }
        }
    }

    public void setWebServiceEncryptionStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setWebServiceEncryptionStatus(enabled);
            }
        }
    }

    /**
     * Set the textView indicating the time offset of the local clock
     * @param time_offset, String format {"SS.xxx" + "ms"} where SS is Seconds and xxx is milliseconds
     */
    public void setTextViewLocalTimeOffset(String time_offset)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeServerConnection.class)
        {
            FragmentAdminModeServerConnection cast_fragment = (FragmentAdminModeServerConnection) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setTextViewLocalTimeOffset(time_offset);
            }
        }
    }

// End Server Tab

// Start Gateway Tab

    public void setUnpluggedOverlayEnabledStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setUnpluggedOverlayEnabledStatus(enabled);
            }
        }
    }

    public void setLT3KHzSetupModeEnabledStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setLT3KHzSetupModeEnabledStatus(enabled);
            }
        }
    }

    public void setAutoAddEarlyWarningScoresEnabledStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setAutoAddEarlyWarningScoresEnabledStatus(enabled);
            }
        }
    }

    public void setDfuBootloaderEnabledStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setDfuBootloaderEnabledStatus(enabled);
            }
        }
    }

    public void setSpO2SpotMeasurementsEnabledStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setSpO2SpotMeasurementsEnabledStatus(enabled);
            }
        }
    }

    public void setGsmOnlyModeEnabledStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setGsmOnlyModeEnabledStatus(enabled);
            }
        }
    }

    public void setLongTermMeasurementTimeout(SensorType sensorType, int timeout)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setLongTermMeasurementTimeout(sensorType, timeout);
            }
        }
    }

    public void setPredefinedAnnotationsEnabledStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setPredefinedAnnotationsEnabledStatus(enabled);
            }
        }
    }

    public void setDevicePeriodicSetupModeEnableStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setDevicePeriodicSetupModeEnableStatus(enabled);
            }
        }
    }

    public void setNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid(int number)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid(number);
            }
        }
    }

    public void setSetupModeLengthInSeconds(int setup_mode_time_in_seconds)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setSetupModeLengthInSeconds(setup_mode_time_in_seconds);
            }
        }
    }

    public void setDevicePeriodicModePeriodTimeInSeconds(int time_in_seconds)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setDevicePeriodicModePeriodTimeInSeconds(time_in_seconds);
            }
        }
    }

    public void setDevicePeriodicModeActiveTimeInSeconds(int time_in_seconds)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setDevicePeriodicModeActiveTimeInSeconds(time_in_seconds);
            }
        }
    }

    public void setDisplayTimeoutLengthInSeconds(int time_in_seconds, boolean timeout_on_charts)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setDisplayTimeoutLengthInSeconds(time_in_seconds, timeout_on_charts);
            }
        }
    }

    public void setDisplayTimeoutAppliesToPatientVitalsDisplay(boolean patient_vitals_should_timeout)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setDisplayTimeoutAppliesToPatientVitalsDisplay(patient_vitals_should_timeout);
            }
        }
    }

    public void setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(int number)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(number);
            }
        }
    }

    public void setPatientIdCheckEnableStatus(boolean enabled)
    {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_admin_page);

        if(fragment.getClass() ==  FragmentAdminModeGatewaySettings.class)
        {
            FragmentAdminModeGatewaySettings cast_fragment = (FragmentAdminModeGatewaySettings) fragment;
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                cast_fragment.setPatientIdCheckEnableStatus(enabled);
            }
        }
    }
}
