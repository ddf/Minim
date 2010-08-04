package ddf.minim.ugens;

/**
 * Midi2Hz is a UGen that will convert a MIDI note number to a frequency in Hertz. This is useful if you 
 * want to drive the frequency input of an Oscil with something that generates MIDI notes.
 * 
 * @author Anderson Mills
 *
 */

public class Midi2Hz extends UGen
{
	/**
	 * Patch something to this input that generates Midi note numbers (values in the range [0,127]
	 */
	public UGenInput midiNoteIn;
	
	private float fixedMidiNoteIn;
	
	/**
	 * Construct a Midi2Hz that generates a fixed value from MIDI note 0.
	 *
	 */
	public Midi2Hz()
	{
		this( 0.0f );
	}
	
	/**
	 * Construct a Midi2Hz that generates a fixed value from fixedMidiNoteIn
	 * @param fixedMidiNoteIn
	 */	
	public Midi2Hz( float fixedMidiNoteIn )
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		//audio = new UGenInput(InputType.AUDIO);
		midiNoteIn = new UGenInput( InputType.CONTROL );
		this.fixedMidiNoteIn = fixedMidiNoteIn;
	}
	
	/**
	 * Set the fixed value this will use if midiNoteIn is not patched.
	 * @param fixedMidiNoteIn
	 */
	public void setMidiNoteIn( float fixedMidiNoteIn )
	{
		this.fixedMidiNoteIn = fixedMidiNoteIn;
	}

	@Override
	protected void uGenerate( float[] channels ) 
	{
		for( int i = 0; i < channels.length; i++ )
		{
			if ( !midiNoteIn.isPatched() )
			{
				channels[ i ] = Frequency.ofMidiNote( fixedMidiNoteIn ).asHz();
			} else {
				channels[ i ] = Frequency.ofMidiNote( midiNoteIn.getLastValues()[ 0 ] ).asHz();
			}
		}
	} 
}