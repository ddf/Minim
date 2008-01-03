/*
 *  Copyright (c) 2007 by Damien Di Fede <ddf@compartmental.net>
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

package ddf.mimin.javasound;

import ddf.minim.AudioMetaData;
import ddf.minim.AudioSample;

final class JSAudioSample extends AudioSample
{
  private ASThread thread;
  private AudioMetaData meta;
  
  JSAudioSample(AudioMetaData mdata, ASThread ast)
  {
    super(ast);
    thread = ast;
    meta = mdata;
  }
  
  public void trigger()
  {
    thread.trigger();
  }
  
  public float[] getChannel(int channelNumber)
  {
    return thread.getChannel(channelNumber);
  }

  public int length()
  {
    return thread.length();
  }
  
  public AudioMetaData getMetaData()
  {
	  return meta;
  }
}
