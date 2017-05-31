/**
  * This sketch demonstrates a way to do offline rendering of audio. We use a UGen chain to generate our audio, but this is not strictly necessary.
  * <p>
  * For more information about Minim and additional features, visit http://code.compartmental.net/minim/
  */
  
import ddf.minim.*;
import ddf.minim.ugens.*;
import javax.sound.sampled.AudioFormat; // this will not be necessary in a future release

// contants to describe the type of file we want to write
final float SAMPLE_RATE = 44100; // ie 44.1k audio "cd quality"
final int   SAMPLE_SIZE = 16; // ie 16 bit audio
final int   CHANNELS    = 1; // many channels of audio (currently only support 1 or 2)
final boolean SIGNED    = true;
final boolean BIGENDIAN = false;

// how many samples we will generate every frame of the sketch (this will impact how quickly the file is written)
final int BUFFER_SIZE = 1024;
// how many seconds of audio to render
final float RENDER_SECONDS = 5.0f;

Minim minim;
// we will use a SignalSplitter as our Recordable source so that we can create an AudioRecorder for writing to disk.
SignalSplitter out;
// the AudioRecorder that will write to disk
AudioRecorder recorder;
// the Summer we will use to tick our generate audio in each update call
Summer summer;
// the buffer we will have Summer write into
MultiChannelBuffer buffer;
// we will calculate how many buffers of audio we need to generate in setup based on RENDER_SECONDS, SAMPLE_RATE, and BUFFER_SIZE
int renderCount;

void setup()
{
  // the width here will implicitly be how many buffers we render
  size(512, 200);
  background(0);
  
  minim = new Minim(this);
  minim.debugOn();
  
  out = new SignalSplitter( new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, SIGNED, BIGENDIAN), BUFFER_SIZE );
  
  // creates a recorder that will write out to a file in the sketch folder
  recorder = minim.createRecorder( out, "render.wav" );
  
  // create the buffer we will render into
  buffer = new MultiChannelBuffer(BUFFER_SIZE, CHANNELS);
  
  // create the summer that will render the audio
  summer = new Summer();
  // make sure it matches our file's sample rate and channel numbers
  summer.setSampleRate(SAMPLE_RATE);
  summer.setChannelCount(CHANNELS);
  
  // now add some stuff that will make sound
  Oscil osc = new Oscil(440, 0.25, Waves.SAW);
  Line freqSweep = new Line(RENDER_SECONDS, 220, 880);
  freqSweep.patch(osc.frequency);
  Oscil mod = new Oscil(0.5f, 0.25f, Waves.SINE);
  mod.patch(osc.amplitude);
  osc.patch( summer );
  
  // start the Line, it will do its thing while we generate in update
  freqSweep.activate();
  
  // start the recorder so we don't miss anything
  recorder.beginRecord();
  
  // how many buffers? well...
  // SAMPLE_RATE defines how many samples per second are played,
  // so SAMPLE_RATE * RENDER_SECONDS is the total number of samples we need to generate.
  // we want to split this into BUFFER_SIZE chunks and figure out how many chunks to render.
  // we add an extra chunk to account for any remainder after dividing. 
  // this means the final file won't necessarily be *exactly* RENDER_SECONDS.
  renderCount = int(SAMPLE_RATE * RENDER_SECONDS) / BUFFER_SIZE + 1;
  println("Will render " + renderCount + " buffers of audio");
}

void draw()
{
  // since we render a buffer every frame we can use frameCount to figure out when to stop rendering
  if ( frameCount < renderCount )
  {   
    switch( CHANNELS )
    {
      case 1:
      {
        // render a buffer
        summer.generate(buffer.getChannel(0));
        // push the buffer to the recorder via the SignalSplitter
        out.samples(buffer.getChannel(0)); //<>//
      }
      break;
      
      case 2:
      {
        // same as above, but for stereo audio
        summer.generate(buffer.getChannel(0), buffer.getChannel(1));
        out.samples(buffer.getChannel(0), buffer.getChannel(1));
      }
      break;
    }
    
    // draw an RMS amplitude representation of the audio that was just generated.
    // we just use the left channel because our audio is effectively mono even when rendering in stereo
    float peak = buffer.getLevel(0) * height;
    float x = map(frameCount, 0, renderCount, 0, width);
    stroke(255);
    line(x, height/2 - peak*0.5f, x, height/2 + peak*0.5f);
    
    // are we done?
    if ( frameCount+1 == renderCount )
    {
      recorder.endRecord();
      // open the sketch folder to see the file and try playing it in a media player!
      recorder.save();
    }
  }
}