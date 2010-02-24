package ddf.minim.ugens;

import ddf.minim.Minim;
import ddf.minim.ugens.UGen.InputType;
import ddf.minim.ugens.UGen.UGenInput;

public class Delay extends UGen
{
	public UGenInput audio;
	public UGenInput delay;
	public UGenInput feedback;

	private float delayTime;
	private float maxDelayTime;
	private float feedbackFactor;
	private float[] delayBuffer;
	private int iBufferIn, iBufferOut;
	private boolean passOriginal;
	
	// constructors
	public Delay()
	{
		this( 0.25f, 0.5f, true );
	}
	public Delay( float delayTime )
	{
		this( delayTime, 0.5f, true );
	}
	public Delay( float delayTime, float feedbackFactor, boolean passOriginal )	
	{		
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		audio = new UGenInput( InputType.AUDIO );
		delay = new UGenInput( InputType.CONTROL );
		feedback = new UGenInput( InputType.CONTROL );
		this.delayTime = delayTime;
		this.feedbackFactor = feedbackFactor;
		this.passOriginal = passOriginal;
		maxDelayTime = this.delayTime;
		iBufferIn = 0;
		iBufferOut = 0;
	}
	
	protected void sampleRateChanged()
	{
		delayBuffer = new float [ (int)( maxDelayTime*sampleRate ) ];
		for( int i = 0; i<delayBuffer.length; i++)
		{
			delayBuffer[ i ] = 0.0f;
		}
		bufferSizeChanged();
	}
	protected void bufferSizeChanged()
	{
		//iBufferOut = (int)( delayTime * sampleRate ) - 1;
		iBufferOut = ( iBufferIn + 1 )%(int)( delayTime * sampleRate );
		Minim.debug("Delay: iBufferOut set to " + iBufferOut );
	}
	
	public void setDelay( float delayTime )
	{
		this.delayTime = delayTime;
		bufferSizeChanged();
	}
	public void setfeedback( float feedbackFactor )
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
		float tmpOut = feedbackFactor*delayBuffer[ iBufferOut ];
		
		// put sound into the buffer
		delayBuffer[ iBufferIn ] = tmpIn + tmpOut; 
		
		// update the buffer indexes
		iBufferIn = ( iBufferIn + 1 )%(int)( delayTime*sampleRate );
		iBufferOut = ( iBufferOut + 1 )%(int)( delayTime*sampleRate );
		//Minim.debug("iBufferIn, iBufferOut = " + iBufferIn +", " + iBufferOut );
		    //if ( ( amplitude == null ) || ( !amplitude.isPatched() ) )
			//{
			//	tmp *= value;
			//} else {
			//	tmp *= amplitude.getLastValues()[i];
			//}
		// pass the audio if necessary
		if ( true == passOriginal )
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