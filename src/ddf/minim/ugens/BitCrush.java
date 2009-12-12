package ddf.minim.ugens;

import ddf.minim.Minim;

public class BitCrush extends UGen
{
	// jam3: define the inputs to gain
    
	public UGenInput audio;
	public UGenInput bitRes;
	private float bitResolution;
	private float nLevels;
	private float halfNLevels;
	public BitCrush()
	{
		this( 1.0f );
	}
	
	public BitCrush(float localBitRes)
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		//audio = new UGenInput(InputType.AUDIO);
		audio = new UGenInput(InputType.AUDIO);
		bitRes = new UGenInput(InputType.CONTROL);
		bitResolution = localBitRes;
		nLevels = (float)Math.floor(Math.pow(2.0, bitResolution));
		halfNLevels = nLevels/2.0f;
		Minim.debug("bitCrush initializing to " + nLevels + " levels.");
	}
	
	public void setBitRes(float localBitRes)
	{
		bitResolution = localBitRes;
	}

	@Override
	protected void uGenerate(float[] channels) 
	{
		for(int i = 0; i < channels.length; i++)
		{
			//float tmp = audio.getLastValues()[i];
			if ((bitRes != null) && (bitRes.isPatched()))
			{
				bitResolution = bitRes.getLastValues()[0];
				nLevels = (float)Math.floor(Math.pow(2.0, bitResolution));
				halfNLevels = nLevels/2.0f;
			}
			channels[i]=(float)(Math.floor(halfNLevels*(audio.getLastValues()[i]))/halfNLevels);
		}
	} 
}