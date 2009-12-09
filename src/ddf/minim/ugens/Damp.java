package ddf.minim.ugens;

import ddf.minim.ugens.UGen.InputType;
import ddf.minim.ugens.UGen.UGenInput;
import ddf.minim.Minim;

/**
 * A UGen that starts at an amplitude value
 * and changes to zero over a specified time.
 * @author nodog
 *
 */
public class Damp extends UGen
{
	// jam3: define the inputs to Oscil
	public UGenInput audio;

	// the initial amplitude of the damp
	private float begAmp;
	// the current amplitude
	private float amp;
	// the time from amp to 0
	private float dampTime;
	// the current size of the step
	private float timeStepSize;
	// the current time
	private float now;
	
	// constructors
	public Damp()
	{
		this(1.0f, 1.0f);
	}
	public Damp(float dT)
	{
		this(dT, 1.0f);
	}
	public Damp(float dT, float beginningAmplitude)
	{
		super();
		audio = new UGenInput(InputType.AUDIO);
		dampTime = dT;
		begAmp = beginningAmplitude;
		now = 0f;
		Minim.debug(" dampTime = " + dampTime + " begAmp = " + begAmp + " now = " + now);
	}
	public void sampleRateChanged()
	{
		timeStepSize = 1/sampleRate;
		setSampleRate(sampleRate);
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		//Minim.debug(" dampTime = " + dampTime + " begAmp = " + begAmp + " now = " + now);
		if (now >= dampTime)
		{
			for(int i = 0; i < channels.length; i++)
			{
				//channels[i] = 0.1f*audio.getLastValues()[i];
				channels[i] = 0.0f;
			}
		} else 
		{
			// TODO if samplerate changes in the middle of this damp, there will be a glitch
			amp = begAmp*(1 - (now/dampTime));
			for(int i = 0; i < channels.length; i++)
			{
				float tmp = audio.getLastValues()[i];
				tmp *= 0.5;
				channels[i] = tmp;
			}
		}
		now += timeStepSize;
	}
}
