package com.isansys.patientgateway.serverlink.webservices;

import android.os.Handler;
import android.os.Looper;

import com.isansys.common.enums.HttpOperationType;
import com.isansys.patientgateway.AES;
import com.isansys.patientgateway.LocalDatabaseStorage;
import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.serverlink.ServerSyncStatus;
import com.isansys.patientgateway.serverlink.ServerSyncing;
import com.isansys.patientgateway.serverlink.constants.ServerResponseErrorCodes;
import com.isansys.patientgateway.serverlink.constants.ServerStatusReceivedCode;
import com.isansys.patientgateway.serverlink.model.ServerPostParameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpWebServices extends WebServices
{
    /*  Encrypted Message objects  */
    // Do NOT remove the Public from here. Android Lint does not get that these NEED to be public
    @SuppressWarnings({"unused", "WeakerAccess"})
    static class IsansysEncryptedMessage
    {
        public String EncryptedBody;
        public String EncryptedSymmetricKey;
        public String KeyId;
        public String MethodName;
        public int    EncryptionType;
    }


    private final String webservice_application_name = "/PatientMeasurementDatabase/json/reply/";

    private final OkHttpClient.Builder client_builder = new OkHttpClient.Builder();


    public HttpWebServices(RemoteLoggingWithEnable logger, ServerSyncing.SessionInformation session_information, PatientGatewayInterface patient_gateway_interface, ServerSyncing desired_server_syncing, ServerSyncStatus sync_status, LocalDatabaseStorage database_storage, String passed_in_mac_address)
    {
        super(logger, session_information, patient_gateway_interface, desired_server_syncing, sync_status, database_storage, passed_in_mac_address);
    }


    private String unpackEncryptedResponse(String msg)
    {
        String localJson = msg;
        try
        {
            JSONObject json_object = new JSONObject(localJson);
            String encrypted_body = json_object.getString("EncryptedBody");
            // We don't use the Encryption Type yet, but it is here when we want to extend the functionality.
            //AES.OverTheWireEncryptionType encryption_type = AES.OverTheWireEncryptionType.values()[Integer.parseInt(json_object.getString("EncryptionType"))];
            Log.d(TAG, "Encrypted response detected.  Unpacking.");
            localJson = AES.decryptCBSWithPadding(encrypted_body);
        }
        catch (Exception e)
        {
            Log.d(TAG, "unpackEncryptedResponse Exception : No EncryptedMessage, so assume Not Encrypted. Exception = " + e.toString());
        }
        return localJson;
    }


    /* Incomplete definitions of HTTP status codes */
    final int HTTP_STATUS_OK = 200;
    final int HTTP_STATUS_UNAUTHORIZED = 401;
    final int HTTP_STATUS_NOT_ACCEPTABLE = 406;


    String getWebServiceAddressFromMeasurementType(String isansys_server_ip_address, HttpOperationType http_operation_type)
    {
        String address;

        if (use_https)
        {
            // Set HTTPS URL
            address = "https://" + isansys_server_ip_address + webservice_application_name;
        }
        else
        {
            // Set HTTP URL
            address = "http://" + isansys_server_ip_address + webservice_application_name;
        }

        switch (http_operation_type)
        {
            case DEVICE_INFO:
            {
                address = address + "DeviceInfoADD";
            }
            break;

            case CHECK_START_PATIENT_SESSION:
            {
                address = address + "GetStatus_PatientStartSessionTime";
            }
            break;

            case CHECK_END_PATIENT_SESSION:
            {
                address = address + "GetStatus_PatientEndSessionTime";
            }
            break;

            case CHECK_START_DEVICE_SESSION:
            {
                address = address + "GetStatus_DeviceStartSessionTime";
            }
            break;

            case CHECK_END_DEVICE_SESSION:
            {
                address = address + "GetStatus_DeviceEndSessionTime";
            }
            break;

            case START_PATIENT_SESSION:
            case END_PATIENT_SESSION:
            {
                address = address + "PatientSessionADD";
            }
            break;

            case PATIENT_SESSION_FULLY_SYNCED:
            {
                address = address + "PatientSessionSETGatewayDataCompleteMultiple";
            }
            break;

            case START_DEVICE_SESSION:
            case END_DEVICE_SESSION:
            {
                address = address + "DeviceSessionADD";
            }
            break;

            case PATIENT_DETAILS:
            {
                address = address + "PatientDetailsADD";
            }
            break;

            case LIFETOUCH_HEART_RATE:
            {
                address = address + "LifetouchHeartRateADDMultiple";
            }
            break;
            case LIFETOUCH_RESPIRATION_RATE:
            {
                address = address + "LifetouchRespirationRateADDMultiple";
            }
            break;
            case LIFETOUCH_HEART_BEAT:
            {
                address = address + "LifetouchHeartBeatADDMultiple";
            }
            break;
            case LIFETOUCH_SETUP_MODE_SAMPLE:
            {
                address = address + "LifetouchSetupModeADDMultiple";
            }
            break;
            case LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE:
            {
                address = address + "LifetouchRawAccelerometerModeADDMultiple";
            }
            break;
            case LIFETOUCH_BATTERY:
            {
                address = address + "LifetouchBatteryADDMultiple";
            }
            break;
            case LIFETOUCH_PATIENT_ORIENTATION:
            {
                address = address + "LifetouchPatientOrientationADDMultiple";
            }
            break;

            case LIFETEMP_TEMPERATURE:
            {
                address = address + "LifetempTemperatureADDMultiple";
            }
            break;
            case LIFETEMP_BATTERY:
            {
                address = address + "LifetempBatteryADDMultiple";
            }
            break;

            case PULSE_OX_MEASUREMENT:
            {
                address = address + "OximeterMeasurementADDMultiple";
            }
            break;
            case PULSE_OX_INTERMEDIATE_MEASUREMENT:
            {
                address = address + "OximeterIntermediateMeasurementADDMultiple";
            }
            break;
            case PULSE_OX_SETUP_MODE_SAMPLE:
            {
                address = address + "OximeterSetupModeADDMultiple";
            }
            break;
            case PULSE_OX_BATTERY:
            {
                address = address + "OximeterBatteryADDMultiple";
            }
            break;

            case BLOOD_PRESSURE_MEASUREMENT:
            {
                address = address + "BloodPressureMeasurementADDMultiple";
            }
            break;
            case BLOOD_PRESSURE_BATTERY:
            {
                address = address + "BloodPressureBatteryADDMultiple";
            }
            break;

            case WEIGHT_SCALE_MEASUREMENT:
            {
                address = address + "WeightScaleMeasurementADDMultiple";
            }
            break;
            case WEIGHT_SCALE_BATTERY:
            {
                address = address + "WeightScaleBatteryADDMultiple";
            }
            break;

            case CONNECTION_EVENT:
            {
                address = address + "ConnectionEventADDMultiple";
            }
            break;

            case SERVER_PING:
            {
                address = address + "PingTest";
            }
            break;

            case CHECK_PATIENT_ID:
            {
                address = address + "PatientStatus";
            }
            break;

            case MANUALLY_ENTERED_HEART_RATES:
            {
                address = address + "ManuallyEnteredHeartRateMeasurementADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_RATES:
            {
                address = address + "ManuallyEnteredRespirationRateMeasurementADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_TEMPERATURES:
            {
                address = address + "ManuallyEnteredTemperatureMeasurementADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
            {
                address = address + "ManuallyEnteredOximeterMeasurementADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            {
                address = address + "ManuallyEnteredBloodPressureMeasurementADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS:
            {
                address = address + "ManuallyEnteredConsciousnessLevelADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            {
                address = address + "ManuallyEnteredSupplementalOxygenLevelADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_ANNOTATIONS:
            {
                address = address + "ManuallyEnteredAnnotationADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            {
                address = address + "ManuallyEnteredCapillaryRefillTimeMeasurementADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                address = address + "ManuallyEnteredRespirationDistressMeasurementADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                address = address + "ManuallyEnteredFamilyOrNurseConcernMeasurementADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                address = address + "ManuallyEnteredUrineOutputADDMultiple";
            }
            break;

            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            {
                address = address + "ManuallyEnteredWeightMeasurementADDMultiple";
            }
            break;

            case EARLY_WARNING_SCORES:
            {
                address = address + "EarlyWarningScoreMeasurementADDMultiple";
            }
            break;

            case GET_DEFAULT_EARLY_WARNING_SCORES_LIST:
            {
                address = address + "ThresholdsetInfo";
            }
            break;

            case SETUP_MODE_LOG:
            {
                address = address + "SetupModeLogADDMultiple";
            }
            break;

            case AUDITABLE_EVENTS:
            {
                address = address + "GatewayAuditLogADDMultiple";
            }
            break;

            case INVALID:
            {
                // These are here instead of a default case on purpose, so if new ones are added Android Lint will complain about missing entries
            }
            break;
        }

        return address;
    }


    static class PostDataResponse
    {
        ServerResponseErrorCodes errorCode;
        String receivedJson;
    }


    PostDataResponse postData(String message, String webservice_address, String method, HttpOperationType http_operation_type)
    {
        String error_message;
        PostDataResponse postDataResponse = new PostDataResponse();

        Log.d(TAG, "--------");
        Log.d(TAG, "postData Start for " + http_operation_type.toString() + ". Address = " + webservice_address);
        logLongLine("postData", message);

        okhttp3.Response response = null;

        try
        {
            if (use_encryption_on_webservice_calls)
            {
                // We take the original call, Encrypt the data, and wrap it, with the target message MethodName and send it to the
                //   Handler for IsansysEncryptedMessages on the WebServices
                IsansysEncryptedMessage enc_msg = new IsansysEncryptedMessage();

                try
                {
                    if (!message.isEmpty())
                    {
                        enc_msg.EncryptedBody = AES.encryptCBSWithPadding(message);
                    }

                    enc_msg.EncryptionType = AES.OverTheWireEncryptionType.CBS_WITH_PKCS5PADDING.ordinal();
                    enc_msg.MethodName = method;
                    webservice_address = webservice_address.replaceAll(method, "IsansysEncryptedMessage");
                    ObjectMapper mapper = new ObjectMapper();
                    message = mapper.writeValueAsString(enc_msg);
                }
                catch (JsonGenerationException
                        | InvalidKeyException
                        | NoSuchAlgorithmException
                        | NoSuchPaddingException
                        | InvalidAlgorithmParameterException
                        | IllegalBlockSizeException
                        | BadPaddingException e1)
                {
                    Log.d(TAG, "Failed to create IsansysEncryptedMessage. " );
                }
            }


            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
            Request request = new Request.Builder()
                    .url(webservice_address)
                    .post(RequestBody.create(message, MEDIA_TYPE_JSON))
                    .build();

            client_builder.connectTimeout(20, TimeUnit.SECONDS);
            client_builder.writeTimeout(20, TimeUnit.SECONDS);
            client_builder.readTimeout(20, TimeUnit.SECONDS);

            if (use_authentication_for_webservice_calls)
            {
                final String username = auth.username();
                final String password = auth.password(webservice_address + username);

                client_builder.authenticator((route, response1) -> {
                    String credential = Credentials.basic(username, password);
                    return response1.request().newBuilder().header("Authorization", credential).build();
                });

                Log.d(TAG, "Set Credentials POST");
            }

            OkHttpClient client = client_builder.build();

            response = client.newCall(request).execute();

            int status_code = response.code();

            // Get the Servers response
            String responseBody = response.body().string();

            if (use_encryption_on_webservice_calls)
            {
                //  Process Encrypted Response.  Will try to detected an Encrypted Response, if not one, will return the original.
                responseBody = unpackEncryptedResponse(responseBody);
            }

            logLongLine("ResponseBody", responseBody);

            String json = responseBody;

            postDataResponse.receivedJson = json;

            response.close();


            // Process the response
            switch (status_code)
            {
                case HTTP_STATUS_OK:
                {
                    if (json.equals("PONG"))
                    {
                        Log.i(TAG, "StatusCode : PONG from the server");
                    }
                    else
                    {
                        String RESPONSE_STATUS_CODE = "StatusCode";
                        String RESPONSE_STATUS_MESSAGE = "MessageStatus";

                        if (json.contains("["))
                        {
                            try
                            {
                                JSONArray reader_array = new JSONArray(json);

                                for (int i = 0; i < reader_array.length(); i++)
                                {
                                    JSONObject json_data = reader_array.getJSONObject(i);

                                    String response_message = json_data.getString(RESPONSE_STATUS_MESSAGE);
                                    int response_status_code = Integer.parseInt(json_data.getString(RESPONSE_STATUS_CODE));

                                    ServerStatusReceivedCode received_server_status_code = ServerStatusReceivedCode.values()[response_status_code];

                                    if (received_server_status_code != ServerStatusReceivedCode.RESPONSE_CODE__VALID)
                                    {
                                        patient_gateway_interface.handleServerInvalidStatusCode(response_status_code, response_message);

                                        error_message = "ALERT ALERT!!!!!!! (JSON ARRAY). Server message \"" + response_message + "\" with status code " + received_server_status_code;
                                        Log.e(TAG, error_message);

                                        postDataResponse.errorCode = ServerResponseErrorCodes.MISC_ERROR;

                                        // update the sessionInformation enum for Gateway
                                        post_updateCurrentSessionInformation_NotSendToServer();

                                        return postDataResponse;
                                    }
                                }
                            }
                            catch (JSONException e)
                            {
                                error_message = "ERROR ServerHttpPost jsonObject : Error Can't create the Reader = " + e.toString();
                                Log.e(TAG, error_message);

                                postDataResponse.errorCode = ServerResponseErrorCodes.MISC_ERROR;

                                // update the sessionInformation enum for Gateway
                                post_updateCurrentSessionInformation_NotSendToServer();

                                return postDataResponse;
                            }
                        }
                        else
                        {
                            try
                            {
                                JSONObject reader = new JSONObject(json);

                                String response_message = reader.getString(RESPONSE_STATUS_MESSAGE);
                                int response_status_code = Integer.parseInt(reader.getString(RESPONSE_STATUS_CODE));

                                ServerStatusReceivedCode received_server_status_code = ServerStatusReceivedCode.values()[response_status_code];

                                if (received_server_status_code != ServerStatusReceivedCode.RESPONSE_CODE__VALID)
                                {
                                    if (received_server_status_code == ServerStatusReceivedCode.RESPONSE_CODE__DEVICE_STATUS__DEVICE_DOES_NOT_EXIST)
                                    {
                                        // Special case for CHECK_DEVICE_STATUS as it can return RESPONSE_CODE__VALID as well as RESPONSE_CODE__DEVICE_STATUS__DEVICE_DOES_NOT_EXIST
                                        error_message = "Alert: received_server_status_code = RESPONSE_CODE__DEVICE_STATUS__DEVICE_DOES_NOT_EXIST....";
                                        Log.e(TAG, error_message);
                                    }
                                    else
                                    {
                                        patient_gateway_interface.handleServerInvalidStatusCode(response_status_code, response_message);

                                        error_message = "ALERT ALERT!!!!!!! (Non JSON array). Server message \"" + response_message + "\" with status code " + received_server_status_code;
                                        Log.e(TAG, error_message);

                                        postDataResponse.errorCode = ServerResponseErrorCodes.MISC_ERROR;

                                        // update the sessionInformation enum for Gateway
                                        post_updateCurrentSessionInformation_NotSendToServer();

                                        return postDataResponse;
                                    }
                                }
                            }
                            catch (JSONException e)
                            {
                                error_message = "ERROR ServerHttpPost jsonObject : Error Can't create the Reader = " + e.toString();
                                Log.e(TAG, error_message);

                                postDataResponse.errorCode = ServerResponseErrorCodes.MISC_ERROR;

                                // update the sessionInformation enum for Gateway
                                post_updateCurrentSessionInformation_NotSendToServer();

                                return postDataResponse;
                            }
                        }
                    }

                    Log.d(TAG, http_operation_type.toString() + " success");

                    authentication_ok = true;

                    postDataResponse.errorCode = ServerResponseErrorCodes.NONE;

                    return postDataResponse;
                }

                case HTTP_STATUS_UNAUTHORIZED:
                {
                    authentication_ok = false;

                    postDataResponse.errorCode = ServerResponseErrorCodes.UNAUTHORIZED;

                    error_message = "ServerHttpPost Status code : " + status_code + " for " + webservice_address;
                    Log.e(TAG, error_message);
                }
                break;

                case HTTP_STATUS_NOT_ACCEPTABLE:
                default:
                {
                    postDataResponse.errorCode = ServerResponseErrorCodes.MISC_ERROR;

                    error_message = "ServerHttpPost Status code : " + status_code + " for " + webservice_address;
                    Log.e(TAG, error_message);
                }
                break;
            }
        }
        catch (IOException e)
        {
            postDataResponse.errorCode = ServerResponseErrorCodes.IO_EXCEPTION;

            error_message = "ERROR ServerHttpPost IOException : Error message = " + e.toString();
            Log.e(TAG, error_message);
        }
        catch (Exception e)
        {
            postDataResponse.errorCode = ServerResponseErrorCodes.MISC_ERROR;

            error_message = "ERROR ServerHttpPost IOException : Error message = " + e.toString();
            Log.e(TAG, error_message);
        }
        finally
        {
            if(response != null)
            {
                response.close();
            }
        }

        // update the sessionInformation enum for Gateway
        post_updateCurrentSessionInformation_NotSendToServer();

        return postDataResponse;
    }


    @Override
    public void sendToServer(ServerPostParameters server_post_parameters)
    {
        Executors.newSingleThreadExecutor().execute(() -> {

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> server_interface_busy = true);

            PostDataResponse postDataResponse = new PostDataResponse();
            boolean result;

            // Long running operation
            try
            {
                String webservice_address = getWebServiceAddressFromMeasurementType(server_address, server_post_parameters.http_operation_type);

                //  Need to extract the WebMethod name.  Done here as we need the params passed in for server IP address etc
                //  So we strip of the address, and the HTTP string to get the Method Name.
                //  This means we don't need to modify every web Service call just to turn on Encryption.
                String method = webservice_address.replace(server_address + webservice_application_name, "").replace("http://", "").replace("https://", "");

                postDataResponse = postData(server_post_parameters.json_string, webservice_address, method, server_post_parameters.http_operation_type);

                result = postDataResponse.errorCode == ServerResponseErrorCodes.NONE;
            }
            catch (Exception e)
            {
                Log.e(TAG, "ERROR ServerHttpPost : Error message = " + e);

                postDataResponse.errorCode = ServerResponseErrorCodes.MISC_ERROR;
                result = false;
            }

            // Make "final" copies so they can be accessed from the below code
            final boolean finalResult = result;
            final PostDataResponse finalPostDataResponse = postDataResponse;

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                Log.d(TAG, "onPostExecute ServerHttpPost : " + server_post_parameters.http_operation_type + ". result = " + finalResult);
                logLongLine("onPostExecute", finalPostDataResponse.receivedJson);

                if (finalPostDataResponse.errorCode == ServerResponseErrorCodes.SERVER_UNREACHABLE)
                {
                    Log.d(TAG, "Cant reach server - ServerResponseErrorCodes.SERVER_UNREACHABLE");
                }

                processServersWebserviceResponse(finalResult, finalPostDataResponse.errorCode, finalPostDataResponse.receivedJson, server_post_parameters);
            });
        });
    }
}
