package ddf.minim.ugens;

/**
 * The Instrument interface is expected by AudioOutput.playNote. You can create your 
 * own instruments by implementing this interface in one of your classes. Typically, 
 * you will create a class that constructs a UGen chain: an Oscil patched to a filter 
 * patched to an ADSR. When noteOn is called you will patch the end of your chain to
 * the AudioOutput you are using and when noteOff is called you will unpatch. 
 *  
 * @author Damien Di Fede
 *
 */
public interface Instrument 
{
  /**
   * Start playing a note. Typically you will patch your UGen chain to your AudioOutput 
   * here.
   * 
   * @param duration how long the note should last (i.e. noteOff will be called after this many seconds)
   */
	void noteOn( float duration );
	
	/**
	 * Stop playing a note. Typically you will unpatch your UGen chain from your AudioOutput here.
	 */
	void noteOff();
}
