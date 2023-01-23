// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;

public final class LifetouchPoorSignalBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout normalMode;

  private LifetouchPoorSignalBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout normalMode) {
    this.rootView = rootView;
    this.normalMode = normalMode;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LifetouchPoorSignalBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LifetouchPoorSignalBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.lifetouch_poor_signal, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LifetouchPoorSignalBinding bind(@NonNull View rootView) {
    if (rootView == null) {
      throw new NullPointerException("rootView");
    }

    LinearLayout normalMode = (LinearLayout) rootView;

    return new LifetouchPoorSignalBinding((LinearLayout) rootView, normalMode);
  }
}
