package ddf.minim.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

import ddf.minim.Minim;

public abstract class TestBase
{	
	protected String fileFolder;
	protected Minim  minim;
	
	protected static void Start(TestBase test, String[] args)
	{
		test.fileFolder = args[0];
		test.minim = new Minim(test);
		test.minim.debugOn();
		
		test.setup(args);
		
		while(test.update())
		{
			// run at 30fps more or less
			try
			{
				Thread.sleep( 33 );
			}
			catch ( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
		
		test.minim.stop();
	}
	
	protected abstract void setup(String[] args);
	
	protected boolean update() { return false; }
	
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
