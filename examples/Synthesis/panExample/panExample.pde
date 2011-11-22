/* panExample
   is an example of using the Pan UGen inside an instrument.

   author: Damien Di Fede
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
  // because we are using a Pan UGen, we need a stereo output.
  out = minim.getLineOut( Minim.STEREO, 1024 );
  
  // initialize the myNote object as a PanInstrument
  PanInstrument myNote = new PanInstrument( 587.3f, 0.5, 0.5, 1.0, out );
  // play a note with the myNote object
  out.playNote( 0.5, 2.6, myNote );
  // give a new note value to myNote
  myNote = new PanInstrument( 415.3f, 0.5, 3.0, 0.5, out );
  // play another note with the myNote object
  out.playNote(3.5, 2.6, myNote );
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

// stop is run when the user presses stop
void stop()
{
  // close the AudioOutput
  out.close();
  // stop the minim object
  minim.stop();
  // stop the processing object
  super.stop();
}
