package com.isansys.pse_isansysportal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.core.app.NotificationCompat;

import com.isansys.remotelogging.RemoteLogging;

import static android.os.Process.myPid;


public class IsansysPortalLogcatCaptureService extends Service implements ContextInterface
{
    private final String TAG = IsansysPortalLogcatCaptureService.class.getName();
    private RemoteLogging Log;

    private OldLogFileCleaner old_log_file_cleaner;

    private static Handler mHandler;

    private volatile boolean threadKill = false;
    //public static final int MSG_READ_FAIL = 1;
    //public static final int MSG_LOG_FAIL = 2;
    public static final int MSG_NEW_LINE = 3;
    //public static final int MSG_LOG_SAVE = 5;

    private final Logger system_logger = LoggerFactory.getLogger(IsansysPortalLogcatCaptureService.class);

    private final IBinder mBinder = new IsansysPortalLogcatCaptureServiceLocalBinder();

    private Thread capture_thread = null;

    private static class IsansysPortalLogcatCaptureServiceLocalBinder extends Binder
    {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // The system calls this method when another component, such as an activity, requests that the service be started, by calling startService().
        // Once this method executes, the service is started and can run in the background indefinitely.
        // If you implement this, it is your responsibility to stop the service when its work is done, by calling stopSelf() or stopService().
        // (If you only want to provide binding, you don't need to implement this method.)

        String NOTIFICATION_CHANNEL_ID = "com.isansys.ui.logcat.capture.service";
        String DISPLAY_TEXT = "IsansysPortalLogcatCaptureService is running in background";

        String channelName = "My Background Service";

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;

        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(DISPLAY_TEXT)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(3001, notification);

        registerThreadExitReceiver();
        registerLogLineReceiver();

        old_log_file_cleaner.Start();

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            // This runs the thread to capture the log lines and store in the files
            run();
        }
        else
        {
            // Do not have permission to write to external storage. UI will be requesting this and will restart the app once its got permission
            Log.e(TAG, "No WRITE_EXTERNAL_STORAGE permission yet");
        }

        return Service.START_STICKY;                    // Auto restart the Portal Logger if Android has to terminate this service
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
                Log.e(TAG, "capture_thread.join exception : " + e.getMessage());
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

        Log.d(TAG, "onDestroy : IsansysPortalLogcatCaptureService");

        requestKill();

        old_log_file_cleaner.Stop();

        try
        {
            unregisterReceiver(logBroadcastGateway);
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
            Log.e(TAG, "runLogCatCapture IOException : " + e);
        }
        
        BufferedReader reader;
        
        assert process != null;

        try 
        {
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String previous_line = "";
            String line;
            String log_timeStamp;
            String line_with_time_Stamp;
            String final_log_line;

            int null_count = 0;



            log_timeStamp = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(MainActivity.ntp_time.currentTimeMillis());
            line_with_time_Stamp = log_timeStamp.concat("    " + "IsansysPortalLogcatCaptureService BEGIN LOGGING");

            system_logger.info(line_with_time_Stamp);



            while (!killRequested()) 
            {
                line = reader.readLine();

                if (line != null)
                {
                    if(!line.equals(previous_line) && !line.equals(""))
                    {
                        log_timeStamp = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(MainActivity.ntp_time.currentTimeMillis());

                        system_logger.info(log_timeStamp + "    " + line);


                        if(MainActivity.logCatPageEnabled)
                        {
                            // Send to the handler in MainActivity, which puts it on the AdminMode logCat page
                            logLineToGUI(line_with_time_Stamp);
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
            Log.e(TAG, "runLogCatCapture bottom IOException : " + e);
            system_logger.info(TAG + " : runLogCatCapture bottom IOException : " + e);
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

    
    private void logLineToGUI(String line)
    {
        Message.obtain(mHandler, MSG_NEW_LINE, line).sendToTarget();
    }
    
    public static void setHandler(Handler handler) 
    {
        mHandler = handler;
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
    private final String ACTION_LOGCAT_THREAD_EXIT = "com.isansys.pse_isansysportal.ACTION_LOGCAT_THREAD_EXIT";
    private final static String LOG_LINE_AS_STRING = "com.isansys.patientgateway.LOG_LINE_AS_STRING.PatientGateway";
    private static IntentFilter gatewayLogLineIntentFilter()
    {
        final IntentFilter intentFilter = new  IntentFilter();
        intentFilter.addAction(ACTION_LOG_MESSAGES_GATEWAY);
        return intentFilter;
    }


    private IntentFilter gatewayLogcatThreadIntentFilter()
    {
        final IntentFilter intentFilter = new  IntentFilter();
        intentFilter.addAction(ACTION_LOGCAT_THREAD_EXIT);
        return intentFilter;
    }


    private final BroadcastReceiver logBroadcastGateway = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            
            if(action.equals(ACTION_LOG_MESSAGES_GATEWAY))
            {
                String  line = intent.getStringExtra(LOG_LINE_AS_STRING);
                if (line != null)
                {
                    String log_timeStamp = TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSecondsMilliseconds(MainActivity.ntp_time.currentTimeMillis());
                    String line_with_time_Stamp = log_timeStamp.concat("    "+line);
                    // Put in this format "11:20:45:060    OnReceive() : 2015-06-26 11:20:45:056 : D : ServerLink :"
                    logLineToGUI(line_with_time_Stamp);
                }
            }
        }
    };


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

    
    private void registerLogLineReceiver()
    {
        Log.d(TAG, "registerLogLineReceiver : register broadcast receiver patient gateway");
        registerReceiver(logBroadcastGateway, gatewayLogLineIntentFilter());
    }


    private void registerThreadExitReceiver()
    {
        Log.d(TAG, "registerThreadExitReceiver : register broadcast receiver patient gateway");
        registerReceiver(logcatThreadReceiver, gatewayLogcatThreadIntentFilter());
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
