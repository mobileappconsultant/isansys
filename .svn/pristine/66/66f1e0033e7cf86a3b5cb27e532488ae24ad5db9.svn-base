package com.isansys.patientgateway;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import android.text.format.DateUtils;

import com.isansys.common.DeviceSession;
import com.isansys.common.PatientInfo;
import com.isansys.common.ThresholdSetAgeBlockDetail;
import com.isansys.common.WebPageDescriptor;
import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.AuditTrailEvent;
import com.isansys.common.enums.DeviceConnectionStatus;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.HttpOperationType;
import com.isansys.common.enums.QueryType;
import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.MeasurementAnnotation;
import com.isansys.common.measurements.MeasurementBatteryReading;
import com.isansys.common.measurements.MeasurementBloodPressure;
import com.isansys.common.measurements.MeasurementCapillaryRefillTime;
import com.isansys.common.measurements.MeasurementConsciousnessLevel;
import com.isansys.common.measurements.MeasurementFamilyOrNurseConcern;
import com.isansys.common.measurements.MeasurementHeartRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredBloodPressure;
import com.isansys.common.measurements.MeasurementManuallyEnteredHeartRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredRespirationRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredSpO2;
import com.isansys.common.measurements.MeasurementManuallyEnteredTemperature;
import com.isansys.common.measurements.MeasurementManuallyEnteredWeight;
import com.isansys.common.measurements.MeasurementPatientOrientation;
import com.isansys.common.measurements.MeasurementRespirationDistress;
import com.isansys.common.measurements.MeasurementRespirationRate;
import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.common.measurements.MeasurementSupplementalOxygenLevel;
import com.isansys.common.measurements.MeasurementTemperature;
import com.isansys.common.measurements.MeasurementUrineOutput;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.MeasurementWeight;
import com.isansys.patientgateway.PatientGatewayService.RawAccelerometerModeDataPoint;
import com.isansys.patientgateway.database.AsyncDatabaseUpdater;
import com.isansys.patientgateway.database.DeleteOldFullySyncedSessions;
import com.isansys.patientgateway.database.RowRange;
import com.isansys.patientgateway.database.RowTracker;
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
import com.isansys.patientgateway.database.TableServerConfigurableText;
import com.isansys.patientgateway.database.TableSetupModeData;
import com.isansys.patientgateway.database.TableSetupModeLog;
import com.isansys.patientgateway.database.TableThresholdSet;
import com.isansys.patientgateway.database.TableThresholdSetAgeBlockDetail;
import com.isansys.patientgateway.database.TableThresholdSetColour;
import com.isansys.patientgateway.database.TableThresholdSetLevel;
import com.isansys.patientgateway.database.TableWards;
import com.isansys.patientgateway.database.TableViewableWebPageDetails;
import com.isansys.patientgateway.database.TableWeightScaleBattery;
import com.isansys.patientgateway.database.TableWeightScaleWeight;
import com.isansys.patientgateway.database.contentprovider.IsansysPatientGatewayContentProvider;
import com.isansys.patientgateway.databaseToJsonMap.CommonManuallyEnteredMeasurementTableInfo;
import com.isansys.patientgateway.databaseToJsonMap.CommonSensorMeasurementTableInfo;
import com.isansys.patientgateway.databaseToJsonMap.DatabaseToJson;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.patientgateway.remotelogging.RemoteLogging;
import com.isansys.patientgateway.serverlink.ServerLink;
import com.isansys.patientgateway.serverlink.ServerSyncStatus;
import com.isansys.patientgateway.serverlink.ServerSyncing;
import com.isansys.patientgateway.serverlink.constants.CheckServerLinkStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalDatabaseStorage
{
    private final RemoteLoggingWithEnable Log;
    private final ContextInterface gateway_context_interface;
    private final String TAG = "LocalDatabaseStorage";
    
    private int number_of_rows_per_json_message = 2000;

    private final ObjectMapper mapper = new ObjectMapper();

    private final DeleteOldFullySyncedSessions delete_old_fully_synced_sessions;

    private final ContentResolver content_resolver;

    private final PatientGatewayInterface patient_gateway_interface;

    private ServerSyncing server_syncing;

    private String device_id_for_audit_trail = "DEVICE_ID_UNKNOWN";
    private int last_known_user_id = -1;
    private final int AUDIT_OPTION_NOT_APPLICABLE = -1;

    private int number_of_unsynced_historical_patient_sessions;

    private final ServerSyncStatus server_sync_status;

    public LocalDatabaseStorage(ContextInterface context_interface,
                                RemoteLogging logger,
                                DeleteOldFullySyncedSessions.DeletionProgressCallback deletionStatusCallBack,
                                PatientGatewayInterface patient_gateway_interface,
                                boolean enable_logs,
                                ServerSyncStatus sync_status)
    {
        gateway_context_interface = context_interface;
        Log = new RemoteLoggingWithEnable(logger, enable_logs);
        delete_old_fully_synced_sessions = new DeleteOldFullySyncedSessions(context_interface, logger, deletionStatusCallBack, this, patient_gateway_interface, enable_logs);

        content_resolver = gateway_context_interface.getAppContext().getContentResolver();

        this.patient_gateway_interface = patient_gateway_interface;

        number_of_unsynced_historical_patient_sessions = getNumberOfUnsyncedHistoricalPatientSessionInDatabase();
        server_sync_status = sync_status;
    }


    public void setServerSyncing(ServerSyncing desired_server_syncing)
    {
        server_syncing = desired_server_syncing;
    }

    
    public void setNumberOfRowsPerJsonMessage(int desired_number_of_rows)
    {
        Log.d(TAG, "setNumberOfRowsPerJsonMessage = " + desired_number_of_rows);

        number_of_rows_per_json_message = desired_number_of_rows;
    }


    public void setAndroidUniqueDeviceIdForAuditTrail(String android_id)
    {
        Log.d(TAG, "setAndroidUniqueDeviceIdForAuditTrail = " + android_id);

        device_id_for_audit_trail = android_id;
    }

    @NonNull
    private ContentValues createSensorMeasurementContentValues(DeviceInfo device_info, MeasurementVitalSign measurement)
    {
        ContentValues content_values = new ContentValues();

        content_values.put(Table.COLUMN_HUMAN_READABLE_DEVICE_ID, device_info.human_readable_device_id);
        content_values.put(Table.COLUMN_DEVICE_SESSION_NUMBER, device_info.getAndroidDeviceSessionId());

        return getContentValuesCommon(measurement, content_values);
    }


    @NonNull
    private ContentValues createManuallyEnteredMeasurementContentValues(int patient_session_number, MeasurementVitalSign measurement, int by_user_id)
    {
        ContentValues content_values = new ContentValues();

        content_values.put(Table.COLUMN_PATIENT_SESSION_NUMBER, patient_session_number);
        content_values.put(Table.COLUMN_BY_USER_ID, by_user_id);

        return getContentValuesCommon(measurement, content_values);
    }


    private ContentValues getContentValuesCommon(MeasurementVitalSign measurement, ContentValues content_values)
    {
        content_values.put(Table.COLUMN_TIMESTAMP, measurement.timestamp_in_ms);
        content_values.put(Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);
        content_values.put(Table.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP, getTimeNow());
        content_values.put(Table.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS, measurement.measurement_validity_time_in_seconds);

        return content_values;
    }


    private void logQueryInfo(String function_name, ArrayList<RowRange> row_ranges, int json_array_length)
    {
        for (RowRange row_range : row_ranges)
        {
            Log.d(TAG, function_name + " : Sending Local Rows " + row_range.start + " to " + row_range.end + " json_array_length = " + json_array_length);
        }
    }


    public void deleteOldFullySyncedSessions(boolean patient_session_running)
    {
        delete_old_fully_synced_sessions.deleteOldFullySyncedSessionsInBackground(patient_session_running);
    }


    private int insertContentValuesAtContentUri(Uri content_uri, ContentValues content_values)
    {
        try
        {
            int database_row_id = -1;

            Uri uri = content_resolver.insert(content_uri, content_values);

            if (uri != null)
            {
                String database_row_id_as_string = uri.toString().substring(uri.toString().lastIndexOf('/') + 1);
                database_row_id = Integer.parseInt(database_row_id_as_string);
            }

            return database_row_id;
        }
        catch (Exception e)
        {
            Log.e(TAG, "insertContentValuesAtContentUri Exception\n" + e);
            return 0;
        }
    }
    
    private int database_inset_count = 0;
    private void asyncInsertContentValuesAtContentUri(Uri content_uri, ContentValues content_values)
    {
        try
        {
            Object start_database_insert_time = System.nanoTime();
            database_inset_count++;
            AsyncDatabaseUpdater temp_query_handler = new AsyncDatabaseUpdater(content_resolver, Log);
	        temp_query_handler.startInsert(database_inset_count, start_database_insert_time, content_uri, content_values);
        }
        catch (Exception e)
        {
            Log.e(TAG, "asyncInsertContentValuesAtContentUri Exception\n" + e);
        }
    }

    // Count the number of bulk insert task running. Initial value is 0
    private final AtomicInteger number_of_bulk_insert_async_task = new AtomicInteger();

    public interface BulkInsertCallBack
    {
        void onInsertFinish(int update_row);
    }


    private void doBulkInsertInBackground(Uri uri, ContentValues[] contentValues, BulkInsertCallBack bulkInsertCallBack)
    {
        Executors.newSingleThreadExecutor().execute(() -> {

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                number_of_bulk_insert_async_task.incrementAndGet();
                Log.d(TAG, "BulkInsertTask : onPreExecute. URI = " + uri.toString() + ". Number of Bulk Insert task currently running = " + number_of_bulk_insert_async_task);
            });

            // Long running operation
            final long startTimeInMillisecond = patient_gateway_interface.getNtpTimeNowInMilliseconds();

            Log.d(TAG, "BulkInsertTask : " + uri + " : Total Time Pending in milliseconds = " + (patient_gateway_interface.getNtpTimeNowInMilliseconds() - startTimeInMillisecond));

            int updated_row = 0;
            try
            {
                updated_row = content_resolver.bulkInsert(uri, contentValues);
            }
            catch (Exception e)
            {
                Log.e(TAG, "bulkInsertContentValuesAtContentUri Exception\n" + e);
            }

            Log.d(TAG, "BulkInsertTask : doInBackground.  URI = " + uri.toString() + ".  Updated row = " + updated_row);

            // Make "Final" copy of the variable so it can be access in the UI thread
            final int finalUpdatedRows = updated_row;

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                number_of_bulk_insert_async_task.decrementAndGet();
                Log.d(TAG, "BulkInsertTask : onPostExecute, Number of AsyncTaskRunning = " + number_of_bulk_insert_async_task);
                Log.d(TAG, "BulkInsertTask : " + uri + " : Total Time of Execution in milliseconds = " + (patient_gateway_interface.getNtpTimeNowInMilliseconds() - startTimeInMillisecond) + " to write " + finalUpdatedRows + " rows");

                bulkInsertCallBack.onInsertFinish(finalUpdatedRows);
            });
        });
    }

    private void bulkInsertContentValuesAtContentUri(Uri content_uri, ContentValues[] content_values, BulkInsertCallBack mBulInsertCallBack)
    {
        doBulkInsertInBackground(content_uri, content_values, mBulInsertCallBack);
    }
        
    private int database_update_count = 0;
    
    private void updateContentsLowLevel(Uri content_uri, ContentValues args, String strFilter)
    {
        try
        {
            Object start_database_insert_time = System.nanoTime();                      // Keep track of time the AsyncDatabaseUpdater starts at so we can see how long it takes to run
            database_update_count++;

            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }

            AsyncDatabaseUpdater temp_query_handler = new AsyncDatabaseUpdater(content_resolver, Log);
            temp_query_handler.startUpdate(database_update_count, start_database_insert_time, content_uri, args, strFilter, null);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception:\n" + e);
        }
    }

    
    private void updateContentValuesAtContentUri(Uri content_uri, String database_table, int database_row_id, int value)
    {
        String strFilter = Table.COLUMN_ID + "=" + database_row_id;

        ContentValues args = new ContentValues();
        args.put(database_table, value);

        updateContentsLowLevel(content_uri, args, strFilter);
    }
    

    private void markRowAsSuccessfullySentToServer(Uri content_uri, String database_table_sent_to_server, String database_table_of_ack_received_timestamp, int database_row_id)
    {
        String strFilter = Table.COLUMN_ID + "=" + database_row_id;

        ContentValues args = new ContentValues();
        args.put(database_table_sent_to_server, true);
        args.put(database_table_of_ack_received_timestamp, getTimeNow());
        
        updateContentsLowLevel(content_uri, args, strFilter);
    }


    private void markRowAsFailedSendingToServer(Uri content_uri, String failed_database_table, String failed_database_table_timestamp, int database_row_id)
    {
        String strFilter = Table.COLUMN_ID + "=" + database_row_id;

        ContentValues args = new ContentValues();
        args.put(failed_database_table, true);
        args.put(failed_database_table_timestamp, getTimeNow());

        updateContentsLowLevel(content_uri, args, strFilter);
    }


    private void markMultipleRowsAsSuccessfullySentToServer(Uri content_uri, int start_database_row_id, int end_database_row_id)
    {
        String strFilter = Table.COLUMN_ID + " BETWEEN " + start_database_row_id + " AND " + end_database_row_id;

        ContentValues args = new ContentValues();
        args.put(Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, true);
        args.put(Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP, getTimeNow());

        updateContentsLowLevel(content_uri, args, strFilter);
    }


    private void markMultipleRowsAsFailedSendingToServer(Uri content_uri, int start_database_row_id, int end_database_row_id)
    {
        String strFilter = Table.COLUMN_ID + " BETWEEN " + start_database_row_id + " AND " + end_database_row_id;

        ContentValues args = new ContentValues();
        args.put(Table.COLUMN_SENT_TO_SERVER_BUT_FAILED, true);
        args.put(Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP, getTimeNow());

        updateContentsLowLevel(content_uri, args, strFilter);
    }


    private long getTimeNow()
    {
        return patient_gateway_interface.getNtpTimeNowInMilliseconds();
    }


    private void updateSentToServerStatus(boolean success,
                                                 String measurement_name_as_string,
                                                 Uri uri,
                                                 RowRange row_range)
    {
        String log_line = "Marking " + measurement_name_as_string + " rows " + row_range.start + " to " + row_range.end + " as ";

        if (success)
        {
            Log.d(TAG, log_line + "sent to server");

            markMultipleRowsAsSuccessfullySentToServer(uri, row_range.start, row_range.end);
        }
        else
        {
            Log.d(TAG, log_line + "FAILED SENDING TO SERVER");

            markMultipleRowsAsFailedSendingToServer(uri, row_range.start, row_range.end);
        }
    }


    private int getNumberOfRows(Uri uri, String[] projection)
    {
        String selection = "";
        return getNumberOfRowsWithSelection(uri, projection, selection);
    }


    private int getNumberOfRowsWithSelection(Uri uri, String[] projection, String selection)
    {
        // All selection done inside Content Provider query as it has to do a join across several tables to ensure this data is syncable
        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        int cursor_count;

        if(cursor != null)
        {
            cursor_count = cursor.getCount();

            cursor.close();

            return cursor_count;
        }
        else
        {
            return  0;
        }
    }


    public void storeLifetouchHeartBeatMeasurements(int device_session_number, long human_readable_device_id, HeartBeatInfo[] heart_beat_list, BulkInsertCallBack mBulkInsertCallBack)
    {
        List<ContentValues> mValueList = new ArrayList<>();

        long written_to_database_timestamp = getTimeNow();

        for (HeartBeatInfo heart_beat : heart_beat_list)
        {
            ContentValues content_values = new ContentValues();

            content_values.put(TableLifetouchHeartBeat.COLUMN_DEVICE_SESSION_NUMBER, device_session_number);
            content_values.put(TableLifetouchHeartBeat.COLUMN_HUMAN_READABLE_DEVICE_ID, human_readable_device_id);
            content_values.put(TableLifetouchHeartBeat.COLUMN_AMPLITUDE, heart_beat.getAmplitude());
            content_values.put(TableLifetouchHeartBeat.COLUMN_TIMESTAMP, heart_beat.getTimestampInMs());
            content_values.put(TableLifetouchHeartBeat.COLUMN_SEQUENCE_ID, heart_beat.getTag());
            content_values.put(TableLifetouchHeartBeat.COLUMN_ACTIVITY_LEVEL, heart_beat.getActivity().getValue());
            content_values.put(TableLifetouchHeartBeat.COLUMN_RR_INTERVAL, heart_beat.getRrInterval());
            content_values.put(TableLifetouchHeartBeat.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);
            content_values.put(TableLifetouchHeartBeat.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP, written_to_database_timestamp);

            mValueList.add(content_values);
        }

        ContentValues[] bulkToInsert = new ContentValues[mValueList.size()];
        mValueList.toArray(bulkToInsert);

        bulkInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_BEATS, bulkToInsert, mBulkInsertCallBack);
    }

    public void updateLifetouchHeartBeatsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_LIFETOUCH_HEART_BEATS", IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_BEATS, row_range);
        }
    }


    public void storeLifetouchRespirationMeasurement(DeviceInfo device_info, MeasurementRespirationRate measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableLifetouchRespirationRate.COLUMN_RESPIRATION_RATE, measurement.respiration_rate);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RESPIRATION_RATES, content_values);
    }


    public void updateLifetouchRespirationMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_LIFETOUCH_RESPIRATION_RATES", IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RESPIRATION_RATES, row_range);
        }
    }


    public void storeSetupModeSamples(DeviceInfo device_info, ConcurrentLinkedQueue<MeasurementSetupModeDataPoint> data_points, BulkInsertCallBack mBulkInsertCallBack)
    {
        List<ContentValues> mValueList = new ArrayList<>();

        long timestamp_now = getTimeNow();

        for (MeasurementSetupModeDataPoint data_point : data_points)
        {
            ContentValues content_values = new ContentValues();

            content_values.put(TableSetupModeData.COLUMN_HUMAN_READABLE_DEVICE_ID, device_info.human_readable_device_id);
            content_values.put(TableSetupModeData.COLUMN_SAMPLE_VALUE, data_point.sample);
            content_values.put(TableSetupModeData.COLUMN_TIMESTAMP, data_point.timestamp_in_ms);
            content_values.put(TableSetupModeData.COLUMN_DEVICE_SESSION_NUMBER, device_info.getAndroidDeviceSessionId());
            content_values.put(TableSetupModeData.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);
            content_values.put(TableSetupModeData.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP, timestamp_now);

            mValueList.add(content_values);
        }

        ContentValues[] bulkToInsert = new ContentValues[mValueList.size()];
        mValueList.toArray(bulkToInsert);

        bulkInsertContentValuesAtContentUri(device_info.getSetupModeUri(), bulkToInsert, mBulkInsertCallBack);
    }


    public void updateLifetouchSetupModeSamplesSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES", IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES, row_range);
        }
    }


    public void storeLifetouchRawAccelerometerModeSamples(int device_session_number, long human_readable_device_id, RawAccelerometerModeDataPoint[] data_points, BulkInsertCallBack mBulkInsertCallBack)
    {
        List<ContentValues> mValueList = new ArrayList<>();

        long timestamp_now = getTimeNow();

        for (RawAccelerometerModeDataPoint data_point : data_points)
        {
            ContentValues content_values = new ContentValues();

            content_values.put(TableLifetouchRawAccelerometerModeSample.COLUMN_HUMAN_READABLE_DEVICE_ID, human_readable_device_id);
            content_values.put(TableLifetouchRawAccelerometerModeSample.COLUMN_X_SAMPLE_VALUE, data_point.x);
            content_values.put(TableLifetouchRawAccelerometerModeSample.COLUMN_Y_SAMPLE_VALUE, data_point.y);
            content_values.put(TableLifetouchRawAccelerometerModeSample.COLUMN_Z_SAMPLE_VALUE, data_point.z);
            content_values.put(TableLifetouchRawAccelerometerModeSample.COLUMN_TIMESTAMP, data_point.timestamp);
            content_values.put(TableLifetouchRawAccelerometerModeSample.COLUMN_DEVICE_SESSION_NUMBER, device_session_number);
            content_values.put(TableLifetouchRawAccelerometerModeSample.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);
            content_values.put(TableLifetouchRawAccelerometerModeSample.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP, timestamp_now);

            mValueList.add(content_values);
        }

        ContentValues[] bulkToInsert = new ContentValues[mValueList.size()];
        mValueList.toArray(bulkToInsert);

        bulkInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES, bulkToInsert, mBulkInsertCallBack);
    }


    public void updateLifetouchRawAccelerometerModeSamplesSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES", IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES, row_range);
        }
    }


    public void storeLifetouchHeartRateMeasurement(DeviceInfo device_info, MeasurementHeartRate measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableLifetouchHeartRate.COLUMN_HEART_RATE, measurement.heart_rate);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_RATES, content_values);
    }


    public void updateLifetouchHeartMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_LIFETOUCH_HEART_RATES", IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_RATES, row_range);
        }
    }


    public void storeLifetouchBatteryMeasurement(DeviceInfo device_info, MeasurementBatteryReading measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableLifetouchBattery.COLUMN_BATTERY_PERCENTAGE, measurement.percentage);
        content_values.put(TableLifetouchBattery.COLUMN_BATTERY_MILLIVOLTS, measurement.millivolts);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_BATTERY_MEASUREMENTS, content_values);
    }


    public void updateLifetouchBatteryMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_LIFETOUCH_BATTERY_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_BATTERY_MEASUREMENTS, row_range);
        }
    }
    
    
    public void storeLifetempTemperatureMeasurement(DeviceInfo device_info, MeasurementTemperature measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableLifetempMeasurement.COLUMN_TEMPERATURE, measurement.temperature);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_TEMPERATURE_MEASUREMENTS, content_values);
    }

    public void updateLifetempTemperatureMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_LIFETEMP_TEMPERATURE_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_TEMPERATURE_MEASUREMENTS, row_range);
        }
    }
    
    
    public void storeLifetempBatteryMeasurement(DeviceInfo device_info, MeasurementBatteryReading measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableLifetempBattery.COLUMN_BATTERY_PERCENTAGE, measurement.percentage);
        content_values.put(TableLifetempBattery.COLUMN_BATTERY_MILLIVOLTS, measurement.millivolts);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_BATTERY_MEASUREMENTS, content_values);
    }


    public void updateLifetempBatteryMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_LIFETEMP_BATTERY_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_BATTERY_MEASUREMENTS, row_range);
        }
    }
    

    public void storeOximeterMeasurement(DeviceInfo device_info, MeasurementSpO2 measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableOximeterMeasurement.COLUMN_PULSE, measurement.pulse);
        content_values.put(TableOximeterMeasurement.COLUMN_SPO2, measurement.SpO2);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_MEASUREMENTS, content_values);
    }


    public void updateOximeterMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_OXIMETER_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_MEASUREMENTS, row_range);
        }
    }


    public void storeOximeterIntermediateMeasurements(DeviceInfo device_info, IntermediateSpO2[] measurements, BulkInsertCallBack mBulkInsertCallBack)
    {
        List<ContentValues> mValueList = new ArrayList<>();

        for (IntermediateSpO2 measurement : measurements)
        {
            ContentValues content_values = new ContentValues();

            content_values.put(Table.COLUMN_HUMAN_READABLE_DEVICE_ID, device_info.human_readable_device_id);
            content_values.put(Table.COLUMN_DEVICE_SESSION_NUMBER, device_info.getAndroidDeviceSessionId());

            content_values.put(Table.COLUMN_TIMESTAMP, measurement.getTimestampInMs());
            content_values.put(Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);
            content_values.put(Table.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP, getTimeNow());

            content_values.put(TableOximeterIntermediateMeasurement.COLUMN_PULSE, measurement.getPulse());
            content_values.put(TableOximeterIntermediateMeasurement.COLUMN_SPO2, measurement.getAmplitude());

            mValueList.add(content_values);
        }

        ContentValues[] bulkToInsert = new ContentValues[mValueList.size()];
        mValueList.toArray(bulkToInsert);

        bulkInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_INTERMEDIATE_MEASUREMENTS, bulkToInsert, mBulkInsertCallBack);
    }


    public void updateOximeterIntermediateMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_OXIMETER_INTERMEDIATE_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_INTERMEDIATE_MEASUREMENTS, row_range);
        }
    }


    public void updateOximeterSetupModeSamplesSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES", IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES, row_range);
        }
    }


    public void storeOximeterBatteryMeasurement(DeviceInfo device_info, MeasurementBatteryReading measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableOximeterBattery.COLUMN_BATTERY_PERCENTAGE, measurement.percentage);
        content_values.put(TableOximeterBattery.COLUMN_BATTERY_MILLIVOLTS, measurement.millivolts);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_BATTERY_MEASUREMENTS, content_values);
    }


    public void updateOximeterBatteryMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_OXIMETER_BATTERY_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_BATTERY_MEASUREMENTS, row_range);
        }
    }


    public void storeBloodPressureMeasurement(DeviceInfo device_info, MeasurementBloodPressure measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableBloodPressureMeasurement.COLUMN_SYSTOLIC, measurement.systolic);
        content_values.put(TableBloodPressureMeasurement.COLUMN_DIASTOLIC, measurement.diastolic);
        content_values.put(TableBloodPressureMeasurement.COLUMN_PULSE, measurement.pulse);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS, content_values);
    }


    public void updateBloodPressureMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS, row_range);
        }
    }

    
    public void storeBloodPressureBatteryMeasurement(DeviceInfo device_info, MeasurementBatteryReading measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableBloodPressureBattery.COLUMN_BATTERY, measurement.percentage);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, content_values);
    }


    public void updateBloodPressureBatteryMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_BLOOD_PRESSURE_BATTERY_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, row_range);
        }
    }


    public void storeWeightScaleMeasurement(DeviceInfo device_info, MeasurementWeight measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableWeightScaleWeight.COLUMN_WEIGHT, measurement.weight);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_MEASUREMENTS, content_values);
    }


    public void updateWeightScaleMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_WEIGHT_SCALE_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_MEASUREMENTS, row_range);
        }
    }


    public void storeWeightScaleBatteryMeasurement(DeviceInfo device_info, MeasurementBatteryReading measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableWeightScaleBattery.COLUMN_BATTERY_PERCENTAGE, measurement.percentage);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_BATTERY_MEASUREMENTS, content_values);
    }


    public void updateWeightScaleBatteryMeasurementsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_WEIGHT_SCALE_BATTERY_MEASUREMENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_BATTERY_MEASUREMENTS, row_range);
        }
    }


    // Session database code below here
    
    
    public int insertPatientDetails(int by_bed_id, PatientInfo patientInfo, int by_user_id, long timestamp)
    {
        ContentValues content_values = new ContentValues();

        content_values.put(TablePatientDetails.COLUMN_BY_BED_ID, by_bed_id);
        content_values.put(TablePatientDetails.COLUMN_HOSPITAL_PATIENT_ID, patientInfo.getHospitalPatientId());
        content_values.put(TablePatientDetails.COLUMN_AGE_BLOCK, -1);
        content_values.put(TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_ID, patientInfo.getThresholdSet().servers_database_row_id);
        content_values.put(TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID, patientInfo.getThresholdSetAgeBlockDetails().servers_database_row_id);
        content_values.put(TablePatientDetails.COLUMN_BY_USER_ID, by_user_id);
        content_values.put(TablePatientDetails.COLUMN_TIMESTAMP, timestamp);
        content_values.put(TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);

        return insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_DETAILS, content_values);
    }
    
    public void patientDetailsSuccessfullySentToServer(int android_local_database_row_to_update, int servers_patient_details_id)
    {
        // Save the Servers ID of this row.
        updateContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_DETAILS, 
                TablePatientDetails.COLUMN_SERVERS_ID, 
                android_local_database_row_to_update, 
                servers_patient_details_id);
        
        markRowAsSuccessfullySentToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_DETAILS, 
                TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED,
                TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP,
                android_local_database_row_to_update);

        // Find all rows of Patient Session where "By Patient Details ID" = "android_local_database_row_to_update" and update "By Servers Patient Details ID" with servers_patient_details_id 
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS;
        String[] projection = {TablePatientSession.COLUMN_ID, TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID};
        String selection = TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID + "=" + android_local_database_row_to_update;

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    int local_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_ID));
                    updateContentValuesAtContentUri(uri, TablePatientSession.COLUMN_BY_SERVERS_PATIENT_DETAILS_ID, local_database_row, servers_patient_details_id);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
    }


    public void patientDetailsFailedSendingToServer(int android_local_database_row_to_update)
    {
        markRowAsFailedSendingToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_DETAILS,
                TablePatientDetails.COLUMN_SENT_TO_SERVER_BUT_FAILED,
                TablePatientDetails.COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP,
                android_local_database_row_to_update);
    }


    public static class ThresholdSetIds
    {
        int servers_threshold_set_id = -1;
        int servers_threshold_set_age_block_id = -1;
    }


    public ThresholdSetIds getPatientDetails(int patient_details_id, PatientInfo patient_info)
    {
        ThresholdSetIds thresholdSetIds = new ThresholdSetIds();

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_DETAILS;

        String[] device_sessions_projection = {
                TablePatientDetails.COLUMN_ID,
                TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_ID,
                TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID,
                TablePatientDetails.COLUMN_BY_BED_ID,
                TablePatientDetails.COLUMN_HOSPITAL_PATIENT_ID,
        };

        String selection = TablePatientDetails.COLUMN_ID + " is " + patient_details_id;

        Cursor cursor = gateway_context_interface.getAppContext().getContentResolver().query(uri, device_sessions_projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                if(cursor.getCount() > 1)
                {
                    Log.e(TAG, "getPatientDetails : more than one patient detail - abort");

                    cursor.close();

                    return thresholdSetIds;
                }

                cursor.moveToFirst();

                patient_info.setHospitalPatientId(cursor.getString(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_HOSPITAL_PATIENT_ID)));

                thresholdSetIds.servers_threshold_set_id = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_ID));
                thresholdSetIds.servers_threshold_set_age_block_id = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID));
            }

            cursor.close();
        }

        return thresholdSetIds;
    }

        
    public int insertDeviceInfo(DeviceType device_type, long human_readable_device_id, int firmware_version_as_int, String firmware_version_as_string, String bluetooth_address, String hardware_version, String model, int by_user_id, long timestamp)
    {
        ContentValues content_values = new ContentValues();

        content_values.put(TableDeviceInfo.COLUMN_DEVICE_TYPE, device_type.ordinal());
        content_values.put(TableDeviceInfo.COLUMN_HUMAN_READABLE_DEVICE_ID, human_readable_device_id);
        content_values.put(TableDeviceInfo.COLUMN_BLUETOOTH_ADDRESS, bluetooth_address);
        content_values.put(TableDeviceInfo.COLUMN_FIRMWARE_VERSION, firmware_version_as_int);
        content_values.put(TableDeviceInfo.COLUMN_FIRMWARE_VERSION_AS_STRING, firmware_version_as_string);
        content_values.put(TableDeviceInfo.COLUMN_HARDWARE, hardware_version);
        content_values.put(TableDeviceInfo.COLUMN_MODEL, model);
        content_values.put(TableDeviceInfo.COLUMN_BY_USER_ID, by_user_id);
        content_values.put(TableDeviceInfo.COLUMN_TIMESTAMP, timestamp);
        content_values.put(TableDeviceInfo.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);

        return insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_INFO, content_values);
    }
    
    public void deviceInfoSuccessfullySentToServer(int android_local_database_row_to_update, int servers_device_info_id)
    {
        // Save the Servers ID of this row.
        updateContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_INFO, 
                TableDeviceInfo.COLUMN_SERVERS_ID, 
                android_local_database_row_to_update, 
                servers_device_info_id);
        
        markRowAsSuccessfullySentToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_INFO, 
                TableDeviceInfo.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, 
                TableDeviceInfo.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP,
                android_local_database_row_to_update);

        Uri uri;
        String selection;
        Cursor cursor;

        // Find all rows of Device Session where "By Device Info ID" = "android_local_database_row_to_update" and update "By Servers Device Info ID" with servers_device_info_id
        uri = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS;
        String[] device_sessions_projection = {TableDeviceSession.COLUMN_ID, TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID};
        selection = TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID + "=" + android_local_database_row_to_update;

        cursor = content_resolver.query(uri, device_sessions_projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    int local_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));
                    updateContentValuesAtContentUri(uri, TableDeviceSession.COLUMN_BY_SERVERS_DEVICE_INFO_ID, local_database_row, servers_device_info_id);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
    }


    public void deviceInfoFailedSendingToServer(int android_local_database_row_to_update)
    {
        markRowAsFailedSendingToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_INFO,
                TableDeviceInfo.COLUMN_SENT_TO_SERVER_BUT_FAILED,
                TableDeviceInfo.COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP,
                android_local_database_row_to_update);
    }


    public void getDeviceInfo(DeviceInfoManager device_info_manager, DeviceSession device_session, long start_time)
    {
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_INFO;

        String[] device_info_projection = {
                TableDeviceInfo.COLUMN_ID,
                TableDeviceInfo.COLUMN_HUMAN_READABLE_DEVICE_ID,
                TableDeviceInfo.COLUMN_BLUETOOTH_ADDRESS,
                TableDeviceInfo.COLUMN_FIRMWARE_VERSION,
                TableDeviceInfo.COLUMN_FIRMWARE_VERSION_AS_STRING,
                TableDeviceInfo.COLUMN_DEVICE_TYPE

        };

        String selection = TablePatientDetails.COLUMN_ID + " is " + device_session.local_device_session_id;

        Cursor cursor = gateway_context_interface.getAppContext().getContentResolver().query(uri, device_info_projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                if(cursor.getCount() > 1)
                {
                    Log.e(TAG, "getPatientDetails : more than one device info - abort");

                    return;
                }

                cursor.moveToFirst();

                DeviceType device_type = DeviceType.values()[cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_DEVICE_TYPE))];

                device_session.device_type = device_type;

                DeviceInfo device_info = device_info_manager.getDeviceInfoByDeviceType(device_type);

                device_info.android_database_device_info_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_ID));
                device_info.setAndroidDeviceSessionId(device_session.local_device_session_id);

                device_info.bluetooth_address = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_BLUETOOTH_ADDRESS));
                device_info.human_readable_device_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_HUMAN_READABLE_DEVICE_ID));

                int firmware_int = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_FIRMWARE_VERSION));
                String firmware_string = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_FIRMWARE_VERSION_AS_STRING));
                device_info.setDeviceFirmwareVersion(firmware_string, firmware_int);

                device_info.desired_device_connection_status = DeviceConnectionStatus.CONNECTED;
                device_info.setActualDeviceConnectionStatus(DeviceConnectionStatus.SEARCHING);

                device_info.setDeviceTypePartOfPatientSession(true);

                device_info.device_session_start_time = start_time;
            }

            cursor.close();
        }
    }

    public int insertPatientSession(int by_patient_id, long patient_start_session_time, int patient_start_session_by_user_id)
    {
        ContentValues content_values = new ContentValues();

        content_values.put(TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID, by_patient_id);
        content_values.put(TablePatientSession.COLUMN_PATIENT_START_SESSION_TIME, patient_start_session_time);
        content_values.put(TablePatientSession.COLUMN_PATIENT_START_SESSION_BY_USER_ID, patient_start_session_by_user_id);
        content_values.put(TablePatientSession.COLUMN_PATIENT_SESSION_PAUSED, false);
        content_values.put(TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);
        content_values.put(TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);

        return insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, content_values);
    }
    
    public void patientStartSessionSuccessfullySentToServer(int android_local_database_row_to_update, int servers_patient_session_id)
    {
        // Save the Servers ID of this row.
        updateContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, 
                TablePatientSession.COLUMN_SERVERS_ID, 
                android_local_database_row_to_update,
                servers_patient_session_id);
        
        markRowAsSuccessfullySentToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, 
                TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, 
                TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP,
                android_local_database_row_to_update);

        // Find all rows of Device Session where "By Patient Session ID" = "android_local_database_row_to_update" and update "By Servers Patient Session ID" with servers_patient_session_id
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS;
        String[] projection = {TableDeviceSession.COLUMN_ID, TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID};
        String selection = TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID + "=" + android_local_database_row_to_update;

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    int local_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));
                    updateContentValuesAtContentUri(uri, TableDeviceSession.COLUMN_BY_SERVERS_PATIENT_SESSION_ID, local_database_row, servers_patient_session_id);

                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
    }


    public void patientStartSessionFailedSendingToServer(int android_local_database_row_to_update)
    {
        markRowAsFailedSendingToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS,
                TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED,
                TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP,
                android_local_database_row_to_update);
    }


    public void endPatientSession(int patient_session_id, long end_patient_session_time, int patient_end_session_by_user_id)
    {
        String strFilter = TablePatientSession.COLUMN_ID + "=" + patient_session_id;

        ContentValues args = new ContentValues();
        args.put(TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME, end_patient_session_time);
        args.put(TablePatientSession.COLUMN_PATIENT_END_SESSION_BY_USER_ID, patient_end_session_by_user_id);

        content_resolver.update(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, args, strFilter, null);
    }

    
	public void pausePatientSession(int patient_session_id) 
	{
        String strFilter = TablePatientSession.COLUMN_ID + "=" + patient_session_id;

        ContentValues args = new ContentValues();
        args.put(TablePatientSession.COLUMN_PATIENT_SESSION_PAUSED, true);

        content_resolver.update(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, args, strFilter, null);
	}
	
	
	public void unpausePatientSession(int patient_session_id) 
	{
        String strFilter = TablePatientSession.COLUMN_ID + "=" + patient_session_id;

        ContentValues args = new ContentValues();
        args.put(TablePatientSession.COLUMN_PATIENT_SESSION_PAUSED, true);

        content_resolver.update(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, args, strFilter, null);
	}
	

	public ArrayList<Pair<DeviceSession, Date>> getOutstandingDeviceSessions()
    {
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS;

        String[] device_sessions_projection = {
                TableDeviceSession.COLUMN_ID,
                TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID,
                TableDeviceSession.COLUMN_DEVICE_START_SESSION_TIME,
                TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME
        };

        String selection = TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME + " is 0";

        ArrayList<Pair<DeviceSession, Date>> return_list = new ArrayList<>();

        Cursor cursor = gateway_context_interface.getAppContext().getContentResolver().query(uri, device_sessions_projection, selection, null, null, null);
        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    DeviceSession temp_device_session = new DeviceSession(SensorType.SENSOR_TYPE__INVALID, DeviceType.DEVICE_TYPE__INVALID, cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID)));
                    Date temp_date = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_DEVICE_START_SESSION_TIME)));

                    Pair<DeviceSession, Date> item = new Pair<>(temp_device_session, temp_date);

                    return_list.add(item);

                    cursor.moveToNext();
                }
            }

            cursor.close();
        }

        return return_list;
    }


    public void patientEndSessionSuccessfullySentToServer(int android_local_database_row_to_update, int servers_device_info_id)
    {
        // Save the Servers ID of this row.
        updateContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, 
                TablePatientSession.COLUMN_SERVERS_ID, 
                android_local_database_row_to_update, 
                servers_device_info_id);
        
        markRowAsSuccessfullySentToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, 
                TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, 
                TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP,
                android_local_database_row_to_update);
    }


    public void patientEndSessionFailedSendingToServer(int android_local_database_row_to_update)
    {
        markRowAsFailedSendingToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS,
                TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED,
                TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP,
                android_local_database_row_to_update);
    }


    public int insertDeviceSession(int by_patient_session_id, int by_device_info_id, long device_start_session_time, int device_start_session_by_user_id, int by_server_patient_session_id)
    {
        ContentValues content_values = new ContentValues();

        content_values.put(TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID, by_patient_session_id);
        content_values.put(TableDeviceSession.COLUMN_BY_SERVERS_PATIENT_SESSION_ID, by_server_patient_session_id);
        content_values.put(TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID, by_device_info_id);
        content_values.put(TableDeviceSession.COLUMN_DEVICE_START_SESSION_TIME, device_start_session_time);
        content_values.put(TableDeviceSession.COLUMN_DEVICE_START_SESSION_BY_USER_ID, device_start_session_by_user_id);
        content_values.put(TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);
        content_values.put(TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);

        return insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS, content_values);
    }
    
    public void deviceStartSessionSuccessfullySentToServer(int android_local_database_row_to_update, int servers_device_session_id)
    {
        updateContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS, 
                TableDeviceSession.COLUMN_SERVERS_ID, 
                android_local_database_row_to_update, 
                servers_device_session_id);
        
        markRowAsSuccessfullySentToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS, 
                TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, 
                TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP,
                android_local_database_row_to_update);
    }


    public void deviceStartSessionFailedSendingToServer(int android_local_database_row_to_update)
    {
        markRowAsFailedSendingToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS,
                TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED,
                TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP,
                android_local_database_row_to_update);
    }


    public void endDeviceSession(int device_session_id, long device_end_session_time, int device_end_session_by_user_id)
    {
        String strFilter = TableDeviceSession.COLUMN_ID + "=" + device_session_id;

        ContentValues args = new ContentValues();
        args.put(TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME, device_end_session_time);
        args.put(TableDeviceSession.COLUMN_DEVICE_END_SESSION_BY_USER_ID, device_end_session_by_user_id);

        content_resolver.update(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS, args, strFilter, null);
    }


    public void deviceEndSessionSuccessfullySentToServer(int database_row_id)
    {
        markRowAsSuccessfullySentToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS, 
                TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, 
                TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP,
                database_row_id);
    }


    public void deviceEndSessionFailedSendingToServer(int android_local_database_row_to_update)
    {
        markRowAsFailedSendingToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS,
                TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED,
                TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP,
                android_local_database_row_to_update);
    }


    public void storeConnectionEvent(int patient_session_number, int device_session_number, ConnectionEvent connected, long timestamp)
    {
        ContentValues content_values = new ContentValues();
        content_values.put(TableConnectionEvent.COLUMN_PATIENT_SESSION_NUMBER, patient_session_number);
        content_values.put(TableConnectionEvent.COLUMN_DEVICE_SESSION_NUMBER, device_session_number);
        content_values.put(TableConnectionEvent.COLUMN_CONNECTED, connected.ordinal());
        content_values.put(TableConnectionEvent.COLUMN_TIMESTAMP, timestamp);
        content_values.put(TableConnectionEvent.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_CONNECTION_EVENTS, content_values);
    }


    public void updateConnectionEventSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_CONNECTION_EVENTS", IsansysPatientGatewayContentProvider.CONTENT_URI_CONNECTION_EVENTS, row_range);
        }
    }


    // These are "special" functions in that they are not currently sent to the Server. 
    // They are just used by WAMP to report how often the Gateway/UI have restarted
    public int storeGatewayStartupTime(long timestamp)
    {
        ContentValues content_values = new ContentValues();
        content_values.put(TableDiagnosticsGatewayStartupTimes.COLUMN_TIMESTAMP, timestamp);

        insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_DIAGNOSTICS_GATEWAY_STARTUP_TIMES, content_values);

        return getNumberOfRowsPending(QueryType.GATEWAY_STARTUP_TIMES, null);
    }

    public int storeUiStartupTime(long timestamp)
    {
        ContentValues content_values = new ContentValues();
        content_values.put(TableDiagnosticsUiStartupTimes.COLUMN_TIMESTAMP, timestamp);

        insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_DIAGNOSTICS_UI_STARTUP_TIMES, content_values);

        return getNumberOfRowsPending(QueryType.UI_STARTUP_TIMES, null);
    }

    public void deleteOldDiagnosticData()
    {
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_DIAGNOSTICS_GATEWAY_STARTUP_TIMES, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_DIAGNOSTICS_UI_STARTUP_TIMES, null, null);
    }


    public void storeWardList(ArrayList<WardInfo> ward_info_list)
    {
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_WARDS, null, null);

        long written_to_database_timestamp = getTimeNow();

        for (WardInfo ward_info : ward_info_list)
        {
            ContentValues content_values = new ContentValues();

            content_values.put(TableWards.COLUMN_SERVERS_ID, ward_info.ward_details_id);
            content_values.put(TableWards.COLUMN_WARD_NAME, ward_info.ward_name);
            content_values.put(TableWards.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP, written_to_database_timestamp);

            insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_WARDS, content_values);
        }
    }


    public void storeBedList(ArrayList<BedInfo> bed_info_list)
    {
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_BEDS, null, null);

        long written_to_database_timestamp = getTimeNow();

        for (BedInfo bed_info : bed_info_list)
        {
            ContentValues content_values = new ContentValues();

            content_values.put(TableBeds.COLUMN_SERVERS_ID, bed_info.bed_details_id);
            content_values.put(TableBeds.COLUMN_BED_NAME, bed_info.bed_name);
            content_values.put(TableBeds.COLUMN_BY_WARD_ID, bed_info.by_ward_id);
            content_values.put(TableBeds.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP, written_to_database_timestamp);

            insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_BEDS, content_values);
        }
    }


    public RowsPending checkIfPatientDetailsPendingToBeSentToServer()
    {
        RowsPending rowsPending = new RowsPending();

        String selection;
        selection = TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "= 0 AND " + TablePatientDetails.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";
        rowsPending.rows_pending_syncable = getNumberOfRowsPending(QueryType.PATIENT_DETAILS, selection);

        selection = TablePatientDetails.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1";
        rowsPending.rows_pending_but_failed = getNumberOfRowsPending(QueryType.PATIENT_DETAILS, selection);

        rowsPending.rows_pending_non_syncable = 0; // not possible to be non-syncable, as sending patient details doesn't depend on any other info syncing first.

        return rowsPending;
    }





    public int checkIfPatientDetailsPendingToBeSentToServerAndSentIt(ServerLink server_interface)
    {
        // Get the oldest database row that hasn't been sent to the Server yet.

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_DETAILS;

        String[] projection = {
                TablePatientDetails.COLUMN_ID,
                TablePatientDetails.COLUMN_SERVERS_ID,
                TablePatientDetails.COLUMN_BY_BED_ID, 
                TablePatientDetails.COLUMN_HOSPITAL_PATIENT_ID, 
                TablePatientDetails.COLUMN_AGE_BLOCK, 
                TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_ID,
                TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID,
                TablePatientDetails.COLUMN_BY_USER_ID,
                TablePatientDetails.COLUMN_TIMESTAMP,
                TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED,
                TablePatientDetails.COLUMN_SENT_TO_SERVER_BUT_FAILED
                };

        String selection = TablePatientDetails.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 AND " + TablePatientDetails.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if(database_rows > 0)
            {
                Log.i(TAG, "checkIfPatientDetailsPendingToBeSentToServerAndSentIt : ServerSyncing.currentSessionInformation.patientDetailSendToServer_Status = " + server_syncing.currentSessionInformation.patientDetailSendToServer_Status);

                switch (server_syncing.currentSessionInformation.patientDetailSendToServer_Status)
                {
                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    {
                        // Process the newest one that hasn't been sent to the Server yet. This will probably be the current active session
                        cursor.moveToLast();
                        String string__android_database_row = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_ID));

                        String string__hospital_patient_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_HOSPITAL_PATIENT_ID));

                        Log.i(TAG, "checkIfPatientDetailsPendingToBeSentToServerAndSentIt : string__android_database_row = " + string__android_database_row + " : string__hospital_patient_id = " + string__hospital_patient_id);

                        int android_database_row = Integer.parseInt(string__android_database_row);

                        server_interface.sendCheckPatientId(string__hospital_patient_id, android_database_row);
                        server_syncing.currentSessionInformation.patientDetailSendToServer_Status = CheckServerLinkStatus.CHECKING;
                    }
                    break;

                    case SEND_PACKET:
                    {
                        // Process the newest one that hasn't been sent to the Server yet. This will probably be the current active session
                        cursor.moveToLast();

                        // Parameters
                        String string__by_bed_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_BY_BED_ID));
                        String string__hospital_patient_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_HOSPITAL_PATIENT_ID));
                        String string__age_block = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_AGE_BLOCK));
                        String string__servers_threshold_set_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_ID));
                        String string__servers_threshold_set_age_block_detail_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_SERVERS_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID));
                        String string__by_user_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_BY_USER_ID));

                        long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_TIMESTAMP));

                        int android_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientDetails.COLUMN_ID));

                        server_interface.sendPatientDetailsToServer(android_database_row,
                                string__by_bed_id,
                                string__by_user_id,
                                string__hospital_patient_id,
                                string__age_block,
                                string__servers_threshold_set_id,
                                string__servers_threshold_set_age_block_detail_id,
                                timestamp);
                    }
                    break;

                    case IDLE:
                    case CHECKING:
                    default:
                        break;
                }
            }
            else
            {
                Log.d(TAG, "checkIfPatientDetailsPendingToBeSentToServerAndSentIt : Cursor count is ZERO");
            }
            cursor.close();
        }
        
        return database_rows;
    }


    public RowsPending checkIfDeviceInfoPendingToBeSentToServer()
    {
        RowsPending rowsPending = new RowsPending();

        String selection;
        selection = TableDeviceInfo.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "= 0 AND " + TableDeviceInfo.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";
        rowsPending.rows_pending_syncable = getNumberOfRowsPending(QueryType.DEVICE_INFO, selection);

        selection = TableDeviceInfo.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1";
        rowsPending.rows_pending_but_failed = getNumberOfRowsPending(QueryType.DEVICE_INFO, selection);

        return rowsPending;
    }


    public int checkIfDeviceInfoPendingToBeSentToServerAndSendIt(ServerLink server_interface)
    {
        // Get the oldest database row that hasn't been sent to the Server yet.

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_INFO;

        String[] projection = {
                TableDeviceInfo.COLUMN_ID,
                TableDeviceInfo.COLUMN_DEVICE_TYPE, 
                TableDeviceInfo.COLUMN_HUMAN_READABLE_DEVICE_ID,
                TableDeviceInfo.COLUMN_BLUETOOTH_ADDRESS,
                TableDeviceInfo.COLUMN_FIRMWARE_VERSION_AS_STRING,
                TableDeviceInfo.COLUMN_HARDWARE, 
                TableDeviceInfo.COLUMN_MODEL, 
                TableDeviceInfo.COLUMN_BY_USER_ID,
                TableDeviceInfo.COLUMN_TIMESTAMP,
                TableDeviceInfo.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED,
                TableDeviceInfo.COLUMN_SENT_TO_SERVER_BUT_FAILED
                };

        String selection = TableDeviceInfo.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 AND " + TableDeviceInfo.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if(database_rows > 0)
            {
                Log.i(TAG, "checkIfDeviceInfoPendingToBeSentToServerAndSendIt : server_syncing.currentSessionInformation.deviceInfoSendToServer_Status = " + server_syncing.currentSessionInformation.deviceInfoSendToServer_Status);

                switch (server_syncing.currentSessionInformation.deviceInfoSendToServer_Status)
                {
                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    {
                        // Process the newest one that hasn't been sent to the Server yet. This will probably be the current active session
                        cursor.moveToLast();

                        // Parameters
                        int device_type_as_int = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_DEVICE_TYPE));
                        long human_readable_device_id = cursor.getLong(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_HUMAN_READABLE_DEVICE_ID));

                        server_interface.sendCheckDeviceDetails(device_type_as_int, human_readable_device_id);

                        server_syncing.currentSessionInformation.deviceInfoSendToServer_Status = CheckServerLinkStatus.CHECKING;
                    }
                    break;

                    case SEND_PACKET:
                    {
                        // Process the newest one that hasn't been sent to the Server yet. This will probably be the current active session
                        cursor.moveToLast();

                        // Parameters
                        int device_type = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_DEVICE_TYPE));
                        String string__bluetooth_address = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_BLUETOOTH_ADDRESS));
                        int human_readable_device_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_HUMAN_READABLE_DEVICE_ID));
                        String firmware_version_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_FIRMWARE_VERSION_AS_STRING));
                        String hardware = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_HARDWARE));
                        String model = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_MODEL));

                        long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_TIMESTAMP));
                        int android_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceInfo.COLUMN_ID));

                        server_interface.sendDeviceInfoToServer(
                                android_database_row,
                                device_type,
                                string__bluetooth_address,
                                human_readable_device_id,
                                firmware_version_as_string,
                                hardware,
                                model,
                                timestamp);
                    }
                    break;

                    case IDLE:
                    case CHECKING:
                    default:
                        break;
                }
            }
            else
            {
                Log.d(TAG, "checkIfDeviceInfoPendingToBeSentToServerAndSendIt : Cursor count is ZERO");
            }

            cursor.close();
        }
        
        return database_rows;
    }


    public RowsPending checkIfStartPatientSessionPendingToBeSentToServer()
    {
        RowsPending rowsPending = new RowsPending();

        String selection;

        rowsPending.rows_pending_syncable = getNumberOfSyncableStartPatientSession(true);

        rowsPending.rows_pending_non_syncable = getNumberOfSyncableStartPatientSession(false);

        selection = TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED + "= 1";
        rowsPending.rows_pending_but_failed = getNumberOfRowsPending(QueryType.PATIENT_SESSIONS, selection);

        return rowsPending;
    }


    private int getNumberOfSyncableStartPatientSession(boolean syncable)
    {
        String[] projection = {
                TablePatientSession.COLUMN_ID,
                TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID,
                TablePatientSession.COLUMN_BY_SERVERS_PATIENT_DETAILS_ID,
                TablePatientSession.COLUMN_PATIENT_START_SESSION_TIME,
                TablePatientSession.COLUMN_PATIENT_START_SESSION_BY_USER_ID,
                TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED
        };

        if(syncable)
        {
            return getNumberOfRows(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_START_PATIENT_SESSIONS, projection);
        }
        else
        {
            return getNumberOfRows(IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_START_PATIENT_SESSIONS, projection);
        }
    }


    public int checkIfStartPatientSessionPendingToBeSentToServerAndSendIt(ServerLink server_interface)
    {
        // Get the oldest database row that hasn't been sent to the Server yet.
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_START_PATIENT_SESSIONS;

        String[] projection = {
                TablePatientSession.COLUMN_ID,
                TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID,
                TablePatientSession.COLUMN_BY_SERVERS_PATIENT_DETAILS_ID,
                TablePatientSession.COLUMN_PATIENT_START_SESSION_TIME,
                TablePatientSession.COLUMN_PATIENT_START_SESSION_BY_USER_ID, 
                TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED 
                };

        // All selection done inside Content Provider query as it has to do a join across several tables to ensure this data is syncable
        Cursor cursor = content_resolver.query(uri, projection, null, null, null, null);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if(database_rows > 0)
            {
                Log.i(TAG, "checkIfStartPatientSessionPendingToBeSentToServerAndSendIt : server_syncing.currentSessionInformation.startPatientSessionSendToServer_Status = " + server_syncing.currentSessionInformation.startPatientSessionSendToServer_Status);

                switch (server_syncing.currentSessionInformation.startPatientSessionSendToServer_Status)
                {
                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    {
                        // Process the oldest one that hasn't been sent to the Server yet. Otherwise the Server may auto close off the existing Patient Sessions for this Bed ID
                        cursor.moveToFirst();

                        // Parameters
                        int android_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_ID));
                        long start_patient_session_time = cursor.getLong(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_PATIENT_START_SESSION_TIME));
                        int servers_patient_id = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_BY_SERVERS_PATIENT_DETAILS_ID));

                        server_interface.send_CheckStartPatientSession(servers_patient_id, start_patient_session_time, android_database_row);
                        server_syncing.currentSessionInformation.startPatientSessionSendToServer_Status = CheckServerLinkStatus.CHECKING;
                    }
                    break;

                    case SEND_PACKET:
                    {
                        // Process the oldest one that hasn't been sent to the Server yet. Otherwise the Server may auto close off the existing Patient Sessions for this Bed ID
                        cursor.moveToFirst();

                        // Parameters
                        String string__by_servers_patient_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_BY_SERVERS_PATIENT_DETAILS_ID));
                        long start_patient_session_time = cursor.getLong(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_PATIENT_START_SESSION_TIME));
                        String string__start_patient_session_by_user_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_PATIENT_START_SESSION_BY_USER_ID));

                        int android_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_ID));

                        server_interface.sendStartPatientSessionToServer(android_database_row, string__by_servers_patient_id, start_patient_session_time, string__start_patient_session_by_user_id);
                    }
                    break;

                    case IDLE:
                    case CHECKING:
                    default:
                        break;
                }
            }
            else
            {
                Log.d(TAG, "checkIfStartPatientSessionPendingToBeSentToServerAndSendIt : Cursor count is ZERO");
            }

            cursor.close();
        }

        return database_rows;
    }


    public RowsPending checkIfEndPatientSessionPendingToBeSentToServer()
    {
        RowsPending rowsPending = new RowsPending();

        rowsPending.rows_pending_syncable = getNumberOfSyncableEndPatientSession(true);
        rowsPending.rows_pending_non_syncable = getNumberOfSyncableEndPatientSession(false);

        String selection = TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME + " IS NOT 0 AND " + TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED + "= 1";
        rowsPending.rows_pending_but_failed = getNumberOfRowsPending(QueryType.PATIENT_SESSIONS, selection);

        return rowsPending;
    }


    private int getNumberOfSyncableEndPatientSession(boolean syncable)
    {
        String[] projection = {
                TablePatientSession.COLUMN_ID,
                TablePatientSession.COLUMN_SERVERS_ID,
                TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME,
                TablePatientSession.COLUMN_PATIENT_END_SESSION_BY_USER_ID,
                TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED
        };

        if(syncable)
        {
            return getNumberOfRows(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_END_PATIENT_SESSIONS, projection);
        }
        else
        {
            return getNumberOfRows(IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_END_PATIENT_SESSIONS, projection);
        }
    }


    public int checkIfEndPatientSessionPendingToBeSentToServerAndSendIt(ServerLink server_interface)
    {
        // Get the oldest database row that hasn't been sent to the Server yet.

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_END_PATIENT_SESSIONS;

        String[] projection = {
                TablePatientSession.COLUMN_ID,
                TablePatientSession.COLUMN_SERVERS_ID,
                TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME,
                TablePatientSession.COLUMN_PATIENT_END_SESSION_BY_USER_ID,
                TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED 
                };

        // All selection done inside Content Provider query as it has to do a join across several tables to ensure this data is syncable
        Cursor cursor = content_resolver.query(uri, projection, null, null, null, null);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if(database_rows > 0)
            {
                Log.i(TAG, "checkIfEndPatientSessionPendingToBeSentToServerAndSendIt : server_syncing.currentSessionInformation.endPatientSessionSendToServer_Status = " + server_syncing.currentSessionInformation.endPatientSessionSendToServer_Status);

                switch (server_syncing.currentSessionInformation.endPatientSessionSendToServer_Status)
                {
                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    {
                        // Process the oldest one that hasn't been sent to the Server yet. Otherwise the Server may auto close off the existing Patient Sessions for this Bed ID
                        cursor.moveToFirst();

                        // Parameters
                        String string__android_database_row = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_ID));

                        long end_session_time = cursor.getLong(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME));
                        String string_end_session_server_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_SERVERS_ID));

                        int end_session_server_id = Integer.parseInt(string_end_session_server_id);
                        int android_database_row = Integer.parseInt(string__android_database_row);

                        server_interface.send_CheckEndPatientSession(end_session_server_id, end_session_time, android_database_row);
                        server_syncing.currentSessionInformation.endPatientSessionSendToServer_Status = CheckServerLinkStatus.CHECKING;
                    }
                    break;

                    case SEND_PACKET:
                    {
                        // Process the oldest one that hasn't been sent to the Server yet. Otherwise the Server may auto close off the existing Patient Sessions for this Bed ID
                        cursor.moveToFirst();

                        // Parameters
                        String string__android_database_row = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_ID));
                        String string__servers_device_session_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_SERVERS_ID));
                        long end_session_time = cursor.getLong(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME));
                        String string__end_session_by_user_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_PATIENT_END_SESSION_BY_USER_ID));

                        int android_database_row = Integer.parseInt(string__android_database_row);

                        server_interface.sendEndPatientSessionToServer(android_database_row, string__servers_device_session_id, end_session_time, string__end_session_by_user_id);
                    }
                    break;

                    case IDLE:
                    case CHECKING:
                    default:
                        break;
                }
            }
            else
            {
                Log.d(TAG, "checkIfEndPatientSessionPendingToBeSentToServerAndSendIt : Cursor Count is ZERO");
            }

            cursor.close();
        }
        
        return database_rows;
    }


    public RowsPending checkIfStartDeviceSessionPendingToBeSentToServer()
    {
        RowsPending rowsPending = new RowsPending();

        String selection;

        rowsPending.rows_pending_syncable = getNumberOfSyncableStartDeviceSession(true);

        rowsPending.rows_pending_non_syncable = getNumberOfSyncableStartDeviceSession(false);

        selection = TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED + "= 1";
        rowsPending.rows_pending_but_failed = getNumberOfRowsPending(QueryType.DEVICE_SESSIONS, selection);

        return rowsPending;
    }


    private int getNumberOfSyncableStartDeviceSession(boolean syncable)
    {
        String[] projection = {
                TableDeviceSession.COLUMN_ID,
                TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID,
                TableDeviceSession.COLUMN_BY_SERVERS_PATIENT_SESSION_ID,
                TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID,
                TableDeviceSession.COLUMN_BY_SERVERS_DEVICE_INFO_ID,
                TableDeviceSession.COLUMN_DEVICE_START_SESSION_TIME,
                TableDeviceSession.COLUMN_DEVICE_START_SESSION_BY_USER_ID,
                TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED
        };

        if(syncable)
        {
            return getNumberOfRows(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_START_DEVICE_SESSIONS, projection);
        }
        else
        {
            return getNumberOfRows(IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_START_DEVICE_SESSIONS, projection);
        }
    }


    private ArrayList<Integer> getSyncedOrUnsyncedDeviceSessionIds(int desired_patient_or_device_session_number, ActiveOrOldSession active_or_old_session, boolean synced)
    {
        ArrayList<Integer> session_ids = new ArrayList<>();

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS;

        String[] projection = {
                TableDeviceSession.COLUMN_ID,
                TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED
        };

        String synced_string = synced ? "1" : "0";

        String selection = TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=" + synced_string;

        if(active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
        {
            selection += " AND " + TableDeviceSession.COLUMN_ID + "=" + desired_patient_or_device_session_number;
        }
        else
        {
            selection += " AND " + TableDeviceSession.COLUMN_ID + "!=" + desired_patient_or_device_session_number;
        }

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();

            while(!cursor.isAfterLast())
            {
                String string__android_database_row = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));

                int android_database_row = Integer.parseInt(string__android_database_row);

                session_ids.add(android_database_row);

                cursor.moveToNext();
            }

            cursor.close();
        }

        return session_ids;
    }


    private ArrayList<Integer> getSyncedOrUnsyncedPatientSessionIds(int desired_patient_or_device_session_number, ActiveOrOldSession active_or_old_session, boolean synced)
    {
        ArrayList<Integer> session_ids = new ArrayList<>();

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS;

        String[] projection = {
                TablePatientSession.COLUMN_ID,
                TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED
        };

        String synced_string = synced ? "1" : "0";

        String selection = TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=" + synced_string;

        if(active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
        {
            selection += " AND " + TablePatientSession.COLUMN_ID + "=" + desired_patient_or_device_session_number;
        }
        else
        {
            selection += " AND " + TablePatientSession.COLUMN_ID + "!=" + desired_patient_or_device_session_number;
        }

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();

            while(!cursor.isAfterLast())
            {
                String string__android_database_row = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));

                int android_database_row = Integer.parseInt(string__android_database_row);

                session_ids.add(android_database_row);

                cursor.moveToNext();
            }

            cursor.close();
        }

        return session_ids;
    }


    public int checkIfStartDeviceSessionPendingToBeSentToServerAndSendIt(ServerLink server_interface)
    {
        // Get the oldest database row that hasn't been sent to the Server yet.

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_START_DEVICE_SESSIONS;

        String[] projection = {
                TableDeviceSession.COLUMN_ID,
                TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID,
                TableDeviceSession.COLUMN_BY_SERVERS_PATIENT_SESSION_ID,
                TableDeviceSession.COLUMN_BY_DEVICE_INFO_ID, 
                TableDeviceSession.COLUMN_BY_SERVERS_DEVICE_INFO_ID,
                TableDeviceSession.COLUMN_DEVICE_START_SESSION_TIME,
                TableDeviceSession.COLUMN_DEVICE_START_SESSION_BY_USER_ID, 
                TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED 
                };

        // All selection done inside Content Provider query as it has to do a join across several tables to ensure this data is syncable
        Cursor cursor = content_resolver.query(uri, projection, null, null, null, null);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if(database_rows > 0)
            {
                Log.i(TAG, "checkIfStartDeviceSessionPendingToBeSentToServerAndSendIt : server_syncing.currentSessionInformation.startDeviceSessionSendToServer_Status = " + server_syncing.currentSessionInformation.startDeviceSessionSendToServer_Status);

                switch (server_syncing.currentSessionInformation.startDeviceSessionSendToServer_Status)
                {
                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    {
                        // Process the oldest one that hasn't been sent to the Server yet. Otherwise the Server may auto close off the existing Device Sessions for this Bed ID
                        cursor.moveToFirst();

                        // Parameters
                        int android_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));
                        long device_start_session_time = cursor.getLong(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_DEVICE_START_SESSION_TIME));
                        int by_servers_patient_session_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_BY_SERVERS_PATIENT_SESSION_ID));
                        int by_servers_device_info_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_BY_SERVERS_DEVICE_INFO_ID));

                        server_interface.send_CheckStartDeviceSession(by_servers_patient_session_id, device_start_session_time, by_servers_device_info_id, android_database_row);
                        server_syncing.currentSessionInformation.startDeviceSessionSendToServer_Status = CheckServerLinkStatus.CHECKING;
                    }
                    break;

                    case SEND_PACKET:
                    {
                        // Process the oldest one that hasn't been sent to the Server yet. Otherwise the Server may auto close off the existing Device Sessions for this Bed ID
                        cursor.moveToFirst();

                        // Parameters
                        int android_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));

                        //String string__patient_session_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_BY_PATIENT_SESSION_ID));
                        String string__servers_patient_session_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_BY_SERVERS_PATIENT_SESSION_ID));
                        String string__servers_device_info_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_BY_SERVERS_DEVICE_INFO_ID));
                        long device_start_session_time = cursor.getLong(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_DEVICE_START_SESSION_TIME));
                        String string__device_start_session_by_user_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_DEVICE_START_SESSION_BY_USER_ID));

                        server_interface.sendStartDeviceSessionToServer(android_database_row, string__servers_patient_session_id, string__servers_device_info_id, device_start_session_time, string__device_start_session_by_user_id);
                    }
                    break;

                    case IDLE:
                    case CHECKING:
                    default:
                        break;
                }
            }
            else
            {
                Log.d(TAG, "checkIfStartDeviceSessionPendingToBeSentToServerAndSendIt : Cursor count is ZERO");
            }

            cursor.close();
        }
        
        return database_rows;
    }
    
    
    public RowsPending checkIfEndDeviceSessionPendingToBeSentToServer()
    {
        RowsPending rowsPending = new RowsPending();

        String selection;

        rowsPending.rows_pending_syncable = getNumberOfSyncableEndDeviceSession(true);

        rowsPending.rows_pending_non_syncable = getNumberOfSyncableEndDeviceSession(false);

        selection = TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME + " IS NOT 0 AND " + TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED + "= 1";
        rowsPending.rows_pending_but_failed = getNumberOfRowsPending(QueryType.DEVICE_SESSIONS, selection);

        return rowsPending;
    }


    private int getNumberOfSyncableEndDeviceSession(boolean syncable)
    {
        String[] projection = {
                TableDeviceSession.COLUMN_ID,
                TableDeviceSession.COLUMN_SERVERS_ID,
                TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME,
                TableDeviceSession.COLUMN_DEVICE_END_SESSION_BY_USER_ID,
                TableDeviceSession.COLUMN_BY_SERVERS_DEVICE_INFO_ID,
                TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED
        };

        if(syncable)
        {
            return getNumberOfRows(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_END_DEVICE_SESSIONS, projection);
        }
        else
        {
            return getNumberOfRows(IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_END_DEVICE_SESSIONS, projection);
        }
    }


    public int checkIfEndDeviceSessionPendingToBeSentToServerAndSendIt(ServerLink server_interface)
    {
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_END_DEVICE_SESSIONS;

        String[] projection = {
                TableDeviceSession.COLUMN_ID,
                TableDeviceSession.COLUMN_SERVERS_ID,
                TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME,
                TableDeviceSession.COLUMN_DEVICE_END_SESSION_BY_USER_ID, 
                TableDeviceSession.COLUMN_BY_SERVERS_DEVICE_INFO_ID,
                TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED 
                };

        // All selection done inside Content Provider query as it has to do a join across several tables to ensure this data is syncable
        Cursor cursor = content_resolver.query(uri, projection, null, null, null, null);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if(database_rows > 0)
            {
                Log.i(TAG, "checkIfEndDeviceSessionPendingToBeSentToServerAndSendIt : server_syncing.currentSessionInformation.endDeviceSessionSendToServer_Status = " + server_syncing.currentSessionInformation.endDeviceSessionSendToServer_Status);

                switch (server_syncing.currentSessionInformation.endDeviceSessionSendToServer_Status)
                {
                    case IDLE:
                    {
                        // Nothing to do
                    }
                    break;

                    case SERVER_CHECK_FOR_UNIQUE_ID:
                    {
                        // Process the oldest one that hasn't been sent to the Server yet. Otherwise the Server may auto close off the existing Device Sessions for this Bed ID
                        cursor.moveToFirst();

                        // Parameters
                        String string__android_database_row = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));
                        long device_end_session_time = cursor.getLong(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME));
                        String string__servers_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_SERVERS_ID));
                        String string__by_servers_device_info_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_BY_SERVERS_DEVICE_INFO_ID));

                        int servers_id = Integer.parseInt(string__servers_id);
                        int by_servers_device_info_id = Integer.parseInt(string__by_servers_device_info_id);
                        int android_database_row = Integer.parseInt(string__android_database_row);

                        server_interface.send_CheckEndDeviceSession(servers_id, device_end_session_time, by_servers_device_info_id, android_database_row);
                        server_syncing.currentSessionInformation.endDeviceSessionSendToServer_Status = CheckServerLinkStatus.CHECKING;
                    }
                    break;

                    case CHECKING:
                    {
                        // Nothing to do as Server doing stuff
                    }
                    break;

                    case SEND_PACKET:
                    {
                        // Process the oldest one that hasn't been sent to the Server yet. Otherwise the Server may auto close off the existing Device Sessions for this Bed ID
                        cursor.moveToFirst();

                        // Parameters
                        String string__android_database_row = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));
                        String string__servers_device_session_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_SERVERS_ID));
                        long device_end_session_time = cursor.getLong(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME));
                        String string__device_end_session_by_user_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_DEVICE_END_SESSION_BY_USER_ID));

                        int android_database_row = Integer.parseInt(string__android_database_row);

                        server_interface.sendEndDeviceSessionToServer(android_database_row, string__servers_device_session_id, device_end_session_time, string__device_end_session_by_user_id);
                    }
                    break;

                    default:
                        break;
                }
            }
            else
            {
                Log.d(TAG, "checkIfEndDeviceSessionPendingToBeSentToServerAndSendIt : Cursor Count is ZERO");
            }

            cursor.close();
        }
        
        return database_rows;
    }
    

    private String getServerDeviceSessionIdFromAndroidDeviceSessionId(String string__device_session_id)
    {
        String servers_device_session_id = "";
       
        String[] projection = {
                TableDeviceSession.COLUMN_ID,
                TableDeviceSession.COLUMN_SERVERS_ID 
                };

        Cursor cursor = content_resolver.query(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS,
                projection, TableDeviceSession.COLUMN_ID + "=" + string__device_session_id, null, null, null);
        
        // There should ONLY be one of these
        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();

                servers_device_session_id = cursor.getString(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_SERVERS_ID));
            }
            cursor.close();
        }

        if (servers_device_session_id.isEmpty())
        {
            Log.e(TAG, "***************************************************************************************************************");
            Log.e(TAG, "getServerDeviceSessionIdFromAndroidDeviceSessionId : Looking for " + string__device_session_id + " but IS EMPTY");
            Log.e(TAG, "***************************************************************************************************************");
        }
        
        return servers_device_session_id;
    }
    
    
    public String getServerPatientSessionIdFromAndroidPatientSessionId(String string__patient_session_id)
    {
        String servers_patient_session_id = "";
       
        String[] projection = {
                TablePatientSession.COLUMN_ID,
                TablePatientSession.COLUMN_SERVERS_ID 
                };

        Cursor cursor = content_resolver.query(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS,
                projection, TablePatientSession.COLUMN_ID + "=" + string__patient_session_id, null, null, null);
        
        // There should ONLY be one of these
        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();

                servers_patient_session_id = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_SERVERS_ID));
            }
            cursor.close();
        }

        if(servers_patient_session_id == null)
        {
        	Log.d(TAG, "getServerPatientSessionIdFromAndroidPatientSessionId : Looking for " + string__patient_session_id + " but NOTHING FOUND, servers_patient_session_id was null");
        }
        else if (servers_patient_session_id.isEmpty())
        {
            Log.d(TAG, "getServerPatientSessionIdFromAndroidPatientSessionId : Looking for " + string__patient_session_id + " but NOTHING FOUND, servers_patient_session_id was empty");
        }
/*
        else
        {
            Log.d(TAG, "getServerPatientSessionIdFromAndroidPatientSessionId : Looking for " + string__patient_session_id + " and found " + servers_patient_session_id);
        }
*/

        return servers_patient_session_id;
    }


    public enum ConnectionEvent
    {
        DEVICE_DISCONNECTED,
        DEVICE_CONNECTED
    }
    
    
    private int getNumberOfRowsPending(QueryType type, String selection)
    {
    	long number_of_rows;
        
        try
        {
            Bundle extras = new Bundle();
            
            switch (type)
            {
                case LIFETOUCH_HEART_BEATS:
                	extras.putString("table", TableLifetouchHeartBeat.TABLE_NAME);
                    break;
    
                case LIFETOUCH_HEART_RATES:
                	extras.putString("table", TableLifetouchHeartRate.TABLE_NAME);
                    break;
                    
                case LIFETOUCH_RESPIRATION_RATES:
                	extras.putString("table", TableLifetouchRespirationRate.TABLE_NAME);
                    break;
                    
                case LIFETOUCH_SETUP_MODE_RAW_SAMPLES:
                	extras.putString("table", TableLifetouchSetupModeRawSample.TABLE_NAME);
                    break;

                case LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES:
                    extras.putString("table", TableLifetouchRawAccelerometerModeSample.TABLE_NAME);
                    break;

                case LIFETOUCH_BATTERY_MEASUREMENTS:
                	extras.putString("table", TableLifetouchBattery.TABLE_NAME);
                    break;

                case LIFETOUCH_PATIENT_ORIENTATIONS:
                	extras.putString("table", TableLifetouchPatientOrientation.TABLE_NAME);
                    break;

                    
                case LIFETEMP_TEMPERATURE_MEASUREMENTS:
                	extras.putString("table", TableLifetempMeasurement.TABLE_NAME);
                    break;
    
                case LIFETEMP_BATTERY_MEASUREMENTS:
                	extras.putString("table", TableLifetempBattery.TABLE_NAME);
                    break;
                
                    
                case PULSE_OX_MEASUREMENTS:
                	extras.putString("table", TableOximeterMeasurement.TABLE_NAME);
                    break;
                    
                case PULSE_OX_INTERMEDIATE_MEASUREMENTS:
                	extras.putString("table", TableOximeterIntermediateMeasurement.TABLE_NAME);
                    break;

                case PULSE_OX_SETUP_MODE_SAMPLES:
                	extras.putString("table", TableOximeterSetupModeRawSample.TABLE_NAME);
                    break;
                    
                case PULSE_OX_BATTERY_MEASUREMENTS:
                	extras.putString("table", TableOximeterBattery.TABLE_NAME);
                    break;
                
                    
                case BLOOD_PRESSURE_MEASUREMENTS:
                	extras.putString("table", TableBloodPressureMeasurement.TABLE_NAME);
                    break;
    
                case BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
                	extras.putString("table", TableBloodPressureBattery.TABLE_NAME);
                    break;


                case WEIGHT_SCALE_MEASUREMENTS:
                    extras.putString("table", TableWeightScaleWeight.TABLE_NAME);
                    break;

                case WEIGHT_SCALE_BATTERY_MEASUREMENTS:
                    extras.putString("table", TableWeightScaleBattery.TABLE_NAME);
                    break;


                case PATIENT_DETAILS:
                	extras.putString("table", TablePatientDetails.TABLE_NAME);
                    break;
                
                case DEVICE_INFO:
                	extras.putString("table", TableDeviceInfo.TABLE_NAME);
                    break;
                    
                case DEVICE_SESSIONS:
                	extras.putString("table", TableDeviceSession.TABLE_NAME);
                    break;
                    
                case PATIENT_SESSIONS:
                	extras.putString("table", TablePatientSession.TABLE_NAME);
                    break;
                    
                case PATIENT_SESSION_FULLY_SYNCED:
                	extras.putString("table", TablePatientSessionsFullySynced.TABLE_NAME);
                    break;

                case CONNECTION_EVENTS:
                	extras.putString("table", TableConnectionEvent.TABLE_NAME);
                    break;
    
                case GATEWAY_STARTUP_TIMES:
                	extras.putString("table", TableDiagnosticsGatewayStartupTimes.TABLE_NAME);
                    break;
    
                case UI_STARTUP_TIMES:
                	extras.putString("table", TableDiagnosticsUiStartupTimes.TABLE_NAME);
                    break;


                case MANUALLY_ENTERED_HEART_RATES:
                	extras.putString("table", TableManuallyEnteredHeartRate.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_RESPIRATION_RATES:
                	extras.putString("table", TableManuallyEnteredRespirationRate.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_TEMPERATURES:
                	extras.putString("table", TableManuallyEnteredTemperature.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
                	extras.putString("table", TableManuallyEnteredSpO2.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
                	extras.putString("table", TableManuallyEnteredBloodPressure.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
                    extras.putString("table", TableManuallyEnteredWeight.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
                	extras.putString("table", TableManuallyEnteredConsciousnessLevel.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
                	extras.putString("table", TableManuallyEnteredSupplementalOxygenLevel.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_ANNOTATIONS:
                    extras.putString("table", TableManuallyEnteredAnnotation.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
                    extras.putString("table", TableManuallyEnteredCapillaryRefillTime.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
                    extras.putString("table", TableManuallyEnteredRespirationDistress.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
                    extras.putString("table", TableManuallyEnteredFamilyOrNurseConcern.TABLE_NAME);
                    break;

                case MANUALLY_ENTERED_URINE_OUTPUT:
                    extras.putString("table", TableManuallyEnteredUrineOutput.TABLE_NAME);
                    break;

                case EARLY_WARNING_SCORES:
                	extras.putString("table", TableEarlyWarningScore.TABLE_NAME);
                    break;

                case ALL_SETUP_MODE_LOGS:
                    extras.putString("table", TableSetupModeLog.TABLE_NAME);
                    break;

                case ALL_AUDITABLE_EVENTS:
                    extras.putString("table", TableAuditTrail.TABLE_NAME);
                    break;

                case EARLY_WARNING_SCORE_THRESHOLD_SETS:
                	extras.putString("table", TableThresholdSet.TABLE_NAME);
                    break;
                    
                case EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS:
                	extras.putString("table", TableThresholdSetAgeBlockDetail.TABLE_NAME);
                    break;
                    
                case EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS:
                	extras.putString("table", TableThresholdSetLevel.TABLE_NAME);
                    break;

                case EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS:
                    extras.putString("table", TableThresholdSetColour.TABLE_NAME);
                    break;

                case SERVER_CONFIGURABLE_TEXT:
                    extras.putString("table", TableServerConfigurableText.TABLE_NAME);
                    break;
            }
            extras.putString("selection", selection);
            
            Bundle count_bundle = content_resolver.call(IsansysPatientGatewayContentProvider.CONTENT_URI, IsansysPatientGatewayContentProvider.CALLABLE_METHOD__GET_COUNT, null, extras);

            if(count_bundle != null)
            {
                number_of_rows = count_bundle.getLong("count");
            }
            else
            {
                number_of_rows = 0;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception in getNumberOfRowsPending for " + type.toString() + ". Exception = " + e.getMessage());
            
            number_of_rows = 0;
        }
        
        return (int)number_of_rows;
    }


    public int checkIfConnectionEventsPendingToBeSentToServerAndSendIt(ServerLink server_interface, ActiveOrOldSession active_or_old_session)
    {
        Cursor cursor = getNumberOfSortedSyncableMeasurementsDataPendingRow(QueryType.CONNECTION_EVENTS, patient_gateway_interface.getAndroidDatabasePatientSessionId(), active_or_old_session);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if (database_rows > 0)
            {
                cursor.moveToLast();                                                        // Move to the oldest non synced sample

                JSONArray json_array = new JSONArray();

                RowTracker row_tracker = new RowTracker();

                for (int i=0; i<database_rows; i++)
                {
                    // Parameters
                    String database_row_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableConnectionEvent.COLUMN_ID));
                    String device_session_number = cursor.getString(cursor.getColumnIndexOrThrow(TableConnectionEvent.COLUMN_DEVICE_SESSION_NUMBER));
                    String patient_session_number = cursor.getString(cursor.getColumnIndexOrThrow(TableConnectionEvent.COLUMN_PATIENT_SESSION_NUMBER));
                    String connected = cursor.getString(cursor.getColumnIndexOrThrow(TableConnectionEvent.COLUMN_CONNECTED));
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TableConnectionEvent.COLUMN_TIMESTAMP));

                    String servers_device_session_id = getServerDeviceSessionIdFromAndroidDeviceSessionId(device_session_number);

                    if(servers_device_session_id.isEmpty())
                    {
                        Log.e(TAG, "**************************************************************");
                        Log.e(TAG, "servers_device_session_id is empty. Returning to prevent crash");
                        Log.e(TAG, "**************************************************************");
                        cursor.close();
                        return 0;
                    }

                    String servers_patient_session_id = getServerPatientSessionIdFromAndroidPatientSessionId(patient_session_number);

                    if(servers_patient_session_id.isEmpty())
                    {
                        Log.e(TAG, "**************************************************************");
                        Log.e(TAG, "servers_patient_session_id is empty. Returning to prevent crash");
                        Log.e(TAG, "**************************************************************");
                        cursor.close();
                        return 0;
                    }

                    row_tracker.addRow(database_row_as_string);

                    try
                    {
                        JSONObject this_measurement = new JSONObject();

                        this_measurement.put("ByDeviceSessionId", servers_device_session_id);
                        this_measurement.put("ByPatientSessionId", servers_patient_session_id);
                        this_measurement.put("Connected", connected);
                        this_measurement.put("Timestamp", Utils.convertTimestampToServerSqlDate(timestamp));

                        json_array.put(this_measurement);
                    }
                    catch (JSONException e1)
                    {
                        e1.printStackTrace();
                    }

                    cursor.moveToPrevious();
                }

                ArrayList<RowRange> row_ranges = row_tracker.getRowRanges();
                logQueryInfo("checkIfConnectionEventsPendingToBeSentToServerAndSendIt", row_ranges, json_array.length());

                server_interface.sendMeasurementsToServer(HttpOperationType.CONNECTION_EVENT, json_array, active_or_old_session, row_ranges);
            }

            cursor.close();
        }

        return database_rows;
    }


    private ObjectNode readCommonSensorMeasurementTablesToJson(Cursor cursor, RowTracker row_tracker, Hashtable<Integer, Integer> server_device_session_id_lookup)
    {
        CommonSensorMeasurementTableInfo sensor_measurement = new CommonSensorMeasurementTableInfo();

        // Keep track of the rows of data that is being sent to the Server so they can be marked as SENT/FAILED
        row_tracker.addRow(cursor.getString(cursor.getColumnIndexOrThrow(Table.COLUMN_ID)));

        long measurement_timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(Table.COLUMN_TIMESTAMP));

        // Convert the timestamp to a format the Server understands (milliseconds)
        sensor_measurement.sql_timestamp = Utils.convertTimestampToServerSqlDate(measurement_timestamp);

        // Convert the timestamp to a format the Server understands (milliseconds)
        sensor_measurement.sql_written_to_android_database_timestamp = Utils.convertTimestampToServerSqlDate(cursor.getLong(cursor.getColumnIndexOrThrow(Table.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP)));

        long measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(Table.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));
        sensor_measurement.measure_expiry_sql_timestamp = Utils.convertTimestampToServerSqlDate(measurement_timestamp + measurement_validity_time_in_seconds * DateUtils.SECOND_IN_MILLIS);

        // Lookup the Servers Device Session ID from the databases Device Session ID
        String device_session_number = cursor.getString(cursor.getColumnIndexOrThrow(Table.COLUMN_DEVICE_SESSION_NUMBER));

        int android_device_session_id = Integer.parseInt(device_session_number);

        int servers_device_session_id;

        if (server_device_session_id_lookup.containsKey(android_device_session_id))
        {
            servers_device_session_id = server_device_session_id_lookup.get(android_device_session_id);
        }
        else
        {
            String string_servers_device_session_id = getServerDeviceSessionIdFromAndroidDeviceSessionId(device_session_number);

            if (string_servers_device_session_id.isEmpty())
            {
                // Invalid
                return null;
            }

            servers_device_session_id = Integer.parseInt(string_servers_device_session_id);

            server_device_session_id_lookup.put(android_device_session_id, servers_device_session_id);
        }

        sensor_measurement.servers_device_session_id = String.valueOf(servers_device_session_id);

        return mapper.valueToTree(sensor_measurement);                                     // Convert the measurement object to JSON
     }


    private ObjectNode readCommonManuallyEnteredMeasurementTablesToJson(Cursor cursor, RowTracker row_tracker)
    {
        CommonManuallyEnteredMeasurementTableInfo sensor_measurement = new CommonManuallyEnteredMeasurementTableInfo();

        // Keep track of the rows of data that is being sent to the Server so they can be marked as SENT/FAILED
        row_tracker.addRow(cursor.getString(cursor.getColumnIndexOrThrow(Table.COLUMN_ID)));

        long measurement_timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(Table.COLUMN_TIMESTAMP));

        // Convert the timestamp to a format the Server understands (milliseconds)
        sensor_measurement.sql_timestamp = Utils.convertTimestampToServerSqlDate(measurement_timestamp);

        // Convert the human readable written to android database timestamp string to a format the Server understands (milliseconds)
        long written_to_android_database_timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(Table.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP));
        sensor_measurement.sql_written_to_android_database_timestamp = Utils.convertTimestampToServerSqlDate(written_to_android_database_timestamp);

        // Get how long the measurement is valid for
        long measurement_validity_time_in_seconds = cursor.getInt(cursor.getColumnIndexOrThrow(Table.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS));
        sensor_measurement.measure_expiry_sql_timestamp = Utils.convertTimestampToServerSqlDate(measurement_timestamp + measurement_validity_time_in_seconds * DateUtils.SECOND_IN_MILLIS);

        // Get the User ID of the QR code
        sensor_measurement.by_user_id = cursor.getString(cursor.getColumnIndexOrThrow(Table.COLUMN_BY_USER_ID));

        // Lookup the Servers Device Session ID from the databases Device Session ID
        String patient_session_number = cursor.getString(cursor.getColumnIndexOrThrow(Table.COLUMN_PATIENT_SESSION_NUMBER));
        String servers_patient_session_id = getServerPatientSessionIdFromAndroidPatientSessionId(patient_session_number);
        if (servers_patient_session_id.isEmpty())
        {
            // Invalid
            return null;
        }
        else
        {
            sensor_measurement.servers_patient_session_id = servers_patient_session_id;

            return mapper.valueToTree(sensor_measurement);                                     // Convert the measurement object to JSON
        }
    }


    private int checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt(String function_name,
                                                                                        QueryType query_type,
                                                                                        ServerLink server_interface,
                                                                                        int desired_device_session_number,
                                                                                        ActiveOrOldSession active_or_old_session,
                                                                                        HttpOperationType http_operation_type)
    {
        Cursor cursor = getNumberOfSortedSyncableMeasurementsDataPendingRow(query_type, desired_device_session_number, active_or_old_session);
        if (cursor != null)
        {
            if(cursor.getCount() > 0)
            {
                cursor.moveToLast();                                                        // Move to the oldest non synced sample

                ArrayNode json_array = mapper.createArrayNode();                            // Jackson equivalent of JSONArray
                RowTracker row_tracker = new RowTracker();                                  // Keep track of the Android database rows sent to the Server

                // Android ID, Servers ID
                Hashtable<Integer, Integer> server_device_session_id_lookup = new Hashtable<>();

                while(!cursor.isBeforeFirst())
                {
                    ObjectNode json_object = readCommonSensorMeasurementTablesToJson(cursor, row_tracker, server_device_session_id_lookup);
                    if (json_object == null)
                    {
                        // Problem looking up Servers Device Session ID from the Android Device Session ID
                        cursor.close();
                        return 0;
                    }

                    List<DatabaseToJson> measurement_specific_info = DatabaseToJson.getMeasurementSpecificInfo(http_operation_type);

                    // Add Measurement Specific info to the JSON object
                    for (DatabaseToJson database_table : measurement_specific_info)
                    {
                        json_object.put(database_table.json_name, cursor.getString(cursor.getColumnIndexOrThrow(database_table.database_name)));
                    }

                    json_array.add(json_object);

                    cursor.moveToPrevious();
                }

                cursor.close();

                ArrayList<RowRange> row_ranges = row_tracker.getRowRanges();

                for (RowRange row_range : row_ranges)
                {
                    Log.d(TAG, function_name + " : Sending Local Rows " + row_range.start + " to " + row_range.end + " json_array_length = " + json_array.size());
                }

                server_interface.sendMeasurementsToServer(http_operation_type, json_array, active_or_old_session, row_ranges);

                return json_array.size();
            }

            Log.d(TAG, "checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt : Cursor count is ZERO for " + query_type);

            cursor.close();
        }

        return 0;
    }


    private int checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt(String function_name,
                                                                                                 QueryType query_type,
                                                                                                 ServerLink server_interface,
                                                                                                 int desired_patient_session_number,
                                                                                                 ActiveOrOldSession active_or_old_session,
                                                                                                 HttpOperationType http_operation_type)
    {
        Cursor cursor = getNumberOfSortedSyncableMeasurementsDataPendingRow(query_type, desired_patient_session_number, active_or_old_session);
        if (cursor != null)
        {
            if(cursor.getCount() > 0)
            {
                cursor.moveToLast();                                                        // Move to the oldest non synced sample

                ArrayNode json_array = mapper.createArrayNode();                            // Jackson equivalent of JSONArray
                RowTracker row_tracker = new RowTracker();                                  // Keep track of the Android database rows sent to the Server

                while(!cursor.isBeforeFirst())
                {
                    ObjectNode json_object = readCommonManuallyEnteredMeasurementTablesToJson(cursor, row_tracker);
                    if (json_object == null)
                    {
                        // Problem looking up Servers Device Session ID from the Android Device Session ID
                        cursor.close();
                        return 0;
                    }

                    List<DatabaseToJson> measurement_specific_info = DatabaseToJson.getMeasurementSpecificInfo(http_operation_type);

                    // Add Measurement Specific info to the JSON object
                    for (DatabaseToJson database_table : measurement_specific_info)
                    {
                        json_object.put(database_table.json_name, cursor.getString(cursor.getColumnIndexOrThrow(database_table.database_name)));
                    }

                    json_array.add(json_object);

                    cursor.moveToPrevious();
                }

                cursor.close();

                ArrayList<RowRange> row_ranges = row_tracker.getRowRanges();

                for (RowRange row_range : row_ranges)
                {
                    Log.d(TAG, function_name + " : Sending Local Rows " + row_range.start + " to " + row_range.end + " json_array_length = " + json_array.size());
                }

                server_interface.sendMeasurementsToServer(http_operation_type, json_array, active_or_old_session, row_ranges);

                return json_array.size();
            }

            Log.d(TAG, "checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt : Cursor count is ZERO");

            cursor.close();
        }

        return 0;
    }


    public int checkIfMoreLifetouchHeartRateDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreLifetouchHeartRateDataPendingToBeSentToServerAndSendIt",
                QueryType.LIFETOUCH_HEART_RATES,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.LIFETOUCH_HEART_RATE);
    }


    public int checkIfMoreLifetouchRespirationRateDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreLifetouchRespirationRateDataPendingToBeSentToServerAndSendIt",
                QueryType.LIFETOUCH_RESPIRATION_RATES,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.LIFETOUCH_RESPIRATION_RATE);
    }


    public int checkIfMoreLifetouchHeartBeatDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreLifetouchHeartBeatDataPendingToBeSentToServerAndSendIt",
                QueryType.LIFETOUCH_HEART_BEATS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.LIFETOUCH_HEART_BEAT);
    }


    public int checkIfMoreLifetouchSetupModeDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreLifetouchSetupModeDataPendingToBeSentToServerAndSendIt",
                QueryType.LIFETOUCH_SETUP_MODE_RAW_SAMPLES,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.LIFETOUCH_SETUP_MODE_SAMPLE);
    }


    public int checkIfMoreLifetouchRawAccelerometerModeDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreLifetouchRawAccelerometerModeDataPendingToBeSentToServerAndSendIt",
                QueryType.LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE);
    }


    public int checkIfMoreLifetouchBatteryDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreLifetouchBatteryDataPendingToBeSentToServerAndSendIt",
                QueryType.LIFETOUCH_BATTERY_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.LIFETOUCH_BATTERY);
    }


    public int checkIfMoreLifetempTemperatureDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreLifetempTemperatureDataPendingToBeSentToServerAndSendIt",
                QueryType.LIFETEMP_TEMPERATURE_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.LIFETEMP_TEMPERATURE);
    }
    
    
    public int checkIfMoreLifetempBatteryDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreLifetempBatteryDataPendingToBeSentToServerAndSendIt",
                QueryType.LIFETEMP_BATTERY_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.LIFETEMP_BATTERY);
    }
    
    
    public int checkIfMorePulseOxMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMorePulseOxMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.PULSE_OX_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.PULSE_OX_MEASUREMENT);
    }
    
    
    public int checkIfMorePulseOxIntermediateMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMorePulseOxIntermediateMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.PULSE_OX_INTERMEDIATE_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.PULSE_OX_INTERMEDIATE_MEASUREMENT);
    }


    public int checkIfMorePulseOxSetupModeDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMorePulseOxSetupModeDataPendingToBeSentToServerAndSendIt",
                QueryType.PULSE_OX_SETUP_MODE_SAMPLES,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.PULSE_OX_SETUP_MODE_SAMPLE);
    }
    
    
    public int checkIfMorePulseOxBatteryDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMorePulseOxBatteryDataPendingToBeSentToServerAndSendIt",
                QueryType.PULSE_OX_BATTERY_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.PULSE_OX_BATTERY);
    }
    
    
    public int checkIfMoreBloodPressureMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreBloodPressureMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.BLOOD_PRESSURE_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.BLOOD_PRESSURE_MEASUREMENT);
    }
    
    
    public int checkIfMoreBloodPressureBatteryDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreBloodPressureBatteryDataPendingToBeSentToServerAndSendIt",
                QueryType.BLOOD_PRESSURE_BATTERY_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.BLOOD_PRESSURE_BATTERY);
    }


    public int checkIfMoreWeightScaleMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreWeightScaleMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.WEIGHT_SCALE_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.WEIGHT_SCALE_MEASUREMENT);
    }


    public int checkIfMoreWeightScaleBatteryDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreSensorMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreWeightScaleBatteryDataPendingToBeSentToServerAndSendIt",
                QueryType.WEIGHT_SCALE_BATTERY_MEASUREMENTS,
                server_interface,
                desired_device_session_number,
                active_or_old_session,
                HttpOperationType.WEIGHT_SCALE_BATTERY);
    }


    private void closeOffAnyOutstandingDeviceSessions(long device_session_end_time, int device_session_ended_by_user_id)
    {
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS;
        
        String[] device_sessions_projection = {
                TableDeviceSession.COLUMN_ID, 
                TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME
                };
        
        String selection = TableDeviceSession.COLUMN_DEVICE_END_SESSION_TIME + " is 0";

        Cursor cursor = content_resolver.query(uri, device_sessions_projection, selection, null, null, null);
        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    int local_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableDeviceSession.COLUMN_ID));

                    endDeviceSession(local_database_row, device_session_end_time, device_session_ended_by_user_id);

                    cursor.moveToNext();
                }
            }

            cursor.close();
        }
    }
    
    
    private void closeOffAnyOutstandingPatientSessions(long patient_session_end_time, int patient_session_ended_by_user_id)
    {
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS;
        
        String[] device_sessions_projection = {
                TablePatientSession.COLUMN_ID, 
                TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME
                };
        
        String selection = TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME + " is 0";

        Cursor cursor = content_resolver.query(uri, device_sessions_projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    int local_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_ID));

                    endPatientSession(local_database_row, patient_session_end_time, patient_session_ended_by_user_id);

                    cursor.moveToNext();
                }
            }

            cursor.close();
        }
    }


    public int getOutstandingPatientSession(PatientGatewayService.PatientSessionInfo patient_session_info)
    {
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS;

        String[] device_sessions_projection = {
                TablePatientSession.COLUMN_ID,
                TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID,
                TablePatientSession.COLUMN_SERVERS_ID,
                TablePatientSession.COLUMN_PATIENT_START_SESSION_TIME,
                TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME
        };

        String selection = TablePatientSession.COLUMN_PATIENT_END_SESSION_TIME + " is 0";

        Cursor cursor = gateway_context_interface.getAppContext().getContentResolver().query(uri, device_sessions_projection, selection, null, null, null);

        int patient_details_id = 0;

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                if(cursor.getCount() > 1)
                {
                    Log.e(TAG, "getOutstandingPatientSession : more than one open session - cannot safely resume");
                    return patient_details_id;
                }

                cursor.moveToFirst();

                patient_session_info.android_database_patient_session_id = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_ID));

                try
                {
                    patient_session_info.server_patient_session_id = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_SERVERS_ID));
                }
                catch(Exception e)
                {
                    Log.e(TAG, "getOutstandingPatientSession : Could not get server_patient_session_id - may not be set");

                    Log.e(TAG, "getOutstandingPatientSession : " + e);
                }

                patient_session_info.patient_session_running = true;
                patient_session_info.start_session_time = cursor.getLong(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_PATIENT_START_SESSION_TIME));

                patient_details_id = cursor.getInt(cursor.getColumnIndexOrThrow(TablePatientSession.COLUMN_BY_PATIENT_DETAILS_ID));
            }

            cursor.close();
        }

        return patient_details_id;
    }
    
    
    public void closeOffAnyOutstandingSessions()
    {
        long patient_session_end_time = patient_gateway_interface.getNtpTimeNowInMilliseconds();
        int patient_session_ended_by_user_id = 0;

        closeOffAnyOutstandingDeviceSessions(patient_session_end_time, patient_session_ended_by_user_id);
        closeOffAnyOutstandingPatientSessions(patient_session_end_time, patient_session_ended_by_user_id);
    }


    public void resetDatabase(boolean delete_ews_threshold_sets)
    {
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_BEATS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RESPIRATION_RATES, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_RATES, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_BATTERY_MEASUREMENTS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_PATIENT_ORIENTATION, null, null);
            
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_TEMPERATURE_MEASUREMENTS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_BATTERY_MEASUREMENTS, null, null);
        
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_MEASUREMENTS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_INTERMEDIATE_MEASUREMENTS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_BATTERY_MEASUREMENTS, null, null);

        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, null, null);

        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_MEASUREMENTS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_WEIGHT_SCALE_BATTERY_MEASUREMENTS, null, null);

        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_CONNECTION_EVENTS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_DETAILS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_INFO, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_DEVICE_SESSIONS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS_FULLY_SYNCED, null, null);

        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_DIAGNOSTICS_GATEWAY_STARTUP_TIMES, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_DIAGNOSTICS_UI_STARTUP_TIMES, null, null);

        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_HEART_RATES, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SPO2, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_DISTRESS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_WEIGHT, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_URINE_OUTPUT, null, null);

        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORES, null, null);

        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_SETUP_MODE_LOGS, null, null);

        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_WARDS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_BEDS, null, null);

        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_AUDIT_TRAIL, null, null);

        deleteServerConfigurableText();

        deleteWebPages();

        if (delete_ews_threshold_sets)
        {
            deleteEarlyWarningScoreThresholdSets();
        }
    }


    public void deleteEarlyWarningScoreThresholdSets()
    {
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SETS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS, null, null);
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS, null, null);
    }


    public void deleteServerConfigurableText()
    {
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_SERVER_CONFIGURABLE_TEXT, null, null);
    }


    public void deleteWebPages()
    {
        content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_VIEWABLE_WEB_PAGES, null, null);
    }


    public void storeManuallyEnteredHeartRateMeasurement(int patient_session_number, MeasurementManuallyEnteredHeartRate measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredHeartRate.COLUMN_HEART_RATE, measurement.heart_rate);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_HEART_RATES, content_values);
    }


    public int checkIfMoreManuallyEnteredHeartRateDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredHeartRateDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_HEART_RATES,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_HEART_RATES);
    }


    public void updateManuallyEnteredHeartRateMeasurementSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_HEART_RATES", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_HEART_RATES, row_range);
        }
    }


    public void storeManuallyEnteredRespirationRateMeasurement(int patient_session_number, MeasurementManuallyEnteredRespirationRate measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredRespirationRate.COLUMN_RESPIRATION_RATE, measurement.respiration_rate);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES, content_values);
    }


    public int checkIfMoreManuallyEnteredRespirationRateDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredRespirationRateDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_RESPIRATION_RATES,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_RESPIRATION_RATES);
    }


    public void updateManuallyEnteredRespirationRateMeasurementSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES, row_range);
        }
    }


    public void storeManuallyEnteredTemperatureMeasurement(int patient_session_number, MeasurementManuallyEnteredTemperature measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredTemperature.COLUMN_TEMPERATURE, new BigDecimal(measurement.temperature).doubleValue());

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE, content_values);
    }


    public int checkIfMoreManuallyEnteredTemperatureMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredTemperatureMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_TEMPERATURES,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_TEMPERATURES);
    }


    public void updateManuallyEnteredTemperatureMeasurementSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE, row_range);
        }
    }


    public void storeManuallyEnteredOximeterMeasurement(int patient_session_number, MeasurementManuallyEnteredSpO2 measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredSpO2.COLUMN_SPO2, measurement.SpO2);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SPO2, content_values);
    }


    public int checkIfMoreManuallyEnteredOximeterMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredOximeterMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_SPO2_MEASUREMENTS,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_SPO2_MEASUREMENTS);
    }


    public void updateManuallyEnteredSpO2MeasurementSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_SPO2", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SPO2, row_range);
        }
    }


    public void storeManuallyEnteredBloodPressureMeasurement(int patient_session_number, MeasurementManuallyEnteredBloodPressure measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredBloodPressure.COLUMN_SYSTOLIC, measurement.systolic);
        content_values.put(TableManuallyEnteredBloodPressure.COLUMN_DIASTOLIC, measurement.diastolic);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE, content_values);
    }


    public int checkIfMoreManuallyEnteredBloodPressureMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredBloodPressureMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS);
    }


    public void updateManuallyEnteredBloodPressureMeasurementSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE, row_range);
        }
    }


    public void storeManuallyEnteredWeightMeasurement(int patient_session_number, MeasurementManuallyEnteredWeight measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredWeight.COLUMN_WEIGHT, measurement.weight);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_WEIGHT, content_values);
    }


    public int checkIfMoreManuallyEnteredWeightMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredWeightMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS);
    }


    public void updateManuallyEnteredWeightMeasurementSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_WEIGHT", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_WEIGHT, row_range);
        }
    }


    public void storeManuallyEnteredConsciousnessLevelMeasurement(int patient_session_number, MeasurementConsciousnessLevel measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredConsciousnessLevel.COLUMN_CONSCIOUSNESS_LEVEL, measurement.value);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, content_values);
    }


    public int checkIfMoreManuallyEnteredConsciousnessLevelMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredConsciousnessLevelMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS);
    }


    public void updateManuallyEnteredConsciousnessLevelMeasurementSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, row_range);
        }
    }


    public void storeManuallyEnteredSupplementalOxygenLevelMeasurement(int patient_session_number, MeasurementSupplementalOxygenLevel measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredSupplementalOxygenLevel.COLUMN_SUPPLEMENTAL_OXYGEN_LEVEL, measurement.value);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, content_values);
    }


    public int checkIfMoreManuallyEnteredSupplementalOxygenLevelMeasurementDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredSupplementalOxygenLevelMeasurementDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS);
    }


    public void updateManuallyEnteredSupplementalOxygenLevelMeasurementSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, row_range);
        }
    }


    public void storeManuallyEnteredAnnotation(int patient_session_number, MeasurementAnnotation measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredAnnotation.COLUMN_ANNOTATION_TEXT, measurement.annotation);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS, content_values);
    }


    public int checkIfMoreManuallyEnteredAnnotationDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredAnnotationDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_ANNOTATIONS,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_ANNOTATIONS);
    }


    public void updateManuallyEnteredAnnotationSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS, row_range);
        }
    }


    public void storeManuallyEnteredCapillaryRefillTime(int patient_session_number, MeasurementCapillaryRefillTime measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredCapillaryRefillTime.COLUMN_CAPILLARY_REFILL_TIME, measurement.value);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, content_values);
    }


    public int checkIfMoreManuallyEnteredCapillaryRefillTimeDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredCapillaryRefillTimeDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME);
    }


    public void updateManuallyEnteredCapillaryRefillTimeSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, row_range);
        }
    }


    public void storeManuallyEnteredRespirationDistress(int patient_session_number, MeasurementRespirationDistress measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredRespirationDistress.COLUMN_RESPIRATION_DISTRESS, measurement.value);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_DISTRESS, content_values);
    }


    public int checkIfMoreManuallyEnteredRespirationDistressDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredRespirationDistressDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_RESPIRATION_DISTRESS,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_RESPIRATION_DISTRESS);
    }


    public void updateManuallyEnteredRespirationDistressTimeSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "updateManuallyEnteredRespirationDistressTimeSentToServerStatus", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_DISTRESS, row_range);
        }
    }


    public void storeManuallyEnteredFamilyOrNurseConcern(int patient_session_number, MeasurementFamilyOrNurseConcern measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredFamilyOrNurseConcern.COLUMN_CONCERN, measurement.concern);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, content_values);
    }


    public int checkIfMoreManuallyEnteredFamilyOrNurseConcernDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredFamilyOrNurseConcernDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN);
    }


    public void updateManuallyEnteredFamilyOrNurseConcernTimeSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "updateManuallyEnteredFamilyOrNurseConcernTimeSentToServerStatus", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, row_range);
        }
    }


    public void storeManuallyEnteredUrineOutput(int patient_session_number, MeasurementUrineOutput measurement, int by_user_id)
    {
        ContentValues content_values = createManuallyEnteredMeasurementContentValues(patient_session_number, measurement, by_user_id);
        content_values.put(TableManuallyEnteredUrineOutput.COLUMN_URINE_OUTPUT, measurement.urine_output);

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_URINE_OUTPUT, content_values);
    }


    public int checkIfMoreManuallyEnteredUrineOutputDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        return checkIfMoreManuallyEnteredMeasurementDataPendingToBeSentToServerAndSendIt("checkIfMoreManuallyEnteredUrineOutputDataPendingToBeSentToServerAndSendIt",
                QueryType.MANUALLY_ENTERED_URINE_OUTPUT,
                server_interface,
                desired_patient_session_number,
                active_or_old_session,
                HttpOperationType.MANUALLY_ENTERED_URINE_OUTPUT);
    }


    public void updateManuallyEnteredUrineOutputTimeSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "updateManuallyEnteredUrineOutputTimeSentToServerStatus", IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_URINE_OUTPUT, row_range);
        }
    }


    public boolean patientSessionFullySyncedRowExistsForAndroidPatientSessionNumber(int android_patient_session_number)
    {
        boolean return_value = false;

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS_FULLY_SYNCED;

        String[] projection = {
                TablePatientSessionsFullySynced.COLUMN_ANDROID_PATIENT_SESSION_NUMBER,
        };

        String selection = TablePatientSessionsFullySynced.COLUMN_ANDROID_PATIENT_SESSION_NUMBER + "=" + android_patient_session_number;

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                // Then a record for this Android Patient Session number is already in the database
                Log.d(TAG, "patientSessionFullySyncedRowExistsForAndroidPatientSessionNumber : already database row for this session " + android_patient_session_number);
                return_value = true;
            }
            else
            {
                // No record - add new data

                Log.d(TAG, "patientSessionFullySyncedRowExistsForAndroidPatientSessionNumber : no database row for this session " + android_patient_session_number);
                return_value = false;
            }

            cursor.close();
        }

        return return_value;
    }


    public void storePatientSessionFullySynced(int android_patient_session_number, int servers_patient_session_number, long timestamp)
    {
        ContentValues content_values = new ContentValues();
        content_values.put(TablePatientSessionsFullySynced.COLUMN_ANDROID_PATIENT_SESSION_NUMBER, android_patient_session_number);
        content_values.put(TablePatientSessionsFullySynced.COLUMN_SERVER_PATIENT_SESSION_NUMBER, servers_patient_session_number);
        content_values.put(TablePatientSessionsFullySynced.COLUMN_TIMESTAMP, timestamp);
        content_values.put(TablePatientSessionsFullySynced.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);
        
        // Cant be the Async version as already running via an ASyncTask
        insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS_FULLY_SYNCED, content_values);
    }


    public RowsPending checkIfPatientSessionFullySyncedRecordsPendingToBeSentToServer()
    {
        RowsPending rowsPending = new RowsPending();

        String selection;
        selection = TablePatientSessionsFullySynced.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 AND " + TablePatientSessionsFullySynced.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";
        rowsPending.rows_pending_syncable = getNumberOfRowsPending(QueryType.PATIENT_SESSION_FULLY_SYNCED, selection);

        selection = TablePatientSessionsFullySynced.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1";
        rowsPending.rows_pending_but_failed = getNumberOfRowsPending(QueryType.PATIENT_SESSION_FULLY_SYNCED, selection);

        return rowsPending;
    }


    public int checkIfPatientSessionFullySyncedRecordsPendingToBeSentToServerAndSendIt(ServerLink server_interface)
    {
        // Create the Cursor
        
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS_FULLY_SYNCED;

        String[] projection = {
                TablePatientSessionsFullySynced.COLUMN_ID, 
                TablePatientSessionsFullySynced.COLUMN_ANDROID_PATIENT_SESSION_NUMBER, 
                TablePatientSessionsFullySynced.COLUMN_SERVER_PATIENT_SESSION_NUMBER,
                TablePatientSessionsFullySynced.COLUMN_TIMESTAMP,
                TablePatientSessionsFullySynced.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED
                };
        
        Cursor cursor;
        String sortOrder = TablePatientSessionsFullySynced.COLUMN_ID + " DESC";
        
        String selection = TablePatientSessionsFullySynced.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0";
        String[] selectionArgs = { };
        cursor = content_resolver.query(uri, projection, selection, selectionArgs, sortOrder, null);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if (database_rows > 0)
            {
                cursor.moveToLast();                                                        // Move to the oldest non synced sample

                JSONArray json_array = new JSONArray();

                RowTracker row_tracker = new RowTracker();

                for (int i=0; i<database_rows; i++)
                {
                    // Parameters
                    String database_row_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSessionsFullySynced.COLUMN_ID));
                    String server_patient_session_number = cursor.getString(cursor.getColumnIndexOrThrow(TablePatientSessionsFullySynced.COLUMN_SERVER_PATIENT_SESSION_NUMBER));

                    row_tracker.addRow(database_row_as_string);

                    try
                    {
                        JSONObject this_measurement = new JSONObject();

                        this_measurement.put("PatientSessionId", server_patient_session_number);

                        json_array.put(this_measurement);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    cursor.moveToPrevious();
                }

                ArrayList<RowRange> row_ranges = row_tracker.getRowRanges();
                logQueryInfo("checkIfPatientSessionFullySyncedRecordsPendingToBeSentToServerAndSendIt", row_ranges, json_array.length());

                server_interface.sendMeasurementsToServer(HttpOperationType.PATIENT_SESSION_FULLY_SYNCED, json_array, ActiveOrOldSession.INVALID, row_ranges);
            }

            cursor.close();
        }
        
        return database_rows;
    }


    public void updatePatientSessionFullySyncedSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_PATIENT_SESSIONS_FULLY_SYNCED", IsansysPatientGatewayContentProvider.CONTENT_URI_PATIENT_SESSIONS_FULLY_SYNCED, row_range);
        }
    }


    public void storeEarlyWarningScore(int patient_session_number, int device_session_number, int early_warning_score, int max_possible, boolean is_alert, int trend_direction, long timestamp)
    {
        ContentValues content_values = new ContentValues();
        content_values.put(TableEarlyWarningScore.COLUMN_PATIENT_SESSION_NUMBER, patient_session_number);
        content_values.put(TableEarlyWarningScore.COLUMN_DEVICE_SESSION_NUMBER, device_session_number);
        content_values.put(TableEarlyWarningScore.COLUMN_EARLY_WARNING_SCORE, early_warning_score);
        content_values.put(TableEarlyWarningScore.COLUMN_MAX_POSSIBLE, max_possible);
        content_values.put(TableEarlyWarningScore.COLUMN_IS_SPECIAL_ALERT, is_alert);
        content_values.put(TableEarlyWarningScore.COLUMN_TREND_DIRECTION, trend_direction);
        content_values.put(TableEarlyWarningScore.COLUMN_TIMESTAMP, timestamp);
        content_values.put(TableEarlyWarningScore.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);
        content_values.put(TableEarlyWarningScore.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP, getTimeNow());
        
        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORES, content_values);
    }


    public int checkIfMoreEarlyWarningScoresPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_patient_session_number, ActiveOrOldSession active_or_old_session)
    {
        Cursor cursor = getNumberOfSortedSyncableMeasurementsDataPendingRow(QueryType.EARLY_WARNING_SCORES, desired_patient_session_number, active_or_old_session);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if (database_rows > 0)
            {
                cursor.moveToLast();                                                        // Move to the oldest non synced sample

                JSONArray json_array = new JSONArray();

                RowTracker row_tracker = new RowTracker();

                for (int i=0; i<database_rows; i++)
                {
                    // Parameters
                    String database_row_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableEarlyWarningScore.COLUMN_ID));
                    String patient_session_number = cursor.getString(cursor.getColumnIndexOrThrow(TableEarlyWarningScore.COLUMN_PATIENT_SESSION_NUMBER));
                    String device_session_number = cursor.getString(cursor.getColumnIndexOrThrow(TableEarlyWarningScore.COLUMN_DEVICE_SESSION_NUMBER));
                    String early_warning_score = cursor.getString(cursor.getColumnIndexOrThrow(TableEarlyWarningScore.COLUMN_EARLY_WARNING_SCORE));
                    String max_possible = cursor.getString(cursor.getColumnIndexOrThrow(TableEarlyWarningScore.COLUMN_MAX_POSSIBLE));
                    String trend_direction = cursor.getString(cursor.getColumnIndexOrThrow(TableEarlyWarningScore.COLUMN_TREND_DIRECTION));

                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TableEarlyWarningScore.COLUMN_TIMESTAMP));

                    long written_to_android_database_timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TableEarlyWarningScore.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP));
                    
                    String servers_patient_session_id = getServerPatientSessionIdFromAndroidPatientSessionId(patient_session_number);
                    
                    String servers_device_session_id = getServerDeviceSessionIdFromAndroidDeviceSessionId(device_session_number);
                    if (servers_device_session_id.isEmpty())
                    {
                        Log.e(TAG, "**************************************************************");
                        Log.e(TAG, "servers_device_session_id is empty. Returning to prevent crash");
                        Log.e(TAG, "**************************************************************");
                        cursor.close();
                        return 0;
                    }

                    row_tracker.addRow(database_row_as_string);

                    try
                    {
                        JSONObject this_measurement = new JSONObject();

                        this_measurement.put("PatientSessionId", servers_patient_session_id);
                        this_measurement.put("ByDevSessionId", servers_device_session_id);
                        this_measurement.put("EarlyWarningScore", early_warning_score);
                        this_measurement.put("MaxPossibleScore", max_possible);
                        this_measurement.put("TrendDirection", trend_direction);
                        this_measurement.put("Timestamp", Utils.convertTimestampToServerSqlDate(timestamp));
                        this_measurement.put("WrittenToAndroidDatabaseTimestamp", Utils.convertTimestampToServerSqlDate(written_to_android_database_timestamp));

                        json_array.put(this_measurement);
                    }
                    catch (JSONException e1)
                    {
                        e1.printStackTrace();
                    }

                    cursor.moveToPrevious();
                }

                ArrayList<RowRange> row_ranges = row_tracker.getRowRanges();
                logQueryInfo("checkIfMoreEarlyWarningScoresPendingToBeSentToServerAndSendIt", row_ranges, json_array.length());

                server_interface.sendMeasurementsToServer(HttpOperationType.EARLY_WARNING_SCORES, json_array, active_or_old_session, row_ranges);
            }

            cursor.close();
        }
        
        return database_rows;
    }


    public void updateEarlyWarningScoresSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_EARLY_WARNING_SCORES", IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORES, row_range);
        }
    }


    public void updateAuditableEventsSentToServerStatusAndDeleteFromGatewayDatabaseIfSucceeded(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_AUDIT_TRAIL", IsansysPatientGatewayContentProvider.CONTENT_URI_AUDIT_TRAIL, row_range);
        }

        if (success)
        {
            // Audit events can be deleted after they are synced

            for (RowRange row_range : row_ranges)
            {
                String auditTrailRowsToDelete = Table.COLUMN_ID + " BETWEEN " + row_range.start + " AND " + row_range.end;

                content_resolver.delete(IsansysPatientGatewayContentProvider.CONTENT_URI_AUDIT_TRAIL, auditTrailRowsToDelete, null);
            }
        }
    }


    public int storeOrUpdateDefaultThresholdSet(String name, boolean default_set, int servers_id)
    {
        int local_database_row = -1;

        // Find all rows of TableThresholdSet where COLUMN_SERVERS_ID = 'servers_id'
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SETS;
        String[] projection = {
                TableThresholdSet.COLUMN_ID,
                TableThresholdSet.COLUMN_NAME,
                TableThresholdSet.COLUMN_SERVERS_ID, 
                TableThresholdSet.COLUMN_IS_DEFAULT,
                };
        String selection = TableThresholdSet.COLUMN_SERVERS_ID + "=" + servers_id;

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        if (cursor != null)
        {
            ContentValues content_values = new ContentValues();
            content_values.put(TableThresholdSet.COLUMN_NAME, name);
            content_values.put(TableThresholdSet.COLUMN_IS_DEFAULT, default_set);
            content_values.put(TableThresholdSet.COLUMN_SERVERS_ID, servers_id);
            content_values.put(TableThresholdSet.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, true);

            if (cursor.getCount() > 0)
            {
                // Database already contains this row from the Server. Update the existing row
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    local_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSet.COLUMN_ID));
                    //updateContentValuesAtContentUri(uri, TableThresholdSet.COLUMN_NAME, local_database_row, name);
                    
                    String strFilter = TableThresholdSet.COLUMN_ID + "=" + local_database_row;
                    updateContentsLowLevel(uri, content_values, strFilter);
                    
                    cursor.moveToNext();
                }
            }
            else
            {
                // Database does NOT already have this info - Insert the new row
                local_database_row = insertContentValuesAtContentUri(uri, content_values);
            }
            
            cursor.close();
        }
        
        return local_database_row;
    }


    public int storeOrUpdateDefaultThresholdSetAgeBlockDetail(ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail, int local_threshold_set_id)
    {
        int local_database_row = -1;
        
        ContentValues content_values = new ContentValues();
        content_values.put(TableThresholdSetAgeBlockDetail.COLUMN_AGE_BOTTOM, thresholdSetAgeBlockDetail.age_range_bottom);
        content_values.put(TableThresholdSetAgeBlockDetail.COLUMN_AGE_TOP, thresholdSetAgeBlockDetail.age_range_top);
        content_values.put(TableThresholdSetAgeBlockDetail.COLUMN_DISPLAY_NAME, thresholdSetAgeBlockDetail.display_name);
        content_values.put(TableThresholdSetAgeBlockDetail.COLUMN_IMAGE_BINARY, thresholdSetAgeBlockDetail.image_binary);
        content_values.put(TableThresholdSetAgeBlockDetail.COLUMN_IS_ADULT, thresholdSetAgeBlockDetail.is_adult);
        content_values.put(TableThresholdSetAgeBlockDetail.COLUMN_SERVERS_ID, thresholdSetAgeBlockDetail.servers_database_row_id);
        content_values.put(TableThresholdSetAgeBlockDetail.COLUMN_THRESHOLD_SET_ID, local_threshold_set_id);
        content_values.put(TableThresholdSetAgeBlockDetail.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, true);
        
        // Find all rows of TableThresholdSet where COLUMN_SERVERS_ID = 'servers_id' 
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_AGE_BLOCK_DETAILS;
        String[] projection = {
                TableThresholdSetAgeBlockDetail.COLUMN_ID,
                TableThresholdSetAgeBlockDetail.COLUMN_AGE_BOTTOM,
                TableThresholdSetAgeBlockDetail.COLUMN_AGE_TOP,
                TableThresholdSetAgeBlockDetail.COLUMN_DISPLAY_NAME,
                TableThresholdSetAgeBlockDetail.COLUMN_IS_ADULT,
                TableThresholdSetAgeBlockDetail.COLUMN_IMAGE_BINARY,
                TableThresholdSetAgeBlockDetail.COLUMN_SERVERS_ID, 
                };
        String selection = TableThresholdSetAgeBlockDetail.COLUMN_SERVERS_ID + "=" + thresholdSetAgeBlockDetail.servers_database_row_id;

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                // Database already contains this row from the Server. Update the existing row
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    local_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSetAgeBlockDetail.COLUMN_ID));
                    //updateContentValuesAtContentUri(uri, TableThresholdSet.COLUMN_NAME, local_database_row, name);
                    
                    String strFilter = TableThresholdSetAgeBlockDetail.COLUMN_ID + "=" + local_database_row;
                    updateContentsLowLevel(uri, content_values, strFilter);
                    
                    cursor.moveToNext();
                }
            }
            else
            {
                // Database does NOT already have this info - Insert the new row
                local_database_row = insertContentValuesAtContentUri(uri, content_values);
            }
            
            cursor.close();
        }
        
        return local_database_row;
    }
    
    
    public int storeOrUpdateDefaultThresholdSetLevel(float range_bottom,
                                                     float range_top,
                                                     int early_warning_score,
                                                     int measurement_type,
                                                     String measurement_type_as_string,
                                                     String display_text,
                                                     String information_text,
                                                     int servers_id,
                                                     int local_threshold_set_age_block_details_id)
    {
        int local_database_row = -1;
        
        ContentValues content_values = new ContentValues();
        content_values.put(TableThresholdSetLevel.COLUMN_SERVERS_ID, servers_id);
        content_values.put(TableThresholdSetLevel.COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID, local_threshold_set_age_block_details_id);
        content_values.put(TableThresholdSetLevel.COLUMN_RANGE_BOTTOM, range_bottom);
        content_values.put(TableThresholdSetLevel.COLUMN_RANGE_TOP, range_top);
        content_values.put(TableThresholdSetLevel.COLUMN_EARLY_WARNING_SCORE, early_warning_score);
        content_values.put(TableThresholdSetLevel.COLUMN_MEASUREMENT_TYPE, measurement_type);
        content_values.put(TableThresholdSetLevel.COLUMN_MEASUREMENT_TYPE_AS_STRING, measurement_type_as_string);
        content_values.put(TableThresholdSetLevel.COLUMN_DISPLAY_TEXT, display_text);
        content_values.put(TableThresholdSetLevel.COLUMN_INFORMATION_TEXT, information_text);
        content_values.put(TableThresholdSetLevel.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, true);
        
        // Find all rows of TableThresholdSet where COLUMN_SERVERS_ID = 'servers_id' 
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_LEVELS;
        String[] projection = {
                TableThresholdSetLevel.COLUMN_ID,
                TableThresholdSetLevel.COLUMN_SERVERS_ID, 
                };
        String selection = TableThresholdSetLevel.COLUMN_SERVERS_ID + "=" + servers_id;

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                // Database already contains this row from the Server. Update the existing row
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    local_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSetLevel.COLUMN_ID));
                    //updateContentValuesAtContentUri(uri, TableThresholdSet.COLUMN_NAME, local_database_row, name);
                    
                    String strFilter = TableThresholdSetLevel.COLUMN_ID + "=" + local_database_row;
                    updateContentsLowLevel(uri, content_values, strFilter);
                    
                    cursor.moveToNext();
                }
            }
            else
            {
                // Database does NOT already have this info - Insert the new row
                local_database_row = insertContentValuesAtContentUri(uri, content_values);
            }
            
            cursor.close();
        }
        
        return local_database_row;
    }

    public int storeOrUpdateDefaultThresholdSetColour(int score,
                                                     int colour,
                                                     int text_colour,
                                                     int servers_id,
                                                     int local_threshold_set_age_block_details_id)
    {
        int local_database_row = -1;

        ContentValues content_values = new ContentValues();
        content_values.put(TableThresholdSetColour.COLUMN_SERVERS_ID, servers_id);
        content_values.put(TableThresholdSetColour.COLUMN_THRESHOLD_SET_AGE_BLOCK_DETAIL_ID, local_threshold_set_age_block_details_id);
        content_values.put(TableThresholdSetColour.COLUMN_SCORE, score);
        content_values.put(TableThresholdSetColour.COLUMN_COLOUR, colour);
        content_values.put(TableThresholdSetColour.COLUMN_TEXT_COLOUR, text_colour);
        content_values.put(TableThresholdSetColour.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, true);

        // Find all rows of TableThresholdSet where COLUMN_SERVERS_ID = 'servers_id'
        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORE_THRESHOLD_SET_COLOURS;
        String[] projection = {
                TableThresholdSetColour.COLUMN_ID,
                TableThresholdSetColour.COLUMN_SERVERS_ID,
        };
        String selection = TableThresholdSetColour.COLUMN_SERVERS_ID + "=" + servers_id;

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                // Database already contains this row from the Server. Update the existing row
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    local_database_row = cursor.getInt(cursor.getColumnIndexOrThrow(TableThresholdSetColour.COLUMN_ID));

                    String strFilter = TableThresholdSetColour.COLUMN_ID + "=" + local_database_row;
                    updateContentsLowLevel(uri, content_values, strFilter);

                    cursor.moveToNext();
                }
            }
            else
            {
                // Database does NOT already have this info - Insert the new row
                local_database_row = insertContentValuesAtContentUri(uri, content_values);
            }

            cursor.close();
        }

        return local_database_row;
    }

    
    public void storeLifetouchPatientOrientation(DeviceInfo device_info, MeasurementPatientOrientation measurement)
    {
        ContentValues content_values = createSensorMeasurementContentValues(device_info, measurement);
        content_values.put(TableLifetouchPatientOrientation.COLUMN_PATIENT_ORIENTATION, measurement.orientation.ordinal());

        asyncInsertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_PATIENT_ORIENTATION, content_values);
    }


    public int checkIfMoreLifetouchPatientOrientationDataPendingToBeSentToServerAndSendIt(ServerLink server_interface, int desired_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        Cursor cursor = getNumberOfSortedSyncableMeasurementsDataPendingRow(QueryType.LIFETOUCH_PATIENT_ORIENTATIONS, desired_device_session_number, active_or_old_session);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if (database_rows > 0)
            {
                cursor.moveToLast();                                                        // Move to the oldest non synced sample

                JSONArray json_array = new JSONArray();

                RowTracker row_tracker = new RowTracker();

                for (int i=0; i<database_rows; i++)
                {
                    // Parameters
                    String database_row_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableLifetouchPatientOrientation.COLUMN_ID));
                    String device_session_number = cursor.getString(cursor.getColumnIndexOrThrow(TableLifetouchPatientOrientation.COLUMN_DEVICE_SESSION_NUMBER));
                    String patient_orientation = cursor.getString(cursor.getColumnIndexOrThrow(TableLifetouchPatientOrientation.COLUMN_PATIENT_ORIENTATION));
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TableLifetouchPatientOrientation.COLUMN_TIMESTAMP));

                    long written_to_android_database_timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TableLifetouchPatientOrientation.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP));

                    String servers_device_session_id = getServerDeviceSessionIdFromAndroidDeviceSessionId(device_session_number);

                    if (servers_device_session_id.isEmpty())
                    {
                        Log.e(TAG, "**************************************************************");
                        Log.e(TAG, "servers_device_session_id is empty. Returning to prevent crash");
                        Log.e(TAG, "**************************************************************");
                        cursor.close();
                        return 0;
                    }

                    row_tracker.addRow(database_row_as_string);

                    try
                    {
                        JSONObject this_measurement = new JSONObject();

                        this_measurement.put("ByDevSessionId", servers_device_session_id);
                        this_measurement.put("PatientOrientation", patient_orientation);
                        this_measurement.put("Timestamp", Utils.convertTimestampToServerSqlDate(timestamp));
                        this_measurement.put("WrittenToAndroidDatabaseTimestamp", Utils.convertTimestampToServerSqlDate(written_to_android_database_timestamp));

                        json_array.put(this_measurement);
                    }
                    catch (JSONException e1)
                    {
                        e1.printStackTrace();
                    }

                    cursor.moveToPrevious();
                }

                ArrayList<RowRange> row_ranges = row_tracker.getRowRanges();
                logQueryInfo("checkIfMoreLifetouchPatientOrientationDataPendingToBeSentToServerAndSendIt", row_ranges, json_array.length());

                server_interface.sendMeasurementsToServer(HttpOperationType.LIFETOUCH_PATIENT_ORIENTATION, json_array, active_or_old_session, row_ranges);
            }

            cursor.close();
        }

        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime);  
        //long ms = duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
        //Log.e(TAG, "checkIfMorePatientOrientationDataPendingToBeSentToServerAndSendIt took " + ms + ". count = " + database_rows + " : " + active_or_old_session);

        return database_rows;
    }


    public void patientOrientationsSentToServerStatus(boolean success, ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            updateSentToServerStatus(success, "CONTENT_URI_LIFETOUCH_PATIENT_ORIENTATION", IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_PATIENT_ORIENTATION, row_range);
        }
    }


    // See http://stackoverflow.com/questions/4187960/asynctask-and-looper-prepare-error
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void resetFailedToSendStatus()
    {
        mHandler.post(resetSendFailedStatusRunnableUsingSQLQuery);
    }


    private final Runnable resetSendFailedStatusRunnableUsingSQLQuery = new Runnable()
    {
        public void run()
        {
            long startTimeAllSessions = System.nanoTime();

            Log.e(TAG, "resetSendFailedStatusRunnableUsingSQLQuery START");

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TablePatientDetails.TABLE_NAME, TablePatientDetails.COLUMN_SENT_TO_SERVER_BUT_FAILED, TablePatientDetails.COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP);
            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableDeviceInfo.TABLE_NAME, TableDeviceInfo.COLUMN_SENT_TO_SERVER_BUT_FAILED, TableDeviceInfo.COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP);

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TablePatientSession.TABLE_NAME, TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED, TablePatientSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP);
            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TablePatientSession.TABLE_NAME, TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED, TablePatientSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP);
            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableDeviceSession.TABLE_NAME, TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED, TableDeviceSession.COLUMN_START_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP);
            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableDeviceSession.TABLE_NAME, TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED, TableDeviceSession.COLUMN_END_SESSION_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP);

            long info_and_sessions_endTime = System.nanoTime();
            long info_and_sessions_duration = (info_and_sessions_endTime - startTimeAllSessions);
            long ms = info_and_sessions_duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
            Log.e(TAG, "resetSendFailedStatusRunnableUsingSQLQuery session and device details. Took " + ms + " ms");

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableConnectionEvent.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.CONNECTION_EVENT, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.CONNECTION_EVENT, ActiveOrOldSession.OLD_SESSION));


            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableLifetouchHeartRate.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_HEART_RATE, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_HEART_RATE, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableLifetouchHeartBeat.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_HEART_BEAT, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_HEART_BEAT, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableLifetouchRespirationRate.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_RESPIRATION_RATE, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_RESPIRATION_RATE, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableLifetouchSetupModeRawSample.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_SETUP_MODE_SAMPLE, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_SETUP_MODE_SAMPLE, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableLifetouchBattery.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_BATTERY, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_BATTERY, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableLifetouchPatientOrientation.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_PATIENT_ORIENTATION, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.LIFETOUCH_PATIENT_ORIENTATION, ActiveOrOldSession.OLD_SESSION));


            long lifetouch_endTime = System.nanoTime();
            long lifetouch_duration = (lifetouch_endTime - info_and_sessions_endTime);
            ms = lifetouch_duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
            Log.e(TAG, "resetSendFailedStatusRunnableUsingSQLQuery lifetouch failed data. Took " + ms + " ms");

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableLifetempMeasurement.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.LIFETEMP_TEMPERATURE, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.LIFETEMP_TEMPERATURE, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableLifetempBattery.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.LIFETEMP_BATTERY, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.LIFETEMP_BATTERY, ActiveOrOldSession.OLD_SESSION));


            long lifetemp_endTime = System.nanoTime();
            long lifetemp_duration = (lifetemp_endTime - lifetouch_endTime);
            ms = lifetemp_duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
            Log.e(TAG, "resetSendFailedStatusRunnableUsingSQLQuery lifetemp failed data. Took " + ms + " ms");


            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableOximeterMeasurement.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.PULSE_OX_MEASUREMENT, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.PULSE_OX_MEASUREMENT, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableOximeterIntermediateMeasurement.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.PULSE_OX_INTERMEDIATE_MEASUREMENT, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.PULSE_OX_INTERMEDIATE_MEASUREMENT, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableOximeterSetupModeRawSample.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.PULSE_OX_SETUP_MODE_SAMPLE, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.PULSE_OX_SETUP_MODE_SAMPLE, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableOximeterBattery.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.PULSE_OX_BATTERY, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.PULSE_OX_BATTERY, ActiveOrOldSession.OLD_SESSION));


            long pulse_ox_endTime = System.nanoTime();
            long pulse_ox_duration = (pulse_ox_endTime - lifetemp_endTime);
            ms = pulse_ox_duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
            Log.e(TAG, "resetSendFailedStatusRunnableUsingSQLQuery pulse ox failed data. Took " + ms + " ms");


            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableBloodPressureMeasurement.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.BLOOD_PRESSURE_MEASUREMENT, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.BLOOD_PRESSURE_MEASUREMENT, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableBloodPressureBattery.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.BLOOD_PRESSURE_BATTERY, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.BLOOD_PRESSURE_BATTERY, ActiveOrOldSession.OLD_SESSION));


            long bp_endTime = System.nanoTime();
            long bp_duration = (bp_endTime - pulse_ox_endTime);
            ms = bp_duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
            Log.e(TAG, "resetSendFailedStatusRunnableUsingSQLQuery BP failed data. Took " + ms + " ms");


            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableWeightScaleWeight.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.WEIGHT_SCALE_MEASUREMENT, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.WEIGHT_SCALE_MEASUREMENT, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableWeightScaleBattery.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.WEIGHT_SCALE_BATTERY, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.WEIGHT_SCALE_BATTERY, ActiveOrOldSession.OLD_SESSION));


            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredHeartRate.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_HEART_RATES, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_HEART_RATES, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredRespirationRate.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_RESPIRATION_RATES, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_RESPIRATION_RATES, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredTemperature.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_TEMPERATURES, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_TEMPERATURES, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredSpO2.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_SPO2_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_SPO2_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredBloodPressure.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredWeight.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_WEIGHT_MEASUREMENTS, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredConsciousnessLevel.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredSupplementalOxygenLevel.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredAnnotation.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_ANNOTATIONS, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_ANNOTATIONS, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredCapillaryRefillTime.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredRespirationDistress.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_RESPIRATION_DISTRESS, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_RESPIRATION_DISTRESS, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredFamilyOrNurseConcern.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, ActiveOrOldSession.OLD_SESSION));

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableManuallyEnteredUrineOutput.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_URINE_OUTPUT, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.MANUALLY_ENTERED_URINE_OUTPUT, ActiveOrOldSession.OLD_SESSION));


            long manual_entry_endTime = System.nanoTime();
            long manual_entry_duration = (manual_entry_endTime - bp_endTime);
            ms = manual_entry_duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
            Log.e(TAG, "resetSendFailedStatusRunnableUsingSQLQuery manual vitals failed data. Took " + ms + " ms");


            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableSetupModeLog.TABLE_NAME, TableSetupModeLog.COLUMN_SENT_TO_SERVER_BUT_FAILED, TableSetupModeLog.COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP);

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableAuditTrail.TABLE_NAME, TableAuditTrail.COLUMN_SENT_TO_SERVER_BUT_FAILED, TableAuditTrail.COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP);

            IsansysPatientGatewayContentProvider.resetSendFailedStatus(TableEarlyWarningScore.TABLE_NAME,
                    server_sync_status.getRowsPending(HttpOperationType.EARLY_WARNING_SCORES, ActiveOrOldSession.ACTIVE_SESSION),
                    server_sync_status.getRowsPending(HttpOperationType.EARLY_WARNING_SCORES, ActiveOrOldSession.OLD_SESSION));

            long endTime = System.nanoTime();
            long duration = (endTime - startTimeAllSessions);
            ms = duration / 1000000;                                                   // Divide by 1000000 to get milliseconds
            Log.e(TAG, "resetSendFailedStatusRunnableUsingSQLQuery END. Took " + ms + " ms");
        }
    };


    // Constant for better code representation
    private final boolean SYNCABLE_DATA = true;
    private final boolean NON_SYNCABLE_DATA = false;

    public RowsPending checkIfMoreSensorMeasurementPendingToBeSentToServer(int desired_patient_or_device_session_number, QueryType queryType, ActiveOrOldSession active_or_old_session, boolean limit_query)
    {
        RowsPending rows_pending = new RowsPending();

        rows_pending.rows_pending_syncable = getPendingMeasurementRows(queryType, desired_patient_or_device_session_number, active_or_old_session, limit_query);

        rows_pending.rows_pending_but_failed = getFailedMeasurementRows(queryType, desired_patient_or_device_session_number, active_or_old_session, limit_query);

        rows_pending.rows_pending_non_syncable = getNonSyncableMeasurementRows(queryType, desired_patient_or_device_session_number, active_or_old_session, limit_query);

        Log.d(TAG, "checkIfMoreSensorMeasurementPendingToBeSentToServer : " + Utils.padLeft(active_or_old_session.toString(), 14) + " : " + Utils.padQueryName(queryType) + " : non_syncable = " + Utils.padNumber(rows_pending.rows_pending_non_syncable) + " : failed = "  + Utils.padNumber(rows_pending.rows_pending_but_failed) + " : syncable = " + Utils.padNumber(rows_pending.rows_pending_syncable));

        return rows_pending;
    }


    private String getSelectionParameterForRowPendingFailed(int desired_patient_or_device_session_number, QueryType queryType, ActiveOrOldSession active_or_old_session)
    {
        switch (queryType)
        {
            case LIFETOUCH_HEART_RATES:
            case LIFETOUCH_HEART_BEATS:
            case LIFETOUCH_RESPIRATION_RATES:
            case LIFETOUCH_SETUP_MODE_RAW_SAMPLES:
            case LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES:
            case LIFETOUCH_BATTERY_MEASUREMENTS:
            case LIFETOUCH_PATIENT_ORIENTATIONS:
            case LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case LIFETEMP_BATTERY_MEASUREMENTS:
            case PULSE_OX_MEASUREMENTS:
            case PULSE_OX_INTERMEDIATE_MEASUREMENTS:
            case PULSE_OX_SETUP_MODE_SAMPLES:
            case PULSE_OX_BATTERY_MEASUREMENTS:
            case BLOOD_PRESSURE_MEASUREMENTS:
            case BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case WEIGHT_SCALE_MEASUREMENTS:
            case WEIGHT_SCALE_BATTERY_MEASUREMENTS:
            case EARLY_WARNING_SCORES:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    return Table.COLUMN_DEVICE_SESSION_NUMBER + "=" + desired_patient_or_device_session_number + " AND " + Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 AND " + Table.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1";
                }
                else
                {
                    return Table.COLUMN_DEVICE_SESSION_NUMBER + "!=" + desired_patient_or_device_session_number + " AND " + Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 AND " + Table.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1";
                }
            }

            case CONNECTION_EVENTS:
            case MANUALLY_ENTERED_HEART_RATES:
            case MANUALLY_ENTERED_RESPIRATION_RATES:
            case MANUALLY_ENTERED_TEMPERATURES:
            case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
            case MANUALLY_ENTERED_ANNOTATIONS:
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    return Table.COLUMN_PATIENT_SESSION_NUMBER + "=" + desired_patient_or_device_session_number + " AND " + Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 AND " + Table.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1";
                }
                else
                {
                    return Table.COLUMN_PATIENT_SESSION_NUMBER + "!=" + desired_patient_or_device_session_number + " AND " + Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 AND " + Table.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1";
                }
            }

            default:
                Log.d(TAG, "getSelectionParameterForRowPendingFailed : query type is not expect value. ALERT ALERT ALERT !!!!!!");
                return null;
        }
    }


    private static class TableQueryParameters
    {
        Uri uri;
        String selection;
        String[] projection;
    }


    private TableQueryParameters getQueryParameters(QueryType type, int desired_patient_or_device_session_number, ActiveOrOldSession active_or_old_session, boolean is_syncable)
    {
        TableQueryParameters tableQueryParameters = new TableQueryParameters();

        List<String> projection = new ArrayList<>();

        switch (type)
        {
            // Device Session ID linked data
            case LIFETOUCH_HEART_RATES:
            case LIFETOUCH_HEART_BEATS:
            case LIFETOUCH_RESPIRATION_RATES:
            case LIFETOUCH_SETUP_MODE_RAW_SAMPLES:
            case LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES:
            case LIFETOUCH_BATTERY_MEASUREMENTS:
            case LIFETOUCH_PATIENT_ORIENTATIONS:
            case LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case LIFETEMP_BATTERY_MEASUREMENTS:
            case PULSE_OX_MEASUREMENTS:
            case PULSE_OX_INTERMEDIATE_MEASUREMENTS:
            case PULSE_OX_SETUP_MODE_SAMPLES:
            case PULSE_OX_BATTERY_MEASUREMENTS:
            case BLOOD_PRESSURE_MEASUREMENTS:
            case BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case WEIGHT_SCALE_MEASUREMENTS:
            case WEIGHT_SCALE_BATTERY_MEASUREMENTS:
            {
                tableQueryParameters.selection = getActiveOrHistoricalSessionSelectionString(Table.COLUMN_DEVICE_SESSION_NUMBER, desired_patient_or_device_session_number, active_or_old_session);

                projection.add(Table.COLUMN_HUMAN_READABLE_DEVICE_ID);
                projection.add(Table.COLUMN_DEVICE_SESSION_NUMBER);
                projection.add(Table.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP);
                projection.add(Table.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS);
            }
            break;

            case CONNECTION_EVENTS:
            {
                tableQueryParameters.selection = getActiveOrHistoricalSessionSelectionString(Table.COLUMN_PATIENT_SESSION_NUMBER, desired_patient_or_device_session_number, active_or_old_session);
            }
            break;

            case EARLY_WARNING_SCORES:
            {
                tableQueryParameters.selection = getActiveOrHistoricalSessionSelectionString(Table.COLUMN_DEVICE_SESSION_NUMBER, desired_patient_or_device_session_number, active_or_old_session);
            }
            break;

            // Patient Session ID linked data
            case MANUALLY_ENTERED_HEART_RATES:
            case MANUALLY_ENTERED_RESPIRATION_RATES:
            case MANUALLY_ENTERED_TEMPERATURES:
            case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
            case MANUALLY_ENTERED_ANNOTATIONS:
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                tableQueryParameters.selection = getActiveOrHistoricalSessionSelectionString(Table.COLUMN_PATIENT_SESSION_NUMBER, desired_patient_or_device_session_number, active_or_old_session);

                projection.add(Table.COLUMN_PATIENT_SESSION_NUMBER);
                projection.add(Table.COLUMN_BY_USER_ID);
                projection.add(Table.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP);
                projection.add(Table.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS);
            }
            break;
        }

        switch (type)
        {
            case LIFETOUCH_HEART_RATES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_LIFETOUCH_HEART_RATES, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_LIFETOUCH_HEART_RATES, is_syncable);
                projection.add(TableLifetouchHeartRate.COLUMN_HEART_RATE);
            }
            break;

            case LIFETOUCH_HEART_BEATS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_LIFETOUCH_HEART_BEATS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_LIFETOUCH_HEART_BEATS, is_syncable);
                projection.add(TableLifetouchHeartBeat.COLUMN_AMPLITUDE);
                projection.add(TableLifetouchHeartBeat.COLUMN_ACTIVITY_LEVEL);
                projection.add(TableLifetouchHeartBeat.COLUMN_RR_INTERVAL);
            }
            break;

            case LIFETOUCH_RESPIRATION_RATES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_LIFETOUCH_RESPIRATION_RATES, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_LIFETOUCH_RESPIRATION_RATES, is_syncable);
                projection.add(TableLifetouchRespirationRate.COLUMN_RESPIRATION_RATE);
            }
            break;

            case LIFETOUCH_SETUP_MODE_RAW_SAMPLES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_LIFETOUCH_SETUP_MODE_SAMPLES, is_syncable);
                projection.add(TableLifetouchSetupModeRawSample.COLUMN_SAMPLE_VALUE);
            }
            break;

            case LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES, is_syncable);
                projection.add(TableLifetouchRawAccelerometerModeSample.COLUMN_X_SAMPLE_VALUE);
                projection.add(TableLifetouchRawAccelerometerModeSample.COLUMN_Y_SAMPLE_VALUE);
                projection.add(TableLifetouchRawAccelerometerModeSample.COLUMN_Z_SAMPLE_VALUE);
            }
            break;

            case LIFETOUCH_BATTERY_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_LIFETOUCH_BATTERY_MEASUREMENTS, is_syncable);
                projection.add(TableLifetouchBattery.COLUMN_BATTERY_PERCENTAGE);
                projection.add(TableLifetouchBattery.COLUMN_BATTERY_MILLIVOLTS);
            }
            break;

            case LIFETOUCH_PATIENT_ORIENTATIONS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_PATIENT_ORIENTATION, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_PATIENT_ORIENTATION, is_syncable);
                projection.add(TableLifetouchPatientOrientation.COLUMN_PATIENT_ORIENTATION);
            }
            break;

            case LIFETEMP_TEMPERATURE_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_LIFETEMP_TEMPERATURE_MEASUREMENTS, is_syncable);
                projection.add(TableLifetempMeasurement.COLUMN_TEMPERATURE);
            }
            break;

            case LIFETEMP_BATTERY_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_LIFETEMP_BATTERY_MEASUREMENTS, is_syncable);
                projection.add(TableLifetempBattery.COLUMN_BATTERY_PERCENTAGE);
                projection.add(TableLifetempBattery.COLUMN_BATTERY_MILLIVOLTS);
            }
            break;

            case PULSE_OX_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_OXIMETER_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_OXIMETER_MEASUREMENTS, is_syncable);
                projection.add(TableOximeterMeasurement.COLUMN_SPO2);
                projection.add(TableOximeterMeasurement.COLUMN_PULSE);
            }
            break;

            case PULSE_OX_INTERMEDIATE_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_OXIMETER_INTERMEDIATE_MEASUREMENTS, is_syncable);
                projection.add(TableOximeterIntermediateMeasurement.COLUMN_SPO2);
                projection.add(TableOximeterIntermediateMeasurement.COLUMN_PULSE);
            }
            break;

            case PULSE_OX_SETUP_MODE_SAMPLES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_OXIMETER_SETUP_MODE_SAMPLES, is_syncable);
                projection.add(TableOximeterSetupModeRawSample.COLUMN_SAMPLE_VALUE);
            }
            break;

            case PULSE_OX_BATTERY_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_OXIMETER_BATTERY_MEASUREMENTS, is_syncable);
                projection.add(TableOximeterBattery.COLUMN_BATTERY_PERCENTAGE);
                projection.add(TableOximeterBattery.COLUMN_BATTERY_MILLIVOLTS);
            }
            break;

            case BLOOD_PRESSURE_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_BLOOD_PRESSURE_MEASUREMENTS, is_syncable);
                projection.add(TableBloodPressureMeasurement.COLUMN_PULSE);
                projection.add(TableBloodPressureMeasurement.COLUMN_SYSTOLIC);
                projection.add(TableBloodPressureMeasurement.COLUMN_DIASTOLIC);
                projection.add(Table.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS);
            }
            break;

            case BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_BLOOD_PRESSURE_BATTERY_MEASUREMENTS, is_syncable);
                projection.add(TableBloodPressureBattery.COLUMN_BATTERY);
            }
            break;

            case WEIGHT_SCALE_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_WEIGHT_SCALE_MEASUREMENTS, is_syncable);
                projection.add(TableWeightScaleWeight.COLUMN_WEIGHT);
                projection.add(Table.COLUMN_MEASUREMENT_VALIDITY_TIME_IN_SECONDS);
            }
            break;

            case WEIGHT_SCALE_BATTERY_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_WEIGHT_SCALE_BATTERY_MEASUREMENTS, is_syncable);
                projection.add(TableWeightScaleBattery.COLUMN_BATTERY_PERCENTAGE);
            }
            break;

            case MANUALLY_ENTERED_HEART_RATES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_HEART_RATES, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_HEART_RATES, is_syncable);
                projection.add(TableManuallyEnteredHeartRate.COLUMN_HEART_RATE);
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_RATES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_RATES, is_syncable);
                projection.add(TableManuallyEnteredRespirationRate.COLUMN_RESPIRATION_RATE);
            }
            break;

            case MANUALLY_ENTERED_TEMPERATURES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_TEMPERATURES, is_syncable);
                projection.add(TableManuallyEnteredTemperature.COLUMN_TEMPERATURE);
            }
            break;

            case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_SPO2, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_SPO2, is_syncable);
                projection.add(TableManuallyEnteredSpO2.COLUMN_SPO2);
            }
            break;

            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURE, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_BLOOD_PRESSURE, is_syncable);
                projection.add(TableManuallyEnteredBloodPressure.COLUMN_SYSTOLIC);
                projection.add(TableManuallyEnteredBloodPressure.COLUMN_DIASTOLIC);
            }
            break;

            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_WEIGHT, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_WEIGHT, is_syncable);
                projection.add(TableManuallyEnteredWeight.COLUMN_WEIGHT);
            }
            break;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL, is_syncable);
                projection.add(TableManuallyEnteredConsciousnessLevel.COLUMN_CONSCIOUSNESS_LEVEL);
            }
            break;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL, is_syncable);
                projection.add(TableManuallyEnteredSupplementalOxygenLevel.COLUMN_SUPPLEMENTAL_OXYGEN_LEVEL);
            }
            break;

            case MANUALLY_ENTERED_ANNOTATIONS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_ANNOTATIONS, is_syncable);
                projection.add(TableManuallyEnteredAnnotation.COLUMN_ANNOTATION_TEXT);
            }
            break;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_CAPILLARY_REFILL_TIME, is_syncable);
                projection.add(TableManuallyEnteredCapillaryRefillTime.COLUMN_CAPILLARY_REFILL_TIME);
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_RESPIRATION_DISTRESS, is_syncable);
                projection.add(TableManuallyEnteredRespirationDistress.COLUMN_RESPIRATION_DISTRESS);
            }
            break;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN, is_syncable);
                projection.add(TableManuallyEnteredFamilyOrNurseConcern.COLUMN_CONCERN);
            }
            break;

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_MANUALLY_ENTERED_URINE_OUTPUT, is_syncable);
                projection.add(TableManuallyEnteredUrineOutput.COLUMN_URINE_OUTPUT);
            }
            break;

            case CONNECTION_EVENTS:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_CONNECTION_EVENTS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_CONNECTION_EVENTS, is_syncable);
                projection.add(TableConnectionEvent.COLUMN_CONNECTED);
                projection.add(TableConnectionEvent.COLUMN_DEVICE_SESSION_NUMBER);
                projection.add(TableConnectionEvent.COLUMN_PATIENT_SESSION_NUMBER);
            }
            break;

            case EARLY_WARNING_SCORES:
            {
                tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_EARLY_WARNING_SCORES, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_EARLY_WARNING_SCORES, is_syncable);
                projection.add(TableEarlyWarningScore.COLUMN_EARLY_WARNING_SCORE);
                projection.add(TableEarlyWarningScore.COLUMN_MAX_POSSIBLE);
                projection.add(TableEarlyWarningScore.COLUMN_IS_SPECIAL_ALERT);
                projection.add(TableEarlyWarningScore.COLUMN_DEVICE_SESSION_NUMBER);
                projection.add(TableEarlyWarningScore.COLUMN_PATIENT_SESSION_NUMBER);
                projection.add(TableEarlyWarningScore.COLUMN_TREND_DIRECTION);
                projection.add(TableEarlyWarningScore.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP);
            }
            break;

            default:
                Log.e(TAG, "getQueryParameters : query type is not expect value. ALERT ALERT ALERT !!!!!!");
        }

        projection.add(Table.COLUMN_ID);
        projection.add(Table.COLUMN_TIMESTAMP);
        projection.add(Table.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED);

        tableQueryParameters.projection = projection.toArray(new String[0]);

        return tableQueryParameters;
    }


    private Uri getSyncableOrNonSyncableURI(Uri syncable, Uri non_syncable, boolean is_syncable)
    {
        if (is_syncable)
        {
            return syncable;
        }
        else
        {
            return non_syncable;
        }
    }


    private String getActiveOrHistoricalSessionSelectionString(String sensorMeasurementColumnDeviceNumber, int desired_patient_or_device_session_number, ActiveOrOldSession active_or_old_session)
    {
        if(active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
        {
            return " AND " + sensorMeasurementColumnDeviceNumber + "=" + desired_patient_or_device_session_number;
        }
        else
        {
            return " AND " + sensorMeasurementColumnDeviceNumber + "!=" + desired_patient_or_device_session_number + " AND " + sensorMeasurementColumnDeviceNumber + ">0";
        }
    }


    private int getNumberOfMeasurementDataPendingRows(TableQueryParameters tableQueryParameters, boolean limit_query)
    {
        int numberOfDataPending;

        String sort_and_limit = "";

        if(limit_query)
        {
            sort_and_limit = " DESC LIMIT " + number_of_rows_per_json_message;
        }

        // All selection done inside Content Provider query as it has to do a join across several tables to ensure this data is syncable
        Cursor cursor = content_resolver.query(tableQueryParameters.uri, tableQueryParameters.projection, tableQueryParameters.selection, null, sort_and_limit, null);

        if(cursor != null)
        {
            numberOfDataPending = cursor.getCount();

            cursor.close();

            return numberOfDataPending;
        }
        else
        {
            return 0;
        }
    }


    private int getPendingMeasurementRows(QueryType type, int desired_patient_or_device_session_number, ActiveOrOldSession active_or_old_session, boolean limit_query)
    {
        return getNumberOfMeasurementRows(type, desired_patient_or_device_session_number, active_or_old_session, true, false, limit_query);
    }


    private int getFailedMeasurementRows(QueryType type, int desired_patient_or_device_session_number, ActiveOrOldSession active_or_old_session, boolean limit_query)
    {
        return getNumberOfMeasurementRows(type, desired_patient_or_device_session_number, active_or_old_session, true, true, limit_query);
    }


    private int getNonSyncableMeasurementRows(QueryType type, int desired_patient_or_device_session_number, ActiveOrOldSession active_or_old_session, boolean limit_query)
    {
        return getNumberOfMeasurementRows(type, desired_patient_or_device_session_number, active_or_old_session, false, false, limit_query);
    }


    private int getNumberOfMeasurementRows(QueryType type, int desired_patient_or_device_session_number, ActiveOrOldSession active_or_old_session, boolean session_info_synced, boolean measurement_syncing_failed, boolean limit_query)
    {
        switch(type)
        {
            case LIFETOUCH_HEART_RATES:
            case LIFETOUCH_HEART_BEATS:
            case LIFETOUCH_RESPIRATION_RATES:
            case LIFETOUCH_SETUP_MODE_RAW_SAMPLES:
            case LIFETOUCH_RAW_ACCELEROMETER_MODE_RAW_SAMPLES:
            case LIFETOUCH_BATTERY_MEASUREMENTS:
            case LIFETOUCH_PATIENT_ORIENTATIONS:
            case LIFETEMP_TEMPERATURE_MEASUREMENTS:
            case LIFETEMP_BATTERY_MEASUREMENTS:
            case PULSE_OX_MEASUREMENTS:
            case PULSE_OX_INTERMEDIATE_MEASUREMENTS:
            case PULSE_OX_SETUP_MODE_SAMPLES:
            case PULSE_OX_BATTERY_MEASUREMENTS:
            case BLOOD_PRESSURE_MEASUREMENTS:
            case BLOOD_PRESSURE_BATTERY_MEASUREMENTS:
            case WEIGHT_SCALE_MEASUREMENTS:
            case WEIGHT_SCALE_BATTERY_MEASUREMENTS:
            case EARLY_WARNING_SCORES:
            {
                // actual device measurements, so get the device session(s)...
                ArrayList<Integer> session_ids = getSyncedOrUnsyncedDeviceSessionIds(desired_patient_or_device_session_number, active_or_old_session, session_info_synced);

                if(session_ids.size() < 1)
                {
                    return 0;
                }

                int number_of_rows = 0;

                for(Integer id : session_ids)
                {
                    if(measurement_syncing_failed)
                    {
                        String selection = getSelectionParameterForRowPendingFailed(id, type, ActiveOrOldSession.ACTIVE_SESSION);
                        number_of_rows += getNumberOfRowsPending(type, selection);
                    }
                    else
                    {
                        // Get only the data for this exact non-syncable session...
                        TableQueryParameters tableQueryParameters = getQueryParameters(type, id, ActiveOrOldSession.ACTIVE_SESSION, session_info_synced);

                        number_of_rows += getNumberOfMeasurementDataPendingRows(tableQueryParameters, limit_query);
                    }

                    if(limit_query && (number_of_rows >= number_of_rows_per_json_message))
                    {
                        return number_of_rows_per_json_message;
                    }
                }

                return number_of_rows;
            }
            // no break, as code will have returned, making it unreachable

            case CONNECTION_EVENTS:
            case MANUALLY_ENTERED_HEART_RATES:
            case MANUALLY_ENTERED_RESPIRATION_RATES:
            case MANUALLY_ENTERED_TEMPERATURES:
            case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL_MEASUREMENTS:
            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL_MEASUREMENTS:
            case MANUALLY_ENTERED_ANNOTATIONS:
            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIMES:
            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                ArrayList<Integer> session_ids = getSyncedOrUnsyncedPatientSessionIds(desired_patient_or_device_session_number, active_or_old_session, session_info_synced);

                if(session_ids.size() < 1)
                {
                    return 0;
                }

                int number_of_rows = 0;

                for(Integer id : session_ids)
                {
                    if(measurement_syncing_failed)
                    {
                        String selection = getSelectionParameterForRowPendingFailed(id, type, ActiveOrOldSession.ACTIVE_SESSION);
                        number_of_rows += getNumberOfRowsPending(type, selection);
                    }
                    else
                    {
                        // Get only the data for this exact non-syncable session...
                        TableQueryParameters tableQueryParameters = getQueryParameters(type, id, ActiveOrOldSession.ACTIVE_SESSION, session_info_synced);

                        number_of_rows += getNumberOfMeasurementDataPendingRows(tableQueryParameters, limit_query);
                    }
                    if(limit_query && (number_of_rows >= number_of_rows_per_json_message))
                    {
                        return number_of_rows_per_json_message;
                    }
                }

                return number_of_rows;
            }
            // no break, as code will have returned, making it unreachable

            default:
            {
                // not a measurement, so do nothing.
                Log.d(TAG, "querying un-implemented non syncable data " + type + " with id " + desired_patient_or_device_session_number);
                return 0;
            }
            // no break, as code will have returned, making it unreachable
        }
    }


    private Cursor getNumberOfSortedSyncableMeasurementsDataPendingRow(QueryType query, int desired_session_number, ActiveOrOldSession active_or_old_session)
    {
        TableQueryParameters tableQueryParameters = getQueryParameters(query, desired_session_number, active_or_old_session, SYNCABLE_DATA);

        String sortOrder = " DESC LIMIT " + number_of_rows_per_json_message;

        // All selection done inside Content Provider query as it has to do a join across several tables to ensure this data is syncable
        return content_resolver.query(tableQueryParameters.uri, tableQueryParameters.projection, tableQueryParameters.selection, null, sortOrder, null);
    }


    public int storeStartSetupMode(SensorType sensorType, DeviceType deviceType, int device_session_number, long start_time)
    {
        ContentValues content_values = new ContentValues();

        content_values.put(TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER, device_session_number);
        content_values.put(TableSetupModeLog.COLUMN_SENSOR_TYPE, sensorType.ordinal());
        content_values.put(TableSetupModeLog.COLUMN_DEVICE_TYPE, deviceType.ordinal());
        content_values.put(TableSetupModeLog.COLUMN_START_SETUP_MODE_TIME, start_time);
        content_values.put(TableSetupModeLog.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, false);

        return insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_SETUP_MODE_LOGS, content_values);
    }


    public void updateSetupModeLogTimestampSentToServerStatus(boolean success, int android_local_database_row_to_update)
    {
        if (success)
        {
            markRowAsSuccessfullySentToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_SETUP_MODE_LOGS,
                    TableSetupModeLog.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED,
                    TableSetupModeLog.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED_TIMESTAMP,
                    android_local_database_row_to_update);
        }
        else
        {
            markRowAsFailedSendingToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_SETUP_MODE_LOGS,
                    TableSetupModeLog.COLUMN_SENT_TO_SERVER_BUT_FAILED,
                    TableSetupModeLog.COLUMN_SENT_TO_SERVER_BUT_FAILED_TIMESTAMP,
                    android_local_database_row_to_update);
        }
    }


    public void updateSetupModeLogTimestampSentToServerStatusFailedByRange(ArrayList<RowRange> row_ranges)
    {
        for (RowRange row_range : row_ranges)
        {
            markMultipleRowsAsFailedSendingToServer(IsansysPatientGatewayContentProvider.CONTENT_URI_SETUP_MODE_LOGS,
                    row_range.start,
                    row_range.end);
        }
    }


    public void storeExitSetupMode(int id, long end_time)
    {
        String filter = TableSetupModeLog.COLUMN_ID + "=" + id;

        ContentValues args = new ContentValues();
        args.put(TableSetupModeLog.COLUMN_END_SETUP_MODE_TIME, end_time);

        content_resolver.update(IsansysPatientGatewayContentProvider.CONTENT_URI_SETUP_MODE_LOGS, args, filter, null);
    }


    private RowsPending checkIfSetupModeLogsPendingToBeSentToServer(ActiveOrOldSession active_or_old_session)
    {
        ArrayList<Integer> active_device_sessions_ids = patient_gateway_interface.getOpenDeviceSessionIdList();

        RowsPending rows_pending = new RowsPending();

        rows_pending.rows_pending_syncable = getNumberOfSyncableSetupModeLogs(active_or_old_session, active_device_sessions_ids, true);

        rows_pending.rows_pending_non_syncable = getNumberOfSyncableSetupModeLogs(active_or_old_session, active_device_sessions_ids, false);

        rows_pending.rows_pending_but_failed = getNumberFailedSetupModeLogs(active_or_old_session, active_device_sessions_ids);

        Log.d(TAG, "checkIfSetupModeLogsPendingToBeSentToServer         : " + Utils.padLeft(active_or_old_session.toString(), 14) + " : " + Utils.padQueryName(QueryType.ALL_SETUP_MODE_LOGS) + " : non_syncable = " + Utils.padNumber(rows_pending.rows_pending_non_syncable) + " : failed = "  + Utils.padNumber(rows_pending.rows_pending_but_failed) + " : syncable = " + Utils.padNumber(rows_pending.rows_pending_syncable));

        return rows_pending;
    }


    public RowsPending checkIfActiveSessionSetupModeLogsPendingToBeSentToServer()
    {
        return checkIfSetupModeLogsPendingToBeSentToServer(ActiveOrOldSession.ACTIVE_SESSION);
    }


    public RowsPending checkIfHistoricalSessionSetupModeLogsPendingToBeSentToServer()
    {
        return checkIfSetupModeLogsPendingToBeSentToServer(ActiveOrOldSession.OLD_SESSION);
    }


    private String buildSetupModeLogSelectionString(ActiveOrOldSession active_or_old_session, ArrayList<Integer> active_device_sessions_ids)
    {
        if(active_device_sessions_ids.size() == 0)
        {
            if(active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
            {
                return " AND " + TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER + "=null";
            }
            else
            {
                return "";
            }
        }

        StringBuilder filter_string = new StringBuilder(" AND (");

        if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
        {
            for(int i = 0; i < active_device_sessions_ids.size(); i++)
            {
                filter_string.append(TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER + "=").append(active_device_sessions_ids.get(i));

                if (i < active_device_sessions_ids.size() - 1)
                {
                    filter_string.append(" OR ");
                }
            }
        }
        else
        {
            for(int i = 0; i < active_device_sessions_ids.size(); i++)
            {
                filter_string.append(TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER + "!=").append(active_device_sessions_ids.get(i));

                if (i < active_device_sessions_ids.size() - 1)
                {
                    filter_string.append(" AND ");
                }
            }
        }
        filter_string.append(") ");

        return filter_string.toString();
    }


    private int getNumberOfSyncableSetupModeLogs(ActiveOrOldSession active_or_old_session, ArrayList<Integer> active_device_sessions_ids, boolean syncable)
    {
        if((active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION) && (active_device_sessions_ids.size() == 0))
        {
            // no active device sessions, so return 0
            return 0;
        }

        String[] projection = {
                TableSetupModeLog.COLUMN_ID,
                TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER,
                TableSetupModeLog.COLUMN_START_SETUP_MODE_TIME,
                TableSetupModeLog.COLUMN_END_SETUP_MODE_TIME,
                TableSetupModeLog.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED
        };

        String selection = buildSetupModeLogSelectionString(active_or_old_session, active_device_sessions_ids);

        if(syncable)
        {
            return getNumberOfRowsWithSelection(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_SETUP_MODE_LOGS, projection, selection);
        }
        else
        {
            return getNumberOfRowsWithSelection(IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_SETUP_MODE_LOGS, projection, selection);
        }
    }


    private int getNumberFailedSetupModeLogs(ActiveOrOldSession active_or_old_session, ArrayList<Integer> active_device_sessions_ids)
    {
        String[] projection = {
                TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER,
                TableSetupModeLog.COLUMN_SENT_TO_SERVER_BUT_FAILED
        };

        String selection = TableSetupModeLog.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1 ";

        selection += buildSetupModeLogSelectionString(active_or_old_session, active_device_sessions_ids);

        return getNumberOfRowsWithSelection(IsansysPatientGatewayContentProvider.CONTENT_URI_SETUP_MODE_LOGS, projection, selection);
    }


    public int checkIfMoreSetupModeLogsPendingToBeSentToServerAndSendIt(ServerLink server_interface, ActiveOrOldSession active_or_old_session)
    {
        ArrayList<Integer> active_device_sessions_ids = patient_gateway_interface.getOpenDeviceSessionIdList();

        List<String> projection = new ArrayList<>();
        projection.add(TableSetupModeLog.COLUMN_ID);
        projection.add(TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER);
        projection.add(TableSetupModeLog.COLUMN_SENSOR_TYPE);
        projection.add(TableSetupModeLog.COLUMN_DEVICE_TYPE);
        projection.add(TableSetupModeLog.COLUMN_START_SETUP_MODE_TIME);
        projection.add(TableSetupModeLog.COLUMN_END_SETUP_MODE_TIME);

        TableQueryParameters tableQueryParameters = new TableQueryParameters();
        tableQueryParameters.uri = getSyncableOrNonSyncableURI(IsansysPatientGatewayContentProvider.CONTENT_URI_SYNCABLE_SETUP_MODE_LOGS, IsansysPatientGatewayContentProvider.CONTENT_URI_NON_SYNCABLE_SETUP_MODE_LOGS, true);
        tableQueryParameters.projection = projection.toArray(new String[0]);
        tableQueryParameters.selection = buildSetupModeLogSelectionString(active_or_old_session, active_device_sessions_ids);

        String sortOrder = " DESC LIMIT " + number_of_rows_per_json_message;

        // All selection done inside Content Provider query as it has to do a join across several tables to ensure this data is syncable
        Cursor cursor = content_resolver.query(tableQueryParameters.uri, tableQueryParameters.projection, tableQueryParameters.selection, null, sortOrder, null);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if (database_rows > 0)
            {
                cursor.moveToLast();                                                        // Move to the oldest non synced sample

                JSONArray json_array = new JSONArray();

                RowTracker row_tracker = new RowTracker();

                for (int i=0; i<database_rows; i++)
                {
                    // Parameters
                    String database_row_as_string = cursor.getString(cursor.getColumnIndexOrThrow(TableSetupModeLog.COLUMN_ID));
                    String device_session_number = cursor.getString(cursor.getColumnIndexOrThrow(TableSetupModeLog.COLUMN_DEVICE_SESSION_NUMBER));
                    int sensor_type_as_int = cursor.getInt(cursor.getColumnIndexOrThrow(TableSetupModeLog.COLUMN_SENSOR_TYPE));
                    int device_type_as_int = cursor.getInt(cursor.getColumnIndexOrThrow(TableSetupModeLog.COLUMN_DEVICE_TYPE));
                    long start_setup_mode_time = cursor.getLong(cursor.getColumnIndexOrThrow(TableSetupModeLog.COLUMN_START_SETUP_MODE_TIME));
                    long end_setup_mode_time = cursor.getLong(cursor.getColumnIndexOrThrow(TableSetupModeLog.COLUMN_END_SETUP_MODE_TIME));

                    String servers_device_session_id = getServerDeviceSessionIdFromAndroidDeviceSessionId(device_session_number);
                    if (servers_device_session_id.isEmpty())
                    {
                        Log.e(TAG, "**************************************************************");
                        Log.e(TAG, "servers_device_session_id is empty. Returning to prevent crash");
                        Log.e(TAG, "**************************************************************");
                        cursor.close();
                        return 0;
                    }

                    row_tracker.addRow(database_row_as_string);

                    try
                    {
                        JSONObject this_measurement = new JSONObject();

                        this_measurement.put("LocalDatabaseRow", database_row_as_string);
                        this_measurement.put("DeviceSessionId", servers_device_session_id);
                        this_measurement.put("StartSetupModeTime", Utils.convertTimestampToServerSqlDate(start_setup_mode_time));
                        this_measurement.put("EndSetupModeTime", Utils.convertTimestampToServerSqlDate(end_setup_mode_time));

                        this_measurement.put("SensorType", sensor_type_as_int);
                        this_measurement.put("DeviceType", device_type_as_int);

                        json_array.put(this_measurement);
                    }
                    catch (JSONException e1)
                    {
                        e1.printStackTrace();
                    }

                    cursor.moveToPrevious();
                }

                ArrayList<RowRange> row_ranges = row_tracker.getRowRanges();
                logQueryInfo("checkIfMoreStopSetupModePendingToBeSentToServerAndSendIt", row_ranges, json_array.length());

                server_interface.sendMeasurementsToServer(HttpOperationType.SETUP_MODE_LOG, json_array, active_or_old_session, row_ranges);
            }

            cursor.close();
        }

        return database_rows;
    }


    public int storeServerConfigurableText(ServerConfigurableText server_configurable_text)
    {
        ContentValues content_values = new ContentValues();
        content_values.put(TableServerConfigurableText.COLUMN_SERVERS_ID, server_configurable_text.getServersId());
        content_values.put(TableServerConfigurableText.COLUMN_STRING, server_configurable_text.getStringText());
        content_values.put(TableServerConfigurableText.COLUMN_STRING_TYPE, server_configurable_text.getStringType().ordinal());
        content_values.put(TableThresholdSetLevel.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED, true);

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_SERVER_CONFIGURABLE_TEXT;

        return insertContentValuesAtContentUri(uri, content_values);
    }


    public int storeWebPage(WebPageDescriptor webPageDescriptor)
    {
        ContentValues content_values = new ContentValues();
        content_values.put(TableViewableWebPageDetails.COLUMN_URL, webPageDescriptor.url);
        content_values.put(TableViewableWebPageDetails.COLUMN_DESCRIPTION, webPageDescriptor.description);

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_VIEWABLE_WEB_PAGES;

        return insertContentValuesAtContentUri(uri, content_values);
    }


    public void storeAuditTrailEventUsingLastKnownUserId(AuditTrailEvent event, long timestamp)
    {
        storeAuditTrailEvent(event, timestamp, last_known_user_id, "");
    }


    public void storeAuditTrailEventUsingLastKnownUserId(AuditTrailEvent event, long timestamp, String additional)
    {
        storeAuditTrailEvent(event, timestamp, last_known_user_id, additional);
    }


    public void storeAuditTrailEvent(AuditTrailEvent event, long timestamp, int by_user_id)
    {
        storeAuditTrailEvent(event, timestamp, by_user_id, String.valueOf(AUDIT_OPTION_NOT_APPLICABLE));
    }


    public void storeAuditTrailEvent(AuditTrailEvent event, long timestamp, int by_user_id, String additional)
    {
        ContentValues content_values = new ContentValues();

        content_values.put(TableAuditTrail.COLUMN_EVENT, event.ordinal());
        content_values.put(TableAuditTrail.COLUMN_TIMESTAMP, timestamp);
        content_values.put(TableAuditTrail.COLUMN_BY_USER_ID, by_user_id);
        content_values.put(TableAuditTrail.COLUMN_ADDITIONAL, additional);
        content_values.put(TableAuditTrail.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP, getTimeNow());
        content_values.put(TableAuditTrail.COLUMN_BLUETOOTH_ADDRESS, device_id_for_audit_trail);
        content_values.put(TableAuditTrail.COLUMN_GATEWAY_NAME, BluetoothAdapter.getDefaultAdapter().getName() );
        content_values.put(TableAuditTrail.COLUMN_BED_ID, patient_gateway_interface.getBedId());

        insertContentValuesAtContentUri(IsansysPatientGatewayContentProvider.CONTENT_URI_AUDIT_TRAIL, content_values);

        last_known_user_id = by_user_id;
    }


    public RowsPending checkIfAuditableEventsPendingToBeSentToServer()
    {
        RowsPending rows_pending = new RowsPending();

        String selection;
        selection = TableAuditTrail.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "= 0 AND " + TableAuditTrail.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";
        rows_pending.rows_pending_syncable = getNumberOfRowsPending(QueryType.ALL_AUDITABLE_EVENTS, selection);

        selection = TableAuditTrail.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=1";
        rows_pending.rows_pending_but_failed = getNumberOfRowsPending(QueryType.ALL_AUDITABLE_EVENTS, selection);

        rows_pending.rows_pending_non_syncable = 0; // not possible to be non-syncable, as audit events doesn't depend on any other info syncing first.

        Log.d(TAG, "checkIfAuditableEventsPendingToBeSentToServer       : " + Utils.padLeft("", 14) + " : " + Utils.padQueryName(QueryType.ALL_AUDITABLE_EVENTS) + " : non_syncable = " + Utils.padNumber(rows_pending.rows_pending_non_syncable) + " : failed = "  + Utils.padNumber(rows_pending.rows_pending_but_failed) + " : syncable = " + Utils.padNumber(rows_pending.rows_pending_syncable));

        return rows_pending;
    }


    public int checkIfAuditableEventsPendingToBeSentToServerAndSendIt(ServerLink server_interface)
    {
        // Get the oldest database row that hasn't been sent to the Server yet.

        Uri uri = IsansysPatientGatewayContentProvider.CONTENT_URI_AUDIT_TRAIL;

        String[] projection = {
            TableAuditTrail.COLUMN_ID,
            TableAuditTrail.COLUMN_BLUETOOTH_ADDRESS,
            TableAuditTrail.COLUMN_BED_ID,
            TableAuditTrail.COLUMN_GATEWAY_NAME,
            TableAuditTrail.COLUMN_TIMESTAMP,
            TableAuditTrail.COLUMN_EVENT,
            TableAuditTrail.COLUMN_BY_USER_ID,
            TableAuditTrail.COLUMN_ADDITIONAL,
            TableAuditTrail.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP,
        };

        String selection = TableAuditTrail.COLUMN_SENT_TO_SERVER_AND_SERVER_ACKNOWLEDGED + "=0 AND " + TablePatientDetails.COLUMN_SENT_TO_SERVER_BUT_FAILED + "=0";

        Cursor cursor = content_resolver.query(uri, projection, selection, null, null, null);

        int database_rows = 0;

        if (cursor != null)
        {
            database_rows = cursor.getCount();

            if(database_rows > 0)
            {
                Log.i(TAG, "checkIfAuditableEventsPendingToBeSentToServerAndSendIt : ServerSyncing.currentSessionInformation.patientDetailSendToServer_Status = " + server_syncing.currentSessionInformation.patientDetailSendToServer_Status);

                switch (server_syncing.currentSessionInformation.auditTrailSendToServer_Status)
                {
                    case IDLE:
                    {
                        // NR this might not right .... just going straight to SEND_PACKET but then we don't need to check patient IDs, device details or other things that checks for other aspects require

                        server_syncing.currentSessionInformation.auditTrailSendToServer_Status = CheckServerLinkStatus.SEND_PACKET;
                    }
                    break;

                    case CHECKING:
                        break;

                    case SEND_PACKET: {
                        // Process the newest one that hasn't been sent to the Server yet. This will probably be the current active session
                        cursor.moveToLast();

                        JSONArray json_array = new JSONArray();

                        RowTracker row_tracker = new RowTracker();

                        while (!cursor.isBeforeFirst())
                        {
                            try
                            {
                                JSONObject this_measurement = new JSONObject();

                                this_measurement.put("GatewayName", cursor.getString(cursor.getColumnIndexOrThrow(TableAuditTrail.COLUMN_GATEWAY_NAME)));
                                this_measurement.put("BedId", cursor.getString(cursor.getColumnIndexOrThrow(TableAuditTrail.COLUMN_BED_ID)));
                                this_measurement.put("Event", cursor.getInt(cursor.getColumnIndexOrThrow(TableAuditTrail.COLUMN_EVENT)));
                                this_measurement.put("Additional", cursor.getString(cursor.getColumnIndexOrThrow(TableAuditTrail.COLUMN_ADDITIONAL)));
                                this_measurement.put("ByUserId", cursor.getInt(cursor.getColumnIndexOrThrow(Table.COLUMN_BY_USER_ID)));
                                this_measurement.put("BluetoothAddress", cursor.getString(cursor.getColumnIndexOrThrow(TableAuditTrail.COLUMN_BLUETOOTH_ADDRESS)));

                                long measurement_timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(Table.COLUMN_TIMESTAMP));
                                this_measurement.put("Timestamp", Utils.convertTimestampToServerSqlDate(measurement_timestamp));

                                long written_to_android_database_timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(Table.COLUMN_WRITTEN_TO_ANDROID_DATABASE_TIMESTAMP));
                                this_measurement.put("WrittenToAndroidDatabaseTimestamp", Utils.convertTimestampToServerSqlDate(written_to_android_database_timestamp));

                                json_array.put(this_measurement);

                                row_tracker.addRow(cursor.getString(cursor.getColumnIndexOrThrow(TableAuditTrail.COLUMN_ID)));
                            }

                            catch (JSONException e1)
                            {
                                e1.printStackTrace();
                            }

                            cursor.moveToPrevious();
                        }

                        ArrayList<RowRange> row_ranges = row_tracker.getRowRanges();

                        server_interface.sendAuditEventsToServer(json_array, row_ranges);
                    }
                    break;

                    default:
                        break;
                }
            }
            else
            {
                Log.d(TAG, "checkIfAuditableEventsPendingToBeSentToServerAndSendIt : Cursor count is ZERO");
            }
            cursor.close();
        }

        return database_rows;
    }


    private int getNumberOfUnsyncedHistoricalPatientSessionInDatabase()
    {
        String selection = "";
        return getNumberOfRowsPending(QueryType.PATIENT_SESSIONS, selection);
    }


    private int getNumberOfUnsyncedHistoricalPatientSessions()
    {
        return number_of_unsynced_historical_patient_sessions;
    }


    public void decrementNumberOfUnsyncedHistoricalPatientSessions()
    {
        if (number_of_unsynced_historical_patient_sessions > 0)
        {
            number_of_unsynced_historical_patient_sessions--;
        }
    }


    public void incrementNumberOfUnsyncedHistoricalPatientSessions()
    {
        number_of_unsynced_historical_patient_sessions++;
    }


    public void checkForFullySyncedHistoricalSessions()
    {
        int historicalSessionsRowsPendingToBeSynced = server_sync_status.getTotalRowsInHistoricalSession();
        int numberOfUnsyncedHistoricalPatientSessions = getNumberOfUnsyncedHistoricalPatientSessions();

        Log.d(TAG, "checkForFullySyncedHistoricalSessions : Unsynced Historical Patient Sessions = " + numberOfUnsyncedHistoricalPatientSessions + " and Historical Rows Total = " + historicalSessionsRowsPendingToBeSynced);

        if ((historicalSessionsRowsPendingToBeSynced >= 0) && (numberOfUnsyncedHistoricalPatientSessions > 0))
        {
            Log.e(TAG, "checkForFullySyncedHistoricalSessions : Attempting to delete one or more fully synced sessions");
            patient_gateway_interface.deleteOldFullySyncedSessions();
        }
        else
        {
            Log.d(TAG, "checkForFullySyncedHistoricalSessions : No Unsynced Historical Session or No Rows Pending");
        }
    }

}
