package com.isansys.patientgateway;

import android.bluetooth.BluetoothAdapter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.isansys.patientgateway.csvwriter.CsvWriter;
import com.isansys.patientgateway.database.Table;
import com.isansys.patientgateway.database.TableBloodPressureMeasurement;
import com.isansys.patientgateway.database.TableEarlyWarningScore;
import com.isansys.patientgateway.database.TableLifetempMeasurement;
import com.isansys.patientgateway.database.TableLifetouchHeartBeat;
import com.isansys.patientgateway.database.TableLifetouchHeartRate;
import com.isansys.patientgateway.database.TableLifetouchPatientOrientation;
import com.isansys.patientgateway.database.TableLifetouchRawAccelerometerModeSample;
import com.isansys.patientgateway.database.TableLifetouchRespirationRate;
import com.isansys.patientgateway.database.TableLifetouchSetupModeRawSample;
import com.isansys.patientgateway.database.TableManuallyEnteredAnnotation;
import com.isansys.patientgateway.database.TableManuallyEnteredBloodPressure;
import com.isansys.patientgateway.database.TableManuallyEnteredConsciousnessLevel;
import com.isansys.patientgateway.database.TableManuallyEnteredHeartRate;
import com.isansys.patientgateway.database.TableManuallyEnteredRespirationRate;
import com.isansys.patientgateway.database.TableManuallyEnteredSpO2;
import com.isansys.patientgateway.database.TableManuallyEnteredSupplementalOxygenLevel;
import com.isansys.patientgateway.database.TableManuallyEnteredTemperature;
import com.isansys.patientgateway.database.TableManuallyEnteredWeight;
import com.isansys.patientgateway.database.TableOximeterIntermediateMeasurement;
import com.isansys.patientgateway.database.TableOximeterMeasurement;
import com.isansys.patientgateway.database.TableOximeterSetupModeRawSample;
import com.isansys.patientgateway.database.contentprovider.IsansysPatientGatewayContentProvider;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.enums.DeviceOrPatientSession;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

class CsvExport
{
    private final RemoteLogging Log;
    private final ContextInterface gateway_context_interface;
    private final String TAG = this.getClass().getSimpleName();

    public CsvExport(ContextInterface context_interface, RemoteLogging logger)
    {
        gateway_context_interface = context_interface;
        Log = logger;
    }


    private String getCsvFileName(String description, long start_device_session_time, long end_device_session_time)
    {
        File sd = Environment.getExternalStorageDirectory();
        
        String start_time = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(start_device_session_time);
        String end_time = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(end_device_session_time);
        
        String gateway_name = BluetoothAdapter.getDefaultAdapter().getName();

        return sd + "/" + gateway_name + "_" + start_time + "_to_" + end_time + "_" + description + ".csv";
    }


    private Cursor getCursorForThisDeviceSessionId(int android_database_device_session_id, Uri uri, String[] measurement_specific_projection)
    {
        String[] common_projection = {
                Table.COLUMN_ID,
                Table.COLUMN_TIMESTAMP,
                Table.COLUMN_DEVICE_SESSION_NUMBER,
        };

        String[] projection = Utils.concatStringArrays(common_projection, measurement_specific_projection);

        String selection = Table.COLUMN_DEVICE_SESSION_NUMBER + "=?";
        String[] selectionArgs = { String.valueOf(android_database_device_session_id) };
        return gateway_context_interface.getAppContext().getContentResolver().query(uri, projection, selection, selectionArgs, null, null);
    }


    private Cursor getCursorForThisPatientSessionId(int android_database_patient_session_id, Uri uri, String[] measurement_specific_projection)
    {
        String[] common_projection = {
                Table.COLUMN_ID,
                Table.COLUMN_TIMESTAMP,
                Table.COLUMN_PATIENT_SESSION_NUMBER,
        };

        String[] projection = Utils.concatStringArrays(common_projection, measurement_specific_projection);

        String selection = Table.COLUMN_PATIENT_SESSION_NUMBER + "=?";
        String[] selectionArgs = { String.valueOf(android_database_patient_session_id) };
        return gateway_context_interface.getAppContext().getContentResolver().query(uri, projection, selection, selectionArgs, null, null);
    }


    private void getDataAndWriteCsv(int session_id,
                                    DeviceOrPatientSession device_or_patient_session,
                                    long session_start_time,
                                    long session_end_time,
                                    String csv_file_name,
                                    Uri uri,
                                    String[] measurement_specific_projection,
                                    String[] measurement_specific_csv_headers)
    {
        try
        {
            String log_line;

            Cursor cursor;

            if (device_or_patient_session == DeviceOrPatientSession.DEVICE_SESSION)
            {
                log_line = "getDataAndWriteCsv : Device session_id = " + session_id;

                cursor = getCursorForThisDeviceSessionId(session_id, uri, measurement_specific_projection);
            }
            else
            {
                log_line = "getDataAndWriteCsv : Patient session_id = " + session_id;

                cursor = getCursorForThisPatientSessionId(session_id, uri, measurement_specific_projection);
            }

            if (cursor != null)
            {
                if(cursor.getCount() > 0)
                {
                    String output_filename = getCsvFileName(csv_file_name, session_start_time, session_end_time);
                    CsvWriter csv_writer = new CsvWriter(new FileWriter(output_filename, true), ',');

                    // Write the Headers
                    csv_writer.write("Id");
                    for (String header : measurement_specific_csv_headers)
                    {
                        csv_writer.write(header);
                    }
                    csv_writer.write("Timestamp");
                    csv_writer.endRecord();

                    Log.d(TAG, log_line + ". cursor.getCount() = " + cursor.getCount());

                    cursor.moveToFirst();

                    while (!cursor.isAfterLast())
                    {
                        csv_writer.write(cursor.getString(cursor.getColumnIndexOrThrow(Table.COLUMN_ID)));

                        for (String table : measurement_specific_projection)
                        {
                            csv_writer.write(cursor.getString(cursor.getColumnIndexOrThrow(table)));
                        }

                        String timestamp = convertTimestampToCsvFriendlyString(cursor.getLong(cursor.getColumnIndexOrThrow(Table.COLUMN_TIMESTAMP)));
                        csv_writer.write(timestamp);

                        csv_writer.endRecord();

                        cursor.moveToNext();
                    }

                    csv_writer.close();
                }
                else
                {
                    Log.d(TAG, "getDataAndWriteCsv : Cursor count is ZERO. CSV files is not created.");
                }

                cursor.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public void exportLifetouchHeartRateData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportLifetouchHeartRateData");

        String[] measurement_specific_projection = {
                TableLifetouchHeartRate.COLUMN_HEART_RATE,
        };

        String[] measurement_specific_csv_headers = {
                "Heart Rate",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_RATES,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportLifetouchRespirationRateData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportLifetouchRespirationRateData");

        String[] measurement_specific_projection = {
                TableLifetouchRespirationRate.COLUMN_RESPIRATION_RATE,
        };

        String[] measurement_specific_csv_headers = {
                "Respiration Rate",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RESPIRATION_RATES,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportLifetouchHeartBeatData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportLifetouchHeartBeatData");

        try 
        {
            String[] measurement_specific_projection = {
                    TableLifetouchHeartBeat.COLUMN_AMPLITUDE,
                    TableLifetouchHeartBeat.COLUMN_ACTIVITY_LEVEL,
                    TableLifetouchHeartBeat.COLUMN_RR_INTERVAL,
                    };

            Cursor cursor = getCursorForThisDeviceSessionId(device_info.getAndroidDeviceSessionId(), IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_HEART_BEATS, measurement_specific_projection);
            
            if (cursor != null)
            {
                if(cursor.getCount() > 0)
                {
                    String output_filename = getCsvFileName(csv_file_name, device_info.device_session_start_time, device_info.device_session_end_time);
                    CsvWriter csv_writer = new CsvWriter(new FileWriter(output_filename, true), ',');

                    // Write the Headers
                    csv_writer.write("Id");
                    csv_writer.write("Amplitude");
                    csv_writer.write("R-R Interval");
                    csv_writer.write("ActivityLevel");
                    csv_writer.write("Timestamp");
                    csv_writer.endRecord();

                    Log.d(TAG, "exportLifetouchHeartBeatData : cursor.getCount() = " + cursor.getCount());

                    cursor.moveToFirst();

                    while (!cursor.isAfterLast())
                    {
                        String timestamp = convertTimestampToCsvFriendlyString(cursor.getLong(cursor.getColumnIndexOrThrow(TableLifetouchHeartBeat.COLUMN_TIMESTAMP)));

                        // Write the Record
                        csv_writer.write(cursor.getString(cursor.getColumnIndexOrThrow(TableLifetouchHeartBeat.COLUMN_ID)));
                        csv_writer.write(cursor.getString(cursor.getColumnIndexOrThrow(TableLifetouchHeartBeat.COLUMN_AMPLITUDE)));
                        csv_writer.write(cursor.getString(cursor.getColumnIndexOrThrow(TableLifetouchHeartBeat.COLUMN_RR_INTERVAL)));
                        csv_writer.write(cursor.getString(cursor.getColumnIndexOrThrow(TableLifetouchHeartBeat.COLUMN_ACTIVITY_LEVEL)));
                        csv_writer.write(timestamp);
                        csv_writer.endRecord();

                        cursor.moveToNext();
                    }
                    csv_writer.close();
                }
                else
                {
                    Log.d(TAG, "exportLifetouchHeartBeatData : Cursor count is ZERO ");
                }

                cursor.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public void exportLifetouchSetupModeData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportLifetouchSetupModeData");

        String[] measurement_specific_projection = {
                TableLifetouchSetupModeRawSample.COLUMN_SAMPLE_VALUE,
        };

        String[] measurement_specific_csv_headers = {
                "Sample",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_SETUP_MODE_SAMPLES,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportLifetouchRawAccelerometerModeData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportLifetouchRawAccelerometerModeData");

        String[] measurement_specific_projection = {
                TableLifetouchRawAccelerometerModeSample.COLUMN_X_SAMPLE_VALUE,
                TableLifetouchRawAccelerometerModeSample.COLUMN_Y_SAMPLE_VALUE,
                TableLifetouchRawAccelerometerModeSample.COLUMN_Z_SAMPLE_VALUE,
        };

        String[] measurement_specific_csv_headers = {
                "Sample",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLES,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportLifetouchPatientOrientationData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportLifetouchPatientOrientationData");

        String[] measurement_specific_projection = {
                TableLifetouchPatientOrientation.COLUMN_PATIENT_ORIENTATION,
        };

        String[] measurement_specific_csv_headers = {
                "Orientation",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETOUCH_PATIENT_ORIENTATION,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportLifetempMeasurementData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportLifetempMeasurementData");

        String[] measurement_specific_projection = {
                TableLifetempMeasurement.COLUMN_TEMPERATURE,
        };

        String[] measurement_specific_csv_headers = {
                "Temperature",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_LIFETEMP_TEMPERATURE_MEASUREMENTS,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportNoninWristOxMeasurementData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportNoninWristOxMeasurementData");

        String[] measurement_specific_projection = {
                TableOximeterMeasurement.COLUMN_SPO2,
                TableOximeterMeasurement.COLUMN_PULSE,
        };

        String[] measurement_specific_csv_headers = {
                "SpO2",
                "Pulse",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_MEASUREMENTS,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportNoninWristOxIntermediateData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportNoninWristOxIntermediateData");

        String[] measurement_specific_projection = {
                TableOximeterIntermediateMeasurement.COLUMN_SPO2,
                TableOximeterIntermediateMeasurement.COLUMN_PULSE,
        };

        String[] measurement_specific_csv_headers = {
                "SpO2",
                "Pulse",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_INTERMEDIATE_MEASUREMENTS,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportNoninWristOxSetupModeData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportNoninWristOxSetupModeData");

        String[] measurement_specific_projection = {
                TableOximeterSetupModeRawSample.COLUMN_SAMPLE_VALUE,
        };

        String[] measurement_specific_csv_headers = {
                "Sample",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_OXIMETER_SETUP_MODE_SAMPLES,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportBloodPressureMeasurementData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportBloodPressureMeasurementData");

        String[] measurement_specific_projection = {
                TableBloodPressureMeasurement.COLUMN_SYSTOLIC,
                TableBloodPressureMeasurement.COLUMN_DIASTOLIC,
                TableBloodPressureMeasurement.COLUMN_PULSE,
        };

        String[] measurement_specific_csv_headers = {
                "Systolic",
                "Diastolic",
                "Pulse",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_BLOOD_PRESSURE_MEASUREMENTS,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }
    
    
    public void exportEarlyWarningScoreMeasurementData(DeviceInfo device_info, String csv_file_name)
    {
        Log.d(TAG, "exportEarlyWarningScoreMeasurementData");

        String[] measurement_specific_projection = {
                TableEarlyWarningScore.COLUMN_EARLY_WARNING_SCORE,
                TableEarlyWarningScore.COLUMN_MAX_POSSIBLE,
                TableEarlyWarningScore.COLUMN_IS_SPECIAL_ALERT,
        };

        String[] measurement_specific_csv_headers = {
                "EWS",
                "MaxPossible",
                "IsSpecialAlert",
        };

        getDataAndWriteCsv(device_info.getAndroidDeviceSessionId(),
                DeviceOrPatientSession.DEVICE_SESSION,
                device_info.device_session_start_time,
                device_info.device_session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_EARLY_WARNING_SCORES,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportManuallyEnteredHeartRateData(int android_database_patient_session_id, long session_start_time, long session_end_time, String csv_file_name)
    {
        Log.d(TAG, "exportManuallyEnteredHeartRateData");

        String[] measurement_specific_projection = {
                TableManuallyEnteredHeartRate.COLUMN_HEART_RATE,
        };

        String[] measurement_specific_csv_headers = {
                "Heart Rate",
        };

        getDataAndWriteCsv(android_database_patient_session_id,
                DeviceOrPatientSession.PATIENT_SESSION,
                session_start_time,
                session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_HEART_RATES,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportManuallyEnteredRespirationRateData(int android_database_patient_session_id, long session_start_time, long session_end_time, String csv_file_name)
    {
        Log.d(TAG, "exportManuallyEnteredRespirationRateData");

        String[] measurement_specific_projection = {
                TableManuallyEnteredRespirationRate.COLUMN_RESPIRATION_RATE,
        };

        String[] measurement_specific_csv_headers = {
                "Respiration Rate",
        };

        getDataAndWriteCsv(android_database_patient_session_id,
                DeviceOrPatientSession.PATIENT_SESSION,
                session_start_time,
                session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_RESPIRATION_RATES,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportManuallyEnteredTemperatureData(int android_database_patient_session_id, long session_start_time, long session_end_time, String csv_file_name)
    {
        Log.d(TAG, "exportManuallyEnteredTemperatureData");

        String[] measurement_specific_projection = {
                TableManuallyEnteredTemperature.COLUMN_TEMPERATURE,
        };

        String[] measurement_specific_csv_headers = {
                "Temperature",
        };

        getDataAndWriteCsv(android_database_patient_session_id,
                DeviceOrPatientSession.PATIENT_SESSION,
                session_start_time,
                session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_TEMPERATURE,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportManuallyEnteredSpO2Data(int android_database_patient_session_id, long session_start_time, long session_end_time, String csv_file_name)
    {
        Log.d(TAG, "exportManuallyEnteredSpO2Data");

        String[] measurement_specific_projection = {
                TableManuallyEnteredSpO2.COLUMN_SPO2,
        };

        String[] measurement_specific_csv_headers = {
                "SpO2",
        };

        getDataAndWriteCsv(android_database_patient_session_id,
                DeviceOrPatientSession.PATIENT_SESSION,
                session_start_time,
                session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SPO2,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportManuallyEnteredBloodPressureData(int android_database_patient_session_id, long session_start_time, long session_end_time, String csv_file_name)
    {
        Log.d(TAG, "exportManuallyEnteredBloodPressureData");

        String[] measurement_specific_projection = {
                TableManuallyEnteredBloodPressure.COLUMN_SYSTOLIC,
                TableManuallyEnteredBloodPressure.COLUMN_DIASTOLIC,
        };

        String[] measurement_specific_csv_headers = {
                "Systolic",
                "Diastolic",
        };

        getDataAndWriteCsv(android_database_patient_session_id,
                DeviceOrPatientSession.PATIENT_SESSION,
                session_start_time,
                session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_BLOOD_PRESSURE,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportManuallyEnteredWeightData(int android_database_patient_session_id, long session_start_time, long session_end_time, String csv_file_name)
    {
        Log.d(TAG, "exportManuallyEnteredBloodPressureData");

        String[] measurement_specific_projection = {
                TableManuallyEnteredWeight.COLUMN_WEIGHT,
        };

        String[] measurement_specific_csv_headers = {
                "Weight",
        };

        getDataAndWriteCsv(android_database_patient_session_id,
                DeviceOrPatientSession.PATIENT_SESSION,
                session_start_time,
                session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_WEIGHT,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportManuallyEnteredConsciousnessLevelData(int android_database_patient_session_id, long session_start_time, long session_end_time, String csv_file_name)
    {
        Log.d(TAG, "exportManuallyEnteredConsciousnessLevelData");

        String[] measurement_specific_projection = {
                TableManuallyEnteredConsciousnessLevel.COLUMN_CONSCIOUSNESS_LEVEL,
        };

        String[] measurement_specific_csv_headers = {
                "Consciousness Level",
        };

        getDataAndWriteCsv(android_database_patient_session_id,
                DeviceOrPatientSession.PATIENT_SESSION,
                session_start_time,
                session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportManuallyEnteredSupplementalOxygenData(int android_database_patient_session_id, long session_start_time, long session_end_time, String csv_file_name)
    {
        Log.d(TAG, "exportManuallyEnteredSupplementalOxygenData");

        String[] measurement_specific_projection = {
                TableManuallyEnteredSupplementalOxygenLevel.COLUMN_SUPPLEMENTAL_OXYGEN_LEVEL,
        };

        String[] measurement_specific_csv_headers = {
                "Supplemental Oxygen",
        };

        getDataAndWriteCsv(android_database_patient_session_id,
                DeviceOrPatientSession.PATIENT_SESSION,
                session_start_time,
                session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVEL,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    public void exportManuallyEnteredAnnotationData(int android_database_patient_session_id, long session_start_time, long session_end_time, String csv_file_name)
    {
        Log.d(TAG, "exportManuallyEnteredAnnotationData");

        String[] measurement_specific_projection = {
                TableManuallyEnteredAnnotation.COLUMN_ANNOTATION_TEXT,
        };

        String[] measurement_specific_csv_headers = {
                "Annotation",
        };

        getDataAndWriteCsv(android_database_patient_session_id,
                DeviceOrPatientSession.PATIENT_SESSION,
                session_start_time,
                session_end_time,
                csv_file_name,
                IsansysPatientGatewayContentProvider.CONTENT_URI_MANUALLY_ENTERED_ANNOTATIONS,
                measurement_specific_projection,
                measurement_specific_csv_headers);
    }


    private String convertTimestampToCsvFriendlyString(long timestamp)
    {
        DateTime dt = new DateTime(new Date(timestamp), DateTimeZone.UTC);

        return dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd_HH:mm:ss.SSS"));
    }
}
