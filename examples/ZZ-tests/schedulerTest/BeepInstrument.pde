class BeepInstrument implements Instrument
{
  Line fade;
  Oscil mSine;
  AudioOutput mOut;
  
  BeepInstrument( float freq, AudioOutput out )
  {
    mOut = out;
    mSine = new Oscil(freq, 0.2, Waves.SINE);
    fade = new Line( 0.1, 0.2, 0 );
    fade.patch(mSine.amplitude);
  }
  
  void noteOn( float dur )
  {
    //println("Note On!");
    fade.activate();
    mSine.patch(out);
  }
  
  void noteOff()
  {
    //println("Note off!");
    mSine.unpatch(out);
  }
}
