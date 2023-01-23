package com.isansys.pse_isansysportal;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajit.customseekbar.ProgressItem;
import com.isansys.common.PatientInfo;
import com.isansys.common.ThresholdSet;
import com.isansys.common.ThresholdSetAgeBlockDetail;
import com.isansys.common.VideoCallContact;
import com.isansys.common.WebPageDescriptor;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.RealTimeServer;
import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.deviceInfo.SetupModeLog;
import com.isansys.pse_isansysportal.deviceInfo.DeviceInfo;
import com.isansys.pse_isansysportal.enums.AnnotationEntryType;
import com.isansys.pse_isansysportal.enums.DayOrNightMode;
import com.isansys.remotelogging.RemoteLogging;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public interface MainActivityInterface
{
    void onQrCodeDetected(String source, String qr_code);
    void onDataMatrixDetected(String source, String contents);
    void clearDesiredDeviceInPatientGateway(DeviceType device_type);
    void clearDesiredDeviceInPatientGateway(SensorType sensor_type);

    void getAllDeviceInfosFromGateway();

    boolean isSessionInProgress();
    boolean getEarlyWarningScoresDeviceEnabled();
    void enableEarlyWarningScoreDevice(boolean enabled);
    boolean includeManualVitalSignEntry();
    void manualVitalsOnlySelected();
    boolean useFrontCamera();

    void enableRealTimeLink(boolean enabled);
    void enableServerDataSync(boolean enabled);
    void getServerSyncEnableStatus();
    void getRealTimeLinkEnableStatus();

    String getPatientGatewaySoftwareVersionNumber();
    String getUserInterfaceSoftwareVersionNumber();

    void getGatewaysAssignedBedDetails();
    void setGatewaysAssignedBedDetails(String assigned_bed_id, String assigned_ward_name, String assigned_bed_name);

    void getServerAddress();
    void setServerAddress(String desired_server_address);

    void setServerPort(String desired_server_port);
    void getServerPort();

    void setRealTimeServerPort(String desired_real_time_server_port);
    void getRealTimeServerPort();

    void testServerLink();

    void emptyLocalDatabase();
    void emptyLocalDatabaseIncludingEwsThresholdSets();

    void exportLocalDatabaseToAndroidRoot();
    void deleteOldExportedDatabases();
    void importDatabaseFromAndroidRoot();

    void getWardsAndBedsFromServer();

    void getDefaultEarlyWarningScoreTypesFromServer();

    void useHttpsForServerConnection(boolean useHttps);
    void getHttpsEnableStatus();

    void setWebServiceAuthenticationEnabled(boolean checked);
    void getWebServiceAuthenticationEnabledStatus();

    void setWebServiceEncryptionEnabled(boolean checked);
    void getWebServiceEncryptionEnabledStatus();


    void setDevicePeriodicSetupModeEnabled(boolean checked);
    void getLifetouchPeriodicSamplingStatus();

    void sendTimeSyncCommand();

    void adminModeExitPressed();

    void getSetupModeLengthInSeconds();
    void setSetupModeLengthInSeconds(int length_in_seconds);

    void getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid();
    void setPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid(int number);

    void getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid();
    void setMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid(int number);

    void forceShowOnScreenKeyboard(EditText editText);
    void dismissOnScreenKeyboard(Context context, View view);

    ArrayList<ThresholdSet> getEarlyWarningScoreThresholdSets();

    void showLogCat(boolean enable_value);

    void setUnpluggedOverlayEnableStatus(boolean enabled);
    void getUnpluggedOverlayEnabledStatusFromPatientGateway();

    void setLT3KHzSetupModeEnableStatus(boolean enabled);
    void getLT3KHzSetupModeEnabledStatusFromPatientGateway();

    void setAutoAddEarlyWarningScoreEnableStatus(boolean enabled);
    void getAutoAddEarlyWarningScoreEnableStatusFromPatientGateway();

    void getBloodPressureLongTermMeasurementTimeout();
    void setBloodPressureLongTermMeasurementTimeout(int timeout);

    void getSpO2LongTermMeasurementTimeout();
    void setSpO2LongTermMeasurementTimeout(int timeout);

    void getWeightLongTermMeasurementTimeout();
    void setWeightLongTermMeasurementTimeout(int timeout);

    void getThirdPartyTemperatureLongTermMeasurementTimeout();
    void setThirdPartyTemperatureLongTermMeasurementTimeout(int timeout);

    long getNtpTimeNowInMilliseconds();

    void setPredefinedAnnotationEnableStatus(boolean enabled);
    void getPredefinedAnnotationEnableStatus();

    void setDfuBootloaderEnableStatus(boolean enabled);
    void getDfuBootloaderEnableStatus();

    boolean getShowMacAddressOnStatus();

    boolean getDeviceDummyDataModeEnableStatus(DeviceType device_type);
    boolean getDeviceDummyDataModeEnableStatus(SensorType sensor_type);

    void getDummyDataModeDeviceLeadsOffEnabledStatus(SensorType sensor_type);

    void getDummyDataModeSpoofEarlyWarningScores();

    void createNewDummyDataPatient();

    void addDummyLifetouch(DeviceType device_type);
    void addDummyLifetemp(DeviceType device_type);
    void addDummyPulseOx(DeviceType device_type);
    void addDummyBloodPressure(DeviceType device_type);
    void addDummyWeightScale(DeviceType device_type);

    void removeDummyDevice(SensorType sensor_type);

    void createNewSession();
    void endSessionPressed(long session_end_time);

    PatientInfo getPatientInfo();

    int getRadioButtonNumberFromThresholdSetAgeBlockDetail(ThresholdSetAgeBlockDetail thresholdSetAgeBlockDetail);

    void simulateDeviceLeadsOff(SensorType sensor_type, boolean simulate_leads_off);

    void simulateDeviceConnectionEvent(SensorType sensor_type, boolean connected);

    int getGatewayUserId();

    void setDummyDataModeNumberOfMeasurementsPerTick(int value);
    void getDummyDataModeNumberOfMeasurementsPerTick();

    void resetPatientInfo();
    int getNumberOfDevicesToShow();
    void enableDummyEarlyWarningScoreDevice(boolean enable);
    void spoofEarlyWarningScores(boolean spoof);

    void enableDummyDataModeBackfillSessionWithData(boolean backfill);
    void setDummyDataModeNumberOfHoursToBackfill(int value);

    void PatientAgeSelected(int radio_button_number);
    ThresholdSetAgeBlockDetail getThresholdSetAgeBlockDetails();

    SystemCommands getSystemCommands();

    void endSessionPressed_DisableFooterAndUserInterfaceTimeout();
    void enableFooterBackButtonAndUserInterfaceTimeout();

    void checkAndCancel_timer(Timer mTimer);
    void checkAndCancel_timerTask(TimerTask mTimerTask);

    DeviceInfo getDeviceByType(DeviceType type);
    DeviceInfo getDeviceByType(SensorType sensor_type);

    void setSendTurnOffCommand(boolean turn_off);

    void transferSessionPressed(long session_end_time);
    void showModeSelectionOrGatewayNotConfiguredYet();

    void setDummyServerDetails();

    void storeEnableManuallyEnteredVitalSigns(boolean enable);

    void setCsvOutputEnableCheckedByAdmin(boolean checked);
    boolean getCsvOutputEnableStatus();

    void setEnablePatientOrientation(boolean checked);
    boolean getEnablePatientOrientation();

    void setShowNumbersOnBatteryIndicator(boolean checked);
    boolean getShowNumbersOnBatteryIndicator();

    void setRunDevicesInTestMode(boolean checked);
    boolean getRunDevicesInTestModeEnableStatus();

    void setManufacturingModeEnabled(boolean checked);
    boolean getManufacturingModeEnabled();

    void setVideoCallsEnabled(boolean enabled);
    boolean getVideoCallsEnabled();

    void setUseBackCameraEnabled(boolean checked);
    boolean getUseBackCameraEnabled();

    void setServerPatientNameLookupEnabled(boolean checked);
    void getPatientNameLookupEnabled();

    void setSimpleHeartRateEnabled(boolean checked);
    void getSimpleHeartRateEnabled();

    void setGsmModeOnlyEnabled(boolean checked);
    void getGsmModeOnlyEnabled();

    void getLastNetworkStatus();

    void setLifetempMeasurementInterval(int measurement_interval_in_seconds);
    int getLifetempMeasurementInterval();

    void setDeveloperPopupEnabled(boolean enabled);
    boolean getDeveloperPopupEnabled();

    void setPatientOrientationMeasurementInterval(int measurement_interval_in_seconds);
    int getPatientOrientationMeasurementInterval();

    void setShowIpAddressOnWifiPopupEnabled(boolean enabled);
    boolean getShowIpAddressOnPopupEnabled();

    void getJsonArraySize();
    void setJsonArraySize(int size);

    void setShowMacAddressOnStatus(boolean enable);

    void setUsaMode(boolean enable);
    boolean getUsaMode();

    void radioOff(byte timeTillOff, byte timeOff);

    void lockScreenSelected();

    void restartGatewayApp();

    String getLastPingTime();

    Bundle getGraphColourBands(VitalSignType vital_sign);
    GraphConfigs.GraphConfig getGraphConfig(VitalSignType vital_sign_type);

    ArrayList<ProgressItem> generateVerticalThresholdBars(ArrayList<GraphColourBand> graph_colour_bands, double graph_min, double graph_max);

    long getSessionStartDate();
    long getEarliestCachedTimestamp();

    long getLocalTimeNowInMilliseconds();

    int getGmtOffsetInMilliseconds();
    int getNumberOfGraphTicksFromRange(double range);

    void drawTextBubbleOnGraph(Canvas canvas, Paint paint, float x, float y, String value, int colour);

    void onGraphScroll(long minX, long maxX);
    void onGraphScale(long minX, long maxX);

    int getMaxGraphViewportSizeInMinutes();
    int getDefaultGraphViewportSizeInMinutes();

    ArrayList<? extends MeasurementVitalSign> getCachedMeasurements(VitalSignType vital_sign_type);
    int getMeasurementIntervalInSecondsIfPresent(SensorType sensor_type);

    boolean validateManuallyEnteredVitalSignValue(VitalSignType vital_sign_type, String vital_sign_value);
    void validateEnteredContactId(String enteredText);

    long getSessionStartMilliseconds();

    void startMonitoringPatientPressed();
    void stopMonitoringCurrentPatient();

    void checkDeviceStatusPressed();
    void changeSessionSettingsPressed();
    void patientVitalsDisplaySelected();
    void observationSetEntrySelected();
    void viewManuallyEnteredVitalSignsSelected();
    void enterAnnotationsSelected();

    void checkPackagingSelected();

    void setHospitalPatientId(String desired_patient_id);

    boolean getServerSyncingEnableStatusVariableOnly();
    boolean isServerLookupOfPatientNameFromPatientIdEnabled();

    void getPatientNameFromPatientId(String hospital_patient_id);

    void showNextButton(boolean enabled);
    void refreshDeviceLeadsOffStatus();

    void measurementValidityTimeSelected(VitalSignValidityTimeDescriptor time_selected);

    void annotationTypeSelected(AnnotationEntryType annotation_entry_type);

    void nextButtonPressed();
    void backButtonPressed();
    void showSetupModeBlobs(boolean show);
    boolean getSetupModeBlobsShouldBeShown();
    void lockButtonPressed();
    int getCurrentScreenBrightness();
    int getMinimumBrightnessLevel();
    int getMaximumBrightnessLevel();
    void incrementScreenBrightness();
    void decrementScreenBrightness();
    void footerFragmentLoaded();

    void enableNightMode(boolean enabled);
    DayOrNightMode getNightModeStatus();
    void showSyncStatusPopup();
    void showWifiStatusPopup(boolean allow_reconnect);
    void showDeveloperPopup();
    void sendCommandToGetWifiStatus();
    String reportEarlyWarningScoreType();

    LinkedList<HeartBeatInfo> getHeartBeatList(long start_time, long end_time);

    void removeDeviceFromGateway(DeviceType device_type);
    void removeDeviceFromGateway(SensorType sensor_type);

    void addDevicesSelected();

    boolean getDeviceCurrentlyConnectedByType(SensorType sensor_type);

    int getNumberOfBluetoothDevicesToConnect();
    ArrayList<DeviceInfo> getBluetoothDevicesTypesInUse();

    int getMainFragmentHeight();

    void searchAgainForDevice(DeviceInfo device_info);

    ArrayList<MainActivity.VitalSignAsStrings> getDataForManuallyEnteredVitalSignsDisplayIndividualFragment(VitalSignType vital_sign_type);

    ArrayList<ManuallyEnteredVitalSignDescriptor> getManuallyEnteredVitalSigns();

    ArrayList<ManualVitalSignButtonDescriptor> getManualVitalSignButtons(int vital_sign_id);
    void observationSetMeasurementSelected(ManuallyEnteredVitalSignDescriptor vital_sign_selected);

    void observationSetMeasurementValueEntered(int vital_sign_id, int button_id);
    void observationSetMeasurementValueEntered(int vital_sign_id, String measurement_value);

    ArrayList<VitalSignValidityTimeDescriptor> getVitalSignValidityTimes();

    ArrayList<DeviceInfo> getDeviceTypesInUse();

    boolean isPoorSignalInLastMinute();
    boolean hasNoBeatsDetectedTimerFired();

    int getMeasurementValidityTimeInSeconds(VitalSignType vital_sign_type);

    boolean isSavedMeasurementValid(VitalSignType vital_sign_type);
    MeasurementVitalSign getSavedMeasurement(VitalSignType vital_sign_type);
    void setSavedMeasurement(VitalSignType vital_sign_type, MeasurementVitalSign measurement);

    RemoteLogging getMainActivityLogger();

    void showLifetouchSetupMode(boolean show);
    void showLifetouchPoincare(boolean show);
    void showLifetouchRawAccelerometer(boolean show);

    void setShowLifetouchActivityLevel(boolean enable);
    boolean getShowLifetouchActivityLevel();

    boolean installGatewayApk();
    boolean installUserInterfaceApk();

    void setAutoResumeEnabled(boolean enable);
    boolean getAutoResumeEnabled();

    void setEnableAutoLogFileUploadToServer(boolean checked);
    boolean getEnableAutoLogFileUploadToServer();

    String getDeviceNameByType(DeviceType device_type);
    String getDeviceNameFromSensorType(SensorType sensorType);

    void getGatewayConfigFromServer();
    void getServerConfigurableTextFromServer();

    boolean isGatewaySetupComplete();
    void showInstallationServerAddressScanFragment();
    void restartSetupWizard();
    void installationProcessComplete();
    void forceInstallationComplete();
    boolean isConnectedToNetwork();
    void turnOffRealTimeStreaming();

    void forceCheckForLatestFirmwareImagesFromServer();

    void showHistoricalSetupModeViewerPopup(double start_timestamp, VitalSignType graph_vital_sign_type);
    void getBulkSetupModeData(SetupModeLog item);
    int getHistoricalSetupModeViewViewportSize();
    void increaseHistoricalSetupModeViewportSize();
    void decreaseHistoricalSetupModeViewportSize();

    void touchEventSoResetTimers();

    Context getAppContext();
    Activity getActivity();

    void showAnnotationPopup(double timestamp);

    boolean getGsmOnlyModeFeatureEnabled();

    float getFooterButtonTextSizeForString(String string);

    int getMatchingManuallyEnteredSystolicBloodPressureMeasurementFromTimestamp(long timestamp_in_ms);
    int getMatchingManuallyEnteredDiastolicBloodPressureMeasurementFromTimestamp(long timestamp_in_ms);

    void showPoincarePopup();
    void closePoincarePopupIfShowing();

    void getDevicePeriodicModePeriodTimeInSeconds();
    void setDevicePeriodicModePeriodTimeInSeconds(int time_in_seconds);
    void getDevicePeriodicModeActiveTimeInSeconds();
    void setDevicePeriodicModeActiveTimeInSeconds(int time_in_seconds);

    void getDisplayTimeoutLengthInSeconds();
    void setDisplayTimeoutLengthInSeconds(int time_in_seconds);
    void getDisplayTimeoutAppliesToPatientVitalsDisplay();
    void setDisplayTimeoutAppliesToPatientVitalsDisplay(boolean applies);

    ArrayList<SetupModeLog> getSetupModeLog(SensorType sensor_type);

    ArrayList<AnnotationDescriptor> getAnnotationConditions();
    ArrayList<AnnotationDescriptor> getAnnotationActions();
    ArrayList<AnnotationDescriptor> getAnnotationOutcomes();

    void annotationConditionsSelected(String selected);
    void annotationActionsSelected(String selected);
    void annotationOutcomesSelected(String selected);

    void annotationEnteredViaKeyboard(String annotation);

    DeviceInfo getDeviceInfoForVitalSign(VitalSignType vital_sign_type);
    SensorType getSensorTypeForVitalSign(VitalSignType vital_sign_type);

    VitalSignType getVitalSignTypeForDeviceInfo(DeviceInfo device_info);

    boolean isScreenLandscape();

    void setAnnotationsTimestamp(long timestamp);
    AnnotationBeingEntered getAnnotationBeingEntered();
    void storeAnnotation();

    boolean showRightHandSideDescriptionForObservationEntry(int vital_sign_id);
    String updateScreenBasedOnManualVitalSignType(int vital_sign_id);
    String getObservationSetVitalSignSelectionTopText();
    void removeManualVitalSignFromObservationSet(ManuallyEnteredVitalSignDescriptor vital_sign_selected);
    ManualVitalSignBeingEntered getManualVitalSignBeingEntered(int vital_sign_id);
    void storeObservationSet();
    MeasurementSetBeingEntered getObservationSetEntered();

    void sendStartSetupModeCommand(DeviceInfo device_info);
    void sendStopSetupModeCommand(DeviceInfo device_info);

    void setWifiLoggingEnabled(boolean enable);
    boolean getWifiLoggingEnabled();

    void setGsmLoggingEnabled(boolean enable);
    boolean getGsmLoggingEnabled();

    void setDatabaseLoggingEnabled(boolean enable);
    boolean getDatabaseLoggingEnabled();

    void setServerLoggingEnabled(boolean enable);
    boolean getServerLoggingEnabled();

    void setBatteryLoggingEnabled(boolean enable);
    boolean getBatteryLoggingEnabled();

    void hideProgress();

    String getGraphLabelForManualVitalSigns(VitalSignType vital_sign_type, double x, double y);

    void forceDismissPatientNamePopup();
    void forceDismissRecyclingReminderPopup();

    <T extends MeasurementVitalSign> String formatMeasurementForDisplay(T generic_measurement);

    boolean isMqttSelected();

    ArrayList<DeviceInfo> getDeviceInfoList();

    ArrayList<VitalSignType> getListOfManualVitalSigns();
    boolean isVitalSignTypeAManualVital(VitalSignType vital_sign_type);

    ThresholdSetAgeBlockDetail getThresholdSetAgeBlockDetailIdForAdults();

    boolean stopUiFastUpdates();

    boolean isTabletGsm();

    String removeLeadingCharacters(String inputString, char characterToRemove);
    String removeTrailingCharacters(String inputString, char characterToRemove);

    void enterUpdateMode();
    void softwareUpdateComplete();
    boolean isSoftwareUpdateAvailable();

    Drawable getDrawableCircle(int ews_score);

    void startConnectionForUnbondedDevice();

    void requestOverlayPermission();
    void requestWriteSettingsPermission();
    void requestCameraPermission();
    void requestRecordAudioPermission();
    void requestWriteExternalStoragePermission();
    void requestAccessNotificationPolicyPermission();
    void requestInstallPackagesPermission();
    void triggerApplicationRestart();

    void setSpO2SpotMeasurementsEnableStatus(boolean enabled);
    void getSpO2SpotMeasurementsEnableStatus();

    void videoCallModeSelectionSelected();
    void videoCallContactsSelected();
    void videoCallContactsSelectedFromUnlockPage();
    void scheduleVideoCallSelected();

    void requestPatientsVideoCallContactsFromServer();

    void patientRequestedVideoCall(ArrayList<VideoCallContact> contacts);

    boolean haveCamera();
    void dummyUnlockCode();
    void dummyAdminCode();

    void setRealTimeClientType(RealTimeServer type);
    RealTimeServer getRealTimeClientType();

    void dismissWifiPopupIfVisible();

    void setDisplayTemperatureInFahrenheitEnableStatus(boolean checked);
    void getDisplayTemperatureInFahrenheitEnableStatus();
    boolean isShowTemperatureInFahrenheitEnabled();

    void setDisplayWeightInLbsEnableStatus(boolean checked);
    void getDisplayWeightInLbsEnableStatus();
    boolean isShowWeightInLbsEnabled();

    void beep();

    void pulseImage(ImageView image);

    void showOnScreenMessage(String message);

    boolean inNonCeMode();

    void webpageSelectionSelected();
    ArrayList<WebPageButtonDescriptor> getWebPageButtons();
    void showWebpagePopup(String url);

    void setViewWebPagesEnabled(boolean enabled);
    boolean getViewWebPagesEnabled();

    void getViewableWebPagesFromServer();

    void removeDeviceFromGatewayAndRemoveFromEwsIfRequested(SensorType sensor_type);

    void highlightFirstWordInTextView(TextView textView);

    boolean isScreensaverEnabled();
    void showScreensaver();
    void screensaverDismissed();
}
