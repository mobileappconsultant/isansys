package com.isansys.patientgateway.algorithms;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import com.isansys.common.measurements.MeasurementHeartRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredHeartRate;
import com.isansys.common.measurements.MeasurementManuallyEnteredRespirationRate;
import com.isansys.common.measurements.MeasurementRespirationRate;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.common.ThresholdSetLevel;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.PatientGatewayService;
import com.isansys.patientgateway.Settings;
import com.isansys.patientgateway.SystemCommands;
import com.isansys.patientgateway.TrackedMeasurement;
import com.isansys.patientgateway.deviceInfo.DeviceInfoManager;
import com.isansys.patientgateway.factories.IntentFactory;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rory on 07/11/2017.
 */

public class EarlyWarningScoreProcessorTest
{
    @Mock
    Context mMockContext;

    @Mock
    ContextInterface dummy_context_interface;

    @Mock
    Settings dummy_settings;

    @Mock
    RemoteLogging dummy_logger;

    @Mock
    IntentFactory dummy_factory;

    @Mock
    Intent dummy_intent;

    @Mock
    DeviceInfoManager device_info_manager;

    @Mock
    SystemCommands dummy_commands;

    @Captor
    ArgumentCaptor<String> string_captor;

    @Captor
    ArgumentCaptor<Integer> int_captor;

    @Captor
    ArgumentCaptor<Long> long_captor;

    @Captor
    ArgumentCaptor<Boolean> boolean_captor;

    @Captor
    ArgumentCaptor<MeasurementSpO2> measurement_captor;


    EarlyWarningScoreProcessor processor;
    static ArrayList<ArrayList<ThresholdSetLevel>> threshold_set_levels;

    @BeforeClass
    public static void createThresholds()
    {
        threshold_set_levels = new ArrayList<>();

        // UNKNOWN - empty list
        threshold_set_levels.add(new ArrayList<ThresholdSetLevel>());

        String dummy_display_text = "DummyDisplayText";
        String dummy_information_text = "DummyInformationText";

        ArrayList<ThresholdSetLevel> hr_values = new ArrayList<>();
        hr_values. add(new ThresholdSetLevel(
                (float) 10.0,
                (float) 0.0,
                3,
                VitalSignType.HEART_RATE.ordinal(),
                "Heart Rate",
                dummy_display_text,
                dummy_information_text
        ));

        hr_values. add(new ThresholdSetLevel(
                (float) 20.0,
                (float) 10.1,
                2,
                VitalSignType.HEART_RATE.ordinal(),
                "Heart Rate",
                dummy_display_text,
                dummy_information_text
        ));




        threshold_set_levels.add(hr_values);

        ArrayList<ThresholdSetLevel> rr_values = new ArrayList<>();
        rr_values. add(new ThresholdSetLevel(
                (float) 10.0,
                (float) 0.0,
                3,
                VitalSignType.RESPIRATION_RATE.ordinal(),
                "Respiration Rate",
                dummy_display_text,
                dummy_information_text
        ));

        rr_values. add(new ThresholdSetLevel(
                (float) 20.0,
                (float) 10.1,
                2,
                VitalSignType.RESPIRATION_RATE.ordinal(),
                "Respiration Rate",
                dummy_display_text,
                dummy_information_text
        ));

        threshold_set_levels.add(rr_values);
    }


    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        when(dummy_context_interface.getAppContext()).thenReturn(mMockContext);

        when(dummy_settings.getDisableCommentsForSpeed()).thenReturn(false);

        when(dummy_factory.getNewIntent(anyString())).thenReturn(dummy_intent);

        processor = new EarlyWarningScoreProcessor(dummy_context_interface, dummy_logger, dummy_factory, device_info_manager);

        // Ignore logging
        doNothing().when(dummy_logger).d(anyString(), anyString());

        // Set the device info manager to report no device sessions
        when(device_info_manager.isDeviceSessionInProgress(any(VitalSignType.class))).thenReturn(false);

        // Ensure the processor is in a clean state - no minutes pending
        processor.reset();

        // Use the dummy thresholds created in createThresholds at the top.
        processor.cacheSelectedEarlyWarningScores(threshold_set_levels, "Test Thresholds");

        processor.enableProcessing(true);
    }


    /**
     * Check that an EWS is created from known HR and RR values.
     * Lifetouch device session running (spoofed)
     * Require only HR and RR, and supply one of each in the lowest threshold bracket.
     *
     * Should get a known EWS created.
     */
    @Test
    public void lifetouchOnly_lowValuesHighEws()
    {
        /* ARRANGE */
        // Set the device info manager to report a Lifetouch device session
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.HEART_RATE)).thenReturn(true);
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.RESPIRATION_RATE)).thenReturn(true);

        // Create an empty tracked measurements Map, as we're not testing tracked measurements in this test
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Insert HR and RR measurements with values of 1 - these should have a score of 3 each...
        // Insert into tracked measurements - all measurements are reported from the ValidityTracker so they should always be in there.
        tracked_measurements.put(VitalSignType.HEART_RATE, new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));

        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);

        /* ASSERT */
        // Check intent was called with correct extras
        verify(dummy_intent, times(3)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (long) long_captor.capture());

        List<Integer> captured_ints = int_captor.getAllValues();

        // Check values were as expected.
        // Should have had an algorithm type of EWS...
        int algorithm_type_value = captured_ints.get(0);
        Assert.assertEquals(Algorithms.AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal(), algorithm_type_value);

        // ... an EWS value of 6...
        int score = captured_ints.get(1);
        Assert.assertEquals(6, score);

        // ... out of a maximum possible value of 6...
        int max_possible = captured_ints.get(2);
        Assert.assertEquals(6, max_possible);

        // ... and NOT a special alert, as that only applies to NEWS - we've called our dummy thresholds
        // "Test Thresholds" so it doesn't apply.
        boolean is_special_alert = boolean_captor.getValue();
        Assert.assertEquals(false, is_special_alert);

        // Round timestamp down to beginning of minute, then add a minute to get end of minute timestamp
        // Effectively, we're rounding up to a whole number of minutes.
        long expected_timestamp = timestamp / DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp *= DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp += DateUtils.MINUTE_IN_MILLIS;

        // Finally, check the timestamp matches the expected value.
        long returned_timestamp = long_captor.getValue();
        Assert.assertEquals(expected_timestamp, returned_timestamp);
    }


    /**
     * Check that an EWS is ONLY created if the expected values are added..
     * Lifetouch device session running (spoofed)
     * Require only HR and RR, and supply one of each in the lowest threshold bracket. Do this for
     * minutes 1 and 3, but add HR but no RR for minute 2.
     *
     * Should get EWS for 1 and 3, but not 2.
     */
    @Test
    public void lifetouchOnly_missingRespirationRate()
    {
        /* ARRANGE */
        // Set the device info manager to report only a Lifetouch device session
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.HEART_RATE)).thenReturn(true);
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.RESPIRATION_RATE)).thenReturn(true);

        // Create an empty tracked measurements Map, as we're not testing tracked measurements in this test
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW minus 2 minutes
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic() - 2*DateUtils.MINUTE_IN_MILLIS;


        /* ACT */
        // Insert HR and RR measurements with values of 1 - these should have a score of 3 each...
        // First minute
        // Insert into tracked measurements - all measurements are reported from the ValidityTracker so they should always be in there.
        tracked_measurements.put(VitalSignType.HEART_RATE, new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));

        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);


        // Second minute - missing RR data
        tracked_measurements.put(VitalSignType.HEART_RATE, new TrackedMeasurement(new MeasurementHeartRate(1, timestamp + DateUtils.MINUTE_IN_MILLIS), DateUtils.MINUTE_IN_MILLIS));
        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp + DateUtils.MINUTE_IN_MILLIS);

        // third minute
        tracked_measurements.put(VitalSignType.HEART_RATE, new TrackedMeasurement(new MeasurementHeartRate(1, timestamp + 2*DateUtils.MINUTE_IN_MILLIS), DateUtils.MINUTE_IN_MILLIS));
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp + 2*DateUtils.MINUTE_IN_MILLIS), DateUtils.MINUTE_IN_MILLIS));

        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp + 2*DateUtils.MINUTE_IN_MILLIS);


        /* ASSERT */
        // Check intent was called with correct extras
        // Should have been called twice only...
        verify(dummy_intent, times(6)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (long) long_captor.capture());

        List<Integer> captured_ints = int_captor.getAllValues();
        List<Boolean> captured_booleans = boolean_captor.getAllValues();
        List<Long> captured_longs = long_captor.getAllValues();


        // Check values were as expected - first invocation
        int algorithm_type_value = captured_ints.get(0);
        Assert.assertEquals(Algorithms.AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal(), algorithm_type_value);

        // ... an EWS value of 6...
        int score = captured_ints.get(1);
        Assert.assertEquals(6, score);

        // ... out of a maximum possible value of 6...
        int max_possible = captured_ints.get(2);
        Assert.assertEquals(6, max_possible);

        // ... and NOT a special alert, as that only applies to NEWS - we've called our dummy thresholds
        // "Test Thresholds" so it doesn't apply.
        boolean is_special_alert = captured_booleans.get(0);
        Assert.assertEquals(false, is_special_alert);

        // Round timestamp down to beginning of minute, then add a minute to get end of minute timestamp
        // Effectively, we're rounding up to a whole number of minutes.
        long expected_timestamp = timestamp / DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp *= DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp += DateUtils.MINUTE_IN_MILLIS;

        // Finally, check the timestamp matches the expected value.
        long returned_timestamp = captured_longs.get(0);
        Assert.assertEquals(expected_timestamp, returned_timestamp);


        // Check values were as expected - second invocation, as the first
        algorithm_type_value = captured_ints.get(3);
        Assert.assertEquals(Algorithms.AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal(), algorithm_type_value);

        score = captured_ints.get(4);
        Assert.assertEquals(6, score);

        max_possible = captured_ints.get(5);
        Assert.assertEquals(6, max_possible);

        is_special_alert = captured_booleans.get(1);
        Assert.assertEquals(false, is_special_alert);

        // Only difference to first invocation is the second EWS timestamp should be two minutes after the first.
        expected_timestamp += 2*DateUtils.MINUTE_IN_MILLIS;

        returned_timestamp = captured_longs.get(1);
        Assert.assertEquals(expected_timestamp, returned_timestamp);
    }


    /**
     * Check EWS works with Manually Entered vitals only...
     * Only manually entered device sessions running.
     * Supply manual HR and RR.
     *
     * Should get a known EWS value
     */
    @Test
    public void manualVitals_checkMeasurementsAreProcessed()
    {
        /* ARRANGE */
        // Set the device info manager to report only manual HR and RR device sessions
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.MANUALLY_ENTERED_HEART_RATE)).thenReturn(true);
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE)).thenReturn(true);

        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurements for the manual vitals
        tracked_measurements.put(VitalSignType.MANUALLY_ENTERED_HEART_RATE, new TrackedMeasurement(new MeasurementManuallyEnteredHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));
        tracked_measurements.put(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE, new TrackedMeasurement(new MeasurementManuallyEnteredRespirationRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));

        // Insert the measurements into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.MANUALLY_ENTERED_HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);


        /* ASSERT */
        // Check intent was called with correct extras
        verify(dummy_intent, times(3)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (long) long_captor.capture());

        List<Integer> captured_ints = int_captor.getAllValues();

        // Check values were as expected.
        // Should have had an algorithm type of EWS...
        int algorithm_type_value = captured_ints.get(0);
        Assert.assertEquals(Algorithms.AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal(), algorithm_type_value);

        // ... an EWS value of 6...
        int score = captured_ints.get(1);
        Assert.assertEquals(6, score);

        // ... out of a maximum possible value of 6...
        int max_possible = captured_ints.get(2);
        Assert.assertEquals(6, max_possible);

        // ... and NOT a special alert, as that only applies to NEWS - we've called our dummy thresholds
        // "Test Thresholds" so it doesn't apply.
        boolean is_special_alert = boolean_captor.getValue();
        Assert.assertEquals(false, is_special_alert);

        // Round timestamp down to beginning of minute, then add a minute to get end of minute timestamp
        // Effectively, we're rounding up to a whole number of minutes.
        long expected_timestamp = timestamp / DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp *= DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp += DateUtils.MINUTE_IN_MILLIS;

        // Finally, check the timestamp matches the expected value.
        long returned_timestamp = long_captor.getValue();
        Assert.assertEquals(expected_timestamp, returned_timestamp);
    }


    /**
     * Check EWS processor ignores manual vitals if a corresponding device session is present...
     * Lifetouch and manual RR device sessions running (spoofed)
     * Supply manual HR, real RR. Then supply real HR.
     *
     * Should only get EWS after the real HR has been added.
     */
    @Test
    public void manualVitals_checkMeasurementsIgnoredIfDevicePresent()
    {
        /* ARRANGE */
        // Set the device info manager to report only a Lifetouch device session
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.HEART_RATE)).thenReturn(true);
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.RESPIRATION_RATE)).thenReturn(true);
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.MANUALLY_ENTERED_HEART_RATE)).thenReturn(true);

        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurements for the manual vitals
        // Manual HR value is 11 so it hits the second threshold with EWS of 2.
        tracked_measurements.put(VitalSignType.MANUALLY_ENTERED_HEART_RATE, new TrackedMeasurement(new MeasurementManuallyEnteredHeartRate(11, timestamp), DateUtils.MINUTE_IN_MILLIS));
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));

        // Insert the measurements into the ews processor...
        // Manual HR, real RR...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.MANUALLY_ENTERED_HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);

        /* ASSERT */
        // Check intent was NOT called (yet)...
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());


        /* ACT part 2... */
        // Add a real HR
        tracked_measurements.put(VitalSignType.HEART_RATE, new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));
        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);

        /* ASSERT part 2... */
        // Check intent was called with correct extras
        verify(dummy_intent, times(3)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (long) long_captor.capture());

        List<Integer> captured_ints = int_captor.getAllValues();

        // Check values were as expected.
        // Should have had an algorithm type of EWS...
        int algorithm_type_value = captured_ints.get(0);
        Assert.assertEquals(Algorithms.AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal(), algorithm_type_value);

        // ... an EWS value of 6 - specifically NOT 9, so the manual vital has been ignored...
        int score = captured_ints.get(1);
        Assert.assertEquals(6, score);

        // ... out of a maximum possible value of 6...
        int max_possible = captured_ints.get(2);
        Assert.assertEquals(6, max_possible);

        // ... and NOT a special alert, as that only applies to NEWS - we've called our dummy thresholds
        // "Test Thresholds" so it doesn't apply.
        boolean is_special_alert = boolean_captor.getValue();
        Assert.assertEquals(false, is_special_alert);

        // Round timestamp down to beginning of minute, then add a minute to get end of minute timestamp
        // Effectively, we're rounding up to a whole number of minutes.
        long expected_timestamp = timestamp / DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp *= DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp += DateUtils.MINUTE_IN_MILLIS;

        // Finally, check the timestamp matches the expected value.
        long returned_timestamp = long_captor.getValue();
        Assert.assertEquals(expected_timestamp, returned_timestamp);
    }


    /**
     * Check that Valid Tracked Device Measurements give EWS scores even if NO device session in progress
     * No device session running - device has been added and removed.
     * Supply device (tracked) HR and RR.
     *
     * Should get a known EWS value
     */
    @Test
    public void noDeviceSession_trackedMeasurementsAreProcessed()
    {
        /* ARRANGE */
        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurements for the manual vitals
        tracked_measurements.put(VitalSignType.HEART_RATE, new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));

        // Insert the measurements into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);


        /* ASSERT */
        // Check intent was called with correct extras
        verify(dummy_intent, times(3)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (long) long_captor.capture());

        List<Integer> captured_ints = int_captor.getAllValues();

        // Check values were as expected.
        // Should have had an algorithm type of EWS...
        int algorithm_type_value = captured_ints.get(0);
        Assert.assertEquals(Algorithms.AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal(), algorithm_type_value);

        // ... an EWS value of 6...
        int score = captured_ints.get(1);
        Assert.assertEquals(6, score);

        // ... out of a maximum possible value of 6...
        int max_possible = captured_ints.get(2);
        Assert.assertEquals(6, max_possible);

        // ... and NOT a special alert, as that only applies to NEWS - we've called our dummy thresholds
        // "Test Thresholds" so it doesn't apply.
        boolean is_special_alert = boolean_captor.getValue();
        Assert.assertEquals(false, is_special_alert);

        // Round timestamp down to beginning of minute, then add a minute to get end of minute timestamp
        // Effectively, we're rounding up to a whole number of minutes.
        long expected_timestamp = timestamp / DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp *= DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp += DateUtils.MINUTE_IN_MILLIS;

        // Finally, check the timestamp matches the expected value.
        long returned_timestamp = long_captor.getValue();
        Assert.assertEquals(expected_timestamp, returned_timestamp);
    }


    /**
     * Check that Valid Tracked Device Measurements give EWS scores and that Manual Vital Signs are ignored
     * No device session running - only manual vitals session.
     * Supply manual HR, real RR. Then supply real HR. All three must be in the tracker initially though.
     *
     * Should only get EWS after the real HR has been added.
     */
    @Test
    public void trackedAndManualVitals_ManualMeasurementsAreIgnored()
    {
        /* ARRANGE */
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.MANUALLY_ENTERED_HEART_RATE)).thenReturn(true);

        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurements for the manual vitals
        // Manual HR value is 11 so it hits the second threshold with EWS of 2.
        tracked_measurements.put(VitalSignType.MANUALLY_ENTERED_HEART_RATE, new TrackedMeasurement(new MeasurementManuallyEnteredHeartRate(11, timestamp), DateUtils.MINUTE_IN_MILLIS));
        tracked_measurements.put(VitalSignType.HEART_RATE, new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS));

        // Insert the measurements into the ews processor...
        // Manual HR, real RR...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.MANUALLY_ENTERED_HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);

        /* ASSERT */
        // Check intent was NOT called (yet)...
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());


        /* ACT part 2... */
        // Add a real HR
        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);

        /* ASSERT part 2... */
        // Check intent was called with correct extras
        verify(dummy_intent, times(3)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (long) long_captor.capture());

        List<Integer> captured_ints = int_captor.getAllValues();

        // Check values were as expected.
        // Should have had an algorithm type of EWS...
        int algorithm_type_value = captured_ints.get(0);
        Assert.assertEquals(Algorithms.AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal(), algorithm_type_value);

        // ... an EWS value of 6 - specifically NOT 9, so the manual vital has been ignored...
        int score = captured_ints.get(1);
        Assert.assertEquals(6, score);

        // ... out of a maximum possible value of 6...
        int max_possible = captured_ints.get(2);
        Assert.assertEquals(6, max_possible);

        // ... and NOT a special alert, as that only applies to NEWS - we've called our dummy thresholds
        // "Test Thresholds" so it doesn't apply.
        boolean is_special_alert = boolean_captor.getValue();
        Assert.assertEquals(false, is_special_alert);

        // Round timestamp down to beginning of minute, then add a minute to get end of minute timestamp
        // Effectively, we're rounding up to a whole number of minutes.
        long expected_timestamp = timestamp / DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp *= DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp += DateUtils.MINUTE_IN_MILLIS;

        // Finally, check the timestamp matches the expected value.
        long returned_timestamp = long_captor.getValue();
        Assert.assertEquals(expected_timestamp, returned_timestamp);
    }


    /**
     * Check no EWS with a timed out vital - Manually Entered vitals only...
     * No device session running - only manual vitals
     * Supply manual HR and RR, with RR timed out.
     *
     * Should NOT get an EWS value at all
     */
    @Test
    public void manualVitals_checkTimedOutMeasurementResultsInNoEws()
    {
        /* ARRANGE */
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.MANUALLY_ENTERED_HEART_RATE)).thenReturn(true);
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE)).thenReturn(true);

        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurement for the manual vitals
        TrackedMeasurement valid_hr = new TrackedMeasurement(new MeasurementManuallyEnteredHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS);
        TrackedMeasurement invalid_rr = new TrackedMeasurement(new MeasurementManuallyEnteredRespirationRate(1, timestamp - DateUtils.MINUTE_IN_MILLIS), DateUtils.MINUTE_IN_MILLIS);
        invalid_rr.valid = false;

        tracked_measurements.put(VitalSignType.MANUALLY_ENTERED_HEART_RATE, valid_hr);
        tracked_measurements.put(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE, invalid_rr);

        // Insert the measurement into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.MANUALLY_ENTERED_HEART_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);


        /* ASSERT */
        // Check intent was called with correct extras
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());


        /* ACT Part 2 */
        // Insert the invalid measurement into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement);

        /* ASSERT Part 2 */
        // Check intent was called with correct extras
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());
    }


    /**
     * Check no EWS with a timed out vital - Tracked vitals
     * No device session running - was added and then removed...
     * Supply tracked HR and RR, with RR timed out.
     *
     * Should NOT get an EWS value at all
     */
    @Test
    public void trackedVitals_checkTimedOutMeasurementResultsInNoEws()
    {
        /* ARRANGE */
        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurements for the manual vitals
        TrackedMeasurement valid_hr = new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS);
        TrackedMeasurement invalid_rr = new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp - DateUtils.MINUTE_IN_MILLIS), DateUtils.MINUTE_IN_MILLIS);
        invalid_rr.valid = false;

        tracked_measurements.put(VitalSignType.HEART_RATE, valid_hr);
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, invalid_rr);

        // Insert the measurement into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);

        /* ASSERT */
        // Check intent was called with correct extras
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());

        /* ACT Part 2 */
        // Insert the invalid measurement into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);

        /* ASSERT Part 2 */
        // Check intent was called with correct extras
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());
    }


    /**
     * Check no EWS with a timed out vital - Tracked vitals
     * Lifetouych device session running
     * Supply tracked HR and RR, with RR timed out.
     *
     * Should NOT get an EWS value at all
     */
    @Test
    public void trackedVitals_checkTimedOutMeasurementResultsInNoEwsEvenWithDeviceSession()
    {
        /* ARRANGE */
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.HEART_RATE)).thenReturn(true);
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.RESPIRATION_RATE)).thenReturn(true);

        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurements for the manual vitals
        TrackedMeasurement valid_hr = new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS);
        TrackedMeasurement invalid_rr = new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp - DateUtils.MINUTE_IN_MILLIS), DateUtils.MINUTE_IN_MILLIS);
        invalid_rr.valid = false;

        tracked_measurements.put(VitalSignType.HEART_RATE, valid_hr);
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, invalid_rr);

        // Insert the measurement into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);

        /* ASSERT */
        // Check intent was called with correct extras
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());

        /* ACT Part 2 */
        // Insert the invalid measurement into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);

        /* ASSERT Part 2 */
        // Check intent was called with correct extras
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());
    }


    /**
     * Check no EWS with a timed out vital - Tracked vitals and Manual Vitals
     * Lifetouch session running (spoofed).
     * Supply tracked HR and RR, with RR timed out, AND manual RR.
     *
     * Should get NO EWS value.
     */
    @Test
    public void trackedAndManualVitals_checkTimedOutTrackedMeasurementResultsInNoEwsIfSessionInProgress()
    {
        /* ARRANGE */
        // Set the device info manager to report only a Lifetouch device session
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.HEART_RATE)).thenReturn(true);
        when(device_info_manager.isDeviceSessionInProgress(VitalSignType.RESPIRATION_RATE)).thenReturn(true);

        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurements for the vitals
        TrackedMeasurement valid_hr = new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS);
        TrackedMeasurement invalid_rr = new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp - DateUtils.MINUTE_IN_MILLIS), DateUtils.MINUTE_IN_MILLIS);
        invalid_rr.valid = false;
        TrackedMeasurement manual_rr = new TrackedMeasurement(new MeasurementManuallyEnteredRespirationRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS);

        tracked_measurements.put(VitalSignType.HEART_RATE, valid_hr);
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, invalid_rr);
        tracked_measurements.put(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE, manual_rr);


        // Insert the measurement into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);


        /* ASSERT */
        // Check intent was called with correct extras
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());
    }


    /**
     * Check EWS with a timed out vital - Tracked vitals and Manual Vitals
     * No device session running.
     * Supply tracked HR and RR, with RR timed out, AND manual RR.
     *
     * Should get an EWS value - no session so manual vital used instead of timed out device measurement.
     */
    @Test
    public void trackedAndManualVitals_checkTimedOutTrackedMeasurementResultsInEwsIfNoSessionInProgress()
    {
        /* ARRANGE */
        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurements for the manual vitals
        TrackedMeasurement valid_hr = new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS);
        TrackedMeasurement invalid_rr = new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp - DateUtils.MINUTE_IN_MILLIS), DateUtils.MINUTE_IN_MILLIS);
        invalid_rr.valid = false;
        // Manual RR value is 11 so it hits the second threshold with EWS of 2.
        TrackedMeasurement manual_rr = new TrackedMeasurement(new MeasurementManuallyEnteredRespirationRate(11, timestamp), DateUtils.MINUTE_IN_MILLIS);

        tracked_measurements.put(VitalSignType.HEART_RATE, valid_hr);
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, invalid_rr);
        tracked_measurements.put(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE, manual_rr);


        // Insert the measurement into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);

        /* ASSERT */
        // Check intent was called with correct extras
        verify(dummy_intent, times(3)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), (long) long_captor.capture());

        List<Integer> captured_ints = int_captor.getAllValues();

        // Check values were as expected.
        // Should have had an algorithm type of EWS...
        int algorithm_type_value = captured_ints.get(0);
        Assert.assertEquals(Algorithms.AlgorithmMeasurementType.EARLY_WARNING_SCORE.ordinal(), algorithm_type_value);

        // ... an EWS value of 5 (due to the manual RR value)...
        int score = captured_ints.get(1);
        Assert.assertEquals(5, score);

        // ... out of a maximum possible value of 6...
        int max_possible = captured_ints.get(2);
        Assert.assertEquals(6, max_possible);

        // ... and NOT a special alert, as that only applies to NEWS - we've called our dummy thresholds
        // "Test Thresholds" so it doesn't apply.
        boolean is_special_alert = boolean_captor.getValue();
        Assert.assertEquals(false, is_special_alert);

        // Round timestamp down to beginning of minute, then add a minute to get end of minute timestamp
        // Effectively, we're rounding up to a whole number of minutes.
        long expected_timestamp = timestamp / DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp *= DateUtils.MINUTE_IN_MILLIS;
        expected_timestamp += DateUtils.MINUTE_IN_MILLIS;

        // Finally, check the timestamp matches the expected value.
        long returned_timestamp = long_captor.getValue();
        Assert.assertEquals(expected_timestamp, returned_timestamp);
    }

    @Test
    public void ewsProcessorDoesNothingWhenNotEnabled()
    {
        /* ARRANGE */
        // turn off processor
        processor.enableProcessing(false);

        // Create an empty tracked measurements Map
        ConcurrentMap<VitalSignType, TrackedMeasurement> tracked_measurements = new ConcurrentHashMap<>();

        // Create a timestamp of NOW.
        long timestamp = PatientGatewayService.getNtpTimeNowInMillisecondsStatic();


        /* ACT */
        // Create TrackedMeasurements for the manual vitals
        TrackedMeasurement valid_hr = new TrackedMeasurement(new MeasurementHeartRate(1, timestamp), DateUtils.MINUTE_IN_MILLIS);
        TrackedMeasurement invalid_rr = new TrackedMeasurement(new MeasurementRespirationRate(1, timestamp - DateUtils.MINUTE_IN_MILLIS), DateUtils.MINUTE_IN_MILLIS);
        invalid_rr.valid = false;
        // Manual RR value is 11 so it hits the second threshold with EWS of 2.
        TrackedMeasurement manual_rr = new TrackedMeasurement(new MeasurementManuallyEnteredRespirationRate(11, timestamp), DateUtils.MINUTE_IN_MILLIS);

        tracked_measurements.put(VitalSignType.HEART_RATE, valid_hr);
        tracked_measurements.put(VitalSignType.RESPIRATION_RATE, invalid_rr);
        tracked_measurements.put(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE, manual_rr);


        // Insert the measurement into the ews processor...
        processor.processMeasurement(tracked_measurements.get(VitalSignType.HEART_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.RESPIRATION_RATE).measurement);
        processor.processMeasurement(tracked_measurements.get(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE).measurement);
        processor.setExpectedMeasurementsForMinute(tracked_measurements, timestamp);


        // Check intent was not called
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (boolean) boolean_captor.capture());
        verify(dummy_intent, times(0)).putExtra(string_captor.capture(), (long) long_captor.capture());
    }
}
