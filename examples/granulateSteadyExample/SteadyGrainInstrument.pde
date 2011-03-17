/* SteadyGrainInstrument
   is an example of how to use GranulateSteady.  Basically it chops up a triangle wave
   into short bits given by the incoming period and the percentOn specifying the duty cycle.
*/

// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class SteadyGrainInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  AudioOutput out;
  GranulateSteady chopper;
  
  // constructor for this instrument
  SteadyGrainInstrument( float frequency, float amplitude, float period, float percentOn, AudioOutput output )
  {
    // equate class variables to constructor variables as necessary 
    out = output;
    
    // create new instances of any UGen objects as necessary
    // Need the triangle tone to chop up.
    Oscil toneOsc = new Oscil( frequency, amplitude, Waves.TRIANGLE );
    // need the GranulateSteady envelope
    chopper = new GranulateSteady( period*percentOn, period*( 1 - percentOn ), 0.0025 );
    
    // patch everything together up to the final output
    // the tone just goes into the chopper
    toneOsc.patch( chopper );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    chopper.patch( out );
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    chopper.unpatch( out );
  }
}
