package ddf.minim.ugens;

import ddf.minim.spi.AudioStream;

public class Input extends UGen 
{
	private AudioStream mInputStream;

	public Input( AudioStream inputStream )
	{
		mInputStream = inputStream;
		inputStream.open();
	}
	
	/**
	 * Calling close will close the AudioStream that this wraps, 
	 * which is proper cleanup for using the stream.
	 */
	public void close()
	{
		mInputStream.close();
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		float[] samples = mInputStream.read();
		// TODO: say the input is mono and output is stereo, what should we do?
		// should we just copy like this and have the input come in the 
		// left side? Or should we somehow expand across the extra channels?
		// what about the opposite problem? stereo input to mono output?
		int length = ( samples.length >= channels.length ) ? channels.length : samples.length;
		System.arraycopy(samples, 0, channels, 0, length);
	}

}
