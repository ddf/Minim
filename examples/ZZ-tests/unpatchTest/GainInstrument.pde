class MultiplierInstrument implements Instrument
{
  Oscil sineOsc, lFOOsc;
  Multiplier  multiplyGate;
  AudioOutput out;
  
  MultiplierInstrument(float frequency, float amplitude, AudioOutput output)
  {
    out = output;
    sineOsc = new Oscil(frequency, amplitude, Waves.SINE);
    multiplyGate = new Multiplier(0);
    sineOsc.patch(multiplyGate);
  }
  
  void noteOn(float dur)
  {
    println("Instron!");
    multiplyGate.setValue(1.0);
    multiplyGate.patch(out);
  }
  
  void noteOff()
  {
    println("Instroff!");
    multiplyGate.setValue(0);
    multiplyGate.unpatch( out );
  }
}
