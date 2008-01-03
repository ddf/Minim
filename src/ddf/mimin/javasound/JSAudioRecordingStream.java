/*
 *  Copyright (c) 2007 by Damien Di Fede <ddf@compartmental.net>
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

package ddf.mimin.javasound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;
import org.tritonus.share.sampled.FloatSampleBuffer;

import ddf.minim.AudioEffect;
import ddf.minim.AudioListener;
import ddf.minim.AudioMetaData;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;

final class JSAudioRecordingStream extends Thread implements
		AudioRecordingStream
{
	private AudioListener		listener;
	private AudioEffect			effect;
	private AudioMetaData				meta;

	// file reading stuff
	private long					lengthInMillis;
	private boolean				play;
	private boolean				loop;
	private int						numLoops;
	private AudioInputStream	ais;
	private byte[]					rawBytes;

	// line writing stuff
	private AudioFormat			format;
	private SourceDataLine		line;
	private FloatSampleBuffer	buffer;
	private int						bufferSize;
	private boolean				finished;

	JSAudioRecordingStream(AudioMetaData mdata, AudioInputStream stream, SourceDataLine sdl,
			int bufferSize)
	{
		super();
		format = sdl.getFormat();
		this.bufferSize = bufferSize;
		buffer = new FloatSampleBuffer(format.getChannels(), bufferSize,
													format.getSampleRate());
		Minim.debug("FloatSampleBuffer has " + buffer.getSampleCount()
				+ " samples.");
		finished = false;
		line = sdl;

		ais = stream;
		meta = mdata;
		lengthInMillis = meta.length(); 
			//
		play = loop = false;
		numLoops = 0;
		rawBytes = new byte[buffer.getByteArrayBufferSize(format)];
	}

	public void run()
	{
		try
		{
			line.open(format, bufferSize() * format.getFrameSize() * 4);
		}
		catch (LineUnavailableException e)
		{
			Minim.error("Error opening SourceDataLine: " + e.getMessage());
		}
		line.start();
		while (!finished)
		{
			// int size = buffer.getByteArrayBufferSize(ais.getFormat());
			rawBytes = new byte[rawBytes.length];

			try
			{
				// read bytes if we're playing
				if (play)
				{
					int bytesRead = ais.read(rawBytes, 0, rawBytes.length);
					// -1 means end of file
					if (bytesRead == -1)
					{
						if (loop)
						{
							// reset the stream, start playing
							if (numLoops == Clip.LOOP_CONTINUOUSLY)
							{
								rewind();
							}
							// reset the stream, start playing, decrement loop count
							else if (numLoops > 0)
							{
								rewind();
								numLoops--;
							}
							// otherwise just stop playing
							else
							{
								loop = false;
								play = false;
							}
						}
						else
						{
							play = false;
						}
					} // bytesRead == -1
				}
			}
			catch (IOException e)
			{
				Minim.error("AudioPlayer: error reading from the file - "
						+ e.getMessage());
			}

			// convert the bytes to floating point samples
			int frameCount = rawBytes.length / format.getFrameSize();
			buffer.setSamplesFromBytes(rawBytes, 0, format, 0, frameCount);

			// process the samples and broadcast them to our listeners
			if (line.getFormat().getChannels() == Minim.MONO)
			{
				float[] samp = buffer.getChannel(0);
				effect.process(samp);
				listener.samples(samp);
			}
			else
			{
				float[] sampL = buffer.getChannel(0);
				float[] sampR = buffer.getChannel(1);
				effect.process(sampL, sampR);
				listener.samples(sampL, sampR);
			}
			// finally convert them back to bytes and write to our line
			byte[] bytes = buffer.convertToByteArray(format);
			line.write(bytes, 0, bytes.length);
		}
		line.drain();
		line.stop();
		line.close();
		line = null;
	}

	public void play()
	{
		play = true;
		loop = false;
	}

	public boolean isPlaying()
	{
		return play;
	}

	public void pause()
	{
		play = false;
	}

	private void rewind()
	{
		try
		{
			ais.reset();
		}
		catch (IOException e)
		{
			Minim.error("Couln't rewind!");
		}
	}

	public void loop(int n)
	{
		loop = true;
		numLoops = n;
		play = true;
	}

	public int getMillisecondLength()
	{
		return (int)lengthInMillis;
	}

	public int getMillisecondPosition()
	{
		try
		{
			int availBytes = ais.available();
			int availMillis = (int)AudioUtils.bytes2Millis(availBytes, format);
			int pos = getMillisecondLength() - availMillis;
			return pos;
		}
		catch (IOException e)
		{
			Minim.error("Couldn't calculate position: " + e.getMessage());
		}
		return -1;
	}

	public void setMillisecondPosition(int millis)
	{
		if (millis < 0)
		{
			millis = 0;
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
				Minim.error("AudioPlayer: Error skipping: " + e.getMessage());
				setMillisecondPosition(currPos);
			}
			Minim.debug("Total actually skipped was " + read + ", which is "
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

	public void open()
	{
		start();
	}

	public void close()
	{
		finished = true;
	}

	public int bufferSize()
	{
		return bufferSize;
	}

	public AudioFormat getFormat()
	{
		return format;
	}

	public int getLoopCount()
	{
		return numLoops;
	}

	public AudioMetaData getMetaData()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setLoopPoints(int start, int stop)
	{
		// TODO Auto-generated method stub
	}

	public Control[] getControls()
	{
		return line.getControls();
	}

	public void setAudioEffect(AudioEffect effect)
	{
		this.effect = effect;
	}

	public void setAudioListener(AudioListener listener)
	{
		this.listener = listener;
	}
}
