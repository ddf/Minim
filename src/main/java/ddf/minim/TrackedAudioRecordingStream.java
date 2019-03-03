package ddf.minim;

import ddf.minim.spi.AudioRecordingStream;

// internal class used to wrap AudioRecordingStream objects returned by the active MinimServiceProvider
// to enable Minim to release references to AudioRecordingStreams closed explicitly by the user.
final class TrackedAudioRecordingStream extends TrackedAudioStream<AudioRecordingStream> implements AudioRecordingStream
{
	
	public TrackedAudioRecordingStream(Minim owningSystem, AudioRecordingStream streamToWrap)
	{
		super(owningSystem, streamToWrap);
	}

	public void play()
	{
		stream.play();
	}

	public void pause()
	{
		stream.pause();
	}

	public boolean isPlaying()
	{
		return stream.isPlaying();
	}

	public void loop(int count)
	{
		stream.loop( count );
	}

	public void setLoopPoints(int start, int stop)
	{
		stream.setLoopPoints( start, stop );
	}

	public int getLoopCount()
	{
		return stream.getLoopCount();
	}

	public int getMillisecondPosition()
	{
		return stream.getMillisecondPosition();
	}

	public void setMillisecondPosition(int pos)
	{
		stream.setMillisecondPosition( pos );
	}

	public int getMillisecondLength()
	{
		return stream.getMillisecondLength();
	}

	public long getSampleFrameLength()
	{
		return stream.getSampleFrameLength();
	}

	public AudioMetaData getMetaData()
	{
		return stream.getMetaData();
	}

	public int getLoopBegin()
	{
		return stream.getLoopBegin();
	}

	public int getLoopEnd()
	{
		return stream.getLoopEnd();
	}
}
