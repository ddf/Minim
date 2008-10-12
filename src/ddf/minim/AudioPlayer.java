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

import ddf.minim.spi.AudioRecording;
import ddf.minim.spi.AudioRecordingStream;

/**
 * An <code>AudioPlayer</code> is used for playing an
 * <code>AudioRecording</code>. Strictly speaking, you don't need an
 * <code>AudioPlayer</code> to play an <code>AudioRecording</code>, because
 * the recording is itself <code>Playable</code>. However, an
 * <code>AudioPlayer</code> does you the favor of providing
 * <code>AudioBuffer</code>s that are sync'd with the recording's output as
 * well as providing direct control over the <code>DataLine</code> being used
 * to transmit the recording to the system. You can get an
 * <code>AudioPlayer</code> by calling {@link Minim#loadFile(String)}, but
 * you can also construct one yourself if you've written your own implementation
 * of <code>AudioRecording</code>.
 * 
 * @author Damien Di Fede
 */

public class AudioPlayer extends AudioSource implements Playable
{
	// the rec that this plays
	private AudioRecording	recording;

	/**
	 * Constructs an <code>AudioPlayer</code> that plays <code>recording</code>.
	 * It is expected that <code>recording</code> will have a
	 * <code>DataLine</code> to control. If it doesn't, any calls to
	 * <code>Controller</code>'s methods will result in a
	 * <code>NullPointerException</code>.
	 * 
	 * @param recording
	 *           the <code>AudioRecording</code> to play
	 */
	public AudioPlayer(AudioRecordingStream recording)
	{
		super(recording);
		this.recording = recording;
	}

	public void play()
	{
		recording.play();
	}

	public void play(int millis)
	{
		cue(millis);
		play();
	}

	public void pause()
	{
		recording.pause();
	}

	public void rewind()
	{
		cue(0);
	}

	public void loop()
	{
		recording.loop(Minim.LOOP_CONTINUOUSLY);
	}

	public void loop(int n)
	{
		recording.loop(n);
	}

	public int loopCount()
	{
		return recording.getLoopCount();
	}

	public int length()
	{
		return recording.getMillisecondLength();
	}

	public int position()
	{
		return recording.getMillisecondPosition();
	}

	public void cue(int millis)
	{
		if (millis < 0)
    {
			millis = 0;
    }
    else if (millis > length())
    {
			millis = length();
    }
		recording.setMillisecondPosition(millis);
	}

	public void skip(int millis)
	{
		int pos = position() + millis;
		if (pos < 0)
    {
			pos = 0;
    }
		else if (pos > length())
    {
			pos = length();
    }
    Minim.debug("AudioPlayer.skip: skipping " + millis + " milliseconds, new position is " + pos);
		recording.setMillisecondPosition(pos);
	}

	public boolean isLooping()
	{
		return recording.getLoopCount() != 0;
	}

	public boolean isPlaying()
	{
		return recording.isPlaying();
	}

	/**
	 * Returns the meta data for the recording being played by this player.
	 * 
	 * @return the meta data for this player's recording
	 */
	public AudioMetaData getMetaData()
	{
		return recording.getMetaData();
	}

	public void setLoopPoints(int start, int stop)
	{
		recording.setLoopPoints(start, stop);

	}
}
