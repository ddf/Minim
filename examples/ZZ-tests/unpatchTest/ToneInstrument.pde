class ToneInstrument implements Instrument
{
  Oscil sineOsc, lFOOsc;
  ADSR  adsr;
  AudioOutput out;
  
  ToneInstrument(String note, float amplitude, AudioOutput output)
  {
    out = output;
    float frequency = Frequency.ofPitch( note ).asHz();

    sineOsc = new Oscil(frequency, amplitude, Waves.TRIANGLE);
    adsr = new ADSR( 1.0, 0.01, 0.01, 1.0, 0.02 );

    sineOsc.patch( adsr );
  }
  
  void noteOn(float dur)
  {
    adsr.patch( out );
    adsr.noteOn();
  }
  
  void noteOff()
  {
    adsr.noteOff();
    adsr.unpatchAfterRelease( out );
  }
}
