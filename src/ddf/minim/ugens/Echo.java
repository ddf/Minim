package ddf.minim.ugens;

import java.util.Arrays;

import ddf.minim.Minim;
import ddf.minim.ugens.UGen.InputType;
import ddf.minim.ugens.UGen.UGenInput;

public class Echo extends UGen
{
	public UGenInput audio;
	public UGenInput delay;
	public UGenInput feedback;

	private float delayTime;
	private float maxDelayTime;
	private float feedbackFactor;
	private double[] delayBuffer;
	private int bufferSize;
	private int iBufferIn, iBufferOut;
	private boolean passAudio;
	
	// constructors
	public Echo()
	{
		this( 0.25f, 0.5f, true );
	}
	public Echo( float delayTime )
	{
		this( delayTime, 0.5f, true );
	}
	public Echo( float delayTime, float feedbackFactor, boolean passAudio )	
	{		
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		audio = new UGenInput( InputType.AUDIO );
		delay = new UGenInput( InputType.CONTROL );
		feedback = new UGenInput( InputType.CONTROL );
		this.delayTime = delayTime;
		this.feedbackFactor = feedbackFactor;
		this.passAudio = passAudio;
		maxDelayTime = this.delayTime;
		iBufferIn = 0;
		iBufferOut = 0;
		bufferSize = 0;
	}
	
	protected void sampleRateChanged()
	{
		delayBuffer = new double [ (int)( maxDelayTime*sampleRate ) ];
		Arrays.fill( delayBuffer, 0.0 );
		bufferSizeChanged();
	}
	protected void bufferSizeChanged()
	{
		//iBufferOut = (int)( delayTime * sampleRate ) - 1;
		int oldBufferSize = bufferSize;
		int newBufferSize = (int)( delayTime * sampleRate );
		if ( newBufferSize < oldBufferSize )
		{
			
			Arrays.fill( delayBuffer, newBufferSize, (int)( maxDelayTime*sampleRate ), 0.0 );
		}
		bufferSize = newBufferSize;
		iBufferOut = ( iBufferIn + 1 )%bufferSize;
		//Minim.debug("Delay: iBufferOut set to " + iBufferOut );
	}
	
	public void setDelay( float delayTime )
	{
		this.delayTime = delayTime;
		bufferSizeChanged();
	}
	public void setFeedback( float feedbackFactor )
	{
		this.feedbackFactor = feedbackFactor;
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		// monoize the signal
		float tmpIn = 0;
		for( int i = 0; i < channels.length; i++ )
		{
			tmpIn += audio.getLastValues()[i]/channels.length;
		}
		
		// pull sound out of the buffer
		float tmpOut = feedbackFactor*(float)delayBuffer[ iBufferOut ];
		
		// put sound into the buffer
		delayBuffer[ iBufferIn ] = tmpIn + tmpOut; 
		
		// update the buffer indexes
		if ( ( delay != null ) && ( delay.isPatched() ) )
		{
			delayTime = delay.getLastValues()[0];
			bufferSizeChanged();
		}
		iBufferIn = ( iBufferIn + 1 )%bufferSize;
		iBufferOut = ( iBufferOut + 1 )%bufferSize;
		if ( ( feedback != null ) && ( feedback.isPatched() ) )
		{
			feedbackFactor = feedback.getLastValues()[0];
		}
		
		//Minim.debug("iBufferIn, iBufferOut = " + iBufferIn +", " + iBufferOut );
		    
		// pass the audio if necessary
		if ( true == passAudio )
		{
			tmpOut += tmpIn;
		}
		
		// put the delay signal out on all channels
		for( int i = 0; i < channels.length; i++ )
		{
			channels[i] = tmpOut;
		}
	} 
}