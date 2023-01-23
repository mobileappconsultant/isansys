package com.isansys.Updater;

import java.io.File;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;


public class ProgramsUpdater extends Service
{
	
	/*
	static String updater_service__patient_gateway_update_file_name = null;
	static String updater_service__user_interface_update_file_name = null;
	*/
	static String updater_service__patient_gateway_update_file_name = "PatientGateway.apk";
	static String updater_service__user_interface_update_file_name = "PSE_IsansysPortal.apk";
	
	String TAG = "UPDATER";

	private final static String ANDROID_PACKAGE = "application/vnd.android.package-archive";

	final Messenger updater_incoming_messenger = new Messenger(new IncomingHandler());
	
	
	String UPDATER_SERVICE_INTENT_STRING = "com.isansys.updater.intent";
	
	String UPDATER_SERVICE_REPLY_INTENT_STRING = "com.isansys.updaterReply.intent";
	
	String UPDATER_SERVICE__PROG_NAME = "programe_name";
	
	String UPDATER_SERVICE__STATUS = "status";
	String UPDATER_SERVICE__STATUS_OK = "ok";
	String UPDATER_SERVICE__STATUS_SHUTDOWN = "shutdown";
	String UPDATER_SERVICE__STATUS_UPDATE = "update";
	String UPDATER_SERVICE__STATUS_ERROR = "error";
	
	String UPDATER_SERVICE__INFO = "info";
	String UPDATER_SERVICE__UPDATE_FILE_NAME = "file_name";
	String UPDATER_SERVICE__UPDATE_FILE_NAME_GATEWAY = "gateway_file_name";
	String UPDATER_SERVICE__UPDATE_FILE_NAME_UI = "ui_file_name";
	 
	String UPDATER_SERVICE__PROG_NAME_PATIENT_GATEWAY = "PatientGateway";
	String UPDATER_SERVICE__PROG_NAME_PATIENT_UI = "UI";
    

	int callsCounter = 0;
	
    class IncomingHandler extends Handler 
    { 
        @Override
        public void handleMessage(Message msg) 
        {
            super.handleMessage(msg);
        }
    }
    
        
    @Override
    public void onCreate() 
    {
        // The system calls this method when the service is first created, to perform one-time setup procedures (before it calls either onStartCommand() or onBind()). 
        // If the service is already running, this method is not called.
        
        super.onCreate();
        
        registerReceiver(broadcastReceiverIncomingProgramsInfo, new IntentFilter(UPDATER_SERVICE_INTENT_STRING));
        
       // replyToProgram(UPDATER_SERVICE__PROG_NAME_PATIENT_GATEWAY,"hello");
        
       // Log.d(TAG,"Sent Hello for test");
    }
    
     
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
        // The system calls this method when another component, such as an activity, requests that the service be started, by calling startService(). 
        // Once this method executes, the service is started and can run in the background indefinitely. 
        // If you implement this, it is your responsibility to stop the service when its work is done, by calling stopSelf() or stopService(). 
        // (If you only want to provide binding, you don't need to implement this method.)
        
        return Service.START_STICKY;                    // Auto restart the Gateway if Android has to terminate this service
    }

    
    @Override
    public IBinder onBind(Intent arg0) 
    {
    	return this.updater_incoming_messenger.getBinder();
    }
    
    @Override
    public void onDestroy() 
    {
        // The system calls this method when the service is no longer used and is being destroyed. 
        // Your service should implement this to clean up any resources such as threads, registered listeners, receivers, etc. 
        // This is the last call the service receives.

        super.onDestroy();

        // Unregister broadcast listeners
        unregisterReceiver(broadcastReceiverIncomingProgramsInfo);
    }

    
    private final BroadcastReceiver broadcastReceiverIncomingProgramsInfo = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
        	
        	String prog_name = intent.getStringExtra(UPDATER_SERVICE__PROG_NAME);
        	String prog_status = intent.getStringExtra(UPDATER_SERVICE__STATUS);
        	
        	//String update_file_name = intent.getStringExtra(UPDATER_SERVICE__UPDATE_FILE_NAME);
        	/*
        	 if status is ready to update and file exists, then ok, request shutdown and update
        	 */
        	//replyToProgram(prog_name);
        	//
        	
        	
        	if(prog_status.equals(UPDATER_SERVICE__STATUS_UPDATE) == true )
        	{
        		
        		if(prog_name.equals(UPDATER_SERVICE__PROG_NAME_PATIENT_GATEWAY) == true )
        		{
        			callsCounter |= 0x01;
        			/*String temp1 = intent.getStringExtra(UPDATER_SERVICE__UPDATE_FILE_NAME);
        			if( temp1.equals("")==false )
        			{
        				updater_service__patient_gateway_update_file_name = temp1;
        			}*/
        			//installUpdate(prog_fileName);
        		}
        		else
        		if(prog_name.equals(UPDATER_SERVICE__PROG_NAME_PATIENT_UI) == true )
        		{
        			callsCounter |= 0x02;
        			/*String temp2 =intent.getStringExtra(UPDATER_SERVICE__UPDATE_FILE_NAME);
        			if( temp2.equals("")==false )
        			{
        				updater_service__user_interface_update_file_name = temp2; 
        			}*/
        			//installUpdate(prog_fileName);
        		}
        		
        		installUpdates();
        		
        	}
        	
        }
    };
    

    	
    public void installUpdates()
    {    
    	
    	
    	//if(updater_service__patient_gateway_update_file_name != null && updater_service__user_interface_update_file_name!=null)
    	{
    	
	    	Context context = getApplicationContext();
	    	
	        //File UIFileToCheck = new File(context.getFilesDir().getAbsolutePath() , updater_service__user_interface_update_file_name);
	        //File PGFileToCheck = new File(context.getFilesDir().getAbsolutePath() , updater_service__patient_gateway_update_file_name);
	    	File UIFileToCheck = new File(Environment.getExternalStorageDirectory() , updater_service__user_interface_update_file_name);
	        File PGFileToCheck = new File(Environment.getExternalStorageDirectory() , updater_service__patient_gateway_update_file_name);
	    	
	        if ( UIFileToCheck.exists() &&  PGFileToCheck.exists() && callsCounter == 3 ) 
	    	{
	        	Intent intent = new Intent(UPDATER_SERVICE_REPLY_INTENT_STRING);
	        	intent.putExtra(UPDATER_SERVICE__PROG_NAME,UPDATER_SERVICE__PROG_NAME_PATIENT_GATEWAY);    	
	        	intent.putExtra(UPDATER_SERVICE__STATUS,UPDATER_SERVICE__STATUS_SHUTDOWN);
	            sendBroadcast(intent);
	            
	            intent = new Intent(UPDATER_SERVICE_REPLY_INTENT_STRING);
	        	intent.putExtra(UPDATER_SERVICE__PROG_NAME,UPDATER_SERVICE__PROG_NAME_PATIENT_UI);        	
	        	intent.putExtra(UPDATER_SERVICE__STATUS,UPDATER_SERVICE__STATUS_SHUTDOWN);
	            sendBroadcast(intent);
	            
                
	             
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                //intent2.setDataAndType(Uri.parse("file://" + context.getFilesDir().getAbsolutePath() + "/" + updater_service__patient_gateway_update_file_name), ANDROID_PACKAGE);
                intent2.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/" + updater_service__patient_gateway_update_file_name), ANDROID_PACKAGE);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
               // updater_service__patient_gateway_update_file_name = null;
                
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                //intent1.setDataAndType(Uri.parse("file://" + context.getFilesDir().getAbsolutePath() + "/" + updater_service__user_interface_update_file_name), ANDROID_PACKAGE);
            	intent1.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/" + updater_service__user_interface_update_file_name), ANDROID_PACKAGE);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
               // updater_service__user_interface_update_file_name = null;
                
                //UIFileToCheck.delete();
                //PGFileToCheck.delete();
                callsCounter =0;
	    	}

    	}
    	
        
    }
    
    
    
    
    
    
    
    
    /*

check if file exists

File file = new File(sdcardpath+ "/" + filename);
if (file.exists())
 {

}


or 

String FILENAME="mytextfile.txt";
File fileToCheck = new File(getExternalCacheDirectory(), FILENAME);
if (fileToCheck.exists()) {
FileOutputStream fos = openFileOutput(&quot;sdcard/mytextfile.txt&quot;, MODE_WORLD_WRITEABLE);
}




writing to SD
       File storageDirectory = new File(Environment.getExternalStorageDirectory(), "location.txt");


		Make sure you have this permission in your manifest:
		
		<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

     * */
    
    
    
    

}
