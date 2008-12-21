package ddf.minim.effects;

import processing.core.PApplet;
import ddf.minim.AudioEffect;

public class ADSR implements AudioEffect
{
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
		process(signal, null);
	}

	public void process(float[] sigLeft, float[] sigRight)
	{
    for(int i = 0; i < sigLeft.length; i++)
    {
      float amp = 0;
      // ATTACK
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
      sigLeft[i] *= amp;
      if ( sigRight != null )
      {
        sigRight[i] *= amp;
      }
      samplesProcessed++;
    }
	}

}
