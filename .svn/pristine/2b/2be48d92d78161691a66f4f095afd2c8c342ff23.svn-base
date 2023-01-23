package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;

public class FragmentVideoCallModeSelection extends FragmentIsansys implements OnClickListener
{
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.video_call_mode_selection, container, false);

        Button buttonCallContacts = v.findViewById(R.id.buttonCallContacts);
        buttonCallContacts.setOnClickListener(this);

        Button buttonScheduleCall = v.findViewById(R.id.buttonScheduleCall);
        buttonScheduleCall.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.buttonCallContacts)
        {
            main_activity_interface.videoCallContactsSelected();
        }
        else if (id == R.id.buttonScheduleCall)
        {
            main_activity_interface.scheduleVideoCallSelected();
        }
    }
}
