package ddf.mimin.javasound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.spi.mpeg.sampled.file.MpegAudioFormat;

import org.tritonus.share.sampled.AudioUtils;
import org.tritonus.share.sampled.FloatSampleBuffer;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import processing.core.PApplet;
import ddf.minim.AudioSample;
import ddf.minim.Minim;
import ddf.minim.Recordable;
import ddf.minim.spi.AudioRecording;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.spi.AudioStream;
import ddf.minim.spi.AudioSynthesizer;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.spi.SampleRecorder;

public class JSMinim implements MinimServiceProvider
{
  static PApplet app;
  
  public JSMinim()
  {
  }
  
  public void start(PApplet parent)
  {
    app = parent;    
  }
  
  public void stop()
  {

  }
  
  public SampleRecorder getSampleRecorder(Recordable source, String fileName,
      boolean buffered)
  {
    String ext = fileName.substring(fileName.indexOf('.') + 1).toLowerCase();
    Minim.debug("createRecorder: file extension is " + ext + ".");
    AudioFileFormat.Type fileType = null;
    if (ext.equals(Minim.WAV.getExtension()))
    {
      fileType = Minim.WAV;
    }
    else if (ext.equals(Minim.AIFF.getExtension()) || ext.equals("aif"))
    {
      fileType = Minim.AIFF;
    }
    else if (ext.equals(Minim.AIFC.getExtension()))
    {
      fileType = Minim.AIFC;
    }
    else if (ext.equals(Minim.AU.getExtension()))
    {
      fileType = Minim.AU;
    }
    else if (ext.equals(Minim.SND.getExtension()))
    {
      fileType = Minim.SND;
    }
    else
    {
      Minim.error("The extension " + ext + " is not a recognized audio file type.");
      return null;
    }
    SampleRecorder recorder = null;
    if (buffered)
    {
      recorder = new JSBufferedSampleRecorder(app.sketchPath(fileName), fileType, source.getFormat(),
          source.bufferSize());
    }
    else
    {
      recorder = new JSStreamingSampleRecorder(app.sketchPath(fileName), fileType, source.getFormat(),
          source.bufferSize());
    }
    return recorder;
  }

  public AudioRecordingStream getAudioRecordingStream(String filename, int bufferSize)
  {
    AudioRecordingStream mstream = null;
    AudioInputStream ais = getAudioInputStream(filename);
    if (ais != null)
    {
      Minim.debug("File format is: " + ais.getFormat().toString());
      AudioFormat format = ais.getFormat();
      // special handling for mp3 files because 
      // they need to be converted to PCM
      if ( format instanceof MpegAudioFormat )
      {
        Map props = getProperties(filename);
        AudioFormat baseFormat = format;
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                                 baseFormat.getSampleRate(), 16, 
                                 baseFormat.getChannels(), 
                                 baseFormat.getChannels() * 2, 
                                 baseFormat.getSampleRate(), false);
        // converts the stream to PCM audio from mp3 audio
        ais = getAudioInputStream(format, ais);
        // source data line is for sending the file audio out to the speakers
        SourceDataLine line = getSourceDataLine(format);
        if ( ais != null && line != null )
        {
          mstream = new JSMP3AudioRecordingStream(filename, props, ais, line, bufferSize);
        }
      } // format instanceof MpegAudioFormat
      else
      {
        // source data line is for sending the file audio out to the speakers
        SourceDataLine line = getSourceDataLine(format);
        if ( line != null )
        {
          mstream = new JSAudioRecordingStream(ais, line, bufferSize);
        }
      } // else
    } // ais != null
    return mstream;
  }
  
  private static Map getProperties(String filename)
  {
    Minim.debug("Getting the properties.");
    Map props = null;
    try
    {
      MpegAudioFileReader reader = new MpegAudioFileReader();
      InputStream stream = app.openStream(filename);
      AudioFileFormat baseFileFormat = reader.getAudioFileFormat(stream, stream.available());
      if ( baseFileFormat instanceof TAudioFileFormat )
      {
        TAudioFileFormat fileFormat = (TAudioFileFormat)baseFileFormat;
        props = fileFormat.properties();
        if ( props == null )
        {
          Minim.error("No file properties available for " + filename + ".");
        }
        else
        {
          Minim.debug("File properties: " + props.toString());
        }
      }
    }
    catch (UnsupportedAudioFileException e)
    {
      Minim.error("Couldn't get the file format for " + filename + ": " 
                  + e.getMessage());
    }
    catch (IOException e)
    {
      Minim.error("Couldn't access " + filename + ": " + e.getMessage());
    }
    return props;
  }

  public AudioStream getAudioStream(int type, int bufferSize, float sampleRate,
      int bitDepth)
  {
    if (bitDepth != 8 && bitDepth != 16)
      throw new IllegalArgumentException(
          "Unsupported bit depth, use either 8 or 16.");
    AudioFormat format = new AudioFormat(sampleRate, bitDepth, type, true,
        false);
    TargetDataLine line = getTargetDataLine(format, bufferSize * 4);
    if (line != null)
    {
      return new JSAudioStream(line, bufferSize);
    }
    return null;
  }

  public AudioSample getAudioSample(String filename, int bufferSize)
  {
    AudioInputStream ais = getAudioInputStream(filename);
    if (ais != null)
    {
      AudioFormat format = ais.getFormat();
      FloatSampleBuffer samples = new FloatSampleBuffer();
      if ( format instanceof MpegAudioFormat )
      {
        AudioFormat baseFormat = format;
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                                 baseFormat.getSampleRate(), 16, 
                                 baseFormat.getChannels(), 
                                 baseFormat.getChannels() * 2, 
                                 baseFormat.getSampleRate(), false);
        // converts the stream to PCM audio from mp3 audio
        ais = getAudioInputStream(format, ais);
        // get a map of properties so we can find out how long it is
        Map props = getProperties(filename);
        // there is a property called mp3.length.bytes, but that is 
        // the length in bytes of the mp3 file, which will of course 
        // be much shorter than the decoded version. so we use the 
        // duration of the file to figure out how many bytes the 
        // decoded file will be.
        long dur = ((Long)props.get("duration")).longValue();
        int toRead = (int)AudioUtils.millis2Bytes(dur/1000, format);
        int totalRead = 0;
        byte[] rawBytes = new byte[toRead];
        try
        {
          // we have to read in chunks because the decoded stream won't 
          // read more than about 2000 bytes at a time
          while ( totalRead < toRead )
          {
            int actualRead = ais.read(rawBytes, totalRead, toRead - totalRead);
            if ( actualRead < 1 ) break;
            totalRead += actualRead;
          }
          ais.close();
        }
        catch ( Exception ioe )
        {
          Minim.error("Minim.loadSample: Error loading file into memory: " 
                      + ioe.getMessage());
        }
        Minim.debug("Needed to read " + toRead + " actually read " + totalRead);
        samples.initFromByteArray(rawBytes, 0, totalRead, format);
      }
      else
      {
        try
        {
          byte[] bytes = new byte[ais.available()];
          ais.read(bytes, 0, bytes.length);
          ais.close();
          samples.initFromByteArray(bytes, 0, bytes.length, format);
        }
        catch ( IOException ioe )
        {
          Minim.error("Minim.loadSample: Error loading file into memory: " 
              + ioe.getMessage());         
        }
      }
      SourceDataLine sdl = getSourceDataLine(format);
      if ( sdl != null )
      {
        ASThread ast = new ASThread(samples, sdl, bufferSize);
        return new JSAudioSample(ast);
      }
      else
      {
        Minim.error("Couldn't acquire a SourceDataLine.");
      }
    }
    return null;
  }
  
  public AudioSynthesizer getAudioSythesizer(int type, int bufferSize,
      float sampleRate, int bitDepth)
  {
    if (bitDepth != 8 && bitDepth != 16)
      throw new IllegalArgumentException(
          "Unsupported bit depth, use either 8 or 16.");
    AudioFormat format = new AudioFormat(sampleRate, bitDepth, type, true,
        false);
    SourceDataLine sdl = getSourceDataLine(format);
    if (sdl != null)
    {
      return new JSAudioSythesizer(sdl, bufferSize);
    }
    return null;
  }

  public AudioRecording getAudioRecording(String filename)
  {
    Clip clip = null;
    AudioInputStream ais = getAudioInputStream(filename);
    if (ais != null)
    {
      AudioFormat format = ais.getFormat();
      if ( format instanceof MpegAudioFormat )
      {
        AudioFormat baseFormat = format;
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                                 baseFormat.getSampleRate(), 16, 
                                 baseFormat.getChannels(), 
                                 baseFormat.getChannels() * 2, 
                                 baseFormat.getSampleRate(), false);
        // converts the stream to PCM audio from mp3 audio
        ais = getAudioInputStream(format, ais);
      }
      DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
      if (AudioSystem.isLineSupported(info))
      {
        // Obtain and open the line.
        try
        {
          clip = (Clip) AudioSystem.getLine(info);
          clip.open(ais);
        }
        catch (Exception e)
        {
          Minim.error("Error obtaining Javasound Clip: " + e.getMessage());
          return null;
        }
      }
      else
      {
        Minim.error("File format not supported.");
        return null;
      }
    }
    Map props = getProperties(filename);
    if ( props == null )
    {
      // this means we're dealing with not-an-mp3
      props = new HashMap();
      props.put("Length", new Integer((int)clip.getMicrosecondLength() / 1000));
      props.put("Filename", filename);      
    }
    return new JSAudioRecording(clip, props);
  }

  

  static AudioInputStream getAudioInputStream(String filename)
  {
    AudioInputStream ais = null;
    BufferedInputStream bis = null;
    if ( filename.startsWith("http") )
    {
      try
      {
        ais = getAudioInputStream( new URL(filename) );
      }
      catch (MalformedURLException e)
      {
        Minim.error("Bad URL: " + e.getMessage());
      }
      catch (UnsupportedAudioFileException e)
      {
        Minim.error("URL is in an unsupported audio file format: " 
                    + e.getLocalizedMessage());
      }
      catch (IOException e)
      {
        Minim.error("Error reading the URL: " + e.getMessage());
      }      
    }
    else
    {
      try
      {
        bis = new BufferedInputStream( app.openStream(filename) );
        ais = getAudioInputStream(bis);
        ais.mark((int) ais.available());
        Minim.debug("Acquired AudioInputStream.\n" + "It is " + ais.getFrameLength()
            + " frames long.\n" + "Marking support: " + ais.markSupported());
      }
      catch (IOException ioe)
      {
        Minim.error("IOException: " + ioe.getMessage());
      }
      catch (UnsupportedAudioFileException uafe)
      {
        Minim.error("Unsupported Audio File: " + uafe.getMessage());
      }
    }
    return ais;
  }
  
  /**
   * This method is also part of AppletMpegSPIWorkaround, which uses yet 
   * another workaround to load an internet radio stream.
   * 
   * @param url the URL of the stream
   * @return an AudioInputStream of the streaming audio
   * @throws UnsupportedAudioFileException
   * @throws IOException
   */
  static AudioInputStream getAudioInputStream(URL url) 
         throws UnsupportedAudioFileException, IOException
  {
    
    //alexey fix: we use MpegAudioFileReaderWorkaround with URL and user agent
    return new MpegAudioFileReaderWorkaround().getAudioInputStream(url, null);
  }
  
  /**
   * This method is a replacement for AudioSystem.getAudioInputStream(InputStream),
   * which includes workaround for getting an mp3 AudioInputStream when sketch is 
   * running in an applet. The workaround was developed by the Tritonus team 
   * and originally comes from the package javazoom.jlgui.basicplayer
   * 
   * @param is The stream to convert to an AudioInputStream
   * @return an AudioInputStream that will read from is
   * @throws UnsupportedAudioFileException
   * @throws IOException
   */
  static AudioInputStream getAudioInputStream(InputStream is) 
         throws UnsupportedAudioFileException, IOException
  {
    try
    {
      return AudioSystem.getAudioInputStream(is);
    }
    catch (Exception iae)
    {
      Minim.debug("Using AppletMpegSPIWorkaround to get codec");
      return new MpegAudioFileReader().getAudioInputStream(is);
    }
  }

  /**
   * This method is a replacement for 
   * AudioSystem.getAudioInputStream(AudioFormat, AudioInputStream), which is 
   * used for audio format conversion at the stream level. This method 
   * includes a workaround for converting from  an mp3 AudioInputStream 
   * when the sketch is running in an applet. The workaround was developed 
   * by the Tritonus team and originally comes from the package 
   * javazoom.jlgui.basicplayer
   * 
   * @param targetFormat the AudioFormat to convert the stream to
   * @param sourceStream the stream containing the unconverted audio
   * @return an AudioInputStream in the target format
   */
  static AudioInputStream getAudioInputStream(AudioFormat targetFormat,
                                                     AudioInputStream sourceStream)
  {
    try
    {
      return AudioSystem.getAudioInputStream(targetFormat, sourceStream);
    }
    catch (IllegalArgumentException iae)
    {
      Minim.debug("Using AppletMpegSPIWorkaround to get codec");
      try
      {
        Class.forName("javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider");
        return new javazoom.spi.mpeg.sampled.convert.
               MpegFormatConversionProvider().getAudioInputStream(targetFormat, sourceStream);
      }
      catch (ClassNotFoundException cnfe)
      {
        throw new IllegalArgumentException("Mpeg codec not properly installed");
      }
    }
  }

  /**
   * Gets a SourceDataLine with the requested AudioFormat and bufferSize.
   * 
   * This method is used by the getLineIn methods, but can be used externally if
   * you know what to do with a SourceDataLine.
   * 
   * @param format
   *          the AudioFormat you want the SourceDataLine to have
   */
  static SourceDataLine getSourceDataLine(AudioFormat format)
  {
    SourceDataLine line = null;
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
    if (AudioSystem.isLineSupported(info))
    {
      try
      {
        line = (SourceDataLine) AudioSystem.getLine(info);
        // we're gonna do lazy line opening
        // line.open(format, bufferSize * format.getFrameSize() * 4);
        Minim.debug("SourceDataLine buffer size is " + line.getBufferSize() + " bytes.\n"
            + "SourceDataLine format is " + line.getFormat().toString() + ".\n"
            + line.getLineInfo().toString() + ".");
      }
      catch (Exception e)
      {
        Minim.error("Error acquiring SourceDataLine: " + e.getMessage());
      }
    }
    else
    {
      Minim.error("Unable to return a SourceDataLine: unsupported format.");
    }
    return line;
  }

  /**
   * Gets a TargetDataLine with the requested AudioFormat and bufferSize.
   * 
   * This method is used by the getLineOut methods, but can be used externally
   * if you know what to do with a TargetDataLine.
   * 
   * @param format
   *          the AudioFormat you want the TargetDataLine to have
   * @param bufferSize
   *          the buffer size you want the TargetDataLine to have
   */
  static TargetDataLine getTargetDataLine(AudioFormat format, int bufferSize)
  {
    TargetDataLine line = null;
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    if (AudioSystem.isLineSupported(info))
    {
      try
      {
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format, bufferSize * format.getFrameSize());
        Minim.debug("TargetDataLine buffer size is " + line.getBufferSize() + "\n"
            + "TargetDataLine format is " + line.getFormat().toString() + "\n"
            + "TargetDataLine info is " + line.getLineInfo().toString());
      }
      catch (Exception e)
      {
        Minim.error("Error acquiring TargetDataLine: " + e.getMessage());
      }
    }
    else
    {
      Minim.error("Unable to return a TargetDataLine: unsupported format.");
    }
    return line;
  }

  
}
