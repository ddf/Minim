package ddf.minim.ugens;


/** A UGen for panning a mono signal in a stereo field
 * <p>
 * Balance : should be between -1 and +1
 *  
 * 
 * @author nb, ddf
 */

public class Pan extends UGen
{
	/**
	 * UGens patched to balance should generate values between -1 and +1.
	 */
	public UGenInput balance;

	private UGen  audio;
	private float mBalance;
	static private float PI2 = (float)Math.PI / 2.f;
	
	/**
	 * Construct a Pan UGen with a particular balance and width. 
	 * @param fBalance a value of 0 means no change in the balance.
	 * @param fWidth a value of 0 means no change in the width.
	 */
	public Pan(float fBalance)
	{
		super();
		mBalance = fBalance;
		balance = new UGenInput(InputType.CONTROL);		
	}
	
	@Override
	protected void addInput( UGen in )
	{
		// System.out.println("Adding " + in.toString() + " to Pan.");
		audio = in;
	}
	
	@Override
	protected void removeInput(UGen input)
	{
		if ( audio == input )
		{
			audio = null;
		}
	}
	
	@Override
	protected void sampleRateChanged()
	{
		audio.setSampleRate(sampleRate());
	}
	
	/**
	 * NOTE: Currently only supports stereo audio!
	 */
	protected void uGenerate(float[] channels) 
	{
		if ( balance.isPatched() )
		{
			mBalance = balance.getLastValues()[0];
		}
		
		// ddf: we may want to do stereo panning in a different class
//		if ( width.isPatched() )
//		{
//			mWidth = width.getLastValues()[0];
//
//		}
		
		// tick our audio as MONO because that's what a Pan is for!
		float[] sample = new float[1];
		if ( audio != null )
		{
			audio.tick(sample);
		}
		
		// formula swiped from the MIDI spcification: http://www.midi.org/about-midi/rp36.shtml
    // Left Channel Gain [dB] = 20*log (cos (Pi/2* max(0,CC#10 – 1)/126)
    // Right Channel Gain [dB] = 20*log (sin (Pi /2* max(0,CC#10 – 1)/126)
		
		// dBvalue = 20.0 * log10 ( linear );
		// dB = 20 * log (linear)

		// conversely...
	  // linear = pow ( 10.0, (0.05 * dBvalue) );
		// linear = 10^(dB/20)
		
		float normBalance = (mBalance+1.f) * 0.5f;
		
		// note that I am calculating amplitude directly, by using the linear value
		// that the MIDI specification suggests inputing into the dB formula.
		float leftAmp = (float)Math.cos( PI2 * normBalance );
		float rightAmp = (float)Math.sin( PI2 * normBalance);
		
		channels[0] = sample[0] * leftAmp;
		channels[1] = sample[0] * rightAmp;
		
		// ddf: we may want to do the stereo panning in a different class
//		if( mWidth != 0 )
//		{
//			float tmp = 1.f/Math.max(1.f + mWidth , 2.f);
//			float coef_M = 1.f * tmp;
//			float coef_S = mWidth * tmp;
//			float m = (channels[0] + channels[1])*coef_M;
//			float s = (channels[0] - channels[1])*coef_S;
//			channels[0] = m-s;
//			channels[1] = m+s;
//		}		
	}
}
