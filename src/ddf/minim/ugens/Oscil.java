package ddf.minim.ugens;

/**
 * Provides a UGen which generates a Waveform at a specified frequency.
 * @author Damien Di Fede, Anderson Mills
 *
 */
public class Oscil extends UGen 
{
	/**
	 * Patch to this to control the amplitude of the oscillator with another UGen.
	 */
	public UGenInput amplitude;

	/**
	 * Patch to this to control the frequency of the oscillator with another UGen.
	 */
	public UGenInput frequency;
	
	/**
	 * Patch to this to control the phase of the oscillator with another UGen.
	 */
	public UGenInput phase;
	
	// the waveform we will oscillate over
	private Waveform  wave;

  // where we will sample our waveform, moves between [0,1]
	private float step;
	// the step size we will use to advance our step
	private float stepSize;
	// what was our frequency from the last time we updated our step size
  // stashed so that we don't do more math than necessary
  private float prevFreq;
	// 1 / sampleRate, which is used to calculate stepSize
	private float oneOverSampleRate;
	
	//constructors
	/**
	 * Constructs an Oscil UGen given frequency in Hz, amplitude, and a waveform
	 * @param frequencyInHertz the frequency this should oscillate at
	 * @param amplitude the base amplitude
	 * @param waveform the waveform we will oscillate over
	 */
	public Oscil(float frequencyInHertz, float amplitude, Waveform waveform)
	{
		this(Frequency.ofHertz(frequencyInHertz), amplitude, waveform);
	}
	
	/**
	 * Constructs an Oscil UGen given frequency in Hz and amplitude.
	 * This oscillator uses a sine wave.
	 * @param frequencyInHertz the frequency this should oscillate at
	 * @param amplitude the amplitude
	 */
	public Oscil(float frequencyInHertz, float amplitude)
	{
		this(Frequency.ofHertz(frequencyInHertz), amplitude);
	}
	
	/**
	 * Constructs an Oscil UGen given a Frequency and amplitude.
	 * This oscillator uses a sine wave.
	 * @param frequency the frequency this should oscillate at
	 * @param amplitude the amplitude
	 */
	//shortcut for building a sine wave
	public Oscil(Frequency frequency, float amplitude)
	{
		this(frequency, amplitude, Waves.SINE);
	}

	/**
	 * Constructs an Oscil UGen given a Frequency, amplitude, and a waveform
	 * @param frequency
	 * @param amplitude
	 * @param waveform
	 */
	//standard constructor
	public Oscil(Frequency freq, float amp, Waveform waveform)
	{
		super();
    
		amplitude = new UGenInput(InputType.CONTROL);
    amplitude.setLastValue(amp);
		
    frequency = new UGenInput(InputType.CONTROL);
    frequency.setLastValue(freq.asHz());
    
		phase = new UGenInput(InputType.CONTROL);
    phase.setLastValue(0.f);
		
    wave = waveform;
		step = 0f;
		oneOverSampleRate = 1.f;
	}	
	
	/**
	 * This routine will be called any time the sample rate changes.
	 */
	protected void sampleRateChanged()
	{
		oneOverSampleRate = 1 / sampleRate();
    // don't call updateStepSize because it checks for frequency change
    stepSize = frequency.getLastValue() * oneOverSampleRate;
    prevFreq = frequency.getLastValue();
	}
	
	// updates our step size based on the current frequency
	private void updateStepSize()
	{
    float currFreq = frequency.getLastValue();
    if ( prevFreq != currFreq )
    {
      stepSize = currFreq * oneOverSampleRate;
      prevFreq = currFreq;
    }
	}
	
	/**
	 * Sets the frequency of this Oscil. You might want to do this to change 
	 * the frequency of this Oscil in response to a button press or something.
	 * For controlling frequency continuously over time you will usually want 
	 * to use the frequency input.
	 * 
	 * @param hz the frequency, in Hertz, to set this Oscil to
	 */
	public void setFrequency( float hz )
	{
		frequency.setLastValue(hz);
		updateStepSize();
	}
	
	/**
	 * Sets the frequency of this Oscil. You might want to do this to change 
	 * the frequency of this Oscil in response to a button press or something.
	 * For controlling frequency continuously over time you will usually want 
	 * to use the frequency input.
	 * 
	 * @param freq the Frequency to set this Oscil to
	 */
	public void setFrequency( Frequency newFreq )
	{
		frequency.setLastValue( newFreq.asHz() );
		updateStepSize();
	}

	/**
	 * Sets the amplitude of this Oscil. You might want to do this to change 
	 * the amplitude of this Oscil in response to a button press or something.
	 * For controlling amplitude continuously over time you will usually want 
	 * to use the amplitude input.
	 * 
	 * @param newAmp amplitude to set this Oscil to
	 */
	public void setAmplitude( float newAmp )
	{
    amplitude.setLastValue(newAmp);
	}
	
	/**
	 * Set the amount that the phase will be offset by. Oscil steps its time 
	 * from 0 to 1, which means that the phase is also normalized. However, 
	 * it still makes sense to set the phase to greater than 1 or even to 
	 * a negative number.
	 * 
	 * @param newPhase 
	 */
	public void setPhase( float newPhase )
	{
    phase.setLastValue(newPhase);
	}
	
	/**
	 * Resets the time-step used by the oscillator to be equal to the 
	 * current phase value.  You will typically use this when starting a 
	 * new note with an Oscil that you have already used so that the 
	 * waveform will begin sounding at the beginning of its period, 
	 * which will typically be a zero-crossing. In other words, use this
	 * to prevent clicks when starting Oscils that have been used before.
	 */
	public void reset()
	{
		step = phase.getLastValue();
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{		
		// start with our base amplitude
		float outAmp = amplitude.getLastValue();
		
		// temporary step location with phase offset.
		float tmpStep = step + phase.getLastValue();
		// don't be less than zero
		if ( tmpStep < 0.f )
		{
			tmpStep -= (int)tmpStep - 1f;
		}
		// don't exceed 1.
		// we don't use Math.floor because that involves casting up 
		// to a double and then back to a float.
		if ( tmpStep > 1.0f )
		{
			tmpStep -= (int)tmpStep;
		}
		
		// calculate the sample value
		float sample = outAmp * wave.value(tmpStep);
		
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = sample;
		}
		
		// update our step size. 
    // this will check to make sure the frequency has changed.
		updateStepSize();
		
		// increase time
		// NOT THIS FROM BEFORE: step += stepSize + fPhase;
		step += stepSize;
		
		// don't be less than zero
		if ( step < 0.f )
		{
			step -= (int)step - 1f;
		}
		
		// don't exceed 1.
		// we don't use Math.floor because that involves casting up 
		// to a double and then back to a float.
		if ( step > 1.0f )
		{
			step -= (int)step;
		}
	}
}
