package com.isansys.pse_isansysportal;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentObservationSetConfirmation extends FragmentIsansys implements View.OnClickListener, ObservationSetConfirmationRecyclerViewAdapter.AdapterInterface
{
    private TextView textObservationSetTimeAndValidity;
    private RecyclerView recyclerView;

    private Button buttonBigButtonTop;
    private Button buttonBigButtonBottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.manual_vital_signs_confirmation, container, false);

        textObservationSetTimeAndValidity = v.findViewById(R.id.textObservationSetTimeAndValidity);

        recyclerView = v.findViewById(R.id.recyclerView);

        buttonBigButtonTop = v.findViewById(R.id.buttonBigButtonTop);
        buttonBigButtonTop.setOnClickListener(this);
        buttonBigButtonTop.setVisibility(View.VISIBLE);

        buttonBigButtonBottom = v.findViewById(R.id.buttonBigButtonBottom);
        buttonBigButtonBottom.setOnClickListener(this);
        buttonBigButtonBottom.setVisibility(View.INVISIBLE);

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        resetButtons();

        int numberOfColumns = 1;

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        ObservationSetConfirmationRecyclerViewAdapter adapter = new ObservationSetConfirmationRecyclerViewAdapter(getContext(), main_activity_interface.getObservationSetEntered());
        adapter.setAdapterInterface(this);
        recyclerView.setAdapter(adapter);

        textObservationSetTimeAndValidity.setText(main_activity_interface.getObservationSetVitalSignSelectionTopText());
    }


    public enum ButtonMode
    {
        VALIDITY_TIME_ENTER,
        VALIDITY_TIME_CONFIRMATION
    }


    private ButtonMode button_mode = ButtonMode.VALIDITY_TIME_ENTER;


    @Override
    public void onClick(View v)
    {
        if(getActivity() != null)
        {
            int id = v.getId();

            if (id == R.id.buttonBigButtonTop)
            {
                switch (button_mode)
                {
                    case VALIDITY_TIME_ENTER:
                    {
                        buttonBigButtonBottom.setText(getResources().getString(R.string.confirm));
                        showBottomControlButton(true);

                        buttonBigButtonTop.setText(getResources().getString(R.string.cancel));
                        showTopControlButton(true);

                        button_mode = ButtonMode.VALIDITY_TIME_CONFIRMATION;
                    }
                    break;

                    case VALIDITY_TIME_CONFIRMATION:
                    {
                        // User pressed Cancel
                        resetButtons();
                    }
                    break;
                }
            }
            else if (id == R.id.buttonBigButtonBottom)
            {
                switch (button_mode)
                {
                    case VALIDITY_TIME_ENTER:
                    {
                        // Button invisible
                    }
                    break;

                    case VALIDITY_TIME_CONFIRMATION:
                    {
                        main_activity_interface.storeObservationSet();

                        // Do not care here as this fragment will be destroyed now.
                        //button_mode = ButtonMode.VALIDITY_TIME_ENTER;
                    }
                    break;
                }
            }
        }
    }


    private void showBottomControlButton(boolean show_button)
    {
        if (show_button)
        {
            buttonBigButtonBottom.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonBigButtonBottom.setVisibility(View.INVISIBLE);
        }
    }


    private void showTopControlButton(boolean show_button)
    {
        if (show_button)
        {
            buttonBigButtonTop.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonBigButtonTop.setVisibility(View.INVISIBLE);
        }
    }


    private void resetButtons()
    {
        showBottomControlButton(false);

        buttonBigButtonTop.setText(getResources().getString(R.string.enter));

        showTopControlButton(true);

        button_mode = ButtonMode.VALIDITY_TIME_ENTER;
    }


    public Drawable getDrawable(int ews_score)
    {
        return main_activity_interface.getDrawableCircle(ews_score);
    }
}
