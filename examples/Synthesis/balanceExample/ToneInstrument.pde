// Every instrument must implement the Instrument interface so 
// playNote() can call the instrument's methods.
class ToneInstrument implements Instrument
{
  // declare our oscillators. sineOsc is used for the sounding tone
  // and lFOOsc is used to control the value of Balance
  Oscil sineOsc, lFOOsc;
  Balance balance;
  AudioOutput out;
  
  ToneInstrument(float frequency, float amplitude, float lfoFrequency, float lfoAmplitude, AudioOutput output)
  {
    out = output;
    sineOsc = new Oscil(frequency, amplitude, Waves.SINE);
    lFOOsc = new Oscil(lfoFrequency, lfoAmplitude, Waves.SINE);
    // Balance takes the value of the Balance as an argument.
    // 0 would result in no change in the signal fed into it
    // negative values will attenuate the left channel and
    // positive values will attenuate the right channel
    balance = new Balance( 0.5 );
    // patch our LFO to the balance control of Balance
    lFOOsc.patch( balance.balance );
    
    // patch our oscillator to the balance and into the damp
    sineOsc.patch( balance );
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn(float dur)
  {
    // to start sounding we simply patch our balance to output
    // this is better than simply turning the volume up because 
    // it means we don't actually have to do any processing until
    // we are meant to be heard.
    balance.patch(out);
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    balance.unpatch(out);
  }
}
