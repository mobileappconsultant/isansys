
package com.isansys.pse_isansysportal;

import android.net.Uri;
import android.provider.BaseColumns;

final class PatientGatewayDatabaseContract implements BaseColumns
{
    private static final String AUTHORITY = "com.isansys.patientgateway.database.contentprovider";
    
    private static Uri buildUpContentUri(String base_path)
    {
        return Uri.parse("content://" + AUTHORITY + "/" + base_path);
    }

    // Table common
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SERVERS_ID = "servers_id";
    public static final String COLUMN_PATIENT_SESSION_NUMBER = "patient_session_number";
    public static final String COLUMN_DEVICE_SESSION_NUMBER = "device_session_number";
    public static final String COLUMN_HUMAN_READABLE_DEVICE_ID = "human_readable_device_id";
    public static final String COLUMN_BY_USER_ID = "by_user_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS = "measurement_validity_time_in_seconds";
    public static final String COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP = "written_to_database_timestamp";
    public static final String COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED = "sent_to_server_and_server_acknowledged";
    public static final String COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP = "sent_to_server_and_server_acknowledged_timestamp";
    public static final String COLUMN_SENT_TO_SERVER_BUT_FAILED = "sent_to_server_but_failed";
    public static final String COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP = "sent_to_server_but_failed_timestamp";

    // Session details
    private static final String BASE_PATH_PATIENT_SESSION = "patient_sessions";
    public static final Uri CONTENT_URI_PATIENT_SESSION = buildUpContentUri(BASE_PATH_PATIENT_SESSION);

    public static final String COLUMN_PATIENT_SESSION__START_SESSION_TIME = "patient_start_session_time";
    
    // Lifetouch Heart Beats
    private static final String BASE_PATH_LIFETOUCH_HEART_BEATS = "lifetouch_heartbeats";
    public static final Uri CONTENT_URI_LIFETOUCH_HEART_BEATS = buildUpContentUri(BASE_PATH_LIFETOUCH_HEART_BEATS);

    public static final String COLUMN_LIFETOUCH_HEART_BEAT__AMPLITUDE = "amplitude";
    public static final String COLUMN_LIFETOUCH_HEART_BEAT__SEQUENCE_ID = "sequence_id";
    public static final String COLUMN_LIFETOUCH_HEART_BEAT__ACTIVITY_LEVEL = "activity_level";
    public static final String COLUMN_LIFETOUCH_HEART_BEAT__RR_INTERVAL = "rr_interval_in_ms";

    // Lifetouch Respiration Rate
    private static final String BASE_PATH_LIFETOUCH_RESPIRATION_RATES = "lifetouch_respiration_rates";
    public static final Uri CONTENT_URI_LIFETOUCH_RESPIRATION_RATES = buildUpContentUri(BASE_PATH_LIFETOUCH_RESPIRATION_RATES);
    
    public static final String COLUMN_LIFETOUCH_RESPIRATION_RATE__RESPIRATION_RATE = "respiration_rate";

    // Lifetouch Heart Rate
    private static final String BASE_PATH_LIFETOUCH_HEART_RATES = "lifetouch_heart_rates";
    public static final Uri CONTENT_URI_LIFETOUCH_HEART_RATES = buildUpContentUri(BASE_PATH_LIFETOUCH_HEART_RATES);

    public static final String COLUMN_LIFETOUCH_HEART_RATE__HEART_RATE = "heart_rate";

    // Lifetouch Setup Mode
    private static final String BASE_PATH_LIFETOUCH_SETUP_MODE_SAMPLES = "lifetouch_setup_mode_samples";
    public static final Uri CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES = buildUpContentUri(BASE_PATH_LIFETOUCH_SETUP_MODE_SAMPLES);

    public static final String COLUMN_LIFETOUCH_SETUP_MODE__SAMPLE_VALUE = "sample_value";

    
    // Pulse Ox
    private static final String BASE_PATH_OXIMETER_MEASUREMENTS= "oximeter_measurements";
    public static final Uri CONTENT_URI_OXIMETER_MEASUREMENTS = buildUpContentUri(BASE_PATH_OXIMETER_MEASUREMENTS);
    
    public static final String COLUMN_OXIMETER__PULSE = "pulse";
    public static final String COLUMN_OXIMETER__SPO2 = "spo2";

    private static final String BASE_PATH_OXIMETER_SETUP_MODE_SAMPLES = "oximeter_setup_mode_samples";
    public static final Uri CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES = buildUpContentUri(BASE_PATH_OXIMETER_SETUP_MODE_SAMPLES);
    public static final String COLUMN_OXIMETER_SETUP_MODE__SAMPLE_VALUE = "sample_value";


    // Temperature
    private static final String BASE_PATH_TEMPERATURE_MEASUREMENTS = "lifetemp_temperature_measurements";
    public static final Uri CONTENT_URI_TEMPERATURE_MEASUREMENTS = buildUpContentUri(BASE_PATH_TEMPERATURE_MEASUREMENTS);

    public static final String COLUMN_LIFETEMP__TEMPERATURE = "temperature";

    
    // Blood Pressure
    private static final String BASE_PATH_BLOOD_PRESSURE_MEASUREMENTS = "blood_pressure_measurements";
    public static final Uri CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS = buildUpContentUri(BASE_PATH_BLOOD_PRESSURE_MEASUREMENTS);

    public static final String COLUMN_BLOOD_PRESSURE__SYSTOLIC = "systolic";
    public static final String COLUMN_BLOOD_PRESSURE__DIASTOLIC = "diastolic";
    public static final String COLUMN_BLOOD_PRESSURE__PULSE = "pulse";


    // Weight Scale
    private static final String BASE_PATH_WEIGHT_SCALE_MEASUREMENTS = "weight_scale_measurements";
    public static final Uri CONTENT_URI_WEIGHT_SCALE_MEASUREMENTS = buildUpContentUri(BASE_PATH_WEIGHT_SCALE_MEASUREMENTS);

    public static final String COLUMN_WEIGHT__WEIGHT = "weight";


    // Manually Entered Heart Rate
    private static final String BASE_PATH_MANUALLY_ENTERED_HEART_RATES = "manually_entered_heart_rates";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_HEART_RATES = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_HEART_RATES);

    public static final String COLUMN_MANUALLY_ENTERED_HEART_RATE__HEART_RATE = "heart_rate";

    // Manually Entered Respiration Rate
    private static final String BASE_PATH_MANUALLY_ENTERED_RESPIRATION_RATES = "manually_entered_respiration_rates";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_RESPIRATION_RATES);
    
    public static final String COLUMN_MANUALLY_ENTERED_RESPIRATION_RATE__RESPIRATION_RATE = "respiration_rate";

    // Manually Entered Temperature
    private static final String BASE_PATH_MANUALLY_ENTERED_TEMPERATURE = "manually_entered_temperature";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_TEMPERATURE);
    
    public static final String COLUMN_MANUALLY_ENTERED_TEMPERATURE__TEMPERATURE = "temperature";

    // Manually Entered SpO2
    private static final String BASE_PATH_MANUALLY_SPO2 = "manually_entered_spo2";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_SPO2 = buildUpContentUri(BASE_PATH_MANUALLY_SPO2);
    
    public static final String COLUMN_MANUALLY_ENTERED_SPO2__SPO2 = "spo2";

    // Manually Entered Blood Pressure
    private static final String BASE_PATH_MANUALLY_BLOOD_PRESSURE = "manually_entered_blood_pressure";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE = buildUpContentUri(BASE_PATH_MANUALLY_BLOOD_PRESSURE);
    
    public static final String COLUMN_MANUALLY_ENTERED_BLOOD_PRESSURE__SYSTOLIC = "systolic";
    public static final String COLUMN_MANUALLY_ENTERED_BLOOD_PRESSURE__DIASTOLIC = "diastolic";

    // Manually Entered Weight Scale
    private static final String BASE_PATH_MANUALLY_ENTERED_WEIGHT = "manually_entered_weight";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_WEIGHT = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_WEIGHT);

    public static final String COLUMN_MANUALLY_ENTERED_WEIGHT__WEIGHT = "weight";

    // Manually Entered Consciousness Level
    private static final String BASE_PATH_MANUALLY_CONSCIOUSNESS_LEVEL = "manually_entered_consciousness_level";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL = buildUpContentUri(BASE_PATH_MANUALLY_CONSCIOUSNESS_LEVEL);

    public static final String COLUMN_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL__CONSCIOUSNESS_LEVEL = "consciousness_level";


    // Manually Entered Supplemental Oxygen Level
    private static final String BASE_PATH_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL = "manually_entered_supplemental_oxygen_level";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL);

    public static final String COLUMN_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN__SUPPLEMENTAL_OXYGEN_LEVEL = "supplemental_oxygen_level";


    // Manually Entered Annotation
    private static final String BASE_PATH_MANUALLY_ENTERED_ANNOTATIONS = "manually_entered_annotations";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_ANNOTATIONS);

    public static final String COLUMN_MANUALLY_ENTERED_ANNOTATION__ANNOTATION_TEXT = "annotation_text";


    // Manually Entered Capillary Refill Time
    private static final String BASE_PATH_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME = "manually_entered_capillary_refill_time";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);

    public static final String COLUMN_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME__CAPILLARY_REFILL_TIME = "capillary_refill_time";


    // Manually Entered Respiration Distress
    private static final String BASE_PATH_MANUALLY_ENTERED_RESPIRATION_DISTRESS = "manually_entered_respiration_distress";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_DISTRESS = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_RESPIRATION_DISTRESS);

    public static final String COLUMN_MANUALLY_ENTERED_RESPIRATION_DISTRESS__RESPIRATION_DISTRESS = "respiration_distress";


    // Manually Entered Parent or Nurse Concern
    private static final String BASE_PATH_MANUALLY_ENTERED_NURSE_OR_FAMILY_CONCERN = "manually_entered_family_or_nurse_concern";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_NURSE_OR_FAMILY_CONCERN = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_NURSE_OR_FAMILY_CONCERN);

    public static final String COLUMN_MANUALLY_ENTERED_NURSE_OR_FAMILY_CONCERN = "concern";


    // Manually Entered Urine Output
    private static final String BASE_PATH_MANUALLY_ENTERED_URINE_OUTPUT = "manually_entered_urine_output";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_URINE_OUTPUT = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_URINE_OUTPUT);

    public static final String COLUMN_MANUALLY_ENTERED_URINE_OUTPUT = "urine_output";



    // Early Waring Scores and Threshold Sets
    private static final String BASE_PATH_EARLY_WARNING_SCORES = "early_warning_scores";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORES = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORES);
    
    public static final String COLUMN_EARLY_WARNING_SCORE__EARLY_WARNING_SCORE = "early_warning_score";                      			// The EWS measurement
    public static final String COLUMN_EARLY_WARNING_SCORE__MAX_POSSIBLE = "max_possible";                                    			// The max number this EWS is measured against. E.g. if there is only a Lifetouch then the Max is different to if there is a Lifetouch, Lifetemp, Pulse Ox and Blood Pressure
    public static final String COLUMN_EARLY_WARNING_SCORE__IS_SPECIAL_ALERT = "is_special_alert";										// EWS level triggered by a special case rather than the aggregate score (e.g. NEWS specifies EWS of 2 (orange) if any one vital sign gives a score of 3)
    public static final String COLUMN_EARLY_WARNING_SCORE__TREND_DIRECTION = "trend_direction";											// Which way is the trend going. Allows the UI and Server to show this onscreen
    public static final String COLUMN_EARLY_WARNING_SCORE__THRESHOLD_SET_AGE_BLOCK_DETAIL_ID = "threshold_set_age_block_detail_id";     // Which Threshold Set Age Block Detail was in use when this EWS was calculated

    
    private static final String BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SETS = "early_warning_score_threshold_sets";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SETS = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SETS);
    
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SETS__NAME = "set_name";                                      // Description of this EWS type. E.g. NEWS, PEWS, Mat Hospital Adult, Rory Hospital Kids
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SETS__IS_DEFAULT = "is_default";                              // Was this download as a Default from the Server

    
    private static final String BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS = "early_warning_score_threshold_set_age_block_details";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS);
    
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__THRESHOLD_SET_ID = "threshold_set_id"; // Which Threshold Set record this Level record is linked to
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__AGE_BOTTOM = "age_bottom";             // Start Age this threshold set is valid across
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__AGE_TOP = "age_top";                   // End Age this threshold set is valid across
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__DISPLAY_NAME = "display_name";         // Name to show on the Gateway/Server
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__IMAGE_BINARY = "image_binary";         // Image to show on the Gateway/Server
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__IS_ADULT = "is_adult";                 // Are these thresholds for Adults

    
    private static final String BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS = "early_warning_score_threshold_set_levels";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS);

    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__THRESHOLD_SET_AGE_BLOCK_DETAIL_ID = "threshold_set_age_block_detail";
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__RANGE_BOTTOM = "range_bottom";                    // Bottom value this threshold set is valid from  
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__RANGE_TOP = "range_top";                          // Top value this threshold set is valid to
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__EARLY_WARNING_SCORE = "early_warning_score";      // Early Warning Score number for this range
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__MEASUREMENT_TYPE = "measurement_type";            // What kind of measurement is it?
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__MEASUREMENT_TYPE_AS_STRING = "measurement_type_as_string";            // What kind of measurement is it?
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__DISPLAY_TEXT = "display_text";                    // What to show on the screen when entering this measurement
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__INFORMATION_TEXT = "information_text";            // What to show on the RHS of the screen when entering this measurement


    private static final String BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS = "early_warning_score_threshold_set_colours";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS);

    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__THRESHOLD_SET_AGE_BLOCK_DETAIL_ID = "threshold_set_age_block_detail";
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__SCORE = "score";
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__COLOUR = "colour";
    public static final String COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__TEXT_COLOUR = "text_colour";


    private static final String BASE_PATH_SETUP_MODE_LOGS = "setup_mode_logs";
    public static final Uri CONTENT_URI_SETUP_MODE_LOGS = buildUpContentUri(BASE_PATH_SETUP_MODE_LOGS);

    public static final String COLUMN_SETUP_MODE_LOGS_SENSOR_TYPE = "sensor_type";
    public static final String COLUMN_SETUP_MODE_LOGS_DEVICE_TYPE = "device_type";
    public static final String COLUMN_SETUP_MODE_LOGS_START_SETUP_MODE_TIME = "start_setup_mode_time";
    public static final String COLUMN_SETUP_MODE_LOGS_END_SETUP_MODE_TIME = "end_setup_mode_time";


    // Server Configurable Text
    private static final String BASE_PATH_SERVER_CONFIGURABLE_TEXT = "server_configurable_text";
    public static final Uri CONTENT_URI_SERVER_CONFIGURABLE_TEXT = buildUpContentUri(BASE_PATH_SERVER_CONFIGURABLE_TEXT);

    public static final String COLUMN_SERVER_CONFIGURABLE_TEXT__STRING = "string";
    public static final String COLUMN_SERVER_CONFIGURABLE_TEXT__STRING_TYPE = "string_type";

    // Wards
    private static final String BASE_PATH_WARDS = "wards";
    public static final Uri CONTENT_URI_WARDS = buildUpContentUri(BASE_PATH_WARDS);

    public static final String COLUMN_WARDS__WARD_NAME = "ward_name";

    // Beds
    private static final String BASE_PATH_BEDS = "beds";
    public static final Uri CONTENT_URI_BEDS = buildUpContentUri(BASE_PATH_BEDS);

    public static final String COLUMN_BEDS__BED_NAME = "bed_name";
    public static final String COLUMN_BEDS__BY_WARD_ID = "by_ward_id";

    // Web Pages
    private static final String BASE_PATH_WEB_PAGE_DETAILS = "web_page_details";
    public static final Uri CONTENT_URI_WEB_PAGE_DETAILS = buildUpContentUri(BASE_PATH_WEB_PAGE_DETAILS);

    public static final String COLUMN_WEB_PAGE_DETAILS__URL = "url";
    public static final String COLUMN_WEB_PAGE_DETAILS__DESCRIPTION = "description";
}