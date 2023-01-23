package com.isansys.patientgateway.serverlink.webservices;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.isansys.patientgateway.LocalDatabaseStorage;
import com.isansys.patientgateway.PatientGatewayInterface;
import com.isansys.patientgateway.RemoteLoggingWithEnable;
import com.isansys.patientgateway.serverlink.ServerSyncStatus;
import com.isansys.patientgateway.serverlink.ServerSyncing;
import com.isansys.patientgateway.serverlink.constants.ServerResponseErrorCodes;
import com.isansys.patientgateway.serverlink.model.MqttDataStructure;
import com.isansys.patientgateway.serverlink.model.ServerPostParameters;
import com.isansys.patientgateway.serverlink.realtimeclient.MqttClient;
import com.isansys.patientgateway.serverlink.realtimeclient.RealTimeClient;
import com.isansys.patientgateway.serverlink.realtimeclient.ResultHandler;
import com.isansys.patientgateway.serverlink.realtimeclient.SubscriptionHandler;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class RealTimeWebservices extends WebServices
{
    RealTimeClient realTimeClient;

    private final String topic_to_server = "topic_to_server";
    public String topic_from_server;
    private String topic_lwt;

    private final long DEFAULT_MESSAGE_TIMEOUT_MS = (5 * DateUtils.SECOND_IN_MILLIS);
    private final long DEFAULT_MESSAGE_TIMEOUT_TICK_LENGTH_MS = 500;

    final Timer message_timeout_timer;

    final RemoteLoggingWithEnable Log;
    final String client_id;
    final Context context;

    public RealTimeWebservices(RemoteLoggingWithEnable logger, ServerSyncing.SessionInformation session_information, PatientGatewayInterface patient_gateway_interface, ServerSyncing desired_server_syncing, ServerSyncStatus sync_status, LocalDatabaseStorage database_storage, String passed_in_mac_address, Context context)
    {
        super(logger, session_information, patient_gateway_interface, desired_server_syncing, sync_status, database_storage, passed_in_mac_address);

        Log = logger;

        client_id = BluetoothAdapter.getDefaultAdapter().getName() + "_" + patient_gateway_interface.getAndroidUniqueDeviceId();
        this.context = context;
        message_timeout_timer = new Timer();

        setupMessageTimeoutTimer(DEFAULT_MESSAGE_TIMEOUT_TICK_LENGTH_MS);
    }


    @Override
    public void setIsansysServerAddress(String desired_address, String desired_port)
    {
        super.setIsansysServerAddress(desired_address, desired_port);

        realTimeClient = new MqttClient(desired_address, desired_port, Log, client_id, context, true);

        realTimeClient.connect(connection_callback);

        realTimeClient.subscribe(topic_from_server, MqttMessage.class, server_subscription_handler);
    }


    private void setupMessageTimeoutTimer(final long timeout_tick_length_ms)
    {
        message_timeout_timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                synchronized(messages_in_progress)
                {
                    // Using an iterator so we can modify the list while iterating without having a concurrentModificationException
                    ListIterator<MqttDataStructure> iterator = messages_in_progress.listIterator();
                    while (iterator.hasNext())
                    {
                        MqttDataStructure message = iterator.next();
                        if (message.timedOutAfterTick(timeout_tick_length_ms))
                        {
                            // Handle timeout
                            Log.e(TAG, "No Response From Server timer fired. Message = " + message.toString());

                            // Report the lack of response here
                            boolean result = false;
                            String webservice_name = message.server_post_parameters.http_operation_type.name();
                            String payload_json = "";

                            handleWebserviceResponse(result, webservice_name, message.message_unique_id, payload_json);

                            // Remove it - code will auto try again later
                            iterator.remove();

                            server_syncing.resetServerSyncStatusAndTriggerSync();
                        }
                    }
                }
            }
        }, timeout_tick_length_ms, timeout_tick_length_ms);
    }


    private final ResultHandler connection_callback = success -> {
        if (success)
        {
            patient_gateway_interface.handleServerConnectedResult(true);
        }
        else
        {
            server_syncing.resetServerSyncStatusAndTriggerSync();

            post_updateCurrentSessionInformation_NotSendToServer();

            patient_gateway_interface.handleServerConnectedResult(false);
        }
    };


    private final SubscriptionHandler server_subscription_handler = new SubscriptionHandler()
    {
        @Override
        public void onEvent(String topicUri, Object event)
        {
            {
                MqttMessage mqttMessage = (MqttMessage) event;
                try
                {
                    Log.w(TAG, "startMqtt messageArrived : " + topicUri + " : " + mqttMessage.toString());

                    if (topicUri.equals(topic_from_server))
                    {
                        Type type = new TypeToken<MqttDataStructure>()
                        {
                        }.getType();
                        MqttDataStructure from_server = new Gson().fromJson(mqttMessage.toString(), type);

                        Log.w(TAG, "mqtt_callback_extender messageArrived : " + from_server.message_type + " : JSON = " + from_server.json);

                        JsonObject json_object = JsonParser.parseString(from_server.json).getAsJsonObject();

                        switch (from_server.message_type)
                        {
                            case WEBSERVICE_RESPONSE:
                            {
                                boolean result = json_object.get(MqttDataStructure.RESULT).getAsBoolean();
                                String webservice_name = json_object.get(MqttDataStructure.WEBSERVICE_NAME).getAsString();
                                String payload_json = json_object.get(MqttDataStructure.PAYLOAD).getAsString();

                                handleWebserviceResponse(result, webservice_name, from_server.message_unique_id, payload_json);
                            }
                            break;

//                            case SIMULATE_USER_QR_CODE:
//                                patient_gateway_interface.emulateQrCodeUnlockUser();
//                                break;
//                            case SIMULATE_ADMIN_QR_CODE:
//                                patient_gateway_interface.emulateQrCodeUnlockAdmin();
//                                break;
//                            case SIMULATE_FEATURE_ENABLE_QR_CODE:
//                                patient_gateway_interface.emulateQrCodeUnlockFeatureEnable();
//                                break;
//                            case NTP_TIME_SYNC:
//                                patient_gateway_interface.forceNtpTimeSync();
//                                break;
//                            case EXPORT_DB:
//                                patient_gateway_interface.exportDB();
//                                break;
//                            case EMPTY_LOCAL_DATABASE:
//                                patient_gateway_interface.remoteEmptyLocalDatabase();
//                                break;
//                            //case LIFETOUCH_SETUP_MODE_ENABLE:               patient_gateway_interface.startLifetouchSetupModeFromServer();                  break;
//                            //case LIFETOUCH_SETUP_MODE_DISABLE:              patient_gateway_interface.stopLifetouchSetupModeFromServer();                   break;
//                            //case LIFETOUCH_ACCELEROMETER_MODE_ENABLE:       patient_gateway_interface.startLifetouchRawAccelerometerModeFromServer();       break;
//                            //case LIFETOUCH_ACCELEROMETER_MODE_DISABLE:      patient_gateway_interface.stopLifetouchRawAccelerometerModeFromServer();        break;
//                            //case NONIN_SETUP_MODE_ENABLE:                   patient_gateway_interface.startNoninWristOxSetupModeFromServer();               break;
//                            //case NONIN_SETUP_MODE_DISABLE:                  patient_gateway_interface.stopNoninWristOxSetupModeFromServer();                break;
//                            case GET_GATEWAY_STATUS:
//                                patient_gateway_interface.reportGatewayStatusToServer();
//                                break;
//                            case ENABLE_PATIENT_NAME_LOOKUP:
//                                patient_gateway_interface.enablePatientNameLookupFromServer(true);
//                                break;
//                            case DISABLE_PATIENT_NAME_LOOKUP:
//                                patient_gateway_interface.enablePatientNameLookupFromServer(false);
//                                break;
//                            case ENABLE_PERIODIC_SETUP_MODE:
//                                patient_gateway_interface.enablePeriodicSetupModeFromServer(true);
//                                break;
//                            case DISABLE_PERIODIC_SETUP_MODE:
//                                patient_gateway_interface.enablePeriodicSetupModeFromServer(false);
//                                break;
//                            case ENABLE_DUMMY_DATA_MODE:
//                                patient_gateway_interface.enableDummyDataModeFromServer(true);
//                                break;
//                            case DISABLE_DUMMY_DATA_MODE:
//                                patient_gateway_interface.enableDummyDataModeFromServer(false);
//                                break;
//                            case ENABLE_NONIN_TURN_ON_BY_INSERTING_FINGER:
//                                patient_gateway_interface.enableNoninTurnOnByFingerInsertion(true);
//                                break;
//                            case DISABLE_NONIN_TURN_ON_BY_INSERTING_FINGER:
//                                patient_gateway_interface.enableNoninTurnOnByFingerInsertion(false);
//                                break;
//                            case ENABLE_UNPLUGGED_OVERLAY:
//                                patient_gateway_interface.enableUnpluggedOverlay(true);
//                                break;
//                            case DISABLE_UNPLUGGED_OVERLAY:
//                                patient_gateway_interface.enableUnpluggedOverlay(false);
//                                break;
//                            case ENABLE_NIGHT_MODE:
//                                patient_gateway_interface.enableNightModeFromServer(true);
//                                break;
//                            case DISABLE_NIGHT_MODE:
//                                patient_gateway_interface.enableNightModeFromServer(false);
//                                break;
//                            case ENABLE_CSV_OUTPUT:
//                                patient_gateway_interface.enableCsvOutput(true);
//                                break;
//                            case DISABLE_CSV_OUTPUT:
//                                patient_gateway_interface.enableCsvOutput(false);
//                                break;
//                            case ENABLE_SIMPLE_HEART_RATE_ALGORITHM:
//                                patient_gateway_interface.enableSimpleHeartRateAlgorithm(true);
//                                break;
//                            case DISABLE_SIMPLE_HEART_RATE_ALGORITHM:
//                                patient_gateway_interface.enableSimpleHeartRateAlgorithm(false);
//                                break;
//                            case RESET_GATEWAY_AND_UI_RUN_COUNTERS:
//                                patient_gateway_interface.resetGatewayAndUserInterfaceRunCounters();
//                                break;
//                            case RESET_SERVER_SYNC_STATUS:
//                                patient_gateway_interface.resetServerSyncStatusAndTriggerSync();
//                                break;
//                            case GET_DATABASE_SYNC_STATUS:
//                                patient_gateway_interface.reportServerSyncStatusToServer();
//                                break;
//                            case GET_DEVICE_INFOS:
//                                patient_gateway_interface.reportAllDeviceInfoToServer();
//                                break;
//
//                            case GET_FEATURES_ENABLED: {
//                                // ToDo: this goes via pubsub
//                                //                sendFeaturesEnabledStatus();
//                            }
//                            break;
//
//                            case SET_JSON_ARRAY_SIZE: {
//                                setJsonArraySize(json_object);
//                            }
//                            break;
//
//                            case SET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK: {
//                                setNumberOfDummyDataModeMeasurementsPerTick(json_object);
//                            }
//                            break;
//
//                            case SET_SETUP_MODE_TIME_IN_SECONDS: {
//                                setSetupModeTimeInSeconds(json_object);
//                            }
//                            break;
//
//                            case SET_DISPLAY_TIMEOUT_IN_SECONDS: {
//                                setDisplayTimeoutInSeconds(json_object);
//                            }
//                            break;
//
//                            case SET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS: {
//                                setDisplayTimeoutAppliesToPatientVitals(json_object);
//                            }
//                            break;
//
//                            case SET_NUMBER_OF_INVALID_NONIN_INTERMEDIATE_MEASUREMENTS_BEFORE_MINUTE_MARKED_AS_INVALID: {
//                                setNumberOfInvalidNoninWristOxIntermediateMeasurementsBeforeMinuteMarkedAsInvalid(json_object);
//                            }
//                            break;
//
//                            case HANDLE_PATIENT_DETAILS_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP: {
//                                handlePatientNameFromServerLookupOfHospitalPatientId(json_object);
//                            }
//                            break;
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "messageArrived Exception : " + e.toString());
                }
            }
        }
    };


    private void handlePatientNameFromServerLookupOfHospitalPatientId(JsonObject json_object)
    {
        patient_gateway_interface.handlePatientNameFromServerLookupOfHospitalPatientId(json_object);
    }


    private void setNumberOfInvalidNoninWristOxIntermediateMeasurementsBeforeMinuteMarkedAsInvalid(JsonObject json_object)
    {
        patient_gateway_interface.setNumberOfInvalidNoninWristOxIntermediateMeasurementsBeforeMinuteMarkedAsInvalid(json_object.get("number").getAsInt());
    }


    private void setSetupModeTimeInSeconds(JsonObject json_object)
    {
        patient_gateway_interface.setSetupModeTimeInSeconds(json_object.get("setup_mode_time_in_seconds").getAsInt());
    }


    private void setDisplayTimeoutInSeconds(JsonObject json_object)
    {
        patient_gateway_interface.setDisplayTimeoutInSeconds(json_object.get("display_timeout").getAsInt());
    }

    private void setDisplayTimeoutAppliesToPatientVitals(JsonObject json_object)
    {
        patient_gateway_interface.setDisplayTimeoutAppliesToPatientVitals(json_object.get("display_timeout_applies_to_patient_vitals").getAsBoolean());
    }

    private void setNumberOfDummyDataModeMeasurementsPerTick(JsonObject json_object)
    {
        patient_gateway_interface.setNumberOfDummyDataModeMeasurementsPerTick(json_object.get("measurements_per_tick").getAsInt());
    }


    private void setJsonArraySize(JsonObject json_object)
    {
        patient_gateway_interface.setMaxWebserviceJsonArraySize(json_object.get("json_array_size").getAsInt());
    }


    private void handleWebserviceResponse(boolean result, String webservice_name, String unique_id, String payload_json)
    {
        try
        {
            Log.w(TAG, "handleWebserviceResponse webservice_name = " + webservice_name + " : result = " + result + " : payload_json = " + payload_json + " : messages_in_progress size = " + messages_in_progress.size());

            synchronized(messages_in_progress)
            {
                // Using an iterator so we can modify the list while iterating without having a concurrentModificationException
                ListIterator<MqttDataStructure> iterator = messages_in_progress.listIterator();
                while (iterator.hasNext())
                {
                    MqttDataStructure message = iterator.next();

                    if (message.message_unique_id.equals(unique_id))
                    {
                        // remove it from the list as it's about to be processed - want to do this as soon as possible to avoid it timing out during processing.
                        iterator.remove();

                        // Need to spoof a server response error code if the result is false - these are only generated by the HTTP post code
                        // ToDo: get error codes via MQTT, or formalise this
                        ServerResponseErrorCodes error_code;
                        if (result)
                        {
                            // successful result, so no error needed
                            error_code = ServerResponseErrorCodes.NONE;
                        }
                        else
                        {
                            // failure - for now treat it as misc error
                            error_code = ServerResponseErrorCodes.MISC_ERROR;
                        }

                        processServersWebserviceResponse(result, error_code, payload_json, message.server_post_parameters);

                        Log.w(TAG, "messages_in_progress size = " + messages_in_progress.size());
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "handleWebserviceResponse : EXCEPTION : " + e.getMessage());
        }
    }


    private final ArrayList<MqttDataStructure> messages_in_progress = new ArrayList<>();



    @Override
    public void sendToServer(ServerPostParameters params)
    {
        mqttPublishToWebserviceIfConnected(params);
    }


    private void mqttPublishToWebserviceIfConnected(ServerPostParameters server_post_params)
    {
        if (realTimeClient.isConnected())
        {
            Log.d(TAG, "mqtt publish for operation type " + server_post_params.http_operation_type + " : messages_in_progress = " + messages_in_progress.size());

            final MqttDataStructure mqtt_data_structure = new MqttDataStructure(server_post_params, DEFAULT_MESSAGE_TIMEOUT_MS);

            messages_in_progress.add(mqtt_data_structure);

            // Keep track of the "webservice call" in progress.
            synchronized(messages_in_progress)
            {
                messages_in_progress.add(mqtt_data_structure);
            }

            realTimeClient.publish(topic_to_server, mqtt_data_structure.toString());
        }
        else
        {
            Log.e(TAG, "MQTT not connected");
        }
    }


//    @Override
//    public void sendCheckDeviceDetails(int device_type_as_int, long human_readable_device_id)
//    {
//        JsonObject json_object = new JsonObject();
//        json_object.addProperty("DeviceType", device_type_as_int);
//        json_object.addProperty("ByHumanReadableDeviceId", human_readable_device_id);
//
//        mqttPublishToWebserviceIfConnected(new ServerPostParameters(HttpOperationType.CHECK_DEVICE_DETAILS, json_object));
//    }
//
//
//    @Override
//    public void getWardDetailsListFromServer()
//    {
//        mqttPublishToWebserviceIfConnected(new ServerPostParameters(HttpOperationType.WARD_DETAILS_LIST));
//    }
//
//    @Override
//    public void getBedDetailsListFromServer()
//    {
//        mqttPublishToWebserviceIfConnected(new ServerPostParameters(HttpOperationType.BED_DETAILS_LIST));
//    }
//
//    @Override
//    public void getGatewayConfigFromServer()
//    {
//        JsonObject json_object = new JsonObject();
//        json_object.addProperty("ApplicationId", 3);
//
//        mqttPublishToWebserviceIfConnected(new ServerPostParameters(HttpOperationType.GET_GATEWAY_CONFIG, json_object));
//    }
//
//
//    @Override
//    public void getServerConfigurableTextFromServer()
//    {
//        JsonObject json_object_annotation_condition = new JsonObject();
//        json_object_annotation_condition.addProperty("ConfigurableStringType", ServerConfigurableTextStringTypes.ANNOTATION_CONDITION.ordinal());
//        mqttPublishToWebserviceIfConnected(new ServerPostParameters(HttpOperationType.GET_SERVER_CONFIGURABLE_TEXT, json_object_annotation_condition));
//
//        JsonObject json_object_annotation_action = new JsonObject();
//        json_object_annotation_action.addProperty("ConfigurableStringType", ServerConfigurableTextStringTypes.ANNOTATION_ACTION.ordinal());
//        mqttPublishToWebserviceIfConnected(new ServerPostParameters(HttpOperationType.GET_SERVER_CONFIGURABLE_TEXT, json_object_annotation_action));
//
//        JsonObject json_object_annotation_outcome = new JsonObject();
//        json_object_annotation_outcome.addProperty("ConfigurableStringType", ServerConfigurableTextStringTypes.ANNOTATION_OUTCOME.ordinal());
//        mqttPublishToWebserviceIfConnected(new ServerPostParameters(HttpOperationType.GET_SERVER_CONFIGURABLE_TEXT, json_object_annotation_outcome));
//    }
//
//
//    @Override
//    public void sendGetFirmwareBinaryRequest(int servers_id)
//    {
//        JsonObject json_object = new JsonObject();
//        json_object.addProperty("DeviceFirmwareId", servers_id);
//
//        mqttPublishToWebserviceIfConnected(new ServerPostParameters(HttpOperationType.DOWNLOAD_DEVICE_FIRMWARE_BY_ID, json_object));
//    }
//
//
//    @Override
//    public void uploadLogFile(File file)
//    {
//        //ToDo: implement over MQTT
//    }
//
//
//    @Override
//    public void sendGetLatestDeviceFirmwareVersionsRequest()
//    {
//        mqttPublishToWebserviceIfConnected(new ServerPostParameters(HttpOperationType.GET_DEVICE_FIRMWARE_VERSION_LIST));
//    }
//
//
//    @Override
//    public void getMQTTCertificateFromServer()
//    {
//        // ToDo - can't do this over MQTT so need to figure out how to separate essential HTTPS webservices out from MQTT code
//    }
}
