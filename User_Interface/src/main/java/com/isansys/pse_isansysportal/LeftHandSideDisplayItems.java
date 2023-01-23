package com.isansys.pse_isansysportal;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import roo.clockanimation.ClockDrawable;

/** Contains the various elements on the Left Hand Side of the patient vitals display, so they are
 * all in one place.
 * <p/>
 * Battery indicator, device info, measurements pending etc.
 * <p/>
 * Does not contain the checkboxes for setup mode etc.
 */
public class LeftHandSideDisplayItems
{
    LinearLayout linearLayoutDeviceInfo = null;
    TextView textHumanReadableDeviceId = null;
    ImageView imageMeasurement = null;
    TextView textNumberOfMeasurementsPending = null;
    ClockDrawable clockDrawable = null;
    ImageView imageBatteryIcon = null;
    TextView textBatteryPercentage = null;
    ImageView imageOutOfRange = null;
    LinearLayout linearLayoutBatteryAndControls = null;

    boolean LowBatteryBlinkAction_run = false;
    final Handler low_battery_handler = new Handler();


    public void setBackgroundColour(int colour)
    {
        if (linearLayoutDeviceInfo != null)
        {
            linearLayoutDeviceInfo.setBackgroundColor(colour);
        }
    }


    public void setDeviceBatteryLevel(int last_battery_reading_in_millivolts, int last_battery_reading_percentage, int low_battery_threshold, boolean show_numbers_on_battery_indicator)
    {
        if(last_battery_reading_in_millivolts < 0)
        {
            // Invalid battery level
            if(textBatteryPercentage != null)
            {
                textBatteryPercentage.setVisibility(View.INVISIBLE);
            }

            setBatteryIconVisibility(View.INVISIBLE);
        }
        else
        {
            // Battery text
            if((textBatteryPercentage != null))
            {
                if(show_numbers_on_battery_indicator)
                {
                    textBatteryPercentage.setVisibility(View.VISIBLE);

                    updateBatteryMeasurementText(last_battery_reading_percentage, last_battery_reading_in_millivolts);
                }
                else
                {
                    textBatteryPercentage.setVisibility(View.GONE);
                }
            }

            // battery icon
            if(last_battery_reading_percentage < low_battery_threshold)
            {
                deviceLowBatteryBlinkActionStart();
            }
            else
            {
                deviceLowBatteryBlinkActionStop();
            }

            updateBatteryImageFromBatteryPercentage(last_battery_reading_percentage);
        }
    }


    /** Overloaded method for devices with no battery level text
     *
     * @param last_battery_reading_percentage
     * @param low_battery_threshold
     */
    public void setDeviceBatteryLevel(int last_battery_reading_percentage, int low_battery_threshold)
    {
        if(last_battery_reading_percentage < 0)
        {
            // Invalid battery level
            setBatteryIconVisibility(View.INVISIBLE);
        }
        else
        {
            // battery icon
            if(last_battery_reading_percentage < low_battery_threshold)
            {
                deviceLowBatteryBlinkActionStart();
            }
            else
            {
                deviceLowBatteryBlinkActionStop();
            }

            updateBatteryImageFromBatteryPercentage(last_battery_reading_percentage);
        }
    }


    public void setBatteryIconVisibility(int visibility)
    {
        if(imageBatteryIcon != null)
        {
            imageBatteryIcon.setVisibility(visibility);
        }
    }


    private void updateBatteryImageFromBatteryPercentage(int battery_percentage)
    {
        if (battery_percentage < 5)                                                     // 0 - 4 = Empty
        {
            imageBatteryIcon.setImageResource(R.drawable.battery_empty);
        }
        else if (battery_percentage < 10)                                               // 5 - 9 = Almost Empty
        {
            imageBatteryIcon.setImageResource(R.drawable.battery_almost_empty);
        }
        else if (battery_percentage < 25)                                               // 10 - 25 = 25%
        {
            imageBatteryIcon.setImageResource(R.drawable.battery_25_percent);
        }
        else if (battery_percentage < 50)                                               // 25 - 49 = 50%
        {
            imageBatteryIcon.setImageResource(R.drawable.battery_50_percent);
        }
        else if (battery_percentage < 75)                                               // 50 - 74 = 75%
        {
            imageBatteryIcon.setImageResource(R.drawable.battery_75_percent);
        }
        else if (battery_percentage < 90)                                               // 75 - 89 = Almost Full
        {
            imageBatteryIcon.setImageResource(R.drawable.battery_almost_full);
        }
        else if (battery_percentage <= 100)                                             // 90 - 100 = Full
        {
            imageBatteryIcon.setImageResource(R.drawable.battery_full);
        }
    }


    private void updateBatteryMeasurementText(int battery_percentage, int battery_voltage_in_millivolts)
    {
        String string = battery_percentage + "% " + battery_voltage_in_millivolts + "mV";
        textBatteryPercentage.setText(string);
    }


    private void deviceLowBatteryBlinkActionStop()
    {
        LowBatteryBlinkAction_run = false;
        setBatteryIconVisibility(View.VISIBLE);
    }


    private void deviceLowBatteryBlinkActionStart()
    {
        if (!LowBatteryBlinkAction_run)
        {
            LowBatteryBlinkAction_run = true;
            deviceLowBatteryBlinkAction();
        }
    }


    private void deviceLowBatteryBlinkAction()
    {
        if (LowBatteryBlinkAction_run)
        {
            if (imageBatteryIcon != null)
            {
                if (imageBatteryIcon.getVisibility() == View.VISIBLE)
                {
                    setBatteryIconVisibility(View.INVISIBLE);
                }
                else
                {
                    setBatteryIconVisibility(View.VISIBLE);
                }
            }

            Runnable runnable = this::deviceLowBatteryBlinkAction;

            low_battery_handler.postDelayed(runnable, 500);
        }
    }


    public void hideAllBatteryElements()
    {
        deviceLowBatteryBlinkActionStop();

        if (textBatteryPercentage != null)
        {
            textBatteryPercentage.setVisibility(View.INVISIBLE);
        }

        setBatteryIconVisibility(View.INVISIBLE);
    }


    public void showAttemptingToReconnected()
    {
        if(linearLayoutBatteryAndControls != null)
        {
            linearLayoutBatteryAndControls.setVisibility(View.GONE);
        }

        if(imageOutOfRange != null)
        {
            imageOutOfRange.setVisibility(View.VISIBLE);
        }
    }


    public void hideAttemptingToReconnected()
    {
        if(linearLayoutBatteryAndControls != null)
        {
            linearLayoutBatteryAndControls.setVisibility(View.VISIBLE);
        }

        if(imageOutOfRange != null)
        {
            imageOutOfRange.setVisibility(View.GONE);
        }
    }
}