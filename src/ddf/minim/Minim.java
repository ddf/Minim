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

package ddf.minim;

import javax.sound.sampled.AudioFileFormat;

import processing.core.PApplet;
import ddf.mimin.javasound.JSMinim;
import ddf.minim.spi.AudioRecording;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.spi.AudioStream;
import ddf.minim.spi.AudioSynthesizer;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.spi.SampleRecorder;

/**
 * The <code>Minim</code> class is how you get what you want from JavaSound.
 * There are methods for obtaining objects for playing audio files:
 * {@link AudioSampleImpl}, {@link AudioSnippet}, and {@link AudioPlayer}. There
 * are methods for obtaining an {@link AudioRecorder}, which is how you record
 * audio to disk. There are methods for obtaining an {@link AudioInput}, which
 * is how you can monitor the computer's line-in or microphone, depending on
 * what the user has set as the record source. Finally there are methods for
 * obtaining an {@link AudioOutput}, which is how you can play audio generated
 * by your program. All of these classes are given {@link AudioStream AudioStreams} 
 * by <code>Minim</code>, which are <code>Thread</code>s that do the actual work 
 * of audio I/O. Because of this, you should always call the <code>close</code> 
 * method of an AudioXXX when you are finished with it.
 * <p>
 * <code>Minim</code> needs to know about your sketch so that it can load files 
 * from the sketches data directory. For this reason, before you do anything with
 * <code>Minim</code>, you must call {@link #start(PApplet) start}. 
 * 
 * @author Damien Di Fede
 */

public class Minim
{
  /** Specifies that you want a MONO AudioInput or AudioOutput */
  public static final int MONO = 1;
  /** Specifies that you want a STEREO AudioInput or AudioOutput */
  public static final int STEREO = 2;
  
  public static final int LOOP_CONTINUOUSLY = -1;

  /** The .wav file format. */
  public static AudioFileFormat.Type WAV = AudioFileFormat.Type.WAVE;
  /** The .aiff file format. */
  public static AudioFileFormat.Type AIFF = AudioFileFormat.Type.AIFF;
  /** The .aifc file format. */
  public static AudioFileFormat.Type AIFC = AudioFileFormat.Type.AIFC;
  /** The .au file format. */
  public static AudioFileFormat.Type AU = AudioFileFormat.Type.AU;
  /** The .snd file format. */
  public static AudioFileFormat.Type SND = AudioFileFormat.Type.SND;

  private static PApplet p;
  private static MinimServiceProvider mimp;
  private static boolean DEBUG;

  private Minim() {}
  
  static int millis()
  {
    return p.millis();
  }

  /**
   * Used internally to report error messages. These error messages will appear
   * in the console area of the PDE if you are running a sketch from the PDE,
   * otherwise they will appear in the Java Console.
   * 
   * @param s
   *          the error message to report
   */
  public static void error(String s)
  {
    PApplet.println("=== Minim Error ===");
    PApplet.println("=== " + s);
    PApplet.println();
  }

  /**
   * Displays a debug message, but only if {@link #debugOn()} has been called. 
   * The message will be displayed in the console area of the PDE, 
   * if you are running your sketch from the PDE. 
   * Otherwise, it will be displayed in the Java Console.
   * 
   * @param s
   *          the message to display
   * @see #debugOn()
   */
  public static void debug(String s)
  {
    if (DEBUG)
    {
      String[] lines = s.split("\n");
      PApplet.println("=== Minim Debug ===");
      for (int i = 0; i < lines.length; i++)
        PApplet.println("=== " + lines[i]);
      PApplet.println();
    }
  }

  /**
   * Turns on debug messages.
   */
  public static void debugOn()
  {
    DEBUG = true;
  }

  /**
   * Turns off debug messages.
   * 
   */
  public static void debugOff()
  {
    DEBUG = false;
  }

  /**
   * Starts Minim.
   * 
   * It is necessary to call this so that Minim can properly open files.
   * 
   * @param pro
   *          the sketch that is going to be using Minim
   */
  public static void start(PApplet pro)
  {
    start(pro, new JSMinim());
  }
  
  public static void start(PApplet pro, MinimServiceProvider impl)
  {
    p = pro;
    mimp = impl;
    mimp.start(pro);
    DEBUG = false;
  }

  /**
   * Stops Minim.
   * 
   * A call to this method should be placed inside of the stop() function of
   * your sketch. We expect that implemenations of the Minim 
   * interface made need to do some cleanup, so this is how we 
   * tell them it's time. 
   * 
   */
  public static void stop()
  {
    mimp.stop();
  }

  /**
   * Loads the requested file into an {@link AudioSample}.
   * 
   * @param filename
   *          the file or URL that you want to load
   * @return an <code>AudioSample</code> with a 1024 sample buffer
   * @see #loadSample(String, int)
   * @see AudioSampleImpl
   */
  static public AudioSample loadSample(String filename)
  {
    return loadSample(filename, 1024);
  }

  /**
   * Loads the requested file into an {@link AudioSample}.
   * 
   * @param filename
   *          the file or URL that you want to load
   * @param bufferSize
   *          the sample buffer size you want
   * @return an <code>AudioSample</code> with a sample buffer of the requested size
   */
  static public AudioSample loadSample(String filename, int bufferSize)
  {
    return mimp.getAudioSample(filename, bufferSize);
  }

  /**
   * Loads the requested file into an {@link AudioSnippet}
   * 
   * @param filename
   *          the file or URL you want to load
   * @return an <code>AudioSnippet</code> of the requested file or URL
   */
  static public AudioSnippet loadSnippet(String filename)
  {
    Minim.debug("Aquiring a Clip from Minim implementation " + mimp.getClass().getName());
    AudioRecording c = mimp.getAudioRecording(filename);
    if ( c != null )
    {
      return new AudioSnippet(c);
    }
    else
    {
      Minim.error("Couldn't load the file " + filename);
    }    
    return null;
  }

  /**
   * Loads the requested file into an {@link AudioPlayer} 
   * with a buffer size of 1024 samples.
   * 
   * @param filename
   *          the file or URL you want to load
   * @return an <code>AudioPlayer</code> with a 1024 sample buffer
   * 
   * @see #loadFile(String, int)
   */
  public static AudioPlayer loadFile(String filename)
  {
    return loadFile(filename, 1024);
  }

  /**
   * Loads the requested file into an {@link AudioPlayer} with 
   * the request buffer size.
   * 
   * @param filename
   *          the file or URL you want to load
   * @param bufferSize
   *          the sample buffer size you want
   *          
   * @return an <code>AudioPlayer</code> with a sample buffer of the requested size
   */
  public static AudioPlayer loadFile(String filename, int bufferSize)
  {
    AudioRecordingStream rec = mimp.getAudioRecordingStream(filename, bufferSize);
    if ( rec != null )
    {
      return new AudioPlayer(rec);
    }
    else
    {
      error("Couldn't load the file " + filename);
    }
    return null;
  }  

  /**
   * Creates an {@link AudioRecorder} that will use <code>source</code> as its 
   * record source and that will save to the file name specified. The format of the 
   * file will be inferred from the extension in the file name. If the extension is 
   * not a recognized file type, this will return null. Be aware that if you choose 
   * buffered recording the call to {@link AudioRecorder#save()} will block until 
   * the entire buffer is written to disk. In the event that the buffer is very large, 
   * your sketch will noticably hang. 
   * 
   * @param source
   *          the <code>Recordable</code> object you want to use as a record source
   * @param fileName
   *          the name of the file to record to
   * @param buffered
   *          whether or not to use buffered recording
   *          
   * @return an <code>AudioRecorder</code> for the record source
   */
  public static AudioRecorder createRecorder(Recordable source,
                                             String fileName, 
                                             boolean buffered)
  {
    SampleRecorder rec = mimp.getSampleRecorder(source, fileName, buffered);
    if ( rec != null )
    {
      return new AudioRecorder(source, rec);
    }
    else
    {
      error("Couldn't create a SampleRecorder.");
    }
    return null;
  }

  /**
   * Gets an {@link AudioInput}, to which you can attach {@link AudioEffect AudioEffects}.
   * 
   * @return an STEREO <code>AudioInput</code> with a 1024 sample buffer, a sample rate of
   *         44100 and a bit depth of 16
   * @see #getLineIn(int, int, float, int)
   */
  static public AudioInput getLineIn()
  {
    return getLineIn(STEREO);
  }

  /**
   * Gets an {@link AudioInput}, to which you can attach {@link AudioEffect AudioEffects}.
   * 
   * @param type
   *          Minim.MONO or Minim.STEREO
   * @return an <code>AudioInput</code> with the requested type, a 1024 sample buffer, a
   *         sample rate of 44100 and a bit depth of 16
   * @see #getLineIn(int, int, float, int)
   */
  static public AudioInput getLineIn(int type)
  {
    return getLineIn(type, 1024, 44100, 16);
  }

  /**
   * Gets an {@link AudioInput}, to which you can attach {@link AudioEffect AudioEffects}.
   * 
   * @param type
   *          Minim.MONO or Minim.STEREO
   * @param bufferSize
   *          how long you want the <code>AudioInput</code>'s sample buffer to be
   * @return an <code>AudioInput</code> with the requested attributes, a sample rate of 44100
   *         and a bit depth of 16
   * @see #getLineIn(int, int, float, int)
   */
  static public AudioInput getLineIn(int type, int bufferSize)
  {
    return getLineIn(type, bufferSize, 44100, 16);
  }

  /**
   * Gets an {@link AudioInput}, to which you can attach {@link AudioEffect AudioEffects}.
   * 
   * @param type
   *          Minim.MONO or Minim.STEREO
   * @param bufferSize
   *          how long you want the <code>AudioInput</code>'s sample buffer to be
   * @param sampleRate
   *          the desired sample rate in Hertz (typically 44100)
   * @return an <code>AudioInput</code> with the requested attributes and a bit depth of 16
   * @see #getLineIn(int, int, float, int)
   */
  static public AudioInput getLineIn(int type, int bufferSize, 
                                     float sampleRate)
  {
    return getLineIn(type, bufferSize, sampleRate, 16);
  }

  /**
   * Gets an {@link AudioInput}, to which you can attach {@link AudioEffect AudioEffects}.
   * 
   * @param type
   *          Minim.MONO or Minim.STEREO
   * @param bufferSize
   *          how long you want the <code>AudioInput</code>'s sample buffer to be
   * @param sampleRate
   *          the desired sample rate in Hertz (typically 44100)
   * @param bitDepth
   *          the desired bit depth (typically 8)
   * @return an <code>AudioInput</code> with the requested attributes
   */
  static public AudioInput getLineIn(int type, int bufferSize,
                                     float sampleRate, int bitDepth)
  {
    AudioStream stream = mimp.getAudioStream(type, bufferSize, sampleRate, bitDepth);
    if ( stream != null )
    {
      return new AudioInput(stream);
    }
    else
    {
      error("Minim.getLineIn: attempt failed, could not secure an AudioInput.");
    }
    return null;
  }

  /**
   * Gets an {@link AudioOutput}, to which you can attach 
   * {@link AudioSignal AudioSignals} and {@link AudioEffect AudioEffects}.
   * 
   * @return a STEREO <code>AudioOutput</code> with a 1024 sample buffer, a sample rate of
   *         44100 and a bit depth of 16
   * @see #getLineOut(int, int, float, int)
   */
  static public AudioOutput getLineOut()
  {
    return getLineOut(STEREO);
  }

  /**
   * Gets an {@link AudioOutput}, to which you can attach 
   * {@link AudioSignal AudioSignals} and {@link AudioEffect AudioEffects}.
   * 
   * @param type
   *          Minim.MONO or Minim.STEREO
   * @return an <code>AudioOutput</code> with the requested type, a 1024 sample buffer, a
   *         sample rate of 44100 and a bit depth of 16
   * @see #getLineOut(int, int, float, int)
   */
  static public AudioOutput getLineOut(int type)
  {
    return getLineOut(type, 1024, 44100, 16);
  }

  /**
    * Gets an {@link AudioOutput}, to which you can attach 
   * {@link AudioSignal AudioSignals} and {@link AudioEffect AudioEffects}.
   * 
   * @param type
   *          Minim.MONO or Minim.STEREO
   * @param bufferSize
   *          how long you want the <code>AudioOutput</code>'s sample buffer to be
   * @return an <code>AudioOutput</code> with the requested attributes, a sample rate of
   *         44100 and a bit depth of 16
   * @see #getLineOut(int, int, float, int)
   */
  static public AudioOutput getLineOut(int type, int bufferSize)
  {
    return getLineOut(type, bufferSize, 44100, 16);
  }

  /**
    * Gets an {@link AudioOutput}, to which you can attach 
   * {@link AudioSignal AudioSignals} and {@link AudioEffect AudioEffects}.
   * 
   * @param type
   *          Minim.MONO or Minim.STEREO
   * @param bufferSize
   *          how long you want the <code>AudioOutput</code>'s sample buffer to be
   * @param sampleRate
   *          the desired sample rate in Hertz (typically 44100)
   * @return an <code>AudioOutput</code> with the requested attributes and a bit depth of 16
   * @see #getLineOut(int, int, float, int)
   */
  static public AudioOutput getLineOut(int type, int bufferSize,
                                       float sampleRate)
  {
    return getLineOut(type, bufferSize, sampleRate, 16);
  }

  /**
   * Gets an {@link AudioOutput}, to which you can attach 
   * {@link AudioSignal AudioSignals} and {@link AudioEffect AudioEffects}.
   * 
   * @param type
   *          Minim.MONO or Minim.STEREO
   * @param bufferSize
   *          how long you want the <code>AudioOutput</code>'s sample buffer to be
   * @param sampleRate
   *          the desired sample rate in Hertz (typically 44100)
   * @param bitDepth
   *          the desired bit depth (typically 8)
   * @return an <code>AudioOutput</code> with the requested attributes
   */
  static public AudioOutput getLineOut(int type, int bufferSize,
                                       float sampleRate, int bitDepth)
  {
    AudioSynthesizer synth = mimp.getAudioSythesizer(type, bufferSize, sampleRate, bitDepth);
    if ( synth != null )
    {
      return new AudioOutput(synth);
    }
    else
    {
      error("Minim.getLineOut: attempt failed, could not secure a LineOut.");
    }
    return null;
  }

  
}
