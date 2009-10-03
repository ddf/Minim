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

package ddf.minim;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Mixer;

import processing.core.PApplet;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.AudioRecording;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.spi.AudioStream;
import ddf.minim.spi.AudioSynthesizer;
import ddf.minim.spi.MinimServiceProvider;
import ddf.minim.spi.SampleRecorder;

/**
 * The <code>Minim</code> class is how you get what you want from JavaSound.
 * There are methods for obtaining objects for playing audio files:
 * {@link AudioSample}, {@link AudioSnippet}, and {@link AudioPlayer}. There
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
 * from the sketch's data directory. For this reason, you must pass a PApplet to the 
 * constructor. 
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

  private static boolean DEBUG = false;
  
  private MinimServiceProvider mimp = null;
  //private PApplet app;
 
  /**
   * Creates an instance of Minim that will use the Javasound implementation.
   * 
   * @param parent
   *              the PApplet that will be used for loading files
   */
  public Minim(PApplet parent) 
  {
    //app = parent;
    mimp = new JSMinim(parent);
  }
  
  /**
   * Creates an instance of Minim that will use the provided implementation for audio.
   * 
   * @param parent
   *              the PApplet that will be used for loading files
   * @param msp
   *              the MinimServiceProvider that will be used for returning audio resources
   */
  public Minim(PApplet parent, MinimServiceProvider msp)
  {
    //app = parent;
    mimp = msp;
    mimp.start();
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
      {
        PApplet.println("=== " + lines[i]);
      }
      PApplet.println();
    }
  }

  /**
   * Turns on debug messages.
   */
  public void debugOn()
  {
    DEBUG = true;
    if ( mimp != null )
    {
   	 mimp.debugOn();
    }
  }

  /**
   * Turns off debug messages.
   * 
   */
  public void debugOff()
  {
    DEBUG = false;
    if ( mimp != null )
    {
   	 mimp.debugOff();
    }
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
  public void stop()
  {
    mimp.stop();
  }
  
  /**
   * Sets the Javasound Mixer that will be used for obtaining input sources
   * such as AudioInputs. This will do nothing if you have provided your 
   * own MinimServiceProvider.
   * 
   * @param mixer
   *          The Mixer we should try to acquire inputs from.
   */
  public void setInputMixer(Mixer mixer)
  {
    if ( mimp instanceof JSMinim )
    {
      ((JSMinim)mimp).setInputMixer(mixer);
    }
  }
  
  /**
   * Sets the Javasound Mixer that will be used for obtain output destinations
   * such as those required by AudioOuput, AudioPlayer, AudioSample, and so forth.
   * This will do nothing if you have provided your own MinimServiceProvider.
   * 
   * @param mixer
   *          The Mixer we should try to acquire outputs from.
   */
  public void setOutputMixer(Mixer mixer)
  {
    if ( mimp instanceof JSMinim)
    {
      ((JSMinim)mimp).setOutputMixer(mixer);
    }
  }

  /**
   * Creates an {@link AudioSample} using the provided samples and AudioFormat,
   * with an output buffer size of 1024 samples.
   * 
   * @param samples
   *          the samples to use
   * @param format
   *          the format to play the samples back at
   */
  public AudioSample createSample(float[] samples, AudioFormat format)
  {
    return createSample(samples, format, 1024);
  }
  
  /**
   * Creates an {@link AudioSample} using the provided samples and AudioFormat,
   * with the desired output buffer size.
   * 
   * @param samples
   *          the samples to use
   * @param format
   *          the format to play them back at
   * @param bufferSize
   *          the output buffer size to use 
   */
  public AudioSample createSample(float[] samples, AudioFormat format, int bufferSize)
  {
    return mimp.getAudioSample(samples, format, bufferSize);
  }
  
  /**
   * Creates an {@link AudioSample} using the provided left and right channel samples
   * with an output buffer size of 1024.
   * 
   * @param left
   *          the left channel of the sample
   * @param right
   *          the right channel of the sample
   * @param format
   *          the format the sample should be played back with
   */
  public AudioSample createSample(float[] left, float[] right, AudioFormat format)
  {
    return createSample(left, right, format, 1024);
  }
  
  /**
   * Creates an {@link AudioSample} using the provided left and right channel samples.
   * 
   * @param left
   *          the left channel of the sample
   * @param right
   *          the right channel of the sample
   * @param format
   *          the format the sample should be played back with
   * @param bufferSize
   *          the output buffer size desired
   */
  public AudioSample createSample(float[] left, float[] right, AudioFormat format, int bufferSize)
  {
    return mimp.getAudioSample(left, right, format, bufferSize);
  }
  
  /**
   * Loads the requested file into an {@link AudioSample}.
   * 
   * @param filename
   *          the file or URL that you want to load
   * @return an <code>AudioSample</code> with a 1024 sample buffer
   * @see #loadSample(String, int)
   * @see AudioSample
   */
  public AudioSample loadSample(String filename)
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
  public AudioSample loadSample(String filename, int bufferSize)
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
  public AudioSnippet loadSnippet(String filename)
  {
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
  public AudioPlayer loadFile(String filename)
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
  public AudioPlayer loadFile(String filename, int bufferSize)
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
  public AudioRecorder createRecorder(Recordable source,
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
  public AudioInput getLineIn()
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
  public AudioInput getLineIn(int type)
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
  public AudioInput getLineIn(int type, int bufferSize)
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
  public AudioInput getLineIn(int type, int bufferSize, 
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
   *          the desired bit depth (typically 16)
   * @return an <code>AudioInput</code> with the requested attributes
   */
  public AudioInput getLineIn(int type, int bufferSize,
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
  public AudioOutput getLineOut()
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
  public AudioOutput getLineOut(int type)
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
  public AudioOutput getLineOut(int type, int bufferSize)
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
  public AudioOutput getLineOut(int type, int bufferSize,
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
   *          the desired bit depth (typically 16)
   * @return an <code>AudioOutput</code> with the requested attributes
   */
  public AudioOutput getLineOut(int type, int bufferSize,
                                       float sampleRate, int bitDepth)
  {
    AudioSynthesizer synth = mimp.getAudioSynthesizer(type, bufferSize, sampleRate, bitDepth);
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
