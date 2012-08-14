package ddf.minim.ugens;

import ddf.minim.UGen;

/**
 * Multiplier is a UGen that will simply multiply the incoming audio signal by either a fixed value 
 * or by whatever its amplitude input is currently generating.
 * 
 * @author Damien Di Fede
 *
 */

public class Multiplier extends UGen
{
	/**
	 * The audio input is where incoming audio should be patched, but you can simply patch to the 
	 * Multiplier itself.
	 */  
	public UGenInput audio;
	
	/**
	 * The amplitude input allows you to control the value being used for multiplying with another UGen.
	 */
	public UGenInput amplitude;
	
	/**
	 * Construct a Multiplier with a fixed value of 1, which will mean incoming audio is not changed.
	 *
	 */
	public Multiplier()
	{
		this( 1f );
	}
	
	/**
	 * Construct a Multiplier with the fixed value of multValue.
	 * @param multValue
	 */
	public Multiplier( float multValue )
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		//audio = new UGenInput(InputType.AUDIO);
		audio = new UGenInput(InputType.AUDIO);
		amplitude = new UGenInput(InputType.CONTROL);
		amplitude.setLastValue(multValue);
	}
	
	/**
	 * Set the fixed value of this Multiplier.
	 * @param gainVal
	 */
	public void setValue( float multValue )
	{
		amplitude.setLastValue( multValue );
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		for(int i = 0; i < channelCount(); i++)
		{
			channels[i] = amplitude.getLastValue() * audio.getLastValues()[i];
		}
	} 
}