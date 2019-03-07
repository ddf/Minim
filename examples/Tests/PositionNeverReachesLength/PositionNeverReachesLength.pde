import ddf.minim.*;

Minim minim;
AudioPlayer player;

void setup()
{
  size(512, 200, P3D);
  
  // we pass this to Minim so that it can load files from the data directory
  minim = new Minim(this);
  minim.debugOn();
  
  // try sample1.mp3, sample2.mp3, and sample3.mp3
  player = minim.loadFile("sample3.mp3");
  
  // play the file from start to finish.
  // if you want to play the file again, 
  // you need to call rewind() first.
  if ( player != null ) player.play();
}

void draw()
{
  if ( player == null ) return;
  
  background(0);
  stroke(255);
  
  text(player.getMetaData().fileName() + ": " + player.position() + " / " + player.length(), 10, 10);
  
  // draw the waveforms
  // the values returned by left.get() and right.get() will be between -1 and 1,
  // so we need to scale them up to see the waveform
  // note that if the file is MONO, left.get() and right.get() will return the same value
  for(int i = 0; i < player.bufferSize() - 1; i++)
  {
    float x1 = map( i, 0, player.bufferSize(), 0, width );
    float x2 = map( i+1, 0, player.bufferSize(), 0, width );
    line( x1, 50 + player.left.get(i)*50, x2, 50 + player.left.get(i+1)*50 );
    line( x1, 150 + player.right.get(i)*50, x2, 150 + player.right.get(i+1)*50 );
  }
}
