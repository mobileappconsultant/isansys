package com.isansys.pse_isansysportal;

import static com.isansys.common.DeviceInfoConstants.INVALID_FIRMWARE_VERSION;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.isansys.common.FirmwareImage;
import com.isansys.common.ThresholdSet;
import com.isansys.common.enums.RealTimeServer;
import com.isansys.patientgateway.BedInfo;
import com.isansys.patientgateway.WardInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentAdminModeServerConnection extends FragmentIsansys implements OnClickListener
{
    private final String TAG = this.getClass().getSimpleName();

    private CheckBox checkBoxRealTimeLinkEnabled;
    private CheckBox checkBoxServerSyncEnabled;

    private TextView textAdminModeGatewaysAssignedBedId;
    
    private EditText editServerAddress;
    private EditText editServerPort;
    private EditText editRealTimeServerPort;

    private LinearLayout linearLayoutAdminServerPingStatus;
    private LinearLayout linearLayoutAdminServerPingStatusColour;

    private Spinner spinnerWardList;
    private Spinner spinnerBedList;

    private CheckBox checkBoxUseHttps;
    private CheckBox checkBoxUseWebServiceAuthentication;
    private CheckBox checkBoxUseWebServiceEncryption;

    private Button buttonSetBedDetails;

    private TextView textViewTimeDrift;

    private Button buttonSetServer;
    private Button buttonSetServerPort;
    private Button buttonSetRealTimeServerPort;

    private Button buttonGetDefaultEarlyWarningScoringTypesFromServer;

    private Button buttonForceInstallationComplete;
    
    private Handler handler;
    
    private ArrayList<BedInfo> bed_info_list = null;
    private ArrayList<WardInfo> ward_info_list = null;
    
    private WardInfo selected_ward_info = new WardInfo();
    private final BedInfo selected_bed_info = new BedInfo();
    
    private LinearLayout linearLayoutGetDefaultEarlyWaringScoreTypesFromServer;
    private TextView textReceivedDefaultEarlyWarningScoringTypes;

    private LinearLayout linearLayoutGettingUpdatesFromServer;
    private TextView textReceivedUpdateFirmwareVersions;

    private String NOT_SET_YET;

    private View viewReceivedGatewayConfig;
    private View viewReceivedServerConfigurableTextFromServer;
    private View viewReceivedWebpageFromServer;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.admin_mode_server_connection, container, false); // Inflate the layout for this fragment

        RealTimeServer realtime_connection_type = main_activity_interface.getRealTimeClientType();

        linearLayoutAdminServerPingStatusColour = v.findViewById(R.id.linearLayoutAdminServerPingStatusColour);
        
        linearLayoutAdminServerPingStatus = v.findViewById(R.id.linearLayoutAdminServerPingStatus);
        linearLayoutAdminServerPingStatus.setVisibility(View.INVISIBLE);

        textAdminModeGatewaysAssignedBedId = v.findViewById(R.id.textAdminModeGatewaysAssignedBedId);
        
        checkBoxRealTimeLinkEnabled = v.findViewById(R.id.checkBoxEnableRealTimeLink);
        checkBoxServerSyncEnabled = v.findViewById(R.id.checkBoxServerDataSyncEnabled);
        checkBoxUseHttps = v.findViewById(R.id.checkBoxUseHttps);
        checkBoxUseWebServiceAuthentication = v.findViewById(R.id.checkBoxUseWebServiceAuthentication);
        checkBoxUseWebServiceEncryption = v.findViewById(R.id.checkBoxUseWebServiceEncryption);

        ImageButton buttonChangeWifi = v.findViewById(R.id.buttonChangeWifi);
        buttonChangeWifi.setOnClickListener(this);

        Button buttonEmptyLocalDatabase = v.findViewById(R.id.buttonEmptyLocalDatabase);
        buttonEmptyLocalDatabase.setOnClickListener(this);

        Button buttonDeleteEarlyWarningScoreThresholdSets = v.findViewById(R.id.buttonDeleteEarlyWarningScoreThresholdSets);
        buttonDeleteEarlyWarningScoreThresholdSets.setOnClickListener(this);

        Button buttonExportLocalDatabase = v.findViewById(R.id.buttonExportLocalDatabase);
        buttonExportLocalDatabase.setOnClickListener(this);

        Button buttonDeleteOldExportedDatabases = v.findViewById(R.id.buttonDeleteOldExportedDatabases);
        buttonDeleteOldExportedDatabases.setOnClickListener(this);

        Button buttonRestartInstallationWizard = v.findViewById(R.id.buttonRestartInstallationWizard);
        buttonRestartInstallationWizard.setOnClickListener(this);

        Button buttonCheckForUpdatedFirmware = v.findViewById(R.id.buttonCheckForUpdatedFirmware);
        buttonCheckForUpdatedFirmware.setOnClickListener(this);

        buttonForceInstallationComplete = v.findViewById(R.id.buttonForceInstallationComplete);
        buttonForceInstallationComplete.setOnClickListener(this);
        buttonForceInstallationComplete.setVisibility(View.INVISIBLE);

        Button buttonTestServerLink = v.findViewById(R.id.buttonTestServerLink);
        buttonTestServerLink.setOnClickListener(this);
        
        editServerAddress = v.findViewById(R.id.editServerAddress);

        buttonSetServer = v.findViewById(R.id.buttonSetServer);
        buttonSetServer.setOnClickListener(this);

        editServerPort = v.findViewById(R.id.editServerPort);

        buttonSetServerPort = v.findViewById(R.id.buttonSetServerPort);
        buttonSetServerPort.setOnClickListener(this);

        TextView labelSetRealTimeServerPort = v.findViewById(R.id.labelSetRealTimeServerPort);
        editRealTimeServerPort = v.findViewById(R.id.editRealTimeServerPort);

        buttonSetRealTimeServerPort = v.findViewById(R.id.buttonSetRealTimeServerPort);
        buttonSetRealTimeServerPort.setOnClickListener(this);

        Button buttonGetWardsAndBedsFromServer = v.findViewById(R.id.buttonGetWardsAndBedsFromServer);
        buttonGetWardsAndBedsFromServer.setOnClickListener(this);

        buttonGetDefaultEarlyWarningScoringTypesFromServer = v.findViewById(R.id.buttonGetDefaultEarlyWarningScoringTypesFromServer);
        buttonGetDefaultEarlyWarningScoringTypesFromServer.setOnClickListener(this);

        linearLayoutGetDefaultEarlyWaringScoreTypesFromServer = v.findViewById(R.id.linearLayoutGetDefaultEarlyWaringScoreTypesFromServer);
        linearLayoutGetDefaultEarlyWaringScoreTypesFromServer.setVisibility(View.GONE);

        textReceivedDefaultEarlyWarningScoringTypes = v.findViewById(R.id.textReceivedDefaultEarlyWarningScoringTypes);
        textReceivedDefaultEarlyWarningScoringTypes.setText(R.string.blank_string);

        Button buttonGetGatewayConfigFromServer = v.findViewById(R.id.buttonGetGatewayConfigFromServer);
        buttonGetGatewayConfigFromServer.setOnClickListener(this);

        linearLayoutGettingUpdatesFromServer = v.findViewById(R.id.linearLayoutGettingUpdatesFromServer);
        linearLayoutGettingUpdatesFromServer.setVisibility(View.GONE);

        viewReceivedGatewayConfig = v.findViewById(R.id.viewReceivedGatewayConfig);
        viewReceivedGatewayConfig.setVisibility(View.INVISIBLE);

        Button buttonGetViewableWebpagesFromServer = v.findViewById(R.id.buttonGetViewableWebpagesFromServer);
        buttonGetViewableWebpagesFromServer.setOnClickListener(this);

        viewReceivedWebpageFromServer = v.findViewById(R.id.viewReceivedWebpageFromServer);
        viewReceivedWebpageFromServer.setVisibility(View.INVISIBLE);

        viewReceivedServerConfigurableTextFromServer = v.findViewById(R.id.viewReceivedServerConfigurableTextFromServer);
        viewReceivedServerConfigurableTextFromServer.setVisibility(View.INVISIBLE);

        Button buttonGetServerConfigurableTextFromServer = v.findViewById(R.id.buttonGetServerConfigurableTextFromServer);
        buttonGetServerConfigurableTextFromServer.setOnClickListener(this);

        textReceivedUpdateFirmwareVersions = v.findViewById(R.id.textReceivedUpdateFirmwareVersions);
        textReceivedUpdateFirmwareVersions.setText(R.string.blank_string);

        Button buttonNtpTimeSync = v.findViewById(R.id.ButtonNTPSync);
        buttonNtpTimeSync.setOnClickListener(this);
        textViewTimeDrift = v.findViewById(R.id.textViewTimeDriftValue);
        textViewTimeDrift.setText(R.string.time_drift_initial);

        Button buttonStartLogCat = v.findViewById(R.id.ButtonLogCat);

        NOT_SET_YET = getResources().getString(R.string.not_set_yet);

        if(realtime_connection_type == RealTimeServer.WAMP)
        {
            labelSetRealTimeServerPort.setText(R.string.set_wamp_server_port);
            editRealTimeServerPort.setHint(R.string.enter_wamp_server_port);
            buttonSetRealTimeServerPort.setText(R.string.set_wamp_server_port);
        }
        else if(realtime_connection_type == RealTimeServer.MQTT)
        {
            labelSetRealTimeServerPort.setText(R.string.set_mqtt_server_port);
            editRealTimeServerPort.setHint(R.string.enter_mqtt_server_port);
            buttonSetRealTimeServerPort.setText(R.string.set_mqtt_server_port);
        }

        spinnerWardList = v.findViewById(R.id.spinnerWardList);
        spinnerWardList.setVisibility(View.INVISIBLE);
        spinnerWardList.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                Log.d(TAG, "onItemSelected = " + parent.getItemAtPosition(pos).toString() + " : Pos = " + pos);

                if (ward_info_list != null)
                {
                    selected_ward_info = ward_info_list.get(pos);

                    Log.d(TAG, "ward_details_id = " + selected_ward_info.ward_details_id + ". ward_name = " + selected_ward_info.ward_name);

                    if (selected_ward_info != null)
                    {
                        refineBedListForThisWardDetailsId(selected_ward_info.ward_details_id);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                Log.d(TAG, "onNothingSelected");
            }
        });

        spinnerBedList = v.findViewById(R.id.spinnerBedList);
        spinnerBedList.setVisibility(View.INVISIBLE);
        spinnerBedList.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                String item_selected = parent.getItemAtPosition(pos).toString();

                if (bed_info_list != null)
                {
                    // Go through the list of beds (all wards) to find the record matching this one (cant use Pos as this is the position in this ward only)
                    for (BedInfo this_bed_info : bed_info_list)
                    {
                        if ((this_bed_info.bed_name.equals(item_selected)) && (this_bed_info.by_ward_id == selected_ward_info.ward_details_id))
                        {
                            selected_bed_info.bed_details_id = this_bed_info.bed_details_id;
                            selected_bed_info.bed_name = this_bed_info.bed_name;

                            Log.d(TAG, "bed_details_id = " + this_bed_info.bed_details_id + ". by_ward_id = " + this_bed_info.by_ward_id + ". bed_name = " + this_bed_info.bed_name);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                Log.d(TAG, "onNothingSelected");
            }
        });

        checkBoxUseHttps.setOnClickListener(x -> main_activity_interface.useHttpsForServerConnection(checkBoxUseHttps.isChecked()));

        checkBoxUseWebServiceAuthentication.setOnClickListener(x -> main_activity_interface.setWebServiceAuthenticationEnabled(checkBoxUseWebServiceAuthentication.isChecked()));

        checkBoxUseWebServiceEncryption.setOnClickListener(x -> main_activity_interface.setWebServiceEncryptionEnabled(checkBoxUseWebServiceEncryption.isChecked()));

        checkBoxRealTimeLinkEnabled.setOnClickListener(x -> {
            boolean enabled = checkBoxRealTimeLinkEnabled.isChecked();
            main_activity_interface.enableRealTimeLink(enabled);
        });

        checkBoxServerSyncEnabled.setOnClickListener(x -> {
            boolean enabled = checkBoxServerSyncEnabled.isChecked();
            main_activity_interface.enableServerDataSync(enabled);
        });

        buttonSetBedDetails = v.findViewById(R.id.buttonSetBedDetails);
        buttonSetBedDetails.setOnClickListener(this);

        buttonNtpTimeSync = v.findViewById(R.id.ButtonNTPSync);
        buttonNtpTimeSync.setOnClickListener(x -> main_activity_interface.sendTimeSyncCommand());
        textViewTimeDrift = v.findViewById(R.id.textViewTimeDriftValue);
        textViewTimeDrift.setText(R.string.time_drift_initial);

        if(buttonStartLogCat == null)
        {
            buttonStartLogCat = v.findViewById(R.id.ButtonLogCat);
        }

        buttonStartLogCat.setOnClickListener(x -> main_activity_interface.showLogCat(true));

        editServerAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                main_activity_interface.touchEventSoResetTimers();

                Activity activity = getActivity();
                if (activity != null)
                {
                    if ((s.toString().isEmpty() || s.toString().contains(" ")))
                    {
                        buttonSetServer.setEnabled(false);
                        buttonSetServer.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                    }
                    else
                    {
                        buttonSetServer.setEnabled(true);

                        // Do not let the user try and enter text in the Server controls if a Patient Session is in progress
                        // Purposefully not disabling the button. If iy is grey and the user presses on it, the normal "You must exit the session first" popup will show
                        if(main_activity_interface.isSessionInProgress())
                        {
                            editServerAddress.setEnabled(false);
                            buttonSetServer.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                        }
                        else
                        {
                            editServerAddress.setEnabled(true);
                            buttonSetServer.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_blue));
                        }
                    }
                }
            }
        });

        editServerPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                main_activity_interface.touchEventSoResetTimers();

                Activity activity = getActivity();
                if (activity != null)
                {
                    if ((s.toString().isEmpty() || s.toString().contains(" ")))
                    {
                        buttonSetServerPort.setEnabled(false);
                        buttonSetServerPort.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                    }
                    else
                    {
                        buttonSetServerPort.setEnabled(true);

                        // Do not let the user try and enter text in the Server controls if a Patient Session is in progress
                        // Purposefully not disabling the button. If iy is grey and the user presses on it, the normal "You must exit the session first" popup will show
                        if(main_activity_interface.isSessionInProgress())
                        {
                            editServerPort.setEnabled(false);
                            buttonSetServerPort.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                        }
                        else
                        {
                            editServerPort.setEnabled(true);
                            buttonSetServerPort.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_blue));
                        }
                    }
                }
            }
        });

        editRealTimeServerPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                main_activity_interface.touchEventSoResetTimers();

                Activity activity = getActivity();
                if (activity != null)
                {
                    if ((s.toString().isEmpty() || s.toString().contains(" ")))
                    {
                        buttonSetRealTimeServerPort.setEnabled(false);
                        buttonSetRealTimeServerPort.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                    }
                    else
                    {
                        buttonSetRealTimeServerPort.setEnabled(true);

                        // Do not let the user try and enter text in the Server controls if a Patient Session is in progress
                        // Purposefully not disabling the button. If iy is grey and the user presses on it, the normal "You must exit the session first" popup will show
                        if(main_activity_interface.isSessionInProgress())
                        {
                            buttonSetRealTimeServerPort.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                            editRealTimeServerPort.setEnabled(false);
                        }
                        else
                        {
                            buttonSetRealTimeServerPort.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_blue));
                            editRealTimeServerPort.setEnabled(true);
                        }
                    }
                }
            }
        });

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        handler = new Handler();

        // Now that all the UI elements have been configured, its safe to ask the Gateway for current settings
        main_activity_interface.getServerAddress();
        main_activity_interface.getWebServiceAuthenticationEnabledStatus();
        main_activity_interface.getWebServiceEncryptionEnabledStatus();
        main_activity_interface.getHttpsEnableStatus();
        main_activity_interface.getServerPort();
        main_activity_interface.getRealTimeServerPort();
        main_activity_interface.getGatewaysAssignedBedDetails();
        main_activity_interface.getServerSyncEnableStatus();
        main_activity_interface.getRealTimeLinkEnableStatus();

        updateEarlyWarningScoreTypeList(main_activity_interface.getEarlyWarningScoreThresholdSets());
    }

    @Override
    public void onDestroyView()
    {
        if (editServerAddress != null)
        {
            main_activity_interface.dismissOnScreenKeyboard(getContext(), editServerAddress);
        }

        if (editServerPort != null)
        {
            main_activity_interface.dismissOnScreenKeyboard(getContext(), editServerPort);
        }

        if (editRealTimeServerPort != null)
        {
            main_activity_interface.dismissOnScreenKeyboard(getContext(), editRealTimeServerPort);
        }

        super.onDestroyView();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.buttonSetServer)
        {
                if(main_activity_interface.isSessionInProgress())
                {
                    showYouMustEndSessionPopup();
                }
                else
                {
                    showChangeServerAddressPopup();
                }
        }
        else if (id == R.id.buttonSetServerPort)
        {
            if(main_activity_interface.isSessionInProgress())
            {
                showYouMustEndSessionPopup();
            }
            else
            {
                try
                {
                    // If the user has not entered a valid number, then an exception will happen preventing this invalid port being sent to the Gateway
                    Integer.parseInt(editServerPort.getText().toString());

                    showChangeServerPortPopup();
                }
                catch (NumberFormatException e)
                {
                    showInvalidNumberEnteredPopup();
                }
            }
        }
        else if (id == R.id.buttonSetRealTimeServerPort)
        {
            if(main_activity_interface.isSessionInProgress())
            {
                showYouMustEndSessionPopup();
            }
            else
            {
                try
                {
                    // If the user has not entered a valid number, then an exception will happen preventing this invalid port being sent to the Gateway
                    Integer.parseInt(editRealTimeServerPort.getText().toString());

                    main_activity_interface.setRealTimeServerPort(editRealTimeServerPort.getText().toString());

                    main_activity_interface.dismissOnScreenKeyboard(getContext(), editRealTimeServerPort);
                }
                catch (NumberFormatException e)
                {
                    showInvalidNumberEnteredPopup();
                }
            }
        }
        else if (id == R.id.buttonTestServerLink)
        {
            main_activity_interface.testServerLink();
        }
        else if (id == R.id.buttonGetWardsAndBedsFromServer)
        {
            main_activity_interface.getWardsAndBedsFromServer();
        }
        else if (id == R.id.buttonSetBedDetails)
        {
            if(main_activity_interface.isSessionInProgress())
            {
                showYouMustEndSessionPopup();
            }
            else if((!selected_bed_info.getInitialised()) || (!selected_ward_info.getInitialised()))
            {
                Log.d(TAG, "buttonSetBedDetails pressed, but bed or ward not set");
            }
            else
            {
                buttonSetBedDetails.clearAnimation();

                main_activity_interface.setGatewaysAssignedBedDetails(String.valueOf(selected_bed_info.bed_details_id), selected_ward_info.ward_name, selected_bed_info.bed_name);
            }
        }
        else if (id == R.id.buttonEmptyLocalDatabase)
        {
            if(main_activity_interface.isSessionInProgress())
            {
                showYouMustEndSessionPopup();
            }
            else
            {
                main_activity_interface.emptyLocalDatabase();
            }
        }
        else if (id == R.id.buttonDeleteEarlyWarningScoreThresholdSets)
        {
            if(main_activity_interface.isSessionInProgress())
            {
                showYouMustEndSessionPopup();
            }
            else
            {
                emptyLocalDatabaseIncludingThresholds();
            }
        }
        else if (id == R.id.buttonExportLocalDatabase)
        {
            main_activity_interface.exportLocalDatabaseToAndroidRoot();
        }
        else if (id == R.id.buttonDeleteOldExportedDatabases)
        {
            main_activity_interface.deleteOldExportedDatabases();
        }
        else if (id == R.id.buttonRestartInstallationWizard)
        {
            if(main_activity_interface.isSessionInProgress())
            {
                showYouMustEndSessionPopup();
            }
            else
            {
                main_activity_interface.restartSetupWizard();
            }
        }
        else if (id == R.id.buttonCheckForUpdatedFirmware)
        {
            linearLayoutGettingUpdatesFromServer.setVisibility(View.VISIBLE);
            textReceivedUpdateFirmwareVersions.setVisibility(View.GONE);

            main_activity_interface.forceCheckForLatestFirmwareImagesFromServer();
        }
        else if (id == R.id.buttonForceInstallationComplete)
        {
            main_activity_interface.forceInstallationComplete();
            main_activity_interface.lockButtonPressed();
        }
        else if (id == R.id.buttonGetDefaultEarlyWarningScoringTypesFromServer)
        {
            if(main_activity_interface.isSessionInProgress())
            {
                showYouMustEndSessionPopup();
            }
            else
            {
                linearLayoutGetDefaultEarlyWaringScoreTypesFromServer.setVisibility(View.VISIBLE);
                textReceivedDefaultEarlyWarningScoringTypes.setVisibility(View.GONE);

                main_activity_interface.getDefaultEarlyWarningScoreTypesFromServer();
            }
        }
        else if (id == R.id.buttonGetGatewayConfigFromServer)
        {
            if(main_activity_interface.isSessionInProgress())
            {
                showYouMustEndSessionPopup();
            }
            else
            {
                main_activity_interface.getGatewayConfigFromServer();
            }
        }
        else if (id == R.id.buttonGetViewableWebpagesFromServer)
        {
            main_activity_interface.getViewableWebPagesFromServer();
        }
        else if (id == R.id.buttonGetServerConfigurableTextFromServer)
        {
            main_activity_interface.getServerConfigurableTextFromServer();
        }
        else if(id == R.id.buttonChangeWifi)
        {
            if (!main_activity_interface.getGsmOnlyModeFeatureEnabled())
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                    startActivityForResult(panelIntent, 545);
                }
                else
                {
                    main_activity_interface.showWifiStatusPopup(true);
                    main_activity_interface.sendCommandToGetWifiStatus();
                }
            }
        }
    }

    private void emptyLocalDatabaseIncludingThresholds()
    {
        main_activity_interface.emptyLocalDatabaseIncludingEwsThresholdSets();

        // Remove the Server Config Text green dot
        viewReceivedServerConfigurableTextFromServer.setVisibility(View.INVISIBLE);
    }

    public void showServerDataSyncStatus(boolean status)
    {
        checkBoxServerSyncEnabled.setChecked(status);
    }

    public void showServerRealtimeLinkStatus(boolean status)
    {
        checkBoxRealTimeLinkEnabled.setChecked(status);

        if (status)
        {
            // Allow interaction with server syncing checkbox
            checkBoxServerSyncEnabled.setButtonDrawable(R.drawable.checkbox);
            checkBoxServerSyncEnabled.setEnabled(true);
        }
        else
        {
            // Disable interaction with server syncing checkbox
            checkBoxServerSyncEnabled.setButtonDrawable(R.drawable.checkbox_disabled);
            checkBoxServerSyncEnabled.setEnabled(false);
        }
    }

    public void setServerAddress(String server_address)
    {
        editServerAddress.setText(server_address);
    }

    public void setServerPort(String server_port)
    {
        editServerPort.setText(server_port);
    }

    public void setRealTimeServerPort(String real_time_server_port)
    {
        editRealTimeServerPort.setText(real_time_server_port);
    }

    public void showServerPingResult(boolean ping_status, boolean authentication_ok)
    {
        if (ping_status)
        {
            linearLayoutAdminServerPingStatusColour.setBackgroundColor(Color.GREEN);
        }
        else
        {
        	if (authentication_ok)
        	{
                linearLayoutAdminServerPingStatusColour.setBackgroundColor(Color.RED);
            }
        	else
        	{
                linearLayoutAdminServerPingStatusColour.setBackgroundColor(Color.MAGENTA);
        	}
        }
        
        linearLayoutAdminServerPingStatus.setVisibility(View.VISIBLE);
        
        // Hide the indicator after 2 seconds
        handler.postDelayed(() -> linearLayoutAdminServerPingStatus.setVisibility(View.INVISIBLE), 2000);
    }

    public void updateWardList(ArrayList<WardInfo> new_ward_info_list)
    {
        // Update our local copy of the ward list
        ward_info_list = new_ward_info_list;

        if (new_ward_info_list.size() > 0)
        {
            spinnerWardList.setVisibility(View.VISIBLE);

            // Create a list of wards for spinnerWardList
            List<String> list_of_ward_names = new ArrayList<>();

            for (WardInfo this_ward_info : new_ward_info_list)
            {
                list_of_ward_names.add(this_ward_info.ward_name);
            }

            if (getActivity() != null)
            {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list_of_ward_names);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerWardList.setAdapter(dataAdapter);
            }
            else
            {
                Log.e(TAG, "updateWardList : getActivity() is NULL");
            }
        }
    }

    public void updateBedList(ArrayList<BedInfo> new_bed_info_list)
    {
        // Update our local copy of the bed list
        bed_info_list = new_bed_info_list;

        buttonSetBedDetails.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_animation));

        if (bed_info_list.size() > 0)
        {
            // Set the Bed list for the selected Ward
            selected_ward_info = ward_info_list.get(spinnerWardList.getSelectedItemPosition());
            refineBedListForThisWardDetailsId(selected_ward_info.ward_details_id);
        }
    }

    private void refineBedListForThisWardDetailsId(int desired_ward_id)
    {
        if (bed_info_list != null)
        {
            spinnerBedList.setVisibility(View.VISIBLE);
            
            // Create a list of beds for spinnerBedList
            List<String> list_of_beds = new ArrayList<>();
            
            for (BedInfo this_bed_info : bed_info_list)
            {
                if (this_bed_info.by_ward_id == desired_ward_id)
                {
                    list_of_beds.add(this_bed_info.bed_name);
                }
            }
            
            Collections.sort(list_of_beds);
            if(getActivity() != null)
            {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list_of_beds);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBedList.setAdapter(dataAdapter);
            }
            else
            {
                Log.e(TAG,"refineBedListForThisWardDetailsId : getActivity() is NULL");
            }
        }
    }
    
    public void updateEarlyWarningScoreTypeList(ArrayList<ThresholdSet> list)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            if (list.size() > 0)
            {
                String time_now = TimestampConversion.convertDateToHumanReadableStringHoursMinutes(main_activity_interface.getNtpTimeNowInMilliseconds());

                StringBuilder threshold_sets = new StringBuilder();
                String separator = ", ";

                for (ThresholdSet this_threshold_set : list)
                {
                    threshold_sets.append(this_threshold_set.name).append(separator);
                }
                if (threshold_sets.length() > 0)
                {
                    threshold_sets = new StringBuilder(threshold_sets.substring(0, threshold_sets.length() - separator.length()));
                }

                final String string = time_now + " : " + threshold_sets;

                activity.runOnUiThread(() -> {
                    hideGettingEwsThresholdsBusyIndicator();

                    buttonGetDefaultEarlyWarningScoringTypesFromServer.setEnabled(false);
                    buttonGetDefaultEarlyWarningScoringTypesFromServer.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_gray));

                    textReceivedDefaultEarlyWarningScoringTypes.setText(string);
                });
            }
            else
            {
                // No Thresholds - so either none download to start with, or Empty Local Database INCLUDING EWS Types button pressed

                activity.runOnUiThread(() -> {
                    hideGettingEwsThresholdsBusyIndicator();

                    buttonGetDefaultEarlyWarningScoringTypesFromServer.setEnabled(true);
                    buttonGetDefaultEarlyWarningScoringTypesFromServer.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_blue));

                    textReceivedDefaultEarlyWarningScoringTypes.setText("");
                });
            }
        }
    }

    public void showForceInstallationCompleteButton(boolean show)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            if (show)
            {
                activity.runOnUiThread(() -> {
                    buttonForceInstallationComplete.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_animation));
                    buttonForceInstallationComplete.setVisibility(View.VISIBLE);
                });
            }
            else
            {
                activity.runOnUiThread(() -> {
                    buttonForceInstallationComplete.clearAnimation();
                    buttonForceInstallationComplete.setVisibility(View.INVISIBLE);
                });
            }
        }
    }
    
    public void showGatewaysAssignedBedId(String gateways_assigned_bed_id)
    {
        if (textAdminModeGatewaysAssignedBedId != null)
        {
            textAdminModeGatewaysAssignedBedId.setText(gateways_assigned_bed_id);
        }
    }

    public void setHttpsEnableStatus(boolean enabled)
    {
        checkBoxUseHttps.setChecked(enabled);
    }

    public void setWebServiceAuthenticationStatus(boolean enabled)
    {
    	checkBoxUseWebServiceAuthentication.setChecked(enabled);
    }

    public void setWebServiceEncryptionStatus(boolean enabled)
    {
    	checkBoxUseWebServiceEncryption.setChecked(enabled);
    }

    /**
     * Set the textView indicating the time offset of the local clock  
     * @param time_offset, String format {"SS.xxx" + "ms"} where SS is Seconds and xxx is milliseconds
     */
    public void setTextViewLocalTimeOffset(String time_offset)
    {
    	if(textViewTimeDrift != null)
    	{
    		textViewTimeDrift.setText(time_offset);
    	}
    }

    private void showChangeServerAddressPopup()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(R.string.are_you_sure_set_server_address);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.stringYes,
                (dialog, id) -> {
                    safeServerChangePreparations();

                    // Remove white space which is possible if a user presses "Set server address" and Space bar at the same time, IIT-2183
                    String serverAddress = editServerAddress.getText().toString().replaceAll("\\s+","");

                    main_activity_interface.setServerAddress(serverAddress);

                    main_activity_interface.sendTimeSyncCommand();

                    main_activity_interface.dismissOnScreenKeyboard(getContext(), editServerAddress);

                    dialog.cancel();
                });

        builder1.setNegativeButton(R.string.stringNo,
                (dialog, id) -> {
                    // do nothing
                    dialog.cancel();
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showChangeServerPortPopup()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(R.string.are_you_sure_set_server_port);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.stringYes,
                (dialog, id) -> {
                    safeServerChangePreparations();

                    main_activity_interface.setServerPort(editServerPort.getText().toString());

                    main_activity_interface.dismissOnScreenKeyboard(getContext(), editServerAddress);

                    dialog.cancel();
                });

        builder1.setNegativeButton(R.string.stringNo,
                (dialog, id) -> {
                    // do nothing
                    dialog.cancel();
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showInvalidNumberEnteredPopup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.invalid_number);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.string_ok,
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showYouMustEndSessionPopup()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(R.string.you_must_end_the_session_first);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.string_ok,
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void safeServerChangePreparations()
    {
        // Disable server link, export DB, clear it, and unset the bed and ward

        main_activity_interface.enableServerDataSync(false);
        main_activity_interface.enableRealTimeLink(false);

        main_activity_interface.exportLocalDatabaseToAndroidRoot();

        emptyLocalDatabaseIncludingThresholds();

        main_activity_interface.setGatewaysAssignedBedDetails(NOT_SET_YET, NOT_SET_YET, NOT_SET_YET);
    }

    public void showGetUpdatedFirmwareResult(boolean success, ArrayList<FirmwareImage> firmware_image_list)
    {
        hideUpdateBusyIndicator();

        StringBuilder firmware_versions = new StringBuilder();
        String separator = ", ";

        for (FirmwareImage firmware_image : firmware_image_list)
        {
            Log.e(TAG, "Latest " + firmware_image.device_type + " = " + firmware_image.latest_stored_firmware_version);

            if (firmware_image.latest_stored_firmware_version != INVALID_FIRMWARE_VERSION)
            {
                String device_type = firmware_image.deviceCodeLookup();
                firmware_versions.append(device_type).append(" : ").append(firmware_image.latest_stored_firmware_version).append(separator);
            }
        }

        if (firmware_versions.length() > 0)
        {
            firmware_versions = new StringBuilder(firmware_versions.substring(0, firmware_versions.length() - separator.length()));
        }

        textReceivedUpdateFirmwareVersions.setText(firmware_versions.toString());
    }

    private void showSuccessOnView(View view, boolean success)
    {
        if (view != null)
        {
            Context context = getActivity();
            if (context != null)
            {
                if (success)
                {
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
                }
                else
                {
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_red));
                }
            }

            view.setVisibility(View.VISIBLE);
        }
    }

    public void showGetGatewayConfigResult(boolean success)
    {
        showSuccessOnView(viewReceivedGatewayConfig, success);
    }

    public void showGetServerConfigurableTextResult(boolean success)
    {
        showSuccessOnView(viewReceivedServerConfigurableTextFromServer, success);
    }

    public void showGetViewableWebPageFromServerResult(boolean success)
    {
        showSuccessOnView(viewReceivedWebpageFromServer, success);
    }

    public void hideUpdateBusyIndicator()
    {
        linearLayoutGettingUpdatesFromServer.setVisibility(View.GONE);
        textReceivedUpdateFirmwareVersions.setVisibility(View.VISIBLE);
    }

    public void hideGettingEwsThresholdsBusyIndicator()
    {
        linearLayoutGetDefaultEarlyWaringScoreTypesFromServer.setVisibility(View.GONE);
        textReceivedDefaultEarlyWarningScoringTypes.setVisibility(View.VISIBLE);
    }
}
