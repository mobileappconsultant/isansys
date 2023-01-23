package com.isansys.patientgateway.algorithms;

import com.isansys.patientgateway.HeartBeatInfo;
import com.isansys.patientgateway.IntermediateMeasurement;
import com.isansys.patientgateway.remotelogging.RemoteLogging;

/**
 * @author      Rory Morrison <rory.morrison @ isansys.com>
 *
 *     OldHeartRate processes heart beat data to find the heart rate.
 */
class OldHeartRate
{
	private final RemoteLogging Log;

    /**
     * Constructor for Heart Rate algorithm
     *
     * @param passed_logger - used for logging a divide by zero exception during processing
     */
	public OldHeartRate(RemoteLogging passed_logger)
    {
        Log = passed_logger;
    }

    /**
     * Extracts a heart rate value from a minute of data
     *
     * Finds all R-R intervals in the minute, and then uses the mean R-R interval to find the
     * average heart rate during the minute.
     *
     * @param minute - data to process
     * @return heart rate value as an integer
     */
    public <T extends IntermediateMeasurement> int getHeartRate(MinuteOfData<T> minute)
	{
        double rate = 0;

        if(minute.data.size() < 2)
        {
        	return 0;
        }
        
	    try
	    {
    	    long rr_sum = 0;
			int rr_count = 0;
    	    double mean_rr;
    	    for (int index = 0; index < minute.data.size(); index++)
    	    {
				int current_rr = ((HeartBeatInfo)minute.data.get(index)).getRrInterval();

				if(current_rr > HeartBeatInfo.RR_NOT_VALID) // i.e. is 0 or more
				{
					rr_sum += current_rr;
					rr_count++;
				}
    	    }
    	    mean_rr = (double)rr_sum / (double)rr_count;
    	    rate = 60000 / mean_rr;
    	}
	    catch (ArithmeticException e)
	    {
	        Log.e("getHeartRate", "Divide by Zero");
	    }
	    
	    return (int)(rate + 0.5);
	}
}
