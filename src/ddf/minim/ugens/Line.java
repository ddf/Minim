package ddf.minim.ugens;

import ddf.minim.ugens.UGen.InputType;
import ddf.minim.ugens.UGen.UGenInput;
import ddf.minim.Minim;

/**
 * A UGen that starts at a value
 * and changes linearly to another value over a specified time.
 * @author nodog
 *
 */
public class Line extends UGen
{
	// jam3: define the inputs to Oscil
	// the initial amplitude
	private float begAmp;
	// the ending amplitude
	private float endAmp;
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
	public Line()
	{
		this(1.0f, 1.0f, 0.0f);
	}
	public Line(float dT)
	{
		this(dT, 1.0f, 0.0f);
	}
	public Line(float dT, float beginningAmplitude)
	{
		this(dT, beginningAmplitude, 0.0f);
	}
	public Line(float dT, float begAmplitude, float endAmplitude)
	{
		super();
		dampTime = dT;
		begAmp = begAmplitude;
		endAmp = endAmplitude;
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
		if (!isActivated)
		{
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = begAmp;
			}
		} else if (dampNow >= dampTime)
		{
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = endAmp;
			}
		} else 
		{
			// TODO if samplerate changes in the middle of Line, there will be a click
			// TODO need to change to method as in ADSR
			amp = begAmp*(1 - (dampNow/dampTime)) + endAmp*(dampNow/dampTime);
			//Minim.debug(" dampTime = " + dampTime + " begAmp = " + begAmp + " amp = " + amp + " dampNow = " + dampNow);
				for(int i = 0; i < channels.length; i++)
			{
				channels[i] = amp;
			}
			dampNow += timeStepSize;
		}
	}
}