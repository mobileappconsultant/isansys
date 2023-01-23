package com.isansys.patientgateway;

import android.os.Handler;

import com.isansys.patientgateway.remotelogging.RemoteLogging;
import com.isansys.patientgateway.serverlink.ServerSyncing;


/**
 * Handles all stages of gateway configuration, and re-configuration after a database upgrade
 *
 * Intention is for startNextStepIfPending() to be called in the gateway timer tick to trigger the
 * next step whenever one is pending. Then we can feed back the success of failure of each step to
 * move on to the next step or go back and retry (if appropriate).
 *
 *
 */
public class SetupWizard
{
    private enum SetupStage
    {
        NOT_INSTALLED,
        NTP_TIME_SYNC,
        SEND_SERVER_PING,
        GET_WARDS_AND_BEDS,
        REALTIME_SERVER_TEST,
        GET_GATEWAY_SETTINGS_FROM_SERVER,
        GET_THRESHOLDS,
        GET_SERVER_CONFIGURABLE_TEXT,
        GET_UPDATED_FIRMWARE,
        GET_WEBPAGES_IF_ENABLED,
        COMPLETE,
    }


    private enum SetupState
    {
        PENDING,
        IN_PROGRESS,
        DONE
    }


    /**
     * Member objects:
     *
     * State trackers, TAG for the class, dependencies passed in from the PatientGatewayService
     *
     */
    private SetupStage stage;
    private SetupState state;

    private final String TAG = this.getClass().getSimpleName();

    private final ServerSyncing server_syncing;
    private final Settings gateway_settings;
    private final PatientGatewayInterface gateway_interface;
    private final RemoteLogging Log;

    private final Handler step_handler;

    /**
     * Constructor for SetupWizard.
     */
    public SetupWizard(RemoteLogging parent_logger, Settings settings, ServerSyncing syncing, PatientGatewayInterface pg_interface, Handler handler)
    {
        Log = parent_logger;
        gateway_settings = settings;
        server_syncing = syncing;
        gateway_interface = pg_interface;

        if(gateway_settings.getInstallationComplete())
        {
            stage = SetupStage.COMPLETE;
        }
        else
        {
            stage = SetupStage.NOT_INSTALLED;
        }

        state = SetupState.DONE;

        step_handler = handler;
    }


    public void setNotInstalled()
    {
        Log.d(TAG, "setNotInstalled");

        stage = SetupStage.NOT_INSTALLED;
        state = SetupState.DONE;

        gateway_settings.storeInstallationComplete(false);
    }


    public void startInstallation()
    {
        Log.d(TAG, "startInstallation");

        stage = SetupStage.NTP_TIME_SYNC;
        state = SetupState.PENDING;

        gateway_settings.storeInstallationComplete(false);
    }


    public void startGetServerSettings()
    {
        Log.d(TAG, "startGetServerSettings called, stage = " + stage);

        stage = SetupStage.GET_GATEWAY_SETTINGS_FROM_SERVER;
        state = SetupState.PENDING;
    }


    private final Runnable installation_step = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(TAG, "installation_step running : " + stage);

            switch(stage)
            {
                case NTP_TIME_SYNC:
                {
                    synchroniseTime();
                }
                break;

                case SEND_SERVER_PING:
                {
                    testServerLink();
                }
                break;

                case GET_WARDS_AND_BEDS:
                {
                    getWardsAndBeds();
                }
                break;

                case REALTIME_SERVER_TEST:
                {
                    testWampLink();
                }
                break;

                case GET_GATEWAY_SETTINGS_FROM_SERVER:
                {
                    getGatewaySettings();
                }
                break;

                case GET_THRESHOLDS:
                {
                    getThresholds();
                }
                break;

                case GET_SERVER_CONFIGURABLE_TEXT:
                {
                    getServerConfigurableText();
                }
                break;

                case GET_UPDATED_FIRMWARE:
                {
                    getFirmware();
                }
                break;

                case GET_WEBPAGES_IF_ENABLED:
                {
                    getWebPages();
                }
                break;

                case COMPLETE:
                {
                    complete();
                }
                break;

                case NOT_INSTALLED:
                {
                    // doesn't make sense to be in this state. Do nothing?
                    state = SetupState.DONE;
                }
                break;
            }
        }
    };


    public boolean startNextStepIfPending()
    {
        if (state == SetupState.PENDING)
        {
            Log.d(TAG, "startNextStepIfPending : Stage = " + stage + " and state = " + state);

            state = SetupState.IN_PROGRESS;

            step_handler.post(installation_step);

            return true;
        }
        else
        {
            return false;
        }
    }


    private void testServerLink()
    {
        state = SetupState.IN_PROGRESS;

        // Ping the server
        server_syncing.sendServerPing();
    }


    public void testServerLinkSuccess()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.SEND_SERVER_PING))
        {
            // Move to next stage and set state pending
            stage = SetupStage.GET_WARDS_AND_BEDS;
            state = SetupState.PENDING;
        }
    }


    public void testServerLinkFailure()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.SEND_SERVER_PING))
        {
            // Keep current stage and go back to pending
            state = SetupState.PENDING;
        }
    }


    private void getWardsAndBeds()
    {
        state = SetupState.IN_PROGRESS;

        server_syncing.getWardDetailsListFromServer();
    }


    public void getWardsAndBedsSuccess()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_WARDS_AND_BEDS))
        {
            // Move to next stage and set state pending

            stage = SetupStage.REALTIME_SERVER_TEST;

            state = SetupState.PENDING;
        }
    }


    public void getWardsAndBedsFailure()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_WARDS_AND_BEDS))
        {
            // keep current stage and go back to pending
            state = SetupState.PENDING;
        }
    }


    private void testWampLink()
    {
        state = SetupState.IN_PROGRESS;

        gateway_interface.enableDisableRealTimeLink(true);
    }


    public void testRealtimeServerLinkSuccess()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.REALTIME_SERVER_TEST))
        {
            // move to next stage and set state pending
            stage = SetupStage.GET_GATEWAY_SETTINGS_FROM_SERVER;
            state = SetupState.PENDING;
        }
    }


    public void testRealtimeServerLinkFailure()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.REALTIME_SERVER_TEST))
        {
            // keep current stage and go back to pending
            state = SetupState.PENDING;
        }
    }


    private void synchroniseTime()
    {
        state = SetupState.IN_PROGRESS;

        gateway_interface.forceNtpTimeSync(); // ToDo: do this without an interface - but that requires refactoring the NTP code...
    }


    public void synchroniseTimeSuccess()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.NTP_TIME_SYNC))
        {
            // move to next stage and set state pending
            stage = SetupStage.SEND_SERVER_PING;
            state = SetupState.PENDING;
        }
    }


    public void synchroniseTimeFailure()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.NTP_TIME_SYNC))
        {
            // keep current stage and go back to pending
            state = SetupState.PENDING;
        }
    }


    private void getGatewaySettings()
    {
        state = SetupState.IN_PROGRESS;

        server_syncing.getGatewayConfigFromServer();
    }


    public void getGatewaySettingsSuccess()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_GATEWAY_SETTINGS_FROM_SERVER))
        {
            // move to next stage and set state pending
            stage = SetupStage.GET_THRESHOLDS;
            state = SetupState.PENDING;
        }
    }


    public void getGatewaySettingsFailure()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_GATEWAY_SETTINGS_FROM_SERVER))
        {
            // keep current stage and go back to pending
            state = SetupState.PENDING;
        }
    }


    private void getThresholds()
    {
        state = SetupState.IN_PROGRESS;

        server_syncing.sendDefaultEarlyWarningScoreTypesRequest();
    }


    public void getThresholdsSuccess()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_THRESHOLDS))
        {
            // move to next stage and set state pending
            stage = SetupStage.GET_SERVER_CONFIGURABLE_TEXT;
            state = SetupState.PENDING;
        }
    }


    public void getThresholdsFailure()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_THRESHOLDS))
        {
            // keep current stage and go back to pending
            state = SetupState.PENDING;
        }
    }


    private void getServerConfigurableText()
    {
        state = SetupState.IN_PROGRESS;

        server_syncing.getServerConfigurableTextFromServer();
    }


    public void getServerConfigurableTextSuccess()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_SERVER_CONFIGURABLE_TEXT))
        {
            // move to next stage and set state pending
            stage = SetupStage.GET_UPDATED_FIRMWARE;
            state = SetupState.PENDING;
        }
    }


    public void getServerConfigurableTextFailure()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_SERVER_CONFIGURABLE_TEXT))
        {
            // keep current stage and go back to pending
            state = SetupState.PENDING;
        }
    }


    private void getFirmware()
    {
        state = SetupState.IN_PROGRESS;

        server_syncing.sendGetLatestDeviceFirmwareVersionsRequest();
    }


    public void getFirmwareSuccess()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_UPDATED_FIRMWARE))
        {
            // move to next stage and set state pending
            if (gateway_interface.areWebPagesEnabled())
            {
                stage = SetupStage.GET_WEBPAGES_IF_ENABLED;
            }
            else
            {
                stage = SetupStage.COMPLETE;
            }

            state = SetupState.PENDING;
        }
    }


    public void getFirmwareFailure()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_UPDATED_FIRMWARE))
        {
            // keep current stage and go back to pending
            state = SetupState.PENDING;
        }
    }


    private void getWebPages()
    {
        state = SetupState.IN_PROGRESS;

        server_syncing.getWebPageDescriptorsFromServer();
    }


    public void getWebPagesSuccess()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_WEBPAGES_IF_ENABLED))
        {
            // move to next stage and set state pending
            stage = SetupStage.COMPLETE;
            state = SetupState.PENDING;
        }
    }


    public void getWebPagesFailure()
    {
        if((state == SetupState.IN_PROGRESS) && (stage == SetupStage.GET_WEBPAGES_IF_ENABLED))
        {
            // keep current stage and go back to pending
            state = SetupState.PENDING;
        }
    }


    /**
     *  Sets the wizard status to complete and enables the server/wamp link.
     *  Deliberately doesn't set gateway settings installation complete, as when this gets called
     *  *either* the user will do it by pressing the installation complete button *or* it's already
     *  true because the gateway has been automatically syncing thresholds
     */
    private void complete()
    {
        stage = SetupStage.COMPLETE;
        state = SetupState.DONE;

        gateway_interface.enableDisableRealTimeLink(true);
        gateway_interface.enableDisableServerSyncing(true);
    }
}
