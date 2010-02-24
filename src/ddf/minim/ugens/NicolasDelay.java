package ddf.minim.ugens;



public class NicolasDelay extends UGen
{
	/**
	 * Delay unit 
	 * You can specify a delay time in milliseconds, a number of echoes 
	 * and the way the amplitude varies between the echoes 
	 * The initial delay and number of echoes that you specify in the constructor 
	 * will determine the length of the buffer so you can't have a longer delay 
	 * when playing with the delay UGenInput.
	 * 
	 * @author nb
	 * 
	 */
	//audio input
	public UGenInput audio;
	//initial delay in ms
	private float delayMs;
	//initial delay in samples
	private int currentDelay;
	//UGenIput to modify the delay
	public UGenInput delay;
	//array of amps for the echoes
	private float[] amps;
	//general amplitude of the set of echoes
	private float amp;
	//Input for the general amplitude
	public UGenInput amplitude;
	
	//the buffer used to store samples
	float[] buffer;
	//size of the complete buffer
	int limit;
	

	
	
	public static final int ONES = 1;
	public static final int EXP = 2;
	public static final int LIN = 3;

	int j=0;
	
	public NicolasDelay(float delayInMs, int numberOfEchoes , int type, float ampli)
	{
		this(delayInMs,ampli);
		amps = new float[numberOfEchoes];
		
		
		if(type == 3)
		{
			for(int i=0;i<numberOfEchoes;i++)
			{
				amps[i]=(1 - (float)i/numberOfEchoes);
			}
		}
		else if(type == 2)
		{
			for(int i=0;i<numberOfEchoes;i++)
			{
				amps[i]=(float)Math.exp(-i);//TODO : better exponential
			}
		}
		else
		{
			for(int i=0;i<numberOfEchoes;i++)
			{
				amps[i]=1;
			}
		}
	}
	
	public NicolasDelay(float delayInMs, float [] amplitudes, float ampli)
	{
		this(delayInMs,ampli);
		amps = amplitudes;
	}
	
	
	
	public NicolasDelay(float delayInMs,float ampli)
	{
		super();
		audio = new UGenInput(InputType.AUDIO);
		delay = new UGenInput(InputType.CONTROL);
		amplitude = new UGenInput(InputType.CONTROL);
		delayMs = delayInMs;
		amp = ampli;
	}
	public void sampleRateChanged()
	{
		currentDelay = (int)Math.floor(delayMs*sampleRate/1000);
		limit = amps.length*currentDelay+1;
		
		buffer=new float[limit];
		setSampleRate(sampleRate);
		j=limit-1;
		
	}
	
	public void calcDelays()
	{
		currentDelay = (int)Math.floor(delayMs*sampleRate/1000);
	}
	
	
	/*
	 * Thoughts : every UGen should know the size of channels
	 * Here for the delay, or for the filter, it would be useful to know how
	 * many buffers we need to create.
	 * 
	 * For now it's mono
	 * 
	 * NB
	 * 
	 * 
	 * 
	 * 
	 * 2)
	 * The way noteoff is defined in the current test instrument makes the delay useless if placed before
	 * the gain (noteoff is basically saying gain=0 at the end of the note, which cancels any fadeout)
	 * */
	
	
	protected void uGenerate(float[] channels) 
	{
	
		
		
		if ((amplitude != null) && (amplitude.isPatched()))
		{
			
			amp = amplitude.getLastValues()[0];

		}
		if ((delay != null) && (delay.isPatched()))
		{
			delayMs= (int)delay.getLastValues()[0];
			calcDelays();
		}
		
		float tmp= audio.getLastValues()[0];
		buffer[j]=tmp;
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = tmp;
			
			for(int k =0; k< amps.length ; k++)
			{
				channels[i] += amp*amps[k]*buffer[(j+(k+1)*currentDelay)%limit];
			}
		}
		
		j--;
		if(j<0) j = limit-1;


		
	}
}
