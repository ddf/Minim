// BumpyInstrument is an example of using an Oscil to run through
// a wave over the course of a note being played.  This is also an
// example of creating a wave using the WavetableGenerator gen7 function.

// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class BumpyInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Oscil toneOsc, envOsc;
  AudioOutput out;

  // constructor for this instrument  
  BumpyInstrument( String pitch, float amplitude, AudioOutput output )
  {
    // equate class variables to constructor variables as necessary
    out = output;
    
    // calculate the frequency for the oscillator from the note name    
    float frequency = Frequency.ofPitch( pitch ).asHz();
    
    // create a wave for the amplitude envelope.
    // The name of the method "gen7" is a reference to a genorator in Csound.
    // This is a somewhat silly, but demonstrative wave.  It rises from 0 to 1
    // over 1/8th of the time, then goes to 0.15 over 1/8th of it's time, then
    // rises to 1 again over 1/128th of it's time, and then decays again to 0
    // for the rest of the time.  Note that this envelope is of fixed shape regardless
    // of duration.
    Wavetable myEnv = WavetableGenerator.gen7( 8192, 
        new float[] { 0.00, 1.00, 0.15, 1.00, 0.00 }, 
        new int[]   { 1024, 1024,   64, 6080 } );

    // create new instances of any UGen objects as necessary
    // The tone is the first five harmonics of a square wave.
    toneOsc = new Oscil( frequency, 1.0f, Waves.square( 5 ) );
    envOsc = new Oscil( 1.0f, amplitude, myEnv );
    
    // patch everything up to the output
    envOsc.patch( toneOsc.amplitude );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // the duration of the amplitude envelope is set to the length of the note
    envOsc.setFrequency( 1.0f/dur );
    // the tone ascillator is patched directly to the output.
    toneOsc.patch( out );  
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    // unpatch the tone oscillator when the note is over
    toneOsc.unpatch( out );
  }
}
