/* balanceExample
   is an example of using the Balance UGen inside an instrument.
   It is important to note that Balance works specifically on stereo signals.
   It is *not* the same as Pan, which takes a mono signal and places it in a stereo field.
   Balance works by simply attenuating either the left or right channel of a stereo signal
   based on what the balance is set to. Negative balance values will attenuate the left channel
   and positive balance values attentuate the right channel.
   
   author: Damien Di Fede
*/

import ddf.minim.*;
import ddf.minim.ugens.*;

Minim minim;
AudioOutput out;
ToneInstrument myNote;

void setup()
{
  // initalize the drawing window
  size(512, 200, P2D);

  // initalize the minim object and output
  minim = new Minim(this);
  // note that we *must* ask for a stereo output because balance does not work with mono output.
  out = minim.getLineOut(Minim.STEREO, 1024);
  
  // pause time when adding a bunch of notes at once
  out.pauseNotes();
  
  // make an instance of my instrument and ask the output to play it
  // arguments are: oscillator frequency, oscillator amplitude, lfo for the balance frequency, lfo for the balance amplitude
  myNote = new ToneInstrument( 200.f, 0.3, 0.5f, 1.0f, out);
  // play this instrument on the output. 
  // arguments are: how many seconds from now to play the note, and how long to play the note for
  out.playNote(0.f, 8.f, myNote );
  
  // make another instance of my instrument
  myNote = new ToneInstrument( 415.3f, 0.3, 1.f, 1.f, out);
  out.playNote(2.f, 0.5f, myNote );
  
  myNote = new ToneInstrument( 415.3f, 0.3, 2.f, 1.f, out);
  out.playNote(3.5, 0.5f, myNote );
  
  myNote = new ToneInstrument( 415.3f, 0.3, 3.f, 1.f, out);
  out.playNote(5.f, 0.5f, myNote );
  
  myNote = new ToneInstrument( 830.6f, 0.3, 5.f, 1.f, out);
  out.playNote(6.5f, 1.5f, myNote );
 
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
