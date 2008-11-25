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
 * An <code>AudioSample</code> is a special kind of file playback that allows
 * you to repeatedly <i>trigger</i> an audio file. It does this by keeping the
 * entire file in an internal buffer and then keeping a list of trigger points.
 * It is {@link Recordable} and {@link Effectable} so access to the samples is
 * available and <code>AudioEffect</code>s can be attached to it, but there
 * are not the cueing abilities found on an <code>AudioSnippet</code> and
 * <code>AudioPlayer</code>. All you can do is <code>trigger()</code> the
 * sound. However, you can trigger the sound even if it is still playing back.
 * It is not advised that you use this class for long sounds (like entire songs,
 * for example) because the entire file is kept in memory.
 * 
 * @author Damien Di Fede
 * 
 */

// TODO: some kind of event for when a sample finishes playing?

public abstract class AudioSample extends AudioSource 
                                  implements BufferedAudio, Triggerable
{
  /**
   * Constructs an AudioSample object that gets its audio from stream.
   * 
   * @param stream the stream that will provide the audio
   */
  public AudioSample(AudioStream stream)
  {
    super(stream);
  }
  
  /**
   * 
   * @return the AudioMetaData for the sample.
   */
  public abstract AudioMetaData getMetaData();
}
