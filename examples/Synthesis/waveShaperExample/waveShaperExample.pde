/* waveShaperExample<br/>
   is an example of using the WaveShaper UGen inside an instrument.
   <p>
   For more information about Minim and additional features, visit http://code.compartmental.net/minim/
   <p>   
   author: Damien Di Fede, Anderson Mills<br/>
   Anderson Mills's work was supported by numediart (www.numediart.org).
*/

import ddf.minim.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*; // for BandPass

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

  // one can set the tempo of the piece in beats per minute, too
  out.setTempo( 120f );
  
  // our chik sounds won't overlap, so we can reuse the same instance
  ChikInstrument chik = new ChikInstrument( out );
  float chikDur = 0.1f;
  
  float shaperAmp = 0.5f;
  // let's do a few repeats of this pattern
  for(int i = 0; i < 4; i++)
  {
    // first set the note offset so we put notes in the right measure
    out.setNoteOffset( i * 8 );
    
    // and now notes!
    out.playNote( 0.f, chikDur, chik );
    out.playNote( 0.f, 1.0f, new WaveShaperInstrument( Frequency.ofPitch("C2").asHz(), shaperAmp, out ) );
    out.playNote( 1.f, chikDur, chik );
    out.playNote( 2.f, chikDur, chik );
    out.playNote( 3.f, chikDur, chik );
    
    out.playNote( 4.f, 1.0f, new WaveShaperInstrument( Frequency.ofPitch("C2").asHz(), shaperAmp, out ) );
    out.playNote( 4.f, chikDur, chik );
    out.playNote( 5.f, chikDur, chik );
    out.playNote( 5.5f, 1.0f, new WaveShaperInstrument( Frequency.ofPitch("Eb2").asHz(), shaperAmp, out ) );
    out.playNote( 6.f, chikDur, chik );
    out.playNote( 7.f, chikDur, chik );
    out.playNote( 7.f, 1.0f, new WaveShaperInstrument( Frequency.ofPitch("Eb2").asHz(), shaperAmp, out ) );
  }
  // one last hit!
  out.playNote( 8.f, chikDur, chik );
  out.playNote( 8.f, 8.0f, new WaveShaperInstrument( Frequency.ofPitch("C1").asHz(), shaperAmp, out ) );
  
  // resume notes after you enter a bunch
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

