package com.isansys.pse_isansysportal;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// This is displayed once the user has clicked on a Manual Vital Sign. This gives the list of the buttons - e.g. No O2, Med O2, High O2, or Yes/No buttons
public class FragmentObservationButtonSelection extends FragmentIsansys implements ManualVitalSignsButtonRecyclerViewAdapter.ItemClickListener
{
    private RecyclerView recyclerView;

    private int vital_sign_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	View v = inflater.inflate(R.layout.observation_display_button_selection, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);

        Bundle bundle = this.getArguments();
        vital_sign_id = bundle.getInt(MainActivity.VITAL_SIGN_ID, 0);

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        int numberOfColumns = 1;

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        showButtons(vital_sign_id);
    }


    private void showButtons(int vital_sign_id)
    {
        boolean show_rhs_description = main_activity_interface.showRightHandSideDescriptionForObservationEntry(vital_sign_id);

        // This is the number of buttons on the screen, along with each buttons text, colour, EWS value, RHS description text
        ManualVitalSignsButtonRecyclerViewAdapter adapter = new ManualVitalSignsButtonRecyclerViewAdapter(getContext(), main_activity_interface.getManualVitalSignButtons(vital_sign_id), show_rhs_description);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(ManualVitalSignButtonDescriptor button_clicked)
    {
        main_activity_interface.observationSetMeasurementValueEntered(button_clicked.vital_sign_type_id, button_clicked.button_id);
    }
}