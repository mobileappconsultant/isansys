package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isansys.common.measurements.VitalSignType;

import org.jetbrains.annotations.NotNull;

public class FragmentGraphWeight extends FragmentGraph
{
    private final String TAG = this.getClass().getSimpleName();

    private View view;

    @Override
    public void onCreate(Bundle saved)
    {
        graph_vital_sign_type = VitalSignType.WEIGHT;
        graph_manually_entered_vital_sign_type = VitalSignType.MANUALLY_ENTERED_WEIGHT;

        super.onCreate(saved);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        view = inflater.inflate(R.layout.graph_weight, container, false);

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
        super.setupGraph(view.findViewById(R.id.plotWeight), getActivity().findViewById(R.id.seekBarGraphScaleWeightLeft), getActivity().findViewById(R.id.seekBarGraphScaleWeightRight));
    }
}
