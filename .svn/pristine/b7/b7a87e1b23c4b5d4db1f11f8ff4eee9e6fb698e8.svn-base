package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.FragmentTransaction;

public class FragmentLogCatWithInputSelection extends FragmentIsansys
{
    private FragmentLogCatDisplay fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.logcat_with_input_selection, container, false);

        CheckBox checkBoxScrollLock = v.findViewById(R.id.checkBoxScrollLock);
        checkBoxScrollLock.setChecked(true);
        checkBoxScrollLock.setOnCheckedChangeListener((buttonView, isChecked) -> fragment.setStackStatus(isChecked));

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();

		fragment = new FragmentLogCatDisplay();

		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.FrameLayoutLogCatDisplay, fragment);
		transaction.addToBackStack(null);
	    transaction.commit();
    }
}
