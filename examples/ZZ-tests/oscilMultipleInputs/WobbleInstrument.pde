// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class WobbleInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Oscil sineOsc, lFO1, lFO2, lFO3, lFO4;
  Multiplier  multiplyGate, multiplyLFO;
  AudioOutput out;
  
  // constructors for this intsrument
  // amplitude is the loudness
  // frequency is the audio frequency
  // lFOFreq is the frequency of the low frequency oscillator
  // output is the audio output
  WobbleInstrument( float amplitude, float frequency, float lFOFreq, AudioOutput output )
  {
    // equate class variables to constructor variables as necessary 
    out = output;
    
    // create new instances of any UGen objects as necessary
    sineOsc = new Oscil( frequency, amplitude, Waves.TRIANGLE );  //audio
    lFO1 = new Oscil( lFOFreq/2.0, frequency, Waves.SINE );  //frequency
    lFO2 = new Oscil( frequency/2.0, 1.0, Waves.SINE );  //amplitude
    lFO3 = new Oscil( frequency/3.0, 100.0, Waves.SINE );  //freqMod
    lFO4 = new Oscil( lFOFreq/4.0, 0.5, Waves.SINE ); //ampMod
    multiplyGate = new Multiplier( 0 );
    multiplyLFO = new Multiplier( 1 );
    
    // patch the different LFOs into the different Oscil inputs
    lFO1.patch( sineOsc.frequency );
    lFO2.patch( sineOsc.amplitude );
    lFO3.patch( sineOsc.frequencyModulation );
    lFO4.patch( sineOsc.amplitudeModulation );
    
    // Patch the oscillator into the multiplyGate and then to the out.
    // The connection to the out here keeps the oscillators running even
    // when the note is off.  This produces an interesting "gated" effect
    // when using the mouse to turn the note on and off. 
    sineOsc.patch( multiplyGate ).patch( out );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // turn on the multiply
    multiplyGate.setValue( 0.5 );
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    // turn off the multiply
    multiplyGate.setValue( 0 );
  }
}
