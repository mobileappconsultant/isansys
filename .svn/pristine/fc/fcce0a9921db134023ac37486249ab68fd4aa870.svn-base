package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentPatientCaseIdEntry extends FragmentIsansys
{
    private final String TAG = FragmentPatientCaseIdEntry.class.getName();

    private FragmentKeypadVitalSignEntry fragmentKeypad;
    private Button buttonGetPatientDetailsFromServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.case_id_entry, container, false);

        buttonGetPatientDetailsFromServer = v.findViewById(R.id.buttonGetPatientDetailsFromServer);
        //buttonGetPatientDetailsFromServer.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_animation));
        buttonGetPatientDetailsFromServer.setOnClickListener(x -> main_activity_interface.getPatientNameFromPatientId(fragmentKeypad.getEnteredText()));

        return v;
    }


    @Override
    public void onPause()
    {
        super.onPause();

        if(getActivity() != null)
        {
            removeFragmentIfShown(fragmentKeypad);
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();

        if(getActivity() != null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.KEYPAD_ENTRY_TYPE, FragmentKeypadVitalSignEntry.KeyPadEntryType.PATIENT_CASE_ID.ordinal());
            bundle.putString(MainActivity.KEYPAD_INITIAL_VALUE, main_activity_interface.getPatientInfo().getHospitalPatientId());

            fragmentKeypad = new FragmentKeypadVitalSignEntry();
            fragmentKeypad.setArguments(bundle);

            getChildFragmentManager().beginTransaction().replace(R.id.frameLayoutCaseIdEntry, fragmentKeypad).commit();
        }
        else
        {
            Log.e(TAG, "FragmentKeypadVitalSignEntry : getActivity() is null");
        }
    }


    public void showLookupButton(boolean show)
    {
        if (show)
        {
            buttonGetPatientDetailsFromServer.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonGetPatientDetailsFromServer.setVisibility(View.GONE);
        }
    }


    public String getEnteredText()
    {
        return fragmentKeypad.getEnteredText();
    }
}
