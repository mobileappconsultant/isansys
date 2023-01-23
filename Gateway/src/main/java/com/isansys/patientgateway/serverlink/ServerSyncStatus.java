package com.isansys.patientgateway.serverlink;

import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.HttpOperationType;
import com.isansys.patientgateway.RowsPending;
import com.isansys.patientgateway.serverlink.constants.ServerSyncingDataUploadPoint;

public class ServerSyncStatus
{
    public volatile RowsPending patient_details;
    public volatile RowsPending device_info;
    public volatile RowsPending start_patient_session;
    public volatile RowsPending start_device_session;
    public volatile RowsPending end_device_session;
    public volatile RowsPending end_patient_session;

    public volatile RowsPending patient_session_fully_synced;

    public volatile RowsPending auditable_events_rows_pending;

    public volatile RowsPending active_session_connection_event_rows_pending;

    public volatile RowsPending active_session_lifetouch_heart_rate_rows_pending;
    public volatile RowsPending active_session_lifetouch_heart_beat_rows_pending;
    public volatile RowsPending active_session_lifetouch_respiration_rate_rows_pending;
    public volatile RowsPending active_session_lifetouch_setup_mode_rows_pending;
    public volatile RowsPending active_session_lifetouch_raw_accelerometer_mode_rows_pending;
    public volatile RowsPending active_session_lifetouch_battery_measurement_rows_pending;
    public volatile RowsPending active_session_lifetouch_patient_orientation_rows_pending;

    public volatile RowsPending active_session_lifetemp_temperature_measurement_rows_pending;
    public volatile RowsPending active_session_lifetemp_battery_measurement_rows_pending;

    public volatile RowsPending active_session_pulse_ox_spo2_measurement_rows_pending;
    public volatile RowsPending active_session_pulse_ox_intermediate_measurement_rows_pending;
    public volatile RowsPending active_session_pulse_ox_setup_mode_rows_pending;
    public volatile RowsPending active_session_pulse_ox_battery_measurement_rows_pending;

    public volatile RowsPending active_session_blood_pressure_measurement_rows_pending;
    public volatile RowsPending active_session_blood_pressure_battery_measurement_rows_pending;

    public volatile RowsPending active_session_weight_scale_measurement_rows_pending;
    public volatile RowsPending active_session_weight_scale_battery_measurement_rows_pending;

    public volatile RowsPending active_session_manually_entered_heart_rate_rows_pending;
    public volatile RowsPending active_session_manually_entered_respiration_rate_rows_pending;
    public volatile RowsPending active_session_manually_entered_temperature_rows_pending;
    public volatile RowsPending active_session_manually_entered_spo2_rows_pending;
    public volatile RowsPending active_session_manually_entered_blood_pressure_rows_pending;
    public volatile RowsPending active_session_manually_entered_weight_rows_pending;
    public volatile RowsPending active_session_manually_entered_consciousness_level_rows_pending;
    public volatile RowsPending active_session_manually_entered_supplemental_oxygen_level_rows_pending;
    public volatile RowsPending active_session_manually_entered_annotation_rows_pending;
    public volatile RowsPending active_session_manually_entered_capillary_refill_time_rows_pending;
    public volatile RowsPending active_session_manually_entered_respiration_distress_rows_pending;
    public volatile RowsPending active_session_manually_entered_family_or_nurse_concern_rows_pending;
    public volatile RowsPending active_session_manually_entered_urine_output_rows_pending;

    public volatile RowsPending active_session_early_warning_score_rows_pending;

    public volatile RowsPending active_session_setup_mode_log_rows_pending;

    public volatile RowsPending old_session_connection_event_rows_pending;

    public volatile RowsPending old_session_lifetouch_heart_rate_rows_pending;
    public volatile RowsPending old_session_lifetouch_heart_beat_rows_pending;
    public volatile RowsPending old_session_lifetouch_respiration_rate_rows_pending;
    public volatile RowsPending old_session_lifetouch_setup_mode_rows_pending;
    public volatile RowsPending old_session_lifetouch_raw_accelerometer_mode_rows_pending;
    public volatile RowsPending old_session_lifetouch_battery_measurement_rows_pending;
    public volatile RowsPending old_session_lifetouch_patient_orientation_rows_pending;

    public volatile RowsPending old_session_lifetemp_temperature_measurement_rows_pending;
    public volatile RowsPending old_session_lifetemp_battery_measurement_rows_pending;

    public volatile RowsPending old_session_pulse_ox_spo2_measurement_rows_pending;
    public volatile RowsPending old_session_pulse_ox_intermediate_measurement_rows_pending;
    public volatile RowsPending old_session_pulse_ox_setup_mode_rows_pending;
    public volatile RowsPending old_session_pulse_ox_battery_measurement_rows_pending;

    public volatile RowsPending old_session_blood_pressure_measurement_rows_pending;
    public volatile RowsPending old_session_blood_pressure_battery_measurement_rows_pending;

    public volatile RowsPending old_session_weight_scale_measurement_rows_pending;
    public volatile RowsPending old_session_weight_scale_battery_measurement_rows_pending;

    public volatile RowsPending old_session_manually_entered_heart_rate_rows_pending;
    public volatile RowsPending old_session_manually_entered_respiration_rate_rows_pending;
    public volatile RowsPending old_session_manually_entered_temperature_rows_pending;
    public volatile RowsPending old_session_manually_entered_spo2_rows_pending;
    public volatile RowsPending old_session_manually_entered_blood_pressure_rows_pending;
    public volatile RowsPending old_session_manually_entered_weight_rows_pending;
    public volatile RowsPending old_session_manually_entered_consciousness_level_rows_pending;
    public volatile RowsPending old_session_manually_entered_supplemental_oxygen_level_rows_pending;
    public volatile RowsPending old_session_manually_entered_annotation_rows_pending;
    public volatile RowsPending old_session_manually_entered_capillary_refill_time_rows_pending;
    public volatile RowsPending old_session_manually_entered_respiration_distress_rows_pending;
    public volatile RowsPending old_session_manually_entered_family_or_nurse_concern_rows_pending;
    public volatile RowsPending old_session_manually_entered_urine_output_rows_pending;

    public volatile RowsPending old_session_early_warning_score_rows_pending;

    public volatile RowsPending old_session_setup_mode_log_rows_pending;

    public ServerSyncStatus()
    {
        patient_details = new RowsPending();
        device_info = new RowsPending();
        start_patient_session = new RowsPending();
        start_device_session = new RowsPending();
        end_device_session = new RowsPending();
        end_patient_session = new RowsPending();
        active_session_connection_event_rows_pending = new RowsPending();
        old_session_connection_event_rows_pending = new RowsPending();
        patient_session_fully_synced = new RowsPending();

        active_session_lifetouch_heart_rate_rows_pending = new RowsPending();
        active_session_lifetouch_heart_beat_rows_pending = new RowsPending();
        active_session_lifetouch_respiration_rate_rows_pending = new RowsPending();
        active_session_lifetouch_setup_mode_rows_pending = new RowsPending();
        active_session_lifetouch_raw_accelerometer_mode_rows_pending = new RowsPending();
        active_session_lifetouch_battery_measurement_rows_pending = new RowsPending();
        active_session_lifetouch_patient_orientation_rows_pending = new RowsPending();
        active_session_lifetemp_temperature_measurement_rows_pending = new RowsPending();
        active_session_lifetemp_battery_measurement_rows_pending = new RowsPending();
        active_session_pulse_ox_spo2_measurement_rows_pending = new RowsPending();
        active_session_pulse_ox_intermediate_measurement_rows_pending = new RowsPending();
        active_session_pulse_ox_setup_mode_rows_pending = new RowsPending();
        active_session_pulse_ox_battery_measurement_rows_pending = new RowsPending();
        active_session_blood_pressure_measurement_rows_pending = new RowsPending();
        active_session_blood_pressure_battery_measurement_rows_pending = new RowsPending();
        active_session_weight_scale_measurement_rows_pending = new RowsPending();
        active_session_weight_scale_battery_measurement_rows_pending = new RowsPending();

        active_session_manually_entered_heart_rate_rows_pending = new RowsPending();
        active_session_manually_entered_respiration_rate_rows_pending = new RowsPending();
        active_session_manually_entered_temperature_rows_pending = new RowsPending();
        active_session_manually_entered_spo2_rows_pending = new RowsPending();
        active_session_manually_entered_blood_pressure_rows_pending = new RowsPending();
        active_session_manually_entered_weight_rows_pending = new RowsPending();
        active_session_manually_entered_consciousness_level_rows_pending = new RowsPending();
        active_session_manually_entered_supplemental_oxygen_level_rows_pending = new RowsPending();
        active_session_manually_entered_annotation_rows_pending = new RowsPending();
        active_session_manually_entered_capillary_refill_time_rows_pending = new RowsPending();
        active_session_manually_entered_respiration_distress_rows_pending = new RowsPending();
        active_session_manually_entered_family_or_nurse_concern_rows_pending = new RowsPending();
        active_session_manually_entered_urine_output_rows_pending = new RowsPending();
        
        active_session_early_warning_score_rows_pending = new RowsPending();
        active_session_setup_mode_log_rows_pending = new RowsPending();

        auditable_events_rows_pending = new RowsPending();

        old_session_lifetouch_heart_rate_rows_pending = new RowsPending();
        old_session_lifetouch_heart_beat_rows_pending = new RowsPending();
        old_session_lifetouch_respiration_rate_rows_pending = new RowsPending();
        old_session_lifetouch_setup_mode_rows_pending = new RowsPending();
        old_session_lifetouch_raw_accelerometer_mode_rows_pending = new RowsPending();
        old_session_lifetouch_battery_measurement_rows_pending = new RowsPending();
        old_session_lifetouch_patient_orientation_rows_pending = new RowsPending();
        old_session_lifetemp_temperature_measurement_rows_pending = new RowsPending();
        old_session_lifetemp_battery_measurement_rows_pending = new RowsPending();
        old_session_pulse_ox_spo2_measurement_rows_pending = new RowsPending();
        old_session_pulse_ox_intermediate_measurement_rows_pending = new RowsPending();
        old_session_pulse_ox_setup_mode_rows_pending = new RowsPending();
        old_session_pulse_ox_battery_measurement_rows_pending = new RowsPending();
        old_session_blood_pressure_measurement_rows_pending = new RowsPending();
        old_session_blood_pressure_battery_measurement_rows_pending = new RowsPending();
        old_session_weight_scale_measurement_rows_pending = new RowsPending();
        old_session_weight_scale_battery_measurement_rows_pending = new RowsPending();

        old_session_manually_entered_heart_rate_rows_pending = new RowsPending();
        old_session_manually_entered_respiration_rate_rows_pending = new RowsPending();
        old_session_manually_entered_temperature_rows_pending = new RowsPending();
        old_session_manually_entered_spo2_rows_pending = new RowsPending();
        old_session_manually_entered_blood_pressure_rows_pending = new RowsPending();
        old_session_manually_entered_weight_rows_pending = new RowsPending();
        old_session_manually_entered_consciousness_level_rows_pending = new RowsPending();
        old_session_manually_entered_supplemental_oxygen_level_rows_pending = new RowsPending();
        old_session_manually_entered_annotation_rows_pending = new RowsPending();
        old_session_manually_entered_capillary_refill_time_rows_pending = new RowsPending();
        old_session_manually_entered_respiration_distress_rows_pending = new RowsPending();
        old_session_manually_entered_family_or_nurse_concern_rows_pending = new RowsPending();
        old_session_manually_entered_urine_output_rows_pending = new RowsPending();

        old_session_early_warning_score_rows_pending = new RowsPending();
        old_session_setup_mode_log_rows_pending = new RowsPending();
    }


    public ServerSyncStatus(int starting_value)
    {
        patient_details = new RowsPending(starting_value++, starting_value++, starting_value++);
        device_info = new RowsPending(starting_value++, starting_value++, starting_value++);

        start_patient_session = new RowsPending(starting_value++, starting_value++, starting_value++);
        end_patient_session = new RowsPending(starting_value++, starting_value++, starting_value++);

        start_device_session = new RowsPending(starting_value++, starting_value++, starting_value++);
        end_device_session = new RowsPending(starting_value++, starting_value++, starting_value++);

        auditable_events_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        patient_session_fully_synced = new RowsPending(starting_value++, starting_value++, starting_value++);

        active_session_lifetouch_heart_rate_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_lifetouch_heart_beat_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_lifetouch_respiration_rate_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_lifetouch_setup_mode_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_lifetouch_raw_accelerometer_mode_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_lifetouch_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_lifetouch_patient_orientation_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        active_session_lifetemp_temperature_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_lifetemp_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        active_session_pulse_ox_spo2_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_pulse_ox_intermediate_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_pulse_ox_setup_mode_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_pulse_ox_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        active_session_blood_pressure_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_blood_pressure_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        active_session_weight_scale_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_weight_scale_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        active_session_manually_entered_heart_rate_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_respiration_rate_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_temperature_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_spo2_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_blood_pressure_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_weight_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_consciousness_level_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_supplemental_oxygen_level_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_annotation_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_capillary_refill_time_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_respiration_distress_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_family_or_nurse_concern_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_manually_entered_urine_output_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        active_session_early_warning_score_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_setup_mode_log_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        active_session_connection_event_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);


        old_session_lifetouch_heart_rate_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_lifetouch_heart_beat_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_lifetouch_respiration_rate_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_lifetouch_setup_mode_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_lifetouch_raw_accelerometer_mode_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_lifetouch_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_lifetouch_patient_orientation_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        old_session_lifetemp_temperature_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_lifetemp_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        old_session_pulse_ox_spo2_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_pulse_ox_intermediate_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_pulse_ox_setup_mode_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_pulse_ox_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        old_session_blood_pressure_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_blood_pressure_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        old_session_weight_scale_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_weight_scale_battery_measurement_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        old_session_manually_entered_heart_rate_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_respiration_rate_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_temperature_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_spo2_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_blood_pressure_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_weight_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_consciousness_level_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_supplemental_oxygen_level_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_annotation_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_capillary_refill_time_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_respiration_distress_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_family_or_nurse_concern_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_manually_entered_urine_output_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);

        old_session_early_warning_score_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_setup_mode_log_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value++);
        old_session_connection_event_rows_pending = new RowsPending(starting_value++, starting_value++, starting_value);
    }


    public RowsPending getRowsPending(HttpOperationType http_operation_type)
    {
       return getRowsPending(http_operation_type, ActiveOrOldSession.INVALID);
    }


    public RowsPending getRowsPending(HttpOperationType http_operation_type, ActiveOrOldSession activeOrOldSession)
    {
        switch (http_operation_type)
        {
            case PATIENT_DETAILS:
                return patient_details;

            case START_PATIENT_SESSION:
                return start_patient_session;

            case START_DEVICE_SESSION:
                return start_device_session;

            case END_DEVICE_SESSION:
                return end_device_session;

            case END_PATIENT_SESSION:
                return end_patient_session;

            case PATIENT_SESSION_FULLY_SYNCED:
                return patient_session_fully_synced;

            case DEVICE_INFO:
                return device_info;

            case AUDITABLE_EVENTS:
                return auditable_events_rows_pending;

            case CONNECTION_EVENT:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_connection_event_rows_pending;
                else
                    return old_session_connection_event_rows_pending;

            case LIFETOUCH_HEART_RATE:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_lifetouch_heart_rate_rows_pending;
                else
                    return old_session_lifetouch_heart_rate_rows_pending;

            case LIFETOUCH_HEART_BEAT:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_lifetouch_heart_beat_rows_pending;
                else
                    return old_session_lifetouch_heart_beat_rows_pending;

            case LIFETOUCH_RESPIRATION_RATE:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_lifetouch_respiration_rate_rows_pending;
                else
                    return old_session_lifetouch_respiration_rate_rows_pending;

            case LIFETOUCH_SETUP_MODE_SAMPLE:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_lifetouch_setup_mode_rows_pending;
                else
                    return old_session_lifetouch_setup_mode_rows_pending;

            case LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_lifetouch_raw_accelerometer_mode_rows_pending;
                else
                    return old_session_lifetouch_raw_accelerometer_mode_rows_pending;

            case LIFETOUCH_BATTERY:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_lifetouch_battery_measurement_rows_pending;
                else
                    return old_session_lifetouch_battery_measurement_rows_pending;

            case LIFETOUCH_PATIENT_ORIENTATION:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_lifetouch_patient_orientation_rows_pending;
                else
                    return old_session_lifetouch_patient_orientation_rows_pending;

            case LIFETEMP_TEMPERATURE:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_lifetemp_temperature_measurement_rows_pending;
                else
                    return old_session_lifetemp_temperature_measurement_rows_pending;

            case LIFETEMP_BATTERY:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_lifetemp_battery_measurement_rows_pending;
                else
                    return old_session_lifetemp_battery_measurement_rows_pending;

            case PULSE_OX_MEASUREMENT:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_pulse_ox_spo2_measurement_rows_pending;
                else
                    return old_session_pulse_ox_spo2_measurement_rows_pending;

            case PULSE_OX_INTERMEDIATE_MEASUREMENT:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_pulse_ox_intermediate_measurement_rows_pending;
                else
                    return old_session_pulse_ox_intermediate_measurement_rows_pending;

            case PULSE_OX_SETUP_MODE_SAMPLE:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_pulse_ox_setup_mode_rows_pending;
                else
                    return old_session_pulse_ox_setup_mode_rows_pending;

            case PULSE_OX_BATTERY:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_pulse_ox_battery_measurement_rows_pending;
                else
                    return old_session_pulse_ox_battery_measurement_rows_pending;

            case BLOOD_PRESSURE_MEASUREMENT:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_blood_pressure_measurement_rows_pending;
                else
                    return old_session_blood_pressure_measurement_rows_pending;

            case BLOOD_PRESSURE_BATTERY:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_blood_pressure_battery_measurement_rows_pending;
                else
                    return old_session_blood_pressure_battery_measurement_rows_pending;

            case WEIGHT_SCALE_MEASUREMENT:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_weight_scale_measurement_rows_pending;
                else
                    return old_session_weight_scale_measurement_rows_pending;

            case WEIGHT_SCALE_BATTERY:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_weight_scale_battery_measurement_rows_pending;
                else
                    return old_session_weight_scale_battery_measurement_rows_pending;

            case MANUALLY_ENTERED_HEART_RATES:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_heart_rate_rows_pending;
                else
                    return old_session_manually_entered_heart_rate_rows_pending;

            case MANUALLY_ENTERED_RESPIRATION_RATES:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_respiration_rate_rows_pending;
                else
                    return old_session_manually_entered_respiration_rate_rows_pending;

            case MANUALLY_ENTERED_TEMPERATURES:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_temperature_rows_pending;
                else
                    return old_session_manually_entered_temperature_rows_pending;

            case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_spo2_rows_pending;
                else
                    return old_session_manually_entered_spo2_rows_pending;

            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_blood_pressure_rows_pending;
                else
                    return old_session_manually_entered_blood_pressure_rows_pending;

            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_weight_rows_pending;
                else
                    return old_session_manually_entered_weight_rows_pending;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_consciousness_level_rows_pending;
                else
                    return old_session_manually_entered_consciousness_level_rows_pending;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_supplemental_oxygen_level_rows_pending;
                else
                    return old_session_manually_entered_supplemental_oxygen_level_rows_pending;

            case MANUALLY_ENTERED_ANNOTATIONS:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_annotation_rows_pending;
                else
                    return old_session_manually_entered_annotation_rows_pending;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_capillary_refill_time_rows_pending;
                else
                    return old_session_manually_entered_capillary_refill_time_rows_pending;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_respiration_distress_rows_pending;
                else
                    return old_session_manually_entered_respiration_distress_rows_pending;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_family_or_nurse_concern_rows_pending;
                else
                    return old_session_manually_entered_family_or_nurse_concern_rows_pending;

            case MANUALLY_ENTERED_URINE_OUTPUT:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_manually_entered_urine_output_rows_pending;
                else
                    return old_session_manually_entered_urine_output_rows_pending;

            case EARLY_WARNING_SCORES:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_early_warning_score_rows_pending;
                else
                    return old_session_early_warning_score_rows_pending;

            case SETUP_MODE_LOG:
                if (activeOrOldSession == ActiveOrOldSession.ACTIVE_SESSION)
                    return active_session_setup_mode_log_rows_pending;
                else
                    return old_session_setup_mode_log_rows_pending;
        }

        return null;
    }


    public int getTotalRowsPending()
    {
        return getTotalRowsInSessionInfo() + getTotalRowsInActiveSession() + getTotalRowsInHistoricalSession();
    }


    public int getTotalRowsInSessionInfo()
    {
        int count = 0;
        count = count + patient_details.getTotalRowsPending();
        count = count + start_patient_session.getTotalRowsPending();
        count = count + start_device_session.getTotalRowsPending();
        count = count + end_device_session.getTotalRowsPending();
        count = count + end_patient_session.getTotalRowsPending();

// Do we care about Patient Session Fully Synced in database?
        count = count + patient_session_fully_synced.getTotalRowsPending();

        count = count + device_info.getTotalRowsPending();
        count = count + auditable_events_rows_pending.getTotalRowsPending();

        return count;
    }


    public int getTotalRowsInActiveSession()
    {
        int count = 0;
        count = count + active_session_connection_event_rows_pending.getTotalRowsPending();

        count = count + active_session_lifetouch_heart_rate_rows_pending.getTotalRowsPending();
        count = count + active_session_lifetouch_heart_beat_rows_pending.getTotalRowsPending();
        count = count + active_session_lifetouch_respiration_rate_rows_pending.getTotalRowsPending();
        count = count + active_session_lifetouch_setup_mode_rows_pending.getTotalRowsPending();
        count = count + active_session_lifetouch_raw_accelerometer_mode_rows_pending.getTotalRowsPending();
        count = count + active_session_lifetouch_battery_measurement_rows_pending.getTotalRowsPending();
        count = count + active_session_lifetouch_patient_orientation_rows_pending.getTotalRowsPending();

        count = count + active_session_lifetemp_temperature_measurement_rows_pending.getTotalRowsPending();
        count = count + active_session_lifetemp_battery_measurement_rows_pending.getTotalRowsPending();

        count = count + active_session_pulse_ox_spo2_measurement_rows_pending.getTotalRowsPending();
        count = count + active_session_pulse_ox_intermediate_measurement_rows_pending.getTotalRowsPending();
        count = count + active_session_pulse_ox_setup_mode_rows_pending.getTotalRowsPending();
        count = count + active_session_pulse_ox_battery_measurement_rows_pending.getTotalRowsPending();

        count = count + active_session_blood_pressure_measurement_rows_pending.getTotalRowsPending();
        count = count + active_session_blood_pressure_battery_measurement_rows_pending.getTotalRowsPending();

        count = count + active_session_weight_scale_measurement_rows_pending.getTotalRowsPending();
        count = count + active_session_weight_scale_battery_measurement_rows_pending.getTotalRowsPending();

        count = count + active_session_manually_entered_heart_rate_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_respiration_rate_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_temperature_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_spo2_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_blood_pressure_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_weight_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_consciousness_level_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_supplemental_oxygen_level_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_annotation_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_capillary_refill_time_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_respiration_distress_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_family_or_nurse_concern_rows_pending.getTotalRowsPending();
        count = count + active_session_manually_entered_urine_output_rows_pending.getTotalRowsPending();

        count = count + active_session_early_warning_score_rows_pending.getTotalRowsPending();

        count = count + active_session_setup_mode_log_rows_pending.getTotalRowsPending();

        return count;
    }


    public int getTotalRowsInHistoricalSession()
    {
        int count = 0;
        count = count + old_session_connection_event_rows_pending.getTotalRowsPending();

        count = count + old_session_lifetouch_heart_rate_rows_pending.getTotalRowsPending();
        count = count + old_session_lifetouch_heart_beat_rows_pending.getTotalRowsPending();
        count = count + old_session_lifetouch_respiration_rate_rows_pending.getTotalRowsPending();
        count = count + old_session_lifetouch_setup_mode_rows_pending.getTotalRowsPending();
        count = count + old_session_lifetouch_raw_accelerometer_mode_rows_pending.getTotalRowsPending();
        count = count + old_session_lifetouch_battery_measurement_rows_pending.getTotalRowsPending();
        count = count + old_session_lifetouch_patient_orientation_rows_pending.getTotalRowsPending();

        count = count + old_session_lifetemp_temperature_measurement_rows_pending.getTotalRowsPending();
        count = count + old_session_lifetemp_battery_measurement_rows_pending.getTotalRowsPending();

        count = count + old_session_pulse_ox_spo2_measurement_rows_pending.getTotalRowsPending();
        count = count + old_session_pulse_ox_intermediate_measurement_rows_pending.getTotalRowsPending();
        count = count + old_session_pulse_ox_setup_mode_rows_pending.getTotalRowsPending();
        count = count + old_session_pulse_ox_battery_measurement_rows_pending.getTotalRowsPending();

        count = count + old_session_blood_pressure_measurement_rows_pending.getTotalRowsPending();
        count = count + old_session_blood_pressure_battery_measurement_rows_pending.getTotalRowsPending();

        count = count + old_session_weight_scale_measurement_rows_pending.getTotalRowsPending();
        count = count + old_session_weight_scale_battery_measurement_rows_pending.getTotalRowsPending();

        count = count + old_session_manually_entered_heart_rate_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_respiration_rate_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_temperature_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_spo2_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_blood_pressure_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_weight_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_consciousness_level_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_supplemental_oxygen_level_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_annotation_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_capillary_refill_time_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_respiration_distress_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_family_or_nurse_concern_rows_pending.getTotalRowsPending();
        count = count + old_session_manually_entered_urine_output_rows_pending.getTotalRowsPending();

        count = count + old_session_early_warning_score_rows_pending.getTotalRowsPending();

        count = count + old_session_setup_mode_log_rows_pending.getTotalRowsPending();

        return count;
    }

    public RowsPending getRowsPending(ServerSyncingDataUploadPoint currentDataUploadPoint)
    {
        switch(currentDataUploadPoint)
        {
            case PATIENT_DETAILS_DATA:
                return patient_details;

            case DEVICE_INFO_DATA:
                return device_info;

            case START_PATIENT_SESSION:
                return start_patient_session;

            case START_DEVICE_SESSION:
                return start_device_session;

            case END_DEVICE_SESSION:
                return end_device_session;

            case END_PATIENT_SESSION:
                return end_patient_session;

            case CONNECTION_EVENT:
                return active_session_connection_event_rows_pending;

            case LIFETOUCH_HEART_RATE:
                return active_session_lifetouch_heart_rate_rows_pending;

            case LIFETOUCH_HEART_BEAT:
                return active_session_lifetouch_heart_beat_rows_pending;

            case LIFETOUCH_RESPIRATION_RATE:
                return active_session_lifetouch_respiration_rate_rows_pending;

            case LIFETOUCH_SETUP_MODE:
                return active_session_lifetouch_setup_mode_rows_pending;

            case LIFETOUCH_BATTERY_MEASUREMENT:
                return active_session_lifetouch_battery_measurement_rows_pending;

            case LIFETOUCH_PATIENT_ORIENTATION:
                return active_session_lifetouch_patient_orientation_rows_pending;

            case LIFETOUCH_RAW_ACCELEROMETER_MODE:
                return active_session_lifetouch_raw_accelerometer_mode_rows_pending;

            case LIFETEMP_TEMPERATURE_MEASUREMENT:
                return active_session_lifetemp_temperature_measurement_rows_pending;

            case LIFETEMP_BATTERY_MEASUREMENT:
                return active_session_lifetemp_battery_measurement_rows_pending;

            case PULSE_OX_MEASUREMENT:
                return active_session_pulse_ox_spo2_measurement_rows_pending;

            case PULSE_OX_INTERMEDIATE_MEASUREMENT:
                return active_session_pulse_ox_intermediate_measurement_rows_pending;

            case PULSE_OX_SETUP_MODE:
                return active_session_pulse_ox_setup_mode_rows_pending;

            case PULSE_OX_BATTERY_MEASUREMENT:
                return active_session_pulse_ox_battery_measurement_rows_pending;

            case BLOOD_PRESSURE_MEASUREMENT:
                return active_session_blood_pressure_measurement_rows_pending;

            case BLOOD_PRESSURE_BATTERY_MEASUREMENT:
                return active_session_blood_pressure_battery_measurement_rows_pending;

            case WEIGHT_SCALE_MEASUREMENT:
                return active_session_weight_scale_measurement_rows_pending;

            case WEIGHT_SCALE_BATTERY_MEASUREMENT:
                return active_session_weight_scale_battery_measurement_rows_pending;

            case MANUALLY_ENTERED_HEART_RATES:
                return active_session_manually_entered_heart_rate_rows_pending;

            case MANUALLY_ENTERED_RESPIRATION_RATES:
                return active_session_manually_entered_respiration_rate_rows_pending;

            case MANUALLY_ENTERED_TEMPERATURE_MEASUREMENTS:
                return active_session_manually_entered_temperature_rows_pending;

            case MANUALLY_ENTERED_OXIMETER_MEASUREMENTS:
                return active_session_manually_entered_spo2_rows_pending;

            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
                return active_session_manually_entered_blood_pressure_rows_pending;

            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
                return active_session_manually_entered_weight_rows_pending;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
                return active_session_manually_entered_consciousness_level_rows_pending;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
                return active_session_manually_entered_supplemental_oxygen_level_rows_pending;

            case MANUALLY_ENTERED_ANNOTATIONS:
                return active_session_manually_entered_annotation_rows_pending;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
                return active_session_manually_entered_capillary_refill_time_rows_pending;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                return active_session_manually_entered_respiration_distress_rows_pending;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                return active_session_manually_entered_family_or_nurse_concern_rows_pending;

            case MANUALLY_ENTERED_URINE_OUTPUT:
                return active_session_manually_entered_urine_output_rows_pending;

            case EARLY_WARNING_SCORES:
                return active_session_early_warning_score_rows_pending;

            case SETUP_MODE_LOGS:
                return active_session_setup_mode_log_rows_pending;

            case AUDITABLE_EVENTS:
                return auditable_events_rows_pending;

            case FULLY_SYNCED_SESSIONS:
                return patient_session_fully_synced;

            case HISTORICAL_CONNECTION_EVENT:
                return old_session_connection_event_rows_pending;

            case HISTORICAL_LIFETOUCH_HEART_RATE:
                return old_session_lifetouch_heart_rate_rows_pending;

            case HISTORICAL_LIFETOUCH_HEART_BEAT:
                return old_session_lifetouch_heart_beat_rows_pending;

            case HISTORICAL_LIFETOUCH_RESPIRATION_RATE:
                return old_session_lifetouch_respiration_rate_rows_pending;

            case HISTORICAL_LIFETOUCH_SETUP_MODE:
                return old_session_lifetouch_setup_mode_rows_pending;

            case HISTORICAL_LIFETOUCH_BATTERY_MEASUREMENT:
                return old_session_lifetouch_battery_measurement_rows_pending;

            case HISTORICAL_LIFETOUCH_PATIENT_ORIENTATION:
                return old_session_lifetouch_patient_orientation_rows_pending;

            case HISTORICAL_LIFETOUCH_RAW_ACCELEROMETER_MODE:
                return old_session_lifetouch_raw_accelerometer_mode_rows_pending;

            case HISTORICAL_LIFETEMP_TEMPERATURE_MEASUREMENT:
                return old_session_lifetemp_temperature_measurement_rows_pending;

            case HISTORICAL_LIFETEMP_BATTERY_MEASUREMENT:
                return old_session_lifetemp_battery_measurement_rows_pending;

            case HISTORICAL_PULSE_OX_MEASUREMENT:
                return old_session_pulse_ox_spo2_measurement_rows_pending;

            case HISTORICAL_PULSE_OX_INTERMEDIATE_MEASUREMENT:
                return old_session_pulse_ox_intermediate_measurement_rows_pending;

            case HISTORICAL_PULSE_OX_SETUP_MODE:
                return old_session_pulse_ox_setup_mode_rows_pending;

            case HISTORICAL_PULSE_OX_BATTERY_MEASUREMENT:
                return old_session_pulse_ox_battery_measurement_rows_pending;

            case HISTORICAL_BLOOD_PRESSURE_MEASUREMENT:
                return old_session_blood_pressure_measurement_rows_pending;

            case HISTORICAL_BLOOD_PRESSURE_BATTERY_MEASUREMENT:
                return old_session_blood_pressure_battery_measurement_rows_pending;

            case HISTORICAL_WEIGHT_SCALE_MEASUREMENT:
                return old_session_weight_scale_measurement_rows_pending;

            case HISTORICAL_WEIGHT_SCALE_BATTERY_MEASUREMENT:
                return old_session_weight_scale_battery_measurement_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_HEART_RATES:
                return old_session_manually_entered_heart_rate_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_RESPIRATION_RATES:
                return old_session_manually_entered_respiration_rate_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_TEMPERATURE_MEASUREMENTS:
                return old_session_manually_entered_temperature_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_OXIMETER_MEASUREMENTS:
                return old_session_manually_entered_spo2_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
                return old_session_manually_entered_blood_pressure_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
                return old_session_manually_entered_weight_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
                return old_session_manually_entered_consciousness_level_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
                return old_session_manually_entered_supplemental_oxygen_level_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_ANNOTATIONS:
                return old_session_manually_entered_annotation_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
                return old_session_manually_entered_capillary_refill_time_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                return old_session_manually_entered_respiration_distress_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                return old_session_manually_entered_family_or_nurse_concern_rows_pending;

            case HISTORICAL_MANUALLY_ENTERED_URINE_OUTPUT:
                return old_session_manually_entered_urine_output_rows_pending;

            case HISTORICAL_SETUP_MODE_LOGS:
                return old_session_setup_mode_log_rows_pending;

            case HISTORICAL_EARLY_WARNING_SCORES:
                return old_session_early_warning_score_rows_pending;

            case NO_MORE_DATA_TO_SYNC:
                return null; // should only hit this if we try to get rows pending for no more data - which is invalid. Current code cannot call this.
        }

        return null; // should only hit this if we add a new enum value but don't handle it in the switch
    }
}
