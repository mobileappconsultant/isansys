// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ChangeSessionSettingsLifetempBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView LifetempChangeSessionDisconnectTextView;

  @NonNull
  public final LinearLayout LinearLayoutProgressBarLifetempChangeSessionDisconnect;

  @NonNull
  public final LinearLayout LinearLayoutTextViewLifetempDisconnectedWarning;

  @NonNull
  public final RelativeLayout RelativeLayout1;

  @NonNull
  public final Button buttonAddLifetemp;

  @NonNull
  public final Button buttonRemoveThermometer;

  @NonNull
  public final LinearLayout linearLayoutChangeSessionSettingsLifetemp;

  @NonNull
  public final ImageView logoLifetemp;

  @NonNull
  public final ProgressBar progressBarLifetempChangeSessionDisconnect;

  @NonNull
  public final TextView textViewChangeSessionSettingsLifetempFirmwareVersion;

  @NonNull
  public final TextView textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber;

  @NonNull
  public final TextView textViewLifetemp;

  @NonNull
  public final TextView textViewLifetempDisconnectedWarning;

  private ChangeSessionSettingsLifetempBinding(@NonNull LinearLayout rootView,
      @NonNull TextView LifetempChangeSessionDisconnectTextView,
      @NonNull LinearLayout LinearLayoutProgressBarLifetempChangeSessionDisconnect,
      @NonNull LinearLayout LinearLayoutTextViewLifetempDisconnectedWarning,
      @NonNull RelativeLayout RelativeLayout1, @NonNull Button buttonAddLifetemp,
      @NonNull Button buttonRemoveThermometer,
      @NonNull LinearLayout linearLayoutChangeSessionSettingsLifetemp,
      @NonNull ImageView logoLifetemp,
      @NonNull ProgressBar progressBarLifetempChangeSessionDisconnect,
      @NonNull TextView textViewChangeSessionSettingsLifetempFirmwareVersion,
      @NonNull TextView textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber,
      @NonNull TextView textViewLifetemp, @NonNull TextView textViewLifetempDisconnectedWarning) {
    this.rootView = rootView;
    this.LifetempChangeSessionDisconnectTextView = LifetempChangeSessionDisconnectTextView;
    this.LinearLayoutProgressBarLifetempChangeSessionDisconnect = LinearLayoutProgressBarLifetempChangeSessionDisconnect;
    this.LinearLayoutTextViewLifetempDisconnectedWarning = LinearLayoutTextViewLifetempDisconnectedWarning;
    this.RelativeLayout1 = RelativeLayout1;
    this.buttonAddLifetemp = buttonAddLifetemp;
    this.buttonRemoveThermometer = buttonRemoveThermometer;
    this.linearLayoutChangeSessionSettingsLifetemp = linearLayoutChangeSessionSettingsLifetemp;
    this.logoLifetemp = logoLifetemp;
    this.progressBarLifetempChangeSessionDisconnect = progressBarLifetempChangeSessionDisconnect;
    this.textViewChangeSessionSettingsLifetempFirmwareVersion = textViewChangeSessionSettingsLifetempFirmwareVersion;
    this.textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber = textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber;
    this.textViewLifetemp = textViewLifetemp;
    this.textViewLifetempDisconnectedWarning = textViewLifetempDisconnectedWarning;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ChangeSessionSettingsLifetempBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ChangeSessionSettingsLifetempBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.change_session_settings__lifetemp, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ChangeSessionSettingsLifetempBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.LifetempChangeSessionDisconnectTextView;
      TextView LifetempChangeSessionDisconnectTextView = ViewBindings.findChildViewById(rootView, id);
      if (LifetempChangeSessionDisconnectTextView == null) {
        break missingId;
      }

      id = R.id.LinearLayout_progressBarLifetempChangeSessionDisconnect;
      LinearLayout LinearLayoutProgressBarLifetempChangeSessionDisconnect = ViewBindings.findChildViewById(rootView, id);
      if (LinearLayoutProgressBarLifetempChangeSessionDisconnect == null) {
        break missingId;
      }

      id = R.id.LinearLayout_textViewLifetempDisconnectedWarning;
      LinearLayout LinearLayoutTextViewLifetempDisconnectedWarning = ViewBindings.findChildViewById(rootView, id);
      if (LinearLayoutTextViewLifetempDisconnectedWarning == null) {
        break missingId;
      }

      id = R.id.RelativeLayout1;
      RelativeLayout RelativeLayout1 = ViewBindings.findChildViewById(rootView, id);
      if (RelativeLayout1 == null) {
        break missingId;
      }

      id = R.id.buttonAddLifetemp;
      Button buttonAddLifetemp = ViewBindings.findChildViewById(rootView, id);
      if (buttonAddLifetemp == null) {
        break missingId;
      }

      id = R.id.buttonRemoveThermometer;
      Button buttonRemoveThermometer = ViewBindings.findChildViewById(rootView, id);
      if (buttonRemoveThermometer == null) {
        break missingId;
      }

      LinearLayout linearLayoutChangeSessionSettingsLifetemp = (LinearLayout) rootView;

      id = R.id.logo_lifetemp;
      ImageView logoLifetemp = ViewBindings.findChildViewById(rootView, id);
      if (logoLifetemp == null) {
        break missingId;
      }

      id = R.id.progressBarLifetempChangeSessionDisconnect;
      ProgressBar progressBarLifetempChangeSessionDisconnect = ViewBindings.findChildViewById(rootView, id);
      if (progressBarLifetempChangeSessionDisconnect == null) {
        break missingId;
      }

      id = R.id.textViewChangeSessionSettingsLifetempFirmwareVersion;
      TextView textViewChangeSessionSettingsLifetempFirmwareVersion = ViewBindings.findChildViewById(rootView, id);
      if (textViewChangeSessionSettingsLifetempFirmwareVersion == null) {
        break missingId;
      }

      id = R.id.textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber;
      TextView textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber = ViewBindings.findChildViewById(rootView, id);
      if (textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber == null) {
        break missingId;
      }

      id = R.id.textViewLifetemp;
      TextView textViewLifetemp = ViewBindings.findChildViewById(rootView, id);
      if (textViewLifetemp == null) {
        break missingId;
      }

      id = R.id.textView_lifetempDisconnectedWarning;
      TextView textViewLifetempDisconnectedWarning = ViewBindings.findChildViewById(rootView, id);
      if (textViewLifetempDisconnectedWarning == null) {
        break missingId;
      }

      return new ChangeSessionSettingsLifetempBinding((LinearLayout) rootView,
          LifetempChangeSessionDisconnectTextView,
          LinearLayoutProgressBarLifetempChangeSessionDisconnect,
          LinearLayoutTextViewLifetempDisconnectedWarning, RelativeLayout1, buttonAddLifetemp,
          buttonRemoveThermometer, linearLayoutChangeSessionSettingsLifetemp, logoLifetemp,
          progressBarLifetempChangeSessionDisconnect,
          textViewChangeSessionSettingsLifetempFirmwareVersion,
          textViewDeviceSpecificSettingsLifetempHumanReadableSerialNumber, textViewLifetemp,
          textViewLifetempDisconnectedWarning);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
