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
import com.isansys.pse_isansysportal.AutoResizeTextView;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class WebpageButtonForSelectionBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final AutoResizeTextView buttonWebpageName;

  private WebpageButtonForSelectionBinding(@NonNull LinearLayout rootView,
      @NonNull AutoResizeTextView buttonWebpageName) {
    this.rootView = rootView;
    this.buttonWebpageName = buttonWebpageName;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static WebpageButtonForSelectionBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static WebpageButtonForSelectionBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.webpage_button_for_selection, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static WebpageButtonForSelectionBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonWebpageName;
      AutoResizeTextView buttonWebpageName = ViewBindings.findChildViewById(rootView, id);
      if (buttonWebpageName == null) {
        break missingId;
      }

      return new WebpageButtonForSelectionBinding((LinearLayout) rootView, buttonWebpageName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
