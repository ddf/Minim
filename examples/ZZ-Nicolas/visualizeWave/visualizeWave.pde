import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;

Minim minim;

void setup()
{
  size(600, 220);

  minim = new Minim(this);
 
 
 //nb : uncomment any "Wavetable test = ..." line to visualize the waveform
 
 
 Wavetable test = Waves.add(new float[] {0.2,0.8},Waves.randomNoise(),Waves.SINE);
 //Wavetable test = Waves.add(new float[] {0.5,0.5}, Waves.saw(0.8),Waves.triangle(0.2));
 //Wavetable test = Waves.add(new float[] {0.5,0.5}, Waves.RandomPulses(80),Waves.SINE);
 //Wavetable test = Waves.triangle(0.2);
 //Wavetable test = Waves.randomNHarms(3);
 //Wavetable test = Waves.RandomPulses(50);
 //Wavetable test =  Disto.Square;
 //Wavetable test = Disto.Diode;
 //Wavetable test = Disto.TruncSine();


float[]  a= test.getWaveform();

 background(0);

 
 
  stroke(255);
  // draw the waveform
  for(int i = 0; i < a.length-1; i++)
  {
    float x1 = map(i, 0, a.length, 0, width);
    float x2 = map(i+1, 0, a.length, 0, width);
   
    line(x1, height/2 + a[i]*100, x2, height/2 + a[i+1]*100);
  
  }

  stroke(255, 0, 0);
  line(0, height/2, width,height/2 );

}

void draw()
{


}
