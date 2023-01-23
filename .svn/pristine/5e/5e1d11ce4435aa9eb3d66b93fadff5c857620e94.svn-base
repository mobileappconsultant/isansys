package com.isansys.patientgateway.database;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.RemoteLoggingWithEnable;

import java.util.concurrent.atomic.AtomicInteger;

public class AsyncDatabaseUpdater extends AsyncQueryHandler 
{
	private final String TAG = AsyncDatabaseUpdater.class.getSimpleName();
    private final RemoteLoggingWithEnable Log;

    // Initial value is 0;
    public static final AtomicInteger concurrent_instances_running = new AtomicInteger();
    
    public AsyncDatabaseUpdater(ContentResolver cr, RemoteLoggingWithEnable logger)
    {
        super(cr);

        Log = logger;

        concurrent_instances_running.incrementAndGet();
    }
    
    /** {@inheritDoc} */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) 
    {
//        final AsyncQueryListener listener = mListener.get();
//        if (listener != null) 
//        {
//            listener.onQueryComplete(token, cookie, cursor);
//        } 
//        else if (cursor != null) 
//        {
//            cursor.close();
//        }
        
        concurrent_instances_running.decrementAndGet();

    	Log.d(TAG, "query completed " + cookie.toString() + " : concurrent_instances_running = " + concurrent_instances_running);
    }
    
    
    @Override
    protected void onUpdateComplete (int token, Object cookie, int result)
    {   
        long stop_database_insert_time = System.nanoTime();
        long start_database_insert_time = (long) cookie;
        long total_time_for_database_Insert = (stop_database_insert_time - start_database_insert_time) / 1000000;
        
        concurrent_instances_running.decrementAndGet();

        Log.d(TAG, "updateContentValuesAtContentUri : onUpdateComplete : Total time to Insert data (ms) = " + total_time_for_database_Insert + ",     token = " + token + ".   Result = " + result + " : concurrent_instances_running = " + concurrent_instances_running);
    }
    
    
    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri)
    {
        long stop_database_insert_time = System.nanoTime();
        long start_database_insert_time = (long) cookie;
        long total_time_for_database_Insert = (stop_database_insert_time - start_database_insert_time) / 1000000;
        
        concurrent_instances_running.decrementAndGet();

        if (!PatientGatewayService.disableCommentsForSpeed())
        {
            Log.d(TAG, "asyncInsertContentValuesAtContentUri : onInsertComplete : Total time to Insert data (ms) = " + total_time_for_database_Insert + ",     token = " + token + ".    URI = " + uri.toString() + " : concurrent_instances_running = " + concurrent_instances_running);
        }
    }
}
