package ddf.minim.ugens;


public class Midi2Hz extends UGen
{
	// jam3: define the inputs to gain
    
	public UGenInput midiNoteIn;
	private float fixedMidiNoteIn;
	
	public Midi2Hz()
	{
		this( 0.0f );
	}
	
	public Midi2Hz( float fixedMidiNoteIn )
	{
		super();
		// jam3: These can't be instantiated until the uGenInputs ArrayList
		//       in the super UGen has been constructed
		//audio = new UGenInput(InputType.AUDIO);
		midiNoteIn = new UGenInput( InputType.CONTROL );
		this.fixedMidiNoteIn = fixedMidiNoteIn;
	}
	
	public void setMidiNoteIn( float fixedMidiNoteIn )
	{
		this.fixedMidiNoteIn = fixedMidiNoteIn;
	}

	@Override
	protected void uGenerate( float[] channels ) 
	{
		for( int i = 0; i < channels.length; i++ )
		{
			if ( ( midiNoteIn == null ) || ( !midiNoteIn.isPatched() ) )
			{
				channels[ i ] = Frequency.ofMidiNote( fixedMidiNoteIn ).asHz();
			} else {
				channels[ i ] = Frequency.ofMidiNote( midiNoteIn.getLastValues()[ 0 ] ).asHz();
			}
		}
	} 
}