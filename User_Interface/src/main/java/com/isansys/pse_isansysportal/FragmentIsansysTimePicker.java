package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erz.timepicker_library.TimePicker;

import java.util.Date;

public abstract class FragmentIsansysTimePicker extends FragmentIsansys implements TimePicker.OnTimeChangedListener, TimePicker.CheckTimeWithinRangeListener
{
    private TimePicker timePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.time_picker, container, false);

        Date initial_date = new Date(main_activity_interface.getNtpTimeNowInMilliseconds());

        timePicker = v.findViewById(R.id.timePicker);

        Activity activity = getActivity();
        if (activity != null)
        {
            timePicker.setDialColor(ContextCompat.getColor(activity, R.color.green));
            timePicker.setClockColor(ContextCompat.getColor(activity, R.color.blue));
        }

        timePicker.enableTwentyFourHour(true);
        timePicker.setTime(initial_date);
        timePicker.setTimeChangedListener(this);
        timePicker.setCheckTimeWithinRangeListener(this);
        timePicker.useDate(false);
        timePicker.showDate(false);
        timePicker.snapToNearestHour(true);

        timeChanged(initial_date);

        return v;
    }


    public boolean timeInValidRange(Date date)
    {
        return true;
    }


    public abstract void timeChanged(Date date);


    public void setTime(Date date)
    {
        timePicker.setTime(date);
    }
}
