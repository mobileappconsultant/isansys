package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;

public class FragmentAndBleNeedsPairing extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    AutoResizeTextView autoResizeTextView;
    Button buttonBeginReconnectionForPairing;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.and_ble_needs_pairing, container, false);

        autoResizeTextView = view.findViewById(R.id.autoSizeTextViewAndBleNeedsPairing);

        Activity activity = getActivity();
        if (activity != null)
        {
            String display_string = activity.getResources().getString(R.string.textBloodPressurePairingFailed);
            autoResizeTextView.setText(display_string);
        }

        buttonBeginReconnectionForPairing = view.findViewById(R.id.beginReconnectionForPairing);

        buttonBeginReconnectionForPairing.setOnClickListener(v -> main_activity_interface.startConnectionForUnbondedDevice());

        buttonBeginReconnectionForPairing.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_animation));

        return view;
    }
}
