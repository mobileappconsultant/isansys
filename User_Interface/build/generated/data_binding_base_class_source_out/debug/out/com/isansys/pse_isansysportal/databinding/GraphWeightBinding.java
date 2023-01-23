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

public final class GraphWeightBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout normalMode;

  @NonNull
  public final GraphView plotWeight;

  @NonNull
  public final CustomSeekBarVertical seekBarGraphScaleWeightLeft;

  @NonNull
  public final CustomSeekBarVertical seekBarGraphScaleWeightRight;

  private GraphWeightBinding(@NonNull LinearLayout rootView, @NonNull LinearLayout normalMode,
      @NonNull GraphView plotWeight, @NonNull CustomSeekBarVertical seekBarGraphScaleWeightLeft,
      @NonNull CustomSeekBarVertical seekBarGraphScaleWeightRight) {
    this.rootView = rootView;
    this.normalMode = normalMode;
    this.plotWeight = plotWeight;
    this.seekBarGraphScaleWeightLeft = seekBarGraphScaleWeightLeft;
    this.seekBarGraphScaleWeightRight = seekBarGraphScaleWeightRight;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static GraphWeightBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static GraphWeightBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.graph_weight, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static GraphWeightBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      LinearLayout normalMode = (LinearLayout) rootView;

      id = R.id.plotWeight;
      GraphView plotWeight = ViewBindings.findChildViewById(rootView, id);
      if (plotWeight == null) {
        break missingId;
      }

      id = R.id.seekBarGraphScaleWeightLeft;
      CustomSeekBarVertical seekBarGraphScaleWeightLeft = ViewBindings.findChildViewById(rootView, id);
      if (seekBarGraphScaleWeightLeft == null) {
        break missingId;
      }

      id = R.id.seekBarGraphScaleWeightRight;
      CustomSeekBarVertical seekBarGraphScaleWeightRight = ViewBindings.findChildViewById(rootView, id);
      if (seekBarGraphScaleWeightRight == null) {
        break missingId;
      }

      return new GraphWeightBinding((LinearLayout) rootView, normalMode, plotWeight,
          seekBarGraphScaleWeightLeft, seekBarGraphScaleWeightRight);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
