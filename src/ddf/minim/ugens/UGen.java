package ddf.minim.ugens;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;

public abstract class UGen
{
	// jam3: enum is automatically static so it can't be in the nested class
	public enum InputType {CONTROL, AUDIO};
	
	// jam3: declaring an inner nested class here
	class UGenInput
	{
		private int slot;
		private UGen incoming;
		private InputType inputType;
		
	    UGenInput()
	    {
	    	// jam3: default to a slot 0 input
	    	this(0);
	    }
	    UGenInput(int sl)
	    {
	    	// TODO default to audio input?!?
	    	this(sl, InputType.AUDIO);
	    }
	    UGenInput(int sl, InputType st)
	    {
	    	slot = sl;
	    	inputType = st;
	    	try
	    	{
	    		uGenInputs[slot] = this;
	    	} catch (ArrayIndexOutOfBoundsException e) 
	    	{
	    	    System.err.println("Caught ArrayIndexOutOfBoundsException: " 
	    	    		+ e.getMessage());
	    	}
	    }
	    int getSlot()
	    {
	    	return slot;
	    }
	    UGen getOuterUGen()
	    {
	    	return UGen.this;
	    }
	    UGen getIncomingUGen()
	    {
	    	return incoming;
	    }
	    void setIncomingUGen(UGen in)
	    {
	    	incoming = in;
	    }
	    boolean isPatched()
	    {
	    	return (incoming != null);
	    }
	    String getTypeAsString()
	    {
	    	String typeLabel = null;
	    	switch (inputType)
	    	{
	    	case AUDIO :
	    		typeLabel = "AUDIO";
	    		break;
	    	case CONTROL :
	    		typeLabel = "CONTROL";
	    		break;	
	    	}
	    	return typeLabel;
	    }
	    void printInput()
	    {
	    	Minim.debug("UGenInput: slot = " + slot 
	    			+ " signal = " + getTypeAsString() + " " 
	    			+ isPatched() );
	    }
	}
	
    private UGenInput[] uGenInputs;
    private int nInputs;
	private float[] lastValues;
	protected float sampleRate;
	
	// TODO remove UGen empty constructor???
	// jam3: here as a placeholder until everything is converted
	//       to the new input array
	public UGen()
	{
		// TODO jam3: remove default of 1 input?
		this(1);
	}
	
	// jam3: initializes the inputUGens array with nIns "slots" for inputs
	public UGen(int nIns)
	{
		nInputs = nIns;
		Minim.debug("number of inputs in this UGen = " + nInputs);
		uGenInputs = new UGenInput[nInputs];
		// TODO How to set length of last values appropriately?
		// jam3: Using "2" here is wrong.  Could make ArrayList and set size with tick?
		lastValues = new float[2];
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
	public final UGen patch(UGen connectToUGen)
	{
		// was:	connectTo.in = this;
		// note that the default implementation of addInput 
		// does exactly the same thing.
		connectToUGen.addInput(this);
		return connectToUGen;
	}
	
	public final UGen patch(UGenInput connectToInput)
	{
		connectToInput.setIncomingUGen(this);		
		return connectToInput.getOuterUGen();
	}
	
	// ddf: Protected because users of UGens should never call this directly.
	//      Sub-classes can override this to control what happens when something
	//      is patched to them. See the Bus class.
	protected void addInput(UGen input)
	{
		// jam3: This default behavior is that an incoming signal will be
		// 		patched to slot 0.  This follows the decision that slot 0 
		//		is audio input if there is one.
		Minim.debug("UGen addInput called.");
		// TODO change nInputs checking to an Exception?
		if (nInputs > 0)
		{
			uGenInputs[0].setIncomingUGen(input);
		} else {
			Minim.debug("Tried to patch to a UGen with no inputs.");
		}
	}
	
	/**
	 * Patch the output of this UGen to the provided AudioOuput. Doing so will immediately 
	 * result in this UGen and all UGens patched into it to begin generating audio.
	 * 
	 * @param out The AudioOutput you want to connect this UGen to.
	 */
	public final void patch(AudioOutput output)
	{
		patch(output.bus);
		setSampleRate(output.sampleRate());
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
		if (nInputs > 0)
		{
			for(int i=0; i<nInputs; i++)
			{		
				if ((uGenInputs[i] != null) && (uGenInputs[i].isPatched()))
				{
					// TODO jam3: figure out how to change this for non-audio signals
					float[] tmp = new float[channels.length];
					uGenInputs[i].getIncomingUGen().tick(tmp);
				}
			}
		}
		uGenerate(channels);
		System.arraycopy(channels, 0, lastValues, 0, channels.length);
	}
	
	/**
	 * Implement this method when you extend UGen.
	 * @param channels
	 */
	protected abstract void uGenerate(float[] channels);
		
	float[] getLastValues()
	{
		return lastValues;
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
		if (sampleRate != newSampleRate)
		{
			sampleRate = newSampleRate;
			sampleRateChanged();
		}
		if (nInputs > 0)
		{
			for(int i=0; i<nInputs; i++)
			{		
				if ((uGenInputs[i] != null) && (uGenInputs[i].isPatched()))
				{
					uGenInputs[i].getIncomingUGen().setSampleRate(newSampleRate);
				}
			}			
		}
	}
	
	public void printInputs()
	{
	   for(int i=0; i<nInputs; i++)
	   {
		   Minim.debug("uGenInputs " + i + " ");
		   if (uGenInputs[i] == null)
		   {
			   Minim.debug("null");   
		   } else {
			   uGenInputs[i].printInput();
		   }
	   }
	}
}
