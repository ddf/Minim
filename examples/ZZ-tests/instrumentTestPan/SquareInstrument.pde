class SquareInstrument implements Instrument
{
  Oscil Osc1, LFO,LFOfreqMod;
  Constant LFOfreq;
  Summer   LFOSum;
  Multiplier  multiply;
  AudioOutput out;
  Pan pan;
  
  SquareInstrument(float frequency, float amplitude, AudioOutput output)
  {
    out = output;
    Osc1 = new Oscil(frequency, amplitude,Waves.SINE);
    
    LFO = new Oscil(1, 1,Waves.SINE);
    LFOfreqMod = new Oscil(0.01, 20,Waves.SINE);
    LFOfreq = new Constant(1);
    LFOSum = new Summer();

    pan = new Pan(1);
    multiply = new Multiplier(0);
    LFO.patch(pan.balance);
   
    LFOfreq.patch( LFOSum );
    LFOfreqMod.patch( LFOSum );
    LFOSum.patch( LFO.frequency );

    Osc1.patch(multiply).patch(pan).patch(out);
  }
 
  void noteOn(float dur)
  {
    //println("Note on!");
    multiply.setValue(1);
  }
  
  void noteOff()
  {
    //println("Note off!");
    multiply.setValue(0);
  }
}
