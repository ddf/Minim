package ddf.minim;

class StereoBuffer implements AudioListener
{
  public MAudioBuffer left;
  public MAudioBuffer right;
  public MAudioBuffer mix;
  
  private Controller parent;
  
  StereoBuffer(int type, int bufferSize, Controller c)
  {
    left = new MAudioBuffer(bufferSize);
    if ( type == Minim.MONO )
    {
      right = left;
      mix = left;
    }
    else
    {
      right = new MAudioBuffer(bufferSize);
      mix = new MAudioBuffer(bufferSize);
    }
    parent = c;
  }
  
  public void samples(float[] samp)
  {
    // Minim.debug("Got samples!");
    left.set(samp);
    parent.update();
  }

  public void samples(float[] sampL, float[] sampR)
  {
    left.set(sampL);
    right.set(sampR);
    mix.mix(sampL, sampR);
    parent.update();
  }
}
