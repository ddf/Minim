import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;

Minim minim;
AudioOutput out;
AudioRecorder recorder;
ToneInstrument myNote;

void setup()
{
  size(512, 200, P2D);

  minim = new Minim(this);
  out = minim.getLineOut( Minim.MONO, 1024);
  recorder = minim.createRecorder( out, "uT.wav", true );
  recorder.beginRecord();
  minim.debugOn();
  out.pauseNotes();
  
  out.setTempo( 60.0 );
  out.setNoteOffset( 1.0 );
  float vol = 0.33;
  out.playNote( 0.00, 0.099, new ToneInstrument( "Fa#", vol, out) );
  out.playNote( 0.00, 0.15, new ToneInstrument( "Re 3", vol, out) );
  out.playNote( 1.00, 0.452, new MultiplierInstrument( 103, vol, out ) );
  out.playNote( 1.00, 0.6, new MultiplierInstrument( 100, vol, out ) );
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

void keyPressed()
{
  recorder.endRecord();
  recorder.save();
}

