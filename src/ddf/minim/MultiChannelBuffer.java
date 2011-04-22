package ddf.minim;

import java.util.ArrayList;

public class MultiChannelBuffer 
{
	// TODO: consider just wrapping a FloatSampleBuffer
	private ArrayList<MAudioBuffer> channels;
	private int bufferSize;
	
	public MultiChannelBuffer(int bufferSize, int numChannels)
	{
		channels = new ArrayList<MAudioBuffer>(numChannels);
		this.bufferSize = bufferSize;
		setChannelCount(numChannels);
	}
	
	public int getBufferSize()
	{
		return bufferSize;
	}
	
	public int getChannelCount()
	{
		return channels.size();
	}
	
	public float getSample( int channelNumber, int sampleIndex )
	{
		return channels.get( channelNumber ).get(  sampleIndex );
	}
	
	public float getSample( int channelNumber, float sample )
	{
		return channels.get( channelNumber ).get( sample );
	}
	
	public float[] getChannel(int channelNumber)
	{
		return channels.get(channelNumber).toArray();
	}
	
	public void setChannel(int channelNumber, float[] samples)
	{
		channels.get(channelNumber).set(samples);
	}
	
	public void setChannelCount(int numChannels)
	{
		if ( channels.size() != numChannels )
		{
			channels.clear();
			for(int i = 0; i < numChannels; i++)
			{
				channels.add( new MAudioBuffer(bufferSize) );
			}
		}
	}
	
	public void setBufferSize(int bufferSize)
	{
		if ( this.bufferSize != bufferSize )
		{
			this.bufferSize = bufferSize;
			int channelCount = channels.size();
			channels.clear();
			setChannelCount(channelCount);
		}
	}
}
