package ddf.minim;

public interface BufferedAudio
{
  static final int LEFT = 1;
  static final int RIGHT = 2;
  /**
   * Gets the samples for the requested channel number as a float array.
   * 
   * @param channelNumber the channel you want the samples for
   * @return the samples in a float array
   */
  float[] getChannel(int channelNumber);
  
  /**
   * Gets the length in milliseconds of the buffered audio.
   * 
   * @return the length in millisecons
   */
  int length();
}
