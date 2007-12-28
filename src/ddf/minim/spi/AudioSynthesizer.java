package ddf.minim.spi;

import ddf.minim.AudioEffect;
import ddf.minim.AudioSignal;

/**
 * An <code>AudioSythesizer</code> is an <code>AudioStream</code> that
 * generates sound, rather than reading sound. It uses the attached
 * <code>AudioSignal</code> and <code>AudioEffect</code> to generate a
 * signal.
 * 
 * @author Damien Di Fede
 * 
 */
public interface AudioSynthesizer extends AudioStream
{
  /**
   * Sets the AudioSignal used by this sythesizer.
   * 
   * @param signal
   *          the AudioSignal used to generate sound
   */
  void setAudioSignal(AudioSignal signal);

  /**
   * Sets the AudioEffect to apply to the signal.
   * 
   * @param effect
   *          the AudioEffect to apply to the signal
   */
  void setAudioEffect(AudioEffect effect);
}
