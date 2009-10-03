package ddf.minim;

import java.util.Random;

/**
 * @author Mark Godfrey <mark.godfrey@gatech.edu>
 */

// Hidden for 2.0.2. We may use this later, but there is no direct application at the moment.
class Wavetable
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
