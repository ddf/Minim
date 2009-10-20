package ddf.minim.ugens;

public class Gain extends UGen
{
	// TODO make this a class that might vary the value over time
	private float value;
	
	public Gain(float gainVal)
	{
		value = gainVal;
	}

	@Override
	public void tick(float[] channels) 
	{
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] *= value;
		}
	} 
}