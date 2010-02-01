package ddf.minim.ugens;


public class Reciprocal extends UGen
{
	// jam3: define the inputs to gain
    
	public UGenInput denominator;
	private float fixedDenominator;
	
	public Reciprocal()
	{
		this( 1.0f );
	}
	
	public Reciprocal( float fixedDenominator )
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		//audio = new UGenInput(InputType.AUDIO);
		denominator = new UGenInput( InputType.CONTROL );
		this.fixedDenominator = fixedDenominator;
	}
	
	public void setReciprocal( float fixedDenominator )
	{
		this.fixedDenominator = fixedDenominator;
	}

	@Override
	protected void uGenerate( float[] channels ) 
	{
		for( int i = 0; i < channels.length; i++ )
		{
			if ( ( denominator == null ) || ( !denominator.isPatched() ) )
			{
				channels[ i ] = 1.0f/fixedDenominator;
			} else {
				channels[ i ] = 1.0f/denominator.getLastValues()[ 0 ];
			}
		}
	} 
}