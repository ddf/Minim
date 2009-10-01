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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;

import ddf.minim.AudioMetaData;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecording;

// TODO: especially when rewound/played repeatedly, tends to start echoing bits and pieces and getting trapped...
// pretty sure this is fixed, but double check


class JSAudioRecording implements AudioRecording, Runnable
{
	private AudioMetaData		meta;
	private byte[] 				samples;
	private Thread					iothread;

	// reading stuff
	private boolean				play;
	private boolean				loop;
	private int						numLoops;
	// loop begin is in milliseconds
	private int						loopBegin;
	// loop end is in bytes
	private int						loopEnd;
	private byte[]					rawBytes;
	private int						totalBytesRead;

	// writing stuff
	protected AudioFormat		format;
	private SourceDataLine		line;
	private boolean				finished;

	JSAudioRecording(byte[] samps, SourceDataLine sdl, AudioMetaData mdata)
	{
		samples = samps;
		meta = mdata;
		format = sdl.getFormat();
		finished = false;
		line = sdl;
		loop = false;
		play = false;
		numLoops = 0;
		loopBegin = 0;
		loopEnd = (int)AudioUtils.millis2BytesFrameAligned(meta.length(), format);
		rawBytes = new byte[sdl.getBufferSize() / 8];
		iothread = null;
	}

	public void run()
	{
		while (!finished)
		{
			while (line.available() < rawBytes.length)
			{
				sleep(10);
			}
			if (play)
			{
				// read in a full buffer of bytes from the file
				if (loop)
				{
					readBytesLoop();
				}
				else
				{
					readBytes();
				}
				// write to the line until all bytes are written
				writeBytes();
			}
			// take a nap
			sleep(10);
		} // while ( !finished )
		line.drain();
		line.close();
		line = null;
	}

	private void sleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
		}
	}

	private synchronized void readBytes()
	{
		int samplesLeft = samples.length - totalBytesRead; 
		if ( samplesLeft  < rawBytes.length )
		{
			readBytes(samplesLeft, 0);
			for(int i = samplesLeft; i < rawBytes.length; i++)
			{
				rawBytes[i] = 0;
			}
			play = false;
		}
		else
		{
			readBytes(rawBytes.length, 0);
		}
	}

	private synchronized void readBytesLoop()
	{
		int toLoopEnd = loopEnd - totalBytesRead;
		if (toLoopEnd <= 0)
		{
			// whoops, our loop end point got switched up
			setMillisecondPosition(loopBegin);
			readBytesLoop();
			return;
		}
		if (toLoopEnd < rawBytes.length)
		{
			readBytes(toLoopEnd, 0);
			if (loop && numLoops == 0)
			{
				loop = false;
				play = false;
			}
			else if (loop)
			{
				setMillisecondPosition(loopBegin);
				readBytes(rawBytes.length - toLoopEnd, toLoopEnd);
				if (numLoops != Minim.LOOP_CONTINUOUSLY)
				{
					numLoops--;
				}
			}
		}
		else
		{
			readBytes(rawBytes.length, 0);
		}
	}

	// copy toRead bytes from samples to rawBytes,
	// starting at offet into rawBytes
	private void readBytes(int toRead, int offset)
	{
		System.arraycopy(samples, totalBytesRead, rawBytes, offset, toRead);
		totalBytesRead += toRead;
	}
	
	// TODO: apparently not handling end-of-file correctly because a user has reported clicks
	// http://processing.org/discourse/yabb_beta/YaBB.cgi?board=Sound;action=display;num=1226609644;start=0#3

	private void writeBytes()
	{
		// the write call will block until the requested amount of bytes
		// is written, however the user might stop the line in the
		// middle of writing and then we get told how much was actually written
		int actualWrit = line.write(rawBytes, 0, rawBytes.length);
		while (actualWrit != rawBytes.length)
		{
			// JSMinim.debug("Wanted to write " + rawBytes.length + ", actually
			// wrote " + actualWrit);
			// wait until there's room for the rest
			while (line.available() < rawBytes.length - actualWrit)
			{
				sleep(10);
			}
			// try again from where we left off
			actualWrit += line.write(rawBytes, actualWrit, rawBytes.length	- actualWrit);
		}
	}

	public void play()
	{
		line.start();
		loop = false;
		numLoops = 0;
		play = true;
	}

	public boolean isPlaying()
	{
		return play;
	}

	public void pause()
	{
		line.stop();
		play = false;
	}

	public void loop(int n)
	{
		loop = true;
		numLoops = n;
		play = true;
		setMillisecondPosition(loopBegin);
		line.start();
	}

	public void open()
	{
		iothread = new Thread(this);
		finished = false;
		iothread.start();
	}

	public void close()
	{
		line.stop();
		finished = true;
		try
		{
			iothread.join(10);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		iothread = null;
	}

	public AudioFormat getFormat()
	{
		return format;
	}

	public int getLoopCount()
	{
		return numLoops;
	}

	public synchronized void setLoopPoints(int start, int stop)
	{
		if (start <= 0 || start > stop)
		{
			loopBegin = 0;
		}
		else
		{
			loopBegin = start;
		}
		if (stop <= getMillisecondLength() && stop > start)
		{
			loopEnd = (int)AudioUtils.millis2BytesFrameAligned(stop, format);
		}
		else
		{
			loopEnd = (int)AudioUtils.millis2BytesFrameAligned(getMillisecondLength(), format);
		}
	}

	public int getMillisecondPosition()
	{
		return (int)AudioUtils.bytes2Millis(totalBytesRead, format);
	}

	public synchronized void setMillisecondPosition(int millis)
	{
		if (millis <= 0)
		{
			totalBytesRead = 0;
		}
		else if (millis > getMillisecondLength())
		{
			 totalBytesRead = samples.length;
		}
		else
		{
			totalBytesRead = (int)AudioUtils.millis2BytesFrameAligned(millis, format);
		}
	}

	public Control[] getControls()
	{
		return line.getControls();
	}

	public AudioMetaData getMetaData()
	{
		return meta;
	}

	public int getMillisecondLength()
	{
		return meta.length();
	}
}
