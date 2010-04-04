package ddf.minim.ugens;

import java.util.Random;

/**
 * @author Mark Godfrey <mark.godfrey@gatech.edu>
 */

public class Wavetable implements Waveform
{

	private float[]	waveform;
	// precalculate this since we use it alot
	private float   lengthForValue;

	public Wavetable(int size)
	{
		waveform = new float[size];
		lengthForValue = size - 1;
	}

	public Wavetable(float[] waveform)
	{
		this.waveform = waveform;
		lengthForValue = waveform.length - 1;
	}
	
	/**
	 * Make a new Wavetable that has the same waveform values as wavetable.
	 * @param wavetable
	 */
	public Wavetable( Wavetable wavetable )
	{
		waveform = new float[ wavetable.waveform.length ];
		System.arraycopy(wavetable.waveform, 0, waveform, 0, waveform.length);
		lengthForValue = waveform.length - 1;
	}

	public void setWaveform(float[] waveform)
	{
		this.waveform = waveform;
		lengthForValue = waveform.length - 1;
	}

	public float get(int i)
	{
		return waveform[i];
	}
	
	/**
	 * At is expected to be in the range [0,1]. This will sample the waveform 
	 * using this value and interpolate between actual sample values as needed.
	 * 
	 * @param at a value in the range [0, 1]
	 * @return this Wavetable sampled at the requested interval
	 */
	public float value(float at)
	{
		float whichSample = lengthForValue * at;
		
		// linearly interpolate between the two samples we want.
		int lowSamp = (int)whichSample;
		int hiSamp = lowSamp + 1;
		// lowSamp might be the last sample in the waveform
		// we need to make sure we wrap.
		if ( hiSamp >= waveform.length )
		{
			hiSamp -= waveform.length;
		}
		
		float rem = whichSample - lowSamp;
		
		return waveform[lowSamp] + rem*(waveform[hiSamp] - waveform[lowSamp]);

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
	
	/**
	 * Apply a DC offset to this Wavetable. In other words, add amount to every 
	 * sample.
	 * @param amount the amount to add to every sample in the table
	 */
	public void offset( float amount )
	{
		for(int i = 0; i < waveform.length; ++i)
		{
			waveform[i] += amount;
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
