package ddf.minim.ugens;

/**
 * Provides a UGen which iterates through a Waveform at a
 * specified frequency.
 * @author Damien Di Fede, Anderson Mills
 *
 */
public class Oscil extends UGen 
{
	/**
	 * specifies the amplitude of the oscillator.
	 */
	public UGenInput amplitude;
	/**
	 * specifies a change in amplitude of the oscillator
	 */
	public UGenInput amplitudeModulation;
	/**
	 * specifies the frequency of the oscillator
	 */
	public UGenInput frequency;
	/**
	 * specifies a change in frequency of the oscillator
	 */
	public UGenInput frequencyModulation;
	
	/**
	 * specifies the phase of the oscillator
	 */
	public UGenInput phase;
	
	// the waveform we will oscillate over
	private Waveform  wave;
	// the base frequency at which we will oscillate
	private Frequency baseFreq;
    // the current frequency at which we oscillate (includes modulation)
	private Frequency freq;
	// the amplitude at which we will oscillate
	private float 	  amp;
	// the phase of the oscillator
	private float     fPhase;
	// where we will sample our waveform, moves between [0,1]
	private float step;
	// the step size we will use to advance our step
	private float stepSize;
	
	//constructors
	/**
	 * Constructs an Oscil UGen given frequency in Hz, amplitude, and a waveform
	 * @param frequencyInHertz
	 * @param amplitude
	 * @param waveform
	 */
	public Oscil(float frequencyInHertz, float amplitude, Waveform waveform)
	{
		this(Frequency.ofHertz(frequencyInHertz), amplitude, waveform);
	}
	/**
	 * Constructs an Oscil UGen given frequency in Hz and amplitude.
	 * This oscillator uses a sine wave.
	 * @param frequencyInHertz
	 * @param amplitude
	 */
	public Oscil(float frequencyInHertz, float amplitude)
	{
		this(Frequency.ofHertz(frequencyInHertz), amplitude);
	}
	/**
	 * Constructs an Oscil UGen given a Frequency and amplitude.
	 * This oscillator uses a sine wave.
	 * @param frequency
	 * @param amplitude
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
		this.amplitudeModulation = new UGenInput(InputType.CONTROL);
		this.frequency = new UGenInput(InputType.CONTROL);
		this.frequencyModulation = new UGenInput(InputType.CONTROL);
		this.phase = new UGenInput(InputType.CONTROL);
		this.wave = wave;
		baseFreq = freq;
		this.freq = baseFreq;
		this.amp = amp;
		step = 0f;
		fPhase = 0f;
	}	
	/**
	 * This routine needs to be called anytime the sampleRate is changed.
	 */
	public void sampleRateChanged()
	{
		stepSizeChanged();
		setSampleRate(sampleRate);
	}
	private void stepSizeChanged()
	{
		stepSize = freq.asHz()/sampleRate;
	}
	
	/**
	 * Sets the frequency to a frequency in Hz after construction.
	 * @param hz
	 */
	public void setFrequency( float hz )
	{
		baseFreq = Frequency.ofHertz( hz );
		freq = baseFreq;
		stepSizeChanged();
	}
	
	/**
	 * Sets the frequency to a Frequency after construction.
	 * @param freq
	 */
	public void setFrequency( Frequency freq )
	{
		baseFreq = freq;
		this.freq = baseFreq;
		stepSizeChanged();
	}
	
	public void setPhase( float newPhase )
	{
		fPhase = newPhase;
	}
	
	public void resetPhase()
	{
		step = 0;
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{		
		// figure out our sample value
		float outAmp = amp;
		// if something is plugged into amplitude
		if ( amplitude.isPatched() )
		{
			outAmp = amplitude.getLastValues()[0];
		}
		
		// if something has been plugged into amplitudeModulation
		if ( amplitudeModulation.isPatched() )
		{
			outAmp += amplitudeModulation.getLastValues()[0];
		}
		// calculate the sample values
		float sample = outAmp * wave.value(step);
		
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = sample;
		}
		
		// if something is plugged into frequency
		if ( frequency.isPatched() )
		{
			baseFreq = Frequency.ofHertz(frequency.getLastValues()[0]);
			stepSizeChanged();
		}
		// if something is plugged into frequencyModulation
		if ( frequencyModulation.isPatched() )
		{
			freq = Frequency.ofHertz(baseFreq.asHz() + frequencyModulation.getLastValues()[0]);
			stepSizeChanged();
		} 
		else
		{
			freq = baseFreq;
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
			step *= -1f;
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
