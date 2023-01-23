package com.isansys.patientgateway.serverlink.webservices;


import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.isansys.common.WebPageDescriptor;
import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.HttpOperationType;
import com.isansys.patientgateway.BedInfo;
import com.isansys.patientgateway.ConfigOption;
import com.isansys.patientgateway.DeviceFirmwareDetails;
import com.isansys.patientgateway.LocalDatabaseStorage;
import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.RowsPending;
import com.isansys.patientgateway.ServerConfigurableText;
import com.isansys.patientgateway.WardInfo;
import com.isansys.patientgateway.database.RowRange;
import com.isansys.patientgateway.serverlink.ServerSyncStatus;
import com.isansys.patientgateway.serverlink.ServerSyncing;
import com.isansys.patientgateway.serverlink.ServiceGenerator;
import com.isansys.patientgateway.serverlink.constants.CheckServerLinkStatus;
import com.isansys.patientgateway.serverlink.constants.ServerResponseErrorCodes;
import com.isansys.patientgateway.serverlink.constants.ServerStatusReceivedCode;
import com.isansys.patientgateway.serverlink.interfaces.LifeguardInterface;
import com.isansys.patientgateway.serverlink.model.CheckDeviceStatusRequest;
import com.isansys.patientgateway.serverlink.model.CheckDeviceStatusResponse;
import com.isansys.patientgateway.serverlink.model.GetBedDetailsResponse;
import com.isansys.patientgateway.serverlink.model.GetDeviceFirmwareByBedIdRequest;
import com.isansys.patientgateway.serverlink.model.GetDeviceFirmwareByIdResponse;
import com.isansys.patientgateway.serverlink.model.GetDeviceFirmwareListRequest;
import com.isansys.patientgateway.serverlink.model.GetDeviceFirmwareListResponse;
import com.isansys.patientgateway.serverlink.model.GetGatewayConfigRequest;
import com.isansys.patientgateway.serverlink.model.GetGatewayConfigResponse;
import com.isansys.patientgateway.serverlink.model.GetMQTTCertificateResponse;
import com.isansys.patientgateway.serverlink.model.GetServerConfigurableTextRequest;
import com.isansys.patientgateway.serverlink.model.GetServerConfigurableTextResponse;
import com.isansys.patientgateway.serverlink.model.GetWardDetailsResponse;
import com.isansys.patientgateway.serverlink.model.GetWebPagesResponse;
import com.isansys.patientgateway.serverlink.model.ServerPostParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class WebServices
{
    final RemoteLoggingWithEnable Log;
    protected final String TAG = this.getClass().getSimpleName(); // ToDo - get parent class name

    protected final ServerSyncing.SessionInformation current_session_information;
    protected final ServerSyncStatus server_sync_status;

    protected final LocalDatabaseStorage local_database_storage;

    protected final PatientGatewayInterface patient_gateway_interface;
    protected final ServerSyncing server_syncing;
    public volatile boolean server_interface_busy = false;
    protected boolean authentication_ok = true;

    protected String server_address;                                     // Address all the HTTP Posts are sent to

    final String gateway_unique_id;

    boolean use_https = false;                                                  // Use HTTP by default
    boolean use_authentication_for_webservice_calls = true;                     // Use Authentication For WebService Calls
    boolean use_encryption_on_webservice_calls = true;                          // Use Encryption For WebService Calls

    private LifeguardInterface lifeguard_interface;
    private final ServiceGenerator service_generator;
    final IsansysAuthentication auth;

    public WebServices(RemoteLoggingWithEnable logger, ServerSyncing.SessionInformation session_information, PatientGatewayInterface patient_gateway_interface, ServerSyncing desired_server_syncing, ServerSyncStatus sync_status, LocalDatabaseStorage database_storage, String passed_in_gateway_unique_id)
    {
        Log = logger;
        current_session_information = session_information;
        server_sync_status = sync_status;
        local_database_storage = database_storage;

        this.patient_gateway_interface = patient_gateway_interface;
        this.server_syncing = desired_server_syncing;

        server_address = patient_gateway_interface.getNotSetYetString();

        gateway_unique_id = passed_in_gateway_unique_id;

        auth = new IsansysAuthentication(logger, passed_in_gateway_unique_id, patient_gateway_interface);

        service_generator = new ServiceGenerator(auth);

        lifeguard_interface = service_generator.createService(LifeguardInterface.class);
    }


    public void setIsansysServerAddress(String desired_address, String desired_webservice_port)
    {
        server_address = desired_address + ":" + desired_webservice_port;

        // If its the default port 80/443, then remove it, so Authentication can work on the Lifeguard and it compares the URL's (and strips out the :80 or :443 at the server)
        if (Integer.parseInt(desired_webservice_port) == 80 || Integer.parseInt(desired_webservice_port) == 443)
        {
            server_address = desired_address;
        }

        Log.d(TAG, "HTTP setIsansysServerAddress : use https = " + use_https);
        Log.d(TAG, "HTTP setIsansysServerAddress : use server_address = " + server_address);

        service_generator.changeApiBaseUrl(server_address, use_https);
        lifeguard_interface = service_generator.createService(LifeguardInterface.class);
    }

    public void useEncryptionForWebServiceCalls(boolean enabled)
    {
        use_encryption_on_webservice_calls = enabled;

        service_generator.useEncryption(enabled);
        lifeguard_interface = service_generator.createService(LifeguardInterface.class);
    }


    public void useHttps(boolean enabled)
    {
        use_https = enabled;
    }


    public void useAuthenticationForWebServiceCalls(boolean enabled)
    {
        use_authentication_for_webservice_calls = enabled;

        service_generator.useAuthentication(enabled);
        lifeguard_interface = service_generator.createService(LifeguardInterface.class);
    }


    /**
     *  updateCurrentSessionInformation_NotSendToServer is called when socket timeout exception happens. In case of IO exception, change the Session Information parameters back to IDLE.
     */
    protected void post_updateCurrentSessionInformation_NotSendToServer()
    {
        if(current_session_information.patientDetailSendToServer_Status == CheckServerLinkStatus.CHECKING || current_session_information.patientDetailSendToServer_Status == CheckServerLinkStatus.SEND_PACKET)
        {
            current_session_information.patientDetailSendToServer_Status = CheckServerLinkStatus.IDLE;
        }
        else if(current_session_information.deviceInfoSendToServer_Status == CheckServerLinkStatus.CHECKING || current_session_information.deviceInfoSendToServer_Status == CheckServerLinkStatus.SEND_PACKET)
        {
            current_session_information.deviceInfoSendToServer_Status = CheckServerLinkStatus.IDLE;
        }
        else if(current_session_information.startPatientSessionSendToServer_Status == CheckServerLinkStatus.CHECKING || current_session_information.startPatientSessionSendToServer_Status == CheckServerLinkStatus.SEND_PACKET)
        {
            current_session_information.startPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
        }
        else if(current_session_information.endPatientSessionSendToServer_Status == CheckServerLinkStatus.CHECKING || current_session_information.endPatientSessionSendToServer_Status == CheckServerLinkStatus.SEND_PACKET)
        {
            current_session_information.endPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
        }
        else if(current_session_information.startDeviceSessionSendToServer_Status == CheckServerLinkStatus.CHECKING || current_session_information.startDeviceSessionSendToServer_Status == CheckServerLinkStatus.SEND_PACKET)
        {
            current_session_information.startDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
        }
        else if(current_session_information.endDeviceSessionSendToServer_Status == CheckServerLinkStatus.CHECKING || current_session_information.endDeviceSessionSendToServer_Status == CheckServerLinkStatus.SEND_PACKET)
        {
            current_session_information.endDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
        }
    }


    protected void processServersWebserviceResponse(Boolean result, ServerResponseErrorCodes server_http_post_error_code, String json_string, ServerPostParameters server_post_parameters)
    {
        final int ERROR_IN_SERVER_RESPONSE = -1;
        final int PASSED_SERVER_CHECK = 0;

        Log.d(TAG, "processServersWebserviceResponse : " + server_post_parameters.http_operation_type + ". result = " + result);

        boolean server_response_error_for_patient_or_device_session_data = false;

        try
        {
            switch (server_post_parameters.http_operation_type)
            {
                case DEVICE_INFO:
                {
                    server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_syncable--;

                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int servers_device_info_id = json.get("DeviceInfoId").getAsInt();

                        Log.d(TAG, "DEVICE_INFO : servers_device_info_id = " + servers_device_info_id);

                        local_database_storage.deviceInfoSuccessfullySentToServer(server_post_parameters.android_database_row, servers_device_info_id);
                    }
                    else
                    {
                        server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_but_failed++;

                        local_database_storage.deviceInfoFailedSendingToServer(server_post_parameters.android_database_row);
                    }

                    server_syncing.currentSessionInformation.deviceInfoSendToServer_Status = CheckServerLinkStatus.IDLE;
                }
                break;

                case START_PATIENT_SESSION:
                {
                    server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_syncable--;

                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int servers_patient_session_id = json.get("PatientSessionId").getAsInt();

                        patient_gateway_interface.setServersPatientSessionId(servers_patient_session_id);

                        Log.i(TAG, "START_PATIENT_SESSION : servers_session_id = " + servers_patient_session_id);

                        local_database_storage.patientStartSessionSuccessfullySentToServer(server_post_parameters.android_database_row, servers_patient_session_id);
                    }
                    else
                    {
                        server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_but_failed++;

                        local_database_storage.patientStartSessionFailedSendingToServer(server_post_parameters.android_database_row);
                    }

                    server_syncing.currentSessionInformation.startPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
                }
                break;

                case END_PATIENT_SESSION:
                {
                    server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_syncable--;

                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int servers_patient_session_id = json.get("PatientSessionId").getAsInt();

                        Log.d(TAG, "END_PATIENT_SESSION : servers_session_id = " + servers_patient_session_id);

                        local_database_storage.patientEndSessionSuccessfullySentToServer(server_post_parameters.android_database_row, servers_patient_session_id);
                    }
                    else
                    {
                        server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_but_failed++;

                        local_database_storage.patientEndSessionFailedSendingToServer(server_post_parameters.android_database_row);
                    }

                    server_syncing.currentSessionInformation.endPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
                }
                break;

                case PATIENT_SESSION_FULLY_SYNCED:
                {
                    local_database_storage.updatePatientSessionFullySyncedSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case START_DEVICE_SESSION:
                {
                    server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_syncable--;

                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int servers_device_session_id = json.get("DeviceSessionID").getAsInt();
                        //int servers_device_session_id = Integer.parseInt(reader.getString("DeviceSessionID"));

                        Log.d(TAG, "START_DEVICE_SESSION : servers_device_session_id = " + servers_device_session_id);

                        local_database_storage.deviceStartSessionSuccessfullySentToServer(server_post_parameters.android_database_row, servers_device_session_id);
                    }
                    else
                    {
                        server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_but_failed++;

                        local_database_storage.deviceStartSessionFailedSendingToServer(server_post_parameters.android_database_row);
                    }

                    server_syncing.currentSessionInformation.startDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
                }
                break;

                case END_DEVICE_SESSION:
                {
                    server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_syncable--;

                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        Log.d(TAG, "END_DEVICE_SESSION : device_session_id = " + json.get("DeviceSessionID").getAsInt());

                        local_database_storage.deviceEndSessionSuccessfullySentToServer(server_post_parameters.android_database_row);
                    }
                    else
                    {
                        server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_but_failed++;

                        local_database_storage.deviceEndSessionFailedSendingToServer(server_post_parameters.android_database_row);
                    }

                    server_syncing.currentSessionInformation.endDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
                }
                break;

                case PATIENT_DETAILS:
                {
                    server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_syncable--;

                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int servers_patient_details_id = json.get("PatientDetailsId").getAsInt();
                        Log.d(TAG, "PATIENT_DETAILS : servers_patient_id = " + servers_patient_details_id);

                        local_database_storage.patientDetailsSuccessfullySentToServer(server_post_parameters.android_database_row, servers_patient_details_id);
                    }
                    else
                    {
                        server_sync_status.getRowsPending(server_post_parameters.http_operation_type).rows_pending_but_failed++;

                        local_database_storage.patientDetailsFailedSendingToServer(server_post_parameters.android_database_row);
                    }

                    server_syncing.currentSessionInformation.patientDetailSendToServer_Status = CheckServerLinkStatus.IDLE;
                }
                break;

                case CONNECTION_EVENT:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateConnectionEventSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case LIFETOUCH_HEART_RATE:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateLifetouchHeartMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case LIFETOUCH_RESPIRATION_RATE:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateLifetouchRespirationMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case LIFETOUCH_HEART_BEAT:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateLifetouchHeartBeatsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case LIFETOUCH_SETUP_MODE_SAMPLE:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateLifetouchSetupModeSamplesSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateLifetouchRawAccelerometerModeSamplesSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case LIFETOUCH_BATTERY:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateLifetouchBatteryMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case LIFETOUCH_PATIENT_ORIENTATION:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.patientOrientationsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case LIFETEMP_TEMPERATURE:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateLifetempTemperatureMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case LIFETEMP_BATTERY:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateLifetempBatteryMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case PULSE_OX_MEASUREMENT:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateOximeterMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case PULSE_OX_INTERMEDIATE_MEASUREMENT:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateOximeterIntermediateMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case PULSE_OX_SETUP_MODE_SAMPLE:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateOximeterSetupModeSamplesSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case PULSE_OX_BATTERY:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateOximeterBatteryMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case BLOOD_PRESSURE_MEASUREMENT:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateBloodPressureMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case BLOOD_PRESSURE_BATTERY:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateBloodPressureBatteryMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case WEIGHT_SCALE_MEASUREMENT:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateWeightScaleMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;
                case WEIGHT_SCALE_BATTERY:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateWeightScaleBatteryMeasurementsSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_HEART_RATES:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredHeartRateMeasurementSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_RESPIRATION_RATES:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredRespirationRateMeasurementSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_TEMPERATURES:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredTemperatureMeasurementSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredSpO2MeasurementSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredBloodPressureMeasurementSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredWeightMeasurementSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredConsciousnessLevelMeasurementSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredSupplementalOxygenLevelMeasurementSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_ANNOTATIONS:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredAnnotationSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredCapillaryRefillTimeSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredFamilyOrNurseConcernTimeSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredRespirationDistressTimeSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case MANUALLY_ENTERED_URINE_OUTPUT:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateManuallyEnteredUrineOutputTimeSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case EARLY_WARNING_SCORES:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateEarlyWarningScoresSentToServerStatus(result, server_post_parameters.row_ranges);
                }
                break;

                case SETUP_MODE_LOG:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    if(result)
                    {
                        JSONArray json_array = new JSONArray(json_string);

                        for (int i = 0; i < json_array.length(); i++)
                        {
                            JSONObject json_data = json_array.getJSONObject(i);

                            int local_database_row = Integer.parseInt(json_data.getString("LocalDatabaseRow"));

                            int status_code = Integer.parseInt(json_data.getString("StatusCode"));

                            ServerStatusReceivedCode received_server_status_code = ServerStatusReceivedCode.values()[status_code];

                            boolean server_status_valid = received_server_status_code == ServerStatusReceivedCode.RESPONSE_CODE__VALID;

                            local_database_storage.updateSetupModeLogTimestampSentToServerStatus(server_status_valid, local_database_row);
                        }
                    }
                    else
                    {
                        Log.e(TAG, "SETUP_MODE_LOG : result was false");

                        local_database_storage.updateSetupModeLogTimestampSentToServerStatusFailedByRange(server_post_parameters.row_ranges);
                    }
                }
                break;

                case SERVER_PING:
                {
                    try
                    {
                        patient_gateway_interface.handlePingResponse(result, authentication_ok);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "SERVER_PING Exception = " + e.getMessage());
                    }
                }
                break;

                case CHECK_PATIENT_ID:
                {
                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int servers_patient_details_id = json.get("PatientDetailsId").getAsInt();
                        boolean patient_id_in_use = json.get("AlreadyUse").getAsBoolean();

                        // There are two condition for sending check request to server 1) when patientId is enter UI fragment and 2) is when starting the session.
                        // Condition below separates the two conditions
                        if(server_syncing.currentSessionInformation.patientDetailSendToServer_Status == CheckServerLinkStatus.CHECKING)
                        {
                            Log.e(TAG, "PATIENT_DETAILS : servers_patient_details_id = " + servers_patient_details_id + ". HTTP Message = init");

                            switch (servers_patient_details_id)
                            {
                                case ERROR_IN_SERVER_RESPONSE:
                                {
                                    // Some error in the server
                                    server_syncing.currentSessionInformation.patientDetailSendToServer_Status = CheckServerLinkStatus.IDLE;

                                    // Report ServerSyncTableObserver about the server response error
                                    server_response_error_for_patient_or_device_session_data = true;
                                }
                                break;

                                case PASSED_SERVER_CHECK:
                                {
                                    server_syncing.currentSessionInformation.patientDetailSendToServer_Status = CheckServerLinkStatus.SEND_PACKET;
                                }
                                break;

                                // servers_patient_details_id will be the Servers PatientDetailsId
                                default:
                                {
                                    server_syncing.currentSessionInformation.patientDetailSendToServer_Status = CheckServerLinkStatus.IDLE;

                                    Log.d(TAG, "PATIENT_DETAILS : servers_patient_id = " + servers_patient_details_id + " : android_database_row = " + server_post_parameters.android_database_row);

                                    // Successfully sent Patient Details.
                                    local_database_storage.patientDetailsSuccessfullySentToServer(server_post_parameters.android_database_row, servers_patient_details_id);
                                }
                                break;
                            }
                        }
                        else
                        {
                            patient_gateway_interface.handlePatientIdStatusResponse(true, patient_id_in_use);
                        }
                    }
                    else
                    {
                        local_database_storage.patientDetailsFailedSendingToServer(server_post_parameters.android_database_row);

                        server_syncing.currentSessionInformation.patientDetailSendToServer_Status = CheckServerLinkStatus.IDLE;
                        patient_gateway_interface.handlePatientIdStatusResponse(false, true);
                    }
                }
                break;

                case CHECK_START_PATIENT_SESSION:
                {
                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int response_value = json.get("PatientSessionId").getAsInt();

                        Log.e(TAG, "CHECK_START_PATIENT_SESSION : response_value = " + response_value + ". HTTP Message = " + json.get("MessageStatus").getAsString());

                        switch (response_value)
                        {
                            case ERROR_IN_SERVER_RESPONSE:
                            {
                                // Some error in the server
                                server_syncing.currentSessionInformation.startPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                                // Report ServerSyncTableObserver about the server response error
                                server_response_error_for_patient_or_device_session_data = true;
                            }
                            break;

                            case PASSED_SERVER_CHECK:
                            {
                                server_syncing.currentSessionInformation.startPatientSessionSendToServer_Status = CheckServerLinkStatus.SEND_PACKET;
                            }
                            break;

                            default:
                            {
                                server_syncing.currentSessionInformation.startPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                                // Mark current start patient session as sent
                                int servers_patient_session_id = json.get("PatientSessionId").getAsInt();

                                patient_gateway_interface.setServersPatientSessionId(servers_patient_session_id);

                                Log.i(TAG, "START_PATIENT_SESSION : servers_session_id = " + servers_patient_session_id);

                                local_database_storage.patientStartSessionSuccessfullySentToServer(server_post_parameters.android_database_row, servers_patient_session_id);
                            }
                            break;
                        }
                    }
                    else
                    {
                        local_database_storage.patientStartSessionFailedSendingToServer(server_post_parameters.android_database_row);

                        server_syncing.currentSessionInformation.startPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
                    }
                }
                break;

                case CHECK_END_PATIENT_SESSION:
                {
                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int response_value = json.get("PatientSessionId").getAsInt();
                        String response_message = json.get("MessageStatus").getAsString();

                        Log.e(TAG, "CHECK_END_PATIENT_SESSION : response_value = " + response_value + ". HTTP Message = " + response_message);

                        switch (response_value)
                        {
                            case ERROR_IN_SERVER_RESPONSE:
                            {
                                // Some error in the server
                                server_syncing.currentSessionInformation.endPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                                // Report ServerSyncTableObserver about the server response error
                                server_response_error_for_patient_or_device_session_data = true;
                            }
                            break;

                            case PASSED_SERVER_CHECK:
                            {
                                server_syncing.currentSessionInformation.endPatientSessionSendToServer_Status = CheckServerLinkStatus.SEND_PACKET;
                            }
                            break;

                            default:
                            {
                                server_syncing.currentSessionInformation.endPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                                int servers_patient_session_id = json.get("PatientSessionId").getAsInt();

                                Log.d(TAG, "END_PATIENT_SESSION : servers_session_id = " + servers_patient_session_id);

                                local_database_storage.patientEndSessionSuccessfullySentToServer(server_post_parameters.android_database_row, servers_patient_session_id);
                            }
                            break;
                        }
                    }
                    else
                    {
                        local_database_storage.patientEndSessionFailedSendingToServer(server_post_parameters.android_database_row);

                        server_syncing.currentSessionInformation.endPatientSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
                    }
                }
                break;

                case CHECK_START_DEVICE_SESSION:
                {
                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int response_value = json.get("DeviceSessionID").getAsInt();
                        String response_message = json.get("MessageStatus").getAsString();

                        Log.e(TAG, "CHECK_START_DEVICE_SESSION : response_value = " + response_value + ". HTTP Message = " + response_message);

                        switch (response_value)
                        {
                            case ERROR_IN_SERVER_RESPONSE:
                            {
                                // Some error in the server
                                server_syncing.currentSessionInformation.startDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                                // Report ServerSyncTableObserver about the server response error
                                server_response_error_for_patient_or_device_session_data = true;
                            }
                            break;

                            case PASSED_SERVER_CHECK:
                            {
                                server_syncing.currentSessionInformation.startDeviceSessionSendToServer_Status = CheckServerLinkStatus.SEND_PACKET;
                            }
                            break;

                            default:
                            {
                                server_syncing.currentSessionInformation.startDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                                int servers_device_session_id = json.get("DeviceSessionID").getAsInt();

                                Log.d(TAG, "START_DEVICE_SESSION : servers_device_session_id = " + servers_device_session_id);

                                local_database_storage.deviceStartSessionSuccessfullySentToServer(server_post_parameters.android_database_row, servers_device_session_id);
                            }
                            break;
                        }
                    }
                    else
                    {
                        local_database_storage.deviceStartSessionFailedSendingToServer(server_post_parameters.android_database_row);

                        server_syncing.currentSessionInformation.startDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
                    }
                }
                break;

                case CHECK_END_DEVICE_SESSION:
                {
                    if (result)
                    {
                        JsonObject json = JsonParser.parseString(json_string).getAsJsonObject();

                        int response_value = json.get("DeviceSessionID").getAsInt();
                        String response_message = json.get("MessageStatus").getAsString();

                        Log.e(TAG, "CHECK_END_DEVICE_SESSION : response_value = " + response_value + ". HTTP Message = " + response_message);

                        switch (response_value)
                        {
                            case ERROR_IN_SERVER_RESPONSE:
                            {
                                // Some error in the server
                                server_syncing.currentSessionInformation.endDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                                // Report ServerSyncTableObserver about the server response error
                                server_response_error_for_patient_or_device_session_data = true;
                            }
                            break;

                            case PASSED_SERVER_CHECK:
                            {
                                server_syncing.currentSessionInformation.endDeviceSessionSendToServer_Status = CheckServerLinkStatus.SEND_PACKET;
                            }
                            break;

                            default:
                            {
                                server_syncing.currentSessionInformation.endDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;

                                Log.d(TAG, "END_DEVICE_SESSION : device_session_id = " + json.get("DeviceSessionID").getAsString());

                                local_database_storage.deviceEndSessionSuccessfullySentToServer(server_post_parameters.android_database_row);
                            }
                            break;
                        }
                    }
                    else
                    {
                        local_database_storage.deviceEndSessionFailedSendingToServer(server_post_parameters.android_database_row);

                        server_syncing.currentSessionInformation.endDeviceSessionSendToServer_Status = CheckServerLinkStatus.IDLE;
                    }
                }
                break;

                case GET_DEFAULT_EARLY_WARNING_SCORES_LIST:
                {
                    if (result)
                    {
                        patient_gateway_interface.handleGetEarlyWarningScoresResponse(json_string);
                    }
                }
                break;

                // Only for MQTT as HTTP goes via LifeguardInterface
                case WARD_DETAILS_LIST:
                {
                    if(result)
                    {
                        WardInfo[] all_ward_info = new Gson().fromJson(json_string, WardInfo[].class);

                        ArrayList<WardInfo> ward_info_list = new ArrayList<>();

                        Collections.addAll(ward_info_list, all_ward_info);

                        patient_gateway_interface.handleReceivedWardList(ward_info_list);
                    }
                }
                break;

                // Only for MQTT as HTTP goes via LifeguardInterface
                case BED_DETAILS_LIST:
                {
                    if(result)
                    {
                        BedInfo[] all_bed_info = new Gson().fromJson(json_string, BedInfo[].class);

                        ArrayList<BedInfo> bed_info_list = new ArrayList<>();

                        Collections.addAll(bed_info_list, all_bed_info);

                        patient_gateway_interface.handleReceivedBedList(bed_info_list);
                    }
                }
                break;

                // Only for MQTT as HTTP goes via LifeguardInterface
                case CHECK_DEVICE_DETAILS:
                {
                    if(result)
                    {
                        CheckDeviceStatusResponse check_device_status_response = new Gson().fromJson(json_string, CheckDeviceStatusResponse.class);
                        String ward_name = check_device_status_response.getWardName();
                        String bed_name = check_device_status_response.getBedName();
                        DeviceType device_type = DeviceType.values()[check_device_status_response.getDeviceType()];
                        int human_readable_device_id = check_device_status_response.getByHumanReadableDeviceId();
                        boolean device_in_use = check_device_status_response.isInUse();

                        Log.w(TAG, "CHECK_DEVICE_DETAILS : " + check_device_status_response);

                        patient_gateway_interface.handleGetDeviceStatusResponse(true, ward_name, bed_name, device_type, human_readable_device_id, device_in_use);
                    }
                }
                break;

                // Only for MQTT as HTTP goes via LifeguardInterface
                case DOWNLOAD_DEVICE_FIRMWARE_BY_ID:
                {
                    if(result)
                    {
                        GetDeviceFirmwareByIdResponse firmware_response = new Gson().fromJson(json_string, GetDeviceFirmwareByIdResponse.class);

                        DeviceType device_type = DeviceType.values()[firmware_response.getDeviceType()];

                        int firmware_version = firmware_response.getFirmwareVersion();
                        String firmware_image_base64 = firmware_response.getFirmwareImage();

                        if (firmware_image_base64 != null)
                        {
                            byte[] firmware_image = Base64.decode(firmware_image_base64, Base64.DEFAULT);
                            int servers_id = firmware_response.getDeviceFirmwareId();

                            String file_name = firmware_response.getFirmwareFileName();

                            patient_gateway_interface.handleReceivedFirmwareImage(servers_id, device_type, firmware_version, firmware_image, file_name);
                        }
                        else
                        {
                            Log.e(TAG, "firmware_image_base64 is NULL !!!!!!!!!!!!");
                        }
                    }
                }
                break;

                // Only for MQTT as HTTP goes via LifeguardInterface
                case GET_DEVICE_FIRMWARE_VERSION_LIST:
                {
                    if(result)
                    {
                        ArrayList<DeviceFirmwareDetails> device_firmware_details_list = new ArrayList<>();

                        DeviceFirmwareDetails[]device_firmware_details = new Gson().fromJson(json_string, DeviceFirmwareDetails[].class);

                        Collections.addAll(device_firmware_details_list, device_firmware_details);

                        patient_gateway_interface.handleReceivedDeviceFirmwareVersionList(device_firmware_details_list);
                    }
                }
                break;

                // Only for MQTT as HTTP goes via LifeguardInterface
                case GET_GATEWAY_CONFIG:
                {
                    if(result)
                    {
                        ArrayList<ConfigOption> config_options_list = new ArrayList<>();

                        ConfigOption[] config_options = new Gson().fromJson(json_string, ConfigOption[].class);

                        Collections.addAll(config_options_list, config_options);

                        patient_gateway_interface.handleReceivedGatewayConfig(config_options_list);
                    }
                }
                break;

                // Only for MQTT as HTTP goes via LifeguardInterface
                case GET_SERVER_CONFIGURABLE_TEXT:
                {
                    if(result)
                    {
//TODO
                        /*
                        ArrayList<ServerConfigurableText> string_list = new ArrayList<>();

                        for (GetServerConfigurableTextResponse string_info : response.body())
                        {
                            string_list.add(new ServerConfigurableText(string_info.getServersId(), string_info.getStringType(), string_info.getStringText()));
                        }

                        patient_gateway_interface.handleReceivedServerConfigurableText(string_list);
                        */
                    }
                }
                break;

                case GET_VIEWABLE_WEBPAGES:
                {
                    if(result)
                    {
//TODO
                    }
                }
                break;

                case AUDITABLE_EVENTS:
                {
                    updateRowsPendingWithResults(result, server_post_parameters);

                    local_database_storage.updateAuditableEventsSentToServerStatusAndDeleteFromGatewayDatabaseIfSucceeded(result, server_post_parameters.row_ranges);
                }
                break;

                // Will never happen for this Case Statement as these are HTTP Gets but need to handle them to prevent warning
                case INVALID:
                {
                    // These are here instead of a default case on purpose, so if new ones are added Android Lint will complain about missing entries
                }
                break;
            }
        }
        catch (Throwable t)
        {
            Log.e(TAG, "processServersWebserviceResponse : Could not parse malformed JSON: \"" + json_string + "\"");
        }

        server_interface_busy = false;

        // HTTP posts for SERVER_PING, CHECK_PATIENT_ID don't use server sync state machine.
        // SERVER_PING is sent by pressing the Ping button in Admin page or every 10 sec from PatientGateway timer.
        // These event should be independent to the State Machine.
        // CHECK_PATIENT_ID should be done instantaneously. Not implemented in the state machine.
        if(server_post_parameters.http_operation_type == HttpOperationType.SERVER_PING || server_post_parameters.http_operation_type == HttpOperationType.CHECK_PATIENT_ID)
        {
            Log.d(TAG, "HTTP Post type = " + server_post_parameters.http_operation_type + ". Not updating ServerSyncTableObserver state");
        }
        else
        {
            server_syncing.server_sync_table_observer.checkAndChangeServerSyncStateFromServerPostResponse(result, server_http_post_error_code, server_response_error_for_patient_or_device_session_data);
        }

        patient_gateway_interface.handleServerResponseComplete(result, server_post_parameters.http_operation_type, server_post_parameters.active_or_old_session);
    }


    private int getNumberOfRowsFromRowRanges(ServerPostParameters server_post_parameters)
    {
        int count = 0;

        for (RowRange rowRange : server_post_parameters.row_ranges)
        {
            Log.d(TAG, "updateRowsPendingWithResults : Row Range = " + rowRange.start + " to " + rowRange.end);
            count = count + ((rowRange.end - rowRange.start) + 1);
        }

        return count;
    }


    // The Rows Pending object is also updated inside ServerSyncTableObserver setDataUploadPoint so make sure this doesnt happen at the same time
    private void updateRowsPendingWithResults(boolean result, ServerPostParameters server_post_parameters)
    {
        int count = getNumberOfRowsFromRowRanges(server_post_parameters);

        String logLine = "updateRowsPendingWithResults : " + result + " : " + count + " : " + server_post_parameters.http_operation_type + " : Before = ";
        RowsPending rowsPending = server_sync_status.getRowsPending(server_post_parameters.http_operation_type, server_post_parameters.active_or_old_session);
        logLine = logLine + rowsPending.toString();

        // If something is synced to the server BEFORE the relevant rowsPending structure has been init'ed then it will default to -1...causing bad things to happen
        if (rowsPending.rows_pending_syncable > 0)
        {
            rowsPending.rows_pending_syncable = rowsPending.rows_pending_syncable - count;
        }

        if (!result)
        {
            // If something is synced to the server BEFORE the relevant rowsPending structure has been init'ed then it will default to -1...causing bad things to happen
            if (rowsPending.rows_pending_but_failed >= 0)
            {
                rowsPending.rows_pending_but_failed = rowsPending.rows_pending_but_failed + count;
            }
        }

        logLine = logLine + " : After = " + rowsPending;
        Log.e(TAG, logLine);
    }


    protected void logLongLine(String description, String string)
    {
        /*
        if (string != null)
        {
            if (string.length() > 100000)
            {
                Log.e(TAG, "logLongLine : " + description + " : Too big : " + string.length());
            }
            else
            {
                ArrayList<String> lines = Utils.splitString(string);
                for (String line : lines)
                {
                    Log.d(TAG, line);
                }
            }
        }
        */
    }


    public abstract void sendToServer(ServerPostParameters params);


    private void handleFailedResponse(String function_name, Response response)
    {
        int status_code = response.code();
        String status_message = response.message();

        Log.e(TAG, function_name + " not successful : Status Code = " + status_code + " : Status Message = " + status_message);
    }


    private void handleFailureToQueueUpRequest(String function_name, HttpOperationType http_operation_type, Throwable t)
    {
        Log.e(TAG, function_name + " Error : " + t.toString());

        patient_gateway_interface.handleServerResponseComplete(false, http_operation_type, ActiveOrOldSession.INVALID);
    }


    public void sendCheckDeviceDetails(int device_type_as_int, long human_readable_device_id)
    {
        final HttpOperationType http_operation_type = HttpOperationType.CHECK_DEVICE_DETAILS;

        CheckDeviceStatusRequest request = new CheckDeviceStatusRequest(device_type_as_int, human_readable_device_id);

        lifeguard_interface.checkDeviceDetails(request).enqueue(new Callback<CheckDeviceStatusResponse>()
        {
            @Override
            public void onResponse(Call<CheckDeviceStatusResponse> call, Response<CheckDeviceStatusResponse> response)
            {
                if(response.isSuccessful())
                {
                    CheckDeviceStatusResponse check_device_status_response = response.body();

                    if (check_device_status_response != null)
                    {
                        try
                        {
                            String ward_name = check_device_status_response.getWardName();
                            String bed_name = check_device_status_response.getBedName();
                            DeviceType device_type = DeviceType.values()[check_device_status_response.getDeviceType()];
                            int human_readable_device_id = check_device_status_response.getByHumanReadableDeviceId();
                            boolean device_in_use = check_device_status_response.isInUse();
                            //String message = check_device_status_response.getMessageStatus();

                            Log.w(TAG, "CHECK_DEVICE_DETAILS : " + check_device_status_response);

                            patient_gateway_interface.handleGetDeviceStatusResponse(true, ward_name, bed_name, device_type, human_readable_device_id, device_in_use);
                        }
                        catch (Exception ee)
                        {
                            patient_gateway_interface.handleGetDeviceStatusResponse(false, "", "", DeviceType.DEVICE_TYPE__INVALID, 0, false);
                        }
                    }
                }
                else
                {
                    handleFailedResponse("sendCheckDeviceDetails", response);
                }

                patient_gateway_interface.handleServerResponseComplete(response.isSuccessful(), http_operation_type, ActiveOrOldSession.INVALID);
            }

            @Override
            public void onFailure(Call<CheckDeviceStatusResponse> call, Throwable t)
            {
                // Cant queue the command up. Does NOT mean a failure from the Server
                handleFailureToQueueUpRequest("sendCheckDeviceDetails", http_operation_type, t);
            }
        });
    }


    public void getWardDetailsListFromServer()
    {
        final HttpOperationType http_operation_type = HttpOperationType.WARD_DETAILS_LIST;

        lifeguard_interface.getWardDetailsList().enqueue(new Callback<List<GetWardDetailsResponse>>()
        {
            @Override
            public void onResponse(Call<List<GetWardDetailsResponse>> call, Response<List<GetWardDetailsResponse>> response)
            {
                patient_gateway_interface.handleServerResponseComplete(response.isSuccessful(), http_operation_type, ActiveOrOldSession.INVALID);

                if(response.isSuccessful() && response.body() != null)
                {
                    ArrayList<WardInfo> ward_info_list = new ArrayList<>();

                    for (GetWardDetailsResponse ward_details : response.body())
                    {
                        WardInfo this_ward_info = new WardInfo(ward_details.getWardDetailsId(), ward_details.getWardName());

                        ward_info_list.add(this_ward_info);

                        Log.d(TAG, "ward_details_id = " + this_ward_info.ward_details_id + " : ward_name = " + this_ward_info.ward_name);
                    }

                    patient_gateway_interface.handleReceivedWardList(ward_info_list);
                }
                else
                {
                    handleFailedResponse("getWardDetailsListFromServer", response);
                }
            }

            @Override
            public void onFailure(Call<List<GetWardDetailsResponse>> call, Throwable t)
            {
                // Cant queue the command up. Does NOT mean a failure from the Server
                handleFailureToQueueUpRequest("getWardDetailsListFromServer", http_operation_type, t);
            }
        });
    }


    public void getBedDetailsListFromServer()
    {
        final HttpOperationType http_operation_type = HttpOperationType.BED_DETAILS_LIST;

        lifeguard_interface.getBedDetailsList().enqueue(new Callback<List<GetBedDetailsResponse>>()
        {
            @Override
            public void onResponse(Call<List<GetBedDetailsResponse>> call, Response<List<GetBedDetailsResponse>> response)
            {
                patient_gateway_interface.handleServerResponseComplete(response.isSuccessful(), http_operation_type, ActiveOrOldSession.INVALID);

                if(response.isSuccessful() && response.body() != null)
                {
                    ArrayList<BedInfo> bed_info_list = new ArrayList<>();

                    for (GetBedDetailsResponse bed_details : response.body())
                    {
                        BedInfo this_bed_info = new BedInfo(bed_details.getBedDetailsId(), bed_details.getByWardId(), bed_details.getBedName());

                        bed_info_list.add(this_bed_info);

                        Log.d(TAG, "Bed Details ID = " + this_bed_info.bed_details_id + " : ByWardID = " + this_bed_info.by_ward_id + " : bed_name = " + this_bed_info.bed_name);
                    }

                    patient_gateway_interface.handleReceivedBedList(bed_info_list);
                }
                else
                {
                    handleFailedResponse("getBedDetailsListFromServer", response);
                }
            }

            @Override
            public void onFailure(Call<List<GetBedDetailsResponse>> call, Throwable t)
            {
                // Cant queue the command up. Does NOT mean a failure from the Server
                handleFailureToQueueUpRequest("getBedDetailsListFromServer", http_operation_type, t);
            }
        });
    }


    public void getGatewayConfigFromServer()
    {
        final HttpOperationType http_operation_type = HttpOperationType.GET_GATEWAY_CONFIG;

        // The database on the Lifeguard Server contains all config options - needs to be limited to only Gateway ones
        GetGatewayConfigRequest request = new GetGatewayConfigRequest(3);

        lifeguard_interface.getGatewayConfig(request).enqueue(new Callback<List<GetGatewayConfigResponse>>()
        {
            @Override
            public void onResponse(Call<List<GetGatewayConfigResponse>> call, Response<List<GetGatewayConfigResponse>> response)
            {
                patient_gateway_interface.handleServerResponseComplete(response.isSuccessful(), http_operation_type, ActiveOrOldSession.INVALID);

                if(response.isSuccessful() && response.body() != null)
                {
                    ArrayList<ConfigOption> config_options_list = new ArrayList<>();

                    for (GetGatewayConfigResponse config_item : response.body())
                    {
                        config_options_list.add(new ConfigOption(config_item.getOptionName(), config_item.getOptionValue()));
                    }

                    patient_gateway_interface.handleReceivedGatewayConfig(config_options_list);
                }
                else
                {
                    handleFailedResponse("getGatewayConfigFromServer", response);
                }
            }

            @Override
            public void onFailure(Call<List<GetGatewayConfigResponse>> call, Throwable t)
            {
                // Cant queue the command up. Does NOT mean a failure from the Server
                handleFailureToQueueUpRequest("getGatewayConfigFromServer", http_operation_type, t);
            }
        });
    }


    public void getServerConfigurableTextFromServer()
    {
        GetServerConfigurableTextRequest request = new GetServerConfigurableTextRequest();

        getServerConfigurableTextFromServerViaHttp(request);
    }

    public void getWebPageDescriptorsFromServer()
    {
        getWebPagesFromServerViaHttp();
    }


/*
    private void getServerConfigurableTextFromServerViaHttp(ServerConfigurableTextStringTypes string_type)
    {
        GetServerConfigurableTextRequest request = new GetServerConfigurableTextRequest(string_type.ordinal());

        getServerConfigurableTextFromServerViaHttp(request);
    }
*/

    private void getServerConfigurableTextFromServerViaHttp(GetServerConfigurableTextRequest request)
    {
        final HttpOperationType http_operation_type = HttpOperationType.GET_SERVER_CONFIGURABLE_TEXT;

        lifeguard_interface.getServerConfigurableText(request).enqueue(new Callback<List<GetServerConfigurableTextResponse>>()
        {
            @Override
            public void onResponse(Call<List<GetServerConfigurableTextResponse>> call, Response<List<GetServerConfigurableTextResponse>> response)
            {
                patient_gateway_interface.handleServerResponseComplete(response.isSuccessful(), http_operation_type, ActiveOrOldSession.INVALID);

                if(response.isSuccessful() && response.body() != null)
                {
                    ArrayList<ServerConfigurableText> string_list = new ArrayList<>();

                    for (GetServerConfigurableTextResponse string_info : response.body())
                    {
                        string_list.add(new ServerConfigurableText(string_info.getServersId(), string_info.getStringType(), string_info.getStringText()));
                    }

                    patient_gateway_interface.handleReceivedServerConfigurableText(string_list);
                }
                else
                {
                    handleFailedResponse("getServerConfigurableTextFromServerViaHttp", response);
                }
            }

            @Override
            public void onFailure(Call<List<GetServerConfigurableTextResponse>> call, Throwable t)
            {
                // Cant queue the command up. Does NOT mean a failure from the Server
                handleFailureToQueueUpRequest("getServerConfigurableTextFromServerViaHttp", http_operation_type, t);
            }
        });
    }


    private void getWebPagesFromServerViaHttp()
    {
        final HttpOperationType http_operation_type = HttpOperationType.GET_VIEWABLE_WEBPAGES;

        lifeguard_interface.getWebPages().enqueue(new Callback<List<GetWebPagesResponse>>()
        {
            @Override
            public void onResponse(Call<List<GetWebPagesResponse>> call, Response<List<GetWebPagesResponse>> response)
            {
                patient_gateway_interface.handleServerResponseComplete(response.isSuccessful(), http_operation_type, ActiveOrOldSession.INVALID);

                if(response.isSuccessful() && response.body() != null)
                {
                    ArrayList<WebPageDescriptor> list = new ArrayList<>();

                    for (GetWebPagesResponse item : response.body())
                    {
                        list.add(new WebPageDescriptor(item.getDescription(), item.getUrl()));
                    }

                    patient_gateway_interface.handleReceivedWebPages(list);
                }
                else
                {
                    handleFailedResponse("getWebPagesFromServerViaHttp", response);
                }
            }

            @Override
            public void onFailure(Call<List<GetWebPagesResponse>> call, Throwable t)
            {
                // Cant queue the command up. Does NOT mean a failure from the Server
                handleFailureToQueueUpRequest("getWebPagesFromServerViaHttp", http_operation_type, t);
            }
        });
    }

    public void sendGetLatestDeviceFirmwareVersionsRequest()
    {
        final HttpOperationType http_operation_type = HttpOperationType.GET_DEVICE_FIRMWARE_VERSION_LIST;

        GetDeviceFirmwareListRequest request = new GetDeviceFirmwareListRequest();

        lifeguard_interface.getDeviceFirmwareList(request).enqueue(new Callback<List<GetDeviceFirmwareListResponse>>()
        {
            @Override
            public void onResponse(Call<List<GetDeviceFirmwareListResponse>> call, Response<List<GetDeviceFirmwareListResponse>> response)
            {
                if(response.isSuccessful() && response.body() != null)
                {
                    ArrayList<DeviceFirmwareDetails> device_firmware_details_list = new ArrayList<>();

                    for (GetDeviceFirmwareListResponse device_firmware_details_response : response.body())
                    {
                        DeviceFirmwareDetails device_firmware_details = new DeviceFirmwareDetails();

                        device_firmware_details.servers_row_id = device_firmware_details_response.getDeviceFirmwareId();
                        device_firmware_details.device_type_as_int = device_firmware_details_response.getDeviceType();
                        device_firmware_details.firmware_version = device_firmware_details_response.getFirmwareVersion();
                        device_firmware_details.filename = device_firmware_details_response.getFirmwareFile();

                        device_firmware_details_list.add(device_firmware_details);
                    }

                    patient_gateway_interface.handleReceivedDeviceFirmwareVersionList(device_firmware_details_list);
                }
                else
                {
                    handleFailedResponse("sendGetLatestDeviceFirmwareVersionsRequest", response);
                }

                patient_gateway_interface.handleServerResponseComplete(response.isSuccessful(), http_operation_type, ActiveOrOldSession.INVALID);
            }

            @Override
            public void onFailure(Call<List<GetDeviceFirmwareListResponse>> call, Throwable t)
            {
                // Cant queue the command up. Does NOT mean a failure from the Server
                handleFailureToQueueUpRequest("sendGetLatestDeviceFirmwareVersionsRequest", http_operation_type, t);
            }
        });
    }


    public void sendGetFirmwareBinaryRequest(int servers_id)
    {
        final HttpOperationType http_operation_type = HttpOperationType.DOWNLOAD_DEVICE_FIRMWARE_BY_ID;

        GetDeviceFirmwareByBedIdRequest request = new GetDeviceFirmwareByBedIdRequest(servers_id);

        lifeguard_interface.getDeviceFirmwareById(request).enqueue(new Callback<GetDeviceFirmwareByIdResponse>()
        {
            @Override
            public void onResponse(Call<GetDeviceFirmwareByIdResponse> call, Response<GetDeviceFirmwareByIdResponse> response)
            {
                patient_gateway_interface.handleServerResponseComplete(response.isSuccessful(), http_operation_type, ActiveOrOldSession.INVALID);

                if(response.isSuccessful() && response.body() != null)
                {
                    GetDeviceFirmwareByIdResponse firmware_response = response.body();

                    DeviceType device_type = DeviceType.values()[firmware_response.getDeviceType()];

                    int firmware_version = firmware_response.getFirmwareVersion();
                    String firmware_image_base64 = firmware_response.getFirmwareImage();

                    if (firmware_image_base64 != null)
                    {
                        byte[] firmware_image = Base64.decode(firmware_image_base64, Base64.DEFAULT);
                        int servers_id = firmware_response.getDeviceFirmwareId();

                        String file_name = firmware_response.getFirmwareFileName();

                        patient_gateway_interface.handleReceivedFirmwareImage(servers_id, device_type, firmware_version, firmware_image, file_name);
                    }
                    else
                    {
                        Log.e(TAG, "firmware_image_base64 is NULL !!!!!!!!!!!!");
                    }
                }
                else
                {
                    handleFailedResponse("sendGetFirmwareBinaryRequest", response);
                }
            }

            @Override
            public void onFailure(Call<GetDeviceFirmwareByIdResponse> call, Throwable t)
            {
                // Cant queue the command up. Does NOT mean a failure from the Server
                handleFailureToQueueUpRequest("sendGetFirmwareBinaryRequest", http_operation_type, t);
            }
        });
    }


    public void uploadLogFile(final File file)
    {
        // Create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("LogFile", file.getName(), requestFile);

        // Add another part within the multipart request
        RequestBody file_name = RequestBody.create(file.getName(), MultipartBody.FORM);
        RequestBody ward_name = RequestBody.create(patient_gateway_interface.getWardName(), MultipartBody.FORM);
        RequestBody bed_name = RequestBody.create(patient_gateway_interface.getBedName(), MultipartBody.FORM);

        // Finally, execute the request
        Call<ResponseBody> call = lifeguard_interface.upload(file_name, ward_name, bed_name, body);
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Log.v("Upload", "success");

                try
                {
                    if (response.body() != null)
                    {
                        if (response.body().string().equals("OK"))
                        {
                            Log.d(TAG, "Deleting the file with filename " + file.getName());
                            if (file.delete())
                            {
                                Log.d(TAG, "Deleted");
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "uploadLogFile : Problem with response_body : Exception = " + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Log.e("uploadLogFile error:", t.getMessage());
            }
        });
    }


    public void getMQTTCertificateFromServer()
    {
        final HttpOperationType http_operation_type = HttpOperationType.GET_MQTT_CERTIFICATE;
        Log.d(TAG, "getMQTTCertificateFromServer queueing query");

        lifeguard_interface.getMQTTCertificateFromServer().enqueue(new Callback<GetMQTTCertificateResponse>()
        {
            @Override
            public void onResponse(Call<GetMQTTCertificateResponse> call, Response<GetMQTTCertificateResponse> response)
            {
                // Handle the response to update installation mode progress etc
                patient_gateway_interface.handleServerResponseComplete(response.isSuccessful(), http_operation_type, ActiveOrOldSession.INVALID);

                if(response.isSuccessful() && response.body() != null)
                {
                    GetMQTTCertificateResponse certificate_response = response.body();

                    Log.d(TAG, "getMQTTCertificateFromServer got certificate");

                    // Write the certificate to the file system or database
                    patient_gateway_interface.handleMqttCertificate(certificate_response);
                }
                else
                {
                    handleFailedResponse("getMQTTCertificateFromServer", response);
                }
            }

            @Override
            public void onFailure(Call<GetMQTTCertificateResponse> call, Throwable t)
            {
                // Cant queue the command up. Does NOT mean a failure from the Server
                handleFailureToQueueUpRequest("getMQTTCertificateFromServer", http_operation_type, t);
            }
        });
    }
}