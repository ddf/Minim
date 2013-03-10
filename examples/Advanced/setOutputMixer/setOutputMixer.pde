/**
  * This sketch demonstrates how to use the <code>setOutputMixer</code> 
  * method of <code>Minim</code> in conjunction with the <code>getLineOut</code> 
  * method. By accessing the Mixer objects of Javasound, you can find one that 
  * corresponds to the output mixer of the sound device of your choice. You can 
  * then set this Mixer as the one that should use when creating an AudioOutput for you.
  * This Mixer will also be used when obtaining outputs for AudioPlayers, AudioSamples, 
  * and any other classes that result in sound being ouput to your speakers.
  * <p>
  * This sketch uses controlP5 for the GUI, a user-contributed Processing library.
  * <p>
  * For more information about Minim and additional features, visit http://code.compartmental.net/minim/
  */

import ddf.minim.*;
// need signals package for SineWave
import ddf.minim.signals.*;
import controlP5.*;
// need to import this so we can use Mixer and Mixer.Info objects
import javax.sound.sampled.*;

Minim minim;
AudioOutput out;
// an array of info objects describing all of 
// the mixers the AudioSystem has. we'll use
// this to populate our gui scroll list and
// also to obtain an actual Mixer when the
// user clicks on an item in the list.
Mixer.Info[] mixerInfo;

// a signal for our output
SineWave sine;

ControlP5 gui;

void setup()
{
  size(512, 275);

  minim = new Minim(this);
  gui = new ControlP5(this);
  
  DropdownList mixers = gui.addDropdownList("Mixers", 10, 10, 475, 280);
  mixers.setLabel("Choose A Mixer");
  
  mixerInfo = AudioSystem.getMixerInfo();
  
  for(int i = 0; i < mixerInfo.length; i++)
  {
    ListBoxItem b = mixers.addItem("item"+i, i);
    b.setText(mixerInfo[i].getName());
  } 
  
  sine = new SineWave(220, 0.3, 44100);
  
}

void draw()
{
  background(0);
  
  //gui.draw();
  
  if ( out != null )
  {
    stroke(255);
    // draw the waveforms
    for(int i = 0; i < out.bufferSize() - 1; i++)
    {
      line(i, 50 + out.left.get(i)*50, i+1, 50 + out.left.get(i+1)*50);
      line(i, 150 + out.right.get(i)*50, i+1, 150 + out.right.get(i+1)*50);
    }
  }
}

public void controlEvent(ControlEvent theEvent) 
{
  int mixerIndex = (int)theEvent.getGroup().value();
  
  println("Using mixer info " + mixerInfo[mixerIndex].getName());
  
  Mixer mixer = AudioSystem.getMixer(mixerInfo[mixerIndex]);
  
  minim.setOutputMixer(mixer);
  
  if ( out != null )
  {
    out.close();
  }
  
  out = minim.getLineOut(Minim.STEREO);  
  
  if ( out != null )
  {
    out.addSignal(sine);
  }
}
