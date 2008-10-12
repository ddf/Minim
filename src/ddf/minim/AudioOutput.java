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

import ddf.minim.spi.AudioSynthesizer;

/**
 * An <code>AudioOutput</code> is used to generate audio with
 * <code>AudioSignal</code>s. Well, strictly speaking, the
 * <code>AudioSynthesizer</code> it is constructed with generates the signals
 * and <code>AudioOutput</code> merely delegates to the synth when signals are
 * added. You can get an <code>AudioOutput</code> from <code>Minim</code> by
 * calling one of the <code>getLineOut</code> methods.
 * 
 * @author Damien Di Fede
 * 
 */
public class AudioOutput extends AudioSource implements Polyphonic
{
  // the synth attach our signals to
  private AudioSynthesizer synth;
  // the signals added by the user
  private SignalChain signals;

  /**
   * Constructs an <code>AudioOutput</code> that will subscribe its buffers to
   * <code>synthesizer</code> and be able to control the <code>DataLine</code>
   * the synthesizer uses for output. If the synth does not have an associated
   * <code>DataLine</code>, then calls to <code>Controller</code>'s
   * methods will result in a <code>NullPointerException</code>.
   * 
   * @param synthesizer
   *          the <code>AudioSynthesizer</code> to subscribe to
   */
  public AudioOutput(AudioSynthesizer synthesizer)
  {
    super(synthesizer);
    synth = synthesizer;
    signals = new SignalChain();
    synth.setAudioSignal(signals);
  }

  public void addSignal(AudioSignal signal)
  {
    signals.add(signal);
  }

  public AudioSignal getSignal(int i)
  {
    return signals.get(i);
  }

  public void removeSignal(AudioSignal signal)
  {
    signals.remove(signal);
  }

  public AudioSignal removeSignal(int i)
  {
    return signals.remove(i);
  }

  public void clearSignals()
  {
    signals.clear();
  }

  public void disableSignal(int i)
  {
    signals.disable(i);
  }

  public void disableSignal(AudioSignal signal)
  {
    signals.disable(signal);
  }

  public void enableSignal(int i)
  {
    signals.enable(i);
  }

  public void enableSignal(AudioSignal signal)
  {
    signals.enable(signal);
  }

  public boolean isEnabled(AudioSignal signal)
  {
    return signals.isEnabled(signal);
  }

  public boolean isSounding()
  {
    return signals.hasEnabled();
  }

  public void noSound()
  {
    signals.disableAll();
  }

  public int signalCount()
  {
    return signals.size();
  }

  public void sound()
  {
    signals.enableAll();
  }

  public boolean hasSignal(AudioSignal signal)
  {
    return signals.contains(signal);
  }
}
