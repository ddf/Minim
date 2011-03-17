import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;

Minim minim;
AudioOutput out;
ToneInstrument myNote1, myNote2, myNote3, myNote4;

void setup()
{
  size(512, 200, P2D);

  minim = new Minim(this);
  out = minim.getLineOut(Minim.MONO, 1024);

  out.pauseNotes();
  myNote1 = new ToneInstrument( "C4", 0.5, out );
  myNote2 = new ToneInstrument( "G3", 0.5, out );
  out.playNote( 0.25, 1.75, myNote1 );
  out.playNote( 1.25, 0.75, myNote2 );
  out.setNoteOffset( 2.0 );
  //myNote1.setNote( "C5" );
  //myNote2.setNote( "E4" );
  //myNote1 = myNote1.setNote( "C5" );
  //myNote2 = myNote2.setNote( "G4" );
  //myNote1 = new ToneInstrument( "C5", 0.5, out );
  //myNote2 = new ToneInstrument( "G3", 0.5, out );
  out.playNote( 0.25, 1.75, myNote1 );
  out.playNote( 1.25, 0.50, myNote2.setNote( "E4" ) );
  out.resumeNotes();
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

void stop()
{
  out.close();
  minim.stop();

  super.stop();
}

