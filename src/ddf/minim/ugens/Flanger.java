package ddf.minim.ugens;

public class Flanger extends UGen
{
	/**
	 * Where the input goes.
	 */
	public UGenInput	audio;

	/**
	 * How much does the flanger delay the incoming signal. Used as the low
	 * value of the modulated delay amount.
	 */
	public UGenInput	delay;

	/**
	 * The frequency of the LFO applied to the delay.
	 */
	public UGenInput	rate;

	/**
	 * How many milliseconds the LFO modulates the delay by at the maximum.
	 */
	public UGenInput	depth;

	/**
	 * How much of output is fed back into the effect.
	 */
	public UGenInput	feedback;

	/**
	 * How much of the dry signal is added to the output.
	 */
	public UGenInput	dry;

	/**
	 * How much of the flanged signal is added to the output.
	 */
	public UGenInput	wet;

	private float[]		delayBuffer;
	private int			outputFrame;
	private int			bufferFrameLength;

	// ////////////
	// LFO
	// ////////////

	// where we will sample our waveform, moves between [0,1]
	private float		step;
	// the step size we will use to advance our step
	private float		stepSize;
	// what was our frequency from the last time we updated our step size
	// stashed so that we don't do more math than necessary
	private float		prevFreq;
	// 1 / sampleRate, which is used to calculate stepSize
	private float		oneOverSampleRate;

	public Flanger(float delayLength, float lfoRate, float delayDepth,
			float feedbackAmplitude, float dryAmplitude, float wetAmplitude)
	{
		audio = addAudio();
		delay = addControl( delayLength );
		rate = addControl( lfoRate );
		depth = addControl( delayDepth );
		feedback = addControl( feedbackAmplitude );
		dry = addControl( dryAmplitude );
		wet = addControl( wetAmplitude );
	}

	private void resetBuffer()
	{
		int sampleCount = (int)( 100 * sampleRate() / 1000 );
		delayBuffer = new float[sampleCount * audio.channelCount()];
		outputFrame = 0;
		bufferFrameLength = sampleCount;
	}

	// clamps rate for us
	private float getRate()
	{
		float r = rate.getLastValue();
		return r > 0.001f ? r : 0.001f;
	}

	protected void sampleRateChanged()
	{
		resetBuffer();

		oneOverSampleRate = 1 / sampleRate();
		// don't call updateStepSize because it checks for frequency change
		stepSize = getRate() * oneOverSampleRate;
		prevFreq = getRate();
		// start at the lowest value
		step = 0.25f;
	}

	// updates our step size based on the current frequency
	private void updateStepSize()
	{
		float currFreq = getRate();
		if ( prevFreq != currFreq )
		{
			stepSize = currFreq * oneOverSampleRate;
			prevFreq = currFreq;
		}
	}

	public void setAudioChannelCount(int numberOfChannels)
	{
		super.setAudioChannelCount( numberOfChannels );
		resetBuffer();
	}

	protected void uGenerate(float[] out)
	{
		// generate lfo value
		float lfo = Waves.SINE.value( step );

		// modulate the delay amount using the lfo value.
		// we always modulate tp a max of 5ms above the input delay.
		float dep = depth.getLastValue() * 0.5f;
		float delMS = delay.getLastValue() + ( lfo * dep + dep );

		// how many sample frames is that?
		int delFrame = (int)( delMS * sampleRate() / 1000 );

		for ( int i = 0; i < out.length; ++i )
		{
			int outputIndex = outputFrame * audio.channelCount() + i;
			float inSample = audio.getLastValues()[i];
			float wetSample = delayBuffer[outputIndex];

			// figure out where we need to place the delayed sample in our ring
			// buffer
			int delIndex = ( ( outputFrame + delFrame ) * audio.channelCount() + i )
					% delayBuffer.length;
			delayBuffer[delIndex] = inSample + wetSample
					* feedback.getLastValue();

			// the output sample is in plus wet, each scaled by amplitude inputs
			out[i] = inSample * dry.getLastValue() + wetSample
					* wet.getLastValue();
		}

		// next output frame
		++outputFrame;
		if ( outputFrame == bufferFrameLength )
		{
			outputFrame = 0;
		}

		updateStepSize();

		// step the LFO
		step += stepSize;
		if ( step > 1 )
		{
			step -= 1;
		}
	}
}