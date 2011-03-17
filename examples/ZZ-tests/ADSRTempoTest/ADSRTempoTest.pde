import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;

Minim minim;
AudioOutput out;
SineInstrument mySine;
SineInstrument myOtherSine;

int xa;
int xDir;

void setup()
{
  size(512, 200, P2D);
  
  minim = new Minim(this);
  out = minim.getLineOut(Minim.MONO, 2048);
  mySine = new SineInstrument(115, 0.5, out);
  myOtherSine = new SineInstrument(220, 0.3, out);

  //out.playNote(0.25, 0.8, new SineInstrument(115, 0.5, out));
  //out.playNote(1.20, 0.8, new SineInstrument(134, 0.5, out));
  out.setTempo( 90f );
  for(int i = 0; i < 4; i++)
  {
    out.playNote(1.25 + i*2.0, 0.3, new SineInstrument(75, 0.8, out));
    out.playNote(2.50 + i*2.0, 0.3, new SineInstrument(75, 0.8, out));
    
    out.playNote(1.75 + i*2.0, 0.3, new SineInstrument(175, 0.6, out));
    out.playNote(2.75 + i*2.0, 0.3, new SineInstrument(175, 0.6, out));
    
    //out.playNote(0.25 + i*2.0, 0.3, new SineInstrument(1750, 0.1, out));
    out.playNote(1.25 + i*2.0, 0.3, new SineInstrument(3750, 0.1, out));
    out.playNote(1.5 + i*2.0, 0.3, new SineInstrument(1750, 0.1, out));
    out.playNote(1.75 + i*2.0, 0.3, new SineInstrument(3750, 0.1, out));
    out.playNote(2.0 + i*2.0, 0.3, new SineInstrument(1750, 0.1, out));
    out.playNote(2.25 + i*2.0, 0.3, new SineInstrument(3750, 0.1, out));
    out.playNote(2.5 + i*2.0, 0.3, new SineInstrument(5550, 0.1, out));
    out.playNote(2.75 + i*2.0, 0.3, new SineInstrument(3750, 0.1, out));
    
    //out.playNote(i*0.5, 0.05, myOtherSine);
  }
}

void draw()
{
  background(0);
  stroke(255);
  // draw the waveforms
  for(int i = 0; i < out.bufferSize() - 1; i++)
  {
    float x1 = map(i, 0, out.bufferSize(), 0, width);
    float x2 = map(i+1, 0, out.bufferSize(), 0, width);
    line(x1, 50 + out.left.get(i)*50, x2, 50 + out.left.get(i+1)*50);
    line(x1, 150 + out.right.get(i)*50, x2, 150 + out.right.get(i+1)*50);
  }  
}

void mousePressed()
{
  mySine.noteOn( 0.0 );
}

void mouseReleased()
{
  mySine.noteOff();
}

void stop()
{
  out.close();
  minim.stop();

  super.stop();
}

