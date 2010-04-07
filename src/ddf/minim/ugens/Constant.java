package ddf.minim.ugens;


/**
 * Just outputs a constant value.
 * @author Anderson Mills
 *
 */
public class Constant extends UGen
{
	private float value;
	
	/**
	 * Empty constructor for Constant.
	 * Sets value to 1.0.
	 */
	public Constant()
	{
		this( 1.0f );
	}
	
	/**
	 * Constructor for Constant.
	 * Sets value to val.
	 * @param val
	 */
	public Constant( float val )
	{
		super();
		value = val;
	}
	
	/**
	 * Sets value of Constant during execution.
	 * @param val
	 */
	public void setConstant( float val )
	{
		value = val;
	}

	@Override
	protected void uGenerate( float[] channels ) 
	{
		for(int i = 0; i < channels.length; i++)
		{
			channels[ i ] = value;
		}
	} 
}