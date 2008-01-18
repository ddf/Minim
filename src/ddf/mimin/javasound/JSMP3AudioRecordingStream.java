package ddf.mimin.javasound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;

import org.tritonus.share.sampled.AudioUtils;
import org.tritonus.share.sampled.FloatSampleBuffer;

import ddf.minim.AudioEffect;
import ddf.minim.AudioListener;
import ddf.minim.AudioMetaData;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;

class JSMP3AudioRecordingStream extends Thread implements AudioRecordingStream
{
  private AudioListener listener;
  private AudioEffect effect;

  // file reading stuff
  private String fileName;
  private AudioMetaData meta;
  private long lengthInMillis;
  private boolean play;
  private boolean loop;
  private int numLoops;
  // keeping the encoded stream around allows us to reset it
  // if the operation is available, and then just make a new
  // decoded stream, rather than totally reloading the file
  private AudioInputStream encAis;
  private DecodedMpegAudioInputStream ais;
  private byte[] rawBytes;

  // line writing stuff
  private AudioFormat format;
  private SourceDataLine line;
  private FloatSampleBuffer buffer;
  private int bufferSize;
  private boolean finished;

  JSMP3AudioRecordingStream(AudioMetaData mdata, AudioInputStream encStream,
      AudioInputStream decStream, SourceDataLine sdl, int bufferSize)
  {
    format = sdl.getFormat();
    line = sdl;
    this.bufferSize = bufferSize;
    buffer = new FloatSampleBuffer(format.getChannels(), 
                                   bufferSize, 
                                   format.getSampleRate());
    Minim.debug("FloatSampleBuffer has " + buffer.getSampleCount()
        + " samples.");
    finished = false;

    meta = mdata;
    fileName = meta.fileName();
    lengthInMillis = meta.length();
    play = loop = false;
    numLoops = 0;
    encAis = encStream;
    ais = (DecodedMpegAudioInputStream) decStream;
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
          int toRead = rawBytes.length;
          int bytesRead = 0;
          while (bytesRead < toRead && play && !finished)
          {
            int actualRead = ais.read(rawBytes, bytesRead, toRead - bytesRead);
            // -1 means end of file
            if (actualRead == -1)
            {
              if (loop)
              {
                // reset the stream
                if (numLoops == Clip.LOOP_CONTINUOUSLY)
                {
                  rewind();
                }
                // reset the stream, decrement loop count
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
              } // if loop
              else
              {
                play = false;
              }
            } // if actualRead == -1
            else
            {
              bytesRead += actualRead;
            }
          }// while bytesRead < toRead
        } // if play
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
      try
      {
        Thread.sleep(10);
      }
      catch (InterruptedException e)
      {
      }
    }
    line.drain();
    line.stop();
    line.close();
    line = null;
    try
    {
      ais.close();
    }
    catch (IOException e)
    {
      Minim.error("Couldn't close the stream");
    }
    ais = null;
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
    Minim.debug("Rewinding...");
    try
    {
      if ( encAis.markSupported() )
      {
        Minim.error("Reseting");
        encAis.reset();
        // don't close the existing stream because it
        // is wrapping our encoded stream
        ais = (DecodedMpegAudioInputStream) JSMinim.getAudioInputStream(format, encAis);        
      }
      else
      {
        // blah, close and reload
        ais.close();
        AudioInputStream encIn = JSMinim.getAudioInputStream(fileName);
        ais = (DecodedMpegAudioInputStream) JSMinim.getAudioInputStream(format, encIn);
      }
    }
    catch (IOException e)
    {
      Minim.error("Couldn't rewind the stream: " + e.getMessage());
    }    
  }

  public void loop(int n)
  {
    loop = true;
    numLoops = n;
    play = true;
  }

  private void skip(int millis)
  {
    if (millis > 0)
    {
      Minim.debug("Skipping forward by " + millis + " milliseconds.");
      // if it puts us past the end of the file, only skip what's left
      if (getMillisecondPosition() + millis > getMillisecondLength())
      {
        millis = getMillisecondLength() - getMillisecondPosition();
      }
      Minim.debug("Skipping " + millis + " millis.");
      // don't want the io thread to read while we're skipping
      boolean playstate = play;
      play = false;
      long toSkip = AudioUtils.millis2Bytes(millis, format);
      byte[] skipBytes = new byte[(int) toSkip];
      long totalSkipped = 0;
      try
      {
        // it's only able to read about 2 seconds at a time
        // so we've got to loop until we've skipped the requested amount
        while (totalSkipped < toSkip)
        {
          int read;
          read = ais.read(skipBytes, 0, skipBytes.length);
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
        Minim.error("Unable to skip due to read error: " + e.getMessage());
      }
      Minim.debug("Total actually skipped was " + totalSkipped + ", which is "
          + AudioUtils.bytes2Millis(totalSkipped, format) + " milliseconds.");
      // restore the play state
      play = playstate;
    }
    else if (millis < 0)
    {
      Minim.debug("Skipping backwards by " + (-millis) + " milliseconds.");
      // to skip backwards we need to rewind
      // and then cue to the new position
      // remember that millis is negative, that's why we add
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

  public int getMillisecondLength()
  {
    return (int) lengthInMillis;
  }

  public int getMillisecondPosition()
  {
    Long pos = (Long) ais.properties().get("mp3.position.microseconds");
    return (int) (pos.longValue() / 1000);
  }

  public AudioMetaData getMetaData()
  {
    return meta;
  }

  public void setLoopPoints(int start, int stop)
  {
    // TODO Auto-generated method stub

  }

  public void setMillisecondPosition(int millis)
  {
    if (millis < 0)
    {
      rewind();
      return;
    }
    if (millis > getMillisecondLength()) millis = getMillisecondLength();
    if (millis > getMillisecondPosition())
    {
      Minim.debug("Skipping for cue.");
      skip(millis - getMillisecondPosition());
    }
    else
    {
      Minim.debug("Rewind and skip for cue.");
      rewind();
      skip(millis);
    }
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
