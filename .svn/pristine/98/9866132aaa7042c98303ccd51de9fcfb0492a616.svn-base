package com.isansys.patientgateway.algorithms;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rory on 30/09/2016.
 */

public class DataPointTest
{
    @Test
    public void DataPointTest_gettersAndSetters()
    {
        DataPoint test_data_point = new DataPoint(1, 2);

        assertEquals(1, test_data_point.getAmplitude(), 0.01);
        assertEquals(2, test_data_point.getTimestamp());

        test_data_point.setAmplitude(4);
        test_data_point.setTimestamp(5);

        assertEquals(4, test_data_point.getAmplitude(), 0.01);
        assertEquals(5, test_data_point.getTimestamp());
    }
}
