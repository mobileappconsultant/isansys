package com.isansys.patientgateway.algorithms.filters;

/**
 * Created by Rory on 13/02/2018.
 */

public class LowPassSinglePole extends IIRFilter
{
    public LowPassSinglePole(float freq, float sampleRate)
    {
        super(freq, sampleRate);
    }

    @Override
    protected void calcCoeff() {
        float fracFreq = getFrequency() / getSampleRate();
//		float x = (float) Math.exp(-2 * Math.PI * fracFreq); // <- this is apparently an approximation, so using the below formula as described in https://dsp.stackexchange.com/questions/34969/cutoff-frequency-of-a-first-order-recursive-filter
        double omega = 2 * Math.PI * fracFreq;
        double quadratic_term = Math.pow(2 - Math.cos(omega), 2) - 1;
        float x = (float)( 2 - Math.cos(omega) -Math.sqrt(quadratic_term) );


        a = new float[] { 1 - x };
        b = new float[] { x };
    }
}