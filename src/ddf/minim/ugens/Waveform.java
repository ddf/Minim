package ddf.minim.ugens;

/**
 * An interface to represent a Waveform that can be sampled by using a value 
 * between 0 and 1. 
 * 
 * @author Damien Di Fede
 *
 */

public interface Waveform 
{
	float value(float at);
}
