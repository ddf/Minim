/**
  * This sketch demonstrates the inverse FFT.<br />
  * The inverse FFT is how you turn frequency domain information into time-domain information.
  * Time-domain information is what you have in a sample buffer and is what can be sent to 
  * the system for playback.
  * <p>
  * All this sketch does is allow you to take the forward FFT of a static buffer and then
  * perform the inverse FFT on the resulting spectrum. You should notice no change in the waveform
  * when you do this. Note however, that if you perform the inverse FFT twice in a row, the waveform
  * will change. This is because the inverse FFT is performed in-place inside of the FFT object.
  * So when you take the inverse the first time, the internal frequency spectrum is transformed into the waveform. 
  * Taking the inverse of *that* is the same thing as taking the forward FFT of the waveform,
  * such is the nature of the FFT. What you wind up with in your buffer is the Real part of the frequency spectrum.
  * <p>
  * Press 'f' to take the forward FFT, and 'd' to take the inverse.
  *
  * <p>
  * For more information about Minim and additional features, visit http://code.compartmental.net/minim/
  */

import ddf.minim.analysis.*;
import ddf.minim.*;
import ddf.minim.signals.*;

FFT fft;
SineWave sine;
float[] buffer;
int bsize = 512;

void setup()
{
  size(512, 200, P3D);

  // create an FFT with a time-domain size the same as the size of buffer
  // it is required that these two values be the same
  // and also that the value is a power of two
  fft = new FFT(bsize, 44100);
  // TODO: use the Mimin Sine class
  sine = new SineWave(600, 1, 44100);
  buffer = new float[bsize];
  // fill the buffer with a sine wave
  sine.generate(buffer);
}

void draw()
{
  background(0);
  noStroke();
  fill(255, 128);
  // draw the waveform
  for(int i = 0; i < buffer.length; i++)
  {
    ellipse(i, 50 + buffer[i]*10, 2, 2);
  }
  noFill();
  stroke(255);
  // draw the spectrum
  for(int i = 0; i < fft.specSize(); i++)
  {
    line(i, height, i, height - fft.getBand(i));
  }
  stroke(255, 0, 0);
  line(width/2, height, width/2, 0);
}


void keyReleased()
{
  if ( key == 'f' ) 
  {
    println("Performing a Forward FFT on buffer.");
    fft.forward(buffer);
  }
  if ( key == 'd' ) 
  {
    println("Performing an Inverse FFT and putting the result in buffer.");
    fft.inverse(buffer);
  }
}
