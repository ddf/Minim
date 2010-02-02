package ddf.minim.ugens;

public class Noise extends UGen 
{
	// Must define the noise types
	public enum Tint { WHITE, PINK, RED };
	
	// the type of noise
	private Tint tint;
	// the amplitude at which  we will generate noise
	private float	amp;
	// the last value
	private float	last;

	public Noise()
	{
		// TODO remove this magic constant
		this(1.0f, Tint.WHITE);
	}
	
	public Noise(float amplitude)
	{
		this(amplitude, Tint.WHITE) ;
	}

	public Noise(float amplitude, Tint noiseType)
	{
		amp = amplitude;
		last = 0f;
		tint = noiseType;
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		float n = 0f;
		switch (tint) 
		{
		case WHITE :
			n = amp*(2.0f*(float)Math.random() - 1.0f);
			break;
		case RED :
			float offset = 2.0f*(float)Math.random();
			n = amp*( ( ( last/amp + offset + 1f ) % 2f ) - 1f );
			last = n;
			break;
		}
		for(int i = 0; i < channels.length; i++)
		{
			channels[i] = n;
		}
	}

}