package ddf.minim.ugens;

import java.util.Arrays;

import ddf.minim.UGen;

//Moog 24 dB/oct resonant lowpass VCF
//References: CSound source code, Stilson/Smith CCRMA paper.
//Modified by paul.kellett@maxim.abel.co.uk July 2000
//Java implementation by Damien Di Fede September 2010

public class MoogFilter extends UGen
{
	public enum Type
	{
		HP,
		LP,
		BP
	}
	
	public UGenInput	audio;
	public UGenInput	frequency;
	public UGenInput	resonance;
	public Type 		type;

	private float		coeff[][];	// filter buffers (beware denormals!)
	
	/**
	 * Creates a low pass filter.
	 * 
	 * @param frequencyInHz
	 * @param normalizedResonance
	 */
	public MoogFilter( float frequencyInHz, float normalizedResonance )
	{
		this( frequencyInHz, normalizedResonance, Type.LP );
	}

	/**
	 * Creates a filter of the type specified.
	 * 
	 * @param frequencyInHz 
	 * 		the cutoff frequency for the filter
	 * @param normalizedResonance 
	 * 		the resonance of the filter [0,1]
	 * @param filterType
	 * 		the type of the filter: HP (high pass), LP (low pass), or BP (band pass)
	 */
	public MoogFilter(float frequencyInHz, float normalizedResonance, Type filterType )
	{
		super();
		
		audio = new UGenInput( InputType.AUDIO );
		frequency = new UGenInput( InputType.CONTROL );
		resonance = new UGenInput( InputType.CONTROL );
		type      = filterType;

		frequency.setLastValue( frequencyInHz );
		resonance.setLastValue( constrain( normalizedResonance, 0.f, 1.f ) );
		
		coeff = new float[getAudioChannelCount()][5];
	}

	public void channelCountChanged()
	{
		if ( coeff == null || coeff.length != getAudioChannelCount() )
		{
			coeff = new float[getAudioChannelCount()][5];
		}
	}

	protected void uGenerate(float[] out)
	{
		// Set coefficients given frequency & resonance [0.0...1.0]
		float t1, t2; // temporary buffers
		float normFreq = frequency.getLastValue() / ( sampleRate() * 0.5f );
		float rez = constrain( resonance.getLastValue(), 0.f, 1.f );

		float q = 1.0f - normFreq;
		float p = normFreq + 0.8f * normFreq * q;
		float f = p + p - 1.0f;
		q = rez * ( 1.0f + 0.5f * q * ( 1.0f - q + 5.6f * q * q ) );

		float[] input = audio.getLastValues();

		for ( int i = 0; i < getAudioChannelCount(); ++i )
		{
			// Filter (in [-1.0...+1.0])
			float[] b = coeff[i];
			float in = constrain( input[i], -1, 1 ); // hard clip

			in -= q * b[4]; // feedback

			t1 = b[1];
			b[1] = ( in + b[0] ) * p - b[1] * f;

			t2 = b[2];
			b[2] = ( b[1] + t1 ) * p - b[2] * f;

			t1 = b[3];
			b[3] = ( b[2] + t2 ) * p - b[3] * f;

			b[4] = ( b[3] + t1 ) * p - b[4] * f;
			b[4] = b[4] - b[4] * b[4] * b[4] * 0.166667f; // clipping
			
			// inelegantly squash denormals
	        if ( Float.isNaN( b[4] ) )
	        {
	        	Arrays.fill( b, 0 );
	        }

			b[0] = in;

			switch( type )
			{
			case HP:
				out[i] = in - b[4];
				break;
				
			case LP:
				out[i] = b[4];
				break;
				
			case BP:
				out[i] = 3.0f * (b[3] - b[4]);
			}
		}
	}
	
	private float constrain( float value, float min, float max )
	{
		if ( value < min ) return min;
		if ( value > max ) return max;
		return value;
	}
}
