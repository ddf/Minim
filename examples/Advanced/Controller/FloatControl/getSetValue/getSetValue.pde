/**
  * This sketch demonstrates how to use the <code>getValue</code> and <code>setValue</code> methods of a 
  * <code>FloatControl</code> object. A <code>FloatControl</code> is what is returned by the <code>gain</code>, 
  * <code>volume</code>, <code>pan</code>, and <code>balance</code> methods of a <code>Controller</code> object.
  * The class used here is an <code>AudioOutput</code> but these control methods are also available on 
  * <code>AudioSample</code>, <code>AudioSnippet</code>, <code>AudioInput</code>, and <code>AudioPlayer</code> objects. 
  * The <code>FloatControl</code> class is defined by the JavaSound API and it 
  * represents a control of a <code>DataLine</code>. A <code>DataLine</code> is a low-level JavaSound class that 
  * is used for sending audio to, or receiving audio from, the audio system. <code>getValue</code> and <code>setValue</code> 
  * allow you to get and set the value of the control. The default implementation simply sets the value as indicated. 
  * If the value indicated is greater than the maximum value, or smaller than the minimum value, 
  * an <code>IllegalArgumentException</code> is thrown. You can avoid having to possibly deal with this exception by using 
  * the appropriate set method of <code>Controller</code>, which will keep the value in the allowed range.
  * <p>
  * Move the mouse left and right to change the pan value.
  */

import ddf.minim.*;
import ddf.minim.signals.*;

Minim minim;
AudioOutput out;
Oscillator  osc;
WaveformRenderer waveform;

void setup()
{
  size(512, 200);
  minim = new Minim(this);
  out = minim.getLineOut();
  
  // see the example AudioOutput >> SawWaveSignal for more about this class
  osc = new SawWave(100, 0.2, out.sampleRate());
  // see the example Polyphonic >> addSignal for more about this
  out.addSignal(osc);
  
  waveform = new WaveformRenderer();
  // see the example Recordable >> addListener for more about this
  out.addListener(waveform); 
  
  textFont(createFont("Arial", 12));
}

void draw()
{
  background(0);
  // see waveform.pde for more about this
  waveform.draw();
  
  if ( out.hasControl(Controller.PAN) )
  {
    // map the mouse position to the range of the pan
    float val = map(mouseX, 0, width, out.pan().getMinimum(), out.pan().getMaximum());
    out.pan().setValue(val);
    text("The current pan is " + out.pan().getValue() + ".", 5, 15);
  }
  else
  {
    text("There is no pan control for this output.", 5, 15);
  }
}
