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

import ddf.minim.spi.AudioStream;

/**
 * An <code>AudioInput</code> provides no extra functionality over what 
 * {@link AudioSource} does, it exists simply for the sake of having a class named 
 * for input from the system. You can obtain an <code>AudioInput</code> by calling 
 * one of the <code>getLineIn</code> methods of <code>Minim</code>. The audio that 
 * the input receives will depend on the current record source of the computer 
 * (such as the line-in or microphone).
 * 
 * @author Damien Di Fede
 *
 */
public class AudioInput extends AudioSource
{ 
  /**
   * Constructs an <code>AudioInput</code> that subscribes to <code>stream</code> and 
   * can control the <code>DataLine</code> that <code>stream</code> is reading from.
   * 
   * @param stream the <code>AudioStream</code> that this will subscribe to for samples
   */
  public AudioInput(AudioStream stream)
  {
    super(stream);
  }
}
