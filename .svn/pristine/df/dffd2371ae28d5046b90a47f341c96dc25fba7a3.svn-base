package com.isansys.pse_isansysportal;

import com.isansys.common.measurements.MeasurementBloodPressure;
import com.isansys.common.measurements.MeasurementHeartRate;
import com.isansys.common.measurements.MeasurementRespirationRate;
import com.isansys.common.measurements.MeasurementSpO2;
import com.isansys.common.measurements.MeasurementTemperature;
import com.isansys.common.measurements.VitalSignType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Local unit tests for the MeasurementCache class.
 */

public class MeasurementCacheTest
{
    MeasurementCache test_cache;

    @Before
    public void setUp()
    {
        test_cache = new MeasurementCache();
    }

    @Test
    public void MeasurementCache_getLatestMeasurementByTypeWithEmptyCache()
    {
        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.HEART_RATE));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.RESPIRATION_RATE));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.TEMPERATURE));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.SPO2));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.BLOOD_PRESSURE));



        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.EARLY_WARNING_SCORE));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.MANUALLY_ENTERED_HEART_RATE));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.MANUALLY_ENTERED_RESPIRATION_RATE));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.MANUALLY_ENTERED_TEMPERATURE));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.MANUALLY_ENTERED_SPO2));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.MANUALLY_ENTERED_BLOOD_PRESSURE));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL));

        Assert.assertNull(test_cache.getLatestMeasurementByType(VitalSignType.MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN));
    }


    @Test
    public void MeasurementCache_getListByType()
    {
        ArrayList<MeasurementHeartRate> test_hr_list = (ArrayList<MeasurementHeartRate>) test_cache.getListFromType(VitalSignType.HEART_RATE);

        test_hr_list.add(new MeasurementHeartRate(0, 0));

        Assert.assertEquals(0, ((ArrayList<MeasurementHeartRate>) test_cache.getListFromType(VitalSignType.HEART_RATE)).get(0).timestamp_in_ms);


        ArrayList<MeasurementRespirationRate> test_rr_list = (ArrayList<MeasurementRespirationRate>) test_cache.getListFromType(VitalSignType.RESPIRATION_RATE);

        test_rr_list.add(new MeasurementRespirationRate(0, 1));

        Assert.assertEquals(1, ((ArrayList<MeasurementRespirationRate>) test_cache.getListFromType(VitalSignType.RESPIRATION_RATE)).get(0).timestamp_in_ms);


        ArrayList<MeasurementTemperature> test_temp_list = (ArrayList<MeasurementTemperature>) test_cache.getListFromType(VitalSignType.TEMPERATURE);

        test_temp_list.add(new MeasurementTemperature(0.0, 2 ));

        Assert.assertEquals(2, ((ArrayList<MeasurementTemperature>) test_cache.getListFromType(VitalSignType.TEMPERATURE)).get(0).timestamp_in_ms);


        ArrayList<MeasurementSpO2> test_nonin_list = (ArrayList<MeasurementSpO2>) test_cache.getListFromType(VitalSignType.SPO2);

        test_nonin_list.add(new MeasurementSpO2(0, 3));

        Assert.assertEquals(3, ((ArrayList<MeasurementSpO2>) test_cache.getListFromType(VitalSignType.SPO2)).get(0).timestamp_in_ms);


        //ToDo: Finish adding these tests
    }


    @Test
    public void MeasurementCache_addAMeasurementAndGetLatest()
    {
        MeasurementHeartRate test_hr = new MeasurementHeartRate(100, 25000);

        test_cache.updateCachedVitalsList(VitalSignType.HEART_RATE, test_hr);

        MeasurementHeartRate added_hr = (MeasurementHeartRate)test_cache.getLatestMeasurementByType(VitalSignType.HEART_RATE);

        Assert.assertEquals(test_hr, added_hr);

        //ToDo: Test for other measurements as well
    }


    @Test
    public void MeasurementCache_updateCachedVitalsListAndSortInTimeOrder_HR()
    {
        MeasurementHeartRate test_hr_1 = new MeasurementHeartRate(100, 25000);
        MeasurementHeartRate test_hr_2 = new MeasurementHeartRate(100, 17000);

        ArrayList<MeasurementHeartRate> list_to_sort = new ArrayList<>();

        list_to_sort.add(test_hr_1);
        list_to_sort.add(test_hr_2);

        test_cache.updateCachedVitalsListAndSortInTimeOrder(VitalSignType.HEART_RATE, list_to_sort);

        MeasurementHeartRate latest_hr = (MeasurementHeartRate)test_cache.getLatestMeasurementByType(VitalSignType.HEART_RATE);

        Assert.assertEquals(test_hr_1, latest_hr);
    }
    //ToDo: test this for other vitals.

    @Test
    public void MeasurementCache_getSize()
    {
        MeasurementHeartRate test_hr = new MeasurementHeartRate(100, 25000);
        MeasurementRespirationRate test_rr = new MeasurementRespirationRate(110, 24000);
        MeasurementTemperature test_temp = new MeasurementTemperature(12.0, 23000);
        MeasurementSpO2 test_nonin_spo2 = new MeasurementSpO2(130, 22000);
        MeasurementBloodPressure test_bp = new MeasurementBloodPressure(150, 160, 20000);

        test_cache.updateCachedVitalsList(VitalSignType.HEART_RATE, test_hr);
        test_cache.updateCachedVitalsList(VitalSignType.RESPIRATION_RATE, test_rr);
        test_cache.updateCachedVitalsList(VitalSignType.TEMPERATURE, test_temp);
        test_cache.updateCachedVitalsList(VitalSignType.SPO2, test_nonin_spo2);
        test_cache.updateCachedVitalsList(VitalSignType.BLOOD_PRESSURE, test_bp);

        assertEquals(1, test_cache.getCacheSize(VitalSignType.HEART_RATE));
        assertEquals(1, test_cache.getCacheSize(VitalSignType.RESPIRATION_RATE));
        assertEquals(1, test_cache.getCacheSize(VitalSignType.TEMPERATURE));
        assertEquals(1, test_cache.getCacheSize(VitalSignType.SPO2));
        assertEquals(1, test_cache.getCacheSize(VitalSignType.BLOOD_PRESSURE));

        //ToDo: add tests for manual vitals.
    }


    @Test
    public void MeasurementCache_clearAll()
    {
        MeasurementHeartRate test_hr = new MeasurementHeartRate(100, 25000);
        MeasurementRespirationRate test_rr = new MeasurementRespirationRate(110, 24000);
        MeasurementTemperature test_temp = new MeasurementTemperature(12.0, 23000);
        MeasurementSpO2 test_nonin_spo2 = new MeasurementSpO2(130, 22000);
        MeasurementBloodPressure test_bp = new MeasurementBloodPressure(150, 160, 20000);

        test_cache.updateCachedVitalsList(VitalSignType.HEART_RATE, test_hr);
        test_cache.updateCachedVitalsList(VitalSignType.RESPIRATION_RATE, test_rr);
        test_cache.updateCachedVitalsList(VitalSignType.TEMPERATURE, test_temp);
        test_cache.updateCachedVitalsList(VitalSignType.SPO2, test_nonin_spo2);
        test_cache.updateCachedVitalsList(VitalSignType.BLOOD_PRESSURE, test_bp);

        assertEquals(1, test_cache.getCacheSize(VitalSignType.HEART_RATE));
        assertEquals(1, test_cache.getCacheSize(VitalSignType.RESPIRATION_RATE));
        assertEquals(1, test_cache.getCacheSize(VitalSignType.TEMPERATURE));
        assertEquals(1, test_cache.getCacheSize(VitalSignType.SPO2));
        assertEquals(1, test_cache.getCacheSize(VitalSignType.BLOOD_PRESSURE));

        //ToDo: add tests for manual vitals.

        test_cache.clearAll();

        assertEquals(0, test_cache.getCacheSize(VitalSignType.HEART_RATE));
        assertEquals(0, test_cache.getCacheSize(VitalSignType.RESPIRATION_RATE));
        assertEquals(0, test_cache.getCacheSize(VitalSignType.TEMPERATURE));
        assertEquals(0, test_cache.getCacheSize(VitalSignType.SPO2));
        assertEquals(0, test_cache.getCacheSize(VitalSignType.BLOOD_PRESSURE));
    }


    @Test
    public void MeasurementCache_getEarliestTimestampInCache()
    {
        // Test returns -1 if all lists empty
        assertEquals(-1, test_cache.getEarliestTimestampInCache());

        // Now populate the lists
        MeasurementHeartRate test_hr = new MeasurementHeartRate(100, 25000);
        MeasurementRespirationRate test_rr = new MeasurementRespirationRate(110, 24000);
        MeasurementTemperature test_temp = new MeasurementTemperature(12.0, 23000);
        MeasurementSpO2 test_nonin_spo2 = new MeasurementSpO2(130, 22000);
        MeasurementBloodPressure test_bp = new MeasurementBloodPressure(150, 160, 20000);

        test_cache.updateCachedVitalsList(VitalSignType.HEART_RATE, test_hr);
        test_cache.updateCachedVitalsList(VitalSignType.RESPIRATION_RATE, test_rr);
        test_cache.updateCachedVitalsList(VitalSignType.TEMPERATURE, test_temp);
        test_cache.updateCachedVitalsList(VitalSignType.SPO2, test_nonin_spo2);
        test_cache.updateCachedVitalsList(VitalSignType.BLOOD_PRESSURE, test_bp);
        // ToDo: Add more of these

        assertEquals(20000, test_cache.getEarliestTimestampInCache());
    }
}