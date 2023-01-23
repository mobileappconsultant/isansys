// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public final class ActivityCustomErrorBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonRestart;

  @NonNull
  public final TextView textErrorDetails;

  @NonNull
  public final TextView textViewInformationLineOne;

  @NonNull
  public final TextView textViewInformationLineTwo;

  private ActivityCustomErrorBinding(@NonNull LinearLayout rootView, @NonNull Button buttonRestart,
      @NonNull TextView textErrorDetails, @NonNull TextView textViewInformationLineOne,
      @NonNull TextView textViewInformationLineTwo) {
    this.rootView = rootView;
    this.buttonRestart = buttonRestart;
    this.textErrorDetails = textErrorDetails;
    this.textViewInformationLineOne = textViewInformationLineOne;
    this.textViewInformationLineTwo = textViewInformationLineTwo;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCustomErrorBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCustomErrorBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_custom_error, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCustomErrorBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonRestart;
      Button buttonRestart = ViewBindings.findChildViewById(rootView, id);
      if (buttonRestart == null) {
        break missingId;
      }

      id = R.id.textErrorDetails;
      TextView textErrorDetails = ViewBindings.findChildViewById(rootView, id);
      if (textErrorDetails == null) {
        break missingId;
      }

      id = R.id.textViewInformationLineOne;
      TextView textViewInformationLineOne = ViewBindings.findChildViewById(rootView, id);
      if (textViewInformationLineOne == null) {
        break missingId;
      }

      id = R.id.textViewInformationLineTwo;
      TextView textViewInformationLineTwo = ViewBindings.findChildViewById(rootView, id);
      if (textViewInformationLineTwo == null) {
        break missingId;
      }

      return new ActivityCustomErrorBinding((LinearLayout) rootView, buttonRestart,
          textErrorDetails, textViewInformationLineOne, textViewInformationLineTwo);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
