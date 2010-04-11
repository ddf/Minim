package ddf.minim.ugens;


/**
 * A UGen which chops the incoming audio into steady grains
 * of sound.  The envelope of these sounds has a linear fade
 * in and fade out.
 * @author Anderson Mills
 *
 */
public class GranulateSteady extends UGen
{
	/**
	 * The default input is "audio."
	 */
	public UGenInput audio;
	/**
	 * Controls the length of each grain.
	 */
	public UGenInput grainLen;
	/**
	 * Controls the space between each grain.
	 */
	public UGenInput spaceLen;
	/**
	 * Controls the length of the fade in and fade out.
	 */
	public UGenInput fadeLen;

	// variables to determine the current placement WRT a grain
	private boolean insideGrain;
	private float timeSinceGrainStart;
	private float timeSinceGrainStop;
	private float timeStep;
	
	// variables to keep track of the grain values
	private float grainLength = 0.010f;
	private float spaceLength = 0.020f;
	private float fadeLength = 0.0025f;
	private float minAmp = 0.0f;
	private float maxAmp = 1.0f;	
	
	/**
	 * Constructor for GranulateSteady.
	 * grainLength, length of each grain, defaults to 10 msec.
	 * spaceLength, space between each grain, defaults to 20 msec.
	 * fadeLength, length of the linear fade in and fade out of the grain envelope, defaults to 2.5 msec.
	 * minAmp, minimum amplitude of the envelope, defaults to 0.
	 * maxAmp, maximum amplitude of the envelope, defaults to 1.
	 */
	public GranulateSteady()
	{
		this( 0.01f, 0.02f, 0.0025f, 0.0f, 1.0f );
	}
	/**
	 * Constructor for GranulateSteady.
	 * minAmp, minimum amplitude of the envelope, defaults to 0.
	 * maxAmp, maximum amplitude of the envelope, defaults to 1.
	 * @param grainLength
	 * 			length of each grain
	 * @param spaceLength
	 * 			space between each grain
	 * @param fadeLength
	 * 			length of the linear fade in and fade out of the grain envelope
	 */
	public GranulateSteady( float grainLength, float spaceLength, float fadeLength )
	{
		this( grainLength, spaceLength, fadeLength, 0.0f, 1.0f );
	}
	/**
	 * Constructor for GranulateSteady.
	 * @param grainLength
	 * 			length of each grain
	 * @param spaceLength
	 * 			space between each grain
	 * @param fadeLength
	 * 			length of the linear fade in and fade out of the grain envelope
	 * @param minAmp
	 * 			minimum amplitude of the envelope
	 * @param maxAmp
	 * 			maximum amplitude of the envelope
	 */
	public GranulateSteady( float grainLength, float spaceLength, float fadeLength, float minAmp, float maxAmp )
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		audio = new UGenInput(InputType.AUDIO);
		grainLen = new UGenInput( InputType.CONTROL );
		spaceLen = new UGenInput( InputType.CONTROL );
		fadeLen = new UGenInput( InputType.CONTROL );
		//amplitude = new UGenInput(InputType.CONTROL);
		this.grainLength = grainLength;
		this.spaceLength = spaceLength;
		this.fadeLength = fadeLength;
		this.minAmp = minAmp;
		this.maxAmp = maxAmp;
		insideGrain = true;
		timeSinceGrainStart = 0.0f;
		timeSinceGrainStop = 0.0f;
		timeStep = 0.0f;
	}
	
	/**
	 * Use this method to notify GranulateSteady that the sample rate has changed.
	 */
	protected void sampleRateChanged()
	{
		timeStep = 1.0f/sampleRate();
	}
	
	// This makes sure that fadeLength isn't more than half the grainLength
	private void checkFadeLength()
	{
		fadeLength = Math.min( fadeLength, grainLength/2.0f );
	}
	// Make those samples!
	@Override
	protected void uGenerate( float[] channels ) 
	{
		if ( insideGrain )  // inside a grain
		{	
			// start with an amplitude at maxAmp
			float amp = maxAmp;
			if ( timeSinceGrainStart < fadeLength )  // inside the rise of the envelope
			{
				// linear fade in
				amp *= timeSinceGrainStart/fadeLength;  
			}
			else if ( timeSinceGrainStart > ( grainLength - fadeLength ) )  // inside the decay of the envelope
			{
				// linear fade out
				amp *= ( grainLength - timeSinceGrainStart )/fadeLength;
			}
			
			// generate the sample
			for( int i = 0; i < channels.length; i++ )
			{
				channels[i] = amp*audio.getLastValues()[i];
			}
			
			// increment time
			timeSinceGrainStart += timeStep;
			
			if ( timeSinceGrainStart > grainLength )  // just after the grain 
			{
				// stop the grain
				timeSinceGrainStop = 0.0f;
				insideGrain = false;
				// only set space volues at the beginning of a space
				if ( spaceLen.isPatched() ) 
				{
					spaceLength = spaceLen.getLastValues()[0];
				}
			}
		}
		else  // outside of a grain
		{
			// generate the samples
			for( int i = 0; i < channels.length; i++ )
			{
				channels[i] = minAmp;
			}
			
			// increment time
			timeSinceGrainStop += timeStep;

			if ( timeSinceGrainStop > spaceLength )  // just inside a grain again
			{
				// start the grain
				timeSinceGrainStart = 0.0f;
				insideGrain = true;
				// only set the grain values at the beginning of a grain
				if ( grainLen.isPatched() ) 
				{
					grainLength = grainLen.getLastValues()[0];
					checkFadeLength();
				}
				if ( fadeLen.isPatched() )
				{
					fadeLength = fadeLen.getLastValues()[0];
					checkFadeLength();
				}
			}
		}
	} 
}