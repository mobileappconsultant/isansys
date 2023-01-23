package com.isansys.patientgateway.algorithms;

import android.content.Context;
import android.content.Intent;

import com.isansys.patientgateway.ContextInterface;
import com.isansys.common.ErrorCodes;
import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.Settings;
import com.isansys.patientgateway.SystemCommands;
import com.isansys.patientgateway.factories.IntentFactory;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static com.isansys.common.enums.PatientPositionOrientation.ORIENTATION_FRONT;
import static com.isansys.patientgateway.algorithms.Algorithms.AlgorithmMeasurementType.HEART_RATE_MEASUREMENT;
import static com.isansys.patientgateway.algorithms.Algorithms.AlgorithmMeasurementType.ORIENTATION_MEASUREMENT;
import static com.isansys.patientgateway.algorithms.Algorithms.AlgorithmMeasurementType.RESPIRATION_RATE_MEASUREMENT;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;


/**
 * Created by Rory on 31/08/2016.
 */
public class HeartBeatProcessorTestOldRrInverse
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
    ConcurrentLinkedQueue<HeartBeatInfo> dummy_heart_beat_queue;

    @Captor
    ArgumentCaptor<String> string_captor;

    @Captor
    ArgumentCaptor<Integer> int_captor;

    @Captor
    ArgumentCaptor<Long> long_captor;

    @Captor
    ArgumentCaptor<HeartBeatInfo> written_to_db_heart_beat_captor;

    @Captor
    ArgumentCaptor<ArrayList<HeartBeatInfo>> reported_to_ui_heart_beat_captor;

    IntermediateMeasurementProcessor<HeartBeatInfo> processor;


    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        when(dummy_context_interface.getAppContext()).thenReturn(mMockContext);

        when(dummy_settings.getSimpleHeartRateEnabledStatus()).thenReturn(false);

        when(dummy_settings.getDisableCommentsForSpeed()).thenReturn(false);

        when(dummy_settings.getPercentageOfPoorSignalHeartBeatsBeforeMinuteMarkedInvalid()).thenReturn(10);

        when(dummy_factory.getNewIntent(anyString())).thenReturn(dummy_intent);

        processor = new IntermediateMeasurementProcessor(dummy_context_interface, dummy_settings, dummy_logger, dummy_factory, dummy_commands, dummy_heart_beat_queue, new HeartBeatInfo());
    }

    @Test
    public void HeartBeatProcessorTest_fakeDataThreeMinutes() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // initial point before the start of the processed minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 59000));

        // minute that gets processed, plus extra beat after the end to trigger HR and RR measurements.
        for(int i = 0; i <= 180; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000;
            int tag = i + 1;
            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
        }

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(12)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(6)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(181)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(181)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(120000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(120000, (long) timestamps.get(1));


        /* Second values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(60, (int) integers.get(5));
        assertEquals(180000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = 15 and timestamp is still 180000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(15, (int) integers.get(7));
        assertEquals(180000, (long) timestamps.get(3));


        /* Third values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(60, (int) integers.get(9));
        assertEquals(240000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = 15 and timestamp is still 240000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(15, (int) integers.get(11));
        assertEquals(240000, (long) timestamps.get(5));

        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(181, written_beats.size());
        assertEquals(181, reported_beats.size());

        for(int i = 0; i < 181; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        // HeartBeats in list are equal, so don't need to test both.
        for(int i = 0; i < 181; i++)
        {
            HeartBeatInfo info = written_beats.get(i);

            assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
            assertEquals(60000 + i*1000, info.getTimestampInMs());
            assertEquals(i + 1, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataThreeMinutesInReverseOrder() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // minutes that get processed
        for(int i = 240; i >= 60; i--)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = i*1000;
            int tag = i;
            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
        }

        // one beat before the beginning
        processor.processMeasurement(new HeartBeatInfo(59, 200, 59000));

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(12)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(6)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(181)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(181)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(240000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 240000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(240000, (long) timestamps.get(1));


        /* Second values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(60, (int) integers.get(5));
        assertEquals(180000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = 15 and timestamp is still 180000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(15, (int) integers.get(7));
        assertEquals(180000, (long) timestamps.get(3));


        /* Third values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(60, (int) integers.get(9));
        assertEquals(120000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(15, (int) integers.get(11));
        assertEquals(120000, (long) timestamps.get(5));



        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(181, written_beats.size());
        assertEquals(181, reported_beats.size());

        for(int i = 0; i < 181; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        // HeartBeats in list are equal, so don't need to test both.
        for(int i = 0; i < 181; i++)
        {
            HeartBeatInfo info = written_beats.get(i);

            assertEquals(200 + (int)(50*Math.sin((240 - i)*Math.PI/2)), info.getAmplitude());
            assertEquals(240000 - i*1000, info.getTimestampInMs());
            assertEquals(240 - i, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataThreeMinutesInMixedOrder() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // minutes that get processed, starting in the middle and alternating

        // Write the middle beat
        int amp = 200 + (int)(50*Math.sin(150*Math.PI/2));
        long ts = 150000;
        int tag = 150;
        processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));

        // write alternating
        for(int i = 1; i <= 90; i++)
        {
            amp = 200 + (int)(50*Math.sin((150 + i)*Math.PI/2));
            ts = 150000 + i*1000;
            tag = 150 + i;
            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));

            amp = 200 + (int)(50*Math.sin((150 - i)*Math.PI/2));
            ts = 150000 - i*1000;
            tag = 150 - i;
            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
        }

        // Write one before beginning of first minute
        amp = 200 + (int)(50*Math.sin((59)*Math.PI/2));
        processor.processMeasurement(new HeartBeatInfo(59, amp, 59000));

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(12)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(6)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(181)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(181)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(180000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 180000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(180000, (long) timestamps.get(1));


        /* Second values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(60, (int) integers.get(5));
        assertEquals(240000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = 16 and timestamp is still 240000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(15, (int) integers.get(7));
        assertEquals(240000, (long) timestamps.get(3));


        /* Third values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(60, (int) integers.get(9));
        assertEquals(120000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(15, (int) integers.get(11));
        assertEquals(120000, (long) timestamps.get(5));



        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(181, written_beats.size());
        assertEquals(181, reported_beats.size());

        for(int i = 0; i < 181; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }


        // Check first written beat
        HeartBeatInfo info = written_beats.get(0);
        assertEquals(200 + (int)(50*Math.sin(151*Math.PI/2)), info.getAmplitude());
        assertEquals(151000, info.getTimestampInMs());
        assertEquals(151, info.getTag());
        assertEquals(1000, info.getRrInterval());


        // check alternating writes
        for(int i = 1; i < 90; i++)
        {
            info = written_beats.get(2*i - 1);

            amp = 200 + (int)(50*Math.sin((151 - i)*Math.PI/2));
            ts = 151000 - i*1000;
            tag = 151 - i;

            assertEquals(amp, info.getAmplitude());
            assertEquals(ts, info.getTimestampInMs());
            assertEquals(tag, info.getTag());
            assertEquals(1000, info.getRrInterval());

            info = written_beats.get(2*i);

            amp = 200 + (int)(50*Math.sin((151 + i)*Math.PI/2));
            ts = 151000 + i*1000;
            tag = 151 + i;

            assertEquals(amp, info.getAmplitude());
            assertEquals(ts, info.getTimestampInMs());
            assertEquals(tag, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }

        // Check last written beat
        info = written_beats.get(180);

        amp = 200 + (int)(50*Math.sin((60)*Math.PI/2));
        ts = 60000;
        tag = 60;

        assertEquals(amp, info.getAmplitude());
        assertEquals(ts, info.getTimestampInMs());
        assertEquals(tag, info.getTag());
        assertEquals(1000, info.getRrInterval());
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataThreeMinutesWithBigRR() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // initial point before the start of the processed minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 59000));

        // Data before the gap
        for(int i = 0; i <= 115; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000;
            int tag = i + 1;
            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
        }

        // Data after the gap
        for(int i = 116; i <= 165; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000 + 15000;
            int tag = i + 1;
            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
        }

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(12)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(6)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(166)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(166)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }

        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(120000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(120000, (long) timestamps.get(1));


        /* Second values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(60, (int) integers.get(5));
        assertEquals(180000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = 15 and timestamp is still 180000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(15, (int) integers.get(7));
        assertEquals(180000, (long) timestamps.get(3));


        /* Third values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(46, (int) integers.get(9));
        assertEquals(240000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = 15 and timestamp is still 240000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(15, (int) integers.get(11));
        assertEquals(240000, (long) timestamps.get(5));




        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(166, written_beats.size());
        assertEquals(166, reported_beats.size());

        for(int i = 0; i < 166; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        HeartBeatInfo info;

        // HeartBeats in list are equal, so don't need to test both.
        for(int i = 0; i <= 115; i++)
        {
            info = written_beats.get(i);

            assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
            assertEquals(60000 + i*1000, info.getTimestampInMs());
            assertEquals(i + 1, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }

        // first one after the gap
        info = written_beats.get(116);

        assertEquals(200 + (int)(50*Math.sin(116*Math.PI/2)), info.getAmplitude());
        assertEquals(60000 + 116000 + 15000, info.getTimestampInMs());
        assertEquals(117, info.getTag());
        assertEquals(16000, info.getRrInterval());

        // Data after the gap
        for(int i = 117; i <= 165; i++)
        {
            info = written_beats.get(i);

            assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
            assertEquals(60000 + i*1000 + 15000, info.getTimestampInMs());
            assertEquals(i + 1, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataThreeMinutesInRandomisedOrder() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // minutes that get processed, starting in the middle and alternating


        ArrayList<Integer> indexes = new ArrayList<>();

        for(int i = 59; i <= 240; i++)
        {
            indexes.add(i);
        }

        Collections.shuffle(indexes);

        int amp;
        long ts;
        int tag;

        // write random order
        for(Integer i : indexes)
        {
            amp = 200 + (int)(50*Math.sin((i)*Math.PI/2));
            ts = i*1000;
            tag = i;
            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
        }


        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(12)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(6)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(181)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, atLeastOnce()).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /*
         * HR and RR values not necessarily reported in any given order - it'll be randomised. So sort timestamps first...
         */
        Collections.sort(timestamps);


        /* First values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(120000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 180000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(120000, (long) timestamps.get(1));


        /* Second values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(60, (int) integers.get(5));
        assertEquals(180000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = 16 and timestamp is still 240000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(15, (int) integers.get(7));
        assertEquals(180000, (long) timestamps.get(3));


        /* Third values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(60, (int) integers.get(9));
        assertEquals(240000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(15, (int) integers.get(11));
        assertEquals(240000, (long) timestamps.get(5));



        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(181, written_beats.size());
        assertEquals(181, reported_beats.size());

        for(int i = 0; i < 181; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        /* Check randomised beats
         * They are not necessarily reported in any given order - it'll be randomised. So sort by timestamps first...
         */
        Collections.sort(written_beats, new Comparator<HeartBeatInfo>()
        {
            @Override
            public int compare(HeartBeatInfo o1, HeartBeatInfo o2)
            {
                return (o1.getTag() - o2.getTag());
            }
        });


        HeartBeatInfo info;
        for(int i = 0; i < 181; i++)
        {
            info = written_beats.get(i);
            amp = 200 + (int)(50*Math.sin((i + 60)*Math.PI/2));
            ts = (i + 60)*1000;
            tag = i + 60;

            assertEquals(amp, info.getAmplitude());
            assertEquals(ts, info.getTimestampInMs());
            assertEquals(tag, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataFourMinutesWithLeadsOff() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // initial point before the start of the processed minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 59000));

        // minute that gets processed, plus extra beat after the end to trigger HR and RR measurements.
        for(int i = 0; i <= 240; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000;
            int tag = i + 1;

            /* Leads off for 20 beats in the second minute, and one in the third. */
            if(((i > 80) && (i < 100)) || (i == 122))
            {
                processor.processMeasurement(new HeartBeatInfo(tag, ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF, ts));
            }
            else
            {
                processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
            }
        }

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(16)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(8)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(241)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(241)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(120000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(120000, (long) timestamps.get(1));


        /* Second values calculated - the second minute - should be leads off
         * check HR values - type = 0, HR = leads_off and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF, (int) integers.get(5));
        assertEquals(180000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = leads_off and timestamp is still 180000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF, (int) integers.get(7));
        assertEquals(180000, (long) timestamps.get(3));


        /* Third values calculated - the third minute
         * check HR values - type = 0, HR = leads_off and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF, (int) integers.get(9));
        assertEquals(240000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = leads_off and timestamp is still 240000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF, (int) integers.get(11));
        assertEquals(240000, (long) timestamps.get(5));


        /* Fourth values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(12));
        assertEquals(60, (int) integers.get(13));
        assertEquals(300000, (long) timestamps.get(6));

        // check RR values - type = 1, RR = 16 and timestamp is still 240000.
        assertEquals(1, (int) integers.get(14));
        assertEquals(15, (int) integers.get(15));
        assertEquals(300000, (long) timestamps.get(7));

        /* Now check the lists of reported and written heart beats.
         * Should be 241 reported - 242 beats were written - 240 for 4 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 241 are reported and all of them have valid RR intervals.
         */

        assertEquals(241, written_beats.size());
        assertEquals(241, reported_beats.size());

        for(int i = 0; i < 241; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        // HeartBeats in list are equal, so don't need to test both.
        for(int i = 0; i < 241; i++)
        {
            HeartBeatInfo info = written_beats.get(i);

            if(((i > 80) && (i < 100)) || (i == 122))
            {
                assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF, info.getAmplitude());
                assertEquals(HeartBeatInfo.RR_NOT_VALID, info.getRrInterval());
            }
            else if((i == 100) | (i == 123))
            {
                assertEquals(200 + (int) (50 * Math.sin(i * Math.PI / 2)), info.getAmplitude());
                assertEquals(HeartBeatInfo.RR_NOT_VALID, info.getRrInterval());
            }
            else
            {
                assertEquals(200 + (int) (50 * Math.sin(i * Math.PI / 2)), info.getAmplitude());
                assertEquals(1000, info.getRrInterval());
            }

            assertEquals(60000 + i*1000, info.getTimestampInMs());
            assertEquals(i + 1, info.getTag());
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataThreeMinutesWithOrientationBeats() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // initial point before the start of the processed minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 59000));

        // Orientation is sent with a separate tag to the other beats, so must be filtered out.
        int real_tag = 1;
        int orientation_tag = 1;

        // minute that gets processed, plus extra beat after the end to trigger HR and RR measurements.
        for(int i = 0; i <= 180; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000;

            /* Orientation beat every minute */
            if(i%60 == 42)
            {
                processor.processMeasurement(new HeartBeatInfo(orientation_tag, ErrorCodes.ERROR_CODE__LIFETOUCH_ORIENTATION_FRONT, ts));

                orientation_tag++;
            }

            processor.processMeasurement(new HeartBeatInfo(real_tag, amp, ts));

            real_tag++;
        }

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers, as is Orientation. Expect 9 data types, 3 RR, 3 HR and 3 orientation
        verify(dummy_intent, times(18)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(9)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(181)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(181)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values reported - the first orientation, HR and RR
         *
         */
        assertEquals(ORIENTATION_MEASUREMENT.ordinal(), (int) integers.get(0));
        assertEquals(ORIENTATION_FRONT.ordinal(), (int) integers.get(1));
        assertEquals(102000, (long) timestamps.get(0));

        //check HR values - type = 0, HR = 60 and timestamp is 120000.
        assertEquals(HEART_RATE_MEASUREMENT.ordinal(), (int) integers.get(2));
        assertEquals(60, (int) integers.get(3));
        assertEquals(120000, (long) timestamps.get(1));
        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(RESPIRATION_RATE_MEASUREMENT.ordinal(), (int) integers.get(4));
        assertEquals(15, (int) integers.get(5));
        assertEquals(120000, (long) timestamps.get(2));




        /* Second values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(ORIENTATION_MEASUREMENT.ordinal(), (int) integers.get(6));
        assertEquals(ORIENTATION_FRONT.ordinal(), (int) integers.get(7));
        assertEquals(162000, (long) timestamps.get(3));

        //check HR values - type = 0, HR = 60 and timestamp is 120000.
        assertEquals(HEART_RATE_MEASUREMENT.ordinal(), (int) integers.get(8));
        assertEquals(60, (int) integers.get(9));
        assertEquals(180000, (long) timestamps.get(4));
        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(RESPIRATION_RATE_MEASUREMENT.ordinal(), (int) integers.get(10));
        assertEquals(15, (int) integers.get(11));
        assertEquals(180000, (long) timestamps.get(5));


        /* Third values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(ORIENTATION_MEASUREMENT.ordinal(), (int) integers.get(12));
        assertEquals(ORIENTATION_FRONT.ordinal(), (int) integers.get(13));
        assertEquals(222000, (long) timestamps.get(6));

        //check HR values - type = 0, HR = 60 and timestamp is 120000.
        assertEquals(HEART_RATE_MEASUREMENT.ordinal(), (int) integers.get(14));
        assertEquals(60, (int) integers.get(15));
        assertEquals(240000, (long) timestamps.get(7));
        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(RESPIRATION_RATE_MEASUREMENT.ordinal(), (int) integers.get(16));
        assertEquals(15, (int) integers.get(17));
        assertEquals(240000, (long) timestamps.get(8));

        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(181, written_beats.size());
        assertEquals(181, reported_beats.size());

        for(int i = 0; i < 181; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        // HeartBeats in list are equal, so don't need to test both.
        for(int i = 0; i < 181; i++)
        {
            HeartBeatInfo info = written_beats.get(i);

            assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
            assertEquals(60000 + i*1000, info.getTimestampInMs());
            assertEquals(i + 1, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataFourMinutesWithNoBeatsDetected() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // initial point before the start of the processed minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 59000));

        // minute that gets processed, plus extra beat after the end to trigger HR and RR measurements.
        for(int i = 0; i <= 240; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000;
            int tag = i + 1;

            /* No Beats Detected for 20 beats in the second minute, and one in the third. */
            if(((i > 80) && (i < 100)) || (i == 122))
            {
                processor.processMeasurement(new HeartBeatInfo(tag, ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED, ts));
            }
            else
            {
                processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
            }
        }

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(16)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(8)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(241)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(241)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(120000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(120000, (long) timestamps.get(1));


        /* Second values calculated - the second minute - should be leads off
         * check HR values - type = 0, HR = leads_off and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED, (int) integers.get(5));
        assertEquals(180000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = leads_off and timestamp is still 180000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED, (int) integers.get(7));
        assertEquals(180000, (long) timestamps.get(3));


        /* Third values calculated - the third minute
         * check HR values - type = 0, HR = leads_off and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED, (int) integers.get(9));
        assertEquals(240000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = leads_off and timestamp is still 240000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED, (int) integers.get(11));
        assertEquals(240000, (long) timestamps.get(5));

        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(241, written_beats.size());
        assertEquals(241, reported_beats.size());

        for(int i = 0; i < 241; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        // HeartBeats in list are equal, so don't need to test both.
        for(int i = 0; i < 241; i++)
        {
            HeartBeatInfo info = written_beats.get(i);

            if(((i > 80) && (i < 100)) || (i == 122))
            {
                assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_NO_BEATS_DETECTED, info.getAmplitude());
                assertEquals(HeartBeatInfo.RR_NOT_VALID, info.getRrInterval());
            }
            else if((i == 100) || (i == 123))
            {
                // Amplitude is valid but RR is not
                assertEquals(200 + (int) (50 * Math.sin(i * Math.PI / 2)), info.getAmplitude());
                assertEquals(HeartBeatInfo.RR_NOT_VALID, info.getRrInterval());
            }
            else
            {
                assertEquals(200 + (int) (50 * Math.sin(i * Math.PI / 2)), info.getAmplitude());
                assertEquals(1000, info.getRrInterval());
            }

            assertEquals(60000 + i*1000, info.getTimestampInMs());
            assertEquals(i + 1, info.getTag());
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataThreeMinutesLowBeats() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // initial point before the start of the processed minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 59000));

        // minute that gets processed, plus extra beat after the end to trigger HR and RR measurements.
        for(int i = 0; i <= 180; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000;
            int tag = i + 1;

            // 6 low amplitude beats between 130000 and 137000; exactly 10 per cent
            // 5 between 190000 and 196000; fewer than 10 percent
            if(((i > 70) && (i < 77))
                || ((i > 130) && (i < 136)))
            {
                amp *= 0.1;
            }

            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
        }

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(12)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(6)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(181)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(181)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(120000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(120000, (long) timestamps.get(1));


        /* Second values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_POOR_SIGNAL_IN_LAST_MINUTE, (int) integers.get(5));
        assertEquals(180000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = 15 and timestamp is still 180000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_POOR_SIGNAL_IN_LAST_MINUTE, (int) integers.get(7));
        assertEquals(180000, (long) timestamps.get(3));


        /* Third values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(60, (int) integers.get(9));
        assertEquals(240000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = 15 and timestamp is still 240000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(14, (int) integers.get(11));                   // 14 not 15 because of the low amplitude beats
        assertEquals(240000, (long) timestamps.get(5));

        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(181, written_beats.size());
        assertEquals(181, reported_beats.size());

        for(int i = 0; i < 181; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        // HeartBeats in list are equal, so don't need to test both.
        for(int i = 0; i < 181; i++)
        {
            HeartBeatInfo info = written_beats.get(i);


            // Low amplitude beats
            if(((i > 70) && (i < 77))
                || ((i > 130) && (i < 136)))
            {
                assertEquals(20 + (int)(5*Math.sin(i*Math.PI/2)), info.getAmplitude());
            }
            else
            {
                assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
            }

            assertEquals(60000 + i*1000, info.getTimestampInMs());
            assertEquals(i + 1, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataThreeMinutesHighBeats() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // initial point before the start of the processed minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 59000));

        // minute that gets processed, plus extra beat after the end to trigger HR and RR measurements.
        for(int i = 0; i <= 180; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000;
            int tag = i + 1;

            // 6 low amplitude beats between 130000 and 137000; exactly 10 per cent
            // 5 between 190000 and 196000; fewer than 10 percent
            if(((i > 70) && (i < 77))
                    || ((i > 130) && (i < 136)))
            {
                amp += 3700;
            }

            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
        }

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(12)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(6)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(181)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(181)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(120000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(120000, (long) timestamps.get(1));


        /* Second values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_POOR_SIGNAL_IN_LAST_MINUTE, (int) integers.get(5));
        assertEquals(180000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = 15 and timestamp is still 180000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_POOR_SIGNAL_IN_LAST_MINUTE, (int) integers.get(7));
        assertEquals(180000, (long) timestamps.get(3));


        /* Third values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(60, (int) integers.get(9));
        assertEquals(240000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = 15 and timestamp is still 240000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(15, (int) integers.get(11));
        assertEquals(240000, (long) timestamps.get(5));

        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(181, written_beats.size());
        assertEquals(181, reported_beats.size());

        for(int i = 0; i < 181; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        // HeartBeats in list are equal, so don't need to test both.
        for(int i = 0; i < 181; i++)
        {
            HeartBeatInfo info = written_beats.get(i);


            // Low amplitude beats
            if(((i > 70) && (i < 77))
                    || ((i > 130) && (i < 136)))
            {
                assertEquals(3900 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
            }
            else
            {
                assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
            }

            assertEquals(60000 + i*1000, info.getTimestampInMs());
            assertEquals(i + 1, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataThreeMinutesMiscErrorCodes() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // initial point before the start of the processed minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 59000));

        // minute that gets processed, plus extra beat after the end to trigger HR and RR measurements.
        int tag = 1;
        for(int i = 0; i <= 180; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000;

            // 6 low amplitude beats between 130000 and 137000; exactly 10 per cent
            // 5 between 190000 and 196000; fewer than 10 percent
            if(i == 42)
            {
                processor.processMeasurement(new HeartBeatInfo(tag, ErrorCodes.ERROR_CODE__LIFETOUCH_NO_TIMESTAMP, ts));
                tag++;
            }
            else if(i == 71)
            {
                processor.processMeasurement(new HeartBeatInfo(0, ErrorCodes.ERROR_CODE__LIFETOUCH_CONNECTION_TIMEOUT, ts));
            }
            else if(i == 155)
            {
                processor.processMeasurement(new HeartBeatInfo(1, ErrorCodes.ERROR_CODE__LIFETOUCH_DATA_CREDIT_TIMEOUT, ts));
            }

            processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
            tag++;
        }

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(12)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(6)).putExtra(string_captor.capture(), (long) long_captor.capture());

        // Two error codes will be written to the database but not reported to the UI - the two TIMEOUT ones.
        verify(dummy_heart_beat_queue, times(184)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(182)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(120000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(15, (int) integers.get(3));
        assertEquals(120000, (long) timestamps.get(1));


        /* Second values calculated - the second minute
         * check HR values - type = 0, HR = 60 and timestamp is 180000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(60, (int) integers.get(5));
        assertEquals(180000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = 15 and timestamp is still 180000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(15, (int) integers.get(7));
        assertEquals(180000, (long) timestamps.get(3));


        /* Third values calculated - the third minute
         * check HR values - type = 0, HR = 60 and timestamp is 240000.
         */
        assertEquals(0, (int) integers.get(8));
        assertEquals(60, (int) integers.get(9));
        assertEquals(240000, (long) timestamps.get(4));

        // check RR values - type = 1, RR = 15 and timestamp is still 240000.
        assertEquals(1, (int) integers.get(10));
        assertEquals(15, (int) integers.get(11));
        assertEquals(240000, (long) timestamps.get(5));

        // Same numbers as above - two are written to the DB but not reported.
        assertEquals(184, written_beats.size());
        assertEquals(182, reported_beats.size());

        // Can't check they're all the same in this way - numbers don't match
//        for(int i = 0; i < 184; i++)
//        {
//            assert(written_beats.get(i).equals(reported_beats.get(i)));
//        }

        // HeartBeats in list are equal, so don't need to test both.
        int list_index = 0;
        int expected_tag = 1;

        for(int i = 0; i < 181; i++)
        {
            // error codes
            if(i == 42)
            {
                HeartBeatInfo info = written_beats.get(list_index);

                assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_NO_TIMESTAMP, info.getAmplitude());
                assertEquals(60000 + i*1000, info.getTimestampInMs());
                assertEquals(expected_tag, info.getTag());
                assertEquals(HeartBeatInfo.RR_NOT_VALID, info.getRrInterval());

                list_index++;
                expected_tag++;

                info = written_beats.get(list_index);

                assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
                assertEquals(60000 + i*1000, info.getTimestampInMs());
                assertEquals(expected_tag, info.getTag());
                assertEquals(HeartBeatInfo.RR_NOT_VALID, info.getRrInterval());

                list_index++;
                expected_tag++;
            }
            else if(i == 71)
            {
                HeartBeatInfo info = written_beats.get(list_index);

                assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_CONNECTION_TIMEOUT, info.getAmplitude());
                assertEquals(60000 + i*1000, info.getTimestampInMs());
                assertEquals(0, info.getTag());
                assertEquals(HeartBeatInfo.RR_NOT_YET_CALCULATED, info.getRrInterval());

                list_index++;


                info = written_beats.get(list_index);

                assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
                assertEquals(60000 + i*1000, info.getTimestampInMs());
                assertEquals(expected_tag, info.getTag());
                assertEquals(1000, info.getRrInterval());

                list_index++;
                expected_tag++;
            }
            else if(i == 155)
            {
                HeartBeatInfo info = written_beats.get(list_index);

                assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_DATA_CREDIT_TIMEOUT, info.getAmplitude());
                assertEquals(60000 + i*1000, info.getTimestampInMs());
                assertEquals(1, info.getTag());
                assertEquals(HeartBeatInfo.RR_NOT_YET_CALCULATED, info.getRrInterval());

                list_index++;


                info = written_beats.get(list_index);

                assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
                assertEquals(60000 + i*1000, info.getTimestampInMs());
                assertEquals(expected_tag, info.getTag());
                assertEquals(1000, info.getRrInterval());

                list_index++;
                expected_tag++;
            }
            else
            {
                HeartBeatInfo info = written_beats.get(list_index);

                assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
                assertEquals(60000 + i*1000, info.getTimestampInMs());
                assertEquals(expected_tag, info.getTag());
                assertEquals(1000, info.getRrInterval());

                list_index++;
                expected_tag++;
            }
        }
    }


    @Test
    public void HeartBeatProcessorTest_fakeDataIncompleteMinuteProcessedOutstanding() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // initial point before the start of the processed minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 59000));

        // minute that gets processed, plus extra beat after the end to trigger HR and RR measurements.
        for(int i = 0; i <= 61; i++)
        {
            int amp = 200 + (int)(50*Math.sin(i*Math.PI/2));
            long ts = 60000 + i*1000;
            int tag = i + 1;

            if(i != 60)
            {
                processor.processMeasurement(new HeartBeatInfo(tag, amp, ts));
            }
            else
            {
                ; // skip i = 60 so that 2nd minute doesn't get processed.
            }
        }

        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(8)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(4)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(60)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(60)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());

        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();
        List<String> key_strings = string_captor.getAllValues();

        List<HeartBeatInfo> written_beats = written_to_db_heart_beat_captor.getAllValues();
        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* First values calculated - the first minute
         * error code leads off as there's only one data point (at 59000
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM, (int) integers.get(1));
        assertEquals(60000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM, (int) integers.get(3));
        assertEquals(60000, (long) timestamps.get(1));


        /* Second values calculated - the first minute
         * check HR values - type = 0, HR = 60 and timestamp is 120000.
         */
        assertEquals(0, (int) integers.get(4));
        assertEquals(60, (int) integers.get(5));
        assertEquals(120000, (long) timestamps.get(2));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(6));
        assertEquals(15, (int) integers.get(7));
        assertEquals(120000, (long) timestamps.get(3));

        /* Now check the lists of reported and written heart beats.
         * Should be 181 reported - 182 beats were written - 180 for 3 minutes of data at 60 bpm, plus one before and one after to ensure the HR and RR are calculated.
         * The first one is never reported, as it has no RR interval, so altogether 181 are reported and all of them have valid RR intervals.
         */

        assertEquals(60, written_beats.size());
        assertEquals(60, reported_beats.size());

        for(int i = 0; i < 60; i++)
        {
            assert(written_beats.get(i).equals(reported_beats.get(i)));
        }

        // HeartBeats in list are equal, so don't need to test both.
        for(int i = 0; i < 60; i++)
        {
            HeartBeatInfo info = written_beats.get(i);

            assertEquals(200 + (int)(50*Math.sin(i*Math.PI/2)), info.getAmplitude());
            assertEquals(60000 + i*1000, info.getTimestampInMs());
            assertEquals(i + 1, info.getTag());
            assertEquals(1000, info.getRrInterval());
        }
    }


    @Test
    public void HeartBeatProcessorTest_singleHeartBeatGeneratesErrorCode() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // single point
        processor.processMeasurement(new HeartBeatInfo(0, 200, 30000));

        // now add a new data point for the next minute - need to make first minute processable.
        processor.processMeasurement(new HeartBeatInfo(1, 200, 90000));


        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(4)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(1)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(1)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());



        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();

        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* Check values calculated - the first minute
         * error code leads off as there's only one data point (at 59000
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM, (int) integers.get(1));
        assertEquals(60000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM, (int) integers.get(3));
        assertEquals(60000, (long) timestamps.get(1));
    }


    @Test
    public void HeartBeatProcessorTest_twoHeartBeatsGeneratesErrorCode() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // Two points in initial minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 30000));

        processor.processMeasurement(new HeartBeatInfo(1, 200, 30000));


        // now add a new data point for the next minute - need to make first minute processable.
        processor.processMeasurement(new HeartBeatInfo(2, 200, 90000));


        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(4)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(2)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(2)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());



        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();

        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* Check values calculated - the first minute
         * error code leads off as there's only one data point (at 59000
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM, (int) integers.get(1));
        assertEquals(60000, (long) timestamps.get(0));

        // check RR values - type = 1, RR = 15 and timestamp is still 120000.
        assertEquals(1, (int) integers.get(2));
        assertEquals(ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM, (int) integers.get(3));
        assertEquals(60000, (long) timestamps.get(1));
    }


    @Test
    public void HeartBeatProcessorTest_threeHeartBeatsProcessedNormally() throws Exception
    {
        doNothing().when(dummy_logger).d(anyString(), anyString());

        processor.reset();

        // Three points in initial minute
        processor.processMeasurement(new HeartBeatInfo(0, 200, 30000));

        processor.processMeasurement(new HeartBeatInfo(1, 250, 31000));

        processor.processMeasurement(new HeartBeatInfo(2, 200, 32000));



        // now add a new data point for the next minute - need to make first minute processable.
        processor.processMeasurement(new HeartBeatInfo(3, 200, 90000));


        processor.processAnyOutstandingData();

        // Capture the extras added to the intents when reporting the HR and RR
        // data types and HR and RR values are integers
        verify(dummy_intent, times(4)).putExtra(string_captor.capture(), (int) int_captor.capture());
        verify(dummy_intent, times(2)).putExtra(string_captor.capture(), (long) long_captor.capture());

        verify(dummy_heart_beat_queue, times(3)).add(written_to_db_heart_beat_captor.capture());
        verify(dummy_commands, times(3)).reportHeartBeats(reported_to_ui_heart_beat_captor.capture());



        // extract all the captured data
        List<Integer> integers = int_captor.getAllValues();
        List<Long> timestamps = long_captor.getAllValues();

        List<ArrayList<HeartBeatInfo>> list_of_lists_of_reported_beats = reported_to_ui_heart_beat_captor.getAllValues();
        ArrayList<HeartBeatInfo> reported_beats = new ArrayList<>();

        for(ArrayList list : list_of_lists_of_reported_beats)
        {
            reported_beats.addAll(list);
        }


        /* Check values calculated - the first minute
         * HR should be 60 BPM, as the rr intervals are all 1000 ms
         */
        assertEquals(0, (int) integers.get(0));
        assertEquals(60, (int) integers.get(1));
        assertEquals(60000, (long) timestamps.get(0));

        // RR is 25 - we've put in a single peak over 3 datapoints - so would expect 60 / 3 = 20 BR/min
        // But presumably smoothing, interpolation, and the way we deal with incomplete data skews this a little.
        // With longer data series, the filtering artefacts don't play such a big part, which is why we do get 15 BR/min on the
        // normal tests over a full minute of data (where a full sine wave "breath" happens over 4 beats, not 3).
        assertEquals(1, (int) integers.get(2));
        assertEquals(25, (int) integers.get(3));
        assertEquals(60000, (long) timestamps.get(1));
    }


    // ToDo: test exact low/high heart beat amplitude threshold levels.
}
