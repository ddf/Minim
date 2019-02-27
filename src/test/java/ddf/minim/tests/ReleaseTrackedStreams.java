package ddf.minim.tests;

import ddf.minim.*;
import ddf.minim.spi.AudioStream;
import ddf.minim.ugens.FilePlayer;

public class ReleaseTrackedStreams extends TestBase
{
	String fileName;
	double lastMem;

	@Override
	protected void setup(String[] args)
	{		
		fileName = args[1];
		minim.debugOff();
	}
	
	protected boolean update()
	{
		FilePlayer player = new FilePlayer(minim.loadFileStream( fileName ));
		player.close();
		
		AudioStream input = minim.getInputStream( Minim.STEREO, 1024, 48000, 16 );
		if (input != null)
		{
			input.close();
		}
		
		double mem = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;
		if (mem < lastMem)
		{			
			System.out.println("Mem dropped to KB: " + mem);
		}
		
		lastMem = mem;
		
		return true;
	}

	public static void main(String[] args)
	{
		Start(new ReleaseTrackedStreams(), args);
	}
}
