package ddf.minim.ugens;
/**
 * A UGen which simply returns the reciprocal value of it's input.
 * @author nodog
 *
 */

public class Reciprocal extends UGen
{
	/**
	 * denominator is the default input 
	 */
	public UGenInput denominator;
	
	/**
	 * 
	 */
	public Reciprocal()
	{
		this( 1.0f );
	}
	/**
	 * Constructor for Reciprocal
	 * @param fixedDenominator
	 * 			the denominator value if the input is never connected
	 */
	public Reciprocal( float fixedDenominator )
	{
		super();
		//audio = new UGenInput(InputType.AUDIO);
		// for this UGen, denominator is the main input and can be audio
		denominator = new UGenInput( InputType.AUDIO );
    denominator.setLastValue(fixedDenominator);
	}
	
	/**
	 * Used to change the fixedDenominator value after instantiation
	 * @param fixedDenominator 
	 *			the denominator value if the input is never connected
	 */
	public void setReciprocal( float fixedDenominator )
	{
		denominator.setLastValue( fixedDenominator );
	}

	/**
	 * Generate the sampleframe
	 */
	@Override
	protected void uGenerate( float[] channels ) 
	{
		for( int i = 0; i < channels.length; i++ )
		{
			channels[ i ] = 1.0f / denominator.getLastValue();
		}
	} 
}