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

import ddf.minim.spi.SampleRecorder;


/**
 * An <code>AudioRecorder</code> can be used to record audio that is being
 * played by a <code>Recordable</code> object. An <code>AudioRecorder</code>
 * need not necessarily record to disk, but the recorders you receive from
 * {@link Minim#createRecorder(Recordable, String, boolean)} will do so. If
 * you'd like to save a file somewhere other than the sketches root folder, you
 * can constructor an <code>AudioRecorder</code> directly, using an absolute
 * path for the filename (such as "C:\My Documents\Music\song.wav"). You can
 * also create a recorder that uses your own implementation of
 * {@link SampleRecorder} (like if you wanted to implement an mp3 encoder).
 * 
 * @author Damien Di Fede
 * 
 */

public class AudioRecorder
{
  private Recordable source;
  private SampleRecorder recorder;

  /**
   * Constructs an <code>AudioRecorder</code> that will use
   * <code>recorder</code> to record <code>recordSource</code>.
   * 
   * @param recordSource
   *          the <code>Recordable</code> object to record
   * @param recorder
   *          the <code>SampleRecorder</code> to use to record it
   */
  public AudioRecorder(Recordable recordSource, SampleRecorder recorder)
  {
    source = recordSource;
    this.recorder = recorder;
    source.addListener(recorder);
  }

  /**
   * Begins recording audio from the current record source. If recording was
   * previously halted, and {@link #save()} was not called, samples will be
   * appended to the end of the material recorded so far.
   * 
   */
  public void beginRecord()
  {
    recorder.beginRecord();
  }

  /**
   * Halts the recording of audio from the current record source.
   * 
   */
  public void endRecord()
  {
    recorder.endRecord();
  }

  /**
   * Returns the current record state.
   * 
   * @return true if this is currently recording
   */
  public boolean isRecording()
  {
    return recorder.isRecording();
  }

  /**
   * Requests the current <code>SampleRecorder</code> to save. This will only
   * work if you have called {@link #endRecord()}. If this was created with a
   * buffered recorder, then calling {@link #beginRecord()} after saving will
   * not overwrite the file on the disk, unless this method is subsequently
   * called. However, if this was created with an unbuffered recorder, it is
   * likely that a call to {@link #beginRecord()} will create the file again,
   * overwriting the file that had previously been saved. An
   * <code>AudioRecording</code> will be returned if the
   * <code>SampleRecorder</code> used to record the audio saved to a file
   * (this will always be the case if you use <code>createRecorder</code> or
   * the first constructor for <code>AudioRecorder</code>).
   * 
   * @return the audio that was recorded as an <code>AudioPlayer</code>
   */
  public AudioPlayer save()
  {
    return new AudioPlayer(recorder.save());
  }

  /**
   * Sets the record source for this recorder. The record source can be set at
   * any time, though in practice it is probably a good idea to mute the old
   * record source, then add the new record source, also muted, and then unmute
   * the new record source. Otherwise, you'll probably wind up with a pop in the
   * recording.
   * 
   * @param recordSource
   *          the new record source
   */
  public void setRecordSource(Recordable recordSource)
  {
    source.removeListener(recorder);
    source = recordSource;
    source.addListener(recorder);
  }

  /**
   * Sets the <code>SampleRecorder</code> for this recorder. Similar caveats
   * apply as with {@link #setRecordSource(Recordable)}. This calls
   * <code>endRecord</code> and <code>save</code> on the current
   * <code>SampleRecorder</code> before setting the new one.
   * 
   * @param recorder
   *          the new <code>SampleRecorder</code> to use
   */
  public void setSampleRecorder(SampleRecorder recorder)
  {
    this.recorder.endRecord();
    this.recorder.save();
    source.removeListener(this.recorder);
    source.addListener(recorder);
    this.recorder = recorder;
  }
}
