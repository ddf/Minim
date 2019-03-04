package ddf.minim.tests;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

/**
 * Test case for the position() of a playable never reaching the value of length()
 * 
 * @see https://github.com/ddf/Minim/issues/70
 * @author Damien Quartz 
 *
 */
public class PlayablePosition extends TestBase
{
	AudioPlayer player;

	@Override
	protected void setup(String[] args)
	{
		player = minim.loadFile( args[1] );
		Minim.debug( args[1] + " is " + player.length() / 1000.0f + " seconds long." );
		player.play();
	}
	
	protected boolean update()
	{
		if ( player.isPlaying() )
		{
			Minim.debug( player.position() + " / " + player.length() );
		}
		
		return player.isPlaying();
	}

	public static void main(String[] args)
	{
		Start(new PlayablePosition(), args);
	}

}
