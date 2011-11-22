// this CrushingInstrument will play a sine wave and then change the bit resulution of the BitCrush
// over time, based on a starting and ending resolution passed in.
class CrushingInstrument implements Instrument
{
  Oscil sineOsc;
  BitCrush bitCrush;
  Line crushLine;
  AudioOutput out;
  
  CrushingInstrument(float frequency, float amplitude, float hiBitRes, float loBitRes, AudioOutput output)
  {
    out = output;
    sineOsc = new Oscil(frequency, amplitude, Waves.SINE);
    bitCrush = new BitCrush(hiBitRes);
    crushLine = new Line(9.0, hiBitRes, loBitRes);
    
    // our Line will control the resolution of the bit crush
    crushLine.patch(bitCrush.bitRes);
    // patch the osc through the bit crush
    sineOsc.patch(bitCrush);
  }
  
  // called by the note manager when this instrument should play
  void noteOn(float dur)
  {
    // patch the bit crush to the output and active our Line when we want to have the note play
    crushLine.activate();
    bitCrush.patch(out);
  }
  
  // called by the note manager when this instrument should stop playing
  void noteOff()
  {
    // unpatch from the output to stop making sound
    bitCrush.unpatch(out);
  }
}
