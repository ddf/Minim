package ddf.minim.signals;

import ddf.minim.AudioOutput;
import ddf.minim.AudioSignal;
import ddf.minim.effects.ADSR;

public class Note implements AudioSignal
{
	
	public static void trigger(AudioSignal wave, ADSR envelope, AudioOutput output)
	{
		new Note(wave, envelope, output);
	}
	
	private AudioOutput out;
	private AudioSignal sig;
	private ADSR env;
	
	private Note(AudioSignal wave, ADSR envelope, AudioOutput output)
	{
		out = output;
		sig = wave;
		env = envelope;
		output.addSignal(this);
		env.trigger();
	}
	
	public void generate(float[] signal)
	{
    if ( !env.done() )
    {
      sig.generate(signal);
      env.process(signal);
    }
    else
		{
			out.removeSignal(this);
		}
	}

	public void generate(float[] left, float[] right)
	{
		sig.generate(left, right);
		env.process(left, right);
		if ( env.done() )
		{
			out.removeSignal(this);
		}
	}

}
