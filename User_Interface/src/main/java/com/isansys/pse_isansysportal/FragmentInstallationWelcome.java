package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.core.content.res.ResourcesCompat;

public class FragmentInstallationWelcome extends FragmentIsansys
{
    private Button buttonBigButtonOne;
    private Button buttonBigButtonTwo;

    private Animation animScale = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v;

        v = inflater.inflate(R.layout.installation_welcome, container, false);

        buttonBigButtonOne = v.findViewById(R.id.buttonBigButtonOne);
        buttonBigButtonOne.setOnClickListener(x -> main_activity_interface.adminModeExitPressed());

        buttonBigButtonTwo = v.findViewById(R.id.buttonBigButtonTwo);
        buttonBigButtonTwo.setOnClickListener(x ->
        {
            if (main_activity_interface.haveCamera())
            {
                main_activity_interface.showInstallationServerAddressScanFragment();
            }
            else
            {
                main_activity_interface.dummyAdminCode();
            }
        });

        if(getActivity() != null)
        {
            animScale = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_animation);
        }

        return v;
    }
    

    @Override
    public void onResume()
    {
        super.onResume();

        updateButtonsDependingOnConnectionStatus();
    }


    public void updateButtonsDependingOnConnectionStatus()
    {
        buttonBigButtonOne.clearAnimation();
        buttonBigButtonTwo.clearAnimation();

        if (main_activity_interface.isConnectedToNetwork())
        {
            if(main_activity_interface.stopUiFastUpdates() == false)
            {
                buttonBigButtonTwo.startAnimation(animScale);
            }

            buttonBigButtonTwo.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_blue, null));
            buttonBigButtonTwo.setClickable(true);
        }
        else
        {
            if(main_activity_interface.stopUiFastUpdates() == false)
            {
                buttonBigButtonOne.startAnimation(animScale);
            }

            buttonBigButtonTwo.clearAnimation();
            buttonBigButtonTwo.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_gray, null));
            buttonBigButtonTwo.setClickable(false);
        }
    }
}
