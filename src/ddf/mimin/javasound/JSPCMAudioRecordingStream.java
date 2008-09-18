package ddf.mimin.javasound;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;

import ddf.minim.AudioMetaData;

public class JSPCMAudioRecordingStream extends JSBaseAudioRecordingStream
{
	private AudioMetaData	meta;
  
  // TODO: test this with a really long WAV file ( larger than 40MB )

	JSPCMAudioRecordingStream(AudioMetaData mdata, AudioInputStream stream,
			SourceDataLine sdl, int bufferSize)
	{
		super(stream, sdl, bufferSize, mdata.length());
		meta = mdata;
	}

	public AudioMetaData getMetaData()
	{
		return meta;
	}

	public int getMillisecondLength()
	{
		return meta.length();
	}
	
	protected void rewind()
	{
		try
		{
			synchronized ( ais )
			{
				ais.reset();
			}
		}
		catch (IOException e)
		{
			JSMinim.error("Couldn't rewind!");
		}
	}
	
	protected int skip(int millis)
	{
		long toSkip = AudioUtils.millis2BytesFrameAligned(millis, format);
		//JSMinim.debug("Skipping forward by " + millis + " milliseconds, which is " + toSkip + " bytes.");
		byte[] skipBytes = new byte[(int)toSkip];
		long totalSkipped = 0;
		try
		{
			while (totalSkipped < toSkip)
			{
				int read;
				synchronized ( ais )
				{
					read = ais.read(skipBytes, 0, (int)(toSkip - totalSkipped));
				}
				if (read == -1)
				{
					// EOF!
					break;
				}
				totalSkipped += read;
			}
		}
		catch (IOException e)
		{
			JSMinim.error("Unable to skip due to read error: " + e.getMessage());
		}
		JSMinim.debug("Total actually skipped was " + totalSkipped + ", which is "
					+ AudioUtils.bytes2Millis(totalSkipped, ais.getFormat())
					+ " milliseconds.");
		return (int)totalSkipped;
	}

}
