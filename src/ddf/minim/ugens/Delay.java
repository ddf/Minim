package ddf.minim.ugens;

import java.util.Arrays;

import ddf.minim.UGen;


/**
 * The Delay UGen is used to create delayed repetitions of the input audio.
 * One can control the delay time and amplification of the repetition.
 * One can also choose whether the repetition is fed back and/or the input is passed through.
 * @author J Anderson Mills III
 */
public class Delay extends UGen
{
	/** 
	 * audio is the incoming audio
	 */
	public UGenInput audio;
	/**
	 * delTime is the time for delay between repetitions.
	 */
	public UGenInput delTime;
	/**
	 * delAmp is the strength of each repetition compared to the previous. 
	 * often labeled as feedback on delay units.
	 */
	public UGenInput delAmp;

	// maximum delay time
	private float maxDelayTime;
	// the delay buffer based on maximum delay time
	private double[] delayBuffer;
	// how many sample frames does the delay buffer hold
	private int	     delayBufferFrames;
	// the index where we pull sound out of the delay buffer
	private int iBufferOut;
	// flag to include continual feedback.
	private boolean feedBackOn;
	// flag to pass the audio straight to the output.
	private boolean passAudioOn;
	
	// constructors
	/**
	 * Constructor for Delay.
	 * @param maxDelayTime
	 * 		is the maximum delay time for any one echo.  This defaults to 0.25s. 
	 * @param amplitudeFactor
	 *      is the amplification factor for feedback and should generally be from 0 to 1.
	 *		This defaults to 0.5.
	 * @param feedBackOn
	 * 		is a boolean flag specifying if the repetition continue to feed back.
	 *		The default value is false.
	 * @param passAudioOn
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
	 *		This defaults to true.
	 */
	public Delay()
	{
		this( 0.25f, 0.5f, false, true );
	}
	
	/**
	 * Constructor for Delay.
	 * @param maxDelayTime
	 * 		is the maximum delay time for any one echo. 
	 * @param amplitudeFactor
	 * 	 	is the amplification factor for feedback and should generally be from 0 to 1.
	 *		This defaults to 0.5.
	 * @param feedBackOn
	 * 		is a boolean flag specifying if the repetition continue to feed back.
	 *		The default value is false.
	 * @param passAudio
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
	 *		This defaults to true.
	 */
	public Delay( float maxDelayTime )
	{
		this( maxDelayTime, 0.5f, false, true );
	}
	
	/**
	 * Constructor for Delay.
	 * @param maxDelayTime
	 * 		is the maximum delay time for any one echo and the default echo time. 
	 * @param amplitudeFactor
	 *      is the amplification factor for feedback and should generally be from 0 to 1.
	 * @param feedBackOn
	 * 		is a boolean flag specifying if the repetition continue to feed back.
	 *		The default value is false.
	 * @param passAudioOn
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
	 *		This defaults to true.
	 */
	public Delay( float maxDelayTime, float amplitudeFactor )	
	{	
		this( maxDelayTime, amplitudeFactor, false, true );
	}
	
	/**
	 * Constructor for Delay.
	 * @param maxDelayTime
	 * 		is the maximum delay time for any one echo and the default echo time. 
	 * @param amplitudeFactor
	 *      is the amplification factor for feedback and should generally be from 0 to 1.
	 * @param feedBackOn
	 * 		is a boolean flag specifying if the repetition continue to feed back.
	 * @param passAudioOn
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
	 *		This defaults to true.
	 */
	public Delay( float maxDelayTime, float amplitudeFactor, boolean feedBackOn )	
	{	
		this( maxDelayTime, amplitudeFactor, feedBackOn, true );
	}	
	
	/**
	 * Constructor for Delay.
	 * @param maxDelayTime
	 * 		is the maximum delay time for any one echo and the default echo time. 
	 * @param amplitudeFactor
	 *      is the amplification factor for feedback and should generally be from 0 to 1.
	 * @param feedBackOn
	 * 		is a boolean flag specifying if the repetition continue to feed back.
	 * @param passAudioOn
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
	 */
	public Delay( float maxDelayTime, float amplitudeFactor, boolean feedBackOn, boolean passAudioOn )	
	{		
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		audio = addAudio();
		
		// time members
		this.maxDelayTime = maxDelayTime;
		delTime = addControl( maxDelayTime );
		
		// amplitude member
		delAmp = addControl( amplitudeFactor );

		// flags
		this.feedBackOn = feedBackOn;
		this.passAudioOn = passAudioOn;

		iBufferOut = 0;
	}

	/**
	 * When the sample rate is changed the buffer needs to be resized.
	 * Currently this causes the allocation of a completely new buffer, but 
	 * since a change in sampleRate will result in a change in the playback
	 * speed of the sound in the buffer, I'm okay with this.
	 */
	protected void sampleRateChanged()
	{
		delayBufferFrames = (int)( maxDelayTime*sampleRate() );
		delayBuffer = new double [ delayBufferFrames*audio.channelCount() ];
		iBufferOut = 0;
	}
	
	public void setAudioChannelCount( int numberOfChannels )
	{
		super.setAudioChannelCount( numberOfChannels );
		
		delayBufferFrames = (int)( maxDelayTime*sampleRate() );
		delayBuffer = new double [ delayBufferFrames*audio.channelCount() ];
		iBufferOut = 0;
	}
	
    /**
     * changes the time in between the echos to the value specified.
     * @param delTime
     * 		It can be up to the maxDelayTime specified.
     * 		The lowest it can be is 1/sampleRate.	
     */
	public void setDelTime( float delayTime )
	{
		delTime.setLastValue( delayTime );
	}
	
	/**
	 * changes the feedback amplification of the echos.
	 * @param delayAmplitude
	 * 		This should normally be between 0 and 1 for decreasing feedback.
	 * 		Phase inverted feedback can be generated with negative numbers, but each echo 
	 * 		will be the inverse of the one before it.
	 */
	public void setDelAmp( float delayAmplitude )
	{
		delAmp.setLastValue( delayAmplitude );
	}

	@Override
	protected void uGenerate(float[] channels) 
	{	
		if ( delayBuffer == null || delayBuffer.length == 0 )
		{
			Arrays.fill( channels, 0 );
			return;
		}
		
		// how many samples do we delay the input
		int delay = (int)(delTime.getLastValue()*sampleRate());
		int channelCount = getAudioChannelCount();
		for( int i = 0; i < channelCount; ++i )
		{
			float in  = audio.getLastValues()[i];
			
			// pull sound out of the delay buffer
			int outSample = iBufferOut*channelCount + i;
			float out = delAmp.getLastValue()*(float)delayBuffer[ outSample ];
			// eat it
			delayBuffer[ outSample ] = 0;
			
			// put sound into the buffer
			int inFrame  = (iBufferOut+delay)%delayBufferFrames;
			int inSample = ( inFrame*channelCount + i);
			delayBuffer[ inSample ] = in;
			
			if ( feedBackOn )
			{
				delayBuffer[ inSample ] += out;
			}
			
			if ( passAudioOn )
			{
				out += in;
			}
			
			channels[i] = out;
		}
		
		iBufferOut = (iBufferOut + 1) % delayBufferFrames;
	} 
}