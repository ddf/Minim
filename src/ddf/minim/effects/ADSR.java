package ddf.minim.effects;

import processing.core.PApplet;
import ddf.minim.AudioEffect;

public class ADSR implements AudioEffect
{
//	private WavetableEnvelope env;
//	private int duration;
	
	int a, d, s, r;
	float susAmp;
	float sampleRate;
	int samplesProcessed;
	
	public ADSR(int attackLength, 
					int decayLength, 
					int sustainLength, 
					float sustainLevel, 
					int releaseLength,
					float sampleRate)
	{
		// convert millisecond lengths to sample counts
		a = (int)(attackLength / 1000.0 * sampleRate);
		d = a + (int)(decayLength / 1000.0 * sampleRate);
		s = d + (int)(sustainLength / 1000.0 * sampleRate);
		r = s + (int)(releaseLength / 1000.0 * sampleRate);
		susAmp = sustainLevel;
		this.sampleRate = sampleRate;
		samplesProcessed = 0;		
		
//		float[] val = { 0, 1, sustainLevel, sustainLevel, 0 };
//		//int[] dist = { attackLength, decayLength, sustainLength, releaseLength };
//		//int size = attackLength + decayLength + sustainLength + releaseLength;
//		// convert millisecond times to the approximate number of samples 
//		// they represent at the given sample rate.
//		int asamp = (int)(attackLength / 1000.0 * sampleRate);
//		int dsamp = (int)(decayLength / 1000.0 * sampleRate);
//		int ssamp = (int)(sustainLength / 1000.0 * sampleRate);
//		int rsamp = (int)(releaseLength / 1000.0 * sampleRate);
//		int[] dist = { asamp, dsamp, ssamp, rsamp };
//		int size = asamp + dsamp + ssamp + rsamp;
//		env = new WavetableEnvelope( WavetableGenerator.gen7(size, val, dist), (int)sampleRate );
//		duration = attackLength + decayLength + sustainLength + releaseLength;
	}
	
	public void trigger()
	{
		samplesProcessed = 0;
	}
	
	public boolean done()
	{
		return samplesProcessed > r;
	}

	public void process(float[] signal)
	{
		if ( samplesProcessed == 0 )
		{
			PApplet.println("ATTACK");
		}
		for(int i = 0; i < signal.length; i++)
		{
			// ATTACK
			float amp = 0;
			if ( samplesProcessed <= a )
			{
				amp = PApplet.map(samplesProcessed, 0, a, 0, 1);
			}
			// DECAY
			else if ( samplesProcessed > a && samplesProcessed <= d )
			{
				amp = PApplet.map(samplesProcessed, a, d, 1, susAmp);
			}
			// SUSTAIN
			else if ( samplesProcessed > d && samplesProcessed <= s )
			{
				amp = susAmp;
			}
			// RELEASE
			else if ( samplesProcessed > s && samplesProcessed <= r )
			{
				amp = PApplet.map(samplesProcessed, s, r, susAmp, 0);
			}
			signal[i] *= amp;
			samplesProcessed++;
			if ( samplesProcessed == a )
			{
				PApplet.println("DECAY");
			}
			else if ( samplesProcessed == d )
			{
				PApplet.println("SUSTAIN");
			}
			else if ( samplesProcessed == s )
			{
				PApplet.println("RELEASE");
			}
			else if ( samplesProcessed == r )
			{
				PApplet.println("END");
			}
		}
	}

	public void process(float[] sigLeft, float[] sigRight)
	{		
	}

}
