/**
  * This sketch demonstrates how to use the averaging abilities of the FFT. <br />
  * Logarithmically spaced averages (i.e. averages that are grouped by octave) are requested 
  * by specifying the band width of the smallest octave and how many bands to split each 
  * octave into. The result is averages that more closely map to how humans perceive sound.
  * <p>
  * For more information about Minim and additional features, visit http://code.compartmental.net/minim/
  */

import ddf.minim.analysis.*;
import ddf.minim.*;

Minim minim;
AudioPlayer jingle;
FFT fft;

void setup()
{
  size(512, 200, P3D);

  minim = new Minim(this);
  jingle = minim.loadFile("jingle.mp3", 2048);
  // loop the file
  jingle.loop();
  // create an FFT object that has a time-domain buffer the same size as jingle's sample buffer
  // note that this needs to be a power of two 
  // and that it means the size of the spectrum will be 1024. 
  // see the online tutorial for more info.
  fft = new FFT(jingle.bufferSize(), jingle.sampleRate());
  // calculate averages based on a miminum octave width of 22 Hz
  // split each octave into three bands
  fft.logAverages(22, 3);
  rectMode(CORNERS);
}

void draw()
{
  background(0);
  fill(255);
  // perform a forward FFT on the samples in jingle's mix buffer
  // note that if jingle were a MONO file, this would be the same as using jingle.left or jingle.right
  fft.forward(jingle.mix);
  // avgWidth() returns the number of frequency bands each average represents
  // we'll use it as the width of our rectangles
  int w = int(width/fft.avgSize());
  for(int i = 0; i < fft.avgSize(); i++)
  {
    // draw a rectangle for each average, multiply the value by 5 so we can see it better
    rect(i*w, height, i*w + w, height - fft.getAvg(i)*5);
  }
}
