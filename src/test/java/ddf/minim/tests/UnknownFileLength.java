package ddf.minim.tests;

import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.Playable;
import ddf.minim.ugens.FilePlayer;

/**
 * This test was created to ensure that audio files whose length is unknown are treated correctly by all API calls that use the length
 * 
 * @see https://github.com/ddf/Minim/issues/93
 * @author Damien Quartz
 *
 */
public class UnknownFileLength extends TestBase
{

	public static void main(String[] args)
	{
		Start(new UnknownFileLength(), args);
	}

	protected void setup(String[] args)
	{
		// this is the test file noted in https://github.com/ddf/Minim/issues/73, which loads with an unknown length,
		// allowing us to ensure that several methods behave correctly under this condition.
		String fileName = "http://www.noiseaddicts.com/samples_1w72b820/2553.mp3";

		testCueSkip( minim.loadFile(fileName) );
		
		testCueSkip( new FilePlayer(minim.loadFileStream( fileName )) );
		
		float fileSampleRate = minim.loadFileIntoBuffer( fileName, new MultiChannelBuffer(2, 1) );
		if (fileSampleRate > 0)
		{
			Minim.debug( fileName + " loaded into buffer has sample rate " + fileSampleRate );
		}
		else
		{
			Minim.debug( fileName + " could not load into buffer." );
		}
	}
	
	void testCueSkip(Playable player)
	{
		if (player != null)
		{
			// should report -1, if it doesn't we need to use a different file
			Minim.debug( player.getMetaData().fileName() + " length: " + player.length() );
			
			// should be ignored and report nothing
			player.cue( -1000 );
			// debug should report skipping forward by 1000 milliseconds
			player.cue( 1000 );	
			
			// debug should report skipping forward by 1000 milliseconds to position 2000
			player.skip( 1000 );
			// debug should report skipping by -3000 milliseconds to position 0
			player.skip( -3000 );
		}
	}
}
