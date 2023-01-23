package com.isansys.pse_isansysportal;

import com.isansys.common.enums.QueryType;
import com.isansys.remotelogging.RemoteLogging;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

class AsyncDatabaseQuery extends AsyncQueryHandler
{
    private final static String TAG = AsyncDatabaseQuery.class.getSimpleName();
    private final RemoteLogging Log;
    
    public AsyncDatabaseQuery(RemoteLogging logger, ContentResolver cr)
    {
        super(cr);

        Log = logger;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) 
    {
        QueryType query_type = QueryType.values()[token];

        if(cursor != null)
        {
            Log.d(TAG, "onQueryComplete Cursor found : " + query_type);
        }
        else
        {
            Log.d(TAG, "onQueryComplete Cursor == null : " + query_type);
        }
        
        Handler data_handler = (Handler) cookie;
        
        Message message = new Message();
        
        message.arg1 = token;
        message.obj = cursor;
        
        //post back to main activity so we can keep all the data processing in one place.
        data_handler.dispatchMessage(message);
    }
}
