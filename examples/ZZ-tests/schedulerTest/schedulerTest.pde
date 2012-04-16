import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.ugens.*;

Minim minim;
AudioOutput out;
AudioRecorder recorder;

void setup()
{
  size(100, 100);
  
  minim = new Minim(this);
  //minim.debugOn();
  out = minim.getLineOut();
  
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
}

void keyPressed()
{
  recorder.endRecord();
  recorder.save();
}

