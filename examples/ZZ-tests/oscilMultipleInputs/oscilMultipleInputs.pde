/* multipleInputExample
   is an example of using several different inputs 
   to an Oscil UGen simultaneously inside an instrument.

   author: Anderson Mills
   Anderson Mills's work was supported by numediart (www.numediart.org)
*/

// import everything necessary to make sound.
import ddf.minim.*;
import ddf.minim.ugens.*;

// create all of the variables that will need to be accessed in
// more than one methods (setup(), draw(), stop()).
Minim minim;
AudioOutput out;
// The myWobble object is here because the mouse can also play the note.
// Because only one object is created, that object receives all of the
// noteOn and noteOff calls from both playNote() and mousePressed/Released().
WobbleInstrument myWobble;
// to show when the mouse is pressed
float redAmount;

// setup is run once at the beginning
void setup()
{
  // initialize the drawing window
  size(512, 200, P2D);

  // initialize the minim and out objects
  minim = new Minim(this);
  out = minim.getLineOut(Minim.STEREO, 1024);
  
  // initialize the myWobble object
  myWobble = new WobbleInstrument( 0.5, 840f, 0.8, out );
  
  //initial red amount
  redAmount = 0.0;
  
  // play a myWobble note for 9.6 seconds after 0.3 seconds
  out.playNote(0.3, 9.6, myWobble );
}

// draw is run many times
void draw()
{
  // erase the window to black
  background( redAmount, 0, 0 );
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

// run whenever the mouse is pressed
void mousePressed()
{
  // turn on the note amultiply
  myWobble.noteOn(0.0);
  redAmount = 99.0;
}

// run whenever the mouse is released
void mouseReleased()
{
  // turn off the note
  myWobble.noteOff();
  redAmount = 0.0;
}

