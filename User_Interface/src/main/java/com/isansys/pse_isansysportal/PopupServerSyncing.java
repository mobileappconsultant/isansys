package com.isansys.pse_isansysportal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.isansys.common.enums.ActiveOrOldSession;
import com.isansys.common.enums.HttpOperationType;
import com.isansys.patientgateway.RowsPending;

import org.jetbrains.annotations.NotNull;


public class PopupServerSyncing extends IsansysPopupDialogFragment
{
    public interface Callback
    {
        void touchEventFromPopupWindow();

        void dismissButtonPressed();

        void retryButtonPressed();
    }

    private Context context;

    private Callback callback;

    static class TextViewGroup
    {
        TextView textViewSyncable;
        TextView textViewFailed;
        TextView textViewNonSyncable;
    }

    // Patient session information

    private final TextViewGroup textViewGroupPatientDetails = new TextViewGroup();

    private final TextViewGroup textViewGroupDeviceInfo = new TextViewGroup();

    private final TextViewGroup textViewGroupStartPatientSession = new TextViewGroup();
    private final TextViewGroup textViewGroupEndPatientSession = new TextViewGroup();

    private final TextViewGroup textViewGroupStartDeviceSession = new TextViewGroup();
    private final TextViewGroup textViewGroupEndDeviceSession = new TextViewGroup();

    private final TextViewGroup textViewGroupAuditEventsPending = new TextViewGroup();

    // Connection events
    private final TextViewGroup textViewGroupActiveSessionConnectionEvents = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionConnectionEvents = new TextViewGroup();

    // Current Active session Vital Signs measurements
    private final TextViewGroup textViewGroupActiveSessionLifetouchHeartRatesPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionLifetouchHeartBeatsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionLifetouchRespirationMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionLifetouchSetupModeSamplesPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionLifetouchRawAccelerometerModeSamplesPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionLifetouchBatteryMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionLifetouchPatientOrientationPending = new TextViewGroup();

    private final TextViewGroup textViewGroupActiveSessionLifetempTemperatureMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionLifetempBatteryMeasurementsPending = new TextViewGroup();

    private final TextViewGroup textViewGroupActiveSessionPulseOxMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionPulseOxIntermediateMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionPulseOxSetupModeSamplesPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionPulseOxBatteryMeasurementsPending = new TextViewGroup();

    private final TextViewGroup textViewGroupActiveSessionBloodPressureMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionBloodPressureBatteryMeasurementsPending = new TextViewGroup();

    private final TextViewGroup textViewGroupActiveSessionWeightScaleMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionWeightScaleBatteryMeasurementsPending = new TextViewGroup();

    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredHeartRateMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredRespirationRateMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredTemperatureMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredSpo2MeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredBloodPressureMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredWeightMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredConsciousnessLevelsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredSupplementalOxygenLevelsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredAnnotationsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredCapillaryRefillTimePending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredRespirationDistressPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredFamilyOrNurseConcernPending = new TextViewGroup();
    private final TextViewGroup textViewGroupActiveSessionManuallyEnteredUrineOutputPending = new TextViewGroup();

    private final TextViewGroup textViewGroupActiveSessionEarlyWarningScoresPending = new TextViewGroup();

    private final TextViewGroup textViewGroupActiveSessionSetupModeLogsPending = new TextViewGroup();


    // Historical Vital Signs Measurements
    private final TextViewGroup textViewGroupOldSessionLifetouchHeartRatesPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionLifetouchHeartBeatsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionLifetouchRespirationMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionLifetouchSetupModeSamplesPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionLifetouchRawAccelerometerModeSamplesPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionLifetouchBatteryMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionLifetouchPatientOrientationPending = new TextViewGroup();

    private final TextViewGroup textViewGroupOldSessionLifetempTemperatureMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionLifetempBatteryMeasurementsPending = new TextViewGroup();

    private final TextViewGroup textViewGroupOldSessionPulseOxMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionPulseOxIntermediateMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionPulseOxSetupModeSamplesPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionPulseOxBatteryMeasurementsPending = new TextViewGroup();

    private final TextViewGroup textViewGroupOldSessionBloodPressureMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionBloodPressureBatteryMeasurementsPending = new TextViewGroup();

    private final TextViewGroup textViewGroupOldSessionWeightScaleMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionWeightScaleBatteryMeasurementsPending = new TextViewGroup();

    private final TextViewGroup textViewGroupOldSessionManuallyEnteredHeartRateMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredRespirationRateMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredTemperatureMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredSpo2MeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredBloodPressureMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredWeightMeasurementsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredConsciousnessLevelsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredSupplementalOxygenLevelsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredAnnotationsPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredCapillaryRefillTimePending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredRespirationDistressPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredFamilyOrNurseConcernPending = new TextViewGroup();
    private final TextViewGroup textViewGroupOldSessionManuallyEnteredUrineOutputPending = new TextViewGroup();

    private final TextViewGroup textViewGroupOldSessionEarlyWarningScoresPending = new TextViewGroup();

    private final TextViewGroup textViewGroupOldSessionSetupModeLogsPending = new TextViewGroup();

    private final Handler webservice_individual_result_handler = new Handler();

    public PopupServerSyncing(MainActivityInterface main_activity_interface)
    {
        super(main_activity_interface);
    }

    public void setArguments(Context passed_context, Callback callback)
    {
        this.context = passed_context;

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
        dialog.setContentView(R.layout.pop_up_server_sync_status);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        textViewGroupPatientDetails.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusPatientDetailsPending);
        textViewGroupPatientDetails.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusPatientDetailsPendingButFailed);

        textViewGroupDeviceInfo.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusDeviceInfoPending);
        textViewGroupDeviceInfo.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusDeviceInfoPendingButFailed);

        textViewGroupStartPatientSession.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableStartPatientSessionPending);
        textViewGroupStartPatientSession.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableStartPatientSessionPending);
        textViewGroupStartPatientSession.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusStartPatientSessionPendingButFailed);

        textViewGroupStartDeviceSession.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableStartDeviceSessionPending);
        textViewGroupStartDeviceSession.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableStartDeviceSessionPending);
        textViewGroupStartDeviceSession.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusStartDeviceSessionPendingButFailed);

        textViewGroupEndDeviceSession.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableEndDeviceSessionPending);
        textViewGroupEndDeviceSession.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableEndDeviceSessionPending);
        textViewGroupEndDeviceSession.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusEndDeviceSessionPendingButFailed);

        textViewGroupEndPatientSession.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableEndPatientSessionPending);
        textViewGroupEndPatientSession.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableEndPatientSessionPending);
        textViewGroupEndPatientSession.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusEndPatientSessionPendingButFailed);

        // Connection events
        textViewGroupActiveSessionConnectionEvents.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionConnectionEventPending);
        textViewGroupActiveSessionConnectionEvents.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionConnectionEventPending);
        textViewGroupActiveSessionConnectionEvents.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionConnectionEventPendingButFailed);

        textViewGroupOldSessionConnectionEvents.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionConnectionEventPending);
        textViewGroupOldSessionConnectionEvents.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionConnectionEventPending);
        textViewGroupOldSessionConnectionEvents.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionConnectionEventPendingButFailed);


        // Active Session Vital Signs Data
        textViewGroupActiveSessionLifetouchHeartRatesPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionLifetouchHeartRatesPending);
        textViewGroupActiveSessionLifetouchHeartRatesPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionLifetouchHeartRatesPending);
        textViewGroupActiveSessionLifetouchHeartRatesPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionLifetouchHeartRatesPendingButFailed);

        textViewGroupActiveSessionLifetouchHeartBeatsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionLifetouchHeartBeatsPending);
        textViewGroupActiveSessionLifetouchHeartBeatsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionLifetouchHeartBeatsPending);
        textViewGroupActiveSessionLifetouchHeartBeatsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionLifetouchHeartBeatsPendingButFailed);

        textViewGroupActiveSessionLifetouchRespirationMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionLifetouchRespirationMeasurementsPending);
        textViewGroupActiveSessionLifetouchRespirationMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionLifetouchRespirationMeasurementsPending);
        textViewGroupActiveSessionLifetouchRespirationMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionLifetouchRespirationMeasurementsPendingButFailed);

        textViewGroupActiveSessionLifetouchSetupModeSamplesPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionLifetouchSetupModeSamplesPending);
        textViewGroupActiveSessionLifetouchSetupModeSamplesPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionLifetouchSetupModeSamplesPending);
        textViewGroupActiveSessionLifetouchSetupModeSamplesPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionLifetouchSetupModeSamplesPendingButFailed);

        textViewGroupActiveSessionLifetouchRawAccelerometerModeSamplesPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionLifetouchRawAccelerometerModeSamplesPending);
        textViewGroupActiveSessionLifetouchRawAccelerometerModeSamplesPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionLifetouchRawAccelerometerModeSamplesPending);
        textViewGroupActiveSessionLifetouchRawAccelerometerModeSamplesPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionLifetouchRawAccelerometerModeSamplesPendingButFailed);

        textViewGroupActiveSessionLifetouchBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionLifetouchBatteryMeasurementsPending);
        textViewGroupActiveSessionLifetouchBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionLifetouchBatteryMeasurementsPending);
        textViewGroupActiveSessionLifetouchBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionLifetouchBatteryMeasurementsPendingButFailed);


        textViewGroupActiveSessionLifetempTemperatureMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionLifetempTemperatureMeasurementsPending);
        textViewGroupActiveSessionLifetempTemperatureMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionLifetempTemperatureMeasurementsPending);
        textViewGroupActiveSessionLifetempTemperatureMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionLifetempTemperatureMeasurementsPendingButFailed);

        textViewGroupActiveSessionLifetempBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionLifetempBatteryMeasurementsPending);
        textViewGroupActiveSessionLifetempBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionLifetempBatteryMeasurementsPending);
        textViewGroupActiveSessionLifetempBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionLifetempBatteryMeasurementsPendingButFailed);


        textViewGroupActiveSessionPulseOxMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionNoninWristOxMeasurementsPending);
        textViewGroupActiveSessionPulseOxMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionNoninWristOxMeasurementsPending);
        textViewGroupActiveSessionPulseOxMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionNoninWristOxMeasurementsPendingButFailed);

        textViewGroupActiveSessionPulseOxIntermediateMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionNoninWristOxIntermediateMeasurementsPending);
        textViewGroupActiveSessionPulseOxIntermediateMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionNoninWristOxIntermediateMeasurementsPending);
        textViewGroupActiveSessionPulseOxIntermediateMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionNoninWristOxIntermediateMeasurementsPendingButFailed);

        textViewGroupActiveSessionPulseOxSetupModeSamplesPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionNoninWristOxSetupModeSamplesPending);
        textViewGroupActiveSessionPulseOxSetupModeSamplesPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionNoninWristOxSetupModeSamplesPending);
        textViewGroupActiveSessionPulseOxSetupModeSamplesPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionNoninWristOxSetupModeSamplesPendingButFailed);

        textViewGroupActiveSessionPulseOxBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionNoninWristOxBatteryMeasurementsPending);
        textViewGroupActiveSessionPulseOxBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionNoninWristOxBatteryMeasurementsPending);
        textViewGroupActiveSessionPulseOxBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionNoninWristOxBatteryMeasurementsPendingButFailed);

        textViewGroupActiveSessionLifetouchPatientOrientationPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionPatientOrientationPending);
        textViewGroupActiveSessionLifetouchPatientOrientationPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionPatientOrientationPending);
        textViewGroupActiveSessionLifetouchPatientOrientationPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionPatientOrientationPendingButFailed);

        textViewGroupActiveSessionBloodPressureMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionBloodPressureMeasurementsPending);
        textViewGroupActiveSessionBloodPressureMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionBloodPressureMeasurementsPending);
        textViewGroupActiveSessionBloodPressureMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionBloodPressureMeasurementsPendingButFailed);

        textViewGroupActiveSessionBloodPressureBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionBloodPressureBatteryMeasurementsPending);
        textViewGroupActiveSessionBloodPressureBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionBloodPressureBatteryMeasurementsPending);
        textViewGroupActiveSessionBloodPressureBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionBloodPressureBatteryMeasurementsPendingButFailed);

        textViewGroupActiveSessionWeightScaleMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionWeightScaleMeasurementsPending);
        textViewGroupActiveSessionWeightScaleMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionWeightScaleMeasurementsPending);
        textViewGroupActiveSessionWeightScaleMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionWeightScaleMeasurementsPendingButFailed);

        textViewGroupActiveSessionWeightScaleBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionWeightScaleBatteryMeasurementsPending);
        textViewGroupActiveSessionWeightScaleBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionWeightScaleBatteryMeasurementsPending);
        textViewGroupActiveSessionWeightScaleBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionWeightScaleBatteryMeasurementsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredHeartRateMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredHeartRateMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredHeartRateMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredHeartRateMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredHeartRateMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredHeartRateMeasurementsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredRespirationRateMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredRespirationRateMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredRespirationRateMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredRespirationRateMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredRespirationRateMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredRespirationRateMeasurementsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredTemperatureMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredTemperatureMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredTemperatureMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredTemperatureMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredTemperatureMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredTemperatureMeasurementsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredSpo2MeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredSpo2MeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredSpo2MeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredSpo2MeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredSpo2MeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredSpo2MeasurementsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredBloodPressureMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredBloodPressureMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredBloodPressureMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredBloodPressureMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredBloodPressureMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredBloodPressureMeasurementsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredWeightMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredWeightMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredWeightMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredWeightMeasurementsPending);
        textViewGroupActiveSessionManuallyEnteredWeightMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredWeightMeasurementsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredConsciousnessLevelsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredConsciousnessLevelsPending);
        textViewGroupActiveSessionManuallyEnteredConsciousnessLevelsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredConsciousnessLevelsPending);
        textViewGroupActiveSessionManuallyEnteredConsciousnessLevelsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredConsciousnessLevelsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredSupplementalOxygenLevelsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredSupplementalOxygenLevelsPending);
        textViewGroupActiveSessionManuallyEnteredSupplementalOxygenLevelsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredSupplementalOxygenLevelsPending);
        textViewGroupActiveSessionManuallyEnteredSupplementalOxygenLevelsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredSupplementalOxygenLevelsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredAnnotationsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredAnnotationsPending);
        textViewGroupActiveSessionManuallyEnteredAnnotationsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredAnnotationsPending);
        textViewGroupActiveSessionManuallyEnteredAnnotationsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredAnnotationsPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredCapillaryRefillTimePending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredCapillaryRefillTimePending);
        textViewGroupActiveSessionManuallyEnteredCapillaryRefillTimePending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredCapillaryRefillTimePending);
        textViewGroupActiveSessionManuallyEnteredCapillaryRefillTimePending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredCapillaryRefillTimePendingButFailed);

        textViewGroupActiveSessionManuallyEnteredRespirationDistressPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredRespirationDistressPending);
        textViewGroupActiveSessionManuallyEnteredRespirationDistressPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredRespirationDistressPending);
        textViewGroupActiveSessionManuallyEnteredRespirationDistressPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredRespirationDistressPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredFamilyOrNurseConcernPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredFamilyOrNurseConcernPending);
        textViewGroupActiveSessionManuallyEnteredFamilyOrNurseConcernPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredFamilyOrNurseConcernPending);
        textViewGroupActiveSessionManuallyEnteredFamilyOrNurseConcernPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredFamilyOrNurseConcernPendingButFailed);

        textViewGroupActiveSessionManuallyEnteredUrineOutputPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionManuallyEnteredUrineOutputPending);
        textViewGroupActiveSessionManuallyEnteredUrineOutputPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionManuallyEnteredUrineOutputPending);
        textViewGroupActiveSessionManuallyEnteredUrineOutputPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionManuallyEnteredUrineOutputPendingButFailed);

        textViewGroupActiveSessionEarlyWarningScoresPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionEarlyWarningScoresPending);
        textViewGroupActiveSessionEarlyWarningScoresPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionEarlyWarningScoresPending);
        textViewGroupActiveSessionEarlyWarningScoresPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionEarlyWarningScoresPendingButFailed);

        textViewGroupActiveSessionSetupModeLogsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionSetupModeLogsPending);
        textViewGroupActiveSessionSetupModeLogsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionSetupModeLogsPending);
        textViewGroupActiveSessionSetupModeLogsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionSetupModeLogsPendingButFailed);

        textViewGroupAuditEventsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableActiveSessionAuditTrailPending);
        textViewGroupAuditEventsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableActiveSessionAuditTrailPending);
        textViewGroupAuditEventsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusActiveSessionAuditTrailPendingButFailed);

        // Historical session Vital Signs Measurements
        textViewGroupOldSessionLifetouchHeartRatesPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionLifetouchHeartRatesPending);
        textViewGroupOldSessionLifetouchHeartRatesPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchHeartRatesPending);
        textViewGroupOldSessionLifetouchHeartRatesPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionLifetouchHeartRatesPendingButFailed);

        textViewGroupOldSessionLifetouchHeartBeatsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionLifetouchHeartBeatsPending);
        textViewGroupOldSessionLifetouchHeartBeatsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchHeartBeatsPending);
        textViewGroupOldSessionLifetouchHeartBeatsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionLifetouchHeartBeatsPendingButFailed);

        textViewGroupOldSessionLifetouchRespirationMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionLifetouchRespirationMeasurementsPending);
        textViewGroupOldSessionLifetouchRespirationMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchRespirationMeasurementsPending);
        textViewGroupOldSessionLifetouchRespirationMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionLifetouchRespirationMeasurementsPendingButFailed);

        textViewGroupOldSessionLifetouchSetupModeSamplesPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionLifetouchSetupModeSamplesPending);
        textViewGroupOldSessionLifetouchSetupModeSamplesPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchSetupModeSamplesPending);
        textViewGroupOldSessionLifetouchSetupModeSamplesPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionLifetouchSetupModeSamplesPendingButFailed);

        textViewGroupOldSessionLifetouchRawAccelerometerModeSamplesPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionLifetouchRawAccelerometerModeSamplesPending);
        textViewGroupOldSessionLifetouchRawAccelerometerModeSamplesPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchRawAccelerometerModeSamplesPending);
        textViewGroupOldSessionLifetouchRawAccelerometerModeSamplesPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionLifetouchRawAccelerometerModeSamplesPendingButFailed);

        textViewGroupOldSessionLifetouchBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionLifetouchBatteryMeasurementsPending);
        textViewGroupOldSessionLifetouchBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionLifetouchBatteryMeasurementsPending);
        textViewGroupOldSessionLifetouchBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionLifetouchBatteryMeasurementsPendingButFailed);

        textViewGroupOldSessionLifetouchPatientOrientationPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionPatientOrientationPending);
        textViewGroupOldSessionLifetouchPatientOrientationPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionPatientOrientationPending);
        textViewGroupOldSessionLifetouchPatientOrientationPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionPatientOrientationPendingButFailed);

        textViewGroupOldSessionLifetempTemperatureMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionLifetempTemperatureMeasurementsPending);
        textViewGroupOldSessionLifetempTemperatureMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionLifetempTemperatureMeasurementsPending);
        textViewGroupOldSessionLifetempTemperatureMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionLifetempTemperatureMeasurementsPendingButFailed);

        textViewGroupOldSessionLifetempBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionLifetempBatteryMeasurementsPending);
        textViewGroupOldSessionLifetempBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionLifetempBatteryMeasurementsPending);
        textViewGroupOldSessionLifetempBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionLifetempBatteryMeasurementsPendingButFailed);

        textViewGroupOldSessionPulseOxMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionNoninWristOxMeasurementsPending);
        textViewGroupOldSessionPulseOxMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionNoninWristOxMeasurementsPending);
        textViewGroupOldSessionPulseOxMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionNoninWristOxMeasurementsPendingButFailed);

        textViewGroupOldSessionPulseOxIntermediateMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionNoninWristOxIntermediateMeasurementsPending);
        textViewGroupOldSessionPulseOxIntermediateMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionNoninWristOxIntermediateMeasurementsPending);
        textViewGroupOldSessionPulseOxIntermediateMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionNoninWristOxIntermediateMeasurementsPendingButFailed);

        textViewGroupOldSessionPulseOxSetupModeSamplesPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionNoninWristOxSetupModeSamplesPending);
        textViewGroupOldSessionPulseOxSetupModeSamplesPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionNoninWristOxSetupModeSamplesPending);
        textViewGroupOldSessionPulseOxSetupModeSamplesPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionNoninWristOxSetupModeSamplesPendingButFailed);

        textViewGroupOldSessionPulseOxBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionNoninWristOxBatteryMeasurementsPending);
        textViewGroupOldSessionPulseOxBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionNoninWristOxBatteryMeasurementsPending);
        textViewGroupOldSessionPulseOxBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionNoninWristOxBatteryMeasurementsPendingButFailed);

        textViewGroupOldSessionBloodPressureMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionBloodPressureMeasurementsPending);
        textViewGroupOldSessionBloodPressureMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionBloodPressureMeasurementsPending);
        textViewGroupOldSessionBloodPressureMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionBloodPressureMeasurementsPendingButFailed);

        textViewGroupOldSessionBloodPressureBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionBloodPressureBatteryMeasurementsPending);
        textViewGroupOldSessionBloodPressureBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionBloodPressureBatteryMeasurementsPending);
        textViewGroupOldSessionBloodPressureBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionBloodPressureBatteryMeasurementsPendingButFailed);

        textViewGroupOldSessionWeightScaleMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionWeightScaleMeasurementsPending);
        textViewGroupOldSessionWeightScaleMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionWeightScaleMeasurementsPending);
        textViewGroupOldSessionWeightScaleMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionWeightScaleMeasurementsPendingButFailed);

        textViewGroupOldSessionWeightScaleBatteryMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionWeightScaleBatteryMeasurementsPending);
        textViewGroupOldSessionWeightScaleBatteryMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionWeightScaleBatteryMeasurementsPending);
        textViewGroupOldSessionWeightScaleBatteryMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionWeightScaleBatteryMeasurementsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredHeartRateMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredHeartRateMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredHeartRateMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredHeartRateMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredHeartRateMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredHeartRateMeasurementsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredRespirationRateMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredRespirationRateMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredRespirationRateMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredRespirationRateMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredRespirationRateMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredRespirationRateMeasurementsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredTemperatureMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredTemperatureMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredTemperatureMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredTemperatureMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredTemperatureMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredTemperatureMeasurementsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredSpo2MeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredSpo2MeasurementsPending);
        textViewGroupOldSessionManuallyEnteredSpo2MeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredSpo2MeasurementsPending);
        textViewGroupOldSessionManuallyEnteredSpo2MeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredSpo2MeasurementsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredBloodPressureMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredBloodPressureMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredBloodPressureMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredBloodPressureMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredBloodPressureMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredBloodPressureMeasurementsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredWeightMeasurementsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredWeightMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredWeightMeasurementsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredWeightMeasurementsPending);
        textViewGroupOldSessionManuallyEnteredWeightMeasurementsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredWeightMeasurementsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredConsciousnessLevelsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredConsciousnessLevelsPending);
        textViewGroupOldSessionManuallyEnteredConsciousnessLevelsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredConsciousnessLevelsPending);
        textViewGroupOldSessionManuallyEnteredConsciousnessLevelsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredConsciousnessLevelsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredSupplementalOxygenLevelsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredSupplementalOxygenLevelsPending);
        textViewGroupOldSessionManuallyEnteredSupplementalOxygenLevelsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredSupplementalOxygenLevelsPending);
        textViewGroupOldSessionManuallyEnteredSupplementalOxygenLevelsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredSupplementalOxygenLevelsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredAnnotationsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredAnnotationsPending);
        textViewGroupOldSessionManuallyEnteredAnnotationsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredAnnotationsPending);
        textViewGroupOldSessionManuallyEnteredAnnotationsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredAnnotationsPendingButFailed);

        textViewGroupOldSessionManuallyEnteredCapillaryRefillTimePending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredCapillaryRefillTimePending);
        textViewGroupOldSessionManuallyEnteredCapillaryRefillTimePending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredCapillaryRefillTimePending);
        textViewGroupOldSessionManuallyEnteredCapillaryRefillTimePending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredCapillaryRefillTimePendingButFailed);

        textViewGroupOldSessionManuallyEnteredRespirationDistressPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredRespirationDistressPending);
        textViewGroupOldSessionManuallyEnteredRespirationDistressPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredRespirationDistressPending);
        textViewGroupOldSessionManuallyEnteredRespirationDistressPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredRespirationDistressPendingButFailed);

        textViewGroupOldSessionManuallyEnteredFamilyOrNurseConcernPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredFamilyOrNurseConcernPending);
        textViewGroupOldSessionManuallyEnteredFamilyOrNurseConcernPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredFamilyOrNurseConcernPending);
        textViewGroupOldSessionManuallyEnteredFamilyOrNurseConcernPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredFamilyOrNurseConcernPendingButFailed);

        textViewGroupOldSessionManuallyEnteredUrineOutputPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionManuallyEnteredUrineOutputPending);
        textViewGroupOldSessionManuallyEnteredUrineOutputPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionManuallyEnteredUrineOutputPending);
        textViewGroupOldSessionManuallyEnteredUrineOutputPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionManuallyEnteredUrineOutputPendingButFailed);

        textViewGroupOldSessionEarlyWarningScoresPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionEarlyWarningScoresPending);
        textViewGroupOldSessionEarlyWarningScoresPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionEarlyWarningScoresPending);
        textViewGroupOldSessionEarlyWarningScoresPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionEarlyWarningScoresPendingButFailed);

        textViewGroupOldSessionSetupModeLogsPending.textViewSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusSyncableOldSessionSetupModeLogsPending);
        textViewGroupOldSessionSetupModeLogsPending.textViewNonSyncable = dialog.findViewById(R.id.textViewPopupServerSyncStatusNonSyncableOldSessionSetupModeLogsPending);
        textViewGroupOldSessionSetupModeLogsPending.textViewFailed = dialog.findViewById(R.id.textViewPopupServerSyncStatusOldSessionSetupModeLogsPendingButFailed);


        Button btnDismiss = dialog.findViewById(R.id.dismiss);
        btnDismiss.setOnClickListener(v -> dismissWindow());


        ImageView imageRetry = dialog.findViewById(R.id.retry);
        imageRetry.setOnClickListener(v -> retryButtonPressed());

        return dialog;
    }


    public void dismissWindow()
    {
        callback.dismissButtonPressed();

        dismissPopupIfVisible();
    }


    private void dismissPopupIfVisible()
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                getDialog().dismiss();
            }
        }
    }


    public void retryButtonPressed()
    {
        callback.retryButtonPressed();
    }


    public void updatePopupServerWebserviceResult(HttpOperationType http_operation_type, ActiveOrOldSession active_or_old_session, boolean webservice_result)
    {
        switch (http_operation_type)
        {
            case INVALID:
                break;

            case PATIENT_DETAILS:
            {
                showIndividualServerWebserviceResult(textViewGroupPatientDetails, webservice_result, active_or_old_session);
            }
            break;

            case DEVICE_INFO:
            {
                showIndividualServerWebserviceResult(textViewGroupDeviceInfo, webservice_result, active_or_old_session);
            }
            break;

            case START_PATIENT_SESSION:
            {
                showIndividualServerWebserviceResult(textViewGroupStartPatientSession, webservice_result, active_or_old_session);
            }
            break;

            case START_DEVICE_SESSION:
            {
                showIndividualServerWebserviceResult(textViewGroupStartDeviceSession, webservice_result, active_or_old_session);
            }
            break;

            case END_DEVICE_SESSION:
            {
                showIndividualServerWebserviceResult(textViewGroupEndDeviceSession, webservice_result, active_or_old_session);
            }
            break;

            case END_PATIENT_SESSION:
            {
                showIndividualServerWebserviceResult(textViewGroupEndPatientSession, webservice_result, active_or_old_session);
            }
            break;

            case AUDITABLE_EVENTS:
            {
                showIndividualServerWebserviceResult(textViewGroupAuditEventsPending, webservice_result, active_or_old_session);
            }
            break;

            case CONNECTION_EVENT:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionConnectionEvents, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionConnectionEvents, webservice_result, active_or_old_session);
                }
            }
            break;

            case LIFETOUCH_HEART_RATE:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionLifetouchHeartRatesPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionLifetouchHeartRatesPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case LIFETOUCH_RESPIRATION_RATE:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionLifetouchRespirationMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionLifetouchRespirationMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case LIFETOUCH_HEART_BEAT:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionLifetouchHeartBeatsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionLifetouchHeartBeatsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case LIFETOUCH_SETUP_MODE_SAMPLE:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionLifetouchSetupModeSamplesPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionLifetouchSetupModeSamplesPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionLifetouchRawAccelerometerModeSamplesPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionLifetouchRawAccelerometerModeSamplesPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case LIFETOUCH_BATTERY:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionLifetouchBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionLifetouchBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case LIFETOUCH_PATIENT_ORIENTATION:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionLifetouchPatientOrientationPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionLifetouchPatientOrientationPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case LIFETEMP_TEMPERATURE:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionLifetempTemperatureMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionLifetempTemperatureMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case LIFETEMP_BATTERY:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionLifetempBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionLifetempBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case PULSE_OX_MEASUREMENT:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionPulseOxMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionPulseOxMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case PULSE_OX_INTERMEDIATE_MEASUREMENT:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionPulseOxIntermediateMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionPulseOxIntermediateMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case PULSE_OX_SETUP_MODE_SAMPLE:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionPulseOxSetupModeSamplesPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionPulseOxSetupModeSamplesPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case PULSE_OX_BATTERY:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionPulseOxBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionPulseOxBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case BLOOD_PRESSURE_MEASUREMENT:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionBloodPressureMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionBloodPressureMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case BLOOD_PRESSURE_BATTERY:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionBloodPressureBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionBloodPressureBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case WEIGHT_SCALE_MEASUREMENT:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionWeightScaleMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionWeightScaleMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case WEIGHT_SCALE_BATTERY:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionWeightScaleBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionWeightScaleBatteryMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case PATIENT_SESSION_FULLY_SYNCED:
            {

            }
            break;

            case MANUALLY_ENTERED_HEART_RATES:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredHeartRateMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredHeartRateMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_RATES:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredRespirationRateMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredRespirationRateMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_TEMPERATURES:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredTemperatureMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredTemperatureMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredSpo2MeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredSpo2MeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredBloodPressureMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredBloodPressureMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredWeightMeasurementsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredWeightMeasurementsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredConsciousnessLevelsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredConsciousnessLevelsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredSupplementalOxygenLevelsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredSupplementalOxygenLevelsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_ANNOTATIONS:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredAnnotationsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredAnnotationsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredCapillaryRefillTimePending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredCapillaryRefillTimePending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredRespirationDistressPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredRespirationDistressPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredFamilyOrNurseConcernPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredFamilyOrNurseConcernPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionManuallyEnteredUrineOutputPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionManuallyEnteredUrineOutputPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case EARLY_WARNING_SCORES:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionEarlyWarningScoresPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionEarlyWarningScoresPending, webservice_result, active_or_old_session);
                }
            }
            break;

            case SETUP_MODE_LOG:
            {
                if (active_or_old_session == ActiveOrOldSession.ACTIVE_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupActiveSessionSetupModeLogsPending, webservice_result, active_or_old_session);
                }
                else if (active_or_old_session == ActiveOrOldSession.OLD_SESSION)
                {
                    showIndividualServerWebserviceResult(textViewGroupOldSessionSetupModeLogsPending, webservice_result, active_or_old_session);
                }
            }
            break;

            // No Green/Red indicator for these yet
            case BED_DETAILS_LIST:
            case WARD_DETAILS_LIST:
            case SERVER_PING:
            case CHECK_DEVICE_DETAILS:
            case CHECK_START_PATIENT_SESSION:
            case CHECK_END_PATIENT_SESSION:
            case CHECK_START_DEVICE_SESSION:
            case CHECK_END_DEVICE_SESSION:
            case CHECK_PATIENT_ID:
            case GET_DEFAULT_EARLY_WARNING_SCORES_LIST:
                break;
        }
    }


    private void showIndividualServerWebserviceResult(final TextViewGroup textViewGroup, boolean webservice_result, ActiveOrOldSession activeOrOldSession)
    {
        final ActiveOrOldSession active_or_old_session = activeOrOldSession;
        final boolean result = webservice_result;

        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                int colour;

                if (webservice_result)
                {
                    colour = ContextCompat.getColor(context, R.color.green);
                    textViewGroup.textViewSyncable.setBackgroundColor(colour);
                }
                else
                {
                    colour = ContextCompat.getColor(context, R.color.red);
                    textViewGroup.textViewFailed.setBackgroundColor(colour);
                }


                // Hide the indicator after 1 second
                webservice_individual_result_handler.postDelayed(() -> {
                    switch (active_or_old_session)
                    {
                        case ACTIVE_SESSION:
                        case INVALID:
                        {
                            if (result)
                            {
                                textViewGroup.textViewSyncable.setBackgroundColor(ContextCompat.getColor(context, R.color.pop_up_server_sync_active_session_background_colour));
                            }
                            else
                            {
                                textViewGroup.textViewFailed.setBackgroundColor(ContextCompat.getColor(context, R.color.pop_up_server_sync_active_session_background_colour));
                            }
                        }
                        break;

                        case OLD_SESSION:
                        {
                            if (result)
                            {
                                textViewGroup.textViewSyncable.setBackgroundColor(ContextCompat.getColor(context, R.color.pop_up_server_sync_historical_session_background_colour));
                            }
                            else
                            {
                                textViewGroup.textViewFailed.setBackgroundColor(ContextCompat.getColor(context, R.color.pop_up_server_sync_historical_session_background_colour));
                            }
                        }
                        break;
                    }
                }, DateUtils.SECOND_IN_MILLIS);
            }
        }
    }


    public void showPatientDetailsRowsPending(RowsPending rows_pending)
    {
        showOnPopupForSessionInfoIfVisible(rows_pending, textViewGroupPatientDetails);
    }


    public void showDeviceInfoRowsPending(RowsPending rows_pending)
    {
        showOnPopupForSessionInfoIfVisible(rows_pending, textViewGroupDeviceInfo);
    }


    public void showStartPatientSessionRowsPending(RowsPending rows_pending)
    {
        showOnPopupForSessionInfoIfVisible(rows_pending, textViewGroupStartPatientSession);
    }


    public void showStartDeviceSessionRowsPending(RowsPending rows_pending)
    {
        showOnPopupForSessionInfoIfVisible(rows_pending, textViewGroupStartDeviceSession);
    }


    public void showEndDeviceSessionRowsPending(RowsPending rows_pending)
    {
        showOnPopupForSessionInfoIfVisible(rows_pending, textViewGroupEndDeviceSession);
    }


    public void showEndPatientSessionRowsPending(RowsPending rows_pending)
    {
        showOnPopupForSessionInfoIfVisible(rows_pending, textViewGroupEndPatientSession);
    }


    public void showAuditTrailRowsPending(RowsPending rows_pending)
    {
        showOnPopupForSessionInfoIfVisible(rows_pending, textViewGroupAuditEventsPending);
    }


    public void showActiveSessionConnectionEventRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionConnectionEvents);
    }
    public void showOldSessionConnectionEventRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionConnectionEvents);
    }


    public void showActiveSessionLifetouchHeartRateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionLifetouchHeartRatesPending);
    }
    public void showOldSessionLifetouchHeartRateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionLifetouchHeartRatesPending);
    }


    public void showActiveSessionLifetouchHeartBeatRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionLifetouchHeartBeatsPending);
    }
    public void showOldSessionLifetouchHeartBeatRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionLifetouchHeartBeatsPending);
    }


    public void showActiveSessionLifetouchRespirationRateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionLifetouchRespirationMeasurementsPending);
    }
    public void showOldSessionLifetouchRespirationRateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionLifetouchRespirationMeasurementsPending);
    }


    public void showActiveSessionLifetouchSetupModeSampleRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionLifetouchSetupModeSamplesPending);
    }
    public void showOldSessionLifetouchSetupModeSampleRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionLifetouchSetupModeSamplesPending);
    }


    public void showActiveSessionLifetouchRawAccelerometerModeSampleRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionLifetouchRawAccelerometerModeSamplesPending);
    }
    public void showOldSessionLifetouchRawAccelerometerModeSampleRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionLifetouchRawAccelerometerModeSamplesPending);
    }


    public void showActiveSessionLifetouchBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionLifetouchBatteryMeasurementsPending);
    }
    public void showOldSessionLifetouchBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionLifetouchBatteryMeasurementsPending);
    }


    public void showActiveSessionLifetouchPatientOrientationRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionLifetouchPatientOrientationPending);
    }
    public void showOldSessionLifetouchPatientOrientationRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionLifetouchPatientOrientationPending);
    }


    public void showActiveSessionLifetempTemperatureRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionLifetempTemperatureMeasurementsPending);
    }
    public void showOldSessionLifetempTemperatureRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionLifetempTemperatureMeasurementsPending);
    }


    public void showActiveSessionLifetempBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionLifetempBatteryMeasurementsPending);
    }
    public void showOldSessionLifetempBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionLifetempBatteryMeasurementsPending);
    }


    public void showActiveSessionPulseOxSpO2RowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionPulseOxMeasurementsPending);
    }
    public void showOldSessionPulseOxSpO2RowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionPulseOxMeasurementsPending);
    }


    public void showActiveSessionPulseOxIntermediateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionPulseOxIntermediateMeasurementsPending);
    }
    public void showOldSessionPulseOxIntermediateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionPulseOxIntermediateMeasurementsPending);
    }


    public void showActiveSessionPulseOxSetupModeSampleRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionPulseOxSetupModeSamplesPending);
    }
    public void showOldSessionPulseOxSetupModeSampleRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionPulseOxSetupModeSamplesPending);
    }


    public void showActiveSessionPulseOxBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionPulseOxBatteryMeasurementsPending);
    }
    public void showOldSessionPulseOxBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionPulseOxBatteryMeasurementsPending);
    }


    public void showActiveSessionBloodPressureRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionBloodPressureMeasurementsPending);
    }
    public void showOldSessionBloodPressureRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionBloodPressureMeasurementsPending);
    }


    public void showActiveSessionBloodPressureBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionBloodPressureBatteryMeasurementsPending);
    }
    public void showOldSessionBloodPressureBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionBloodPressureBatteryMeasurementsPending);
    }


    public void showActiveSessionWeightScaleRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionWeightScaleMeasurementsPending);
    }
    public void showOldSessionWeightScaleRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionWeightScaleMeasurementsPending);
    }


    public void showActiveSessionWeightScaleBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionWeightScaleBatteryMeasurementsPending);
    }
    public void showOldSessionWeightScaleBatteryRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionWeightScaleBatteryMeasurementsPending);
    }


    public void showActiveSessionManuallyEnteredHeartRateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredHeartRateMeasurementsPending);
    }
    public void showOldSessionManuallyEnteredHeartRateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredHeartRateMeasurementsPending);
    }


    public void showActiveSessionManuallyEnteredRespirationRateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredRespirationRateMeasurementsPending);
    }
    public void showOldSessionManuallyEnteredRespirationRateRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredRespirationRateMeasurementsPending);
    }


    public void showActiveSessionManuallyEnteredTemperatureRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredTemperatureMeasurementsPending);
    }
    public void showOldSessionManuallyEnteredTemperatureRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredTemperatureMeasurementsPending);
    }


    public void showActiveSessionManuallyEnteredSpO2RowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredSpo2MeasurementsPending);
    }
    public void showOldSessionManuallyEnteredSpO2RowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredSpo2MeasurementsPending);
    }


    public void showActiveSessionManuallyEnteredBloodPressureRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredBloodPressureMeasurementsPending);
    }
    public void showOldSessionManuallyEnteredBloodPressureRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredBloodPressureMeasurementsPending);
    }


    public void showActiveSessionManuallyEnteredWeightRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredWeightMeasurementsPending);
    }
    public void showOldSessionManuallyEnteredWeightRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredWeightMeasurementsPending);
    }


    public void showActiveSessionManuallyEnteredConsciousnessLevelRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredConsciousnessLevelsPending);
    }
    public void showOldSessionManuallyEnteredConsciousnessLevelRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredConsciousnessLevelsPending);
    }


    public void showActiveSessionManuallyEnteredSupplementalOxygenLevelRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredSupplementalOxygenLevelsPending);
    }
    public void showOldSessionManuallyEnteredSupplementalOxygenLevelRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredSupplementalOxygenLevelsPending);
    }


    public void showActiveSessionManuallyEnteredAnnotationRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredAnnotationsPending);
    }
    public void showOldSessionManuallyEnteredAnnotationRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredAnnotationsPending);
    }


    public void showActiveSessionManuallyEnteredCapillaryRefillTimeRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredCapillaryRefillTimePending);
    }
    public void showOldSessionManuallyEnteredCapillaryRefillTimeRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredCapillaryRefillTimePending);
    }


    public void showActiveSessionManuallyEnteredRespirationDistressRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredRespirationDistressPending);
    }
    public void showOldSessionManuallyEnteredRespirationDistressRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredRespirationDistressPending);
    }


    public void showActiveSessionManuallyEnteredFamilyOrNurseConcernRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredFamilyOrNurseConcernPending);
    }
    public void showOldSessionManuallyEnteredFamilyOrNurseConcernRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredFamilyOrNurseConcernPending);
    }


    public void showActiveSessionManuallyEnteredUrineOutputRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionManuallyEnteredUrineOutputPending);
    }
    public void showOldSessionManuallyEnteredUrineOutputRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionManuallyEnteredUrineOutputPending);
    }


    public void showActiveSessionEarlyWarningScoreRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionEarlyWarningScoresPending);
    }
    public void showOldSessionEarlyWarningScoreRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionEarlyWarningScoresPending);
    }


    public void showActiveSessionSetupModeLogRowsPending(RowsPending rows_pending)
    {
        showOnPopupForActiveSessionIfVisible(rows_pending, textViewGroupActiveSessionSetupModeLogsPending);
    }
    public void showOldSessionSetupModeLogRowsPending(RowsPending rows_pending)
    {
        showOnPopupForOldSessionIfVisible(rows_pending, textViewGroupOldSessionSetupModeLogsPending);
    }


    private void showOnPopupIfVisible(RowsPending rows_pending, TextViewGroup textViewGroup, ActiveOrOldSession activeOrOldSession)
    {
        if ((getDialog() != null) && (getContext() != null))
        {
            if (getDialog().isShowing())
            {
                int backgroundColour;

                if (activeOrOldSession == ActiveOrOldSession.OLD_SESSION)
                {
                    backgroundColour = ContextCompat.getColor(getContext(), R.color.pop_up_server_sync_historical_session_background_colour);
                }
                else
                {
                    // Active or Invalid
                    backgroundColour = ContextCompat.getColor(getContext(), R.color.pop_up_server_sync_active_session_background_colour);
                }

                textViewGroup.textViewSyncable.setBackgroundColor(backgroundColour);
                textViewGroup.textViewSyncable.setText(String.valueOf(rows_pending.rows_pending_syncable));

                if (textViewGroup.textViewNonSyncable != null)
                {
                    textViewGroup.textViewNonSyncable.setBackgroundColor(backgroundColour);

                    if (rows_pending.rows_pending_non_syncable > 0)
                    {
                        textViewGroup.textViewNonSyncable.setText(String.valueOf(rows_pending.rows_pending_non_syncable));
                    }
                    else
                    {
                        textViewGroup.textViewNonSyncable.setText("");
                    }
                }

                if (textViewGroup.textViewFailed != null)
                {
                    textViewGroup.textViewFailed.setBackgroundColor(backgroundColour);

                    if (rows_pending.rows_pending_but_failed > 0)
                    {
                        textViewGroup.textViewFailed.setText(String.valueOf(rows_pending.rows_pending_but_failed));
                    }
                    else
                    {
                        textViewGroup.textViewFailed.setText("");
                    }
                }
            }
        }
    }


    private void showOnPopupForActiveSessionIfVisible(RowsPending rows_pending, TextViewGroup textViewGroup)
    {
        showOnPopupIfVisible(rows_pending, textViewGroup, ActiveOrOldSession.ACTIVE_SESSION);
    }


    private void showOnPopupForOldSessionIfVisible(RowsPending rows_pending, TextViewGroup textViewGroup)
    {
        showOnPopupIfVisible(rows_pending, textViewGroup, ActiveOrOldSession.OLD_SESSION);
    }


    private void showOnPopupForSessionInfoIfVisible(RowsPending rows_pending, TextViewGroup textViewGroup)
    {
        showOnPopupIfVisible(rows_pending, textViewGroup, ActiveOrOldSession.INVALID);
    }


    @Override
    public void onDismiss(@NotNull DialogInterface dialog)
    {
        setShowNotInProgress();

        callback.dismissButtonPressed();

        super.onDismiss(dialog);
    }
}
