package com.isansys.pse_isansysportal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

public class PopupWifiStatus extends IsansysPopupDialogFragment
{
    public interface Callback
    {
        void touchEventFromPopupWindow();

        void reconnectWifi();

        void dismissButtonPressed();
    }

    private Callback callback;

    private TextView textViewWifiEnableStatus;
    private TextView textViewWifiConnectedToSSID;
    private TextView textViewWifiSSID;
    private TextView textViewWifiIPAddress;
    private TextView textViewWifiSignalLevel;
    private TextView textViewWifiConnectionStatus;
    private TextView textViewWifiBSSID;
    private TextView textViewWifiConnectionFailureReason;

    private TableRow tableRowIpAddress;

    private final MainActivityInterface main_activity_interface;

    private boolean allow_reconnect;

    public PopupWifiStatus(MainActivityInterface main_activity_interface)
    {
        super(main_activity_interface);

        this.main_activity_interface = main_activity_interface;

        allow_reconnect = false;
    }

    public void setArguments(Callback callback)
    {
        this.callback = callback;
    }

    public void showReconnectButton(boolean allow_reconnect)
    {
        this.allow_reconnect = allow_reconnect;
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
        dialog.setContentView(R.layout.pop_up_wifi_status);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        textViewWifiEnableStatus = dialog.findViewById(R.id.textView_wifi_enabled_status);
        textViewWifiConnectedToSSID = dialog.findViewById(R.id.textView_wifi_connected_to_ssid);
        textViewWifiSSID = dialog.findViewById(R.id.textView_wifi_SSID);
        textViewWifiIPAddress = dialog.findViewById(R.id.textView_wifi_IP_address);
        textViewWifiSignalLevel = dialog.findViewById(R.id.textView_wifi_signal_level);
        textViewWifiConnectionStatus = dialog.findViewById(R.id.textView_wifi_connection_status);
        textViewWifiBSSID = dialog.findViewById(R.id.textView_wifi_BSSID);
        textViewWifiConnectionFailureReason = dialog.findViewById(R.id.textView_wifi_failure_reason);

        tableRowIpAddress = dialog.findViewById(R.id.tableRowIpAddress);

        Button buttonWifiPopupDismiss = dialog.findViewById(R.id.button_wifi_popup_dismiss);
        buttonWifiPopupDismiss.setOnClickListener(v -> {
            callback.dismissButtonPressed();

            dismissPopupIfVisible();
        });


        Button buttonReconnectWifi = dialog.findViewById(R.id.button_wifi_reconnect);

        if(allow_reconnect)
        {
            buttonReconnectWifi.setOnClickListener(v -> callback.reconnectWifi());
            buttonReconnectWifi.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonReconnectWifi.setVisibility(View.GONE);
        }


        return dialog;
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


    public void updateWifiEnabledStatus(boolean enabled)
    {
        if(enabled)
        {
            showOnPopupIfVisible(textViewWifiEnableStatus, true, R.color.green);
        }
        else
        {
            showOnPopupIfVisible(textViewWifiEnableStatus, false, R.color.red);
        }
    }


    public void updateWifiConnectedToSSIDStatus(boolean connected)
    {
        if(connected)
        {
            showOnPopupIfVisible(textViewWifiConnectedToSSID, true, R.color.green);
        }
        else
        {
            showOnPopupIfVisible(textViewWifiConnectedToSSID, false, R.color.red);
        }
    }


    public void updateWifiSSID(String ssid)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                if(ssid != null)
                {
                    if(ssid.equals(main_activity_interface.getAppContext().getResources().getString(R.string.not_set_yet)) || ssid.equals(""))
                    {
                        showOnPopupIfVisible(textViewWifiSSID, ssid, R.color.red);
                    }
                    else
                    {
                        showOnPopupIfVisible(textViewWifiSSID, ssid, R.color.green);
                    }
                }
            }
        }
    }


    public void updateWifiIPAddress(String IP_address)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                if(IP_address != null)
                {
                    if(IP_address.equals(main_activity_interface.getAppContext().getResources().getString(R.string.not_set_yet)) || IP_address.equals("0.0.0.0"))
                    {
                        showOnPopupIfVisible(textViewWifiIPAddress,IP_address,R.color.red);
                    }
                    else
                    {
                        showOnPopupIfVisible(textViewWifiIPAddress,IP_address,R.color.green);
                    }
                }
             }
        }
    }


    public void enableWifiIPAddress(boolean enabled)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                if (enabled)
                {
                    tableRowIpAddress.setVisibility(View.VISIBLE);
                }
                else
                {
                    tableRowIpAddress.setVisibility(View.GONE);
                }
            }
        }
    }


    public void updateWifiLevel(int signal_level)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                String total_wifi_signal_level_string = signal_level + main_activity_interface.getAppContext().getResources().getString(R.string.string_total_wifi_level);

                if(signal_level <= 0)
                {
                    showOnPopupIfVisible(textViewWifiSignalLevel,total_wifi_signal_level_string, R.color.red);
                }
                else
                {
                    showOnPopupIfVisible(textViewWifiSignalLevel,total_wifi_signal_level_string, R.color.green);
                }
            }
        }
    }

    /**
     * Updates the current status (connected, disconnected) of wifi.
     * @param current_wifi_status (String): status in String
     */
    public void updateWifiStatus(String current_wifi_status)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                if(current_wifi_status != null)
                {
                    if (!current_wifi_status.equals("CONNECTED"))
                    {
                        showOnPopupIfVisible(textViewWifiConnectionStatus, current_wifi_status, R.color.red);
                    }
                    else
                    {
                        showOnPopupIfVisible(textViewWifiConnectionStatus, current_wifi_status, R.color.green);
                    }
                }
            }
        }
    }


    public void updateWifiBSSID(String bssid)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                if(bssid != null)
                {
                    if (bssid.equals(main_activity_interface.getAppContext().getResources().getString(R.string.not_set_yet)) || bssid.equals("00.00.00.00.00.00"))
                    {
                        showOnPopupIfVisible(textViewWifiBSSID, bssid, R.color.red);
                    }
                    else
                    {
                        showOnPopupIfVisible(textViewWifiBSSID, bssid, R.color.green);
                    }
                }
            }
        }
    }


    public void updateWifiConnectionFailureReason(String failure_reason)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                if(failure_reason != null)
                {
                    if (failure_reason.equals(main_activity_interface.getAppContext().getResources().getString(R.string.wifi_failure_reason_none)))
                    {
                        showOnPopupIfVisible(textViewWifiConnectionFailureReason, failure_reason, R.color.green);
                    }
                    else
                    {
                        showOnPopupIfVisible(textViewWifiConnectionFailureReason, failure_reason, R.color.red);
                    }
                }
            }
        }
    }


    private void showOnPopupIfVisible(TextView textview, boolean boolean_wifi_status, int color_id)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                textview.setText(Boolean.toString(boolean_wifi_status));

                textview.setTextColor(ContextCompat.getColor(main_activity_interface.getAppContext(), color_id));
            }
        }
    }

    private void showOnPopupIfVisible(TextView textview, String string_wifi_Status, int color_id)
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                textview.setText(string_wifi_Status);

                textview.setTextColor(ContextCompat.getColor(main_activity_interface.getAppContext(), color_id));
            }
        }
    }


    @Override
    public void onDismiss(@NotNull DialogInterface dialog)
    {
        setShowNotInProgress();

        callback.dismissButtonPressed();

        super.onDismiss(dialog);
    }
}
