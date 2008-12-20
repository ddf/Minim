package ddf.minim;

/**
 * @author Mark Godfrey <mark.godfrey@gatech.edu>
 */

public abstract class WavetableGenerator
{

	// generates piecewise linear waveforms
	// val are values of breakpoints with dist between them
	// NOTE: good for ADSR envelopes!!
	public static Wavetable gen7(int size, float[] val, int[] dist)
	{
		//System.out.println("gen7: " + size + ", " + val + ", " + dist);
		float[] waveform = new float[size];

		// check lenghts of arrays
		if (val.length - 1 != dist.length)
		{
			System.out.println("Input arrays of invalid sizes!");
			return null;
		}

		// check if size is sum of dists
		int sum = 0;
		for (int i = 0; i < dist.length; i++)
		{
			sum += dist[i];
		}
		if (size != sum)
		{
			System.out.println("Distances do not sum to size!");
			return null;
		}

		// waveform[0] = val[0];
		int i = 0;
		for (int j = 1; j < val.length; j++)
		{
			waveform[i] = val[j - 1];
			float m = (val[j] - val[j - 1]) / (float)(dist[j - 1]);
			for (int k = i + 1; k < i + dist[j - 1]; k++)
			{
				waveform[k] = m * (k - i) + val[j - 1];
			}
			i += dist[j - 1];
		}
		waveform[waveform.length - 1] = val[val.length - 1];

		// for(int n = 0; n < waveform.length; n++)
		// System.out.println(waveform[n]);

		return new Wavetable(waveform);
	}

	// generates waveform from lists of partials
	// phases are between 0 and 1
	public static Wavetable gen9(int size, float[] partial, float[] amp,	float[] phase)
	{

		if (partial.length != amp.length 
		 || partial.length != phase.length
		 || amp.length != phase.length)
		{
			System.err.println("Input arrays of different size!");
			return null;
		}

		float[] waveform = new float[size];

		float index = 0;
		for (int i = 0; i < size; i++)
		{
			index = (float)i / (size - 1);
			for (int j = 0; j < partial.length; j++)
			{
				waveform[i] += amp[j]
						* Math.sin(2 * Math.PI * partial[j] * index + phase[j]);
			}
		}

		return new Wavetable(waveform);
	}

	// generate waveforms from harmonic amplitude list
	public static Wavetable gen10(int size, float[] amp)
	{

		float[] waveform = new float[size];

		float index = 0;
		for (int i = 0; i < size; i++)
		{
			index = (float)i / (size - 1);
			for (int j = 0; j < amp.length; j++)
			{
				waveform[i] += amp[j] * Math.sin(2 * Math.PI * (j + 1) * index);
			}
		}

		return new Wavetable(waveform);
	}

}
