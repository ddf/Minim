package ddf.mimin.javasound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Control;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.FloatSampleBuffer;

import ddf.minim.AudioEffect;
import ddf.minim.AudioListener;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;

public abstract class JSBaseAudioRecordingStream implements Runnable,
		AudioRecordingStream
{
	private Thread 				iothread;
	private AudioListener		listener;
	private AudioEffect			effect;

	// reading stuff
	private boolean				play;
	private boolean				loop;
	private int						numLoops;
	protected AudioInputStream	ais;
	private byte[]					rawBytes;

	// writing stuff
	protected AudioFormat		format;
	private SourceDataLine		line;
	private FloatSampleBuffer	buffer;
	private int						bufferSize;
	private boolean				finished;
	private float[]				silence;

	JSBaseAudioRecordingStream(AudioInputStream stream, SourceDataLine sdl,
			int bufferSize)
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
		rawBytes = new byte[buffer.getByteArrayBufferSize(format)];
		silence = new float[bufferSize];
		iothread = null;
	}

	public void run()
	{
		while ( !finished )
		{
			while ( line.available() < rawBytes.length )
			{
				sleep(10);
			}
			if ( play )
			{
				// read in a full buffer of bytes from the file
				// but only if there's room in the line for them
				readBytes();
				// convert them to floating point
				// hand those arrays to our effect
				// and convert back to bytes
				process();
				// write to the line until all bytes are written
				writeBytes();
			}
			//	send samples to the listener
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
		while( bytesRead < toRead )
		{
			try
			{
				int actualRead = 0;
				synchronized ( ais )
				{
					actualRead = ais.read(rawBytes, bytesRead, toRead - bytesRead);
					//JSMinim.debug("Wanted to read " + (toRead-bytesRead) + ", actually read " + actualRead);
				}
				if (actualRead == -1 && loop)
				{
					// reset the stream
					if (numLoops == Minim.LOOP_CONTINUOUSLY)
					{
						setMillisecondPosition(0);
					}
					// reset the stream, decrement loop count
					else if (numLoops > 0)
					{
						setMillisecondPosition(0);
						numLoops--;
					}
					// otherwise just stop playing
					else
					{
						loop = false;
						pause();
					}
				} // if actualRead == -1 && loop
				else
				{
					bytesRead += actualRead;
				}					
			}
			catch (IOException e)
			{
				JSMinim.error("Error reading from the file - " + e.getMessage());
			}
		}
	}
	
	private void writeBytes()
	{
		// the write call will block until the requested amount of bytes
		// is written, however the user might stop the line in the 
		// middle of writing and then we get told how much was actually written
		int actualWrit = line.write(rawBytes, 0, rawBytes.length);
		while ( actualWrit != rawBytes.length )
		{
			//JSMinim.debug("Wanted to write " + rawBytes.length + ", actually wrote " + actualWrit);
			// wait until there's room for the rest
			while ( line.available() < rawBytes.length - actualWrit )
			{
				sleep(10);
			}
			// try again from where we left off
			actualWrit += line.write(rawBytes, actualWrit, rawBytes.length - actualWrit);			
		}
	}
	
	private void broadcast()
	{
		synchronized ( buffer )
		{
			if ( buffer.getChannelCount() == Minim.MONO )
			{
				if ( play )
				{
					listener.samples(buffer.getChannel(0));
				}
				else
				{
					listener.samples(silence);
				}
			}
			else if ( buffer.getChannelCount() == Minim.STEREO )
			{
				if ( play )
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
		synchronized ( buffer )
		{
			int frameCount = rawBytes.length / format.getFrameSize();
			buffer.setSamplesFromBytes(rawBytes, 0, format, 0, frameCount);
	
			// process the samples
			if ( buffer.getChannelCount() == Minim.MONO)
			{
				effect.process(buffer.getChannel(0));
			}
			else if ( buffer.getChannelCount() == Minim.STEREO )
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iothread = null;
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
