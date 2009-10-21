package ddf.minim.ugens;

public class Frequency
{
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
		// TODO convert properly
		return 0;
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
	
	public static Frequency ofPitch(String pitchName)
	{
		// TODO return a frequency object that has the frequency of pitchName
		return null;
	}
}
