/*
    This whole file can be incorporated into unit tests. As it stands it isn't ever run (except for testing during development)
 */



//package com.isansys.patientgateway;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
///**
// * Created by Sushant on 01/07/2016.
// * Enable this code to insert (TABLE_SIZE*TABLE_SIZE) data in the DB.
// * Call the "flushSinWaveSetupModeQueueToDatabase" function in PatientGatewayTimerTick to estimate the execution time
// */
//public class BulkInsertTestCode
//{
//    private static final String TAG = "BulkInsertTestCode";
//
//    private final static ConcurrentLinkedQueue<PatientGatewayService.SetupModeDataPoint> queue_sin_wave_setup_mode_datapoints_for_wamp = new ConcurrentLinkedQueue<>();
//    private volatile static boolean sin_wave_setup_mode_DB_update_inProgress = false;
//
//    private static BulkInsertTestCode mBulkInsertTestCode = null;
//
//    /**
//     * Initialized one once.
//     */
//    private BulkInsertTestCode()
//    {
//        // default instantiation
//        newSinWaveGeneratorAsynTask.execute();
//    }
//
//    public static BulkInsertTestCode getBulkInsertTestCodeInstance()
//    {
//        if(mBulkInsertTestCode == null)
//        {
//            mBulkInsertTestCode = new BulkInsertTestCode();
//        }
//        return mBulkInsertTestCode;
//    }
//
//    // Setup mode data is updated every 10ms. Table size of 100 gives 1 second period of sine wave
//    private static final int TABLE_SIZE = 100;
//    private static final float TWO_PIE = (float) (2 * 3.1459);
//    private static final float PHASE_INCREMENT = (float) (TWO_PIE / TABLE_SIZE);
//    private static final int AMPLITUDE = 2048;
//    private static final int WAMP_DATA_SEND_INTERVAL = 10;                                      // Run every 10mS = 100 Hz simulated setup mode data
//
//    private static ArrayList<Short> sineWaveGenerator()
//    {
//        ArrayList<Short> wave_in = new ArrayList<>();
//        float current_phase = (float) 0.00;
//        for(int i = 0; i < TABLE_SIZE; i++)
//        {
//            wave_in.add((short) (AMPLITUDE + 1 * AMPLITUDE * (Math.sin(current_phase))));
//            current_phase = current_phase + PHASE_INCREMENT;
//        }
//
//        return wave_in;
//    }
//
//
//    private final sinWaveGeneratorAsynTask newSinWaveGeneratorAsynTask = new sinWaveGeneratorAsynTask();
//
//    private void populate_sin_wave_setup_mode_data(int total_number_of_samples)
//    {
//        long time_stamp_value = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();
//
//        for(int i=0; i<total_number_of_samples; i++)
//        {
//            ArrayList<Short> wave_data_list = sineWaveGenerator();
//
//            for(int j=0; j<total_number_of_samples; j++)
//            {
//                PatientGatewayService.SetupModeDataPoint data_points = new PatientGatewayService.SetupModeDataPoint();
//                data_points.sample = wave_data_list.get(j);
//                data_points.timestamp = time_stamp_value + (i * TABLE_SIZE * WAMP_DATA_SEND_INTERVAL) + (j * WAMP_DATA_SEND_INTERVAL);
//                queue_sin_wave_setup_mode_datapoints_for_wamp.add(data_points);
//            }
//        }
//    }
//
//    private class sinWaveGeneratorAsynTask extends AsyncTask<Void,Void,Void>
//    {
//        @Override
//        protected Void doInBackground(Void... params)
//        {
//            populate_sin_wave_setup_mode_data(TABLE_SIZE);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid)
//        {
//            Log.d(TAG, "sinWaveGeneratorAsynTask : array size = " + queue_sin_wave_setup_mode_datapoints_for_wamp.size());
//        }
//    }
//
//    public synchronized void flushSinWaveSetupModeQueueToDatabase()
//    {
//        // Get current number of data points to store
//        int total_number_of_datapoints_in_array = queue_sin_wave_setup_mode_datapoints_for_wamp.size();         // Get a snapshot of the current size. This is updating in real time
//        int i = 0;
//
//        PatientGatewayService.SetupModeDataPoint[] data_points;
//
//        try
//        {
//            // Are there any Setup Mode data points to write to the database.
//            if ((total_number_of_datapoints_in_array > 0) && (sin_wave_setup_mode_DB_update_inProgress == false))
//            {
//
//                // Create new data_points array to hold values to be written to database
//                data_points = new PatientGatewayService.SetupModeDataPoint[total_number_of_datapoints_in_array];
//
//                // Copy ALL the Setup Mode Datapoints into data_points array
//                for (i = 0; i < total_number_of_datapoints_in_array; i++)
//                {
//                    data_points[i] = queue_sin_wave_setup_mode_datapoints_for_wamp.element();                // Pop the oldest Sample Data Point from the Linked List
//                }
//
//                final int number_of_row_to_update = data_points.length;
//
//                // This will avoid multiple creation of bulk insert async task
//                sin_wave_setup_mode_DB_update_inProgress = true;
//
//                // Store Setup Mode Datapoints in database
//                LocalDatabaseStorage.storeLifetouchSetupModeSamples(PatientGatewayService.device_info__lifetouch.android_database_device_session_id,
//                        PatientGatewayService.device_info__lifetouch.human_readable_device_id,
//                        data_points,
//                        new LocalDatabaseStorage.BulkInsertCallBack()
//                        {
//                            @Override
//                            public void onInsertFinish(int update_row)
//                            {
//                                // Check correct number of samples written to database
//                                if (update_row > 0)
//                                {
//                                    Log.d(TAG, "BulkInsertTask flushSinWaveSetupModeQueueToDatabase : Expected number_of_datapoints_to_store = "
//                                            + number_of_row_to_update +  ".. updated row = " + update_row + ". Previous queue_sin_wave_setup_mode_datapoints_for_wamp size = " + queue_sin_wave_setup_mode_datapoints_for_wamp.size());
//
//                                    // "update_row" is conformed number of data written to server
//                                    // Remove data from the head of ArrayList
//                                    for(int i = 0; i<update_row; i++)
//                                    {
////                                        queue_sin_wave_setup_mode_datapoints_for_wamp.poll();
//                                    }
//
//                                    Log.d(TAG, "BulkInsertTask : Current size of queue_sin_wave_setup_mode_datapoints_for_wamp = " + queue_sin_wave_setup_mode_datapoints_for_wamp.size());
//
//                                    sin_wave_setup_mode_DB_update_inProgress = false;
//                                }
//                                else
//                                {
//                                    Log.e(TAG,"BulkInsertTask flushSinWaveSetupModeQueueToDatabase : ALERT ALERT... All data not updated");
//                                }
//                            }
//                        });
//            }
//            else
//            {
//                Log.e(TAG,"BulkInsertTask flushSinWaveSetupModeQueueToDatabase : previous async task still executing");
//            }
//        }
//        catch(Exception e)
//        {
//            // Log Exception
//            Log.e(TAG, "********** WEIRD pollFirst() BUG **********************");
//            Log.e(TAG, e.toString());
//            Log.e(TAG, "i = " + i + ". number_of_datapoints_to_store = " + total_number_of_datapoints_in_array);
//            Log.e(TAG, "Clearing the sin wave setup mode data linked list to be on the safe side");
//
//            sin_wave_setup_mode_DB_update_inProgress = false;
//            queue_sin_wave_setup_mode_datapoints_for_wamp.clear();
//        }
//    }
//
//
//}
