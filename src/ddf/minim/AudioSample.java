package ddf.minim;

import ddf.minim.spi.AudioStream;

/**
 * An <code>AudioSample</code> is a special kind of file playback that allows
 * you to repeatedly <i>trigger</i> an audio file. It does this by keeping the
 * entire file in an internal buffer and then keeping a list of trigger points.
 * It is {@link Recordable} and {@link Effectable} so access to the samples is
 * available and <code>AudioEffect</code>s can be attached to it, but there
 * are not the cueing abilities found on an <code>AudioSnippet</code> and
 * <code>AudioPlayer</code>. All you can do is <code>trigger()</code> the
 * sound. However, you can trigger the sound even if it is still playing back.
 * It is not advised that you use this class for long sounds (like entire songs,
 * for example) because the entire file is kept in memory.
 * 
 * @author Damien Di Fede
 * 
 */
public abstract class AudioSample extends AudioSource 
                                  implements BufferedAudio, Triggerable
{
  public AudioSample(AudioStream stream)
  {
    super(stream);
  }
  
  public abstract AudioMetaData getMetaData();
}
