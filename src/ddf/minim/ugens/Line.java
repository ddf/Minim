package ddf.minim.ugens;

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
	// the time from begAmp to endAmp
	private float lineTime;
	// the current size of the step
	private float timeStepSize;
	// the current time
	private float lineNow;
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
	public Line(float lT, float begAmplitude, float endAmplitude)
	{
		super();
		lineTime = lT;
		begAmp = begAmplitude;
		amp = begAmp;
		endAmp = endAmplitude;
		lineNow = 0f;  // TODO test value
		isActivated = false;
		Minim.debug(" dampTime = " + lineTime + " begAmp = " + begAmp + " now = " + lineNow);
	}
	public void activate()
	{
		lineNow = 0f;
		isActivated = true;
	}
	public void setEndAmp( float newEndAmp )
	{
		endAmp = newEndAmp;
	}
	public void setLineTime( float newLineTime )
	{
		lineTime = newLineTime;
	}
	
	protected void sampleRateChanged()
	{
		timeStepSize = 1/sampleRate();
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
		} else if (lineNow >= lineTime)
		{
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = endAmp;
			}
		} else 
		{
			// TODO if samplerate changes in the middle of Line, there will be a click
			// TODO need to change to method as in ADSR
			//amp = begAmp*(1 - (lineNow/lineTime)) + endAmp*(lineNow/lineTime);
			amp += ( endAmp - amp )*timeStepSize/( lineTime - lineNow );
			//Minim.debug(" dampTime = " + dampTime + " begAmp = " + begAmp + " amp = " + amp + " dampNow = " + dampNow);
				for(int i = 0; i < channels.length; i++)
			{
				channels[i] = amp;
			}
			lineNow += timeStepSize;
		}
	}
}