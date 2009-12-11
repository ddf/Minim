package ddf.minim.ugens;

import ddf.minim.Minim;

/**
 * Just outputs a constant value.
 * Can be changed with the setValue method.
 * @author nodog
 *
 */
public class Constant extends UGen
{
	private float value;
	
	public Constant()
	{
		this( 1.0f );
	}
	
	public Constant(float val)
	{
		super();
		value = val;
	}
	
	public void setValue(float val)
	{
		value = val;
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = value;
		}
	} 
}