package ddf.minim.ugens;


/**
 * Abs is a UGen that outputs the absolue value of its input.
 * 
 * @author Damien Di Fede
 *
 */
public class Abs extends UGen 
{
	/**
	 * The input that we will take the absolute value of.
	 */
	public UGenInput audio;
	
	public Abs()
	{
		audio = new UGenInput(InputType.AUDIO);
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		for(int i = 0; i < channels.length; ++i)
		{
			channels[i] = Math.abs( audio.getLastValues()[i] );
		}
	}

}
