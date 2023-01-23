package com.isansys.patientgateway.algorithms;

import com.isansys.patientgateway.algorithms.filters.HighPass;
import com.isansys.patientgateway.algorithms.filters.LowPassSinglePole;

import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rory on 13/02/2018.
 */

public class HeartRateFilteredRespirationCalculator
{
    public static double filteredPeakDetect(List<DataPoint> data, int heart_rate)
    {
        int breaths = 0;

        int resampling_rate = heart_rate/12; // gives 5 Hz for 60 bpm

        if(resampling_rate < 1)
        {
            resampling_rate = 1;
        }

        double cutoff_frequency = 0.4*heart_rate/60; // 1.5 times Nyquist limit, in Hz

        List<DataPoint> filtered_data = Interpolation.smoothedLinearInterpolate(data, resampling_rate, false); // no smoothing
        // Now de-trend
        filtered_data = Interpolation.detrend(filtered_data);

        LowPassSinglePole low_pass_1 = new LowPassSinglePole((float)cutoff_frequency, (float)resampling_rate);
        LowPassSinglePole low_pass_2 = new LowPassSinglePole((float)cutoff_frequency, (float)resampling_rate);
        LowPassSinglePole low_pass_3 = new LowPassSinglePole((float)cutoff_frequency, (float)resampling_rate);
        LowPassSinglePole low_pass_4 = new LowPassSinglePole((float)cutoff_frequency, (float)resampling_rate);

        /* Note the below HighPass filter setup looks like a bug, because we're passing in a constant 10 Hz sample rate rather than the
         * resampling_rate the other filters use. BUT the low pass filters have both fc and fs proportional to HR.
         * Inside the filter instance, the coefficients are based on a fractional frequency = fc/fs.
         *
         * So the below is equivalent to having a variable fc of HR/30 * 1/60 (or equally, the highpass fc / 12)
         * and then passing in the actual resampling frequency.
         *
         * This gives us an actual cutoff frequency equal to 4 breaths per minute at HR = 120 BPM,
         * 1 Br/min at HR = 30 BPM or 10 Br/min at HR = 300 BPM.
         *
         * This may or may not have originally been done on purpose, but since it's been tested like this,
         * and passed accuracy tests, it makes sense to leave it as it is.
         */
        HighPass high_pass = new HighPass((float)0.066666 , (float)10);

        high_pass.process(filtered_data);

        low_pass_1.process(filtered_data);
        low_pass_2.process(filtered_data);
        low_pass_3.process(filtered_data);
        low_pass_4.process(filtered_data);


        List<Extremum> extrema = simpleExtremaDetect(filtered_data);

        List<BreathFeature> deltas = getFullDifferencesArrayFromExtrema(extrema);	/* Constructs array of amplitude values from peak to trough and trough to peak */

        if(deltas.size() > 0)
        {

            Median median_calculator = new Median();
            double median = median_calculator.evaluate(BreathFeature.getAmplitudesArray(deltas));

            double lower_threshold = 0.2*median; /* 0.2 * 75th percentile of peak values */
            double upper_threshold = 6*median; /* 2 * 75th percentile of peak values */


            long start_time = -1;
            long end_time = -1;

            for(BreathFeature delta : deltas)
            {
                if((delta.amplitude > lower_threshold) && (delta.amplitude < upper_threshold))
                {
                    breaths++;

                    if(start_time < 0)
                    {
                        start_time = delta.start_time;
                    }

                    if(end_time < delta.end_time)
                    {
                        end_time = delta.end_time;
                    }
                }
            }
            double factor = ((double)(60000)) / ((double)(end_time - start_time));

            return (factor * breaths/2.0);
        }
        else
        {
            return -1;
        }
    }


    private static class BreathFeature
    {
        BreathFeature(double size, long start, long end)
        {
            amplitude = size;
            start_time = start;
            end_time = end;
        }

        public static double[] getAmplitudesArray(List<BreathFeature> data)
        {
            double [] return_array = new double[data.size()];

            for(int i = 0; i < data.size(); i++)
            {
                return_array[i] = data.get(i).amplitude;
            }

            return return_array;
        }

        long start_time;
        long end_time;

        double amplitude;
    }

    private static List<BreathFeature> getFullDifferencesArrayFromExtrema(List<Extremum> extrema)
    {
        int deltas_count = extrema.size() - 1;
        List<BreathFeature> return_list = new ArrayList<>();

        for(int i = 0; i < deltas_count; i++)
        {
            double point_one = extrema.get(i).Amplitude;
            double point_two = extrema.get(i + 1).Amplitude;
            long start_time = extrema.get(i).Timestamp;
            long end_time = extrema.get(i + 1).Timestamp;

            if(point_one > point_two)
            {
                return_list.add(new BreathFeature(point_one - point_two, start_time, end_time));
            }
            else
            {
                return_list.add(new BreathFeature(point_two - point_one, start_time, end_time));
            }
        }

        return return_list;
    }


    private enum Slope { NOT_SET, DECREASING, STATIONARY, INCREASING }

    private enum ExtremumType { UNKNOWN, PEAK, TROUGH }

    private static class Extremum
    {
        private ExtremumType Type;
        private double Amplitude;
        private long Timestamp;

        Extremum(ExtremumType type, double amplitude, long timestamp)
        {
            this.Type = type;
            this.Amplitude = amplitude;
            this.Timestamp = timestamp;
        }

        public ExtremumType getType()
        {
            return this.Type;
        }

        public void setType(ExtremumType type)
        {
            this.Type = type;
        }


        public double getAmplitude()
        {
            return this.Amplitude;
        }

        public void setAmplitude(double amplitude)
        {
            this.Amplitude = amplitude;
        }


        public long getTimestamp()
        {
            return this.Timestamp;
        }

        public void setTimestamp(long timestamp)
        {
            this.Timestamp = timestamp;
        }
    }

    public static List<Extremum> simpleExtremaDetect(List<DataPoint> Data)
    {
        Slope current_slope = Slope.NOT_SET;
        Slope previous_slope = Slope.NOT_SET;
        Slope next_slope = Slope.NOT_SET;
        boolean first_run = true;
        int i;

        List<Extremum> return_list = new ArrayList<>();

        if(Data.size() < 3)
        {
            return return_list;		/* Return the empty list */
        }
        else
        {
            // Nothing to do
        }

        for(i=1;i<Data.size();i++)
        {
            if(Data.get(i-1).getAmplitude() < Data.get(i).getAmplitude())
            {
                next_slope = Slope.INCREASING;
            }
            else if(Data.get(i-1).getAmplitude() == Data.get(i).getAmplitude())
            {
                next_slope = Slope.STATIONARY;
            }
            else if(Data.get(i-1).getAmplitude() > Data.get(i).getAmplitude())
            {
                next_slope = Slope.DECREASING;
            }
            else
            {
                // Logically never actually hit this...
            }

            if((next_slope != current_slope) || (first_run == true))
            {
                if(previous_slope != Slope.NOT_SET)
                {
					/*Analyse the slopes																												*/
                    if(previous_slope == Slope.INCREASING && current_slope == Slope.STATIONARY && next_slope == Slope.DECREASING)
                    {
						/*found a peak at i - 1																											*/
                        return_list.add(new Extremum(ExtremumType.PEAK, Data.get(i - 1).getAmplitude(), Data.get(i - 1).getTimestamp()));
                    }
                    else if(current_slope == Slope.INCREASING && next_slope == Slope.DECREASING )
                    {
						/*found a peak at i - 1																											*/
                        return_list.add(new Extremum(ExtremumType.PEAK, Data.get(i - 1).getAmplitude(), Data.get(i - 1).getTimestamp()));
                    }
                    else if(previous_slope == Slope.INCREASING && current_slope == Slope.DECREASING && first_run == true)
                    {
						/*found a peak at i - 2																											*/
                        return_list.add(new Extremum(ExtremumType.PEAK, Data.get(i - 2).getAmplitude(), Data.get(i - 2).getTimestamp()));
                    }
                    else if(previous_slope == Slope.DECREASING && current_slope == Slope.STATIONARY && next_slope == Slope.INCREASING)
                    {
						/*found a trough at i - 1																										*/
                        return_list.add(new Extremum(ExtremumType.TROUGH, Data.get(i - 1).getAmplitude(), Data.get(i - 1).getTimestamp()));
                    }
                    else if(current_slope == Slope.DECREASING && next_slope == Slope.INCREASING)
                    {
						/*found a trough at i - 1																										*/
                        return_list.add(new Extremum(ExtremumType.TROUGH, Data.get(i - 1).getAmplitude(), Data.get(i - 1).getTimestamp()));
                    }
                    else if(previous_slope == Slope.DECREASING && current_slope == Slope.INCREASING && first_run == true)
                    {
						/*found a trough at i - 2																										*/
                        return_list.add(new Extremum(ExtremumType.TROUGH, Data.get(i - 2).getAmplitude(), Data.get(i - 2).getTimestamp()));
                    }
                    else
                    {
                        // Not an extremum
                    }

                    first_run = false;
                }
                else
                {
                    // Can't yet categorise properly...
                }

				/*Now update the current slope...																										*/
                previous_slope = current_slope;
                current_slope = next_slope;
            }
            else
            {
                // Slopes are the same, so don't update
            }
        }

        return return_list;
    }
}
