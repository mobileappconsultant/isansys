package com.isansys.patientgateway.algorithms.filters;

public class LowPassBiQuad extends IIRFilter
{
	public LowPassBiQuad(float freq, float sampleRate) 
	{
		super(freq, sampleRate);
	}

	@Override
	protected void calcCoeff() 
	{
		//ToDo: implement bi-quad coefficient calculations...
		double Q = 0.7071;
//		double peakGain = 0;
	    double norm;
	    double V = 1; //Math.pow(10, peakGain / 20.0);
	    
	    float Fc = getFrequency() / getSampleRate();
	    
	    double K = Math.tan(Math.PI * Fc);
		
		
        norm = 1 / (1 + K / Q + K * K);
        float a0 = (float)(K * K * norm);
        float a1 = 2 * a0;
        float a2 = a0;
        float b1 = (float)(2 * (K * K - 1) * norm);
        float b2 = (float)((1 - K / Q + K * K) * norm);
        
		a = new float[] { a0, a1, a2 };
		b = new float[] { -b1, -b2 };
	}
}
