package com.isansys.patientgateway.serverlink;

import java.io.File;
import java.util.ArrayList;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.JSONArray;

import com.google.gson.JsonObject;
import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.HttpOperationType;
import com.isansys.common.enums.RealTimeServer;
import com.isansys.common.enums.SensorType;
import com.isansys.common.enums.VideoCallStatus;
import com.isansys.patientgateway.LocalDatabaseStorage;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.serverlink.webservices.HttpWebServices;
import com.isansys.patientgateway.serverlink.webservices.WebServices;
import com.isansys.patientgateway.Settings;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.database.RowRange;
import com.isansys.patientgateway.remotelogging.RemoteLogging;
import com.isansys.patientgateway.serverlink.model.ServerPostParameters;

// Class to handle top-level pubsub and webservice code
public class ServerLink
{
    private final RemoteLoggingWithEnable Log;
    private final String TAG = this.getClass().getSimpleName();

    static public final String INTENT__COMMANDS_FROM_SERVER = "com.isansys.patientgateway.serverlink.commands_from_server";

    public volatile boolean server_interface_busy = false;

    private final PubSubConnection pubSubConnection;

    private final WebServices webServices;

    private final ServerSyncing server_syncing;

    private final Settings gateway_settings;

    private final PatientGatewayInterface gateway_interface;

    private final String gateway_unique_id;

    public ServerLink(ServerSyncing desired_server_syncing, ContextInterface context_interface, RemoteLogging logger, LocalDatabaseStorage database_storage, String passed_in_gateway_unique_id, PatientGatewayInterface patient_gateway_interface, boolean enable_logs, Settings gateway_settings, ServerSyncStatus sync_status)
    {
        Log = new RemoteLoggingWithEnable(logger, enable_logs);
        this.server_syncing = desired_server_syncing;
        this.gateway_settings = gateway_settings;
        this.gateway_interface = patient_gateway_interface;
        this.gateway_unique_id = passed_in_gateway_unique_id;

        // Initialise the WAMP connection. This manages the real-time link between the Gateway and the Server
        pubSubConnection = new PubSubConnection(context_interface, Log, gateway_unique_id, gateway_interface);

        webServices = new HttpWebServices(Log,
                server_syncing.currentSessionInformation,
                patient_gateway_interface,
                desired_server_syncing,
                sync_status,
                database_storage,
                passed_in_gateway_unique_id);


//        webServices = new RealTimeWebservices(Log,
//                server_syncing.currentSessionInformation,
//                patient_gateway_interface,
//                desired_server_syncing,
//                sync_status,
//                database_storage,
//                context_interface.getAppContext());
    }


    public boolean getRealTimeServerEnabledStatusFromSettings()
    {
        return gateway_settings.getRealTimeLinkEnabledStatus();
    }


    public void setIsansysServerAddress(String desired_address, String desired_port)
    {
        webServices.setIsansysServerAddress(desired_address, desired_port);
    }


    public void useEncryptionForWebServiceCalls(boolean enabled)
    {
        webServices.useEncryptionForWebServiceCalls(enabled);
    }


    public void useHttps(boolean enabled)
    {
        webServices.useHttps(enabled);
    }


    public void useAuthenticationForWebServiceCalls(boolean enabled)
    {
        webServices.useAuthenticationForWebServiceCalls(enabled);
    }


    public void sendMeasurementsToServer(HttpOperationType http_operation_type, JSONArray json_array, ActiveOrOldSession active_or_old_session, ArrayList<RowRange> row_ranges)
    {
        ServerPostParameters params = new ServerPostParameters(http_operation_type);
        params.active_or_old_session = active_or_old_session;
        params.json_string = json_array.toString();
        params.row_ranges = row_ranges;
        webServices.sendToServer(params);
    }


    public void sendMeasurementsToServer(HttpOperationType http_operation_type, ArrayNode json_array, ActiveOrOldSession active_or_old_session, ArrayList<RowRange> row_ranges)
    {
        ServerPostParameters params = new ServerPostParameters(http_operation_type);
        params.active_or_old_session = active_or_old_session;
        params.json_string = json_array.toString();
        params.row_ranges = row_ranges;
        webServices.sendToServer(params);
    }


    public void sendPatientDetailsToServer(int android_database_row,
                                           String bed_id,
                                           String user_id,
                                           String hospital_patient_id,
                                           String age_block,
                                           String servers_threshold_set_id,
                                           String servers_threshold_set_age_block_detail_id,
                                           long written_to_android_database_timestamp)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("ByBedId", bed_id);
        json_object.addProperty("ByUserId", user_id);
        json_object.addProperty("HospitalPatientId", hospital_patient_id);

        json_object.addProperty("FirstName", "XX");
        json_object.addProperty("LastName", "YY");
        json_object.addProperty("DOB", Utils.convertTimestampToServerSqlDate(written_to_android_database_timestamp));


        json_object.addProperty("AgeBlock", age_block);
        json_object.addProperty("ThresholdSetId", servers_threshold_set_id);
        json_object.addProperty("ThresholdSetAgeBlockDetailId", servers_threshold_set_age_block_detail_id);
        json_object.addProperty("WrittenToAndroidDatabaseTimestamp", Utils.convertTimestampToServerSqlDate(written_to_android_database_timestamp));

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.PATIENT_DETAILS, json_object, android_database_row));
    }


    // see sendMeasurementsToServer
    public void sendAuditEventsToServer(JSONArray json_array, ArrayList<RowRange> row_ranges)
    {
        final HttpOperationType http_operation_type = HttpOperationType.AUDITABLE_EVENTS;

        ServerPostParameters params = new ServerPostParameters(http_operation_type);
        params.json_string = json_array.toString();
        params.row_ranges = row_ranges;
        webServices.sendToServer(params);
    }


    public void sendStartPatientSessionToServer(int android_database_row, String by_patient_id, long start_session_time, String session_started_by_user_id)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("ByPatientId", by_patient_id);
        json_object.addProperty("PatientStartSessionTime", Utils.convertTimestampToServerSqlDate(start_session_time));
        json_object.addProperty("PatientStartSessionByUserId", session_started_by_user_id);

        // Send the Olsen Timezone. This allows the Data Export tool on the Server to know what timezone to display the data in.
        TimeZone tz = TimeZone.getDefault();
        String olsen_timezone = tz.getID();
        json_object.addProperty("OlsenTimezone", olsen_timezone);

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.START_PATIENT_SESSION, json_object, android_database_row));
    }


    public void sendEndPatientSessionToServer(int android_database_row, String patient_session_id, long end_session_time, String session_ended_by_user_id)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("PatientSessionId", patient_session_id);
        json_object.addProperty("PatientEndSessionTime", Utils.convertTimestampToServerSqlDate(end_session_time));
        json_object.addProperty("PatientEndSessionByUserId", session_ended_by_user_id);

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.END_PATIENT_SESSION, json_object, android_database_row));
    }


    public void sendDeviceInfoToServer(int android_database_row,
                                       int device_type,
                                       String bluetooth_address,
                                       int human_readable_device_id,
                                       String firmware_version_as_string,
                                       String hardware,
                                       String model,
                                       long written_to_android_database_timestamp)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("DeviceType", device_type);
        json_object.addProperty("BluetoothAddress", bluetooth_address);
        json_object.addProperty("HumanReadableDeviceId", human_readable_device_id);
        json_object.addProperty("FirmwareVersion", firmware_version_as_string);
        json_object.addProperty("Hardware", hardware);
        json_object.addProperty("Model", model);
        json_object.addProperty("WrittenToAndroidDatabaseTimestamp", Utils.convertTimestampToServerSqlDate(written_to_android_database_timestamp));

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.DEVICE_INFO, json_object, android_database_row));
    }


    public void sendStartDeviceSessionToServer(int android_database_row, String patient_session_id, String device_info_id, long device_start_session_time, String device_start_session_by_user_id)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("ByPatientSessionId", patient_session_id);
        json_object.addProperty("ByDeviceId", device_info_id);
        json_object.addProperty("DeviceStartSessionTime", Utils.convertTimestampToServerSqlDate(device_start_session_time));
        json_object.addProperty("DeviceStartSessionByUserId", device_start_session_by_user_id);

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.START_DEVICE_SESSION, json_object, android_database_row));
    }


    public void sendEndDeviceSessionToServer(int android_database_row, String device_session_id, long device_session_end_time, String device_session_ended_by_user_id)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("DeviceSessionID", device_session_id);
        json_object.addProperty("DeviceEndSessionTime", Utils.convertTimestampToServerSqlDate(device_session_end_time));
        json_object.addProperty("DeviceEndSessionByUserId", device_session_ended_by_user_id);

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.END_DEVICE_SESSION, json_object, android_database_row));
    }


    public void sendServerPing()
    {
        int android_database_row = 0;                                              // Dummy value
        String json_message = "";

        ServerPostParameters params = new ServerPostParameters(HttpOperationType.SERVER_PING);
        params.json_string = json_message;
        params.android_database_row = android_database_row;
        webServices.sendToServer(params);
    }


    public void sendCheckDeviceDetails(int device_type_as_int, long human_readable_device_id)
    {
        webServices.sendCheckDeviceDetails(device_type_as_int, human_readable_device_id);
    }


    public void sendCheckPatientId(String hospitalPatientId, int android_database_row)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("HospitalPatientId", hospitalPatientId);

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.CHECK_PATIENT_ID, json_object, android_database_row));
    }


    public void send_CheckStartPatientSession(int byPatientSessionId, long startTime, int android_database_row)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("ByPatientId", byPatientSessionId);
        json_object.addProperty("PatientStartSessionTime", Utils.convertTimestampToServerSqlDate(startTime));

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.CHECK_START_PATIENT_SESSION, json_object, android_database_row));
    }


    public void send_CheckEndPatientSession(int byPatientSessionId, long endTime, int android_database_row)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("ByPatientId", byPatientSessionId);
        json_object.addProperty("PatientEndSessionTime", Utils.convertTimestampToServerSqlDate(endTime));

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.CHECK_END_PATIENT_SESSION, json_object, android_database_row));
    }


    public void send_CheckStartDeviceSession(int byPatientSessionId, long startTime, int server_device_info_id, int android_database_row)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("ByPatientSessionId", byPatientSessionId);
        json_object.addProperty("ByDeviceId", server_device_info_id);
        json_object.addProperty("DeviceStartSessionTime", Utils.convertTimestampToServerSqlDate(startTime));

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.CHECK_START_DEVICE_SESSION, json_object, android_database_row));
    }


    public void send_CheckEndDeviceSession(int byPatientSessionId, long endTime, int server_device_info_id, int android_database_row)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("ByPatientSessionId", byPatientSessionId);
        json_object.addProperty("ByDeviceId", server_device_info_id);
        json_object.addProperty("DeviceEndSessionTime", Utils.convertTimestampToServerSqlDate(endTime));

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.CHECK_END_DEVICE_SESSION, json_object, android_database_row));
    }


    public void getWardDetailsListFromServer()
    {
        webServices.getWardDetailsListFromServer();
    }


    public void getBedDetailsListFromServer()
    {
        webServices.getBedDetailsListFromServer();
    }


    public void getGatewayConfigFromServer()
    {
        webServices.getGatewayConfigFromServer();
    }


    public void getServerConfigurableTextFromServer()
    {
        webServices.getServerConfigurableTextFromServer();
    }

    public void getWebPageDescriptorsFromServer()
    {
        webServices.getWebPageDescriptorsFromServer();
    }

    public void sendDefaultEarlyWarningScoreTypesRequest()
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("OnlyDefault", true);

        // Last "0" parameter is to used by other HttpGet webServices

        webServices.sendToServer(new ServerPostParameters(HttpOperationType.GET_DEFAULT_EARLY_WARNING_SCORES_LIST, json_object, 0));
    }


    public void sendGetLatestDeviceFirmwareVersionsRequest()
    {
        webServices.sendGetLatestDeviceFirmwareVersionsRequest();
    }


    public void sendGetFirmwareBinaryRequest(int servers_id)
    {
        webServices.sendGetFirmwareBinaryRequest(servers_id);
    }


    public void uploadLogFile(final File file)
    {
        webServices.uploadLogFile(file);
    }


    public boolean isRealTimeServerConnected()
    {
        return pubSubConnection.connected();
    }


    public void connectToRealTimeServer()
    {
        if (getRealTimeServerEnabledStatusFromSettings())
        {
            try
            {
                pubSubConnection.connect();
            }
            catch (Exception e)
            {
                Log.e(TAG, "connect Exception : " + e);
            }
        }
    }


    public void disconnectRealTimeServerIfConnected()
    {
        Log.e(TAG, "disconnectRealTimeServerIfConnected");

        try
        {
            pubSubConnection.disconnectIfConnected();
        }
        catch (Exception e)
        {
            Log.e(TAG, "disconnectRealTimeServerIfConnected Exception : " + e);
        }
    }


    /**
     * Disconnect the real-time server (WAMP or MQTT), then configure it.
     * <p/>
     * Connection will be re-created next time the ping timer fires (at most 10 seconds) so there's no need
     * to re-enable it here.
     */
    public void configureRealTimeServer()
    {
        String server_address = gateway_settings.getServerAddress();

        disconnectRealTimeServerIfConnected();

        String server_port = gateway_settings.getRealTimeServerPort();
        Log.d(TAG, "configureRealTimeServer : server_address = " + server_address + " : server_port = " + server_port + " : Type = " + gateway_settings.getRealTimeServerType().toString());

        // Configure the Realtime link.
        pubSubConnection.setHostName(server_address);
        pubSubConnection.setPort(server_port);

        if(gateway_settings.getRealTimeServerType() == RealTimeServer.MQTT)
        {
            sendMQTTCertificateRequest();
        }

        int bed_id = Integer.parseInt(gateway_settings.getGatewaysAssignedBedId());
        pubSubConnection.updateTopics(bed_id);

        // Do this last as it needs bed ID set, at least for MQTT
        pubSubConnection.setRealTimeServerType(gateway_settings.getRealTimeServerType());
    }


    public void sendDatabaseSyncStatus(JsonObject json_object)
    {
        pubSubConnection.sendDatabaseSyncStatus(json_object.toString());
    }


    public void sendDeviceSetupModeData(JsonObject json_object)
    {
        pubSubConnection.sendDeviceSetupModeData(json_object.toString());
    }


    public void sendDeviceRawAccelerometerModeEnabledState(int server_patient_session_id, SensorType sensor_type, boolean status)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("PatientSessionId", server_patient_session_id);
        json_object.addProperty("enabled", status);
        json_object.addProperty("sensor_type", sensor_type.ordinal());

        pubSubConnection.sendDeviceRawAccelerometerModeEnabledState(json_object);
    }


    public void sendDeviceRawAccelerometerModeData(JsonObject json_object)
    {
        pubSubConnection.sendDeviceRawAccelerometerModeData(json_object.toString());
    }


    public void requestPatientNameFromHospitalPatientDetailsId(String hospital_patient_id, String bed_id, String unique_id)
    {
        Log.d(TAG, "sendRequestPatientNameFromHospitalPatientDetailsId : " + hospital_patient_id);

        JsonObject json_object = new JsonObject();
        json_object.addProperty("hospitalPatientId", hospital_patient_id);
        json_object.addProperty("bedId", bed_id);
        json_object.addProperty("uniqueId", unique_id);

        pubSubConnection.subscribeToTopicReportPatientNameFromHospitalPatientDetailsId(unique_id);
        pubSubConnection.requestPatientNameFromHospitalPatientDetailsId(json_object);
    }


    public void requestPatientSpecificVideoCallContacts(int patientSessionId, String bedId, String uniqueId)
    {
        Log.d(TAG, "requestPatientSpecificVideoCallContacts : Servers Patient Session ID = " + patientSessionId);

        JsonObject json_object = new JsonObject();
        json_object.addProperty("patientSessionId", patientSessionId);
        json_object.addProperty("bedId", bedId);
        json_object.addProperty("uniqueId", uniqueId);

        pubSubConnection.subscribeToTopicVideoCallPatientContactsRequest(uniqueId);
        pubSubConnection.requestPatientSpecificVideoCallContacts(json_object);
    }


    public void sendDeviceLeadsOffStatus(int server_patient_session_id, DeviceInfo device_info, boolean enabled)
    {
        Log.d(TAG, "sendDeviceLeadsOffStatus : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : " + enabled + " for Servers Patient Session ID " + server_patient_session_id);

        JsonObject json_object = new JsonObject();
        json_object.addProperty("PatientSessionId", server_patient_session_id);
        json_object.addProperty("LeadsOff", enabled);
        json_object.addProperty("SensorType", device_info.sensor_type.ordinal());
        json_object.addProperty("DeviceType", device_info.device_type.ordinal());

        pubSubConnection.sendDeviceLeadsOffStatus(json_object);
    }


    public void sendGatewayStatus(JsonObject json_object)
    {
        Log.d(TAG, "sendGatewayStatus : " + json_object.toString());

        pubSubConnection.sendGatewayStatus(json_object.toString());
    }


    public void sendAllDeviceInfo(JsonObject json_object)
    {
        Log.d(TAG, "sendAllDeviceInfo : " + json_object.toString());

        // ToDo: Uncomment???
        //pubSubConnection.sendGatewayStatus(json_object.toString());
    }

    
    public void sendFeaturesEnabledStatus()
    {
        Log.d(TAG, "sendFeaturesEnabledStatus");

        JsonObject json_object = new JsonObject();
        json_object.addProperty("manual_vital_signs_entry_enabled", gateway_settings.getEnableManualVitalSignsEntry());
        json_object.addProperty("csv_output_enabled", gateway_settings.getCsvOutputEnabledStatus());
        json_object.addProperty("simple_heart_rate_enabled", gateway_settings.getSimpleHeartRateEnabledStatus());

        json_object.addProperty("show_numbers_on_battery_indicator_enabled", gateway_settings.getShowNumbersOnBatteryIndicator());
        json_object.addProperty("show_ip_address_on_wifi_popup_enabled", gateway_settings.getShowIpAddressOnWifiPopupEnabled());
        json_object.addProperty("run_devices_in_test_mode_enabled", gateway_settings.getRunDevicesInTestMode());
        json_object.addProperty("developer_popup_enabled", gateway_settings.getDeveloperPopupEnabled());
        json_object.addProperty("show_mac_on_device_status_enabled", gateway_settings.getShowMacAddressOnSettingsPage());
        json_object.addProperty("usa_mode_enabled", gateway_settings.getUsaOnlyMode());
        json_object.addProperty("lifetouch_activity_level_enabled", gateway_settings.getShowLifetouchActivityLevel());
        json_object.addProperty("session_auto_resume_enabled", gateway_settings.getAutoResumeEnabled());
        json_object.addProperty("auto_log_file_upload_enabled", gateway_settings.getEnableAutoUploadLogsToServer());
        json_object.addProperty("manufacturing_mode_enabled", gateway_settings.getManufacturingModeEnabledStatus());
        json_object.addProperty("back_camera_enabled", gateway_settings.getUseBackCameraEnabledStatus());
        json_object.addProperty("patient_orientation_enabled", gateway_settings.getEnablePatientOrientation());
        json_object.addProperty("wifi_logging_enabled", gateway_settings.getEnableWifiLogging());
        json_object.addProperty("gsm_logging_enabled", gateway_settings.getEnableGsmLogging());
        json_object.addProperty("database_logging_enabled", gateway_settings.getEnableDatabaseLogging());
        json_object.addProperty("server_logging_enabled", gateway_settings.getEnableServerLogging());
        json_object.addProperty("battery_logging_enabled", gateway_settings.getEnableBatteryLogging());
        json_object.addProperty("lifetemp_measurement_interval_in_seconds", gateway_settings.getLifetempMeasurementInterval());
        json_object.addProperty("patient_orientation_measurement_interval_in_seconds", gateway_settings.getPatientOrientationMeasurementIntervalInSeconds());
        json_object.addProperty("json_array_size", gateway_settings.getNumberOfDatabaseRowsPerJsonMessage());
        json_object.addProperty("video_calls_enabled", gateway_settings.getVideoCallsEnabledStatus());

        pubSubConnection.sendFeaturesEnabledStatus(json_object.toString());
    }


    public void sendGatewayPing()
    {
        pubSubConnection.sendGatewayPing();
    }


    public void sendDeviceSetupModeEnabledState(int server_patient_session_id, SensorType sensor_type, boolean enabled)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("PatientSessionId", server_patient_session_id);
        json_object.addProperty("enabled", enabled);
        json_object.addProperty("sensor_type", sensor_type.ordinal());

        pubSubConnection.sendDeviceSetupModeEnabledState(json_object);
    }

    public RealTimeServer getRealTimeServerType()
    {
        return pubSubConnection.getRealTimeServerType();
    }


    // VideoCallStatusFromGateway in Lifeguard
    public void sendGatewayVideoCallStatus(String connection_id, VideoCallStatus videoCallStatus, String bedId, long utcTimestamp)
    {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("videoCallStatus", videoCallStatus.ordinal());
        json_object.addProperty("connectionId", connection_id);
        json_object.addProperty("bedId", bedId);
        json_object.addProperty("dateTime", Utils.convertTimestampToServerSqlDate(utcTimestamp));

        pubSubConnection.sendGatewayVideoCallStatus(json_object);
    }

    public void sendPatientOnGatewayRequestingVideoCall(JsonObject json_object)
    {
        pubSubConnection.sendPatientOnGatewayRequestingVideoCall(json_object.toString());
    }


    public void sendMQTTCertificateRequest()
    {
        webServices.getMQTTCertificateFromServer();
    }
}
