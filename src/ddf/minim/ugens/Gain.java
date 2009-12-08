package ddf.minim.ugens;

import ddf.minim.Minim;

public class Gain extends UGen
{
	// jam3: define the inputs to gain
    
	public UGenInput audio;
	public UGenInput amplitude;
	private float value;
	
	public Gain()
	{
		this( 0f );
	}
	
	public Gain(float gainVal)
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		//audio = new UGenInput(InputType.AUDIO);
		audio = new UGenInput(InputType.AUDIO);
		amplitude = new UGenInput(InputType.CONTROL);
		value = gainVal;
	}
	
	public void setValue(float gainVal)
	{
		value = gainVal;
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		for(int i = 0; i < channels.length; i++)
		{
			float tmp = audio.getIncomingUGen().getLastValues()[i];
			if ((amplitude == null) || (!amplitude.isPatched()))
			{
				tmp *= value;
			} else {
				tmp *= amplitude.getIncomingUGen().getLastValues()[i];
			}
			channels[i] = tmp;
		}
	} 
}