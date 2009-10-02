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

import ddf.minim.AudioMetaData;

class JSPCMAudioRecordingStream extends JSBaseAudioRecordingStream
{
	private AudioMetaData	meta;

	JSPCMAudioRecordingStream(JSMinim sys, AudioMetaData mdata, AudioInputStream stream,
			SourceDataLine sdl, int bufferSize)
	{
		super(sys, stream, sdl, bufferSize, mdata.length());
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
    // close and reload
    // because marking the thing such that you can play the
    // entire file without the mark being invalidated,
    // essentially means you are loading the file into memory
    // as it is played. which can mean out-of-memory for large files.
		try
		{
			synchronized ( ais )
			{
        ais.close();
        ais = system.getAudioInputStream(meta.fileName());
			}
		}
		catch (IOException e)
		{
		  system.error("Couldn't rewind!");
		}
	}
	
	protected int skip(int millis)
	{
		long toSkip = AudioUtils.millis2BytesFrameAligned(millis, format);
		system.debug("Skipping forward by " + millis + " milliseconds, which is " + toSkip + " bytes.");
		byte[] skipBytes = new byte[(int)toSkip];
		long totalSkipped = 0;
		try
		{
			while (totalSkipped < toSkip)
			{
				long read;
				synchronized ( ais )
				{
          // we don't use skip here because it sometimes has problems where
          // it's "unable to skip an integer number of frames",
          // which sometimes means it doesn't skip at all and other times
          // means that you wind up with noise because it lands at half
          // a sample off from where it should be. 
          // read seems to be rock solid.
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
		system.debug("Total actually skipped was " + totalSkipped + ", which is "
					+ AudioUtils.bytes2Millis(totalSkipped, ais.getFormat())
					+ " milliseconds.");
		return (int)totalSkipped;
	}

}
