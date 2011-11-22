// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class ToneInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Oscil sineOsc;
  // we use the LFO to control the phase of sineOsc
  Oscil LFO;
  AudioOutput out;
  
  // constructors for this intsrument
  ToneInstrument( float frequency, float amplitude, float lfoFrequency, AudioOutput output )
  {
    // equate class variables to constructor variables as necessary 
    out = output;
    
    // create new instances of any UGen objects as necessary
    sineOsc = new Oscil( frequency, amplitude, Waves.SINE );
    LFO = new Oscil( lfoFrequency, 1.0f, Waves.SINE );
    
    // connect the LFO to the phase of sineOsc
    LFO.patch( sineOsc.phase );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // reset sineOsc and LFO so that we don't get clicks when starting the note
    sineOsc.reset();
    LFO.reset();
    // and patch to the output
    sineOsc.patch( out );
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    sineOsc.unpatch( out );
  }
}
