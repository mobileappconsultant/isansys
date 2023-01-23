package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;


public class FragmentPulseOxDisconnectedLeadsOff extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.pulse_ox_disconnected_leadsoff, container, false);

        String total_display_string = getActivity().getResources().getString(R.string.nonin_disconnected) + " @ " + this.getArguments().getString("disconnection_timestamp") + "\n" + getActivity().getResources().getString(R.string.leads_off_detetect_for_more_than_ten_minutes);

        AutoResizeTextView autoResizeTextView = view.findViewById(R.id.autoSizeTextViewPulseOxDiconnectedLeadsOff);
        autoResizeTextView.setText(total_display_string);

        return view;
    }
}
