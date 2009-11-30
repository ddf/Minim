package ddf.minim.ugens;

import java.util.Arrays;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;

public abstract class UGen
{
	// jam3: enum is automatically static so it can't be in the nested class
	public enum SignalType { CONTROL, AUDIO };
	
	// jam3: declaring an inner nested class here
	class UGenInput
	{
		private int slot;
		private UGen incoming;
		private SignalType signalType;
	    UGenInput()
	    {
	    	// jam3: default to a 0 input
	    	this( 0 );
	    }
	    UGenInput(int sl)
	    {
	    	// TODO range checking on input slot number
	    	this( sl, SignalType.AUDIO );
	    }
	    UGenInput(int sl, SignalType st)
	    {
	    	// TODO range checking on input slot number
	    	slot = sl;
	    	signalType = st;
	    	uGenInputs[slot] = this;
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
	    // TODO Decide if it's a problem to give this a second name?
	    void addInput(UGen in)
	    {
	    	setIncomingUGen( in );
	    	System.out.println("UGenInput addInput called.");
	    	
	    }
	    void printInput()
	    {
	    	String typeLabel = null;
	    	String filled = null ; 
	    	switch (signalType)
	    	{
	    	case AUDIO :
	    		typeLabel = "AUDIO";
	    		break;
	    	case CONTROL :
	    		typeLabel = "CONTROL";
	    		break;	
	    	}
	    	if ( incoming == null)
	    	{
	    		filled = "not filled";
	    	} else {
	    		filled = "filled";
	    	}
	    		
	    	System.out.println("UGenInput: slot = " + slot + " signal = " + typeLabel + " " + filled );
	    }
	    //void setLastValues(float[] channels)
	    //{
	    //	if (lastValues == null )
	    //	{
	    //		System.out.println("lastValues is null!");
	    //		//lastValues = new float[channels.length];
	    //	}
	    //	if (channels == null )
	    //	{
	    //		System.out.println("channels is null!");
	    //		//lastValues = new float[channels.length];
	    //	}
	    //	for( int i=0; i<channels.length; i++)
	    //	{
	    //		lastValues[i] = channels[i];
	    //	}
	    //	//lastValues = Arrays.copyOf(channels, channels.length);
	    //}
	    //float[] getLastValues()
	    //{
	    // 	return lastValues;
	    //}
	}
	
    private UGenInput[] uGenInputs;
    private int nInputs;
	private float[] lastValues;
	
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
		System.out.println("number of inputs in this UGen = " + nInputs);
		uGenInputs = new UGenInput[nInputs];
		//for( int i=0; i<nInputs; i++)
		//{
		//	uGenInputs[i] = new UGenInput(i);
		//}
		lastValues = new float[2]; // NOT CORRECT
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
	
	// jam3: adding a patch method for inputs
	public final UGen patch(UGenInput connectToInput)
	{
		connectToInput.addInput(this);		
		return connectToInput.getOuterUGen();
	}
	
	// ddf: Protected because users of UGens should never call this directly.
	//      Sub-classes can override this to control what happens when something
	//      is patched to them. See the Bus class.
	protected void addInput(UGen input)
	{
		// TODO determine correct default behaviour for addInput
		// jam3: any default behavior here is going to be wrong
        //       for now, patch directly to the first slot if there is one
		System.out.println("UGen addInput called.");
		if (nInputs > 0)
		{
			//Call the method from the default UGenInput 
			uGenInputs[0].addInput(input);
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
			for( int i=0; i<nInputs; i++ )
			{		
				if (( uGenInputs[i] != null ) && ( uGenInputs[i].getIncomingUGen() != null ))
				{
					// TODO jam3: figure out how to change this for non-audio signals
					//uGenInputs[i].getIncomingUGen().tick(tmp[i]);
					//aUGenInput.setLastValues(tmp[i]);
					float[] tmp = new float[channels.length];
					uGenInputs[i].getIncomingUGen().tick(tmp);
					//System.out.println("ticked input " + uGenInputs[i].getSlot() + " value 0 = " + tmp[0] );
					//aUGenInput.setLastValues(channels);
				}
			}
			//channels = tmp[0];
		}
		uGenerate(channels);
		for( int i=0; i<channels.length; i++ )
		{		
			lastValues[i]=channels[i];
			//System.out.println(lastValues[i]);
		}
	}
	
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
			for( int i=0; i<nInputs; i++ )
			{		
				if (( uGenInputs[i] != null ) && ( uGenInputs[i].getIncomingUGen() != null ))
				{
					// TODO jam3: figure out how to change this for non-audio signals
					uGenInputs[i].getIncomingUGen().setSampleRate(newSampleRate);
				}
			}			
		}
	}
	
	public void printInputs()
	{
	   for( int i=0; i<nInputs; i++)
	   {
		   System.out.print("uGenInputs " + i + " ");
		   if ( uGenInputs[ i ] == null )
		   {
			   System.out.println("null");   
		   } else {
			   uGenInputs[ i ].printInput();
		   }
	   }
	}
}
