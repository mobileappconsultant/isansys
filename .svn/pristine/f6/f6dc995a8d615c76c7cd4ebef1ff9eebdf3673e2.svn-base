package com.isansys.patientgateway.algorithms;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rory on 30/01/2018.
 */

public class Interpolation
{
    public static List<DataPoint> detrend(List<DataPoint> data)
    {
        SimpleRegression regression = new SimpleRegression();
        List<DataPoint> detrended_data = new ArrayList<>();

        int size = data.size();

        for (int i = 0; i < size; i++)
        {
            regression.addData(data.get(i).getTimestamp(), data.get(i).getAmplitude());
        }

        double slope = regression.getSlope();
        double intercept = regression.getIntercept();

        for (int i = 0; i < size; i++)
        {
            DataPoint point = new DataPoint();
            //y -= intercept + slope * x

            point.setAmplitude(data.get(i).getAmplitude() - (intercept + slope*data.get(i).getTimestamp()));
            point.setTimestamp(data.get(i).getTimestamp());

            detrended_data.add(point);
        }

        return detrended_data;
    }



    public static List<DataPoint> smoothedLinearInterpolate(List<DataPoint> data, int frequency, boolean filter)
    {
        int points_index = 0;
        int n = 0;
        int i;
        double delta_y;
        double delta_x;

        int samples_per_minute = frequency*60;
        int sampling_period_ms = 60000/samples_per_minute;


        List<DataPoint> interpolated_points = new ArrayList<>();

        // If not enough data in input list, return empty list.
        if(data.size() < 3)
        {
            return interpolated_points;
        }
        else
        {
            // Nothing to do
        }

        DataPoint first_data_point = new DataPoint();
        first_data_point.setAmplitude(data.get(0).getAmplitude());
        first_data_point.setTimestamp(data.get(0).getTimestamp());

        interpolated_points.add(first_data_point);

        for(i = 1; i < samples_per_minute; i++)
        {
            if(points_index + 1 >= data.size())
            {
                break;
            }
            else if((data.get(0).getTimestamp() + (i*sampling_period_ms)) >= data.get(points_index + 1).getTimestamp())
            {
                points_index++;
                n = 0;

                DataPoint data_point =  new DataPoint();
                data_point.setAmplitude(data.get(points_index).getAmplitude());
                data_point.setTimestamp(data.get(0).getTimestamp() + i*sampling_period_ms);

                interpolated_points.add(data_point);

            }
            else
            {
                n++;
				/*Linear interpolation here																												*/

                delta_y = data.get(points_index + 1).getAmplitude();
                delta_y -= data.get(points_index).getAmplitude();

                delta_x = data.get(points_index + 1).getTimestamp();
                delta_x -= data.get(points_index).getTimestamp();

                DataPoint data_point =  new DataPoint();
                data_point.setAmplitude(data.get(points_index).getAmplitude() + (n * sampling_period_ms * (delta_y)/(delta_x)));
                data_point.setTimestamp(data.get(0).getTimestamp() + i*sampling_period_ms);

                interpolated_points.add(data_point);
            }
        }


        if(filter)
        {
            List<DataPoint> filtered_points = new ArrayList<>();

            for(int j = 0; j < interpolated_points.size(); j ++)
            {
                int bottom = 0;
                int top = interpolated_points.size() - 1;

                if(j > 3)
                {
                    bottom = j - 3;
                }

                if(j < (interpolated_points.size() - 3))
                {
                    top = j + 3;
                }

                double sum = 0;
                for(int k = bottom; k <= top; k++)
                {
                    sum += interpolated_points.get(k).getAmplitude();
                }
                sum /= (top - bottom + 1);

                DataPoint filtered_point = new DataPoint((int)(sum), interpolated_points.get(j).getTimestamp());

                filtered_points.add(filtered_point);

            }

            return filtered_points;
        }
        else
        {
            return interpolated_points;
        }
    }
}
