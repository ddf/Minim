/**
  * This sketch demonstrates how to use the BeatDetect object in FREQ_ENERGY mode for offline (non-realtime) beat detection.<br />
  * To "tick" the analysis you must call <code>detect</code> with successive buffers of audio. 
  * You can do this inside of <code>draw</code> by calling <code>read</code> on the AudioRecordingStream for the file.
  * <p>
  * For more information about Minim and additional features, 
  * visit http://code.compartmental.net/minim/
  */

import ddf.minim.*;
import ddf.minim.spi.*;
import ddf.minim.analysis.*;

Minim minim;
MultiChannelBuffer buffer;
AudioRecordingStream song;
BeatDetect beat;

void setup()
{
  size(512, 200, P3D);
  
  minim = new Minim(this);
  
  // buffer to read into from the file stream
  buffer = new MultiChannelBuffer(1024, 2);
  // load a file stream we can read the file from a buffer at a time
  song = minim.loadFileStream("marcus_kellis_theme.mp3");
  // we have to tell the AudioRecordingStream to play so read will return data.
  song.play();
  // a beat detection object that is FREQ_ENERGY mode that 
  // expects buffers the length of song's buffer size
  // and samples captured at songs's sample rate
  beat = new BeatDetect(buffer.getBufferSize(), song.getFormat().getSampleRate());
  // set the sensitivity to 300 milliseconds
  // After a beat has been detected, the algorithm will wait for 300 milliseconds 
  // before allowing another beat to be reported. You can use this to dampen the 
  // algorithm if it is giving too many false-positives. The default value is 10, 
  // which is essentially no damping. If you try to set the sensitivity to a negative value, 
  // an error will be reported and it will be set to 10 instead. 
  // note that what sensitivity you choose will depend a lot on what kind of audio 
  // you are analyzing. in this example, we use the same BeatDetect object for 
  // detecting kick, snare, and hat, but that this sensitivity is not especially great
  // for detecting snare reliably (though it's also possible that the range of frequencies
  // used by the isSnare method are not appropriate for the song).
  beat.setSensitivity(300);
}

void draw()
{
  background(0);
  
  // process a buffer
  song.read(buffer);
  // do beat detection on the left channel
  beat.detect(buffer.getChannel(0));
  
  // draw a green rectangle for every detect band
  // that had an onset this frame
  float rectW = width / beat.detectSize();
  for(int i = 0; i < beat.detectSize(); ++i)
  {
    // test one frequency band for an onset
    if ( beat.isOnset(i) )
    {
      fill(0,200,0);
      rect( i*rectW, 0, rectW, height);
    }
  }
  
  // draw an orange rectangle over the bands in 
  // the range we are querying
  int lowBand = 5;
  int highBand = 15;
  // at least this many bands must have an onset 
  // for isRange to return true
  int numberOfOnsetsThreshold = 4;
  if ( beat.isRange(lowBand, highBand, numberOfOnsetsThreshold) )
  {
    fill(232,179,2,200);
    rect(rectW*lowBand, 0, (highBand-lowBand)*rectW, height);
  }
}
