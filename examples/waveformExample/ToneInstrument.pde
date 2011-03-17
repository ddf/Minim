// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class ToneInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Oscil toneOsc;
  ADSR adsr;
  AudioOutput out;
  
  // constructors for this intsrument
  ToneInstrument( String note, float amplitude, Waveform wave, AudioOutput output )
  {
    // equate class variables to constructor variables as necessary
    out = output;
    
    // make any calculations necessary for the new UGen objects
    // this turns a note name into a frequency
    float frequency = Frequency.ofPitch( note ).asHz();
    
    // create new instances of any UGen objects as necessary
    toneOsc = new Oscil( frequency, amplitude, wave );
    adsr = new ADSR( 1.0, 0.04, 0.01, 1.0, 0.1 );
 
    // patch everything together up to the final output
    toneOsc.patch( adsr );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // turn on the adsr
    adsr.noteOn();
    // patch the adsr into the output
    adsr.patch( out );
  }
  
  void noteOff()
  {
    // turn off the note in the adsr
    adsr.noteOff();
    // but don't unpatch until the release is through
    adsr.unpatchAfterRelease( out );
  }
}
