package com.isansys.patientgateway.database;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;

import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.QueryType;
import com.isansys.common.enums.SensorType;
import com.isansys.patientgateway.GenericStartStopTimer;
import com.isansys.patientgateway.LocalDatabaseStorage;
import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.serverlink.ServerSyncStatus;
import com.isansys.patientgateway.serverlink.ServerSyncing;
import com.isansys.patientgateway.Settings;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.patientgateway.remotelogging.RemoteLogging;
import com.isansys.patientgateway.serverlink.constants.ServerResponseErrorCodes;
import com.isansys.patientgateway.serverlink.constants.ServerSyncingDataUploadPoint;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerSyncTableObserver extends ContentObserver
{
    private final String TAG = "ServerSyncTableObserver";

    private final RemoteLoggingWithEnable Log;

    private final Settings settings;

    private final DeviceInfoManager device_info_manager;
    private final LocalDatabaseStorage local_database_storage;

    private final PatientGatewayInterface patient_gateway_interface;

    private final ServerSyncing server_syncing;

    private final ServerSyncStatus server_sync_status;

    private final ExecutorService sync_executor;

    public ServerSyncTableObserver(RemoteLogging logger, ServerSyncing desired_server_syncing, Handler handler, Settings passed_settings, DeviceInfoManager info_manager, LocalDatabaseStorage database_storage, PatientGatewayInterface desired_patient_gateway_interface, boolean enable_logs, ServerSyncStatus sync_status)
    {
        super(handler);

        Log = new RemoteLoggingWithEnable(logger, enable_logs);
        settings = passed_settings;
        server_syncing = desired_server_syncing;
        device_info_manager = info_manager;
        local_database_storage = database_storage;
        patient_gateway_interface = desired_patient_gateway_interface;

        server_sync_status = sync_status;

        sync_executor = Executors.newSingleThreadExecutor();

        resetServerSyncStatusAndTriggerSync();
    }

    /**
     * Enum for procedure to sync data to server. Procedure to sync Patient's Data, Patient's Session information is different
     */
    private enum ServerSyncAdaptorStatus
    {
        DATA_FULLY_SYNCED,

        START_DATA_SYNC_TO_SERVER,
        DATA_BEING_SYNCED_TO_SERVER,
        DATA_SUCCESSFULLY_SYNCED_TO_SERVER,

        CHECK_PATIENTS_SESSION_DATA_IN_SERVER,
        WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER,
        WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER,
        SEND_PATIENT_SESSION_DATA_TO_SERVER,
        PATIENT_SESSION_SUCCESSFULLY_SYNCED_TO_SERVER,
    }

    private volatile ServerSyncAdaptorStatus server_sync_adaptor_state = ServerSyncAdaptorStatus.DATA_FULLY_SYNCED;


    private ServerSyncingDataUploadPoint entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;
    private ServerSyncingDataUploadPoint previous_entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;



    private boolean getNextDataTypeToUpload(int number_of_rows_pending, ServerSyncingDataUploadPoint fallback_data_type_to_upload, boolean is_recurrence_call)
    {
        if(number_of_rows_pending > 0)
        {
            if (current_error_handler_state == ErrorState.NONE)
            {
                // Check if current data type is synced before
                if(previous_entire_session_data_upload_start_point == entire_session_data_upload_start_point)
                {
                    // This row was synced last time. Try other row for upload data
                    entire_session_data_upload_start_point = fallback_data_type_to_upload;

                    if(is_recurrence_call)
                    {
                        // one cycle is completed. No other data to sync. Set the entire_session_data_upload_start_point to Current data type

                        // Need to do a break to not auto progress in setDataUploadPoint
                        return true;
                    }

                    // Do NOT do a break after returning. Causes the code to auto drop down into the next case in setDataUploadPoint (which is fallback_data_type_to_upload)
                    return false;
                }
                else
                {
                    // No error in server sync. This vital sign isn't synced before.

                    // Need to do a break to not auto progress in setDataUploadPoint
                    return true;
                }
            }
            else
            {
                // Error has happened while server syncing so move on an attempt to send the next measurement type
                entire_session_data_upload_start_point = fallback_data_type_to_upload;

                // Need to do a break to not auto progress in setDataUploadPoint
                return true;
            }
        }
        else
        {
            // No rows pending. Move onto next type to upload
            entire_session_data_upload_start_point = fallback_data_type_to_upload;

            // Do NOT do a break after returning. Causes the code to auto drop down into the next case in setDataUploadPoint (which is fallback_data_type_to_upload)
            return false;
        }
    }


    /**
     * ALL Patient Session information should be synced to server before proceeding to patient data
     * @param number_of_rows_pending : Integer - Total Number of syncable row pending
     * @param fallback_data_type_to_upload : ServerSyncingDataUploadPoint - If check fails then move to next data point
     * @param is_recurrence_call : boolean - setDataUploadPoint() is called twice to go through all the data upload points to determine sync point
     * @return : boolean, True if current Session information is needed to be synced
     */
    private boolean getNextPatientSessionUploadPoint(int number_of_rows_pending, ServerSyncingDataUploadPoint fallback_data_type_to_upload, boolean is_recurrence_call)
    {
        if(number_of_rows_pending > 0)
        {
            // No error in server sync. This vital sign isn't synced before.

            // Need to do a break;
            return true;
        }
        else
        {
            // Error has happened while server syncing. Check if this row is tried before
            if (previous_entire_session_data_upload_start_point == entire_session_data_upload_start_point)
            {
                // if true one loop is complete. No more data is left to send.
                if(is_recurrence_call)
                {
                    // one cycle is completed. Set the entire_session_data_upload_start_point to default "NO_MORE_DATA_TO_SYNC"
                    entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.NO_MORE_DATA_TO_SYNC;

                    // Need to do a break;
                    return true;
                }
            }

            entire_session_data_upload_start_point = fallback_data_type_to_upload;
        }

        // Do NOT do a break;
        return false;
    }


    private void setDataUploadPoint(boolean is_recurrence_call)
    {
        final boolean GET_LIMIT_DATA_FROM_QUERY = true;

        Log.d(TAG, "setDataUploadPoint : current start point : " + entire_session_data_upload_start_point);

        switch (entire_session_data_upload_start_point)
        {
            case PATIENT_DETAILS_DATA:
            {
                server_sync_status.patient_details = local_database_storage.checkIfPatientDetailsPendingToBeSentToServer();

                if(getNextPatientSessionUploadPoint(server_sync_status.patient_details.rows_pending_syncable, ServerSyncingDataUploadPoint.DEVICE_INFO_DATA, is_recurrence_call))
                {
                    break;
                }
            }

            case DEVICE_INFO_DATA:
            {
                server_sync_status.device_info = local_database_storage.checkIfDeviceInfoPendingToBeSentToServer();

                if(getNextPatientSessionUploadPoint(server_sync_status.device_info.rows_pending_syncable, ServerSyncingDataUploadPoint.START_PATIENT_SESSION, is_recurrence_call))
                {
                    break;
                }
            }

            case START_PATIENT_SESSION:
            {
                server_sync_status.start_patient_session = local_database_storage.checkIfStartPatientSessionPendingToBeSentToServer();

                if(getNextPatientSessionUploadPoint(server_sync_status.start_patient_session.rows_pending_syncable, ServerSyncingDataUploadPoint.START_DEVICE_SESSION, is_recurrence_call))
                {
                    break;
                }
            }

            case START_DEVICE_SESSION:
            {
                server_sync_status.start_device_session = local_database_storage.checkIfStartDeviceSessionPendingToBeSentToServer();

                if(getNextPatientSessionUploadPoint(server_sync_status.start_device_session.rows_pending_syncable, ServerSyncingDataUploadPoint.END_DEVICE_SESSION, is_recurrence_call))
                {
                    break;
                }
            }

            case END_DEVICE_SESSION:
            {
                server_sync_status.end_device_session = local_database_storage.checkIfEndDeviceSessionPendingToBeSentToServer();

                if(getNextPatientSessionUploadPoint(server_sync_status.end_device_session.rows_pending_syncable, ServerSyncingDataUploadPoint.END_PATIENT_SESSION, is_recurrence_call))
                {
                    break;
                }
            }

            case END_PATIENT_SESSION:
            {
                server_sync_status.end_patient_session = local_database_storage.checkIfEndPatientSessionPendingToBeSentToServer();

                if(getNextPatientSessionUploadPoint(server_sync_status.end_patient_session.rows_pending_syncable, ServerSyncingDataUploadPoint.CONNECTION_EVENT, is_recurrence_call))
                {
                    break;
                }
            }

            case CONNECTION_EVENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_connection_event_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_connection_event_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.CONNECTION_EVENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_connection_event_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.LIFETOUCH_HEART_RATE, is_recurrence_call))
                {
                    break;
                }
            }

            case LIFETOUCH_HEART_RATE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_lifetouch_heart_rate_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_lifetouch_heart_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_HEART_RATES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_lifetouch_heart_rate_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.LIFETOUCH_HEART_BEAT, is_recurrence_call))
                {
                    break;
                }
            }

            case LIFETOUCH_HEART_BEAT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_lifetouch_heart_beat_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_lifetouch_heart_beat_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_HEART_BEATS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_lifetouch_heart_beat_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.LIFETOUCH_RESPIRATION_RATE, is_recurrence_call))
                {
                    break;
                }
            }

            case LIFETOUCH_RESPIRATION_RATE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_lifetouch_respiration_rate_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_lifetouch_respiration_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_RESPIRATION_RATES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_lifetouch_respiration_rate_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.LIFETOUCH_SETUP_MODE, is_recurrence_call))
                {
                    break;
                }
            }

            case LIFETOUCH_SETUP_MODE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_lifetouch_setup_mode_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_lifetouch_setup_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_SETUP_MODE_RAW_SAMPLES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_lifetouch_setup_mode_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.LIFETOUCH_RAW_ACCELEROMETER_MODE, is_recurrence_call))
                {
                    break;
                }
            }

            case LIFETOUCH_RAW_ACCELEROMETER_MODE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.LIFETOUCH_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case LIFETOUCH_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_lifetouch_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_lifetouch_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_lifetouch_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.LIFETOUCH_PATIENT_ORIENTATION, is_recurrence_call))
                {
                    break;
                }
            }

            case LIFETOUCH_PATIENT_ORIENTATION:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_lifetouch_patient_orientation_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_lifetouch_patient_orientation_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_PATIENT_ORIENTATIONS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_lifetouch_patient_orientation_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.LIFETEMP_TEMPERATURE_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case LIFETEMP_TEMPERATURE_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).getAndroidDeviceSessionId(), QueryType.LIFETEMP_TEMPERATURE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_lifetemp_temperature_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.LIFETEMP_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case LIFETEMP_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_lifetemp_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_lifetemp_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).getAndroidDeviceSessionId(), QueryType.LIFETEMP_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_lifetemp_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.PULSE_OX_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case PULSE_OX_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), QueryType.PULSE_OX_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.PULSE_OX_INTERMEDIATE_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case PULSE_OX_INTERMEDIATE_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), QueryType.PULSE_OX_INTERMEDIATE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.PULSE_OX_SETUP_MODE, is_recurrence_call))
                {
                    break;
                }
            }

            case PULSE_OX_SETUP_MODE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_pulse_ox_setup_mode_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_pulse_ox_setup_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), QueryType.PULSE_OX_SETUP_MODE_SAMPLES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_pulse_ox_setup_mode_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.PULSE_OX_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case PULSE_OX_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), QueryType.PULSE_OX_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_pulse_ox_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.BLOOD_PRESSURE_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case BLOOD_PRESSURE_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_blood_pressure_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_blood_pressure_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).getAndroidDeviceSessionId(), QueryType.BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_blood_pressure_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.BLOOD_PRESSURE_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case BLOOD_PRESSURE_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).getAndroidDeviceSessionId(), QueryType.BLOOD_PRESSURE_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_blood_pressure_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.WEIGHT_SCALE_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case WEIGHT_SCALE_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_weight_scale_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_weight_scale_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE).getAndroidDeviceSessionId(), QueryType.WEIGHT_SCALE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_weight_scale_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.WEIGHT_SCALE_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case WEIGHT_SCALE_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_weight_scale_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_weight_scale_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE).getAndroidDeviceSessionId(), QueryType.WEIGHT_SCALE_BATTERY_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_weight_scale_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_HEART_RATES, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_HEART_RATES:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_heart_rate_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_heart_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_HEART_RATES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_heart_rate_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_RESPIRATION_RATES, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_RESPIRATION_RATES:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_respiration_rate_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_respiration_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_RESPIRATION_RATES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_respiration_rate_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_TEMPERATURE_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_TEMPERATURE_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_temperature_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_temperature_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_TEMPERATURES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_temperature_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_OXIMETER_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_OXIMETER_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_spo2_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_spo2_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_SPO2_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_spo2_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_blood_pressure_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_blood_pressure_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_blood_pressure_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_weight_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_weight_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_weight_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_consciousness_level_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_consciousness_level_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_consciousness_level_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_ANNOTATIONS, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_ANNOTATIONS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_annotation_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_annotation_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_ANNOTATIONS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_annotation_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_capillary_refill_time_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_capillary_refill_time_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_capillary_refill_time_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_RESPIRATION_DISTRESS, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_respiration_distress_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_respiration_distress_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_RESPIRATION_DISTRESS, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_respiration_distress_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_family_or_nurse_concern_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_family_or_nurse_concern_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_family_or_nurse_concern_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.MANUALLY_ENTERED_URINE_OUTPUT, is_recurrence_call))
                {
                    break;
                }
            }

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_manually_entered_urine_output_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_manually_entered_urine_output_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_URINE_OUTPUT, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_manually_entered_urine_output_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.EARLY_WARNING_SCORES, is_recurrence_call))
                {
                    break;
                }
            }

            case EARLY_WARNING_SCORES:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_early_warning_score_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_early_warning_score_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).getAndroidDeviceSessionId(), QueryType.EARLY_WARNING_SCORES, ActiveOrOldSession.ACTIVE_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_early_warning_score_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.SETUP_MODE_LOGS, is_recurrence_call))
                {
                    break;
                }
            }

            case SETUP_MODE_LOGS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.active_session_setup_mode_log_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.active_session_setup_mode_log_rows_pending = local_database_storage.checkIfActiveSessionSetupModeLogsPendingToBeSentToServer();
                }

                if(getNextDataTypeToUpload(server_sync_status.active_session_setup_mode_log_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.AUDITABLE_EVENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case AUDITABLE_EVENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.auditable_events_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.auditable_events_rows_pending = local_database_storage.checkIfAuditableEventsPendingToBeSentToServer();
                }

                if(getNextDataTypeToUpload(server_sync_status.auditable_events_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.FULLY_SYNCED_SESSIONS, is_recurrence_call))
                {
                    break;
                }
            }

            case FULLY_SYNCED_SESSIONS:
            {
                server_sync_status.patient_session_fully_synced = local_database_storage.checkIfPatientSessionFullySyncedRecordsPendingToBeSentToServer();

                if(getNextDataTypeToUpload(server_sync_status.patient_session_fully_synced.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_CONNECTION_EVENT, is_recurrence_call))
                {
                    break;
                }
            }

            // Historical data pending
            case HISTORICAL_CONNECTION_EVENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_connection_event_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_connection_event_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.CONNECTION_EVENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_connection_event_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_LIFETOUCH_HEART_RATE, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_LIFETOUCH_HEART_RATE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_lifetouch_heart_rate_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_lifetouch_heart_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_HEART_RATES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_lifetouch_heart_rate_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_LIFETOUCH_HEART_BEAT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_LIFETOUCH_HEART_BEAT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_lifetouch_heart_beat_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_lifetouch_heart_beat_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_HEART_BEATS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_lifetouch_heart_beat_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_LIFETOUCH_RESPIRATION_RATE, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_LIFETOUCH_RESPIRATION_RATE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_lifetouch_respiration_rate_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_lifetouch_respiration_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_RESPIRATION_RATES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_lifetouch_respiration_rate_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_LIFETOUCH_SETUP_MODE, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_LIFETOUCH_SETUP_MODE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_lifetouch_setup_mode_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_lifetouch_setup_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_SETUP_MODE_RAW_SAMPLES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_lifetouch_setup_mode_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_LIFETOUCH_RAW_ACCELEROMETER_MODE, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_LIFETOUCH_RAW_ACCELEROMETER_MODE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_lifetouch_raw_accelerometer_mode_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_LIFETOUCH_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_LIFETOUCH_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_lifetouch_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_lifetouch_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_lifetouch_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_LIFETOUCH_PATIENT_ORIENTATION, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_LIFETOUCH_PATIENT_ORIENTATION:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_lifetouch_patient_orientation_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_lifetouch_patient_orientation_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).getAndroidDeviceSessionId(), QueryType.LIFETOUCH_PATIENT_ORIENTATIONS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_lifetouch_patient_orientation_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_LIFETEMP_TEMPERATURE_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_LIFETEMP_TEMPERATURE_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).getAndroidDeviceSessionId(), QueryType.LIFETEMP_TEMPERATURE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_lifetemp_temperature_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_LIFETEMP_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_LIFETEMP_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_lifetemp_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_lifetemp_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).getAndroidDeviceSessionId(), QueryType.LIFETEMP_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_lifetemp_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_PULSE_OX_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_PULSE_OX_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), QueryType.PULSE_OX_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_pulse_ox_spo2_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_PULSE_OX_INTERMEDIATE_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_PULSE_OX_INTERMEDIATE_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), QueryType.PULSE_OX_INTERMEDIATE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_pulse_ox_intermediate_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_PULSE_OX_SETUP_MODE, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_PULSE_OX_SETUP_MODE:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_pulse_ox_setup_mode_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_pulse_ox_setup_mode_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), QueryType.PULSE_OX_SETUP_MODE_SAMPLES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_pulse_ox_setup_mode_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_PULSE_OX_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_PULSE_OX_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).getAndroidDeviceSessionId(), QueryType.PULSE_OX_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_pulse_ox_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_BLOOD_PRESSURE_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_BLOOD_PRESSURE_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_blood_pressure_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_blood_pressure_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).getAndroidDeviceSessionId(), QueryType.BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_blood_pressure_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_BLOOD_PRESSURE_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_BLOOD_PRESSURE_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).getAndroidDeviceSessionId(), QueryType.BLOOD_PRESSURE_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_blood_pressure_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_WEIGHT_SCALE_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_WEIGHT_SCALE_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_weight_scale_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_weight_scale_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE).getAndroidDeviceSessionId(), QueryType.WEIGHT_SCALE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_weight_scale_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_WEIGHT_SCALE_BATTERY_MEASUREMENT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_WEIGHT_SCALE_BATTERY_MEASUREMENT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_weight_scale_battery_measurement_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_weight_scale_battery_measurement_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE).getAndroidDeviceSessionId(), QueryType.WEIGHT_SCALE_BATTERY_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_weight_scale_battery_measurement_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_HEART_RATES, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_HEART_RATES:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_heart_rate_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_heart_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_HEART_RATES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_heart_rate_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_RESPIRATION_RATES, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_RESPIRATION_RATES:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_respiration_rate_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_respiration_rate_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_RESPIRATION_RATES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_respiration_rate_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_TEMPERATURE_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_TEMPERATURE_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_temperature_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_temperature_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_TEMPERATURES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_temperature_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_OXIMETER_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_OXIMETER_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_spo2_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_spo2_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_SPO2_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_spo2_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_blood_pressure_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_blood_pressure_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_blood_pressure_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_WEIGHT_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_weight_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_weight_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_weight_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_consciousness_level_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_consciousness_level_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_consciousness_level_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_supplemental_oxygen_level_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_ANNOTATIONS, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_ANNOTATIONS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_annotation_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_annotation_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_ANNOTATIONS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_annotation_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_capillary_refill_time_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_capillary_refill_time_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_capillary_refill_time_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_RESPIRATION_DISTRESS, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_respiration_distress_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_respiration_distress_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_RESPIRATION_DISTRESS, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_respiration_distress_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_family_or_nurse_concern_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_family_or_nurse_concern_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_family_or_nurse_concern_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_MANUALLY_ENTERED_URINE_OUTPUT, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_MANUALLY_ENTERED_URINE_OUTPUT:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_manually_entered_urine_output_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_manually_entered_urine_output_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(patient_gateway_interface.getAndroidDatabasePatientSessionId(), QueryType.MANUALLY_ENTERED_URINE_OUTPUT, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_manually_entered_urine_output_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_EARLY_WARNING_SCORES, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_EARLY_WARNING_SCORES:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_early_warning_score_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_early_warning_score_rows_pending = local_database_storage.checkIfMoreSensorMeasurementPendingToBeSentToServer(device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).getAndroidDeviceSessionId(), QueryType.EARLY_WARNING_SCORES, ActiveOrOldSession.OLD_SESSION, GET_LIMIT_DATA_FROM_QUERY);
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_early_warning_score_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.HISTORICAL_SETUP_MODE_LOGS, is_recurrence_call))
                {
                    break;
                }
            }

            case HISTORICAL_SETUP_MODE_LOGS:
            {
                // This function just decides if there is ANY data to send for a particular type. It doesnt care how much. So only do a database read when necessary
                if (server_sync_status.old_session_setup_mode_log_rows_pending.rows_pending_syncable <= 0)
                {
                    server_sync_status.old_session_setup_mode_log_rows_pending = local_database_storage.checkIfHistoricalSessionSetupModeLogsPendingToBeSentToServer();
                }

                if(getNextDataTypeToUpload(server_sync_status.old_session_setup_mode_log_rows_pending.rows_pending_syncable, ServerSyncingDataUploadPoint.NO_MORE_DATA_TO_SYNC, is_recurrence_call))
                {
                    break;
                }
            }

            case NO_MORE_DATA_TO_SYNC:
            {
                if(!is_recurrence_call)
                {
                    entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;

                    Log.d(TAG, "setDataUploadPoint : Recursion for remaining data pend check.");

                    setDataUploadPoint(true);
                }
                else
                {
                    Log.d(TAG, "setDataUploadPoint : No need to sync more data");

                    local_database_storage.checkForFullySyncedHistoricalSessions();
                }
            }
            break;
        }

        // Previous upload point is important for recurrence. It is used to loop through the data.
        previous_entire_session_data_upload_start_point = entire_session_data_upload_start_point;
    }

    private ServerSyncAdaptorStatus getOperationStateBasedOnDataType(ServerSyncingDataUploadPoint session_data_type_sync)
    {
        switch (session_data_type_sync)
        {
            case START_PATIENT_SESSION:
            case START_DEVICE_SESSION:
            case END_DEVICE_SESSION:
            case END_PATIENT_SESSION:
            {
                return ServerSyncAdaptorStatus.CHECK_PATIENTS_SESSION_DATA_IN_SERVER;
            }

            case NO_MORE_DATA_TO_SYNC:
            {
                return ServerSyncAdaptorStatus.DATA_FULLY_SYNCED;
            }

            // Everything else
            default:
            {
                return ServerSyncAdaptorStatus.START_DATA_SYNC_TO_SERVER;
            }
        }
    }


    private void changeSyncAdaptorState(ServerSyncAdaptorStatus desired_state)
    {
        Log.d(TAG, "changeSyncAdaptorState : desired_state = " + desired_state.toString() + ". server_sync_adaptor_state " + server_sync_adaptor_state.toString());

        switch (desired_state)
        {
            case DATA_FULLY_SYNCED:
            {
                server_sync_adaptor_state = desired_state;
            }
            break;

            case START_DATA_SYNC_TO_SERVER:
            {
                // Check if previous data is being synced before starting another operation
                if(        (server_sync_adaptor_state == ServerSyncAdaptorStatus.DATA_FULLY_SYNCED)
                        || (server_sync_adaptor_state == ServerSyncAdaptorStatus.PATIENT_SESSION_SUCCESSFULLY_SYNCED_TO_SERVER)
                        || (server_sync_adaptor_state == ServerSyncAdaptorStatus.DATA_SUCCESSFULLY_SYNCED_TO_SERVER))
                {
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.START_DATA_SYNC_TO_SERVER;
                }
                else
                {
                    if((server_response_error_for_patient_or_device_session_data) || (!successful_http_post_to_server))
                    {
                        Log.w(TAG, "changeSyncAdaptorState : Error occurred in START_DATA_SYNC_TO_SERVER. Previous sync adaptor State = " + server_sync_adaptor_state.toString());

                        server_sync_adaptor_state = ServerSyncAdaptorStatus.START_DATA_SYNC_TO_SERVER;
                    }
                    else
                    {
                        Log.e(TAG, "changeSyncAdaptorState : ALERT ALERT!!! Current Server sync state isn't changed to START_DATA_SYNC_TO_SERVER. UNEXPECTED STATE == " + server_sync_adaptor_state.toString());
                    }
                }
            }
            break;

            // Current state is already syncing data to server. Wait till previous data is synced
            case DATA_BEING_SYNCED_TO_SERVER:
            {
                server_sync_adaptor_state =  ServerSyncAdaptorStatus.DATA_BEING_SYNCED_TO_SERVER;
            }
            break;

            case DATA_SUCCESSFULLY_SYNCED_TO_SERVER:
            {
                // Extra check before changing the state
                if(server_sync_adaptor_state == ServerSyncAdaptorStatus.DATA_BEING_SYNCED_TO_SERVER)
                {
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.DATA_SUCCESSFULLY_SYNCED_TO_SERVER;
                }
                else
                {
                    Log.e(TAG, "DATA_SUCCESSFULLY_SYNCED_TO_SERVER : But previous state is not DATA_BEING_SYNCED_TO_SERVER");
                }
            }
            break;

            // Patient session details are first checked with server and then written.
            case CHECK_PATIENTS_SESSION_DATA_IN_SERVER:
            {
                if(    (server_sync_adaptor_state == ServerSyncAdaptorStatus.DATA_FULLY_SYNCED)
                    || (server_sync_adaptor_state == ServerSyncAdaptorStatus.PATIENT_SESSION_SUCCESSFULLY_SYNCED_TO_SERVER)
                    || (server_sync_adaptor_state == ServerSyncAdaptorStatus.DATA_SUCCESSFULLY_SYNCED_TO_SERVER))
                {
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.CHECK_PATIENTS_SESSION_DATA_IN_SERVER;
                }
                else
                {
                    if((server_response_error_for_patient_or_device_session_data) || (!successful_http_post_to_server))
                    {
                        Log.d(TAG, "changeSyncAdaptorState : Error occurred in CHECK_PATIENTS_SESSION_DATA_IN_SERVER. Previous sync adaptor State = " + server_sync_adaptor_state.toString());

                        server_sync_adaptor_state = ServerSyncAdaptorStatus.CHECK_PATIENTS_SESSION_DATA_IN_SERVER;
                    }
                    else
                    {
                        Log.e(TAG, "changeSyncAdaptorState : ALERT ALERT!!! Current Server sync state isn't changed to CHECK_PATIENTS_SESSION_DATA_IN_SERVER. UNEXPECTED STATE == " + server_sync_adaptor_state.toString());
                    }
                }
            }
            break;

            // Waiting for server feedback before syncing another patient details
            case WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER:
            {
                if(server_sync_adaptor_state == ServerSyncAdaptorStatus.CHECK_PATIENTS_SESSION_DATA_IN_SERVER)
                {
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER;
                }
                else
                {
                    Log.d(TAG, "changeSyncAdaptorState : WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER. ERROR not expected state. server_sync_adaptor_state " + server_sync_adaptor_state.toString());
                }
            }
            break;

            case SEND_PATIENT_SESSION_DATA_TO_SERVER:
            {
                // Error happened while syncing data need to handle it.
                if((server_response_error_for_patient_or_device_session_data) || (!successful_http_post_to_server))
                {
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.CHECK_PATIENTS_SESSION_DATA_IN_SERVER;
                }
                else
                {
                    if(server_sync_adaptor_state == ServerSyncAdaptorStatus.WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER)
                    {
                        Log.w(TAG, "changeSyncAdaptorState : SEND_PATIENT_SESSION_DATA_TO_SERVER. Sending Patient session data. changed from server_sync_adaptor_state " + server_sync_adaptor_state.toString());

                        server_sync_adaptor_state = ServerSyncAdaptorStatus.SEND_PATIENT_SESSION_DATA_TO_SERVER;
                    }
                    else
                    {
                        Log.e(TAG, "changeSyncAdaptorState : ALERT ALERT. Current Server sync state isn't changed to SEND_PATIENT_SESSION_DATA_TO_SERVER. UNEXPECTED STATE == " + server_sync_adaptor_state.toString());
                    }
                }
            }
            break;

            case WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER:
            {
                if(server_sync_adaptor_state == ServerSyncAdaptorStatus.SEND_PATIENT_SESSION_DATA_TO_SERVER)
                {
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER;
                }
                else
                {
                    Log.d(TAG, "changeSyncAdaptorState : WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER. ERROR not expected state. server_sync_adaptor_state " + server_sync_adaptor_state.toString());
                }
            }
            break;

            case PATIENT_SESSION_SUCCESSFULLY_SYNCED_TO_SERVER:
            {
                // Extra check before changing the state
                if(server_sync_adaptor_state == ServerSyncAdaptorStatus.WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER)
                {
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.PATIENT_SESSION_SUCCESSFULLY_SYNCED_TO_SERVER;
                }
                else
                {
                    Log.e(TAG, "DATA_SUCCESSFULLY_SYNCED_TO_SERVER : but previous state is not DATA_BEING_SYNCED_TO_SERVER");
                }
            }
            break;
        }

        doOperationsDependingOnSyncAdaptorState();
    }


    private void doOperationsDependingOnSyncAdaptorState()
    {
        Log.d(TAG, "doOperationsDependingOnSyncAdaptorState : " + server_sync_adaptor_state.toString());

        switch (server_sync_adaptor_state)
        {
            case DATA_FULLY_SYNCED:
            case DATA_BEING_SYNCED_TO_SERVER:
            case WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER:
            case WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER:
            {
                // Nothing to do
            }
            break;

            case START_DATA_SYNC_TO_SERVER:
            {
                changeSyncAdaptorState(ServerSyncAdaptorStatus.DATA_BEING_SYNCED_TO_SERVER);

                updateDataToServer();
            }
            break;

            case DATA_SUCCESSFULLY_SYNCED_TO_SERVER:
            case PATIENT_SESSION_SUCCESSFULLY_SYNCED_TO_SERVER:
            {
                checkAndSendPendingData();
            }
            break;

            case CHECK_PATIENTS_SESSION_DATA_IN_SERVER:
            {
                changeSyncAdaptorState(ServerSyncAdaptorStatus.WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER);

                updateDataToServer();
            }
            break;

            case SEND_PATIENT_SESSION_DATA_TO_SERVER:
            {
                // Change the state to data being synced to server
                changeSyncAdaptorState(ServerSyncAdaptorStatus.WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER);

                updateDataToServer();
            }
            break;
        }
    }

    /**
     * boolean to check if periodic database deletion is in progress. Set true in  preExecution()
     * of "ASyncTaskToDeleteOldDataFromLocalDatabase" and set false in onPostExecution().
     * Separate instance of deletion progress "is_database_deletion_in_progress" is made to have
     * full control of Server Sync with same class. If deletion doesn't finish for
     */
    private volatile boolean is_database_deletion_in_progress = false;
    private final int NUMBER_OF_SECONDS_BEFORE_STARTING_SERVER_SYNC = 10 * (int)DateUtils.SECOND_IN_MILLIS;

    public void setDatabaseDeletionProgressStatus(boolean is_deletion_in_progress)
    {
        Log.d(TAG, "setDatabaseDeletionProgressStatus : is_database_deletion_in_progress = " + is_deletion_in_progress);

        is_database_deletion_in_progress = is_deletion_in_progress;
    }

    // Subclasses should override this method to handle content changes.
    // To ensure correct operation on older versions of the framework that did not provide a Uri argument, applications
    // should also implement the onChange(boolean) overload of this method whenever they implement the onChange(boolean, Uri) overload
    @Override
    public void onChange(boolean selfChange, Uri uri)
    {
        String uri_as_string = uri.toString();
        uri_as_string = uri_as_string.substring(uri_as_string.lastIndexOf('/') + 1);
        uri_as_string = Utils.padRight(uri_as_string, 35);

        String log_line = "onChange : URI = " + uri_as_string + " : " + Utils.padRight(server_sync_adaptor_state.toString(), 50);

        // Stage machines are already running and they will find this new data automatically.
        Log.d(TAG, "update DataType " + log_line );

        if( server_sync_adaptor_state == ServerSyncAdaptorStatus.DATA_FULLY_SYNCED )
        {
            // Kick off the state machines to start syncing the new data to the Server
            checkAndSendPendingData();
        }
    }


    // Implement the onChange(boolean) method to delegate the change notification to the onChange(boolean, Uri) method to ensure correct operation on older versions
    // of the framework that did not have the onChange(boolean, Uri) method.
    @Override
    public void onChange(boolean selfChange)
    {
        // Invoke the method signature available as of Android platform version 4.1, with a null URI.
        super.onChange(selfChange, null);
    }


    private int number_of_check_operating = 0;

    /**
     * Function to check if more data pending to send to the server and send it
     */
    private synchronized void checkAndSendPendingData()
    {
        number_of_check_operating++;

        Log.d(TAG, "checkAndSendPendingData : START number_of_check_operating " + number_of_check_operating);

        // First check if the server or WIFI has a problem before sending data
        checkServerSyncError();

        if(current_error_handler_state == ErrorState.NONE)
        {
            runServerSyncTask();
        }

        if(number_of_check_operating > 0)
        {
            number_of_check_operating--;
            Log.d(TAG, "checkAndSendPendingData : ENDING number_of_check_operating " + number_of_check_operating);
        }
    }


    private synchronized void runServerSyncTask()
    {
        if(number_of_server_sync_threads == 0)
        {
            Log.d(TAG, "runServerSyncTask : Executing updateDataToServerInBackground");

            number_of_server_sync_threads++;

            updateDataToServerInBackground();
        }
        else
        {
            Log.w(TAG, "runServerSyncTask : updateDataToServerInBackground is already RUNNING");
        }
    }


    /**
     * Called from the Server_Syncing class after data sync completions
     * @param http_post_successful Has the data got to the Server
     */
    public void checkAndChangeServerSyncStateFromServerPostResponse(boolean http_post_successful, ServerResponseErrorCodes server_http_post_error_code, boolean patient_session_data_successfully_synced)
    {
        successful_http_post_to_server = http_post_successful;

        last_http_post_error_code = server_http_post_error_code;

        server_response_error_for_patient_or_device_session_data = patient_session_data_successfully_synced;

        if(http_post_successful)
        {
            switch(server_sync_adaptor_state)
            {
                case DATA_BEING_SYNCED_TO_SERVER:
                {
                    Log.d(TAG, "checkAndChangeServerSyncStateFromServerPostResponse : Data written Successfully. current state = DATA_BEING_SYNCED_TO_SERVER");

                    changeSyncAdaptorState(ServerSyncAdaptorStatus.DATA_SUCCESSFULLY_SYNCED_TO_SERVER);
                }
                break;

                case WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER:
                {
                    Log.d(TAG, "checkAndChangeServerSyncStateFromServerPostResponse : Check patient session successful. current state = WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER");

                    changeSyncAdaptorState(ServerSyncAdaptorStatus.SEND_PATIENT_SESSION_DATA_TO_SERVER);
                }
                break;

                case WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER:
                {
                    Log.d(TAG, "checkAndChangeServerSyncStateFromServerPostResponse : Patient session data successfully synced to server. Current session = WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER");

                    changeSyncAdaptorState(ServerSyncAdaptorStatus.PATIENT_SESSION_SUCCESSFULLY_SYNCED_TO_SERVER);
                }
                break;

                case DATA_FULLY_SYNCED:
                case START_DATA_SYNC_TO_SERVER:
                case DATA_SUCCESSFULLY_SYNCED_TO_SERVER:
                case CHECK_PATIENTS_SESSION_DATA_IN_SERVER:
                case SEND_PATIENT_SESSION_DATA_TO_SERVER:
                case PATIENT_SESSION_SUCCESSFULLY_SYNCED_TO_SERVER:
                default:
                {
                    Log.d(TAG, "ALERT ALERT!!! Unexpected sync adaptor state. Current adaptor state = " + server_sync_adaptor_state.toString());
                }
            }
        }
        else
        {
            // Check the patient session information upload point and move to the next one.

            switch (server_sync_adaptor_state)
            {
                case DATA_BEING_SYNCED_TO_SERVER:
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.START_DATA_SYNC_TO_SERVER;
                    break;

                case WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER:
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.CHECK_PATIENTS_SESSION_DATA_IN_SERVER;
                    break;

                case WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER:
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.SEND_PATIENT_SESSION_DATA_TO_SERVER;
                    break;

                default:
                    Log.e(TAG, "checkAndChangeServerSyncStateFromServerPostResponse : Unknown data type synced to server. ALERT ALERT !!!");
                    break;
            }

            Log.e(TAG, "checkAndChangeServerSyncStateFromServerPostResponse : Data written failed. Changing the current state to " + server_sync_adaptor_state.toString());

            checkAndSendPendingData();
        }
    }


    /**
     * Function to start syncing data to server
     */
    private void updateDataToServer()
    {
        if ((settings.getServerSyncEnabledStatus()) && (settings.isServerAddressSet()) && (patient_gateway_interface.isConnectedToNetwork()))
        {
            if (!(server_syncing.isServerLinkBusy()))
            {
                Log.d(TAG, "updateDataToServer : Starting server syncing : " + entire_session_data_upload_start_point);

                server_syncing.checkIfMoreDataPendingToBeSentToServerAndSendIt(entire_session_data_upload_start_point);
            }
            else
            {
                Log.d(TAG, "updateDataToServer : server_interface_busy = True");

                // Checking device details and webservice Ping can make the server_interface_busy. If so then
                // retry syncing.
                checkAndSendPendingData();
            }
        }
        else
        {
            Log.d(TAG, "updateDataToServer : Server Link disabled / Server Address not setup / Not connected to WIFI");

            // Check the patient session information upload point and move to the next one.
            switch (server_sync_adaptor_state)
            {
                case DATA_BEING_SYNCED_TO_SERVER:
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.START_DATA_SYNC_TO_SERVER;
                    break;

                case WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER:
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.CHECK_PATIENTS_SESSION_DATA_IN_SERVER;
                    break;

                case WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER:
                    server_sync_adaptor_state = ServerSyncAdaptorStatus.SEND_PATIENT_SESSION_DATA_TO_SERVER;
                    break;

                default:
                    Log.e(TAG, "checkAndChangeServerSyncStateFromServerPostResponse : Unknown data type synced to server. ALERT ALERT !!!");
                    break;
            }

            // Need to trigger the state machine after connection is established.
            checkAndSendPendingData();
        }
    }

    private int number_of_server_sync_threads = 0;


    private void updateDataToServerInBackground()
    {
        sync_executor.execute(() -> {

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                Log.d(TAG, "updateDataToServerInBackground : Start : Number of Threads = " + number_of_server_sync_threads);
            });

            // Long running operation
            setDataUploadPoint(false);

            // Based on the type of data to be synced, set state machine operation point.
            ServerSyncAdaptorStatus server_sync_adaptor_status = getOperationStateBasedOnDataType(entire_session_data_upload_start_point);

            Log.w(TAG, "updateDataToServerInBackground : Current data type upload point = " + entire_session_data_upload_start_point + ".  server_sync_adaptor_status = " + server_sync_adaptor_status.toString());

            // update the adaptor state based on the previous state
            changeSyncAdaptorState(server_sync_adaptor_status);

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {

                if(number_of_server_sync_threads > 0)
                {
                    number_of_server_sync_threads--;
                }

                Log.d(TAG, "updateDataToServerInBackground : End : Number of Threads = " + number_of_server_sync_threads);
            });
        });
    }


    private enum ErrorState
    {
        NONE,

        // WIFI/GSM connection issue
        SERVER_SYNCING_DISABLED_OR_NO_NETWORK_CONNECTION,

        // Http post is waiting for response
        SERVER_SYNC_BUSY,

        // Server related error
        SERVER_DATA_SYNC_ERROR_HANDLER,

        // Server acknowledged wrong patient data being send
        PATIENT_SESSION_SYNC_ERROR_HANDLER,

        // Database deletion caused onChange execution. Handle this state as Error as unexpected trigger
        DATABASE_DELETION_IN_PROGRESS_HANDLER,
    }


    private ErrorState current_error_handler_state = ErrorState.NONE;

    /**
     * boolean : True if HTTP post is successful
     */
    private volatile boolean successful_http_post_to_server = true;
    private volatile ServerResponseErrorCodes last_http_post_error_code = ServerResponseErrorCodes.NONE;

    /**
     * boolean : True if server response for Session Information data is Invalid.
     */
    private volatile boolean server_response_error_for_patient_or_device_session_data = false;


    /**
     * Function to determine the Error status. Function checks the Settings and HTTP post return value to determine the error
     */
    private void checkServerSyncError()
    {
        if((!settings.getServerSyncEnabledStatus()) || (!settings.isServerAddressSet()) || (!patient_gateway_interface.isConnectedToNetwork()))
        {
            // Will start a new timer running which will call this check again after it fires.
            processError(ErrorState.SERVER_SYNCING_DISABLED_OR_NO_NETWORK_CONNECTION);
        }
        else if(server_syncing.isServerLinkBusy())
        {
            // Will start a new timer running which will call this check again after it fires.
            processError(ErrorState.SERVER_SYNC_BUSY);
        }
        else if((!successful_http_post_to_server) || (last_http_post_error_code != ServerResponseErrorCodes.NONE))
        {
            // Error related to Server side.
            processError(ErrorState.SERVER_DATA_SYNC_ERROR_HANDLER);

        }
        else if(server_response_error_for_patient_or_device_session_data)
        {
            // Server response for current patient session data is invalid
            processError(ErrorState.PATIENT_SESSION_SYNC_ERROR_HANDLER);
        }
        else if(is_database_deletion_in_progress)
        {
            processError(ErrorState.DATABASE_DELETION_IN_PROGRESS_HANDLER);
        }
        else
        {
            // Successful sync of data to server
            processError(ErrorState.NONE);
        }
    }


    /**
     * Function to change current error. Called before checking pending data and sending them.
     */
    private void processError(ErrorState error)
    {
        // Need this switch statement because there might be a case of multiple failures.
        // For multiple failure, check the previous state and then change the current state

        Log.d(TAG, "processError : current_error_handler_state = " + error + ". Changed from " + current_error_handler_state.toString());

        current_error_handler_state = error;

        switch (current_error_handler_state)
        {
            case NONE:
            {
                stopServerSyncTimer();
            }
            break;

            case SERVER_SYNCING_DISABLED_OR_NO_NETWORK_CONNECTION:
            {
                // Reset the adaptor state to fully synced
                server_sync_adaptor_state = ServerSyncAdaptorStatus.DATA_FULLY_SYNCED;

                entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;
                previous_entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;

                // Reset the http_post and error_code to default value
                successful_http_post_to_server = true;
                last_http_post_error_code = ServerResponseErrorCodes.NONE;
                server_response_error_for_patient_or_device_session_data = false;

                // Open timer and check if Gateway is connected back
                startServerSyncTimer(CONNECTION_ERROR_RETRY_TIME);
            }
            break;

            case SERVER_SYNC_BUSY:
            {
                // Reset the adaptor state to fully synced
                server_sync_adaptor_state = ServerSyncAdaptorStatus.DATA_FULLY_SYNCED;

                // Reset the error state to NONE
                current_error_handler_state = ErrorState.NONE;

                // Reset the http_post and error_code to default value
                successful_http_post_to_server = true;
                last_http_post_error_code = ServerResponseErrorCodes.NONE;
                server_response_error_for_patient_or_device_session_data = false;

                // Wait until server is ready
                startServerSyncTimer(SERVER_BUSY_RETRY_TIME);
            }
            break;

            case SERVER_DATA_SYNC_ERROR_HANDLER:
            {
                switch (last_http_post_error_code)
                {
                    /* Note that SERVER_UNREACHABLE CLIENT_PROTOCOL_EXCEPTION and NOT_ACCEPTABLE
                       are not ever generated in code (SERVER_UNREACHABLE and CLIENT_PROTOCOL_EXCEPTION
                       previously were in the apache http code, but the new okhttp code generates IO_EXCEPTION instead)
                     */
                    case SERVER_UNREACHABLE:
                    case IO_EXCEPTION:
                    case MISC_ERROR:
                    case UNAUTHORIZED:
                    {
                        // Wait for "SERVER_UNREACHABLE_RETRY_TIME" for another sync procedure to execute
                        startServerSyncTimerForServerError(SERVER_UNREACHABLE_RETRY_TIME);
                    }
                    break;

                    case CLIENT_PROTOCOL_EXCEPTION:
                    case NOT_ACCEPTABLE:
                    {
                        // Post another data without delay
                        startServerSyncTimerForServerError(0);
                    }
                    break;

                    case NONE:
                    {
                        Log.d(TAG, "processError : SERVER_DATA_SYNC_ERROR_HANDLER. Error is none.");
                    }
                    break;

                    default:
                        Log.d(TAG, "processError : SERVER_DATA_SYNC_ERROR_HANDLER. Unhandled Error ALERT ALERT!!!!!");
                }

                Log.d(TAG, "processError : SERVER_DATA_SYNC_ERROR_HANDLER. current server post error code = " + last_http_post_error_code.toString());
            }
            break;

            case PATIENT_SESSION_SYNC_ERROR_HANDLER:
            {
                // Send other session data or try after few second
                patientSessionErrorHandler();
            }
            break;

            case DATABASE_DELETION_IN_PROGRESS_HANDLER:
            {
                startServerSyncTimer(NUMBER_OF_SECONDS_BEFORE_STARTING_SERVER_SYNC);
            }
            break;
        }
    }


    /**
     * Timer : Server sync timer to check if problem is resolved
     */
    private Timer server_sync_timer = new Timer("server_sync_timer");

    /**
     * Number of ms wait till for checking Server status after server unreachable error
     */
    private final int SERVER_UNREACHABLE_RETRY_TIME = 10 * (int)DateUtils.SECOND_IN_MILLIS;

    /**
     * Number of ms wait till for checking Server status after wifi disconnected, server address not set or server link disabled
     */
    private final int CONNECTION_ERROR_RETRY_TIME = 10 * (int)DateUtils.SECOND_IN_MILLIS;

    /**
     * Number of ms wait till for checking Server status when ServerSync is already in progress. Should be greater than 20sec because Connection timeout is 20sec
     */
    private final int SERVER_BUSY_RETRY_TIME = 10 * (int)DateUtils.SECOND_IN_MILLIS;


    /**
     * Stop previously running Server Sync timer and Start new one
     */
    private void startServerSyncTimer(int initial_delay)
    {
        Log.d(TAG, "startServerSyncTimer: Starting Server Sync timer ");

        GenericStartStopTimer.cancelTimer(server_sync_timer, Log.getLog());

        server_sync_timer.purge();

        server_sync_timer = new Timer("server_sync_timer");

        server_sync_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "startServerSyncTimer : Server sync timer fired. Current error status = " + current_error_handler_state.toString());

                checkAndSendPendingData();
            }
        }, initial_delay);
    }


    /**
     * Stop previously running Server Sync timer and Start new one
     */
    private void resetStateAndStartServerSyncTimer(int initial_delay)
    {
        Log.d(TAG, "startServerSyncTimer: Starting Server Sync timer ");

        GenericStartStopTimer.cancelTimer(server_sync_timer, Log.getLog());

        server_sync_timer.purge();

        server_sync_timer = new Timer("server_sync_timer");

        server_sync_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "resetStateAndStartServerSyncTimer : Server sync timer fired. Current error status = " + current_error_handler_state.toString());

                // Reset the error state to NONE
                current_error_handler_state = ErrorState.NONE;

                entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;
                previous_entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;

                // Reset the http_post and error_code to default value
                successful_http_post_to_server = true;
                last_http_post_error_code = ServerResponseErrorCodes.NONE;
                server_response_error_for_patient_or_device_session_data = false;

                // Reset the adaptor state to fully synced
                server_sync_adaptor_state = ServerSyncAdaptorStatus.DATA_FULLY_SYNCED;

                checkAndSendPendingData();
            }
        }, initial_delay);
    }

    /**
     * Server Sync error happened. Set the timer for specified time.
     */
    private void startServerSyncTimerForServerError(int initial_delay)
    {
        Log.d(TAG, "startServerSyncTimerForServerError: Starting Server Sync timer");

        GenericStartStopTimer.cancelTimer(server_sync_timer, Log.getLog());

        server_sync_timer.purge();

        server_sync_timer = new Timer("server_sync_timer");

        server_sync_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "startServerSyncTimerForServerError : Server sync timer fired. Current error status = " + current_error_handler_state.toString());

                runServerSyncTask();
            }
        }, initial_delay);
    }

    /**
     * Stop previously running Server Sync timer and non-periodic timer
     */
    private void startServerSyncStateMachine(int execution_delay)
    {
        Log.d(TAG, "Starting Server Sync timer");

        GenericStartStopTimer.cancelTimer(server_sync_timer, Log.getLog());

        server_sync_timer.purge();

        server_sync_timer = new Timer("server_sync_timer");

        server_sync_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Periodic Server sync timer fired.");

                if((server_sync_adaptor_state == ServerSyncAdaptorStatus.DATA_FULLY_SYNCED) && (current_error_handler_state == ErrorState.NONE))
                {
                    checkAndSendPendingData();
                }
                else
                {
                    Log.d(TAG, "Periodic timer triggered but data is being to server. Current sync state = " + server_sync_adaptor_state.toString());
                }
            }
        }, execution_delay);
    }


    /**
     * If one of the Patient Session data is failed to send to the server then move to next data upload point without delay
     */
    private void patientSessionErrorHandler()
    {
        Log.d(TAG, "Starting Server Sync timer for Patient Session Error");

        GenericStartStopTimer.cancelTimer(server_sync_timer, Log.getLog());

        server_sync_timer.purge();

        runServerSyncTask();
    }


    /**
     * Stop current Server sync timer. Function is called when no error happens
     */
    private void stopServerSyncTimer()
    {
        Log.d(TAG, "Stopping Server Sync timer");
        GenericStartStopTimer.cancelTimer(server_sync_timer, Log.getLog());
    }

    /**
     *  Function to re-initiate the sync state machine
     */
    public void resetServerSyncStatusAndTriggerSync()
    {
        Log.d(TAG, "resetServerSyncStatusAndTriggerSync : Externally starting server sync state machine.");

        // Reset the adaptor state to fully synced
        server_sync_adaptor_state = ServerSyncAdaptorStatus.DATA_FULLY_SYNCED;

        // Reset the error state to NONE
        current_error_handler_state = ErrorState.NONE;

        entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;
        previous_entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;

        // Reset the http_post and error_code to default value
        successful_http_post_to_server = true;
        last_http_post_error_code = ServerResponseErrorCodes.NONE;
        server_response_error_for_patient_or_device_session_data = false;

        // Trigger the sync procedure
        startServerSyncStateMachine(SERVER_UNREACHABLE_RETRY_TIME);
    }


/*
    public void simulateStateMachineFailure()
    {
        Log.d(TAG, "simulateStateMachineFailure : Simulating server sync state machine failure");

        // In this state, StateMachine is waiting for data to be upload to server.
        server_sync_adaptor_state =  ServerSyncAdaptorStatus.DATA_BEING_SYNCED_TO_SERVER;

        // Reset the error state to NONE
        current_error_handler_state = ErrorState.NONE;

        changeSyncAdaptorState(server_sync_adaptor_state);
    }
*/


    public void pendingDataSizeZero_changeSyncAdaptorState(ServerSyncingDataUploadPoint currentDataUploadPoint)
    {
        // ALL the data size needed to be send to server is ZERO. So start the timer and set the server_sync_adaptor_state to "DATA_FULLY_SYNCED"

        server_sync_status.getRowsPending(currentDataUploadPoint).rows_pending_syncable = 0;

        switch (server_sync_adaptor_state)
        {
            case DATA_BEING_SYNCED_TO_SERVER:
                server_sync_adaptor_state = ServerSyncAdaptorStatus.START_DATA_SYNC_TO_SERVER;
                break;

            case WAITING_FOR_PATIENT_SESSION_FEEDBACK_FROM_SERVER:
                server_sync_adaptor_state = ServerSyncAdaptorStatus.CHECK_PATIENTS_SESSION_DATA_IN_SERVER;
                break;

            case WAITING_FOR_PATIENT_SESSION_SYNCED_TO_SERVER:
                server_sync_adaptor_state = ServerSyncAdaptorStatus.SEND_PATIENT_SESSION_DATA_TO_SERVER;
                break;

            default:
                Log.e(TAG, "pendingDataSizeZero_changeSyncAdaptorState : Unknown data type synced to server. ALERT ALERT !!!");
                break;
        }

        Log.d(TAG, "pendingDataSizeZero_changeSyncAdaptorState : Data written unsuccessful. Changing the current state to " + server_sync_adaptor_state.toString() + ".  Upload point is = " + currentDataUploadPoint);

        resetStateAndStartServerSyncTimer(SERVER_UNREACHABLE_RETRY_TIME);
    }


    public void restartServerSyncAfterResettingDataFailedToSend()
    {
        Log.d(TAG, "restartServerSyncAfterResettingDataFailedToSend : server_sync_adaptor_state = " + server_sync_adaptor_state);

        entire_session_data_upload_start_point = ServerSyncingDataUploadPoint.PATIENT_DETAILS_DATA;

        // If sync is not in progress then start the syncing
        if(server_sync_adaptor_state == ServerSyncAdaptorStatus.DATA_FULLY_SYNCED)
        {
            Log.d(TAG, "restartServerSyncAfterResettingDataFailedToSend : Starting Sync procedure after resetting all the failed_send data");

            // Kick off the state machines to start syncing the new data to the Server
            checkAndSendPendingData();
        }
    }

}

