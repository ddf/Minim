// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class ToneInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Oscil sineOsc;
  Damp  damp;
  AudioOutput out;

  // constructors for this instrument  
  ToneInstrument(float frequency, float amplitude, AudioOutput output)
  {
    // equate class variables to constructor variables as necessary
    out = output;
    
    // create new instances of the UGen objects for this instrument
    sineOsc = new Oscil( frequency, amplitude, Waves.TRIANGLE );
    damp = new Damp( 0.001, 1.0 );
    
    // patch everything together up to the final output
    sineOsc.patch( damp );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn(float dur)
  {
    // set the damp time from the duration given to the note
    damp.setDampTimeFromDuration( dur );
    // activate the damp
    damp.activate();
    // and finally patch the damp to the output
    damp.patch( out );
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    // the damp time of a damp can be changed after damp has been started,
    // so unpatching after the entire damp is over is useful.
    damp.unpatchAfterDamp( out );
  }
}
