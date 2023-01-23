package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erz.timepicker_library.TimePicker;

import java.util.Date;

public class FragmentAnnotationEntrySelectAnnotationTime extends FragmentIsansysWithTimestamp implements TimePicker.OnTimeChangedListener, TimePicker.CheckTimeWithinRangeListener
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.annotation_time, container, false);

        Date initial_date = new Date(main_activity_interface.getNtpTimeNowInMilliseconds());

        TimePicker timePicker = v.findViewById(R.id.timePicker);

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

        timeChanged(initial_date);

        return v;
    }


    public boolean timeInValidRange(Date date)
    {
        Date time_now = new Date(main_activity_interface.getNtpTimeNowInMilliseconds());
        Date session_start_time = new Date(main_activity_interface.getSessionStartMilliseconds());

        return !date.after(time_now) && !date.before(session_start_time);
    }


    public void timeChanged(Date date)
    {
        main_activity_interface.setAnnotationsTimestamp(date.getTime());
    }
}
