package ddf.mimin.javasound;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;
import ddf.minim.AudioMetaData;

public class JSMPEGAudioRecordingStream extends JSBaseAudioRecordingStream
{
	private AudioMetaData		meta;
	private AudioInputStream	encAis;

	JSMPEGAudioRecordingStream(AudioMetaData mdata, AudioInputStream encStream,
	                  			AudioInputStream decStream, SourceDataLine sdl, int bufferSize)
	{
		super(decStream, sdl, bufferSize, mdata.length());
		meta = mdata;
		encAis = encStream;
	}

	public AudioMetaData getMetaData()
	{
		return meta;
	}

	public int getMillisecondLength()
	{
		return meta.length();
	}
	
	protected int skip(int millis)
	{
		JSMinim.debug("Skipping forward by " + millis + " milliseconds.");
		long toSkip = AudioUtils.millis2BytesFrameAligned(millis, format);
		byte[] skipBytes = new byte[(int)toSkip];
		long totalSkipped = 0;
		try
		{
			// it's only able to read about 2 seconds at a time
			// so we've got to loop until we've skipped the requested amount
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
		JSMinim.debug("Total actually skipped was " + totalSkipped
				+ ", which is " + AudioUtils.bytes2Millis(totalSkipped, format)
				+ " milliseconds.");
		return (int)totalSkipped;
	}
	
	protected void rewind()
	{
		if (encAis.markSupported())
		{
			try
			{
				synchronized ( ais )
				{
					encAis.reset();
					// don't close the existing stream because it
					// is wrapping our encoded stream
					ais = (DecodedMpegAudioInputStream)JSMinim.getAudioInputStream(format, encAis);
				}
				return;
			}
			catch (Exception e)
			{
				JSMinim.error("Couldn't rewind using reset (" + e.getMessage()
						+ "), will try reloading the file.");
			}
		}

		// blah, close and reload
		synchronized ( ais )
		{
			try
			{
				ais.close();
			}
			catch (IOException e)
			{
				JSMinim.error("Couldn't close the stream for reloading: "
						+ e.getMessage());
			}
			AudioInputStream encIn = JSMinim.getAudioInputStream(meta.fileName());
			ais = (DecodedMpegAudioInputStream)JSMinim.getAudioInputStream(format, encIn);
		}
	}

}
