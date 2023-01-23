package com.isansys;

import android.content.ComponentCallbacks2;
import android.util.Log;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class PortalApplication extends android.app.Application
{

    public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);

        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW)
        {
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
                        trim_memory_level = "TRIM_MEMORY_BACKGROUND";
                    }
                    break;

                    case TRIM_MEMORY_COMPLETE:
                    {
                        trim_memory_level = "TRIM_MEMORY_BACKGROUND";
                    }
                    break;

                    default:
                    {
                        trim_memory_level = "unknown level = " + level;
                    }
                }

                Log.d("PortalApplication", "onTrimMemory : trim memory level : " + trim_memory_level);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
