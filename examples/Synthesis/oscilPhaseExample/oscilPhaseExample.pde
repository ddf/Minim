/* oscilPhaseExample<br/>
   is an example of controlling the phase of an Oscil UGen inside an instrument.
   <p>
   For more information about Minim and additional features, visit http://code.compartmental.net/minim/
   <p>
   author: Damien Di Fede
*/

// import everything necessary to make sound.
import ddf.minim.*;
import ddf.minim.ugens.*;

// create all of the variables that will need to be accessed in
// more than one methods (setup(), draw(), stop()).
Minim minim;
AudioOutput out;

// we reuse this instrument to demonstrate 
// how you can resetPhase on an Oscil to
// start notes at a zero crossing, regardless
// of where it left off
ToneInstrument sine440;

// setup is run once at the beginning
void setup()
{  
  // initialize the drawing window
  size( 512, 200, P2D );
  
    // initialize the minim and out objects
  minim = new Minim( this );
  out = minim.getLineOut( Minim.MONO, 1024 );

  // initialize our steady tone. we pass in 0 for the 
  // frequency of the phase LFO because we don't want 
  // the phase of this instrument to sweep
  sine440 = new ToneInstrument( 440.f, 0.25, 0.f, out );
  
  // pause time when adding a bunch of notes at once
  out.pauseNotes();
  
  // we'll add four sets of two tones, sweeping the phase of
  // one of the tones each time.
  float toneDur = 2.f;
  for(int i = 0; i < 4; i++)
  {
    // play a note with the myNote object
    out.playNote( i * toneDur * 1.5, 2.f, sine440 );
    // play a note with an instrument whose phase will sweep
    // we sweep just a bit faster each time
    // notice how slow the LFO needs to be to actually hear the beating increase as
    // this oscil slowly goes more out of phase with sine440.
    out.playNote( i * toneDur * 1.5, 2.f, new ToneInstrument(440.f, 0.25f, 0.00001f + i * 0.00001f, out) );
  }
  
  // resume time after a bunch of notes are added at once
  out.resumeNotes();
  
}

// draw is run many times
void draw()
{
  // erase the window to black
  background( 0 );
  // draw using a white stroke
  stroke( 255 );
  // draw the waveforms
  for( int i = 0; i < out.bufferSize() - 1; i++ )
  {
    // find the x position of each buffer value
    float x1  =  map( i, 0, out.bufferSize(), 0, width );
    float x2  =  map( i+1, 0, out.bufferSize(), 0, width );
    // draw a line from one buffer position to the next for both channels
    line( x1, 50 + out.left.get(i)*50, x2, 50 + out.left.get(i+1)*50);
    line( x1, 150 + out.right.get(i)*50, x2, 150 + out.right.get(i+1)*50);
  }  
}
