package ddf.minim.ugens;


/** A UGen for panning a mono signal in a stereo field
 * <p>
 * 
 * @author nb, ddf
 */

public class Pan extends UGen
{
	/**
	 * UGens patched to pan should generate values between -1 and +1.
	 */
	public UGenInput pan;

	private UGen  audio;
	private float mPanValue;
	static private float PI2 = (float)Math.PI / 2.f;
	
	/**
	 * Construct a Pan UGen with a particular balance and width. 
	 * @param fBalance a value of 0 means to pan dead center, -1 hard left, and 1 hard right.
	 */
	public Pan(float panValue)
	{
		super();
		mPanValue = panValue;
		pan = new UGenInput(InputType.CONTROL);		
	}
	
	/**
	 * Set the pan value of this Pan. Values passed to this method should be 
	 * between -1 and +1.
	 * 
	 * @param panValue
	 */
	public void setPan( float panValue )
	{
		mPanValue = panValue;
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
	@Override
	protected void uGenerate(float[] channels) 
	{
		if ( pan.isPatched() )
		{
			mPanValue = pan.getLastValues()[0];
		}
		
		// tick our audio as MONO because that's what a Pan is for!
		float[] sample = new float[1];
		if ( audio != null )
		{
			audio.tick(sample);
			// we need to then get the last value because 
			// something else might have already ticked our audio input
			// in that case, sample will not be filled with anything
			// in tick.
			sample[0] = audio.getLastValues()[0];
		}
		
		// formula swiped from the MIDI spcification: http://www.midi.org/about-midi/rp36.shtml
		// Left Channel Gain [dB] = 20*log (cos (Pi/2* max(0,CC#10 – 1)/126)
		// Right Channel Gain [dB] = 20*log (sin (Pi /2* max(0,CC#10 – 1)/126)
		
		// dBvalue = 20.0 * log10 ( linear );
		// dB = 20 * log (linear)

		// conversely...
		// linear = pow ( 10.0, (0.05 * dBvalue) );
		// linear = 10^(dB/20)
		
		float normBalance = (mPanValue+1.f) * 0.5f;
		
		// note that I am calculating amplitude directly, by using the linear value
		// that the MIDI specification suggests inputing into the dB formula.
		float leftAmp = (float)Math.cos( PI2 * normBalance );
		float rightAmp = (float)Math.sin( PI2 * normBalance);
		
		channels[0] = sample[0] * leftAmp;
		channels[1] = sample[0] * rightAmp;		
	}
}
