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

public final class VideoCallContactButtonForSelectionBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final AutoResizeTextView buttonContact;

  @NonNull
  public final LinearLayout observationDisplayButtonForSelection;

  private VideoCallContactButtonForSelectionBinding(@NonNull LinearLayout rootView,
      @NonNull AutoResizeTextView buttonContact,
      @NonNull LinearLayout observationDisplayButtonForSelection) {
    this.rootView = rootView;
    this.buttonContact = buttonContact;
    this.observationDisplayButtonForSelection = observationDisplayButtonForSelection;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static VideoCallContactButtonForSelectionBinding inflate(
      @NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static VideoCallContactButtonForSelectionBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.video_call_contact_button_for_selection, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static VideoCallContactButtonForSelectionBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonContact;
      AutoResizeTextView buttonContact = ViewBindings.findChildViewById(rootView, id);
      if (buttonContact == null) {
        break missingId;
      }

      LinearLayout observationDisplayButtonForSelection = (LinearLayout) rootView;

      return new VideoCallContactButtonForSelectionBinding((LinearLayout) rootView, buttonContact,
          observationDisplayButtonForSelection);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
