package com.isansys.pse_isansysportal;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.isansys.common.enums.RealTimeServer;
import com.isansys.patientgateway.BedInfo;
import com.isansys.patientgateway.WardInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentInstallationProgress extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    private TextView textViewServerAddress;
    private TextView textViewServerPort;

    private TableRow tableRowRealTimePort;
    private TextView textViewRealTimePortLabel;
    private TextView textViewRealTimePort;

    private TableRow tableRowServerLinkTest;
    private TextView textViewServerLinkTest;
    private View viewServerLinkStatus;
    private ProgressBar progressBarServerLinkStatus;

    private TableRow tableRowRealtimeServerLinkTest;
    private TextView textViewRealtimeServerLinkTest;
    private View viewRealtimeServerLinkStatus;
    private ProgressBar progressBarRealtimeServerLinkStatus;

    private TableRow tableRowGettingWardsAndBeds;
    private TextView textViewGetWardsAndBeds;
    private View viewGettingWardsAndBeds;
    private ProgressBar progressBarGettingWardsAndBeds;

    private Spinner spinnerWardList;
    private Spinner spinnerBedList;

    private TableRow tableRowSelectedWardAndBed;
    private TextView textViewSelectedWard;
    private TextView textViewSelectedBed;

    private TableRow tableRowSelectWard;

    private TableRow tableRowSelectBed;

    private TableRow tableRowConfirmWardAndBed;

    private TableRow tableRowNtpTimeSync;
    private TextView textViewNtpTimeSync;
    private View viewNtpTimeSync;
    private ProgressBar progressBarNtpTimeSync;

    private TableRow tableRowGetGatewayConfig;
    private TextView textViewGetGatewayConfig;
    private View viewGetGatewayConfig;
    private ProgressBar progressBarGetGatewayConfig;

    private TableRow tableRowUpdatedFirmwareCheck;
    private TextView textViewGetUpdatedDeviceFirmware;
    private View viewGetUpdatedFirmware;
    private ProgressBar progressBarGetUpdatedDeviceFirmware;

    private TableRow tableRowGetWebPages;
    private TextView textViewGetWebPages;
    private View viewGetWebPages;
    private ProgressBar progressBarGetWebPages;

    private TableRow tableRowThresholds;
    private TextView textViewGetThresholds;
    private View viewGetThresholds;
    private ProgressBar progressBarGetThresholds;

    private TableRow tableRowServerConfigurableText;
    private TextView textViewGetServerConfigurableText;
    private View viewGetServerConfigurableText;
    private ProgressBar progressBarGetServerConfigurableText;

    private TableRow tableRowSetupComplete;

    private TextView textViewErrorMessage;

    private ArrayList<BedInfo> bed_info_list = null;
    private ArrayList<WardInfo> ward_info_list = null;

    private WardInfo selected_ward_info = new WardInfo();
    private final BedInfo selected_bed_info = new BedInfo();

    public enum SetupStage
    {
        NTP_TIME_SYNC,
        SEND_SERVER_PING,
        GETTING_WARDS_AND_BEDS,
        SELECT_WARD_AND_BED,
        REALTIME_SERVER_LINK_TEST,
        GET_GATEWAY_SETTINGS_FROM_SERVER,
        GET_THRESHOLDS,
        GET_SERVER_CONFIGURABLE_TEXT,
        GET_UPDATED_FIRMWARE,
        GET_WEBPAGES_IF_ENABLED,
        COMPLETE,
    }

    private SetupStage setup_stage;

    public static final String INSTALLATION_PROGRESS_REFRESH_SERVER_DATA_ONLY = "INSTALLATION_PROGRESS_REFRESH_SERVER_DATA_ONLY";
    public static final String SHOW_BUTTON_AT_END = "SHOW_BUTTON_AT_END";

    private boolean refresh_server_data_only;
    private boolean show_button_at_end;

    private RealTimeServer realtime_connection_type;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.installation_progress, container, false);

        realtime_connection_type = main_activity_interface.getRealTimeClientType();

        textViewServerAddress = v.findViewById(R.id.textViewServerAddress);
        textViewServerPort = v.findViewById(R.id.textViewServerPort);

        tableRowRealTimePort = v.findViewById(R.id.tableRowRealTimePort);
        tableRowRealTimePort.setVisibility(View.GONE);
        textViewRealTimePortLabel = v.findViewById(R.id.textViewRealTimePortLabel);
        textViewRealTimePort = v.findViewById(R.id.textViewRealTimePort);

        tableRowServerLinkTest = v.findViewById(R.id.tableRowServerLinkTest);
        tableRowServerLinkTest.setVisibility(View.GONE);
        textViewServerLinkTest = v.findViewById(R.id.textViewServerLinkTest);
        viewServerLinkStatus = v.findViewById(R.id.viewServerLinkStatus);
        progressBarServerLinkStatus = v.findViewById(R.id.progressBarServerLinkStatus);

        tableRowRealtimeServerLinkTest = v.findViewById(R.id.tableRowRealTimeLinkTest);
        tableRowRealtimeServerLinkTest.setVisibility(View.GONE);
        textViewRealtimeServerLinkTest = v.findViewById(R.id.textViewRealTimeLinkTest);
        viewRealtimeServerLinkStatus = v.findViewById(R.id.viewRealTimeLinkStatus);
        progressBarRealtimeServerLinkStatus = v.findViewById(R.id.progressBarRealTimeLinkStatus);

        tableRowGettingWardsAndBeds = v.findViewById(R.id.tableRowGettingWardsAndBeds);
        textViewGetWardsAndBeds = v.findViewById(R.id.textViewGetWardsAndBeds);
        tableRowGettingWardsAndBeds.setVisibility(View.GONE);
        viewGettingWardsAndBeds = v.findViewById(R.id.viewGettingWardsAndBeds);
        progressBarGettingWardsAndBeds = v.findViewById(R.id.progressBarGettingWardsAndBeds);

        tableRowSelectedWardAndBed = v.findViewById(R.id.tableRowSelectedWardAndBed);
        tableRowSelectedWardAndBed.setVisibility(View.GONE);
        textViewSelectedWard = v.findViewById(R.id.textViewSelectedWard);
        textViewSelectedBed = v.findViewById(R.id.textViewSelectedBed);

        tableRowSelectWard = v.findViewById(R.id.tableRowSelectWard);
        tableRowSelectWard.setVisibility(View.GONE);

        tableRowSelectBed = v.findViewById(R.id.tableRowSelectBed);
        tableRowSelectBed.setVisibility(View.GONE);

        tableRowConfirmWardAndBed = v.findViewById(R.id.tableRowConfirmWardAndBed);
        tableRowConfirmWardAndBed.setVisibility(View.GONE);

        tableRowNtpTimeSync = v.findViewById(R.id.tableRowNtpTimeSync);
        tableRowNtpTimeSync.setVisibility(View.GONE);
        textViewNtpTimeSync = v.findViewById(R.id.textViewNtpTimeSync);
        viewNtpTimeSync = v.findViewById(R.id.viewNtpTimeSync);
        progressBarNtpTimeSync = v.findViewById(R.id.progressBarNtpTimeSync);

        tableRowGetGatewayConfig = v.findViewById(R.id.tableRowGetGatewayConfig);
        tableRowGetGatewayConfig.setVisibility(View.GONE);
        textViewGetGatewayConfig = v.findViewById(R.id.textViewGetGatewayConfig);
        viewGetGatewayConfig = v.findViewById(R.id.viewGetGatewayConfig);
        progressBarGetGatewayConfig = v.findViewById(R.id.progressBarGetGatewayConfig);

        tableRowThresholds = v.findViewById(R.id.tableRowThresholds);
        tableRowThresholds.setVisibility(View.GONE);
        textViewGetThresholds = v.findViewById(R.id.textViewGetThresholds);
        viewGetThresholds = v.findViewById(R.id.viewGetThresholds);
        progressBarGetThresholds = v.findViewById(R.id.progressBarGetThresholds);

        tableRowServerConfigurableText = v.findViewById(R.id.tableRowServerConfigurableText);
        tableRowServerConfigurableText.setVisibility(View.GONE);
        textViewGetServerConfigurableText = v.findViewById(R.id.textViewGetServerConfigurableText);
        viewGetServerConfigurableText = v.findViewById(R.id.viewGetServerConfigurableText);
        progressBarGetServerConfigurableText = v.findViewById(R.id.progressBarGetServerConfigurableText);

        tableRowUpdatedFirmwareCheck = v.findViewById(R.id.tableRowUpdatedFirmwareCheck);
        tableRowUpdatedFirmwareCheck.setVisibility(View.GONE);
        textViewGetUpdatedDeviceFirmware = v.findViewById(R.id.textViewGetUpdatedDeviceFirmware);
        viewGetUpdatedFirmware = v.findViewById(R.id.viewGetUpdatedFirmware);
        progressBarGetUpdatedDeviceFirmware = v.findViewById(R.id.progressBarGetUpdatedDeviceFirmware);

        tableRowGetWebPages = v.findViewById(R.id.tableRowGetWebPages);
        tableRowGetWebPages.setVisibility(View.GONE);
        textViewGetWebPages = v.findViewById(R.id.textViewGetWebPages);
        viewGetWebPages = v.findViewById(R.id.viewGetWebPages);
        progressBarGetWebPages = v.findViewById(R.id.progressBarGetWebPages);

        tableRowSetupComplete = v.findViewById(R.id.tableRowSetupComplete);
        tableRowSetupComplete.setVisibility(View.GONE);

        textViewErrorMessage = v.findViewById(R.id.textViewErrorMessage);
        textViewErrorMessage.setVisibility(View.GONE);

        spinnerWardList = v.findViewById(R.id.spinnerWardList);
        spinnerWardList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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

            }
        });


        spinnerBedList = v.findViewById(R.id.spinnerBedList);
        spinnerBedList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                String item_selected = parent.getItemAtPosition(pos).toString();

                if (bed_info_list != null)
                {
                    tableRowConfirmWardAndBed.setVisibility(View.INVISIBLE);

                    // Go through the list of beds (all wards) to find the record matching this one (cant use Pos as this is the position in this ward only)
                    for (BedInfo this_bed_info : bed_info_list)
                    {
                        if ((this_bed_info.bed_name.equals(item_selected)) && (this_bed_info.by_ward_id == selected_ward_info.ward_details_id))
                        {
                            selected_bed_info.bed_details_id = this_bed_info.bed_details_id;
                            selected_bed_info.bed_name = this_bed_info.bed_name;

                            Log.d(TAG, "bed_details_id = " + this_bed_info.bed_details_id + ". by_ward_id = " + this_bed_info.by_ward_id + ". bed_name = " + this_bed_info.bed_name);

                            tableRowConfirmWardAndBed.setVisibility(View.VISIBLE);
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


        Button buttonSetBedDetails = v.findViewById(R.id.buttonSetBedDetails);
        buttonSetBedDetails.setOnClickListener(v1 -> {
            main_activity_interface.setGatewaysAssignedBedDetails(String.valueOf(selected_bed_info.bed_details_id), selected_ward_info.ward_name, selected_bed_info.bed_name);

            setup_stage = SetupStage.REALTIME_SERVER_LINK_TEST;

            setupNextSetupWizardStage();
        });


        Button buttonSetupComplete = v.findViewById(R.id.buttonSetupComplete);
        buttonSetupComplete.setOnClickListener(v12 -> main_activity_interface.installationProcessComplete());


        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            refresh_server_data_only = bundle.getBoolean(INSTALLATION_PROGRESS_REFRESH_SERVER_DATA_ONLY);
            show_button_at_end = bundle.getBoolean(SHOW_BUTTON_AT_END);
        }

        if (refresh_server_data_only)
        {
            setup_stage = SetupStage.GET_GATEWAY_SETTINGS_FROM_SERVER;

            tableRowServerLinkTest.setVisibility(View.GONE);
            tableRowRealtimeServerLinkTest.setVisibility(View.GONE);
            tableRowGettingWardsAndBeds.setVisibility(View.GONE);
            tableRowSelectedWardAndBed.setVisibility(View.GONE);
            tableRowSelectWard.setVisibility(View.GONE);
            tableRowSelectBed.setVisibility(View.GONE);
            tableRowConfirmWardAndBed.setVisibility(View.GONE);
            tableRowNtpTimeSync.setVisibility(View.GONE);
        }
        else
        {
            setup_stage = SetupStage.NTP_TIME_SYNC;
        }

        return v;
    }


    @Override
    public void onResume()
    {
        main_activity_interface.getServerAddress();
        main_activity_interface.getServerPort();
        main_activity_interface.getRealTimeServerPort();

        if (!refresh_server_data_only)
        {
            main_activity_interface.turnOffRealTimeStreaming();
        }

        setupNextSetupWizardStage();

        super.onResume();
    }


    private void setupNextSetupWizardStage()
    {
        Log.e(TAG, "setupNextSetupWizardStage : setup_stage = " + setup_stage);

        hideErrorMessage();

        switch (setup_stage)
        {
            case NTP_TIME_SYNC:
            {
                // Show new row
                tableRowNtpTimeSync.setVisibility(View.VISIBLE);
                progressBarNtpTimeSync.setVisibility(View.VISIBLE);
                viewNtpTimeSync.setVisibility(View.GONE);
            }
            break;

            case SEND_SERVER_PING:
            {
                // Tidy up previous state
                setFontAndSizeToInactive(textViewNtpTimeSync);

                // Show new table rows
                tableRowServerLinkTest.setVisibility(View.VISIBLE);
                progressBarServerLinkStatus.setVisibility(View.VISIBLE);
                viewServerLinkStatus.setVisibility(View.GONE);
            }
            break;

            case GETTING_WARDS_AND_BEDS:
            {
                // Tidy up previous state
                setFontAndSizeToInactive(textViewServerLinkTest);

                tableRowGettingWardsAndBeds.setVisibility(View.VISIBLE);
                progressBarGettingWardsAndBeds.setVisibility(View.VISIBLE);
                viewGettingWardsAndBeds.setVisibility(View.GONE);
            }
            break;

            case SELECT_WARD_AND_BED:
            {
                // Tidy up previous state
                setFontAndSizeToInactive(textViewGetWardsAndBeds);

                // Show new table rows
                tableRowSelectWard.setVisibility(View.VISIBLE);
                tableRowSelectBed.setVisibility(View.VISIBLE);
            }
            break;

            case REALTIME_SERVER_LINK_TEST:
            {
                // Tidy up previous state
                tableRowSelectWard.setVisibility(View.GONE);
                tableRowSelectBed.setVisibility(View.GONE);
                tableRowConfirmWardAndBed.setVisibility(View.GONE);

                textViewSelectedWard.setText(selected_ward_info.ward_name);
                textViewSelectedBed.setText(selected_bed_info.bed_name);
                tableRowSelectedWardAndBed.setVisibility(View.VISIBLE);

                // Show new row
                tableRowRealtimeServerLinkTest.setVisibility(View.VISIBLE);

                if(realtime_connection_type == RealTimeServer.WAMP)
                {
                    textViewRealtimeServerLinkTest.setText(R.string.wamp_link_test);
                }
                else if(realtime_connection_type == RealTimeServer.MQTT)
                {
                    textViewRealtimeServerLinkTest.setText(R.string.mqtt_link_test);
                }

                progressBarRealtimeServerLinkStatus.setVisibility(View.VISIBLE);
                viewRealtimeServerLinkStatus.setVisibility(View.GONE);
            }
            break;

            case GET_GATEWAY_SETTINGS_FROM_SERVER:
            {
                // Tidy up previous state if getting here via SELECT_WARD_AND_BED
                tableRowSelectWard.setVisibility(View.GONE);
                tableRowSelectBed.setVisibility(View.GONE);
                tableRowConfirmWardAndBed.setVisibility(View.GONE);

                textViewSelectedWard.setText(selected_ward_info.ward_name);
                textViewSelectedBed.setText(selected_bed_info.bed_name);
                tableRowSelectedWardAndBed.setVisibility(View.VISIBLE);

                // Tidy up previous state if getting here via REALTIME_SERVER_LINK_TEST
                setFontAndSizeToInactive(textViewRealtimeServerLinkTest);

                // Show new row
                tableRowGetGatewayConfig.setVisibility(View.VISIBLE);
                progressBarGetGatewayConfig.setVisibility(View.VISIBLE);
                viewGetGatewayConfig.setVisibility(View.GONE);
            }
            break;

            case GET_THRESHOLDS:
            {
                // Tidy up previous state
                setFontAndSizeToInactive(textViewGetGatewayConfig);

                // Show new row
                tableRowThresholds.setVisibility(View.VISIBLE);
                progressBarGetThresholds.setVisibility(View.VISIBLE);
                viewGetThresholds.setVisibility(View.GONE);
            }
            break;

            case GET_SERVER_CONFIGURABLE_TEXT:
            {
                // Tidy up previous state
                setFontAndSizeToInactive(textViewGetThresholds);

                // Show new row
                tableRowServerConfigurableText.setVisibility(View.VISIBLE);
                progressBarGetServerConfigurableText.setVisibility(View.VISIBLE);
                viewGetServerConfigurableText.setVisibility(View.GONE);
            }
            break;

            case GET_UPDATED_FIRMWARE:
            {
                // Tidy up previous state
                setFontAndSizeToInactive(textViewGetServerConfigurableText);

                // Show new row
                tableRowUpdatedFirmwareCheck.setVisibility(View.VISIBLE);
                progressBarGetUpdatedDeviceFirmware.setVisibility(View.VISIBLE);
                viewGetUpdatedFirmware.setVisibility(View.GONE);
            }
            break;

            case GET_WEBPAGES_IF_ENABLED:
            {
                // Tidy up previous state
                setFontAndSizeToInactive(textViewGetUpdatedDeviceFirmware);

                // Show new row
                tableRowGetWebPages.setVisibility(View.VISIBLE);
                progressBarGetWebPages.setVisibility(View.VISIBLE);
                viewGetWebPages.setVisibility(View.GONE);
            }
            break;

            case COMPLETE:
            {
                if (!show_button_at_end)
                {
                    main_activity_interface.lockScreenSelected();
                }
                else
                {
                    // Tidy up previous state (two ways of getting here - so previous row can be different
                    setFontAndSizeToInactive(textViewGetUpdatedDeviceFirmware);
                    setFontAndSizeToInactive(textViewGetWebPages);

                    // Show new row
                    tableRowSetupComplete.setVisibility(View.VISIBLE);
                }
            }
            break;
        }
    }

    public void setServerAddress(String server_address)
    {
        textViewServerAddress.setText(server_address);
    }


    public void setServerPort(String server_port)
    {
        textViewServerPort.setText(server_port);
    }


    public void setRealTimeServerPort(String real_time_server_port)
    {
        tableRowRealTimePort.setVisibility(View.VISIBLE);
        if(realtime_connection_type == RealTimeServer.WAMP)
        {
            textViewRealTimePortLabel.setText(R.string.lifeguard_server_wamp_port);
        }
        else if(realtime_connection_type == RealTimeServer.MQTT)
        {
            textViewRealTimePortLabel.setText(R.string.lifeguard_server_mqtt_port);
        }

        textViewRealTimePort.setText(real_time_server_port);
    }


    private void hideErrorMessage()
    {
        textViewErrorMessage.setVisibility(View.GONE);
    }


    private void showErrorMessage(String error_text)
    {
        textViewErrorMessage.setVisibility(View.VISIBLE);
        textViewErrorMessage.setText(error_text);
    }


    public void showServerPingResult(boolean ping_status, boolean authentication_ok)
    {
        hideErrorMessage();
        progressBarServerLinkStatus.setVisibility(View.GONE);

        showIndicator(viewServerLinkStatus, ping_status);

        if (ping_status)
        {
            setup_stage = SetupStage.GETTING_WARDS_AND_BEDS;
            setupNextSetupWizardStage();
        }
        else
        {
            textViewErrorMessage.setVisibility(View.VISIBLE);

            if (authentication_ok)
            {
                showErrorMessage(getResources().getString(R.string.no_response_from_lifeguard_server_please_check_address_and_port_correct));
            }
            else
            {
                showErrorMessage(getResources().getString(R.string.authentication_failure));
            }
        }
    }


    public void showRealtimeServerConnectionResult(boolean connected)
    {
        if (setup_stage == SetupStage.REALTIME_SERVER_LINK_TEST)
        {
            hideErrorMessage();

            progressBarRealtimeServerLinkStatus.setVisibility(View.GONE);
        }

        showIndicator(viewRealtimeServerLinkStatus, connected);

        if (setup_stage == SetupStage.REALTIME_SERVER_LINK_TEST)
        {
            if (connected)
            {
                setup_stage = SetupStage.GET_GATEWAY_SETTINGS_FROM_SERVER;

                setupNextSetupWizardStage();
            }
            else
            {
                if(realtime_connection_type == RealTimeServer.WAMP)
                {
                    showErrorMessage(getResources().getString(R.string.cannot_connect_to_wamp_server_please_check_port_accessible));
                }
                else if(realtime_connection_type == RealTimeServer.MQTT)
                {
                    showErrorMessage(getResources().getString(R.string.cannot_connect_to_mqtt_server_please_check_port_accessible));
                }
            }
        }
    }

    public void updateWardList(ArrayList<WardInfo> new_ward_info_list)
    {
        progressBarGettingWardsAndBeds.setVisibility(View.GONE);

        if (new_ward_info_list.size() > 0)
        {
            showIndicator(viewGettingWardsAndBeds, true);

            setup_stage = SetupStage.SELECT_WARD_AND_BED;
            setupNextSetupWizardStage();

            // Update our local copy of the ward list
            ward_info_list = new_ward_info_list;

            // Create a list of wards for spinnerWardList
            List<String> list_of_ward_names = new ArrayList<>();

            for (WardInfo this_ward_info : new_ward_info_list)
            {
                list_of_ward_names.add(this_ward_info.ward_name);
            }

            if(getActivity() != null)
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
        else
        {
            showIndicator(viewGettingWardsAndBeds, false);
        }
    }


    public void updateBedList(ArrayList<BedInfo> new_bed_info_list)
    {
        // Update our local copy of the bed list
        bed_info_list = new_bed_info_list;

        // Set the Bed list for the selected Ward
        selected_ward_info = ward_info_list.get(spinnerWardList.getSelectedItemPosition());
        refineBedListForThisWardDetailsId(selected_ward_info.ward_details_id);
    }


    private void refineBedListForThisWardDetailsId(int desired_ward_id)
    {
        if (bed_info_list != null)
        {
            // Create a list of beds for spinnerBedList
            List<String> list_of_beds = new ArrayList<>();

            for (BedInfo this_bed_info : bed_info_list)
            {
                if (this_bed_info.by_ward_id == desired_ward_id)
                {
                    Log.e(TAG, "Adding Bed : " + this_bed_info.bed_name);
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


    /**
     * Set the textView indicating the time offset of the local clock
     */
    public void setTextViewLocalTimeOffset(boolean got_offset, int attempt_number, int attempt_max)
    {
        hideErrorMessage();

        progressBarNtpTimeSync.setVisibility(View.GONE);

        showIndicator(viewNtpTimeSync, got_offset);

        if (setup_stage == SetupStage.NTP_TIME_SYNC)
        {
            if (got_offset)
            {
                setup_stage = SetupStage.SEND_SERVER_PING;
                setupNextSetupWizardStage();
            }
            else
            {
                Log.e(TAG, "setTextViewLocalTimeOffset failed : " + attempt_number + "/" + attempt_max);

                if (attempt_number == attempt_max)
                {
                    showErrorMessage(getResources().getString(R.string.cannot_connect_to_ntp_server_ntp_sync_failed));
                }
                else
                {
                    showErrorMessage(getResources().getString(R.string.cannot_connect_to_ntp_server_please_check_port_accessible) + " (" + attempt_number + "/" + attempt_max + ")");
                }
            }
        }
    }


    public void showGetGatewayConfigResult(boolean success)
    {
        hideErrorMessage();
        progressBarGetGatewayConfig.setVisibility(View.GONE);

        showIndicator(viewGetGatewayConfig, success);

        if (success)
        {
            setup_stage = SetupStage.GET_THRESHOLDS;
            setupNextSetupWizardStage();
        }
        else
        {
            showErrorMessage(getResources().getString(R.string.no_response_from_lifeguard_server_please_check_address_and_port_correct));
        }
    }


    public void showGetThresholdsResult(boolean success)
    {
        hideErrorMessage();
        progressBarGetThresholds.setVisibility(View.GONE);

        showIndicator(viewGetThresholds, success);

        if (success)
        {
            setup_stage = SetupStage.GET_SERVER_CONFIGURABLE_TEXT;
            setupNextSetupWizardStage();
        }
        else
        {
            showErrorMessage(getResources().getString(R.string.no_response_from_lifeguard_server_please_check_address_and_port_correct));
        }
    }


    public void showGetServerConfigurableTextResult(boolean success)
    {
        hideErrorMessage();
        progressBarGetServerConfigurableText.setVisibility(View.GONE);

        showIndicator(viewGetServerConfigurableText, success);

        if (success)
        {
            setup_stage = SetupStage.GET_UPDATED_FIRMWARE;
            setupNextSetupWizardStage();
        }
        else
        {
            showErrorMessage(getResources().getString(R.string.no_response_from_lifeguard_server_please_check_address_and_port_correct));
        }
    }


    public void showGetUpdatedFirmwareResult(boolean success)
    {
        hideErrorMessage();
        progressBarGetUpdatedDeviceFirmware.setVisibility(View.GONE);

        showIndicator(viewGetUpdatedFirmware, success);

        if (success)
        {
            if (main_activity_interface.getViewWebPagesEnabled())
            {
                setup_stage = SetupStage.GET_WEBPAGES_IF_ENABLED;
            }
            else
            {
                setup_stage = SetupStage.COMPLETE;
            }

            setupNextSetupWizardStage();
        }
        else
        {
            showErrorMessage(getResources().getString(R.string.no_response_from_lifeguard_server_please_check_address_and_port_correct));
        }
    }

    public void showGetWebPagesResult(boolean success)
    {
        hideErrorMessage();
        progressBarGetWebPages.setVisibility(View.GONE);

        showIndicator(viewGetWebPages, success);

        if (success)
        {
            setup_stage = SetupStage.COMPLETE;
            setupNextSetupWizardStage();
        }
        else
        {
            showErrorMessage(getResources().getString(R.string.no_response_from_lifeguard_server_please_check_address_and_port_correct));
        }
    }

    private void showIndicator(View indicator, boolean success)
    {
        indicator.setVisibility(View.VISIBLE);

        if (success)
        {
            indicator.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_green, null));
        }
        else
        {
            indicator.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_red, null));
        }
    }

    private void setFontAndSizeToInactive(TextView textView)
    {
        int INACTIVE_TEXT_SIZE = 24;

        textView.setTextSize(INACTIVE_TEXT_SIZE);
        textView.setTypeface(null, Typeface.NORMAL);
    }
}
