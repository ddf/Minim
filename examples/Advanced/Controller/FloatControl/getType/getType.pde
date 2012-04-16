/**
  * This sketch demonstrates how to use the <code>getType</code> method of a <code>FloatControl</code> object.
  * A <code>FloatControl</code> is what is returned by the <code>gain</code>, <code>volume</code>, <code>pan</code>, and 
  * <code>balance</code> methods of a <code>Controller</code> object. The class used here is an 
  * <code>AudioOutput</code> but these control methods are also available on <code>AudioSample</code>, <code>AudioSnippet</code>, <code>AudioInput</code>, 
  * and <code>AudioPlayer</code> objects. The <code>FloatControl</code> class is defined by the JavaSound API and it 
  * represents a control of a <code>DataLine</code>. A <code>DataLine</code> is a low-level JavaSound class that 
  * is used for sending audio to, or receiving audio from, the audio system. <code>getType</code> will return a 
  * <code>Control.Type</code> object that is equal to one of the static <code>Control.Type</code> objects of <code>Controller</code>.
  * These are <code>Controller.BALANCE</code>, <code>Controller.GAIN</code>, <code>Controller.MUTE</code>, 
  * <code>Controller.PAN</code>, <code>Controller.SAMPLE_RATE</code>, and <code>Controller.VOLUME</code>. This method is 
  * inherited from the <code>Control</code> class and is more useful when you are trying to figure out what controls 
  * you have in the array returned by <code>getControls</code> (see the example Controller >> getControls).
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
    if ( out.gain().getType() == Controller.GAIN )
    {
      text("We got the gain!", 5, 15);
    }
    else
    {
      // we should never see this
      text("We didn't get the gain!", 5, 15);
    }
  }
  else
  {
    text("There is no gain control for this output.", 5, 15);
  }
}
