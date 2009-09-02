package ddf.minim.signals;

import ddf.minim.AudioOutput;
import ddf.minim.AudioSignal;
import ddf.minim.Minim;
import ddf.minim.signals.ADSR;

public class Note implements AudioSignal
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
