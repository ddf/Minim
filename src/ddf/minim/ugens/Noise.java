package ddf.minim.ugens;
/**
 * Provides a UGen which generates noise.
 * @author Anderson Mills, Damien Di Fede
 *
 */
public class Noise extends UGen 
{
	/**
	 * enumeration used to specify the tint of the noise
	 */
	public enum Tint { WHITE, PINK, RED, BROWN };
	
	// the type of noise
	private Tint tint;
	// the amplitude at which  we will generate noise
	private float	amp;
	// the last value
	private float	lastN;

	/**
	 * Constructor for white noise with an amplitude of 1.0.
	 */
	public Noise()
	{
		this( 1.0f, Tint.WHITE );
	}
	/**
	 * Constructor for white noise of the specified amplitude.
	 * @param amplitude
	 */
	public Noise(float amplitude)
	{
		this( amplitude, Tint.WHITE ) ;
	}
	/**
	 * Constructor for noise of the specified tint with an amplitude of 1.0.
	 * @param noiseType
	 */
	public Noise( Tint noiseType )
	{
		this( 1.0f, noiseType ) ;
	}
	/**
	 * Costructor for noise of a specific tint with a specified amplitude.
	 * @param amplitude
	 * @param noiseType
	 */
	public Noise(float amplitude, Tint noiseType)
	{
		amp = amplitude;
		lastN = 0f;
		tint = noiseType;
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		float n = amp*(2.0f*(float)Math.random() - 1.0f);
		switch (tint) 
		{
		// BROWN and RED noise are the same and are made by 
		// integrating over white noise
		// TODO thoroughly check this
		case BROWN :
		case RED :
			float outputN = ( n + lastN )/2.0f;
			lastN = n;
			n = outputN;
			//n = 0.5f;
			break;
		// PINK noise has a 10db/decade (3db/octave) slope
		// TODO implement PINK noise
		case PINK :
		// WHITE noise is the default and is made by
		// generating uniform random numbers
		case WHITE :
		default :
			break;
		}
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = n;
		}
	}

}