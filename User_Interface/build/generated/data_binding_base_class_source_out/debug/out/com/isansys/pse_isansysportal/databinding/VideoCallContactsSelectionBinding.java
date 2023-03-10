// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class VideoCallContactsSelectionBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonVideoCall;

  @NonNull
  public final ImageView imageVideoCall;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final TextView textTop;

  @NonNull
  public final TextView textViewNoContactsHaveBeenSetup;

  private VideoCallContactsSelectionBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonVideoCall, @NonNull ImageView imageVideoCall,
      @NonNull RecyclerView recyclerView, @NonNull TextView textTop,
      @NonNull TextView textViewNoContactsHaveBeenSetup) {
    this.rootView = rootView;
    this.buttonVideoCall = buttonVideoCall;
    this.imageVideoCall = imageVideoCall;
    this.recyclerView = recyclerView;
    this.textTop = textTop;
    this.textViewNoContactsHaveBeenSetup = textViewNoContactsHaveBeenSetup;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static VideoCallContactsSelectionBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static VideoCallContactsSelectionBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.video_call_contacts_selection, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static VideoCallContactsSelectionBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonVideoCall;
      Button buttonVideoCall = ViewBindings.findChildViewById(rootView, id);
      if (buttonVideoCall == null) {
        break missingId;
      }

      id = R.id.imageVideoCall;
      ImageView imageVideoCall = ViewBindings.findChildViewById(rootView, id);
      if (imageVideoCall == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      RecyclerView recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.textTop;
      TextView textTop = ViewBindings.findChildViewById(rootView, id);
      if (textTop == null) {
        break missingId;
      }

      id = R.id.textViewNoContactsHaveBeenSetup;
      TextView textViewNoContactsHaveBeenSetup = ViewBindings.findChildViewById(rootView, id);
      if (textViewNoContactsHaveBeenSetup == null) {
        break missingId;
      }

      return new VideoCallContactsSelectionBinding((LinearLayout) rootView, buttonVideoCall,
          imageVideoCall, recyclerView, textTop, textViewNoContactsHaveBeenSetup);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
