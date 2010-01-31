package ddf.minim.ugens;
import ddf.minim.AudioOutput;

public class DefaultInstrument implements Instrument
{
	Oscil sineOsc, lFOOsc;
	Gain  gainGate;
	AudioOutput output;
		  
	public DefaultInstrument( float frequency, AudioOutput output )
	{
		this.output = output;
		    
		float amplitude = 0.3f; 
		sineOsc = new Oscil( frequency, amplitude, Waves.Triangle );
		gainGate = new Gain( 0.0f );
		sineOsc.patch( gainGate );
	}
		  
	public void noteOn( float dur )
	{
		gainGate.setValue( 1.0f );
		gainGate.patch( output );
	}
		  
	public void noteOff()
	{
		gainGate.setValue( 0.0f );
	}
}
