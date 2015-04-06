package ddf.minim;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class DefaultFileSystemHandler
  implements IFileSystemHandler
{
  public boolean auto = true;
  public String customSketchPath = "";
  
  public String sketchPath(String fileName)
  {
    if (new File(fileName).isAbsolute()) {
      return fileName;
    }
    if (this.auto) {
      return System.getProperty("user.dir") + File.separator + fileName;
    }
    return this.customSketchPath + File.separator + fileName;
  }
  
  public InputStream createInput(String filename)
    throws FileNotFoundException
  {
    InputStream input = new FileInputStream(sketchPath(filename));
    if ((input != null) && (filename.toLowerCase().endsWith(".gz"))) {
      try
      {
        return new GZIPInputStream(input);
      }
      catch (IOException e)
      {
        e.printStackTrace();
        return null;
      }
    }
    return input;
  }
}
