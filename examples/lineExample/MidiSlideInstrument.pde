/* MidiSlideInstrument
   uses an Oscil to make a square wave.  However, a Line is also
   used to slide the frequency of the Oscil from a starting frequency
   to an ending frequency.  Midi2Hz is used to make that slide musical.
*/  
   
// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class MidiSlideInstrument implements Instrument
{
  // create all variables that must be used throughout the class
  Oscil tone;
  Line  freqControl;
  Midi2Hz midi2Hz;
  ADSR adsrGate;
  AudioOutput out;

  // constructor for this instrument  
  MidiSlideInstrument(float begNote, float endNote, float amp, AudioOutput output)
  {
    // create all variables that must be used throughout the class
    out = output;
    
    // create new instances of any UGen objects as necessary
    tone = new Oscil( begNote, amp, Waves.SQUARE );
    adsrGate = new ADSR( 1.0, 0.001, 0.0, 1.0, 0.001 );
    freqControl = new Line( 1.0, begNote, endNote );
    midi2Hz = new Midi2Hz();
    
    // patch everything together up to the final output
    // Here, the line is patched through the midi2Hz UGen into the tone frequency
    freqControl.patch( midi2Hz ).patch( tone.frequency );
    // and the tone is patched into an ADSR
    tone.patch( adsrGate );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // when the note is turned on, the line has to be told how long it will last
    freqControl.setLineTime( dur );
    // and be told to start now
    freqControl.activate();
    // the ADSR has to be patched tho the output
    adsrGate.patch( out );
    // and be told to start also
    adsrGate.noteOn();
  }
 
  // every instrument must have a noteOff() method 
  void noteOff()
  {
    // when the note ends, the ADSR has to be told to start the release
    adsrGate.noteOff();
    // and then after the release, the output should be unpatched
    adsrGate.unpatchAfterRelease( out );
  }
}
