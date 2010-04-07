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
    // the current frequency at which we oscillate
	private Frequency freq;
	// our amplitude in the absence of an amplitude input
	private float 	  amp;
	// the phase of the oscillator in the absence of a phase input
	private float     fPhase;
	// where we will sample our waveform, moves between [0,1]
	private float step;
	// the step size we will use to advance our step
	private float stepSize;
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
	public Oscil(Frequency freq, float amp, Waveform wave)
	{
		super();
		this.amplitude = new UGenInput(InputType.CONTROL);
		this.frequency = new UGenInput(InputType.CONTROL);
		this.phase = new UGenInput(InputType.CONTROL);
		this.wave = wave;
		this.freq = freq;
		this.amp = amp;
		step = 0f;
		fPhase = 0f;
		oneOverSampleRate = 1.f;
	}	
	
	/**
	 * This routine will be called any time the sample rate changes.
	 */
	protected void sampleRateChanged()
	{
		oneOverSampleRate = 1 / sampleRate();
		stepSizeChanged();
	}
	
	// updates our step size based on the current frequency
	private void stepSizeChanged()
	{
		stepSize = freq.asHz() * oneOverSampleRate;
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
		freq.setAsHz(hz);
		stepSizeChanged();
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
		freq.setAsHz( newFreq.asHz() );
		stepSizeChanged();
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
		fPhase = newPhase;
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
		step = fPhase;
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{		
		// start with our base amplitude
		float outAmp = amp;
		
		// if something is plugged into amplitude
		// then we override our base amplitude
		if ( amplitude.isPatched() )
		{
			outAmp = amplitude.getLastValues()[0];
		}
		
		// calculate the sample value
		float sample = outAmp * wave.value(step);
		
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = sample;
		}
		
		// if something is plugged into frequency
		// we update our frequency and recalculate our stepSize
		if ( frequency.isPatched() )
		{
			freq.setAsHz( frequency.getLastValues()[0] );
			stepSizeChanged();
		}
		
		if ( phase.isPatched() )
		{
			fPhase = phase.getLastValues()[0];
		}
		
		// increase time
		step += stepSize + fPhase;
		
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
