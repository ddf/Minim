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

package ddf.minim.analysis;

import ddf.minim.Minim;

/**
 * A Gauss window function.
 *
 * @see   <a href="http://en.wikipedia.org/wiki/Window_function#Gauss_window">The Gauss Window</a>
 */
public class GaussWindow extends WindowFunction
{
  double alpha;

  /** Constructs a Gauss window function. */
  public GaussWindow(double alpha)
  {
    if ( alpha < 0.0 || alpha > 0.5 ) {
      Minim.error("Range for GauseWindow out of bounds. Value must be <= 0.5");
      return;
    }
    this.alpha = alpha;  
  }

  /**
   * Windows the data in samples.
   *
   * @param samples sample buffer to be windowed
   */
  public void apply(float[] samples) 
  {
    for (int n = 0; n < samples.length; n++ ) {
      samples[n] *= Math.pow(Math.E, -0.5 * Math.pow((n - (samples.length - 1) / (double) 2) / (this.alpha * (samples.length - 1) / (double) 2), (double) 2));
    }
  }
}
