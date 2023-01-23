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

public final class LifetouchNormalModeRespirationRateBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout normalMode;

  @NonNull
  public final GraphView plotRR;

  @NonNull
  public final CustomSeekBarVertical seekBarGraphScaleLifetouchRespirationRateLeft;

  @NonNull
  public final CustomSeekBarVertical seekBarGraphScaleLifetouchRespirationRateRight;

  private LifetouchNormalModeRespirationRateBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout normalMode, @NonNull GraphView plotRR,
      @NonNull CustomSeekBarVertical seekBarGraphScaleLifetouchRespirationRateLeft,
      @NonNull CustomSeekBarVertical seekBarGraphScaleLifetouchRespirationRateRight) {
    this.rootView = rootView;
    this.normalMode = normalMode;
    this.plotRR = plotRR;
    this.seekBarGraphScaleLifetouchRespirationRateLeft = seekBarGraphScaleLifetouchRespirationRateLeft;
    this.seekBarGraphScaleLifetouchRespirationRateRight = seekBarGraphScaleLifetouchRespirationRateRight;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LifetouchNormalModeRespirationRateBinding inflate(
      @NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LifetouchNormalModeRespirationRateBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.lifetouch_normal_mode_respiration_rate, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LifetouchNormalModeRespirationRateBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      LinearLayout normalMode = (LinearLayout) rootView;

      id = R.id.plotRR;
      GraphView plotRR = ViewBindings.findChildViewById(rootView, id);
      if (plotRR == null) {
        break missingId;
      }

      id = R.id.seekBarGraphScaleLifetouchRespirationRateLeft;
      CustomSeekBarVertical seekBarGraphScaleLifetouchRespirationRateLeft = ViewBindings.findChildViewById(rootView, id);
      if (seekBarGraphScaleLifetouchRespirationRateLeft == null) {
        break missingId;
      }

      id = R.id.seekBarGraphScaleLifetouchRespirationRateRight;
      CustomSeekBarVertical seekBarGraphScaleLifetouchRespirationRateRight = ViewBindings.findChildViewById(rootView, id);
      if (seekBarGraphScaleLifetouchRespirationRateRight == null) {
        break missingId;
      }

      return new LifetouchNormalModeRespirationRateBinding((LinearLayout) rootView, normalMode,
          plotRR, seekBarGraphScaleLifetouchRespirationRateLeft,
          seekBarGraphScaleLifetouchRespirationRateRight);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
