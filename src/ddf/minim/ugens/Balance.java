package ddf.minim.ugens;

public class Balance extends UGen{

	// jam3: define the inputs to gain
    
	public UGenInput audio;
	public UGenInput balance;
	private float balanceVal;
	
	public Balance()
	{
		this( 0.0f );
	}
	
	public Balance( float balanceVal )
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		//audio = new UGenInput(InputType.AUDIO);
		audio = new UGenInput(InputType.AUDIO);
		balance = new UGenInput(InputType.CONTROL);
		this.balanceVal = balanceVal;
	}
	
	public void setValue(float balanceVal)
	{
		this.balanceVal = balanceVal;
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		for(int i = 0; i < channels.length; i++)
		{
			float tmp = audio.getLastValues()[i];
			if ( ( balance != null ) && ( balance.isPatched() ) )
			{
				balanceVal = balance.getLastValues()[0];
			}
			channels[i] = tmp*(float)Math.min( 1.0f, Math.max( 0.0f, 1.0f + Math.pow( -1.0f, i )* balanceVal ) );
		}
	} 
}