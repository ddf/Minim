import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;

Minim minim;
AudioOutput out;
SquareInstrument myTest;
SquareInstrument myOtherTest;

void setup()
{
  size(600, 200);

  minim = new Minim(this);
  out = minim.getLineOut(Minim.MONO, 2048);
  myTest = new SquareInstrument(500, 0.3, out);
  
  out.playNote(0, 5, myTest);
  
/*
  for(int i = 0; i < 16; i++)
  {
    out.playNote(0.25 + i*0.5, 0.2, myTest);
    
  }
  */
}

void draw()
{
 background(0);
  stroke(255);
  // draw the waveforms
  for(int i = 0; i < out.bufferSize()-1; i++)
  {
    float x1 = map(i, 0, out.bufferSize(), 0, width);
    float x2 = map(i+1, 0, out.bufferSize(), 0, width);
    line(x1, 50 + out.left.get(i)*50, x2, 50 + out.left.get(i+1)*50);
    line(x1, 150 + out.right.get(i)*50, x2, 150 + out.right.get(i+1)*50);
  }
  // draw 0 lines
  stroke(255, 0, 0);
  line(0, 50, width, 50);
  line(0, 150, width, 150);
  
  


}

void mousePressed()
{
  myTest.noteOn(0.0);
  //myOtherTest.noteOn();
}

void mouseReleased()
{
  myTest.noteOff();
 // myOtherTest.noteOff();
}

void stop()
{
  out.close();
  minim.stop();

  super.stop();
}

