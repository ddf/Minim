import ddf.minim.*;
import ddf.minim.ugens.*;
import ddf.minim.analysis.*;

Minim       minim;
AudioOutput out;
Noise       noize;
Flanger     flange;

// we render the spectrum instead of the waveform
// because this lets you really see what the Flanger is doing.
FFT         fft;

void setup()
{
  size( 512, 512 );
  
  minim = new Minim(this);
  out   = minim.getLineOut();
  
  // we use white noise to demonstrate the Flanger effect
  // because the sound of the Flanger is more 
  // pronounced when the audio being flanged 
  // has high frequency content
  noize = new Noise( Noise.Tint.WHITE );
  
  flange = new Flanger( 10,     // delay length in milliseconds ( clamped to [0,100] )
                        0.1f,   // lfo rate in Hz ( clamped at low end to 0.001 )
                        10,      // delay depth in milliseconds ( minimum of 0 )
                        0.5f,   // amount of feedback ( clamped to [0,1] )
                        0.5f,   // amount of dry signal ( clamped to [0,1] )
                        0.5f    // amount of wet signal ( clamped to [0,1] )
                       );
                        
  noize.patch( flange ).patch( out );
  
  fft = new FFT( 1024, out.sampleRate() );
}

void draw()
{
  background( 0 );
  
  stroke( 255 );
  
  fft.forward( out.mix );
  
  for( int i = 0; i < fft.specSize(); ++i )
  {
    float val = fft.getBand( i );
    line( i, height, i, height - pow ( 10.0, (0.05 * val) )*2 );
  }
}

void mouseMoved()
{
  flange.delay.setLastValue( map( mouseX, 0,      width, 1, 5 ) );
  flange.depth.setLastValue( map( mouseY, height, 0,     1, 5 ) );
}


