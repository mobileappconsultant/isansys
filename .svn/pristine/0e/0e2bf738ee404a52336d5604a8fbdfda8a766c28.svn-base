package com.isansys.patientgateway.algorithms;

import android.content.Context;
import android.content.Intent;

import com.isansys.common.enums.DeviceType;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.patientgateway.ContextInterface;
import com.isansys.patientgateway.IntermediateSpO2;
import com.isansys.patientgateway.Settings;
import com.isansys.patientgateway.SystemCommands;
import com.isansys.patientgateway.factories.IntentFactory;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.isansys.common.measurements.MeasurementVitalSign.INVALID_MEASUREMENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by Rory on 07/11/2017.
 */

public class SpO2ProcessorTest
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
    SystemCommands dummy_commands;

    @Mock
    ConcurrentLinkedQueue<IntermediateSpO2> dummy_spo2_queue;
    
    @Captor
    ArgumentCaptor<String> string_captor;

    @Captor
    ArgumentCaptor<Integer> int_captor;

    @Captor
    ArgumentCaptor<MeasurementSpO2> measurement_captor;


    IntermediateMeasurementProcessor<IntermediateSpO2> processor;


    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        when(dummy_context_interface.getAppContext()).thenReturn(mMockContext);

        when(dummy_settings.getDisableCommentsForSpeed()).thenReturn(false);

        when(dummy_settings.getMaxNumberNoninWristOxIntermediateMeasurementsInvalidBeforeMinuteMarkedInvalid()).thenReturn(55);

        when(dummy_factory.getNewIntent(anyString())).thenReturn(dummy_intent);

        processor = new IntermediateMeasurementProcessor(dummy_context_interface, dummy_settings, dummy_logger, dummy_factory, dummy_commands, dummy_spo2_queue, new IntermediateSpO2());
    }

    @Test
    public void SpO2ProcessorTest_fakeDataThreeMinutes() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.setDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX);

        // initial point before the start of the processed minute
        processor.processMeasurement(new IntermediateSpO2(99, 60, 59000));

        int val = 99;
        int pulse = 60;

        // minute that gets processed, plus extra beat after the end to trigger HR and RR measurements.
        for(int i = 0; i <= 180; i++)
        {
            long ts = 60000 + i*1000;
            processor.processMeasurement(new IntermediateSpO2(val, pulse, ts));
        }

        // Now trigger the averaging of the first(?) minute.
        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(8)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(4)).putExtra(string_captor.capture(), measurement_captor.capture());


        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<MeasurementSpO2> measurements = measurement_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();


        /* Second values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(Algorithms.AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal(), (int) integers.get(0));
        assertEquals(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX.ordinal(), (int) integers.get(1));
        assertEquals(120000, measurements.get(0).timestamp_in_ms);
        assertEquals(99, measurements.get(0).SpO2);
        assertEquals(60, measurements.get(0).pulse);


        /* Third values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(Algorithms.AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal(), (int) integers.get(2));
        assertEquals(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX.ordinal(), (int) integers.get(3));
        assertEquals(180000, measurements.get(1).timestamp_in_ms);
        assertEquals(99, measurements.get(1).SpO2);
        assertEquals(60, measurements.get(1).pulse);


        /* Fourth values calculated - the fourth minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(Algorithms.AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal(), (int) integers.get(4));
        assertEquals(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX.ordinal(), (int) integers.get(5));
        assertEquals(240000, measurements.get(2).timestamp_in_ms);
        assertEquals(99, measurements.get(2).SpO2);
        assertEquals(60, measurements.get(2).pulse);


        /* First values calculated - the first minute, calculated last when we call processAnyOutstandingData
         * should be invalid as there's only one measurement
         */
        assertEquals(Algorithms.AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal(), (int) integers.get(6));
        assertEquals(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX.ordinal(), (int) integers.get(7));
        assertEquals(60000, measurements.get(3).timestamp_in_ms);
        assertEquals(INVALID_MEASUREMENT, measurements.get(3).SpO2);
        assertEquals(INVALID_MEASUREMENT, measurements.get(3).pulse);
    }



    @Test
    public void SpO2ProcessorTest_fakeDataOneMinuteNoValidAndSomeMissing() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.setDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX);

        int val = INVALID_MEASUREMENT;
        int pulse = INVALID_MEASUREMENT;

        // 50 invalid measurements - so we're below the 55 measurement threshold
        for(int i = 0; i <= 50; i++)
        {
            long ts = i*1000;
            processor.processMeasurement(new IntermediateSpO2(val, pulse, ts));
        }

        //then 1 after the end of the minute
        processor.processMeasurement(new IntermediateSpO2(99, 60, 61000));

        // Now trigger the averaging of the first minute.
        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), measurement_captor.capture());


        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<MeasurementSpO2> measurements = measurement_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(Algorithms.AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal(), (int) integers.get(0));
        assertEquals(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX.ordinal(), (int) integers.get(1));
        assertEquals(60000, measurements.get(0).timestamp_in_ms);
        assertEquals(INVALID_MEASUREMENT, measurements.get(0).SpO2);
        assertEquals(INVALID_MEASUREMENT, measurements.get(0).pulse);
    }


    @Test
    public void SpO2ProcessorTest_fakeDataOneMinuteOneValidAndSomeMissing() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.setDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX);

        int val = INVALID_MEASUREMENT;
        int pulse = INVALID_MEASUREMENT;

        // 50 invalid measurements - so we're below the 55 measurement threshold
        for(int i = 0; i <= 50; i++)
        {
            long ts = i*1000;
            processor.processMeasurement(new IntermediateSpO2(val, pulse, ts));
        }

        // One valid measurement
        processor.processMeasurement(new IntermediateSpO2(93, 66, 58000));

        //then 1 after the end of the minute
        processor.processMeasurement(new IntermediateSpO2(99, 60, 61000));

        // Now trigger the averaging of the first minute.
        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), measurement_captor.capture());


        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<MeasurementSpO2> measurements = measurement_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();


        /* First values calculated - the first minute
         * Expect invalid, as there was only one measurement in the minute so the missing ones count as invalid
         */
        assertEquals(Algorithms.AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal(), (int) integers.get(0));
        assertEquals(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX.ordinal(), (int) integers.get(1));
        assertEquals(60000, measurements.get(0).timestamp_in_ms);
        assertEquals(INVALID_MEASUREMENT, measurements.get(0).SpO2);
        assertEquals(INVALID_MEASUREMENT, measurements.get(0).pulse);
    }


    @Test
    public void SpO2ProcessorTest_fakeDataOneMinuteMixOfValidAndInvalid() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.setDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX);

        int val = INVALID_MEASUREMENT;
        int pulse = INVALID_MEASUREMENT;

        // 50 invalid measurements - so we're below the 55 measurement threshold
        for(int i = 0; i <= 50; i++)
        {
            long ts = i*1000;
            processor.processMeasurement(new IntermediateSpO2(val, pulse, ts));
        }

        // remaining 10 valid
        for(int i = 51; i <= 60; i++)
        {
            val = i + 39; // will go from 90 to 98 - should average to 94. Last sample (99) goes into the next minute and triggers the measurement
            pulse = 60;
            long ts = i*1000;
            processor.processMeasurement(new IntermediateSpO2(val, pulse, ts));
        }

        // Now trigger the averaging of the minute.
        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), measurement_captor.capture());


        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<MeasurementSpO2> measurements = measurement_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(Algorithms.AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal(), (int) integers.get(0));
        assertEquals(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX.ordinal(), (int) integers.get(1));
        assertEquals(60000, measurements.get(0).timestamp_in_ms);
        assertEquals(94, measurements.get(0).SpO2);
        assertEquals(60, measurements.get(0).pulse);
    }


    @Test
    public void SpO2ProcessorTest_fakeDataOneMinuteFiftyFiveInvalid() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.setDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX);

        int val = INVALID_MEASUREMENT;
        int pulse = INVALID_MEASUREMENT;

        // 55 invalid measurements - so we're below the 55 measurement threshold
        for(int i = 0; i <= 54; i++)
        {
            long ts = i*1000;
            processor.processMeasurement(new IntermediateSpO2(val, pulse, ts));
        }

        val = 99;
        pulse = 60;

        // remaining measurements valid
        for(int i = 55; i <= 60; i++)
        {
            long ts = i*1000;
            processor.processMeasurement(new IntermediateSpO2(val, pulse, ts));
        }

        // Now trigger the averaging of the minute.
        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), measurement_captor.capture());


        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<MeasurementSpO2> measurements = measurement_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(Algorithms.AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal(), (int) integers.get(0));
        assertEquals(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX.ordinal(), (int) integers.get(1));
        assertEquals(60000, measurements.get(0).timestamp_in_ms);
        assertEquals(99, measurements.get(0).SpO2);
        assertEquals(60, measurements.get(0).pulse);
    }


    @Test
    public void SpO2ProcessorTest_fakeDataOneMinuteFiftySixInvalid() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.setDeviceType(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX);

        int val = INVALID_MEASUREMENT;
        int pulse = INVALID_MEASUREMENT;

        // 55 invalid measurements - so we're below the 55 measurement threshold
        for(int i = 0; i <= 55; i++)
        {
            long ts = i*1000;
            processor.processMeasurement(new IntermediateSpO2(val, pulse, ts));
        }

        val = 99;
        pulse = 60;
        // remaining measurements valid
        for(int i = 56; i <= 60; i++)
        {
            long ts = i*1000;
            processor.processMeasurement(new IntermediateSpO2(val, pulse, ts));
        }

        // Now trigger the averaging of the minute.
        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(1)).putExtra(string_captor.capture(), measurement_captor.capture());


        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<MeasurementSpO2> measurements = measurement_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(Algorithms.AlgorithmMeasurementType.PULSE_OX_MEASUREMENT.ordinal(), (int) integers.get(0));
        assertEquals(DeviceType.DEVICE_TYPE__NONIN_WRIST_OX.ordinal(), (int) integers.get(1));
        assertEquals(60000, measurements.get(0).timestamp_in_ms);
        assertEquals(INVALID_MEASUREMENT, measurements.get(0).SpO2);
        assertEquals(INVALID_MEASUREMENT, measurements.get(0).pulse);
    }
}
