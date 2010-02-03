package ddf.minim.ugens;

import ddf.minim.spi.AudioRecordingStream;

public class FilePlayer extends UGen 
{
	private AudioRecordingStream mFileStream;
	
	public FilePlayer( AudioRecordingStream iFileStream )
	{
		mFileStream = iFileStream;
		// we'll need to do this eventually, I think.
		// but for now we don't need this because it starts the iothread,
		// which is not what we want.
		// mFileStream.open();	
		mFileStream.play();
	}
	
	
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
