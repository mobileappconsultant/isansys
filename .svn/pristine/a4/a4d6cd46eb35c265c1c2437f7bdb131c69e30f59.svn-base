package com.isansys.patientgateway.database;

import java.util.concurrent.Executors;

import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.isansys.patientgateway.LocalDatabaseStorage;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.database.contentprovider.IsansysPatientGatewayContentProvider;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class DeleteOldFullySyncedSessions
{
    private final RemoteLoggingWithEnable Log;
    private final ContextInterface gateway_context_interface;

    private final String TAG = "DeleteOldFullySyncedSessions";

    private volatile boolean deletion_task_in_progress;

    private final DeletionProgressCallback deletionProgressStatus;

    private final LocalDatabaseStorage local_database_storage;

    private final PatientGatewayInterface patient_gateway_interface;
    
    public DeleteOldFullySyncedSessions(ContextInterface context_interface, RemoteLogging logger, DeletionProgressCallback deletionStatusCallBack, LocalDatabaseStorage database_storage, PatientGatewayInterface passed_patient_gateway_interface, boolean enable_logs)
    {
        gateway_context_interface = context_interface;
        Log = new RemoteLoggingWithEnable(logger, enable_logs);
        deletion_task_in_progress = false;

        local_database_storage = database_storage;

        deletionProgressStatus = deletionStatusCallBack;

        patient_gateway_interface = passed_patient_gateway_interface;
    }


    public interface DeletionProgressCallback
    {
        void setDeletionProgressStatus(boolean is_in_progress);
    }
    
    
    // Only do the delete if there is no Patient Session running. This ensures we never can have an INSERT at the same time as a DELETE
    public void deleteOldFullySyncedSessionsInBackground(boolean patient_session_running)
    {
        if (!deletion_task_in_progress)
        {
            Log.d(TAG, "deletion_task_in_progress is false so creating new AsyncTask");

            Executors.newSingleThreadExecutor().execute(() -> {
                // Update UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    deletion_task_in_progress = true;
                    deletionProgressStatus.setDeletionProgressStatus(true);
                });

                // Long running operation
                deleteOldFullySyncedData(patient_session_running);

                // Update UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    deletion_task_in_progress = false;
                    deletionProgressStatus.setDeletionProgressStatus(false);
                });
            });
        }
        else
        {
            Log.e(TAG, "deletion_task_in_progress is TRUE so NOT starting another call");
        }
    }


    private Cursor getClosedSyncedPatientSessions()
    {
        Uri uri_patient_sessions = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS;
        
        String[] projection_patient_sessions = 
        { 
             TablePatientSession.COLUMN_ID,
             TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME,
             TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED,
             TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED,
        };
        
        // Has the Session been closed
        String selection = TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME + " IS NOT 0";

        // Has the Start Patient Session been synced to the Server
        selection += " AND " + TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";

        // Has the End Patient Session been synced to the Server
        selection += " AND " + TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";

        return gateway_context_interface.getAppContext().getContentResolver().query(uri_patient_sessions, projection_patient_sessions, selection, null, null, null);
    }
    
    
    private Cursor getDeviceSessionsForPatientSessionId(String patient_session_id)
    {
        Uri uri_device_sessions = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS;

        String[] projection_device_session = { 
            TableDeviceSession.COLUMN_ID,
            TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID,
        };
        
        String selection_device_session = TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID + "=?";
        String[] selection_args_device_session = new String[1];
        selection_args_device_session[0] = patient_session_id;

        return gateway_context_interface.getAppContext().getContentResolver().query(uri_device_sessions, projection_device_session, selection_device_session, selection_args_device_session, null, null);
    }
    

    private Cursor getClosedAndSyncedDeviceSessionsForPatientSessionId(String patient_session_id)
    {
        Uri uri_device_sessions = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS;

        String[] projection_device_session = { 
            TableDeviceSession.COLUMN_ID,
            TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID,
            TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED,
            TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED,
        };
        
        // Is this for this Patient Session
        String selection = TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID + "=" + patient_session_id + " AND ";

        // Has the Session been closed
        selection += TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME + " IS NOT 0";

        // Has the Start Device Session been synced to the Server
        selection += " AND " + TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";
        
        // Has the End Device Session been synced to the Server
        selection += " AND " + TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";

        return gateway_context_interface.getAppContext().getContentResolver().query(uri_device_sessions, projection_device_session, selection, null, null, null);
    }


    private String getPatientDetailsFromSessionId(String patient_session_id)
    {
        Uri uri_patient_sessions = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS;
        String patient_details_id = "";

        String[] projection_patient_sessions =
                {
                        TablePatientSession.COLUMN_ID,
                        TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID
                };

        // Patient Session ID matches
        String selection = TablePatientSession.COLUMN_ID + " = " + patient_session_id;

        try (Cursor cursor = gateway_context_interface.getAppContext().getContentResolver().query(uri_patient_sessions, projection_patient_sessions, selection, null, null, null))
        {
            if ((cursor != null) && (cursor.getCount() == 1))
            {
                cursor.moveToFirst();

                patient_details_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID));
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "getPatientDetailsFromSessionId failed: " + e);
        }

        return patient_details_id;
    }


    private String getDeviceInfoFromSessionId(String device_session_id)
    {
        Uri uri_device_sessions = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS;
        String device_info_id = "";

        String[] projection_device_sessions =
                {
                        TableDeviceSession.COLUMN_ID,
                        TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID
                };

        // Patient Session ID matches
        String selection = TablePatientSession.COLUMN_ID + " = " + device_session_id;

        try (Cursor cursor = gateway_context_interface.getAppContext().getContentResolver().query(uri_device_sessions, projection_device_sessions, selection, null, null, null))
        {
            if ((cursor != null) && (cursor.getCount() == 1))
            {
                cursor.moveToFirst();

                device_info_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID));
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "getDeviceInfoFromSessionId failed: " + e);
        }

        return device_info_id;
    }


    private void deleteOldFullySyncedData(boolean patient_session_running)
    {
    	try
    	{
	        long startTimeAllSessions = System.nanoTime();
	        
	    	Log.e(TAG, "deleteOldFullySyncedData START : patient_session_running = " + patient_session_running);
	    	
	        // Get a list of Patient Session ID's where End Session Time is NOT null - therefore the session is closed
	    	Cursor cursor_closed_patient_sessions = getClosedSyncedPatientSessions();

            Log.e(TAG, "getClosedSyncedPatientSessions finished");

            if (cursor_closed_patient_sessions != null)
	    	{
	    		int number_of_closed_patient_sessions_to_check = cursor_closed_patient_sessions.getCount();
	    		
	    		if (number_of_closed_patient_sessions_to_check > 0)
	    		{
                    Log.e(TAG, "Checking " + number_of_closed_patient_sessions_to_check + " closed and synced Patient Session records");
	        		
	    			cursor_closed_patient_sessions.moveToFirst();
	    
	    			while(number_of_closed_patient_sessions_to_check > 0)
	        		{
                        long startTimeThisSession = System.nanoTime();

                        String android_patient_session_id_to_delete = cursor_closed_patient_sessions.getString(cursor_closed_patient_sessions.getColumnIndexOrThrow(TablePatientSession.COLUMN_ID));

                        boolean already_row = local_database_storage.patientSessionFullySyncedRowExistsForAndroidPatientSessionNumber(Integer.parseInt(android_patient_session_id_to_delete));

                        if (already_row && patient_session_running)
                        {
                            Log.d(TAG, "No point in further processing Android Patient Session " + android_patient_session_id_to_delete + " - its fully synced and cannot delete while Patient Session running");
                        }
                        else
                        {
                            // Either the Patient Session is not fully synced, or there is no Patient Session running......

                            // Get a list of all Device Session ID's for this Patient Session ID
                            Cursor cursor_device_sessions = getDeviceSessionsForPatientSessionId(android_patient_session_id_to_delete);

                            // Get a list of Device Session ID's for this (closed and fully synced) Patient Session ID
                            Cursor cursor_closed_and_synced_device_sessions = getClosedAndSyncedDeviceSessionsForPatientSessionId(android_patient_session_id_to_delete);

                            Log.e(TAG, "Android Patient Session ID = " + android_patient_session_id_to_delete + " : Device Sessions = " + cursor_device_sessions.getCount() + " : Synced Device Session info = " + cursor_closed_and_synced_device_sessions.getCount());

                            int number_of_clear_device_sessions = 0;
                            int number_of_device_session_in_patient_session;
                            int number_of_device_sessions_to_check;

                            if (cursor_device_sessions.getCount() == cursor_closed_and_synced_device_sessions.getCount())
                            {
                                // All the Device Sessions rows have been closed AND synced to the Server

                                number_of_device_sessions_to_check = number_of_device_session_in_patient_session = cursor_closed_and_synced_device_sessions.getCount();

                                Log.e(TAG, "Checking " + number_of_device_sessions_to_check + " Device Sessions to make sure all their measurement data has been synced");

                                // Verify all device sessions returned ARE in our list
                                cursor_closed_and_synced_device_sessions.moveToFirst();

                                while(number_of_device_sessions_to_check > 0)
                                {
                                    String deviceSessionIdUnderTest = cursor_closed_and_synced_device_sessions.getString(cursor_closed_patient_sessions.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));

                                    Log.d(TAG, "Checking for 'still to be sent' data in Android Patient Session = " + android_patient_session_id_to_delete + ". Android Device Session = " + deviceSessionIdUnderTest);

                                    int number_of_rows_in_device_session_data_not_sent_to_server_yet = getNumberOfUnsyncedDeviceSessionMeasurementsAndDeleteSyncedDataIfRequired(patient_session_running, deviceSessionIdUnderTest);

                                    if(number_of_rows_in_device_session_data_not_sent_to_server_yet == 0)
                                    {
                                        // All this Device Sessions data has been synced.
                                        // Do not do any deletions if a Patient Session is running. This tries to ensure that multiple database operations do not occur at the same time

                                        if (!patient_session_running)
                                        {
                                            Log.d(TAG, "     Attempting deletion of Android Device Session ID " + deviceSessionIdUnderTest);

                                            // Delete already ended patient session once all the data has already been sent
                                            int numberOfDeletedDeviceSessions = gateway_context_interface.getAppContext().getContentResolver().delete(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS, TableDeviceSession.COLUMN_ID + "=" + deviceSessionIdUnderTest + " AND " + TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1", null);

                                            if (numberOfDeletedDeviceSessions > 0)
                                            {
                                                Log.d(TAG, "     Deleted Android Device Session ID " + deviceSessionIdUnderTest);
                                            }
                                            else
                                            {
                                                Log.e(TAG, "     Could not delete Android Device Session ID " + deviceSessionIdUnderTest);
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "     NOT Attempting deletion of Android Device Session ID " + deviceSessionIdUnderTest + " as patient session running");
                                        }

                                        number_of_clear_device_sessions++;
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Cannot delete Device Session - data still pending");
                                    }

                                    number_of_device_sessions_to_check--;
                                    cursor_closed_and_synced_device_sessions.moveToNext();
                                }


                                // If all of the Device Sessions are deleted, then time to see if the Manually Entered data can be deleted, and then the Patient Session itself
                                if(number_of_clear_device_sessions == number_of_device_session_in_patient_session)
                                {
                                    Log.d(TAG, "Checking for 'still to be sent' manually entered data in Android Patient Session = " + android_patient_session_id_to_delete);

                                    int number_of_manually_entered_rows_in_patient_session_data_not_sent_to_server_yet = getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired(patient_session_running, android_patient_session_id_to_delete);

                                    if (number_of_manually_entered_rows_in_patient_session_data_not_sent_to_server_yet == 0)
                                    {
                                        // Add an entry in the Patient Session Fully Synced table in the Android database.
                                        // This will get deleted once this info has got to the Server
                                        int servers_patient_session_id = Integer.parseInt(local_database_storage.getServerPatientSessionIdFromAndroidPatientSessionId(android_patient_session_id_to_delete));
                                        int android_patient_session_id = Integer.parseInt(android_patient_session_id_to_delete);

                                        Log.d(TAG, "Creating new record in TablePatientSessionsFullySynced for Android Patient Session ID " + android_patient_session_id + " AKA ServersPatientSessionId = " + servers_patient_session_id);

                                        local_database_storage.storePatientSessionFullySynced(android_patient_session_id, servers_patient_session_id, patient_gateway_interface.getNtpTimeNowInMilliseconds());

                                        // Do not do any deletions if a Patient Session is running. This tries to ensure that multiple database operations  do not occur at the same time
                                        if (!patient_session_running)
                                        {
                                            String selection = TablePatientSession.COLUMN_ID + "=" + android_patient_session_id_to_delete + " AND " + TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";

                                            Log.d(TAG, "     Patient Session NOT running. Attempting deletion of Android Patient Session ID " + android_patient_session_id_to_delete);
                                            Log.d(TAG, "     selection = " + selection);

                                            // Delete the Patient Session now all the Device Session have been deleted
                                            int numberOfDeletedPatientSessions = gateway_context_interface.getAppContext().getContentResolver().delete(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, selection, null);

                                            if (numberOfDeletedPatientSessions == 1)
                                            {
                                                local_database_storage.decrementNumberOfUnsyncedHistoricalPatientSessions();
                                            }

                                            Log.d(TAG, "numberOfDeletedPatientSessions = " + numberOfDeletedPatientSessions);

                                            if (numberOfDeletedPatientSessions > 0)
                                            {
                                                Log.d(TAG, "Deleted Android Patient Session ID " + android_patient_session_id_to_delete);
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "     NOT Attempting deletion of fully synced and closed Android Patient Session ID " + android_patient_session_id_to_delete + " as patient session running");
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Cannot delete Patient Session - data still pending");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "Android Patient Session ID " + android_patient_session_id_to_delete + " not deleted as child data still needing syncing to server" );
                                }
                            }
                            else
                            {
                                Log.d(TAG, "Device Session(s) rows not closed or synced to Server");
                            }

                            cursor_closed_and_synced_device_sessions.close();
                            cursor_device_sessions.close();
                        }

	                    cursor_closed_patient_sessions.moveToNext();
	                    number_of_closed_patient_sessions_to_check--;

                        long endTime = System.nanoTime();
                        long duration = (endTime - startTimeThisSession);
                        long ms = duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
                        Log.e(TAG, "deleteOldFullySyncedData END (this Session). Took " + ms + " ms");

                    } // while(number_of_closed_patient_sessions_to_check > 0)
                }
	    		else
	    		{
	    		    Log.e(TAG, "No closed Patient Sessions");
	    		}
	
	            cursor_closed_patient_sessions.close();
	        }
	    	else
	    	{
	    	    Log.e(TAG, "Null cursor");
	    	}
	    	
	    	long endTime = System.nanoTime();
	    	long duration = (endTime - startTimeAllSessions);
	    	long ms = duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
	    	Log.e(TAG, "deleteOldFullySyncedData END (all sessions). Took " + ms + " ms");
    	}
    	catch(Exception e)
    	{
    		Log.e(TAG, "deleteOldFullySyncedData ERROR");
    		Log.e(TAG, "");
    		Log.e(TAG, "");
    		Log.e(TAG, e.toString());
    	}
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired(boolean patient_session_running, String patientSessionIdToDelete)
    {
        int number_of_rows_not_sent_to_server_yet = 0;

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TablePatientDetails(patient_session_running, patientSessionIdToDelete);

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredHeartRate(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredRespirationRate(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredTemperatureMeasurements(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredSpO2Measurements(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredBloodPressureMeasurements(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredWeightMeasurements(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredConsciousLevelMeasurements(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredAnnotations(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredCapillaryRefillTime(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredRespirationDistress(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredFamilyOrNurseConcern(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredSupplementalOxygenLevel(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredWeight(patient_session_running, patientSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyUrineOutput(patient_session_running, patientSessionIdToDelete);

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableEarlyWarningScores(patient_session_running, patientSessionIdToDelete);

        if(number_of_rows_not_sent_to_server_yet > 0)
        {
            Log.d(TAG, "     Android Patient Session " + patientSessionIdToDelete + " still has " + number_of_rows_not_sent_to_server_yet + " rows of manually entered data waiting to be sent to server - Cannot delete");
        }
        else
        {
            Log.d(TAG, "     Android Patient Session " + patientSessionIdToDelete + " fully synced to Server");
        }
        
        return number_of_rows_not_sent_to_server_yet;
    }


    private int getNumberOfUnsyncedDeviceSessionMeasurementsAndDeleteSyncedDataIfRequired(boolean patient_session_running, String deviceSessionIdToDelete)
    {
		int number_of_rows_not_sent_to_server_yet = 0;

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableDeviceInfo(patient_session_running, deviceSessionIdToDelete);

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchHeartBeat(patient_session_running, deviceSessionIdToDelete);
		number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchHeartRate(patient_session_running, deviceSessionIdToDelete);
		number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchRespirationRate(patient_session_running, deviceSessionIdToDelete);
		number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchSetupModeSamples(patient_session_running, deviceSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchBattery(patient_session_running, deviceSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchPatientOrientation(patient_session_running, deviceSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchRawAccelerometerModeSamples(patient_session_running, deviceSessionIdToDelete);

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetempMeasurement(patient_session_running, deviceSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetempBattery(patient_session_running, deviceSessionIdToDelete);
        
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableOximeterMeasurement(patient_session_running, deviceSessionIdToDelete);
		number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableOximeterIntermediateMeasurement(patient_session_running, deviceSessionIdToDelete);
		number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableOximeterSetupMode(patient_session_running, deviceSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableOximeterBattery(patient_session_running, deviceSessionIdToDelete);

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableBloodPressureMeasurement(patient_session_running, deviceSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableBloodPressureBattery(patient_session_running, deviceSessionIdToDelete);

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableWeightScaleMeasurement(patient_session_running, deviceSessionIdToDelete);
        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableWeightScaleBattery(patient_session_running, deviceSessionIdToDelete);

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableSetupModeLogs(patient_session_running, deviceSessionIdToDelete);

        number_of_rows_not_sent_to_server_yet += getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableConnectionEvents(patient_session_running, deviceSessionIdToDelete);

        if(number_of_rows_not_sent_to_server_yet > 0)
		{
			Log.d(TAG, "     Android Device Session " + deviceSessionIdToDelete + " still has " + number_of_rows_not_sent_to_server_yet + " rows of data waiting to be sent to server - Cannot delete");
		}
		else
		{
			Log.d(TAG, "     Android Device Session " + deviceSessionIdToDelete + " fully synced to Server");
		}
		
		return number_of_rows_not_sent_to_server_yet;
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TablePatientDetails(boolean patient_session_running, String patientSessionIdToDelete)
    {
        // look up device info ID from device session ID
        String patient_details_id = getPatientDetailsFromSessionId(patientSessionIdToDelete);

        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patient_details_id,
                IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_DETAILS,
                TablePatientDetails.COLUMN_ID,
                "TablePatientDetails");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredHeartRate(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_HEART_RATES,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredHeartRate");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredRespirationRate(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredRespirationRate");
    }
    
    
    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredTemperatureMeasurements(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredTemperature");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredSpO2Measurements(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SPO2,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredSpO2");
    }
    
    
    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredBloodPressureMeasurements(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredBloodPressure");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredWeightMeasurements(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_WEIGHT,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredWeight");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredConsciousLevelMeasurements(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredConsciousnessLevel");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredAnnotations(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredAnnotation");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredCapillaryRefillTime(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredCapillaryRefillTime");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredRespirationDistress(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_DISTRESS,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredRespirationDistress");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredFamilyOrNurseConcern(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredFamilyOrNurseConcern");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredSupplementalOxygenLevel(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredSupplementalOxygenLevel");
    }

    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyEnteredWeight(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_WEIGHT,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredWeight");
    }

    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableManuallyUrineOutput(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_URINE_OUTPUT,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableManuallyEnteredUrineOutput");
    }

    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableEarlyWarningScores(boolean patient_session_running, String patientSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                patientSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORES,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
                "TableEarlyWarningScores");
    }


    private int getNumberOfUnsyncedPatientSessionMeasurementsAndDeleteSyncedDataIfRequired_TableDeviceInfo(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        // look up device info ID from device session ID
        String device_info_id = getDeviceInfoFromSessionId(deviceSessionIdToDelete);

        return deleteOldDataBySessionIdGeneric(patient_session_running,
                device_info_id,
                IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_INFO,
                TableDeviceInfo.COLUMN_ID,
                "TableDeviceInfo");
    }


    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableSetupModeLogs(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_SETUP_MODE_LOGS,
                TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER,
                "TableSetupModeLog");
    }


    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableConnectionEvents(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_CONNECTION_EVENTS,
                TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER,
                "TableConnectionEvent");
    }


    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableBloodPressureBattery(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, 
                TableBloodPressureBattery.COLUMN_DEVICE_SESSION_NUMBER,
                "TableBloodPressureBattery");
    }
    
    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableBloodPressureMeasurement(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS, 
                TableBloodPressureMeasurement.COLUMN_DEVICE_SESSION_NUMBER,
                "TableBloodPressureMeasurement");
    }



    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableWeightScaleMeasurement(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_MEASUREMENTS,
                TableBloodPressureMeasurement.COLUMN_DEVICE_SESSION_NUMBER,
                "TableWeightScaleMeasurement");
    }

    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableWeightScaleBattery(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_BATTERY_MEASUREMENTS,
                TableBloodPressureBattery.COLUMN_DEVICE_SESSION_NUMBER,
                "TableWeightScaleBattery");
    }



    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetempBattery(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_BATTERY_MEASUREMENTS, 
                TableLifetempBattery.COLUMN_DEVICE_SESSION_NUMBER,
                "TableLifetempBattery");
    }
    
    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetempMeasurement(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_TEMPERATURE_MEASUREMENTS, 
                TableLifetempMeasurement.COLUMN_DEVICE_SESSION_NUMBER,
                "TableLifetempMeasurement");
    }
    
    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchBattery(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_BATTERY_MEASUREMENTS, 
                TableLifetouchBattery.COLUMN_DEVICE_SESSION_NUMBER,
                "TableLifetouchBattery");
    }
    
    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchPatientOrientation(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_PATIENT_ORIENTATION, 
                TableLifetouchPatientOrientation.COLUMN_DEVICE_SESSION_NUMBER,
                "TableLifetouchPatientOrientation");
    }
        
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchHeartBeat(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_BEATS,
                TableLifetouchHeartBeat.COLUMN_DEVICE_SESSION_NUMBER,
                "TableLifetouchHeartBeat");
    }
        
    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchHeartRate(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_RATES, 
                TableLifetouchHeartRate.COLUMN_DEVICE_SESSION_NUMBER,
                "TableLifetouchHeartRate");
    }
    
    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchRespirationRate(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RESPIRATION_RATES, 
                TableLifetouchRespirationRate.COLUMN_DEVICE_SESSION_NUMBER,
                "TableLifetouchRespirationRate");
    }
        
    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchSetupModeSamples(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES,
                TableLifetouchSetupModeRawSample.COLUMN_DEVICE_SESSION_NUMBER,
                "TableLifetouchSetupModeRawSample");
    }


    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableLifetouchRawAccelerometerModeSamples(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES,
                TableLifetouchRawAccelerometerModeSample.COLUMN_DEVICE_SESSION_NUMBER,
                "TableLifetouchRawAccelerometerModeSample");
    }


    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableOximeterBattery(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_BATTERY_MEASUREMENTS,
                TableOximeterBattery.COLUMN_DEVICE_SESSION_NUMBER,
                "TableOximeterBattery");
    }

    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableOximeterSetupMode(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES,
                TableOximeterSetupModeRawSample.COLUMN_DEVICE_SESSION_NUMBER,
                "TableOximeterSetupModeRawSample");
    }

    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableOximeterIntermediateMeasurement(boolean patient_session_running, String deviceSessionIdToDelete)
    {
        return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
                IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_INTERMEDIATE_MEASUREMENTS, 
                TableOximeterIntermediateMeasurement.COLUMN_DEVICE_SESSION_NUMBER,
                "TableOximeterIntermediateMeasurement");
    }
        
    
    private int getNumberOfUnsyncedMeasurementsAndDeleteSyncedDataIfRequired_TableOximeterMeasurement(boolean patient_session_running, String deviceSessionIdToDelete)
    {
		return deleteOldDataBySessionIdGeneric(patient_session_running,
                deviceSessionIdToDelete,
		        IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_MEASUREMENTS, 
		        TableOximeterMeasurement.COLUMN_DEVICE_SESSION_NUMBER,
                "TableOximeterMeasurement");
    }


    private int deleteOldDataBySessionIdGeneric(boolean patient_session_running, String sessionIdToDelete, Uri desired_uri, String TableSessionNumber, String TableNameForLogs)
    {
        String selection;
        Cursor cursor = null;
        int number_of_rows_deleted;
        int number_of_rows_not_sent_to_server = 0;
        
        try
        {
            // Query for data not sent
            String[] projection = { 
                    TableSessionNumber,
                    Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED
                };
            
            selection = TableSessionNumber + "=" + sessionIdToDelete + " AND " + Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0";

            //Log.e(TAG, desired_uri.toString() + " selection = " + selection);
            
            cursor = gateway_context_interface.getAppContext().getContentResolver().query(desired_uri, projection, selection, null, null, null);
            if(cursor != null)
            {
                number_of_rows_not_sent_to_server = cursor.getCount(); 
                if(number_of_rows_not_sent_to_server > 0)
                {
                    Log.d(TAG, "     " + TableNameForLogs + " still has " + number_of_rows_not_sent_to_server + " rows not synced to server");
                }
            }

            if (!patient_session_running)
            {
                // Perform cleaning
                selection = TableSessionNumber + "=" + sessionIdToDelete + " AND " + Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=1";

                number_of_rows_deleted = gateway_context_interface.getAppContext().getContentResolver().delete(desired_uri, selection, null);

                if (number_of_rows_deleted > 0)
                {
                    Log.d(TAG, "     " + TableNameForLogs + " rows deleted = " + number_of_rows_deleted + " (as synced to server)");
                }
            }
        }
        catch(IllegalArgumentException e)
        {
            Log.e(TAG, e.toString());
        }
        catch(Exception e)
        {
            number_of_rows_not_sent_to_server = -1;
        }
        
        if (cursor != null)
        {
            cursor.close();
        }
        
        return number_of_rows_not_sent_to_server;
    }

}
