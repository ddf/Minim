/**
  * This sketch demonstrates the difference between linearly spaced averages and
  * logarithmically spaced averages.
  * <p>
  * From top to bottom:<br/>
  * - The full spectrum.<br/>
  * - The spectrum grouped into 30 linearly spaced averages.<br/>
  * - The spectrum grouped logarithmically into 10 octaves, each split into 3 bands.
  * <p>
  * For more information about Minim and additional features, 
  * visit http://code.compartmental.net/minim/
  */
 
import ddf.minim.analysis.*;
import ddf.minim.*;
import ddf.minim.ugens.*;
 
Minim minim;

// audio signal
AudioOutput out;
Noise       noize;
MoogFilter  rezFilter;
Oscil       lfo;

// FFT objects for displaying
// the two different average spectrums
FFT fftLin;
FFT fftLog;

float height3;
float height23;
 
void setup()
{
  size(512, 600, P3D);
  height3 = height/3;
  height23 = 2*height/3;
 
  minim = new Minim(this);
  
  // create our audio signal
  {
    out = minim.getLineOut();
    noize = new Noise( 0.8f, Noise.Tint.WHITE );
    rezFilter = new MoogFilter( 100.f, 0.5f );
    // sweep the cutoff of the filter over an 800 hz range
    lfo = new Oscil( 0.5f, 2000.f, Waves.SINE );
    // centered on 500 Hz
    lfo.offset.setLastValue( 2060 );
    lfo.patch( rezFilter.frequency );
    noize.patch( rezFilter ).patch( out );
  }

  // create the FFT objects
  {
    fftLin = new FFT(out.bufferSize(), out.sampleRate());
    // calculate the averages by grouping frequency bands linearly. use 30 averages.
    fftLin.linAverages(30);

    fftLog = new FFT(out.bufferSize(), out.sampleRate());
    // calculate averages based on a miminum octave width of 22 Hz
    // split each octave into three bands
    // this should result in 30 averages
    fftLog.logAverages(22, 3);
  }

  rectMode(CORNERS);
}
 
void draw()
{
  background(0);

  // analyze
  {
    fftLin.forward(out.mix);
    fftLog.forward(out.mix);
  }
  
  // draw the full spectrum
  {    
    stroke(255);
    noFill();
    for(int i = 0; i < fftLin.specSize(); i++)
    {
      // get the value of the ith frequency band in the spectrum.
      float iBandVal = fftLin.getBand(i);
      line(i, height3, i, height3 - iBandVal*10);
    }
  }
 
  // draw the linear averages
  {
    noStroke();
    fill(255);
    // avgSize returns the number of averages current being calculated
    int w = int(width/fftLin.avgSize());
    for(int i = 0; i < fftLin.avgSize(); i++)
    {
      // get the value of the ith average band in the spectrum.
      float iBandVal = fftLin.getAvg(i);
      // draw a rectangle for each average.
      rect(i*w, height23, i*w + w, height23 - iBandVal*10);
    }
  }
 
  // draw the logarithmic averages
  {
    int w = int(width/fftLog.avgSize());
    for(int i = 0; i < fftLog.avgSize(); i++)
    {
      // get the value of the ith average band in the spectrum.
      float iBandVal = fftLog.getAvg(i);
      // draw a rectangle for each average.
      rect(i*w, height, i*w + w, height - iBandVal*10);
    }
  }
}
