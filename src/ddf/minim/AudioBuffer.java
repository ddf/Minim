/*
 *  Copyright (c) 2007 - 2009 by Damien Di Fede <ddf@compartmental.net>
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
 * An <code>AudioBuffer</code> is specifically a buffer of floating point samples 
 * corresponding to a single channel of streaming audio. It is readonly, but you 
 * can obtain a copy of the samples in the buffer by using {@link #toArray()}. In 
 * fact, when drawing a waveform, you should use <code>toArray</code> rather than 
 * iterating over the buffer itself. This is because it is possible that the samples 
 * in the buffer will be replaced with new ones between calls to {@link #get(int)}, 
 * which results in a waveform that appears to have discontinuities in it.
 * 
 * @author Damien Di Fede
 *
 */

public interface AudioBuffer
{
  /**
   * Returns the length of the buffer.
   * 
   * @return the number of samples in the buffer
   */
  int size();
  
  /**
   * Gets the <code>i<sup>th</sup></code> sample in the buffer. This method
   * does not do bounds checking, so it may throw an exception.
   * 
   * @param i
   *          the index of the sample you want to get
   *          
   * @return the <code>i<sup>th</sup></code> sample
   * 
   * @example Basics/DrawWaveformAndLevel
   */
  float get(int i);
  
  /**
   * Gets the current level of the buffer. It is calculated as the
   * root-mean-square of all the samples in the buffer.
   * 
   * @return the RMS amplitude of the buffer
   * 
   * @example Basics/DrawWaveformAndLevel
   */
  float level();
  
  /**
   * Returns the samples in the buffer in a new float array. 
   * Modifying the samples in the returned array will not change 
   * the samples in the buffer.
   * 
   * @return a new float array containing the buffer's samples
   */
  float[] toArray();
}
