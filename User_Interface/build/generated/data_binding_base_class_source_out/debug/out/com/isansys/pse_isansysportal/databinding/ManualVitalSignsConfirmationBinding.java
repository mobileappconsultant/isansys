// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ManualVitalSignsConfirmationBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonBigButtonBottom;

  @NonNull
  public final Button buttonBigButtonTop;

  @NonNull
  public final LinearLayout linearLayoutManualVitalSignEntryConfirmation;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final TextView textObservationSetTimeAndValidity;

  private ManualVitalSignsConfirmationBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonBigButtonBottom, @NonNull Button buttonBigButtonTop,
      @NonNull LinearLayout linearLayoutManualVitalSignEntryConfirmation,
      @NonNull RecyclerView recyclerView, @NonNull TextView textObservationSetTimeAndValidity) {
    this.rootView = rootView;
    this.buttonBigButtonBottom = buttonBigButtonBottom;
    this.buttonBigButtonTop = buttonBigButtonTop;
    this.linearLayoutManualVitalSignEntryConfirmation = linearLayoutManualVitalSignEntryConfirmation;
    this.recyclerView = recyclerView;
    this.textObservationSetTimeAndValidity = textObservationSetTimeAndValidity;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ManualVitalSignsConfirmationBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ManualVitalSignsConfirmationBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.manual_vital_signs_confirmation, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ManualVitalSignsConfirmationBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonBigButtonBottom;
      Button buttonBigButtonBottom = ViewBindings.findChildViewById(rootView, id);
      if (buttonBigButtonBottom == null) {
        break missingId;
      }

      id = R.id.buttonBigButtonTop;
      Button buttonBigButtonTop = ViewBindings.findChildViewById(rootView, id);
      if (buttonBigButtonTop == null) {
        break missingId;
      }

      id = R.id.linearLayoutManualVitalSignEntryConfirmation;
      LinearLayout linearLayoutManualVitalSignEntryConfirmation = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutManualVitalSignEntryConfirmation == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      RecyclerView recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.textObservationSetTimeAndValidity;
      TextView textObservationSetTimeAndValidity = ViewBindings.findChildViewById(rootView, id);
      if (textObservationSetTimeAndValidity == null) {
        break missingId;
      }

      return new ManualVitalSignsConfirmationBinding((LinearLayout) rootView, buttonBigButtonBottom,
          buttonBigButtonTop, linearLayoutManualVitalSignEntryConfirmation, recyclerView,
          textObservationSetTimeAndValidity);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
