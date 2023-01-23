package com.isansys.patientgateway;

import android.os.Handler;

import com.isansys.patientgateway.remotelogging.RemoteLogging;
import com.isansys.patientgateway.serverlink.ServerSyncing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class SetupWizardTest
{

    @Mock
    RemoteLogging mock_logger;

    @Mock
    Settings mock_settings;

    @Mock
    ServerSyncing mock_server_syncing;

    @Mock
    PatientGatewayInterface mock_interface;

    @Mock
    Handler mock_handler;


    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void stateMachineInitialSetupTest()
    {
        when(mock_settings.getInstallationComplete()).thenReturn(false);

        SetupWizard test_wizard = new SetupWizard(mock_logger, mock_settings, mock_server_syncing, mock_interface, mock_handler);

        // try starting installation on a fresh wizard when not pending.
        assertEquals(test_wizard.startNextStepIfPending(), false);

        test_wizard.startInstallation();

        assertEquals(test_wizard.startNextStepIfPending(), true);
    }


    @Test
    public void stateMachineRunThrough()
    {
        when(mock_settings.getInstallationComplete()).thenReturn(false);

        SetupWizard test_wizard = new SetupWizard(mock_logger, mock_settings, mock_server_syncing, mock_interface, mock_handler);


        // start installation
        test_wizard.startInstallation();

        // check we've started - returns true when starting the next step
        assertEquals(test_wizard.startNextStepIfPending(), true);
        // check still running
        assertEquals(test_wizard.startNextStepIfPending(), false);
        // set success for the wrong step
        test_wizard.getWardsAndBedsSuccess();
        // still running
        assertEquals(test_wizard.startNextStepIfPending(), false);

        // set failure for wrong step
        test_wizard.getWardsAndBedsFailure();
        // still running
        assertEquals(test_wizard.startNextStepIfPending(), false);

        // set failure for current step
        test_wizard.synchroniseTimeFailure();
        // check testServerLink is pending again
        assertEquals(test_wizard.startNextStepIfPending(), true);

        // set success for the server link
        test_wizard.synchroniseTimeSuccess();

        // check we can start the next step - will go to GET_WARDS_AND_BEDS
        assertEquals(test_wizard.startNextStepIfPending(), true);



    }

}
