package com.isansys.patientgateway.algorithms;

/**
 * @author      Rory Morrison <rory.morrison @ isansys.com>
 *
 * DataPoint is a class to represent a heart beat sent from the Lifetouch, or an R-R interval when processing heart beats
 *
 */

public class DataPoint
{
    /**
     * Time of the data point in milliseconds
     */
    private long timestamp;

    /**
     * Amplitude of data point.
     */
    private double amplitude;


    /**
     * Default constructor
     */
    public DataPoint()
    {
        timestamp = 0;
        amplitude = 0;
    }

    /**
     * Constructor with input values
     * @param amplitude
     * @param timestamp
     */
    public DataPoint(double amplitude, long timestamp)
    {
        this.timestamp = timestamp;
        this.amplitude = amplitude;
    }

    /**
     * Getter for amplitude
     * @return amplitude
     */
    public double getAmplitude()
    {
        return amplitude;
    }

    /**
     * Setter for amplitude
     * @param amplitude
     */
    public void setAmplitude(double amplitude)
    {
        this.amplitude = amplitude;
    }

    /**
     * Setter for amplitude - casts an int to a double.
     * @param amplitude
     */
    public void setAmplitude(int amplitude)
    {
        this.amplitude = amplitude;
    }

    /**
     * Getter for timestamp
     * @return timestamp
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * Setter for timestamp
     * @param timestamp
     */
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}
