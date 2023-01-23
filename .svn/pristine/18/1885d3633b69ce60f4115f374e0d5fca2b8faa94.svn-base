package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.VitalSignType;

import org.jetbrains.annotations.NotNull;

public class FragmentGraphPulseOxNormalMode extends FragmentGraph
{
    private final String TAG = this.getClass().getSimpleName();

    private View view;

    @Override
    public void onCreate(Bundle saved)
    {
        graph_vital_sign_type = VitalSignType.SPO2;
        graph_manually_entered_vital_sign_type = VitalSignType.MANUALLY_ENTERED_SPO2;

        super.onCreate(saved);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        view = inflater.inflate(R.layout.pulse_ox_normal_mode, container, false);

        return view;
    }


    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume");

        setupGraph();

        super.redrawGraph();

        if (main_activity_interface.getSetupModeBlobsShouldBeShown())
        {
            redrawSetupModeLogGraph(main_activity_interface.getSetupModeLog(SensorType.SENSOR_TYPE__SPO2));
        }
        else
        {
            hideSetupModeBlobs();
        }

        super.onResume();
    }


    private void setupGraph()
    {
        super.setupGraph(view.findViewById(R.id.plotPulseOximeter), getActivity().findViewById(R.id.seekBarGraphScalePulseOxLeft), getActivity().findViewById(R.id.seekBarGraphScalePulseOxRight));
    }


    public void showSetupModeBlobs(boolean show)
    {
        if (show)
        {
            redrawSetupModeLogGraph(main_activity_interface.getSetupModeLog(SensorType.SENSOR_TYPE__SPO2));
        }
        else
        {
            hideSetupModeBlobs();
        }
    }
}
