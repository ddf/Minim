import ddf.minim.*;
import ddf.minim.ugens.*;

Minim       minim;
AudioOutput out;

Constant    kickBegin;
Sampler     kick;


void setup()
{
  size(395, 200, OPENGL);
  minim = new Minim(this);
  out   = minim.getLineOut();

  kick  = new Sampler( "BD.wav", 4, minim );
  
  kickBegin = new Constant(1000);
  kickBegin.patch( kick.begin );
  kick.trigger();
  
  kick.patch( out );
}

void draw()
{
}

void keyPressed()
{
  kickBegin.setConstant( 500 );
  kick.trigger();
}
