import ddf.minim.*;

/**
  * Tests for an out-of-memory exception when attempting to skip more than a minute of audio in a large file.
  * We test both wav and mp3 to account for possible differences in the decoders.
  */
  
Minim       minim;
AudioPlayer mp3;
AudioPlayer wav;

void setup()
{
  size( 256, 256 );
  
  minim = new Minim(this);
  minim.debugOn();
  
  mp3 = minim.loadFile( "long.mp3" );
  wav = minim.loadFile( "long.wav" );
}

void draw()
{
  background(20);
  
  stroke( 255, 0, 0 );
  
  float x = map( mp3.position(), 0, mp3.length(), 0, width );
  line( x, height/4 - 20, x, height/4 + 20 );
  
  x = map( wav.position(), 0, wav.length(), 0, width );
  line( x, 3*height/4 - 20, x, 3*height/4 + 20 );
}

void keyPressed()
{
  // skip forward and back just over a minute
  int ms = 70 * 1000;
  if ( key == 'f' )
  {
    mp3.skip( ms );
  }
  else if ( key == 'd' )
  {
    mp3.skip( -ms );
  }
  else if ( key == 'v' )
  {
    wav.skip( ms );
  }
  else if ( key == 'c' )
  {
    wav.skip( -ms );
  }
  
  // play pause
  if ( key == 's' )
  {
    mp3.play();
  }
  else if ( key == 'a' )
  {
    mp3.pause();
  }
  else if ( key == 'x' )
  {
    wav.play();
  }
  else if ( key == 'z' )
  {
    wav.pause();
  }
}




