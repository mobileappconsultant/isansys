package com.isansys.patientgateway.algorithms;

import android.text.format.DateUtils;

import com.isansys.patientgateway.IntermediateMeasurement;

class MinuteOfSpO2 <T extends IntermediateMeasurement> extends MinuteOfData<T>
{
    public MinuteOfSpO2(long timestamp, T default_instance)
    {
        super(timestamp, default_instance);
    }


    /**
     * We expect exactly 60 measurements per minute,
     * and the last measurement in the minute should be within 1 second of the end timestamp.
     *
     * @return false (i.e. no gaps) if 60 measurements present and last data is < 1 second before end
     */
    @Override
    boolean doesMinuteContainGaps()
    {
        int size = data.size();
        if((size >= 60) && (end_timestamp - data.get(size -1).getTimestampInMs() <= DateUtils.SECOND_IN_MILLIS))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Checks if the candidate minute is consecutive with this one.
     * <p>
     * Minutes are consecutive if the timestamp of the last heart beat of this minute is consecutive with
     * the timestamp of the first beat in the candidate minute (note NOT vice versa).
     * <p>
     *     Consecutive timestamps, in this case, are if the time delta between them is less than 1.5 seconds.
     *     This is because we expect data at approximately 1 second intervals, so if the delta would round to 1 second
     *     we assume they're consecutive.
     * <p>
     * @param candidate the minute to compare
     * @return boolean
     */
    @Override
    boolean areMinutesConsecutive(MinuteOfData<T> candidate)
    {
        long end_of_this_minute_timestamp = this.data.get(data.size() - 1).getTimestampInMs();
        long start_of_next_minute_timestamp = candidate.data.get(0).getTimestampInMs();

        long delta = start_of_next_minute_timestamp - end_of_this_minute_timestamp;

        if(delta < 1500) // ToDo: define as a variable
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    @Override
    public boolean isNextBeatReceived()
    {
        return true; // This check only works for heart eats, so default to true.
    }


    @Override
    public boolean isPreviousBeatReceived()
    {
        return true; // This check only works for heart eats, so default to true.
    }
}
