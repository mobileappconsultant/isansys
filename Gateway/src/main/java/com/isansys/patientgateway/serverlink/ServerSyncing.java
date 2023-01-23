package com.isansys.patientgateway.serverlink;

import android.content.ContentResolver;
import android.os.Handler;
import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.QueryType;
import com.isansys.common.enums.SensorType;
import com.isansys.common.enums.VideoCallStatus;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.GenericStartStopTimer;
import com.isansys.patientgateway.LocalDatabaseStorage;
import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.RowsPending;
import com.isansys.patientgateway.Settings;
import com.isansys.patientgateway.database.ServerSyncTableObserver;
import com.isansys.patientgateway.database.contentprovider.IsansysPatientGatewayContentProvider;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.patientgateway.remotelogging.RemoteLogging;
import com.isansys.patientgateway.serverlink.constants.CheckServerLinkStatus;
import com.isansys.patientgateway.serverlink.constants.ServerSyncingDataUploadPoint;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ServerSyncing
{
    public static class SessionInformation
    {
        public CheckServerLinkStatus patientDetailSendToServer_Status;
        public CheckServerLinkStatus deviceInfoSendToServer_Status;
        public CheckServerLinkStatus startPatientSessionSendToServer_Status;
        public CheckServerLinkStatus startDeviceSessionSendToServer_Status;
        public CheckServerLinkStatus endPatientSessionSendToServer_Status;
        public CheckServerLinkStatus endDeviceSessionSendToServer_Status;

        public CheckServerLinkStatus auditTrailSendToServer_Status;

        SessionInformation()
        {
            reset();
        }

        void reset()
        {
            patientDetailSendToServer_Status = CheckServerLinkStatus.IDLE;
            deviceInfoSendToServer_Status = CheckServerLinkStatus.IDLE;
            startPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
            startDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
            endPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
            endDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
            auditTrailSendToServer_Status = CheckServerLinkStatus.IDLE;
        }
    }


    private final RemoteLogging logger;
    private final RemoteLoggingWithEnable Log;
    private final String TAG = "ServerSyncing";
    private final DeviceInfoManager device_info_manager;

    private final ServerLink server_interface;
    
    public final SessionInformation currentSessionInformation;

    private final LocalDatabaseStorage local_database_storage;

    private final PatientGatewayInterface patient_gateway_interface;

    private final ContentResolver content_resolver;

    public final ServerSyncTableObserver server_sync_table_observer;

    private final Settings gateway_settings;

    private Timer server_ping_timer;

    private boolean sync_status_test_mode;

    private Timer reset_send_status_of_data_that_failed_to_send;

    private final static int reset_send_status_of_data_that_failed_to_send_time = (int)(10 * DateUtils.MINUTE_IN_MILLIS);


    public ServerSyncing(ContextInterface context_interface,
                            RemoteLogging logger,
                            Settings gateway_settings,
                            DeviceInfoManager device_info_manager,
                            LocalDatabaseStorage database_storage,
                            String passed_in_gateway_unique_id,
                            PatientGatewayInterface patient_gateway_interface,
                            boolean enable_logs,
                            ServerSyncStatus sync_status)
    {
        // currentSessionInformation is used to check if ServerLink is currently checking the given session information
        // ServerLink updates the value of each sessionInformation
        this.logger = logger;
        Log = new RemoteLoggingWithEnable(logger, enable_logs);
        currentSessionInformation = new SessionInformation();

        server_sync_status = sync_status;

        this.patient_gateway_interface = patient_gateway_interface;

        this.server_interface = new ServerLink(this, context_interface, logger, database_storage, passed_in_gateway_unique_id, patient_gateway_interface, enable_logs, gateway_settings, server_sync_status);

        this.device_info_manager = device_info_manager;

        this.local_database_storage = database_storage;

        this.gateway_settings = gateway_settings;

        content_resolver = context_interface.getAppContext().getContentResolver();

        Handler dataChangeHandler = new Handler();
        server_sync_table_observer = new ServerSyncTableObserver(logger, this, dataChangeHandler, gateway_settings, device_info_manager, local_database_storage, patient_gateway_interface, enable_logs, server_sync_status);
        registerGatewayContentObservers();

        sync_status_test_mode = false;

        setupResetSendStatusTimer(reset_send_status_of_data_that_failed_to_send_time);
    }


    private final ServerSyncStatus server_sync_status;


    private void setupResetSendStatusTimer(int initial_delay)
    {
        Log.d(TAG, "setupResetSendStatusTimer");

        reset_send_status_of_data_that_failed_to_send = new Timer("reset_send_status_of_data_that_failed_to_send");

        reset_send_status_of_data_that_failed_to_send.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Reset 'Server Sync Failed Data' timer fired");

                resetDatabaseFailedToSendStatus();
            }
        }, initial_delay, reset_send_status_of_data_that_failed_to_send_time);
    }


    private void resetDatabaseFailedToSendStatus()
    {
        local_database_storage.resetFailedToSendStatus();

        // If all data are marked as failed to sync then need to trigger the State Machine for sending new syncable data
        server_sync_table_observer.restartServerSyncAfterResettingDataFailedToSend();
    }


    public void resetDatabaseFailedToSendStatusAndResetTimer()
    {
        Log.d(TAG, "resetDatabaseFailedToSendStatusAndResetTimer");

        reset_send_status_of_data_that_failed_to_send.cancel();
        setupResetSendStatusTimer(0);
    }


    public void configureRealTimeServerIfRequired()
    {
        if (gateway_settings.getRealTimeLinkEnabledStatus())
        {
            server_interface.configureRealTimeServer();

            setupServerConnectionAndPingTimer();
        }
        else
        {
            server_interface.disconnectRealTimeServerIfConnected();
        }

        patient_gateway_interface.reportRealTimeLinkEnableStatus();
    }


    private void setupServerConnectionAndPingTimer()
    {
        int SERVER_PING_TIME_IN_SECONDS = 10;

        int interval = SERVER_PING_TIME_IN_SECONDS * (int) DateUtils.SECOND_IN_MILLIS;

        // Cancel the previous timer before starting another on
        GenericStartStopTimer.cancelTimer(server_ping_timer, logger);

        server_ping_timer = new Timer("server_connection_and_ping_timer");

        /* Schedule with an interval between each task run - used to be scheduleAtFixedRate, but that can
        * result in multiple tasks building up in a backlog if the task doesn't complete quickly enough
        */
        server_ping_timer.schedule(new TimerTask()
        {
            public void run()
            {
                connectToServerAndSendServerPingIfNeeded();
            }
        }, 10 * (int) DateUtils.SECOND_IN_MILLIS, interval);                                                  // Wait 10 seconds before starting the Ping timer
    }


    private void connectToServerAndSendServerPingIfNeeded()
    {
        // If Wifi/GSM connected, and WAMP enabled in Settings, and Server address setup setup
        if (patient_gateway_interface.isConnectedToNetwork()
                && gateway_settings.isServerAddressSet()
                && gateway_settings.getRealTimeLinkEnabledStatus())
        {
            if (isRealTimeServerConnected())
            {
                sendGatewayPingToServer();
            }
            else
            {
                server_interface.connectToRealTimeServer();
            }
        }
        else
        {
            // Wifi not connected, or Server not enabled, or IP address not setup yet
            Log.d(TAG, "connectToServerAndSendServerPingIfNeeded : Wifi not connected, or Server not enabled, or IP address not setup yet ");
        }
    }


    public boolean isRealTimeServerConfiguredAndConnected()
    {
        if (patient_gateway_interface.isConnectedToNetwork() &&                                     // WIFI is connected
                gateway_settings.isServerAddressSet() &&                                            // Server address has been set
                gateway_settings.isGatewayAssignedWardAndBedSet() &&                                // Gateway Bed ID has been set
                isRealTimeServerConnected()                                                         // Connected to WAMP or MQTT
        )
        {
            return true;
        }

        return false;
    }


    /*
     * @ charger_unplugged : boolean = set to False if charger is unplugged. This variable is updated to "False" every battery reading if tablet is not charger_unplugged to charger.
     * @ plugged_and_charging : boolean = set to True if Tablet is putting charge in. This variable is updated on every battery reading.
     * @ battery_below_50_percent : boolean = set to True if tablet battery percentage is less than 50. This variable is updated every battery reading to True is battery level is less than 50
     * @ tablet_charged_within_period : boolean = set to False if tablet isn't charged for specified period of time (6 hours)
     * @ last_charged_time : long = time stamp in ms for last time charged. "0" if not charged with in 6 hours.
     * @ valid_updated_tablet_charge_status : boolean = True if the check for last charge time is done. This is updated every five minutes.

     * @ NOTE : tablet_charged_within_period and last_charged_time is updated once every 5 minutes. During other time, Invalid timestamp = "1" is sent.
     * @ NOTE : if timestamp = "1" then ignore tablet_charged_within_period and last_charged_time variable.
     */
    private void sendGatewayPingToServer()
    {
        server_interface.sendGatewayPing();
    }


    public void sendDatabaseRowsPendingToServer(ServerSyncStatus server_sync_status)
    {
        JsonObject sync_status = new JsonObject();

        // Add Session Info
        sync_status.addProperty("patient_details_rows_pending", new Gson().toJson(server_sync_status.patient_details));

        sync_status.addProperty("device_info_rows_pending", new Gson().toJson(server_sync_status.device_info));

        sync_status.addProperty("start_patient_session_rows_pending", new Gson().toJson(server_sync_status.start_patient_session));
        sync_status.addProperty("start_device_session_rows_pending", new Gson().toJson(server_sync_status.start_device_session));

        sync_status.addProperty("end_device_session_rows_pending", new Gson().toJson(server_sync_status.end_device_session));
        sync_status.addProperty("end_patient_session_rows_pending", new Gson().toJson(server_sync_status.end_patient_session));

        sync_status.addProperty("patient_session_fully_synced_rows_pending", new Gson().toJson(server_sync_status.patient_session_fully_synced));

        // Audit trail
        sync_status.addProperty("auditable_events_rows_pending", new Gson().toJson(server_sync_status.auditable_events_rows_pending));

        // Connection events
        sync_status.addProperty("active_session_connection_event_rows_pending", new Gson().toJson(server_sync_status.active_session_connection_event_rows_pending));
        sync_status.addProperty("old_session_connection_event_rows_pending", new Gson().toJson(server_sync_status.old_session_connection_event_rows_pending));

        // Lifetouch
        sync_status.addProperty("active_session_lifetouch_heart_rate_rows_pending", new Gson().toJson(server_sync_status.active_session_lifetouch_heart_rate_rows_pending));
        sync_status.addProperty("active_session_lifetouch_heart_beat_rows_pending", new Gson().toJson(server_sync_status.active_session_lifetouch_heart_beat_rows_pending));
        sync_status.addProperty("active_session_lifetouch_respiration_rate_rows_pending", new Gson().toJson(server_sync_status.active_session_lifetouch_respiration_rate_rows_pending));
        sync_status.addProperty("active_session_lifetouch_setup_mode_rows_pending", new Gson().toJson(server_sync_status.active_session_lifetouch_setup_mode_rows_pending));
        sync_status.addProperty("active_session_lifetouch_raw_accelerometer_mode_rows_pending", new Gson().toJson(server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending));
        sync_status.addProperty("active_session_lifetouch_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_lifetouch_battery_measurement_rows_pending));
        sync_status.addProperty("active_session_lifetouch_patient_orientation_rate_rows_pending", new Gson().toJson(server_sync_status.active_session_lifetouch_patient_orientation_rows_pending));

        // Lifetemp
        sync_status.addProperty("active_session_lifetemp_temperature_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending));
        sync_status.addProperty("active_session_lifetemp_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_lifetemp_battery_measurement_rows_pending));

        // Pulse Oximeter
        sync_status.addProperty("active_session_pulse_ox_spo2_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending));
        sync_status.addProperty("active_session_pulse_ox_intermediate_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending));
        sync_status.addProperty("active_session_pulse_ox_setup_mode_rows_pending", new Gson().toJson(server_sync_status.active_session_pulse_ox_setup_mode_rows_pending));
        sync_status.addProperty("active_session_pulse_ox_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending));

        // Blood Pressure
        sync_status.addProperty("active_session_blood_pressure_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_blood_pressure_measurement_rows_pending));
        sync_status.addProperty("active_session_blood_pressure_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending));

        // Weight Scales
        sync_status.addProperty("active_session_weight_scale_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_weight_scale_measurement_rows_pending));
        sync_status.addProperty("active_session_weight_scale_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.active_session_weight_scale_battery_measurement_rows_pending));

        // Manual Vitals
        sync_status.addProperty("active_session_manually_entered_heart_rate_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_heart_rate_rows_pending));
        sync_status.addProperty("active_session_manually_entered_respiration_rate_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_respiration_rate_rows_pending));
        sync_status.addProperty("active_session_manually_entered_temperature_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_temperature_rows_pending));
        sync_status.addProperty("active_session_manually_entered_spo2_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_spo2_rows_pending));
        sync_status.addProperty("active_session_manually_entered_blood_pressure_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_blood_pressure_rows_pending));
        sync_status.addProperty("active_session_manually_entered_weight_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_weight_rows_pending));
        sync_status.addProperty("active_session_manually_entered_consciousness_level_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_consciousness_level_rows_pending));
        sync_status.addProperty("active_session_manually_entered_supplemental_oxygen_level_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending));
        sync_status.addProperty("active_session_manually_entered_annotation_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_annotation_rows_pending));
        sync_status.addProperty("active_session_manually_entered_capillary_refill_time_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_capillary_refill_time_rows_pending));
        sync_status.addProperty("active_session_manually_entered_respiration_distress_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_respiration_distress_rows_pending));
        sync_status.addProperty("active_session_manually_entered_family_or_nurse_concern_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_family_or_nurse_concern_rows_pending));
        sync_status.addProperty("active_session_manually_entered_urine_output_rows_pending", new Gson().toJson(server_sync_status.active_session_manually_entered_urine_output_rows_pending));

        sync_status.addProperty("active_session_setup_mode_log_rows_pending", new Gson().toJson(server_sync_status.active_session_setup_mode_log_rows_pending));
        sync_status.addProperty("active_session_early_warning_score_rows_pending", new Gson().toJson(server_sync_status.active_session_early_warning_score_rows_pending));


        sync_status.addProperty("old_session_lifetouch_heart_rate_rows_pending", new Gson().toJson(server_sync_status.old_session_lifetouch_heart_rate_rows_pending));
        sync_status.addProperty("old_session_lifetouch_heart_beat_rows_pending", new Gson().toJson(server_sync_status.old_session_lifetouch_heart_beat_rows_pending));
        sync_status.addProperty("old_session_lifetouch_respiration_rate_rows_pending", new Gson().toJson(server_sync_status.old_session_lifetouch_respiration_rate_rows_pending));
        sync_status.addProperty("old_session_lifetouch_setup_mode_rows_pending", new Gson().toJson(server_sync_status.old_session_lifetouch_setup_mode_rows_pending));
        sync_status.addProperty("old_session_lifetouch_raw_accelerometer_mode_rows_pending", new Gson().toJson(server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending));
        sync_status.addProperty("old_session_lifetouch_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_lifetouch_battery_measurement_rows_pending));
        sync_status.addProperty("old_session_lifetouch_patient_orientation_rate_rows_pending", new Gson().toJson(server_sync_status.old_session_lifetouch_patient_orientation_rows_pending));

        sync_status.addProperty("old_session_lifetemp_temperature_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending));
        sync_status.addProperty("old_session_lifetemp_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_lifetemp_battery_measurement_rows_pending));

        sync_status.addProperty("old_session_pulse_ox_spo2_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending));
        sync_status.addProperty("old_session_pulse_ox_intermediate_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending));
        sync_status.addProperty("old_session_pulse_ox_setup_mode_rows_pending", new Gson().toJson(server_sync_status.old_session_pulse_ox_setup_mode_rows_pending));
        sync_status.addProperty("old_session_pulse_ox_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending));

        sync_status.addProperty("old_session_blood_pressure_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_blood_pressure_measurement_rows_pending));
        sync_status.addProperty("old_session_blood_pressure_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending));

        sync_status.addProperty("old_session_weight_scale_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_weight_scale_measurement_rows_pending));
        sync_status.addProperty("old_session_weight_scale_battery_measurement_rows_pending", new Gson().toJson(server_sync_status.old_session_weight_scale_battery_measurement_rows_pending));

        sync_status.addProperty("old_session_manually_entered_heart_rate_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_heart_rate_rows_pending));
        sync_status.addProperty("old_session_manually_entered_respiration_rate_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_respiration_rate_rows_pending));
        sync_status.addProperty("old_session_manually_entered_temperature_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_temperature_rows_pending));
        sync_status.addProperty("old_session_manually_entered_spo2_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_spo2_rows_pending));
        sync_status.addProperty("old_session_manually_entered_blood_pressure_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_blood_pressure_rows_pending));
        sync_status.addProperty("old_session_manually_entered_blood_pressure_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_weight_rows_pending));
        sync_status.addProperty("old_session_manually_entered_consciousness_level_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_consciousness_level_rows_pending));
        sync_status.addProperty("old_session_manually_entered_supplemental_oxygen_level_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending));
        sync_status.addProperty("old_session_manually_entered_annotation_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_annotation_rows_pending));
        sync_status.addProperty("old_session_manually_entered_capillary_refill_time_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_capillary_refill_time_rows_pending));
        sync_status.addProperty("old_session_manually_entered_respiration_distress_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_respiration_distress_rows_pending));
        sync_status.addProperty("old_session_manually_entered_family_or_nurse_concern_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_family_or_nurse_concern_rows_pending));
        sync_status.addProperty("old_session_manually_entered_urine_output_rows_pending", new Gson().toJson(server_sync_status.old_session_manually_entered_urine_output_rows_pending));

        sync_status.addProperty("old_session_setup_mode_log_rows_pending", new Gson().toJson(server_sync_status.old_session_setup_mode_log_rows_pending));
        sync_status.addProperty("old_session_early_warning_score_rows_pending", new Gson().toJson(server_sync_status.old_session_early_warning_score_rows_pending));


        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.sendDatabaseSyncStatus(sync_status);
        }
    }

    public boolean isRealTimeServerConnected()
    {
        return server_interface.isRealTimeServerConnected();
    }


    public void sendDeviceSetupModeData(int server_patient_session_id, DeviceInfo device_info, int scaling_multiplier)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            if (device_info.queue_setup_mode_datapoints_for_server.size() > 0)
            {
                // Create JSON array and populate it with setup data
                JsonArray json_array = new JsonArray();

                for (MeasurementSetupModeDataPoint setup_data : device_info.queue_setup_mode_datapoints_for_server)
                {
                    JsonObject setup_dataPoint = new JsonObject();
                    setup_dataPoint.addProperty("data", setup_data.sample * scaling_multiplier);
                    setup_dataPoint.addProperty("timestamp", setup_data.timestamp_in_ms);
                    json_array.add(setup_dataPoint);
                }

                // Add "points" string in front of message
                JsonObject json_data = new JsonObject();
                json_data.addProperty("server_patient_session_id", server_patient_session_id);
                json_data.addProperty("sensor_type", device_info.sensor_type.ordinal());
                json_data.addProperty("device_type", device_info.device_type.ordinal());
                json_data.add("points", json_array);

                server_interface.sendDeviceSetupModeData(json_data);
            }
        }

        device_info.queue_setup_mode_datapoints_for_server.clear();
    }


    public void sendDeviceRawAccelerometerModeEnabledStateIfServerConnected(int server_patient_session_id, DeviceInfo device_info, boolean status)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.sendDeviceRawAccelerometerModeEnabledState(server_patient_session_id, device_info.sensor_type, status);
        }
    }


    public void sendDeviceRawAccelerometerModeDataIfServerConnected(JsonObject json_data)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.sendDeviceRawAccelerometerModeData(json_data);
        }
    }


    public void sendDeviceLeadsOffStatusIfServerConnected(int server_patient_session_id, DeviceInfo device_info, boolean status)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.sendDeviceLeadsOffStatus(server_patient_session_id, device_info, status);
        }
    }


    public void requestPatientNameFromHospitalPatientDetailsIdIfServerConnected(String hospital_patient_id, String bed_id, String unique_id)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.requestPatientNameFromHospitalPatientDetailsId(hospital_patient_id, bed_id, unique_id);
        }
    }

    public void requestPatientSpecificVideoCallContactsIfServerConnected(int servers_patient_session_id, String bed_id, String unique_id)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.requestPatientSpecificVideoCallContacts(servers_patient_session_id, bed_id, unique_id);
        }
    }

    public void sendGatewayStatusIfServerConnected(JsonObject json_object)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.sendGatewayStatus(json_object);
        }
    }


    public void sendFeaturesEnabledStatusIfServerConnected()
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.sendFeaturesEnabledStatus();
        }
    }


    public void sendDeviceSetupModeEnabledState(int server_patient_session_id, SensorType sensor_type, boolean status)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.sendDeviceSetupModeEnabledState(server_patient_session_id, sensor_type, status);
        }
    }


    public void readAmountInDatabaseAndReport()
    {
        long time_taken_for_session_info;
        long time_taken_for_active_session;
        long time_taken_for_historical_session;
        long total_time;

        Log.i(TAG, "readAmountInDatabaseAndReport start");

        time_taken_for_session_info = readAmountOfSessionDetailsDataPendingInDatabase();

        time_taken_for_active_session = readAmountOfActiveSessionDataPendingInDatabase();

        time_taken_for_historical_session = readAmountOfHistoricalSessionDataPendingInDatabase();

        total_time = time_taken_for_session_info + time_taken_for_active_session + time_taken_for_historical_session;

        Log.i(TAG, "readAmountInDatabaseAndReport took " + total_time + " ms. Session Info = " + time_taken_for_session_info + " : Active = " + time_taken_for_active_session + " : Historical = " + time_taken_for_historical_session);

        dummyServerSyncStatusToLog(server_sync_status);

        if(sync_status_test_mode)
        {
            patient_gateway_interface.reportDatabaseStatus(new ServerSyncStatus(1));
        }
        else
        {
            patient_gateway_interface.reportDatabaseStatus(server_sync_status);
        }
    }


    private void dummyServerSyncStatusToLog(ServerSyncStatus server_sync_status)
    {
        StringBuilder string_builder = new StringBuilder();

        if (server_sync_status.patient_details.getTotalRowsPending() > 0)
        {
            string_builder.append("patient_details.rows_pending_syncable = ").append(server_sync_status.patient_details.rows_pending_syncable).append(". ");
            string_builder.append("patient_details.rows_pending_non_syncable = ").append(server_sync_status.patient_details.rows_pending_non_syncable).append(". ");
            string_builder.append("patient_details.rows_pending_but_failed = ").append(server_sync_status.patient_details.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.device_info.getTotalRowsPending() > 0)
        {
            string_builder.append("device_info.rows_pending_syncable = ").append(server_sync_status.device_info.rows_pending_syncable).append(". ");
            string_builder.append("device_info.rows_pending_non_syncable = ").append(server_sync_status.device_info.rows_pending_non_syncable).append(". ");
            string_builder.append("device_info.rows_pending_but_failed = ").append(server_sync_status.device_info.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.start_patient_session.getTotalRowsPending() > 0)
        {
            string_builder.append("start_patient_session.rows_pending_syncable = ").append(server_sync_status.start_patient_session.rows_pending_syncable).append(". ");
            string_builder.append("start_patient_session.rows_pending_non_syncable = ").append(server_sync_status.start_patient_session.rows_pending_non_syncable).append(". ");
            string_builder.append("start_patient_session.rows_pending_but_failed = ").append(server_sync_status.start_patient_session.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.start_device_session.getTotalRowsPending() > 0)
        {
            string_builder.append("start_device_session.rows_pending_syncable = ").append(server_sync_status.start_device_session.rows_pending_syncable).append(". ");
            string_builder.append("start_device_session.rows_pending_non_syncable = ").append(server_sync_status.start_device_session.rows_pending_non_syncable).append(". ");
            string_builder.append("start_device_session.rows_pending_but_failed = ").append(server_sync_status.start_device_session.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.end_device_session.getTotalRowsPending() > 0)
        {
            string_builder.append("end_device_session.rows_pending_non_syncable = ").append(server_sync_status.end_device_session.rows_pending_non_syncable).append(". ");
            string_builder.append("end_device_session.rows_pending_syncable = ").append(server_sync_status.end_device_session.rows_pending_syncable).append(". ");
            string_builder.append("end_device_session.rows_pending_but_failed = ").append(server_sync_status.end_device_session.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.end_patient_session.getTotalRowsPending() > 0)
        {
            string_builder.append("end_patient_session.rows_pending_syncable = ").append(server_sync_status.end_patient_session.rows_pending_syncable).append(". ");
            string_builder.append("end_patient_session.rows_pending_non_syncable = ").append(server_sync_status.end_patient_session.rows_pending_non_syncable).append(". ");
            string_builder.append("end_patient_session.rows_pending_but_failed = ").append(server_sync_status.end_patient_session.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.active_session_connection_event_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("active_session_connection_event_rows_pending.row_pending = ").append(server_sync_status.active_session_connection_event_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("active_session_connection_event_rows_pending = ").append(server_sync_status.active_session_connection_event_rows_pending).append(". ");
            string_builder.append("active_session_connection_event_rows_pending = ").append(server_sync_status.active_session_connection_event_rows_pending).append(". ");
        }

        if (server_sync_status.patient_session_fully_synced.getTotalRowsPending() > 0)
        {
            string_builder.append("patient_session_fully_synced.rows_pending_syncable = ").append(server_sync_status.patient_session_fully_synced.rows_pending_syncable).append(". ");
            string_builder.append("patient_session_fully_synced.rows_pending_non_syncable = ").append(server_sync_status.patient_session_fully_synced.rows_pending_non_syncable).append(". ");
            string_builder.append("patient_session_fully_synced.rows_pending_but_failed = ").append(server_sync_status.patient_session_fully_synced.rows_pending_but_failed).append(". ");
        }


        if (server_sync_status.active_session_lifetouch_heart_rate_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_lifetouch_heart_rate_rows_pending = ").append(server_sync_status.active_session_lifetouch_heart_rate_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_lifetouch_heart_rate_rows_pending = ").append(server_sync_status.active_session_lifetouch_heart_rate_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_lifetouch_heart_rate_rows_pending_but_failed = ").append(server_sync_status.active_session_lifetouch_heart_rate_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_lifetouch_heart_beat_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_lifetouch_heart_beat_rows_pending = ").append(server_sync_status.active_session_lifetouch_heart_beat_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_lifetouch_heart_beat_rows_pending =  ").append(server_sync_status.active_session_lifetouch_heart_beat_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_lifetouch_heart_beat_rows_pending_but_failed = ").append(server_sync_status.active_session_lifetouch_heart_beat_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_lifetouch_respiration_rate_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_lifetouch_respiration_rate_rows_pending = ").append(server_sync_status.active_session_lifetouch_respiration_rate_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_lifetouch_respiration_rate_rows_pending = ").append(server_sync_status.active_session_lifetouch_respiration_rate_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_lifetouch_respiration_rate_rows_pending_but_failed = ").append(server_sync_status.active_session_lifetouch_respiration_rate_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_lifetouch_setup_mode_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_lifetouch_setup_mode_rows_pending = ").append(server_sync_status.active_session_lifetouch_setup_mode_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_lifetouch_setup_mode_rows_pending = ").append(server_sync_status.active_session_lifetouch_setup_mode_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_lifetouch_setup_mode_rows_pending_but_failed = ").append(server_sync_status.active_session_lifetouch_setup_mode_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_lifetouch_raw_accelerometer_mode_rows_pending = ").append(server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_lifetouch_raw_accelerometer_mode_rows_pending = ").append(server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_lifetouch_raw_accelerometer_mode_rows_pending_but_failed = ").append(server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_lifetouch_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_lifetouch_battery_measurement_rows_pending = ").append(server_sync_status.active_session_lifetouch_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_lifetouch_battery_measurement_rows_pending = ").append(server_sync_status.active_session_lifetouch_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_lifetouch_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_lifetouch_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_lifetouch_patient_orientation_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_lifetouch_patient_orientation_rows_pending = ").append(server_sync_status.active_session_lifetouch_patient_orientation_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_lifetouch_patient_orientation_rows_pending = ").append(server_sync_status.active_session_lifetouch_patient_orientation_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_lifetouch_patient_orientation_rows_pending_but_failed = ").append(server_sync_status.active_session_lifetouch_patient_orientation_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_lifetemp_temperature_measurement_rows_pending = ").append(server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_lifetemp_temperature_measurement_rows_pending = ").append(server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_lifetemp_temperature_measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_lifetemp_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_lifetemp_battery_measurement_rows_pending = ").append(server_sync_status.active_session_lifetemp_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_lifetemp_battery_measurement_rows_pending = ").append(server_sync_status.active_session_lifetemp_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_lifetemp_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_lifetemp_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_pulse_ox_measurement_rows_pending = ").append(server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_pulse_ox_measurement_rows_pending = ").append(server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_pulse_ox_measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_pulse_ox_intermediate_measurement_rows_pending = ").append(server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_pulse_ox_intermediate_measurement_rows_pending = ").append(server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_pulse_ox_intermediate_measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_pulse_ox_setup_mode_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_pulse_ox_setup_mode_rows_pending = ").append(server_sync_status.active_session_pulse_ox_setup_mode_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_pulse_ox_setup_mode_rows_pending = ").append(server_sync_status.active_session_pulse_ox_setup_mode_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_pulse_ox_setup_mode_rows_pending_but_failed = ").append(server_sync_status.active_session_pulse_ox_setup_mode_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_pulse_ox_battery_measurement_rows_pending = ").append(server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_pulse_ox_battery_measurement_rows_pending = ").append(server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_pulse_ox_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.active_session_blood_pressure_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_blood_pressure_measurement_rows_pending = ").append(server_sync_status.active_session_blood_pressure_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_blood_pressure_measurement_rows_pending = ").append(server_sync_status.active_session_blood_pressure_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_blood_pressure_measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_blood_pressure_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_blood_pressure_battery_measurement_rows_pending = ").append(server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_blood_pressure_battery_measurement_rows_pending = ").append(server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_blood_pressure_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.active_session_weight_scale_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_weight_scale_measurement_rows_pending = ").append(server_sync_status.active_session_weight_scale_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_weight_scale__measurement_rows_pending = ").append(server_sync_status.active_session_weight_scale_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_weight_scale__measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_weight_scale_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_weight_scale_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_weight_scale_battery_measurement_rows_pending = ").append(server_sync_status.active_session_weight_scale_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_weight_scale_battery_measurement_rows_pending = ").append(server_sync_status.active_session_weight_scale_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_weight_scale_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.active_session_weight_scale_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.active_session_manually_entered_heart_rate_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_manually_entered_heart_rate_rows_pending = ").append(server_sync_status.active_session_manually_entered_heart_rate_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_manually_entered_heart_rate_rows_pending = ").append(server_sync_status.active_session_manually_entered_heart_rate_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_manually_entered_heart_rate_rows_pending_but_failed = ").append(server_sync_status.active_session_manually_entered_heart_rate_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_manually_entered_respiration_rate_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_manually_entered_respiration_rate_rows_pending = ").append(server_sync_status.active_session_manually_entered_respiration_rate_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_manually_entered_respiration_rate_rows_pending = ").append(server_sync_status.active_session_manually_entered_respiration_rate_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_manually_entered_respiration_rate_rows_pending_but_failed = ").append(server_sync_status.active_session_manually_entered_respiration_rate_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_manually_entered_temperature_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_manually_entered_temperature_rows_pending = ").append(server_sync_status.active_session_manually_entered_temperature_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_manually_entered_temperature_rows_pending = ").append(server_sync_status.active_session_manually_entered_temperature_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_manually_entered_temperature_rows_pending_but_failed = ").append(server_sync_status.active_session_manually_entered_temperature_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_manually_entered_spo2_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_manually_entered_spo2_rows_pending = ").append(server_sync_status.active_session_manually_entered_spo2_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_manually_entered_spo2_rows_pending = ").append(server_sync_status.active_session_manually_entered_spo2_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_manually_entered_spo2_rows_pending_but_failed = ").append(server_sync_status.active_session_manually_entered_spo2_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_manually_entered_blood_pressure_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_manually_entered_blood_pressure_rows_pending = ").append(server_sync_status.active_session_manually_entered_blood_pressure_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_manually_entered_blood_pressure_rows_pending = ").append(server_sync_status.active_session_manually_entered_blood_pressure_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_manually_entered_blood_pressure_rows_pending_but_failed = ").append(server_sync_status.active_session_manually_entered_blood_pressure_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_manually_entered_weight_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_manually_entered_weight_rows_pending = ").append(server_sync_status.active_session_manually_entered_weight_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_manually_entered_weight_rows_pending = ").append(server_sync_status.active_session_manually_entered_weight_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_manually_entered_weight_rows_pending_but_failed = ").append(server_sync_status.active_session_manually_entered_weight_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_manually_entered_consciousness_level_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_manually_entered_consciousness_level_rows_pending = ").append(server_sync_status.active_session_manually_entered_consciousness_level_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_manually_entered_consciousness_level_rows_pending = ").append(server_sync_status.active_session_manually_entered_consciousness_level_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_manually_entered_consciousness_level_rows_pending_but_failed = ").append(server_sync_status.active_session_manually_entered_consciousness_level_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_manually_entered_supplemental_oxygen_level_rows_pending = ").append(server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_manually_entered_supplemental_oxygen_level_rows_pending = ").append(server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_manually_entered_supplemental_oxygen_level_rows_pending_but_failed = ").append(server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.active_session_early_warning_score_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_active_session_early_warning_score_rows_pending = ").append(server_sync_status.active_session_early_warning_score_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_active_session_early_warning_score_rows_pending = ").append(server_sync_status.active_session_early_warning_score_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("active_session_early_warning_score_rows_pending_but_failed = ").append(server_sync_status.active_session_early_warning_score_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.old_session_lifetouch_heart_rate_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_lifetouch_heart_rate_rows_pending = ").append(server_sync_status.old_session_lifetouch_heart_rate_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_lifetouch_heart_rate_rows_pending = ").append(server_sync_status.old_session_lifetouch_heart_rate_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_lifetouch_heart_rate_rows_pending_but_failed = ").append(server_sync_status.old_session_lifetouch_heart_rate_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_lifetouch_heart_beat_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_lifetouch_heart_beat_rows_pending = ").append(server_sync_status.old_session_lifetouch_heart_beat_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_lifetouch_heart_beat_rows_pending = ").append(server_sync_status.old_session_lifetouch_heart_beat_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_lifetouch_heart_beat_rows_pending_but_failed = ").append(server_sync_status.old_session_lifetouch_heart_beat_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_lifetouch_respiration_rate_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_lifetouch_respiration_rate_rows_pending = ").append(server_sync_status.old_session_lifetouch_respiration_rate_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_lifetouch_respiration_rate_rows_pending = ").append(server_sync_status.old_session_lifetouch_respiration_rate_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_lifetouch_respiration_rate_rows_pending_but_failed = ").append(server_sync_status.old_session_lifetouch_respiration_rate_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_lifetouch_setup_mode_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_lifetouch_setup_mode_rows_pending = ").append(server_sync_status.old_session_lifetouch_setup_mode_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_lifetouch_setup_mode_rows_pending = ").append(server_sync_status.old_session_lifetouch_setup_mode_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_lifetouch_setup_mode_rows_pending_but_failed = ").append(server_sync_status.old_session_lifetouch_setup_mode_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_lifetouch_raw_accelerometer_mode_rows_pending = ").append(server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_lifetouch_raw_accelerometer_mode_rows_pending = ").append(server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_lifetouch_raw_accelerometer_mode_rows_pending_but_failed = ").append(server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_lifetouch_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_lifetouch_battery_measurement_rows_pending = ").append(server_sync_status.old_session_lifetouch_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_lifetouch_battery_measurement_rows_pending = ").append(server_sync_status.old_session_lifetouch_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_lifetouch_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_lifetouch_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_lifetouch_patient_orientation_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_lifetouch_patient_orientation_rows_pending = ").append(server_sync_status.old_session_lifetouch_patient_orientation_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_lifetouch_patient_orientation_rows_pending = ").append(server_sync_status.old_session_lifetouch_patient_orientation_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_lifetouch_patient_orientation_rows_pending_but_failed = ").append(server_sync_status.old_session_lifetouch_patient_orientation_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_lifetemp_temperature_measurement_rows_pending = ").append(server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_lifetemp_temperature_measurement_rows_pending = ").append(server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_lifetemp_temperature_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_lifetemp_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_lifetemp_battery_measurement_rows_pending = ").append(server_sync_status.old_session_lifetemp_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_lifetemp_battery_measurement_rows_pending = ").append(server_sync_status.old_session_lifetemp_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_lifetemp_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_lifetemp_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_pulse_ox_measurement_rows_pending = ").append(server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_pulse_ox_measurement_rows_pending = ").append(server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_pulse_ox_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_pulse_ox_intermediate_measurement_rows_pending = ").append(server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_pulse_ox_intermediate_measurement_rows_pending = ").append(server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_pulse_ox_intermediate_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_pulse_ox_setup_mode_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_pulse_ox_setup_mode_rows_pending = ").append(server_sync_status.old_session_pulse_ox_setup_mode_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_pulse_ox_setup_mode_rows_pending = ").append(server_sync_status.old_session_pulse_ox_setup_mode_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_pulse_ox_setup_mode_rows_pending_but_failed = ").append(server_sync_status.old_session_pulse_ox_setup_mode_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_pulse_ox_battery_measurement_rows_pending = ").append(server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_pulse_ox_battery_measurement_rows_pending = ").append(server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_pulse_ox_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.old_session_blood_pressure_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_blood_pressure_measurement_rows_pending = ").append(server_sync_status.old_session_blood_pressure_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_blood_pressure_measurement_rows_pending = ").append(server_sync_status.old_session_blood_pressure_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_blood_pressure_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_blood_pressure_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_blood_pressure_battery_measurement_rows_pending = ").append(server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_blood_pressure_battery_measurement_rows_pending = ").append(server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_blood_pressure_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.old_session_weight_scale_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_weight_scale_measurement_rows_pending = ").append(server_sync_status.old_session_weight_scale_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_weight_scale_measurement_rows_pending = ").append(server_sync_status.old_session_weight_scale_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_weight_scale_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_weight_scale_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_weight_scale_battery_measurement_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_weight_scale_battery_measurement_rows_pending = ").append(server_sync_status.old_session_weight_scale_battery_measurement_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_weight_scale_battery_measurement_rows_pending = ").append(server_sync_status.old_session_weight_scale_battery_measurement_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_weight_scale_battery_measurement_rows_pending_but_failed = ").append(server_sync_status.old_session_weight_scale_battery_measurement_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.old_session_manually_entered_heart_rate_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_manually_entered_heart_rate_rows_pending = ").append(server_sync_status.old_session_manually_entered_heart_rate_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_manually_entered_heart_rate_rows_pending = ").append(server_sync_status.old_session_manually_entered_heart_rate_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_manually_entered_heart_rate_rows_pending_but_failed = ").append(server_sync_status.old_session_manually_entered_heart_rate_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_manually_entered_respiration_rate_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_manually_entered_respiration_rate_rows_pending = ").append(server_sync_status.old_session_manually_entered_respiration_rate_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_manually_entered_respiration_rate_rows_pending = ").append(server_sync_status.old_session_manually_entered_respiration_rate_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_manually_entered_respiration_rate_rows_pending_but_failed = ").append(server_sync_status.old_session_manually_entered_respiration_rate_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_manually_entered_temperature_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_manually_entered_temperature_rate_rows_pending = ").append(server_sync_status.old_session_manually_entered_temperature_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_manually_entered_temperature_rate_rows_pending = ").append(server_sync_status.old_session_manually_entered_temperature_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_manually_entered_temperature_rate_rows_pending_but_failed = ").append(server_sync_status.old_session_manually_entered_temperature_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_manually_entered_spo2_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_manually_entered_oximeter_rows_pending = ").append(server_sync_status.old_session_manually_entered_spo2_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_manually_entered_oximeter_rows_pending = ").append(server_sync_status.old_session_manually_entered_spo2_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_manually_entered_oximeter_rows_pending_but_failed = ").append(server_sync_status.old_session_manually_entered_spo2_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_manually_entered_blood_pressure_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_manually_entered_blood_pressure_rows_pending = ").append(server_sync_status.old_session_manually_entered_blood_pressure_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_manually_entered_blood_pressure_rows_pending = ").append(server_sync_status.old_session_manually_entered_blood_pressure_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_manually_entered_blood_pressure_rows_pending_but_failed = ").append(server_sync_status.old_session_manually_entered_blood_pressure_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_manually_entered_weight_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_manually_entered_weight_rows_pending = ").append(server_sync_status.old_session_manually_entered_weight_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_manually_entered_weight_rows_pending = ").append(server_sync_status.old_session_manually_entered_weight_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_manually_entered_weight_rows_pending_but_failed = ").append(server_sync_status.old_session_manually_entered_weight_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_manually_entered_consciousness_level_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_manually_entered_consciousness_level_rows_pending = ").append(server_sync_status.old_session_manually_entered_consciousness_level_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_manually_entered_consciousness_level_rows_pending = ").append(server_sync_status.old_session_manually_entered_consciousness_level_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_manually_entered_consciousness_level_rows_pending_but_failed = ").append(server_sync_status.old_session_manually_entered_consciousness_level_rows_pending.rows_pending_but_failed).append(". ");
        }
        if (server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_manually_entered_supplemental_oxygen_level_rows_pending = ").append(server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_manually_entered_supplemental_oxygen_level_rows_pending = ").append(server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_manually_entered_supplemental_oxygen_level_rows_pending_but_failed = ").append(server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_but_failed).append(". ");
        }

        if (server_sync_status.old_session_early_warning_score_rows_pending.getTotalRowsPending() > 0)
        {
            string_builder.append("syncable_old_session_early_warning_score_rows_pending = ").append(server_sync_status.old_session_early_warning_score_rows_pending.rows_pending_syncable).append(". ");
            string_builder.append("non_syncable_old_session_early_warning_score_rows_pending = ").append(server_sync_status.old_session_early_warning_score_rows_pending.rows_pending_non_syncable).append(". ");
            string_builder.append("old_session_early_warning_score_rows_pending_but_failed = ").append(server_sync_status.old_session_early_warning_score_rows_pending.rows_pending_but_failed).append(". ");
        }

        String log_line = string_builder.toString();
        if (!log_line.isEmpty())
        {
            Log.d(TAG, "server_sync_status = " + log_line);
        }
    }


    private long readAmountOfSessionDetailsDataPendingInDatabase()
    {
        long startTime = System.nanoTime();

        // Get Session Info
        server_sync_status.patient_details = local_database_storage.checkIfPatientDetailsPendingToBeSentToServer();
        server_sync_status.device_info = local_database_storage.checkIfDeviceInfoPendingToBeSentToServer();
        server_sync_status.start_patient_session = local_database_storage.checkIfStartPatientSessionPendingToBeSentToServer();
        server_sync_status.start_device_session = local_database_storage.checkIfStartDeviceSessionPendingToBeSentToServer();
        server_sync_status.end_device_session = local_database_storage.checkIfEndDeviceSessionPendingToBeSentToServer();
        server_sync_status.end_patient_session = local_database_storage.checkIfEndPatientSessionPendingToBeSentToServer();
        server_sync_status.patient_session_fully_synced = local_database_storage.checkIfPatientSessionFullySyncedRecordsPendingToBeSentToServer();
        server_sync_status.auditable_events_rows_pending = local_database_storage.checkIfAuditableEventsPendingToBeSentToServer();       

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        return duration / 1000000;                                                                  // Divide by 1000000 to get milliseconds.
    }

    private final boolean GET_ALL_DATA_FROM_QUERY = false;

    private long readAmountOfActiveSessionDataPendingInDatabase()
    {
        RowsPending default_UI_row_pending_values = new RowsPending();
        default_UI_row_pending_values.rows_pending_syncable = 0;
        default_UI_row_pending_values.rows_pending_non_syncable = 0;
        default_UI_row_pending_values.rows_pending_but_failed = 0;

        long startTime = System.nanoTime();

        DeviceInfo device_info;

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);
        if (device_info.isDeviceSessionInProgress())
        {
            server_sync_status.active_session_lifetouch_heart_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_HEART_RATES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_lifetouch_heart_beat_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_HEART_BEATS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_lifetouch_respiration_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_RESPIRATION_RATES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_lifetouch_setup_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_SETUP_MODE_RAW_SAMPLES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_lifetouch_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_lifetouch_patient_orientation_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_PATIENT_ORIENTATIONS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        }
        else
        {
            // If there is no device session, the default values will be -1. Update here
            server_sync_status.active_session_lifetouch_heart_rate_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_lifetouch_heart_beat_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_lifetouch_respiration_rate_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_lifetouch_setup_mode_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_lifetouch_battery_measurement_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_lifetouch_patient_orientation_rows_pending = default_UI_row_pending_values;
        }

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE);
        if (device_info.isDeviceSessionInProgress())
        {
            server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETEMP_TEMPERATURE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_lifetemp_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETEMP_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        }
        else
        {
            // If there is no device session, the default values will be -1. Update here
            server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_lifetemp_battery_measurement_rows_pending = default_UI_row_pending_values;
        }

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);
        if (device_info.isDeviceSessionInProgress())
        {
            server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.PULSE_OX_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.PULSE_OX_INTERMEDIATE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_pulse_ox_setup_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.PULSE_OX_SETUP_MODE_SAMPLES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.PULSE_OX_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        }
        else
        {
            // If there is no device session, the default values will be -1. Update here
            server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_pulse_ox_setup_mode_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending = default_UI_row_pending_values;
        }

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        if (device_info.isDeviceSessionInProgress())
        {
            server_sync_status.active_session_blood_pressure_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.BLOOD_PRESSURE_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        }
        else
        {
            // If there is no device session, the default values will be -1. Update here
            server_sync_status.active_session_blood_pressure_measurement_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending = default_UI_row_pending_values;
        }

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE);
        if (device_info.isDeviceSessionInProgress())
        {
            server_sync_status.active_session_weight_scale_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.WEIGHT_SCALE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
            server_sync_status.active_session_weight_scale_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.WEIGHT_SCALE_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        }
        else
        {
            // If there is no device session, the default values will be -1. Update here
            server_sync_status.active_session_weight_scale_measurement_rows_pending = default_UI_row_pending_values;
            server_sync_status.active_session_weight_scale_battery_measurement_rows_pending = default_UI_row_pending_values;
        }

        server_sync_status.active_session_connection_event_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.CONNECTION_EVENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);

        server_sync_status.active_session_manually_entered_heart_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_HEART_RATES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_respiration_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_RESPIRATION_RATES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_temperature_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_TEMPERATURES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_spo2_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_SPO2_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_blood_pressure_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_weight_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_consciousness_level_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_annotation_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_ANNOTATIONS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_capillary_refill_time_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_respiration_distress_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_RESPIRATION_DISTRESS, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.active_session_manually_entered_family_or_nurse_concern_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);

        server_sync_status.active_session_setup_mode_log_rows_pending = local_database_storage.checkIfActiveSessionSetupModeLogsPendingToBeSentToServer();

        server_sync_status.active_session_early_warning_score_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).getAndroidDeviceSessionId(), QueryType.EARLY_WARNING_SCORES, ActiveOrOldSession.ACTIVE_SESSION, GET_ALL_DATA_FROM_QUERY);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        return duration / 1000000;                                                                  // Divide by 1000000 to get milliseconds.
    }

    
    private long readAmountOfHistoricalSessionDataPendingInDatabase()
    {
        long startTime = System.nanoTime();

        DeviceInfo device_info;

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);
        server_sync_status.old_session_lifetouch_heart_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_HEART_RATES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_lifetouch_heart_beat_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_HEART_BEATS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_lifetouch_respiration_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_RESPIRATION_RATES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_lifetouch_setup_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_SETUP_MODE_RAW_SAMPLES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_lifetouch_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_lifetouch_patient_orientation_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETOUCH_PATIENT_ORIENTATIONS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE);
        server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETEMP_TEMPERATURE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_lifetemp_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.LIFETEMP_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);
        server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.PULSE_OX_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.PULSE_OX_INTERMEDIATE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_pulse_ox_setup_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.PULSE_OX_SETUP_MODE_SAMPLES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.PULSE_OX_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        server_sync_status.old_session_blood_pressure_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.BLOOD_PRESSURE_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE);
        server_sync_status.old_session_weight_scale_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.WEIGHT_SCALE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_weight_scale_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info.getAndroidDeviceSessionId(), QueryType.WEIGHT_SCALE_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);

        server_sync_status.old_session_manually_entered_heart_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_HEART_RATES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_respiration_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_RESPIRATION_RATES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_temperature_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_TEMPERATURES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_spo2_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_SPO2_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_blood_pressure_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_weight_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_consciousness_level_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_annotation_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_ANNOTATIONS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_capillary_refill_time_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_respiration_distress_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_RESPIRATION_DISTRESS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);
        server_sync_status.old_session_manually_entered_family_or_nurse_concern_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);

        server_sync_status.old_session_setup_mode_log_rows_pending = local_database_storage.checkIfHistoricalSessionSetupModeLogsPendingToBeSentToServer();

        server_sync_status.old_session_early_warning_score_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).getAndroidDeviceSessionId(), QueryType.EARLY_WARNING_SCORES, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);

        server_sync_status.old_session_connection_event_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.CONNECTION_EVENTS, ActiveOrOldSession.OLD_SESSION, GET_ALL_DATA_FROM_QUERY);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        return duration / 1000000;                                                                  // Divide by 1000000 to get milliseconds.
    }

    
    // This function relies on server_sync_status being updated before hand
    public void checkIfMoreDataPendingToBeSentToServerAndSendIt(ServerSyncingDataUploadPoint current_session_type_to_sync)
    {
        Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : current_session_type_to_sync = " + current_session_type_to_sync.toString());

        switch (current_session_type_to_sync)
        {
            case PATIENT_DETAILS_DATA:
            {
                switch(currentSessionInformation.patientDetailSendToServer_Status)
                {
                    case IDLE:
                    {
                        currentSessionInformation.patientDetailSendToServer_Status = CheckServerLinkStatus.SEND_PACKET;
                    }
                    break;

                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    case CHECKING:
                    case SEND_PACKET:
                    default:
                        break;
                }

                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : Before update. currentSessionInformation.patientDetailSendToServer_Status = " + currentSessionInformation.patientDetailSendToServer_Status);

                // Execute these in this SPECIFIC order. The first few return server_id's that the remaining ones need
                int patient_details_rows_pending_syncable = local_database_storage.checkIfPatientDetailsPendingToBeSentToServerAndSentIt(server_interface);

                if(patient_details_rows_pending_syncable == 0)
                {
                    currentSessionInformation.patientDetailSendToServer_Status = CheckServerLinkStatus.IDLE;

                    // Actual number of pending row to be synced to the server is zero
                    // There are failed data to be synced. Update the sync adaptor state
                    server_sync_table_observer.pendingDataSizeZero_changeSyncAdaptorState(current_session_type_to_sync);
                }

                Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : patient_details_rows_pending and syncable = " + patient_details_rows_pending_syncable);
                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : currentSessionInformation.patientDetailSendToServer_Status = " + currentSessionInformation.patientDetailSendToServer_Status);

                return;
            }

            case AUDITABLE_EVENTS:
            {
                switch (currentSessionInformation.auditTrailSendToServer_Status)
                {
                    case IDLE:
                    {
                        currentSessionInformation.auditTrailSendToServer_Status = CheckServerLinkStatus.SEND_PACKET;
                    }
                    break;

                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    case CHECKING:
                    case SEND_PACKET:
                    default:
                        break;
                }

                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : Before update. currentSessionInformation.auditTrailSendToServer_Status = " + currentSessionInformation.auditTrailSendToServer_Status);

                // Execute these in this SPECIFIC order. The first few return server_id's that the remaining ones need
                int audit_events_rows_pending_syncable = local_database_storage.checkIfAuditableEventsPendingToBeSentToServerAndSendIt(server_interface);

                if (audit_events_rows_pending_syncable == 0)
                {
                    currentSessionInformation.auditTrailSendToServer_Status = CheckServerLinkStatus.IDLE;

                    // Actual number of pending row to be synced to the server is zero
                    // There are failed data to be synced. Update the sync adaptor state
                    server_sync_table_observer.pendingDataSizeZero_changeSyncAdaptorState(current_session_type_to_sync);
                }

                Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : audit_events_rows_pending_syncable = " + audit_events_rows_pending_syncable);
                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : currentSessionInformation.auditTrailSendToServer_Status = " + currentSessionInformation.auditTrailSendToServer_Status);

                return;
            }


            case DEVICE_INFO_DATA:
            {
                switch (currentSessionInformation.deviceInfoSendToServer_Status)
                {
                    case IDLE:
                    {
                        // Because same devices can be used in multiple Gateways.
                        currentSessionInformation.deviceInfoSendToServer_Status = CheckServerLinkStatus.SEND_PACKET;
                    }
                    break;

                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    case CHECKING:
                    case SEND_PACKET:
                    default:
                        break;
                }

                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : Before Update. currentSessionInformation.deviceInfoSendToServer_Status = " + currentSessionInformation.deviceInfoSendToServer_Status);

                int device_info_rows_pending_syncable = local_database_storage.checkIfDeviceInfoPendingToBeSentToServerAndSendIt(server_interface);

                if(device_info_rows_pending_syncable == 0)
                {
                    currentSessionInformation.deviceInfoSendToServer_Status = CheckServerLinkStatus.IDLE;

                    // Actual number of pending row to be synced to the server is zero
                    // There are failed data to be synced. Update the sync adaptor state
                    server_sync_table_observer.pendingDataSizeZero_changeSyncAdaptorState(current_session_type_to_sync);
                }

                Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : device_info_rows_pending = " + device_info_rows_pending_syncable);
                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : currentSessionInformation.deviceInfoSendToServer_Status = " + currentSessionInformation.deviceInfoSendToServer_Status);

                return;
            }

            case START_PATIENT_SESSION:
            {
                switch (currentSessionInformation.startPatientSessionSendToServer_Status)
                {
                    case IDLE:
                    {
                        currentSessionInformation.startPatientSessionSendToServer_Status = CheckServerLinkStatus.SERVER_CHECK_FOR_UNIQUE_ID;
                    }
                    break;

                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    case CHECKING:
                    case SEND_PACKET:
                    default:
                        break;
                }

                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : Before update. currentSessionInformation.startPatientSessionSendToServer_Status = " + currentSessionInformation.startPatientSessionSendToServer_Status);

                int start_patient_session_rows_pending_syncable = local_database_storage.checkIfStartPatientSessionPendingToBeSentToServerAndSendIt(server_interface);

                if(start_patient_session_rows_pending_syncable == 0)
                {
                    currentSessionInformation.startPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                    // Actual number of pending row to be synced to the server is zero
                    // There are failed data to be synced. Update the sync adaptor state
                    server_sync_table_observer.pendingDataSizeZero_changeSyncAdaptorState(current_session_type_to_sync);
                }

                Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : start_patient_session_rows_pending = " + start_patient_session_rows_pending_syncable);
                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : currentSessionInformation.startPatientSessionSendToServer_Status = " + currentSessionInformation.startPatientSessionSendToServer_Status);

                return;
            }

            case START_DEVICE_SESSION:
            {
                switch (currentSessionInformation.startDeviceSessionSendToServer_Status)
                {
                    case IDLE:
                    {
                        currentSessionInformation.startDeviceSessionSendToServer_Status = CheckServerLinkStatus.SERVER_CHECK_FOR_UNIQUE_ID;
                    }
                    break;

                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    case CHECKING:
                    case SEND_PACKET:
                    default:
                        break;
                }

                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt :Before update. currentSessionInformation.startDeviceSessionSendToServer_Status = " + currentSessionInformation.startDeviceSessionSendToServer_Status);

                int start_device_session_rows_pending_syncable = local_database_storage.checkIfStartDeviceSessionPendingToBeSentToServerAndSendIt(server_interface);

                if(start_device_session_rows_pending_syncable == 0)
                {
                    currentSessionInformation.startDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                    // Actual number of pending row to be synced to the server is zero
                    // There are failed data to be synced. Update the sync adaptor state
                    server_sync_table_observer.pendingDataSizeZero_changeSyncAdaptorState(current_session_type_to_sync);
                }

                Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : start_device_session_rows_pending = " + start_device_session_rows_pending_syncable );
                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : currentSessionInformation.startDeviceSessionSendToServer_Status = " + currentSessionInformation.startDeviceSessionSendToServer_Status);

                return;
            }

            case END_DEVICE_SESSION:
            {
                switch (currentSessionInformation.endDeviceSessionSendToServer_Status)
                {
                    case IDLE:
                    {
                        currentSessionInformation.endDeviceSessionSendToServer_Status = CheckServerLinkStatus.SERVER_CHECK_FOR_UNIQUE_ID;
                    }
                    break;

                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    case CHECKING:
                    case SEND_PACKET:
                    default:
                        break;
                }

                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : Before update. currentSessionInformation.endDeviceSessionSendToServer_Status = " + currentSessionInformation.endDeviceSessionSendToServer_Status);

                int end_device_session_rows_pending_syncable = local_database_storage.checkIfEndDeviceSessionPendingToBeSentToServerAndSendIt(server_interface);

                if(end_device_session_rows_pending_syncable == 0)
                {
                    currentSessionInformation.endDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                    // Actual number of pending row to be synced to the server is zero
                    // There are failed data to be synced. Update the sync adaptor state
                    server_sync_table_observer.pendingDataSizeZero_changeSyncAdaptorState(current_session_type_to_sync);
                }

                Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : end_device_session_rows_pending = " + end_device_session_rows_pending_syncable );
                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : currentSessionInformation.endDeviceSessionSendToServer_Status = " + currentSessionInformation.endDeviceSessionSendToServer_Status);

                return;
            }

            case END_PATIENT_SESSION:
            {
                switch (currentSessionInformation.endPatientSessionSendToServer_Status)
                {
                    case IDLE:
                    {
                        currentSessionInformation.endPatientSessionSendToServer_Status = CheckServerLinkStatus.SERVER_CHECK_FOR_UNIQUE_ID;
                    }
                    break;

                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    case CHECKING:
                    case SEND_PACKET:
                    default:
                        break;
                }

                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : Before update. currentSessionInformation.endPatientSessionSendToServer_Status = " + currentSessionInformation.endPatientSessionSendToServer_Status);

                int end_patient_session_rows_pending_syncable = local_database_storage.checkIfEndPatientSessionPendingToBeSentToServerAndSendIt(server_interface);

                if(end_patient_session_rows_pending_syncable == 0)
                {
                    currentSessionInformation.endPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                    // Actual number of pending row to be synced to the server is zero
                    // There are failed data to be synced. Update the sync adaptor state
                    server_sync_table_observer.pendingDataSizeZero_changeSyncAdaptorState(current_session_type_to_sync);
                }

                Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : end_patient_session_rows_pending = " + end_patient_session_rows_pending_syncable);
                Log.i(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : currentSessionInformation.endPatientSessionSendToServer_Status = " + currentSessionInformation.endPatientSessionSendToServer_Status);

                return;
            }

            case CONNECTION_EVENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfConnectionEventsPendingToBeSentToServerAndSendIt(server_interface, ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case LIFETOUCH_HEART_RATE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchHeartRateDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case LIFETOUCH_HEART_BEAT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchHeartBeatDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case LIFETOUCH_RESPIRATION_RATE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchRespirationRateDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case LIFETOUCH_SETUP_MODE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchSetupModeDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case LIFETOUCH_RAW_ACCELEROMETER_MODE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchRawAccelerometerModeDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case LIFETOUCH_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case LIFETOUCH_PATIENT_ORIENTATION:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchPatientOrientationDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case LIFETEMP_TEMPERATURE_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetempTemperatureDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case LIFETEMP_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetempBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case PULSE_OX_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMorePulseOxMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case PULSE_OX_INTERMEDIATE_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMorePulseOxIntermediateMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case PULSE_OX_SETUP_MODE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMorePulseOxSetupModeDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case PULSE_OX_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMorePulseOxBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case BLOOD_PRESSURE_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreBloodPressureMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case BLOOD_PRESSURE_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreBloodPressureBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case WEIGHT_SCALE_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreWeightScaleMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case WEIGHT_SCALE_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreWeightScaleBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_HEART_RATES:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredHeartRateDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_RESPIRATION_RATES:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredRespirationRateDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_TEMPERATURE_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredTemperatureMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_OXIMETER_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredOximeterMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredBloodPressureMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredWeightMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredConsciousnessLevelMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredSupplementalOxygenLevelMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_ANNOTATIONS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredAnnotationDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredCapillaryRefillTimeDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredRespirationDistressDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredFamilyOrNurseConcernDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredUrineOutputDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case EARLY_WARNING_SCORES:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreEarlyWarningScoresPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).getAndroidDeviceSessionId(), ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case SETUP_MODE_LOGS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreSetupModeLogsPendingToBeSentToServerAndSendIt(server_interface, ActiveOrOldSession.ACTIVE_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case FULLY_SYNCED_SESSIONS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfPatientSessionFullySyncedRecordsPendingToBeSentToServerAndSendIt(server_interface);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_CONNECTION_EVENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfConnectionEventsPendingToBeSentToServerAndSendIt(server_interface, ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_LIFETOUCH_HEART_RATE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchHeartRateDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_LIFETOUCH_HEART_BEAT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchHeartBeatDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_LIFETOUCH_RESPIRATION_RATE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchRespirationRateDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_LIFETOUCH_SETUP_MODE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchSetupModeDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_LIFETOUCH_RAW_ACCELEROMETER_MODE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchRawAccelerometerModeDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_LIFETOUCH_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_LIFETOUCH_PATIENT_ORIENTATION:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetouchPatientOrientationDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_LIFETEMP_TEMPERATURE_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetempTemperatureDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_LIFETEMP_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreLifetempBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_PULSE_OX_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMorePulseOxMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_PULSE_OX_INTERMEDIATE_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMorePulseOxIntermediateMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_PULSE_OX_SETUP_MODE:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMorePulseOxSetupModeDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_PULSE_OX_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMorePulseOxBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_BLOOD_PRESSURE_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreBloodPressureMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_BLOOD_PRESSURE_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreBloodPressureBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_WEIGHT_SCALE_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreWeightScaleMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_WEIGHT_SCALE_BATTERY_MEASUREMENT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreWeightScaleBatteryDataPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_HEART_RATES:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredHeartRateDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_RESPIRATION_RATES:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredRespirationRateDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_TEMPERATURE_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredTemperatureMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_OXIMETER_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredOximeterMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredBloodPressureMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredWeightMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredConsciousnessLevelMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredSupplementalOxygenLevelMeasurementDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_ANNOTATIONS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredAnnotationDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredCapillaryRefillTimeDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredRespirationDistressDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredFamilyOrNurseConcernDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_MANUALLY_ENTERED_URINE_OUTPUT:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreManuallyEnteredUrineOutputDataPendingToBeSentToServerAndSendIt(server_interface, patient_gateway_interface.getAndroidDatabasePatientSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_EARLY_WARNING_SCORES:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreEarlyWarningScoresPendingToBeSentToServerAndSendIt(server_interface, device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).getAndroidDeviceSessionId(), ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case HISTORICAL_SETUP_MODE_LOGS:
            {
                int rows_pending_rows_pending_syncable = local_database_storage.checkIfMoreSetupModeLogsPendingToBeSentToServerAndSendIt(server_interface, ActiveOrOldSession.OLD_SESSION);
                handleRowsPending(current_session_type_to_sync, rows_pending_rows_pending_syncable);
                return;
            }

            case NO_MORE_DATA_TO_SYNC:
            {
                Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : Server sync state is SYNCING_NO_MORE_DATA. ALERT ALERT!!!!!! This state is not expected to occur in ServerSyncing");
            }
            break;

            default:
            {
                Log.e(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : current_session_type_to_sync = " + current_session_type_to_sync + " but is unhandled");
            }
            break;
        }
    }


    private void handleRowsPending(ServerSyncingDataUploadPoint current_session_type_to_sync, int rows_pending_rows_pending_syncable)
    {
        if(rows_pending_rows_pending_syncable == 0)
        {
            // Actual number of pending row to be synced to the server is zero
            // There are failed data to be synced. Update the sync adaptor state
            server_sync_table_observer.pendingDataSizeZero_changeSyncAdaptorState(current_session_type_to_sync);
        }

        Log.d(TAG, "checkIfMoreDataPendingToBeSentToServerAndSendIt : " + current_session_type_to_sync + " = " + rows_pending_rows_pending_syncable);
    }


    private void registerGatewayContentObservers()
    {
        // Register change in Lifetouch table
        final boolean NOTIFY_EXACT_URI_CHANGE = false;

        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_RATES, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_BEATS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RESPIRATION_RATES, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_BATTERY_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_PATIENT_ORIENTATION, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Lifetemp table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_TEMPERATURE_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_BATTERY_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Pulse Oximeter table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_INTERMEDIATE_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_BATTERY_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Blood Pressure table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Weight table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_BATTERY_MEASUREMENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Session table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_DETAILS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_INFO, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Connection event table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_CONNECTION_EVENTS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Audit Trail event table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_AUDIT_TRAIL, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Manually Entered Measurement tables
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_HEART_RATES, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SPO2, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_WEIGHT, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_URINE_OUTPUT, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Session fully synced table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS_FULLY_SYNCED, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Setup Mode Logs table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_SETUP_MODE_LOGS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);

        // Register change in Early warning and threshold table
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORES, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SETS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
        content_resolver.registerContentObserver(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS, NOTIFY_EXACT_URI_CHANGE, server_sync_table_observer);
    }


    public void unRegisterGatewayContentObservers()
    {
        content_resolver.unregisterContentObserver(server_sync_table_observer);
    }


    public void setIsansysServerAddress(String desired_address, String desired_port)
    {
        server_interface.setIsansysServerAddress(desired_address, desired_port);
    }


    public void useHttps(boolean enabled)
    {
        server_interface.useHttps(enabled);
    }


    public void useEncryptionForWebServiceCalls(boolean enabled)
    {
        server_interface.useEncryptionForWebServiceCalls(enabled);
    }


    public void useAuthenticationForWebServiceCalls(boolean enabled)
    {
        server_interface.useAuthenticationForWebServiceCalls(enabled);
    }


    public void sendGetLatestDeviceFirmwareVersionsRequest()
    {
        server_interface.sendGetLatestDeviceFirmwareVersionsRequest();
    }


    public void sendGetFirmwareBinaryRequest(int servers_id)
    {
        server_interface.sendGetFirmwareBinaryRequest(servers_id);
    }


    public void getBedDetailsListFromServer()
    {
        server_interface.getBedDetailsListFromServer();
    }


    public void getWardDetailsListFromServer()
    {
        server_interface.getWardDetailsListFromServer();
    }


    public void getGatewayConfigFromServer()
    {
        server_interface.getGatewayConfigFromServer();
    }


    public void getServerConfigurableTextFromServer()
    {
        server_interface.getServerConfigurableTextFromServer();
    }

    public void getWebPageDescriptorsFromServer()
    {
        server_interface.getWebPageDescriptorsFromServer();
    }

    public void sendDefaultEarlyWarningScoreTypesRequest()
    {
        server_interface.sendDefaultEarlyWarningScoreTypesRequest();
    }


    public void sendServerPing()
    {
        server_interface.sendServerPing();
    }


    public void sendCheckDeviceDetails(int device_type_as_int, long human_readable_device_id)
    {
        server_interface.sendCheckDeviceDetails(device_type_as_int, human_readable_device_id);
    }


    public boolean isServerLinkBusy()
    {
        return server_interface.server_interface_busy;
    }


    public void uploadLogFile(File file)
    {
        server_interface.uploadLogFile(file);
    }


    public void sendAllDeviceInfo(JsonObject device_info_objects)
    {
        server_interface.sendAllDeviceInfo(device_info_objects);
    }


    public void resetServerSyncStatusAndTriggerSync()
    {
        server_sync_table_observer.resetServerSyncStatusAndTriggerSync();
    }

    public void runInTestMode(boolean test_mode)
    {
        sync_status_test_mode = test_mode;
    }

    public void sendGatewayVideoCallStatusToServer(String connection_id, VideoCallStatus status, String bedId, long utcTimestamp)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.sendGatewayVideoCallStatus(connection_id, status, bedId, utcTimestamp);
        }
    }

    public void sendPatientInitedVideoCallRequestToServer(JsonObject json_object)
    {
        if (isRealTimeServerConfiguredAndConnected())
        {
            server_interface.sendPatientOnGatewayRequestingVideoCall(json_object);
        }
    }

    public void sendMQTTCertificateRequest()
    {
        server_interface.sendMQTTCertificateRequest();
    }
}
