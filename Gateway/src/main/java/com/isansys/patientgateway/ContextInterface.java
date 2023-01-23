package com.isansys.patientgateway;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Rory on 04/04/2017.
 *
 * Allows non-activity or -service classes to access the application context for sending broadcasts etc.
 * This is intended to prevent crashes due to DeadObjectExceptions from the context.sendBroadcast() calls.
 *
 */

public interface ContextInterface
{
    Context getAppContext();

    void sendBroadcastIntent(Intent intent);
}
