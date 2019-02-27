package ddf.minim;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;

import ddf.minim.spi.AudioStream;

// internal class used to wrap AudioStream objects returned by the active MinimServiceProvider
//to enable Minim to release references to AudioStreams closed explicitly by the user.
class TrackedAudioStream<T extends AudioStream> implements AudioStream
{
	private Minim system;
	protected T stream;
	
	public TrackedAudioStream( Minim owningSystem, T streamToWrap )
	{
		system = owningSystem;
		stream = streamToWrap;
	}

	public void open()
	{
		stream.open();
		system.addStream( stream );
	}

	public void close()
	{
		stream.close();
		system.removeStream( stream );
	}

	public Control[] getControls()
	{
		return stream.getControls();
	}

	public AudioFormat getFormat()
	{
		return stream.getFormat();
	}

	@SuppressWarnings("deprecation")
	public float[] read()
	{
		return stream.read();
	}

	public int read(MultiChannelBuffer buffer)
	{
		return stream.read(buffer);
	}
}
