package com.isansys.patientgateway.algorithms;

import android.content.Intent;

import com.isansys.common.ErrorCodes;
import com.isansys.common.enums.DeviceType;
import com.isansys.common.enums.PatientPositionOrientation;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.IntermediateMeasurement;
import com.isansys.patientgateway.IntermediateSpO2;
import com.isansys.patientgateway.Settings;
import com.isansys.patientgateway.SystemCommands;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.algorithms.Algorithms.AlgorithmMeasurementType;
import com.isansys.patientgateway.factories.IntentFactory;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.isansys.common.measurements.MeasurementVitalSign.INVALID_MEASUREMENT;


/**
 * @author      Rory Morrison <rory.morrison @ isansys.com>
 * <p>
 * HeartBeatProcessor orders and stores heart beats received from the lifetouch. When all heart beats
 * are received, either because an individual minute has been completely transferred or because the
 * lifetouch reports it has no more data to send, each minute is processed to give a Heart Rate
 * measurement and a Respiration Rate measurement.
 */
public class IntermediateMeasurementProcessor<T extends IntermediateMeasurement>
{
    private final RemoteLogging Log;
    private final ContextInterface gateway_context_interface;
    private final String TAG = "IntermediateMeasurementProcessor";

    private final List<MinuteOfData<T>> intermediate_measurement_minutes;

    private final Settings application_settings;

    private final IntentFactory intent_factory;

    public final static String ORIENTATION = "com.patientgateway.heartbeatprocessor.orientation";

    private final SystemCommands commands_to_ui;
    private final ConcurrentLinkedQueue<T> queue_to_database;

    private final OldHeartRate old_heart_rate;
    private final SimpleHeartRate new_heart_rate;

    private final HeartRateFilteredRespirationCalculator respiration_rate_calculator;

    private final T default_generic_instance;

    private DeviceType device_type = DeviceType.DEVICE_TYPE__INVALID;

    /**
     * Constructor for the processor.
     * <p>
     * arguments required for dependency injection.
     * <p>
     * @param context_interface application context, used to send broadcast intents to the rest of the application.
     * @param settings application settings, used to determine which algorithms to use
     * @param log logger object to handle logcat messages
     * @param factory IntentFactory to allow mocking of the intents which send data back to the application
     */
    public IntermediateMeasurementProcessor(ContextInterface context_interface, Settings settings, RemoteLogging log, IntentFactory factory, SystemCommands outgoing_commands, ConcurrentLinkedQueue<T> data_queue, T default_instance)
    {
        gateway_context_interface = context_interface;

        application_settings = settings;

        Log = log;

        intent_factory = factory;

        commands_to_ui = outgoing_commands;
        queue_to_database = data_queue;

        old_heart_rate = new OldHeartRate(Log);
        new_heart_rate = new SimpleHeartRate();

        respiration_rate_calculator = new HeartRateFilteredRespirationCalculator();

        intermediate_measurement_minutes = new ArrayList<>();

        default_generic_instance = default_instance;
    }

    /**
     * Clears all cached data in the processor.
     * <p>
     * Used to empty out any unprocessed heart beats e.g. when ending a lifetouch session, so that they
     * aren't erroneously included in the next device session.
     */
    public void reset()
    {
        intermediate_measurement_minutes.clear();
    }


    public void setDeviceType(DeviceType type)
    {
        device_type = type;
    }

    /**
     * Gets a MinuteOfData from intermediate_measurement_minutes.
     * <p>
     * If the index is less than 0 or greater than the size of the list, returns null.
     * <p>
     * @param index the location in the list
     * @return the MinuteOfData at that index (or null)
     */
    private MinuteOfData<T> getMinuteFromListByIndex(int index)
    {
        if((index < intermediate_measurement_minutes.size()) && (index >= 0))
        {
            return intermediate_measurement_minutes.get(index);
        }

        return null;
    }

    
    /**
     * Sorts and orders heart beats into minutes for processing by the HR and RR algorithms.
     * <p>
     * This function maintains a list of "minutes of data", each of which is a list of heart beats.
     * Each time a new heart beat is received, its timestamp is compared against the existing minutes.
     * If it fits within an existing minute, it is added in the correct place, otherwise a new minute is created.
     * <p>
     * The tag is used to check for a completed minute - if all tags within a minute are consecutive, and the 
     * beats directly before and after it have been received, then all the beats in that minute have been received and 
     * it can be processed.
     * <p>
     * The minute is then sent to the algorithms, which output heart rate and respiration rate.
     * <p>
     */
    public boolean processMeasurement(T point)
    {
        try
        {
            if(handleOrientationErrorCode(point))
            {
                return false;   /* beat was an orientation beat, and so cannot be processed with the others.
                                 * Orientation beats use their own tag, and so don't fit into the minute of data structure.
                                 */

            }

            if(filterDebugBeats(point))
            {
                Log.w(TAG, "processMeasurement: filtered out a debug heart beat");

                /* Ensure it's still written to the database, but not reported to the UI as a heart beat. */
                queue_to_database.add(point);

                return false;  /* Beat amplitude was used to convey debug info about the remote device (in practice, will be a lifetouch)
                                * so ignore it for HR and RR purposes.
                                */
            }

            // check where it goes and insert beat...
            if(intermediate_measurement_minutes.size() == 0)
            {
                // No existing minutes, so create one for the new heart beat.
                intermediate_measurement_minutes.add(getNewMinute(point.getTimestampInMs()));

                // add the HeartBeatInfo
                addDataAndCheckForBeatsToReport(null, intermediate_measurement_minutes.get(0), null, point);
            }
            else
            {
                /* Need to check existing minutes. In ideal operation, the device will never go out of range, so new heart beats
                 * will be received in timestamp order. In that case, the new heart beat will only be either during the most recent 
                 * minute, or directly after it. Therefore, start by looking at the last minute, and work backwards.
                 */

                boolean beat_written = false;

                // Get the index of the last minute in the list. This will be used to iterate back through the list
                int minute_index = intermediate_measurement_minutes.size() - 1;

                while((beat_written == false) && (minute_index >= 0))
                {
                    beat_written = checkDataAgainstMinute(minute_index, point);
                    minute_index--;
                }

                if(beat_written == false)
                {
                    // Beat belongs to a minute before the start of the current first minute in the list.

                    MinuteOfData temp = getNewMinute(point.getTimestampInMs());

                    MinuteOfData first_in_list = getMinuteFromListByIndex(0);

                    // add the HeartBeatInfo
                    addDataAndCheckForBeatsToReport(null, temp, first_in_list, point);

                    // Finally, insert the new minute between minute_to_check and next_minute
                    intermediate_measurement_minutes.add(0, temp);

                }
            }

            // Now check if we can process or delete any of the existing minutes.
            return checkMinutesListForProcessing();
        }
        catch(Exception e)
        {
            Log.e(TAG, "exception occurred:");
            Log.e(TAG, e.toString());
            
            e.printStackTrace();

            return false;
        }
    }


    /**
     * Checks if a DataPoint fits within the minute stored at a particular index of intermediate_measurement_minutes
     * <p>
     * The minute is found by getMinuteFromListByIndex(). The check is performed by MinuteOfData.isTimestampOutsideMinute().
     * <p>
     * If the data point is inside the minute, it will be added to it. If it is before the minute, it will do nothing.
     * <p>
     * If it is after the minute, then a new minute will be created and added to the intermediate_measurement_minutes list.
     * The data point will be added to this new minute
     * <p>
     * IMPORTANT: this function should be called on each index in the intermediate_measurement_minutes list, from highest to lowest in order.
     * That way, a new minute is only created (because the data point goes after the current minute index) if
     * the next minute has been checked and the DataPoint belongs before it (i.e. it belongs between minute n+1 and minute n)
     * <p>
     * @param minute_index the index of the minute in intermediate_measurement_minutes
     * @param point the DataPoint being checked
     * @return true if the DataPoint was added to any minute, false otherwise.
     */
    private boolean checkDataAgainstMinute(int minute_index, T point)
    {
        boolean was_data_added = false;

        long timestamp = point.getTimestampInMs();

        // Get the minute from the list
        MinuteOfData minute_to_check = getMinuteFromListByIndex(minute_index);
        // Also get the two minutes either side in the list
        MinuteOfData next_minute = getMinuteFromListByIndex(minute_index + 1);
        MinuteOfData previous_minute = getMinuteFromListByIndex(minute_index - 1);

        if(minute_to_check != null)
        {
            // Check if new data point is before, after or within the minute.
            int timestamp_outside_minute = minute_to_check.isTimestampOutsideMinute(timestamp);

            switch (timestamp_outside_minute)
            {
                case 1:
                {
                    /* New data point is after minute_to_check.
                     * Since we're working backwards through the list of minutes, we've already checked the next minute in the list (or we're at the end of the list).
                     * It must therefore be in a new minute, after minute_to_check and before the next minute in the list (if it exists).
                     */

                    // So: create the new minute
                    MinuteOfData temp = getNewMinute(point.getTimestampInMs());

                    // add the HeartBeatInfo
                    addDataAndCheckForBeatsToReport(minute_to_check, temp, next_minute, point);

                    // Finally, insert the new minute between minute_to_check and next_minute
                    intermediate_measurement_minutes.add(minute_index + 1, temp);

                    was_data_added = true;
                }
                break;

                case 0:
                {
                    // add the HeartBeatInfo
                    addDataAndCheckForBeatsToReport(previous_minute, minute_to_check, next_minute, point);

                    was_data_added = true;
                }
                break;

                case -1:
                    // data point is earlier than minute_to_check, so keep iterating
                    break;
            }
        }
        else
        {
            // can't check a minute if it doesn't exist so do nothing.
        }

        return was_data_added;
    }

    /**
     * Checks if current_minute is consecutive with previous_minute and with next_minute.
     * <p>
     * Minutes are consecutive if the HeartBeat tags are consecutive and the time difference is small
     * enough that a tag rollover is unlikely or impossible.
     * <p>
     * The check is done by checking the last beat of the current minute against the first of the next_minute,
     * and the last of the previous_minute against the first of the current_minute.
     * <p>
     * If both these checks show the beats are consecutive, then the next_beat_received_after_end_of_minute and
     * previous_beat_received_before_beginning_of_minute are set to the relevant heart beats.
     * <p>
     * @param previous_minute the MinuteOfData before the current_minute
     * @param current_minute the MinuteOfData being checked
     * @param next_minute the MinuteOfData after the current_minute
     */
    private ArrayList<T> consecutiveMinutesCheck(MinuteOfData<T> previous_minute, MinuteOfData<T> current_minute, MinuteOfData<T> next_minute)
    {
        ArrayList<T> return_list = new ArrayList<>();

        /* Check if new beat is consecutive with first beat of next_minute
         * (if next_minute is null, second condition will not be evaluated).
         */
        if ((next_minute != null) && (current_minute.areMinutesConsecutive(next_minute)))
        {
            // If it is consecutive, we store the information so that we know there are no more beats expected between the two minutes
            current_minute.next_beat_received_after_end_of_minute = next_minute.getFirstDataPoint();
            next_minute.previous_beat_received_before_beginning_of_minute = current_minute.getLastDataPoint();

            // Check if the update has made the first R-R interval in the next minute calculable
            next_minute.checkFirstRRIntervalIfRequired(return_list);
        }

        /* Check if new beat is consecutive with last beat of previous minute
         * (if previous_minute is null, second condition will not be evaluated).
         */
        if ((previous_minute != null) && (previous_minute.areMinutesConsecutive(current_minute)))
        {
            // If it is consecutive, we store the information so that we know there are no more beats expected between the two minutes
            current_minute.previous_beat_received_before_beginning_of_minute = previous_minute.getLastDataPoint();
            previous_minute.next_beat_received_after_end_of_minute = current_minute.getFirstDataPoint();

            // Check if the update has made the first R-R interval in the current minute calculable
            current_minute.checkFirstRRIntervalIfRequired(return_list);
        }

        return return_list;
    }

    /**
     * Checks the list intermediate_measurement_minutes and processes any minutes that are ready.
     * <p>
     * Minutes are ready if they contain no gaps (all heart beat tags are consecutive in the data list)
     * and the next and previous heart beats have been received.
     * <p>
     * Processing is done by processOneMinuteOfHeartBeats().
     */
    private boolean checkMinutesListForProcessing()
    {
        ListIterator<MinuteOfData<T>> processing_iterator = intermediate_measurement_minutes.listIterator();
        boolean minutes_processed = false;

        try
        {
            while(processing_iterator.hasNext())
            {
                MinuteOfData<T> minute_to_process = processing_iterator.next();

                /* Now process the full minutes
                 *
                 * To process it, we need to have received the consecutive heart beat before the first beat in the minute, and the consecutive heart beat after the last beat in the minute.
                 * We've been tracking that for each minute as we add new beats.
                 */
                if((minute_to_process.isPreviousBeatReceived()) && (minute_to_process.isNextBeatReceived()))
                {
                    // Also require all beats within the minute to be consecutive, so we know ALL data has been received.
                    if(minute_to_process.doesMinuteContainGaps() != true)
                    {
                        // If all data received, process the minute...
                        processOneMinuteOfData(minute_to_process);
                        minutes_processed = true;
                        // Then remove it from the list so we don't process it again (and it frees up the memory)
                        processing_iterator.remove();
                    }
                    else
                    {
                        // Don't process the minute
                    }
                }
                else
                {
                    // Still don't process the minute
                }
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "checkMinutesForProcessing: exception occurred in algorithm processing:");
            Log.e(TAG, e.toString());

            e.printStackTrace();
        }

        return minutes_processed;
    }


    /**
     * Processes all minutes of data except for the last one.
     * <p>
     * This function is called when the lifetouch reports no pending heart beats.
     * At that point, any minutes that haven't yet been processed (apart from the most recent one which is still being added to)
     * must be complete, since there are no more heart beats for the lifetouch to send.
     * <p>
     * Therefore we can call processOneMinuteOfHeartBeats on them and then delete them.
     */
    public boolean processAnyOutstandingData()
    {
        boolean minutes_processed = false;

        // If size of list <= 1, there are no outstanding minutes to process
        if(intermediate_measurement_minutes.size() > 1)
        {
            // Process all minutes except the last one.
            int limit = intermediate_measurement_minutes.size() - 1;
            
            for(int i = 0; i < limit; i++)
            {
                // Each time, process the first minute
                MinuteOfData minute_to_process = intermediate_measurement_minutes.get(0);

                processOneMinuteOfData(minute_to_process);
                
                // Then delete it, so next time round the second minute has become the first.
                intermediate_measurement_minutes.remove(0);

                minutes_processed = true;
            }
        }

        return minutes_processed;
    }

    // Processing SpO2 intermediate measurements after the Nonin has been disconnected for >60 secs (see calling function)
    // Otherwise these remaining partially filled minutes will only be processed when the next intermediate is received which may be hours later
    // Copied from above, only differences being not subtracting 1 in int limit = intermediate_measurement_minutes.size() - 1; and checking for intermediate_measurement_minutes.size() > 0 instead of 1
    public boolean processAnyOutstandingDataIncludingLastMinute()
    {
        boolean minutes_processed = false;

        // If size of list <= 1, there are no outstanding minutes to process
        if(intermediate_measurement_minutes.size() > 0)
        {
            // Process all minutes except the last one.
            int limit = intermediate_measurement_minutes.size();

            for(int i = 0; i < limit; i++)
            {
                // Each time, process the first minute
                MinuteOfData minute_to_process = intermediate_measurement_minutes.get(0);

                processOneMinuteOfData(minute_to_process);

                // Then delete it, so next time round the second minute has become the first.
                intermediate_measurement_minutes.remove(0);

                minutes_processed = true;
            }
        }

        return minutes_processed;
    }

    
    /**
     * Checks for correct algorithm to call. Currently only need to process heart beats.
     *
     * @param minute_to_process
     */
    private void processOneMinuteOfData(MinuteOfData<T> minute_to_process)
    {
        if(default_generic_instance instanceof HeartBeatInfo)
        {
            processOneMinuteOfHeartBeats(minute_to_process);
        }
        else if(default_generic_instance instanceof IntermediateSpO2)
        {
            processOneMinuteOfSpO2(minute_to_process);
        }
        else
        {
            Log.e(TAG, "processOneMinuteOfData: Unknown Data Type!");
        }
    }


    private void processOneMinuteOfSpO2(MinuteOfData<T> minute)
    {
        // Keep track of the number of Invalid Data or Finger Off events detected each minute
        int number_of_pulse_ox_invalid_data_or_finger_off_detected_in_minute_sample = 0;
        int number_of_pulse_ox_valid_data_in_minute_sample = 0;

        int number_of_missing_pulse_ox_data_in_minute = 60 - minute.data.size(); //The assumption here is that there are always 60 samples in a minute...

        int spo2_sum = 0;
        int hr_sum = 0;

        MeasurementSpO2 average_measurement = new MeasurementSpO2();

        for (T measurement : minute.data)
        {
            // Keep track of the number of invalid intermediate measurements
            if (measurement.getAmplitude() == INVALID_MEASUREMENT)
            {
                number_of_pulse_ox_invalid_data_or_finger_off_detected_in_minute_sample++;
            }
            else
            {
                IntermediateSpO2 cast_measurement = (IntermediateSpO2) (measurement);
                spo2_sum += cast_measurement.getSpO2();
                hr_sum += cast_measurement.getPulse();

                number_of_pulse_ox_valid_data_in_minute_sample++;
            }
        }

        // Add the number of missing samples as they should count as invalid for these purposes.
        number_of_pulse_ox_invalid_data_or_finger_off_detected_in_minute_sample += number_of_missing_pulse_ox_data_in_minute;

        if (number_of_pulse_ox_valid_data_in_minute_sample == 0)
        {
            // If there are no intermediate measurements for this minute then mark it as invalid
            average_measurement.SpO2 = INVALID_MEASUREMENT;
            average_measurement.pulse = INVALID_MEASUREMENT;
        }
        else if (number_of_pulse_ox_invalid_data_or_finger_off_detected_in_minute_sample > application_settings.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid())
        {
            // If there have been too many invalid intermediate measurements (invalid data or finger off), then set this minutes measurement as invalid
            Log.e(TAG, "number_of_pulse_ox_invalid_data_or_finger_off_detected_in_minute_sample = " + number_of_pulse_ox_invalid_data_or_finger_off_detected_in_minute_sample + ". Writing INVALID_MEASUREMENT");

            average_measurement.SpO2 = INVALID_MEASUREMENT;
            average_measurement.pulse = INVALID_MEASUREMENT;
        }
        else
        {
            double mean_spo2;
            double mean_hr;

            mean_spo2 = (double)spo2_sum / (double)number_of_pulse_ox_valid_data_in_minute_sample;
            mean_hr = (double)hr_sum / (double)number_of_pulse_ox_valid_data_in_minute_sample;

            /* Round to NEAREST integer. Casting to an int simply discards the decimal places, so by adding 0.5, rounding is done correctly.
             * e.g 1.5 becomes 2.0, and is cast to 2, so is correctly rounded up.
             * 1.49 becomes 1.99 but is still cast to 1, so correctly rounded down.
             * 1.99 becomes 2.49, which is cast to 2, so is correctly rounded up.
             * Presumably at some point we run into rounding errors with double precision, but we're rounding to an integer here, so we don't really care.
             *
             * Also note: casting NaN in this way gives us an int of 0 - slightly counterintuitive, as we might expect some kind of divide by zero error.
             * So need to be careful we never do that!
             */

            average_measurement.SpO2 = (int) (mean_spo2 + 0.5);
            average_measurement.pulse = (int) (mean_hr + 0.5);
        }

        average_measurement.timestamp_in_ms = minute.end_timestamp;

        reportPulseOxMeasurement(average_measurement);
    }



    /**
     * Extracts a heart rate and respiration rate from a minute's worth of data
     * <p>
     * If any beat in the minute is a leads off or leads on error value, the whole minute's HR and RR
     * will be set to the corresponding error value. This is used when displaying the data to provide
     * grey bars across the graph.
     * <p>
     * Otherwise the relevant algorithms will be called to calculate the HR and RR
     * <p>
     * @param minute the data to analyse
     */
    private void processOneMinuteOfHeartBeats(MinuteOfData<T> minute)
    {
        int errorCode = 0;

        minute.filterUnwantedErrorCodesFromMinute();

        // Amplitude below which we are operating out of the documents spec of the Lifetouch product
        int number_of_low_amplitude_beats = 0;

        // Amplitude above which we are operating out of the documents spec of the Lifetouch product
        int number_of_high_amplitude_beats = 0;

        int percentage_of_poor_signal_heart_beats_before_minute_marked_invalid = application_settings.getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid();

        for(int counter=0; counter<minute.data.size(); counter++)
        {
            int amplitudeOfBeat = minute.data.get(counter).getAmplitude();
            if (amplitudeOfBeat >= ErrorCodes.ERROR_CODES)
            {
                errorCode = amplitudeOfBeat;
            }
            else if (amplitudeOfBeat < 112)
            {
                // If the Amplitude is below 300 uV (112 ADC) then we are out of the Operating Spec of the Lifetouch.
                // The hardware can detect this but if we go about 240 bpm then it starts miscalculating. This puts a hard limit in.
                number_of_low_amplitude_beats++;
            }
            else if (amplitudeOfBeat > 3724)
            {
                // If the Amplitude is above 3724 ADC counts (10mV) then we are out of the Operating Spec of the Lifetouch.
                // The hardware can detect this but if we go about 240 bpm then it starts miscalculating. This puts a hard limit in.
                number_of_high_amplitude_beats++;
            }
        }

        if (number_of_low_amplitude_beats > 0)
        {
            double percentage_of_low_amplitude_beats_in_minute = (double)number_of_low_amplitude_beats / (double)minute.data.size();
            percentage_of_low_amplitude_beats_in_minute = percentage_of_low_amplitude_beats_in_minute * 100;

            if ((int)percentage_of_low_amplitude_beats_in_minute >= percentage_of_poor_signal_heart_beats_before_minute_marked_invalid)
            {
                errorCode = ErrorCodes.ERROR_CODE__LIFETOUCH_POOR_SIGNAL_IN_LAST_MINUTE;
            }
        }

        if (number_of_high_amplitude_beats > 0)
        {
            double percentage_of_high_amplitude_beats_in_minute = (double)number_of_high_amplitude_beats / (double)minute.data.size();
            percentage_of_high_amplitude_beats_in_minute = percentage_of_high_amplitude_beats_in_minute * 100;

            if ((int)percentage_of_high_amplitude_beats_in_minute >= percentage_of_poor_signal_heart_beats_before_minute_marked_invalid)
            {
                errorCode = ErrorCodes.ERROR_CODE__LIFETOUCH_POOR_SIGNAL_IN_LAST_MINUTE;
            }
        }

        int heart_rate;
        int respiration_rate;

        if(errorCode == 0)
        {
            if (minute.data.size() > 2) // Interpolation in the respiration rate requires at least 3 beats to work.
            {
                // Calculate the Heart Rate
                if (application_settings.getSimpleHeartRateEnabledStatus())
                {
                    heart_rate = new_heart_rate.getHeartRate(minute);
                }
                else
                {
                    heart_rate = old_heart_rate.getHeartRate(minute);
                }

                // Calculate the Respiration Rate
                respiration_rate = (int) (respiration_rate_calculator.filteredPeakDetect(minute.getAmplitudeData(), heart_rate) + 0.5);
            }
            else
            {
                /* Respiration rate algorithm doesn't work with so few points.
                 * Report invalid data.
                 */
                heart_rate = ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM;
                respiration_rate = ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM;
            }
        }
        else
        {
            // Report leads off value
            heart_rate = errorCode;
            respiration_rate = errorCode;
        }

        reportHeartRateMeasurement(heart_rate, minute.end_timestamp);
        reportRespirationRateMeasurement(respiration_rate, minute.end_timestamp);

        if (!application_settings.getDisableCommentsForSpeed())
        {
            Log.d(TAG, "HR = " + heart_rate + "; AM Respiration rate found = " + respiration_rate + "; timestamp = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(minute.end_timestamp));
        }
    }

    /** Orientation error codes have their own separate tag, so can't be processed as heart beats - instead they are reported as orientation measurements.
     * All other error codes must be written to the DB as heat beats so that a record is kept - this means they must be processed as heart beats, sorted by tag and timestamp etc.
     * <p>
     * @param point HeartBeatInfo to filter
     * @return boolean whether the beat was filtered or not
     */
    private boolean handleOrientationErrorCode(T point)
    {
        if ((point.getAmplitude() >= ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_BASE) &&
                (point.getAmplitude() < (ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_BASE + PatientPositionOrientation.values().length)))
        {
            // Process the beat as an orientation not a heart beat.
            processOrientationBeat(point);

            return true;
        }

        return false;
    }


    /** Some error codes sent from the remote device refer to events in the device that don't invalidate
     *  the minute of data. Others, like NO_BEATS_DETECTED should invalidate the minute as normal.
     *
     *  Beats filtered out are:
     *      LIFETOUCH_CONNECTION_TIMEOUT
     *      LIFETOUCH_DATA_CREDIT_TIMEOUT
     *      LIFETOUCH_ACK_TIMEOUT
     *      LIFETOUCH_ADVERTISING_TIMEOUT
     */
    private boolean filterDebugBeats(T point)
    {
        switch(point.getAmplitude())
        {
            case ErrorCodes.ERROR_CODE__LIFETOUCH_CONNECTION_TIMEOUT:
            case ErrorCodes.ERROR_CODE__LIFETOUCH_DATA_CREDIT_TIMEOUT:
            case ErrorCodes.ERROR_CODE__LIFETOUCH_ACK_TIMEOUT:
            case ErrorCodes.ERROR_CODE__LIFETOUCH_ADVERTISING_TIMEOUT:
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Used to handle heart beats encoding the patient's orientation
     * <p>
     * Orientation (standing, lying on back, front, side etc.) may optionally be sent by the lifetouch
     * encoded as a heart beat. These beats are excluded from the MinuteOfData object as they would
     * interfere with HR and RR calculations. Not currently used.
     * <p>
     * @param point Data point to process
     */
    private void processOrientationBeat(T point)
    {
    	 PatientPositionOrientation orientation = PatientPositionOrientation.ORIENTATION_UNKNOWN;
    	 
    	 try
    	 {
    		 int orientation_as_int = point.getAmplitude() - ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_BASE;
         	
         	 orientation = PatientPositionOrientation.values()[orientation_as_int];
    	 }
    	 catch(Error e)
    	 {
    		 Log.e(TAG, "HeartBeatProcessor:  Received wrong orientation values.");
    	 }
    	 
     	 reportOrientation(orientation, point.getTimestampInMs());
    }
    
    
    private void reportOrientation(PatientPositionOrientation orientation, long timestamp)
    {
        Intent outgoing_intent = intent_factory.getNewIntent(Algorithms.INTENT__INCOMING_FROM_ALGORITHMS);
        outgoing_intent.putExtra("measurement_type", AlgorithmMeasurementType.ORIENTATION_MEASUREMENT.ordinal());
        outgoing_intent.putExtra(ORIENTATION, orientation.ordinal());
        outgoing_intent.putExtra("timestamp", timestamp);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }
    
    
    private void reportHeartRateMeasurement(int heart_rate, long timestamp)
    {
        Intent outgoing_intent = intent_factory.getNewIntent(Algorithms.INTENT__INCOMING_FROM_ALGORITHMS);
        outgoing_intent.putExtra("measurement_type", AlgorithmMeasurementType.HEART_RATE_MEASUREMENT.ordinal());
        outgoing_intent.putExtra("heart_rate", heart_rate);
        outgoing_intent.putExtra("timestamp", timestamp);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }

    
    private void reportRespirationRateMeasurement(int respiration_rate, long timestamp)
    {
        Intent outgoing_intent = intent_factory.getNewIntent(Algorithms.INTENT__INCOMING_FROM_ALGORITHMS);
        outgoing_intent.putExtra("measurement_type", AlgorithmMeasurementType.RESPIRATION_RATE_MEASUREMENT.ordinal());
        outgoing_intent.putExtra("respiration_rate", respiration_rate);
        outgoing_intent.putExtra("timestamp", timestamp);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    private void reportPulseOxMeasurement(MeasurementSpO2 measurement)
    {
        Intent outgoing_intent = intent_factory.getNewIntent(Algorithms.INTENT__INCOMING_FROM_ALGORITHMS);
        outgoing_intent.putExtra("measurement_type", AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal());
        outgoing_intent.putExtra("device_type", device_type.ordinal());
        outgoing_intent.putExtra("measurement", measurement);
        gateway_context_interface.sendBroadcastIntent(outgoing_intent);
    }


    private void reportData(ArrayList<T> data)
    {
        if(default_generic_instance instanceof HeartBeatInfo)
        {
            reportHeartBeats(data);
        }
        else if(default_generic_instance instanceof IntermediateSpO2)
        {
            queueIntermediatesForDatabaseWrite(data);
        }
        else
        {
            Log.e(TAG, "reportData: Unknown data type!");
        }
    }


    private void reportHeartBeats(ArrayList<T> beats)
    {
        // Specific to heart beats...
        // ToDo: make UI reporting generic as well.
        ArrayList<HeartBeatInfo> list_to_report = new ArrayList<>();

        for(T measurement : beats)
        {
            list_to_report.add((HeartBeatInfo)measurement);
        }

        commands_to_ui.reportHeartBeats(list_to_report);

        queueIntermediatesForDatabaseWrite(beats);
    }


    private void queueIntermediatesForDatabaseWrite(List<T> beats)
    {
//        queue_to_database.addAll(beats);
        // Using the above code breaks the HeartBeatProcessor unit tests
        // ToDo: switch back to the above (i.e. addAll()), and fix the tests so they don't specifically require add() to have been called.
        for(T beat : beats)
        {
            queue_to_database.add(beat);
        }
    }


    //This is specific to HeartBeats because it calculates RR intervals...
    private void addDataAndCheckForBeatsToReport(MinuteOfData<T> previous_minute, MinuteOfData<T> current_minute, MinuteOfData<T> next_minute, T data)
    {
        if(current_minute.addDataPoint(data))
        {
            ArrayList<T> beats_to_report;

            if(data.hasRrIntervals())
            {
                // Check if temp is consecutive with either next or current minute
                beats_to_report = consecutiveMinutesCheck(previous_minute, current_minute, next_minute);

                // This will only get new RR intervals (i.e. ones that haven't been calculated yet), so we can get HeartBeatInfos including RR intervals to report to the UI

                beats_to_report.addAll(((MinuteOfHeartBeats)current_minute).calculateNewRrIntervals());

                if (next_minute != null)
                {
                    next_minute.checkFirstRRIntervalIfRequired(beats_to_report);
                }
            }
            else
            {
                beats_to_report = new ArrayList<>();

                beats_to_report.add(data);
            }

            if (beats_to_report.size() > 0)
            {
                // Send them to the UI, and queue them for writing to the DB
                reportData(beats_to_report);
                //Log.d(TAG, "Reporting " + beats_to_report.size() +" beats");
            }
            else
            {
                //Log.d(TAG, "No new beats to report");
            }
        }
    }

    private MinuteOfData getNewMinute(long timestamp)
    {
        if(default_generic_instance instanceof HeartBeatInfo)
        {
            return new MinuteOfHeartBeats<>(timestamp, default_generic_instance);
        }
        else if(default_generic_instance instanceof IntermediateSpO2)
        {
            return new MinuteOfSpO2<>(timestamp, default_generic_instance);
        }
        else
        {
            return new MinuteOfData<>(timestamp, default_generic_instance);
        }
    }
}
