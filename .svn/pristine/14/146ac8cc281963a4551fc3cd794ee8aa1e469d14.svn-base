package com.isansys.patientgateway.algorithms;

import java.util.ArrayList;
import java.util.List;


public class OldRespirationRates {
	
	static class DataPair
    {
        double value1;
        double value2;
        
        DataPair()
        {
        	value1 = 0;
        	value2 = 0;
        }
    }
    
    public enum Slope
    {
        Decreasing,
        Stationary,
        Increasing
    }
    
    
// --Commented out by Inspection START (07/01/2016 18:32):
//    public static int FMP2P(List<DataPoint> Data)
//    {
//
//    	List<DataPoint> RR_intervals = DataPoint.getRRIntervals(Data);
//
//    	/* Now can just run the AMP2P algorithm on RR intervals */
//    	return AMP2P(RR_intervals);
//    }
// --Commented out by Inspection STOP (07/01/2016 18:32)

    
	public int AMP2P(List<DataPoint> data)
	{
		double resp_rate_result = 0.0;

        // Respiration rate algorithm.  Mean respiration rate is calculated from the mean time interval between
        // adjacent peaks in RS amplitude distribution. 
        double mean_period;
        List<DataPoint> Maxima;
        
        if(data.size() < 2)
        {
        	return 0;
        }
        
        	Maxima = Get_Discrete_Maxima(data);
        	
            if (Maxima.size() > 1)
            {
                mean_period = (double)(Maxima.get(Maxima.size() - 1).getTimestamp() - Maxima.get(0).getTimestamp()) / (Maxima.size() - 1);
                resp_rate_result = (double)60000 / (mean_period);
            }
            else
            {
                //throw new Exception(Maxima.size());
        }

		return (int)(resp_rate_result + 0.5);
	}
	

	
	private List<DataPoint> Get_Discrete_Maxima(List<DataPoint> data)
    {
		DataPair data_pair = new DataPair();
        List<DataPoint> maxima = new ArrayList<>();
        boolean Set_First_Slope = true;
        int n;
        Slope First_Slope = Slope.Stationary;
        Slope This_Slope;
        boolean first_max_detected;
        n = 0;
        first_max_detected = false;
        
        data_pair.value1 = data.get(n).getAmplitude();
        data_pair.value2 = data.get(n+1).getAmplitude();
        n++;
        
        while ((Get_Slope(data_pair) == Slope.Stationary) && (n < data.size() - 1))
        {
            data_pair.value1 = data.get(n).getAmplitude();
            data_pair.value2 = data.get(n+1).getAmplitude();
            n++;
        }
        
        while (n < data.size() - 1)
        {
            if (Set_First_Slope == true)
            {
                First_Slope = Get_Slope(data_pair);
            }
            This_Slope = First_Slope;
            while ((n < data.size() - 1) && (This_Slope == First_Slope))
            {
                data_pair.value1 = data.get(n).getAmplitude();
                data_pair.value2 = data.get(n+1).getAmplitude();
                n++;
                This_Slope = Get_Slope(data_pair);
            }
            while ((Get_Slope(data_pair) == Slope.Stationary) && (n < data.size() - 1))
            {
                data_pair.value1 = data.get(n).getAmplitude();
                data_pair.value2 = data.get(n+1).getAmplitude();
                n++;
                This_Slope = Get_Slope(data_pair);
            }

            if (n < data.size() - 1)
            {
                Set_First_Slope = false;
                if (Get_Slope(data_pair) == First_Slope)
                {
                    data_pair.value1 = data.get(n).getAmplitude();
                    data_pair.value2 = data.get(n+1).getAmplitude();
                    n++;
                    //This_Slope = Get_Slope(data_pair);
                }
                else
                {
                    if (This_Slope == Slope.Decreasing)
                    {
                    	DataPoint maximum = new DataPoint();
                        
                        maximum.setAmplitude(data.get(n - 1).getAmplitude());
                        maximum.setTimestamp(data.get(n - 1).getTimestamp());
                        maxima.add(maximum);
                    } // Remove 'if' condition for minimum and maximum detection

                    assert First_Slope != null;
                    switch (First_Slope)
                    {
                        case Increasing:
                            if (first_max_detected == false)
                            {
                                first_max_detected = true;
                            }
                            data_pair.value1 = data.get(n).getAmplitude();
                            data_pair.value2 = data.get(n+1).getAmplitude();
                            n++;
                            First_Slope = Slope.Decreasing;
                            break;
                        case Decreasing:
                            data_pair.value1 = data.get(n).getAmplitude();
                            data_pair.value2 = data.get(n+1).getAmplitude();
                            n++;
                            First_Slope = Slope.Increasing;
                            break;
					default:
						break;

                        /* There is a subtle step going on here - not sure if it's a bug or a clever but risky way of filtering.                */
                        /* After an extreme is found, Acquire_Discrete_Data() moves to the next pair of points after the extreme                */
                        /* and then the algorithm assumes that the slope will still be going in the same direction, WITHOUT measuring it.       */
                        /* Example: the signal goes up, down (we find a maximum), up (but algorithm assumes down),                              */
                        /* down (we should have found another maximum here, but it was missed as algorithm had up, down, down, down).           */
                        /* In this example, we've found only one of two peaks. This COULD be a problem...                                       */
                        /* BUT it does neatly filter out smaller fluctuations that could be noise. It just doesn't do it obviously.             */
                    }
                }
            }
        }
        return maxima;
    }


	private Slope Get_Slope(DataPair data_pair)
    {
		if(data_pair.value1 > data_pair.value2)
		{
			return Slope.Decreasing;
		}
		else if(data_pair.value1 == data_pair.value2)
		{
			return Slope.Stationary;
		}
		else if(data_pair.value1 < data_pair.value2)
		{
			return Slope.Increasing;
		}
		else
		{
			return null;
		}
	}
}
