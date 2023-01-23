package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.erz.timepicker_library.TimePicker;

import java.util.Date;


public class FragmentEndSessionTimePicker extends FragmentIsansysWithTimestamp implements TimePicker.OnTimeChangedListener, TimePicker.CheckTimeWithinRangeListener
{
    private Button button_enter;
    private Button button_confirm;

    private TextView textViewEndSessionTimePicker;

    private long end_session_time_in_ms;

    private static final String TAG = FragmentEndSessionTimePicker.class.getName();

    private static boolean is_session_ended;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.end_session_time_picker, container, false);

        button_enter = v.findViewById(R.id.buttonEndSessionBigButtonTop);
        button_enter.setOnClickListener(button_enter_click_listener);
        button_enter.setVisibility(View.VISIBLE);

        // Confirm Button Settings
        button_confirm = v.findViewById(R.id.buttonEndSessionBigButtonBottom);
        button_confirm.setOnClickListener(button_confirm_click_listener);
        button_confirm.setVisibility(View.INVISIBLE);
        button_confirm.setEnabled(false);

        textViewEndSessionTimePicker = v.findViewById(R.id.textViewEndSessionTimePicker);

        Bundle args = getArguments();
        is_session_ended = args.getBoolean("is_session_ended");

        if(is_session_ended)
        {
            Log.d(TAG, "End session pressed user");
        }
        else
        {
            Log.d(TAG, "Transfer session pressed user");
        }

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
        timePicker.useDate(true);
        timePicker.showDate(true);
        timePicker.snapToNearestHour(false);

        timeChanged(initial_date);

        return v;
    }


    private enum SessionEndTimeEntryMode
    {
        NOT_SET_YET,
        ENTER_TIME_PRESSED
    }

    private SessionEndTimeEntryMode session_end_time_entry_mode = SessionEndTimeEntryMode.NOT_SET_YET;

    private final View.OnClickListener button_enter_click_listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Log.d(TAG, "button_enter_click_listener : Current session_end_time_entry_mode = " + session_end_time_entry_mode.toString());

            switch (session_end_time_entry_mode)
            {
                case NOT_SET_YET:
                {
                    // Make the confirm button visible and clickable
                    enableConfirmButton();

                    // Make cancel button visible and clickable
                    enableCancelButton();

                    enableEntry(false);

                    textViewEndSessionTimePicker.setVisibility(View.INVISIBLE);

                    session_end_time_entry_mode = SessionEndTimeEntryMode.ENTER_TIME_PRESSED;
                }
                break;

                case ENTER_TIME_PRESSED:
                {
                    // Enter button was set as Cancel Button. Time Picker and "Enter" button is is re-enabled
                    enableEnterButton();

                    // Enable Time Picker
                    enableEntry(true);

                    // Disable confirm button
                    disableButton(button_confirm);

                    textViewEndSessionTimePicker.setVisibility(View.VISIBLE);

                    session_end_time_entry_mode = SessionEndTimeEntryMode.NOT_SET_YET;
                }
                break;
            }
        }
    };


    private final View.OnClickListener button_confirm_click_listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Log.d(TAG, "button_confirm_click_listener : Current session_end_time_entry_mode = " + session_end_time_entry_mode.toString());

            switch (session_end_time_entry_mode)
            {
                case NOT_SET_YET:
                    break;

                case ENTER_TIME_PRESSED:
                {
                    // Confirm button is pressed after time is set and enter button is pressed.

                    // Disable "Enter" button
                    disableButton(button_enter);

                    // Disable "Confirm" button
                    disableButton(button_confirm);

                    // Disable Time Picker
                    enableEntry(false);

                    // Send end session command to the UI MainActivity and Gateway
                    triggerMainActivityEndSession();
                }
                break;
            }
        }
    };


    private void disableButton(Button mButton)
    {
        mButton.setVisibility(View.INVISIBLE);
        mButton.setEnabled(false);
    }


    private void enableConfirmButton()
    {
        button_confirm.setOnClickListener(button_confirm_click_listener);
        button_confirm.setVisibility(View.VISIBLE);
        button_confirm.setEnabled(true);
    }

    private void enableCancelButton()
    {
        button_enter.setText(getResources().getText(R.string.cancel));
        button_enter.setVisibility(View.VISIBLE);
        button_enter.setOnClickListener(button_enter_click_listener);
        button_enter.setEnabled(true);
    }

    private void enableEnterButton()
    {
        button_enter.setText(getResources().getText(R.string.enter));
        button_enter.setVisibility(View.VISIBLE);
        button_enter.setOnClickListener(button_enter_click_listener);
        button_enter.setEnabled(true);
    }

    /**
     * This function is called by timeChanged() after ActionMove in FragmentEndSessionTimePicker. This is linked up to get the
     * end session time in milliseconds.
     * @param timeStamp : long  -> Number of milliseconds
     */
    public void setTimestamp(long timeStamp)
    {
        end_session_time_in_ms = timeStamp;
    }


    private void triggerMainActivityEndSession()
    {
        if(is_session_ended)
        {
            Log.d(TAG, "triggerMainActivityEndSession : End session timeStamp =  " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(end_session_time_in_ms));
            main_activity_interface.endSessionPressed(end_session_time_in_ms);
        }
        else
        {
            Log.d(TAG, "triggerMainActivityEndSession : transfer session timeStamp =  " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(end_session_time_in_ms));
            main_activity_interface.transferSessionPressed(end_session_time_in_ms);
        }

        main_activity_interface.showModeSelectionOrGatewayNotConfiguredYet();
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


    private void enableEntry(boolean enable_entry)
    {
        TimePicker timePicker = getView().findViewById(R.id.timePicker);

        Activity activity = getActivity();
        if (activity != null)
        {
            if(enable_entry)
            {
                timePicker.setClockColor(ContextCompat.getColor(activity, R.color.blue));

                timePicker.disableTouch(false);
            }
            else
            {
                timePicker.setClockColor(ContextCompat.getColor(activity, R.color.gray));

                timePicker.disableTouch(true);
            }
        }
    }
}
