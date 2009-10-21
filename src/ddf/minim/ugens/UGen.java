package ddf.minim.ugens;

import ddf.minim.AudioSignal;
import ddf.minim.AudioOutput;

public abstract class UGen implements AudioSignal 
{
	private UGen in;
	
	// TODO describe how this patching stuff works.
	/**
	 *
	 * @param output
	 * @return
	 */
	public UGen patch(UGen output)
	{
		output.in = this;
		return output;
	}
	
	public void patch(AudioOutput out)
	{
		out.addSignal(this);
	}

	public abstract void tick(float[] channels);
	
	// this is how we get back to the beginning of the ugen chain.
	// 
	private void ugentick(float[] channels)
	{
		if ( in != null )
		{
			in.ugentick(channels);
		}
		tick(channels);
	}
	
	
	public void generate(float[] mono)
	{
		float[] sample = new float[1];
		for(int i = 0; i < mono.length; i++)
		{
			sample[0] = 0;
			ugentick(sample);
			mono[i] = sample[0];
		}
	}
	
	public void generate(float[] left, float[] right)
	{
		float[] sample = new float[2];
		for(int i = 0; i < left.length; i++)
		{
			sample[0] = 0;
			sample[1] = 0;
			ugentick(sample);
			left[i] = sample[0];
			right[i] = sample[1];
		}
	}
}
