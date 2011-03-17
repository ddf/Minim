// this ChikInstrument will make a "chik" sound
class ChikInstrument implements Instrument
{
  // to make a chik sound we'll just filter some noise
  Noise noize;
  BandPass bpFilt;
  AudioOutput out;
  
  ChikInstrument( AudioOutput output )
  {
    out = output;
    
    // amplitude and noise tint
    noize = new Noise( 1.f, Noise.Tint.WHITE);
    
    // make a bandpass with center frequency of 400Hz, bandwidth of 20Hz and sample rate of 44100.
    bpFilt = new BandPass( 1000.f, 200.f, 44100.f );
    
    // patch the noise through the filter
    noize.patch( bpFilt );
  }
  
  // called by the note manager when this instrument should play
  void noteOn(float dur)
  {
    bpFilt.patch(out);
  }
  
  // called by the note manager when this instrument should stop playing
  void noteOff()
  {
    bpFilt.unpatch(out);
  }
}
