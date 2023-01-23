package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

public class FragmentLifetempNotAttached extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.lifetemp_not_attached, container, false); // Inflate the layout for this fragment
    }
}
