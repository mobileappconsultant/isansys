package com.isansys.patientgateway;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.text.format.DateUtils;

import com.isansys.common.ErrorCodes;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.algorithms.EarlyWarningScoreProcessor;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.patientgateway.ntpTimeSync.TimeSource;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

class LongTermMeasurementValidityTracker
{
    private final String TAG = "LongTermMeasurementValidityTracker";

    private final ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements;

    private Timer measurement_validity_timer;

    private final RemoteLogging Log;
    private final EarlyWarningScoreProcessor early_warning_score_processor;
    private final TimeSource gateway_ntp_time;
    private final DeviceInfoManager device_info_manager;

    // This keeps track of what to do when a measurements validity time expires - keep telling the EWS Processor to expect more readings, or remove it from the EWS processing
    private final ConcurrentMap<VitalSignType, Boolean> map_vital_sign_types;
    private final boolean KEEP = true;
    private final boolean REMOVE = false;

    LongTermMeasurementValidityTracker(RemoteLogging logger,
                                       EarlyWarningScoreProcessor ews_processor,
                                       TimeSource ntp,
                                       DeviceInfoManager device_info_manager)
    {
        this.early_warning_score_processor = ews_processor;
        this.gateway_ntp_time = ntp;
        this.Log = logger;
        this.device_info_manager = device_info_manager;

        tracked_measurements = new ConcurrentHashMap<>();

        map_vital_sign_types = new ConcurrentHashMap<>();
        for (VitalSignType vital_sign_type : VitalSignType.values())
        {
            map_vital_sign_types.put(vital_sign_type, KEEP);
        }

        reset();
    }


    public void reset()
    {
        Log.d(TAG, "reset");

        createLongTermMeasurementTimer();

        tracked_measurements.clear();
    }


    private void createLongTermMeasurementTimer()
    {
        long time_now = gateway_ntp_time.currentTimeMillis();

        long initial_delay = DateUtils.MINUTE_IN_MILLIS - (time_now % DateUtils.MINUTE_IN_MILLIS) + DateUtils.SECOND_IN_MILLIS;

        Log.d(TAG, "Creating timer at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(time_now));
        Log.d(TAG, "Initial delay is " + TimestampConversion.convertDateToUtcHumanReadableStringHoursMinutesSecondsMilliseconds(initial_delay));

        GenericStartStopTimer.cancelTimer(measurement_validity_timer, Log);
        measurement_validity_timer = new Timer();
        measurement_validity_timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                long current_time = gateway_ntp_time.currentTimeMillis();

                String log_line = "Measurement Validity timer fired at " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(current_time);
                log_line = log_line.concat(" : Tracked measurements = ");
                log_line = log_line.concat(Utils.dumpTrackedMeasurementVitalSignNames(tracked_measurements));
                Log.d(TAG, log_line);

                // This FOR loop forces the loop to be in Vital Sign Order. E.g. we remove a BP sensor reading, before check if the Manual BP is to be used
                for (VitalSignType vital_sign_type : VitalSignType.values())
                {
                    TrackedMeasurement tracked_measurement = tracked_measurements.get(vital_sign_type);

                    if (tracked_measurement != null)
                    {
                        boolean measurement_expired = tracked_measurement.measurement.timestamp_in_ms >= tracked_measurement.timeout_time;

                        log_line = "Measurement Validity : Checking " + Utils.padVitalSignName(tracked_measurement.getType());
                        log_line = log_line.concat(" : valid = " + tracked_measurement.valid);
                        log_line = log_line.concat(" : timestamp = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(tracked_measurement.measurement.timestamp_in_ms));
                        log_line = log_line.concat(" : current_timestamp = " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(current_time));
                        log_line = log_line.concat(" : measurement_expired = " + measurement_expired);
                        Log.d(TAG, log_line);

                        while ((tracked_measurement.valid) && (tracked_measurement.measurement.timestamp_in_ms < current_time))
                        {
                            if (tracked_measurement.measurement.timestamp_in_ms < tracked_measurement.timeout_time)
                            {
                                Log.d(TAG, "Measurement Validity :      " + tracked_measurement.getType() + " : tracked_measurement.valid = true");

                                sendToEarlyWarningScoreProcessorIfNeededAndUpdateTimestamp(tracked_measurement);
                            }
                            else
                            {
                                tracked_measurement.valid = false;

                                Log.d(TAG, "Measurement Validity :      " + tracked_measurement.getType() + " : tracked_measurement.valid = false");

                                // The measurement has expired. Check to see if there is a Device Session in progress
                                // If NOT then it means the sensor has been removed from the Session via the Change Session Settings page
                                // Work out if this class needs to carry on telling the EWS processor to wait for more measurements of this vital sign type or not.
                                // User selects this via the Keep/Remove popup.
                                boolean deviceSessionInProgress = device_info_manager.isDeviceSessionInProgress(vital_sign_type);
                                if (!deviceSessionInProgress)
                                {
                                    Log.d(TAG, "No device session in progress for " + tracked_measurement.getType());

                                    if (map_vital_sign_types.get(vital_sign_type) == REMOVE)
                                    {
                                        Log.d(TAG, tracked_measurement.getType() + " : REMOVE");

                                        Log.d(TAG, "Removing " + tracked_measurement.getType() + " from tracked_measurements list");
                                        tracked_measurements.remove(tracked_measurement.getType());
                                        Log.d(TAG, "Tracked measurements are now : " + Utils.dumpTrackedMeasurementVitalSignNames(tracked_measurements));

                                        // Reset back to default of KEEP for next time a sensor is added with this vital sign type
                                        map_vital_sign_types.put(vital_sign_type, KEEP);
                                    }
                                    else
                                    {
                                        Log.d(TAG, tracked_measurement.getType() + " : KEEP");
                                    }
                                }
                            }
                        }
                    }
                }

                early_warning_score_processor.setExpectedMeasurementsForMinute(tracked_measurements, roundTimestampDownToMinute(current_time));
            }
        }, initial_delay, DateUtils.MINUTE_IN_MILLIS);
    }


    private void sendToEarlyWarningScoreProcessorIfNeededAndUpdateTimestamp(TrackedMeasurement tracked_measurement)
    {
        reportMeasurementToEarlyWarningScoreProcessor(tracked_measurement);

        // Update its timestamp in prep for next time ita accessed
        if ((tracked_measurement.measurement.timestamp_in_ms % DateUtils.MINUTE_IN_MILLIS) > 0)
        {
            // Round down to a whole multiple of a minute
            tracked_measurement.measurement.timestamp_in_ms = roundTimestampDownToMinute(tracked_measurement.measurement.timestamp_in_ms);
        }

        tracked_measurement.measurement.timestamp_in_ms += DateUtils.MINUTE_IN_MILLIS;
    }


    private long roundTimestampDownToMinute(long unrounded)
    {
        long result = unrounded / DateUtils.MINUTE_IN_MILLIS;
        result *= DateUtils.MINUTE_IN_MILLIS;

        return result;
    }


    public void addNewLongTermMeasurement(MeasurementVitalSign measurement)
    {
        String log_line = "addNewLongTermMeasurement : " + measurement.getType() + " : measurement_validity_time_in_seconds " + measurement.measurement_validity_time_in_seconds + ". Value = " + measurement.getPrimaryMeasurement() + " @ " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms);

        long millisecond_timeout_length = measurement.measurement_validity_time_in_seconds * DateUtils.SECOND_IN_MILLIS;

        TrackedMeasurement existing_measurement = tracked_measurements.get(measurement.getType());

        if (existing_measurement != null)
        {
            if(existing_measurement.initial_measurement_time <= measurement.timestamp_in_ms)
            {
                if((measurement.getPrimaryMeasurement() < ErrorCodes.ERROR_CODES)
                        && (measurement.getPrimaryMeasurement() != MeasurementVitalSign.INVALID_MEASUREMENT))
                {
                    log_line = log_line.concat(" : Updating existing tracked_measurement");
                    Log.d(TAG, log_line);

                    Log.d(TAG, "addNewLongTermMeasurement : Tracked Measurements = " + Utils.dumpTrackedMeasurementVitalSignNames(tracked_measurements));

                    long temp_initial_measurement_time = measurement.timestamp_in_ms;

                    boolean measurement_overlap = measurement.timestamp_in_ms < existing_measurement.measurement.timestamp_in_ms;
                    // Copy the existing tracked timestamp to the new measurement if there is a validity overlap.
                    // Otherwise we'd report the new tracked measurement several times starting from the
                    // initial timestamp and reporting every minute until the current time. Those minutes
                    // already have the old tracked measurement reported for them.
                    if (measurement_overlap)
                    {
                        measurement.timestamp_in_ms = existing_measurement.measurement.timestamp_in_ms;
                    }

                    existing_measurement.measurement = measurement;
                    existing_measurement.timeout_time = temp_initial_measurement_time + millisecond_timeout_length;
                    existing_measurement.valid = true;
                    existing_measurement.initial_measurement_time = temp_initial_measurement_time;

                    if(!measurement_overlap)
                    {
                        // Only report the new measurement if there was no overlap. If there was an overlap,
                        // The new measurement now has a timestamp set to the next minute, and can be reported as normal when the
                        // timer fires. It also means the tracked measurement has been reported for this minute
                        // so reporting the new measurement < 1 min later could cause strange behaviour.
                        sendToEarlyWarningScoreProcessorIfNeededAndUpdateTimestamp(existing_measurement);
                    }
                }
                else
                {
                    log_line = log_line.concat(" : NOT updating existing tracked_measurement - new measurement invalid or error code");
                    Log.d(TAG, log_line);
                    Log.d(TAG, "addNewLongTermMeasurement : Tracked Measurements = " + Utils.dumpTrackedMeasurementVitalSignNames(tracked_measurements));
                }
            }
            else
            {
                log_line = log_line.concat(" : Tracked measurement is tracking a more recent timestamp than the received measurement");
                Log.d(TAG, log_line);

                Log.d(TAG, "addNewLongTermMeasurement : Tracked Measurements = " + Utils.dumpTrackedMeasurementVitalSignNames(tracked_measurements));

                TrackedMeasurement temp_tracked_measurement = new TrackedMeasurement(measurement, millisecond_timeout_length);
                sendToEarlyWarningScoreProcessorIfNeededAndUpdateTimestamp(temp_tracked_measurement);
            }
        }
        else
        {
            log_line = log_line.concat(" : Adding new tracked_measurement");
            Log.d(TAG, log_line);

            TrackedMeasurement new_tracked_measurement = new TrackedMeasurement(measurement, millisecond_timeout_length);
            tracked_measurements.put(measurement.getType(), new_tracked_measurement);

            Log.d(TAG, "addNewLongTermMeasurement : Tracked Measurements = " + Utils.dumpTrackedMeasurementVitalSignNames(tracked_measurements));

            sendToEarlyWarningScoreProcessorIfNeededAndUpdateTimestamp(new_tracked_measurement);
        }
    }


    public void removeVitalSignFromTrackedMeasurementsOnceItsExpired(VitalSignType vitalSignType)
    {
        Log.d(TAG, "removeVitalSignFromTrackedMeasurementsOnceItsExpired : " + vitalSignType);

        // do we have a valid tracked measurement?
        if(tracked_measurements.get(vitalSignType).valid == true)
        {
            map_vital_sign_types.put(vitalSignType, REMOVE); // remove it when it times out
        }
        else
        {
            tracked_measurements.remove(vitalSignType); // invalid already so remove it now
        }
    }


    private void reportMeasurementToEarlyWarningScoreProcessor(TrackedMeasurement tracked_measurement)
    {
        Log.d(TAG, "reportMeasurementToEarlyWarningScoreProcessor : " + tracked_measurement.getType()
                + " : Measurement Time : " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(tracked_measurement.measurement.timestamp_in_ms)
                + " : Expiry Time = " + TimestampConversion.convertDateToHumanReadableStringYearMonthDayHoursMinutesSeconds(tracked_measurement.timeout_time)
                + " : Value = " + tracked_measurement.measurement.getPrimaryMeasurement());

        early_warning_score_processor.processMeasurement(tracked_measurement.measurement);
    }
}
