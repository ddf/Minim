import processing.opengl.*;

/**
  * This sketch is a more involved use of AudioSamples to create a simple drum machine. 
  * Click on the buttons to toggle them on and off. The buttons that are on will trigger 
  * samples when the beat marker passes over their column. You can change the tempo by 
  * clicking in the BPM box and dragging the mouse up and down.
  * <p>
  * We achieve the timing by using AudioOutput's playNote method and a cleverly written Instrument.
  * <p>
  * For more information about Minim and additional features, 
  * visit http://code.compartmental.net/minim/
  */


import controlP5.*;
import ddf.minim.*;
import ddf.minim.ugens.*;

Minim       minim;
AudioOutput out;

Sampler     kick;
Sampler     snare;
Sampler     hat;

ControlP5 gui;
boolean[] hatRow = new boolean[16];
boolean[] snrRow = new boolean[16];
boolean[] kikRow = new boolean[16];

public int bpm;

int beat; // which beat we're on

// here's an Instrument implementation that we use 
// to trigger Samplers every sixteenth note. 
// Notice how we get away with using only one instance
// of this class to have endless beat making by 
// having the class schedule itself to be played
// at the end of its noteOff method. 
class Tick implements Instrument
{
  void noteOn( float dur )
  {
    if ( hatRow[beat] ) hat.trigger();
    if ( snrRow[beat] ) snare.trigger();
    if ( kikRow[beat] ) kick.trigger();
  }
  
  void noteOff()
  {
    // next beat
    beat = (beat+1)%16;
    // set the new tempo
    out.setTempo( bpm );
    // play this again right now, with a sixteenth note duration
    out.playNote( 0, 0.25f, this );
  }
}

void setup()
{
  size(395, 200, OPENGL);
  minim = new Minim(this);
  out   = minim.getLineOut();
  
  // load all of our samples, using 4 voices for each.
  // this will help ensure we have enough voices to handle even
  // very fast tempos.
  kick  = new Sampler( "BD.wav", 4, minim );
  snare = new Sampler( "SD.wav", 4, minim );
  hat   = new Sampler( "CHH.wav", 4, minim );
  
  // patch samplers to the output
  kick.patch( out );
  snare.patch( out );
  hat.patch( out );
  
  gui = new ControlP5(this);
  gui.setColorForeground(color(128, 200));
  gui.setColorActive(color(255, 0, 0, 200));
  Toggle h;
  Toggle s;
  Toggle k;
  for (int i = 0; i < 16; i++)
  {
    h = gui.addToggle("hat" + i, false, 10+i*24, 50, 14, 30);
    h.setId(i);
    h.setLabel("hat");
    s = gui.addToggle("snr" + i, false, 10+i*24, 100, 14, 30);
    s.setId(i);
    s.setLabel("snr");
    k = gui.addToggle("kik" + i, false, 10+i*24, 150, 14, 30);
    k.setId(i);
    k.setLabel("kik");
  }
  gui.addNumberbox("bpm", 120, 10, 5, 20, 15);
  bpm = 120;
  beat = 0;
  
  // start the sequencer
  out.setTempo( bpm );
  out.playNote( 0, 0.25f, new Tick() );
  
  textFont(createFont("Arial", 16));
}

void draw()
{
  background(0);
  fill(255);
  //text(frameRate, width - 60, 20);
  
  stroke(128);
  if ( beat % 4 == 0 )
  {
    fill(200, 0, 0);
  }
  else
  {
    fill(0, 200, 0);
  }
    
  // beat marker    
  rect(10+beat*24, 35, 14, 9);
  
  gui.draw();
}

public void controlEvent(ControlEvent e)
{
  println(e.getController().getLabel() + ": " + e.controller().value());
  if ( e.controller().getLabel() == "hat" )
  {
    hatRow[ e.controller().id() ] = e.controller().value() == 0.0 ? false : true;
  }
  else if ( e.controller().getLabel() == "snr" )
  {
    snrRow[ e.controller().id() ] = e.controller().value() == 0.0 ? false : true;
  }
  else if ( e.controller().getLabel() == "kik" )
  {
    kikRow[ e.controller().id() ] = e.controller().value() == 0.0 ? false : true;
  }
}
