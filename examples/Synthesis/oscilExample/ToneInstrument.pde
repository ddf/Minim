// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class ToneInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Oscil sineOsc, lFOOsc;
  Multiplier  multiplyGate;
  AudioOutput out;
  
  // constructors for this intsrument
  ToneInstrument( float frequency, float amplitude, AudioOutput output )
  {
    // equate class variables to constructor variables as necessary 
    out = output;
    
    // create new instances of any UGen objects as necessary
    sineOsc = new Oscil( frequency, amplitude, Waves.SINE );
    multiplyGate = new Multiplier( 0 );
    
    // patch everything together up to the final output
    sineOsc.patch( multiplyGate );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // turn on the multiply
    multiplyGate.setValue( 1.0 );
    // and patch to the output
    multiplyGate.patch( out );
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    // turn off the multiply
    multiplyGate.setValue( 0 );
    // and unpatch the output 
    // this causes the entire instrument to stop calculating sampleframes
    // which is good when the instrument is no longer generating sound.
    multiplyGate.unpatch( out );
  }
}
