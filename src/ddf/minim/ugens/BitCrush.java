package ddf.minim.ugens;

import ddf.minim.Minim;
import ddf.minim.UGen;

/**
 * BitCrush is an effect that reduces the fidelity of the incoming signal.
 * This results in a sound that is "crunchier" sounding, or "distorted". 
 * <p>
 * Audio is represented digitally (ultimately) as an integral value. If you 
 * have 16-bit audio, then you can represent a sample value with any number 
 * in the range -32,768 to +32,767. If you bit-crush this audio do be 8-bit,
 * then you effectively reduce it representation to -128 to +127, even though 
 * you will still represent it with a 16-bit number. This reduction in the 
 * fidelity of the representation essentially squares off the waveform, 
 * which makes it sound "crunchy". Try bit crushing down to 1-bit and see 
 * what you get!
 *  
 * @author Anderson Mills
 *
 */
public class BitCrush extends UGen
{
	// jam3: define the inputs to gain
    
	/**
	 * The audio input is where audio that gets bit-crushed should be patched. 
	 * However, you don't need to patch directly to this input, patching to
	 * the UGen itself will accomplish the same thing.
	 */
	public UGenInput audio;
	
	/**
	 * Control the bit resolution with another UGen by patching to bitRes. Values that 
	 * make sense for this start at 1 and go up to whatever the actual resolution of 
	 * the incoming audio is (typically 16).
	 */
	public UGenInput bitRes;
	
	/**
     * Control the bit rate with another UGen by patching to bitRate.
     * Values that make sense for this start at 1 and go up to whatever the
     * sample rate of your AudioOutput are (typically 44100)
     */
    public UGenInput bitRate;
	
	float[] sampledFrame;
	int    	sampleCounter;
	
	/**
	 * Construct a BitCrush with a bit resolution of 1 and a bit rate of 44100.
	 *
	 */
	public BitCrush()
	{
		this( 1.0f, 44100 );
	}
	
	/**
	 * Construct a BitCrush with the specified bit resolution and bit rate.
	 * 
	 * @param localBitRes typically you'll want this in the range [1,16]
	 * @param localBitRate this must be in the range [1,outputSampleRate] 
	 */
	public BitCrush( float localBitRes, float localBitRate )
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		//audio = new UGenInput(InputType.AUDIO);
		audio = new UGenInput(InputType.AUDIO);
		bitRes = new UGenInput(InputType.CONTROL);
		bitRes.setLastValue(localBitRes);
		bitRate = new UGenInput(InputType.CONTROL);
		bitRate.setLastValue( localBitRate );
		
		sampledFrame = new float[ getAudioChannelCount() ];
	}
	
	protected void channelCountChanged()
	{
		sampledFrame  = new float[ getAudioChannelCount() ];
		sampleCounter = 0;
		
		//System.out.println( "BitCrush now has " + getAudioChannelCount() + " channels." );
	}
	
	/**
	 * Set the bit resolution directly.
	 * 
	 * @param localBitRes typically you'll want this in the range [1,16]
	 */
	public void setBitRes(float localBitRes)
	{
		bitRes.setLastValue(localBitRes);
	}

	@Override
	protected void uGenerate(float[] out) 
	{
		if ( sampleCounter <= 0 )
		{
			if ( audio.getLastValues().length != getAudioChannelCount() )
			{
				Minim.error( "BitCrush audio has " + audio.getLastValues().length + " channels and sampledFrame has " + getAudioChannelCount()  );
			}
			System.arraycopy( audio.getLastValues(), 0, sampledFrame, 0, getAudioChannelCount() );
			sampleCounter = (int)(sampleRate() / Math.max(bitRate.getLastValue(),1));
		}
		
	    final int res       = 1 << (int)bitRes.getLastValue();
	    for( int i = 0; i < out.length; ++i )
	    {
	        int       samp      = (int)(res * sampledFrame[i]);
	        out[i]              = (float)samp/res;
	    }
	    
	    --sampleCounter;
	} 
}