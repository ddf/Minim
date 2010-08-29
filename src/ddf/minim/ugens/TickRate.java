package ddf.minim.ugens;

public class TickRate extends UGen 
{
	private UGen audio;
	
	public UGenInput value;

	
	private float[] currentSample;
	private float[] nextSample;
	private float   sampleCount;
	private boolean	bInterpolate;
	
	public TickRate()
	{
		this( 1.f );
	}
	
	public TickRate( float tickRate )
	{
		value = new UGenInput(InputType.CONTROL);
		value.setLastValue(tickRate);
		sampleCount = 0.f;
		currentSample = new float[2];
		nextSample = new float[2];
		bInterpolate = false;
	}
	
	public void setInterpolation( boolean doInterpolate )
	{
		bInterpolate = doInterpolate;
	}
	
	@Override
	protected void addInput( UGen in )
	{
		audio = in;
		audio.setAudioChannelCount(currentSample.length);
	}
	
	@Override
	protected void removeInput( UGen in )
	{
		if ( audio == in )
		{
			audio = null;
		}
	}
	
	@Override
	protected void sampleRateChanged()
	{
		if ( audio != null )
		{
			audio.setSampleRate(sampleRate());
		}
	}
	
	@Override
	public void setAudioChannelCount( int numberOfChannels )
	{
		if ( currentSample.length != numberOfChannels )
		{
			super.setAudioChannelCount(numberOfChannels); 
			
			currentSample = new float[numberOfChannels];
			nextSample = new float[numberOfChannels];
		
			if ( audio != null )
			{
				audio.setAudioChannelCount(numberOfChannels);
				audio.tick(currentSample);
				audio.tick(nextSample);
				sampleCount = 0;
			}
		}
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		float sampleStep = value.getLastValue();
		
		// for 0 or negative rate values, we just stop generating audio
		// effectively pausing generation of the patched ugen.
		if ( sampleStep <= 0.f )
		{
			for(int i = 0; i < channels.length; ++i)
			{
				channels[i] = 0.f;
			}
			
			return;
		}
		
		if ( bInterpolate )
		{
			for(int i = 0; i < channels.length; ++i)
			{
				float sampleDiff = nextSample[i] - currentSample[i];
				channels[i] = currentSample[i] + sampleDiff * sampleCount;
			}
		}
		else
		{
			System.arraycopy(currentSample, 0, channels, 0, channels.length);
		}
		
		if ( audio != null )
		{
			sampleCount += sampleStep;
			
			while( sampleCount >= 1.f )
			{
				System.arraycopy(nextSample, 0, currentSample, 0, nextSample.length);
				audio.tick(nextSample);
				sampleCount -= 1.f;
			}
		}
	}

}
