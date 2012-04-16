import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.ugens.*;

Minim minim;
AudioOutput out;
AudioRecorder recorder;
int n;

void setup()
{
  size(100, 100);
  
  minim = new Minim(this);
  //minim.debugOn();
  out = minim.getLineOut();
  n = 0;
  
  recorder = minim.createRecorder(out, "beeps.wav", false);
  recorder.beginRecord();
  
  out.setTempo(120);
  
  out.pauseNotes();
  
  for(int i = 0; i < 320; i++)
  {
    float b = i;
    float dur = 1;
    out.playNote(b, dur, new BeepInstrument(220, out));
    out.playNote(b+0.25, dur, new BeepInstrument(440, out));
    out.playNote(b+0.5, dur, new BeepInstrument(440, out));
    out.playNote(b+0.75, dur, new BeepInstrument(440, out));
    
    out.playNote(b, dur, new BeepInstrument(110, out));
    out.playNote(b+0.25, dur, new BeepInstrument(220, out));
    out.playNote(b+0.5, dur, new BeepInstrument(220, out));
    out.playNote(b+0.75, dur, new BeepInstrument(220, out));
    
    out.playNote(b, dur, new BeepInstrument(493.88, out));
    out.playNote(b+0.25, dur, new BeepInstrument(554.37, out));
    out.playNote(b+0.5, dur, new BeepInstrument(554.37, out));
    out.playNote(b+0.75, dur, new BeepInstrument(554.37, out));    
  }
  
  out.resumeNotes();
}

void draw()
{
  background(0);
  n = (n + 1) % 60;
  if ( 1 == n )
  {
    //out.pauseNotes();
    out.playNote(0, 1, new BeepInstrument(1100, out));
    out.playNote(0.01, 1, new BeepInstrument(1210, out));
    out.playNote(0.02, 1, new BeepInstrument(1320, out));
    out.playNote(0.03, 1, new BeepInstrument(1430, out));
    out.playNote(0.04, 1, new BeepInstrument(1540, out));
    out.playNote(0.05, 1, new BeepInstrument(1650, out));
    out.playNote(0.06, 1, new BeepInstrument(1760, out));
    //out.resumeNotes();
  }

}

void keyPressed()
{
  recorder.endRecord();
  recorder.save();
}

