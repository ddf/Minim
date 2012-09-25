/* 
 * For more information about Minim and additional features, visit http://code.compartmental.net/minim/
 */

import ddf.minim.*;
import ddf.minim.ugens.*;

Minim minim;
AudioOutput out;

void setup()
{
  // initialize the drawing window
  size( 512, 200, P2D );

  // initialize the minim and out objects
  minim = new Minim( this );
  out = minim.getLineOut( Minim.MONO );
  
  // queue up some notes using the Crush Instrument
  // its arguments are sine wave frequency, amplitude, and bit crush resolution
  out.playNote(0.5, 2.6, new CrushInstrument( 392.0, 0.5, 16.0, out) );
  out.playNote(3.5, 2.6, new CrushInstrument( 370.0, 0.5, 4.0, out) );
  out.playNote(6.5, 2.6, new CrushInstrument( 261.6, 0.5, 3.0, out) );
  out.playNote(9.5, 2.6, new CrushInstrument( 247.0, 0.5, 2.0, out) );
  
  // queue up a Crushing Instrument, which will change the bit resolution over time
  // its arguments are sine frequency, amplitude, bit crush resolution start and end
  out.playNote(12.5, 10.0, new CrushingInstrument( 191.0, 0.5, 5.2, 1.0, out) );
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
