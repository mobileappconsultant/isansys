package com.isansys.pse_isansysportal;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.isansys.common.enums.SensorType;
import com.isansys.pse_isansysportal.enums.DayOrNightMode;

import java.util.Timer;
import java.util.TimerTask;

public class FragmentFooter extends FragmentIsansys implements OnClickListener
{
    private static final String TAG = FragmentFooter.class.getName();
    
    private Timer timer_gui_time_updater = new Timer();
    private final Handler handler = new Handler();
    private final Handler webservice_result_handler = new Handler();

    private RelativeLayout relativeLayoutFooter;

    private Button buttonBack;
    private Button buttonLock;
    private Button buttonNext;
    private CheckBox checkBoxShowOrHideSetupModeBlobs;
    private ImageView small_set_up_mode_blob_image;

    private boolean desired_button_back_visibility;
    private boolean desired_button_lock_visibility;
    private boolean desired_button_next_visibility;

    private SeekBar seekBarBrightness;

    private View viewSystemStatus;
    
    private ImageView signal_strength_image;

    private TextView textGuiTime;
    private TextView textGuiTimeSmall;
    
    private ImageView android_battery_image;
    private RelativeLayout relativeLayoutBatteryDetails;
    private TextView textViewFooterBatteryPercentage;
    private TextView textViewFooterBatteryCurrent;
    private TextView textViewFooterBatteryVoltage;
    private TextView textViewFooterBatteryTemperature;

    private TextView textViewFooterFreeDiskSpacePercentage;
    
    private ImageView server_image;
    private View viewFooterServerWebserviceResult;
    
    private ImageView realTimeServerImage;
    private View viewFooterRealTimeServerStatus;

    private Animation next_button_scale_animation = null;
    
    private Drawable drawable_wifi_none = null;
    private Drawable drawable_wifi_one = null;
    private Drawable drawable_wifi_two = null;
    private Drawable drawable_wifi_three = null;
    private Drawable drawable_wifi_four = null;

    private Drawable drawable_gsm_none = null;
    private Drawable drawable_gsm_one = null;
    private Drawable drawable_gsm_two = null;
    private Drawable drawable_gsm_three = null;
    private Drawable drawable_gsm_four = null;

    private ImageView imageDayOrNight;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.footer, container, false);

        imageDayOrNight = v.findViewById(R.id.imageDayOrNight);
        imageDayOrNight.setOnClickListener(this);

        Button buttonIncreaseScreenBrightness = v.findViewById(R.id.buttonIncreaseScreenBrightness);
        buttonIncreaseScreenBrightness.setOnClickListener(this);

        Button buttonDecreaseScreenBrightness = v.findViewById(R.id.buttonDecreaseScreenBrightness);
        buttonDecreaseScreenBrightness.setOnClickListener(this);

        relativeLayoutBatteryDetails = v.findViewById((R.id.relativeLayoutBatteryDetails));
        relativeLayoutBatteryDetails.setVisibility(View.INVISIBLE);

        return v;
    }

    
    @Override
    public void onStart()
    {
        textGuiTime = getView().findViewById(R.id.textHeaderGuiTime);
        textGuiTimeSmall = getView().findViewById(R.id.textHeaderGuiTimeSmall);

        buttonBack = getView().findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

        checkBoxShowOrHideSetupModeBlobs = getView().findViewById(R.id.checkBoxShowHideSetupModeBlobs);
        checkBoxShowOrHideSetupModeBlobs.setOnClickListener(this);
        checkBoxShowOrHideSetupModeBlobs.setChecked(true);

        small_set_up_mode_blob_image = getView().findViewById(R.id.smallSetupModeBlobSymbol);
        small_set_up_mode_blob_image.setImageResource(R.drawable.setup_mode_blob_31x25);
        small_set_up_mode_blob_image.setVisibility(View.INVISIBLE);

        buttonLock = getView().findViewById(R.id.buttonLock);
        buttonLock.setOnClickListener(this);

        buttonNext = getView().findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(this);

        viewSystemStatus = getView().findViewById(R.id.viewFooterSystemStatus);
        Activity activity = getActivity();
        if (activity != null)
        {
            viewSystemStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_yellow));
        }
        viewSystemStatus.setVisibility(View.VISIBLE);
        
        android_battery_image = getView().findViewById(R.id.imageFooterAndroidBattery);
        android_battery_image.setImageResource(R.drawable.battery_empty);
        android_battery_image.setOnTouchListener((arg0, arg1) -> {

            if (arg1.getAction() == MotionEvent.ACTION_UP)
            {
                if (relativeLayoutBatteryDetails.getVisibility() == View.INVISIBLE)
                {
                    relativeLayoutBatteryDetails.setVisibility(View.VISIBLE);
                }
                else
                {
                    relativeLayoutBatteryDetails.setVisibility(View.INVISIBLE);
                }
            }

            return true;
        });
        
        textViewFooterBatteryPercentage = getView().findViewById(R.id.textViewFooterBatteryPercentage);
        textViewFooterBatteryCurrent = getView().findViewById(R.id.textViewFooterBatteryCurrent);
        textViewFooterBatteryVoltage = getView().findViewById(R.id.textViewFooterBatteryVoltage);
        textViewFooterBatteryTemperature = getView().findViewById(R.id.textViewFooterBatteryTemperature);
        
        textViewFooterFreeDiskSpacePercentage = getView().findViewById(R.id.textViewFooterFreeDiskSpacePercentage);
        
        signal_strength_image = getView().findViewById(R.id.imageSignalStrength);
        signal_strength_image.setVisibility(View.VISIBLE);
        signal_strength_image.setOnTouchListener((arg0, arg1) -> {

            if (arg1.getAction() == MotionEvent.ACTION_UP)
            {
                if (!main_activity_interface.getGsmOnlyModeFeatureEnabled())
                {
                    boolean allow_reconnect = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;

                    main_activity_interface.showWifiStatusPopup(allow_reconnect);
                    main_activity_interface.sendCommandToGetWifiStatus();
                }
            }

            return true;
        });

        drawable_wifi_none = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_none);
        drawable_wifi_one = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_one);
        drawable_wifi_two = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_two);
        drawable_wifi_three = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_three);
        drawable_wifi_four = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_four);

        drawable_gsm_none = ContextCompat.getDrawable(getActivity(), R.drawable.gsm_0);
        drawable_gsm_one = ContextCompat.getDrawable(getActivity(), R.drawable.gsm_1);
        drawable_gsm_two = ContextCompat.getDrawable(getActivity(), R.drawable.gsm_2);
        drawable_gsm_three = ContextCompat.getDrawable(getActivity(), R.drawable.gsm_3);
        drawable_gsm_four = ContextCompat.getDrawable(getActivity(), R.drawable.gsm_4);

        LinearLayout linearLayoutFooterServerLink = getView().findViewById(R.id.linearLayoutFooterServerLink);
        linearLayoutFooterServerLink.setOnTouchListener((arg0, arg1) -> {

            if (arg1.getAction() == MotionEvent.ACTION_UP)
            {
                main_activity_interface.showSyncStatusPopup();
            }

            return true;
        });

        LinearLayout linearLayoutFooterDeveloperBits = getView().findViewById(R.id.linearLayoutFooterDeveloperBits);
        linearLayoutFooterDeveloperBits.setOnTouchListener((arg0, arg1) -> {

            if (arg1.getAction() == MotionEvent.ACTION_UP)
            {
                if(main_activity_interface.getDeveloperPopupEnabled())
                {
                    main_activity_interface.showDeveloperPopup();
                }
            }

            return true;
        });

        server_image = getView().findViewById(R.id.imageServer);
        server_image.setVisibility(View.INVISIBLE);
        viewFooterServerWebserviceResult = getView().findViewById(R.id.viewFooterServerStatus);
        viewFooterServerWebserviceResult.setVisibility(View.INVISIBLE);
        
        realTimeServerImage = getView().findViewById(R.id.imageWamp);
        realTimeServerImage.setVisibility(View.INVISIBLE);
        viewFooterRealTimeServerStatus = getView().findViewById(R.id.viewFooterWampStatus);
        viewFooterRealTimeServerStatus.setVisibility(View.INVISIBLE);

        seekBarBrightness = getView().findViewById(R.id.seekBarScreenBrightness);
        seekBarBrightness.setMin(main_activity_interface.getMinimumBrightnessLevel());
        seekBarBrightness.setMax(main_activity_interface.getMaximumBrightnessLevel());
        seekBarBrightness.setOnTouchListener((view, motionEvent) -> true);

        setScreenBrightnessSliderPosition(main_activity_interface.getCurrentScreenBrightness());

        if(getActivity() != null)
        {
            next_button_scale_animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_animation);
        }
        else
        {
            Log.e(TAG,"onStart : FragmentFooter getActivity() is NULL");
        }

        main_activity_interface.checkAndCancel_timer(timer_gui_time_updater);

        timer_gui_time_updater = new Timer();
        timer_gui_time_updater.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                // Don't update the footer if UI Activity isn't active
                if((getActivity() != null) && isAdded())
                {
                    onGuiTimeUpdaterTimerTick();
                }
            }
        }, 0, DateUtils.SECOND_IN_MILLIS);
        
        relativeLayoutFooter = getView().findViewById(R.id.relativeLayoutFooter);

        super.onStart();
    }

    @Override
    public void onPause()
    {
        main_activity_interface.checkAndCancel_timer(timer_gui_time_updater);

        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        main_activity_interface.checkAndCancel_timer(timer_gui_time_updater);

        timer_gui_time_updater = new Timer();
        timer_gui_time_updater.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                // Don't update the footer if UI Activity isn't active
                if((getActivity() != null) && isAdded())
                {
                    onGuiTimeUpdaterTimerTick();
                }
            }
        }, 0, DateUtils.SECOND_IN_MILLIS);

        setBackButtonVisible(desired_button_back_visibility);
        setLockButtonVisible(desired_button_lock_visibility);
        setNextButtonVisible(desired_button_next_visibility);

        main_activity_interface.footerFragmentLoaded();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.buttonNext)
        {
            buttonNextClick();
        }
        else if (id == R.id.buttonLock)
        {
            buttonLockClick();
        }
        else if (id == R.id.buttonBack)
        {
            buttonBackClick();
        }
        else if (id == R.id.checkBoxShowHideSetupModeBlobs)
        {
            main_activity_interface.showSetupModeBlobs(checkBoxShowOrHideSetupModeBlobs.isChecked());
        }
        else if (id == R.id.imageDayOrNight)
        {
            nightModeClicked();
        }
        else if (id == R.id.buttonIncreaseScreenBrightness)
        {
            main_activity_interface.incrementScreenBrightness();
        }
        else if (id == R.id.buttonDecreaseScreenBrightness)
        {
            main_activity_interface.decrementScreenBrightness();
        }
    }


    private void nightModeClicked()
    {
        boolean isDayMode = main_activity_interface.getNightModeStatus() == DayOrNightMode.DAY_MODE;
        main_activity_interface.enableNightMode(isDayMode);
        showNightModeEnabled(isDayMode);

        setScreenBrightnessSliderPosition(main_activity_interface.getCurrentScreenBrightness());
    }

    
    private void buttonNextClick()
    {
        Log.d(TAG, "buttonNextClick");
        main_activity_interface.nextButtonPressed();
    }


    private void buttonLockClick()
    {
        Log.d(TAG, "buttonLockClick");
        main_activity_interface.lockButtonPressed();
    }


    private void buttonBackClick()
    {
        Log.d(TAG, "buttonBackClick");
        main_activity_interface.backButtonPressed();
    }


    public void setBackButtonVisible(boolean enabled)
    {
        if (buttonBack != null)
        {
            if (enabled)
            {
                buttonBack.setVisibility(View.VISIBLE);
            }
            else
            {
                buttonBack.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void setBackButtonAsShowOrHideSetupModeBlobsVisible(boolean enabled)
    {
        showCheckBoxShowOrHideSetupModeBlobs(enabled && setupModeLogsExistInThisSession());
    }


    public boolean isShowSetupModeBlobsChecked()
    {
        if (checkBoxShowOrHideSetupModeBlobs != null)
        {
            return checkBoxShowOrHideSetupModeBlobs.isChecked();
        }

        return true; // checkBoxShowOrHideSetupModeBlobs was null for some reason, so defaulting to show the blobs
    }


    public void showCheckBoxShowOrHideSetupModeBlobs(boolean show)
    {
        if (checkBoxShowOrHideSetupModeBlobs != null && small_set_up_mode_blob_image != null)
        {
            if (show)
            {
                checkBoxShowOrHideSetupModeBlobs.setVisibility(View.VISIBLE);
                small_set_up_mode_blob_image.setVisibility(View.VISIBLE);
            }
            else
            {
                checkBoxShowOrHideSetupModeBlobs.setVisibility(View.INVISIBLE);
                small_set_up_mode_blob_image.setVisibility(View.INVISIBLE);
            }
        }
    }


    // Whether or not to show the "Show Setup Mode Blobs" control, don't even show it if there are no setup mode logs
    public boolean setupModeLogsExistInThisSession()
    {
        return main_activity_interface.getSetupModeLog(SensorType.SENSOR_TYPE__LIFETOUCH).size() > 0 || main_activity_interface.getSetupModeLog(SensorType.SENSOR_TYPE__SPO2).size() > 0;
    }


    public void setLockButtonVisible(boolean enabled)
    {
        if (buttonLock != null)
        {
            if (enabled)
            {
                buttonLock.setVisibility(View.VISIBLE);
            }
            else
            {
                buttonLock.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    
    public void setNextButtonVisible(boolean enabled)
    {
        if (buttonNext != null)
        {
            if (enabled)
            {
                if(next_button_scale_animation == null)
                {
                    if(getActivity() != null)
                    {
                        next_button_scale_animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_animation);
                    }
                    else
                    {
                        Log.e(TAG,"setNextButtonVisible : getActivity() is null");
                    }
                }
                buttonNext.setAnimation(next_button_scale_animation);

                buttonNext.setVisibility(View.VISIBLE);
            }
            else
            {
                buttonNext.setAnimation(null);
                buttonNext.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void setDesiredBackButtonVisibility(boolean enabled)
    {
        desired_button_back_visibility = enabled;
    }


    public void setDesiredLockButtonVisibility(boolean enabled)
    {
        desired_button_lock_visibility = enabled;
    }


    public void setDesiredNextButtonVisibility(boolean enabled)
    {
        desired_button_next_visibility = enabled;
    }


    public void setBackButtonText(String desired_text)
    {
        if (buttonBack != null)
        {
            // The View has been created so the buttons are not null
            buttonBack.setText(desired_text);

            buttonBack.setTextSize(main_activity_interface.getFooterButtonTextSizeForString(desired_text));
        }
    }


    public void setNextButtonText(String desired_text)
    {
        if (buttonNext != null)
        {
            // The View has been created so the buttons are not null
            buttonNext.setText(desired_text);

            buttonNext.setTextSize(main_activity_interface.getFooterButtonTextSizeForString(desired_text));
        }
    }


    public void setLockButtonText(String desired_text)
    {
        if (buttonLock != null)
        {
            // The View has been created so the buttons are not null
            buttonLock.setText(desired_text);

            buttonLock.setTextSize(main_activity_interface.getFooterButtonTextSizeForString(desired_text));
        }
    }

    
    public void setScreenBrightnessSliderPosition(int value)
    {
        seekBarBrightness.setProgress(value);
    }
    
    
    public void showSystemStatusStatus(boolean connected)
    {
        if (viewSystemStatus != null)
        {
            Activity activity = getActivity();
            if (activity != null)
            {
                if (connected)
                {
                    viewSystemStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_green));
                }
                else
                {
                    viewSystemStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_red));
                }
            }
        }
    }


    public void showWifiLevel(int level)
    {
        showSignalLevel(true, level);
    }


    public void showGsmLevel(int level)
    {
        showSignalLevel(false, level);
    }


    private void showSignalLevel(boolean show_wifi, int level)
    {
        switch (level)
        {
            case 0:
            {
                signal_strength_image.clearAnimation();

                if (show_wifi)
                {
                    signal_strength_image.setImageDrawable(drawable_wifi_none);
                }
                else
                {
                    signal_strength_image.setImageDrawable(drawable_gsm_none);
                }

                if(main_activity_interface.stopUiFastUpdates() == false)
                {
                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.signal_strength_blink_animation);
                    signal_strength_image.startAnimation(myFadeInAnimation);
                }
            }
            break;
            
            case 1:
            {
                signal_strength_image.clearAnimation();
                signal_strength_image.setVisibility(View.VISIBLE);

                if (show_wifi)
                {
                    signal_strength_image.setImageDrawable(drawable_wifi_one);
                }
                else
                {
                    signal_strength_image.setImageDrawable(drawable_gsm_one);
                }
            }
            break;
            
            case 2:
            {
                signal_strength_image.clearAnimation();
                signal_strength_image.setVisibility(View.VISIBLE);

                if (show_wifi)
                {
                    signal_strength_image.setImageDrawable(drawable_wifi_two);
                }
                else
                {
                    signal_strength_image.setImageDrawable(drawable_gsm_two);
                }
            }
            break;
            
            case 3:
            {
                signal_strength_image.clearAnimation();
                signal_strength_image.setVisibility(View.VISIBLE);

                if (show_wifi)
                {
                    signal_strength_image.setImageDrawable(drawable_wifi_three);
                }
                else
                {
                    signal_strength_image.setImageDrawable(drawable_gsm_three);
                }
            }
            break;

            case 4:
            {
                signal_strength_image.clearAnimation();
                signal_strength_image.setVisibility(View.VISIBLE);

                if (show_wifi)
                {
                    signal_strength_image.setImageDrawable(drawable_wifi_four);
                }
                else
                {
                    signal_strength_image.setImageDrawable(drawable_gsm_four);
                }
            }
            break;
        }
    }
    
    
    private void onGuiTimeUpdaterTimerTick()
    {
        Runnable runnable = this::updateClock;

        handler.post(runnable);
    }


    public void updateClock()
    {
        long time_now_in_milliseconds = main_activity_interface.getNtpTimeNowInMilliseconds();
        
        textGuiTime.setText(TimestampConversion.convertDateToHumanReadableStringHoursMinutes(time_now_in_milliseconds)); 
        textGuiTimeSmall.setText(TimestampConversion.convertDateToHumanReadableStringDayMonthHoursMinutesSeconds(time_now_in_milliseconds));
    }

    public void setAndroidBatteryCurrent(int level)
    {
        if (textViewFooterBatteryCurrent != null)
        {
            textViewFooterBatteryCurrent.setText(String.valueOf(level));

            Activity activity = getActivity();
            if (activity != null)
            {
                if (level > 0)
                {
                    textViewFooterBatteryCurrent.setTextColor(ContextCompat.getColor(activity, R.color.green));
                }
                else
                {
                    textViewFooterBatteryCurrent.setTextColor(ContextCompat.getColor(activity, R.color.red));
                }
            }
        }
    }
    

    public void setAndroidBatteryVoltage(int voltage_in_millivolts)
    {        
        if (textViewFooterBatteryVoltage != null)
        {
            String string = voltage_in_millivolts + getResources().getString(R.string.mv);
            textViewFooterBatteryVoltage.setText(string);

            Activity activity = getActivity();
            if (activity != null)
            {
                if (voltage_in_millivolts > 4000)
                {
                    textViewFooterBatteryVoltage.setTextColor(ContextCompat.getColor(activity, R.color.green));
                }
                else
                {
                    textViewFooterBatteryVoltage.setTextColor(ContextCompat.getColor(activity, R.color.red));
                }
            }
        }
    }


    public void setAndroidBatteryTemperature(float temperature)
    {
        if (textViewFooterBatteryTemperature != null)
        {
            String string = temperature + getResources().getString(R.string.degreesC);
            textViewFooterBatteryTemperature.setText(string);
        }
    }


    public void setAndroidBatteryLevel(int battery_percentage, boolean charging)
    {
        if (textViewFooterBatteryPercentage != null)
        {
            String string = battery_percentage + getResources().getString(R.string.percent_symbol);
            textViewFooterBatteryPercentage.setText(string);
        }

        if (android_battery_image != null)
        {
            if (charging)
            {
                android_battery_image.setImageResource(R.drawable.battery_charging);
            }
            else
            {
                if (battery_percentage < 5)                                                     // 0 - 4 = Empty
                {
                    android_battery_image.setImageResource(R.drawable.battery_empty);
                }
                else if (battery_percentage < 10)                                               // 5 - 9 = Almost Empty
                {
                    android_battery_image.setImageResource(R.drawable.battery_almost_empty);
                }
                else if (battery_percentage < 25)                                               // 10 - 25 = 25% 
                {
                    android_battery_image.setImageResource(R.drawable.battery_25_percent);
                }
                else if (battery_percentage < 50)                                               // 25 - 49 = 50%
                {
                    android_battery_image.setImageResource(R.drawable.battery_50_percent);
                }
                else if (battery_percentage < 75)                                               // 50 - 74 = 75%
                {
                    android_battery_image.setImageResource(R.drawable.battery_75_percent);
                }
                else if (battery_percentage < 90)                                               // 75 - 89 = Almost Full
                {
                    android_battery_image.setImageResource(R.drawable.battery_almost_full);
                }
                else if (battery_percentage <= 100)                                             // 90 - 100 = Full
                {
                    android_battery_image.setImageResource(R.drawable.battery_full);
                }
            }
        }
    }

    private boolean invalid_server_status_code = false;
    public void showServerWebserviceResult(boolean webservice_result, int received_server_code)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            // invalid_server_status_code is used to make sure that when INVALID_STATUS of web page is shown for 1000ms without interruption.
            // invalid_server_status_code is set FALSE in "showServerWebserviceResult_invalid_server_status" run override function
            if(!invalid_server_status_code)
            {
                if (webservice_result)
                {
                    if (received_server_code == 0)
                    {
                        viewFooterServerWebserviceResult.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_green));
                    }
                    else
                    {
                        viewFooterServerWebserviceResult.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_orange));
                        invalid_server_status_code = true;
                        showServerWebserviceResult_invalid_server_status();
                    }
                }
                else
                {
                    viewFooterServerWebserviceResult.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_red));
                }

                if(!invalid_server_status_code)
                {
                    viewFooterServerWebserviceResult.setVisibility(View.VISIBLE);

                    // This function will be called hopefully fast and furious. So empty the handler each time to make sure there is only one timeout
                    webservice_result_handler.removeCallbacksAndMessages(null);

                    // Hide the indicator after 2 seconds
                    webservice_result_handler.postDelayed(() -> viewFooterServerWebserviceResult.setVisibility(View.INVISIBLE), DateUtils.SECOND_IN_MILLIS);
                }
            }
            else
            {
                Log.w(TAG, "invalid server status code visibility is still on");
            }
        }
    }
    
   
    private void showServerWebserviceResult_invalid_server_status()
    {
        Log.i(TAG, "showServerWebserviceResult_invalid_server_status received from the Patient Gateway");

        viewFooterServerWebserviceResult.setVisibility(View.VISIBLE);
        
        // This function will be called hopefully fast and furious. So empty the handler each time to make sure there is only one timeout
        webservice_result_handler.removeCallbacksAndMessages(null);

        // Hide the indicator after 2 seconds
        webservice_result_handler.postDelayed(() -> {
            viewFooterServerWebserviceResult.setVisibility(View.INVISIBLE);
            invalid_server_status_code = false;
        }, 2000);
    }

    public void enableServerConnectionStatusIndicator(boolean enable)
    {
        if (enable)
        {
            //viewHeaderServerWebserviceResult.setVisibility(View.VISIBLE);
            server_image.setVisibility(View.VISIBLE);
        }
        else
        {
            viewFooterServerWebserviceResult.setVisibility(View.INVISIBLE);
            server_image.setVisibility(View.INVISIBLE);
        }
    }

    
    public void showRealTimeServerConnectionStatus(boolean connected)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            if (connected)
            {
                viewFooterRealTimeServerStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_green));
            }
            else
            {
                viewFooterRealTimeServerStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_red));
            }
        }

    }

    
    public void enableRealTimeServerConnectionStatusIndicator(boolean enable)
    {
        if (enable)
        {
            realTimeServerImage.setVisibility(View.VISIBLE);
            viewFooterRealTimeServerStatus.setVisibility(View.VISIBLE);
        }
        else
        {
            realTimeServerImage.setVisibility(View.INVISIBLE);
            viewFooterRealTimeServerStatus.setVisibility(View.INVISIBLE);
        }
    }


    public void showNightModeEnabled(boolean enabled)
    {
        if (enabled)
        {
            imageDayOrNight.setImageDrawable(ContextCompat.getDrawable(main_activity_interface.getAppContext(), R.drawable.day_or_night_white));
        }
        else
        {
            imageDayOrNight.setImageDrawable(ContextCompat.getDrawable(main_activity_interface.getAppContext(), R.drawable.day_or_night));
        }
    }


    public void runInSlowUpdateMode()
    {
        signal_strength_image.clearAnimation();

        main_activity_interface.checkAndCancel_timer(timer_gui_time_updater);

        timer_gui_time_updater = new Timer();
        timer_gui_time_updater.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                // Don't update the footer if UI Activity isn't active
                if((getActivity() != null) && isAdded())
                {
                    onGuiTimeUpdaterTimerTick();
                }
            }
        }, 0, 10 * DateUtils.SECOND_IN_MILLIS);
    }


    public void hideControlsDueToUpdateMode(boolean hide)
    {
        if (hide)
        {
            relativeLayoutFooter.setVisibility(View.INVISIBLE);
        }
        else
        {
            relativeLayoutFooter.setVisibility(View.VISIBLE);
        }
    }

    public void showFreeDiskSpace(int free_disk_space_percentage)
    {
        if (textViewFooterFreeDiskSpacePercentage != null)
        {
            String string = free_disk_space_percentage + getResources().getString(R.string.percent_symbol_free);
            textViewFooterFreeDiskSpacePercentage.setText(string);
        }
    }
}
