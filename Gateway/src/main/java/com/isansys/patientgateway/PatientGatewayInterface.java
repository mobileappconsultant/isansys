package com.isansys.patientgateway;


import com.google.gson.JsonObject;
import com.isansys.common.WebPageDescriptor;
import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.HttpOperationType;
import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.common.enums.DeviceType;
import com.isansys.patientgateway.deviceInfo.DeviceInfo;
import com.isansys.patientgateway.enums.UnlockCodeSource;
import com.isansys.patientgateway.gsm.GsmEventManager;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.patientgateway.serverlink.ServerSyncStatus;
import com.isansys.patientgateway.serverlink.model.GetMQTTCertificateResponse;
import com.isansys.patientgateway.tabletBattery.TabletBatteryInterpreter;
import com.isansys.patientgateway.wifi.WifiEventManager;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

public interface PatientGatewayInterface extends ContextInterface
{
    long getNtpTimeNowInMilliseconds();

    int getAndroidDatabasePatientSessionId();

    ArrayList<Integer> getOpenDeviceSessionIdList();

    void reportDatabaseStatus(ServerSyncStatus server_sync_status);

    boolean isConnectedToNetwork();

    void gsmEventHappened(GsmEventManager.GsmStatus gsm_status);

    void setServersPatientSessionId(int desired_id);

    void reportTabletBatteryInfo(TabletBatteryInterpreter.GatewayBatteryInfo gateway_battery_info, BatteryDataAndEventHandler.TabletBatteryChargingStatus tablet_charging_status);

    void wifiEventHappened(WifiEventManager.WifiStatus wifi_status);

    void handlePingResponse(boolean ping_status, boolean authentication_ok);
    void handleGetEarlyWarningScoresResponse(String json_array_as_string);
    void handleGetDeviceStatusResponse(boolean result, String ward_name, String bed_name, DeviceType device_type, int human_readable_device_id, boolean device_in_use);
    void handleServerInvalidStatusCode(int server_status_code, String server_message);
    void handleServerResponseComplete(boolean success,
                                      HttpOperationType http_operation_type,
                                      ActiveOrOldSession active_or_old_session);
    void handlePatientIdStatusResponse(boolean result, boolean patient_id_in_use);

    void handleReceivedWardList(ArrayList<WardInfo> ward_info_list);
    void handleReceivedBedList(ArrayList<BedInfo> bed_info_list);
    void handleReceivedGatewayConfig(ArrayList<ConfigOption> config_options_list);
    void handleReceivedServerConfigurableText(ArrayList<ServerConfigurableText> server_configurable_text_list);
    void handleReceivedWebPages(ArrayList<WebPageDescriptor> webPageDescriptorArrayList);
    void handleReceivedDeviceFirmwareVersionList(ArrayList<DeviceFirmwareDetails> device_firmware_details_list);
    void handleReceivedFirmwareImage(int servers_id, DeviceType device_type, int firmware_version, byte[]  firmware_image, String filename);

    void uploadLogFileToServer(File file);

    String getWardName();
    String getBedName();
    boolean isBedIdSet();
    String getBedId();

    boolean serverLinkSetupConnectedAndSyncingEnabled();

    String getNotSetYetString();

    void handleServerConnectedResult(boolean connected);
    void reportRealTimeLinkEnableStatus();

    void emulateQrCodeUnlockUser(UnlockCodeSource source);
    void emulateQrCodeUnlockAdmin(UnlockCodeSource source);
    void emulateQrCodeUnlockFeatureEnable(UnlockCodeSource source);
    void forceNtpTimeSync();
    void exportDB();
    void remoteEmptyLocalDatabase();
    void reportGatewayStatusToServer();
    void enablePatientNameLookupFromServer(boolean enabled);
    void enablePeriodicSetupModeFromServer(boolean enabled);
    void enableDummyDataModeFromServer(boolean enabled);
    void enableUnpluggedOverlay(boolean enabled);
    void enableNightModeFromServer(boolean enabled);
    void enableCsvOutput(boolean enabled);
    void enableSimpleHeartRateAlgorithm(boolean enabled);
    void resetGatewayAndUserInterfaceRunCounters();
    void resetServerSyncStatusAndTriggerSync();
    void setMaxWebserviceJsonArraySize(int json_array_size);
    void setNumberOfDummyDataModeMeasurementsPerTick(int measurements_per_tick);
    void setSetupModeTimeInSeconds(int setup_mode_time_in_seconds);
    void setNumberOfInvalidNoninWristOxIntermediateMeasurementsBeforeMinuteMarkedAsInvalid(int number);
    void handlePatientNameFromServerLookupOfHospitalPatientId(JsonObject json_object);
    void setDisplayTimeoutInSeconds(int display_time);
    void setDisplayTimeoutAppliesToPatientVitals(boolean display_timeout_applies_to_patient_vitals);

    void enableDisableServerSyncing(boolean server_sync_enabled);
    void enableDisableRealTimeLink(boolean real_time_link_enabled);

    void reportServerSyncStatusToServer();
    String getAndroidUniqueDeviceId();
    void reportAllDeviceInfoToServer();

    DeviceInfo getDeviceInfoBySensorType(SensorType sensor_type);

    void forceResetWifi();

    boolean inGsmOnlyMode();

    void forceResetBluetooth(boolean delayed, boolean remove_devices);

    void deleteOldFullySyncedSessions();

    void handleMqttCertificate(GetMQTTCertificateResponse certificate_response);

    String getJsonForGatewayPing();

    boolean areWebPagesEnabled();
}
