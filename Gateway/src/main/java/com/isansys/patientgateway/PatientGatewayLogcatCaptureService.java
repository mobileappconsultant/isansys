package com.isansys.patientgateway;

import static android.os.Process.myPid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.isansys.patientgateway.remotelogging.RemoteLogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class PatientGatewayLogcatCaptureService extends Service implements ContextInterface
{
    private final String TAG = PatientGatewayLogcatCaptureService.class.getName();
    private RemoteLogging Log;

    private OldLogFileCleaner old_log_file_cleaner;

    private volatile boolean threadKill = false;

    private static final Logger system_logger = LoggerFactory.getLogger(PatientGatewayLogcatCaptureService.class);

    private final IBinder mBinder = new PatientGatewayLogcatCaptureLocalBinder();

    private Thread capture_thread = null;

    private static class PatientGatewayLogcatCaptureLocalBinder extends Binder
    {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // The system calls this method when another component, such as an activity, requests that the service be started, by calling startService().
        // Once this method executes, the service is started and can run in the background indefinitely.
        // If you implement this, it is your responsibility to stop the service when its work is done, by calling stopSelf() or stopService().
        // (If you only want to provide binding, you don't need to implement this method.)

        String NOTIFICATION_CHANNEL_ID = "com.isansys.patient.gateway.logcat.capture.service";
        String DISPLAY_TEXT = "PatientGatewayLogcatCaptureService is running in background";

        String channelName = "My Background Service";

        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;

        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(DISPLAY_TEXT)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1002, notification);

        registerThreadExitReceiver();

        old_log_file_cleaner.Start();

        // This runs the thread to capture the log lines and store in the files
        run();

        return Service.START_STICKY;                    // Auto restart the Logger Capture if Android has to terminate this service
    }


    private void run()
    {
        // kill any previously running instance
        if(capture_thread != null)
        {
            requestKill();

            Log.d(TAG, "kill requested");

            try
            {
                capture_thread.join();
            }
            catch(InterruptedException e)
            {

            }
        }

        // then set threadKill false and start a new instance.
        Log.d(TAG, "starting new thread.");
        threadKill = false;

        capture_thread = new Thread(worker);
        capture_thread.start();
    }


    @Override
    public void onCreate() 
    {
        super.onCreate();

        Log = new RemoteLogging();

        old_log_file_cleaner = new OldLogFileCleaner(Log);
    }

    
    @Override
    public void onDestroy() 
    {
        super.onDestroy();

        Log.d(TAG, "onDestroy : PatientGateway LogcatService");

        requestKill();

        old_log_file_cleaner.Stop();

        try
        {
            unregisterReceiver(logcatThreadReceiver);
        }
        catch (Exception e)
        {

        }

        stopForeground(true);
    }


    private final Runnable worker = this::runLogCatCapture;


    private void runLogCatCapture() 
    {
        Process process = null;

        try
        {
            // first clear out the main buffer, so we get a clean start
//            process = Runtime.getRuntime().exec("logcat -b all -c");

            int pid = myPid();

            // now start reading in the logcat output.
            process = Runtime.getRuntime().exec("logcat --pid=" + pid);
        }
        catch (IOException e) 
        {
            Log.e(TAG, "runLogCatCapture IOException : " + e.toString());
        }
        
        BufferedReader reader;
        
        assert process != null;

        try 
        {
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String previous_line = "";
            String line;
            String log_timeStamp;

            int null_count = 0;

            while (!killRequested()) 
            {
                line = reader.readLine();
                if (line != null)
                {
                    if(!line.equals(previous_line) && !line.equals(""))
                    {
                        log_timeStamp = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(PatientGatewayService.getNtpTimeNowInMillisecondsStatic());

                        system_logger.info(log_timeStamp + "    " + line);

                        // Only send the log line if logCat Fragment is enabled
                        if(PatientGatewayService.is_logCat_fragment_enable)
                        {
                            broadcastUpdate(line);
                        }
                    }

                    previous_line = line;
                }
                else
                {
                    null_count++;
                }

                if(null_count > 10)
                {
                    reader.close();
                    process.destroy();

                    throw(new IOException("readLine returned null more than 10 times"));
                }
            }
            
            Log.i(TAG, "Prepping thread for termination");
            system_logger.info(TAG + " : Prepping thread for termination");

            reader.close();
            process.destroy();
        }
        catch (IOException e)
        {
            Log.e(TAG, "runLogCatCapture bottom IOException : " + e.toString());
            system_logger.info(TAG + " : runLogCatCapture bottom IOException : " + e.toString());
        }

        Log.d(TAG, "Exiting thread...");
        system_logger.info(TAG + " : Exiting thread...");

        if(!killRequested())
        {
            Log.d(TAG, "thread ended unexpectedly, sending restart message");
            system_logger.info(TAG + " : thread ended unexpectedly, sending restart message");

            informServiceThreadEnded();
        }
    }
    
    private synchronized void requestKill() 
    {
        threadKill = true;
    }
    
    private synchronized boolean killRequested()
    {
        return threadKill;
    }
    

    private void informServiceThreadEnded()
    {
        sendBroadcastIntent(new Intent(ACTION_LOGCAT_THREAD_EXIT));
    }


    @Override
    public IBinder onBind(Intent intent) 
    {
        return mBinder;
    }
    
    @Override
    public boolean onUnbind(Intent intent) 
    {
        return false;
    }
    
    
    private final static String ACTION_LOG_MESSAGES_GATEWAY = "com.isansys.patientgateway.ACTION_LOG_MESSAGES_GATEWAY.PatientGateway";
    private final String ACTION_LOGCAT_THREAD_EXIT = "com.isansys.patientgateway.ACTION_LOGCAT_THREAD_EXIT";
    private final static String LOG_LINE_AS_STRING = "com.isansys.patientgateway.LOG_LINE_AS_STRING.PatientGateway";


    private IntentFilter gatewayLogcatThreadIntentFilter()
    {
        final IntentFilter intentFilter = new  IntentFilter();
        intentFilter.addAction(ACTION_LOGCAT_THREAD_EXIT);
        return intentFilter;
    }


    private final BroadcastReceiver logcatThreadReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            if(action.equals(ACTION_LOGCAT_THREAD_EXIT))
            {
                run();
            }
        }
    };


    private void registerThreadExitReceiver()
    {
        Log.d(TAG, "registerThreadExitReceiver : register broadcast receiver patient gateway");
        registerReceiver(logcatThreadReceiver, gatewayLogcatThreadIntentFilter());
    }


    private void broadcastUpdate(String Log_line)
    {
        final Intent intent = new Intent(ACTION_LOG_MESSAGES_GATEWAY);
        
        intent.putExtra(LOG_LINE_AS_STRING, Log_line);
        
        sendBroadcast(intent);
    }


    public Context getAppContext()
    {
        return getApplicationContext();
    }


    public void sendBroadcastIntent(Intent intent)
    {
        try
        {
            sendBroadcast(intent);
        }
        catch(Exception ex)
        {
            android.util.Log.e("sendBroadcastIntent", "Send broadcast failed: ", ex);
        }
    }
}
