package com.isansys.pse_isansysportal;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class FragmentGraphRawAccelerometerMode extends FragmentIsansys
{
    private final String TAG = FragmentGraphRawAccelerometerMode.class.getName();

    // GraphView variables
    private LineGraphSeries<DataPoint> graphViewSeriesRawAccelerometerModeXAxis = null;
    private LineGraphSeries<DataPoint> graphViewSeriesRawAccelerometerModeYAxis = null;
    private LineGraphSeries<DataPoint> graphViewSeriesRawAccelerometerModeZAxis = null;

    private double graphViewAxisX;
    private double graphViewAxisY;
    private double graphViewAxisZ;

    private final int max_data_points = 500;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.graph_raw_accelerometer_mode, container, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Accelerometer data is 8 bit so max of 256
        double y_axis_max = 256;
        double y_axis_min = 0;

        // Real-time data for Line Chart by GraphView
        graphViewSeriesRawAccelerometerModeXAxis = new LineGraphSeries<>(new DataPoint[]
                {
                        new DataPoint(0.0d, y_axis_max/2)
                });
        graphViewSeriesRawAccelerometerModeYAxis = new LineGraphSeries<>(new DataPoint[]
                {
                        new DataPoint(0.0d, y_axis_max/2)
                });
        graphViewSeriesRawAccelerometerModeZAxis = new LineGraphSeries<>(new DataPoint[]
                {
                        new DataPoint(0.0d, y_axis_max/2)
                });

        graphViewAxisX = 0.0d;
        graphViewAxisY = 0.0d;
        graphViewAxisZ = 0.0d;

        // Draw the line with Red color
        graphViewSeriesRawAccelerometerModeXAxis.setColor(Color.rgb(200, 0, 0));
        graphViewSeriesRawAccelerometerModeYAxis.setColor(Color.rgb(0, 200, 0));
        graphViewSeriesRawAccelerometerModeZAxis.setColor(Color.rgb(0, 0, 200));

        // Make specific line thickness
        graphViewSeriesRawAccelerometerModeXAxis.setThickness(3);
        graphViewSeriesRawAccelerometerModeYAxis.setThickness(3);
        graphViewSeriesRawAccelerometerModeZAxis.setThickness(3);

        graphViewSeriesRawAccelerometerModeXAxis.setTitle("X");
        graphViewSeriesRawAccelerometerModeYAxis.setTitle("Y");
        graphViewSeriesRawAccelerometerModeZAxis.setTitle("Z");

        GraphView graphView = new GraphView(getActivity());

        // Setup the Y Axis viewport
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(y_axis_min);
        graphView.getViewport().setMaxY(y_axis_max);

        // Setup the X Axis viewport
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(max_data_points);

        // GraphView set styles
        graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
        graphView.getGridLabelRenderer().setNumVerticalLabels(4 * 3);
        graphView.getGridLabelRenderer().setHighlightZeroLines(false);

        // Add defined graph series to plot area
        graphView.addSeries(graphViewSeriesRawAccelerometerModeXAxis);
        graphView.addSeries(graphViewSeriesRawAccelerometerModeYAxis);
        graphView.addSeries(graphViewSeriesRawAccelerometerModeZAxis);

        // Add a legend
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setFixedPosition(-10,-10);
        graphView.getLegendRenderer().setBackgroundColor(Color.WHITE);
        graphView.getLegendRenderer().setSpacing(15);
        graphView.getLegendRenderer().setTextSize(40);

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                return "";
            }
        });

        LinearLayout graphViewLayout1 = getActivity().findViewById(R.id.plotRawAccelerometer);
        graphViewLayout1.addView(graphView);   // Show on Layout
    }


    public void updateSample(short x, short y, short z, long timestamp_in_ms)
    {
        if (graphViewSeriesRawAccelerometerModeXAxis != null)
        {
            graphViewSeriesRawAccelerometerModeXAxis.appendData(new DataPoint(++graphViewAxisX, x), true, max_data_points);  // Add Y axis value
        }

        if (graphViewSeriesRawAccelerometerModeYAxis != null)
        {
            graphViewSeriesRawAccelerometerModeYAxis.appendData(new DataPoint(++graphViewAxisY, y), true, max_data_points);  // Add Y axis value
        }

        if (graphViewSeriesRawAccelerometerModeZAxis != null)
        {
            graphViewSeriesRawAccelerometerModeZAxis.appendData(new DataPoint(++graphViewAxisZ, z), true, max_data_points);  // Add Y axis value
        }
    }
}
