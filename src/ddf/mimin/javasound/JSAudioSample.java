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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;
import org.tritonus.share.sampled.FloatSampleBuffer;

import ddf.minim.AudioEffect;
import ddf.minim.AudioListener;
import ddf.minim.AudioSampleImpl;
import ddf.minim.Minim;

final class JSAudioSample extends Thread
                         implements AudioSampleImpl
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
  
  JSAudioSample(FloatSampleBuffer samps, SourceDataLine sdl, int bufferSize)
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

  public void setAudioListener(AudioListener listener)
  {
    this.listener = listener;    
  }

  public Control[] getControls()
  {
    return line.getControls();
  }

  public int length()
  {
    return length;
  }
}
