package com.isansys.pse_isansysportal;

import android.content.Intent;
import android.os.Bundle;

import com.isansys.common.PatientInfo;
import com.isansys.common.VideoCallContact;
import com.isansys.common.enums.AuditTrailEvent;
import com.isansys.common.enums.Commands;
import com.isansys.common.enums.RealTimeServer;
import com.isansys.common.enums.SensorType;
import com.isansys.common.enums.VideoCallStatus;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;
import com.isansys.common.enums.DeviceType;

import java.util.ArrayList;

public class SystemCommands
{
    private final String INTENT__COMMANDS_TO_PATIENT_GATEWAY = "com.isansys.patientgateway.commands_to_patient_gateway";

    private static ContextInterface ui_context_interface;

    public SystemCommands(ContextInterface context_interface)
    {
        ui_context_interface = context_interface;
    }

    private void sendGatewayCommandNoPayload(Commands command)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", command.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_emptyLocalDatabase()
    {
        sendGatewayCommandNoPayload(Commands.CMD_EMPTY_LOCAL_DATABASE);
    }


    public void sendGatewayCommand_emptyLocalDatabaseIncludingEwsThresholdSets()
    {
        sendGatewayCommandNoPayload(Commands.CMD_EMPTY_LOCAL_DATABASE_INCLUDING_EWS_THRESHOLD_SETS);
    }


    public void sendGatewayCommand_exportLocalDatabaseToAndroidRoot()
    {
        sendGatewayCommandNoPayload(Commands.CMD_EXPORT_LOCAL_DATABASE_TO_ANDROID_ROOT);
    }


    public void sendGatewayCommand_importDatabaseFromAndroidRoot()
    {
        sendGatewayCommandNoPayload(Commands.CMD_IMPORT_DATABASE_FROM_ANDROID_ROOT);
    }


    public void sendGatewayCommand_deleteOldExportedDatabases()
    {
        sendGatewayCommandNoPayload(Commands.CMD_DELETE_EXPORTED_DATABASES);
    }


    public void sendGatewayCommand_enableNightMode(boolean enabled)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        outgoing_intent.putExtra("command", Commands.CMD_ENABLE_NIGHT_MODE.ordinal());
        outgoing_intent.putExtra("night_mode_enable", enabled);
        ui_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendGatewayCommand_radioOff(byte timeTillOff, byte timeOff)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        outgoing_intent.putExtra("command", Commands.CMD_RADIO_OFF.ordinal());
        outgoing_intent.putExtra("radio_off_time_till_off", timeTillOff);
        outgoing_intent.putExtra("radio_off_time_off", timeOff);
        ui_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendGatewayCommand_getServerAddress()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SERVER_ADDRESS);
    }


    public void sendGatewayCommand_setServerAddress(String desired_server_address)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SERVER_ADDRESS.ordinal());
        intent.putExtra("server_address", desired_server_address);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_setDummyServerDetails()
    {
        sendGatewayCommandNoPayload(Commands.CMD_SET_DUMMY_SERVER_DETAILS);
    }


    public void sendGatewayCommand_setServerPort(String desired_server_port)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SERVER_PORT.ordinal());
        intent.putExtra("server_port", desired_server_port);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getServerPort()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SERVER_PORT);
    }


    public void sendGatewayCommand_setRealTimeServerPort(String desired_realtime_server_port)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_REALTIME_SERVER_PORT.ordinal());
        intent.putExtra("realtime_server_port", desired_realtime_server_port);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getRealTimeServerPort()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_REALTIME_SERVER_PORT);
    }


    public void sendGatewayCommand_resetBluetooth(boolean delayed, boolean remove_devices)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_RESET_BLUETOOTH.ordinal());
        intent.putExtra("delayed", delayed);
        intent.putExtra("remove_devices", remove_devices);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getPatientNameFromHospitalPatientId(String hospitalPatientId)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_GET_PATIENT_NAME_FROM_HOSPITAL_PATIENT_ID.ordinal());
        intent.putExtra("hospitalPatientId", hospitalPatientId);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getWardsAndBedsFromServer()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_WARDS_AND_BEDS_FROM_SERVER);
    }


    public void sendGatewayCommand_getGatewayConfigFromServer()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_GATEWAY_CONFIG_FROM_SERVER);
    }


    public void sendGatewayCommand_getServerConfigurableTextFromServer()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SERVER_CONFIGURABLE_TEXT_FROM_SERVER);
    }


    public void sendGatewayCommand_getDefaultEarlyWarningScoreTypesFromServer()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DEFAULT_EARLY_WARNING_SCORE_TYPES_FROM_SERVER);
    }


    public void sendGatewayCommand_getViewableWebPagesFromServer()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_VIEWABLE_WEB_PAGES_FROM_SERVER);
    }


    public void sendGatewayCommand_clearDesiredDevice(DeviceType device_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_CLEAR_DESIRED_DEVICE.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getAllDeviceInfo()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DEVICE_INFO);
    }


    public void sendGatewayCommand_connectToDesiredBluetoothDevices()
    {
        sendGatewayCommandNoPayload(Commands.CMD_CONNECT_TO_DESIRED_BLUETOOTH_DEVICES);
    }


    public void sendGatewayCommand_sendStartSetupModeCommand(DeviceType device_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_START_SETUP_MODE.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_sendStopSetupModeCommand(DeviceType device_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_STOP_SETUP_MODE.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_sendStartDeviceRawAccelerometerModeCommand(DeviceType device_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_START_DEVICE_RAW_ACCELEROMETER_MODE.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_sendStopDeviceRawAccelerometerModeCommand(DeviceType device_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_STOP_DEVICE_RAW_ACCELEROMETER_MODE.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_disconnectDevice(DeviceType device_type, int device_session_ended_by_user_id, boolean turn_off)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_DISCONNECT_FROM_DESIRED_DEVICE.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        intent.putExtra("device_session_ended_by_user_id", device_session_ended_by_user_id);
        intent.putExtra("turn_off", turn_off);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_retryConnectingToDevice(DeviceType device_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_RETRY_CONNECTING_TO_DEVICE.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_forceDeviceLeadsOffState(SensorType sensor_type, boolean simulate_leads_off)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_FORCE_DEVICE_LEADS_OFF_STATE.ordinal());
        intent.putExtra("sensor_type", sensor_type.ordinal());
        intent.putExtra("simulate_leads_off", simulate_leads_off);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_enableServerSync(boolean enable_or_disable)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_ENABLE_SERVER_SYNCING.ordinal());
        intent.putExtra("enable_server_syncing", enable_or_disable);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getServerSyncEnableStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SERVER_SYNC_ENABLE_STATUS);
    }


    public void sendGatewayCommand_getDummyDataModeDeviceLeadsOffEnabledStatus(SensorType sensor_type)
    {
        sendGatewayCommandWithSensorType(Commands.CMD_GET_DUMMY_DATA_MODE_DEVICE_LEADS_OFF_ENABLE_STATUS, sensor_type);
    }


    public void sendGatewayCommand_getDummyDataModeSpoofEarlyWarningScores()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SPOOF_EARLY_WARNING_SCORES);
    }


    public void sendGatewayCommand_enableRealTimeLink(boolean enable_or_disable)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_ENABLE_REALTIME_LINK.ordinal());
        intent.putExtra("enable_real_time_link", enable_or_disable);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getRealtimeLinkEnableStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_REALTIME_LINK_ENABLE_STATUS);
    }


    public void sendGatewayCommand_validateQrCode(String qr_code_contents)
    {
        Bundle bundle = new Bundle();

        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_VALIDATE_QR_CODE.ordinal());
        intent.putExtra("QR_CODE_CONTENTS", qr_code_contents);
        intent.putExtras(bundle);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_validateInstallationQrCode(String qr_code_contents)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_VALIDATE_INSTALLATION_QR_CODE.ordinal());
        intent.putExtra("QR_CODE_CONTENTS", qr_code_contents);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_validateDataMatrix(String contents)
    {
        Bundle bundle = new Bundle();

        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_VALIDATE_DATA_MATRIX_BARCODE.ordinal());
        intent.putExtra("contents", contents);
        intent.putExtras(bundle);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    // This command creates a new row in the SessionSettingsStorage database containing the Patient's details, and the connection measurement devices.
    public void sendGatewayCommand_createNewSessionCommand(PatientInfo patient_info, int gateway_user_id)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_CREATE_NEW_SESSION.ordinal());
        intent.putExtra("hospital_patient_id", patient_info.getHospitalPatientId());
        intent.putExtra("servers_threshold_set_id", patient_info.getThresholdSet().servers_database_row_id);
        intent.putExtra("servers_threshold_set_age_block_id", patient_info.getThresholdSetAgeBlockDetails().servers_database_row_id);
        intent.putExtra("gateway_user_id", gateway_user_id);

        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_updateExistingSessionCommand(int gateway_user_id)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_UPDATE_EXISTING_SESSION.ordinal());
        intent.putExtra("gateway_user_id", gateway_user_id);

        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_endExistingSession(int gateway_user_id, boolean turn_devices_off, long session_end_time)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_END_EXISTING_SESSION.ordinal());
        intent.putExtra("session_ended_by_user_id", gateway_user_id);
        intent.putExtra("turn_devices_off", turn_devices_off);
        intent.putExtra("session_end_time", session_end_time);

        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getPatientThresholdSetCommand()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_PATIENT_THRESHOLD_SET);
    }


    public void sendGatewayCommand_getHospitalPatientIdCommand()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_HOSPITAL_PATIENT_ID);
    }


    public void sendGatewayCommand_getGatewaySessionNumbersCommand()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_GATEWAY_SESSION_NUMBERS);
    }


    public void sendGatewayCommand_pingServer()
    {
        sendGatewayCommandNoPayload(Commands.CMD_PING_SERVER);
    }


    public void sendGatewayCommand_setDesiredDevice(DeviceInfo device_info)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);

        intent.putExtra("command", Commands.CMD_SET_DESIRED_DEVICE.ordinal());
        intent.putExtra("device_type", device_info.device_type.ordinal());
        intent.putExtra("human_readable_device_id", device_info.human_readable_device_id);
        intent.putExtra("bluetooth_address", device_info.bluetooth_address);
        intent.putExtra("device_name", device_info.device_name);
        intent.putExtra("dummy_data_mode", device_info.dummy_data_mode);
        intent.putExtra("firmware_version", device_info.firmware_version);
        intent.putExtra("firmware_string", device_info.firmware_string);
        intent.putExtra("measurement_interval_in_seconds", device_info.measurement_interval_in_seconds);

        // These are only valid for devices with DataMatrix barcodes. QR codes do not have this info in them
        intent.putExtra("lot_number", device_info.lot_number);
        intent.putExtra("manufacture_date_in_millis", device_info.manufacture_date.getMillis());
        intent.putExtra("expiration_date_in_millis", device_info.expiration_date.getMillis());

        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_setGatewaysAssignedBedDetails(String gateways_assigned_bed_id, String gateways_assigned_ward_name, String gateways_assigned_bed_name)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_GATEWAYS_ASSIGNED_BED_DETAILS.ordinal());
        intent.putExtra("gateways_assigned_bed_id", gateways_assigned_bed_id);
        intent.putExtra("gateways_assigned_ward_name", gateways_assigned_ward_name);
        intent.putExtra("gateways_assigned_bed_name", gateways_assigned_bed_name);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getGatewaysAssignedBedDetails()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_GATEWAYS_ASSIGNED_BED_DETAILS);
    }


    public void sendGatewayCommand_checkDeviceStatus(DeviceType device_type, long human_readable_device_id)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_CHECK_DEVICE_STATUS.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        intent.putExtra("human_readable_device_id", human_readable_device_id);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_sendGatewayPing()
    {
        sendGatewayCommandNoPayload(Commands.CMD_CHECK_GATEWAY_UI_CONNECTION);
    }


    public void sendGatewayCommand_refreshDeviceConnectionState()
    {
        sendGatewayCommandNoPayload(Commands.CMD_REFRESH_DEVICE_CONNECTION_STATE);
    }


    public void sendGatewayCommand_setHttpsEnableStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_ENABLE_HTTPS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getHttpsEnableStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_HTTPS_ENABLE_STATUS);
    }


    public void sendGatewayCommand_enableWebServiceAuthentication(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_ENABLE_WEBSERVICE_AUTHENTICATION.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getWebServiceAuthenticationEnableStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_WEBSERVICE_AUTHENTICATION_ENABLE_STATUS);
    }


    public void sendGatewayCommand_enableWebServiceEncryption(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_ENABLE_WEBSERVICE_ENCRYPTION.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getWebServiceEncryptionEnableStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_WEBSERVICE_ENCRYPTION_ENABLE_STATUS);
    }


    public void sendGatewayCommand_setPatientNameLookupEnabled(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_ENABLE_PATIENT_NAME_LOOKUP.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getPatientNameLookupEnableStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_PATIENT_NAME_LOOKUP_ENABLE_STATUS);
    }


    public void sendGatewayCommand_setSimpleHeartRateEnabled(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SIMPLE_HEART_RATE_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getSimpleHeartRateEnabled()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SIMPLE_HEART_RATE_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setGsmModeOnlyEnabled(boolean gsm_mode_only)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_GSM_MODE_ONLY_ENABLED_STATUS.ordinal());
        intent.putExtra("gsm_mode_only", gsm_mode_only);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getGsmModeOnlyEnabled()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_GSM_MODE_ONLY_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setCsvOutputEnabled(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_ENABLE_CSV_OUTPUT.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getCsvOutputEnableStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_CSV_ENABLE_STATUS);
    }


    public void sendGatewayCommand_enableDevicePeriodicSetupMode(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_ENABLE_DEVICE_PERIODIC_SETUP_MODE.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getLifetouchPeriodicSamplingStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DEVICE_PERIODIC_SETUP_MODE_ENABLE_STATUS);
    }

    public void sendGatewayCommand_getDeviceLeadsOffStatus(DeviceType device_type)
    {
        Intent outgoing_intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        outgoing_intent.putExtra("command", Commands.CMD_GET_DEVICE_LEADS_OFF_STATUS.ordinal());
        outgoing_intent.putExtra("device_type", device_type.ordinal());
        ui_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    public void sendGatewayCommand_timeSync()
    {
        sendGatewayCommandNoPayload(Commands.CMD_DO_NTP_TIME_SYNC);
    }


    public void sendAdminModeExitButtonPressed_Event()
    {
        sendGatewayCommandNoPayload(Commands.CMD_EXIT_BUTTON_PRESSED);
    }


    public void sendGatewayCommand_getJsonArraySize()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_JSON_ARRAY_SIZE);
    }


    public void sendGatewayCommand_setJsonArraySize(int json_array_size)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_JSON_ARRAY_SIZE.ordinal());
        intent.putExtra("json_array_size", json_array_size);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_spoofDeviceConnectionState(SensorType sensor_type, boolean connected)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SPOOF_DEVICE_CONNECTION_STATE.ordinal());
        intent.putExtra("sensor_type", sensor_type.ordinal());
        intent.putExtra("connected", connected);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_setNumberOfDummyDataModeMeasurementsPerTick(int measurements_per_tick)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK.ordinal());
        intent.putExtra("measurements_per_tick", measurements_per_tick);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getNumberOfDummyDataModeMeasurementsPerTick()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK);
    }

    public void sendGatewayCommand_tellGatewayThatUiHasBooted(long most_recent_ui_boot_time)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_TELL_GATEWAY_THAT_UI_HAS_BOOTED.ordinal());
        intent.putExtra("most_recent_ui_boot_time", most_recent_ui_boot_time);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getPatientStartSessionTime()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_PATIENT_START_SESSION_TIME);
    }

    public void sendGatewayCommand_getSetupModeTimeInSeconds()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SETUP_MODE_TIME_IN_SECONDS);
    }

    public void sendGatewayCommand_setSetupModeTimeInSeconds(int setup_mode_time_in_seconds)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SETUP_MODE_TIME_IN_SECONDS.ordinal());
        intent.putExtra("setup_mode_time_in_seconds", setup_mode_time_in_seconds);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getMaxNumberNoninPulseOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID);
    }

    public void sendGatewayCommand_setMaxNumberNoninPulseOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(int number)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID.ordinal());
        intent.putExtra("number", number);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getDisplayTimeoutInSeconds()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DISPLAY_TIMEOUT_IN_SECONDS);

    }

    public void sendGatewayCommand_setDisplayTimeoutInSeconds(int time_in_seconds)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DISPLAY_TIMEOUT_IN_SECONDS.ordinal());
        intent.putExtra("time_in_seconds", time_in_seconds);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_setDisplayTimeoutAppliesToPatientVitalsDisplay(boolean display_timeout_applies_to_patient_vitals)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY.ordinal());
        intent.putExtra("display_timeout_applies_to_patient_vitals", display_timeout_applies_to_patient_vitals);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getDisplayTimeoutAppliesToPatientVitalsDisplay()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY);
    }


    public void sendGatewayCommand_getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID);
    }

    public void sendGatewayCommand_setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(int percentage)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID.ordinal());
        intent.putExtra("percentage", percentage);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_storeVitalSign(VitalSignType vital_sign_type, DeviceType device_type, MeasurementVitalSign data_point, int by_user_id)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_STORE_VITAL_SIGN.ordinal());
        intent.putExtra("vital_sign_type", vital_sign_type.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        intent.putExtra("data_point", data_point);
        intent.putExtra("by_user_id", by_user_id);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_setEarlyWarningScoresDeviceEnabled(boolean enabled, boolean dummy_data_mode, int user_id)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_ENABLE_EARLY_WARNING_SCORES_DEVICE.ordinal());

        intent.putExtra("enabled", enabled);
        intent.putExtra("dummy_data_mode", dummy_data_mode);
        intent.putExtra("user_id", user_id);

        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_setManualVitalSignsEnabled(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_MANUAL_VITAL_SIGNS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getManualVitalSignsEnableStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_MANUAL_VITAL_SIGNS_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setSpoofEarlyWarningScores(boolean spoof)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SPOOF_EARLY_WARNING_SCORES.ordinal());

        intent.putExtra("spoof", spoof);

        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void reportPatientOrientation()
    {
        sendGatewayCommandNoPayload(Commands.CMD_REPORT_PATIENT_ORIENTATION);
    }

    public void reportLogCatEnableStatus(boolean enable)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_LOG_CAT_MESSAGES.ordinal());
        intent.putExtra("enabled", enable);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_stopRunningBluetoothScan()
    {
        sendGatewayCommandNoPayload(Commands.CMD_STOP_RUNNING_BLUETOOTH_SCAN);
    }

    public void sendGatewayCommand_wifiReconnection()
    {
        sendGatewayCommandNoPayload(Commands.CMD_RECONNECT_WIFI);
    }


    public void sendGatewayCommand_getWifiStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_WIFI_STATUS);
    }


    public void sendGatewayCommand_setRunDevicesInTestMode(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_RUN_DEVICES_IN_TEST_MODE.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getRunDevicesInTestMode()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_RUN_DEVICES_IN_TEST_MODE);
    }


    private void sendGatewayCommand_setTimeout(SensorType sensorType, int timeout)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES.ordinal());
        intent.putExtra("sensor_type", sensorType.ordinal());
        intent.putExtra("timeout", timeout);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    private void sendGatewayCommandWithSensorType(Commands command, SensorType sensorType)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", command.ordinal());
        intent.putExtra("sensor_type", sensorType.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    private void sendGatewayCommand_getLongTermMeasurementTimeout(SensorType sensorType)
    {
        sendGatewayCommandWithSensorType(Commands.CMD_GET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES, sensorType);
    }

    public void sendGatewayCommand_getBloodPressureLongTermMeasurementTimeout()
    {
        sendGatewayCommand_getLongTermMeasurementTimeout(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
    }

    public void sendGatewayCommand_setBloodPressureLongTermMeasurementTimeoutInMinutes(int timeout)
    {
        sendGatewayCommand_setTimeout(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, timeout);
    }


    public void sendGatewayCommand_getSpO2LongTermMeasurementTimeout()
    {
        sendGatewayCommand_getLongTermMeasurementTimeout(SensorType.SENSOR_TYPE__SPO2);
    }

    public void sendGatewayCommand_setSpO2LongTermMeasurementTimeoutInMinutes(int timeout)
    {
        sendGatewayCommand_setTimeout(SensorType.SENSOR_TYPE__SPO2, timeout);
    }


    public void sendGatewayCommand_getWeightLongTermMeasurementTimeout()
    {
        sendGatewayCommand_getLongTermMeasurementTimeout(SensorType.SENSOR_TYPE__WEIGHT_SCALE);
    }

    public void sendGatewayCommand_setWeightLongTermMeasurementTimeoutInMinutes(int timeout)
    {
        sendGatewayCommand_setTimeout(SensorType.SENSOR_TYPE__WEIGHT_SCALE, timeout);
    }


    public void sendGatewayCommand_getThirdPartyTemperatureLongTermMeasurementTimeout()
    {
        sendGatewayCommand_getLongTermMeasurementTimeout(SensorType.SENSOR_TYPE__TEMPERATURE);
    }

    public void sendGatewayCommand_setThirdPartyTemperatureLongTermMeasurementTimeoutInMinutes(int timeout)
    {
        sendGatewayCommand_setTimeout(SensorType.SENSOR_TYPE__TEMPERATURE, timeout);
    }


    public void sendGatewayCommand_getDatabaseStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DATABASE_STATUS_FOR_POPUP_SERVER_STATUS);
    }


    public void sendGatewayCommand_setUnpluggedOverlayEnableStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_UNPLUGGED_OVERLAY_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getUnpluggedOverlayEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_UNPLUGGED_OVERLAY_ENABLED_STATUS);
    }

    public void sendGatewayCommand_setLT3KHzSetupModeEnableStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_LT3_KHZ_SETUP_MODE_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getLT3KHzSetupModeEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_LT3_KHZ_SETUP_MODE_ENABLED_STATUS);
    }

    public void sendGatewayCommand_setAutoAddEwsEnableStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_AUTO_ENABLE_EWS__ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getAutoAddEwsEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_AUTO_ENABLE_EWS__ENABLED_STATUS);
    }


    public void sendGatewayCommand_enableDummyDataModeBackfillSessionWithData(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DUMMY_DATA_MODE_BACKFILL_SESSION_WITH_DATA_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_setDummyDataModeBackfillHours(int hours)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DUMMY_DATA_MODE_BACKFILL_HOURS.ordinal());
        intent.putExtra("hours", hours);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getNtpClockOffsetInMs()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_NTP_CLOCK_OFFSET_IN_MS);
    }


    public void sendGatewayCommand_setManufacturingModeEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_MANUFACTURING_MODE_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getManufacturingModeEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_MANUFACTURING_MODE_ENABLED_STATUS);
    }


    public void sendGatewayCommand_disableBluetoothAdapter()
    {
        sendGatewayCommandNoPayload(Commands.CMD_DISABLE_BLUETOOTH_ADAPTER);
    }


    public void sendGatewayCommand_enableBluetoothAdapter()
    {
        sendGatewayCommandNoPayload(Commands.CMD_ENABLE_BLUETOOTH_ADAPTER);
    }


    public void sendGatewayCommand_disableWifi()
    {
        sendGatewayCommandNoPayload(Commands.CMD_DISABLE_WIFI);
    }


    public void sendGatewayCommand_enableWifi()
    {
        sendGatewayCommandNoPayload(Commands.CMD_ENABLE_WIFI);
    }


    public void sendGatewayCommand_getSweetblueDiagnostics()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SWEETBLUE_DIAGNOSTICS);
    }


    public void sendGatewayCommand_setUseBackCameraEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_USE_BACK_CAMERA_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getUseBackCameraEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_USE_BACK_CAMERA_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setPatientOrientationEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_PATIENT_ORIENTATION_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getDisplayPatientOrientationEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_PATIENT_ORIENTATION_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setAutoUploadLogFilesToServerEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_AUTO_UPLOAD_LOG_FILES_TO_SERVER_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getAutoUploadLogFilesToServerEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_AUTO_UPLOAD_LOG_FILES_TO_SERVER_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setShowNumbersOnBatteryIndicatorEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SHOW_NUMBERS_ON_BATTERY_INDICATOR_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getShowNumbersOnBatteryIndicatorEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SHOW_NUMBERS_ON_BATTERY_INDICATOR_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setShowMacAddressEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SHOW_MAC_ADDRESS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getShowMacAddressEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SHOW_MAC_ADDRESS_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setUsaModeEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_USA_MODE_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getUsaModeEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_USA_MODE_ENABLED_STATUS);
    }


    public void sendGatewayCommand_restartInstallationWizard()
    {
        sendGatewayCommandNoPayload(Commands.CMD_RESTART_INSTALLATION_WIZARD);
    }


    public void sendGatewayCommand_storeDeveloperPopupEnabled(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DEVELOPER_POPUP_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getDeveloperPopupEnabled()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DEVELOPER_POPUP_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setLifetempMeasurementIntervalInSeconds(int measurement_interval_in_seconds)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS.ordinal());
        intent.putExtra("measurement_interval_in_seconds", measurement_interval_in_seconds);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getLifetempMeasurementInterval()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS);
    }


    public void sendGatewayCommand_setPatientOrientationMeasurementIntervalInSeconds(int measurement_interval_in_seconds)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS.ordinal());
        intent.putExtra("measurement_interval_in_seconds", measurement_interval_in_seconds);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getPatientOrientationMeasurementInterval()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS);
    }


    public void sendGatewayCommand_storeShowIpAddressOnWifiPopupEnabled(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SHOW_IP_ADDRESS_ON_WIFI_POPUP_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getShowIpAddressOnWifiPopupEnabled()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SHOW_IP_ADDRESS_ON_WIFI_POPUP_ENABLED_STATUS);
    }


    public void sendGatewayCommand_getDeviceOperatingMode(DeviceType device_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_GET_DEVICE_OPERATING_MODE.ordinal());
        intent.putExtra("device_type", device_type.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_setShowLifetouchActivityLevelEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SHOW_LIFETOUCH_ACTIVITY_LEVEL_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getShowLifetouchActivityEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SHOW_LIFETOUCH_ACTIVITY_LEVEL_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setAutoResumeEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_AUTO_RESUME_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getAutoResumeEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_AUTO_RESUME_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setWifiLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_WIFI_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getWifiLoggingEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_WIFI_LOGGING_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setGsmLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_GSM_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getGsmLoggingEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_GSM_LOGGING_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setDatabaseLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DATABASE_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getDatabaseLoggingEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DATABASE_LOGGING_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setServerLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SERVER_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getServerLoggingEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SERVER_LOGGING_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setBatteryLoggingEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_BATTERY_LOGGING_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getBatteryLoggingEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_BATTERY_LOGGING_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setDfuBootloaderEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DFU_BOOTLOADER_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getDfuBootloaderEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DFU_BOOTLOADER_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setPredefinedAnnotationEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_PREDEFINED_ANNOTATION_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getPredefinedAnnotationEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_PREDEFINED_ANNOTATION_ENABLED_STATUS);
    }


    public void sendGatewayCommand_setSpO2SpotMeasurementsEnableStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getSpo2SpotMeasurementsEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS);
    }


    public void sendGatewayCommand_forceCheckForLatestFirmwareImagesFromServer()
    {
        sendGatewayCommandNoPayload(Commands.CMD_FORCE_CHECK_FOR_LATEST_FIRMWARE_IMAGES_FROM_SERVER);
    }


    public void sendGatewayCommand_getInstallationComplete()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_INSTALLATION_COMPLETE);
    }


    public void sendGatewayCommand_setInstallationComplete()
    {
        sendGatewayCommandNoPayload(Commands.CMD_SET_INSTALLATION_COMPLETE);
    }


    public void sendGatewayCommand_resetDatabaseFailedToSendStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_RESET_DATABASE_FAILED_TO_SEND_STATUS);
    }


    public void sendGatewayCommand_crashPatientGatewayOnDemand()
    {
        sendGatewayCommandNoPayload(Commands.CMD_CRASH_PATIENT_GATEWAY_ON_DEMAND);
    }


    public void sendGatewayCommand_startNoninBlePlaybackSimulationFromFile()
    {
        sendGatewayCommandNoPayload(Commands.CMD_START_NONIN_PLAYBACK_SIMULATION_FROM_FILE);
    }


    public void sendGatewayCommand_getLocationEnabled()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_LOCATION_ENABLED);
    }


    public void sendGatewayCommand_getDevicePeriodicModePeriodTimeInSeconds()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS);
    }

    public void sendGatewayCommand_setDevicePeriodicModePeriodTimeInSeconds(int time_in_seconds)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS.ordinal());
        intent.putExtra("time_in_seconds", time_in_seconds);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getDevicePeriodicModeActiveTimeInSeconds()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS);
    }


    public void sendGatewayCommand_setDevicePeriodicModeActiveTimeInSeconds(int time_in_seconds)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS.ordinal());
        intent.putExtra("time_in_seconds", time_in_seconds);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getRealtimeServerType()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_REALTIME_SERVER_TYPE);
    }


    public void sendGatewayCommand_setRealtimeServerType(RealTimeServer server_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_REALTIME_SERVER_TYPE.ordinal());
        intent.putExtra("realtime_server_type", server_type.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_getLastNetworkStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_LAST_NETWORK_STATUS);
    }

    public void sendGatewayCommand_getRealTimeServerConnectedStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_REALTIME_SERVER_CONNECTED_STATUS);
    }


    public void sendGatewayCommand_storeAuditTrailEvent(AuditTrailEvent event, long timestamp, int by_user_id, String additional)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_STORE_AUDIT_TRAIL_EVENT.ordinal());
        intent.putExtra("by_user_id", by_user_id);
        intent.putExtra("event", event.ordinal());
        intent.putExtra("additional", additional);
        intent.putExtra("timestamp", timestamp);
        ui_context_interface.sendBroadcastIntent(intent);
    }


    public void sendGatewayCommand_enterUpdateMode()
    {
        sendGatewayCommandNoPayload(Commands.CMD_ENTER_SOFTWARE_UPDATE_MODE);
    }

    public void sendGatewayCommand_softwareUpdateComplete()
    {
        sendGatewayCommandNoPayload(Commands.CMD_SOFTWARE_UPDATE_COMPLETE);
    }

    public void sendGatewayCommand_getSoftwareUpdateModeState()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_SOFTWARE_UPDATE_STATE);
    }

    public void sendGatewayCommand_startServerDataDownload()
    {
        sendGatewayCommandNoPayload(Commands.CMD_START_SERVER_DATA_DOWNLOAD);
    }

    public void sendGatewayCommand_forceServerDataDownload()
    {
        sendGatewayCommandNoPayload(Commands.CMD_FORCE_SERVER_DATA_DOWNLOAD);
    }


    public void sendGatewayCommand_serverSyncingTestMode(boolean test_mode_on)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SERVER_SYNC_DATA_IN_TEST_MODE.ordinal());
        intent.putExtra("test_mode", test_mode_on);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_setVideoCallsEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_VIDEO_CALLS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getVideoCallsEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_VIDEO_CALLS_ENABLED_STATUS);
    }


    public void sendGatewayCommand_reportGatewayVideoCallStatus(String connection_id, VideoCallStatus videoCallStatus)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_REPORT_GATEWAY_VIDEO_CALL_STATUS.ordinal());
        intent.putExtra("connection_id", connection_id);
        intent.putExtra("videoCallStatus", videoCallStatus.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_reportPatientSpecificVideoCallContactsFromServer()
    {
        sendGatewayCommandNoPayload(Commands.CMD_REQUEST_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_FROM_SERVER);
    }

    public void sendGatewayCommand_patientRequestingOutgoingVideoCall(ArrayList<VideoCallContact> contacts)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_PATIENT_ON_GATEWAY_REQUESTING_VIDEO_CALL.ordinal());
        intent.putParcelableArrayListExtra("contact", contacts);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_patientCancelledOutgoingVideoCallRequest()
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_PATIENT_ON_GATEWAY_CANCELLED_VIDEO_CALL_REQUEST.ordinal());
        //intent.putExtra("contact", contact);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_dummyUserQrCode()
    {
        sendGatewayCommandNoPayload(Commands.CMD_DUMMY_QR_CODE_UNLOCK_USER);
    }

    public void sendGatewayCommand_dummyAdminQrCode()
    {
        sendGatewayCommandNoPayload(Commands.CMD_DUMMY_QR_CODE_UNLOCK_ADMIN);
    }

    public void sendGatewayCommand_setDisplayTemperatureInFahrenheitEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getDisplayTemperatureInFahrenheitEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS);
    }

    public void sendGatewayCommand_setDisplayWeightInLbsEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getDisplayWeightInLbsEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS);
    }

    public void sendGatewayCommand_getFreeDiskSpace()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_FREE_DISK_SPACE);
    }

    public void sendGatewayCommand_setViewWebPagesEnabledStatus(boolean enabled)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_SET_VIEW_WEBPAGES_ENABLED_STATUS.ordinal());
        intent.putExtra("enabled", enabled);
        ui_context_interface.sendBroadcastIntent(intent);
    }

    public void sendGatewayCommand_getViewWebPagesEnabledStatus()
    {
        sendGatewayCommandNoPayload(Commands.CMD_GET_VIEW_WEBPAGES_ENABLED_STATUS);
    }

    public void sendGatewayCommand_removeVitalSignTypeFromEws(VitalSignType vital_sign_type)
    {
        Intent intent = new Intent(INTENT__COMMANDS_TO_PATIENT_GATEWAY);
        intent.putExtra("command", Commands.CMD_REMOVE_VITAL_SIGN_TYPE_FROM_EWS.ordinal());
        intent.putExtra("vital_sign_type", vital_sign_type.ordinal());
        ui_context_interface.sendBroadcastIntent(intent);
    }
}
