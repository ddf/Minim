package ddf.minim.ugens;

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
	private float dampNow;
	// the damp has been activated
	private boolean isActivated;
	
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
		dampNow = 0f;  // TODO test value
		isActivated = false;
		Minim.debug(" dampTime = " + dampTime + " begAmp = " + begAmp + " now = " + dampNow);
	}
	public void activate()
	{
		dampNow = 0f;
		isActivated = true;
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
		if ((!isActivated) || (dampNow >= dampTime))
		{
			for(int i = 0; i < channels.length; i++)
			{
				//channels[i] = 0.1f*audio.getLastValues()[i];
				channels[i] = 0.0f;
			}
		} else 
		{
			// TODO if samplerate changes in the middle of Damp, there will be a click
			// TODO need to change to method as in ADSR
			amp = begAmp*(1 - (dampNow/dampTime));
			//Minim.debug(" dampTime = " + dampTime + " begAmp = " + begAmp + " amp = " + amp + " now = " + now);
				for(int i = 0; i < channels.length; i++)
			{
				float tmp = audio.getLastValues()[i];
				tmp *= amp;
				channels[i] = tmp;
			}
			dampNow += timeStepSize;
		}
	}
}
