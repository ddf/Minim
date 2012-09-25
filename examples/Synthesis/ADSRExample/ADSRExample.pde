/* ADSRExample<br/>
   is an example of using the ADSR envelope within an instrument.
   <p>
   For more information about Minim and additional features, visit http://code.compartmental.net/minim/
   <p>
   author: Anderson Mills<br/>
   Anderson Mills's work was supported by numediart (www.numediart.org)
*/

// import everything necessary to make sound.
import ddf.minim.*;
import ddf.minim.ugens.*;

// create all of the variables that will need to be accessed in
// more than one methods (setup(), draw(), stop()).
Minim minim;
AudioOutput out;

// setup is run once at the beginning
void setup()
{
  // initialize the drawing window
  size( 512, 200, P2D );

  // initialize the minim and out objects
  minim = new Minim( this );
  out = minim.getLineOut( Minim.MONO, 2048 );
  
  // pause time when adding a bunch of notes at once
  out.pauseNotes();
  
  // make four repetitions of the same pattern
  for( int i = 0; i < 4; i++ )
  {
    // add some low notes
    out.playNote( 1.25 + i*2.0, 0.3, new ToneInstrument( 75, 0.49, out ) );
    out.playNote( 2.50 + i*2.0, 0.3, new ToneInstrument( 75, 0.49, out ) );
    
    // add some middle notes
    out.playNote( 1.75 + i*2.0, 0.3, new ToneInstrument( 175, 0.4, out ) );
    out.playNote( 2.75 + i*2.0, 0.3, new ToneInstrument( 175, 0.4, out ) );
    
    // add some high notes
    out.playNote( 1.25 + i*2.0, 0.3, new ToneInstrument( 3750, 0.07, out ) );
    out.playNote( 1.5 + i*2.0, 0.3, new ToneInstrument( 1750, 0.02, out ) );
    out.playNote( 1.75 + i*2.0, 0.3, new ToneInstrument( 3750, 0.07, out ) );
    out.playNote( 2.0 + i*2.0, 0.3, new ToneInstrument( 1750, 0.02, out ) );
    out.playNote( 2.25 + i*2.0, 0.3, new ToneInstrument( 3750, 0.07, out ) );
    out.playNote( 2.5 + i*2.0, 0.3, new ToneInstrument( 5550, 0.09, out ) );
    out.playNote( 2.75 + i*2.0, 0.3, new ToneInstrument( 3750, 0.07, out ) );
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
