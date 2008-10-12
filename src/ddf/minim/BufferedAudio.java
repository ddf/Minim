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

public interface BufferedAudio
{
  static final int LEFT = 1;
  static final int RIGHT = 2;
  /**
   * Gets the samples for the requested channel number as a float array.
   * 
   * @param channelNumber the channel you want the samples for
   * @return the samples in a float array
   */
  float[] getChannel(int channelNumber);
  
  /**
   * Gets the length in milliseconds of the buffered audio.
   * 
   * @return the length in millisecons
   */
  int length();
}
