// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;

public final class ListHistoricalSetupModeTimesBinding implements ViewBinding {
  @NonNull
  private final TextView rootView;

  @NonNull
  public final TextView textHistoricalSetupModeButton;

  private ListHistoricalSetupModeTimesBinding(@NonNull TextView rootView,
      @NonNull TextView textHistoricalSetupModeButton) {
    this.rootView = rootView;
    this.textHistoricalSetupModeButton = textHistoricalSetupModeButton;
  }

  @Override
  @NonNull
  public TextView getRoot() {
    return rootView;
  }

  @NonNull
  public static ListHistoricalSetupModeTimesBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ListHistoricalSetupModeTimesBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.list_historical_setup_mode_times, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ListHistoricalSetupModeTimesBinding bind(@NonNull View rootView) {
    if (rootView == null) {
      throw new NullPointerException("rootView");
    }

    TextView textHistoricalSetupModeButton = (TextView) rootView;

    return new ListHistoricalSetupModeTimesBinding((TextView) rootView,
        textHistoricalSetupModeButton);
  }
}
