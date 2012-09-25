/**
  This is an example of how to use a FilePlayer UGen to play an audio file. It support all of the same formats that 
  AudioPlayer does, but allows you to insert the audio from the file into a UGen chain. FilePlayer provides all the 
  same methods that AudioPlayer does for controlling the file playback: play(), loop(), cue(int position), etc.
  <p>
  Press any key to pause and unpause playback!
  <p>
  For more information about Minim and additional features, visit http://code.compartmental.net/minim/
  <p>  
  author: Damien Di Fede
*/

import ddf.minim.*;
import ddf.minim.spi.*; // for AudioRecordingStream
import ddf.minim.ugens.*;

// declare everything we need to play our file
Minim minim;
FilePlayer filePlayer;
AudioOutput out;

// you can use your own file by putting it in the data directory of this sketch
// and changing the value assigned to fileName here.
String fileName = "http://code.compartmental.net/minim/examples/audio/groove.mp3";

void setup()
{
  // setup the size of the app
  size(640, 240);
  
  // create our Minim object for loading audio
  minim = new Minim(this);

  // get an AudioRecordingStream from Minim, which is what FilePlayer will control
  AudioRecordingStream myFile = minim.loadFileStream( fileName, // the file to load
                                                      1024,     // the size of the buffer. 1024 is a typical buffer size
                                                      true      // whether to load it totally into memory or not
                                                                // we say true because the file is short 
                                                    );
                               
  // this opens the file and puts it in the "play" state.                           
  filePlayer = new FilePlayer( myFile );
  // and then we'll tell the recording to loop indefinitely
  filePlayer.loop();
  
  // get a line out from Minim. It's important that the file is the same audio format 
  // as our output (i.e. same sample rate, number of channels, etc).
  out = minim.getLineOut();
  
  // patch the file player to the output
  filePlayer.patch(out);
                        
}

// keyPressed is called whenever a key on the keyboard is pressed
void keyPressed()
{
  // you can query whether the file is playing or not
  // playing simply means that it is generating sound
  // this will be true if you tell it to play() or loop()
  if ( filePlayer.isPlaying() )
  {
    // pauses playback of the file
    filePlayer.pause();
  }
  else
  {
    // starts the file looping again. this will reset the position
    // to whatever the current loop start point is. by default the 
    // loop start point is the beginning of the file and end point 
    // is the end of the file.
    filePlayer.loop();
  }
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
