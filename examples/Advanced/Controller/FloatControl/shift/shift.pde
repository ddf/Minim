/**
  * This sketch demonstrates how to use the <code>shift</code> method of a 
  * <code>FloatControl</code> object. A <code>FloatControl</code> is what is returned by the <code>gain</code>, 
  * <code>volume</code>, <code>pan</code>, and <code>balance</code> methods of a <code>Controller</code> object.
  * The class used here is an <code>AudioOutput</code> but these control methods are also available on 
  * <code>AudioSample</code>, <code>AudioSnippet</code>, <code>AudioInput</code>, and <code>AudioPlayer</code> objects. 
  * The <code>FloatControl</code> class is defined by the JavaSound API and it 
  * represents a control of a <code>DataLine</code>. A <code>DataLine</code> is a low-level JavaSound class that 
  * is used for sending audio to, or receiving audio from, the audio system. <code>shift</code> changes the control 
  * value from the initial value to the final value linearly over the specified time period, specified in microseconds. 
  * This method returns without blocking; it does not wait for the shift to complete. An implementation should complete 
  * the operation within the time specified. The default implementation simply changes the value to the final value 
  * immediately. You will find that usually shifting is not supported and it is recommended that you use the 
  * appropriate shift method of <code>Controller</code> (see the example Controller >> shifting).
  * <p>
  * Press 's' to shift the pan from left to right (probably it will just be set hard right).
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
    text("The current pan is " + out.pan().getValue() + ".", 5, 15);
  }
  else
  {
    text("There is no pan control for this output.", 5, 15);
  }
}

void keyReleased()
{
  if ( out.hasControl(Controller.PAN) )
  {
    // the shift time is in microseconds!
    if ( key == 's' ) out.pan().shift(-1, 1, 2000000);
  }
}
