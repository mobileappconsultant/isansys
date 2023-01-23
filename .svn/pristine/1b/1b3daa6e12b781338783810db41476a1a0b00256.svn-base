package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.VitalSignType;

import org.jetbrains.annotations.NotNull;

public class FragmentGraphLifetouchNormalModeHeartRate extends FragmentGraph
{
    private final String TAG = this.getClass().getSimpleName();

    private View view;

    @Override
    public void onCreate(Bundle saved)
    {
        graph_vital_sign_type = VitalSignType.HEART_RATE;
        graph_manually_entered_vital_sign_type = VitalSignType.MANUALLY_ENTERED_HEART_RATE;

        super.onCreate(saved);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        view = inflater.inflate(R.layout.lifetouch_normal_mode_heart_rate, container, false); // Inflate the layout for this fragment

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
            redrawSetupModeLogGraph(main_activity_interface.getSetupModeLog(SensorType.SENSOR_TYPE__LIFETOUCH));
        }

        super.onResume();
    }


    private void setupGraph()
    {
        super.setupGraph(view.findViewById(R.id.plotHR), getActivity().findViewById(R.id.seekBarGraphScaleLifetouchHeartRateLeft), getActivity().findViewById(R.id.seekBarGraphScaleLifetouchHeartRateRight));
    }
}
