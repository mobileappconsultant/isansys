package com.isansys.pse_isansysportal;

import static android.media.AudioManager.ADJUST_MUTE;
import static android.telephony.TelephonyManager.DATA_ACTIVITY_NONE;
import static android.telephony.TelephonyManager.DATA_DISCONNECTED;
import static com.isansys.common.DeviceInfoConstants.DUMMY_FIRMWARE_VERSION;
import static com.isansys.common.DeviceInfoConstants.INVALID_DEVICE_SESSION_ID;
import static com.isansys.common.DeviceInfoConstants.INVALID_FIRMWARE_VERSION;
import static com.isansys.common.DeviceInfoConstants.INVALID_HUMAN_READABLE_DEVICE_ID;
import static com.isansys.common.DeviceInfoConstants.NO_LOT_NUMBER;
import static java.lang.Math.PI;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.ajit.customseekbar.ProgressItem;
import com.isansys.common.AppVersions;
import com.isansys.common.BuildConfig;
import com.isansys.common.DeviceSession;
import com.isansys.common.ErrorCodes;
import com.isansys.common.FirmwareImage;
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
import com.isansys.common.enums.RadioType;
import com.isansys.common.enums.RealTimeServer;
import com.isansys.common.enums.SensorType;
import com.isansys.common.enums.ServerConfigurableTextStringTypes;
import com.isansys.common.enums.VideoCallStatus;
import com.isansys.common.measurements.MeasurementAnnotation;
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
import com.isansys.common.measurements.MeasurementRespirationDistress;
import com.isansys.common.measurements.MeasurementRespirationRate;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.common.measurements.MeasurementSupplementalOxygenLevel;
import com.isansys.common.measurements.MeasurementTemperature;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.MeasurementWeight;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.ntpTimeSync.AlternateTimeSource;
import com.isansys.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.BedInfo;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.ServerConfigurableText;
import com.isansys.patientgateway.WardInfo;
import com.isansys.patientgateway.deviceInfo.SetupModeLog;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;
import com.isansys.pse_isansysportal.enums.AnnotationEntryType;
import com.isansys.pse_isansysportal.enums.DayOrNightMode;
import com.isansys.pse_isansysportal.enums.KeyboardMode;
import com.isansys.pse_isansysportal.enums.ObservationSetEntryType;
import com.isansys.remotelogging.RemoteLogging;
import com.isansys.ui.permissions_fragment.FragmentPermissions;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import dagger.hilt.android.AndroidEntryPoint;
import io.openvidu.openvidu_android.openvidu.LocalParticipant;
import io.openvidu.openvidu_android.openvidu.RemoteParticipant;
import io.openvidu.openvidu_android.openvidu.Session;
import io.openvidu.openvidu_android.utils.CustomHttpClient;
import io.openvidu.openvidu_android.websocket.CustomWebSocket;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

@AndroidEntryPoint
public class MainActivity extends FragmentActivity implements MainActivityInterface, ContextInterface
{
    private long session_start_milliseconds = -1;
    private long session_start_date = -1;

    private boolean main_activity_resumed = false;

    private boolean software_update_mode_active = false;

    public final UpdateModeStatus updateModeStatus = new UpdateModeStatus();

    public boolean isSoftwareUpdateAvailable()
    {
        boolean available = false;

        available |= (updateModeStatus.available_gateway_version > app_versions.getGatewayVersionNumberAsInt());
        available |= (updateModeStatus.available_ui_version > app_versions.getUserInterfaceVersionNumberAsInt());

        return available;
    }

    public boolean isSoftwareUpdateAvailableAndNoSessionRunning()
    {
        return isSoftwareUpdateAvailable() && (is_session_in_progress == false);
    }

    class ScreenLockCountdownTimer extends CountDownTimer
    {
        ScreenLockCountdownTimer(long startTime, long interval)
        {
            super(startTime, interval);
        }

        @Override
        public void onFinish()
        {
            Log.d(TAG, "ScreenLockTimer : onFinish");

            if (features_enabled.gateway_setup_complete)
            {
                closeAllPopups();

                recordAuditTrailEventForScreenLockedByPageTimeout();

                lockScreenSelected();
            }
            else
            {
                showInstallationWelcomeFragmentAndResetHeader();
            }
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            Log.d(TAG, "ScreenLockTimer onTick : " + current_page + " : " + (millisUntilFinished / (int)DateUtils.SECOND_IN_MILLIS));
        }
    }

    private long screen_lock_timer_timeout_in_milliseconds = 2 * (int)DateUtils.MINUTE_IN_MILLIS;
    private ScreenLockCountdownTimer screen_lock_countdown__timer = new ScreenLockCountdownTimer(screen_lock_timer_timeout_in_milliseconds, (int)DateUtils.SECOND_IN_MILLIS);

    // Class to auto dim the screen if the user hasn't touched it in a while
    class ScreenDimmingCountdownTimer extends CountDownTimer
    {
        ScreenDimmingCountdownTimer(long startTime, long interval)
        {
            super(startTime, interval);
        }


        @Override
        public void onFinish()
        {
            if (day_or_night_mode == DayOrNightMode.DAY_MODE)
            {
                Log.d(TAG, "ScreenDimmingCountdownTimer : onFinish - DAY_MODE");

                setScreenBrightness(screen_dim_value_day);
            }
            else
            {
                Log.d(TAG, "ScreenDimmingCountdownTimer : onFinish - NIGHT_MODE");

                setScreenBrightness(screen_dim_value_night);
            }
        }


        @Override
        public void onTick(long millisUntilFinished)
        {
            Log.d(TAG, "ScreenDimmingCountdownTimer onTick : " + current_page + " : " + (millisUntilFinished / (int)DateUtils.SECOND_IN_MILLIS));
        }


        void touchEvent()
        {
            startScreenDimmingCountdownTimer();

            switch (day_or_night_mode)
            {
                case DAY_MODE:
                {
                    setScreenBrightness(current_screen_brightness_level_day);
                }
                break;

                case NIGHT_MODE:
                {
                    setScreenBrightness(current_screen_brightness_level_night);
                }
                break;
            }
        }
    }


    private final long screen_dimming_timeout_in_milliseconds = 2 * (int)DateUtils.MINUTE_IN_MILLIS;
    private final ScreenDimmingCountdownTimer screen_dimming_countdown_timer = new ScreenDimmingCountdownTimer(screen_dimming_timeout_in_milliseconds, (int)DateUtils.SECOND_IN_MILLIS);


    // Brightness levels that the screen dimming code dims to
    private final int screen_dim_value_night = 8;
    private final int screen_dim_value_day = 8;

    // Startup screen brightness
    private int current_screen_brightness_level_day = 60;
    private int current_screen_brightness_level_night = 128;

    // Max/Min that the Screen Slider can be set to in brightness levels
    private final int screen_brightness_level_min_day = 1;
    private final int screen_brightness_level_max_day = 128;

    private final int screen_brightness_level_min_night = 30;
    private final int screen_brightness_level_max_night = 128;

    private int display_timeout_length_in_seconds = 120;
    private boolean display_timeout_applies_to_patient_vitals = false;

    private VideoCallDetails videoCallDetails;
    private UserInterfacePage page_before_video_call_started = UserInterfacePage.INVALID;
    private VideoCallStatus videoCallStatus;
    private MediaPlayer mediaPlayer;


    // Prevents vertical scrolling in Patient Vitals Display, e.g. if scrolling horizontally
    private void disableVerticalScrolling()
    {
        CustomScrollView scrollView =  findViewById(R.id.patient_vitals_display_scrollview);
        scrollView.setScrollingEnabled(false);
    }


    private void enableVerticalScrolling()
    {
        CustomScrollView scrollView =  findViewById(R.id.patient_vitals_display_scrollview);
        scrollView.setScrollingEnabled(true);
    }


    // Stops Patient Vitals display scrolling if horizontal movement turns into a vertical movement before touch ceases, or if vertical movement is ongoing
    private void stopListeningForScroll()
    {
        gesture_detector = null;
    }


    private void startListeningForScroll()
    {
        gesture_detector = new GestureDetector(getAppContext(), gesture_listener);
    }


    // Code that fires when a finger touches (Down, Move and Up) the screen
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                Log.d(TAG, "dispatchTouchEvent : MotionEvent = ACTION_DOWN");

                unpluggedOverlayTouchEvent();
            }
            break;

            case MotionEvent.ACTION_MOVE:
            {
                //vibrator.vibrate(30);

                Log.d(TAG, "dispatchTouchEvent : MotionEvent = ACTION_MOVE");
            }
            break;

            case MotionEvent.ACTION_UP:
            {
                Log.d(TAG, "dispatchTouchEvent : MotionEvent = ACTION_UP");

                stopScreenLockCountdownTimerAndRestartIfDesired();

                screen_dimming_countdown_timer.touchEvent();
            }
            break;
        }


        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
        {
            if (gesture_detector != null)
            {
                gesture_detector.onTouchEvent(event);
            }
            else
            {
                scale_gesture_detector.onTouchEvent(event);
            }

            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                startListeningForScroll();
            }
        }

        return super.dispatchTouchEvent(event);
    }


    private RemoteLogging Log;
    private final String TAG = "UI";
    private SystemCommands portal_system_commands;

    private AppVersions app_versions;

    private FragmentHeader header_fragment = null;
    private FragmentFooter footer_fragment = null;

    private RelativeLayout relativeLayoutMain;

    //private Vibrator vibrator;

    private SavedMeasurements saved_measurements = null;

    private Timer user_interface_timer = null;

    private final GraphConfigs graph_configs = new GraphConfigs();

    private long graphViewFinalMinX;  // Used at the end of scaling to align graphs
    private long graphViewFinalMaxX;  // (Graphs may become misaligned during scaling across graphs)

    /**
     * This time source represents the sync time from Server.
     * All the application code which uses the Android's Clock Should use this ClockSource Instead
     */

    public static final TimeSource ntp_time = new AlternateTimeSource();


    private GestureDetector gesture_detector;
    private boolean scaling_handled_by_detector = false;  // Prevents scaling across 2 or more graphs from over scrolling graphs, effectively making scaling less sensitive

    private final GestureDetector.SimpleOnGestureListener gesture_listener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }

        // e1:The first down motion event that started the scrolling, e2: The move motion event that triggered the current onScroll.
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            try
            {
                double angle = Math.atan2(e2.getX() - e1.getX(), e2.getY() - e1.getY()) * 180 / PI;

                if ((angle > -45 && angle < 45) || angle > 135 || angle < -135)
                {
                    Log.d(TAG, "Vertical scrolling");

                    enableVerticalScrolling();
                }
                else
                {
                    Log.d(TAG, "Horizontal scrolling");

                    disableVerticalScrolling();
                }

                stopListeningForScroll();
            }

            catch(Exception e)
            {
                Log.w(TAG,"Exception in gesture_listener onScroll " + e);

                return true;
            }

            return true;
        }
    };


    // ScaleGestureDetector to detect a "global" pinch-zoom that might begin with a thumb on one graph and a finger on a different graph,
    // which would otherwise be interpreted as 2 scroll movement gestures (1 for each graph)
    private ScaleGestureDetector scale_gesture_detector;

    private final ScaleGestureDetector.OnScaleGestureListener scale_gesture_listener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.scaleAllGraphs(detector);
            }
            return true;
        }


        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector)
        {
            Log.d(TAG, "onScaleBegin" );

            disableVerticalScrolling();

            stopListeningForScroll();

            scaling_handled_by_detector = true;

            return true;
        }


        @Override
        public void onScaleEnd(ScaleGestureDetector detector)
        {
            Log.d(TAG, "onScaleEnd" );

            scaling_handled_by_detector = false;

            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.scrollOrScale(graphViewFinalMinX, graphViewFinalMaxX);
            }
        }
    };


    public enum UserInterfacePage
    {
        INVALID,
        UNLOCK_SCREEN,
        MODE_SELECTION,
        PATIENT_DETAILS_NAME,
        PATIENT_CASE_ID_ENTRY,
        PATIENT_THRESHOLD_CATEGORY,
        ADD_DEVICES,
        DEVICE_CONNECTION,
        CHECK_DEVICE_STATUS,
        CHANGE_SESSION_SETTINGS,

        OBSERVATION_SET_TIME_SELECTION,             // Pick the time of the observation set
        OBSERVATION_SET_VITAL_SIGN_SELECTION,       // Show the list of manual vitals to pick from
        OBSERVATION_SET_MEASUREMENT_KEYPAD_ENTRY,   // Show the keypad for entry
        OBSERVATION_SET_MEASUREMENT_BUTTON_ENTRY,   // Show the buttons for entry
        OBSERVATION_SET_VALIDITY_TIME_ENTRY,        // Validity time for observation set
        OBSERVATION_SET_CONFIRMATION,

        VIEW_MANUALLY_ENTERED_VITAL_SIGNS,
        ANNOTATION_ENTRY_SELECT_ANNOTATION_TYPE,
        ANNOTATION_ENTRY_SELECT_ANNOTATION_TIME,
        ANNOTATION_ENTRY_ENTER_ANNOTATION_VIA_KEYBOARD,
        ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_CONDITION,
        ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_ACTION,
        ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_OUTCOME,
        ANNOTATION_ENTRY_CONFIRMATION,
        END_SETTINGS,
        PATIENT_VITALS_DISPLAY,
        ADMIN_MODE,
        LOGCAT_DISPLAY,
        INSTALLATION_MODE_PROGRESS,
        INSTALLATION_MODE_WELCOME,
        INSTALLATION_MODE_SERVER_ADDRESS_SCAN,
        DUMMY_DATA_MODE,
        FEATURE_ENABLE_MODE,
        GATEWAY_NOT_RESPONDING,
        EMPTY_PATIENT_VITALS_DISPLAY,
        END_SESSION_TIME_SELECTION,
        GATEWAY_CONFIGURATION_ERROR,
        SOFTWARE_UPDATE_AVAILABLE,
        SOFTWARE_UPDATE_IN_PROGRESS,
        ANDROID_PERMISSIONS,
        MANUFACTURING_MODE__CHECK_PACKAGING,
        VIDEO_CALL_MODE_SELECTION,
        VIDEO_CALL_CONTACTS,
        VIDEO_CALL_CONTACTS_FROM_UNLOCK_PAGE,
        VIDEO_CALL_SCHEDULE,
        WEBPAGE_SELECTION,
        EMPTY,
    }

    private UserInterfacePage current_page = UserInterfacePage.UNLOCK_SCREEN;
    private UserInterfacePage previous_page_before_locking_page = UserInterfacePage.MODE_SELECTION;

    private PatientInfo patient_info = new PatientInfo();

    public enum ActiveNetworkTypes
    {
        UNKNOWN,
        NO_NETWORK,
        WIFI,
        MOBILE,
    }

    private ActiveNetworkTypes active_network_type = ActiveNetworkTypes.UNKNOWN;

    public enum WifiErrorStatus
    {
        NO_ERROR,
        NOT_CONFIGURED_ERROR,
        AUTHENTICATION_ERROR,
        UNKNOWN_SSID_ERROR,
        ADMIN_WIFI_DISABLED,
    }

    class WifiStatus
    {
        boolean hardware_enabled = false;
        boolean connected_to_ssid = false;
        String ssid = NOT_SET_YET;
        String ip_address_string = NOT_SET_YET;
        int wifi_level = 0;

        String wifi_connection_status = NOT_SET_YET;
        String wifi_BSSID = NOT_SET_YET;
        WifiErrorStatus mWifiErrorStatus = WifiErrorStatus.NO_ERROR;

        private WifiStatus()
        {

        }
    }

    private final WifiStatus wifi_status = new WifiStatus();


    static class GsmStatus
    {
        int data_activity_direction = DATA_ACTIVITY_NONE;
        int data_connection_state = DATA_DISCONNECTED;
        int network_type = TelephonyManager.NETWORK_TYPE_UNKNOWN;
        int signal_level = 0;

        private GsmStatus()
        {

        }
    }

    private final GsmStatus gsm_status = new GsmStatus();



    // Holds the User ID from the QR code that unlocked the Gateway
    private int gateway_user_id = -1;

    // Used to record options associated with auditable events, e.g. which manually entered vital signs type.
    private final int AUDIT_OPTION_NOT_APPLICABLE = -1;
    private int gateway_audit_last_option_chosen = AUDIT_OPTION_NOT_APPLICABLE;

    private final MeasurementCache measurement_cache = new MeasurementCache();
    private final SetupModeLogCache setup_mode_log_cache = new SetupModeLogCache();

    private String NOT_SET_YET = "NOT SET YET";

    private String patient_gateways_assigned_bed_id = NOT_SET_YET;
    private String patient_gateways_assigned_ward_name = NOT_SET_YET;
    private String patient_gateways_assigned_bed_name = NOT_SET_YET;

    private final int INVALID_PATIENT_SESSION = 0;
    private int patient_session_number = INVALID_PATIENT_SESSION;

    private boolean is_session_in_progress = false;

    private ArrayList<DeviceSession> device_sessions;

    private int server_status_code = -1;


    // Last HR measurement triggered a <300uV threshold or >10mV threshold
    private boolean poor_signal_in_last_minute = false;

    // Has the Lifetouch not detected any beats in the last 30 seconds
    private boolean lifetouch_no_beats_detected = false;
    private boolean previous_value_of_is_lifetouch_no_beats_detected = false;

    private int lifetemp_measurement_interval_in_seconds = 60;
    private int patient_orientation_measurement_interval_in_seconds = 60;


    // Do NOT set this anywhere in the UI. It is ONLY there to be updated by the CMD_REPORT_SERVER_SYNC_ENABLE_STATUS command
    private boolean server_sync_enabled = false;

    private final HeartBeatCache heart_beat_cache = new HeartBeatCache();

    private DayOrNightMode day_or_night_mode = DayOrNightMode.DAY_MODE;


    private final ArrayList<ThresholdSet> default_early_warning_score_threshold_sets = new ArrayList<>();

    private final ArrayList<WardInfo> cached_wards = new ArrayList<>();
    private final ArrayList<BedInfo> cached_beds = new ArrayList<>();

    private final ArrayList<WebPageButtonDescriptor> cached_webpages = new ArrayList<>();

    static class FeaturesEnabled
    {
        boolean gateway_setup_complete = false;
        boolean manually_entered_vital_signs = false;
        boolean csv_output = false;
        boolean run_devices_in_test_mode = false;
        boolean unplugged_overlay = false;
        boolean manufacturing_mode = false;
        boolean server_lookup_of_patient_name_from_patient_id = false;
        boolean simple_heart_rate = false;
        boolean gsm_only_mode = false;
        boolean use_back_camera = false;
        boolean patient_orientation = false;
        boolean show_numbers_of_battery_indicator = false;
        boolean show_mac_address = false;
        boolean usa_mode = false;
        boolean show_lifetouch_activity_level = false;
        boolean developer_popup = false;
        boolean show_ip_address_on_wifi_popup = false;
        boolean auto_resume_enabled = false;
        boolean auto_logfile_upload_to_server = false;
        boolean wifi_logging_enabled = false;
        boolean gsm_logging_enabled = false;
        boolean database_logging_enabled = false;
        boolean server_logging_enabled = false;
        boolean battery_logging_enabled = false;
        boolean dfu_bootloader_enabled = false;
        boolean spot_spot_measurements_enabled = false;
        boolean predefined_annotations_enabled = false;
        boolean auto_enable_ews = false;
        RealTimeServer realtime_server_type = RealTimeServer.INVALID;
        boolean stop_fast_ui_updates = false; // Only set true for some Appium tests, to streamline finding elements on the UI
        boolean video_calls_enabled = false;
        boolean show_temperature_in_fahrenheit = false;
        boolean show_weight_in_lbs = false;
        boolean view_webpages_enabled = false;
    }

    private final FeaturesEnabled features_enabled = new FeaturesEnabled();

    private boolean location_services_turned_on = false;

    private PopupHistoricalSetupModeViewer popup_historical_setup_mode_viewer;

    private PopupAnnotationViewer popup_annotation_viewer;

    private PopupServerSyncing popup_server_syncing;

    private PopupLifetouchPoincare popup_lifetouch_poincare;

    private PopupWifiStatus popup_wifi_status;

    private PopupDeveloperOptions popup_developer_options;

    private PopupPatientName popup_patient_name;

    private PopupRecyclingReminder popup_recycling_reminder;

    private PopupVideoCall popup_video_call;

    private PopupWebpage popup_webpage;

    private PopupBluetoothError popup_bluetooth_error;

    private int long_term_measurement_timeout = 60;

    private int historical_setup_viewer_view_port_width = 10 * (int)DateUtils.SECOND_IN_MILLIS;

    private ProgressWindow progressWindow;

    private long last_time_unplugged_overlay_shown = 0;
    private final long unplugged_overlay_timeout_after_pressing_screen = 10 * (int)DateUtils.MINUTE_IN_MILLIS;                                // Show it again after 10 mins if the user presses the screen to dismiss it

    private RelativeLayout unplugged_overlay;

    private void showUnpluggedOverlay(final boolean show)
    {
        Log.d(TAG, "showUnpluggedOverlay : " + show);

        if (permissions.haveOverlayPermission())
        {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            if (show)
            {
                if((main_activity_resumed == true) && (unplugged_overlay == null))
                {
                    ImageView unpluggedIcon = new ImageView(getApplicationContext());

                    Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.charger);
                    if (d != null)
                    {
                        unpluggedIcon.setImageDrawable(d);

                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        unpluggedIcon.setLayoutParams(layoutParams);

                        unplugged_overlay = new RelativeLayout(getApplicationContext());
                        unplugged_overlay.setBackgroundColor(0xA0FF0000);                           // The translucent red color
                        unplugged_overlay.addView(unpluggedIcon);

                        addOverlay(windowManager, unplugged_overlay);
                    }
                }
                else
                {
                    Log.w(TAG, "showUnpluggedOverlay: main activity not resumed or overlay already exists");
                }
            }
            else
            {
                if (unplugged_overlay != null) {
                    windowManager.removeView(unplugged_overlay);

                    unplugged_overlay = null;
                }
            }
        }
    }

    private void unpluggedOverlayTouchEvent()
    {
        showUnpluggedOverlay(false);
    }

    private boolean unpluggedOverlayTimeoutPassed(long time_now)
    {
        return time_now > (last_time_unplugged_overlay_shown + unplugged_overlay_timeout_after_pressing_screen);
    }

    FragmentScreenSaver fragmentScreenSaver = new FragmentScreenSaver();

    private void showScreenSaver(final boolean show)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (show)
        {
            transaction.replace(R.id.fragment_overlay, fragmentScreenSaver);
        }
        else
        {
            transaction.remove(fragmentScreenSaver);
        }
        transaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void showScreensaver()
    {
        Log.d(TAG, "showScreensaver");

        showScreenSaver(true);
    }

    public void screensaverDismissed()
    {
        Log.d(TAG, "screensaverDismissed");

        showScreenSaver(false);

        FragmentQrCodeUnlock fragment = (FragmentQrCodeUnlock) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            fragment.setupButtonTimeout();
            fragment.buttonScanQrCodeClicked();
        }
    }

    boolean enable_screensaver = true;

    public boolean isScreensaverEnabled()
    {
        return enable_screensaver;
    }


    public void toggleHideyBar()
    {
        int newUiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private Permissions permissions;

    final String android_device_manufacturer = Build.MANUFACTURER;
    final String android_device_model = Build.MODEL;

    public boolean isRaspberryPi()
    {
        return android_device_manufacturer.equals("brcm") && android_device_model.equals("Raspberry Pi 4");
    }

    public boolean haveCamera()
    {
        return !isRaspberryPi();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Log for debugging
        Log = new RemoteLogging();

        Log.d(TAG, "onCreate");

        if (BuildConfig.DEBUG == false)
        {
            // This sets a custom error activity class instead of the default one.
            CustomActivityOnCrash.setErrorActivityClass(CustomErrorActivity.class);

            // Install CustomActivityOnCrash
            CustomActivityOnCrash.install(this);

            Log.d(TAG, "Installed CustomActivityOnCrash");
        }
        else
        {
            Log.d(TAG, "NOT installed CustomActivityOnCrash as DEBUG build");
        }

        Log.d(TAG, "Manufacturer = " + android_device_manufacturer);
        Log.d(TAG, "Model = " + android_device_model);
        Log.d(TAG, "Is Raspberry PI = " + isRaspberryPi());

        NOT_SET_YET = getResources().getString(R.string.not_set_yet);

        patient_gateways_assigned_bed_id = NOT_SET_YET;
        patient_gateways_assigned_ward_name = NOT_SET_YET;
        patient_gateways_assigned_bed_name = NOT_SET_YET;

        // Remove Android title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove Android notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Keep screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// This has to be AFTER the requestWindowFeature code
        super.onCreate(savedInstanceState);

        // Load the Layout
        setContentView(R.layout.main);
        relativeLayoutMain = findViewById(R.id.relativeLayoutMain);

        permissions = new Permissions(this);

        // Set Screen Brightness mode to manual
        setScreenBrightnessModeToManual();

        //vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Init admin_exit_button_pressed variable
        admin_exit_button_pressed = false;

        if (permissions.haveWriteExternalStoragePermission())
        {
            if (bound_to_logcat_capture_service == false)
            {
                Log.d(TAG, "onCreate : Starting Portal Logcat Service");

                ComponentName service_name = startService(new Intent(getAppContext(), IsansysPortalLogcatCaptureService.class));

                IsansysPortalLogcatCaptureService.setHandler(mHandler);

                if (service_name != null)
                {
                    Log.d(TAG, "onCreate : Successfully started the Portal Logcat service. Component Name = " + service_name);
                }
                else
                {
                    Log.w(TAG, "onCreate : ALERT ALERT ALERT!!! Portal Logcat service isn't started. Component Name is null");
                }

                bound_to_logcat_capture_service = true;
            }
            else
            {
                Log.w(TAG, "onCreate : Starting Portal Logcat Service");
            }
        }
        else
        {
            Log.e(TAG, "onCreate : *******************************************************************************************");
            Log.e(TAG, "onCreate : Do not have Write Storage permission yet - so NOT started IsansysPortalLogcatCaptureService");
            Log.e(TAG, "onCreate : *******************************************************************************************");
        }

        logCatPageEnabled = false;

        portal_system_commands = new SystemCommands(this);

        progressWindow = ProgressWindow.getInstance(this);

        popup_historical_setup_mode_viewer = new PopupHistoricalSetupModeViewer(this);

        popup_annotation_viewer = new PopupAnnotationViewer(this);

        popup_server_syncing = new PopupServerSyncing(this);
        popup_server_syncing.setArguments(this, new PopupServerSyncing.Callback()
        {
            @Override
            public void touchEventFromPopupWindow()
            {
                Log.e(TAG, "Main Activity : touchEventFromPopupWindow Server Syncing");
                screen_dimming_countdown_timer.touchEvent();
            }

            @Override
            public void dismissButtonPressed()
            {
                Log.e(TAG, "Main Activity : dismissButtonPressed, Cancel Server Syncing Timer");

                stopPopupServerSyncingTimers();
            }

            @Override
            public void retryButtonPressed()
            {
                Log.e(TAG, "Main Activity : retryButtonPressed");

                retryServerSyncing();
            }
        });


        popup_patient_name = new PopupPatientName(this);
        popup_patient_name.setArguments(new PopupPatientName.Callback()
        {
            @Override
            public void touchEventFromPopupWindow()
            {
                Log.e(TAG, "Main Activity : touchEventFromPopupWindow Patient Name");
                screen_dimming_countdown_timer.touchEvent();
            }

            @Override
            public void correctPatientDetailsButtonPressed(FullPatientDetails fullPatientDetails)
            {
                Log.d(TAG, "Main Activity: correctPatientDetailsButtonPressed");

                if (current_page == UserInterfacePage.PATIENT_CASE_ID_ENTRY)
                {
                    FragmentPatientCaseIdEntry fragment = (FragmentPatientCaseIdEntry) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                    {
                        setHospitalPatientId(fragment.getEnteredText(), fullPatientDetails);

                        nextButtonPressed();
                    }
                }
            }

            @Override
            public void incorrectPatientDetailsButtonPressed()
            {
                Log.d(TAG, "Main Activity: incorrectPatientDetailsButtonPressed");
            }

            @Override
            public void usingCaseId()
            {
                Log.d(TAG, "Main Activity: usingCaseId");

                if (current_page == UserInterfacePage.PATIENT_CASE_ID_ENTRY)
                {
                    FragmentPatientCaseIdEntry fragment = (FragmentPatientCaseIdEntry) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                    {
                        setHospitalPatientId(fragment.getEnteredText());

                        nextButtonPressed();
                    }
                }
            }
        });

        popup_video_call = new PopupVideoCall(this);
        popup_video_call.setArguments(new PopupVideoCall.Callback()
        {
            @Override
            public void AcceptIncomingVideoCall()
            {
                Log.d(TAG, "popup_video_call Callback: AcceptIncomingVideoCall : " + videoCallStatus + " : " + current_page);

                joinVideoCall(videoCallDetails);
            }

            @Override
            public void DeclineIncomingVideoCall()
            {
                Log.d(TAG, "popup_video_call Callback: RejectIncomingVideoCall : " + videoCallStatus + " : " + current_page);

                setVideoCallStatusAndReportToServer(VideoCallStatus.VIDEO_CALL_STATUS__SERVER_CALLING_GATEWAY__GATEWAY_DECLINED);

                String existingConnectionId = videoCallDetails.connection_id;

                // Reset back to defaults
                videoCallDetails = new VideoCallDetails();

                // Set the Connection ID back. Only used by the next Report Video Status code to tell the Lifeguard that the status is now VIDEO_CALL_STATUS__NONE
                videoCallDetails.connection_id = existingConnectionId;
            }

            @Override
            public void MeetingFailed()
            {
                Log.d(TAG, "popup_video_call Callback: MeetingFailed : " + videoCallStatus + " : " + current_page);
//TODO
                handleVideoCallFailed();
            }

            @Override
            public void MeetingLeaveComplete()
            {
                String line = "popup_video_call Callback: MeetingLeaveComplete : " + videoCallStatus + " : " + current_page;

                // There appears to be a Zoom bug where ZoomMeetingLeaveComplete is fired even though the rest of the Zoom code thinks its trying to reconnect
                if (videoCallStatus == VideoCallStatus.VIDEO_CALL_STATUS__RECONNECTING)
                {
                    line = line + " : IGNORING";
                    Log.d(TAG, line);
                }
                else
                {
                    Log.d(TAG, line);

                    popup_video_call.dismissPopupIfVisible();
                }
            }

            @Override
            public void UserWantsToLeaveMeeting()
            {
                Log.d(TAG, "popup_video_call Callback: UserWantsToLeaveMeeting : " + videoCallStatus + " : " + current_page);

                leaveVideoCall();
            }

            @Override
            public void MissedCall()
            {
                Log.d(TAG, "popup_video_call Callback: MissedCall : " + videoCallStatus + " : " + current_page);

                setVideoCallStatusAndReportToServer(VideoCallStatus.VIDEO_CALL_STATUS__MISSED_CALL);
            }

            @Override
            public void PopupDismissed()
            {
                Log.d(TAG, "popup_video_call Callback: PopupDismissed : " + videoCallStatus + " : " + current_page);

                videoCallLeftSoGoBackToPreviousPage();

                setVideoCallStatusAndReportToServer(VideoCallStatus.VIDEO_CALL_STATUS__NONE);

                // Reenable the Screen Lock timer if needed
                stopScreenLockCountdownTimerAndRestartIfDesired();

                // Reenable the Screen Dimming timer
                startScreenDimmingCountdownTimer();
            }

            @Override
            public void ScreenTouched()
            {
                Log.d(TAG, "popup_video_call Callback: ScreenTouched : " + videoCallStatus + " : " + current_page);

                unpluggedOverlayTouchEvent();
            }

            @Override
            public void StartRingingAudio()
            {
                startRingingAudio();
            }

            @Override
            public void StopRingingAudio()
            {
                stopRingingAudio();
            }

            @Override
            public void SetupVolumeForIncomingCallRinging()
            {
                setupVolumeForIncomingCallRinging();
            }

            @Override
            public void SetupVolumeForOutgoingCallRinging()
            {
                setupVolumeForOutgoingCallRinging();
            }

            @Override
            public void SetupVolumeForActiveCall()
            {
                setupVolumeForActiveCall();
            }

            @Override
            public void CancelledOutgoingCallRequest()
            {
                Log.d(TAG, "CancelledOutgoingCallRequest");
                patientCancelledVideoCallRequest();
            }

            @Override
            public boolean ToggleAudioEnabled()
            {
                return toggleAudioEnabled();
            }

            @Override
            public boolean ToggleVideoEnabled()
            {
                return toggleVideoEnabled();
            }
        });

        popup_developer_options = new PopupDeveloperOptions(this);
        popup_developer_options.setArguments(new PopupDeveloperOptions.Callback()
        {
            @Override
            public void touchEventFromPopupWindow()
            {
                Log.d(TAG, "Main Activity : touchEventFromPopupWindow popup_developer_options");
                screen_dimming_countdown_timer.touchEvent();
            }

            @Override
            public void resetBluetoothAdaptor(boolean remove_devices)
            {
                Log.d(TAG, "Main Activity : touch event for resetBluetoothAdaptor. remove_devices = " + remove_devices);
                portal_system_commands.sendGatewayCommand_resetBluetooth(true, remove_devices);
            }

            @Override
            public void disableBluetoothAdapter()
            {
                Log.d(TAG, "Main Activity : disableBluetoothAdapter");
                portal_system_commands.sendGatewayCommand_disableBluetoothAdapter();
            }

            @Override
            public void enableBluetoothAdapter()
            {
                Log.d(TAG, "Main Activity : enableBluetoothAdapter");
                portal_system_commands.sendGatewayCommand_enableBluetoothAdapter();
            }

            @Override
            public void disableWifi()
            {
                Log.d(TAG, "Main Activity : disableWifi");
                portal_system_commands.sendGatewayCommand_disableWifi();
            }

            @Override
            public void enableWifi()
            {
                Log.d(TAG, "Main Activity : enableWifi");
                portal_system_commands.sendGatewayCommand_enableWifi();
            }

            @Override
            public void dismissButtonPressed()
            {
                Log.d(TAG, "Main Activity: dismissButtonPressed in Developer Popup");
            }

            @Override
            public void getSweetblueDiagnostics()
            {
                portal_system_commands.sendGatewayCommand_getSweetblueDiagnostics();
            }

            @Override
            public void resetDatabaseFailedToSendStatus()
            {
                portal_system_commands.sendGatewayCommand_resetDatabaseFailedToSendStatus();
            }

            @Override
            public void crashPatientGatewayOnDemand()
            {
                portal_system_commands.sendGatewayCommand_crashPatientGatewayOnDemand();
            }

            @SuppressWarnings({"divzero", "NumericOverflow"})
            @Override
            public void crashUserInterfaceOnDemand()
            {
                int crash_on_demand;
                crash_on_demand = 1/0;

                Log.e(TAG, "Will never get here but stops Android Lint complaining about unused var : " + crash_on_demand);
            }

            @Override
            public void startNoninBlePlaybackSimulationFromFile()
            {
                portal_system_commands.sendGatewayCommand_startNoninBlePlaybackSimulationFromFile();
            }

            @Override
            public void enableServerSyncingTestMode()
            {
                portal_system_commands.sendGatewayCommand_serverSyncingTestMode(true);
            }

            @Override
            public void disableServerSyncingTestMode()
            {
                portal_system_commands.sendGatewayCommand_serverSyncingTestMode(false);
            }
        });

        popup_webpage = new PopupWebpage(this);
        popup_webpage.setArguments(new PopupWebpage.Callback()
        {
            @Override
            public void ScreenTouched()
            {
                Log.d(TAG, "Main Activity : ScreenTouched popup_webpage");
                screen_dimming_countdown_timer.touchEvent();
            }

            @Override
            public void PopupDismissed()
            {

            }
        });

        popup_wifi_status = new PopupWifiStatus(this);
        popup_wifi_status.setArguments(new PopupWifiStatus.Callback()
        {
            @Override
            public void touchEventFromPopupWindow()
            {
                Log.d(TAG, "Main Activity : touchEventFromPopupWindow Wifi Status");
                screen_dimming_countdown_timer.touchEvent();
            }

            @Override
            public void reconnectWifi()
            {
                Log.d(TAG, "Main Activity : touch event for wifi reconnection");
                sendWifiReconnectCommand();

                // Restart the countDown counter
                stopWifiStatus_popupCountDownTimer();
                startWifiStatus_popupCountDownTimer();
            }

            @Override
            public void dismissButtonPressed()
            {
                Log.d(TAG, "Main Activity: dismissButtonPressed in wifi status popup");
                stopWifiStatus_popupCountDownTimer();
            }
        });


        popup_recycling_reminder = new PopupRecyclingReminder(this);
        popup_recycling_reminder.setArguments(new PopupRecyclingReminder.Callback()
        {
            @Override
            public void touchEventFromPopupWindow()
            {
                Log.d(TAG, "Main Activity : touchEventFromPopupWindow Recycling Reminder");
                screen_dimming_countdown_timer.touchEvent();
            }

            @Override
            public void dismissButtonPressed()
            {
                Log.d(TAG, "Main Activity: dismissButtonPressed in Recycling Reminder");
            }
        });

        popup_bluetooth_error = new PopupBluetoothError(this);
        popup_bluetooth_error.setArguments(new PopupBluetoothError.Callback()
        {
            @Override
            public void touchEventFromPopupWindow()
            {
                Log.d(TAG, "Main Activity : touchEventFromPopupWindow Bluetooth Error");
                screen_dimming_countdown_timer.touchEvent();
            }

            @Override
            public void dismissButtonPressed()
            {
                Log.d(TAG, "Main Activity: dismissButtonPressed in Bluetooth Error popup");
            }
        });

        resetCachedDeviceInfoList();

        saved_measurements = new SavedMeasurements(Log);

        // Setup the Header/Footer and QR Code Unlock fragments.
        header_fragment = new FragmentHeader();
        footer_fragment = new FragmentFooter();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_header, header_fragment).commitNow();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_footer, footer_fragment).commitNow();

        handler_gateway_not_responding = new Handler();

        IntentFilter event_filter = new IntentFilter();
        event_filter.addAction(Intent.ACTION_SHUTDOWN);

        event_filter.addAction(Intent.ACTION_SCREEN_OFF);
        event_filter.addAction(Intent.ACTION_SCREEN_ON);

        event_filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        event_filter.addAction(Intent.ACTION_POWER_CONNECTED);

        event_filter.addAction(Intent.ACTION_BATTERY_OKAY);
        event_filter.addAction(Intent.ACTION_BATTERY_LOW);

        registerReceiver(broadcastReceiverAndroidEvents, event_filter);

        app_versions = new AppVersions(this);

        Log.d(TAG, "onCreate : Software version = " + app_versions.getUserInterfaceVersionNumber());

        // Tell the Gateway that the UI has (re)booted
        portal_system_commands.sendGatewayCommand_tellGatewayThatUiHasBooted(getNtpTimeNowInMilliseconds());

        vital_signs_async_query = new AsyncDatabaseQuery(Log, getContentResolver());

        scale_gesture_detector = new ScaleGestureDetector(getAppContext(), scale_gesture_listener);
        gesture_detector = new GestureDetector(getAppContext(), gesture_listener);

        // Get the Early Warning Scoring sets from the Gateway
        getEarlyWarningScoringSetsFromPatientGatewayDatabase();


        // Init Video Conference code
        videoCallDetails = new VideoCallDetails();
        videoCallStatus = VideoCallStatus.VIDEO_CALL_STATUS__INVALID;

        mediaPlayer = MediaPlayer.create(getAppContext(), R.raw.phone_ringing);
        mediaPlayer.setLooping(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager.isNotificationPolicyAccessGranted())
        {
            // Set Android volume to 100%
            setAndroidVolumeToFixedPercentage(1.0f);
        }
        else
        {
            // ACCESS_NOTIFICATION_POLICY has not been granted yet. However once it is granted, the UI restarts so will be fine next time around
        }
    }


    // Do short beep sound, e.g. after QR Bar code completion
    public void beep()
    {
        (new ToneGenerator(AudioManager.STREAM_SYSTEM, 100)).startTone(ToneGenerator.TONE_PROP_BEEP);
    }


    public void startRingingAudio()
    {
        stopRingingAudio();

        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    public void stopRingingAudio()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
    }

    public boolean isScreenLandscape()
    {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }


    private final BroadcastReceiver broadcastReceiverAndroidEvents = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            switch (action)
            {
                case Intent.ACTION_SHUTDOWN:
                    Log.e(TAG, "ACTION_SHUTDOWN");
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
                    Log.e(TAG, "ACTION_POWER_CONNECTED");
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


    // Called if the Home Button is pressed during development or Gateway Installation.
    @Override
    protected void onUserLeaveHint()
    {
        super.onUserLeaveHint();

        // Remove the Overlay from the screen
        unpluggedOverlayTouchEvent();
    }


    @Override
    public void onResume()
    {
        // Log for debugging
        Log.d(TAG, "onResume");

        Log.d(TAG, "Android Language = " + Locale.getDefault());

        // Make sure the screen is default size. On Android 9 all of the font sizes are too large by default.
        setFontScaleIfHaveWritePermissions();

        // Hide the onscreen button bar on Android 9
        //toggleHideyBar();

        // Setup the User Interface Timer. This runs UI events every second
        checkAndCancel_timer(user_interface_timer);
        user_interface_timer = new Timer();
        user_interface_timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                onUserInterfaceTimerTick();
            }
        }, 0, (int)DateUtils.SECOND_IN_MILLIS);

        // Setup the User Interface Screen Lock timer.
        // If after X seconds no button has been pressed, it auto switches to the Lock Screen
        stopScreenLockCountdownTimerAndRestartIfDesired();

        setScreenBrightnessFromFooter(getStartupScreenBrightness());

        // Setup the User Inactivity Timer. If after X seconds no button has been pressed, then dim the screen to save power
        startScreenDimmingCountdownTimer();

        // Check to see if the app has the correct permissions. If not show the Permissions screen - NOT the Unlock screen as the Camera permission
        // might not be granted yet
        boolean forceShowAndroidPermissions;
        if(!permissions.checkForRequiredAndroidPermissions())
        {
            Log.e(TAG, "Need to setup permissions");
            forceShowAndroidPermissions = true;
        }
        else
        {
            Log.d(TAG, "All permissions granted");
            forceShowAndroidPermissions = false;
        }

        // Setup the Intent Filters
        if(software_update_mode_active && !forceShowAndroidPermissions)
        {
            Log.d(TAG, "Update Mode : Registered updateMode receiver");

            // This only handles a subset of the UI commands - only those that are needed to make Update Mode work
            // Reason for this is that the other commands MAY change during the update. So dont want the Gateway sending Command X which
            // which the (now old) UI processes differently to how the (new) Gateway was expecting
            registerReceiver(broadcastReceiverIncomingCommandsFromPatientGateway__updateMode, new IntentFilter(INTENT__COMMANDS_TO_USER_INTERFACE));

            if(footer_fragment != null)
            {
                Log.d(TAG, "Update Mode : Hiding footer controls as in update mode");
                footer_fragment.hideControlsDueToUpdateMode(true);
            }
        }
        else
        {
            Log.d(TAG, "Update Mode : Registered normal receiver");

            registerReceiver(broadcastReceiverIncomingCommandsFromPatientGateway, new IntentFilter(INTENT__COMMANDS_TO_USER_INTERFACE));

            if(footer_fragment != null)
            {
                Log.d(TAG, "Update Mode : Showing footer controls as NOT in update mode");
                footer_fragment.hideControlsDueToUpdateMode(false);
            }
        }

        // Set patient_session_number to invalid to force a refresh of on-screen data when the gateway reports session numbers.
        patient_session_number = INVALID_PATIENT_SESSION;

        // Get the gateway status info
        getCurrentStatusFromGateway();

        // Get session start time (if set)
        portal_system_commands.sendGatewayCommand_getPatientStartSessionTime();

        // Get the wifi status from the Gateway
        sendCommandToGetWifiStatus();

        portal_system_commands.sendGatewayCommand_getLocationEnabled();

        queryServerConfigurableText();

        queryWardsAndBeds();

        queryWebPages();

        portal_system_commands.sendGatewayCommand_getDisplayTimeoutInSeconds();

        Log.d(TAG, "onResume : current_page = " + current_page);

        if (isVideoCallPopupShowing())
        {
            Log.d(TAG, "onResume : Video Call popup onscreen");

            current_page = UserInterfacePage.EMPTY;
        }

        if (current_page == UserInterfacePage.DEVICE_CONNECTION)
        {
            // Make the Gateway send the sendCommandEndOfDeviceConnection if all the devices are connected
            // Means if the UI is resumed on the Device Connection page, then the Start Monitoring button reappears
            portal_system_commands.sendGatewayCommand_refreshDeviceConnectionState();

            // Pairing Popups will trigger this onResume when they dismiss
            // So trigger reload of scan progress indicators, as we're continuing the existing scan
            showDeviceConnectionFragment_ResumeCurrentScan();
        }
        else if((current_page == UserInterfacePage.ANDROID_PERMISSIONS) || forceShowAndroidPermissions)
        {
            showAndroidPermissionsFragment();
        }
        else if(current_page == UserInterfacePage.SOFTWARE_UPDATE_IN_PROGRESS)
        {
            showSoftwareUpdateMode();
        }
        else if(current_page == UserInterfacePage.EMPTY)
        {
            emptyFragmentSelected();
        }
        else
        {
            lockScreenSelected();
        }

        super.onResume();

        main_activity_resumed = true;
    }


    private void setFontScaleIfHaveWritePermissions()
    {
        if (permissions.haveWriteSettingsPermission())
        {
            Settings.System.putFloat(getBaseContext().getContentResolver(), Settings.System.FONT_SCALE, (float) 1);
        }
    }


    private void getCurrentStatusFromGateway()
    {
        // Send "Ping" command to the Patient Gateway to check its running
        sendPingCommandToPatientGateway();

        // Get Gateway Status
        getServerAddress();
        getGatewaysAssignedBedDetails();
        getServerSyncEnableStatus();
        getRealTimeLinkEnableStatus();
        portal_system_commands.sendGatewayCommand_getRealTimeServerConnectedStatus();
        portal_system_commands.sendGatewayCommand_getRunDevicesInTestMode();

        getSetupModeLengthInSeconds();
        getDevicePeriodicModePeriodTimeInSeconds();
        getDevicePeriodicModeActiveTimeInSeconds();

        // Is UI showing DEVICE_CONNECTION page
        if (current_page != UserInterfacePage.DEVICE_CONNECTION)
        {
            // UI sends these at Startup to check if a session is already in progress
            // UI might have crashed or being reloaded during development
            portal_system_commands.sendGatewayCommand_getGatewaySessionNumbersCommand();
            portal_system_commands.sendGatewayCommand_getHospitalPatientIdCommand();
            portal_system_commands.sendGatewayCommand_getPatientThresholdSetCommand();

            // Ask the Gateway for any info about devices.
            // UI might have crashed or being reloaded during development
            getAllDeviceInfosFromGateway();
        }

        portal_system_commands.sendGatewayCommand_getManualVitalSignsEnableStatus();
        portal_system_commands.sendGatewayCommand_getManufacturingModeEnabledStatus();
        portal_system_commands.sendGatewayCommand_getCsvOutputEnableStatus();
        getBloodPressureLongTermMeasurementTimeout();
        getSpO2LongTermMeasurementTimeout();
        getWeightLongTermMeasurementTimeout();
        getThirdPartyTemperatureLongTermMeasurementTimeout();
        portal_system_commands.sendGatewayCommand_getUseBackCameraEnabledStatus();
        portal_system_commands.sendGatewayCommand_getDisplayPatientOrientationEnabledStatus();
        portal_system_commands.sendGatewayCommand_getShowNumbersOnBatteryIndicatorEnabledStatus();
        portal_system_commands.sendGatewayCommand_getShowMacAddressEnabledStatus();
        portal_system_commands.sendGatewayCommand_getUsaModeEnabledStatus();
        portal_system_commands.sendGatewayCommand_getShowLifetouchActivityEnabledStatus();
        portal_system_commands.sendGatewayCommand_getDeveloperPopupEnabled();
        portal_system_commands.sendGatewayCommand_getShowIpAddressOnWifiPopupEnabled();
        getSensorOperatingMode(SensorType.SENSOR_TYPE__LIFETOUCH);
        getSensorOperatingMode(SensorType.SENSOR_TYPE__SPO2);

        // Get the Gateways time offset to NTP time so can adjust the UI's time
        portal_system_commands.sendGatewayCommand_getNtpClockOffsetInMs();

        portal_system_commands.sendGatewayCommand_getLifetempMeasurementInterval();
        portal_system_commands.sendGatewayCommand_getPatientOrientationMeasurementInterval();
        portal_system_commands.sendGatewayCommand_getAutoResumeEnabledStatus();
        portal_system_commands.sendGatewayCommand_getAutoUploadLogFilesToServerEnabledStatus();
        portal_system_commands.sendGatewayCommand_getWifiLoggingEnabledStatus();
        portal_system_commands.sendGatewayCommand_getGsmLoggingEnabledStatus();
        portal_system_commands.sendGatewayCommand_getDatabaseLoggingEnabledStatus();
        portal_system_commands.sendGatewayCommand_getServerLoggingEnabledStatus();
        portal_system_commands.sendGatewayCommand_getBatteryLoggingEnabledStatus();
        getDfuBootloaderEnableStatus();
        getPredefinedAnnotationEnableStatus();
        getDisplayTemperatureInFahrenheitEnableStatus();
        getDisplayWeightInLbsEnableStatus();
        portal_system_commands.sendGatewayCommand_getInstallationComplete();
        portal_system_commands.sendGatewayCommand_getRealtimeServerType();

        getPatientNameLookupEnabled();
        getSpO2SpotMeasurementsEnableStatus();

        getGsmModeOnlyEnabled();

        portal_system_commands.sendGatewayCommand_getVideoCallsEnabledStatus();

        portal_system_commands.sendGatewayCommand_getViewWebPagesEnabledStatus();

        portal_system_commands.sendGatewayCommand_getSoftwareUpdateModeState();

        getUnpluggedOverlayEnabledStatusFromPatientGateway();

        getAutoAddEarlyWarningScoreEnableStatusFromPatientGateway();

        if (!getGsmOnlyModeFeatureEnabled())
        {
            sendCommandToGetWifiStatus();
        }

        getDiskFreePercentage();
    }


    @Override
    public void onRestart()
    {
        // Stop the Back Button doing anything
        Log.d(TAG, "onRestart");

        super.onRestart();
    }


    @Override
    public void onStart()
    {
        super.onStart();

        Log.d(TAG, "onStart");
    }


    @Override
    public void onBackPressed()
    {
        /* Each time the Back button is pressed you get the following in the logs
            D/ViewRootImpl: ViewPostImeInputStage processKey 0
            D/ViewRootImpl: ViewPostImeInputStage processKey 1
        */

        // Stop the Back Button doing anything
        Log.d(TAG, "onBackPressed - Ignored");
    }


    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause");
        main_activity_resumed = false;
        super.onPause();

        hideProgress();
        showUnpluggedOverlay(false);
        showNightModeOverlay(false);

        closeAllPopups();

        logTotalMemory();
    }


    private void closePopup(IsansysPopupDialogFragment popup_fragment)
    {
        if (popup_fragment != null)
        {
            if(popup_fragment.getDialog() != null)
            {
                if (popup_fragment.getDialog().isShowing())
                {
                    popup_fragment.dismiss();
                }
            }
        }
    }


    @Override
    protected void onStop()
    {
        try
        {
            Log.d(TAG, "onStop : unregisterReceiver");

            // If for some reason this receiver hasn't been registered properly, it will throw an IllegalArgumentException
            unregisterReceiver(broadcastReceiverIncomingCommandsFromPatientGateway);

            unregisterReceiver(broadcastReceiverAndroidEvents);
        }
        catch (IllegalArgumentException e)
        {
            Log.e(TAG, e.getMessage());
        }

        checkAndCancel_timer(user_interface_timer);

        // Cancel the countdown timers
        stopScreenLockCountdownTimer();
        stopScreenDimmingCountdownTimer();

        Log.d(TAG, "onStop");

        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy : isFinishing = " + isFinishing());

        super.onDestroy();

        try
        {
            Log.d(TAG, "onDestroy : unregisterReceiver");

            // If for some reason this receiver hasn't been registered properly, it will throw an IllegalArgumentException
            unregisterReceiver(broadcastReceiverIncomingCommandsFromPatientGateway);

            unregisterReceiver(broadcastReceiverAndroidEvents);
        }
        catch (IllegalArgumentException e)
        {
            Log.e(TAG, e.getMessage());
        }

        if(admin_exit_button_pressed)
        {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 80);

            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);

            Log.d(TAG, "onDestroy kill process by PID = " + Process.myPid());

            Process.killProcess(Process.myPid());
        }
    }

    private int user_interface_timer_tick_counter = 0;

    private void onUserInterfaceTimerTick()
    {
        sendPingCommandToPatientGateway();

        user_interface_timer_tick_counter++;
        if (user_interface_timer_tick_counter == 10)
        {
            user_interface_timer_tick_counter = 0;

            logProcessHeapSize();

            logTotalMemory();
        }
    }


    public void onQrCodeDetected(String source, String qr_code)
    {
        switch (source)
        {
            case "FragmentAddDevices":
            case "FragmentQrCodeUnlock":
            case "FragmentCheckDeviceStatus":
            case "FragmentCheckPackaging":
                portal_system_commands.sendGatewayCommand_validateQrCode(qr_code);
                break;

            case "FragmentInstallationServerAddressScan":
            {
                portal_system_commands.sendGatewayCommand_validateInstallationQrCode(qr_code);

                // Also allow Admin QR codes here to get directly to the Admin page without having to go via the Install Wizard
                portal_system_commands.sendGatewayCommand_validateQrCode(qr_code);
            }
            break;

            default:
                Log.d(TAG, "onQrCodeDetected : Invalid QR code not processed");
                break;
        }
    }

    public void onDataMatrixDetected(String source, String contents)
    {
        switch (source)
        {
            case "FragmentAddDevices":
            case "FragmentCheckDeviceStatus":
                portal_system_commands.sendGatewayCommand_validateDataMatrix(contents);
                break;

            default:
                Log.d(TAG, "onDataMatrixDetected : Ignored as from invalid source");
                break;
        }
    }

    private void startUserInterfaceTimeoutAndEnterAdminMode()
    {
        adminModeSelected();
    }


    private void startUserInterfaceTimeoutAndEnterFeatureEnableMode()
    {
        featureEnableModeSelected();
    }


    public void sendStartSetupModeCommand(DeviceInfo device_info)
    {
        portal_system_commands.sendGatewayCommand_sendStartSetupModeCommand(device_info.device_type);
    }


    public void sendStopSetupModeCommand(DeviceInfo device_info)
    {
        portal_system_commands.sendGatewayCommand_sendStopSetupModeCommand(device_info.device_type);
    }


    private AsyncDatabaseQuery vital_signs_async_query;


    private static final String INTENT__COMMANDS_TO_USER_INTERFACE = "com.isansys.patientgateway.commands_to_user_interface";


    private final ArrayList<DeviceInfo> cached_device_info_list = new ArrayList<>();


    // Used as part of Patient Gateway ping to check if the PG is responding
    private Handler handler_gateway_not_responding;

    private long patient_gateway_not_responding_counter = 0;
    private static final long SHOW_GATEWAY_NOT_RESPONDING_AFTER_TIME = 30;

    private void sendPingCommandToPatientGateway()
    {
        Log.i(TAG, "Sending Ping from UI at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(System.currentTimeMillis()) + " : Current page = " + current_page);

        portal_system_commands.sendGatewayCommand_sendGatewayPing();

        Runnable runnable = () -> {
            patient_gateway_not_responding_counter++;

            Log.e(TAG, "Patient Gateway not responding : patient_gateway_not_responding_counter = " + patient_gateway_not_responding_counter);

            if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
            {
                footer_fragment.showSystemStatusStatus(false);
            }
            else
            {
                Log.e(TAG, "sendPingCommandToPatientGateway : footer_fragment = null");
            }

            if(patient_gateway_not_responding_counter >= SHOW_GATEWAY_NOT_RESPONDING_AFTER_TIME)
            {
                if(current_page != UserInterfacePage.GATEWAY_NOT_RESPONDING)
                {
                    gatewayNotRespondingSelected();
                }
            }
        };

        // this handler is removed in CMD_CHECK_SERVER_UI_CONNECTION intent received
        handler_gateway_not_responding.postDelayed(runnable, (int)DateUtils.SECOND_IN_MILLIS);
    }

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


    private final BroadcastReceiver broadcastReceiverIncomingCommandsFromPatientGateway = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int command = intent.getIntExtra("command", 0);

            try
            {
                Commands incoming_command = Commands.values()[command];

                switch (incoming_command)
                {
                    case CMD_REPORT_GATEWAY_SESSION_NUMBERS:
                    {
                        int new_patient_session_number = intent.getIntExtra("patient_session_number", INVALID_PATIENT_SESSION);
    
                        device_sessions = intent.getParcelableArrayListExtra("device_session_list");
    
                        Log.d(TAG, "CMD_REPORT_GATEWAY_SESSION_NUMBERS : new_patient_session_number = " + new_patient_session_number);
                        Log.d(TAG, "CMD_REPORT_GATEWAY_SESSION_NUMBERS : Patient Session Number = " + patient_session_number);
    
                        for (DeviceSession device_session : device_sessions)
                        {
                            Log.d(TAG, "CMD_REPORT_GATEWAY_SESSION_NUMBERS : " + device_session.sensor_type + " : " + device_session.device_type + " = " + device_session.local_device_session_id);
                        }
    
                        if(patient_session_number != new_patient_session_number)
                        {
                            // new session started or old one finished or recent data reload required after UI crash
                            patient_session_number = new_patient_session_number;
    
                            emptyGraphCaches();
    
                            if(patient_session_number == INVALID_PATIENT_SESSION)
                            {
                                // Old session has been ended, so reset device info's
                                resetCachedSessionInfo();
                            }
                            else
                            {
                                // New session started or recent data required to be reloaded after UI crash
                                is_session_in_progress = true;
    
                                // Query the database for the latest data
                                getMostRecentDataFromPatientGatewayDatabase(measurement_cache.max_data_points);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_PATIENT_THRESHOLD_SET:
                    {
                        if(isSessionInProgress())
                        {
                            int servers_threshold_set_id = intent.getIntExtra("servers_threshold_set_id", 0);
                            int servers_threshold_set_age_block_details_id = intent.getIntExtra("servers_threshold_set_age_block_details_id", 0);
    
                            ThresholdSet thresholdSet = getThresholdSetFromServersThresholdSetId(servers_threshold_set_id);
                            ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = getThresholdAgeBlockFromServersThresholdAgeBlockId(servers_threshold_set_age_block_details_id);
    
                            setAgeRange(thresholdSet, thresholdSetAgeBlockDetail);
    
                            createManualVitalSignInfos();
                        }
                        else
                        {
                            // Session hasn't started and on refresh has happened in UI. OnRefresh happens when connecting Pulse-ox. This will reset the Age_Rage and patient hospital ID
                            // So if session is not running than don't reset the patient_info
                        }
                    }
                    break;
    
                    case CMD_REPORT_HOSPITAL_PATIENT_ID:
                    {
                        if(isSessionInProgress())
                        {
                            setHospitalPatientId(intent.getStringExtra("hospital_patient_id"));
                        }
                        else
                        {
                            // Session hasn't started and on refresh has happened in UI. OnRefresh happens when connecting Pulse-ox. This will reset the Age_Rage and patient hospital ID
                            // So if session is not running than don't reset the patient_info
                        }
                    }
                    break;
    
                    case CMD_UPDATE_USER_INTERFACE_BLUETOOTH_STATUS:
                    {
                        int patient_gateway_status_as_int = intent.getIntExtra("patient_gateway_status", 0);
                        BluetoothStatus bluetooth_status = BluetoothStatus.values()[patient_gateway_status_as_int];
    
                        SensorType sensor_type = getSensorTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(sensor_type);
    
                        Log.d(TAG, "CMD_UPDATE_USER_INTERFACE_BLUETOOTH_STATUS : " + device_info.getSensorTypeAndDeviceTypeAsString() + " is in state " + bluetooth_status);
    
                        switch (bluetooth_status)
                        {
                            case BLUETOOTH_SCAN_STARTED:
                            {
                                if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                                {
                                    FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                    {
                                        fragment.showDeviceScanStarted(device_info);
                                    }
                                }
                            }
                            break;
    
                            case BLUETOOTH_SCAN_FINISHED__DEVICE_NOT_FOUND:
                            {
                                if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                                {
                                    FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                    {
                                        fragment.showDeviceNotFound(device_info);
                                    }
                                }
                            }
                            break;
    
                            case BLUETOOTH_DEVICE_FOUND:
                            {
                                if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                                {
                                    FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                    {
                                        fragment.showDeviceFound(device_info);
                                    }
                                }
                            }
                            break;
    
                            case BLUETOOTH_DEVICE_PAIRING:
                            {
                                if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                                {
                                    FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                    {
                                        fragment.showDevicePairing(device_info);
                                    }
                                }
                            }
                            break;
    
                            case BLUETOOTH_DEVICE_PAIRING_FAILED:
                            {
                                if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                                {
                                    FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                    {
                                        fragment.showDevicePairingFailed(device_info);
                                    }
                                }
                            }
                            break;
    
                            case BLUETOOTH_DEVICE_PAIRED__CONNECTING:
                            {
                                if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                                {
                                    FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                    {
                                        fragment.showDevicePaired(device_info);
                                    }
                                }
                            }
                            break;
    
                            case BLUETOOTH_DEVICE_CONNECTED:
                            {
                                if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                                {
                                    FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                    {
                                        fragment.showDeviceConnectedAndHideDeviceCancelSearchOrSearchAgainButton(device_info);
                                    }
                                }
                            }
                            break;
    
                            case BLUETOOTH_DEVICE_TEMPORARILY_DISCONNECTED:
                            {
                                // Only sent by Bluetooth Low Energy devices
    
                                if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                                {
                                    if(isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO())
                                    {
                                        Log.w(TAG, "BLUETOOTH_DEVICE_TEMPORARILY_DISCONNECTED : this cant be executed as number of connected device is ZERO");
                                    }
                                    else if(device_info.isDeviceHumanReadableDeviceIdValid() == false)
                                    {
                                        Log.w(TAG, "BLUETOOTH_DEVICE_TEMPORARILY_DISCONNECTED : this cant be executed as Device ID not valid");
                                    }
                                    else
                                    {
                                        FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                        {
                                            if (device_info.isDeviceTypeABtleSensorDevice())
                                            {
                                                fragment.deviceStateChange(device_info);
    
                                                Log.d(TAG, "BLUETOOTH_DEVICE_TEMPORARILY_DISCONNECTED : " + device_info.getSensorTypeAndDeviceTypeAsString() + " temporary Disconnect.");
                                            }
                                        }
                                    }
                                }
                            }
                            break;
    
                            case BLUETOOTH_DEVICE_DISCONNECTED:
                            {
                                if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                                {
                                    patientVitalsDisplayDeviceStateChangeOfDevicesInSession(getDeviceByType(sensor_type));
                                }
                            }
                            break;
                        }
                    }
                    break;
    
                    case CMD_REPORT_BLUETOOTH_SCAN_PROGRESS:
                    {
                        int bluetooth_scan_progress_percentage = intent.getIntExtra("bluetooth_scan_progress", 50);
    
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        SensorType sensor_type = getSensorTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(sensor_type);
    
                        Log.d(TAG, "CMD_REPORT_BLUETOOTH_SCAN_PROGRESS : " + device_info.getSensorTypeAndDeviceTypeAsString() + " : " + bluetooth_scan_progress_percentage + "%");
    
                        if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                        {
                            // This is going to take a while to scan so keep on resetting the screen lock timer
                            stopScreenLockCountdownTimerAndRestartIfDesired();
    
                            FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDeviceProgressBarValue(device_info, bluetooth_scan_progress_percentage);
                            }
                        }
                    }
                    break;
    
                    case CMD_CHECK_GATEWAY_UI_CONNECTION:
                    {
                        // Remove the "timeout" runnable now the Patient Gateway has respond
                        handler_gateway_not_responding.removeCallbacksAndMessages(null);
    
                        // Reset the timeout counter
                        patient_gateway_not_responding_counter = 0;
    
                        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                        {
                            footer_fragment.showSystemStatusStatus(true);
                        }
                        else
                        {
                            Log.e(TAG, "CMD_CHECK_GATEWAY_UI_CONNECTION : footer_fragment = null" );
                        }

                        if(current_page == UserInterfacePage.GATEWAY_NOT_RESPONDING)
                        {
                            lockScreenSelected();
                        }
                    }
                    break;
    
                    case CMD_SEND_NEW_SETUP_MODE_DATA_TO_UI:
                    {
                        DeviceInfo device_into = getDeviceByType(getDeviceTypeFromIntent(intent));
    
                        int setup_mode_sample = intent.getIntExtra("setup_mode_sample", 0);
                        long timestamp_in_ms = intent.getLongExtra("timestamp_in_ms", 0);
    
                        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            if(isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO())
                            {
                                Log.w(TAG, "CMD_SEND_NEW_SETUP_MODE_DATA_TO_UI : this cant be executed as number of connected device is ZERO");
                            }
                            else
                            {
                                FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.newSetupModeSample(device_into, setup_mode_sample, timestamp_in_ms);
                                }
                            }
                        }
                    }
                    break;
    
                    case CMD_SEND_NEW_RAW_ACCELEROMETER_MODE_DATA_TO_UI:
                    {
                        short[] x_axis_raw_accelerometer_mode_data = intent.getShortArrayExtra("x_axis_raw_accelerometer_mode_data");
                        short[] y_axis_raw_accelerometer_mode_data = intent.getShortArrayExtra("y_axis_raw_accelerometer_mode_data");
                        short[] z_axis_raw_accelerometer_mode_data = intent.getShortArrayExtra("z_axis_raw_accelerometer_mode_data");
    
                        int timestamp = 0;                                                  // Dummy. In future this should be calculated. This way the fragment can know to put gaps on the screen
    
                        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            if(isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO())
                            {
                                Log.w(TAG, "CMD_SEND_NEW_RAW_ACCELEROMETER_MODE_DATA_TO_UI : this cant be executed as number of connected device is ZERO");
                            }
                            else
                            {
                                FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    int number_of_samples = x_axis_raw_accelerometer_mode_data.length;
    
                                    for(int i=0; i<number_of_samples; i++)
                                    {
                                        short x = x_axis_raw_accelerometer_mode_data[i];
                                        short y = y_axis_raw_accelerometer_mode_data[i];
                                        short z = z_axis_raw_accelerometer_mode_data[i];
    
                                        fragment.newLifetouchRawAccelerometerSamples(x, y, z, timestamp);
                                    }
                                }
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_GATEWAYS_ASSIGNED_BED_DETAILS:
                    {
                        Log.d(TAG, "CMD_REPORT_GATEWAYS_ASSIGNED_BED_ID ");
    
                        patient_gateways_assigned_bed_id = intent.getStringExtra("gateways_assigned_bed_id");
                        patient_gateways_assigned_ward_name = intent.getStringExtra("gateways_assigned_ward_name");
                        patient_gateways_assigned_bed_name = intent.getStringExtra("gateways_assigned_bed_name");
    
                        // Store in Shared Preferences. ONLY needed for CustomErrorActivity
                        storeGatewayAssignedBedDetails(patient_gateways_assigned_bed_id, patient_gateways_assigned_ward_name, patient_gateways_assigned_bed_name);
    
                        Log.e(TAG, "patient_gateways_assigned_bed_id = " + patient_gateways_assigned_bed_id);
                        Log.e(TAG, "patient_gateways_assigned_ward_name = " + patient_gateways_assigned_ward_name);
                        Log.e(TAG, "patient_gateways_assigned_bed_name = " + patient_gateways_assigned_bed_name);
    
                        if (UtilityFunctions.isFragmentAddedAndResumed(header_fragment))
                        {
                            header_fragment.setBedDetailsText(patient_gateways_assigned_ward_name, patient_gateways_assigned_bed_name);
                        }
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showGatewaysAssignedBedId(patient_gateways_assigned_bed_id);
                            }
                        }
    
                        showForceInstallationCompleteButtonOnAdminScreen();
                    }
                    break;
    
                    case CMD_REPORT_SERVER_ADDRESS:
                    {
                        String server_address = intent.getStringExtra("server_address");
                        Log.d(TAG, "CMD_REPORT_SERVER_ADDRESS = " + server_address);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setServerAddress(server_address);
                            }
                        }
                        else if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                        {
                            FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setServerAddress(server_address);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SERVER_PING_RESULT:
                    {
                        boolean server_ping_result = intent.getBooleanExtra("server_ping_result", false);
                        boolean authentication_ok = intent.getBooleanExtra("authentication_ok", false);
    
                        Log.d(TAG, "CMD_REPORT_SERVER_PING_RESULT = " + server_ping_result);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showServerPingResult(server_ping_result, authentication_ok);
                            }
                        }
                        else if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                        {
                            FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showServerPingResult(server_ping_result, authentication_ok);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_INVALID_SERVER_STATUS_CODE:
                    {
                        server_status_code = intent.getIntExtra("server_status_code", 0);
                        String server_message = intent.getStringExtra("server_message");
    
                        Log.e(TAG, "ALERT ALERT!!!!!!!!!!!!!!!   CMD_REPORT_INVALID_SERVER_STATUS_CODE.  Status Code = " + server_status_code + ".  Server Message :-"+ server_message);
    
                        //footer_fragment.showServerWebserviceResult_invalid_server_status(server_status_code);
                    }
                    break;
    
                    case CMD_REPORT_SERVER_RESPONSE:
                    {
                        boolean server_webservice_result = intent.getBooleanExtra("server_webservice_result", false);
    
                        int http_operation_type_as_int = intent.getIntExtra("http_operation_type_as_int", HttpOperationType.INVALID.ordinal());
                        HttpOperationType http_operation_type = HttpOperationType.values()[http_operation_type_as_int];
    
                        int active_or_old_session_as_int = intent.getIntExtra("active_or_old_session_as_int", ActiveOrOldSession.INVALID.ordinal());
                        ActiveOrOldSession active_or_old_session = ActiveOrOldSession.values()[active_or_old_session_as_int];
    
                        if(popup_server_syncing.getDialog() != null)
                        {
                            if(popup_server_syncing.getDialog().isShowing())
                            {
                                updatePopupServerSyncRowsPending(intent);
                                popup_server_syncing.updatePopupServerWebserviceResult(http_operation_type, active_or_old_session, server_webservice_result);
                            }
                        }
    
                        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                        {
                            footer_fragment.showServerWebserviceResult(server_webservice_result, server_status_code);
                            server_status_code = 0;
                        }

                        if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                        {
                            FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                switch (http_operation_type)
                                {
                                    case GET_GATEWAY_CONFIG:
                                    {
                                        fragment.showGetGatewayConfigResult(server_webservice_result);
                                    }
                                    break;
    
                                    case GET_SERVER_CONFIGURABLE_TEXT:
                                    {
                                        fragment.showGetServerConfigurableTextResult(server_webservice_result);
                                    }
                                    break;

                                    case GET_VIEWABLE_WEBPAGES:
                                    {
                                        fragment.showGetWebPagesResult(server_webservice_result);
                                    }
                                    break;
                                }
                            }
                        }
                        else if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                switch (http_operation_type)
                                {
                                    case GET_DEFAULT_EARLY_WARNING_SCORES_LIST:
                                    {
                                        // If Success then need to keep it showing busy until the thresholds have been processed
                                        if (server_webservice_result == false)
                                        {
                                            fragment.hideGettingEwsThresholdsBusyIndicator();
                                        }
                                    }
                                    break;

                                    case GET_GATEWAY_CONFIG:
                                    {
                                        fragment.showGetGatewayConfigResult(server_webservice_result);
                                    }
                                    break;
    
                                    case GET_SERVER_CONFIGURABLE_TEXT:
                                    {
                                        fragment.showGetServerConfigurableTextResult(server_webservice_result);
                                    }
                                    break;

                                    case GET_VIEWABLE_WEBPAGES:
                                    {
                                        fragment.showGetViewableWebPageFromServerResult(server_webservice_result);
                                    }
                                    break;

                                    case GET_DEVICE_FIRMWARE_VERSION_LIST:
                                    {
                                        // If Success then need to keep it showing busy until the Firmware/APK update is downloaded
                                        if (server_webservice_result == false)
                                        {
                                            fragment.hideUpdateBusyIndicator();
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_QR_CODE_DETAILS:
                    {
                        boolean qr_code_validity = intent.getBooleanExtra("qr_code_validity", false);
                        if (qr_code_validity)
                        {
                            long human_readable_device_id = intent.getLongExtra("human_readable_product_id", 0);
                            String bluetooth_device_address= intent.getStringExtra("bluetooth_device_address");
                            BarcodeDeviceType barcode_type = BarcodeDeviceType.values()[intent.getIntExtra("barcode_type", BarcodeDeviceType.BARCODE_TYPE__INVALID.ordinal())];
    
                            Log.d(TAG, "CMD_REPORT_QR_CODE_DETAILS : barcode_type = " + barcode_type);
    
                            switch (barcode_type)
                            {
                                case BARCODE_TYPE__LIFETOUCH:
                                case BARCODE_TYPE__LIFETOUCH_BLUE_V2:
                                case BARCODE_TYPE__LIFETEMP_V2:
                                case BARCODE_TYPE__NONIN_WRIST_OX:
                                case BARCODE_TYPE__NONIN_WRIST_OX_BTLE:
                                case BARCODE_TYPE__AND_UA767:
                                case BARCODE_TYPE__AND_TM2441:
                                case BARCODE_TYPE__AND_UA651:
                                case BARCODE_TYPE__FORA_IR20:
                                case BARCODE_TYPE__INSTAPATCH:
                                case BARCODE_TYPE__LIFETOUCH_THREE:
                                case BARCODE_TYPE__NONIN_3230:
                                case BARCODE_TYPE__AND_UC352BLE:
                                case BARCODE_TYPE__MEDLINKET:
                                case BARCODE_TYPE__AND_UA1200BLE:
                                case BARCODE_TYPE__AND_UA656BLE:
                                {
                                    // Then the Gateway is reporting that its a Device QR code
                                    DeviceType device_type = DeviceType.values()[intent.getIntExtra("device_type", 0)];
                                    SensorType sensor_type = SensorType.values()[intent.getIntExtra("sensor_type", 0)];
    
                                    Log.d(TAG, "CMD_REPORT_QR_CODE_DETAILS : device_type  = " + device_type);
                                    Log.d(TAG, "CMD_REPORT_QR_CODE_DETAILS : sensor_type  = " + sensor_type);
    
                                    boolean dummy_data = intent.getBooleanExtra("dummy_data", false);
    
                                    handleDeviceQrCode(sensor_type, device_type, human_readable_device_id, bluetooth_device_address, dummy_data);
                                }
                                break;
    
                                // Unlock Gateway QR code. Normal mode of operation
                                case BARCODE_TYPE__UNLOCK_REQUEST_GENERAL:
                                {
                                    gateway_user_id = (int)human_readable_device_id;
    
                                    Log.d(TAG, "CMD_REPORT_QR_CODE_DETAILS : Database User ID Row = " + gateway_user_id);
    
                                    boolean qr_code_was_emulated = intent.getBooleanExtra("emulated", false);
    
                                    if (qr_code_was_emulated == false)
                                    {
                                        storeAuditTrailEvent(AuditTrailEvent.QR_GENERAL_UNLOCK, gateway_user_id);
                                    }
    
                                    if (current_page == UserInterfacePage.UNLOCK_SCREEN)
                                    {
                                        Log.w(TAG, "CMD_REPORT_QR_CODE_DETAILS : BARCODE_TYPE__UNLOCK_REQUEST_GENERAL : previous_page_before_locking_page = " + previous_page_before_locking_page);
    
                                        switch (previous_page_before_locking_page)
                                        {
                                            case PATIENT_DETAILS_NAME:
                                            {
                                                showPatientDetailsName(true);
                                            }
                                            break;
                                            
                                            case PATIENT_CASE_ID_ENTRY:
                                            {
                                                showPatientCaseIdEntry();
                                            }
                                            break;
    
                                            case PATIENT_THRESHOLD_CATEGORY:
                                            {
                                                patientDetailsThresholdCategorySelected();
                                            }
                                            break;
    
                                            case ADD_DEVICES:
                                            {
                                                addDevicesSelected();
                                            }
                                            break;
    
                                            case DEVICE_CONNECTION:
                                            {
                                                // Make the Gateway send the sendCommandEndOfDeviceConnection is all the devices are connected
                                                // Means if the UI is locked on the Device Connection page, then the Start Monitoring button reappears
                                                portal_system_commands.sendGatewayCommand_refreshDeviceConnectionState();
    
                                                // trigger reload of scan progress indicators, as we're continuing whatever scan was in progress when the screen was locked
                                                showDeviceConnectionFragment_ResumeCurrentScan();
                                            }
                                            break;
    
                                            case CHECK_DEVICE_STATUS:
                                            {
                                                checkDeviceStatusPressed();
                                            }
                                            break;
    
                                            case END_SESSION_TIME_SELECTION:
                                            {
                                                showEndSessionTimerSelectionFragment();
                                            }
                                            break;
    
                                            case SOFTWARE_UPDATE_IN_PROGRESS:
                                            {
                                                showSoftwareUpdateMode();
                                            }
                                            break;

                                            case WEBPAGE_SELECTION:
                                            {
                                                webpageSelectionSelected();
                                            }
                                            break;
    
                                            case ADMIN_MODE:
                                            case INSTALLATION_MODE_PROGRESS:
                                            case INSTALLATION_MODE_SERVER_ADDRESS_SCAN:
                                            case UNLOCK_SCREEN:
                                            case INVALID:
                                                break;
    
                                            case MODE_SELECTION:
                                            case DUMMY_DATA_MODE:
                                            case SOFTWARE_UPDATE_AVAILABLE:
                                            default:
                                            {
                                                showModeSelectionOrGatewayNotConfiguredYet();
                                            }
                                            break;
    
                                        } // switch (previous_page_before_locking_page)
    
                                    } // if (current_page == UserInterfacePage.UNLOCK_SCREEN)
    
                                }
                                break;
    
                                // Unlock Gateway QR code - in Admin mode - more stuff visible on screen
                                case BARCODE_TYPE__UNLOCK_REQUEST_ADMIN:
                                {
                                    gateway_user_id = (int)human_readable_device_id;
    
                                    Log.d(TAG, "CMD_REPORT_QR_CODE_DETAILS : Database User ID Row = " + gateway_user_id);
    
                                    boolean qr_code_was_emulated = intent.getBooleanExtra("emulated", false);
                                    if (qr_code_was_emulated == false)
                                    {
                                        storeAuditTrailEvent(AuditTrailEvent.QR_ADMIN_UNLOCK, gateway_user_id);
                                    }
    
                                    if ((current_page == UserInterfacePage.UNLOCK_SCREEN)
                                            || (current_page == UserInterfacePage.INSTALLATION_MODE_SERVER_ADDRESS_SCAN)
                                            || (current_page == UserInterfacePage.INSTALLATION_MODE_WELCOME))
                                    {
                                        startUserInterfaceTimeoutAndEnterAdminMode();
                                    }
                                }
                                break;
    
                                case BARCODE_TYPE__UNLOCK_REQUEST_FEATURE_ENABLE:
                                {
                                    gateway_user_id = (int)human_readable_device_id;
    
                                    Log.d(TAG, "CMD_REPORT_QR_CODE_DETAILS : Database User ID Row = " + gateway_user_id);
    
                                    boolean qr_code_was_emulated = intent.getBooleanExtra("emulated", false);
    
                                    if (current_page == UserInterfacePage.UNLOCK_SCREEN)
                                    {
                                        startUserInterfaceTimeoutAndEnterFeatureEnableMode();
                                    }
                                }
                                break;
    
                                case BARCODE_TYPE__UNLOCK_DUMMY_DATA_MODE:
                                {
                                    gateway_user_id = (int)human_readable_device_id;
    
                                    Log.d(TAG, "CMD_REPORT_QR_CODE_DETAILS : Database User ID Row = " + gateway_user_id);
    
                                    storeAuditTrailEvent(AuditTrailEvent.QR_DUMMY_UNLOCK, gateway_user_id);
    
                                    if (current_page == UserInterfacePage.UNLOCK_SCREEN)
                                    {
                                        showDummyDataModeOrGatewayNotConfiguredYet();
                                    }
                                }
                                break;
    
                                default:
                                {
                                    Log.e(TAG, "Invalid Barcode Type");
                                }
                                break;
                            }
                        }
                        else
                        {
                            //  Invalid QR code
                        }
                    }
                    break;
    
                    case CMD_REPORT_INSTALLATION_QR_CODE_DETAILS:
                    {
                        boolean qr_code_valid = intent.getBooleanExtra("qr_code_validity", false);
                        if (qr_code_valid)
                        {
                            showInstallationProgressFragment();
                        }
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_BATTERY_LEVEL:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);
    
                        device_info.last_battery_reading_percentage = intent.getIntExtra("battery_percentage", 0);
                        device_info.last_battery_reading_in_millivolts = intent.getIntExtra("battery_voltage_in_millivolts", 0);
                        device_info.last_battery_reading_received_timestamp = intent.getLongExtra("time_measurement_received", 0);
    
                        Log.d(TAG, device_type + " Battery Level = " + device_info.last_battery_reading_percentage + "% = " + device_info.last_battery_reading_in_millivolts + "mV at " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(device_info.last_battery_reading_received_timestamp));
    
                        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            if(isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO())
                            {
                                Log.w(TAG, "CMD_REPORT_DEVICE_BATTERY_LEVEL : this cant be executed as number of connected device is ZERO");
                            }
                            else
                            {
                                FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.setDeviceBatteryLevel(device_info);
                                }
                            }
                        }
    
                        if (current_page == UserInterfacePage.UNLOCK_SCREEN)
                        {
                            FragmentQrCodeUnlock fragment = (FragmentQrCodeUnlock) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDeviceBatteryLevel(device_info);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_INFO:
                    {
                        processReportedDeviceInfo(intent);
                    }
                    break;
    
                    case CMD_END_OF_DEVICE_CONNECTION:
                    {
                        boolean all_desired_devices_connected = intent.getBooleanExtra("all_desired_devices_connected", false);
    
                        boolean auto_press_start_monitoring_button = intent.getBooleanExtra("auto_press_start_monitoring_button", false);
    
                        Log.d(TAG, "CMD_END_OF_DEVICE_CONNECTION : all_desired_devices_connected = " + all_desired_devices_connected + " : auto_press_start_monitoring_button = " + auto_press_start_monitoring_button);
    
                        if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                        {
                            if (all_desired_devices_connected)
                            {
                                if (auto_press_start_monitoring_button)
                                {
                                    // Simulate the button being pressed
                                    startMonitoringButtonPressed();
                                }
                                else
                                {
                                    showStartMonitoringButton();
                                }
                            }
                            else
                            {
                                // Bluetooth error has happened
    
                                // Hide the Start Monitoring button
                                hideStartMonitoringButton();
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SERVER_SYNC_ENABLE_STATUS:
                    {
                        boolean enable_status = intent.getBooleanExtra("server_sync_enabled", false);
    
                        server_sync_enabled = enable_status;
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showServerDataSyncStatus(enable_status);
                            }
                        }
    
                        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                        {
                            footer_fragment.enableServerConnectionStatusIndicator(enable_status);
                        }
                    }
                    break;
    
                    case CMD_REPORT_REALTIME_LINK_ENABLE_STATUS:
                    {
                        boolean enable_status = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showServerRealtimeLinkStatus(enable_status);
                            }
                        }
    
                        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                        {
                            footer_fragment.enableRealTimeServerConnectionStatusIndicator(enable_status);
                        }
                    }
                    break;
    
                    case CMD_REPORT_CONNECTED_TO_SERVER_STATUS:
                    {
                        boolean connected = intent.getBooleanExtra("connected_to_server", false);
    
                        Log.d(TAG, "CMD_REPORT_CONNECTED_TO_SERVER_STATUS : " + connected);
    
                        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                        {
                            footer_fragment.showRealTimeServerConnectionStatus(connected);
                        }
    
                        if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                        {
                            FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showRealtimeServerConnectionResult(connected);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DUMMY_DATA_MODE_DEVICE_LEADS_OFF_ENABLE_STATUS:
                    {
                        SensorType sensor_type = getSensorTypeFromIntent(intent);
    
                        boolean enabled = intent.getBooleanExtra("enabled_status", false);
    
                        if (current_page == UserInterfacePage.DUMMY_DATA_MODE)
                        {
                            FragmentDummyDataMode fragment = (FragmentDummyDataMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDeviceLeadsOffEnableStatus(sensor_type, enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_WARD_LIST:
                    {
                        ArrayList<WardInfo> ward_info_list = intent.getParcelableArrayListExtra("ward_info_list");
    
                        for (WardInfo this_ward_info : ward_info_list)
                        {
                            Log.d(TAG, "ward_details_id = " + this_ward_info.ward_details_id + " : ward_name = " + this_ward_info.ward_name);
                        }
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.updateWardList(ward_info_list);
                            }
                        }
                        else if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                        {
                            FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.updateWardList(ward_info_list);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_CACHED_DATA_UPDATED:
                    {
                        handleCachedDataUpdated(intent);
                    }
                    break;
    
                    case CMD_REPORT_SERVER_DATA_SYNC_STARTING_DUE_TO_MISSING_THRESHOLDS:
                    {
                        showInstallationProgressGetServerDataFragment(false);
                    }
                    break;
    
                    case CMD_REPORT_SERVER_DATA_SYNC_STARTING_DUE_TO_SOFTWARE_UPDATE:
                    {
                        showInstallationProgressGetServerDataFragment(true);
                    }
                    break;
    
                    case CMD_REPORT_EWS_DOWNLOAD_SUCCESS:
                    {
                        boolean success = intent.getBooleanExtra("success", false);
    
                        if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                        {
                            FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showGetThresholdsResult(success);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_BED_LIST:
                    {
                        ArrayList<BedInfo> bed_info_list = intent.getParcelableArrayListExtra("bed_info_list");
    
                        if (bed_info_list.size() > 0)
                        {
                            for (BedInfo this_bed_info : bed_info_list)
                            {
                                Log.d(TAG, "Bed Details ID = " + this_bed_info.bed_details_id + " : ByWardID = " + this_bed_info.by_ward_id + " : bed_name = " + this_bed_info.bed_name);
                            }
    
                            if (current_page == UserInterfacePage.ADMIN_MODE)
                            {
                                FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.updateBedList(bed_info_list);
                                }
                            }
                            else if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                            {
                                FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.updateBedList(bed_info_list);
                                }
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_LEAD_OFF_DETECTION_STATUS:
                    {
                        SensorType sensor_type = getSensorTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(sensor_type);
    
                        boolean not_connected_to_patient = intent.getBooleanExtra("not_connected_to_patient", false);
    
                        handleLeadsOffStatusChange(device_info, not_connected_to_patient);
                    }
                    break;
    
                    case CMD_REPORT_LIFETOUCH_NO_BEATS_DETECTED_TIMER_STATUS:
                    {
                        DeviceInfo device_info = getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);
    
                        // Need to save status to when the Patient Vitals Display loads it can read the current status
                        lifetouch_no_beats_detected = intent.getBooleanExtra("no_beats_detected", false);
    
                        if ((lifetouch_no_beats_detected != previous_value_of_is_lifetouch_no_beats_detected) && device_info.isDeviceHumanReadableDeviceIdValid() && current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            patientVitalsDisplayDeviceStateChangeOfDevicesInSession(device_info);
                        }
    
                        previous_value_of_is_lifetouch_no_beats_detected = lifetouch_no_beats_detected;
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_OPERATING_MODE:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);
    
                        DeviceInfo.OperatingMode operating_mode = DeviceInfo.OperatingMode.values()[intent.getIntExtra("operating_mode", 0)];
    
                        // Periodic setup mode handled by handlePeriodicModeChange()
                        if (operating_mode == DeviceInfo.OperatingMode.GATEWAY_INITIATED_SETUP_MODE || operating_mode == DeviceInfo.OperatingMode.SERVER_INITIATED_SETUP_MODE)
                        {
                            device_info.setDeviceInSetupMode(true);
                        }
                        else
                        {
                            device_info.setDeviceInSetupMode(false);
                            device_info.setDeviceInPeriodicSetupMode(false);  // IIT-2066, handlePeriodicModeChange() does not always handle this correctly, DeviceInfo can think devices are in periodic setup
                                                                              // mode when they are not, causing user-initiated setup mode to show the uncancellable "Periodic setup mode" checkbox
                        }
    
                        device_info.setDeviceInRawAccelerometerMode(operating_mode == DeviceInfo.OperatingMode.GATEWAY_INITIATED_RAW_ACCELEROMETER_MODE || operating_mode == DeviceInfo.OperatingMode.SERVER_INITIATED_RAW_ACCELEROMETER_MODE);
    
                        device_info.setDeviceInServerControl(operating_mode == DeviceInfo.OperatingMode.SERVER_INITIATED_RAW_ACCELEROMETER_MODE || operating_mode == DeviceInfo.OperatingMode.SERVER_INITIATED_SETUP_MODE);
    
                        if ((device_info.hasDeviceSetupModeStateChanged()) && (device_info.isDeviceHumanReadableDeviceIdValid()))
                        {
                            refreshDeviceSetupModeStateOnScreen(device_info);
                        }
                        device_info.updatePreviousValueOfIsDeviceInSetupMode();
    
    
                        if ((device_info.hasDeviceRawAccelerometerModeStateChanged()) && (device_info.isDeviceHumanReadableDeviceIdValid()))
                        {
                            refreshDeviceRawAccelerometerModeStateOnScreen(device_info);
                        }
                        device_info.updatePreviousValueOfIsDeviceInRawAccelerometerMode();
                    }
                    break;
    
                    case CMD_REPORT_CHECK_DEVICE_STATUS_RESULTS:
                    {
                        Log.d(TAG, "CMD_REPORT_CHECK_DEVICE_STATUS_RESULTS");
    
                        boolean result = intent.getBooleanExtra("result", false);
    
                        if (result)
                        {
                            // Got a reply from the Server
                            String ward_name = intent.getStringExtra("ward_name");
                            String bed_name = intent.getStringExtra("bed_name");
                            DeviceType device_type = getDeviceTypeFromIntent(intent);
                            DeviceInfo device_info = getDeviceByType(device_type);
                            boolean device_in_use = intent.getBooleanExtra("in_use", true);
    
                            Log.w(TAG, "CMD_REPORT_CHECK_DEVICE_STATUS_RESULTS : device_in_use : " + device_in_use);
    
                            if (current_page == UserInterfacePage.CHECK_DEVICE_STATUS)
                            {
                                FragmentCheckDeviceStatus fragment = (FragmentCheckDeviceStatus) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.showCheckingWithServer(false);
    
                                    if (device_in_use)
                                    {
                                        fragment.showDeviceInUse(ward_name, bed_name);
                                    }
                                    else
                                    {
                                        fragment.showDeviceNotInUse();
                                    }
                                }
                            }
    
                            if (current_page == UserInterfacePage.ADD_DEVICES)
                            {
                                FragmentAddDevices fragment = (FragmentAddDevices) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    boolean good_to_use = true;
    
                                    if (device_in_use)
                                    {                                                       // If both of these are empty then the device is NOT already in use
                                        good_to_use = false;
                                    }
    
                                    fragment.showDeviceGoodToUseResult(device_info.sensor_type, good_to_use, ward_name, bed_name);
                                }
                            }
                        }
                        else
                        {
                            // Webservice call failed
    
                            if (current_page == UserInterfacePage.CHECK_DEVICE_STATUS)
                            {
                                FragmentCheckDeviceStatus fragment = (FragmentCheckDeviceStatus) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.showCheckingWithServer(false);
                                    fragment.serverRequestFailed();
                                }
                            }
    
                            if (current_page == UserInterfacePage.ADD_DEVICES)
                            {
                                FragmentAddDevices fragment = (FragmentAddDevices) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.serverRequestFailed();
                                }
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SERVER_PORT:
                    {
                        String server_port = intent.getStringExtra("server_port");
                        Log.d(TAG, "CMD_REPORT_SERVER_PORT = " + server_port);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setServerPort(server_port);
                            }
                        }
                        else if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                        {
                            FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setServerPort(server_port);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_REALTIME_SERVER_PORT:
                    {
                        String realtime_server_port = intent.getStringExtra("realtime_server_port");
                        Log.d(TAG, "CMD_REPORT_REALTIME_SERVER_PORT = " + realtime_server_port);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setRealTimeServerPort(realtime_server_port);
                            }
                        }
                        else if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                        {
                            FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setRealTimeServerPort(realtime_server_port);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_SETUP_MODE_STARTED_VIA_SERVER:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);
    
                        Log.d(TAG, "CMD_REPORT_DEVICE_SETUP_MODE_STARTED_VIA_SERVER : " + device_type);
    
                        closePoincarePopupIfShowing();
    
                        device_info.setDeviceInServerControl(true);
                        device_info.setDeviceInSetupMode(true);
    
                        refreshDeviceSetupModeStateOnScreen(device_info);
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_SETUP_MODE_STOPPED_VIA_SERVER:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);
    
                        Log.d(TAG, "CMD_REPORT_DEVICE_SETUP_MODE_STOPPED_VIA_SERVER : " + device_type);
    
                        device_info.setDeviceInServerControl(false);
    
                        device_info.setDeviceInSetupMode(false);
    
                        refreshDeviceSetupModeStateOnScreen(device_info);
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_RAW_ACCELEROMETER_MODE_STARTED_VIA_SERVER:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);
    
                        Log.d(TAG, "CMD_REPORT_DEVICE_RAW_ACCELEROMETER_MODE_STARTED_VIA_SERVER : " + device_type);
    
                        closePoincarePopupIfShowing();
    
                        device_info.setDeviceInRawAccelerometerMode(true);
                        device_info.setDeviceInServerControl(true);
    
                        refreshDeviceRawAccelerometerModeStateOnScreen(device_info);
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_RAW_ACCELEROMETER_MODE_STOPPED_VIA_SERVER:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);
    
                        Log.d(TAG, "CMD_REPORT_DEVICE_RAW_ACCELEROMETER_MODE_STOPPED_VIA_SERVER : " + device_type);
    
                        device_info.setDeviceInRawAccelerometerMode(false);
                        device_info.setDeviceInServerControl(false);
    
                        refreshDeviceRawAccelerometerModeStateOnScreen(device_info);
                    }
                    break;
    
                    case CMD_REPORT_HTTPS_ENABLE_STATUS:
                    {
                        boolean https_enabled = intent.getBooleanExtra("https_enabled", false);
                        Log.d(TAG, "CMD_REPORT_HTTPS_ENABLE_STATUS = " + https_enabled);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setHttpsEnableStatus(https_enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_WEBSERVICE_AUTHENTICATION_ENABLE_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);
                        Log.d(TAG, "CMD_REPORT_WEBSERVICE_AUTHENTICATION_ENABLE_STATUS = " + enabled);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setWebServiceAuthenticationStatus(enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_WEBSERVICE_ENCRYPTION_ENABLE_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);
                        Log.d(TAG, "CMD_REPORT_WEBSERVICE_ENCRYPTION_ENABLE_STATUS = " + enabled);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setWebServiceEncryptionStatus(enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_PATIENT_NAME_FROM_SERVER_LOOKUP_OF_HOSPITAL_PATIENT_ID:
                    {
                        // This is NEVER stored on the Gateway. Only held in RAM
                        FullPatientDetails fullPatientDetails = new FullPatientDetails();

                        fullPatientDetails.firstName = intent.getStringExtra("firstName");
                        fullPatientDetails.lastName = intent.getStringExtra("lastName");
                        fullPatientDetails.dateOfBirth = intent.getStringExtra("dob");
                        fullPatientDetails.gender = intent.getStringExtra("gender");
    
                        if (fullPatientDetails.firstName == null)
                        {
                            fullPatientDetails.firstName = getResources().getString(R.string.four_dashes);
                        }
    
                        if (fullPatientDetails.lastName == null)
                        {
                            fullPatientDetails.lastName = getResources().getString(R.string.four_dashes);
                        }
    
                        if (fullPatientDetails.dateOfBirth == null)
                        {
                            fullPatientDetails.dateOfBirth = getResources().getString(R.string.four_dashes);
                        }
    
                        if (fullPatientDetails.gender == null)
                        {
                            fullPatientDetails.gender = getResources().getString(R.string.four_dashes);
                        }
                        else
                        {
                            if (fullPatientDetails.gender.equals("M"))
                            {
                                fullPatientDetails.gender = getResources().getString(R.string.male);
                            }
                            else if (fullPatientDetails.gender.equals("F"))
                            {
                                fullPatientDetails.gender = getResources().getString(R.string.female);
                            }
                        }
    
                        boolean complete = intent.getBooleanExtra("complete", false);
                        PatientDetailsLookupStatus status = PatientDetailsLookupStatus.values()[intent.getIntExtra("status", 0)];

    /*
                        // These are useful during testing, but must NOT be used in production as it means we have Patient Names in the log files
                        Log.e(TAG, "CMD_REPORT_PATIENT_NAME_FROM_SERVER_LOOKUP_OF_HOSPITAL_PATIENT_ID : firstName = " + firstName);
                        Log.e(TAG, "CMD_REPORT_PATIENT_NAME_FROM_SERVER_LOOKUP_OF_HOSPITAL_PATIENT_ID : lastName = " + lastName);
                        Log.e(TAG, "CMD_REPORT_PATIENT_NAME_FROM_SERVER_LOOKUP_OF_HOSPITAL_PATIENT_ID : dob = " + dob);
                        Log.e(TAG, "CMD_REPORT_PATIENT_NAME_FROM_SERVER_LOOKUP_OF_HOSPITAL_PATIENT_ID : gender = " + gender);
    */
                        Log.d(TAG, "CMD_REPORT_PATIENT_NAME_FROM_SERVER_LOOKUP_OF_HOSPITAL_PATIENT_ID : complete = " + complete);
                        Log.d(TAG, "CMD_REPORT_PATIENT_NAME_FROM_SERVER_LOOKUP_OF_HOSPITAL_PATIENT_ID : status = " + status);

                        if (current_page == UserInterfacePage.PATIENT_CASE_ID_ENTRY)
                        {
                            if(popup_patient_name.getDialog() != null)
                            {
                                if(popup_patient_name.getDialog().isShowing())
                                {
                                    switch (status)
                                    {
                                        case REQUEST_SENT_TO_EXTERNAL_SERVER:
                                        {
                                            popup_patient_name.showRequestSentToHospitalServer();
                                        }
                                        break;
                                    }

                                    if (complete)
                                    {
                                        popup_patient_name.stopServerResponseTimerAsServerResponseReceived();

                                        if (status == PatientDetailsLookupStatus.PATIENT_FOUND)
                                        {
                                            popup_patient_name.setPatientInfo(fullPatientDetails);
                                        }
                                        else if (status == PatientDetailsLookupStatus.NO_RESPONSE_FROM_EXTERNAL_SERVER)
                                        {
                                            popup_patient_name.showNoResponseFromExternalServer();
                                        }
                                        else if (status == PatientDetailsLookupStatus.PATIENT_NOT_FOUND)
                                        {
                                            popup_patient_name.showPatientCaseIdNotFound();
                                        }
                                        else if (status == PatientDetailsLookupStatus.UNKNOWN_ERROR)
                                        {
                                            popup_patient_name.stopServerResponseTimerAsErrorOccurred();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_PATIENT_NAME_LOOKUP_ENABLE_STATUS:
                    {
                        features_enabled.server_lookup_of_patient_name_from_patient_id = intent.getBooleanExtra("patient_id_check_enabled", false);
                        Log.d(TAG, "CMD_REPORT_PATIENT_NAME_LOOKUP_ENABLE_STATUS = patient ID enable status " + features_enabled.server_lookup_of_patient_name_from_patient_id);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setPatientIdCheckEnableStatus(features_enabled.server_lookup_of_patient_name_from_patient_id);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_PERIODIC_SETUP_MODE_ENABLE_STATUS:
                    {
                        boolean enabled = getEnabledFromIntent(intent);
    
                        Log.d(TAG, "CMD_REPORT_DEVICE_PERIODIC_SETUP_MODE_ENABLE_STATUS = enabled " + enabled);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDevicePeriodicSetupModeEnableStatus(enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_CSV_ENABLE_STATUS:
                    {
                        features_enabled.csv_output = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setCsvOutputEnableStatus(features_enabled.csv_output);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_MANUAL_VITAL_SIGNS_ENABLED_STATUS:
                    {
                        features_enabled.manually_entered_vital_signs = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setManualVitalSignsEnableStatus(features_enabled.manually_entered_vital_signs);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SPOOF_EARLY_WARNING_SCORES:
                    {
                        boolean spoof = intent.getBooleanExtra("spoof_early_warning_scores", false);
    
                        if (current_page == UserInterfacePage.DUMMY_DATA_MODE)
                        {
                            FragmentDummyDataMode fragment = (FragmentDummyDataMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDummyDataCheckBoxSpoofEarlyWarningScores(spoof);
                            }
                        }
                    }
                    break;
    
                    case CMD_DISPLAY_DEVICE_PERIODIC_MODE_DATA_IN_UI:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);
    
                        boolean in_periodic_setup_mode = intent.getBooleanExtra("in_periodic_setup_mode", false);
    
                        handlePeriodicModeChange(device_info, in_periodic_setup_mode);
                    }
                    break;
    
                    case CMD_REPORT_BLE_DEVICE_CHANGE_SESSION_DISCONNECTED:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);
    
                        Log.d(TAG, "CMD_REPORT_BLE_DEVICE_CHANGE_SESSION_DISCONNECTED = Change session Disconnect of " + device_info.getSensorTypeAndDeviceTypeAsString() + " is completed");
    
                        clearDesiredDeviceInPatientGateway(device_type);
    
                        if (current_page == UserInterfacePage.CHANGE_SESSION_SETTINGS)
                        {
                            FragmentChangeSessionSettings fragment = (FragmentChangeSessionSettings) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.bleDeviceDisconnectionCompleted(device_info);
                            }
                            else
                            {
                                Log.e(TAG, "FragmentChangeSessionSettings : fragment == null !!!!!!!!!");
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_NTP_CLOCK_OFFSET_IN_MS:
                    {
                        boolean success = intent.getBooleanExtra("success", false);
    
                        int attempt_number = intent.getIntExtra("attempt_number", 0);
                        int attempt_max = intent.getIntExtra("attempt_max", 0);
    
                        if (success)
                        {
                            double local_clock_offset_in_milliseconds = intent.getDoubleExtra("local_clock_offset_in_milliseconds", 0);
    
                            AlternateTimeSource.setTimeOffsetInMilliseconds(local_clock_offset_in_milliseconds);
    
                            Log.d(TAG, "CMD_REPORT_NTP_CLOCK_OFFSET_IN_MS : Received the local time offset of the tablet as  " + new DecimalFormat("0.00").format(local_clock_offset_in_milliseconds) + " ms");
    
                            String time_offset_string = "";
                            if (local_clock_offset_in_milliseconds > 0)
                            {
                                time_offset_string = "+";
                            }
    
                            time_offset_string += new DecimalFormat("0.000").format(local_clock_offset_in_milliseconds / DateUtils.SECOND_IN_MILLIS) + " Sec";
    
                            // Fragment footer is update every second by the timer, So no need to call the timer update here.
                            if (current_page == UserInterfacePage.ADMIN_MODE)
                            {
                                FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.setTextViewLocalTimeOffset(time_offset_string);
                                }
                            }
                            else if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                            {
                                FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.setTextViewLocalTimeOffset(true, attempt_number, attempt_max);
                                }
                            }
    
                            FragmentFooter fragment = (FragmentFooter) getSupportFragmentManager().findFragmentById(R.id.fragment_footer);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.updateClock();
                            }
                        }
                        else
                        {
                            String failure_string = "Failed " + attempt_number + "/" + attempt_max;
    
                            if (current_page == UserInterfacePage.ADMIN_MODE)
                            {
                                FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.setTextViewLocalTimeOffset(failure_string);
                                }
                            }
    
                            if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                            {
                                FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.setTextViewLocalTimeOffset(false, attempt_number, attempt_max);
                                }
                            }
                        }
                    }
                    break;
    
                    case CMD_UPDATE_UI_DEVICE_MEASUREMENT_PROGRESS_BAR:
                    {
                        for (VitalSignType vital_sign_type : VitalSignType.values())
                        {
                            MeasurementVitalSign measurement = getSavedMeasurement(vital_sign_type);
    
                            if (measurement != null)
                            {
                                if (measurement.measurement_validity_time_left_in_seconds >= 0)
                                {
                                    if(features_enabled.stop_fast_ui_updates)
                                    {
                                        // For automated tests, only update the progress bar every 10 seconds
                                        // This helps UiAutomator2 run smoothly without timing out waiting for the screen to be idle
                                        if((measurement.measurement_validity_time_left_in_seconds % 10) == 0)
                                        {
                                            updatePatientVitalsMeasurementProgressBar(vital_sign_type, measurement.measurement_validity_time_left_in_seconds);
                                        }
                                    }
                                    else
                                    {
                                        updatePatientVitalsMeasurementProgressBar(vital_sign_type, measurement.measurement_validity_time_left_in_seconds);
                                    }
    
                                    measurement.measurement_validity_time_left_in_seconds--;
                                }
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_WIFI_STATUS:
                    {
                        wifi_status.hardware_enabled = intent.getBooleanExtra("wifi_enabled", false);
                        wifi_status.connected_to_ssid = intent.getBooleanExtra("connected_to_ssid", false);
                        wifi_status.ssid = intent.getStringExtra("ssid");
                        wifi_status.ip_address_string = intent.getStringExtra("ip_address_string");
                        wifi_status.wifi_level = intent.getIntExtra("wifi_level", 0);
                        wifi_status.wifi_connection_status = intent.getStringExtra("wifi_Status");
                        wifi_status.wifi_BSSID = intent.getStringExtra("wifi_BSSID");
                        int wifi_error_status_integer = intent.getIntExtra("mWifiErrorStatus", 0);
                        wifi_status.mWifiErrorStatus = WifiErrorStatus.values()[wifi_error_status_integer];
    
                        int active_network_type_integer = intent.getIntExtra("active_network_type", 0);
                        active_network_type = ActiveNetworkTypes.values()[active_network_type_integer];
    
                        Log.d(TAG, "CMD_REPORT_WIFI_STATUS : active_network_type = " + active_network_type.name());
                        Log.d(TAG, "CMD_REPORT_WIFI_STATUS : mCollection wifi_hardware_enabled    " + wifi_status.hardware_enabled);
                        Log.d(TAG, "CMD_REPORT_WIFI_STATUS : mCollection connected_to_ssid    " + wifi_status.connected_to_ssid);
                        Log.d(TAG, "CMD_REPORT_WIFI_STATUS : mCollection ssid    " + wifi_status.ssid);
                        Log.d(TAG, "CMD_REPORT_WIFI_STATUS : mCollection ip_address_string    " + wifi_status.ip_address_string);
                        Log.d(TAG, "CMD_REPORT_WIFI_STATUS : mCollection wifi_level   " + wifi_status.wifi_level);
                        Log.d(TAG, "CMD_REPORT_WIFI_STATUS : mCollection wifi_connection_status    " + wifi_status.wifi_connection_status);
                        Log.d(TAG, "CMD_REPORT_WIFI_STATUS : mCollection wifi_BSSID   " + wifi_status.wifi_BSSID);
                        Log.d(TAG, "CMD_REPORT_WIFI_STATUS : mCollection wifi_failure_reason    " + wifi_status.mWifiErrorStatus.name());
    
                        if (active_network_type == ActiveNetworkTypes.WIFI)
                        {
                            // Is footer_fragment valid
                            if (UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                            {
                                if (getGsmOnlyModeFeatureEnabled() == false)
                                {
                                    // Show Wifi signal strength on footer fragment
                                    footer_fragment.showWifiLevel(wifi_status.wifi_level);
                                }
    
                                if (isConnectedToWifi() == false)
                                {
                                    footer_fragment.showRealTimeServerConnectionStatus(false);
                                }
                            }
                        }
    
                        // If wifi status popup is enabled then update the display
                        updateWifiStatusInPopup();
    
                        if(current_page == UserInterfacePage.INSTALLATION_MODE_WELCOME)
                        {
                            FragmentInstallationWelcome fragment = (FragmentInstallationWelcome) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.updateButtonsDependingOnConnectionStatus();
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_GSM_STATUS:
                    {
                        int active_network_type_integer = intent.getIntExtra("active_network_type", 0);
                        active_network_type = ActiveNetworkTypes.values()[active_network_type_integer];
                        Log.d(TAG, "CMD_REPORT_GSM_STATUS : active_network_type = " + active_network_type.name());
    
                        gsm_status.data_activity_direction = intent.getIntExtra("data_activity_direction", 0);
                        Log.d(TAG, "CMD_REPORT_GSM_STATUS : data_activity_direction = " + gsm_status.data_activity_direction);
    
                        gsm_status.data_connection_state = intent.getIntExtra("data_connection_state", 0);
                        Log.d(TAG, "CMD_REPORT_GSM_STATUS : data_connection_state = " + gsm_status.data_connection_state);
    
                        gsm_status.network_type = intent.getIntExtra("network_type", 0);
                        Log.d(TAG, "CMD_REPORT_GSM_STATUS : network_type = " + gsm_status.network_type);
    
                        gsm_status.signal_level = intent.getIntExtra("signal_level", 0);
                        Log.d(TAG, "CMD_REPORT_GSM_STATUS : signal_level = " + gsm_status.signal_level);
    
                        if (active_network_type == ActiveNetworkTypes.MOBILE)
                        {
                            if (UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                            {
                                if (isConnectedToWifi() == false)
                                {
                                    // Show Gsm signal strength on footer fragment
                                    footer_fragment.showGsmLevel(gsm_status.signal_level);
                                }
                            }
                        }
    
                        if(current_page == UserInterfacePage.INSTALLATION_MODE_WELCOME)
                        {
                            FragmentInstallationWelcome fragment = (FragmentInstallationWelcome) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.updateButtonsDependingOnConnectionStatus();
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DATABASE_EMPTIED:
                    {
                        boolean deleted_ews_thresholds = intent.getBooleanExtra("deleted_ews_thresholds", false);
                        Log.d(TAG, "CMD_REPORT_DATABASE_EMPTIED : deleted_ews_thresholds = " + deleted_ews_thresholds);
    
                        if(deleted_ews_thresholds)
                        {
                            // ToDo RM: test if we need this
                            getEarlyWarningScoringSetsFromPatientGatewayDatabase();
                            queryServerConfigurableText();
                        }
                    }
                    break;
    
                    case CMD_REPORT_DATABASE_STATUS:
                    {
                        if(popup_server_syncing.getDialog() != null)
                        {
                            if(popup_server_syncing.getDialog().isShowing())
                            {
                                updatePopupServerSyncRowsPending(intent);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_JSON_ARRAY_SIZE:
                    {
                        int json_array_size = intent.getIntExtra("json_array_size", 50);
    
                        if(current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setJsonArraySize(json_array_size);
                            }
                            else
                            {
                                Log.e(TAG, "CMD_REPORT_JSON_ARRAY_SIZE : FragmentAdminMode instance is NULL!!!!!!");
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_LIFETOUCH_BEATS_RECEIVED:
                    {
                        ArrayList<HeartBeatInfo> heart_beat_list = intent.getParcelableArrayListExtra("heart_beat_list");
    
                        DeviceInfo device_info = getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);
    
                        for (HeartBeatInfo heart_beat : heart_beat_list)
                        {
                            heart_beat_cache.add(heart_beat);
    
                            if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                            {
                                if(isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO())
                                {
                                    Log.w(TAG, "CMD_REPORT_LIFETOUCH_BEATS_RECEIVED : isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO = true");
                                }
                                else
                                {
                                    FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                    {
                                        if ((heart_beat.isBeatAmplitudeTooSmallOrToHigh() == false)
                                                && (features_enabled.stop_fast_ui_updates == false))
                                        {
                                            fragment.showHeartBeatIcon(heart_beat);
                                        }
                                    }
                                }
                            }
                            else if (current_page == UserInterfacePage.UNLOCK_SCREEN)
                            {
                                if(device_info.isDeviceOffBody() == false)
                                {
                                    FragmentQrCodeUnlock fragment = (FragmentQrCodeUnlock) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                    {
                                        if ((heart_beat.isBeatAmplitudeTooSmallOrToHigh() == false) && (features_enabled.stop_fast_ui_updates == false))
                                        {
                                            fragment.showHeartBeatIcon(heart_beat);
                                        }
                                    }
                                }
                            }
    
                            if (heart_beat.isBeatAmplitudeTooSmallOrToHigh())
                            {
                                Log.e(TAG, "*** Low/High Beat Amplitude ***");
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK:
                    {
                        int measurements_per_tick = intent.getIntExtra("measurements_per_tick", 1);
    
                        if (current_page == UserInterfacePage.DUMMY_DATA_MODE)
                        {
                            FragmentDummyDataMode fragment = (FragmentDummyDataMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setNumberOfMeasurementsPerTick(measurements_per_tick);
                            }
                        }
                    }
                    break;
    
                    case CMD_TELL_UI_THAT_GATEWAY_HAS_BOOTED:
                    {
                        Log.e(TAG, "CMD_TELL_UI_THAT_GATEWAY_HAS_BOOTED");
    
                        is_session_in_progress = false;
    
                        closeAllPopups();
    
                        // Reset cached info, so that it will be cleanly repopulated with new GW status info.
                        resetCachedSessionInfo();
    
                        getCurrentStatusFromGateway();
    
                        if(permissions.checkForRequiredAndroidPermissions())
                        {
                            current_page = UserInterfacePage.MODE_SELECTION;
    
                            lockScreenSelected();
                        }
                        else
                        {
                            showAndroidPermissionsFragment();
                        }
                    }
                    break;
    
                    case CMD_REPORT_NUMBER_OF_LIFETOUCH_HEART_BEATS_PENDING:
                    {
                        int number_of_measurements_pending = intent.getIntExtra("number_of_heart_beats_pending", 1);
    
                        // Synced lifetouch returns O heart beats pending
                        Log.d(TAG, "CMD_REPORT_NUMBER_OF_LIFETOUCH_HEART_BEATS_PENDING : number_of_heart_beats_pending = " + number_of_measurements_pending);
    
                        DeviceInfo device_info = getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);
                        device_info.setMeasurementsPending(number_of_measurements_pending);
    
                        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setNumberOfMeasurementsPending(SensorType.SENSOR_TYPE__LIFETOUCH, number_of_measurements_pending);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_NUMBER_OF_LIFETEMP_MEASUREMENTS_PENDING:
                    {
                        int number_of_measurements_pending = intent.getIntExtra("number_of_measurements_pending", 1);
    
                        Log.d(TAG, "CMD_REPORT_NUMBER_OF_LIFETEMP_MEASUREMENTS_PENDING : number_of_measurements_pending = " + number_of_measurements_pending);
    
                        DeviceInfo device_info = getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);
                        device_info.setMeasurementsPending(number_of_measurements_pending);
    
                        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setNumberOfMeasurementsPending(SensorType.SENSOR_TYPE__TEMPERATURE, number_of_measurements_pending);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_NEW_VITALS_DATA:
                    {
                        int type = intent.getIntExtra("data_type", -1);
    
                        VitalSignType vital_sign_type = VitalSignType.values()[type];
    
                        Log.d(TAG, "Vitals received from intent : vital_sign_type = " + vital_sign_type);
    
                        switch (vital_sign_type)
                        {
                            case HEART_RATE:
                            case RESPIRATION_RATE:
                            case TEMPERATURE:
                            case SPO2:
                            case BLOOD_PRESSURE:
                            case WEIGHT:
                            case MANUALLY_ENTERED_HEART_RATE:
                            case MANUALLY_ENTERED_RESPIRATION_RATE:
                            case MANUALLY_ENTERED_TEMPERATURE:
                            case MANUALLY_ENTERED_SPO2:
                            case MANUALLY_ENTERED_BLOOD_PRESSURE:
                            case MANUALLY_ENTERED_WEIGHT:
                            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                            case MANUALLY_ENTERED_ANNOTATION:
                            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                            case MANUALLY_ENTERED_URINE_OUTPUT:
                            case EARLY_WARNING_SCORE:
                            {
                                MeasurementVitalSign measurement = intent.getParcelableExtra("data_point");
                                handleMeasurement(vital_sign_type, measurement);
                            }
                            break;
    
                            case PATIENT_ORIENTATION:
                            case NOT_SET_YET:
                                break;
                        }
                    }
                    break;
    
                    case CMD_REPORT_PATIENT_START_SESSION_TIME:
                    {
                        session_start_milliseconds = intent.getLongExtra("patient_start_session_time", 0);
    
                        session_start_date = (session_start_milliseconds/(int)DateUtils.DAY_IN_MILLIS) * (int)DateUtils.DAY_IN_MILLIS;
    
                        String string_patient_start_session_time = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(session_start_milliseconds);
                        Log.d(TAG, "CMD_REPORT_PATIENT_START_SESSION_TIME : patient_start_session_time = " + string_patient_start_session_time);
                    }
                    break;
    
                    case CMD_REPORT_SETUP_MODE_TIME_IN_SECONDS:
                    {
                        int setup_mode_time_in_seconds = intent.getIntExtra("setup_mode_time_in_seconds", -1);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setSetupModeLengthInSeconds(setup_mode_time_in_seconds);
                            }
                        }
    
                        setCachedDeviceInfoSetupModeTimes(setup_mode_time_in_seconds);
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS:
                    {
                        int time_in_seconds = intent.getIntExtra("time_in_seconds", -1);
    
                        Log.d(TAG, "CMD_REPORT_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS = time_in_seconds " + time_in_seconds);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDevicePeriodicModePeriodTimeInSeconds(time_in_seconds);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS:
                    {
                        int time_in_seconds = intent.getIntExtra("time_in_seconds", -1);
    
                        Log.d(TAG, "CMD_REPORT_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS = time_in_seconds " + time_in_seconds);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDevicePeriodicModeActiveTimeInSeconds(time_in_seconds);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID:
                    {
                        int number = intent.getIntExtra("number", -1);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setNoninWristOxNumberOfInvalidIntermediateMeasurementsBeforeMarkingAsInvalid(number);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DISPLAY_TIMEOUT_IN_SECONDS:
                    {
                        int number = intent.getIntExtra("number", 120);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDisplayTimeoutLengthInSeconds(number, display_timeout_applies_to_patient_vitals);
                            }
                        }
    
                        display_timeout_length_in_seconds = number;
    
                        stopScreenLockCountdownTimerAndRestartIfDesired();
                    }
                    break;
    
                    case CMD_REPORT_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY:
                    {
                        boolean patient_vitals_should_timeout = intent.getBooleanExtra("display_timeout_applies_to_patient_vitals", false);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDisplayTimeoutAppliesToPatientVitalsDisplay(patient_vitals_should_timeout);
                            }
                        }
    
                        display_timeout_applies_to_patient_vitals = patient_vitals_should_timeout;
                    }
                    break;
    
                    case CMD_REPORT_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID:
                    {
                        int percentage = intent.getIntExtra("percentage", -1);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(percentage);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_PATIENT_ORIENTATION:
                    {
                        int orientation_as_int = intent.getIntExtra("patient_orientation", -1);
                        long timestamp = intent.getLongExtra("timestamp", 0);
    
                        PatientPositionOrientation patient_orientation = PatientPositionOrientation.values()[orientation_as_int];
    
                        boolean show_orientation_icon;
    
                        if ((getNtpTimeNowInMilliseconds() - timestamp) < DateUtils.MINUTE_IN_MILLIS)
                        {
                            // Measurement is current
                            show_orientation_icon = true;
                        }
                        else
                        {
                            // Measurement is old (probably downloading cached data)
                            show_orientation_icon = false;
                        }
    
                        setPatientOrientation(patient_orientation, show_orientation_icon);
    
                        switch (patient_orientation)
                        {
                            case ORIENTATION_UPRIGHT:
                                Log.d("POSITION", "POSITION: Upright");
                                break;
                            case ORIENTATION_LEFT_SIDE:
                                Log.d("POSITION", "POSITION: LeftSide");
                                break;
                            case ORIENTATION_RIGHT_SIDE:
                                Log.d("POSITION", "POSITION: RightSide");
                                break;
                            case ORIENTATION_FRONT:
                                Log.d("POSITION", "POSITION: Front");
                                break;
                            case ORIENTATION_BACK:
                                Log.d("POSITION", "POSITION: Back");
                                break;
                            case ORIENTATION_UPSIDE_DOWN:
                                Log.d("POSITION", "POSITION: UpsideDown");
                                break;
                            default:
                                Log.d("POSITION", "POSITION: Unknown");
                                break;
                        }
                    }
                    break;
    
                    case CMD_REPORT_ALL_BLUETOOTH_DEVICES_NOT_CONNECTED:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);
                        SensorType sensor_type = getSensorTypeFromIntent(intent);
    
                        Log.d(TAG, "CMD_REPORT_ALL_BLUETOOTH_DEVICES_NOT_CONNECTED : device_type = " + device_type);
    
                        displaySearchAgainButton(device_info);
                    }
                    break;
    
                    case CMD_REPORT_UNPLUGGED_OVERLAY_ENABLED_STATUS:
                    {
                        boolean is_unplugged_overlay_enabled_status = intent.getBooleanExtra("enabled", true);
    
                        Log.d(TAG, "CMD_REPORT_UNPLUGGED_OVERLAY_ENABLED_STATUS : is_unplugged_overlay_enabled_status = " + is_unplugged_overlay_enabled_status);
    
                        features_enabled.unplugged_overlay = is_unplugged_overlay_enabled_status;
    
                        // If its turned off then remove the overlay if required
                        if (features_enabled.unplugged_overlay == false)
                        {
                            showUnpluggedOverlay(false);
                        }
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setUnpluggedOverlayEnabledStatus(is_unplugged_overlay_enabled_status);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_LT3_KHZ_SETUP_MODE_ENABLED_STATUS:
                    {
                        boolean enabled = intent.getBooleanExtra("enabled", true);
                        Log.d(TAG, "CMD_REPORT_LT3_KHZ_SETUP_MODE_ENABLED_STATUS : enabled = " + enabled);

                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setLT3KHzSetupModeEnabledStatus(enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_AUTO_ENABLE_EWS__ENABLED_STATUS:
                    {
                        boolean enabled = intent.getBooleanExtra("enabled", true);
    
                        Log.d(TAG, "CMD_REPORT_AUTO_ENABLE_EWS__ENABLED_STATUS : enabled = " + enabled);
    
                        features_enabled.auto_enable_ews = enabled;
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setAutoAddEarlyWarningScoresEnabledStatus(enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_RUN_DEVICES_IN_TEST_MODE:
                    {
                        features_enabled.run_devices_in_test_mode = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setRunDevicesInTestModeEnableStatus(features_enabled.run_devices_in_test_mode);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES:
                    {
                        SensorType sensor_type = getSensorTypeFromIntent(intent);

                        long_term_measurement_timeout = intent.getIntExtra("timeout", 60);

                        Log.d(TAG, "CMD_REPORT_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES : " + sensor_type + " = " + long_term_measurement_timeout);

                        if(current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setLongTermMeasurementTimeout(sensor_type, long_term_measurement_timeout);
                            }
                            else
                            {
                                Log.e(TAG, "CMD_REPORT_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES : FragmentAdminMode instance is NULL!!!!!!");
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_GATEWAY_PLUGGED_IN_STATUS_TIMER_SWITCH_SAFE:
                    {
                        boolean charger_unplugged = intent.getBooleanExtra("charger_unplugged", false);
    
                        Log.e(TAG, "CMD_REPORT_GATEWAY_PLUGGED_IN_STATUS_TIMER_SWITCH_SAFE : (10 min average) charger_unplugged = " + charger_unplugged);
    
                        if (features_enabled.unplugged_overlay)
                        {
                            if (charger_unplugged == false)
                            {
                                showUnpluggedOverlay(false);
                            }
                            else
                            {
                                long time_now = getNtpTimeNowInMilliseconds();
    
                                if (unpluggedOverlayTimeoutPassed(time_now))
                                {
                                    last_time_unplugged_overlay_shown = time_now;
                                    showUnpluggedOverlay(true);
                                }
                            }
                        }
                    }
                    break;
    
                    case CMD_FROM_SERVER__ENABLE_UI_NIGHT_MODE:
                    {
                        boolean enabled = getEnabledFromIntent(intent);
    
                        Log.e(TAG, "CMD_ENABLE_UI_NIGHT_MODE_FROM_SERVER : enabled = " + enabled);
    
                        enableNightModeInFooter(enabled);
                    }
                    break;
    
                    case CMD_REPORT_DATA_MATRIX_VALIDATION_RESULT:
                    {
                        boolean validity = intent.getBooleanExtra("validity", false);
    
                        if (validity)
                        {
                            long human_readable_device_id = intent.getLongExtra("human_readable_product_id", 0);
                            String bluetooth_device_address = intent.getStringExtra("bluetooth_device_address");
                            BarcodeDeviceType barcode_type = BarcodeDeviceType.values()[intent.getIntExtra("barcode_type", BarcodeDeviceType.BARCODE_TYPE__INVALID.ordinal())];
    
                            String gtin = intent.getStringExtra("GTIN");
                            String lot_number = intent.getStringExtra("lot_number");
                            String manufacture_date = intent.getStringExtra("manufacture_date");
                            String expiration_date = intent.getStringExtra("expiration_date");
                            String serial_number = intent.getStringExtra("serial_number");
    
                            Log.d(TAG, "Barcode Type = " + barcode_type.toString());
                            Log.d(TAG, "Human Readable Product ID = " + human_readable_device_id);
                            Log.d(TAG, "Bluetooth Device Address = " + bluetooth_device_address);
                            Log.d(TAG, "GTIN = " + gtin);
                            Log.d(TAG, "serial_number = " + serial_number);
    
                            DateTime manufacture_date_as_datetime = null;
                            DateTime expiration_date_as_datetime = null;
    
                            try
                            {
                                // Dates from Data Matrix are in the format YYMMDD. E.g. 170126 for 26th Jan 2017. The output will be "26 Jan 2017"
                                SimpleDateFormat format = new SimpleDateFormat("yyMMdd", Locale.getDefault());
    
                                manufacture_date_as_datetime = new DateTime(format.parse(manufacture_date));
                                expiration_date_as_datetime = new DateTime(format.parse(expiration_date));
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Problem parsing date in Data Matrix : " + e);
                            }
    
                            switch (barcode_type)
                            {
                                case BARCODE_TYPE__LIFETOUCH:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__LIFETOUCH, DeviceType.DEVICE_TYPE__LIFETOUCH, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;
    
                                case BARCODE_TYPE__LIFETOUCH_BLUE_V2:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__LIFETOUCH, DeviceType.DEVICE_TYPE__LIFETOUCH_BLUE_V2, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;
    
                                case BARCODE_TYPE__LIFETEMP_V2:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__TEMPERATURE, DeviceType.DEVICE_TYPE__LIFETEMP_V2, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;
    
                                case BARCODE_TYPE__NONIN_WRIST_OX:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__SPO2, DeviceType.DEVICE_TYPE__NONIN_WRIST_OX, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;
    
                                case BARCODE_TYPE__AND_UA767:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, DeviceType.DEVICE_TYPE__AND_UA767, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;
    
                                case BARCODE_TYPE__FORA_IR20:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__TEMPERATURE, DeviceType.DEVICE_TYPE__FORA_IR20, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;
    
                                case BARCODE_TYPE__INSTAPATCH:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__LIFETOUCH, DeviceType.DEVICE_TYPE__INSTAPATCH, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;
    
                                case BARCODE_TYPE__LIFETOUCH_THREE:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__LIFETOUCH, DeviceType.DEVICE_TYPE__LIFETOUCH_THREE, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;
    
                                case BARCODE_TYPE__NONIN_3230:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__SPO2, DeviceType.DEVICE_TYPE__NONIN_3230, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;

                                case BARCODE_TYPE__AND_UC352BLE:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__WEIGHT_SCALE, DeviceType.DEVICE_TYPE__AND_UC352BLE, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;

                                case BARCODE_TYPE__MEDLINKET:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__SPO2, DeviceType.DEVICE_TYPE__MEDLINKET, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;

                                case BARCODE_TYPE__AND_UA1200BLE:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, DeviceType.DEVICE_TYPE__AND_UA1200BLE, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;

                                case BARCODE_TYPE__AND_UA656BLE:
                                {
                                    handleDeviceDataMatrix(SensorType.SENSOR_TYPE__BLOOD_PRESSURE, DeviceType.DEVICE_TYPE__AND_UA656BLE, human_readable_device_id, bluetooth_device_address, manufacture_date_as_datetime, expiration_date_as_datetime, lot_number);
                                }
                                break;

                                default:
                                {
                                    Log.e(TAG, "Invalid Barcode Type");
                                }
                                break;
                            }
                        }
                        else
                        {
                            showOnScreenMessage("Invalid Data Matrix code");
                        }
                    }
                    break;
    
                    case CMD_REPORT_MANUFACTURING_MODE_ENABLED_STATUS:
                    {
                        features_enabled.manufacturing_mode = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setManufacturingModeEnabledStatus(features_enabled.manufacturing_mode);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SIMPLE_HEART_RATE_ENABLED_STATUS:
                    {
                        features_enabled.simple_heart_rate = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setSimpleHeartRateEnabledStatus(features_enabled.simple_heart_rate);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_GSM_MODE_ONLY_ENABLED_STATUS:
                    {
                        features_enabled.gsm_only_mode = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setGsmOnlyModeEnabledStatus(features_enabled.gsm_only_mode);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_USB_ACCESSORY_CONNECTION_STATUS:
                    {
                        boolean connected = intent.getBooleanExtra("connected", false);
    
                        //footer_fragment.showUsbAccessoryStatus(connected);
                    }
                    break;
    
                    case CMD_REPORT_SWEETBLUE_DIAGNOSTICS:
                    {
                        int number_of_times_postCompleteBleReset_called = intent.getIntExtra("number_of_times_postCompleteBleReset_called", -1);
                        int number_of_times_postBleResetWithoutRemovingDevices_called = intent.getIntExtra("number_of_times_postBleResetWithoutRemovingDevices_called", -1);
    
                        if(popup_developer_options.getDialog() != null)
                        {
                            if(popup_developer_options.getDialog().isShowing())
                            {
                                popup_developer_options.showPostCompleteBleReset(number_of_times_postCompleteBleReset_called);
                                popup_developer_options.showPostBleResetWithoutRemovingDevices(number_of_times_postBleResetWithoutRemovingDevices_called);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_USE_BACK_CAMERA_ENABLED_STATUS:
                    {
                        features_enabled.use_back_camera = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setUseBackCameraEnableStatus(features_enabled.use_back_camera);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_PATIENT_ORIENTATION_ENABLED_STATUS:
                    {
                        features_enabled.patient_orientation = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setPatientOrientationEnableStatus(features_enabled.patient_orientation);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SHOW_NUMBERS_ON_BATTERY_INDICATOR_ENABLED_STATUS:
                    {
                        features_enabled.show_numbers_of_battery_indicator = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setShowNumbersOnBatteryIndicatorEnableStatus(features_enabled.show_numbers_of_battery_indicator);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SHOW_MAC_ADDRESS_ENABLED_STATUS:
                    {
                        features_enabled.show_mac_address = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setShowMacAddressEnableStatus(features_enabled.show_mac_address);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_USA_MODE_ENABLED_STATUS:
                    {
                        features_enabled.usa_mode = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setUsaModeEnableStatus(features_enabled.usa_mode);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SHOW_LIFETOUCH_ACTIVITY_LEVEL_ENABLED_STATUS:
                    {
                        features_enabled.show_lifetouch_activity_level = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setShowLifetouchActivityLevelEnableStatus(features_enabled.show_lifetouch_activity_level);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_AUTO_RESUME_ENABLED_STATUS:
                    {
                        features_enabled.auto_resume_enabled = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setEnableAutoResume(features_enabled.auto_resume_enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_AUTO_UPLOAD_LOG_FILES_TO_SERVER_ENABLED_STATUS:
                    {
                        features_enabled.auto_logfile_upload_to_server = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setAutoUploadLogFileToServerEnableStatus(features_enabled.auto_logfile_upload_to_server);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_WIFI_LOGGING_ENABLED_STATUS:
                    {
                        features_enabled.wifi_logging_enabled = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setEnableWifiLogging(features_enabled.wifi_logging_enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_GSM_LOGGING_ENABLED_STATUS:
                    {
                        features_enabled.gsm_logging_enabled = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setEnableGsmLogging(features_enabled.gsm_logging_enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DATABASE_LOGGING_ENABLED_STATUS:
                    {
                        features_enabled.database_logging_enabled = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setEnableDatabaseLogging(features_enabled.database_logging_enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SERVER_LOGGING_ENABLED_STATUS:
                    {
                        features_enabled.server_logging_enabled = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setEnableServerLogging(features_enabled.server_logging_enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_BATTERY_LOGGING_ENABLED_STATUS:
                    {
                        features_enabled.battery_logging_enabled = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setEnableBatteryLogging(features_enabled.battery_logging_enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DFU_BOOTLOADER_ENABLED_STATUS:
                    {
                        features_enabled.dfu_bootloader_enabled = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDfuBootloaderEnabledStatus(features_enabled.dfu_bootloader_enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS:
                    {
                        features_enabled.spot_spot_measurements_enabled = getEnabledFromIntent(intent);
    
                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setSpO2SpotMeasurementsEnabledStatus(features_enabled.spot_spot_measurements_enabled);
                            }
                        }
                    }
                    break;
    
                    case CMD_REPORT_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS:
                    {
                        features_enabled.show_temperature_in_fahrenheit = getEnabledFromIntent(intent);
    
                        Log.d(TAG, "CMD_REPORT_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS : show_temperature_in_fahrenheit = " + features_enabled.show_temperature_in_fahrenheit);
    
                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
    
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDisplayTemperatureInFahrenheitEnabledStatus(features_enabled.show_temperature_in_fahrenheit);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS:
                    {
                        features_enabled.show_weight_in_lbs = getEnabledFromIntent(intent);

                        DeviceInfo device_info = getDeviceInfoForVitalSign(VitalSignType.WEIGHT);
                        device_info.show_weight_in_lbs = features_enabled.show_weight_in_lbs;

                        addOrUpdateDeviceInfo(device_info);

                        Log.d(TAG, "CMD_REPORT_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS : show_weight_in_lbs = " + features_enabled.show_weight_in_lbs);

                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setDisplayWeightInLbsEnabledStatus(features_enabled.show_weight_in_lbs);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_PREDEFINED_ANNOTATION_ENABLED_STATUS:
                    {
                        features_enabled.predefined_annotations_enabled = getEnabledFromIntent(intent);

                        if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setPredefinedAnnotationsEnabledStatus(features_enabled.predefined_annotations_enabled);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS:
                    {
                        lifetemp_measurement_interval_in_seconds = intent.getIntExtra("measurement_interval_in_seconds", 60);

                        Log.d(TAG, "CMD_REPORT_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS : lifetemp_measurement_interval_in_seconds = " + lifetemp_measurement_interval_in_seconds);

                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setLifetempMeasurementInterval(lifetemp_measurement_interval_in_seconds);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_DEVELOPER_POPUP_ENABLED_STATUS:
                    {
                        features_enabled.developer_popup = getEnabledFromIntent(intent);

                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setCheckBoxEnableDeveloperPopup(features_enabled.developer_popup);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS:
                    {
                        patient_orientation_measurement_interval_in_seconds = intent.getIntExtra("measurement_interval_in_seconds", 60);

                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setPatientOrientationMeasurementInterval(patient_orientation_measurement_interval_in_seconds);
                            }
                        }

                        Log.d(TAG, "CMD_REPORT_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS : patient_orientation_measurement_interval_in_seconds = " + patient_orientation_measurement_interval_in_seconds);
                    }
                    break;

                    case CMD_REPORT_SHOW_IP_ADDRESS_ON_WIFI_POPUP_ENABLED_STATUS:
                    {
                        features_enabled.show_ip_address_on_wifi_popup = getEnabledFromIntent(intent);

                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setCheckBoxShowIpAddressOnWifiPopup(features_enabled.show_ip_address_on_wifi_popup);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_GATEWAY_CHARGE_STATUS:
                    {
                        int android_percentage = intent.getIntExtra("android_percentage", 0);
                        boolean charging = intent.getBooleanExtra("charging", false);
                        int current_avg = intent.getIntExtra("current_avg", 0);
                        int voltage = intent.getIntExtra("voltage", 0);
                        float temperature = intent.getFloatExtra("temperature", 0);

                        // If the brightness setting in tablet is set to automatic then we don't know the correct brightness
                        // Printing screen brightness every 10 secs gives us information to determine the power loss because of high brightness
                        try
                        {
                            float curBrightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                            Log.d(TAG, "Current Screen Brightness = " + curBrightnessValue);
                        }
                        catch (SettingNotFoundException e)
                        {
                            e.printStackTrace();
                        }

                        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                        {
                            Log.d(TAG, "Updating battery image. android_percentage = " + android_percentage + " Charging = " + charging + " Temperature = " + temperature);
                            footer_fragment.setAndroidBatteryLevel(android_percentage, charging);
                            footer_fragment.setAndroidBatteryCurrent(current_avg);
                            footer_fragment.setAndroidBatteryVoltage(voltage);
                            footer_fragment.setAndroidBatteryTemperature(temperature);
                        }
                    }
                    break;

                    case CMD_REPORT_DFU_PROGRESS:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        SensorType sensor_type = getSensorTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(sensor_type);
                        int progress = intent.getIntExtra("progress", 50);

                        Log.d(TAG, sensor_type + " : " + device_info.getSensorTypeAndDeviceTypeAsString() + " DFU progress = " + progress);

                        if (current_page == UserInterfacePage.DEVICE_CONNECTION)
                        {
                            FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showFirmwareUpdate(device_info, progress);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_INSTALLATION_COMPLETE:
                    {
                        features_enabled.gateway_setup_complete = intent.getBooleanExtra("complete", false);

                        Log.d(TAG, "CMD_REPORT_INSTALLATION_COMPLETE : features_enabled.gateway_setup_complete = " + features_enabled.gateway_setup_complete + " : current_page = " + current_page);

                        // This will be reported by the Gateway as False if clearing out the Database (including EWS) that happens when changing Server IP/Port
                        if(current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            // Do nothing
                        }
                        else if((current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                                ||(current_page == UserInterfacePage.INSTALLATION_MODE_WELCOME)
                                ||(current_page == UserInterfacePage.INSTALLATION_MODE_SERVER_ADDRESS_SCAN))
                        {
                            if (needToStartInstallationWizard())
                            {
                                // We have already started installation mode, so don't restart it now.
                            }
                            else
                            {
                                lockScreenSelected();
                            }
                        }
                        else if (permissions.checkForRequiredAndroidPermissions() == false)
                        {
                            showAndroidPermissionsFragment();
                        }
                        else if (needToStartInstallationWizard())
                        {
                            startInstallationWizard();
                        }
                        else
                        {
                            // Installation is complete and we're not on an installation page so there's nothing to do
                        }
                    }
                    break;

                    case CMD_REPORT_CHECK_FOR_LATEST_FIRMWARE_IMAGES_FROM_SERVER_COMPLETE:
                    {
                        boolean success = intent.getBooleanExtra("success", false);

                        ArrayList<FirmwareImage> firmware_image_list = intent.getParcelableArrayListExtra("firmware_image_list");

                        for(FirmwareImage firmware : firmware_image_list)
                        {
                            handleSoftwareUpdateReported(firmware.device_type, firmware.latest_stored_firmware_version, firmware.file_name);
                        }

                        if (current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS)
                        {
                            FragmentInstallationProgress fragment = (FragmentInstallationProgress) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showGetUpdatedFirmwareResult(success);
                            }
                        }
                        else if (current_page == UserInterfacePage.ADMIN_MODE)
                        {
                            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showGetUpdatedFirmwareResult(success, firmware_image_list);
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_DEVICE_SETUP_MODE_HISTORY:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        SensorType sensor_type = getSensorTypeFromIntent(intent);

                        SetupModeLog setup_mode_log = intent.getParcelableExtra("setup_mode_log");

                        Log.d(TAG, "Log CMD_REPORT_DEVICE_SETUP_MODE_HISTORY : " + sensor_type + " : " + device_type + " : " + TimestampConversion.convertDateToHumanReadableStringDayHoursMinutesSeconds(setup_mode_log.start_time) + " : " + TimestampConversion.convertDateToHumanReadableStringDayHoursMinutesSeconds(setup_mode_log.end_time));

                        ArrayList<SetupModeLog> new_item_list = new ArrayList<>();
                        new_item_list.add(setup_mode_log);

                        // Remove duplicates and sort in time order
                        setup_mode_log_cache.updateCachedVitalsListAndSortInTimeOrder(sensor_type, new_item_list);

                        if(current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.redrawGraphSetupModeIndicators(SensorType.SENSOR_TYPE__LIFETOUCH);
                                fragment.redrawGraphSetupModeIndicators(SensorType.SENSOR_TYPE__SPO2);
                            }

                            if (UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                            {
                                footer_fragment.setBackButtonAsShowOrHideSetupModeBlobsVisible(true);
                            }
                        }

                        // Stops crash if setup mode starts while history popup is displayed and shows new setup mode sessions when they finish
                        if (popup_historical_setup_mode_viewer.adapter != null)
                        {
                            popup_historical_setup_mode_viewer.adapter.notifyDataSetChanged();
                        }
                    }
                    break;

                    case CMD_REPORT_LOCATION_ENABLED:
                    {
                        location_services_turned_on = getEnabledFromIntent(intent);

                        if(location_services_turned_on == false)
                        {
                            showLocationServicesOffError();
                        }
                    }
                    break;

                    case CMD_REPORT_MANUAL_VITAL_SIGN_DEVICE_INFO:
                    {
                        DeviceType device_type = getDeviceTypeFromIntent(intent);
                        DeviceInfo device_info = getDeviceByType(device_type);

                        device_info.setDeviceTypePartOfPatientSession(intent.getBooleanExtra("show_on_ui", false));
                        device_info.sensor_type = SensorType.values()[intent.getIntExtra("sensor_type", SensorType.SENSOR_TYPE__INVALID.ordinal())];

                        outputDeviceInfo("CMD_REPORT_MANUAL_VITAL_SIGN_DEVICE_INFO", device_info);

                        addOrUpdateDeviceInfo(device_info);
                    }
                    break;

                    case CMD_REPORT_REALTIME_SERVER_TYPE:
                    {
                        int server_type = intent.getIntExtra("server_type", 0);
                        features_enabled.realtime_server_type = RealTimeServer.values()[server_type];
                    }
                    break;

                    case CMD_TEST_ONLY_STOP_FAST_UI_UPDATES:
                    {
                        Log.d(TAG, "CMD_TEST_ONLY_STOP_FAST_UI_UPDATES");

                        features_enabled.stop_fast_ui_updates = true;

                        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                        {
                            footer_fragment.runInSlowUpdateMode();
                        }
                        else
                        {
                            Log.e(TAG, "runClockTickInSlowMode : footer_fragment null or not added");
                        }
                    }
                    break;

                    case CMD_REPORT_NONIN_PLAYBACK_IS_ONGOING:
                    {
                        boolean nonin_playback_is_ongoing = intent.getBooleanExtra("playback_has_started", false);

                        DeviceInfo deviceInfo = getDeviceByType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE);

                        deviceInfo.nonin_playback_ongoing = nonin_playback_is_ongoing;

                        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.deviceStateChange(getDeviceByType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE));
                            }
                        }

                        Log.d(TAG, "CMD_REPORT_NONIN_PLAYBACK_IS_ONGOING : nonin_playback_is_ongoing = " + nonin_playback_is_ongoing);
                    }
                    break;

                    case CMD_REPORT_NONIN_PLAYBACK_MIGHT_OCCUR:
                    {
                        Log.d(TAG, "CMD_REPORT_NONIN_PLAYBACK_MIGHT_OCCUR");

                        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.deviceStateChange(getDeviceByType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX_BTLE));
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_SOFTWARE_UPDATE_AVAILABLE:
                    {
                        Log.d(TAG, "CMD_REPORT_SOFTWARE_UPDATE_AVAILABLE");

                        handleSoftwareUpdateReported(intent);
                    }
                    break;

                    case CMD_REPORT_IN_SOFTWARE_UPDATE_MODE:
                    {
                        if(permissions.checkForRequiredAndroidPermissions() == false)
                        {
                            Log.e(TAG, "Permissions are not setup yet. Ignoring CMD_REPORT_IN_SOFTWARE_UPDATE_MODE");
                        }
                        else
                        {
                            Log.d(TAG, "CMD_REPORT_IN_SOFTWARE_UPDATE_MODE : Update Mode : Setting up Update receiver");

                            software_update_mode_active = true;

                            // Deregister regular broadcast receiver
                            unregisterReceiver(broadcastReceiverIncomingCommandsFromPatientGateway);

                            // Register update mode receiver
                            registerReceiver(broadcastReceiverIncomingCommandsFromPatientGateway__updateMode, new IntentFilter(INTENT__COMMANDS_TO_USER_INTERFACE));

                            if(footer_fragment != null)
                            {
                                Log.d(TAG, "Update Mode : Hiding footer controls");
                                footer_fragment.hideControlsDueToUpdateMode(true);
                            }

                            showSoftwareUpdateMode();
                        }
                    }
                    break;

                    case CMD_TEST_ONLY_SPOOF_SOFTWARE_UPDATE_INSTALLED:
                    {
                        Log.d(TAG, "CMD_TEST_ONLY_SPOOF_SOFTWARE_UPDATE_INSTALLED");

                        handleSpoofUpdateInstalled(intent);
                    }
                    break;

                    case CMD_SHOW_SERVER_SYNCING_POPUP:
                    {
                        boolean enabled = getEnabledFromIntent(intent);

                        if (enabled)
                        {
                            showSyncStatusPopup();
                        }
                        else
                        {
                            popup_server_syncing.dismissWindow();
                        }
                    }
                    break;

                    case CMD_FORWARD_VIDEO_CALL_REQUEST_TO_USER_INTERFACE:
                    {
                        // Only show the incoming call popup if NOT in a call already
                        if (session == null)
                        {
                            videoCallDetails = intent.getParcelableExtra("video_call_details");

                            videoCallDetails.display_name = patient_info.getHospitalPatientId()  + " : " + patient_gateways_assigned_bed_name + " : GW " + BluetoothAdapter.getDefaultAdapter().getName();

                            closeAllPopups();

                            if (videoCallDetails.cancel)
                            {
                                stopRingingAudio();

                                popup_video_call.dismissPopupIfVisible();
                            }
                            else
                            {
                                showIncomingVideoCallPopup(videoCallDetails.from_text, videoCallDetails.missed_call_text, videoCallDetails.ring_length_in_seconds);
                            }
                        }
                        else
                        {
                            Log.e(TAG, "Ignoring CMD_FORWARD_VIDEO_CALL_REQUEST_TO_USER_INTERFACE as in call already");
                        }
                    }
                    break;

                    case CMD_FORWARD_VIDEO_CALL_JOIN_TO_USER_INTERFACE:
                    {
                        videoCallDetails = intent.getParcelableExtra("video_call_details");

                        Log.d(TAG, "CMD_FORWARD_VIDEO_CALL_JOIN_TO_USER_INTERFACE : " + videoCallDetails.toString());

                        videoCallDetails.display_name = patient_info.getHospitalPatientId()  + " : " + patient_gateways_assigned_bed_name + " : GW " + BluetoothAdapter.getDefaultAdapter().getName();

                        stopRingingAudio();

                        if (videoCallDetails.cancel)
                        {
                            Log.d(TAG, "CMD_FORWARD_VIDEO_CALL_JOIN_TO_USER_INTERFACE : Cancel");

                            leaveVideoCall();

                            closeAllPopups();
                        }
                        else
                        {
                            Log.d(TAG, "CMD_FORWARD_VIDEO_CALL_JOIN_TO_USER_INTERFACE : VIDEO : connectToOpenViduServer");

                            connectToOpenViduServer(videoCallDetails);

                            popup_video_call.showMeetingViewIncomingCall();
                        }
                    }
                    break;

                    case CMD_FORWARD_VIDEO_CALL_DECLINED_TO_USER_INTERFACE:
                    {
                        VideoCallContact contactThatDeclinedTheCall = intent.getParcelableExtra("contactThatDeclinedTheCall");

                        Log.d(TAG, "CMD_FORWARD_VIDEO_CALL_DECLINED_TO_USER_INTERFACE : contactThatDeclinedTheCall = " + contactThatDeclinedTheCall.toString());

                        Log.d(TAG, "CMD_FORWARD_VIDEO_CALL_DECLINED_TO_USER_INTERFACE : requestedVideoCallContacts before = " + requestedVideoCallContacts.size());

                        // Remove the Lifeguard user that declined the call from the list of contacts for this call.
                        // Once there are none left then can show the "Declined Call" text on the popup
                        requestedVideoCallContacts.removeIf(n -> (n.name.equals(contactThatDeclinedTheCall.name)));

                        Log.d(TAG, "CMD_FORWARD_VIDEO_CALL_DECLINED_TO_USER_INTERFACE : requestedVideoCallContacts after = " + requestedVideoCallContacts.size());

                        // Command is only processed if NOT in a call already as dont want this to be shown. If in a call then session is NOT null
                        if (session == null && requestedVideoCallContacts.size() == 0)
                        {
                            Log.d(TAG, "CMD_FORWARD_VIDEO_CALL_DECLINED_TO_USER_INTERFACE : Not in call and no contacts still ringing. Showing Declined Call");

                            // After "missed call" seconds the Popup will time out and show "No answer. Please try again later".
                            // So rather than wait, can just display that now.
                            popup_video_call.declinedCall();
                        }
                        else
                        {
                            Log.d(TAG, "CMD_FORWARD_VIDEO_CALL_DECLINED_TO_USER_INTERFACE : In call. Ignoring");
                        }
                    }
                    break;

                    case CMD_FORWARD_VIDEO_CALL_BROWSER_CONNECTION_ID_TO_USER_INTERFACE:
                    {
                        Log.d(TAG, "CMD_FORWARD_VIDEO_CALL_BROWSER_CONNECTION_ID_TO_USER_INTERFACE");

                        String connectionId = intent.getStringExtra("connectionId");
                        videoCallDetails.connection_id = connectionId;
                    }
                    break;

                    case CMD_REPORT_BLUETOOTH_OFF_ERROR:
                    {
                        Log.d(TAG, "CMD_REPORT_BLUETOOTH_OFF_ERROR");

                        if(current_page == UserInterfacePage.DEVICE_CONNECTION)
                        {
                            showBluetoothError();
                        }
                    }
                    break;

                    case CMD_REPORT_VIDEO_CALLS_ENABLED_STATUS:
                    {
                        features_enabled.video_calls_enabled = getEnabledFromIntent(intent);

                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setVideoCallsEnabledStatus(features_enabled.video_calls_enabled);
                            }
                        }
                        else if (current_page == UserInterfacePage.UNLOCK_SCREEN)
                        {
                            FragmentQrCodeUnlock fragment = (FragmentQrCodeUnlock) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.showStartVideoCallButtonIfNeeded();
                            }
                        }
                    }
                    break;

                    case CMD_REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_RECEIVED_FROM_SERVER:
                    {
                        boolean success = intent.getBooleanExtra("success", false);

                        if (success)
                        {
                            Log.d(TAG, "CMD_REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_RECEIVED_FROM_SERVER success");

                            ArrayList<VideoCallContact> received_contacts = intent.getParcelableArrayListExtra("contacts");

                            Log.d(TAG, "CMD_REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_RECEIVED_FROM_SERVER received_contacts size " + received_contacts.size());

                            for(VideoCallContact contact : received_contacts)
                            {
                                Log.d(TAG, "CMD_REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_RECEIVED_FROM_SERVER : Received contact = " + contact.name + " : " + contact.email + " : " + contact.available);
                            }

                            if (current_page == UserInterfacePage.VIDEO_CALL_CONTACTS || current_page == UserInterfacePage.VIDEO_CALL_CONTACTS_FROM_UNLOCK_PAGE)
                            {
                                FragmentVideoCallContactSelectionList fragment = (FragmentVideoCallContactSelectionList) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    fragment.showContacts(received_contacts);
                                }
                            }
                        }
                        else
                        {
                            Log.e(TAG, "CMD_REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_RECEIVED_FROM_SERVER failed");

                            // Tell the user that the contact lookup has failed
                            if (current_page == UserInterfacePage.VIDEO_CALL_CONTACTS || current_page == UserInterfacePage.VIDEO_CALL_CONTACTS_FROM_UNLOCK_PAGE)
                            {
                                FragmentVideoCallContactSelectionList fragment = (FragmentVideoCallContactSelectionList) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                                {
                                    //fragment.showContacts(received_contacts);
                                }
                            }
                        }
                    }
                    break;

                    case CMD_TELL_USER_INTERFACE_TO_EXIT:
                    {
                        adminModeExitPressed();
                    }
                    break;

                    case CMD_REPORT_FREE_DISK_SPACE:
                    {
                        int free_percentage = intent.getIntExtra("free_percentage", -1);

                        Log.d(TAG, "CMD_REPORT_FREE_DISK_SPACE = " + free_percentage);

                        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                        {
                            footer_fragment.showFreeDiskSpace(free_percentage);
                        }
                    }
                    break;

                    case CMD_RECALCULATE_THRESHOLDS_AFTER_SERVER_CONFIG_RECEIVED:
                    {
                        Log.d(TAG, "CMD_RECALCULATE_THRESHOLDS_AFTER_SERVER_CONFIG_RECEIVED");

                        getEarlyWarningScoringSetsFromPatientGatewayDatabase();
                    }
                    break;

                    case CMD_REPORT_VIEW_WEBPAGES_ENABLED_STATUS:
                    {
                        features_enabled.view_webpages_enabled = getEnabledFromIntent(intent);

                        Log.d(TAG, "features_enabled.view_webpages_enabled = " +features_enabled.view_webpages_enabled);

                        if (current_page == UserInterfacePage.FEATURE_ENABLE_MODE)
                        {
                            FragmentFeatureEnable fragment = (FragmentFeatureEnable) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setViewWebPagesEnabledStatus(features_enabled.view_webpages_enabled);
                            }
                        }
                    }
                    break;

                    default:
                    {
                        Log.e(TAG, "Unknown command (" + incoming_command + ") but unhandled");
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

    private void processWebpagesThatUserCanVisit(ArrayList<WebPageDescriptor> receivedWebpageDescriptors)
    {
        Drawable blueButton = ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_blue);

        cached_webpages.clear();

        for (WebPageDescriptor x : receivedWebpageDescriptors)
        {
            cached_webpages.add(new WebPageButtonDescriptor(x.description, x.url, blueButton));
        }
    }

    // Gateway has told the UI that new data has been received from the Lifeguard server
    private void handleCachedDataUpdated(Intent intent)
    {
        int query_type_as_int = intent.getIntExtra("query_type", -1);
        QueryType type = QueryType.values()[query_type_as_int];

        /* Trigger a database query for the threshold sets */
        Log.d(TAG, "handleCachedDataUpdated: Cached data of type " + type + " reported as updated in database. Querying DB for new versions");

        switch(type)
        {
            case WARDS:
            case BEDS:
            {
                // Not implemented yet. ToDo RM
            }
            break;

            case SERVER_CONFIGURABLE_TEXT:
            {
                queryServerConfigurableText();
            }
            break;

            case EARLY_WARNING_SCORE_THRESHOLD_SETS:
            {
                getEarlyWarningScoringSetsFromPatientGatewayDatabase();
            }
            break;

            case WEBPAGES:
            {
                queryWebPages();
            }
            break;
        }
    }


    private boolean getEnabledFromIntent(Intent intent)
    {
        return intent.getBooleanExtra("enabled", false);
    }


    private final BroadcastReceiver broadcastReceiverIncomingCommandsFromPatientGateway__updateMode = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int command = intent.getIntExtra("command", 0);

            try
            {
                Commands incoming_command = Commands.values()[command];

                switch (incoming_command)
                {
                    case CMD_CHECK_GATEWAY_UI_CONNECTION:
                    {
                        // Remove the "timeout" runnable now the Patient Gateway has respond
                        handler_gateway_not_responding.removeCallbacksAndMessages(null);

                        // Reset the timeout counter
                        patient_gateway_not_responding_counter = 0;

                        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
                        {
                            footer_fragment.showSystemStatusStatus(true);
                        }
                        else
                        {
                            Log.e(TAG, "CMD_CHECK_GATEWAY_UI_CONNECTION : footer_fragment = null" );
                        }

                        if(current_page == UserInterfacePage.GATEWAY_NOT_RESPONDING)
                        {
                            lockScreenSelected();
                        }
                    }
                    break;

                    case CMD_REPORT_SOFTWARE_UPDATE_AVAILABLE:
                    {
                        Log.d(TAG, "CMD_REPORT_SOFTWARE_UPDATE_AVAILABLE");

                        handleSoftwareUpdateReported(intent);
                    }
                    break;

                    case CMD_TEST_ONLY_SPOOF_SOFTWARE_UPDATE_INSTALLED:
                    {
                        Log.d(TAG, "CMD_TEST_ONLY_SPOOF_SOFTWARE_UPDATE_INSTALLED");

                        handleSpoofUpdateInstalled(intent);
                    }
                    break;

                    case CMD_REPORT_CACHED_DATA_UPDATED:
                    {
                        handleCachedDataUpdated(intent);
                    }
                    break;

                    default:
                    {

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


    private void handleSoftwareUpdateReported(Intent intent)
    {
        int available_software_version = intent.getIntExtra("available_version", 0);
        DeviceType type = DeviceType.values()[intent.getIntExtra("device_type", 0)];
        String name = intent.getStringExtra("apk_name");

        handleSoftwareUpdateReported(type, available_software_version, name);
    }

    /**
     * Used to spoof an app installation for testing Update Mode
     */
    private void handleSpoofUpdateInstalled(Intent intent)
    {
        int available_software_version = intent.getIntExtra("available_version", 0);
        DeviceType type = DeviceType.values()[intent.getIntExtra("device_type", 0)];

        switch(type)
        {
            case DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY:
            {
                updateModeStatus.available_gateway_version = available_software_version;
            }
            break;

            case DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE:
            {
                updateModeStatus.available_ui_version = available_software_version;
            }
            break;

            default:
            {
                Log.w(TAG, "CMD_TEST_ONLY_SPOOF_ALL_UPDATES_INSTALLED - incorrect device type");
            }
            break;
        }
    }

    private void handleSoftwareUpdateReported(DeviceType type, int version, String apk)
    {
        switch(type)
        {
            case DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY:
            {
                if(version > updateModeStatus.available_gateway_version)
                {
                    updateModeStatus.setGatewayVersionDetails(version, apk);

                    Log.d(TAG, "handleSoftwareUpdateReported : " + updateModeStatus.getGatewayInfoForComment());
                }
            }
            break;

            case DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE:
            {
                if(version > updateModeStatus.available_ui_version)
                {
                    updateModeStatus.setUserInterfaceVersionDetails(version, apk);

                    Log.d(TAG, "handleSoftwareUpdateReported : " + updateModeStatus.getUserInterfaceInfoForComment());
                }
            }
            break;

            default:
            {
                // Do nothing
            }
            break;
        }

        if(isSoftwareUpdateAvailableAndNoSessionRunning() && current_page == UserInterfacePage.UNLOCK_SCREEN)
        {
            FragmentQrCodeUnlock fragment = (FragmentQrCodeUnlock) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.showSoftwareUpdatesPending();
            }
        }
    }

    private static final String PACKAGE_INSTALLED_ACTION = "com.isansys.pse_isansysportal.SESSION_API_PACKAGE_INSTALLED";

    public boolean installLocalApk(String apk_name)
    {
        PackageInstaller.Session session = null;
        try
        {
            PackageInstaller packageInstaller = getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            int sessionId = packageInstaller.createSession(params);
            session = packageInstaller.openSession(sessionId);

            String filepath = Environment.getExternalStorageDirectory() + File.separator + "firmware_images" + File.separator;
            String apk = filepath + apk_name;
            File apk_file = new File(apk);

            Log.d(TAG, "installLocalApk : apk = " + apk);

            if(apk_file.exists())
            {
                Log.d(TAG, "installLocalApk : Exists");

                addApkToInstallSession(apk_file, session);
                // Create an install status receiver.
                Context context = getAppContext();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setAction(PACKAGE_INSTALLED_ACTION);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, sessionId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                IntentSender statusReceiver = pendingIntent.getIntentSender();

                Log.d(TAG, "installLocalApk : committing installation for apk " + apk_name);
                // Commit the session (this will start the installation workflow).
                session.commit(statusReceiver);

                session.close();

                return true;
            }
            else
            {
                Log.e(TAG, "installLocalApk : APK " + apk_name + " Not Found");

                if (session != null)
                {
                    session.abandon();
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Couldn't install package", e);
        }
        catch (RuntimeException e)
        {
            if (session != null)
            {
                session.abandon();
            }

            Log.e(TAG, "installLocalApk : error installing " + apk_name);
            throw e;
        }

        return false;
    }


    private void addApkToInstallSession(File install_apk, PackageInstaller.Session session) throws IOException
    {
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        try (OutputStream packageInSession = session.openWrite("package", 0, -1); InputStream is = new FileInputStream(install_apk))
        {
            byte[] buffer = new byte[16384];
            int n;
            while ((n = is.read(buffer)) >= 0)
            {
                packageInSession.write(buffer, 0, n);
            }
        }
    }


    // Note: this Activity must run in singleTop launchMode for it to be able to receive the intent
    // in onNewIntent().
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        Log.d(TAG, "onNewIntent : Received action " + action);

        if (PACKAGE_INSTALLED_ACTION.equals(action))
		{
            Log.d(TAG, "onNewIntent : Received PACKAGE_INSTALLED_ACTION");

            int status = extras.getInt(PackageInstaller.EXTRA_STATUS);
            String message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);

            Log.d(TAG, "onNewIntent : message = " + message);

            switch (status)
            {
                case PackageInstaller.STATUS_PENDING_USER_ACTION:
                {
                    Log.d(TAG, "onNewIntent : STATUS_PENDING_USER_ACTION");

                    // Make sure any previous "FAILED"s are gone
                    updateModeStatus.setApkInstallStatus(UpdateModeStatus.ApkInstallStatus.NONE);

                    // Our apps are not "privileged", so the user has to confirm the install.
                    // An Android popup will now appear on the screen saying
                    // "Do you want to install an update to this existing application?"
                    // "Your existing data will not be lost"
                    Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
                    startActivity(confirmIntent);
                }
                break;

                case PackageInstaller.STATUS_SUCCESS:
                {
                    Log.d(TAG, "onNewIntent : STATUS_SUCCESS");

                    Log.d(TAG, "onNewIntent : Install succeeded!");

                    // When newIntent fires, that UI has not resumed properly yet, so it cant restart the Gateway from here

                    // This only gets called for the Gateway - as obviously when the UI updates, there is not anything to get the intent
                    updateModeStatus.setApkInstallStatus(UpdateModeStatus.ApkInstallStatus.INSTALLED_RESTART_GATEWAY);
                }
                break;

                case PackageInstaller.STATUS_FAILURE:
                case PackageInstaller.STATUS_FAILURE_BLOCKED:
                case PackageInstaller.STATUS_FAILURE_CONFLICT:
                case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
                case PackageInstaller.STATUS_FAILURE_INVALID:
                case PackageInstaller.STATUS_FAILURE_STORAGE:
                {
                    Log.e(TAG, "onNewIntent : Install FAILED : " + current_page + " : status = " + status + " : " + message);

                    // Passed to FragmentUpdateMode in a bundle when its (re)created
                    updateModeStatus.setApkInstallStatus(UpdateModeStatus.ApkInstallStatus.FAILED);
                 }
                 break;

                case PackageInstaller.STATUS_FAILURE_ABORTED:
                {
                    Log.e(TAG, "onNewIntent : Install ABORTED : " + current_page + " : status = " + status + " : " + message);

                    // Passed to FragmentUpdateMode in a bundle when its (re)created
                    updateModeStatus.setApkInstallStatus(UpdateModeStatus.ApkInstallStatus.ABORTED);
                }
                break;

                default:
                    Log.d(TAG, "onNewIntent : Unrecognized status received from installer: " + status);
            }
        }
    }


    private void handlePeriodicModeChange(DeviceInfo device_info, boolean in_periodic_setup_mode)
    {
        Log.d(TAG, "handlePeriodicModeChange " + device_info.getSensorTypeAndDeviceTypeAsString() + " : in_periodic_setup_mode = " + in_periodic_setup_mode);

        device_info.setDeviceInPeriodicSetupMode(in_periodic_setup_mode);

        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
        {
            if(isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO())
            {
                Log.w(TAG, "handlePeriodicModeChange " + device_info.getSensorTypeAndDeviceTypeAsString() + " : isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO = true");
            }
            else
            {
                FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    if(in_periodic_setup_mode)
                    {
                        if (device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
                        {
                            closePoincarePopupIfShowing();
                        }

                        fragment.setupCheckboxForPeriodicSetupMode(device_info);
                    }
                }
            }
        }

        refreshDeviceSetupModeStateOnScreen(device_info);
    }


    private void handleLeadsOffStatusChange(DeviceInfo device_info, boolean not_connected_to_patient)
    {
        // Need to save status to when the Patient Vitals Display loads it can read the current status
        device_info.setDeviceOffBody(not_connected_to_patient);

        if ((device_info.hasDeviceOffBodyStateChanged()) && (device_info.isDeviceHumanReadableDeviceIdValid()))
        {
            refreshLeadsStateOnScreen(device_info);
        }

        device_info.updatePreviousValueOfIsDeviceOffBodyState();
    }


    private void processReportedDeviceInfo(Intent intent)
    {
        DeviceType device_type = getDeviceTypeFromIntent(intent);
        DeviceInfo device_info = getDeviceByType(device_type);

        device_info.radio_type = RadioType.values()[intent.getIntExtra("radio_type", RadioType.RADIO_TYPE__NONE.ordinal())];
        device_info.sensor_type = SensorType.values()[intent.getIntExtra("sensor_type", SensorType.SENSOR_TYPE__INVALID.ordinal())];
        device_info.human_readable_device_id = intent.getLongExtra("human_readable_device_id", INVALID_HUMAN_READABLE_DEVICE_ID);
        device_info.bluetooth_address = intent.getStringExtra("bluetooth_address");
        device_info.device_name = intent.getStringExtra("device_name");
        device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.values()[intent.getIntExtra("actual_device_connection_status", DeviceConnectionStatus.NOT_PAIRED.ordinal())]);
        device_info.measurement_interval_in_seconds = intent.getIntExtra("measurement_interval_in_seconds", -1);
        device_info.setup_mode_time_in_seconds = intent.getIntExtra("setup_mode_time_in_seconds", -1);
        device_info.setDeviceTypePartOfPatientSession(intent.getBooleanExtra("show_on_ui", false));
        device_info.dummy_data_mode = intent.getBooleanExtra("dummy_data_mode", false);
        device_info.firmware_version = intent.getIntExtra("firmware_version", INVALID_FIRMWARE_VERSION);
        device_info.firmware_string = intent.getStringExtra("firmware_string");
        device_info.last_battery_reading_percentage = intent.getIntExtra("last_battery_reading_percentage", 0);
        device_info.last_battery_reading_in_millivolts = intent.getIntExtra("last_battery_reading_in_millivolts", 0);
        device_info.last_battery_reading_received_timestamp = intent.getLongExtra("last_battery_reading_received_timestamp", 0);
        device_info.android_database_device_session_id = intent.getIntExtra("android_database_device_session_id", INVALID_DEVICE_SESSION_ID);

        device_info.lot_number = intent.getStringExtra("lot_number");
        if (device_info.lot_number == null)
        {
            device_info.lot_number = NO_LOT_NUMBER;
        }

        device_info.manufacture_date = new DateTime(intent.getLongExtra("manufacture_date_in_millis", 0));
        device_info.expiration_date = new DateTime(intent.getLongExtra("expiration_date_in_millis", 0));

        /// Specific to Nonin
        device_info.counter_leads_off_after_last_valid_data = intent.getLongExtra("counter_leads_off_after_last_valid_data", 0);
        device_info.timestamp_leads_off_disconnection = intent.getLongExtra("timestamp_leads_off_disconnection", 0);
        device_info.counter_total_leads_off = intent.getLongExtra("counter_total_leads_off", 0);

        device_info.max_setup_mode_sample_size = intent.getIntExtra("max_setup_mode_sample_size", 10);

        device_info.supports_setup_mode = intent.getBooleanExtra("supports_setup_mode", false);

        device_info.supports_disconnecting_progress_bar = intent.getBooleanExtra("supports_disconnecting_progress_bar", false);

        device_info.supports_battery_info = intent.getBooleanExtra("supports_battery_info", true);

        outputDeviceInfo("processReportedDeviceInfo", device_info);

        addOrUpdateDeviceInfo(device_info);

        if (isOrWasSensorTypeConnected(device_info.sensor_type))
        {
            hideManuallyEnteredVitalSignDeviceDependingOnSensorType(device_info.sensor_type);
        }

        switch (current_page)
        {
            case DUMMY_DATA_MODE:
            {
                FragmentDummyDataMode fragment = (FragmentDummyDataMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    fragment.showAddDevicesButtons(device_info, !device_info.isDeviceHumanReadableDeviceIdValid());

                    fragment.showStartStopPatientSessionButton();
                }
            }
            break;

            case ADD_DEVICES:
            {
                FragmentAddDevices fragment = (FragmentAddDevices) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    if (device_type == DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE)
                    {
                        fragment.updateAddRemoveEarlyWarningScoreButton();
                    }
                    else
                    {
                        fragment.setHumanReadableDeviceIdByType(device_info, device_info.human_readable_device_id);

                        fragment.showDataMatrixInfo(device_info.sensor_type, device_info.lot_number, device_info.manufacture_date, device_info.expiration_date);
                    }

                    showConnectionButtonIfValid();
                }
            }
            break;

            case CHANGE_SESSION_SETTINGS:
            {
                FragmentChangeSessionSettings fragment = (FragmentChangeSessionSettings) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    fragment.showDeviceStatus(device_info);
                }
            }
            break;

            case PATIENT_VITALS_DISPLAY:
            {
                if(device_info.isDeviceTypePartOfPatientSession() == false)
                {
                    Log.w(TAG, "processReportedDeviceInfo : PATIENT_VITALS_DISPLAY : " + device_info.device_type + " isDeviceTypePartOfPatientSession == false");
                }
                else if(device_info.isSensorTypeGatewayInfo())
                {
                    Log.w(TAG, "processReportedDeviceInfo : PATIENT_VITALS_DISPLAY : " + device_info.device_type + " isSensorTypeGatewayInfo == true");
                }
                else
                {
                    Log.w(TAG, "processReportedDeviceInfo : PATIENT_VITALS_DISPLAY : " + device_info.device_type + " isDeviceTypePartOfPatientSession == TRUE");

                    FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                    {
                        fragment.deviceStateChange(device_info);

                        fragment.setDeviceBatteryLevel(device_info);
                    }
                }
            }
            break;

            case UNLOCK_SCREEN:
            {
                FragmentQrCodeUnlock fragment = (FragmentQrCodeUnlock) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    fragment.showDeviceInfo(device_info);
                }
            }
            break;

            default:
                break;
        }
    }


    private void updatePatientVitalsMeasurementProgressBar(VitalSignType vital_sign_type, int measurement_validity_time_left_in_seconds)
    {
        if(current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
        {
            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (fragment != null)
            {
                fragment.setDevicesProgressBarValue(vital_sign_type, measurement_validity_time_left_in_seconds);
            }
        }
    }


    private void resetCachedSessionInfo()
    {
        resetCachedDeviceInfoList();

        saved_measurements.reset();

        poor_signal_in_last_minute = false;
        lifetouch_no_beats_detected = false;

        is_session_in_progress = false;

        observation_set_being_entered.reset();

        resetPatientInfo();
    }


    private <T extends MeasurementVitalSign> void displayMeasurement(VitalSignType vital_sign_type, T measurement, boolean saved_measurement_updated)
    {
        switch (current_page)
        {
            case PATIENT_VITALS_DISPLAY:
            {
                if (isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO())
                {
                    Log.w(TAG, "displayMeasurement : isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO = true");
                }
                else
                {
                    FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                    {
                        fragment.updateMeasurement(vital_sign_type, measurement, saved_measurement_updated);
                    }
                }
            }
            break;

            case DUMMY_DATA_MODE:
            {
                FragmentDummyDataMode fragment = (FragmentDummyDataMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    fragment.showMeasurement(vital_sign_type, measurement);
                }
            }
            break;

            default:
            {
                Log.d(TAG, "displayMeasurement : Current page not Patient Vitals Display or Dummy Data Mode so ignoring");
            }
            break;
        }
    }


    // Called by CMD_REPORT_NEW_VITALS_DATA
    private <T extends MeasurementVitalSign> void handleMeasurement(VitalSignType vital_sign_type, T measurement)
    {
        switch (vital_sign_type)
        {
            case HEART_RATE:
            case RESPIRATION_RATE:
            {
                poor_signal_in_last_minute = (measurement.getPrimaryMeasurement() == ErrorCodes.ERROR_CODE__LIFETOUCH_POOR_SIGNAL_IN_LAST_MINUTE);

                measurement_cache.updateCachedVitalsList(vital_sign_type, measurement);
            }
            break;

            case TEMPERATURE:
            {
                // Convert the temperature to Fahrenheit if needed
                if (isShowTemperatureInFahrenheitEnabled())
                {
                    Log.d(TAG, "Temperature being converted = " + measurement.getPrimaryMeasurement());
                    ((MeasurementTemperature)measurement).temperature = convertTemperatureToDegreesF(measurement.getPrimaryMeasurement());
                }

                if (getManufacturingModeEnabled() == false)
                {
                    BigDecimal bigDecimal_rounded_temp = new BigDecimal(String.valueOf(measurement.getPrimaryMeasurement())).setScale(1, BigDecimal.ROUND_HALF_UP);
                    double rounded_temp = bigDecimal_rounded_temp.doubleValue();

                    MeasurementTemperature rounded_measurement = new MeasurementTemperature(rounded_temp, measurement.timestamp_in_ms);
                    measurement_cache.updateCachedVitalsList(vital_sign_type, rounded_measurement);
                }
                else
                {
                    measurement_cache.updateCachedVitalsList(vital_sign_type, measurement);
                }
            }
            break;

            case WEIGHT:
            {
                // Convert the weight to lbs if needed
                if (isShowWeightInLbsEnabled())
                {
                    Log.d(TAG, "Weight being converted = " + measurement.getPrimaryMeasurement());
                    ((MeasurementWeight)measurement).weight = convertWeightToLbs(measurement.getPrimaryMeasurement());
                }

                if (getManufacturingModeEnabled() == false)
                {
                    BigDecimal bigDecimal_rounded_weight = new BigDecimal(String.valueOf(measurement.getPrimaryMeasurement())).setScale(1, BigDecimal.ROUND_HALF_UP);
                    double rounded_weight = bigDecimal_rounded_weight.doubleValue();

                    MeasurementWeight rounded_measurement = new MeasurementWeight(rounded_weight, measurement.timestamp_in_ms);
                    measurement_cache.updateCachedVitalsList(vital_sign_type, rounded_measurement);
                }
                else
                {
                    measurement_cache.updateCachedVitalsList(vital_sign_type, measurement);
                }
            }
            break;

            case MANUALLY_ENTERED_TEMPERATURE:
            {
                // Convert the temperature to Fahrenheit if needed
                if (isShowTemperatureInFahrenheitEnabled())
                {
                    Log.d(TAG, "Temperature being converted = " + measurement.getPrimaryMeasurement());
                    ((MeasurementManuallyEnteredTemperature)measurement).temperature = convertTemperatureToDegreesF(measurement.getPrimaryMeasurement());
                }

                measurement_cache.updateCachedVitalsList(vital_sign_type, measurement);
            }
            break;

            case MANUALLY_ENTERED_WEIGHT:
            {
                // Convert the Weight to lbs if needed
                if (isShowWeightInLbsEnabled())
                {
                    Log.d(TAG, "Weight being converted = " + measurement.getPrimaryMeasurement());
                    ((MeasurementManuallyEnteredWeight)measurement).weight = convertWeightToLbs(measurement.getPrimaryMeasurement());
                }

                measurement_cache.updateCachedVitalsList(vital_sign_type, measurement);
            }
            break;

            case SPO2:
            {
                MeasurementSpO2 process_measurement = (MeasurementSpO2)measurement;
                process_measurement.SpO2 = checkForSpO2InvalidReading(process_measurement.SpO2);
                measurement_cache.updateCachedVitalsList(vital_sign_type, process_measurement);
            }
            break;

            default:
            {
                measurement_cache.updateCachedVitalsList(vital_sign_type, measurement);
            }
            break;
        }

        replaceSavedMeasurementIfNewer(vital_sign_type, measurement);
    }


    private <T extends MeasurementVitalSign> void replaceSavedMeasurementIfNewer(VitalSignType vital_sign_type, T measurement)
    {
        MeasurementVitalSign saved_measurement = getSavedMeasurement(vital_sign_type);

        // Only update the Spot Measurement if the timestamp is newer than the previous measurement
        if ((saved_measurement != null) && (saved_measurement.timestamp_in_ms < measurement.timestamp_in_ms))
        {
            Log.d(TAG, "replaceSavedMeasurementIfNewer : setSavedMeasurement " + UtilityFunctions.padVitalSignName(vital_sign_type));

            setSavedMeasurement(vital_sign_type, measurement);

            displayMeasurement(vital_sign_type, measurement, true);
        }
        else
        {
            displayMeasurement(vital_sign_type, measurement, false);
        }
    }


    private void refreshLeadsStateOnScreen(DeviceInfo device_info)
    {
        Log.d(TAG, "refreshLeadsStateOnScreen : " + device_info.getSensorTypeAndDeviceTypeAsString());

        switch (current_page)
        {
            case PATIENT_VITALS_DISPLAY:
            {
                patientVitalsDisplayDeviceStateChangeOfDevicesInSession(device_info);
            }
            break;

            case UNLOCK_SCREEN:
            {
                FragmentQrCodeUnlock fragment = (FragmentQrCodeUnlock) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment) && (device_info.isDeviceHumanReadableDeviceIdValid()))
                {
                    fragment.showDeviceLeadsOffDetected(device_info, device_info.isDeviceOffBody());
                }
            }
            break;
        }
    }


    private void refreshDeviceRawAccelerometerModeStateOnScreen(DeviceInfo device_info)
    {
        switch (current_page)
        {
            case PATIENT_VITALS_DISPLAY:
            {
                patientVitalsDisplayDeviceStateChangeOfDevicesInSession(device_info);
            }
            break;

            case UNLOCK_SCREEN:
            {
                FragmentQrCodeUnlock fragment = (FragmentQrCodeUnlock) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment) && (device_info.isDeviceHumanReadableDeviceIdValid()))
                {
                    fragment.showDeviceInRawAccelerometerMode(device_info, device_info.isDeviceInRawAccelerometerMode());
                }
            }
            break;
        }
    }


    private void patientVitalsDisplayDeviceStateChangeOfDevicesInSession(DeviceInfo device_info)
    {
        if(isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO())
        {
            Log.w(TAG, "isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO = true");
        }
        else
        {
            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.deviceStateChange(device_info);
            }
        }
    }


    private void refreshDeviceSetupModeStateOnScreen(DeviceInfo device_info)
    {
        switch (current_page)
        {
            case PATIENT_VITALS_DISPLAY:
            {
                patientVitalsDisplayDeviceStateChangeOfDevicesInSession(device_info);
            }
            break;

            case UNLOCK_SCREEN:
            {
                FragmentQrCodeUnlock fragment = (FragmentQrCodeUnlock) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment) && device_info.isDeviceHumanReadableDeviceIdValid())
                {
                    fragment.showDeviceInSetupMode(device_info, device_info.isDeviceInSetupMode());
                }
            }
            break;
        }
    }


    public boolean isServerLookupOfPatientNameFromPatientIdEnabled()
    {
        return features_enabled.server_lookup_of_patient_name_from_patient_id;
    }


    public void refreshDeviceLeadsOffStatus()
    {
        portal_system_commands.sendGatewayCommand_getDeviceLeadsOffStatus(getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH).device_type);
        portal_system_commands.sendGatewayCommand_getDeviceLeadsOffStatus(getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE).device_type);
        portal_system_commands.sendGatewayCommand_getDeviceLeadsOffStatus(getDeviceByType(SensorType.SENSOR_TYPE__SPO2).device_type);
    }


    public boolean isSessionInProgress()
    {
        Log.d(TAG, "isSessionInProgress = " + is_session_in_progress);

        return is_session_in_progress;
    }


    public void clearDesiredDeviceInPatientGateway(DeviceType device_type)
    {
        portal_system_commands.sendGatewayCommand_clearDesiredDevice(device_type);
    }


    public void clearDesiredDeviceInPatientGateway(SensorType sensor_type)
    {
        portal_system_commands.sendGatewayCommand_clearDesiredDevice(getDeviceByType(sensor_type).device_type);
    }


    public void setGatewaysAssignedBedDetails(String gateways_assigned_bed_id, String gateways_assigned_ward_name, String gateways_assigned_bed_name)
    {
        portal_system_commands.sendGatewayCommand_setGatewaysAssignedBedDetails(gateways_assigned_bed_id, gateways_assigned_ward_name, gateways_assigned_bed_name);
    }


    public void showOnScreenMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.d(TAG + " : showOnScreenMessage", message);
    }


    private void enableNavigationButtons(boolean back_enabled, boolean lock_enabled, boolean next_enabled)
    {
        //Log.d("enableNavigationButtons", Log.getStackTraceString(new Exception()));

        if (footer_fragment != null)
        {
            footer_fragment.setBackButtonAsShowOrHideSetupModeBlobsVisible(false);
        }

        showBackButton(back_enabled);
        showLockButton(lock_enabled);
        showNextButton(next_enabled);
    }


    private void showBackButton(boolean back_enabled)
    {
        if(footer_fragment != null)
        {
            footer_fragment.setDesiredBackButtonVisibility(back_enabled);

            if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
            {
                footer_fragment.setBackButtonVisible(back_enabled);
            }
            else
            {
                Log.e(TAG, "showBackButton : footer_fragment not added and/or resumed");
            }
        }
        else
        {
            Log.e(TAG, "showBackButton : footer_fragment = null");
        }
    }


    private void showLockButton(boolean lock_enabled)
    {
        if(footer_fragment != null)
        {
            footer_fragment.setDesiredLockButtonVisibility(lock_enabled);

            if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
            {
                footer_fragment.setLockButtonVisible(lock_enabled);
            }
            else
            {
                Log.e(TAG, "showLockButton : footer_fragment not added and/or resumed");
            }
        }
        else
        {
            Log.e(TAG, "showLockButton : footer_fragment = null");
        }
    }


    public void showNextButton(boolean next_enabled)
    {
        if(footer_fragment != null)
        {
            footer_fragment.setDesiredNextButtonVisibility(next_enabled);

            if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
            {
                footer_fragment.setNextButtonVisible(next_enabled);
            }
            else
            {
                Log.e(TAG, "showNextButton : footer_fragment not added and/or resumed");
            }
        }
        else
        {
            Log.e(TAG, "showNextButton : footer_fragment = null");
        }
    }


    private void setLockButtonText(String desired_text)
    {
        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
        {
            footer_fragment.setLockButtonText(desired_text);
        }
        else
        {
            Log.e(TAG, "setLockButtonText : footer_fragment = null");
        }
    }


    private void setBackButtonText(String desired_text)
    {
        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
        {
            footer_fragment.setBackButtonText(desired_text);
        }
        else
        {
            Log.e(TAG, "setBackButtonText : footer_fragment = null");
        }
    }


    private void setNextButtonText(String desired_text)
    {
        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
        {
            footer_fragment.setNextButtonText(desired_text);
        }
        else
        {
            Log.e(TAG, "setNextButtonText : footer_fragment = null");
        }
    }


    public void showModeSelectionOrGatewayNotConfiguredYet()
    {
        portal_system_commands.sendGatewayCommand_getLocationEnabled();

        if (permissions.checkForRequiredAndroidPermissions() == false)
        {
            showAndroidPermissionsFragment();
        }
        else if(isSoftwareUpdateAvailableAndNoSessionRunning())
        {
            showSoftwareUpdateAvailable();
        }
        else if (software_update_mode_active)
        {
            showSoftwareUpdateMode();
        }
        else if (needToStartInstallationWizard())
        {
            startInstallationWizard();
        }
        else
        {
            // Following check is added to avoid the start of session without setting User ID
            // User ID is set by scanning Unlock and Admin QR code.
            if(gateway_user_id == -1)
            {
                lockScreenSelected();
            }
            else
            {
                modeSelectionSelected();
            }
        }
    }

    private boolean send_turn_off_command_when_stopping_session = false;


    public void setSendTurnOffCommand(boolean turn_off)
    {
        send_turn_off_command_when_stopping_session = turn_off;

        showEndSessionTimerSelectionFragment();
    }


    private void showEndSessionTimerSelectionFragment()
    {
        enableNavigationButtons(true, true, false);

        current_page = UserInterfacePage.INVALID;

        FragmentEndSessionTimePicker fragmentEndSessionTimePicker = new FragmentEndSessionTimePicker();

        // Checking if end session or transfer session is selected
        Bundle args = new Bundle();
        args.putBoolean("is_session_ended", send_turn_off_command_when_stopping_session);
        fragmentEndSessionTimePicker.setArguments(args);

        showFragment(fragmentEndSessionTimePicker, UserInterfacePage.END_SESSION_TIME_SELECTION);
    }


    private void showDummyDataModeOrGatewayNotConfiguredYet()
    {
        portal_system_commands.sendGatewayCommand_getLocationEnabled();

        if (needToStartInstallationWizard())
        {
            startInstallationWizard();
        }
        else
        {
            dummyDataModeSelected();
        }
    }


    private void modeSelectionSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, true, false);
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentModeSelection(), UserInterfacePage.MODE_SELECTION);
    }

    private void emptyFragmentSelected()
    {
        Log.d(TAG, "emptyFragmentSelected");

        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, true, false);
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentEmpty(), UserInterfacePage.EMPTY);
    }

    private void showAndroidPermissionsFragment()
    {
        if (UtilityFunctions.isFragmentAddedAndResumed(header_fragment))
        {
            header_fragment.reset();
        }

        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, false, false);

        Bundle bundle = new Bundle();
        bundle.putBoolean(FragmentAndroidPermissions.KEY_PERMISSION_OVERLAY, permissions.haveOverlayPermission());
        bundle.putBoolean(FragmentAndroidPermissions.KEY_PERMISSION_WRITE_SETTINGS, permissions.haveWriteSettingsPermission());
        bundle.putBoolean(FragmentAndroidPermissions.KEY_PERMISSION_CAMERA, permissions.haveCameraPermission());
        bundle.putBoolean(FragmentAndroidPermissions.KEY_PERMISSION_RECORD_AUDIO, permissions.haveRecordAudioPermission());
            bundle.putBoolean(FragmentAndroidPermissions.KEY_PERMISSION_WRITE_EXTERNAL_STORAGE, permissions.haveWriteExternalStoragePermission());

        bundle.putBoolean(FragmentAndroidPermissions.KEY_PERMISSION_ACCESS_NOTIFICATION_POLICY, permissions.haveAccessNotificationPermission());
        bundle.putBoolean(FragmentAndroidPermissions.KEY_PERMISSION_REQUEST_PACKAGE_INSTALLS, permissions.haveInstallPackagesPermission());


        showFragmentWithBundle(new FragmentPermissions(), bundle, UserInterfacePage.ANDROID_PERMISSIONS);
    }


    private void adminModeSelected()
    {
        current_page = UserInterfacePage.INVALID;

        enableNavigationButtons(false, true, false);
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentAdminMode(), UserInterfacePage.ADMIN_MODE);
    }


    private void featureEnableModeSelected()
    {
        current_page = UserInterfacePage.INVALID;

        enableNavigationButtons(false, true, false);
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentFeatureEnable(), UserInterfacePage.FEATURE_ENABLE_MODE);
    }


    private void dummyDataModeSelected()
    {
        current_page = UserInterfacePage.INVALID;

        Log.d(TAG, "dummyDataModeSelected");

        enableNavigationButtons(false, true, false);
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentDummyDataMode(), UserInterfacePage.DUMMY_DATA_MODE);
    }


    private void showSoftwareUpdateAvailable()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, true, false);
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentSoftwareUpdateAvailable(), UserInterfacePage.SOFTWARE_UPDATE_AVAILABLE);
    }


    private void showSoftwareUpdateMode()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, false, false);

        Bundle bundle = new Bundle();
        bundle.putParcelable(FragmentUpdateMode.UPDATE_MODE_STATUS, updateModeStatus);

        showFragmentWithBundle(new FragmentUpdateMode(), bundle, UserInterfacePage.SOFTWARE_UPDATE_IN_PROGRESS);
    }


    private void showPatientDetailsName(boolean show_keyboard)
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, true);
        setBackButtonText(getResources().getString(R.string.text_stop_this_session));
        setNextButtonText(getResources().getString(R.string.textNext));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        Bundle bundle = new Bundle();
        bundle.putInt(FragmentPatientDetails.KEY_KEYBOARD_MODE, getKeyboardModeAsEnum().ordinal());
        bundle.putBoolean(FragmentPatientDetails.KEY_SHOW_ONSCREEN_KEYBOARD, show_keyboard);

        showFragmentWithBundle(new FragmentPatientDetails(), bundle, UserInterfacePage.PATIENT_DETAILS_NAME);
    }


    private void showPatientCaseIdEntry()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.text_stop_this_session));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentPatientCaseIdEntry(), UserInterfacePage.PATIENT_CASE_ID_ENTRY);
    }


    public void startMonitoringPatientPressed()
    {
        if (isServerLookupOfPatientNameFromPatientIdEnabled())
        {
            showPatientCaseIdEntry();
        }
        else
        {
            showPatientDetailsName(true);
        }

        //portal_system_commands.sendGatewayCommand_resetBluetooth(false, true);
    }


    private void patientDetailsThresholdCategorySelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, true);
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textNext));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentPatientDetailsAge(), UserInterfacePage.PATIENT_THRESHOLD_CATEGORY);
    }


    public void stopMonitoringCurrentPatient()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentEndSession(), UserInterfacePage.END_SETTINGS);
    }


    public void checkDeviceStatusPressed()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentCheckDeviceStatus(), UserInterfacePage.CHECK_DEVICE_STATUS);
    }


    public void lockScreenSelected()
    {
        // If current-page is admin or admin_server_address_entry then Don't store the previous as admin page.
        if(current_page == UserInterfacePage.ADMIN_MODE
                || current_page == UserInterfacePage.INSTALLATION_MODE_PROGRESS
                || current_page == UserInterfacePage.INSTALLATION_MODE_SERVER_ADDRESS_SCAN
                || current_page == UserInterfacePage.FEATURE_ENABLE_MODE
                || current_page == UserInterfacePage.UNLOCK_SCREEN )
        {
            // Do nothing
        }
        else
        {
            previous_page_before_locking_page = current_page;
        }

        current_page = UserInterfacePage.INVALID;

        if (features_enabled.manufacturing_mode)
        {
            setLockButtonText(getResources().getString(R.string.simulate_user_qr_code));
            enableNavigationButtons(false, true, false);
        }
        else
        {
            enableNavigationButtons(false, false, false);
        }

        showFragment(new FragmentQrCodeUnlock(), UserInterfacePage.UNLOCK_SCREEN);
    }


    private boolean checkLifetouch_Connected()
    {
        DeviceInfo device_info = getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);

        if(device_info.isDeviceHumanReadableDeviceIdValid())
        {
            return device_info.getActualDeviceConnectionStatus() != DeviceConnectionStatus.NOT_PAIRED;
        }
        return false;
    }


    private void setPatientOrientation(PatientPositionOrientation orientation, boolean visible)
    {
        if(current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
        {
            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                DeviceInfo device_info__lifetouch = getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);

                if (device_info__lifetouch.isDeviceTypePartOfPatientSession() && features_enabled.patient_orientation)
                {
                    fragment.setLifetouchPatientOrientation(orientation, visible);
                }
                else
                {
                    fragment.setLifetouchPatientOrientation(orientation, false);
                }
            }
        }
    }

    public void patientVitalsDisplaySelected()
    {
        Log.e(TAG, "patientVitalsDisplaySelected");

        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, true, false);

        if (footer_fragment != null)
        {
            footer_fragment.setBackButtonAsShowOrHideSetupModeBlobsVisible(true);
        }

        if (features_enabled.manufacturing_mode)
        {
            setLockButtonText(getResources().getString(R.string.textChangeSessionSettingsMultiLine));
        }
        else
        {
            setLockButtonText(getResources().getString(R.string.textUnlockScreen));
        }

        if(isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO())
        {
            showFragment(new FragmentEmptyDevicePatientVitalDisplay(), UserInterfacePage.EMPTY_PATIENT_VITALS_DISPLAY);
        }
        else
        {
            showFragment(new FragmentPatientVitalsDisplay(), UserInterfacePage.PATIENT_VITALS_DISPLAY);
        }

        if(checkLifetouch_Connected())
        {
            portal_system_commands.reportPatientOrientation();
        }
    }


    public void addDevicesSelected()
    {
        Log.d(TAG,"addDevicesSelected() auto_enable_ews? " + (features_enabled.auto_enable_ews));

        if (isSessionInProgress() == false)
        {
            enableEarlyWarningScoreDevice(features_enabled.auto_enable_ews);
        }

        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textConnect));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentAddDevices(), UserInterfacePage.ADD_DEVICES);

        showConnectionButtonIfValid();
    }


    private void gatewayNotRespondingSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, false, false);

        showFragment(new FragmentGatewayNotResponding(), UserInterfacePage.GATEWAY_NOT_RESPONDING);
    }


    public void startConnectionForUnbondedDevice()
    {
        Log.d(TAG, "startConnectionForUnbondedDevice");

        lockButtonPressed();
        previous_page_before_locking_page = UserInterfacePage.DEVICE_CONNECTION;
    }


    private void showFragmentWithBundle(Fragment fragment, Bundle bundle, UserInterfacePage new_ui_page)
    {
        fragment.setArguments(bundle);

        showFragment(fragment, new_ui_page);
    }


    private void showFragment(Fragment fragment, UserInterfacePage new_ui_page)
    {
        // Don't log anything about the Feature Enable page
        if (new_ui_page != UserInterfacePage.FEATURE_ENABLE_MODE)
        {
            storeAuditTrailEvent(AuditTrailEvent.NEW_PAGE_BEING_SHOWN, gateway_user_id, new_ui_page.toString());
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, fragment);
        transaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();

        current_page = new_ui_page;

        stopScreenLockCountdownTimerAndRestartIfDesired();
    }


    /**
     * When device Connection fragment is created, device starts to connect automatically.
     * BackButton and LockScreen is made invisible until all device are connected, disconnected or for 3 minutes (fail safe).
     */
    private void deviceConnectionSelected()
    {
        Log.d(TAG, "deviceConnectionSelected");

        showDeviceConnectionFragment_StartNewScan();
    }


    private void showDeviceConnectionFragment_StartNewScan()
    {
        showDeviceConnectionFragment(false);
        startDeviceConnectionIfNecessary();
    }


    private void showDeviceConnectionFragment_ResumeCurrentScan()
    {
        showDeviceConnectionFragment(true);
    }


    private void showDeviceConnectionFragment(boolean resume_in_progress_scan)
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentDeviceConnection(resume_in_progress_scan), UserInterfacePage.DEVICE_CONNECTION);
    }


    private void startDeviceConnectionIfNecessary()
    {
        // Send command to Patient Gateway to start the device connection
        for(DeviceInfo device_info : cached_device_info_list)
        {
            if (device_info.isDeviceTypeASensorDevice())
            {
                if (device_info.isDeviceHumanReadableDeviceIdValid())
                {
                    portal_system_commands.sendGatewayCommand_connectToDesiredBluetoothDevices();
                    return;
                }
            }
        }
    }


    public void changeSessionSettingsPressed()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentChangeSessionSettings(), UserInterfacePage.CHANGE_SESSION_SETTINGS);
    }


    public void showInstallationServerAddressScanFragment()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, false, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentInstallationServerAddressScan(), UserInterfacePage.INSTALLATION_MODE_SERVER_ADDRESS_SCAN);
    }


    private void showInstallationWelcomeFragment()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, false, false);

        showFragment(new FragmentInstallationWelcome(), UserInterfacePage.INSTALLATION_MODE_WELCOME);
    }


    private void showInstallationProgressFragment()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, false, false);
        setBackButtonText(getResources().getString(R.string.restart_installation_wizard));

        Bundle bundle = new Bundle();
        bundle.putBoolean(FragmentInstallationProgress.INSTALLATION_PROGRESS_REFRESH_SERVER_DATA_ONLY, false);
        bundle.putBoolean(FragmentInstallationProgress.SHOW_BUTTON_AT_END, true);
        showFragmentWithBundle(new FragmentInstallationProgress(), bundle, UserInterfacePage.INSTALLATION_MODE_PROGRESS);
    }


    private void showInstallationProgressGetServerDataFragment(boolean show_button_at_end)
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, true, false);

        Bundle bundle = new Bundle();
        bundle.putBoolean(FragmentInstallationProgress.INSTALLATION_PROGRESS_REFRESH_SERVER_DATA_ONLY, true);
        bundle.putBoolean(FragmentInstallationProgress.SHOW_BUTTON_AT_END, show_button_at_end);
        showFragmentWithBundle(new FragmentInstallationProgress(), bundle, UserInterfacePage.INSTALLATION_MODE_PROGRESS);
    }


    private final MeasurementSetBeingEntered observation_set_being_entered = new MeasurementSetBeingEntered();


    public void storeObservationSet()
    {
        int measurement_validity_time_in_seconds = observation_set_being_entered.getValidityTime();
        long timestamp = observation_set_being_entered.getTimestamp();

        for (ManualVitalSignBeingEntered observation : observation_set_being_entered.getMeasurements())
        {
            storeManuallyEnteredVitalSign(observation, measurement_validity_time_in_seconds, timestamp, getGatewayUserId());
        }

        observation_set_being_entered.reset();

        showModeSelectionOrGatewayNotConfiguredYet();
    }


    public void observationSetEntrySelected()
    {
        if (observation_set_being_entered.getSize() > 0)
        {
            observationSetVitalSignSelectionSelected();
        }
        else
        {
            observationSetTimeEntrySelected();
        }
    }


    private void observationSetTimeEntrySelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, true);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));
        setNextButtonText(getResources().getString(R.string.next));

        showFragment(new FragmentObservationSetTimeEntry(), UserInterfacePage.OBSERVATION_SET_TIME_SELECTION);
    }


    private void observationSetVitalSignSelectionSelected()
    {
        current_page = UserInterfacePage.INVALID;

        enableNavigationButtons(true, true, observation_set_being_entered.getSize() > 0);

        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));
        setNextButtonText(getResources().getString(R.string.next));

        showFragment(new FragmentObservationSetSelectionList(), UserInterfacePage.OBSERVATION_SET_VITAL_SIGN_SELECTION);
    }


    // User has selected a Manual Vital from the Observation Set list of vital signs
    public void observationSetMeasurementSelected(ManuallyEnteredVitalSignDescriptor vital_sign_selected)
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));
        setNextButtonText(getResources().getString(R.string.next));

        Bundle bundle = new Bundle();
        bundle.putInt(MainActivity.VITAL_SIGN_ID, vital_sign_selected.vital_sign_id);
        bundle.putInt(MainActivity.VITAL_ENTRY_METHOD, vital_sign_selected.observation_set_entry_type.ordinal());
        FragmentObservationSetVitalSignEntry fragment = new FragmentObservationSetVitalSignEntry();
        fragment.setArguments(bundle);

        switch (vital_sign_selected.observation_set_entry_type)
        {
            case BUTTON_SELECTION:
            {
                showFragment(fragment, UserInterfacePage.OBSERVATION_SET_MEASUREMENT_BUTTON_ENTRY);
            }
            break;

            case KEYPAD:
            {
                showFragment(fragment, UserInterfacePage.OBSERVATION_SET_MEASUREMENT_KEYPAD_ENTRY);
            }
            break;
        }
    }


    public void observationSetMeasurementValueEntered(int vital_sign_id, int button_id)
    {
        ManualVitalSignInfo manual_vital_sign_info = getManualVitalSignInfo(vital_sign_id);

        if (manual_vital_sign_info != null)
        {
            String human_readable_name = manual_vital_sign_info.vital_sign_display_name;

            ManualVitalSignButtonDescriptor button_descriptor = manual_vital_sign_info.button_info.get(button_id);
            int ews_score = button_descriptor.ews_score;

            observation_set_being_entered.addVitalSign(vital_sign_id, human_readable_name, button_id, ews_score);

            observationSetVitalSignSelectionSelected();
        }
    }


    public void observationSetMeasurementValueEntered(int vital_sign_id, String measurement_value)
    {
        ManualVitalSignInfo manual_vital_sign_info = getManualVitalSignInfo(vital_sign_id);

        if (manual_vital_sign_info != null)
        {
            String human_readable_name = manual_vital_sign_info.vital_sign_display_name;

            Bundle bundle = getGraphColourBands(VitalSignType.values()[vital_sign_id]);
            ArrayList<GraphColourBand> graph_colour_bands = bundle.getParcelableArrayList("graph_colour_bands");

            double value;

            if (measurement_value.contains("/"))
            {
                int position_of_slash = measurement_value.indexOf("/");

                String primary_measurement = measurement_value.substring(0, position_of_slash);

                value = Integer.parseInt(primary_measurement);
            }
            else
            {
                value = Double.parseDouble(measurement_value);
            }

            final int EWS_SCORE_NOT_SET = -1;
            int ews_score = EWS_SCORE_NOT_SET;

            for (GraphColourBand graph_colour_band : graph_colour_bands)
            {
                if (value < graph_colour_band.less_than_value)
                {
                    ews_score = graph_colour_band.ews_value;
                    break;
                }
            }

            try
            {
                if (ews_score == EWS_SCORE_NOT_SET)
                {
                    // Value is at or above the top of the chart, so return the EWS of the top value
                    ews_score = graph_colour_bands.get(graph_colour_bands.size() - 1).ews_value;
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "Exception in observationSetMeasurementValueEntered looking up graph_colour_band : " + e.getMessage());
            }

            observation_set_being_entered.addVitalSign(vital_sign_id, human_readable_name, measurement_value, ews_score);

            observationSetVitalSignSelectionSelected();
        }
    }


    public MeasurementSetBeingEntered getObservationSetEntered()
    {
        return observation_set_being_entered;
    }


    public static final String VITAL_SIGN_TYPE = "VITAL_SIGN_TYPE";
    public static final String KEYPAD_INITIAL_VALUE = "KEYPAD_INITIAL_VALUE";
    public static final String VITAL_SIGN_ID = "VITAL_SIGN_ID";
    public static final String VITAL_ENTRY_METHOD = "VITAL_ENTRY_METHOD";

    public static final String MANUALLY_ENTERED_VITAL_SIGN_TYPE = "MANUALLY_ENTERED_VITAL_SIGN_TYPE";

    public static final String KEYPAD_ENTRY_TYPE = "KEYPAD_ENTRY_TYPE";


    private void observationSetValidityTimeSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));
        setNextButtonText(getResources().getString(R.string.next));

        showFragment(new FragmentManualVitalSignValidityTime(), UserInterfacePage.OBSERVATION_SET_VALIDITY_TIME_ENTRY);
    }


    private void observationSetConfirmationSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentObservationSetConfirmation(), UserInterfacePage.OBSERVATION_SET_CONFIRMATION);
    }


    public void viewManuallyEnteredVitalSignsSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentManualVitalSignsDisplayAll(), UserInterfacePage.VIEW_MANUALLY_ENTERED_VITAL_SIGNS);
    }


    public void videoCallModeSelectionSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentVideoCallModeSelection(), UserInterfacePage.VIDEO_CALL_MODE_SELECTION);
    }


    private void selectAnnotationConfirmation()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));
        setNextButtonText(getResources().getString(R.string.confirm));

        showFragment(new FragmentAnnotationConfirmation(), UserInterfacePage.ANNOTATION_ENTRY_CONFIRMATION);
    }


    public void nextButtonPressed()
    {
        Log.d(TAG, "nextButtonPressed : current_page = " + current_page);

        switch (current_page)
        {
            case UNLOCK_SCREEN:
            case MODE_SELECTION:
            case CHANGE_SESSION_SETTINGS:
                break;

            case PATIENT_DETAILS_NAME:
            case PATIENT_CASE_ID_ENTRY:
            {
                // Clear down the any selected Age Range and remove all Scanned devices. May have got here by pressing Back from the Age Range or Add Devices page
                patient_info.resetServersThresholdSetInfo();

                for (DeviceType device_type : DeviceType.values())
                {
                    removeDeviceFromGateway(device_type);
                }

                enableEarlyWarningScoreDevice(false);
                // End of "reset" code

                patientDetailsThresholdCategorySelected();

                setBackButtonText(getResources().getString(R.string.back));
            }
            break;

            case PATIENT_THRESHOLD_CATEGORY:
            {
                addDevicesSelected();
            }
            break;

            case ADD_DEVICES:
            {
                deviceConnectionSelected();
            }
            break;

            case DEVICE_CONNECTION:
            {
                // User has pressed the Start Monitoring button on the Device Connection page
                startMonitoringButtonPressed();
            }
            break;

            case PATIENT_VITALS_DISPLAY:
            {
                // User has pressed the "Reset Zoom" button on Patient Vitals Display page
                resetGraphViewport();
            }
            break;

            case OBSERVATION_SET_TIME_SELECTION:
            {
                FragmentIsansysWithTimestamp fragment = (FragmentIsansysWithTimestamp) this.getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    observation_set_being_entered.setTimestamp(fragment.getTimestamp());
                }

                observationSetValidityTimeSelected();
            }
            break;

            case OBSERVATION_SET_VALIDITY_TIME_ENTRY:
            {
                // NO NEXT BUTTON HERE
                // User selects an onscreen button
            }
            break;

            case OBSERVATION_SET_MEASUREMENT_KEYPAD_ENTRY:
            {
                FragmentObservationSetVitalSignEntry fragment = (FragmentObservationSetVitalSignEntry) this.getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    fragment.setMeasurementValueToKeypad();
                }
            }
            break;

            case OBSERVATION_SET_MEASUREMENT_BUTTON_ENTRY:
            {
                // NO NEXT BUTTON HERE
                // User selects an onscreen button
            }
            break;

            case OBSERVATION_SET_VITAL_SIGN_SELECTION:
            {
                observationSetConfirmationSelected();
            }
            break;

            case ANNOTATION_ENTRY_SELECT_ANNOTATION_TYPE:
            {
                selectAnnotationTimeSelected();
            }
            break;

            case ANNOTATION_ENTRY_SELECT_ANNOTATION_TIME:
            {
                switch(annotation_being_entered.getAnnotationEntryType())
                {
                    case CUSTOM_VIA_ONSCREEN_KEYBOARD:
                    {
                        enterAnnotationViaKeyboardSelected();
                    }
                    break;

                    case PREDEFINED_FROM_SERVER:
                    {
                        enterPredefinedAnnotationConditionSelected();
                    }
                    break;
                }
            }
            break;

            case ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_CONDITION:
            {
                enterPredefinedAnnotationActionSelected();
            }
            break;

            case ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_ACTION:
            {
                enterPredefinedAnnotationOutcomeSelected();
            }
            break;

            case ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_OUTCOME:
            case ANNOTATION_ENTRY_ENTER_ANNOTATION_VIA_KEYBOARD:
            {
                selectAnnotationConfirmation();
            }
            break;

            case ANNOTATION_ENTRY_CONFIRMATION:
            {
                // Cant get here. The Confirm/Cancel buttons will take care of it
            }
            break;

            default:
            {
                Log.d(TAG, "nextButtonPressed - but current_page value not handled = " + current_page);
            }
            break;
        }
    }


    private void startMonitoringButtonPressed()
    {
        if (isSessionInProgress())
        {
            portal_system_commands.sendGatewayCommand_updateExistingSessionCommand(gateway_user_id);
        }
        else
        {
            createNewSession();
        }

        // The Create New Session command will mark the Gateway Device Info's as setDeviceTypePartOfPatientSession(true)
        // But this will take a finite time to come back to the UI. In the mean time patientVitalsDisplaySelected will put the
        // Patient Vitals Display fragment on the screen without the newly added devices. So set show_on_ui here.
        for (DeviceInfo existing_device : cached_device_info_list)
        {
            if (existing_device.isDeviceHumanReadableDeviceIdValid())
            {
                existing_device.setDeviceTypePartOfPatientSession(true);
            }
        }

        patientVitalsDisplaySelected();
    }


    private void resetSessionButtonPressed_checkAndRemoveConnectedDevices()
    {
        for(DeviceInfo device_info : cached_device_info_list)
        {
            if (device_info.isDeviceTypeASensorDevice())
            {
                Log.d(TAG, "resetSessionPressed_checkAndRemoveConnectedDevices : " + device_info.getSensorTypeAndDeviceTypeAsString() + " :  " + device_info.getActualDeviceConnectionStatus());

                portal_system_commands.sendGatewayCommand_disconnectDevice(device_info.device_type, gateway_user_id, false);
            }
        }
    }


    public void backButtonPressed()
    {
        Log.d(TAG, "backButtonPressed on " + current_page);

        switch (current_page)
        {
            case UNLOCK_SCREEN:
            case MODE_SELECTION:
                break;

            case CHANGE_SESSION_SETTINGS:
            case CHECK_DEVICE_STATUS:
            case END_SETTINGS:
            case ANNOTATION_ENTRY_SELECT_ANNOTATION_TYPE:
            case END_SESSION_TIME_SELECTION:
            case MANUFACTURING_MODE__CHECK_PACKAGING:
            case VIDEO_CALL_MODE_SELECTION:
            case OBSERVATION_SET_VITAL_SIGN_SELECTION:
            case OBSERVATION_SET_VALIDITY_TIME_ENTRY:
            {
                showModeSelectionOrGatewayNotConfiguredYet();
            }
            break;

            case PATIENT_DETAILS_NAME:
            case PATIENT_CASE_ID_ENTRY:
            {
                resetSessionButtonPressed_checkAndRemoveConnectedDevices();

                patient_info.reset();
                setHospitalPatientId("");

                showModeSelectionOrGatewayNotConfiguredYet();
            }
            break;

            case PATIENT_THRESHOLD_CATEGORY:
            {
                if (isServerLookupOfPatientNameFromPatientIdEnabled())
                {
                    setHeaderHospitalPatientIdText("");
                    showPatientCaseIdEntry();
                }
                else
                {
                    showPatientDetailsName(false);
                }

                setAgeRange(null, null);
            }
            break;

            case ADD_DEVICES:
            {
                if(isSessionInProgress())
                {
                    // If we were on the Add Devices page, and have scanned a device (which connects and goes green on Device Connection page), but have NOT pressed
                    // Start Monitoring, then the device IS connected via Bluetooth but there is NO device session for it - which is a very weird state!
                    // So forcefully disconnect these devices here.
                    for (DeviceInfo existing_device : cached_device_info_list)
                    {
                        if ((existing_device.isDeviceSessionInProgress() == false) && (existing_device.isDeviceHumanReadableDeviceIdValid())) // RM: Only do it for devices which have a device ID (i.e. have been scanned) but don't have a session ID (so start monitoring hasn't been pressed).
                        {
                            Log.d(TAG, existing_device.device_type + " is being REMOVED as its connected via Bluetooth but there is no Device Session for it");
                            portal_system_commands.sendGatewayCommand_disconnectDevice(existing_device.device_type, gateway_user_id, false);
                        }
                    }

                    changeSessionSettingsPressed();
                }
                else
                {
                    patientDetailsThresholdCategorySelected();
                }
            }
            break;

            case DEVICE_CONNECTION:
            {
                // Cancel the device searching process before leaving DEVICE_CONNECTION
                portal_system_commands.sendGatewayCommand_stopRunningBluetoothScan();

                previous_page_before_locking_page = UserInterfacePage.ADD_DEVICES;

                addDevicesSelected();
            }
            break;

            case OBSERVATION_SET_TIME_SELECTION:
            case VIEW_MANUALLY_ENTERED_VITAL_SIGNS:
            {
                showModeSelectionOrGatewayNotConfiguredYet();

                setBackButtonText(getResources().getString(R.string.back));
            }
            break;

            case OBSERVATION_SET_MEASUREMENT_KEYPAD_ENTRY:
            case OBSERVATION_SET_MEASUREMENT_BUTTON_ENTRY:
            case OBSERVATION_SET_CONFIRMATION:
            {
                observationSetVitalSignSelectionSelected();
            }
            break;

            case ANNOTATION_ENTRY_SELECT_ANNOTATION_TIME:
            {
                if (features_enabled.predefined_annotations_enabled)
                {
                    enterAnnotationsSelected();
                }
                else
                {
                    showModeSelectionOrGatewayNotConfiguredYet();
                }
            }
            break;

            case ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_CONDITION:
            case ANNOTATION_ENTRY_ENTER_ANNOTATION_VIA_KEYBOARD:
            case ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_ACTION:
            case ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_OUTCOME:
            case ANNOTATION_ENTRY_CONFIRMATION:
            {
                enterAnnotationsSelected();
            }
            break;

            case INSTALLATION_MODE_SERVER_ADDRESS_SCAN:
            case INSTALLATION_MODE_PROGRESS:
            {
                restartSetupWizard();
            }
            break;

            case LOGCAT_DISPLAY:
            {
                showLogCat(false);
            }
            break;

            case VIDEO_CALL_CONTACTS:
            case VIDEO_CALL_SCHEDULE:
            {
                //videoCallModeSelectionSelected();

                // As Scheduled video calls are not working yet, go directly back to main page
                showModeSelectionOrGatewayNotConfiguredYet();
            }
            break;

            case VIDEO_CALL_CONTACTS_FROM_UNLOCK_PAGE:
            {
                lockScreenSelected();
            }
            break;

            case WEBPAGE_SELECTION:
            {
                showModeSelectionOrGatewayNotConfiguredYet();
            }
            break;

            default:
            {
                Log.d(TAG, "backButtonPressed - but current_page value not handled = " + current_page);
            }
            break;
        }
    }


    public void showSetupModeBlobs(boolean show)
    {
        Log.d(TAG, "showSetupModeBlobs on " + current_page + " show? " + show);

        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
        {
            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.showSetupModeBlobs(show);
            }
        }
    }


    public boolean getSetupModeBlobsShouldBeShown()
    {
        return footer_fragment.isShowSetupModeBlobsChecked();
    }


    public void lockButtonPressed()
    {
        if (isVideoCallPopupShowing())
        {
            Log.e(TAG, "Ignoring call to lockButtonPressed as Incoming Video Call popup on screen : " + current_page);
            return;
        }

        if (features_enabled.manufacturing_mode == false)
        {
            Log.d(TAG, "lockButtonPressed on " + current_page);

            storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_USER, gateway_user_id);

            switch (current_page)
            {
                case PATIENT_DETAILS_NAME:
                {
                    setHospitalPatientId("");                                               // Clear the Patient ID. This forces the user to enter it again (and therefore do a Server unique test)
                    lockScreenSelected();
                }
                break;

                case ADMIN_MODE:
                {
                    portal_system_commands.sendGatewayCommand_getLocationEnabled();

                    if (needToStartInstallationWizard())
                    {
                        startInstallationWizard();
                    }
                    else
                    {
                        lockScreenSelected();
                    }
                }
                break;

                default:
                {
                    lockScreenSelected();
                }
                break;
            }
        }
        else
        {
            // In Manufacturing Mode.

            switch (current_page)
            {
                case UNLOCK_SCREEN:
                {
                    showModeSelectionOrGatewayNotConfiguredYet();
                }
                break;

                case PATIENT_VITALS_DISPLAY:
                {
                    changeSessionSettingsPressed();
                }
                break;

                default:
                {
                    lockScreenSelected();
                }
                break;
            }
        }
    }


    public void createNewSession()
    {
        createManualVitalSignInfos();

        // Empty out the Graph caches
        emptyGraphCaches();

        // Send Create New Session command to Gateway
        portal_system_commands.sendGatewayCommand_createNewSessionCommand(patient_info, gateway_user_id);
    }


    private void emptyGraphCaches()
    {
        measurement_cache.clearAll();
        setup_mode_log_cache.clearAll();
        heart_beat_cache.clear();
    }


    public int getMeasurementIntervalInSecondsIfPresent(SensorType sensor_type)
    {
        for(DeviceInfo existing_device : cached_device_info_list)
        {
            if (existing_device.sensor_type == sensor_type)
            {
                return existing_device.measurement_interval_in_seconds;
            }
        }

        // If it doesn't exist, return Invalid
        return -1;
    }


    public int getMeasurementValidityTimeInSeconds(VitalSignType vital_sign_type)
    {
        MeasurementVitalSign measurement = getSavedMeasurement(vital_sign_type);

        if (measurement != null)
        {
            return measurement.measurement_validity_time_in_seconds;
        }
        else
        {
            return -1;
        }
    }

    private ThresholdSet getThresholdSetFromServersThresholdAgeBlockId(int server_age_block_id)
    {
        for (ThresholdSet thresholdSet : default_early_warning_score_threshold_sets)
        {
            for (ThresholdSetAgeBlockDetail age_block_detail : thresholdSet.list_threshold_set_age_block_detail)
            {
                if (age_block_detail.servers_database_row_id == server_age_block_id)
                {
                    return thresholdSet;
                }
            }
        }

        return null;
    }


    private ThresholdSet getThresholdSetFromServersThresholdSetId(int server_threshold_set_id)
    {
        for (ThresholdSet thresholdSet : default_early_warning_score_threshold_sets)
        {
            if (thresholdSet.servers_database_row_id == server_threshold_set_id)
            {
                return thresholdSet;
            }
        }

        return null;
    }


    private ThresholdSetAgeBlockDetail getThresholdAgeBlockFromServersThresholdAgeBlockId(int server_age_block_id)
    {
        for (ThresholdSet thresholdSet : default_early_warning_score_threshold_sets)
        {
            for (ThresholdSetAgeBlockDetail age_block_detail : thresholdSet.list_threshold_set_age_block_detail)
            {
                if (age_block_detail.servers_database_row_id == server_age_block_id)
                {
                    return age_block_detail;
                }
            }
        }

        return null;
    }


    private void setupGraphColoursFromThresholds(ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail)
    {
        ArrayList<ArrayList<ThresholdSetLevel>> threshold_blocks_by_device = thresholdSetAgeBlockDetail.list_of_threshold_set_levels_by_measurement_type;

        if (threshold_blocks_by_device.size() >= 12)
        {
            graph_configs.clear();

            ArrayList<ThresholdSetColour> colours = thresholdSetAgeBlockDetail.list_of_threshold_set_colours;

            for (ArrayList<ThresholdSetLevel> device_specific_list : threshold_blocks_by_device)
            {
                for (ThresholdSetLevel level : device_specific_list)
                {
                    float less_than_value = level.top;
                    float greater_than_or_equal_value = level.bottom;

                    int ews_value = level.early_warning_score;

                    DrawableAndBackgroundAndTextColour buttonAndBackgroundAndTextColour = getButtonAndTextColourFromEwsScore(ews_value, colours);

                    MeasurementTypes type = MeasurementTypes.values()[level.measurement_type];

                    GraphColourBand graph_colour_band = new GraphColourBand(ews_value, less_than_value, greater_than_or_equal_value, buttonAndBackgroundAndTextColour.background_colour, buttonAndBackgroundAndTextColour.text_colour);

                    switch (type)
                    {
                        case HEART_RATE:
                        {
                            graph_configs.getGraphConfig(VitalSignType.HEART_RATE).add(graph_colour_band);
                        }
                        break;

                        case RESPIRATION_RATE:
                        {
                            graph_configs.getGraphConfig(VitalSignType.RESPIRATION_RATE).add(graph_colour_band);
                        }
                        break;

                        case TEMPERATURE:
                        {
                            graph_configs.getGraphConfig(VitalSignType.TEMPERATURE).add(graph_colour_band);
                        }
                        break;

                        case SPO2:
                        {
                            graph_configs.getGraphConfig(VitalSignType.SPO2).add(graph_colour_band);
                        }
                        break;

                        case BLOOD_PRESSURE:
                        {
                            graph_configs.getGraphConfig(VitalSignType.BLOOD_PRESSURE).add(graph_colour_band);
                        }
                        break;

                        case WEIGHT:
                        {
                            graph_configs.getGraphConfig(VitalSignType.WEIGHT).add(graph_colour_band);
                        }
                        break;

                        case EARLY_WARNING_SCORE:
                        {
                            graph_configs.getGraphConfig(VitalSignType.EARLY_WARNING_SCORE).add(graph_colour_band);
                        }
                        break;

                        case CONSCIOUSNESS_LEVEL:
                        {
                            graph_configs.getGraphConfig(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL).add(graph_colour_band);
                        }
                        break;

                        case SUPPLEMENTAL_OXYGEN:
                        {
                            graph_configs.getGraphConfig(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN).add(graph_colour_band);
                        }
                        break;

                        case CAPILLARY_REFILL_TIME:
                        {
                            graph_configs.getGraphConfig(VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME).add(graph_colour_band);
                        }
                        break;

                        case RESPIRATION_DISTRESS:
                        {
                            graph_configs.getGraphConfig(VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS).add(graph_colour_band);
                        }
                        break;

                        case FAMILY_OR_NURSE_CONCERN:
                        {
                            graph_configs.getGraphConfig(VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN).add(graph_colour_band);
                        }
                        break;

                        case URINE_OUTPUT:
                        {
                            graph_configs.getGraphConfig(VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT).add(graph_colour_band);
                        }
                        break;

                        case HEART_BEAT:
                        case BLOOD_PRESSURE_ENUM_NO_LONGER_USED_BUT_REQUIRED_FOR_BACKWARDS_COMAPTIBILITY:
                        case PATIENT_ORIENTATION:
                        case UNKNOWN:
                            break;
                    }
                }
            }
        }
        else
        {
            Log.d(TAG, "Some or all of thresholds by measurement type not yet received.");
        }
    }



    private ThresholdSetAgeBlockDetail getThresholdSetAgeBlockDetailFromRadioButtonNumber(int radio_button_number)
    {
        int counter = 0;

        for (ThresholdSet threshold_set : default_early_warning_score_threshold_sets)
        {
            for (ThresholdSetAgeBlockDetail age_block_detail : threshold_set.list_threshold_set_age_block_detail)
            {
                if (counter == radio_button_number)
                {
                    // Got the right set of Thresholds
                    return age_block_detail;
                }

                counter++;
            }
        }

        return null;
    }

    public int getRadioButtonNumberFromThresholdSetAgeBlockDetail(ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail)
    {
        int counter = 0;

        for (ThresholdSet threshold_set : default_early_warning_score_threshold_sets)
        {
            for (ThresholdSetAgeBlockDetail age_block_detail : threshold_set.list_threshold_set_age_block_detail)
            {
                if (thresholdSetAgeBlockDetail.servers_database_row_id == age_block_detail.servers_database_row_id)
                {
                    return counter;
                }

                counter++;
            }
        }

        return -1;
    }



    public void PatientAgeSelected(int radio_button_number)
    {
        Log.e(TAG, "PatientAgeSelected : radio_button_number = " + radio_button_number);

        ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = getThresholdSetAgeBlockDetailFromRadioButtonNumber(radio_button_number);

        if (thresholdSetAgeBlockDetail != null)
        {
            ThresholdSet thresholdSet = getThresholdSetFromServersThresholdAgeBlockId(thresholdSetAgeBlockDetail.servers_database_row_id);

            Log.e(TAG, "PatientAgeSelected : " + thresholdSetAgeBlockDetail.display_name);

            setAgeRange(thresholdSet, thresholdSetAgeBlockDetail);

            if (current_page == UserInterfacePage.PATIENT_THRESHOLD_CATEGORY)
            {
                FragmentPatientDetailsAge fragment = (FragmentPatientDetailsAge) this.getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    fragment.writeTextTextExplainingWhatRadioButtonsDo(thresholdSetAgeBlockDetail.display_name);
                }
            }
        }
    }


    public long getSessionStartDate()
    {
        return session_start_date;
    }

/*
    public long getSessionStartTime()
    {
        return session_start_time;
    }
*/


    public long getEarliestCachedTimestamp()
    {
        return measurement_cache.getEarliestTimestampInCache();
    }


    public long getSessionStartMilliseconds()
    {
        return session_start_milliseconds;
    }


    public ArrayList<? extends MeasurementVitalSign> getCachedMeasurements(VitalSignType vital_sign_type)
    {
        return measurement_cache.getCachedMeasurements(vital_sign_type);
    }


    public Bundle getGraphColourBands(VitalSignType vital_sign)
    {
        Bundle bundle = new Bundle();

        GraphConfigs.GraphConfig graph_config = getGraphConfig(vital_sign);
        if (graph_config != null)
        {
            bundle.putParcelableArrayList("graph_colour_bands", graph_config.graph_colour_bands);
        }

        return bundle;
    }


    public GraphConfigs.GraphConfig getGraphConfig(VitalSignType vital_sign_type)
    {
        return graph_configs.getGraphConfig(vital_sign_type);
    }


    private void showConnectionButtonIfValid()
    {
        int number_of_devices = 0;

        // Need to make sure there are real physical devices to connect to....in order to show the "Connect" button.
        for(DeviceInfo device_info : cached_device_info_list)
        {
            if((device_info.isDeviceHumanReadableDeviceIdValid()) && (device_info.isDeviceTypeASensorDevice()))
            {
                if(device_info.scanRequired())
                {
                    number_of_devices++;
                }
                else
                {
                    Log.d(TAG, "showConnectionButtonIfValid : Device already connected");
                }
            }
        }

        if (number_of_devices > 0)
        {
            setNextButtonText(getResources().getString(R.string.textConnect));

            enableNavigationButtons(true, true, true);
        }
        else
        {
            enableNavigationButtons(true, true, false);
        }
    }


    private void resetCachedDeviceInfoList()
    {
        cached_device_info_list.clear();
    }


    public DeviceInfo getDeviceByType(SensorType sensor_type)
    {
        for(DeviceInfo device_info : cached_device_info_list)
        {
            if (device_info.sensor_type == sensor_type)
            {
                return device_info;
            }
        }

        return getDeviceByType(DeviceType.DEVICE_TYPE__INVALID);
    }


    public DeviceInfo getDeviceByType(DeviceType device_type)
    {
        for(DeviceInfo device_info : cached_device_info_list)
        {
            if (device_info.device_type == device_type)
            {
                return device_info;
            }
        }

        // Device not in cached list, so return a dummy device.
        return new DeviceInfo(device_type);
    }


    /**
     * Get a list of all devices currently in the session (as far as the UI knows).
     *
     * Excludes GATEWAY_INFO sensor types as they aren't meaningfully sensor devices
     *
     * Also excludes EWS if it's the only device present, as it shouldn't show as an in use device
     * in that case, as it won't have any data until other sensors are added
     *
     * @return ArrayList<DeviceInfo> devices_in_use - sensor devices, algorithms and manual vital devices only.
     */
    public ArrayList<DeviceInfo> getDeviceTypesInUse()
    {
        ArrayList<DeviceInfo> devices_in_use = new ArrayList<>();

        for(DeviceInfo device_info : cached_device_info_list)
        {
            if((device_info.sensor_type != SensorType.SENSOR_TYPE__GATEWAY_INFO) && device_info.isDeviceTypePartOfPatientSession())
            {
                devices_in_use.add(device_info);
            }
        }

        // Special case for EWS
        // The user can start a Manual Vitals Only session with Auto Add EWS enabled, but not enter in any manual vitals. They can then go to the Patient Vitals page
        if (devices_in_use.size() == 1)
        {
            if (devices_in_use.get(0).device_type == DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE)
            {
                devices_in_use.clear();
            }
        }

        return devices_in_use;
    }


    public ArrayList<DeviceInfo> getBluetoothDevicesTypesInUse()
    {
        ArrayList<DeviceInfo> devices_in_use = new ArrayList<>();

        for(DeviceInfo device_info : cached_device_info_list)
        {
            if (device_info.isDeviceTypeASensorDevice())
            {
                if(device_info.isDeviceHumanReadableDeviceIdValid())
                {
                    devices_in_use.add(device_info);
                }
            }
        }

        for(DeviceInfo device_info : devices_in_use)
        {
            Log.d(TAG, "getBluetoothDevicesTypesInUse : devices_in_use : " + device_info.getSensorTypeAndDeviceTypeAsString());
        }

        return getDevicesInUseInSensorTypeOrder(devices_in_use);
    }


    @NonNull
    private ArrayList<DeviceInfo> getDevicesInUseInSensorTypeOrder(ArrayList<DeviceInfo> devices_in_use)
    {
        // If we have scanned a UA767 and a Nonin BLE then because of the order of the DeviceTypes
        // devices_in_use will contain (and display) the BP before the SpO2 even though the Gateway
        // will connect to the SpO2 first

        // Need to make sure that devices_in_use is in Sensor Type order

        ArrayList<DeviceInfo> devices_in_use_in_sensor_type_order = new ArrayList<>();

        for(DeviceInfo device_info : devices_in_use)
        {
            if (device_info.sensor_type == SensorType.SENSOR_TYPE__LIFETOUCH)
            {
                devices_in_use_in_sensor_type_order.add(device_info);
            }
        }

        for(DeviceInfo device_info : devices_in_use)
        {
            if (device_info.sensor_type == SensorType.SENSOR_TYPE__TEMPERATURE)
            {
                devices_in_use_in_sensor_type_order.add(device_info);
            }
        }

        for(DeviceInfo device_info : devices_in_use)
        {
            if (device_info.sensor_type == SensorType.SENSOR_TYPE__SPO2)
            {
                devices_in_use_in_sensor_type_order.add(device_info);
            }
        }

        for(DeviceInfo device_info : devices_in_use)
        {
            if (device_info.sensor_type == SensorType.SENSOR_TYPE__BLOOD_PRESSURE)
            {
                devices_in_use_in_sensor_type_order.add(device_info);
            }
        }

        for(DeviceInfo device_info : devices_in_use)
        {
            if (device_info.sensor_type == SensorType.SENSOR_TYPE__WEIGHT_SCALE)
            {
                devices_in_use_in_sensor_type_order.add(device_info);
            }
        }

        return devices_in_use_in_sensor_type_order;
    }


    public int getNumberOfDevicesToShow()
    {
        int number_of_devices = 0;

        for(DeviceInfo device : cached_device_info_list)
        {
            if(device.dummy_data_mode)
            {
                number_of_devices++;
            }
        }

        return number_of_devices;
    }


    public int getNumberOfBluetoothDevicesToConnect()
    {
        int number_of_devices = 0;

        for(DeviceInfo device_info : cached_device_info_list)
        {
            if(device_info.isDeviceHumanReadableDeviceIdValid())
            {
                if (device_info.isDeviceTypeASensorDevice())
                {
                    number_of_devices++;
                }
                else
                {
                    Log.d(TAG, "getView : getNumberOfBluetoothDevicesToConnect : Not added device type = " + device_info.getSensorTypeAndDeviceTypeAsString());
                }
            }
        }

        return number_of_devices;
    }


    private void addOrUpdateDeviceInfo(DeviceInfo device)
    {
        if(device.isDeviceTypeASensorDevice())
        {
            addOrUpdateUniqueDeviceInfo(device);
        }
        else
        {
            addOrUpdateNonUniqueDeviceInfo(device);
        }
    }


    /**
     * Used for Lifetouch, Lifetemp, SpO2 and BP sensor devices
     * </p>
     * There should only ever be one of these at a time, so use sensor_type to find it.
     * This should ensure that we don't create a new device for (e.g.) the Nonin BTLE if a nonin classic
     * was previously connected.
     *
     * @param device the device info to add
     */
    private void addOrUpdateUniqueDeviceInfo(DeviceInfo device)
    {
        int index = 0;

        for(DeviceInfo cached_device_info : cached_device_info_list)
        {
            if (cached_device_info.sensor_type == device.sensor_type)
            {
                cached_device_info_list.set(index, device);
                return;
            }

            index++;
        }

        cached_device_info_list.add(device);
    }


    /**
     * Used for manual vitals, algorithms, and gateway info.
     * </p>
     * It's possible for there to be multiple of each of these sensor types, so use device type to find it.
     * We still expect there to be only one of each manual vital etc. so can safely use device type.
     */
    private void addOrUpdateNonUniqueDeviceInfo(DeviceInfo device)
    {
        int index = 0;

        for(DeviceInfo cached_device_info : cached_device_info_list)
        {
            if (cached_device_info.device_type == device.device_type)
            {
                cached_device_info_list.set(index, device);
                return;
            }

            index++;
        }

        cached_device_info_list.add(device);
    }


    private void resetCachedDeviceInfo(DeviceType device_type)
    {
        int index = 0;

        for(DeviceInfo cached_device_info : cached_device_info_list)
        {
            if(cached_device_info.device_type == device_type)
            {
                cached_device_info_list.set(index, new DeviceInfo(device_type));
                return;
            }

            index++;
        }
    }

    private void setAgeRange(ThresholdSet thresholdSet, ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetails)
    {
        if ((thresholdSet != null) && (thresholdSetAgeBlockDetails != null))
        {
            patient_info.setThresholdSet(thresholdSet);

            patient_info.setThresholdSetAgeBlockDetails(thresholdSetAgeBlockDetails);

            setupGraphColoursFromThresholds(patient_info.getThresholdSetAgeBlockDetails());

            if (UtilityFunctions.isFragmentAddedAndResumed(header_fragment))
            {
                header_fragment.setAgeRange(patient_info.getThresholdSetAgeBlockDetails());
            }
        }
        else
        {
            if (UtilityFunctions.isFragmentAddedAndResumed(header_fragment))
            {
                header_fragment.setAgeRange(null);
            }
        }
    }

    public ThresholdSetAgeBlockDetail getThresholdSetAgeBlockDetails()
    {
        return patient_info.getThresholdSetAgeBlockDetails();
    }


    private void setHeaderHospitalPatientIdText(String patient_id)
    {
        if (UtilityFunctions.isFragmentAddedAndResumed(header_fragment))
        {
            header_fragment.setHospitalPatientIdText(patient_id);
        }
    }


    // This only is ONLY called when Patient Name Lookup is enabled on the Admin page
    public void setHospitalPatientId(String desired_hospital_patient_id, FullPatientDetails fullPatientDetails)
    {
        patient_info.setHospitalPatientId(desired_hospital_patient_id);

        String headerPatientName = fullPatientDetails.firstName + " " + fullPatientDetails.lastName + " (" + fullPatientDetails.dateOfBirth + ")";
        setHeaderHospitalPatientIdText(headerPatientName);
    }


    public void setHospitalPatientId(String desired_hospital_patient_id)
    {
        patient_info.setHospitalPatientId(desired_hospital_patient_id);

        setHeaderHospitalPatientIdText(patient_info.getHospitalPatientId());
    }


    public PatientInfo getPatientInfo()
    {
        return patient_info;
    }


    public void endSessionPressed(long session_end_time)
    {
        Log.d(TAG, "endSessionPressed : End session @  " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(session_end_time));

        // Only show this if there are Isansys sensors as part of the session
        if (getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH).isDeviceSessionInProgress() ||
                getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE).device_type == DeviceType.DEVICE_TYPE__LIFETEMP_V2 && getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE).isDeviceSessionInProgress())
        {
            showRecyclingReminder();
        }

        endSession(true, session_end_time);
    }


    public void transferSessionPressed(long session_end_time)
    {
        Log.d(TAG, "transferSessionPressed : Transfer session @ " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(session_end_time));

        endSession(false, session_end_time);
    }


    private void endSession(boolean turn_devices_off, long session_end_time)
    {
        Log.d(TAG, "sendGatewayCommand_endExistingSession : end session time = " + TimestampConversion.convertDateToHumanReadableStringWeekdayMonthDayHoursMinutes(session_end_time));
        portal_system_commands.sendGatewayCommand_endExistingSession(gateway_user_id, turn_devices_off, session_end_time);

        resetCachedSessionInfo();
    }


    public void resetPatientInfo()
    {
        patient_info = new PatientInfo();

        setHospitalPatientId(patient_info.getHospitalPatientId());
        setAgeRange(null, null);
    }


    private void showStartMonitoringButton()
    {
        Log.d(TAG, "showStartMonitoringButton");
        enableNavigationButtons(true, true, true);
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textStartMonitoring));

        // Also remove any "Search Again" buttons
        FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            fragment.hideAllSearchAgainButtons();
        }
    }


    private void hideStartMonitoringButton()
    {
        Log.d(TAG, "hideStartMonitoringButton");
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
    }


    public void enableRealTimeLink(boolean enabled)
    {
        Log.d(TAG, "enableRealTimeLink : " + enabled);

        footer_fragment.enableRealTimeServerConnectionStatusIndicator(enabled);
        portal_system_commands.sendGatewayCommand_enableRealTimeLink(enabled);
    }


    public void enableServerDataSync(boolean enabled)
    {
        Log.d(TAG, "enableServerDataSync : " + enabled);

        footer_fragment.enableServerConnectionStatusIndicator(enabled);
        portal_system_commands.sendGatewayCommand_enableServerSync(enabled);
    }


    public boolean getServerSyncingEnableStatusVariableOnly()
    {
        return server_sync_enabled;
    }


    public void turnOffRealTimeStreaming()
    {
        footer_fragment.enableRealTimeServerConnectionStatusIndicator(false);

        portal_system_commands.sendGatewayCommand_enableRealTimeLink(false);
    }


    public String getPatientGatewaySoftwareVersionNumber()
    {
        return app_versions.getGatewayVersionNumber();
    }


    public String getUserInterfaceSoftwareVersionNumber()
    {
        return app_versions.getUserInterfaceVersionNumber();
    }


    public void setServerAddress(String desired_server_address)
    {
        portal_system_commands.sendGatewayCommand_setServerAddress(desired_server_address);
    }


    public void getServerPort()
    {
        portal_system_commands.sendGatewayCommand_getServerPort();
    }


    public void setServerPort(String desired_server_port)
    {
        portal_system_commands.sendGatewayCommand_setServerPort(desired_server_port);
    }


    public void getRealTimeServerPort()
    {
        portal_system_commands.sendGatewayCommand_getRealTimeServerPort();
    }


    public void setRealTimeServerPort(String desired_real_time_server_port)
    {
        portal_system_commands.sendGatewayCommand_setRealTimeServerPort(desired_real_time_server_port);
    }


    public void getHttpsEnableStatus()
    {
        portal_system_commands.sendGatewayCommand_getHttpsEnableStatus();
    }


    public void getWebServiceAuthenticationEnabledStatus()
    {
        portal_system_commands.sendGatewayCommand_getWebServiceAuthenticationEnableStatus();
    }

    public void getWebServiceEncryptionEnabledStatus()
    {
        portal_system_commands.sendGatewayCommand_getWebServiceEncryptionEnableStatus();
    }

    public int getMainFragmentHeight()
    {
        // Find out the size of the Main Fragment so we can work out how big to make each of the rows in the ListAdapter
        FrameLayout main_fragment = this.findViewById(R.id.fragment_main);

        return main_fragment.getHeight();
    }


    // Called by the Change Session Settings and Dummy Data Mode - Remove XXXX button
    public void removeDeviceFromGateway(DeviceType device_type)
    {
        DeviceInfo device_info = getDeviceByType(device_type);

        // Only show the recycling popup if an Isansys sensor has just been removed...
        if (device_type == DeviceType.DEVICE_TYPE__LIFETOUCH  || device_type == DeviceType.DEVICE_TYPE__LIFETEMP_V2)
        {
            // ... ...and there is a device session running for it
            if (device_info.isDeviceSessionInProgress())
            {
                showRecyclingReminder();
            }
        }

        int device_session_ended_by_user_id = gateway_user_id;

        Log.d(TAG, "removeDeviceFromGateway : " + device_info.getSensorTypeAndDeviceTypeAsString());

        portal_system_commands.sendGatewayCommand_disconnectDevice(device_type, device_session_ended_by_user_id, true);

        device_info.resetDeviceOffBodyStates();
        device_info.resetDeviceInSetupModeStates();
        device_info.resetDeviceInRawAccelerometerModeStates();
    }


    public void removeDeviceFromGateway(SensorType sensor_type)
    {
        DeviceInfo device_info = getDeviceByType(sensor_type);

        // Store audit trail device removal here, not in removeDeviceFromGateway(DeviceType) because removeDeviceFromGateway() gets called for all devices when starting a new session
        String device_details = device_info.device_name + " " + device_info.human_readable_device_id;

        storeAuditTrailEvent(AuditTrailEvent.DEVICE_WAS_JUST_REMOVED, gateway_user_id, device_details);

        removeDeviceFromGateway(device_info.device_type);
    }


    public void searchAgainForDevice(DeviceInfo device_info)
    {
        if (device_info.isDeviceTypeASensorDevice())
        {
            Log.d(TAG, "searchAgainForDevice : " + device_info.getSensorTypeAndDeviceTypeAsString());

            portal_system_commands.sendGatewayCommand_retryConnectingToDevice(device_info.device_type);
        }
    }


    public void testServerLink()
    {
        portal_system_commands.sendGatewayCommand_pingServer();
    }


    private void setScreenBrightnessModeToManual()
    {
        if (permissions.haveWriteSettingsPermission())
        {
            Log.d(TAG, "setScreenBrightnessModeToManual");
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
    }


    private void setScreenBrightness(int desired_brightness_level)
    {
        if (permissions.haveWriteSettingsPermission())
        {
            Log.d(TAG, "setScreenBrightness = " + desired_brightness_level);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, desired_brightness_level);
        }
    }


    private void setScreenBrightnessFromFooter(int desired_brightness_level)
    {
        // Save the value of the Footer slider value. This is what the "OnTouch" event resets the screen brightness back to after the user touches the screen

        switch (day_or_night_mode)
        {
            case DAY_MODE:
            {
                current_screen_brightness_level_day = desired_brightness_level;
            }
            break;

            case NIGHT_MODE:
            {
                current_screen_brightness_level_night = desired_brightness_level;
            }
            break;
        }

        setScreenBrightness(desired_brightness_level);
    }


    public void incrementScreenBrightness()
    {
        int step = 10;

        // Save the value of the Footer slider value. This is what the "OnTouch" event resets the screen brightness back to after the user touches the screen
        switch (day_or_night_mode)
        {
            case DAY_MODE:
            {
                current_screen_brightness_level_day += step;

                int max_level = getMaximumBrightnessLevel();
                if (current_screen_brightness_level_day > max_level)
                {
                    current_screen_brightness_level_day = max_level;
                }

                setScreenBrightness(current_screen_brightness_level_day);

                footer_fragment.setScreenBrightnessSliderPosition(current_screen_brightness_level_day);
            }
            break;

            case NIGHT_MODE:
            {
                current_screen_brightness_level_night += step;

                int max_level = getMaximumBrightnessLevel();
                if (current_screen_brightness_level_night > max_level)
                {
                    current_screen_brightness_level_night = max_level;
                }

                setScreenBrightness(current_screen_brightness_level_night);

                footer_fragment.setScreenBrightnessSliderPosition(current_screen_brightness_level_night);
            }
            break;
        }
    }

    public void decrementScreenBrightness()
    {
        int step = 10;

        // Save the value of the Footer slider value. This is what the "OnTouch" event resets the screen brightness back to after the user touches the screen
        switch (day_or_night_mode)
        {
            case DAY_MODE:
            {
                current_screen_brightness_level_day -= step;

                int min_level = getMinimumBrightnessLevel();
                if (current_screen_brightness_level_day < min_level)
                {
                    current_screen_brightness_level_day = min_level;
                }

                setScreenBrightness(current_screen_brightness_level_day);

                footer_fragment.setScreenBrightnessSliderPosition(current_screen_brightness_level_day);
            }
            break;

            case NIGHT_MODE:
            {
                current_screen_brightness_level_night -= step;

                int min_level = getMinimumBrightnessLevel();
                if (current_screen_brightness_level_night < min_level)
                {
                    current_screen_brightness_level_night = min_level;
                }

                setScreenBrightness(current_screen_brightness_level_night);

                footer_fragment.setScreenBrightnessSliderPosition(current_screen_brightness_level_night);
            }
            break;
        }
    }


    public int getCurrentScreenBrightness()
    {
        float current_screen_brightness = 0;

        try
        {
            current_screen_brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (SettingNotFoundException e)
        {
            e.printStackTrace();
        }

        Log.d(TAG, "getCurrentScreenBrightness = " + current_screen_brightness);

        return (int)current_screen_brightness;
    }


    private int getStartupScreenBrightness()
    {
        return screen_brightness_level_max_day / 2;
    }


    public int getMinimumBrightnessLevel()
    {
        switch (day_or_night_mode)
        {
            case DAY_MODE:      return screen_brightness_level_min_day;
            case NIGHT_MODE:    return screen_brightness_level_min_night;
        }

        // Never used but function has to return something
        return 1;
    }


    public int getMaximumBrightnessLevel()
    {
        switch (day_or_night_mode)
        {
            case DAY_MODE:      return screen_brightness_level_max_day;
            case NIGHT_MODE:    return screen_brightness_level_max_night;
        }

        // Never used but function has to return something
        return 1;
    }


    public void enableNightMode(boolean night_mode_enabled)
    {
        if (permissions.haveOverlayPermission())
        {
            if (night_mode_enabled)
            {
                day_or_night_mode = DayOrNightMode.NIGHT_MODE;

                setScreenBrightnessFromFooter(current_screen_brightness_level_night);

                footer_fragment.setScreenBrightnessSliderPosition(current_screen_brightness_level_night);

                relativeLayoutMain.setBackgroundColor(ContextCompat.getColor(this, R.color.background_grey));

                showNightModeOverlay(true);
            }
            else
            {
                day_or_night_mode = DayOrNightMode.DAY_MODE;

                setScreenBrightnessFromFooter(current_screen_brightness_level_day);

                footer_fragment.setScreenBrightnessSliderPosition(current_screen_brightness_level_day);

                relativeLayoutMain.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                showNightModeOverlay(false);
            }

            // Tell the Gateway to put the devices into Night/Day mode
            portal_system_commands.sendGatewayCommand_enableNightMode(night_mode_enabled);
        }
    }

    public DayOrNightMode getNightModeStatus()
    {
        return day_or_night_mode;
    }


    public void getWardsAndBedsFromServer()
    {
        portal_system_commands.sendGatewayCommand_getWardsAndBedsFromServer();
    }


    public void getGatewayConfigFromServer()
    {
        portal_system_commands.sendGatewayCommand_getGatewayConfigFromServer();
    }


    public void getServerConfigurableTextFromServer()
    {
        portal_system_commands.sendGatewayCommand_getServerConfigurableTextFromServer();
    }


    public void getDefaultEarlyWarningScoreTypesFromServer()
    {
        portal_system_commands.sendGatewayCommand_getDefaultEarlyWarningScoreTypesFromServer();
    }


    public void getViewableWebPagesFromServer()
    {
        portal_system_commands.sendGatewayCommand_getViewableWebPagesFromServer();
    }


    public void emptyLocalDatabase()
    {
        portal_system_commands.sendGatewayCommand_emptyLocalDatabase();
    }


    public void emptyLocalDatabaseIncludingEwsThresholdSets()
    {
        portal_system_commands.sendGatewayCommand_emptyLocalDatabaseIncludingEwsThresholdSets();

        features_enabled.gateway_setup_complete = false;

        default_early_warning_score_threshold_sets.clear();

        cached_wards.clear();
        cached_beds.clear();

        serverConfigurableTextStrings.clear();

        updateAdminModeEarlyWarningScoreThresholdSets();
    }


    public void exportLocalDatabaseToAndroidRoot()
    {
        portal_system_commands.sendGatewayCommand_exportLocalDatabaseToAndroidRoot();
    }


    public void importDatabaseFromAndroidRoot()
    {
        portal_system_commands.sendGatewayCommand_importDatabaseFromAndroidRoot();
    }


    public void deleteOldExportedDatabases()
    {
        portal_system_commands.sendGatewayCommand_deleteOldExportedDatabases();
    }


    public boolean isPoorSignalInLastMinute()
    {
        return poor_signal_in_last_minute;
    }


    public boolean hasNoBeatsDetectedTimerFired()
    {
        return lifetouch_no_beats_detected;
    }


    public long getNtpTimeNowInMilliseconds()
    {
        return ntp_time.currentTimeMillis();
    }


    public long getLocalTimeNowInMilliseconds()
    {
        return getNtpTimeNowInMilliseconds() + getGmtOffsetInMilliseconds();
    }


    public void setDummyServerDetails()
    {
        portal_system_commands.sendGatewayCommand_setDummyServerDetails();
    }


    public ArrayList<ProgressItem> generateVerticalThresholdBars(ArrayList<GraphColourBand> graph_colour_bands, double graph_min, double graph_max)
    {
        ArrayList<ProgressItem> progressItemList = new ArrayList<>();
        ProgressItem mProgressItem;

        double range = graph_max - graph_min;

        if (graph_colour_bands != null)
        {
            // Convert the Graph Colour bands to percentages
            for (GraphColourBand graph_colour_band : graph_colour_bands)
            {
                mProgressItem = new ProgressItem();

                double temp = (graph_colour_band.less_than_value - graph_min) / range;
                temp = temp * 100;
                mProgressItem.progressItemPercentage = (float) temp;

                mProgressItem.color = graph_colour_band.band_colour;
                mProgressItem.label = String.valueOf((int) graph_colour_band.less_than_value);
                progressItemList.add(mProgressItem);
            }
        }

        return progressItemList;
    }


    public int getNumberOfGraphTicksFromRange(double range)
    {
        int integer_range = (int) range;

        if(integer_range % 55 == 0)
        {
            return 12;
        }
        else if(integer_range % 50 == 0)
        {
            return 11;
        }
        else if(integer_range % 45 == 0)
        {
            return 10;
        }
        else if(integer_range % 40 == 0)
        {
            return 9;
        }
        else if(integer_range % 35 == 0)
        {
            return 8;
        }
        else if(integer_range % 30 == 0)
        {
            return 7;
        }
        else if(integer_range % 25 == 0)
        {
            return 6;
        }
        else if(integer_range % 20 == 0)
        {
            return 5;
        }
        else if(integer_range % 15 == 0)
        {
            return 4;
        }
        else
        {
            return 5;
        }
    }


    public LinkedList<HeartBeatInfo> getHeartBeatList(long start_time, long end_time)
    {
        long start_function_time = System.nanoTime();

        LinkedList<HeartBeatInfo> return_list =  heart_beat_cache.getHeartBeatList(start_time, end_time);

        long total_time = (System.nanoTime() - start_function_time) / 1000000;
        Log.e(TAG, "getHeartBeatList time = " + total_time);

        return return_list;
    }


    public void getServerAddress()
    {
        portal_system_commands.sendGatewayCommand_getServerAddress();
    }


    public void getServerSyncEnableStatus()
    {
        portal_system_commands.sendGatewayCommand_getServerSyncEnableStatus();
    }


    public void getRealTimeLinkEnableStatus()
    {
        portal_system_commands.sendGatewayCommand_getRealtimeLinkEnableStatus();
    }


    public void getGatewaysAssignedBedDetails()
    {
        portal_system_commands.sendGatewayCommand_getGatewaysAssignedBedDetails();
    }


    public void radioOff(byte time_till_off, byte time_off)
    {
        portal_system_commands.sendGatewayCommand_radioOff(time_till_off, time_off);
    }


    public void getAllDeviceInfosFromGateway()
    {
        portal_system_commands.sendGatewayCommand_getAllDeviceInfo();
    }


    public void useHttpsForServerConnection(boolean useHttps)
    {
        Log.d(TAG, "Use HTTPS = " + useHttps);
        portal_system_commands.sendGatewayCommand_setHttpsEnableStatus(useHttps);
    }


    public void setWebServiceAuthenticationEnabled(boolean checked)
    {
        Log.d(TAG, "Use WebService Authentication = " + checked);
        portal_system_commands.sendGatewayCommand_enableWebServiceAuthentication(checked);
    }

    public void setWebServiceEncryptionEnabled(boolean checked)
    {
        Log.d(TAG, "Use WebService Encryption = " + checked);
        portal_system_commands.sendGatewayCommand_enableWebServiceEncryption(checked);
    }


    public void setServerPatientNameLookupEnabled(boolean checked)
    {
        Log.d(TAG, "setServerPatientNameLookupEnabled = " + checked);
        portal_system_commands.sendGatewayCommand_setPatientNameLookupEnabled(checked);
    }


    public void setSimpleHeartRateEnabled(boolean checked)
    {
        Log.d(TAG, "setSimpleHeartRateEnabled = " + checked);
        portal_system_commands.sendGatewayCommand_setSimpleHeartRateEnabled(checked);
    }


    public void setGsmModeOnlyEnabled(boolean checked)
    {
        Log.d(TAG, "setGsmModeOnlyEnabled = " + checked);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reminder");
        builder.setMessage("Changes to GSM mode are ONLY valid after restarting the Gateway");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        portal_system_commands.sendGatewayCommand_setGsmModeOnlyEnabled(checked);
    }


    public void setDeveloperPopupEnabled(boolean enabled)
    {
        Log.d(TAG, "setDeveloperPopupEnabled = " + enabled);

        portal_system_commands.sendGatewayCommand_storeDeveloperPopupEnabled(enabled);
    }


    public boolean getDeveloperPopupEnabled()
    {
        boolean enabled = features_enabled.developer_popup;
        Log.d(TAG, "getDeveloperPopupEnabled = " + enabled);
        return enabled;
    }


    public void setShowIpAddressOnWifiPopupEnabled(boolean enabled)
    {
        Log.d(TAG, "setShowIpAddressOnWifiPopupEnabled = " + enabled);

        portal_system_commands.sendGatewayCommand_storeShowIpAddressOnWifiPopupEnabled(enabled);
    }


    public boolean getShowIpAddressOnPopupEnabled()
    {
        boolean enabled = features_enabled.show_ip_address_on_wifi_popup;
        Log.d(TAG, "getShowIpAddressOnPopupEnabled = " + enabled);
        return enabled;
    }


    public void setCsvOutputEnableCheckedByAdmin(boolean checked)
    {
        Log.d(TAG, "CSV Output Enabled = " + checked);
        portal_system_commands.sendGatewayCommand_setCsvOutputEnabled(checked);
    }


    // Required for if changing Periodic Setup mode timers or enabled status if Periodic Setup mode is enabled AND active whilst user changes are being made
    private void stopPeriodicSetupModeIfActive()
    {
        Log.d(TAG, "stopPeriodicSetupModeIfActive()");

        for(DeviceInfo device_info : cached_device_info_list)
        {
            if (device_info.isDeviceTypeASensorDevice())
            {
                if (device_info.isDeviceHumanReadableDeviceIdValid())
                {
                    Log.d(TAG, "stopPeriodicSetupModeIfActive() : " + device_info.getSensorTypeAndDeviceTypeAsString());

                    if (device_info.isDeviceInPeriodicSetupMode())
                    {
                        device_info.setDeviceInPeriodicSetupMode(false);

                        sendStopSetupModeCommand(device_info);
                    }
                }
            }
        }
    }


    public void setDevicePeriodicSetupModeEnabled(boolean checked)
    {
        Log.d(TAG, "setDevicePeriodicSetupModeEnabled = " + checked);

        if (checked == false)
        {
            stopPeriodicSetupModeIfActive();
        }

        portal_system_commands.sendGatewayCommand_enableDevicePeriodicSetupMode(checked);
    }


    public void getPatientNameLookupEnabled()
    {
        portal_system_commands.sendGatewayCommand_getPatientNameLookupEnableStatus();
    }


    public void getSimpleHeartRateEnabled()
    {
        portal_system_commands.sendGatewayCommand_getSimpleHeartRateEnabled();
    }


    public void getGsmModeOnlyEnabled()
    {
        portal_system_commands.sendGatewayCommand_getGsmModeOnlyEnabled();
    }


    public boolean getCsvOutputEnableStatus()
    {
        return features_enabled.csv_output;
    }

    public void getLifetouchPeriodicSamplingStatus()
    {
        portal_system_commands.sendGatewayCommand_getLifetouchPeriodicSamplingStatus();
    }

    // Below functions are called from the Dummy Data Mode fragment
    public void createNewDummyDataPatient()
    {
        // Create a new Patient using an auto generated Hospital Patient ID, and Age
        String desired_patient_id = patient_gateways_assigned_ward_name + ":" + patient_gateways_assigned_bed_name + " @ " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(getNtpTimeNowInMilliseconds());
        setHospitalPatientId(desired_patient_id);

        if (current_page == UserInterfacePage.DUMMY_DATA_MODE)
        {
            FragmentDummyDataMode fragment = (FragmentDummyDataMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.setHospitalPatientID(desired_patient_id);
            }
        }
    }


    public void addDummyLifetouch(DeviceType device_type)
    {
        long human_readable_device_id = 100001;
        String bluetooth_device_address = "AA:BB:CC:DD:EE:FF";

        DeviceInfo device = new DeviceInfo(device_type);
        device.human_readable_device_id = human_readable_device_id;
        device.bluetooth_address = bluetooth_device_address;
        device.device_name = getDeviceNameByType(device_type);
        device.dummy_data_mode = true;
        device.firmware_version = DUMMY_FIRMWARE_VERSION;

        portal_system_commands.sendGatewayCommand_setDesiredDevice(device);
    }


    public void addDummyLifetemp(DeviceType device_type)
    {
        long human_readable_device_id = 100002;
        String bluetooth_device_address = "AA:BB:CC:DD:EE:FF";

        DeviceInfo device = new DeviceInfo(device_type);
        device.human_readable_device_id = human_readable_device_id;
        device.bluetooth_address = bluetooth_device_address;
        device.device_name = getDeviceNameByType(device_type);
        device.measurement_interval_in_seconds = getLifetempMeasurementInterval();
        device.dummy_data_mode = true;
        device.firmware_version = DUMMY_FIRMWARE_VERSION;

        portal_system_commands.sendGatewayCommand_setDesiredDevice(device);
    }


    public void addDummyPulseOx(DeviceType device_type)
    {
        long human_readable_device_id = 100003;
        String bluetooth_device_address = "AA:BB:CC:DD:EE:FF";

        DeviceInfo device = new DeviceInfo(device_type);
        device.human_readable_device_id = human_readable_device_id;
        device.bluetooth_address = bluetooth_device_address;
        device.device_name = getDeviceNameByType(device_type);
        device.dummy_data_mode = true;

        portal_system_commands.sendGatewayCommand_setDesiredDevice(device);
    }


    public void addDummyBloodPressure(DeviceType device_type)
    {
        long human_readable_device_id = 100004;
        String bluetooth_device_address = "AA:BB:CC:DD:EE:FF";

        DeviceInfo device = new DeviceInfo(device_type);
        device.human_readable_device_id = human_readable_device_id;
        device.bluetooth_address = bluetooth_device_address;
        device.device_name = getDeviceNameByType(device_type);
        device.dummy_data_mode = true;

        portal_system_commands.sendGatewayCommand_setDesiredDevice(device);
    }


    public void addDummyWeightScale(DeviceType device_type)
    {
        long human_readable_device_id = 100005;
        String bluetooth_device_address = "AA:BB:CC:DD:EE:FF";

        DeviceInfo device = new DeviceInfo(device_type);
        device.human_readable_device_id = human_readable_device_id;
        device.bluetooth_address = bluetooth_device_address;
        device.device_name = getDeviceNameByType(device_type);
        device.dummy_data_mode = true;

        portal_system_commands.sendGatewayCommand_setDesiredDevice(device);
    }


    public void removeDummyDevice(SensorType sensor_type)
    {
        removeDeviceFromGatewayAndRemoveFromEwsIfRequested(sensor_type);
    }


    public void removeDeviceFromGatewayAndRemoveFromEwsIfRequested(SensorType sensor_type)
    {
        DeviceInfo deviceInfo = getDeviceByType(sensor_type);
        if (isEarlyWarningScoresDeviceSessionInProgress() && deviceInfo.isDeviceSessionInProgress())
        {
            showRemoveFromEwsPopup(sensor_type);
        }
        else
        {
            removeDeviceFromGatewayAndClearDesiredDeviceInPatientGateway(sensor_type);
        }
    }


    public void removeDeviceFromGatewayAndClearDesiredDeviceInPatientGateway(SensorType sensor_type)
    {
        removeDeviceFromGateway(sensor_type);
        clearDesiredDeviceInPatientGateway(sensor_type);
    }


    public void getDummyDataModeDeviceLeadsOffEnabledStatus(SensorType sensor_type)
    {
        portal_system_commands.sendGatewayCommand_getDummyDataModeDeviceLeadsOffEnabledStatus(sensor_type);
    }


    public void getDummyDataModeSpoofEarlyWarningScores()
    {
        portal_system_commands.sendGatewayCommand_getDummyDataModeSpoofEarlyWarningScores();
    }


    public void simulateDeviceLeadsOff(SensorType sensor_type, boolean simulate_leads_off)
    {
        portal_system_commands.sendGatewayCommand_forceDeviceLeadsOffState(sensor_type, simulate_leads_off);
    }


    public void sendTimeSyncCommand()
    {
        portal_system_commands.sendGatewayCommand_timeSync();
    }


    public void setSavedMeasurement(VitalSignType vital_sign_type, MeasurementVitalSign measurement)
    {
        if (saved_measurements.isMeasurementValid(vital_sign_type, measurement))
        {
            saved_measurements.setSavedMeasurement(vital_sign_type, measurement);
        }
        else
        {
            saved_measurements.reset(vital_sign_type);
        }
    }


    public MeasurementVitalSign getSavedMeasurement(VitalSignType vital_sign_type)
    {
        return saved_measurements.getSavedMeasurement(vital_sign_type);
    }


    public boolean isSavedMeasurementValid(VitalSignType vital_sign_type)
    {
        return saved_measurements.isSavedMeasurementValid(vital_sign_type);
    }


    public void setUsaMode(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setUsaModeEnabledStatus(enabled);
    }

    public boolean getUsaMode()
    {
        return features_enabled.usa_mode;
    }


    public void setShowMacAddressOnStatus(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setShowMacAddressEnabledStatus(enabled);
    }

    public boolean getShowMacAddressOnStatus()
    {
        return features_enabled.show_mac_address;
    }


    public int getGatewayUserId()
    {
        return gateway_user_id;
    }


    private boolean isNumberOfDevicesConnectToPatientVitalsDisplay_ZERO()
    {
        return getDeviceTypesInUse().size() == 0;
    }


    private static boolean admin_exit_button_pressed = false;
    private static boolean bound_to_logcat_capture_service = false;

    public void adminModeExitPressed()
    {
        storeAuditTrailEvent(AuditTrailEvent.GATEWAY_EXITED_FROM_ADMIN_SCREEN, gateway_user_id);

        portal_system_commands.sendAdminModeExitButtonPressed_Event();

        checkAndCancel_timer(user_interface_timer);

        // Cancel the countdown timers
        stopScreenLockCountdownTimer();
        stopScreenDimmingCountdownTimer();

        admin_exit_button_pressed = true;

        broadcastExitApplicationIntent();

        Log.d(TAG, "adminModeExitPressed : Starting logger application");

        // Stop the logger service
        if(bound_to_logcat_capture_service)
        {
            boolean is_service_stopped = stopService(new Intent(getAppContext(), IsansysPortalLogcatCaptureService.class));

            if(is_service_stopped)
            {
                Log.d(TAG, "adminModeExitPressed : Portal Logcat service is stopped successfully");
            }
            else
            {
                Log.d(TAG, "adminModeExitPressed : ALERT ALERT ALERT!!!!   Portal Logcat service isn't stopped");
            }

            bound_to_logcat_capture_service = false;
        }
        else
        {
            Log.w(TAG, "Portal logger service isn't created before");
        }

        // Unregister the broadcast receiver
        try
        {
            Log.d(TAG, "onStop : unregisterReceiver : broadcastReceiverIncomingCommandsFromPatientGateway");
            // If for some reason this receiver hasn't been registered properly, it will throw an IllegalArgumentException
            unregisterReceiver(broadcastReceiverIncomingCommandsFromPatientGateway);

            Log.d(TAG, "onStop : unregisterReceiver : broadcastReceiverAndroidEvents");
            unregisterReceiver(broadcastReceiverAndroidEvents);
        }
        catch (IllegalArgumentException e)
        {
            Log.e(TAG, e.getMessage());
        }

        // Clear the UI time
        checkAndCancel_timer(user_interface_timer);

        // Cancel the countdown timers
        stopScreenLockCountdownTimer();
        stopScreenDimmingCountdownTimer();

        // Send intent to Action with FLAG_ACTIVITY_CLEAR_TOP
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 80);

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

        // Start Patient Gateway application
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.isansys.patientgateway");
        LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        LaunchIntent.putExtra("CloseApplicationTriggered", "close_app");
        LaunchIntent.putExtra("CloseApplicationStatus", true);
        Log.i(TAG, "UI Launching the Gateway to close it");
        startActivity(LaunchIntent);

        // Close UI application
        Log.d(TAG, "adminModeExitPressed : finishing UI app");
        finishAndRemoveTask();
    }


    private final static String ACTION_ADMIN_EXIT_APPLICATION_BUTTON_PRESSED = "com.isansys.patientgateway.ACTION_ADMIN_EXIT_APPLICATION_BUTTON_PRESSED.PatientGateway";
    private final static String EXIT_BUTTON_PRESSED = "com.isansys.patientgateway.EXIT_BUTTON_PRESSED.PatientGateway";

    private void broadcastExitApplicationIntent()
    {
        final Intent intent = new Intent(ACTION_ADMIN_EXIT_APPLICATION_BUTTON_PRESSED);

        intent.putExtra(EXIT_BUTTON_PRESSED, true);

        sendBroadcast(intent);
    }

    // this variable is used check to check if the main UI is in Logcat display mode,
    /*
     * if true UI main fragment is displaying Logs lines
     */
    public static volatile boolean logCatPageEnabled = false;

    public void showLogCat(boolean enable)
    {
        logCatPageEnabled = enable;

        portal_system_commands.reportLogCatEnableStatus(enable);

        if (enable)
        {
            showLogCatFragment();
        }
        else
        {
            adminModeSelected();
        }
    }


    private void showLogCatFragment()
    {
        current_page = UserInterfacePage.INVALID;

        enableNavigationButtons(true, true, false);

        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentLogCatWithInputSelection(), UserInterfacePage.LOGCAT_DISPLAY);
    }


    private static class MyHandler extends Handler
    {
        private final WeakReference<MainActivity> main_activity_weak_reference;

        MyHandler(MainActivity service)
        {
            main_activity_weak_reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg)
        {
            MainActivity service = main_activity_weak_reference.get();

            if (msg.what == IsansysPortalLogcatCaptureService.MSG_NEW_LINE)
            {
                service.addLineInFragmentLogCat((String) msg.obj);
            }
            else
            {
                super.handleMessage(msg);
            }
        }
    }


    private final MyHandler mHandler = new MyHandler(this);


    private void addLineInFragmentLogCat(String new_line)
    {
        if(current_page == UserInterfacePage.LOGCAT_DISPLAY && logCatPageEnabled)
        {
            FragmentLogCatDisplay fragmentLog = (FragmentLogCatDisplay) getSupportFragmentManager().findFragmentById(R.id.FrameLayoutLogCatDisplay);
            if(fragmentLog != null)
            {
                fragmentLog.putLogLineInFragment(new_line);
            }
            else
            {
                Log.e(TAG, "addLineInFragmentLogCat : FragmentLogCatDisplay fragmentLog == null !!!!!!!!!!!");
            }
        }
    }


    public boolean getDeviceDummyDataModeEnableStatus(SensorType sensor_type)
    {
        for(DeviceInfo device_info : cached_device_info_list)
        {
            if(device_info.sensor_type == sensor_type)
            {
                return device_info.dummy_data_mode;
            }
        }

        return false;
    }


    public boolean getDeviceDummyDataModeEnableStatus(DeviceType device_type)
    {
        for(DeviceInfo device_info : cached_device_info_list)
        {
            if(device_info.device_type == device_type)
            {
                return device_info.dummy_data_mode;
            }
        }

        return false;
    }


    public void setJsonArraySize(int size)
    {
        portal_system_commands.sendGatewayCommand_setJsonArraySize(size);
    }


    public void getJsonArraySize()
    {
        portal_system_commands.sendGatewayCommand_getJsonArraySize();
    }


    public void simulateDeviceConnectionEvent(SensorType sensor_type, boolean connected)
    {
        portal_system_commands.sendGatewayCommand_spoofDeviceConnectionState(sensor_type, connected);
    }


    public void setDummyDataModeNumberOfMeasurementsPerTick(int measurements_per_tick)
    {
        portal_system_commands.sendGatewayCommand_setNumberOfDummyDataModeMeasurementsPerTick(measurements_per_tick);
    }


    public void getDummyDataModeNumberOfMeasurementsPerTick()
    {
        portal_system_commands.sendGatewayCommand_getNumberOfDummyDataModeMeasurementsPerTick();
    }


    public boolean getDeviceCurrentlyConnectedByType(SensorType sensor_type)
    {
        return getDeviceByType(sensor_type).isActualDeviceConnectionStatusConnected();
    }


    public boolean getDeviceCurrentlyConnectedByType(DeviceType device_type)
    {
        return getDeviceByType(device_type).isActualDeviceConnectionStatusConnected();
    }


    /**
     * Function to disable the Screen Lock timer, footer back button
     * End Session button pressed in FragmentEndSession. But Lifetouch HeartBeat are still pending. So disabling the footer back button and screen lock timer.
     */
    public void endSessionPressed_DisableFooterAndUserInterfaceTimeout()
    {
        FragmentFooter fragment = (FragmentFooter) getSupportFragmentManager().findFragmentById(R.id.fragment_footer);

        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            // Hide footer back button so that user cant navigate back to session
            fragment.setBackButtonVisible(false);

            // Hide footer lock button so that user cant navigate back to session
            fragment.setLockButtonVisible(false);
        }

        // Stop the user interface timer so that all the pending heartbeats are downloaded in the FragmentEndSession
        stopScreenLockCountdownTimer();
    }

    /**
     * Function to re-enable the footer's back button and lock button. User interface lock screen Timeout is started again
     */
    public void enableFooterBackButtonAndUserInterfaceTimeout()
    {
        FragmentFooter fragment = (FragmentFooter) getSupportFragmentManager().findFragmentById(R.id.fragment_footer);

        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            // Hide footer back button so that user cant navigate back to session
            fragment.setBackButtonVisible(true);

            // Hide footer lock button so that user cant navigate back to session
            fragment.setLockButtonVisible(true);
        }

        stopScreenLockCountdownTimerAndRestartIfDesired();
    }


    private void getMostRecentDataFromPatientGatewayDatabase(long number_of_minutes_of_data_to_get)
    {
        long timestamp_now_in_milliseconds = getNtpTimeNowInMilliseconds();
        long starting_timestamp = timestamp_now_in_milliseconds - (number_of_minutes_of_data_to_get * (int)DateUtils.MINUTE_IN_MILLIS);

        String sort_order = "_id DESC";

        String[] hb_projection = {
                PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_BEAT__AMPLITUDE,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_BEAT__SEQUENCE_ID,
                PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_BEAT__ACTIVITY_LEVEL,
                PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_BEAT__RR_INTERVAL,
        };

        String[] hr_projection = {
                PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_RATE__HEART_RATE,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP
        };

        String[] rr_projection = {
                PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_RESPIRATION_RATE__RESPIRATION_RATE,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP
        };

        String[] temp_projection = {
                PatientGatewayDatabaseContract.COLUMN_LIFETEMP__TEMPERATURE,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP
        };

        String[] spo2_projection = {
                PatientGatewayDatabaseContract.COLUMN_OXIMETER__SPO2,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP
        };

        String[] bp_projection = {
                PatientGatewayDatabaseContract.COLUMN_BLOOD_PRESSURE__SYSTOLIC,
                PatientGatewayDatabaseContract.COLUMN_BLOOD_PRESSURE__DIASTOLIC,
                PatientGatewayDatabaseContract.COLUMN_BLOOD_PRESSURE__PULSE,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] weight_scale_projection = {
                PatientGatewayDatabaseContract.COLUMN_WEIGHT__WEIGHT,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        // Manually Entered Vitals
        String[] manually_entered_hr_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_HEART_RATE__HEART_RATE,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_rr_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_RESPIRATION_RATE__RESPIRATION_RATE,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_temp_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_TEMPERATURE__TEMPERATURE,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_spo2_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_SPO2__SPO2,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_bp_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_BLOOD_PRESSURE__SYSTOLIC,
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_BLOOD_PRESSURE__DIASTOLIC,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_weight_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_WEIGHT__WEIGHT,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_consciousness_level_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL__CONSCIOUSNESS_LEVEL,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_supplemental_oxygen_level_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN__SUPPLEMENTAL_OXYGEN_LEVEL,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_annotation_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_ANNOTATION__ANNOTATION_TEXT,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP
        };

        String[] manually_entered_capillary_refill_time_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME__CAPILLARY_REFILL_TIME,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_respiration_distress_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_RESPIRATION_DISTRESS__RESPIRATION_DISTRESS,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_nurse_or_family_concern_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_NURSE_OR_FAMILY_CONCERN,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] manually_entered_urine_output_projection = {
                PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_URINE_OUTPUT,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP,
                PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS
        };

        String[] early_warning_score_projection = {
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE__EARLY_WARNING_SCORE,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE__MAX_POSSIBLE,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE__IS_SPECIAL_ALERT,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE__TREND_DIRECTION,
                PatientGatewayDatabaseContract.COLUMN_TIMESTAMP
        };

        String[] setup_mode_logs_projection = {
                PatientGatewayDatabaseContract.COLUMN_SETUP_MODE_LOGS_SENSOR_TYPE,
                PatientGatewayDatabaseContract.COLUMN_SETUP_MODE_LOGS_DEVICE_TYPE,
                PatientGatewayDatabaseContract.COLUMN_SETUP_MODE_LOGS_START_SETUP_MODE_TIME,
                PatientGatewayDatabaseContract.COLUMN_SETUP_MODE_LOGS_END_SETUP_MODE_TIME
        };

        String query_selection_argument;
        Uri query_uri;

        for(DeviceSession device_session : device_sessions)
        {
            Log.d(TAG, "getMostRecentDataFromPatientGatewayDatabase : Setting up query for sensor type : " + device_session.sensor_type + " : " + device_session.device_type + " : Device Session ID = " + device_session.local_device_session_id);

            switch(device_session.sensor_type)
            {
                case SENSOR_TYPE__LIFETOUCH:
                {
                    query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id;
                    query_uri = PatientGatewayDatabaseContract.CONTENT_URI_LIFETOUCH_HEART_BEATS;
                    vital_signs_async_query.startQuery(QueryType.LIFETOUCH_HEART_BEATS.ordinal(), handler_vitals_data, query_uri, hb_projection, query_selection_argument, null, sort_order);

                    query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id + " AND " + PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " >= '" + starting_timestamp + "'";
                    query_uri = PatientGatewayDatabaseContract.CONTENT_URI_LIFETOUCH_HEART_RATES;
                    vital_signs_async_query.startQuery(QueryType.LIFETOUCH_HEART_RATES.ordinal(), handler_vitals_data, query_uri, hr_projection, query_selection_argument, null, sort_order);

                    query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id + " AND " + PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " >= '" + starting_timestamp + "'";
                    query_uri = PatientGatewayDatabaseContract.CONTENT_URI_LIFETOUCH_RESPIRATION_RATES;
                    vital_signs_async_query.startQuery(QueryType.LIFETOUCH_RESPIRATION_RATES.ordinal(), handler_vitals_data, query_uri, rr_projection, query_selection_argument, null, sort_order);

                    query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id;
                    query_uri = PatientGatewayDatabaseContract.CONTENT_URI_SETUP_MODE_LOGS;
                    vital_signs_async_query.startQuery(QueryType.LIFETOUCH_SETUP_MODE_LOGS.ordinal(), handler_vitals_data, query_uri, setup_mode_logs_projection, query_selection_argument, null, sort_order);
                }
                break;

                case SENSOR_TYPE__TEMPERATURE:
                {
                    query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id + " AND " + PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " >= '" + starting_timestamp + "'";
                    query_uri = PatientGatewayDatabaseContract.CONTENT_URI_TEMPERATURE_MEASUREMENTS;
                    vital_signs_async_query.startQuery(QueryType.LIFETEMP_TEMPERATURE_MEASUREMENTS.ordinal(), handler_vitals_data, query_uri, temp_projection, query_selection_argument, null, sort_order);
                }
                break;

                case SENSOR_TYPE__SPO2:
                {
                    query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id + " AND " + PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " >= '" + starting_timestamp + "'";
                    query_uri = PatientGatewayDatabaseContract.CONTENT_URI_OXIMETER_MEASUREMENTS;
                    vital_signs_async_query.startQuery(QueryType.PULSE_OX_MEASUREMENTS.ordinal(), handler_vitals_data, query_uri, spo2_projection, query_selection_argument, null, sort_order);

                    query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id;
                    query_uri = PatientGatewayDatabaseContract.CONTENT_URI_SETUP_MODE_LOGS;
                    vital_signs_async_query.startQuery(QueryType.NONIN_SETUP_MODE_LOGS.ordinal(), handler_vitals_data, query_uri, setup_mode_logs_projection, query_selection_argument, null, sort_order);
                }
                break;

                case SENSOR_TYPE__BLOOD_PRESSURE:
                {
                    query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id + " AND " + PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " >= '" + starting_timestamp + "'";
                    query_uri = PatientGatewayDatabaseContract.CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS;
                    vital_signs_async_query.startQuery(QueryType.BLOOD_PRESSURE_MEASUREMENTS.ordinal(), handler_vitals_data, query_uri, bp_projection, query_selection_argument, null, sort_order);
                }
                break;

                case SENSOR_TYPE__WEIGHT_SCALE:
                {
                    query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id + " AND " + PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " >= '" + starting_timestamp + "'";
                    query_uri = PatientGatewayDatabaseContract.CONTENT_URI_WEIGHT_SCALE_MEASUREMENTS;
                    vital_signs_async_query.startQuery(QueryType.WEIGHT_SCALE_MEASUREMENTS.ordinal(), handler_vitals_data, query_uri, weight_scale_projection, query_selection_argument, null, sort_order);
                }
                break;

                case SENSOR_TYPE__ALGORITHM:
                {
                    switch (device_session.device_type)
                    {
                        case DEVICE_TYPE__EARLY_WARNING_SCORE:
                        {
                            query_selection_argument = PatientGatewayDatabaseContract.COLUMN_DEVICE_SESSION_NUMBER + "=" + device_session.local_device_session_id + " AND " + PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " >= '" + starting_timestamp + "'";
                            query_uri = PatientGatewayDatabaseContract.CONTENT_URI_EARLY_WARNING_SCORES;
                            vital_signs_async_query.startQuery(QueryType.EARLY_WARNING_SCORES.ordinal(), handler_vitals_data, query_uri, early_warning_score_projection, query_selection_argument, null, sort_order);
                        }
                        break;
                    }
                }
                break;
            }
        }


        // Manual Vital Signs
        query_selection_argument = PatientGatewayDatabaseContract.COLUMN_PATIENT_SESSION_NUMBER + "=" + patient_session_number + " AND " + PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " >= '" + starting_timestamp + "'";

        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_HEART_RATES.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_HEART_RATES, manually_entered_hr_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_RESPIRATION_RATES.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES, manually_entered_rr_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_TEMPERATURES.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE, manually_entered_temp_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_SPO2_MEASUREMENTS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_SPO2, manually_entered_spo2_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE, manually_entered_bp_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_WEIGHT, manually_entered_weight_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, manually_entered_consciousness_level_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, manually_entered_supplemental_oxygen_level_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_ANNOTATIONS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS, manually_entered_annotation_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, manually_entered_capillary_refill_time_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_RESPIRATION_DISTRESS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_DISTRESS, manually_entered_respiration_distress_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_NURSE_OR_FAMILY_CONCERN, manually_entered_nurse_or_family_concern_projection, query_selection_argument, null, sort_order);
        vital_signs_async_query.startQuery(QueryType.MANUALLY_ENTERED_URINE_OUTPUT.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_MANUALLY_ENTERED_URINE_OUTPUT, manually_entered_urine_output_projection, query_selection_argument, null, sort_order);
    }


    public ArrayList<ThresholdSet> getEarlyWarningScoreThresholdSets()
    {
        return default_early_warning_score_threshold_sets;
    }


    private void updateAdminModeEarlyWarningScoreThresholdSets()
    {
        if (current_page == UserInterfacePage.ADMIN_MODE)
        {
            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.updateEarlyWarningScoreTypeList(default_early_warning_score_threshold_sets);
            }
        }

        showForceInstallationCompleteButtonOnAdminScreen();
    }


    private void showForceInstallationCompleteButtonOnAdminScreen()
    {
        if (current_page == UserInterfacePage.ADMIN_MODE)
        {
            FragmentAdminMode fragment = (FragmentAdminMode) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                boolean thresholds_received = default_early_warning_score_threshold_sets.size() > 0;
                boolean bed_id_set;
                boolean got_server_configurable_text = !serverConfigurableTextStrings.isEmpty();

                try
                {
                    Integer.parseInt(patient_gateways_assigned_bed_id);

                    bed_id_set = true;
                }
                catch (NumberFormatException e)
                {
                    bed_id_set = false;
                }

                Log.d(TAG, "showForceInstallationCompleteButtonOnAdminScreen : isGatewaySetupComplete() = " + isGatewaySetupComplete());
                Log.d(TAG, "showForceInstallationCompleteButtonOnAdminScreen : bed_id_set = " + bed_id_set);
                Log.d(TAG, "showForceInstallationCompleteButtonOnAdminScreen : thresholds_received = " + thresholds_received);
                Log.d(TAG, "showForceInstallationCompleteButtonOnAdminScreen : got_server_configurable_text = " + got_server_configurable_text);

                if ((isGatewaySetupComplete() == false) && bed_id_set && thresholds_received && got_server_configurable_text)
                {
                    fragment.showForceInstallationCompleteButton(true);
                }
                else
                {
                    fragment.showForceInstallationCompleteButton(false);
                }
            }
        }
    }


    /**
     * Trying to load Threshold Sets, Threshold Set Age Block Details and Threshold Set Levels.
     *
     * Can't make all three queries in one go as need the result of the first before we can use the data of the second.
     * So do the query for Threshold Sets here. Once that query completes, do the query for Threshold Set Age Block Details.
     * And when that query finishes, do the Threshold Set Levels query
     */
    private void getEarlyWarningScoringSetsFromPatientGatewayDatabase()
    {
        // Clear the cached thresholds
        default_early_warning_score_threshold_sets.clear();
        updateAdminModeEarlyWarningScoreThresholdSets();

        String[] projection = {
                PatientGatewayDatabaseContract.COLUMN_ID,
                PatientGatewayDatabaseContract.COLUMN_SERVERS_ID,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SETS__NAME,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SETS__IS_DEFAULT
        };

        vital_signs_async_query.startQuery(QueryType.EARLY_WARNING_SCORE_THRESHOLD_SETS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SETS, projection, null, null, null);
    }


    private void queryThresholdSetAgeBlockDetails()
    {
        String[] projection = {
                PatientGatewayDatabaseContract.COLUMN_ID,
                PatientGatewayDatabaseContract.COLUMN_SERVERS_ID,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__THRESHOLD_SET_ID,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__AGE_BOTTOM,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__AGE_TOP,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__DISPLAY_NAME,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__IMAGE_BINARY,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__IS_ADULT,
        };

        vital_signs_async_query.startQuery(QueryType.EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS, projection, null, null, null);
    }


    private void queryThresholdSetLevels()
    {
        String[] projection = {
                PatientGatewayDatabaseContract.COLUMN_ID,
                PatientGatewayDatabaseContract.COLUMN_SERVERS_ID,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__THRESHOLD_SET_AGE_BLOCK_DETAIL_ID,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__RANGE_BOTTOM,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__RANGE_TOP,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__EARLY_WARNING_SCORE,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__MEASUREMENT_TYPE,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__MEASUREMENT_TYPE_AS_STRING,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__DISPLAY_TEXT,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__INFORMATION_TEXT,
        };

        vital_signs_async_query.startQuery(QueryType.EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS, projection, null, null, null);
    }


    private void queryThresholdSetColours()
    {
        String[] projection = {
                PatientGatewayDatabaseContract.COLUMN_ID,
                PatientGatewayDatabaseContract.COLUMN_SERVERS_ID,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__THRESHOLD_SET_AGE_BLOCK_DETAIL_ID,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__SCORE,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__COLOUR,
                PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__TEXT_COLOUR,
        };

        vital_signs_async_query.startQuery(QueryType.EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS, projection, null, null, null);
    }


    private void queryServerConfigurableText()
    {
        Log.d(TAG, "queryServerConfigurableText start");
        String[] projection = {
                PatientGatewayDatabaseContract.COLUMN_ID,
                PatientGatewayDatabaseContract.COLUMN_SERVERS_ID,
                PatientGatewayDatabaseContract.COLUMN_SERVER_CONFIGURABLE_TEXT__STRING,
                PatientGatewayDatabaseContract.COLUMN_SERVER_CONFIGURABLE_TEXT__STRING_TYPE
        };

        vital_signs_async_query.startQuery(QueryType.SERVER_CONFIGURABLE_TEXT.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_SERVER_CONFIGURABLE_TEXT, projection, null, null, null);
    }


    private void queryWards()
    {
        String[] projection = {
                PatientGatewayDatabaseContract.COLUMN_ID,
                PatientGatewayDatabaseContract.COLUMN_SERVERS_ID,
                PatientGatewayDatabaseContract.COLUMN_WARDS__WARD_NAME,
                PatientGatewayDatabaseContract.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP
        };

        vital_signs_async_query.startQuery(QueryType.WARDS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_WARDS, projection, null, null, null);
    }


    private void queryBeds()
    {
        String[] projection = {
                PatientGatewayDatabaseContract.COLUMN_ID,
                PatientGatewayDatabaseContract.COLUMN_SERVERS_ID,
                PatientGatewayDatabaseContract.COLUMN_BEDS__BED_NAME,
                PatientGatewayDatabaseContract.COLUMN_BEDS__BY_WARD_ID,
                PatientGatewayDatabaseContract.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP
        };

        vital_signs_async_query.startQuery(QueryType.BEDS.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_BEDS, projection, null, null, null);
    }

    private void queryWebPages()
    {
        String[] projection = {
                PatientGatewayDatabaseContract.COLUMN_ID,
                PatientGatewayDatabaseContract.COLUMN_WEB_PAGE_DETAILS__URL,
                PatientGatewayDatabaseContract.COLUMN_WEB_PAGE_DETAILS__DESCRIPTION,
        };

        vital_signs_async_query.startQuery(QueryType.WEBPAGES.ordinal(), handler_vitals_data, PatientGatewayDatabaseContract.CONTENT_URI_WEB_PAGE_DETAILS, projection, null, null, null);
    }


    public void getSetupModeLengthInSeconds()
    {
        portal_system_commands.sendGatewayCommand_getSetupModeTimeInSeconds();
    }

    public void setSetupModeLengthInSeconds(int setup_mode_time_in_seconds)
    {
        portal_system_commands.sendGatewayCommand_setSetupModeTimeInSeconds(setup_mode_time_in_seconds);
    }


    public void getDevicePeriodicModePeriodTimeInSeconds()
    {
        portal_system_commands.sendGatewayCommand_getDevicePeriodicModePeriodTimeInSeconds();
    }

    public void setDevicePeriodicModePeriodTimeInSeconds(int time_in_seconds)
    {
        portal_system_commands.sendGatewayCommand_setDevicePeriodicModePeriodTimeInSeconds(time_in_seconds);
    }


    public void getDevicePeriodicModeActiveTimeInSeconds()
    {
        portal_system_commands.sendGatewayCommand_getDevicePeriodicModeActiveTimeInSeconds();
    }

    public void setDevicePeriodicModeActiveTimeInSeconds(int time_in_seconds)
    {
        portal_system_commands.sendGatewayCommand_setDevicePeriodicModeActiveTimeInSeconds(time_in_seconds);
    }


    public void getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid()
    {
        portal_system_commands.sendGatewayCommand_getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid();
    }


    public void setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(int percentage)
    {
        portal_system_commands.sendGatewayCommand_setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(percentage);
    }


    public void getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid()
    {
        portal_system_commands.sendGatewayCommand_getMaxNumberNoninPulseOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid();
    }


    public void setMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(int number)
    {
        portal_system_commands.sendGatewayCommand_setMaxNumberNoninPulseOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(number);
    }


    public void getDisplayTimeoutLengthInSeconds()
    {
        portal_system_commands.sendGatewayCommand_getDisplayTimeoutInSeconds();
    }

    public void getDisplayTimeoutAppliesToPatientVitalsDisplay()
    {
        portal_system_commands.sendGatewayCommand_getDisplayTimeoutAppliesToPatientVitalsDisplay();
    }

    public void setDisplayTimeoutLengthInSeconds(int time_in_seconds)
    {
        display_timeout_length_in_seconds = time_in_seconds;

        portal_system_commands.sendGatewayCommand_setDisplayTimeoutInSeconds(time_in_seconds);
    }

    public void setDisplayTimeoutAppliesToPatientVitalsDisplay(boolean applies)
    {
        display_timeout_applies_to_patient_vitals = applies;

        portal_system_commands.sendGatewayCommand_setDisplayTimeoutAppliesToPatientVitalsDisplay(applies);
    }

    private static class VitalsDataHandler extends Handler
    {
        private final WeakReference<MainActivity> main_activity_weak_reference;

        VitalsDataHandler(MainActivity service)
        {
            main_activity_weak_reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg)
        {
            MainActivity service = main_activity_weak_reference.get();

            service.handleVitalsData(msg);
        }
    }


    private void processCursorInBackground(QueryType query_type, Cursor cursor)
    {
        Executors.newSingleThreadExecutor().execute(() -> {

            // Long running operation
            processCursor(query_type, cursor);

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(this::hideProgress);
        });
    }


    private final VitalsDataHandler handler_vitals_data = new VitalsDataHandler(this);


    private void handleVitalsData(Message msg)
    {
        QueryType query_type = QueryType.values()[msg.arg1];
        Cursor cursor = (Cursor)msg.obj;

        if(cursor != null)
        {
            processCursorInBackground(query_type, cursor);
        }
        else
        {
            Log.e(TAG, "Cursor == null");
        }
    }


    private void processCursor(QueryType query_type, Cursor cursor)
    {
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0)
        {
            Log.e(TAG, "Cursor received for " + query_type + " but empty");

            switch(query_type)
            {
                case WEBPAGES:
                {
                    Log.d(TAG, "Clearing cached_webpages as no websites for user to view");

                    // No webpages that the user can vist
                    cached_webpages.clear();
                }
                break;
            }
        }
        else
        {
            Log.d(TAG, "Cursor received for " + query_type + " : Length = " + cursor.getCount());

            long start_query_time = System.nanoTime();

            cursor.moveToLast();

            // Process the cursor
            switch(query_type)
            {
                case LIFETOUCH_HEART_RATES:
                {
                    VitalSignType vital_sign_type = VitalSignType.HEART_RATE;

                    ArrayList<MeasurementHeartRate> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int heart_rate = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_RATE__HEART_RATE));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        measurements.add(new MeasurementHeartRate(heart_rate, timestamp_in_ms));

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case LIFETOUCH_RESPIRATION_RATES:
                {
                    VitalSignType vital_sign_type = VitalSignType.RESPIRATION_RATE;

                    ArrayList<MeasurementRespirationRate> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int respiration_rate = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_RESPIRATION_RATE__RESPIRATION_RATE));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        measurements.add(new MeasurementRespirationRate(respiration_rate, timestamp_in_ms));

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case LIFETEMP_TEMPERATURE_MEASUREMENTS:
                {
                    VitalSignType vital_sign_type = VitalSignType.TEMPERATURE;

                    ArrayList<MeasurementTemperature> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        double temperature = cursor.getDouble(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_LIFETEMP__TEMPERATURE));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        MeasurementTemperature measurement = new MeasurementTemperature(temperature, timestamp_in_ms);

                        // Convert the temperature to Fahrenheit if needed
                        if (isShowTemperatureInFahrenheitEnabled())
                        {
                            measurement.temperature = convertTemperatureToDegreesF(measurement.getPrimaryMeasurement());
                        }

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case PULSE_OX_MEASUREMENTS:
                {
                    VitalSignType vital_sign_type = VitalSignType.SPO2;

                    ArrayList<MeasurementSpO2> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int spo2 = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_OXIMETER__SPO2));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        spo2 = checkForSpO2InvalidReading(spo2);

                        measurements.add(new MeasurementSpO2(spo2, timestamp_in_ms));

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case BLOOD_PRESSURE_MEASUREMENTS:
                {
                    VitalSignType vital_sign_type = VitalSignType.BLOOD_PRESSURE;

                    ArrayList<MeasurementBloodPressure> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int systolic = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_BLOOD_PRESSURE__SYSTOLIC));
                        int diastolic = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_BLOOD_PRESSURE__DIASTOLIC));
                        int pulse = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_BLOOD_PRESSURE__PULSE));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementBloodPressure measurement = new MeasurementBloodPressure(systolic, diastolic, pulse, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case WEIGHT_SCALE_MEASUREMENTS:
                {
                    VitalSignType vital_sign_type = VitalSignType.WEIGHT;

                    ArrayList<MeasurementWeight> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        double weight = cursor.getDouble(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_WEIGHT__WEIGHT));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementWeight measurement = new MeasurementWeight(weight, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        if (isShowWeightInLbsEnabled())
                        {
                            measurement.weight = convertWeightToLbs(measurement.getPrimaryMeasurement());
                        }

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type);
                }
                break;

                case LIFETOUCH_HEART_BEATS:
                {
                    while (!cursor.isBeforeFirst())
                    {
                        int heart_beat_tag = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_BEAT__SEQUENCE_ID));

                        int heart_beat_amplitude = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_BEAT__AMPLITUDE));

                        long heart_beat_timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        String heart_beat__activity_level_as_string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_BEAT__ACTIVITY_LEVEL));
                        HeartBeatInfo.ActivityLevel heart_beat_activity = HeartBeatInfo.ActivityLevel.fromInt(Integer.parseInt(heart_beat__activity_level_as_string));

                        int heart_beat_rr_interval = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_HEART_BEAT__RR_INTERVAL));

                        HeartBeatInfo heart_beat = new HeartBeatInfo(heart_beat_tag, heart_beat_amplitude, heart_beat_timestamp_in_ms, heart_beat_activity, heart_beat_rr_interval, getNtpTimeNowInMilliseconds());

                        heart_beat_cache.add(heart_beat);

                        cursor.moveToPrevious();
                    }
                }
                break;

                case MANUALLY_ENTERED_HEART_RATES:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_HEART_RATE;

                    ArrayList<MeasurementManuallyEnteredHeartRate> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int heart_rate = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_HEART_RATE__HEART_RATE));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                            int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                            MeasurementManuallyEnteredHeartRate measurement = new MeasurementManuallyEnteredHeartRate(heart_rate, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                            measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.

                    showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE, SensorType.SENSOR_TYPE__LIFETOUCH);
                }
                break;

                case MANUALLY_ENTERED_RESPIRATION_RATES:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE;

                    ArrayList<MeasurementManuallyEnteredRespirationRate> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int respiration_rate = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_RESPIRATION_RATE__RESPIRATION_RATE));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementManuallyEnteredRespirationRate measurement = new MeasurementManuallyEnteredRespirationRate(respiration_rate, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.

                    showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE, SensorType.SENSOR_TYPE__LIFETOUCH);
                }
                break;

                case MANUALLY_ENTERED_TEMPERATURES:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_TEMPERATURE;

                    ArrayList<MeasurementManuallyEnteredTemperature> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        double temperature = cursor.getDouble(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_TEMPERATURE__TEMPERATURE));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementManuallyEnteredTemperature measurement = new MeasurementManuallyEnteredTemperature(temperature, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        // Convert the temperature to Fahrenheit if needed
                        if (isShowTemperatureInFahrenheitEnabled())
                        {
                            measurement.temperature = convertTemperatureToDegreesF(measurement.getPrimaryMeasurement());
                        }

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.

                    showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE, SensorType.SENSOR_TYPE__TEMPERATURE);
                }
                break;

                case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_SPO2;

                    ArrayList<MeasurementManuallyEnteredSpO2> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int spo2 = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_SPO2__SPO2));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementManuallyEnteredSpO2 measurement = new MeasurementManuallyEnteredSpO2(spo2, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.

                    showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SPO2, SensorType.SENSOR_TYPE__SPO2);
                }
                break;

                case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE;

                    ArrayList<MeasurementManuallyEnteredBloodPressure> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int systolic = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_BLOOD_PRESSURE__SYSTOLIC));
                        int diastolic = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_BLOOD_PRESSURE__DIASTOLIC));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementManuallyEnteredBloodPressure measurement = new MeasurementManuallyEnteredBloodPressure(systolic, diastolic, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.

                    showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE, SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
                }
                break;

                case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_WEIGHT;

                    ArrayList<MeasurementManuallyEnteredWeight> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        double weight = cursor.getDouble(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_WEIGHT__WEIGHT));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementManuallyEnteredWeight measurement = new MeasurementManuallyEnteredWeight(weight, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        if (isShowWeightInLbsEnabled())
                        {
                            measurement.weight = convertWeightToLbs(measurement.getPrimaryMeasurement());
                        }

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type);

                    showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT, SensorType.SENSOR_TYPE__WEIGHT_SCALE);
                }
                break;

                case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL;

                    ArrayList<MeasurementConsciousnessLevel> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        String value_as_string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL__CONSCIOUSNESS_LEVEL));
                        int value = Integer.parseInt(value_as_string);

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementConsciousnessLevel measurement = new MeasurementConsciousnessLevel(value, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN;

                    ArrayList<MeasurementSupplementalOxygenLevel> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        String value_as_string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN__SUPPLEMENTAL_OXYGEN_LEVEL));
                        int value = Integer.parseInt(value_as_string);

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementSupplementalOxygenLevel measurement = new MeasurementSupplementalOxygenLevel(value, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case MANUALLY_ENTERED_ANNOTATIONS:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_ANNOTATION;

                    ArrayList<MeasurementAnnotation> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        String annotation_text = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_ANNOTATION__ANNOTATION_TEXT));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        MeasurementAnnotation measurement = new MeasurementAnnotation(annotation_text, timestamp_in_ms);

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME;

                    ArrayList<MeasurementCapillaryRefillTime> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        String value_as_string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME__CAPILLARY_REFILL_TIME));
                        int value = Integer.parseInt(value_as_string);

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementCapillaryRefillTime measurement = new MeasurementCapillaryRefillTime(value, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS;

                    ArrayList<MeasurementRespirationDistress> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        String value_as_string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_RESPIRATION_DISTRESS__RESPIRATION_DISTRESS));
                        int value = Integer.parseInt(value_as_string);

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementRespirationDistress measurement = new MeasurementRespirationDistress(value, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN;

                    ArrayList<MeasurementFamilyOrNurseConcern> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        String value_as_string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_NURSE_OR_FAMILY_CONCERN));
                        int concern = Integer.parseInt(value_as_string);

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementFamilyOrNurseConcern measurement = new MeasurementFamilyOrNurseConcern(concern, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case MANUALLY_ENTERED_URINE_OUTPUT:
                {
                    VitalSignType vital_sign_type = VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT;

                    ArrayList<MeasurementUrineOutput> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int urine_output = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MANUALLY_ENTERED_URINE_OUTPUT));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        int measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));

                        MeasurementUrineOutput measurement = new MeasurementUrineOutput(urine_output, timestamp_in_ms, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case EARLY_WARNING_SCORES:
                {
                    VitalSignType vital_sign_type = VitalSignType.EARLY_WARNING_SCORE;

                    ArrayList<MeasurementEarlyWarningScore> measurements = new ArrayList<>();

                    while (!cursor.isBeforeFirst())
                    {
                        int value = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE__EARLY_WARNING_SCORE));

                        int max_possible = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE__MAX_POSSIBLE));

                        String is_special_alert_as_string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE__IS_SPECIAL_ALERT));
                        boolean alert_level = Boolean.parseBoolean(is_special_alert_as_string);

                        int trend_direction = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE__TREND_DIRECTION));

                        long timestamp_in_ms = getTimestampInMilliseconds(cursor);

                        MeasurementEarlyWarningScore measurement = new MeasurementEarlyWarningScore(value, max_possible, alert_level, trend_direction, timestamp_in_ms, getNtpTimeNowInMilliseconds());

                        measurements.add(measurement);

                        cursor.moveToPrevious();
                    }

                    handleVitalsDataCommon(measurements, vital_sign_type); // cache and sort data, then update on the UI thread.
                }
                break;

                case EARLY_WARNING_SCORE_THRESHOLD_SETS:
                {
                    default_early_warning_score_threshold_sets.clear();

                    Log.e(TAG, "EARLY_WARNING_SCORE_THRESHOLD_SETS size = " + cursor.getCount());

                    cursor.moveToFirst();

                    // Parse the records
                    while (!cursor.isAfterLast())
                    {
                        ThresholdSet this_threshold_set = new ThresholdSet();
                        this_threshold_set.local_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_ID));
                        this_threshold_set.servers_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SERVERS_ID));
                        this_threshold_set.name = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SETS__NAME));

                        String is_default_as_string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SETS__IS_DEFAULT));
                        this_threshold_set.is_default = is_default_as_string.equals("1");

                        default_early_warning_score_threshold_sets.add(this_threshold_set);

                        cursor.moveToNext();
                    }

                    // Query the next level of data
                    queryThresholdSetAgeBlockDetails();

                    runOnUiThread(this::updateAdminModeEarlyWarningScoreThresholdSets);
                }
                break;

                case EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS:
                {
                    Log.e(TAG, "EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS size = " + cursor.getCount());

                    cursor.moveToFirst();

                    // Parse the records
                    while (!cursor.isAfterLast())
                    {
                        int threshold_set_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__THRESHOLD_SET_ID));

                        ThresholdSetAgeBlockDetail this_threshold_set_age_block_detail = new ThresholdSetAgeBlockDetail();
                        this_threshold_set_age_block_detail.local_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_ID));
                        this_threshold_set_age_block_detail.servers_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SERVERS_ID));
                        this_threshold_set_age_block_detail.age_range_bottom = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__AGE_BOTTOM));
                        this_threshold_set_age_block_detail.age_range_top = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__AGE_TOP));
                        this_threshold_set_age_block_detail.display_name = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__DISPLAY_NAME));
                        this_threshold_set_age_block_detail.image_binary = cursor.getBlob(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__IMAGE_BINARY));
                        this_threshold_set_age_block_detail.is_adult = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS__IS_ADULT)) > 0;

                        for (ThresholdSet threshold_set : default_early_warning_score_threshold_sets)
                        {
                            if (threshold_set.local_database_row_id == threshold_set_id)
                            {
                                threshold_set.list_threshold_set_age_block_detail.add(this_threshold_set_age_block_detail);
                            }
                        }

                        cursor.moveToNext();
                    }

                    // Query the next level of data
                    queryThresholdSetLevels();
                }
                break;

                case EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS:
                {
                    Log.e(TAG, "EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS size = " + cursor.getCount());

                    cursor.moveToFirst();

                    // Parse the records
                    while (!cursor.isAfterLast())
                    {
                        int threshold_set_age_block_detail_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__THRESHOLD_SET_AGE_BLOCK_DETAIL_ID));

                        ThresholdSetLevel this_threshold_set_level = new ThresholdSetLevel();
                        this_threshold_set_level.local_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_ID));
                        this_threshold_set_level.servers_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SERVERS_ID));
                        this_threshold_set_level.top = cursor.getFloat(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__RANGE_TOP));
                        this_threshold_set_level.bottom = cursor.getFloat(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__RANGE_BOTTOM));
                        this_threshold_set_level.early_warning_score = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__EARLY_WARNING_SCORE));
                        this_threshold_set_level.measurement_type = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__MEASUREMENT_TYPE));
                        this_threshold_set_level.measurement_type_string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__MEASUREMENT_TYPE_AS_STRING));
                        this_threshold_set_level.display_text = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__DISPLAY_TEXT));
                        this_threshold_set_level.information_text = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS__INFORMATION_TEXT));

                        // Convert the Temperature Thresholds to Fahrenheit if required
                        MeasurementTypes type = MeasurementTypes.values()[this_threshold_set_level.measurement_type];
                        if (type == MeasurementTypes.TEMPERATURE)
                        {
                            if (isShowTemperatureInFahrenheitEnabled())
                            {
                                float original_top = this_threshold_set_level.top;
                                float original_bottom = this_threshold_set_level.bottom;

                                this_threshold_set_level.top = (float)convertTemperatureToDegreesF(this_threshold_set_level.top);
                                this_threshold_set_level.bottom = (float)convertTemperatureToDegreesF(this_threshold_set_level.bottom);

                                Log.d(TAG, "Converted Temperature Threshold to Fahrenheit : Bottom " + original_bottom + "°C -> " + this_threshold_set_level.bottom + "°F : Top = " + original_top + "°C -> " + this_threshold_set_level.top + "°F");
                            }
                        }

                        // Convert the Weight Thresholds to Lbs if required
                        if (type == MeasurementTypes.WEIGHT)
                        {
                            if (isShowWeightInLbsEnabled())
                            {
                                float original_top = this_threshold_set_level.top;
                                float original_bottom = this_threshold_set_level.bottom;

                                this_threshold_set_level.top = (float) convertWeightToLbs(this_threshold_set_level.top);
                                this_threshold_set_level.bottom = (float) convertWeightToLbs(this_threshold_set_level.bottom);

                                Log.d(TAG, "Converted Weight Threshold to lbs : Bottom " + original_bottom + " Kg -> " + this_threshold_set_level.bottom + " lb : Top = " + original_top + " Kg -> " + this_threshold_set_level.top + " lb");
                            }
                        }

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

                    // Query the next level of data
                    queryThresholdSetColours();
                }
                break;

                case EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS:
                {
                    Log.e(TAG, "EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS size = " + cursor.getCount());

                    cursor.moveToFirst();

                    // Parse the records
                    while (!cursor.isAfterLast())
                    {
                        int threshold_set_age_block_detail_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__THRESHOLD_SET_AGE_BLOCK_DETAIL_ID));

                        ThresholdSetColour this_threshold_set_colour = new ThresholdSetColour();
                        this_threshold_set_colour.local_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_ID));
                        this_threshold_set_colour.servers_database_row_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SERVERS_ID));
                        this_threshold_set_colour.score = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__SCORE));
                        this_threshold_set_colour.colour = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__COLOUR));
                        this_threshold_set_colour.text_colour = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS__TEXT_COLOUR));

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

                    Log.e(TAG, "Early Warning Score Threshold Info read from local database");

                    ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = patient_info.getThresholdSetAgeBlockDetails();
                    if (thresholdSetAgeBlockDetail != null)
                    {
                        setupGraphColoursFromThresholds(thresholdSetAgeBlockDetail);
                    }

                    // Can ask the Gateway for the current Patients Thresholds now we have finished loading them in
                    portal_system_commands.sendGatewayCommand_getPatientThresholdSetCommand();

                    runOnUiThread(() -> {
                        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                        {
                            final FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                               fragment.setEarlyWarningScoreType(reportEarlyWarningScoreType());
                            }
                        }
                        else if (current_page == UserInterfacePage.MODE_SELECTION)
                        {
                            final FragmentModeSelection fragment = (FragmentModeSelection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

                            if ((is_session_in_progress == false) && UtilityFunctions.isFragmentAddedAndResumed(fragment))
                            {
                                fragment.setStartSessionClickable(true);
                            }
                        }
                    });
                }
                break;

                case LIFETOUCH_SETUP_MODE_RAW_SAMPLES:
                {
                    handleSetupModeData(device_type_for_setup_mode_query, cursor, PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_SETUP_MODE__SAMPLE_VALUE);
                }
                break;

                case PULSE_OX_SETUP_MODE_SAMPLES:
                {
                    handleSetupModeData(device_type_for_setup_mode_query, cursor, PatientGatewayDatabaseContract.COLUMN_OXIMETER_SETUP_MODE__SAMPLE_VALUE);
                }
                break;

                case LIFETOUCH_SETUP_MODE_LOGS:
                case NONIN_SETUP_MODE_LOGS:
                {
                    handleSetupModeLogs(cursor);
                }
                break;

                case SERVER_CONFIGURABLE_TEXT:
                {
                    serverConfigurableTextStrings.clear();

                    while (!cursor.isBeforeFirst())
                    {
                        ServerConfigurableText server_configurable_text = new ServerConfigurableText();
                        server_configurable_text.android_database_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_ID));
                        server_configurable_text.server_database_id = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SERVERS_ID));
                        server_configurable_text.string = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SERVER_CONFIGURABLE_TEXT__STRING));
                        server_configurable_text.string_type = ServerConfigurableTextStringTypes.values()[cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SERVER_CONFIGURABLE_TEXT__STRING_TYPE))];

                        switch (server_configurable_text.string_type)
                        {
                            case ANNOTATION_CONDITION:
                            {
                                AnnotationDescriptor annotation_descriptor = new AnnotationDescriptor(server_configurable_text.string, server_configurable_text.android_database_id, server_configurable_text.server_database_id);
                                serverConfigurableTextStrings.addAnnotationCondition(annotation_descriptor);
                            }
                            break;

                            case ANNOTATION_ACTION:
                            {
                                AnnotationDescriptor annotation_descriptor = new AnnotationDescriptor(server_configurable_text.string, server_configurable_text.android_database_id, server_configurable_text.server_database_id);
                                serverConfigurableTextStrings.addAnnotationAction(annotation_descriptor);
                            }
                            break;

                            case ANNOTATION_OUTCOME:
                            {
                                AnnotationDescriptor annotation_descriptor = new AnnotationDescriptor(server_configurable_text.string, server_configurable_text.android_database_id, server_configurable_text.server_database_id);
                                serverConfigurableTextStrings.addAnnotationOutcome(annotation_descriptor);
                            }
                            break;
                        }

                        cursor.moveToPrevious();
                    }

                    runOnUiThread(this::showForceInstallationCompleteButtonOnAdminScreen);
                }
                break;

                case WEBPAGES:
                {
                    Log.e(TAG, "WEBPAGES size = " + cursor.getCount());

                    ArrayList<WebPageDescriptor> webPageDescriptorArrayList = new ArrayList<>();

                    cursor.moveToFirst();

                    // Parse the records
                    while (!cursor.isAfterLast())
                    {
                        String url = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_WEB_PAGE_DETAILS__URL));
                        String description = cursor.getString(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_WEB_PAGE_DETAILS__DESCRIPTION));

                        Log.d(TAG, "WEBPAGES : Found URL = " + url + " : Description = " + description);

                        webPageDescriptorArrayList.add(new WebPageDescriptor(description, url));

                        cursor.moveToNext();
                    }

                    processWebpagesThatUserCanVisit(webPageDescriptorArrayList);
                }
                break;

                case WARDS:
                {
                    cached_wards.clear();
                    //ToDo RM: implement these
                }
                break;

                case BEDS:
                {
                    cached_beds.clear();
                    //ToDo RM: implement these
                }
                break;
            }

            long duration_in_ms = (System.nanoTime() - start_query_time) / 1000000;             // Divide by 1000000 to get milliseconds.

            Log.d(TAG, "Database Query : " + query_type + " took time " + duration_in_ms + " ms");
        }

        cursor.close();
    }


    private void handleSetupModeData(DeviceType device_type, Cursor cursor, String column_name)
    {
        Log.d(TAG, " : handleSetupModeData = " + device_type);

        final ArrayList<MeasurementSetupModeDataPoint> measurements = new ArrayList<>();

        cursor.moveToFirst();

        // Parse the records
        while (!cursor.isAfterLast())
        {
            int value = cursor.getInt(cursor.getColumnIndexOrThrow(column_name));
            long timestamp_in_ms = getTimestampInMilliseconds(cursor);

            measurements.add(new MeasurementSetupModeDataPoint(value, timestamp_in_ms));

            cursor.moveToNext();
        }

        sortSetupModeSamplesInAscendingTimestampOrder(measurements);

        int y_axis_max;

        // Hardcoding these for time being. Should use the ones setup in the Gateway (search "new SetupModeInfo") but the one from Device Info is ONLY ever the current device info and not
        // the device info from the previous device type
        // E.g. if the user had a Nonin BT and a Nonin BTLE in the session, then only the BTLE max graph size would be retrieved
        switch (device_type)
        {
            case DEVICE_TYPE__LIFETOUCH:
            case DEVICE_TYPE__LIFETOUCH_THREE:
            {
                y_axis_max = 4096;
            }
            break;

            case DEVICE_TYPE__MEDLINKET:
            {
                y_axis_max = 128;
            }
            break;

            case DEVICE_TYPE__NONIN_WRIST_OX:
            {
                y_axis_max = 256;
            }
            break;

            case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
            default:
            {
                y_axis_max = 65536;
            }
            break;
        }

        final int y_axis_max_final = y_axis_max;

        runOnUiThread(() -> {
            popup_historical_setup_mode_viewer.setYAxisMax(y_axis_max_final);
            popup_historical_setup_mode_viewer.bulkLoadGraphFromMeasurementArray(measurements);
        });
    }


    private void handleSetupModeLogs(Cursor cursor)
    {
        SensorType sensor_type = SensorType.SENSOR_TYPE__INVALID;

        ArrayList<SetupModeLog> setup_mode_log = new ArrayList<>();

        while (!cursor.isBeforeFirst())
        {
            int sensor_type_as_int = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SETUP_MODE_LOGS_SENSOR_TYPE));
            sensor_type = SensorType.values()[sensor_type_as_int];

            int device_type_as_int = cursor.getInt(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SETUP_MODE_LOGS_DEVICE_TYPE));
            DeviceType device_type = DeviceType.values()[device_type_as_int];

            long start_setup_mode_time = cursor.getLong(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SETUP_MODE_LOGS_START_SETUP_MODE_TIME));
            long end_setup_mode_time = cursor.getLong(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_SETUP_MODE_LOGS_END_SETUP_MODE_TIME));

            if(start_setup_mode_time != 0)
            {
                if (end_setup_mode_time != 0)
                {
                    setup_mode_log.add(new SetupModeLog(sensor_type, device_type, start_setup_mode_time, end_setup_mode_time));
                }
                else
                {
                    Log.e(TAG, sensor_type + " setup mode log has no end time!!!!");
                }
            }
            else
            {
                Log.e(TAG, sensor_type + " setup mode log has no start time!!!!");
            }

            cursor.moveToPrevious();
        }

        if (setup_mode_log.size() > 0)
        {
            // Remove duplicates and sort in time order
            setup_mode_log_cache.updateCachedVitalsListAndSortInTimeOrder(sensor_type, setup_mode_log);

            SensorType final_sensor_type = sensor_type;
            runOnUiThread(() -> {
                // This database read may have taken a "while" to execute. The Graphs onResume function will therefore have read the "not updated" cached_temperature_measurements list
                // and plotted "zero" measurements. So push these new measurements to the graph
                if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
                {
                    FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                    if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                    {
                        fragment.redrawGraphSetupModeIndicators(final_sensor_type);
                    }
                }
            });
        }
    }


    private long getTimestampInMilliseconds(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(PatientGatewayDatabaseContract.COLUMN_TIMESTAMP));
    }


    private void sortSetupModeSamplesInAscendingTimestampOrder(ArrayList<MeasurementSetupModeDataPoint> measurements)
    {
        measurements.sort((lhs, rhs) -> Long.compare(lhs.timestamp_in_ms, rhs.timestamp_in_ms));
    }


    private <T extends MeasurementVitalSign> void handleVitalsDataCommon(ArrayList<T> measurements, VitalSignType vital_sign_type)
    {
        runOnUiThread(() ->
        {
            measurement_cache.updateCachedVitalsListAndSortInTimeOrder(vital_sign_type, measurements);

            T most_recent_measurement = measurements.get(measurements.size()-1);

            replaceSavedMeasurementIfNewer(vital_sign_type, most_recent_measurement);

            // This database read may have taken a "while" to execute. The Graphs onResume function will therefore have read the "not updated" cached_temperature_measurements list
            // and plotted "zero" measurements. So push these new measurements to the graph
            if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
            {
                FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
                {
                    fragment.redrawGraphUsingCachedMeasurements(vital_sign_type);
                }
            }
        });
    }


    private int checkForSpO2InvalidReading(int spo2)
    {
    /*
     * When Leads off detected, Patient gateway sends the "-1" value. this value cant be plotted in the graph.
     * SpO2 value is changed to the 150 so that graph can show full y-axis range.
     */
        int INVALID_READING = -1;
        if(spo2 == INVALID_READING)
        {
            spo2 = ErrorCodes.ERROR_CODE__NONIN_WRIST_OX_LEADS_OFF;
        }
        return spo2;
    }


    public boolean includeManualVitalSignEntry()
    {
        return features_enabled.manually_entered_vital_signs;
    }


    public void storeEnableManuallyEnteredVitalSigns(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setManualVitalSignsEnabled(enabled);
    }


    private void showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(DeviceType manually_entered_device_type, SensorType sensor_type)
    {
        // Is there a sensor device? If so it will have its own fragment which the Manual Vital Signs will be drawn on.
        // If there is NOT a sensor device, then make a Manually Entered Vital Signs graph fragment

        if (isOrWasSensorTypeConnected(sensor_type))
        {
            // Hide Manually Entered Vital Sign graph
            hideManuallyEnteredVitalSignDevice(manually_entered_device_type);
        }
        else
        {
            // Show Manually Entered Vital Sign graph
            addOrUpdateManuallyEnteredVitalSignDevice(manually_entered_device_type);
        }
    }


    private void addOrUpdateManuallyEnteredVitalSignDevice(DeviceType device_type)
    {
        DeviceInfo device_info = getDeviceByType(device_type);
        device_info.human_readable_device_id = 0;
        device_info.setDeviceTypePartOfPatientSession(true);
        device_info.measurement_interval_in_seconds = 60;
        addOrUpdateDeviceInfo(device_info);
    }


    private void hideManuallyEnteredVitalSignDeviceDependingOnSensorType(SensorType sensor_type)
    {
        switch (sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                hideManuallyEnteredVitalSignDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE);
                hideManuallyEnteredVitalSignDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE);
            }
            break;

            case SENSOR_TYPE__TEMPERATURE:
            {
                hideManuallyEnteredVitalSignDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE);
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                hideManuallyEnteredVitalSignDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SPO2);
            }
            break;

            case SENSOR_TYPE__BLOOD_PRESSURE:
            {
                hideManuallyEnteredVitalSignDevice(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE);
            }
            break;
        }
    }


    private void hideManuallyEnteredVitalSignDevice(DeviceType device_type)
    {
        DeviceInfo device_info = getDeviceByType(device_type);
        device_info.setDeviceTypePartOfPatientSession(false);
        addOrUpdateDeviceInfo(device_info);
    }


    private void storeManuallyEnteredVitalSign(ManualVitalSignBeingEntered observation, int measurement_validity_time_in_seconds, long timestamp, int by_user_id)
    {
        int vital_sign_id = observation.getVitalSignId();

        String value = observation.getValue();

        // If its a button, then its one of the measurements with predefined values. These ALWAYS have index 0 as "UNKNOWN"
        int button_id = observation.getButtonId() + 1;

        Log.e(TAG, "storeManuallyEnteredVitalSign : vital_sign_id = " + vital_sign_id + " : button_id = " + (button_id - 1));

        VitalSignType vital_sign_type = VitalSignType.values()[vital_sign_id];

        switch(vital_sign_type)
        {
            case MANUALLY_ENTERED_HEART_RATE:
            {
                MeasurementManuallyEnteredHeartRate measurement = new MeasurementManuallyEnteredHeartRate(Integer.parseInt(value), timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(device_type, SensorType.SENSOR_TYPE__LIFETOUCH);
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_RATE:
            {
                MeasurementManuallyEnteredRespirationRate measurement = new MeasurementManuallyEnteredRespirationRate(Integer.parseInt(value), timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(device_type, SensorType.SENSOR_TYPE__LIFETOUCH);
            }
            break;

            case MANUALLY_ENTERED_TEMPERATURE:
            {
                double temperature = Double.parseDouble(value);

                // Temperature is either degrees C or degrees C

                if (isShowTemperatureInFahrenheitEnabled())
                {
                    // It was degrees F so convert back to degrees C
                    temperature = convertTemperatureToDegreesC(temperature);
                }

                MeasurementManuallyEnteredTemperature measurement = new MeasurementManuallyEnteredTemperature(temperature, timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(device_type, SensorType.SENSOR_TYPE__TEMPERATURE);
            }
            break;

            case MANUALLY_ENTERED_SPO2:
            {
                MeasurementManuallyEnteredSpO2 measurement = new MeasurementManuallyEnteredSpO2(Integer.parseInt(value), timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SPO2;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(device_type, SensorType.SENSOR_TYPE__SPO2);
            }
            break;

            case MANUALLY_ENTERED_BLOOD_PRESSURE:
            {
                if (value.contains("/"))
                {
                    int position_of_slash = value.indexOf("/");

                    String systolic_string = value.substring(0, position_of_slash);
                    String diastolic_string = value.substring(position_of_slash + 1);

                    int systolic = Integer.parseInt(systolic_string);
                    int diastolic = Integer.parseInt(diastolic_string);

                    MeasurementManuallyEnteredBloodPressure measurement = new MeasurementManuallyEnteredBloodPressure(systolic, diastolic, timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                    Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                    DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE;

                    portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                    showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(device_type, SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
                }
                else
                {
                    // No slash in "value"
                }
            }
            break;

            case MANUALLY_ENTERED_WEIGHT:
            {
                double weight = Double.parseDouble(value);

                if (isShowWeightInLbsEnabled())
                {
                    // It was lbs so convert back to Kg
                    weight = convertWeightToKg(weight);
                }

                MeasurementManuallyEnteredWeight measurement = new MeasurementManuallyEnteredWeight(weight, timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                showOrHideManuallyEnteredVitalSignGraphFragmentDependentOnIfSensorConnected(device_type, SensorType.SENSOR_TYPE__WEIGHT_SCALE);
            }
            break;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
            {
                int consciousness_level = button_id;

                MeasurementConsciousnessLevel measurement = new MeasurementConsciousnessLevel(consciousness_level, timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                addOrUpdateManuallyEnteredVitalSignDevice(device_type);
            }
            break;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
            {
                int supplemental_oxygen_level = button_id;

                MeasurementSupplementalOxygenLevel measurement = new MeasurementSupplementalOxygenLevel(supplemental_oxygen_level, timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                addOrUpdateManuallyEnteredVitalSignDevice(device_type);
            }
            break;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            {
                int refill_time = button_id;

                MeasurementCapillaryRefillTime measurement = new MeasurementCapillaryRefillTime(refill_time, timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_CAPILLARY_REFILL_TIME;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                addOrUpdateManuallyEnteredVitalSignDevice(device_type);
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                int respiration_distress = button_id;

                MeasurementRespirationDistress measurement = new MeasurementRespirationDistress(respiration_distress, timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_DISTRESS;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                addOrUpdateManuallyEnteredVitalSignDevice(device_type);
            }
            break;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                int concern = button_id;

                MeasurementFamilyOrNurseConcern measurement = new MeasurementFamilyOrNurseConcern(concern, timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                addOrUpdateManuallyEnteredVitalSignDevice(device_type);
            }
            break;

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                MeasurementUrineOutput measurement = new MeasurementUrineOutput(Integer.parseInt(value), timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());

                Log.e(TAG, "storeManuallyEnteredVitalSign = " + UtilityFunctions.dumpMeasurementInfo(vital_sign_type, measurement));

                DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_URINE_OUTPUT;

                portal_system_commands.sendGatewayCommand_storeVitalSign(vital_sign_type, device_type, measurement, by_user_id);

                addOrUpdateManuallyEnteredVitalSignDevice(device_type);
            }
            break;
        }
    }


    private void storeAnnotation(String value, long timestamp, int measurement_validity_time_in_seconds, int by_user_id)
    {
        MeasurementAnnotation measurement = new MeasurementAnnotation(value, timestamp, measurement_validity_time_in_seconds, getNtpTimeNowInMilliseconds());
        DeviceType device_type = DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_ANNOTATION;

        portal_system_commands.sendGatewayCommand_storeVitalSign(VitalSignType.MANUALLY_ENTERED_ANNOTATION, device_type, measurement, by_user_id);

        modeSelectionSelected();
    }

    // Overloaded storeAuditTrailEvent(), use the version with the "option" parameter if an "option" is necessary to be logged.
    private void storeAuditTrailEvent(AuditTrailEvent event, int by_user_id, String additional)
    {
        portal_system_commands.sendGatewayCommand_storeAuditTrailEvent(event, ntp_time.currentTimeMillis(), by_user_id, additional);
    }


    private void storeAuditTrailEvent(AuditTrailEvent event, int by_user_id)
    {
        storeAuditTrailEvent(event, by_user_id, "");
    }


    // Audit logs
    private void recordAuditTrailEventForScreenLockedByPageTimeout()
    {
        switch (current_page)
        {
            case PATIENT_VITALS_DISPLAY:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_PATIENT_VITALS_DISPLAY_TIMEOUT, gateway_user_id);
            }
            break;

            case MODE_SELECTION:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_MODE_SELECTION_TIMEOUT, gateway_user_id);
            }
            break;

            case ADMIN_MODE:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_ADMIN_PAGE_TIMEOUT, gateway_user_id);
            }
            break;

            case SOFTWARE_UPDATE_AVAILABLE:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_SOFTWARE_UPDATE_AVAILABLE_TIMEOUT, gateway_user_id);
            }
            break;

            case PATIENT_DETAILS_NAME:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_PATIENT_DETAILS_NAME_TIMEOUT, gateway_user_id);
            }
            break;

            case PATIENT_THRESHOLD_CATEGORY:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_PATIENT_DETAILS_AGE_TIMEOUT, gateway_user_id);
            }
            break;

            case END_SETTINGS:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_END_SETTINGS_TIMEOUT, gateway_user_id);
            }
            break;

            case END_SESSION_TIME_SELECTION:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_END_SESSION_TIME_SELECTION_TIMEOUT, gateway_user_id);
            }
            break;

            case CHECK_DEVICE_STATUS:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_CHECK_DEVICE_STATUS_TIMEOUT, gateway_user_id);
            }
            break;

            case ADD_DEVICES:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_ADD_DEVICES_TIMEOUT, gateway_user_id);
            }
            break;

            case GATEWAY_NOT_RESPONDING:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_GATEWAY_NOT_RESPONDING_TIMEOUT, gateway_user_id);
            }
            break;

            case CHANGE_SESSION_SETTINGS:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_CHANGE_SESSION_SETTINGS_TIMEOUT, gateway_user_id);
            }
            break;

            case LOGCAT_DISPLAY:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_LOGCAT_DISPLAY_TIMEOUT, gateway_user_id);
            }
            break;

            case EMPTY_PATIENT_VITALS_DISPLAY:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_EMPTY_PATIENT_VITALS_DISPLAY_TIMEOUT, gateway_user_id);
            }
            break;

            case VIEW_MANUALLY_ENTERED_VITAL_SIGNS:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_MANUALLY_ENTERED_VITAL_SIGNS_TIMEOUT, gateway_user_id);
            }
            break;

            case OBSERVATION_SET_TIME_SELECTION:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_OBSERVATION_SET_TIME_SELECTION_TIMEOUT, gateway_user_id);
            }
            break;

            case OBSERVATION_SET_VITAL_SIGN_SELECTION:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_OBSERVATION_SET_VITAL_SIGN_SELECTION_TIMEOUT, gateway_user_id);
            }
            break;

            case OBSERVATION_SET_VALIDITY_TIME_ENTRY:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_OBSERVATION_SET_VALIDITY_TIME_ENTRY_TIMEOUT, gateway_user_id, String.valueOf(gateway_audit_last_option_chosen));
            }
            break;

            case OBSERVATION_SET_CONFIRMATION:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_OBSERVATION_SET_CONFIRMATION_TIMEOUT, gateway_user_id, String.valueOf(gateway_audit_last_option_chosen));
            }
            break;

            case OBSERVATION_SET_MEASUREMENT_BUTTON_ENTRY:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_OBSERVATION_SET_MEASUREMENT_BUTTON_ENTRY_TIMEOUT, gateway_user_id, String.valueOf(gateway_audit_last_option_chosen));
            }
            break;

            case OBSERVATION_SET_MEASUREMENT_KEYPAD_ENTRY:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_OBSERVATION_SET_MEASUREMENT_KEYPAD_ENTRY_TIMEOUT, gateway_user_id, String.valueOf(gateway_audit_last_option_chosen));
            }
            break;

            case ANNOTATION_ENTRY_CONFIRMATION:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_ANNOTATION_ENTRY_CONFIRMATION_TIMEOUT, gateway_user_id);
            }
            break;

            case ANNOTATION_ENTRY_SELECT_ANNOTATION_TYPE:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_ANNOTATION_ENTRY_SELECT_ANNOTATION_TYPE_TIMEOUT, gateway_user_id);
            }
            break;

            case ANNOTATION_ENTRY_SELECT_ANNOTATION_TIME:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_ANNOTATION_ENTRY_SELECT_ANNOTATION_TIME_TIMEOUT, gateway_user_id);
            }
            break;

            case ANNOTATION_ENTRY_ENTER_ANNOTATION_VIA_KEYBOARD:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_ANNOTATION_ENTRY_ENTER_ANNOTATION_VIA_KEYBOARD_TIMEOUT, gateway_user_id);
            }
            break;

            case ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_CONDITION:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_CONDITION_TIMEOUT, gateway_user_id);
            }
            break;

            case ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_ACTION:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_ACTION_TIMEOUT, gateway_user_id);
            }
            break;

            case ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_OUTCOME:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_OUTCOME_TIMEOUT, gateway_user_id);
            }
            break;

            case GATEWAY_CONFIGURATION_ERROR:
            {
                storeAuditTrailEvent(AuditTrailEvent.SCREEN_LOCKED_BY_GATEWAY_CONFIGURATION_ERROR_TIMEOUT, gateway_user_id);
            }
            break;

            default:
                Log.v(TAG, "Unhandled page timeout for audit " + current_page);
                break;
        }
}

    private void updatePopupServerSyncRowsPending(Intent intent)
    {
        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
        {
            // Session Info
            popup_server_syncing.showPatientDetailsRowsPending(intent.getParcelableExtra("patient_details_rows_pending"));

            popup_server_syncing.showDeviceInfoRowsPending(intent.getParcelableExtra("device_info_rows_pending"));

            popup_server_syncing.showStartPatientSessionRowsPending(intent.getParcelableExtra("start_patient_session_rows_pending"));
            popup_server_syncing.showStartDeviceSessionRowsPending(intent.getParcelableExtra("start_device_session_rows_pending"));
            popup_server_syncing.showEndDeviceSessionRowsPending(intent.getParcelableExtra("end_device_session_rows_pending"));
            popup_server_syncing.showEndPatientSessionRowsPending(intent.getParcelableExtra("end_patient_session_rows_pending"));

            // Connection Events
            popup_server_syncing.showActiveSessionConnectionEventRowsPending(intent.getParcelableExtra("active_session_connection_event_rows_pending"));
            popup_server_syncing.showOldSessionConnectionEventRowsPending(intent.getParcelableExtra("old_session_connection_event_rows_pending"));

            // Lifetouch
            popup_server_syncing.showActiveSessionLifetouchHeartRateRowsPending(intent.getParcelableExtra("active_session_lifetouch_heart_rate_rows_pending"));
            popup_server_syncing.showOldSessionLifetouchHeartRateRowsPending(intent.getParcelableExtra("old_session_lifetouch_heart_rate_rows_pending"));

            popup_server_syncing.showActiveSessionLifetouchHeartBeatRowsPending(intent.getParcelableExtra("active_session_lifetouch_heart_beat_rows_pending"));
            popup_server_syncing.showOldSessionLifetouchHeartBeatRowsPending(intent.getParcelableExtra("old_session_lifetouch_heart_beat_rows_pending"));

            popup_server_syncing.showActiveSessionLifetouchRespirationRateRowsPending(intent.getParcelableExtra("active_session_lifetouch_respiration_rate_rows_pending"));
            popup_server_syncing.showOldSessionLifetouchRespirationRateRowsPending(intent.getParcelableExtra("old_session_lifetouch_respiration_rate_rows_pending"));

            popup_server_syncing.showActiveSessionLifetouchSetupModeSampleRowsPending(intent.getParcelableExtra("active_session_lifetouch_setup_mode_rows_pending"));
            popup_server_syncing.showOldSessionLifetouchSetupModeSampleRowsPending(intent.getParcelableExtra("old_session_lifetouch_setup_mode_rows_pending"));

            popup_server_syncing.showActiveSessionLifetouchRawAccelerometerModeSampleRowsPending(intent.getParcelableExtra("active_session_lifetouch_raw_accelerometer_mode_rows_pending"));
            popup_server_syncing.showOldSessionLifetouchRawAccelerometerModeSampleRowsPending(intent.getParcelableExtra("old_session_lifetouch_raw_accelerometer_mode_rows_pending"));

            popup_server_syncing.showActiveSessionLifetouchBatteryRowsPending(intent.getParcelableExtra("active_session_lifetouch_battery_measurement_rows_pending"));
            popup_server_syncing.showOldSessionLifetouchBatteryRowsPending(intent.getParcelableExtra("old_session_lifetouch_battery_measurement_rows_pending"));

            popup_server_syncing.showActiveSessionLifetouchPatientOrientationRowsPending(intent.getParcelableExtra("active_session_lifetouch_patient_orientation_rows_pending"));
            popup_server_syncing.showOldSessionLifetouchPatientOrientationRowsPending(intent.getParcelableExtra("old_session_lifetouch_patient_orientation_rows_pending"));

            // Lifetemp
            popup_server_syncing.showActiveSessionLifetempTemperatureRowsPending(intent.getParcelableExtra("active_session_lifetemp_temperature_measurement_rows_pending"));
            popup_server_syncing.showOldSessionLifetempTemperatureRowsPending(intent.getParcelableExtra("old_session_lifetemp_temperature_measurement_rows_pending"));

            popup_server_syncing.showActiveSessionLifetempBatteryRowsPending(intent.getParcelableExtra("active_session_lifetemp_battery_measurement_rows_pending"));
            popup_server_syncing.showOldSessionLifetempBatteryRowsPending(intent.getParcelableExtra("old_session_lifetemp_battery_measurement_rows_pending"));

            // Pulse Ox
            popup_server_syncing.showActiveSessionPulseOxSpO2RowsPending(intent.getParcelableExtra("active_session_pulse_ox_spo2_measurement_rows_pending"));
            popup_server_syncing.showOldSessionPulseOxSpO2RowsPending(intent.getParcelableExtra("old_session_pulse_ox_spo2_measurement_rows_pending"));

            popup_server_syncing.showActiveSessionPulseOxIntermediateRowsPending(intent.getParcelableExtra("active_session_pulse_ox_intermediate_measurement_rows_pending"));
            popup_server_syncing.showOldSessionPulseOxIntermediateRowsPending(intent.getParcelableExtra("old_session_pulse_ox_intermediate_measurement_rows_pending"));

            popup_server_syncing.showActiveSessionPulseOxSetupModeSampleRowsPending(intent.getParcelableExtra("active_session_pulse_ox_setup_mode_rows_pending"));
            popup_server_syncing.showOldSessionPulseOxSetupModeSampleRowsPending(intent.getParcelableExtra("old_session_pulse_ox_setup_mode_rows_pending"));

            popup_server_syncing.showActiveSessionPulseOxBatteryRowsPending(intent.getParcelableExtra("active_session_pulse_ox_battery_measurement_rows_pending"));
            popup_server_syncing.showOldSessionPulseOxBatteryRowsPending(intent.getParcelableExtra("old_session_pulse_ox_battery_measurement_rows_pending"));

            // Blood Pressure
            popup_server_syncing.showActiveSessionBloodPressureRowsPending(intent.getParcelableExtra("active_session_blood_pressure_measurement_rows_pending"));
            popup_server_syncing.showOldSessionBloodPressureRowsPending(intent.getParcelableExtra("old_session_blood_pressure_measurement_rows_pending"));

            popup_server_syncing.showActiveSessionBloodPressureBatteryRowsPending(intent.getParcelableExtra("active_session_blood_pressure_battery_measurement_rows_pending"));
            popup_server_syncing.showOldSessionBloodPressureBatteryRowsPending(intent.getParcelableExtra("old_session_blood_pressure_battery_measurement_rows_pending"));

            // Weight Scale
            popup_server_syncing.showActiveSessionWeightScaleRowsPending(intent.getParcelableExtra("active_session_weight_scale_measurement_rows_pending"));
            popup_server_syncing.showOldSessionWeightScaleRowsPending(intent.getParcelableExtra("old_session_weight_scale_measurement_rows_pending"));

            popup_server_syncing.showActiveSessionWeightScaleBatteryRowsPending(intent.getParcelableExtra("active_session_weight_scale_battery_measurement_rows_pending"));
            popup_server_syncing.showOldSessionWeightScaleBatteryRowsPending(intent.getParcelableExtra("old_session_weight_scale_battery_measurement_rows_pending"));

            // Manual Vitals
            popup_server_syncing.showActiveSessionManuallyEnteredHeartRateRowsPending(intent.getParcelableExtra("active_session_manually_entered_heart_rate_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredHeartRateRowsPending(intent.getParcelableExtra("old_session_manually_entered_heart_rate_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredRespirationRateRowsPending(intent.getParcelableExtra("active_session_manually_entered_respiration_rate_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredRespirationRateRowsPending(intent.getParcelableExtra("old_session_manually_entered_respiration_rate_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredTemperatureRowsPending(intent.getParcelableExtra("active_session_manually_entered_temperature_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredTemperatureRowsPending(intent.getParcelableExtra("old_session_manually_entered_temperature_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredSpO2RowsPending(intent.getParcelableExtra("active_session_manually_entered_spo2_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredSpO2RowsPending(intent.getParcelableExtra("old_session_manually_entered_spo2_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredBloodPressureRowsPending(intent.getParcelableExtra("active_session_manually_entered_blood_pressure_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredBloodPressureRowsPending(intent.getParcelableExtra("old_session_manually_entered_blood_pressure_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredWeightRowsPending(intent.getParcelableExtra("active_session_manually_entered_weight_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredWeightRowsPending(intent.getParcelableExtra("old_session_manually_entered_weight_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredConsciousnessLevelRowsPending(intent.getParcelableExtra("active_session_manually_entered_consciousness_level_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredConsciousnessLevelRowsPending(intent.getParcelableExtra("old_session_manually_entered_consciousness_level_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredSupplementalOxygenLevelRowsPending(intent.getParcelableExtra("active_session_manually_entered_supplemental_oxygen_level_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredSupplementalOxygenLevelRowsPending(intent.getParcelableExtra("old_session_manually_entered_supplemental_oxygen_level_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredAnnotationRowsPending(intent.getParcelableExtra("active_session_manually_entered_annotation_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredAnnotationRowsPending(intent.getParcelableExtra("old_session_manually_entered_annotation_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredCapillaryRefillTimeRowsPending(intent.getParcelableExtra("active_session_manually_entered_capillary_refill_time_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredCapillaryRefillTimeRowsPending(intent.getParcelableExtra("old_session_manually_entered_capillary_refill_time_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredRespirationDistressRowsPending(intent.getParcelableExtra("active_session_manually_entered_respiration_distress_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredRespirationDistressRowsPending(intent.getParcelableExtra("old_session_manually_entered_respiration_distress_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredFamilyOrNurseConcernRowsPending(intent.getParcelableExtra("active_session_manually_entered_family_or_nurse_concern_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredFamilyOrNurseConcernRowsPending(intent.getParcelableExtra("old_session_manually_entered_family_or_nurse_concern_rows_pending"));

            popup_server_syncing.showActiveSessionManuallyEnteredUrineOutputRowsPending(intent.getParcelableExtra("active_session_manually_entered_urine_output_rows_pending"));
            popup_server_syncing.showOldSessionManuallyEnteredUrineOutputRowsPending(intent.getParcelableExtra("old_session_manually_entered_urine_output_rows_pending"));

            // Early Warning Scores
            popup_server_syncing.showActiveSessionEarlyWarningScoreRowsPending(intent.getParcelableExtra("active_session_early_warning_score_rows_pending"));
            popup_server_syncing.showOldSessionEarlyWarningScoreRowsPending(intent.getParcelableExtra("old_session_early_warning_score_rows_pending"));

            // Setup Mode Start/Stop
            popup_server_syncing.showActiveSessionSetupModeLogRowsPending(intent.getParcelableExtra("active_session_setup_mode_log_rows_pending"));
            popup_server_syncing.showOldSessionSetupModeLogRowsPending(intent.getParcelableExtra("old_session_setup_mode_log_rows_pending"));

            // Audit trail
            popup_server_syncing.showAuditTrailRowsPending(intent.getParcelableExtra("auditable_events_rows_pending"));
        }
    }


    public void annotationTypeSelected(AnnotationEntryType annotation_entry_type)
    {
        annotation_being_entered.setAnnotationEntryType(annotation_entry_type);

        showNextButton(true);
    }


    public void measurementValidityTimeSelected(VitalSignValidityTimeDescriptor time_selected)
    {
        observation_set_being_entered.setValidityTime(time_selected);
        observationSetVitalSignSelectionSelected();
    }


    public void validateEnteredContactId(String enteredText)
    {
        FragmentPatientCaseIdEntry fragment = (FragmentPatientCaseIdEntry) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            boolean showCaseIdLookupButton;

            if (enteredText.isEmpty())
            {
                showCaseIdLookupButton = false;
            }
            else
            {
                showCaseIdLookupButton = true;
            }

            fragment.showLookupButton(showCaseIdLookupButton);
        }
    }


    public boolean validateManuallyEnteredVitalSignValue(VitalSignType vital_sign_type, String vital_sign_value_as_string)
    {
        boolean show_keypad_as_entered_value_is_valid = false;

        boolean show_next_button_as_value_not_blank;

        // This is a simple check to make sure the entered value is in range for the vital sign type.
        if (vital_sign_value_as_string.isEmpty())
        {
            show_keypad_as_entered_value_is_valid = true;
            show_next_button_as_value_not_blank = false;
        }
        else
        {
            show_next_button_as_value_not_blank = true;

            switch (vital_sign_type)
            {
                case MANUALLY_ENTERED_HEART_RATE:
                {
                    int heart_rate = Integer.parseInt(vital_sign_value_as_string);

                    show_keypad_as_entered_value_is_valid = (heart_rate > 0) && (heart_rate < 300);
                }
                break;

                case MANUALLY_ENTERED_RESPIRATION_RATE:
                {
                    int respiration_rate = Integer.parseInt(vital_sign_value_as_string);

                    show_keypad_as_entered_value_is_valid = (respiration_rate > 0) && (respiration_rate < 300);
                }
                break;

                case MANUALLY_ENTERED_TEMPERATURE:
                {
                    double temperature = Double.parseDouble(vital_sign_value_as_string);

                    // Temperature is either degrees C or degrees F

                    if (isShowTemperatureInFahrenheitEnabled())
                    {
                        // It was degrees F so convert back to degrees C
                        temperature = convertTemperatureToDegreesC(temperature);
                    }

                    String[] split = vital_sign_value_as_string.split("\\.");

                    if((split.length > 1) && (split[1].length() > 1))
                    {
                        show_keypad_as_entered_value_is_valid = false;
                    }
                    else if (temperature <= 45.0)
                    {
                        show_keypad_as_entered_value_is_valid = true;
                    }
                    else
                    {
                        show_keypad_as_entered_value_is_valid = false;
                    }
                }
                break;

                case MANUALLY_ENTERED_SPO2:
                {
                    int spo2 = Integer.parseInt(vital_sign_value_as_string);

                    show_keypad_as_entered_value_is_valid = (spo2 > 0) && (spo2 <= 100);
                }
                break;

                case MANUALLY_ENTERED_BLOOD_PRESSURE:
                {
                    // Need to check we have valid systolic AND diastolic before allowing the user to click on Enter
                    show_next_button_as_value_not_blank = false;

                    String systolic_string;
                    String diastolic_string;

                    boolean systolic_valid = true;
                    boolean diastolic_valid = true;

                    int slash_position = vital_sign_value_as_string.indexOf("/");

                    // Check to see if the String has a "/" in it. If so split into Systolic and Diastolic
                    if (slash_position >= 0)
                    {
                        systolic_string = vital_sign_value_as_string.substring(0, slash_position);
                        diastolic_string = vital_sign_value_as_string.substring(slash_position + 1);
                    }
                    else
                    {
                        systolic_string = vital_sign_value_as_string;
                        diastolic_string = "";
                    }

                    if (systolic_string.length() > 0)
                    {
                        // Check the Systolic value to make sure it is within a real range
                        int systolic = Integer.parseInt(systolic_string);

                        systolic_valid = (systolic > 0) && (systolic <= 300);
                    }

                    if (diastolic_string.length() > 0)
                    {
                        // Check the diastolic value to make sure it is within a real range
                        int diastolic = Integer.parseInt(diastolic_string);

                        if ((diastolic > 0) && (diastolic <= 300))
                        {
                            diastolic_valid = true;

                            show_next_button_as_value_not_blank = systolic_valid;
                        }
                        else
                        {
                            diastolic_valid = false;
                        }
                    }

                    show_keypad_as_entered_value_is_valid = systolic_valid && diastolic_valid;
                }
                break;

                case MANUALLY_ENTERED_WEIGHT:
                {
                    double weight = Double.parseDouble(vital_sign_value_as_string);

                    if (isShowWeightInLbsEnabled())
                    {
                        // It was lbs so convert back to Kg
                        weight = convertWeightToKg(weight);
                    }

                    String[] split = vital_sign_value_as_string.split("\\.");

                    if((split.length > 1) && (split[1].length() > 1))
                    {
                        show_keypad_as_entered_value_is_valid = false;
                    }
//TODO do we need a valid range here??
                    else if ((weight > 0) && (weight < 500))
                    {
                        show_keypad_as_entered_value_is_valid = true;
                    }
                    else
                    {
                        show_keypad_as_entered_value_is_valid = false;
                    }
                }
                break;

                case MANUALLY_ENTERED_URINE_OUTPUT:
                {
                    int value = Integer.parseInt(vital_sign_value_as_string);

                    show_keypad_as_entered_value_is_valid = (value > 0) && (value <= 500);
                }
                break;

                case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                case MANUALLY_ENTERED_ANNOTATION:
                case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                {
                    show_keypad_as_entered_value_is_valid = true;
                }
                break;

                case HEART_RATE:
                case RESPIRATION_RATE:
                case TEMPERATURE:
                case SPO2:
                case BLOOD_PRESSURE:
                case EARLY_WARNING_SCORE:
                case PATIENT_ORIENTATION:
                case NOT_SET_YET:
                    break;
            }
        }

        if(show_keypad_as_entered_value_is_valid == false)
        {
            // entered value is invalid, so set show_top_button_as_value_not_blank to false
            show_next_button_as_value_not_blank = false;
        }

        showNextButton(show_next_button_as_value_not_blank);

        return show_keypad_as_entered_value_is_valid;
    }


    public static class VitalSignAsStrings
    {
        String measurement;
        String timestamp_in_ms;
    }


    private String getHeartRateUnits()
    {
        return getResources().getString(R.string.bpm);
    }


    private String getRespirationRateUnits()
    {
        return getResources().getString(R.string.breath_per_min);
    }


    private String getTemperatureUnits()
    {
        if (isShowTemperatureInFahrenheitEnabled())
        {
            return getResources().getString(R.string.degreesF);
        }
        else
        {
            return getResources().getString(R.string.degreesC);
        }
    }


    private String getSpO2Units()
    {
        return "%";
    }


    private String getWeightUnits()
    {
        if (isShowWeightInLbsEnabled())
        {
            return getResources().getString(R.string.pounds_measurement_unit_preceded_by_space);
        }
        else
        {
            return getResources().getString(R.string.kilogram_measurement_unit_preceded_by_space);
        }
    }


    private String getUrineOutputUnits()
    {
        return "ml";
    }


    private double convertTemperatureToDegreesF(double temperature)
    {
        if(temperature < ErrorCodes.ERROR_CODES)
        {
            return (temperature * 1.8) + 32;
        }
        else
        {
            return temperature; // Don't convert error code values
        }
    }


    private double convertTemperatureToDegreesC(double temperature)
    {
        return (temperature - 32) / 1.8;
    }


    private double convertWeightToKg(double weight)
    {
        return (weight * 0.45359237d);
    }


    private double convertWeightToLbs(double weight)
    {
        return (weight * 2.2046226218d);
    }


    private BigDecimal roundValueToOneOrTwoDecimalPlaces(double value, boolean display_to_two_decimal_places)
    {
        BigDecimal rounded_value;

        if(display_to_two_decimal_places)
        {
            rounded_value = new BigDecimal(String.valueOf(value)).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        else
        {
            rounded_value = new BigDecimal(String.valueOf(value)).setScale(1, BigDecimal.ROUND_HALF_UP);
        }

        return rounded_value;
    }


    public <T extends MeasurementVitalSign> String formatMeasurementForDisplay(T generic_measurement)
    {
        VitalSignType vitalSignType = generic_measurement.getType();

        MeasurementTypes measurementType = MeasurementTypes.getMeasurementTypeFromVitalSignType(vitalSignType);
        ArrayList<ThresholdSetLevel> thresholdSetLevels = patient_info.getThresholdSetAgeBlockDetails().getThresholdSetLevelForMeasurementType(measurementType);

        switch (vitalSignType)
        {
            case HEART_RATE:
            case MANUALLY_ENTERED_HEART_RATE:
            case RESPIRATION_RATE:
            case MANUALLY_ENTERED_RESPIRATION_RATE:
            {
                //MeasurementHeartRate measurement = (MeasurementHeartRate) generic_measurement;
                return String.format(Locale.getDefault(), "%3d", (int)generic_measurement.getPrimaryMeasurement());
            }

            case TEMPERATURE:
            case MANUALLY_ENTERED_TEMPERATURE:
            {
                BigDecimal rounded_value = roundValueToOneOrTwoDecimalPlaces(generic_measurement.getPrimaryMeasurement(), getManufacturingModeEnabled());
                return rounded_value.toString() + getTemperatureUnits();
            }

            case SPO2:
            case MANUALLY_ENTERED_SPO2:
            {
                return String.format(Locale.getDefault(), "%2d", (int)generic_measurement.getPrimaryMeasurement()) + getSpO2Units();
            }

            case BLOOD_PRESSURE:
            case MANUALLY_ENTERED_BLOOD_PRESSURE:
            {
                if(generic_measurement.getType() == VitalSignType.BLOOD_PRESSURE)
                {
                    MeasurementBloodPressure cast_measurement = (MeasurementBloodPressure)generic_measurement;

                    return String.format(Locale.getDefault(), "%3d", cast_measurement.systolic) + "/" + String.format(Locale.getDefault(), "%3d", cast_measurement.diastolic);
                }
                else
                {
                    MeasurementManuallyEnteredBloodPressure cast_measurement = (MeasurementManuallyEnteredBloodPressure)generic_measurement;

                    return String.format(Locale.getDefault(), "%3d", cast_measurement.systolic) + "/" + String.format(Locale.getDefault(), "%3d", cast_measurement.diastolic);
                }
            }

            case WEIGHT:
            case MANUALLY_ENTERED_WEIGHT:
            {
                BigDecimal rounded_value = roundValueToOneOrTwoDecimalPlaces(generic_measurement.getPrimaryMeasurement(), false);

                return rounded_value.toString() + getWeightUnits();
            }

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
            {
                MeasurementConsciousnessLevel measurement = (MeasurementConsciousnessLevel) generic_measurement;
                int value = measurement.value - 1;                                                  // Button values start at 1 as lowest number but threshold array start index is 0
                return thresholdSetLevels.get(value).display_text;
            }

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
            {
                MeasurementSupplementalOxygenLevel measurement = (MeasurementSupplementalOxygenLevel) generic_measurement;
                int value = measurement.value - 1;                                                  // Button values start at 1 as lowest number but threshold array start index is 0
                return thresholdSetLevels.get(value).display_text;
            }

            case MANUALLY_ENTERED_ANNOTATION:
            {
                MeasurementAnnotation measurement = (MeasurementAnnotation) generic_measurement;
                return measurement.annotation;
            }

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            {
                MeasurementCapillaryRefillTime measurement = (MeasurementCapillaryRefillTime) generic_measurement;
                int value = measurement.value - 1;                                                  // Button values start at 1 as lowest number but threshold array start index is 0
                return thresholdSetLevels.get(value).display_text;
            }

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                MeasurementRespirationDistress measurement = (MeasurementRespirationDistress) generic_measurement;
                int value = measurement.value - 1;                                                  // Button values start at 1 as lowest number but threshold array start index is 0
                return thresholdSetLevels.get(value).display_text;
            }

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                MeasurementFamilyOrNurseConcern measurement = (MeasurementFamilyOrNurseConcern) generic_measurement;
                int value = measurement.concern - 1;                                                // Button values start at 1 as lowest number but threshold array start index is 0
                return thresholdSetLevels.get(value).display_text;
            }

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                return String.format(Locale.getDefault(), "%3d", (int)generic_measurement.getPrimaryMeasurement());
            }

            case EARLY_WARNING_SCORE:
            {
                MeasurementEarlyWarningScore cast_measurement = (MeasurementEarlyWarningScore) generic_measurement;

                String ews = String.valueOf(cast_measurement.early_warning_score);

                // Code to deal with EWS special cases for colours
                if (cast_measurement.is_special_alert)
                {
                    ews += "*";
                }

                ews += "/";
                ews += cast_measurement.max_possible_score;

                return ews;
            }

            case PATIENT_ORIENTATION:
            case NOT_SET_YET:
                break;
        }

        return "";
    }


    public ArrayList<VitalSignAsStrings> getDataForManuallyEnteredVitalSignsDisplayIndividualFragment(VitalSignType vital_sign_type)
    {
        ArrayList<VitalSignAsStrings> list = new ArrayList<>();
        ArrayList<? extends MeasurementVitalSign> original_measurement_list = getCachedMeasurements(vital_sign_type);

        if (getListOfManualVitalSigns().contains(vital_sign_type))
        {
            for(MeasurementVitalSign measurement : original_measurement_list)
            {
                VitalSignAsStrings vital_sign_as_strings = new VitalSignAsStrings();

                vital_sign_as_strings.measurement = formatMeasurementForDisplay(measurement);
                vital_sign_as_strings.timestamp_in_ms = TimestampConversion.convertDateToHumanReadableStringWeekdayMonthDayHoursMinutes(measurement.timestamp_in_ms);

                list.add(vital_sign_as_strings);
            }
        }

        return list;
    }


    private InputMethodManager getInputMethodManager()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm;
    }


    private boolean isBluetoothKeyboardConnected()
    {
        return getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
    }


    public void forceShowOnScreenKeyboard(EditText editText)
    {
        // Disable the Samsung keyboard toolbar and Settings button
        editText.setPrivateImeOptions("disableToolbar=true");

        getInputMethodManager().toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void dismissOnScreenKeyboard(Context context, View view)
    {
        getInputMethodManager().hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private boolean isOrWasSensorTypeConnected(SensorType sensor_type)
    {
        return getDeviceByType(sensor_type).isDeviceTypePartOfPatientSession();
    }


    public String reportEarlyWarningScoreType()
    {
        ThresholdSet thresholdSet = patient_info.getThresholdSet();
        ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = patient_info.getThresholdSetAgeBlockDetails();

        if ((thresholdSet != null) && (thresholdSetAgeBlockDetail != null))
        {
            return thresholdSet.name + System.lineSeparator() + thresholdSetAgeBlockDetail.display_name;
        }
        else
        {
            return getResources().getString(R.string.stringEarlyWarningScoreTypeNotReceived);
        }
    }


    /* This function is only called from the AddDevices fragment, where we want to know whether the "Add" button has been pressed or not.
     * If isDeviceTypePartOfPatientSession is true, then a device session has been started in the gateway.
     * But that's no good, as we're on this page before the sessions have been started.
     * isDeviceHumanReadableDeviceIdValid should be true if the add button has been pressed. So that's the condition we need to use.
     */
    public boolean getEarlyWarningScoresDeviceEnabled()
    {
        return getDeviceByType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).isDeviceHumanReadableDeviceIdValid();
    }

    public boolean isEarlyWarningScoresDeviceSessionInProgress()
    {
        return getDeviceByType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).isDeviceSessionInProgress();
    }

    public void enableEarlyWarningScoreDevice(boolean enabled)
    {
        getDeviceByType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).dummy_data_mode = false;

        portal_system_commands.sendGatewayCommand_setEarlyWarningScoresDeviceEnabled(enabled, false, gateway_user_id);
    }

    public void enableDummyEarlyWarningScoreDevice(boolean enabled)
    {
        getDeviceByType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).setDeviceTypePartOfPatientSession(enabled);
        getDeviceByType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE).dummy_data_mode = true;

        portal_system_commands.sendGatewayCommand_setEarlyWarningScoresDeviceEnabled(enabled, true, gateway_user_id);
    }

    public void spoofEarlyWarningScores(boolean spoof)
    {
        portal_system_commands.sendGatewayCommand_setSpoofEarlyWarningScores(spoof);
    }

    public void showSyncStatusPopup()
    {
        portal_system_commands.sendGatewayCommand_getDatabaseStatus();

        if((popup_server_syncing.getDialog() == null) || (!popup_server_syncing.getDialog().isShowing()))	// This is safe, because if the sync_status is null, using || the second statement won't be evaluated.
        {
            startPopupServerSyncingTimers();

            popup_server_syncing.show(getSupportFragmentManager(), "");
        }
        else
        {
            Log.d(TAG, "showSyncStatusPopup : popup already showing");
        }
    }

    private void startPopupServerSyncingTimers()
    {
        Log.d(TAG, "startPopupServerSyncingTimers");

        startPopupServerSyncingTimerDatabaseRefresh();

        startPopupServerSyncingAutoCloseTimer();
    }


    private void stopPopupServerSyncingTimers()
    {
        Log.d(TAG, "stopPopupServerSyncingTimers");

        stopPopupServerSyncingTimerDatabaseRefresh();

        stopPopupServerSyncingTimerAutoClose();
    }


    private void retryServerSyncing()
    {
        portal_system_commands.sendGatewayCommand_resetDatabaseFailedToSendStatus();
    }


    private Timer timer_popup_server_syncing_auto_close = new Timer();
    private Timer timer_popup_server_syncing_database_refresh = new Timer();

    private void startPopupServerSyncingAutoCloseTimer()
    {
        Log.d(TAG, "startPopupServerSyncingAutoCloseTimer");

        if(timer_popup_server_syncing_auto_close != null)
        {
            timer_popup_server_syncing_auto_close.cancel();
        }

        timer_popup_server_syncing_auto_close = new Timer();

        timer_popup_server_syncing_auto_close.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(() -> {
                    Log.d(TAG, "Auto closing Popup Server Syncing window");
                    popup_server_syncing.dismissWindow();
                });
            }
        }, 15 * (int)DateUtils.MINUTE_IN_MILLIS);
    }


    private void startPopupServerSyncingTimerDatabaseRefresh()
    {
        Log.d(TAG, "startPopupServerSyncingTimerDatabaseRefresh");

        final int DATABASE_STATUS_UPDATE_INTERVAL = 60 * (int) DateUtils.SECOND_IN_MILLIS;

        if(timer_popup_server_syncing_database_refresh != null)
        {
            timer_popup_server_syncing_database_refresh.cancel();
        }

        timer_popup_server_syncing_database_refresh = new Timer();

        timer_popup_server_syncing_database_refresh.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Server sync status timer fired. Sending Database status command to Gateway");

                portal_system_commands.sendGatewayCommand_getDatabaseStatus();
            }
        }, DATABASE_STATUS_UPDATE_INTERVAL, DATABASE_STATUS_UPDATE_INTERVAL);
    }


    private void stopPopupServerSyncingTimerAutoClose()
    {
        Log.d(TAG, "stopPopupServerSyncingTimerAutoClose");

        if(timer_popup_server_syncing_auto_close != null)
        {
            timer_popup_server_syncing_auto_close.cancel();

            timer_popup_server_syncing_auto_close.purge();
        }
    }


    private void stopPopupServerSyncingTimerDatabaseRefresh()
    {
        Log.d(TAG, "stopPopupServerSyncingTimerDatabaseRefresh");

        if(timer_popup_server_syncing_database_refresh != null)
        {
            timer_popup_server_syncing_database_refresh.cancel();

            timer_popup_server_syncing_database_refresh.purge();
        }
    }


    public void showDeveloperPopup()
    {
        if((popup_developer_options.getDialog() == null) || (!popup_developer_options.getDialog().isShowing()))	// This is safe, because if the sync_status is null, using || the second statement won't be evaluated.
        {
            popup_developer_options.show(getSupportFragmentManager(), "");
        }
        else
        {
            Log.d(TAG, "showDeveloperPopup : popup already showing");
        }
    }


    public void showWifiStatusPopup(boolean allow_reconnect)
    {
        if((popup_wifi_status.getDialog() == null) || (!popup_wifi_status.getDialog().isShowing()))	// This is safe, because if the sync_status is null, using || the second statement won't be evaluated.
        {
            if(wifi_status.mWifiErrorStatus == WifiErrorStatus.ADMIN_WIFI_DISABLED)
            {
                Log.d(TAG, "showWifiStatusPopup : wifi status is disabled from Admin mode.");
            }
            else
            {
                popup_wifi_status.showReconnectButton(allow_reconnect);
                popup_wifi_status.show(getSupportFragmentManager(), "");
                startWifiStatus_popupCountDownTimer();
            }
        }
        else
        {
            Log.d(TAG, "showWifiStatusPopup : popup already showing");
        }
    }

    private void updateWifiStatusInPopup()
    {
        if(popup_wifi_status.getDialog() != null)
        {
            if(popup_wifi_status.getDialog().isShowing())
            {
                popup_wifi_status.updateWifiEnabledStatus(wifi_status.hardware_enabled);
                popup_wifi_status.updateWifiConnectedToSSIDStatus(wifi_status.connected_to_ssid);
                popup_wifi_status.updateWifiSSID(wifi_status.ssid);
                popup_wifi_status.updateWifiIPAddress(wifi_status.ip_address_string);
                popup_wifi_status.updateWifiLevel(wifi_status.wifi_level);
                popup_wifi_status.updateWifiStatus(wifi_status.wifi_connection_status);
                popup_wifi_status.updateWifiBSSID(wifi_status.wifi_BSSID);
                popup_wifi_status.updateWifiConnectionFailureReason(getWifiFailureReasonString(wifi_status.mWifiErrorStatus));

                popup_wifi_status.enableWifiIPAddress(features_enabled.show_ip_address_on_wifi_popup);
            }
            else
            {
                Log.d(TAG, "updateWifiStatusInPopup : popup is not showing");
            }
        }
        else
        {
            Log.d(TAG, "updateWifiStatusInPopup : popup is null");
        }
    }

    private final long TIMEOUT_WIFI_STATUS_POPUP = (int)DateUtils.MINUTE_IN_MILLIS;
    private final PopupWifiStatusCountDownTimer mPopupWifiStatusCountDownTimer = new PopupWifiStatusCountDownTimer(TIMEOUT_WIFI_STATUS_POPUP, DateUtils.SECOND_IN_MILLIS, this);

    private void startWifiStatus_popupCountDownTimer()
    {
        Log.d(TAG, "Starting wifi status Popup countdown timer. Start time = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(getNtpTimeNowInMilliseconds()));

        checkAndStart_countDownTimer(mPopupWifiStatusCountDownTimer);
    }

    private void stopWifiStatus_popupCountDownTimer()
    {
        Log.d(TAG, "Stopping wifi status Popup countdown timer. Stop time = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(getNtpTimeNowInMilliseconds()));
        checkAndCancel_countDownTimer(mPopupWifiStatusCountDownTimer);
    }


    private String getWifiFailureReasonString(WifiErrorStatus wifi_error_status)
    {
        switch (wifi_error_status)
        {
            case NO_ERROR:
            {
                return this.getResources().getString(R.string.wifi_failure_reason_none);
            }

            case NOT_CONFIGURED_ERROR:
            {
                return this.getResources().getString(R.string.wifi_failure_reason_NotConfigured);
            }

            case AUTHENTICATION_ERROR:
            {
                return this.getResources().getString(R.string.wifi_failure_reason_AuthenticationError);
            }

            case UNKNOWN_SSID_ERROR:
            {
                return this.getResources().getString(R.string.wifi_failure_reason_UnknownSSID);
            }

            case ADMIN_WIFI_DISABLED:
            {
                return this.getResources().getString(R.string.wifi_disabled_in_admin_page);
            }
        }

        return this.getResources().getString(R.string.wifi_failure_reason_Unknown);
    }


    public int getGmtOffsetInMilliseconds()
    {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        //return mTimeZone.getRawOffset();                                                          // Get the number of milliseconds offset of the Timezone
        return mTimeZone.getOffset(getNtpTimeNowInMilliseconds());                                     // Get the number of milliseconds offset of the Timezone AND Daylight Savings Time (if needed)
    }

    /*

    String display_name;
    String units_for_keypad_entered;
    List<ButtonInfo>
        ButtonInfo  DrawableName
                    Button String
                    RHS Description String



                    "Confusion", vital_sign_id, button_id++, Color.BLACK, button_red, "Description A")
    */



    private final ArrayList<ManualVitalSignInfo> manual_vital_sign_infos = new ArrayList<>();

    private ManualVitalSignInfo getManualVitalSignInfo(int vital_sign_id)
    {
        for (ManualVitalSignInfo manual_vital_sign_info : manual_vital_sign_infos)
        {
            if (manual_vital_sign_info.vital_sign_id == vital_sign_id)
            {
                return manual_vital_sign_info;
            }
        }

        return null;
    }


    // The order here is the order of the list on FragmentManualVitalSignsSelectionList
    private void createManualVitalSignInfos()
    {
        manual_vital_sign_infos.clear();

        ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetails = patient_info.getThresholdSetAgeBlockDetails();

        ManualVitalSignInfo manual_vital_sign_info;

        // MANUALLY_ENTERED_RESPIRATION_RATE
        manual_vital_sign_info = new ManualVitalSignInfo();
        manual_vital_sign_info.vital_sign_id = VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE.ordinal();
        manual_vital_sign_info.vital_sign_display_name = getResources().getString(R.string.respiratory_rate);
        manual_vital_sign_info.entry_type = ObservationSetEntryType.KEYPAD;
        manual_vital_sign_info.units_display_string = getRespirationRateUnits();
        manual_vital_sign_info.show_rhs_description = false;
        manual_vital_sign_info.please_enter_the_xxxx = getResources().getString(R.string.please_enter_the_respiration_rate);
        manual_vital_sign_infos.add(manual_vital_sign_info);

        // MANUALLY_ENTERED_SPO2
        manual_vital_sign_info = new ManualVitalSignInfo();
        manual_vital_sign_info.vital_sign_id = VitalSignType.MANUALLY_ENTERED_SPO2.ordinal();
        manual_vital_sign_info.vital_sign_display_name = getResources().getString(R.string.spo2);
        manual_vital_sign_info.entry_type = ObservationSetEntryType.KEYPAD;
        manual_vital_sign_info.units_display_string = getSpO2Units();
        manual_vital_sign_info.show_rhs_description = false;
        manual_vital_sign_info.please_enter_the_xxxx = getResources().getString(R.string.please_enter_the_spo2);
        manual_vital_sign_infos.add(manual_vital_sign_info);

        //MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN
        if (thresholdSetAgeBlockDetails != null)
        {
//Move this and Server over to using VitalSignType as the type
            ArrayList<ThresholdSetColour> thresholdSetColours = thresholdSetAgeBlockDetails.list_of_threshold_set_colours;
            ArrayList<ThresholdSetLevel> thresholdSetLevels = thresholdSetAgeBlockDetails.getThresholdSetLevelForMeasurementType(MeasurementTypes.SUPPLEMENTAL_OXYGEN);
            if (thresholdSetLevels.size() > 0)
            {
                String vital_sign_display_name = getResources().getString(R.string.supplemental_oxygen);
                String please_enter_the_xxxx = getResources().getString(R.string.please_enter_the_supplemental_oxygen);

                manual_vital_sign_info = makeButtonSelectionManualVitalSign(thresholdSetColours, thresholdSetLevels, VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN, vital_sign_display_name, please_enter_the_xxxx);
                manual_vital_sign_infos.add(manual_vital_sign_info);
            }
        }

        // MANUALLY_ENTERED_RESPIRATION_DISTRESS
        if (thresholdSetAgeBlockDetails != null)
        {
//Move this and Server over to using VitalSignType as the type
            ArrayList<ThresholdSetColour> thresholdSetColours = thresholdSetAgeBlockDetails.list_of_threshold_set_colours;
            ArrayList<ThresholdSetLevel> thresholdSetLevels = thresholdSetAgeBlockDetails.getThresholdSetLevelForMeasurementType(MeasurementTypes.RESPIRATION_DISTRESS);
            if (thresholdSetLevels.size() > 0)
            {
                String vital_sign_display_name = getResources().getString(R.string.respiration_distress);
                String please_enter_the_xxxx = getResources().getString(R.string.please_select_the_respiration_distress_level);

                manual_vital_sign_info = makeButtonSelectionManualVitalSign(thresholdSetColours, thresholdSetLevels, VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS, vital_sign_display_name, please_enter_the_xxxx);
                manual_vital_sign_infos.add(manual_vital_sign_info);
            }
        }

        // MANUALLY_ENTERED_HEART_RATE
        manual_vital_sign_info = new ManualVitalSignInfo();
        manual_vital_sign_info.vital_sign_id = VitalSignType.MANUALLY_ENTERED_HEART_RATE.ordinal();
        manual_vital_sign_info.vital_sign_display_name = getResources().getString(R.string.heart_rate);
        manual_vital_sign_info.entry_type = ObservationSetEntryType.KEYPAD;
        manual_vital_sign_info.units_display_string = getHeartRateUnits();
        manual_vital_sign_info.show_rhs_description = false;
        manual_vital_sign_info.please_enter_the_xxxx = getResources().getString(R.string.please_enter_the_heart_rate);
        manual_vital_sign_infos.add(manual_vital_sign_info);

        // MANUALLY_ENTERED_BLOOD_PRESSURE
        manual_vital_sign_info = new ManualVitalSignInfo();
        manual_vital_sign_info.vital_sign_id = VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE.ordinal();
        manual_vital_sign_info.vital_sign_display_name = getResources().getString(R.string.blood_pressure);
        manual_vital_sign_info.entry_type = ObservationSetEntryType.KEYPAD;
        manual_vital_sign_info.units_display_string = "";
        manual_vital_sign_info.show_rhs_description = false;
        manual_vital_sign_info.please_enter_the_xxxx = getResources().getString(R.string.please_enter_the_blood_pressure);
        manual_vital_sign_infos.add(manual_vital_sign_info);

        // MANUALLY_ENTERED_WEIGHT
        manual_vital_sign_info = new ManualVitalSignInfo();
        manual_vital_sign_info.vital_sign_id = VitalSignType.MANUALLY_ENTERED_WEIGHT.ordinal();
        manual_vital_sign_info.vital_sign_display_name = getResources().getString(R.string.weight);
        manual_vital_sign_info.entry_type = ObservationSetEntryType.KEYPAD;
        manual_vital_sign_info.units_display_string = "";
        manual_vital_sign_info.show_rhs_description = false;
        manual_vital_sign_info.please_enter_the_xxxx = getResources().getString(R.string.please_enter_the_weight);
        manual_vital_sign_infos.add(manual_vital_sign_info);

        //MANUALLY_ENTERED_CAPILLARY_REFILL_TIME
        if (thresholdSetAgeBlockDetails != null)
        {
//Move this and Server over to using VitalSignType as the type
            ArrayList<ThresholdSetColour> thresholdSetColours = thresholdSetAgeBlockDetails.list_of_threshold_set_colours;
            ArrayList<ThresholdSetLevel> thresholdSetLevels = thresholdSetAgeBlockDetails.getThresholdSetLevelForMeasurementType(MeasurementTypes.CAPILLARY_REFILL_TIME);
            if (thresholdSetLevels.size() > 0)
            {
                String vital_sign_display_name = getResources().getString(R.string.capillary_refill_time);
                String please_enter_the_xxxx = getResources().getString(R.string.please_select_the_capillary_refill_time);

                manual_vital_sign_info = makeButtonSelectionManualVitalSign(thresholdSetColours, thresholdSetLevels, VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, vital_sign_display_name, please_enter_the_xxxx);
                manual_vital_sign_infos.add(manual_vital_sign_info);
            }
        }

        // MANUALLY_ENTERED_TEMPERATURE
        manual_vital_sign_info = new ManualVitalSignInfo();
        manual_vital_sign_info.vital_sign_id = VitalSignType.MANUALLY_ENTERED_TEMPERATURE.ordinal();
        manual_vital_sign_info.vital_sign_display_name = getResources().getString(R.string.temperature);
        manual_vital_sign_info.entry_type = ObservationSetEntryType.KEYPAD;
        manual_vital_sign_info.units_display_string = getTemperatureUnits();
        manual_vital_sign_info.show_rhs_description = false;
        manual_vital_sign_info.please_enter_the_xxxx = getResources().getString(R.string.please_enter_the_temperature);
        manual_vital_sign_infos.add(manual_vital_sign_info);

        //MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL
        if (thresholdSetAgeBlockDetails != null)
        {
//Move this and Server over to using VitalSignType as the type
            ArrayList<ThresholdSetColour> thresholdSetColours = thresholdSetAgeBlockDetails.list_of_threshold_set_colours;
            ArrayList<ThresholdSetLevel> thresholdSetLevels = thresholdSetAgeBlockDetails.getThresholdSetLevelForMeasurementType(MeasurementTypes.CONSCIOUSNESS_LEVEL);
            if (thresholdSetLevels.size() > 0)
            {
                String vital_sign_display_name = getResources().getString(R.string.consciousness_level);
                String please_enter_the_xxxx = getResources().getString(R.string.please_enter_the_consciousness_level);

                manual_vital_sign_info = makeButtonSelectionManualVitalSign(thresholdSetColours, thresholdSetLevels, VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, vital_sign_display_name, please_enter_the_xxxx);
                manual_vital_sign_infos.add(manual_vital_sign_info);
            }
        }

        //FAMILY_OR_NURSE_CONCERN
        if (thresholdSetAgeBlockDetails != null)
        {
//Move this and Server over to using VitalSignType as the type
            ArrayList<ThresholdSetColour> thresholdSetColours = thresholdSetAgeBlockDetails.list_of_threshold_set_colours;
            ArrayList<ThresholdSetLevel> thresholdSetLevels = thresholdSetAgeBlockDetails.getThresholdSetLevelForMeasurementType(MeasurementTypes.FAMILY_OR_NURSE_CONCERN);
            if (thresholdSetLevels.size() > 0)
            {
                String vital_sign_display_name = getResources().getString(R.string.family_or_nurse_concern);
                String please_enter_the_xxxx = getResources().getString(R.string.please_select_if_there_is_family_or_nurse_concern);

                manual_vital_sign_info = makeButtonSelectionManualVitalSign(thresholdSetColours, thresholdSetLevels, VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, vital_sign_display_name, please_enter_the_xxxx);
                manual_vital_sign_infos.add(manual_vital_sign_info);
            }
        }


        // MANUALLY_ENTERED_URINE_OUTPUT
        manual_vital_sign_info = new ManualVitalSignInfo();
        manual_vital_sign_info.vital_sign_id = VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT.ordinal();
        manual_vital_sign_info.vital_sign_display_name = getResources().getString(R.string.urine_output);
        manual_vital_sign_info.entry_type = ObservationSetEntryType.KEYPAD;
        manual_vital_sign_info.units_display_string = getUrineOutputUnits();
        manual_vital_sign_info.show_rhs_description = false;
        manual_vital_sign_info.please_enter_the_xxxx = getResources().getString(R.string.please_enter_the_urine_output);
        manual_vital_sign_infos.add(manual_vital_sign_info);
    }


    private ManualVitalSignInfo makeButtonSelectionManualVitalSign(ArrayList<ThresholdSetColour> thresholdSetColours, ArrayList<ThresholdSetLevel> thresholdSetLevels, VitalSignType vitalSignType, String vital_sign_display_name, String please_enter_the_xxxx)
    {
        ManualVitalSignInfo manual_vital_sign_info = new ManualVitalSignInfo();
        manual_vital_sign_info.vital_sign_id = vitalSignType.ordinal();
        manual_vital_sign_info.vital_sign_display_name = vital_sign_display_name;
        manual_vital_sign_info.entry_type = ObservationSetEntryType.BUTTON_SELECTION;
        manual_vital_sign_info.units_display_string = "";
        manual_vital_sign_info.please_enter_the_xxxx = please_enter_the_xxxx;

        ArrayList<ManualVitalSignButtonDescriptor> vital_sign_button_descriptors = new ArrayList<>();
        int button_id = 0;

        boolean show_rhs_description = false;

        for (ThresholdSetLevel thresholdSetLevel : thresholdSetLevels)
        {
            // Get the text to display in the button
            String display_text = thresholdSetLevel.display_text;

            // Get the text to show on the RHS
            String information_text = thresholdSetLevel.information_text;
            if (information_text.isEmpty() == false)
            {
                // If its not blank then we have to text to show on the RHS so resize the buttons
                show_rhs_description = true;
            }

            // Get the EWS score
            int ews_score = thresholdSetLevel.early_warning_score;

            DrawableAndBackgroundAndTextColour buttonAndTextColour = getButtonAndTextColourFromEwsScore(ews_score, thresholdSetColours);

            vital_sign_button_descriptors.add(new ManualVitalSignButtonDescriptor(display_text, manual_vital_sign_info.vital_sign_id, button_id++, ews_score, buttonAndTextColour.text_colour, buttonAndTextColour.shape, information_text));
        }

        manual_vital_sign_info.button_info = vital_sign_button_descriptors;
        manual_vital_sign_info.show_rhs_description = show_rhs_description;
        return manual_vital_sign_info;
    }


    static class BackgroundAndTextColour
    {
        final int background_colour;
        final int text_colour;

        BackgroundAndTextColour(int background_colour, int text_colour)
        {
            this.background_colour = background_colour;
            this.text_colour = text_colour;
        }
    }


    static class DrawableAndBackgroundAndTextColour extends BackgroundAndTextColour
    {
        final Drawable shape;

        DrawableAndBackgroundAndTextColour(Drawable shape, BackgroundAndTextColour backgroundAndTextColour)
        {
            super(backgroundAndTextColour.background_colour, backgroundAndTextColour.text_colour);
            this.shape = shape;
        }
    }


    // Create a Drawable that looks like the Isansys buttons but with a custom colour
    private Drawable createButtonDrawable(int colour)
    {
        /*
        Code equiv of the button_xxx.xml files

        <shape
            xmlns:android="http://schemas.android.com/apk/res/android"
            android:shape="rectangle"
            >
            <corners
                android:radius="30dp"
                />
            <stroke
                android:width="3px"
                android:color="@color/black"
                />
            <solid
                android:color="@color/red"
                />
        </shape>
        */

        Resources r = getResources();
        float corner_pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, r.getDisplayMetrics());
        float stroke_pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, r.getDisplayMetrics());

        // Using a GradientDrawable as it seems to be the only one where the corner radius can be set
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{colour, colour});
        gd.setStroke((int)stroke_pixels, Color.BLACK);
        gd.setCornerRadius((int)corner_pixels);

        return gd;
    }


    // Create a Drawable that looks like the Circle_xxx drawables but with a custom colour
    private Drawable createCircleDrawable(int colour)
    {
        /*
        Code equiv of the circle_xxx.xml files

        <shape
            xmlns:android="http://schemas.android.com/apk/res/android"
            android:shape="oval">

            <solid
               android:color="@color/green"
               />

            <stroke
                android:width="1px"
                android:color="@color/black"
                />

        </shape>
        */

        Resources r = getResources();
        float corner_pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, r.getDisplayMetrics());
        float stroke_pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, r.getDisplayMetrics());

        // Using a GradientDrawable as it seems to be the only one where the corner radius can be set
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{colour, colour});
        gd.setStroke((int)stroke_pixels, Color.BLACK);
        gd.setCornerRadius((int)corner_pixels);

        return gd;
    }


    private BackgroundAndTextColour getBackAndTextColourForEwsScoreFromThresholds(int ews_score, ArrayList<ThresholdSetColour> thresholdSetColours)
    {
        // Assume Error by default
        int background_colour = ContextCompat.getColor(this, R.color.gray);
        int text_colour = ContextCompat.getColor(this, R.color.early_warning_text__white);

        // The colours in the Threshold file are RGB. Android expects an opacity value. So this needs to be added to each of the colours
        int force_solid_colour = 0xFF000000;

        for (ThresholdSetColour thresholdSetColour : thresholdSetColours)
        {
            if (thresholdSetColour.score == ews_score)
            {
                background_colour = thresholdSetColour.colour + force_solid_colour;
                text_colour = thresholdSetColour.text_colour + force_solid_colour;
                break;
            }
        }

        // Negative numbers are a special case.
        // These Thresholds are ONLY for colour codes - NOT EWS VALUES.
        // These will be IGNORED in the EWS processor
        if (ews_score == -1)
        {
            background_colour = ContextCompat.getColor(this, R.color.blue);
            text_colour = ContextCompat.getColor(this, R.color.early_warning_text__white);
        }

        return new BackgroundAndTextColour(background_colour, text_colour);
    }


    private DrawableAndBackgroundAndTextColour getButtonAndTextColourFromEwsScore(int ews_score, ArrayList<ThresholdSetColour> thresholdSetColours)
    {
        BackgroundAndTextColour backgroundAndTextColour = getBackAndTextColourForEwsScoreFromThresholds(ews_score, thresholdSetColours);

        Drawable button = createButtonDrawable(backgroundAndTextColour.background_colour);

        return new DrawableAndBackgroundAndTextColour(button, backgroundAndTextColour);
    }


    private DrawableAndBackgroundAndTextColour getCircleAndTextColourFromEwsScore(int ews_score, ArrayList<ThresholdSetColour> thresholdSetColours)
    {
        BackgroundAndTextColour backgroundAndTextColour = getBackAndTextColourForEwsScoreFromThresholds(ews_score, thresholdSetColours);

        Drawable circle = createCircleDrawable(backgroundAndTextColour.background_colour);

        return new DrawableAndBackgroundAndTextColour(circle, backgroundAndTextColour);
    }


    public Drawable getDrawableCircle(int ews_score)
    {
        return getCircleAndTextColourFromEwsScore(ews_score, patient_info.getThresholdSetAgeBlockDetails().list_of_threshold_set_colours).shape;
    }


    private ManuallyEnteredVitalSignDescriptor createNewManuallyEnteredVitalSignsDescriptor(int vital_sign_id, String description_string, ObservationSetEntryType observation_set_entry_type)
    {
        VitalSignType vital_sign_type = VitalSignType.values()[vital_sign_id];

        if (measurement_cache.getCacheSize(vital_sign_type) > 0)
        {
            return new ManuallyEnteredVitalSignDescriptor(description_string, vital_sign_type.ordinal(), observation_set_entry_type);
        }
        else if (observation_set_being_entered.getMeasurements().size() > 0)
        {
            // If we are entering an observation set, there may be measurements that have been entered but as the observation set is not complete, these
            // have not been written to the database - and are therefore NOT in the cache

            // Check if there is a measurement of this vital_sign_type in the list.......
            ManualVitalSignBeingEntered measurement = getManualVitalSignBeingEntered(vital_sign_id);
            if (measurement != null)
            {
                ManualVitalSignInfo manual_vital_sign_info = getManualVitalSignInfo(vital_sign_id);
                if (manual_vital_sign_info != null)
                {
                    return new ManuallyEnteredVitalSignDescriptor(description_string, vital_sign_id, observation_set_entry_type);
                }
            }
        }

        return new ManuallyEnteredVitalSignDescriptor(description_string, vital_sign_id, observation_set_entry_type);
    }


    public ArrayList<ManuallyEnteredVitalSignDescriptor> getManuallyEnteredVitalSigns()
    {
        ArrayList<ManuallyEnteredVitalSignDescriptor> manually_entered_vital_signs = new ArrayList<>();

        for (ManualVitalSignInfo manual_vital_sign_info : manual_vital_sign_infos)
        {
            manually_entered_vital_signs.add(createNewManuallyEnteredVitalSignsDescriptor(manual_vital_sign_info.vital_sign_id, manual_vital_sign_info.vital_sign_display_name, manual_vital_sign_info.entry_type));
        }

        return manually_entered_vital_signs;
    }


    public ArrayList<ManualVitalSignButtonDescriptor> getManualVitalSignButtons(int vital_sign_id)
    {
        ManualVitalSignInfo manual_vital_sign_info = getManualVitalSignInfo(vital_sign_id);
        if (manual_vital_sign_info != null)
        {
            return manual_vital_sign_info.button_info;
        }

        return new ArrayList<>();
    }


    public ArrayList<VitalSignValidityTimeDescriptor> getVitalSignValidityTimes()
    {
        ArrayList<VitalSignValidityTimeDescriptor> validity_times = new ArrayList<>();

        int seconds_in_minute = 60;
        int hour_in_seconds = 60 * seconds_in_minute;

        validity_times.add(new VitalSignValidityTimeDescriptor(getResources().getString(R.string.five_minutes), 5 * seconds_in_minute));
        validity_times.add(new VitalSignValidityTimeDescriptor(getResources().getString(R.string.ten_minutes), 10 * seconds_in_minute));
        validity_times.add(new VitalSignValidityTimeDescriptor(getResources().getString(R.string.fifteen_minutes), 15 * seconds_in_minute));
        validity_times.add(new VitalSignValidityTimeDescriptor(getResources().getString(R.string.thirty_minutes), 30 * seconds_in_minute));
        validity_times.add(new VitalSignValidityTimeDescriptor(getResources().getString(R.string.text1Hour), hour_in_seconds));
        validity_times.add(new VitalSignValidityTimeDescriptor(getResources().getString(R.string.text2Hours), 2 * hour_in_seconds));
        validity_times.add(new VitalSignValidityTimeDescriptor(getResources().getString(R.string.text4Hours), 4 * hour_in_seconds));
        validity_times.add(new VitalSignValidityTimeDescriptor(getResources().getString(R.string.text8Hours), 8 * hour_in_seconds));
        validity_times.add(new VitalSignValidityTimeDescriptor(getResources().getString(R.string.text12Hours), 12 * hour_in_seconds));

        return validity_times;
    }


    public void restartGatewayApp()
    {
        Log.d(TAG, "restartGatewayApp");

        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.isansys.patientgateway");
        boolean gateway_already_started = false;

        if (LaunchIntent != null)
        {
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService( ACTIVITY_SERVICE );
            List<RunningAppProcessInfo> processInfo = activityManager.getRunningAppProcesses();
            int processInfo_size = processInfo.size();
            for(int i = 0; i < processInfo_size; i++)
            {
                if(processInfo.get(i).processName.equals("com.isansys.patientgateway"))
                {
                    Log.e(TAG, "restartGatewayApp : Gateway is already launched");
                    gateway_already_started = true;
                }
            }

            if(gateway_already_started)
            {
                Log.w(TAG, "restartGatewayApp : Starting the already started app with killing it first");
            }
            else
            {
                Log.i(TAG, "restartGatewayApp : Launching the Gateway App");
            }

            startActivity(LaunchIntent);
        }
        else
        {
            Log.e(TAG, "restartGatewayApp : No Patient Gateway found");
        }

    }

    public String getLastPingTime()
    {
        return TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(getNtpTimeNowInMilliseconds() - SHOW_GATEWAY_NOT_RESPONDING_AFTER_TIME);
    }


    private void checkAndStart_countDownTimer(CountDownTimer counter_down_timer)
    {
        try
        {
            if (counter_down_timer != null)
            {
                counter_down_timer.cancel();

                counter_down_timer.start();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "checkAndStart_countDownTimer : Timer isn't created. Exception e = " + e);
        }
    }


    private void checkAndCancel_countDownTimer(CountDownTimer counter_down_timer)
    {
        try
        {
            if(counter_down_timer != null)
            {
                counter_down_timer.cancel();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "checkAndCancel_countDownTimer : timer is not cancelled. Exception e = " + e);
        }
    }


    public void checkAndCancel_timer(Timer mTimer)
    {
        try
        {
            if(mTimer != null)
            {
                mTimer.cancel();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "checkAndCancel_timer : timer is not cancelled. Exception e = " + e);
        }
    }


    public void checkAndCancel_timerTask(TimerTask mTimerTask)
    {
        try
        {
            if(mTimerTask != null)
            {
                mTimerTask.cancel();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,"check_and_cancel_timerTask : TimerTask exception e = " + e);
        }
    }


    private void outputDeviceInfo(String command, DeviceInfo device)
    {
        Log.w(TAG, command + UtilityFunctions.padDeviceType(device.device_type) +
                " : human_readable_device_id = " + UtilityFunctions.padHumanReadableDeviceId(device.human_readable_device_id) +
                " : bluetooth_address = " + UtilityFunctions.padBluetoothAddress(device.bluetooth_address) +
                " : device_name = " + device.device_name +
                " : actual_device_connection_status = " + device.getActualDeviceConnectionStatus() +
                " : measurement_interval = " + device.measurement_interval_in_seconds +
                " : setup_mode_time_in_seconds = " + device.setup_mode_time_in_seconds +
                " : show_on_ui = " + device.isDeviceTypePartOfPatientSession() +
                " : dummy_data_mode = " + device.dummy_data_mode +
                " : firmware_version = " + device.firmware_version+
                " : counter_leads_off_after_last_valid_data = " + device.counter_leads_off_after_last_valid_data+
                " : counter_total_leads_off = " + device.counter_total_leads_off +
                " : radio_type = " + device.radio_type +
                " : sensor_type = " + device.sensor_type
        );
    }


    public void setEnablePatientOrientation(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setPatientOrientationEnabledStatus(enabled);
    }


    public boolean getEnablePatientOrientation()
    {
        return features_enabled.patient_orientation;
    }


    public void setShowNumbersOnBatteryIndicator(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setShowNumbersOnBatteryIndicatorEnabledStatus(enabled);
    }


    public boolean getShowNumbersOnBatteryIndicator()
    {
        return features_enabled.show_numbers_of_battery_indicator;
    }


    private void displaySearchAgainButton(DeviceInfo device_info)
    {
        if (current_page == UserInterfacePage.DEVICE_CONNECTION)
        {
            FragmentDeviceConnection fragment = (FragmentDeviceConnection) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                Log.d(TAG, "displaySearchAgainButton : " + device_info.getSensorTypeAndDeviceTypeAsString());

                fragment.showDeviceSearchAgainButton(device_info);
            }
        }
    }


    private void sendWifiReconnectCommand()
    {
        portal_system_commands.sendGatewayCommand_wifiReconnection();
    }


    public void sendCommandToGetWifiStatus()
    {
        portal_system_commands.sendGatewayCommand_getWifiStatus();
    }


    public void setUnpluggedOverlayEnableStatus(boolean checked)
    {
        portal_system_commands.sendGatewayCommand_setUnpluggedOverlayEnableStatus(checked);
    }

    public void getUnpluggedOverlayEnabledStatusFromPatientGateway()
    {
        portal_system_commands.sendGatewayCommand_getUnpluggedOverlayEnabledStatus();
    }

    public void setLT3KHzSetupModeEnableStatus(boolean checked)
    {
        portal_system_commands.sendGatewayCommand_setLT3KHzSetupModeEnableStatus( checked);
    }

    public void getLT3KHzSetupModeEnabledStatusFromPatientGateway()
    {
        portal_system_commands.sendGatewayCommand_getLT3KHzSetupModeEnabledStatus();
    }

    public void setAutoAddEarlyWarningScoreEnableStatus(boolean checked)
    {
        portal_system_commands.sendGatewayCommand_setAutoAddEwsEnableStatus(checked);
    }

    public void getAutoAddEarlyWarningScoreEnableStatusFromPatientGateway()
    {
        portal_system_commands.sendGatewayCommand_getAutoAddEwsEnabledStatus();
    }


    public void setPredefinedAnnotationEnableStatus(boolean checked)
    {
        portal_system_commands.sendGatewayCommand_setPredefinedAnnotationEnabledStatus(checked);
    }

    public void getPredefinedAnnotationEnableStatus()
    {
        portal_system_commands.sendGatewayCommand_getPredefinedAnnotationEnabledStatus();
    }


    public void setDfuBootloaderEnableStatus(boolean checked)
    {
        portal_system_commands.sendGatewayCommand_setDfuBootloaderEnabledStatus(checked);
    }

    public void getDfuBootloaderEnableStatus()
    {
        portal_system_commands.sendGatewayCommand_getDfuBootloaderEnabledStatus();
    }


    public void setSpO2SpotMeasurementsEnableStatus(boolean checked)
    {
        portal_system_commands.sendGatewayCommand_setSpO2SpotMeasurementsEnableStatus(checked);
    }

    public void getSpO2SpotMeasurementsEnableStatus()
    {
        portal_system_commands.sendGatewayCommand_getSpo2SpotMeasurementsEnabledStatus();
    }


    public void setDisplayTemperatureInFahrenheitEnableStatus(boolean checked)
    {
        portal_system_commands.sendGatewayCommand_setDisplayTemperatureInFahrenheitEnabledStatus(checked);
    }

    public void getDisplayTemperatureInFahrenheitEnableStatus()
    {
        portal_system_commands.sendGatewayCommand_getDisplayTemperatureInFahrenheitEnabledStatus();
    }

    public void setDisplayWeightInLbsEnableStatus(boolean checked)
    {
        portal_system_commands.sendGatewayCommand_setDisplayWeightInLbsEnabledStatus(checked);
    }

    public void getDisplayWeightInLbsEnableStatus()
    {
        portal_system_commands.sendGatewayCommand_getDisplayWeightInLbsEnabledStatus();
    }

    public void getDiskFreePercentage()
    {
        portal_system_commands.sendGatewayCommand_getFreeDiskSpace();
    }


    public void drawTextBubbleOnGraph(Canvas canvas, Paint paint, float x, float y, String value, int background_colour)
    {
        paint.setAntiAlias(true);
        paint.setTextSize(35);
        paint.setStrokeWidth(3);

        // Calculate the size of the text
        Rect textBounds = new Rect();
        paint.getTextBounds(value, 0, value.length(), textBounds);
        int text_width = (int)paint.measureText(value);
        int text_height = textBounds.height();

        // Calculate where to draw the background filled rectangle
        int margin_between_text_and_border = 10;

        int left = (int)(x - (text_width / 2f)) - margin_between_text_and_border;
        int top = (int)(y - (text_height / 2f)) - margin_between_text_and_border;
        int right = (int)(x + (text_width / 2f)) + margin_between_text_and_border;
        int bottom = (int)(y + (text_height / 2f)) + margin_between_text_and_border;

        Rect rect = new Rect(left, top, right, bottom);

        RectF rectF = new RectF(rect);
        int cornersRadius = 20;

        // Draw the rectangle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(background_colour);
        canvas.drawRoundRect(rectF, cornersRadius, cornersRadius, paint);

        // Draw the border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rectF, cornersRadius, cornersRadius, paint);

        // Draw the measurement value
        canvas.drawText(value, x - (text_width / 2f), y + (text_height / 2f), paint);
    }


    public void setRunDevicesInTestMode(boolean checked)
    {
        Log.d(TAG, "RunDevicesInTestMode Enabled = " + checked);
        portal_system_commands.sendGatewayCommand_setRunDevicesInTestMode(checked);
    }


    public boolean getRunDevicesInTestModeEnableStatus()
    {
        Log.d(TAG, "getRunDevicesInTestModeEnableStatus = " + features_enabled.run_devices_in_test_mode);
        return features_enabled.run_devices_in_test_mode;
    }


    public void getBloodPressureLongTermMeasurementTimeout()
    {
        Log.d(TAG, "getBloodPressureLongTermMeasurementTimeout");

        portal_system_commands.sendGatewayCommand_getBloodPressureLongTermMeasurementTimeout();
    }


    public void setBloodPressureLongTermMeasurementTimeout(int timeout_in_minutes)
    {
        Log.d(TAG, "setBloodPressureLongTermMeasurementTimeout : timeout = " + timeout_in_minutes);

        portal_system_commands.sendGatewayCommand_setBloodPressureLongTermMeasurementTimeoutInMinutes(timeout_in_minutes);
    }


    public void getSpO2LongTermMeasurementTimeout()
    {
        Log.d(TAG, "getSpO2LongTermMeasurementTimeout");

        portal_system_commands.sendGatewayCommand_getSpO2LongTermMeasurementTimeout();
    }


    public void setSpO2LongTermMeasurementTimeout(int timeout_in_minutes)
    {
        Log.d(TAG, "setSpO2LongTermMeasurementTimeout : timeout = " + timeout_in_minutes);

        portal_system_commands.sendGatewayCommand_setSpO2LongTermMeasurementTimeoutInMinutes(timeout_in_minutes);
    }


    public void getWeightLongTermMeasurementTimeout()
    {
        Log.d(TAG, "getWeightLongTermMeasurementTimeout");

        portal_system_commands.sendGatewayCommand_getWeightLongTermMeasurementTimeout();
    }


    public void setWeightLongTermMeasurementTimeout(int timeout_in_minutes)
    {
        Log.d(TAG, "setWeightLongTermMeasurementTimeout : timeout = " + timeout_in_minutes);

        portal_system_commands.sendGatewayCommand_setWeightLongTermMeasurementTimeoutInMinutes(timeout_in_minutes);
    }


    public void getThirdPartyTemperatureLongTermMeasurementTimeout()
    {
        Log.d(TAG, "getThirdPartyTemperatureLongTermMeasurementTimeout");

        portal_system_commands.sendGatewayCommand_getThirdPartyTemperatureLongTermMeasurementTimeout();
    }


    public void setThirdPartyTemperatureLongTermMeasurementTimeout(int timeout_in_minutes)
    {
        Log.d(TAG, "setThirdPartyTemperatureLongTermMeasurementTimeout : timeout = " + timeout_in_minutes);

        portal_system_commands.sendGatewayCommand_setThirdPartyTemperatureLongTermMeasurementTimeoutInMinutes(timeout_in_minutes);
    }


    private void addOverlay(WindowManager windowManager, RelativeLayout overlay)
    {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        windowManager.addView(overlay, params);
    }


    private RelativeLayout night_mode_overlay;

    private void showNightModeOverlay(boolean show)
    {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        if (show)
        {
            if(night_mode_overlay == null)
            {
                night_mode_overlay = new RelativeLayout(this);
                night_mode_overlay.setBackgroundColor(0xA0000000); // The translucent black color

                addOverlay(windowManager, night_mode_overlay);
            }
        }
        else
        {
            if(night_mode_overlay != null)
            {
                windowManager.removeView(night_mode_overlay);

                night_mode_overlay = null;
            }
        }
    }


    private void enableNightModeInFooter(boolean enabled)
    {
        if(UtilityFunctions.isFragmentAddedAndResumed(footer_fragment))
        {
            footer_fragment.showNightModeEnabled(enabled);
        }
        else
        {
            Log.e(TAG, "enableNightModeInFooter : footer_fragment = null");
        }
    }


    public void enableDummyDataModeBackfillSessionWithData(boolean backfill)
    {
        portal_system_commands.sendGatewayCommand_enableDummyDataModeBackfillSessionWithData(backfill);
    }


    public void setDummyDataModeNumberOfHoursToBackfill(int hours)
    {
        portal_system_commands.sendGatewayCommand_setDummyDataModeBackfillHours(hours);
    }


    private int DEFAULT_VIEWPORT_SIZE_IN_MINUTES = 2 * 60;                                          // Default to showing 2 hours on the screen by default or when pressing Reset Zoom
    private final int MAX_VIEWPORT_SIZE_IN_MINUTES = 24 * 60;                                       // Do not allow the user to zoom out more than 24 hours - wont be able to see individual minutes on the graphs


    private void resetGraphViewport()
    {
        long viewport_end_time_in_ms = getNtpTimeNowInMilliseconds() - getSessionStartDate();

        long viewport_start_time_in_ms = viewport_end_time_in_ms - (DEFAULT_VIEWPORT_SIZE_IN_MINUTES * DateUtils.MINUTE_IN_MILLIS);

        graphAdjustViewport(viewport_start_time_in_ms, viewport_end_time_in_ms);

        showNextButton(false);
    }


    public int getMaxGraphViewportSizeInMinutes()
    {
        return MAX_VIEWPORT_SIZE_IN_MINUTES;
    }


    public int getDefaultGraphViewportSizeInMinutes()
    {
        return DEFAULT_VIEWPORT_SIZE_IN_MINUTES;
    }


    public void onGraphScroll(long minX, long maxX)
    {
        Log.e(TAG, "graphScroll : From " + TimestampConversion.convertDateToHumanReadableStringWeekdayMonthDayHoursMinutes(minX + getSessionStartDate()) + " to " + TimestampConversion.convertDateToHumanReadableStringWeekdayMonthDayHoursMinutes(maxX + getSessionStartDate()));

        graphAdjustViewport(minX, maxX);
    }


    public void onGraphScale(long minX, long maxX)
    {
        Log.e(TAG, "graphScale : From " + TimestampConversion.convertDateToHumanReadableStringWeekdayMonthDayHoursMinutes(minX + getSessionStartDate()) + " to " + TimestampConversion.convertDateToHumanReadableStringWeekdayMonthDayHoursMinutes(maxX + getSessionStartDate()) + " = " + ((maxX - minX) / (int)DateUtils.MINUTE_IN_MILLIS) + " minutes");

        graphViewFinalMinX = minX;
        graphViewFinalMaxX = maxX;

        graphAdjustViewport(minX, maxX);
    }


    private void graphAdjustViewport(long minX, long maxX)
    {
        if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
        {
            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment) && !scaling_handled_by_detector)
            {
                fragment.scrollOrScale(minX, maxX);
            }

            setNextButtonText(getResources().getString(R.string.resetZoom));
            showNextButton(true);
        }
        else
        {
            Log.w(TAG, "graphAdjustViewport: current page is no longer patient vitals display");
        }
    }


    /* This is used to get the system commands object from inside a fragment.
       Fragments can't be passed arguments into their constructors, so dependency injection must be done a different way.
       A function in their main_activity_interface can then get the system commands instance in (e.g.) onAttach()
     */
    public SystemCommands getSystemCommands()
    {
        return portal_system_commands;
    }


    public void setManufacturingModeEnabled(boolean enabled)
    {
        if (enabled)
        {
            DEFAULT_VIEWPORT_SIZE_IN_MINUTES = 5;                                       // Show 5 minutes of data on the screen
        }
        else
        {
            DEFAULT_VIEWPORT_SIZE_IN_MINUTES = 2 * 60;                                  // Show 2 hours on the screen by default or when pressing Reset Zoom
        }

        portal_system_commands.sendGatewayCommand_setManufacturingModeEnabledStatus(enabled);
    }


    public boolean getManufacturingModeEnabled()
    {
        return features_enabled.manufacturing_mode;
    }


    public void setVideoCallsEnabled(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setVideoCallsEnabledStatus(enabled);
    }

    public boolean getVideoCallsEnabled()
    {
        return features_enabled.video_calls_enabled;
    }


    public void setViewWebPagesEnabled(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setViewWebPagesEnabledStatus(enabled);
    }

    public boolean getViewWebPagesEnabled()
    {
        return features_enabled.view_webpages_enabled;
    }


    public boolean isShowTemperatureInFahrenheitEnabled()
    {
        return features_enabled.show_temperature_in_fahrenheit;
    }

    public boolean isShowWeightInLbsEnabled()
    {        
        return features_enabled.show_weight_in_lbs;
    }

    public void setUseBackCameraEnabled(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setUseBackCameraEnabledStatus(enabled);
    }


    public boolean getUseBackCameraEnabled()
    {
        return features_enabled.use_back_camera;
    }


    public void setLifetempMeasurementInterval(int measurement_interval_in_seconds)
    {
        lifetemp_measurement_interval_in_seconds = measurement_interval_in_seconds;
        portal_system_commands.sendGatewayCommand_setLifetempMeasurementIntervalInSeconds(measurement_interval_in_seconds);
    }


    public int getLifetempMeasurementInterval()
    {
        return lifetemp_measurement_interval_in_seconds;
    }


    public void setPatientOrientationMeasurementInterval(int measurement_interval_in_seconds)
    {
        patient_orientation_measurement_interval_in_seconds = measurement_interval_in_seconds;
        portal_system_commands.sendGatewayCommand_setPatientOrientationMeasurementIntervalInSeconds(measurement_interval_in_seconds);
    }


    public int getPatientOrientationMeasurementInterval()
    {
        return patient_orientation_measurement_interval_in_seconds;
    }


    public void manualVitalsOnlySelected()
    {
        if (isSessionInProgress())
        {
            portal_system_commands.sendGatewayCommand_updateExistingSessionCommand(gateway_user_id);
        }
        else
        {
            createNewSession();
        }

        observationSetEntrySelected();
    }


    public boolean useFrontCamera()
    {
        return !features_enabled.use_back_camera;
    }


    public void getPatientNameFromPatientId(String hospital_patient_id)
    {
        Log.w(TAG, "getPatientNameFromPatientId : " + hospital_patient_id);

        showPatientNamePopup();

        portal_system_commands.sendGatewayCommand_getPatientNameFromHospitalPatientId(hospital_patient_id);
    }


    private void showPatientNamePopup()
    {
        if((popup_patient_name.getDialog() == null) || (!popup_patient_name.getDialog().isShowing()))	// This is safe, because if the sync_status is null, using || the second statement won't be evaluated.
        {
            popup_patient_name.show(getSupportFragmentManager(), "");
        }
        else
        {
            Log.d(TAG, "showPatientNamePopup : popup already showing");
        }
    }


    public void forceDismissPatientNamePopup()
    {
        popup_patient_name.dismissPopupIfVisible();
    }


    private void showRecyclingReminder()
    {
        if((popup_recycling_reminder.getDialog() == null) || (!popup_recycling_reminder.getDialog().isShowing()))
        {
            popup_recycling_reminder.show(getSupportFragmentManager(), "");
        }
        else
        {
            Log.d(TAG, "showRecyclingReminder : popup already showing");
        }
    }


    public void forceDismissRecyclingReminderPopup()
    {
        popup_recycling_reminder.dismissPopupIfVisible();
    }


    private void showBluetoothError()
    {
        if((popup_bluetooth_error.getDialog() == null) || (!popup_bluetooth_error.getDialog().isShowing()))
        {
            popup_bluetooth_error.show(getSupportFragmentManager(), "");
        }
        else
        {
            Log.d(TAG, "showBluetoothError : popup already showing");
        }
    }


    public void forceDismissBluetoothErrorPopup()
    {
        popup_bluetooth_error.dismissPopupIfVisible();
    }


    private void showRemoveFromEwsPopup(SensorType sensor_type)
    {
        // Disable the screen timeout here until the user selects either Remove or Keep from the popup
        stopScreenLockCountdownTimer();

        PopupRemoveFromEws popup_remove_from_ews = new PopupRemoveFromEws(this, sensor_type);
        popup_remove_from_ews.setArguments(new PopupRemoveFromEws.Callback()
        {
            @Override
            public void touchEventFromPopupWindow()
            {
                Log.d(TAG, "Main Activity : touchEventFromPopupWindow Remove From EWS popup");
                screen_dimming_countdown_timer.touchEvent();
            }

            @Override
            public void removeButtonPressed(SensorType sensor_type)
            {
                Log.d(TAG, "Main Activity: removeButtonPressed in Remove From EWS Popup");

                // Remove from EWS as well as remove the device
                removeVitalSignTypesFromEWS(sensor_type);

                // Remove the device
                removeDeviceFromGatewayAndClearDesiredDeviceInPatientGateway(sensor_type);

                // Enable screen timeout again
                stopScreenLockCountdownTimerAndRestartIfDesired();
            }

            @Override
            public void keepButtonPressed(SensorType sensor_type)
            {
                Log.d(TAG, "Main Activity: keepButtonPressed in Remove From EWS Popup");

                // Do NOT remove from EWS. Just remove the device
                removeDeviceFromGatewayAndClearDesiredDeviceInPatientGateway(sensor_type);

                // Enable screen timeout again
                stopScreenLockCountdownTimerAndRestartIfDesired();
            }
        });

        popup_remove_from_ews.show(getSupportFragmentManager(), "");
    }


    private void removeVitalSignTypesFromEWS(SensorType sensor_type)
    {
        VitalSignType[] vitalSignTypes = VitalSignType.getVitalSignTypesFromSensorType(sensor_type);

        Log.d(TAG, "Main Activity: removeButtonPressed in Remove From EWS Popup : Removing " + vitalSignTypes.length + " vital sign types");

        for (VitalSignType vitalSignType : vitalSignTypes)
        {
            Log.d(TAG, "Main Activity: removeButtonPressed in Remove From EWS Popup : Removing vital sign type = " + vitalSignType);
            portal_system_commands.sendGatewayCommand_removeVitalSignTypeFromEws(vitalSignType);
        }
    }


    private void stopScreenLockCountdownTimerAndRestartIfDesired()
    {
        Log.d(TAG, "stopScreenLockCountdownTimerAndRestartIfDesired : " + current_page);

        stopScreenLockCountdownTimer();

        // Only start the Timeout if not on the Unlock Screen, Device Connection, Dummy Data Mode or the Patient Vitals Display.....
        if (    (current_page != UserInterfacePage.UNLOCK_SCREEN) &&
                (current_page != UserInterfacePage.DEVICE_CONNECTION) &&
                (current_page != UserInterfacePage.DUMMY_DATA_MODE) &&
                (current_page != UserInterfacePage.INSTALLATION_MODE_PROGRESS) &&
                (current_page != UserInterfacePage.INSTALLATION_MODE_SERVER_ADDRESS_SCAN) &&
                (current_page != UserInterfacePage.INSTALLATION_MODE_WELCOME) &&
                (current_page != UserInterfacePage.ANDROID_PERMISSIONS) &&
                (current_page != UserInterfacePage.EMPTY) &&                            // If the Empty fragment is shown, then a Video call is in progress
                (current_page != UserInterfacePage.SOFTWARE_UPDATE_IN_PROGRESS)
                )
        {
            // Set amount of time before timeout
            if (current_page == UserInterfacePage.PATIENT_VITALS_DISPLAY)
            {
                if (display_timeout_length_in_seconds > 0 && display_timeout_applies_to_patient_vitals)
                {
                    screen_lock_timer_timeout_in_milliseconds = display_timeout_length_in_seconds * DateUtils.SECOND_IN_MILLIS;
                }
                else
                {
                    screen_lock_timer_timeout_in_milliseconds = 0;
                }
            }
            else
            {
                screen_lock_timer_timeout_in_milliseconds = display_timeout_length_in_seconds * DateUtils.SECOND_IN_MILLIS;
            }

            // .... and not in Manufacturing Mode and, if in Patient Vitals, that the screen lock is actually desired
            if (features_enabled.manufacturing_mode == false && screen_lock_timer_timeout_in_milliseconds > 0)
            {
                Log.d(TAG, "stopScreenLockCountdownTimerAndRestartIfDesired : Turning ON screen timeout : " + current_page);

                // Making a new timer because timeout duration might be different and this can only be changed during creation
                screen_lock_countdown__timer = new ScreenLockCountdownTimer(screen_lock_timer_timeout_in_milliseconds, (int)DateUtils.SECOND_IN_MILLIS);

                startScreenLockCountdownTimer();
            }
        }
        else
        {
            Log.d(TAG, "stopScreenLockCountdownTimerAndRestartIfDesired : NOT turning on screen lock timeout : " + current_page);
        }
    }


    private void startScreenLockCountdownTimer()
    {
        Log.d(TAG, "startScreenLockCountdownTimer : " + current_page);

        checkAndStart_countDownTimer(screen_lock_countdown__timer);
    }


    private void stopScreenLockCountdownTimer()
    {
        Log.d(TAG, "stopScreenLockCountdownTimer : " + current_page);

        checkAndCancel_countDownTimer(screen_lock_countdown__timer);
    }


    private void startScreenDimmingCountdownTimer()
    {
        Log.d(TAG, "startScreenDimmingCountdownTimer : " + current_page);

        if (isVideoCallPopupShowing() == false)
        {
            Log.d(TAG, "startScreenDimmingCountdownTimer NOT enabled : " + current_page);

            checkAndStart_countDownTimer(screen_dimming_countdown_timer);
        }
        else
        {
            Log.d(TAG, "startScreenDimmingCountdownTimer enabled : " + current_page);
        }
    }


    private void stopScreenDimmingCountdownTimer()
    {
        Log.d(TAG, "stopScreenDimmingCountdownTimer : " + current_page);

        checkAndCancel_countDownTimer(screen_dimming_countdown_timer);
    }


    public void restartSetupWizard()
    {
        portal_system_commands.sendGatewayCommand_restartInstallationWizard();

        startInstallationWizard();
    }


    public void forceInstallationComplete()
    {
        enableRealTimeLink(true);
        enableServerDataSync(true);

        installationProcessComplete();
    }


    public void installationProcessComplete()
    {
        features_enabled.gateway_setup_complete = true;

        portal_system_commands.sendGatewayCommand_setInstallationComplete();
    }


    private boolean needToStartInstallationWizard()
    {
        return !features_enabled.gateway_setup_complete;
    }


    private void startInstallationWizard()
    {
        enableServerDataSync(false);
        enableRealTimeLink(false);

        showInstallationWelcomeFragmentAndResetHeader();
    }


    private void showInstallationWelcomeFragmentAndResetHeader()
    {
        showInstallationWelcomeFragment();

        if (UtilityFunctions.isFragmentAddedAndResumed(header_fragment))
        {
            header_fragment.reset();
        }
    }


    private NetworkInfo getActiveNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo();
    }


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


    private boolean isConnectedToWifi()
    {
        NetworkInfo active_network = getActiveNetwork();

        if (active_network != null)
        {
            if (active_network.getType() == ConnectivityManager.TYPE_WIFI)
            {
                return active_network.isConnected();
            }
        }

        return false;
    }


    private boolean isConnectedToGsm()
    {
        NetworkInfo active_network = getActiveNetwork();

        if (active_network != null)
        {
            if (active_network.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                return active_network.isConnected();
            }
        }

        return false;
    }


    public Context getAppContext()
    {
        return getApplicationContext();
    }


    public Activity getActivity()
    {
        return this;
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


    public RemoteLogging getMainActivityLogger()
    {
        return Log;
    }


    private void removeLifetouchOptionsOverlayAndUpdateScreen()
    {
        FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            fragment.removeLifetouchOptionsOverlayIfShown();
            fragment.deviceStateChange(getDeviceByType(DeviceType.DEVICE_TYPE__LIFETOUCH));
        }
    }


    public void showLifetouchSetupMode(boolean show)
    {
        DeviceInfo device_info = getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);

        if (show)
        {
            // Send Enter Setup Mode command to Lifetouch
            sendStartSetupModeCommand(device_info);
        }
        else
        {
            // Send Stop Setup Mode command to Lifetouch
            sendStopSetupModeCommand(device_info);
        }

        removeLifetouchOptionsOverlayAndUpdateScreen();
    }


    public void showLifetouchPoincare(boolean show)
    {
        if (show)
        {
            FragmentPatientVitalsDisplay fragment = (FragmentPatientVitalsDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.removeLifetouchOptionsOverlayIfShown();

                DeviceInfo device_info = getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);

                if (device_info.isDeviceInSetupMode() || device_info.isDeviceInRawAccelerometerMode())
                {
                    //if Poincare requested during Motion or Setup mode, user should return to Motion or Setup mode after Poincare
                    // ... so in this case, don't worry about saving the config. button state etc., i.e. do nothing
                }
                else
                {
                    fragment.setConfigButtonToPoincare();
                }
            }

            showPoincarePopup();
        }
    }


    public void showLifetouchRawAccelerometer(boolean show)
    {
        DeviceInfo device_info = getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);

        if (show)
        {
            portal_system_commands.sendGatewayCommand_sendStartDeviceRawAccelerometerModeCommand(device_info.device_type);
        }
        else
        {
            portal_system_commands.sendGatewayCommand_sendStopDeviceRawAccelerometerModeCommand(device_info.device_type);
        }

        removeLifetouchOptionsOverlayAndUpdateScreen();
    }


    public void setShowLifetouchActivityLevel(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setShowLifetouchActivityLevelEnabledStatus(enabled);
    }


    public boolean getShowLifetouchActivityLevel()
    {
        return features_enabled.show_lifetouch_activity_level;
    }


    public void setAutoResumeEnabled(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setAutoResumeEnabledStatus(enabled);
    }


    public boolean getAutoResumeEnabled()
    {
        return features_enabled.auto_resume_enabled;
    }


    public void setEnableAutoLogFileUploadToServer(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setAutoUploadLogFilesToServerEnabledStatus(enabled);
    }


    public boolean getEnableAutoLogFileUploadToServer()
    {
        return features_enabled.auto_logfile_upload_to_server;
    }


    public boolean installGatewayApk()
    {
        updateModeStatus.setGatewayInstallationInProgress();

        return installLocalApk(updateModeStatus.getGatewayApkName());
    }


    public boolean installUserInterfaceApk()
    {
        updateModeStatus.setUserInterfaceInstallationInProgress();

        return installLocalApk(updateModeStatus.getUserInterfaceApkName());
    }


    public boolean isGatewaySetupComplete()
    {
        return features_enabled.gateway_setup_complete;
    }


    public void forceCheckForLatestFirmwareImagesFromServer()
    {
        portal_system_commands.sendGatewayCommand_forceCheckForLatestFirmwareImagesFromServer();
    }


    public void showHistoricalSetupModeViewerPopup(double start_timestamp, VitalSignType vital_sign_type)
    {
        try
        {
            if(popup_historical_setup_mode_viewer.getDialog() != null && popup_historical_setup_mode_viewer.getDialog().isShowing())
            {
                // Already showing
                Log.d(TAG, "showHistoricalSetupModeViewerPopup already shown");
            }
            else
            {
                // Convert timestamp back to a full UNIX timestamp
                start_timestamp += session_start_date;

                SensorType sensor_type = getSensorTypeForVitalSign(vital_sign_type);

                int y_axis_max = getDeviceByType(sensor_type).max_setup_mode_sample_size;
                ArrayList<SetupModeLog> setup_mode_log = getSetupModeLog(sensor_type);

                popup_historical_setup_mode_viewer.setArguments(y_axis_max, setup_mode_log);

                popup_historical_setup_mode_viewer.show(getSupportFragmentManager(), "");

                for (SetupModeLog item : setup_mode_log)
                {
                    if (item.start_time >= start_timestamp)
                    {
                        getBulkSetupModeData(item);
                        return;
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "showHistoricalSetupModeViewerPopup Exception : " + e);
        }
    }

    public void closeHistoricalSetupModeViewerPopupIfShowing()
    {
        closePopup(popup_historical_setup_mode_viewer);
    }

    public void showAnnotationPopup(double timestamp)
    {
        if(popup_annotation_viewer.getDialog() != null && popup_annotation_viewer.getDialog().isShowing())
        {
            // Already showing
            Log.d(TAG, "showAnnotationPopup already shown");
        }
        else
        {
            popup_annotation_viewer.setArguments((ArrayList<MeasurementAnnotation>) getCachedMeasurements(VitalSignType.MANUALLY_ENTERED_ANNOTATION));
            popup_annotation_viewer.show(getSupportFragmentManager(), "");
        }
    }

    public void closeAnnotationPopupIfShowing()
    {
        closePopup(popup_annotation_viewer);
    }


    private DeviceType device_type_for_setup_mode_query;

    private void getDeviceBulkSetupModeData(SetupModeLog item, QueryType query_type, Uri uri, String[] projection)
    {
        String string_start_timestamp = TimestampConversion.convertDateToUtcHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(item.start_time);
        String string_end_timestamp = TimestampConversion.convertDateToUtcHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(item.end_time);

        Log.d(TAG, "getDeviceBulkSetupModeData : " + item.sensor_type + " : " + item.device_type + " : Start GMT = " + string_start_timestamp + " : End GMT   = " + string_end_timestamp);

        if (item.end_time == -1)
        {
            return;
        }

        // Very hacky way of doing it but have not found a better way as cant pass anything custom into StartQuery
        device_type_for_setup_mode_query = item.device_type;

        String sort_order = "_id ASC";
        String query_selection_argument = PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " >= '" + item.start_time + "' AND " + PatientGatewayDatabaseContract.COLUMN_TIMESTAMP + " <= '" + item.end_time + "'";

        vital_signs_async_query.startQuery(query_type.ordinal(), handler_vitals_data, uri, projection, query_selection_argument, null, sort_order);
    }


    public void getBulkSetupModeData(SetupModeLog item)
    {
        switch (item.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
            {
                String[] projection = {
                        PatientGatewayDatabaseContract.COLUMN_LIFETOUCH_SETUP_MODE__SAMPLE_VALUE,
                        PatientGatewayDatabaseContract.COLUMN_TIMESTAMP
                };

                getDeviceBulkSetupModeData(item, QueryType.LIFETOUCH_SETUP_MODE_RAW_SAMPLES, PatientGatewayDatabaseContract.CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES, projection);
            }
            break;

            case SENSOR_TYPE__SPO2:
            {
                String[] projection = {
                        PatientGatewayDatabaseContract.COLUMN_OXIMETER_SETUP_MODE__SAMPLE_VALUE,
                        PatientGatewayDatabaseContract.COLUMN_TIMESTAMP
                };

                getDeviceBulkSetupModeData(item, QueryType.PULSE_OX_SETUP_MODE_SAMPLES, PatientGatewayDatabaseContract.CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES, projection);
            }
            break;
        }

        showProgress();
    }


    public int getHistoricalSetupModeViewViewportSize()
    {
        return historical_setup_viewer_view_port_width;
    }


    public void increaseHistoricalSetupModeViewportSize()
    {
        historical_setup_viewer_view_port_width += 10 * (int)DateUtils.SECOND_IN_MILLIS;
    }


    public void decreaseHistoricalSetupModeViewportSize()
    {
        historical_setup_viewer_view_port_width -= 10 * (int)DateUtils.SECOND_IN_MILLIS;

        if (historical_setup_viewer_view_port_width == 0)
        {
            historical_setup_viewer_view_port_width = 10 * (int)DateUtils.SECOND_IN_MILLIS;
        }
    }


    public void touchEventSoResetTimers()
    {
        stopScreenLockCountdownTimerAndRestartIfDesired();

        screen_dimming_countdown_timer.touchEvent();
    }


    public boolean getGsmOnlyModeFeatureEnabled()
    {
        return features_enabled.gsm_only_mode;
    }


    public float getFooterButtonTextSizeForString(String string)
    {
        String language = Locale.getDefault().toString();

        float text_size = getResources().getDimension(R.dimen.default_footer_button_text_size);

        if (language.equals("da_DK"))
        {
            if (string.equals(getResources().getString(R.string.textConnect)) || string.equals(getResources().getString(R.string.textStartMonitoring)))
            {
                text_size = getResources().getDimension(R.dimen.smaller_footer_button_text_size);
            }
        }

        return text_size;
    }


    public int getMatchingManuallyEnteredSystolicBloodPressureMeasurementFromTimestamp(long timestamp_in_ms)
    {
        return measurement_cache.getMatchingManuallyEnteredBloodPressureMeasurementFromTimestamp(timestamp_in_ms).systolic;
    }


    public int getMatchingManuallyEnteredDiastolicBloodPressureMeasurementFromTimestamp(long timestamp_in_ms)
    {
        return measurement_cache.getMatchingManuallyEnteredBloodPressureMeasurementFromTimestamp(timestamp_in_ms).diastolic;
    }


    private void showLocationServicesOffError()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(false, false, false);

        FragmentGatewayConfigurationError fragment_to_show = new FragmentGatewayConfigurationError();

        fragment_to_show.setWarningText(R.string.location_services_off);

        showFragment(fragment_to_show, UserInterfacePage.GATEWAY_CONFIGURATION_ERROR);
    }


    private void handleDeviceQrCode(SensorType sensor_type, DeviceType device_type, long human_readable_device_id, String bluetooth_device_address, boolean dummy_data)
    {
        Log.d(TAG, "handleDeviceQrCode : Sensor Type = " + sensor_type);
        Log.d(TAG, "handleDeviceQrCode : Device Type = " + device_type);
        Log.d(TAG, "handleDeviceQrCode : Human Readable Product ID = " + human_readable_device_id);
        Log.d(TAG, "handleDeviceQrCode : Bluetooth Device Address = " + bluetooth_device_address);
        Log.d(TAG, "handleDeviceQrCode : dummy data = " + dummy_data);

        if (current_page == UserInterfacePage.ADD_DEVICES)
        {
            DeviceInfo current_sensor_type_device_info = getDeviceByType(sensor_type);

            // If there is already a Sensor Type Device Info that is part of the Patient Session AND have a valid Human Readable Device ID, then
            // ignore this QR code.
            // If a previous SensorType Device was in the session but has been removed, then isDeviceTypePartOfPatientSession will still be true, but
            // isDeviceHumanReadableDeviceIdValid will be false (ID = -1)
            if (current_sensor_type_device_info.isDeviceTypePartOfPatientSession() && current_sensor_type_device_info.isDeviceHumanReadableDeviceIdValid())
            {
                Log.w(TAG, "handleDeviceQrCode : Ignoring " + device_type + " QR code as device of same sensor type already part of Patient Session");
            }
            else
            {
                portal_system_commands.sendGatewayCommand_checkDeviceStatus(device_type, human_readable_device_id);

                DeviceInfo device_info = new DeviceInfo(device_type);
                device_info.human_readable_device_id = human_readable_device_id;
                device_info.bluetooth_address = bluetooth_device_address;
                device_info.device_name = getDeviceNameFromSensorType(sensor_type);
                device_info.dummy_data_mode = dummy_data;
                if(dummy_data)
                {
                    device_info.firmware_version = 123;
                }
                else
                {
                    device_info.firmware_version = INVALID_FIRMWARE_VERSION;
                }

                if(device_type == DeviceType.DEVICE_TYPE__LIFETEMP_V2)
                {
                    device_info.measurement_interval_in_seconds = getLifetempMeasurementInterval();
                }

                portal_system_commands.sendGatewayCommand_setDesiredDevice(device_info);
            }
        }
        else if (current_page == UserInterfacePage.CHECK_DEVICE_STATUS)
        {
            portal_system_commands.sendGatewayCommand_checkDeviceStatus(device_type, human_readable_device_id);

            FragmentCheckDeviceStatus fragment = (FragmentCheckDeviceStatus) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.setDeviceType(device_type);
                fragment.setHumanReadableNumberDeviceId(human_readable_device_id);
                fragment.setDeviceMacAddress(bluetooth_device_address);
                fragment.showCheckingWithServer(true);
            }
        }
        else if (current_page == UserInterfacePage.MANUFACTURING_MODE__CHECK_PACKAGING)
        {
            FragmentCheckPackaging fragment = (FragmentCheckPackaging) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.setDeviceType(device_type);
                fragment.setHumanReadableNumberDeviceId(human_readable_device_id);
                fragment.setDeviceMacAddress(bluetooth_device_address);

                fragment.setupForNextBarcodeScan();
            }
        }
    }


    private void handleDeviceDataMatrix(SensorType sensor_type, DeviceType device_type, long human_readable_device_id, String bluetooth_device_address, DateTime manufacture_date_as_datetime, DateTime expiration_date_as_datetime, String lot_number)
    {
        if (current_page == UserInterfacePage.ADD_DEVICES)
        {
            portal_system_commands.sendGatewayCommand_checkDeviceStatus(device_type, human_readable_device_id);

            DeviceInfo device_info = new DeviceInfo(device_type);
            device_info.human_readable_device_id = human_readable_device_id;
            device_info.bluetooth_address = bluetooth_device_address;
            device_info.device_name = getDeviceNameFromSensorType(sensor_type);
            device_info.manufacture_date = manufacture_date_as_datetime;
            device_info.expiration_date = expiration_date_as_datetime;
            device_info.lot_number = lot_number;

            portal_system_commands.sendGatewayCommand_setDesiredDevice(device_info);
        }
        else if (current_page == UserInterfacePage.CHECK_DEVICE_STATUS)
        {
            portal_system_commands.sendGatewayCommand_checkDeviceStatus(device_type, human_readable_device_id);

            FragmentCheckDeviceStatus fragment = (FragmentCheckDeviceStatus) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.setDeviceType(device_type);
                fragment.setHumanReadableNumberDeviceId(human_readable_device_id);
                fragment.setDeviceMacAddress(bluetooth_device_address);
                fragment.showCheckingWithServer(true);
            }
        }
    }


    public String getDeviceNameByType(DeviceType device_type)
    {
        switch(device_type)
        {
            case DEVICE_TYPE__LIFETOUCH:
            case DEVICE_TYPE__LIFETOUCH_BLUE_V2:
            case DEVICE_TYPE__LIFETOUCH_THREE:
                return getResources().getString(R.string.textLifetouch);

            case DEVICE_TYPE__LIFETEMP_V2:
                return getResources().getString(R.string.textLifetemp);

            case DEVICE_TYPE__NONIN_WRIST_OX:
            case DEVICE_TYPE__NONIN_WRIST_OX_BTLE:
                return getResources().getString(R.string.textNonin);

            case DEVICE_TYPE__AND_UA767:
                return "A&D UA767";

            case DEVICE_TYPE__AND_ABPM_TM2441:
                return "A&D TM2441";

            case DEVICE_TYPE__AND_UA651:
                return "A&D UA651";

            case DEVICE_TYPE__AND_UA656BLE:
                return "A&D UA656";

            case DEVICE_TYPE__MEDLINKET:
                return "MEDLINKET";

            case DEVICE_TYPE__FORA_IR20:
                return getResources().getString(R.string.textForaIr20);

            case DEVICE_TYPE__AND_UC352BLE:
                return "A&D UC352";

            case DEVICE_TYPE__AND_UA1200BLE:
                return "A&D UA1200";
        }

        return "";
    }


    public String getDeviceNameFromSensorType(SensorType sensor_type)
    {
        switch(sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
                return getResources().getString(R.string.textLifetouch);

            case SENSOR_TYPE__TEMPERATURE:
                return getResources().getString(R.string.textThermometer);

            case SENSOR_TYPE__SPO2:
                return getResources().getString(R.string.textPulseOximeter);

            case SENSOR_TYPE__BLOOD_PRESSURE:
                return getResources().getString(R.string.textBloodPressureSingleLine);

            case SENSOR_TYPE__WEIGHT_SCALE:
                return getResources().getString(R.string.weightScaleSingleLine);
        }

        return "";
    }


    private void setCachedDeviceInfoSetupModeTimes(int setup_mode_time_in_seconds)
    {
        for(DeviceInfo existing_device : cached_device_info_list)
        {
            existing_device.setup_mode_time_in_seconds = setup_mode_time_in_seconds;
        }
    }


    public void checkPackagingSelected()
    {
        current_page = UserInterfacePage.INVALID;
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textNext));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        enableNavigationButtons(true, true, false);

        showFragment(new FragmentCheckPackaging(), UserInterfacePage.MANUFACTURING_MODE__CHECK_PACKAGING);
    }


    public void enterAnnotationsSelected()
    {
        // Reset the annotation being entered
        annotation_being_entered = new AnnotationBeingEntered();

        current_page = UserInterfacePage.INVALID;
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textNext));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        if (features_enabled.predefined_annotations_enabled)
        {
            enableNavigationButtons(true, true, false);

            showFragment(new FragmentAnnotationEntrySelectAnnotationType(), UserInterfacePage.ANNOTATION_ENTRY_SELECT_ANNOTATION_TYPE);
        }
        else
        {
            enableNavigationButtons(true, true, true);

            annotation_being_entered.setAnnotationEntryType(AnnotationEntryType.CUSTOM_VIA_ONSCREEN_KEYBOARD);

            showFragment(new FragmentAnnotationEntrySelectAnnotationTime(), UserInterfacePage.ANNOTATION_ENTRY_SELECT_ANNOTATION_TIME);
        }
    }


    private void selectAnnotationTimeSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, true);
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textNext));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentAnnotationEntrySelectAnnotationTime(), UserInterfacePage.ANNOTATION_ENTRY_SELECT_ANNOTATION_TIME);
    }


    private KeyboardMode getKeyboardModeAsEnum()
    {
        KeyboardMode keyboardMode = KeyboardMode.ONSCREEN;
        if (isBluetoothKeyboardConnected())
        {
            keyboardMode = KeyboardMode.BLUETOOTH;
        }

        return keyboardMode;
    }


    private void enterAnnotationViaKeyboardSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textNext));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        Bundle bundle = new Bundle();
        bundle.putInt(FragmentAnnotationTextEntry.KEY_KEYBOARD_MODE, getKeyboardModeAsEnum().ordinal());

        showFragmentWithBundle(new FragmentAnnotationTextEntry(), bundle, UserInterfacePage.ANNOTATION_ENTRY_ENTER_ANNOTATION_VIA_KEYBOARD);
    }


    private void enterPredefinedAnnotationConditionSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textNext));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentAnnotationConditionList(), UserInterfacePage.ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_CONDITION);
    }


    private void enterPredefinedAnnotationActionSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textNext));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentAnnotationActionList(), UserInterfacePage.ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_ACTION);
    }


    private void enterPredefinedAnnotationOutcomeSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setNextButtonText(getResources().getString(R.string.textNext));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentAnnotationOutcomeList(), UserInterfacePage.ANNOTATION_ENTRY_ENTER_PREDEFINED_ANNOTATION_OUTCOME);
    }


    private void showNextButtonIfStringNotEmpty(String selected)
    {
        showNextButton(!selected.isEmpty());
    }

    private final ServerConfigurableTextStrings serverConfigurableTextStrings = new ServerConfigurableTextStrings();

    public ArrayList<AnnotationDescriptor> getAnnotationConditions()
    {
        return serverConfigurableTextStrings.getAnnotationConditions();
    }


    public void annotationConditionsSelected(String selected)
    {
        annotation_being_entered.setConditions(selected);

        showNextButtonIfStringNotEmpty(selected);
    }


    public ArrayList<AnnotationDescriptor> getAnnotationActions()
    {
        return serverConfigurableTextStrings.getAnnotationActions();
    }


    public void annotationActionsSelected(String selected)
    {
        annotation_being_entered.setActions(selected);

        showNextButtonIfStringNotEmpty(selected);
    }


    public ArrayList<AnnotationDescriptor> getAnnotationOutcomes()
    {
        return serverConfigurableTextStrings.getAnnotationOutcomes();
    }


    public void annotationOutcomesSelected(String selected)
    {
        annotation_being_entered.setOutcomes(selected);

        showNextButtonIfStringNotEmpty(selected);
    }


    public VitalSignType getVitalSignTypeForDeviceInfo(DeviceInfo device_info)
    {
        switch(device_info.sensor_type)
        {
            case SENSOR_TYPE__LIFETOUCH:
                return VitalSignType.HEART_RATE;
//                return VitalSignType.RESPIRATION_RATE;

            case SENSOR_TYPE__TEMPERATURE:
                return VitalSignType.TEMPERATURE;

            case SENSOR_TYPE__SPO2:
                return VitalSignType.SPO2;

            case SENSOR_TYPE__BLOOD_PRESSURE:
                return VitalSignType.BLOOD_PRESSURE;

            case SENSOR_TYPE__WEIGHT_SCALE:
                return VitalSignType.WEIGHT;

            case SENSOR_TYPE__ALGORITHM:
            {
                switch (device_info.device_type)
                {
                    case DEVICE_TYPE__EARLY_WARNING_SCORE:
                        return VitalSignType.EARLY_WARNING_SCORE;
                }
            }
            break;

            case SENSOR_TYPE__MANUAL_VITAL:
            {
                switch (device_info.device_type)
                {
                    case DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE:
                        return VitalSignType.MANUALLY_ENTERED_HEART_RATE;

                    case DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE:
                        return VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE;

                    case DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE:
                        return VitalSignType.MANUALLY_ENTERED_TEMPERATURE;

                    case DEVICE_TYPE__MANUALLY_ENTERED_SPO2:
                        return VitalSignType.MANUALLY_ENTERED_SPO2;

                    case DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE:
                        return VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE;

                    case DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT:
                        return VitalSignType.MANUALLY_ENTERED_WEIGHT;

                    case DEVICE_TYPE__MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
                        return VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME;

                    case DEVICE_TYPE__MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:
                        return VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL;

                    case DEVICE_TYPE__MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                        return VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN;

                    case DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                        return VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS;

                    case DEVICE_TYPE__MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:
                        return VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN;

                    case DEVICE_TYPE__MANUALLY_ENTERED_URINE_OUTPUT:
                        return VitalSignType.MANUALLY_ENTERED_URINE_OUTPUT;
                }
            }
            break;
        }

        return VitalSignType.NOT_SET_YET;
    }


    public DeviceInfo getDeviceInfoForVitalSign(VitalSignType vital_sign_type)
    {
        switch (vital_sign_type)
        {
            case HEART_RATE:
            case RESPIRATION_RATE:
                return getDeviceByType(SensorType.SENSOR_TYPE__LIFETOUCH);

            case TEMPERATURE:                               return getDeviceByType(SensorType.SENSOR_TYPE__TEMPERATURE);
            case SPO2:                                      return getDeviceByType(SensorType.SENSOR_TYPE__SPO2);
            case BLOOD_PRESSURE:                            return getDeviceByType(SensorType.SENSOR_TYPE__BLOOD_PRESSURE);
            case WEIGHT:                                    return getDeviceByType(SensorType.SENSOR_TYPE__WEIGHT_SCALE);

            case MANUALLY_ENTERED_HEART_RATE:               return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE);
            case MANUALLY_ENTERED_RESPIRATION_RATE:         return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE);
            case MANUALLY_ENTERED_TEMPERATURE:              return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE);
            case MANUALLY_ENTERED_SPO2:                     return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SPO2);
            case MANUALLY_ENTERED_BLOOD_PRESSURE:           return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE);
            case MANUALLY_ENTERED_WEIGHT:                   return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT);

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:    return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);
            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL:      return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL);
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:  return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN);
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:     return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_DISTRESS);
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN:      return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN);
            case MANUALLY_ENTERED_URINE_OUTPUT:             return getDeviceByType(DeviceType.DEVICE_TYPE__MANUALLY_ENTERED_URINE_OUTPUT);

            case EARLY_WARNING_SCORE:                       return getDeviceByType(DeviceType.DEVICE_TYPE__EARLY_WARNING_SCORE);
        }

        return getDeviceByType(DeviceType.DEVICE_TYPE__INVALID);
    }


    public SensorType getSensorTypeForVitalSign(VitalSignType vital_sign_type)
    {
        switch (vital_sign_type)
        {
            case HEART_RATE:
            case RESPIRATION_RATE:
                return SensorType.SENSOR_TYPE__LIFETOUCH;

            case TEMPERATURE:
                return SensorType.SENSOR_TYPE__TEMPERATURE;

            case SPO2:
                return SensorType.SENSOR_TYPE__SPO2;

            case BLOOD_PRESSURE:
                return SensorType.SENSOR_TYPE__BLOOD_PRESSURE;
        }

        return SensorType.SENSOR_TYPE__INVALID;
    }

    public void closeVideoCallPopupIfShowing()
    {
        closePopup(popup_video_call);
    }


    public void closePoincarePopupIfShowing()
    {
        closePopup(popup_lifetouch_poincare);
    }


    public void showPoincarePopup()
    {
        final FragmentManager fragmentManager = getSupportFragmentManager();

        popup_lifetouch_poincare = new PopupLifetouchPoincare(this);
        popup_lifetouch_poincare.show(fragmentManager, "");

        // Add in code to detect when the popup is dismissed by tapping the screen outside the popup window
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks()
        {
            @Override
            public void onFragmentViewDestroyed(@NotNull FragmentManager fm, @NotNull Fragment f) {
                super.onFragmentViewDestroyed(fm, f);

                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }


    public ArrayList<SetupModeLog> getSetupModeLog(SensorType sensor_type)
    {
        return setup_mode_log_cache.getCachedMeasurements(sensor_type);
    }


    public void setWifiLoggingEnabled(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setWifiLoggingEnabledStatus(enabled);
    }
    public boolean getWifiLoggingEnabled()
    {
        return features_enabled.wifi_logging_enabled;
    }

    public void setGsmLoggingEnabled(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setGsmLoggingEnabledStatus(enabled);
    }
    public boolean getGsmLoggingEnabled()
    {
        return features_enabled.gsm_logging_enabled;
    }

    public void setDatabaseLoggingEnabled(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setDatabaseLoggingEnabledStatus(enabled);
    }
    public boolean getDatabaseLoggingEnabled()
    {
        return features_enabled.database_logging_enabled;
    }

    public void setServerLoggingEnabled(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setServerLoggingEnabledStatus(enabled);
    }
    public boolean getServerLoggingEnabled()
    {
        return features_enabled.server_logging_enabled;
    }

    public void setBatteryLoggingEnabled(boolean enabled)
    {
        portal_system_commands.sendGatewayCommand_setBatteryLoggingEnabledStatus(enabled);
    }
    public boolean getBatteryLoggingEnabled()
    {
        return features_enabled.battery_logging_enabled;
    }

    private void showProgress()
    {
        progressWindow.showProgress();
    }


    public void hideProgress()
    {
        progressWindow.hideProgress();
    }


    private AnnotationBeingEntered annotation_being_entered = new AnnotationBeingEntered();


    public void setAnnotationsTimestamp(long timestamp)
    {
        annotation_being_entered.setTimestamp(timestamp);
    }


    public AnnotationBeingEntered getAnnotationBeingEntered()
    {
        return annotation_being_entered;
    }


    public void annotationEnteredViaKeyboard(String annotation)
    {
        annotation_being_entered.setKeyboardAnnotation(annotation);
    }


    public void storeAnnotation()
    {
        int measurement_validity_time_in_seconds = 0;
        storeAnnotation(annotation_being_entered.getAnnotation(), annotation_being_entered.getTimestamp(), measurement_validity_time_in_seconds, getGatewayUserId());
    }


    public boolean showRightHandSideDescriptionForObservationEntry(int vital_sign_id)
    {
        ManualVitalSignInfo manual_vital_sign_info = getManualVitalSignInfo(vital_sign_id);
        if (manual_vital_sign_info != null)
        {
            return manual_vital_sign_info.show_rhs_description;
        }

        return false;
    }


    public String getObservationSetVitalSignSelectionTopText()
    {
        String observation_time = getResources().getString(R.string.observation_time);
        String measurement_timestamp = TimestampConversion.convertDateToHumanReadableStringDayHoursMinutes(observation_set_being_entered.getTimestamp());

        String validity_time = getResources().getString(R.string.validity_time);
        String measurement_validity_time = observation_set_being_entered.getValidityTimeForDisplay();

        return observation_time + " = " + measurement_timestamp + " : " + validity_time + " = " + measurement_validity_time;
    }


    public String updateScreenBasedOnManualVitalSignType(int vital_sign_id)
    {
        gateway_audit_last_option_chosen = vital_sign_id;

        ManualVitalSignInfo vitalSignInfo = getManualVitalSignInfo(vital_sign_id);

        if (vitalSignInfo != null)
        {
            return vitalSignInfo.please_enter_the_xxxx;
        }
        else
        {
            return "";
        }
    }


    public void removeManualVitalSignFromObservationSet(ManuallyEnteredVitalSignDescriptor vital_sign_selected)
    {
        observation_set_being_entered.remove(vital_sign_selected);

        // Redraw the screen
        observationSetVitalSignSelectionSelected();
    }


    public ManualVitalSignBeingEntered getManualVitalSignBeingEntered(int vital_sign_id)
    {
        ManualVitalSignBeingEntered measurement = observation_set_being_entered.getMeasurement(vital_sign_id);
        if (measurement != null)
        {
            ManualVitalSignInfo manual_vital_sign_info = getManualVitalSignInfo(vital_sign_id);
            if (manual_vital_sign_info != null)
            {
                switch (manual_vital_sign_info.entry_type)
                {
                    case BUTTON_SELECTION:
                    {
                        measurement.setValue(manual_vital_sign_info.button_info.get(measurement.getButtonId()).button_text);
                    }
                    break;

                    default:
                    {
                        // Measurement.value already contains the String value
                    }
                    break;
                }
            }
        }

        return measurement;
    }


    public String getGraphLabelForManualVitalSigns(VitalSignType vital_sign_type, double x, double y)
    {
        String value;

        switch (vital_sign_type)
        {
            case MANUALLY_ENTERED_TEMPERATURE:
            case MANUALLY_ENTERED_WEIGHT:
                // Numbers with decimal points
                value = String.valueOf((float)y);
                break;

            case MANUALLY_ENTERED_BLOOD_PRESSURE:
                // Graph starts at 1970 to make arrays smaller
                long real_measurement_time = (long)x + getSessionStartDate();
                int systolic = getMatchingManuallyEnteredSystolicBloodPressureMeasurementFromTimestamp(real_measurement_time);
                int diastolic = getMatchingManuallyEnteredDiastolicBloodPressureMeasurementFromTimestamp(real_measurement_time);
                value = systolic + "/" + diastolic;
                break;

            default:
                value = String.valueOf((int)y);
                break;
        }

        return value;
    }


    public void queryWardsAndBeds()
    {
        queryWards();
        queryBeds();
    }


    public boolean isMqttSelected()
    {
        return features_enabled.realtime_server_type == RealTimeServer.MQTT;
    }


    private DeviceType getDeviceTypeFromIntent(Intent intent)
    {
        return DeviceType.values()[intent.getIntExtra("device_type", DeviceType.DEVICE_TYPE__INVALID.ordinal())];
    }


    private SensorType getSensorTypeFromIntent(Intent intent)
    {
        return SensorType.values()[intent.getIntExtra("sensor_type", SensorType.SENSOR_TYPE__INVALID.ordinal())];
    }


    private void getSensorOperatingMode(SensorType sensor_type)
    {
        DeviceInfo device_info = getDeviceByType(sensor_type);
        portal_system_commands.sendGatewayCommand_getDeviceOperatingMode(device_info.device_type);
    }


    public ArrayList<DeviceInfo> getDeviceInfoList()
    {
        return cached_device_info_list;
    }


    public ArrayList<VitalSignType> getListOfManualVitalSigns()
    {
        ArrayList<VitalSignType> list_of_manual_vital_sign_types = new ArrayList<>();
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_HEART_RATE);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_TEMPERATURE);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_SPO2);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_WEIGHT);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_ANNOTATION);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_RESPIRATION_DISTRESS);
        list_of_manual_vital_sign_types.add(VitalSignType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN);

        return list_of_manual_vital_sign_types;
    }


    public boolean isVitalSignTypeAManualVital(VitalSignType vital_sign_type)
    {
        return getListOfManualVitalSigns().contains(vital_sign_type);
    }


    public ThresholdSetAgeBlockDetail getThresholdSetAgeBlockDetailIdForAdults()
    {
        for (ThresholdSet thresholdSet : default_early_warning_score_threshold_sets)
        {
            for (ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail : thresholdSet.list_threshold_set_age_block_detail)
            {
                if (thresholdSetAgeBlockDetail.is_adult)
                {
                    return thresholdSetAgeBlockDetail;
                }
            }
        }

        return null;
    }


    public boolean stopUiFastUpdates()
    {
        return features_enabled.stop_fast_ui_updates;
    }


    public void getLastNetworkStatus()
    {
        portal_system_commands.sendGatewayCommand_getLastNetworkStatus();
    }


    public boolean isTabletGsm()
    {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }


    public String removeLeadingCharacters(String inputString, char characterToReplace)
    {
        return UtilityFunctions.removeLeadingCharacters(inputString, characterToReplace);
    }


    public String removeTrailingCharacters(String inputString, char characterToReplace)
    {
        return UtilityFunctions.removeTrailingCharacters(inputString, characterToReplace);
    }


    public void enterUpdateMode()
    {
        portal_system_commands.sendGatewayCommand_enterUpdateMode();

        showSoftwareUpdateMode();
    }


    public void softwareUpdateComplete()
    {
        portal_system_commands.sendGatewayCommand_softwareUpdateComplete();

        software_update_mode_active = false;
        updateModeStatus.setInstallationNotInProgress();

        // deregister update mode receiver
        unregisterReceiver(broadcastReceiverIncomingCommandsFromPatientGateway__updateMode);

        // register regular broadcast receiver
        registerReceiver(broadcastReceiverIncomingCommandsFromPatientGateway, new IntentFilter(INTENT__COMMANDS_TO_USER_INTERFACE));

        if (footer_fragment != null)
        {
            footer_fragment.hideControlsDueToUpdateMode(false);
        }

        current_page = UserInterfacePage.MODE_SELECTION;

        lockScreenSelected();

        getCurrentStatusFromGateway();
    }


    public void requestOverlayPermission()
    {
        permissions.requestOverlayPermission();
    }


    public void requestWriteSettingsPermission()
    {
        permissions.requestWriteSettingsPermission();
    }


    public void requestCameraPermission()
    {
        permissions.requestCameraPermission();
    }


    public void requestRecordAudioPermission()
    {
        permissions.requestRecordAudioPermission();
    }


    public void requestWriteExternalStoragePermission()
    {
        permissions.requestWriteExternalStoragePermission();
    }


    public void requestAccessNotificationPolicyPermission()
    {
        permissions.requestAccessNotificationPolicyPermission();
    }


    public void requestInstallPackagesPermission()
    {
        permissions.requestInstallPackagesPermission();
    }


    private void joinVideoCall(VideoCallDetails meetingDetails)
    {
        if (current_page != UserInterfacePage.EMPTY)
        {
            page_before_video_call_started = current_page;
        }

        // Have to ensure that the UI activity is NOT using the camera otherwise Android will complain
        if (current_page == UserInterfacePage.ADD_DEVICES ||
                current_page == UserInterfacePage.CHECK_DEVICE_STATUS ||
                current_page == UserInterfacePage.UNLOCK_SCREEN )
        {
            Log.d(TAG, "Switching to Empty fragment to ensure the QR Scanning Camera code does not interfere with the Zoom code");

            emptyFragmentSelected();
        }

        Log.d(TAG, "joinVideoCall : " + current_page);

        connectToOpenViduServer(meetingDetails);
    }


    private void handleVideoCallFailed()
    {
        //zoomWrapper.leaveZoomMeeting(false);

        popup_video_call.showMeetingFailed();
    }


    public void leaveVideoCall()
    {
        popup_video_call.dismissPopupIfVisible();

        leaveSession();
    }

    // Fully left the Video conference
    public void videoCallLeftSoGoBackToPreviousPage()
    {
        Log.d(TAG, "videoCallLeftSoGoBackToPreviousPage : current_page = " + current_page + " : " + page_before_video_call_started);

        if (current_page != page_before_video_call_started)
        {
            switchBackToFragment(page_before_video_call_started);
        }

        // Video button will have been disabled to prevent double taps
        if (current_page == UserInterfacePage.VIDEO_CALL_CONTACTS_FROM_UNLOCK_PAGE || current_page == UserInterfacePage.VIDEO_CALL_CONTACTS)
        {
            FragmentVideoCallContactSelectionList fragment = (FragmentVideoCallContactSelectionList) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
            {
                fragment.enableVideoCallButtonIfContactsSelected();
            }
        }
    }


    public void dismissOnScreenKeyboardCurrentFocus()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null)
        {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void showIncomingVideoCallPopup(String fromText, String missedCallText, int displayTimeInSeconds)
    {
        String startOfLogLine = "popup_video_call - showIncomingVideoCallPopup :";

        Log.d(TAG, startOfLogLine + " videoCallStatus = " + videoCallStatus);

        if((popup_video_call.getDialog() == null) || (!popup_video_call.getDialog().isShowing()))
        {
            Log.d(TAG, startOfLogLine + " Popup not on screen. Therefore no call in progress. Setup for a new Zoom call");

            dismissOnScreenKeyboardCurrentFocus();

            showUnpluggedOverlay(false);

            // While the Incoming call popup is being shown, stop the Screen Lock timer
            // Once the popup is removed, the code will auto resume the previous fragment which will take care of the screen lock again
            stopScreenLockCountdownTimer();

            // Stop the Screen dimming timer. Want to have the screen as bright as possible during a video call
            stopScreenDimmingCountdownTimer();

            setVideoCallStatusAndReportToServer(VideoCallStatus.VIDEO_CALL_STATUS__SERVER_CALLING_GATEWAY__GATEWAY_RINGING);

            popup_video_call.configureParameters(PopupVideoCall.VideoCallType.INCOMING_FROM_SERVER, fromText, missedCallText, displayTimeInSeconds);
            popup_video_call.show(getSupportFragmentManager(), "");
        }
        else
        {
            startOfLogLine = startOfLogLine.concat(" Popup already showing :");

            switch (videoCallStatus)
            {
                case VIDEO_CALL_STATUS__SERVER_CALLING_GATEWAY__GATEWAY_RINGING:
                {
                    Log.d(TAG, startOfLogLine + " Ignoring showIncomingVideoCallPopup - Already in VIDEO_CALL_STATUS__INCOMING_CALL_RINGING");
                }
                break;

                case VIDEO_CALL_STATUS__CONNECTING:
                case VIDEO_CALL_STATUS__RECONNECTING:
                {
                    Log.d(TAG, startOfLogLine + " Ignoring showIncomingVideoCallPopup - VIDEO_CALL_STATUS__CONNECTING or VIDEO_CALL_STATUS__RECONNECTING");
                }
                break;

                case VIDEO_CALL_STATUS__MISSED_CALL:
                case VIDEO_CALL_STATUS__IDLE:
                {
                    Log.d(TAG, startOfLogLine + " Resetting back to Incoming Call from Missed Call or Idle (after call Failure)");

                    videoCallStatus = VideoCallStatus.VIDEO_CALL_STATUS__SERVER_CALLING_GATEWAY__GATEWAY_RINGING;

                    popup_video_call.configureParameters(PopupVideoCall.VideoCallType.INCOMING_FROM_SERVER, fromText, missedCallText, displayTimeInSeconds);
                    popup_video_call.resetPopupForIncomingCall();
                }
                break;

                case VIDEO_CALL_STATUS__DISCONNECTING:
                default:
                {
                    Log.d(TAG, startOfLogLine + " Do nothing");
                }
                break;
            }
        }
    }

    private void showOutgoingVideoCallPopup(String toText, String missedCallText, int ringTimeInSeconds)
    {
        String startOfLogLine = "popup_video_call - showOutgoingVideoCallPopup :";

        Log.d(TAG, startOfLogLine + " videoCallStatus = " + videoCallStatus);

        if((popup_video_call.getDialog() == null) || (!popup_video_call.getDialog().isShowing()))
        {
            Log.d(TAG, startOfLogLine + " Popup not on screen. Therefore no call in progress. Setup for a new Zoom call");

            dismissOnScreenKeyboardCurrentFocus();

            showUnpluggedOverlay(false);

            // While the Incoming call popup is being shown, stop the Screen Lock timer
            // Once the popup is removed, the code will auto resume the previous fragment which will take care of the screen lock again
            stopScreenLockCountdownTimer();

            // Stop the Screen dimming timer. Want to have the screen as bright as possible during a video call
            stopScreenDimmingCountdownTimer();

            setVideoCallStatusAndReportToServer(VideoCallStatus.VIDEO_CALL_STATUS__GATEWAY_TO_SERVER_CALL_RINGING);

            page_before_video_call_started = current_page;

            popup_video_call.configureParameters(PopupVideoCall.VideoCallType.OUTGOING_TO_SERVER, toText, missedCallText, ringTimeInSeconds);
            popup_video_call.show(getSupportFragmentManager(), "");
        }
    }

    private void setVideoCallStatusAndReportToServer(VideoCallStatus updatedVideoCallStatus)
    {
        videoCallStatus = updatedVideoCallStatus;

        Log.d(TAG,"setVideoCallStatusAndReportToServer : videoCallStatus = " + videoCallStatus);

        portal_system_commands.sendGatewayCommand_reportGatewayVideoCallStatus(videoCallDetails.connection_id, videoCallStatus);
    }


    private boolean isVideoCallPopupShowing()
    {
        if(popup_video_call.getDialog() != null)
        {
            if (popup_video_call.getDialog().isShowing())
            {
                Log.d(TAG, "isIncomingVideoCallPopupShowing");

                return true;
            }
        }

        return false;
    }


    // 70% would be 0.7f;
    private void setAndroidVolumeToFixedPercentage(float percent)
    {
        setAndroidStreamVolumeToFixedPercentageOfMaxVolume(percent, AudioManager.STREAM_VOICE_CALL);
        setAndroidStreamVolumeToFixedPercentageOfMaxVolume(percent, AudioManager.STREAM_SYSTEM);
        setAndroidStreamVolumeToFixedPercentageOfMaxVolume(percent, AudioManager.STREAM_RING);
        setAndroidStreamVolumeToFixedPercentageOfMaxVolume(percent, AudioManager.STREAM_MUSIC);
        setAndroidStreamVolumeToFixedPercentageOfMaxVolume(percent, AudioManager.STREAM_ALARM);

        // Set notifications stream to mute to prevent notifications from SureMDM, Weather, Play store logins etc. making sounds
        // Doing this each time that setAndroidVolumeToFixedPercentage() is called because otherwise notification sounds returned after a video call
        setAndroidStreamVolumeToFixedPercentageOfMaxVolume(ADJUST_MUTE, AudioManager.STREAM_NOTIFICATION);
    }


    private void setAndroidStreamVolumeToFixedPercentageOfMaxVolume(float percent, int streamType)
    {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        // This seems to make the microphone more sensitive
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        int maxVolume = audioManager.getStreamMaxVolume(streamType);
        int desiredVolume = (int) (maxVolume * percent);
        audioManager.setStreamVolume(streamType, desiredVolume, 0);
    }


    private void closeAllPopups()
    {
        // Dismiss popups if showing
        closeAnnotationPopupIfShowing();
        closeHistoricalSetupModeViewerPopupIfShowing();
        closePoincarePopupIfShowing();
        closeVideoCallPopupIfShowing();

        closePopup(popup_developer_options);
        closePopup(popup_patient_name);
        closePopup(popup_recycling_reminder);
        closePopup(popup_server_syncing);
        closePopup(popup_wifi_status);
    }


    private void switchBackToFragment(UserInterfacePage userInterfacePage)
    {
        switch (userInterfacePage)
        {
            case ADD_DEVICES:
            {
                addDevicesSelected();
            }
            break;

            case CHECK_DEVICE_STATUS:
            {
                checkDeviceStatusPressed();
            }
            break;

            case UNLOCK_SCREEN:
            {
                lockScreenSelected();
            }
            break;

            default:
            {
                showModeSelectionOrGatewayNotConfiguredYet();
            }
            break;
        }
    }

    private String OPENVIDU_URL;
    private Session session;
    private CustomHttpClient httpClient;

    private ArrayList<VideoCallContact> requestedVideoCallContacts = new ArrayList<>();

    private void connectToOpenViduServer(VideoCallDetails meetingDetails)
    {
        Log.d(TAG, "VIDEO : connectToOpenViduServer : " + meetingDetails.toString());

        initViews();
        viewToConnectingState();

        OPENVIDU_URL = meetingDetails.server_url;

        httpClient = new CustomHttpClient(OPENVIDU_URL, "Basic " + android.util.Base64.encodeToString(("OPENVIDUAPP:" + meetingDetails.meeting_password).getBytes(), android.util.Base64.NO_PADDING).trim());

        String sessionId = meetingDetails.meeting_id;
        getToken(sessionId);
    }


    private void initViews()
    {
        popup_video_call.initViews();
    }

    public void viewToDisconnectedState()
    {
        setVideoCallStatusAndReportToServer(VideoCallStatus.VIDEO_CALL_STATUS__DISCONNECTING);

        runOnUiThread(() -> popup_video_call.viewToDisconnectedState());
    }

    public void viewToConnectingState()
    {
        setVideoCallStatusAndReportToServer(VideoCallStatus.VIDEO_CALL_STATUS__CONNECTING);

        runOnUiThread(() -> popup_video_call.viewToConnectingState());
    }

    public void viewToConnectedState()
    {
        setVideoCallStatusAndReportToServer(VideoCallStatus.VIDEO_CALL_STATUS__IN_MEETING);

        runOnUiThread(() -> popup_video_call.viewToConnectedState());
    }

    public void createRemoteParticipantVideo(final RemoteParticipant remoteParticipant)
    {
        Handler mainHandler = new Handler(this.getMainLooper());
        Runnable myRunnable = () -> {

            int numberOfParticipants = session.getNumberOfRemoteParticipants();
            if (numberOfParticipants < 6)
            {
                View rowView = this.getLayoutInflater().inflate(R.layout.peer_video, null);
                int rowId = View.generateViewId();
                rowView.setId(rowId);

                GridLayout gridLayout = popup_video_call.getViewsContainer();
                gridLayout.addView(rowView);

                // This is peer_video.xml
                SurfaceViewRenderer videoView = (SurfaceViewRenderer) ((ViewGroup) rowView).getChildAt(0);
                remoteParticipant.setVideoView(videoView);
                videoView.setMirror(false);

                EglBase rootEglBase = EglBase.create();
                videoView.init(rootEglBase.getEglBaseContext(), null);
                // Do not want the remote videos overlaying the local view
                videoView.setZOrderMediaOverlay(false);

                View textView = ((ViewGroup) rowView).getChildAt(1);
                remoteParticipant.setParticipantNameText((TextView) textView);
                remoteParticipant.setView(rowView);
                remoteParticipant.getParticipantNameText().setText(remoteParticipant.getParticipantName());
                remoteParticipant.getParticipantNameText().setPadding(10, 3, 10, 3);

                resizeParticipantViews();

                Log.d(TAG, "VIDEO : Updated : Rows = " + gridLayout.getRowCount() + " : Cols = " + gridLayout.getColumnCount() + " : Number remote people = " + numberOfParticipants);
            }
            else
            {
                Log.e(TAG, "VIDEO : Already got 6 videos. IGNORING");
            }
        };

        mainHandler.post(myRunnable);
    }

    private void resizeParticipantViews()
    {
        int rows;
        int columns;

        switch (session.getNumberOfRemoteParticipants())
        {
            case 1:
            {
                rows = 1;
                columns = 1;
            }
            break;

            case 2:
            {
                rows = 1;
                columns = 2;
            }
            break;

            case 3:
            case 4:
            {
                rows = 2;
                columns = 2;
            }
            break;

            case 5:
            case 6:
            default:
            {
                rows = 2;
                columns = 3;
            }
            break;
        }

        // Set the desired number of rows and columns
        GridLayout gridLayout = popup_video_call.getViewsContainer();
        gridLayout.setRowCount(rows);
        gridLayout.setColumnCount(columns);

        // Now resize the Views themselves based on the new sizes
        int viewHeight = gridLayout.getHeight() / rows;
        int viewWidth = gridLayout.getWidth() / columns;

        Map<String, RemoteParticipant> map = session.getRemoteParticipantsHashMap();
        for (Map.Entry<String, RemoteParticipant> entry : map.entrySet())
        {
            SurfaceViewRenderer x = entry.getValue().getVideoView();
            if (x != null)
            {
                x.setLayoutParams(new FrameLayout.LayoutParams(viewWidth, viewHeight));
            }
        }

        Log.d(TAG, "VIDEO : resizeParticipantViews : View Height = " + viewHeight + " : View Width = " + viewWidth);
    }

    public void setRemoteMediaStream(MediaStream stream, final RemoteParticipant remoteParticipant)
    {
        final VideoTrack videoTrack = stream.videoTracks.get(0);
        videoTrack.addSink(remoteParticipant.getVideoView());
        runOnUiThread(() -> remoteParticipant.getVideoView().setVisibility(View.VISIBLE));
    }

    public boolean toggleAudioEnabled()
    {
        if (session != null)
        {
            if (session.getLocalParticipant() != null)
            {
                this.toggleTrackEnabled(session.getLocalParticipant().getAudioTrack());

                return true;
            }
        }

        return false;
    }

    public boolean toggleVideoEnabled()
    {
        if (session != null)
        {
            if (session.getLocalParticipant() != null)
            {
                this.toggleTrackEnabled(session.getLocalParticipant().getVideoTrack());

                return true;
            }
        }

        return false;
    }

    private void toggleTrackEnabled(MediaStreamTrack track)
    {
        if(track == null)
        {
            return;
        }

        track.setEnabled(!track.enabled());
    }

    public void leaveSession()
    {
        Log.d(TAG, "VIDEO : leaveSession");

        requestedVideoCallContacts.clear();

        if (this.session != null)
        {
            this.session.leaveSession();

            this.session = null;
        }

        if (this.httpClient != null)
        {
            this.httpClient.dispose();

            this.httpClient = null;
        }

        viewToDisconnectedState();
    }


    private void getToken(String sessionId)
    {
        try {
            // Session Request
            RequestBody sessionBody = RequestBody.create("{\"customSessionId\": \"" + sessionId + "\"}", MediaType.parse("application/json; charset=utf-8"));
            this.httpClient.httpCall("/openvidu/api/sessions", "POST", "application/json", sessionBody, new Callback()
            {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
                {
                    Log.d(TAG, "VIDEO : getToken responseString: " + response.body().string());

                    // Token Request
                    RequestBody tokenBody = RequestBody.create("{}", MediaType.parse("application/json; charset=utf-8"));
                    httpClient.httpCall("/openvidu/api/sessions/" + sessionId + "/connection", "POST", "application/json", tokenBody, new Callback() {

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response)
                        {
                            String responseString = null;
                            try {
                                responseString = response.body().string();
                            } catch (IOException e) {
                                Log.e(TAG, "VIDEO : getToken : Error getting body : " + e.getMessage());
                            }
                            Log.d(TAG, "VIDEO : getToken : responseString2: " + responseString);
                            JSONObject tokenJsonObject;
                            String token = null;
                            try {
                                tokenJsonObject = new JSONObject(responseString);
                                token = tokenJsonObject.getString("token");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getTokenSuccess(token, sessionId);
                        }

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e)
                        {
                            Log.e(TAG, "VIDEO : getToken : Error POST /api/tokens : " + e.getMessage());
                            connectionError();
                        }
                    });
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e)
                {
                    Log.e(TAG, "VIDEO : getToken : Error POST /api/sessions : " + e.getMessage());
                    connectionError();
                }
            });
        }
        catch (IOException e)
        {
            Log.e(TAG, "VIDEO : getToken : Error getting token : " + e.getMessage());
            e.printStackTrace();
            connectionError();
        }
    }

    private void getTokenSuccess(String token, String sessionId)
    {
        Log.d(TAG, "VIDEO : getTokenSuccess");

        // Initialize our session
        session = new Session(sessionId, token, popup_video_call.getViewsContainer(), this);
//TODO this does not work if no Session running
        // Initialize our local participant and start local camera
        String participantName = "Patient ID : " + patient_info.getHospitalPatientId();
        LocalParticipant localParticipant = new LocalParticipant(participantName, session, this.getApplicationContext(), popup_video_call.getLocalVideoView());

        Log.d(TAG, "VIDEO : getTokenSuccess : Start Camera");
//TODO disable if needed
        localParticipant.startCamera();


        Log.d(TAG, "VIDEO : getTokenSuccess : Start Web Socket");

        // Initialize and connect the Web Socket to OpenVidu Server
        startWebSocket();
    }

    private void startWebSocket()
    {
        CustomWebSocket webSocket = new CustomWebSocket(session, OPENVIDU_URL, this);
        webSocket.execute();
        session.setWebSocket(webSocket);
    }

    private void connectionError()
    {
        Log.e(TAG, "VIDEO : connectionError");

        Runnable myRunnable = () -> {
            Toast toast = Toast.makeText(this, "Error connecting to " + OPENVIDU_URL, Toast.LENGTH_LONG);
            toast.show();
            viewToDisconnectedState();
        };
        new Handler(this.getMainLooper()).post(myRunnable);
    }


    public void videoCallContactsSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentVideoCallContactSelectionList(), UserInterfacePage.VIDEO_CALL_CONTACTS);
    }


    public void videoCallContactsSelectedFromUnlockPage()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, false, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentVideoCallContactSelectionList(), UserInterfacePage.VIDEO_CALL_CONTACTS_FROM_UNLOCK_PAGE);
    }


    public void scheduleVideoCallSelected()
    {
//        current_page = UserInterfacePage.INVALID;
//        enableNavigationButtons(true, true, false);
//        setBackButtonText(getResources().getString(R.string.back));
//        setLockButtonText(getResources().getString(R.string.textLockScreen));

        //showFragment(new FragmentVideoCallModeSelection(), UserInterfacePage.VIDEO_CALL_SCHEDULE);
    }

    public void requestPatientsVideoCallContactsFromServer()
    {
        portal_system_commands.sendGatewayCommand_reportPatientSpecificVideoCallContactsFromServer();
    }

    public void patientRequestedVideoCall(ArrayList<VideoCallContact> contacts)
    {
        if (contacts.size() > 0)
        {
            requestedVideoCallContacts = contacts;

            portal_system_commands.sendGatewayCommand_patientRequestingOutgoingVideoCall(contacts);

            closeAllPopups();

            String callingText = getResources().getString(R.string.calling_hospital);
            String noAnswerText = getResources().getString(R.string.no_answer_try_again_later);
            int ringLengthInSeconds = 30;

            storeAuditTrailEvent(AuditTrailEvent.PATIENT_REQUESTED_VIDEO_CALL, gateway_user_id);

            showOutgoingVideoCallPopup(callingText, noAnswerText, ringLengthInSeconds);
        }
    }

    public void patientCancelledVideoCallRequest()
    {
        setVideoCallStatusAndReportToServer(VideoCallStatus.VIDEO_CALL_STATUS__GATEWAY_CALLING_SERVER__GATEWAY_CANCELLED);
        portal_system_commands.sendGatewayCommand_patientCancelledOutgoingVideoCallRequest();
    }

    public void setupVolumeForIncomingCallRinging()
    {
        setAndroidVolumeToFixedPercentage(1.0f);
    }

    public void setupVolumeForOutgoingCallRinging()
    {
        setAndroidVolumeToFixedPercentage(1.0f);
    }

    public void setupVolumeForActiveCall()
    {
        setAndroidVolumeToFixedPercentage(1.0f);
    }

    public void dummyUnlockCode()
    {
        portal_system_commands.sendGatewayCommand_dummyUserQrCode();
    }

    public void dummyAdminCode()
    {
        portal_system_commands.sendGatewayCommand_dummyAdminQrCode();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Permissions.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0)
            {
                boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (granted)
                {
                    Log.d(TAG, "Got Write External Settings permission, but does not take effect until UI restarted");

                    triggerApplicationRestart();
                }
            }
        }
    }

    public void triggerApplicationRestart()
    {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }


    // Copied from the Patient Gateway.
    // UI should not use these as its has its own local variables, but this is needed for if the UI crashes so the CustomErrorActivity can know the
    // Ward/Bed
    public void storeGatewayAssignedBedDetails(String bed_id, String ward_name, String bed_name)
    {
        // See http://stackoverflow.com/questions/3570690/whats-the-best-way-to-do-application-settings-in-android for more details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("gateways_assigned_bed_id", bed_id);
        editor.putString("gateways_assigned_ward_name", ward_name);
        editor.putString("gateways_assigned_bed_name", bed_name);
        editor.commit();
    }


    public void setRealTimeClientType(RealTimeServer server_type)
    {
        portal_system_commands.sendGatewayCommand_setRealtimeServerType(server_type);
    }


    public RealTimeServer getRealTimeClientType()
    {
        return features_enabled.realtime_server_type;
    }

    public void dismissWifiPopupIfVisible()
    {
        popup_wifi_status.dismissPopupIfVisible();
    }

    public void pulseImage(ImageView image)
    {
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(
                image,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        animation.setDuration(666);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setRepeatMode(ObjectAnimator.REVERSE);
        animation.setInterpolator(new FastOutSlowInInterpolator());
        animation.start();
    }

    public void footerFragmentLoaded()
    {
        getLastNetworkStatus();

        footer_fragment.setScreenBrightnessSliderPosition(getCurrentScreenBrightness());
    }


    // If true then turns on the Header showing "CAUTION : Exclusively for clinical investigation" and hides the GTIN number on the Admin page
    private final boolean non_ce_mode = false;

    public boolean inNonCeMode()
    {
        return non_ce_mode;
    }


    public void webpageSelectionSelected()
    {
        current_page = UserInterfacePage.INVALID;
        enableNavigationButtons(true, true, false);
        setBackButtonText(getResources().getString(R.string.back));
        setLockButtonText(getResources().getString(R.string.textLockScreen));

        showFragment(new FragmentWebpageSelection(), UserInterfacePage.WEBPAGE_SELECTION);
    }
    
    public void showWebpagePopup(String url)
    {
        if((popup_webpage.getDialog() == null) || (!popup_webpage.getDialog().isShowing()))
        {
            popup_webpage.setUrl(url);
            popup_webpage.show(getSupportFragmentManager(), "");
        }
        else
        {
            Log.d(TAG, "showWebpagePopup : popup already showing");
        }
    }

    public ArrayList<WebPageButtonDescriptor> getWebPageButtons()
    {
        return cached_webpages;
    }

    public void highlightFirstWordInTextView(TextView textView)
    {
        String text = textView.getText().toString();
        String firstWord = text.split(" ")[0];

        SpannableString ss = new SpannableString(text);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ss.setSpan(boldSpan, 0, firstWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
    }
}
