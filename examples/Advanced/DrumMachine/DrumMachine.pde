import processing.opengl.*;

/**
  * This sketch is a more involved use of AudioSamples to create a simple drum machine. Click on the buttons to 
  * toggle them on and off. The buttons that are on will trigger samples when the beat marker passes over their 
  * column. You can change the tempo by clicking in the BPM box and dragging the mouse up and down.
  */


import controlP5.*;
import ddf.minim.*;

Minim minim;
AudioSample kick;
AudioSample snare;
AudioSample hat;

ControlP5 gui;
boolean[] hatRow = new boolean[16];
boolean[] snrRow = new boolean[16];
boolean[] kikRow = new boolean[16];

public int bpm;
int tempo; // how long a sixteenth note is in milliseconds
int clock; // the timer for moving from note to note
int beat; // which beat we're on
boolean beatTriggered; // only trigger each beat once

void setup()
{
  size(395, 200, OPENGL);
  minim = new Minim(this);
  // load BD.wav from the data folder, with a 512 sample buffer
  int bsize = 2048;
  kick = minim.loadSample("BD.wav", bsize);
  // load SD.wav from the data folder
  snare = minim.loadSample("SD.wav", bsize);
  // load CHH.wav from the data folder
  hat = minim.loadSample("CHH.wav", bsize);
  
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
  tempo = 125;
  clock = millis();
  beat = 0;
  beatTriggered = false;
  
  textFont(createFont("Arial", 16));
}

void draw()
{
  background(0);
  fill(255);
  //text(frameRate, width - 60, 20);
  
  if ( millis() - clock >= tempo )
  {
    clock = millis();
    beat = (beat+1) % 16;
    beatTriggered = false;
  }
  
  if ( !beatTriggered )
  {
    if ( hatRow[beat] ) hat.trigger();
    if ( snrRow[beat] ) snare.trigger();
    if ( kikRow[beat] ) kick.trigger();
    beatTriggered = true;
  }
  
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
  
  // use the mix buffer do draw the waveforms.
  // because these are MONO files, we could have used the left or right buffers and got the same data
  for (int i = 0; i < kick.mix.size()-1; i++)
  {
    line(i, 65 - hat.mix.get(i)*30, i+1, 65 - hat.mix.get(i+1)*30);
    line(i, 115 - snare.mix.get(i)*30, i+1, 115 - snare.mix.get(i+1)*30);
    line(i, 165 - kick.mix.get(i)*30, i+1, 165 - kick.mix.get(i+1)*30);
  }
  
  gui.draw();
}

public void controlEvent(ControlEvent e)
{
  //println(e.controller().label() + ": " + e.controller().value());
  if ( e.controller().label() == "hat" )
  {
    hatRow[ e.controller().id() ] = e.controller().value() == 0.0 ? false : true;
  }
  else if ( e.controller().label() == "snr" )
  {
    snrRow[ e.controller().id() ] = e.controller().value() == 0.0 ? false : true;
  }
  else if ( e.controller().label() == "kik" )
  {
    kikRow[ e.controller().id() ] = e.controller().value() == 0.0 ? false : true;
  }
  else if ( e.controller().name() == "bpm" )
  {
    float bps = (float)bpm/60.0f;
    tempo = int(1000 / (bps * 4)); 
  }
}

void stop()
{
  // close the AudioSamples before we exit
  kick.close();
  snare.close();
  hat.close();
  minim.stop();
  
  super.stop();
}
