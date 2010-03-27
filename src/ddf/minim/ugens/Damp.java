package ddf.minim.ugens;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;

/**
 * A UGen that starts at an amplitude value
 * and changes to zero over a specified time.
 * @author nodog
 *
 */
public class Damp extends UGen
{
	// jam3: define the inputs to Oscil
	public UGenInput audio;

	// the initial amplitude of the damp
	private float maxAmp;
	// the current amplitude
	private float amp;
	// the time from maxAmp to 0
	private float dampTime;
	// the time from 0 to maxAmp
	private float attackTime;
	// the current size of the step
	private float timeStepSize;
	// the current time
	private float now;
	// the damp has been activated
	private boolean isActivated;
	// unpatch the note after it's finished
	private boolean unpatchAfterDamp;
	private AudioOutput output;
	
	// constructors
	public Damp()
	{
		this( 0.0f, 1.0f, 1.0f );
	}
	
	public Damp( float dampTime )
	{
		this( 0.0f, dampTime, 1.0f );
	}
	
	public Damp( float attackTime, float dampTime )
	{
		this( attackTime, dampTime, 1.0f );
	}
	
	public Damp( float attackTime, float dampTime, float maxAmp )
	{
		super();
		audio = new UGenInput(InputType.AUDIO);
		this.attackTime = attackTime;
		this.dampTime = dampTime;
		this.maxAmp = maxAmp;
		isActivated = false;
		Minim.debug(" attackTime = " + attackTime + " dampTime = " + dampTime 
				+ " maxAmp = " + this.maxAmp + " now = " + now );
	}
	
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
	
	public void setAttackTime( float attackTime )
	{
		this.attackTime = attackTime;
	}
	
	public void setDampTime( float dampTime )
	{
		this.dampTime = dampTime;
	}
	
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
	
	public void sampleRateChanged()
	{
		timeStepSize = 1/sampleRate;
		setSampleRate( sampleRate );
	}
	
	/**
	 * Tell the Damp that it should unpatch itself from the output after the release time.
	 * @param output
	 */
	public void unpatchAfterDamp( AudioOutput output )
	{
		unpatchAfterDamp = true;
		this.output = output;
	}
	
	@Override
	protected void uGenerate( float[] channels ) 
	{
		if ( !isActivated ) 
		{
			for( int i = 0; i < channels.length; i++ )
			{
				channels[ i ] = 0.0f;
			}
		} else if ( now >= ( dampTime + attackTime ) )
		{
			for( int i = 0; i < channels.length; i++ )
			{
				channels[ i ] = 0.0f;
			}
			if ( unpatchAfterDamp )
			{
			 	unpatch( output );
			 	Minim.debug(" unpatching Damp ");
			}
		} else if ( now >= attackTime )  // in the damp time
		{
			amp += ( 0 - amp )*timeStepSize/( dampTime + attackTime - now );
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
