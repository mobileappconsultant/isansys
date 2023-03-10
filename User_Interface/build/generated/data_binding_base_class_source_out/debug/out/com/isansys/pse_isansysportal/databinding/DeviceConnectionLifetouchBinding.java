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

public final class DeviceConnectionLifetouchBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonLifetouchCancelSearchOrSearchAgain;

  @NonNull
  public final LinearLayout linearLayoutDeviceConnectionLifetouch;

  @NonNull
  public final ImageView logoLifetouch;

  @NonNull
  public final RoundCornerProgressBar progressBarLifetouchConnection;

  @NonNull
  public final TextView textViewDeviceConnectionLifetouchHumanReadableSerialNumber;

  @NonNull
  public final TextView textViewLifetouch;

  @NonNull
  public final TextView textViewLifetouchSearchStatus;

  private DeviceConnectionLifetouchBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonLifetouchCancelSearchOrSearchAgain,
      @NonNull LinearLayout linearLayoutDeviceConnectionLifetouch, @NonNull ImageView logoLifetouch,
      @NonNull RoundCornerProgressBar progressBarLifetouchConnection,
      @NonNull TextView textViewDeviceConnectionLifetouchHumanReadableSerialNumber,
      @NonNull TextView textViewLifetouch, @NonNull TextView textViewLifetouchSearchStatus) {
    this.rootView = rootView;
    this.buttonLifetouchCancelSearchOrSearchAgain = buttonLifetouchCancelSearchOrSearchAgain;
    this.linearLayoutDeviceConnectionLifetouch = linearLayoutDeviceConnectionLifetouch;
    this.logoLifetouch = logoLifetouch;
    this.progressBarLifetouchConnection = progressBarLifetouchConnection;
    this.textViewDeviceConnectionLifetouchHumanReadableSerialNumber = textViewDeviceConnectionLifetouchHumanReadableSerialNumber;
    this.textViewLifetouch = textViewLifetouch;
    this.textViewLifetouchSearchStatus = textViewLifetouchSearchStatus;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DeviceConnectionLifetouchBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DeviceConnectionLifetouchBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.device_connection__lifetouch, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DeviceConnectionLifetouchBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonLifetouchCancelSearchOrSearchAgain;
      Button buttonLifetouchCancelSearchOrSearchAgain = ViewBindings.findChildViewById(rootView, id);
      if (buttonLifetouchCancelSearchOrSearchAgain == null) {
        break missingId;
      }

      LinearLayout linearLayoutDeviceConnectionLifetouch = (LinearLayout) rootView;

      id = R.id.logo_lifetouch;
      ImageView logoLifetouch = ViewBindings.findChildViewById(rootView, id);
      if (logoLifetouch == null) {
        break missingId;
      }

      id = R.id.progressBarLifetouchConnection;
      RoundCornerProgressBar progressBarLifetouchConnection = ViewBindings.findChildViewById(rootView, id);
      if (progressBarLifetouchConnection == null) {
        break missingId;
      }

      id = R.id.textViewDeviceConnectionLifetouchHumanReadableSerialNumber;
      TextView textViewDeviceConnectionLifetouchHumanReadableSerialNumber = ViewBindings.findChildViewById(rootView, id);
      if (textViewDeviceConnectionLifetouchHumanReadableSerialNumber == null) {
        break missingId;
      }

      id = R.id.textViewLifetouch;
      TextView textViewLifetouch = ViewBindings.findChildViewById(rootView, id);
      if (textViewLifetouch == null) {
        break missingId;
      }

      id = R.id.textViewLifetouchSearchStatus;
      TextView textViewLifetouchSearchStatus = ViewBindings.findChildViewById(rootView, id);
      if (textViewLifetouchSearchStatus == null) {
        break missingId;
      }

      return new DeviceConnectionLifetouchBinding((LinearLayout) rootView,
          buttonLifetouchCancelSearchOrSearchAgain, linearLayoutDeviceConnectionLifetouch,
          logoLifetouch, progressBarLifetouchConnection,
          textViewDeviceConnectionLifetouchHumanReadableSerialNumber, textViewLifetouch,
          textViewLifetouchSearchStatus);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
