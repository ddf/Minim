package ddf.minim.ugens;

import ddf.minim.ugens.UGen.InputType;
import ddf.minim.ugens.UGen.UGenInput;
import ddf.minim.Minim;

/**
 * A UGen that plays audio through a standard ADSR
 * envelope based on time from noteOn and noteOff
 * @author nodog
 *
 */
public class ADSR extends UGen
{
	// jam3: define the inputs to Oscil
	public UGenInput audio;

	// the max amplitude of the envelope
	private float maxAmplitude;
	// the current amplitude
	private float amplitude;
	// the time of the attack
	private float attackTime;
	// the time of the decay
	private float decayTime;
	// the level of the sustain
	private float sustainLevel;
	// the time of the release
	private float releaseTime;
	// the current size of the step
	private float timeStepSize;
	// the time from noteOn
	private float timeFromOn;
	// the time from noteOff
	private float timeFromOff;
	// the envelope has received noteOn
	private boolean isTurnedOn;
	// the envelope has received noteOff
	private boolean isTurnedOff;
	
	// constructors
	public ADSR()
	{
		this(1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
	}
	public ADSR(float maxAmp)
	{
		this(maxAmp, 1.0f, 1.0f, 1.0f, 1.0f);
	}
	public ADSR(float maxAmp, float attTime)
	{
		this(maxAmp, attTime, 1.0f, 1.0f, 1.0f);
	}
	public ADSR(float maxAmp, float attTime, float decTime)
	{
		this(maxAmp, attTime, decTime, 1.0f, 1.0f);
	}
	public ADSR(float maxAmp, float attTime, float decTime, float susLvl)
	{
		this(maxAmp, attTime, decTime, susLvl, 1.0f);
	}
	public ADSR(float maxAmp, float attTime, float decTime, float susLvl, float relTime)
	{
		super();
		audio = new UGenInput(InputType.AUDIO);
		maxAmplitude = maxAmp;
		attackTime = attTime;
		decayTime = decTime;
		sustainLevel = susLvl;
		releaseTime = relTime;
		amplitude = 0.0f;
		isTurnedOn = false;
		isTurnedOff = false;
		timeFromOn = -1.0f;
		timeFromOff = -1.0f;
		//Minim.debug(" dampTime = " + dampTime + " begAmp = " + begAmp + " now = " + dampNow);
	}
	public void noteOn()
	{
		timeFromOn = 0f;
		isTurnedOn = true;
	}
	public void noteOff()
	{
		timeFromOff = 0f;
		isTurnedOff = true;
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
		// before or after the envelope, just output zero
		if ((!isTurnedOn) || (timeFromOff >= releaseTime))
		{
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = 0.0f;
			}
		}
		// inside the envelope
		else
		{
			if ((isTurnedOn) && (!isTurnedOff))
			{
				// ATTACK
				if (timeFromOn <= attackTime)
				{
					// use time remaining until maxAmplitude to change amplitude
					float timeRemain = (attackTime - timeFromOn);
					amplitude += (maxAmplitude - amplitude)*timeStepSize/timeRemain;				
				}
				// DECAY
				else if ((timeFromOn > attackTime) && (timeFromOn <= (attackTime+decayTime)))
				{
					float timeRemain = (attackTime + decayTime - timeFromOn);
					amplitude += (sustainLevel*maxAmplitude - amplitude)*timeStepSize/timeRemain;			
				}
				// SUSTAIN
				else if (timeFromOn > (attackTime+decayTime))
				{
					amplitude = sustainLevel*maxAmplitude;
				}
				timeFromOn += timeStepSize;
			}
			// RELEASE
			else //isTurnedOn and isTurnedOFF and timeFromOff < releaseTime
			{
				float timeRemain = (releaseTime - timeFromOff);
				amplitude += (0 - amplitude)*timeStepSize/timeRemain;
				timeFromOff += timeStepSize;
			}
			
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = amplitude*audio.getLastValues()[i];
			}		
		}
	}
}
