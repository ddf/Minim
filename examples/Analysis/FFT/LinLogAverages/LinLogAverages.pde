/**
  * This sketch demonstrates the difference between linearly spaced averages and logarithmically spaced averages 
  * (averages that are grouped by octave). It also draw the full spectrum so you can see how each set of averages 
  * compares to it.
  * <p>
  * From top to bottom:
  * <ul>
  *  <li>The full spectrum.</li>
  *  <li>The spectrum grouped into 30 linearly spaced averages.</li>
  *  <li>The spectrum grouped logarithmically into 10 octaves, each split into 3 bands.</li>
  * </ul>
  * <p>
  * For more information about Minim and additional features, visit http://code.compartmental.net/minim/ 
  */

import ddf.minim.analysis.*;
import ddf.minim.*;

Minim minim;  
AudioPlayer jingle;
FFT fftLin;
FFT fftLog;
float height3;
float height23;
float spectrumScale = 2;

void setup()
{
  size(512, 480, P3D);
  height3 = height/3;
  height23 = 2*height/3;

  minim = new Minim(this);
  jingle = minim.loadFile("jingle.mp3", 1024);
  // loop the file
  jingle.loop();
  // create an FFT object that has a time-domain buffer the same size as jingle's sample buffer
  // note that this needs to be a power of two 
  // and that it means the size of the spectrum will be 1024. 
  // see the online tutorial for more info.
  fftLin = new FFT(jingle.bufferSize(), jingle.sampleRate());
  // calculate the averages by grouping frequency bands linearly. use 30 averages.
  fftLin.linAverages(30);
  fftLog = new FFT(jingle.bufferSize(), jingle.sampleRate());
  // calculate averages based on a miminum octave width of 22 Hz
  // split each octave into three bands
  // this should result in 30 averages
  fftLog.logAverages(22, 3);
  rectMode(CORNERS);
}

void draw()
{
  background(0);
  // perform a forward FFT on the samples in jingle's mix buffer
  // note that if jingle were a MONO file, this would be the same as using jingle.left or jingle.right
  fftLin.forward(jingle.mix);
  
  stroke(255);
  noFill();
  // draw the full spectrum
  for(int i = 0; i < fftLin.specSize(); i++)
  {
    line(i, height3, i, height3 - fftLin.getBand(i)*spectrumScale);
  }
  
  noStroke();
  fill(255);
  // draw the linear averages
  int w = int(width/fftLin.avgSize());
  for(int i = 0; i < fftLin.avgSize(); i++)
  {
    // draw a rectangle for each average, multiply the value by spectrumScale so we can see it better
    rect(i*w, height23, i*w + w, height23 - fftLin.getAvg(i)*spectrumScale);
  }
  
  // draw the logarithmic averages
  fftLog.forward(jingle.mix);
  w = int(width/fftLog.avgSize());
  for(int i = 0; i < fftLog.avgSize(); i++)
  {
    // draw a rectangle for each average, multiply the value by spectrumScale so we can see it better
    rect(i*w, height, i*w + w, height - fftLog.getAvg(i)*spectrumScale);
  }
}
