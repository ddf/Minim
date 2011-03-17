import ddf.minim.*;
import ddf.minim.spi.*;
import ddf.minim.signals.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;

Minim minim;
AudioRecordingStream myFile;
String fileName = "http://www.future-boy.net/audio/outofit/02_-_brad_sucks_-_certain_death_(future_boy_pulse_mix).mp3";
MultiChannelBuffer samples;

void setup()
{
  size(640, 480);
  
  minim = new Minim(this);

  myFile = minim.loadFileStream( fileName, // the file to load
                                 width,    // the size of the buffer
                                 false     // whether to load it totally into memory or not
                               );
  // final intention is to have it "playing" already.                               
  myFile.play();
                           
  samples = new MultiChannelBuffer( 
                                    width  // how big is the buffer
                                  , myFile.getFormat().getChannels() // how many channels
                                  );
}

void draw()
{
  background(0);
  
  stroke(255);
  noFill();

  for(int c = 0; c < samples.getChannelCount(); c++)
  {
    beginShape();
    for(int i = 0; i < samples.getBufferSize(); i++)
    {
      float x = i;
      float y = (c+1)*height/(samples.getChannelCount()+1) - (samples.getChannel(c)[i]*50);
      vertex(x,y);
    }
    endShape();
  }
}

void keyPressed()
{
  myFile.read(samples);
}



