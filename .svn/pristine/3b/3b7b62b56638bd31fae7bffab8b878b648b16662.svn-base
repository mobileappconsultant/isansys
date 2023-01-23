package com.isansys.pse_isansysportal;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class FragmentGraphSetupMode extends FragmentIsansys
{
    private boolean start_fresh_graph = false;
    private long last_added_timestamp = 0;

    // GraphView variables
    private LineGraphSeries<DataPoint> graphViewSeries = null;
    private GraphView graphView = null;

    private int graph_refresh_counter = 0;

    private View setup_mode_view;

    private int max_sample_size;
    private int number_of_samples;

    public static final String MAX_SAMPLE_SIZE = "MAX_SAMPLE_SIZE";
    public static final String NUMBER_OF_SAMPLES = "NUMBER_OF_SAMPLES";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setup_mode_view = inflater.inflate(R.layout.setup_mode, container, false);
    
        return setup_mode_view;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        Bundle bundle = this.getArguments();
        max_sample_size = bundle.getInt(MAX_SAMPLE_SIZE, 256);
        number_of_samples = bundle.getInt(NUMBER_OF_SAMPLES, 1000);
        setupGraph(max_sample_size);
    }


    public void updateSetupMode(int value, long timestamp_in_ms)
    {
    	long graph_timestamp_in_ms = timestamp_in_ms - main_activity_interface.getSessionStartDate();

        if ((graphViewSeries != null) && (graph_timestamp_in_ms > last_added_timestamp))
        {
            last_added_timestamp = graph_timestamp_in_ms;

        	if(start_fresh_graph)
        	{
        		graphViewSeries.resetData(new DataPoint[]{new DataPoint(graph_timestamp_in_ms, value)});
        		start_fresh_graph = false;
        	}
        	else
        	{
        		graphViewSeries.appendData(new DataPoint(graph_timestamp_in_ms, value), true, number_of_samples);
        	}
        	
            // redraw the graph every 4 points... at 100 Hz sampling this gives us 25 fps
        	if((++graph_refresh_counter % 4) == 0)
        	{
            	graphView.getViewport().setMinX(graph_timestamp_in_ms - 10000);
            	graphView.getViewport().setMaxX(graph_timestamp_in_ms);
        		
            	if(graph_refresh_counter >= 100)
            	{
	        		graphView.onDataChanged(true, false);
	        		graph_refresh_counter = 0;
            	}
        	}
        }
    }
    
   
    void setupGraph(double desiredYAxisMax)
    {
		long timestamp_now_in_milliseconds = main_activity_interface.getNtpTimeNowInMilliseconds() - main_activity_interface.getSessionStartDate();

        int max_graph_size = 10000;
        long starting_timestamp = timestamp_now_in_milliseconds - max_graph_size;
        
        // Real-time data for Line Chart by GraphView
        graphViewSeries = new LineGraphSeries<>(new DataPoint[]
                {
                        new DataPoint(timestamp_now_in_milliseconds, 0.0d)
                });

        // Draw the line with Red color
        graphViewSeries.setColor(Color.rgb(200, 0, 0));

        // Make specific line thickness
        graphViewSeries.setThickness(3);
        
        View graph = setup_mode_view.findViewById(R.id.plotRealTimeECG);
        
        graphView = (GraphView) graph;

        graphView.addSeries(graphViewSeries);
        
        // GraphView Bounds for Y axis
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0.0d);
        graphView.getViewport().setMaxY(desiredYAxisMax);
        
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(starting_timestamp);
        graphView.getViewport().setMaxX(timestamp_now_in_milliseconds);
        
        graphView.onDataChanged(false, false);

    	// enable or disable scrolling
        graphView.getViewport().setScrollable(false);
        graphView.getViewport().setScalable(false);
        
        // GraphView set styles
        graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
        graphView.getGridLabelRenderer().setTextSize(getResources().getDimension(R.dimen.graph_labels));
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

                // Let Graphview generate Y-axis label for us
                return super.formatLabel(value, false);
            }
        });
        
        graphView.getGridLabelRenderer().reloadStyles();

        Activity activity = getActivity();
        if (activity != null)
        {
            graphView.setBackgroundColor(ContextCompat.getColor(activity, R.color.graph_background_colour));
        }

        start_fresh_graph = true;
        last_added_timestamp = 0;
    }
}
