package com.isansys.patientgateway;

import static com.isansys.common.DeviceInfoConstants.CONTINUOUS_SETUP_MODE;
import static com.isansys.common.DeviceInfoConstants.INVALID_DEVICE_INFO_ID;
import static com.isansys.common.DeviceInfoConstants.INVALID_DEVICE_SESSION_ID;
import static com.isansys.common.DeviceInfoConstants.INVALID_FIRMWARE_VERSION;
import static com.isansys.common.DeviceInfoConstants.INVALID_HUMAN_READABLE_DEVICE_ID;
import static com.isansys.common.DeviceInfoConstants.INVALID_USER_ID;
import static com.isansys.common.DeviceInfoConstants.SETUP_MODE_LOG_INITIAL_START_TIME;
import static com.isansys.common.measurements.MeasurementVitalSign.INVALID_MEASUREMENT;
import static com.isansys.patientgateway.algorithms.NoninWristOxPlaybackDecoder.NONIN_WRIST_OX__READINGS_FROM_PLAYBACK;
import static com.isansys.patientgateway.tabletBattery.TabletBatteryInterpreter.GatewayBatteryInfo;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings.Secure;
import android.text.format.DateUtils;
import android.util.Base64;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.util.Pair;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.isansys.common.AppVersions;
import com.isansys.common.DeviceSession;
import com.isansys.common.ErrorCodes;
import com.isansys.common.PatientInfo;
import com.isansys.common.ThresholdSet;
import com.isansys.common.ThresholdSetAgeBlockDetail;
import com.isansys.common.ThresholdSetColour;
import com.isansys.common.ThresholdSetLevel;
import com.isansys.common.VideoCallContact;
import com.isansys.common.VideoCallDetails;
import com.isansys.common.WebPageDescriptor;
import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.AuditTrailEvent;
import com.isansys.common.enums.BarcodeDeviceType;
import com.isansys.common.enums.BluetoothStatus;
import com.isansys.common.enums.Commands;
import com.isansys.common.enums.DeviceConnectionStatus;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.HttpOperationType;
import com.isansys.common.enums.MeasurementTypes;
import com.isansys.common.enums.PatientDetailsLookupStatus;
import com.isansys.common.enums.PatientPositionOrientation;
import com.isansys.common.enums.QueryType;
import com.isansys.common.enums.RealTimeServer;
import com.isansys.common.enums.SensorType;
import com.isansys.common.enums.VideoCallStatus;
import com.isansys.common.measurements.MeasurementAnnotation;
import com.isansys.common.measurements.MeasurementBatteryReading;
import com.isansys.common.measurements.MeasurementBloodPressure;
import com.isansys.common.measurements.MeasurementCapillaryRefillTime;
import com.isansys.common.measurements.MeasurementConsciousnessLevel;
import com.isansys.common.measurements.MeasurementEarlyWarningScore;
import com.isansys.common.measurements.MeasurementFamilyOrNurseConcern;
import com.isansys.common.measurements.MeasurementHeartRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredBloodPressure;
import com.isansys.common.measurements.MeasurementManuallyEnteredHeartRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredRespirationRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredSpO2;
import com.isansys.common.measurements.MeasurementManuallyEnteredTemperature;
import com.isansys.common.measurements.MeasurementUrineOutput;
import com.isansys.common.measurements.MeasurementManuallyEnteredWeight;
import com.isansys.common.measurements.MeasurementPatientOrientation;
import com.isansys.common.measurements.MeasurementRespirationDistress;
import com.isansys.common.measurements.MeasurementRespirationRate;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.common.measurements.MeasurementSupplementalOxygenLevel;
import com.isansys.common.measurements.MeasurementTemperature;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.MeasurementWeight;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.algorithms.Algorithms;
import com.isansys.patientgateway.algorithms.Algorithms.AlgorithmMeasurementType;
import com.isansys.patientgateway.algorithms.EarlyWarningScoreProcessor;
import com.isansys.patientgateway.algorithms.IntermediateMeasurementProcessor;
import com.isansys.patientgateway.algorithms.NoninWristOxPlaybackDecoder;
import com.isansys.patientgateway.bluetooth.BluetoothPairing;
import com.isansys.patientgateway.bluetooth.SpO2.NoninWristOx;
import com.isansys.patientgateway.bluetooth.bloodPressure.AnD_UA767;
import com.isansys.patientgateway.bluetooth.bloodPressure.And_UA767_MeasurementStatus;
import com.isansys.patientgateway.bluetooth.temperature.Fora_Ir20;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_TM2441.BluetoothLe_AnD_TM2441;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UA651.BluetoothLe_AnD_UA651;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UA1200BLE.BluetoothLe_AnD_UA1200BLE;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UA656.BluetoothLe_AnD_UA656;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.AND_UC352BLE.BluetoothLe_AnD_UC352BLE;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Instapatch.BluetoothLeInstapatch;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Lifetemp.BluetoothLeLifetempV2;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Lifetouch.BluetoothLeLifetouch;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Lifetouch.LifetouchDfu;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree.BluetoothLeLifetouchThree;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.LifetouchThree.LifetouchThreeSetupMode;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.Nonin3230.BluetoothLeNonin3230;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.NoninWristOxBtle.BluetoothLeNoninWristOx;
import com.isansys.patientgateway.bluetoothLowEnergyDevices.MedLinket.BluetoothLeMedLinket;
import com.isansys.patientgateway.database.AsyncDatabaseUpdater;
import com.isansys.patientgateway.database.DeleteOldFullySyncedSessions;
import com.isansys.patientgateway.database.TableThresholdSet;
import com.isansys.patientgateway.database.TableThresholdSetAgeBlockDetail;
import com.isansys.patientgateway.database.TableThresholdSetColour;
import com.isansys.patientgateway.database.TableThresholdSetLevel;
import com.isansys.patientgateway.database.contentprovider.IsansysPatientGatewayContentProvider;
import com.isansys.patientgateway.deviceInfo.BtClassicSensorDevice;
import com.isansys.patientgateway.deviceInfo.BtleSensorDevice;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.patientgateway.enums.UnlockCodeSource;
import com.isansys.patientgateway.enums.VideoCallContactsLookupStatus;
import com.isansys.patientgateway.factories.IntentFactory;
import com.isansys.patientgateway.gsm.GsmEventManager;
import com.isansys.patientgateway.ntpTimeSync.AlternateTimeSource;
import com.isansys.patientgateway.ntpTimeSync.NTP_Simple;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;
import com.isansys.patientgateway.serverlink.ServerLink;
import com.isansys.patientgateway.serverlink.ServerSyncStatus;
import com.isansys.patientgateway.serverlink.ServerSyncing;
import com.isansys.patientgateway.serverlink.model.GetMQTTCertificateResponse;
import com.isansys.patientgateway.tabletBattery.TabletBatteryInterpreter;
import com.isansys.patientgateway.wifi.WifiEventManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;


public class PatientGatewayService extends Service implements DevicePeriodicMode.DevicePeriodicModeEvents,
                                                              PatientGatewayInterface,
                                                              ContextInterface
{
    private WakeLock wake_lock;

    private RemoteLogging Log;

    private Settings gateway_settings;

    private SetupWizard setup_wizard;

    private CsvExport csv_export;

    // Has to be static for disableCommentsForSpeed
    private static DummyDataMode dummy_data_instance;

    private final Messenger patient_gateway_incoming_messenger = new Messenger(new IncomingHandler());

    private SystemCommands patient_gateway_outgoing_commands;

    private IntentFactory intent_factory;

    private AppVersions app_versions;

    private final String TAG = this.getClass().getSimpleName();

    private Timer patient_gateway_service_timer;

    private int number_of_times_gateway_has_booted = 0;
    private long most_recent_gateway_boot_time = -1;
    private int number_of_times_ui_has_booted = 0;
    private long most_recent_ui_boot_time = -1;

    private boolean inform_audit_trail_if_battery_is_5_percent = false;  // We are only really interested when the battery level first reaches 5%, not every indication of 5% or lower

    private final String INTENT__COMMANDS_TO_PATIENT_GATEWAY = "com.isansys.patientgateway.commands_to_patient_gateway";

    private PatientPositionOrientation last_patient_orientation = PatientPositionOrientation.ORIENTATION_UNKNOWN;
    private long last_patient_orientation_timestamp = 0;

    private ArrayList<WardInfo> cached_wards = new ArrayList<>();
    private ArrayList<BedInfo> cached_beds = new ArrayList<>();

    public static final int LT3_MIN_DFU_VERSION = 299;

/*
    private final long GTIN_ISANSYS_PATIENT_GATEWAY                 = 5060488680007L;

    private final long GTIN_LIFETEMP_SENSOR_SMALL                   = 5060488680014L;
    private final long GTIN_LIFETEMP_SENSOR_MEDIUM                  = 5060488680021L;
    private final long GTIN_LIFETEMP_SENSOR_LARGE                   = 5060488680038L;
    private final long GTIN_LIFETEMP_CARTON_BOX_SMALL_TEN           = 5060488680045L;
    private final long GTIN_LIFETEMP_CARTON_BOX_MEDIUM_TEN          = 5060488680052L;
    private final long GTIN_LIFETEMP_CARTON_BOX_LARGE_TEN           = 5060488680069L;

    private final long GTIN_LIFETOUCH_SENSOR_NEONATE                = 5060488680076L;
    private final long GTIN_LIFETOUCH_SENSOR_NEONATE_SNAPS          = 5060488680083L;
    private final long GTIN_LIFETOUCH_SENSOR_SMALL                  = 5060488680090L;
    private final long GTIN_LIFETOUCH_SENSOR_MEDIUM                 = 5060488680106L;
    private final long GTIN_LIFETOUCH_SENSOR_LARGE                  = 5060488680113L;
    private final long GTIN_LIFETOUCH_CARTON_BOX_NEONATE_TEN        = 5060488680120L;
    private final long GTIN_LIFETOUCH_CARTON_BOX_NEONATE_SNAPS_TEN  = 5060488680137L;
    private final long GTIN_LIFETOUCH_CARTON_BOX_SMALL_TEN          = 5060488680144L;
    private final long GTIN_LIFETOUCH_CARTON_BOX_MEDIUM_TEN         = 5060488680151L;
    private final long GTIN_LIFETOUCH_CARTON_BOX_LARGE_TEN          = 5060488680168L;

    private final long GTIN_LIFEGUARD_SERVER                        = 5060488680175L;
*/


    private Handler btle_scanning_timeout_handler;
    private Handler dummy_device_scan_handler;

    // Class to handle the timing for Device Periodic Mode
    private DevicePeriodicMode device_periodic_mode;

    private BluetoothPairing bluetooth_pairing;

    private final boolean DO_NOT_DELETE_EWS_THRESHOLDS = false;
    private final boolean DELETE_EWS_THRESHOLDS = true;

    private BatteryDataAndEventHandler battery_data_and_event_handler;

    private TabletBatteryInterpreter mBatteryInterpreter;

    private WifiEventManager wifi_event_manager;
    private WifiEventManager.WifiStatus last_wifi_status = null;

    private GsmEventManager gsm_event_manager;
    private GsmEventManager.GsmStatus last_gsm_status = null;

    private OldLogFileUploader old_log_file_uploader;

    private boolean night_mode_enabled;

    private ArrayList<DeviceSession> device_sessions;

    // ToDo: make this private
    public static DeviceInfoManager device_info_manager;
    private FirmwareImageManager firmware_image_manager;
    private DeviceType current_scan_type = DeviceType.DEVICE_TYPE__INVALID;

    // Vital Signs processors
    private IntermediateMeasurementProcessor<HeartBeatInfo> lifetouch_heart_beat_processor;
    private IntermediateMeasurementProcessor<IntermediateSpO2> intermediate_spo2_processor;

    // In this mode, once the SpO2 device is disconnected, then after X seconds any existing "minutes" in intermediate_spo2_processor are processed
    // as NO more data is going to be received for these partial minutes
    public static boolean spO2_spot_measurements_enabled = false;

    // LONGER than a minute, as need to handle use case when patient puts device on for X secs, off for Y and then on for Z secs again all in the same minute
    private final long spo2_intermediate_tidy_up_timer_timeout_length = 70 * (int)DateUtils.SECOND_IN_MILLIS;


    private final CountDownTimer spo2_intermediate_tidy_up_timer = new CountDownTimer(spo2_intermediate_tidy_up_timer_timeout_length, (int)DateUtils.SECOND_IN_MILLIS)
    {
        @Override
        public void onTick(long ticks)
        {
            final double SECOND_IN_TICKS = 1000;
            Log.d(TAG, "SpO2 Intermediate Measurements Tidy Up timer tick : " + Math.round((double)ticks / SECOND_IN_TICKS));
        }

        @Override
        public void onFinish()
        {
            Log.d(TAG, "SpO2 Intermediate Measurements Tidy Up timer finished : processAnyOutstandingDataIncludingLastMinute");

            intermediate_spo2_processor.processAnyOutstandingDataIncludingLastMinute();
        }

    };


    private EarlyWarningScoreProcessor early_warning_score_processor;

    private LongTermMeasurementValidityTracker long_term_measurement_validity_tracker;

    private ServerSyncStatus server_sync_status;

    private LocalDatabaseStorage local_database_storage;

    private ServerSyncing server_syncing;

    private BluetoothAdapterResetManager bluetooth_reset_manager;

    private DiskUsage diskUsage;

    public static class PatientSessionInfo
    {
        final static int INVALID_PATIENT_SESSION = 0;

        public int android_database_patient_session_id;

        // Stores the value of server's patient id. When new device is added to the session then it can retrieve this value and give to new device
        public int server_patient_session_id;

        // Master variable keeping track if a Patient Session is in progress or not.
        // Should only log data from the sensors if there is a Patient Session AND Device Session in progress, otherwise the data isn't from this session
        public boolean patient_session_running;

        public long start_session_time;

        // Variable set to TRUE if the Session is being restored from file after a Gateway upgrade
        boolean restored_session;

        // These two are used for logging the number of times Sweetblue has "error'ed" but we have recovered this session
        public int number_of_times_postCompleteBleReset_called;
        public int number_of_times_postBleResetWithoutRemovingDevices_called;

        PatientSessionInfo()
        {
            setInitialValues();
        }

        /**
         * Reset the session info to an as-new state
         */
        void setInitialValues()
        {
            android_database_patient_session_id = INVALID_PATIENT_SESSION;
            server_patient_session_id = INVALID_PATIENT_SESSION;
            patient_session_running = false;
            start_session_time = -1;
            restored_session = false;
            number_of_times_postCompleteBleReset_called = 0;
            number_of_times_postBleResetWithoutRemovingDevices_called = 0;
        }
    }

    public final PatientSessionInfo patient_session_info = new PatientSessionInfo();

    private PatientInfo patient_info = new PatientInfo();

    // Class to hold a single Raw Accelerometer Mode data point
    static public class RawAccelerometerModeDataPoint
    {
        public short x;
        public short y;
        public short z;
        public long timestamp;
    }

    // Thread safe queue to hold the Lifetouch Heart Beats. These are received in the Patient Gateway Service but written to the Database every 60 seconds on the 1 second timer (running on another thread)
    private final static ConcurrentLinkedQueue<HeartBeatInfo> queue_lifetouch_heart_beats_datapoints = new ConcurrentLinkedQueue<>();

    // Thread safe queue to hold the Lifetouch Raw Accelerometer Mode samples. These are received in the Patient Gateway Service but written to the Database on the 1 second timer (running on another thread)
    public final static ConcurrentLinkedQueue<RawAccelerometerModeDataPoint> queue_lifetouch_raw_accelerometer_mode_datapoints = new ConcurrentLinkedQueue<>();
    private final static ConcurrentLinkedQueue<RawAccelerometerModeDataPoint> queue_lifetouch_raw_accelerometer_mode_datapoints_for_server = new ConcurrentLinkedQueue<>();

    // Code to check that the User Interface is still running
    private CheckUserInterfaceRunning checkUserInterfaceRunning;

    /**
     * bound_to_logcat_capture_service : boolean - set True if logcat service is running
     */
    private boolean bound_to_logcat_capture_service = false;

    /**
     * This time source represents the sync time from Server.
     * All the application code which uses the Android's Clock Should use this ClockSource Instead
     */
    private static final TimeSource ntp_time = new AlternateTimeSource();

    /* EWS objects */
    private EwsQueryHandler ews_query_handler;

    private final ArrayList<ThresholdSet> default_early_warning_score_threshold_sets = new ArrayList<>();

    private void showOnScreenMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Log.e(TAG + "showOnScreenMessage", message);
    }


    private static class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
        }
    }


    private static IntentFilter makeBluetoothSearchingAndPairingIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothPairing.BLUETOOTH_PAIRING__SEARCHING);
        intentFilter.addAction(BluetoothPairing.BLUETOOTH_PAIRING__DESIRED_DEVICE_FOUND);
        intentFilter.addAction(BluetoothPairing.BLUETOOTH_PAIRING__DESIRED_DEVICE_NOT_FOUND);
        intentFilter.addAction(BluetoothPairing.BLUETOOTH_PAIRING__PAIRING_STARTED);
        intentFilter.addAction(BluetoothPairing.BLUETOOTH_PAIRING__PAIRING_SUCCESS);
        intentFilter.addAction(BluetoothPairing.BLUETOOTH_PAIRING__PAIRING_FAILED);
        return intentFilter;
    }


    // This function handles the output of the BluetoothPairing class.
    private final BroadcastReceiver broadcastReceiverBluetoothSearchingAndPairing = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            String bluetooth_address = intent.getStringExtra("bluetooth_address");

            DeviceInfo device_info = device_info_manager.getDeviceInfoFromBluetoothAddress(bluetooth_address);

            if (device_info != null)
            {
                switch (action)
                {
                    case BluetoothPairing.BLUETOOTH_PAIRING__SEARCHING:
                    {
                        Log.d(TAG, "BLUETOOTH_PAIRING__SEARCHING");

                        // Update the UI to show Scan Started messages
                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_SCAN_STARTED);
                    }
                    break;

                    case BluetoothPairing.BLUETOOTH_PAIRING__DESIRED_DEVICE_FOUND:
                    {
                        Log.d(TAG, "BLUETOOTH_PAIRING__DESIRED_DEVICE_FOUND");

                        // Update the UI to show Device Found messages
                        device_info.cancelBluetoothConnectionTimer();

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_FOUND);
                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.FOUND);

                        // Tell the UI about the actual_device_connection_status change
                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                    }
                    break;

                    case BluetoothPairing.BLUETOOTH_PAIRING__DESIRED_DEVICE_NOT_FOUND:
                    {
                        Log.d(TAG, "BLUETOOTH_PAIRING__DESIRED_DEVICE_NOT_FOUND");

                        // Update the UI to show Device NOT Found messages
                        device_info.cancelBluetoothConnectionTimer();

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_SCAN_FINISHED__DEVICE_NOT_FOUND);

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.NOT_PAIRED);

                        // Tell the UI about the actual_device_connection_status change
                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                        checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                    }
                    break;

                    case BluetoothPairing.BLUETOOTH_PAIRING__PAIRING_STARTED:
                    {
                        Log.d(TAG, "BLUETOOTH_PAIRING__PAIRING_STARTED");

                        // Update the UI to show Pairing messages
                        device_info.cancelBluetoothConnectionTimer();

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING);
                    }
                    break;

                    case BluetoothPairing.BLUETOOTH_PAIRING__PAIRING_SUCCESS:
                    {
                        Log.d(TAG, "BLUETOOTH_PAIRING__PAIRING_SUCCESS : " + bluetooth_address);

                        device_info.cancelBluetoothConnectionTimer();

                        // Update the UI to show Paired - Connecting messages
                        switch (device_info.device_type)
                        {
                            case DEVICE_TYPE__NONIN_WRIST_OX:
                            {
                                patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);
                            }
                            break;

                            case DEVICE_TYPE__FORA_IR20:
                            {
                                patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);

                                sendBroadcast(new Intent(Fora_Ir20.SET_ADDED_TO_SESSION));
                            }
                            break;

                            case DEVICE_TYPE__AND_UA767:
                            {
                                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.PAIRED);

                                patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                                sendBroadcast(new Intent(AnD_UA767.SET_ADDED_TO_SESSION));

                                // Tell the UI about the actual_device_connection_status change
                                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                                checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                            }
                            break;
                        }
                    }
                    break;

                    case BluetoothPairing.BLUETOOTH_PAIRING__PAIRING_FAILED:
                    {
                        Log.d(TAG, "BLUETOOTH_PAIRING__PAIRING_FAILED");

                        // Update the UI to show Pairing Failed messages
                        device_info.cancelBluetoothConnectionTimer();

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING_FAILED);
                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.NOT_PAIRED);

                        // Tell the UI about the actual_device_connection_status change
                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                        checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                    }
                    break;
                }
            }
        }
    };


    private final BroadcastReceiver broadcastReceiverAndroidEvents = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            switch (action)
            {
                case Intent.ACTION_SHUTDOWN:
                {
                    // ACTION_SHUTDOWN implies power off or deliberate shutdown - so don't want to resume session on next start.
                    deleteCurrentSessionFile();

                    Log.e(TAG, "ACTION_SHUTDOWN");
                }
                break;

                case Intent.ACTION_SCREEN_ON:
                    Log.e(TAG, "ACTION_SCREEN_ON");
                    break;

                case Intent.ACTION_SCREEN_OFF:
                    Log.e(TAG, "ACTION_SCREEN_OFF");
                    break;

                case Intent.ACTION_POWER_DISCONNECTED:
                    Log.e(TAG, "ACTION_POWER_DISCONNECTED");
                    break;

                case Intent.ACTION_POWER_CONNECTED:
                {
                    Log.e(TAG, "ACTION_POWER_CONNECTED");

                    // Reset the unplugged event counter integer to 0
                    battery_data_and_event_handler.resetUnpluggedEventCounter();

                    patient_gateway_outgoing_commands.reportGatewayPluggedInStatusTimerSwitchSafe(false);
                }
                break;

                case Intent.ACTION_BATTERY_OKAY:
                    Log.e(TAG, "ACTION_BATTERY_OKAY");
                    break;

                case Intent.ACTION_BATTERY_LOW:
                    Log.e(TAG, "ACTION_BATTERY_LOW");
                    break;
            }
        }
    };


    private static abstract class CustomAsyncQueryHandler extends AsyncQueryHandler
    {
        final WeakReference<PatientGatewayService> service_resolver_weak_reference;

        CustomAsyncQueryHandler(ContentResolver cr, PatientGatewayService service)
        {
            super(cr);

            service_resolver_weak_reference = new WeakReference<>(service);
        }

        protected abstract void onQueryFinished(int token, Cursor cursor);

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor)
        {
            onQueryFinished(token, cursor);
        }
    }


    private static class EwsQueryHandler extends CustomAsyncQueryHandler
    {
        EwsQueryHandler(ContentResolver cr, PatientGatewayService service)
        {
            super(cr, service);
        }

        protected void onQueryFinished(int token, Cursor cursor)
        {
            try
            {
                service_resolver_weak_reference.get().onEwsQueryComplete(token, cursor);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                cursor.close();
            }
        }
    }


    private void onEwsQueryComplete(int token, Cursor cursor)
    {
        if(cursor != null)
        {
            ThresholdQueryType queryType = ThresholdQueryType.values()[token];

            Log.d(TAG, "onEwsQueryComplete : EWS cursor found for " + queryType);

            switch (queryType)
            {
                case THRESHOLD_SETS:
                    processThresholdSets(cursor);
                    break;

                case THRESHOLD_AGE_BLOCK_DETAILS:
                    processThresholdSetAgeBlockDetails(cursor);
                    break;

                case THRESHOLD_SET_LEVELS:
                    processThresholdSetLevels(cursor);
                    break;

                case THRESHOLD_SET_COLOURS:
                    processThresholdSetColours(cursor);
                    break;

                default:
                    Log.w(TAG, "onEwsQueryComplete : EWS query received with unknown token");
                    break;
            }
        }
        else
        {
            Log.d(TAG, "onEwsQueryComplete : EWS cursor == null");
        }

        Log.d(TAG, "onEwsQueryComplete : EWS query completed");
    }

    private boolean isExternalStorageWritable()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if a volume containing external storage is available to at least read.
    private boolean isExternalStorageReadable()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    @Override
    public void onCreate()
    {
        // The system calls this method when the service is first created, to perform one-time setup procedures (before it calls either onStartCommand() or onBind()).
        // If the service is already running, this method is not called.

        super.onCreate();

        Log = new RemoteLogging();

        Log.d(TAG, "onCreate main function");

        Log.d(TAG, "Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
        Log.d(TAG, "Build.PRODUCT = " + Build.PRODUCT);
        Log.d(TAG, "Build.MODEL = " + Build.MODEL);
        Log.d(TAG, "Build.MANUFACTURER = " + Build.MANUFACTURER);
        Log.d(TAG, "Build.ID = " + Build.ID);
        Log.d(TAG, "Build.HOST = " + Build.HOST);
        Log.d(TAG, "Build.HARDWARE = " + Build.HARDWARE);
        Log.d(TAG, "Build.FINGERPRINT = " + Build.FINGERPRINT);
        Log.d(TAG, "Build.DISPLAY = " + Build.DISPLAY);
        Log.d(TAG, "Build.DEVICE = " + Build.DEVICE);
        Log.d(TAG, "Build.BOOTLOADER = " + Build.BOOTLOADER);
        Log.d(TAG, "Build.BOARD = " + Build.BOARD);

        String android_id = getAndroidUniqueDeviceId();

        Log.d(TAG, "Android ID (hex)  = " + android_id);
        Log.d(TAG, "Android ID (long) = " + isansysParseHex(android_id));

        Log.d(TAG, "isExternalStorageWritable = " + isExternalStorageWritable());
        Log.d(TAG, "isExternalStorageReadable = " + isExternalStorageReadable());

/*
        RhinoTest rhino_test = new RhinoTest(this);
        rhino_test.runTest();
*/
        if (BuildConfig.DEBUG == false)
        {
            // This sets a custom error activity class instead of the default one.
            CustomActivityOnCrash.setErrorActivityClass(CustomErrorActivity.class);

            // Install CustomActivityOnCrash
            CustomActivityOnCrash.install(this);
        }

        // Get wakelock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wake_lock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PatientGateway:MyWakelockTag");
        wake_lock.acquire(10*60*1000L /*10 minutes*/);

        // If there is a new DB in tablet_root/database_import, then import it. This potentially allows us to set up failure conditions from other gateways.
        // DB is deleted after import.
        importDB();

        gateway_settings = new Settings(this);

        device_info_manager = new DeviceInfoManager(this, Log, ntp_time, patient_session_info);
        firmware_image_manager = new FirmwareImageManager(Log, gateway_settings);

        registerReceiver(mGattUpdateReceiver_Lifetouch, makeLifetouchGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_LifetouchThree, makeLifetouchThreeGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_Lifetemp_V2, makeLifetempV2GattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_NoninWristOx3150Ble, makeNoninWristOx3150BleGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_Nonin3230, makeNonin3230GattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_MedLinket, makeMedLinketBleGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_AndTm2441, makeAndTm2441BleGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_AndUa651, makeAndUa651BleGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_AndUc352, makeAndUc352BleGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_AndUA1200, makeAndUA1200BleGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_Instapatch, makeInstapatchGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver_AndUa656, makeAndUa656BleGattUpdateIntentFilter());

        // We are using LocalBroadcastReceiver instead of normal BroadcastReceiver for optimization purposes
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(mDfuUpdateReceiver, makeDfuUpdateIntentFilter());

        btle_scanning_timeout_handler = new Handler();
        dummy_device_scan_handler = new Handler();

        intent_factory = new IntentFactory();

        app_versions = new AppVersions(this);

        csv_export = new CsvExport(this, Log);

        wifi_event_manager = new WifiEventManager(this, Log, this, gateway_settings.getEnableWifiLogging());
        gsm_event_manager = new GsmEventManager(this, Log, this, gateway_settings.getEnableGsmLogging());

        server_sync_status = new ServerSyncStatus();

        local_database_storage = new LocalDatabaseStorage(this,
                Log,
                new DeleteOldFullySyncedSessions.DeletionProgressCallback()
                {
                    @Override
                    public void setDeletionProgressStatus(boolean is_in_progress)
                    {
                        // To have full control of Server Sync in "ServerSyncTableObserver" another instance of
                        // deletion in progress status is made
                        server_syncing.server_sync_table_observer.setDatabaseDeletionProgressStatus(is_in_progress);
                    }
                },
                this,
                gateway_settings.getEnableDatabaseLogging(),
                server_sync_status);

        String gateway_unique_id = getAndroidUniqueDeviceId();

        local_database_storage.setAndroidUniqueDeviceIdForAuditTrail(gateway_unique_id);

        server_syncing = new ServerSyncing(this, Log, gateway_settings, device_info_manager, local_database_storage, gateway_unique_id, this, gateway_settings.getEnableServerLogging(), server_sync_status);
        local_database_storage.setServerSyncing(server_syncing);

        old_log_file_uploader = new OldLogFileUploader(Log, this);

        patient_gateway_outgoing_commands = new SystemCommands(this, Log, gateway_settings);

        diskUsage = new DiskUsage(patient_gateway_outgoing_commands);

        bluetooth_reset_manager = new BluetoothAdapterResetManager(this, Log, device_info_manager, patient_gateway_outgoing_commands);

        lifetouch_heart_beat_processor = new IntermediateMeasurementProcessor(this, gateway_settings, Log, intent_factory, patient_gateway_outgoing_commands, queue_lifetouch_heart_beats_datapoints, new HeartBeatInfo());
        intermediate_spo2_processor = new IntermediateMeasurementProcessor(this, gateway_settings, Log, intent_factory, patient_gateway_outgoing_commands, pulse_ox_intermediate_measurements, new IntermediateSpO2());

        early_warning_score_processor = new EarlyWarningScoreProcessor(this, Log, intent_factory, device_info_manager);

        dummy_data_instance = new DummyDataMode(this, Log, gateway_settings, device_info_manager, this);

        battery_data_and_event_handler = new BatteryDataAndEventHandler(Log, this, gateway_settings.getEnableBatteryLogging());

        mBatteryInterpreter = new TabletBatteryInterpreter(this, Log, battery_data_and_event_handler, this, gateway_settings.getEnableBatteryLogging());

        local_database_storage.setNumberOfRowsPerJsonMessage(gateway_settings.getNumberOfDatabaseRowsPerJsonMessage());

        dummy_data_instance.setNumberOfSimulatedMeasurementsPerTick(gateway_settings.getNumberOfDummyDataModeMeasurementsPerTick());

        most_recent_gateway_boot_time = getNtpTimeNowInMilliseconds();
        number_of_times_gateway_has_booted = local_database_storage.storeGatewayStartupTime(most_recent_gateway_boot_time);


        device_sessions = new ArrayList<>();

        setup_wizard = new SetupWizard(Log, gateway_settings, server_syncing, this, new Handler());

        // NOTE : On Android 11, turning off Wifi does NOT work. Therefore this code will NOT work.
        // You need to manually turn the Wifi on/off via the Admin Wifi icon
        if (inGsmOnlyMode())
        {
            // If the Gateway has been set to GSM only, then do not register for Wifi events. This stops the Wifi from trying to search out access points when they will
            // not be used.....freeing up the radio chip to talk to BTLE faster
            enableWifi(false);
        }
        else
        {
            enableWifi(true);

            registerReceiver(wifi_event_manager.wifi_status_receiver, wifi_event_manager.wifiIntentFilter());
        }


        if(gateway_settings.getSoftwareUpdateModeActive())
        {
            registerReceiver(broadcastReceiverIncomingCommandsFromUserInterface__updateMode, new IntentFilter(INTENT__COMMANDS_TO_PATIENT_GATEWAY));
        }
        else
        {
            registerReceiver(broadcastReceiverIncomingCommandsFromUserInterface, new IntentFilter(INTENT__COMMANDS_TO_PATIENT_GATEWAY));
        }

        registerReceiver(broadcastReceiverIncomingFromAlgorithms, new IntentFilter(Algorithms.INTENT__INCOMING_FROM_ALGORITHMS));
        registerReceiver(broadcastReceiverIncomingCommandFromServer, new IntentFilter(ServerLink.INTENT__COMMANDS_FROM_SERVER));

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverNoninWristOx2, makeNoninWristOxIntentFilter());
        registerReceiver(broadcastReceiverNoninWristOx2Playback, makeNoninWristOxPlaybackIntentFilter());
        registerReceiver(broadcastReceiverAnDUa767Data, makeAnDUa767IntentFilter());
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverForaIr20, makeForaIr20IntentFilter());

        registerReceiver(broadcastReceiverBluetoothSearchingAndPairing, makeBluetoothSearchingAndPairingIntentFilter());

        registerReceiver(broadcastReceiverFromUsbAccessory, makeUsbAccessoryIntentFilter());

        IntentFilter event_filter = new IntentFilter();
        event_filter.addAction(Intent.ACTION_SHUTDOWN);

        event_filter.addAction(Intent.ACTION_SCREEN_OFF);
        event_filter.addAction(Intent.ACTION_SCREEN_ON);

        event_filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        event_filter.addAction(Intent.ACTION_POWER_CONNECTED);

        event_filter.addAction(Intent.ACTION_BATTERY_OKAY);
        event_filter.addAction(Intent.ACTION_BATTERY_LOW);

        registerReceiver(broadcastReceiverAndroidEvents, event_filter);

        // Start the Nonin Wrist Ox service

        startService(new Intent(getApplicationContext(), NoninWristOx.class));

        // Start the A&D UA767 service
        startService(new Intent(getApplicationContext(), AnD_UA767.class));

        // Start the FORA IR20 service
        startService(new Intent(getApplicationContext(), Fora_Ir20.class));

        // If the Server port hasn't been set to anything yet, set it to 80 by default. Saves having to set it on new Gateways
        String server_port = gateway_settings.getServerPort();
        if (server_port.equals(gateway_settings.NOT_SET_YET))
        {
            gateway_settings.storeServerPort("80");
        }


        // If the MQTT port hasn't been set to anything yet, set it to 8883 by default. Saves having to set it on new Gateways
        String real_time_port = gateway_settings.getRealTimeServerPort();
        if (real_time_port.equals(gateway_settings.NOT_SET_YET))
        {
            gateway_settings.storeRealTimeServerPort("8883");
        }


        if(gateway_settings.isServerAddressSet() == false)
        {
            Log.e(TAG, "isansys_server_ip_address is not set up yet so can't configure server syncing");
        }
        else
        {
            setupServerAddressFromSettings();
        }

        Log.d(TAG, "Gateways Assigned Bed ID = " + gateway_settings.getGatewaysAssignedBedId());
        Log.d(TAG, "Gateways Assigned Ward Name = " + gateway_settings.getGatewaysAssignedWardName());
        Log.d(TAG, "Gateways Assigned Bed Name = " + gateway_settings.getGatewaysAssignedBedName());

        // Init code to check that the UI is running.
        checkUserInterfaceRunning = new CheckUserInterfaceRunning(Log, this);

        // The UI hasn't normally been started yet. However, if the Gateway has crashed for any reason, the UI will already be running
        patient_gateway_outgoing_commands.tellUiThatGatewayHasBooted();


        /* Set up a query handler for early warning score thresholds */
        ews_query_handler = new EwsQueryHandler(this.getContentResolver(), this);

        /* Now trigger a query with it to get the EWS thresholds*/
        queryThresholdSets();

        // Set up PatientGatewayService timer tick last to make sure everything has been configured by this point
        // THIS MUST BE THE LAST THING to make sure that we do not poll anything on onPatientGatewayServiceTimerTick that is not setup yet
        GenericStartStopTimer.cancelTimer(patient_gateway_service_timer, Log);
        patient_gateway_service_timer = new Timer("patient_gateway_service_timer");
        patient_gateway_service_timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                onPatientGatewayServiceTimerTick();
            }
        }, 0, (int)DateUtils.SECOND_IN_MILLIS);


        if (bound_to_logcat_capture_service == false)
        {
            ComponentName service_name = startService(new Intent(getAppContext(), PatientGatewayLogcatCaptureService.class));

            if(service_name != null)
            {
                Log.d(TAG, "Successfully starting Gateway Logcat service. Component Name = " + service_name);

                bound_to_logcat_capture_service = true;
            }
            else
            {
                Log.w(TAG, "ALERT ALERT!!!! Gateway Logcat service is not started. Component Name is NULL");
            }
        }
        else
        {
            Log.w(TAG, "onCreate : ALERT ALERT!!!! Gateway Logcat service shouldn't be running. ");
        }


        // Do an NTP sync. If the Server has not been setup yet, or not connected to Wifi, this will fail.
        // This automatically sets up a timer to resync every hour.
        ntpTimeSync(false);

        device_periodic_mode = new DevicePeriodicMode(Log, this);

        if((gateway_settings.getAutoResumeEnabled()) && (lastSessionFileExists()))
        {
            Log.d("RESTARTING", "Auto Resume enabled");

            // Automatically try to resume any open sessions
            bluetooth_pairing = new BluetoothPairing(this, Log);

            resumePreviousSessionIfExists();
        }
        else
        {
            Log.d("RESTARTING", "Auto Resume NOT enabled or no session to resume");

            // Close off outstanding sessions and unpair bluetooth classic.
            local_database_storage.closeOffAnyOutstandingSessions();

            unpairAllBluetoothDevices();

            // If it exists, delete the old session file so it can't be resumed at a later date.
            deleteCurrentSessionFile();
        }

        if (gateway_settings.getEnableAutoUploadLogsToServer())
        {
            old_log_file_uploader.start();
        }

        firmware_image_manager.updateLatestStoredFirmwareVersionIfNewer(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY, app_versions.getGatewayVersionNumberAsInt());
        firmware_image_manager.updateLatestStoredFirmwareVersionIfNewer(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE, app_versions.getUserInterfaceVersionNumberAsInt());

        if (needToGetNewServerData())
        {
            startGetServerSettingsDueToSoftwareUpdate();
        }

        local_database_storage.storeAuditTrailEvent(AuditTrailEvent.GATEWAY_SERVICE_JUST_STARTED, ntp_time.currentTimeMillis(), INVALID_USER_ID );

        Executors.newSingleThreadExecutor().execute(() -> {
            // Long running operation
            deleteOldMemoryLogs();
        });
    }


    private void setupServerAddressFromSettings()
    {
        String isansys_server_address = gateway_settings.getServerAddress();
        Log.d(TAG, "setupServerAddressFromSettings : Address = " + isansys_server_address);

        String isansys_server_port = gateway_settings.getServerPort();
        Log.d(TAG, "setupServerAddressFromSettings : Port = " + isansys_server_port);

        // Has to be before the Server address is setup so the value of use_https can be used
        boolean use_https = gateway_settings.getHttpsEnabledStatus();
        Log.d(TAG, "setupServerAddressFromSettings : Using HTTPS = " + use_https);
        server_syncing.useHttps(use_https);

        // Pass the Server address to classes that need it
        server_syncing.setIsansysServerAddress(isansys_server_address, isansys_server_port);

        boolean enable_webservice_authentication = gateway_settings.getWebServiceAuthenticationEnabledStatus();
        Log.d(TAG, "setupServerAddressFromSettings : Using Webservice Authentication = " + enable_webservice_authentication);
        server_syncing.useAuthenticationForWebServiceCalls(enable_webservice_authentication);

        boolean enable_webservice_encryption = gateway_settings.getWebServiceEncryptionEnabledStatus();
        Log.d(TAG, "setupServerAddressFromSettings : Using Webservice Encryption = " + enable_webservice_encryption);
        server_syncing.useEncryptionForWebServiceCalls(enable_webservice_encryption);

        server_syncing.configureRealTimeServerIfRequired();
    }


    public void reportTabletBatteryInfo(TabletBatteryInterpreter.GatewayBatteryInfo gateway_battery_info, BatteryDataAndEventHandler.TabletBatteryChargingStatus tablet_charging_status)
    {
        patient_gateway_outgoing_commands.reportGatewayPluggedInStatusTimerSwitchSafe(tablet_charging_status.charger_unplugged_long_term);
        patient_gateway_outgoing_commands.reportGatewayChargeStatus(gateway_battery_info);

        // Audit trail log the 1st time battery reaches 5%
        if (gateway_battery_info.android_percentage > 5)
        {
            inform_audit_trail_if_battery_is_5_percent = true;
        }

        if (gateway_battery_info.android_percentage < 6 && inform_audit_trail_if_battery_is_5_percent == true)
        {
            inform_audit_trail_if_battery_is_5_percent = false;

            local_database_storage.storeAuditTrailEventUsingLastKnownUserId(AuditTrailEvent.GATEWAY_BATTERY_FIVE_PERCENT_OR_BELOW, ntp_time.currentTimeMillis());
        }
    }


    private static boolean fileCopy(String source, String dest)
    {
        File source_file = new File(source);
        File destination_file = new File(dest);

        if (source_file.exists())
        {
            try
            {
                InputStream src = new FileInputStream(source_file);
                OutputStream dst = new FileOutputStream(destination_file);

                streamToStream(src, dst);

                src.close();
                dst.close();
            }
            catch (IOException e)
            {
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }


    private static boolean streamToStream(InputStream is, OutputStream os)
    {
        int count = 0;
        try
        {
            while(count != -1)
            {
                byte[] bytes = new byte[2048];

                count = is.read(bytes);

                if(count == -1)
                {
                    continue;
                }

                os.write(bytes, 0, count);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    private void reportInstallationCompleteFlag()
    {
        patient_gateway_outgoing_commands.reportInstallationComplete(gateway_settings.getInstallationComplete());
    }


    public static boolean exportDBStatic()
    {
        File sd = Environment.getExternalStorageDirectory();

        String gateway_name = BluetoothAdapter.getDefaultAdapter().getName();
        String currentDBPath = "data/data/com.isansys.patientgateway/databases/gateway.db";
        String backupDBPath = sd + File.separator + gateway_name + "_gateway_backup" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(PatientGatewayService.getNtpTimeNowInMillisecondsStatic()) + ".db";

        // One of these was causing the file copy to fail on Android 11
        backupDBPath = backupDBPath.replace(" ", "_");
        backupDBPath = backupDBPath.replace(":", "-");

        return fileCopy(currentDBPath, backupDBPath);
    }


    public void exportDB()
    {
        try
        {
            showOnScreenMessage(getString(R.string.starting_database_export_this_might_take_a_few_seconds));

            // /storage/emulated/0
            File sd = Environment.getExternalStorageDirectory();

            Log.d(TAG, "exportDB : Start : " + sd.getAbsolutePath());

            if (sd.canWrite())
            {
                Log.d(TAG, "exportDB : Can write");

                String gateway_name = BluetoothAdapter.getDefaultAdapter().getName();
                String currentDBPath = "data/data/com.isansys.patientgateway/databases/gateway.db";
                String backupDBPath = sd + File.separator + gateway_name + "_gateway_backup" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(PatientGatewayService.getNtpTimeNowInMillisecondsStatic()) + ".db";

                // One of these was causing the file copy to fail on Android 11
                backupDBPath = backupDBPath.replace(" ", "_");
                backupDBPath = backupDBPath.replace(":", "-");

                Log.d(TAG, "exportDB : File = " + backupDBPath);

                if (fileCopy(currentDBPath, backupDBPath))
                {
                    showOnScreenMessage(getString(R.string.database_exported_to) + backupDBPath);
                }
                else
                {
                    showOnScreenMessage(getString(R.string.cannot_find_database_not_exported_to) + backupDBPath);
                }
            }
            else
            {
                Log.e(TAG, "exportDB : Can NOT write");
            }
        }
        catch (Exception e)
        {
            showOnScreenMessage(getString(R.string.problem_found_please_make_a_note_of_the_time_and_tell_isansys));
            Log.e(TAG, "Exception exporting database : " + e);
        }
    }


    private void importDB()
    {
        Log.w(TAG, "Starting DB import");

        try
        {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite())
            {
                String currentDBPath = "data/data/com.isansys.patientgateway/databases/gateway.db";
                String import_path = sd + "/database_import/gateway.db";

                File source_file = new File(import_path);

                if (source_file.exists())
                {
                    showOnScreenMessage(getString(R.string.starting_database_import_this_might_take_a_few_seconds));
                }

                if (fileCopy(import_path, currentDBPath))
                {
                    showOnScreenMessage(getString(R.string.database_imported));

                    File importedFile = new File(import_path);
                    importedFile.delete();
                }
                else
                {
                    Log.w(TAG, "Cant find DB - not imported");
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception importing database : " + e);
        }

        Log.w(TAG, "Finished DB import");
    }


    private void deleteExportedDatabases()
    {
        String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
        String database_file_path = SD_CARD_PATH + "/";

        File f_f = new File(database_file_path);

        TimeZone utc = TimeZone.getTimeZone("UTC");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(utc);

        try
        {
            File[] files = f_f.listFiles();

            // Build compare date. Will delete exported databases older than 2 days
            Calendar calendar = Calendar.getInstance(utc);
            calendar.add(Calendar.DAY_OF_YEAR, -2);

            String limitDateString = formatter.format(calendar.getTime());

            // Format it for date only
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(utc);

            Date limitDate = sdf.parse(limitDateString);

            if (files != null)
            {
                for (File file : files)
                {
                    String filename = file.getName();
                    String extension = filename.substring(filename.lastIndexOf(".") + 1);

                    if (extension.equals("db"))
                    {
                        // Get the files last modified date
                        Date lastModifiedDate = new Date(file.lastModified());

                        if (lastModifiedDate.before(limitDate))
                        {
                            // Delete the file
                            if (file.delete())
                            {
                                Log.d(TAG, "deleteExportedDatabases : Deleted " + filename + " as old enough : " + lastModifiedDate);
                            }
                            else
                            {
                                Log.e(TAG, "deleteExportedDatabases : PROBLEM DELETING FILE " + filename);
                            }
                        }
                        else
                        {
                            Log.d(TAG, "deleteExportedDatabases : Ignoring " + filename + " as NOT old enough : " + lastModifiedDate);
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "deleteExportedDatabases Exception : " + e);
        }
    }

    
    private void deleteOldMemoryLogs()
    {
        String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
        String database_file_path = SD_CARD_PATH + "/IsansysLogging/";

        File f_f = new File(database_file_path);

        TimeZone utc = TimeZone.getTimeZone("UTC");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(utc);

        try
        {
            File[] files = f_f.listFiles();

            if (files != null)
            {
                for (File file : files)
                {
                    String filename = file.getName();
                    String extension = filename.substring(filename.lastIndexOf(".") + 1);

                    if (extension.equals("txt"))
                    {

                        // Delete the file
                        if (file.delete())
                        {
                            Log.d(TAG, "deleteOldMemoryLogs : Deleted " + filename);
                        }
                        else
                        {
                            Log.e(TAG, "deleteOldMemoryLogs : PROBLEM DELETING FILE " + filename);
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "deleteOldMemoryLogs Exception : " + e);
        }
    }




    /*
    @Override
    protected void onResume()
    {
        super.onResume();

        btle_onResume();
    }
    */
    /*
    @Override
    protected void onPause()
    {
        super.onPause();

        btle_onPause();
    }
    */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // The system calls this method when another component, such as an activity, requests that the service be started, by calling startService().
        // Once this method executes, the service is started and can run in the background indefinitely.
        // If you implement this, it is your responsibility to stop the service when its work is done, by calling stopSelf() or stopService().
        // (If you only want to provide binding, you don't need to implement this method.)

        Log.d("START_COMMAND", "onStartCommand");

        String NOTIFICATION_CHANNEL_ID = "com.isansys.patient.gateway.service";
        String DISPLAY_TEXT = "Patient Gateway is running in background";

        String channelName = "My Background Service";

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;

        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(DISPLAY_TEXT)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1001, notification);

        return Service.START_STICKY;                    // Auto restart the Gateway if Android has to terminate this service
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        // The system calls this method when another component wants to bind with the service (such as to perform RPC), by calling bindService().
        // In your implementation of this method, you must provide an interface that clients use to communicate with the service, by returning an IBinder.
        // You must always implement this method, but if you don't want to allow binding, then you should return null.

        Log.d(TAG, "PatientGatewayService onBind");

        return this.patient_gateway_incoming_messenger.getBinder();
    }


    @Override
    public void onLowMemory()
    {
        Log.d(TAG, "onLowMemory called");

        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level)
    {
        String level_string;

        switch (level)
        {
            case TRIM_MEMORY_RUNNING_MODERATE:
            {
                level_string = "TRIM_MEMORY_RUNNING_MODERATE";
            }
            break;

            case TRIM_MEMORY_RUNNING_LOW:
            {
                level_string = "TRIM_MEMORY_RUNNING_LOW";
            }
            break;

            case TRIM_MEMORY_RUNNING_CRITICAL:
            {
                level_string = "TRIM_MEMORY_RUNNING_CRITICAL - processes may be killed!!!";
            }
            break;

            case TRIM_MEMORY_BACKGROUND:
            {
                level_string = "TRIM_MEMORY_BACKGROUND";
            }
            break;

            case TRIM_MEMORY_COMPLETE:
            {
                level_string = "TRIM_MEMORY_COMPLETE";
            }
            break;

            case TRIM_MEMORY_MODERATE:
            {
                level_string = "TRIM_MEMORY_MODERATE";
            }
            break;

            case TRIM_MEMORY_UI_HIDDEN:
            {
                level_string = "TRIM_MEMORY_UI_HIDDEN";
            }
            break;

            default:
            {
                level_string = Integer.toString(level);
            }
        }

        Log.d(TAG, "onTrimMemory called with level = " + level_string);

        super.onTrimMemory(level);
    }


    @Override
    public void onDestroy()
    {
        // The system calls this method when the service is no longer used and is being destroyed.
        // Your service should implement this to clean up any resources such as threads, registered listeners, receivers, etc.
        // This is the last call the service receives.

        Log.d(TAG, "Start of onDestroy");

        // Release wakelock
        if (wake_lock.isHeld())
        {
            Log.d(TAG, "Releasing Wakelock");
            wake_lock.release();
        }
        else
        {
            Log.d(TAG, "Wakelock not held");
        }

        mBatteryInterpreter.getBatteryVoltage();

        unregisterReceiver(mGattUpdateReceiver_Lifetouch);
        unregisterReceiver(mGattUpdateReceiver_LifetouchThree);
        unregisterReceiver(mGattUpdateReceiver_Lifetemp_V2);
        unregisterReceiver(mGattUpdateReceiver_NoninWristOx3150Ble);
        unregisterReceiver(mGattUpdateReceiver_Nonin3230);
        unregisterReceiver(mGattUpdateReceiver_MedLinket);
        unregisterReceiver(mGattUpdateReceiver_AndTm2441);
        unregisterReceiver(mGattUpdateReceiver_AndUa651);
        unregisterReceiver(mGattUpdateReceiver_AndUA1200);
        unregisterReceiver(mGattUpdateReceiver_AndUc352);
        unregisterReceiver(mGattUpdateReceiver_Instapatch);
        unregisterReceiver(mGattUpdateReceiver_AndUa656);

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.unregisterReceiver(mDfuUpdateReceiver);

        GenericStartStopTimer.cancelTimer(patient_gateway_service_timer, Log);

        if (bluetooth_pairing != null)
        {
            bluetooth_pairing.bluetoothCancelAndUnregister();
        }

        unRegisterServerUpdateService();

        unregisterReceiver(wifi_event_manager.wifi_status_receiver);

        // Unregister broadcast listeners
        unregisterReceiver(broadcastReceiverIncomingCommandsFromUserInterface);
        unregisterReceiver(broadcastReceiverIncomingFromAlgorithms);
        unregisterReceiver(broadcastReceiverIncomingCommandFromServer);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverNoninWristOx2);
        unregisterReceiver(broadcastReceiverAnDUa767Data);
        unregisterReceiver(broadcastReceiverNoninWristOx2Playback);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverForaIr20);
        unregisterReceiver(broadcastReceiverBluetoothSearchingAndPairing);
        bluetooth_reset_manager.destroy();
        unregisterReceiver(broadcastReceiverAndroidEvents);
        unregisterReceiver(broadcastReceiverFromUsbAccessory);

        device_info_manager.destroy();

        checkUserInterfaceRunning.destroy();

        // NOTE : On Android 11, turning off Wifi does NOT work. Therefore this code will NOT work.
        // You need to manually turn the Wifi on/off via the Admin Wifi icon
        if (gateway_settings.getGsmOnlyModeEnabledStatus())
        {
            enableWifi(true);
        }

        Log.e(TAG, "End of onDestroy");
        super.onDestroy();
    }


    /* Used to update the scan devices page, so if a device has been found and connected (even if
     * it's now disconnected or rescanning) it should be treated as connected here.
     */
    private boolean checkConnectionStatusAndUpdateUI(DeviceInfo device_info)
    {
        if (device_info.isDeviceHumanReadableDeviceIdValid())
        {
            switch(device_info.getActualDeviceConnectionStatus())
            {
                case SEARCHING:
                case NOT_PAIRED:
                case UNBONDED:
                case FOUND:
                {
                    // Device not yet paired so don't update
                    return false;
                }

                case CONNECTED:
                case DISCONNECTED:
                {
                    // Device has already been connected so update connection page
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);
                }
                break;

                case PAIRED:
                {
                    if(device_info.device_type == DeviceType.DEVICE_TYPE__AND_UA767)
                    {
                        // Device has already been PAIRED so update connection page
                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);
                    }
                }
            }
        }

        return true;
    }


    private boolean startDeviceConnection(final DeviceInfo device_info)
    {
        if (device_info.isDeviceHumanReadableDeviceIdValid())
        {
            Log.d(TAG, "startDeviceConnection : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : actual_device_connection_status = " + device_info.getActualDeviceConnectionStatus());

            switch (device_info.getActualDeviceConnectionStatus())
            {
                case SEARCHING:
                {
                    Log.d(TAG, "startDeviceConnection : " + device_info.getSensorTypeAndDeviceTypeAsString() + " should be connected but IS NOT");

                    current_scan_type = device_info.device_type;

                    setupAndRunBluetoothConnectionProgressTimer(device_info);

                    if(device_info.dummy_data_mode)
                    {
                        Log.d(TAG, "startDeviceConnection : DUMMY DATA DEVICE with desired_bluetooth_search_period_in_milliseconds = " + device_info.desired_bluetooth_search_period_in_milliseconds);

                        dummy_device_scan_handler.postDelayed(() -> {
                            Log.d(TAG, "startDeviceConnection : dummy data mode = true - automatically succeeding on scan");

                            device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);

                            if(device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX)
                            {
                                // Simulate that the Nonin was connected as normal
                                sendBroadcast(new Intent(NoninWristOx.NONIN_WRIST_OX__CONNECTED));
                            }

                            dummy_data_instance.initVariables(device_info);

                            patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                            device_info.cancelBluetoothConnectionTimer();

                            checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                        }, 5*DateUtils.SECOND_IN_MILLIS);
                    }
                    else
                    {
                        Log.d(TAG, "startDeviceConnection : desired_bluetooth_search_period_in_milliseconds = " + device_info.desired_bluetooth_search_period_in_milliseconds);

                        if(device_info instanceof BtleSensorDevice)
                        {
                            BtleSensorDevice sweetblue_device = (BtleSensorDevice) device_info;
                            patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(sweetblue_device, BluetoothStatus.BLUETOOTH_SCAN_STARTED);

                            // Stops scanning after a pre-defined scan period.
                            startBleScanTimeout(sweetblue_device);

                            Log.e(TAG, "Searching for device type " + device_info.getSensorTypeAndDeviceTypeAsString() + " with address: " + device_info.bluetooth_address);

                            sweetblue_device.resetForUserControlledScan();

                            device_info_manager.getBluetoothLeDeviceController().connectWithScan((device_info.desired_bluetooth_search_period_in_milliseconds / 1000), sweetblue_device);
                        }
                        else if(device_info instanceof BtClassicSensorDevice)
                        {
                            switch (device_info.device_type)
                            {
                                case DEVICE_TYPE__NONIN_WRIST_OX:
                                {
                                    sendBroadcast(new Intent(NoninWristOx.SET_ADDED_TO_SESSION));
                                }
                                break;

                                case DEVICE_TYPE__AND_UA767:
                                {
                                    // Don't set the AnD as added - this is done after pairing.
                                }
                                break;

                                case DEVICE_TYPE__FORA_IR20:
                                {
                                    sendBroadcast(new Intent(Fora_Ir20.SET_ADDED_TO_SESSION));
                                }
                                break;
                            }

                            bluetooth_pairing.searchAndPair(device_info.bluetooth_address, device_info.pairing_pin_number);
                        }
                    }
                    return true;
                }

                case NOT_PAIRED:
                {
                    // Device scan previously failed
                    Log.i(TAG, "startDeviceConnection : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : scan previously failed");
                }
                break;

                case UNBONDED:
                {
                    Log.i(TAG, "startDeviceConnection : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : UNBONDED");
                }
                break;

                default:
                {
                    // Device has already been paired
                    // So don't need to re-pair.
                    Log.i(TAG, "startDeviceConnection : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : DEFAULT : is already connected");
                }
                break;
            }
        }

        return false;
    }


    private void startBleScanTimeout(final DeviceInfo device_info)
    {
        btle_scanning_timeout_handler.removeCallbacksAndMessages(null);

        btle_scanning_timeout_handler.postDelayed(() -> {
            Log.e(TAG, "btle_scanning_timeout_handler fired");

            stopOnGoingBluetoothScan();

            checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
        }, device_info.desired_bluetooth_search_period_in_milliseconds);
    }


    private void checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(DeviceInfo device_info)
    {
        if(current_scan_type == device_info.device_type)
        {
            current_scan_type = DeviceType.DEVICE_TYPE__INVALID;

            device_info.cancelBluetoothConnectionTimer();

            if (device_info instanceof BtleSensorDevice)
            {
                btle_scanning_timeout_handler.removeCallbacksAndMessages(null);
            }

            connectNextDeviceToGatewayIfNeeded();
        }
        else
        {
            connectNextDeviceToGatewayIfNeededAndNoScanRunning();
        }
    }


    private void connectNextDeviceToGatewayIfNeededAndNoScanRunning()
    {
        if(current_scan_type == DeviceType.DEVICE_TYPE__INVALID)
        {
            connectNextDeviceToGatewayIfNeeded();
        }
    }


    private void connectNextDeviceToGatewayIfNeeded()
    {
        boolean all_desired_devices_connected = true;

        ArrayList<SensorType> connection_order_list = new ArrayList<>();

        connection_order_list.add(SensorType.SENSOR_TYPE__LIFETOUCH);
        connection_order_list.add(SensorType.SENSOR_TYPE__TEMPERATURE);
        connection_order_list.add(SensorType.SENSOR_TYPE__SPO2);
        connection_order_list.add(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        connection_order_list.add(SensorType.SENSOR_TYPE__WEIGHT_SCALE);

        for (SensorType sensor_type : connection_order_list)
        {
            DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(sensor_type);

            all_desired_devices_connected &= checkConnectionStatusAndUpdateUI(device_info);
        }

        Log.d(TAG, "connectNextDeviceToGatewayIfNeeded : all_desired_devices_connected = " + all_desired_devices_connected);

        for (SensorType sensor_type : connection_order_list)
        {
            DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(sensor_type);

            if(startDeviceConnection(device_info))
                return;
        }

        if (all_desired_devices_connected)
        {
            Log.d(TAG, "all_desired_devices_connected == true");

            // Reset the variable to ensure we only do this once
            patient_session_info.restored_session = false;

            if (gateway_settings.getEnableDfuBootloader())
            {
                // Check if any Firmware Updates pending
                if (firmware_image_manager.firmwareUpdatePending())
                {
                    Log.d(TAG, "Device(s) have firmware updates ready");

                    for (DeviceType device_type : DeviceType.values())
                    {
                        if (firmware_image_manager.firmwareUpdatePending(device_type))
                        {
                            Log.d(TAG, "Firmware update pending for " + device_type);

                            DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(device_type);

                            // Only try and start the DFU if we are connected to the device and NOT already in DFU mode. This code may have been called by another device connecting....
                            if ((device_info.isActualDeviceConnectionStatusConnected())
                                    && (!device_info.isDeviceSessionInProgress())) // Also skip DFU if there is already a valid device session running - as it may be a device coming back from out of range or recovering from a reset.
                            {
                                Log.d(TAG, "Updating " + device_info.getSensorTypeAndDeviceTypeAsString() + " firmware from " + device_info.getDeviceFirmwareVersion() + " to " + firmware_image_manager.getLatestStoredFirmwareVersion(device_type));

                                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.DFU);

                                // Update the firmware. Device will reset after the firmware download
                                device_info.firmwareOtaUpdate(firmware_image_manager.getLatestStoredBinary(device_type));

                                // set the dfu progress received time so that it has an initial value of when we start the DFU
                                device_info.dfuProgressReceived(getNtpTimeNowInMilliseconds());
                            }
                            else
                            {
                                Log.d(TAG, device_type + " not in DeviceConnectionStatus.CONNECTED or session already in progress. DeviceConnectionStatus = " + device_info.getActualDeviceConnectionStatus() + " and  isDeviceSessionInProgress() = " + device_info.isDeviceSessionInProgress());
                            }
                        }
                    }
                }
                else
                {
                    // Let the UI know it can show the Show Monitoring button now all the desired devices are connected
                    patient_gateway_outgoing_commands.sendCommandEndOfDeviceConnection(true, patient_session_info.restored_session);
                }
            }
            else
            {
                // Let the UI know it can show the Show Monitoring button now all the desired devices are connected
                patient_gateway_outgoing_commands.sendCommandEndOfDeviceConnection(true, patient_session_info.restored_session);
            }
        }
        else
        {
            // Bluetooth devices not connected
            Log.e(TAG, "connectNextDeviceToGatewayIfNeeded finished attempting to connect devices but one or more NOT CONNECTED");

            // Send command to UI to display "Search Again" Button for un paired devices
            sendCommandToDisplaySearchAgainButton();
        }
    }


    private static IntentFilter makeNoninWristOxIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NoninWristOx.NONIN_WRIST_OX__CONNECTED);
        intentFilter.addAction(NoninWristOx.NONIN_WRIST_OX__NEW_MEASUREMENT_DATA);
        intentFilter.addAction(NoninWristOx.NONIN_WRIST_OX__NEW_SETUP_DATA_SAMPLE);
        intentFilter.addAction(NoninWristOx.NONIN_WRIST_OX__NEW_SETUP_DATA_FRAME_INFO);
        intentFilter.addAction(NoninWristOx.NONIN_WRIST_OX__DISCONNECTED);
        intentFilter.addAction(NoninWristOx.NONIN_WRIST_OX__TEMPORARY_DISCONNECTION);
        intentFilter.addAction(NoninWristOx.NONIN_WRIST_OX__CHECKSUM_ERROR);
        return intentFilter;
    }


    private static IntentFilter makeNoninWristOxPlaybackIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(NoninWristOxPlaybackDecoder.NONIN_WRIST_OX__INTERMEDIATE_MEASUREMENTS_FROM_PLAYBACK);
        intentFilter.addAction(BluetoothLeNoninWristOx.ACTION_NONIN_PLAYBACK_EXPECTED);
        intentFilter.addAction(BluetoothLeNoninWristOx.ACTION_NONIN_PLAYBACK_IS_BEING_RECEIVED);
        intentFilter.addAction(BluetoothLeNoninWristOx.ACTION_NONIN_SETUP_MODE_IS_BLOCKED);
        return intentFilter;
    }


    private static IntentFilter makeForaIr20IntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Fora_Ir20.FORA_IR20__CONNECTED);
        intentFilter.addAction(Fora_Ir20.FORA_IR20__DISCONNECTED);
        intentFilter.addAction(Fora_Ir20.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    private boolean isPatientSessionRunning()
    {
        return patient_session_info.patient_session_running;
    }


    private boolean send_pulse_ox_battery_level = false;    // boolean to send the pulse-ox battery level once at the beginning. Then send battery level once every minute

    private final static ConcurrentLinkedQueue<IntermediateMeasurement> pulse_ox_intermediate_measurements = new ConcurrentLinkedQueue<>();
    private boolean nonin_playback_expected = false;  // This is changed when a Nonin playback has been fully decoded, which may be a few minutes after the following
    private boolean nonin_setup_mode_blocked = false; // Preventing the possibility of setup mode on Nonin until it is known that playback is not required as setup mode can interfere with playback


    private final BroadcastReceiver broadcastReceiverNoninWristOx2 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            
            spO2_spot_measurements_enabled = gateway_settings.getEnableSpO2SpotMeasurements();

            DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX);

            if(Objects.equals(action, NoninWristOx.NONIN_WRIST_OX__CONNECTED))
            {
                Log.d(TAG, "NoninWristOx.NONIN_WRIST_OX__CONNECTED");

                spo2_intermediate_tidy_up_timer.cancel();

                send_pulse_ox_battery_level = false;

                storeConnectionEventIfSessionRunning(device_info, LocalDatabaseStorage.ConnectionEvent.DEVICE_CONNECTED);

                patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);

                // Tell Server about the new connection status
                reportGatewayStatusToServer();

                // Tell the UI about the actual_device_connection_status change
                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                if (device_info.dummy_data_mode == false)
                {
                    checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                }
            }

            if (Objects.equals(action, NoninWristOx.NONIN_WRIST_OX__NEW_MEASUREMENT_DATA))
            {
                boolean valid_reading = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__VALID_READING, false);

                int sp_o2 = intent.getIntExtra(NoninWristOx.NONIN_WRIST_OX__SP_O2, -1);
                int pulse = intent.getIntExtra(NoninWristOx.NONIN_WRIST_OX__HEART_RATE, -1);
                long timestamp_in_ms = intent.getLongExtra(NoninWristOx.NONIN_WRIST_OX__TIMESTAMP, -1);

                boolean low_battery = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__LOW_BATTERY, false);

                int battery_percentage;

                if (low_battery)
                {
                    battery_percentage = 0;
                }
                else
                {
                    battery_percentage = 100;
                }

                device_info.device_disconnected_from_body =  intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__DETACHED_FROM_PATIENT, false);

                boolean artifact = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__ARTIFACT, false);
                boolean out_of_track = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__OUT_OF_TRACK, false);
                boolean low_perfusion = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__LOW_PERFUSION, false);
                boolean marginal_perfusion = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__MARGINAL_PERFUSION, false);
                boolean smartpoint_algorithm = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__SMART_POINT_ALGORITHM, false);

                if (!disableCommentsForSpeed()) {
                    Log.i(TAG, "NoninWristOx.NONIN_WRIST_OX__NEW_DATA : SpO2 reading from the Nonin is " + sp_o2
                            + ". pulse_ox_detached_from_patient = " + device_info.device_disconnected_from_body
                            + ". pulse = " + pulse
                            + ". timestamp_in_ms = " + timestamp_in_ms
                            + ". low_battery_condition = " + low_battery
                            + ". artifact = " + artifact
                            + ". out_of_track = " + out_of_track
                            + ". low_perfusion = " + low_perfusion
                            + ". marginal_perfusion = " + marginal_perfusion
                            + ". smartpoint_algorithm = " + smartpoint_algorithm
                    );
                }

                // Wait until there is a Patient Session AND Device Session in progress before processing the data.
                // If the user has scanned the Pulse Ox QR code, and put it on their finger, it will take measurements. However they might not have pressed "Starting Monitoring" which
                // would start a new Patient/Device Session off. This means that all the Device Session Info etc is invalid
                if (isPatientSessionRunning())
                {
                    if (device_info.isDeviceSessionInProgress())
                    {
                        // Tell the UI if the Pulse Ox is on the finger or not
                        patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

                        // Nonin Data is send after three packets of setup mode data
                        if(device_info.isInSetupMode() == false)
                        {
                            server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);
                        }


                        // Start of Intermediate measurement processing
                        String timestamp_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms);

                        // Assume invalid measurement to start with.
                        IntermediateSpO2 pulse_ox_intermediate_measurement = new IntermediateSpO2(INVALID_MEASUREMENT, INVALID_MEASUREMENT, timestamp_in_ms);

                        // If pulse ox is on patient....
                        if(device_info.device_disconnected_from_body == false)
                        {
                            // ....process newly received data
                            if (valid_reading)
                            {
                                if (!disableCommentsForSpeed())
                                {
                                    Log.d(TAG, "pulse_ox_intermediate_measurement at " + timestamp_as_string + " = " + sp_o2 + ". device_info__pulse_ox.android_database_device_session_id = " + device_info.getAndroidDeviceSessionId());
                                }

                                // Update the measurement with the valid readings
                                pulse_ox_intermediate_measurement.setSpO2(sp_o2);
                                pulse_ox_intermediate_measurement.setPulse(pulse);

                                if(send_pulse_ox_battery_level == false)
                                {
                                    // Save the Battery reading so can be read from the UI on demand
                                    device_info.setLastBatteryReading(battery_percentage, -1, timestamp_in_ms);

                                    // Show the battery on the UI screen as soon as possible after connecting.
                                    patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                    send_pulse_ox_battery_level = true;
                                }

                                // Reset the leads-off counter.
                                device_info.counter_leads_off_after_last_valid_data = 0;
                                device_info.timestamp_leads_off_disconnection = 0;
                            }
                            else
                            {
                                // Invalid intermediate measurement
                                Log.e(TAG, "pulse_ox_intermediate_measurement Invalid Reading @ " + timestamp_as_string);
                            }
                        }
                        else
                        {
                            // Leads-off detected. Increment leads-off counters
                            device_info.counter_leads_off_after_last_valid_data++;
                            device_info.timestamp_leads_off_disconnection = getNtpTimeNowInMilliseconds();
                            device_info.counter_total_leads_off++;

                            // Pulse Ox Finger Off detected
                            Log.e(TAG, "pulse_ox_intermediate_measurement Finger-off @ " + timestamp_as_string);
                        }

                        boolean has_one_minute_elapsed;

                        has_one_minute_elapsed = intermediate_spo2_processor.processMeasurement(pulse_ox_intermediate_measurement); //, gateway_settings.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid());

                        if(nonin_playback_expected == false)
                        {
                        has_one_minute_elapsed |= intermediate_spo2_processor.processAnyOutstandingData();
                        }

                        // Save the Battery reading so can be read from the UI on demand
                        device_info.setLastBatteryReading(battery_percentage, -1, timestamp_in_ms);

                        // Write Nonin Battery value
                        patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                        if (has_one_minute_elapsed)
                        {
                            Log.e(TAG, "Store Nonin Battery measurement");

                            // Nonin Classic does not give us this
                            int battery_millivolts = 0;

                            MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                    battery_millivolts,
                                    battery_percentage,
                                    timestamp_in_ms,
                                    getNtpTimeNowInMilliseconds());

                            local_database_storage.storeOximeterBatteryMeasurement(device_info, measurement);
                        }

                        // Sync intermediate measurements every minute in timer tick
                    }
                    else
                    {
                        Log.e(TAG, "pulse_ox_intermediate_measurement ERROR : Received value but no Device Session yet");
                    }
                }
                else
                {
                    Log.d(TAG, "Received pulse_ox_intermediate_measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms) + " but ignored as no Patient Session in progress");
                }
            }

            if (Objects.equals(action, NoninWristOx.NONIN_WRIST_OX__NEW_SETUP_DATA_SAMPLE))
            {
                // Wait until there is a Patient Session AND Device Session in progress before processing the data.
                // If the user has scanned the Pulse Ox QR code, and put it on their finger, it will take measurements. However they might not have pressed "Starting Monitoring" which
                // would start a new Patient/Device Session off. This means that all the Device Session Info etc is invalid
                if ((isPatientSessionRunning()) && (device_info.isDeviceSessionInProgress()))
                {
                    // Get data from Intent
                    short sample = intent.getShortExtra(NoninWristOx.NONIN_WRIST_OX__SETUP_MODE_SAMPLE, (short)0);
                    long timestamp = intent.getLongExtra(NoninWristOx.NONIN_WRIST_OX__SETUP_MODE_TIMESTAMP, 0);

                    MeasurementSetupModeDataPoint datapoint = new MeasurementSetupModeDataPoint(sample, timestamp);

                    processTimestampForSetupModeLogEntry(device_info, datapoint.timestamp_in_ms);

                    // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                    // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                    device_info.queue_setup_mode_datapoints.add(datapoint);

                    patient_gateway_outgoing_commands.sendNewSetupModeSampleToUserInterface(device_info.device_type, datapoint);

                    if (device_info.isInServerInitedRawDataMode())
                    {
                        device_info.queue_setup_mode_datapoints_for_server.add(datapoint);

                        // Wait for 1 second's worth of setup mode data and send 75 readings in 1 message, otherwise graphs can lag and disappear if many (4+) Nonins are sending setup mode data
                        if (device_info.queue_setup_mode_datapoints_for_server.size() > 74)
                        {
                            // Scale the original Nonin Setup data up to 16 bit.
                            server_syncing.sendDeviceSetupModeData(patient_session_info.server_patient_session_id, device_info, 256);
                        }
                    }
                }
                else
                {
                    Log.d(TAG, "NONIN_WRIST_OX__NEW_SETUP_DATA. Received data but ignored as no Patient or Device Session in progress");
                }
            }

            if (Objects.equals(action, NoninWristOx.NONIN_WRIST_OX__NEW_SETUP_DATA_FRAME_INFO))
            {
                // Wait until there is a Patient Session AND Device Session in progress before processing the data.
                // If the user has scanned the Pulse Ox QR code, and put it on their finger, it will take measurements. However they might not have pressed "Starting Monitoring" which
                // would start a new Patient/Device Session off. This means that all the Device Session Info etc is invalid
                if ((isPatientSessionRunning()) && (device_info.isDeviceSessionInProgress()))
                {
                    // Get data from Intent
                    //boolean valid_reading = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__VALID_READING, false);

                    //int sp_o2 = intent.getIntExtra(NoninWristOx.NONIN_WRIST_OX__SP_O2, -1);
                    //int pulse = intent.getIntExtra(NoninWristOx.NONIN_WRIST_OX__HEART_RATE, -1);

                    //boolean low_battery = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__LOW_BATTERY, false);
                    //boolean critically_low_battery = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__CRITICALLY_LOW_BATTERY, false);

                    //boolean artifact = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__ARTIFACT, false);
                    //boolean out_of_track = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__OUT_OF_TRACK, false);
                    //boolean low_perfusion = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__LOW_PERFUSION, false);
                    //boolean marginal_perfusion = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__MARGINAL_PERFUSION, false);
                    //boolean smartpoint_algorithm = intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__SMART_POINT_ALGORITHM, false);

                    int software_revision = intent.getIntExtra(NoninWristOx.NONIN_WRIST_OX__SOFTWARE_REVISION, -1);
                    String software_string = intent.getStringExtra(NoninWristOx.NONIN_WRIST_OX__SOFTWARE_STRING);

                    device_info.setDeviceFirmwareVersion(software_string, software_revision);

                    device_info.device_disconnected_from_body =  intent.getBooleanExtra(NoninWristOx.NONIN_WRIST_OX__DETACHED_FROM_PATIENT, false);

                    if (device_info.device_disconnected_from_body)
                    {
                        // if pulse-ox is detached from the patient, go back to the normal mode and display leads-off page.
                        Log.e(TAG, "NONIN_WRIST_OX__NEW_SETUP_DATA_FRAME_INFO : Detached from patient");

                        // Send detached from patient command to User Interface
                        patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

                        // This sends command to Nonin Pulse-ox driver.
                        exitSetupModeIfRunning(device_info);
                    }
                    else
                    {
                        Log.d(TAG, "NONIN_WRIST_OX__NEW_SETUP_DATA_FRAME_INFO : Attached to patient");
                    }

                    server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);
                }
                else
                {
                    Log.d(TAG, "NONIN_WRIST_OX__NEW_SETUP_DATA_FRAME_INFO. Received data but ignored as no Patient or Device Session in progress");
                }
            }


            if(Objects.equals(action, NoninWristOx.NONIN_WRIST_OX__DISCONNECTED))
            {
                Log.d(TAG, "NoninWristOx.NONIN_WRIST_OX__DISCONNECTED");

                restartSpO2IntermediateTidyUpTimerIfRequired();

                send_pulse_ox_battery_level = false;

                storeConnectionEventIfSessionRunning(device_info, LocalDatabaseStorage.ConnectionEvent.DEVICE_DISCONNECTED);

                // If nonin is disconnected while in setup mode than reset the operation mode to normal and send command to portal
                if(device_info.isInSetupMode())
                {
                    exitDeviceSetupMode(device_info, true);
                }

                patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_DISCONNECTED);

                if(device_info.isActualDeviceConnectionStatusNotPaired())
                {
                    Log.d(TAG, "Pulse ox disconnect received, but device already unpaired!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
                else
                {
                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.DISCONNECTED);
                }

                // Remove any "Finger Off" status if the device is disconnected as do not "know" what the state is now
                device_info.device_disconnected_from_body = false;
                server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);

                // Tell Server about the new connection status
                reportGatewayStatusToServer();

                // Tell the UI about the actual_device_connection_status change
                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
            }

            if(Objects.equals(action, NoninWristOx.NONIN_WRIST_OX__TEMPORARY_DISCONNECTION))
            {
                Log.d(TAG, "NoninWristOx.NONIN_WRIST_OX__TEMPORARY_DISCONNECTION");
                send_pulse_ox_battery_level = false;
            }

            if(Objects.equals(action, NoninWristOx.NONIN_WRIST_OX__CHECKSUM_ERROR))
            {
                Log.e(TAG, "NoninWristOx.NONIN_WRIST_OX__CHECKSUM_ERROR");

                if(device_info.isInSetupMode())
                {
                    exitDeviceSetupMode(device_info, true);
                }
                else
                {
                    // Nonin wrist-ox is already in the Normal mode. No need to send the OperatingMode state to Normal
                }
            }
        }
    };

    private final BroadcastReceiver broadcastReceiverNoninWristOx2Playback = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            if (Objects.equals(action, NoninWristOxPlaybackDecoder.NONIN_WRIST_OX__INTERMEDIATE_MEASUREMENTS_FROM_PLAYBACK))
            {
                Bundle bundle = intent.getExtras();

                ArrayList<IntermediateSpO2> received_playback_measurements = bundle.getParcelableArrayList(NONIN_WRIST_OX__READINGS_FROM_PLAYBACK);

                Log.d(TAG, "NONIN_WRIST_OX__INTERMEDIATE_MEASUREMENTS_FROM_PLAYBACK " + received_playback_measurements.size() + " readings");

                for (int i=0; i < received_playback_measurements.size()-1; i++)
                {
                    intermediate_spo2_processor.processMeasurement(received_playback_measurements.get(i));
                }

                Log.d(TAG, "NONIN_WRIST_OX__INTERMEDIATE_MEASUREMENTS_FROM_PLAYBACK processed measurements");
            }

            if (Objects.equals(action, BluetoothLeNoninWristOx.ACTION_NONIN_PLAYBACK_EXPECTED))
            {
                nonin_playback_expected = intent.getBooleanExtra("expected", false);

                if (nonin_playback_expected == false)
                {
                    intermediate_spo2_processor.processAnyOutstandingData();
                }

                Log.d(TAG, "ACTION_NONIN_PLAYBACK_EXPECTED nonin_playback_expected? nonin_playback_expected = " + nonin_playback_expected);
            }

            if (Objects.equals(action, BluetoothLeNoninWristOx.ACTION_NONIN_PLAYBACK_IS_BEING_RECEIVED))
            {
                // This is changed as soon as a Nonin playback is known to be necessary, i.e. when the playback command has been sent to the device
                boolean nonin_playback_ongoing = intent.getBooleanExtra("ongoing", false);

                Log.d(TAG, "ACTION_NONIN_PLAYBACK_IS_BEING_RECEIVED " + nonin_playback_ongoing);

                patient_gateway_outgoing_commands.reportNoninPlaybackIsOngoingToUserInterface(nonin_playback_ongoing);
            }

            if (Objects.equals(action, BluetoothLeNoninWristOx.ACTION_NONIN_SETUP_MODE_IS_BLOCKED))
            {
                nonin_setup_mode_blocked = intent.getBooleanExtra("nonin_setup_mode_blocked", false);

                patient_gateway_outgoing_commands.reportNoninPlaybackMightOccurToUserInterface();

                Log.d(TAG, "ACTION_NONIN_SETUP_MODE_IS_BLOCKED  nonin_setup_mode_blocked = " + nonin_setup_mode_blocked);
            }
        }
    };


    private final BroadcastReceiver broadcastReceiverForaIr20 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__FORA_IR20);

            if(Objects.equals(action, Fora_Ir20.FORA_IR20__CONNECTED))
            {
                patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);
                storeConnectionEventIfSessionRunning(device_info, LocalDatabaseStorage.ConnectionEvent.DEVICE_CONNECTED);
                // Tell Server about the new connection status
                reportGatewayStatusToServer();

                // Tell the UI about the actual_device_connection_status change
                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                if (device_info.dummy_data_mode == false)
                {
                    checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                }
            }

            if (Objects.equals(action, Fora_Ir20.FORA_IR20__DISCONNECTED))
            {
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.DISCONNECTED);

                storeConnectionEventIfSessionRunning(device_info, LocalDatabaseStorage.ConnectionEvent.DEVICE_DISCONNECTED);

                patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_DISCONNECTED);

                // Tell Server about the new connection status
                reportGatewayStatusToServer();

                // Tell the UI about the actual_device_connection_status change
                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
            }

            if (Objects.equals(action, Fora_Ir20.ACTION_DATA_AVAILABLE))
            {
                String data = intent.getStringExtra(Fora_Ir20.FORA_DATA_AS_STRING);
                String data_type = intent.getStringExtra(Fora_Ir20.DATA_TYPE);

                if ((data == null) || (data_type == null))
                {
                    if (data == null)
                    {
                        Log.d(TAG, "data = null");
                    }

                    if (data_type == null)
                    {
                        Log.d(TAG, "data_type = null");
                    }
                }
                else
                {
                    if (data_type.equals(Fora_Ir20.DATATYPE_FORA_TEMPERATURE_MEASUREMENT))
                    {
                        String string_temperature = data.split("__")[0];
                        String string_timestamp = data.split("__")[1];

                        double temperature_in = Double.parseDouble(string_temperature);

                        BigDecimal temperature_value = new BigDecimal(temperature_in);

                        if (gateway_settings.getManufacturingModeEnabledStatus())
                        {
                            temperature_value = temperature_value.setScale(2, RoundingMode.HALF_UP);
                        }
                        else
                        {
                            temperature_value = temperature_value.setScale(1, RoundingMode.HALF_UP);
                        }

                        long timestamp_in_ms;
                        timestamp_in_ms = Long.parseLong(string_timestamp) ;

                        int measurement_validity_time_in_seconds = gateway_settings.getThirdPartyTemperatureLongTermMeasurementTimeoutInMinutes() * 60;

                        MeasurementTemperature measurement = new MeasurementTemperature(temperature_value.doubleValue(), timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        // Store measurement in database
                        local_database_storage.storeLifetempTemperatureMeasurement(device_info, measurement);

                        reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.TEMPERATURE, measurement);
                    }
                }
            }
        }
    };


    private void storeConnectionEventIfSessionRunning(DeviceInfo device_info, LocalDatabaseStorage.ConnectionEvent connection_event)
    {
        if (isPatientSessionRunning())
        {
            if (device_info.isDeviceSessionInProgress())
            {
                local_database_storage.storeConnectionEvent(patient_session_info.android_database_patient_session_id,
                        device_info.getAndroidDeviceSessionId(),
                        connection_event,
                        getNtpTimeNowInMilliseconds());
            }
        }
    }


    private static IntentFilter makeAnDUa767IntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AnD_UA767.CONNECTED);
        intentFilter.addAction(AnD_UA767.NEW_DATA);
        intentFilter.addAction(AnD_UA767.DISCONNECTED);
        return intentFilter;
    }


    private final BroadcastReceiver broadcastReceiverAnDUa767Data = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_UA767);

            if(Objects.equals(action, AnD_UA767.CONNECTED))
            {
                Log.d(TAG, "AnD_UA767.CONNECTED");

                patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);

                // Tell the UI about the actual_device_connection_status change
                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
            }

            if(Objects.equals(action, AnD_UA767.NEW_DATA))
            {
                final And_UA767_MeasurementStatus.Status measurement_status = And_UA767_MeasurementStatus.Status.values()[intent.getIntExtra(AnD_UA767.MEASUREMENT_STATUS, -1)];

                int diastolic = intent.getIntExtra(AnD_UA767.DIASTOLIC, -1);
                int systolic = intent.getIntExtra(AnD_UA767.SYSTOLIC, -1);
                int pulse_rate = intent.getIntExtra(AnD_UA767.PULSE_RATE, -1);
                final int mean_arterial_pressure = intent.getIntExtra(AnD_UA767.MEAN_ARTERIAL_PRESSURE, -1);

                final String device_name = intent.getStringExtra(AnD_UA767.DEVICE_NAME);
                final int device_type = intent.getIntExtra(AnD_UA767.DEVICE_TYPE, -1);
                final String serial_number = intent.getStringExtra(AnD_UA767.SERIAL_NUMBER_STRING);
                final int device_firmware_revision = intent.getIntExtra(AnD_UA767.DEVICE_FIRMWARE_REVISION, -1);
                final int device_hardware_revision = intent.getIntExtra(AnD_UA767.DEVICE_HARDWARE_REVISION, -1);

                long measurement_timestamp_in_ms = intent.getLongExtra(AnD_UA767.MEASUREMENT_TIME, -1);
                Date measurement_time = new Date();
                measurement_time.setTime(measurement_timestamp_in_ms);

                long transmission_time_int = intent.getLongExtra(AnD_UA767.TRANSMISSION_TIME, -1);
                Date transmission_time = new Date();
                transmission_time.setTime(transmission_time_int);

                final String battery_status_string = intent.getStringExtra(AnD_UA767.DEVICE_BATTERY_STATUS);

                // Only process the received data is there is currently a Patient Session in progress.....
                if (isPatientSessionRunning())
                {
                    // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                    if (device_info.isDeviceSessionInProgress())
                    {
                        // ....and data captured date is after session start date
                        if(measurement_time.after(new Date(device_info.device_session_start_time)))
                        {
                            int battery_percentage;

                            if (battery_status_string.contentEquals(AnD_UA767.BATTERY_GOOD))
                            {
                                battery_percentage = 100;
                            }
                            else
                            {
                                battery_percentage = 0;
                            }

                            Log.d(TAG, "diastolic = " + diastolic);
                            Log.d(TAG, "systolic = " + systolic);
                            Log.d(TAG, "pulse_rate = " + pulse_rate);
                            Log.d(TAG, "mean_arterial_pressure = " + mean_arterial_pressure);
                            Log.d(TAG, "device name = " + device_name);
                            Log.d(TAG, "device_type = " + device_type);
                            Log.d(TAG, "serial_number = " + serial_number);
                            Log.d(TAG, "device_firmware_revision = " + device_firmware_revision);
                            Log.d(TAG, "device_hardware_revision = " + device_hardware_revision);
                            Log.d(TAG, "measurement_time = " + measurement_time);
                            Log.d(TAG, "transmission_time = " + transmission_time);
                            Log.d(TAG, "battery_status_string = " + battery_status_string);
                            Log.d(TAG, "battery_percentage = " + battery_percentage);

                            switch (measurement_status)
                            {
                                case MEASUREMENT_ERROR:
                                case CUFF_ERROR:
                                case IRREGULAR_HEART_BEAT:
                                case LOW_BATTERY_STATUS_AND_IRREGULAR_HEART_BEAT:
                                {
                                    Log.d(TAG, "Processing error : " + measurement_status);

                                    systolic = ErrorCodes.ERROR_CODE__AND_MEASUREMENT_ERROR;
                                    diastolic = ErrorCodes.ERROR_CODE__AND_MEASUREMENT_ERROR;
                                    pulse_rate = ErrorCodes.ERROR_CODE__AND_MEASUREMENT_ERROR;
                                }
                                // No break - continue to process data using the above error code values

                                case LOW_BATTERY_STATUS:
                                case NON_INITED_ENUM_VALUE:
                                case CORRECT_MEASUREMENT:
                                {
                                    Log.d(TAG, "Processing BP measurement : " + measurement_status);

                                    MeasurementBatteryReading battery_measurement = new MeasurementBatteryReading(
                                            0,
                                            battery_percentage,
                                            measurement_timestamp_in_ms,
                                            getNtpTimeNowInMilliseconds());

                                    local_database_storage.storeBloodPressureBatteryMeasurement(device_info, battery_measurement);

                                    // Save the Battery reading so can be read from the UI on demand
                                    device_info.setLastBatteryReading(battery_percentage, -1, measurement_timestamp_in_ms);

                                    patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                    if (systolic != 0 && diastolic != 0)
                                    {
                                        int measurement_validity_time_in_seconds = gateway_settings.getBloodPressureLongTermMeasurementTimeoutInMinutes() * 60;

                                        MeasurementBloodPressure measurement = new MeasurementBloodPressure(systolic, diastolic, pulse_rate, measurement_timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                                        local_database_storage.storeBloodPressureMeasurement(device_info, measurement);

                                        reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.BLOOD_PRESSURE, measurement);
                                    }
                                }
                                break;
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as captured time is after session start time");
                        }
                    }
                    else
                    {
                        Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                    }
                }
                else
                {
                    Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                }
            }

            if(Objects.equals(action, AnD_UA767.DISCONNECTED))
            {
                Log.d(TAG, "AnD_UA767.DISCONNECTED");

                patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_DISCONNECTED);

                if(device_info.isActualDeviceConnectionStatusNotPaired())
                {
                    Log.d(TAG, "Blood Pressure disconnect received, but device already unpaired!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
                else
                {
                    // Note : If UI is in FragmentDeviceConnection then Disconnected is taken as Connected as it is PAIRED.
                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.DISCONNECTED);
                }

                // Tell Server about the new connection status
                reportGatewayStatusToServer();

                // Tell the UI about the actual_device_connection_status change
                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
            }
        }
    };


    private final ConcurrentLinkedQueue<Integer> queue_firmware_images_to_download = new ConcurrentLinkedQueue<>();


    public void handlePatientIdStatusResponse(boolean result, boolean patient_id_in_use)
    {
        Log.w(TAG, "SERVER_INTERFACE_STATUS_UPDATE__PATIENT_ID_STATUS_RECEIVED : patient_id_in_use     =   " + patient_id_in_use + "  with the result ->>>>>" + result);

        String first_name = "First Name";
        String last_name = "Last Name";
        String dob = "DD/OO/BB";
        String gender = "M/F";
        final boolean complete = true;
        PatientDetailsLookupStatus status = PatientDetailsLookupStatus.PATIENT_FOUND;

        patient_gateway_outgoing_commands.sendCommandReportPatientNameFromServerLookupOfHospitalPatientId(first_name, last_name, dob, gender, complete, status);
    }


    public void handleServerResponseComplete(boolean success, HttpOperationType http_operation_type, ActiveOrOldSession active_or_old_session)
    {
        reportWebserviceResult(success, http_operation_type, active_or_old_session, server_sync_status);

        handleSetupWizardResult(success, http_operation_type);
    }


    private void handleSetupWizardResult(boolean success, HttpOperationType type)
    {
        switch (type)
        {
            case WARD_DETAILS_LIST:
            case BED_DETAILS_LIST:
            {
                if(success)
                {
                    // not a success until ward and bed are set by the user.
                    // setup_wizard.getWardsAndBedsSuccess();
                }
                else
                {
                    setup_wizard.getWardsAndBedsFailure();
                }
            }
            break;

            case GET_GATEWAY_CONFIG:
            {
                if(success)
                {
                    setup_wizard.getGatewaySettingsSuccess();
                }
                else
                {
                    setup_wizard.getGatewaySettingsFailure();
                }
            }
            break;

            case GET_SERVER_CONFIGURABLE_TEXT:
            {
                if(success)
                {
                    setup_wizard.getServerConfigurableTextSuccess();
                }
                else
                {
                    setup_wizard.getServerConfigurableTextFailure();
                }
            }
            break;

            case GET_VIEWABLE_WEBPAGES:
            {
                if(success)
                {
                    setup_wizard.getWebPagesSuccess();
                }
                else
                {
                    setup_wizard.getWebPagesFailure();
                }
            }
            break;

            default:
            {
                // do nothing. Either the type doesn't correspond to an installation step, or it is handled elsewhere.
            }
            break;
        }
    }


    public void handleServerInvalidStatusCode(int server_status_code, String server_message)
    {
        Log.e(TAG, "SERVER_INTERFACE_STATUS_UPDATE__INVALID_SERVER_STATUS_CODE : Status code is " + server_status_code + ".    Server Message " + server_message);

        patient_gateway_outgoing_commands.sendCommandReportInvalidServerStatusCode(server_status_code, server_message);
    }


    public void handleGetDeviceStatusResponse(boolean result, String ward_name, String bed_name, DeviceType device_type, int human_readable_device_id, boolean device_in_use)
    {
        patient_gateway_outgoing_commands.reportCheckDeviceDetailsResults(result, ward_name, bed_name, device_type, human_readable_device_id, device_in_use);
    }


    public void handleReceivedWardList(ArrayList<WardInfo> ward_info_list)
    {
        cached_wards = ward_info_list;

        // Now ask for the Bed List
        server_syncing.getBedDetailsListFromServer();
    }


    public void handleReceivedBedList(ArrayList<BedInfo> bed_info_list)
    {
        cached_beds = bed_info_list;

        filterInvalidWards();

        cached_wards.sort(Comparator.comparing(lhs -> lhs.ward_name.toLowerCase()));

        patient_gateway_outgoing_commands.reportWardList(cached_wards);
        patient_gateway_outgoing_commands.reportBedList(cached_beds);

        local_database_storage.storeWardList(cached_wards);
        local_database_storage.storeBedList(cached_beds);
    }


    private void filterInvalidWards()
    {
        Map<Integer, WardInfo> filtered_wards = new HashMap<>();

        // Create a HashMap of the cached_wards. This allows in place deletion of them below
        for (WardInfo wardInfo : cached_wards)
        {
            filtered_wards.put(wardInfo.ward_details_id, wardInfo);
        }

        cached_wards.clear();

        // Iterate through the map
        for (Integer key : filtered_wards.keySet())
        {
            WardInfo ward = filtered_wards.get(key);

            boolean found_bed_for_ward = false;

            for (BedInfo bed : cached_beds)
            {
                if (ward != null)
                {
                    if (bed.by_ward_id == ward.ward_details_id)
                    {
                        found_bed_for_ward = true;
                        break;
                    }
                }
            }

            if (found_bed_for_ward)
            {
                cached_wards.add(ward);
            }
            else
            {
                if (filtered_wards.containsValue(ward))
                {
                    filtered_wards.remove(ward);
                }
            }
        }
    }


    public void handleReceivedDeviceFirmwareVersionList(ArrayList<DeviceFirmwareDetails> device_firmware_details_list)
    {
        queue_firmware_images_to_download.clear();

        for (DeviceFirmwareDetails device_firmware_details : device_firmware_details_list)
        {
            Log.d(TAG, "device_firmware_details : " + device_firmware_details.filename);

            DeviceType type = DeviceType.values()[device_firmware_details.device_type_as_int];

            if(firmware_image_manager.downloadRequired(type, device_firmware_details.firmware_version))
            {
                String filename = device_firmware_details.filename;
                int backslash_index = filename.lastIndexOf("\\") + 1;

                filename = filename.substring(backslash_index);

                Log.d(TAG, "queueing for download : " + filename);

                queue_firmware_images_to_download.add(device_firmware_details.servers_row_id);
            }
        }

        firmware_image_manager.deleteAllFirmwareImagesNotInManager();

        downloadNextFirmwareImageIfRequired();
    }


    public void handleReceivedFirmwareImage(int servers_id, DeviceType device_type, int firmware_version, byte[] firmware_image, String file_name)
    {
        Log.d(TAG, "New Firmware Received : " + device_type + " Firmware Version = " + firmware_version + " and size = " + firmware_image.length);

        if ((file_name != null) && (!file_name.equals("null")))
        {
            firmware_image_manager.updateLatestStoredFirmwareInfoAndWriteImage(device_type, firmware_version, firmware_image, file_name);

            queue_firmware_images_to_download.remove(servers_id);
        }
        else
        {
            Log.w(TAG, "handleReceivedFirmwareImage : version older than current latest, or filename null");
        }

        downloadNextFirmwareImageIfRequired();
    }


    public void handleGetEarlyWarningScoresResponse(String json_array_as_string)
    {
        default_early_warning_score_threshold_sets.clear();

        Log.d(TAG, "SERVER_INTERFACE_STATUS_UPDATE__DEFAULT_EARLY_WARNING_SCORES_LIST_RECEIVED");

        StoreEwsThresholdsInBackground(json_array_as_string);
    }

    private void StoreEwsThresholdsInBackground(String json_array_as_string)
    {
        Executors.newSingleThreadExecutor().execute(() -> {

            // Long running operation
            storeEwsThresholds(json_array_as_string);
        });
    }


    private void storeEwsThresholds(String json_array_as_string)
    {
        try
        {
            // Delete the existing thresholds
            local_database_storage.deleteEarlyWarningScoreThresholdSets();

            JSONArray json_array_threshold_sets = new JSONArray(json_array_as_string);

            final String TAG_EWS_LEVEL = "EWS-LEVEL";

            Log.d(TAG_EWS_LEVEL, "Received " + json_array_threshold_sets.length() + " Default Threshold Sets");

            int array_length = json_array_threshold_sets.length();

            // JSON array not compatible with ForEach loop
            for (int i = 0; i < array_length; i++)
            {
                JSONObject object_threshold_set = json_array_threshold_sets.getJSONObject(i);

                ThresholdSet this_threshold_set = new ThresholdSet();
                this_threshold_set.servers_database_row_id = object_threshold_set.getInt("ThresholdSetId");
                this_threshold_set.name = object_threshold_set.getString("ThresholdSetName");
                this_threshold_set.is_default = object_threshold_set.getInt("ThresholdSetIsDefault") == 1;

                String log_string = "this_threshold_set.servers_database_row_id = " + this_threshold_set.servers_database_row_id;
                log_string += " : this_threshold_set.name = " + this_threshold_set.name;
                log_string += " : this_threshold_set.is_default = " + this_threshold_set.is_default;
                Log.d(TAG_EWS_LEVEL, log_string);

                // Store/Update this data in the local database
                this_threshold_set.local_database_row_id = local_database_storage.storeOrUpdateDefaultThresholdSet(this_threshold_set.name, this_threshold_set.is_default, this_threshold_set.servers_database_row_id);

                JSONArray json_array_threshold_set_age_block_details = object_threshold_set.getJSONArray("ThresholdSetAgeBlockDetails");
                Log.d(TAG_EWS_LEVEL, "Received " + json_array_threshold_set_age_block_details.length() + " Age block details");

                for (int j = 0; j < json_array_threshold_set_age_block_details.length(); j++)
                {
                    JSONObject object_threshold_set_age_block_detail = json_array_threshold_set_age_block_details.getJSONObject(j);

                    ThresholdSetAgeBlockDetail this_threshold_set_age_block_detail = new ThresholdSetAgeBlockDetail();
                    this_threshold_set_age_block_detail.age_range_bottom = object_threshold_set_age_block_detail.getInt("AgeRangeBottom");
                    this_threshold_set_age_block_detail.age_range_top = object_threshold_set_age_block_detail.getInt("AgeRangeTop");
                    this_threshold_set_age_block_detail.display_name = object_threshold_set_age_block_detail.getString("DisplayName");
                    this_threshold_set_age_block_detail.is_adult = object_threshold_set_age_block_detail.getInt("IsAdult") > 0;

                    try
                    {
                        String display_image_base64 = object_threshold_set_age_block_detail.getString("ImageBinary");
                        this_threshold_set_age_block_detail.image_binary = Base64.decode(display_image_base64, Base64.DEFAULT);
                    }
                    catch (Exception e)
                    {
                        // Ensure we do not crash when talking to a server that does not have ImageBinary in the JSON
                        byte[] dummy_byte_array = new byte[1];
                        dummy_byte_array[0] = 0x55;

                        this_threshold_set_age_block_detail.image_binary = dummy_byte_array;
                    }

                    this_threshold_set_age_block_detail.servers_database_row_id = object_threshold_set_age_block_detail.getInt("ThresholdSetAgeBlockDetailId");
                    Log.d(TAG_EWS_LEVEL, "age_range_bottom = " + this_threshold_set_age_block_detail.age_range_bottom + " : " + "age_range_top = " + this_threshold_set_age_block_detail.age_range_top + " : display_name = " + this_threshold_set_age_block_detail.display_name);

                    // Store/Update this data in the local database
                    this_threshold_set_age_block_detail.local_database_row_id = local_database_storage.storeOrUpdateDefaultThresholdSetAgeBlockDetail(this_threshold_set_age_block_detail, this_threshold_set.local_database_row_id);

                    // Process ThresholdSetMeasurementTypeDetails
                    JSONArray json_array_threshold_set_measurement_type_details = object_threshold_set_age_block_detail.getJSONArray("ThresholdSetMeasurementTypeDetails");
                    Log.d(TAG_EWS_LEVEL, "Received " + json_array_threshold_set_measurement_type_details.length() + " Measurement Type Details");

                    for(int k = 0; k < MeasurementTypes.values().length; k++)
                    {
                        this_threshold_set_age_block_detail.list_of_threshold_set_levels_by_measurement_type.add(new ArrayList<>());
                    }

                    for (int k = 0; k < json_array_threshold_set_measurement_type_details.length(); k++)
                    {
                        JSONObject object_threshold_set_measurement_type_details = json_array_threshold_set_measurement_type_details.getJSONObject(k);

                        String measurement_type_display = object_threshold_set_measurement_type_details.getString("MeasurementTypeDisplay");
                        int measurement_type = object_threshold_set_measurement_type_details.getInt("MeasurementType");

                        JSONArray json_array_threshold_set_ews_levels = object_threshold_set_measurement_type_details.getJSONArray("ThresholdSetEWSLevels");
                        Log.d(TAG_EWS_LEVEL, "Received " + json_array_threshold_set_ews_levels.length() + " json_array_threshold_set_ews_levels");

                        ArrayList<ThresholdSetLevel> this_measurement_type_threshold_set = new ArrayList<>();

                        for (int l = 0; l < json_array_threshold_set_ews_levels.length(); l++)
                        {
                            JSONObject object_threshold_set_ews_levels = json_array_threshold_set_ews_levels.getJSONObject(l);

                            String display_text = "";
                            try
                            {
                                display_text = object_threshold_set_ews_levels.getString("DisplayText");
                            }
                            catch (JSONException ex)
                            {
                                Log.e(TAG, "display_text not present");
                            }

                            String information_text = "";
                            try
                            {
                                information_text = object_threshold_set_ews_levels.getString("InformationText");
                            }
                            catch (JSONException ex)
                            {
                                Log.e(TAG, "information_text not present");
                            }

                            ThresholdSetLevel this_threshold_set_level = new ThresholdSetLevel(
                                    (float)object_threshold_set_ews_levels.getDouble("Top"),
                                    (float)object_threshold_set_ews_levels.getDouble("Bottom"),
                                    object_threshold_set_ews_levels.getInt("EarlyWarningScore"),
                                    measurement_type,
                                    measurement_type_display,
                                    display_text,
                                    information_text);

                            this_threshold_set_level.servers_database_row_id = object_threshold_set_ews_levels.getInt("ThresholdSetLevelId");

                            // Store/Update this data in the local database
                            local_database_storage.storeOrUpdateDefaultThresholdSetLevel(
                                    this_threshold_set_level.bottom,
                                    this_threshold_set_level.top,
                                    this_threshold_set_level.early_warning_score,
                                    this_threshold_set_level.measurement_type,
                                    this_threshold_set_level.measurement_type_string,
                                    this_threshold_set_level.display_text,
                                    this_threshold_set_level.information_text,
                                    this_threshold_set_level.servers_database_row_id,
                                    this_threshold_set_age_block_detail.local_database_row_id);

                            Log.d(TAG_EWS_LEVEL, "    top = " + this_threshold_set_level.top + " : bottom = " + this_threshold_set_level.bottom + " : early_warning_score = " + this_threshold_set_level.early_warning_score + " : measurement_type = " + this_threshold_set_level.measurement_type + " : measurement_type_string = " + this_threshold_set_level.measurement_type_string);

                            this_measurement_type_threshold_set.add(this_threshold_set_level);
                        }

                        // Add the measurement-specific thresholds at index of measurement type
                        this_threshold_set_age_block_detail.list_of_threshold_set_levels_by_measurement_type.set(measurement_type, this_measurement_type_threshold_set);
                    }
                    // End Process ThresholdSetMeasurementTypeDetails

                    // Process ThresholdSetColours
                    JSONArray json_array_threshold_set_colours = object_threshold_set_age_block_detail.getJSONArray("ThresholdSetColours");
                    Log.d(TAG_EWS_LEVEL, "Received " + json_array_threshold_set_colours.length() + " Colours");

                    for (int k = 0; k < json_array_threshold_set_colours.length(); k++)
                    {
                        JSONObject object_threshold_set_colour = json_array_threshold_set_colours.getJSONObject(k);

                        int score = object_threshold_set_colour.getInt("Score");
                        int colour = object_threshold_set_colour.getInt("Colour");
                        int text_colour = object_threshold_set_colour.getInt("TextColour");

                        ThresholdSetColour threshold_set_colour = new ThresholdSetColour(score, colour, text_colour);
                        threshold_set_colour.servers_database_row_id = object_threshold_set_colour.getInt("ThresholdSetColourId");

                        // Store/Update this data in the local database
                        local_database_storage.storeOrUpdateDefaultThresholdSetColour(
                                threshold_set_colour.score,
                                threshold_set_colour.colour,
                                threshold_set_colour.text_colour,
                                threshold_set_colour.servers_database_row_id,
                                this_threshold_set_age_block_detail.local_database_row_id);

                        this_threshold_set_age_block_detail.list_of_threshold_set_colours.add(threshold_set_colour);
                    }
                    // End Process ThresholdSetColours

                    this_threshold_set.list_threshold_set_age_block_detail.add(this_threshold_set_age_block_detail);
                }

                default_early_warning_score_threshold_sets.add(this_threshold_set);
            }

            // report download success - so installation mode knows it's done
            patient_gateway_outgoing_commands.reportEwsDownloadSuccess(true);
            // then report EWS updated, to trigger the UI reading them in.
            patient_gateway_outgoing_commands.reportCachedDataUpdated(QueryType.EARLY_WARNING_SCORE_THRESHOLD_SETS);

            setup_wizard.getThresholdsSuccess();
        }
        catch (JSONException e)
        {
            Log.e(TAG, "SERVER_INTERFACE_STATUS_UPDATE__DEFAULT_EARLY_WARNING_SCORES_LIST_RECEIVED JSON Exception : " + e);
            patient_gateway_outgoing_commands.reportEwsDownloadSuccess(false);

            setup_wizard.getThresholdsFailure();
        }
        catch (Exception e)
        {
            Log.e(TAG, "SERVER_INTERFACE_STATUS_UPDATE__DEFAULT_EARLY_WARNING_SCORES_LIST_RECEIVED Exception : " + e);
            patient_gateway_outgoing_commands.reportEwsDownloadSuccess(false);

            setup_wizard.getThresholdsFailure();
        }
    }


    public void handleReceivedGatewayConfig(ArrayList<ConfigOption> config_options_list)
    {
        Log.d(TAG, "Got Gateway Config");

        for (ConfigOption config_option : config_options_list)
        {
            Log.e(TAG, "handleReceivedGatewayConfig option_name = " + config_option.option_name + " : option_value = " + config_option.option_value);

            String option_name = config_option.option_name;
            String option_value  = config_option.option_value;

            switch (option_name)
            {
                // Admin page top row
                case "UseAuthentication":
                    enableWebserviceAuthentication(Boolean.parseBoolean(option_value));
                    break;
                case "UseEncryption":
                    enableWebserviceEncryption(Boolean.parseBoolean(option_value));
                    break;

                // Admin page drop downs
                case "handleGatewayConfigResponse":
                    setSetupModeTimeInSeconds(Integer.parseInt(option_value));
                    break;
                case "PercentagePoorSignalBeatsBeforeMarkingAsInvalid":
                    setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(Integer.parseInt(option_value));
                    break;
                case "NumberInvalidSpO2IntermediatesBeforeMarkingInvalid":
                    setNumberOfInvalidNoninWristOxIntermediateMeasurementsBeforeMinuteMarkedAsInvalid(Integer.parseInt(option_value));
                    break;
                case "SpO2LongTermMeasurementTimeoutInMinutes":
                    setLongTimeMeasurementTimeoutInMinutes(SensorType.SENSOR_TYPE__SPO2, Integer.parseInt(option_value));
                    break;
                case "BloodPressureLongTermMeasurementTimeoutInMinutes":
                    setLongTimeMeasurementTimeoutInMinutes(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, Integer.parseInt(option_value));
                    break;
                case "WeightLongTermMeasurementTimeoutInMinutes":
                    setLongTimeMeasurementTimeoutInMinutes(SensorType.SENSOR_TYPE__WEIGHT_SCALE, Integer.parseInt(option_value));
                    break;
                case "ThirdPartyLongTermMeasurementTimeoutInMinutes":
                    setLongTimeMeasurementTimeoutInMinutes(SensorType.SENSOR_TYPE__TEMPERATURE, Integer.parseInt(option_value));
                    break;
                case "DisplayTimeoutInSeconds":
                    setDisplayTimeoutInSeconds(Integer.parseInt(option_value));
                    break;

                // Admin page checkboxes
                case "PeriodicSetupMode":
                    enableDevicePeriodicSetupMode(Boolean.parseBoolean(option_value));
                    break;
                case "EnableUnpluggedOverlay":
                    enableUnpluggedOverlay(Boolean.parseBoolean(option_value));
                    break;
                case "DisplayTimeoutAppliesToPatientVitals":
                    setDisplayTimeoutAppliesToPatientVitals(Boolean.parseBoolean(option_value));
                    break;

                // Feature Enable page first row
                case "EnableManuallyEnteredVitalSigns":
                    enableManualVitalSigns(Boolean.parseBoolean(option_value));
                    break;
                case "EnableCsvOutput":
                    enableCsvOutput(Boolean.parseBoolean(option_value));
                    break;
                case "ShowNumbersOnBatteryIndicator":
                    showNumbersOnBatteryIndicators(Boolean.parseBoolean(option_value));
                    break;
                case "ShowIpAddressOnWifiPopup":
                    setShowIpAddressOnWifiPopupEnabledStatus(Boolean.parseBoolean(option_value));
                    break;

                // Feature Enable page second row
                case "RunDevicesInTestMode":
                    runDevicesInTestMode(Boolean.parseBoolean(option_value));
                    break;
                case "PatientNameLookup":
                    enablePatientNameLookup(Boolean.parseBoolean(option_value));
                    break;
                case "SimpleHeartRate":
                    enableSimpleHeartRateAlgorithm(Boolean.parseBoolean(option_value));
                    break;
                case "GsmModeOnly":
                    setGsmModeOnlyEnabledStatus(Boolean.parseBoolean(option_value));
                    break;
                case "EnableDeveloperPopup":
                    storeDeveloperPopupEnabledStatus(Boolean.parseBoolean(option_value));
                    break;

                // Feature Enable page third row
                case "ShowMacOnDeviceStatus":
                    showMacAddress(Boolean.parseBoolean(option_value));
                    break;
                case "EnableUsaMode":
                    enableUsaOnlyMode(Boolean.parseBoolean(option_value));
                    break;
                case "EnableLifetouchActivityLevel":
                    showLifetouchActivityLevel(Boolean.parseBoolean(option_value));
                    break;
                case "EnableSessionAutoResume":
                    enableSessionAutoResume(Boolean.parseBoolean(option_value));
                    break;
                case "EnableAutoLogFileUpload":
                    enableAutoUploadLogsToServer(Boolean.parseBoolean(option_value));
                    break;

                // Feature Enable page bottom row
                case "LifetempMeasurementIntervalInSeconds":
                    setLifetempMeasurementIntervalInSeconds(Integer.parseInt(option_value));
                    break;
                case "EnablePatientOrientation":
                    enablePatientOrientation(Boolean.parseBoolean(option_value));
                    break;
                case "PatientOrientationMeasurementIntervalInSeconds":
                    storePatientOrientationMeasurementInterval(Integer.parseInt(option_value));
                    break;
                case "JsonSize":
                    setMaxWebserviceJsonArraySize(Integer.parseInt(option_value));
                    break;

                case "EnableVideoCalls":
                    enableVideoCalls(Boolean.parseBoolean(option_value));
                    break;

                case "DisplayTemperatureInFahrenheit":
                    displayTemperatureInFahrenheit(Boolean.parseBoolean(option_value));
                    break;

                case "DisplayWeightInLbs":
                    displayWeightInLbs(Boolean.parseBoolean(option_value));
                    break;

                case "EnableWebPages":
                    enableViewWebPages(Boolean.parseBoolean(option_value));
                    break;

                default:
                    break;
            }
        } // for

        patient_gateway_outgoing_commands.recalculateThresholdsFollowingUpdatedServerConfig();
    }


    public void handleReceivedServerConfigurableText(ArrayList<ServerConfigurableText> server_configurable_text_list)
    {
        Log.d(TAG, "handleReceivedServerConfigurableText : List size = " + server_configurable_text_list.size());

        if (server_configurable_text_list.size() > 0)
        {
            local_database_storage.deleteServerConfigurableText();

            for (ServerConfigurableText server_configurable_text : server_configurable_text_list)
            {
                Log.d(TAG, "handleReceivedServerConfigurableText : " + server_configurable_text.getStringType() + " = " + server_configurable_text.getStringText());

                local_database_storage.storeServerConfigurableText(server_configurable_text);
            }
        }

        // Tell the UI to go ahead and read from the database.
        patient_gateway_outgoing_commands.reportCachedDataUpdated(QueryType.SERVER_CONFIGURABLE_TEXT);
    }


    public void handleReceivedWebPages(ArrayList<WebPageDescriptor> webPageDescriptorArrayList)
    {
        Log.d(TAG, "handleReceivedWebPages : List size = " + webPageDescriptorArrayList.size());

        local_database_storage.deleteWebPages();

        if (webPageDescriptorArrayList.size() > 0)
        {
            for (WebPageDescriptor descriptor : webPageDescriptorArrayList)
            {
                Log.d(TAG, "handleReceivedWebPages : URL = " + descriptor.url + " : Description = " + descriptor.description);

                local_database_storage.storeWebPage(descriptor);
            }
        }

        // Tell the UI to go ahead and read from the database.
        patient_gateway_outgoing_commands.reportCachedDataUpdated(QueryType.WEBPAGES);
    }

    public void handleMqttCertificate(GetMQTTCertificateResponse certificate_response)
    {
        writeCertificateFile("ca.crt", certificate_response.getCaCertificateString());
        writeCertificateFile("client.crt", certificate_response.getClientCertificateString());
        writeCertificateFile("client.key", certificate_response.getClientKeyString());
    }


    private void writeCertificateFile(String filename, String certificate_string)
    {
        if(certificate_string != null)
        {
            if (certificate_string.length() > 0)
            {
                try
                {
                    String filepath = this.getFilesDir() + File.separator + "certificates" + File.separator;

                    Log.d(TAG, "writeCertificateFile : writing to " + filename);

                    File file = new File(filepath);
                    if (!file.exists())
                    {
                        file.mkdirs();
                    }

                    file = new File(filepath + filename);

                    if (file.exists())
                    {
                        file.delete();
                    }

                    file.createNewFile();

                    OutputStream outputStream = null;

                    try
                    {
                        int fileSize = certificate_string.getBytes().length;

                        outputStream = new FileOutputStream(file);

                        outputStream.write(certificate_string.getBytes(), 0, fileSize);

                        outputStream.flush();
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "handleMqttCertificate failed with error message " + e.getMessage());
                    }
                    finally
                    {
                        if (outputStream != null)
                        {
                            outputStream.close();
                        }
                    }
                }
                catch (IOException e)
                {
                    Log.e(TAG, "handleMqttCertificate failed with error message " + e.getMessage());
                }
            }
        }
    }


    public void handlePingResponse(boolean ping_status, boolean authentication_ok)
    {
        Log.d(TAG, "handlePingResponse : " + ping_status);

        patient_gateway_outgoing_commands.reportServerPingResult(ping_status, authentication_ok);

        if(ping_status)
        {
            setup_wizard.testServerLinkSuccess();
        }
        else
        {
            setup_wizard.testServerLinkFailure();
        }
    }


    private void downloadNextFirmwareImageIfRequired()
    {
        if (queue_firmware_images_to_download.isEmpty() == false)
        {
            int servers_row_id = queue_firmware_images_to_download.peek();

            Log.e(TAG, "downloadNextFirmwareImageIfRequired : DOWNLOAD servers id = " + servers_row_id);

            server_syncing.sendGetFirmwareBinaryRequest(servers_row_id);
        }
        else
        {
            Log.e(TAG, "downloadNextFirmwareImageIfRequired : EMPTY");

            // Tell the UI that the Firmware check has finished
            patient_gateway_outgoing_commands.reportCheckForLatestFirmwareComplete(true, firmware_image_manager.getFirmwareImageList());

            setup_wizard.getFirmwareSuccess();

            // Store server sync settings version as this is the ast step in the wizard.
            gateway_settings.storeGatewayServerSettingsVersion(app_versions.getGatewayVersionNumberAsInt());
        }
    }


    private final BroadcastReceiver broadcastReceiverIncomingFromAlgorithms = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int measurement_index = intent.getIntExtra("measurement_type", 0);

            AlgorithmMeasurementType measurement_type = AlgorithmMeasurementType.values()[measurement_index];

            switch (measurement_type)
            {
                case HEART_RATE_MEASUREMENT:
                {
                    int heart_rate = intent.getIntExtra("heart_rate", 0);
                    long timestamp = intent.getLongExtra("timestamp", 0);

                    Log.d(TAG, "HEART_RATE_MEASUREMENT:       Heart Rate = " + heart_rate + "           time stamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(timestamp));

                    // Shouldn't need these two checks, as no beats are pushed to the algorithms if there is no Device Session, but better to standardise on them
                    // Only process the received data is there is currently a Patient Session in progress.....
                    if (isPatientSessionRunning())
                    {
                        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);

                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                        if (device_info.isDeviceSessionInProgress())
                        {
                            MeasurementHeartRate measurement = new MeasurementHeartRate(heart_rate, timestamp, getNtpTimeNowInMilliseconds());

                            local_database_storage.storeLifetouchHeartRateMeasurement(device_info, measurement);

                            reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.HEART_RATE, measurement);
                        }
                        else
                        {
                            Log.e(TAG, "Rx'ed HEART_RATE_MEASUREMENT but IGNORED as no Device Session in progress");
                        }
                    }
                    else
                    {
                        Log.e(TAG, "Rx'ed HEART_RATE_MEASUREMENT but IGNORED as no Device Session in progress");
                    }
                }
                break;

                case RESPIRATION_RATE_MEASUREMENT:
                {
                    int respiration_rate = intent.getIntExtra("respiration_rate", 0);
                    long timestamp = intent.getLongExtra("timestamp", 0);

                    Log.d(TAG, "RESPIRATION_RATE_MEASUREMENT: Respiration Rate = " + respiration_rate + "     time stamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(timestamp));

                    // Shouldn't need these two checks, as no beats are pushed to the algorithms if there is no Device Session, but better to standardise on them
                    // Only process the received data is there is currently a Patient Session in progress.....
                    if (isPatientSessionRunning())
                    {
                        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);

                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                        if (device_info.isDeviceSessionInProgress())
                        {
                            MeasurementRespirationRate measurement = new MeasurementRespirationRate(respiration_rate, timestamp, getNtpTimeNowInMilliseconds());

                            local_database_storage.storeLifetouchRespirationMeasurement(device_info, measurement);

                            reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.RESPIRATION_RATE, measurement);
                        }
                        else
                        {
                            Log.e(TAG, "Rx'ed RESPIRATION_RATE_MEASUREMENT but IGNORED as no Device Session in progress");
                        }
                    }
                    else
                    {
                        Log.e(TAG, "Rx'ed RESPIRATION_RATE_MEASUREMENT but IGNORED as no Device Session in progress");
                    }
                }
                break;

                case PULSE_OX_MEASUREMENT:
                {
                    MeasurementSpO2 measurement = intent.getParcelableExtra("measurement");

                    DeviceInfo device_info = getDeviceInfoFromIntent(intent);

                    if (measurement.SpO2 != INVALID_MEASUREMENT)
                    {
                        // Valid measurement
                        Log.d(TAG, "Writing average SpO2 value to database = " + measurement.SpO2 + "      device_info_manager.device_info__pulse_ox.android_database_device_session_id = " + device_info.getAndroidDeviceSessionId());
                    }
                    else
                    {
                        // Too many Invalid/Finger Off measurements, so mark whole minute as -1
                        Log.e(TAG, "Too many Invalid Data/Finger Off in minute. Writing -1 for Nonin SpO2");
                    }

                    measurement.measurement_validity_time_in_seconds = gateway_settings.getSpO2LongTermMeasurementTimeoutInMinutes() * 60;

                    storePulseOxMeasurement(measurement, device_info);
                }
                break;

                case EARLY_WARNING_SCORE:
                {
                    int early_warning_score = intent.getIntExtra("early_warning_score", 0);
                    int max_possible = intent.getIntExtra("max_possible", 0);
                    boolean is_special_alert = intent.getBooleanExtra("is_special_alert", false);
                    int trend_direction = intent.getIntExtra("trend_direction", 0);
                    long timestamp = intent.getLongExtra("timestamp", 0);

                    Log.d(TAG, "EARLY_WARNING_SCORE = " + early_warning_score + " / " + max_possible + "     time stamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(timestamp));

                    // Only process the received data is there is currently a Patient Session in progress.....
                    if (isPatientSessionRunning())
                    {
                        local_database_storage.storeEarlyWarningScore(
                                patient_session_info.android_database_patient_session_id,
                                device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).getAndroidDeviceSessionId(),
                                early_warning_score,
                                max_possible,
                                is_special_alert,
                                trend_direction,
                                timestamp);

                        patient_gateway_outgoing_commands.reportNewVitalsData(VitalSignType.EARLY_WARNING_SCORE, new MeasurementEarlyWarningScore(early_warning_score, max_possible, is_special_alert, trend_direction, timestamp, getNtpTimeNowInMilliseconds()));
                    }
                    else
                    {
                        Log.e(TAG, "Rx'ed EARLY_WARNING_SCORE but IGNORED as no Patient Session in progress");
                    }
                }
                break;

                case ORIENTATION_MEASUREMENT:
                {
                    // Only process the received data is there is currently a Patient Session in progress.....
                    if (isPatientSessionRunning())
                    {
                        int orientation_as_int = intent.getIntExtra(IntermediateMeasurementProcessor.ORIENTATION, -1);

                        PatientPositionOrientation orientation = PatientPositionOrientation.values()[orientation_as_int];
                        long timestamp = intent.getLongExtra("timestamp", 0);

                        patient_gateway_outgoing_commands.reportPatientOrientation(orientation, timestamp);

                        last_patient_orientation = orientation;
                        last_patient_orientation_timestamp = timestamp;

                        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);

                        MeasurementPatientOrientation measurement = new MeasurementPatientOrientation(orientation, timestamp, getNtpTimeNowInMilliseconds());

                        local_database_storage.storeLifetouchPatientOrientation(device_info, measurement);
                    }
                    else
                    {
                        Log.e(TAG, "Rx'ed PATIENT_ORIENTATION but IGNORED as no Patient Session in progress");
                    }
                }
                break;

                default:
                    break;
            }
        }
    };


    private void storePulseOxMeasurement(MeasurementSpO2 measurement, DeviceInfo device_info)
    {
        measurement.calculateRemainingValidityTime(getNtpTimeNowInMilliseconds());

        local_database_storage.storeOximeterMeasurement(device_info, measurement);
        reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.SPO2, measurement);
    }


    private DeviceInfo getDeviceInfoFromIntent(Intent intent)
    {
        DeviceType device_type = getDeviceTypeFromIntent(intent);
        return device_info_manager.getDeviceInfoByDeviceType(device_type);
    }


    public void setNumberOfDummyDataModeMeasurementsPerTick(int measurements_per_tick)
    {
        gateway_settings.storeDummyDataModeMeasurementsPerTick(measurements_per_tick);

        dummy_data_instance.setNumberOfSimulatedMeasurementsPerTick(measurements_per_tick);

        patient_gateway_outgoing_commands.reportNumberOfDummyDataModeMeasurementsPerTick(measurements_per_tick);
    }


    public void resetServerSyncStatusAndTriggerSync()
    {
        showOnScreenMessage("Reset Server Sync Status triggered");

        server_syncing.resetServerSyncStatusAndTriggerSync();
    }


    public void reportServerSyncStatusToServer()
    {
        getDatabaseStatus();
    }


    public void reportAllDeviceInfoToServer()
    {
        JsonObject all_device_infos = device_info_manager.getAllDeviceInfo();

        server_syncing.sendAllDeviceInfo(all_device_infos);
    }


    public String getAndroidUniqueDeviceId()
    {
        /*
        https://developer.android.com/reference/android/provider/Settings.Secure

        ANDROID_ID

        public static final String ANDROID_ID

        On Android 8.0 (API level 26) and higher versions of the platform, a 64-bit number (expressed as a hexadecimal string), unique to each
        combination of app-signing key, user, and device. Values of ANDROID_ID are scoped by signing key and user. The value may change if a
        factory reset is performed on the device or if an APK signing key changes. For more information about how the platform handles ANDROID_ID
        in Android 8.0 (API level 26) and higher, see Android 8.0 Behavior Changes.

        Note: For apps that were installed prior to updating the device to a version of Android 8.0 (API level 26) or higher, the value of
        ANDROID_ID changes if the app is uninstalled and then reinstalled after the OTA. To preserve values across uninstalls after an OTA to
        Android 8.0 or higher, developers can use Key/Value Backup.

        In versions of the platform lower than Android 8.0 (API level 26), a 64-bit number (expressed as a hexadecimal string) that is randomly
        generated when the user first sets up the device and should remain constant for the lifetime of the user's device. On devices that have
        multiple users, each user appears as a completely separate device, so the ANDROID_ID value is unique to each user.

        Note: If the caller is an Instant App the ID is scoped to the Instant App, it is generated when the Instant App is first installed and
        reset if the user clears the Instant App.
        */

        return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
    }


    public void resetGatewayAndUserInterfaceRunCounters()
    {
        Log.i(TAG, "Deleting Gateway and UI run counters");

        // Bit of a kludge here. Delete all the data.....
        local_database_storage.deleteOldDiagnosticData();

        // ...and write the most recent records again.
        if (most_recent_gateway_boot_time != -1)
        {
            number_of_times_gateway_has_booted = local_database_storage.storeGatewayStartupTime(most_recent_gateway_boot_time);
        }

        if (most_recent_ui_boot_time != -1)
        {
            number_of_times_ui_has_booted = local_database_storage.storeUiStartupTime(most_recent_ui_boot_time);
        }

        reportGatewayStatusToServer();
    }


    public void enableNightModeFromServer(boolean enabled)
    {
        patient_gateway_outgoing_commands.reportNightModeEnabledFromServer(enabled);
    }


    public void enableDummyDataModeFromServer(boolean enabled)
    {
        if (enabled)
        {
            if (isPatientSessionRunning())
            {
                showOnScreenMessage("Enable Dummy Data Mode command received but IGNORED because session is already running");
            }
            else
            {
                showOnScreenMessage("Enable Dummy Data Mode command received");

                // Create a new Patient using an auto generated Hospital Patient ID, and Age
                String ward_name = gateway_settings.getGatewaysAssignedWardName();
                String bed_name = gateway_settings.getGatewaysAssignedBedName();

                String desired_patient_id = ward_name + " " + bed_name + " @ " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(getNtpTimeNowInMilliseconds());

                int servers_threshold_set_id = 0;
                int servers_threshold_set_age_block_id = 0;

                for (ThresholdSet thresholdSet : default_early_warning_score_threshold_sets)
                {
                    for (ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail : thresholdSet.list_threshold_set_age_block_detail)
                    {
                        if (thresholdSetAgeBlockDetail.is_adult)
                        {
                            servers_threshold_set_id = thresholdSet.servers_database_row_id;
                            servers_threshold_set_age_block_id = thresholdSet.list_threshold_set_age_block_detail.get(0).servers_database_row_id;
                            break;
                        }
                    }
                }

                int session_started_by_user_id = 1;

                DeviceInfo device_info;

                // Add Dummy Lifetouch
                device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__LIFETOUCH);
                device_info.human_readable_device_id = 100001;
                device_info.bluetooth_address = "AA:BB:CC:DD:EE:FF";
                device_info.desired_device_connection_status = DeviceConnectionStatus.CONNECTED;
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);
                device_info.start_date_at_midnight_in_milliseconds = 0;
                device_info.device_name = "Dummy Lifetouch";
                device_info.dummy_data_mode = true;

                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                dummy_data_instance.initVariables(device_info);

                // Add Dummy Lifetemp
                device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__LIFETEMP_V2);
                device_info.human_readable_device_id = 100002;
                device_info.bluetooth_address = "AA:BB:CC:DD:EE:FF";
                device_info.desired_device_connection_status = DeviceConnectionStatus.CONNECTED;
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);
                device_info.device_name = "Dummy Lifetemp";
                device_info.dummy_data_mode = true;

                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                dummy_data_instance.initVariables(device_info);

//            // Add Dummy Lifetemp V2
//            device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__LIFETEMP_V2);
//            device_info.human_readable_device_id = 100003;
//            device_info.bluetooth_address = "AA:BB:CC:DD:EE:FF";
//            device_info.desired_device_connection_status = DeviceConnectionStatus.CONNECTED;
//            device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);
//            device_info.device_name = "Dummy Lifetemp V2";
//            device_info.dummy_data_mode = true;
//
//            patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
//
//            dummy_data_instance.initVariables(device_info);


                // Add Dummy Pulse Ox
                device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX);
                device_info.human_readable_device_id = 100003;
                device_info.bluetooth_address = "AA:BB:CC:DD:EE:FF";
                device_info.desired_device_connection_status = DeviceConnectionStatus.CONNECTED;
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);
                device_info.device_name = "Nonin_Medical_Inc";
                device_info.dummy_data_mode = true;

                // Simulate that the Nonin was connected as normal
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(NoninWristOx.NONIN_WRIST_OX__CONNECTED));

                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                dummy_data_instance.initVariables(device_info);

                // Add Dummy Blood Pressure
                device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_UA767);
                device_info.human_readable_device_id = 100004;
                device_info.bluetooth_address = "AA:BB:CC:DD:EE:FF";
                device_info.desired_device_connection_status = DeviceConnectionStatus.CONNECTED;
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);
                device_info.device_name = "UA767";
                device_info.dummy_data_mode = true;

                // Simulate that the A&D was connected as normal
                sendBroadcast(new Intent(AnD_UA767.CONNECTED));

                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                dummy_data_instance.initVariables(device_info);

                // Add Dummy EarlyWarningScores
                device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE);
                device_info.human_readable_device_id = 100005;
                device_info.dummy_data_mode = true;

                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                dummy_data_instance.initVariables(device_info);

                createNewSession(session_started_by_user_id, desired_patient_id, servers_threshold_set_id, servers_threshold_set_age_block_id);

                // report these after creating new session, as UI only processes them once session is in progress.
                patient_gateway_outgoing_commands.reportHospitalPatientID(desired_patient_id);
                patient_gateway_outgoing_commands.reportPatientThresholdSet(patient_info);
            }
        }
        else
        {
            showOnScreenMessage("Disable Dummy Data Mode command received");

            int session_ended_by_user_id = -1;

            endExistingSession(session_ended_by_user_id, true, getNtpTimeNowInMilliseconds());

            reportGatewayStatusToServer();
        }
    }


    public void enablePeriodicSetupModeFromServer(boolean enabled)
    {
        if (enabled)
        {
            showOnScreenMessage("Enable Periodic Setup Mode command received");
        }
        else
        {
            showOnScreenMessage("Disable Periodic Setup Mode command received");
        }

        enableDevicePeriodicSetupMode(enabled);
    }


    public void enablePatientNameLookupFromServer(boolean enabled)
    {
        if (enabled)
        {
            showOnScreenMessage("Enable Patient Name Lookup command received");
        }
        else
        {
            showOnScreenMessage("Disable Patient Name Lookup command received");
        }

        enablePatientNameLookup(enabled);
    }


    public void enablePredefinedAnnotationsFromServer(boolean enabled)
    {
        if (enabled)
        {
            showOnScreenMessage("Enable Pre Defined Annotations command received");
        }
        else
        {
            showOnScreenMessage("Disable Pre Defined Annotations command received");
        }

        enablePredefinedAnnotations(enabled);
    }


    public void enableAutoAddEWSFromServer(boolean enabled)
    {
        if (enabled)
        {
            showOnScreenMessage("Enable Auto Add EWS command received");
        }
        else
        {
            showOnScreenMessage("Disable Auto Add EWS command received");
        }

        enableAutoEnableEws(enabled);
    }


    public void enableDfuFromServer(boolean enabled)
    {
        if (enabled)
        {
            showOnScreenMessage("Enable DFU command received");
        }
        else
        {
            showOnScreenMessage("Disable DFU command received");
        }

        dfuBootloaderEnabled(enabled);
    }

    private void startDeviceRawAccelerometerModeFromServer(BtleSensorDevice device_info, boolean do_not_stream_data)
    {
        if (device_info.isDeviceSessionInProgress())
        {
            showOnScreenMessage("Start " + device_info.getSensorTypeAndDeviceTypeAsString() + " Raw Accelerometer Mode command received");

            // Tell UI that Server started Raw Acc mode
            patient_gateway_outgoing_commands.sendCommandReportDeviceRawAccelerometerModeStartedFromServer(device_info.device_type);

            if (do_not_stream_data)
            {
                enterDeviceRawAccelerometerMode(device_info, DeviceInfo.OperatingMode.SERVER_INITIATED_RAW_ACCELEROMETER_MODE_NOT_STREAMING_DATA);
            }
            else
            {
                enterDeviceRawAccelerometerMode(device_info, DeviceInfo.OperatingMode.SERVER_INITIATED_RAW_ACCELEROMETER_MODE_STREAMING_DATA);
            }
        }
        else
        {
            showOnScreenMessage("Start " + device_info.getSensorTypeAndDeviceTypeAsString() + " Raw Accelerometer Mode command received but IGNORED as no Device Session");
        }
    }


    private void stopDeviceRawAccelerometerModeFromServer(BtleSensorDevice device_info)
    {
        if (device_info.isDeviceSessionInProgress())
        {
            if(device_info.isInRawAccelerometerMode())
            {
                showOnScreenMessage("Stop " + device_info.getSensorTypeAndDeviceTypeAsString() + " Raw Accelerometer Mode command received");

                // Tell UI that Server stopped Setup mode
                patient_gateway_outgoing_commands.sendCommandReportDeviceRawAccelerometerModeStoppedFromServer(device_info);

                exitDeviceRawAccelerometerMode(device_info, true);
            }
            else
            {
                showOnScreenMessage("Exit " + device_info.getSensorTypeAndDeviceTypeAsString() + " Raw Accelerometer Mode command received but IGNORED as NOT in RAW_ACCELEROMETER_MODE");
            }
        }
        else
        {
            showOnScreenMessage("Exit " + device_info.getSensorTypeAndDeviceTypeAsString() + " Raw Accelerometer Mode command received but IGNORED as no device session");
        }

        if(device_info.dummy_data_mode)
        {
            dummy_data_instance.stopDeviceRawAccelerometerMode(device_info.sensor_type);
        }
    }


    private void startDeviceSetupModeFromServer(DeviceInfo device_info, boolean do_not_stream_data)
    {
        if (device_info.isDeviceSessionInProgress())
        {
            if (!((device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE) && nonin_setup_mode_blocked))
            {
                showOnScreenMessage("startDeviceSetupModeFromServer " + device_info.getSensorTypeAndDeviceTypeAsString() + " command received");

                // Tell UI that Server started Setup mode
                patient_gateway_outgoing_commands.sendCommandReportDeviceSetupModeStartedFromServer(device_info.device_type);

                if (do_not_stream_data)
                {
                    enterDeviceSetupMode(device_info, DeviceInfo.OperatingMode.SERVER_INITIATED_SETUP_MODE_NOT_STREAMING_DATA);
                }
                else
                {
                    enterDeviceSetupMode(device_info, DeviceInfo.OperatingMode.SERVER_INITIATED_SETUP_MODE_STREAMING_DATA);
                }
            }
            else
            {
                if (device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE && nonin_setup_mode_blocked)
                {
                    showOnScreenMessage("Start " + device_info.getSensorTypeAndDeviceTypeAsString() + " Setup Mode command received but IGNORED as nonin_setup_mode_blocked due to possible playback required after recent reconnection ");

                    Log.d(TAG, "Start " + device_info.getSensorTypeAndDeviceTypeAsString() + " Setup Mode command received but IGNORED as nonin_setup_mode_blocked due to possible playback required after recent reconnection");
                }
            }
        }
        else
        {
            showOnScreenMessage("Start " + device_info.getSensorTypeAndDeviceTypeAsString() + " Setup Mode command received but IGNORED as no " + device_info.device_type + " Session");
        }
    }


    private void stopDeviceSetupModeFromServer(DeviceInfo device_info)
    {
        if (device_info.isDeviceSessionInProgress())
        {
            if(device_info.isInSetupMode())
            {
                showOnScreenMessage("stopDeviceSetupModeFromServer " + device_info.getSensorTypeAndDeviceTypeAsString() + " command received");

                // Tell UI that Server stopped Setup mode
                patient_gateway_outgoing_commands.sendCommandReportDeviceSetupModeStoppedFromServer(device_info.device_type);

                exitSetupModeIfRunning(device_info);
            }
            else
            {
                showOnScreenMessage("Exit " + device_info.getSensorTypeAndDeviceTypeAsString() + " Setup Mode command received but IGNORED as NOT in SETUP_MODE");
            }
        }
        else
        {
            showOnScreenMessage("Exit " + device_info.getSensorTypeAndDeviceTypeAsString() + " Setup Mode command received but IGNORED as no " + device_info.device_type + " Session");
        }
    }


    public void remoteEmptyLocalDatabase()
    {
        showOnScreenMessage("Empty Local Database command received from Server");

        // Empty DB
        emptyLocalDatabase(DO_NOT_DELETE_EWS_THRESHOLDS);
    }


    public void emulateQrCodeUnlockFeatureEnable(UnlockCodeSource source)
    {
        showOnScreenMessage("QR_CODE_UNLOCK_FEATURE_ENABLE command received");

        QrCodeData emulated_unlock_qr = new QrCodeData(BarcodeDeviceType.BARCODE_TYPE__UNLOCK_REQUEST_FEATURE_ENABLE, 1);

        patient_gateway_outgoing_commands.reportQrCodeOrNfcDetailsToUserInterface(emulated_unlock_qr);

        if (source == UnlockCodeSource.FROM_SERVER)
        {
            local_database_storage.storeAuditTrailEvent(AuditTrailEvent.QR_FROM_SERVER_FEATURE_ENABLE_UNLOCK, ntp_time.currentTimeMillis(), INVALID_USER_ID);
        }
        else
        {
            local_database_storage.storeAuditTrailEvent(AuditTrailEvent.QR_FEATURE_ENABLE_UNLOCK, ntp_time.currentTimeMillis(), INVALID_USER_ID);
        }
    }


    public void emulateQrCodeUnlockAdmin(UnlockCodeSource source)
    {
        showOnScreenMessage("QR_CODE_UNLOCK_ADMIN command received");

        QrCodeData emulated_unlock_qr = new QrCodeData(BarcodeDeviceType.BARCODE_TYPE__UNLOCK_REQUEST_ADMIN,1);

        patient_gateway_outgoing_commands.reportQrCodeOrNfcDetailsToUserInterface(emulated_unlock_qr);

        if (source == UnlockCodeSource.FROM_SERVER)
        {
            local_database_storage.storeAuditTrailEvent(AuditTrailEvent.QR_FROM_SERVER_ADMIN_UNLOCK, ntp_time.currentTimeMillis(), INVALID_USER_ID);
        }
        else
        {
            local_database_storage.storeAuditTrailEvent(AuditTrailEvent.QR_ADMIN_UNLOCK, ntp_time.currentTimeMillis(), INVALID_USER_ID);
        }
    }


    public void emulateQrCodeUnlockUser(UnlockCodeSource source)
    {
        showOnScreenMessage("QR_CODE_UNLOCK_USER command received");

        QrCodeData emulated_unlock_qr = new QrCodeData(BarcodeDeviceType.BARCODE_TYPE__UNLOCK_REQUEST_GENERAL, 1);

        patient_gateway_outgoing_commands.reportQrCodeOrNfcDetailsToUserInterface(emulated_unlock_qr);

        if (source == UnlockCodeSource.FROM_SERVER)
        {
            local_database_storage.storeAuditTrailEvent(AuditTrailEvent.QR_FROM_SERVER_GENERAL_UNLOCK, ntp_time.currentTimeMillis(), INVALID_USER_ID);
        }
        else
        {
            local_database_storage.storeAuditTrailEvent(AuditTrailEvent.QR_GENERAL_UNLOCK, ntp_time.currentTimeMillis(), INVALID_USER_ID);
        }
    }


    private final BroadcastReceiver broadcastReceiverIncomingCommandFromServer = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int command = intent.getIntExtra("command", 0);

            try
            {
                Commands incoming_server_command = Commands.values()[command];

                Log.d(TAG, "Incoming Server Command = " + incoming_server_command.toString());

                switch(incoming_server_command)
                {
                    case CMD_FROM_SERVER__ENABLE_DATA_SYNCING_TO_SERVER:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Enable Data Syncing to Server", enabled);

                        // Turn on Server link
                        enableDisableServerSyncing(enabled);

                        // Tell UI that server syncing is enabled/disabled
                        patient_gateway_outgoing_commands.reportServerSyncEnableStatus();
                    }
                    break;

                    case CMD_FROM_SERVER__START_DEVICE_SETUP_MODE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);

                        // The majority of times, this parameter will NOT be there as its special case where the customer is making
                        // their own "periodic setup mode" via WAMP/MQTT. In this case they do not need to stream the data live as they will process it via the database
                        boolean do_not_stream_data = intent.getBooleanExtra("do_not_stream_data", false);

                        startDeviceSetupModeFromServer(device_info, do_not_stream_data);
                    }
                    break;

                    case CMD_FROM_SERVER__STOP_DEVICE_SETUP_MODE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);

                        stopDeviceSetupModeFromServer(device_info);
                    }
                    break;

                    case CMD_FROM_SERVER__START_DEVICE_RAW_ACCELEROMETER_MODE:
                    {
                        BtleSensorDevice device_info = (BtleSensorDevice) getDeviceInfoFromIntent(intent);

                        // The majority of times, this parameter will NOT be there as its special case where the customer is making
                        // their own "periodic accelerometer mode" via WAMP/MQTT. In this case they do not need to stream the data live as they will process it via the database
                        boolean do_not_stream_data = intent.getBooleanExtra("do_not_stream_data", false);

                        startDeviceRawAccelerometerModeFromServer(device_info, do_not_stream_data);
                    }
                    break;

                    case CMD_FROM_SERVER__STOP_DEVICE_RAW_ACCELEROMETER_MODE:
                    {
                        BtleSensorDevice device_info = (BtleSensorDevice) getDeviceInfoFromIntent(intent);

                        stopDeviceRawAccelerometerModeFromServer(device_info);
                    }
                    break;

                    case CMD_FROM_SERVER__EXPORT_LOCAL_DATABASE_TO_ANDROID_ROOT:
                    {
                        // Already does Toast message inside function
                        exportDB();
                    }
                    break;

                    case CMD_FROM_SERVER__EMPTY_LOCAL_DATABASE:
                    {
                        remoteEmptyLocalDatabase();
                    }
                    break;

                    case CMD_FROM_SERVER__REPORT_GATEWAY_STATUS:
                    {
                        reportGatewayStatusToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_PATIENT_NAME_LOOKUP:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Enable Patient Name Lookup", enabled);

                        enablePatientNameLookupFromServer(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_PRE_DEFINED_ANNOTATIONS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Enable Pre Defined Annotations ", enabled);

                        enablePredefinedAnnotationsFromServer(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_AUTO_ADD_EWS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Enable Auto Add EWS ", enabled);

                        enableAutoAddEWSFromServer(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_DFU:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Enable DFU ", enabled);

                        enableDfuFromServer(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_PERIODIC_SETUP_MODE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Enable Periodic Setup Mode", enabled);

                        enablePeriodicSetupModeFromServer(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__MANUAL_TIME_SYNC:
                    {
                        forceNtpTimeSync();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_DUMMY_DATA_MODE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        enableDummyDataModeFromServer(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__QR_CODE_UNLOCK_USER:
                    {
                        emulateQrCodeUnlockUser(UnlockCodeSource.FROM_SERVER);
                    }
                    break;

                    case CMD_FROM_SERVER__QR_CODE_UNLOCK_ADMIN:
                    {
                        emulateQrCodeUnlockAdmin(UnlockCodeSource.FROM_SERVER);
                    }
                    break;

                    case CMD_FROM_SERVER__QR_CODE_UNLOCK_FEATURE_ENABLE:
                    {
                        emulateQrCodeUnlockFeatureEnable(UnlockCodeSource.FROM_SERVER);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_SIMPLE_HEART_RATE_ALGORITHM:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Enable Simple Heart Rate Algorithm", enabled);

                        enableSimpleHeartRateAlgorithm(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_WEBSERVICE_AUTHENTICATION:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        gateway_settings.storeWebServiceAuthenticationEnabled(enabled);

                        server_syncing.useAuthenticationForWebServiceCalls(enabled);

                        patient_gateway_outgoing_commands.reportWebServiceAuthenticationEnabledStatus(enabled);

                        reportGatewayStatusToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_WEBSERVICE_ENCRYPTION:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        gateway_settings.storeWebServiceEncryptionEnabled(enabled);

                        server_syncing.useEncryptionForWebServiceCalls(enabled);

                        patient_gateway_outgoing_commands.reportWebServiceEncryptionEnabledStatus(enabled);

                        reportGatewayStatusToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__SET_JSON_ARRAY_SIZE:
                    {
                        int size = intent.getIntExtra("array_size", 500);

                        setMaxWebserviceJsonArraySize(size);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_SECONDS:
                    {
                        SensorType sensorType = getSensorTypeFromIntent(intent);

                        int length_in_seconds = intent.getIntExtra("length_in_seconds", 60);
                        int time_in_minutes = length_in_seconds / 60;

                        setLongTimeMeasurementTimeoutInMinutes(sensorType, time_in_minutes);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK:
                    {
                        int measurements_per_tick = intent.getIntExtra("value", 1);

                        setNumberOfDummyDataModeMeasurementsPerTick(measurements_per_tick);
                    }
                    break;

                    case CMD_FROM_SERVER__RESET_GATEWAY_AND_UI_RUN_COUNTERS:
                    {
                        resetGatewayAndUserInterfaceRunCounters();
                    }
                    break;

                    case CMD_FROM_SERVER__SET_SETUP_MODE_TIME_IN_SECONDS:
                    {
                        int length_in_seconds = intent.getIntExtra("length_in_seconds", 1);

                        setSetupModeTimeInSeconds(length_in_seconds);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS:
                    {
                        int length_in_seconds = intent.getIntExtra("length_in_seconds", 1);

                        setDevicePeriodicModeActiveTimeInSeconds(length_in_seconds);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS:
                    {
                        int length_in_seconds = intent.getIntExtra("length_in_seconds", 1);

                        setDevicePeriodicModePeriodTimeInSeconds(length_in_seconds);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID:
                    {
                        int number = intent.getIntExtra("number", 0);

                        setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(number);
                    }
                    break;

                    case CMD_FROM_SERVER__MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID:
                    {
                        int number = intent.getIntExtra("number", 0);

                        setNumberOfInvalidNoninWristOxIntermediateMeasurementsBeforeMinuteMarkedAsInvalid(number);
                    }
                    break;

                    case CMD_FROM_SERVER__REPORT_FEATURES_ENABLED:
                    {
                        reportFeaturesEnabledToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_MANUAL_VITAL_SIGNS_ENTRY:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Manual Vital Sign Entry", enabled);

                        enableManualVitalSigns(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_CSV_OUTPUT:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("CSV Output", enabled);

                        gateway_settings.storeCsvOutputEnableStatus(enabled);

                        patient_gateway_outgoing_commands.reportCsvOutputEnableStatus(enabled);

                        reportFeaturesEnabledToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_NIGHT_MODE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        enableNightModeFromServer(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_UNPLUGGED_OVERLAY:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Unplugged Overlay", enabled);

                        enableUnpluggedOverlay(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__RESET_SERVER_SYNC_STATUS:
                    {
                        resetServerSyncStatusAndTriggerSync();
                    }
                    break;

                    case CMD_FROM_SERVER__SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        enableSpO2SpotMeasurements(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__REPORT_PATIENT_NAME_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP:
                    {
                        String firstName = intent.getStringExtra("firstName");
                        String lastName = intent.getStringExtra("lastName");
                        String dob = intent.getStringExtra("dob");
                        String gender = intent.getStringExtra("gender");

                        boolean complete = intent.getBooleanExtra("complete", false);                   // Server lookup completed
                        PatientDetailsLookupStatus status = PatientDetailsLookupStatus.values()[intent.getIntExtra("status", 0)];

/*
                    // These are useful during testing, but must NOT be used in production as it means we have Patient Names in the log files
                    Log.e(TAG, "CMD_FROM_SERVER__REPORT_PATIENT_NAME_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP : firstName = " + firstName);
                    Log.e(TAG, "CMD_FROM_SERVER__REPORT_PATIENT_NAME_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP : lastName = " + lastName);
                    Log.e(TAG, "CMD_FROM_SERVER__REPORT_PATIENT_NAME_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP : dob = " + dob);
                    Log.e(TAG, "CMD_FROM_SERVER__REPORT_PATIENT_NAME_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP : gender = " + gender);
*/
                        Log.d(TAG, "CMD_FROM_SERVER__REPORT_PATIENT_NAME_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP : complete = " + complete);
                        Log.d(TAG, "CMD_FROM_SERVER__REPORT_PATIENT_NAME_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP : status = " + status);

                        patient_gateway_outgoing_commands.sendCommandReportPatientNameFromServerLookupOfHospitalPatientId(firstName, lastName, dob, gender, complete, status);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_DISPLAY_TIMEOUT_IN_SECONDS:
                    {
                        int length_in_seconds = intent.getIntExtra("length_in_seconds", 1);

                        setDisplayTimeoutInSeconds(length_in_seconds);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY:
                    {
                        boolean enabled = intent.getBooleanExtra("enabled", false);

                        setDisplayTimeoutAppliesToPatientVitals(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__REQUEST_DATABASE_SYNC_STATUS:
                    {
                        reportServerSyncStatusToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__REQUEST_ALL_DEVICE_INFO_OBJECTS:
                    {
                        reportAllDeviceInfoToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__DELETE_EXPORTED_DATABASES:
                    {
                        deleteExportedDatabases();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_GSM_MODE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Early GSM Mode", enabled);

                        setGsmModeOnlyEnabledStatus(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_USA_MODE_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("USA Mode", enabled);

                        enableUsaOnlyMode(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_DEVELOPER_POPUP_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Developer Popup", enabled);

                        storeDeveloperPopupEnabledStatus(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__SHOW_SERVER_SYNCING_POPUP:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Show server syncing popup", enabled);

                        patient_gateway_outgoing_commands.showServerSyncingStatusPopupOnUserInterface(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_SHOW_NUMBERS_ON_BATTERY_INDICATOR:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Show numbers on battery indicator", enabled);

                        showNumbersOnBatteryIndicators(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_SHOW_IP_ADDRESS_ON_WIFI_POPUP:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Show IP address on Wifi popup", enabled);

                        setShowIpAddressOnWifiPopupEnabledStatus(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_RUN_DEVICES_IN_TEST_MODE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Run devices in test mode", enabled);

                        runDevicesInTestMode(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_SHOW_MAC_ADDRESS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Show MAC address on settings page", enabled);

                        gateway_settings.storeShowMacAddressOnSettingsPage(enabled);

                        patient_gateway_outgoing_commands.reportShowMacAddressEnabledStatus(gateway_settings.getShowMacAddressOnSettingsPage());

                        reportFeaturesEnabledToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_LIFETOUCH_ACTIVITY_LEVEL:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Lifetouch activity level", enabled);

                        gateway_settings.storeShowLifetouchActivityLevel(enabled);

                        patient_gateway_outgoing_commands.reportShowLifetouchActivityLevelEnabledStatus(gateway_settings.getShowLifetouchActivityLevel());

                        reportFeaturesEnabledToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_SESSION_AUTO_RESUME:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Enable session auto resume", enabled);

                        enableSessionAutoResume(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_AUTO_UPLOAD_LOG_FILES_TO_SERVER:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Auto upload log files to server", enabled);

                        enableAutoUploadLogsToServer(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_MANUFACTURING_MODE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Manufacturing mode", enabled);

                        gateway_settings.storeManufacturingModeEnabledStatus(enabled);

                        patient_gateway_outgoing_commands.reportManufacturingModeEnabledStatus(gateway_settings.getManufacturingModeEnabledStatus());

                        reportFeaturesEnabledToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_USE_BACK_CAMERA:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Use back camera", enabled);

                        gateway_settings.storeUseBackCameraEnabledStatus(enabled);

                        patient_gateway_outgoing_commands.reportUseBackCameraEnabledStatus(gateway_settings.getUseBackCameraEnabledStatus());

                        reportFeaturesEnabledToServer();
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_PATIENT_ORIENTATION:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Enable patient orientation", enabled);

                        enablePatientOrientation(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_WIFI_LOGGING:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Wifi logging", enabled);

                        wifiLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_GSM_LOGGING:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("GSM logging", enabled);

                        gsmLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_DATABASE_LOGGING:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Database logging", enabled);

                        databaseLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_SERVER_LOGGING:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Server logging", enabled);

                        serverLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_BATTERY_LOGGING:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Battery logging", enabled);

                        batteryLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS:
                    {
                        int length_in_seconds = intent.getIntExtra("length_in_seconds", 60);

                        setLifetempMeasurementIntervalInSeconds(length_in_seconds);
                    }
                    break;

                    case CMD_FROM_SERVER__SET_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS:
                    {
                        int length_in_seconds = intent.getIntExtra("length_in_seconds", 60);

                        storePatientOrientationMeasurementInterval(length_in_seconds);
                    }
                    break;

                    case CMD_FROM_SERVER__REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_UPDATE:
                    {
                        VideoCallContactsLookupStatus status = VideoCallContactsLookupStatus.values()[intent.getIntExtra("status", -1)];

                        Log.d(TAG, "CMD_FROM_SERVER__REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_UPDATE : " + status);

                        switch (status)
                        {
                            case STARTING_REQUEST_TO_EXTERNAL_SERVER:
                            {
//TODO
                            }
                            break;

                            case CONTACTS_RECEIVED_FROM_EXTERNAL_SERVER:
                            {
                                ArrayList<VideoCallContact> contactList = intent.getParcelableArrayListExtra("contactList");

                                Log.d(TAG, "CONTACTS_RECEIVED_FROM_EXTERNAL_SERVER : Number of contacts = " + (contactList != null ? contactList.size() : 0));

                                patient_gateway_outgoing_commands.reportPatientSpecificVideoCallContactsReceivedFromServer(true, contactList);
                            }
                            break;

                            case CONTACTS_LOOKUP_FAILED:
                            default:
                            {
                                Log.d(TAG, "CONTACTS_LOOKUP_FAILED");
                            }
                            break;
                        }
                    }
                    break;

                    case CMD_FROM_SERVER__REQUEST_VIDEO_CALL:
                    {
                        VideoCallDetails videoCallDetails = intent.getParcelableExtra("video_call_details");

                        Log.d(TAG, "CMD_FROM_SERVER_REQUEST_VIDEO_CALL : videoCallDetails = " + videoCallDetails);

                        forwardVideoCallRequestToUserInterface(videoCallDetails);
                    }
                    break;

                    case CMD_FROM_SERVER__JOIN_VIDEO_CALL:
                    {
                        VideoCallDetails videoCallDetails = intent.getParcelableExtra("video_call_details");
    
                        Log.d(TAG, "CMD_FROM_SERVER_JOIN_VIDEO_CALL : videoCallDetails = " + videoCallDetails);
    
                        forwardVideoCallJoinToUserInterface(videoCallDetails);
                    }
                    break;

                    case CMD_FROM_SERVER__SERVER_DECLINED_VIDEO_CALL:
                    {
                        Log.d(TAG, "CMD_FROM_SERVER__SERVER_DECLINED_VIDEO_CALL");

                        VideoCallContact contactThatDeclinedTheCall = intent.getParcelableExtra("contactThatDeclinedTheCall");

                        forwardVideoCallDeclinedToUserInterface(contactThatDeclinedTheCall);
                    }
                    break;

                    case CMD_FROM_SERVER__UPDATE_VIDEO_CALL_CONNECTION_ID:
                    {
                        String connectionId = intent.getStringExtra("connectionId");

                        Log.d(TAG, "CMD_FROM_SERVER__UPDATE_VIDEO_CALL_CONNECTION_ID : connectionId = " + connectionId);

                        forwardBrowsersConnectionIdUserInterface(connectionId);
                    }
                    break;

                    case CMD_FROM_SERVER__ENABLE_VIDEO_CALLS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showEnabledStatusOnScreen("Video Calls", enabled);

                        enableVideoCalls(enabled);
                    }
                    break;

                    case CMD_FROM_SERVER__EXIT_GATEWAY:
                    {
                        Log.d(TAG, "CMD_FROM_SERVER__EXIT_GATEWAY");

                        // Cant exit the Gateway app here as the UI is still running.
                        // So send a command to the UI to make it think someone has pressed the Exit button on the Admin screen
                        patient_gateway_outgoing_commands.tellUserInterfaceToExit();
                    }
                    break;

                    default:
                    {
                        // Its a command not meant for a Gateway - ignore
                    }
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "EXCEPTION. command byte = " + command + " : " + e);
            }
        }
    };


    private void showEnabledStatusOnScreen(String display_string, boolean enabled)
    {
        if (enabled)
        {
            showOnScreenMessage(display_string + " : Enabled");
        }
        else
        {
            showOnScreenMessage(display_string + " : Disabled");
        }
    }


    private final BroadcastReceiver broadcastReceiverIncomingCommandsFromUserInterface = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int command = intent.getIntExtra("command", 0);

            try
            {
                Commands incoming_command = Commands.values()[command];

                if (incoming_command != Commands.CMD_CHECK_GATEWAY_UI_CONNECTION)
                {
                    Log.d(TAG, "Command = " + incoming_command.toString());
                }
                else
                {
                    //Log.d(TAG, "Ping from UI at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(getNtpTimeNowInMilliseconds()) + " : " + logProcessHeapSize());
                }

                switch(incoming_command)
                {
                    case CMD_CREATE_NEW_SESSION:
                    {
                        String hospital_patient_id = intent.getStringExtra("hospital_patient_id");
                        int servers_threshold_set_id = intent.getIntExtra("servers_threshold_set_id", -1);
                        int servers_threshold_set_age_block_id = intent.getIntExtra("servers_threshold_set_age_block_id", -1);
                        int session_started_by_user_id = intent.getIntExtra("gateway_user_id", 1);

                        createNewSession(session_started_by_user_id, hospital_patient_id, servers_threshold_set_id, servers_threshold_set_age_block_id);
                    }
                    break;

                    case CMD_UPDATE_EXISTING_SESSION:
                    {
                        int session_updated_by_user_id = intent.getIntExtra("gateway_user_id", 1);

                        long session_updated_time = getNtpTimeNowInMilliseconds();

                        addDeviceInfoAndDeviceSessionsIfNeeded(session_updated_by_user_id, session_updated_time);

                        // Let the UI know the new device session numbers
                        patient_gateway_outgoing_commands.reportGatewaySessionNumbers(patient_session_info.android_database_patient_session_id, device_sessions);

                        reportGatewayStatusToServer();
                    }
                    break;

                    case CMD_END_EXISTING_SESSION:
                    {
                        int session_ended_by_user_id = intent.getIntExtra("session_ended_by_user_id", -1);
                        boolean turn_devices_off = intent.getBooleanExtra("turn_devices_off", false);
                        long session_end_time = intent.getLongExtra("session_end_time", getNtpTimeNowInMilliseconds());

                        endExistingSession(session_ended_by_user_id, turn_devices_off, session_end_time);
                    }
                    break;

                    case CMD_GET_PATIENT_THRESHOLD_SET:
                    {
                        patient_gateway_outgoing_commands.reportPatientThresholdSet(patient_info);
                    }
                    break;

                    case CMD_GET_HOSPITAL_PATIENT_ID:
                    {
                        patient_gateway_outgoing_commands.reportHospitalPatientID(patient_info.getHospitalPatientId());
                    }
                    break;

                    case CMD_GET_GATEWAY_SESSION_NUMBERS:
                    {
                        patient_gateway_outgoing_commands.reportGatewaySessionNumbers(patient_session_info.android_database_patient_session_id, device_sessions);
                    }
                    break;

                    case CMD_ENABLE_SERVER_SYNCING:
                    {
                        boolean server_sync_enabled = intent.getBooleanExtra("enable_server_syncing", false);
                        enableDisableServerSyncing(server_sync_enabled);
                    }
                    break;

                    case CMD_ENABLE_REALTIME_LINK:
                    {
                        boolean real_time_link_enabled = intent.getBooleanExtra("enable_real_time_link", false);
                        enableDisableRealTimeLink(real_time_link_enabled);
                    }
                    break;

                    case CMD_GET_SERVER_SYNC_ENABLE_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportServerSyncEnableStatus();
                    }
                    break;

                    case CMD_GET_DUMMY_DATA_MODE_DEVICE_LEADS_OFF_ENABLE_STATUS:
                    {
                        SensorType sensor_type = getSensorTypeFromIntent(intent);

                        patient_gateway_outgoing_commands.reportDummyDataModeDeviceLeadsOffEnableStatus(sensor_type, dummy_data_instance.getSimulateLeadsOff(sensor_type));
                    }
                    break;

                    case CMD_GET_REALTIME_LINK_ENABLE_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportRealTimeLinkEnableStatus();
                    }
                    break;

                    case CMD_ENABLE_NIGHT_MODE:
                    {
                        night_mode_enabled = intent.getBooleanExtra("night_mode_enable", false);
                        Log.d(TAG, "night_mode_enabled = " + night_mode_enabled);

                        device_info_manager.enableNightMode(night_mode_enabled);

                        reportGatewayStatusToServer();
                    }
                    break;

                    case CMD_SET_DESIRED_DEVICE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);

                        boolean force_show_on_ui = false;
                        // Check to see if there is an existing Device Info of the request Sensor Type
                        // If so then reset it to remove it from the Patient Gateway.
                        // This ensures there is only ever a single Sensor Type (with a Radio) at a time
                        DeviceInfo sensor_type_device_info = device_info_manager.getDeviceInfoBySensorType(device_info.sensor_type);
                        if (sensor_type_device_info.isDeviceTypeASensorDevice())
                        {
                            if (sensor_type_device_info.isDeviceTypePartOfPatientSession() && sensor_type_device_info.isDeviceHumanReadableDeviceIdValid())
                            {
                                Log.w(TAG, "CMD_SET_DESIRED_DEVICE : Ignoring " + device_info.device_type + " as " + sensor_type_device_info.device_type + " is part of the Patient Session");
                            }
                            else
                            {
                                if(sensor_type_device_info.isDeviceTypePartOfPatientSession())
                                {
                                    // device was previously removed - so need to set show_on_ui true for new device...
                                    force_show_on_ui = true;
                                }

                                sensor_type_device_info.resetAsNew();
                            }
                        }

                        device_info.human_readable_device_id = intent.getLongExtra("human_readable_device_id", INVALID_HUMAN_READABLE_DEVICE_ID);
                        device_info.bluetooth_address = intent.getStringExtra("bluetooth_address");
                        device_info.desired_device_connection_status = DeviceConnectionStatus.CONNECTED;
                        device_info.dummy_data_mode = intent.getBooleanExtra("dummy_data_mode", false);
                        device_info.measurement_interval_in_seconds = intent.getIntExtra("measurement_interval_in_seconds", 60);

                        device_info.lot_number = intent.getStringExtra("lot_number");
                        device_info.manufacture_date = new DateTime(intent.getLongExtra("manufacture_date_in_millis", 0));
                        device_info.expiration_date = new DateTime(intent.getLongExtra("expiration_date_in_millis", 0));

                        // This is only useful in Dummy Data Mode as the real device will update this
                        int firmware_version = intent.getIntExtra("firmware_version", INVALID_FIRMWARE_VERSION);
                        String firmware_string = intent.getStringExtra("firmware_string");
                        device_info.setDeviceFirmwareVersion(firmware_string, firmware_version);

                        // Only used by BT Classic devices
                        device_info.device_name = intent.getStringExtra("device_name");
                        String pin = String.valueOf(device_info.human_readable_device_id);
                        device_info.pairing_pin_number = "";

                        if(device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
                        {
                            device_info.setPatientOrientationModeEnabled(gateway_settings.getEnablePatientOrientation());
                            device_info.setPatientOrientationModeIntervalTime(gateway_settings.getPatientOrientationMeasurementIntervalInSeconds());
                        }

                        if (device_info.sensor_type == SensorType.SENSOR_TYPE__WEIGHT_SCALE)
                        {
                            device_info.setShowWeightInLbs(gateway_settings.getDisplayWeightInLbsEnabledStatus());
                        }

                        device_info.setDeviceTypePartOfPatientSession(force_show_on_ui);

                        switch (device_info.device_type)
                        {
                            case DEVICE_TYPE__NONIN_WRIST_OX:
                            {
                                device_info.pairing_pin_number = ("000000" + pin).substring(pin.length());
                            }
                            break;

                            case DEVICE_TYPE__FORA_IR20:
                            {
                                device_info.pairing_pin_number = "111111";
                            }
                            break;

                            default:
                            {
                            }
                            break;
                        }

                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                    }
                    break;

                    case CMD_CONNECT_TO_DESIRED_BLUETOOTH_DEVICES:
                    {
                        for (DeviceInfo device_info : device_info_manager.getListOfSensorDeviceInfoObjects())
                        {
                            setDeviceConnectionStatusToSearchingForValidDevices(device_info);
                        }

                        connectNextDeviceToGatewayIfNeededAndNoScanRunning();
                    }
                    break;

                    case CMD_REFRESH_DEVICE_CONNECTION_STATE:
                    {
                        // This forces a resend of sendCommandEndOfDeviceConnection if all the desired devices are connected
                        connectNextDeviceToGatewayIfNeededAndNoScanRunning();
                    }
                    break;

                    case CMD_CHECK_GATEWAY_UI_CONNECTION:
                    {
                        // Have heard from the UI, so reset the UI timeout
                        checkUserInterfaceRunning.resetUserInterfaceTimeout();

                        patient_gateway_outgoing_commands.sendPingCommandToUserInterface();
                    }
                    break;

                    case CMD_START_SETUP_MODE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);

                        enterDeviceSetupMode(device_info, DeviceInfo.OperatingMode.GATEWAY_INITIATED_SETUP_MODE);
                    }
                    break;

                    case CMD_STOP_SETUP_MODE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);

                        exitSetupModeIfRunning(device_info);
                    }
                    break;

                    case CMD_START_DEVICE_RAW_ACCELEROMETER_MODE:
                    {
                        BtleSensorDevice device_info = (BtleSensorDevice)getDeviceInfoFromIntent(intent);

                        enterDeviceRawAccelerometerMode(device_info, DeviceInfo.OperatingMode.GATEWAY_INITIATED_RAW_ACCELEROMETER_MODE);
                    }
                    break;

                    case CMD_STOP_DEVICE_RAW_ACCELEROMETER_MODE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);

                        exitRawAccelerometerModeIfRunning(device_info);
                    }
                    break;

                    case CMD_GET_WARDS_AND_BEDS_FROM_SERVER:
                    {
                        server_syncing.getWardDetailsListFromServer();
                    }
                    break;

                    case CMD_GET_GATEWAY_CONFIG_FROM_SERVER:
                    {
                        server_syncing.getGatewayConfigFromServer();
                    }
                    break;

                    case CMD_GET_SERVER_CONFIGURABLE_TEXT_FROM_SERVER:
                    {
                        server_syncing.getServerConfigurableTextFromServer();
                    }
                    break;

                    case CMD_GET_DEFAULT_EARLY_WARNING_SCORE_TYPES_FROM_SERVER:
                    {
                        server_syncing.sendDefaultEarlyWarningScoreTypesRequest();
                    }
                    break;

                    case CMD_GET_VIEWABLE_WEB_PAGES_FROM_SERVER:
                    {
                        server_syncing.getWebPageDescriptorsFromServer();
                    }
                    break;

                    case CMD_GET_GATEWAYS_ASSIGNED_BED_DETAILS:
                    {
                        reportGatewayBedDetails();
                    }
                    break;

                    case CMD_SET_GATEWAYS_ASSIGNED_BED_DETAILS:
                    {
                        if (isPatientSessionRunning() == false)
                        {
                            String gateways_assigned_bed_id = intent.getStringExtra("gateways_assigned_bed_id");
                            String gateways_assigned_ward_name = intent.getStringExtra("gateways_assigned_ward_name");
                            String gateways_assigned_bed_name = intent.getStringExtra("gateways_assigned_bed_name");

                            Log.d(TAG, "Setting gateways_assigned_bed_id = " + gateways_assigned_bed_id);
                            Log.d(TAG, "Setting gateways_assigned_ward_name = " + gateways_assigned_ward_name);
                            Log.d(TAG, "Setting gateways_assigned_bed_name = " + gateways_assigned_bed_name);

                            gateway_settings.storeGatewayAssignedBedDetails(gateways_assigned_bed_id, gateways_assigned_ward_name, gateways_assigned_bed_name);

                            reportGatewayBedDetails();

                            setup_wizard.getWardsAndBedsSuccess();

                            server_syncing.configureRealTimeServerIfRequired();
                        }
                    }
                    break;

                    case CMD_GET_SERVER_ADDRESS:
                    {
                        Log.d(TAG, "Incoming command = CMD_GET_SERVER_ADDRESS");
                        patient_gateway_outgoing_commands.reportServerAddress(gateway_settings.getServerAddress());
                    }
                    break;

                    case CMD_SET_SERVER_ADDRESS:
                    {
                        String isansys_server_address = intent.getStringExtra("server_address");

                        Log.d(TAG, "Incoming command = CMD_SET_SERVER_ADDRESS = " + isansys_server_address);

                        // Save the address in the Patient Gateway app preferences
                        gateway_settings.storeServerAddress(isansys_server_address);

                        gateway_settings.storeInstallationComplete(false);

                        reportInstallationCompleteFlag();

                        // Read the value back to ensure its stored correctly
                        isansys_server_address = gateway_settings.getServerAddress();

                        // Pass the Server address to classes that need it
                        server_syncing.setIsansysServerAddress(isansys_server_address, gateway_settings.getServerPort());

                        server_syncing.configureRealTimeServerIfRequired();

                        patient_gateway_outgoing_commands.reportServerAddress(isansys_server_address);
                    }
                    break;

                    case CMD_PING_SERVER:
                    {
                        Log.d(TAG, "Incoming command = CMD_PING_SERVER");
                        server_syncing.sendServerPing();
                    }
                    break;

                    case CMD_VALIDATE_QR_CODE:
                    {
                        validateQrCode(intent);
                    }
                    break;

                    case CMD_VALIDATE_INSTALLATION_QR_CODE:
                    {
                        String qr_code_contents = intent.getStringExtra("QR_CODE_CONTENTS");

                        validateInstallationModeQrCodeContents(qr_code_contents);
                    }
                    break;

                    case CMD_RESET_BLUETOOTH:
                    {
                        boolean delayed = intent.getBooleanExtra("delayed", false);
                        boolean remove_devices = intent.getBooleanExtra("remove_devices", false);

                        forceResetBluetooth(delayed, remove_devices);
                    }
                    break;

                    case CMD_DISABLE_BLUETOOTH_ADAPTER:
                    {
                        Log.e(TAG, "CMD_DISABLE_BLUETOOTH_ADAPTER");

                        BluetoothAdapter.getDefaultAdapter().disable();
                    }
                    break;

                    case CMD_ENABLE_BLUETOOTH_ADAPTER:
                    {
                        Log.e(TAG, "CMD_ENABLE_BLUETOOTH_ADAPTER");

                        bluetooth_reset_manager.handleBluetoothOff();
                    }
                    break;

                    case CMD_DISABLE_WIFI:
                    {
                        // NOTE : On Android 11, turning off Wifi does NOT work. Therefore this code will NOT work.
                        // You need to manually turn the Wifi on/off via the Admin Wifi icon
                        enableWifi(false);
                    }
                    break;

                    case CMD_ENABLE_WIFI:
                    {
                        // NOTE : On Android 11, turning off Wifi does NOT work. Therefore this code will NOT work.
                        // You need to manually turn the Wifi on/off via the Admin Wifi icon
                        enableWifi(true);
                    }
                    break;

                    case CMD_EMPTY_LOCAL_DATABASE:
                    {
                        emptyLocalDatabase(DO_NOT_DELETE_EWS_THRESHOLDS);
                    }
                    break;

                    case CMD_EMPTY_LOCAL_DATABASE_INCLUDING_EWS_THRESHOLD_SETS:
                    {
                        emptyLocalDatabaseIncludingEwsThresholdSets();
                    }
                    break;

                    case CMD_EXPORT_LOCAL_DATABASE_TO_ANDROID_ROOT:
                    {
                        exportDB();
                    }
                    break;

                    case CMD_IMPORT_DATABASE_FROM_ANDROID_ROOT:
                    {
                        importDB();
                    }
                    break;

                    case CMD_DELETE_EXPORTED_DATABASES:
                    {
                        deleteExportedDatabases();
                    }
                    break;

                    case CMD_CHECK_DEVICE_STATUS:
                    {
                        if (serverLinkSetupConnectedAndSyncingEnabled())
                        {
                            DeviceType device_type = getDeviceTypeFromIntent(intent);
                            long human_readable_device_id = intent.getLongExtra("human_readable_device_id", INVALID_HUMAN_READABLE_DEVICE_ID);

                            server_syncing.sendCheckDeviceDetails(device_type.ordinal(), human_readable_device_id);
                        }
                    }
                    break;

                    case CMD_SET_DUMMY_SERVER_DETAILS:
                    {
                        String isansys_server_address = "1.2.3.4";
                        String isansys_server_port = "5";

                        String isansys_realtime_port = "8883";

                        // Save the address in the Patient Gateway app preferences
                        gateway_settings.storeServerAddress(isansys_server_address);
                        gateway_settings.setRealTimeServerType(RealTimeServer.MQTT);
                        gateway_settings.storeRealTimeServerPort(isansys_realtime_port);

                        // Read the value back to ensure its stored correctly
                        isansys_server_address = gateway_settings.getServerAddress();

                        // Pass the Server address to classes that need it
                        server_syncing.setIsansysServerAddress(isansys_server_address, isansys_server_port);

                        patient_gateway_outgoing_commands.reportServerAddress(isansys_server_address);

                        gateway_settings.storeGatewayAssignedBedDetails("3", "Dummy Ward", "Dummy Bed");
                    }
                    break;

                    case CMD_RADIO_OFF:
                    {
                        byte timeTillOff = intent.getByteExtra("radio_off_time_till_off", (byte)0);
                        byte timeOff = intent.getByteExtra("radio_off_time_off", (byte)0);

                        Log.d(TAG, "radio_off = timeTillOff " + timeTillOff + " timeOff "+ timeOff);

                        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);

                        // Check it's a BTLE device - getDeviceInfoBySensorType returns DEVICE_TYPE__INVALID device
                        // if there's no lifetouch in the current patient session
                        if(device_info.isDeviceTypeABtleSensorDevice())
                        {
                            ((BtleSensorDevice) device_info).radioOff(timeTillOff, timeOff);
                        }
                    }
                    break;

                    case CMD_SET_SERVER_PORT:
                    {
                        String isansys_server_port = intent.getStringExtra("server_port");

                        // Save the port in the Patient Gateway app preferences
                        gateway_settings.storeServerPort(isansys_server_port);

                        // Read the value back to ensure its stored correctly
                        isansys_server_port = gateway_settings.getServerPort();

                        // Update the webServices
                        server_syncing.setIsansysServerAddress(gateway_settings.getServerAddress(), isansys_server_port);

                        server_syncing.configureRealTimeServerIfRequired();

                        patient_gateway_outgoing_commands.reportServerPort(isansys_server_port);
                    }
                    break;

                    case CMD_GET_SERVER_PORT:
                    {
                        patient_gateway_outgoing_commands.reportServerPort(gateway_settings.getServerPort());
                    }
                    break;

                    case CMD_SET_REALTIME_SERVER_PORT:
                    {
                        String realtime_server_port = intent.getStringExtra("realtime_server_port");

                        // Save the port in the Patient Gateway app preferences
                        gateway_settings.storeRealTimeServerPort(realtime_server_port);

                        // Read the value back to ensure its stored correctly
                        realtime_server_port = gateway_settings.getRealTimeServerPort();

                        server_syncing.configureRealTimeServerIfRequired();

                        patient_gateway_outgoing_commands.reportRealTimeServerPort(realtime_server_port);

                        showOnScreenMessage("RealTime Port Set");
                    }
                    break;

                    case CMD_GET_REALTIME_SERVER_PORT:
                    {
                        patient_gateway_outgoing_commands.reportRealTimeServerPort(gateway_settings.getRealTimeServerPort());
                    }
                    break;

                    case CMD_ENABLE_HTTPS:
                    {
                        boolean use_https = getEnabledFromIntent(intent);

                        enableHttps(use_https);
                    }
                    break;

                    case CMD_GET_HTTPS_ENABLE_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportHttpsEnabledStatus(gateway_settings.getHttpsEnabledStatus());
                    }
                    break;

                    case CMD_ENABLE_WEBSERVICE_AUTHENTICATION:
                    {
                        boolean enable_webservice_authentication = getEnabledFromIntent(intent);

                        enableWebserviceAuthentication(enable_webservice_authentication);
                    }
                    break;

                    case CMD_GET_WEBSERVICE_AUTHENTICATION_ENABLE_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportWebServiceAuthenticationEnabledStatus(gateway_settings.getWebServiceAuthenticationEnabledStatus());
                    }
                    break;

                    case CMD_ENABLE_WEBSERVICE_ENCRYPTION:
                    {
                        boolean enable_webservice_encryption = getEnabledFromIntent(intent);

                        enableWebserviceEncryption(enable_webservice_encryption);
                    }
                    break;

                    case CMD_GET_WEBSERVICE_ENCRYPTION_ENABLE_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportWebServiceEncryptionEnabledStatus(gateway_settings.getWebServiceEncryptionEnabledStatus());
                    }
                    break;

                    case CMD_GET_PATIENT_NAME_FROM_HOSPITAL_PATIENT_ID:
                    {
                        String hospitalPatientId = intent.getStringExtra("hospitalPatientId");
                        String bed_id = gateway_settings.getGatewaysAssignedBedId();
                        String unique_id = bed_id + "-" + getNtpTimeNowInMilliseconds();

                        server_syncing.requestPatientNameFromHospitalPatientDetailsIdIfServerConnected(hospitalPatientId, bed_id, unique_id);
                    }
                    break;

                    case CMD_ENABLE_PATIENT_NAME_LOOKUP:
                    {
                        boolean patient_id_enable_status = getEnabledFromIntent(intent);

                        enablePatientNameLookup(patient_id_enable_status);
                    }
                    break;

                    case CMD_GET_PATIENT_NAME_LOOKUP_ENABLE_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportPatientNameLookupEnableStatus(gateway_settings.getPatientNameLookupEnabledStatus());
                        Log.d(TAG, "CMD_GET_PATIENT_NAME_LOOKUP_ENABLE_STATUS : getPatientNameLookupEnabledStatus is " + gateway_settings.getPatientNameLookupEnabledStatus());
                    }
                    break;

                    case CMD_ENABLE_CSV_OUTPUT:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        enableCsvOutput(enabled);
                    }
                    break;

                    case CMD_GET_CSV_ENABLE_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportCsvOutputEnableStatus(gateway_settings.getCsvOutputEnabledStatus());
                    }
                    break;

                    case CMD_ENABLE_EARLY_WARNING_SCORES_DEVICE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);
                        int user_id = intent.getIntExtra("user_id", -1);
                        long session_updated_time = getNtpTimeNowInMilliseconds();

                        DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE);

                        if(enabled)
                        {
                            device_info.human_readable_device_id = 0;
                            device_info.dummy_data_mode = intent.getBooleanExtra("dummy_data_mode", false);

                            if(device_info.dummy_data_mode)
                            {
                                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);
                                dummy_data_instance.initVariables(device_info);
                            }

                            ThresholdSet threshold_set = patient_info.getThresholdSet();
                            ThresholdSetAgeBlockDetail threshold_set_age_block_detail = patient_info.getThresholdSetAgeBlockDetails();

                            if ((threshold_set != null) && (threshold_set_age_block_detail != null))
                            {
                                ArrayList<ArrayList<ThresholdSetLevel>> selected_ews_levels = threshold_set_age_block_detail.list_of_threshold_set_levels_by_measurement_type;

                                device_info.device_name = threshold_set.name;
                                early_warning_score_processor.cacheSelectedEarlyWarningScores(selected_ews_levels, threshold_set.name);
                            }

                            if(isPatientSessionRunning())
                            {
                                if(addDeviceInfoAndDeviceSessionIfNeeded(device_info, session_updated_time, user_id))
                                {
                                    early_warning_score_processor.enableProcessing(true);
                                }

                                // Let the UI know the new device session numbers
                                patient_gateway_outgoing_commands.reportGatewaySessionNumbers(patient_session_info.android_database_patient_session_id, device_sessions);

                                reportGatewayStatusToServer();
                            }
                        }
                        else
                        {
                            long device_end_session_time = getNtpTimeNowInMilliseconds();

                            removeEarlyWarningScoreDevice(device_end_session_time, user_id);

                            early_warning_score_processor.reset();
                        }

                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                    }
                    break;

                    case CMD_SET_SPOOF_EARLY_WARNING_SCORES:
                    {
                        dummy_data_instance.spoof_early_warning_scores = intent.getBooleanExtra("spoof", false);

                        // update whether ews processor is enabled
                        if(dummy_data_instance.spoof_early_warning_scores == true)
                        {
                            early_warning_score_processor.enableProcessing(false); // spoofing ews so don't process vitals
                        }
                        else if(device_info_manager.isEarlyWarningScoreDevicePartOfSession())
                        {
                            early_warning_score_processor.enableProcessing(true); // not spoofing, and ews is in the session - so need to process vitals
                        }
                        else
                        {
                            early_warning_score_processor.enableProcessing(false); // not spoofing, but EWS not in the session - so shouldn't be processing
                        }

                        patient_gateway_outgoing_commands.reportSpoofEarlyWarningScores(dummy_data_instance.spoof_early_warning_scores);
                    }
                    break;

                    case CMD_GET_SPOOF_EARLY_WARNING_SCORES:
                    {
                        patient_gateway_outgoing_commands.reportSpoofEarlyWarningScores(dummy_data_instance.spoof_early_warning_scores);
                    }
                    break;

                    case CMD_FORCE_DEVICE_LEADS_OFF_STATE:
                    {
                        SensorType sensor_type = getSensorTypeFromIntent(intent);
                        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(sensor_type);

                        boolean simulate_leads_off = intent.getBooleanExtra("simulate_leads_off", false);

                        dummy_data_instance.setSimulateLeadsOff(device_info.sensor_type, simulate_leads_off);

                        patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, simulate_leads_off);

                        server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, simulate_leads_off);
                    }
                    break;

                    case CMD_ENABLE_DEVICE_PERIODIC_SETUP_MODE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        Log.d(TAG, "CMD_ENABLE_DEVICE_PERIODIC_SETUP_MODE : enabled is " + enabled);

                        enableDevicePeriodicSetupMode(enabled);
                    }
                    break;

                    case CMD_GET_DEVICE_PERIODIC_SETUP_MODE_ENABLE_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportDevicePeriodicSetupModeEnableStatus(gateway_settings.getPeriodicModeEnabledStatus());

                        Log.d(TAG, "CMD_GET_DEVICE_PERIODIC_SETUP_MODE_ENABLE_STATUS : enabled is " + gateway_settings.getPeriodicModeEnabledStatus());
                    }
                    break;

                    case CMD_GET_DEVICE_LEADS_OFF_STATUS:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);

                        Log.d(TAG, "CMD_GET_DEVICE_LEADS_OFF_STATUS : " + device_info.getSensorTypeAndDeviceTypeAsString() + " leads off detection state is  " + device_info.device_disconnected_from_body);

                        if(device_info.dummy_data_mode)
                        {
                            patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, dummy_data_instance.getSimulateLeadsOff(device_info.sensor_type));
                        }
                        else
                        {
                            patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);
                        }
                    }
                    break;

                    case CMD_DO_NTP_TIME_SYNC:
                    {
                        if (isPatientSessionRunning() == false)
                        {
                            Log.d(TAG, "CMD_DO_NTP_TIME_SYNC : Command to sync NTP time triggered.  ");

                            ntpTimeSync(true);
                        }
                    }
                    break;

                    case CMD_GET_NTP_CLOCK_OFFSET_IN_MS:
                    {
                        double local_clock_offset = AlternateTimeSource.getTimeOffsetInMilliseconds();

                        if (local_clock_offset == 0)
                        {
                            patient_gateway_outgoing_commands.reportNtpClockOffset(false, -1, -1, -1);
                        }
                        else
                        {
                            patient_gateway_outgoing_commands.reportNtpClockOffset(true, local_clock_offset, -1, -1);
                        }
                    }
                    break;

                    case CMD_EXIT_BUTTON_PRESSED:
                    {
                        Log.e(TAG, "CMD_EXIT_BUTTON_PRESSED : Stopping Gateway !!!!!!");

                        exitApplicationAndStopService();
                    }
                    break;

                    case CMD_GET_JSON_ARRAY_SIZE:
                    {
                        patient_gateway_outgoing_commands.reportJsonArraySize(gateway_settings.getNumberOfDatabaseRowsPerJsonMessage());
                    }
                    break;

                    case CMD_SET_JSON_ARRAY_SIZE:
                    {
                        int json_array_size = intent.getIntExtra("json_array_size", 50);
                        setMaxWebserviceJsonArraySize(json_array_size);
                    }
                    break;

                    case CMD_SPOOF_DEVICE_CONNECTION_STATE:
                    {
                        SensorType sensor_type = getSensorTypeFromIntent(intent);
                        boolean connected = intent.getBooleanExtra("connected", false);

                        forceDeviceConnectionState(connected, device_info_manager.getDeviceInfoBySensorType(sensor_type));
                    }
                    break;

                    case CMD_GET_WIFI_STATUS:
                    {
                        // This code is now redundant as getWifiStatus() now handles all the re-enabled and reconnection automatically.
                        // Left in as a good test case. Hit the Wifi icon on the footer, and this code disconnects from the SSID. Forces the auto reconnection code to trigger and reconnect
                        Log.d(TAG, "Command received CMD_GET_WIFI_STATUS");

                        wifi_event_manager.checkAndSetWifiStatus();
                    }
                    break;

                    case CMD_GET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK:
                    {
                        patient_gateway_outgoing_commands.reportNumberOfDummyDataModeMeasurementsPerTick(gateway_settings.getNumberOfDummyDataModeMeasurementsPerTick());
                    }
                    break;

                    case CMD_SET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK:
                    {
                        int measurements_per_tick = intent.getIntExtra("measurements_per_tick", 1);

                        gateway_settings.storeDummyDataModeMeasurementsPerTick(measurements_per_tick);

                        dummy_data_instance.setNumberOfSimulatedMeasurementsPerTick(measurements_per_tick);
                    }
                    break;

                    case CMD_TELL_GATEWAY_THAT_UI_HAS_BOOTED:
                    {
                        most_recent_ui_boot_time = intent.getLongExtra("most_recent_ui_boot_time", 0);

                        number_of_times_ui_has_booted = local_database_storage.storeUiStartupTime(most_recent_ui_boot_time);
                    }
                    break;

                    case CMD_GET_PATIENT_START_SESSION_TIME:
                    {
                        Log.d(TAG, "CMD_GET_PATIENT_START_SESSION_TIME : command Received");

                        long start_date_in_ms = patient_session_info.start_session_time;
                        patient_gateway_outgoing_commands.reportPatientStartSessionTime(start_date_in_ms);
                    }
                    break;

                    case CMD_SET_SETUP_MODE_TIME_IN_SECONDS:
                    {
                        int setup_mode_time_in_seconds = intent.getIntExtra("setup_mode_time_in_seconds", -1);

                        setSetupModeTimeInSeconds(setup_mode_time_in_seconds);
                    }
                    break;

                    case CMD_GET_SETUP_MODE_TIME_IN_SECONDS:
                    {
                        patient_gateway_outgoing_commands.reportSetupModeTimeInSeconds(gateway_settings.getSetupModeTimeInSeconds());
                    }
                    break;

                    case CMD_SET_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS:
                    {
                        int time_in_seconds = intent.getIntExtra("time_in_seconds", -1);

                        setDevicePeriodicModePeriodTimeInSeconds(time_in_seconds);
                    }
                    break;

                    case CMD_GET_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS:
                    {
                        patient_gateway_outgoing_commands.reportDevicePeriodicModePeriodTimeInSeconds(gateway_settings.getPeriodicModePeriodTimeInSeconds());
                    }
                    break;

                    case CMD_SET_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS:
                    {
                        int time_in_seconds = intent.getIntExtra("time_in_seconds", -1);

                        setDevicePeriodicModeActiveTimeInSeconds(time_in_seconds);
                    }
                    break;

                    case CMD_GET_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS:
                    {
                        patient_gateway_outgoing_commands.reportDevicePeriodicModeActiveTimeInSeconds(gateway_settings.getPeriodicModeActiveTimeInSeconds());
                    }
                    break;

                    case CMD_SET_MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID:
                    {
                        int number = intent.getIntExtra("number", 0);

                        setNumberOfInvalidNoninWristOxIntermediateMeasurementsBeforeMinuteMarkedAsInvalid(number);
                    }
                    break;

                    case CMD_GET_MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID:
                    {
                        patient_gateway_outgoing_commands.reportMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(gateway_settings.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid());
                    }
                    break;

                    case CMD_SET_DISPLAY_TIMEOUT_IN_SECONDS:
                    {
                        int time_in_seconds = intent.getIntExtra("time_in_seconds", 120);

                        setDisplayTimeoutInSeconds(time_in_seconds);
                    }
                    break;

                    case CMD_GET_DISPLAY_TIMEOUT_IN_SECONDS:
                    {
                        patient_gateway_outgoing_commands.reportDisplayTimeoutInSeconds(gateway_settings.getDisplayTimeoutLengthInSeconds());
                    }
                    break;

                    case CMD_SET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY:
                    {
                        boolean display_timeout_applies_to_patient_vitals = intent.getBooleanExtra("display_timeout_applies_to_patient_vitals", false);

                        setDisplayTimeoutAppliesToPatientVitals(display_timeout_applies_to_patient_vitals);
                    }
                    break;

                    case CMD_GET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY:
                    {
                        patient_gateway_outgoing_commands.reportDisplayTimeoutAppliesToPatientVitals(gateway_settings.getDisplayTimeoutAppliesToPatientVitals());
                    }
                    break;

                    case CMD_SET_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID:
                    {
                        int percentage = intent.getIntExtra("percentage", 0);

                        setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(percentage);
                    }
                    break;

                    case CMD_GET_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID:
                    {
                        patient_gateway_outgoing_commands.reportPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(gateway_settings.getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid());
                    }
                    break;

                    case CMD_STORE_AUDIT_TRAIL_EVENT:
                    {
                        int by_user_id = intent.getIntExtra("by_user_id", -1);
                        //int gotVal = intent.getIntExtra("event", -1);
                        AuditTrailEvent event = AuditTrailEvent.values()[intent.getIntExtra("event", -1)];
                        long timestamp = intent.getLongExtra("timestamp", -1);
                        String additional = intent.getStringExtra("additional");

                        local_database_storage.storeAuditTrailEvent(event, timestamp, by_user_id, additional);
                    }
                    break;

                    case CMD_STORE_VITAL_SIGN:
                    {
                        VitalSignType vital_sign_type = getVitalSignTypeFromIntent(intent);

                        DeviceType device_type = getDeviceTypeFromIntent(intent);

                        int by_user_id = intent.getIntExtra("by_user_id", -1);

                        Log.d(TAG, "CMD_STORE_VITAL_SIGN : " + vital_sign_type);

                        switch (vital_sign_type)
                        {
                            case MANUALLY_ENTERED_HEART_RATE:
                            {
                                MeasurementManuallyEnteredHeartRate measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredHeartRateMeasurement(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_RESPIRATION_RATE:
                            {
                                MeasurementManuallyEnteredRespirationRate measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredRespirationRateMeasurement(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_TEMPERATURE:
                            {
                                MeasurementManuallyEnteredTemperature measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredTemperatureMeasurement(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_SPO2:
                            {
                                MeasurementManuallyEnteredSpO2 measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredOximeterMeasurement(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_BLOOD_PRESSURE:
                            {
                                MeasurementManuallyEnteredBloodPressure measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredBloodPressureMeasurement(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_WEIGHT:
                            {
                                MeasurementManuallyEnteredWeight measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredWeightMeasurement(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                            {
                                MeasurementConsciousnessLevel measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredConsciousnessLevelMeasurement(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                            {
                                MeasurementSupplementalOxygenLevel measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredSupplementalOxygenLevelMeasurement(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_ANNOTATION:
                            {
                                MeasurementAnnotation measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredAnnotation(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                            {
                                MeasurementCapillaryRefillTime measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredCapillaryRefillTime(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                            {
                                MeasurementRespirationDistress measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredRespirationDistress(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                            {
                                MeasurementFamilyOrNurseConcern measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredFamilyOrNurseConcern(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;

                            case MANUALLY_ENTERED_URINE_OUTPUT:
                            {
                                MeasurementUrineOutput measurement = handleManuallyEnteredDataPoint(intent, device_type, by_user_id);

                                local_database_storage.storeManuallyEnteredUrineOutput(patient_session_info.android_database_patient_session_id, measurement, by_user_id);
                            }
                            break;
                        }
                    }
                    break;

                    case CMD_REPORT_PATIENT_ORIENTATION:
                    {
                        patient_gateway_outgoing_commands.reportPatientOrientation(last_patient_orientation, last_patient_orientation_timestamp);
                    }
                    break;

                    case CMD_SET_LOG_CAT_MESSAGES:
                    {
                        boolean is_enabled = getEnabledFromIntent(intent);
                        setLogCatFragmentStatus(is_enabled);
                    }
                    break;

                    case CMD_STOP_RUNNING_BLUETOOTH_SCAN:
                    {
                        Log.d(TAG, "CMD_STOP_RUNNING_BLUETOOTH_SCAN");

                        stopOnGoingBluetoothScan();
                    }
                    break;

                    case CMD_RECONNECT_WIFI:
                    {
                        Log.d(TAG,"CMD_RECONNECT_WIFI : starting wifi reconnection");

                        forceResetWifi();
                    }
                    break;

                    case CMD_SET_RUN_DEVICES_IN_TEST_MODE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        Log.d(TAG,"CMD_SET_RUN_DEVICES_IN_TEST_MODE_ENABLE_STATUS : setting test mode status = " + enabled);

                        runDevicesInTestMode(enabled);
                    }
                    break;

                    case CMD_GET_RUN_DEVICES_IN_TEST_MODE:
                    {
                        patient_gateway_outgoing_commands.reportRunDevicesInTestMode(gateway_settings.getRunDevicesInTestMode());
                    }
                    break;

                    case CMD_SET_MANUAL_VITAL_SIGNS_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        Log.d(TAG,"CMD_SET_MANUAL_VITAL_SIGNS_ENABLED_STATUS : setting manual vital signs enabled = " + enabled);

                        enableManualVitalSigns(enabled);
                    }
                    break;

                    case CMD_GET_MANUAL_VITAL_SIGNS_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportManualVitalSignsEnabledStatus(gateway_settings.getEnableManualVitalSignsEntry());
                    }
                    break;

                    case CMD_SET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES:
                    {
                        SensorType sensorType = getSensorTypeFromIntent(intent);
                        int measurement_timeout = intent.getIntExtra("timeout", 60);

                        setLongTimeMeasurementTimeoutInMinutes(sensorType, measurement_timeout);
                    }
                    break;

                    case CMD_GET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES:
                    {
                        SensorType sensorType = getSensorTypeFromIntent(intent);

                        int timeout = 0;
                        switch (sensorType)
                        {
                            case SENSOR_TYPE__SPO2:
                            {
                                timeout = gateway_settings.getSpO2LongTermMeasurementTimeoutInMinutes();
                            }
                            break;

                            case SENSOR_TYPE__BLOOD_PRESSURE:
                            {
                                timeout = gateway_settings.getBloodPressureLongTermMeasurementTimeoutInMinutes();
                            }
                            break;

                            case SENSOR_TYPE__WEIGHT_SCALE:
                            {
                                timeout = gateway_settings.getWeightLongTermMeasurementTimeoutInMinutes();
                            }
                            break;

                            case SENSOR_TYPE__TEMPERATURE:
                            {
                                timeout = gateway_settings.getThirdPartyTemperatureLongTermMeasurementTimeoutInMinutes();
                            }
                            break;
                        }

                        patient_gateway_outgoing_commands.reportLongTermMeasurementTimeoutInMinutes(sensorType, timeout);
                    }
                    break;

                    case CMD_SET_UNPLUGGED_OVERLAY_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        Log.d(TAG,"CMD_SET_UNPLUGGED_OVERLAY_ENABLED_STATUS : enabled = " + enabled);

                        enableUnpluggedOverlay(enabled);
                    }
                    break;

                    case CMD_GET_UNPLUGGED_OVERLAY_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportUnpluggedOverlayEnabledStatus(gateway_settings.getUnpluggedOverlayEnabledStatus());
                    }
                    break;

                    case CMD_SET_LT3_KHZ_SETUP_MODE_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);
                        Log.d(TAG, "CMD_SET_LT3_KHZ_SETUP_MODE_ENABLED_STATUS : enabled = " + enabled);
                        gateway_settings.storeLT3KHzSetupModeEnableStatus(enabled);
                    }
                    break;

                    case CMD_GET_LT3_KHZ_SETUP_MODE_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportLT3KHzSetupModeEnabledStatus(gateway_settings.getLT3KHzSetupModeEnabledStatus());
                    }
                    break;

                    case CMD_SET_AUTO_ENABLE_EWS__ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        Log.d(TAG,"CMD_SET_AUTO_ENABLE_EWS__ENABLED_STATUS : enabled = " + enabled);

                        enableAutoEnableEws(enabled);
                    }
                    break;

                    case CMD_GET_AUTO_ENABLE_EWS__ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportAutoEnableEwsEnabledStatus(gateway_settings.getAutoEnableEwsEnabledStatus());
                    }
                    break;

                    case CMD_SET_DUMMY_DATA_MODE_BACKFILL_SESSION_WITH_DATA_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        Log.e(TAG, "CMD_SET_DUMMY_DATA_MODE_BACKFILL_SESSION_WITH_DATA_ENABLED_STATUS : " + enabled);

                        dummy_data_instance.enableBackfill(enabled);
                    }
                    break;

                    case CMD_SET_DUMMY_DATA_MODE_BACKFILL_HOURS:
                    {
                        int hours = intent.getIntExtra("hours", 0);
                        Log.e(TAG, "CMD_SET_DUMMY_DATA_MODE_BACKFILL_HOURS = " + hours);

                        dummy_data_instance.setNumberOfHoursToBackfill(hours);
                    }
                    break;

                    case CMD_VALIDATE_DATA_MATRIX_BARCODE:
                    {
                        String contents = intent.getStringExtra("contents");
                        contents = contents.replaceAll("[^A-Za-z0-9]", "");                             // Remove all non alphanumeric characters from the string

                    /*
                    'contents' looks like the following for an Isansys datamatrix barcode
                    015060488680076810B0000111601011716040121B0000240d0b9cbac15f73897fd0d2d734140f7

                    This breaks down as below

                    Datatype    Data
                    01          50604886800768                  GTIN (Global Trade Identifier Number)
                    10          B0000                           Lot Number
                    11          160101                          Manufacture Date. YYMMDD
                    17          160401                          Expiration Date. YYMMDD
                    21          B0000                           Serial Number
                    240         d0b9cbac15f73897fd0d2d734140f7  "QR Code" Contents. This is the 32 bytes of cipher text (not the 20 byte Ascii85 string) from the original QR codes.
                                                                This is encrypted so allows us to check if this datamatrix is an Isansys generated one or not
                    */

                        if (contents.length() == 81)
                        {
                            String GTIN = contents.substring(2, 16);
                            String lot_number = contents.substring(18, 23);
                            String manufacture_date = contents.substring(25, 31);
                            String expiration_date = contents.substring(33, 39);
                            String serial_number = contents.substring(41, 46);
                            String isansys_specific_info = contents.substring(49);

                            Log.d(TAG, "GTIN = " + GTIN + " : lot_number = " + lot_number + " : manufacture_date = " + manufacture_date + " : expiration_date = " + expiration_date + " : serial_number = " + serial_number);

                            byte[] ciphertext = Utils.hexStringToByteArray(isansys_specific_info);
                            byte[] aes_key_byte_array = Utils.hexStringToByteArray("000102030405060708090a0b0c0d0e0f");
                            byte[] plaintext = null;

                            try
                            {
                                // Decrypt the cipher text to get the plaintext
                                plaintext = AES.decrypt(ciphertext, aes_key_byte_array);
                            }
                            catch (Exception e)
                            {
                                Log.d("Error", e.toString());
                            }

                            Intent outgoing_intent = new Intent(patient_gateway_outgoing_commands.INTENT__COMMANDS_TO_USER_INTERFACE);
                            outgoing_intent.putExtra("command", Commands.CMD_REPORT_DATA_MATRIX_VALIDATION_RESULT.ordinal());

                            if (plaintext != null)
                            {
                                Log.d(TAG, "Hex Decryption : " + Utils.byteArrayToHexString(plaintext));

                                if ((plaintext[0] == 'I') && (plaintext[1] == 'S') && (plaintext[2] == 'A'))
                                {
                                    // Then its a valid Isansys product QR code. Start processing from index 3 onwards
                                    int i;
                                    int j = 3;

                                    // Bytes 3, 4 and 5 are the Human Readable Product ID. E.g. Lifetouch 234
                                    long human_readable_product_id = 0;
                                    for(i = 0; i<3; i++)
                                    {
                                        int next_byte = (int)plaintext[j++] & 0xFF;
                                        human_readable_product_id *= 256;
                                        human_readable_product_id += next_byte;
                                    }
                                    Log.d(TAG, "Human Readable Product ID = " + human_readable_product_id);
                                    outgoing_intent.putExtra("human_readable_product_id", human_readable_product_id);

                                    // Bytes 6, 7, 8, 9, 10 and 11 are the Bluetooth Address
                                    StringBuilder bluetooth_device_address_string_builder = new StringBuilder();
                                    for (i=0; i<6; i++)
                                    {
                                        String value_as_hex_string = String.format("%02X:", plaintext[j++]);
                                        bluetooth_device_address_string_builder.append(value_as_hex_string);
                                    }
                                    // Remove the trailing ":"
                                    bluetooth_device_address_string_builder.deleteCharAt(bluetooth_device_address_string_builder.length() - 1);

                                    String bluetooth_device_address = bluetooth_device_address_string_builder.toString();
                                    Log.d(TAG, "Bluetooth Device Address = " + bluetooth_device_address);
                                    outgoing_intent.putExtra("bluetooth_device_address", bluetooth_device_address);


                                    // Byte 12 is the Device Type
                                    int device_type = plaintext[j++];
                                    Log.d(TAG, "Device Type = " + device_type);
                                    outgoing_intent.putExtra("device_type", device_type);


                                    // Bytes 13, 14 and 15 are currently spare
                                    StringBuilder spare_bytes_string_builder = new StringBuilder();
                                    for (i=0; i<3; i++)
                                    {
                                        spare_bytes_string_builder.append((char)plaintext[j++]);
                                    }
                                    Log.d(TAG, "Spare Bytes = " + spare_bytes_string_builder);

                                    outgoing_intent.putExtra("validity", true);

                                    outgoing_intent.putExtra("GTIN", GTIN);
                                    outgoing_intent.putExtra("lot_number", lot_number);
                                    outgoing_intent.putExtra("manufacture_date", manufacture_date);
                                    outgoing_intent.putExtra("expiration_date", expiration_date);
                                    outgoing_intent.putExtra("serial_number", serial_number);
                                }
                                else
                                {
                                    Log.d(TAG, "Non Isansys Datamatrix");
                                    outgoing_intent.putExtra("validity", false);
                                }
                            }
                            else
                            {
                                Log.d(TAG, "Non Isansys Datamatrix");
                                outgoing_intent.putExtra("validity", false);
                            }

                            sendBroadcast(outgoing_intent);
                        }
                    }
                    break;

                    case CMD_GET_DATABASE_STATUS_FOR_POPUP_SERVER_STATUS:
                    {
                        Log.d(TAG, "CMD_GET_DATABASE_STATUS_FOR_POPUP_SERVER_STATUS");

                        getDatabaseStatus();
                    }
                    break;

                    case CMD_SET_MANUFACTURING_MODE_ENABLED_STATUS:
                    {
                        boolean is_enabled = getEnabledFromIntent(intent);

                        gateway_settings.storeManufacturingModeEnabledStatus(is_enabled);

                        patient_gateway_outgoing_commands.reportManufacturingModeEnabledStatus(gateway_settings.getManufacturingModeEnabledStatus());

                        reportFeaturesEnabledToServer();
                    }
                    break;

                    case CMD_GET_MANUFACTURING_MODE_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportManufacturingModeEnabledStatus(gateway_settings.getManufacturingModeEnabledStatus());
                    }
                    break;

                    case CMD_SET_SIMPLE_HEART_RATE_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        enableSimpleHeartRateAlgorithm(enabled);
                    }
                    break;

                    case CMD_GET_SIMPLE_HEART_RATE_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportSimpleHeartRateEnabledStatus(gateway_settings.getSimpleHeartRateEnabledStatus());
                    }
                    break;

                    case CMD_SET_GSM_MODE_ONLY_ENABLED_STATUS:
                    {
                        boolean gsm_mode_only = intent.getBooleanExtra("gsm_mode_only", false);

                        setGsmModeOnlyEnabledStatus(gsm_mode_only);
                    }
                    break;

                    case CMD_GET_GSM_MODE_ONLY_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportGsmModeOnlyEnabledStatus(inGsmOnlyMode());
                    }
                    break;

                    case CMD_GET_SWEETBLUE_DIAGNOSTICS:
                    {
                        patient_gateway_outgoing_commands.reportSweetBlueDiagnostics(patient_session_info.number_of_times_postCompleteBleReset_called, patient_session_info.number_of_times_postBleResetWithoutRemovingDevices_called);
                    }
                    break;

                    case CMD_SET_USE_BACK_CAMERA_ENABLED_STATUS:
                    {
                        boolean is_enabled = getEnabledFromIntent(intent);

                        gateway_settings.storeUseBackCameraEnabledStatus(is_enabled);

                        patient_gateway_outgoing_commands.reportUseBackCameraEnabledStatus(gateway_settings.getUseBackCameraEnabledStatus());

                        reportFeaturesEnabledToServer();
                    }
                    break;

                    case CMD_GET_USE_BACK_CAMERA_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportUseBackCameraEnabledStatus(gateway_settings.getUseBackCameraEnabledStatus());
                    }
                    break;

                    case CMD_SET_PATIENT_ORIENTATION_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        enablePatientOrientation(enabled);
                    }
                    break;

                    case CMD_GET_PATIENT_ORIENTATION_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportPatientOrientationEnabledStatus(gateway_settings.getEnablePatientOrientation());
                    }
                    break;

                    case CMD_SET_AUTO_UPLOAD_LOG_FILES_TO_SERVER_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        enableAutoUploadLogsToServer(enabled);
                    }
                    break;

                    case CMD_GET_AUTO_UPLOAD_LOG_FILES_TO_SERVER_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportEnableAutoUploadOfLogsFilesToServer(gateway_settings.getEnableAutoUploadLogsToServer());
                    }
                    break;

                    case CMD_SET_WIFI_LOGGING_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        wifiLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_GET_WIFI_LOGGING_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportWifiLoggingEnabledStatus(gateway_settings.getEnableWifiLogging());
                    }
                    break;

                    case CMD_SET_GSM_LOGGING_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        gsmLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_GET_GSM_LOGGING_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportGsmLoggingEnabledStatus(gateway_settings.getEnableGsmLogging());
                    }
                    break;

                    case CMD_SET_DATABASE_LOGGING_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        databaseLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_GET_DATABASE_LOGGING_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportDatabaseLoggingEnabledStatus(gateway_settings.getEnableDatabaseLogging());
                    }
                    break;

                    case CMD_SET_SERVER_LOGGING_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        serverLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_GET_SERVER_LOGGING_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportServerLoggingEnabledStatus(gateway_settings.getEnableServerLogging());
                    }
                    break;

                    case CMD_SET_BATTERY_LOGGING_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        batteryLoggingEnabled(enabled);
                    }
                    break;

                    case CMD_GET_BATTERY_LOGGING_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportBatteryLoggingEnabledStatus(gateway_settings.getEnableBatteryLogging());
                    }
                    break;

                    case CMD_SET_DFU_BOOTLOADER_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        dfuBootloaderEnabled(enabled);
                    }
                    break;

                    case CMD_GET_DFU_BOOTLOADER_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportDfuBootloaderEnabledStatus(gateway_settings.getEnableDfuBootloader());
                    }
                    break;

                    case CMD_SET_SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        spO2SpotMeasurementsEnabled(enabled);
                    }
                    break;

                    case CMD_GET_SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportSpO2SpotMeasurementsEnabledStatus(gateway_settings.getEnableSpO2SpotMeasurements());
                    }
                    break;

                    case CMD_SET_PREDEFINED_ANNOTATION_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        predefineAnnotationsEnabled(enabled);
                    }
                    break;

                    case CMD_GET_PREDEFINED_ANNOTATION_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportPredefinedAnnotationEnabledStatus(gateway_settings.getEnablePredefineAnnotations());
                    }
                    break;

                    case CMD_SET_SHOW_NUMBERS_ON_BATTERY_INDICATOR_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showNumbersOnBatteryIndicators(enabled);
                    }
                    break;

                    case CMD_GET_SHOW_NUMBERS_ON_BATTERY_INDICATOR_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportShowNumbersOfBatteryIndicatorEnabledStatus(gateway_settings.getShowNumbersOnBatteryIndicator());
                    }
                    break;

                    case CMD_SET_SHOW_MAC_ADDRESS_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showMacAddress(enabled);
                    }
                    break;

                    case CMD_GET_SHOW_MAC_ADDRESS_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportShowMacAddressEnabledStatus(gateway_settings.getShowMacAddressOnSettingsPage());
                    }
                    break;

                    case CMD_SET_USA_MODE_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        enableUsaOnlyMode(enabled);
                    }
                    break;

                    case CMD_GET_USA_MODE_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportUsaModeEnabledStatus(gateway_settings.getUsaOnlyMode());
                    }
                    break;

                    case CMD_SET_SHOW_LIFETOUCH_ACTIVITY_LEVEL_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        showLifetouchActivityLevel(enabled);
                    }
                    break;

                    case CMD_GET_SHOW_LIFETOUCH_ACTIVITY_LEVEL_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportShowLifetouchActivityLevelEnabledStatus(gateway_settings.getShowLifetouchActivityLevel());
                    }
                    break;

                    case CMD_SET_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS:
                    {
                        int measurement_interval_in_seconds = intent.getIntExtra("measurement_interval_in_seconds", 60);

                        setLifetempMeasurementIntervalInSeconds(measurement_interval_in_seconds);
                    }
                    break;

                    case CMD_GET_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS:
                    {
                        patient_gateway_outgoing_commands.reportLifetempMeasurementInterval(gateway_settings.getLifetempMeasurementInterval());
                    }
                    break;

                    case CMD_RESTART_INSTALLATION_WIZARD:
                    {
                        if (isPatientSessionRunning() == false)
                        {
                            gateway_settings.resetToDefaults();

                            // Reset Gateway address here to use the defaults
                            setupServerAddressFromSettings();

                            setup_wizard.setNotInstalled();

                            emptyLocalDatabaseIncludingEwsThresholdSets();

                            // This will set the UI bed details to NOT_SET_YET
                            reportGatewayBedDetails();
                        }
                    }
                    break;

                    case CMD_SET_DEVELOPER_POPUP_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        storeDeveloperPopupEnabledStatus(enabled);
                    }
                    break;

                    case CMD_GET_DEVELOPER_POPUP_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportDeveloperPopupEnabled(gateway_settings.getDeveloperPopupEnabled());
                    }
                    break;

                    case CMD_SET_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS:
                    {
                        int measurement_interval_in_seconds = intent.getIntExtra("measurement_interval_in_seconds", 60);

                        storePatientOrientationMeasurementInterval(measurement_interval_in_seconds);
                    }
                    break;

                    case CMD_GET_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS:
                    {
                        patient_gateway_outgoing_commands.reportPatientOrientationMeasurementInterval(gateway_settings.getPatientOrientationMeasurementIntervalInSeconds());
                    }
                    break;

                    case CMD_SET_SHOW_IP_ADDRESS_ON_WIFI_POPUP_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        setShowIpAddressOnWifiPopupEnabledStatus(enabled);
                    }
                    break;

                    case CMD_GET_SHOW_IP_ADDRESS_ON_WIFI_POPUP_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportShowIpAddressOnWifiPopupEnabled(gateway_settings.getShowIpAddressOnWifiPopupEnabled());
                    }
                    break;

                    case CMD_GET_DEVICE_OPERATING_MODE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);
                        patient_gateway_outgoing_commands.sendCommandReportDeviceOperatingMode(device_info);
                    }
                    break;

                    case CMD_CLEAR_DESIRED_DEVICE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);
                        clearDesiredDevice(device_info);
                    }
                    break;

                    case CMD_GET_DEVICE_INFO:
                    {
                        reportAllDeviceInfos();
                    }
                    break;

                    case CMD_RETRY_CONNECTING_TO_DEVICE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);
                        retryConnectingToDevice(device_info);
                    }
                    break;

                    case CMD_DISCONNECT_FROM_DESIRED_DEVICE:
                    {
                        DeviceInfo device_info = getDeviceInfoFromIntent(intent);

                        int device_session_ended_by_user_id = intent.getIntExtra("device_session_ended_by_user_id", -1);
                        boolean turn_off = intent.getBooleanExtra("turn_off", true);

                        disconnectFromDesiredDevice(device_info, device_session_ended_by_user_id, turn_off);
                    }
                    break;

                    case CMD_SET_AUTO_RESUME_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        enableSessionAutoResume(enabled);
                    }
                    break;

                    case CMD_GET_AUTO_RESUME_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportAutoResumeStatus(gateway_settings.getAutoResumeEnabled());
                    }
                    break;

                    case CMD_FORCE_CHECK_FOR_LATEST_FIRMWARE_IMAGES_FROM_SERVER:
                    {
                        server_syncing.sendGetLatestDeviceFirmwareVersionsRequest();
                    }
                    break;

                    case CMD_GET_INSTALLATION_COMPLETE:
                    {
                        reportInstallationCompleteFlag();
                    }
                    break;

                    case CMD_SET_INSTALLATION_COMPLETE:
                    {
                        gateway_settings.storeInstallationComplete(true);
                        reportInstallationCompleteFlag();
                        reportGatewayBedDetails(); //ToDo: why is this here?
                    }
                    break;

                    case CMD_RESET_DATABASE_FAILED_TO_SEND_STATUS:
                    {
                        server_syncing.resetDatabaseFailedToSendStatusAndResetTimer();

                        getDatabaseStatus();

                        showOnScreenMessage("Failed to Send status reset");
                    }
                    break;

                    case CMD_GET_LOCATION_ENABLED:
                    {
                        boolean enabled = device_info_manager.getBluetoothLeDeviceController().checkLocationEnabled();

                        patient_gateway_outgoing_commands.sendCommandReportLocationEnabled(enabled);
                    }
                    break;

                    case CMD_TEST_ONLY_DELETE_EWS_THRESHOLDS:
                    {
                        emptyLocalDatabase(DELETE_EWS_THRESHOLDS);

                        // now query the thresholds to trigger an auto re-sync.
                        queryThresholdSets();
                    }
                    break;

                    case CMD_GET_REALTIME_SERVER_TYPE:
                    {
                        RealTimeServer server_type = gateway_settings.getRealTimeServerType();

                        patient_gateway_outgoing_commands.sendCommandReportRealTimeServerType(server_type);
                    }
                    break;

                    case CMD_SET_REALTIME_SERVER_TYPE:
                    {
                        int type = intent.getIntExtra("realtime_server_type", RealTimeServer.INVALID.ordinal());

                        RealTimeServer server_type = RealTimeServer.values()[type];

                        gateway_settings.setRealTimeServerType(server_type);

                        server_syncing.configureRealTimeServerIfRequired();

                        patient_gateway_outgoing_commands.sendCommandReportRealTimeServerType(gateway_settings.getRealTimeServerType());
                    }
                    break;

                    case CMD_REPORT_NFC_TAG:
                    {
                        Bundle extras = intent.getBundleExtra("bundle");
                        byte[] payload = extras.getByteArray("payload");

                        Log.d(TAG, "CMD_REPORT_NFC_TAG : Payload = " + Utils.byteArrayToHexString(payload));

                        byte[] aes_key_byte_array = Utils.hexStringToByteArray("000102030405060708090a0b0c0d0e0f");

                        byte[] plaintext = decryptQrCode(payload, aes_key_byte_array);

                        QrCodeData qr_info = new QrCodeData(plaintext, Log);

                        patient_gateway_outgoing_commands.reportQrCodeOrNfcDetailsToUserInterface(qr_info);
                    }
                    break;

                    case CMD_CRASH_PATIENT_GATEWAY_ON_DEMAND:
                    {
                        int crash_on_demand;

                        // Below stops Android Lint complaining. This code is here on purpose to test out how the app handles a crash
                        //noinspection divzero,NumericOverflow
                        crash_on_demand = 1/0;

                        Log.e(TAG, "Will never get here but stops Android Lint complaining about unused var : " + crash_on_demand);
                    }
                    break;

                    case CMD_START_NONIN_PLAYBACK_SIMULATION_FROM_FILE:
                    {
                        Log.d(TAG, "Starting playback simulation from file");

                        Intent startPlaybackSimulationIntent = new Intent();
                        startPlaybackSimulationIntent.setAction(NoninWristOxPlaybackDecoder.NONIN_WRIST_OX__START_PLAYBACK_SIMULATION_FROM_FILE);
                        sendBroadcastIntent(startPlaybackSimulationIntent);
                    }
                    break;

                    case CMD_GET_LAST_NETWORK_STATUS:
                    {
                        if (inGsmOnlyMode())
                        {
                            // In GSM mode
                            if (last_gsm_status != null)
                            {
                                patient_gateway_outgoing_commands.reportGsmStatusToUserInterface(last_gsm_status, ActiveNetworkTypes.MOBILE);
                            }
                            else
                            {
                                Log.e(TAG, "In GSM mode but last_gsm_status is NULL");
                            }
                        }
                        else
                        {
                            // In Wifi mode
                            if (last_wifi_status != null)
                            {
                                patient_gateway_outgoing_commands.reportWifiStatusToUserInterface(last_wifi_status, ActiveNetworkTypes.WIFI);
                            }
                            else
                            {
                                Log.e(TAG, "In WIFI mode but last_wifi_status is NULL");
                            }
                        }
                    }
                    break;

                    case CMD_GET_REALTIME_SERVER_CONNECTED_STATUS:
                    {
                        patient_gateway_outgoing_commands.sendCommandReportConnectedToServerStatus(server_syncing.isRealTimeServerConnected());
                    }
                    break;

                    case CMD_ENTER_SOFTWARE_UPDATE_MODE:
                    {
                        Log.d(TAG, "Entering update mode");

                        enterSoftwareUpdateMode();

                        patient_gateway_outgoing_commands.reportInSoftwareUpdateMode();
                    }
                    break;

                    case CMD_GET_SOFTWARE_UPDATE_STATE:
                    {
                        reportAvailableUpdateVersions();
                    }
                    break;

                    case CMD_START_SERVER_DATA_DOWNLOAD:
                    {
                        if (needToGetNewServerData())
                        {
                            startGetServerSettingsDueToSoftwareUpdate();
                        }
                    }
                    break;

                    case CMD_FORCE_SERVER_DATA_DOWNLOAD:
                    {
                        startGetServerSettingsDueToSoftwareUpdate();
                    }
                    break;

                    case CMD_SERVER_SYNC_DATA_IN_TEST_MODE:
                    {
                        boolean run_in_test_mode = intent.getBooleanExtra("test_mode", false);

                        server_syncing.runInTestMode(run_in_test_mode);
                    }
                    break;

                    case CMD_REPORT_GATEWAY_VIDEO_CALL_STATUS:
                    {
                        String connection_id = intent.getStringExtra("connection_id");
                        VideoCallStatus videoCallStatus = VideoCallStatus.values()[intent.getIntExtra("videoCallStatus", VideoCallStatus.VIDEO_CALL_STATUS__INVALID.ordinal())];

                        //  Now tell the Server
                        Log.d(TAG, "CMD_REPORT_GATEWAY_VIDEO_CALL_STATUS : " + videoCallStatus);

                        server_syncing.sendGatewayVideoCallStatusToServer(connection_id, videoCallStatus, gateway_settings.getGatewaysAssignedBedId(), getNtpTimeNowInMilliseconds());
                    }
                    break;

                    case CMD_SET_VIDEO_CALLS_ENABLED_STATUS:
                    {
                        boolean enabled = intent.getBooleanExtra("enabled", false);

                        enableVideoCalls(enabled);
                    }
                    break;

                    case CMD_GET_VIDEO_CALLS_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportVideoCallsEnabledStatus(gateway_settings.getVideoCallsEnabledStatus());
                    }
                    break;

                    case CMD_REQUEST_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_FROM_SERVER:
                    {
                        int serversPatientSessionId = patient_session_info.server_patient_session_id;
                        String bedId = gateway_settings.getGatewaysAssignedBedId();
                        String uniqueId = bedId + "-" + getNtpTimeNowInMilliseconds();

                        server_syncing.requestPatientSpecificVideoCallContactsIfServerConnected(serversPatientSessionId, bedId, uniqueId);
/*
                    // TEST CODE
                    ArrayList<VideoCallContact> contacts = new ArrayList<>();

                    int lifeguard_user_id = 0;
                    contacts.add(new VideoCallContact(lifeguard_user_id++, "Mat", true));
                    contacts.add(new VideoCallContact(lifeguard_user_id++, "Keith", true));
                    contacts.add(new VideoCallContact(lifeguard_user_id++, "Lea", false));

                    patient_gateway_outgoing_commands.reportPatientSpecificVideoCallContactsReceivedFromServer(true, contacts);
*/
                    }
                    break;

                    case CMD_PATIENT_ON_GATEWAY_REQUESTING_VIDEO_CALL:
                    {
                        ArrayList<VideoCallContact> contacts = intent.getParcelableArrayListExtra("contact"); // intent.getParcelableExtra("contact");

                        sendPatientInitedVideoCallRequestToServer(VideoCallRequest.REQUEST_CALL, contacts);
                    }
                    break;

                    case CMD_PATIENT_ON_GATEWAY_CANCELLED_VIDEO_CALL_REQUEST:
                    {
                        sendPatientInitedVideoCallRequestToServer(VideoCallRequest.CANCEL_CALL_REQUEST, null);
                    }
                    break;

                    case CMD_DUMMY_QR_CODE_UNLOCK_USER:
                    {
                        emulateQrCodeUnlockUser(UnlockCodeSource.FROM_USER);
                    }
                    break;

                    case CMD_DUMMY_QR_CODE_UNLOCK_ADMIN:
                    {
                        emulateQrCodeUnlockAdmin(UnlockCodeSource.FROM_USER);
                    }
                    break;

                    case CMD_SET_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        displayTemperatureInFahrenheit(enabled);
                    }
                    break;

                    case CMD_GET_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportDisplayTemperatureInFahrenheitEnabledStatus(gateway_settings.getDisplayTemperatureInFahrenheitEnabledStatus());
                    }
                    break;

                    case CMD_SET_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        displayWeightInLbs(enabled);
                    }
                    break;

                    case CMD_GET_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportDisplayWeightInLbsEnabledStatus(gateway_settings.getDisplayWeightInLbsEnabledStatus());
                    }
                    break;

                    case CMD_GET_FREE_DISK_SPACE:
                    {
                        patient_gateway_outgoing_commands.reportFreeDiskSpace(diskUsage.getFreeDiskSpace());
                    }
                    break;

                    case CMD_RECALCULATE_THRESHOLDS_AFTER_SERVER_CONFIG_RECEIVED:
                    {
                        patient_gateway_outgoing_commands.recalculateThresholdsFollowingUpdatedServerConfig();
                    }
                    break;
                    
                    case CMD_SET_VIEW_WEBPAGES_ENABLED_STATUS:
                    {
                        boolean enabled = intent.getBooleanExtra("enabled", false);

                        enableViewWebPages(enabled);
                    }
                    break;

                    case CMD_GET_VIEW_WEBPAGES_ENABLED_STATUS:
                    {
                        patient_gateway_outgoing_commands.reportViewWebPagesEnabledStatus(gateway_settings.getViewWebPagesEnabledStatus());
                    }
                    break;

                    case CMD_REMOVE_VITAL_SIGN_TYPE_FROM_EWS:
                    {
                        VitalSignType vital_sign_type = getVitalSignTypeFromIntent(intent);

                        long_term_measurement_validity_tracker.removeVitalSignFromTrackedMeasurementsOnceItsExpired(vital_sign_type);
                    }
                    break;

                    default:
                    {
                        Log.d(TAG, "Unknown Command : " + incoming_command + " : " + command);
                    }
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "EXCEPTION. command byte = " + command + " : " + e);
            }
        }
    };


    private void checkForInstallationQrCodeTextFile()
    {
        Log.w(TAG, "checkForInstallationQrCodeTextFile");

        try
        {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canRead())
            {
                Log.w(TAG, "checkForInstallationQrCodeTextFile : Can read External Storage Directory");

                String filename = sd + "/installation.txt";

                File file = new File(filename);
                if (file.exists())
                {
                    Log.w(TAG, "checkForInstallationQrCodeTextFile : File exists");

                    StringBuilder text = new StringBuilder();

                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null)
                    {
                        text.append(line);
                    }
                    br.close();

                    file.delete();

                    Log.d(TAG, "checkForInstallationQrCodeTextFile : Contents = " + text);

                    if (text.length() > 0)
                    {
                        validateInstallationModeQrCodeContents(text.toString());
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "checkForInstallationQrCodeTextFile : EXCEPTION : " + e);
        }
    }


    private void validateInstallationModeQrCodeContents(String qr_code_contents)
    {
        byte[] aes_key_byte_array = Utils.hexStringToByteArray("642110d0d49bfafd2634fc3f6320df5b");

        byte[] plaintext = decryptQrCode(qr_code_contents, aes_key_byte_array);

        Intent outgoing_intent = new Intent(patient_gateway_outgoing_commands.INTENT__COMMANDS_TO_USER_INTERFACE);
        outgoing_intent.putExtra("command", Commands.CMD_REPORT_INSTALLATION_QR_CODE_DETAILS.ordinal());

        if (plaintext != null)
        {
            Log.d("Hex Decryption", Utils.byteArrayToHexString(plaintext));

            boolean isIpAddressInstallationQrCode = (plaintext[0] == 'I') && (plaintext[1] == 'S') && (plaintext[2] == 'A');
            boolean isDomainInstallationQrCode = (plaintext[0] == 'L') && (plaintext[1] == 'I') && (plaintext[2] == 'F');

            if (isIpAddressInstallationQrCode || isDomainInstallationQrCode)
            {
                String serverAddress;
                int webservicePort;
                int realTimePort;
                boolean useMqtt;
                boolean useHttps;

                if (isIpAddressInstallationQrCode)
                {
                    // Then its a valid (IP address) Installation Mode QR code
                    int i;
                    int j = 3;

                    // Read in and create the Servers IP address
                    StringBuilder stringBuilder = new StringBuilder();
                    for (i=0; i<3; i++)
                    {
                        stringBuilder.append(plaintext[j++] & 0xFF);
                        stringBuilder.append(".");
                    }
                    stringBuilder.append(plaintext[j++] & 0xFF);

                    serverAddress = stringBuilder.toString();
                    Log.e(TAG, "IP Address Installation QR code : serverAddress = " + serverAddress);

                    webservicePort = 0;
                    for(i = 0; i<2; i++)
                    {
                        int nextByte = (int)plaintext[j++] & 0xFF;
                        webservicePort *= 256;
                        webservicePort += nextByte;
                    }
                    Log.e(TAG, "IP Address Installation QR code : webservicePort = " + webservicePort);

                    realTimePort = 0;
                    for(i = 0; i<2; i++)
                    {
                        int nextByte = (int)plaintext[j++] & 0xFF;
                        realTimePort *= 256;
                        realTimePort += nextByte;
                    }
                    Log.e(TAG, "IP Address Installation QR code : realTimePort = " + realTimePort);

                    int flags = (int)plaintext[j++] & 0xFF;
                    Log.e(TAG, "IP Address Installation QR code : flags = " + flags);

                    useMqtt = false;
                    if ((flags & 0x01) > 0)
                    {
                        useMqtt = true;
                    }
                    Log.e(TAG, "IP Address Installation QR code : useMqtt = " + useMqtt);

                    useHttps = false;
                    if ((flags & 0x02) > 0)
                    {
                        useHttps = true;
                    }
                    Log.e(TAG, "IP Address Installation QR code : useHttps = " + useHttps);
                }
                else
                {
                    // Then it is a Domain Name Installation QR code

                    String plainTextString = new String(plaintext, StandardCharsets.UTF_8);

                    // Skip past the LIF bytes
                    plainTextString = plainTextString.substring(3);

                    // Remove any NULL chars from the end as the AES decryption will probably leave some there as padding
                    plainTextString = plainTextString.replace("\0", "");

                    Log.d(TAG, "Domain Name Installation QR code : " + plainTextString);

                    JsonObject jsonObject = JsonParser.parseString(plainTextString).getAsJsonObject();

                    serverAddress = jsonObject.get("dn").getAsString();
                    Log.d(TAG, "Domain Name Installation QR code : serverAddress = " + serverAddress);

                    webservicePort = jsonObject.get("ws").getAsInt();
                    Log.d(TAG, "Domain Name Installation QR code : webservicePort = " + webservicePort);

                    realTimePort = jsonObject.get("rt").getAsInt();
                    Log.d(TAG, "Domain Name Installation QR code : realTimePort = " + realTimePort);

                    useMqtt = jsonObject.get("mqtt").getAsInt() == 1;
                    Log.d(TAG, "Domain Name Installation QR code : useMqtt = " + useMqtt);

                    useHttps = jsonObject.get("https").getAsInt() == 1;
                    Log.d(TAG, "Domain Name Installation QR code : useHttps = " + useHttps);
                }

                if (useMqtt)
                {
                    gateway_settings.setRealTimeServerType(RealTimeServer.MQTT);
                }
                else
                {
                    gateway_settings.setRealTimeServerType(RealTimeServer.WAMP);
                }

                // Store Server address, real time port and server port...
                gateway_settings.storeServerAddress(serverAddress);
                gateway_settings.storeServerPort(Integer.toString(webservicePort));
                gateway_settings.storeRealTimeServerPort(Integer.toString(realTimePort));
                gateway_settings.storeHttpsEnableStatus(useHttps);

                serverAddress = gateway_settings.getServerAddress();
                String string_server_port = gateway_settings.getServerPort();
                String string_real_time_port = gateway_settings.getRealTimeServerPort();

                // Pass the Server address to classes that need it
                server_syncing.useHttps(gateway_settings.getHttpsEnabledStatus());
                server_syncing.setIsansysServerAddress(serverAddress, string_server_port);

                patient_gateway_outgoing_commands.reportServerAddress(serverAddress);
                patient_gateway_outgoing_commands.reportServerPort(string_server_port);
                patient_gateway_outgoing_commands.reportHttpsEnabledStatus(gateway_settings.getHttpsEnabledStatus());

                patient_gateway_outgoing_commands.sendCommandReportRealTimeServerType(gateway_settings.getRealTimeServerType());
                patient_gateway_outgoing_commands.reportRealTimeServerPort(string_real_time_port);

                if (useMqtt)
                {
                    enableDisableServerSyncing(true);
                }

                setup_wizard.startInstallation();

                outgoing_intent.putExtra("qr_code_validity", true);
            }
            else
            {
                Log.d(TAG, "Non Isansys Installation QR code");
                outgoing_intent.putExtra("qr_code_validity", false);
            }
        }
        else
        {
            Log.d(TAG, "No text - Non Isansys Installation QR code");
            outgoing_intent.putExtra("qr_code_validity", false);
        }

        sendBroadcast(outgoing_intent);
    }

    enum VideoCallRequest
    {
        REQUEST_CALL,
        CANCEL_CALL_REQUEST
    }

    public void sendPatientInitedVideoCallRequestToServer(VideoCallRequest videoCallRequest, ArrayList<VideoCallContact> contacts)
    {
        Log.d(TAG, "sendPatientInitedVideoCallRequestToServer");

        JsonObject payload = new JsonObject();

        payload.addProperty("bed_id", getBedId());
        payload.addProperty("hospital_patient_id", patient_info.getHospitalPatientId());
        payload.addProperty("timestamp", TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(getNtpTimeNowInMilliseconds()));

        if (videoCallRequest == VideoCallRequest.CANCEL_CALL_REQUEST)
        {
            payload.addProperty("cancel", true);
        }
        else
        {
            payload.addProperty("cancel", false);
            payload.add("contact", new Gson().toJsonTree(contacts));
        }

        server_syncing.sendPatientInitedVideoCallRequestToServer(payload);
    }

    private void validateQrCode(Intent intent)
    {
        String qr_code_contents = intent.getStringExtra("QR_CODE_CONTENTS");

        // Try with default (new) key first
        byte[] aes_key_byte_array = Utils.hexStringToByteArray("28482B4D6251655468576D5A71337436");
        byte[] plaintext = decryptQrCode(qr_code_contents, aes_key_byte_array);
        QrCodeData qr_info = new QrCodeData(plaintext, Log);

        if(qr_info.isValid() == false)
        {
            // If its not valid, then try with the old key
            aes_key_byte_array = Utils.hexStringToByteArray("000102030405060708090a0b0c0d0e0f");
            plaintext = decryptQrCode(qr_code_contents, aes_key_byte_array);
            qr_info = new QrCodeData(plaintext, Log);
        }

        patient_gateway_outgoing_commands.reportQrCodeOrNfcDetailsToUserInterface(qr_info);
    }


    private boolean getEnabledFromIntent(Intent intent)
    {
        return intent.getBooleanExtra("enabled", false);
    }


    private final BroadcastReceiver broadcastReceiverIncomingCommandsFromUserInterface__updateMode = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int command = intent.getIntExtra("command", 0);

            try
            {
                Commands incoming_command = Commands.values()[command];

                if (incoming_command != Commands.CMD_CHECK_GATEWAY_UI_CONNECTION)
                {
                    Log.d(TAG, "Command = " + incoming_command.toString());
                }
                else
                {
                    //Log.d(TAG, "Ping from UI at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(getNtpTimeNowInMilliseconds()) + " : " + logProcessHeapSize());
                }

                switch (incoming_command)
                {
                    case CMD_CHECK_GATEWAY_UI_CONNECTION:
                    {
                        // Have heard from the UI, so reset the UI timeout
                        checkUserInterfaceRunning.resetUserInterfaceTimeout();

                        patient_gateway_outgoing_commands.sendPingCommandToUserInterface();
                    }
                    break;

                    case CMD_VALIDATE_QR_CODE:
                    {
                        validateQrCode(intent);
                    }
                    break;

                    case CMD_GET_SOFTWARE_UPDATE_STATE:
                    {
                        reportAvailableUpdateVersions();

                        patient_gateway_outgoing_commands.reportInSoftwareUpdateMode();
                    }
                    break;

                    case CMD_SOFTWARE_UPDATE_COMPLETE:
                    {
                        exitSoftwareUpdateMode();

                        if (needToGetNewServerData())
                        {
                            startGetServerSettingsDueToSoftwareUpdate();
                        }
                    }
                    break;

                    case CMD_GET_GATEWAYS_ASSIGNED_BED_DETAILS:
                    {
                        reportGatewayBedDetails();
                    }
                    break;

                    default:
                    {
                        // Do Nothing
                    }
                    break;
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "EXCEPTION. command byte = " + command + " : " + e);
            }
        }
    };


    private void enterSoftwareUpdateMode()
    {
        gateway_settings.storeSoftwareUpdateModeActive(true);

        // deregister regular broadcast receiver
        unregisterReceiver(broadcastReceiverIncomingCommandsFromUserInterface);

        // register update mode receiver
        registerReceiver(broadcastReceiverIncomingCommandsFromUserInterface__updateMode, new IntentFilter(INTENT__COMMANDS_TO_PATIENT_GATEWAY));

    }


    private void exitSoftwareUpdateMode()
    {
        gateway_settings.storeSoftwareUpdateModeActive(false);

        // re-register regular broadcast receiver
        registerReceiver(broadcastReceiverIncomingCommandsFromUserInterface, new IntentFilter(INTENT__COMMANDS_TO_PATIENT_GATEWAY));

        // de-register update mode receiver
        unregisterReceiver(broadcastReceiverIncomingCommandsFromUserInterface__updateMode);

        firmware_image_manager.deleteAllApksInFirmwareFolder();
    }


    private void reportAvailableUpdateVersions()
    {
        int gw_version = firmware_image_manager.getLatestStoredFirmwareVersion(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY);
        String gw_name = firmware_image_manager.getFirmwareFileName(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY);

        int ui_version = firmware_image_manager.getLatestStoredFirmwareVersion(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE);
        String ui_name = firmware_image_manager.getFirmwareFileName(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE);

        if(queue_firmware_images_to_download.isEmpty())
        {
            patient_gateway_outgoing_commands.reportSoftwareUpdateAvailable(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY, gw_version, gw_name);
            patient_gateway_outgoing_commands.reportSoftwareUpdateAvailable(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE, ui_version, ui_name);
        }
        else
        {
            Log.d(TAG, "reportAvailableUpdateVersions : firmware download in progress so not reporting updates available");
        }
    }


    private void emptyLocalDatabaseIncludingEwsThresholdSets()
    {
        emptyLocalDatabase(DELETE_EWS_THRESHOLDS);

        gateway_settings.storeInstallationComplete(false);
        reportInstallationCompleteFlag();

        // Update the Gateways cache
        queryThresholdSets();
    }


    public void forceResetWifi()
    {
        wifi_event_manager.turnWifiOffAndOn(true);
    }


    public void forceResetBluetooth(boolean delayed, boolean remove_devices)
    {
        int delay = delayed ? 10 : 0;

        // Delayed = false only when resetting BLE before starting a new session.
        // In that case, want to skip turning wifi off and on in the reset code.
        bluetooth_reset_manager.skip_turning_wifi_off = !delayed;

        if(remove_devices)
        {
            Log.d(TAG, "CMD_RESET_BLUETOOTH : removing all devices and resetting bluetooth");
            device_info_manager.getBluetoothLeDeviceController().postCompleteBleReset(delay);
        }
        else
        {
            device_info_manager.getBluetoothLeDeviceController().postBleResetWithoutRemovingDevices(delay);
        }
    }


    private void forceDeviceConnectionState(boolean connected, DeviceInfo device_info)
    {
        if(connected)
        {
            device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);
            if(device_info.dummy_data_mode)
            {
                // ensure device is initialised, as it won't have been done on device creation any more.
                dummy_data_instance.initVariables(device_info);
            }

            storeConnectionEventIfSessionRunning(device_info, LocalDatabaseStorage.ConnectionEvent.DEVICE_CONNECTED);
        }
        else
        {
            device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.DISCONNECTED);

            storeConnectionEventIfSessionRunning(device_info, LocalDatabaseStorage.ConnectionEvent.DEVICE_DISCONNECTED);
        }

        // Tell Server about the new connection status
        reportGatewayStatusToServer();

        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
    }


    // Wrapper for outgoing command so it can be accessed via patient gateway interface
    public void reportRealTimeLinkEnableStatus()
    {
        patient_gateway_outgoing_commands.reportRealTimeLinkEnableStatus();
    }


    private void reportAllDeviceInfos()
    {
        patient_gateway_outgoing_commands.reportDeviceInfo(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH));
        patient_gateway_outgoing_commands.reportDeviceInfo(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE));
        patient_gateway_outgoing_commands.reportDeviceInfo(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2));
        patient_gateway_outgoing_commands.reportDeviceInfo(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE));
        patient_gateway_outgoing_commands.reportDeviceInfo(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE));

        for(DeviceInfo device_info : device_info_manager.getListOfNonSensorDeviceInfoObjects())
        {
            patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
        }
/*
                    if (device_type == DeviceType.DEVICE_TYPE__PULSE_TRANSIT_TIME)
                    {
                        if((gateway_settings.getPulseTransitTimeEnabledStatus()) && (device_info_manager.isDeviceTypeHumanReadableDeviceIdValid(DeviceType.DEVICE_TYPE__LIFEOX)))
                        {
                            // PTT graph should be shown only if lifeox and lifetouch connected to gateway
                            device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__PULSE_TRANSIT_TIME).device_type = DeviceType.DEVICE_TYPE__PULSE_TRANSIT_TIME;
                            //device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__PULSE_TRANSIT_TIME).show_on_ui = true;
                        }
                        else
                        {
                            // PTT graph should be shown only if lifeox and lifetouch connected to gateway
                            device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__PULSE_TRANSIT_TIME).device_type = DeviceType.DEVICE_TYPE__PULSE_TRANSIT_TIME;
                            //device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__PULSE_TRANSIT_TIME).show_on_ui = false;
                        }
                    }
*/
    }


    private <T extends MeasurementVitalSign> void reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType vital_sign_type, T measurement)
    {
        patient_gateway_outgoing_commands.reportNewVitalsData(vital_sign_type, measurement);

        if (vital_sign_type.equals(VitalSignType.MANUALLY_ENTERED_ANNOTATION))
        {
            // Do not add it to the Long Term Measurement tracker
        }
        else
        {
            T temp;

            try
            {
                /* Clone the measurement, as it will be modified by the Measurement Validity Tracker, and
                 * it still needs to be written to the database.
                 */
                temp = (T)measurement.clone();

                long_term_measurement_validity_tracker.addNewLongTermMeasurement(temp);
            }
            catch(CloneNotSupportedException e)
            {
                Log.e(TAG, "reportNewVitalsDataAndAddToMeasurementValidityTracker: CloneNotSupportedException - could not report measurement to validity tracker.");
            }
        }
    }


    private void enableSessionAutoResume(boolean enabled)
    {
        gateway_settings.setAutoResumeEnabled(enabled);

        patient_gateway_outgoing_commands.reportAutoResumeStatus(gateway_settings.getAutoResumeEnabled());

        reportFeaturesEnabledToServer();
    }


    private void retryConnectingToDevice(DeviceInfo device_info)
    {
        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.SEARCHING);

        // Tell the UI about the actual_device_connection_status change
        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

        connectNextDeviceToGatewayIfNeededAndNoScanRunning();
    }


    // CMD_DISCONNECT_FROM_DESIRED_DEVICE
    private void disconnectFromDesiredDevice(DeviceInfo device_info, int device_session_ended_by_user_id, boolean turn_off)
    {
        Log.d(TAG, "disconnectFromDesiredDevice = " + device_info.getSensorTypeAndDeviceTypeAsString() + " : device_session_ended_by_user_id = " + device_session_ended_by_user_id + " : turn_off = " + turn_off);

        long device_end_session_time = getNtpTimeNowInMilliseconds();

        exitRawAccelerometerModeIfRunning(device_info);

        exitSetupModeIfRunningAndDisconnectFromDevice(device_info, device_end_session_time, device_session_ended_by_user_id, turn_off);

        cancelSpo2SpotMeasurementsIfRunning(device_info);

        patient_gateway_outgoing_commands.reportGatewaySessionNumbers(patient_session_info.android_database_patient_session_id, device_sessions);
    }


    private void exitSetupModeIfRunningAndDisconnectFromDevice(DeviceInfo device_info, long end_session_time, int session_ended_by_user_id, boolean turn_off)
    {
        exitSetupModeIfRunning(device_info);

        Log.d(TAG, "exitSetupModeIfRunningAndDisconnectFromDevice. device_type = " + device_info.getSensorTypeAndDeviceTypeAsString() + " : turn_off = " + turn_off + " : id = " + device_info.human_readable_device_id);

        if (device_info.isDeviceHumanReadableDeviceIdValid())
        {
            if(device_info instanceof BtleSensorDevice)
            {
                // Do device type specific work
                switch (device_info.sensor_type)
                {
                    case SENSOR_TYPE__LIFETOUCH:
                    {
                        flushLifetouchHeartBeatQueueToDatabase();
                    }
                    break;

                    case SENSOR_TYPE__TEMPERATURE:
                    {
                        lastTemperaturePacketId = -1;
                    }
                    break;

                    case SENSOR_TYPE__SPO2:
                    {
                        switch (device_info.device_type)
                        {
                            case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
                            case DEVICE_TYPE__NONIN_3230:
                            {
                                nonin_playback_expected = false;
                                nonin_setup_mode_blocked = false;
                            }
                            break;
                        }

                        // There is NO turn off command for any of the Pulse Ox devices.
                        // HAVE to disable to make disconnectBtleDevice work properly
                        turn_off = false;
                    }
                    break;

                    case SENSOR_TYPE__BLOOD_PRESSURE:
                    {
                        // There is NO turn off command for any of the BP devices.
                        // HAVE to disable to make disconnectBtleDevice work properly
                        turn_off = false;
                    }
                    break;

                    case SENSOR_TYPE__WEIGHT_SCALE:
                    {
                        // There is NO turn off command for any of the BP devices.
                        // HAVE to disable to make disconnectBtleDevice work properly
                        turn_off = false;
                    }
                    break;
                }

                if (device_info.isDeviceSessionInProgress() == false)
                {
                    // Override the default Turn Off here if the device is NOT part of the Patient Session yet (Add Device's Remove button)
                    turn_off = false;
                }

                if(device_info.dummy_data_mode == false)
                {
                    disconnectBtleDevice((BtleSensorDevice) device_info, turn_off);
                }
            }
            else if(device_info instanceof BtClassicSensorDevice)
            {
                // Do device type specific work
                switch (device_info.device_type)
                {
                    case DEVICE_TYPE__NONIN_WRIST_OX:
                    {
                        sendBroadcast(new Intent(NoninWristOx.SET_REMOVED_FROM_SESSION));

                        flushPulseOxIntermediateQueueToDatabase();
                    }
                    break;

                    case DEVICE_TYPE__AND_UA767:
                    {
                        sendBroadcast(new Intent(AnD_UA767.SET_REMOVED_FROM_SESSION));
                    }
                    break;

                    case DEVICE_TYPE__FORA_IR20:
                    {
                        sendBroadcast(new Intent(Fora_Ir20.SET_REMOVED_FROM_SESSION));
                    }
                    break;
                }

                bluetooth_pairing.cancelBondingAndUnpairIfNeeded(device_info.bluetooth_address);
            }

            if (device_info.sensor_type == SensorType.SENSOR_TYPE__SPO2)
            {
                // Handle any outstanding SpO2 Intermediate measurements (if SpO2 spot measurements are enabled)
                restartSpO2IntermediateTidyUpTimerIfRequired();
            }

            endDeviceSessionAndExportCsvIfNeeded(device_info, end_session_time, session_ended_by_user_id);

            resetDeviceInfoAndReportToUi(device_info);
        }
    }


    private void exitSetupModeIfRunning(DeviceInfo device_info)
    {
        exitSetupModeIfRunning(device_info, true, true);
    }


    private void exitSetupModeIfRunning(DeviceInfo device_info, boolean send_exit_setup_mode_to_device, boolean stop_setup_mode_timer_running)
    {
        if (device_info != null)
        {
            if (device_info.isInSetupMode())
            {
                exitDeviceSetupMode(device_info, send_exit_setup_mode_to_device, stop_setup_mode_timer_running);
            }
        }
    }


    private void exitRawAccelerometerModeIfRunning(DeviceInfo device_info)
    {
        exitRawAccelerometerModeIfRunning(device_info, true);
    }


    private void exitRawAccelerometerModeIfRunning(DeviceInfo device_info, boolean send_low_level_exit_mode_commands)
    {
        if (device_info != null)
        {
            if(device_info.isInRawAccelerometerMode())
            {
                exitDeviceRawAccelerometerMode((BtleSensorDevice)device_info, send_low_level_exit_mode_commands);
            }
        }
    }


    // Ending SpO2 spot measurements immediately after Spo2 device removed or patient session ended
    private void cancelSpo2SpotMeasurementsIfRunning(DeviceInfo device_info)
    {
        if (device_info.sensor_type == SensorType.SENSOR_TYPE__SPO2)
        {
            if (spO2_spot_measurements_enabled)
            {
                Log.d(TAG, "cancelSpo2SpotMeasurementsIfRunning following SpO2 device removal or ending of patient session");

                spo2_intermediate_tidy_up_timer.cancel();

                intermediate_spo2_processor.reset();
            }
        }
    }


    private void setDeviceConnectionStatusToSearchingForValidDevices(DeviceInfo device_info)
    {
        if (device_info.isDeviceHumanReadableDeviceIdValid())
        {
            Log.d(TAG, "setDeviceConnectionStatusToSearchingForValidDevices: " + device_info.getSensorTypeAndDeviceTypeAsString());

            if((device_info.isActualDeviceConnectionStatusNotPaired()) || (device_info.isActualDeviceConnectionStatusUnexpectedlyUnbonded()))
            {
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.SEARCHING);
            }
            else
            {
                Log.e(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " is already paired");
            }

            patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
        }
    }


    private void clearDesiredDevice(DeviceInfo device_info)
    {
        if(device_info.getActualDeviceConnectionStatus() != DeviceConnectionStatus.CONNECTED)
        {
            device_info.resetAsNew();
        }

        // Tell the UI the new device info (default invalid)
        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
    }


    private void storePatientOrientationMeasurementInterval(int measurement_interval_in_seconds)
    {
        Log.d(TAG, "storePatientOrientationMeasurementIntervalInSeconds : measurement_interval_in_seconds = " + measurement_interval_in_seconds);

        gateway_settings.storePatientOrientationMeasurementIntervalInSeconds(measurement_interval_in_seconds);

        patient_gateway_outgoing_commands.reportPatientOrientationMeasurementInterval(gateway_settings.getPatientOrientationMeasurementIntervalInSeconds());

        reportFeaturesEnabledToServer();
    }


    private void storeDeveloperPopupEnabledStatus(boolean enabled)
    {
        Log.d(TAG, "storeDeveloperPopupEnabledStatus = " + enabled);

        gateway_settings.storeDeveloperPopupEnabled(enabled);

        patient_gateway_outgoing_commands.reportDeveloperPopupEnabled(gateway_settings.getDeveloperPopupEnabled());

        reportFeaturesEnabledToServer();
    }


    private void setGsmModeOnlyEnabledStatus(boolean gsm_mode_only)
    {
        boolean gsm_available = gsm_event_manager.isTabletGsm();

        Log.d(TAG, "setGsmModeOnlyEnabledStatus : desired state is " + gsm_mode_only + " and GSM available is " + gsm_available);

        if (gsm_available && gsm_mode_only)
        {
            gateway_settings.storeGsmModeOnlyEnabledStatus(true);

            Log.d(TAG, "wifi_event_manager.unRegisterWifiStatusReceiver()");
            wifi_event_manager.cancelReconnectionRunnable();

            // Do not unregister it here as its done during the onDestroy
            //unregisterReceiver(wifi_event_manager.wifi_status_receiver);

            // If the Gateway has been set to GSM only, then do not register for Wifi events. This stops the Wifi from trying to search out access points when they will
            // not be used.....freeing up the radio chip to talk to BTLE faster
            enableWifi(false);
        }
        else
        {
            gateway_settings.storeGsmModeOnlyEnabledStatus(false);

            enableWifi(true);

            registerReceiver(wifi_event_manager.wifi_status_receiver, wifi_event_manager.wifiIntentFilter());
            Log.d(TAG, "wifi_event_manager.registerWifiStatusReceiver()");
        }

        patient_gateway_outgoing_commands.reportGsmModeOnlyEnabledStatus(inGsmOnlyMode());

        reportGatewayStatusToServer();
    }


    private void enableVideoCalls(boolean enabled)
    {
        Log.d(TAG, "enableVideoCalls = " + enabled);

        gateway_settings.storeVideoCallsEnabledStatus(enabled);

        patient_gateway_outgoing_commands.reportVideoCallsEnabledStatus(gateway_settings.getVideoCallsEnabledStatus());

        reportGatewayStatusToServer();
    }


    private void enableViewWebPages(boolean enabled)
    {
        Log.d(TAG, "enableViewWebPages = " + enabled);

        gateway_settings.storeViewWebPagesEnabledStatus(enabled);

        patient_gateway_outgoing_commands.reportViewWebPagesEnabledStatus(gateway_settings.getViewWebPagesEnabledStatus());

        reportGatewayStatusToServer();
    }


    private void setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(int percentage)
    {
        Log.d(TAG, "storePercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid = " + percentage);

        gateway_settings.storePercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(percentage);

        patient_gateway_outgoing_commands.reportPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(gateway_settings.getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid());

        reportGatewayStatusToServer();
    }


    private void setShowIpAddressOnWifiPopupEnabledStatus(boolean enabled)
    {
        Log.d(TAG, "setShowIpAddressOnWifiPopupEnabledStatus = " + enabled);

        gateway_settings.storeShowIpAddressOnWifiPopupEnabled(enabled);

        patient_gateway_outgoing_commands.reportShowIpAddressOnWifiPopupEnabled(gateway_settings.getShowIpAddressOnWifiPopupEnabled());

        reportFeaturesEnabledToServer();
    }


    private void reportGatewayBedDetails()
    {
        patient_gateway_outgoing_commands.reportGatewaysAssignedBedDetails(gateway_settings.getGatewaysAssignedBedId(),
                gateway_settings.getGatewaysAssignedWardName(),
                gateway_settings.getGatewaysAssignedBedName()
        );
    }


    private void enableAutoUploadLogsToServer(boolean enabled)
    {
        Log.d(TAG, "enableAutoUploadLogsToServer = " + enabled);

        gateway_settings.storeEnableAutoUploadLogsToServer(enabled);

        if (enabled)
        {
            old_log_file_uploader.start();
        }
        else
        {
            old_log_file_uploader.stop();
        }

        patient_gateway_outgoing_commands.reportEnableAutoUploadOfLogsFilesToServer(gateway_settings.getEnableAutoUploadLogsToServer());

        reportFeaturesEnabledToServer();
    }


    private void enablePatientOrientation(boolean enabled)
    {
        Log.d(TAG, "enablePatientOrientation = " + enabled);

        gateway_settings.storeEnablePatientOrientation(enabled);

        loadPatientOrientationFromSettings();

        patient_gateway_outgoing_commands.reportPatientOrientationEnabledStatus(gateway_settings.getEnablePatientOrientation());

        reportFeaturesEnabledToServer();
    }


    private void loadPatientOrientationFromSettings()
    {
        DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);
        if(device_info.device_type != DeviceType.DEVICE_TYPE__INVALID)
        {
            device_info.setPatientOrientationModeEnabled(gateway_settings.getEnablePatientOrientation());
            device_info.setPatientOrientationModeIntervalTime(gateway_settings.getPatientOrientationMeasurementIntervalInSeconds());
        }
    }


    private void showNumbersOnBatteryIndicators(boolean enabled)
    {
        Log.d(TAG, "showNumbersOnBatteryIndicators = " + enabled);

        gateway_settings.storeShowNumbersOnBatteryIndicator(enabled);

        patient_gateway_outgoing_commands.reportShowNumbersOfBatteryIndicatorEnabledStatus(gateway_settings.getShowNumbersOnBatteryIndicator());

        reportFeaturesEnabledToServer();
    }


    private void setLifetempMeasurementIntervalInSeconds(int measurement_interval_in_seconds)
    {
        Log.d(TAG, "setLifetempMeasurementIntervalInSeconds = " + measurement_interval_in_seconds);

        gateway_settings.storeLifetempMeasurementInterval(measurement_interval_in_seconds);

        patient_gateway_outgoing_commands.reportLifetempMeasurementInterval(gateway_settings.getLifetempMeasurementInterval());

        reportFeaturesEnabledToServer();
    }


    private void showMacAddress(boolean enabled)
    {
        Log.d(TAG, "showMacAddress = " + enabled);

        gateway_settings.storeShowMacAddressOnSettingsPage(enabled);

        patient_gateway_outgoing_commands.reportShowMacAddressEnabledStatus(gateway_settings.getShowMacAddressOnSettingsPage());

        reportFeaturesEnabledToServer();
    }


    private void wifiLoggingEnabled(boolean enabled)
    {
        Log.d(TAG, "wifiLoggingEnabled = " + enabled);

        gateway_settings.storeEnableWifiLogging(enabled);

        patient_gateway_outgoing_commands.reportWifiLoggingEnabledStatus(gateway_settings.getEnableWifiLogging());

        reportFeaturesEnabledToServer();
    }


    private void gsmLoggingEnabled(boolean enabled)
    {
        Log.d(TAG, "gsmLoggingEnabled = " + enabled);

        gateway_settings.storeEnableGsmLogging(enabled);

        patient_gateway_outgoing_commands.reportGsmLoggingEnabledStatus(gateway_settings.getEnableGsmLogging());

        reportFeaturesEnabledToServer();
    }


    private void databaseLoggingEnabled(boolean enabled)
    {
        Log.d(TAG, "databaseLoggingEnabled = " + enabled);

        gateway_settings.storeEnableDatabaseLogging(enabled);

        patient_gateway_outgoing_commands.reportDatabaseLoggingEnabledStatus(gateway_settings.getEnableDatabaseLogging());

        reportFeaturesEnabledToServer();
    }


    private void serverLoggingEnabled(boolean enabled)
    {
        Log.d(TAG, "serverLoggingEnabled = " + enabled);

        gateway_settings.storeEnableServerLogging(enabled);

        patient_gateway_outgoing_commands.reportServerLoggingEnabledStatus(gateway_settings.getEnableServerLogging());

        reportFeaturesEnabledToServer();
    }


    private void batteryLoggingEnabled(boolean enabled)
    {
        Log.d(TAG, "batteryLoggingEnabled = " + enabled);

        gateway_settings.storeEnableBatteryLogging(enabled);

        patient_gateway_outgoing_commands.reportBatteryLoggingEnabledStatus(gateway_settings.getEnableBatteryLogging());

        reportFeaturesEnabledToServer();
    }


    private void dfuBootloaderEnabled(boolean enabled)
    {
        Log.d(TAG, "dfuBootloaderEnabled = " + enabled);

        gateway_settings.storeEnableDfuBootloader(enabled);

        patient_gateway_outgoing_commands.reportDfuBootloaderEnabledStatus(gateway_settings.getEnableDfuBootloader());

        reportGatewayStatusToServer();
    }

    private void spO2SpotMeasurementsEnabled(boolean enabled)
    {
        Log.d(TAG, "spO2SpotMeasurementsEnabled = " + enabled);

        gateway_settings.storeEnableSpO2SpotMeasurements(enabled);

        patient_gateway_outgoing_commands.reportSpO2SpotMeasurementsEnabledStatus(gateway_settings.getEnableSpO2SpotMeasurements());

        reportGatewayStatusToServer();
    }

    private void predefineAnnotationsEnabled(boolean enabled)
    {
        Log.d(TAG, "predefineAnnotationsEnabled = " + enabled);

        gateway_settings.storeEnablePredefinedAnnotations(enabled);

        patient_gateway_outgoing_commands.reportPredefinedAnnotationEnabledStatus(gateway_settings.getEnablePredefineAnnotations());

        reportGatewayStatusToServer();
    }

    private void displayTemperatureInFahrenheit(boolean enabled)
    {
        Log.d(TAG, "displayTemperatureInFahrenheit = " + enabled);

        gateway_settings.storeDisplayTemperatureInFahrenheitEnabledStatus(enabled);

        patient_gateway_outgoing_commands.reportDisplayTemperatureInFahrenheitEnabledStatus(gateway_settings.getDisplayTemperatureInFahrenheitEnabledStatus());

        reportGatewayStatusToServer();
    }

    private void displayWeightInLbs(boolean enabled)
    {
        Log.d(TAG, "displayWeightInLbs = " + enabled);

        gateway_settings.storeDisplayWeightInLbsEnabledStatus(enabled);

        patient_gateway_outgoing_commands.reportDisplayWeightInLbsEnabledStatus(gateway_settings.getDisplayWeightInLbsEnabledStatus());

        reportGatewayStatusToServer();
    }

    private void enableUsaOnlyMode(boolean enabled)
    {
        Log.d(TAG, "showUsaOnlyMode = " + enabled);

        gateway_settings.storeUsaOnlyMode(enabled);

        patient_gateway_outgoing_commands.reportUsaModeEnabledStatus(gateway_settings.getUsaOnlyMode());

        reportFeaturesEnabledToServer();
    }


    private void showLifetouchActivityLevel(boolean enabled)
    {
        Log.d(TAG, "showLifetouchActivityLevel = " + enabled);

        gateway_settings.storeShowLifetouchActivityLevel(enabled);

        patient_gateway_outgoing_commands.reportShowLifetouchActivityLevelEnabledStatus(gateway_settings.getShowLifetouchActivityLevel());

        reportFeaturesEnabledToServer();
    }


    private void enableManualVitalSigns(boolean enabled)
    {
        Log.d(TAG, "enableManualVitalSigns = " + enabled);

        gateway_settings.storeEnableManualVitalSignsEntry(enabled);

        patient_gateway_outgoing_commands.reportManualVitalSignsEnabledStatus(gateway_settings.getEnableManualVitalSignsEntry());

        reportFeaturesEnabledToServer();
    }


    public void setNumberOfInvalidNoninWristOxIntermediateMeasurementsBeforeMinuteMarkedAsInvalid(int number)
    {
        Log.d(TAG, "setNumberOfInvalidNoninWristOxIntermediateMeasurementsBeforeMinuteMarkedAsInvalid = " + number);

        gateway_settings.storeMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(number);

        patient_gateway_outgoing_commands.reportMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(gateway_settings.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid());

        reportGatewayStatusToServer();
    }


    public void setDisplayTimeoutAppliesToPatientVitals(boolean applies)
    {
        Log.d(TAG, "setDisplayTimeoutAppliesToPatientVitals = " + applies);

        gateway_settings.storeDisplayTimeoutAppliesToPatientVitals(applies);

        patient_gateway_outgoing_commands.reportDisplayTimeoutAppliesToPatientVitals(gateway_settings.getDisplayTimeoutAppliesToPatientVitals());

        reportGatewayStatusToServer();
    }

    private void setLongTimeMeasurementTimeoutInMinutes(SensorType sensorType, int measurement_timeout_in_minutes)
    {
        Log.d(TAG, "setLongTimeMeasurementTimeoutInMinutes " + sensorType + " = " + measurement_timeout_in_minutes);

        int readback = 0;

        switch (sensorType)
        {
            case SENSOR_TYPE__SPO2:
            {
                gateway_settings.storeSpO2LongTermMeasurementTimeoutInMinutes(measurement_timeout_in_minutes);

                readback = gateway_settings.getSpO2LongTermMeasurementTimeoutInMinutes();
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                gateway_settings.storeBloodPressureLongTermMeasurementTimeoutInMinutes(measurement_timeout_in_minutes);

                readback = gateway_settings.getBloodPressureLongTermMeasurementTimeoutInMinutes();
            }
            break;

            case SENSOR_TYPE__WEIGHT_SCALE:
            {
                gateway_settings.storeWeightLongTermMeasurementTimeoutInMinutes(measurement_timeout_in_minutes);

                readback = gateway_settings.getWeightLongTermMeasurementTimeoutInMinutes();
            }
            break;

            case SENSOR_TYPE__TEMPERATURE:
            {
                gateway_settings.storeThirdPartyTemperatureLongTermMeasurementTimeoutInMinutes(measurement_timeout_in_minutes);

                readback = gateway_settings.getThirdPartyTemperatureLongTermMeasurementTimeoutInMinutes();

            }
            break;
        }

        patient_gateway_outgoing_commands.reportLongTermMeasurementTimeoutInMinutes(sensorType, readback);

        reportGatewayStatusToServer();
    }


    public void setMaxWebserviceJsonArraySize(int json_array_size)
    {
        Log.d(TAG, "setMaxWebserviceJsonArraySize = " + json_array_size);

        gateway_settings.storeNumberOfDatabaseRowsPerJsonMessage(json_array_size);

        patient_gateway_outgoing_commands.reportJsonArraySize(gateway_settings.getNumberOfDatabaseRowsPerJsonMessage());

        local_database_storage.setNumberOfRowsPerJsonMessage(gateway_settings.getNumberOfDatabaseRowsPerJsonMessage());

        reportFeaturesEnabledToServer();
    }


    private void runDevicesInTestMode(boolean enabled)
    {
        Log.d(TAG, "runDevicesInTestMode = " + enabled);

        gateway_settings.storeRunDevicesInTestMode(enabled);

        for (DeviceInfo device_info : device_info_manager.getListOfSensorDeviceInfoObjects())
        {
            device_info.setTestMode(gateway_settings.getRunDevicesInTestMode());
        }

        patient_gateway_outgoing_commands.reportRunDevicesInTestMode(gateway_settings.getRunDevicesInTestMode());

        reportFeaturesEnabledToServer();
    }


    public void enableSimpleHeartRateAlgorithm(boolean enabled)
    {
        Log.d(TAG, "enableSimpleHeartRateAlgorithm = " + enabled);

        gateway_settings.storeSimpleHeartRateEnabledStatus(enabled);

        patient_gateway_outgoing_commands.reportSimpleHeartRateEnabledStatus(gateway_settings.getSimpleHeartRateEnabledStatus());

        reportFeaturesEnabledToServer();
    }


    public void enableCsvOutput(boolean enabled)
    {
        Log.d(TAG, "enableCsvOutput = " + enabled);

        gateway_settings.storeCsvOutputEnableStatus(enabled);

        patient_gateway_outgoing_commands.reportCsvOutputEnableStatus(gateway_settings.getCsvOutputEnabledStatus());

        reportFeaturesEnabledToServer();
    }


    public void enableUnpluggedOverlay(boolean enabled)
    {
        Log.d(TAG, "enableUnpluggedOverlay = " + enabled);

        gateway_settings.storeUnpluggedOverlayEnableStatus(enabled);

        patient_gateway_outgoing_commands.reportUnpluggedOverlayEnabledStatus(gateway_settings.getUnpluggedOverlayEnabledStatus());

        reportGatewayStatusToServer();
    }


    private void enableAutoEnableEws(boolean enabled)
    {
        Log.d(TAG, "enableAutoEnableEws = " + enabled);

        gateway_settings.storeAutoEnableEwsEnableStatus(enabled);

        patient_gateway_outgoing_commands.reportAutoEnableEwsEnabledStatus(gateway_settings.getAutoEnableEwsEnabledStatus());

        reportGatewayStatusToServer();
    }


    private void enableHttps(boolean use_https)
    {
        Log.d(TAG, "enableHttps = " + use_https);

        // Save the status in the Patient Gateway app preferences
        gateway_settings.storeHttpsEnableStatus(use_https);

        // Read the value back to ensure its stored correctly
        use_https = gateway_settings.getHttpsEnabledStatus();

        server_syncing.useHttps(use_https);

        // Trigger an update of the Server URL to use this new HTTP/HTTPS value
        server_syncing.setIsansysServerAddress(gateway_settings.getServerAddress(), gateway_settings.getServerPort());

        patient_gateway_outgoing_commands.reportHttpsEnabledStatus(use_https);
    }


    private void enableWebserviceEncryption(boolean enable_webservice_encryption)
    {
        Log.d(TAG, "enableWebserviceEncryption = " + enable_webservice_encryption);

        // Save the status in the Patient Gateway app preferences
        gateway_settings.storeWebServiceEncryptionEnabled(enable_webservice_encryption);

        // Read the value back to ensure its stored correctly
        enable_webservice_encryption = gateway_settings.getWebServiceEncryptionEnabledStatus();

        server_syncing.useEncryptionForWebServiceCalls(enable_webservice_encryption);

        patient_gateway_outgoing_commands.reportWebServiceEncryptionEnabledStatus(enable_webservice_encryption);

        reportGatewayStatusToServer();
    }


    private void enableWebserviceAuthentication(boolean enable_webservice_authentication)
    {
        Log.d(TAG, "enableWebserviceAuthentication = " + enable_webservice_authentication);

        // Save the status in the Patient Gateway app preferences
        gateway_settings.storeWebServiceAuthenticationEnabled(enable_webservice_authentication);

        // Read the value back to ensure its stored correctly
        enable_webservice_authentication = gateway_settings.getWebServiceAuthenticationEnabledStatus();

        server_syncing.useAuthenticationForWebServiceCalls(enable_webservice_authentication);

        patient_gateway_outgoing_commands.reportWebServiceAuthenticationEnabledStatus(enable_webservice_authentication);

        reportGatewayStatusToServer();
    }


    private byte[] decryptQrCode(String qr_code_contents, byte[] aes_key_byte_array)
    {
        // ASCII85 decode the intents qr_code_contents to get the AES encrypted ciphertext
        byte[] ciphertext = Ascii85Coder.decodeAscii85StringToBytes(qr_code_contents);

        return decryptQrCode(ciphertext, aes_key_byte_array);
    }


    private byte[] decryptQrCode(byte[] ciphertext, byte[] aes_key_byte_array)
    {
        byte[] plaintext = null;

        try
        {
            // Decrypt the cipher text to get the plaintext
            plaintext = AES.decrypt(ciphertext, aes_key_byte_array);
        }
        catch (Exception e)
        {
            Log.d("Error", e.toString());
        }

        return plaintext;
    }


    private void getDatabaseStatus()
    {
        Executors.newSingleThreadExecutor().execute(() -> {

            // Long running operation
            server_syncing.readAmountInDatabaseAndReport();
        });
    }


    private ThresholdSet getThresholdSetFromServersThresholdSetId(int server_threshold_set_id)
    {
        for (ThresholdSet thresholdSet : default_early_warning_score_threshold_sets)
        {
            if (thresholdSet.servers_database_row_id == server_threshold_set_id)
            {
                // Got the right set of Thresholds
                Log.d(TAG, "getThresholdSetFromServersThresholdSetId FOUND : " + server_threshold_set_id);

                return thresholdSet;
            }
        }

        Log.e(TAG, "getThresholdSetFromServersThresholdSetId NOT FOUND : " + server_threshold_set_id);
        return null;
    }


    private ThresholdSetAgeBlockDetail getThresholdSetAgeBlockDetailFromServersThresholdSetAgeBlockDetailsId(int servers_threshold_set_age_block_id)
    {
        for (ThresholdSet threshold_set : default_early_warning_score_threshold_sets)
        {
            for (ThresholdSetAgeBlockDetail age_block_detail : threshold_set.list_threshold_set_age_block_detail)
            {
                if (age_block_detail.servers_database_row_id == servers_threshold_set_age_block_id)
                {
                    // Got the right set of Thresholds
                    Log.d(TAG, "getThresholdSetAgeBlockDetailFromServersThresholdSetAgeBlockDetailsId FOUND : " + servers_threshold_set_age_block_id);

                    return age_block_detail;
                }
            }
        }

        Log.e(TAG, "getThresholdSetAgeBlockDetailFromServersThresholdSetAgeBlockDetailsId NOT FOUND : " + servers_threshold_set_age_block_id);

        return null;
    }


    private void createNewSession(int session_started_by_user_id, String hospital_patient_id, int servers_threshold_set_id, int servers_threshold_set_age_block_id)
    {
        Log.d(TAG, "createNewSession : " + session_started_by_user_id + " : " + hospital_patient_id + " : " + servers_threshold_set_id + " : " + servers_threshold_set_age_block_id);

        long time_now_in_milliseconds = getNtpTimeNowInMilliseconds();

        // See if we need to run in Periodic Mode (Every X seconds, take Setup Mode data for Y seconds)
        if (gateway_settings.getPeriodicModeEnabledStatus())
        {
            device_periodic_mode.enable(gateway_settings.getPeriodicModePeriodTimeInSeconds(), gateway_settings.getPeriodicModeActiveTimeInSeconds());
        }

        patient_info.setHospitalPatientId(hospital_patient_id);

        ThresholdSet thresholdSet = getThresholdSetFromServersThresholdSetId(servers_threshold_set_id);
        ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = getThresholdSetAgeBlockDetailFromServersThresholdSetAgeBlockDetailsId(servers_threshold_set_age_block_id);

        patient_info.setThresholdSet(thresholdSet);
        patient_info.setThresholdSetAgeBlockDetails(thresholdSetAgeBlockDetail);

        long patient_session_start_time = time_now_in_milliseconds;

        if (dummy_data_instance.isDummyDataModeEnabled() && dummy_data_instance.isDummyDataModeBackFillEnabled())
        {
            patient_session_start_time -= (dummy_data_instance.getNumberOfHoursToBackfill() * DateUtils.HOUR_IN_MILLIS);
        }

        String string__bed_id = gateway_settings.getGatewaysAssignedBedId();
        int bed_id = Integer.parseInt(string__bed_id);

        patient_session_info.setInitialValues();

        patient_session_info.start_session_time = patient_session_start_time;

        patient_gateway_outgoing_commands.reportPatientStartSessionTime(patient_session_info.start_session_time);

        // *****************************************************************************************************************************************
        // Create new Patient Details row
        // *****************************************************************************************************************************************
        int patient_details_uri_as_int = local_database_storage.insertPatientDetails(bed_id, patient_info, session_started_by_user_id, patient_session_info.start_session_time);
        Log.e(TAG, "patient_details_uri_as_int = " + patient_details_uri_as_int);


        // *****************************************************************************************************************************************
        // Create a new Patient Session row
        // *****************************************************************************************************************************************
        patient_session_info.android_database_patient_session_id = local_database_storage.insertPatientSession(patient_details_uri_as_int, patient_session_info.start_session_time, session_started_by_user_id);
        Log.e(TAG, "patient_session_info.android_database_patient_session_id = " + patient_session_info.android_database_patient_session_id);

        // Let the UI know the new device session numbers
        patient_gateway_outgoing_commands.reportGatewaySessionNumbers(patient_session_info.android_database_patient_session_id, device_sessions);

        // Reset the Heart Beat and SpO2 processing code for this new session
        lifetouch_heart_beat_processor.reset();
        intermediate_spo2_processor.reset();

        addDeviceInfoAndDeviceSessionsIfNeeded(session_started_by_user_id, patient_session_info.start_session_time);

        patient_session_info.patient_session_running = true;

        updateDeviceMeasurementProgressBar();

        reportGatewayStatusToServer();

        if(device_info_manager.isEarlyWarningScoreDevicePartOfSession())
        {
            ArrayList<ArrayList<ThresholdSetLevel>> selected_ews_levels = patient_info.getThresholdSetAgeBlockDetails().list_of_threshold_set_levels_by_measurement_type;

            early_warning_score_processor.cacheSelectedEarlyWarningScores(selected_ews_levels, patient_info.getThresholdSet().name);

            patient_gateway_outgoing_commands.reportDeviceInfo(device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE));
        }

        if (long_term_measurement_validity_tracker != null)
        {
            long_term_measurement_validity_tracker.reset();
        }
        else
        {
            long_term_measurement_validity_tracker = new LongTermMeasurementValidityTracker(Log, early_warning_score_processor, ntp_time, device_info_manager);
        }

        if (dummy_data_instance.isDummyDataModeEnabled())
        {
            if (dummy_data_instance.isDummyDataModeBackFillEnabled())
            {
                dummy_data_instance.backfillWithDummyData(time_now_in_milliseconds);
            }
        }

        if(gateway_settings.getAutoResumeEnabled())
        {
            writeCurrentSessionFile(patient_session_info.android_database_patient_session_id);
        }

        nonin_playback_expected = false;
        nonin_setup_mode_blocked = false;
    }


    // Called by CMD_ENABLE_DEVICE_PERIODIC_SETUP_MODE and CMD_FROM_SERVER__ENABLE_PERIODIC_SETUP_MODE
    private void enableDevicePeriodicSetupMode(boolean enabled)
    {
        Log.d(TAG, "enableDevicePeriodicSetupMode = " + enabled);

        // This might have been called while a session is in progress - update the value in device_periodic_mode directly
        if (enabled)
        {
            device_periodic_mode.enable(gateway_settings.getPeriodicModePeriodTimeInSeconds(), gateway_settings.getPeriodicModeActiveTimeInSeconds());
        }
        else
        {
            device_periodic_mode.disable();
        }

        gateway_settings.storePeriodicModeEnabledStatus(enabled);

        patient_gateway_outgoing_commands.reportDevicePeriodicSetupModeEnableStatus(enabled);

        reportGatewayStatusToServer();
    }

    /*
     * @ charger_unplugged : boolean = set to False if charger is unplugged.This variable is updated to "False" every battery reading if tablet is not charger_unplugged to charger.
     * @ plugged_and_charging : boolean = set to True if Tablet is putting charge in. This variable is updated on every battery reading.
     * @ battery_below_50_percent : boolean = set to True if tablet battery percentage is less than 50. This variable is updated every battery reading to True is battery level is less than 50
     * @ tablet_charged_within_period : boolean = set to False if tablet isn't charged for specified period of time (6 hours)
     * @ last_charged_time : long = time stamp in ms for last time charged. "0" if not charged with in 6 hours.
     * @ valid_updated_tablet_charge_status : boolean = True if the check for last charge time is done. This is updated every five minutes.
     * @ NOTE : tablet_charged_within_period and last_charged_time is updated once every 5 minutes. During other time, Invalid timestamp = "1" is sent.
     * @ NOTE : if timestamp = "1" then ignore tablet_charged_within_period and last_charged_time variable.
     */
    public void reportGatewayStatusToServer()
    {
        JsonObject gateway_status = new JsonObject();

        gateway_status.addProperty("gateway_status_timestamp", TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(getNtpTimeNowInMilliseconds()));

        gateway_status.addProperty("lifetouch_periodic_sampling_mode_enabled", gateway_settings.getPeriodicModeEnabledStatus());
        gateway_status.addProperty("server_link_enabled", gateway_settings.getServerSyncEnabledStatus());
        gateway_status.addProperty("server_address", gateway_settings.getServerAddress());
        gateway_status.addProperty("assigned_ward_name", gateway_settings.getGatewaysAssignedWardName());
        gateway_status.addProperty("assigned_bed_name", gateway_settings.getGatewaysAssignedBedName());
        gateway_status.addProperty("from_ward_name", gateway_settings.getGatewaysAssignedWardName());
        gateway_status.addProperty("from_bed_name", gateway_settings.getGatewaysAssignedBedName());
        gateway_status.addProperty("from_bed_id", gateway_settings.getGatewaysAssignedBedId());
        gateway_status.addProperty("gateway_name", BluetoothAdapter.getDefaultAdapter().getName());
        gateway_status.addProperty("patient_session_running", isPatientSessionRunning());
        gateway_status.addProperty("dummy_data_mode_enable", dummy_data_instance.isDummyDataModeEnabled());
        gateway_status.addProperty("patient_gateway_software_version", app_versions.getGatewayVersionNumber());
        gateway_status.addProperty("user_interface_software_version", app_versions.getUserInterfaceVersionNumber());
        gateway_status.addProperty("webservice_authentication_enabled", gateway_settings.getWebServiceAuthenticationEnabledStatus());
        gateway_status.addProperty("webservice_encryption_enabled", gateway_settings.getWebServiceEncryptionEnabledStatus());
        gateway_status.addProperty("dummy_data_mode_measurements_per_tick", gateway_settings.getNumberOfDummyDataModeMeasurementsPerTick());
        gateway_status.addProperty("concurrent_instances_running", AsyncDatabaseUpdater.concurrent_instances_running);

        gateway_status.addProperty("number_of_times_gateway_has_booted", number_of_times_gateway_has_booted);
        gateway_status.addProperty("most_recent_gateway_boot_time", TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(most_recent_gateway_boot_time));

        gateway_status.addProperty("number_of_times_ui_has_booted", number_of_times_ui_has_booted);
        gateway_status.addProperty("most_recent_ui_boot_time", TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(most_recent_ui_boot_time));

        GatewayBatteryInfo gateway_battery_info = mBatteryInterpreter.getTabletBatteryInfo();

        // Copied from Gateway Ping but makes life a lot easier on the Server HTML page having it duplicated here
        gateway_status.addProperty("battery_charging", gateway_battery_info.charging);
        gateway_status.addProperty("battery_percentage", gateway_battery_info.android_percentage);
        gateway_status.addProperty("battery_temperature", gateway_battery_info.temperature);
        gateway_status.addProperty("battery_current_avg", gateway_battery_info.current_avg);

        gateway_status.addProperty("plugged_status", battery_data_and_event_handler.tablet_charging_status.charger_unplugged);
        gateway_status.addProperty("plugged_and_charging_state", battery_data_and_event_handler.tablet_charging_status.plugged_and_charging);
        gateway_status.addProperty("battery_below_50_percent", battery_data_and_event_handler.tablet_charging_status.battery_below_50_percent);

        gateway_status.addProperty("charge_status_valid_reading", battery_data_and_event_handler.tablet_charging_status.valid_updated_tablet_charge_status);
        gateway_status.addProperty("tablet_charged_within_peroid_status", battery_data_and_event_handler.tablet_charging_status.tablet_charged_within_period);
        gateway_status.addProperty("last_time_charged_timestamp", battery_data_and_event_handler.tablet_charging_status.last_charged_time);

        gateway_status.addProperty("setup_mode_time_in_seconds", gateway_settings.getSetupModeTimeInSeconds());
        gateway_status.addProperty("periodic_setup_mode_active_time_in_seconds", gateway_settings.getPeriodicModeActiveTimeInSeconds());
        gateway_status.addProperty("periodic_setup_mode_period_time_in_seconds", gateway_settings.getPeriodicModePeriodTimeInSeconds());

        gateway_status.addProperty("display_timeout", gateway_settings.getDisplayTimeoutLengthInSeconds());
        gateway_status.addProperty("display_timeout_applies_to_patient_vitals", gateway_settings.getDisplayTimeoutAppliesToPatientVitals());

        gateway_status.addProperty("percentage_of_poor_signal_heart_beats_before_marking_as_invalid", gateway_settings.getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid());
        gateway_status.addProperty("nonin_pulse_ox_number_invalid_intermediate_measurements_before_marking_as_invalid", gateway_settings.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid());

        gateway_status.addProperty("spo2_long_term_measurements_timeout", gateway_settings.getSpO2LongTermMeasurementTimeoutInMinutes() * 60);
        gateway_status.addProperty("blood_pressure_long_term_measurements_timeout", gateway_settings.getBloodPressureLongTermMeasurementTimeoutInMinutes() * 60);
        gateway_status.addProperty("weight_long_term_measurements_timeout", gateway_settings.getWeightLongTermMeasurementTimeoutInMinutes() * 60);
        gateway_status.addProperty("third_party_temperature_long_term_measurements_timeout", gateway_settings.getThirdPartyTemperatureLongTermMeasurementTimeoutInMinutes() * 60);

        DeviceInfo device_info;

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);
        gateway_status.addProperty("lifetouch_connected", device_info.isActualDeviceConnectionStatusConnected());
        gateway_status.addProperty("lifetouch_firmware_version", device_info.getDeviceFirmwareVersion());

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE);
        gateway_status.addProperty("lifetemp_connected", device_info.isActualDeviceConnectionStatusConnected());
        gateway_status.addProperty("lifetemp_firmware_version", device_info.getDeviceFirmwareVersion());

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);
        gateway_status.addProperty("nonin_pulse_ox_connected", device_info.isActualDeviceConnectionStatusConnected());

        // Get a list of all the paired Bluetooth devices
        BluetoothAdapter bluetooth_adaptor;
        bluetooth_adaptor = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth_adaptor != null)
        {
            Set<BluetoothDevice> pairedDevices = bluetooth_adaptor.getBondedDevices();

            StringBuilder json_string = new StringBuilder("{\"device_list\": [");

            if (pairedDevices.size() > 0)
            {
                for (BluetoothDevice device : pairedDevices)
                {
                    json_string.append("{\"device_name\": \"").append(device.getName()).append("\"},");
                }

                // Remove trailing comma
                json_string = new StringBuilder(json_string.substring(0, json_string.length() - 1));
            }

            // Close the json object
            json_string.append("]}");

            gateway_status.addProperty("paired_bluetooth_devices", json_string.toString());
        }

        gateway_status.addProperty("night_mode_enabled", night_mode_enabled);

        gateway_status.addProperty("unplugged_overlay_enabled", gateway_settings.getUnpluggedOverlayEnabledStatus());

        gateway_status.addProperty("enable_spo2_spot_measurements", gateway_settings.getEnableSpO2SpotMeasurements());

        gateway_status.addProperty("lifetouch_operating_mode", device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).operating_mode.ordinal());
        gateway_status.addProperty("lifetemp_operating_mode", device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).operating_mode.ordinal());
        gateway_status.addProperty("nonin_operating_mode", device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).operating_mode.ordinal());
        gateway_status.addProperty("ua767_operating_mode", device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_UA767).operating_mode.ordinal());
        gateway_status.addProperty("blood_pressure_operating_mode", device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).operating_mode.ordinal());

        gateway_status.addProperty("disk_free_space_percentage", diskUsage.getFreeDiskSpace());

        gateway_status.addProperty("lifetouch_device_session_running", device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH).isDeviceSessionInProgress());
        gateway_status.addProperty("lifetemp_device_session_running", device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE).isDeviceSessionInProgress());
        gateway_status.addProperty("pulse_ox_device_session_running", device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2).isDeviceSessionInProgress());
        gateway_status.addProperty("blood_pressure_ox_device_session_running", device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE).isDeviceSessionInProgress());
        gateway_status.addProperty("ews_device_session_running", device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).isDeviceSessionInProgress());

        gateway_status.addProperty("patient_name_lookup_enabled", gateway_settings.getPatientNameLookupEnabledStatus());
        gateway_status.addProperty("pre_defined_annotations", gateway_settings.getEnablePredefineAnnotations());
        gateway_status.addProperty("auto_add_ews", gateway_settings.getAutoEnableEwsEnabledStatus());
        gateway_status.addProperty("dfu", gateway_settings.getEnableDfuBootloader());
        gateway_status.addProperty("gsm_mode_supported", gsm_event_manager.isTabletGsm());
        gateway_status.addProperty("enable_gsm_mode", gateway_settings.getGsmOnlyModeEnabledStatus());
        gateway_status.addProperty("video_calls_enabled", gateway_settings.getVideoCallsEnabledStatus());
        gateway_status.addProperty("display_temperature_in_fahrenheit", gateway_settings.getDisplayTemperatureInFahrenheitEnabledStatus());
        gateway_status.addProperty("display_weight_in_lbs", gateway_settings.getDisplayWeightInLbsEnabledStatus());
        gateway_status.addProperty("view_webpages", gateway_settings.getViewWebPagesEnabledStatus());

        server_syncing.sendGatewayStatusIfServerConnected(gateway_status);
    }


    public String getJsonForGatewayPing()
    {
        JsonObject gateway_ping = new JsonObject();

        gateway_ping.addProperty("from_bed_id", gateway_settings.getGatewaysAssignedBedId());
        gateway_ping.addProperty("from_ward_name", gateway_settings.getGatewaysAssignedWardName());
        gateway_ping.addProperty("from_bed_name", gateway_settings.getGatewaysAssignedBedName());

        GatewayBatteryInfo gateway_battery_info = mBatteryInterpreter.getTabletBatteryInfo();
        gateway_ping.addProperty("battery_charging", gateway_battery_info.charging);
        gateway_ping.addProperty("battery_percentage", gateway_battery_info.android_percentage);
        gateway_ping.addProperty("battery_temperature", gateway_battery_info.temperature);
        gateway_ping.addProperty("battery_current_avg", gateway_battery_info.current_avg);

        gateway_ping.addProperty("plugged_status", battery_data_and_event_handler.tablet_charging_status.charger_unplugged);
        gateway_ping.addProperty("plugged_and_charging_state", battery_data_and_event_handler.tablet_charging_status.plugged_and_charging);
        gateway_ping.addProperty("battery_below_50_percent", battery_data_and_event_handler.tablet_charging_status.battery_below_50_percent);
        gateway_ping.addProperty("charge_status_valid_reading", battery_data_and_event_handler.tablet_charging_status.valid_updated_tablet_charge_status);
        gateway_ping.addProperty("tablet_charged_within_peroid_status", battery_data_and_event_handler.tablet_charging_status.tablet_charged_within_period);
        gateway_ping.addProperty("last_time_charged_timestamp", battery_data_and_event_handler.tablet_charging_status.last_charged_time);

        return gateway_ping.toString();
    }


    private void reportFeaturesEnabledToServer()
    {
        server_syncing.sendFeaturesEnabledStatusIfServerConnected();
    }



    private void reportWebserviceResult(boolean result, HttpOperationType http_operation_type, ActiveOrOldSession active_or_old_session, ServerSyncStatus server_sync_status)
    {
        // Update UI
        patient_gateway_outgoing_commands.reportServerResponse(result, http_operation_type, active_or_old_session, server_sync_status);
/*
        // Update WAMP
        try
        {
            if(server_syncing.isRealTimeServerConnected())
            {
                JSONObject sync_status = new JSONObject();

                sync_status.put("server_webservice_result", result);
                sync_status.put("http_operation_type", http_operation_type);
                sync_status.put("active_or_old_session_as_int", active_or_old_session.ordinal());
                sync_status.put("active_or_old_session", active_or_old_session);

                String json_payload = addSessionInfoToJsonObject(sync_status, server_sync_status);

                pubSubConnection.sendWebserviceResult(json_payload);
            }
        }
        catch (JSONException e1)
        {
            e1.printStackTrace();
        }
*/
    }


    public void reportDatabaseStatus(ServerSyncStatus server_sync_status)
    {
        // Update UI
        patient_gateway_outgoing_commands.reportDatabaseStatus(server_sync_status);

        // Update Server
        server_syncing.sendDatabaseRowsPendingToServer(server_sync_status);
    }


    private void endExistingSession(int session_ended_by_user_id, boolean turn_devices_off, long session_end_time)
    {
        Log.d(TAG, "endExistingSession : End session time = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(session_end_time));

        // Moved this here, so it'll be false when removing the devices below
        patient_session_info.patient_session_running = false;

        // Cancelling Spo2 spot measurements, otherwise they might arrive after the session has finished when device_session_number is -1
        cancelSpo2SpotMeasurementsIfRunning(device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2));

        // Stop Device Periodic Mode from running as there is no session now
        if (gateway_settings.getPeriodicModeEnabledStatus())
        {
            device_periodic_mode.disable();
        }

        for (DeviceInfo device_info : device_info_manager.getListOfSensorDeviceInfoObjects())
        {
            exitRawAccelerometerModeIfRunning(device_info);

            exitSetupModeIfRunningAndDisconnectFromDevice(device_info, session_end_time, session_ended_by_user_id, turn_devices_off);
        }

        device_info_manager.getBluetoothLeDeviceController().endSessionStopAllScans();

        for(DeviceInfo device_info : device_info_manager.getListOfNonSensorDeviceInfoObjects())
        {
            endDeviceSessionAndExportCsvIfNeeded(device_info, session_end_time, session_ended_by_user_id);

            resetDeviceInfoAndReportToUi(device_info);
        }

        device_sessions.clear();

        early_warning_score_processor.reset();

        if (long_term_measurement_validity_tracker != null)
        {
            long_term_measurement_validity_tracker.reset();
        }


        if (gateway_settings.getCsvOutputEnabledStatus())
        {
            csv_export.exportManuallyEnteredHeartRateData(patient_session_info.android_database_patient_session_id, patient_session_info.start_session_time, session_end_time, "manually_entered_heart_rate_data");
            csv_export.exportManuallyEnteredRespirationRateData(patient_session_info.android_database_patient_session_id, patient_session_info.start_session_time, session_end_time, "manually_entered_respiration_rate_data");
            csv_export.exportManuallyEnteredTemperatureData(patient_session_info.android_database_patient_session_id, patient_session_info.start_session_time, session_end_time, "manually_entered_temperature_data");
            csv_export.exportManuallyEnteredSpO2Data(patient_session_info.android_database_patient_session_id, patient_session_info.start_session_time, session_end_time, "manually_entered_spo2_data");
            csv_export.exportManuallyEnteredBloodPressureData(patient_session_info.android_database_patient_session_id, patient_session_info.start_session_time, session_end_time, "manually_entered_blood_pressure");
            csv_export.exportManuallyEnteredWeightData(patient_session_info.android_database_patient_session_id, patient_session_info.start_session_time, session_end_time, "manually_entered_weight");
            csv_export.exportManuallyEnteredConsciousnessLevelData(patient_session_info.android_database_patient_session_id, patient_session_info.start_session_time, session_end_time, "manually_entered_consciousness_level");
            csv_export.exportManuallyEnteredSupplementalOxygenData(patient_session_info.android_database_patient_session_id, patient_session_info.start_session_time, session_end_time, "manually_entered_supplemental_oxygen");
            csv_export.exportManuallyEnteredAnnotationData(patient_session_info.android_database_patient_session_id, patient_session_info.start_session_time, session_end_time, "manually_entered_annotation");
        }

        local_database_storage.endPatientSession(patient_session_info.android_database_patient_session_id, session_end_time, session_ended_by_user_id);

        local_database_storage.incrementNumberOfUnsyncedHistoricalPatientSessions();

        patient_session_info.android_database_patient_session_id = PatientSessionInfo.INVALID_PATIENT_SESSION;

        patient_session_info.server_patient_session_id = PatientSessionInfo.INVALID_PATIENT_SESSION;

        removeDeviceMeasurementProgressBarTimer();

        patient_info = new PatientInfo();
        patient_gateway_outgoing_commands.reportHospitalPatientID(patient_info.getHospitalPatientId());
        patient_gateway_outgoing_commands.reportPatientThresholdSet(patient_info);

        // Let the UI know the new device session numbers
        patient_gateway_outgoing_commands.reportGatewaySessionNumbers(patient_session_info.android_database_patient_session_id, device_sessions);

        reportGatewayStatusToServer();

        deleteCurrentSessionFile();


        device_info_manager.resetAll();

    }



    private void emptyLocalDatabase(boolean delete_ews_threshold_sets)
    {
        firmware_image_manager.clearLatestStoredFirmwareVersions();

        local_database_storage.resetDatabase(delete_ews_threshold_sets);

        if (delete_ews_threshold_sets)
        {
            showOnScreenMessage(getString(R.string.local_database_reset_including_ews_thresholds));
        }
        else
        {
            showOnScreenMessage(getString(R.string.local_database_reset_not_including_ews_thresholds));
        }

        patient_gateway_outgoing_commands.reportDatabaseEmptied(delete_ews_threshold_sets);
    }


    public void enableDisableServerSyncing(boolean server_sync_enabled)
    {
        Log.d(TAG, "enableDisableServerSyncing : " + server_sync_enabled);

        if(!gateway_settings.isGatewayAssignedWardAndBedSet())
        {
            Log.d(TAG, "server_sync_enabled set to false as bed ID not set yet");
            server_sync_enabled = false;
        }

        gateway_settings.storeServerSyncEnableStatus(server_sync_enabled);

        if (server_sync_enabled)
        {
            Log.d(TAG, "enableDisableServerSyncing : Calling resetDatabaseFailedToSendStatus");
            server_syncing.resetDatabaseFailedToSendStatusAndResetTimer();
        }

        patient_gateway_outgoing_commands.reportServerSyncEnableStatus();

        reportGatewayStatusToServer();
    }


    public void enableDisableRealTimeLink(boolean real_time_link_enabled)
    {
        Log.d(TAG, "real_time_link_enabled = " + real_time_link_enabled);

//        // ToDo: might need to leave this ON as we need the link open to get beds and wards, potentially...
//        if(!gateway_settings.isGatewayAssignedWardAndBedSet())
//        {
//            Log.d(TAG, "real_time_link_enabled set to false as bed ID not set yet");
//
//            real_time_link_enabled = false;
//        }

        gateway_settings.storeRealTimeLinkEnableStatus(real_time_link_enabled);

        patient_gateway_outgoing_commands.reportRealTimeLinkEnableStatus();

        if(real_time_link_enabled == false)
        {
            // turn off data syncing if server link is off
            gateway_settings.storeServerSyncEnableStatus(false);

            patient_gateway_outgoing_commands.reportServerSyncEnableStatus();
        }

        server_syncing.configureRealTimeServerIfRequired();

        reportGatewayStatusToServer();
    }


    private void enablePatientNameLookup(boolean patient_name_lookup_enable_status)
    {
        Log.d(TAG, "enablePatientNameLookup = " + patient_name_lookup_enable_status);

        gateway_settings.storePatientNameLookupEnabledStatus(patient_name_lookup_enable_status);

        patient_name_lookup_enable_status = gateway_settings.getPatientNameLookupEnabledStatus();

        patient_gateway_outgoing_commands.reportPatientNameLookupEnableStatus(patient_name_lookup_enable_status);

        reportGatewayStatusToServer();
    }

    private void enablePredefinedAnnotations(boolean enable_pre_defined_annotations)
    {
        Log.d(TAG, "enablePredefinedAnnotations = " + enable_pre_defined_annotations);

        gateway_settings.storeEnablePredefinedAnnotations(enable_pre_defined_annotations);

        enable_pre_defined_annotations = gateway_settings.getEnablePredefineAnnotations();

        patient_gateway_outgoing_commands.reportPredefinedAnnotationEnabledStatus(enable_pre_defined_annotations);

        reportGatewayStatusToServer();
    }


    private IntentFilter makeDfuUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LifetouchDfu.BROADCAST_PROGRESS);
        intentFilter.addAction(LifetouchDfu.BROADCAST_DFU_UPLOAD_COMPLETE);
        intentFilter.addAction(LifetouchDfu.BROADCAST_ERROR);
        return intentFilter;
    }


    private final BroadcastReceiver mDfuUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            final String action = intent.getAction();
            final int progress = intent.getIntExtra(LifetouchDfu.EXTRA_DATA, 0);
            final DeviceType device_type = DeviceType.values()[intent.getIntExtra(LifetouchDfu.DEVICE_TYPE, 0)];

            DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(device_type);

            switch (action)
            {
                case LifetouchDfu.BROADCAST_PROGRESS:
                    logDfuProgress(device_info, progress);
                    device_info.dfuProgressReceived(getNtpTimeNowInMilliseconds());
                    break;

                case LifetouchDfu.BROADCAST_DFU_UPLOAD_COMPLETE:
                    Log.e(TAG, "BROADCAST_DFU_UPLOAD_COMPLETE");

                    // Firmware update has finished
                    firmware_image_manager.setFirmwareUpdatePending(device_info.device_type, false);
                    device_info.dfuComplete();
                    break;

                case LifetouchDfu.BROADCAST_ERROR:
                    Log.e(TAG, "mDfuUpdateReceiver Error : " + progress);
                    break;
            }
        }
    };


    private void logDfuProgress(DeviceInfo device_info, final int progress)
    {
        /*
        switch (progress)
        {
            case DfuService.PROGRESS_CONNECTING:
                Log.d(TAG, "logDfuProgress PROGRESS_CONNECTING: " + progress);
                break;
            case DfuService.PROGRESS_STARTING:
                Log.d(TAG, "logDfuProgress PROGRESS_STARTING: " + progress);
                break;
            case DfuService.PROGRESS_VALIDATING:
                Log.d(TAG, "logDfuProgress PROGRESS_VALIDATING: " + progress);
                break;
            case DfuService.PROGRESS_DISCONNECTING:
                Log.d(TAG, "logDfuProgress PROGRESS_DISCONNECTING: " + progress);
                break;
            case DfuService.PROGRESS_COMPLETED:
                Log.d(TAG, "logDfuProgress PROGRESS_COMPLETED: " + progress);
                break;
            default:
                Log.d(TAG, "logDfuProgress : " + progress + "%");
                break;
        }
        */
        Log.d(TAG, "logDfuProgress : " + device_info.getSensorTypeAndDeviceTypeAsString() + " = " + progress + "%");

        patient_gateway_outgoing_commands.reportDfuProgress(device_info, progress);
    }


    private void reportSetupModeEnabledStateToServer(int server_patient_session_id, DeviceInfo device_info, boolean enabled)
    {
        if (gateway_settings.getRealTimeLinkEnabledStatus())
        {
            server_syncing.sendDeviceSetupModeEnabledState(server_patient_session_id, device_info.sensor_type, enabled);
        }
    }


    private void processTimestampForSetupModeLogEntry(DeviceInfo device_info, long timestamp)
    {
        if (timestamp < device_info.set_mode_log_entry.start_time)
        {
            device_info.set_mode_log_entry.start_time = timestamp;
            device_info.set_mode_log_entry.end_time = timestamp;

            // Write the Start Setup Mode Log
            device_info.current_setup_mode_log_database_id = local_database_storage.storeStartSetupMode(device_info.sensor_type, device_info.device_type, device_info.getAndroidDeviceSessionId(), timestamp);
        }

        if (timestamp > device_info.set_mode_log_entry.end_time)
        {
            device_info.set_mode_log_entry.end_time = timestamp;
        }
    }


    private void enterDeviceSetupMode(DeviceInfo device_info, DeviceInfo.OperatingMode desired_operating_mode)
    {
        enterDeviceSetupMode(device_info, desired_operating_mode, true);
    }


    private void enterDeviceSetupMode(DeviceInfo device_info, DeviceInfo.OperatingMode desired_operating_mode, boolean send_start_setup_mode_command)
    {
        enterDeviceSetupMode(device_info, desired_operating_mode, send_start_setup_mode_command, gateway_settings.getSetupModeTimeInSeconds());
    }


    private void enterDeviceSetupMode(final DeviceInfo device_info, DeviceInfo.OperatingMode desired_operating_mode, boolean send_start_setup_mode_command, int setup_mode_time_in_seconds)
    {
        Log.d(TAG, "enterDeviceSetupMode " + device_info.getSensorTypeAndDeviceTypeAsString()
                + " : desired_operating_mode = " + desired_operating_mode
                + " : send_start_setup_mode_command = " + send_start_setup_mode_command
                + " : current_setup_mode_log_database_id = " + device_info.current_setup_mode_log_database_id
                + " : setup_mode_time_in_seconds = " + setup_mode_time_in_seconds
        );

        exitRawAccelerometerModeIfRunning(device_info);

        device_info.setOperatingMode(desired_operating_mode);

        device_info.createSetupModeLogEntry();

        if(device_info.dummy_data_mode)
        {
            dummy_data_instance.enableDummyDeviceSetupMode(device_info);
        }
        else
        {
            if (send_start_setup_mode_command)
            {
                boolean khz_setup_mode_enabled = gateway_settings.getLT3KHzSetupModeEnabledStatus();
                device_info_manager.enableSetupMode(device_info.device_type, true, khz_setup_mode_enabled);
            }
        }

        reportSetupModeEnabledStateToServer(patient_session_info.server_patient_session_id, device_info, true);

        if (desired_operating_mode != DeviceInfo.OperatingMode.PERIODIC_SETUP_MODE)
        {
            device_info.resetDeviceInfoSetupModeTimer();

            device_info.setup_mode_time_left_in_seconds = setup_mode_time_in_seconds;

            // Create a timeout here for X seconds/minutes after which it calls stopSetupMode
            device_info.getSetupModeTimeoutHandler().postDelayed(() -> decrementSetupModeTimer(device_info), DateUtils.SECOND_IN_MILLIS);
        }

        if (send_start_setup_mode_command)
        {
            // Only tell the UI to switch to Setup Mode fragment if its not already on the screen
            patient_gateway_outgoing_commands.sendCommandReportDeviceOperatingMode(device_info);
        }

        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
    }


    private void decrementSetupModeTimer(final DeviceInfo device_info)
    {
        boolean device_in_continuous_setup_mode = device_info.isDeviceInContinuousSetupMode();

        Log.i(TAG, device_info.device_type + " device_info.setup_mode_time_left_in_seconds = " + device_info.setup_mode_time_left_in_seconds + " : device_in_continuous_setup_mode = " + device_in_continuous_setup_mode);

        if ((device_info.setup_mode_time_left_in_seconds > 0) && (device_in_continuous_setup_mode == false))
        {
            device_info.setup_mode_time_left_in_seconds--;
        }

        if (device_info.setup_mode_time_left_in_seconds == 0)
        {
            device_info.resetDeviceInfoSetupModeTimer();

            Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " setup mode finished. Calling exitSetupModeIfRunning()");
            exitSetupModeIfRunning(device_info);
        }
        else
        {
            device_info.getSetupModeTimeoutHandler().postDelayed(() -> decrementSetupModeTimer(device_info), DateUtils.SECOND_IN_MILLIS);
        }
    }


    private void exitDeviceSetupMode(DeviceInfo device_info, boolean send_exit_setup_mode_command)
    {
        exitDeviceSetupMode(device_info, send_exit_setup_mode_command, true);
    }


    private void exitDeviceSetupMode(DeviceInfo device_info, boolean send_exit_setup_mode_command, boolean stop_setup_mode_timer_running)
    {
        Log.d(TAG, "exitDeviceSetupMode " + device_info.getSensorTypeAndDeviceTypeAsString() +
                " : current_setup_mode_log_database_id = " + device_info.current_setup_mode_log_database_id +
                " : send_exit_setup_mode_command = " + send_exit_setup_mode_command +
                " : end_time = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(device_info.set_mode_log_entry.end_time) +
                " : setup_mode_time_left_in_seconds = " + device_info.setup_mode_time_left_in_seconds +
                " : stop_setup_mode_timer_running = " + stop_setup_mode_timer_running
        );

        if (stop_setup_mode_timer_running)
        {
            // Stop the timeout handler in case the user has requested to exit Setup Mode before the timeout fires
            device_info.resetDeviceInfoSetupModeTimer();
        }

        if(device_info.dummy_data_mode)
        {
            dummy_data_instance.stopDeviceSetupMode(device_info.sensor_type);
        }
        else
        {
            if (send_exit_setup_mode_command)
            {
                device_info_manager.enableSetupMode(device_info.device_type, false);
            }
        }

        // If Nonin exiting setup mode, send remaining setup mode data
        if (device_info.sensor_type == SensorType.SENSOR_TYPE__SPO2 && device_info.queue_setup_mode_datapoints_for_server.size() > 0)
        {
            if (device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX)
            {
                // Scale the original Nonin Setup data up to 16 bit.
                server_syncing.sendDeviceSetupModeData(patient_session_info.server_patient_session_id, device_info, 256);
            }
            else if (device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE)
            {
                server_syncing.sendDeviceSetupModeData(patient_session_info.server_patient_session_id, device_info, 1);
            }
            else if (device_info.device_type == DeviceType.DEVICE_TYPE__MEDLINKET)
            {
                // Scale the original Medlinket Setup data up to 16 bit.
                server_syncing.sendDeviceSetupModeData(patient_session_info.server_patient_session_id, device_info, 512);
            }
        }

        device_info.queue_setup_mode_datapoints_for_server.clear();

        device_info.setOperatingMode(DeviceInfo.OperatingMode.NORMAL_MODE);

        reportSetupModeEnabledStateToServer(patient_session_info.server_patient_session_id, device_info, false);

        if (send_exit_setup_mode_command)
        {
            // Only tell the UI to switch to Normal Mode fragment if its not already on the screen
            patient_gateway_outgoing_commands.sendCommandReportDeviceOperatingMode(device_info);
        }

        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

        // If for unknown reason there is No Start (i.e. no data has been received from the device....so no Start Log has been written) then do not write the End Log
        if (device_info.set_mode_log_entry.start_time != SETUP_MODE_LOG_INITIAL_START_TIME)
        {
            local_database_storage.storeExitSetupMode(device_info.current_setup_mode_log_database_id, device_info.set_mode_log_entry.end_time);

            Log.d(TAG, "sendCommandReportDeviceSetupModeLog " + device_info.getSensorTypeAndDeviceTypeAsString() +
                    " : start_time = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(device_info.set_mode_log_entry.start_time) +
                    " : end_time = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(device_info.set_mode_log_entry.end_time)
            );

            patient_gateway_outgoing_commands.sendCommandReportDeviceSetupModeLog(device_info);
        }
    }


    private void enterDeviceRawAccelerometerMode(BtleSensorDevice device_info, DeviceInfo.OperatingMode operating_mode)
    {
        exitSetupModeIfRunning(device_info);

        device_info.setOperatingMode(operating_mode);

        if(device_info.dummy_data_mode)
        {
            dummy_data_instance.enableDummyDeviceRawAccelerometerMode(device_info);
        }
        else
        {
            device_info.enableRawAccelerometerMode(true);
        }

        server_syncing.sendDeviceRawAccelerometerModeEnabledStateIfServerConnected(patient_session_info.server_patient_session_id, device_info, true);

        patient_gateway_outgoing_commands.sendCommandReportDeviceOperatingMode(device_info);
    }


    private void exitDeviceRawAccelerometerMode(BtleSensorDevice device_info, boolean send_exit_raw_accelerometer_mode_command)
    {
        if(device_info.dummy_data_mode)
        {
            dummy_data_instance.stopDeviceRawAccelerometerMode(device_info.sensor_type);
        }
        else
        {
            if (send_exit_raw_accelerometer_mode_command)
            {
                device_info.enableRawAccelerometerMode(false);
            }
        }

        queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.clear();

        device_info.setOperatingMode(DeviceInfo.OperatingMode.NORMAL_MODE);

        server_syncing.sendDeviceRawAccelerometerModeEnabledStateIfServerConnected(patient_session_info.server_patient_session_id, device_info, false);

        patient_gateway_outgoing_commands.sendCommandReportDeviceOperatingMode(device_info);

        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
    }


    private void exitApplicationAndStopService()
    {
        if(bound_to_logcat_capture_service)
        {
            Log.d(TAG, "Stopping the PatientGateway logger service");

            boolean is_service_Stopped = stopService(new Intent(getAppContext(), PatientGatewayLogcatCaptureService.class));

            if(is_service_Stopped)
            {
                Log.d(TAG, "exitApplicationAndStopService : Successfully stopped Logcat service");
            }
            else
            {
                Log.d(TAG, "exitApplicationAndStopService : Logcat service isn't stopped successfully");
            }

            bound_to_logcat_capture_service = false;
        }
        else
        {
            Log.w(TAG, "ALERT ALERT!!! Log service is already killed. ");
        }

        stopForeground(true);

        deleteCurrentSessionFile();

        // Stop PatientGateway service
        stopSelf();
    }


    private void endDeviceSessionAndExportCsvIfNeeded(DeviceInfo device_info, long end_time, int ended_by_user_id)
    {
        if (device_info.isDeviceSessionInProgress())
        {
            device_info.device_session_end_time = end_time;
            exportCsv(device_info);

            local_database_storage.endDeviceSession(device_info.getAndroidDeviceSessionId(), end_time, ended_by_user_id);
        }
    }


    private void resetDeviceInfoAndReportToUi(DeviceInfo device_info)
    {
        device_info.resetAsNew();

        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
    }


    private void removeEarlyWarningScoreDevice(long device_session_end_time, int device_session_ended_by_user_id)
    {
        DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE);

        device_info.setDeviceTypePartOfPatientSession(false);

        if (device_info.isDeviceSessionInProgress())
        {
            endDeviceSessionAndExportCsvIfNeeded(device_info, device_session_end_time, device_session_ended_by_user_id);
        }

        resetDeviceInfoAndReportToUi(device_info);

        patient_gateway_outgoing_commands.reportGatewaySessionNumbers(patient_session_info.android_database_patient_session_id, device_sessions);
    }


    public void startUserInterface()
    {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.isansys.pse_isansysportal");

        if (launchIntent != null)
        {
            Log.i(TAG, "Restarting User Interface");
            startActivity(launchIntent);
        }
        else
        {
            Log.e(TAG, "No User Interface found");
        }
    }


    private void unpairAllBluetoothDevices()
    {
        Log.e(TAG, "unpairAllBluetoothDevices start");

        bluetooth_pairing = new BluetoothPairing(this, Log);
        bluetooth_pairing.unpairAllPairedDevices();

        Log.e(TAG, "unpairAllBluetoothDevices end");
    }


    private int patient_gateway_service_timer_tick_counter = 0;
    private int patient_gateway_service_timer_minute_counter = 0;
    private int database_deletion_timer_tick_counter = 0;

    private final long NUMBER_OF_TICKS_BEFORE_DATABASE_DELETION = 6 * 60 * 60; // six hours
    private final double MEGABYTE = 1048576;

    private void logProcessHeapSize()
    {
        Double allocated = (double) Debug.getNativeHeapAllocatedSize() / MEGABYTE;
        Double available = (double) Debug.getNativeHeapSize() / MEGABYTE;
        Double free = (double) Debug.getNativeHeapFreeSize() / MEGABYTE;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.i(TAG, "heap - native: allocated: " + df.format(allocated) + "MB of total size " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        Log.i(TAG, "heap - Memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime().totalMemory()/MEGABYTE)) + "MB of max-heap " + df.format(Double.valueOf(Runtime.getRuntime().maxMemory()/MEGABYTE))+ "MB (" + df.format(Double.valueOf(Runtime.getRuntime().freeMemory()/MEGABYTE)) +"MB is free)");
    }


    private void logTotalMemory()
    {
        ActivityManager activityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(mi);

        double availableMegs = mi.availMem / MEGABYTE;
        double totalMegs = mi.totalMem / MEGABYTE;
        double usedMegs = totalMegs - availableMegs;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.i(TAG, "Total memory used: " + df.format(usedMegs) + "MB of total size " + df.format(totalMegs) + "MB (" + df.format(availableMegs) + "MB free)");
        Log.i(TAG, "Low memory situation = " + mi.lowMemory);
    }

/*
    private int number_of_heap_dump_operations = 0;

    private class DumpHeapProfileInBackground extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            number_of_heap_dump_operations++;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Log.d(TAG, "DumpHeapProfileInBackground : starting AsyncTask. Number of operations = " + number_of_heap_dump_operations);

            dumpHeapFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            number_of_heap_dump_operations--;
            super.onPostExecute(aVoid);
        }
    }


    private void dumpHeapFile()
    {
        try
        {
            String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
            String heap_directory = "/HeapDump/PatientGateway/";
            String full_heap_directory_path = SD_CARD_PATH + heap_directory;

            File folder_files = new File(full_heap_directory_path);
            if(!folder_files.exists())
            {
                folder_files.mkdirs();
            }

            if(folder_files != null)
            {
                File[] file_list = folder_files.listFiles();

                if(file_list != null)
                {
                    Log.d(TAG, "dumpHeapFile : folder length = " + file_list.length);

                    // Delete all the previous files
                    int deleted_file_count = 0;
                    for(File file_f : file_list)
                    {
                        file_list[deleted_file_count].delete();
                        deleted_file_count++;
                    }

                    Log.d(TAG, "dumpHeapFile : Total files deleted = " + deleted_file_count);

//                    if(file_list.length > 10)
//                    {
//                        Arrays.sort(file_list, new Comparator<File>()
//                        {
//                            @Override
//                            public int compare(File lhs, File rhs)
//                            {
//                                return Long.valueOf(lhs.lastModified()).compareTo(rhs.lastModified());
//                            }
//
//                        });
//
//
//
//
//// Code to delete more than 10 hprof files
////                        int total_files = file_list.length;
////                        int deleted_file_count = 0;
////
////                        while(total_files > 10)
////                        {
////                            total_files--;
////
////                            Log.d(TAG, "dumpHeapFile : File to be deleted = " + file_list[deleted_file_count].getName());
////
////                            file_list[deleted_file_count].delete();
////
////                            deleted_file_count++;
////                        }
//
//                        Log.d(TAG, "dumpHeapFile : Total files deleted = " + deleted_file_count);
//                    }
                }
            }

            File total_dir = new File(full_heap_directory_path);

            String current_time =  TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(getNtpTimeNowInMilliseconds());

            String fileName = "patientGatewayHeap_" + current_time + ".hprof";

            File gateway_hprog = new File(total_dir, fileName);

            Debug.dumpHprofData(gateway_hprog.getAbsolutePath());

            Log.d(TAG, "dumpHeapFile : file name is " + fileName);
        }
        catch (Exception e)
        {
            Log.d(TAG, "dumpHeapFile : Exception e = " + e.toString());
        }
    }
*/

    void showBondedBluetoothDevices()
    {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null)
        {
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if (pairedDevices != null)
            {
                Log.d(TAG, "Number of bonded bluetooth devices = " + pairedDevices.size());
                if (pairedDevices.size() > 0)
                {
                    for (BluetoothDevice d: pairedDevices)
                    {
                        String btID = String.format("   Bluetooth device [%s]  type: %s", d.getName(), d.getType());
                        Log.d(TAG, btID);
                    }
                }
            }
        }
    }


    private void onPatientGatewayServiceTimerTick()
    {
        Log.d(TAG, "onPatientGatewayServiceTimerTick : Time = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(getNtpTimeNowInMilliseconds()));

        patient_gateway_service_timer_tick_counter++;

        if(patient_gateway_service_timer_tick_counter % 10 == 0)
        {
            showBondedBluetoothDevices();

            // Do things that we want to happen every 10 seconds
            logProcessHeapSize();

            logTotalMemory();

            System.gc();

            bluetooth_reset_manager.checkForDeadBluetooth();

            checkForInstallationQrCodeTextFile();
        }

        // Is  a session running?
        if (patient_session_info.android_database_patient_session_id != PatientSessionInfo.INVALID_PATIENT_SESSION)
        {
            // Is Dummy Data Mode enabled?
            if (dummy_data_instance.isDummyDataModeEnabled())
            {
                // Do Dummy Data Mode work
                dummy_data_instance.tick();
            }
        }


        // UnComment the following code to test the Bulk insert in Local Database
//            if((device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__LIFETOUCH).night_mode_enabled == true) && (device_info_manager.isDeviceTypeHumanReadableDeviceIdValid(DeviceType.DEVICE_TYPE__LIFETOUCH)))
//            {
//                BulkInsertTestCode.getBulkInsertTestCodeInstance().flushSinWaveSetupModeQueueToDatabase();
//            }

        // Setup Mode database writing is done here every second instead of when the Setup Mode sample is received, as there is a time overhead of writing individual records to the database.
        // This way we write up to a seconds worth of Setup mode data at a time
        for (DeviceInfo device_info : device_info_manager.getListOfSensorDeviceInfoObjectsThatSupportSetupMode())
        {
            flushSetupModeQueueToDatabase(device_info);
        }

        if (nonin_playback_expected)
        {
            Log.d(TAG, "PG sending NONIN_WRIST_OX__GATEWAY_READY_FOR_NEXT_PLAYBACK_IF_AVAILABLE ? ");

            Intent readyForNextPlaybackIntent = new Intent();
            readyForNextPlaybackIntent.setAction(NoninWristOxPlaybackDecoder.NONIN_WRIST_OX__GATEWAY_READY_FOR_NEXT_PLAYBACK_IF_AVAILABLE);
            sendBroadcastIntent(readyForNextPlaybackIntent);
        }

        // Raw Accelerometer Mode database writing is done here every second instead of when the Raw Accelerometer Mode sample is received, as there is a time overhead of writing individual records to the database.
        // This way we write up to a seconds worth of Raw Accelerometer mode data at a time
        flushLifetouchRawAccelerometerModeQueueToDatabase();

        
        if (patient_gateway_service_timer_tick_counter == 60)
        {
            // Do events that happen every minute
            patient_gateway_service_timer_minute_counter++;

            patient_gateway_service_timer_tick_counter = 0;

            flushLifetouchHeartBeatQueueToDatabase();

            flushPulseOxIntermediateQueueToDatabase();

            if (inGsmOnlyMode())
            {
                // Nothing to do
            }
            else
            {
                dumpWifiStatusToLog();
            }

            if (patient_gateway_service_timer_minute_counter == 60)
            {
                // Do events that happen every hour
                patient_gateway_service_timer_minute_counter = 0;

                if (serverLinkSetupConnectedAndSyncingEnabled())
                {
                    Log.d(TAG, "Checking Server for updated device firmware images (hourly)");
                    server_syncing.sendGetLatestDeviceFirmwareVersionsRequest();
                }
            }
        }

        // Start deletion procedure every SIX hour.
        if(database_deletion_timer_tick_counter >= NUMBER_OF_TICKS_BEFORE_DATABASE_DELETION)
        {
            deleteOldFullySyncedSessions();
        }
        else
        {
            database_deletion_timer_tick_counter++;
        }

        if ((device_periodic_mode != null) && (device_periodic_mode.isEnabled()))
        {
            device_periodic_mode.tick();
        }

        setup_wizard.startNextStepIfPending();
    }


    public void deleteOldFullySyncedSessions()
    {
        database_deletion_timer_tick_counter = 0;

        // Check to see if any of the previous sessions have been fully synced to the Server. This sets the AllSynced flag at the Server and means we can delete that session from the Gateway to save space
        local_database_storage.deleteOldFullySyncedSessions(isPatientSessionRunning());
    }


    private volatile static boolean lifetouch_heartbeat_queue_update_in_progress = false;
    private synchronized void flushLifetouchHeartBeatQueueToDatabase()
    {
        // Get current number of heart beats to store
        int total_number_of_datapoints_in_array = queue_lifetouch_heart_beats_datapoints.size();
        int i = 0;

        HeartBeatInfo[] data_points = queue_lifetouch_heart_beats_datapoints.toArray(new HeartBeatInfo[total_number_of_datapoints_in_array]);

        try
        {
            // Are there any Heart Beats data points to write to the database.
            if((total_number_of_datapoints_in_array > 0) && (lifetouch_heartbeat_queue_update_in_progress == false))
            {
                final int number_of_row_to_update = data_points.length;

                lifetouch_heartbeat_queue_update_in_progress = true;

                DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);

                // Write these to the database
                local_database_storage.storeLifetouchHeartBeatMeasurements(device_info.getAndroidDeviceSessionId(),
                        device_info.human_readable_device_id,
                        data_points,
                        new LocalDatabaseStorage.BulkInsertCallBack()
                        {
                            @Override
                            public void onInsertFinish(int update_row)
                            {
                                // Check actual number of data written to database
                                if (update_row > 0)
                                {
                                    Log.d(TAG, "BulkInsertTask : flushLifetouchHeartBeatQueueToDatabase : Expected number_of_datapoints_to_store = "
                                            + number_of_row_to_update +  ".. updated row = " + update_row + ". Previous queue_lifetouch_heart_beats_datapoints = " + queue_lifetouch_heart_beats_datapoints.size());

                                    // "update_row" is confirmed number of data written to server
                                    // Remove data from the head of ArrayList
                                    for(int i = 0; i<update_row; i++)
                                    {
                                        queue_lifetouch_heart_beats_datapoints.poll();
                                    }

                                    lifetouch_heartbeat_queue_update_in_progress = false;

                                    Log.d(TAG, "BulkInsertTask : Current queue_lifetouch_heart_beats_datapoints = " + queue_lifetouch_heart_beats_datapoints.size());
                                }
                                else
                                {
                                    Log.e(TAG,"BulkInsertTask : flushLifetouchHeartBeatQueueToDatabase : ALERT ALERT... All data not updated");
                                }
                            }
                        });
            }
        }
        catch(Exception e)
        {
            // Log Exception
            Log.e(TAG, "flushLifetouchHeartBeatQueueToDatabase ********** WEIRD pollFirst() BUG **********************");
            Log.e(TAG, e.toString());
            Log.e(TAG, "i = " + i + ". number_of_heart_beats_to_store = " + total_number_of_datapoints_in_array);
            Log.e(TAG, "Clearing the Lifetouch heart beat list to be on the safe side");

            lifetouch_heartbeat_queue_update_in_progress = false;

            queue_lifetouch_heart_beats_datapoints.clear();
        }
    }


    private synchronized void flushSetupModeQueueToDatabase(final DeviceInfo device_info)
    {
        try
        {
            // Are there any Setup Mode data points to write to the database.
            if ((device_info.queue_setup_mode_datapoints.size() > 0) && (device_info.setup_mode_database_write_in_progress == false))
            {
                Log.e(TAG, "flushSetupModeQueueToDatabase : Pending = " + device_info.queue_setup_mode_datapoints.size());

                // This will avoid multiple creation of bulk insert async task
                device_info.setup_mode_database_write_in_progress = true;

                // Store Setup Mode Datapoints in database
                local_database_storage.storeSetupModeSamples(device_info, device_info.queue_setup_mode_datapoints,
                        new LocalDatabaseStorage.BulkInsertCallBack()
                        {
                            @Override
                            public void onInsertFinish(int rows_updated)
                            {
                                // Check correct number of samples written to database
                                if (rows_updated > 0)
                                {
                                    Log.d(TAG, "BulkInsertTask flushSetupModeQueueToDatabase : Written " + rows_updated + ". Pending = " + device_info.queue_setup_mode_datapoints.size());

                                    // "rows_updated" is confirmed number of data written to server
                                    // Remove data from the head of ConcurrentLinkedQueue
                                    for (int i = 0; i < rows_updated; i++)
                                    {
                                        device_info.queue_setup_mode_datapoints.poll();
                                    }

                                    Log.d(TAG, "BulkInsertTask flushSetupModeQueueToDatabase : Removed " + rows_updated + " : Pending = " + device_info.queue_setup_mode_datapoints.size());

                                    device_info.setup_mode_database_write_in_progress = false;
                                }
                                else
                                {
                                    Log.e(TAG, "BulkInsertTask flushSetupModeQueueToDatabase : ALERT ALERT... All data not updated");
                                }
                            }
                        }
                );
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            Log.e(TAG, "Clearing device_info.queue_setup_mode_datapoints to be on the safe side");

            device_info.setup_mode_database_write_in_progress = false;
            device_info.queue_setup_mode_datapoints.clear();
        }
    }


    private volatile static boolean lifetouch_raw_accelerometer_mode_DB_update_inProgress = false;
    private synchronized void flushLifetouchRawAccelerometerModeQueueToDatabase()
    {
        // Get current number of data points to store
        int total_number_of_datapoints_in_array = queue_lifetouch_raw_accelerometer_mode_datapoints.size();         // Get a snapshot of the current size. This is updating in real time
        int i = 0;

        RawAccelerometerModeDataPoint[] data_points = queue_lifetouch_raw_accelerometer_mode_datapoints.toArray(new RawAccelerometerModeDataPoint[total_number_of_datapoints_in_array]);

        try
        {
            // Are there any Raw Accelerometer  Mode data points to write to the database.
            if ((total_number_of_datapoints_in_array > 0) && (lifetouch_raw_accelerometer_mode_DB_update_inProgress == false))
            {
                final int number_of_row_to_update = data_points.length;

                // This will avoid multiple creation of bulk insert async task
                lifetouch_raw_accelerometer_mode_DB_update_inProgress = true;

                DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);

                // Store Setup Mode Datapoints in database
                local_database_storage.storeLifetouchRawAccelerometerModeSamples(device_info.getAndroidDeviceSessionId(),
                        device_info.human_readable_device_id,
                        data_points,
                        new LocalDatabaseStorage.BulkInsertCallBack()
                        {
                            @Override
                            public void onInsertFinish(int update_row)
                            {
                                // Check correct number of samples written to database
                                if (update_row > 0)
                                {
                                    Log.d(TAG, "BulkInsertTask flushLifetouchRawAccelerometerModeQueueToDatabase : Expected number_of_datapoints_to_store = "
                                            + number_of_row_to_update +  ".. updated row = " + update_row + ". Previous queue_lifetouch_raw_accelerometer_mode_datapoints size = " + queue_lifetouch_raw_accelerometer_mode_datapoints.size());

                                    // "update_row" is confirmed number of data written to server
                                    // Remove data from the head of ArrayList
                                    for(int i = 0; i<update_row; i++)
                                    {
                                        queue_lifetouch_raw_accelerometer_mode_datapoints.poll();
                                    }

                                    Log.d(TAG, "BulkInsertTask : Current size of queue_lifetouch_raw_accelerometer_mode_datapoints = " + queue_lifetouch_raw_accelerometer_mode_datapoints.size());

                                    lifetouch_raw_accelerometer_mode_DB_update_inProgress = false;
                                }
                                else
                                {
                                    Log.e(TAG,"BulkInsertTask flushLifetouchRawAccelerometerModeQueueToDatabase : ALERT ALERT... All data not updated");
                                }
                            }
                        });
            }
        }
        catch(Exception e)
        {
            // Log Exception
            Log.e(TAG, "********** WEIRD pollFirst() BUG **********************");
            Log.e(TAG, e.toString());
            Log.e(TAG, "i = " + i + ". number_of_datapoints_to_store = " + total_number_of_datapoints_in_array);
            Log.e(TAG, "Clearing the Lifetouch raw accelerometer mode data linked list to be on the safe side");

            lifetouch_raw_accelerometer_mode_DB_update_inProgress = false;
            queue_lifetouch_raw_accelerometer_mode_datapoints.clear();
        }
    }


    private volatile static boolean pulse_ox_intermediate_queue_database_update_in_progress = false;
    private synchronized void flushPulseOxIntermediateQueueToDatabase()
    {
        // Get current number of heart beats to store
        int total_number_of_datapoints_in_array = pulse_ox_intermediate_measurements.size();
        int i = 0;

        IntermediateSpO2[] data_points = pulse_ox_intermediate_measurements.toArray(new IntermediateSpO2[total_number_of_datapoints_in_array]);

        try
        {
            // Are there any Setup Mode data points to write to the database.
            if((total_number_of_datapoints_in_array > 0) && (pulse_ox_intermediate_queue_database_update_in_progress == false))
            {
                final int number_of_row_to_update = data_points.length;

                pulse_ox_intermediate_queue_database_update_in_progress = true;

                DeviceInfo device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);

                local_database_storage.storeOximeterIntermediateMeasurements(device_info, data_points,
                        new LocalDatabaseStorage.BulkInsertCallBack()
                        {
                            @Override
                            public void onInsertFinish(int update_row)
                            {
                                if (update_row > 0)
                                {
                                    Log.d(TAG, "BulkInsertTask : pulse_ox_intermediate_measurements : Expected number_of_datapoints_to_store = "
                                            + number_of_row_to_update +  " .. Actual updated row =  " + update_row+ ". Previous pulse_ox_intermediate_measurements size = " + pulse_ox_intermediate_measurements.size());

                                    // Remove the row already written to the server
                                    for (int i=0; i<update_row; i++)
                                    {
                                        pulse_ox_intermediate_measurements.poll();
                                    }

                                    pulse_ox_intermediate_queue_database_update_in_progress = false;

                                    Log.d(TAG, "BulkInsertTask : Current pulse_ox_intermediate_measurements size = " + pulse_ox_intermediate_measurements.size());
                                }
                                else
                                {
                                    // Attempt to write next time
                                    Log.e(TAG, "BulkInsertTask : PROBLEM WRITING SPO2 INTERMEDIATES TO DATABASE. Row needed to be updated  " + number_of_row_to_update);
                                }
                            }
                        });
            }
        }
        catch(Exception e)
        {
            // Log Exception
            Log.e(TAG, "********** WEIRD pollFirst() BUG **********************");
            Log.e(TAG, e.toString());
            Log.e(TAG, "i = " + i + ". number_of_datapoints_to_store = " + total_number_of_datapoints_in_array);
            Log.e(TAG, "Clearing the Pulse Ox intermediate data linked list to be on the safe side");

            pulse_ox_intermediate_queue_database_update_in_progress = false;
            pulse_ox_intermediate_measurements.clear();
        }
    }


    private void dumpWifiStatusToLog()
    {
        Log.d(TAG, "Wifi status : enabled = " + wifi_event_manager.wifi_status.hardware_enabled + ". ssid = " + wifi_event_manager.wifi_status.ssid
                + ". ip_address_string = " + wifi_event_manager.wifi_status.ip_address_string + ". wifi_level = " + wifi_event_manager.wifi_status.wifi_level);
    }


    private void exportCsv(DeviceInfo device_info)
    {
        if (gateway_settings.getCsvOutputEnabledStatus())
        {
            switch (device_info.sensor_type)
            {
                case SENSOR_TYPE__LIFETOUCH:
                {
                    csv_export.exportLifetouchHeartRateData(device_info, "lifetouch_heart_rate_data");
                    csv_export.exportLifetouchRespirationRateData(device_info, "lifetouch_respiration_rate_data");
                    csv_export.exportLifetouchHeartBeatData(device_info, "lifetouch_heart_beat_data");
                    csv_export.exportLifetouchSetupModeData(device_info, "lifetouch_setup_mode_data");
                    csv_export.exportLifetouchRawAccelerometerModeData(device_info, "lifetouch_raw_accelerometer_mode_data");

                    if (gateway_settings.getEnablePatientOrientation())
                    {
                        csv_export.exportLifetouchPatientOrientationData(device_info, "lifetouch_patient_orientation_data");
                    }
                }
                break;

                case SENSOR_TYPE__TEMPERATURE:
                {
                    csv_export.exportLifetempMeasurementData(device_info, "lifetemp_measurement_data");
                }
                break;

                case SENSOR_TYPE__SPO2:
                {
                    csv_export.exportNoninWristOxMeasurementData(device_info, "pulse_ox_measurement_data");
                    csv_export.exportNoninWristOxIntermediateData(device_info, "pulse_ox_intermediate_data");
                    csv_export.exportNoninWristOxSetupModeData(device_info, "pulse_ox_setup_mode_data");
                }
                break;

                case SENSOR_TYPE__BLOOD_PRESSURE:
                {
                    csv_export.exportBloodPressureMeasurementData(device_info, "blood_pressure_measurement_data");
                }
                break;

                case SENSOR_TYPE__ALGORITHM:
                {
                    if (device_info.device_type == DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE)
                    {
                        csv_export.exportEarlyWarningScoreMeasurementData(device_info, "early_warning_score_measurement_data");
                    }
                }
                break;
            }
        }
    }


    private void disconnectBtleDevice(BtleSensorDevice device_info, boolean send_turn_off_command)
    {
        Log.d(TAG, "disconnectBtleDevice : " + device_info.device_type + " : send_turn_off_command = " + send_turn_off_command);

        if(send_turn_off_command)
        {
            device_info.sendTurnOffCommand();
        }
        else
        {
            removeDeviceDisconnection(device_info);
            removeDeviceFromSweetblue(device_info);
        }
    }


    /*
     * This handler acts as low pass filter. Handler is fired after 1 sec of leads-off detection.
     * This avoids the strange behaviour of UI graph fragment.
     */
    private final Handler lifetouch_leads_off_handler = new Handler();

    // Handles various events fired by the Service.
    // ACTION_CONNECTED: connected to a GATT server.
    // ACTION_DISCONNECTED: disconnected from a GATT server.
    // ACTION_AVAILABLE: received data from the device.  This can be a result of read or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver_Lifetouch = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            int device_type_int = intent.getIntExtra("device_type", 0);
            DeviceType device_type = DeviceType.values()[device_type_int];

            DeviceInfo info = device_info_manager.getDeviceInfoByDeviceType(device_type);

            if(info.isDeviceTypeABtleSensorDevice())
            {
                final BtleSensorDevice device_info = (BtleSensorDevice) info;

                switch (action)
                {
                    case BluetoothLeLifetouch.ACTION_CONNECTED:
                    {
                        Log.d(TAG, "BluetoothLeLifetouch.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersion());

                        handleConnection(device_info);
                    }
                    break;

                    case BluetoothLeLifetouch.ACTION_DISCONNECTED:
                    {
                        Log.d(TAG, "BluetoothLeLifetouch.ACTION_DISCONNECTED");

                        removeDeviceDisconnection(device_info, false, false);
                    }
                    break;

                    case BluetoothLeLifetouch.ACTION_RESCAN_REQUIRED:
                    {
                        Log.d(TAG, "Rescanning for Lifetouch after firmware update");

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.NOT_PAIRED);

                        setDeviceConnectionStatusToSearchingForValidDevices(device_info);

                        connectNextDeviceToGatewayIfNeededAndNoScanRunning();
                    }
                    break;

                    case BluetoothLeLifetouch.ACTION_TURNED_OFF:
                    {
                        Log.d(TAG, "BluetoothLeLifetouch.ACTION_TURNED_OFF");

                        removeDeviceFromSweetblue(device_info);
                    }
                    break;

                    case BluetoothLeLifetouch.ACTION_DATA_AVAILABLE:
                    {
                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                        try
                        {
                            String data = intent.getStringExtra(BluetoothLeLifetouch.DATA_AS_STRING);
                            String data_type = intent.getStringExtra(BluetoothLeLifetouch.DATA_TYPE);
                            String device_address = intent.getStringExtra(BluetoothLeLifetouch.DEVICE_ADDRESS);

                            if ((data == null) || (device_address == null) || (data_type == null))
                            {
                                if (data == null)
                                {
                                    Log.d(TAG, "data = null");
                                }

                                if (data_type == null)
                                {
                                    Log.d(TAG, "data_type = null");
                                }

                                if (device_address == null)
                                {
                                    Log.d(TAG, "device_address = null");
                                }
                            }
                            else
                            {
                                if (!disableCommentsForSpeed())
                                {
                                    Log.d(TAG, "ACTION_DATA_AVAILABLE device_address : " + device_address + ". Data Type : " + data_type);
                                }

                                switch (data_type)
                                {
                                    case BluetoothLeLifetouch.DATATYPE_BATTERY_LEVEL:
                                    {
                                        final int NO_TIME_STAMP = -1;

                                        int battery_percentage = intent.getIntExtra(BluetoothLeLifetouch.BATTERY_PERCENTAGE, 0);
                                        int battery_millivolts = intent.getIntExtra(BluetoothLeLifetouch.BATTERY_VOLTAGE, 0);
                                        long battery_percentage_timestamp = intent.getLongExtra(BluetoothLeLifetouch.BATTERY_PERCENTAGE_TIMESTAMP, NO_TIME_STAMP);

                                        long timestamp_in_ms = getNtpTimeNowInMilliseconds();                // Use NOW as we don't know when the measurement was taken

                                        // if timestamp is not send from lifetouch devices than give current time in ms.
                                        // Making it backward compatible, if timestamp isn't send from a lifetouch then it is old lifetouch
                                        if (battery_percentage_timestamp != NO_TIME_STAMP)
                                        {
                                            timestamp_in_ms = battery_percentage_timestamp + device_info.start_date_at_midnight_in_milliseconds;
                                        }

                                        MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                                battery_millivolts,
                                                battery_percentage,
                                                timestamp_in_ms,
                                                getNtpTimeNowInMilliseconds());

                                        if (!disableCommentsForSpeed())
                                        {
                                            Log.i(TAG, "Lifetouch Battery Voltage = " + battery_millivolts + ". Lifetouch Battery Percentage = " + battery_percentage + " . Timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                        }

                                        // Only process the received data is there is currently a Patient Session in progress.....
                                        if (isPatientSessionRunning())
                                        {
                                            // .....and a Device Session
                                            if (device_info.isDeviceSessionInProgress())
                                            {
                                                // Save the Battery reading so can be read from the UI on demand
                                                device_info.setLastBatteryReading(battery_percentage, battery_millivolts, timestamp_in_ms);

                                                patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                                local_database_storage.storeLifetouchBatteryMeasurement(device_info, measurement);
                                            }
                                            else
                                            {
                                                Log.e(TAG, "Rx'ed Lifetouch Battery Level but IGNORED as no Device Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed Lifetouch Battery Level but IGNORED as no Patient Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouch.DATATYPE_FIRMWARE_REVISION:
                                    {
                                        Log.i(TAG, "Received " + data_type + " : " + data);

                                        int firmware_int = intent.getIntExtra(BluetoothLeLifetouch.FIRMWARE_VERSION_NUMBER, -1);
                                        String firmware_version_string = data.trim();

                                        try
                                        {
                                            device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                            patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                                            int latest_stored_firmware_version = firmware_image_manager.getLatestStoredFirmwareVersion(device_info.device_type);

                                            if ((device_info.getDeviceFirmwareVersion() < latest_stored_firmware_version) && (device_info.getDeviceFirmwareVersion() > 1357))
                                            {
                                                // Store that there is a new firmware OTA ready to be done in device_info.
                                                firmware_image_manager.setFirmwareUpdatePending(device_info.device_type, true);

                                                Log.e(TAG, "Lifetouch firmware update pending. From " + device_info.getDeviceFirmwareVersion() + " to " + latest_stored_firmware_version);
                                            }
                                            else
                                            {
                                                // Store that Firmware is up to date.
                                                firmware_image_manager.setFirmwareUpdatePending(device_info.device_type, false);

                                                Log.e(TAG, "Lifetouch firmware up to date : Current FW = v" + device_info.getDeviceFirmwareVersion() + " and latest FW = v" + latest_stored_firmware_version);
                                            }
                                        }
                                        catch (NumberFormatException x)
                                        {
                                            Log.e("ERROR", "lifetouch_firmware_revision INVALID : " + data);
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouch.DATATYPE_BEAT_INFO:
                                    {
                                        int number_of_heart_beats_pending = intent.getIntExtra(BluetoothLeLifetouch.HEART_BEATS_PENDING, 0);

                                        patient_gateway_outgoing_commands.reportNumberOfLifetouchHeartBeatsPending(number_of_heart_beats_pending);

                                        ArrayList<HeartBeatInfo> heart_beat_list = intent.getParcelableArrayListExtra(BluetoothLeLifetouch.HEART_BEAT_INFO);

                                        for (HeartBeatInfo this_heart_beat : heart_beat_list)
                                        {
                                            if (this_heart_beat.getAmplitude() == ErrorCodes.ERROR_CODE__LIFETOUCH_NO_TIMESTAMP)
                                            {
                                                // Set the timestamp and ignore this data
                                                Log.e(TAG, "Received no_timestamp error code : re-sending timestamp as Session Start Time is now valid");

                                                device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());

                                                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                                                // Reset the heart beat processing to remove any remaining data that was received before the new timestamp
                                                lifetouch_heart_beat_processor.reset();
                                            }
                                            else
                                            {
                                                // Add the Lifetouch's Start Date as this timestamp from the Lifetouch is only the number of milliseconds since Midnight on that Start Date
                                                this_heart_beat.setTimestampInMs(this_heart_beat.getTimestampInMs() + device_info.start_date_at_midnight_in_milliseconds);


                                                // Only process the received data is there is currently a Patient Session in progress.....
                                                if (isPatientSessionRunning())
                                                {
                                                    // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                                    if (device_info.isDeviceSessionInProgress())
                                                    {
                                                        Date heart_beat_timestamp_as_date = new Date(this_heart_beat.getTimestampInMs());

                                                        // Only process the received data if the Heartbeat's timestamp is after the start of the device session
                                                        if (heart_beat_timestamp_as_date.getTime() >= device_info.device_session_start_time)
                                                        {
                                                            lifetouch_heart_beat_processor.processMeasurement(this_heart_beat);

                                                            if (this_heart_beat.isBeatRealTime())
                                                            {
                                                                if (this_heart_beat.getAmplitude() == ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED)
                                                                {
                                                                    device_info.no_measurements_detected = true;
                                                                }
                                                                else if (this_heart_beat.getAmplitude() < ErrorCodes.ERROR_CODES)
                                                                {
                                                                    device_info.no_measurements_detected = false;
                                                                }

                                                                patient_gateway_outgoing_commands.reportLifetouchNoBeatsDetectedTimerStatus(device_info.no_measurements_detected);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            // Should only happen during development
                                                            Log.e(TAG, "Rx'ed HEART_BEATS_BEAT_INFO but IGNORED AS TIMESTAMP BEFORE THE START OF THIS DEVICE SESSION : " + Utils.explainHeartBeat(this_heart_beat) + ". Device Session Start = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(device_info.device_session_start_time));
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Log.e(TAG, "Rx'ed HEART_BEATS_BEAT_INFO but IGNORED as no Device Session in progress : " + Utils.explainHeartBeat(this_heart_beat));
                                                    }
                                                }
                                                else
                                                {
                                                    Log.e(TAG, "Rx'ed HEART_BEATS_BEAT_INFO but IGNORED as no Patient Session in progress : " + Utils.explainHeartBeat(this_heart_beat));
                                                }
                                            }
                                        }

                                        // Now all heat beats processed, if no beats pending then can process outstanding heart rates
                                        if (number_of_heart_beats_pending == 0)
                                        {
                                            lifetouch_heart_beat_processor.processAnyOutstandingData();
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouch.DATATYPE_SETUP_MODE_RAW_SAMPLES:
                                    {
                                        // Only process the received data is there is currently a Patient Session in progress.....
                                        if (isPatientSessionRunning())
                                        {
                                            // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                            if (device_info.isDeviceSessionInProgress())
                                            {
                                                Bundle bundle = intent.getExtras();
                                                ArrayList<MeasurementSetupModeDataPoint> setup_mode_datapoints = bundle.getParcelableArrayList(BluetoothLeLifetouch.SETUP_MODE_DATA);

                                                Log.d(TAG, "BluetoothLeLifetouch.DATATYPE_SETUP_MODE_RAW_SAMPLES : number_of_samples " + setup_mode_datapoints.size());

                                                for (MeasurementSetupModeDataPoint datapoint : setup_mode_datapoints)
                                                {
                                                    datapoint.timestamp_in_ms += device_info.start_date_at_midnight_in_milliseconds;

                                                    if (datapoint.sample != BluetoothLeLifetouch.GAP_IN_DATA)
                                                    {
                                                        processTimestampForSetupModeLogEntry(device_info, datapoint.timestamp_in_ms);
                                                    }

                                                    // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                                                    // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                                                    device_info.queue_setup_mode_datapoints.add(datapoint);

                                                    if (device_info.isInServerInitedRawDataMode())
                                                    {
                                                        device_info.queue_setup_mode_datapoints_for_server.add(datapoint);
                                                    }

                                                    patient_gateway_outgoing_commands.sendNewSetupModeSampleToUserInterface(device_info.device_type, datapoint);
                                                }

                                                if (device_info.isInServerInitedRawDataMode())
                                                {
                                                    server_syncing.sendDeviceSetupModeData(patient_session_info.server_patient_session_id, device_info, 1);
                                                }
                                            }
                                            else
                                            {
                                                Log.e(TAG, "Rx'ed SETUP_MODE_RAW_SAMPLES but IGNORED as no Device Session in progress");
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed SETUP_MODE_RAW_SAMPLES but IGNORED as no Patient Session in progress");
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouch.DATATYPE_RAW_ACCELEROMETER_RAW_SAMPLES:
                                    {
                                        // Only process the received data is there is currently a Patient Session in progress.....
                                        if (isPatientSessionRunning())
                                        {
                                            // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                            if (device_info.isDeviceSessionInProgress())
                                            {
                                                short[] x_axis_samples = intent.getShortArrayExtra(BluetoothLeLifetouch.RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES);
                                                short[] y_axis_samples = intent.getShortArrayExtra(BluetoothLeLifetouch.RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES);
                                                short[] z_axis_samples = intent.getShortArrayExtra(BluetoothLeLifetouch.RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES);
                                                long[] timestamps = intent.getLongArrayExtra(BluetoothLeLifetouch.RAW_ACCELEROMETER_MODE__TIMESTAMPS);

                                                //Log.d(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLES");

                                                // Data will always coming in 3's
                                                int number_of_samples = x_axis_samples.length;

                                                for (int i = 0; i < number_of_samples; i++)
                                                {
                                                    timestamps[i] += device_info.start_date_at_midnight_in_milliseconds;

                                                    RawAccelerometerModeDataPoint datapoint = new RawAccelerometerModeDataPoint();

                                                    datapoint.x = x_axis_samples[i];
                                                    datapoint.y = y_axis_samples[i];
                                                    datapoint.z = z_axis_samples[i];
                                                    datapoint.timestamp = timestamps[i];

                                                    Log.d(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLE : x = " + datapoint.x + " : y = " + datapoint.y + " : " + datapoint.z + " : time = " + datapoint.timestamp);

                                                    // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                                                    // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                                                    queue_lifetouch_raw_accelerometer_mode_datapoints.add(datapoint);

                                                    if (device_info.isInServerInitedRawDataMode())
                                                    {
                                                        queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.add(datapoint);
                                                    }
                                                }

                                                patient_gateway_outgoing_commands.sendNewLifetouchRawAccelerometerModeData(x_axis_samples, y_axis_samples, z_axis_samples);

                                                // Before sending setup mode data, check connection
                                                if (device_info.isInServerInitedRawDataMode())
                                                {
                                                    if (queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.size() > 0)
                                                    {
                                                        // Create JSON array and populate it with setup data
                                                        JsonArray json_array = new JsonArray();

                                                        for (RawAccelerometerModeDataPoint data_point : queue_lifetouch_raw_accelerometer_mode_datapoints_for_server)
                                                        {
                                                            JsonObject json_object = new JsonObject();
                                                            json_object.addProperty("x", data_point.x);
                                                            json_object.addProperty("y", data_point.y);
                                                            json_object.addProperty("z", data_point.z);
                                                            json_object.addProperty("timestamp", data_point.timestamp);
                                                            json_array.add(json_object);
                                                        }

                                                        // Add "points" string in front of message
                                                        JsonObject json_data = new JsonObject();
                                                        json_data.addProperty("sensor_type", device_info.sensor_type.ordinal());
                                                        json_data.addProperty("device_type", device_info.device_type.ordinal());
                                                        json_data.addProperty("server_patient_session_id", patient_session_info.server_patient_session_id);

                                                        json_data.add("points", json_array);

                                                        server_syncing.sendDeviceRawAccelerometerModeDataIfServerConnected(json_data);

                                                        // After sending data clear array.
                                                        queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.clear();
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                Log.e(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLES but IGNORED as no Device Session in progress");
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLES but IGNORED as no Patient Session in progress");
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouch.DATATYPE_HEART_BEAT_LEADS_OFF:
                                    {
                                        // Only update if leads status is newer than previous status
                                        long leads_status_timestamp = intent.getLongExtra(BluetoothLeLifetouch.HEART_BEAT_LEADS_OFF_TIMESTAMP, -1);

                                        if (leads_status_timestamp > device_info.last_device_disconnected_timestamp)
                                        {
                                            device_info.last_device_disconnected_timestamp = leads_status_timestamp;

                                            // False = on the body
                                            // True = off the body
                                            device_info.device_disconnected_from_body = intent.getBooleanExtra(BluetoothLeLifetouch.HEART_BEAT_LEADS_OFF, false);

                                            // Handler is fired after 1 sec of leads-off message received from lifetouchBle service
                                            // If leads-off is reported soon than 1 sec then previously added message is removed and recent leads-off status is added
                                            // remove all the previous messages
                                            lifetouch_leads_off_handler.removeCallbacksAndMessages(null);

                                            // Create the runnable and run after 1 second
                                            lifetouch_leads_off_handler.postDelayed(() -> {
                                                // If lifetouch is disconnected with in 1 sec of leads-off detected then don't update the UI
                                                if (device_info.isActualDeviceConnectionStatusConnected())
                                                {
                                                    if (device_info.device_disconnected_from_body == false)
                                                    {
                                                        Log.e(TAG, "Lifetouch on body : reporting to UI");
                                                    }
                                                    else
                                                    {
                                                        Log.e(TAG, "Lifetouch OFF body : reporting to UI");
                                                        resetPatientOrientation();
                                                    }

                                                    //Report leads-off status to UI
                                                    patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

                                                    server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);
                                                }
                                                else
                                                {
                                                    Log.w(TAG, "Lifetouch leads-off detected. But Lifetouch is disconnected");
                                                }
                                            }, (int) DateUtils.SECOND_IN_MILLIS);
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouch.DATATYPE_GET_TIMESTAMP:
                                    {
                                        Log.i(TAG, "Received " + data_type + " : " + data);

                                        handleReceivedIsansysFormatTimestamp(device_info, data);
                                    }
                                    break;

                                    default:
                                    {
                                        // data_type == Unknown
                                        Log.e(TAG, "Unhandled in mGattUpdateReceiver_Lifetouch : Data Type = " + data_type + "   Data = " + data);
                                    }
                                    break;
                                }

                                // Log.i(CLASS_TAG, data_type + " : " + data);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                            Log.e(TAG, "exception occurred:");
                            Log.e(TAG, e.toString());
                        }
                    }
                    break;

                    case BluetoothLeLifetouch.ACTION_AUTHENTICATION_PASSED:
                    {
                        Log.d(TAG, "ACTION_AUTHENTICATION_PASSED");

                        putDeviceIntoDesiredOperatingMode(device_info);
                    }
                    break;

                    case BluetoothLeLifetouch.ACTION_AUTHENTICATION_FAILED:
                    {
                        Log.d(TAG, "ACTION_AUTHENTICATION_FAILED");
                    }
                    break;
                }
            }
            else
            {
                Log.d(TAG, "mGattUpdateReceiver_Lifetouch ACTION RECEIVED but NO VALID DEVICE FOUND IN SESSION");
            }
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver_LifetouchThree = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            int device_type_int = intent.getIntExtra("device_type", 0);
            DeviceType device_type = DeviceType.values()[device_type_int];

            DeviceInfo info = device_info_manager.getDeviceInfoByDeviceType(device_type);

            if(info.isDeviceTypeABtleSensorDevice())
            {
                final BtleSensorDevice device_info = (BtleSensorDevice) info;

                switch (action)
                {
                    case BluetoothLeLifetouchThree.ACTION_CONNECTED:
                    {
                        Log.d(TAG, "BluetoothLeLifetouchThree.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersion());

                        handleConnection(device_info);
                    }
                    break;

                    case BluetoothLeLifetouchThree.ACTION_DISCONNECTED:
                    {
                        Log.d(TAG, "BluetoothLeLifetouchThree.ACTION_DISCONNECTED");

                        removeDeviceDisconnection(device_info, false, false);
                    }
                    break;

                    case BluetoothLeLifetouchThree.ACTION_RESCAN_REQUIRED:
                    {
                        Log.d(TAG, "Rescanning for Lifetouch after firmware update");

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.NOT_PAIRED);

                        setDeviceConnectionStatusToSearchingForValidDevices(device_info);

                        connectNextDeviceToGatewayIfNeededAndNoScanRunning();
                    }
                    break;

                    case BluetoothLeLifetouchThree.ACTION_TURNED_OFF:
                    {
                        Log.d(TAG, "BluetoothLeLifetouchThree.ACTION_TURNED_OFF");

                        removeDeviceFromSweetblue(device_info);
                    }
                    break;

                    case BluetoothLeLifetouchThree.ACTION_DATA_AVAILABLE:
                    {
                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                        try
                        {
                            String data = intent.getStringExtra(BluetoothLeLifetouchThree.DATA_AS_STRING);
                            String data_type = intent.getStringExtra(BluetoothLeLifetouchThree.DATA_TYPE);
                            String device_address = intent.getStringExtra(BluetoothLeLifetouchThree.DEVICE_ADDRESS);

                            if ((data == null) || (device_address == null) || (data_type == null))
                            {
                                if (data == null)
                                {
                                    Log.d(TAG, "data = null");
                                }

                                if (data_type == null)
                                {
                                    Log.d(TAG, "data_type = null");
                                }

                                if (device_address == null)
                                {
                                    Log.d(TAG, "device_address = null");
                                }
                            }
                            else
                            {
                                if (!disableCommentsForSpeed())
                                {
                                    Log.d(TAG, "ACTION_DATA_AVAILABLE device_address : " + device_address + ". Data Type : " + data_type);
                                }

                                switch (data_type)
                                {
                                    case BluetoothLeLifetouchThree.DATATYPE_BATTERY_LEVEL:
                                    {
                                        final int NO_TIME_STAMP = -1;

                                        int battery_percentage = intent.getIntExtra(BluetoothLeLifetouchThree.BATTERY_PERCENTAGE, 0);
                                        int battery_millivolts = intent.getIntExtra(BluetoothLeLifetouchThree.BATTERY_VOLTAGE, 0);
                                        long battery_percentage_timestamp = intent.getLongExtra(BluetoothLeLifetouchThree.BATTERY_PERCENTAGE_TIMESTAMP, NO_TIME_STAMP);

                                        long timestamp_in_ms = getNtpTimeNowInMilliseconds();                // Use NOW as we don't know when the measurement was taken

                                        // if timestamp is not send from lifetouch devices than give current time in ms.
                                        // Making it backward compatible, if timestamp isn't send from a lifetouch then it is old lifetouch
                                        if (battery_percentage_timestamp != NO_TIME_STAMP)
                                        {
                                            timestamp_in_ms = battery_percentage_timestamp + device_info.start_date_at_midnight_in_milliseconds;
                                        }

                                        MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                                battery_millivolts,
                                                battery_percentage,
                                                timestamp_in_ms,
                                                getNtpTimeNowInMilliseconds());

                                        if (!disableCommentsForSpeed())
                                        {
                                            Log.i(TAG, "Lifetouch Three Battery Voltage = " + battery_millivolts + ". Lifetouch Battery Percentage = " + battery_percentage + " . Timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                        }

                                        // Only process the received data is there is currently a Patient Session in progress.....
                                        if (isPatientSessionRunning())
                                        {
                                            // .....and a Device Session
                                            if (device_info.isDeviceSessionInProgress())
                                            {
                                                // Save the Battery reading so can be read from the UI on demand
                                                device_info.setLastBatteryReading(battery_percentage, battery_millivolts, timestamp_in_ms);

                                                patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                                local_database_storage.storeLifetouchBatteryMeasurement(device_info, measurement);
                                            }
                                            else
                                            {
                                                Log.e(TAG, "Rx'ed Lifetouch Three Battery Level but IGNORED as no Device Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed Lifetouch Three Battery Level but IGNORED as no Patient Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouchThree.DATATYPE_FIRMWARE_REVISION:
                                    {
                                        Log.i(TAG, "Received " + data_type + " : " + data);

                                        int firmware_int = intent.getIntExtra(BluetoothLeLifetouchThree.FIRMWARE_VERSION_NUMBER, -1);
                                        String firmware_version_string = data.trim();

                                        try
                                        {
                                            device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                            patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                                            int latest_stored_firmware_version = firmware_image_manager.getLatestStoredFirmwareVersion(device_info.device_type);

                                            if ((device_info.getDeviceFirmwareVersion() < latest_stored_firmware_version) && (device_info.getDeviceFirmwareVersion() > LT3_MIN_DFU_VERSION))
                                            {
                                                // Store that there is a new firmware OTA ready to be done in device_info.
                                                firmware_image_manager.setFirmwareUpdatePending(device_info.device_type, true);

                                                Log.e(TAG, "Lifetouch Three firmware update pending. From " + device_info.getDeviceFirmwareVersion() + " to " + latest_stored_firmware_version);
                                            }
                                            else
                                            {
                                                // Store that Firmware is up to date.
                                                firmware_image_manager.setFirmwareUpdatePending(device_info.device_type, false);

                                                Log.e(TAG, "Lifetouch Three firmware up to date : Current FW = v" + device_info.getDeviceFirmwareVersion() + " and latest FW = v" + latest_stored_firmware_version);
                                            }
                                        }
                                        catch (NumberFormatException x)
                                        {
                                            Log.e("ERROR", "lifetouch_firmware_revision INVALID : " + data);
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouchThree.DATATYPE_BEAT_INFO:
                                    {
                                        int number_of_heart_beats_pending = intent.getIntExtra(BluetoothLeLifetouchThree.HEART_BEATS_PENDING, 0);

                                        patient_gateway_outgoing_commands.reportNumberOfLifetouchHeartBeatsPending(number_of_heart_beats_pending);

                                        ArrayList<HeartBeatInfo> heart_beat_list = intent.getParcelableArrayListExtra(BluetoothLeLifetouchThree.HEART_BEAT_INFO);

                                        for (HeartBeatInfo this_heart_beat : heart_beat_list)
                                        {
                                            if (this_heart_beat.getAmplitude() == ErrorCodes.ERROR_CODE__LIFETOUCH_NO_TIMESTAMP)
                                            {
                                                // Set the timestamp and ignore this data
                                                Log.e(TAG, "Received no_timestamp error code : re-sending timestamp as Session Start Time is now valid");

                                                device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());

                                                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                                                // Reset the heart beat processing to remove any remaining data that was received before the new timestamp
                                                lifetouch_heart_beat_processor.reset();
                                            }
                                            else
                                            {
                                                // Add the Lifetouch's Start Date as this timestamp from the Lifetouch is only the number of milliseconds since Midnight on that Start Date
                                                this_heart_beat.setTimestampInMs(this_heart_beat.getTimestampInMs() + device_info.start_date_at_midnight_in_milliseconds);


                                                // Only process the received data is there is currently a Patient Session in progress.....
                                                if (isPatientSessionRunning())
                                                {
                                                    // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                                    if (device_info.isDeviceSessionInProgress())
                                                    {
                                                        Date heart_beat_timestamp_as_date = new Date(this_heart_beat.getTimestampInMs());

                                                        // Only process the received data if the Heartbeat's timestamp is after the start of the device session
                                                        if (heart_beat_timestamp_as_date.getTime() >= device_info.device_session_start_time)
                                                        {
                                                            lifetouch_heart_beat_processor.processMeasurement(this_heart_beat);

                                                            if (this_heart_beat.isBeatRealTime())
                                                            {
                                                                if (this_heart_beat.getAmplitude() == ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED)
                                                                {
                                                                    device_info.no_measurements_detected = true;
                                                                }
                                                                else if (this_heart_beat.getAmplitude() < ErrorCodes.ERROR_CODES)
                                                                {
                                                                    device_info.no_measurements_detected = false;
                                                                }

                                                                patient_gateway_outgoing_commands.reportLifetouchNoBeatsDetectedTimerStatus(device_info.no_measurements_detected);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            // Should only happen during development
                                                            Log.e(TAG, "Rx'ed HEART_BEATS_BEAT_INFO but IGNORED AS TIMESTAMP BEFORE THE START OF THIS DEVICE SESSION : " + Utils.explainHeartBeat(this_heart_beat) + ". Device Session Start = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(device_info.device_session_start_time));
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Log.e(TAG, "Rx'ed HEART_BEATS_BEAT_INFO but IGNORED as no Device Session in progress : " + Utils.explainHeartBeat(this_heart_beat));
                                                    }
                                                }
                                                else
                                                {
                                                    Log.e(TAG, "Rx'ed HEART_BEATS_BEAT_INFO but IGNORED as no Patient Session in progress : " + Utils.explainHeartBeat(this_heart_beat));
                                                }
                                            }
                                        }

                                        // Now all heat beats processed, if no beats pending then can process outstanding heart rates
                                        if (number_of_heart_beats_pending == 0)
                                        {
                                            lifetouch_heart_beat_processor.processAnyOutstandingData();
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouchThree.DATATYPE_SETUP_MODE_RAW_SAMPLES:
                                    {
                                        // Only process the received data is there is currently a Patient Session in progress.....
                                        if (isPatientSessionRunning())
                                        {
                                            // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                            if (device_info.isDeviceSessionInProgress())
                                            {
                                                Bundle bundle = intent.getExtras();
                                                ArrayList<MeasurementSetupModeDataPoint> setup_mode_datapoints = bundle.getParcelableArrayList(BluetoothLeLifetouchThree.SETUP_MODE_DATA);
                                                boolean khz_setup_mode_enabled = gateway_settings.getLT3KHzSetupModeEnabledStatus();

                                                for (int i = 0; i < setup_mode_datapoints.size(); i++)
                                                {
                                                    MeasurementSetupModeDataPoint datapoint = setup_mode_datapoints.get(i);
                                                    datapoint.timestamp_in_ms += device_info.start_date_at_midnight_in_milliseconds;

                                                    if (datapoint.sample != LifetouchThreeSetupMode.GAP_IN_DATA) {
                                                        processTimestampForSetupModeLogEntry(device_info, datapoint.timestamp_in_ms);
                                                    }

                                                    // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                                                    // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                                                    device_info.queue_setup_mode_datapoints.add(datapoint);

                                                    // If not in KHz mode, process every sample
                                                    // If in KHz mode, process every 10th sample
                                                    if (!khz_setup_mode_enabled || i % 10 == 0) {
                                                        if (device_info.isInServerInitedRawDataMode()) {
                                                            device_info.queue_setup_mode_datapoints_for_server.add(datapoint);
                                                        }

                                                        patient_gateway_outgoing_commands.sendNewSetupModeSampleToUserInterface(device_info.device_type, datapoint);
                                                    }
                                                }

                                                if (device_info.isInServerInitedRawDataMode())
                                                {
                                                    server_syncing.sendDeviceSetupModeData(patient_session_info.server_patient_session_id, device_info, 1);
                                                }
                                            }
                                            else
                                            {
                                                Log.e(TAG, "Rx'ed SETUP_MODE_RAW_SAMPLES but IGNORED as no Device Session in progress");
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed SETUP_MODE_RAW_SAMPLES but IGNORED as no Patient Session in progress");
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouchThree.DATATYPE_RAW_ACCELEROMETER_RAW_SAMPLES:
                                    {
                                        // Only process the received data is there is currently a Patient Session in progress.....
                                        if (isPatientSessionRunning())
                                        {
                                            // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                            if (device_info.isDeviceSessionInProgress())
                                            {
                                                short[] x_axis_samples = intent.getShortArrayExtra(BluetoothLeLifetouchThree.RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES);
                                                short[] y_axis_samples = intent.getShortArrayExtra(BluetoothLeLifetouchThree.RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES);
                                                short[] z_axis_samples = intent.getShortArrayExtra(BluetoothLeLifetouchThree.RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES);
                                                long[] timestamps = intent.getLongArrayExtra(BluetoothLeLifetouchThree.RAW_ACCELEROMETER_MODE__TIMESTAMPS);

                                                //Log.d(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLES");

                                                // Data will always coming in 3's
                                                int number_of_samples = x_axis_samples.length;

                                                for (int i = 0; i < number_of_samples; i++)
                                                {
                                                    timestamps[i] += device_info.start_date_at_midnight_in_milliseconds;

                                                    RawAccelerometerModeDataPoint datapoint = new RawAccelerometerModeDataPoint();

                                                    datapoint.x = x_axis_samples[i];
                                                    datapoint.y = y_axis_samples[i];
                                                    datapoint.z = z_axis_samples[i];
                                                    datapoint.timestamp = timestamps[i];

                                                    Log.d(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLE : x = " + datapoint.x + " : y = " + datapoint.y + " : " + datapoint.z + " : time = " + datapoint.timestamp);

                                                    // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                                                    // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                                                    queue_lifetouch_raw_accelerometer_mode_datapoints.add(datapoint);

                                                    if (device_info.isInServerInitedRawDataMode())
                                                    {
                                                        queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.add(datapoint);
                                                    }
                                                }

                                                patient_gateway_outgoing_commands.sendNewLifetouchRawAccelerometerModeData(x_axis_samples, y_axis_samples, z_axis_samples);

                                                // Before sending setup mode data, check connection
                                                if (device_info.isInServerInitedRawDataMode())
                                                {
                                                    if (queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.size() > 0)
                                                    {
                                                        // Create JSON array and populate it with setup data
                                                        JsonArray json_array = new JsonArray();

                                                        for (RawAccelerometerModeDataPoint data_point : queue_lifetouch_raw_accelerometer_mode_datapoints_for_server)
                                                        {
                                                            JsonObject json_object = new JsonObject();
                                                            json_object.addProperty("x", data_point.x);
                                                            json_object.addProperty("y", data_point.y);
                                                            json_object.addProperty("z", data_point.z);
                                                            json_object.addProperty("timestamp", data_point.timestamp);
                                                            json_array.add(json_object);
                                                        }

                                                        // Add "points" string in front of message
                                                        JsonObject json_data = new JsonObject();
                                                        json_data.addProperty("sensor_type", device_info.sensor_type.ordinal());
                                                        json_data.addProperty("device_type", device_info.device_type.ordinal());
                                                        json_data.addProperty("server_patient_session_id", patient_session_info.server_patient_session_id);

                                                        json_data.add("points", json_array);

                                                        server_syncing.sendDeviceRawAccelerometerModeDataIfServerConnected(json_data);

                                                        // After sending data clear array.
                                                        queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.clear();
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                Log.e(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLES but IGNORED as no Device Session in progress");
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLES but IGNORED as no Patient Session in progress");
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouchThree.DATATYPE_HEART_BEAT_LEADS_OFF:
                                    {
                                        // Only update if leads status is newer than previous status
                                        long leads_status_timestamp = intent.getLongExtra(BluetoothLeLifetouchThree.HEART_BEAT_LEADS_OFF_TIMESTAMP, -1);

                                        if (leads_status_timestamp > device_info.last_device_disconnected_timestamp)
                                        {
                                            device_info.last_device_disconnected_timestamp = leads_status_timestamp;

                                            // False = on the body
                                            // True = off the body
                                            device_info.device_disconnected_from_body = intent.getBooleanExtra(BluetoothLeLifetouchThree.HEART_BEAT_LEADS_OFF, false);

                                            // Handler is fired after 1 sec of leads-off message received from lifetouchBle service
                                            // If leads-off is reported soon than 1 sec then previously added message is removed and recent leads-off status is added
                                            // remove all the previous messages
                                            lifetouch_leads_off_handler.removeCallbacksAndMessages(null);

                                            // Create the runnable and run after 1 second
                                            lifetouch_leads_off_handler.postDelayed(() -> {
                                                // If lifetouch is disconnected with in 1 sec of leads-off detected then don't update the UI
                                                if (device_info.isActualDeviceConnectionStatusConnected())
                                                {
                                                    if (device_info.device_disconnected_from_body == false)
                                                    {
                                                        Log.e(TAG, "Lifetouch on body : reporting to UI");
                                                    }
                                                    else
                                                    {
                                                        Log.e(TAG, "Lifetouch OFF body : reporting to UI");
                                                        resetPatientOrientation();
                                                    }

                                                    //Report leads-off status to UI
                                                    patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

                                                    server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);
                                                }
                                                else
                                                {
                                                    Log.w(TAG, "Lifetouch leads-off detected. But Lifetouch is disconnected");
                                                }
                                            }, (int) DateUtils.SECOND_IN_MILLIS);
                                        }
                                    }
                                    break;

                                    case BluetoothLeLifetouchThree.DATATYPE_GET_TIMESTAMP:
                                    {
                                        Log.i(TAG, "Received " + data_type + " : " + data);

                                        handleReceivedIsansysFormatTimestamp(device_info, data);
                                    }
                                    break;

                                    default:
                                    {
                                        // data_type == Unknown
                                        Log.e(TAG, "Unhandled in mGattUpdateReceiver_Lifetouch : Data Type = " + data_type + "   Data = " + data);
                                    }
                                    break;
                                }

                                // Log.i(CLASS_TAG, data_type + " : " + data);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                            Log.e(TAG, "exception occurred:");
                            Log.e(TAG, e.toString());
                        }
                    }
                    break;

                    case BluetoothLeLifetouchThree.ACTION_AUTHENTICATION_PASSED:
                    {
                        Log.d(TAG, "ACTION_AUTHENTICATION_PASSED");

                        putDeviceIntoDesiredOperatingMode(device_info);
                    }
                    break;

                    case BluetoothLeLifetouchThree.ACTION_AUTHENTICATION_FAILED:
                    {
                        Log.d(TAG, "ACTION_AUTHENTICATION_FAILED");
                    }
                    break;
                }
            }
            else
            {
                Log.d(TAG, "mGattUpdateReceiver_Lifetouch ACTION RECEIVED but NO VALID DEVICE FOUND IN SESSION");
            }
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver_Instapatch = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            int device_type_int = intent.getIntExtra("device_type", 0);
            DeviceType device_type = DeviceType.values()[device_type_int];

            DeviceInfo info = device_info_manager.getDeviceInfoByDeviceType(device_type);

            if(info.isDeviceTypeABtleSensorDevice())
            {
                final BtleSensorDevice device_info = (BtleSensorDevice) info;

                switch (action)
                {
                    case BluetoothLeInstapatch.ACTION_CONNECTED:
                    {
                        Log.d(TAG, "BluetoothLeInstapatch.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersion());

                        handleConnection(device_info);
                    }
                    break;

                    case BluetoothLeInstapatch.ACTION_DISCONNECTED:
                    {
                        Log.d(TAG, "BluetoothLeInstapatch.ACTION_DISCONNECTED");

                        removeDeviceDisconnection(device_info, false, false);
                    }
                    break;

                    case BluetoothLeInstapatch.ACTION_RESCAN_REQUIRED:
                    {
                        Log.d(TAG, "Rescanning for Lifetouch after firmware update");

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.NOT_PAIRED);

                        setDeviceConnectionStatusToSearchingForValidDevices(device_info);

                        connectNextDeviceToGatewayIfNeededAndNoScanRunning();
                    }
                    break;

                    case BluetoothLeInstapatch.ACTION_TURNED_OFF:
                    {
                        Log.d(TAG, "BluetoothLeInstapatch.ACTION_TURNED_OFF");

                        removeDeviceFromSweetblue(device_info);
                    }
                    break;

                    case BluetoothLeInstapatch.ACTION_DATA_AVAILABLE:
                    {
                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                        try
                        {
                            String data = intent.getStringExtra(BluetoothLeInstapatch.DATA_AS_STRING);
                            String data_type = intent.getStringExtra(BluetoothLeInstapatch.DATA_TYPE);
                            String device_address = intent.getStringExtra(BluetoothLeInstapatch.DEVICE_ADDRESS);

                            if ((data == null) || (device_address == null) || (data_type == null))
                            {
                                if (data == null)
                                {
                                    Log.d(TAG, "data = null");
                                }

                                if (data_type == null)
                                {
                                    Log.d(TAG, "data_type = null");
                                }

                                if (device_address == null)
                                {
                                    Log.d(TAG, "device_address = null");
                                }
                            }
                            else
                            {
                                if (!disableCommentsForSpeed())
                                {
                                    Log.d(TAG, "ACTION_DATA_AVAILABLE device_address : " + device_address + ". Data Type : " + data_type);
                                }

                                switch (data_type)
                                {
                                    case BluetoothLeInstapatch.DATATYPE_BATTERY_LEVEL:
                                    {
                                        final int NO_TIME_STAMP = -1;

                                        int battery_percentage = intent.getIntExtra(BluetoothLeInstapatch.BATTERY_PERCENTAGE, 0);
                                        int battery_millivolts = intent.getIntExtra(BluetoothLeInstapatch.BATTERY_VOLTAGE, 0);
                                        long battery_percentage_timestamp = intent.getLongExtra(BluetoothLeInstapatch.BATTERY_PERCENTAGE_TIMESTAMP, NO_TIME_STAMP);

                                        long timestamp_in_ms = getNtpTimeNowInMilliseconds();                // Use NOW as we don't know when the measurement was taken

                                        // if timestamp is not send from lifetouch devices than give current time in ms.
                                        // Making it backward compatible, if timestamp isn't send from a lifetouch then it is old lifetouch
                                        if (battery_percentage_timestamp != NO_TIME_STAMP)
                                        {
                                            timestamp_in_ms = battery_percentage_timestamp + device_info.start_date_at_midnight_in_milliseconds;
                                        }

                                        MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                                battery_millivolts,
                                                battery_percentage,
                                                timestamp_in_ms,
                                                getNtpTimeNowInMilliseconds());

                                        if (!disableCommentsForSpeed())
                                        {
                                            Log.i(TAG, "Lifetouch Battery Voltage = " + battery_millivolts + ". Lifetouch Battery Percentage = " + battery_percentage + " . Timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                        }

                                        // Only process the received data is there is currently a Patient Session in progress.....
                                        if (isPatientSessionRunning())
                                        {
                                            // .....and a Device Session
                                            if (device_info.isDeviceSessionInProgress())
                                            {
                                                // Save the Battery reading so can be read from the UI on demand
                                                device_info.setLastBatteryReading(battery_percentage, battery_millivolts, timestamp_in_ms);

                                                patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                                local_database_storage.storeLifetouchBatteryMeasurement(device_info, measurement);
                                            }
                                            else
                                            {
                                                Log.e(TAG, "Rx'ed Lifetouch Battery Level but IGNORED as no Device Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed Lifetouch Battery Level but IGNORED as no Patient Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                        }
                                    }
                                    break;

                                    case BluetoothLeInstapatch.DATATYPE_FIRMWARE_REVISION:
                                    {
                                        Log.i(TAG, "Received " + data_type + " : " + data);

                                        int firmware_int = intent.getIntExtra(BluetoothLeInstapatch.FIRMWARE_VERSION_NUMBER, -1);
                                        String firmware_version_string = data.trim();

                                        try
                                        {
                                            device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                            patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                                            int latest_stored_firmware_version = firmware_image_manager.getLatestStoredFirmwareVersion(device_info.device_type);

                                            if ((device_info.getDeviceFirmwareVersion() < latest_stored_firmware_version) && (device_info.getDeviceFirmwareVersion() > 1357))
                                            {
                                                // Store that there is a new firmware OTA ready to be done in device_info.
                                                firmware_image_manager.setFirmwareUpdatePending(device_info.device_type, true);

                                                Log.e(TAG, "Lifetouch firmware update pending. From " + device_info.getDeviceFirmwareVersion() + " to " + latest_stored_firmware_version);
                                            }
                                            else
                                            {
                                                // Store that Firmware is up to date.
                                                firmware_image_manager.setFirmwareUpdatePending(device_info.device_type, false);

                                                Log.e(TAG, "Lifetouch firmware up to date : Current FW = v" + device_info.getDeviceFirmwareVersion() + " and latest FW = v" + latest_stored_firmware_version);
                                            }
                                        }
                                        catch (NumberFormatException x)
                                        {
                                            Log.e("ERROR", "lifetouch_firmware_revision INVALID : " + data);
                                        }
                                    }
                                    break;

                                    case BluetoothLeInstapatch.DATATYPE_VITAL_SIGN_SET:
                                    {
                                        int numberOfVitalSignSetsPending = intent.getIntExtra(BluetoothLeInstapatch.TYPE_VITAL_SIGN_SETS_PENDING, 0);
                                        VitalSignSet vitalSignSet = intent.getParcelableExtra(BluetoothLeInstapatch.TYPE_VITAL_SIGN_SET);

                                        long timestamp = vitalSignSet.getTimestampInMs() + device_info.timestamp_offset;
                                        String log_line = "Tag = " + vitalSignSet.getTag() + " : HR = " + vitalSignSet.getHeartRate() + " : RR = " + vitalSignSet.getRespirationRate() + " : Temp = " + vitalSignSet.getTemperature() + " : @ " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(timestamp);
                                        log_line = log_line + " : Pending = " + numberOfVitalSignSetsPending;

                                        Log.d(TAG, "New Vitals : " + log_line);
                                    }
                                    break;

                                    case BluetoothLeInstapatch.DATATYPE_BEAT_INFO:
                                    {
                                        int number_of_heart_beats_pending = intent.getIntExtra(BluetoothLeInstapatch.HEART_BEATS_PENDING, 0);

                                        patient_gateway_outgoing_commands.reportNumberOfLifetouchHeartBeatsPending(number_of_heart_beats_pending);

                                        ArrayList<HeartBeatInfo> heart_beat_list = intent.getParcelableArrayListExtra(BluetoothLeInstapatch.HEART_BEAT_INFO);

                                        for (HeartBeatInfo this_heart_beat : heart_beat_list)
                                        {
                                            if (this_heart_beat.getAmplitude() == ErrorCodes.ERROR_CODE__LIFETOUCH_NO_TIMESTAMP)
                                            {
                                                // Set the timestamp and ignore this data
                                                Log.e(TAG, "Received no_timestamp error code : re-sending timestamp as Session Start Time is now valid");

                                                device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());

                                                patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                                                // Reset the heart beat processing to remove any remaining data that was received before the new timestamp
                                                lifetouch_heart_beat_processor.reset();
                                            }
                                            else
                                            {
                                                // Add the Lifetouch's Start Date as this timestamp from the Lifetouch is only the number of milliseconds since Midnight on that Start Date
                                                this_heart_beat.setTimestampInMs(this_heart_beat.getTimestampInMs() + device_info.start_date_at_midnight_in_milliseconds);


                                                // Only process the received data is there is currently a Patient Session in progress.....
                                                if (isPatientSessionRunning())
                                                {
                                                    // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                                    if (device_info.isDeviceSessionInProgress())
                                                    {
                                                        Date heart_beat_timestamp_as_date = new Date(this_heart_beat.getTimestampInMs());

                                                        // Only process the received data if the Heartbeat's timestamp is after the start of the device session
                                                        if (heart_beat_timestamp_as_date.getTime() >= device_info.device_session_start_time)
                                                        {
                                                            lifetouch_heart_beat_processor.processMeasurement(this_heart_beat);

                                                            if (this_heart_beat.isBeatRealTime())
                                                            {
                                                                if (this_heart_beat.getAmplitude() == ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED)
                                                                {
                                                                    device_info.no_measurements_detected = true;
                                                                }
                                                                else if (this_heart_beat.getAmplitude() < ErrorCodes.ERROR_CODES)
                                                                {
                                                                    device_info.no_measurements_detected = false;
                                                                }

                                                                patient_gateway_outgoing_commands.reportLifetouchNoBeatsDetectedTimerStatus(device_info.no_measurements_detected);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            // Should only happen during development
                                                            Log.e(TAG, "Rx'ed HEART_BEATS_BEAT_INFO but IGNORED AS TIMESTAMP BEFORE THE START OF THIS DEVICE SESSION : " + Utils.explainHeartBeat(this_heart_beat) + ". Device Session Start = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(device_info.device_session_start_time));
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Log.e(TAG, "Rx'ed HEART_BEATS_BEAT_INFO but IGNORED as no Device Session in progress : " + Utils.explainHeartBeat(this_heart_beat));
                                                    }
                                                }
                                                else
                                                {
                                                    Log.e(TAG, "Rx'ed HEART_BEATS_BEAT_INFO but IGNORED as no Patient Session in progress : " + Utils.explainHeartBeat(this_heart_beat));
                                                }
                                            }
                                        }

                                        // Now all heat beats processed, if no beats pending then can process outstanding heart rates
                                        if (number_of_heart_beats_pending == 0)
                                        {
                                            lifetouch_heart_beat_processor.processAnyOutstandingData();
                                        }
                                    }
                                    break;

                                    case BluetoothLeInstapatch.DATATYPE_SETUP_MODE_RAW_SAMPLES:
                                    {
                                        // Only process the received data is there is currently a Patient Session in progress.....
                                        if (isPatientSessionRunning())
                                        {
                                            // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                            if (device_info.isDeviceSessionInProgress())
                                            {
                                                Bundle bundle = intent.getExtras();
                                                ArrayList<MeasurementSetupModeDataPoint> setup_mode_datapoints = bundle.getParcelableArrayList(BluetoothLeInstapatch.SETUP_MODE_DATA);

                                                Log.d(TAG, "BluetoothLeInstapatch.DATATYPE_SETUP_MODE_RAW_SAMPLES : number_of_samples " + setup_mode_datapoints.size());

                                                for (MeasurementSetupModeDataPoint datapoint : setup_mode_datapoints)
                                                {
                                                    Log.d(TAG, "DATATYPE_SETUP_MODE_RAW_SAMPLES : TS = " + datapoint.timestamp_in_ms + " = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(datapoint.timestamp_in_ms + device_info.timestamp_offset) + " = " + datapoint.sample);

                                                    datapoint.timestamp_in_ms += device_info.timestamp_offset;

                                                    if (datapoint.sample != BluetoothLeInstapatch.GAP_IN_DATA)
                                                    {
                                                        processTimestampForSetupModeLogEntry(device_info, datapoint.timestamp_in_ms);
                                                    }

                                                    // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                                                    // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                                                    device_info.queue_setup_mode_datapoints.add(datapoint);

                                                    if (device_info.isInServerInitedRawDataMode())
                                                    {
                                                        device_info.queue_setup_mode_datapoints_for_server.add(datapoint);
                                                    }

                                                    patient_gateway_outgoing_commands.sendNewSetupModeSampleToUserInterface(device_info.device_type, datapoint);
                                                }

                                                if (device_info.isInServerInitedRawDataMode())
                                                {
                                                    server_syncing.sendDeviceSetupModeData(patient_session_info.server_patient_session_id, device_info, 1);
                                                }
                                            }
                                            else
                                            {
                                                Log.e(TAG, "Rx'ed SETUP_MODE_RAW_SAMPLES but IGNORED as no Device Session in progress");
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed SETUP_MODE_RAW_SAMPLES but IGNORED as no Patient Session in progress");
                                        }
                                    }
                                    break;

//                                    case BluetoothLeInstapatch.DATATYPE_RAW_ACCELEROMETER_RAW_SAMPLES:
//                                    {
//                                        // Only process the received data is there is currently a Patient Session in progress.....
//                                        if (isPatientSessionRunning())
//                                        {
//                                            // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
//                                            if (device_info.isDeviceSessionInProgress())
//                                            {
//                                                short[] x_axis_samples = intent.getShortArrayExtra(BluetoothLeInstapatch.RAW_ACCELEROMETER_MODE__X_AXIS_SAMPLES);
//                                                short[] y_axis_samples = intent.getShortArrayExtra(BluetoothLeInstapatch.RAW_ACCELEROMETER_MODE__Y_AXIS_SAMPLES);
//                                                short[] z_axis_samples = intent.getShortArrayExtra(BluetoothLeInstapatch.RAW_ACCELEROMETER_MODE__Z_AXIS_SAMPLES);
//                                                long[] timestamps = intent.getLongArrayExtra(BluetoothLeInstapatch.RAW_ACCELEROMETER_MODE__TIMESTAMPS);
//
//                                                //Log.d(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLES");
//
//                                                // Data will always coming in 3's
//                                                int number_of_samples = x_axis_samples.length;
//
//                                                for (int i = 0; i < number_of_samples; i++)
//                                                {
//                                                    timestamps[i] += device_info.start_date_at_midnight_in_milliseconds;
//
//                                                    RawAccelerometerModeDataPoint datapoint = new RawAccelerometerModeDataPoint();
//
//                                                    datapoint.x = x_axis_samples[i];
//                                                    datapoint.y = y_axis_samples[i];
//                                                    datapoint.z = z_axis_samples[i];
//                                                    datapoint.timestamp = timestamps[i];
//
//                                                    Log.d(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLE : x = " + datapoint.x + " : y = " + datapoint.y + " : " + datapoint.z + " : time = " + datapoint.timestamp);
//
//                                                    // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
//                                                    // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
//                                                    queue_lifetouch_raw_accelerometer_mode_datapoints.add(datapoint);
//
//                                                    if (device_info.isInServerInitedRawDataMode())
//                                                    {
//                                                        queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.add(datapoint);
//                                                    }
//                                                }
//
//                                                patient_gateway_outgoing_commands.sendNewLifetouchRawAccelerometerModeData(x_axis_samples, y_axis_samples, z_axis_samples);
//
//                                                // Before sending setup mode data, check connection
//                                                if (device_info.isInServerInitedRawDataMode())
//                                                {
//                                                    if (queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.size() > 0)
//                                                    {
//                                                        // Create JSON array and populate it with setup data
//                                                        JsonArray json_array = new JsonArray();
//
//                                                        for (RawAccelerometerModeDataPoint data_point : queue_lifetouch_raw_accelerometer_mode_datapoints_for_server)
//                                                        {
//                                                            JsonObject json_object = new JsonObject();
//                                                            json_object.addProperty("x", data_point.x);
//                                                            json_object.addProperty("y", data_point.y);
//                                                            json_object.addProperty("z", data_point.z);
//                                                            json_object.addProperty("timestamp", data_point.timestamp);
//                                                            json_array.add(json_object);
//                                                        }
//
//                                                        // Add "points" string in front of message
//                                                        JsonObject json_data = new JsonObject();
//                                                        json_data.addProperty("sensor_type", device_info.sensor_type.ordinal());
//                                                        json_data.addProperty("device_type", device_info.device_type.ordinal());
//                                                        json_data.addProperty("server_patient_session_id", patient_session_info.server_patient_session_id);
//
//                                                        json_data.add("points", json_array);
//
//                                                        server_syncing.sendDeviceRawAccelerometerModeDataIfServerConnected(json_data);
//
//                                                        // After sending data clear array.
//                                                        queue_lifetouch_raw_accelerometer_mode_datapoints_for_server.clear();
//                                                    }
//                                                }
//                                            }
//                                            else
//                                            {
//                                                Log.e(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLES but IGNORED as no Device Session in progress");
//                                            }
//                                        }
//                                        else
//                                        {
//                                            Log.e(TAG, "Rx'ed RAW_ACCELEROMETER_RAW_SAMPLES but IGNORED as no Patient Session in progress");
//                                        }
//                                    }
//                                    break;

                                    case BluetoothLeInstapatch.DATATYPE_HEART_BEAT_LEADS_OFF:
                                    {
                                        // Only update if leads status is newer than previous status
                                        long leads_status_timestamp = intent.getLongExtra(BluetoothLeInstapatch.HEART_BEAT_LEADS_OFF_TIMESTAMP, -1);

                                        if (leads_status_timestamp > device_info.last_device_disconnected_timestamp)
                                        {
                                            device_info.last_device_disconnected_timestamp = leads_status_timestamp;

                                            // False = on the body
                                            // True = off the body
                                            device_info.device_disconnected_from_body = intent.getBooleanExtra(BluetoothLeInstapatch.HEART_BEAT_LEADS_OFF, false);

                                            // Handler is fired after 1 sec of leads-off message received from lifetouchBle service
                                            // If leads-off is reported soon than 1 sec then previously added message is removed and recent leads-off status is added
                                            // remove all the previous messages
                                            lifetouch_leads_off_handler.removeCallbacksAndMessages(null);

                                            // Create the runnable and run after 1 second
                                            lifetouch_leads_off_handler.postDelayed(() -> {
                                                // If lifetouch is disconnected with in 1 sec of leads-off detected then don't update the UI
                                                if (device_info.isActualDeviceConnectionStatusConnected())
                                                {
                                                    if (device_info.device_disconnected_from_body == false)
                                                    {
                                                        Log.e(TAG, "Lifetouch on body : reporting to UI");
                                                    }
                                                    else
                                                    {
                                                        Log.e(TAG, "Lifetouch OFF body : reporting to UI");
                                                        resetPatientOrientation();
                                                    }

                                                    //Report leads-off status to UI
                                                    patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

                                                    server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);
                                                }
                                                else
                                                {
                                                    Log.w(TAG, "Lifetouch leads-off detected. But Lifetouch is disconnected");
                                                }
                                            }, (int) DateUtils.SECOND_IN_MILLIS);
                                        }
                                    }
                                    break;

                                    case BluetoothLeInstapatch.DATATYPE_GET_TIMESTAMP:
                                    {
                                        Log.i(TAG, "Received " + data_type + " : " + data);

                                        handleReceivedIsansysFormatTimestamp(device_info, data);
                                    }
                                    break;

                                    default:
                                    {
                                        // data_type == Unknown
                                        Log.e(TAG, "Unhandled in mGattUpdateReceiver_LifetouchGreen : Data Type = " + data_type + "   Data = " + data);
                                    }
                                    break;
                                }

                                // Log.i(CLASS_TAG, data_type + " : " + data);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                            Log.e(TAG, "exception occurred:");
                            Log.e(TAG, e.toString());
                        }
                    }
                    break;

                    case BluetoothLeInstapatch.ACTION_AUTHENTICATION_PASSED:
                    {
                        Log.d(TAG, "ACTION_AUTHENTICATION_PASSED");

                        putDeviceIntoDesiredOperatingMode(device_info);
                    }
                    break;

                    case BluetoothLeInstapatch.ACTION_AUTHENTICATION_FAILED:
                    {
                        Log.d(TAG, "ACTION_AUTHENTICATION_FAILED");
                    }
                    break;
                }
            }
            else
            {
                Log.d(TAG, "mGattUpdateReceiver_LifetouchGreen ACTION RECEIVED but NO VALID DEVICE FOUND IN SESSION");
            }
        }
    };


    private void removeDeviceFromSweetblue(BtleSensorDevice device_info)
    {
        device_info.disconnectDevice();
        patient_gateway_outgoing_commands.sendBleDeviceChangeSessionDisconnected(device_info.device_type);
    }


    private void removeDeviceDisconnection(DeviceInfo device_info)
    {
        removeDeviceDisconnection(device_info, true, true);
    }


    private void removeDeviceDisconnection(DeviceInfo device_info, boolean stop_setup_mode_timer_running, boolean send_low_level_exit_mode_commands)
    {
        device_info.saveOperatingModeWhenDisconnected();

        Log.d(TAG, "removeDeviceDisconnection : " + device_info.device_type + " : " + device_info.getOperatingModeWhenDisconnected() + " : stop_setup_mode_timer_running = " + stop_setup_mode_timer_running);

        switch (device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                resetPatientOrientation();

                exitSetupModeIfRunning(device_info, send_low_level_exit_mode_commands, stop_setup_mode_timer_running);

                exitRawAccelerometerModeIfRunning(device_info, send_low_level_exit_mode_commands);
            }
            break;

            case SENSOR_TYPE__TEMPERATURE:
            {
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                switch (device_info.device_type)
                {
                    case DEVICE_TYPE__NONIN_WRIST_OX:
                    case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
                    {
                        exitSetupModeIfRunning(device_info, send_low_level_exit_mode_commands, stop_setup_mode_timer_running);
                    }
                    break;
                }
            }
            break;
        }

        handleDisconnection(device_info);
    }


    private void resetPatientOrientation()
    {
        last_patient_orientation = PatientPositionOrientation.ORIENTATION_UNKNOWN;
        last_patient_orientation_timestamp = 0;
    }


    private void handleConnection(DeviceInfo device_info)
    {
        Log.d(TAG, "handleConnection : " + device_info.device_type);

        storeConnectionEventIfSessionRunning(device_info, LocalDatabaseStorage.ConnectionEvent.DEVICE_CONNECTED);

        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);

        // Introduced with Nonin BLE Playback to stop "Nonin WristOx not attached" messages flashing up during connections when playing back, clearing memory etc.
        // Current leads off status is not actually known, so now assuming it's attached until notified otherwise
        device_info.device_disconnected_from_body = false;
        patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

        // Tell the UI about the actual_device_connection_status change
        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

        // Tell Server about the new connection status
        reportGatewayStatusToServer();

        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

        checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
    }


    private void putDeviceIntoDesiredOperatingMode(DeviceInfo device_info)
    {
        if (device_info.isDeviceSessionInProgress())
        {
            // Device has reconnected to Gateway, either from out of range, or radio watchdog has fired and reset the device radio
            if (device_info.wasDeviceInSetupModeBeforeDisconnection())
            {
                String log_line = "putDeviceIntoDesiredOperatingMode : " + device_info.device_type + " was in Setup Mode before connecting : setup_mode_time_left_in_seconds = " + device_info.setup_mode_time_left_in_seconds;

                // No point doing this with less time as there may not be enough time to go "out" and "in" before the timer runs out
                if (device_info.setup_mode_time_left_in_seconds > 30)
                {
                    Log.e(TAG, log_line + " : Attempting to go back into Setup Mode");

                    enterDeviceSetupMode(device_info, device_info.getOperatingModeWhenDisconnected(), true, device_info.setup_mode_time_left_in_seconds);
                }
                else
                {
                    Log.e(TAG, log_line + " : Not enough time to go back into setup mode as less than 30 seconds left on Setup Mode timer or not in continuous setup mode");

                    // report device not in setup mode and stop the timer, as device not going back in to setup mode.
                    patient_gateway_outgoing_commands.sendCommandReportDeviceOperatingMode(device_info);

                    device_info.resetDeviceInfoSetupModeTimer();
                }
            }
            else if (device_info.wasDeviceInRawAccelerometerModeBeforeDisconnection())
            {
                Log.e(TAG, "putDeviceIntoDesiredOperatingMode : " + device_info.getSensorTypeAndDeviceTypeAsString() + " was in Raw Accelerometer Mode before connecting");

                enterDeviceRawAccelerometerMode((BtleSensorDevice)device_info, device_info.getOperatingModeWhenDisconnected());
            }
            else
            {
                Log.e(TAG, "putDeviceIntoDesiredOperatingMode : " + device_info.getSensorTypeAndDeviceTypeAsString() + " was in Normal Mode before connecting");
            }
        }
        else
        {
            Log.e(TAG, "putDeviceIntoDesiredOperatingMode : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : First connection");
        }
    }


    private void handleDisconnection(DeviceInfo device_info)
    {
        storeConnectionEventIfSessionRunning(device_info, LocalDatabaseStorage.ConnectionEvent.DEVICE_DISCONNECTED);

        if(device_info.isActualDeviceConnectionStatusNotPaired())
        {
            Log.e(TAG, "Disconnect received, but device already unpaired!");
        }
        else if(device_info.isActualDeviceConnectionStatusUnexpectedlyUnbonded())
        {
            Log.w(TAG, "Disconnect received, but device was unexpectedly unbonded");
        }
        else
        {
            Log.d(TAG, "Setting to DeviceConnectionStatus.DISCONNECTED");
            device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.DISCONNECTED);
        }

        // Remove any "Leads Off/Finger Off" status if the device is disconnected as dont "know" what the state is now
        device_info.device_disconnected_from_body = false;
        server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);

        // Tell Server about the new connection status
        reportGatewayStatusToServer();

        // Tell the UI about the actual_device_connection_status change
        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

        if (device_info.desired_device_connection_status == DeviceConnectionStatus.CONNECTED)
        {
            Log.i(TAG, "Updating UI -> " + device_info.getSensorTypeAndDeviceTypeAsString() + " BLUETOOTH_DEVICE_TEMPORARILY_DISCONNECTED");

            patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_TEMPORARILY_DISCONNECTED);
        }
        else
        {
            Log.i(TAG, "Updating UI -> " + device_info.getSensorTypeAndDeviceTypeAsString() + " BLUETOOTH_DEVICE_DISCONNECTED");

            patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_DISCONNECTED);
        }
    }


    private static long lastTemperaturePacketId = -1;


    // Handles various events fired by the Service.
    // ACTION_CONNECTED: connected to a GATT server.
    // ACTION_DISCONNECTED: disconnected from a GATT server.
    // ACTION_AVAILABLE: received data from the device.  This can be a result of read or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver_Lifetemp_V2 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            final BtleSensorDevice device_info = (BtleSensorDevice) device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__LIFETEMP_V2);

            switch (action)
            {
                case BluetoothLeLifetempV2.ACTION_CONNECTED:
                {
                    Log.d(TAG, "BluetoothLeLifetempV2.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersion());

                    handleConnection(device_info);
                }
                break;

                case BluetoothLeLifetempV2.ACTION_DISCONNECTED:
                {
                    Log.e(TAG, "BluetoothLeLifetempV2.ACTION_DISCONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersion());

                    removeDeviceDisconnection(device_info);
                }
                break;

                case BluetoothLeLifetempV2.ACTION_TURNED_OFF:
                {
                    Log.d(TAG, "BluetoothLeLifetempV2.ACTION_TURNED_OFF");

                    removeDeviceFromSweetblue(device_info);
                }
                break;

                case BluetoothLeLifetempV2.ACTION_DATA_AVAILABLE:
                {
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                    try
                    {
                        String data = intent.getStringExtra(BluetoothLeLifetempV2.DATA_AS_STRING);
                        String data_type = intent.getStringExtra(BluetoothLeLifetempV2.DATA_TYPE);
                        String device_address = intent.getStringExtra(BluetoothLeLifetempV2.DEVICE_ADDRESS);

                        if ((data == null) || (device_address == null) || (data_type == null))
                        {
                            if (data == null)
                            {
                                Log.d(TAG, "data = null");
                            }

                            if (data_type == null)
                            {
                                Log.d(TAG, "data_type = null");
                            }

                            if (device_address == null)
                            {
                                Log.d(TAG, "device_address = null");
                            }
                        }
                        else
                        {
                            switch (data_type)
                            {
                                case BluetoothLeLifetempV2.DATATYPE_TEMPERATURE_MEASUREMENT:
                                {
                                    long packetId = intent.getLongExtra(BluetoothLeLifetempV2.PACKET_ID, -1);

                                    if (!disableCommentsForSpeed())
                                    {
                                        Log.d(TAG, "Temperature : " + data);
                                    }

                                    String string_temperature = data.split("__")[0];
                                    String string_timestamp = data.split("__")[1];
                                    String string_battery_percentage = data.split("__")[2];
                                    String string_battery_voltage = data.split("__")[3];
                                    String string_measurements_pending = data.split("__")[4];

                                    String string_temperature_integer_part = string_temperature.split("\\.")[0];

                                    double battery_percentage = Double.parseDouble(string_battery_percentage);
                                    double battery_voltage = Double.parseDouble(string_battery_voltage);
                                    double temperature_in = Double.parseDouble(string_temperature);
                                    int temperature_integer_part = Integer.parseInt(string_temperature_integer_part);

                                    BigDecimal temperature_value = new BigDecimal(temperature_in);

                                    if (gateway_settings.getManufacturingModeEnabledStatus())
                                    {
                                        temperature_value = temperature_value.setScale(2, RoundingMode.HALF_UP);
                                    }
                                    else
                                    {
                                        temperature_value = temperature_value.setScale(1, RoundingMode.HALF_UP);
                                    }

                                    long timestamp_in_ms;

                                    int measurements_pending = Integer.parseInt(string_measurements_pending);
                                    patient_gateway_outgoing_commands.reportNumberOfLifetempMeasurementsPending(measurements_pending);

                                    if (device_info.dummy_data_mode)
                                    {
                                        timestamp_in_ms = Long.parseLong(string_timestamp);
                                    }
                                    else
                                    {
                                        timestamp_in_ms = Integer.parseInt(string_timestamp) + device_info.start_date_at_midnight_in_milliseconds;
                                    }

                                    // Round timestamp down to multiples of 1 second. This discards milliseconds, and should result in timestamps at 0 seconds and 0 milliseconds past the minute, as with the HR, RR and SpO2
                                    timestamp_in_ms /= DateUtils.SECOND_IN_MILLIS;
                                    timestamp_in_ms *= DateUtils.SECOND_IN_MILLIS;

                                    boolean repeatedPacketId = (lastTemperaturePacketId == packetId);

                                    lastTemperaturePacketId = packetId;

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if ((isPatientSessionRunning()) && (repeatedPacketId == false))
                                    {
                                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            if (temperature_integer_part == ErrorCodes.ERROR_CODE__LIFETEMP_LEADS_OFF)
                                            {
                                                if (!disableCommentsForSpeed())
                                                {
                                                    Log.i(TAG, "Lifetemp Leads off Received at " + new Date(timestamp_in_ms) + ". Battery Voltage = " + battery_voltage);
                                                }
                                            }
                                            else
                                            {
                                                if (!disableCommentsForSpeed())
                                                {
                                                    Log.i(TAG, "Temperature Received : " + temperature_in + " at " + new Date(timestamp_in_ms) + " = " + temperature_value.doubleValue() + ". Battery Voltage = " + battery_voltage);
                                                }
                                            }

                                            // Store measurement in database
                                            MeasurementTemperature measurement = new MeasurementTemperature(temperature_value.doubleValue(), timestamp_in_ms, getNtpTimeNowInMilliseconds());

                                            local_database_storage.storeLifetempTemperatureMeasurement(device_info, measurement);

                                            reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.TEMPERATURE, measurement);

                                            if (timestamp_in_ms > device_info.last_device_disconnected_timestamp)
                                            {
                                                // Update leads state
                                                device_info.device_disconnected_from_body = temperature_integer_part == ErrorCodes.ERROR_CODE__LIFETEMP_LEADS_OFF;

                                                device_info.last_device_disconnected_timestamp = timestamp_in_ms;
                                            }

                                            patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

                                            server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);

                                            // The battery is sampled every time the radio tries to transmit the temperature data. This is NOT the same as the timestamp of when the temperature measurement was taken
                                            long timestamp = getNtpTimeNowInMilliseconds();

                                            MeasurementBatteryReading battery_measurement = new MeasurementBatteryReading(
                                                    (int) battery_voltage,
                                                    (int) battery_percentage,
                                                    timestamp_in_ms,
                                                    getNtpTimeNowInMilliseconds());

                                            // Save the Battery reading so can be read from the UI on demand
                                            device_info.setLastBatteryReading((int) battery_percentage, (int) battery_voltage, timestamp);

                                            patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                            local_database_storage.storeLifetempBatteryMeasurement(device_info, battery_measurement);
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed Lifetemp Temperature reading but IGNORED as no Device Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                        }
                                    }
                                    else
                                    {
                                        if (repeatedPacketId)
                                        {
                                            Log.e(TAG, "Rx'ed Lifetemp Temperature reading but IGNORED as packet is REPEATED - Packet ID : " + packetId + ", timestamp : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed Lifetemp Temperature reading but IGNORED as no Patient Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms));
                                        }
                                    }
                                }
                                break;

                                case BluetoothLeLifetempV2.DATATYPE_FIRMWARE_REVISION:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    try
                                    {
                                        int firmware_int = intent.getIntExtra(BluetoothLeLifetempV2.FIRMWARE_VERSION_NUMBER, -1);
                                        String firmware_version_string = data.trim();

                                        Log.i(TAG, "Received Lifetemp Firmware Version : " + firmware_int);

                                        device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                    }
                                    catch (NumberFormatException x)
                                    {
                                        Log.e("ERROR", "lifetemp_firmware_revision INVALID : " + data);
                                    }
                                }
                                break;

                                case BluetoothLeLifetempV2.DATATYPE_GET_TIMESTAMP:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    handleReceivedIsansysFormatTimestamp(device_info, data);
                                }
                                break;

                                case BluetoothLeLifetempV2.DATATYPE_LIFETEMP_MEASUREMENT_INTERVAL:
                                {
                                    device_info.setMeasurementInterval();
                                }
                                break;

                                default:
                                {
                                    // data_type == Unknown
                                    Log.e(TAG, "Unhandled in mGattUpdateReceiver_Lifetemp_V2 : Data Type = " + data_type + "   Data = " + data);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    };


    private final float TIME_DIFF_FOR_EACH_PLETH_DATA = (float) 13.3333;

    private int nonin_pairing_failure_count = 0;

    private final BroadcastReceiver mGattUpdateReceiver_NoninWristOx3150Ble = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            final BtleSensorDevice device_info = (BtleSensorDevice) device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE);

            spO2_spot_measurements_enabled = gateway_settings.getEnableSpO2SpotMeasurements();

            switch (action)
            {
                case BluetoothLeNoninWristOx.ACTION_PAIRING:
                {
                    Log.d(TAG, "BluetoothLeNoninWristOx.ACTION_PAIRING");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING);
                }
                break;

                case BluetoothLeNoninWristOx.ACTION_PAIRING_SUCCESS:
                {
                    Log.d(TAG, "BluetoothLeNoninWristOx.ACTION_PAIRING_SUCCESS");

                    nonin_pairing_failure_count = 0;

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);
                }
                break;

                case BluetoothLeNoninWristOx.ACTION_PAIRING_FAILURE:
                {
                    Log.d(TAG, "BluetoothLeNoninWristOx.ACTION_PAIRING_FAILURE");


                    if((current_scan_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE)
                        &&(nonin_pairing_failure_count < 1))
                    {
                        nonin_pairing_failure_count++;

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.SEARCHING);

                        // Tell the UI about the actual_device_connection_status change
                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_SCAN_STARTED);

                        connectNextDeviceToGatewayIfNeeded();
                    }
                    else
                    {
                        nonin_pairing_failure_count = 0;

                        btle_scanning_timeout_handler.removeCallbacksAndMessages(null);

                        stopOnGoingBluetoothScan();

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                        // Tell the UI about the actual_device_connection_status change
                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING_FAILED);

                        checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                    }
                }
                break;

                case BluetoothLeNoninWristOx.ACTION_CONNECTED:
                {
                    Log.d(TAG, "BluetoothLeNoninWristOx.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    handleConnection(device_info);

                    spo2_intermediate_tidy_up_timer.cancel();

                    nonin_pairing_failure_count = 0;

                    putDeviceIntoDesiredOperatingMode(device_info);
                }
                break;

                case BluetoothLeNoninWristOx.ACTION_DISCONNECTED:
                {
                    Log.e(TAG, "BluetoothLeNoninWristOx.ACTION_DISCONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    nonin_pairing_failure_count = 0;

                    restartSpO2IntermediateTidyUpTimerIfRequired();

                    removeDeviceDisconnection(device_info);
                }
                break;

                case BluetoothLeNoninWristOx.ACTION_DATA_AVAILABLE:
                {
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                    try
                    {
                        String data = intent.getStringExtra(BluetoothLeNoninWristOx.DATA_AS_STRING);
                        String data_type = intent.getStringExtra(BluetoothLeNoninWristOx.DATA_TYPE);
                        String device_address = intent.getStringExtra(BluetoothLeNoninWristOx.DEVICE_ADDRESS);

                        if ((data == null) || (device_address == null) || (data_type == null))
                        {
                            if (data == null)
                            {
                                Log.d(TAG, "data = null");
                            }

                            if (data_type == null)
                            {
                                Log.d(TAG, "data_type = null");
                            }

                            if (device_address == null)
                            {
                                Log.d(TAG, "device_address = null");
                            }
                        }
                        else
                        {
                            switch (data_type)
                            {
                                case BluetoothLeNoninWristOx.DATATYPE_SPO2_MEASUREMENT:
                                {
                                    boolean valid_reading = intent.getBooleanExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__VALID_READING, false);

                                    int sp_o2 = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__SPO2, -1);
                                    int pulse = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__PULSE_RATE, -1);
                                    int counter = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__COUNTER, -1);
//TODO handle missing packets
                                    long timestamp_in_ms = intent.getLongExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__TIMESTAMP, -1);
                                    String timestamp_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms);

                                    int pulse_amplitude_index = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__PULSE_AMPLITUDE_INDEX, -1);
                                    int battery_voltage = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__BATTERY_VOLTAGE, -1);
                                    int battery_percentage = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE, -1);

                                    // Info decoded from Status byte
                                    boolean encryption = intent.getBooleanExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__ENCRYPTION, false);
                                    boolean low_battery = intent.getBooleanExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_BATTERY, false);
                                    device_info.device_disconnected_from_body =  intent.getBooleanExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT, false);
                                    boolean searching = intent.getBooleanExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__SEARCHING, false);
                                    boolean smartpoint_algorithm = intent.getBooleanExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__SMART_POINT_ALGORITHM, false);
                                    boolean low_weak_signal = intent.getBooleanExtra(BluetoothLeNoninWristOx.NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_WEAK_SIGNAL, false);

                                    Log.i(TAG, "BluetoothLeNoninWristOx.DATATYPE_SPO2_MEASUREMENT : SpO2 = " + sp_o2
                                            + ". pulse = " + pulse
                                            + ". counter = " + counter
                                            + ". timestamp_in_ms = " + timestamp_as_string
                                            + ". pulse_amplitude_index = " + pulse_amplitude_index
                                            + ". battery_voltage = " + battery_voltage
                                            + ". battery_percentage = " + battery_percentage
                                            + ". encryption = " + encryption
                                            + ". low_battery = " + low_battery
                                            + ". pulse_ox_detached_from_patient = " + device_info.device_disconnected_from_body
                                            + ". searching = " + searching
                                            + ". smartpoint_algorithm = " + smartpoint_algorithm
                                            + ". low_weak_signal = " + low_weak_signal
                                    );

                                    // Wait until there is a Patient Session AND Device Session in progress before processing the data.
                                    // If the user has scanned the Pulse Ox QR code, and put it on their finger, it will take measurements. However they might not have pressed "Starting Monitoring" which
                                    // would start a new Patient/Device Session off. This means that all the Device Session Info etc is invalid
                                    if (isPatientSessionRunning())
                                    {
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            // Tell the UI if the Pulse Ox is on the finger or not
                                            patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

                                            // Nonin Data is send after three packets of setup mode data
                                            if(device_info.isInSetupMode() == false)
                                            {
                                                server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);
                                            }

                                            // Assume invalid measurement to start with.
                                            IntermediateSpO2 pulse_ox_intermediate_measurement = new IntermediateSpO2(INVALID_MEASUREMENT, INVALID_MEASUREMENT, timestamp_in_ms);

                                            // If pulse ox is on patient....
                                            if(device_info.device_disconnected_from_body == false)
                                            {
                                                // ....process newly received data
                                                if (valid_reading)
                                                {
                                                    if (!disableCommentsForSpeed())
                                                    {
                                                        Log.d(TAG, "pulse_ox_intermediate_measurement at " + timestamp_as_string + " = " + sp_o2 + ". android_database_device_session_id = " + device_info.getAndroidDeviceSessionId());
                                                    }

                                                    // Update the measurement with the valid readings
                                                    pulse_ox_intermediate_measurement.setSpO2(sp_o2);
                                                    pulse_ox_intermediate_measurement.setPulse(pulse);

                                                    if(send_pulse_ox_battery_level == false)
                                                    {
                                                        // Save the Battery reading so can be read from the UI on demand
                                                        device_info.setLastBatteryReading(battery_percentage, battery_voltage, timestamp_in_ms);

                                                        // Show the battery on the UI screen as soon as possible after connecting.
                                                        patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                                        send_pulse_ox_battery_level = true;
                                                    }

                                                    // Reset the leads-off counter.
                                                    device_info.counter_leads_off_after_last_valid_data = 0;
                                                    device_info.timestamp_leads_off_disconnection = 0;
                                                }
                                                else
                                                {
                                                    // Invalid intermediate measurement
                                                    Log.e(TAG, "pulse_ox_intermediate_measurement Invalid Reading @ " + timestamp_as_string);
                                                }
                                            }
                                            else
                                            {
                                                // Leads-off detected. Increment leads-off counters
                                                device_info.counter_leads_off_after_last_valid_data++;
                                                device_info.timestamp_leads_off_disconnection = getNtpTimeNowInMilliseconds();
                                                device_info.counter_total_leads_off++;

                                                // Pulse Ox Finger Off detected
                                                Log.e(TAG, "pulse_ox_intermediate_measurement Finger-off @ " + timestamp_as_string);
                                            }

                                            boolean has_one_minute_elapsed;

                                            has_one_minute_elapsed = intermediate_spo2_processor.processMeasurement(pulse_ox_intermediate_measurement); //, gateway_settings.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid());

                                            has_one_minute_elapsed |= intermediate_spo2_processor.processAnyOutstandingData();
                                            
                                            // Save the Battery reading so can be read from the UI on demand
                                            device_info.setLastBatteryReading(battery_percentage, battery_voltage, timestamp_in_ms);

                                            // Write Nonin Battery value
                                            patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                            if (has_one_minute_elapsed)
                                            {
                                                Log.e(TAG, "Store Nonin BLE Battery measurement : battery_percentage = " + battery_percentage + "% : battery_voltage = " + battery_voltage);

                                                int battery_millivolts = battery_voltage * 100;

                                                MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                                        battery_millivolts,
                                                        battery_percentage,
                                                        timestamp_in_ms,
                                                        getNtpTimeNowInMilliseconds());

                                                local_database_storage.storeOximeterBatteryMeasurement(device_info, measurement);
                                            }

                                            // Sync intermediate measurements every minute in timer tick
                                        }
                                        else
                                        {
                                            Log.e(TAG, "pulse_ox_intermediate_measurement ERROR : Received value but no Device Session yet");
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received pulse_ox_intermediate_measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms) + " but ignored as no Patient Session in progress");
                                    }
                                }
                                break;

                                case BluetoothLeNoninWristOx.DATATYPE_DEVICE_STATUS:
                                {
                                    int sensor_type = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__SENSOR_TYPE, -1);
                                    int error = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__ERROR, -1);
                                    int battery_voltage = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__BATTERY_VOLTAGE, -1);
                                    int battery_percentage = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__BATTERY_PERCENTAGE, -1);
                                    int tx_index = intent.getIntExtra(BluetoothLeNoninWristOx.NONIN_DEVICE_STATUS__TX_INDEX, -1);

                                    Log.i(TAG, "BluetoothLeNoninWristOx.DATATYPE_DEVICE_STATUS : sensor_type = " + sensor_type
                                            + ". error = " + error
                                            + ". battery_voltage = " + battery_voltage
                                            + ". battery_percentage = " + battery_percentage
                                            + ". tx_index = " + tx_index
                                    );
                                }
                                break;

                                case BluetoothLeNoninWristOx.DATATYPE_PPG_DATA:
                                {
                                    // Wait until there is a Patient Session AND Device Session in progress before processing the data.
                                    // If the user has scanned the Pulse Ox QR code, and put it on their finger, it will take measurements. However they might not have pressed "Starting Monitoring" which
                                    // would start a new Patient/Device Session off. This means that all the Device Session Info etc is invalid
                                    if ((isPatientSessionRunning()) && (device_info.isDeviceSessionInProgress()))
                                    {
                                        Bundle bundle = intent.getExtras();
                                        ArrayList<MeasurementSetupModeDataPoint> setup_mode_datapoints = bundle.getParcelableArrayList(BluetoothLeNoninWristOx.PPG_DATA_POINTS);

                                        Log.d(TAG, "BluetoothLeNoninWristOx.DATATYPE_PPG_DATA. Number of samples = " + setup_mode_datapoints.size());

                                        for(MeasurementSetupModeDataPoint datapoint : setup_mode_datapoints)
                                        {
                                            processTimestampForSetupModeLogEntry(device_info, datapoint.timestamp_in_ms);

                                            // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                                            // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                                            device_info.queue_setup_mode_datapoints.add(datapoint);

                                            if (device_info.isInServerInitedRawDataMode())
                                            {
                                                device_info.queue_setup_mode_datapoints_for_server.add(datapoint);
                                            }

                                            patient_gateway_outgoing_commands.sendNewSetupModeSampleToUserInterface(device_info.device_type, datapoint);
                                        }

                                        if (device_info.isInServerInitedRawDataMode())
                                        {
                                            server_syncing.sendDeviceSetupModeData(patient_session_info.server_patient_session_id, device_info, 1);
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "NONIN_WRIST_OX__NEW_SETUP_DATA. Received data but ignored as no Patient or Device Session in progress");
                                    }
                                }
                                break;
/*
                                case BluetoothLeNoninWristOx.DATATYPE_BATTERY_LEVEL:
                                {
                                    final int NO_TIME_STAMP = -1;

                                    int battery_percentage = intent.getIntExtra(BluetoothLeNoninWristOx.BATTERY_PERCENTAGE, 0);
                                    int battery_voltage_in_millivolts = intent.getIntExtra(BluetoothLeNoninWristOx.BATTERY_VOLTAGE, 0);
                                    long battery_percentage_timestamp = intent.getLongExtra(BluetoothLeNoninWristOx.BATTERY_PERCENTAGE_TIMESTAMP, NO_TIME_STAMP);

                                    long timestamp = getNtpTimeNowInMilliseconds();                // Use NOW as we don't know when the measurement was taken

                                    // if timestamp is not send from lifetouch devices than give current time in ms.
                                    // Making it backward compatible, if timestamp isn't send from a lifetouch then it is old lifetouch
                                    if (battery_percentage_timestamp != NO_TIME_STAMP)
                                    {
                                        timestamp = battery_percentage_timestamp + device_info.start_date_at_midnight_in_milliseconds;
                                    }

                                    if (!disableCommentsForSpeed())
                                    {
                                        Log.i(TAG, "Lifetouch Battery Voltage = " + String.valueOf(battery_voltage_in_millivolts) + ". Lifetouch Battery Percentage = " + String.valueOf(battery_percentage) + " . Timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp));
                                    }

                                    // Save the Battery reading so can be read from the UI on demand
                                    device_info.setLastBatteryReading(battery_percentage, battery_voltage_in_millivolts, timestamp);

                                    patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // .....and a Device Session
                                        if (device_info.isDeviceHumanReadableDeviceIdValid())
                                        {
                                            //local_database_storage.storeLifetouchBatteryMeasurement(device_info.getAndroidDeviceSessionId(), device_info.human_readable_device_id, battery_percentage, battery_voltage_in_millivolts, timestamp);
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed Lifetouch Battery Level but IGNORED as no Device Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp));
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "Rx'ed Lifetouch Battery Level but IGNORED as no Patient Session in progress : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp));
                                    }
                                }
                                break;

                                case BluetoothLeNoninWristOx.DATATYPE_LIFEOX_PLETH_DATA:
                                {
                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                        if (device_info.isDeviceHumanReadableDeviceIdValid())
                                        {
                                            try
                                            {
                                                int sample_length = 0;
                                                short[] setup_mode_samples = intent.getShortArrayExtra(BluetoothLeLifeox.LIFEOX__SETUP_MODE_SAMPLE);
                                                long setup_mode_timestamps = intent.getLongExtra(BluetoothLeLifeox.LIFEOX__SETUP_MODE_TIMESTAMP, -1);

                                                setup_mode_timestamps += device_info.start_date_at_midnight_in_milliseconds;

                                                if (setup_mode_samples != null)
                                                {
                                                    sample_length = setup_mode_samples.length;
                                                }
                                                else
                                                {
                                                    Log.e(TAG, "BluetoothLeGattAttributesLifeox.STRING_LIFEOX_PLETH_DATA setup_mode_sample = Null");
                                                }

                                                for (int i = 0; i < sample_length; i++)
                                                {
                                                    float time_difference_from_start_of_packet = (TIME_DIFF_FOR_EACH_PLETH_DATA * (sample_length - i));

                                                    // Subtract the TIME_DIFF_FOR_EACH_PLETH_DATA based on the sample captured
                                                    long timestamp_in_ms = (setup_mode_timestamps - (long) (time_difference_from_start_of_packet + 0.5));

                                                    MeasurementSetupModeDataPoint datapoint = new MeasurementSetupModeDataPoint(setup_mode_samples[i], timestamp_in_ms);

                                                    // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                                                    // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                                                    device_info.queue_setup_mode_datapoints.add(datapoint);
                                                    device_info.queue_setup_mode_datapoints_for_server.add(datapoint);

                                                    patient_gateway_outgoing_commands.sendNewSetupModeSampleToUserInterface(device_info.device_type, datapoint);
                                                }

                                                server_syncing.sendDeviceSetupModeData(device_info);
                                            }
                                            catch (Exception e)
                                            {
                                                Log.e(TAG, "BluetoothLeGattAttributesLifeox.STRING_LIFEOX_PLETH_DATA : Exception e = " + e.toString());
                                            }
                                        }
                                    }
                                }
                                break;
*/

                                case BluetoothLeNoninWristOx.DATATYPE_FIRMWARE_REVISION:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    int firmware_int = intent.getIntExtra(BluetoothLeNoninWristOx.FIRMWARE_VERSION_NUMBER, -1);
                                    String firmware_version_string = data.trim();

                                    Log.i(TAG, "Received NoninWristOx Firmware Version : " + firmware_int);

                                    device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLeNoninWristOx.DATATYPE_GET_TIMESTAMP:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    long received_timestamp_as_long = intent.getLongExtra(BluetoothLeNoninWristOx.RECEIVED_TIMESTAMP_AS_LONG, 0);

                                    long time_now = getNtpTimeNowInMilliseconds();

                                    long time_difference = Math.abs(time_now - received_timestamp_as_long);

                                    Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " received timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(received_timestamp_as_long) + " " + received_timestamp_as_long + " time_now:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(time_now) + " " + time_now + " difference:" + time_difference);

                                    if (time_difference > 10*DateUtils.SECOND_IN_MILLIS)    // 10 seconds
                                    {
                                        // This means the Nonin time is at least 10 seconds fast or slow, so update the time
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp wrong by at least 10 seconds. Calling setTimestamp");

                                        device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());
                                    }
                                    else
                                    {
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp already set");

                                        device_info.handleGeneratedTimestamp(received_timestamp_as_long);

                                        device_info.connectToCharacteristics();
                                    }

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                default:
                                {
                                    // data_type == Unknown
                                    Log.w(TAG, "Unhandled in mGattUpdateReceiver_NoninWristOx3150Ble : Data Type = " + data_type + "   Data = " + data);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        Log.e(TAG, "exception occurred:");
                        Log.e(TAG, e.toString());
                    }
                }
                break;
            }
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver_Nonin3230 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            final BtleSensorDevice device_info = (BtleSensorDevice) device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__NONIN_3230);

            spO2_spot_measurements_enabled = gateway_settings.getEnableSpO2SpotMeasurements();

            switch (action)
            {
                case BluetoothLeNonin3230.ACTION_PAIRING:
                {
                    Log.d(TAG, "BluetoothLeNonin3230.ACTION_PAIRING");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING);
                }
                break;

                case BluetoothLeNonin3230.ACTION_PAIRING_SUCCESS:
                {
                    Log.d(TAG, "BluetoothLeNonin3230.ACTION_PAIRING_SUCCESS");

                    nonin_pairing_failure_count = 0;

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);
                }
                break;

                case BluetoothLeNonin3230.ACTION_PAIRING_FAILURE:
                {
                    Log.d(TAG, "BluetoothLeNonin3230.ACTION_PAIRING_FAILURE");

                    if((current_scan_type == DeviceType.DEVICE_TYPE__NONIN_3230) &&(nonin_pairing_failure_count < 1))
                    {
                        nonin_pairing_failure_count++;

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.SEARCHING);

                        // Tell the UI about the actual_device_connection_status change
                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_SCAN_STARTED);

                        connectNextDeviceToGatewayIfNeeded();
                    }
                    else
                    {
                        nonin_pairing_failure_count = 0;

                        btle_scanning_timeout_handler.removeCallbacksAndMessages(null);

                        stopOnGoingBluetoothScan();

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                        // Tell the UI about the actual_device_connection_status change
                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING_FAILED);

                        checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                    }
                }
                break;

                case BluetoothLeNonin3230.ACTION_CONNECTED:
                {
                    Log.d(TAG, "BluetoothLeNonin3230.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    handleConnection(device_info);

                    spo2_intermediate_tidy_up_timer.cancel();

                    nonin_pairing_failure_count = 0;

                    send_pulse_ox_battery_level = false;

                    putDeviceIntoDesiredOperatingMode(device_info);
                }
                break;

                case BluetoothLeNonin3230.ACTION_DISCONNECTED:
                {
                    Log.e(TAG, "BluetoothLeNonin3230.ACTION_DISCONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    nonin_pairing_failure_count = 0;

                    send_pulse_ox_battery_level = false;

                    restartSpO2IntermediateTidyUpTimerIfRequired();

                    removeDeviceDisconnection(device_info);
                }
                break;

                case BluetoothLeNonin3230.ACTION_NONIN_3230_SPOT_MEASUREMENT_AVAILABLE:
                {
                    String data = intent.getStringExtra(BluetoothLeNonin3230.NONIN_3230_SPOT_DATA_AS_STRING);
                    String data_type = intent.getStringExtra(BluetoothLeNonin3230.DATA_TYPE);

                    if ((data == null) || (data_type == null))
                    {
                        if (data == null)
                        {
                            Log.d(TAG, "data = null");
                        }

                        if (data_type == null)
                        {
                            Log.d(TAG, "data_type = null");
                        }
                    }
                    else
                    {
                        if (data_type.equals(BluetoothLeNonin3230.DATATYPE_NONIN_3230_SPOT_MEASUREMENT))
                        {
                            String string_spo2 = data.split("__")[0];
                            String string_timestamp = data.split("__")[1];

                            double spo2_in = Double.parseDouble(string_spo2);

                            BigDecimal spo2_value = new BigDecimal(spo2_in);

                            if (gateway_settings.getManufacturingModeEnabledStatus())
                            {
                                spo2_value = spo2_value.setScale(2, RoundingMode.HALF_UP);
                            }
                            else
                            {
                                spo2_value = spo2_value.setScale(1, RoundingMode.HALF_UP);
                            }

                            long timestamp_in_ms;
                            timestamp_in_ms = Long.parseLong(string_timestamp) ;

                            int measurement_validity_time_in_seconds = gateway_settings.getSpO2LongTermMeasurementTimeoutInMinutes() * 60;

                            MeasurementSpO2 measurement = new MeasurementSpO2(spo2_value.intValue(), INVALID_MEASUREMENT, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                            Date measurement_time = new Date();
                            measurement_time.setTime(timestamp_in_ms);

                            // Only process the received data is there is currently a Patient Session in progress.....
                            if (isPatientSessionRunning())
                            {
                                // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                if (device_info.isDeviceSessionInProgress())
                                {
                                    // ....and data captured date is after session start date
                                    if (measurement_time.after(new Date(device_info.device_session_start_time)))
                                    {
                                        storePulseOxMeasurement(measurement, device_info);
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received Nonin 3230 Spot measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as captured time is before session start time");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "Received Nonin 3230 Spot measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                }
                            }
                            else
                            {
                                Log.d(TAG, "Received Nonin 3230 Spot measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                            }
                        }
                    }
                }
                break;

                case BluetoothLeNonin3230.ACTION_DATA_AVAILABLE:
                {
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                    try
                    {
                        String data = intent.getStringExtra(BluetoothLeNonin3230.DATA_AS_STRING);
                        String data_type = intent.getStringExtra(BluetoothLeNonin3230.DATA_TYPE);
                        String device_address = intent.getStringExtra(BluetoothLeNonin3230.DEVICE_ADDRESS);

                        if ((data == null) || (device_address == null) || (data_type == null))
                        {
                            if (data == null)
                            {
                                Log.d(TAG, "data = null");
                            }

                            if (data_type == null)
                            {
                                Log.d(TAG, "data_type = null");
                            }

                            if (device_address == null)
                            {
                                Log.d(TAG, "device_address = null");
                            }
                        }
                        else
                        {
                            switch (data_type)
                            {
                                case BluetoothLeNonin3230.DATATYPE_SPO2_MEASUREMENT:
                                {
                                    boolean valid_reading = intent.getBooleanExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__VALID_READING, false);

                                    int sp_o2 = intent.getIntExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__SPO2, -1);
                                    int pulse = intent.getIntExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__PULSE_RATE, -1);
                                    int counter = intent.getIntExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__COUNTER, -1);
//TODO handle missing packets
                                    long timestamp_in_ms = intent.getLongExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__TIMESTAMP, -1);
                                    String timestamp_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms);

                                    int pulse_amplitude_index = intent.getIntExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__PULSE_AMPLITUDE_INDEX, -1);

                                    // Info decoded from Status byte
                                    boolean encryption = intent.getBooleanExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__ENCRYPTION, false);
                                    device_info.device_disconnected_from_body =  intent.getBooleanExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT, false);
                                    boolean searching = intent.getBooleanExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__SEARCHING, false);
                                    boolean smartpoint_algorithm = intent.getBooleanExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__SMART_POINT_ALGORITHM, false);
                                    boolean low_weak_signal = intent.getBooleanExtra(BluetoothLeNonin3230.NONIN_CONTINUOUS_OXIMETRY__STATUS__LOW_WEAK_SIGNAL, false);

                                    Log.i(TAG, "BluetoothLeNonin3230.DATATYPE_SPO2_MEASUREMENT : SpO2 = " + sp_o2
                                            + ". pulse = " + pulse
                                            + ". counter = " + counter
                                            + ". timestamp_in_ms = " + timestamp_as_string
                                            + ". pulse_amplitude_index = " + pulse_amplitude_index
                                            + ". encryption = " + encryption
                                            + ". pulse_ox_detached_from_patient = " + device_info.device_disconnected_from_body
                                            + ". searching = " + searching
                                            + ". smartpoint_algorithm = " + smartpoint_algorithm
                                            + ". low_weak_signal = " + low_weak_signal
                                    );

                                    // Wait until there is a Patient Session AND Device Session in progress before processing the data.
                                    // If the user has scanned the Pulse Ox QR code, and put it on their finger, it will take measurements. However they might not have pressed "Starting Monitoring" which
                                    // would start a new Patient/Device Session off. This means that all the Device Session Info etc is invalid
                                    if (isPatientSessionRunning())
                                    {
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            // Tell the UI if the Pulse Ox is on the finger or not
                                            patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

                                            // Nonin Data is send after three packets of setup mode data
                                            if(device_info.isInSetupMode() == false)
                                            {
                                                server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);
                                            }

                                            // Assume invalid measurement to start with.
                                            IntermediateSpO2 pulse_ox_intermediate_measurement = new IntermediateSpO2(INVALID_MEASUREMENT, INVALID_MEASUREMENT, timestamp_in_ms);

                                            // If pulse ox is on patient....
                                            if(device_info.device_disconnected_from_body == false)
                                            {
                                                // ....process newly received data
                                                if (valid_reading)
                                                {
                                                    if (!disableCommentsForSpeed())
                                                    {
                                                        Log.d(TAG, "pulse_ox_intermediate_measurement at " + timestamp_as_string + " = " + sp_o2 + ". android_database_device_session_id = " + device_info.getAndroidDeviceSessionId());
                                                    }

                                                    // Update the measurement with the valid readings
                                                    pulse_ox_intermediate_measurement.setSpO2(sp_o2);
                                                    pulse_ox_intermediate_measurement.setPulse(pulse);

                                                    // Reset the leads-off counter.
                                                    device_info.counter_leads_off_after_last_valid_data = 0;
                                                    device_info.timestamp_leads_off_disconnection = 0;
                                                }
                                                else
                                                {
                                                    // Invalid intermediate measurement
                                                    Log.e(TAG, "pulse_ox_intermediate_measurement Invalid Reading @ " + timestamp_as_string);
                                                }
                                            }
                                            else
                                            {
                                                // Leads-off detected. Increment leads-off counters
                                                device_info.counter_leads_off_after_last_valid_data++;
                                                device_info.timestamp_leads_off_disconnection = getNtpTimeNowInMilliseconds();
                                                device_info.counter_total_leads_off++;

                                                // Pulse Ox Finger Off detected
                                                Log.e(TAG, "pulse_ox_intermediate_measurement Finger-off @ " + timestamp_as_string);
                                            }

                                            boolean has_one_minute_elapsed;

                                            has_one_minute_elapsed = intermediate_spo2_processor.processMeasurement(pulse_ox_intermediate_measurement); //, gateway_settings.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid());

                                            has_one_minute_elapsed |= intermediate_spo2_processor.processAnyOutstandingData();

                                            // Sync intermediate measurements every minute in timer tick
                                        }
                                        else
                                        {
                                            Log.e(TAG, "pulse_ox_intermediate_measurement ERROR : Received value but no Device Session yet");
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received pulse_ox_intermediate_measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms) + " but ignored as no Patient Session in progress");
                                    }
                                }
                                break;

                                case BluetoothLeNonin3230.DATATYPE_DEVICE_STATUS:
                                {
                                    int sensor_type = intent.getIntExtra(BluetoothLeNonin3230.NONIN_DEVICE_STATUS__SENSOR_TYPE, -1);
                                    int error = intent.getIntExtra(BluetoothLeNonin3230.NONIN_DEVICE_STATUS__ERROR, -1);
                                    int battery_percentage = intent.getIntExtra(BluetoothLeNonin3230.NONIN_DEVICE_STATUS__BATTERY_PERCENTAGE, -1);
                                    int tx_index = intent.getIntExtra(BluetoothLeNonin3230.NONIN_DEVICE_STATUS__TX_INDEX, -1);
                                    long timestamp_in_ms = intent.getLongExtra(BluetoothLeNonin3230.NONIN_DEVICE_STATUS__TIMESTAMP, -1);

                                    if (isPatientSessionRunning())
                                    {
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            device_info.setLastBatteryReading(battery_percentage, -1, timestamp_in_ms);

                                            if (send_pulse_ox_battery_level == false)
                                            {
                                                // Save the Battery reading so can be read from the UI on demand
                                                device_info.setLastBatteryReading(battery_percentage, -1, timestamp_in_ms);

                                                // Show the battery on the UI screen as soon as possible after connecting.
                                                patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                                send_pulse_ox_battery_level = true;
                                            }

                                            // Write Nonin Battery value
                                            patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                            Log.e(TAG, "Store Nonin 3230 Battery measurement : battery_percentage = " + battery_percentage + "%");

                                            int battery_millivolts = -1;

                                            MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                                    battery_millivolts,
                                                    battery_percentage,
                                                    timestamp_in_ms,
                                                    getNtpTimeNowInMilliseconds());

                                            local_database_storage.storeOximeterBatteryMeasurement(device_info, measurement);

                                            Log.i(TAG, "BluetoothLeNonin3230.DATATYPE_DEVICE_STATUS : sensor_type = " + sensor_type
                                                    + ". error = " + error
                                                    + ". battery_percentage = " + battery_percentage
                                                    + ". tx_index = " + tx_index
                                            );
                                        }
                                    }
                                }
                                break;

                                case BluetoothLeNonin3230.DATATYPE_FIRMWARE_REVISION:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    int firmware_int = intent.getIntExtra(BluetoothLeNonin3230.FIRMWARE_VERSION_NUMBER, -1);
                                    String firmware_version_string = data.trim();

                                    Log.i(TAG, "Received Nonin 3230 Firmware Version : " + firmware_int);

                                    device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLeNonin3230.DATATYPE_GET_TIMESTAMP:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    long received_timestamp_as_long = intent.getLongExtra(BluetoothLeNonin3230.RECEIVED_TIMESTAMP_AS_LONG, 0);

                                    long time_now = getNtpTimeNowInMilliseconds();

                                    long time_difference = Math.abs(time_now - received_timestamp_as_long);

                                    Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " received timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(received_timestamp_as_long) + " " + received_timestamp_as_long + " time_now:" + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(time_now) + " " + time_now + " difference:" + time_difference);

                                    if (time_difference > 10*DateUtils.SECOND_IN_MILLIS)    // 10 seconds
                                    {
                                        // This means the Nonin time is at least 10 seconds fast or slow, so update the time
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp wrong by at least 10 seconds. Calling setTimestamp");

                                        device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());
                                    }
                                    else
                                    {
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp already set");

                                        device_info.handleGeneratedTimestamp(received_timestamp_as_long);

                                        device_info.connectToCharacteristics();
                                    }

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                default:
                                {
                                    // data_type == Unknown
                                    Log.w(TAG, "Unhandled in mGattUpdateReceiver_Nonin3230 : Data Type = " + data_type + "   Data = " + data);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        Log.e(TAG, "exception occurred:");
                        Log.e(TAG, e.toString());
                    }
                }
                break;
            }
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver_MedLinket = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            final BtleSensorDevice device_info = (BtleSensorDevice) device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__MEDLINKET);

            spO2_spot_measurements_enabled = gateway_settings.getEnableSpO2SpotMeasurements();

            switch (action)
            {
                case BluetoothLeMedLinket.ACTION_PAIRING:
                {
                    Log.d(TAG, "BluetoothLeMedLinket.ACTION_PAIRING");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING);
                }
                break;

                case BluetoothLeMedLinket.ACTION_PAIRING_SUCCESS:
                {
                    Log.d(TAG, "BluetoothLeMedLinket.ACTION_PAIRING_SUCCESS");

                    nonin_pairing_failure_count = 0;

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);
                }
                break;

                case BluetoothLeMedLinket.ACTION_PAIRING_FAILURE:
                {
                    Log.d(TAG, "BluetoothLeMedLinket.ACTION_PAIRING_FAILURE");

                    if((current_scan_type == DeviceType.DEVICE_TYPE__MEDLINKET) &&(nonin_pairing_failure_count < 1))
                    {
                        nonin_pairing_failure_count++;

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.SEARCHING);

                        // Tell the UI about the actual_device_connection_status change
                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_SCAN_STARTED);

                        connectNextDeviceToGatewayIfNeeded();
                    }
                    else
                    {
                        nonin_pairing_failure_count = 0;

                        btle_scanning_timeout_handler.removeCallbacksAndMessages(null);

                        stopOnGoingBluetoothScan();

                        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                        // Tell the UI about the actual_device_connection_status change
                        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                        patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING_FAILED);

                        checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                    }
                }
                break;

                case BluetoothLeMedLinket.ACTION_CONNECTED:
                {
                    Log.d(TAG, "BluetoothLeMedLinket.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    handleConnection(device_info);

                    spo2_intermediate_tidy_up_timer.cancel();

                    nonin_pairing_failure_count = 0;

                    putDeviceIntoDesiredOperatingMode(device_info);
                }
                break;

                case BluetoothLeMedLinket.ACTION_DISCONNECTED:
                {
                    Log.e(TAG, "BluetoothLeMedLinket.ACTION_DISCONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    nonin_pairing_failure_count = 0;

                    restartSpO2IntermediateTidyUpTimerIfRequired();

                    removeDeviceDisconnection(device_info);
                }
                break;

                case BluetoothLeMedLinket.ACTION_DATA_AVAILABLE:
                {
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                    try
                    {
                        String data = intent.getStringExtra(BluetoothLeMedLinket.DATA_AS_STRING);
                        String data_type = intent.getStringExtra(BluetoothLeMedLinket.DATA_TYPE);
                        String device_address = intent.getStringExtra(BluetoothLeMedLinket.DEVICE_ADDRESS);

                        if ((data == null) || (device_address == null) || (data_type == null))
                        {
                            if (data == null)
                            {
                                Log.d(TAG, "data = null");
                            }

                            if (data_type == null)
                            {
                                Log.d(TAG, "data_type = null");
                            }

                            if (device_address == null)
                            {
                                Log.d(TAG, "device_address = null");
                            }
                        }
                        else
                        {
                            switch (data_type)
                            {
                                case BluetoothLeMedLinket.DATATYPE_SPO2_MEASUREMENT:
                                {
                                    //boolean valid_reading = intent.getBooleanExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__VALID_READING, false);

                                    int sp_o2 = intent.getIntExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__SPO2, -1);
                                    int pulse = intent.getIntExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__PULSE_RATE, -1);

                                    long timestamp_in_ms = intent.getLongExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__TIMESTAMP, -1);
                                    String timestamp_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms);

                                    //int battery_voltage = intent.getIntExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_VOLTAGE, -1);
                                    //int battery_percentage = intent.getIntExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE, -1);

                                    // Info decoded from Status byte
                                    device_info.device_disconnected_from_body =  intent.getBooleanExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__STATUS__DETACHED_FROM_PATIENT, false);
                                    boolean searching = intent.getBooleanExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__STATUS__SEARCHING, false);

                                    Log.i(TAG, "BluetoothLeMedLinket.DATATYPE_SPO2_MEASUREMENT : SpO2 = " + sp_o2
                                            + ". pulse = " + pulse
                                            + ". timestamp_in_ms = " + timestamp_as_string
                                            + ". pulse_ox_detached_from_patient = " + device_info.device_disconnected_from_body
                                            + ". searching = " + searching
                                    );

                                    // Wait until there is a Patient Session AND Device Session in progress before processing the data.
                                    // If the user has scanned the Pulse Ox QR code, and put it on their finger, it will take measurements. However they might not have pressed "Starting Monitoring" which
                                    // would start a new Patient/Device Session off. This means that all the Device Session Info etc is invalid
                                    if (isPatientSessionRunning())
                                    {
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            // Tell the UI if the Pulse Ox is on the finger or not
                                            patient_gateway_outgoing_commands.reportDeviceLeadOffDetectionStatus(device_info.sensor_type, device_info.device_disconnected_from_body);

                                            // Nonin Data is send after three packets of setup mode data
                                            if(device_info.isInSetupMode() == false)
                                            {
                                                server_syncing.sendDeviceLeadsOffStatusIfServerConnected(patient_session_info.server_patient_session_id, device_info, device_info.device_disconnected_from_body);
                                            }

                                            // Assume invalid measurement to start with.
                                            IntermediateSpO2 pulse_ox_intermediate_measurement = new IntermediateSpO2(INVALID_MEASUREMENT, INVALID_MEASUREMENT, timestamp_in_ms);

                                            // If pulse ox is on patient....
                                            if(device_info.device_disconnected_from_body == false)
                                            {
                                                // ....process newly received data
                                                if (!disableCommentsForSpeed())
                                                {
                                                    Log.d(TAG, "pulse_ox_intermediate_measurement at " + timestamp_as_string + " = " + sp_o2 + ". android_database_device_session_id = " + device_info.getAndroidDeviceSessionId());
                                                }

                                                // Update the measurement with the valid readings
                                                pulse_ox_intermediate_measurement.setSpO2(sp_o2);
                                                pulse_ox_intermediate_measurement.setPulse(pulse);

                                                if(send_pulse_ox_battery_level == false)
                                                {
                                                    // Save the Battery reading so can be read from the UI on demand
                                        //            device_info.setLastBatteryReading(battery_percentage, battery_voltage, timestamp_in_ms);

                                                    // Show the battery on the UI screen as soon as possible after connecting.
                                                    patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                                    send_pulse_ox_battery_level = true;
                                                }

                                                // Reset the leads-off counter.
                                                device_info.counter_leads_off_after_last_valid_data = 0;
                                                device_info.timestamp_leads_off_disconnection = 0;
                                            }
                                            else
                                            {
                                                // Leads-off detected. Increment leads-off counters
                                                device_info.counter_leads_off_after_last_valid_data++;
                                                device_info.timestamp_leads_off_disconnection = getNtpTimeNowInMilliseconds();
                                                device_info.counter_total_leads_off++;

                                                // Pulse Ox Finger Off detected
                                                Log.e(TAG, "pulse_ox_intermediate_measurement Finger-off @ " + timestamp_as_string);
                                            }

                                            boolean has_one_minute_elapsed;

                                            has_one_minute_elapsed = intermediate_spo2_processor.processMeasurement(pulse_ox_intermediate_measurement); //, gateway_settings.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid());

                                            has_one_minute_elapsed |= intermediate_spo2_processor.processAnyOutstandingData();

                                            // Save the Battery reading so can be read from the UI on demand
                                            //device_info.setLastBatteryReading(battery_percentage, battery_voltage, timestamp_in_ms);

                                            // Write Nonin Battery value
                                            patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                            if (has_one_minute_elapsed)
                                            {
                                                /*
                                                Log.e(TAG, "Store Nonin BLE Battery measurement : battery_percentage = " + battery_percentage + "% : battery_voltage = " + battery_voltage);

                                                int battery_millivolts = battery_voltage * 100;

                                                MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                                        battery_millivolts,
                                                        battery_percentage,
                                                        timestamp_in_ms,
                                                        getNtpTimeNowInMilliseconds());

                                                local_database_storage.storeOximeterBatteryMeasurement(device_info, measurement);
                                            */
                                            }

                                            // Sync intermediate measurements every minute in timer tick
                                        }
                                        else
                                        {
                                            Log.e(TAG, "pulse_ox_intermediate_measurement ERROR : Received value but no Device Session yet");
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received pulse_ox_intermediate_measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(timestamp_in_ms) + " but ignored as no Patient Session in progress");
                                    }
                                }
                                break;

                                case BluetoothLeMedLinket.DATATYPE_PPG_DATA:
                                {
                                    // Wait until there is a Patient Session AND Device Session in progress before processing the data.
                                    // If the user has scanned the Pulse Ox QR code, and put it on their finger, it will take measurements. However they might not have pressed "Starting Monitoring" which
                                    // would start a new Patient/Device Session off. This means that all the Device Session Info etc is invalid
                                    if ((isPatientSessionRunning()) && (device_info.isDeviceSessionInProgress()))
                                    {
                                        Bundle bundle = intent.getExtras();
                                        ArrayList<MeasurementSetupModeDataPoint> setup_mode_datapoints = bundle.getParcelableArrayList(BluetoothLeMedLinket.PPG_DATA_POINTS);

                                    //    Log.d(TAG, "BluetoothLeMedLinket.DATATYPE_PPG_DATA. Number of samples = " + setup_mode_datapoints.size());

                                        for(MeasurementSetupModeDataPoint datapoint : setup_mode_datapoints)
                                        {
                                            processTimestampForSetupModeLogEntry(device_info, datapoint.timestamp_in_ms);

                                            // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                                            // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                                            device_info.queue_setup_mode_datapoints.add(datapoint);

                                            if (device_info.isInServerInitedRawDataMode())
                                            {
                                                device_info.queue_setup_mode_datapoints_for_server.add(datapoint);
                                            }

                                            patient_gateway_outgoing_commands.sendNewSetupModeSampleToUserInterface(device_info.device_type, datapoint);
                                        }

                                        if (device_info.isInServerInitedRawDataMode())
                                        {
                                            // Scale the original Medlinket Setup data up to 16 bit.
                                            server_syncing.sendDeviceSetupModeData(patient_session_info.server_patient_session_id, device_info, 512);
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "BluetoothLeMedLinket.DATATYPE_PPG_DATA Received data but ignored as no Patient or Device Session in progress");
                                    }
                                }
                                break;

                                case BluetoothLeMedLinket.DATATYPE_BATTERY_LEVEL:
                                {
                                    final int NO_TIME_STAMP = -1;

                                    int battery_percentage = intent.getIntExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_PERCENTAGE, 0);
                                    long timestamp = intent.getLongExtra(BluetoothLeMedLinket.MEDLINKET_CONTINUOUS_OXIMETRY__BATTERY_TIMESTAMP, NO_TIME_STAMP);

                                    // Save the Battery reading so can be read from the UI on demand
                                    device_info.setLastBatteryReading(battery_percentage, -1, timestamp);

                                    patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                    if (getNtpTimeNowInMilliseconds() - device_info.last_battery_reading_written_to_database_timestamp >= DateUtils.MINUTE_IN_MILLIS)
                                    {
                                        // MedLinket does not give us this
                                        int battery_millivolts = 0;

                                        MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                                battery_millivolts,
                                                battery_percentage,
                                                timestamp,
                                                getNtpTimeNowInMilliseconds());

                                        local_database_storage.storeOximeterBatteryMeasurement(device_info, measurement);

                                        device_info.last_battery_reading_written_to_database_timestamp = getNtpTimeNowInMilliseconds();
                                    }
                                }

                                break;

/*                                case BluetoothLeMedLinket.DATATYPE_LIFEOX_PLETH_DATA:
                                {
                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                        if (device_info.isDeviceHumanReadableDeviceIdValid())
                                        {
                                            try
                                            {
                                                int sample_length = 0;
                                                short[] setup_mode_samples = intent.getShortArrayExtra(BluetoothLeLifeox.LIFEOX__SETUP_MODE_SAMPLE);
                                                long setup_mode_timestamps = intent.getLongExtra(BluetoothLeLifeox.LIFEOX__SETUP_MODE_TIMESTAMP, -1);

                                                setup_mode_timestamps += device_info.start_date_at_midnight_in_milliseconds;

                                                if (setup_mode_samples != null)
                                                {
                                                    sample_length = setup_mode_samples.length;
                                                }
                                                else
                                                {
                                                    Log.e(TAG, "BluetoothLeGattAttributesLifeox.STRING_LIFEOX_PLETH_DATA setup_mode_sample = Null");
                                                }

                                                for (int i = 0; i < sample_length; i++)
                                                {
                                                    float time_difference_from_start_of_packet = (TIME_DIFF_FOR_EACH_PLETH_DATA * (sample_length - i));

                                                    // Subtract the TIME_DIFF_FOR_EACH_PLETH_DATA based on the sample captured
                                                    long timestamp_in_ms = (setup_mode_timestamps - (long) (time_difference_from_start_of_packet + 0.5));

                                                    MeasurementSetupModeDataPoint datapoint = new MeasurementSetupModeDataPoint(setup_mode_samples[i], timestamp_in_ms);

                                                    // Setup mode samples are coming in too fast to be able to log each one to the database at a time. The datapoints are added to a queue.
                                                    // The PatientGatewayTimerTick checks every second if this queue has data and writes one seconds worth of data to the database at a time.
                                                    device_info.queue_setup_mode_datapoints.add(datapoint);
                                                    device_info.queue_setup_mode_datapoints_for_server.add(datapoint);

                                                    patient_gateway_outgoing_commands.sendNewSetupModeSampleToUserInterface(device_info.device_type, datapoint);
                                                }

                                                server_syncing.sendDeviceSetupModeData(device_info);
                                            }
                                            catch (Exception e)
                                            {
                                                Log.e(TAG, "BluetoothLeGattAttributesLifeox.STRING_LIFEOX_PLETH_DATA : Exception e = " + e.toString());
                                            }
                                        }
                                    }
                                }
                                break;
*/

                                case BluetoothLeMedLinket.DATATYPE_FIRMWARE_REVISION:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    int firmware_int = intent.getIntExtra(BluetoothLeMedLinket.FIRMWARE_VERSION_NUMBER, -1);
                                    String firmware_version_string = data.trim();

                                    Log.i(TAG, "Received NoninWristOx Firmware Version : " + firmware_int);

                                    device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLeMedLinket.DATATYPE_GET_TIMESTAMP:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    long time_now = getNtpTimeNowInMilliseconds();

                                    device_info.handleGeneratedTimestamp(time_now);

                                    device_info.connectToCharacteristics();

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                default:
                                {
                                    // data_type == Unknown
                                    Log.w(TAG, "Unhandled in mGattUpdateReceiver_NoninWristOx3150Ble : Data Type = " + data_type + "   Data = " + data);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        Log.e(TAG, "exception occurred:");
                        Log.e(TAG, e.toString());
                    }
                }
                break;
            }
        }
    };


    private void restartSpO2IntermediateTidyUpTimerIfRequired()
    {
        if (spO2_spot_measurements_enabled && isPatientSessionRunning())
        {
            Log.d(TAG, "restartSpO2IntermediateTidyUpTimerIfRequired : (Re)Starting spo2_intermediate_tidy_up_timer");

            spo2_intermediate_tidy_up_timer.cancel();
            spo2_intermediate_tidy_up_timer.start();
        }
        else
        {
            Log.d(TAG, "restartSpO2IntermediateTidyUpTimerIfRequired : NOT required :  spO2_spot_measurements_enabled : " + spO2_spot_measurements_enabled + " isPatientSessionRunning : " + isPatientSessionRunning());
        }
    }


    private final BroadcastReceiver mGattUpdateReceiver_AndTm2441 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            final BtleSensorDevice device_info = (BtleSensorDevice) device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_ABPM_TM2441);

            switch (action)
            {
                case BluetoothLe_AnD_TM2441.ACTION_PAIRING:
                {
                    Log.d(TAG, "BluetoothLe_AnD_TM2441.ACTION_PAIRING");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING);
                }
                break;

                case BluetoothLe_AnD_TM2441.ACTION_PAIRING_SUCCESS:
                {
                    Log.d(TAG, "BluetoothLe_AnD_TM2441.ACTION_PAIRING_SUCCESS");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);
                }
                break;

                case BluetoothLe_AnD_TM2441.ACTION_PAIRING_FAILURE:
                {
                    Log.d(TAG, "BluetoothLe_AnD_TM2441.ACTION_PAIRING_FAILURE");

                    btle_scanning_timeout_handler.removeCallbacksAndMessages(null);

                    stopOnGoingBluetoothScan();

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING_FAILED);

                    checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                }
                break;

                case BluetoothLe_AnD_TM2441.ACTION_CONNECTED:
                {
                    Log.d(TAG, "BluetoothLe_AnD_TM2441.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    handleConnection(device_info);
                }
                break;

                case BluetoothLe_AnD_TM2441.ACTION_DISCONNECTED:
                {
                    Log.e(TAG, "BluetoothLe_AnD_TM2441.ACTION_DISCONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    removeDeviceDisconnection(device_info);
                }
                break;

                case BluetoothLe_AnD_TM2441.ACTION_UNEXPECTED_UNBOND:
                {
                    Log.e(TAG, "BluetoothLe_AnD_TM2441.ACTION_UNEXPECTED_UNBOND");

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                }
                break;

                case BluetoothLe_AnD_TM2441.ACTION_DATA_AVAILABLE:
                {
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                    try
                    {
                        String data = intent.getStringExtra(BluetoothLe_AnD_TM2441.DATA_AS_STRING);
                        String data_type = intent.getStringExtra(BluetoothLe_AnD_TM2441.DATA_TYPE);
                        String device_address = intent.getStringExtra(BluetoothLe_AnD_TM2441.DEVICE_ADDRESS);

                        if ((data == null) || (device_address == null) || (data_type == null))
                        {
                            if (data == null)
                            {
                                Log.d(TAG, "data = null");
                            }

                            if (data_type == null)
                            {
                                Log.d(TAG, "data_type = null");
                            }

                            if (device_address == null)
                            {
                                Log.d(TAG, "device_address = null");
                            }
                        }
                        else
                        {
                            switch (data_type)
                            {
                                case BluetoothLe_AnD_TM2441.DATATYPE_BLOOD_PRESSURE_MEASUREMENT:
                                {
                                    int systolic = intent.getIntExtra(BluetoothLe_AnD_TM2441.SYSTOLIC, -1);
                                    int diastolic = intent.getIntExtra(BluetoothLe_AnD_TM2441.DIASTOLIC, -1);
                                    int pulse_rate = intent.getIntExtra(BluetoothLe_AnD_TM2441.PULSE_RATE, -1);
                                    int mean_arterial_pressure = intent.getIntExtra(BluetoothLe_AnD_TM2441.MEAN_ARTERIAL_PRESSURE, -1);

                                    long measurement_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_TM2441.TIMESTAMP, -1);
                                    String measurement_timestamp_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_timestamp_in_ms);

                                    Date measurement_time = new Date();
                                    measurement_time.setTime(measurement_timestamp_in_ms);

                                    Log.i(TAG, "BluetoothLe_AnD_TM2441.DATATYPE_BLOOD_PRESSURE_MEASUREMENT : systolic = " + systolic
                                            + ". diastolic = " + diastolic
                                            + ". pulse_rate = " + pulse_rate
                                            + ". mean_arterial_pressure = " + mean_arterial_pressure
                                            + ". measurement_timestamp_as_string = " + measurement_timestamp_as_string
                                    );

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            // ....and data captured date is after session start date
                                            if(measurement_time.after(new Date(device_info.device_session_start_time)))
                                            {
                                                int measurement_validity_time_in_seconds = gateway_settings.getBloodPressureLongTermMeasurementTimeoutInMinutes() * 60;

                                                MeasurementBloodPressure measurement = new MeasurementBloodPressure(systolic, diastolic, pulse_rate, measurement_timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                                                local_database_storage.storeBloodPressureMeasurement(device_info, measurement);

                                                reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.BLOOD_PRESSURE, measurement);
                                            }
                                            else
                                            {
                                                Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as captured time is before session start time");
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                    }
                                }
                                break;

                                case BluetoothLe_AnD_TM2441.DATATYPE_FIRMWARE_REVISION:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    int firmware_int = intent.getIntExtra(BluetoothLe_AnD_TM2441.FIRMWARE_VERSION_NUMBER, -1);
                                    String firmware_version_string = data.trim();

                                    device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_TM2441.DATATYPE_GET_TIMESTAMP:
                                {
                                    long received_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_TM2441.RECEIVED_TIMESTAMP_AS_LONG, 0);
                                    long gateway_timestamp_when_timestamp_was_received = intent.getLongExtra(BluetoothLe_AnD_TM2441.GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED, 0);

                                    long delta = Math.abs(received_timestamp_in_ms - gateway_timestamp_when_timestamp_was_received);

                                    Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " received timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(received_timestamp_in_ms) + " received at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(gateway_timestamp_when_timestamp_was_received));

                                    // If the time matches +/- 30 seconds then don't set it again.
                                    if (delta < 30*DateUtils.SECOND_IN_MILLIS)
                                    {
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp already set/valid");

                                        device_info.handleGeneratedTimestamp(received_timestamp_in_ms);
                                    }
                                    else if(device_info.isStartSessionTimeInvalid())
                                    {
                                        // Timestamp not valid. Set it
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp not set. Calling setTimestamp");

                                        device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());
                                    }
                                    else
                                    {
                                        Log.w(TAG, "Timestamp received with delta " + delta + " ms, but not resetting as session in progress.");
                                    }

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_TM2441.DATATYPE_BATTERY_LEVEL:
                                {
                                    int battery_percentage = intent.getIntExtra(BluetoothLe_AnD_TM2441.BATTERY_PERCENTAGE, 0);
                                    long timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_TM2441.BATTERY_TIMESTAMP, 0);

                                    MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                            0,
                                            battery_percentage,
                                            timestamp_in_ms,
                                            getNtpTimeNowInMilliseconds());

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // .....and a Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            local_database_storage.storeBloodPressureBatteryMeasurement(device_info, measurement);

                                            // Save the Battery reading so can be read from the UI on demand
                                            device_info.setLastBatteryReading(battery_percentage, -1, timestamp_in_ms);

                                            patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed BluetoothLe_AnD_TM2441 Battery Level but IGNORED as no Device Session in progress.");
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "Rx'ed BluetoothLe_AnD_TM2441 Battery Level but IGNORED as no Patient Session in progress.");
                                    }
                                }
                                break;

                                default:
                                {
                                    // data_type == Unknown
                                    Log.w(TAG, "Unhandled in mGattUpdateReceiver_NoninWristOx3150Ble : Data Type = " + data_type + "   Data = " + data);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        Log.e(TAG, "exception occurred:");
                        Log.e(TAG, e.toString());
                    }
                }
                break;
            }
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver_AndUa651 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            final BtleSensorDevice device_info = (BtleSensorDevice) device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_UA651);

            switch (action)
            {
                case BluetoothLe_AnD_UA651.ACTION_PAIRING:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA651.ACTION_PAIRING");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING);
                }
                break;

                case BluetoothLe_AnD_UA651.ACTION_PAIRING_SUCCESS:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA651.ACTION_PAIRING_SUCCESS");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);
                }
                break;

                case BluetoothLe_AnD_UA651.ACTION_PAIRING_FAILURE:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA651.ACTION_PAIRING_FAILURE");

                    btle_scanning_timeout_handler.removeCallbacksAndMessages(null);

                    stopOnGoingBluetoothScan();

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING_FAILED);

                    checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                }
                break;

                case BluetoothLe_AnD_UA651.ACTION_CONNECTED:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA651.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    handleConnection(device_info);
                }
                break;

                case BluetoothLe_AnD_UA651.ACTION_DISCONNECTED:
                {
                    Log.e(TAG, "BluetoothLe_AnD_UA651.ACTION_DISCONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    removeDeviceDisconnection(device_info);
                }
                break;

                case BluetoothLe_AnD_UA651.ACTION_UNEXPECTED_UNBOND:
                {
                    Log.e(TAG, "BluetoothLe_AnD_UA651.ACTION_UNEXPECTED_UNBOND");

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                }
                break;

                case BluetoothLe_AnD_UA651.ACTION_DATA_AVAILABLE:
                {
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                    try
                    {
                        String data = intent.getStringExtra(BluetoothLe_AnD_UA651.DATA_AS_STRING);
                        String data_type = intent.getStringExtra(BluetoothLe_AnD_UA651.DATA_TYPE);
                        String device_address = intent.getStringExtra(BluetoothLe_AnD_UA651.DEVICE_ADDRESS);

                        if ((data == null) || (device_address == null) || (data_type == null))
                        {
                            if (data == null)
                            {
                                Log.d(TAG, "data = null");
                            }

                            if (data_type == null)
                            {
                                Log.d(TAG, "data_type = null");
                            }

                            if (device_address == null)
                            {
                                Log.d(TAG, "device_address = null");
                            }
                        }
                        else
                        {
                            switch (data_type)
                            {
                                case BluetoothLe_AnD_UA651.DATATYPE_BLOOD_PRESSURE_MEASUREMENT:
                                {
                                    int systolic = intent.getIntExtra(BluetoothLe_AnD_UA651.SYSTOLIC, -1);
                                    int diastolic = intent.getIntExtra(BluetoothLe_AnD_UA651.DIASTOLIC, -1);
                                    int pulse_rate = intent.getIntExtra(BluetoothLe_AnD_UA651.PULSE_RATE, -1);
                                    int mean_arterial_pressure = intent.getIntExtra(BluetoothLe_AnD_UA651.MEAN_ARTERIAL_PRESSURE, -1);

                                    long measurement_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UA651.TIMESTAMP, -1);
                                    String measurement_timestamp_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_timestamp_in_ms);

                                    Date measurement_time = new Date();
                                    measurement_time.setTime(measurement_timestamp_in_ms);

                                    Log.i(TAG, "BluetoothLe_AnD_UA651.DATATYPE_BLOOD_PRESSURE_MEASUREMENT : systolic = " + systolic
                                            + ". diastolic = " + diastolic
                                            + ". pulse_rate = " + pulse_rate
                                            + ". mean_arterial_pressure = " + mean_arterial_pressure
                                            + ". measurement_timestamp_as_string = " + measurement_timestamp_as_string
                                    );

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            // ....and data captured date is after session start date
                                            if(measurement_time.after(new Date(device_info.device_session_start_time)))
                                            {
                                                int measurement_validity_time_in_seconds = gateway_settings.getBloodPressureLongTermMeasurementTimeoutInMinutes() * 60;

                                                MeasurementBloodPressure measurement = new MeasurementBloodPressure(systolic, diastolic, pulse_rate, measurement_timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                                                local_database_storage.storeBloodPressureMeasurement(device_info, measurement);

                                                reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.BLOOD_PRESSURE, measurement);
                                            }
                                            else
                                            {
                                                Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as captured time is before session start time");
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                    }
                                }
                                break;

                                case BluetoothLe_AnD_UA651.DATATYPE_FIRMWARE_REVISION:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    int firmware_int = intent.getIntExtra(BluetoothLe_AnD_UA651.FIRMWARE_VERSION_NUMBER, -1);
                                    String firmware_version_string = data.trim();

                                    device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_UA651.DATATYPE_GET_TIMESTAMP:
                                {
                                    long received_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UA651.RECEIVED_TIMESTAMP_AS_LONG, 0);
                                    long gateway_timestamp_when_timestamp_was_received = intent.getLongExtra(BluetoothLe_AnD_UA651.GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED, 0);

                                    long delta = Math.abs(received_timestamp_in_ms - gateway_timestamp_when_timestamp_was_received);

                                    Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " received timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(received_timestamp_in_ms) + " received at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(gateway_timestamp_when_timestamp_was_received));

                                    // If the time matches +/- 90 seconds then don't set it again.
                                    // Using 90 rather than 30 as the AnD will advertise for up to 1 minute,
                                    // so the timestamp might be > 30 seconds out of date by the time we connect
                                    if (delta < 90*DateUtils.SECOND_IN_MILLIS)
                                    {
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp already set/valid");

                                        device_info.handleGeneratedTimestamp(received_timestamp_in_ms);
                                    }
                                    else
                                    {
                                        // Timestamp not valid. Set it
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp not set. Calling setTimestamp");

                                        device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());
                                    }

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_UA651.DATATYPE_BATTERY_LEVEL:
                                {
                                    int battery_percentage = intent.getIntExtra(BluetoothLe_AnD_UA651.BATTERY_PERCENTAGE, 0);
                                    long timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UA651.BATTERY_TIMESTAMP, 0);

                                    MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                            0,
                                            battery_percentage,
                                            timestamp_in_ms,
                                            getNtpTimeNowInMilliseconds());

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // .....and a Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            local_database_storage.storeBloodPressureBatteryMeasurement(device_info, measurement);

                                            // Save the Battery reading so can be read from the UI on demand
                                            device_info.setLastBatteryReading(battery_percentage, -1, timestamp_in_ms);

                                            patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed BluetoothLe_AnD_UA651 Battery Level but IGNORED as no Device Session in progress.");
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "Rx'ed BluetoothLe_AnD_UA651 Battery Level but IGNORED as no Patient Session in progress.");
                                    }
                                }
                                break;

                                default:
                                {
                                    // data_type == Unknown
                                    Log.w(TAG, "Unhandled in mGattUpdateReceiver_NoninWristOx3150Ble : Data Type = " + data_type + "   Data = " + data);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        Log.e(TAG, "exception occurred:");
                        Log.e(TAG, e.toString());
                    }
                }
                break;
            }
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver_AndUa656 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            final BtleSensorDevice device_info = (BtleSensorDevice) device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_UA656BLE);

            switch (action)
            {
                case BluetoothLe_AnD_UA656.ACTION_PAIRING:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA656.ACTION_PAIRING");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING);
                }
                break;

                case BluetoothLe_AnD_UA656.ACTION_PAIRING_SUCCESS:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA656.ACTION_PAIRING_SUCCESS");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);
                }
                break;

                case BluetoothLe_AnD_UA656.ACTION_PAIRING_FAILURE:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA656.ACTION_PAIRING_FAILURE");

                    btle_scanning_timeout_handler.removeCallbacksAndMessages(null);

                    stopOnGoingBluetoothScan();

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING_FAILED);

                    checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                }
                break;

                case BluetoothLe_AnD_UA656.ACTION_CONNECTED:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA656.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    handleConnection(device_info);
                }
                break;

                case BluetoothLe_AnD_UA656.ACTION_DISCONNECTED:
                {
                    Log.e(TAG, "BluetoothLe_AnD_UA656.ACTION_DISCONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    removeDeviceDisconnection(device_info);
                }
                break;

                case BluetoothLe_AnD_UA656.ACTION_UNEXPECTED_UNBOND:
                {
                    Log.e(TAG, "BluetoothLe_AnD_UA656.ACTION_UNEXPECTED_UNBOND");

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                }
                break;

                case BluetoothLe_AnD_UA656.ACTION_DATA_AVAILABLE:
                {
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                    try
                    {
                        String data = intent.getStringExtra(BluetoothLe_AnD_UA656.DATA_AS_STRING);
                        String data_type = intent.getStringExtra(BluetoothLe_AnD_UA656.DATA_TYPE);
                        String device_address = intent.getStringExtra(BluetoothLe_AnD_UA656.DEVICE_ADDRESS);

                        if ((data == null) || (device_address == null) || (data_type == null))
                        {
                            if (data == null)
                            {
                                Log.d(TAG, "data = null");
                            }

                            if (data_type == null)
                            {
                                Log.d(TAG, "data_type = null");
                            }

                            if (device_address == null)
                            {
                                Log.d(TAG, "device_address = null");
                            }
                        }
                        else
                        {
                            switch (data_type)
                            {
                                case BluetoothLe_AnD_UA656.DATATYPE_BLOOD_PRESSURE_MEASUREMENT:
                                {
                                    int systolic = intent.getIntExtra(BluetoothLe_AnD_UA656.SYSTOLIC, -1);
                                    int diastolic = intent.getIntExtra(BluetoothLe_AnD_UA656.DIASTOLIC, -1);
                                    int pulse_rate = intent.getIntExtra(BluetoothLe_AnD_UA656.PULSE_RATE, -1);
                                    int mean_arterial_pressure = intent.getIntExtra(BluetoothLe_AnD_UA656.MEAN_ARTERIAL_PRESSURE, -1);

                                    long measurement_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UA656.TIMESTAMP, -1);
                                    String measurement_timestamp_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_timestamp_in_ms);

                                    Date measurement_time = new Date();
                                    measurement_time.setTime(measurement_timestamp_in_ms);

                                    Log.i(TAG, "BluetoothLe_AnD_UA656.DATATYPE_BLOOD_PRESSURE_MEASUREMENT : systolic = " + systolic
                                            + ". diastolic = " + diastolic
                                            + ". pulse_rate = " + pulse_rate
                                            + ". mean_arterial_pressure = " + mean_arterial_pressure
                                            + ". measurement_timestamp_as_string = " + measurement_timestamp_as_string
                                    );

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            // ....and data captured date is after session start date
                                            if(measurement_time.after(new Date(device_info.device_session_start_time)))
                                            {
                                                int measurement_validity_time_in_seconds = gateway_settings.getBloodPressureLongTermMeasurementTimeoutInMinutes() * 60;

                                                MeasurementBloodPressure measurement = new MeasurementBloodPressure(systolic, diastolic, pulse_rate, measurement_timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                                                local_database_storage.storeBloodPressureMeasurement(device_info, measurement);

                                                reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.BLOOD_PRESSURE, measurement);
                                            }
                                            else
                                            {
                                                Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as captured time is before session start time");
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                    }
                                }
                                break;

                                case BluetoothLe_AnD_UA656.DATATYPE_FIRMWARE_REVISION:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    int firmware_int = intent.getIntExtra(BluetoothLe_AnD_UA656.FIRMWARE_VERSION_NUMBER, -1);
                                    String firmware_version_string = data.trim();

                                    device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_UA656.DATATYPE_GET_TIMESTAMP:
                                {
                                    long received_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UA656.RECEIVED_TIMESTAMP_AS_LONG, 0);
                                    long gateway_timestamp_when_timestamp_was_received = intent.getLongExtra(BluetoothLe_AnD_UA656.GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED, 0);

                                    long delta = Math.abs(received_timestamp_in_ms - gateway_timestamp_when_timestamp_was_received);

                                    Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " received timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(received_timestamp_in_ms) + " received at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(gateway_timestamp_when_timestamp_was_received));

                                    // If the time matches +/- 90 seconds then don't set it again.
                                    // Using 90 rather than 30 as the AnD will advertise for up to 1 minute,
                                    // so the timestamp might be > 30 seconds out of date by the time we connect
                                    if (delta < 90*DateUtils.SECOND_IN_MILLIS)
                                    {
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp already set/valid");

                                        device_info.handleGeneratedTimestamp(received_timestamp_in_ms);
                                    }
                                    else
                                    {
                                        // Timestamp not valid. Set it
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp not set. Calling setTimestamp");

                                        device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());
                                    }

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_UA656.DATATYPE_BATTERY_LEVEL:
                                {
                                    int battery_percentage = intent.getIntExtra(BluetoothLe_AnD_UA656.BATTERY_PERCENTAGE, 0);
                                    long timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UA656.BATTERY_TIMESTAMP, 0);

                                    MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                            0,
                                            battery_percentage,
                                            timestamp_in_ms,
                                            getNtpTimeNowInMilliseconds());

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // .....and a Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            local_database_storage.storeBloodPressureBatteryMeasurement(device_info, measurement);

                                            // Save the Battery reading so can be read from the UI on demand
                                            device_info.setLastBatteryReading(battery_percentage, -1, timestamp_in_ms);

                                            patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed BluetoothLe_AnD_UA656 Battery Level but IGNORED as no Device Session in progress.");
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "Rx'ed BluetoothLe_AnD_UA656 Battery Level but IGNORED as no Patient Session in progress.");
                                    }
                                }
                                break;

                                default:
                                {
                                    // data_type == Unknown
                                    Log.w(TAG, "Unhandled in mGattUpdateReceiver_NoninWristOx3150Ble : Data Type = " + data_type + "   Data = " + data);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        Log.e(TAG, "exception occurred:");
                        Log.e(TAG, e.toString());
                    }
                }
                break;
            }
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver_AndUA1200 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            final BtleSensorDevice device_info = (BtleSensorDevice) device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_UA1200BLE);

            switch (action)
            {
                case BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING);
                }
                break;

                case BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING_SUCCESS:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING_SUCCESS");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);
                }
                break;

                case BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING_FAILURE:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING_FAILURE");

                    btle_scanning_timeout_handler.removeCallbacksAndMessages(null);

                    stopOnGoingBluetoothScan();

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING_FAILED);

                    checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                }
                break;

                case BluetoothLe_AnD_UA1200BLE.ACTION_CONNECTED:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UA1200BLE.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    handleConnection(device_info);
                }
                break;

                case BluetoothLe_AnD_UA1200BLE.ACTION_DISCONNECTED:
                {
                    Log.e(TAG, "BluetoothLe_AnD_UA1200BLE.ACTION_DISCONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    removeDeviceDisconnection(device_info);
                }
                break;

                case BluetoothLe_AnD_UA1200BLE.ACTION_UNEXPECTED_UNBOND:
                {
                    Log.e(TAG, "BluetoothLe_AnD_UA1200BLE.ACTION_UNEXPECTED_UNBOND");

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                }
                break;

                case BluetoothLe_AnD_UA1200BLE.ACTION_DATA_AVAILABLE:
                {
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                    try
                    {
                        String data = intent.getStringExtra(BluetoothLe_AnD_UA1200BLE.DATA_AS_STRING);
                        String data_type = intent.getStringExtra(BluetoothLe_AnD_UA1200BLE.DATA_TYPE);
                        String device_address = intent.getStringExtra(BluetoothLe_AnD_UA1200BLE.DEVICE_ADDRESS);

                        if ((data == null) || (device_address == null) || (data_type == null))
                        {
                            if (data == null)
                            {
                                Log.d(TAG, "data = null");
                            }

                            if (data_type == null)
                            {
                                Log.d(TAG, "data_type = null");
                            }

                            if (device_address == null)
                            {
                                Log.d(TAG, "device_address = null");
                            }
                        }
                        else
                        {
                            switch (data_type)
                            {
                                case BluetoothLe_AnD_UA1200BLE.DATATYPE_BLOOD_PRESSURE_MEASUREMENT:
                                {
                                    int systolic = intent.getIntExtra(BluetoothLe_AnD_UA1200BLE.SYSTOLIC, -1);
                                    int diastolic = intent.getIntExtra(BluetoothLe_AnD_UA1200BLE.DIASTOLIC, -1);
                                    int pulse_rate = intent.getIntExtra(BluetoothLe_AnD_UA1200BLE.PULSE_RATE, -1);
                                    int mean_arterial_pressure = intent.getIntExtra(BluetoothLe_AnD_UA1200BLE.MEAN_ARTERIAL_PRESSURE, -1);

                                    long measurement_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UA1200BLE.TIMESTAMP, -1);
                                    String measurement_timestamp_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_timestamp_in_ms);

                                    Date measurement_time = new Date();
                                    measurement_time.setTime(measurement_timestamp_in_ms);

                                    Log.i(TAG, "BluetoothLe_AnD_UA1200BLE.DATATYPE_BLOOD_PRESSURE_MEASUREMENT : systolic = " + systolic
                                            + ". diastolic = " + diastolic
                                            + ". pulse_rate = " + pulse_rate
                                            + ". mean_arterial_pressure = " + mean_arterial_pressure
                                            + ". measurement_timestamp_as_string = " + measurement_timestamp_as_string
                                    );

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            // ....and data captured date is after session start date
                                            if(measurement_time.after(new Date(device_info.device_session_start_time)))
                                            {
                                                int measurement_validity_time_in_seconds = gateway_settings.getBloodPressureLongTermMeasurementTimeoutInMinutes() * 60;

                                                MeasurementBloodPressure measurement = new MeasurementBloodPressure(systolic, diastolic, pulse_rate, measurement_timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                                                local_database_storage.storeBloodPressureMeasurement(device_info, measurement);

                                                reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.BLOOD_PRESSURE, measurement);
                                            }
                                            else
                                            {
                                                Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as captured time is before session start time");
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received Blood Pressure reading at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                    }
                                }
                                break;

                                case BluetoothLe_AnD_UA1200BLE.DATATYPE_FIRMWARE_REVISION:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    int firmware_int = intent.getIntExtra(BluetoothLe_AnD_UA1200BLE.FIRMWARE_VERSION_NUMBER, -1);
                                    String firmware_version_string = data.trim();

                                    device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_UA1200BLE.DATATYPE_GET_TIMESTAMP:
                                {
                                    long received_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UA1200BLE.RECEIVED_TIMESTAMP_AS_LONG, 0);
                                    long gateway_timestamp_when_timestamp_was_received = intent.getLongExtra(BluetoothLe_AnD_UA1200BLE.GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED, 0);

                                    long delta = Math.abs(received_timestamp_in_ms - gateway_timestamp_when_timestamp_was_received);

                                    Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " received timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(received_timestamp_in_ms) + " received at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(gateway_timestamp_when_timestamp_was_received));

                                    // If the time matches +/- 90 seconds then don't set it again.
                                    // Using 90 rather than 30 as the AnD will advertise for up to 1 minute,
                                    // so the timestamp might be > 30 seconds out of date by the time we connect
                                    if (delta < 90*DateUtils.SECOND_IN_MILLIS)
                                    {
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp already set/valid");

                                        device_info.handleGeneratedTimestamp(received_timestamp_in_ms);
                                    }
                                    else
                                    {
                                        // Timestamp not valid. Set it
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp not set. Calling setTimestamp");

                                        device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());
                                    }

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_UA1200BLE.DATATYPE_BATTERY_LEVEL:
                                {
                                    int battery_percentage = intent.getIntExtra(BluetoothLe_AnD_UA1200BLE.BATTERY_PERCENTAGE, 0);
                                    long timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UA1200BLE.BATTERY_TIMESTAMP, 0);

                                    MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                            0,
                                            battery_percentage,
                                            timestamp_in_ms,
                                            getNtpTimeNowInMilliseconds());

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // .....and a Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            local_database_storage.storeBloodPressureBatteryMeasurement(device_info, measurement);

                                            // Save the Battery reading so can be read from the UI on demand
                                            device_info.setLastBatteryReading(battery_percentage, -1, timestamp_in_ms);

                                            patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed BluetoothLe_AnD_UA1200BLE Battery Level but IGNORED as no Device Session in progress.");
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "Rx'ed BluetoothLe_AnD_UA1200BLE Battery Level but IGNORED as no Patient Session in progress.");
                                    }
                                }
                                break;

                                default:
                                {
                                    // data_type == Unknown
                                    Log.w(TAG, "Unhandled in mGattUpdateReceiver_NoninWristOx3150Ble : Data Type = " + data_type + "   Data = " + data);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        Log.e(TAG, "exception occurred:");
                        Log.e(TAG, e.toString());
                    }
                }
                break;
            }
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver_AndUc352 = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            final BtleSensorDevice device_info = (BtleSensorDevice) device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__AND_UC352BLE);

            switch (action)
            {
                case BluetoothLe_AnD_UC352BLE.ACTION_PAIRING:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UC352BLE.ACTION_PAIRING");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING);
                }
                break;

                case BluetoothLe_AnD_UC352BLE.ACTION_PAIRING_SUCCESS:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UC352BLE.ACTION_PAIRING_SUCCESS");

                    if(current_scan_type == device_info.device_type)
                    {
                        // Reset the progress timer so the progress bar starts from 0 again
                        setupAndRunBluetoothConnectionProgressTimer(device_info);

                        // Reset the scan timeout as well
                        startBleScanTimeout(device_info);
                    }

                    // Update the UI to show Pairing messages
                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRED__CONNECTING);
                }
                break;

                case BluetoothLe_AnD_UC352BLE.ACTION_PAIRING_FAILURE:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UC352BLE.ACTION_PAIRING_FAILURE");

                    btle_scanning_timeout_handler.removeCallbacksAndMessages(null);

                    stopOnGoingBluetoothScan();

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_PAIRING_FAILED);

                    checkFinishedTypeAndConnectNextDeviceToGatewayIfNeeded(device_info);
                }
                break;

                case BluetoothLe_AnD_UC352BLE.ACTION_CONNECTED:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UC352BLE.ACTION_CONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    handleConnection(device_info);
                }
                break;

                case BluetoothLe_AnD_UC352BLE.ACTION_DISCONNECTED:
                {
                    Log.e(TAG, "BluetoothLe_AnD_UC352BLE.ACTION_DISCONNECTED : Firmware version = " + device_info.getDeviceFirmwareVersionString());

                    removeDeviceDisconnection(device_info);
                }
                break;

                case BluetoothLe_AnD_UC352BLE.ACTION_UNEXPECTED_UNBOND:
                {
                    Log.e(TAG, "BluetoothLe_AnD_UC352BLE.ACTION_UNEXPECTED_UNBOND");

                    device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.UNBONDED);

                    // Tell the UI about the actual_device_connection_status change
                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                }
                break;

                case BluetoothLe_AnD_UC352BLE.ACTION_DATA_AVAILABLE:
                {
                    Log.d(TAG, "BluetoothLe_AnD_UC352BLE.ACTION_DATA_AVAILABLE");

                    patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_DEVICE_CONNECTED);

                    try
                    {
                        String data = intent.getStringExtra(BluetoothLe_AnD_UC352BLE.DATA_AS_STRING);
                        String data_type = intent.getStringExtra(BluetoothLe_AnD_UC352BLE.DATA_TYPE);
                        String device_address = intent.getStringExtra(BluetoothLe_AnD_UC352BLE.DEVICE_ADDRESS);

                        if ((data == null) || (device_address == null) || (data_type == null))
                        {
                            if (data == null)
                            {
                                Log.e(TAG, "BluetoothLe_AnD_UC352BLE data = null");
                            }

                            if (data_type == null)
                            {
                                Log.e(TAG, "BluetoothLe_AnD_UC352BLE data_type = null");
                            }

                            if (device_address == null)
                            {
                                Log.e(TAG, "BluetoothLe_AnD_UC352BLE device_address = null");
                            }
                        }
                        else
                        {
                            switch (data_type)
                            {
                                case BluetoothLe_AnD_UC352BLE.DATATYPE_WEIGHT_MEASUREMENT:
                                {
                                    Log.d(TAG, "BluetoothLe_AnD_UC352BLE.DATATYPE_WEIGHT_MEASUREMENT");
                                    double weight = intent.getDoubleExtra(BluetoothLe_AnD_UC352BLE.WEIGHT, -1);

                                    long measurement_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UC352BLE.TIMESTAMP, -1);
                                    String measurement_timestamp_as_string = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_timestamp_in_ms);

                                    Date measurement_time = new Date();
                                    measurement_time.setTime(measurement_timestamp_in_ms);

                                    Log.i(TAG, "BluetoothLe_AnD_UC352BLE.DATATYPE_WEIGHT_MEASUREMENT : weight = " + weight
                                            + ". measurement_timestamp_as_string = " + measurement_timestamp_as_string
                                    );

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // ....and a Device Session. The Change Session Settings page allows there to be a Patient Session but NO Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            // ....and data captured date is after session start date
                                            if(measurement_time.after(new Date(device_info.device_session_start_time)))
                                            {
                                                int measurement_validity_time_in_seconds = gateway_settings.getWeightLongTermMeasurementTimeoutInMinutes() * 60;

                                                MeasurementWeight measurement = new MeasurementWeight(weight, measurement_timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                                                local_database_storage.storeWeightScaleMeasurement(device_info, measurement);

                                                reportNewVitalsDataAndAddToMeasurementValidityTracker(VitalSignType.WEIGHT, measurement);
                                            }
                                            else
                                            {
                                                Log.d(TAG, "Received Weight Scale measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as captured time is before session start time");
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "Received Weight Scale measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Received Weight Scale measurement at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(measurement_time) + " but ignored as no Session in progress");
                                    }
                                }
                                break;

                                case BluetoothLe_AnD_UC352BLE.DATATYPE_FIRMWARE_REVISION:
                                {
                                    Log.i(TAG, "Received " + data_type + " : " + data);

                                    int firmware_int = intent.getIntExtra(BluetoothLe_AnD_UC352BLE.FIRMWARE_VERSION_NUMBER, -1);
                                    String firmware_version_string = data.trim();

                                    device_info.setDeviceFirmwareVersion(firmware_version_string, firmware_int);

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_UC352BLE.DATATYPE_GET_TIMESTAMP:
                                {
                                    long received_timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UC352BLE.RECEIVED_TIMESTAMP_AS_LONG, 0);
                                    long gateway_timestamp_when_timestamp_was_received = intent.getLongExtra(BluetoothLe_AnD_UC352BLE.GATEWAY_TIMESTAMP_WHEN_TIMESTAMP_WAS_RECEIVED, 0);

                                    long delta = Math.abs(received_timestamp_in_ms - gateway_timestamp_when_timestamp_was_received);

                                    Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " received timestamp = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(received_timestamp_in_ms) + " received at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(gateway_timestamp_when_timestamp_was_received));

                                    // If the time matches +/- 90 seconds then don't set it again.
                                    // Using 90 rather than 30 as the AnD will advertise for up to 1 minute,
                                    // so the timestamp might be > 30 seconds out of date by the time we connect
                                    if (delta < 90*DateUtils.SECOND_IN_MILLIS)
                                    {
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp already set/valid");

                                        device_info.handleGeneratedTimestamp(received_timestamp_in_ms);
                                    }
                                    else
                                    {
                                        // Timestamp not valid. Set it
                                        Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp not set. Calling setTimestamp");

                                        device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());
                                    }

                                    patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
                                }
                                break;

                                case BluetoothLe_AnD_UC352BLE.DATATYPE_BATTERY_LEVEL:
                                {
                                    int battery_percentage = intent.getIntExtra(BluetoothLe_AnD_UC352BLE.BATTERY_PERCENTAGE, 0);
                                    long timestamp_in_ms = intent.getLongExtra(BluetoothLe_AnD_UC352BLE.BATTERY_TIMESTAMP, 0);

                                    MeasurementBatteryReading measurement = new MeasurementBatteryReading(
                                            0,
                                            battery_percentage,
                                            timestamp_in_ms,
                                            getNtpTimeNowInMilliseconds());

                                    // Only process the received data is there is currently a Patient Session in progress.....
                                    if (isPatientSessionRunning())
                                    {
                                        // .....and a Device Session
                                        if (device_info.isDeviceSessionInProgress())
                                        {
                                            // Save the Battery reading so can be read from the UI on demand
                                            device_info.setLastBatteryReading(battery_percentage, -1, timestamp_in_ms);

                                            patient_gateway_outgoing_commands.reportDeviceBatteryLevel(device_info);

                                            local_database_storage.storeWeightScaleBatteryMeasurement(device_info, measurement);
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Rx'ed BluetoothLe_AnD_UC352BLE Battery Level but IGNORED as no Device Session in progress.");
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "Rx'ed BluetoothLe_AnD_UC352BLE Battery Level but IGNORED as no Patient Session in progress.");
                                    }
                                }
                                break;

                                default:
                                {
                                    // data_type == Unknown
                                    Log.w(TAG, "Unhandled in BluetoothLe_AnD_UC352BLE : Data Type = " + data_type + "   Data = " + data);
                                }
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        Log.e(TAG, "exception occurred:");
                        Log.e(TAG, e.toString());
                    }
                }
                break;
            }
        }
    };


    private void handleReceivedIsansysFormatTimestamp(BtleSensorDevice device_info, String data)
    {
        // Now work out if we need to send a new timestamp... (Lifetouch Blue / Lifetemp v1 and v2)
        if (data.equals("00 00 00 00 00 00 00 00 "))
        {
            // This means its the first time the device has connected to the Gateway.....
            Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp not set. Calling setTimestamp");

            device_info.setDeviceTimestamp(getNtpTimeNowInMilliseconds());

        }
        // Detect if this is the first connection for Instapatch
        else if (device_info.device_type == DeviceType.DEVICE_TYPE__INSTAPATCH) // | (device_info.device_type == DeviceType.DEVICE_TYPE__LIFETOUCH_GREEN)
        {
            // Only do this once at connection. This is BEFORE any Indications/Notifications are connected to.
            if (device_info.timestamp_offset == 0)
            {
                String[] bytes = data.split(" ");

                long millisecond_timestamp = (Long.parseLong(bytes[0], 16) << 24);
                millisecond_timestamp += (Long.parseLong(bytes[1], 16) << 16);
                millisecond_timestamp += (Long.parseLong(bytes[2], 16) << 8);
                millisecond_timestamp += Long.parseLong(bytes[3], 16);

                // Instapatch has been on "millisecond_timestamp" milliseconds
                // This is equal to NOW (ignoring Android delays)

                long time_now = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();

                // This is the time that needs to be added to every from Instapatch
                device_info.timestamp_offset = time_now - millisecond_timestamp;
            }
        }
        else
        {
            Log.d(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " : timestamp already set");

            device_info.parseTimestamp(data);
        }

        device_info.connectToCharacteristics();

        patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
    }


    private void setupAndRunBluetoothConnectionProgressTimer(final DeviceInfo device_info)
    {
        // Update the UI every 1% of desired_bluetooth_scan_period_in_milliseconds
        int timer_rate = device_info.desired_bluetooth_search_period_in_milliseconds / 100;

        device_info.cancelBluetoothConnectionTimer();
        device_info.makeNewBluetoothConnectionTimerObject();
        device_info.getBluetoothConnectionTimer().scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                Log.d(TAG, device_info.device_type + " : bluetooth_connection_counter = " + device_info.bluetooth_connection_counter);

                patient_gateway_outgoing_commands.sendCommandReportBluetoothScanProgress(device_info);

                device_info.bluetooth_connection_counter++;
            }
        }, 0, timer_rate);
    }


    private static IntentFilter makeLifetouchGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeLifetouch.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLeLifetouch.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLeLifetouch.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeLifetouch.ACTION_TURNED_OFF);
        intentFilter.addAction(BluetoothLeLifetouch.ACTION_RESCAN_REQUIRED);
        intentFilter.addAction(BluetoothLeLifetouch.ACTION_AUTHENTICATION_PASSED);
        intentFilter.addAction(BluetoothLeLifetouch.ACTION_AUTHENTICATION_FAILED);

        return intentFilter;
    }


    private static IntentFilter makeLifetouchThreeGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeLifetouchThree.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLeLifetouchThree.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLeLifetouchThree.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeLifetouchThree.ACTION_TURNED_OFF);
        intentFilter.addAction(BluetoothLeLifetouchThree.ACTION_RESCAN_REQUIRED);
        intentFilter.addAction(BluetoothLeLifetouchThree.ACTION_AUTHENTICATION_PASSED);
        intentFilter.addAction(BluetoothLeLifetouchThree.ACTION_AUTHENTICATION_FAILED);

        return intentFilter;
    }


    private static IntentFilter makeInstapatchGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeInstapatch.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLeInstapatch.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLeInstapatch.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeInstapatch.ACTION_TURNED_OFF);
        intentFilter.addAction(BluetoothLeInstapatch.ACTION_RESCAN_REQUIRED);
        intentFilter.addAction(BluetoothLeInstapatch.ACTION_AUTHENTICATION_PASSED);
        intentFilter.addAction(BluetoothLeInstapatch.ACTION_AUTHENTICATION_FAILED);

        return intentFilter;
    }


    private static IntentFilter makeLifetempV2GattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeLifetempV2.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLeLifetempV2.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLeLifetempV2.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeLifetempV2.ACTION_TURNED_OFF);

        return intentFilter;
    }


    private static IntentFilter makeNoninWristOx3150BleGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeNoninWristOx.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLeNoninWristOx.ACTION_PAIRING);
        intentFilter.addAction(BluetoothLeNoninWristOx.ACTION_PAIRING_SUCCESS);
        intentFilter.addAction(BluetoothLeNoninWristOx.ACTION_PAIRING_FAILURE);
        intentFilter.addAction(BluetoothLeNoninWristOx.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLeNoninWristOx.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }


    private static IntentFilter makeNonin3230GattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeNonin3230.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLeNonin3230.ACTION_PAIRING);
        intentFilter.addAction(BluetoothLeNonin3230.ACTION_PAIRING_SUCCESS);
        intentFilter.addAction(BluetoothLeNonin3230.ACTION_PAIRING_FAILURE);
        intentFilter.addAction(BluetoothLeNonin3230.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLeNonin3230.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeNonin3230.ACTION_NONIN_3230_SPOT_MEASUREMENT_AVAILABLE);

        return intentFilter;
    }


    private static IntentFilter makeMedLinketBleGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeMedLinket.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLeMedLinket.ACTION_PAIRING);
        intentFilter.addAction(BluetoothLeMedLinket.ACTION_PAIRING_SUCCESS);
        intentFilter.addAction(BluetoothLeMedLinket.ACTION_PAIRING_FAILURE);
        intentFilter.addAction(BluetoothLeMedLinket.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLeMedLinket.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }


    private static IntentFilter makeAndTm2441BleGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLe_AnD_TM2441.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_TM2441.ACTION_PAIRING);
        intentFilter.addAction(BluetoothLe_AnD_TM2441.ACTION_PAIRING_SUCCESS);
        intentFilter.addAction(BluetoothLe_AnD_TM2441.ACTION_PAIRING_FAILURE);
        intentFilter.addAction(BluetoothLe_AnD_TM2441.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_TM2441.ACTION_UNEXPECTED_UNBOND);
        intentFilter.addAction(BluetoothLe_AnD_TM2441.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }


    private static IntentFilter makeAndUa651BleGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLe_AnD_UA651.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_UA651.ACTION_PAIRING);
        intentFilter.addAction(BluetoothLe_AnD_UA651.ACTION_PAIRING_SUCCESS);
        intentFilter.addAction(BluetoothLe_AnD_UA651.ACTION_PAIRING_FAILURE);
        intentFilter.addAction(BluetoothLe_AnD_UA651.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_UA651.ACTION_UNEXPECTED_UNBOND);
        intentFilter.addAction(BluetoothLe_AnD_UA651.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }


    private static IntentFilter makeAndUa656BleGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLe_AnD_UA656.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_UA656.ACTION_PAIRING);
        intentFilter.addAction(BluetoothLe_AnD_UA656.ACTION_PAIRING_SUCCESS);
        intentFilter.addAction(BluetoothLe_AnD_UA656.ACTION_PAIRING_FAILURE);
        intentFilter.addAction(BluetoothLe_AnD_UA656.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_UA656.ACTION_UNEXPECTED_UNBOND);
        intentFilter.addAction(BluetoothLe_AnD_UA656.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }


    private static IntentFilter makeAndUA1200BleGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLe_AnD_UA1200BLE.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING);
        intentFilter.addAction(BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING_SUCCESS);
        intentFilter.addAction(BluetoothLe_AnD_UA1200BLE.ACTION_PAIRING_FAILURE);
        intentFilter.addAction(BluetoothLe_AnD_UA1200BLE.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_UA1200BLE.ACTION_UNEXPECTED_UNBOND);
        intentFilter.addAction(BluetoothLe_AnD_UA1200BLE.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }


    private static IntentFilter makeAndUc352BleGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLe_AnD_UC352BLE.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_UC352BLE.ACTION_PAIRING);
        intentFilter.addAction(BluetoothLe_AnD_UC352BLE.ACTION_PAIRING_SUCCESS);
        intentFilter.addAction(BluetoothLe_AnD_UC352BLE.ACTION_PAIRING_FAILURE);
        intentFilter.addAction(BluetoothLe_AnD_UC352BLE.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothLe_AnD_UC352BLE.ACTION_UNEXPECTED_UNBOND);
        intentFilter.addAction(BluetoothLe_AnD_UC352BLE.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }


    private boolean addDeviceInfoAndDeviceSessionIfNeeded(DeviceInfo device_info, long time, int updated_by_user_id)
    {
        // If Device in use and doesn't have a Device Info ID in the database, then its just been added. Record the Device Info for it
        if (device_info.isDeviceHumanReadableDeviceIdValid() && (device_info.android_database_device_info_id == INVALID_DEVICE_INFO_ID))
        {
            // Store addition in Gateway audit if device is an actual, real device (e.g. DEVICE_TYPE__LIFETOUCH) and not a non-device within DeviceType (e.g. DEVICE_TYPE__EARLY_WARNING_SCORE)
            if (device_info_manager.getListOfSensorDeviceInfoObjects().contains(device_info))
            {
                String device_details = device_info.device_name + " " + device_info.human_readable_device_id;

                // Using last known user ID for Audit log
                local_database_storage.storeAuditTrailEventUsingLastKnownUserId(AuditTrailEvent.DEVICE_WAS_JUST_ADDED, ntp_time.currentTimeMillis(), device_details);
            }

            device_info.setDeviceTypePartOfPatientSession(true);

            device_info.android_database_device_info_id = local_database_storage.insertDeviceInfo(device_info.device_type,
                                                                                           device_info.human_readable_device_id,
                                                                                           device_info.getDeviceFirmwareVersion(),
                                                                                           device_info.getDeviceFirmwareVersionString(),
                                                                                           device_info.bluetooth_address,
                                                                                           device_info.hardware_version,
                                                                                           device_info.model,
                                                                                           updated_by_user_id,
                                                                                           time);
            // Start a Device Session for it
            if (device_info.getAndroidDeviceSessionId()== INVALID_DEVICE_SESSION_ID)
            {
                int database_id = local_database_storage.insertDeviceSession(patient_session_info.android_database_patient_session_id,
                                                                                                     device_info.android_database_device_info_id,
                                                                                                     time,
                                                                                                     updated_by_user_id,
                                                                                                     patient_session_info.server_patient_session_id);

                device_info.setAndroidDeviceSessionId(database_id);
            }

            device_info.getOrSetTimestamp(time);

            if (device_info.dummy_data_mode)
            {
                // Real device sets/gets Timestamp on device. Dummy data mode doesn't use that code path so explicitly set it here
                device_info.device_session_start_time = time;

                // Dummy data mode creates data with timestamp of "now" i.e. the time it was created.
                // So set the offset value to 0.
                device_info.start_date_at_midnight_in_milliseconds = 0;
            }


            addDeviceSessionToListIfNotAlreadyAdded(device_info);

            Log.e(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + ".android_database_device_info_id = " + device_info.android_database_device_info_id);
            Log.e(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + ".android_database_device_session_id = " + device_info.getAndroidDeviceSessionId());

            return true;
        }
        else
        {
            // Device Session already exists
            return false;
        }
    }


    private void addDeviceSessionToListIfNotAlreadyAdded(DeviceInfo device_info)
    {
        boolean already_added = false;

        for(DeviceSession session : device_sessions)
        {
            if (session.local_device_session_id == device_info.getAndroidDeviceSessionId())
            {
                already_added = true;
                break;
            }
        }

        if(already_added != true)
        {
            device_sessions.add(new DeviceSession(device_info.sensor_type, device_info.device_type, device_info.getAndroidDeviceSessionId()));
        }
    }


    // Called by CMD_UPDATE_EXISTING_SESSION and createNewSession
    private void addDeviceInfoAndDeviceSessionsIfNeeded(int session_updated_by_user_id, long session_updated_time)
    {
        Log.e(TAG, "addDeviceInfoAndDeviceSessionsIfNeeded : session_updated_time = " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(session_updated_time) + " : session_updated_by_user_id = " + session_updated_by_user_id);

        DeviceInfo device_info;

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__LIFETOUCH);
        if (addDeviceInfoAndDeviceSessionIfNeeded(device_info, session_updated_time, session_updated_by_user_id))
        {
            // don't need to set device type here, as it's not used yet, but might be useful to have in future...
            lifetouch_heart_beat_processor.setDeviceType(device_info.device_type);

            handleBtleConnection(device_info);
        }

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__TEMPERATURE);
        if (addDeviceInfoAndDeviceSessionIfNeeded(device_info, session_updated_time, session_updated_by_user_id))
        {
            handleBtleConnection(device_info);
        }

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__SPO2);
        if (addDeviceInfoAndDeviceSessionIfNeeded(device_info, session_updated_time, session_updated_by_user_id))
        {
            // Init the SpO2 algorithm now the device session has started
            intermediate_spo2_processor.setDeviceType(device_info.device_type);

            switch (device_info.device_type)
            {
                case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
                {
                    handleBtleConnection(device_info);
                }
                break;
            }
        }

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
        addDeviceInfoAndDeviceSessionIfNeeded(device_info, session_updated_time, session_updated_by_user_id);

        device_info = device_info_manager.getDeviceInfoBySensorType(SensorType.SENSOR_TYPE__WEIGHT_SCALE);
        addDeviceInfoAndDeviceSessionIfNeeded(device_info, session_updated_time, session_updated_by_user_id);

        device_info = device_info_manager.getDeviceInfoByDeviceType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE);
        if(addDeviceInfoAndDeviceSessionIfNeeded(device_info, session_updated_time, session_updated_by_user_id))
        {
            early_warning_score_processor.enableProcessing(true);
        }

        device_info = addGatewayTabletInfo(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY, app_versions.getGatewayVersionNumber());
        addDeviceInfoAndDeviceSessionIfNeeded(device_info, session_updated_time, session_updated_by_user_id);

        device_info = addGatewayTabletInfo(DeviceType.DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE, app_versions.getUserInterfaceVersionNumber());
        addDeviceInfoAndDeviceSessionIfNeeded(device_info, session_updated_time, session_updated_by_user_id);

        // Let the UI know the new device session numbers
        patient_gateway_outgoing_commands.reportGatewaySessionNumbers(patient_session_info.android_database_patient_session_id, device_sessions);
    }


    private long isansysParseHex(String number_as_hex_string)
    {
        String padded_hex_string = Utils.leftPadWithZeroes(number_as_hex_string, 16);

        String upper_half = padded_hex_string.substring(0, 8);
        long upper_half_long = Long.parseLong(upper_half, 16);

        String lower_half = padded_hex_string.substring(8, 16);
        long lower_half_long = Long.parseLong(lower_half, 16);

        upper_half_long = upper_half_long * 256;
        upper_half_long = upper_half_long * 256;
        upper_half_long = upper_half_long * 256;
        upper_half_long = upper_half_long * 256;

        return upper_half_long + lower_half_long;
    }


    private DeviceInfo addGatewayTabletInfo(DeviceType device_type, String version_number)
    {
        String unique_device_id = getAndroidUniqueDeviceId();

        // Tablet W0062 has ID of e56e0f4fabb698da which refuses to parse
        //long unique_device_id_as_number = Long.parseLong("e56e0f4fabb698da", 16);

        long unique_device_id_as_number = isansysParseHex(unique_device_id);

        DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(device_type);
        device_info.hardware_version = BluetoothAdapter.getDefaultAdapter().getName();
        device_info.model = Build.MODEL;
        device_info.human_readable_device_id = unique_device_id_as_number;
        device_info.setDeviceFirmwareVersion(version_number, Integer.parseInt(version_number));
        device_info.bluetooth_address = unique_device_id;
        return device_info;
    }


    private void handleBtleConnection(DeviceInfo device_info)
    {
        if(device_info.isActualDeviceConnectionStatusConnected())
        {
            Log.e(TAG, "addDeviceInfoAndDeviceSessionsIfNeeded : handleConnection " + device_info.getSensorTypeAndDeviceTypeAsString());

            handleConnection(device_info);
        }
    }


    static public long getNtpTimeNowInMillisecondsStatic()
    {
        return ntp_time.currentTimeMillis();
    }


    public long getNtpTimeNowInMilliseconds()
    {
        return ntp_time.currentTimeMillis();
    }


    public int getAndroidDatabasePatientSessionId()
    {
        return patient_session_info.android_database_patient_session_id;
    }


    public void setServersPatientSessionId(int desired_value)
    {
        patient_session_info.server_patient_session_id = desired_value;
    }


    private static final String TIME_SYNC = "TIME SYNC";
    private final Handler ntp_sync_handler = new Handler();

    private int ntp_get_time_async_task_retry_count = 0;
    private final int ntp_get_time_async_task_retry_max_tries = 5;


    public void forceNtpTimeSync()
    {
        showOnScreenMessage("Manual Time Sync command received");

        ntpTimeSync(true);
    }


    private void ntpTimeSync(boolean force)
    {
        if (gateway_settings.getInstallationComplete() || force)
        {
            Log.i(TIME_SYNC, "ntpTimeSync");

            ntp_get_time_async_task_retry_count = 0;

            runNtpSyncIfNoSessionRunningElseRetryInOneHour();
        }
        else
        {
            Log.i(TIME_SYNC, "Not doing time sync as Setup Wizard not run yet");
        }
    }


    private void runNtpSyncIfNoSessionRunningElseRetryInOneHour()
    {
        // Only do an NTP time sync if there is NO session running. If the local time is wrong at the start of the session then it MUST continue to be wrong
        // throughout the ENTIRE session as otherwise there is the risk of data being stored at 12.00:01 12.00:02 12.00:03 12.00:04 (TIMESYNC) 12.00:01 12.00:02
        // or there being a gap in the data 12.00:01 12.00:02 12.00:03 12.00:04 (TIMESYNC) 12.00:11 12.00:12
        if (patient_session_info.patient_session_running == false)
        {
            ntpGetTimeInBackground();
        }
        else
        {
            Log.d(TAG, "Not doing NTP sync as session running");
            queueUpNtpSyncInAnHour();
        }
    }


    private void queueUpNtpSyncInAnHour()
    {
        // Queue up the next sync in an hour
        ntp_sync_handler.removeCallbacksAndMessages(null);
        ntp_sync_handler.postDelayed(() -> ntpTimeSync(false), DateUtils.HOUR_IN_MILLIS);
    }


    private void ntpGetTimeInBackground()
    {
        Executors.newSingleThreadExecutor().execute(() -> {

            // Long running operation

            boolean no_server_or_network;
            final String TAG = "NtpGetTimeInBackground";
            boolean result;

            NTP_Simple ntp_simple = new NTP_Simple(this, Log);

            String server_name = gateway_settings.getServerAddress();
            Log.i(TAG, "Server Address is " + server_name);

            boolean connected_to_network = isConnectedToNetwork();

            if(server_name.equals(gateway_settings.NOT_SET_YET) || !connected_to_network)
            {
                Log.e(TAG, "Server isn't set or wifi is off. Not doing NTP Sync");
                Log.e(TAG, "Server Address is " + server_name);
                Log.e(TAG, "isConnectedToNetwork() = " + connected_to_network);

                no_server_or_network = true;
                result = false;
            }
            else
            {
                no_server_or_network = false;
                result = ntp_simple.doTimeSync(server_name);
            }

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> ntpPostExecute(result, no_server_or_network, ntp_simple.getLocalClockOffsetInMilliseconds()));
        });
    }

    private void ntpPostExecute(boolean success, boolean no_server_or_network, double local_clock_offset_in_milliseconds)
    {
        if (success)
        {
            queueUpNtpSyncInAnHour();

            if (local_clock_offset_in_milliseconds != 0)
            {
                AlternateTimeSource.setTimeOffsetInMilliseconds(local_clock_offset_in_milliseconds);
                patient_gateway_outgoing_commands.reportNtpClockOffset(true, local_clock_offset_in_milliseconds, -1, -1);

                Log.i(TIME_SYNC, "Clock offset value is " + local_clock_offset_in_milliseconds + " ms");

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

                Log.i(TIME_SYNC, "System.currentTimeMillis()    is " + df.format(new Date(System.currentTimeMillis())));
                Log.i(TIME_SYNC, "getNtpTimeNowInMilliseconds() is " + df.format(new Date(getNtpTimeNowInMilliseconds())));
            }
            else
            {
                Log.e(TIME_SYNC, "Error receiving the clock offset. Time is not update ");
            }

            setup_wizard.synchroniseTimeSuccess();
        }
        else
        {
            if (no_server_or_network == false)
            {
                ntp_get_time_async_task_retry_count++;

                Log.e(TAG, "NtpGetTimeAsyncTask : " + ntp_get_time_async_task_retry_count + "/" + ntp_get_time_async_task_retry_max_tries);

                if (ntp_get_time_async_task_retry_count < ntp_get_time_async_task_retry_max_tries)
                {
                    Log.e(TAG, "NtpGetTimeAsyncTask : Time sync failed. Retrying");

                    patient_gateway_outgoing_commands.reportNtpClockOffset(false, -1, ntp_get_time_async_task_retry_count, ntp_get_time_async_task_retry_max_tries);

                    runNtpSyncIfNoSessionRunningElseRetryInOneHour();
                }
                else
                {
                    Log.e(TAG, "NtpGetTimeAsyncTask : Time sync failed. Giving up and retrying in an hour");

                    patient_gateway_outgoing_commands.reportNtpClockOffset(false, -1, ntp_get_time_async_task_retry_count, ntp_get_time_async_task_retry_max_tries);

                    queueUpNtpSyncInAnHour();

                    setup_wizard.synchroniseTimeFailure();
                }
            }
            else
            {
                // No Server address of no Network....its not a "Failure" at this point so do not try and retry now, but queue up another attempt in an hour
                queueUpNtpSyncInAnHour();

                setup_wizard.synchroniseTimeFailure();
            }
        }
    }


    /*
     * Timer to set the progress bar of the "BLE device" in Patient vital display
     */
    private Timer measurement_validity_timer;

    /**
     * Function to start the Schedule progress of device measurement for FragmentPatientVitalDisplay
     * Called when Start Monitoring for session is pressed or resumePreviousSessionIfExists
     */
    private void updateDeviceMeasurementProgressBar()
    {
        GenericStartStopTimer.cancelTimer(measurement_validity_timer, Log);

        measurement_validity_timer = new Timer();
        measurement_validity_timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run()
            {
                //Log.d(TAG,"updateDeviceMeasurementProgressBar : timer for measurement progress bar triggered");
                patient_gateway_outgoing_commands.sendUpdateProgressBarMeasurement_UI();
            }
        }, 0, (int)DateUtils.SECOND_IN_MILLIS);
    }

    /**
     * Remove the timer for device measurement progress bar
     */
    private void removeDeviceMeasurementProgressBarTimer()
    {
        GenericStartStopTimer.cancelTimer(measurement_validity_timer, Log);
    }


    private void unRegisterServerUpdateService()
    {
        server_syncing.unRegisterGatewayContentObservers();
    }


    public void periodicMode_enterDeviceSetupMode()
    {
        Log.e(TAG, "periodicMode_enterDeviceSetupMode global");

        for (DeviceInfo device_info : device_info_manager.getListOfSensorDeviceInfoObjectsThatSupportSetupMode())
        {
            if (device_info.device_type == DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE && nonin_setup_mode_blocked == true)
            {
                Log.d(TAG, "Not entering Periodic setup mode because Nonin has recently re-connected and a playback may occur");
            }
            else
            {
                periodicMode_enterDeviceSetupMode(device_info_manager.getDeviceInfoByDeviceType(device_info.device_type));
            }
        }
    }


    public void periodicMode_exitDeviceSetupMode()
    {
        Log.e(TAG, "periodicMode_exitDeviceSetupMode global");

        for (DeviceInfo device_info : device_info_manager.getListOfSensorDeviceInfoObjectsThatSupportSetupMode())
        {
            periodicMode_exitDeviceSetupMode(device_info_manager.getDeviceInfoByDeviceType(device_info.device_type));
        }
    }


    private void periodicMode_enterDeviceSetupMode(DeviceInfo device_info)
    {
        if (device_info.isDeviceSessionInProgress())
        {
            if (device_info.isActualDeviceConnectionStatusConnected())
            {
                if (device_info.device_disconnected_from_body == false)
                {
                    Log.d(TAG, "Starting " + device_info.getSensorTypeAndDeviceTypeAsString() + " Periodic Setup Mode");

                    boolean send_low_level_commands_to_device = true;

                    // If setup mode was already active when Periodic setup mode became active, need to signify end of setup mode.
                    // However do NOT send the low level Exit Setup Mode command, as the device will go straight back into setup mode below and this could cause timing issues
                    if (device_info.isInSetupMode())
                    {
                        send_low_level_commands_to_device = false;

                        exitDeviceSetupMode(device_info, send_low_level_commands_to_device);
                    }

                    enterDeviceSetupMode(device_info, DeviceInfo.OperatingMode.PERIODIC_SETUP_MODE, send_low_level_commands_to_device);

                    // Tell the UI to switch to the Period Setup Mode view
                    patient_gateway_outgoing_commands.reportDisplayDevicePeriodicModeDataInUserInterface(device_info.device_type, true);
                }
                else
                {
                    Log.e(TAG, device_info.getSensorTypeAndDeviceTypeAsString() + " leads off so not entering setup mode");
                }
            }
        }
    }


    private void periodicMode_exitDeviceSetupMode(DeviceInfo device_info)
    {
        if (device_info.isDeviceSessionInProgress())
        {
            if (device_info.isActualDeviceConnectionStatusConnected())
            {
                Log.e(TAG, "periodicMode_exitDeviceSetupMode : " + device_info.getSensorTypeAndDeviceTypeAsString());

                exitSetupModeIfRunning(device_info);

                // Tell the UI to stop showing Periodic Setup Mode display
                patient_gateway_outgoing_commands.reportDisplayDevicePeriodicModeDataInUserInterface(device_info.device_type, false);
            }
        }
    }


    public void setSetupModeTimeInSeconds(int setup_mode_time_in_seconds)
    {
        Log.d(TAG, "setSetupModeTimeInSeconds = " + setup_mode_time_in_seconds);

        if(setup_mode_time_in_seconds > 0)
        {
            gateway_settings.storeSetupModeTimeInSeconds(setup_mode_time_in_seconds);

            if (setup_mode_time_in_seconds == CONTINUOUS_SETUP_MODE)
            {
                // User has requested Continuous Setup Mode.
                // The UI code will take care of disabling access to Periodic Setup Mode, but it does NOT stop it, if its already running
                enableDevicePeriodicSetupMode(false);
            }
        }
        else
        {
            Log.e(TAG, "setSetupModeTimeInSeconds : setup_mode_time_in_seconds is INVALID, settings not changed");
        }

        patient_gateway_outgoing_commands.reportSetupModeTimeInSeconds(setup_mode_time_in_seconds);

        reportGatewayStatusToServer();
    }


    public void setDisplayTimeoutInSeconds(int timeout_in_seconds)
    {
        Log.d(TAG, "setDisplayTimeoutInSeconds = " + timeout_in_seconds);

        gateway_settings.storeDisplayTimeoutLengthInSeconds(timeout_in_seconds);

        patient_gateway_outgoing_commands.reportDisplayTimeoutInSeconds(timeout_in_seconds);

        reportGatewayStatusToServer();
    }


    private void setDevicePeriodicModePeriodTimeInSeconds(int time_in_seconds)
    {
        boolean new_value = time_in_seconds != gateway_settings.getPeriodicModePeriodTimeInSeconds();

        if(time_in_seconds > 0)
        {
            gateway_settings.storePeriodicModePeriodTimeInSeconds(time_in_seconds);

            Log.d(TAG, "setDevicePeriodicModePeriodTimeInSeconds  = " + time_in_seconds);

            patient_gateway_outgoing_commands.reportDevicePeriodicModePeriodTimeInSeconds(gateway_settings.getPeriodicModePeriodTimeInSeconds());

            if (new_value)
            {
                // Restart Periodic mode if period time has changed
                if (gateway_settings.getPeriodicModeEnabledStatus())
                {
                    Log.d(TAG, "setDevicePeriodicModePeriodTimeInSeconds : Restarting Periodic Mode");

                    device_periodic_mode.disable();
                    device_periodic_mode.enable(time_in_seconds, gateway_settings.getPeriodicModeActiveTimeInSeconds());
                }
            }
        }
        else
        {
            Log.e(TAG, "setDevicePeriodicModePeriodTimeInSeconds : time_in_seconds is INVALID, settings not changed");
        }

        reportGatewayStatusToServer();
    }


    private void setDevicePeriodicModeActiveTimeInSeconds(int time_in_seconds)
    {
        boolean new_value = time_in_seconds != gateway_settings.getPeriodicModeActiveTimeInSeconds();

        if(time_in_seconds > 0)
        {
            gateway_settings.storePeriodicModeActiveTimeInSeconds(time_in_seconds);

            Log.d(TAG, "setDevicePeriodicModeActiveTimeInSeconds = " + time_in_seconds);

            patient_gateway_outgoing_commands.reportDevicePeriodicModeActiveTimeInSeconds(gateway_settings.getPeriodicModeActiveTimeInSeconds());

            if (new_value)
            {
                // Restart Periodic mode if active time has changed
                if (gateway_settings.getPeriodicModeEnabledStatus())
                {
                    Log.d(TAG, "setDevicePeriodicModeActiveTimeInSeconds : Restarting Periodic Mode");

                    device_periodic_mode.disable();
                    device_periodic_mode.enable(gateway_settings.getPeriodicModePeriodTimeInSeconds(), time_in_seconds);
                }
            }
        }
        else
        {
            Log.e(TAG, "setDevicePeriodicModeActiveTimeInSeconds : time_in_seconds is INVALID, settings not changed");
        }

        reportGatewayStatusToServer();
    }


    enum ThresholdQueryType
    {
        THRESHOLD_SETS,
        THRESHOLD_AGE_BLOCK_DETAILS,
        THRESHOLD_SET_LEVELS,
        THRESHOLD_SET_COLOURS,
    }


    private void queryThresholdSets()
    {
        String[] projection = {
                TableThresholdSet.COLUMN_ID,
                TableThresholdSet.COLUMN_SERVERS_ID,
                TableThresholdSet.COLUMN_NAME,
                TableThresholdSet.COLUMN_IS_DEFAULT
                };


        ews_query_handler.startQuery(ThresholdQueryType.THRESHOLD_SETS.ordinal(), null, IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SETS, projection, null, null, null);
    }


    private void processThresholdSets(Cursor cursor)
    {
        default_early_warning_score_threshold_sets.clear();

        int cursor_count = cursor.getCount();

        Log.d(TAG, "EARLY_WARNING_SCORE_THRESHOLD_SETS size = " + cursor_count);

        if(cursor_count > 0)
        {
            cursor.moveToFirst();

            // Parse the records
            while (!cursor.isAfterLast())
            {
                ThresholdSet this_threshold_set = new ThresholdSet();

                this_threshold_set.local_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSet.COLUMN_ID));
                this_threshold_set.servers_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSet.COLUMN_SERVERS_ID));
                this_threshold_set.name = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSet.COLUMN_NAME));

                String is_default_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSet.COLUMN_IS_DEFAULT));
                this_threshold_set.is_default = is_default_as_string.equals("1");

                default_early_warning_score_threshold_sets.add(this_threshold_set);

                cursor.moveToNext();
            }
        }
        else
        {
            Log.d(TAG, "processThresholdSets : Cursor count is ZERO");

            // Tell the UI to read the EWS thresholds again (so it knows there arent any)
            patient_gateway_outgoing_commands.reportCachedDataUpdated(QueryType.EARLY_WARNING_SCORE_THRESHOLD_SETS);

            if (gateway_settings.getInstallationComplete() && (gateway_settings.getSoftwareUpdateModeActive() == false))
            {
                // If there are no EWS threshold but Installation complete flag, it means the database has been emptied/upgraded
                // If update is in progress, we can wait for it to complete, otherwise need to re-sync them
                Log.d(TAG, "processThresholdSets : Telling the UI as Installation Complete flag is true - so these need to be redownloaded");

                patient_gateway_outgoing_commands.reportServerDataDownloadStartingDueToMissingThresholds();

                setup_wizard.startGetServerSettings();
            }
        }

        queryThresholdSetAgeBlockDetails();
    }


    private void queryThresholdSetAgeBlockDetails()
    {
        String[] projection = {
                TableThresholdSetAgeBlockDetail.COLUMN_ID,
                TableThresholdSetAgeBlockDetail.COLUMN_SERVERS_ID,
                TableThresholdSetAgeBlockDetail.COLUMN_THRESHOLD_SET_ID,
                TableThresholdSetAgeBlockDetail.COLUMN_AGE_BOTTOM,
                TableThresholdSetAgeBlockDetail.COLUMN_AGE_TOP,
                TableThresholdSetAgeBlockDetail.COLUMN_DISPLAY_NAME,
                TableThresholdSetAgeBlockDetail.COLUMN_IS_ADULT,
                };


        ews_query_handler.startQuery(ThresholdQueryType.THRESHOLD_AGE_BLOCK_DETAILS.ordinal(), null, IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS, projection, null, null, null);
    }


    private void processThresholdSetAgeBlockDetails(Cursor cursor)
    {
        int cursor_count  = cursor.getCount();

        Log.d(TAG, "EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS size = " + cursor_count);

        if(cursor_count > 0)
        {
            cursor.moveToFirst();

            // Parse the records
            while (!cursor.isAfterLast())
            {
                String local_database_row_id_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetAgeBlockDetail.COLUMN_ID));
                String server_database_row_id_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetAgeBlockDetail.COLUMN_SERVERS_ID));
                String threshold_set_id_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetAgeBlockDetail.COLUMN_THRESHOLD_SET_ID));
                String age_bottom_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetAgeBlockDetail.COLUMN_AGE_BOTTOM));
                String age_top_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetAgeBlockDetail.COLUMN_AGE_TOP));
                String display_name_id_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetAgeBlockDetail.COLUMN_DISPLAY_NAME));
                String is_adult_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetAgeBlockDetail.COLUMN_IS_ADULT));

                int threshold_set_id = Integer.parseInt(threshold_set_id_as_string);

                ThresholdSetAgeBlockDetail this_threshold_set_age_block_detail = new ThresholdSetAgeBlockDetail();
                this_threshold_set_age_block_detail.local_database_row_id = Integer.parseInt(local_database_row_id_as_string);
                this_threshold_set_age_block_detail.servers_database_row_id = Integer.parseInt(server_database_row_id_as_string);
                this_threshold_set_age_block_detail.age_range_bottom = Integer.parseInt(age_bottom_as_string);
                this_threshold_set_age_block_detail.age_range_top = Integer.parseInt(age_top_as_string);
                this_threshold_set_age_block_detail.display_name = display_name_id_as_string;
                this_threshold_set_age_block_detail.is_adult = (Integer.parseInt(is_adult_as_string) > 0);

                for (ThresholdSet threshold_set : default_early_warning_score_threshold_sets)
                {
                    if (threshold_set.local_database_row_id == threshold_set_id)
                    {
                        threshold_set.list_threshold_set_age_block_detail.add(this_threshold_set_age_block_detail);
                    }
                }

                cursor.moveToNext();
            }
        }
        else
        {
            Log.e(TAG, "processThresholdSetAgeBlockDetails : Cursor count is ZERO");
        }

        // Query the next level of data
        queryThresholdSetLevels();
    }


    private void queryThresholdSetLevels()
    {
        String[] projection = {
                TableThresholdSetLevel.COLUMN_ID,
                TableThresholdSetLevel.COLUMN_SERVERS_ID,
                TableThresholdSetLevel.COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID,
                TableThresholdSetLevel.COLUMN_RANGE_BOTTOM,
                TableThresholdSetLevel.COLUMN_RANGE_TOP,
                TableThresholdSetLevel.COLUMN_EARLY_WARNING_SCORE,
                TableThresholdSetLevel.COLUMN_MEASUREMENT_TYPE,
                TableThresholdSetLevel.COLUMN_MEASUREMENT_TYPE_AS_STRING,
                TableThresholdSetLevel.COLUMN_DISPLAY_TEXT,
                TableThresholdSetLevel.COLUMN_INFORMATION_TEXT,
                };

        ews_query_handler.startQuery(ThresholdQueryType.THRESHOLD_SET_LEVELS.ordinal(), null, IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS, projection, null, null, null);
    }


    private void processThresholdSetLevels(Cursor cursor)
    {
        int cursor_count =  cursor.getCount();

        Log.e(TAG, "EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS size = " + cursor_count);

        if(cursor_count > 0)
        {
            cursor.moveToFirst();

            // Parse the records
            while (!cursor.isAfterLast())
            {
                String local_database_row_id_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_ID));
                String server_database_row_id_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_SERVERS_ID));
                String threshold_set_age_block_detail_id_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID));
                String range_bottom_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_RANGE_BOTTOM));
                String range_top_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_RANGE_TOP));
                String early_warning_score_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_EARLY_WARNING_SCORE));
                String integer_measurement_type_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_MEASUREMENT_TYPE));
                String string_measurement_type_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_MEASUREMENT_TYPE_AS_STRING));
                String display_text = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_DISPLAY_TEXT));
                String information_text = cursor.getString(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_INFORMATION_TEXT));
                int threshold_set_age_block_detail_id = Integer.parseInt(threshold_set_age_block_detail_id_as_string);

                ThresholdSetLevel this_threshold_set_level = new ThresholdSetLevel(
                        Float.parseFloat(range_top_as_string),
                        Float.parseFloat(range_bottom_as_string),
                        Integer.parseInt(early_warning_score_as_string),
                        Integer.parseInt(integer_measurement_type_as_string),
                        string_measurement_type_as_string,
                        display_text,
                        information_text);

                this_threshold_set_level.local_database_row_id = Integer.parseInt(local_database_row_id_as_string);
                this_threshold_set_level.servers_database_row_id = Integer.parseInt(server_database_row_id_as_string);

                for (ThresholdSet threshold_set : default_early_warning_score_threshold_sets)
                {
                    for (ThresholdSetAgeBlockDetail threshold_set_age_block_detail : threshold_set.list_threshold_set_age_block_detail)
                    {
                        if (threshold_set_age_block_detail.local_database_row_id == threshold_set_age_block_detail_id)
                        {
                            while(threshold_set_age_block_detail.list_of_threshold_set_levels_by_measurement_type.size() <= this_threshold_set_level.measurement_type)
                            {
                                threshold_set_age_block_detail.list_of_threshold_set_levels_by_measurement_type.add(new ArrayList<>());
                            }

                            threshold_set_age_block_detail.list_of_threshold_set_levels_by_measurement_type.get(this_threshold_set_level.measurement_type).add(this_threshold_set_level);
                        }
                    }
                }

                cursor.moveToNext();
            }

            queryThresholdSetColours();
/*
            // If this is valid, then auto resuming an old (crashed) session
            if(isEarlyWarningScoreDeviceEnabled())
            {
                try
                {
                    ArrayList<ArrayList<ThresholdSetLevel>> selected_ews_levels = default_early_warning_score_threshold_sets.get(patient_info.threshold_set_id).list_threshold_set_age_block_detail.get(patient_info.threshold_age_range_id).list_of_threshold_set_levels_by_measurement_type;
                    String ews_name = default_early_warning_score_threshold_sets.get(patient_info.threshold_set_id).name;

                    early_warning_score_processor.cacheSelectedEarlyWarningScores(selected_ews_levels, ews_name);
                }
                catch(Exception e)
                {
                    Log.e(TAG, "Could not resume EWS session due to error");
                    Log.e(TAG, e.toString());
                }
            }
*/
            Log.e(TAG, "Threshold Set Levels read from local database");
        }
        else
        {
            Log.e(TAG, "processThresholdSetLevels : Cursor counter is ZERO");
        }
    }


    private void queryThresholdSetColours()
    {
        String[] projection = {
                TableThresholdSetColour.COLUMN_ID,
                TableThresholdSetColour.COLUMN_SERVERS_ID,
                TableThresholdSetColour.COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID,
                TableThresholdSetColour.COLUMN_SCORE,
                TableThresholdSetColour.COLUMN_COLOUR,
                TableThresholdSetColour.COLUMN_TEXT_COLOUR,
        };

        ews_query_handler.startQuery(ThresholdQueryType.THRESHOLD_SET_COLOURS.ordinal(), null, IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS, projection, null, null, null);
    }


    private void processThresholdSetColours(Cursor cursor)
    {
        int cursor_count =  cursor.getCount();

        Log.e(TAG, "EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS size = " + cursor_count);

        if(cursor_count > 0)
        {
            cursor.moveToFirst();

            // Parse the records
            while (!cursor.isAfterLast())
            {
                int local_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSetColour.COLUMN_ID));
                int server_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSetColour.COLUMN_SERVERS_ID));
                int threshold_set_age_block_detail_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSetColour.COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSetColour.COLUMN_SCORE));
                int colour = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSetColour.COLUMN_COLOUR));
                int text_colour = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSetColour.COLUMN_TEXT_COLOUR));

                ThresholdSetColour this_threshold_set_colour = new ThresholdSetColour(score, colour, text_colour);
                this_threshold_set_colour.local_database_row_id = local_database_row_id;
                this_threshold_set_colour.servers_database_row_id = server_database_row_id;

                for (ThresholdSet threshold_set : default_early_warning_score_threshold_sets)
                {
                    for (ThresholdSetAgeBlockDetail threshold_set_age_block_detail : threshold_set.list_threshold_set_age_block_detail)
                    {
                        if (threshold_set_age_block_detail.local_database_row_id == threshold_set_age_block_detail_id)
                        {
                            threshold_set_age_block_detail.list_of_threshold_set_colours.add(this_threshold_set_colour);
                        }
                    }
                }

                cursor.moveToNext();
            }

            Log.e(TAG, "Threshold Set Colours read from local database");
        }
        else
        {
            Log.e(TAG, "processThresholdSetColours : Cursor counter is ZERO");
        }
    }


    // Do not send the log line to UI if LogCat fragment is disable
    public static volatile boolean is_logCat_fragment_enable = false;

    private void setLogCatFragmentStatus(boolean enabled)
    {
        is_logCat_fragment_enable = enabled;
        Log.d(TAG, "setLogCatFragmentStatus, is_logCat_fragment_enable = " + is_logCat_fragment_enable);
    }


    private void sendCommandToDisplaySearchAgainButton()
    {
        for (DeviceInfo device_info : device_info_manager.getListOfSensorDeviceInfoObjects())
        {
            if(device_info.isDeviceHumanReadableDeviceIdValid())
            {
                if(device_info.isActualDeviceConnectionStatusNotPaired()
                    || device_info.isActualDeviceConnectionStatusUnexpectedlyUnbonded())
                {
                    // If device is added but not connected after scanning, send command to UserInterface to display "Search Again" button
                    patient_gateway_outgoing_commands.reportBluetoothDeviceNotConnected(device_info);
                }
            }
        }
    }


    private void stopOnGoingBluetoothScan()
    {
        if(current_scan_type != DeviceType.DEVICE_TYPE__INVALID)
        {
            DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(current_scan_type);

            Log.d(TAG, "stopOnGoingBluetoothScan : Stopping " + device_info.getSensorTypeAndDeviceTypeAsString() + " bluetooth scan");

            device_info.cancelBluetoothConnectionTimer();

            if(device_info instanceof BtClassicSensorDevice)
            {
                bluetooth_pairing.bluetoothCancelDiscoveryIfRequired();
            }
            else if (device_info instanceof BtleSensorDevice)
            {
                device_info_manager.getBluetoothLeDeviceController().stopRunningScan();
                ((BtleSensorDevice)device_info).disconnectDevice();
            }

            patient_gateway_outgoing_commands.updateUserInterfaceBluetoothStatus(device_info, BluetoothStatus.BLUETOOTH_SCAN_FINISHED__DEVICE_NOT_FOUND);
            device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.NOT_PAIRED);

            // Tell the UI about the actual_device_connection_status change
            patient_gateway_outgoing_commands.reportDeviceInfo(device_info);

            current_scan_type = DeviceType.DEVICE_TYPE__INVALID;
        }
    }


    static public boolean disableCommentsForSpeed()
    {
        return dummy_data_instance.isDummyDataModeBackFillEnabled();
    }


    private boolean previously_connected_to_network = false;

    private void handleConnectionEventIfNeeded()
    {
        boolean connected_to_network = isConnectedToNetwork();

        if (connected_to_network != previously_connected_to_network)
        {
            if (connected_to_network)
            {
                Log.e(TAG, "Newly connected to Network");

                ntpTimeSync(false);
            }
            else
            {
                Log.e(TAG, "Disconnected from network");
            }
        }

        previously_connected_to_network = connected_to_network;
    }


    public void wifiEventHappened(WifiEventManager.WifiStatus wifi_status)
    {
        last_wifi_status = wifi_status;

        if (inGsmOnlyMode())
        {
            Log.e(TAG, "wifiEventHappened but not reporting to UI as in GSM mode");
        }
        else
        {
            Log.d(TAG, "wifiEventHappened");

            patient_gateway_outgoing_commands.reportWifiStatusToUserInterface(wifi_status, ActiveNetworkTypes.WIFI);
        }

        handleConnectionEventIfNeeded();
    }


    public void gsmEventHappened(GsmEventManager.GsmStatus gsm_status)
    {
        last_gsm_status = gsm_status;

        if (inGsmOnlyMode())
        {
            Log.d(TAG, "gsmEventHappened");

            patient_gateway_outgoing_commands.reportGsmStatusToUserInterface(gsm_status, ActiveNetworkTypes.MOBILE);
        }
        else
        {
            Log.d(TAG, "gsmEventHappened but IGNORED as in Wifi mode");
        }

        handleConnectionEventIfNeeded();
    }


    private NetworkInfo getActiveNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo();
    }


    public enum ActiveNetworkTypes
    {
        UNKNOWN,
        NO_NETWORK,
        WIFI,
        MOBILE,
    }


/*
    private ActiveNetworkTypes getActiveNetworkType()
    {
        NetworkInfo active_network = getActiveNetwork();

        if (active_network != null)
        {
            if (active_network.getType() == ConnectivityManager.TYPE_WIFI)
            {
                return ActiveNetworkTypes.WIFI;
            }
            else if (active_network.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                return ActiveNetworkTypes.MOBILE;
            }
            else
            {
                Log.e(TAG, "Unknown network type in getActiveNetworkType : " + active_network.getType());
                return ActiveNetworkTypes.UNKNOWN;
            }
        }
        else
        {
            return ActiveNetworkTypes.NO_NETWORK;
        }
    }
*/


    public boolean isConnectedToNetwork()
    {
        NetworkInfo active_network = getActiveNetwork();

        if (active_network != null)
        {
            if (active_network.getType() == ConnectivityManager.TYPE_WIFI)
            {
                return active_network.isConnected();
            }
            else if (active_network.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                return active_network.isConnected();
            }
        }

        return false;
    }


    private static IntentFilter makeUsbAccessoryIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_USB_ACCESSORY_RECEIVED_DATA);
        intentFilter.addAction(MainActivity.ACTION_USB_ACCESSORY_CONNECTED);
        intentFilter.addAction(MainActivity.ACTION_USB_ACCESSORY_DISCONNECTED);
        return intentFilter;
    }


    private final BroadcastReceiver broadcastReceiverFromUsbAccessory = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            switch (action)
            {
                case MainActivity.ACTION_USB_ACCESSORY_RECEIVED_DATA:
                {
                    String message = intent.getStringExtra("message");

                    byte[] payload = intent.getByteArrayExtra("payload");

                    Log.d(TAG, "Got broadcast : " + message);

                    byte[] aes_key_byte_array = Utils.hexStringToByteArray("000102030405060708090a0b0c0d0e0f");

                    byte[] plaintext = decryptQrCode(payload, aes_key_byte_array);

                    QrCodeData qr_info = new QrCodeData(plaintext, Log);

                    patient_gateway_outgoing_commands.reportQrCodeOrNfcDetailsToUserInterface(qr_info);
                }
                break;

                case MainActivity.ACTION_USB_ACCESSORY_CONNECTED:
                    showOnScreenMessage("ACTION_USB_ACCESSORY_CONNECTED");
                    patient_gateway_outgoing_commands.reportUsbAccessoryConnectedStatus(true);
                    break;

                case MainActivity.ACTION_USB_ACCESSORY_DISCONNECTED:
                    showOnScreenMessage("ACTION_USB_ACCESSORY_DISCONNECTED");
                    patient_gateway_outgoing_commands.reportUsbAccessoryConnectedStatus(false);
                    break;
            }
        }
    };


    private void sendToUsbAccessory(String message)
    {
        Intent intent = new Intent(MainActivity.ACTION_USB_ACCESSORY_TRANSMIT_DATA);
        intent.putExtra("message", message);
        getApplicationContext().sendBroadcast(intent);
    }


    public void enableSpO2SpotMeasurements(boolean enabled)
    {
        Log.d(TAG, "enableSpO2SpotMeasurements = " + enabled);

        gateway_settings.storeEnableSpO2SpotMeasurements(enabled);

        patient_gateway_outgoing_commands.reportSpO2SpotMeasurementsEnabledStatus(gateway_settings.getEnableSpO2SpotMeasurements());
        
        reportGatewayStatusToServer();
    }


    private void enableWifi(boolean enabled)
    {
        wifi_event_manager.setWifiEnabled(enabled);
    }


    private int mqtt_fail_count = 0;
    public void handleServerConnectedResult(boolean connected)
    {
        Log.e(TAG, "handleServerConnectedResult : connected = " + connected);

        if(connected)
        {
            reportGatewayStatusToServer();

            // If installation is running, WAMP/MQTT link test has succeeded and next step is synchroniseTime()
            setup_wizard.testRealtimeServerLinkSuccess();

            mqtt_fail_count = 0;
        }
        else
        {
            setup_wizard.testRealtimeServerLinkFailure();

            if(gateway_settings.getRealTimeServerType() == RealTimeServer.MQTT)
            {
                mqtt_fail_count ++;

                if(mqtt_fail_count >= 6)  // connection attempt every 10 seconds, so we check for certificates every minute.
                {
                    mqtt_fail_count = 0;
                    server_syncing.sendMQTTCertificateRequest();
                }
            }
        }

        patient_gateway_outgoing_commands.sendCommandReportConnectedToServerStatus(connected);
    }


    public Context getAppContext()
    {
        return getApplicationContext();
    }


    public void sendBroadcastIntent(Intent intent)
    {
        try
        {
            sendBroadcast(intent);
        }
        catch(Exception ex)
        {
            android.util.Log.e("sendBroadcastIntent", "Send broadcast failed: ", ex);
        }
    }

    private void resumePreviousSessionIfExists()
    {
        int patient_details_id = local_database_storage.getOutstandingPatientSession(patient_session_info);

        if (patient_details_id != 0)
        {
            Log.d("RESTARTING", "resumePreviousSessionIfExists - patient_details_id is valid");

            LocalDatabaseStorage.ThresholdSetIds thresholdSetIds = local_database_storage.getPatientDetails(patient_details_id, patient_info);
            ThresholdSet thresholdSet = getThresholdSetFromServersThresholdSetId(thresholdSetIds.servers_threshold_set_id);
            ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = getThresholdSetAgeBlockDetailFromServersThresholdSetAgeBlockDetailsId(thresholdSetIds.servers_threshold_set_age_block_id);
            patient_info.setThresholdSet(thresholdSet);
            patient_info.setThresholdSetAgeBlockDetails(thresholdSetAgeBlockDetail);

            // Create and Start the measurement validity timer running
            updateDeviceMeasurementProgressBar();

            // Make a new Measurement Validity Tracker
            if (long_term_measurement_validity_tracker != null)
            {
                long_term_measurement_validity_tracker.reset();
            }
            else
            {
                long_term_measurement_validity_tracker = new LongTermMeasurementValidityTracker(Log, early_warning_score_processor, ntp_time, device_info_manager);
            }

            // Now retrieve device sessions and info...
            ArrayList<Pair<DeviceSession, Date>> device_sessions_with_start_time = local_database_storage.getOutstandingDeviceSessions();

            for (Pair<DeviceSession, Date> session_pair : device_sessions_with_start_time)
            {
                local_database_storage.getDeviceInfo(device_info_manager, session_pair.first, session_pair.second.getTime());

                device_sessions.add(session_pair.first);
            }

            // Update the UI with device info and patient info...
            reportAllDeviceInfos();

            patient_gateway_outgoing_commands.reportPatientThresholdSet(patient_info);
            patient_gateway_outgoing_commands.reportHospitalPatientID(patient_info.getHospitalPatientId());

//TODO move over to SensorType
            for (DeviceSession session : device_sessions)
            {
                switch (session.device_type)
                {
                    case DEVICE_TYPE__LIFETOUCH:
                    case DEVICE_TYPE__LIFETEMP_V2:
                    {
//                        DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(session.type);
//                        device_info.setBleDeviceAddress();
                    }
                    break;

                    case DEVICE_TYPE__NONIN_WRIST_OX:
                    {
                        sendBroadcast(new Intent(NoninWristOx.SET_ADDED_TO_SESSION));
                    }
                    break;

                    case DEVICE_TYPE__AND_UA767:
                    {
                        // AnD also reconnects automatically if it's not unpaired.
                        sendBroadcast(new Intent(AnD_UA767.SET_ADDED_TO_SESSION));
                    }
                    break;
                }
            }

            device_info_manager.getBluetoothLeDeviceController().postBleReScanDefaultDelay();
        }
        else
        {
            // Close off existing sessions as a precaution - something could have gone wrong with resuming
            Log.d("RESTARTING", "resumePreviousSessionIfExists - could not resume, closing outstanding sessions");

            local_database_storage.closeOffAnyOutstandingSessions();
        }
    }


    private boolean lastSessionFileExists()
    {
        String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
        String log_directory = "/IsansysLogging/";
        String full_log_directory_path = SD_CARD_PATH + log_directory + "LastSession.txt";

        File myFile = new File(full_log_directory_path);

        return myFile.exists();
    }


    private void writeCurrentSessionFile(int session_id)
    {
        try
        {
            String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
            String log_directory = "/IsansysLogging/";
            String full_log_directory_path = SD_CARD_PATH + log_directory + "LastSession.txt";

            File myFile = new File(full_log_directory_path);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.append(TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(System.currentTimeMillis())).
                    append(" : patient session id = ").
                    append(Integer.toString(session_id));

            myOutWriter.close();
            fOut.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, "writeCurrentSessionFile Exception : " + e);
        }
    }


    private void deleteCurrentSessionFile()
    {
        String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
        String log_directory = "/IsansysLogging/";
        String full_log_directory_path = SD_CARD_PATH + log_directory + "LastSession.txt";

        File myFile = new File(full_log_directory_path);

        if(myFile.exists())
        {
            myFile.delete();
        }
    }


    public ArrayList<Integer> getOpenDeviceSessionIdList()
    {
        return device_info_manager.getListOfActiveDeviceSessions();
    }


    public void uploadLogFileToServer(File file)
    {
        server_syncing.uploadLogFile(file);
    }


    public String getWardName()
    {
        return gateway_settings.getGatewaysAssignedWardName();
    }


    public String getBedName()
    {
        return gateway_settings.getGatewaysAssignedBedName();
    }


    public String getBedId()
    {
        return gateway_settings.getGatewaysAssignedBedId();
    }


    public boolean serverLinkSetupConnectedAndSyncingEnabled()
    {
        return server_syncing.isRealTimeServerConfiguredAndConnected() && gateway_settings.getServerSyncEnabledStatus();
    }


    public boolean isBedIdSet()
    {
        return !gateway_settings.getGatewaysAssignedBedId().equals(gateway_settings.NOT_SET_YET);
    }


    public String getNotSetYetString()
    {
        return getResources().getString(R.string.not_set_yet);
    }


    public void handlePatientNameFromServerLookupOfHospitalPatientId(JsonObject json_object)
    {
        String firstName = json_object.get("firstName").getAsString();
        String lastName = json_object.get("lastName").getAsString();
        String dob = json_object.get("dob").getAsString();
        String gender = json_object.get("gender").getAsString();

        boolean complete = json_object.get("complete").getAsBoolean();                  // Server lookup completed
        PatientDetailsLookupStatus status = PatientDetailsLookupStatus.values()[json_object.get("gender").getAsInt()];

        Log.d(TAG, "handlePatientNameFromServerLookupOfHospitalPatientId : complete = " + complete);
        Log.d(TAG, "handlePatientNameFromServerLookupOfHospitalPatientId : status = " + status);

        patient_gateway_outgoing_commands.sendCommandReportPatientNameFromServerLookupOfHospitalPatientId(firstName, lastName, dob, gender, complete, status);
    }


    private <T extends MeasurementVitalSign> T handleManuallyEnteredDataPoint(Intent intent, DeviceType device_type, int by_user_id)
    {
        DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(device_type);

        T measurement;

        if(device_info.device_type != DeviceType.DEVICE_TYPE__INVALID)
        {
            device_info.setDeviceTypePartOfPatientSession(true);
            device_info.human_readable_device_id = 1;
            device_info.desired_device_connection_status = DeviceConnectionStatus.CONNECTED;
            device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.CONNECTED);

            measurement = intent.getParcelableExtra("data_point");

            addDeviceInfoAndDeviceSessionIfNeeded(device_info, measurement.timestamp_in_ms, by_user_id);
            patient_gateway_outgoing_commands.reportDeviceInfo(device_info);
        }
        else
        {
            measurement = intent.getParcelableExtra("data_point");
        }

        reportNewVitalsDataAndAddToMeasurementValidityTracker(measurement.getType(), measurement);

        return measurement;
    }


    private DeviceType getDeviceTypeFromIntent(Intent intent)
    {
        return DeviceType.values()[intent.getIntExtra("device_type", DeviceType.DEVICE_TYPE__INVALID.ordinal())];
    }


    private SensorType getSensorTypeFromIntent(Intent intent)
    {
        return SensorType.values()[intent.getIntExtra("sensor_type", SensorType.SENSOR_TYPE__INVALID.ordinal())];
    }


    private VitalSignType getVitalSignTypeFromIntent(Intent intent)
    {
        return VitalSignType.values()[intent.getIntExtra("vital_sign_type", -1)];
    }


    public DeviceInfo getDeviceInfoBySensorType(SensorType sensor_type)
    {
        return device_info_manager.getDeviceInfoBySensorType(sensor_type);
    }


    public boolean inGsmOnlyMode()
    {
        return gateway_settings.getGsmOnlyModeEnabledStatus();
    }


    private void startGetServerSettingsDueToSoftwareUpdate()
    {
        Log.d(TAG, "Forcing Server Config/Data download");

        patient_gateway_outgoing_commands.reportServerDataDownloadStartingDueToSoftwareUpdate();

        setup_wizard.startGetServerSettings();
    }

    
    private boolean needToGetNewServerData()
    {
        int gateway_server_settings_version = gateway_settings.getGatewayServerSettingsVersion();

        Log.d(TAG, "needToGetNewServerData : gateway_server_settings_version = " + gateway_server_settings_version);

        if(!gateway_settings.getInstallationComplete())
        {
            Log.d(TAG, "needToGetNewServerData : gateway installation incomplete - data will be synced during installation");

            return false;
        }
        else if (gateway_server_settings_version == gateway_settings.NOT_SET_YET_INT)
        {
            // If this is the first time the app has been installed, then this value will be NOT_SET_YET_INT
            return true;
        }
        else
        {
            // Otherwise we're installed and can check if version numbers are up to date
            if (app_versions.doAllVersionNumbersMatch())
            {
                Log.d(TAG, "needToGetNewServerData : Version numbers match = " + app_versions.getGatewayVersionNumberAsInt());

                // Apps are NOT in the process of being upgraded - all have the same version so its either pre upgrade or post upgrade
                if (gateway_server_settings_version == app_versions.getGatewayVersionNumberAsInt())
                {
                    // Nothing to do as Settings number matches app versions
                    Log.d(TAG, "needToGetNewServerData : No need for update");
                    return false;
                }
                else
                {
                    // Settings number does NOT match
                    // Need to download the Server Data again
                    Log.d(TAG, "needToGetNewServerData : Need update");
                    return true;
                }
            }
            else
            {
                // Apps are being updated as the version numbers do not match
                Log.d(TAG, "needToGetNewServerData : Application version numbers do NOT match. Therefore do not request new data from the Server");
                return false;
            }
        }
    }

    public void forwardVideoCallRequestToUserInterface(VideoCallDetails videoCallDetails)
    {
        patient_gateway_outgoing_commands.forwardVideoCallRequestToUserInterface(videoCallDetails);
    }

    public void forwardVideoCallJoinToUserInterface(VideoCallDetails videoCallDetails)
    {
        patient_gateway_outgoing_commands.forwardVideoCallJoinToUserInterface(videoCallDetails);
    }

    public void forwardVideoCallDeclinedToUserInterface(VideoCallContact contactThatDeclinedTheCall)
    {
        patient_gateway_outgoing_commands.forwardVideoCallDeclinedToUserInterface(contactThatDeclinedTheCall);
    }

    public void forwardBrowsersConnectionIdUserInterface(String connectionId)
    {
        patient_gateway_outgoing_commands.forwardBrowsersConnectionIdUserInterface(connectionId);
    }

    public boolean areWebPagesEnabled()
    {
        return gateway_settings.getViewWebPagesEnabledStatus();
    }
}
