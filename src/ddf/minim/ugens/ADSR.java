package ddf.minim.ugens;

import ddf.minim.AudioOutput;
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

	// amplitude before the ADSR hits
	private float beforeAmplitude;
	// amplitude after the release of the ADSR
	private float afterAmplitude;
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
	// unpatch the note after it's finished
	private boolean unpatchAfterNoteFinished;
	private int unpatchCountdown;
	private AudioOutput output;
	
	// constructors
	public ADSR()
	{
		this(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f);
	}
	public ADSR(float maxAmp)
	{
		this(maxAmp, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f);
	}
	public ADSR(float maxAmp, float attTime)
	{
		this(maxAmp, attTime, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f);
	}
	public ADSR(float maxAmp, float attTime, float decTime)
	{
		this(maxAmp, attTime, decTime, 1.0f, 1.0f, 0.0f, 0.0f);
	}
	public ADSR(float maxAmp, float attTime, float decTime, float susLvl)
	{
		this(maxAmp, attTime, decTime, susLvl, susLvl, 0.0f, 0.0f);
	}
	public ADSR(float maxAmp, float attTime, float decTime, float susLvl, float relTime)
	{
		this(maxAmp, attTime, decTime, susLvl, relTime, 0.0f, 0.0f);
	}
	public ADSR(float maxAmp, float attTime, float decTime, float susLvl, float relTime, float befAmp)
	{
		this(maxAmp, attTime, decTime, susLvl, relTime, befAmp, 0.0f);
	}
	public ADSR(float maxAmp, float attTime, float decTime, float susLvl, float relTime, float befAmp, float aftAmp)
	{
		super();
		audio = new UGenInput(InputType.AUDIO);
		maxAmplitude = maxAmp;
		attackTime = attTime;
		decayTime = decTime;
		sustainLevel = susLvl;
		releaseTime = relTime;
		beforeAmplitude = befAmp;
		afterAmplitude = aftAmp;
		amplitude = beforeAmplitude;
		isTurnedOn = false;
		isTurnedOff = false;
		timeFromOn = -1.0f;
		timeFromOff = -1.0f;
		unpatchAfterNoteFinished = false;
		//Minim.debug(" dampTime = " + dampTime + " begAmp = " + begAmp + " now = " + dampNow);
	}
	public void setParameters( float maxAmp, float attTime, float decTime, float susLvl, float relTime, float befAmp, float aftAmp)
	{
		maxAmplitude = maxAmp;
		attackTime = attTime;
		decayTime = decTime;
		sustainLevel = susLvl;
		releaseTime = relTime;
		beforeAmplitude = befAmp;
		afterAmplitude = aftAmp;
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
	
	public void unpatchAfterNoteFinished(AudioOutput out)
	{
		unpatchAfterNoteFinished = true;
		unpatchCountdown = 0;
		output = out;
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		//Minim.debug(" dampTime = " + dampTime + " begAmp = " + begAmp + " now = " + now);
		// before the envelope, just output the beforeAmplitude*audio
		if (!isTurnedOn)
		{
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = beforeAmplitude*audio.getLastValues()[i];
			}
		}
		// after the envelope, just output the afterAmplitude*audio
		else if (timeFromOff > releaseTime)
		{
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = afterAmplitude*audio.getLastValues()[i];
				
			}
		
			if ( ( unpatchAfterNoteFinished ) && ( unpatchCountdown-- <= 0) )
			{
			 	unpatch(output);
			 	Minim.debug(" unpatching ADSR ");
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
				amplitude += (afterAmplitude - amplitude)*timeStepSize/timeRemain;
				timeFromOff += timeStepSize;
			}
			
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = amplitude*audio.getLastValues()[i];
			}		
		}
	}
}
