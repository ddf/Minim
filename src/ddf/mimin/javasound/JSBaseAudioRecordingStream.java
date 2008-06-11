package ddf.mimin.javasound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Control;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;
import org.tritonus.share.sampled.FloatSampleBuffer;

import ddf.minim.AudioEffect;
import ddf.minim.AudioListener;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;

public abstract class JSBaseAudioRecordingStream implements Runnable,
		AudioRecordingStream
{
	private Thread					iothread;
	private AudioListener		listener;
	private AudioEffect			effect;

	// reading stuff
	private boolean				play;
	private boolean				loop;
	private int						numLoops;
	// loop begin is in milliseconds
	private int						loopBegin;
	// loop end is in bytes
	private int						loopEnd;
	protected AudioInputStream	ais;
	private byte[]					rawBytes;
	private int						totalBytesRead;

	// writing stuff
	protected AudioFormat		format;
	private SourceDataLine		line;
	private FloatSampleBuffer	buffer;
	private int						bufferSize;
	private boolean				finished;
	private float[]				silence;

	JSBaseAudioRecordingStream(AudioInputStream stream, SourceDataLine sdl,
			int bufferSize, int msLen)
	{
		format = sdl.getFormat();
		this.bufferSize = bufferSize;
		buffer = new FloatSampleBuffer(format.getChannels(), bufferSize,
													format.getSampleRate());
		JSMinim.debug("FloatSampleBuffer has " + buffer.getSampleCount()
				+ " samples.");
		finished = false;
		line = sdl;

		ais = stream;
		loop = false;
		play = false;
		numLoops = 0;
		loopBegin = 0;
		loopEnd = (int)AudioUtils.millis2BytesFrameAligned(msLen, format);
		rawBytes = new byte[buffer.getByteArrayBufferSize(format)];
		silence = new float[bufferSize];
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
				// convert them to floating point
				// hand those arrays to our effect
				// and convert back to bytes
				process();
				// write to the line until all bytes are written
				writeBytes();
			}
			// send samples to the listener
			// these will be what we just put into the line
			// which means they should be pretty well sync'd
			// with the audible result
			broadcast();
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

	private void readBytes()
	{
		int bytesRead = 0;
		int toRead = rawBytes.length;
		try
		{
			while (bytesRead < toRead)
			{

				int actualRead = 0;
				synchronized (ais)
				{
					actualRead = ais.read(rawBytes, bytesRead, toRead - bytesRead);
					// JSMinim.debug("Wanted to read " + (toRead-bytesRead) + ",
					// actually read " + actualRead);
				}
				if (actualRead == -1)
				{
					pause();
					break;
				}
				else
				{
					bytesRead += actualRead;
				}
			}

		}
		catch (IOException e)
		{
			JSMinim.error("Error reading from the file - " + e.getMessage());
		}
		totalBytesRead += bytesRead;
	}

	private void readBytesLoop()
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
			readBytesWrap(toLoopEnd, 0);
			if (loop && numLoops == 0)
			{
				loop = false;
				pause();
			}
			else if (loop)
			{
				setMillisecondPosition(loopBegin);
				readBytesWrap(rawBytes.length - toLoopEnd, toLoopEnd);
				if (numLoops != Minim.LOOP_CONTINUOUSLY)
				{
					numLoops--;
				}
			}
		}
		else
		{
			readBytesWrap(rawBytes.length, 0);
		}
	}

	// read toRead bytes from ais into rawBytes.
	// we assume here that if we get to the end of the file
	// that we should wrap around to the beginning
	private void readBytesWrap(int toRead, int offset)
	{
		int bytesRead = 0;
		try
		{
			while (bytesRead < toRead)
			{

				int actualRead = 0;
				synchronized (ais)
				{
					actualRead = ais.read(rawBytes, bytesRead + offset, 
                                          toRead - bytesRead);
				}
				if (-1 == actualRead)
				{
					setMillisecondPosition(0);
				}
				else if (actualRead == 0)
				{
					// we want to prevent an infinite loop
					// but this will hopefully never happen because
					// we set the loop end point with a frame aligned byte number
					break;
				}
				else
				{
					bytesRead += actualRead;
					totalBytesRead += actualRead;
				}
			}

		}
		catch (IOException ioe)
		{
			JSMinim.error("Error reading from the file - " + ioe.getMessage());
		}
	}

	private void writeBytes()
	{
		// the write call will block until the requested amount of bytes
		// is written, however the user might stop the line in the
		// middle of writing and then we get told how much was actually written
		int actualWrit = line.write(rawBytes, 0, rawBytes.length);
		while (actualWrit != rawBytes.length)
		{
      /* TODO funny issue here where we will sleep because the line has been 
       * stopped and there's no more room in the buffer. what should be happening
       * is that the listener gets broadcast silence but because we loop here,
       * that doesn't happen. need to think of a decent solution.
       */
			while (line.available() < rawBytes.length - actualWrit)
			{
				sleep(10);
			}
			// try again from where we left off
			actualWrit += line.write(rawBytes, actualWrit, rawBytes.length	- actualWrit);
		}
	}

	private void broadcast()
	{
		synchronized (buffer)
		{
			if (buffer.getChannelCount() == Minim.MONO)
			{
				if (play)
				{
					listener.samples(buffer.getChannel(0));
				}
				else
				{
					listener.samples(silence);
				}
			}
			else if (buffer.getChannelCount() == Minim.STEREO)
			{
				if (play)
				{
					listener.samples(buffer.getChannel(0), buffer.getChannel(1));
				}
				else
				{
					listener.samples(silence, silence);
				}
			}
		}
	}

	private synchronized void process()
	{
		synchronized (buffer)
		{
			int frameCount = rawBytes.length / format.getFrameSize();
			buffer.setSamplesFromBytes(rawBytes, 0, format, 0, frameCount);

			// process the samples
			if (buffer.getChannelCount() == Minim.MONO)
			{
				effect.process(buffer.getChannel(0));
			}
			else if (buffer.getChannelCount() == Minim.STEREO)
			{
				effect.process(buffer.getChannel(0), buffer.getChannel(1));
			}
			// finally convert them back to bytes
			buffer.convertToByteArray(rawBytes, 0, format);
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
		try
		{
			ais.close();
		}
		catch (IOException e)
		{
		}
		ais = null;		
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

	public void setLoopPoints(int start, int stop)
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

	public void setMillisecondPosition(int millis)
	{
		if (millis <= 0)
		{
			rewind();
			totalBytesRead = 0;
			return;
		}

		if (millis > getMillisecondLength())
		{
			millis = getMillisecondLength();
		}

		if (millis < getMillisecondPosition())
		{
			rewind();
		}
		boolean wasPlaying = play;
		play = false;
		totalBytesRead = skip(millis);
		play = wasPlaying;
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

	protected abstract void rewind();

	// skip forward millis
	protected abstract int skip(int millis);
}
