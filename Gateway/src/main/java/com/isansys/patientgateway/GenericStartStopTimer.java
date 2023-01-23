package com.isansys.patientgateway;

import com.isansys.patientgateway.remotelogging.RemoteLogging;
import java.util.Timer;

/**
 * Class to start and stop timer. This class contains static function only.
 * Any class using Timer should use this class functions.
 */
public class GenericStartStopTimer
{
    /**
     * TAG variable for log capture
     */
    private static final String TAG = "GenericStartStopTimer";

    /**
     * Function to check if timer is initialized and cancel it
     * @param mTimer : Timer , timer to cancel
     */
    public static void cancelTimer(Timer mTimer, RemoteLogging Log)
    {
        try
        {
            if(mTimer != null)
            {
                mTimer.cancel();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "startTimer : Exception e = " + e.toString());
        }
    }

}
