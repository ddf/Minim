package ddf.minim.ugens;

import java.util.Arrays;

import ddf.minim.Minim;
import ddf.minim.ugens.UGen.InputType;
import ddf.minim.ugens.UGen.UGenInput;
/**
 * The Echo UGen is used to create repetitive echoes of the input audio.
 * It is a from of delay, specifically with feedback.
 * @author J Anderson Mills III
 */
public class Echo extends UGen
{
	/** 
	 * audio is the incoming audio
	 */
	public UGenInput audio;
	/**
	 * delay is the time for delay between echoes.
	 */
	public UGenInput delay;
	/**
	 * feedback is the strength of each repetition, including the first.
	 */
	public UGenInput feedback;

	// current delay time
	private float echoTime;
	// maximum delay time
	private float maxEchoTime;
	// current feedback factor
	private float feedbackFactor;
	// the echo buffer based on maximum delay time
	private double[] echoBuffer;
	// the bufferSize stored for convenience
	private int bufferSize;
	// the index of the input and output of the buffer
	private int iBufferIn, iBufferOut;
	// whether or not to pass the audio straight to the output.
	private boolean passAudio;
	
	// constructors
	/**
	 * Constructor for Echo.
	 * @param maxEchoTime
	 * 		is the maximum delay time for any one echo.  This defaults to 0.25s. 
	 * @param feedbackFactor
	 *      is the amplification factor for feedback and should generally be from 0 to 1.
	 *		This defaults to 0.5.
	 * @param passAudio
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
	 *		This defaults to true.
	 */
	public Echo()
	{
		this( 0.25f, 0.5f, true );
	}
	public Echo( float echoTime )
	/**
	 * Constructor for Echo.
	 * @param maxEchoTime
	 * 		is the maximum delay time for any one echo. 
	 * @param feedbackFactor
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
	 *		This defaults to 0.5.
	 * @param passAudio
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
	 *		This defaults to true.
	 */
	{
		this( echoTime, 0.5f, true );
	}
	/**
	 * Constructor for Echo.
	 * @param maxEchoTime
	 * 		is the maximum delay time for any one echo and the default echo time. 
	 * @param feedbackFactor
	 *      is the amplification factor for feedback and should generally be from 0 to 1.
	 * @param passAudio
	 * 	 	is a boolean value specifying whether to pass the input audio to the output as well.
	 */
	public Echo( float maxEchoTime, float feedbackFactor, boolean passAudio )	
	{		
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		audio = new UGenInput( InputType.AUDIO );
		delay = new UGenInput( InputType.CONTROL );
		feedback = new UGenInput( InputType.CONTROL );
		this.maxEchoTime = maxEchoTime;
		this.feedbackFactor = feedbackFactor;
		this.passAudio = passAudio;
		echoTime = this.maxEchoTime;
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
		echoBuffer = new double [ (int)( maxEchoTime*sampleRate ) ];
		Arrays.fill( echoBuffer, 0.0 );
		bufferSizeChanged();
	}
	// Recalculate the new bufferSize and make sure to clear out old data.
	private void bufferSizeChanged()
	{
		int oldBufferSize = bufferSize;
		int newBufferSize = (int)( echoTime * sampleRate );
		if ( newBufferSize < oldBufferSize )
		{
			Arrays.fill( echoBuffer, newBufferSize, (int)( maxEchoTime*sampleRate ), 0.0 );
		}
		bufferSize = newBufferSize;
		iBufferOut = ( iBufferIn + 1 )%bufferSize;
	}
    /**
     * changes the time in between the echos to the value specified.
     * @param delayTime
     * It can be up to the maxEchoTime specified.
     * The lowest it can be is 1/sampleRate.	
     */
	public void setDelay( float delayTime )
	{
		this.echoTime = delayTime;
		bufferSizeChanged();
	}
	/**
	 * changes the feedback amplification of the echos.
	 * @param feedbackFactor
	 * This should normally be between 0 and 1 for decreasing feedback.
	 * Phase inverted feedback can be generated with negative numbers, but each echa will be the inverse
	 * of the one before it.
	 */
	public void setFeedback( float feedbackFactor )
	{
		this.feedbackFactor = feedbackFactor;
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
		float tmpOut = feedbackFactor*(float)echoBuffer[ iBufferOut ];
		
		// put sound into the buffer
		echoBuffer[ iBufferIn ] = tmpIn + tmpOut; 
		
		// update the buffer indexes
		if ( ( delay != null ) && ( delay.isPatched() ) )
		{
			echoTime = delay.getLastValues()[0];
			bufferSizeChanged();
		}
		iBufferIn = ( iBufferIn + 1 )%bufferSize;
		iBufferOut = ( iBufferOut + 1 )%bufferSize;
		
		// update the feedbackFactor
		if ( ( feedback != null ) && ( feedback.isPatched() ) )
		{
			feedbackFactor = feedback.getLastValues()[0];
		}
			    
		// pass the audio if necessary
		if ( true == passAudio )
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