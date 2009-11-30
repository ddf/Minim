package ddf.minim.ugens;

import ddf.minim.Minim;

public class Gain extends UGen
{
	// jam3: define the inputs to gain
    protected static int gainInputs = 2;
	public UGenInput audio;
	public UGenInput amplitude;
	
	// TODO make this a class that might vary the value over time
	private float value;
	
	public Gain(float gainVal)
	{
		super(gainInputs);
		// jam3: I think these can't be constructed until the uGenInputs array
		//       in the super UGen has been constructed
		this.printInputs();
		audio = new UGenInput(0, SignalType.AUDIO);
		this.printInputs();
		amplitude = new UGenInput(1, SignalType.CONTROL);
		this.printInputs();
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
			if ((amplitude == null) || (amplitude.getIncomingUGen() == null))
			{
				tmp *= value;
				//channels[i] *= value;
			} else {
				// TODO fix this for control vs. audio signals
				// TODO fix this kludge put here for the first sample
				//channels[i] *= amplitude.getIncomingUGen().getLastValues()[i];
				tmp *= amplitude.getIncomingUGen().getLastValues()[i];
				//System.out.println("channel " + i + " = " + tmp);
			}
			channels[i] = tmp;
		}
	} 
}