// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ManualVitalSignsBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final FrameLayout frameLayoutManualVitalSignsEntry;

  @NonNull
  public final TextView textTop;

  private ManualVitalSignsBinding(@NonNull LinearLayout rootView,
      @NonNull FrameLayout frameLayoutManualVitalSignsEntry, @NonNull TextView textTop) {
    this.rootView = rootView;
    this.frameLayoutManualVitalSignsEntry = frameLayoutManualVitalSignsEntry;
    this.textTop = textTop;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ManualVitalSignsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ManualVitalSignsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.manual_vital_signs, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ManualVitalSignsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.frameLayoutManualVitalSignsEntry;
      FrameLayout frameLayoutManualVitalSignsEntry = ViewBindings.findChildViewById(rootView, id);
      if (frameLayoutManualVitalSignsEntry == null) {
        break missingId;
      }

      id = R.id.textTop;
      TextView textTop = ViewBindings.findChildViewById(rootView, id);
      if (textTop == null) {
        break missingId;
      }

      return new ManualVitalSignsBinding((LinearLayout) rootView, frameLayoutManualVitalSignsEntry,
          textTop);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}