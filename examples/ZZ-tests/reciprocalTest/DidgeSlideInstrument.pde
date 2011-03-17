/* This instrument simulates a didgeridoo with a moving length.
 * The wavelength (lambda) of the note played by a didgeridoo is approximately
 * four times the length (L) of the didgeridoo.  Freqency (f) is found by
 * from the following formula using the approximate speed of sound in air (c).
 * f = c / lambda
 * This means that as the length of the didgeridoo increases linearly,
 * the frequency of the tone will decrease inversely.  This descent is
 * not linear, abviously. 
*/

class DidgeSlideInstrument implements Instrument
{
  Oscil tone;
  Multiplier  gate;
  Line  lengthLine;
  Constant factor;
  Multiplier multiplier;
  Reciprocal reciprocal;
  AudioOutput out;

  float speedOfSound = 343.0f;
  float waveLengthDivisor = 4.0f;
    
  DidgeSlideInstrument( float amp, float begLength, float endLength, AudioOutput output)
  {
    out = output;
    float begFreq = speedOfSound/(waveLengthDivisor*begLength);
    
    tone = new Oscil( begFreq, amp, Waves.triangle( 4 ) );
    gate = new Multiplier(0);
    factor = new Constant( speedOfSound/waveLengthDivisor );
    lengthLine = new Line( 1.0, begLength, endLength );
    multiplier = new Multiplier();
    reciprocal = new Reciprocal();
    
    factor.patch( multiplier.amplitude );
    lengthLine.patch( reciprocal.denominator ).patch( multiplier );
    multiplier.patch( tone.frequency );
    tone.patch(gate).patch(out);
  }
  
  void noteOn(float dur)
  {
    gate.setValue(1);
    lengthLine.setLineTime( dur );
    lengthLine.activate();
  }
  
  void noteOff()
  {
    //println("Note off!");
    gate.setValue(0);
  }
}
