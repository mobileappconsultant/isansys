// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public final class ManualVitalSignsEntryFamilyOrNurseConcernBinding implements ViewBinding {
  @NonNull
  private final TableLayout rootView;

  @NonNull
  public final Button buttonFamilyOrNurseConcernNo;

  @NonNull
  public final Button buttonFamilyOrNurseConcernYes;

  @NonNull
  public final TextView textVitalSignValue;

  private ManualVitalSignsEntryFamilyOrNurseConcernBinding(@NonNull TableLayout rootView,
      @NonNull Button buttonFamilyOrNurseConcernNo, @NonNull Button buttonFamilyOrNurseConcernYes,
      @NonNull TextView textVitalSignValue) {
    this.rootView = rootView;
    this.buttonFamilyOrNurseConcernNo = buttonFamilyOrNurseConcernNo;
    this.buttonFamilyOrNurseConcernYes = buttonFamilyOrNurseConcernYes;
    this.textVitalSignValue = textVitalSignValue;
  }

  @Override
  @NonNull
  public TableLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ManualVitalSignsEntryFamilyOrNurseConcernBinding inflate(
      @NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ManualVitalSignsEntryFamilyOrNurseConcernBinding inflate(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.manual_vital_signs_entry__family_or_nurse_concern, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ManualVitalSignsEntryFamilyOrNurseConcernBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonFamilyOrNurseConcernNo;
      Button buttonFamilyOrNurseConcernNo = ViewBindings.findChildViewById(rootView, id);
      if (buttonFamilyOrNurseConcernNo == null) {
        break missingId;
      }

      id = R.id.buttonFamilyOrNurseConcernYes;
      Button buttonFamilyOrNurseConcernYes = ViewBindings.findChildViewById(rootView, id);
      if (buttonFamilyOrNurseConcernYes == null) {
        break missingId;
      }

      id = R.id.textVitalSignValue;
      TextView textVitalSignValue = ViewBindings.findChildViewById(rootView, id);
      if (textVitalSignValue == null) {
        break missingId;
      }

      return new ManualVitalSignsEntryFamilyOrNurseConcernBinding((TableLayout) rootView,
          buttonFamilyOrNurseConcernNo, buttonFamilyOrNurseConcernYes, textVitalSignValue);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}