/**
  * This sketch demonstrates how to use the <code>setInputMixer</code> 
  * method of <code>Minim</code> in conjunction with the <code>getLineIn</code> 
  * method. By accessing the <code>Mixer</code> objects of Javasound, 
  * you can find one that corresponds to the input mixer of the sound device 
  * of your choice. You can then set this <code>Mixer</code> as the one 
  * that <code>Minim</code> should use when creating an <code>AudioInput</code> 
  * for you
  * <p>
  * This sketch uses controlP5 for the GUI, a user-contributed Processing library.
  */

import ddf.minim.*;
import controlP5.*;
// need to import this so we can use Mixer and Mixer.Info objects
import javax.sound.sampled.*;

Minim minim;
AudioInput in;
// an array of info objects describing all of 
// the mixers the AudioSystem has. we'll use
// this to populate our gui scroll list and
// also to obtain an actual Mixer when the
// user clicks on an item in the list.
Mixer.Info[] mixerInfo;

ControlP5 gui;

void setup()
{
  size(512, 275);

  minim = new Minim(this);
  gui = new ControlP5(this);
  
  ScrollList mixers = gui.addScrollList("Mixers", 10, 10, 475, 280);
  mixers.setLabel("Choose A Mixer");
  
  mixerInfo = AudioSystem.getMixerInfo();
  
  for(int i = 0; i < mixerInfo.length; i++)
  {
    controlP5.Button b = mixers.addItem("item"+i, i);
    b.setLabel(mixerInfo[i].getName());
  } 
  
}

void draw()
{
  background(0);
  
  //gui.draw();
  
  if ( in != null )
  {
    stroke(255);
    // draw the waveforms
    for(int i = 0; i < in.bufferSize() - 1; i++)
    {
      line(i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50);
      line(i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50);
    }
  }
}

public void controlEvent(ControlEvent theEvent) 
{
  int mixerIndex = (int)theEvent.controller().value();
  
  println("User chose " + theEvent.controller().label());
  println("Using mixer info " + mixerInfo[mixerIndex].getName());
  
  Mixer mixer = AudioSystem.getMixer(mixerInfo[mixerIndex]);
  
  if ( in != null )
  {
    in.close();
  }
  
  minim.setInputMixer(mixer);
  
  in = minim.getLineIn(Minim.STEREO);
  
}


void stop()
{
  // always close Minim audio classes when you are done with them
  if ( in != null )
  {
    in.close();
  }
  minim.stop();
  
  super.stop();
}
