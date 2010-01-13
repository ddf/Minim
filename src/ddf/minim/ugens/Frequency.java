package ddf.minim.ugens;

public class Frequency
{
	static float HZA4=440.0f;
	static float MIDIA4=69.0f;
	static float MIDIOCTAVE=12.0f;
	float freq;
	
	Frequency(float hz)
	{
		freq = hz;
	}
	
	public float asHz()
	{
		return freq;
	}
	
	public float asMidiNote()
	{
		float midiNote = MIDIA4 + MIDIOCTAVE*(float)Math.log( freq/HZA4 )/(float)Math.log( 2.0 );
		return midiNote;
	}
	
	public String asPitch()
	{
		// TODO convert to something like "A4"
		return "";
	}
	
	void useFrequency(UGen freqGen)
	{
		
	}
	
	public static Frequency ofHertz(float hz)
	{
		return new Frequency(hz);
	}
	
	public static Frequency ofMidiNote(float midiNote)
	{
		float hz = HZA4*(float)Math.pow( 2.0, ( midiNote - MIDIA4 )/MIDIOCTAVE );
		return new Frequency(hz);
	}
	
	public static Frequency ofPitch(String pitchName)
	{
		// TODO return a frequency object that has the frequency of pitchName
		return null;
	}
}
