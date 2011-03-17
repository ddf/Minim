import ddf.minim.*;
import ddf.minim.ugens.*;


Minim minim;
AudioOutput out;

ModInstrument waves[];
int currentWave;

String  waveNames[] = { "SINE", "SAW", "SQUARE", "TRIANGLE" };

PFont font;

void setup()
{
  size(480, 320);
  
  minim = new Minim(this);
  out = minim.getLineOut();
  
  waves = new ModInstrument[4];
  
  waves[0] = new ModInstrument( Waves.SINE );
  // generating a phazer to ramp the amplitude down.
  waves[1] = new ModInstrument( WavetableGenerator.gen7( 2, new float[] { 1, 0 }, new int[] { 2 } ) );
  waves[2] = new ModInstrument( WavetableGenerator.gen7( 4096, new float[] { 1, 1, 0, 0 }, new int[] { 2036, 24, 2036 } ) );
  waves[3] = new ModInstrument( Waves.TRIANGLE );

  currentWave = 0;
  
  waves[0].noteOn(0.f);
  
  font = loadFont("Serif-48.vlw");
  textFont(font);
  textAlign(CENTER);
}

void draw()
{
  background(0);
   
  float amp = map(mouseY, 0, height, 1.f, 0.01f);
  float mod = map(mouseX, 0, width, 0.1f, 10.f);
 
  for(int i = 0; i < waves.length; i++)
  {
    waves[i].amp.setValue( amp );
    waves[i].mod.setFrequency( Frequency.ofHertz(mod) );
  }   
  
  fill(255);
  
  text( waveNames[currentWave], width/2, height/2 );
}

void keyPressed()
{
  if ( key == ' ' )
  {
    waves[currentWave].noteOff();
    currentWave = (currentWave+1) % 4;
    waves[currentWave].noteOn(0.f);
  }
}


