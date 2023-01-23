package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.VitalSignType;

import org.jetbrains.annotations.NotNull;


public class FragmentGraphManuallyEntered extends FragmentGraph
{
    private final String TAG = this.getClass().getSimpleName();

    private View view;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        view = inflater.inflate(R.layout.graph_manually_entered, container, false);

        return view;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        Bundle bundle = this.getArguments();
        int x = bundle.getInt(MainActivity.VITAL_SIGN_TYPE, 0);
        int y = bundle.getInt(MainActivity.MANUALLY_ENTERED_VITAL_SIGN_TYPE, 0);

        // Even though this fragment is Manual Vitals ONLY, it still needs to call getGraphColourBands to get the background colours
        graph_vital_sign_type = VitalSignType.values()[x];
        graph_manually_entered_vital_sign_type = VitalSignType.values()[y];
    }


    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume : graph_manually_entered_vital_sign_type = " + graph_manually_entered_vital_sign_type);

        SensorType sensor_type = main_activity_interface.getSensorTypeForVitalSign(graph_manually_entered_vital_sign_type);
        super.setExpectedMeasurementIntervalInMilliseconds(main_activity_interface.getMeasurementIntervalInSecondsIfPresent(sensor_type) * DateUtils.SECOND_IN_MILLIS);

        super.setupGraph(view.findViewById(R.id.plotManuallyEntered), view.findViewById(R.id.seekBarGraphScaleManuallyEnteredLeft), view.findViewById(R.id.seekBarGraphScaleManuallyEnteredRight));

        super.getManualVitalSignsFromMainActivity();

        super.onResume();
    }
}
