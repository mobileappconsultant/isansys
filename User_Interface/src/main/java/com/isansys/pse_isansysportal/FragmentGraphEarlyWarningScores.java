package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isansys.common.measurements.MeasurementEarlyWarningScore;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;
import com.jjoe64.graphview.series.DataPoint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
public class FragmentGraphEarlyWarningScores extends FragmentGraph
{
    private final String TAG = this.getClass().getSimpleName();

    private View view;


    @Override
    public void onCreate(Bundle saved)
    {
        graph_vital_sign_type = VitalSignType.EARLY_WARNING_SCORE;

        super.onCreate(saved);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        view = inflater.inflate(R.layout.early_warning_score_graph, container, false); // Inflate the layout for this fragment

        return view;
    }


    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume");

        setupGraph();

        redrawNormalMeasurementGraph(main_activity_interface.getCachedMeasurements(graph_vital_sign_type));

        super.onResume();
    }


    public <T extends MeasurementVitalSign> void redrawNormalMeasurementGraph(ArrayList<T> cached_measurements)
    {
        if (cached_measurements != null)
        {
            Log.d(TAG, "onResume (cached measurement): the size of cached measurement is " + cached_measurements.size());

            ArrayList<DataPoint> dataPoints = new ArrayList<>();
            ArrayList<DataPoint> dataPointsMaxPossible = new ArrayList<>();

            long session_start_time = main_activity_interface.getSessionStartDate();

            // Using Iterator here instead of simple For loop to ensure we dont get a concurrent modification exception if new measurements are added to the measurement cache while we read them here
            for (T cached_measurement : cached_measurements) {
                MeasurementEarlyWarningScore measurement = (MeasurementEarlyWarningScore) cached_measurement;

                dataPoints.add(new DataPoint(measurement.timestamp_in_ms - session_start_time, measurement.early_warning_score));
                dataPointsMaxPossible.add(new DataPoint(measurement.timestamp_in_ms - session_start_time, measurement.max_possible_score));
            }

            bulkLoadNormal(dataPoints.toArray(new DataPoint[dataPoints.size()]));
            bulkLoadMaxPossible(dataPointsMaxPossible.toArray(new DataPoint[dataPoints.size()]));
        }
    }


    private void setupGraph()
    {
        super.setupGraph(view.findViewById(R.id.plotEarlyWarningScores), getActivity().findViewById(R.id.seekBarGraphScaleEarlyWarningScoresLeft), getActivity().findViewById(R.id.seekBarGraphScaleEarlyWarningScoresRight));
    }
}
