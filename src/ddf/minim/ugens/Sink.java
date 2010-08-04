package ddf.minim.ugens;

import java.util.Vector;

/**
 * A Sink is similar to a Summer, but instead of summing all of the UGens patched to it,
 * it simply ticks them and only generates silence. This is useful if you have a UGen that 
 * needs to be ticked but that shouldn't be generating audio, such as an EnvelopeFollower.
 * 
 * @author Damien Di Fede
 *
 */
public class Sink extends UGen 
{
	private Vector<UGen> ugens;
	
	public Sink() 
	{
		ugens = new Vector<UGen>();
	}

	@Override
	protected void addInput( UGen input )
	{
		ugens.add(input);
	}
	
	@Override
	protected void removeInput( UGen input )
	{
		ugens.remove(input);
	}
	
	protected void sampleRateChanged()
	{
		// ddf: need to let all of the UGens in our list know about the sample rate change
		for(int i = 0; i < ugens.size(); i++)
		{
			ugens.get(i).setSampleRate(sampleRate());
		}
	}
	
	@Override
	protected void uGenerate(float[] channels) 
	{
		// ddf: we use toArray here because it's possible that one of the
		//      UGens in our list will remove itself from the list as part of
		//      its tick (for example: ADSR has an unpatchAfterNoteFinished feature
		//      which results in it unpatching itself during its uGenerate call).
		//      If the list is modified while we are iterating over it, we won't 
		//      generate audio correctly.
		UGen[] ugensArray = ugens.toArray( new UGen[] {} );
		for(int i = 0; i < ugensArray.length; i++)
		{
			ugensArray[i].tick(channels);
		}
		
		for(int i = 0; i < channels.length; ++i)
		{
			channels[i] = 0;
		}
	}
}
