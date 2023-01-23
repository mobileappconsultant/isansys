package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class FragmentSoftwareUpdateAvailable extends FragmentIsansys implements View.OnClickListener
{
    private final String TAG = this.getClass().getSimpleName();

    private Button buttonEnterUpdateMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.software_update_available, container, false); // Inflate the layout for this fragment

        buttonEnterUpdateMode = v.findViewById(R.id.buttonEnterUpdateMode);
        buttonEnterUpdateMode.setOnClickListener(this);
        buttonEnterUpdateMode.setClickable(true);

        buttonEnterUpdateMode.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_animation));

        return v;
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.buttonEnterUpdateMode)
        {
            buttonEnterUpdateModeClick();
        }
    }


    private void buttonEnterUpdateModeClick()
    {
        main_activity_interface.enterUpdateMode();
    }
}
