package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.VitalSignType;

import org.jetbrains.annotations.NotNull;

public class FragmentGraphTemperature extends FragmentGraph
{
    private final String TAG = this.getClass().getSimpleName();

    private View view;

    @Override
    public void onCreate(Bundle saved)
    {
        graph_vital_sign_type = VitalSignType.TEMPERATURE;
        graph_manually_entered_vital_sign_type = VitalSignType.MANUALLY_ENTERED_TEMPERATURE;

        super.onCreate(saved);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        view = inflater.inflate(R.layout.temperature_graph, container, false);

        return view;
    }


    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume");

        setupGraph();

        super.redrawGraph();

        super.onResume();
    }


    private void setupGraph()
    {
        super.setExpectedMeasurementIntervalInMilliseconds(main_activity_interface.getMeasurementIntervalInSecondsIfPresent(SensorType.SENSOR_TYPE__TEMPERATURE) * DateUtils.SECOND_IN_MILLIS);

        super.setupGraph(view.findViewById(R.id.plotTemperature), getActivity().findViewById(R.id.seekBarGraphScaleLifetempTemperatureLeft), getActivity().findViewById(R.id.seekBarGraphScaleLifetempTemperatureRight));
    }
}
