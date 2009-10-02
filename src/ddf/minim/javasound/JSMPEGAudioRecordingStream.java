/*
 *  Copyright (c) 2007 - 2008 by Damien Di Fede <ddf@compartmental.net>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package ddf.minim.javasound;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;
import ddf.minim.AudioMetaData;


class JSMPEGAudioRecordingStream extends JSBaseAudioRecordingStream
{
	private AudioMetaData		meta;
	private AudioInputStream	encAis;

	JSMPEGAudioRecordingStream(JSMinim sys, AudioMetaData mdata, AudioInputStream encStream,
	                  			AudioInputStream decStream, SourceDataLine sdl, int bufferSize)
	{
		super(sys, decStream, sdl, bufferSize, mdata.length());
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
		system.debug("Skipping forward by " + millis + " milliseconds.");
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
          // we don't use skip here because it doesn't work with our decoder
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
			system.error("Unable to skip due to read error: " + e.getMessage());
		}
		system.debug("Total actually skipped was " + totalSkipped
				+ ", which is " + AudioUtils.bytes2Millis(totalSkipped, format)
				+ " milliseconds.");
		return (int)totalSkipped;
	}
	
	protected void rewind()
	{
		// close and reload
	  // because marking the thing such that you can play the
    // entire file without the mark being invalidated,
    // essentially means you are loading the file into memory
    // as it is played. which can mean out-of-memory for large files.
		synchronized ( ais )
		{
			try
			{
				ais.close();
			}
			catch (IOException e)
			{
				system.error("Couldn't close the stream for reloading: "
						+ e.getMessage());
			}
			AudioInputStream encIn = system.getAudioInputStream(meta.fileName());
			ais = (DecodedMpegAudioInputStream)system.getAudioInputStream(format, encIn);
		}
	}

}
