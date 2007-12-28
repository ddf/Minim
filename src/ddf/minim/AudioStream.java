package ddf.minim;

/**
 * An <code>AudioStream</code> is a stream of samples that is coming from 
 * somewhere. Users of an <code>AudioStream</code> don't really need to know
 * where the samples are coming from. However, typically they will be read 
 * from a <code>Line</code> or a file. An <code>AudioStream</code> needs to 
 * be opened before being used and closed when you are finished with it.
 * 
 * @author Damien Di Fede
 *
 */
public interface AudioStream extends AudioResource
{  
  /**
   * Set the AudioListener to receive samples from this source.
   * 
   * @param listener
   *          the AudioListener to receive samples
   */
  void setAudioListener(AudioListener listener);

  /**
   * Set the AudioEffect to apply to this stream.
   * 
   * @param effect
   *          the AudioEffect to apply to the stream
   */
  void setAudioEffect(AudioEffect effect);
  
  /**
   * The size of the buffer that will be sent to listeners and effects.
   * 
   * @return the size of the buffer sent to listeners
   */
  int bufferSize();
}
