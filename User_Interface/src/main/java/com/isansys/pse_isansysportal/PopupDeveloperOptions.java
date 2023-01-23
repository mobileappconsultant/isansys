package com.isansys.pse_isansysportal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class PopupDeveloperOptions extends IsansysPopupDialogFragment
{
    public interface Callback
    {
        void touchEventFromPopupWindow();

        void resetBluetoothAdaptor(boolean remove_devices);

        void disableBluetoothAdapter();
        void enableBluetoothAdapter();

        void disableWifi();
        void enableWifi();

        void dismissButtonPressed();

        void getSweetblueDiagnostics();

        void resetDatabaseFailedToSendStatus();

        void crashPatientGatewayOnDemand();
        void crashUserInterfaceOnDemand();

        void startNoninBlePlaybackSimulationFromFile();

        void enableServerSyncingTestMode();
        void disableServerSyncingTestMode();
    }

    private Callback callback;

    private TextView textViewPostCompleteBleReset;
    private TextView textViewPostBleResetWithoutRemovingDevices;

    public PopupDeveloperOptions(MainActivityInterface main_activity_interface)
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
        dialog.setContentView(R.layout.pop_up_developer_menu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        Button buttonPopupDismiss = dialog.findViewById(R.id.button_developer_popup_dismiss);
        buttonPopupDismiss.setOnClickListener(v -> {
            callback.dismissButtonPressed();

            dismissPopupIfVisible();
        });

        Button buttonResetBluetooth = dialog.findViewById(R.id.buttonResetBluetooth);
        buttonResetBluetooth.setOnClickListener(v -> callback.resetBluetoothAdaptor(false));

        Button buttonRemoveDevicesAndResetBluetooth = dialog.findViewById(R.id.buttonRemoveDevicesAndResetBluetooth);
        buttonRemoveDevicesAndResetBluetooth.setOnClickListener(v -> callback.resetBluetoothAdaptor(true));

        Button buttonDisableBluetoothAdapter = dialog.findViewById(R.id.buttonDisableBluetoothAdapter);
        buttonDisableBluetoothAdapter.setOnClickListener(v -> callback.disableBluetoothAdapter());

        Button buttonEnableBluetoothAdapter = dialog.findViewById(R.id.buttonEnableBluetoothAdapter);
        buttonEnableBluetoothAdapter.setOnClickListener(v -> callback.enableBluetoothAdapter());

        Button buttonDisableWifi = dialog.findViewById(R.id.buttonDisableWifi);
        buttonDisableWifi.setOnClickListener(v -> callback.disableWifi());

        Button buttonEnableWifi = dialog.findViewById(R.id.buttonEnableWifi);
        buttonEnableWifi.setOnClickListener(v -> callback.enableWifi());

        Button buttonResetDatabaseFailedToSendStatus = dialog.findViewById(R.id.buttonResetDatabaseFailedtoSendStatus);
        buttonResetDatabaseFailedToSendStatus.setOnClickListener(v -> callback.resetDatabaseFailedToSendStatus());

        Button buttonCrashPatientGatewayOnDemand = dialog.findViewById(R.id.buttonCrashPatientGatewayOnDemand);
        buttonCrashPatientGatewayOnDemand.setOnClickListener(v -> callback.crashPatientGatewayOnDemand());

        Button buttonCrashUserInterfaceOnDemand = dialog.findViewById(R.id.buttonCrashUserInterfaceOnDemand);
        buttonCrashUserInterfaceOnDemand.setOnClickListener(v -> callback.crashUserInterfaceOnDemand());

        Button buttonStartNoninPlaybackSimulationFromFile = dialog.findViewById(R.id.buttonStartNoninBlePlaybackFromFile);
        buttonStartNoninPlaybackSimulationFromFile.setOnClickListener(v -> {
            callback.startNoninBlePlaybackSimulationFromFile();

            dismissPopupIfVisible();
        });

        textViewPostCompleteBleReset = dialog.findViewById(R.id.textViewPostCompleteBleReset);
        textViewPostBleResetWithoutRemovingDevices = dialog.findViewById(R.id.textViewPostBleResetWithoutRemovingDevices);

        callback.getSweetblueDiagnostics();

        Button buttonServerSyncingTestModeEnable = dialog.findViewById(R.id.buttonEnableServerSyncingTestMode);
        buttonServerSyncingTestModeEnable.setOnClickListener(v -> callback.enableServerSyncingTestMode());

        Button buttonServerSyncingTestModeDisable = dialog.findViewById(R.id.buttonDisableServerSyncingTestMode);
        buttonServerSyncingTestModeDisable.setOnClickListener(v -> callback.disableServerSyncingTestMode());

        return dialog;
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


    private void showValue(final TextView textview, int value)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                textview.setText(String.valueOf(value));
            }
        }
    }


    public void showPostCompleteBleReset(int value)
    {
        showValue(textViewPostCompleteBleReset, value);
    }


    public void showPostBleResetWithoutRemovingDevices(int value)
    {
        showValue(textViewPostBleResetWithoutRemovingDevices, value);
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog)
    {
        setShowNotInProgress();

        callback.dismissButtonPressed();

        super.onDismiss(dialog);
    }
}
