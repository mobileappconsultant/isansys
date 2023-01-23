package com.isansys.pse_isansysportal;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// This is the list of manual vital signs the user can add for this Observation Set
public class FragmentObservationSetSelectionList extends FragmentIsansys implements ObservationSetSelectionRecyclerViewAdapter.AdapterInterface
{
    private TextView textTop;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	View v = inflater.inflate(R.layout.observation_display_measurement_selection, container, false);

        textTop = v.findViewById(R.id.textTop);
        recyclerView = v.findViewById(R.id.recyclerView);

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        textTop.setText(main_activity_interface.getObservationSetVitalSignSelectionTopText());

        int numberOfColumns;

        if (main_activity_interface.isScreenLandscape())
        {
            numberOfColumns = 4;
        }
        else
        {
            numberOfColumns = 2;
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        showVitalSigns();
    }


    private void showVitalSigns()
    {
        ObservationSetSelectionRecyclerViewAdapter adapter = new ObservationSetSelectionRecyclerViewAdapter(getContext(), main_activity_interface.getManuallyEnteredVitalSigns());
        adapter.setAdapterInterface(this);
        recyclerView.setAdapter(adapter);
    }


    public void onButtonItemClick(ManuallyEnteredVitalSignDescriptor vital_sign_selected)
    {
        main_activity_interface.observationSetMeasurementSelected(vital_sign_selected);
    }


    public void onCancelItemClick(ManuallyEnteredVitalSignDescriptor vital_sign_selected)
    {
        main_activity_interface.removeManualVitalSignFromObservationSet(vital_sign_selected);
    }


    public ManualVitalSignBeingEntered getManualVitalSignValueForThisVitalSignDescriptor(ManuallyEnteredVitalSignDescriptor vital_sign_selected)
    {
        return main_activity_interface.getManualVitalSignBeingEntered(vital_sign_selected.vital_sign_id);
    }
}