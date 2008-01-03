package ddf.minim.spi;

import ddf.minim.AudioMetaData;

/**
 * An <code>AudioRecording</code> is a <code>Playable</code> 
 * <code>AudioStream</code>.
 * This usually means that the backing audio is being read from a file.
 * 
 * @author Damien Di Fede
 * 
 */
public interface AudioRecording extends AudioResource
{
	/**
	 * Starts playback of the source.
	 * 
	 */
	void play();

	/**
	 * Stops playback of the source.
	 * 
	 */
	void pause();

	boolean isPlaying();

	/**
	 * Starts looping playback from the current position. Playback will continue
	 * to the loop's end point, then loop back to the loop start point count
	 * times, and finally continue playback to the end of the clip.
	 * 
	 * If the current position when this method is invoked is greater than the
	 * loop end point, playback simply continues to the end of the source without
	 * looping.
	 * 
	 * A count value of 0 indicates that any current looping should cease and
	 * playback should continue to the end of the clip. The behavior is undefined
	 * when this method is invoked with any other value during a loop operation.
	 * 
	 * If playback is stopped during looping, the current loop status is cleared;
	 * the behavior of subsequent loop and start requests is not affected by an
	 * interrupted loop operation.
	 * 
	 * @param count
	 *           the number of times playback should loop back from the loop's
	 *           end position to the loop's start position, or
	 *           Minim.LOOP_CONTINUOUSLY to indicate that looping should continue
	 *           until interrupted
	 */
	void loop(int count);

	/**
	 * Sets the loops points in the source, in milliseconds
	 * 
	 * @param start
	 *           the position of the beginning of the loop
	 * @param stop
	 *           the position of the end of the loop
	 */
	void setLoopPoints(int start, int stop);

	/**
	 * How many loops are left to go. 0 means this isn't looping and -1 means
	 * that it is looping continuously.
	 * 
	 * @return how many loops left
	 */
	int getLoopCount();

	/**
	 * Gets the current millisecond position of the source.
	 * 
	 * @return the current possition, in milliseconds in the source
	 */
	int getMillisecondPosition();

	/**
	 * Sets the current millisecond position of the source.
	 * 
	 * @param pos
	 *           the posititon to cue the playback head to
	 */
	void setMillisecondPosition(int pos);

	/**
	 * Returns the length of the source in milliseconds. Infinite sources, such
	 * as internet radio streams, should return -1.
	 * 
	 * @return the length of the source, in milliseconds
	 */
	int getMillisecondLength();

	/**
	 * Returns meta data about the recording, such as duration, name, ID3 tags
	 * perhaps.
	 * 
	 * @return the MetaData of the recording
	 */
	AudioMetaData getMetaData();
}
