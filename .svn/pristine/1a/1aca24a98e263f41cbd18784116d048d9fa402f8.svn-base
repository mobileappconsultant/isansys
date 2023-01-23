package com.isansys.pse_isansysportal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;


public class PopupPatientName extends IsansysPopupDialogFragment
{
    public interface Callback
    {
        void touchEventFromPopupWindow();

        void correctPatientDetailsButtonPressed(FullPatientDetails fullPatientDetails);
        void incorrectPatientDetailsButtonPressed();

        void usingCaseId();
    }

    private final String TAG = PopupPatientName.class.getName();

    private Callback callback;

    private FullPatientDetails fullPatientDetails;

    private LinearLayout linearLayoutLookupStatus;
    private TextView textViewPatientIdLookupStatus;
    private ProgressBar progressBarTalkingToServer;
    private LinearLayout linearLayoutBackAndCaseIdButtons;
    private Button buttonGoBackToEditing;
    private Button buttonUseCaseId;

    private final Handler server_response_timer = new Handler();

    private LinearLayout linearLayoutShowPatientDetails;
    private TextView textViewPopupFirstName;
    private TextView textViewPopupLastName;
    private TextView textViewPopupDateOfBirth;
    private TextView textViewPopupGender;

    private enum Stage
    {
        LOOKING_UP,
        GOT_SERVER_RESPONSE,
        SERVER_RETURNED_ERROR,
        SERVER_TIMEOUT
    }


    public PopupPatientName(MainActivityInterface main_activity_interface)
    {
        super(main_activity_interface);
    }

    public void setArguments(Callback callback)
    {
        this.callback = callback;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Dialog dialog = new Dialog(getActivity())
        {
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    callback.touchEventFromPopupWindow();
                }

                return super.dispatchTouchEvent(event);
            }
        };

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_patient_name);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        fullPatientDetails = new FullPatientDetails();

        linearLayoutShowPatientDetails = dialog.findViewById(R.id.linearLayoutShowPatientDetails);
        linearLayoutShowPatientDetails.setVisibility(View.GONE);

        textViewPopupFirstName = dialog.findViewById(R.id.textViewPopupFirstName);
        textViewPopupLastName = dialog.findViewById(R.id.textViewPopupLastName);
        textViewPopupDateOfBirth = dialog.findViewById(R.id.textViewPopupDateOfBirth);
        textViewPopupGender = dialog.findViewById(R.id.textViewPopupGender);

        textViewPopupFirstName.setText(fullPatientDetails.firstName);
        textViewPopupLastName.setText(fullPatientDetails.lastName);

        if (!fullPatientDetails.dateOfBirth.isEmpty())
        {
            textViewPopupDateOfBirth.setText(fullPatientDetails.dateOfBirth);
        }

        if (!fullPatientDetails.gender.isEmpty())
        {
            textViewPopupGender.setText(fullPatientDetails.gender);
        }

        Button buttonCorrectPatientDetails = dialog.findViewById(R.id.buttonCorrectPatientDetails);
        buttonCorrectPatientDetails.setOnClickListener(v -> {
            callback.correctPatientDetailsButtonPressed(fullPatientDetails);
            dismissPopupIfVisible();
        });

        Button buttonIncorrectPatientDetails = dialog.findViewById(R.id.buttonIncorrectPatientDetails);
        buttonIncorrectPatientDetails.setOnClickListener(v -> {
            callback.incorrectPatientDetailsButtonPressed();
            dismissPopupIfVisible();
        });


        linearLayoutLookupStatus = dialog.findViewById(R.id.linearLayoutLookupStatus);

        textViewPatientIdLookupStatus = dialog.findViewById(R.id.textViewPatientIdLookupStatus);
        progressBarTalkingToServer = dialog.findViewById(R.id.serverResponseWaitingProgressBar);

        linearLayoutBackAndCaseIdButtons = dialog.findViewById(R.id.linearLayoutBackAndCaseIdButtons);

        buttonGoBackToEditing = dialog.findViewById(R.id.buttonGoBackToEditing);
        buttonGoBackToEditing.setOnClickListener(v -> dismissPopupIfVisible());

        buttonUseCaseId = dialog.findViewById(R.id.buttonUseCaseId);
        buttonUseCaseId.setOnClickListener(v -> {
            callback.usingCaseId();
            dismissPopupIfVisible();
        });

        startServerResponseTimer();

        setPageContentVisibilitiesToStage(Stage.LOOKING_UP);

        return dialog;
    }


    private void startServerResponseTimer()
    {
        server_response_timer.postDelayed(this::showNoResponseFromExternalServer, 10 * DateUtils.SECOND_IN_MILLIS);
    }


    public void stopServerResponseTimerAsErrorOccurred()
    {
        server_response_timer.removeCallbacksAndMessages(null);

        setPageContentVisibilitiesToStage(Stage.SERVER_RETURNED_ERROR);
    }


    public void stopServerResponseTimerAsServerResponseReceived()
    {
        server_response_timer.removeCallbacksAndMessages(null);

        setPageContentVisibilitiesToStage(Stage.GOT_SERVER_RESPONSE);
    }


    private void setPageContentVisibilitiesToStage(Stage stage)
    {
        Log.d(TAG, "setPageContentVisibilitiesToStage : " + stage.toString());

        switch(stage)
        {
            case LOOKING_UP:
            {
                showPatientIdLookupStatus(getResources().getString(R.string.starting_patient_name_lookup));

                // Show please wait and swirly progress indicator
                linearLayoutLookupStatus.setVisibility(View.VISIBLE);
                textViewPatientIdLookupStatus.setVisibility(View.VISIBLE);
                progressBarTalkingToServer.setVisibility(View.VISIBLE);

                // Hide everything else
                linearLayoutBackAndCaseIdButtons.setVisibility(View.GONE);
                linearLayoutShowPatientDetails.setVisibility(View.GONE);
            }
            break;

            case GOT_SERVER_RESPONSE:
            {
                linearLayoutShowPatientDetails.setVisibility(View.VISIBLE);

                // Everything else invisible
                linearLayoutLookupStatus.setVisibility(View.GONE);
            }
            break;

            case SERVER_RETURNED_ERROR:
            {
                linearLayoutLookupStatus.setVisibility(View.VISIBLE);

                // Show timeout message and initial instructions.
                textViewPatientIdLookupStatus.setText(getResources().getString(R.string.server_returned_an_error));
                textViewPatientIdLookupStatus.setVisibility(View.VISIBLE);
                linearLayoutBackAndCaseIdButtons.setVisibility(View.VISIBLE);

                // Everything else invisible
                linearLayoutShowPatientDetails.setVisibility(View.GONE);
                progressBarTalkingToServer.setVisibility(View.GONE);
            }
            break;

            case SERVER_TIMEOUT:
            {
                linearLayoutLookupStatus.setVisibility(View.VISIBLE);

                // Show timeout message and initial instructions.
                textViewPatientIdLookupStatus.setText(getResources().getString(R.string.server_timed_out));
                textViewPatientIdLookupStatus.setVisibility(View.VISIBLE);
                linearLayoutBackAndCaseIdButtons.setVisibility(View.VISIBLE);

                // Everything else invisible
                progressBarTalkingToServer.setVisibility(View.GONE);
                linearLayoutShowPatientDetails.setVisibility(View.GONE);
            }
            break;
        }
    }


    public void showRequestSentToHospitalServer()
    {
        showPatientIdLookupStatus(getResources().getString(R.string.request_sent_to_hospital_server));
    }


    public void showPatientCaseIdNotFound()
    {
        linearLayoutLookupStatus.setVisibility(View.VISIBLE);
        linearLayoutShowPatientDetails.setVisibility(View.GONE);

        progressBarTalkingToServer.setVisibility(View.GONE);
        buttonGoBackToEditing.setVisibility(View.VISIBLE);
        buttonUseCaseId.setVisibility(View.GONE);
        linearLayoutBackAndCaseIdButtons.setVisibility(View.VISIBLE);

        showPatientIdLookupStatus(getResources().getString(R.string.patient_id_not_found));
    }


    public void showNoResponseFromExternalServer()
    {
        setPageContentVisibilitiesToStage(Stage.SERVER_TIMEOUT);
    }


    public void showPatientIdLookupStatus(String display_string)
    {
        textViewPatientIdLookupStatus.setText(display_string);
    }


    public void setPatientInfo(FullPatientDetails fullPatientDetails)
    {
        this.fullPatientDetails = fullPatientDetails;

        if((getDialog() != null) && (getDialog().isShowing()))
        {
            textViewPopupFirstName.setText(fullPatientDetails.firstName);
            textViewPopupLastName.setText(fullPatientDetails.lastName);
            textViewPopupDateOfBirth.setText(fullPatientDetails.dateOfBirth);
            textViewPopupGender.setText(fullPatientDetails.gender);
        }
    }

    public void dismissPopupIfVisible()
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                getDialog().dismiss();
            }
        }
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog)
    {
        setShowNotInProgress();

        super.onDismiss(dialog);
    }
}
