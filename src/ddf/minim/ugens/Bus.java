package ddf.minim.ugens;

import java.util.ArrayList;

import ddf.minim.AudioOutput;
import ddf.minim.AudioSignal;

/**
 * A UGen that will group together other UGens and sum their
 * @author ddf
 *
 */
public class Bus extends UGen implements AudioSignal
{
	private AudioOutput out;
	ArrayList<UGen> ugens;
	
	public Bus()
	{
		ugens = new ArrayList<UGen>();
	}
	
	public Bus(AudioOutput output)
	{
		out = output;
		ugens = new ArrayList<UGen>();
	}
	
	public UGen patch(UGen input)
	{
		ugens.add(input);
		return this;
	}
	
	@Override
	protected void ugentick(float[] channels) 
	{
		for(int i = 0; i < ugens.size(); i++)
		{
			float[] tmp = new float[channels.length];
			ugens.get(i).tick(tmp);
			for(int c = 0; c < channels.length; c++)
			{
				channels[c] += tmp[c];
			}
		}
	}
	
	/**
	 * Generates a buffer of samples by ticking this UGen mono.length times. Like the 
	 * tick method, this will result in all of the 
	 */
	public void generate(float[] mono)
	{
		float[] sample = new float[1];
		for(int i = 0; i < mono.length; i++)
		{
			if ( out != null )
			{
				out.noteManager.tick();
			}
			sample[0] = 0;
			tick(sample);
			mono[i] = sample[0];
		}
	}
	
	public void generate(float[] left, float[] right)
	{
		float[] sample = new float[2];
		for(int i = 0; i < left.length; i++)
		{
			if ( out != null )
			{
				out.noteManager.tick();
			}
			sample[0] = 0;
			sample[1] = 0;
			tick(sample);
			left[i] = sample[0];
			right[i] = sample[1];
		}
	}

}
