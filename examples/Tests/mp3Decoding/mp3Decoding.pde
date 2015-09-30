/**
  * This sketch demonstrates how to play a file with Minim using an AudioPlayer. <br />
  * It's also a good example of how to draw the waveform of the audio.
  * <p>
  * For more information about Minim and additional features, 
  * visit http://code.compartmental.net/minim/
  */

import ddf.minim.*;

Minim minim;
AudioPlayer player;

void setup()
{
  size(512, 200, P3D);
  
  // we pass this to Minim so that it can load files from the data directory
  minim = new Minim(this);
  minim.debugOn();
  
  // loadFile will look in all the same places as loadImage does.
  // this means you can find files that are in the data folder and the 
  // sketch folder. you can also pass an absolute path, or a URL.
  //
  // TODO 
  // test24.mpg, test25.mpg, test26.mpg, test27.mpg, 
  // test28.mpg, test29.mpg, test31.mpg, test32.mpg, test34.mpg
  // throw Unsupported Audio File: not a MPEG stream: Unable to read mp3 header
  // this is caused by line 340 in MpegAudioFileReader where
  // we try to read the first audio header of the file to get format info
  // but based on how it is interpreting the header at the very beginning of the file
  // it finds that the data following the "first frame" does not conform to a frame header
  // so it chews through the rest of the file one byte at a time looking for a legit frame header
  // (this happens in the do/while loop of the syncHeader function of Bitstream)
  // it seems strange that the very beginning of the file matches, but no four bytes further on do.
  //
  // TODO test30.mpg
  // throws array index out of bounds from jl.decoder.LayerIDecoder.SubbandLayer1Stereo.read_allocation
  //
  player = minim.loadFile("test24.mpg");
  
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