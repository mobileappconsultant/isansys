// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ManualVitalSignsEntrySupplementalOxygenLevelBinding implements ViewBinding {
  @NonNull
  private final GridLayout rootView;

  @NonNull
  public final Button buttonSupplementalOxygenHigh;

  @NonNull
  public final Button buttonSupplementalOxygenLow;

  @NonNull
  public final Button buttonSupplementalOxygenNone;

  @NonNull
  public final TextView textVitalSignValue;

  private ManualVitalSignsEntrySupplementalOxygenLevelBinding(@NonNull GridLayout rootView,
      @NonNull Button buttonSupplementalOxygenHigh, @NonNull Button buttonSupplementalOxygenLow,
      @NonNull Button buttonSupplementalOxygenNone, @NonNull TextView textVitalSignValue) {
    this.rootView = rootView;
    this.buttonSupplementalOxygenHigh = buttonSupplementalOxygenHigh;
    this.buttonSupplementalOxygenLow = buttonSupplementalOxygenLow;
    this.buttonSupplementalOxygenNone = buttonSupplementalOxygenNone;
    this.textVitalSignValue = textVitalSignValue;
  }

  @Override
  @NonNull
  public GridLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ManualVitalSignsEntrySupplementalOxygenLevelBinding inflate(
      @NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ManualVitalSignsEntrySupplementalOxygenLevelBinding inflate(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.manual_vital_signs_entry__supplemental_oxygen_level, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ManualVitalSignsEntrySupplementalOxygenLevelBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonSupplementalOxygenHigh;
      Button buttonSupplementalOxygenHigh = ViewBindings.findChildViewById(rootView, id);
      if (buttonSupplementalOxygenHigh == null) {
        break missingId;
      }

      id = R.id.buttonSupplementalOxygenLow;
      Button buttonSupplementalOxygenLow = ViewBindings.findChildViewById(rootView, id);
      if (buttonSupplementalOxygenLow == null) {
        break missingId;
      }

      id = R.id.buttonSupplementalOxygenNone;
      Button buttonSupplementalOxygenNone = ViewBindings.findChildViewById(rootView, id);
      if (buttonSupplementalOxygenNone == null) {
        break missingId;
      }

      id = R.id.textVitalSignValue;
      TextView textVitalSignValue = ViewBindings.findChildViewById(rootView, id);
      if (textVitalSignValue == null) {
        break missingId;
      }

      return new ManualVitalSignsEntrySupplementalOxygenLevelBinding((GridLayout) rootView,
          buttonSupplementalOxygenHigh, buttonSupplementalOxygenLow, buttonSupplementalOxygenNone,
          textVitalSignValue);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
