class SquareInstrument implements Instrument
{
  Oscil Osc;
  Gain  gain;
  AudioOutput out;
Echo del;
Line delamp;
  
  SquareInstrument(float frequency, float amplitude, AudioOutput output)
  {
    out = output;
    Osc = new Oscil(frequency, amplitude,Waves.randomNoise());
    gain = new Gain(0);
    
    /**
    nb : 
    note : it all happens when you click
    i recommand short clicks to hear the echoes
    try the different 
    del = ... 
    lines.
    
    */
    
    
    
  // del = new Delay(200,20,Delay.LIN,1);
   //del = new Delay(400,5, Delay.EXP,1);
   del = new Echo(200,new float[] {1, 0, 1, 1 , 0 , 1,1,1},1); 
   
   
   
   
   
 // delamp = new Line(0.2,0,1);
   
// delamp.patch(del.amplitude);
   
    Osc.patch(gain).patch(del).patch(out);
  }
 
  void noteOn(float dur)
  {
    //println("Note on!");
    gain.setValue(1);
  // delamp.activate();
  }
  
  void noteOff()
  {
    //println("Note off!");
    gain.setValue(0);
  }
}
