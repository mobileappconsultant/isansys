package com.isansys.patientgateway.database.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.isansys.patientgateway.RowsPending;
import com.isansys.patientgateway.database.DatabaseHelper;
import com.isansys.patientgateway.database.Table;
import com.isansys.patientgateway.database.TableAuditTrail;
import com.isansys.patientgateway.database.TableBeds;
import com.isansys.patientgateway.database.TableBloodPressureBattery;
import com.isansys.patientgateway.database.TableBloodPressureMeasurement;
import com.isansys.patientgateway.database.TableConnectionEvent;
import com.isansys.patientgateway.database.TableDeviceInfo;
import com.isansys.patientgateway.database.TableDeviceSession;
import com.isansys.patientgateway.database.TableDiagnosticsGatewayStartupTimes;
import com.isansys.patientgateway.database.TableDiagnosticsUiStartupTimes;
import com.isansys.patientgateway.database.TableEarlyWarningScore;
import com.isansys.patientgateway.database.TableLifetempBattery;
import com.isansys.patientgateway.database.TableLifetempMeasurement;
import com.isansys.patientgateway.database.TableLifetouchBattery;
import com.isansys.patientgateway.database.TableLifetouchHeartBeat;
import com.isansys.patientgateway.database.TableLifetouchHeartRate;
import com.isansys.patientgateway.database.TableLifetouchPatientOrientation;
import com.isansys.patientgateway.database.TableLifetouchRawAccelerometerModeSample;
import com.isansys.patientgateway.database.TableLifetouchRespirationRate;
import com.isansys.patientgateway.database.TableLifetouchSetupModeRawSample;
import com.isansys.patientgateway.database.TableManuallyEnteredAnnotation;
import com.isansys.patientgateway.database.TableManuallyEnteredBloodPressure;
import com.isansys.patientgateway.database.TableManuallyEnteredCapillaryRefillTime;
import com.isansys.patientgateway.database.TableManuallyEnteredConsciousnessLevel;
import com.isansys.patientgateway.database.TableManuallyEnteredFamilyOrNurseConcern;
import com.isansys.patientgateway.database.TableManuallyEnteredHeartRate;
import com.isansys.patientgateway.database.TableManuallyEnteredMeasurement;
import com.isansys.patientgateway.database.TableManuallyEnteredRespirationDistress;
import com.isansys.patientgateway.database.TableManuallyEnteredRespirationRate;
import com.isansys.patientgateway.database.TableManuallyEnteredSpO2;
import com.isansys.patientgateway.database.TableManuallyEnteredSupplementalOxygenLevel;
import com.isansys.patientgateway.database.TableManuallyEnteredTemperature;
import com.isansys.patientgateway.database.TableManuallyEnteredUrineOutput;
import com.isansys.patientgateway.database.TableManuallyEnteredWeight;
import com.isansys.patientgateway.database.TableOximeterBattery;
import com.isansys.patientgateway.database.TableOximeterIntermediateMeasurement;
import com.isansys.patientgateway.database.TableOximeterMeasurement;
import com.isansys.patientgateway.database.TableOximeterSetupModeRawSample;
import com.isansys.patientgateway.database.TablePatientDetails;
import com.isansys.patientgateway.database.TablePatientSession;
import com.isansys.patientgateway.database.TablePatientSessionsFullySynced;
import com.isansys.patientgateway.database.TableSensorMeasurement;
import com.isansys.patientgateway.database.TableServerConfigurableText;
import com.isansys.patientgateway.database.TableSetupModeLog;
import com.isansys.patientgateway.database.TableThresholdSet;
import com.isansys.patientgateway.database.TableThresholdSetAgeBlockDetail;
import com.isansys.patientgateway.database.TableThresholdSetColour;
import com.isansys.patientgateway.database.TableThresholdSetLevel;
import com.isansys.patientgateway.database.TableWards;
import com.isansys.patientgateway.database.TableViewableWebPageDetails;
import com.isansys.patientgateway.database.TableWeightScaleBattery;
import com.isansys.patientgateway.database.TableWeightScaleWeight;

public class IsansysPatientGatewayContentProvider extends ContentProvider
{
    private static SQLiteDatabase db;

    private static final String AUTHORITY = "com.isansys.patientgateway.database.contentprovider";

    private final String TAG = "ContentProvider";


    private static Uri buildUpContentUri(String base_path)
    {
        return Uri.parse("content://" + AUTHORITY + "/" + base_path);
    }

	public static final Uri CONTENT_URI = buildUpContentUri("");

    private static final String QUERY_PARAMETER_LIMIT = "limit";

    // Lifetouch database tables
    private static final String BASE_PATH_LIFETOUCH_HEART_RATES = "lifetouch_heart_rates";
    public static final Uri CONTENT_URI_LIFETOUCH_HEART_RATES = buildUpContentUri(BASE_PATH_LIFETOUCH_HEART_RATES);
    private static final String BASE_PATH_SYNCABLE_LIFETOUCH_HEART_RATES = "syncable_lifetouch_heart_rates";
    public static final Uri CONTENT_URI_SYNCABLE_LIFETOUCH_HEART_RATES = buildUpContentUri(BASE_PATH_SYNCABLE_LIFETOUCH_HEART_RATES);
    private static final String BASE_PATH_NON_SYNCABLE_LIFETOUCH_HEART_RATES = "non_syncable_lifetouch_heart_rates";
    public static final Uri CONTENT_URI_NON_SYNCABLE_LIFETOUCH_HEART_RATES = buildUpContentUri(BASE_PATH_NON_SYNCABLE_LIFETOUCH_HEART_RATES);

    private static final String BASE_PATH_LIFETOUCH_HEART_BEATS = "lifetouch_heartbeats";
    public static final Uri CONTENT_URI_LIFETOUCH_HEART_BEATS = buildUpContentUri(BASE_PATH_LIFETOUCH_HEART_BEATS);
    private static final String BASE_PATH_SYNCABLE_LIFETOUCH_HEART_BEATS = "syncable_lifetouch_heart_beats";
    public static final Uri CONTENT_URI_SYNCABLE_LIFETOUCH_HEART_BEATS = buildUpContentUri(BASE_PATH_SYNCABLE_LIFETOUCH_HEART_BEATS);
    private static final String BASE_PATH_NON_SYNCABLE_LIFETOUCH_HEART_BEATS = "non_syncable_lifetouch_heart_beats";
    public static final Uri CONTENT_URI_NON_SYNCABLE_LIFETOUCH_HEART_BEATS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_LIFETOUCH_HEART_BEATS);

    private static final String BASE_PATH_LIFETOUCH_SETUP_MODE_SAMPLES = "lifetouch_setup_mode_samples";
    public static final Uri CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES = buildUpContentUri(BASE_PATH_LIFETOUCH_SETUP_MODE_SAMPLES);
    private static final String BASE_PATH_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES = "syncable_lifetouch_setup_mode_samples";
    public static final Uri CONTENT_URI_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES = buildUpContentUri(BASE_PATH_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES);
    private static final String BASE_PATH_NON_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES = "non_syncable_lifetouch_setup_mode_samples";
    public static final Uri CONTENT_URI_NON_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES = buildUpContentUri(BASE_PATH_NON_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES);

    private static final String BASE_PATH_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES = "lifetouch_raw_accelerometer_mode_samples";
    public static final Uri CONTENT_URI_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES = buildUpContentUri(BASE_PATH_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES);
    private static final String BASE_PATH_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES = "syncable_lifetouch_raw_accelerometer_mode_samples";
    public static final Uri CONTENT_URI_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES = buildUpContentUri(BASE_PATH_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES);
    private static final String BASE_PATH_NON_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES = "non_syncable_lifetouch_raw_accelerometer_mode_samples";
    public static final Uri CONTENT_URI_NON_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES = buildUpContentUri(BASE_PATH_NON_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES);

    private static final String BASE_PATH_LIFETOUCH_RESPIRATION_RATES = "lifetouch_respiration_rates";
    public static final Uri CONTENT_URI_LIFETOUCH_RESPIRATION_RATES = buildUpContentUri(BASE_PATH_LIFETOUCH_RESPIRATION_RATES);
    private static final String BASE_PATH_SYNCABLE_LIFETOUCH_RESPIRATION_RATES = "syncable_lifetouch_respiration_rates";
    public static final Uri CONTENT_URI_SYNCABLE_LIFETOUCH_RESPIRATION_RATES = buildUpContentUri(BASE_PATH_SYNCABLE_LIFETOUCH_RESPIRATION_RATES);
    private static final String BASE_PATH_NON_SYNCABLE_LIFETOUCH_RESPIRATION_RATES = "non_syncable_lifetouch_respiration_rates";
    public static final Uri CONTENT_URI_NON_SYNCABLE_LIFETOUCH_RESPIRATION_RATES = buildUpContentUri(BASE_PATH_NON_SYNCABLE_LIFETOUCH_RESPIRATION_RATES);

    private static final String BASE_PATH_LIFETOUCH_BATTERY_MEASUREMENTS = "lifetouch_battery_measurements";
    public static final Uri CONTENT_URI_LIFETOUCH_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_LIFETOUCH_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS = "syncable_lifetouch_battery_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS = "non_syncable_lifetouch_battery_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS);
    
    private static final String BASE_PATH_LIFETOUCH_PATIENT_ORIENTATION = "patient_orientation";
    public static final Uri CONTENT_URI_LIFETOUCH_PATIENT_ORIENTATION = buildUpContentUri(BASE_PATH_LIFETOUCH_PATIENT_ORIENTATION);
    private static final String BASE_PATH_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATION = "syncable_lifetouch_patient_orientations";
    public static final Uri CONTENT_URI_SYNCABLE_PATIENT_ORIENTATION = buildUpContentUri(BASE_PATH_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATION);
    private static final String BASE_PATH_NON_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATION = "non_syncable_lifetouch_patient_orientations";
    public static final Uri CONTENT_URI_NON_SYNCABLE_PATIENT_ORIENTATION = buildUpContentUri(BASE_PATH_NON_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATION);

    
    // Lifetemp database tables
    private static final String BASE_PATH_LIFETEMP_TEMPERATURE_MEASUREMENTS = "lifetemp_temperature_measurements";
    public static final Uri CONTENT_URI_LIFETEMP_TEMPERATURE_MEASUREMENTS = buildUpContentUri(BASE_PATH_LIFETEMP_TEMPERATURE_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS = "syncable_lifetemp_temperature_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS = "non_syncable_lifetemp_temperature_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS);

    private static final String BASE_PATH_LIFETEMP_BATTERY_MEASUREMENTS = "lifetemp_battery_measurements";
    public static final Uri CONTENT_URI_LIFETEMP_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_LIFETEMP_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS = "syncable_lifetemp_battery_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS = "non_syncable_lifetemp_battery_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS);

    
    // Oximeter database tables
    private static final String BASE_PATH_OXIMETER_MEASUREMENTS = "oximeter_measurements";
    public static final Uri CONTENT_URI_OXIMETER_MEASUREMENTS = buildUpContentUri(BASE_PATH_OXIMETER_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_OXIMETER_MEASUREMENTS = "syncable_oximeter_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_OXIMETER_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_OXIMETER_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_OXIMETER_MEASUREMENTS = "non_syncable_oximeter_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_OXIMETER_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_OXIMETER_MEASUREMENTS);

    private static final String BASE_PATH_OXIMETER_INTERMEDIATE_MEASUREMENTS = "oximeter_intermediate_measurements";
    public static final Uri CONTENT_URI_OXIMETER_INTERMEDIATE_MEASUREMENTS = buildUpContentUri(BASE_PATH_OXIMETER_INTERMEDIATE_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS = "syncable_oximeter_intermediate_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS = "non_syncable_oximeter_intermediate_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS);

    private static final String BASE_PATH_OXIMETER_SETUP_MODE_SAMPLES = "oximeter_setup_mode_samples";
    public static final Uri CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES = buildUpContentUri(BASE_PATH_OXIMETER_SETUP_MODE_SAMPLES);
    private static final String BASE_PATH_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES = "syncable_oximeter_setup_mode_samples";
    public static final Uri CONTENT_URI_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES = buildUpContentUri(BASE_PATH_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES);
    private static final String BASE_PATH_NON_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES = "non_syncable_oximeter_setup_mode_samples";
    public static final Uri CONTENT_URI_NON_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES = buildUpContentUri(BASE_PATH_NON_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES);

    private static final String BASE_PATH_OXIMETER_BATTERY_MEASUREMENTS = "oximeter_battery_measurements";
    public static final Uri CONTENT_URI_OXIMETER_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_OXIMETER_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS = "syncable_oximeter_battery_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS = "non_syncable_oximeter_battery_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS);


    // Blood Pressure database tables
    private static final String BASE_PATH_BLOOD_PRESSURE_MEASUREMENTS = "blood_pressure_measurements";
    public static final Uri CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS = buildUpContentUri(BASE_PATH_BLOOD_PRESSURE_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS = "syncable_blood_pressure_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS = "non_syncable_blood_pressure_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS);

    private static final String BASE_PATH_BLOOD_PRESSURE_BATTERY_MEASUREMENTS = "blood_pressure_battery_measurements";
    public static final Uri CONTENT_URI_BLOOD_PRESSURE_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_BLOOD_PRESSURE_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS = "syncable_blood_pressure_battery_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS = "non_syncable_blood_pressure_battery_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS);


    // Weight Scale database tables
    private static final String BASE_PATH_WEIGHT_SCALE_MEASUREMENTS = "weight_scale_measurements";
    public static final Uri CONTENT_URI_WEIGHT_SCALE_MEASUREMENTS = buildUpContentUri(BASE_PATH_WEIGHT_SCALE_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS = "syncable_weight_scale_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS = "non_syncable_weight_scale_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS);

    private static final String BASE_PATH_WEIGHT_SCALE_BATTERY_MEASUREMENTS = "weight_scale_battery_measurements";
    public static final Uri CONTENT_URI_WEIGHT_SCALE_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_WEIGHT_SCALE_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS = "syncable_weight_scale_battery_measurements";
    public static final Uri CONTENT_URI_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS);
    private static final String BASE_PATH_NON_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS = "non_syncable_weight_scale_battery_measurements";
    public static final Uri CONTENT_URI_NON_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS);


    private static final String BASE_PATH_PATIENT_SESSIONS = "patient_sessions";
    public static final Uri CONTENT_URI_PATIENT_SESSIONS = buildUpContentUri(BASE_PATH_PATIENT_SESSIONS);
    private static final String BASE_PATH_SYNCABLE_START_PATIENT_SESSIONS = "syncable_start_patient_sessions";
    public static final Uri CONTENT_URI_SYNCABLE_START_PATIENT_SESSIONS = buildUpContentUri(BASE_PATH_SYNCABLE_START_PATIENT_SESSIONS);
    private static final String BASE_PATH_NON_SYNCABLE_START_PATIENT_SESSIONS = "non_syncable_start_patient_sessions";
    public static final Uri CONTENT_URI_NON_SYNCABLE_START_PATIENT_SESSIONS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_START_PATIENT_SESSIONS);
    private static final String BASE_PATH_SYNCABLE_END_PATIENT_SESSIONS = "syncable_end_patient_sessions";
    public static final Uri CONTENT_URI_SYNCABLE_END_PATIENT_SESSIONS = buildUpContentUri(BASE_PATH_SYNCABLE_END_PATIENT_SESSIONS);
    private static final String BASE_PATH_NON_SYNCABLE_END_PATIENT_SESSIONS = "non_syncable_end_patient_sessions";
    public static final Uri CONTENT_URI_NON_SYNCABLE_END_PATIENT_SESSIONS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_END_PATIENT_SESSIONS);

    private static final String BASE_PATH_PATIENT_DETAILS = "patient_details";
    public static final Uri CONTENT_URI_PATIENT_DETAILS = buildUpContentUri(BASE_PATH_PATIENT_DETAILS);
    
    private static final String BASE_PATH_DEVICE_INFO = "device_info";
    public static final Uri CONTENT_URI_DEVICE_INFO = buildUpContentUri(BASE_PATH_DEVICE_INFO);

    private static final String BASE_PATH_DEVICE_SESSIONS = "device_sessions";
    public static final Uri CONTENT_URI_DEVICE_SESSIONS = buildUpContentUri(BASE_PATH_DEVICE_SESSIONS);
    private static final String BASE_PATH_SYNCABLE_START_DEVICE_SESSIONS = "syncable_start_device_sessions";
    public static final Uri CONTENT_URI_SYNCABLE_START_DEVICE_SESSIONS = buildUpContentUri(BASE_PATH_SYNCABLE_START_DEVICE_SESSIONS);
    private static final String BASE_PATH_NON_SYNCABLE_START_DEVICE_SESSIONS = "non_syncable_start_device_sessions";
    public static final Uri CONTENT_URI_NON_SYNCABLE_START_DEVICE_SESSIONS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_START_DEVICE_SESSIONS);
    private static final String BASE_PATH_SYNCABLE_END_DEVICE_SESSIONS = "syncable_end_device_sessions";
    public static final Uri CONTENT_URI_SYNCABLE_END_DEVICE_SESSIONS = buildUpContentUri(BASE_PATH_SYNCABLE_END_DEVICE_SESSIONS);
    private static final String BASE_PATH_NON_SYNCABLE_END_DEVICE_SESSIONS = "non_syncable_end_device_sessions";
    public static final Uri CONTENT_URI_NON_SYNCABLE_END_DEVICE_SESSIONS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_END_DEVICE_SESSIONS);

    private static final String BASE_PATH_SETUP_MODE_LOGS = "setup_mode_logs";
    public static final Uri CONTENT_URI_SETUP_MODE_LOGS = buildUpContentUri(BASE_PATH_SETUP_MODE_LOGS);
    private static final String BASE_PATH_SYNCABLE_SETUP_MODE_LOGS = "syncable_setup_mode_logs";
    public static final Uri CONTENT_URI_SYNCABLE_SETUP_MODE_LOGS = buildUpContentUri(BASE_PATH_SYNCABLE_SETUP_MODE_LOGS);
    private static final String BASE_PATH_NON_SYNCABLE_SETUP_MODE_LOGS = "non_syncable_setup_mode_logs";
    public static final Uri CONTENT_URI_NON_SYNCABLE_SETUP_MODE_LOGS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_SETUP_MODE_LOGS);

    private static final String BASE_PATH_CONNECTION_EVENTS = "connection_events";
    public static final Uri CONTENT_URI_CONNECTION_EVENTS = buildUpContentUri(BASE_PATH_CONNECTION_EVENTS);
    private static final String BASE_PATH_SYNCABLE_CONNECTION_EVENTS = "syncable_connection_events";
    public static final Uri CONTENT_URI_SYNCABLE_CONNECTION_EVENTS = buildUpContentUri(BASE_PATH_SYNCABLE_CONNECTION_EVENTS);
    private static final String BASE_PATH_NON_SYNCABLE_CONNECTION_EVENTS = "non_syncable_connection_events";
    public static final Uri CONTENT_URI_NON_SYNCABLE_CONNECTION_EVENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_CONNECTION_EVENTS);

    private static final String BASE_PATH_DIAGNOSTICS_GATEWAY_STARTUP_TIMES = "diagnostics_gateway_startup_times";
    public static final Uri CONTENT_URI_DIAGNOSTICS_GATEWAY_STARTUP_TIMES = buildUpContentUri(BASE_PATH_DIAGNOSTICS_GATEWAY_STARTUP_TIMES);

    private static final String BASE_PATH_DIAGNOSTICS_UI_STARTUP_TIMES = "diagnostics_ui_startup_times";
    public static final Uri CONTENT_URI_DIAGNOSTICS_UI_STARTUP_TIMES = buildUpContentUri(BASE_PATH_DIAGNOSTICS_UI_STARTUP_TIMES);

    private static final String BASE_PATH_MANUALLY_ENTERED_HEART_RATES = "manually_entered_heart_rates";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_HEART_RATES = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_HEART_RATES);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_HEART_RATES = "syncable_manually_entered_heart_rates";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_HEART_RATES = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_HEART_RATES);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_HEART_RATES = "non_syncable_manually_entered_heart_rates";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_HEART_RATES = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_HEART_RATES);

    private static final String BASE_PATH_MANUALLY_ENTERED_RESPIRATION_RATES = "manually_entered_respiration_rates";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_RESPIRATION_RATES);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES = "syncable_manually_entered_respiration_rates";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES = "non_syncable_manually_entered_respiration_rates";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES);

    private static final String BASE_PATH_MANUALLY_ENTERED_TEMPERATURE = "manually_entered_temperature";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_TEMPERATURE);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES = "syncable_manually_entered_temperatures";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES = "non_syncable_manually_entered_temperatures";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES);

    private static final String BASE_PATH_MANUALLY_ENTERED_SPO2 = "manually_entered_spo2";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_SPO2 = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_SPO2);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_SPO2 = "syncable_manually_entered_spo2";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_SPO2 = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_SPO2);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_SPO2 = "non_syncable_manually_entered_spo2";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_SPO2 = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_SPO2);

    private static final String BASE_PATH_MANUALLY_ENTERED_BLOOD_PRESSURE = "manually_entered_blood_pressure";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_BLOOD_PRESSURE);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_BLOOD_PRESSURE = "syncable_manually_entered_blood_pressures";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURE = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_BLOOD_PRESSURE);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_BLOOD_PRESSURE = "non_syncable_manually_entered_blood_pressures";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURE = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_BLOOD_PRESSURE);

    private static final String BASE_PATH_MANUALLY_ENTERED_WEIGHT = "manually_entered_weight";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_WEIGHT = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_WEIGHT);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_WEIGHT = "syncable_manually_entered_weights";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_WEIGHT = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_WEIGHT);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_WEIGHT = "non_syncable_manually_entered_weights";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_WEIGHT = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_WEIGHT);

    private static final String BASE_PATH_MANUALLY_CONSCIOUSNESS_LEVEL = "manually_entered_consciousness_level";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL = buildUpContentUri(BASE_PATH_MANUALLY_CONSCIOUSNESS_LEVEL);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_CONSCIOUSNESS_LEVEL = "syncable_manually_entered_consciousness_levels";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_CONSCIOUSNESS_LEVEL);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_CONSCIOUSNESS_LEVEL = "non_syncable_manually_entered_consciousness_levels";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_CONSCIOUSNESS_LEVEL);

    private static final String BASE_PATH_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL = "manually_entered_supplemental_oxygen_level";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL = "syncable_manually_entered_supplemental_oxygen_levels";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL = "non_syncable_manually_entered_supplemental_oxygen_levels";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL);

    private static final String BASE_PATH_MANUALLY_ENTERED_ANNOTATIONS = "manually_entered_annotations";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_ANNOTATIONS);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS = "syncable_manually_entered_annotations";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS = "non_syncable_manually_entered_annotations";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS);

    private static final String BASE_PATH_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME = "manually_entered_capillary_refill_time";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME = "syncable_manually_entered_capillary_refill_time";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME = "non_syncable_manually_entered_capillary_refill_time";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);

    private static final String BASE_PATH_MANUALLY_ENTERED_RESPIRATION_DISTRESS = "manually_entered_respiration_distress";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_DISTRESS = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_RESPIRATION_DISTRESS);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS = "syncable_manually_entered_respiration_distress";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS = "non_syncable_manually_entered_respiration_distress";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS);

    private static final String BASE_PATH_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN = "manually_entered_family_or_nurse_concern";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN = "syncable_manually_entered_family_or_nurse_concern";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN = "non_syncable_manually_entered_family_or_nurse_concern";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN);

    private static final String BASE_PATH_MANUALLY_ENTERED_URINE_OUTPUT = "manually_entered_urine_output";
    public static final Uri CONTENT_URI_MANUALLY_ENTERED_URINE_OUTPUT = buildUpContentUri(BASE_PATH_MANUALLY_ENTERED_URINE_OUTPUT);
    private static final String BASE_PATH_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT = "syncable_manually_entered_urine_output";
    public static final Uri CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT = buildUpContentUri(BASE_PATH_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT);
    private static final String BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT = "non_syncable_manually_entered_urine_output";
    public static final Uri CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT = buildUpContentUri(BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT);

    private static final String BASE_PATH_PATIENT_SESSIONS_FULLY_SYNCED = "patient_sessions_fully_synced";
    public static final Uri CONTENT_URI_PATIENT_SESSIONS_FULLY_SYNCED = buildUpContentUri(BASE_PATH_PATIENT_SESSIONS_FULLY_SYNCED);

    // Early Warning Scores and Threshold Sets
    private static final String BASE_PATH_EARLY_WARNING_SCORES = "early_warning_scores";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORES = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORES);
    private static final String BASE_PATH_SYNCABLE_EARLY_WARNING_SCORES = "syncable_early_warning_scores";
    public static final Uri CONTENT_URI_SYNCABLE_EARLY_WARNING_SCORES = buildUpContentUri(BASE_PATH_SYNCABLE_EARLY_WARNING_SCORES);
    private static final String BASE_PATH_NON_SYNCABLE_EARLY_WARNING_SCORES = "non_syncable_early_warning_scores";
    public static final Uri CONTENT_URI_NON_SYNCABLE_EARLY_WARNING_SCORES = buildUpContentUri(BASE_PATH_NON_SYNCABLE_EARLY_WARNING_SCORES);

    private static final String BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SETS = "early_warning_score_threshold_sets";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SETS = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SETS);

    private static final String BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS = "early_warning_score_threshold_set_age_block_details";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS);

    private static final String BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS = "early_warning_score_threshold_set_levels";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS);

    private static final String BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS = "early_warning_score_threshold_set_colours";
    public static final Uri CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS = buildUpContentUri(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS);

    private static final String BASE_PATH_SERVER_CONFIGURABLE_TEXT = "server_configurable_text";
    public static final Uri CONTENT_URI_SERVER_CONFIGURABLE_TEXT = buildUpContentUri(BASE_PATH_SERVER_CONFIGURABLE_TEXT);

    private static final String BASE_PATH_WARDS = "wards";
    public static final Uri CONTENT_URI_WARDS = buildUpContentUri(BASE_PATH_WARDS);

    private static final String BASE_PATH_BEDS = "beds";
    public static final Uri CONTENT_URI_BEDS = buildUpContentUri(BASE_PATH_BEDS);

    private static final String BASE_PATH_AUDIT_TRAIL = "audit_trail";
    public static final Uri CONTENT_URI_AUDIT_TRAIL = buildUpContentUri(BASE_PATH_AUDIT_TRAIL);
    private static final String BASE_PATH_SYNCABLE_AUDITABLE_EVENTS = "syncable_auditable_events";
    public static final Uri CONTENT_URI_SYNCABLE_AUDITABLE_EVENTS = buildUpContentUri(BASE_PATH_SYNCABLE_AUDITABLE_EVENTS);
    private static final String BASE_PATH_NON_SYNCABLE_AUDITABLE_EVENTS = "non_syncable_auditable_events";
    public static final Uri CONTENT_URI_NON_SYNCABLE_AUDITABLE_EVENTS = buildUpContentUri(BASE_PATH_NON_SYNCABLE_AUDITABLE_EVENTS);

    private static final String BASE_PATH_VIEWABLE_WEB_PAGES = "web_page_details";
    public static final Uri CONTENT_URI_VIEWABLE_WEB_PAGES = buildUpContentUri(BASE_PATH_VIEWABLE_WEB_PAGES);


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    
    public static final String CALLABLE_METHOD__GET_COUNT = "getCount";
    public static final String CALLABLE_METHOD__OPEN_TEST_DATABASE = "openTestDatabase";

    public enum UriType
    {
        // Lifetouch
        ALL_LIFETOUCH_HEART_BEATS,
        LIFETOUCH_HEART_BEAT_DATABASE_ID,
        SYNCABLE_LIFETOUCH_HEART_BEATS,
        NON_SYNCABLE_LIFETOUCH_HEART_BEATS,
    
        ALL_LIFETOUCH_RESPIRATION_RATES,
        LIFETOUCH_RESPIRATION_RATE_DATABASE_ID,
        SYNCABLE_LIFETOUCH_RESPIRATION_RATES,
        NON_SYNCABLE_LIFETOUCH_RESPIRATION_RATES,
    
        ALL_LIFETOUCH_SETUP_MODE_SAMPLES,
        LIFETOUCH_SETUP_MODE_SAMPLE_DATABASE_ID,
        SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES,
        NON_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES,

        ALL_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES,
        LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE_DATABASE_ID,
        SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES,
        NON_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES,

        ALL_LIFETOUCH_HEART_RATES,
        LIFETOUCH_HEART_RATE_DATABASE_ID,
        SYNCABLE_LIFETOUCH_HEART_RATES,
        NON_SYNCABLE_LIFETOUCH_HEART_RATES,
    
        ALL_LIFETOUCH_BATTERY_MEASUREMENTS,
        LIFETOUCH_BATTERY_MEASUREMENT_DATABASE_ID,
        SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS,
        NON_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS,
        
        ALL_LIFETOUCH_PATIENT_ORIENTATIONS,
        LIFETOUCH_PATIENT_ORIENTATION_DATABASE_ID,    
        SYNCABLE_LIFETOUCH_PATIENT_ORIENTATIONS,
        NON_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATIONS,
        
        // Lifetemp
        ALL_LIFETEMP_TEMPERATURE_MEASUREMENTS,
        LIFETEMP_TEMPERATURE_MEASUREMENT_DATABASE_ID,
        SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS,
        NON_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS,
            
        ALL_LIFETEMP_BATTERY_MEASUREMENTS,
        LIFETEMP_BATTERY_MEASUREMENT_DATABASE_ID,
        SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS,
        NON_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS,
        
        // Pulse Oximeter
        ALL_OXIMETER_MEASUREMENTS,
        OXIMETER_MEASUREMENT_DATABASE_ID,
        SYNCABLE_OXIMETER_MEASUREMENTS,
        NON_SYNCABLE_OXIMETER_MEASUREMENTS,
    
        ALL_OXIMETER_INTERMEDIATE_MEASUREMENTS,
        OXIMETER_INTERMEDIATE_MEASUREMENT_DATABASE_ID,
        SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS,
        NON_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS,
        
        ALL_OXIMETER_SETUP_MODE_SAMPLES,
        OXIMETER_SETUP_MODE_SAMPLE_DATABASE_ID,
        SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES,
        NON_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES,
    
        ALL_OXIMETER_BATTERY_MEASUREMENTS,
        OXIMETER_BATTERY_MEASUREMENT_DATABASE_ID,
        SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS,
        NON_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS,


        // Blood Pressure
        ALL_BLOOD_PRESSURE_MEASUREMENTS,
        BLOOD_PRESSURE_MEASUREMENT_DATABASE_ID,
        SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS,
        NON_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS,
    
        ALL_BLOOD_PRESSURE_BATTERY_MEASUREMENTS,
        BLOOD_PRESSURE_BATTERY_MEASUREMENT_DATABASE_ID,
        SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS,
        NON_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS,

        // Weight Scale
        ALL_WEIGHT_SCALE_MEASUREMENTS,
        WEIGHT_SCALE_MEASUREMENT_DATABASE_ID,
        SYNCABLE_WEIGHT_SCALE_MEASUREMENTS,
        NON_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS,

        ALL_WEIGHT_SCALE_BATTERY_MEASUREMENTS,
        WEIGHT_SCALE_BATTERY_MEASUREMENT_DATABASE_ID,
        SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS,
        NON_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS,

        // Patient Details
        ALL_PATIENT_DETAILS,
        PATIENT_DETAILS_DATABASE_ID,
        
        // Device Session Details
        ALL_DEVICE_SESSIONS,
        DEVICE_SESSION_DATABASE_ID,
        SYNCABLE_START_DEVICE_SESSIONS,
        NON_SYNCABLE_START_DEVICE_SESSIONS,
        SYNCABLE_END_DEVICE_SESSIONS,
        NON_SYNCABLE_END_DEVICE_SESSIONS,
    
        // Device Info Details
        ALL_DEVICE_INFO,
        DEVICE_INFO_DATABASE_ID,
    
        // Patient Session Details
        ALL_PATIENT_SESSIONS,
        PATIENT_SESSION_DATABASE_ID,
        SYNCABLE_START_PATIENT_SESSIONS,
        NON_SYNCABLE_START_PATIENT_SESSIONS,
        SYNCABLE_END_PATIENT_SESSIONS,
        NON_SYNCABLE_END_PATIENT_SESSIONS,
    
        ALL_PATIENT_SESSION_FULLY_SYNCED,
        PATIENT_SESSION_FULLY_SYNCED_DATABASE_ID,

        // Connection Events
        ALL_CONNECTION_EVENTS,
        CONNECTION_EVENT_DATABASE_ID,
        SYNCABLE_CONNECTION_EVENTS,
        NON_SYNCABLE_CONNECTION_EVENTS,

        // Diagnostics Events
        ALL_GATEWAY_STARTUP_TIMES,
        GATEWAY_STARTUP_TIMES_DATABASE_ID,
    
        ALL_UI_STARTUP_TIMES,
        UI_STARTUP_TIMES_DATABASE_ID,

        // Manually Entered Vital Signs
        ALL_MANUALLY_ENTERED_HEART_RATES,
        MANUALLY_ENTERED_HEART_RATE_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_HEART_RATES,
        NON_SYNCABLE_MANUALLY_ENTERED_HEART_RATES,
    
        ALL_MANUALLY_ENTERED_RESPIRATION_RATES,
        MANUALLY_ENTERED_RESPIRATION_RATE_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES,
        NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES,
    
        ALL_MANUALLY_ENTERED_TEMPERATURES,
        MANUALLY_ENTERED_TEMPERATURE_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_TEMPERATURES,
        NON_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES,
    
        ALL_MANUALLY_ENTERED_SPO2S,
        MANUALLY_ENTERED_SPO2_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_SPO2,
        NON_SYNCABLE_MANUALLY_ENTERED_SPO2,
    
        ALL_MANUALLY_ENTERED_BLOOD_PRESSURES,
        MANUALLY_ENTERED_BLOOD_PRESSURE_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURES,
        NON_SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURES,

        ALL_MANUALLY_ENTERED_WEIGHTS,
        MANUALLY_ENTERED_WEIGHT_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_WEIGHTS,
        NON_SYNCABLE_MANUALLY_ENTERED_WEIGHTS,

        ALL_MANUALLY_ENTERED_CONSCIOUS_LEVELS,
        MANUALLY_ENTERED_CONSCIOUS_LEVEL_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS,
        NON_SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS,

        ALL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS,
        MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS,
        NON_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS,

        ALL_MANUALLY_ENTERED_ANNOTATIONS,
        MANUALLY_ENTERED_ANNOTATION_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS,
        NON_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS,

        ALL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES,
        MANUALLY_ENTERED_CAPILLARY_REFILL_TIME_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES,
        NON_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES,

        ALL_MANUALLY_ENTERED_RESPIRATION_DISTRESS,
        MANUALLY_ENTERED_RESPIRATION_DISTRESS_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS,
        NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS,

        ALL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN,
        MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN,
        NON_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN,

        ALL_MANUALLY_ENTERED_URINE_OUTPUT,
        MANUALLY_ENTERED_URINE_OUTPUT_DATABASE_ID,
        SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT,
        NON_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT,

        // Early Warning Scores
        ALL_EARLY_WARNING_SCORES,
        EARLY_WARNING_SCORE_DATABASE_ID,
        SYNCABLE_EARLY_WARNING_SCORES,
        NON_SYNCABLE_EARLY_WARNING_SCORES,
        
        // Early Warning Score Threshold Levels
        ALL_EARLY_WARNING_SCORE_THRESHOLD_SETS,
        EARLY_WARNING_SCORE_THRESHOLD_SET_DATABASE_ID,

        ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS,
        EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAIL_DATABASE_ID,

        ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS,
        EARLY_WARNING_SCORE_THRESHOLD_SET_LEVEL_DATABASE_ID,

        ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS,
        EARLY_WARNING_SCORE_THRESHOLD_SET_COLOUR_DATABASE_ID,

        // Setup Mode Log
        ALL_SETUP_MODE_LOGS,
        SETUP_MODE_LOG_DATABASE_ID,
        SYNCABLE_SETUP_MODE_LOGS,
        NON_SYNCABLE_SETUP_MODE_LOGS,

        // Auditable Events
        ALL_AUDITABLE_EVENTS,
        AUDITABLE_EVENTS_DATABASE_ID,
        SYNCABLE_AUDITABLE_EVENTS,
        NON_SYNCABLE_AUDITABLE_EVENTS,

        // Server Configurable Text
        ALL_SERVER_CONFIGURABLE_TEXT,
        SERVER_CONFIGURABLE_TEXT_DATABASE_ID,

        // Wards
        ALL_WARDS,
        WARD_DATABASE_ID,

        // Beds
        ALL_BEDS,
        BED_DATABASE_ID,

        // Viewable Web Pages
        ALL_VIEWABLE_WEB_PAGES,
    }
    
    static
    {
        // Lifetouch URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_HEART_BEATS, UriType.ALL_LIFETOUCH_HEART_BEATS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_HEART_BEATS + "/#", UriType.LIFETOUCH_HEART_BEAT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_LIFETOUCH_HEART_BEATS, UriType.SYNCABLE_LIFETOUCH_HEART_BEATS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_LIFETOUCH_HEART_BEATS, UriType.NON_SYNCABLE_LIFETOUCH_HEART_BEATS.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_HEART_RATES, UriType.ALL_LIFETOUCH_HEART_RATES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_HEART_RATES + "/#", UriType.LIFETOUCH_HEART_RATE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_LIFETOUCH_HEART_RATES, UriType.SYNCABLE_LIFETOUCH_HEART_RATES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_LIFETOUCH_HEART_RATES, UriType.NON_SYNCABLE_LIFETOUCH_HEART_RATES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_RESPIRATION_RATES, UriType.ALL_LIFETOUCH_RESPIRATION_RATES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_RESPIRATION_RATES + "/#", UriType.LIFETOUCH_RESPIRATION_RATE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_LIFETOUCH_RESPIRATION_RATES, UriType.SYNCABLE_LIFETOUCH_RESPIRATION_RATES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_LIFETOUCH_RESPIRATION_RATES, UriType.NON_SYNCABLE_LIFETOUCH_RESPIRATION_RATES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_SETUP_MODE_SAMPLES, UriType.ALL_LIFETOUCH_SETUP_MODE_SAMPLES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_SETUP_MODE_SAMPLES + "/#", UriType.LIFETOUCH_SETUP_MODE_SAMPLE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES, UriType.SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES, UriType.NON_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES, UriType.ALL_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES + "/#", UriType.LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES, UriType.SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES, UriType.NON_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_BATTERY_MEASUREMENTS, UriType.ALL_LIFETOUCH_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_BATTERY_MEASUREMENTS + "/#", UriType.LIFETOUCH_BATTERY_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS, UriType.SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS, UriType.NON_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS.ordinal());
        
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_PATIENT_ORIENTATION, UriType.ALL_LIFETOUCH_PATIENT_ORIENTATIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETOUCH_PATIENT_ORIENTATION + "/#", UriType.LIFETOUCH_PATIENT_ORIENTATION_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATION , UriType.SYNCABLE_LIFETOUCH_PATIENT_ORIENTATIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATION, UriType.NON_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATIONS.ordinal());


        // Lifetemp URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETEMP_TEMPERATURE_MEASUREMENTS, UriType.ALL_LIFETEMP_TEMPERATURE_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETEMP_TEMPERATURE_MEASUREMENTS + "/#", UriType.LIFETEMP_TEMPERATURE_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS , UriType.SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS, UriType.NON_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS.ordinal());
    
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETEMP_BATTERY_MEASUREMENTS, UriType.ALL_LIFETEMP_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_LIFETEMP_BATTERY_MEASUREMENTS + "/#", UriType.LIFETEMP_BATTERY_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS , UriType.SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS , UriType.NON_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS.ordinal());


        // Oximeter URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_OXIMETER_MEASUREMENTS, UriType.ALL_OXIMETER_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_OXIMETER_MEASUREMENTS + "/#", UriType.OXIMETER_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_OXIMETER_MEASUREMENTS, UriType.SYNCABLE_OXIMETER_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_OXIMETER_MEASUREMENTS, UriType.NON_SYNCABLE_OXIMETER_MEASUREMENTS.ordinal());
        
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_OXIMETER_INTERMEDIATE_MEASUREMENTS, UriType.ALL_OXIMETER_INTERMEDIATE_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_OXIMETER_INTERMEDIATE_MEASUREMENTS + "/#", UriType.OXIMETER_INTERMEDIATE_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS, UriType.SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS, UriType.NON_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS.ordinal());
        
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_OXIMETER_SETUP_MODE_SAMPLES, UriType.ALL_OXIMETER_SETUP_MODE_SAMPLES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_OXIMETER_SETUP_MODE_SAMPLES + "/#", UriType.OXIMETER_SETUP_MODE_SAMPLE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES, UriType.SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES, UriType.NON_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_OXIMETER_BATTERY_MEASUREMENTS, UriType.ALL_OXIMETER_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_OXIMETER_BATTERY_MEASUREMENTS + "/#", UriType.OXIMETER_BATTERY_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS, UriType.SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS, UriType.NON_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS.ordinal());


        // Blood Pressure URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_BLOOD_PRESSURE_MEASUREMENTS, UriType.ALL_BLOOD_PRESSURE_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_BLOOD_PRESSURE_MEASUREMENTS + "/#", UriType.BLOOD_PRESSURE_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS, UriType.SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS, UriType.NON_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, UriType.ALL_BLOOD_PRESSURE_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_BLOOD_PRESSURE_BATTERY_MEASUREMENTS + "/#", UriType.BLOOD_PRESSURE_BATTERY_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, UriType.SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, UriType.NON_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS.ordinal());


        // Weight Scale URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_WEIGHT_SCALE_MEASUREMENTS, UriType.ALL_WEIGHT_SCALE_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_WEIGHT_SCALE_MEASUREMENTS + "/#", UriType.WEIGHT_SCALE_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS, UriType.SYNCABLE_WEIGHT_SCALE_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS, UriType.NON_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_WEIGHT_SCALE_BATTERY_MEASUREMENTS, UriType.ALL_WEIGHT_SCALE_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_WEIGHT_SCALE_BATTERY_MEASUREMENTS + "/#", UriType.WEIGHT_SCALE_BATTERY_MEASUREMENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS, UriType.SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS, UriType.NON_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS.ordinal());


        // Patient Session URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PATIENT_SESSIONS, UriType.ALL_PATIENT_SESSIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PATIENT_SESSIONS + "/#", UriType.PATIENT_SESSION_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_START_PATIENT_SESSIONS, UriType.SYNCABLE_START_PATIENT_SESSIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_START_PATIENT_SESSIONS, UriType.NON_SYNCABLE_START_PATIENT_SESSIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_END_PATIENT_SESSIONS, UriType.SYNCABLE_END_PATIENT_SESSIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_END_PATIENT_SESSIONS, UriType.NON_SYNCABLE_END_PATIENT_SESSIONS.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PATIENT_SESSIONS_FULLY_SYNCED, UriType.ALL_PATIENT_SESSION_FULLY_SYNCED.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PATIENT_SESSIONS_FULLY_SYNCED + "/#", UriType.PATIENT_SESSION_FULLY_SYNCED_DATABASE_ID.ordinal());


        // Patient Detail URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PATIENT_DETAILS, UriType.ALL_PATIENT_DETAILS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PATIENT_DETAILS + "/#", UriType.PATIENT_DETAILS_DATABASE_ID.ordinal());


        // Device Session URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DEVICE_SESSIONS, UriType.ALL_DEVICE_SESSIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DEVICE_SESSIONS + "/#", UriType.DEVICE_SESSION_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_START_DEVICE_SESSIONS, UriType.SYNCABLE_START_DEVICE_SESSIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_START_DEVICE_SESSIONS, UriType.NON_SYNCABLE_START_DEVICE_SESSIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_END_DEVICE_SESSIONS, UriType.SYNCABLE_END_DEVICE_SESSIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_END_DEVICE_SESSIONS, UriType.NON_SYNCABLE_END_DEVICE_SESSIONS.ordinal());

        // Device Info URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DEVICE_INFO, UriType.ALL_DEVICE_INFO.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DEVICE_INFO + "/#", UriType.DEVICE_INFO_DATABASE_ID.ordinal());


        // Connection Event URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_CONNECTION_EVENTS, UriType.ALL_CONNECTION_EVENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_CONNECTION_EVENTS + "/#", UriType.CONNECTION_EVENT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_CONNECTION_EVENTS, UriType.SYNCABLE_CONNECTION_EVENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_CONNECTION_EVENTS, UriType.NON_SYNCABLE_CONNECTION_EVENTS.ordinal());


        // Diagnostic Events URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DIAGNOSTICS_GATEWAY_STARTUP_TIMES, UriType.ALL_GATEWAY_STARTUP_TIMES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DIAGNOSTICS_GATEWAY_STARTUP_TIMES + "/#", UriType.GATEWAY_STARTUP_TIMES_DATABASE_ID.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DIAGNOSTICS_UI_STARTUP_TIMES, UriType.ALL_UI_STARTUP_TIMES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DIAGNOSTICS_UI_STARTUP_TIMES + "/#", UriType.UI_STARTUP_TIMES_DATABASE_ID.ordinal());


        // Manually Entered Vital Signs
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_HEART_RATES, UriType.ALL_MANUALLY_ENTERED_HEART_RATES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_HEART_RATES + "/#", UriType.MANUALLY_ENTERED_HEART_RATE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_HEART_RATES, UriType.SYNCABLE_MANUALLY_ENTERED_HEART_RATES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_HEART_RATES, UriType.NON_SYNCABLE_MANUALLY_ENTERED_HEART_RATES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_RESPIRATION_RATES, UriType.ALL_MANUALLY_ENTERED_RESPIRATION_RATES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_RESPIRATION_RATES + "/#", UriType.MANUALLY_ENTERED_RESPIRATION_RATE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES, UriType.SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES, UriType.NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_TEMPERATURE, UriType.ALL_MANUALLY_ENTERED_TEMPERATURES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_TEMPERATURE + "/#", UriType.MANUALLY_ENTERED_TEMPERATURE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES, UriType.SYNCABLE_MANUALLY_ENTERED_TEMPERATURES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES, UriType.NON_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_SPO2, UriType.ALL_MANUALLY_ENTERED_SPO2S.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_SPO2 + "/#", UriType.MANUALLY_ENTERED_SPO2_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_SPO2, UriType.SYNCABLE_MANUALLY_ENTERED_SPO2.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_SPO2, UriType.NON_SYNCABLE_MANUALLY_ENTERED_SPO2.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_BLOOD_PRESSURE, UriType.ALL_MANUALLY_ENTERED_BLOOD_PRESSURES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_BLOOD_PRESSURE + "/#", UriType.MANUALLY_ENTERED_BLOOD_PRESSURE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_BLOOD_PRESSURE, UriType.SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_BLOOD_PRESSURE, UriType.NON_SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_WEIGHT, UriType.ALL_MANUALLY_ENTERED_WEIGHTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_WEIGHT + "/#", UriType.MANUALLY_ENTERED_WEIGHT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_WEIGHT, UriType.SYNCABLE_MANUALLY_ENTERED_WEIGHTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_WEIGHT, UriType.NON_SYNCABLE_MANUALLY_ENTERED_WEIGHTS.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_CONSCIOUSNESS_LEVEL, UriType.ALL_MANUALLY_ENTERED_CONSCIOUS_LEVELS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_CONSCIOUSNESS_LEVEL + "/#", UriType.MANUALLY_ENTERED_CONSCIOUS_LEVEL_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_CONSCIOUSNESS_LEVEL, UriType.SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_CONSCIOUSNESS_LEVEL, UriType.NON_SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, UriType.ALL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL + "/#", UriType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, UriType.SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, UriType.NON_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_ANNOTATIONS, UriType.ALL_MANUALLY_ENTERED_ANNOTATIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_ANNOTATIONS + "/#", UriType.MANUALLY_ENTERED_ANNOTATION_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS, UriType.SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS, UriType.NON_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, UriType.ALL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME + "/#", UriType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, UriType.SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, UriType.NON_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_RESPIRATION_DISTRESS, UriType.ALL_MANUALLY_ENTERED_RESPIRATION_DISTRESS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_RESPIRATION_DISTRESS + "/#", UriType.MANUALLY_ENTERED_RESPIRATION_DISTRESS_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS, UriType.SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS, UriType.NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, UriType.ALL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN + "/#", UriType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, UriType.SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, UriType.NON_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_URINE_OUTPUT, UriType.ALL_MANUALLY_ENTERED_URINE_OUTPUT.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MANUALLY_ENTERED_URINE_OUTPUT + "/#", UriType.MANUALLY_ENTERED_URINE_OUTPUT_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT, UriType.SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT, UriType.NON_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT.ordinal());

        // Early Warning Scores and Threshold Sets
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORES, UriType.ALL_EARLY_WARNING_SCORES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORES + "/#", UriType.EARLY_WARNING_SCORE_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_EARLY_WARNING_SCORES, UriType.SYNCABLE_EARLY_WARNING_SCORES.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_EARLY_WARNING_SCORES, UriType.NON_SYNCABLE_EARLY_WARNING_SCORES.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SETS, UriType.ALL_EARLY_WARNING_SCORE_THRESHOLD_SETS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SETS + "/#", UriType.EARLY_WARNING_SCORE_THRESHOLD_SET_DATABASE_ID.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS, UriType.ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS + "/#", UriType.EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAIL_DATABASE_ID.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS, UriType.ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS + "/#", UriType.EARLY_WARNING_SCORE_THRESHOLD_SET_LEVEL_DATABASE_ID.ordinal());

        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS, UriType.ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS + "/#", UriType.EARLY_WARNING_SCORE_THRESHOLD_SET_COLOUR_DATABASE_ID.ordinal());

        // Setup Mode Log URI's
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SETUP_MODE_LOGS, UriType.ALL_SETUP_MODE_LOGS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SETUP_MODE_LOGS + "/#", UriType.SETUP_MODE_LOG_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_SETUP_MODE_LOGS, UriType.SYNCABLE_SETUP_MODE_LOGS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_SETUP_MODE_LOGS, UriType.NON_SYNCABLE_SETUP_MODE_LOGS.ordinal());

        // Server Configurable Text
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SERVER_CONFIGURABLE_TEXT, UriType.ALL_SERVER_CONFIGURABLE_TEXT.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SERVER_CONFIGURABLE_TEXT + "/#", UriType.SERVER_CONFIGURABLE_TEXT_DATABASE_ID.ordinal());

        // Wards
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_WARDS, UriType.ALL_WARDS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_WARDS + "/#", UriType.WARD_DATABASE_ID.ordinal());

        // Beds
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_BEDS, UriType.ALL_BEDS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_BEDS + "/#", UriType.BED_DATABASE_ID.ordinal());

        // Audit trail
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_AUDIT_TRAIL, UriType.ALL_AUDITABLE_EVENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_AUDIT_TRAIL + "/#", UriType.AUDITABLE_EVENTS_DATABASE_ID.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_SYNCABLE_AUDITABLE_EVENTS, UriType.SYNCABLE_AUDITABLE_EVENTS.ordinal());
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_NON_SYNCABLE_AUDITABLE_EVENTS, UriType.NON_SYNCABLE_AUDITABLE_EVENTS.ordinal());

        // Viewable Web Pages
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_VIEWABLE_WEB_PAGES, UriType.ALL_VIEWABLE_WEB_PAGES.ordinal());
    }

    
    @Override
    public boolean onCreate()
    {
        DatabaseHelper database = new DatabaseHelper(getContext());
    	
        db = database.getWritableDatabase();

        return false;
    }

    public void openTestDatabase(String db_location)
    {
        db.close();

        DatabaseHelper database = new DatabaseHelper(getContext(), db_location);

        db = database.getWritableDatabase();
    }

    @Override
    synchronized public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        String limit = uri.getQueryParameter(QUERY_PARAMETER_LIMIT);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriTypeAsInt = sURIMatcher.match(uri);

        UriType uriType = UriType.values()[uriTypeAsInt];
        
        switch (uriType)
        {
            case ALL_LIFETOUCH_HEART_BEATS:
            case ALL_LIFETOUCH_HEART_RATES:
            case ALL_LIFETOUCH_RESPIRATION_RATES:
            case ALL_LIFETOUCH_SETUP_MODE_SAMPLES:
            case ALL_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
            case ALL_LIFETOUCH_BATTERY_MEASUREMENTS:
            case ALL_LIFETOUCH_PATIENT_ORIENTATIONS:
            case ALL_LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case ALL_LIFETEMP_BATTERY_MEASUREMENTS:
            case ALL_OXIMETER_MEASUREMENTS:
            case ALL_OXIMETER_INTERMEDIATE_MEASUREMENTS:
            case ALL_OXIMETER_SETUP_MODE_SAMPLES:
            case ALL_OXIMETER_BATTERY_MEASUREMENTS:
            case ALL_BLOOD_PRESSURE_MEASUREMENTS:
            case ALL_BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case ALL_WEIGHT_SCALE_MEASUREMENTS:
            case ALL_WEIGHT_SCALE_BATTERY_MEASUREMENTS:
            case ALL_PATIENT_DETAILS:
            case ALL_DEVICE_SESSIONS:
            case ALL_DEVICE_INFO:
            case ALL_PATIENT_SESSIONS:
            case ALL_PATIENT_SESSION_FULLY_SYNCED:
            case ALL_CONNECTION_EVENTS:
            case ALL_GATEWAY_STARTUP_TIMES:
            case ALL_UI_STARTUP_TIMES:
            case ALL_MANUALLY_ENTERED_HEART_RATES:
            case ALL_MANUALLY_ENTERED_RESPIRATION_RATES:
            case ALL_MANUALLY_ENTERED_TEMPERATURES:
            case ALL_MANUALLY_ENTERED_SPO2S:
            case ALL_MANUALLY_ENTERED_BLOOD_PRESSURES:
            case ALL_MANUALLY_ENTERED_WEIGHTS:
            case ALL_MANUALLY_ENTERED_CONSCIOUS_LEVELS:
            case ALL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            case ALL_MANUALLY_ENTERED_ANNOTATIONS:
            case ALL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case ALL_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case ALL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case ALL_MANUALLY_ENTERED_URINE_OUTPUT:
            case ALL_EARLY_WARNING_SCORES:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SETS:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS:
            case ALL_SETUP_MODE_LOGS:
            case ALL_AUDITABLE_EVENTS:
            case ALL_SERVER_CONFIGURABLE_TEXT:
            case ALL_WARDS:
            case ALL_BEDS:
            case ALL_VIEWABLE_WEB_PAGES:
                queryBuilder.setTables(getDatabaseTableNameFromUri(uriType));
                break;

            case LIFETOUCH_HEART_BEAT_DATABASE_ID:
            case LIFETOUCH_HEART_RATE_DATABASE_ID:
            case LIFETOUCH_RESPIRATION_RATE_DATABASE_ID:
            case LIFETOUCH_SETUP_MODE_SAMPLE_DATABASE_ID:
            case LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE_DATABASE_ID:
            case LIFETOUCH_BATTERY_MEASUREMENT_DATABASE_ID:
            case LIFETOUCH_PATIENT_ORIENTATION_DATABASE_ID:
            case LIFETEMP_TEMPERATURE_MEASUREMENT_DATABASE_ID:
            case LIFETEMP_BATTERY_MEASUREMENT_DATABASE_ID:
            case OXIMETER_MEASUREMENT_DATABASE_ID:
            case OXIMETER_INTERMEDIATE_MEASUREMENT_DATABASE_ID:
            case OXIMETER_SETUP_MODE_SAMPLE_DATABASE_ID:
            case OXIMETER_BATTERY_MEASUREMENT_DATABASE_ID:
            case BLOOD_PRESSURE_MEASUREMENT_DATABASE_ID:
            case BLOOD_PRESSURE_BATTERY_MEASUREMENT_DATABASE_ID:
            case WEIGHT_SCALE_MEASUREMENT_DATABASE_ID:
            case WEIGHT_SCALE_BATTERY_MEASUREMENT_DATABASE_ID:
            case PATIENT_DETAILS_DATABASE_ID:
            case DEVICE_SESSION_DATABASE_ID:
            case DEVICE_INFO_DATABASE_ID:
            case PATIENT_SESSION_DATABASE_ID:
            case PATIENT_SESSION_FULLY_SYNCED_DATABASE_ID:
            case CONNECTION_EVENT_DATABASE_ID:
            case GATEWAY_STARTUP_TIMES_DATABASE_ID:
            case UI_STARTUP_TIMES_DATABASE_ID:
            case MANUALLY_ENTERED_HEART_RATE_DATABASE_ID:
            case MANUALLY_ENTERED_RESPIRATION_RATE_DATABASE_ID:
            case MANUALLY_ENTERED_TEMPERATURE_DATABASE_ID:
            case MANUALLY_ENTERED_SPO2_DATABASE_ID:
            case MANUALLY_ENTERED_BLOOD_PRESSURE_DATABASE_ID:
            case MANUALLY_ENTERED_WEIGHT_DATABASE_ID:
            case MANUALLY_ENTERED_CONSCIOUS_LEVEL_DATABASE_ID:
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_DATABASE_ID:
            case MANUALLY_ENTERED_ANNOTATION_DATABASE_ID:
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME_DATABASE_ID:
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS_DATABASE_ID:
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN_DATABASE_ID:
            case MANUALLY_ENTERED_URINE_OUTPUT_DATABASE_ID:
            case EARLY_WARNING_SCORE_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAIL_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_LEVEL_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_COLOUR_DATABASE_ID:
            case SETUP_MODE_LOG_DATABASE_ID:
            case SERVER_CONFIGURABLE_TEXT_DATABASE_ID:
            case WARD_DATABASE_ID:
            case BED_DATABASE_ID:
            case AUDITABLE_EVENTS_DATABASE_ID:
                queryBuilder.setTables(getDatabaseTableNameFromUri(uriType));
                queryBuilder.appendWhere(Table.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;

            case SYNCABLE_LIFETOUCH_HEART_BEATS:
            case SYNCABLE_LIFETOUCH_HEART_RATES:
            case SYNCABLE_LIFETOUCH_RESPIRATION_RATES:
            case SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES:
            case SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
            case SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS:
            case SYNCABLE_LIFETOUCH_PATIENT_ORIENTATIONS:
            case SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS:
            case SYNCABLE_OXIMETER_MEASUREMENTS:
            case SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS:
            case SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES:
            case SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS:
            case SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS:
            case SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case SYNCABLE_WEIGHT_SCALE_MEASUREMENTS:
            case SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS:
                return sensorMeasurementQuery(uri, projection, getDatabaseTableNameFromUri(uriType), selection, sortOrder, true);

            case SYNCABLE_EARLY_WARNING_SCORES:
                return earlyWarningScoreQuery(uri, projection, selection, true);

            case SYNCABLE_MANUALLY_ENTERED_HEART_RATES:
            case SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES:
            case SYNCABLE_MANUALLY_ENTERED_TEMPERATURES:
            case SYNCABLE_MANUALLY_ENTERED_SPO2:
            case SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURES:
            case SYNCABLE_MANUALLY_ENTERED_WEIGHTS:
            case SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS:
            case SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            case SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS:
            case SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT:
                return manuallyEnteredMeasurementQuery(uri, projection, getDatabaseTableNameFromUri(uriType), selection, true);

            case SYNCABLE_START_PATIENT_SESSIONS:
                return patientSessionSpecificQuery(uri, projection, true, true);
            case SYNCABLE_END_PATIENT_SESSIONS:
                return patientSessionSpecificQuery(uri, projection, false, true);

            case SYNCABLE_START_DEVICE_SESSIONS:
                return deviceSessionSpecificQuery(uri, projection, true, true);
            case SYNCABLE_END_DEVICE_SESSIONS:
                return deviceSessionSpecificQuery(uri, projection, false, true);

            case SYNCABLE_SETUP_MODE_LOGS:
                return setupModeLogSpecificQuery(uri, projection, selection, sortOrder, true);

            case SYNCABLE_AUDITABLE_EVENTS:
                return auditTrailSpecificQuery(uri, projection, selection, sortOrder, true);

            case SYNCABLE_CONNECTION_EVENTS:
                return connectionEventSpecificQuery(uri, projection, selection, true);

            case NON_SYNCABLE_LIFETOUCH_HEART_BEATS:
            case NON_SYNCABLE_LIFETOUCH_HEART_RATES:
            case NON_SYNCABLE_LIFETOUCH_RESPIRATION_RATES:
            case NON_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES:
            case NON_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
            case NON_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS:
            case NON_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATIONS:
            case NON_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case NON_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS:
            case NON_SYNCABLE_OXIMETER_MEASUREMENTS:
            case NON_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS:
            case NON_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES:
            case NON_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS:
            case NON_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS:
            case NON_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case NON_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS:
            case NON_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS:
                return sensorMeasurementQuery(uri, projection, getDatabaseTableNameFromUri(uriType), selection, sortOrder, false);

            case NON_SYNCABLE_EARLY_WARNING_SCORES:
                return earlyWarningScoreQuery(uri, projection, selection, false);

            case NON_SYNCABLE_MANUALLY_ENTERED_HEART_RATES:
            case NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES:
            case NON_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES:
            case NON_SYNCABLE_MANUALLY_ENTERED_SPO2:
            case NON_SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURES:
            case NON_SYNCABLE_MANUALLY_ENTERED_WEIGHTS:
            case NON_SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS:
            case NON_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            case NON_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS:
            case NON_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case NON_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case NON_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT:
                return manuallyEnteredMeasurementQuery(uri, projection, getDatabaseTableNameFromUri(uriType), selection, false);

            case NON_SYNCABLE_START_PATIENT_SESSIONS:
                return patientSessionSpecificQuery(uri, projection, true, false);
            case NON_SYNCABLE_END_PATIENT_SESSIONS:
                return patientSessionSpecificQuery(uri, projection, false, false);

            case NON_SYNCABLE_START_DEVICE_SESSIONS:
                return deviceSessionSpecificQuery(uri, projection, true, false);
            case NON_SYNCABLE_END_DEVICE_SESSIONS:
                return deviceSessionSpecificQuery(uri, projection, false, false);

            case NON_SYNCABLE_SETUP_MODE_LOGS:
                return setupModeLogSpecificQuery(uri, projection, selection, sortOrder, false);

            case NON_SYNCABLE_AUDITABLE_EVENTS:
                return auditTrailSpecificQuery(uri, projection, selection, sortOrder, false);

            case NON_SYNCABLE_CONNECTION_EVENTS:
                return connectionEventSpecificQuery(uri, projection, selection,false);

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    
    public static void resetSendFailedStatus(String table_name, String table_failed_to_id, String table_failed_to_send_timeStamp)
    {
        String size_query = "SELECT * FROM " + table_name + " WHERE " + table_failed_to_id + "=1 LIMIT 1";

        Cursor cursor = db.rawQuery(size_query, null);

        try
        {
            if (cursor != null && cursor.getCount() > 0)
            {
                String join = "UPDATE " + table_name + " SET ";

                String query = join + table_failed_to_id + "=0, " + table_failed_to_send_timeStamp + "=0 " + "WHERE " + table_failed_to_id + "=1";

                db.execSQL(query);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            cursor.close();
        }
    }


    public static void resetSendFailedStatus(String table_name, RowsPending active_session, RowsPending historical_session)
    {
        try
        {
            if((active_session.rows_pending_but_failed > 0) || (historical_session.rows_pending_but_failed > 0))
            {
                String join = "UPDATE " + table_name + " SET ";

                String query = join + Table.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0, " + Table.COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP + "=0 " + "WHERE " + Table.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1 AND " + Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0";

                db.execSQL(query);

                active_session.rows_pending_but_failed = 0;
                historical_session.rows_pending_but_failed = 0;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    synchronized public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Override
    synchronized public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values)
    {
        int numberOfInsertedValues = 0;

        int uriTypeAsInt = sURIMatcher.match(uri);

        UriType uriType = UriType.values()[uriTypeAsInt];

        // Start transition to allow fast DB insert
        db.beginTransaction();

        try
        {
            switch (uriType)
            {
                case ALL_LIFETOUCH_HEART_BEATS:
                case ALL_LIFETOUCH_SETUP_MODE_SAMPLES:
                case ALL_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
                case ALL_OXIMETER_INTERMEDIATE_MEASUREMENTS:
                case ALL_OXIMETER_SETUP_MODE_SAMPLES:
                    numberOfInsertedValues = bulkInsertCommonFunction(uri, getDatabaseTableNameFromUri(uriType), values);
                break;

                default:
                    numberOfInsertedValues = 0;
                break;
            }
        }
        catch (SQLException e)
        {
            Log.d(TAG, "SQL Exception = " + e);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Exception = " + e);
        }

        db.endTransaction();

        return numberOfInsertedValues;
    }

    @Override
    synchronized public Uri insert(@NonNull Uri uri, ContentValues values)
    {
        long id;
        
        Uri return_uri;
        int uriTypeAsInt = sURIMatcher.match(uri);
        
        UriType uriType = UriType.values()[uriTypeAsInt];

        id = db.insert(getDatabaseTableNameFromUri(uriType), null, values);

        switch (uriType)
        {
            case ALL_LIFETOUCH_HEART_BEATS:
                return_uri = Uri.parse(BASE_PATH_LIFETOUCH_HEART_BEATS + "/" + id);
                break;
                
            case ALL_LIFETOUCH_HEART_RATES:
                return_uri = Uri.parse(BASE_PATH_LIFETOUCH_HEART_RATES + "/" + id);
                break;
                
            case ALL_LIFETOUCH_RESPIRATION_RATES:
                return_uri = Uri.parse(BASE_PATH_LIFETOUCH_RESPIRATION_RATES + "/" + id);
                break;
                
            case ALL_LIFETOUCH_SETUP_MODE_SAMPLES:
                return_uri = Uri.parse(BASE_PATH_LIFETOUCH_SETUP_MODE_SAMPLES + "/" + id);
                break;

            case ALL_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
                return_uri = Uri.parse(BASE_PATH_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES + "/" + id);
                break;

            case ALL_LIFETOUCH_BATTERY_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_LIFETOUCH_BATTERY_MEASUREMENTS + "/" + id);
                break;

            case ALL_LIFETOUCH_PATIENT_ORIENTATIONS:
                return_uri = Uri.parse(BASE_PATH_LIFETOUCH_PATIENT_ORIENTATION + "/" + id);
                break;
                
                
                
            case ALL_LIFETEMP_TEMPERATURE_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_LIFETEMP_TEMPERATURE_MEASUREMENTS + "/" + id);
                break;

            case ALL_LIFETEMP_BATTERY_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_LIFETEMP_BATTERY_MEASUREMENTS + "/" + id);
                break;


                
            case ALL_OXIMETER_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_OXIMETER_MEASUREMENTS + "/" + id);
                break;
                
            case ALL_OXIMETER_INTERMEDIATE_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_OXIMETER_INTERMEDIATE_MEASUREMENTS + "/" + id);
                break;

            case ALL_OXIMETER_SETUP_MODE_SAMPLES:
                return_uri = Uri.parse(BASE_PATH_OXIMETER_SETUP_MODE_SAMPLES + "/" + id);
                break;

            case ALL_OXIMETER_BATTERY_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_OXIMETER_BATTERY_MEASUREMENTS + "/" + id);
                break;

                
            // Blood Pressure
            case ALL_BLOOD_PRESSURE_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_BLOOD_PRESSURE_MEASUREMENTS + "/" + id);
                break;
                
            case ALL_BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_BLOOD_PRESSURE_BATTERY_MEASUREMENTS + "/" + id);
                break;


            // Weight Scale
            case ALL_WEIGHT_SCALE_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_WEIGHT_SCALE_MEASUREMENTS + "/" + id);
                break;

            case ALL_WEIGHT_SCALE_BATTERY_MEASUREMENTS:
                return_uri = Uri.parse(BASE_PATH_WEIGHT_SCALE_MEASUREMENTS + "/" + id);
                break;



            case ALL_PATIENT_DETAILS:
                return_uri = Uri.parse(BASE_PATH_PATIENT_DETAILS + "/" + id);
                break;
                
            case ALL_DEVICE_SESSIONS:
                return_uri = Uri.parse(BASE_PATH_DEVICE_SESSIONS + "/" + id);
                break;
                
            case ALL_DEVICE_INFO:
                return_uri = Uri.parse(BASE_PATH_DEVICE_INFO + "/" + id);
                break;
                
            case ALL_PATIENT_SESSIONS:
                return_uri = Uri.parse(BASE_PATH_PATIENT_SESSIONS + "/" + id);
                break;
                
            case ALL_PATIENT_SESSION_FULLY_SYNCED:
                return_uri = Uri.parse(BASE_PATH_PATIENT_SESSIONS_FULLY_SYNCED + "/" + id);
                break;

            case ALL_CONNECTION_EVENTS:
                return_uri = Uri.parse(BASE_PATH_CONNECTION_EVENTS + "/" + id);
                break;

            case ALL_GATEWAY_STARTUP_TIMES:
                return_uri = Uri.parse(BASE_PATH_DIAGNOSTICS_GATEWAY_STARTUP_TIMES + "/" + id);
                break;

            case ALL_UI_STARTUP_TIMES:
                return_uri = Uri.parse(BASE_PATH_DIAGNOSTICS_UI_STARTUP_TIMES + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_HEART_RATES:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_HEART_RATES + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_RESPIRATION_RATES:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_RESPIRATION_RATES + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_TEMPERATURES:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_TEMPERATURE + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_SPO2S:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_SPO2 + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_BLOOD_PRESSURES:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_BLOOD_PRESSURE + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_WEIGHTS:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_WEIGHT + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_CONSCIOUS_LEVELS:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_CONSCIOUSNESS_LEVEL + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_ANNOTATIONS:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_ANNOTATIONS + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_RESPIRATION_DISTRESS + "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN+ "/" + id);
                break;

            case ALL_MANUALLY_ENTERED_URINE_OUTPUT:
                return_uri = Uri.parse(BASE_PATH_MANUALLY_ENTERED_URINE_OUTPUT + "/" + id);
                break;

            case ALL_EARLY_WARNING_SCORES:
                return_uri = Uri.parse(BASE_PATH_EARLY_WARNING_SCORES + "/" + id);
                break;

            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SETS:
                return_uri = Uri.parse(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SETS + "/" + id);
                break;

            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS:
                return_uri = Uri.parse(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS + "/" + id);
                break;

            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS:
                return_uri = Uri.parse(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS + "/" + id);
                break;

            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS:
                return_uri = Uri.parse(BASE_PATH_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS + "/" + id);
                break;

            case ALL_SETUP_MODE_LOGS:
                return_uri = Uri.parse(BASE_PATH_SETUP_MODE_LOGS + "/" + id);
                break;

            case ALL_SERVER_CONFIGURABLE_TEXT:
                return_uri = Uri.parse(BASE_PATH_SERVER_CONFIGURABLE_TEXT + "/" + id);
                break;

            case ALL_WARDS:
                return_uri = Uri.parse(BASE_PATH_WARDS + "/" + id);
                break;

            case ALL_BEDS:
                return_uri = Uri.parse(BASE_PATH_BEDS + "/" + id);
                break;

            case ALL_AUDITABLE_EVENTS:
                return_uri = Uri.parse(BASE_PATH_AUDIT_TRAIL + "/" + id);
                break;

            case ALL_VIEWABLE_WEB_PAGES:
                return_uri = Uri.parse(BASE_PATH_VIEWABLE_WEB_PAGES + "/" + id);
                break;

            default:
            {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return return_uri;
    }
    

    @Override
    synchronized public int delete(@NonNull Uri uri, String selection, String[] selectionArgs)
    {
        int uriTypeAsInt = sURIMatcher.match(uri);
        UriType uriType = UriType.values()[uriTypeAsInt];
        
        int rowsDeleted;
        
        switch (uriType)
        {
            case ALL_LIFETOUCH_HEART_BEATS:
            case ALL_LIFETOUCH_HEART_RATES:
            case ALL_LIFETOUCH_RESPIRATION_RATES:
            case ALL_LIFETOUCH_SETUP_MODE_SAMPLES:
            case ALL_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
            case ALL_LIFETOUCH_BATTERY_MEASUREMENTS:
            case ALL_LIFETOUCH_PATIENT_ORIENTATIONS:
            case ALL_LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case ALL_LIFETEMP_BATTERY_MEASUREMENTS:
            case ALL_OXIMETER_MEASUREMENTS:
            case ALL_OXIMETER_INTERMEDIATE_MEASUREMENTS:
            case ALL_OXIMETER_SETUP_MODE_SAMPLES:
            case ALL_OXIMETER_BATTERY_MEASUREMENTS:
            case ALL_BLOOD_PRESSURE_MEASUREMENTS:
            case ALL_BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case ALL_WEIGHT_SCALE_MEASUREMENTS:
            case ALL_WEIGHT_SCALE_BATTERY_MEASUREMENTS:
            case ALL_PATIENT_DETAILS:
            case ALL_DEVICE_SESSIONS:
            case ALL_DEVICE_INFO:
            case ALL_PATIENT_SESSIONS:
            case ALL_PATIENT_SESSION_FULLY_SYNCED:
            case ALL_CONNECTION_EVENTS:
            case ALL_GATEWAY_STARTUP_TIMES:
            case ALL_UI_STARTUP_TIMES:
            case ALL_MANUALLY_ENTERED_HEART_RATES:
            case ALL_MANUALLY_ENTERED_RESPIRATION_RATES:
            case ALL_MANUALLY_ENTERED_TEMPERATURES:
            case ALL_MANUALLY_ENTERED_SPO2S:
            case ALL_MANUALLY_ENTERED_BLOOD_PRESSURES:
            case ALL_MANUALLY_ENTERED_WEIGHTS:
            case ALL_MANUALLY_ENTERED_CONSCIOUS_LEVELS:
            case ALL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            case ALL_MANUALLY_ENTERED_ANNOTATIONS:
            case ALL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case ALL_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case ALL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case ALL_MANUALLY_ENTERED_URINE_OUTPUT:
            case ALL_EARLY_WARNING_SCORES:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SETS:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS:
            case ALL_SETUP_MODE_LOGS:
            case ALL_SERVER_CONFIGURABLE_TEXT:
            case ALL_WARDS:
            case ALL_BEDS:
            case ALL_AUDITABLE_EVENTS:
            case ALL_VIEWABLE_WEB_PAGES:
            {
                rowsDeleted = db.delete(getDatabaseTableNameFromUri(uriType), selection, selectionArgs);
            }
            break;

            case LIFETOUCH_HEART_BEAT_DATABASE_ID:
            case LIFETOUCH_HEART_RATE_DATABASE_ID:
            case LIFETOUCH_RESPIRATION_RATE_DATABASE_ID:
            case LIFETOUCH_SETUP_MODE_SAMPLE_DATABASE_ID:
            case LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE_DATABASE_ID:
            case LIFETOUCH_BATTERY_MEASUREMENT_DATABASE_ID:
            case LIFETOUCH_PATIENT_ORIENTATION_DATABASE_ID:
            case LIFETEMP_TEMPERATURE_MEASUREMENT_DATABASE_ID:
            case LIFETEMP_BATTERY_MEASUREMENT_DATABASE_ID:
            case OXIMETER_MEASUREMENT_DATABASE_ID:
            case OXIMETER_INTERMEDIATE_MEASUREMENT_DATABASE_ID:
            case OXIMETER_SETUP_MODE_SAMPLE_DATABASE_ID:
            case OXIMETER_BATTERY_MEASUREMENT_DATABASE_ID:
            case BLOOD_PRESSURE_MEASUREMENT_DATABASE_ID:
            case BLOOD_PRESSURE_BATTERY_MEASUREMENT_DATABASE_ID:
            case WEIGHT_SCALE_MEASUREMENT_DATABASE_ID:
            case WEIGHT_SCALE_BATTERY_MEASUREMENT_DATABASE_ID:
            case PATIENT_DETAILS_DATABASE_ID:
            case DEVICE_SESSION_DATABASE_ID:
            case DEVICE_INFO_DATABASE_ID:
            case PATIENT_SESSION_DATABASE_ID:
            case PATIENT_SESSION_FULLY_SYNCED_DATABASE_ID:
            case CONNECTION_EVENT_DATABASE_ID:
            case GATEWAY_STARTUP_TIMES_DATABASE_ID:
            case UI_STARTUP_TIMES_DATABASE_ID:
            case MANUALLY_ENTERED_HEART_RATE_DATABASE_ID:
            case MANUALLY_ENTERED_RESPIRATION_RATE_DATABASE_ID:
            case MANUALLY_ENTERED_TEMPERATURE_DATABASE_ID:
            case MANUALLY_ENTERED_SPO2_DATABASE_ID:
            case MANUALLY_ENTERED_BLOOD_PRESSURE_DATABASE_ID:
            case MANUALLY_ENTERED_WEIGHT_DATABASE_ID:
            case MANUALLY_ENTERED_CONSCIOUS_LEVEL_DATABASE_ID:
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_DATABASE_ID:
            case MANUALLY_ENTERED_ANNOTATION_DATABASE_ID:
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME_DATABASE_ID:
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS_DATABASE_ID:
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN_DATABASE_ID:
            case MANUALLY_ENTERED_URINE_OUTPUT_DATABASE_ID:
            case EARLY_WARNING_SCORE_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAIL_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_LEVEL_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_COLOUR_DATABASE_ID:
            case SETUP_MODE_LOG_DATABASE_ID:
            case WARD_DATABASE_ID:
            case BED_DATABASE_ID:
            case AUDITABLE_EVENTS_DATABASE_ID:
            {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                {
                    rowsDeleted = db.delete(getDatabaseTableNameFromUri(uriType), Table.COLUMN_ID + "=" + id, null);
                }
                else
                {
                    rowsDeleted = db.delete(getDatabaseTableNameFromUri(uriType), Table.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
            }
            break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }
    

    @Override
    synchronized public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int uriTypeAsInt = sURIMatcher.match(uri);
        UriType uriType = UriType.values()[uriTypeAsInt];
        
        int rowsUpdated;
        
        boolean notify_update = false;
        
        switch (uriType)
        {
            case ALL_DEVICE_SESSIONS:
            case ALL_PATIENT_SESSIONS:
            {
                if(values.containsKey(TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME) ||
                        values.containsKey(TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME))
                {
                    notify_update = true;
                }
            }

            case ALL_LIFETOUCH_HEART_BEATS:
            case ALL_LIFETOUCH_HEART_RATES:
            case ALL_LIFETOUCH_RESPIRATION_RATES:
            case ALL_LIFETOUCH_SETUP_MODE_SAMPLES:
            case ALL_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
            case ALL_LIFETOUCH_BATTERY_MEASUREMENTS:
            case ALL_LIFETOUCH_PATIENT_ORIENTATIONS:
            case ALL_LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case ALL_LIFETEMP_BATTERY_MEASUREMENTS:
            case ALL_OXIMETER_MEASUREMENTS:
            case ALL_OXIMETER_INTERMEDIATE_MEASUREMENTS:
            case ALL_OXIMETER_SETUP_MODE_SAMPLES:
            case ALL_OXIMETER_BATTERY_MEASUREMENTS:
            case ALL_BLOOD_PRESSURE_MEASUREMENTS:
            case ALL_BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case ALL_WEIGHT_SCALE_MEASUREMENTS:
            case ALL_WEIGHT_SCALE_BATTERY_MEASUREMENTS:
            case ALL_PATIENT_DETAILS:
            case ALL_DEVICE_INFO:
            case ALL_PATIENT_SESSION_FULLY_SYNCED:
            case ALL_CONNECTION_EVENTS:
            case ALL_GATEWAY_STARTUP_TIMES:
            case ALL_UI_STARTUP_TIMES:
            case ALL_MANUALLY_ENTERED_HEART_RATES:
            case ALL_MANUALLY_ENTERED_RESPIRATION_RATES:
            case ALL_MANUALLY_ENTERED_TEMPERATURES:
            case ALL_MANUALLY_ENTERED_SPO2S:
            case ALL_MANUALLY_ENTERED_BLOOD_PRESSURES:
            case ALL_MANUALLY_ENTERED_WEIGHTS:
            case ALL_MANUALLY_ENTERED_CONSCIOUS_LEVELS:
            case ALL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            case ALL_MANUALLY_ENTERED_ANNOTATIONS:
            case ALL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case ALL_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case ALL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case ALL_MANUALLY_ENTERED_URINE_OUTPUT:
            case ALL_EARLY_WARNING_SCORES:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SETS:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS:
            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS:
            case ALL_SETUP_MODE_LOGS:
            case ALL_SERVER_CONFIGURABLE_TEXT:
            case ALL_WARDS:
            case ALL_BEDS:
            case ALL_AUDITABLE_EVENTS:
            case ALL_VIEWABLE_WEB_PAGES:
            {
                rowsUpdated = db.update(getDatabaseTableNameFromUri(uriType), values, selection, selectionArgs);
            }
            break;

            case DEVICE_SESSION_DATABASE_ID:
            case PATIENT_SESSION_DATABASE_ID:
            {
                if(values.containsKey(TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME) ||
                        values.containsKey(TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME))
                {
                    notify_update = true;
                }
            }

            case LIFETOUCH_HEART_BEAT_DATABASE_ID:
            case LIFETOUCH_HEART_RATE_DATABASE_ID:
            case LIFETOUCH_RESPIRATION_RATE_DATABASE_ID:
            case LIFETOUCH_SETUP_MODE_SAMPLE_DATABASE_ID:
            case LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE_DATABASE_ID:
            case LIFETOUCH_BATTERY_MEASUREMENT_DATABASE_ID:
            case LIFETOUCH_PATIENT_ORIENTATION_DATABASE_ID:
            case LIFETEMP_TEMPERATURE_MEASUREMENT_DATABASE_ID:
            case LIFETEMP_BATTERY_MEASUREMENT_DATABASE_ID:
            case OXIMETER_MEASUREMENT_DATABASE_ID:
            case OXIMETER_INTERMEDIATE_MEASUREMENT_DATABASE_ID:
            case OXIMETER_SETUP_MODE_SAMPLE_DATABASE_ID:
            case OXIMETER_BATTERY_MEASUREMENT_DATABASE_ID:
            case BLOOD_PRESSURE_MEASUREMENT_DATABASE_ID:
            case BLOOD_PRESSURE_BATTERY_MEASUREMENT_DATABASE_ID:
            case WEIGHT_SCALE_MEASUREMENT_DATABASE_ID:
            case WEIGHT_SCALE_BATTERY_MEASUREMENT_DATABASE_ID:
            case PATIENT_DETAILS_DATABASE_ID:
            case DEVICE_INFO_DATABASE_ID:
            case PATIENT_SESSION_FULLY_SYNCED_DATABASE_ID:
            case CONNECTION_EVENT_DATABASE_ID:
            case GATEWAY_STARTUP_TIMES_DATABASE_ID:
            case UI_STARTUP_TIMES_DATABASE_ID:
            case MANUALLY_ENTERED_HEART_RATE_DATABASE_ID:
            case MANUALLY_ENTERED_RESPIRATION_RATE_DATABASE_ID:
            case MANUALLY_ENTERED_TEMPERATURE_DATABASE_ID:
            case MANUALLY_ENTERED_SPO2_DATABASE_ID:
            case MANUALLY_ENTERED_BLOOD_PRESSURE_DATABASE_ID:
            case MANUALLY_ENTERED_WEIGHT_DATABASE_ID:
            case MANUALLY_ENTERED_CONSCIOUS_LEVEL_DATABASE_ID:
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_DATABASE_ID:
            case MANUALLY_ENTERED_ANNOTATION_DATABASE_ID:
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME_DATABASE_ID:
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS_DATABASE_ID:
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN_DATABASE_ID:
            case MANUALLY_ENTERED_URINE_OUTPUT_DATABASE_ID:
            case EARLY_WARNING_SCORE_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAIL_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_LEVEL_DATABASE_ID:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_COLOUR_DATABASE_ID:
            case SETUP_MODE_LOG_DATABASE_ID:
            case SERVER_CONFIGURABLE_TEXT_DATABASE_ID:
            case WARD_DATABASE_ID:
            case BED_DATABASE_ID:
            case AUDITABLE_EVENTS_DATABASE_ID:
            {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection))
                {
                    rowsUpdated = db.update(getDatabaseTableNameFromUri(uriType), values, Table.COLUMN_ID + "=" + id, null);
                }
                else
                {
                    rowsUpdated = db.update(getDatabaseTableNameFromUri(uriType), values, Table.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
            }
            break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        if(notify_update)
        {
        	getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
    
    
    @Override
    public synchronized Bundle call(@NonNull String method, String arg, Bundle extras)
    {
        switch (method)
        {
            case CALLABLE_METHOD__GET_COUNT:
                return getCount(extras);

            case CALLABLE_METHOD__OPEN_TEST_DATABASE:
                openTestDatabase(arg);
                return null;

            default:
                return null;
        }
    }
    
    
    private Bundle getCount(Bundle extras)
    {
        String table = extras.getString("table");
        String selection = extras.getString("selection");

        long count = DatabaseUtils.queryNumEntries(db, table, selection);

        Bundle return_bundle = new Bundle();

        return_bundle.putLong("count", count);

        return return_bundle;
    }


    private String mergeProjectionIntoSingleLine(String[] projection, String append_string)
    {
        StringBuilder projection_single_line = new StringBuilder();

        for (String line : projection)
        {
            projection_single_line.append(append_string).append(line).append(",");
        }

        projection_single_line = new StringBuilder(projection_single_line.substring(0, projection_single_line.length() - 1));    // Remove last comma on the end

        return projection_single_line.toString();
    }


    private Cursor patientSessionSpecificQuery(Uri uri, String[] projection, boolean start_patient_session, boolean syncable)
    {
        String projection_single_line = mergeProjectionIntoSingleLine(projection, "ps.");

        String select = "SELECT " + projection_single_line + " FROM " + TablePatientSession.TABLE_NAME + " ps ";
        String join;
        String where;

        if (start_patient_session)
        {
            if(syncable)
            {
                join = "INNER JOIN " + TablePatientDetails.TABLE_NAME + " pd ON ps." + TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID + "= pd." + TablePatientDetails.COLUMN_ID;
                where = " WHERE pd." + TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1 ";
            }
            else
            {
                join = "LEFT OUTER JOIN " + TablePatientDetails.TABLE_NAME + " pd ON ps." + TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID + "= pd." + TablePatientDetails.COLUMN_ID;
                where = " WHERE (pd." + TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0) ";
            }

            where += " AND ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 ";
            where += " AND ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED + "=0";
        }
        else
        {
            if(syncable)
            {
                join = "INNER JOIN " + TablePatientDetails.TABLE_NAME + " pd ON ps." + TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID + "= pd." + TablePatientDetails.COLUMN_ID;
                where = " WHERE pd." + TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1 ";

                // Cant attempt to send a End Patient Session until the Start Patient Session has been sent to the Server. Otherwise the COLUMN_SERVERS_ID column in NULL, so when there database query code tries to parse it
                // it to a number, it will get a NULL exception
                where += " AND ps." + TablePatientSession.COLUMN_SERVERS_ID + " IS NOT NULL ";
            }
            else
            {
                join = "LEFT OUTER JOIN " + TablePatientDetails.TABLE_NAME + " pd ON ps." + TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID + "= pd." + TablePatientDetails.COLUMN_ID;
                where = " WHERE (pd." + TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0) ";
            }

            where += " AND ps." + TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 ";
            where += " AND ps." + TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME + " IS NOT 0 ";
            where += " AND ps." + TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED + "=0";
        }

        String query = select + join + where;

        //Log.d(TAG, "patientSessionSpecificQuery : Query = " + query);

        return runRawQuery(uri, query);
    }

    private Cursor deviceSessionSpecificQuery(Uri uri, String[] projection, boolean start_device_session, boolean syncable)
    {
        String projection_single_line = mergeProjectionIntoSingleLine(projection, "ds.");

        String select = "SELECT " + projection_single_line + " FROM " + TableDeviceSession.TABLE_NAME + " ds ";
        String join_one;
        String join_two;

        String where;

        String query;

        if (start_device_session)
        {
            if(syncable)
            {
                join_one = " INNER JOIN " + TablePatientSession.TABLE_NAME + " ps ON ds." + TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID + "= ps." + TablePatientSession.COLUMN_ID;
                join_two = " INNER JOIN " + TableDeviceInfo.TABLE_NAME + " di ON ds." + TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID + "= di." + TableDeviceInfo.COLUMN_ID;
                where = " WHERE ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1 AND di." + TableDeviceInfo.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
            }
            else
            {
                join_one = " LEFT OUTER JOIN " + TablePatientSession.TABLE_NAME + " ps ON ds." + TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID + "= ps." + TablePatientSession.COLUMN_ID;
                join_two = " LEFT OUTER JOIN " + TableDeviceInfo.TABLE_NAME + " di ON ds." + TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID + "= di." + TableDeviceInfo.COLUMN_ID;
                where = " WHERE ((ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " =0)";
                where += " OR (di." + TableDeviceInfo.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " =0))";
            }

            where += " AND ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 ";
            where += " AND ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED + "=0";

            query = select + join_one + join_two + where;
        }
        else
        {
            if(syncable)
            {
                where = " WHERE ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
                where += " AND ds." + TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " =0 ";
                where += " AND ds." + TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME + " IS NOT 0 ";
                where += " AND ds." + TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED + " =0 ";

                // Cant attempt to send a End Device Session until the Start Device Session has been sent to the Server. Otherwise the COLUMN_SERVERS_ID column in NULL, so when there database query code tries to parse it
                // it to a number, it will get a NULL exception
                where += " AND ds." + TableDeviceSession.COLUMN_SERVERS_ID + " IS NOT NULL ";
            }
            else
            {
                where = " WHERE ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 ";
                where += " AND ds." + TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " =0 ";
                where += " AND ds." + TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME + " IS NOT 0 ";
                where += " AND ds." + TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED + " =0 ";
            }

            query = select + where;
        }

        //Log.d(TAG, "deviceSessionSpecificQuery : Query = " + query);

        return runRawQuery(uri, query);
    }


    private Cursor setupModeLogSpecificQuery(Uri uri, String[] projection, String selection, String sort, boolean syncable)
    {
        String projection_single_line = mergeProjectionIntoSingleLine(projection, "x.");

        String select = "SELECT " + projection_single_line + " FROM " + TableSetupModeLog.TABLE_NAME + " x ";

        String join;

        String where;

        String query;

        if(syncable)
        {
            join = " INNER JOIN " + TableDeviceSession.TABLE_NAME + " ds ON x." + TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER + " = ds." + TableDeviceSession.COLUMN_ID;
            where = " WHERE ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
        }
        else
        {
            join = " LEFT OUTER JOIN " + TableDeviceSession.TABLE_NAME + " ds ON x." + TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER + " = ds." + TableDeviceSession.COLUMN_ID;
            where = " WHERE (ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " =0)";
        }

        where += " AND x." + TableSetupModeLog.COLUMN_END_SETUP_MODE_TIME + " IS NOT 0 ";
        where += " AND x." + TableSetupModeLog.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 ";
        where += " AND x." + TableSetupModeLog.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";

        String sortOrder = "";

        if(sort != null)
        {
            sortOrder = " ORDER BY x." + TableSetupModeLog.COLUMN_ID + sort;
        }

        query = select + join + where + selection + sortOrder;

        return runRawQuery(uri, query);
    }


    private Cursor auditTrailSpecificQuery(Uri uri, String[] projection, String selection, String sort, boolean syncable)
    {
        String projection_single_line = mergeProjectionIntoSingleLine(projection, "x.");

        String select = "SELECT " + projection_single_line + " FROM " + TableAuditTrail.TABLE_NAME + " x ";

        String join;

        String where;

        String query;

        if(syncable)
        {
            join = " INNER JOIN " + TableDeviceSession.TABLE_NAME /* + " ds ON x." + TableAuditTrail.COLUMN_DEVICE_SESSION_NUMBER*/ + " = ds." + TableDeviceSession.COLUMN_ID;
            where = " WHERE ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
        }
        else
        {
            join = " LEFT OUTER JOIN " + TableDeviceSession.TABLE_NAME /*+ " ds ON x." + TableAuditTrail.COLUMN_DEVICE_SESSION_NUMBER*/ + " = ds." + TableDeviceSession.COLUMN_ID;
            where = " WHERE (ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + " =0)";
        }

        where += " AND x." + TableAuditTrail.COLUMN_TIMESTAMP + " IS NOT 0 ";
        where += " AND x." + TableAuditTrail.COLUMN_EVENT + "=0 ";
        where += " AND x." + TableAuditTrail.COLUMN_BY_USER_ID + "=0";

        String sortOrder = "";

        if(sort != null)
        {
            sortOrder = " ORDER BY x." + TableAuditTrail.COLUMN_ID + sort;
        }

        query = select + join + where + selection + sortOrder;

        return runRawQuery(uri, query);
    }


    private Cursor connectionEventSpecificQuery(Uri uri, String[] projection, String selection, boolean syncable)
    {
        String projection_single_line = mergeProjectionIntoSingleLine(projection, "measurement.");

        String select = "SELECT " + projection_single_line + " FROM " + TableConnectionEvent.TABLE_NAME + " measurement ";
        String join_one;
        String join_two;
        String where;

        if(syncable)
        {
            join_one = " INNER JOIN " + TablePatientSession.TABLE_NAME + " ps ON measurement." + TableConnectionEvent.COLUMN_PATIENT_SESSION_NUMBER + "= ps." + TablePatientSession.COLUMN_ID;
            join_two = " INNER JOIN " + TableDeviceSession.TABLE_NAME + " ds ON measurement." + TableConnectionEvent.COLUMN_DEVICE_SESSION_NUMBER + "= ds." + TableDeviceSession.COLUMN_ID;
            where = " WHERE ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
            where += " AND ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
        }
        else
        {
            join_one = " LEFT OUTER JOIN " + TablePatientSession.TABLE_NAME + " ps ON measurement." + TableConnectionEvent.COLUMN_PATIENT_SESSION_NUMBER + "= ps." + TablePatientSession.COLUMN_ID;
            join_two = " LEFT OUTER JOIN " + TableDeviceSession.TABLE_NAME + " ds ON measurement." + TableConnectionEvent.COLUMN_DEVICE_SESSION_NUMBER + "= ds." + TableDeviceSession.COLUMN_ID;
            where = " WHERE ((ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0)";
            where += " OR (ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0))";
        }

        where += " AND measurement." + TableConnectionEvent.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0";
        where += " AND measurement." + TableConnectionEvent.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";

        String query = select + join_one + join_two + where + selection;

        return runRawQuery(uri, query);
    }


    private Cursor sensorMeasurementQuery(Uri uri, String[] projection, String measurement_table, String selection, String sort, boolean syncable)
    {
        String projection_single_line = mergeProjectionIntoSingleLine(projection, "measurement.");

        String select = "SELECT " + projection_single_line + " FROM " + TableDeviceSession.TABLE_NAME + " ds ";
        String join = " CROSS JOIN " +  measurement_table + " measurement ON measurement." + TableSensorMeasurement.COLUMN_DEVICE_SESSION_NUMBER + "= ds." + TableDeviceSession.COLUMN_ID;

        String where;

        if(syncable)
        {
            where = " WHERE ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
        }
        else
        {
            where = " WHERE (ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0)";
        }

        where += selection;

        where += " AND measurement." + TableSensorMeasurement.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0";
        where += " AND measurement." + TableSensorMeasurement.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";

        String sortOrder = " ORDER BY measurement." + TableSensorMeasurement.COLUMN_ID + sort;

        String query = select + join + where + sortOrder;

        return runRawQuery(uri, query);
    }


    private Cursor manuallyEnteredMeasurementQuery(Uri uri, String[] projection, String measurement_table, String selection, boolean syncable)
    {
        String projection_single_line = mergeProjectionIntoSingleLine(projection, "measurement.");

        String select = "SELECT " + projection_single_line + " FROM " + measurement_table + " measurement ";
        String join;
        String where;

        if(syncable)
        {
            join = " INNER JOIN " + TablePatientSession.TABLE_NAME + " ps ON measurement." + TableManuallyEnteredMeasurement.COLUMN_PATIENT_SESSION_NUMBER + "= ps." + TablePatientSession.COLUMN_ID;
            where = " WHERE ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
        }
        else
        {
            join = " LEFT OUTER JOIN " + TablePatientSession.TABLE_NAME + " ps ON measurement." + TableManuallyEnteredMeasurement.COLUMN_PATIENT_SESSION_NUMBER + "= ps." + TablePatientSession.COLUMN_ID;
            where = " WHERE (ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0)";
        }

        where += selection;
        where += " AND measurement." + TableManuallyEnteredMeasurement.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0";
        where += " AND measurement." + TableManuallyEnteredMeasurement.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";
        String query = select + join + where;

        return runRawQuery(uri, query);
    }


    private Cursor earlyWarningScoreQuery(Uri uri, String[] projection, String selection, boolean syncable)
    {
        String projection_single_line = mergeProjectionIntoSingleLine(projection, "measurement.");

        String select = "SELECT " + projection_single_line + " FROM " + TableEarlyWarningScore.TABLE_NAME + " measurement ";
        String join_one;
        String join_two;
        String where;

        if(syncable)
        {
            join_one = " INNER JOIN " + TablePatientSession.TABLE_NAME + " ps ON measurement." + TableEarlyWarningScore.COLUMN_PATIENT_SESSION_NUMBER + "= ps." + TablePatientSession.COLUMN_ID;
            join_two = " INNER JOIN " + TableDeviceSession.TABLE_NAME + " ds ON measurement." + TableEarlyWarningScore.COLUMN_DEVICE_SESSION_NUMBER + "= ds." + TableDeviceSession.COLUMN_ID;
            where = " WHERE ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
            where += " AND ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
        }
        else
        {
            join_one = " LEFT OUTER JOIN " + TablePatientSession.TABLE_NAME + " ps ON measurement." + TableEarlyWarningScore.COLUMN_PATIENT_SESSION_NUMBER + "= ps." + TablePatientSession.COLUMN_ID;
            join_two = " LEFT OUTER JOIN " + TableDeviceSession.TABLE_NAME + " ds ON measurement." + TableEarlyWarningScore.COLUMN_DEVICE_SESSION_NUMBER + "= ds." + TableDeviceSession.COLUMN_ID;
            where = " WHERE ((ps." + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0)";
            where += " OR ds." + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0)";
        }

        where += selection;
        where += " AND measurement." + TableEarlyWarningScore.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0";
        where += " AND " + TableEarlyWarningScore.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";

        String query = select + join_one + join_two + where;

        return runRawQuery(uri, query);
    }


    private Cursor runRawQuery(Uri uri, String query)
    {
        Log.d("SQL", "Raw Query = " + query);

        Cursor cursor = db.rawQuery(query, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    private int bulkInsertCommonFunction(Uri uri, String tableName, ContentValues[] values)
    {
        int numberOfInsertedValues = 0;

        for(ContentValues cv : values)
        {
            long return_id = db.insertOrThrow(tableName, null, cv);

            if(return_id <= 0)
            {
                throw new SQLException("Failed to insert row...  URI = " + uri);
            }
            else
            {
                numberOfInsertedValues++;
            }
        }

        db.setTransactionSuccessful();

        getContext().getContentResolver().notifyChange(uri, null);

        return numberOfInsertedValues;
    }


    private static String getDatabaseTableNameFromUri(UriType uri_type)
    {
        switch (uri_type)
        {
            case ALL_LIFETOUCH_HEART_BEATS:
            case LIFETOUCH_HEART_BEAT_DATABASE_ID:
            case SYNCABLE_LIFETOUCH_HEART_BEATS:
            case NON_SYNCABLE_LIFETOUCH_HEART_BEATS:
                return TableLifetouchHeartBeat.TABLE_NAME;

            case ALL_LIFETOUCH_HEART_RATES:
            case LIFETOUCH_HEART_RATE_DATABASE_ID:
            case SYNCABLE_LIFETOUCH_HEART_RATES:
            case NON_SYNCABLE_LIFETOUCH_HEART_RATES:
                return TableLifetouchHeartRate.TABLE_NAME;

            case ALL_LIFETOUCH_RESPIRATION_RATES:
            case LIFETOUCH_RESPIRATION_RATE_DATABASE_ID:
            case SYNCABLE_LIFETOUCH_RESPIRATION_RATES:
            case NON_SYNCABLE_LIFETOUCH_RESPIRATION_RATES:
                return TableLifetouchRespirationRate.TABLE_NAME;

            case ALL_LIFETOUCH_SETUP_MODE_SAMPLES:
            case LIFETOUCH_SETUP_MODE_SAMPLE_DATABASE_ID:
            case SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES:
            case NON_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES:
                return TableLifetouchSetupModeRawSample.TABLE_NAME;

            case ALL_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
            case LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE_DATABASE_ID:
            case SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
            case NON_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES:
                return TableLifetouchRawAccelerometerModeSample.TABLE_NAME;

            case ALL_LIFETOUCH_BATTERY_MEASUREMENTS:
            case LIFETOUCH_BATTERY_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS:
            case NON_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS:
                return TableLifetouchBattery.TABLE_NAME;

            case ALL_LIFETOUCH_PATIENT_ORIENTATIONS:
            case LIFETOUCH_PATIENT_ORIENTATION_DATABASE_ID:
            case SYNCABLE_LIFETOUCH_PATIENT_ORIENTATIONS:
            case NON_SYNCABLE_LIFETOUCH_PATIENT_ORIENTATIONS:
                return TableLifetouchPatientOrientation.TABLE_NAME;


            case ALL_LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case LIFETEMP_TEMPERATURE_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case NON_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS:
                return TableLifetempMeasurement.TABLE_NAME;

            case ALL_LIFETEMP_BATTERY_MEASUREMENTS:
            case LIFETEMP_BATTERY_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS:
            case NON_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS:
                return TableLifetempBattery.TABLE_NAME;


            case ALL_OXIMETER_MEASUREMENTS:
            case OXIMETER_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_OXIMETER_MEASUREMENTS:
            case NON_SYNCABLE_OXIMETER_MEASUREMENTS:
                return TableOximeterMeasurement.TABLE_NAME;

            case ALL_OXIMETER_INTERMEDIATE_MEASUREMENTS:
            case OXIMETER_INTERMEDIATE_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS:
            case NON_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS:
                return TableOximeterIntermediateMeasurement.TABLE_NAME;

            case ALL_OXIMETER_SETUP_MODE_SAMPLES:
            case OXIMETER_SETUP_MODE_SAMPLE_DATABASE_ID:
            case SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES:
            case NON_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES:
                return TableOximeterSetupModeRawSample.TABLE_NAME;

            case ALL_OXIMETER_BATTERY_MEASUREMENTS:
            case OXIMETER_BATTERY_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS:
            case NON_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS:
                return TableOximeterBattery.TABLE_NAME;

            case ALL_BLOOD_PRESSURE_MEASUREMENTS:
            case BLOOD_PRESSURE_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS:
            case NON_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS:
                return TableBloodPressureMeasurement.TABLE_NAME;

            case ALL_BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case BLOOD_PRESSURE_BATTERY_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case NON_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
                return TableBloodPressureBattery.TABLE_NAME;

            case ALL_WEIGHT_SCALE_MEASUREMENTS:
            case WEIGHT_SCALE_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_WEIGHT_SCALE_MEASUREMENTS:
            case NON_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS:
                return TableWeightScaleWeight.TABLE_NAME;

            case ALL_WEIGHT_SCALE_BATTERY_MEASUREMENTS:
            case WEIGHT_SCALE_BATTERY_MEASUREMENT_DATABASE_ID:
            case SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS:
            case NON_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS:
                return TableWeightScaleBattery.TABLE_NAME;

            case ALL_PATIENT_DETAILS:
            case PATIENT_DETAILS_DATABASE_ID:
                return TablePatientDetails.TABLE_NAME;

            case ALL_DEVICE_INFO:
            case DEVICE_INFO_DATABASE_ID:
                return TableDeviceInfo.TABLE_NAME;

            case ALL_DEVICE_SESSIONS:
            case DEVICE_SESSION_DATABASE_ID:
            case SYNCABLE_START_DEVICE_SESSIONS:
            case NON_SYNCABLE_START_DEVICE_SESSIONS:
            case SYNCABLE_END_DEVICE_SESSIONS:
            case NON_SYNCABLE_END_DEVICE_SESSIONS:
                return TableDeviceSession.TABLE_NAME;

            case ALL_PATIENT_SESSIONS:
            case PATIENT_SESSION_DATABASE_ID:
            case SYNCABLE_START_PATIENT_SESSIONS:
            case NON_SYNCABLE_START_PATIENT_SESSIONS:
            case SYNCABLE_END_PATIENT_SESSIONS:
            case NON_SYNCABLE_END_PATIENT_SESSIONS:
                return TablePatientSession.TABLE_NAME;

            case ALL_PATIENT_SESSION_FULLY_SYNCED:
            case PATIENT_SESSION_FULLY_SYNCED_DATABASE_ID:
                return TablePatientSessionsFullySynced.TABLE_NAME;

            case ALL_CONNECTION_EVENTS:
            case CONNECTION_EVENT_DATABASE_ID:
            case SYNCABLE_CONNECTION_EVENTS:
            case NON_SYNCABLE_CONNECTION_EVENTS:
                return TableConnectionEvent.TABLE_NAME;

            case ALL_GATEWAY_STARTUP_TIMES:
            case GATEWAY_STARTUP_TIMES_DATABASE_ID:
                return TableDiagnosticsGatewayStartupTimes.TABLE_NAME;

            case ALL_UI_STARTUP_TIMES:
            case UI_STARTUP_TIMES_DATABASE_ID:
                return TableDiagnosticsUiStartupTimes.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_HEART_RATES:
            case MANUALLY_ENTERED_HEART_RATE_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_HEART_RATES:
            case NON_SYNCABLE_MANUALLY_ENTERED_HEART_RATES:
                return TableManuallyEnteredHeartRate.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_RESPIRATION_RATES:
            case MANUALLY_ENTERED_RESPIRATION_RATE_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES:
            case NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES:
                return TableManuallyEnteredRespirationRate.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_TEMPERATURES:
            case MANUALLY_ENTERED_TEMPERATURE_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_TEMPERATURES:
            case NON_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES:
                return TableManuallyEnteredTemperature.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_SPO2S:
            case MANUALLY_ENTERED_SPO2_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_SPO2:
            case NON_SYNCABLE_MANUALLY_ENTERED_SPO2:
                return TableManuallyEnteredSpO2.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_BLOOD_PRESSURES:
            case MANUALLY_ENTERED_BLOOD_PRESSURE_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURES:
            case NON_SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURES:
                return TableManuallyEnteredBloodPressure.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_WEIGHTS:
            case MANUALLY_ENTERED_WEIGHT_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_WEIGHTS:
            case NON_SYNCABLE_MANUALLY_ENTERED_WEIGHTS:
                return TableManuallyEnteredWeight.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_CONSCIOUS_LEVELS:
            case MANUALLY_ENTERED_CONSCIOUS_LEVEL_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS:
            case NON_SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS:
                return TableManuallyEnteredConsciousnessLevel.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            case NON_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
                return TableManuallyEnteredSupplementalOxygenLevel.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_ANNOTATIONS:
            case MANUALLY_ENTERED_ANNOTATION_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS:
            case NON_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS:
                return TableManuallyEnteredAnnotation.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case NON_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
                return TableManuallyEnteredCapillaryRefillTime.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                return TableManuallyEnteredRespirationDistress.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case NON_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                return TableManuallyEnteredFamilyOrNurseConcern.TABLE_NAME;

            case ALL_MANUALLY_ENTERED_URINE_OUTPUT:
            case MANUALLY_ENTERED_URINE_OUTPUT_DATABASE_ID:
            case SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT:
            case NON_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT:
                return TableManuallyEnteredUrineOutput.TABLE_NAME;

            case ALL_EARLY_WARNING_SCORES:
            case EARLY_WARNING_SCORE_DATABASE_ID:
            case SYNCABLE_EARLY_WARNING_SCORES:
            case NON_SYNCABLE_EARLY_WARNING_SCORES:
                return TableEarlyWarningScore.TABLE_NAME;

            case ALL_SETUP_MODE_LOGS:
            case SETUP_MODE_LOG_DATABASE_ID:
            case SYNCABLE_SETUP_MODE_LOGS:
            case NON_SYNCABLE_SETUP_MODE_LOGS:
                return TableSetupModeLog.TABLE_NAME;

            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SETS:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_DATABASE_ID:
                return TableThresholdSet.TABLE_NAME;

            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAIL_DATABASE_ID:
                return TableThresholdSetAgeBlockDetail.TABLE_NAME;

            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_LEVEL_DATABASE_ID:
                return TableThresholdSetLevel.TABLE_NAME;

            case ALL_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS:
            case EARLY_WARNING_SCORE_THRESHOLD_SET_COLOUR_DATABASE_ID:
                return TableThresholdSetColour.TABLE_NAME;

            case ALL_SERVER_CONFIGURABLE_TEXT:
            case SERVER_CONFIGURABLE_TEXT_DATABASE_ID:
                return TableServerConfigurableText.TABLE_NAME;

            case ALL_WARDS:
            case WARD_DATABASE_ID:
                return TableWards.TABLE_NAME;

            case ALL_BEDS:
            case BED_DATABASE_ID:
                return TableBeds.TABLE_NAME;

            case ALL_AUDITABLE_EVENTS:
            case AUDITABLE_EVENTS_DATABASE_ID:
                return TableAuditTrail.TABLE_NAME;

            case ALL_VIEWABLE_WEB_PAGES:
                return TableViewableWebPageDetails.TABLE_NAME;
        }

        return "ERROR";
    }
}
