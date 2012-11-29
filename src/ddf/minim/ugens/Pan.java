package ddf.minim.ugens;

import ddf.minim.UGen;

/**
 * A UGen for panning a mono signal in a stereo field
 * <p>
 * 
 * @author nb, ddf
 */

public class Pan extends UGen
{
	/**
	 * UGens patched to pan should generate values between -1 and +1.
	 */
	public UGenInput		pan;

	private UGen			audio;
	private float[]			tickBuffer = new float[1];

	static private float	PIOVER2	= (float)Math.PI / 2.f;

	/**
	 * Construct a Pan UGen with a particular balance and width.
	 * 
	 * @param panValue
	 *            float: a value of 0 means to pan dead center, 
	 *            -1 hard left, and 1 hard right.
	 */
	public Pan(float panValue)
	{
		super();
		pan = new UGenInput( InputType.CONTROL );
		pan.setLastValue( panValue );
	}

	/**
	 * Set the pan value of this Pan. Values passed to this method should be
	 * between -1 and +1.
	 * 
	 * @param panValue
	 */
	public void setPan(float panValue)
	{
		pan.setLastValue( panValue );
	}

	@Override
	protected void addInput(UGen in)
	{
		// System.out.println("Adding " + in.toString() + " to Pan.");
		audio = in;
		// we only deal in MONO!
		audio.setChannelCount( 1 );
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
		if ( audio != null )
		{
			audio.setSampleRate( sampleRate() );
		}
	}

	public void setChannelCount(int numberOfChannels)
	{
		if ( numberOfChannels == 2 )
		{
			super.setChannelCount( numberOfChannels );
		}
		else
		{
			throw new IllegalArgumentException( "Pan MUST be ticked with STEREO output! It doesn't make sense in any other context!" );
		}
	}

	/**
	 * NOTE: Currently only supports stereo audio!
	 */
	@Override
	protected void uGenerate(float[] channels)
	{
		if ( channels.length != 2 )
		{
			throw new IllegalArgumentException( "Pan MUST be ticked with STEREO output! It doesn't make sense in any other context!" );
		}

		float panValue = pan.getLastValue();

		// tick our audio as MONO because that's what a Pan is for!
		if ( audio != null )
		{
			audio.tick( tickBuffer );
		}

		// formula swiped from the MIDI spcification:
		// http://www.midi.org/about-midi/rp36.shtml
		// Left Channel Gain [dB] = 20*log (cos (Pi/2* max(0,CC#10 – 1)/126)
		// Right Channel Gain [dB] = 20*log (sin (Pi /2* max(0,CC#10 – 1)/126)

		// dBvalue = 20.0 * log10 ( linear );
		// dB = 20 * log (linear)

		// conversely...
		// linear = pow ( 10.0, (0.05 * dBvalue) );
		// linear = 10^(dB/20)

		float normBalance = ( panValue + 1.f ) * 0.5f;

		// note that I am calculating amplitude directly, by using the linear
		// value
		// that the MIDI specification suggests inputing into the dB formula.
		float leftAmp = (float)Math.cos( PIOVER2 * normBalance );
		float rightAmp = (float)Math.sin( PIOVER2 * normBalance );

		channels[0] = tickBuffer[0] * leftAmp;
		channels[1] = tickBuffer[0] * rightAmp;
	}
}
