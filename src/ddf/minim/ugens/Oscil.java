package ddf.minim.ugens;

public class Oscil extends UGen 
{
	public final static Wavetable Sine = WavetableGenerator.gen10(8192, new float[] { 1 });
	public final static Wavetable Saw = WavetableGenerator.gen7(8192, new float[] {-1,1}, new int[] {8192});
	public final static Wavetable Square = WavetableGenerator.gen7(8192, new float[] {-1,-1,1,1}, new int[] {4096,0,4096});
	public final static Wavetable Triangle = WavetableGenerator.gen7(8192, new float[] {-1,1,-1}, new int[] {4096,4096});
	//public Wavetable AddSines;//covers all harmonically built waveforms
	//no static because needs harmonic.content to be built
	//no final, otherwise one of the constructors complains about no initialization of Add
	//breaking news : AddSines is not even used, as I found a rather clean way to call the 'standard' 
	//constructor from the "harmonic" constructor
	
	
	// the waveform we will oscillate over
	private Waveform  wave;
	// the frequency at which we will oscillate
	private Frequency freq;
	// the amplitude at which we will oscillate
	private float 	  amp;
	// where we will sample our waveform, moves between [0,1]
	private float step;
	// the step size we will use to advance our step
	private float stepSize;
	
	
	//constructors
	
	//the three first constructors only transform freqs in Hz into Frequency objects
	public Oscil(float frequencyInHertz, float amplitude, String wavetype, int numberOfHarms)
	{
		this(Frequency.ofHertz(frequencyInHertz), amplitude, wavetype, numberOfHarms);
	}
	
	public Oscil(float frequencyInHertz, float amplitude, Waveform waveform)
	{
		this(Frequency.ofHertz(frequencyInHertz), amplitude, waveform);
	}
	
	public Oscil(float frequencyInHertz, float amplitude, String wavetype, float dutyCycle )
	{
		this(Frequency.ofHertz(frequencyInHertz), amplitude, wavetype, dutyCycle);
	}
	
	//this constructor deals with harmonically built waveforms, using the Harmonics class.
	public Oscil(Frequency frequency, float amplitude, String wavetype, int numberOfHarms)
	{
		this(frequency, amplitude, WavetableGenerator.gen10(8192, new Harmonics(wavetype,numberOfHarms).content));
	}
	
	public Oscil(Frequency frequency, float amplitude, String wavetype, float dutyCycle )
	{
		this(frequency, amplitude, WavetableGenerator.gen7(8192, new float[] {1,1,-1,-1}, new int[] {(int)(dutyCycle*8192),0,8192-(int)(dutyCycle*8192)}));
		//note : wavetype is not even used...
	}
	

	//standard constructor
	public Oscil(Frequency frequency, float amplitude, Waveform waveform)
	{
		//waveform=WavetableGenerator.gen10(8192, new float[] {1});
		//might be useful at some point, for a unified way of calling the different wavetables
		wave = waveform;
		freq = frequency;
		amp = amplitude;
		step = 0f;
	}
	
	
	
	
	public void sampleRateChanged()
	{
		stepSize = freq.asHz() / sampleRate;
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		// figure out our sample value
		float sample = amp * wave.value(step);
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = sample;
		}
		step += stepSize;
		// make sure we don't exceed 1.0.
		// floor is less expensive than %?
		step -= (float)Math.floor(step);
	}
}
