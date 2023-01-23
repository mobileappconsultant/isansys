package com.isansys.patientgateway.algorithms;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.content.Intent;
import androidx.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Pair;

import com.isansys.common.enums.MeasurementTypes;
import com.isansys.common.ErrorCodes;
import com.isansys.common.ThresholdSetLevel;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.TimestampConversion;
import com.isansys.patientgateway.TrackedMeasurement;
import com.isansys.patientgateway.Utils;
import com.isansys.patientgateway.algorithms.Algorithms.AlgorithmMeasurementType;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.common.measurements.MeasurementVitalSign;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.factories.IntentFactory;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

public class EarlyWarningScoreProcessor
{
    private final RemoteLogging Log;
    private final ContextInterface context_interface;

    private final String TAG = "EarlyWarningScoreProcessor";
    
    private ArrayList<ArrayList<ThresholdSetLevel>> cached_ews_thresholds_list = new ArrayList<>();

    // Using EarlyWarningScoreMinute.start_timestamp as the Long Key for the HashMap
    private final ConcurrentHashMap<Long, EarlyWarningScoreMinute> early_warning_score_minutes = new ConcurrentHashMap<>();

    private final IntentFactory intent_factory;
    private final DeviceInfoManager device_info_manager;

    private String ews_set_name = "";

    private boolean processing_enabled;

    public EarlyWarningScoreProcessor(ContextInterface context_interface,
                                      RemoteLogging logger,
                                      IntentFactory factory,
                                      DeviceInfoManager device_info_manager)
    {
        this.context_interface = context_interface;
        this.Log = logger;
        intent_factory = factory;
        this.device_info_manager = device_info_manager;
        processing_enabled = false;
    }
    
    
    public void reset() 
    {
        ews_set_name = "";
        cached_ews_thresholds_list = new ArrayList<>();                                 // Note that cached_ews_thresholds_list refers to one of the threshold lists in PatientGatewayService,
                                                                                        // so calling clear() here would also clear that list, which we don't want. Instead, we make
                                                                                        // cached_ews_thresholds_list refer to a new empty list until cacheSelectedEarlyWarningScores is called again.
        early_warning_score_minutes.clear();
        processing_enabled = false;
    }


    public void enableProcessing(boolean enable)
    {
        processing_enabled = enable;
    }


    public boolean processingEnabled()
    {
        return processing_enabled;
    }


    public void cacheSelectedEarlyWarningScores(ArrayList<ArrayList<ThresholdSetLevel>> thresholds_in_use, String ews_set)
    {
        cached_ews_thresholds_list = thresholds_in_use;
        ews_set_name = ews_set;
    }


    // Called by Long Term Measurement Validity Tracker only (sendToEarlyWarningScoreProcessorIfNeededAndUpdateTimestamp)
    public void processMeasurement(MeasurementVitalSign measurement)
    {
        if(processing_enabled)
        {
            Log.d(TAG, "processMeasurement : " + measurement.getType() + " : Value = " + measurement.getPrimaryMeasurement());

            ArrayList<ThresholdSetLevel> thresholds = new ArrayList<>();
            double value = MeasurementVitalSign.INVALID_MEASUREMENT;

            MeasurementTypes measurement_type = MeasurementTypes.getMeasurementTypeFromVitalSignType(measurement.getType());

            if (measurement_type != MeasurementTypes.UNKNOWN)
            {
                if (cached_ews_thresholds_list.size() > measurement_type.getValue())
                {
                    thresholds = cached_ews_thresholds_list.get(measurement_type.getValue());
                    value = measurement.getPrimaryMeasurement();
                }
                else
                {
                    Log.e(TAG, "processMeasurement: Threshold set not available for measurement " + measurement_type);
                }
            }

            // If we have thresholds, and value is valid and not an error code, get the EWS value from them
            if ((thresholds.size() > 0) && (value != MeasurementVitalSign.INVALID_MEASUREMENT) && (value < ErrorCodes.ERROR_CODES))
            {
                calculateEws(measurement, thresholds, value);
            }
            else
            {
                // Might be leads off measurement - will never have data for that minute so do not make one
                //Log.e(TAG, vital_sign_type + " removing the EWS minute for this measurement as will NEVER have all the data as its invalid/leads off @ " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms));

                removeThisMeasurementsEarlyWarningScoreMinute(measurement);
            }

            checkIfCanProcessOrDeleteExistingMinutes();
        }
    }


    public void setExpectedMeasurementsForMinute(ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements, long timestamp_in_ms)
    {
        ArrayList<VitalSignType> expected_measurements = getVitalSignTypesToIncludeInEarlyWarningScore(tracked_measurements);

        // find minute and set list.
        try
        {
            boolean list_added = false;

            for(EarlyWarningScoreMinute minute : early_warning_score_minutes.values())
            {
                if (!list_added)
                {
                    if ((timestamp_in_ms <= minute.end_timestamp) & (timestamp_in_ms > minute.start_timestamp))
                    {

                        minute.setExpectedMeasurements(expected_measurements);

                        list_added = true;
                    }
                }
            }

            if(!list_added)    // Didn't go into any of the existing minutes, so make a new one
            {
                EarlyWarningScoreMinute minute = new EarlyWarningScoreMinute(timestamp_in_ms);

                minute.setExpectedMeasurements(expected_measurements);

                early_warning_score_minutes.put(minute.start_timestamp, minute);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "exception occurred in EWS processing:");
            Log.e(TAG, e.toString());

            e.printStackTrace();
        }

        checkIfCanProcessOrDeleteExistingMinutes();
    }

    private void calculateEws(MeasurementVitalSign measurement, ArrayList<ThresholdSetLevel> thresholds, double value)
    {
        int early_warning_score = 0;
        int max_possible = 0;
        int special_alert_level = 0;

        ThresholdSetLevel bottom_threshold = thresholds.get(0);
        ThresholdSetLevel top_threshold = thresholds.get(thresholds.size() - 1);

        // Check if we're above top of scale or below bottom. If so, use top/bottom ews value
        if(value < bottom_threshold.bottom)
        {
            early_warning_score = bottom_threshold.early_warning_score;
        }
        else if(value >= top_threshold.top)
        {
            early_warning_score = top_threshold.early_warning_score;
        }

        // Now go through all the thresholds to check if hr is within them AND to get max possible value
        for(ThresholdSetLevel threshold : thresholds)
        {
            // Check for positive EWS. For PEWS, Consciousness Level and Family Or Nurse Concern are not included, so their EWS values are negative. This allows us to still have graph colours
            // but not include them in the EWS calculation
            if (threshold.early_warning_score > 0)
            {
                if((value >= threshold.bottom) & (value < threshold.top))
                {
                    early_warning_score = threshold.early_warning_score;
                }

                if(threshold.early_warning_score > max_possible)
                {
                    max_possible = threshold.early_warning_score;
                }
            }
        }

        if (early_warning_score < 0)
        {
            // Special case for vital signs that do not contribute towards the EWS, but have negative thresholds so the UI graph has colour bands to show
            early_warning_score = 0;
        }

        if (max_possible < 0)
        {
            // Special case for vital signs that do not contribute towards the EWS, but have negative thresholds so the UI graph has colour bands to show
            max_possible = 0;
        }

        // If any one NEWS parameter == 3, then the overall EWS must be at least orange, which is 5 - 6.
        if(("NEWS".equals(ews_set_name)) ||
                ("NEWS2".equals(ews_set_name)))
        {
            if(early_warning_score > 2)
            {
                special_alert_level = 5;
            }
        }

        insertEarlyWarningScoresIntoMinutes(measurement, early_warning_score, max_possible, special_alert_level);
    }


    private void removeThisMeasurementsEarlyWarningScoreMinute(MeasurementVitalSign measurement)
    {
        for(EarlyWarningScoreMinute minute : early_warning_score_minutes.values())
        {
            if((measurement.timestamp_in_ms <= minute.end_timestamp) & (measurement.timestamp_in_ms > minute.start_timestamp))
            {
                Log.d(TAG, "removeThisMeasurementsEarlyWarningScoreMinute " + Utils.padVitalSignName(measurement.getType()) + " : " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(minute.start_timestamp) + " to " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(minute.end_timestamp));

                early_warning_score_minutes.remove(minute.start_timestamp);
                return;
            }
        }
    }


    private void insertEarlyWarningScoresIntoMinutes(MeasurementVitalSign measurement, int early_warning_score, int max_possible, int special_alert_level)
    {
        String log_line = "insertEarlyWarningScoresIntoMinutes : " + Utils.padVitalSignName(measurement.getType()) + " : measurement_validity_time_in_seconds " + measurement.measurement_validity_time_in_seconds + " : EWS = " + early_warning_score + "/" + max_possible + " : Value = " + measurement.getPrimaryMeasurement() + " @ " + TimestampConversion.convertDateToHumanReadableStringHoursMinutesSeconds(measurement.timestamp_in_ms) + " : Num Minutes = " + early_warning_score_minutes.values().size();

        try
        {
            boolean ews_added = false;

            for(EarlyWarningScoreMinute minute : early_warning_score_minutes.values())
            {
                if (!ews_added)
                {
                    if ((measurement.timestamp_in_ms <= minute.end_timestamp) & (measurement.timestamp_in_ms > minute.start_timestamp))
                    {
                        minute.addEarlyWarningScore(measurement.getType(), early_warning_score, max_possible);

                        log_line = log_line.concat(" : Updating existing minute : " + minute);

                        if (special_alert_level > minute.special_alert_level)
                        {
                            minute.special_alert_level = special_alert_level;
                        }

                        ews_added = true;
                    }
                }
            }

            if(!ews_added)    // Didn't go into any of the existing minutes, so make a new one
            {
                EarlyWarningScoreMinute minute = new EarlyWarningScoreMinute(measurement.timestamp_in_ms);

                minute.addEarlyWarningScore(measurement.getType(), early_warning_score, max_possible);

                log_line = log_line.concat(" : Adding new minute");

                minute.special_alert_level = special_alert_level;

                early_warning_score_minutes.put(minute.start_timestamp, minute);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "exception occurred in EWS processing:");
            Log.e(TAG, e.toString());

            e.printStackTrace();
        }

        Log.d(TAG, log_line);
    }


    private void checkIfCanProcessOrDeleteExistingMinutes()
    {
        // Now check if we can process or delete any of the existing minutes.

        long thirty_six_hours_ago = PatientGatewayService.getNtpTimeNowInMillisecondsStatic() - (int) (1.5 * DateUtils.DAY_IN_MILLIS);

        try
        {
            for (EarlyWarningScoreMinute minute_to_process : early_warning_score_minutes.values())
            {
                if(minute_to_process.calculateMinute())
                {
                    boolean is_alert_level = false;
                    int total_ews = minute_to_process.total_ews;

                    if(minute_to_process.special_alert_level > 0)
                    {
                        is_alert_level = true;

                        if(minute_to_process.special_alert_level > total_ews)
                        {
                            total_ews = minute_to_process.special_alert_level;
                        }
                    }

                    reportEarlyWarningScore(total_ews, is_alert_level, minute_to_process.max_possible_ews, minute_to_process.end_timestamp);

                    early_warning_score_minutes.remove(minute_to_process.start_timestamp);
                }
                else if(minute_to_process.end_timestamp < thirty_six_hours_ago)
                {
                    // Give up on ever processing it, so delete it.
                    early_warning_score_minutes.remove(minute_to_process.start_timestamp);
                }
                /*
                else
                {
                    // Wait up to 24 hours to receive any data from out of range devices
                }
                */
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "exception occurred in EWS processing:");
            Log.e(TAG, e.toString());

            e.printStackTrace();
        }
    }


    @NonNull
    private ArrayList<VitalSignType> getVitalSignTypesToIncludeInEarlyWarningScore(ConcurrentMap<VitalSignType, TrackedMeasurement> active_tracked_measurements)
    {
        ArrayList<VitalSignType> vital_sign_sessions_in_progress = new ArrayList<>();

        Log.d(TAG, "getVitalSignTypesToIncludeInEarlyWarningScore : active_tracked_measurements : " + Utils.dumpTrackedMeasurementVitalSignNames(active_tracked_measurements));

        // Get a list of Vital Signs to include in the EWS by checking active_tracked_measurements in
        // VitalSignType order
        for (VitalSignType vital_sign_type : VitalSignType.values())
        {
            // Ignore EWS - can't include EWS in its own EWS measurement.
            if (vital_sign_type != VitalSignType.EARLY_WARNING_SCORE)
            {
                // if there's data of that type being tracked OR a device session of the same type is running...
                if ((active_tracked_measurements.containsKey(vital_sign_type)) || (device_info_manager.isDeviceSessionInProgress(vital_sign_type)))
                {
                    // Now check if vitals type is included in the EWS - some vitals e.g. weight, and temperature for PEWS
                    // are not part of EWS and have a single blue threshold colour.
                    // These do not need to be processed for the EWS score as they will always add 0/0
                    if(isVitalPartOfEwsScheme(vital_sign_type))
                    {
                        // Now check for a device session or manually entered vital.
                        // A Manual Vital Sign device session can be in progress at the same time as a Sensor device. The Manual Vital Sign needs to be ignored.
                        // However, the Sensor device session can be closed but with the Long Term Measurement Validity Tracker reporting a Vital Sign.
                        // E.g. Blood Pressure was in the session, took a reading that is valid for an hour, but was disconnected 5 minutes later. The Sensor reading is still valid for 55 minutes
                        // Need to prevent the Manual BP from being used in the EWS for these 55 minutes

                        boolean add_to_vital_signs_sessions_in_progress = addToExpectedVitalSigns(vital_sign_type, active_tracked_measurements);

                        if (add_to_vital_signs_sessions_in_progress) {
                            vital_sign_sessions_in_progress.add(vital_sign_type);
                        }
                    }
                }
            }
        }

        return vital_sign_sessions_in_progress;
    }

    private boolean addToExpectedVitalSigns(VitalSignType vital_sign_type, ConcurrentMap<VitalSignType, TrackedMeasurement> active_tracked_measurements)
    {
        boolean add_to_vital_signs_sessions_in_progress = false;

        if(vital_sign_type.isManualVital())
        {
            VitalSignType equivalent_sensor_vital_type = vital_sign_type.getEquivalentSensorVitalSignType();
            // Include if:
            // No equivalent sensor vital exists
            // OR no equivalent session running AND EITHER
                // No equivalent senor vital currently tracked
                // OR sensor vital invalid
            if((equivalent_sensor_vital_type == VitalSignType.NOT_SET_YET)
                || ((device_info_manager.isDeviceSessionInProgress(equivalent_sensor_vital_type) == false)
                    &&((active_tracked_measurements.containsKey(equivalent_sensor_vital_type) == false)
                        ||(active_tracked_measurements.get(equivalent_sensor_vital_type).valid == false)
                    )
                )
            )
            {
                // No equivalent type found - treat it like a normal vital
                add_to_vital_signs_sessions_in_progress = true;
            }

        }
        else
        {
            VitalSignType equivalent_manual_vital_type = vital_sign_type.getEquivalentManualVitalSignType();
            // Include IF
            // Relevant device session currently running
            // OR current measurement is still valid
            // OR no equivalent Manual vital <- this is because we're still tracking the vital,
            // so we expect it at some point - either as a manual vital, or via a new device session.
            // Can't just ignore it and calculate the EWS without it.
            if((device_info_manager.isDeviceSessionInProgress(vital_sign_type)) // <= do this one first, as if it's false we know active_tracked_measurements contains vital_sign_type, which avoids an NPE
                    || (active_tracked_measurements.get(vital_sign_type).valid == true)
                    || (active_tracked_measurements.containsKey(equivalent_manual_vital_type) == false))
            {
                add_to_vital_signs_sessions_in_progress = true;
            }
        }

        return add_to_vital_signs_sessions_in_progress;
    }


    private boolean isVitalPartOfEwsScheme(VitalSignType vital_type)
    {
        MeasurementTypes measurement_type = MeasurementTypes.getMeasurementTypeFromVitalSignType(vital_type);
        ArrayList<ThresholdSetLevel> thresholds = new ArrayList<>();

        if(measurement_type != MeasurementTypes.UNKNOWN)
        {
            if (cached_ews_thresholds_list.size() > measurement_type.getValue())
            {
                thresholds = cached_ews_thresholds_list.get(measurement_type.getValue());
            }
            else
            {
                Log.e(TAG, "isVitalPartOfEwsScheme: Threshold set not available for measurement " + measurement_type);
            }
        }

        // Expect all blue thresholds with 0/0 EWS for any vital sign not included in EWS.
        // These are encoded as early_warning_score = -1 in the threshold set.
        // Size will be zero if the vital sign wasn't found at all.
        if(thresholds.size() > 0)
        {
            int total = 0;
            for(ThresholdSetLevel level : thresholds)
            {
                total += level.early_warning_score;
            }

            // If all EWS values are < 0, total here will be < 0 so we don't include the vital sign
            // Therefore if total >= 0, this means we have valid EWS values so return true.
            return (total >= 0);
        }
        else
        {
            // threshold set is empty
            return false;
        }
    }


    private void reportEarlyWarningScore(int early_warning_score, boolean is_special_alert, int max_possible, long timestamp)
    {
        Intent outgoing_intent = intent_factory.getNewIntent(Algorithms.INTENT__INCOMING_FROM_ALGORITHMS);

        outgoing_intent.putExtra("measurement_type", AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal());
        
        outgoing_intent.putExtra("early_warning_score", early_warning_score);
        outgoing_intent.putExtra("is_special_alert", is_special_alert);
        outgoing_intent.putExtra("max_possible", max_possible);
        outgoing_intent.putExtra("timestamp", timestamp);

        context_interface.sendBroadcastIntent(outgoing_intent);
    }
}
