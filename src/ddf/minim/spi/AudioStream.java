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

package ddf.minim.spi;

import ddf.minim.AudioEffect;
import ddf.minim.AudioListener;

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
