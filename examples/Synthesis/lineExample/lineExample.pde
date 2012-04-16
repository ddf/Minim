/* lineExample
   is an example of how to use the Line UGen inside an instrument.
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

// setup is run once at the beginning
void setup()
{ 
  // initialize the drawing window
  size( 512, 200, P2D );
  
  // initialize the minim and out objects
  minim = new Minim( this );
  out = minim.getLineOut( Minim.MONO, 2048 );
  
  // pause time when adding a bunch of notes at once
  // This guarantees accurate timing between all notes added at once.
  out.pauseNotes();

  // set the tempo for the piece
  out.setTempo( 120.0 );
  
  // I want a pause before the music starts`
  out.setNoteOffset( 1.0 );
  
  // play a note or several with the MidiSlideInstrument
  // which basically slides a square wave between two frequencies using
  // MIDI (and therefore a logarithmic slide).
 
  // short pop
  out.playNote( 0.0, 0.1, new MidiSlideInstrument( 48, 48, 0.2, out ) );

  // long slide up of one note
  out.playNote( 1.0, 2.00, new MidiSlideInstrument( 48, 60, 0.2, out ) );
  
  // a few blips and chirps
  out.playNote( 1.0, 0.1, new MidiSlideInstrument( 87, 99, 0.2, out ) );
  out.playNote( 1.5, 0.05, new MidiSlideInstrument( 99, 99, 0.2, out ) );
  out.playNote( 3.0, 0.1, new MidiSlideInstrument( 87, 99, 0.2, out ) );  
  out.playNote( 4.0, 0.1, new MidiSlideInstrument( 48, 48, 0.2, out ) );
  out.playNote( 4.25, 0.1, new MidiSlideInstrument( 48, 48, 0.2, out ) );
  
  // slide of a chord up 
  out.playNote( 5.0, 2.00, new MidiSlideInstrument( 48, 60, 0.2, out ) );
  out.playNote( 5.0, 2.00, new MidiSlideInstrument( 60, 72, 0.2, out ) );
  out.playNote( 5.0, 2.00, new MidiSlideInstrument( 67, 79, 0.2, out ) );
  
  // two more blips
  out.playNote( 8.0, 0.1, new MidiSlideInstrument( 84, 84, 0.2, out ) );
  out.playNote( 8.25, 0.05, new MidiSlideInstrument( 96, 84, 0.2, out ) );

  // a 1 beat chord slide down
  out.playNote( 9.0, 1.00, new MidiSlideInstrument( 60, 55, 0.2, out ) );
  out.playNote( 9.0, 1.00, new MidiSlideInstrument( 72, 67, 0.2, out ) );
  out.playNote( 9.0, 1.00, new MidiSlideInstrument( 79, 74, 0.2, out ) );
  
  // after a quarter beat pause, continue that chord down
  out.playNote( 10.25, 0.75, new MidiSlideInstrument( 55, 48, 0.2, out ) );
  out.playNote( 10.25, 0.75, new MidiSlideInstrument( 67, 60, 0.2, out ) );
  out.playNote( 10.25, 0.75, new MidiSlideInstrument( 74, 67, 0.2, out ) );
  
  // more chirps
  out.playNote( 11.5, 0.05, new MidiSlideInstrument( 84, 96, 0.2, out ) );
  out.playNote( 11.125, 0.05, new MidiSlideInstrument( 60, 48, 0.2, out ) );
  out.playNote( 11.25, 0.05, new MidiSlideInstrument( 96, 48, 0.2, out ) );

  // one long slow note down
  out.playNote( 12.00, 2.5, new MidiSlideInstrument( 60, 48, 0.2, out ) );
  
  // joined by two others (this does not make a harmonic chord,
  // because the notes are all sliding to one common goal)
  out.playNote( 13.00, 1.5, new MidiSlideInstrument( 55, 48, 0.2, out ) );
  out.playNote( 13.00, 1.5, new MidiSlideInstrument( 67, 48, 0.2, out ) );
  
  // two constant notes with a sliding note
  out.playNote( 15.00, 2.0, new MidiSlideInstrument( 48, 48, 0.2, out ) );  
  out.playNote( 15.00, 1.0, new MidiSlideInstrument( 36, 48, 0.2, out ) );
  out.playNote( 15.00, 1.01, new MidiSlideInstrument( 36, 36, 0.1, out ) );
  
  // more chirps
  out.playNote( 16.5, 0.05, new MidiSlideInstrument( 48, 36, 0.2, out ) );
  out.playNote( 16.75, 0.05, new MidiSlideInstrument( 70, 48, 0.2, out ) );
  
  // and final blips and chirps
  out.playNote( 17.50, 0.1, new MidiSlideInstrument( 96, 96, 0.2, out ) );
  out.playNote( 18.00, 0.1, new MidiSlideInstrument( 72, 84, 0.1, out ) );
  out.playNote( 18.00, 0.2, new MidiSlideInstrument( 84, 84, 0.2, out ) );
  
  // resume time after adding all of these notes at once.
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
