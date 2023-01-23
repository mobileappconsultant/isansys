package com.isansys.patientgateway;

import android.os.Environment;
import android.text.format.DateUtils;

import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

class OldLogFileUploader
{
    private final String TAG = OldLogFileUploader.class.getSimpleName();
    private final RemoteLogging Log;

    private final PatientGatewayInterface patient_gateway_interface;

    private Timer timer;

    private enum ApplicationToUpload
    {
        PATIENT_GATEWAY,
        USER_INTERFACE,
    }

    public OldLogFileUploader(RemoteLogging logger, PatientGatewayInterface patient_gateway_interface)
    {
        this.Log = logger;
        this.patient_gateway_interface = patient_gateway_interface;
    }


    public void start()
    {
        Log.d(TAG, "Start");

        int OLD_DATA_UPLOADING_FIRST_TIME_TWO_MINUTES = (int) (2 * DateUtils.MINUTE_IN_MILLIS);
        int OLD_DATA_UPLOADING_TIME_FOUR_HOURS = (int) (4 * DateUtils.HOUR_IN_MILLIS);

        timer = new Timer("OldLogFileUploaderTimer");
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                if (patient_gateway_interface.serverLinkSetupConnectedAndSyncingEnabled())
                {
                    if (patient_gateway_interface.isBedIdSet())
                    {
                        uploadOldLoggingFiles();
                    }
                    else
                    {
                        Log.e(TAG, "Timer fired but NO WARD AND BED SETUP");
                    }
                }
                else
                {
                    Log.e(TAG, "Timer fired but NO NETWORK CONNECTION");
                }
            }
        }, OLD_DATA_UPLOADING_FIRST_TIME_TWO_MINUTES, OLD_DATA_UPLOADING_TIME_FOUR_HOURS);
    }


    public void stop()
    {
        Log.d(TAG, "Stop");

        GenericStartStopTimer.cancelTimer(timer, Log);
    }


    private boolean uploadOldLoggingFilesSub(ApplicationToUpload application_to_upload)
    {
        String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();

        String full_log_directory_path;

        switch (application_to_upload)
        {
            case PATIENT_GATEWAY:
            default:
                full_log_directory_path = SD_CARD_PATH + "/IsansysLogging/PatientGateway/";
                break;

            case USER_INTERFACE:
                full_log_directory_path = SD_CARD_PATH + "/IsansysLogging/PSE_IsansysPortal/";
                break;
        }

        try
        {
            File[] files = new File(full_log_directory_path).listFiles(file -> file.getPath().endsWith(".zip"));

            if (files.length == 0)
            {
                Log.d(TAG, "No files pending to be uploaded : " + application_to_upload);
                return false;
            }
            else
            {
                Log.d(TAG, application_to_upload + " files pending  = " + files.length);
            }

            // Sort the files in alphabetical and ascending order
            Arrays.sort(files);

            for (File file : files)
            {
                Log.d(TAG, "Uploading " + file.getName() + " to Server");
                patient_gateway_interface.uploadLogFileToServer(file);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "uploadOldLoggingFiles Exception : " + e);
        }

        return true;
    }


    private void uploadOldLoggingFiles()
    {
        if (uploadOldLoggingFilesSub(ApplicationToUpload.PATIENT_GATEWAY))
        {
            // There were Patient Gateway log files which have now been uploaded
            return;
        }

        if (uploadOldLoggingFilesSub(ApplicationToUpload.USER_INTERFACE))
        {
            // There were UI log files which have now been uploaded
            return;
        }
    }
}
