class SineInstrument implements Instrument
{
  Oscil sineOsc;
  Multiplier  multiply;
  ADSR  adsr;
  AudioOutput out;
  
  SineInstrument(float frequency, float amplitude, AudioOutput output)
  {
    out = output;
    sineOsc = new Oscil(frequency, amplitude, Waves.TRIANGLE);
    multiply = new Multiplier(0);
    adsr = new ADSR(0.5, 0.01, 0.05, 0.5, 0.5);
    sineOsc.patch(adsr).patch(out);
  }
  
  void noteOn(float dur)
  {
    //println("Note on!");
    multiply.setValue(1);
    adsr.noteOn();
  }
  
  void noteOff()
  {
    //println("Note off!");
    multiply.setValue(0);
    adsr.noteOff();
  }
}
