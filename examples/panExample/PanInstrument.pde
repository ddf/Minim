// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class PanInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Oscil sineOsc, LFO;
  Pan pan;
  AudioOutput out;
  
  // constructors for this intsrument
  PanInstrument( float oscFrequency, float oscAmplitude, float lfoFrequency, float lfoAmplitude, AudioOutput output )
  {
    // equate class variables to constructor variables as necessary 
    out = output;
    
    // create new instances of any UGen objects as necessary
    sineOsc = new Oscil( oscFrequency, oscAmplitude, Waves.SINE );
    // the arguments to the Pan UGen are for the balance and width.
    // balance ranges from -1 to 1, which basically are hard-left and 
    // hard-right, respectively.
    // we create our pan with 0 because we will drive the value of 
    // the balance using Pan's balance UGenInput.
    pan = new Pan(0);
    // LFO stands for low frequency oscillator. we will use this to control
    // the balance input of the Pan Ugen.
    LFO = new Oscil( lfoFrequency, lfoAmplitude, Waves.SINE );
        
    // patch everything together up to the final output
    sineOsc.patch( pan );
    LFO.patch( pan.pan );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // and patch to the output
    pan.patch( out );
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    // and unpatch the output 
    // this causes the entire instrument to stop calculating sampleframes
    // which is good when the instrument is no longer generating sound.
    pan.unpatch( out );
  }
}
