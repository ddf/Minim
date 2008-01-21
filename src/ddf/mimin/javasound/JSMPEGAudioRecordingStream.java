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
		super(decStream, sdl, bufferSize);
		meta = mdata;
		encAis = encStream;
	}

	public AudioMetaData getMetaData()
	{
		// TODO Auto-generated method stub
		return meta;
	}

	public int getMillisecondLength()
	{
		return meta.length();
	}

	public int getMillisecondPosition()
	{
		Long pos = (Long)((DecodedMpegAudioInputStream)ais).properties().get("mp3.position.microseconds");
		return (int)(pos.longValue() / 1000);
	}

	public void setMillisecondPosition(int millis)
	{
		if (millis <= 0)
		{
			rewind();
		}
		if (millis > getMillisecondLength())
			millis = getMillisecondLength();
		if (millis > getMillisecondPosition())
		{
			//JSMinim.debug("Skipping for cue.");
			skip(millis - getMillisecondPosition());
		}
		else
		{
			//JSMinim.debug("Rewind and skip for cue.");
			rewind();
			skip(millis);
		}
	}
	
	private void skip(int millis)
	{
		if (millis > 0)
		{
			JSMinim.debug("Skipping forward by " + millis + " milliseconds.");
			// if it puts us past the end of the file, only skip what's left
			if (getMillisecondPosition() + millis > getMillisecondLength())
			{
				millis = getMillisecondLength() - getMillisecondPosition();
			}
			JSMinim.debug("Skipping " + millis + " millis.");
			long toSkip = AudioUtils.millis2Bytes(millis, format);
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
						read = ais.read(skipBytes, 0, skipBytes.length);
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
		}
		else if (millis < 0)
		{
			JSMinim.debug("Skipping backwards by " + (-millis) + " milliseconds.");
			// to skip backwards we need to rewind
			// and then cue to the new position
			// remember that millis is negative, that's why we add
			if (getMillisecondPosition() > 0)
			{
				int pos = getMillisecondPosition() + millis;
				rewind();
				if (pos > 0)
				{
					skip(pos);
				}
			}
		}
	}
	
	private void rewind()
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
