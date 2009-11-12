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

/**
* A Lanczos window function.
*
* @see   <a href="http://en.wikipedia.org/wiki/Window_function#Lanczos_window">The Lanczos Window</a>
*/
public class LanczosWindow extends WindowFunction
{
  /** Constructs a Lanczos window. */
  public LanczosWindow()
  {
  }

  /**
  * Windows the data in samples.
  *
  * @param samples sample buffer to be windowed
  */
  public void apply(float[] samples)
  {
    for (int n = 0; n < samples.length; n++)
    {
      float x = 2 * n / (float) (samples.length - 1) - 1;
      samples[n] *= Math.sin(Math.PI * x) / (Math.PI * x);
    }
  }
}

