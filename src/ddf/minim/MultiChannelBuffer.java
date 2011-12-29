package ddf.minim;


public class MultiChannelBuffer 
{
	// TODO: consider just wrapping a FloatSampleBuffer
	private float[][]	channels;
	private int 		bufferSize;
	
	public MultiChannelBuffer(int bufferSize, int numChannels)
	{
		channels = new float[numChannels][bufferSize];
		this.bufferSize = bufferSize;
	}
	
	/**
	 * Copy the data in other to this MultiChannelBuffer.
	 * 
	 * @param other
	 * 			the buffer to copy
	 */
	public void set( MultiChannelBuffer other )
	{
		bufferSize = other.bufferSize;
		channels   = other.channels.clone();
	}
	
	public int getBufferSize()
	{
		return bufferSize;
	}
	
	public int getChannelCount()
	{
		return channels.length;
	}
	
	public float getSample( int channelNumber, int sampleIndex )
	{
		return channels[channelNumber][sampleIndex];
	}
	
	public float getSample( int channelNumber, float sample )
	{
		  int lowSamp = (int)sample;
		  int hiSamp = lowSamp + 1;
		  if ( hiSamp == bufferSize )
		  {
			  return channels[channelNumber][lowSamp];
		  }
		  float lerp = sample - lowSamp;
		  return channels[channelNumber][lowSamp] + lerp*(channels[channelNumber][hiSamp] - channels[channelNumber][lowSamp]);
	}
	
	public void setSample( int channelNumber, int sampleIndex, float value )
	{
		channels[channelNumber][sampleIndex] = value;
	}
	
	public float[] getChannel(int channelNumber)
	{
		return channels[channelNumber];
	}
	
	public void setChannel(int channelNumber, float[] samples)
	{
		System.arraycopy( samples, 0, channels[channelNumber], 0, bufferSize );
	}
	
	public void setChannelCount(int numChannels)
	{
		if ( channels.length != numChannels )
		{
			channels = new float[numChannels][bufferSize];
		}
	}
	
	public void setBufferSize(int bufferSize)
	{
		if ( this.bufferSize != bufferSize )
		{
			this.bufferSize = bufferSize;
			for( int i = 0; i < channels.length; ++i )
			{
				channels[i] = new float[bufferSize];
			}
		}
	}
}
