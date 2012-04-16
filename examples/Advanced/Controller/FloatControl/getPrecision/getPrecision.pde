/**
  * This sketch demonstrates how to use the <code>getPrecision</code> method of a <code>FloatControl</code> object.
  * A <code>FloatControl</code> is what is returned by the <code>gain</code>, <code>volume</code>, <code>pan</code>, and 
  * <code>balance</code> methods of a <code>Controller</code> object. The class used here is an 
  * <code>AudioOutput</code> but these control methods are also available on <code>AudioSample</code>, <code>AudioSnippet</code>, <code>AudioInput</code>, 
  * and <code>AudioPlayer</code> objects. The <code>FloatControl</code> class is defined by the JavaSound API and it 
  * represents a control of a <code>DataLine</code>. A <code>DataLine</code> is a low-level JavaSound class that 
  * is used for sending audio to, or receiving audio from, the audio system. <code>getPrecision</code> gets the 
  * resolution or granularity of the control, in the units that the control measures. The precision is the size of 
  * the increment between discrete valid values for this control, over the set of supported floating-point values. 
  */

import ddf.minim.*;

Minim minim;
AudioOutput out;

void setup()
{
  size(512, 200);
  minim = new Minim(this);
  out = minim.getLineOut();
  
  textFont(createFont("Arial", 12));
}

void draw()
{
  background(0);
  
  if ( out.hasControl(Controller.GAIN) )
  {
    text("The gain has a precision of " + out.gain().getPrecision() + " " + out.gain().getUnits() + ".", 5, 15);
  }
  else
  {
    text("There is no gain control for this output.", 5, 15);
  }
}
