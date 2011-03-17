class ModInstrument implements Instrument
{
  Oscil wave;
  Oscil mod;
  Multiplier amp;
  
  ModInstrument( Wavetable wavetable )
  {
    wave = new Oscil( 220.f, 0.5, Waves.SINE );
    mod = new Oscil( 1.f, 1.f, wavetable );
    amp = new Multiplier(1.f);
    
    mod.patch( wave.amplitude );
    wave.patch( amp );
  }
  
  void noteOn( float duration )
  {
    amp.patch(out);
  }
  
  void noteOff()
  {
    amp.unpatch(out);
  }
}
