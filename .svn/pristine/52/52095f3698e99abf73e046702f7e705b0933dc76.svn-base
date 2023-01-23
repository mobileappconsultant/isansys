package com.isansys.patientgateway;

import android.os.Handler;
import android.text.format.DateUtils;

import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

public class CheckUserInterfaceRunning
{
    private final String TAG = "CheckUserInterfaceRunning";
    private final RemoteLogging Log;

    private Timer timer;
    private int user_interface_timeout;
    private final int TOTAL_TIME_BEFORE_STARTING_USER_INTERFACE = 30;

    private final Handler user_interface_restart_handler = new Handler();

    private final PatientGatewayService service;

    public CheckUserInterfaceRunning(RemoteLogging log, PatientGatewayService service)
    {
        Log = log;
        this.service = service;

        resetUserInterfaceTimeout();

        setupUserInterfaceNothingHeardTimer();
    }

    public void resetUserInterfaceTimeout()
    {
        user_interface_timeout = TOTAL_TIME_BEFORE_STARTING_USER_INTERFACE;
    }

    private void setupUserInterfaceNothingHeardTimer()
    {
        GenericStartStopTimer.cancelTimer(timer, Log);

        timer = new Timer("ui_nothing_heard_timer");
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "user_interface_timeout = " + user_interface_timeout);

                if(user_interface_timeout <= 0)
                {
                    Log.e(TAG, "ALERT ALERT User Interface is not responding. Starting User Interface again");

                    resetUserInterfaceTimeout();

                    user_interface_restart_handler.post(() -> startUserInterfaceInSeparateThread());
                }

                user_interface_timeout--;
            }
        }, 0, (int) DateUtils.SECOND_IN_MILLIS);
    }

    private void startUserInterfaceInSeparateThread()
    {
        Executors.newSingleThreadExecutor().execute(service::startUserInterface);
    }

    public void destroy()
    {
        resetUserInterfaceTimeout();

        GenericStartStopTimer.cancelTimer(timer, Log);

        user_interface_restart_handler.removeCallbacksAndMessages(null);
    }
}
