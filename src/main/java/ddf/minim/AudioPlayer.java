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

import ddf.minim.spi.AudioOut;
import ddf.minim.spi.AudioRecordingStream;

/**
 * An <code>AudioPlayer</code> provides a self-contained way of playing a 
 * sound file by streaming it from disk (or the internet). It
 * provides methods for playing and looping the file, as well 
 * as methods for setting the position in the file and 
 * looping a section of the file. You can obtain an 
 * <code>AudioPlayer</code> by using the loadFile method of the Minim 
 * class.
 * 
 * @example Basics/PlayAFile
 * 
 * @related Minim
 * 
 * @author Damien Di Fede
 */

public class AudioPlayer extends AudioSource implements Playable
{
	// the rec that this plays
	private AudioRecordingStream	recording;
	private AudioOut		output;
	// only set to true is pause is called
	private boolean isPaused;

	/**
	 * Constructs an <code>AudioPlayer</code> that plays <code>recording</code> using
	 * the <code>AudioOut</code> provided. Generally you will not call this directly
	 * and will instead use the <code>Minim.loadFile</code> method.
	 * 
	 * @see Minim#loadFile(String)
	 * 
	 * @param recording
	 *           the <code>AudioRecordingStream</code> to play
	 *           
	 * @param out the <code>AudioOut</code> to play the recording on
	 *           
	 * @invisible
	 */
	public AudioPlayer(AudioRecordingStream recording, AudioOut out)
	{
		super(out);
		this.recording = recording;
		output = out;
		// output.setAudioSignal( new StreamSignal(recording, output.bufferSize()) );
		output.setAudioStream(recording);
	}

   /**
    * Starts playback from the current position. 
    * If this was previously set to loop, looping will be disabled.
    * 
    * @shortdesc Starts playback from the current position.
    * 
    * @example Basics/PlayAFile
    * 
    * @related AudioPlayer
    */
	public void play()
	{
		recording.play();
		isPaused = false;
	}

   /**
    * Starts playback some number of milliseconds into the file. 
    * If this was previously set to loop, looping will be disabled.
    * 
    * @shortdesc Starts playback some number of milliseconds into the file.
    * 
    * @param millis 
    * 			int: how many milliseconds from the beginning of the file to begin playback from
    * 
    *  @related AudioPlayer
    */
	public void play(int millis)
	{
		cue(millis);
		play();
	}

	/**
	 * Pauses playback.
	 * 
	 * @example AudioPlayer/pause
	 * 
	 * @related AudioPlayer
	 */
	public void pause()
	{
		recording.pause();
		isPaused = true;
	}

   /**
    * Rewinds to the beginning. This <i>does not</i> stop playback.
    * 
    * @example AudioPlayer/rewind
    * 
    * @related AudioPlayer
    */
	public void rewind()
	{
		cue(0);
	}

   /**
    * Set the <code>AudioPlayer</code> to loop some number of times.
    * If it is already playing, the position
    * <i>will not</i> be reset to the beginning. 
    * If it is not playing, it will start playing.
    * If you previously called this method and then paused the
    * <code>AudioPlayer</code>, you can resume looping
    * by using the result of <code>getLoopCount()</code> as
    * the argument for this method. 
    * To loop indefinitely, use <code>loop()</code>.
    * 
    * @shortdesc Set the <code>AudioPlayer</code> to loop some number of times.
    * 
    * @param num
    *          int: the number of times to loop
    *          
    * @example AudioPlayer/loopNum
    *          
	* @related AudioPlayer
    */
	public void loop(int num)
	{
		// if we were paused, we need to grab the current state because calling loop totally resets it.
		// Issue #72: if the recording is currently playing, we also need to do this,
		// otherwise we start the loop over, which contradicts the above documentation.
		//
		// If this has never been paused before and the stream isn't playing,
		// then people probably will expect the file to start playing from the loopStart, not from the beginning.
		if ( isPaused || recording.isPlaying() )
		{
			int pos = recording.getMillisecondPosition();
			recording.loop( num );
			recording.setMillisecondPosition(pos);
		}
		else
		{
			recording.loop(num);
		}
		
		isPaused = false;
	}
	
   /**
    * Sets the <code>AudioPlayer</code> to loop indefinitely. 
    * If it is already playing, the position
    * <i>will not</i> be reset to the beginning. 
    * If it is not playing, it will start playing.
    * 
    * @shortdesc Sets the <code>AudioPlayer</code> to loop indefinitely.
    * 
    * @example AudioPlayer/loop
    * 
    * @related AudioPlayer
    */
	public void loop()
	{
		loop(Minim.LOOP_CONTINUOUSLY);
	}

   /**
    * Returns the number of loops left to do. 
    * 
    * @return int: the number of loops left
    * 
    * @example AudioPlayer/loopNum
    * 
    * @related AudioPlayer
    */
	public int loopCount()
	{
		return recording.getLoopCount();
	}

   /**
    * Returns the length of the sound in milliseconds. If for any reason the 
    * length could not be determined, this will return -1. However, an unknown 
    * length should not impact playback.
    * 
    * @shortdesc Returns the length of the sound in milliseconds.
    * 
    * @return int: the length of the sound in milliseconds
    * 
    * @example Advanced/CueAnAudioPlayer
    * 
    * @related AudioPlayer
    */
	public int length()
	{
		return recording.getMillisecondLength();
	}

   /**
    * Returns the current position of the "playhead" in milliseconds
    * (ie how much of the sound has already been played).
    * 
    * @example Advanced/CueAnAudioPlayer
    * 
    * @return int: the current position of the "playhead" in milliseconds
    * 
    * @related AudioPlayer
    */
	public int position()
	{
		return recording.getMillisecondPosition();
	}

   /**
    * Sets the position to <code>millis</code> milliseconds from
    * the beginning. This will not change the play state. If an error
    * occurs while trying to cue, the position will not change. 
    * If you try to cue to a negative position or to a position 
    * that is greater than a non-negative <code>length()</code>, 
    * the amount will be clamped to zero or <code>length()</code>.
    * 
    * @shortdesc Sets the position to <code>millis</code> milliseconds from
    * the beginning.
    * 
    * @example Advanced/CueAnAudioPlayer
    * 
    * @param millis 
    * 			int: the millisecond position to place the "playhead"
    * 
    * @related length ( )
    * @related AudioPlayer
    */
	public void cue(int millis)
	{
		if (millis < 0)
	    {
			millis = 0;
	    }
	    else	    	
	    {
	    	// only clamp millis to the length of the file if the length is known.
	    	// otherwise we will try to skip what is asked and count on the underlying stream to handle it.
	    	int len = recording.getMillisecondLength();
	    	if (len >= 0 && millis > len)
	    	{
	    		millis = len;
	    	}
	    }
		recording.setMillisecondPosition(millis);
	}

	/**
	 * Skips <code>millis</code> milliseconds from the current position. 
	 * <code>millis</code> can be negative, which will make this skip backwards. 
	 * If the skip amount would result in a negative position or a position that is greater than 
	 * a non-negative <code>length()</code>, the new position will be clamped to zero or <code>length()</code>.
	 * 
	 * @shortdesc Skips <code>millis</code> milliseconds from the current position.
	 * 
	 * @param millis 
	 * 			int: how many milliseconds to skip, sign indicates direction
	 * 
	 * @example AudioPlayer/skip
	 * 
	 * @related AudioPlayer
	 */
	public void skip(int millis)
	{
		int pos = position() + millis;
		if ( pos < 0 )
		{
			pos = 0;
		}
		
		Minim.debug("AudioPlayer.skip: attempting to skip " + millis + " milliseconds, to position " + pos);
		cue(pos);
	}

   /**
    * Returns true if the <code>AudioPlayer</code> is currently playing 
    * and has more than one loop left to play.
    * 
    * @return true if this is looping, false if not
    * 
    * @example AudioPlayer/loopNum
    * 
    * @related AudioPlayer
    */
	public boolean isLooping()
	{
		return recording.getLoopCount() != 0;
	}

   /**
    * Indicates if the <code>AudioPlayer</code> is currently playing.
    * 
    * @return true if this is currently playing, false if not
    * 
    * @example AudioPlayer/loopNum
    * 
    * @related AudioPlayer
    */
	public boolean isPlaying()
	{
		return recording.isPlaying();
	}

	/**
	 * Returns the meta data for the recording being played by this player.
	 * 
	 * @return AudioMetaData: the meta data for this player's recording
	 * 
	 * @example Basics/GetMetaData
	 * 
	 * @related AudioPlayer
	 * @related AudioMetaData
	 */
	public AudioMetaData getMetaData()
	{
		return recording.getMetaData();
	}

   /**
    * Sets the beginning and end of the section to loop when looping.
    * These should be between 0 and the length of the file.
    * If <code>end</code> is larger than the length of the file,
    * the end of the loop will be set to the end of the file.
    * If the length of the file is unknown and <end> is positive,
    * it will be used directly.
    * If <code>end</code> is negative, the end of the loop 
    * will be set to the end of the file.
    * If <code>begin</code> is greater than <code>end</code> 
    * (unless <code>end</code> is negative), it will be clamped
    * to one millisecond before <code>end</code>.
    * 
    * @param begin 
    * 		int: the beginning of the loop in milliseconds
    * @param end 
    * 		int: the end of the loop in milliseconds, or -1 to set it to the end of the file
    * 
    * @example AudioPlayer/setLoopPoints
    * 
    * @related loop ( )
    * @related getLoopBegin ( )
    * @related getLoopEnd ( )
    * @related AudioPlayer
    */
	public void setLoopPoints(int begin, int end)
	{
		recording.setLoopPoints(begin, end);
	}
	
	/**
	 * Gets the current millisecond position of the beginning of the looped section.
	 * 
	 * @return 
	 * 		int: the beginning of the looped section in milliseconds
	 * 
	 * @example AudioPlayer/setLoopPoints
	 * 
	 * @related setLoopPoints ( )
	 * @related loop ( )
	 * @related AudioPlayer
	 * 
	 */
	public int getLoopBegin()
	{
		return recording.getLoopBegin();
	}

	/**
	 * Gets the current millisecond position of the end of the looped section.
	 * This can be -1 if the length is unknown and <code>setLoopPoints</code> has never been called.
	 * 
	 * @return 
	 * 		int: the end of the looped section in milliseconds
	 * 
	 * @example AudioPlayer/setLoopPoints
	 * 
	 * @related setLoopPoints ( )
	 * @related loop ( )
	 * @related AudioPlayer
	 */
	public int getLoopEnd()
	{
		return recording.getLoopEnd();
	}	
	
	/**
	 * Release the resources associated with playing this file.
	 * All AudioPlayers returned by Minim's loadFile method 
	 * will be closed by Minim when it's stop method is called. 
	 * If you are using Processing, Minim's stop method will be 
	 * called automatically when your application exits.
	 * 
	 * @shortdesc Release the resources associated with playing this file.
	 * 
	 * @related AudioPlayer
	 * 
	 * @invisible 
	 */
	public void close()
	{
		recording.close();
		super.close();
	}
}
