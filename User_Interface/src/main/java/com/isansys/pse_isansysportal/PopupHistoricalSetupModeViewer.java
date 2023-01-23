package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.isansys.common.measurements.MeasurementSetupModeDataPoint;
import com.isansys.patientgateway.deviceInfo.SetupModeLog;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;

public class PopupHistoricalSetupModeViewer extends IsansysPopupDialogFragment
{
    private static final int NO_DATA = -1;

    private double y_axis_max;
    private ArrayList<SetupModeLog> setup_mode_log;

    private TextView textViewTitle;
    private TextView textPopUpHistoricalSetupModeViewerViewportSize;

    private GraphView graphView = null;
    private LineGraphSeries<DataPoint> graphViewSeries;

    public ArrayAdapter adapter;

    public PopupHistoricalSetupModeViewer(MainActivityInterface main_activity_interface)
    {
        super(main_activity_interface);
    }


    public void setArguments(double y_axis_max, ArrayList<SetupModeLog> setup_mode_log)
    {
        setYAxisMax(y_axis_max);
        this.setup_mode_log = setup_mode_log;
    }


    public void setYAxisMax(double y_axis_max)
    {
        this.y_axis_max = y_axis_max;

        if (graphView != null)
        {
            graphView.getViewport().setMaxY(y_axis_max);
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
        dialog.setContentView(R.layout.pop_up_historical_setup_mode_viewer);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ListView listview = dialog.findViewById(R.id.listview);
        adapter = new ArrayAdapter<>(main_activity_interface.getAppContext(), R.layout.list_historical_setup_mode_times, setup_mode_log);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener((parent, view, position, id) -> {
            final SetupModeLog item = (SetupModeLog) parent.getItemAtPosition(position);
            main_activity_interface.getBulkSetupModeData(item);
        });

        textViewTitle = dialog.findViewById(R.id.textPopUpHistoricalSetupModeViewerTitle);
        textViewTitle.setText("");

        textPopUpHistoricalSetupModeViewerViewportSize = dialog.findViewById(R.id.textPopUpHistoricalSetupModeViewerViewportSize);

        setViewportWidth(main_activity_interface.getHistoricalSetupModeViewViewportSize());

        Button buttonHistoricalSetupModeViewerDecreaseViewport = dialog.findViewById(R.id.buttonHistoricalSetupModeViewerDecreaseViewport);
        buttonHistoricalSetupModeViewerDecreaseViewport.setOnClickListener(v -> {
            main_activity_interface.decreaseHistoricalSetupModeViewportSize();
            setViewportWidth(main_activity_interface.getHistoricalSetupModeViewViewportSize());
        });

        Button buttonHistoricalSetupModeViewerIncreaseViewport = dialog.findViewById(R.id.buttonHistoricalSetupModeViewerIncreaseViewport);
        buttonHistoricalSetupModeViewerIncreaseViewport.setOnClickListener(v -> {
            main_activity_interface.increaseHistoricalSetupModeViewportSize();
            setViewportWidth(main_activity_interface.getHistoricalSetupModeViewViewportSize());
        });

        Button buttonPopupDismiss = dialog.findViewById(R.id.buttonHistoricalSetupModeViewerDismiss);
        buttonPopupDismiss.setOnClickListener(v -> {
            main_activity_interface.hideProgress();
            dismiss();
        });

        View graph = dialog.findViewById(R.id.plotHistoricalSetupModeLifetouch);
        graphView = (GraphView) graph;

        // Real-time data for Line Chart by GraphView
        graphViewSeries = new LineGraphSeries<>();

        // Draw the line with Red color
        graphViewSeries.setColor(Color.rgb(200, 0, 0));

        // Make specific line thickness
        graphViewSeries.setThickness(3);


        graphView.addSeries(graphViewSeries);

        // GraphView Bounds for Y axis
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0.0d);
        graphView.getViewport().setMaxY(y_axis_max);

        graphView.getViewport().setXAxisBoundsManual(true);

        graphView.onDataChanged(false, false);

        // Enable or disable scrolling
        graphView.getViewport().setScrollable(true); // horizontal scrolling
        graphView.getViewport().setScrollableY(false); // vertical scrolling
        graphView.getViewport().setScalable(true); // horizontal zooming and scrolling
        graphView.getViewport().setScalableY(false); // vertical zooming and scrolling

        // GraphView set styles
        graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
        graphView.getGridLabelRenderer().setTextSize(getResources().getDimension(R.dimen.full_screen_graph_labels));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(11);
        graphView.getGridLabelRenderer().setNumVerticalLabels(11);

        graphView.getGridLabelRenderer().setLabelsSpace(10);
        graphView.getGridLabelRenderer().setPadding(12);

        graphView.getGridLabelRenderer().setHighlightZeroLines(false);
        graphView.getGridLabelRenderer().setVerticalLabelsAlign(Paint.Align.LEFT);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);

        graphView.getGridLabelRenderer().setHorizontalRoundingValue(10);

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX)
                {
                    // The UTC timezone here is ONLY here to make sure the DateTime object doesn't try and become local time. The main_activity_interface.getGmtOffsetInMilliseconds() handles the Timezone and Daylight Saving Time offset
                    DateTime now_in_current_timezone_and_daylight_saving_time = new DateTime((long) value + main_activity_interface.getGmtOffsetInMilliseconds(), DateTimeZone.UTC);
                    return TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(now_in_current_timezone_and_daylight_saving_time);
                }

                // Let GraphView generate Y-axis label for us
                return super.formatLabel(value, false);
            }
        });

        graphView.getGridLabelRenderer().reloadStyles();

        Activity activity = getActivity();
        if (activity != null)
        {
            graphView.setBackgroundColor(ContextCompat.getColor(activity, R.color.graph_background_colour));
        }

        return dialog;
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
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.95);
                window.setAttributes(params);
            }
        }
        
        super.onResume();
    }


    public void bulkLoadGraphFromMeasurementArray(ArrayList<MeasurementSetupModeDataPoint> cached_measurements)
    {
        DataPoint[] dataPoints = new DataPoint[cached_measurements.size()];
        int index = 0;

        long session_start_time = main_activity_interface.getSessionStartDate();

        for (MeasurementSetupModeDataPoint measurement : cached_measurements)
        {
            if (measurement.sample == NO_DATA)
            {
                dataPoints[index++] = new DataPoint(measurement.timestamp_in_ms - session_start_time, Double.NaN);
            }
            else
            {
                dataPoints[index++] = new DataPoint(measurement.timestamp_in_ms - session_start_time, measurement.sample);
            }
        }

        if (dataPoints.length > 0)
        {
            String start_time = TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(cached_measurements.get(0).timestamp_in_ms);
            String end_time = TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(cached_measurements.get(cached_measurements.size()-1).timestamp_in_ms);
            String title = start_time + " - " + end_time;
            textViewTitle.setText(title);

            int view_port_width = main_activity_interface.getHistoricalSetupModeViewViewportSize();

            long start_viewport_timestamp = (long)dataPoints[0].getX();
            long end_viewport_timestamp = (long)dataPoints[0].getX() + view_port_width;

            graphView.getViewport().setMinX(start_viewport_timestamp);
            graphView.getViewport().setMaxX(end_viewport_timestamp);

            graphViewSeries.resetData(dataPoints);
        }
    }


    private void setViewportWidth(int view_port_width)
    {
        textPopUpHistoricalSetupModeViewerViewportSize.setText(String.valueOf(view_port_width / DateUtils.SECOND_IN_MILLIS));

        if (graphView != null)
        {
            graphView.getViewport().setMaxX(graphView.getViewport().getMinX(false) + view_port_width);
            graphView.onDataChanged(true, false); // parameters true,false cause the fastest redraw, fixing X-axis updates (Redmine 1580)
        }
    }
}
