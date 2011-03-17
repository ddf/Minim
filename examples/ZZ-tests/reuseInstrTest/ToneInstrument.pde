class ToneInstrument implements Instrument
{
  Oscil sineOsc, lFOOsc;
  Multiplier  multiplyGate;
  AudioOutput out;
  
  ToneInstrument(String note, float amplitude, AudioOutput output)
  {
    out = output;
    sineOsc = new Oscil( Frequency.ofPitch( note ), amplitude, Waves.TRIANGLE );
    multiplyGate = new Multiplier(0);
    println("Instrument: about to patch");
    sineOsc.patch(multiplyGate);
    println("Instrument: patched");
  }
  
  void noteOn(float dur)
  {
    println("Instron!");
    //sineOsc.setFrequency( 123.45 );
    multiplyGate.setValue(1.0);
    multiplyGate.patch(out);
  }
  
  void noteOff()
  {
    println("Instroff!");
    multiplyGate.setValue(0);
    
  }
  
  ToneInstrument setNote( String note )
  {
    sineOsc.setFrequency( Frequency.ofPitch( note ) );
    return this;
  }
}
