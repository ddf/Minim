/**
  * This sketch demonstrates how to use the notch, or band reject, filter that comes with Minim.<br />
  * Move mouse left and right change the center frequency of the band to reject, <br />
  * move it up and down to change the band width of the reject band.
  * <p>
  * For more information about Minim and additional features, visit http://code.compartmental.net/minim/ 
  */

import ddf.minim.*;
import ddf.minim.effects.*;

Minim minim;
AudioPlayer groove;
NotchFilter ntf;

void setup()
{
  size(512, 200, P3D);
  minim = new Minim(this);
  groove = minim.loadFile("groove.mp3");
  groove.loop();
  // make a low pass filter with a cutoff frequency of 100 Hz
  // the second argument is the sample rate of the audio that will be filtered
  // it is required to correctly compute values used by the filter
  ntf = new NotchFilter(440, 20, groove.sampleRate());
  groove.addEffect(ntf);
}

void draw()
{
  background(0);
  stroke(255);
  // we multiply the values returned by get by 50 so we can see the waveform
  for ( int i = 0; i < groove.bufferSize() - 1; i++ )
  {
    float x1 = map(i, 0, groove.bufferSize(), 0, width);
    float x2 = map(i+1, 0, groove.bufferSize(), 0, width);
    line(x1, height/4 - groove.left.get(i)*50, x2, height/4 - groove.left.get(i+1)*50);
    line(x1, 3*height/4 - groove.right.get(i)*50, x2, 3*height/4 - groove.right.get(i+1)*50);
  }
  // draw a rectangle to represent the reject band
  noStroke();
  fill(255, 0, 0, 60);
  rect(mouseX - ntf.getBandWidth()/20, 0, ntf.getBandWidth()/10, height);
}

void mouseMoved()
{
  // map mouseX to the range [100, 10000], an arbitrary range of band reject frequencies
  float notchBand = map(mouseX, 0, width, 100, 2000);
  ntf.setFreq(notchBand);
  // map mouseY to the range [50, 500], and arbitrary range of band widths
  float bandWidth = map(mouseY, 0, height, 50, 500);
  ntf.setBandWidth(bandWidth);
  ntf.printCoeff();
}
