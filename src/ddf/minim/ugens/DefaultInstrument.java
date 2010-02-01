package ddf.minim.ugens;
import ddf.minim.AudioOutput;
//import ddf.minim.effects.IIRFilter;
import ddf.minim.effects.LowPassSP;

public class DefaultInstrument implements Instrument
{
	Oscil toneOsc;
	Noise noiseGen;
	Damp noiseEnv, toneEnv;
	//Gain toneEnv;
	//Damp toneEnv;
	AudioOutput output;
	Summer summer;
	LowPassSP lpFilter;
		  
	public DefaultInstrument( float frequency, AudioOutput output )
	{
		this.output = output;
		    
		float amplitude = 0.3f; 
		noiseGen = new Noise( 0.4f*amplitude, Noise.Tint.WHITE );
		noiseEnv = new Damp( 0.05f );
		lpFilter = new LowPassSP( 2.0f*frequency, output.sampleRate() );
		toneOsc = new Oscil( frequency, 0.9f*amplitude, Waves.Triangle );
		//toneEnv = new Damp( 1.0f );
		toneEnv = new Damp( 2.0f/frequency, 1.0f );
		//toneEnv = new Gain( 0f );
		summer = new Summer();
		
		toneOsc.patch( toneEnv ).patch( summer );
		noiseGen.patch( noiseEnv ).patch( lpFilter).patch( summer );
		//.patch( output );
	}
		  
	public void noteOn( float dur )
	{
		summer.patch( output );
		toneEnv.setDampTimeFromDuration( dur );
		toneEnv.noteOn();
		noiseEnv.noteOn();
		//toneEnv.setValue( 1.0f );
		//summer.patch( output );
	}
		  
	public void noteOff()
	{
		//toneEnv.setValue( 0.0f );
		summer.unpatch( output );
	}
}
