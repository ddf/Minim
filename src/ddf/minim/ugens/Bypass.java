package ddf.minim.ugens;

/**
 * The Bypass UGen allows you to wrap another UGen and then insert that UGen into your
 * signal chain using Bypass in its place. You can then dynamically route the 
 * audio through the wrapped UGen or simply allow incoming audio to pass through unaffected. 
 * Using a Bypass UGen allows you to avoid concurrency issues caused by patching and unpatching 
 * during runtime from a Thread other than the audio one.
 * <p>
 * Your usage of Bypass might look something like this:
 * <p>
 * <pre>
 * Bypass granulate = new Bypass<GranulateSteady>( new GranulateSteady() );
 * filePlayer.patch( granulate ).patch( mainOut );
 * </pre>
 * <p>
 * If you needed to patch something else to one of the inputs of the GranulateSteady,
 * you'd use the <code>ugen</code> method of Bypass to retrieve the wrapped UGen
 * and operate on it:
 * <p>
 * <pre>
 * grainLenLine.patch( granulate.ugen().grainLen );
 * </pre>
 * <p>
 * Now, calling the <code>activate</code> method will <em>bypass</em> the granulate effect 
 * so that the Bypass object outputs the audio that is coming into it. Calling the 
 * <code>deactivate</code> method will route the audio through the wrapped effect. The 
 * <code>isActive</code> method indicates whether or not the wrapped effect is currently 
 * being bypassed.
 * 
 * @author Damien Di Fede
 *
 * @param <T> The type of UGen being wrapped, like GranulateSteady.
 */

public class Bypass<T extends UGen> extends UGen 
{
	private T mUGen;
	// do NOT allow people to patch directly to this!
	private UGenInput audio;
	
	private boolean mActive;
	
	public Bypass( T ugen )
	{
		mUGen = ugen;
		audio = new UGenInput();
		mActive = false;
	}
	
	public T ugen() 
	{
		return mUGen;
	}
	
	public void sampleRateChanged()
	{
		mUGen.setSampleRate( sampleRate() );
	}
	
	protected void addInput( UGen input )
	{
		audio.setIncomingUGen( input );
		input.patch( mUGen );
	}
	
	protected void removeInput( UGen input )
	{
		if ( audio.getIncomingUGen() == input )
		{
			audio.setIncomingUGen(null);
			input.unpatch( mUGen );
		}
		
	}
	
	public void activate()
	{
		mActive = true;
	}
	
	public void deactivate()
	{
		mActive = false;
	}
	
	public boolean isActive()
	{
		return mActive;
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		mUGen.tick(channels);
		
		// but stomp the result if we are active
		if ( mActive )
		{
			if ( audio.isPatched() )
			{
				System.arraycopy(audio.getLastValues(), 0, channels, 0, channels.length);
			}
			else
			{
				for(int i = 0; i < channels.length; ++i)
				{
					channels[i] = 0;
				}
			}
		}
	}

}
