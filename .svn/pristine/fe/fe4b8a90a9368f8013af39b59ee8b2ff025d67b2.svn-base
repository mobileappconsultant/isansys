package com.isansys.pse_isansysportal;

import com.isansys.pse_isansysportal.enums.ObservationSetEntryType;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentObservationSetVitalSignEntry extends FragmentIsansys
{
    private final String TAG = FragmentObservationSetVitalSignEntry.class.getName();

    private FragmentKeypadVitalSignEntry fragmentKeypadVitalSignEntry;
    private FragmentObservationButtonSelection fragmentButtonSelection;

    private TextView textTop;

    private int vital_sign_id;
    private ObservationSetEntryType data_entry_type;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.manual_vital_signs, container, false);

        textTop = v.findViewById(R.id.textTop);

        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            vital_sign_id = bundle.getInt(MainActivity.VITAL_SIGN_ID, 0);
            data_entry_type = ObservationSetEntryType.values()[bundle.getInt(MainActivity.VITAL_ENTRY_METHOD, 0)];
        }

        return v;
    }


    @Override
    public void onPause()
    {
        super.onPause();

        removeFragments();
    }


    @Override
    public void onResume()
    {
        super.onResume();

        Log.e(TAG, "FragmentObservationSetVitalSignEntry onResume : vital_sign_id = " + vital_sign_id);

        handleVitalSignSelection();
    }


    private void handleVitalSignSelection()
    {
        textTop.setText(main_activity_interface.updateScreenBasedOnManualVitalSignType(vital_sign_id));

        switch (data_entry_type)
        {
            case KEYPAD:
            {
                if(getActivity() != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt(MainActivity.KEYPAD_ENTRY_TYPE, FragmentKeypadVitalSignEntry.KeyPadEntryType.VITAL_SIGN.ordinal());
                    bundle.putInt(MainActivity.VITAL_SIGN_TYPE, vital_sign_id);
                    bundle.putString(MainActivity.KEYPAD_INITIAL_VALUE, "");

                    fragmentKeypadVitalSignEntry = new FragmentKeypadVitalSignEntry();
                    fragmentKeypadVitalSignEntry.setArguments(bundle);

                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayoutManualVitalSignsEntry, fragmentKeypadVitalSignEntry).commit();
                }
                else
                {
                    Log.e(TAG, "handleVitalSignSelection : "+ vital_sign_id + " getActivity() is null");
                }
            }
            break;

            case BUTTON_SELECTION:
            {
                if(getActivity() != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt(MainActivity.VITAL_SIGN_ID, vital_sign_id);

                    fragmentButtonSelection = new FragmentObservationButtonSelection();
                    fragmentButtonSelection.setArguments(bundle);

                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayoutManualVitalSignsEntry, fragmentButtonSelection).commit();
                }
                else
                {
                    Log.e(TAG, "handleVitalSignSelection : "+ vital_sign_id + " getActivity() is null");
                }
            }
            break;
        }
    }


    private void removeFragments()
    {
        if(getActivity() != null)
        {
            switch (data_entry_type)
            {
                case KEYPAD:
                {
                    removeFragmentIfShown(fragmentKeypadVitalSignEntry);
                }
                break;

                case BUTTON_SELECTION:
                {
                    removeFragmentIfShown(fragmentButtonSelection);
                }
                break;
            }
        }
    }


    public void setMeasurementValueToKeypad()
    {
        FragmentKeypadVitalSignEntry fragment = (FragmentKeypadVitalSignEntry) (getChildFragmentManager().findFragmentById(R.id.frameLayoutManualVitalSignsEntry));
        if (UtilityFunctions.isFragmentAddedAndResumed(fragment))
        {
            main_activity_interface.observationSetMeasurementValueEntered(vital_sign_id, fragment.getEnteredText());
        }
    }
}
