package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentManualVitalSignValidityTime extends FragmentIsansysWithTimestamp
{
    private static final String TAG = FragmentManualVitalSignValidityTime.class.getName();

    private FragmentManualVitalSignsValidityTimeSelectionList fragmentManualVitalSignValiditySelection;

    private TextView textTop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.manual_vital_signs, container, false);

        textTop = v.findViewById(R.id.textTop);
        textTop.setVisibility(View.INVISIBLE);

        return v;
    }


    @Override
    public void onPause()
    {
        super.onPause();

        removeFragmentIfShown(fragmentManualVitalSignValiditySelection);
    }


    @Override
    public void onResume()
    {
        super.onResume();

        textTop.setText(getResources().getString(R.string.select_how_long_to_use_this_measurement_in_the_early_warning_score));
        textTop.setVisibility(View.VISIBLE);

        fragmentManualVitalSignValiditySelection = new FragmentManualVitalSignsValidityTimeSelectionList();
        if(getActivity() != null)
        {
            getChildFragmentManager().beginTransaction().replace(R.id.frameLayoutManualVitalSignsEntry, fragmentManualVitalSignValiditySelection).commit();
        }
        else
        {
            Log.e(TAG, "handleVitalSignSelectionAfterVitalSignEntry : getActivity() is null");
        }
    }
}
