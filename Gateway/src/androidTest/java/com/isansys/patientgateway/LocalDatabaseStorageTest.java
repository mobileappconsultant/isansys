package com.isansys.patientgateway;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.isansys.common.PatientInfo;
import com.isansys.common.ThresholdSet;
import com.isansys.common.ThresholdSetAgeBlockDetail;
import com.isansys.patientgateway.database.DeleteOldFullySyncedSessions;
import com.isansys.patientgateway.database.contentprovider.IsansysPatientGatewayContentProvider;
import com.isansys.patientgateway.remotelogging.RemoteLogging;
import com.isansys.patientgateway.serverlink.ServerSyncStatus;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.mockito.Mockito.when;

/**
 * Created by Rory on 13/04/2018.
 */

@RunWith(AndroidJUnit4.class)
public class LocalDatabaseStorageTest
{
    @Mock
    private ContextInterface context_interface;

    @Mock
    RemoteLogging logger;

    @Mock
    PatientGatewayInterface mock_interface;

    @Mock
    ServerSyncStatus mock_sync_status;


    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        when(context_interface.getAppContext()).thenReturn(getInstrumentation().getTargetContext());
    }

    @After
    public void tearDown()
    {

    }

    @Test
    public void test_patientDetailsAndSessions()
    {
        final LocalDatabaseStorage test_storage = new LocalDatabaseStorage(context_interface, logger, new DeleteOldFullySyncedSessions.DeletionProgressCallback()
        {
            @Override
            public void setDeletionProgressStatus(boolean is_in_progress)
            {

            }
        }, mock_interface, true, mock_sync_status);

        try
        {
            String currentDBPath = "data/data/com.isansys.patientgateway/databases/test_gateway.db";

            InputStream input = getInstrumentation().getTargetContext().getResources().getAssets().open("fresh_database_v91.db");

            OutputStream dst = new FileOutputStream(currentDBPath);

            streamToStream(input, dst);

            input.close();
            dst.close();
        }
        catch(IOException e)
        {
            System.out.println("testCheckForSetupModePending failed: could not copy test database to tablet.");
        }


        context_interface.getAppContext().getContentResolver().call(IsansysPatientGatewayContentProvider.CONTENT_URI, IsansysPatientGatewayContentProvider.CALLABLE_METHOD__OPEN_TEST_DATABASE, "test_gateway.db", null);


        /* Patient Details */

        RowsPending rp = test_storage.checkIfPatientDetailsPendingToBeSentToServer();

        Assert.assertTrue(rp.getTotalRowsPending() == 0);

        Assert.assertTrue(rp.rows_pending_but_failed == 0);
        Assert.assertTrue(rp.rows_pending_non_syncable == 0);
        Assert.assertTrue(rp.rows_pending_syncable == 0);

        PatientInfo patientInfo = new PatientInfo();
        patientInfo.setHospitalPatientId("test_name");

        ThresholdSet thresholdSet = new ThresholdSet();
        thresholdSet.servers_database_row_id = 123;
        patientInfo.setThresholdSet(thresholdSet);

        ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail = new ThresholdSetAgeBlockDetail();
        thresholdSetAgeBlockDetail.servers_database_row_id = 456;
        patientInfo.setThresholdSetAgeBlockDetails(thresholdSetAgeBlockDetail);

        final int details_id = test_storage.insertPatientDetails(1, patientInfo, 1, 123456);
        Assert.assertTrue(details_id == 1); // details ID should be 1, i.e. the first row in the table.

        rp = test_storage.checkIfPatientDetailsPendingToBeSentToServer();

        Assert.assertTrue(rp.getTotalRowsPending() == 1);

        Assert.assertTrue(rp.rows_pending_but_failed == 0);
        Assert.assertTrue(rp.rows_pending_non_syncable == 0);
        Assert.assertTrue(rp.rows_pending_syncable == 1);

        PatientInfo test_info = new PatientInfo();

        test_storage.getPatientDetails(details_id, test_info);

        Assert.assertEquals("test_name", test_info.getHospitalPatientId());

        // need to run the database updates on a thread with a Looper - so using runOnMainSync
        getInstrumentation().runOnMainSync(new Runnable()
        {
            @Override
            public void run()
            {
                test_storage.patientDetailsFailedSendingToServer(details_id); // uses the Patient Details column ID, which is returned by the insert above.
            }
        });

        try
        {
            // wait to allow the operation to complete
            Thread.sleep(1000);
        }
        catch(InterruptedException e)
        {
            System.out.println(e.toString());
        }

        rp = test_storage.checkIfPatientDetailsPendingToBeSentToServer();

        Assert.assertTrue(rp.getTotalRowsPending() == 1);

        Assert.assertTrue(rp.rows_pending_but_failed == 1);  // should now be failed
        Assert.assertTrue(rp.rows_pending_non_syncable == 0);
        Assert.assertTrue(rp.rows_pending_syncable == 0    );


        final int dummy_server_id__patient_details = 2;

        getInstrumentation().runOnMainSync(new Runnable()
        {
            @Override
            public void run()
            {
                test_storage.resetFailedToSendStatus();

                test_storage.patientDetailsSuccessfullySentToServer(details_id, dummy_server_id__patient_details); // uses the Patient Details column ID, which is returned by the insert above.
            }
        });

        try
        {
            // wait to allow the operation to complete
            Thread.sleep(1000);
        }
        catch(InterruptedException e)
        {
            System.out.println(e.toString());
        }

        rp = test_storage.checkIfPatientDetailsPendingToBeSentToServer();

        // shouldn't be any pending rows now.
        Assert.assertTrue(rp.getTotalRowsPending() == 0);

        Assert.assertTrue(rp.rows_pending_but_failed == 0);
        Assert.assertTrue(rp.rows_pending_non_syncable == 0);
        Assert.assertTrue(rp.rows_pending_syncable == 0);







        /* Patient Session */
        rp = test_storage.checkIfStartPatientSessionPendingToBeSentToServer();

        Assert.assertTrue(rp.getTotalRowsPending() == 0);

        Assert.assertTrue(rp.rows_pending_but_failed == 0);
        Assert.assertTrue(rp.rows_pending_non_syncable == 0);
        Assert.assertTrue(rp.rows_pending_syncable == 0);

        int session_id = test_storage.insertPatientSession(details_id, 123456, 1);

        rp = test_storage.checkIfStartPatientSessionPendingToBeSentToServer();

        Assert.assertTrue(rp.getTotalRowsPending() == 1);

        Assert.assertTrue(rp.rows_pending_but_failed == 0);
        Assert.assertTrue(rp.rows_pending_non_syncable == 0); // patient session is non-syncable, as the patient details isn't synced.
        Assert.assertTrue(rp.rows_pending_syncable == 1);

        patientInfo.setHospitalPatientId("test_name_two");
        final int unsynced_details_id = test_storage.insertPatientDetails(2, patientInfo, 1, 234567);

        int second_session_id = test_storage.insertPatientSession(unsynced_details_id, 234567, 1);


        rp = test_storage.checkIfStartPatientSessionPendingToBeSentToServer();

        Assert.assertTrue(rp.getTotalRowsPending() == 2);

        Assert.assertTrue(rp.rows_pending_but_failed == 0);
        Assert.assertTrue(rp.rows_pending_non_syncable == 1); // patient session is non-syncable, as the patient details isn't synced.
        Assert.assertTrue(rp.rows_pending_syncable == 1);
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

}
