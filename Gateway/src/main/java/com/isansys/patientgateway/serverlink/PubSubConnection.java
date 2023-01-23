package com.isansys.patientgateway.serverlink;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.isansys.common.VideoCallContact;
import com.isansys.common.VideoCallDetails;
import com.isansys.common.enums.Commands;
import com.isansys.common.enums.RealTimeServer;
import com.isansys.common.enums.SensorType;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.serverlink.realtimeclient.MqttClient;
import com.isansys.patientgateway.serverlink.realtimeclient.RealTimeClient;
import com.isansys.patientgateway.serverlink.realtimeclient.WampClient;

import java.util.ArrayList;
import java.util.Date;

import static com.isansys.patientgateway.serverlink.ServerLink.INTENT__COMMANDS_FROM_SERVER;

// This class wraps up real time communications between the Tablet and the Server for streaming Setup mode, Leads Off status, and for receiving commands from the Server

public class PubSubConnection
{
    private final RemoteLoggingWithEnable Log;
	private final ContextInterface gateway_context_interface;
    private final String TAG = "PubSubConnection";

    private String hostname = "192.168.1.148";
    private String port = "9001";
    
    private String topic_enable_server_link;                                            // Incoming command to enable/disable the Server Link (webServices only - not WAMP)
    private String topic_enable_device_setup_mode;
    private String topic_enable_device_raw_accelerometer_mode;
    private String topic_device_setup_mode_samples;
    private String topic_device_raw_accelerometer_mode_sample;
    private String topic_report_device_leadsoff;
    private String topic_gateway_ping;
    private String topic_export_database_to_tablet;
    private String topic_empty_gateway_database;
    private String topic_reset_gateway_and_ui_run_counters;                             // Incoming command to empty the App run counters database tables  
    private String topic_report_gateway_status;                                         // Incoming command to request the current Gateway status - Admin mode control settings etc
    private String topic_gateway_status_response;                                       // Response of above command back to the Server/Webpage
    private String topic_enable_patient_name_lookup;                                    // Incoming command to enable/disable Patient Name Lookup from the Server
    private String topic_enable_periodic_setup_mode;                                    // Incoming command to enable/disable Periodic Setup Mode every hour
    private String topic_manual_time_sync;                                              // Incoming command to trigger a manual NTP time sync with the Server 
    private String topic_enable_dummy_data_mode;                                        // Incoming command to enable/disable Dummy Data Mode
    private String topic_qr_code_unlock_user;                                           // Incoming command to simulate presenting a User QR Code
    private String topic_qr_code_unlock_admin;                                          // Incoming command to simulate presenting an Admin QR Code
    private String topic_qr_code_unlock_feature_enable;                                 // Incoming command to simulate presenting a Feature Enable QR Code
    private String topic_report_webservice_result;                                      // Notify WAMP the result of the last webservice call
    private String topic_report_database_status;                                        // Replicate the UI footer via WAMP
    private String topic_enable_webservice_authentication;								// Incoming command to enable webservice authentication
    private String topic_set_json_array_size;                                           // Incoming command to set JSON array size sent to the Server.
    private String topic_set_spo2_long_term_measurements_timeout;                       // Incoming command to set long-term measurement timeout
    private String topic_set_blood_pressure_long_term_measurements_timeout;             // Incoming command to set long-term measurement timeout
    private String topic_set_weight_long_term_measurements_timeout;                     // Incoming command to set long-term measurement timeout
    private String topic_set_third_party_temperature_long_term_measurements_timeout;    // Incoming command to set long-term measurement timeout
    private String topic_set_dummy_data_mode_measurements_per_tick;                     // Incoming command to set the number of Heart Beats and Pulse Ox Intermediate measurements written per second
    private String topic_setup_mode_length_in_seconds;                                  // Incoming command to set Lifetouch setup mode length (in seconds)
    private String topic_periodic_setup_mode_active_time_in_seconds;                    // Incoming command to set length of periodic setup mode
    private String topic_periodic_setup_mode_period_time_in_seconds;                    // Incoming command to set period of periodic setup mode
    private String topic_percentage_of_poor_signal_heart_beats_before_marking_as_invalid;
                                                                                        // Incoming command to set percentage of poor signal heart beats can be received before marking as invalid
    private String topic_nonin_pulse_ox_number_invalid_intermediate_measurements_before_marking_as_invalid;
                                                                                        // Incoming command to set how many intermediate measurements the Nonin Pulse Ox can have before marking that minute as invalid
    private String topic_enable_webservice_encryption;								    // Incoming command to enable webservice Encryption
    private String topic_get_features_enabled;								            // Incoming command to trigger the below
    private String topic_report_features_enabled;								        // Outgoing to WAMP server to report what "features" are enabled
    private String topic_enable_manual_vital_signs_entry;								// Incoming command to enable Manual Vital Signs Entry (Feature Unlock page)
    private String topic_enable_csv_output;								                // Incoming command to enable CSV Output (Feature Unlock page)
    private String topic_report_device_setup_mode_enabled;
    private String topic_report_device_raw_accelerometer_mode_enabled;
    private String topic_enable_night_mode;								                // Incoming command to enable night mode
    private String topic_enable_unplugged_overlay;								        // Incoming command to enable night mode
    private String topic_reset_server_sync_state;								        // Incoming command to reset the sever sync's state
    private String topic_request_patient_name_from_hospital_patient_details_id;         // Outgoing command to request the Patient Name from the passed Hospital Patient ID
    private String topic_enable_spo2_spot_measurements;                                 // Incoming command to enable SpO2 non-finalised minutes to be processed before requiring SpO2 reconnection
    private String topic_enable_simple_heart_rate;
    private String topic_display_timeout_in_seconds;                                    // Incoming command to set Display Timeout
    private String topic_display_timeout_applies_to_patient_vitals;                     // Incoming command to signify whether or not Patient Vitals is subject to the display timeout

    private String topic_request_database_sync_status;								    // Incoming command
    private String topic_request_all_device_info_objects;	    					    // Incoming command
    private String topic_delete_exported_databases;	    					            // Incoming command
    private String topic_enable_gsm_mode;	    					                    // Incoming command
    private String topic_enable_usa_only_mode;	    					                // Incoming command
    private String topic_enable_developer_popup;	    					            // Incoming command
    private String topic_show_server_syncing_popup;	    					            // Incoming command to show/hide the Server Syncing Popup

    private String topic_enable_pre_defined_annotations;                                // Incoming command to enable pre defined annotations
    private String topic_enable_auto_add_ews;                                           // Incoming command to enable auto add EWS
    private String topic_enable_dfu;   							                        // Incoming command to enable DFU

    private String topic_enable_show_numbers_on_battery_indicator;                      // Incoming command from Feature Enable
    private String topic_enable_show_ip_address_on_wifi_popup;                          // Incoming command from Feature Enable
    private String topic_enable_run_devices_in_test_mode;                               // Incoming command from Feature Enable
    private String topic_enable_show_mac_on_device_status;                              // Incoming command from Feature Enable
    private String topic_enable_lifetouch_activity_level;                               // Incoming command from Feature Enable
    private String topic_enable_session_auto_resume;                                    // Incoming command from Feature Enable
    private String topic_enable_auto_log_file_upload;                                   // Incoming command from Feature Enable
    private String topic_enable_manufacturing_mode;                                     // Incoming command from Feature Enable
    private String topic_enable_back_camera;                                            // Incoming command from Feature Enable
    private String topic_enable_patient_orientation;                                    // Incoming command from Feature Enable
    private String topic_enable_wifi_logging;                                           // Incoming command from Feature Enable
    private String topic_enable_gsm_logging;                                            // Incoming command from Feature Enable
    private String topic_enable_database_logging;                                       // Incoming command from Feature Enable
    private String topic_enable_server_logging;                                         // Incoming command from Feature Enable
    private String topic_enable_battery_logging;                                        // Incoming command from Feature Enable
    private String topic_lifetemp_measurement_interval_in_seconds;                      // Incoming command from Feature Enable
    private String topic_patient_orientation_measurement_interval_in_seconds;           // Incoming command from Feature Enable
    private String topic_enable_video_calls;                                            // Incoming command from Feature Enable
    private String topic_request_patient_specific_video_call_contacts;                  // Outgoing command to request the Patient Name from the passed Hospital Patient ID
    private String topic_server_requesting_video_call;                                  // Incoming command to request a Video call
    private String topic_server_declined_video_call;                                    // Incoming command from Lifeguard to decline a a Video call request
    private String topic_server_update_video_call_connection_id;                        // Incoming command from Lifeguard to tell the Gateway the Lifeguard Browser Connection ID
    private String topic_server_joining_video_call;                                     // Incoming command to join a Video call
    private String topic_report_gateway_video_call_status;                              // Outgoing command to Server
    private String topic_exit_gateway;                                                  // Incoming command to exit the Gateway

    private String topic_patient_on_gateway_requesting_video_call;                      // Outgoing command to notify the Server that the Patient has asked for a Video call
    private RealTimeClient mConnection;

    private boolean have_valid_topics = false;

    // variables to implement timeout on Gateway_Ping so we can see if the network connection has been lost without the Socket being closed.
    private Date dtLastPong = null;
    private boolean pong_received = false;

    private final String gateway_unique_id;

    private final PatientGatewayInterface patient_gateway_interface;

    private String bed_id;

    private final Gson gson;

    public PubSubConnection(ContextInterface context_interface, RemoteLoggingWithEnable logger, String passed_in_gateway_unique_id, PatientGatewayInterface desired_patient_gateway_interface)
    {
        gateway_context_interface = context_interface;

        Log = logger;

        gson = new Gson();

        gateway_unique_id = passed_in_gateway_unique_id;

        patient_gateway_interface = desired_patient_gateway_interface;
        mConnection = null;
        bed_id = "INVALID";
    }
    
    
    public void updateTopics(int desired_bed_id)
    {
        bed_id = String.valueOf(desired_bed_id);
        
        topic_enable_server_link = "bed" + bed_id + "/enable_server_link";
        topic_enable_device_setup_mode = "bed" + bed_id + "/enable_device_setup_mode";
        topic_device_setup_mode_samples = "bed" + bed_id + "/device_setup_mode_samples";
        topic_enable_device_raw_accelerometer_mode = "bed" + bed_id + "/enable_device_raw_accelerometer_mode";
        topic_device_raw_accelerometer_mode_sample = "bed" + bed_id + "/device_raw_accelerometer_mode_samples";
        topic_report_device_leadsoff = "bed" + bed_id + "/report_device_leadsoff";
        topic_export_database_to_tablet = "bed" + bed_id + "/export_database";
        topic_empty_gateway_database = "bed" + bed_id + "/empty_database";
        topic_reset_gateway_and_ui_run_counters = "bed" + bed_id + "/reset_gateway_and_ui_run_counters";
        topic_report_gateway_status = "bed" + bed_id + "/report_gateway_status";
        topic_gateway_status_response = "bed" + bed_id + "/gateway_status_response";
        topic_enable_patient_name_lookup = "bed" + bed_id + "/enable_patient_name_lookup";
        topic_enable_periodic_setup_mode = "bed" + bed_id + "/enable_periodic_setup_mode";
        topic_enable_dummy_data_mode = "bed" + bed_id + "/enable_dummy_data_mode";
        topic_manual_time_sync = "bed" + bed_id + "/manual_time_sync";
        topic_qr_code_unlock_user = "bed" + bed_id + "/qr_code_unlock_user"; 
        topic_qr_code_unlock_admin = "bed" + bed_id + "/qr_code_unlock_admin";
        topic_qr_code_unlock_feature_enable = "bed" + bed_id + "/qr_code_unlock_feature_enable";
        topic_report_webservice_result = "bed" + bed_id + "/report_webservice_result"; 
        topic_report_database_status = "bed" + bed_id + "/report_database_status";
        topic_enable_webservice_authentication = "bed" + bed_id + "/enable_webservice_authentication";
        topic_enable_webservice_encryption = "bed" + bed_id + "/enable_webservice_encryption";
        topic_set_json_array_size = "bed" + bed_id + "/set_json_array_size";
        topic_set_spo2_long_term_measurements_timeout = "bed" + bed_id + "/set_spo2_long_term_measurements_timeout";
        topic_set_blood_pressure_long_term_measurements_timeout = "bed" + bed_id + "/set_blood_pressure_long_term_measurements_timeout";
        topic_set_weight_long_term_measurements_timeout = "bed" + bed_id + "/set_weight_long_term_measurements_timeout";
        topic_set_third_party_temperature_long_term_measurements_timeout = "bed" + bed_id + "/set_third_party_temperature_long_term_measurements_timeout";
        topic_set_dummy_data_mode_measurements_per_tick = "bed" + bed_id + "/set_dummy_data_mode_measurements_per_tick"; 
        topic_setup_mode_length_in_seconds = "bed" + bed_id + "/set_setup_mode_length_in_seconds";
        topic_periodic_setup_mode_active_time_in_seconds = "bed" + bed_id + "/set_periodic_setup_mode_active_time_in_seconds";
        topic_periodic_setup_mode_period_time_in_seconds = "bed" + bed_id + "/set_periodic_setup_mode_period_time_in_seconds";
        topic_percentage_of_poor_signal_heart_beats_before_marking_as_invalid = "bed" + bed_id + "/set_percentage_of_poor_signal_heart_beats_before_marking_as_invalid";
        topic_nonin_pulse_ox_number_invalid_intermediate_measurements_before_marking_as_invalid = "bed" + bed_id + "/set_nonin_pulse_ox_number_invalid_intermediate_measurements_before_marking_as_invalid";
        topic_get_features_enabled = "bed" + bed_id + "/get_features_enabled";
        topic_report_features_enabled = "bed" + bed_id + "/report_features_enabled";
        topic_enable_manual_vital_signs_entry = "bed" + bed_id + "/enable_manual_vital_signs_entry";
        topic_enable_csv_output = "bed" + bed_id + "/enable_csv_output";
        topic_report_device_setup_mode_enabled = "bed" + bed_id + "/report_device_setup_mode_enabled";
        topic_report_device_raw_accelerometer_mode_enabled = "bed" + bed_id + "/report_device_raw_accelerometer_mode_enabled";
        topic_enable_night_mode = "bed" + bed_id + "/enable_night_mode";
        topic_enable_unplugged_overlay = "bed" + bed_id + "/enable_unplugged_overlay";
        topic_reset_server_sync_state = "bed" + bed_id + "/reset_server_sync_state";
        topic_request_patient_name_from_hospital_patient_details_id = "isansys.com\\IsansysPatientDataLookup\\PatientDetailsRequest";
        topic_enable_spo2_spot_measurements = "bed" + bed_id + "/enable_spo2_spot_measurements";
        topic_enable_simple_heart_rate =  "bed" + bed_id + "/enable_simple_heart_rate";
        topic_display_timeout_in_seconds = "bed" + bed_id + "/display_timeout";
        topic_display_timeout_applies_to_patient_vitals = "bed" + bed_id + "/display_timeout_applies_to_patient_vitals";
        topic_request_database_sync_status = "bed" + bed_id + "/request_database_sync_status";
        topic_request_all_device_info_objects = "bed" + bed_id + "/request_all_device_info_objects";
        topic_delete_exported_databases = "bed" + bed_id + "/delete_exported_databases";
        topic_enable_gsm_mode = "bed" + bed_id + "/enable_gsm_mode";
        topic_enable_usa_only_mode = "bed" + bed_id + "/enable_usa_only_mode";
        topic_enable_developer_popup = "bed" + bed_id + "/enable_developer_popup";
        topic_show_server_syncing_popup = "bed" + bed_id + "/show_server_syncing_popup";
        topic_enable_pre_defined_annotations = "bed" + bed_id + "/pre_defined_annotations";
        topic_enable_auto_add_ews = "bed" + bed_id + "/auto_add_ews";
        topic_enable_dfu = "bed" + bed_id + "/dfu";
        topic_enable_show_numbers_on_battery_indicator = "bed" + bed_id + "/enable_show_numbers_on_battery_indicator";
        topic_enable_show_ip_address_on_wifi_popup = "bed" + bed_id + "/enable_show_ip_address_on_wifi_popup";
        topic_enable_run_devices_in_test_mode = "bed" + bed_id + "/enable_run_devices_in_test_mode";
        topic_enable_show_mac_on_device_status = "bed" + bed_id + "/enable_show_mac_on_device_status";
        topic_enable_lifetouch_activity_level = "bed" + bed_id + "/enable_lifetouch_activity_level";
        topic_enable_session_auto_resume = "bed" + bed_id + "/enable_session_auto_resume";
        topic_enable_auto_log_file_upload = "bed" + bed_id + "/enable_auto_log_file_upload";
        topic_enable_manufacturing_mode = "bed" + bed_id + "/enable_manufacturing_mode";
        topic_enable_back_camera = "bed" + bed_id + "/enable_back_camera";
        topic_enable_patient_orientation = "bed" + bed_id + "/enable_patient_orientation";
        topic_enable_wifi_logging = "bed" + bed_id + "/enable_wifi_logging";
        topic_enable_gsm_logging = "bed" + bed_id + "/enable_gsm_logging";
        topic_enable_database_logging = "bed" + bed_id + "/enable_database_logging";
        topic_enable_server_logging = "bed" + bed_id + "/enable_server_logging";
        topic_enable_battery_logging = "bed" + bed_id + "/enable_battery_logging";
        topic_lifetemp_measurement_interval_in_seconds = "bed" + bed_id + "/lifetemp_measurement_interval_in_seconds";
        topic_patient_orientation_measurement_interval_in_seconds = "bed" + bed_id + "/patient_orientation_measurement_interval_in_seconds";
        topic_enable_video_calls = "bed" + bed_id + "/enable_video_calls";
        topic_request_patient_specific_video_call_contacts = "isansys.com\\IsansysVideoCallContactsLookup\\VideoCallPatientContactsRequest";
        topic_server_requesting_video_call = "bed" + bed_id + "/request_video_call";
        topic_server_declined_video_call = "bed" + bed_id + "/decline_video_call";
        topic_server_update_video_call_connection_id = "bed" + bed_id + "/update_video_call_connection_id";
        topic_server_joining_video_call = "bed" + bed_id + "/join_video_call";
        topic_patient_on_gateway_requesting_video_call = "isansys.com\\IsansysVideoCallManager\\PatientOnGatewayRequestingVideoCall";
        topic_exit_gateway = "bed" + bed_id + "/exit_gateway";

        // These two include the Bed ID in the message so the Server only has to listen to a single topic
        topic_gateway_ping = "gateway_ping";
        topic_report_gateway_video_call_status = "report_video_call_status";

        have_valid_topics = true;
    }
    
    
    public void setHostName(String desired_host_name)
    {
        hostname = desired_host_name;
    }
    

    public void setPort(String desired_port)
    {
        port = desired_port;
    }

    
    public boolean connected()
    {
        if (mConnection != null)
        {
            return mConnection.isConnected();
        }
    	else
        {
            return false;
        }
    }


    public boolean isClientConnectedAndSending()
    {
        if (mConnection != null)
        {
            return (mConnection.isConnected() && pong_received);
        }
        else
        {
            return false;
        }
    }
    
	private void MakeSubscriptions()
	{
    	try
    	{
     		subscribeToTopicEnableServerLink();
            subscribeToTopicEnableDeviceSetupMode();
            subscribeToTopicEnableDeviceRawAccelerometerMode();
            subscribeToTopicEnablePatientNameLookup();
            subscribeToTopicEnablePeriodicSetupMode();
            subscribeToTopicEnableDummyDataMode();
            subscribeToTopicSetJsonArraySize();
            subscribeToSpO2LongTermMeasurementsTimeout();
            subscribeToBloodPressureLongTermMeasurementsTimeout();
            subscribeToWeightLongTermMeasurementsTimeout();
            subscribeToThirdPartyTemperatureLongTermMeasurementsTimeout();
            subscribeToTopicSetupModeLengthInSeconds();
            subscribeToTopicPeriodicSetupModeActiveTimeInSeconds();
            subscribeToTopicPeriodicSetupModePeriodTimeInSeconds();
            subscribeToTopicPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid();
            subscribeToTopicNoninPulseOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid();                    
            subscribeToTopicSetDummyDataModeMeasurementsPerTick();
            subscribeToTopicManualTimeSync();
            subscribeToTopicQrCodeUnlockUser();
            subscribeToTopicQrCodeUnlockAdmin();
            subscribeToTopicQrCodeUnlockFeatureEnable();
            subscribeToTopicExportDatabase();
            subscribeToTopicEmptyGatewayDatabase();
            subscribeToTopicResetGatewayAndUserInterfaceRunCounters();
            subscribeToTopicReportGatewayStatus();
            subscribeToTopicEnableWebserviceAuthentication();
            subscribeToTopicEnableWebserviceEncryption();
            subscribeToTopicGetFeaturesEnabled();
            subscribeToTopicEnableManualVitalSignsEntry();
            subscribeToTopicEnableCsvOutput();
            subscribeToTopicEnableNightMode();
            subscribeToTopicEnableUnpluggedOverlay();
            subscribeToTopicResetServerSyncStatus();
            subscribeToTopicEnableSpO2SpotMeasurements();
            subscribeToTopicEnableSimpleHeartRate();
            subscribeToTopicDisplayTimeoutInSeconds();
            subscribeToTopicDisplayTimeoutAppliesToPatientVitals();
            subscribeToTopicRequestDatabaseSyncStatus();
            subscribeToTopicRequestAllDeviceInfoObjects();
            subscribeToTopicDeleteAllExportedDatabases();
            subscribeToTopicEnableGsmMode();
            subscribeToTopicEnableUsaOnlyMode();
            subscribeToTopicEnableDeveloperPopup();
            subscribeToTopicShowServerSyncingPopup();
            subscribeToTopicEnablePredefinedAnnotations();
            subscribeToTopicEnableAutoAddEWS();
            subscribeToTopicDFU();
            // Feature Enabled
            subscribeToTopicEnableShowNumbersOnBatteryIndicator();
            subscribeToTopicEnableShowIpAddressOnWifiPopup();
            subscribeToTopicEnableRunDevicesInTestMode();
            subscribeToTopicEnableLifetouchActivityLevel();
            subscribeToTopicEnableShowMacAddress();
            subscribeToTopicEnableSessionAutoResume();
            subscribeToTopicEnableAutoUploadLogFilesToServer();
            subscribeToTopicEnableManufacturingMode();
            subscribeToTopicEnableUseBackCamera();
            subscribeToTopicEnablePatientOrientation();
            subscribeToTopicEnableWifiLogging();
            subscribeToTopicEnableGsmLogging();
            subscribeToTopicEnableDatabaseLogging();
            subscribeToTopicEnableServerLogging();
            subscribeToTopicEnableBatteryLogging();
            subscribeToTopicLifetempMeasurementIntervalInSeconds();
            subscribeToTopicPatientOrientationMeasurementIntervalInSeconds();
            subscribeToTopicEnableVideoCalls();
            // End of Feature Enabled

            // Video Call topics
            subscribeToTopicServerRequestingVideoCall();                                            // Incoming call request from Server
            subscribeToTopicServerJoiningVideoCall();                                               // Server connection to call
            subscribeToTopicServerDeclinedVideoCall();                                              // Server declined the Gateways call request
            subscribeToTopicUpdateVideoCallConnectionId();                                          // Server telling the Gateway the Contact Names logged in Browser ID (if any)

            subscribeToTopicExitGateway();

            // Whenever we connect, broadcast the current gateway status. Important on reconnect during a session in case device disconnection events were missed.
            Intent outgoing_intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
            outgoing_intent.putExtra("command", Commands.CMD_FROM_SERVER__REPORT_GATEWAY_STATUS.ordinal());
            gateway_context_interface.sendBroadcastIntent(outgoing_intent);
        }
    	catch(Exception e)
    	{
    		Log.e(TAG, "Subscriptions failed");
    		Log.e(TAG, e.toString());
    	}
	}

    
    public void connect()
    {
    	if (mConnection != null && have_valid_topics)
    	{
            mConnection.connect(success -> {
                if(success)
                {
                    MakeSubscriptions();

                    patient_gateway_interface.handleServerConnectedResult(true);
                }
                else
                {
                    disconnectRealTimeClient();

                    Log.d(TAG, "Connection failed");

                    patient_gateway_interface.handleServerConnectedResult(false);
                }
            }
            );
    	}
    	else
    	{
    		Log.d(TAG, "Cannot establish connection as no Topics have been initialised (no bed id)");
    	}
    }
    
    
    private void subscribeToTopicReportGatewayStatus()
    {
        mConnection.subscribe(topic_report_gateway_status, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__REPORT_GATEWAY_STATUS));
    }

    private void subscribeToTopicGetFeaturesEnabled()
    {
        mConnection.subscribe(topic_get_features_enabled, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__REPORT_FEATURES_ENABLED));
    }

    private void subscribeToTopicResetGatewayAndUserInterfaceRunCounters()
    {
        mConnection.subscribe(topic_reset_gateway_and_ui_run_counters, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__RESET_GATEWAY_AND_UI_RUN_COUNTERS));
    }

    private void subscribeToTopicEmptyGatewayDatabase()
    {
        mConnection.subscribe(topic_empty_gateway_database, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__EMPTY_LOCAL_DATABASE));
    }

    private void subscribeToTopicExportDatabase()
    {
        mConnection.subscribe(topic_export_database_to_tablet, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__EXPORT_LOCAL_DATABASE_TO_ANDROID_ROOT));
    }

    private void subscribeToTopicQrCodeUnlockAdmin()
    {
        mConnection.subscribe(topic_qr_code_unlock_admin, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__QR_CODE_UNLOCK_ADMIN));
    }

    private void subscribeToTopicQrCodeUnlockUser()
    {
        mConnection.subscribe(topic_qr_code_unlock_user, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__QR_CODE_UNLOCK_USER));
    }

    private void subscribeToTopicQrCodeUnlockFeatureEnable()
    {
        mConnection.subscribe(topic_qr_code_unlock_feature_enable, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__QR_CODE_UNLOCK_FEATURE_ENABLE));
    }

    private void subscribeToTopicManualTimeSync()
    {
        mConnection.subscribe(topic_manual_time_sync, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__MANUAL_TIME_SYNC));
    }

    private void subscribeToTopicSetDummyDataModeMeasurementsPerTick()
    {
        mConnection.subscribe(topic_set_dummy_data_mode_measurements_per_tick, MeasurementsPerTick.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                MeasurementsPerTick measurements_per_tick = (MeasurementsPerTick)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set Dummy Data Measurements Per Tick = " + measurements_per_tick.number);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK.ordinal());
                intent.putExtra("measurements_per_tick", measurements_per_tick.number);
                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }


    private void subscribeToTopicPercentageOfPoorSignalHeartBeatsBeforeMarkingAsInvalid()
    {
        mConnection.subscribe(topic_percentage_of_poor_signal_heart_beats_before_marking_as_invalid, Number.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                Number number = (Number)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set Max Percentage of Poor Signal Heart Beats before minute marked invalid = " + number.number);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID.ordinal());
                intent.putExtra("number", number.number);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }


    private void subscribeToTopicNoninPulseOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid()
    {
        mConnection.subscribe(topic_nonin_pulse_ox_number_invalid_intermediate_measurements_before_marking_as_invalid, Number.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                Number number = (Number)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set Max Number Nonin Pulse Ox Intermediate Measurements Invalid before minute marked invalid = " + number.number);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID.ordinal());
                intent.putExtra("number", number.number);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }

    private void subscribeToTopicSetupModeLengthInSeconds()
    {
        mConnection.subscribe(topic_setup_mode_length_in_seconds, LengthInSeconds.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                LengthInSeconds time = (LengthInSeconds)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set Setup Mode Time in Seconds = " + time.length_in_seconds);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_SETUP_MODE_TIME_IN_SECONDS.ordinal());
                intent.putExtra("length_in_seconds", time.length_in_seconds);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }


    private void subscribeToTopicPeriodicSetupModeActiveTimeInSeconds()
    {
        mConnection.subscribe(topic_periodic_setup_mode_active_time_in_seconds, LengthInSeconds.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                LengthInSeconds time = (LengthInSeconds)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set Periodic Setup Mode Active Time in Seconds = " + time.length_in_seconds);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS.ordinal());
                intent.putExtra("length_in_seconds", time.length_in_seconds);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }


    private void subscribeToTopicPeriodicSetupModePeriodTimeInSeconds()
    {
        mConnection.subscribe(topic_periodic_setup_mode_period_time_in_seconds, LengthInSeconds.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                LengthInSeconds time = (LengthInSeconds)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set Periodic Setup Mode Period Time in Seconds = " + time.length_in_seconds);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS.ordinal());
                intent.putExtra("length_in_seconds", time.length_in_seconds);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }


    private void subscribeToTopicDisplayTimeoutInSeconds()
    {
        mConnection.subscribe(topic_display_timeout_in_seconds, LengthInSeconds.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                LengthInSeconds time = (LengthInSeconds)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set Display Timeout in Seconds = " + time.length_in_seconds);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_DISPLAY_TIMEOUT_IN_SECONDS.ordinal());
                intent.putExtra("length_in_seconds", time.length_in_seconds);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }


    private void subscribeToTopicDisplayTimeoutAppliesToPatientVitals()
    {
        mConnection.subscribe(topic_display_timeout_applies_to_patient_vitals, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__SET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY));
    }


    private void subscribeToTopicSetJsonArraySize()
    {
        mConnection.subscribe(topic_set_json_array_size, JsonArraySize.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                JsonArraySize json_size = (JsonArraySize)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set JSON array size = " + json_size.array_size);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_JSON_ARRAY_SIZE.ordinal());

                intent.putExtra("array_size", json_size.array_size);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }


    private void processLongTermMeasurementTimeout(String topicUri, Object event, SensorType sensorType)
    {
        try
        {
            // when we get an event, we safely can cast to the type we specified previously
            LengthInSeconds time = (LengthInSeconds)event;

            Log.d(TAG, "Event received on " + topicUri + " : Set Long Term Measurements Time Out = " + time.length_in_seconds);

            Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
            intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_SECONDS.ordinal());
            intent.putExtra("sensor_type", sensorType.ordinal());
            intent.putExtra("length_in_seconds", time.length_in_seconds);

            gateway_context_interface.sendBroadcastIntent(intent);
        }
        catch(Exception e)
        {
            Log.e(TAG, "onEvent failed");
            Log.e(TAG, e.toString());
        }
    }

    private void subscribeToSpO2LongTermMeasurementsTimeout()
    {
        mConnection.subscribe(topic_set_spo2_long_term_measurements_timeout, LengthInSeconds.class, (topicUri, event) -> processLongTermMeasurementTimeout(topicUri, event, SensorType.SENSOR_TYPE__SPO2));
    }

    private void subscribeToBloodPressureLongTermMeasurementsTimeout()
    {
        mConnection.subscribe(topic_set_blood_pressure_long_term_measurements_timeout, LengthInSeconds.class, (topicUri, event) -> processLongTermMeasurementTimeout(topicUri, event, SensorType.SENSOR_TYPE__BLOOD_PRESSURE));
    }

    private void subscribeToWeightLongTermMeasurementsTimeout()
    {
        mConnection.subscribe(topic_set_weight_long_term_measurements_timeout, LengthInSeconds.class, (topicUri, event) -> processLongTermMeasurementTimeout(topicUri, event, SensorType.SENSOR_TYPE__WEIGHT_SCALE));
    }

    private void subscribeToThirdPartyTemperatureLongTermMeasurementsTimeout()
    {
        mConnection.subscribe(topic_set_third_party_temperature_long_term_measurements_timeout, LengthInSeconds.class, (topicUri, event) -> processLongTermMeasurementTimeout(topicUri, event, SensorType.SENSOR_TYPE__TEMPERATURE));
    }

    private void subscribeToTopicEnableDummyDataMode()
    {
        mConnection.subscribe(topic_enable_dummy_data_mode, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_DUMMY_DATA_MODE));
    }

    private void subscribeToTopicEnablePeriodicSetupMode()
    {
        mConnection.subscribe(topic_enable_periodic_setup_mode, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_PERIODIC_SETUP_MODE));
    }

    private void subscribeToTopicEnablePatientNameLookup()
    {
        mConnection.subscribe(topic_enable_patient_name_lookup, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_PATIENT_NAME_LOOKUP));
    }

    private void subscribeToTopicEnablePredefinedAnnotations()
    {
        mConnection.subscribe(topic_enable_pre_defined_annotations, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_PRE_DEFINED_ANNOTATIONS));
    }

    private void subscribeToTopicEnableAutoAddEWS()
    {
        mConnection.subscribe(topic_enable_auto_add_ews, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_AUTO_ADD_EWS));
    }

    private void subscribeToTopicDFU()
    {
        mConnection.subscribe(topic_enable_dfu, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_DFU));
    }

    private void subscribeToTopicEnableDeviceSetupMode()
    {
        mConnection.subscribe(topic_enable_device_setup_mode, EnabledAndSensorType.class, (topicUri, event) -> handleEnableDisableSetupMode(topicUri, (EnabledAndSensorType) event));
    }

    private void handleEnableDisableSetupMode(String topicUri, EnabledAndSensorType event)
    {
        try
        {
            Log.d(TAG, "Event received on " + topicUri + " : Setup mode enabled = " + event.enabled  + " : \"Do Not Stream Data\" flag = " + event.do_not_stream_data);

            SensorType type = SensorType.values()[event.sensor_type];
            DeviceInfo device_info = patient_gateway_interface.getDeviceInfoBySensorType(type);

            Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);

            if (event.enabled)
            {
                intent.putExtra("command", Commands.CMD_FROM_SERVER__START_DEVICE_SETUP_MODE.ordinal());
            }
            else
            {
                intent.putExtra("command", Commands.CMD_FROM_SERVER__STOP_DEVICE_SETUP_MODE.ordinal());
            }

            intent.putExtra("device_type", device_info.device_type.ordinal());
            intent.putExtra("do_not_stream_data", event.do_not_stream_data);

            gateway_context_interface.sendBroadcastIntent(intent);
        }
        catch (Exception e)
        {
            Log.e(TAG, "onEvent failed");
            Log.e(TAG, e.toString());
        }
    }

    private void subscribeToTopicEnableDeviceRawAccelerometerMode()
    {
        mConnection.subscribe(topic_enable_device_raw_accelerometer_mode, EnabledAndSensorType.class, (topicUri, event) -> HandleEnableDisableRawAccelerometerMode(topicUri, (EnabledAndSensorType) event));
    }

    private void HandleEnableDisableRawAccelerometerMode(String topicUri, EnabledAndSensorType event)
    {
        try
        {
            Log.d(TAG, "Event received on " + topicUri + " : Raw Accelerometer mode enabled = " + event.enabled + " : \"Do Not Stream Data\" flag = " + event.do_not_stream_data);

            SensorType type = SensorType.values()[event.sensor_type];
            DeviceInfo device_info = patient_gateway_interface.getDeviceInfoBySensorType(type);

            Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);

            if(event.enabled)
            {
                intent.putExtra("command", Commands.CMD_FROM_SERVER__START_DEVICE_RAW_ACCELEROMETER_MODE.ordinal());
            }
            else
            {
                intent.putExtra("command", Commands.CMD_FROM_SERVER__STOP_DEVICE_RAW_ACCELEROMETER_MODE.ordinal());
            }

            intent.putExtra("device_type", device_info.device_type.ordinal());
            intent.putExtra("do_not_stream_data", event.do_not_stream_data);

            gateway_context_interface.sendBroadcastIntent(intent);
        }
        catch(Exception e)
        {
            Log.e(TAG, "onEvent failed");
            Log.e(TAG, e.toString());
        }
    }

    private void subscribeToTopicEnableServerLink()
    {
        mConnection.subscribe(topic_enable_server_link, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_DATA_SYNCING_TO_SERVER));
    }
    
	private void subscribeToTopicEnableWebserviceAuthentication() 
	{
		mConnection.subscribe(topic_enable_webservice_authentication, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_WEBSERVICE_AUTHENTICATION));
	}
	
	private void subscribeToTopicEnableWebserviceEncryption() 
	{
		mConnection.subscribe(topic_enable_webservice_encryption, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_WEBSERVICE_ENCRYPTION));
	}

    private void subscribeToTopicEnableManualVitalSignsEntry()
    {
        mConnection.subscribe(topic_enable_manual_vital_signs_entry, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_MANUAL_VITAL_SIGNS_ENTRY));
    }

    private void subscribeToTopicEnableCsvOutput()
    {
        mConnection.subscribe(topic_enable_csv_output, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_CSV_OUTPUT));
    }

    private void subscribeToTopicEnableNightMode()
    {
        mConnection.subscribe(topic_enable_night_mode, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_NIGHT_MODE));
    }

    private void subscribeToTopicEnableSpO2SpotMeasurements()
    {
        mConnection.subscribe(topic_enable_spo2_spot_measurements, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS));
    }


    private void subscribeToTopicEnableSimpleHeartRate()
    {
        mConnection.subscribe(topic_enable_simple_heart_rate, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_SIMPLE_HEART_RATE_ALGORITHM));
    }

    private void subscribeToTopicEnableUnpluggedOverlay()
    {
        mConnection.subscribe(topic_enable_unplugged_overlay, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_UNPLUGGED_OVERLAY));
    }


    private void subscribeToTopicResetServerSyncStatus()
    {
        mConnection.subscribe(topic_reset_server_sync_state, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__RESET_SERVER_SYNC_STATUS));
    }


    private void subscribeToTopicRequestDatabaseSyncStatus()
    {
        mConnection.subscribe(topic_request_database_sync_status, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__REQUEST_DATABASE_SYNC_STATUS));
    }


    private void subscribeToTopicRequestAllDeviceInfoObjects()
    {
        mConnection.subscribe(topic_request_all_device_info_objects, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__REQUEST_ALL_DEVICE_INFO_OBJECTS));
    }


    private void subscribeToTopicDeleteAllExportedDatabases()
    {
        mConnection.subscribe(topic_delete_exported_databases, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__DELETE_EXPORTED_DATABASES));
    }


    private void subscribeToTopicEnableGsmMode()
    {
        mConnection.subscribe(topic_enable_gsm_mode, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_GSM_MODE));
    }


    private void subscribeToTopicEnableUsaOnlyMode()
    {
        mConnection.subscribe(topic_enable_usa_only_mode, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__SET_USA_MODE_ENABLED_STATUS));
    }


    private void subscribeToTopicEnableDeveloperPopup()
    {
        mConnection.subscribe(topic_enable_developer_popup, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__SET_DEVELOPER_POPUP_ENABLED_STATUS));
    }


    private void subscribeToTopicShowServerSyncingPopup()
    {
        mConnection.subscribe(topic_show_server_syncing_popup, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__SHOW_SERVER_SYNCING_POPUP));
    }

    private void subscribeToTopicEnableShowNumbersOnBatteryIndicator()
    {
        mConnection.subscribe(topic_enable_show_numbers_on_battery_indicator, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_SHOW_NUMBERS_ON_BATTERY_INDICATOR));
    }


    private void subscribeToTopicEnableShowIpAddressOnWifiPopup()
    {
        mConnection.subscribe(topic_enable_show_ip_address_on_wifi_popup, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_SHOW_IP_ADDRESS_ON_WIFI_POPUP));
    }


    private void subscribeToTopicEnableRunDevicesInTestMode()
    {
        mConnection.subscribe(topic_enable_run_devices_in_test_mode, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_RUN_DEVICES_IN_TEST_MODE));
    }


    private void subscribeToTopicEnableShowMacAddress()
    {
        mConnection.subscribe(topic_enable_show_mac_on_device_status, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_SHOW_MAC_ADDRESS));
    }


    private void subscribeToTopicEnableLifetouchActivityLevel()
    {
        mConnection.subscribe(topic_enable_lifetouch_activity_level, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_LIFETOUCH_ACTIVITY_LEVEL));
    }


    private void subscribeToTopicEnableSessionAutoResume()
    {
        mConnection.subscribe(topic_enable_session_auto_resume, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_SESSION_AUTO_RESUME));
    }


    private void subscribeToTopicEnableAutoUploadLogFilesToServer()
    {
        mConnection.subscribe(topic_enable_auto_log_file_upload, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_AUTO_UPLOAD_LOG_FILES_TO_SERVER));
    }


    private void subscribeToTopicEnableManufacturingMode()
    {
        mConnection.subscribe(topic_enable_manufacturing_mode, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_MANUFACTURING_MODE));
    }


    private void subscribeToTopicEnableUseBackCamera()
    {
        mConnection.subscribe(topic_enable_back_camera, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_USE_BACK_CAMERA));
    }


    private void subscribeToTopicEnablePatientOrientation()
    {
        mConnection.subscribe(topic_enable_patient_orientation, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_PATIENT_ORIENTATION));
    }


    private void subscribeToTopicEnableWifiLogging()
    {
        mConnection.subscribe(topic_enable_wifi_logging, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_WIFI_LOGGING));
    }


    private void subscribeToTopicEnableGsmLogging()
    {
        mConnection.subscribe(topic_enable_gsm_logging, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_GSM_LOGGING));
    }


    private void subscribeToTopicEnableDatabaseLogging()
    {
        mConnection.subscribe(topic_enable_database_logging, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_DATABASE_LOGGING));
    }


    private void subscribeToTopicEnableServerLogging()
    {
        mConnection.subscribe(topic_enable_server_logging, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_SERVER_LOGGING));
    }


    private void subscribeToTopicEnableBatteryLogging()
    {
        mConnection.subscribe(topic_enable_battery_logging, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_BATTERY_LOGGING));
    }


    public void subscribeToTopicLifetempMeasurementIntervalInSeconds()
    {
        mConnection.subscribe(topic_lifetemp_measurement_interval_in_seconds, LengthInSeconds.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                LengthInSeconds time = (LengthInSeconds)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set Lifetemp Measurement Interval in Seconds = " + time.length_in_seconds);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS.ordinal());
                intent.putExtra("length_in_seconds", time.length_in_seconds);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }


    public void subscribeToTopicPatientOrientationMeasurementIntervalInSeconds()
    {
        mConnection.subscribe(topic_patient_orientation_measurement_interval_in_seconds, LengthInSeconds.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                LengthInSeconds time = (LengthInSeconds)event;

                Log.d(TAG, "Event received on " + topicUri + " : Set Patient Orientation Measurement Interval in Seconds = " + time.length_in_seconds);

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SET_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS.ordinal());
                intent.putExtra("length_in_seconds", time.length_in_seconds);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }

    public void subscribeToTopicEnableVideoCalls()
    {
        mConnection.subscribe(topic_enable_video_calls, EnabledStatus.class, (topicUri, event) -> handleCommandWithEnable(topicUri, event, Commands.CMD_FROM_SERVER__ENABLE_VIDEO_CALLS));
    }

    public void subscribeToTopicExitGateway()
    {
        mConnection.subscribe(topic_exit_gateway, UnusedDummyClass.class, (topicUri, event) -> handleCommandNoParameters(topicUri, Commands.CMD_FROM_SERVER__EXIT_GATEWAY));
    }

    private void handleCommandNoParameters(String topicUri, Commands command)
    {
        try
        {
            Log.d(TAG, "Event received on " + topicUri);

            Intent outgoing_intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
            outgoing_intent.putExtra("command", command.ordinal());
            gateway_context_interface.sendBroadcastIntent(outgoing_intent);
        }
        catch (Exception e)
        {
            Log.e(TAG, "onEvent failed on topic : " + topicUri);
            Log.e(TAG, e.toString());
        }
    }


    private void handleCommandWithEnable(String topicUri, Object event, Commands command)
    {
        try
        {
            EnabledStatus request = (EnabledStatus)event;

            Log.d(TAG, "Event received on " + topicUri + " : Enabled = " + request.enabled);

            Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
            intent.putExtra("command", command.ordinal());
            intent.putExtra("enabled", request.enabled);
            gateway_context_interface.sendBroadcastIntent(intent);
        }
        catch (Exception e)
        {
            Log.e(TAG, "onEvent failed on topic : " + topicUri);
            Log.e(TAG, e.toString());
        }
    }


    public void subscribeToTopicReportPatientNameFromHospitalPatientDetailsId(String uniqueId)
    {
        final String topic = "isansys.com\\IsansysPatientDataLookup\\PatientDetailsResponse\\" + uniqueId;

        mConnection.subscribe(topic, PatientNameLookupFromHospitalPatientId.class, (topicUri, event) -> {
            try
            {
                // when we get an event, we safely can cast to the type we specified previously
                PatientNameLookupFromHospitalPatientId event_triggered = (PatientNameLookupFromHospitalPatientId)event;

/*
                // These are useful during testing, but must NOT be used in production as it means we have Patient Names in the log files
                Log.e(TAG, "Event received on " + topicUri + " : firstName = " + String.valueOf(event_triggered.firstName));
                Log.e(TAG, "Event received on " + topicUri + " : lastName = " + String.valueOf(event_triggered.lastName));
                Log.e(TAG, "Event received on " + topicUri + " : dob = " + String.valueOf(event_triggered.dob));
                Log.e(TAG, "Event received on " + topicUri + " : gender = " + String.valueOf(event_triggered.gender));
*/
                Log.d(TAG, "Event received on " + topicUri + " : status = " + event_triggered.status);

                Intent outgoing_intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                outgoing_intent.putExtra("command", Commands.CMD_FROM_SERVER__REPORT_PATIENT_NAME_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP.ordinal());

                outgoing_intent.putExtra("firstName", event_triggered.firstName);
                outgoing_intent.putExtra("lastName", event_triggered.lastName);
                outgoing_intent.putExtra("dob", event_triggered.dob);
                outgoing_intent.putExtra("gender", event_triggered.gender);

                outgoing_intent.putExtra("complete", event_triggered.complete);
                outgoing_intent.putExtra("status", event_triggered.status);
                gateway_context_interface.sendBroadcastIntent(outgoing_intent);

                if (event_triggered.complete)
                {
                    Log.d(TAG, "Lookup complete. Unsubscribing from temporary topic = " + topic);
                    mConnection.unsubscribe(topic);
                }
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed on " + topicUri);
                Log.e(TAG, e.toString());
            }
        });
    }

    public void subscribeToTopicVideoCallPatientContactsRequest(String uniqueId)
    {
        final String topic = "isansys.com\\IsansysVideoCallContactsLookup\\VideoCallPatientContactsResponse\\" + uniqueId;

        mConnection.subscribe(topic, PatientContactsResponse.class, (topicUri, event) -> {
            try
            {
                PatientContactsResponse response = (PatientContactsResponse)event;

                ArrayList<VideoCallContact> contactList = new Gson().fromJson(response.contactListAsJson, new TypeToken<ArrayList<VideoCallContact>>(){}.getType());

                Log.d(TAG, "Event received on " + topicUri + " : status = " + response.status);
                Log.d(TAG, "Event received on " + topicUri + " : statusMessage = " + response.statusMessage);
                Log.d(TAG, "Event received on " + topicUri + " : number of contacts = " + (contactList != null ? contactList.size() : 0));

                Intent outgoing_intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                outgoing_intent.putExtra("command", Commands.CMD_FROM_SERVER__REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_UPDATE.ordinal());
                outgoing_intent.putExtra("contactList", contactList);
                outgoing_intent.putExtra("status", response.status);
                outgoing_intent.putExtra("statusMessage", response.statusMessage);
                gateway_context_interface.sendBroadcastIntent(outgoing_intent);

                // Unsubscribing from temporary topic
                mConnection.unsubscribe(topic);
            }
            catch(Exception e)
            {
                Log.e(TAG, "onEvent failed");
                Log.e(TAG, e.toString());
            }
        });
    }

    private void subscribeToTopicServerRequestingVideoCall()
    {
        mConnection.subscribe(topic_server_requesting_video_call, VideoCallDetails.class, (topicUri, event) -> {
            try
            {
                VideoCallDetails videoCallDetails = (VideoCallDetails)event;

                Log.d(TAG, "Event received on " + topicUri + " : videoCallDetails = " + videoCallDetails.toString());

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__REQUEST_VIDEO_CALL.ordinal());
                intent.putExtra("video_call_details", videoCallDetails);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "subscribeToTopicRequestVideoCall : onEvent failed : " + e);
                Log.e(TAG, e.toString());
            }
        });
    }

    private void subscribeToTopicServerJoiningVideoCall()
    {
        mConnection.subscribe(topic_server_joining_video_call, VideoCallDetails.class, (topicUri, event) -> {
            try
            {
                VideoCallDetails videoCallDetails = (VideoCallDetails)event;

                Log.d(TAG, "Event received on " + topicUri + " : videoCallDetails = " + videoCallDetails.toString());

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__JOIN_VIDEO_CALL.ordinal());
                intent.putExtra("video_call_details", videoCallDetails);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "subscribeToTopicRequestVideoCall : onEvent failed : " + e);
                Log.e(TAG, e.toString());
            }
        });
    }

    private void subscribeToTopicUpdateVideoCallConnectionId()
    {
        mConnection.subscribe(topic_server_update_video_call_connection_id, VideoCallBrowserConnectionIdDetails.class, (topicUri, event) -> {
            try
            {
                VideoCallBrowserConnectionIdDetails payload = (VideoCallBrowserConnectionIdDetails)event;

                Log.d(TAG, "Event received on " + topicUri + " : json = " + gson.toJson(payload));

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__UPDATE_VIDEO_CALL_CONNECTION_ID.ordinal());
                intent.putExtra("connectionId", payload.connectionId);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "subscribeToTopicUpdateVideoCallConnectionId : onEvent failed : " + e);
                Log.e(TAG, e.toString());
            }
        });
    }

    private void subscribeToTopicServerDeclinedVideoCall()
    {
        mConnection.subscribe(topic_server_declined_video_call, VideoCallDeclined.class, (topicUri, event) -> {
            try
            {
                VideoCallDeclined payload = (VideoCallDeclined)event;

                Log.d(TAG, "Event received on " + topicUri + " : json = " + gson.toJson(payload));

                Intent intent = new Intent(INTENT__COMMANDS_FROM_SERVER);
                intent.putExtra("command", Commands.CMD_FROM_SERVER__SERVER_DECLINED_VIDEO_CALL.ordinal());
                intent.putExtra("contactThatDeclinedTheCall", payload.contactThatDeclinedTheCall);

                gateway_context_interface.sendBroadcastIntent(intent);
            }
            catch(Exception e)
            {
                Log.e(TAG, "subscribeToTopicServerDeclinedVideoCall : onEvent failed : " + e);
                Log.e(TAG, e.toString());
            }
        });
    }

    public void disconnectIfConnected()
    {
        if (connected())
        {
            Log.d(TAG, "mConnection.disconnect()");

            disconnectRealTimeClient();
        }
    }


    public void sendDeviceLeadsOffStatus(JsonObject json_object)
    {
        if (isClientConnectedAndSending())
        {
            try
            {
                mConnection.publish(topic_report_device_leadsoff, json_object.toString());
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }


    private void setClientPingTime()
    {
		dtLastPong = new java.util.Date();
    }
    

    public void sendGatewayPing()
    {
		//  If we have a Pong time (time we last heard from the Server, then test to see if it was over PING_TIMEOUT seconds ago.
        if (dtLastPong != null)
        {
        	Date dtNow = new Date();
        	long diff = dtNow.getTime() - dtLastPong.getTime();

        	long seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(diff);

            long PING_TIMEOUT = 20;
            if (seconds  > PING_TIMEOUT)
        	{
        		Log.d(TAG, "Too long since last PONG!" + dtLastPong.toString());
        		
        		// Doing a disconnect will cause the Gateway to try and re-establish the connection and set up all the Pub Subs again.
                disconnectRealTimeClient();

                patient_gateway_interface.forceResetWifi();

        		// No point continuing as the connection is now closed.
        		return;
        	}
        }
        else
        {
            // We don't have a timeout time, so set it to now - that way we have a 20 second timeout from when the PING gets sent.
            setClientPingTime();
        }

        Log.d(TAG, "sendGatewayPing");
        // Lets make an RPC call to the Server to check it really is there
        //  We need to do this as we do not get a fail on the Subscribe if the Server is not there (network unplugged/disabled).
        mConnection.pingConnection("http://isansys.com/procedures/#wamp_ping",
                success -> {
                    if (success)
                    {
                        Log.d(TAG, "PONG received");
                        //  We have the correct response from the server, so reset our timeout time.
                        setClientPingTime();

                        pong_received = true;

                        String json_data = patient_gateway_interface.getJsonForGatewayPing();

                        Log.d(TAG, "sendGatewayPing JSON data : " + json_data);

                        if (connected())
                        {
                            try
                            {
                                mConnection.publish(topic_gateway_ping, json_data);
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, e.toString());
                            }
                        }
                    }
                }
        );
    }


    private void disconnectRealTimeClient()
    {
        mConnection.disconnect();
        dtLastPong = null;
        pong_received = false;
    }


    private void sendJsonDataToTopic(String topic, String json_data)
    {
        if (isClientConnectedAndSending())
        {
            try
            {
                mConnection.publish(topic, json_data);
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }


    public void sendDeviceSetupModeData(String json_data)
    {
        sendJsonDataToTopic(topic_device_setup_mode_samples, json_data);
    }


    public void sendDeviceRawAccelerometerModeData(String json_data)
    {
        sendJsonDataToTopic(topic_device_raw_accelerometer_mode_sample, json_data);
    }


    public void sendDeviceSetupModeEnabledState(JsonObject json_object)
    {
        if (isClientConnectedAndSending())
        {
            try
            {
                mConnection.publish(topic_report_device_setup_mode_enabled, json_object.toString());
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }


    public void sendDeviceRawAccelerometerModeEnabledState(JsonObject json_object)
    {
        if (isClientConnectedAndSending())
        {
            try
            {
                mConnection.publish(topic_report_device_raw_accelerometer_mode_enabled, json_object.toString());
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }


    public void sendGatewayStatus(String json_data)
    {
        Log.d(TAG, "sendGatewayStatus : " + json_data);

        if (isClientConnectedAndSending())
        {
        	try
        	{
	            // JSON array of Gateway status
	            mConnection.publish(topic_gateway_status_response, json_data);
        	}
            catch(Exception e)
            {
            	Log.e(TAG, e.toString());
            }
        }
    }


    public void sendFeaturesEnabledStatus(String json_data)
    {
        Log.d(TAG, "sendFeaturesEnabledStatus : " + json_data);

        if (isClientConnectedAndSending())
        {
            try
            {
                // JSON array of Gateway status
                mConnection.publish(topic_report_features_enabled, json_data);
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }


    public void sendDatabaseSyncStatus(String json_data)
    {
        if (isClientConnectedAndSending())
        {
        	try
        	{
        		// JSON array of Gateway Database status
        		mConnection.publish(topic_report_database_status, json_data);
        	}
            catch(Exception e)
            {
            	Log.e(TAG, e.toString());
            }
        }
    }
    
    
    public void sendWebserviceResult(String json_data)
    {
        if (isClientConnectedAndSending())
        {
        	try
        	{
	            // JSON array of Gateway Database status
	            mConnection.publish(topic_report_webservice_result, json_data);
        	}
            catch(Exception e)
            {
            	Log.e(TAG, e.toString());
            }
        }
    }


    public void requestPatientNameFromHospitalPatientDetailsId(JsonObject json_object)
    {
        if (isClientConnectedAndSending())
        {
            try
            {
                mConnection.publish(topic_request_patient_name_from_hospital_patient_details_id, json_object.toString());
            }
            catch(Exception e)
            {
                Log.e(TAG, "requestPatientNameFromHospitalPatientDetailsId Exception : " + e);
            }
        }
    }


    public void requestPatientSpecificVideoCallContacts(JsonObject json_object)
    {
        if (isClientConnectedAndSending())
        {
            try
            {
                Log.e(TAG, "requestPatientSpecificVideoCallContacts : " + json_object.toString());

                mConnection.publish(topic_request_patient_specific_video_call_contacts, json_object.toString());
            }
            catch(Exception e)
            {
                Log.e(TAG, "requestPatientSpecificVideoCallContacts Exception : " + e);
            }
        }
    }


    public void sendGatewayVideoCallStatus(JsonObject json_object)
    {
        if (isClientConnectedAndSending())
        {
            try
            {
                mConnection.publish(topic_report_gateway_video_call_status, json_object.toString());
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }


    public void sendPatientOnGatewayRequestingVideoCall(String json_data)
    {
        Log.d(TAG, "sendPatientOnGatewayRequestingVideoCall : " + json_data);

        if (isClientConnectedAndSending())
        {
            try
            {
                mConnection.publish(topic_patient_on_gateway_requesting_video_call, json_data);
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }

    public RealTimeServer getRealTimeServerType()
    {
        if(mConnection == null)
        {
            return RealTimeServer.INVALID;
        }
        else if(mConnection.getClass().equals(WampClient.class))
        {
            return RealTimeServer.WAMP;
        }
        else if(mConnection.getClass().equals(MqttClient.class))
        {
            return RealTimeServer.MQTT;
        }
        else
        {
            return RealTimeServer.INVALID;
        }
    }


    public void setRealTimeServerType(RealTimeServer server_type)
    {
        switch(server_type)
        {
            case WAMP:
            {
                mConnection = new WampClient(hostname, port, Log, gateway_unique_id);
            }
            break;

            case MQTT:
            {
                mConnection = new MqttClient(hostname, port, Log, gateway_unique_id, gateway_context_interface.getAppContext(), true);
            }
            break;

            case INVALID:
            {
                mConnection = null;

                return;
            }
        }
    }


    // We want PubSub events delivered to us in JSON payload to be automatically converted to this domain POJO. We specify this class later when we subscribe.
    private static class EnabledStatus
    {
        public boolean enabled;
    }

    private static class EnabledAndSensorType
    {
        public boolean enabled;
        public int sensor_type;
        public boolean do_not_stream_data;

        // Default to FALSE. If the Server wants to NOT stream the data then it has to set do_not_stream_data = true
        public EnabledAndSensorType()
        {
            do_not_stream_data = false;
        }
    }

    private static class JsonArraySize
    {
    	public int array_size;
    }
    
    private static class MeasurementsPerTick
    {
        public int number;
    }
    
    private static class UnusedDummyClass
    {
        public boolean dummy;
    }
        
    private static class LengthInSeconds
    {
        public int length_in_seconds;
    }

    private static class Number
    {
        public int number;
    }

    private static class PatientNameLookupFromHospitalPatientId
    {
        public String bedId;

        public String firstName;
        public String lastName;
        public String dob;                                                                          //  yyyy-MM-dd
        public String gender;                                                                       // "M" or "F" or "?"

        public boolean complete;
        public int status;
    }

    private static class PatientContactsResponse
    {
        public String contactListAsJson;

        public int status;
        public String statusMessage;
    }

    public static class VideoCallDeclined
    {
        public int bedId;
        public VideoCallContact contactThatDeclinedTheCall;
    }

    private static class VideoCallBrowserConnectionIdDetails
    {
        public int bedId;
        public String connectionId;
    }

}
