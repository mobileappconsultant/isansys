// Generated by view binder compiler. Do not edit!
package com.isansys.pse_isansysportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.isansys.pse_isansysportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.webrtc.SurfaceViewRenderer;

public final class PopUpIncomingVideoCallBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonEndCall;

  @NonNull
  public final Button buttonEndCallNo;

  @NonNull
  public final Button buttonEndCallYes;

  @NonNull
  public final Button buttonFailedCallOkay;

  @NonNull
  public final Button buttonOkay;

  @NonNull
  public final Button buttonOutgoingCallCancelRequest;

  @NonNull
  public final Button buttonRejectCall;

  @NonNull
  public final Button buttonStartVideoCall;

  @NonNull
  public final ImageView imageCamera;

  @NonNull
  public final ImageView imageInfo;

  @NonNull
  public final ImageView imageMicrophone;

  @NonNull
  public final ImageView imageVideoCall;

  @NonNull
  public final TextView labelInWaitingRoom;

  @NonNull
  public final TextView labelPleaseWaitConnecting;

  @NonNull
  public final LinearLayout linearLayoutEndCall;

  @NonNull
  public final LinearLayout linearLayoutEndCallConfirm;

  @NonNull
  public final LinearLayout linearLayoutFailedCall;

  @NonNull
  public final LinearLayout linearLayoutInWaitingRoom;

  @NonNull
  public final LinearLayout linearLayoutIncomingCallTextAndButtons;

  @NonNull
  public final LinearLayout linearLayoutMissedOrDeclinedCall;

  @NonNull
  public final LinearLayout linearLayoutOutgoingCallTextAndButton;

  @NonNull
  public final LinearLayout linearLayoutPopupIncomingVideoCall;

  @NonNull
  public final LinearLayout linearLayoutSelfView;

  @NonNull
  public final LinearLayout linearLayoutToggleMicrophone;

  @NonNull
  public final LinearLayout linearLayoutToggleVideo;

  @NonNull
  public final LinearLayout linearLayoutVideoCallIcon;

  @NonNull
  public final LinearLayout linearLayoutWaitingForMeetingToStart;

  @NonNull
  public final SurfaceViewRenderer localGlSurfaceView;

  @NonNull
  public final FrameLayout meetingVideoView;

  @NonNull
  public final RelativeLayout relativeLayoutVideoCallStarted;

  @NonNull
  public final TextView textViewFailedCallText;

  @NonNull
  public final TextView textViewMeetingText;

  @NonNull
  public final TextView textViewMissedOrDeclinedCallText;

  @NonNull
  public final TextView textViewOutgoingCallText;

  @NonNull
  public final GridLayout viewsContainer;

  private PopUpIncomingVideoCallBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonEndCall, @NonNull Button buttonEndCallNo,
      @NonNull Button buttonEndCallYes, @NonNull Button buttonFailedCallOkay,
      @NonNull Button buttonOkay, @NonNull Button buttonOutgoingCallCancelRequest,
      @NonNull Button buttonRejectCall, @NonNull Button buttonStartVideoCall,
      @NonNull ImageView imageCamera, @NonNull ImageView imageInfo,
      @NonNull ImageView imageMicrophone, @NonNull ImageView imageVideoCall,
      @NonNull TextView labelInWaitingRoom, @NonNull TextView labelPleaseWaitConnecting,
      @NonNull LinearLayout linearLayoutEndCall, @NonNull LinearLayout linearLayoutEndCallConfirm,
      @NonNull LinearLayout linearLayoutFailedCall, @NonNull LinearLayout linearLayoutInWaitingRoom,
      @NonNull LinearLayout linearLayoutIncomingCallTextAndButtons,
      @NonNull LinearLayout linearLayoutMissedOrDeclinedCall,
      @NonNull LinearLayout linearLayoutOutgoingCallTextAndButton,
      @NonNull LinearLayout linearLayoutPopupIncomingVideoCall,
      @NonNull LinearLayout linearLayoutSelfView,
      @NonNull LinearLayout linearLayoutToggleMicrophone,
      @NonNull LinearLayout linearLayoutToggleVideo,
      @NonNull LinearLayout linearLayoutVideoCallIcon,
      @NonNull LinearLayout linearLayoutWaitingForMeetingToStart,
      @NonNull SurfaceViewRenderer localGlSurfaceView, @NonNull FrameLayout meetingVideoView,
      @NonNull RelativeLayout relativeLayoutVideoCallStarted,
      @NonNull TextView textViewFailedCallText, @NonNull TextView textViewMeetingText,
      @NonNull TextView textViewMissedOrDeclinedCallText,
      @NonNull TextView textViewOutgoingCallText, @NonNull GridLayout viewsContainer) {
    this.rootView = rootView;
    this.buttonEndCall = buttonEndCall;
    this.buttonEndCallNo = buttonEndCallNo;
    this.buttonEndCallYes = buttonEndCallYes;
    this.buttonFailedCallOkay = buttonFailedCallOkay;
    this.buttonOkay = buttonOkay;
    this.buttonOutgoingCallCancelRequest = buttonOutgoingCallCancelRequest;
    this.buttonRejectCall = buttonRejectCall;
    this.buttonStartVideoCall = buttonStartVideoCall;
    this.imageCamera = imageCamera;
    this.imageInfo = imageInfo;
    this.imageMicrophone = imageMicrophone;
    this.imageVideoCall = imageVideoCall;
    this.labelInWaitingRoom = labelInWaitingRoom;
    this.labelPleaseWaitConnecting = labelPleaseWaitConnecting;
    this.linearLayoutEndCall = linearLayoutEndCall;
    this.linearLayoutEndCallConfirm = linearLayoutEndCallConfirm;
    this.linearLayoutFailedCall = linearLayoutFailedCall;
    this.linearLayoutInWaitingRoom = linearLayoutInWaitingRoom;
    this.linearLayoutIncomingCallTextAndButtons = linearLayoutIncomingCallTextAndButtons;
    this.linearLayoutMissedOrDeclinedCall = linearLayoutMissedOrDeclinedCall;
    this.linearLayoutOutgoingCallTextAndButton = linearLayoutOutgoingCallTextAndButton;
    this.linearLayoutPopupIncomingVideoCall = linearLayoutPopupIncomingVideoCall;
    this.linearLayoutSelfView = linearLayoutSelfView;
    this.linearLayoutToggleMicrophone = linearLayoutToggleMicrophone;
    this.linearLayoutToggleVideo = linearLayoutToggleVideo;
    this.linearLayoutVideoCallIcon = linearLayoutVideoCallIcon;
    this.linearLayoutWaitingForMeetingToStart = linearLayoutWaitingForMeetingToStart;
    this.localGlSurfaceView = localGlSurfaceView;
    this.meetingVideoView = meetingVideoView;
    this.relativeLayoutVideoCallStarted = relativeLayoutVideoCallStarted;
    this.textViewFailedCallText = textViewFailedCallText;
    this.textViewMeetingText = textViewMeetingText;
    this.textViewMissedOrDeclinedCallText = textViewMissedOrDeclinedCallText;
    this.textViewOutgoingCallText = textViewOutgoingCallText;
    this.viewsContainer = viewsContainer;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static PopUpIncomingVideoCallBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static PopUpIncomingVideoCallBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.pop_up_incoming_video_call, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static PopUpIncomingVideoCallBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonEndCall;
      Button buttonEndCall = ViewBindings.findChildViewById(rootView, id);
      if (buttonEndCall == null) {
        break missingId;
      }

      id = R.id.buttonEndCallNo;
      Button buttonEndCallNo = ViewBindings.findChildViewById(rootView, id);
      if (buttonEndCallNo == null) {
        break missingId;
      }

      id = R.id.buttonEndCallYes;
      Button buttonEndCallYes = ViewBindings.findChildViewById(rootView, id);
      if (buttonEndCallYes == null) {
        break missingId;
      }

      id = R.id.buttonFailedCallOkay;
      Button buttonFailedCallOkay = ViewBindings.findChildViewById(rootView, id);
      if (buttonFailedCallOkay == null) {
        break missingId;
      }

      id = R.id.buttonOkay;
      Button buttonOkay = ViewBindings.findChildViewById(rootView, id);
      if (buttonOkay == null) {
        break missingId;
      }

      id = R.id.buttonOutgoingCallCancelRequest;
      Button buttonOutgoingCallCancelRequest = ViewBindings.findChildViewById(rootView, id);
      if (buttonOutgoingCallCancelRequest == null) {
        break missingId;
      }

      id = R.id.buttonRejectCall;
      Button buttonRejectCall = ViewBindings.findChildViewById(rootView, id);
      if (buttonRejectCall == null) {
        break missingId;
      }

      id = R.id.buttonStartVideoCall;
      Button buttonStartVideoCall = ViewBindings.findChildViewById(rootView, id);
      if (buttonStartVideoCall == null) {
        break missingId;
      }

      id = R.id.imageCamera;
      ImageView imageCamera = ViewBindings.findChildViewById(rootView, id);
      if (imageCamera == null) {
        break missingId;
      }

      id = R.id.imageInfo;
      ImageView imageInfo = ViewBindings.findChildViewById(rootView, id);
      if (imageInfo == null) {
        break missingId;
      }

      id = R.id.imageMicrophone;
      ImageView imageMicrophone = ViewBindings.findChildViewById(rootView, id);
      if (imageMicrophone == null) {
        break missingId;
      }

      id = R.id.imageVideoCall;
      ImageView imageVideoCall = ViewBindings.findChildViewById(rootView, id);
      if (imageVideoCall == null) {
        break missingId;
      }

      id = R.id.labelInWaitingRoom;
      TextView labelInWaitingRoom = ViewBindings.findChildViewById(rootView, id);
      if (labelInWaitingRoom == null) {
        break missingId;
      }

      id = R.id.labelPleaseWaitConnecting;
      TextView labelPleaseWaitConnecting = ViewBindings.findChildViewById(rootView, id);
      if (labelPleaseWaitConnecting == null) {
        break missingId;
      }

      id = R.id.linearLayoutEndCall;
      LinearLayout linearLayoutEndCall = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutEndCall == null) {
        break missingId;
      }

      id = R.id.linearLayoutEndCallConfirm;
      LinearLayout linearLayoutEndCallConfirm = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutEndCallConfirm == null) {
        break missingId;
      }

      id = R.id.linearLayoutFailedCall;
      LinearLayout linearLayoutFailedCall = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutFailedCall == null) {
        break missingId;
      }

      id = R.id.linearLayoutInWaitingRoom;
      LinearLayout linearLayoutInWaitingRoom = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutInWaitingRoom == null) {
        break missingId;
      }

      id = R.id.linearLayoutIncomingCallTextAndButtons;
      LinearLayout linearLayoutIncomingCallTextAndButtons = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutIncomingCallTextAndButtons == null) {
        break missingId;
      }

      id = R.id.linearLayoutMissedOrDeclinedCall;
      LinearLayout linearLayoutMissedOrDeclinedCall = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutMissedOrDeclinedCall == null) {
        break missingId;
      }

      id = R.id.linearLayoutOutgoingCallTextAndButton;
      LinearLayout linearLayoutOutgoingCallTextAndButton = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutOutgoingCallTextAndButton == null) {
        break missingId;
      }

      LinearLayout linearLayoutPopupIncomingVideoCall = (LinearLayout) rootView;

      id = R.id.linearLayoutSelfView;
      LinearLayout linearLayoutSelfView = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutSelfView == null) {
        break missingId;
      }

      id = R.id.linearLayoutToggleMicrophone;
      LinearLayout linearLayoutToggleMicrophone = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutToggleMicrophone == null) {
        break missingId;
      }

      id = R.id.linearLayoutToggleVideo;
      LinearLayout linearLayoutToggleVideo = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutToggleVideo == null) {
        break missingId;
      }

      id = R.id.linearLayoutVideoCallIcon;
      LinearLayout linearLayoutVideoCallIcon = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutVideoCallIcon == null) {
        break missingId;
      }

      id = R.id.linearLayoutWaitingForMeetingToStart;
      LinearLayout linearLayoutWaitingForMeetingToStart = ViewBindings.findChildViewById(rootView, id);
      if (linearLayoutWaitingForMeetingToStart == null) {
        break missingId;
      }

      id = R.id.local_gl_surface_view;
      SurfaceViewRenderer localGlSurfaceView = ViewBindings.findChildViewById(rootView, id);
      if (localGlSurfaceView == null) {
        break missingId;
      }

      id = R.id.meetingVideoView;
      FrameLayout meetingVideoView = ViewBindings.findChildViewById(rootView, id);
      if (meetingVideoView == null) {
        break missingId;
      }

      id = R.id.relativeLayoutVideoCallStarted;
      RelativeLayout relativeLayoutVideoCallStarted = ViewBindings.findChildViewById(rootView, id);
      if (relativeLayoutVideoCallStarted == null) {
        break missingId;
      }

      id = R.id.textViewFailedCallText;
      TextView textViewFailedCallText = ViewBindings.findChildViewById(rootView, id);
      if (textViewFailedCallText == null) {
        break missingId;
      }

      id = R.id.textViewMeetingText;
      TextView textViewMeetingText = ViewBindings.findChildViewById(rootView, id);
      if (textViewMeetingText == null) {
        break missingId;
      }

      id = R.id.textViewMissedOrDeclinedCallText;
      TextView textViewMissedOrDeclinedCallText = ViewBindings.findChildViewById(rootView, id);
      if (textViewMissedOrDeclinedCallText == null) {
        break missingId;
      }

      id = R.id.textViewOutgoingCallText;
      TextView textViewOutgoingCallText = ViewBindings.findChildViewById(rootView, id);
      if (textViewOutgoingCallText == null) {
        break missingId;
      }

      id = R.id.views_container;
      GridLayout viewsContainer = ViewBindings.findChildViewById(rootView, id);
      if (viewsContainer == null) {
        break missingId;
      }

      return new PopUpIncomingVideoCallBinding((LinearLayout) rootView, buttonEndCall,
          buttonEndCallNo, buttonEndCallYes, buttonFailedCallOkay, buttonOkay,
          buttonOutgoingCallCancelRequest, buttonRejectCall, buttonStartVideoCall, imageCamera,
          imageInfo, imageMicrophone, imageVideoCall, labelInWaitingRoom, labelPleaseWaitConnecting,
          linearLayoutEndCall, linearLayoutEndCallConfirm, linearLayoutFailedCall,
          linearLayoutInWaitingRoom, linearLayoutIncomingCallTextAndButtons,
          linearLayoutMissedOrDeclinedCall, linearLayoutOutgoingCallTextAndButton,
          linearLayoutPopupIncomingVideoCall, linearLayoutSelfView, linearLayoutToggleMicrophone,
          linearLayoutToggleVideo, linearLayoutVideoCallIcon, linearLayoutWaitingForMeetingToStart,
          localGlSurfaceView, meetingVideoView, relativeLayoutVideoCallStarted,
          textViewFailedCallText, textViewMeetingText, textViewMissedOrDeclinedCallText,
          textViewOutgoingCallText, viewsContainer);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}