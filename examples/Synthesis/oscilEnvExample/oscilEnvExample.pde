/* oscilEnvExample<br/>
   is an example of using the Oscil UGen as an amplitude envelope for a note.
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

  // play several notes of different base frequencies and lengths
  // using the BumpyInstrument and its envelope
  out.playNote( 0.5, 2.6, new BumpyInstrument( "A4", 0.5, out ) );
  out.playNote( 2.5, 1.6, new BumpyInstrument( "F4", 0.5, out ) );
  out.playNote( 3.6, 0.9, new BumpyInstrument( "D4", 0.5, out ) );
}

// draw is run many times
void draw()
{
  // erase the window to bluishwhite
  background( 240, 240, 255);
  // draw using a dark red stroke
  stroke( 128, 64, 64 );
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
