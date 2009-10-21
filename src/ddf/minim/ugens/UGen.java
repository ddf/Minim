package ddf.minim.ugens;

import ddf.minim.AudioSignal;
import ddf.minim.AudioOutput;

public abstract class UGen
{
	private UGen in;
	protected AudioOutput out;
	protected float sampleRate;
	
	// TODO describe how this patching stuff works.
	/**
	 * Connect the output of this UGen to the input of connectTo. Doing so will chain these 
	 * two UGens together, causing them to generate sound at the same time when the end of 
	 * chain is patched to an AudioOutput.
	 * 
	 * @param connectTo The UGen to connect to.
	 * @return connectTo is returned so that you can chain patch calls. For example: 
	 * <pre>
	 * sine.patch(gain).patch(out);
	 * </pre>
	 */
	public UGen patch(UGen connectTo)
	{
		connectTo.in = this;
		return connectTo;
	}
	
	public void patch(Frequency freq)
	{
		freq.useFrequency(this);
	}
	
	/**
	 * Patch the output of this UGen to the provided AudioOuput. Doing so will immediately 
	 * result in this UGen and all UGens patched into it to begin generating audio.
	 * 
	 * @param out The AudioOutput you want to connect this UGen to.
	 */
	public void patch(AudioOutput output)
	{
		out = output;
		out.bus.patch(this);
		setSampleRate(out.sampleRate());
	}
	
	/**
	 * Generates one sample frame for this UGen. 
	 * 
	 * @param channels
	 *    An array that represents one sample frame. To generate a mono signal, 
	 *    pass an array of length 1, if stereo an array of length 2, and so on.
	 *    How a UGen deals with multi-channel sound will be implementation dependent.
	 */
	public void tick(float[] channels)
	{
		if ( in != null )
		{
			in.tick(channels);
		}
		ugentick(channels);
	}
	
	/**
	 * Override this method in your derived class to receive a notification
	 * when the sample rate of your UGen has changed. You might need to do 
	 * this to recalculate sample rate dependent values, such as the 
	 * step size for an oscillator.
	 *
	 */
	protected void sampleRateChanged()
	{
		// default implementation does nothing.
	}
	
	
	/**
	 * Implement this method when you extend UGen.
	 * @param channels
	 */
	protected abstract void ugentick(float[] channels);
	
	private void setSampleRate(float sr)
	{
		if ( sampleRate != sr )
		{
			sampleRate = sr;
			sampleRateChanged();
		}
		if ( in != null )
		{
			in.setSampleRate(sr);
		}
	}
}
