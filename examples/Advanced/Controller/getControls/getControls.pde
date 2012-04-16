/**
  * This sketch demonstrates how to use the <code>getControls</code> method of a <code>Controller</code> object. 
  * The class used here is an <code>AudioOutput</code> but you can also get the controls of  
  * <code>AudioSample</code>, <code>AudioSnippet</code>, <code>AudioInput</code>, and <code>AudioPlayer</code> objects. 
  * <code>getControls</code> returns an array of JavaSound <code>Control</code> objects. You can determine what type 
  * of <code>Control</code> each one is by using the <code>getType</code> method and then comparing the type to one 
  * of the static control types of <code>Controller</code> (see the hasControl example). If you plan to use the 
  * <code>getControls</code> method you must also import the <code>Control</code> class from JavaSound (see the source).
  */

import ddf.minim.*;
import ddf.minim.signals.*;
import javax.sound.sampled.Control;

Minim minim;
AudioOutput out;
Control[] controls;

void setup()
{
  size(512, 200);
  minim = new Minim(this);
  out = minim.getLineOut();
  controls = out.getControls();
  
  textFont(createFont("Arial", 12));
}

void draw()
{
  background(0);
  
  for ( int i = 0; i < controls.length; i++ )
  {
    text("Control " + (i+1) + " is a " + controls[i].toString() + ".", 5, 15 + i*15);
  }
}
