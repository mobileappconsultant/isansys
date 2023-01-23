package com.isansys.pse_isansysportal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.webrtc.EglBase;
import org.webrtc.SurfaceViewRenderer;

public class PopupVideoCall extends IsansysPopupDialogFragment
{
    private final String TAG = this.getClass().getSimpleName();

    private LinearLayout linearLayoutWaitingForMeetingToStart;
    private LinearLayout linearLayoutInWaitingRoom;

    private FrameLayout mMeetingVideoView;

    private MissedCallCountdownTimer timerMissedCall;

    private GridLayout views_container;
    private SurfaceViewRenderer localVideoView;

    public enum VideoCallType
    {
        INCOMING_FROM_SERVER,
        OUTGOING_TO_SERVER,
    }

    private VideoCallType videoCallType;

    public PopupVideoCall(MainActivityInterface main_activity_interface)
    {
        super(main_activity_interface);
    }

    class MissedCallCountdownTimer extends CountDownTimer
    {
        MissedCallCountdownTimer(long startTime)
        {
            super(startTime, 1000);

            Log.d(TAG, "MissedCallCountdownTimer : startTime : " + startTime);
        }

        @Override
        public void onFinish()
        {
            Log.d(TAG, "MissedCallCountdownTimer : onFinish");

            // Waited for the user long enough. Change to showing Miss Call message
            missedCall();
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            Log.d(TAG, "MissedCallCountdownTimer : onTick : " + millisUntilFinished / (int) DateUtils.SECOND_IN_MILLIS);
        }
    }


    private void missedCall()
    {
        Log.d(TAG, "missedCall");

        showMissedOrDeclinedCallAndStopRinging();

        callback.MissedCall();
    }

    public void declinedCall()
    {
        Log.d(TAG, "declinedCall");

        resetMissedCallTimer();

        showMissedOrDeclinedCallAndStopRinging();
    }

    private void showMissedOrDeclinedCallAndStopRinging()
    {
        hideUiElements();
        linearLayoutMissedOrDeclinedCall.setVisibility(View.VISIBLE);

        callback.StopRingingAudio();
    }

    public void resetPopupForIncomingCall()
    {
        Log.d(TAG, "resetPopupForIncomingCall");

        showIncomingCallRequestAndStartRinging();
    }

    public interface Callback
    {
        void AcceptIncomingVideoCall();
        void DeclineIncomingVideoCall();
        void MeetingLeaveComplete();
        void MeetingFailed();
        void UserWantsToLeaveMeeting();
        void MissedCall();
        void PopupDismissed();
        void ScreenTouched();
        void StartRingingAudio();
        void StopRingingAudio();
        void SetupVolumeForIncomingCallRinging();
        void SetupVolumeForOutgoingCallRinging();
        void SetupVolumeForActiveCall();
        void CancelledOutgoingCallRequest();
        boolean ToggleAudioEnabled();
        boolean ToggleVideoEnabled();
    }

    RelativeLayout relativeLayoutVideoCallStarted;

    LinearLayout linearLayoutPopupIncomingVideoCall;

    LinearLayout linearLayoutVideoCallIcon;
    LinearLayout linearLayoutIncomingCallTextAndButtons;
    LinearLayout linearLayoutOutgoingCallTextAndButton;

    LinearLayout linearLayoutMissedOrDeclinedCall;
    LinearLayout linearLayoutFailedCall;

    LinearLayout linearLayoutEndCall;
    LinearLayout linearLayoutEndCallConfirm;

    private Callback callback;

    private String meetingFromText = "";
    private String missedCallText = "";

    public void setArguments(Callback callback)
    {
        this.callback = callback;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Context context = getActivity();

        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_incoming_video_call);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().getDecorView().setOnTouchListener((v, event) -> {
            callback.ScreenTouched();

            v.performClick();

            return false;
        });


        views_container = dialog.findViewById(R.id.views_container);
        localVideoView = dialog.findViewById(R.id.local_gl_surface_view);

        Button buttonStartVideoCall = dialog.findViewById(R.id.buttonStartVideoCall);
        buttonStartVideoCall.setOnClickListener(x -> {
            callback.StopRingingAudio();

            resetMissedCallTimer();

            callback.AcceptIncomingVideoCall();

            callback.SetupVolumeForActiveCall();

            switchToMeetingView();
        });

        Button buttonRejectCall = dialog.findViewById(R.id.buttonRejectCall);
        buttonRejectCall.setOnClickListener(x -> {
            callback.StopRingingAudio();
            callback.DeclineIncomingVideoCall();
            dismissPopupIfVisible();
        });

        Button buttonOutgoingCallCancelRequest = dialog.findViewById(R.id.buttonOutgoingCallCancelRequest);
        buttonOutgoingCallCancelRequest.setOnClickListener(x -> {
            callback.StopRingingAudio();
            callback.CancelledOutgoingCallRequest();
            dismissPopupIfVisible();
        });

        Button buttonEndCall = dialog.findViewById(R.id.buttonEndCall);
        buttonEndCall.setOnClickListener(x -> {
            // Switch to the Confirmation buttons
            linearLayoutEndCall.setVisibility(View.GONE);
            linearLayoutEndCallConfirm.setVisibility(View.VISIBLE);
        });

        Button buttonEndCallYes = dialog.findViewById(R.id.buttonEndCallYes);
        buttonEndCallYes.setOnClickListener(x -> leaveCall());

        Button buttonEndCallNo = dialog.findViewById(R.id.buttonEndCallNo);
        buttonEndCallNo.setOnClickListener(x -> {
            // User changed their mind and does not want to leave the meeting. Switch back to original button
            linearLayoutEndCall.setVisibility(View.VISIBLE);
            linearLayoutEndCallConfirm.setVisibility(View.GONE);
        });

        ImageView imageMicrophone = dialog.findViewById(R.id.imageMicrophone);
        LinearLayout linearLayoutToggleMicrophone = dialog.findViewById(R.id.linearLayoutToggleMicrophone);
        linearLayoutToggleMicrophone.setOnClickListener(arg0 -> {

            // Only toggle the image if the Toggle function actually worked - until the meeting starts its invalid
            if (toggleAudioEnabled())
            {
                // Toggle the image of the icon
                if (imageMicrophone.getDrawable().getConstantState().equals(imageMicrophone.getContext().getDrawable(R.drawable.microphone_white).getConstantState()))
                {
                    imageMicrophone.setImageResource(R.drawable.mute_white);
                }
                else
                {
                    imageMicrophone.setImageResource(R.drawable.microphone_white);
                }
            }
        });

        ImageView imageCamera = dialog.findViewById(R.id.imageCamera);
        LinearLayout linearLayoutToggleVideo = dialog.findViewById(R.id.linearLayoutToggleVideo);
        linearLayoutToggleVideo.setOnClickListener(arg0 -> {

            // Only toggle the image if the Toggle function actually worked - until the meeting starts its invalid
            if (toggleVideoEnabled())
            {
                // Toggle the image of the icon
                if (imageCamera.getDrawable().getConstantState().equals(imageCamera.getContext().getDrawable(R.drawable.video_white).getConstantState()))
                {
                    imageCamera.setImageResource(R.drawable.no_video_white);
                }
                else
                {
                    imageCamera.setImageResource(R.drawable.video_white);
                }
            }
        });

        relativeLayoutVideoCallStarted = dialog.findViewById(R.id.relativeLayoutVideoCallStarted);

        linearLayoutPopupIncomingVideoCall = dialog.findViewById(R.id.linearLayoutPopupIncomingVideoCall);
        linearLayoutVideoCallIcon = dialog.findViewById(R.id.linearLayoutVideoCallIcon);
        linearLayoutIncomingCallTextAndButtons = dialog.findViewById(R.id.linearLayoutIncomingCallTextAndButtons);
        linearLayoutOutgoingCallTextAndButton = dialog.findViewById(R.id.linearLayoutOutgoingCallTextAndButton);
        linearLayoutMissedOrDeclinedCall = dialog.findViewById(R.id.linearLayoutMissedOrDeclinedCall);
        linearLayoutFailedCall = dialog.findViewById(R.id.linearLayoutFailedCall);
        linearLayoutWaitingForMeetingToStart = dialog.findViewById(R.id.linearLayoutWaitingForMeetingToStart);
        linearLayoutInWaitingRoom = dialog.findViewById(R.id.linearLayoutInWaitingRoom);
        linearLayoutEndCall = dialog.findViewById(R.id.linearLayoutEndCall);
        linearLayoutEndCallConfirm = dialog.findViewById(R.id.linearLayoutEndCallConfirm);

        TextView textViewMissedOrDeclinedCallText = dialog.findViewById(R.id.textViewMissedOrDeclinedCallText);
        textViewMissedOrDeclinedCallText.setText(missedCallText);

        Button buttonOkay = dialog.findViewById(R.id.buttonOkay);
        buttonOkay.setOnClickListener(x -> dismissPopupIfVisible());

        Button buttonFailedCallOkay = dialog.findViewById(R.id.buttonFailedCallOkay);
        buttonFailedCallOkay.setOnClickListener(x -> dismissPopupIfVisible());

        mMeetingVideoView = dialog.findViewById(R.id.meetingVideoView);

        // Set the linear layouts to GONE
        hideUiElements();

        if (videoCallType == VideoCallType.INCOMING_FROM_SERVER)
        {
            TextView textView = dialog.findViewById(R.id.textViewMeetingText);
            textView.setText(meetingFromText);

            showIncomingCallRequestAndStartRinging();
        }
        else
        {
            showOutgoingCallRequestAndStartRinging();
        }

        dialog.show();

        return dialog;
    }

    private void leaveCall()
    {
        // User confirmed they want to end the call
        callback.UserWantsToLeaveMeeting();

        // Do NOT dismiss the popup. Wait for the Meeting to have left and then ZoomMeetingLeaveComplete will fire
        //dismissPopupIfVisible();
    }

    private boolean toggleAudioEnabled()
    {
        return callback.ToggleAudioEnabled();
    }

    private boolean toggleVideoEnabled()
    {
        return callback.ToggleVideoEnabled();
    }

    public void hideUiElements()
    {
        Log.d(TAG, "hideUiElements");

        linearLayoutIncomingCallTextAndButtons.setVisibility(View.GONE);
        linearLayoutOutgoingCallTextAndButton.setVisibility(View.GONE);
        linearLayoutMissedOrDeclinedCall.setVisibility(View.GONE);
        linearLayoutFailedCall.setVisibility(View.GONE);
        linearLayoutInWaitingRoom.setVisibility(View.GONE);
        linearLayoutWaitingForMeetingToStart.setVisibility(View.GONE);
        linearLayoutEndCall.setVisibility(View.GONE);
        linearLayoutEndCallConfirm.setVisibility(View.GONE);

        relativeLayoutVideoCallStarted.setVisibility(View.GONE);
    }

    public void showMeetingViewIncomingCall()
    {
        Log.d(TAG, "showMeetingViewIncomingCall");

        resetMissedCallTimer();
        linearLayoutOutgoingCallTextAndButton.setVisibility(View.GONE);

        callback.SetupVolumeForActiveCall();

        switchToMeetingView();
    }

    public void showIncomingCallRequestAndStartRinging()
    {
        Log.d(TAG, "showIncomingCallRequest");

        // Hide each of the Layouts and only show the correct one
        hideUiElements();
        linearLayoutVideoCallIcon.setVisibility(View.VISIBLE);
        linearLayoutIncomingCallTextAndButtons.setVisibility(View.VISIBLE);
        mMeetingVideoView.setVisibility(View.VISIBLE);

        // Max Volume to get patients attention
        callback.SetupVolumeForIncomingCallRinging();
        callback.StartRingingAudio();
    }


    public void showOutgoingCallRequestAndStartRinging()
    {
        Log.d(TAG, "showOutgoingCallRequest");

        // Hide each of the Layouts and only show the correct one
        hideUiElements();
        linearLayoutOutgoingCallTextAndButton.setVisibility(View.VISIBLE);

        // Reduced volume while calling out. User (Patient/Clinician) already at the Gateway
        callback.SetupVolumeForOutgoingCallRinging();
        callback.StartRingingAudio();
    }


    public void showMeetingFailed()
    {
        Log.d(TAG, "showMeetingFailed");

        hideUiElements();
        linearLayoutFailedCall.setVisibility(View.VISIBLE);
    }

    public void dismissPopupIfVisible()
    {
        Log.d(TAG, "dismissPopupIfVisible");

        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                callback.PopupDismissed();

                getDialog().dismiss();
            }
        }
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog)
    {
        Log.d(TAG, "onDismiss");

        setShowNotInProgress();

        resetMissedCallTimer();

        super.onDismiss(dialog);
    }

    private void resetMissedCallTimer()
    {
        if (timerMissedCall != null)
        {
            Log.d(TAG, "MissedCallCountdownTimer : existing one cancelled");
            timerMissedCall.cancel();
        }
    }

    public void configureParameters(VideoCallType videoCallType, String fromText, String missedCallText, int displayTimeInSeconds)
    {
        this.videoCallType = videoCallType;

        meetingFromText = fromText;
        this.missedCallText = missedCallText;

        resetMissedCallTimer();
        timerMissedCall = new MissedCallCountdownTimer(displayTimeInSeconds * DateUtils.SECOND_IN_MILLIS);
        timerMissedCall.start();
    }

    public void switchToMeetingView()
    {
        int padding = 20;
        linearLayoutPopupIncomingVideoCall.setPadding(padding, padding, padding, padding);

        hideUiElements();
        relativeLayoutVideoCallStarted.setVisibility(View.VISIBLE);
        linearLayoutEndCall.setVisibility(View.VISIBLE);

        localVideoView.bringToFront();
    }

    public void initViews()
    {
        EglBase rootEglBase = EglBase.create();
        localVideoView.init(rootEglBase.getEglBaseContext(), null);
        localVideoView.setMirror(true);
        localVideoView.setEnableHardwareScaler(true);
        localVideoView.setZOrderMediaOverlay(true);
    }

    public void viewToDisconnectedState()
    {
        Log.d(TAG, "VIDEO : viewToDisconnectedState");

        if(localVideoView != null)
        {
            localVideoView.clearImage();
            localVideoView.release();
        }
    }

    public void viewToConnectingState()
    {
        Log.d(TAG, "VIDEO : viewToConnectingState");
    }

    public void viewToConnectedState()
    {
        Log.d(TAG, "VIDEO : viewToConnectedState");
    }

    public GridLayout getViewsContainer()
    {
        return views_container;
    }

    public SurfaceViewRenderer getLocalVideoView()
    {
        return localVideoView;
    }

}
