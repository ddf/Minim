package ddf.minim.ugens;

import java.util.Arrays;

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
	 */
	public UGenInput delAmp;

	// current delay time
	private float delayTime;
	// maximum delay time
	private float maxDelayTime;
	// current feedback factor
	private float amplitudeFactor;
	// the delay buffer based on maximum delay time
	private double[] delayBuffer;
	// the bufferSize stored for convenience
	private int bufferSize;
	// the index of the input and output of the buffer
	private int iBufferIn, iBufferOut;
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
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
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
		audio = new UGenInput( InputType.AUDIO );
		delTime = new UGenInput( InputType.CONTROL );
		delAmp = new UGenInput( InputType.CONTROL );
		this.maxDelayTime = maxDelayTime;
		this.amplitudeFactor = amplitudeFactor;
		this.feedBackOn = feedBackOn;
		this.passAudioOn = passAudioOn;
		delayTime = this.maxDelayTime;
		iBufferIn = 0;
		iBufferOut = 0;
		bufferSize = 0;
	}

	/**
	 * When the sample rate is changed the buffer needs to be resized.
	 * Currently this causes the allocation of a completely new buffer, but 
	 * since a change in sampleRate will result in a change in the playback
	 * speed of the sound in the buffer, I'm okay with this.
	 */
	protected void sampleRateChanged()
	{
		delayBuffer = new double [ (int)( maxDelayTime*sampleRate() ) ];
		Arrays.fill( delayBuffer, 0.0 );
		bufferSizeChanged();
	}
	
	// Recalculate the new bufferSize and make sure to clear out old data.
	private void bufferSizeChanged()
	{
		int oldBufferSize = bufferSize;
		int newBufferSize = (int)( delayTime * sampleRate() );
		if ( newBufferSize < oldBufferSize )
		{
			Arrays.fill( delayBuffer, newBufferSize, (int)( maxDelayTime*sampleRate() ), 0.0 );
		}
		bufferSize = newBufferSize;
		iBufferOut = ( iBufferIn + 1 )%bufferSize;
	}
	
    /**
     * changes the time in between the echos to the value specified.
     * @param delTime
     * 		It can be up to the maxDelayTime specified.
     * 		The lowest it can be is 1/sampleRate.	
     */
	public void setDelTime( float delTime )
	{
		this.delayTime = delTime;
		bufferSizeChanged();
	}
	
	/**
	 * changes the feedback amplification of the echos.
	 * @param delAmp
	 * 		This should normally be between 0 and 1 for decreasing feedback.
	 * 		Phase inverted feedback can be generated with negative numbers, but each echa will be the inverse
	 * 		of the one before it.
	 */
	public void setDelAmp( float delAmp )
	{
		this.amplitudeFactor = delAmp;
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		// mono-ize the signal
		float tmpIn = 0;
		for( int i = 0; i < channels.length; i++ )
		{
			tmpIn += audio.getLastValues()[ i ]/channels.length;
		}
		
		// pull sound out of the buffer
		float tmpOut = amplitudeFactor*(float)delayBuffer[ iBufferOut ];
		
		// put sound into the buffer
		delayBuffer[ iBufferIn ] = tmpIn;
		if ( feedBackOn ) 
		{
			delayBuffer[ iBufferIn ] +=tmpOut; 
		}
		
		// update the buffer indexes
		if ( delTime.isPatched() )
		{
			delayTime = delTime.getLastValues()[0];
			bufferSizeChanged();
		}
		iBufferIn = ( iBufferIn + 1 )%bufferSize;
		iBufferOut = ( iBufferOut + 1 )%bufferSize;
		
		// update the feedbackFactor
		if ( delAmp.isPatched() ) 
		{
			amplitudeFactor = delAmp.getLastValues()[0];
		}
			    
		// pass the audio if necessary
		if ( passAudioOn )
		{
			tmpOut += tmpIn;
		}
		
		// put the delay signal out on all channels
		for( int i = 0; i < channels.length; i++ )
		{
			channels[ i ] = tmpOut;
		}
	} 
}