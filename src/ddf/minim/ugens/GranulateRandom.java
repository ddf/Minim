package ddf.minim.ugens;

import ddf.minim.Minim;

public class GranulateRandom extends UGen
{    
	public UGenInput audio;
	//public UGenInput amplitude;
	private Waveform sine = Waves.Sine;
	private boolean insideGrain;
	private float timeSinceGrainStart;
	private float timeSinceGrainStop;
	private float timeStep;
	
	private float fadeLength = 0.0025f;
	private float grainLength = 0.010f;
	private float spaceLength = 0.020f;
	private float fadeLengthMin = 0.0025f;
	private float grainLengthMin = 0.010f;
	private float spaceLengthMin = 0.020f;
	private float fadeLengthMax = 0.0025f;
	private float grainLengthMax = 0.010f;
	private float spaceLengthMax = 0.020f;
	
	//public GranulateRandom()
	//{
	//	this( 0.01f, 0.02f, 0.0025f );
	//}
	
	public GranulateRandom(float grainLengthMin, float spaceLengthMin, float fadeLengthMin,
			float grainLengthMax, float spaceLengthMax, float fadeLengthMax)
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		audio = new UGenInput(InputType.AUDIO);
		this.grainLengthMin = grainLengthMin;
		this.spaceLengthMin = spaceLengthMin;
		this.fadeLengthMin = fadeLengthMin;
		this.grainLengthMax = grainLengthMax;
		this.spaceLengthMax = spaceLengthMax;
		this.fadeLengthMax = fadeLengthMax;
		insideGrain = false;
		timeSinceGrainStart = 0.0f;
		timeSinceGrainStop = 0.0f;
		timeStep = 0.0f;
	}

	public void sampleRateChanged()
	{
		timeStep = 1.0f/sampleRate;
	}
	
	private void checkFadeLength()
	{
		fadeLength = Math.min( fadeLength, grainLength/2.0f );
	}
	
	private float randomBetween( float min, float max )
	{
		return (max - min)*(float)Math.random()	+ min;
	}
	
	@Override
	protected void uGenerate( float[] channels ) 
	{
	
		if ( insideGrain )
		{	
			float amp = 1.0f;
			
			// TODO protection for overlapping in and out fades
			if ( timeSinceGrainStart < fadeLength )
			{
				amp = timeSinceGrainStart/fadeLength;
				//amp = sine.value( timeSinceGrainStart/( 4.0f*fadeLength ) );
			}
			else if ( timeSinceGrainStart > ( grainLength - fadeLength ) )
			{
				amp = ( grainLength - timeSinceGrainStart )/fadeLength;
				//amp = sine.value( ( grainLength - timeSinceGrainStart )/( 4.0f*fadeLength ) );
			}
			
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = amp*audio.getLastValues()[i];
			}
			timeSinceGrainStart += timeStep;
			if (timeSinceGrainStart > grainLength) 
			{
				timeSinceGrainStop = 0.0f;
				insideGrain = false;
				spaceLength = randomBetween( spaceLengthMin, spaceLengthMax );
			}
		}
		else
		{
			for(int i = 0; i < channels.length; i++)
			{
				channels[i] = 0.0f;
			}
			timeSinceGrainStop += timeStep;
			if (timeSinceGrainStop > spaceLength)
			{
				timeSinceGrainStart = 0.0f;
				insideGrain = true;
				grainLength = randomBetween( grainLengthMin, grainLengthMax );
				fadeLength = randomBetween( fadeLengthMin, fadeLengthMax );
				checkFadeLength();
			}
		}
	} 
}