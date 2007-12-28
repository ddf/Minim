package ddf.mimin.javasound;

import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import ddf.minim.spi.AudioRecording;

class JSAudioRecording implements AudioRecording
{
  private Clip c;
  private int loopCount;
  private Map props;
  private boolean playing;
  
  JSAudioRecording(Clip clip, Map properties)
  {
    c = clip;
    // because Clip doesn't give access to the loop count
    // we just loop it ourselves by triggering off of a STOP event
    c.addLineListener(
        new LineListener()
        {
          public void update(LineEvent event)
          {
            if ( event.getType().equals(LineEvent.Type.STOP) )
            {
              if ( playing && loopCount != 0 )
              {
                c.setMicrosecondPosition(0);
                c.start();
                if ( loopCount > 0 )
                {
                  loopCount--;
                }
              }
              else
              {
                playing = false;
              }
            }
          }
        }
    );
    playing = false;
    loopCount = 0;
    props = properties;
  }
  public int getLoopCount()
  {
    return loopCount;
  }

  public int getMillisecondLength()
  {
    return (int) c.getMicrosecondLength() / 1000;
  }

  public int getMillisecondPosition()
  {
    return (int) c.getMicrosecondPosition() / 1000;
  }

  public Map getProperties()
  {
    return props;
  }

  public boolean isPlaying()
  {
    return playing;
  }

  public void loop(int count)
  {
    play();
    loopCount = count; 
  }

  public void setLoopPoints(int start, int end)
  {
    c.setLoopPoints(start, end);
  }

  public void setMillisecondPosition(int pos)
  {
    c.setMicrosecondPosition(pos*1000);
  }

  public void play()
  {
    if ( c.getMicrosecondPosition() != c.getMicrosecondLength() )
    {
      c.start();
      playing = true;
    }
  }

  public void pause()
  {
    c.stop();
    playing = false;
  }

  public void close()
  {
    c.close();
  }

  public Control[] getControls()
  {
    return c.getControls();
  }

  public AudioFormat getFormat()
  {
    return c.getFormat();
  }

  public void open()
  {
    // don't need to do anything here
  }
}
