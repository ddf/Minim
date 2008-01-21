package ddf.mimin.javasound;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;

import ddf.minim.AudioMetaData;
import ddf.minim.Minim;

public class JSPCMAudioRecordingStream extends JSBaseAudioRecordingStream
{
	private AudioMetaData	meta;

	JSPCMAudioRecordingStream(AudioMetaData mdata, AudioInputStream stream,
			SourceDataLine sdl, int bufferSize)
	{
		super(stream, sdl, bufferSize);
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

	public int getMillisecondPosition()
	{
		// TODO Auto-generated method stub
		try
		{
			int availBytes = ais.available();
			int availMillis = (int)AudioUtils.bytes2Millis(availBytes, format);
			int pos = getMillisecondLength() - availMillis;
			return pos;
		}
		catch (IOException e)
		{
			JSMinim.error("Couldn't calculate position: " + e.getMessage());
		}
		return -1;
	}

	public void setMillisecondPosition(int millis)
	{
		if (millis <= 0)
		{
			rewind();
			return;
		}
		if (millis > getMillisecondLength())
			millis = getMillisecondLength();
		if (millis > getMillisecondPosition())
		{
			skip(millis - getMillisecondPosition());
		}
		else
		{
			rewind();
			int bytes = (int)AudioUtils.millis2BytesFrameAligned(millis, format);
			long bytesRead = 0;
			try
			{
				bytesRead = ais.skip(bytes);
			}
			catch (IOException e)
			{
				Minim.error("AudioPlayer: Error setting cue point: "
						+ e.getMessage());
			}
			Minim.debug("Total actually skipped was " + bytesRead + ", which is "
					+ AudioUtils.bytes2Millis(bytesRead, ais.getFormat())
					+ " milliseconds.");
		}

	}
	
	private void rewind()
	{
		try
		{
			ais.reset();
		}
		catch (IOException e)
		{
			JSMinim.error("Couldn't rewind!");
		}
	}
	
	private void skip(int millis)
	{
		if (millis > 0)
		{
			// if it puts us past the end of the file, only skip what's left
			if (getMillisecondPosition() + millis > getMillisecondLength())
			{
				millis = getMillisecondLength() - getMillisecondPosition();
			}
			long bytes = AudioUtils.millis2BytesFrameAligned(millis,
																				ais.getFormat());
			long read = 0;
			int currPos = getMillisecondPosition();
			try
			{
				read = ais.skip(bytes);
			}
			catch (IOException e)
			{
				JSMinim.error("AudioPlayer: Error skipping: " + e.getMessage());
				setMillisecondPosition(currPos);
			}
			JSMinim.debug("Total actually skipped was " + read + ", which is "
					+ AudioUtils.bytes2Millis(read, ais.getFormat())
					+ " milliseconds.");
		}
		else if (millis < 0)
		{
			// to skip backwards we need to rewind
			// and then cue to the new position
			// remember that millis is negative, so we add
			if (getMillisecondPosition() > 0)
			{
				int pos = getMillisecondPosition() + millis;
				rewind();
				if (pos > 0)
				{
					setMillisecondPosition(pos);
				}
			}
		}
	}

}
