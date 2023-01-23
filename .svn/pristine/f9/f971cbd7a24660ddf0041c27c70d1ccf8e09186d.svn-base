package com.isansys.patientgateway.algorithms;

import android.text.format.DateUtils;

import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.patientgateway.IntermediateMeasurement;

import java.util.ArrayList;
import java.util.List;

import static com.isansys.common.ErrorCodes.ERROR_CODES;
import static com.isansys.common.ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF;
import static com.isansys.common.ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_ON;
import static com.isansys.common.ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED;

/**
 * @author      Rory Morrison <rory.morrison @ isansys.com>
 * <p>
 * MinuteOfData contains heart beat data from the Lifetouch, as DataPoints.
 * <p>
 * It covers a period of 1 minute, from start_timestamp (inclusive) up to but not including end_timestamp.
 *
 */
class MinuteOfData<T extends IntermediateMeasurement>
{
    protected final long start_timestamp;
    final long end_timestamp;
    T next_beat_received_after_end_of_minute;
    T previous_beat_received_before_beginning_of_minute;
    int last_heart_beat_added_at_index = -1;

    List<T> data;


    /**
     * Constructor for the minute.
     * <p>
     * Called with the timestamp of a DataPoint that is to be added.
     * The timestamp is rounded down to a multiple of one minute to give the start timestamp.
     * <p>
     * Then calling the following:
     * <p>
     * {@code minute = new MinuteOfData(datapoint.timestamp);
     * minute.addDataPoint(datapoint);}
     * <p>
     * will ensure that the minute is created and the datapoint is correctly added.
     * <p>
     * @param timestamp a timestamp within the minute
     */
    public MinuteOfData(long timestamp, T default_instance)
    {
        start_timestamp = DateUtils.MINUTE_IN_MILLIS * (timestamp / DateUtils.MINUTE_IN_MILLIS);    // round down to 1 minute intervals
        end_timestamp = start_timestamp + DateUtils.MINUTE_IN_MILLIS;

        next_beat_received_after_end_of_minute = default_instance;
        previous_beat_received_before_beginning_of_minute = default_instance;

        data = new ArrayList<>();
    }


    public T getFirstDataPoint()
    {
        return data.get(0);
    }


    public T getLastDataPoint()
    {
        return data.get(data.size() - 1);
    }


    /**
     * Checks whether a given timestamp is within the start and end times of this minute.
     * <p>
     * @param timestamp timestamp to check
     * @return + 1 if after, - 1 if before, 0 if within.
     */
    int isTimestampOutsideMinute(long timestamp)
    {
        if(timestamp < start_timestamp) // Convention is that if timestamp == start_timestamp it is IN this minute
        {
            // Timestamp is from before this minute
            return -1;
        }
        else if(timestamp < end_timestamp) // Following above convention, if timestamp == end_timestamp it is in the next minute, since this end_timestamp == the next start_timestamp
        {
            // Timestamp is from this minute
            return 0;
        }
        else
        {
            // Timestamp is from after this minute
            return 1;
        }
    }

    /**
     * Stub method - returns true so if a Minute doesn't override the method we will never process the data.
     */
    boolean doesMinuteContainGaps()
    {
        return true;
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
    boolean areMinutesConsecutive(MinuteOfData<T> candidate)
    {
        return false;
    }

    /**
     * Adds a new data point to the minute.
     * <p>
     * If the minute is empty, data point is automatically added
     * <p>
     * If size is one, data point is compared by timestamp and added before or after the existing point.
     * If timestamps match, then it is compared by tag. If both match it is ignored as a duplicate.
     * <p>
     * If size is bigger than one, first checks if new data point is before or after the current list.
     * Then checks if it is somewhere in the middle of the list. In each case if the tag and timestamp
     * both match with a DataPoint already in the list, the new one is ignored.
     * <p>
     * @param point the DataPoint to add
     * @return boolean - whether or not the data point was added.
     */
    boolean addDataPoint(T point)
    {
        int size = data.size();

        switch (size)
        {
            case 0:
            {
                data.add(point);

                last_heart_beat_added_at_index = 0;

                return true;
            }

            case 1:
            {
                // compare with existing single data point
                if (point.getTimestampInMs() < data.get(0).getTimestampInMs())
                {
                    data.add(0, point);

                    last_heart_beat_added_at_index = 0;

                    return true;
                }
                else
                if (point.getTimestampInMs() > data.get(0).getTimestampInMs())
                {
                    data.add(1, point);

                    last_heart_beat_added_at_index = 1;

                    return true;
                }
                else
                {
                    return handleTimestampsEqual(point, 0);
                }
            }

            default:
            {
                // start by checking first and last points
                if (point.getTimestampInMs() < data.get(0).getTimestampInMs())
                {
                    data.add(0, point);

                    last_heart_beat_added_at_index = 0;

                    return true;
                }
                else
                {
                    if ((point.getTimestampInMs() > data.get(size - 1).getTimestampInMs()))
                    {
                        data.add(size, point);

                        last_heart_beat_added_at_index = size;

                        return true;
                    }
                    else
                    {
                        if (point.getTimestampInMs() == data.get(0).getTimestampInMs())
                        {
                            return handleTimestampsEqual(point, 0);
                        }
                        else
                        {
                            if (point.getTimestampInMs() == data.get(size - 1).getTimestampInMs())
                            {
                                return handleTimestampsEqual(point, size - 1);
                            }
                            else
                            {
                                // now check if it fits between any of the existing data points
                                for (int i = 1; i < size; i++)
                                {
                                    if ((point.getTimestampInMs() < data.get(i).getTimestampInMs()) && (point.getTimestampInMs() > data.get(i - 1).getTimestampInMs()))
                                    {
                                        // write data between i and i - 1
                                        data.add(i, point);

                                        last_heart_beat_added_at_index = i;

                                        return true;
                                    }
                                    else
                                    if (point.getTimestampInMs() == data.get(i).getTimestampInMs())
                                    {
                                        return handleTimestampsEqual(point, i);
                                    }
                                    else
                                    {
                                        // Keep iterating...
                                    }
                                }
                            }
                        }
                    }
                }
            }
            break;
        }

        return false;
    }

    boolean handleTimestampsEqual(T point, int i)
    {
        return false;
    }


    public List<DataPoint> getAmplitudeData()
    {
        List<DataPoint> amplitudes_list = new ArrayList<>();

        for(T info : data)
        {
            amplitudes_list.add(new DataPoint(info.getAmplitude(), info.getTimestampInMs()));
        }

        return amplitudes_list;
    }


    public boolean isNextBeatReceived()
    {
        return ((next_beat_received_after_end_of_minute.getAmplitude() != MeasurementVitalSign.INVALID_MEASUREMENT) &&
                (next_beat_received_after_end_of_minute.getTimestampInMs() != MeasurementVitalSign.INVALID_TIMESTAMP));
    }


    public boolean isPreviousBeatReceived()
    {
        return ((previous_beat_received_before_beginning_of_minute.getAmplitude() != MeasurementVitalSign.INVALID_MEASUREMENT) &&
                (previous_beat_received_before_beginning_of_minute.getTimestampInMs() != MeasurementVitalSign.INVALID_TIMESTAMP));
    }


    /* Drops all error codes from the minute unless they are used in the processing (i.e. leads state and
     * NO_BEATS_DETECTED)
     */
    public void filterUnwantedErrorCodesFromMinute()
    {
        List<T> temp = new ArrayList<>();

        for(T info : data)
        {
            if((info.getAmplitude() < ERROR_CODES)
                    || (info.getAmplitude() == ERROR_CODE__LIFETOUCH_LEADS_OFF)
                    || (info.getAmplitude() == ERROR_CODE__LIFETOUCH_LEADS_ON)
                    || (info.getAmplitude() == ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED))
            {
                temp.add(info);
            }
        }

        data = temp;
    }

    public void checkFirstRRIntervalIfRequired(ArrayList<T> return_list)
    {
    }
}
