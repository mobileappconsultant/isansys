// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DeviceConnectionPulseOxBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonPulseOxCancelSearchOrSearchAgain;

  @NonNull
  public final LinearLayout linearLayoutDeviceConnectionPulseOx;

  @NonNull
  public final ImageView logoPulseOx;

  @NonNull
  public final RoundCornerProgressBar progressBarPulseOxConnection;

  @NonNull
  public final TextView textViewDeviceConnectionPulseOxHumanReadableSerialNumber;

  @NonNull
  public final TextView textViewPulseOx;

  @NonNull
  public final TextView textViewPulseOxSearchStatus;

  private DeviceConnectionPulseOxBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonPulseOxCancelSearchOrSearchAgain,
      @NonNull LinearLayout linearLayoutDeviceConnectionPulseOx, @NonNull ImageView logoPulseOx,
      @NonNull RoundCornerProgressBar progressBarPulseOxConnection,
      @NonNull TextView textViewDeviceConnectionPulseOxHumanReadableSerialNumber,
      @NonNull TextView textViewPulseOx, @NonNull TextView textViewPulseOxSearchStatus) {
    this.rootView = rootView;
    this.buttonPulseOxCancelSearchOrSearchAgain = buttonPulseOxCancelSearchOrSearchAgain;
    this.linearLayoutDeviceConnectionPulseOx = linearLayoutDeviceConnectionPulseOx;
    this.logoPulseOx = logoPulseOx;
    this.progressBarPulseOxConnection = progressBarPulseOxConnection;
    this.textViewDeviceConnectionPulseOxHumanReadableSerialNumber = textViewDeviceConnectionPulseOxHumanReadableSerialNumber;
    this.textViewPulseOx = textViewPulseOx;
    this.textViewPulseOxSearchStatus = textViewPulseOxSearchStatus;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DeviceConnectionPulseOxBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DeviceConnectionPulseOxBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.device_connection__pulse_ox, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DeviceConnectionPulseOxBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonPulseOxCancelSearchOrSearchAgain;
      Button buttonPulseOxCancelSearchOrSearchAgain = ViewBindings.findChildViewById(rootView, id);
      if (buttonPulseOxCancelSearchOrSearchAgain == null) {
        break missingId;
      }

      LinearLayout linearLayoutDeviceConnectionPulseOx = (LinearLayout) rootView;

      id = R.id.logo_pulse_ox;
      ImageView logoPulseOx = ViewBindings.findChildViewById(rootView, id);
      if (logoPulseOx == null) {
        break missingId;
      }

      id = R.id.progressBarPulseOxConnection;
      RoundCornerProgressBar progressBarPulseOxConnection = ViewBindings.findChildViewById(rootView, id);
      if (progressBarPulseOxConnection == null) {
        break missingId;
      }

      id = R.id.textViewDeviceConnectionPulseOxHumanReadableSerialNumber;
      TextView textViewDeviceConnectionPulseOxHumanReadableSerialNumber = ViewBindings.findChildViewById(rootView, id);
      if (textViewDeviceConnectionPulseOxHumanReadableSerialNumber == null) {
        break missingId;
      }

      id = R.id.textViewPulseOx;
      TextView textViewPulseOx = ViewBindings.findChildViewById(rootView, id);
      if (textViewPulseOx == null) {
        break missingId;
      }

      id = R.id.textViewPulseOxSearchStatus;
      TextView textViewPulseOxSearchStatus = ViewBindings.findChildViewById(rootView, id);
      if (textViewPulseOxSearchStatus == null) {
        break missingId;
      }

      return new DeviceConnectionPulseOxBinding((LinearLayout) rootView,
          buttonPulseOxCancelSearchOrSearchAgain, linearLayoutDeviceConnectionPulseOx, logoPulseOx,
          progressBarPulseOxConnection, textViewDeviceConnectionPulseOxHumanReadableSerialNumber,
          textViewPulseOx, textViewPulseOxSearchStatus);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}