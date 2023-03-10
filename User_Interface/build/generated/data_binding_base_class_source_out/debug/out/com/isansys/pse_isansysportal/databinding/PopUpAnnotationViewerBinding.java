// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class PopUpAnnotationViewerBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonHistoricalSetupModeViewerDismiss;

  @NonNull
  public final ListView listview;

  private PopUpAnnotationViewerBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonHistoricalSetupModeViewerDismiss, @NonNull ListView listview) {
    this.rootView = rootView;
    this.buttonHistoricalSetupModeViewerDismiss = buttonHistoricalSetupModeViewerDismiss;
    this.listview = listview;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static PopUpAnnotationViewerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static PopUpAnnotationViewerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.pop_up_annotation_viewer, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static PopUpAnnotationViewerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonHistoricalSetupModeViewerDismiss;
      Button buttonHistoricalSetupModeViewerDismiss = ViewBindings.findChildViewById(rootView, id);
      if (buttonHistoricalSetupModeViewerDismiss == null) {
        break missingId;
      }

      id = R.id.listview;
      ListView listview = ViewBindings.findChildViewById(rootView, id);
      if (listview == null) {
        break missingId;
      }

      return new PopUpAnnotationViewerBinding((LinearLayout) rootView,
          buttonHistoricalSetupModeViewerDismiss, listview);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
