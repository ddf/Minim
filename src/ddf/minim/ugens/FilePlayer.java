package ddf.minim.ugens;

import ddf.minim.spi.AudioRecordingStream;

/**
 * The FilePlayer UGen provides a way for you to wrap an AudioRecordingStream with 
 * the UGen interface, allowing you to patching into a UGen graph any way you choose.
 * You can get an AudioRecordingStream from Minim by calling Minim.loadFileStream.
 * 
 * @author Damien Di Fede
 *
 */

public class FilePlayer extends UGen 
{
	private AudioRecordingStream mFileStream;
	
	/**
	 * Construct a FilePlayer that will read from iFileStream.
	 * @param iFileStream the AudioRecordingStream this should read from
	 */
	public FilePlayer( AudioRecordingStream iFileStream )
	{
		mFileStream = iFileStream;
		// we'll need to do this eventually, I think.
		// but for now we don't need this because it starts the iothread,
		// which is not what we want.
		// mFileStream.open();	
		mFileStream.play();
	}
	
	/**
	 * Returns the wrapped AudioRecordingStream so that you can manipulate it while it 
	 * plays back, by calling pause, play, skip, etc.
	 * 
	 * @return the wrapped AudioRecordingStream
	 */
	public AudioRecordingStream getStream()
	{
		return mFileStream;
	}
	
	/**
	 * Calling close will close the AudioStream that this wraps, 
	 * which is proper cleanup for using the stream.
	 */
	public void close()
	{
		mFileStream.close();
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		float[] samples = mFileStream.read();
		// TODO: say the input is mono and output is stereo, what should we do?
		// should we just copy like this and have the input come in the 
		// left side? Or should we somehow expand across the extra channels?
		// what about the opposite problem? stereo input to mono output?
		int length = ( samples.length >= channels.length ) ? channels.length : samples.length;
		System.arraycopy(samples, 0, channels, 0, length);
	}

}
