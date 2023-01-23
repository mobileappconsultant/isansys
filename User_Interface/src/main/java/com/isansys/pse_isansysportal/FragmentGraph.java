package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.ajit.customseekbar.CustomSeekBarVertical;
import com.ajit.customseekbar.ProgressItem;
import com.isansys.common.ErrorCodes;
import com.isansys.common.measurements.MeasurementAnnotation;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.deviceInfo.SetupModeLog;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentGraph extends FragmentIsansys implements Viewport.OnXAxisBoundsChangedListener
{
    private final String TAG = this.getClass().getSimpleName();

    private Timer graph_refresh_timer;
    private final Handler handler = new Handler();

    // GraphView variables
    private BarGraphSeries<DataPoint> graphViewSeries = null;
    private PointsGraphSeries<DataPoint> graphViewSeriesManuallyEntered = null;                     // Show Manual Vital Sign values as bubbles
    private PointsGraphSeries<DataPoint> graphViewSeriesMaxPossible = null;                         // This is a Points graph (with custom shape) as you cant have two bar graphs on top of each other
    private PointsGraphSeries<DataPoint> graphViewSeriesAllowableScroll = null;
    private PointsGraphSeries<DataPoint> graphViewSeriesHistoricalSetupModeViewer = null;           // Shows where Setup Mode was started as clickable bubbles
    private PointsGraphSeries<DataPoint> graphViewSeriesAnnotations = null;                         // Shows Annotations as clickable bubbles

    private CustomSeekBarVertical seekBarGraphScaleLeft = null;
    private GraphView graphView = null;
    private CustomSeekBarVertical seekBarGraphScaleRight = null;

    private long expected_measurement_interval_in_milliseconds = DateUtils.MINUTE_IN_MILLIS;

    private int graph_viewport_size_in_minutes;

    private GraphConfigs.GraphConfig graph_config;

    VitalSignType graph_vital_sign_type;
    VitalSignType graph_manually_entered_vital_sign_type;

    private ArrayList<? extends MeasurementVitalSign> cached_manual_vital_sign_measurements = null;

    boolean show_as_block = false;

    @Override
    public void onResume()
    {
        Log.d(TAG, "FragmentGraph onResume");

        graphView.getViewport().setOnXAxisBoundsChangedListener(this);

        graph_config = main_activity_interface.getGraphConfig(graph_vital_sign_type);

        int graph_refresh_time_in_seconds = 30;
        int graph_refresh_time_in_milliseconds = (int)(graph_refresh_time_in_seconds * DateUtils.SECOND_IN_MILLIS);

        main_activity_interface.checkAndCancel_timer(graph_refresh_timer);

        long time_now = main_activity_interface.getNtpTimeNowInMilliseconds();

        long initial_delay = graph_refresh_time_in_milliseconds - (time_now % graph_refresh_time_in_milliseconds) + DateUtils.SECOND_IN_MILLIS;

        graph_refresh_timer = new Timer();
        graph_refresh_timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                if( (getActivity() != null) && (isAdded()) )
                {
                    handler.post(graphRefreshRunnable);
                }
            }
        }, initial_delay, graph_refresh_time_in_seconds * DateUtils.SECOND_IN_MILLIS);

        ArrayList<MeasurementAnnotation> annotations = (ArrayList<MeasurementAnnotation>)main_activity_interface.getCachedMeasurements(VitalSignType.MANUALLY_ENTERED_ANNOTATION);
        redrawAnnotationsOnGraph(annotations);

        super.onResume();
    }


    private final Runnable graphRefreshRunnable = () -> {
        generateManualVitalsBars();

        graphRefresh();
    };


    @Override
    public void onPause()
    {
        Log.d(TAG, "FragmentGraph onPause");

        main_activity_interface.checkAndCancel_timer(graph_refresh_timer);

        super.onPause();
    }


    private void graphRefresh()
    {
        long timestamp_now_in_milliseconds = main_activity_interface.getNtpTimeNowInMilliseconds() - main_activity_interface.getSessionStartDate() + 30000;

        // Get viewport X max and min
        double xAxisMin = graphView.getViewport().getMinX(false);
        double xAxisMax = graphView.getViewport().getMaxX(false);
        double xAxisRange = xAxisMax - xAxisMin;

        graphView.getViewport().setMinX(timestamp_now_in_milliseconds - xAxisRange);
        graphView.getViewport().setMaxX(timestamp_now_in_milliseconds);

        graphViewSeriesAllowableScroll.appendData(new DataPoint(timestamp_now_in_milliseconds, -1), false, 1);

        graphView.onDataChanged(false, false);
    }


    private int getColourFromValue(double value)
    {
        if ((value >= ErrorCodes.ERROR_CODES) || (graph_config.graph_colour_bands.size() < 1))
        {
            return Color.LTGRAY;
        }
        else
        {
            for (GraphColourBand graph_colour_band : graph_config.graph_colour_bands)
            {
                if (value < graph_colour_band.less_than_value)
                {
/*
                    if (graph_colour_band.band_colour == ContextCompat.getColor(getContext(), R.color.early_warning_color_score__red))
                    {
                        Log.e(TAG, "value = " + value + ": colour = RED");
                    }
                    if (graph_colour_band.band_colour == ContextCompat.getColor(getContext(), R.color.early_warning_color_score__orange))
                    {
                        Log.e(TAG, "value = " + value + ": colour = ORANGE");
                    }
                    if (graph_colour_band.band_colour == ContextCompat.getColor(getContext(), R.color.early_warning_color_score__yellow))
                    {
                        Log.e(TAG, "value = " + value + ": colour = YELLOW");
                    }
                    if (graph_colour_band.band_colour == ContextCompat.getColor(getContext(), R.color.early_warning_color_score__green))
                    {
                        Log.e(TAG, "value = " + value + ": colour = GREEN");
                    }
*/
                    return graph_colour_band.band_colour;
                }
            }
        }

        Log.e(TAG, "value = " + value + ": colour = DEFAULT");

        // Value is at or above the top of the chart, so return the colour of the top band
        return graph_config.graph_colour_bands.get(graph_config.graph_colour_bands.size() - 1).band_colour;
    }


    void setupGraph(View graph, CustomSeekBarVertical seekBarGraphScaleLeft, CustomSeekBarVertical seekBarGraphScaleRight)
    {
        graph_config = main_activity_interface.getGraphConfig(graph_vital_sign_type);

        graph_viewport_size_in_minutes = main_activity_interface.getDefaultGraphViewportSizeInMinutes();

        long timestamp_now_in_milliseconds = main_activity_interface.getNtpTimeNowInMilliseconds() - main_activity_interface.getSessionStartDate();
        long earliest_cached_timestamp = main_activity_interface.getEarliestCachedTimestamp();
        long two_hours_ago = timestamp_now_in_milliseconds - getGraphViewportSizeInMilliseconds();

        long starting_timestamp;

        if((earliest_cached_timestamp < 0) || (two_hours_ago < earliest_cached_timestamp))
        {
            starting_timestamp = two_hours_ago;
        }
        else
        {
            starting_timestamp = earliest_cached_timestamp;
        }

        graphViewSeries = new BarGraphSeries<>(new DataPoint[]{new DataPoint(starting_timestamp, graph_config.graph_y_axis_max_min.min)});
        graphViewSeriesManuallyEntered = new PointsGraphSeries<>(new DataPoint[]{new DataPoint(starting_timestamp, -1)});
        graphViewSeriesMaxPossible = new PointsGraphSeries<>(new DataPoint[]{new DataPoint(starting_timestamp, -1)});
        graphViewSeriesAllowableScroll = new PointsGraphSeries<>(new DataPoint[]{new DataPoint(timestamp_now_in_milliseconds, -1)});
        graphViewSeriesHistoricalSetupModeViewer = new PointsGraphSeries<>();
        graphViewSeriesAnnotations = new PointsGraphSeries<>();

        this.seekBarGraphScaleLeft = seekBarGraphScaleLeft;
        graphView = (GraphView) graph;
        this.seekBarGraphScaleRight = seekBarGraphScaleRight;

        graphViewSeries.setValueDependentColor(data -> getColourFromValue(data.getY()));

        graphViewSeries.setSpacing(10);
        graphViewSeries.setDataWidth(expected_measurement_interval_in_milliseconds);

        graphView.addSeries(graphViewSeriesMaxPossible);                                            // This is used by EWS. Must be first to be overwritten by Values
        graphView.addSeries(graphViewSeries);
        graphView.addSeries(graphViewSeriesManuallyEntered);
        graphView.addSeries(graphViewSeriesAllowableScroll);
        graphView.addSeries(graphViewSeriesHistoricalSetupModeViewer);
        graphView.addSeries(graphViewSeriesAnnotations);

        graphViewSeriesManuallyEntered.setCustomShape((canvas, paint, x, y, dataPoint) -> {
            int background_colour = getColourFromValue(dataPoint.getY());

            String label = main_activity_interface.getGraphLabelForManualVitalSigns(graph_manually_entered_vital_sign_type, dataPoint.getX(), dataPoint.getY());

            main_activity_interface.drawTextBubbleOnGraph(canvas, paint, x, y, label, background_colour);
        });

        graphViewSeriesMaxPossible.setShape(PointsGraphSeries.Shape.RECTANGLE);
        graphViewSeriesMaxPossible.setColor(Color.LTGRAY);

        graphViewSeriesMaxPossible.setCustomShape((canvas, paint, x, y, dataPoint) -> {
            paint.setStrokeWidth(1);

            float graphHeight = graphView.getGraphContentHeight();
            float graphTop = graphView.getGraphContentTop();

            float bar_bottom = y + (graphHeight - y) + graphTop;

            // Trial and error to match the bar width having 2 hours of data on the screen
            // Left, Top, Right, Bottom
            canvas.drawRect(x-8, y, x+6, bar_bottom, paint);
        });


        graphViewSeriesHistoricalSetupModeViewer.setCustomShape((canvas, paint, x, y, dataPoint) -> {
            Activity activity = getActivity();
            if (activity != null)
            {
                int background_colour = ContextCompat.getColor(activity, R.color.blue);
                String value = " S ";
                main_activity_interface.drawTextBubbleOnGraph(canvas, paint, x, y, value, background_colour);
            }
        });


        graphViewSeriesHistoricalSetupModeViewer.setOnDataPointTapListener((series, dataPoint) -> main_activity_interface.showHistoricalSetupModeViewerPopup(dataPoint.getX(), graph_vital_sign_type));


        graphViewSeriesAnnotations.setCustomShape((canvas, paint, x, y, dataPoint) -> {
            Activity activity = getActivity();
            if (activity != null)
            {
                int background_colour = ContextCompat.getColor(activity, R.color.blue);
                String value = " A ";
                main_activity_interface.drawTextBubbleOnGraph(canvas, paint, x, y, value, background_colour);
            }
        });


        graphViewSeriesAnnotations.setOnDataPointTapListener((series, dataPoint) -> main_activity_interface.showAnnotationPopup(dataPoint.getX()));


        double y_axis_max = graph_config.graph_y_axis_max_min.max;
        double y_axis_min = graph_config.graph_y_axis_max_min.min;

        // Setup the Y Axis viewport
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(y_axis_min);
        graphView.getViewport().setMaxY(y_axis_max);

        // Setup the X Axis viewport
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(starting_timestamp);
        graphView.getViewport().setMaxX(timestamp_now_in_milliseconds);

        graphView.getViewport().setMaxXAxisSize(main_activity_interface.getMaxGraphViewportSizeInMinutes() * (int)DateUtils.MINUTE_IN_MILLIS);

        graphView.onDataChanged(false, false);

        // enable or disable scrolling
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        // GraphView set styles
        graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
        graphView.getGridLabelRenderer().setTextSize(getResources().getDimension(R.dimen.graph_labels));

        if (graph_viewport_size_in_minutes >= 10)
        {
            graphView.getGridLabelRenderer().setNumHorizontalLabels(graph_viewport_size_in_minutes / 10 + 1);
        }
        else
        {
            graphView.getGridLabelRenderer().setNumHorizontalLabels(graph_viewport_size_in_minutes + 1);
        }

        int numberOfGraphTicks = main_activity_interface.getNumberOfGraphTicksFromRange(y_axis_max - y_axis_min);
        Log.d(TAG, "numberOfGraphTicks = " + numberOfGraphTicks);
        graphView.getGridLabelRenderer().setNumVerticalLabels(numberOfGraphTicks);

        graphView.getGridLabelRenderer().setLabelsSpace(10);
        graphView.getGridLabelRenderer().setPadding(12);

        graphView.getGridLabelRenderer().setHighlightZeroLines(false);

        graphView.getGridLabelRenderer().setVerticalLabelsAlign(Paint.Align.RIGHT);
        graphView.getGridLabelRenderer().setLabelVerticalWidth(30);

        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);

        graphView.getGridLabelRenderer().setHorizontalRoundingValue(DateUtils.MINUTE_IN_MILLIS);

        if (graph_config.do_not_round_to_axis_most_significant_digit)
        {
            graphView.getGridLabelRenderer().setHumanRounding(true, false);
        }

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX)
                {
                    // Graph starts at 1970 to make arrays smaller
                    long real_measurement_time = (long)value + main_activity_interface.getSessionStartDate();

                    DateTime measurement_datetime = new DateTime(real_measurement_time);
                    DateTime now_timestamp_local_time = new DateTime(main_activity_interface.getLocalTimeNowInMilliseconds(), DateTimeZone.UTC);

                    if (measurement_datetime.getDayOfMonth() != now_timestamp_local_time.getDayOfMonth())
                    {
                        return TimestampConversion.convertDateToHumanReadableStringDayHoursMinutes(real_measurement_time);
                    }
                    else
                    {
                        return TimestampConversion.convertDateToHumanReadableStringHoursMinutes(real_measurement_time);
                    }
                }
                else
                {
                    if (show_as_block)
                    {
                        return "";
                    }
                    else
                    {
                        // Let GraphView generate Y-axis label for us
                        return super.formatLabel(value, false);
                    }
                }
            }
        });

        graphView.getGridLabelRenderer().reloadStyles();

        Activity activity = getActivity();
        if (activity != null)
        {
            graphView.setBackgroundColor(ContextCompat.getColor(activity, R.color.graph_background_colour));
        }

        ArrayList<ProgressItem> progressItemList = main_activity_interface.generateVerticalThresholdBars(graph_config.graph_colour_bands, y_axis_min, y_axis_max);

        if(getActivity() != null)
        {
            if(seekBarGraphScaleLeft != null)
            {
                seekBarGraphScaleLeft.initData(progressItemList);
                seekBarGraphScaleLeft.invalidate();
            }

            if(seekBarGraphScaleRight != null)
            {
                seekBarGraphScaleRight.initData(progressItemList);
                seekBarGraphScaleRight.invalidate();
            }
        }
        else
        {
            Log.e(TAG,"setupGraph : getActivity() is NULL");
        }
    }


    void bulkLoadNormal(DataPoint[] dataPoints)
    {
        if (dataPoints.length > 0)
        {
            graphViewSeries.resetData(dataPoints);
        }
    }


    private void bulkLoadManuallyEntered(DataPoint[] dataPoints)
    {
        if (dataPoints.length > 0)
        {
            graphViewSeriesManuallyEntered.resetData(dataPoints);
        }
    }


    void bulkLoadMaxPossible(DataPoint[] dataPoints)
    {
        if (dataPoints.length > 0)
        {
            graphViewSeriesMaxPossible.resetData(dataPoints);
        }
    }


    private void bulkLoadHistoricalSetupModeLog(DataPoint[] dataPoints)
    {
        if (dataPoints.length > 0)
        {
            graphViewSeriesHistoricalSetupModeViewer.resetData(dataPoints);
        }
    }


    private void bulkLoadAnnotations(DataPoint[] dataPoints)
    {
        if (dataPoints.length > 0)
        {
            graphViewSeriesAnnotations.resetData(dataPoints);
        }
    }


    // Doing a bulk load instead of adding a single measurement at a time as GraphView does not support out of time sequence values. Doing a bulk load is only 10ms or so
    private <T extends MeasurementVitalSign> void bulkLoadGraphFromMeasurementArray(ArrayList<T> cached_measurements, boolean manually_entered)
    {
        ArrayList<DataPoint> dataPoints = new ArrayList<>();

        long session_start_time = main_activity_interface.getSessionStartDate();

        double y_axis_min = graph_config.graph_y_axis_max_min.min;

        // Using Iterator here instead of simple For loop to ensure we dont get a concurrent modification exception if new measurements are added to the measurement cache while we read them here
        for (MeasurementVitalSign measurement : cached_measurements)
        {
            double measurement_value = measurement.getPrimaryMeasurement();

            if (!manually_entered)
            {
                if (measurement_value < y_axis_min)
                {
                    measurement_value = y_axis_min;
                }
            }

            dataPoints.add(new DataPoint(measurement.timestamp_in_ms - session_start_time, measurement_value));
        }

        if (manually_entered)
        {
            Log.d(TAG, "redrawMeasurementGraph : the size of Manually Entered cached measurement is " + dataPoints.size());

            bulkLoadManuallyEntered(dataPoints.toArray(new DataPoint[0]));
        }
        else
        {
            Log.d(TAG, "redrawMeasurementGraph : the size of Normal cached measurement is " + dataPoints.size());

            bulkLoadNormal(dataPoints.toArray(new DataPoint[0]));
        }
    }


    void setExpectedMeasurementIntervalInMilliseconds(long number_in_milliseconds)
    {
        expected_measurement_interval_in_milliseconds = number_in_milliseconds;
    }


    private long getGraphViewportSizeInMilliseconds()
    {
        return graph_viewport_size_in_minutes * DateUtils.MINUTE_IN_MILLIS;
    }


    @Override
    public void onXAxisBoundsChanged(double minX, double maxX, Viewport.OnXAxisBoundsChangedListener.Reason reason)
    {
        switch (reason)
        {
            case SCALE:
            {
                main_activity_interface.onGraphScale((long) minX, (long) maxX);
            }
            break;

            case SCROLL:
            {
                main_activity_interface.onGraphScroll((long) minX, (long) maxX);
            }
            break;
        }
    }


    // Called when MainActivity detects a scale gesture (possibly made in 2 graphs), so that scale gestures can be
    // sent instead of graphs interpreting the gesture as 2 scroll gestures.
    public void scaleGraph(ScaleGestureDetector detector)
    {
        if (graphView != null)
        {
            graphView.getViewport().getScaleGestureListener().onScale(detector);
        }
    }


    void scrollOrScaleToTimeWindow(long start_timestamp, long end_timestamp)
    {
        graph_viewport_size_in_minutes = (int)((end_timestamp - start_timestamp) / expected_measurement_interval_in_milliseconds);

        if (graphView != null)
        {
            graphView.getViewport().setMinX(start_timestamp);
            graphView.getViewport().setMaxX(end_timestamp);

            graphView.onDataChanged(true, false);
        }
    }


    private <T extends MeasurementVitalSign> void generateManualVitalsBars()
    {
        if (cached_manual_vital_sign_measurements != null)
        {
            ArrayList<T> new_measurements = (ArrayList<T>)cached_manual_vital_sign_measurements.clone();

            for (int x = 0; x < cached_manual_vital_sign_measurements.size(); x++)
            {
                MeasurementVitalSign cached_vital = cached_manual_vital_sign_measurements.get(x);

                try
                {
                    long timestamp = cached_vital.timestamp_in_ms;

                    int measurement_validity_in_minutes = cached_vital.measurement_validity_time_in_seconds / 60;

                    long time_now = main_activity_interface.getNtpTimeNowInMilliseconds();

                    for (int i=1; i<measurement_validity_in_minutes; i++)
                    {
                        T vital = (T)cached_vital.clone();

                        vital.timestamp_in_ms = timestamp + (i * DateUtils.MINUTE_IN_MILLIS);

                        // Only plot the measurements up to now. Otherwise the graph will plot bars in the future and there is no way of stopping the graph from scrolling to them
                        if (time_now >= vital.timestamp_in_ms)
                        {
                            new_measurements.add(vital);
                        }
                    }
                }
                catch (CloneNotSupportedException e)
                {
                    Log.e(TAG, "CloneNotSupportedException");
                }
            }

            // Sort this into time order
            sortCachedVitalsListInTimeOrder(new_measurements);

            DataPoint[] dataPoints = new DataPoint[new_measurements.size()];

            if (new_measurements.size() == 1)
            {
                // Need an additional space at the start for a dummy measurement to get around the GraphView bug where single measurements appear to be plotted far too wide
                dataPoints = new DataPoint[new_measurements.size() + 1];
            }

            int index = 0;

            long session_start_time = main_activity_interface.getSessionStartDate();

            for (MeasurementVitalSign measurement : new_measurements)
            {
                long time = measurement.timestamp_in_ms - session_start_time;

                // Add the additional dummy measurement to get around the GraphView bug where single measurements appear to be plotted far too wide
                if (new_measurements.size() == 1)
                {
                    double dummy_measurement = graph_config.graph_y_axis_max_min.min;
                    long dummy_timestamp = time - DateUtils.MINUTE_IN_MILLIS;

                    Log.d(TAG, "Adding Dummy measurement : timestamp_in_ms = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(dummy_timestamp));

                    dataPoints[index++] = new DataPoint(dummy_timestamp, dummy_measurement);
                }

                dataPoints[index++] = new DataPoint(time, measurement.getPrimaryMeasurement());
            }

            bulkLoadNormal(dataPoints);
        }
    }


    private <T extends MeasurementVitalSign> void redrawManualVitalSignBarGraphWithValidityTimes(ArrayList<T> cached_measurements)
    {
        // Save the measurements locally so generateManualVitalsBars can be called from a timer every 30 seconds
        this.cached_manual_vital_sign_measurements = (ArrayList<T>)cached_measurements.clone();

        generateManualVitalsBars();
    }


    private void sortCachedVitalsListInTimeOrder(ArrayList<? extends MeasurementVitalSign> list)
    {
        list.sort((Comparator<MeasurementVitalSign>) (lhs, rhs) -> Long.compare(lhs.timestamp_in_ms, rhs.timestamp_in_ms));
    }


    public <T extends MeasurementVitalSign> void redrawNormalMeasurementGraph(ArrayList<T> cached_measurements)
    {
        redrawMeasurementGraph(cached_measurements, false);
    }


    <T extends MeasurementVitalSign> void redrawManuallyEnteredMeasurementGraph(ArrayList<T> cached_measurements)
    {
        redrawMeasurementGraph(cached_measurements, true);
    }


    void redrawSetupModeLogGraph(ArrayList<SetupModeLog> setup_mode_log)
    {
        DataPoint[] dataPoints = new DataPoint[setup_mode_log.size()];
        int index = 0;

        long session_start_time = main_activity_interface.getSessionStartDate();

        double y_axis_max = graph_config.graph_y_axis_max_min.max;
        double y_axis_min = graph_config.graph_y_axis_max_min.min;

        double y_axis_delta = y_axis_max - y_axis_min;
        double distance_from_top_of_graph = y_axis_delta / 9;
        double y_axis_value = y_axis_max - distance_from_top_of_graph;

        for (SetupModeLog setup_mode_log_entry : setup_mode_log)
        {
            dataPoints[index++] = new DataPoint(setup_mode_log_entry.start_time - session_start_time, y_axis_value);
        }

        bulkLoadHistoricalSetupModeLog(dataPoints);
    }


    <T extends MeasurementVitalSign> void redrawAnnotationsOnGraph(ArrayList<T> annotations)
    {
        if (annotations != null)
        {
            DataPoint[] dataPoints = new DataPoint[annotations.size()];
            int index = 0;

            long session_start_time = main_activity_interface.getSessionStartDate();

            double y_axis_max = graph_config.graph_y_axis_max_min.max;
            double y_axis_min = graph_config.graph_y_axis_max_min.min;

            double y_axis_delta = y_axis_max - y_axis_min;
            double distance_from_top_of_graph = y_axis_delta / 9;
            double y_axis_value = y_axis_max - distance_from_top_of_graph;

            for (T annotation : annotations)
            {
                dataPoints[index++] = new DataPoint(annotation.timestamp_in_ms - session_start_time, y_axis_value);
            }

            bulkLoadAnnotations(dataPoints);
        }
    }


    // Doing a bulk load instead of adding a single measurement at a time as GraphView does not support out of time sequence values. Doing a bulk load is only 10ms or so
    public <T extends MeasurementVitalSign> void redrawMeasurementGraph(ArrayList<T> cached_measurements, boolean manually_entered)
    {
        if (cached_measurements != null)
        {
            bulkLoadGraphFromMeasurementArray(cached_measurements, manually_entered);
        }
    }


    void getManualVitalSignsFromMainActivity()
    {
        ArrayList<? extends MeasurementVitalSign> cached_measurements = main_activity_interface.getCachedMeasurements(graph_manually_entered_vital_sign_type);

        if (show_as_block)
        {
            redrawManualVitalSignBarGraphWithValidityTimes(cached_measurements);
        }
        else
        {
            redrawManuallyEnteredMeasurementGraph(cached_measurements);
        }
    }


    void setupForNoYAxisVitalSign()
    {
        graphView.getGridLabelRenderer().setNumVerticalLabels(2);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.VERTICAL);

        showThresholds(false);
    }


    private void showThresholds(boolean show)
    {
        if (show)
        {
            seekBarGraphScaleLeft.setVisibility(View.VISIBLE);
            seekBarGraphScaleRight.setVisibility(View.VISIBLE);
        }
        else
        {
            seekBarGraphScaleLeft.setVisibility(View.INVISIBLE);
            seekBarGraphScaleRight.setVisibility(View.INVISIBLE);
        }
    }


    void redrawGraph()
    {
        redrawNormalMeasurementGraph(main_activity_interface.getCachedMeasurements(graph_vital_sign_type));

        redrawManuallyEnteredMeasurementGraph(main_activity_interface.getCachedMeasurements(graph_manually_entered_vital_sign_type));
    }


    public void hideSetupModeBlobs()
    {
        DataPoint[] noDataPoints = new DataPoint[0];

        graphViewSeriesHistoricalSetupModeViewer.resetData(noDataPoints);
    }
}
