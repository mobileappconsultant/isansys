package com.isansys.patientgateway.algorithms;

import com.isansys.patientgateway.IntermediateMeasurement;

import static com.isansys.common.ErrorCodes.ERROR_CODES;

class SimpleHeartRate
{
    /**
     * Constructor for Heart Rate algorithm
     *
     */
    public SimpleHeartRate()
    {
    }

    /**
     * Extracts a heart rate value from a minute of data.
     *
     * Heart rate is simply the number of heart beats contained in the minute.
     *
     * @param minute MinuteOfData to process
     * @return Heart rate as an integer.
     */
    public <T extends IntermediateMeasurement> int getHeartRate(MinuteOfData<T> minute)
    {
        int num_beats = 0;

        for(T info : minute.data)
        {
            if(info.getAmplitude() < ERROR_CODES)
            {
                num_beats++;
            }
        }

        return num_beats;
    }
}
