package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.isansys.common.ThresholdSet;
import com.isansys.common.ThresholdSetAgeBlockDetail;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FragmentModeSelection extends FragmentIsansys implements OnClickListener
{
    private final String TAG = this.getClass().getSimpleName();

    private Button buttonBigButtonOne;
    private Button buttonBigButtonTwo;
    private Button buttonBigButtonThree;
    private Button buttonBigButtonFour;
    private Button buttonBigButtonFive;
    private Button buttonBigButtonSix;

    private Animation animScale = null;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View  v;

        if (main_activity_interface.isSessionInProgress())
        {
            if (main_activity_interface.includeManualVitalSignEntry())
            {
                // CHANGED TO WEBPAGES FOR RELEASE 33
                //if(main_activity_interface.getVideoCallsEnabled())
                if (main_activity_interface.getViewWebPagesEnabled() && (main_activity_interface.getWebPageButtons().size() > 0))
                {
                    v = inflater.inflate(R.layout.mode_selection_six_buttons, container, false);

                    // 6 buttons
                    Button buttonBigButtonFour = v.findViewById(R.id.buttonBigButtonFour);
                    buttonBigButtonFour.setOnClickListener(this);

                    buttonBigButtonFive = v.findViewById(R.id.buttonBigButtonFive);
                    buttonBigButtonFive.setOnClickListener(this);

                    buttonBigButtonSix = v.findViewById(R.id.buttonBigButtonSix);
                    buttonBigButtonSix.setOnClickListener(this);
                }
                else
                {
                    // 5 buttons
                    v = inflater.inflate(R.layout.mode_selection_five_buttons, container, false);

                    Button buttonBigButtonFour = v.findViewById(R.id.buttonBigButtonFour);
                    buttonBigButtonFour.setOnClickListener(this);

                    buttonBigButtonFive = v.findViewById(R.id.buttonBigButtonFive);
                    buttonBigButtonFive.setOnClickListener(this);
                }
            }
            else
            {
                // CHANGED TO WEBPAGES FOR RELEASE 33
                //if(main_activity_interface.getVideoCallsEnabled())
                if(main_activity_interface.getViewWebPagesEnabled())
                {
                    v = inflater.inflate(R.layout.mode_selection_four_buttons, container, false);

                    buttonBigButtonFour = v.findViewById(R.id.buttonBigButtonFour);
                    buttonBigButtonFour.setOnClickListener(this);
                }
                else
                {
                    v = inflater.inflate(R.layout.mode_selection_three_buttons, container, false);
                }
            }

            buttonBigButtonOne = v.findViewById(R.id.buttonBigButtonOne);
            buttonBigButtonOne.setOnClickListener(this);
    
            buttonBigButtonTwo = v.findViewById(R.id.buttonBigButtonTwo);
            buttonBigButtonTwo.setOnClickListener(this);

            buttonBigButtonThree = v.findViewById(R.id.buttonBigButtonThree);
            buttonBigButtonThree.setOnClickListener(this);
        }
        else
        {
            if (main_activity_interface.getViewWebPagesEnabled() && (main_activity_interface.getWebPageButtons().size() > 0))
            {
                v = inflater.inflate(R.layout.mode_selection_three_buttons_one_plus_two, container, false);

                buttonBigButtonThree = v.findViewById(R.id.buttonBigButtonThree);
                buttonBigButtonThree.setOnClickListener(this);
            }
            else
            {
                v = inflater.inflate(R.layout.mode_selection_two_buttons, container, false);
            }

            buttonBigButtonOne = v.findViewById(R.id.buttonBigButtonOne);
            buttonBigButtonOne.setOnClickListener(this);

            buttonBigButtonTwo = v.findViewById(R.id.buttonBigButtonTwo);
            buttonBigButtonTwo.setOnClickListener(this);
        }

        if(getActivity() != null)
        {
            animScale = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_animation);
        }
        else
        {
            Log.e(TAG, "onCreateView : FragmentModeSelection getActivity() is null");
        }

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        
        if (main_activity_interface.isSessionInProgress())
        {
            Log.d(TAG, "session in progress");
            buttonBigButtonOne.setText(getResources().getString(R.string.textPatientVitalsDisplay));
            buttonBigButtonThree.setText(getResources().getString(R.string.textStopMonitoringCurrentPatient));

            if (main_activity_interface.includeManualVitalSignEntry())
            {
/*
// REMOVED FOR TIME BEING SO CAN USE THE BUTTON FOR RELEASE 33 FOR WEBPAGES
                if (main_activity_interface.getVideoCallsEnabled())
                {
                    // six buttons...
                    buttonBigButtonSix.setText(getResources().getString(R.string.video_call));
                }
*/
                if (buttonBigButtonSix != null)
                {
                    if (main_activity_interface.getViewWebPagesEnabled())
                    {
                        buttonBigButtonSix.setText(getResources().getString(R.string.view_webpages));
                    }
                    else
                    {
                        buttonBigButtonSix.setVisibility(View.GONE);
                    }
                }

                buttonBigButtonFive.setText(getResources().getString(R.string.annotations));
            }
            else
            {
/*
// REMOVED FOR TIME BEING SO CAN USE THE BUTTON FOR RELEASE 33 FOR WEBPAGES
                if (main_activity_interface.getVideoCallsEnabled())
                {
                    // four buttons...
                    buttonBigButtonFour.setText(getResources().getString(R.string.video_call));
                }
*/
                if (main_activity_interface.getViewWebPagesEnabled())
                {
                    buttonBigButtonFour.setText(getResources().getString(R.string.view_webpages));
                }
            }
        }
        else
        {
            Log.d(TAG, "session NOT in progress");
            buttonBigButtonOne.setText(getResources().getString(R.string.textStartMonitoringAPatient));

            if (main_activity_interface.getManufacturingModeEnabled())
            {
                buttonBigButtonTwo.setText(getResources().getString(R.string.check_packaging));
            }
            else
            {
                buttonBigButtonTwo.setText(getResources().getString(R.string.textCheckDeviceStatus));
            }

            if (main_activity_interface.getViewWebPagesEnabled() && (main_activity_interface.getWebPageButtons().size() > 0))
            {
                buttonBigButtonThree.setText(getResources().getString(R.string.view_webpages));
            }

            // check threshold state...
            int number_of_age_blocks = 0;

            ArrayList<ThresholdSet> thresholdSets = main_activity_interface.getEarlyWarningScoreThresholdSets();
            for (ThresholdSet thresholdSet : thresholdSets)
            {
                for (ThresholdSetAgeBlockDetail age_block_detail : thresholdSet.list_threshold_set_age_block_detail)
                {
                    number_of_age_blocks ++;
                }
            }

            setStartSessionClickable(number_of_age_blocks > 0);
        }
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.buttonBigButtonOne)
        {
            handleButtonOneClick();
        }
        else if (id == R.id.buttonBigButtonTwo)
        {
            handleButtonTwoClick();
        }
        else if (id == R.id.buttonBigButtonThree)
        {
            handleButtonThreeClick();
        }
        else if (id == R.id.buttonBigButtonFour)
        {
            handleButtonFourClick();
        }
        else if (id == R.id.buttonBigButtonFive)
        {
            handleButtonFiveClick();
        }
        else if (id == R.id.buttonBigButtonSix)
        {
            handleButtonSixClick();
        }
    }


    private void handleButtonOneClick()
    {
        if (main_activity_interface.isSessionInProgress())
        {
            main_activity_interface.patientVitalsDisplaySelected();
        }
        else
        {
            // Do an NTP Time Sync here, as its just before a session is started, but it has NOT "started" yet.
            main_activity_interface.sendTimeSyncCommand();

            main_activity_interface.startMonitoringPatientPressed();
        }
    }


    private void handleButtonTwoClick()
    {
        if (main_activity_interface.isSessionInProgress())
        {
            main_activity_interface.changeSessionSettingsPressed();
        }
        else
        {
            if (main_activity_interface.getManufacturingModeEnabled())
            {
                main_activity_interface.checkPackagingSelected();
            }
            else
            {
                main_activity_interface.checkDeviceStatusPressed();
            }
        }
    }


    private void handleButtonThreeClick()
    {
        if (main_activity_interface.isSessionInProgress())
        {
            main_activity_interface.stopMonitoringCurrentPatient();
        }
        else
        {
            if (main_activity_interface.getViewWebPagesEnabled() && (main_activity_interface.getWebPageButtons().size() > 0))
            {
                main_activity_interface.webpageSelectionSelected();
            }
        }
    }

    private void handleButtonFourClick()
    {
        if(main_activity_interface.includeManualVitalSignEntry())
        {
            // five or six button layout - so four is manual vitals
            main_activity_interface.observationSetEntrySelected();
        }
        // CHANGED FOR RELEASE 33 TO USE WEBPAGES
/*
        else if(main_activity_interface.getVideoCallsEnabled())
        {
            // four button layout, so four is video
            main_activity_interface.videoCallModeSelectionSelected();
        }
 */
        else if(main_activity_interface.getVideoCallsEnabled())
        {
            // four button layout, so four is webpages
            main_activity_interface.webpageSelectionSelected();
        }
    }

    private void handleButtonFiveClick()
    {
        // If shown, will always be annotations
        main_activity_interface.enterAnnotationsSelected();
    }

    private void handleButtonSixClick()
    {
/*
// REPLACED WITH WEBPAGES FOR RELEASE 33

        if (main_activity_interface.getVideoCallsEnabled())
        {
            // As Scheduled video calls are not working yet, go directly to video call contacts
            //main_activity_interface.videoCallModeSelectionSelected();
            main_activity_interface.videoCallContactsSelected();
        }
*/
        if (main_activity_interface.getViewWebPagesEnabled())
        {
            main_activity_interface.webpageSelectionSelected();
        }
    }


    public void setStartSessionClickable(boolean clickable)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            if(clickable)
            {
                buttonBigButtonOne.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_blue));
                buttonBigButtonOne.setClickable(true);

                if(main_activity_interface.stopUiFastUpdates())
                {
                    buttonBigButtonOne.clearAnimation();
                }
                else
                {
                    buttonBigButtonOne.startAnimation(animScale);
                }
            }
            else
            {
                buttonBigButtonOne.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_gray));
                buttonBigButtonOne.setClickable(false);

                buttonBigButtonOne.clearAnimation();
            }
        }

    }
}
