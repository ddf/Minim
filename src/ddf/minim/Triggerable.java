package ddf.minim;

public interface Triggerable
{
  /**
   * Triggers the sound to play once. Can be called again before 
   * the sound finishes playing.
   *
   */
  void trigger();
}
