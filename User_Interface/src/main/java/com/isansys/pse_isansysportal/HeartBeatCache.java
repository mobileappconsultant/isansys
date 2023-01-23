package com.isansys.pse_isansysportal;

import com.isansys.patientgateway.HeartBeatInfo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HeartBeatCache
{
    private final List<HeartBeatInfo> heart_beat_list;

    public HeartBeatCache()
    {
        heart_beat_list = Collections.synchronizedList(new LinkedList<>());
    }


    public void add(HeartBeatInfo info)
    {
        heart_beat_list.add(info);
    }


    public void clear()
    {
        heart_beat_list.clear();
    }


    public LinkedList<HeartBeatInfo> getHeartBeatList(long start_time, long end_time)
    {
        LinkedList<HeartBeatInfo> selected_heart_beats = new LinkedList<>();

// Come up with a way of doing this from the end of the cache (as it will be in time order)
// Can then BREAK out of the FOR loop

        synchronized (heart_beat_list)
        {

            for (HeartBeatInfo heart_beat : heart_beat_list)
            {
                if ((heart_beat.getTimestampInMs() >= start_time) && (heart_beat.getTimestampInMs() < end_time))
                {
                    selected_heart_beats.add(heart_beat);
                }
            }
        }
        return selected_heart_beats;
    }


    public int size()
    {
        return heart_beat_list.size();
    }
}
