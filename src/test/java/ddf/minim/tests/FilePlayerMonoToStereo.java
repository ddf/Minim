package ddf.minim.tests;

import ddf.minim.ugens.FilePlayer;

public class FilePlayerMonoToStereo extends TestBase
{
	FilePlayer file;

	public static void main(String[] args)
	{
		Start(new FilePlayerMonoToStereo(), args);
	}

	@Override
	protected void setup(String[] args)
	{
		file = new FilePlayer(minim.loadFileStream( args[1] ));
		if ( file != null )
		{
			file.patch( minim.getLineOut() );
			file.play();
		}
	}
	
	protected boolean update()
	{
		return file.isPlaying();
	}
}
