package com.isansys.pse_isansysportal;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.isansys.patientgateway.HeartBeatInfo;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.Executors;

import me.bendik.simplerangeview.SimpleRangeView;

public class PopupLifetouchPoincare extends IsansysPopupDialogFragment
{
    private final String TAG = this.getClass().getSimpleName();

    private final Handler handlerGraphRefresh = new Handler();

    private GraphicalView chartView;
    private XYSeries xy_series_black;
    private XYSeries xy_series_red;

    private RadioButton checkBox10MinutesGraph;

    private SimpleRangeView rangeView;
    private long rangeViewEndTimeInMs;

    private Spinner spinnerDaySelection;
    private Spinner spinnerHourSelector;

    private LinkedList<HeartBeatInfo> heart_beat_list = new LinkedList<>();

    private int graph_size_minutes = 10;

    private boolean show_real_time_data = false;


    public PopupLifetouchPoincare(MainActivityInterface main_activity_interface)
    {
        super(main_activity_interface);
    }


    private static class DayOfSession
    {
        final DateTime start_of_day;
        final String dropdown_string;

        DayOfSession(String ds, DateTime dt)
        {
            dropdown_string = ds;
            start_of_day = dt;
        }

        @NotNull
        @Override
        public String toString()
        {
            return dropdown_string;
        }
    }


    private static class HourOfSession
    {
        final long start_ms;
        final long end_ms;
        final String dropdown_string;

        HourOfSession(String ds, long s, long e)
        {
            dropdown_string = ds;
            start_ms = s;
            end_ms = e;
        }

        @NotNull
        @Override
        public String toString()
        {
            return dropdown_string;
        }
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
                    main_activity_interface.touchEventSoResetTimers();
                }

                return super.dispatchTouchEvent(event);
            }
        };

        dialog.setOnDismissListener(this);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_poincare);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button buttonPopupDismiss = dialog.findViewById(R.id.buttonDismissPoincare);
        buttonPopupDismiss.setOnClickListener(v -> {
            main_activity_interface.showLifetouchPoincare(false);
            dialog.dismiss();
        });

        LinearLayout linear_layout_poincare_chart = dialog.findViewById(R.id.linear_layout_poincare_chart);

        checkBox10MinutesGraph = dialog.findViewById(R.id.checkBox10MinutesGraph);

        RadioGroup radioGroupGraphTimeSelection = dialog.findViewById(R.id.radioGroupGraphTimeSelection);
        radioGroupGraphTimeSelection.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.checkBox1MinuteGraph)
            {
                graph_size_minutes = 1;
            }
            else if (checkedId == R.id.checkBox2MinutesGraph)
            {
                graph_size_minutes = 2;
            }
            else if (checkedId == R.id.checkBox3MinutesGraph)
            {
                graph_size_minutes = 3;
            }
            else if (checkedId == R.id.checkBox5MinutesGraph)
            {
                graph_size_minutes = 5;
            }
            else if (checkedId == R.id.checkBox10MinutesGraph)
            {
                graph_size_minutes = 10;
            }
            else if (checkedId == R.id.checkBox15MinutesGraph)
            {
                graph_size_minutes = 15;
            }

            rangeView.setMinDistance(graph_size_minutes);

            int start_position = rangeView.getEnd() - graph_size_minutes;
            if (start_position < 0)
            {
                rangeView.setStart(0);
                rangeView.setEnd(graph_size_minutes);
            }
            else
            {
                rangeView.setStart(start_position);
            }

            Log.d(TAG, "onCheckedChanged : graph_size_minutes = " + graph_size_minutes);

            setGraphViewStateAndUpdate();
        });

        spinnerDaySelection = dialog.findViewById(R.id.spinnerDaySelection);
        spinnerHourSelector = dialog.findViewById(R.id.spinnerHoursSelection);


        rangeView = dialog.findViewById(R.id.rangeView);
        rangeView.setShowActiveTicks(false);
        rangeView.setCount(61);                     // One more than an hour to make it show BOTH hour marks
        rangeView.setActiveThumbRadius(0);          // Means you have to click EXACTLY in the right place to try and resize. So in effect turning it off
        rangeView.setActiveLineThickness(30);
        rangeView.setLabelFontSize(26f);
        rangeView.setMovable(true);

        rangeView.setOnRangeLabelsListener((simpleRangeView, i, state) -> {
            // Draw labels every 5 minutes
            if ((i % 5) == 0)
            {
                String start = "";
                String end = "";

                if (i == 0)
                {
                    // Random space at the start due to labels placement being a bit screwed up in the library
                    start = " ";
                }

                if (i == 60)
                {
                    // Random space at the end due to labels placement being a bit screwed up in the library
                    end = " ";
                }

                return start + TimestampConversion.convertDateToHumanReadableStringHoursMinutes(rangeViewEndTimeInMs - (DateUtils.HOUR_IN_MILLIS - (i * DateUtils.MINUTE_IN_MILLIS))) + end;
            }
            else
            {
                return "";
            }
        });

        rangeView.setOnTrackRangeListener(new SimpleRangeView.OnTrackRangeListener()
        {
            @Override
            public void onStartRangeChanged(@NotNull SimpleRangeView rangeView, int start)
            {
                // Prevent the bar from resizing
                if ((rangeView.getEnd() - start) > graph_size_minutes)
                {
                    rangeView.setEnd(start + graph_size_minutes);
                }
            }

            @Override
            public void onEndRangeChanged(@NotNull SimpleRangeView rangeView, int end)
            {
                // Prevent the bar from resizing
                if ((end - rangeView.getStart()) > graph_size_minutes)
                {
                    rangeView.setStart(end - graph_size_minutes);
                }

                // Update the screen
                setGraphViewStateAndUpdate();
            }
        });

        setupGraph(linear_layout_poincare_chart);
        return dialog;
    }


    private void setupGraph(LinearLayout linear_layout_poincare_chart)
    {
        // Create the XYSeries to plot
        xy_series_black = new XYSeries("Black");
        xy_series_red = new XYSeries("Red");

        XYMultipleSeriesDataset data_set = new XYMultipleSeriesDataset();
        data_set.addSeries(xy_series_black);
        data_set.addSeries(xy_series_red);

        XYSeriesRenderer renderer_black = new XYSeriesRenderer();
        renderer_black.setColor(Color.BLACK);
        renderer_black.setDisplayBoundingPoints(true);
        renderer_black.setPointStyle(PointStyle.X);
        renderer_black.setPointStrokeWidth(10);

        XYSeriesRenderer renderer_red = new XYSeriesRenderer();
        renderer_red.setColor(Color.RED);
        renderer_red.setDisplayBoundingPoints(true);
        renderer_red.setPointStyle(PointStyle.X);
        renderer_red.setPointStrokeWidth(10);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer_black);
        mRenderer.addSeriesRenderer(renderer_red);

        mRenderer.setXAxisMax(1.6);
        mRenderer.setYAxisMax(1.6);

        mRenderer.setXAxisMin(0);
        mRenderer.setYAxisMin(0);

        mRenderer.setShowLegend(false);

        // Avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        mRenderer.setMargins(new int[]{0, 70, 30, 0});

        mRenderer.setPanEnabled(true, true);

        mRenderer.setYLabelsColor(0, Color.BLACK);
        mRenderer.setXLabelsColor(Color.BLACK);

        mRenderer.setAxesColor(Color.BLACK);

        mRenderer.setXLabels(10);
        mRenderer.setYLabels(10);

        mRenderer.setYLabelsAngle(270);
        mRenderer.setYAxisAlign(Align.LEFT, 0);
        mRenderer.setYLabelsAlign(Align.CENTER, 0);

        mRenderer.setXTitle("RRI [i+1]");
        mRenderer.setYTitle("RRI [i]");
        mRenderer.setAxisTitleTextSize(30);

        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setLabelsTextSize(30);

        mRenderer.setShowLabels(true);

        mRenderer.setShowGrid(true);
        mRenderer.setGridColor(Color.LTGRAY);

        if(getActivity() != null)
        {
            chartView = ChartFactory.getScatterChartView(getActivity(), data_set, mRenderer);
            linear_layout_poincare_chart.addView(chartView, 0);
        }
        else
        {
            Log.e(TAG, "setupGraph : getActivity() is null");
        }
    }


    @Override
    public void onResume()
    {
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            Window window = dialog.getWindow();
            if (window != null)
            {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int)(getResources().getDisplayMetrics().widthPixels * 0.95);
                params.height = (int)(getResources().getDisplayMetrics().heightPixels * 0.95);
                window.setAttributes(params);

                long start_patient_session_time_in_ms = main_activity_interface.getSessionStartMilliseconds();
                if(start_patient_session_time_in_ms != 0)
                {
                    updateSpinnerList(start_patient_session_time_in_ms);
                }
            }
        }

        super.onResume();
    }


    private void setGraphViewStateAndUpdate()
    {
        removeGraphRefreshHandlerCallbacks();

        handlerGraphRefresh.post(runnableGraphRefresh);
    }


    /**
     * Runnable to update the Poincare plot
     */
    private final Runnable runnableGraphRefresh = new Runnable()
    {
        @Override
        public void run()
        {
            long time_now = main_activity_interface.getNtpTimeNowInMilliseconds();

            if(show_real_time_data)
            {
                rangeViewEndTimeInMs = time_now;

                if (rangeView.getEnd() == rangeView.getCount()-1)
                {
                    Log.d(TAG, "Live data");

                    long start_time = time_now - (graph_size_minutes * DateUtils.MINUTE_IN_MILLIS);
                    getHeartBeatListSelectedRange(start_time, time_now);

                    // Graph refresh in background thread showing Red Beats
                    //new doGraphUpdateInBackground().execute(true);
                    doGraphUpdateInBackground(true);

                    // Currently looking at Real Time data - so update every second
                    handlerGraphRefresh.postDelayed(runnableGraphRefresh, DateUtils.SECOND_IN_MILLIS);
                }
                else
                {
                    Log.d(TAG, "'Now' but not live data");

                    // Combo box shows "Now" but not most recent data
                    updateGraphNoRedBeats(time_now);
                }
            }
            else
            {
                // Combo box is not showing "Now"
                Log.d(TAG, "Not 'Now' data");

                rangeViewEndTimeInMs = graph_end_time;

                updateGraphNoRedBeats(graph_end_time);
            }

            // Force the labels to be updated with the desired times
            rangeView.invalidate();
        }
    };


    private void updateGraphNoRedBeats(long most_recent_time)
    {
        long end_time = most_recent_time - (DateUtils.HOUR_IN_MILLIS - (rangeView.getEnd() * DateUtils.MINUTE_IN_MILLIS));
        long start_time = end_time - (graph_size_minutes * DateUtils.MINUTE_IN_MILLIS);

        getHeartBeatListSelectedRange(start_time, end_time);

        // Graph refresh in background thread NOT showing Red Beats
        //new doGraphUpdateInBackground().execute(false);
        doGraphUpdateInBackground(false);
    }


    /**
     * Function to Remove Handler for every second update
     */
    private void removeGraphRefreshHandlerCallbacks()
    {
        handlerGraphRefresh.removeCallbacks(runnableGraphRefresh);
    }


    @Override
    public void onDestroyView()
    {
        removeGraphRefreshHandlerCallbacks();

        super.onDestroyView();
    }


    private ArrayList<HourOfSession> hours_in_dropdown = new ArrayList<>();    //Array to store the hour based on timeStamp (eg. 13/05/2015 13:00:00, 13/05/2015 14:00:00) in millisecond
    private ArrayList<DayOfSession> days_in_dropdown = new ArrayList<>();    //Array to store the days based on DateTime


    private void updateSpinnerList(long start_patient_session_time_in_ms)
    {
        Log.e(TAG, "updateSpinnerList : start_patient_session_time_in_ms (Local) = " + TimestampConversion.convertDateToHumanReadableStringDayHoursMinutesSeconds(start_patient_session_time_in_ms));
        Log.e(TAG, "updateSpinnerList : start_patient_session_time_in_ms (GMT) = " + TimestampConversion.convertDateToUtcHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(start_patient_session_time_in_ms));

        updateDaysSpinner(start_patient_session_time_in_ms);

        DateTime last_day_in_list = days_in_dropdown.get(days_in_dropdown.size() - 1).start_of_day;

        updateHoursSpinner(last_day_in_list, start_patient_session_time_in_ms);
    }


    private void updateDaysSpinner(long start_session_time_in_ms)
    {
        Log.d(TAG, "updateDaysSpinner. Start session time = " + TimestampConversion.convertDateToBchFormatHumanReadableStringYearMonthDayHoursMinutesSeconds(start_session_time_in_ms));

        long time_now = main_activity_interface.getNtpTimeNowInMilliseconds();
        DateTime date_time_now = new DateTime(time_now);
        DateTime session_start_date_time = new DateTime(start_session_time_in_ms);

        DayOfSession temp_day = new DayOfSession(getResources().getString(R.string.today), date_time_now.withTimeAtStartOfDay());

        days_in_dropdown = new ArrayList<>();

        days_in_dropdown.add(temp_day);


        int numberOfDaysSinceSessionStarted = date_time_now.getDayOfYear() - session_start_date_time.getDayOfYear();
        Log.d(TAG, "updateDaysSpinner : numberOfDaysSinceSessionStarted = " + numberOfDaysSinceSessionStarted);

        for(int j=0; j<numberOfDaysSinceSessionStarted; j++)
        {
            DateTime lifetouch_session_day = session_start_date_time.plusDays(j);
            Log.i(TAG, "updateDaysSpinner : day = " + lifetouch_session_day.toString());

            temp_day = new DayOfSession(TimestampConversion.convertDateToHumanReadableStringYearMonthDay(lifetouch_session_day), lifetouch_session_day.withTimeAtStartOfDay());

            days_in_dropdown.add(temp_day);
            Log.i(TAG, "updateDaysSpinner : added day to the list = " + lifetouch_session_day.dayOfYear().getDateTime());
        }


        if(getActivity() != null)
        {
            // Set the text view for spinnerDaySelection
            ArrayAdapter<DayOfSession> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, days_in_dropdown);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDaySelection.setAdapter(dataAdapter);

            spinnerDaySelection.setOnItemSelectedListener(new OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3)
                {
                    Log.d(TAG, "spinnerDaySelection setOnItemSelectedListener : selected item index number = " + arg2);

                    // Update the hour spinner bar according to the selected day
                    DateTime selected_day = days_in_dropdown.get(arg2).start_of_day;

                    Log.d(TAG, "spinnerDaySelection selected day = " + selected_day.toString());

                    updateHoursSpinner(selected_day, main_activity_interface.getSessionStartMilliseconds());

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0)
                {
                }
            });
        }
        else
        {
            Log.e(TAG,"updateSpinnerList : getActivity() is null");
        }
    }


    private void updateHoursSpinner(final DateTime selected_day, long start_session_time_in_ms)
    {
        Log.d(TAG, "updateHoursSpinner. Start session time = " + TimestampConversion.convertDateToBchFormatHumanReadableStringYearMonthDayHoursMinutesSeconds(start_session_time_in_ms));

        hours_in_dropdown = new ArrayList<>();

        long time_now = main_activity_interface.getNtpTimeNowInMilliseconds();
        DateTime date_time_now = new DateTime(time_now);
        DateTime session_start_date_time = new DateTime(start_session_time_in_ms);

        boolean first_day_of_session_selected = selected_day.withTimeAtStartOfDay().isEqual(session_start_date_time.withTimeAtStartOfDay());
        boolean today_selected = selected_day.withTimeAtStartOfDay().isEqual(date_time_now.withTimeAtStartOfDay());

        int hours_of_session_in_selected_day;
        int first_hour;

        HourOfSession temp_hour;

        // Check if the selected day is start of the session day
        if(first_day_of_session_selected)
        {
            // Check if the session start day is over or not
            if(today_selected)
            {
                first_hour = session_start_date_time.getHourOfDay();
                hours_of_session_in_selected_day = date_time_now.getHourOfDay() - first_hour;

                long hour_X0 = DateUtils.HOUR_IN_MILLIS * (date_time_now.getMillis()/DateUtils.HOUR_IN_MILLIS);
                long hour_X1 = hour_X0 + DateUtils.HOUR_IN_MILLIS;

                temp_hour = new HourOfSession(getResources().getString(R.string.String_Now), hour_X0, hour_X1);
                hours_in_dropdown.add(temp_hour);
            }
            else
            {
                first_hour = session_start_date_time.getHourOfDay();
                hours_of_session_in_selected_day = 24 - first_hour;

            }
            Log.d(TAG, "Start of session day selected " + TimestampConversion.convertDateToHumanReadableStringYearMonthDay(session_start_date_time)
            + ".     Hours in the day = " + hours_of_session_in_selected_day);
        }
        else
        {
            first_hour = 0;

            if (today_selected)
            {
                hours_of_session_in_selected_day = date_time_now.getHourOfDay();

                Log.d(TAG, "Today selected =  " + selected_day.toString() + ".     Hours in the day = " + hours_of_session_in_selected_day);

                long hour_X0 = DateUtils.HOUR_IN_MILLIS * (date_time_now.getMillis() / DateUtils.HOUR_IN_MILLIS);
                long hour_X1 = hour_X0 + DateUtils.HOUR_IN_MILLIS;

                temp_hour = new HourOfSession(getResources().getString(R.string.String_Now), hour_X0, hour_X1);
                hours_in_dropdown.add(temp_hour);
            }
            else
            {
                Log.d(TAG, "Intermediate day selected =  " + selected_day.toString() + ".     Hours in the day = 24");
                hours_of_session_in_selected_day = 24;
            }
        }

        for(int i=0; i<hours_of_session_in_selected_day;i++)
        {
            int hour = selected_day.getHourOfDay() + first_hour + i;

            long hour_X0 = selected_day.withTimeAtStartOfDay().getMillis() + DateUtils.HOUR_IN_MILLIS * hour;
            long hour_X1 = hour_X0 + DateUtils.HOUR_IN_MILLIS;

            temp_hour = new HourOfSession(formatHourStringForSpinner(hour), hour_X0, hour_X1);
            hours_in_dropdown.add(temp_hour);

            Log.d(TAG, "getSelectedHoursListForSpinner : (hour_XO,hour_X1) = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(hour_X0)+" , "+TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(hour_X1));
        }

        if(getActivity() != null)
        {
            // Set the text view for spinnerHourSelector
            ArrayAdapter<HourOfSession> data_adapter_hours = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, hours_in_dropdown);
            data_adapter_hours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerHourSelector.setAdapter(data_adapter_hours);

            spinnerHourSelector.setOnItemSelectedListener(new OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int index_number, long arg3)
                {
                    removeGraphRefreshHandlerCallbacks();

                    if(hours_in_dropdown.get(index_number).dropdown_string.equals(getResources().getString(R.string.String_Now)))
                    {
                        // Spinner saws "Now"
                        Log.i(TAG, "Loading the Current hour of data");

                        show_real_time_data = true;

                        // Auto goto "Live" data
                        rangeView.setStart(50);
                        rangeView.setEnd(60);

                        defaultTo10Minutes();
                    }
                    else
                    {
                        show_real_time_data = false;

                        HourOfSession displayed_hour = hours_in_dropdown.get(index_number);

                        graph_start_time = displayed_hour.start_ms;
                        graph_end_time = displayed_hour.end_ms;

                        Log.i(TAG, "spinnerHourSelector.setOnItemSelectedListener (Local): " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(graph_start_time) + " to " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(graph_end_time));
                        Log.i(TAG, "spinnerHourSelector.setOnItemSelectedListener (GMT)  : " + TimestampConversion.convertDateToUtcHumanReadableStringYearMonthDayHoursMinutesSeconds(graph_start_time) + " to " + TimestampConversion.convertDateToUtcHumanReadableStringYearMonthDayHoursMinutesSeconds(graph_end_time));

                        setGraphViewStateAndUpdate();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0)
                {
                }
            });
        }
        else
        {
            Log.e(TAG,"updateSpinnerList : getActivity() is null");
        }

        defaultTo10Minutes();
    }


    private long graph_start_time;
    private long graph_end_time;


    private String formatHourStringForSpinner(int hourSelected)
    {
        if(hourSelected == 23)
        {
            return hourSelected + ":00 - 00:00";
        }
        else
        {
            return String.format(Locale.getDefault(), "%02d", hourSelected) + ":00 - " + String.format(Locale.getDefault(), "%02d", hourSelected + 1) + ":00";
        }
    }


    private void defaultTo10Minutes()
    {
        Log.i(TAG, "defaultTo10Minutes");

        if(checkBox10MinutesGraph.isChecked())
        {
            graph_size_minutes = 10;
            setGraphViewStateAndUpdate();
        }
        else
        {
            checkBox10MinutesGraph.setChecked(true);
        }
    }


    private void getHeartBeatListSelectedRange(long start_time, long end_time)
    {
        Log.i(TAG, "getHeartBeatListSelectedRange : start_time = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(start_time));
        Log.i(TAG, "getHeartBeatListSelectedRange : end_time   = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(end_time));

        heart_beat_list = main_activity_interface.getHeartBeatList(start_time, end_time);
    }

    private void doGraphUpdateInBackground(boolean show_read_beats)
    {
        Executors.newSingleThreadExecutor().execute(() -> {

            // Long running operation
            calculateRR(heart_beat_list, show_read_beats);

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> chartView.repaint());
        });
    }

    private void calculateRR(LinkedList<HeartBeatInfo> heart_beat_list, boolean highlight_red_beats)
    {
        //long start_time = System.nanoTime();

        heart_beat_list.sort((lhs, rhs) -> (int) (lhs.getTimestampInMs() - rhs.getTimestampInMs()));

        // Clear out the old scatter plot data
        xy_series_black.clear();
        xy_series_red.clear();

        int number_of_points_added = 0;
        int number_of_points_to_add = heart_beat_list.size() - 2;

        while (number_of_points_added < number_of_points_to_add)
        {
            long Rn = heart_beat_list.get(number_of_points_added).getTimestampInMs();
            long Rn_plus_one = heart_beat_list.get(number_of_points_added + 1).getTimestampInMs();
            long Rn_plus_two = heart_beat_list.get(number_of_points_added + 2).getTimestampInMs();

            if (Rn == Rn_plus_one)
            {
                //Remove point Rn_plus_one
                heart_beat_list.remove(number_of_points_added + 1);
                number_of_points_to_add--;
            }
            else if (Rn_plus_one == Rn_plus_two)
            {
                // Remove point Rn_plus_two
                heart_beat_list.remove(number_of_points_added + 2);
                number_of_points_to_add--;
            }
            else
            {
                // Add RR intervals to the plot
                long x = Rn_plus_two - Rn_plus_one;
                long y = Rn_plus_one - Rn;

  //              long x_prime = heart_beat_list.get(number_of_points_added + 1).getRrInterval();
  //              long y_prime = heart_beat_list.get(number_of_points_added + 2).getRrInterval();

 //               Log.e("Moo", "x = " + x + " : x' = " + x_prime + " : y = " + y + " : y' = " + y_prime);

                // This converts from milliseconds to seconds
                double double_x = (double) x / DateUtils.SECOND_IN_MILLIS;
                double double_y = (double) y / DateUtils.SECOND_IN_MILLIS;

                // Quick sanity check - both RR intervals between 0 and 5 SECONDS
                if ((double_x < 5) & (double_x > 0) & (double_y < 5) & (double_y > 0))
                {
                    if (highlight_red_beats)
                    {
                        if((number_of_points_added + 3) >= number_of_points_to_add)
                        {
                            xy_series_red.add(double_x, double_y);
                        }
                        else
                        {
                            xy_series_black.add(double_x, double_y);
                        }
                    }
                    else
                    {
                        xy_series_black.add(double_x, double_y);
                    }
                }

                number_of_points_added++;
            }
        }

        //Log.e(TAG, "calculateRR executing time = " + ((System.nanoTime() - start_time) / 1000000));
    }
}
