/**
  * This sketch demonstrates how to use a HighPassSP filter.<br />
  * Move the mouse to the right to increase the cutoff frequency and to the left to decrease it.
  * <p>
  * For more information about Minim and additional features, visit http://code.compartmental.net/minim/
  */

import ddf.minim.*;
import ddf.minim.effects.*;

Minim minim;
AudioPlayer groove;
HighPassSP hpf;

void setup()
{
  size(512, 200, P3D);
  minim = new Minim(this);
  groove = minim.loadFile("groove.mp3");
  groove.loop();
  // make a low pass filter with a cutoff frequency of 100 Hz
  // the second argument is the sample rate of the audio that will be filtered
  // it is required to correctly compute values used by the filter
  hpf = new HighPassSP(1000, groove.sampleRate());
  groove.addEffect(hpf);
}

void draw()
{
  background(0);
  stroke(255);
  // draw the waveforms
  // the values returned by left.get() and right.get() will be between -1 and 1,
  // so we need to scale them up to see the waveform
  for(int i = 0; i < groove.bufferSize() - 1; i++)
  {
    float x1 = map(i, 0, groove.bufferSize(), 0, width);
    float x2 = map(i+1, 0, groove.bufferSize(), 0, width);
    line(x1, height/4 - groove.left.get(i)*50, x2, height/4 - groove.left.get(i+1)*50);
    line(x1, 3*height/4 - groove.right.get(i)*50, x2, 3*height/4 - groove.right.get(i+1)*50);
  }
}

void mouseMoved()
{
  // map the mouse position to the range [1000, 14000], an arbitrary range of cutoff frequencies
  float cutoff = map(mouseX, 0, width, 1000, 14000);
  hpf.setFreq(cutoff);
}

