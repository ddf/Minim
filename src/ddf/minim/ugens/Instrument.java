package ddf.minim.ugens;

public interface Instrument 
{
	/**
	 * Start playing a note.
	 */
	void noteOn( float duration );
	
	/**
	 * Stop playing a note.
	 */
	void noteOff();
}
