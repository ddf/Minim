// The GranularInstrument is intended use GranulateRandom to chop up audio
//    generated using a triangle wave
//    into sound grains which are random in length and spaced randomly within the
//    parameters given.  The beginning frequency and the ending frequency of the 
//    tone is set in the constructor of the instrument.

// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class GrabularInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Line freqLine;
  GranulateRandom chopper;
  
  // the constructor for this instrument.  The amplitude and beginning and ending
  // frequency must all be specified.
  GrabularInstrument( float amplitude, float begFreq, float endFreq )
  {
    // create new instances of any UGen objects necessary
    // the tone
    Oscil toneOsc = new Oscil( begFreq, amplitude, Waves.TRIANGLE);
    // a line to specify the frequency of the tone
    freqLine = new Line( 2.6, begFreq, endFreq );
    // the granulation of the tone
    chopper = new GranulateRandom( 0.005, 0.005, 0.001, 0.020, 0.020, 0.002 );

    // patch everything together up until the output
    // the frequency line goes to the toneOsc frequency
    freqLine.patch( toneOsc.frequency );
    // and the tone goes into the chopper
    toneOsc.patch( chopper );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // change the time of the line to the duration minus 1 second
    freqLine.setLineTime( dur - 1.0 );
    // activate the line
    freqLine.activate();
    // and patch the chopper to the output to get the ball rolling
    chopper.patch( out );
  }
 
  // every instrument must have a noteOff() method 
  void noteOff()
  {
    // pull the chopper off the output
    // an ADSR here would be nicer, but this is the dirty way
    chopper.unpatch( out );
  }
}
