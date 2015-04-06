package ddf.minim;

import java.io.FileNotFoundException;
import java.io.InputStream;

public abstract interface IFileSystemHandler
{
  public abstract String sketchPath(String paramString);
  
  public abstract InputStream createInput(String paramString)
    throws FileNotFoundException;
}
