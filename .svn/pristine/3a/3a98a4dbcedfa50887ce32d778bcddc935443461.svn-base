package com.isansys.patientgateway.algorithms;

import com.isansys.common.ErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class NewRespirationRates 
{
    public enum Slope
    {
        DECREASING,
        STATIONARY,
        INCREASING,
        NOT_SET
    }


    public double simplePeakDetect(List<DataPoint> Data)
    {
        Slope current_slope = Slope.NOT_SET;
        Slope previous_slope = Slope.NOT_SET;
        Slope next_slope = Slope.NOT_SET;
        int i;
        boolean first_run = true;
        int last_peak_index = 0;
        double resp_rate = 0;
    
        List<DataPoint> breaths = new ArrayList<>();
        
        if(Data.size() < 3)
        {
            return ErrorCodes.ERROR_CODE__NOT_ENOUGH_DATA_FOR_ALGORITHM;
        }

        for(i=1; i<Data.size(); i++)
        {
            double last_breath_amplitude = Data.get(i-1).getAmplitude();
            double current_breath_amplitude = Data.get(i).getAmplitude();

            if(last_breath_amplitude < current_breath_amplitude)
            {
                next_slope = Slope.INCREASING;
            }
            else if(last_breath_amplitude == current_breath_amplitude)
            {
                next_slope = Slope.STATIONARY;
            }
            else if(last_breath_amplitude > current_breath_amplitude)
            {
                next_slope = Slope.DECREASING;
            }

            if(next_slope != current_slope)
            {
                if(previous_slope != Slope.NOT_SET)
                {
                    /* Analyse the slopes                                                                                                           */
                    if((previous_slope == Slope.INCREASING && current_slope == Slope.STATIONARY && next_slope == Slope.DECREASING)
                            || (next_slope == Slope.DECREASING && current_slope == Slope.INCREASING)
                            || (current_slope == Slope.DECREASING && previous_slope == Slope.INCREASING && first_run))
                    {
                        /* Found a peak                                                                                                             */
                        if(first_run)
                        {
                            DataPoint breath = Data.get(i - 1);
                            
                            breaths.add(breath);
                            
                            last_peak_index = i;
                        }
                        else if(i >= last_peak_index)
                        {
                            DataPoint breath = Data.get(i - 1);
                            
                            breaths.add(breath);
                            
                            last_peak_index = i;
                        }
                        else
                        {
                            // Nothing to do
                        }
                    }
                    else
                    {
                        // Nothing to do
                    }
    
                    first_run = false;
                }
                else
                {
                    // Can't yet categorise properly...
                }
    
                /* Now update the current slope...                                                                                                  */
                previous_slope = current_slope;
                current_slope = next_slope;
            }
            else
            {
                // They are the same, so don't update
            }
        }
    

        try
        {
            if (breaths.size() > 1)
            {
                double mean_period = (double)(breaths.get(breaths.size() - 1).getTimestamp() - breaths.get(0).getTimestamp()) / (breaths.size() - 1);
                resp_rate = (double)60000 / (mean_period);
            }
        }
        catch (Exception E)
        {
            //Log.e("Exception. " + E.Message + E.StackTrace);
        }

        return resp_rate;
    }
}
