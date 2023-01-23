// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FeatureEnableBinding implements ViewBinding {
  @NonNull
  private final TableLayout rootView;

  @NonNull
  public final Button buttonImportDatabase;

  @NonNull
  public final Button buttonSetDummyServer;

  @NonNull
  public final Button buttonUnitRadioOff;

  @NonNull
  public final CheckBox checkBoxDisplayPatientOrientation;

  @NonNull
  public final CheckBox checkBoxDisplayTemperatureInFahrenheit;

  @NonNull
  public final CheckBox checkBoxDisplayWeightInLbs;

  @NonNull
  public final CheckBox checkBoxEnableAutoLogFileUploadToServer;

  @NonNull
  public final CheckBox checkBoxEnableAutoResume;

  @NonNull
  public final CheckBox checkBoxEnableBackCamera;

  @NonNull
  public final CheckBox checkBoxEnableBatteryLogging;

  @NonNull
  public final CheckBox checkBoxEnableCsvOutput;

  @NonNull
  public final CheckBox checkBoxEnableDatabaseLogging;

  @NonNull
  public final CheckBox checkBoxEnableDeveloperPopup;

  @NonNull
  public final CheckBox checkBoxEnableGsmLogging;

  @NonNull
  public final CheckBox checkBoxEnableManuallyEnteredVitalSigns;

  @NonNull
  public final CheckBox checkBoxEnableManufacturingMode;

  @NonNull
  public final CheckBox checkBoxEnableServerLogging;

  @NonNull
  public final CheckBox checkBoxEnableVideoCalls;

  @NonNull
  public final CheckBox checkBoxEnableViewWebPages;

  @NonNull
  public final CheckBox checkBoxEnableWifiLogging;

  @NonNull
  public final CheckBox checkBoxRunDevicesInTestMode;

  @NonNull
  public final CheckBox checkBoxShowIpAddressOnWifiPopup;

  @NonNull
  public final CheckBox checkBoxShowLifetouchActivityLevel;

  @NonNull
  public final CheckBox checkBoxShowMacAddressOnDeviceStatusScreen;

  @NonNull
  public final CheckBox checkBoxShowNumbersOnBatteryIndicator;

  @NonNull
  public final CheckBox checkBoxSimpleHeartRate;

  @NonNull
  public final CheckBox checkBoxUsaMode;

  @NonNull
  public final TextView labelJsonArraySize;

  @NonNull
  public final TextView labelLifetempMeasurementInterval;

  @NonNull
  public final TextView labelPatientOrientationMeasurementInterval;

  @NonNull
  public final TextView labelRealTimeClientType;

  @NonNull
  public final Spinner spinnerJsonArraySize;

  @NonNull
  public final Spinner spinnerLifetempMeasurementInterval;

  @NonNull
  public final Spinner spinnerPatientOrientationMeasurementInterval;

  @NonNull
  public final Spinner spinnerRealTimeClientType;

  private FeatureEnableBinding(@NonNull TableLayout rootView, @NonNull Button buttonImportDatabase,
      @NonNull Button buttonSetDummyServer, @NonNull Button buttonUnitRadioOff,
      @NonNull CheckBox checkBoxDisplayPatientOrientation,
      @NonNull CheckBox checkBoxDisplayTemperatureInFahrenheit,
      @NonNull CheckBox checkBoxDisplayWeightInLbs,
      @NonNull CheckBox checkBoxEnableAutoLogFileUploadToServer,
      @NonNull CheckBox checkBoxEnableAutoResume, @NonNull CheckBox checkBoxEnableBackCamera,
      @NonNull CheckBox checkBoxEnableBatteryLogging, @NonNull CheckBox checkBoxEnableCsvOutput,
      @NonNull CheckBox checkBoxEnableDatabaseLogging,
      @NonNull CheckBox checkBoxEnableDeveloperPopup, @NonNull CheckBox checkBoxEnableGsmLogging,
      @NonNull CheckBox checkBoxEnableManuallyEnteredVitalSigns,
      @NonNull CheckBox checkBoxEnableManufacturingMode,
      @NonNull CheckBox checkBoxEnableServerLogging, @NonNull CheckBox checkBoxEnableVideoCalls,
      @NonNull CheckBox checkBoxEnableViewWebPages, @NonNull CheckBox checkBoxEnableWifiLogging,
      @NonNull CheckBox checkBoxRunDevicesInTestMode,
      @NonNull CheckBox checkBoxShowIpAddressOnWifiPopup,
      @NonNull CheckBox checkBoxShowLifetouchActivityLevel,
      @NonNull CheckBox checkBoxShowMacAddressOnDeviceStatusScreen,
      @NonNull CheckBox checkBoxShowNumbersOnBatteryIndicator,
      @NonNull CheckBox checkBoxSimpleHeartRate, @NonNull CheckBox checkBoxUsaMode,
      @NonNull TextView labelJsonArraySize, @NonNull TextView labelLifetempMeasurementInterval,
      @NonNull TextView labelPatientOrientationMeasurementInterval,
      @NonNull TextView labelRealTimeClientType, @NonNull Spinner spinnerJsonArraySize,
      @NonNull Spinner spinnerLifetempMeasurementInterval,
      @NonNull Spinner spinnerPatientOrientationMeasurementInterval,
      @NonNull Spinner spinnerRealTimeClientType) {
    this.rootView = rootView;
    this.buttonImportDatabase = buttonImportDatabase;
    this.buttonSetDummyServer = buttonSetDummyServer;
    this.buttonUnitRadioOff = buttonUnitRadioOff;
    this.checkBoxDisplayPatientOrientation = checkBoxDisplayPatientOrientation;
    this.checkBoxDisplayTemperatureInFahrenheit = checkBoxDisplayTemperatureInFahrenheit;
    this.checkBoxDisplayWeightInLbs = checkBoxDisplayWeightInLbs;
    this.checkBoxEnableAutoLogFileUploadToServer = checkBoxEnableAutoLogFileUploadToServer;
    this.checkBoxEnableAutoResume = checkBoxEnableAutoResume;
    this.checkBoxEnableBackCamera = checkBoxEnableBackCamera;
    this.checkBoxEnableBatteryLogging = checkBoxEnableBatteryLogging;
    this.checkBoxEnableCsvOutput = checkBoxEnableCsvOutput;
    this.checkBoxEnableDatabaseLogging = checkBoxEnableDatabaseLogging;
    this.checkBoxEnableDeveloperPopup = checkBoxEnableDeveloperPopup;
    this.checkBoxEnableGsmLogging = checkBoxEnableGsmLogging;
    this.checkBoxEnableManuallyEnteredVitalSigns = checkBoxEnableManuallyEnteredVitalSigns;
    this.checkBoxEnableManufacturingMode = checkBoxEnableManufacturingMode;
    this.checkBoxEnableServerLogging = checkBoxEnableServerLogging;
    this.checkBoxEnableVideoCalls = checkBoxEnableVideoCalls;
    this.checkBoxEnableViewWebPages = checkBoxEnableViewWebPages;
    this.checkBoxEnableWifiLogging = checkBoxEnableWifiLogging;
    this.checkBoxRunDevicesInTestMode = checkBoxRunDevicesInTestMode;
    this.checkBoxShowIpAddressOnWifiPopup = checkBoxShowIpAddressOnWifiPopup;
    this.checkBoxShowLifetouchActivityLevel = checkBoxShowLifetouchActivityLevel;
    this.checkBoxShowMacAddressOnDeviceStatusScreen = checkBoxShowMacAddressOnDeviceStatusScreen;
    this.checkBoxShowNumbersOnBatteryIndicator = checkBoxShowNumbersOnBatteryIndicator;
    this.checkBoxSimpleHeartRate = checkBoxSimpleHeartRate;
    this.checkBoxUsaMode = checkBoxUsaMode;
    this.labelJsonArraySize = labelJsonArraySize;
    this.labelLifetempMeasurementInterval = labelLifetempMeasurementInterval;
    this.labelPatientOrientationMeasurementInterval = labelPatientOrientationMeasurementInterval;
    this.labelRealTimeClientType = labelRealTimeClientType;
    this.spinnerJsonArraySize = spinnerJsonArraySize;
    this.spinnerLifetempMeasurementInterval = spinnerLifetempMeasurementInterval;
    this.spinnerPatientOrientationMeasurementInterval = spinnerPatientOrientationMeasurementInterval;
    this.spinnerRealTimeClientType = spinnerRealTimeClientType;
  }

  @Override
  @NonNull
  public TableLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FeatureEnableBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FeatureEnableBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.feature_enable, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FeatureEnableBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonImportDatabase;
      Button buttonImportDatabase = ViewBindings.findChildViewById(rootView, id);
      if (buttonImportDatabase == null) {
        break missingId;
      }

      id = R.id.buttonSetDummyServer;
      Button buttonSetDummyServer = ViewBindings.findChildViewById(rootView, id);
      if (buttonSetDummyServer == null) {
        break missingId;
      }

      id = R.id.buttonUnitRadioOff;
      Button buttonUnitRadioOff = ViewBindings.findChildViewById(rootView, id);
      if (buttonUnitRadioOff == null) {
        break missingId;
      }

      id = R.id.checkBoxDisplayPatientOrientation;
      CheckBox checkBoxDisplayPatientOrientation = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxDisplayPatientOrientation == null) {
        break missingId;
      }

      id = R.id.checkBoxDisplayTemperatureInFahrenheit;
      CheckBox checkBoxDisplayTemperatureInFahrenheit = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxDisplayTemperatureInFahrenheit == null) {
        break missingId;
      }

      id = R.id.checkBoxDisplayWeightInLbs;
      CheckBox checkBoxDisplayWeightInLbs = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxDisplayWeightInLbs == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableAutoLogFileUploadToServer;
      CheckBox checkBoxEnableAutoLogFileUploadToServer = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableAutoLogFileUploadToServer == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableAutoResume;
      CheckBox checkBoxEnableAutoResume = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableAutoResume == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableBackCamera;
      CheckBox checkBoxEnableBackCamera = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableBackCamera == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableBatteryLogging;
      CheckBox checkBoxEnableBatteryLogging = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableBatteryLogging == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableCsvOutput;
      CheckBox checkBoxEnableCsvOutput = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableCsvOutput == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableDatabaseLogging;
      CheckBox checkBoxEnableDatabaseLogging = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableDatabaseLogging == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableDeveloperPopup;
      CheckBox checkBoxEnableDeveloperPopup = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableDeveloperPopup == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableGsmLogging;
      CheckBox checkBoxEnableGsmLogging = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableGsmLogging == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableManuallyEnteredVitalSigns;
      CheckBox checkBoxEnableManuallyEnteredVitalSigns = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableManuallyEnteredVitalSigns == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableManufacturingMode;
      CheckBox checkBoxEnableManufacturingMode = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableManufacturingMode == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableServerLogging;
      CheckBox checkBoxEnableServerLogging = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableServerLogging == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableVideoCalls;
      CheckBox checkBoxEnableVideoCalls = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableVideoCalls == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableViewWebPages;
      CheckBox checkBoxEnableViewWebPages = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableViewWebPages == null) {
        break missingId;
      }

      id = R.id.checkBoxEnableWifiLogging;
      CheckBox checkBoxEnableWifiLogging = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxEnableWifiLogging == null) {
        break missingId;
      }

      id = R.id.checkBoxRunDevicesInTestMode;
      CheckBox checkBoxRunDevicesInTestMode = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxRunDevicesInTestMode == null) {
        break missingId;
      }

      id = R.id.checkBoxShowIpAddressOnWifiPopup;
      CheckBox checkBoxShowIpAddressOnWifiPopup = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxShowIpAddressOnWifiPopup == null) {
        break missingId;
      }

      id = R.id.checkBoxShowLifetouchActivityLevel;
      CheckBox checkBoxShowLifetouchActivityLevel = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxShowLifetouchActivityLevel == null) {
        break missingId;
      }

      id = R.id.checkBoxShowMacAddressOnDeviceStatusScreen;
      CheckBox checkBoxShowMacAddressOnDeviceStatusScreen = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxShowMacAddressOnDeviceStatusScreen == null) {
        break missingId;
      }

      id = R.id.checkBoxShowNumbersOnBatteryIndicator;
      CheckBox checkBoxShowNumbersOnBatteryIndicator = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxShowNumbersOnBatteryIndicator == null) {
        break missingId;
      }

      id = R.id.checkBoxSimpleHeartRate;
      CheckBox checkBoxSimpleHeartRate = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxSimpleHeartRate == null) {
        break missingId;
      }

      id = R.id.checkBoxUsaMode;
      CheckBox checkBoxUsaMode = ViewBindings.findChildViewById(rootView, id);
      if (checkBoxUsaMode == null) {
        break missingId;
      }

      id = R.id.labelJsonArraySize;
      TextView labelJsonArraySize = ViewBindings.findChildViewById(rootView, id);
      if (labelJsonArraySize == null) {
        break missingId;
      }

      id = R.id.labelLifetempMeasurementInterval;
      TextView labelLifetempMeasurementInterval = ViewBindings.findChildViewById(rootView, id);
      if (labelLifetempMeasurementInterval == null) {
        break missingId;
      }

      id = R.id.labelPatientOrientationMeasurementInterval;
      TextView labelPatientOrientationMeasurementInterval = ViewBindings.findChildViewById(rootView, id);
      if (labelPatientOrientationMeasurementInterval == null) {
        break missingId;
      }

      id = R.id.labelRealTimeClientType;
      TextView labelRealTimeClientType = ViewBindings.findChildViewById(rootView, id);
      if (labelRealTimeClientType == null) {
        break missingId;
      }

      id = R.id.spinnerJsonArraySize;
      Spinner spinnerJsonArraySize = ViewBindings.findChildViewById(rootView, id);
      if (spinnerJsonArraySize == null) {
        break missingId;
      }

      id = R.id.spinnerLifetempMeasurementInterval;
      Spinner spinnerLifetempMeasurementInterval = ViewBindings.findChildViewById(rootView, id);
      if (spinnerLifetempMeasurementInterval == null) {
        break missingId;
      }

      id = R.id.spinnerPatientOrientationMeasurementInterval;
      Spinner spinnerPatientOrientationMeasurementInterval = ViewBindings.findChildViewById(rootView, id);
      if (spinnerPatientOrientationMeasurementInterval == null) {
        break missingId;
      }

      id = R.id.spinnerRealTimeClientType;
      Spinner spinnerRealTimeClientType = ViewBindings.findChildViewById(rootView, id);
      if (spinnerRealTimeClientType == null) {
        break missingId;
      }

      return new FeatureEnableBinding((TableLayout) rootView, buttonImportDatabase,
          buttonSetDummyServer, buttonUnitRadioOff, checkBoxDisplayPatientOrientation,
          checkBoxDisplayTemperatureInFahrenheit, checkBoxDisplayWeightInLbs,
          checkBoxEnableAutoLogFileUploadToServer, checkBoxEnableAutoResume,
          checkBoxEnableBackCamera, checkBoxEnableBatteryLogging, checkBoxEnableCsvOutput,
          checkBoxEnableDatabaseLogging, checkBoxEnableDeveloperPopup, checkBoxEnableGsmLogging,
          checkBoxEnableManuallyEnteredVitalSigns, checkBoxEnableManufacturingMode,
          checkBoxEnableServerLogging, checkBoxEnableVideoCalls, checkBoxEnableViewWebPages,
          checkBoxEnableWifiLogging, checkBoxRunDevicesInTestMode, checkBoxShowIpAddressOnWifiPopup,
          checkBoxShowLifetouchActivityLevel, checkBoxShowMacAddressOnDeviceStatusScreen,
          checkBoxShowNumbersOnBatteryIndicator, checkBoxSimpleHeartRate, checkBoxUsaMode,
          labelJsonArraySize, labelLifetempMeasurementInterval,
          labelPatientOrientationMeasurementInterval, labelRealTimeClientType, spinnerJsonArraySize,
          spinnerLifetempMeasurementInterval, spinnerPatientOrientationMeasurementInterval,
          spinnerRealTimeClientType);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}