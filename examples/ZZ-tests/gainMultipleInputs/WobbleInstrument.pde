class WobbleInstrument implements Instrument
{
  Oscil sineOsc, lFOOsc;
  Multiplier  multiplyGate, multiplyLFO;
  AudioOutput out;
  
  WobbleInstrument(float frequency, float amplitude, float lFOFreq, AudioOutput output)
  {
    out = output;
    sineOsc = new Oscil(frequency, amplitude, Waves.SINE);
    lFOOsc = new Oscil(lFOFreq/2.0, 1.0, Waves.SINE);
    multiplyGate = new Multiplier(0);
    multiplyLFO = new Multiplier(1);
    //multiplyLFO.setSampleRate(out.sampleRate());
    println("WobbleInstrument: about to patch");
    //sineOsc.patch(multiply.audio).patch(out);
    //multiplyLFO.printInputs();
    lFOOsc.patch(multiplyLFO.amplitude);
    //sineOsc.patch(multiplyGate.audio).patch(multiplyLFO.audio).patch(out);
    //lFOOsc.patch(sineOsc.frequencyModulation);
    sineOsc.patch(multiplyGate).patch(multiplyLFO).patch(out);
    //multiplyLFO.printInputs();
    //lFOOsc.patch(multiply.amplitude);
    //lFOOsc.patch(multiply.audio).patch(out);
    println("WobbleInstrument: patched");
  }
  
  void noteOn(float dur)
  {
    println("wobble on!");
    multiplyGate.setValue(0.5);
  }
  
  void noteOff()
  {
    println("wobble off!");
    multiplyGate.setValue(0);
  }
}
