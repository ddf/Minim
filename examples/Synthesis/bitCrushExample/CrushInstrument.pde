// this CrushInstrument will play a sine wave bit crushed
// to a certain bit resolution. this results in the audio sounding
// "crunchier".
class CrushInstrument implements Instrument
{
  Oscil sineOsc;
  BitCrush bitCrush;
  AudioOutput out;
  
  CrushInstrument(float frequency, float amplitude, float bitRes, AudioOutput output)
  {
    out = output;
    sineOsc = new Oscil(frequency, amplitude, Waves.SINE);
    
    // BitCrush takes the bit resolution for an argument
    bitCrush = new BitCrush(bitRes, output.sampleRate());
    
    sineOsc.patch(bitCrush);
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn(float dur)
  {
    bitCrush.patch(out);
  }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    bitCrush.unpatch(out);
  }
}
