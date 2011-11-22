// this instrument uses a WaveShaper to shape an Oscil
// over time.
class WaveShaperInstrument implements Instrument
{
  // our tone
  Oscil sineOsc;
  // what we'll shape our oscil with
  WaveShaper shaper;
  // a line to change the amount of shaping over time
  Line shaperAmountLine;
  // and a reciprocal to change the output amplitude over time
  Reciprocal reciprocal;
  
  AudioOutput out;

  WaveShaperInstrument(float frequency, float amplitude, AudioOutput output)
  {
    out = output;
    sineOsc = new Oscil(frequency, amplitude, Waves.SINE);
    // We've created three different waves to shape the sine with.  Just uncomment
    // one of the "shaper =" lines to hear the different waves.
    // The first is a modified saw wave.  We made this while we were experimenting
    // with the WaveShaper and liked it, so it remains.
    Wavetable shapeA = new Wavetable( Waves.SAW );
    shapeA.set(0, -1.0);
    shapeA.set(shapeA.size()-1, 1.0);
    // The second argument in WaveShaper
    // is the amount of shaping to be applied, which in our case doesn't 
    // really matter because we are going to drive that with a Line.
    shaper = new WaveShaper(amplitude, 5, shapeA);
    
    // If we want to shape the sine with a saw wave... 
    //shaper = new WaveShaper( amplitude, 5, Waves.SAW );
    
    // We can choose to wrap around the ends of the waveshaping map for interesting
    // effects, and one does this by setting the fourth argument to true.
    //shaper = new WaveShaper( amplitude, 5, Waves.SAW, true );
    
    shaperAmountLine = new Line(5.f, 1.f, 25.f);
    reciprocal = new Reciprocal();
   
    // patch the line into the mapAmplitude of the WaveShaper
    shaperAmountLine.patch( shaper.mapAmplitude );
    // Patch the reciprocal of the line into the outAmplitude.
    // Since the line goes from 1 to 25, the reciprocal goes from 1/1 to 1/25.
    // This creates a pretty good approximation of a drum envelope.
    shaperAmountLine.patch( reciprocal ).patch( shaper.outAmplitude );
    sineOsc.patch( shaper );
  }
 
  void noteOn(float dur)
  {
    // set our line time based on duration
    shaperAmountLine.setLineTime( dur );
    shaperAmountLine.activate();
    shaper.patch( out );
  }
  
  void noteOff()
  {
    shaper.unpatch( out );
  }
}
