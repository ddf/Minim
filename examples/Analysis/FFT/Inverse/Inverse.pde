/**
 * This sketch demonstrates very simply how you might use the inverse FFT to generate an audio signal.<br />
 * Click in the window or drag the mouse around to modify the spectrum in realtime. Press the 'c' key to reset it.
 * <br />
 * You might wonder what the actual frequencies added to the spectrum are.
 * Frequencies are a fraction of the sampling rate, which can be found with the formula <b>f = i/N</b>
 * where <b>f</b> is the fraction of the sampling rate, <b>i</b> is the index of the frequency band,
 * and <b>N</b> is the time-domain size of the FFT. In this case we have a 2048 point FFT and we are
 * changing the frequency bands between 1 and 512. So for example, if you modify the band at x = 20,
 * then <b>f = 20/2048 = 0.009765625</b>. Since our sampling rate is 44100 Hz,
 * the frequency in Hz that is being added to the spectrum is <b>44100 * 0.0390625 = 430.664 Hz</b>
 *
 * <p>
 * For more information about Minim and additional features, visit http://code.compartmental.net/minim/
 */
 
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;

Minim minim;
AudioOutput mainOut;
// a custom UGen used to generate realtime audio from an FFT (defined at the bottom)
SpectralGen spectralGen;
 
void setup()
{
  size(512, 300);

  minim = new Minim(this);
  mainOut = minim.getLineOut();
  // the spectrum size *must* be a power of two!
  // since we want to use the full width of the window to manipulate the spectrum,
  // we quadruple it in this constructor. this is because we manipulating a spectrum
  // for synthesis, we can only use the bottom half, but even above the bottom quarter,
  // single bands are basically inaudible.
  spectralGen = new SpectralGen( width*4 );

  spectralGen.patch( mainOut );
}
 
void draw()
{
  background(0);
  noStroke();
  fill(255, 128);
  
  // draw the waveform
  for(int i = 0; i < mainOut.mix.size(); i++)
  {
    ellipse(i, 50 + mainOut.mix.get(i)*30, 2, 2);
  }
  
  // draw the spectrum
  noFill();
  stroke(255);
  for(int i = 0; i < width; i++)
  {
    line(i, height, i, height - spectralGen.getBand(i));
  }
}

void keyPressed()
{
  if ( key == 'c' )
  {
    for(int i = 0; i < spectralGen.size(); ++i )
    {
      spectralGen.setBand(i,0);
    }
  }
}

void mousePressed()
{
  mouseDragged();
}
 
void mouseDragged()
{
  float magnitude = height - mouseY;
  int   band = mouseX + 1;
  if ( band > 0 && band < spectralGen.size() )
  {
    spectralGen.setBand( band, magnitude );
  }
}

class SpectralGen extends UGen
{
  FFT     fft;
  
  int     timeSize;
  // how big the "overlap" window is.
  int     windowSize;
  int     outIndex;
  // a ring buffer we write into every time we generate a new buffer of audio from the fft
  float[] output;
  // the buffer we pass to fft.inverse to generate a new buffer of audio
  float[] inverse;
  // we have to reconfigure the fft before each inverse call, since the inverse call modifies the interal data
  // so have a buffer of silence we pass into forward and our own array of spectral amplitudes
  float[] silence;
  float[] amplitudes;
  
  public SpectralGen( int specSize )
  {
    fft      = new FFT(specSize, mainOut.sampleRate());
    timeSize = specSize;
    windowSize = specSize/2;
    output = new float[specSize];
    inverse  = new float[specSize];
    amplitudes = new float[specSize/2];
    silence = new float[specSize];
    outIndex = output.length;
  }
  
  public int size()
  {
    return amplitudes.length;
  }
  
  public void setBand( int b, float amp )
  {
    amplitudes[b] = amp;
  }
  
  public float getBand( int b )
  {
    return amplitudes[b];
  }
  
  protected void uGenerate( float[] out )
  {
     if ( outIndex % windowSize == 0 )
     {
       // reset all the data.
       // if you were modifying audio coming *into* this UGen,
       // you'd want to write input audio into this buffer
       // so that when forward is performed the spectrum
       // will represent the audio that has occurred since
       // the last inverse operation. see the Vocodor 
       // UGen source code for an example of this.
       fft.forward(silence);
       for(int i = 0; i < amplitudes.length; ++i)
       {
         fft.setBand(i, amplitudes[i]*0.1f);
       }
       fft.inverse( inverse );
       fft.HAMMING.apply( inverse );
       
       if ( outIndex == output.length )
       {
         outIndex = 0;
       } 
       
       for( int s = 0; s < timeSize; ++s )
       {
         int o = (s + outIndex) % timeSize;
         output[o] += inverse[s];
       }
     }
     
     for( int i = 0; i < out.length; ++i )
     {
       out[i] = output[outIndex];
     }
     output[outIndex] = 0;
     
     ++outIndex;
  }
}