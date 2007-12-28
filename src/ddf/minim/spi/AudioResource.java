package ddf.minim.spi;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;

public interface AudioResource
{
  /**
   * Opens the resource to be used.
   * 
   */
  void open();

  /**
   * Closes the resource, releasing any memory.
   * 
   */
  void close();

  /**
   * Returns the Controls available for this AudioResource
   * 
   * @return an array of Control objects, that can be used to manipulate the
   *         resource
   */
  Control[] getControls();
  
  AudioFormat getFormat();
}
