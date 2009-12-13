package ddf.minim.ugens;

import ddf.minim.ugens.UGen.InputType;
import ddf.minim.ugens.UGen.UGenInput;

public class Disto extends UGen 
{
	/**
	 * A distortion UGen.
	 * 
	 * A library of shapes is defined, that the user can call.
	 * The shapes are Wavetables, which can be used in a creative way 
	 * (using waveforms from the Waves library for example)
	 * 
	 * @author nb
	 */
	
	//audio input
	public UGenInput audio;
	
	
	float amount2;
	public UGenInput amount;
	Wavetable shape;
	
	public Disto(Wavetable sh, float am)
	{
		super();
		shape=sh;
		amount2=am;
		audio = new UGenInput(InputType.AUDIO);
		amount = new UGenInput(InputType.CONTROL);
	}
	
	

	
	
	
	
	//the input signal is supposed to be less than 1 in amplitude 
	//as Wavetable is basically an array of floats accessed via a 0 to 1.0 index, 
	//some shifting+scaling has to be done
	
	//the shape is supposed to be -1 at [0] and +1 at [length].
	
	protected void uGenerate(float[] channels)
	{
		if ((amount != null) && (amount.isPatched()))
		{
			amount2 = amount.getLastValues()[0];

		}
		
		
		for(int i = 0; i < channels.length; i++)
		{
			
			float a= amount2*audio.getLastValues()[i];
			
			channels[i]= (Math.abs(a)> 1f)? Math.signum(a) : shape.value(a/2+0.5f);
			channels[i]/=amount2;
		}
	}

	//public final static Wavetable Diode = new Wavetable(new float[] {-1,-1,1,1});
	public final static Wavetable Diode = WavetableGenerator.gen7(512, new float[] {-1,-1,1,1}, new int[]{128,256,128});
	
	//this one is a 
	public final static Wavetable Square = WavetableGenerator.gen7(512, new float[] {-1,-1,1,1}, new int[]{256,0,256});
	//public final static Wavetable Saw = new Wavetable(new float[] {-1,1});
	
	public static final Wavetable TruncSine()
	{
		Wavetable sine = WavetableGenerator.gen10(512, new float [] {1});
		Wavetable truncsine = new Wavetable(new float[256]);
		System.arraycopy(sine.getWaveform(), 128,truncsine.getWaveform(), 0, 256);
		truncsine.invert();
		return truncsine;
	}
}
