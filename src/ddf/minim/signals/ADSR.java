package ddf.minim.signals;

import processing.core.PApplet;
import ddf.minim.AudioSignal;
import ddf.minim.Minim;

// For 2.0.2 I don't want this class to be visible. We will likely pull it out entirely
// as part of the project with Numediart.
class ADSR implements AudioSignal
{
	private int a, d, s, r;
	private float susAmp;
	private int samplesProcessed;
	float[] allAmps;
	
	public ADSR(float attackLength, 
				float decayLength, 
				float sustainLength, 
				float sustainLevel, 
				float releaseLength,
				float sampleRate)
	{
		// convert second lengths to sample counts
		int asamp = (int)(attackLength * sampleRate);
		int dsamp = (int)(decayLength * sampleRate);
		int ssamp = (int)(sustainLength * sampleRate);
		int rsamp = (int)(releaseLength * sampleRate);
		Minim.debug("Attack: " + asamp + ", Decay: " + dsamp + ", Sustain: " + ssamp + ", Release: " + rsamp);
		a = asamp;
		d = a + dsamp;
		s = d + ssamp; 
		r = s + rsamp;
		susAmp = sustainLevel;
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
	
	public void generate(float[] signal) 
	{
		generate(signal, null);
	}

	public void generate(float[] left, float[] right) 
	{
	    for(int i = 0; i < left.length; i++)
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
	      left[i] = amp;
	      if ( right != null )
	      {
	        right[i] = amp;
	      }
	      samplesProcessed++;
	    }
	}

}
