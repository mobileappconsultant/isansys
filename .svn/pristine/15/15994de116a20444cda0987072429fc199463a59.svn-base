package com.isansys.patientgateway;

import android.util.Log;

public class GatewayApplication extends android.app.Application
{
    public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);

        try
        {
            String trim_memory_level;

            switch(level)
            {
                case TRIM_MEMORY_RUNNING_LOW:
                {
                    trim_memory_level = "TRIM_MEMORY_RUNNING_LOW";
                }
                break;

                case TRIM_MEMORY_RUNNING_MODERATE:
                {
                    trim_memory_level = "TRIM_MEMORY_RUNNING_MODERATE";
                }
                break;

                case TRIM_MEMORY_RUNNING_CRITICAL:
                {
                    trim_memory_level = "TRIM_MEMORY_RUNNING_CRITICAL";
                }
                break;

                case TRIM_MEMORY_UI_HIDDEN:
                {
                    trim_memory_level = "TRIM_MEMORY_UI_HIDDEN";
                }
                break;

                case TRIM_MEMORY_BACKGROUND:
                {
                    trim_memory_level = "TRIM_MEMORY_BACKGROUND";
                }
                break;

                case TRIM_MEMORY_MODERATE:
                {
                    trim_memory_level = "TRIM_MEMORY_MODERATE";
                }
                break;

                case TRIM_MEMORY_COMPLETE:
                {
                    trim_memory_level = "TRIM_MEMORY_COMPLETE";
                }
                break;

                default:
                {
                    trim_memory_level = "unknown level = " + level;
                }
            }

            Log.d("GatewayApplication", "onTrimMemory : trim memory level : " + trim_memory_level);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
