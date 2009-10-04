package ddf.minim;

import javax.sound.sampled.AudioFormat;

import ddf.minim.spi.AudioStream;

/**
 * An <code>AudioSource</code> is a kind of wrapper around an
 * <code>AudioStream</code>. An <code>AudioSource</code> will add its
 * <code>AudioBuffer</code>s as listeners on the stream so that you can
 * access the stream's samples without having to implement
 * <code>AudioListener</code> yourself. It also provides the
 * <code>Effectable</code> and <code>Recordable</code> interface. Because an
 * <code>AudioStream</code> must be closed when you are finished with it, you
 * must remember to call {@link #close()} on any <code>AudioSource</code>s
 * you obtain from Minim, such as <code>AudioInput</code>s,
 * <code>AudioOutput</code>s, and <code>AudioPlayer</code>s.
 * 
 * @author Damien Di Fede
 * 
 */

// TODO: add a way for the user to pull the audio without having to 
// have it playback, for people that want to just analyze files.
public class AudioSource extends Controller implements Effectable, Recordable
{
  private AudioStream stream;
  // the signal splitter used to manage listeners to the source
  // our stereobuffer will be the first in the list
  private SignalSplitter splitter;
  // the StereoBuffer that will subscribe to synth
  private StereoBuffer buffer;
  // the effects chain used for effecting
  private EffectsChain effects;

  /**
   * The buffer containing the left channel samples. If this is a mono
   * <code>AudioSource</code>, it contains the single channel of audio.
   */
  public final AudioBuffer left;

  /**
   * The buffer containing the right channel samples. If this is a mono
   * <code>AudioSource</code>, <code>right</code> contains the same samples
   * as <code>left</code>.
   */
  public final AudioBuffer right;

  /**
   * The buffer containing the mix of the left and right channels. If this is a
   * mono <code>AudioSource</code>, <code>mix</code> contains the same
   * samples as <code>left</code>.
   */
  public final AudioBuffer mix;

  /**
   * Constructs an <code>AudioSource</code> that will subscribe to the samples
   * in <code>stream</code>. It is expected that the stream is using a
   * <code>DataLine</code> for playback. If it is not, calls to
   * <code>Controller</code>'s methods will result in a
   * <code>NullPointerException</code>.
   * 
   * @param istream
   *          the <code>AudioStream</code> to subscribe to and wrap
   */
  public AudioSource(AudioStream istream)
  {
    super(istream.getControls());
    stream = istream;
    
    // we gots a buffer for users to poll
    buffer = new StereoBuffer(stream.getFormat().getChannels(), stream.bufferSize(), this);
    left = buffer.left;
    right = buffer.right;
    mix = buffer.mix;
    
    // we gots a signal splitter that we'll add any listeners the user wants
    splitter = new SignalSplitter(stream.getFormat(), stream.bufferSize());
    // we stick our buffer in the signal splitter because we can only set one
    // listener on the stream
    splitter.addListener(buffer);
    // and there it goes.
    stream.setAudioListener(splitter);
    
    // we got an effects chain that we'll add user effects to
    effects = new EffectsChain();
    // we set it as the effect on the stream
    stream.setAudioEffect(effects);
    
    stream.open();
  }

  /**
   * Closes the <code>AudioStream</code> this was constructed with.
   * 
   */
  public void close()
  {
    stream.close();
  }

  public void addEffect(AudioEffect effect)
  {
    effects.add(effect);
  }

  public void clearEffects()
  {
    effects.clear();
  }

  public void disableEffect(int i)
  {
    effects.disable(i);
  }

  public void disableEffect(AudioEffect effect)
  {
    effects.disable(effect);
  }

  public int effectCount()
  {
    return effects.size();
  }

  public void effects()
  {
    effects.enableAll();
  }

  public boolean hasEffect(AudioEffect e)
  {
    return effects.contains(e);
  }

  public void enableEffect(int i)
  {
    effects.enable(i);
  }

  public void enableEffect(AudioEffect effect)
  {
    effects.enable(effect);
  }

  public AudioEffect getEffect(int i)
  {
    return effects.get(i);
  }

  public boolean isEffected()
  {
    return effects.hasEnabled();
  }

  public boolean isEnabled(AudioEffect effect)
  {
    return effects.isEnabled(effect);
  }

  public void noEffects()
  {
    effects.disableAll();
  }

  public void removeEffect(AudioEffect effect)
  {
    effects.remove(effect);
  }

  public AudioEffect removeEffect(int i)
  {
    return effects.remove(i);
  }

  public void addListener(AudioListener listener)
  {
    splitter.addListener(listener);
  }

  public int bufferSize()
  {
    return stream.bufferSize();
  }

  public AudioFormat getFormat()
  {
    return stream.getFormat();
  }

  public void removeListener(AudioListener listener)
  {
    splitter.removeListener(listener);
  }

  public int type()
  {
    return stream.getFormat().getChannels();
  }

  public float sampleRate()
  {
    return stream.getFormat().getSampleRate();
  }
}
