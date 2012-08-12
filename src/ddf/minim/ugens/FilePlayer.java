package ddf.minim.ugens;

import java.util.Arrays;

import ddf.minim.AudioMetaData;
import ddf.minim.Minim;
import ddf.minim.Playable;
import ddf.minim.UGen;
import ddf.minim.spi.AudioRecordingStream;

/**
 * The FilePlayer UGen provides a way for you to wrap an AudioRecordingStream with 
 * the UGen interface, allowing you to patching into a UGen graph any way you choose.
 * You can get an AudioRecordingStream from Minim by calling Minim.loadFileStream.
 * 
 * @author Damien Di Fede
 *
 */

public class FilePlayer extends UGen implements Playable
{
	private AudioRecordingStream mFileStream;
	
	/**
	 * Construct a FilePlayer that will read from iFileStream.
	 * @param iFileStream the AudioRecordingStream this should read from
	 */
	public FilePlayer( AudioRecordingStream iFileStream )
	{
		mFileStream = iFileStream;
		// we'll need to do this eventually, I think.
		// but for now we don't need this because it starts the iothread,
		// which is not what we want.
		// mFileStream.open();	
		mFileStream.play();
	}
	
	/**
	 * Returns the wrapped AudioRecordingStream.
	 * 
	 * @return the wrapped AudioRecordingStream
	 */
	public AudioRecordingStream getStream()
	{
		return mFileStream;
	}
	
	public void play()
	{
		mFileStream.play();
	}

	public void play(int millis)
	{
		cue(millis);
		play();
	}

	public void pause()
	{
		mFileStream.pause();
	}

	public void rewind()
	{
		cue(0);
	}

	public void loop()
	{
		mFileStream.loop(Minim.LOOP_CONTINUOUSLY);
	}

	public void loop(int n)
	{
		mFileStream.loop(n);
	}

	public int loopCount()
	{
		return mFileStream.getLoopCount();
	}

	public int length()
	{
		return mFileStream.getMillisecondLength();
	}

	public int position()
	{
		return mFileStream.getMillisecondPosition();
	}

	public void cue(int millis)
	{
		if (millis < 0)
    {
			millis = 0;
    }
    else if (millis > length())
    {
			millis = length();
    }
		mFileStream.setMillisecondPosition(millis);
	}

	public void skip(int millis)
	{
		int pos = position() + millis;
		if (pos < 0)
    {
			pos = 0;
    }
		else if (pos > length())
    {
			pos = length();
    }
    Minim.debug("AudioPlayer.skip: skipping " + millis + " milliseconds, new position is " + pos);
    mFileStream.setMillisecondPosition(pos);
	}

	public boolean isLooping()
	{
		return mFileStream.getLoopCount() != 0;
	}

	public boolean isPlaying()
	{
		return mFileStream.isPlaying();
	}

	/**
	 * Returns the meta data for the recording being played by this player.
	 * 
	 * @return the meta data for this player's recording
	 */
	public AudioMetaData getMetaData()
	{
		return mFileStream.getMetaData();
	}

	public void setLoopPoints(int start, int stop)
	{
		mFileStream.setLoopPoints(start, stop);
	}
	
	/**
	 * Calling close will close the AudioStream that this wraps, 
	 * which is proper cleanup for using the stream.
	 */
	public void close()
	{
		mFileStream.close();
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		if ( mFileStream.isPlaying() )
		{
			float[] samples = mFileStream.read();
			// special case: mono expands out to all channels.
			if ( samples.length == 1 )
			{
				Arrays.fill(  channels, samples[0] );
			}
			// we have more than one channel, don't try to fill larger channel requests
			if ( samples.length <= channels.length )
			{
				System.arraycopy(samples, 0, channels, 0, samples.length);
			}
			// special case: we are stereo, output is mono.
			else if ( channels.length == 1 && samples.length == 2 )
			{
				channels[0] = (samples[0]+samples[1])/2.0f;
			}
		}
		else
		{
			Arrays.fill( channels, 0 );
		}
	}

}
