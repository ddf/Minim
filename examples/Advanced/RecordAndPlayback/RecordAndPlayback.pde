/**
  * This sketch demonstrates how to an <code>AudioRecorder</code> to record audio to disk and then immediately 
  * play it back by creating a new <code>AudioPlayer</code> for the <code>AudioRecording</code> returned by <code>save</code>. 
  * To use this sketch you need to have something plugged into the line-in on your computer. Press 'r' to toggle 
  * recording on and off and the press 's' to save to disk. The recorded file will be placed in the data folder of 
  * the sketch.
  */

import ddf.minim.*;

Minim minim;
AudioInput in;
AudioRecorder recorder;
AudioPlayer player;

void setup()
{
  size(512, 200, P3D);
  textMode(SCREEN);  
  minim = new Minim(this);
  
  // get a stereo line-in: sample buffer length of 2048
  // default sample rate is 44100, default bit depth is 16
  in = minim.getLineIn(Minim.STEREO, 2048);
  // create an AudioRecorder that will record from in to the filename specified, using buffered recording
  // buffered recording means that all captured audio will be written into a sample buffer
  // then, when save() is called, the contents of the buffer will actually be written to a file
  // the file will be located in the sketch's data folder.
  recorder = minim.createRecorder(in, "myrecording.wav", true);
  
  textFont(createFont("Arial", 12));
}

void draw()
{
  background(0); 
  stroke(255);
  // draw the waveforms
  // the values returned by left.get() and right.get() will be between -1 and 1,
  // so we need to scale them up to see the waveform
  for(int i = 0; i < in.left.size()-1; i++)
  {
    line(i, 50 + in.left.get(i)*50, i+1, 50 + in.left.get(i+1)*50);
    line(i, 150 + in.right.get(i)*50, i+1, 150 + in.right.get(i+1)*50);
  }
  
  if ( recorder.isRecording() )
  {
    text("Now recording...", 5, 15);
  }
  else
  {
    text("Not recording.", 5, 15);
  }
}

void keyReleased()
{
  if ( key == 'r' ) 
  {
    // to indicate that you want to start or stop capturing audio data, you must call
    // startRecording() and stopRecording() on the AudioFileOut object. You can start and stop
    // as many times as you like, the audio data will be appended to the end of the buffer 
    // (in the case of buffered recording) or to the end of the file (in the case of streamed recording). 
    if ( recorder.isRecording() ) 
    {
      recorder.endRecord();
    }
    else 
    {
      recorder.beginRecord();
    }
  }
  if ( key == 's' )
  {
    // we've filled the file out buffer, 
    // now write it to a file of the type we specified in setup
    // in the case of buffered recording, 
    // this will appear to freeze the sketch for sometime, if the buffer is large
    // in the case of streamed recording, 
    // it will not freeze as the data is already in the file and all that is being done
    // is closing the file.
    // save returns the recorded audio in an AudioRecording, 
    // which we can then play with an AudioPlayer
    if ( player != null )
    {
        player.close();
    }
    player = recorder.save();
    player.play();
  }
}

void stop()
{
  // always close Minim audio classes when you are done with them
  in.close();
  if ( player != null )
  {
    player.close();
  }
  minim.stop();
  
  super.stop();
}
