package ddf.minim.ugens;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;

/**
 * A UGen that starts at an amplitude value
 * and changes to zero over a specified time.
 * All times are measured in seconds.
 * @author Anderson Mills
 *
 */
public class Damp extends UGen
{
	/**
	 *  The default input is "audio." You don't need to patch directly to this input,
	 *  patching to the UGen itself will accomplish the same thing.
	 */
	public UGenInput audio;

	// the maximum amplitude of the damp
	private float maxAmp;
	// the current amplitude
	private float amp;
	// the time from maxAmp to afterAmplitude
	private float dampTime;
	// the time from beforeAmplitude to maxAmp
	private float attackTime;
	// amplitude before the damp hits
	private float beforeAmplitude;
	// amplitude after the release of the damp
	private float afterAmplitude;
	// the current size of the step
	private float timeStepSize;
	// the current time
	private float now;
	// the damp has been activated
	private boolean isActivated;
	// unpatch the note after it's finished
	private boolean unpatchAfterDamp;
	private AudioOutput output;
	
	/**
	 * Constructor for Damp envelope.
	 * attackTime, rise time of the damp envelope, defaults to 0.
	 * dampTime, decay time of the damp envelope, defaults to 1.
	 * maxAmp, maximum amlitude of the damp envelope, defaults to 1.
	 * befAmp, amplitude before the damp envelope,
	 * and aftAmp, amplitude after the damp envelope,
	 * default to 0.
	 */
	public Damp()
	{
		this( 0.0f, 1.0f, 1.0f, 0.0f, 0.0f );
	}
	/**
	 * Constructor for Damp envelope.
	 * attackTime, rise time of the damp envelope, defaults to 0.
	 * maxAmp, maximum amlitude of the damp envelope, defaults to 1.
	 * befAmp, amplitude before the damp envelope,
	 * and aftAmp, amplitude after the damp envelope,
	 * default to 0.
	 * @param dampTime
	 * 			decay time of the damp envelope
	 */
	 public Damp( float dampTime )
	{
		this( 0.0f, dampTime, 1.0f, 0.0f, 0.0f );
	}
	/**
	 * Constructor for Damp envelope.
	 * maxAmp, maximum amlitude of the damp envelope, defaults to 1.
	 * befAmp, amplitude before the damp envelope,
	 * and aftAmp, amplitude after the damp envelope,
	 * default to 0.
	 * @param attackTime 
	 * 			rise time of the damp envelope
	 * @param dampTime
	 * 			decay time of the damp envelope
	 */	
	public Damp( float attackTime, float dampTime )
	{
		this( attackTime, dampTime, 1.0f, 0.0f, 0.0f );
	}
	/**
	 * Constructor for Damp envelope.
	 * befAmp, amplitude before the damp envelope,
	 * and aftAmp, amplitude after the damp envelope,
	 * default to 0.
	 * @param attackTime 
	 * 			rise time of the damp envelope
	 * @param dampTime
	 * 			decay time of the damp envelope
	 * @param maxAmp
	 * 			maximum amlitude of the damp envelope
	 */
	public Damp( float attackTime, float dampTime, float maxAmp )
	{
		this( attackTime, dampTime, maxAmp, 0.0f, 0.0f );
	}
	/**
	 * Constructor for Damp envelope.
	 * @param attackTime 
	 * 			rise time of the damp envelope
	 * @param dampTime
	 * 			decay time of the damp envelope
	 * @param maxAmp
	 * 			maximum amlitude of the damp envelope
	 * @param befAmp
	 * 			amplitude before the damp envelope
	 * @param aftAmp
	 * 			amplitude after the damp envelope
	 */
	public Damp( float attackTime, float dampTime, float maxAmp, float befAmp, float aftAmp )
	{
		super();
		audio = new UGenInput(InputType.AUDIO);
		this.attackTime = attackTime;
		this.dampTime = dampTime;
		this.maxAmp = maxAmp;
		beforeAmplitude = befAmp;
		afterAmplitude = aftAmp;
		isActivated = false;
		amp = beforeAmplitude;
		Minim.debug(" attackTime = " + attackTime + " dampTime = " + dampTime 
				+ " maxAmp = " + this.maxAmp + " now = " + now );
	}
	
	/**
	 * Specifies that the damp envelope should begin.
	 */
	public void activate()
	{
		now = 0f;
		isActivated = true;
		if( timeStepSize > attackTime )
		{
			amp = maxAmp;
		}  else
		{
			amp = 0f;
		}
	}
	
	/**
	 * Permits the setting of the attackTime parameter.
	 * @param attackTime
	 * 			rise time of the damp envelope
	 */
	public void setAttackTime( float attackTime )
	{
		this.attackTime = attackTime;
	}
	
	/**
	 * Permits the setting of the attackTime parameter.
	 * @param dampTime
	 * 			decay time of the damp envelope
	 */
	public void setDampTime( float dampTime )
	{
		this.dampTime = dampTime;
	}
	
	/**
	 * Permits the setting of the attackTime parameter.  If attackTime is
	 * positive, and less than the total duration, then the dampTime is 
	 * the total duration after the attackTime, otherwise, the attackTime
	 * and dampTime are both set to half the duration.
	 * 
	 * @param duration
	 * 			duration of the entire damp envelope
	 */
	public void setDampTimeFromDuration( float duration )
	{
		float tmpDampTime = duration - attackTime;
		if ( tmpDampTime > 0.0f )
		{
			dampTime = tmpDampTime;
		} else
		{
			attackTime = duration/2.0f;
			dampTime = duration/2.0f;
		}
	}
	
	@Override
	protected void sampleRateChanged()
	{
		timeStepSize = 1/sampleRate();
	}	

	/**
	 * Tell the Damp that it should unpatch itself from the output after the release time.
	 * @param output
	 * 			AudioOutput for this Damp
	 */
	public void unpatchAfterDamp( AudioOutput output )
	{
		unpatchAfterDamp = true;
		this.output = output;
	}
	
	@Override
	protected void uGenerate( float[] channels ) 
	{
		// before the damp
		if ( !isActivated ) 
		{
			for( int i = 0; i < channels.length; i++ )
			{
				channels[ i ] = beforeAmplitude*audio.getLastValues()[ i ];
			}
		}
		// after the damp
		else if ( now >= ( dampTime + attackTime ) )
		{
			for( int i = 0; i < channels.length; i++ )
			{
				channels[ i ] = afterAmplitude*audio.getLastValues()[ i ];
			}
			if ( unpatchAfterDamp )
			{
			 	unpatch( output );
			 	Minim.debug(" unpatching Damp ");
			}
		}
		// after the attack, during the decay
		else if ( now >= attackTime )  // in the damp time
		{
			amp += ( afterAmplitude - amp )*timeStepSize/( dampTime + attackTime - now );
			for( int i = 0; i < channels.length; i++ )
			{
				channels[i] = amp*audio.getLastValues()[ i ];
			}
			now += timeStepSize;
		} else // in the attack time
		{
			amp += ( maxAmp - amp )*timeStepSize/( attackTime - now );
			for( int i = 0; i < channels.length; i++ )
			{
				channels[i] = amp*audio.getLastValues()[ i ];
			}
			now += timeStepSize;
		}
	}
}
