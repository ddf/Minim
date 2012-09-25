/**
  * This sketch demonstrates how to use the <code>createSample</code> method of <code>Minim</code>. 
  * The <code>createSample</code> method allows you to create an <code>AudioSample</code> by provided 
  * either one or two float arrays, which are the sound you want be able to trigger. 
  * <p>
  * See the loadSample example for more information about <code>AudioSample</code>s.
  * <p>
  * Press 't' to trigger the sample.
  * <p>
  * For more information about Minim and additional features, visit http://code.compartmental.net/minim/
  */

import ddf.minim.*;
import ddf.minim.signals.*;
// we must import this package to create an AudioFormat object
import javax.sound.sampled.*;

Minim minim;
AudioSample wave;
// we'll use a sine wave signal to generate a buffer of floats
// that will be used to create an AudioSample.
SineWave sine;

void setup()
{
  size(512, 200, P3D);
  // always start Minim before you do anything with it
  minim = new Minim(this);
  
  // make a sine wave!
  sine = new SineWave( 220,  // frequency in Hz
                       0.5,  // amplitude
                       44100 // sample rate
                     );
  
  // we'll make a MONO sample, but there is also a version
  // of createSample that you can pass two float arrays to:
  // which will be used for the left and right channels
  // of a stereo sample.
  float[] samples = new float[1024*8];
  
  // generate some audio. there will be a click at the end
  // because we aren't fading it out or anything.
  sine.generate(samples);
  
  // when we create a sample we need to provide an AudioFormat so 
  // the sound will be played back correctly.
  AudioFormat format = new AudioFormat( 44100, // sample rate
                                        16,    // sample size in bits
                                        1,     // channels
                                        true,  // signed
                                        true   // bigEndian
                                      );
                                      
  // finally, create the AudioSample
  wave = minim.createSample( samples, // the samples
                             format,  // the format
                             1024     // the output buffer size
                            );
}

void draw()
{
  background(0);
  stroke(255);
  // use the mix buffer to draw the waveforms.
  for (int i = 0; i < wave.bufferSize() - 1; i++)
  {
    line(i, 100 - wave.left.get(i)*50, i+1, 100 - wave.left.get(i+1)*50);
  }
}

void keyPressed()
{
  if ( key == 't' ) 
  {
    wave.trigger();
  }
}

