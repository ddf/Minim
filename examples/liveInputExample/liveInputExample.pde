/* liveInputExample
   is an example of using the LiveInput UGen to patch the input from your computer (usually microphone) to the output.
   
   author: Damien Di Fede
*/

import ddf.minim.*;
import ddf.minim.ugens.*;

Minim minim;
AudioOutput out;
LiveInput in;

void setup()
{
  // initialize the drawing window
  size(512, 200);
  
  // initialize the minim and out objects
  minim = new Minim(this);
  out = minim.getLineOut();
  
  // construct a LiveInput by giving it an InputStream from minim.
  // we ask for an input with the same audio properties as the output.
  in = new LiveInput( minim.getInputStream(out.getFormat().getChannels(), out.bufferSize(), out.sampleRate(), out.getFormat().getSampleSizeInBits()) );
  
  // create granulate UGen so we can hear the input being modfied before it goes to the output
  GranulateSteady grain = new GranulateSteady();
  
  // patch the input through the grain effect to the output
  in.patch(grain).patch(out);
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
  // close the LiveInput
  in.close();
  // close the AudioOutput
  out.close();
  // stop the minim object
  minim.stop();
  // stop the processing object
  super.stop();
}

