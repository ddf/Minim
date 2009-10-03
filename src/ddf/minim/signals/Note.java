package ddf.minim.signals;

import ddf.minim.AudioOutput;
import ddf.minim.AudioSignal;

// For 2.0.2 I don't want this class to be visible. We will probably pull it out 
// entirely as part of the project with Numediart.
class Note implements AudioSignal
{
	private AudioOutput out;
	private AudioSignal sig;
	private ADSR env;
	private float[] amps;
	
	public Note(AudioSignal wave, ADSR envelope, AudioOutput output)
	{
		out = output;
		sig = wave;
		env = envelope;
		output.addSignal(this);
		env.trigger();
		amps = new float[output.bufferSize()];
	}
	
	public void generate(float[] signal)
	{
		sig.generate(signal);
		env.generate(amps);
		for(int i = 0; i < amps.length; i++)
		{
			signal[i] *= amps[i];
		}
		
		if ( env.done() )
		{
			out.removeSignal(this);
		}
	}

	public void generate(float[] left, float[] right)
	{
		sig.generate(left, right);
		env.generate(amps);
		for(int i = 0; i < amps.length; i++)
		{
			left[i] *= amps[i];
			right[i] *= amps[i];
		}
		
		if ( env.done() )
		{
			out.removeSignal(this);
		}
	}

}
