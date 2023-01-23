package com.isansys.patientgateway.algorithms;

import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.IntermediateSpO2;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rory on 30/09/2016.
 */

public class MinuteOfDataTest
{
    /**
     * HeartBeatMinutes...
     *
     */

    @Test
    public void MinuteOfHeartBeatsTest_isTimestampOutsideMinute() throws Exception
    {
        // Minute starts at 60000 ms
        MinuteOfData test_minute = new MinuteOfHeartBeats(60000, new HeartBeatInfo());

        // 65000 should be in the minute, therefore expect result of 0
        assertEquals(0, test_minute.isTimestampOutsideMinute(65000));

        // 59000 should be before, so expect -1
        assertEquals(-1, test_minute.isTimestampOutsideMinute(59000));

        // 150000 should be after, so expect +1
        assertEquals(1, test_minute.isTimestampOutsideMinute(150000));

        // Now check the boundaries.
        // 60000 should be (just) inside the minute
        assertEquals(0, test_minute.isTimestampOutsideMinute(60000));

        // as should 119999
        assertEquals(0, test_minute.isTimestampOutsideMinute(119000));


        // 120000 should be (just) after the minute
        assertEquals(1, test_minute.isTimestampOutsideMinute(120000));

        // 59999 should be just before
        assertEquals(-1, test_minute.isTimestampOutsideMinute(59999));
    }

    @Test
    public void MinuteOfHeartBeatsTest_addDataPoint() throws Exception
    {
        MinuteOfData test_minute = new MinuteOfHeartBeats(0, new HeartBeatInfo());

        HeartBeatInfo test_point = new HeartBeatInfo();

        boolean result = test_minute.addDataPoint(test_point);

        assertEquals(true, result);
    }

    @Test
    public void MinuteOfHeartBeatsTest_addDuplicateData() throws Exception
    {
        MinuteOfData test_minute = new MinuteOfHeartBeats(0, new HeartBeatInfo());

        HeartBeatInfo test_point = new HeartBeatInfo();

        // fist add
        boolean result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // add duplicate
        test_point = new HeartBeatInfo();
        result = test_minute.addDataPoint(test_point);

        assertEquals(false, result);
    }

    @Test
    public void MinuteOfHeartBeatsTest_addOrderedData()
    {
        MinuteOfData test_minute = new MinuteOfHeartBeats(0, new HeartBeatInfo());

        HeartBeatInfo test_point = new HeartBeatInfo(0, 0, 0);

        // fist add
        boolean result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // second add
        test_point = new HeartBeatInfo(1, 0, 1000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // third add
        test_point = new HeartBeatInfo(2, 0, 2000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        assertEquals(false, test_minute.doesMinuteContainGaps());
    }

    @Test
    public void MinuteOfHeartBeatsTest_addUnorderedData()
    {
        MinuteOfData test_minute = new MinuteOfHeartBeats(0, new HeartBeatInfo());

        HeartBeatInfo test_point = new HeartBeatInfo(0, 0, 0);

        // fist add
        boolean result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // second add
        test_point = new HeartBeatInfo(2, 0, 2000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // third add
        test_point = new HeartBeatInfo(1, 0, 1000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        assertEquals(false, test_minute.doesMinuteContainGaps());
    }

    @Test
    public void MinuteOfHeartBeatsTest_addGappyData()
    {
        MinuteOfData test_minute = new MinuteOfHeartBeats(0, new HeartBeatInfo());

        HeartBeatInfo test_point = new HeartBeatInfo(0, 0, 0);

        // fist add
        boolean result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // second add
        test_point = new HeartBeatInfo(2, 0, 2000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        assertEquals(true, test_minute.doesMinuteContainGaps());
    }

    // ToDo: More tests - e.g. tag overflow, 2 beats with same timestamp, etc.



    /**
     * SpO2 Minutes...
     *
     */

    @Test
    public void MinuteOfSpO2Test_isTimestampOutsideMinute() throws Exception
    {
        // Minute starts at 60000 ms
        MinuteOfData test_minute = new MinuteOfSpO2(60000, new IntermediateSpO2());

        // 65000 should be in the minute, therefore expect result of 0
        assertEquals(0, test_minute.isTimestampOutsideMinute(65000));

        // 59000 should be before, so expect -1
        assertEquals(-1, test_minute.isTimestampOutsideMinute(59000));

        // 150000 should be after, so expect +1
        assertEquals(1, test_minute.isTimestampOutsideMinute(150000));

        // Now check the boundaries.
        // 60000 should be (just) inside the minute
        assertEquals(0, test_minute.isTimestampOutsideMinute(60000));

        // as should 119999
        assertEquals(0, test_minute.isTimestampOutsideMinute(119000));


        // 120000 should be (just) after the minute
        assertEquals(1, test_minute.isTimestampOutsideMinute(120000));

        // 59999 should be just before
        assertEquals(-1, test_minute.isTimestampOutsideMinute(59999));
    }

    @Test
    public void MinuteOfSpO2Test_addDataPoint() throws Exception
    {
        MinuteOfData test_minute = new MinuteOfSpO2(0, new IntermediateSpO2());

        IntermediateSpO2 test_point = new IntermediateSpO2();

        boolean result = test_minute.addDataPoint(test_point);

        assertEquals(true, result);

        assertEquals(1, test_minute.data.size());
    }

    @Test
    public void MinuteOfSpO2Test_addDuplicateData() throws Exception
    {
        MinuteOfData test_minute = new MinuteOfSpO2(0, new IntermediateSpO2());

        IntermediateSpO2 test_point = new IntermediateSpO2();

        // fist add
        boolean result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // add duplicate
        test_point = new IntermediateSpO2();
        result = test_minute.addDataPoint(test_point);

        assertEquals(false, result);

        assertEquals(1, test_minute.data.size());
    }

    @Test
    public void MinuteOfSpO2Test_addOrderedData()
    {
        MinuteOfData test_minute = new MinuteOfSpO2(0, new IntermediateSpO2());

        IntermediateSpO2 test_point = new IntermediateSpO2(99, 88, 0);

        // first add
        boolean result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // second add
        test_point = new IntermediateSpO2(99, 88, 1000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // third add
        test_point = new IntermediateSpO2(99, 88, 2000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // Should return true, as for SpO2 the test is how many intermediate measurements there are - no gaps if there are >= 60.
        assertEquals(true, test_minute.doesMinuteContainGaps());

        assertEquals(3, test_minute.data.size());
    }

    @Test
    public void MinuteOfSpO2Test_addUnorderedData()
    {
        MinuteOfData test_minute = new MinuteOfSpO2(0, new IntermediateSpO2());

        IntermediateSpO2 test_point = new IntermediateSpO2(99, 88, 0);

        // first add
        boolean result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // second add
        test_point = new IntermediateSpO2(99, 88, 2000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // third add
        test_point = new IntermediateSpO2(99, 88, 1000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // Should return true, as for SpO2 the test is how many intermediate measurements there are - no gaps if there are >= 60.
        assertEquals(true, test_minute.doesMinuteContainGaps());

        assertEquals(3, test_minute.data.size());
    }

    @Test
    public void MinuteOfSpO2Test_addGappyData()
    {
        MinuteOfData test_minute = new MinuteOfSpO2(0, new IntermediateSpO2());

        IntermediateSpO2 test_point = new IntermediateSpO2(99, 88, 0);

        // fist add
        boolean result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        // second add
        test_point = new IntermediateSpO2(99, 88, 2000);
        result = test_minute.addDataPoint(test_point);
        assertEquals(true, result);

        assertEquals(true, test_minute.doesMinuteContainGaps());

        assertEquals(2, test_minute.data.size());
    }
}
