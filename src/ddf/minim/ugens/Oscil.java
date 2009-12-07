package ddf.minim.ugens;

import ddf.minim.Minim;
import ddf.minim.ugens.UGen.InputType;
import ddf.minim.ugens.UGen.UGenInput;

public class Oscil extends UGen 
{

	// jam3: define the inputs to Oscil
	//public UGenInput audio;
	public UGenInput amplitude;
	public UGenInput amplitudeModulation;
	public UGenInput frequency;
	public UGenInput frequencyModulation;
	
	// the waveform we will oscillate over
	private Waveform  wave;
	// the base frequency at which we will oscillate
	private Frequency baseFreq;
    // the current frequency at which we oscillate (includes modulation)
	private Frequency freq;
	// the amplitude at which we will oscillate
	private float 	  amp;
	// where we will sample our waveform, moves between [0,1]
	private float step;
	// the step size we will use to advance our step
	private float stepSize;
	
	//constructors
	public Oscil(float frequencyInHertz, float amplitude, Waveform waveform)
	{
		this(Frequency.ofHertz(frequencyInHertz), amplitude, waveform);
	}
	
	public Oscil(float frequencyInHertz, float amplitude)
	{
		this(Frequency.ofHertz(frequencyInHertz), amplitude);
	}

	//shortcut for building a sine wave
	public Oscil(Frequency frequency, float amplitude)
	{
		this(frequency, amplitude, Waves.Sine);
	}

	//standard constructor
	public Oscil(Frequency frequency, float amplitude, Waveform waveform)
	{
		super();
		this.amplitude = new UGenInput(InputType.CONTROL);
		this.amplitudeModulation = new UGenInput(InputType.CONTROL);
		this.frequency = new UGenInput(InputType.CONTROL);
		this.frequencyModulation = new UGenInput(InputType.CONTROL);
		wave = waveform;
		baseFreq = frequency;
		freq = baseFreq;
		amp = amplitude;
		step = 0f;
	}
		
	public void sampleRateChanged()
	{
		stepSizeChanged();
		setSampleRate(sampleRate);
	}
	
	public void stepSizeChanged()
	{
		stepSize = freq.asHz() / sampleRate;
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{		
		// figure out our sample value
		float tmpAmp;
		if ((amplitude != null) && (amplitude.isPatched()))
		{
			tmpAmp = amplitude.getLastValues()[0];
		} else 
		{
			tmpAmp = amp;
		}
		
		if ((amplitudeModulation != null) && (amplitudeModulation.isPatched()))
		{
			tmpAmp += amplitudeModulation.getLastValues()[0];
		}
		
		float sample = tmpAmp * wave.value(step);
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = sample;
		}
		
		if ((frequency !=null) && (frequency.isPatched()))
		{
			baseFreq = Frequency.ofHertz(frequency.getLastValues()[0]);
			stepSizeChanged();
		}
		
		if ((frequencyModulation !=null) && (frequencyModulation.isPatched()))
		{
			freq = Frequency.ofHertz(baseFreq.asHz() + frequencyModulation.getLastValues()[0]);
			stepSizeChanged();
		}
		
		step += stepSize;
		// make sure we don't exceed 1.0.
		// floor is less expensive than %?
		step -= (float)Math.floor(step);
		//Minim.debug("Oscil::uGenerate freq = " + freq.asHz()
		//		+ "  step = " + step + "  stepSize = " + stepSize
		//		+ "  sample = " + sample);
	}
}
