/* noiseInstrument
   just plays a burst of noise of the specified tint and amplitude
   
   author: Anderson Mills
   Anderson Mills's work was sponsored by numediart (www.numediart.org).
*/

// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class NoiseInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Noise myNoise;
  Multiplier  multiply;
  AudioOutput out;
  
  // constructors for the intsrument
  NoiseInstrument( float amplitude, Noise.Tint noiseTint, AudioOutput output )
  {
    // equate class variables to constructor variables as necessary
    out = output;
    
    // create new instances of any UGen objects as necessary
    // white noise is used for this instrument
    myNoise = new Noise( amplitude, noiseTint );
    multiply = new Multiplier( 0 );
    
    // patch everything together up to the final output
    myNoise.patch( multiply );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // turn on the multiply
    multiply.setValue( 1.0 );
    // and patch to the output
    multiply.patch( out );
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    // turn off the multiply
    multiply.setValue( 0 );
    // and unpatch the output 
    // this causes the entire instrument to stop calculating sampleframes
    // which is good when the instrument is no longer generating sound.
    multiply.unpatch( out );
  }
}
