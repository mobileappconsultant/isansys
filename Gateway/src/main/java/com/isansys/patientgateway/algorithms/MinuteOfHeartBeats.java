package com.isansys.patientgateway.algorithms;

import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.IntermediateMeasurement;

import java.util.ArrayList;

public class MinuteOfHeartBeats <T extends IntermediateMeasurement> extends MinuteOfData<T>
{
    /**
     * Minimum time it would take for heart beat tag to get from 0 to rollover (8192)
     * <p>
     * This is defined by the maximum possible heart rate the lifetouch can detect.
     * OSEA requires a set interval between heart beats of at least 195 ms, and the tag rolls over at 8192.
     * Therefore at the maximum detectable heart rate, the time to rollover would be 8192*195 = 1,597,440 ms (or about 26 minutes)
     */
    protected static long MINIMUM_POSSIBLE_TAG_ROLLOVER_TIME_IN_MILLISECONDS;


    public MinuteOfHeartBeats(long timestamp, T default_instance)
    {
        super(timestamp, default_instance);

        MINIMUM_POSSIBLE_TAG_ROLLOVER_TIME_IN_MILLISECONDS = ((HeartBeatInfo)(default_instance)).getTagMaxSize()*195;
    }


    /**
     * Checks for gaps in the tags of the data currently added to the minute.
     * <p>
     * Compares 1 + the difference in tag number from the first point to the last point to the number of
     * data points in the minute. If they are equal, then the minute does not contain gaps.
     * <p>
     * Note that this doesn't guarantee the minute is "complete" or "filled" - for example a minute
     * could have only two consecutive heart beats. The tag difference would be one, as they are consecutive.
     * The size of the data list would be 2. 1 + 1 = 2 , so the minute contains no gaps. But there could
     * still be 58 seconds' worth of the minute with no heart beats in it.
     * <p>
     * @return boolean.
     */
    @Override
    boolean doesMinuteContainGaps()
    {
        int minute_count = data.size();

        int first_tag = ((HeartBeatInfo)(data.get(0))).getTag();
        int last_tag = ((HeartBeatInfo)(data.get(minute_count - 1))).getTag();

        int tag_delta = (last_tag - first_tag + 1);

        if(tag_delta < 0)
        {
            tag_delta += ((HeartBeatInfo)(data.get(0))).getTagMaxSize();
        }

        if(tag_delta == minute_count)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     * Gets the tag of the first data point in the minute.
     * <p>
     * @return tag number
     */
    private int getFirstTag()
    {
        return ((HeartBeatInfo)(data.get(0))).getTag();
    }

    /**
     * Gets the tag of the last data point in the minute.
     * <p>
     * @return tag number
     */
    private int getLastTag()
    {
        return ((HeartBeatInfo)(data.get(data.size() - 1))).getTag();
    }


    /**
     * Checks if the candidate minute is consecutive with this one.
     * <p>
     * Minutes are consecutive if the tag of the last heart beat of this minute is consecutive with
     * the tag of the first beat in the candidate minute (note NOT vice versa).
     * <p>
     * They are not consecutive either if the tags are not consecutive, or if the minimum rollover
     * time has passed, in which case it is possible that a rollover has occurred and it is not
     * possible to be sure they are consecutive
     * <p>
     * @param candidate the minute to compare
     * @return boolean
     */
    @Override
    boolean areMinutesConsecutive(MinuteOfData<T> candidate)
    {
        MinuteOfHeartBeats cast_minute = (MinuteOfHeartBeats)(candidate);

        int tag_delta = cast_minute.getFirstTag() - this.getLastTag();

        // Get time between the minutes
        long time_delta = cast_minute.start_timestamp - this.end_timestamp;

        // If time delta is less than the minimum possible time for the tag rollover, we have to assume no rollover has happened.
        if(time_delta < MINIMUM_POSSIBLE_TAG_ROLLOVER_TIME_IN_MILLISECONDS)
        {
            // Then if the tags themselves are consecutive, we must have two consecutive heart beats, even if the time difference is several minutes
            return (tag_delta % ((HeartBeatInfo)(data.get(0))).getTagMaxSize()) == 1;
        }
        else
        {
            // Time delta is large, so assume a tag rollover has happened, and heart beats are not consecutive.
            return false;
        }
    }


    /**
     * Checks the minute for heart beats which don't have a valid RR interval and for which one can now be calculated
     * <p/>
     * It then calculates the RR interval, and adds the updated heart beat to a list to return
     *
     * @return list of T for which a new RR interval has been calculated
     */
    public ArrayList<T> calculateNewRrIntervals() // ToDo - rename? Move?
    {
        ArrayList<T> new_rr_intervals = new ArrayList<>();

        if((last_heart_beat_added_at_index >= 0) && (last_heart_beat_added_at_index < data.size()))
        {
            T this_beat = data.get(last_heart_beat_added_at_index);

            if(last_heart_beat_added_at_index >= 1)
            {
                //check previous beat
                T previous_beat = data.get(last_heart_beat_added_at_index - 1);

                this_beat.calculateAndSetRrIntervalIfPossible(previous_beat, new_rr_intervals);
            }

            if(last_heart_beat_added_at_index < data.size() - 1)
            {
                // check next beat
                T next_beat = data.get(last_heart_beat_added_at_index + 1);

                next_beat.calculateAndSetRrIntervalIfPossible(this_beat, new_rr_intervals);
            }
        }

        return new_rr_intervals;
    }


    /**
     * Checks if the minute has an RR interval which can now be calculated between the previous minute and the first beat
     * <p/>
     * It then calculates the RR interval, and adds the updated heart beat to a list to return
     *
     * @return list of T for which a new RR interval has been calculated
     */
    @Override
    public void checkFirstRRIntervalIfRequired(ArrayList<T> list_to_report)
    {
        // check beat 0 against previous minute - it always exists, because minutes aren't created unless there is a beat to put in them.
        T first_beat = data.get(0);

        if (isPreviousBeatReceived())
        {
            first_beat.calculateAndSetRrIntervalIfPossible(previous_beat_received_before_beginning_of_minute, list_to_report);
        }
    }


    @Override
    public boolean handleTimestampsEqual(T added_point, int index)
    {
        HeartBeatInfo point = (HeartBeatInfo)added_point;

        HeartBeatInfo indexed_point = (HeartBeatInfo) data.get(index);

        if (point.getTag() == indexed_point.getTag())
        {
            // timestamp and tag equal => duplicated data, so ignore
            return false;
        }
        else
        if (point.getTag() > indexed_point.getTag())
        {
            data.add(index + 1, added_point);

            last_heart_beat_added_at_index = index + 1;

            return true;
        }
        else
        {
            data.add(index, added_point);

            last_heart_beat_added_at_index = index;

            return true;
        }
    }


    @Override
    public boolean isNextBeatReceived()
    {
        return (( ((HeartBeatInfo)next_beat_received_after_end_of_minute).getTag() != MeasurementVitalSign.INVALID_MEASUREMENT) &&
                (next_beat_received_after_end_of_minute.getAmplitude() != MeasurementVitalSign.INVALID_MEASUREMENT) &&
                (next_beat_received_after_end_of_minute.getTimestampInMs() != MeasurementVitalSign.INVALID_TIMESTAMP));
    }


    @Override
    public boolean isPreviousBeatReceived()
    {
        return (( ((HeartBeatInfo)previous_beat_received_before_beginning_of_minute).getTag() != MeasurementVitalSign.INVALID_MEASUREMENT) &&
                (previous_beat_received_before_beginning_of_minute.getAmplitude() != MeasurementVitalSign.INVALID_MEASUREMENT) &&
                (previous_beat_received_before_beginning_of_minute.getTimestampInMs() != MeasurementVitalSign.INVALID_TIMESTAMP));
    }
}
