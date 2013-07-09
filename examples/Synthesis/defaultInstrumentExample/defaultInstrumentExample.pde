/* defaultInstrumentExample<br/>
   is an example of using the extremely simple default
   instrument built into Minim.  The following is intended
   to be pretty much the minimum necessary to use the default
   instrument.
   <p>
   For more information about Minim and additional features, 
   visit http://code.compartmental.net/minim/
   <p>   
   author: Anderson Mills<br/>
   Anderson Mills's work was supported by numediart (www.numediart.org)
*/

// import everything necessary to make sound.
import ddf.minim.*;
import ddf.minim.ugens.*;

void setup()
{
  Minim minim = new Minim( this );
  AudioOutput out = minim.getLineOut();
  
  // given start time, duration, and frequency
  out.playNote( 0.0, 0.9, 97.99 );
  out.playNote( 1.0, 0.9, 123.47 );
  
  // given start time, duration, and note name  
  out.playNote( 2.0, 2.9, "C3" );
  out.playNote( 3.0, 1.9, "E3" );
  out.playNote( 4.0, 0.9, "G3" );
    
  // given start time and note name or frequency
  // (duration defaults to 1.0)
  out.playNote( 5.0, "" );
  out.playNote( 6.0, 329.63);
  out.playNote( 7.0, "G4" );
  
  // set a note offset  
  out.setNoteOffset( 8.1 );
  
  // because only given a note name or frequency
  // starttime defaults to 0.0 and duration defaults to 1.0
  out.playNote( "G5" );
  out.playNote( 987.77 );
}

void draw()
{
}
