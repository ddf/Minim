package ddf.minim.ugens;

import java.util.Random;

/**
 * @author Mark Godfrey <mark.godfrey@gatech.edu>
 */

// TODO Wavetable needs an add method
public class Wavetable implements Waveform
{

	protected float[]	waveform;

	public Wavetable(int size)
	{
		waveform = new float[size];
	}

	public Wavetable(float[] waveform)
	{
		this.waveform = waveform;
	}

	public void setWaveform(float[] waveform)
	{
		this.waveform = waveform;
	}

	public float get(int i)
	{
		return waveform[i];
	}
	
	/**
	 * At is expected to be in the range [0,1]. This will sample the waveform 
	 * using this value and interpolate between actual sample values as needed.
	 * 
	 * @param at
	 * @return
	 */
	public float value(float at)
	{
		float whichSample = (float)(waveform.length-1) * at;
		
		// linearaly interpolate between the two samples we want.
		int lowSamp = (int)Math.floor(whichSample);
		//int hiSamp = (int)Math.ceil(whichSample);
		int hiSamp = lowSamp + 1;
		
		float rem = whichSample - lowSamp;
		
		return get(lowSamp) + rem*(get(hiSamp) - get(lowSamp));
		//return get(lowSamp) * (1-rem) + get(hiSamp) * rem;
		// This was here for testing.  
		// Causes non-interpolation, but adds max # of oscillators
		//return get(lowSamp);
	}

	public float[] getWaveform()
	{
		return waveform;
	}

	public void set(int i, float f)
	{
		waveform[i] = f;
	}

	public int size()
	{
		return waveform.length;
	}

	public void scale(float a)
	{
		for (int i = 0; i < waveform.length; i++)
		{
			waveform[i] *= a;
		}
	}

	public void normalize()
	{
		float max = Float.MIN_VALUE;
		for (int i = 0; i < waveform.length; i++)
		{
			if (Math.abs(waveform[i]) > max)
				max = Math.abs(waveform[i]);
		}
		scale(1 / max);
	}

	public void invert()
	{
		flip(0);
	}

	public void flip(float in)
	{
		for (int i = 0; i < waveform.length; i++)
		{
			if (waveform[i] > in)
				waveform[i] = in - (waveform[i] - in);
			else
				waveform[i] = in + (in - waveform[i]);
		}
	}

//	public void fold(float in)
//	{
//		for (int i = 0; i < waveform.length; i++)
//		{
//			// if(waveform[i] < in) waveform[i] ;
//		}
//	}

	public void addNoise(float sigma)
	{
		Random rgen = new Random();
		for (int i = 0; i < waveform.length; i++)
		{
			waveform[i] += ((float)rgen.nextGaussian()) * sigma;
		}
	}

	public void rectify()
	{
		for (int i = 0; i < waveform.length; i++)
		{
			if (waveform[i] < 0)
				waveform[i] *= -1;
		}
	}

	public void smooth(int windowLength)
	{
		if (windowLength < 1)
			return;
		float[] temp = (float[])waveform.clone();
		for (int i = windowLength; i < waveform.length; i++)
		{
			float avg = 0;
			for (int j = i - windowLength; j <= i; j++)
			{
				avg += temp[j] / windowLength;
			}
			waveform[i] = avg;
		}
	}

}
