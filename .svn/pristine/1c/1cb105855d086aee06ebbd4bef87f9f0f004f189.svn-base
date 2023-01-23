package com.isansys.patientgateway;

import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class RemoteLoggingWithEnable
{
    private final RemoteLogging Log;

    private final boolean enable_logging;

    public RemoteLoggingWithEnable(RemoteLogging Log, boolean enable_logs)
    {
        this.Log = Log;
        this.enable_logging = enable_logs;
    }


    public RemoteLogging getLog()
    {
        return Log;
    }


    public void v(final String tag, final String msg)
    {
        if (enable_logging)
        {
            Log.v(tag, msg);
        }
    }

    public void d(final String tag, final String msg)
    {
        if (enable_logging)
        {
            Log.d(tag, msg);
        }
    }

    public void i(final String tag, final String msg)
    {
        if (enable_logging)
        {
            Log.i(tag, msg);
        }
    }

    public void w(final String tag, final String msg)
    {
        if (enable_logging)
        {
            Log.w(tag, msg);
        }
    }

    public void e(final String tag, final String msg)
    {
        if (enable_logging)
        {
            Log.e(tag, msg);
        }
    }
}
