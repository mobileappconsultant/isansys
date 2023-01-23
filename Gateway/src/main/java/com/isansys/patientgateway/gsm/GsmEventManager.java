package com.isansys.patientgateway.gsm;

import android.annotation.TargetApi;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import static android.telephony.TelephonyManager.DATA_ACTIVITY_DORMANT;
import static android.telephony.TelephonyManager.DATA_ACTIVITY_IN;
import static android.telephony.TelephonyManager.DATA_ACTIVITY_INOUT;
import static android.telephony.TelephonyManager.DATA_ACTIVITY_NONE;
import static android.telephony.TelephonyManager.DATA_ACTIVITY_OUT;
import static android.telephony.TelephonyManager.DATA_CONNECTED;
import static android.telephony.TelephonyManager.DATA_CONNECTING;
import static android.telephony.TelephonyManager.DATA_DISCONNECTED;
import static android.telephony.TelephonyManager.DATA_SUSPENDED;


// Special case here in that the signalStrength getLevel function needs API 23 (Android 6) but we target a lower API due to 23 requiring manual user actions to run Bluetooth (!!!!)
@TargetApi(23)
public class GsmEventManager
{
    private final String TAG = "GsmEventManager";

    private final RemoteLoggingWithEnable Log;

    private final PatientGatewayInterface patient_gateway_interface;

    public static class GsmStatus
    {
        public int data_activity_direction = DATA_ACTIVITY_NONE;
        public int data_connection_state = DATA_DISCONNECTED;
        public int network_type = TelephonyManager.NETWORK_TYPE_UNKNOWN;
        public int signal_level = 0;

        private GsmStatus()
        {

        }
    }

    private final GsmStatus gsm_status = new GsmStatus();
    private final TelephonyManager tm;


    /**
     * Initialized is done in OnCreate of PatientGatewayService
     * @param parent_context : Application Context
     */
    public GsmEventManager(Context parent_context, RemoteLogging logger, PatientGatewayInterface passed_patient_gateway_interface, boolean enable_logging)
    {
        Log = new RemoteLoggingWithEnable(logger, enable_logging);
        patient_gateway_interface = passed_patient_gateway_interface;

        tm = (TelephonyManager) parent_context.getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener myPhoneStateListener = new PhoneStateListener()
        {
            public void onDataActivity(int direction)
            {
                switch (direction)
                {
                    case DATA_ACTIVITY_NONE:
                        Log.e(TAG, "onDataActivity : DATA_ACTIVITY_NONE");
                        break;

                    case DATA_ACTIVITY_IN:
                        Log.e(TAG, "onDataActivity : DATA_ACTIVITY_IN");
                        break;

                    case DATA_ACTIVITY_OUT:
                        Log.e(TAG, "onDataActivity : DATA_ACTIVITY_OUT");
                        break;

                    case DATA_ACTIVITY_INOUT:
                        Log.e(TAG, "onDataActivity : DATA_ACTIVITY_INOUT");
                        break;

                    case DATA_ACTIVITY_DORMANT:
                        Log.e(TAG, "onDataActivity : DATA_ACTIVITY_DORMANT");
                        break;
                }

                gsm_status.data_activity_direction = direction;
                patient_gateway_interface.gsmEventHappened(gsm_status);
            }

            public void onDataConnectionStateChanged(int state, int networkType)
            {
                switch (state)
                {
                    case DATA_DISCONNECTED:
                        Log.e(TAG, "onDataConnectionStateChanged : DATA_DISCONNECTED. networkType = " + networkType);
                        break;
                    case DATA_CONNECTING:
                        Log.e(TAG, "onDataConnectionStateChanged : DATA_CONNECTING. networkType = " + networkType);
                        break;
                    case DATA_CONNECTED:
                        Log.e(TAG, "onDataConnectionStateChanged : DATA_CONNECTED. networkType = " + networkType);
                        break;
                    case DATA_SUSPENDED:
                        Log.e(TAG, "onDataConnectionStateChanged : DATA_SUSPENDED. networkType = " + networkType);
                        break;
                }

                switch (networkType)
                {
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_CDMA");
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_EDGE");
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_EVDO_0");
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_GPRS");
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_HSDPA");
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_HSPA");
                        break;
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_IDEN");
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_LTE");
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_UMTS");
                        break;
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        Log.i(TAG, "onDataConnectionStateChanged: NETWORK_TYPE_UNKNOWN");
                        break;
                    default:
                        Log.w(TAG, "onDataConnectionStateChanged: Undefined Network: " + networkType);
                        break;
                }

                gsm_status.data_connection_state = state;
                gsm_status.network_type = networkType;

                patient_gateway_interface.gsmEventHappened(gsm_status);
            }

            public void onServiceStateChanged(ServiceState serviceState) {
                Log.e(TAG, "onServiceStateChanged ; " + serviceState.toString());
            }

            public void onSignalStrengthsChanged(SignalStrength signalStrength)
            {
                Log.e(TAG, "onSignalStrengthsChanged : " + signalStrength.toString());
                Log.e(TAG, "onSignalStrengthsChanged getLevel : " + signalStrength.getLevel());

                gsm_status.signal_level = signalStrength.getLevel();

                patient_gateway_interface.gsmEventHappened(gsm_status);
            }
        };

        try
        {
            tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_DATA_ACTIVITY | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
        catch(Exception e)
        {
            Log.e(TAG, "telephone manager listen setup : " + e.toString());
        }
    }


    public boolean isTabletGsm()
    {
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }
}
