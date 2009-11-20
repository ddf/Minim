package ddf.minim.ugens;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;

public abstract class UGen
{
    private UGen[] inputUGens;
    private int nInputs;
	
	// ddf: I don't believe this is being used for anything right now
	//      and I don't recall what I thought I'd need it for.
	protected AudioOutput out;
	
	protected float sampleRate;
	
	// TODO remove UGen empty constructor???
	// jam3: here as a placeholder until everything is converted
	//       to the new input array
	public UGen()
	{
		//TODO jam3: remove default of 1 input
		this( 1 );
	}
	
	/**
	 * 
	 */
	// jam3: initializes the inputUGens array with nIns "slots" for inputs
	public UGen(int nIns)
	{
		nInputs = nIns;
		inputUGens = new UGen[nInputs];
	}
	
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
	// ddf: this is final because we never want people to override it.
	public final UGen patch(UGen connectTo)
	{
		// was:	connectTo.in = this;
		// note that the default implementation of addInput 
		// does exactly the same thing.
		connectTo.addInput(this);
		return connectTo;
	}
	
	// ddf: Protected because users of UGens should never call this directly.
	//      Sub-classes can override this to control what happens when something
	//      is patched to them. See the Bus class.
    // jam3: In fact, all UGens should override this to specify where inputUGens go
	protected void addInput(UGen input)
	{
		// TODO determine correct default behaviour for addInput
		// jam3: any default behavior here is going to be wrong
        //       for now, patch directly to the first slot if there is one
		if ( nInputs > 0)
		{
			inputUGens[0] = input;
		} else {
			Minim.debug("Tried to patch to a UGen with no inputs.");
		}
	}
	
	// ddf: I don't think we need this anymore.
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
	public final void patch(AudioOutput output)
	{
		out = output;
		patch(out.bus);
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
		if ( nInputs > 0 )
		{
			for(UGen aUGen : inputUGens )
			{
				if ( aUGen != null )
				{
					// TODO jam3: figure out how to change this for non-audio signals
					aUGen.tick(channels);					
				}
			}
		}
		uGenerate(channels);
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
	protected abstract void uGenerate(float[] channels);
	
	/**
	 * Set the sample rate for this UGen.
	 * 
	 * @param newSampleRate the sample rate this UGen should generate at.
	 */
	// ddf: changed this to public because Bus needs to be able to call it
	//      on all of its UGens when it has its sample rate set by being connected 
	//      to an AudioOuput. Realized it's not actually a big deal for people to 
	//      set the sample rate of any UGen they create whenever they want. In fact, 
	//      could actually make total sense to want to do this with something playing 
	//      back a chunk of audio loaded from disk. Made this final because it should 
	//      never be overriden. If sub-classes need to know about sample rate changes 
	//      the should override sampleRateChanged()
	public final void setSampleRate(float newSampleRate)
	{
		if ( sampleRate != newSampleRate )
		{
			sampleRate = newSampleRate;
			sampleRateChanged();
		}
		if ( nInputs > 0 )
		{
			for(UGen aUGen : inputUGens )
			{
				if ( aUGen != null )
				{
					aUGen.setSampleRate(newSampleRate);
				}
			}
		}
	}
}
