/**
  * This sketch demonstrates how to use the <code>getMinLabel</code> method of a <code>FloatControl</code> object.
  * A <code>FloatControl</code> is what is returned by the <code>gain</code>, <code>volume</code>, <code>pan</code>, and 
  * <code>balance</code> methods of a <code>Controller</code> object. The class used here is an 
  * <code>AudioOutput</code> but these control methods are also available on <code>AudioSample</code>, <code>AudioSnippet</code>, <code>AudioInput</code>, 
  * and <code>AudioPlayer</code> objects. The <code>FloatControl</code> class is defined by the JavaSound API and it 
  * represents a control of a <code>DataLine</code>. A <code>DataLine</code> is a low-level JavaSound class that 
  * is used for sending audio to, or receiving audio from, the audio system. Float controls have a minimum 
  * value that they can be set to. The value has an associated <code>String</code> label, 
  * which is what is returned by the <code>getMinLabel</code> method.
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
  
  if ( out.hasControl(Controller.PAN) )
  {
    text("The pan's minimum value label is " + out.pan().getMinLabel() + ".", 5, 15);
  }
  else
  {
    text("There is no pan control for this output.", 5, 15);
  }
}
