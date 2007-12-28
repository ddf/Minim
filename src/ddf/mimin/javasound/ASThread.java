package ddf.mimin.javasound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;
import org.tritonus.share.sampled.FloatSampleBuffer;

import ddf.minim.AudioEffect;
import ddf.minim.AudioListener;
import ddf.minim.BufferedAudio;
import ddf.minim.Minim;
import ddf.minim.Triggerable;
import ddf.minim.spi.AudioStream;

class ASThread extends Thread implements AudioStream, BufferedAudio, Triggerable
{
  private AudioListener listener;
  private AudioEffect effects;
  
  // sample stuff
  private AudioFormat format;
  private FloatSampleBuffer samples;
  private int bufferSize;
  private int[] marks;
  private int   markAt;
  private int length;
  
  // line writing stuff
  private SourceDataLine line;
  private FloatSampleBuffer buffer;
  private boolean finished;
  
  ASThread(FloatSampleBuffer samps, SourceDataLine sdl, int bufferSize)
  {
    super();
    format = sdl.getFormat();
    this.bufferSize = bufferSize;
    
    marks = new int[20];
    for (int i = 0; i < marks.length; i++)
      marks[i] = -1;
    markAt = 0;
    samples = samps;
    
    line = sdl;
    buffer = new FloatSampleBuffer(format.getChannels(), 
                                   bufferSize,
                                   format.getSampleRate());
    length = (int)AudioUtils.frames2Millis(bufferSize, format);
    finished = false;
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
    while ( !finished )
    {
      // clear the buffer
      buffer.makeSilence();
      // build our signal from all the marks
      for (int i = 0; i < marks.length; i++)
      {
        int begin = marks[i];
        if (begin == -1) continue;
        //Minim.debug("Sample trigger in process at marks[" + i + "] = " + marks[i]);
        int j, k;
        for (j = begin, k = 0; j < samples.getSampleCount()
                            && k < buffer.getSampleCount(); j++, k++)
        {
          if ( format.getChannels() == Minim.MONO )
          {
            buffer.getChannel(0)[k] += samples.getChannel(0)[j];
          }
          else
          {
            buffer.getChannel(0)[k] += samples.getChannel(0)[j];           
            buffer.getChannel(1)[k] += samples.getChannel(1)[j];
          }
        }
        if ( j < samples.getSampleCount() )
        {
          marks[i] = j;
        }
        else
        {
          //Minim.debug("Sample trigger ended.");
          marks[i] = -1;
        }
      }
      // apply effects and broadcast samples to our listeners
      if ( format.getChannels() == Minim.MONO )
      {
        effects.process(buffer.getChannel(0));
        listener.samples(buffer.getChannel(0));
      }
      else
      {
        effects.process(buffer.getChannel(0), buffer.getChannel(1));
        listener.samples(buffer.getChannel(0), buffer.getChannel(1));
      }
      // write to the line
      byte[] bytes = buffer.convertToByteArray(format);
      line.write(bytes, 0, bytes.length);
    }
    line.drain();
    line.stop();
    line.close();
    line = null;
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

  public void setAudioEffect(AudioEffect effect)
  {
    effects = effect;    
  }

  public void setAudioListener(AudioListener al)
  {
    listener = al;    
  }

  public Control[] getControls()
  {
    return line.getControls();
  }

  public int length()
  {
    return length;
  }
  
  public void trigger()
  {
    marks[markAt] = 0;
    markAt++;
    if ( markAt == marks.length ) markAt = 0;
  }
  
  public float[] getChannel(int channelNumber)
  {
    if ( buffer.getChannelCount() < channelNumber )
    {
      return buffer.getChannel(channelNumber);
    }
    return null;
  }
}
