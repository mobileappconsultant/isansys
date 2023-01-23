package com.isansys.pse_isansysportal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.text.format.DateUtils;

import com.isansys.patientgateway.HeartBeatInfo;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class HeartBeatCacheTest
{
    HeartBeatCache test_cache;


    @Before
    public void setUp()
    {
        test_cache = new HeartBeatCache();
    }


    @Test
    public void HeartBeatCache_getMeasurementWithEmptyCache()
    {
        long time_now = System.currentTimeMillis();

        List<HeartBeatInfo> empty_list = test_cache.getHeartBeatList(0, time_now);

        assertEquals(0, empty_list.size());
    }


    @Test
    public void HeartBeatCache_getSingleMeasurement()
    {
        long time_now = System.currentTimeMillis();

        int tag = 1;
        int amplitude = 2;
        long timestamp = time_now - DateUtils.MINUTE_IN_MILLIS;
        HeartBeatInfo.ActivityLevel activity_level = HeartBeatInfo.ActivityLevel.LOW;
        int rr_interval = 1000;


        test_cache.add(new HeartBeatInfo(tag, amplitude, timestamp, activity_level, rr_interval, time_now));

        List<HeartBeatInfo> heart_beat_list = test_cache.getHeartBeatList(0, time_now);

        assertEquals(1, heart_beat_list.size());

        HeartBeatInfo returned_info = heart_beat_list.get(0);

        assertEquals(tag, returned_info.getTag());
        assertEquals(amplitude, returned_info.getAmplitude());
        assertEquals(timestamp, returned_info.getTimestampInMs());
        assertEquals(activity_level, returned_info.getActivity());
        assertEquals(rr_interval, returned_info.getRrInterval());
    }


    @Test
    public void HeartBeatCache_checkThreadSafety()
    {
        final int number_of_heart_beats = 100000;
        final long time_now = System.currentTimeMillis();

        Runnable add_heartbeats_runnable = () -> {
            int amplitude = 200;
            HeartBeatInfo.ActivityLevel activity_level = HeartBeatInfo.ActivityLevel.LOW;
            int rr_interval = 1000;

            for(int i = number_of_heart_beats; i > 0; i--)
            {
                test_cache.add(new HeartBeatInfo(i, amplitude, time_now - i*DateUtils.SECOND_IN_MILLIS, activity_level, rr_interval, time_now));
            }
        };


        Thread add_heartbeats_thread = new Thread(add_heartbeats_runnable);

        add_heartbeats_thread.start();

        while(test_cache.size() < number_of_heart_beats)
        {
            List<HeartBeatInfo> added_so_far = test_cache.getHeartBeatList(0, time_now);

            System.out.println("Added so far: " + added_so_far.size());
        }

        assertEquals(number_of_heart_beats, test_cache.size());
    }


    @Test
    public void HeartBeatCache_benchmarkGetHeartbeatList()
    {
        final int number_of_heart_beats = 500000; // just over 5 days of data at 60 bpm

        final long time_now = System.currentTimeMillis();

        int amplitude = 200;
        HeartBeatInfo.ActivityLevel activity_level = HeartBeatInfo.ActivityLevel.LOW;
        int rr_interval = 1000;

        // add all 500,000 heartbeats to the list...
        for(int i = number_of_heart_beats; i > 0; i--)
        {
            test_cache.add(new HeartBeatInfo(i, amplitude, time_now - i*DateUtils.SECOND_IN_MILLIS, activity_level, rr_interval, time_now));
        }

        long end_time;
        long start_time = System.currentTimeMillis();

        List<HeartBeatInfo> latest_minute = test_cache.getHeartBeatList(time_now - DateUtils.MINUTE_IN_MILLIS, time_now);

        end_time = System.currentTimeMillis();

        long time_taken = end_time - start_time;

        // Remember, this is running on the test PC, not on the tablet. Nevertheless, it's in the 10s of ms so shouldn't be a problem...
        System.out.println("Getting list took " + time_taken + " ms");

        assertEquals(60, latest_minute.size());

        assertTrue(time_taken < 100);
    }
}
