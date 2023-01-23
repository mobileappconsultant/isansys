package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erz.timepicker_library.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class FragmentObservationSetTimeEntry extends FragmentIsansysWithTimestamp implements TimePicker.OnTimeChangedListener, TimePicker.CheckTimeWithinRangeListener
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.observation_set_time, container, false);

        // Round down time selected to the minute. 12:30.59 -> 12:30.00
        long ntp_time_now_in_ms = main_activity_interface.getNtpTimeNowInMilliseconds();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ntp_time_now_in_ms);
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MILLISECOND, 0 );

        Date initial_date = calendar.getTime();

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

        if (date.after(time_now) || date.before(session_start_time))
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    public void timeChanged(Date date)
    {
        setTimestamp(date.getTime());
    }
}
