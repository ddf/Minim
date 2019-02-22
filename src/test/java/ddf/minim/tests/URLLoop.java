package ddf.minim.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

import ddf.minim.*;

/**
 * This test was created to ensure that audio files loaded from the internet loop correctly.
 * 
 * @see https://github.com/ddf/Minim/issues/73
 * @author Damien Quartz
 *
 */
public class URLLoop
{
	String fileFolder;

	public static void main(String[] args)
	{
		URLLoop test = new URLLoop();
		
		test.Start( args );
	}

	void Start(String[] args)
	{
		fileFolder = args[0];
		
		Minim minim = new Minim(this);
		minim.debugOn();
		// this is the test file noted in https://github.com/ddf/Minim/issues/73
		AudioPlayer player = minim.loadFile("http://www.noiseaddicts.com/samples_1w72b820/2553.mp3");
		player.loop();
		
		while(player.isPlaying())
		{			
		}
		
		minim.stop();
	}
	
	public String sketchPath( String fileName )
	{
		return Paths.get( fileFolder, fileName ).toString();
	}
	
	public InputStream createInput( String fileName )
	{
		InputStream stream = null;
		if (fileName.startsWith( "http"))
		{
			try
			{
				stream = new URL(fileName).openStream();
			}
			catch(Exception ex)
			{
				System.err.println( "Unable to load file at " + fileName );
			}
		}
		else
		{
			try
			{
				stream = new FileInputStream(sketchPath(fileName));
			}
			catch( FileNotFoundException ex )
			{
				System.err.println( "Unable to find file " + fileName );
			}
		}
		
		return stream;
	}
}
