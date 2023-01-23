package com.isansys.pse_isansysportal;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentManualVitalSignsValidityTimeSelectionList extends FragmentIsansys implements ManualVitalSignsValidityTimeSelectionRecyclerViewAdapter.ItemClickListener
{
    private TextView textValidityTimeSelected;

    private RecyclerView recyclerViewValidityTimeSelection;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	View v = inflater.inflate(R.layout.vital_sign_validity_time_selection, container, false);

        recyclerViewValidityTimeSelection = v.findViewById(R.id.recyclerViewValidityTimeSelection);

        textValidityTimeSelected = v.findViewById(R.id.textValidityTimeSelected);
        textValidityTimeSelected.setText(R.string.blank_string);

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        
        int numberOfColumns = 3;

        recyclerViewValidityTimeSelection.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        ManualVitalSignsValidityTimeSelectionRecyclerViewAdapter adapter = new ManualVitalSignsValidityTimeSelectionRecyclerViewAdapter(getContext(), main_activity_interface.getVitalSignValidityTimes());
        adapter.setClickListener(this);
        adapter.enableEntry(true);
        recyclerViewValidityTimeSelection.setAdapter(adapter);
    }


    @Override
    public void onItemClick(VitalSignValidityTimeDescriptor time_selected)
    {
        main_activity_interface.measurementValidityTimeSelected(time_selected);

        //selection = time_selected.value;
        textValidityTimeSelected.setText(time_selected.display_value);
    }
}