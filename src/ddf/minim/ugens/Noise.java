package ddf.minim.ugens;

public class Noise extends UGen 
{

	// Must define the noise types
	public enum NoiseType { WHITE, PINK, RED };
	
	// the type of noise
	private NoiseType noisTyp;
	// the amplitude at which  we will generate noise
	private float	amp;
	// the last value
	private float	last;

	public Noise()
	{
		// TODO remove this magic constant
		this(1.0f);
	}
	
	public Noise(float amplitude)
	{
		this( amplitude, NoiseType.WHITE ) ;
	}

	public Noise(float amplitude, NoiseType noiseType)
	{
		amp = amplitude;
		last = 0f;
		noisTyp = noiseType;
		
	}
	
	@Override
	protected void ugentick(float[] channels) 
	{
		float n = 0f;
		switch (noisTyp) 
		{
		case WHITE :
			n = amp*(float)Math.random()*2 - 1;
			break;
		case RED :
			float offset = (float)Math.random()*2;
			n = amp*( ( last/amp + offset ) % 2f - 1 );
			last = n;
			break;
		}
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = n;
		}
	}

}
