package ddf.minim.ugens;

import ddf.minim.Minim;

/**
 * BitCrush is an effect that reduces the fidelity of the incoming signal.
 * This results in a sound that is "crunchier" sounding, or "distorted". 
 * <p>
 * Audio is represented digitally (ultimately) as an integral value. If you 
 * have 16-bit audio, then you can represent a sample value with any number 
 * in the range -32,768 to +32,767. If you bit-crush this audio do be 8-bit,
 * then you effectively reduce it representation to -128 to +127, even though 
 * you will still represent it with a 16-bit number. This reduction in the 
 * fidelity of the representation essentially squares off the waveform, 
 * which makes it sound "crunchy". Try bit crushing down to 1-bit and see 
 * what you get!
 *  
 * @author Anderson Mills
 *
 */
public class BitCrush extends UGen
{
	// jam3: define the inputs to gain
    
	/**
	 * The audio input is where audio that gets bit-crushed should be patched. 
	 * However, you don't need to patch directly to this input, patching to
	 * the UGen itself will accomplish the same thing.
	 */
	public UGenInput audio;
	
	/**
	 * Control the bit resolution with another UGen by patching to bitRes. Values that 
	 * make sense for this start at 1 and go up to whatever the actual resolution of 
	 * the incoming audio is (typically 16).
	 */
	public UGenInput bitRes;
	
	
	private float bitResolution;
	private float nLevels;
	private float halfNLevels;
	
	/**
	 * Construct a BitCrush with a bit resolution of 1.
	 *
	 */
	public BitCrush()
	{
		this( 1.0f );
	}
	
	/**
	 * Construct a BitCrush with the specified bit resolution.
	 * 
	 * @param localBitRes typically you'll want this in the range [1,16]
	 */
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
	
	/**
	 * Set the bit resolution directly.
	 * 
	 * @param localBitRes typically you'll want this in the range [1,16]
	 */
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
			// TODO: using these Math functions means values get casted up to a double.
			//       we can do better by just using multiplication and casting to int.
			if (bitRes.isPatched())
			{
				bitResolution = bitRes.getLastValues()[0];
				nLevels = (float)Math.floor(Math.pow(2.0, bitResolution));
				halfNLevels = nLevels/2.0f;
			}
			channels[i]=(float)(Math.floor(halfNLevels*(audio.getLastValues()[i]))/halfNLevels);
		}
	} 
}