package com.isansys.patientgateway;

import android.content.Intent;

import com.isansys.common.DeviceSession;
import com.isansys.common.FirmwareImage;
import com.isansys.common.PatientInfo;
import com.isansys.common.ThresholdSet;
import com.isansys.common.ThresholdSetAgeBlockDetail;
import com.isansys.common.VideoCallContact;
import com.isansys.common.VideoCallDetails;
import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.BluetoothStatus;
import com.isansys.common.enums.Commands;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.HttpOperationType;
import com.isansys.common.enums.PatientDetailsLookupStatus;
import com.isansys.common.enums.PatientPositionOrientation;
import com.isansys.common.enums.QueryType;
import com.isansys.common.enums.RealTimeServer;
import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.gsm.GsmEventManager;
import com.isansys.patientgateway.remotelogging.RemoteLogging;
import com.isansys.patientgateway.serverlink.ServerSyncStatus;
import com.isansys.patientgateway.tabletBattery.TabletBatteryInterpreter;
import com.isansys.patientgateway.wifi.WifiEventManager;

import java.util.ArrayList;

public class SystemCommands
{
    private final RemoteLogging Log;
    private final ContextInterface gateway_context_interface;
    
    public final String INTENT__COMMANDS_TO_USER_INTERFACE = "com.isansys.patientgateway.commands_to_user_interface";

    private final Settings settings;
    private final String TAG;


    public SystemCommands(ContextInterface context_interface, RemoteLogging logger, Settings passed_settings)
    {
        gateway_context_interface = context_interface;
        settings = passed_settings;

        Log = logger;

        TAG = gateway_context_interface.getAppContext().getClass().getSimpleName();
    }

    private void sendUserInterfaceCommandNoPayload(Commands command)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", command.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    public void reportGatewaySessionNumbers(int android_database_patient_session_id, ArrayList<DeviceSession> device_sessions)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_GATEWAY_SESSION_NUMBERS.ordinal());
        outgoing_intent.putExtra("patient_session_number", android_database_patient_session_id);
        outgoing_intent.putParcelableArrayListExtra("device_session_list", device_sessions);
     
        Log.e(TAG, "reportGatewaySessionNumbers : android_database_patient_session_id = " + android_database_patient_session_id);
        
        for(DeviceSession device_session : device_sessions)
        {
        	Log.e(TAG, "reportGatewaySessionNumbers : " + device_session.sensor_type + " : " + device_session.device_type + " : device_session_id = " + device_session.local_device_session_id);
        }

        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportPatientThresholdSet(PatientInfo patientInfo)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_PATIENT_THRESHOLD_SET.ordinal());

        ThresholdSet thresholdSet = patientInfo.getThresholdSet();
        if (thresholdSet != null)
        {
            outgoing_intent.putExtra("servers_threshold_set_id", patientInfo.getThresholdSet().servers_database_row_id);
        }
        else
        {
            outgoing_intent.putExtra("servers_threshold_set_id", -1);
        }

        ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = patientInfo.getThresholdSetAgeBlockDetails();
        if (thresholdSetAgeBlockDetail != null)
        {
            outgoing_intent.putExtra("servers_threshold_set_age_block_details_id", patientInfo.getThresholdSetAgeBlockDetails().servers_database_row_id);
        }
        else
        {
            outgoing_intent.putExtra("servers_threshold_set_age_block_details_id", -1);
        }

        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportHospitalPatientID(String hospital_patient_id)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_HOSPITAL_PATIENT_ID.ordinal());
        outgoing_intent.putExtra("hospital_patient_id", hospital_patient_id);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportGatewaysAssignedBedDetails(String gateways_assigned_bed_id, String gateways_assigned_ward_name, String gateways_assigned_bed_name)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_GATEWAYS_ASSIGNED_BED_DETAILS.ordinal());
        outgoing_intent.putExtra("gateways_assigned_bed_id", gateways_assigned_bed_id);
        outgoing_intent.putExtra("gateways_assigned_ward_name", gateways_assigned_ward_name);
        outgoing_intent.putExtra("gateways_assigned_bed_name", gateways_assigned_bed_name);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportServerAddress(String server_address)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_SERVER_ADDRESS.ordinal());
        outgoing_intent.putExtra("server_address", server_address);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportRealTimeServerPort(String realtime_server_port)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_REALTIME_SERVER_PORT.ordinal());
        outgoing_intent.putExtra("realtime_server_port", realtime_server_port);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportServerPort(String server_port)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_SERVER_PORT.ordinal());
        outgoing_intent.putExtra("server_port", server_port);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportHttpsEnabledStatus(boolean https_enabled)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_HTTPS_ENABLE_STATUS.ordinal());
        outgoing_intent.putExtra("https_enabled", https_enabled);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }
    

    public void reportWebServiceAuthenticationEnabledStatus(boolean enabled)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_WEBSERVICE_AUTHENTICATION_ENABLE_STATUS.ordinal());
        outgoing_intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    public void reportWebServiceEncryptionEnabledStatus(boolean enabled)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_WEBSERVICE_ENCRYPTION_ENABLE_STATUS.ordinal());
        outgoing_intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportServerPingResult(boolean result, boolean authentication_ok)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_SERVER_PING_RESULT.ordinal());
        outgoing_intent.putExtra("server_ping_result", result);
        outgoing_intent.putExtra("authentication_ok", authentication_ok);

        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportServerResponse(boolean result, HttpOperationType http_operation_type, ActiveOrOldSession active_or_old_session, ServerSyncStatus server_sync_status)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_SERVER_RESPONSE.ordinal());

        outgoing_intent.putExtra("server_webservice_result", result);
        outgoing_intent.putExtra("http_operation_type_as_int", http_operation_type.ordinal());
        outgoing_intent.putExtra("active_or_old_session_as_int", active_or_old_session.ordinal());

        outgoing_intent = addSessionInfoAndMeasurementsToIntent(server_sync_status, outgoing_intent);

        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    private Intent addSessionInfoAndMeasurementsToIntent(ServerSyncStatus server_sync_status, Intent outgoing_intent)
    {
        // Add Session Info
        outgoing_intent.putExtra("patient_details_rows_pending", server_sync_status.patient_details);

        outgoing_intent.putExtra("device_info_rows_pending", server_sync_status.device_info);

        outgoing_intent.putExtra("start_patient_session_rows_pending", server_sync_status.start_patient_session);
        outgoing_intent.putExtra("start_device_session_rows_pending", server_sync_status.start_device_session);

        outgoing_intent.putExtra("end_device_session_rows_pending", server_sync_status.end_device_session);
        outgoing_intent.putExtra("end_patient_session_rows_pending", server_sync_status.end_patient_session);        

        outgoing_intent.putExtra("patient_session_fully_synced_rows_pending", server_sync_status.patient_session_fully_synced);

        outgoing_intent.putExtra("active_session_connection_event_rows_pending", server_sync_status.active_session_connection_event_rows_pending);
        outgoing_intent.putExtra("old_session_connection_event_rows_pending", server_sync_status.old_session_connection_event_rows_pending);

        outgoing_intent.putExtra("active_session_lifetouch_heart_rate_rows_pending", server_sync_status.active_session_lifetouch_heart_rate_rows_pending);
        outgoing_intent.putExtra("old_session_lifetouch_heart_rate_rows_pending", server_sync_status.old_session_lifetouch_heart_rate_rows_pending);

        outgoing_intent.putExtra("active_session_lifetouch_heart_beat_rows_pending", server_sync_status.active_session_lifetouch_heart_beat_rows_pending);
        outgoing_intent.putExtra("old_session_lifetouch_heart_beat_rows_pending", server_sync_status.old_session_lifetouch_heart_beat_rows_pending);

        outgoing_intent.putExtra("active_session_lifetouch_respiration_rate_rows_pending", server_sync_status.active_session_lifetouch_respiration_rate_rows_pending);
        outgoing_intent.putExtra("old_session_lifetouch_respiration_rate_rows_pending", server_sync_status.old_session_lifetouch_respiration_rate_rows_pending);

        outgoing_intent.putExtra("active_session_lifetouch_setup_mode_rows_pending", server_sync_status.active_session_lifetouch_setup_mode_rows_pending);
        outgoing_intent.putExtra("old_session_lifetouch_setup_mode_rows_pending", server_sync_status.old_session_lifetouch_setup_mode_rows_pending);

        outgoing_intent.putExtra("active_session_lifetouch_raw_accelerometer_mode_rows_pending", server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending);
        outgoing_intent.putExtra("old_session_lifetouch_raw_accelerometer_mode_rows_pending", server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending);

        outgoing_intent.putExtra("active_session_lifetouch_battery_measurement_rows_pending", server_sync_status.active_session_lifetouch_battery_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_lifetouch_battery_measurement_rows_pending", server_sync_status.old_session_lifetouch_battery_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_lifetouch_patient_orientation_rows_pending", server_sync_status.active_session_lifetouch_patient_orientation_rows_pending);
        outgoing_intent.putExtra("old_session_lifetouch_patient_orientation_rows_pending", server_sync_status.old_session_lifetouch_patient_orientation_rows_pending);

        outgoing_intent.putExtra("active_session_lifetemp_temperature_measurement_rows_pending", server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_lifetemp_temperature_measurement_rows_pending", server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_lifetemp_battery_measurement_rows_pending", server_sync_status.active_session_lifetemp_battery_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_lifetemp_battery_measurement_rows_pending", server_sync_status.old_session_lifetemp_battery_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_pulse_ox_spo2_measurement_rows_pending", server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_pulse_ox_spo2_measurement_rows_pending", server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_pulse_ox_intermediate_measurement_rows_pending", server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_pulse_ox_intermediate_measurement_rows_pending", server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_pulse_ox_setup_mode_rows_pending", server_sync_status.active_session_pulse_ox_setup_mode_rows_pending);
        outgoing_intent.putExtra("old_session_pulse_ox_setup_mode_rows_pending", server_sync_status.old_session_pulse_ox_setup_mode_rows_pending);

        outgoing_intent.putExtra("active_session_pulse_ox_battery_measurement_rows_pending", server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_pulse_ox_battery_measurement_rows_pending", server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_blood_pressure_measurement_rows_pending", server_sync_status.active_session_blood_pressure_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_blood_pressure_measurement_rows_pending", server_sync_status.old_session_blood_pressure_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_blood_pressure_battery_measurement_rows_pending", server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_blood_pressure_battery_measurement_rows_pending", server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_weight_scale_measurement_rows_pending", server_sync_status.active_session_weight_scale_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_weight_scale_measurement_rows_pending", server_sync_status.old_session_weight_scale_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_weight_scale_battery_measurement_rows_pending", server_sync_status.active_session_weight_scale_battery_measurement_rows_pending);
        outgoing_intent.putExtra("old_session_weight_scale_battery_measurement_rows_pending", server_sync_status.old_session_weight_scale_battery_measurement_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_heart_rate_rows_pending", server_sync_status.active_session_manually_entered_heart_rate_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_heart_rate_rows_pending", server_sync_status.old_session_manually_entered_heart_rate_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_respiration_rate_rows_pending", server_sync_status.active_session_manually_entered_respiration_rate_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_respiration_rate_rows_pending", server_sync_status.old_session_manually_entered_respiration_rate_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_temperature_rows_pending", server_sync_status.active_session_manually_entered_temperature_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_temperature_rows_pending", server_sync_status.old_session_manually_entered_temperature_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_spo2_rows_pending", server_sync_status.active_session_manually_entered_spo2_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_spo2_rows_pending", server_sync_status.old_session_manually_entered_spo2_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_blood_pressure_rows_pending", server_sync_status.active_session_manually_entered_blood_pressure_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_blood_pressure_rows_pending", server_sync_status.old_session_manually_entered_blood_pressure_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_weight_rows_pending", server_sync_status.active_session_manually_entered_weight_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_weight_rows_pending", server_sync_status.old_session_manually_entered_weight_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_consciousness_level_rows_pending", server_sync_status.active_session_manually_entered_consciousness_level_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_consciousness_level_rows_pending", server_sync_status.old_session_manually_entered_consciousness_level_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_supplemental_oxygen_level_rows_pending", server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_supplemental_oxygen_level_rows_pending", server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_annotation_rows_pending", server_sync_status.active_session_manually_entered_annotation_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_annotation_rows_pending", server_sync_status.old_session_manually_entered_annotation_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_capillary_refill_time_rows_pending", server_sync_status.active_session_manually_entered_capillary_refill_time_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_capillary_refill_time_rows_pending", server_sync_status.old_session_manually_entered_capillary_refill_time_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_respiration_distress_rows_pending", server_sync_status.active_session_manually_entered_respiration_distress_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_respiration_distress_rows_pending", server_sync_status.old_session_manually_entered_respiration_distress_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_family_or_nurse_concern_rows_pending", server_sync_status.active_session_manually_entered_family_or_nurse_concern_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_family_or_nurse_concern_rows_pending", server_sync_status.old_session_manually_entered_family_or_nurse_concern_rows_pending);

        outgoing_intent.putExtra("active_session_manually_entered_urine_output_rows_pending", server_sync_status.active_session_manually_entered_urine_output_rows_pending);
        outgoing_intent.putExtra("old_session_manually_entered_urine_output_rows_pending", server_sync_status.old_session_manually_entered_urine_output_rows_pending);

        outgoing_intent.putExtra("active_session_early_warning_score_rows_pending", server_sync_status.active_session_early_warning_score_rows_pending);
        outgoing_intent.putExtra("old_session_early_warning_score_rows_pending", server_sync_status.old_session_early_warning_score_rows_pending);

        outgoing_intent.putExtra("active_session_setup_mode_log_rows_pending", server_sync_status.active_session_setup_mode_log_rows_pending);
        outgoing_intent.putExtra("old_session_setup_mode_log_rows_pending", server_sync_status.old_session_setup_mode_log_rows_pending);

        outgoing_intent.putExtra("auditable_events_rows_pending", server_sync_status.auditable_events_rows_pending);


        return outgoing_intent;
    }


    public void reportDatabaseStatus(ServerSyncStatus server_sync_status)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DATABASE_STATUS.ordinal());

        outgoing_intent = addSessionInfoAndMeasurementsToIntent(server_sync_status, outgoing_intent);

        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendNewSetupModeSampleToUserInterface(DeviceType device_type, MeasurementSetupModeDataPoint datapoint)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_SEND_NEW_SETUP_MODE_DATA_TO_UI.ordinal());
        outgoing_intent.putExtra("device_type", device_type.ordinal());
        outgoing_intent.putExtra("setup_mode_sample", datapoint.sample);
        outgoing_intent.putExtra("timestamp_in_ms", datapoint.timestamp_in_ms);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendNewLifetouchRawAccelerometerModeData(short[] x_axis_raw_accelerometer_mode_data, short[] y_axis_raw_accelerometer_mode_data, short[] z_axis_raw_accelerometer_mode_data)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_SEND_NEW_RAW_ACCELEROMETER_MODE_DATA_TO_UI.ordinal());
        outgoing_intent.putExtra("x_axis_raw_accelerometer_mode_data", x_axis_raw_accelerometer_mode_data);
        outgoing_intent.putExtra("y_axis_raw_accelerometer_mode_data", y_axis_raw_accelerometer_mode_data);
        outgoing_intent.putExtra("z_axis_raw_accelerometer_mode_data", z_axis_raw_accelerometer_mode_data);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportServerSyncEnableStatus()
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_SERVER_SYNC_ENABLE_STATUS.ordinal());
        outgoing_intent.putExtra("server_sync_enabled", settings.getServerSyncEnabledStatus());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportDummyDataModeDeviceLeadsOffEnableStatus(SensorType sensor_type, boolean status)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DUMMY_DATA_MODE_DEVICE_LEADS_OFF_ENABLE_STATUS.ordinal());
        outgoing_intent.putExtra("sensor_type", sensor_type.ordinal());
        outgoing_intent.putExtra("enabled_status", status);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportRealTimeLinkEnableStatus()
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_REALTIME_LINK_ENABLE_STATUS.ordinal());
        outgoing_intent.putExtra("enabled", settings.getRealTimeLinkEnabledStatus());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportDeviceLeadOffDetectionStatus(SensorType sensor_type, boolean not_connected_to_patient)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_LEAD_OFF_DETECTION_STATUS.ordinal());
        outgoing_intent.putExtra("sensor_type", sensor_type.ordinal());
        outgoing_intent.putExtra("not_connected_to_patient", not_connected_to_patient);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportLifetouchNoBeatsDetectedTimerStatus(boolean no_beats_detected)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_LIFETOUCH_NO_BEATS_DETECTED_TIMER_STATUS.ordinal());
        outgoing_intent.putExtra("no_beats_detected", no_beats_detected);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportCheckDeviceDetailsResults(boolean result, String ward_name, String bed_name, DeviceType device_type, long human_readable_device_id, boolean server_device_inUse_status)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_CHECK_DEVICE_STATUS_RESULTS.ordinal());
        outgoing_intent.putExtra("result", result);
        outgoing_intent.putExtra("ward_name", ward_name);
        outgoing_intent.putExtra("bed_name", bed_name);
        outgoing_intent.putExtra("in_use", server_device_inUse_status);
        outgoing_intent.putExtra("device_type", device_type.ordinal());
        outgoing_intent.putExtra("human_readable_device_id", human_readable_device_id);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendCommandReportDeviceSetupModeStartedFromServer(DeviceType device_type)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_SETUP_MODE_STARTED_VIA_SERVER.ordinal());
        outgoing_intent.putExtra("device_type", device_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendCommandReportDeviceRawAccelerometerModeStartedFromServer(DeviceType device_type)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_RAW_ACCELEROMETER_MODE_STARTED_VIA_SERVER.ordinal());
        outgoing_intent.putExtra("device_type", device_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendCommandReportPatientNameFromServerLookupOfHospitalPatientId(String firstName, String lastName, String dob, String gender, boolean complete, PatientDetailsLookupStatus status)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_PATIENT_NAME_FROM_SERVER_LOOKUP_OF_HOSPITAL_PATIENT_ID.ordinal());
        outgoing_intent.putExtra("firstName", firstName);
        outgoing_intent.putExtra("lastName", lastName);
        outgoing_intent.putExtra("dob", dob);
        outgoing_intent.putExtra("gender", gender);
        outgoing_intent.putExtra("complete", complete);
        outgoing_intent.putExtra("status", status.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }
 

    public void sendCommandReportInvalidServerStatusCode(int server_status_code, String server_message)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_INVALID_SERVER_STATUS_CODE.ordinal());
        outgoing_intent.putExtra("server_status_code", server_status_code);
        outgoing_intent.putExtra("server_message", server_message);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendCommandReportDeviceSetupModeStoppedFromServer(DeviceType device_type)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_SETUP_MODE_STOPPED_VIA_SERVER.ordinal());
        outgoing_intent.putExtra("device_type", device_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendCommandReportDeviceRawAccelerometerModeStoppedFromServer(DeviceInfo device_info)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_RAW_ACCELEROMETER_MODE_STOPPED_VIA_SERVER.ordinal());
        outgoing_intent.putExtra("device_type", device_info.device_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendCommandReportDeviceOperatingMode(DeviceInfo device_info)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_OPERATING_MODE.ordinal());
        outgoing_intent.putExtra("device_type", device_info.device_type.ordinal());
        outgoing_intent.putExtra("operating_mode", device_info.operating_mode.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    
    public void sendCommandReportConnectedToServerStatus(boolean connected_to_server)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_CONNECTED_TO_SERVER_STATUS.ordinal());
        outgoing_intent.putExtra("connected_to_server", connected_to_server);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    
    public void updateUserInterfaceBluetoothStatus(DeviceInfo device_info, BluetoothStatus bluetooth_status)
    {
        if (!PatientGatewayService.disableCommentsForSpeed()) {
            Log.d(TAG, "updateUserInterfaceBluetoothStatus : " + device_info.device_type + " : " + bluetooth_status);
        }

        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_UPDATE_USER_INTERFACE_BLUETOOTH_STATUS.ordinal());
        outgoing_intent.putExtra("patient_gateway_status", bluetooth_status.ordinal());
        outgoing_intent.putExtra("device_type", device_info.device_type.ordinal());
        outgoing_intent.putExtra("sensor_type", device_info.sensor_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendCommandReportBluetoothScanProgress(DeviceInfo device_info)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_BLUETOOTH_SCAN_PROGRESS.ordinal());
        outgoing_intent.putExtra("bluetooth_scan_progress", device_info.bluetooth_connection_counter);
        outgoing_intent.putExtra("device_type", device_info.device_type.ordinal());
        outgoing_intent.putExtra("sensor_type", device_info.sensor_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendCommandEndOfDeviceConnection(boolean all_desired_devices_connected, boolean restored_session)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_END_OF_DEVICE_CONNECTION.ordinal());
        outgoing_intent.putExtra("all_desired_devices_connected", all_desired_devices_connected);
        outgoing_intent.putExtra("auto_press_start_monitoring_button", restored_session);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendPingCommandToUserInterface()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_CHECK_GATEWAY_UI_CONNECTION);
    }


    public void reportDeviceBatteryLevel(DeviceInfo device_info)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_BATTERY_LEVEL.ordinal());
        outgoing_intent.putExtra("device_type", device_info.device_type.ordinal());
        outgoing_intent.putExtra("battery_percentage", device_info.last_battery_reading_percentage);
        outgoing_intent.putExtra("battery_voltage_in_millivolts", device_info.last_battery_reading_in_millivolts);
        outgoing_intent.putExtra("time_measurement_received", device_info.last_battery_reading_received_timestamp);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportDeviceInfo(DeviceInfo device_info)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);

        outgoing_intent.putExtra("device_type", device_info.device_type.ordinal());

        Log.d(TAG, "reportDeviceInfo : " + device_info.device_type + " : " + device_info.getActualDeviceConnectionStatus());

        boolean valid_command = true;

        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            case SENSOR_TYPE__TEMPERATURE:
            {
                outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_INFO.ordinal());

                outgoing_intent.putExtra("lot_number", device_info.lot_number);
                outgoing_intent.putExtra("manufacture_date_in_millis", device_info.manufacture_date.getMillis());
                outgoing_intent.putExtra("expiration_date_in_millis", device_info.expiration_date.getMillis());
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_INFO.ordinal());

                outgoing_intent.putExtra("counter_leads_off_after_last_valid_data", device_info.counter_leads_off_after_last_valid_data);
                outgoing_intent.putExtra("timestamp_leads_off_disconnection",device_info.timestamp_leads_off_disconnection);
                outgoing_intent.putExtra("counter_total_leads_off", device_info.counter_total_leads_off);
            }
            break;
            
            case SENSOR_TYPE__BLOOD_PRESSURE:
            case SENSOR_TYPE__WEIGHT_SCALE:
            case SENSOR_TYPE__ALGORITHM:
            case SENSOR_TYPE__GATEWAY_INFO:
            {
                outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_INFO.ordinal());
            }
            break;

            case SENSOR_TYPE__MANUAL_VITAL:
            {
                outgoing_intent.putExtra("command", Commands.CMD_REPORT_MANUAL_VITAL_SIGN_DEVICE_INFO.ordinal());
            }
            break;

            default:
            {
                Log.e(TAG, "INVALID in reportDeviceInfo");
                valid_command = false;
            }
            break;
        }

        if (valid_command)
        {
            outgoing_intent.putExtra("human_readable_device_id", device_info.human_readable_device_id);
            outgoing_intent.putExtra("bluetooth_address", device_info.bluetooth_address);
            outgoing_intent.putExtra("device_name", device_info.device_name);
            outgoing_intent.putExtra("actual_device_connection_status", device_info.getActualDeviceConnectionStatus().ordinal());
            outgoing_intent.putExtra("show_on_ui", device_info.isDeviceTypePartOfPatientSession());
            outgoing_intent.putExtra("measurement_interval_in_seconds", device_info.measurement_interval_in_seconds);
            outgoing_intent.putExtra("setup_mode_time_in_seconds", settings.getSetupModeTimeInSeconds());
            outgoing_intent.putExtra("dummy_data_mode", device_info.dummy_data_mode);
            outgoing_intent.putExtra("firmware_version", device_info.getDeviceFirmwareVersion());
            outgoing_intent.putExtra("firmware_string", device_info.getDeviceFirmwareVersionString());
            outgoing_intent.putExtra("last_battery_reading_percentage", device_info.last_battery_reading_percentage);
            outgoing_intent.putExtra("last_battery_reading_in_millivolts", device_info.last_battery_reading_in_millivolts);
            outgoing_intent.putExtra("last_battery_reading_received_timestamp", device_info.last_battery_reading_received_timestamp);
            outgoing_intent.putExtra("android_database_device_session_id", device_info.getAndroidDeviceSessionId());
            outgoing_intent.putExtra("radio_type", device_info.getRadioType().ordinal());
            outgoing_intent.putExtra("sensor_type", device_info.sensor_type.ordinal());

            if (device_info.deviceSupportsSetupMode())
            {
                outgoing_intent.putExtra("max_setup_mode_sample_size", device_info.getMaxSetupModeSampleSize());

                outgoing_intent.putExtra("supports_setup_mode", true);
            }

            // Device specific attributes
            switch (device_info.device_type)
            {
                case DEVICE_TYPE__LIFETOUCH:
                case DEVICE_TYPE__LIFETOUCH_BLUE_V2:
                case DEVICE_TYPE__LIFETOUCH_THREE:
                case DEVICE_TYPE__LIFETEMP_V2:
                {
                    outgoing_intent.putExtra("supports_disconnecting_progress_bar", true);
                }
                break;

                case DEVICE_TYPE__FORA_IR20:
                {
                    outgoing_intent.putExtra("supports_battery_info", false);
                }

                default:
                    outgoing_intent.putExtra("supports_disconnecting_progress_bar", false);
            }

            gateway_context_interface.sendBroadcastIntent(outgoing_intent);
        }
    }
    
    
    public void reportBedList(ArrayList<BedInfo> bed_info_list)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_BED_LIST.ordinal());
        outgoing_intent.putParcelableArrayListExtra("bed_info_list", bed_info_list);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportWardList(ArrayList<WardInfo> ward_info_list)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_WARD_LIST.ordinal());
        outgoing_intent.putParcelableArrayListExtra("ward_info_list", ward_info_list);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    public void reportCachedDataUpdated(QueryType type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_CACHED_DATA_UPDATED.ordinal());
        intent.putExtra("query_type", type.ordinal());

        gateway_context_interface.sendBroadcastIntent(intent);
    }
    
    public void reportPatientNameLookupEnableStatus(boolean enabled)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_PATIENT_NAME_LOOKUP_ENABLE_STATUS.ordinal());
        outgoing_intent.putExtra("patient_id_check_enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }
    
    public void reportCsvOutputEnableStatus(boolean enabled)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_CSV_ENABLE_STATUS.ordinal());
        outgoing_intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }
   
    public void reportSpoofEarlyWarningScores(boolean spoof)
    {
    	Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_SPOOF_EARLY_WARNING_SCORES.ordinal());
        outgoing_intent.putExtra("spoof_early_warning_scores", spoof);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }
    
    public void reportDevicePeriodicSetupModeEnableStatus(boolean enabled)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_PERIODIC_SETUP_MODE_ENABLE_STATUS.ordinal());
        outgoing_intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportDisplayDevicePeriodicModeDataInUserInterface(DeviceType device_type, boolean enabled)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_DISPLAY_DEVICE_PERIODIC_MODE_DATA_IN_UI.ordinal());
        outgoing_intent.putExtra("device_type", device_type.ordinal());
        outgoing_intent.putExtra("in_periodic_setup_mode", enabled);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendBleDeviceChangeSessionDisconnected(DeviceType device_type)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_BLE_DEVICE_CHANGE_SESSION_DISCONNECTED.ordinal());
        outgoing_intent.putExtra("device_type", device_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    /**
     * Broadcast Report to UI with local clock offset drift value 
     * @param local_clock_offset_in_milliseconds Drift of local clock
     */
    public void reportNtpClockOffset(boolean success, double local_clock_offset_in_milliseconds, int attempt_number, int attempt_max)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_NTP_CLOCK_OFFSET_IN_MS.ordinal());
        outgoing_intent.putExtra("success", success);
        outgoing_intent.putExtra("local_clock_offset_in_milliseconds", local_clock_offset_in_milliseconds);
        outgoing_intent.putExtra("attempt_number", attempt_number);
        outgoing_intent.putExtra("attempt_max", attempt_max);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendUpdateProgressBarMeasurement_UI()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_UPDATE_UI_DEVICE_MEASUREMENT_PROGRESS_BAR);
    }


    public void reportDatabaseEmptied(boolean deleted_ews_thresholds)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DATABASE_EMPTIED.ordinal());
        outgoing_intent.putExtra("deleted_ews_thresholds", deleted_ews_thresholds);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    
    public void reportJsonArraySize(int json_array_size)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_JSON_ARRAY_SIZE.ordinal());
        intent.putExtra("json_array_size", json_array_size);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportHeartBeats(ArrayList<HeartBeatInfo> heart_beat_list)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_LIFETOUCH_BEATS_RECEIVED.ordinal());
        intent.putParcelableArrayListExtra("heart_beat_list", heart_beat_list);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportNumberOfDummyDataModeMeasurementsPerTick(int measurements_per_tick)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK.ordinal());
        intent.putExtra("measurements_per_tick", measurements_per_tick);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void tellUiThatGatewayHasBooted()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_TELL_UI_THAT_GATEWAY_HAS_BOOTED);
    }


    public void reportNumberOfLifetouchHeartBeatsPending(int number_of_heart_beats_pending)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_NUMBER_OF_LIFETOUCH_HEART_BEATS_PENDING.ordinal());
        intent.putExtra("number_of_heart_beats_pending", number_of_heart_beats_pending);
        gateway_context_interface.sendBroadcastIntent(intent);
    }

    
    public void reportNewVitalsData(VitalSignType vital_sign_type, MeasurementVitalSign data_point)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_NEW_VITALS_DATA.ordinal());
        intent.putExtra("data_type", vital_sign_type.ordinal());
        intent.putExtra("data_point", data_point);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportSetupModeTimeInSeconds(int setup_mode_time_in_seconds)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_SETUP_MODE_TIME_IN_SECONDS.ordinal());
        outgoing_intent.putExtra("setup_mode_time_in_seconds", setup_mode_time_in_seconds);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportDevicePeriodicModePeriodTimeInSeconds(int time_in_seconds)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS.ordinal());
        outgoing_intent.putExtra("time_in_seconds", time_in_seconds);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportDevicePeriodicModeActiveTimeInSeconds(int time_in_seconds)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS.ordinal());
        outgoing_intent.putExtra("time_in_seconds", time_in_seconds);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(int number)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID.ordinal());
        outgoing_intent.putExtra("number", number);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportDisplayTimeoutInSeconds(int number)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DISPLAY_TIMEOUT_IN_SECONDS.ordinal());
        outgoing_intent.putExtra("number", number);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportDisplayTimeoutAppliesToPatientVitals(boolean applies)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY.ordinal());
        outgoing_intent.putExtra("display_timeout_applies_to_patient_vitals", applies);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(int percentage)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID.ordinal());
        outgoing_intent.putExtra("percentage", percentage);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportPatientStartSessionTime(long patientStartSession)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_PATIENT_START_SESSION_TIME.ordinal());
        intent.putExtra("patient_start_session_time", patientStartSession);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportPatientOrientation(PatientPositionOrientation patientOrientation, long timeStamp)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_PATIENT_ORIENTATION.ordinal());
        intent.putExtra("patient_orientation", patientOrientation.ordinal());
        intent.putExtra("timestamp", timeStamp);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportBluetoothDeviceNotConnected(DeviceInfo device_info)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_ALL_BLUETOOTH_DEVICES_NOT_CONNECTED.ordinal());
        intent.putExtra("device_type", device_info.device_type.ordinal());
        intent.putExtra("sensor_type", device_info.sensor_type.ordinal());
        intent.putExtra("human_readable_device_id",device_info.human_readable_device_id);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportWifiStatusToUserInterface(WifiEventManager.WifiStatus mWifi_status, PatientGatewayService.ActiveNetworkTypes active_network_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_WIFI_STATUS.ordinal());
        intent.putExtra("wifi_enabled", mWifi_status.hardware_enabled);
        intent.putExtra("connected_to_ssid", mWifi_status.connected_to_ssid);
        intent.putExtra("ssid", mWifi_status.ssid);
        intent.putExtra("ip_address_string", mWifi_status.ip_address_string);
        intent.putExtra("wifi_level", mWifi_status.wifi_level);
        intent.putExtra("wifi_Status", mWifi_status.wifi_Status);
        intent.putExtra("wifi_BSSID", mWifi_status.wifi_BSSID);
        intent.putExtra("wifi_error_status", mWifi_status.wifi_error_status.ordinal());
        intent.putExtra("active_network_type", active_network_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportGsmStatusToUserInterface(GsmEventManager.GsmStatus gsm_status, PatientGatewayService.ActiveNetworkTypes active_network_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_GSM_STATUS.ordinal());
        intent.putExtra("data_activity_direction", gsm_status.data_activity_direction);
        intent.putExtra("data_connection_state", gsm_status.data_connection_state);
        intent.putExtra("network_type", gsm_status.network_type);
        intent.putExtra("signal_level", gsm_status.signal_level);
        intent.putExtra("active_network_type", active_network_type.ordinal());
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportRunDevicesInTestMode(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_RUN_DEVICES_IN_TEST_MODE.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportManualVitalSignsEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_MANUAL_VITAL_SIGNS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportLongTermMeasurementTimeoutInMinutes(SensorType sensorType, int timeout)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES.ordinal());
        intent.putExtra("sensor_type", sensorType.ordinal());
        intent.putExtra("timeout", timeout);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportGatewayPluggedInStatusTimerSwitchSafe(boolean charger_unplugged)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_GATEWAY_PLUGGED_IN_STATUS_TIMER_SWITCH_SAFE.ordinal());
        intent.putExtra("charger_unplugged", charger_unplugged);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportUnpluggedOverlayEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_UNPLUGGED_OVERLAY_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }

    public void reportLT3KHzSetupModeEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_LT3_KHZ_SETUP_MODE_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }

    public void reportAutoEnableEwsEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_AUTO_ENABLE_EWS__ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportNightModeEnabledFromServer(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_FROM_SERVER__ENABLE_UI_NIGHT_MODE.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportManufacturingModeEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_MANUFACTURING_MODE_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportSimpleHeartRateEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_SIMPLE_HEART_RATE_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportUsbAccessoryConnectedStatus(boolean connected)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_USB_ACCESSORY_CONNECTION_STATUS.ordinal());
        intent.putExtra("connected", connected);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportGsmModeOnlyEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_GSM_MODE_ONLY_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportSweetBlueDiagnostics(int number_of_times_postCompleteBleReset_called, int number_of_times_postBleResetWithoutRemovingDevices_called)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_SWEETBLUE_DIAGNOSTICS.ordinal());
        intent.putExtra("number_of_times_postCompleteBleReset_called", number_of_times_postCompleteBleReset_called);
        intent.putExtra("number_of_times_postBleResetWithoutRemovingDevices_called", number_of_times_postBleResetWithoutRemovingDevices_called);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportUseBackCameraEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_USE_BACK_CAMERA_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportPatientOrientationEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_PATIENT_ORIENTATION_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportShowNumbersOfBatteryIndicatorEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_SHOW_NUMBERS_ON_BATTERY_INDICATOR_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportShowMacAddressEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_SHOW_MAC_ADDRESS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportWifiLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_WIFI_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportGsmLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_GSM_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportDatabaseLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_DATABASE_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportServerLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_SERVER_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportBatteryLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_BATTERY_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportDfuBootloaderEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_DFU_BOOTLOADER_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportSpO2SpotMeasurementsEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportPredefinedAnnotationEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_PREDEFINED_ANNOTATION_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportDisplayTemperatureInFahrenheitEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportDisplayWeightInLbsEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportUsaModeEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_USA_MODE_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportShowLifetouchActivityLevelEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_SHOW_LIFETOUCH_ACTIVITY_LEVEL_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportLifetempMeasurementInterval(int measurement_interval_in_seconds)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS.ordinal());
        intent.putExtra("measurement_interval_in_seconds", measurement_interval_in_seconds);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportDeveloperPopupEnabled(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_DEVELOPER_POPUP_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportPatientOrientationMeasurementInterval(int measurement_interval_in_seconds)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS.ordinal());
        intent.putExtra("measurement_interval_in_seconds", measurement_interval_in_seconds);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportShowIpAddressOnWifiPopupEnabled(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_SHOW_IP_ADDRESS_ON_WIFI_POPUP_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportGatewayChargeStatus(TabletBatteryInterpreter.GatewayBatteryInfo gateway_battery_info)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_GATEWAY_CHARGE_STATUS.ordinal());
        intent.putExtra("android_percentage", gateway_battery_info.android_percentage);
        intent.putExtra("charging", gateway_battery_info.charging);
        intent.putExtra("current_avg", gateway_battery_info.current_avg);
        intent.putExtra("voltage", gateway_battery_info.voltage);
        intent.putExtra("temperature", gateway_battery_info.temperature);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportNumberOfLifetempMeasurementsPending(int number_of_measurements_pending)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_NUMBER_OF_LIFETEMP_MEASUREMENTS_PENDING.ordinal());
        intent.putExtra("number_of_measurements_pending", number_of_measurements_pending);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportAutoResumeStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_AUTO_RESUME_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportDfuProgress(DeviceInfo device_info, int progress)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_DFU_PROGRESS.ordinal());
        intent.putExtra("device_type", device_info.device_type.ordinal());
        intent.putExtra("sensor_type", device_info.sensor_type.ordinal());
        intent.putExtra("progress", progress);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportInstallationComplete(boolean complete)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_INSTALLATION_COMPLETE.ordinal());
        intent.putExtra("complete", complete);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportCheckForLatestFirmwareComplete(boolean success, ArrayList<FirmwareImage> firmware_image_list)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_CHECK_FOR_LATEST_FIRMWARE_IMAGES_FROM_SERVER_COMPLETE.ordinal());
        intent.putExtra("success", success);
        intent.putExtra("firmware_image_list", firmware_image_list);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void sendCommandReportDeviceSetupModeLog(DeviceInfo device_info)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_DEVICE_SETUP_MODE_HISTORY.ordinal());
        intent.putExtra("device_type", device_info.device_type.ordinal());
        intent.putExtra("sensor_type", device_info.sensor_type.ordinal());
        intent.putExtra("setup_mode_log", device_info.set_mode_log_entry);

        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void sendCommandReportLocationEnabled(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_LOCATION_ENABLED.ordinal());
        intent.putExtra("enabled", enabled);

        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportEnableAutoUploadOfLogsFilesToServer(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_AUTO_UPLOAD_LOG_FILES_TO_SERVER_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportEwsDownloadSuccess(boolean success)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_EWS_DOWNLOAD_SUCCESS.ordinal());
        intent.putExtra("success", success);
        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportServerDataDownloadStartingDueToMissingThresholds()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_REPORT_SERVER_DATA_SYNC_STARTING_DUE_TO_MISSING_THRESHOLDS);
    }


    public void reportServerDataDownloadStartingDueToSoftwareUpdate()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_REPORT_SERVER_DATA_SYNC_STARTING_DUE_TO_SOFTWARE_UPDATE);
    }


    public void sendCommandReportRealTimeServerType(RealTimeServer server_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_REALTIME_SERVER_TYPE.ordinal());
        intent.putExtra("server_type", server_type.ordinal());

        gateway_context_interface.sendBroadcastIntent(intent);
    }


    public void reportQrCodeOrNfcDetailsToUserInterface(QrCodeData info)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_QR_CODE_DETAILS.ordinal());

        if(info.isValid())
        {
            outgoing_intent.putExtra("bluetooth_device_address", info.getBluetoothDeviceAddress());
            outgoing_intent.putExtra("barcode_type", info.getBarcodeType().ordinal());
            outgoing_intent.putExtra("human_readable_product_id", info.getHumanReadableId());

            outgoing_intent.putExtra("device_type", info.getDeviceType().ordinal());
            outgoing_intent.putExtra("sensor_type", info.getSensorType().ordinal());

            outgoing_intent.putExtra("qr_code_validity", true);

            outgoing_intent.putExtra("emulated", info.emulated);
        }
        else
        {
            outgoing_intent.putExtra("qr_code_validity", false);
        }

        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportNoninPlaybackIsOngoingToUserInterface(boolean playback_has_started)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_NONIN_PLAYBACK_IS_ONGOING.ordinal());
        outgoing_intent.putExtra("playback_has_started", playback_has_started);

        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    // used to disable setup mode button on Patient Vitals
    public void reportNoninPlaybackMightOccurToUserInterface()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_REPORT_NONIN_PLAYBACK_MIGHT_OCCUR);
    }


    public void reportSoftwareUpdateAvailable(DeviceType type, int available_version, String apk_name)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_SOFTWARE_UPDATE_AVAILABLE.ordinal());
        outgoing_intent.putExtra("device_type", type.ordinal());
        outgoing_intent.putExtra("available_version", available_version);
        outgoing_intent.putExtra("apk_name", apk_name);

        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void reportInSoftwareUpdateMode()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_REPORT_IN_SOFTWARE_UPDATE_MODE);
    }


    public void showServerSyncingStatusPopupOnUserInterface(boolean enabled)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_SHOW_SERVER_SYNCING_POPUP.ordinal());
        outgoing_intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }
    
    public void forwardVideoCallRequestToUserInterface(VideoCallDetails videoCallDetails)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_FORWARD_VIDEO_CALL_REQUEST_TO_USER_INTERFACE.ordinal());
        outgoing_intent.putExtra("video_call_details", videoCallDetails);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    public void forwardVideoCallJoinToUserInterface(VideoCallDetails videoCallDetails)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_FORWARD_VIDEO_CALL_JOIN_TO_USER_INTERFACE.ordinal());
        outgoing_intent.putExtra("video_call_details", videoCallDetails);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    public void forwardVideoCallDeclinedToUserInterface(VideoCallContact contactThatDeclinedTheCall)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_FORWARD_VIDEO_CALL_DECLINED_TO_USER_INTERFACE.ordinal());
        outgoing_intent.putExtra("contactThatDeclinedTheCall", contactThatDeclinedTheCall);

        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    public void forwardBrowsersConnectionIdUserInterface(String connectionId)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_FORWARD_VIDEO_CALL_BROWSER_CONNECTION_ID_TO_USER_INTERFACE.ordinal());
        outgoing_intent.putExtra("connectionId", connectionId);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    public void reportBluetoothOff()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_REPORT_BLUETOOTH_OFF_ERROR);
    }

    public void reportVideoCallsEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_VIDEO_CALLS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }

    public void reportPatientSpecificVideoCallContactsReceivedFromServer(boolean success, ArrayList<VideoCallContact> contacts)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_RECEIVED_FROM_SERVER.ordinal());
        intent.putExtra("success", success);
        intent.putExtra("contacts", contacts);
        gateway_context_interface.sendBroadcastIntent(intent);
    }

    public void tellUserInterfaceToExit()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_TELL_USER_INTERFACE_TO_EXIT);
    }

    public void reportFreeDiskSpace(int free_percentage)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_FREE_DISK_SPACE.ordinal());
        intent.putExtra("free_percentage", free_percentage);
        gateway_context_interface.sendBroadcastIntent(intent);
    }

    public void recalculateThresholdsFollowingUpdatedServerConfig()
    {
        sendUserInterfaceCommandNoPayload(Commands.CMD_RECALCULATE_THRESHOLDS_AFTER_SERVER_CONFIG_RECEIVED);
    }

    public void reportViewWebPagesEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_USER_INTERFACE);
        intent.putExtra("command", Commands.CMD_REPORT_VIEW_WEBPAGES_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        gateway_context_interface.sendBroadcastIntent(intent);
    }
}
