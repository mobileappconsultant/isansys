
package com.isansys.Updater;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends Activity
{
	
	String TAG = "MainActivity UPDATER";
	
	String UPDATER_SERVICE_INTENT_STRING = "com.isansys.updater.intent";
	
	String UPDATER_SERVICE_REPLY_INTENT_STRING = "com.isansys.updaterReply.intent";
    
    boolean bound_to_programMonitoring_service = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Updater onCreate");
        
        setContentView(R.layout.activity_main);
        
        // Start the Logger Service
        Intent intent = new Intent(UPDATER_SERVICE_INTENT_STRING);
        startService(intent);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "updater onPause");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "Updater onResume");
        
        Intent intent = new Intent(UPDATER_SERVICE_INTENT_STRING);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
        
        try
        {
            String logger_software_version_number = "v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            
            TextView textViewVersionNumber;
            textViewVersionNumber = (TextView) findViewById(R.id.textViewVersionNumber);
            textViewVersionNumber.setText(logger_software_version_number);
        }
        catch (NameNotFoundException e)
        {
            ;
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d(TAG, "Updater onStop");
        // Unbind if it is bound to the service
        if (bound_to_programMonitoring_service)
        {
            this.unbindService(myConnection);
            bound_to_programMonitoring_service = false;
        }
    }

    
    @Override
    protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Updater onDestroy");
	}
    
    
    private ServiceConnection myConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder binder)
        {
        	Log.d(TAG, "Connected to Updater Service");
        	
        	bound_to_programMonitoring_service = true;
        }

        public void onServiceDisconnected(ComponentName className)
        {
        	Log.d(TAG, "Disconnected from Updater Service");
        	
        	bound_to_programMonitoring_service = false;
        }
    };
       
}
