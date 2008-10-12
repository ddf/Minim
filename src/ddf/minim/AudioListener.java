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

/**
 * An <code>AudioListener</code> can be used to monitor <code>Recordable</code> 
 * objects. Each time a <code>Recordable</code> object receives a new sample buffer 
 * from the audio system, or generates a new sample buffer at the request of the 
 * audio system, it passes a copy of this buffer to its listeners. You can 
 * implement this interface if you want to receive samples in a callback fashion, 
 * rather than using an object's <code>AudioBuffer</code>s to access them. You 
 * add an <code>AudioListener</code> to a <code>Recordable</code> by calling 
 * {@link Recordable#addListener(AudioListener)}. When you want to stop 
 * receiving samples you call {@link Recordable#removeListener(AudioListener)}.
 * 
 * @author Damien Di Fede
 *
 */
public interface AudioListener
{
  /**
   * Called by the <code>Recordable</code> object this is attached to 
   * when that object has new samples.
   * 
   * @param samp a buffer of samples from a MONO sound stream
   */
  void samples(float[] samp);
  
  /**
   * Called by the <code>Recordable</code> object this is attached to
   * when that object has new samples.
   * 
   * @param sampL the left channel of a STEREO sound stream
   * @param sampR the right channel of a STEREO sound stream
   */
  void samples(float[] sampL, float[] sampR);
}
