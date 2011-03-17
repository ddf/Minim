class SquareInstrument implements Instrument
{
  Oscil Osc;
  Multiplier  multiply;
  AudioOutput out;
WaveShaper disto;
Line distchange;
  
  SquareInstrument(float frequency, float amplitude, AudioOutput output)
  {
    out = output;
    Osc = new Oscil(frequency, amplitude,Waves.SINE);
    multiply = new Multiplier(0);
    
disto = new WaveShaper(WaveShaper.TruncSine(), 1);

 distchange = new Line(5,1,8);
   
distchange.patch(disto.amount);
   
    Osc.patch(disto).patch(multiply).patch(out);
  }
 
  void noteOn(float dur)
  {
    //println("Note on!");
    multiply.setValue(1);
   distchange.activate();
  }
  
  void noteOff()
  {
    //println("Note off!");
    multiply.setValue(0);
  }
}
