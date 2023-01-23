// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.ajit.customseekbar.CustomSeekBarVertical;
import com.isansys.pse_isansysportal.R;
import com.jjoe64.graphview.GraphView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class BloodPressureGraphBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout normalMode;

  @NonNull
  public final GraphView plotBloodPressure;

  @NonNull
  public final CustomSeekBarVertical seekBarGraphScaleBloodPressureLeft;

  @NonNull
  public final CustomSeekBarVertical seekBarGraphScaleBloodPressureRight;

  private BloodPressureGraphBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout normalMode, @NonNull GraphView plotBloodPressure,
      @NonNull CustomSeekBarVertical seekBarGraphScaleBloodPressureLeft,
      @NonNull CustomSeekBarVertical seekBarGraphScaleBloodPressureRight) {
    this.rootView = rootView;
    this.normalMode = normalMode;
    this.plotBloodPressure = plotBloodPressure;
    this.seekBarGraphScaleBloodPressureLeft = seekBarGraphScaleBloodPressureLeft;
    this.seekBarGraphScaleBloodPressureRight = seekBarGraphScaleBloodPressureRight;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static BloodPressureGraphBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static BloodPressureGraphBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.blood_pressure_graph, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static BloodPressureGraphBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      LinearLayout normalMode = (LinearLayout) rootView;

      id = R.id.plotBloodPressure;
      GraphView plotBloodPressure = ViewBindings.findChildViewById(rootView, id);
      if (plotBloodPressure == null) {
        break missingId;
      }

      id = R.id.seekBarGraphScaleBloodPressureLeft;
      CustomSeekBarVertical seekBarGraphScaleBloodPressureLeft = ViewBindings.findChildViewById(rootView, id);
      if (seekBarGraphScaleBloodPressureLeft == null) {
        break missingId;
      }

      id = R.id.seekBarGraphScaleBloodPressureRight;
      CustomSeekBarVertical seekBarGraphScaleBloodPressureRight = ViewBindings.findChildViewById(rootView, id);
      if (seekBarGraphScaleBloodPressureRight == null) {
        break missingId;
      }

      return new BloodPressureGraphBinding((LinearLayout) rootView, normalMode, plotBloodPressure,
          seekBarGraphScaleBloodPressureLeft, seekBarGraphScaleBloodPressureRight);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
