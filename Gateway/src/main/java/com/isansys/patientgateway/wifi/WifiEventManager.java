package com.isansys.patientgateway.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.format.DateUtils;

import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.Locale;

public class WifiEventManager
{
    private final String TAG = "WifiEventManager";

    private final RemoteLoggingWithEnable Log;
    private NetworkInfo network_info;

    private final PatientGatewayInterface patient_gateway_interface;

    private final WifiManager wifi_manager;

    private static String NOT_SET_YET = "";

    private static final Handler wifi_status_handler = new Handler();

    public enum WifiErrorStatus
    {
        NO_ERROR,
        NOT_CONFIGURED_ERROR,
        AUTHENTICATION_ERROR,
        UNKNOWN_SSID_ERROR,
    }

    // Wait EXECUTION_DELAY_TIME mS  before reconnection
    private static final long WIFI_OFF_TIME = 5 * DateUtils.SECOND_IN_MILLIS;

    // Every thirty seconds execute the reconnection procedure
    private static final long WIFI_RECONNECTION_TIME = 60 * DateUtils.SECOND_IN_MILLIS;

    /**
     * Gateway wifi status. Currently Wifi_failure_reason reports only "Authentication Error"
     */
    public static class WifiStatus
    {
        public boolean hardware_enabled = false;
        public boolean connected_to_ssid = false;
        public String ssid = NOT_SET_YET;
        public String ip_address_string = NOT_SET_YET;
        public int wifi_level = 0;

        public String wifi_Status = NOT_SET_YET;
        public String wifi_BSSID = NOT_SET_YET;
        public WifiErrorStatus wifi_error_status = WifiErrorStatus.NO_ERROR;

        @Override
        public String toString()
        {
            return "hardware_enabled : " + hardware_enabled +
                    "  connected_to_ssid : " + connected_to_ssid +
                    "  ssid : " + ssid +
                    "  ip_address_string : " + ip_address_string +
                    "  wifi_level : " + wifi_level +
                    "  wifi_Status : " + wifi_Status +
                    "  wifi_BSSID : " + wifi_BSSID +
                    "  wifi_error_status : " + wifi_error_status;
        }
    }

    public final WifiStatus wifi_status = new WifiStatus();

    /**
     * Initialized is done in OnCreate of PatientGatewayService
     */
    public WifiEventManager(Context context, RemoteLogging logger, PatientGatewayInterface patient_gateway_interface, boolean enable_logging)
    {
        Log = new RemoteLoggingWithEnable(logger, enable_logging);

        wifi_manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        this.patient_gateway_interface = patient_gateway_interface;

        NOT_SET_YET = this.patient_gateway_interface.getNotSetYetString();

        context.registerReceiver(wifi_status_receiver, wifiIntentFilter());

        turnWifiOffAndOn(true);
    }


    /**
     * Wifi status change intents
     * @return intentFilter
     */
    public IntentFilter wifiIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        return intentFilter;
    }

    /**
     * Broadcast receiver for wifi status receiver
     */
    public final BroadcastReceiver wifi_status_receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            switch (action)
            {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                {
                    Log.d(TAG, "wifi_status_receiver, Intent = NETWORK_STATE_CHANGED_ACTION");

                    network_info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    Log.d(TAG, "wifi_status_receiver : network_info " + network_info.getDetailedState().toString());

                    String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
                    Log.d(TAG, "wifi_status_receiver : bssid " + bssid);

                    NetworkInfo.DetailedState detailedInfo = network_info.getDetailedState();

                    Log.d(TAG, "checkNetworkInfo = " + detailedInfo);

                    if (detailedInfo == NetworkInfo.DetailedState.DISCONNECTED)
                    {
                        turnWifiOffAndOn(false);
                    }
                }
                break;

                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                {
                    Log.d(TAG, "wifi_status_receiver, Intent = WIFI_STATE_CHANGED_ACTION");

                    int wifi_current_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

                    int wifi_previous_state = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, -1);

                    Log.d(TAG, "wifi_current_state = " + logCheckWifiChangedState(wifi_current_state));
                    Log.d(TAG, "wifi_previous_state = " + logCheckWifiChangedState(wifi_previous_state));

                    if (wifi_current_state == WifiManager.WIFI_STATE_DISABLED)
                    {
                        if (wifi_manager.isWifiEnabled() == false)
                        {
                            Log.d(TAG, "logCheckWifiChangedState : calling startWifiReconnectionAfterTwoSeconds");
                            turnWifiOffAndOn(false);
                        }
                    }
                }
                break;

                //Broadcast intent action indicating that the state of establishing a connection to an access point has changed.
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                {
                    //Broadcast intent action indicating that the state of establishing a connection to an access point has changed.
                    // One extra provides the new SupplicantState.
                    // Note that the supplicant state is Wi-Fi specific, and is not generally the most useful thing to look at if you are just interested in the overall state of connectivity.
                    Log.d(TAG, "wifi_status_receiver, Intent = SUPPLICANT_STATE_CHANGED_ACTION");

                    SupplicantState mSupplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                    Log.d(TAG, "SUPPLICANT_STATE_CHANGED_ACTION : SupplicantState = " + mSupplicantState.name());

                    int error_authenticating = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                    Log.d(TAG, "SUPPLICANT_STATE_CHANGED_ACTION : SupplicantState = " + error_authenticating);

                    if (mSupplicantState == SupplicantState.DISCONNECTED)
                    {
                        Log.d(TAG, "SupplicantState.DISCONNECTED");

                        // Find the reason for disconnection
                        if (error_authenticating == 1)
                        {
                            Log.d(TAG, "SUPPLICANT_STATE_CHANGED_ACTION : Authentication Error = " + error_authenticating);

                            wifiStatusErrorHandler(WifiErrorStatus.AUTHENTICATION_ERROR);
                        }
                    }
                    else
                    {
                        if (mSupplicantState == SupplicantState.COMPLETED)
                        {
                            Log.d(TAG, "SupplicantState.COMPLETED");

                            cancelReconnectionRunnable();
                            wifi_status.wifi_error_status = WifiErrorStatus.NO_ERROR;
                            quick_reset_pending = false;
                        }
                    }
                }
                break;

                // Broadcast intent action indicating that a connection to the supplicant has been established (and it is now possible to perform Wi-Fi operations)
                // or the connection to the supplicant has been lost
                case WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION:
                {
                    // The lookup key for a boolean that indicates whether a connection to the supplicant daemon has been gained or lost. true means a connection now exists.
                    boolean isConnected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);

                    Log.d(TAG, "wifi_status_receiver, SUPPLICANT_CONNECTION_CHANGE_ACTION : isConnected = " + isConnected);
                }
                break;
            }

            checkAndSetWifiStatus();
        }
    };


    private String logCheckWifiChangedState(int wifi_state)
    {
        switch(wifi_state)
        {
            case WifiManager.WIFI_STATE_DISABLING:
                return "WIFI_STATE_DISABLING";

            case WifiManager.WIFI_STATE_DISABLED:
                return "WIFI_STATE_DISABLED";

            case WifiManager.WIFI_STATE_ENABLED:
                return "WIFI_STATE_ENABLED";

            case WifiManager.WIFI_STATE_ENABLING:
                return "WIFI_STATE_ENABLING";

            case WifiManager.WIFI_STATE_UNKNOWN:
                return "WIFI_STATE_UNKNOWN";
        }

        return "NO CASE STATEMENT";
    }


    private boolean quick_reset_pending = false;


    private final Runnable wifi_reconnection_runnable = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(TAG, "wifi_reconnection_runnable");

            boolean in_gsm_mode = patient_gateway_interface.inGsmOnlyMode();

            if(wifi_manager.isWifiEnabled() == false)
            {
                if (in_gsm_mode)
                {
                    // In GSM mode. Dont bother re-enabling Wifi
                    Log.e(TAG, "wifi_reconnection_runnable : In GSM mode - ignoring Wifi enable request");

                }
                else
                {
                    Log.d(TAG, "wifi_reconnection_runnable : wifi is being enabled");
                    wifi_manager.setWifiEnabled(true);
                }
            }

            quick_reset_pending = false;

            if (in_gsm_mode == false)
            {
                turnWifiOffAndOnAfterDelayIfNothingPending();
            }
        }
    };

    /**
     * Start reconnection runnable in handler after EXECUTION_DELAY_TIME delay.
     * Wifi is disabled and re-enabled
     */
    public void turnWifiOffAndOn(boolean force_turn_off)
    {
        if(quick_reset_pending == false)
        {
            Log.d(TAG, "turnWifiOffAndOn : Executing wifi reconnection procedure");

            quick_reset_pending = true;

            if ((wifi_manager.isWifiEnabled() == true) || (force_turn_off))
            {
                wifi_manager.setWifiEnabled(false);
            }

            wifi_status.hardware_enabled = false;
            wifi_status.connected_to_ssid = false;
            wifi_status.ssid = NOT_SET_YET;
            wifi_status.ip_address_string = NOT_SET_YET;
            wifi_status.wifi_level = 0;

            wifi_status.wifi_Status = NOT_SET_YET;
            wifi_status.wifi_BSSID = NOT_SET_YET;

            cancelReconnectionRunnable();

            wifi_status.wifi_error_status = WifiErrorStatus.NO_ERROR;

            wifi_status_handler.postDelayed(wifi_reconnection_runnable, WIFI_OFF_TIME);
        }
        else
        {
            Log.d(TAG, "turnWifiOffAndOn : turn off already pending");
        }
    }


    /**
     * Check if WifiInfo and NetworkInfo are null. WifiInfo and NetworkInfo are null at the start of Gateway.
     */
    // CMD_GET_WIFI_STATUS or wifi_status_receiver
    public void checkAndSetWifiStatus()
    {
        WifiInfo wifi_info = wifi_manager.getConnectionInfo();

        wifi_status.hardware_enabled = wifi_manager.isWifiEnabled();

        if((wifi_info != null) && (network_info != null))
        {
            wifi_status.connected_to_ssid = network_info.isConnected();
            wifi_status.wifi_Status = network_info.getDetailedState().toString();

            // Returns the service set identifier (SSID) of the current 802.11 network.
            // If the SSID can be decoded as UTF-8, it will be returned surrounded by double quotation marks.
            // Otherwise, it is returned as a string of hex digits. The SSID may be <unknown ssid> if there is no network currently connected.
            wifi_status.ssid = wifi_info.getSSID();
            if(wifi_status.ssid.equals("<unknown ssid>") || wifi_status.ssid.equals(""))
            {
                wifi_status.ssid = NOT_SET_YET;
            }

            //Return the basic service set identifier (BSSID) of the current access point. The BSSID may be null if there is no network currently connected.
            wifi_status.wifi_BSSID = wifi_info.getBSSID();
            if(wifi_status.wifi_BSSID == null)
            {
                wifi_status.wifi_BSSID = NOT_SET_YET;
            }
            else
            {
                if(wifi_status.wifi_BSSID.equals("00:00:00:00:00:00"))
                {
                    wifi_status.wifi_BSSID = NOT_SET_YET;
                }
            }

            // Returns the Access point ssid for valid connection. If not returns Zero.
            int ip_address = wifi_info.getIpAddress();
            if(ip_address == 0)
            {
                wifi_status.ip_address_string = NOT_SET_YET;
            }
            else
            {
                wifi_status.ip_address_string = String.format(Locale.getDefault(), "%d.%d.%d.%d", (ip_address & 0xff),(ip_address >> 8 & 0xff),(ip_address >> 16 & 0xff),(ip_address >> 24 & 0xff));
            }

            // Has the Wifi signal strength changed since last time
            int number_of_signal_levels = 5;
            int current_wifi_level = WifiManager.calculateSignalLevel(wifi_info.getRssi(), number_of_signal_levels);
            Log.d(TAG, "wifi_status_receiver : WIFI changed, Signal Level changed : " + current_wifi_level);
            wifi_status.wifi_level = current_wifi_level;

            if((wifi_status.connected_to_ssid == false) && (wifi_status.ssid.equals(NOT_SET_YET)) && (wifi_status.wifi_Status.equals(NetworkInfo.DetailedState.DISCONNECTED.toString())))
            {
                // If not in authentication error then change to unknown SSID
                if(wifi_status.wifi_error_status != WifiErrorStatus.AUTHENTICATION_ERROR)
                {
                    wifiStatusErrorHandler(WifiErrorStatus.UNKNOWN_SSID_ERROR);
                }
            }
        }
        else
        {
            Log.d(TAG, "checkAndSetWifiStatus : WifiInfo and Network info isn't set");
            wifiStatusErrorHandler(WifiErrorStatus.NOT_CONFIGURED_ERROR);
        }

        tellGatewayNewWifiStatus();
    }


    private void tellGatewayNewWifiStatus()
    {
        patient_gateway_interface.wifiEventHappened(wifi_status);
    }


    private void wifiStatusErrorHandler(WifiErrorStatus error_status)
    {
        Log.d(TAG, "wifiStatusErrorHandler : wifi_error_status = " + error_status.toString());

        // If new state is updated then only start the reconnection process
        if(error_status != wifi_status.wifi_error_status)
        {
            turnWifiOffAndOn(false);
        }
        else
        {
            // Previous error state isn't changed. So timer is still ticking.
            Log.d(TAG, "wifiStatusErrorHandler : previous error state isn't changed. reconnection runnable still pending");
        }

        wifi_status.wifi_error_status = error_status;
    }


    private void turnWifiOffAndOnAfterDelayIfNothingPending()
    {
        if(quick_reset_pending == false)
        {
            cancelReconnectionRunnable();

            wifi_status_handler.postDelayed(() -> turnWifiOffAndOn(false), WIFI_RECONNECTION_TIME);
        }
    }


    public void cancelReconnectionRunnable()
    {
        Log.d(TAG, "cancelReconnectionRunnable");
        // Removing previous runnable of wifi disconnection. Avoid multiple execution.
        wifi_status_handler.removeCallbacksAndMessages(null);
    }


    public void setWifiEnabled(boolean enabled)
    {
        wifi_manager.setWifiEnabled(enabled);
    }
}
